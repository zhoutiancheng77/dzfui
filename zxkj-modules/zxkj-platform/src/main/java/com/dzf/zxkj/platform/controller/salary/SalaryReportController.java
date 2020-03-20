package com.dzf.zxkj.platform.controller.salary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.base.utils.ValueUtils;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportColumn;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryTotalVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportExcel;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl.SalaryReportExcelFactory;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.ExcelReport;
import com.dzf.zxkj.platform.util.JsonErrorUtil;
import com.dzf.zxkj.platform.util.NationalAreaUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 工资表
 */
@RestController
@RequestMapping("/salary/gl_gzbact2")
@Slf4j
public class SalaryReportController  extends BaseController {

    @Autowired
    private ISalaryReportService gl_gzbserv = null;
    @Autowired
    private SingleObjectBO singlebo = null;
    @Autowired
    private SalaryReportExcelFactory factory;
    @Autowired
    protected IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    private IUserService userServiceImpl;
    @Autowired
    private ISalaryBaseService gl_gzbbaseserv;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("/query")
    public ReturnData<Json> query(@RequestParam("page") int page, @RequestParam("rows") int rows, @RequestParam("opdate") String qj,
                                  @RequestParam("billtype") String billtype, @RequestParam("pk_corp") String pk_corp, @RequestParam("isfenye") String isfenye) {
        Json json = new Json();
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        if(SalaryTypeEnum.TOTAL.getValue().equals(billtype)){
            SalaryTotalVO[] vos = gl_gzbserv.queryTotal(pk_corp, qj);// 查询工资表概况数据
            json.setRows(vos);
        }else{
            if ("Y".equals(isfenye)) {// 分页
                QueryPageVO pagevo = gl_gzbserv.queryBodysBypage(pk_corp, qj, billtype, page, rows);
                json.setTotal(Long.valueOf(pagevo.getTotal()));
                json.setRows(pagevo.getPagevos());
            } else {
                SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, qj, billtype);// 查询工资表数据
                if (vos == null || vos.length == 0) {
                    vos = new SalaryReportVO[0];
                }
                json.setRows(vos);
            }
        }

        DZFBoolean bool = gl_gzbserv.queryIsGZ(pk_corp, qj);// 查询是否关账
        String msg = "状态代码";
        if (bool.booleanValue()) {
            msg +="500";
        } else {
            msg +="-600";
        }
        json.setMsg(msg);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    private SalaryReportVO calTotal(SalaryReportVO[] vos) {
        // 计算合计行数据
        String[] columns = {"yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj", "ynssde", "grsds", "sfgz", "znjyzc",
                "jxjyzc", "zfdkzc", "zfzjzc", "sylrzc", "ljsre", "ljznjyzc", "ljjxjyzc", "ljzfdkzc", "ljzfzjzc",
                "ljsylrzc", "ljynse", "yyjse", "ljzxkc", "qyyanglaobx", "qyyiliaobx", "qyshiyebx", "qyzfgjj", "qygsbx",
                "qyshybx"};

        SalaryReportVO nvo = new SalaryReportVO();
        nvo.setYgbm("合计");

        for (String column : columns) {
            DZFDouble d1 = DZFDouble.ZERO_DBL;
            for (SalaryReportVO svo : vos) {
                if (svo.getYfgz() == null)
                    svo.setYfgz(DZFDouble.ZERO_DBL);
                d1 = SafeCompute.add(d1,
                        DZFNumberUtil.toNotNullValue(ValueUtils.getDZFDouble(svo.getAttributeValue(column))).setScale(2,
                                DZFDouble.ROUND_HALF_UP));
                nvo.setAttributeValue(column, d1);
            }
        }
        return nvo;
    }

