package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.MoreWorkBookKj2013Excel;
import com.dzf.zxkj.report.excel.rptexp.OneWorkBookKj2007Excel;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

public class ExcelExportNingXiaHandler extends ExcelExportHander implements OneWorkBookKj2007Excel, MoreWorkBookKj2013Excel {

    @Override
    public Workbook createWorkBookZcfzKj2013(Map<String, ZcFzBVO> vOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NINGXIA, ResourceUtil.ResourceEnum.KJ2013ZCFZ);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleZcfzbSheet(sheet, taxaxVoMap, vOMap, 7, new Integer[]{0,2,3,4,6,7},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookLrbKj2013(Map<String, LrbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NINGXIA, ResourceUtil.ResourceEnum.KJ2013LR);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleLrbSheet(sheet, taxaxVoMap, vOMap, 3, new Integer[]{0,2,3}, new String[]{"byje","bnljje"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookXjllKj2013(Map<String, XjllbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.NINGXIA, ResourceUtil.ResourceEnum.KJ2013XJLL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleXjllSheet(sheet, taxaxVoMap, vOMap, 6, new Integer[]{0,2,3}, new String[]{"bqje","sqje"});
        return workbook;
    }



    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.GUANGXI, ResourceUtil.ResourceEnum.KJ2007ALL);
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
}
