package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.ReportPrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.ExMultiVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmReportDatagridColumn;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.print.cwzb.MultiColumnPdfField;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnReport;
import com.dzf.zxkj.report.utils.ExcelReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.itextpdf.text.BaseColor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("gl_rep_multiserv")
@Slf4j
public class MultiColumnController extends ReportBaseController {

    @Autowired
    private IMultiColumnReport gl_rep_multiserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    private String[] getFrozenColumns() {
        return new String[]{"rq", "pzh", "zy", "jf", "df", "fx", "ye", "pk_accsubj", "pk_tzpz_h"};
    }

    /**
     * 查询科目明细数据
     */
    @PostMapping("/query")
    public ReturnData<Grid> queryAction(@MultiRequestBody KmReoprtQueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        KmReoprtQueryParamVO vo = getQueryParamVO(queryvo, corpVO);
        try {
            List<KmReportDatagridColumn> columnList = new ArrayList<KmReportDatagridColumn>();
            List<KmReportDatagridColumn> columnList2 = new ArrayList<KmReportDatagridColumn>();
            /** 先动态生成column数据 */
            /** 开始日期应该在建账日期前 */
            checkPowerDate(vo, corpVO);
            /** 是否显示当年的本年累计 */
            vo.setBtotalyear(DZFBoolean.TRUE);
            /** 动态的列数 */
            Object[] objs = gl_rep_multiserv.getMulColumns(vo);

            ExMultiVO[] mulresvos = (ExMultiVO[]) objs[0];

            if (mulresvos != null && mulresvos.length > 0) {
                List<String> columnlist = (ArrayList<String>) objs[1];
                int len = columnlist == null ? 0 : columnlist.size();

                SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
                SQLParameter sp = new SQLParameter();
                sp.addParam(vo.getPk_corp());
                YntCpaccountVO[] cpvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " nvl(dr,0)=0 and pk_corp = ? ", sp);//AccountCache.getInstance().get("", vo.getPk_corp());
                Map<String, YntCpaccountVO> kmbmmap = new HashMap<String, YntCpaccountVO>();
                for (YntCpaccountVO cpvo : cpvos) {
                    kmbmmap.put(cpvo.getAccountcode(), cpvo);
                }
                YntCpaccountVO currvo = kmbmmap.get(vo.getKms_first());
                /** 循环，根据科目确定借/贷方 */
                if (len > 0) {
                    KmReportDatagridColumn dc2 = new KmReportDatagridColumn();
                    dc2.setTitle("余额");
                    dc2.setField("ye");
                    ;
                    dc2.setWidth(150);
                    dc2.setRowspan(2);
                    dc2.setAlign("right");
                    dc2.setHalign("center");
                    columnList.add(dc2);

                    KmReportDatagridColumn dcjf = null;

                    KmReportDatagridColumn dcdf = null;

                    Integer jfbfint = 0;
                    Integer dfbfint = 0;
                    List<String> frozenlist = Arrays.asList(getFrozenColumns());
                    Set<String> kmbmlist = new HashSet<String>();
                    for (String keyvalue : columnlist) {
                        if (!frozenlist.contains(keyvalue)) {
                            if (!kmbmlist.contains(keyvalue)) {
                                kmbmlist.add(keyvalue);
                                YntCpaccountVO rescpavo = kmbmmap.get(keyvalue.split("_")[0]);
                                if ((rescpavo != null && rescpavo.getDirection() == 0) || (
                                        !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 0
                                )) {
                                    if (dcjf == null) {
                                        dcjf = new KmReportDatagridColumn();
                                    }
                                    dcjf.setTitle("借方");
                                    jfbfint = dcjf.getColspan() == null ? 0 : dcjf.getColspan();
                                    dcjf.setColspan(jfbfint.intValue() + 1);
                                } else if ((rescpavo != null && rescpavo.getDirection() == 1) || (
                                        !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 1
                                )) {
                                    if (dcdf == null) {
                                        dcdf = new KmReportDatagridColumn();
                                    }
                                    dcdf.setTitle("贷方");
                                    dfbfint = dcdf.getColspan() == null ? 0 : dcdf.getColspan();
                                    dcdf.setColspan(dfbfint.intValue() + 1);
                                }
                            }
                        }
                    }
                    if (dcjf != null) {
                        columnList.add(dcjf);
                    }

                    if (dcdf != null) {
                        columnList.add(dcdf);
                    }
                }
                String key;
                KmReportDatagridColumn dc = null;
                /** 获取属性的名字 */
                String[] strs = null;
                /** 分组，借方在上，贷方在下 */
                List<KmReportDatagridColumn> jfcolumnlist = new ArrayList<KmReportDatagridColumn>();
                List<KmReportDatagridColumn> dfcolumnlist = new ArrayList<KmReportDatagridColumn>();
                for (int i = 0; i < len; i++) {
                    key = columnlist.get(i);
                    dc = new KmReportDatagridColumn();
                    YntCpaccountVO rescpavo = kmbmmap.get(key.split("_")[0]);
                    /** 获取属性的名字 */
                    strs = key.split("_");
                    dc.setField(strs[0]);
                    dc.setTitle(strs[1]);
                    dc.setHalign("center");
                    dc.setAlign("right");
                    dc.setWidth(100);
                    if ((rescpavo != null && rescpavo.getDirection() == 0) || (
                            !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 0
                    )) {
                        jfcolumnlist.add(dc);
                    } else if ((rescpavo != null && rescpavo.getDirection() == 1) || (
                            !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 1
                    )) {
                        dfcolumnlist.add(dc);
                    }
                }

                for (KmReportDatagridColumn dctemp : jfcolumnlist) {
                    columnList2.add(dctemp);
                }

                for (KmReportDatagridColumn dctemp : dfcolumnlist) {
                    columnList2.add(dctemp);
                }

                HashMap<String, Object> map = null;
                List<Map<String, Object>> resultData = new ArrayList<>();
                /** 这里是重点 */
                List<ExMultiVO> loanList = new ArrayList<ExMultiVO>();
                if (mulresvos != null && mulresvos.length > 0) {
                    int i = 0;
                    for (ExMultiVO votemp : mulresvos) {
                        votemp.setPk_currency(vo.getPk_currency());
                        resultData.add(votemp.getHash());
                    }
                }
                grid.setRows(resultData);
                grid.setColumns(columnList);
                grid.setColumnlist2(columnList2);
                grid.setSuccess(true);
                grid.setMsg("查询成功!");

            } else {
                grid.setColumns(columnList);
                grid.setColumnlist2(columnList2);
                grid.setSuccess(false);
                grid.setRows(new ArrayList<ExMultiVO>());
                grid.setMsg("查询数据为空!");
            }
        } catch (Exception e) {
            grid.setRows(new ArrayList<KmMxZVO>());
            printErrorLog(grid, e, "查询失败!");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "多栏账查询:"+vo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ vo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助项目参照
     */
    @GetMapping("queryFzLb")
    public ReturnData<Grid> getFzLb(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountHVO[] bvos = zxkjPlatformService.queryHByPkCorp(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助类别查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助项目参照
     */
    @GetMapping("queryFzxm")
    public ReturnData<Grid> getFzxm(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryAllB(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助项目查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助项目查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 序列化成Json字符串
     *
     * @return
     * @throws Exception
     */
    private static String toJson(String str, Map<String, Object> map, int i) {
        str = str + "{";
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                str = str + "\"" + key + "\"" + ":\"" + map.get(key).toString().replaceAll("\n", "") + "\",";
            } else {
                str = str + "\"" + key + "\"" + ":\"" + map.get(key) + "\",";
            }
        }
        str = str + "},";
        return str;
    }

    private KmReoprtQueryParamVO getQueryParamVO(KmReoprtQueryParamVO paramvo, CorpVO corpVO) {
        paramvo.setXsyljfs(DZFBoolean.TRUE);
        paramvo.setXswyewfs(DZFBoolean.TRUE);
        paramvo.setIshasjz(DZFBoolean.FALSE);
        paramvo.setPk_corp(paramvo.getCorpIds1());
        paramvo.setBtotalyear(DZFBoolean.TRUE);
        paramvo = (KmReoprtQueryParamVO) super.getQueryParamVO(paramvo, corpVO);
        return paramvo;
    }

    @PostMapping("print/pdf")
    public void print(@RequestParam Map<String, String> pmap1, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        try {
            ReportPrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), ReportPrintParamVO.class);
            MultiColumnPdfField columnPdfField = JsonUtils.deserialize(JsonUtils.serialize(pmap1), MultiColumnPdfField.class);

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

            Map<String, String> pmap = new HashMap<String, String>();
            pmap.put("type", printParamVO.getType());
            pmap.put("pageOrt", printParamVO.getPageOrt());
            pmap.put("left", printParamVO.getLeft());
            pmap.put("top", printParamVO.getTop());
            pmap.put("printdate", printParamVO.getPrintdate());
            pmap.put("font", printParamVO.getFont());
            pmap.put("period", columnPdfField.getPeriod());
            pmap.put("gs", columnPdfField.getCorpName());

            /** 是否横向 */
            if ("Y".equals(printParamVO.getPageOrt())) {
                printReporUtil.setIscross(DZFBoolean.TRUE);
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);
            }

            List<String> headslist = columnPdfField.getHeadList();
            List<String> fieldslist = columnPdfField.getFieldList();
            int[] widths = columnPdfField.getWidths();

            String[] fields = fieldslist.toArray(new String[fieldslist.size()]);
            ColumnCellAttr[] columncellattrvos = JsonUtils.deserialize(columnPdfField.getColumns(), ColumnCellAttr[].class);

            printReporUtil.setBasecolor(new BaseColor(167, 167, 167));

            List<Map<String, String>> data = JsonUtils.deserialize(columnPdfField.getData(), List.class, Map.class);

            printReporUtil.printMultiColumn(data, columnPdfField.getTitle() + "多栏账", headslist, fields, widths, 20, Arrays.asList(columncellattrvos), pmap, response);
        } catch (IOException e) {
            log.error("打印失败", e);
        }
    }

