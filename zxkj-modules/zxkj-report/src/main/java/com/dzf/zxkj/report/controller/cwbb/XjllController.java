package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.report.ReportDataGrid.XjllMsgVo;
import com.dzf.zxkj.platform.model.report.XjllMxvo;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.XjllMXbExcelField;
import com.dzf.zxkj.report.excel.cwbb.XjllbExcelField;
import com.dzf.zxkj.report.service.cwbb.IXjllbReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_xjlybact")
@Slf4j
public class XjllController extends ReportBaseController {

    @Autowired
    private IXjllbReport gl_rep_xjlybserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     *
     */
    @PostMapping("/queryAction")
    public ReturnData<ReportDataGrid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{vo.getPk_corp()},null);
            if (vo != null) {
                checkPowerDate(vo,corpVO);
                XjllbVO[] xjllbvos = gl_rep_xjlybserv.query(vo);
                if (xjllbvos != null && xjllbvos.length > 0) {
                    grid.setTotal((long) xjllbvos.length);
                    grid.setRows(Arrays.asList(xjllbvos));
                }
                //赋值不平衡信息
                putBlanceMsg(grid,xjllbvos);
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XjllbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        // 日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "现金流量表查询:" + vo.getQjq(), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }


    private void putBlanceMsg(ReportDataGrid grid, XjllbVO[] xjllbvos) {
        grid.setBlancemsg(true);
        grid.setBlancetitle("");
        DZFDouble xjlltotal = DZFDouble.ZERO_DBL;
        DZFDouble kmqcvalue = DZFDouble.ZERO_DBL;
        DZFDouble kmqmvalue = DZFDouble.ZERO_DBL;
        if (xjllbvos != null && xjllbvos.length > 0) {
            XjllMsgVo xjllmsgvo = grid.new XjllMsgVo();
            for (XjllbVO bvo : xjllbvos) {
                if (bvo.getBxjlltotal() != null && bvo.getBxjlltotal().booleanValue()) {
                    xjlltotal = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (bvo.getBkmqc() != null && bvo.getBkmqc().booleanValue()) {
                    kmqcvalue = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (bvo.getBkmqm() != null && bvo.getBkmqm().booleanValue()) {
                    kmqmvalue = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
            }
            DZFDouble ce = xjlltotal.sub(kmqmvalue.sub(kmqcvalue));
            xjllmsgvo.setXjlltotal(xjlltotal);
            xjllmsgvo.setKmqcvalue(kmqcvalue);
            xjllmsgvo.setKmqmvalue(kmqmvalue);
            xjllmsgvo.setCe(ce);
            if (ce.doubleValue()!=0) {
                grid.setBlancemsg(false);
                grid.setBlancetitle("不平衡");
            }
            grid.setXjll_jyx(xjllmsgvo);
        }
    }

    /**
     * 联查现金流量明细账
     */
    @PostMapping("/queryMxAction")
    public ReturnData<Grid> queryMxAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Grid grid = new Grid();
        try {
            QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
            // 校验
            checkSecurityData(null, new String[]{queryvo.getPk_corp()},null);
            if(vo != null){
                XjllMxvo[] xjllMxvo = gl_rep_xjlybserv.getXJllMX(vo.getQjq(), vo.getPk_corp(), vo.getHc());
                if(xjllMxvo != null && xjllMxvo.length > 0){
                    grid.setTotal((long)xjllMxvo.length);
                    grid.setRows(Arrays.asList(xjllMxvo));
                }
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XjllMxvo>());
            printErrorLog(grid, e, "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }


    private String getPubParam(CorpVO cpvo) {
        return "corpIds="+cpvo.getPk_corp()+"&gsname="+ CodeUtils1.deCode(cpvo.getUnitname());
    }

    @PostMapping("/linkPz")
    public  ReturnData<Json> linkPz(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Json json = new Json();
        try {
            QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
            // 校验
            checkSecurityData(null, new String[]{vo.getPk_corp()},null);
            //凭证查询vo
            VoucherParamVO pzparamvo = new VoucherParamVO();
            pzparamvo.setPk_corp(vo.getPk_corp());
            pzparamvo.setBegindate(DateUtils.getPeriodStartDate(vo.getQjq()));
            pzparamvo.setEnddate(DateUtils.getPeriodEndDate(vo.getQjq()));
            pzparamvo.setIs_error_cash(Boolean.TRUE);
            QueryPageVO pagevo = zxkjPlatformService.processQueryVoucherPaged(pzparamvo);
            SuperVO[] vos = (SuperVO[]) pagevo.getPagevos();
            String url = "";
            if(vos!=null && vos.length ==1){
                //填制凭证界面
                url = (String)vos[0].getAttributeValue("pk_tzpz_h");
            }else{
                url = "gl/gl_pzgl/gl_pzgl.jsp?";
            }
            json.setSuccess(true);
            json.setMsg("成功");
            json.setRows(url);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return  ReturnData.ok().data(json);
    }

    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        XjllbVO[] listVo= JsonUtils.deserialize(excelExportVO.getList(),XjllbVO[].class);

        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();
        String corpIds = queryparamvo.getPk_corp();
        if(StringUtil.isEmpty(corpIds)){
            corpIds = corpVO.getPk_corp();
        }
        Excelexport2003<XjllbVO> lxs = new Excelexport2003<XjllbVO>();
        XjllbExcelField xjll = new XjllbExcelField();
        xjll.setZeroshownull(!queryparamvo.getBshowzero().booleanValue());
        xjll.setColumnOrder(excelExportVO.getColumnOrder());
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(corpIds);

        if(cpvo!=null){
            xjll.setCorptype(cpvo.getCorptype());
        }
        getXjllExcel(excelExportVO,queryparamvo,userVO,listVo, gs, qj, xjll);//获取现金流量的数据

        baseExcelExport(response,lxs,xjll);

        String excelsel = excelExportVO.getExcelsel();
        if(!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)){
            qj  = qj.substring(0, 4);
        }
        // 日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "现金流量表导出:" +  qj, ISysConstants.SYS_2);
    }

    //导出Excel
    @PostMapping("export/excelmx")
    public void excelReportMx(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        XjllMxvo[] listVo = JsonUtils.deserialize(excelExportVO.getList(), XjllMxvo[].class);
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();
        String corpIds = queryparamvo.getPk_corp();
        if (StringUtil.isEmpty(corpIds)) {
            corpIds =  corpVO.getPk_corp();
        }
        Excelexport2003<XjllMxvo> lxs = new Excelexport2003<XjllMxvo>();
        XjllMXbExcelField xjll = new XjllMXbExcelField();
        xjll.setKmmxvos(listVo);
        xjll.setCorpName(gs);
        xjll.setQj(qj);

        baseExcelExport(response,lxs,xjll);

        String excelsel = excelExportVO.getExcelsel();
        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {
            qj = qj.substring(0, 4);
        }
        // 日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "现金流量表明细导出:" + qj, ISysConstants.SYS_2);
    }

    private void getXjllExcel(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryParamvo,UserVO userVO , XjllbVO[] listVo, String gs, String qj, XjllbExcelField xjllb) {

        List<XjllbVO[]> lrbvos = new ArrayList<XjllbVO[]>();

        String excelsel = excelExportVO.getExcelsel();

        String[] strs = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };

        List<String> periods = new ArrayList<String>();

        List<String> titlename = new ArrayList<String>();
        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {// 按照年来查询

            queryParamvo.setQjq(queryParamvo.getQjq().substring(0, 4)+"-01");

            queryParamvo.setQjz(queryParamvo.getQjq().substring(0, 4)+ "-12");

            Map<String, XjllbVO[]> mapvalues = gl_rep_xjlybserv.queryEveryPeriod(queryParamvo);

            CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());

            String begstr = null;

            if (cpvo.getBegindate().getYear() == Integer.parseInt(queryParamvo.getQjq().substring(0, 4))) {// 和建账日期对比
                begstr = DateUtils.getPeriod(cpvo.getBegindate()) + "-01";// 从一月份开始查询
            }else{
                begstr =  queryParamvo.getQjq().substring(0, 4)+"-01" + "-01";
            }

            String endstr =  queryParamvo.getQjq().substring(0, 4)+ "-12" + "-01";// 从一月份开始查询

            periods = ReportUtil.getPeriods(new DZFDate(begstr), new DZFDate(endstr));

            for (String period : periods) {
                if (mapvalues.get(period) != null && mapvalues.get(period).length > 0) {
                    lrbvos.add(mapvalues.get(period));
                }
            }

            for (int i = strs.length - lrbvos.size(); i < 12; i++) {
                titlename.add(strs[i]);
            }
        }else{
            lrbvos.add(listVo);
            titlename.add("现金流量表");
            periods.add(qj);
        }

        xjllb.setPeriods(periods.toArray(new String[0]));
        xjllb.setAllsheetxjllvos(lrbvos);
        xjllb.setAllsheetname(titlename.toArray(new String[0]));
        xjllb.setXjllbvos(listVo);
        xjllb.setQj(qj);
        xjllb.setCreator(userVO.getUser_name());
        xjllb.setCorpName(gs);
    }


    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String strlist = printParamVO.getList();
            String type = printParamVO.getType();
            String font = printParamVO.getFont();
            String columnOrder = printParamVO.getColumnOrder();
            if(strlist==null){
                return;
            }
            XjllbVO[] bodyvos =JsonUtils.deserialize(strlist, XjllbVO[].class);
            Map<String,String> tmap=new LinkedHashMap<String,String>();//声明一个map用来存前台传来的设置参数
            tmap.put("公司",  printParamVO.getCorpName());
            tmap.put("期间",  printParamVO.getTitleperiod());
            tmap.put("单位",  "元");
            QueryParamVO paramvo = new QueryParamVO();
            paramvo.setPk_corp(corpVO.getPk_corp());
            List<CorpTaxVo> listVos = zxkjPlatformService.queryTaxVoByParam(paramvo, userVO);
            if(listVos != null && listVos.size() > 0){
                Optional<CorpTaxVo> optional = listVos.stream().filter(v-> corpVO.getPk_corp().equals(v.getPk_corp())).findFirst();
                optional.ifPresent(corpTaxVo ->{
                    if(!StringUtil.isEmpty(corpTaxVo.getLegalbodycode())){
                        pmap.put("单位负责人", corpTaxVo.getLegalbodycode());
                    }
                    if(!StringUtil.isEmpty(corpTaxVo.getLinkman1())){
                        pmap.put("财务负责人", corpTaxVo.getLinkman1());
                    }
                    pmap.put("制表人", userVO.getUser_name());
                });
            }
            if(type.equals("1") &&  bodyvos.length>33){//A4纸
                printReporUtil.setLineheight(18f);
            }
            if(type.equals("2")){//B5纸
                printReporUtil.setLineheight(12f);
            }
            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setBf_Bold(printReporUtil.getBf());
            printReporUtil.setBasecolor(new BaseColor(0,0,0));//设置单元格线颜色
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));
//			CorpVO cpvo = CorpCache.getInstance().get("", corpIds);
            if("on".equalsIgnoreCase(columnOrder)){
                printReporUtil.printHz(getXjLlToPrint(queryparamvo),null,"现 金 流 量 表",
                        new String[]{"xm","hc","bqje","sqje"},
                        new String[]{"项            目","行次","本月金额","本年累计金额"},
                        new int[]{7,1,2,2},20,pmap,tmap);
            }else{
                printReporUtil.printHz(getXjLlToPrint(queryparamvo),null,"现 金 流 量 表",
                        new String[]{"xm","hc","sqje","bqje"},
                        new String[]{"项            目","行次","本年累计金额","本月金额"},
                        new int[]{7,1,2,2},20,pmap,tmap);

            }
            // 日志记录接口
            writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "现金流量表打印:" +    printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    private Map<String, List<SuperVO>> getXjLlToPrint(QueryParamVO queryParamvo) {

        Map<String, List<SuperVO>> resmap = new LinkedHashMap<String, List<SuperVO>>();

        queryParamvo.setQjq(queryParamvo.getBegindate1().toString().substring(0, 7));

        queryParamvo.setQjz(queryParamvo.getEnddate().toString().substring(0, 7));

        Map<String, XjllbVO[]> mapvalues = gl_rep_xjlybserv.queryEveryPeriod(queryParamvo);

        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());

        DZFDate endstr =  DateUtils.getPeriodEndDate(queryParamvo.getEnddate().toString().substring(0,7));// 从一月份开始查询

        List<String> periods = ReportUtil.getPeriods(queryParamvo.getBegindate1(), endstr);

        for (String period : periods) {
            if (mapvalues.get(period) != null && mapvalues.get(period).length > 0) {
                List<SuperVO> t_list = new ArrayList<SuperVO>();
                for(XjllbVO vo:mapvalues.get(period)){
                    t_list.add((SuperVO)vo);
                }
                resmap.put(period, t_list);
            }
        }

        return resmap;
    }
}
