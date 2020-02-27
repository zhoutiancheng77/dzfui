package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 利润表表导出配置
 * 
 * @author zhw
 *
 */
public class LrbExcelField extends MuiltSheetAndTitleExceport<LrbVO> {

	private LrbVO[] lrbvos = null;

	private List<LrbVO[]>  allsheetlrbvos = null;

	private String columnOrder = null;
	
	protected String[] periods = null;
	
	private String[] allsheetname = null;
	
	private String corptype = null;//公司类型

	private String qj = null;

	private String now = DZFDate.getDate(new Date()).toString();

	private String creator = null;

	private String corpname = null;

	private boolean zeroshownull = true;

	@Override
	public String getExcelport2007Name() {
		return "利润表("+corpname+")-" + new ReportUtil().formatqj(periods) + ".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "利润表("+corpname+")-" + new ReportUtil().formatqj(periods) + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "利润表";
	}

	@Override
	public String getSheetName() {
		return "利润表";
	}

	@Override
	public LrbVO[] getData() {
		return lrbvos;
	}
	
	public String[] getPeriods() {
		return periods;
	}

	public void setPeriods(String[] periods) {
		this.periods = periods;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		if("on".equalsIgnoreCase(columnOrder)){//企业会计制度
			return new Fieldelement[] {
					new Fieldelement("xm", "项目", false, 0, false,60,false),
					new Fieldelement("hs", "行次", false, 0, false),
					new Fieldelement("byje", "本月金额", true, 2, zeroshownull),
					new Fieldelement("bnljje", "本年累计金额", true, 2, zeroshownull),
			};
		}else{
			return  new Fieldelement[] {
					new Fieldelement("xm", "项目", false, 0, false,60,false),
					new Fieldelement("hs", "行次", false, 0, false),
					new Fieldelement("bnljje", "本年累计金额", true, 2, zeroshownull),
					new Fieldelement("byje", "本月金额", true, 2, zeroshownull),
			};
		}
	}

	public boolean isZeroshownull() {
		return zeroshownull;
	}

	public void setZeroshownull(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

	public void setLrbvos(LrbVO[] lrbvos) {
		this.lrbvos = lrbvos;
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
	
	public List<LrbVO[]> getAllsheetlrbvos() {
		return allsheetlrbvos;
	}

	public void setAllsheetlrbvos(List<LrbVO[]> allsheetlrbvos) {
		this.allsheetlrbvos = allsheetlrbvos;
	}

	public String[] getAllsheetname() {
		return allsheetname;
	}

	public void setAllsheetname(String[] allsheetname) {
		this.allsheetname = allsheetname;
	}

	@Override
	public List<LrbVO[]> getAllSheetData() {
		return allsheetlrbvos;
	}

	@Override
	public String[] getAllSheetName() {
		return allsheetname;
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
		String column2_name = "02表";
		if ("00000100AA10000000000BMD".equals(corptype)) {// 小企业
			column2_name = "会小企业02表";
		} else if ("00000100AA10000000000BMF".equals(corptype)) {//企业会计准则 
			column2_name = "会企02表";
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
