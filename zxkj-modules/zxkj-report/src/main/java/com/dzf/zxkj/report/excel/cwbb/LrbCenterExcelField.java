package com.dzf.zxkj.report.excel.cwbb;


import com.dzf.zxkj.report.utils.ReportUtil;

/**
 * 利润表表导出配置
 * 
 * @author zhw
 *
 */
public class LrbCenterExcelField extends LrbExcelField {

	@Override
	public String getExcelport2007Name() {
		return "分部利润表(" + getCorpName() + ")-" + new ReportUtil().formatqj(periods) + ".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "分部利润表(" + getCorpName() + ")-" + new ReportUtil().formatqj(periods) + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "分部利润表";
	}

	@Override
	public String getSheetName() {
		return "分部利润表";
	}

}
