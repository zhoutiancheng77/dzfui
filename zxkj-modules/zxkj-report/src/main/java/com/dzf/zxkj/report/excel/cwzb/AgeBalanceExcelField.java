package com.dzf.zxkj.report.excel.cwzb;


import com.dzf.zxkj.common.entity.DynamicAttributeVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;

import java.util.Date;

public class AgeBalanceExcelField implements IExceport<DynamicAttributeVO> {
	
	private DynamicAttributeVO[] expvos = null;
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;

	private Fieldelement[] fields;
	@Override
	public String getExcelport2007Name() {
		return "往来账龄余额表"+now+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "往来账龄余额表_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "往来账龄余额表";
	}

	@Override
	public String getSheetName() {
		return "往来账龄余额表";
	}

	public void setExpvos (DynamicAttributeVO[] vos) {
		this.expvos = vos;
	}
	@Override
	public DynamicAttributeVO[] getData() {
		return expvos;
	}

	public void setFields (Fieldelement[] fields) {
		this.fields = fields;
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
		return new boolean[]{true,true,false};
	}

}
