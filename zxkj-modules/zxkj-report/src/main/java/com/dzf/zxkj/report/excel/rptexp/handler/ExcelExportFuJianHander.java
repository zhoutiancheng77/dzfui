package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.model.gl.gl_cwreport.LrbVO;
import com.dzf.model.gl.gl_cwreport.XjllbVO;
import com.dzf.model.gl.gl_cwreport.ZcFzBVO;
import com.dzf.report.enums.ExportTemplateEnum;
import com.dzf.service.gl.gl_cwreport.rptexp.ExcelExportHander;
import com.dzf.service.gl.gl_cwreport.rptexp.OneWorkBookKj2007Excel;
import com.dzf.service.gl.gl_cwreport.rptexp.OneWorkBookKj2013Excel;
import com.dzf.service.gl.gl_cwreport.rptexp.ResourceUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

public class ExcelExportFuJianHander extends ExcelExportHander implements OneWorkBookKj2007Excel, OneWorkBookKj2013Excel {
    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.FUJIAN, ResourceUtil.ResourceEnum.KJ2007ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 6, new Integer[]{2, 3, 4, 5, 6, 7},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        //利润表
        sheet = workbook.getSheetAt(2);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 5, new Integer[]{2,3,4}, new String[]{"bnljje","lastyear_bnljje"});
        //现金流量表
        sheet = workbook.getSheetAt(3);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 6, new Integer[]{2,3,4}, new String[]{"sqje","sqje_last"});
        return workbook;
    }

    @Override
    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.FUJIAN, ResourceUtil.ResourceEnum.KJ2013ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(1);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 5, new Integer[]{2, 4, 5, 6, 9, 10},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        //利润表
        sheet = workbook.getSheetAt(2);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 4, new Integer[]{2,7,8}, new String[]{"byje","bnljje"});
        //现金流量表
        sheet = workbook.getSheetAt(3);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 5, new Integer[]{2,7,9}, new String[]{"bqje","sqje"});
        return workbook;
    }
}
