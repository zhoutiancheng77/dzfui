package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.excel.param.UnitExceport;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.Date;


public class XjyhrjzExcelField implements IExceport<KmMxZVO>, UnitExceport<KmMxZVO> {

	private KmMxZVO[] xjrjzvos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String currencyname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
		new Fieldelement("rq", "日期",false,0,false),
		new Fieldelement("km", "科目",false,0,false),
		new Fieldelement("pzh", "凭证号",false,0,false),
		new Fieldelement("zy", "摘要",false,0,false),
		new Fieldelement("jf","借方",true,2,true),
		new Fieldelement("df", "贷方",true,2,true),
		new Fieldelement("fx", "方向",false,0,false),
		new Fieldelement("ye", "余额",true,2,true),
	};

	@Override
	public String getExcelport2007Name() {
		return "现金、银行日记账-"+ ReportUtil.formatQj(qj)+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "现金、银行日记账-"+ReportUtil.formatQj(qj)+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "现金/银行日记账";
	}

	@Override
	public String getSheetName() {
		return "现金银行日记账";
	}

	@Override
	public KmMxZVO[] getData() {
		return xjrjzvos;
	}


	public KmMxZVO[] getXjrjzvos() {
		return xjrjzvos;
	}

	public void setXjrjzvos(KmMxZVO[] xjrjzvos) {
		this.xjrjzvos = xjrjzvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
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
	
	public void setCorpName(String corpname){
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{true,true,true};
	}
	
	

	public String getCurrencyname() {
		return currencyname;
	}

	public void setCurrencyname(String currencyname) {
		this.currencyname = currencyname;
	}

	@Override
	public String getDw() {
		return currencyname;
	}
}