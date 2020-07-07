package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.report.excel.rptexp.MoreWorkBookKj2007Excel;
import com.dzf.zxkj.report.excel.rptexp.MoreWorkBookKj2013Excel;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

public class ExcelExportLiaoNingHander extends ExcelExportStandardHander implements MoreWorkBookKj2007Excel, MoreWorkBookKj2013Excel {
//    @Override
//    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
//        Resource resource = ResourceUtil.get(ExportTemplateEnum.LIAONING, ResourceUtil.ResourceEnum.KJ2013ALL);
//        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
//        //资产负债表
//        Sheet sheet = workbook.getSheetAt(0);
//        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 8, new Integer[]{2, 3, 4, 6, 7, 8},new String[]{"qmye1","ncye1","qmye2","ncye2"});
//        //利润表
//        sheet = workbook.getSheetAt(1);
//        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 4, new Integer[]{1,2,3}, new String[]{"byje","bnljje"});
//        //现金流量表
//        sheet = workbook.getSheetAt(2);
//        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{2,3,4}, new String[]{"bqje","sqje"});
//        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 4, new Integer[]{1,3,4}, new String[]{"bqje","sqje"});
//        return workbook;
//    }

    @Override
    public Workbook createWorkBookLrbKj2013(Map<String, LrbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.LIAONING, ResourceUtil.ResourceEnum.KJ2013LR);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleLrbSheet(sheet, taxaxVoMap, vOMap, 4, new Integer[]{0,2,3}, new String[]{"byje","bnljje"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookXjllKj2013(Map<String, XjllbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.LIAONING, ResourceUtil.ResourceEnum.KJ2013XJLL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        handleXjllSheet(sheet, taxaxVoMap, vOMap, 5, new Integer[]{0,2,3}, new String[]{"bqje", "sqje"});
        return workbook;
    }
}
