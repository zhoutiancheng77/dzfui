package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.excel.rptexp.*;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

public class ExcelExportNeiMengGuHander extends ExcelExportHander implements OneWorkBookKj2007Excel, MoreWorkBookKj2013Excel {
    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NEIMENGGU, ResourceUtil.ResourceEnum.KJ2007ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(0);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 8, new Integer[]{2, 3, 4, 6, 7, 8},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        Row row = sheet.getRow(4);
        row.getCell(0).setCellValue(getNsrsbh());
        row.getCell(1).setCellValue(getNsrmc());
        row.getCell(2).setCellValue(getEndQj());
        row.getCell(3).setCellValue(getBeginQj());
        row.getCell(4).setCellValue(getEndQj());

        //利润表
        sheet = workbook.getSheetAt(1);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 4, new Integer[]{1,2,3}, new String[]{"bnljje","lastyear_bnljje"});
        //现金流量表
        sheet = workbook.getSheetAt(2);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{2,3,4}, new String[]{"sqje","sqje_last"});
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{1,3,4}, new String[]{"sqje","sqje_last"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap, String versionno) throws Exception {
        return null;
    }

    //old-单文件（多表）
    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NEIMENGGU, ResourceUtil.ResourceEnum.KJ2013ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(0);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 8, new Integer[]{2, 3, 4, 6, 7, 8},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        Row row = sheet.getRow(4);
        row.getCell(0).setCellValue(getNsrsbh());
        row.getCell(1).setCellValue(getNsrmc());
        row.getCell(2).setCellValue(getEndQj());
        row.getCell(3).setCellValue(getBeginQj());
        row.getCell(4).setCellValue(getEndQj());
        //利润表
        sheet = workbook.getSheetAt(1);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 4, new Integer[]{1,2,3}, new String[]{"byje","bnljje"});
        //现金流量表
        sheet = workbook.getSheetAt(2);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{2,3,4}, new String[]{"bqje","sqje"});
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{1,3,4}, new String[]{"bqje","sqje"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookZcfzKj2013(Map<String, ZcFzBVO> zcFzBVOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NEIMENGGU, ResourceUtil.ResourceEnum.KJ2013ZCFZ);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        putValue(sheet, "B3", getNsrsbh()); //B3 - TaxNo
        putValue(sheet, "E3", getNsrmc()); //E3 - CorpName
        putValue(sheet, "B4", getCurrDate("yyyy-M-d")); //B4 - tbrq
        putValue(sheet, "E4", getBeginDate()); //E4 - BeginDate
        putValue(sheet, "H4", getEndDate()); //H4 - EndDate
        handleZcfzbSheet(sheet, taxaxVoMap, zcFzBVOMap, 6, new Integer[]{0,2,3,4,6,7},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookLrbKj2013(Map<String, LrbVO> lrbVOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NEIMENGGU, ResourceUtil.ResourceEnum.KJ2013LR);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleLrbSheet(sheet, taxaxVoMap, lrbVOMap, 3, new Integer[]{0,2,3}, new String[]{"byje","bnljje"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookXjllKj2013(Map<String, XjllbVO> xjllbVOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NEIMENGGU, ResourceUtil.ResourceEnum.KJ2013XJLL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        putValue(sheet, "A3", "纳税人识别号：" + getNsrsbh()); //A3 - "纳税人识别号：" + TaxNo
        putValue(sheet, "A4", "纳税人名称：" + getNsrmc()); //A4 - "纳税人名称：" + CorpName
        handleXjllSheet(sheet, taxaxVoMap, xjllbVOMap, 5, new Integer[]{0,2,3}, new String[]{"bqje","sqje"});
        return workbook;
    }
}
