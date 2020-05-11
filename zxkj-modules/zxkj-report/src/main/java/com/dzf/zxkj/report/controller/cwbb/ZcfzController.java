package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.ZcfzExcelField;
import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportRecordEnum;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import com.dzf.zxkj.report.excel.rptexp.handler.ExcelExportPubHandler;
import com.dzf.zxkj.report.excel.rptexp.handler.TaxEnHander;
import com.dzf.zxkj.report.excel.rptexp.handler.TaxExportHander;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IXjllbReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("gl_rep_zcfzact")
@Slf4j
public class ZcfzController extends ReportBaseController {

    @Autowired
    private IZcFzBReport gl_rep_zcfzserv;

    @Autowired
    private ILrbReport gl_rep_lrbserv;

    @Autowired
    private IXjllbReport gl_rep_xjlybserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    public SingleObjectBO singleObjectBO;


    /**
     * 查询
     */
    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()}, null);
            String begindate = DateUtils.getPeriod(queryParamvo.getBegindate1());
            String ishajz = "N";
            if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }
            String ishasye = queryvo.getIshasye();
            String hasye1 = queryvo.getHasye1();
            String hasye2 = queryvo.getHasye2();
            String hasye3 = queryvo.getHasye3();
            String hasye4 = queryvo.getHasye4();
            if (ishasye == null || "".equals(ishasye)) {
                ishasye = "N";
            }
            /** 全局记忆 按往来科目明细分析填列 */
