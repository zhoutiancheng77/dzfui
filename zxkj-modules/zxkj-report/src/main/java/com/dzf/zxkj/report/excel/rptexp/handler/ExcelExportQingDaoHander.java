package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.OneWorkBookKj2007Excel;
import com.dzf.zxkj.report.excel.rptexp.OneWorkBookKj2013Excel;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

public class ExcelExportQingDaoHander extends ExcelExportHander implements OneWorkBookKj2007Excel, OneWorkBookKj2013Excel {
    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception{
//        Resource resource = ResourceUtil.get(ExportTemplateEnum.QINGDAO, ResourceUtil.ResourceEnum.KJ2007ALL);
//        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
//        //资产负债表
//        Sheet sheet = workbook.getSheetAt(1);
//        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 2, new Integer[]{2, 3, 4, 5, 6, 7}, new String[]{"qmye1","ncye1","qmye2","ncye2"});
//        //利润表
//        sheet = workbook.getSheetAt(2);
//        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 2, new Integer[]{2,3,4}, new String[]{"bnljje","lastyear_bnljje"});
//        //现金流量表
//        sheet = workbook.getSheetAt(3);
//        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 2, new Integer[]{2,3,4}, new String[]{"sqje","sqje_last"});
//        return workbook;
        // 默认走20196号，不走老版了
        return createWorkBookKj2007(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, lrbVOMap, xjllbVOMap, zcFzBVOMap, "20196");
    }

    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap, String versionno) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.QINGDAO, ResourceUtil.ResourceEnum.KJ2007ALL,versionno);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        // 资产负债表
        Sheet sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 6, new Integer[] { 1, 3, 4, 5, 7, 8},
                new String[] { "qmye1", "ncye1", "qmye2", "ncye2" });
        // 利润表
        sheet = workbook.getSheetAt(2);
        handleLrbSheet(sheet, lrbTaxVoMap, lrbVOMap, 5, new Integer[] { 1, 2, 3 },
                new String[] { "bnljje", "lastyear_bnljje" });
        // 现金流量表
        sheet = workbook.getSheetAt(3);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 6, new Integer[] { 1, 2, 3 },
                new String[] { "sqje", "sqje_last" });
        return workbook;
    }

    @Override
    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception{
        Resource resource = ResourceUtil.get(ExportTemplateEnum.QINGDAO, ResourceUtil.ResourceEnum.KJ2013ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());

        //新格式：公共信息页没有资产负债表日，报表编码为CWBB_XQYKJZZ

        String nsrsbh = getNsrsbh();
        String nsrmc = getNsrmc();
        DZFDate sDate = new DZFDate(getInnerBeginDate()); //税款所属期起
        DZFDate eDate = new DZFDate(getInnerEndDate()); //税款所属期止
        //String bbrq = getEndDate("yyyy年MM月dd日"); //资产负债表日

        //北京、青岛、广东、陕西、大连、青海等使用通用表头格式

        //直接修改《公共信息表》页单元格即可
        Sheet sheet = workbook.getSheet("公共信息表");
        putValue(sheet, "B5", nsrsbh); //B5 - TaxNo
        putValue(sheet, "B6", nsrmc); //B6 - CorpName
        //财报所属期起、止
        putValue(sheet, "B7", sDate.getYear()); //B7 - BeginDate.Year
        putValue(sheet, "D7", sDate.getMonth()); //D7 - BeginDate.Month
        putValue(sheet, "F7", sDate.getDay()); //F7 - BeginDate.Day
        putValue(sheet, "B8", eDate.getYear()); //B8 - EndDate.Year
        putValue(sheet, "D8", eDate.getMonth()); //D8 - EndDate.Month
        putValue(sheet, "F8", eDate.getDay()); //F8 - EndDate.Day

        //资产负债表
        sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 5, new Integer[]{1, 3, 4, 5, 7, 8},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        //利润表
        sheet = workbook.getSheetAt(2);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 4, new Integer[]{1,3,4}, new String[]{"bnljje","byje"}); //改为本年累计、本月
        //现金流量表
        sheet = workbook.getSheetAt(3);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 5, new Integer[]{1,3,4}, new String[]{"sqje","bqje"}); //改为本年累计、本月

        //region old（旧格式：公共信息页有资产负债表日，报表编码为CWBB_XQY_KJZZ）
        /*
        //String tbrq = getCurrDate("yyyy-M-d"); //报税日期取当前系统日期？

        //直接修改《公共信息表》页单元格即可
        Sheet sheet = workbook.getSheet("公共信息表");
        putValue(sheet, "B5", nsrsbh); //B5 - TaxNo
        putValue(sheet, "B6", nsrmc); //B6 - CorpName
        //财报所属期起、止
        putValue(sheet, "B7", sDate.getYear()); //B7 - BeginDate.Year
        putValue(sheet, "D7", sDate.getMonth()); //D7 - BeginDate.Month
        putValue(sheet, "F7", sDate.getDay()); //F7 - BeginDate.Day
        putValue(sheet, "B8", eDate.getYear()); //B8 - EndDate.Year
        putValue(sheet, "D8", eDate.getMonth()); //D8 - EndDate.Month
        putValue(sheet, "F8", eDate.getDay()); //F8 - EndDate.Day
        putValue(sheet, "B9", eDate.getYear()); //B9 - EndDate.Year
        putValue(sheet, "D9", eDate.getMonth()); //D9 - EndDate.Month
        putValue(sheet, "F9", eDate.getDay()); //F9 - EndDate.Day

        //资产负债表
        sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 3, new Integer[]{2, 4, 5, 6, 9, 10},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        //利润表
        sheet = workbook.getSheetAt(2);
        putValue(sheet, "F3", tbrq); //F3 - tbrq
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 3, new Integer[]{2,7,8},new String[]{"byje","bnljje"}); //本月、本年累计
        //现金流量表
        sheet = workbook.getSheetAt(3);
        putValue(sheet, "F3", tbrq); //F3 - tbrq
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 3, new Integer[]{2,7,9}, new String[]{"bqje","sqje"}); //本月、本年累计
        */
        //endregion

        //北京、青岛、广东、陕西、大连、青海等有公共信息表的，需要全表重算
        workbook.setForceFormulaRecalculation(true);
        return workbook;
    }
}
