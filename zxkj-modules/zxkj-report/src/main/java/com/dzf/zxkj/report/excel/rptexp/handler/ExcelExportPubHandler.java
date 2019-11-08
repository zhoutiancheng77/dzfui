package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.report.excel.rptexp.CommonExcelProcess;
import com.dzf.zxkj.report.excel.rptexp.CwbbType;
import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExcelExportPubHandler extends ExcelExportHander implements CommonExcelProcess {
    /**
     * 创建单张报表（多报表文件）
     *
     * @param cwbbType
     * @param vOMap
     * @param taxVoMap
     * @return
     * @throws Exception
     */
    @Override
    public Workbook createOneRptBook(CwbbType cwbbType, Map<String, SuperVO> vOMap, Map<String, String> taxVoMap) throws Exception {
        if (taxVoMap.isEmpty()) //没有定义字段映射的不创建
            return null;
        String areaType = getAreaType();
        String corpType = getCorpType();
        Resource resource = ResourceUtil.get(areaType, corpType, cwbbType);
        if (resource == null) //没有定义模板的不创建
            return null;
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); //单张表的Excel，报表一般是第1个sheet

        int rowBegin = 4; //多数报表的表格数据区域都是从4行及以后开始的
        //准备colnos、fields
        ColMapInfo info = getColumnMappingInfo(areaType, corpType, cwbbType);
        if (info == null) {
            log.error("导出财报时未设置列映射规则！");
            return null;
        }

        processHeaderFooter(sheet, areaType, corpType, cwbbType);
        //填写报表
        handleSheet(sheet, taxVoMap, vOMap, rowBegin, info.colnos, info.fields);
        return workbook;
    }

    /**
     * 创建完整财务报表（单报表文件）
     *
     * @param lrbTaxVoMap
     * @param zcfzTaxVoMap
     * @param xjllTaxVoMap
     * @param lrbVOMap
     * @param xjllbVOMap
     * @param zcfzbVOMap
     * @return
     * @throws Exception
     */
    @Override
    public Workbook createFullRptBook(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, SuperVO> zcfzbVOMap, Map<String, SuperVO> lrbVOMap, Map<String, SuperVO> xjllbVOMap) throws Exception {
        String areaType = getAreaType();
        String corpType = getCorpType();
        Resource resource = ResourceUtil.get(areaType, corpType, CwbbType.EMPTY);
        if (resource == null) //没有定义模板的不创建
            return null;
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());

        //资产负债表、利润表、现金流量表
        CwbbType[] cwbbTypes = new CwbbType[]{CwbbType.ZCFZB, CwbbType.LRB, CwbbType.XJLLB};
        HashMap[] taxVoMaps = new HashMap[]{(HashMap) zcfzTaxVoMap, (HashMap) lrbTaxVoMap, (HashMap) xjllTaxVoMap};
        HashMap[] voMaps = new HashMap[]{(HashMap) zcfzbVOMap, (HashMap) lrbVOMap, (HashMap) xjllbVOMap};

        Sheet sheet;
        boolean isPubInfoPage = areaType.equals("26") || areaType.equals("28") || areaType.equals("32");
        ColMapInfo info;
        HashMap taxVoMap;
        int rowBegin = 4; //多数报表的表格数据区域都是从4行及以后开始的
        for (int i = 0; i < 3; i++) {
            sheet = getCwbbSheet(workbook, cwbbTypes[i]);
            taxVoMap = taxVoMaps[i];
            if (sheet != null && !taxVoMap.isEmpty()) { //有表样和字段映射的才填写
                //准备colnos、fields
                info = getColumnMappingInfo(areaType, corpType, cwbbTypes[i]);
                if (info == null) {
                    log.error("导出财报时未设置列映射规则！");
                    return null;
                }

                if (!isPubInfoPage || i == 0) { //陕西、大连、青海等有公共信息表的，只需设置一次表头
                    processHeaderFooter(sheet, areaType, corpType, cwbbTypes[i]);
                }
                //填写报表
                handleSheet(sheet, taxVoMap, voMaps[i], rowBegin, info.colnos, info.fields);
            }
        }
        if (isPubInfoPage) //陕西、大连、青海等有公共信息表的，需要全表重算
            workbook.setForceFormulaRecalculation(true);

        return workbook;
    }

    /**
     * 处理表头、表尾字段（静态单元格映射）
     * TODO: 可以考虑把表头表尾字段放到静态字段映射表中（表达式支持宏定义和对象调用）。这样当维护报表的表头表尾项时不需要写代码，只需增加字段映射
     *
     * @param sheet
     * @param areaType
     * @param corpType
     * @param cwbbType
     */
    private void processHeaderFooter(Sheet sheet, String areaType, String corpType, CwbbType cwbbType) {
        String nsrsbh = getNsrsbh();
        String nsrmc = getNsrmc();
        DZFDate sDate = new DZFDate(getInnerBeginDate()); //税款所属期起
        DZFDate eDate = new DZFDate(getInnerEndDate()); //税款所属期止
        String bbrq = getEndDate("yyyy年MM月dd日"); //资产负债表日

        switch (areaType) {
            case "25": //山西
                if (cwbbType == CwbbType.ZCFZB) {
                    putValue(sheet, "C4", nsrmc); //C4 - CorpName
                    putValue(sheet, "G4", bbrq); //G4 - EndDate
                } else if (cwbbType == CwbbType.LRB) {
                    putValue(sheet, "C4", nsrmc); //C4 - CorpName
                    putValue(sheet, "E4", bbrq); //E4 - EndDate
                } else if (cwbbType == CwbbType.XJLLB) {
                    putValue(sheet, "A3", "编制单位：" + nsrmc); //A3 - "编制单位："+CorpName
                    if (is07zz(corpType)) {
                        String perioddesc = String.format("       日期：%d年%s月%s日 至 %d年%s月%s日", sDate.getYear(), sDate.getStrMonth(), sDate.getStrDay(), eDate.getYear(), eDate.getStrMonth(), eDate.getStrDay());
                        putValue(sheet, "A2", perioddesc); //A2 - "       日期：yyyy年mm月dd日 至 yyyy年mm月dd日"
                    } else {
                        putValue(sheet, "A2", getEndDate("                 报表日期：yyyy年MM月dd日")); //A2 - "                 报表日期：yyyy年mm月dd日"
                    }
                }
                break;
            case "27": //新疆
                putValue(sheet, "B2", nsrsbh); //B2 - TaxNo
                putValue(sheet, "B3", nsrmc); //B3 - CorpName

                String perioddesc = String.format("税款所属期：%d年%s月%s日 至 %d年%s月%s日", sDate.getYear(), sDate.getStrMonth(), sDate.getStrDay(), eDate.getYear(), eDate.getStrMonth(), eDate.getStrDay());
                //String tbrq = String.format("填表日期：%d年%02d月15日", eDate.getYear(), eDate.getMonth() % 12 + 1);
                String tbrq = getCurrDate("填表日期：yyyy年MM月dd日"); //王冬佩：填表日期取当前系统日期
                if (cwbbType == CwbbType.ZCFZB) {
                    putValue(sheet, "H2", perioddesc); //H2 - "税款所属期：2019年04月01日 至 2019年06月30日"
                    putValue(sheet, "J3", tbrq); //J3 - "填表日期：2019年07月15日"
                } else if (cwbbType == CwbbType.LRB) {
                    putValue(sheet, "E2", perioddesc); //E2 - "税款所属期：2019年04月01日 至 2019年06月30日"
                    putValue(sheet, "H3", tbrq); //H3 - "填表日期：2019年07月15日"
                } else if (cwbbType == CwbbType.XJLLB) {
                    putValue(sheet, "C2", perioddesc); //C2 - "税款所属期：2019年04月01日 至 2019年06月30日"
                    putValue(sheet, "E3", tbrq); //E3 - "填表日期：2019年07月15日"
                }
                break;
            case "29": //宁波
                putValue(sheet, "A3", nsrmc); //A3 - CorpName
                if (cwbbType == CwbbType.ZCFZB) {
                    putValue(sheet, "A2", bbrq); //A2 - EndDate
                } else if (cwbbType == CwbbType.LRB) {
                    perioddesc = String.format("%d年%s月%s日～%d年%s月%s日", sDate.getYear(), sDate.getStrMonth(), sDate.getStrDay(), eDate.getYear(), eDate.getStrMonth(), eDate.getStrDay());
                    putValue(sheet, "A2", perioddesc); //暂不写成季度，万一是月报呢 //A2 - "2019年第三季度"
                }
                break;
            case "30": //江西
                putValue(sheet, "A3", "公司：" + nsrmc); //A3 - "公司：" + CorpName
                if (cwbbType == CwbbType.ZCFZB) {
                    putValue(sheet, "C3", "期间：" + getEndDate()); //C3 - "期间：2019-09-30"
                } else if (cwbbType == CwbbType.LRB) {
                    putValue(sheet, "B3", "期间：" + getEndDate("yyyy-MM")); //B3 - "期间：2019-09"
                }
                break;
            case "31": //黑龙江
                putValue(sheet, "A4", "纳税人识别号：" + nsrsbh); //A4 - "纳税人识别号：" + TaxNo
                perioddesc = String.format("税款所属期：%d年%d月%d日 至 %d年%d月%d日", sDate.getYear(), sDate.getMonth(), sDate.getDay(), eDate.getYear(), eDate.getMonth(), eDate.getDay());
                putValue(sheet, "A3", perioddesc); //A3 - "税款所属期：2019年4月1日 至 2019年6月30日"
                break;
            //陕西、大连、青海使用通用表头格式
            case "26":
            case "28":
            case "32":
                //直接修改《公共信息表》页单元格即可
                sheet = sheet.getWorkbook().getSheet("公共信息表");
                putValue(sheet, "B5", nsrsbh); //B5 - TaxNo
                putValue(sheet, "B6", nsrmc); //B6 - CorpName
                putValue(sheet, "B7", sDate.getYear()); //B7 - BeginDate.Year
                putValue(sheet, "D7", sDate.getMonth()); //D7 - BeginDate.Month
                putValue(sheet, "F7", sDate.getDay()); //F7 - BeginDate.Day
                putValue(sheet, "B8", eDate.getYear()); //B8 - EndDate.Year
                putValue(sheet, "D8", eDate.getMonth()); //D8 - EndDate.Month
                putValue(sheet, "F8", eDate.getDay()); //F8 - EndDate.Day
                putValue(sheet, "B9", eDate.getYear()); //B9 - EndDate.Year
                putValue(sheet, "D9", eDate.getMonth()); //D9 - EndDate.Month
                putValue(sheet, "F9", eDate.getDay()); //F9 - EndDate.Day
                break;
        }
    }

    public void putValue(Sheet sheet, String tocell, Object value) {
        //B5 --> R5C2：rowno=4，colno=1
        int rowno = 0, colno = 0;
        char c;
        for (int i = 0; i < tocell.length(); i++) {
            c = tocell.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                colno += colno * 26 + (c - 'A' + 1);
            } else {
                rowno += rowno * 10 + (c - '0');
            }
        }
        rowno--;
        colno--;

        sheet.getRow(rowno).getCell(colno).setCellValue(value != null ? value.toString() : "");
    }

    /**
     * 得到指定地区指定会计准则指定报表的列映射规则（colnos-目标位置，fields-数据来源）
     * TODO: 可以按地区、会计制度、报表等把colnos、fields等列映射规则保存到配置文件或数据库中，就不用写这么多if分支了
     *
     * @param areaType
     * @param corpType
     * @param cwbbType
     * @return
     */
    private ColMapInfo getColumnMappingInfo(String areaType, String corpType, CwbbType cwbbType) {
        Integer[] colnos = null;
        String[] fields = null;
        if (cwbbType == CwbbType.ZCFZB) {          //资产负债表
            if (areaType.equals("25")) { //山西
                colnos = new Integer[]{1, 4, 5, 6, 9, 10};
            } else if (areaType.equals("27")) { //新疆
                colnos = new Integer[]{0, 4, 5, 7, 12, 13};
            } else if (areaType.equals("29") || areaType.equals("30") || areaType.equals("31")) { //宁波、江西、黑龙江
                colnos = new Integer[]{0, 2, 3, 4, 6, 7};
            } else if (areaType.equals("26") || areaType.equals("28") || areaType.equals("32")) { //陕西、大连、青海
                if (is07zz(corpType))
                    colnos = new Integer[]{2, 3, 4, 5, 6, 7};
                else
                    colnos = new Integer[]{2, 4, 5, 6, 9, 10};
            }
            fields = new String[]{"qmye1", "ncye1", "qmye2", "ncye2"};
        } else if (cwbbType == CwbbType.LRB) {     //利润表
            if (areaType.equals("25") || areaType.equals("27")) { //山西、新疆
                if (areaType.equals("25")) { //山西
                    colnos = new Integer[]{1, 4, 5};
                } else if (areaType.equals("27")) { //新疆
                    colnos = new Integer[]{0, 6, 9};
                }
                if (is07zz(corpType)) { //07企业：本期、上期
                    fields = new String[]{"bnljje", "lastyear_bnljje"};
                } else {                //13小企业：本年累计、本月
                    fields = new String[]{"bnljje", "byje"};
                }
            } else if (areaType.equals("29") || areaType.equals("30") || areaType.equals("31")) { //宁波、江西、黑龙江：利润表和现金流量表不区分07企业和13小企业
                colnos = new Integer[]{0, 2, 3};
                if (areaType.equals("31")) //黑龙江是本年累计、本月
                    fields = new String[]{"bnljje", "byje"};
                else //本月、本年累计
                    fields = new String[]{"byje", "bnljje"};
            } else if (areaType.equals("26") || areaType.equals("28") || areaType.equals("32")) { //陕西、大连、青海
                if (is07zz(corpType)) { //07企业：本期、上期
                    colnos = new Integer[]{2, 3, 4};
                    fields = new String[]{"bnljje", "lastyear_bnljje"};
                } else {                //13小企业：本月、本年累计
                    colnos = new Integer[]{2, 7, 8};
                    fields = new String[]{"byje", "bnljje"};
                }
            }
        } else if (cwbbType == CwbbType.XJLLB) {   //现金流量表
            if (areaType.equals("25") || areaType.equals("27")) { //山西、新疆
                if (areaType.equals("25")) { //山西
                    colnos = new Integer[]{0, 2, 3};
                } else if (areaType.equals("27")) { //新疆
                    colnos = new Integer[]{0, 4, 5};
                }
                if (is07zz(corpType)) { //07企业：本期、上期
                    fields = new String[]{"sqje", "sqje_last"};
                } else {                //13小企业：本年累计、本月
                    fields = new String[]{"sqje", "bqje"};
                }
            } else if (areaType.equals("26") || areaType.equals("28") || areaType.equals("32")) { //陕西、大连、青海
                if (is07zz(corpType)) { //07企业：本期、上期
                    colnos = new Integer[]{2, 3, 4};
                    fields = new String[]{"sqje", "sqje_last"};
                } else {                //13小企业：本月、本年累计
                    colnos = new Integer[]{2, 7, 9};
                    fields = new String[]{"bqje", "sqje"};
                }
            }
        }

        if (colnos == null || colnos.length == 0 || fields == null || fields.length == 0)
            return null;
        return new ColMapInfo(colnos, fields);
    }

    /**
     * 是否 07一般企业准则
     *
     * @param corpType
     * @return
     */
    public static boolean is07zz(String corpType) {
        return corpType.equals("00000100AA10000000000BMF");
    }

    public static Sheet getCwbbSheet(Workbook workbook, CwbbType cwbbType) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().contains(cwbbType.getName()))
                return sheet;
        }
        return null;
    }

    private class ColMapInfo {
        public Integer[] colnos;
        public String[] fields;

        public ColMapInfo(Integer[] colnos, String[] fields) {
            this.colnos = colnos;
            this.fields = fields;
        }
    }
}