//            getSession().setAttribute("ishasye", ishasye);
            checkPowerDate(queryParamvo, corpVO);
            ZcFzBVO[] kmmxvos = null;
            Object[] objs = null;
            if ("Y".equals(ishasye)) {
                String[] yes = new String[]{ishasye, hasye1, hasye2, hasye3, hasye4};
                objs = gl_rep_zcfzserv.getZCFZBVOsConMsg(begindate, queryParamvo.getPk_corp(), ishajz, yes);
            } else {
                objs = gl_rep_zcfzserv.getZCFZBVOsConMsg(begindate, queryParamvo.getPk_corp(), ishajz, new String[]{ishasye, "N", "N", "N", "N"});
            }

            kmmxvos = (ZcFzBVO[]) objs[0];

            log.info("查询成功！");
            grid.setTotal((long) (kmmxvos == null ? 0 : kmmxvos.length));
            grid.setRows(kmmxvos == null ? new ArrayList<ZcFzBVO>() : Arrays.asList(kmmxvos));
            grid.setMsg("查询成功");
            String blancemsg = isBlance(kmmxvos, queryParamvo.getPk_corp());
            // 看看是否相平
            grid.setSuccess(true);
            if (blancemsg == null) {
                grid.setBlancemsg(true);
            } else {
                List<String[]> noblance = (List<String[]>) objs[1];
                grid.setBlancemsg(false);
                grid.setZcfz_jyx((ZcfzMsgVo) objs[2]);
//                grid.setMsg(noblance);
                grid.setMsglist(noblance);
                grid.setBlancetitle(blancemsg);
            }

        } catch (Exception e) {
            grid.setRows(new ArrayList<ZcFzBVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "资产负债查询:" + queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }


    private String isBlance2(ZcfzMsgVo zcfzmsgvo) {
        DZFDouble zcvalue = VoUtils.getDZFDouble(zcfzmsgvo.getZcvalue());
        DZFDouble fzvalue = VoUtils.getDZFDouble(zcfzmsgvo.getFzvalue());
        DZFDouble qyvale = VoUtils.getDZFDouble(zcfzmsgvo.getQyvalue());
        if (zcvalue.sub(fzvalue).sub(qyvale).doubleValue() != 0) {
            return "N";
        }
        DZFDouble wfpvalue = VoUtils.getDZFDouble(zcfzmsgvo.getWfpvlaue());
        DZFDouble jlrvalue = VoUtils.getDZFDouble(zcfzmsgvo.getJlrvalue());
        if (wfpvalue.sub(jlrvalue).doubleValue() != 0) {
            return "N";
        }
        return null;
    }

    public String isBlance(ZcFzBVO[] dataVOS, String pk_corp) {
        if (dataVOS == null || dataVOS.length == 0) {
            return null;
        }

        ZcFzBVO lastbvo = (ZcFzBVO) dataVOS[dataVOS.length - 1];
        //如果是村集体，则lastvo 不是最后一个
        Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
        if (corpschema == DzfUtil.VILLAGECOLLECTIVE.intValue()
                || corpschema == DzfUtil.RURALCOOPERATIVE.intValue()) {
            for (int i = dataVOS.length - 1; i >= 0; i--) {
                if (dataVOS[i].getZc().indexOf("资产总计") >= 0) {
                    lastbvo = dataVOS[i];
                    break;
                }
            }
        }


        DZFDouble ncye1 = (lastbvo).getNcye1() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getNcye1();
        DZFDouble ncye2 = (lastbvo).getNcye2() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getNcye2();
        DZFDouble qmye1 = (lastbvo).getQmye1() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getQmye1();
        DZFDouble qmye2 = (lastbvo).getQmye2() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getQmye2();

        StringBuffer message = new StringBuffer();
        if (qmye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(qmye2.setScale(2, DZFDouble.ROUND_HALF_UP))
                .doubleValue() != 0) {
            message.append("期末余额,");
        }
        if (ncye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(ncye2.setScale(2, DZFDouble.ROUND_HALF_UP))
                .doubleValue() != 0) {
            message.append("年初余额，");
        }

        if (message.toString().trim().length() > 0) {
//			return "资产负债：" + message.substring(0, message.toString().trim().length() - 1) + "不平";
            return "资产负债表不平,差额" + Common.format(SafeCompute.sub(qmye1, qmye2)) + ",请检查";
        } else {
            return null;
        }
    }


    @Override
    public String getPrintTitleName() {
        return "资 产 负 债 表";
    }


    /**
     * 导出Excel
     */
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()}, null);
        ZcFzBVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), ZcFzBVO[].class);
        String qj = excelExportVO.getPeriod();

        //获取利润表数据
        String corpIds = queryparamvo.getPk_corp();
        if (StringUtil.isEmpty(corpIds)) {
            corpIds = corpVO.getPk_corp();
        }
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(corpIds);
        String gs = excelExportVO.getCorpName();
        Excelexport2003<ZcFzBVO> lxs = new Excelexport2003<ZcFzBVO>();
        ZcfzExcelField zcfz = getExcelField(excelExportVO, queryparamvo, userVO, listVo, gs, qj);
        zcfz.setCorptype(cpvo.getCorptype());

        baseExcelExport(response, lxs, zcfz);

        String excelsel = excelExportVO.getExcelsel();
        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {
            qj = qj.substring(0, 4);
        }
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "资产负债导出:" + qj, ISysConstants.SYS_2);
    }

    /**
     * @return []
     * @Author gzx
     * @Description 税局报表导出
     * @Date 14:56 2018/12/21
     * @Param void
     */
    @PostMapping("exportSj/excel")
    public void excelReportSj(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()}, null);
        ZcFzBVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), ZcFzBVO[].class);
        String qj = excelExportVO.getPeriod();

        //获取利润表数据
        String corpIds = queryparamvo.getPk_corp();
        if (StringUtil.isEmpty(corpIds)) {
            corpIds = corpVO.getPk_corp();
        }
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(corpIds);
        String corpType = cpvo.getCorptype();

        //获取利润表数据
        LrbVO[] lrbvos = gl_rep_lrbserv.getLrbDataForCwBs(qj, corpIds, excelExportVO.getQjlx());
        //获取现金流量数据
        XjllbVO[] xjllbvos = gl_rep_xjlybserv.getXjllDataForCwBs(qj, corpIds, excelExportVO.getQjlx());

        //税局模式
        SQLParameter sp = new SQLParameter();
        sp.addParam(excelExportVO.getAreaType());
        sp.addParam(corpType);
        LrbTaxVo[] lrbtaxvos = (LrbTaxVo[]) singleObjectBO.queryByCondition(LrbTaxVo.class, "nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);
        ZcfzTaxVo[] zcfztaxvos = (ZcfzTaxVo[]) singleObjectBO.queryByCondition(ZcfzTaxVo.class, "nvl(dr,0)=0 and area_type = ? and corptype =?   order by ordernum", sp);
        XjllTaxVo[] xjlltaxvos = (XjllTaxVo[]) singleObjectBO.queryByCondition(XjllTaxVo.class, "nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);

        String fileType = ExportTemplateEnum.getFileType(excelExportVO.getAreaType());

        if ("1".equals(fileType)) {
            TaxExportHander taxExportHander = ExportTemplateEnum.getTaxHander(excelExportVO.getAreaType());

            Document doc = taxExportHander.writeZcfzXMLFile(listVo, lrbvos, xjllbvos, lrbtaxvos, zcfztaxvos, xjlltaxvos, cpvo, Integer.parseInt(excelExportVO.getAreaType()));

            exportTax(response, doc, "资产负债表、利润表、现金流量表(" + qj + ").tax");
        } else {
            ExcelExportHander excelExportHander = ExportTemplateEnum.getExcelHandler(excelExportVO.getAreaType());
            //设置地区、单/多、会计制度等
            excelExportHander.init(excelExportVO.getAreaType(), corpType);

            //设置上下文，用于Excel填值时取数用
            excelExportHander.setCpvo(cpvo);
            excelExportHander.setQj(qj);
            excelExportHander.setQjlx(excelExportVO.getQjlx());

            Map<String, Workbook> workBookMap = null;
            if (excelExportHander instanceof ExcelExportPubHandler)
                workBookMap = excelExportHander.handleCommon(corpType, lrbtaxvos, zcfztaxvos, xjlltaxvos, lrbvos, xjllbvos, listVo);
            else
                workBookMap = excelExportHander.handle(corpType, lrbtaxvos, zcfztaxvos, xjlltaxvos, lrbvos, xjllbvos, listVo);

            exportExcelToZip(response, workBookMap, "资产负债表、利润表、现金流量表(" + qj + ")");
        }

//        qj = qj.substring(0, 4);
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "财务报表税局模式导出:" + ExportRecordEnum.getRecordMessage(excelExportVO.getAreaType(), excelExportVO.getPeriod(), excelExportVO.getQjlx()), ISysConstants.SYS_2);
    }


    private void exportTax(HttpServletResponse response, Document doc, String taxfilename) {
        OutputStream toClient = null;
        XMLWriter writer = null;
        try {
            String formattedName = URLEncoder.encode(taxfilename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + taxfilename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream;charset=gb2312");
            // 用于格式化xml内容和设置头部标签
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置xml文档的编码为utf-8
            format.setEncoding("utf-8");
            format.setIndent(true); //设置是否缩进
            format.setIndent("    "); //以四个空格方式实现缩进
            format.setNewlines(true); //设置是否换行
            writer = new XMLWriter(toClient, format);
            writer.write(doc);
            writer.close();
            toClient.flush();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("导出错误", e);
            ;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("导出tax错误", e);
            }
        }
    }

    private ZcfzExcelField getExcelField(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryParamvo, UserVO userVO, ZcFzBVO[] listVo, String gs, String qj) {
        ZcfzExcelField zcfz = new ZcfzExcelField(!queryParamvo.getBshowzero().booleanValue());

        List<ZcFzBVO[]> listbvos = new ArrayList<ZcFzBVO[]>();

        String excelsel = excelExportVO.getExcelsel();

        String[] strs = new String[]{"一月", "二月", "三月", "四月",
                "五月", "六月", "七月", "八月",
                "九月", "十月", "十一月", "十二月"};

        List<String> periods = new ArrayList<String>();
        List<String> titlename = new ArrayList<String>();

        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {//按照年来查询
            String ishajz = "N";
            if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }

            String ishasye = queryParamvo.getIshasye();
            String hasye1 = queryParamvo.getHasye1();
            String hasye2 = queryParamvo.getHasye2();
            String hasye3 = queryParamvo.getHasye3();
            if (ishasye == null || "".equals(ishasye)) {
                ishasye = "N";
            }
            String[] yes = null;

            if ("Y".equals(ishasye)) {
                yes = new String[]{ishasye, hasye1, hasye2, hasye3};
            } else {
                yes = new String[]{"N", "N", "N", "N"};
            }

            CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());

            String begstr = null;

            if (cpvo.getBegindate().getYear() == queryParamvo.getBegindate1().getYear()) {//和建账日期对比
                begstr = DateUtils.getPeriod(cpvo.getBegindate()) + "-01";//从一月份开始查询
            } else {
                begstr = queryParamvo.getBegindate1().getYear() + "-01" + "-01";
            }

            String endstr = queryParamvo.getBegindate1().getYear() + "-12" + "-01";//从一月份开始查询 ;

            listbvos = gl_rep_zcfzserv.getZcfzVOs(new DZFDate(begstr), new DZFDate(endstr),
                    queryParamvo.getPk_corp(), ishajz, yes, null);

            periods = ReportUtil.getPeriods(new DZFDate(begstr), new DZFDate(endstr));

            for (int i = strs.length - listbvos.size(); i < 12; i++) {
                titlename.add(strs[i]);
            }
        } else {
            listbvos.add(listVo);//多页签导出集合
            titlename.add("资产负债表");
            periods.add(qj);
        }

        zcfz.setPeriods(periods.toArray(new String[0]));
        zcfz.setAllsheetname(titlename.toArray(new String[0]));
        zcfz.setZcfzvos(listVo);
        zcfz.setAllsheetzcvos(listbvos);
        zcfz.setQj(qj);
        zcfz.setCreator(userVO.getUser_name());
        zcfz.setCorpName(gs);
        return zcfz;
    }


    /**
     * @return []
     * @Author gzx
     * @Description 英文报表导出
     * @Param void
     */
    @PostMapping("export/excelEn")
    public void excelReportEn(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryParamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryParamvo.getPk_corp()}, null);
        ZcFzBVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), ZcFzBVO[].class);
        String qj = excelExportVO.getPeriod();
        String gs = excelExportVO.getCorpName();
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());
        queryParamvo.setBegindate1(new DZFDate(qj.substring(0, 7) + "-01"));
        List<ZcFzBVO[]> listZcfzBvos = getListZcfzBvos(excelExportVO, queryParamvo, listVo, gs, qj, cpvo);
        List<LrbVO[]> listLrbBvos = getListLrbBvos(excelExportVO, queryParamvo, qj, cpvo);

        LrbTaxVo[] lrbtaxvos = (LrbTaxVo[]) singleObjectBO.queryByCondition(LrbTaxVo.class, "nvl(dr,0)=0 and area_type = '1314'", new SQLParameter());

        Map<String, String> taxvo = new HashMap();
        for (LrbTaxVo lrbTaxVo : lrbtaxvos) {
            String vname = lrbTaxVo.getVname();
            if (!StringUtil.isEmpty(vname)) {
                String[] vanmeArr = vname.split("_");
                taxvo.put(vanmeArr[0].toLowerCase(), vanmeArr[1]);
            }
        }

        TaxEnHander taxHander = new TaxEnHander();
        taxHander.setPeriod(qj);
        Map<String, Workbook> workbookMap = taxHander.handle(listZcfzBvos, taxvo, cpvo.unitname, listLrbBvos);

        exportExcelToZip(response, workbookMap, "资产负债表、利润表(" + qj + ")");

        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "财务报表英文模式导出:" + qj, ISysConstants.SYS_2);
    }

    private List<LrbVO[]> getListLrbBvos(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryParamvo, String qj, CorpVO cpvo) {
        List<LrbVO[]> lrbvos = new ArrayList<LrbVO[]>();
        String excelsel = excelExportVO.getExcelsel();
        // 按照年来查询
        String year = queryParamvo.getBegindate1().getYear() + "";

        Map<String, List<LrbVO>> mapvalues = gl_rep_lrbserv.getYearLrbMap(year, queryParamvo.getPk_corp(),
                queryParamvo.getXmmcid(), null, queryParamvo.getIshasjz());
        List<String> periods = new ArrayList<String>();
        String begstr = queryParamvo.getBegindate1().toString();
        String endstr = queryParamvo.getBegindate1().toString();
        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {
            if (cpvo.getBegindate().getYear() == queryParamvo.getBegindate1().getYear()) {// 和建账日期对比
                begstr = DateUtils.getPeriod(cpvo.getBegindate()) + "-01";// 从一月份开始查询
            } else {
                begstr = queryParamvo.getBegindate1().getYear() + "-01" + "-01";
            }

            endstr = queryParamvo.getBegindate1().getYear() + "-12" + "-01";// 从一月份开始查询
        }

        periods = ReportUtil.getPeriods(new DZFDate(begstr), new DZFDate(endstr));

        for (String period : periods) {
            if (mapvalues.get(period) != null && mapvalues.get(period).size() > 0) {
                lrbvos.add(mapvalues.get(period).toArray(new LrbVO[0]));
            }
        }


        return lrbvos;
    }

    private List<ZcFzBVO[]> getListZcfzBvos(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryParamvo, ZcFzBVO[] listVo, String gs, String qj, CorpVO cpvo) {
        List<ZcFzBVO[]> listZcfzBvos = new ArrayList<>();
        String excelsel = excelExportVO.getExcelsel();
        if (!StringUtil.isEmpty(excelsel) && "1".equals(excelsel)) {
            String ishajz = "N";
            if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }

            String ishasye = queryParamvo.getIshasye();
            String hasye1 = queryParamvo.getHasye1();
            String hasye2 = queryParamvo.getHasye2();
            String hasye3 = queryParamvo.getHasye3();
            if (ishasye == null || "".equals(ishasye)) {
                ishasye = "N";
            }
            String[] yes = null;

            if ("Y".equals(ishasye)) {
                yes = new String[]{ishasye, hasye1, hasye2, hasye3};
            } else {
                yes = new String[]{"N", "N", "N", "N"};
            }

            String begstr = null;

            if (cpvo.getBegindate().getYear() == queryParamvo.getBegindate1().getYear()) {//和建账日期对比
                begstr = DateUtils.getPeriod(cpvo.getBegindate()) + "-01";//从一月份开始查询
            } else {
                begstr = queryParamvo.getBegindate1().getYear() + "-01" + "-01";
            }

            String endstr = queryParamvo.getBegindate1().getYear() + "-12" + "-01";//从一月份开始查询 ;

            listZcfzBvos = gl_rep_zcfzserv.getZcfzVOs(new DZFDate(begstr), new DZFDate(endstr),
                    queryParamvo.getPk_corp(), ishajz, yes, null);
        } else {
            listZcfzBvos.add(listVo);
        }
        return listZcfzBvos;
    }


    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()}, null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String type = printParamVO.getType();
            String font = printParamVO.getFont();
            printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", "元");
            QueryParamVO paramvo = new QueryParamVO();
            paramvo.setPk_corp(corpVO.getPk_corp());
            List<CorpTaxVo> listVos = zxkjPlatformService.queryTaxVoByParam(paramvo, userVO);
            if (listVos != null && listVos.size() > 0) {
                Optional<CorpTaxVo> optional = listVos.stream().filter(v -> corpVO.getPk_corp().equals(v.getPk_corp())).findFirst();
                optional.ifPresent(corpTaxVo -> {
                    if (!StringUtil.isEmpty(corpTaxVo.getLegalbodycode())) {
                        pmap.put("单位负责人", corpTaxVo.getLegalbodycode());
                    }
                    if (!StringUtil.isEmpty(corpTaxVo.getLinkman1())) {
                        pmap.put("财务负责人", corpTaxVo.getLinkman1());
                    }
                    pmap.put("制表人", userVO.getUser_name());
                });
            }

            printReporUtil.setLineheight(-1f);//设置行高
            printReporUtil.setFirstlineheight(20f);

            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setBf_Bold(printReporUtil.getBf());
            printReporUtil.setBasecolor(new BaseColor(0, 0, 0));//设置单元格线颜色
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = getPrintXm(0);
            printReporUtil.printHz(getZcfzMap(queryparamvo,zxkjPlatformService, gl_rep_zcfzserv), null, "资 产 负 债 表",
                    (String[]) obj[0], (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
            //日志记录
            writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                    "资产负债打印:" + printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }

    public Map<String, List<SuperVO>> getZcfzMap(QueryParamVO queryParamvo, IZxkjPlatformService zxkjPlatformService,
                                                 IZcFzBReport gl_rep_zcfzserv) {
        Map<String, List<SuperVO>> resmap = new LinkedHashMap<String, List<SuperVO>>();

        String ishajz = "N";
        if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
            ishajz = "Y";
        }

        String ishasye = queryParamvo.getIshasye();
        String hasye1 = queryParamvo.getHasye1();
        String hasye2 = queryParamvo.getHasye2();
        String hasye3 = queryParamvo.getHasye3();
        String hasye4 = queryParamvo.getHasye4();
        if (ishasye == null || "".equals(ishasye)) {
            ishasye = "N";
        }
        String[] yes = null;

        if ("Y".equals(ishasye)) {
            yes = new String[]{ishasye, hasye1, hasye2, hasye3, hasye4};
        } else {
            yes = new String[]{"N", "N", "N", "N", "N"};
        }

        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());

        String begstr = null;

        begstr = queryParamvo.getBegindate1().toString();

        String endstr = queryParamvo.getEnddate().toString();// 从一月份开始查询

        String begstr1 = "";
        if (cpvo.getBegindate().getYear() == queryParamvo.getBegindate1().getYear()) {//和建账日期对比
            begstr1 = DateUtils.getPeriod(cpvo.getBegindate()) + "-01";//从一月份开始查询
        } else {
            begstr1 = queryParamvo.getBegindate1().getYear() + "-01" + "-01";
        }

        List<ZcFzBVO[]> listbvos = gl_rep_zcfzserv.getZcfzVOs(new DZFDate(begstr1), new DZFDate(endstr), queryParamvo.getPk_corp(),
                ishajz, yes, null);

        if (listbvos != null && listbvos.size() > 0) {
            List<SuperVO> tlist = null;
            for (int i = 0; i < listbvos.size(); i++) {
                tlist = new ArrayList<SuperVO>();
                for (ZcFzBVO bvo_t : listbvos.get(i)) {
                    tlist.add(bvo_t);
                }
                if (begstr.substring(0, 7).compareTo(listbvos.get(i)[0].getPeriod()) <= 0) {
                    resmap.put(DateUtils.getPeriodEndDate(listbvos.get(i)[0].getPeriod()).toString(), tlist);
                }
            }
        }

        return resmap;
    }

    public Object[] getPrintXm(int type) {
        Object[] obj = new Object[4];
        switch (type) {
            case 0:
                obj[0] = new String[]{"zc", "hc1", "qmye1", "ncye1", "fzhsyzqy", "hc2", "qmye2", "ncye2"};
                obj[1] = new String[]{"资      产", "行次", "期末余额", "年初余额", "负债和所有者权益", "行次", "期末余额", "年初余额"};
                obj[2] = new int[]{5, 1, 3, 3, 5, 1, 3, 3};
                obj[3] = 20;
                break;
            default:
                break;
        }
        return obj;
    }


}
