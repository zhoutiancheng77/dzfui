package com.dzf.zxkj.report.excel.rptexp.handler;

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

public class ExcelExportGuiZhouHander extends ExcelExportHander implements OneWorkBookKj2007Excel, OneWorkBookKj2013Excel {
    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
//        Resource resource = ResourceUtil.get(ExportTemplateEnum.GUIZHOU, ResourceUtil.ResourceEnum.KJ2007ALL);
//        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
//
//        Sheet sheet = workbook.getSheetAt(0);
//        sheet.getRow(4).getCell(1).setCellValue(getNsrsbh());
//        sheet.getRow(5).getCell(1).setCellValue(getNsrmc());
//
//        sheet.getRow(6).getCell(1).setCellValue(getBeginQj().substring(0,4));
//        sheet.getRow(6).getCell(3).setCellValue(getBeginQj().substring(5,7));
//        sheet.getRow(6).getCell(5).setCellValue(getBeginQj().substring(8,10));
//
//        sheet.getRow(7).getCell(1).setCellValue(getEndQj().substring(0,4));
//        sheet.getRow(7).getCell(3).setCellValue(getEndQj().substring(5,7));
//        sheet.getRow(7).getCell(5).setCellValue(getEndQj().substring(8,10));
//
//        //资产负债表
//        sheet = workbook.getSheetAt(1);
//        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 6, new Integer[]{2, 3, 4, 5, 6, 7}, new String[]{"qmye1", "ncye1", "qmye2", "ncye2"});
//        sheet.getRow(2).getCell(3).setCellValue(getNsrsbh());
//        sheet.getRow(2).getCell(6).setCellValue(getNsrmc());
//        sheet.getRow(3).getCell(5).setCellValue(getBeginQj());
//        sheet.getRow(3).getCell(7).setCellValue(getEndQj());
//
//        //利润表
//        sheet = workbook.getSheetAt(2);
//        handleLrbSheet(sheet, lrbTaxVoMap, lrbVOMap, 5, new Integer[]{2, 3, 4}, new String[]{"bnljje", "lastyear_bnljje"});
//        sheet.getRow(2).getCell(3).setCellValue(getNsrsbh());
//        sheet.getRow(2).getCell(6).setCellValue(getNsrmc());
//        sheet.getRow(3).getCell(3).setCellValue(getEndQj());
//        sheet.getRow(3).getCell(5).setCellValue(getBeginQj());
//        sheet.getRow(3).getCell(7).setCellValue(getEndQj());
//
//        //现金流量表
//        sheet = workbook.getSheetAt(3);
//        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 6, new Integer[]{2, 3, 4}, new String[]{"sqje", "sqje_last"});
//
//        sheet.getRow(2).getCell(3).setCellValue(getNsrsbh());
//        sheet.getRow(2).getCell(6).setCellValue(getNsrmc());
//        sheet.getRow(3).getCell(3).setCellValue(getEndQj());
//        sheet.getRow(3).getCell(5).setCellValue(getBeginQj());
//        sheet.getRow(3).getCell(7).setCellValue(getEndQj());
//        return workbook;
        // 默认走20196号，不走老版了
        return createWorkBookKj2007(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, lrbVOMap, xjllbVOMap, zcFzBVOMap, "20196");
    }

    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap, String versionno) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.BEIJING, ResourceUtil.ResourceEnum.KJ2007ALL,versionno);
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
    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.GUIZHOU, ResourceUtil.ResourceEnum.KJ2013ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        sheet.getRow(4).getCell(1).setCellValue(getNsrsbh());
        sheet.getRow(5).getCell(1).setCellValue(getNsrmc());

        sheet.getRow(6).getCell(1).setCellValue(getBeginQj().substring(0,4));
        sheet.getRow(6).getCell(3).setCellValue(getBeginQj().substring(5,7));
        sheet.getRow(6).getCell(5).setCellValue(getBeginQj().substring(8,10));

        sheet.getRow(7).getCell(1).setCellValue(getEndQj().substring(0,4));
        sheet.getRow(7).getCell(3).setCellValue(getEndQj().substring(5,7));
        sheet.getRow(7).getCell(5).setCellValue(getEndQj().substring(8,10));

        //资产负债表
        sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 5, new Integer[]{2, 4, 5, 6, 9, 10}, new String[]{"qmye1", "ncye1", "qmye2", "ncye2"});
        sheet.getRow(1).getCell(3).setCellValue(getNsrsbh());
        sheet.getRow(2).getCell(3).setCellValue(getNsrmc());
        sheet.getRow(2).getCell(7).setCellValue("税款所属期:"+getBeginQj());
        sheet.getRow(2).getCell(9).setCellValue("至  "+getEndQj());

        //利润表
        sheet = workbook.getSheetAt(2);
        handleLrbSheet(sheet, lrbTaxVoMap, lrbVOMap, 4, new Integer[]{2, 7, 8}, new String[]{"byje", "bnljje"});
        sheet.getRow(1).getCell(3).setCellValue(getNsrsbh());
        sheet.getRow(2).getCell(3).setCellValue(getNsrmc());
        sheet.getRow(2).getCell(5).setCellValue(getEndQj());
        sheet.getRow(2).getCell(7).setCellValue(getBeginQj()+"至"+getEndQj());


        //现金流量表
        sheet = workbook.getSheetAt(3);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 5, new Integer[]{2, 7, 9}, new String[]{"bqje", "sqje"});
        sheet.getRow(1).getCell(3).setCellValue(getNsrsbh());
        sheet.getRow(2).getCell(3).setCellValue(getNsrmc());
        sheet.getRow(2).getCell(5).setCellValue(getEndQj());
        sheet.getRow(2).getCell(7).setCellValue(getBeginQj());
        sheet.getRow(2).getCell(9).setCellValue(getEndQj());
        return workbook;
    }
}
