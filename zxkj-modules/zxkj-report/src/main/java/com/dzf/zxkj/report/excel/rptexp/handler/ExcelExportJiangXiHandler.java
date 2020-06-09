package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.MoreWorkBookKj2013Excel;
import com.dzf.zxkj.report.excel.rptexp.OneWorkBookKj2007Excel;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import java.util.Map;


public class ExcelExportJiangXiHandler extends ExcelExportHander
		implements OneWorkBookKj2007Excel, MoreWorkBookKj2013Excel {

	@Override
	public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap,
										 Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap,
										 Map<String, ZcFzBVO> zcFzBVOMap) throws Exception {
		return createWorkBookKj2007(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, lrbVOMap, xjllbVOMap, zcFzBVOMap, "20196");
	}

	@Override
	public Workbook createWorkBookKj2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap,
			Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap,
			Map<String, ZcFzBVO> zcFzBVOMap, String versionno) throws Exception {
		Resource resource = ResourceUtil.get(ExportTemplateEnum.JIANGXI, ResourceUtil.ResourceEnum.KJ2007ALL,
				versionno);
		Workbook workbook = WorkbookFactory.create(resource.getInputStream());
		// 资产负债表
		Sheet sheet = workbook.getSheetAt(0);
		handleZcfzbSheet(sheet, zcfzTaxVoMap, zcFzBVOMap, 4, new Integer[] { 0, 2, 3, 4, 6, 7 },
				new String[] { "qmye1", "ncye1", "qmye2", "ncye2" });
		// 利润表
		sheet = workbook.getSheetAt(1);
		handleLrbSheet(sheet, lrbTaxVoMap, lrbVOMap, 4, new Integer[] { 0, 2, 3 },
				new String[] { "bnljje", "lastyear_bnljje" });
		return workbook;
	}

	@Override
	public Workbook createWorkBookZcfzKj2013(Map<String, ZcFzBVO> zcFzBVOMap, Map<String, String> taxaxVoMap)
			throws Exception {
		Resource resource = ResourceUtil.get(ExportTemplateEnum.JIANGXI, ResourceUtil.ResourceEnum.KJ2013ZCFZ);
		Workbook workbook = WorkbookFactory.create(resource.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		String nsrmc = getNsrmc();
		putValue(sheet, "A3", "公司：" + nsrmc); // A3 - "公司：" + CorpName
		putValue(sheet, "C3", "期间：" + getEndDate()); // C3 - "期间：2019-09-30"
		handleZcfzbSheet(sheet, taxaxVoMap, zcFzBVOMap, 3, new Integer[] { 0, 2, 3, 4, 6, 7 },
				new String[] { "qmye1", "ncye1", "qmye2", "ncye2" });
		return workbook;
	}

	@Override
	public Workbook createWorkBookLrbKj2013(Map<String, LrbVO> lrbVOMap, Map<String, String> taxaxVoMap)
			throws Exception {
		Resource resource = ResourceUtil.get(ExportTemplateEnum.JIANGXI, ResourceUtil.ResourceEnum.KJ2013LR);
		Workbook workbook = WorkbookFactory.create(resource.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		String nsrmc = getNsrmc();
		putValue(sheet, "A3", "公司：" + nsrmc); // A3 - "公司：" + CorpName
		putValue(sheet, "B3", "期间：" + getEndDate("yyyy-MM")); // B3 -
																// "期间：2019-09"
		handleLrbSheet(sheet, taxaxVoMap, lrbVOMap, 3, new Integer[] { 0, 2, 3 }, new String[] { "byje", "bnljje" });
		return workbook;
	}

	@Override
	public Workbook createWorkBookXjllKj2013(Map<String, XjllbVO> xjllbVOMap, Map<String, String> taxaxVoMap)
			throws Exception {
		return null;
	}

}
