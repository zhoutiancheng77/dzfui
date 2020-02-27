package com.dzf.zxkj.report.excel.cwbb;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 现金流量表导出配置
 * 
 * @author zhw
 *
 */
public class XjllbExcelField extends MuiltSheetAndTitleExceport<XjllbVO> {

	private XjllbVO[] xjllbvos = null;

	private List<XjllbVO[]> allsheetxjllvos = null;

	private String[] periods = null;

	private String[] allsheetname = null;
	
	private String corptype = null;

	private String columnOrder = null;

	private String qj = null;

	private String now = DZFDate.getDate(new Date()).toString();

	private String creator = null;

	private String corpname = null;

	private boolean zeroshownull = true;



	@Override
	public String getExcelport2007Name() {
		return "现金流量表(" + corpname + ")-" + new ReportUtil().formatqj(periods) + ".xlsx";
	}

	public boolean isZeroshownull() {
		return zeroshownull;
	}

	public void setZeroshownull(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}

	@Override
	public String getExcelport2003Name() {
		return "现金流量表(" + corpname + ")-" + new ReportUtil().formatqj(periods) + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "现金流量表";
	}

	@Override
	public String getSheetName() {
		return "现金流量表";
	}

	@Override
	public XjllbVO[] getData() {
		return xjllbvos;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		if("on".equals(columnOrder) ){
			return  new Fieldelement[] { new Fieldelement("xm", "项目", false, 0, false, 55, false),
					new Fieldelement("hc", "行次", false, 0, false, 4, false),
					new Fieldelement("bqje", "本月金额", true, 2, zeroshownull),
					new Fieldelement("sqje", "本年累计金额", true, 2, zeroshownull) ,
			};
		}else{
			return new Fieldelement[] { new Fieldelement("xm", "项目", false, 0, false, 55, false),
					new Fieldelement("hc", "行次", false, 0, false, 4, false),
					new Fieldelement("sqje", "本年累计金额", true, 2, zeroshownull) ,
					new Fieldelement("bqje", "本月金额", true, 2, zeroshownull),
			};
		}
	}

	public void setXjllbvos(XjllbVO[] xjllbvos) {
		this.xjllbvos = xjllbvos;
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
		return new boolean[] { true, true, false };
	}

	@Override
	public String[] getAllSheetName() {
		return allsheetname;
	}

	@Override
	public List<XjllbVO[]> getAllSheetData() {
		return allsheetxjllvos;
	}

	@Override
	public String[] getAllPeriod() {
		return periods;
	}

	public List<XjllbVO[]> getAllsheetxjllvos() {
		return allsheetxjllvos;
	}

	public void setAllsheetxjllvos(List<XjllbVO[]> allsheetxjllvos) {
		this.allsheetxjllvos = allsheetxjllvos;
	}

	public String[] getPeriods() {
		return periods;
	}

	public void setPeriods(String[] periods) {
		this.periods = periods;
	}

	public String[] getAllsheetname() {
		return allsheetname;
	}

	public void setAllsheetname(String[] allsheetname) {
		this.allsheetname = allsheetname;
	}

	public String getNow() {
		return now;
	}

	public void setNow(String now) {
		this.now = now;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public XjllbVO[] getXjllbvos() {
		return xjllbvos;
	}

	public String getCreator() {
		return creator;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	@Override
	public List<TitleColumnExcelport> getHeadColumns() {
		String column2_name = "03表";
		if ("00000100AA10000000000BMD".equals(corptype)) {// 小企业
			column2_name = "会小企业03表";
		} else if ("00000100AA10000000000BMF".equals(corptype)) {//企业会计准则 
			column2_name = "会企03表";
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
