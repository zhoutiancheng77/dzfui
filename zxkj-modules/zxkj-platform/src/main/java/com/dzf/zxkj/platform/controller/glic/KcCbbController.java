package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.VOSortUtils;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.KccbNewExcelField;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.IKcCbb;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * 库存成本表
 *
 */
@RestController
@RequestMapping("/glic/gl_rep_kcccbact")
@Slf4j
public class KcCbbController extends GlicReportController{
    @Autowired
    private IKcCbb ic_rep_cbbserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IParameterSetService parameterserv;
    @GetMapping("/query")
    public ReturnData<Json> queryAction(@RequestParam Map<String, String> param){
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        checkPowerDate(queryParamvo);
        Map<String, IcDetailVO> result = ic_rep_cbbserv.queryDetail(queryParamvo,SystemUtil.getLoginCorpVo());
        if(result == null || result.size() == 0){
            throw new BusinessException("查询数据为空");
        }
        //将查询后的数据分页展示
        List<IcDetailVO> list = getPagedMXZVos( result, queryParamvo.getPage(),queryParamvo.getRows() , grid);
        grid.setKcDetail(list);
        grid.setRows(list);
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    private List<IcDetailVO> getPagedMXZVos(Map<String, IcDetailVO> result, int page, int rows,
                                            ReportDataGrid grid){
        List<IcDetailVO> spList = new ArrayList<>();
        List<IcDetailVO> spList1 = new ArrayList<>();
        Set<Map.Entry<String, IcDetailVO>> entrySet = result.entrySet();
        Iterator<Map.Entry<String, IcDetailVO>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, IcDetailVO> entry = iter.next();
            spList.add(entry.getValue());
        }
        if(spList != null && spList.size() > 0){
            int start= (page-1)*rows;
            for(int i = start ; i < page * rows && i < spList.size(); i++){
                spList1.add(spList.get(i));
            }
            grid.setTotal((long)spList1.size());
        }else{
            grid.setTotal(0L);
        }
        return spList1;
    }
    @GetMapping("/showzg")
    public ReturnData<Json> showzg(){
        Grid grid = new Grid();
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            String userid = SystemUtil.getLoginUserId();
            DZFDate doped = new DZFDate(SystemUtil.getLoginDate());

            List<TempInvtoryVO> cvos = ic_rep_cbbserv.queryZgVOs(corpvo, userid, doped);
            if(cvos != null && cvos.size() > 1){
                Collections.sort(cvos, new Comparator<TempInvtoryVO>() {
                    @Override
                    public int compare(TempInvtoryVO o1, TempInvtoryVO o2) {
                        return VOSortUtils.compareContainsNull(o1.getKmbm(), o2.getKmbm());
                    }
                });
            }
            grid.setRows(cvos== null ? new ArrayList<TempInvtoryVO>() : cvos);
            grid.setTotal(cvos == null ? 0L : (long)cvos.size());
            grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/adjustKc")
    public ReturnData adjustKc( @RequestParam("period") String period){
        Json json = new Json();
        if(StringUtil.isEmpty(period))
            throw new BusinessException("期间数据为空,请检查");
        String pk_corp =SystemUtil.getLoginCorpId() ;
        TzpzHVO headvo = ic_rep_cbbserv.queryJzPz(pk_corp, period);
        boolean flag = true;
        if(headvo == null){
            flag = false;
        }
        json.setSuccess(flag);
        json.setHead(headvo);
        json.setMsg(flag ? "查询成功" : period + "没有结转凭证，请检查");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/zgsave")
    public ReturnData zgsave(@RequestBody Map<String, String> map){
        Grid grid = new Grid();
        //处理暂估
        String period = "";
        String zg = map.get("zg");
        if(StringUtil.isEmpty(zg))
            throw new BusinessException("暂估数据不全，请检查");
        zg = zg.replace("}{", "},{");
//        zg = "[" + zg + "]";
        TempInvtoryVO[] bodyvos = JsonUtils.deserialize(zg, TempInvtoryVO[].class);
        period = bodyvos[0].getPeriod();
        ic_rep_cbbserv.saveZg(bodyvos, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        grid.setSuccess(true);
        grid.setMsg("暂估处理成功");
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:存货暂估期间“" + period + "”存货数据", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
        String period = "";
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            PrintParamVO printvo = JsonUtils.convertValue(pmap, PrintParamVO.class);//
            String strlist = pmap.get("list");
            if (strlist == null) {
                return;
            }
            if (printvo.getPageOrt().equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
            strlist = strlist.replace("}{", "},{");
            IcDetailVO[] bodyvos =JsonUtils.deserialize(strlist, IcDetailVO[].class);
            if(bodyvos!=null && bodyvos.length>0){
                for(IcDetailVO vo:bodyvos){
                    vo.setPk_corp(SystemUtil.getLoginCorpId());
                }
            }
            period = bodyvos[0].getTitlePeriod();
            String gs = bodyvos[0].getGs();
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", gs);
            tmap.put("期间", period);
//            ColumnCellAttr[] columncellattrvos= JsonUtils.convertValue(printvo.getColumnslist(), ColumnCellAttr[].class);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));
            //初始化表体列编码和列名称
            List<ColumnCellAttr> list = new ArrayList<>();
            String strclassif = pmap.get("classif");
            DZFBoolean flag = new DZFBoolean(strclassif);
//            if(!flag.booleanValue())
//             list.add(new ColumnCellAttr("存货类别",null,null,2,"spfl_name",3));
            list.add(new ColumnCellAttr("存货名称",null,null,2,"spmc",3));
            list.add(new ColumnCellAttr("规格(型号)",null,null,2,"spgg",3));
            list.add(new ColumnCellAttr("计量单位",null,null,2,"jldw",2));
            list.add(new ColumnCellAttr("期初",null,3,null,null,3));
            list.add(new ColumnCellAttr("入库",null,3,null,null,3));
            list.add(new ColumnCellAttr("出库",null,3,null,null,3));
            list.add(new ColumnCellAttr("结存",null,3,null,null,3));
            list.add(new ColumnCellAttr("数量",null,null,null,"qcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"qcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"qcje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"srsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"srdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"srje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"fcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"fcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"fcje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"jcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"jcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"jcje",3));

            printReporUtil.printReport(bodyvos,"库存成本表",list,15,pmap.get("type"),pmap,tmap);
        } catch (DocumentException e) {
            log.error("库存成本表打印错误", e);
        } catch (IOException e) {
            log.error("库存成本表打印错误", e);
        }catch (Exception e) {
            log.error("库存成本表打印失败", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存成本表打印错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:打印期间“" + period + "”存货数据", ISysConstants.SYS_2);
    }

    /**
     * 导出excel
     */
    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
        OutputStream toClient = null;
        String qj = "";
        try {
            String strlist = param.get("list");
            if (strlist == null) {
                return;
            }
            String strclassif = param.get("classif");
            strlist = strlist.replace("}{", "},{");
            IcDetailVO[] vo =JsonUtils.deserialize(strlist, IcDetailVO[].class);
            String gs = vo[0].getGs();
            qj = vo[0].getTitlePeriod();
            Excelexport2003<IcDetailVO> lxs = new Excelexport2003<IcDetailVO>();

            String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

            KccbNewExcelField xsz = new KccbNewExcelField(num, price);
            DZFBoolean flag = new DZFBoolean(strclassif);

            xsz.setFields(flag.booleanValue() ? xsz.getFields2() : xsz.getFields1());
            xsz.setIcDetailVos(vo);
            xsz.setQj(qj);
            xsz.setCreator(SystemUtil.getLoginUserVo().getUser_name());
            xsz.setCorpName(gs);
            response.reset();
            String filename = xsz.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(xsz, toClient);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("库存成本表excel导出错误", e);
        }catch (Exception e) {
            log.error("库存成本表excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("库存成本表excel导出错误", e);
            }
            try {
                if (response!=null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存成本表excel导出错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:导出期间“" + qj + "”存货数据", ISysConstants.SYS_2);
    }

    @GetMapping("/getClassifyFlag")
    public ReturnData getClassifyFlag(){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        InventorySetVO vo = gl_ic_invtorysetserv.query(pk_corp);

        boolean flag = true;
        if(vo != null && vo.getChcbjzfs() == 1){
            flag = false;
        }
        json.setSuccess(true);
        json.setHead(flag);
        json.setMsg("查询成功");
        return ReturnData.ok().data(json);
    }
}