    @GetMapping("/judgeHasPZ")
    public ReturnData<Json> judgeHasPZ(@RequestParam("opdate") String qj) {
        Json json = new Json();
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String msg = gl_gzbserv.judgeHasPZ(SystemUtil.getLoginCorpId(), qj);
        json.setRows(null);
        json.setMsg(msg);
        json.setSuccess(true);
        log.info("查询成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/isGZ")
    public ReturnData<Json> isGZ(@RequestParam("opdate") String qj, @RequestParam("isgz") String isgz) {
        Json json = new Json();
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        DZFBoolean bool = gl_gzbserv.isGZ(SystemUtil.getLoginCorpId(), qj, isgz);

        String msg = "状态代码";
        if (bool == null) {
            msg +="500";
        } else {
            if (bool.booleanValue()) {
                msg +="600";
            } else {
                msg +="700";
            }
        }
        json.setRows(null);
        json.setMsg(msg);
        json.setSuccess(true);
        if (!StringUtil.isEmpty(qj)) {
            DZFDate from = new DZFDate(qj + "-01");
            String info = null;
            if ("true".equals(isgz)) {
                info = "关账：";
            }
            if ("false".equals(isgz)) {
                info = "取消关账：";
            }
            if (!StringUtil.isEmpty(info)) {
                writeLogRecord(LogRecordEnum.OPE_KJ_SALARY,info + from.getYear() + "年" + from.getMonth() + "月", ISysConstants.SYS_2);
            }
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody Map<String, String> map) {
        Json json = new Json();
        String strArr = map.get("strArr");
        if (DZFValueCheck.isEmpty(strArr)) {
            throw new BusinessException("数据为空");
        }
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");

        String billtype = map.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");

        String pk_corp = map.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        List<SalaryReportVO> list = new ArrayList<>();
        SalaryReportVO vo = JsonUtils.deserialize(strArr, SalaryReportVO.class);
        list.add(vo);
        SalaryReportVO[] vos = gl_gzbserv.save(pk_corp, list, qj, SystemUtil.getLoginUserId(), billtype);
        json.setMsg("工资表保存成功");
        json.setRows(vos);
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资表保存", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData<Json> delete(@RequestBody Map<String, String> map) {
        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String pk_corp = map.get("ops");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String primaryKey = map.get("pks");
        if (StringUtil.isEmpty(primaryKey)) {
            throw new BusinessException("删除数据为空");
        }
        checkOwnCorp(pk_corp);
        SalaryReportVO[] vo = gl_gzbserv.delete(pk_corp, primaryKey, qj);
        json.setRows(vo);
        json.setMsg("删除成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资表删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/check")
    public ReturnData<Json> check(@RequestParam("opdate") String qj,
                                  @RequestParam("operate") String str, @RequestParam("pk_corp") String pk_corp) {
        Json json = new Json();
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        if (!pk_corp.equals(SystemUtil.getLoginCorpId())) {
            CorpVO corp = corpService.queryByPk(pk_corp);
            throw new BusinessException("请切换到" + corp.getUnitname() + "公司,再进行操作！");
        }
        gl_gzbserv.checkPz(pk_corp, qj, str, true);
        json.setSuccess(true);
        return ReturnData.ok().data(json);

    }

    @GetMapping("/gzjt")
    public ReturnData<Json> gzjt(@RequestParam Map<String, String> map) {
        Json json = new Json();
        String pk_corp = map.get("pk_corp");
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        CorpVO corp = corpService.queryByPk(pk_corp);
        TzpzHVO msg = gl_gzbserv.saveToVoucher(corp, qj, SystemUtil.getLoginUserId(),"gzjt");
        json.setData(msg);
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资计提", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryGlpz")
    public ReturnData<Json> queryGlpz(@RequestParam("sourcebilltype") String sourcebilltype,
                                      @RequestParam("period") String period, @RequestParam("pk_corp") String pk_corp) {
        Json grid = new Json();
        if (StringUtil.isEmpty(period))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        sourcebilltype = pk_corp + period + "gzjt," + pk_corp + period + "gzff";
        TzpzHVO[] hvos = gl_gzbserv.queryGlpz(sourcebilltype,pk_corp);
        if (hvos == null || hvos.length == 0) {
            grid.setData(hvos);
            grid.setTotal((long) 0);
        } else {
            grid.setData(hvos);
            grid.setTotal((long) hvos.length);
        }
        grid.setSuccess(true);
        grid.setMsg("联查成功！");
        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资联查凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/gzff")
    public ReturnData<Json> gzff(@RequestParam Map<String, String> map) {
        Json json = new Json();
        String qj = map.get("opdate");
        String pk_corp = map.get("pk_corp");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        CorpVO corp = corpService.queryByPk(pk_corp);
        TzpzHVO msg = gl_gzbserv.saveToVoucher(corp,  qj, SystemUtil.getLoginUserId(), "gzff");
        json.setData(msg);
        json.setSuccess(true);

        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资发放", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getCopyMonth")
    public ReturnData<Json> getCopyMonth(@RequestParam("copyTodate") String copyTodate, @RequestParam("pk_corp") String pk_corp) {
        Json json = new Json();

        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        SalaryReportVO[] vos = gl_gzbserv.queryAllType(pk_corp, copyTodate);// 查询工资表数据
        if (vos != null && vos.length > 0) {

        } else {
            String sql = "select max(qj) from  ynt_salaryreport t where t.qj <? and t.pk_corp =? and nvl(t.dr,0)=0 ";
            SQLParameter sp = new SQLParameter();
            sp.addParam(copyTodate);
            sp.addParam(pk_corp);
            Object o = singlebo.executeQuery(sql, sp, new ColumnProcessor());
            json.setData(o);
        }
        json.setMsg("获取复制期间成功");
        json.setSuccess(true);

        return ReturnData.ok().data(json);
    }

    @GetMapping("/copyByMonth")
    public ReturnData<Json> copyByMonth(@RequestParam("copyFromdate") String copyFromdate, @RequestParam("copyTodate") String copyTodate,
                                        @RequestParam("billtype") String billtype, @RequestParam("pk_corp") String pk_corp, @RequestParam("auto") String auto) {
        Json json = new Json();

        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        checkOwnCorp(pk_corp);
        SalaryReportVO[] vos = null;
        if (!StringUtil.isEmpty(auto)) {
            // 复制最近月份工资表到当前月份
            if ("Y".equalsIgnoreCase(auto)) {
                vos = gl_gzbserv.saveCopyByMonth(pk_corp, copyFromdate, copyTodate, SystemUtil.getLoginUserId(), null);
            }else{
                vos = gl_gzbserv.saveCopyByMonth(pk_corp, copyFromdate, copyTodate, SystemUtil.getLoginUserId(), null);
            }
        } else {
            vos = gl_gzbserv.saveCopyByMonth(pk_corp, copyFromdate, copyTodate, SystemUtil.getLoginUserId(), null);
        }
        json.setRows(vos);
        json.setMsg("复制成功");
        json.setSuccess(true);
        log.info("复制成功");

        if (!StringUtil.isEmpty(copyFromdate)) {// &&
            DZFDate from = new DZFDate(copyFromdate + "-01");
            DZFDate to = new DZFDate(copyTodate + "-01");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY,
                    "复制工资表：" + from.getYear() + "年" + from.getMonth() + "月-" + to.getYear() + "年" + to.getMonth() + "月",
                    ISysConstants.SYS_2);
        }
        return ReturnData.ok().data(json);
    }

    private int[] deleteArr(int index, int array[]) {
        // 数组的删除其实就是覆盖前一位
        int[] arrNew = new int[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }

    private String[] deleteArr(int index, String array[]) {
        // 数组的删除其实就是覆盖前一位
        String[] arrNew = new String[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }

    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(PrintParamVO printParamVO, @RequestParam Map<String, String> map, HttpServletResponse response) {
        String opdate = null;
        String beginPeriod = null;
        String endPeriod = null;
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);

            String billtype = map.get("billtype");
            if (StringUtil.isEmpty(billtype))
                throw new BusinessException("类型为空");
            String pk_corp = map.get("pk_corp");
            if (StringUtil.isEmpty(billtype))
                throw new BusinessException("公司为空");
            opdate = map.get("opdate");
            if (StringUtil.isEmpty(opdate))
                throw new BusinessException("期间为空");
//            setIscross(DZFBoolean.TRUE);// 是否横向
            // 查询工资表数据
//            SalaryReportVO[] bodyvos = gl_gzbserv.query(pk_corp, opdate, billtype);

            String periodRange = map.get("periodRange");
            if(StringUtil.isEmptyWithTrim(periodRange))
                return;
            String[] periods = periodRange.split(",");

            if(periods == null || periods.length !=2){
                throw new BusinessException("传入期间出错");
            }
            beginPeriod = periods[0];
            if (StringUtil.isEmpty(beginPeriod))
                throw new BusinessException("期间为空");
            endPeriod = periods[1];
            if (StringUtil.isEmpty(endPeriod))
                throw new BusinessException("期间为空");
            SalaryReportVO[] bodyvos = gl_gzbserv.query(pk_corp, beginPeriod,endPeriod, billtype);
            if (bodyvos == null || bodyvos.length == 0)
                return;
            for (SalaryReportVO vo : bodyvos) {
                vo.setZjlx(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getName());
            }
//            qijian = bodyvos[0].getQj();
            CorpVO cvo = corpService.queryByPk(pk_corp);
            bodyvos[0].setPk_corp(cvo.getUnitname());// 用pk_corp属性传递
            // 公司名称
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title参数
            tmap.put("公司", cvo.getUnitname());
            tmap.put("期间", bodyvos[0].getQj());
            String hiddenphone = pmap.get("hiddenphone");
            String zbr = pmap.get("zbr");
            if (!StringUtil.isEmpty(zbr) && new DZFBoolean(zbr).booleanValue()) {
                tmap.put("制表人", SystemUtil.getLoginUserVo().getUser_name());
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
            List<Integer> hiddenColList = getHiddenColumn(billtype);
            if (!StringUtil.isEmpty(hiddenphone) && new DZFBoolean(hiddenphone).booleanValue()) {
                // 隐藏手机号
                hiddenColList.add(1);
            }

            printReporUtil.setLineheight(22F);
            String[] columns = SalaryReportColumn.getCodes(hiddenColList);
            String[] columnNames = SalaryReportColumn.getNames(hiddenColList, billtype);
            int[] widths = SalaryReportColumn.getWidths(hiddenColList);
            for (int i = 0; i < columnNames.length; i++) {
                if ("费用科目".equals(columnNames[i])) {
                    columnNames = deleteArr(i, columnNames);
                    columns = deleteArr(i, columns);
                    widths = deleteArr(i, widths);
                    break;
                }
            }
            if (pmap.get("type").equals("4")) {
//                printReporUtil.setRotate(DZFBoolean.TRUE);
            }
            Map<String,List<SuperVO>> smap = getMapSaraly(bodyvos);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            printReporUtil.setIscross(DZFBoolean.TRUE);
            printReporUtil.printHz(smap,null,
                    "工 资 表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")", columns, columnNames,
                    widths, 60, pmap, tmap);

        } catch (DocumentException e) {
            log.error("工资表打印失败!", e);
        } catch (IOException e) {
            log.error("工资表打印失败!", e);
        } catch (Exception e) {
            log.error("工资表打印失败!", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("工资表打印错误", e);
            }
        }
        if (!StringUtil.isEmpty(opdate)) {
//            DZFDate from = new DZFDate(opdate + "-01");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY,
                    "工资表打印："+beginPeriod+"到" + endPeriod, ISysConstants.SYS_2);
        }
    }

    private Map<String,List<SuperVO>> getMapSaraly( SalaryReportVO[] bodyvos){

        Map<String,List<SuperVO>> map = new LinkedMap();
        VOUtil.ascSort(bodyvos, new String[] { "qj" });
        String key = "";
        List<SuperVO> list = null;
        for (SalaryReportVO vo : bodyvos) {
            key = vo.getQj();
            if(map.containsKey(key)){
                list = map.get(key);
            }else{
                list = new ArrayList<>();
            }
            list.add(vo);
            map.put(key,list);
        }
        map.forEach((ke1, value) -> {
            SalaryReportVO nvo = calTotal(value.toArray(new SalaryReportVO[value.size()]));
            value.add(nvo);
        });
        return map;
    }

    @PostMapping("/impExcel")
    public ReturnData<Json> impExcel(HttpServletRequest request) {
        Json json = new Json();
        String opdate = null;
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile infile = multipartRequest.getFile("impfile");
            if (infile == null) {
                throw new BusinessException("请选择导入文件!");
            }

            opdate = multipartRequest.getParameter("period");
            String billtype = multipartRequest.getParameter("billtype");
            if (StringUtil.isEmpty(billtype))
                throw new BusinessException("类型为空");

            String filename = infile.getOriginalFilename();
            int index = filename.lastIndexOf(".");
            String filetype = filename.substring(index + 1);

            String pk_corp = multipartRequest.getParameter("pk_corp");
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("公司为空");
            }
            checkOwnCorp(pk_corp);
            CorpVO corpvo = corpService.queryByPk(pk_corp);
            Object[] vos = onBoImp(infile, opdate, filetype, billtype, corpvo);
            json.setMsg("");
            json.setRows(vos);
            json.setSuccess(true);
        } catch (Exception e) {
            JsonErrorUtil.jsonErrorLog(json, log, e, "文件导入失败!");
        }
        if (!StringUtil.isEmpty(opdate)) {
            DZFDate date = new DZFDate(opdate + "-01");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY,
                    "导入工资表：" + date.getYear() + "年" + date.getMonth() + "月", ISysConstants.SYS_2);
        }
        return ReturnData.ok().data(json);
    }

    private Object[] onBoImp(MultipartFile infile, String opdate, String filename, String billtype, CorpVO corpvo)
            throws Exception {
        InputStream is = null;
        try {
            is = infile.getInputStream();
            Workbook rwb = null;
            if ("xls".equals(filename)) {
                rwb = new HSSFWorkbook(is);
            } else if ("xlsx".equals(filename)) {
                rwb = new XSSFWorkbook(is);
            } else {
                throw new BusinessException("不支持的文件格式");
            }
            // XSSFWorkbook rwb = new XSSFWorkbook(is);
            int sheetno = rwb.getNumberOfSheets();
            if (sheetno == 0) {
                throw new Exception("需要导入的数据为空。");
            }
            Sheet sheets = rwb.getSheetAt(0);// 取第2个工作簿
            CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
            ISalaryReportExcel salExcel = null;
            if ("2019-01".compareTo(opdate) > 0
                    || (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
                salExcel = factory.produce(corptaxvo);
            } else {
                salExcel = factory.produce2019(corptaxvo);
            }
            SalaryReportVO[] vos = salExcel.impExcel(null, sheets, opdate, billtype, corptaxvo);

            /// 根据类型拆成多个数组
            if (vos == null || vos.length <= 0) {
                throw new BusinessException("导入文件数据为空，请检查。");
            }

            Object[] objs = gl_gzbserv.saveImpExcel(SystemUtil.getLoginDate(), SystemUtil.getLoginUserId(), corpvo, vos, opdate,
                    vos[0].getImpmodeltype(), billtype);

            return objs;
        } catch (FileNotFoundException e2) {
            throw new Exception("文件未找到");
        } catch (IOException e2) {
            throw new Exception("文件格式不正确，请选择导入文件");
        } catch (Exception e2) {
            throw e2;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    @PostMapping("/expExcelData")
    public void expExcelData(HttpServletResponse response, @RequestParam Map<String, String> pmap) {

        String billtype = pmap.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        String pk_corp = pmap.get("pk_corp");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("公司为空");
        String opdate = pmap.get("opdate");
        if (StringUtil.isEmpty(opdate))
            throw new BusinessException("期间为空");

        // 查询工资表数据
        SalaryReportVO[] listVo = gl_gzbserv.query(pk_corp, opdate, billtype);
        if (listVo == null || listVo.length == 0)
            return;

        String zbr = pmap.get("zbr");// 制表人
        for (SalaryReportVO vo : listVo) {
            vo.setZjlx(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getName());
            if (!StringUtil.isEmpty(zbr) && new DZFBoolean(zbr).booleanValue()) {
                if (StringUtil.isEmpty(vo.getCoperatorid())) {
                    vo.setCoperatorid(SystemUtil.getLoginUserId());
                }
            } else {
                vo.setCoperatorid(null);
            }
        }
        String qj = listVo[0].getQj();
        String[] qjval = qj.split("-");
        StringBuffer sb = new StringBuffer();
        sb.append(qjval[0]).append("年").append(qjval[1])
                .append("月工资表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ").xls");
        ExcelReport<SalaryReportVO> ex = new ExcelReport<>();
        String hiddenphone = pmap.get("hiddenphone");

        List<Integer> hiddenColList = getHiddenColumn(billtype);
        if (!StringUtil.isEmpty(hiddenphone) && new DZFBoolean(hiddenphone).booleanValue()) {
            // 隐藏手机号
            hiddenColList.add(1);
        }
        Map<String, String> map = SalaryReportColumn.getMapColumn(hiddenColList, billtype);

        map.remove("fykmname");

        String[] enFields = new String[map.size()];
        String[] cnFields = new String[map.size()];
        // 填充普通字段数组
        int count = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }

        List<SalaryReportVO> list = new ArrayList<>();
        for (SalaryReportVO vo : listVo) {
            list.add(vo);
        }

        SalaryReportVO nvo = calTotal(list.toArray(new SalaryReportVO[list.size()]));
        list.add(nvo);
        OutputStream toClient = null;
        DZFDate udate = new DZFDate();
        try {
            response.reset();
            String exName = sb.toString();
            String formattedName = URLEncoder.encode(exName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + exName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            ex.exportExcel("工 资 表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")",
                    cnFields, enFields, list, SystemUtil.getLoginCorpVo().getUnitname(), qj, toClient, userServiceImpl);
//            String srt2 = new String(length, "UTF-8");
//            response.addHeader("Content-Length", srt2);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误", e);
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "导出工资表：" + qjval[0] + "年" + qjval[1] + "月",
                ISysConstants.SYS_2);
        }
    }

    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
        OutputStream toClient = null;
        try {
            String billtype = pmap.get("billtype");
            if (StringUtil.isEmpty(billtype))
                throw new BusinessException("类型为空");
            String type = pmap.get("type");
            if (StringUtil.isEmpty(type))
                type = "1";
            response.reset();
            String fileName = null;
            ExcelReport<SalaryReportVO> ex = new ExcelReport<>();
            if ("1".equals(type)) {
                if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
                    fileName = "salarytemplate.xlsx";
                } else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
                    fileName = "salarytemplate_lw.xls";
                } else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
                    fileName = "salarytemplate_wj.xls";
                } else if (billtype.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
                    fileName = "salarytemplate_nz.xls";
                }

                // 设置response的Header
                String date = "工资表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")";
                String formattedName = URLEncoder.encode(date, "UTF-8");

                if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
                    response.addHeader("Content-Disposition",
                            "attachment;filename=" + date + ";filename*=UTF-8''" + formattedName+ ".xlsx");
                } else {
                    response.addHeader("Content-Disposition",
                            "attachment;filename=" + date + ";filename*=UTF-8''" + formattedName+ ".xls");
                }
                toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/vnd.ms-excel;charset=gb2312");
                ex.expFile(toClient, fileName);
            } else {
                // 设置response的Header
                String formattedName = URLEncoder.encode("操作说明", "UTF-8");
                response.addHeader("Content-Disposition",
                        "attachment;filename=操作说明;filename*=UTF-8''" + formattedName+ ".docx");
                toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/vnd.ms-excel;charset=gb2312");
                fileName = "instructions.docx";
                ex.expFile(toClient, fileName);
            }
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误", e);
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "导出工资表模版", ISysConstants.SYS_2);
        }
    }

    @PostMapping("/expNSSBB")
    public void expNSSBB(HttpServletResponse response,  @RequestParam Map<String, String> pmap) {
        OutputStream toClient = null;
        String period = pmap.get("period");
        try {

            if (StringUtil.isEmpty(period))
                throw new BusinessException("期间为空");

            String billtype = pmap.get("billtype");
            if (StringUtil.isEmpty(billtype))
                throw new BusinessException("类型为空");

            String pk_corp = pmap.get("pk_corp");
            if (StringUtil.isEmpty(pk_corp))
                throw new BusinessException("公司为空");
            // 查询工资表数据
            // 查询工资表数据

            // 查询工资表数据
            SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, period, billtype);
            if (vos.length == 0)
                return;

            String json = JsonUtils.serialize(vos);
            JSONArray jsonArray = (JSONArray) JSON.parseArray(json);
            response.reset();
            CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
            // CorpVO corpvo = (CorpVO) singlebo.queryByPrimaryKey(CorpVO.class,
            // pk_corp);
            ISalaryReportExcel salExcel = null;
            if ("2019-01".compareTo(period) > 0
                    || (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
                salExcel = factory.produce(corptaxvo);
            } else {
                salExcel = factory.produce2019(corptaxvo);
            }

            String exName = new String(SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + "("
                    + salExcel.getAreaName(corptaxvo) + ").xls");

            if (StringUtil.isEmpty(salExcel.getAreaName(corptaxvo))) {
                exName = new String(SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ".xls");
            }

            String formattedName = URLEncoder.encode(exName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + exName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            Map<String, Integer> tabidsheetmap = new HashMap<String, Integer>();
            tabidsheetmap.put("B100000", 0);
            salExcel.exportExcel(jsonArray, toClient, billtype, corptaxvo, SystemUtil.getLoginUserVo());
            toClient.flush();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {

                    toClient.close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            String date[] = period.split("-");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "导出纳税申报表：" + date[0] + "年" + date[1] + "月",
                    ISysConstants.SYS_2);
        }
    }


    @PostMapping("/expPersonCheck")
    public ReturnData expPersonCheck(@RequestParam Map<String, String> pmap) {
        Json json = new Json();
        List<SalaryReportVO>  list = getSalaryDataList(pmap);
        String period = pmap.get("period");
        if ("2019-01".compareTo(period) > 0) {
        } else {
            for (SalaryReportVO vo : list) {
                checkPersonInfo(vo);
            }
        }
        json.setMsg("excel导出校验成功");
        json.setSuccess(true);
        return ReturnData.ok();
    }

    private List<SalaryReportVO>  getSalaryDataList(Map<String, String> pmap){

        String period = pmap.get("period");
        if (StringUtil.isEmpty(period))
            throw new BusinessException("期间为空");
        String billtype = pmap.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");

        String pk_corp = pmap.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp))
            throw new BusinessException("公司为空");

        List<SalaryReportVO> list = new ArrayList<>();
        // 查询工资表数据
        SalaryReportVO[] vos = gl_gzbserv.queryAllType(pk_corp, period);
        if (vos == null || vos.length == 0)
            return list;
        Map<String, List<String>> map = gl_gzbserv.queryAllTypeBeforeCurr(pk_corp, period);
        String qj = vos[0].getQj();

        String preqj = DateUtils.getPreviousPeriod(qj);
        SalaryReportVO[] vos1 = gl_gzbserv.queryAllType(vos[0].getPk_corp(), preqj);// 查询上一个月工资表数据


        List<String> slist = new ArrayList<>();
        for (SalaryReportVO vo : vos) {
            String minqj = null;
            List<String> qlist = map.get(vo.getCpersonid());
            if (qlist == null || qlist.size() == 0) {
                minqj = period;
            } else {
                minqj = qlist.get(0);
            }
            if (StringUtil.isEmpty(vo.getVdef1()))
                vo.setVdef1(DateUtils.getPeriodStartDate(minqj).toString());
            vo.setRyzt("正常");
            list.add(vo);
            slist.add(vo.getZjbm());
        }

        if (vos1 != null && vos1.length > 0) {
            for (SalaryReportVO vo : vos1) {
                if (!slist.contains(vo.getZjbm())) {
                    String minqj = null;
                    String maxqj = null;
                    List<String> qlist = map.get(vo.getCpersonid());
                    if (qlist == null || qlist.size() == 0) {
                        minqj = period;
                        maxqj = period;
                    } else {
                        minqj = qlist.get(0);
                        maxqj = qlist.get(qlist.size() - 1);
                    }
                    if (StringUtil.isEmpty(vo.getVdef1()))
                        vo.setVdef1(DateUtils.getPeriodStartDate(minqj).toString());
                    if (StringUtil.isEmpty(vo.getVdef2()))
                        vo.setVdef2(DateUtils.getPeriodEndDate(maxqj).toString());
                    vo.setRyzt("非正常");
                    list.add(vo);
                }
            }
        }
       return list;
    }

    @PostMapping("/expPerson")
    public void expPerson(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
        List<SalaryReportVO>  list = getSalaryDataList(pmap);
        String billtype = pmap.get("billtype");
        String period = pmap.get("period");
        String pk_corp = pmap.get("pk_corp");
        expPerson(response,list,pk_corp,period,billtype);
    }

    private void expPerson(HttpServletResponse response, List<SalaryReportVO> list, String pk_corp, String period,
                           String billtype) {
        OutputStream toClient = null;
        try {
            CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
            ISalaryReportExcel salExcel = null;
            if ("2019-01".compareTo(period) > 0
                    || (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
                salExcel = factory.produce(corptaxvo);
            } else {
                salExcel = factory.produce2019(corptaxvo);
                for (SalaryReportVO vo : list) {
                    if (SalaryTypeEnum.REMUNERATION.getValue().equals(vo.getBilltype())) {
                        vo.setSfgy("雇员");
                    } else {
                        vo.setSfgy("雇员");
                    }
                }
            }
            String json = JsonUtils.serialize(list.toArray(new SalaryReportVO[list.size()]));
            JSONArray jsonArray = (JSONArray) JSON.parseArray(json);
            response.reset();
            String exName = new String("人员信息.xls");
            String formattedName = URLEncoder.encode(exName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + exName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            salExcel.expPerson(jsonArray, response.getOutputStream(), billtype);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            String date[] = period.split("-");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "导出个人信息：" + date[0] + "年" + date[1] + "月",
                    ISysConstants.SYS_2);
        }
    }

    private void checkPersonInfo(SalaryReportVO vo) {
        if (StringUtil.isEmpty(vo.getZjlx())) {
            throw new BusinessException("职员信息证件类型不能为空");
        } else {
            if (!SalaryReportEnum.IDCARD.getValue().equals(vo.getZjlx())) {
                if (StringUtil.isEmpty(vo.getYgname())) {
                    throw new BusinessException("职员信息姓名不能为空");
                }
                if (StringUtil.isEmpty(vo.getVarea())) {
                    throw new BusinessException("职员信息国籍不能为空");
                }
                if (StringUtil.isEmpty(vo.getVdef1())) {
                    throw new BusinessException("职员信息任职受雇日期不能为空");
                }
//				if (StringUtil.isEmpty(vo.getVdef2())) {
//					throw new BusinessException("离职日期不能为空");
//				}
                if (StringUtil.isEmpty(vo.getVdef3())) {
                    throw new BusinessException("职员信息出生日期不能为空");
                }
                if (StringUtil.isEmpty(vo.getVdef4())) {
                    throw new BusinessException("职员信息性别不能为空");
                }
            }
        }
    }

    private List<Integer> getHiddenColumn(String billtype) {
        List<Integer> list = new ArrayList<>();
        int[] hidenCol = null;
        if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
            hidenCol = SalaryReportColumn.LWHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
            hidenCol = SalaryReportColumn.WJGZHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
            hidenCol = SalaryReportColumn.NZJHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
            hidenCol = SalaryReportColumn.ZCHIDEN;
        }

        if (hidenCol == null || hidenCol.length == 0)
            return list;
        for (int col : hidenCol) {
            list.add(col);
        }
        return list;
    }

    @PostMapping("/setFykm")
    public ReturnData<Json> setFykm(@RequestBody Map<String, String> map) {
        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String billtype = map.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        String pk_corp = map.get("ops");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String fykmid = map.get("fykmid");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("更新科目为空");
        }
        String primaryKey = map.get("pks");
        if (StringUtil.isEmpty(primaryKey)) {
            throw new BusinessException("更新数据为空");
        }
        checkOwnCorp(pk_corp);
        gl_gzbserv.updateFykm(pk_corp, fykmid, primaryKey, qj, billtype);
        json.setMsg("更新费用科目成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "更新费用科目", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/setDept")
    public ReturnData<Json> setDept(@RequestBody Map<String, String> map) {
        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String billtype = map.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        String pk_corp = map.get("ops");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String cdeptid = map.get("cdeptid");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("更新部门为空");
        }
        String primaryKey = map.get("pks");
        if (StringUtil.isEmpty(primaryKey)) {
            throw new BusinessException("更新数据为空");
        }
        checkOwnCorp(pk_corp);
        gl_gzbserv.updateDeptid(pk_corp, cdeptid, primaryKey, qj, billtype);
        json.setMsg("更新部门成功");
        json.setSuccess(true);

        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "更新费用科目", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/getSalaryAccSet")
    public ReturnData<Json> getSalaryAccSet(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String billtype = map.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        String pk_corp = map.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String isnew = map.get("isnew");
        SalaryReportVO[] vos = null;
        String cpersonids = map.get("cpersonids");
        String opdate = map.get("opdate");
        if (StringUtil.isEmpty(opdate)) {
            throw new BusinessException("期间为空");
        }
        if (isnew == null || !"true".equals(isnew)) {
            vos = gl_gzbserv.getSalarySetInfo(pk_corp, billtype, cpersonids, opdate);
        } else {
            if ("2019-01".compareTo(opdate) > 0 || (!StringUtil.isEmpty(billtype)
                    && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
                vos = gl_gzbserv.getSalarySetInfo(pk_corp, billtype, cpersonids, opdate);
            } else {
                vos = gl_gzbserv.calLjData(pk_corp, cpersonids, billtype, opdate);
            }
        }
        json.setMsg("获取工资科目设置成功");
        json.setRows(vos);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/changeNum")
    public ReturnData<Json> changeNum(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String billtype = map.get("billtype");
        if (StringUtil.isEmpty(billtype))
            throw new BusinessException("类型为空");
        String pk_corp = map.get("ops");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String primaryKey = map.get("pks");
        if (StringUtil.isEmpty(primaryKey)) {
            throw new BusinessException("更新数据为空");
        }
        checkOwnCorp(pk_corp);
        String strlist = map.get("chgdata");
        if (!StringUtil.isEmpty(strlist) && (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())
                || billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue()))) {
            SalaryBaseVO setvo = JsonUtils.deserialize(strlist, SalaryBaseVO.class);
            gl_gzbbaseserv.updateChangeNum(pk_corp, setvo, primaryKey, qj, billtype);
        }
        json.setMsg("调整基数成功");
        json.setSuccess(true);

        writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "调整基数", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/checkJzDate")
    public ReturnData<Json> checkJzDate(@RequestParam("date") String date, @RequestParam("corp_id") String pk_corp) {

        Json json = new Json();
        checkOwnCorp(pk_corp);
        CorpVO corpVo = corpService.queryByPk(pk_corp);
        if (corpVo == null)
            throw new BusinessException("选择公司出错！");
        else if (corpVo.getBegindate() == null)
            throw new BusinessException("公司建账日期为空！");
        json.setData(corpVo.getBegindate().toDate());
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getNationalArea")
    public ReturnData<Json> getNationalArea() {
        Json json = new Json();
        String nationalArea = NationalAreaUtil.getNationalArea();
        String[] nationals = nationalArea.split(",");
        List<SalaryReportVO> list = new ArrayList<>();
        for (String str : nationals) {
            SalaryReportVO vo = new SalaryReportVO();
            vo.setVarea(str);
            list.add(vo);
        }
        json.setRows(list);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}