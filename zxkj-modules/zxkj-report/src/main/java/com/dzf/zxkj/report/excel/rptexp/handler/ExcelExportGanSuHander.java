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

public class ExcelExportGanSuHander extends ExcelExportHander implements OneWorkBookKj2007Excel, OneWorkBookKj2013Excel {

    private void fillNsrxx(Sheet sheet){
        String headerText = sheet.getRow(0).getCell(0).getStringCellValue();
        headerText = headerText.replace("纳税人识别号:","纳税人识别号:"+getNsrsbh());
        headerText = headerText.replace("税款所属期起止:","税款所属期起止:"+getBeginQj());
        headerText = headerText.replace("至","至"+getEndQj());
        headerText = headerText.replace("纳税人名称:","纳税人名称:"+getNsrmc());
        headerText = headerText.replace("报送日期:","报送日期:"+getEndQj());
        sheet.getRow(0).getCell(0).setCellValue(headerText);
    }

    private void fillNsrxx1(Sheet sheet){
        String headerText = sheet.getRow(0).getCell(0).getStringCellValue();
        headerText = headerText.replace("纳税人识别号:","纳税人识别号:"+getNsrsbh());
        sheet.getRow(0).getCell(0).setCellValue(headerText);
        headerText = sheet.getRow(0).getCell(3).getStringCellValue();
        headerText = headerText.replace("税款所属期起止:","税款所属期起止:"+getBeginQj());
        headerText = headerText.replace("至","至"+getEndQj());
        sheet.getRow(0).getCell(3).setCellValue(headerText);

        headerText = sheet.getRow(1).getCell(0).getStringCellValue();
        headerText = headerText.replace("纳税人名称:","纳税人名称:"+getNsrmc());
        sheet.getRow(1).getCell(0).setCellValue(headerText);
    }

    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.GANSU, ResourceUtil.ResourceEnum.KJ2007ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(0);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 4, new Integer[]{0, 2, 4, 5, 7, 8},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        fillNsrxx1(sheet);
        //利润表
        sheet = workbook.getSheetAt(1);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 2, new Integer[]{0,2,3}, new String[]{"bnljje","lastyear_bnljje"});
        fillNsrxx(sheet);
        //现金流量表
        sheet = workbook.getSheetAt(2);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 3, new Integer[]{0,2,3}, new String[]{"sqje","sqje_last"});
        fillNsrxx(sheet);
        return workbook;
    }

    @Override
    public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap, String versionno) throws Exception {
        return null;
    }

    @Override
    public Workbook createWorkBookKj2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
        Resource resource = ResourceUtil.get(ExportTemplateEnum.GANSU, ResourceUtil.ResourceEnum.KJ2013ALL);
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        //资产负债表
        Sheet sheet = workbook.getSheetAt(0);
        handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 3, new Integer[]{0, 2, 3, 4, 6, 7},new String[]{"qmye1","ncye1","qmye2","ncye2"});
        fillNsrxx(sheet);
        //利润表
        sheet = workbook.getSheetAt(1);
        handleLrbSheet(sheet, lrbTaxVoMap,lrbVOMap, 2, new Integer[]{0,2,3}, new String[]{"bnljje","byje"});
        fillNsrxx(sheet);
        //现金流量表
        sheet = workbook.getSheetAt(2);
        handleXjllSheet(sheet, xjllTaxVoMap, xjllbVOMap, 3, new Integer[]{0,2,3}, new String[]{"sqje","bqje"});
        fillNsrxx(sheet);
        return workbook;
    }
}
