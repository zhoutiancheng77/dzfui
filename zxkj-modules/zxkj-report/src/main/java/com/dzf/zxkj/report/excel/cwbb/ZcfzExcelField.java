package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 资产负债表表导出配置
 * 
 * @author zhw
 *
 */
public class ZcfzExcelField  extends MuiltSheetAndTitleExceport<ZcFzBVO> {

	private ZcFzBVO[] zcfzvos = null;
	
	private List<ZcFzBVO[]> allsheetzcvos = null;
	
	private String[] periods = null;
	
	private String[] allsheetname = null;

	private String qj = null;

	private String now = DZFDate.getDate(new Date()).toString();

	private String creator = null;

	private String corpname = null;
	
	private String corptype;

	private boolean zeroshownull = true;

	public ZcfzExcelField(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}


	@Override
	public String getExcelport2007Name() {
		return "资产负债表("+corpname+")-" + new ReportUtil().formatqj(periods) + ".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "资产负债表("+corpname+")-" + new ReportUtil().formatqj(periods) + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "资产负债表";
	}

	@Override
	public String getSheetName() {
		return "资产负债表";
	}

	@Override
	public ZcFzBVO[] getData() {
		return zcfzvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return new Fieldelement[] {
				new Fieldelement("zc", "资产", false, 0, false,30,false),
				new Fieldelement("hc1", "行次", false, 0, false),
				new Fieldelement("qmye1", "期末余额", true, 2, zeroshownull),
				new Fieldelement("ncye1", "年初余额", true, 2, zeroshownull),
				new Fieldelement("fzhsyzqy", "负债和所有者权益", false, 0, false,30,false),
				new Fieldelement("hc2", "行次", false, 0, false),
				new Fieldelement("qmye2", "期末余额", true, 2, zeroshownull),
				new Fieldelement("ncye2", "年初余额", true, 2, zeroshownull)};
	}

	public void setZcfzvos(ZcFzBVO[] zcfzvos) {
		this.zcfzvos = zcfzvos;
	}

	@Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}

	public void setCorpName(String corpname) {
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[] { true, true, true };
	}
	
	@Override
	public List<ZcFzBVO[]> getAllSheetData() {
		return allsheetzcvos;
	}

	@Override
	public String[] getAllSheetName() {
		return allsheetname;
	}
	
	public List<ZcFzBVO[]> getAllsheetzcvos() {
		return allsheetzcvos;
	}

	public void setAllsheetzcvos(List<ZcFzBVO[]> allsheetzcvos) {
		this.allsheetzcvos = allsheetzcvos;
	}

	public String[] getAllsheetname() {
		return allsheetname;
	}

	public void setAllsheetname(String[] allsheetname) {
		this.allsheetname = allsheetname;
	}

	public boolean isZeroshownull() {
		return zeroshownull;
	}

	public void setZeroshownull(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}

	public String[] getPeriods() {
		return periods;
	}

	public void setPeriods(String[] periods) {
		this.periods = periods;
	}

	@Override
	public String[] getAllPeriod() {
		return periods;
	}
	

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	@Override
	public List<TitleColumnExcelport> getHeadColumns() {
		String column2_name = "01表";
		if ("00000100AA10000000000BMD".equals(corptype)) {// 小企业
			column2_name = "会小企业01表";
		} else if ("00000100AA10000000000BMF".equals(corptype)) {//企业会计准则 
			column2_name = "会企01表";
		}
		List<TitleColumnExcelport> lists = new ArrayList<TitleColumnExcelport>();
		TitleColumnExcelport column2 = new TitleColumnExcelport(1, column2_name, HorizontalAlignment.RIGHT);
		lists.add(column2);
		return lists;
	}

	@Override
	public TitleColumnExcelport getTitleColumns() {
		TitleColumnExcelport column1 = new TitleColumnExcelport(1, getSheetName(), HorizontalAlignment.RIGHT);
		return column1;
	}
	
}