    @PostMapping("export/excel")
    public void export(@MultiRequestBody ReportExcelExportVO exportVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        String data = exportVO.getData();

        List<Map<String, String>> d = JsonUtils.deserialize(data, List.class, Map.class);
        ColumnCellAttr[] columncellattrvos = JsonUtils.deserialize(exportVO.getColumncellattrvos(), ColumnCellAttr[].class);

        String titlename = exportVO.getTitleName();
        String period = exportVO.getPeriod();
        String gs = exportVO.getCorpName();
        ExcelReport ex = new ExcelReport();
        List<String> headslist = new ArrayList<String>();
        List<String> fieldslist = new ArrayList<String>();

        Set<String> groupkey = new HashSet<String>();
        for (ColumnCellAttr attr : columncellattrvos) {
            if (attr.getColumname().indexOf("_") > 0) {
                if (!groupkey.contains(attr.getColumname().split("_")[0])) {
                    groupkey.add(attr.getColumname().split("_")[0]);
                    headslist.add(attr.getColumname().split("_")[0]);
                }
            }
            headslist.add(attr.getColumname());
            fieldslist.add(attr.getColumn());
        }
        OutputStream toClient = null;
        try {
            response.reset();
            String date = DateUtils.getDate(new Date());
            String fileName = "多栏账_" + date + ".xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = ex.exportExcel(titlename + "多栏账", headslist, fieldslist, d, toClient, "", gs, period);
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
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
        }
    }

}
