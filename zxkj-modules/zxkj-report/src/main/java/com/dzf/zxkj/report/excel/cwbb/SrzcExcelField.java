package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.SrzcBVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.Date;


public class SrzcExcelField implements IExceport<SrzcBVO> {
	
	private SrzcBVO[] expvos = null;
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;

	private boolean zeroshownull = true;

	@Override
	public String getExcelport2007Name() {
		return "收入支出表-"+ ReportUtil.formatQj(qj)+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "收入支出表-"+ReportUtil.formatQj(qj)+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "收入支出表";
	}

	@Override
	public String getSheetName() {
		return "收入支出表";
	}

	public void setExpvos (SrzcBVO[] vos) {
		this.expvos = vos;
	}
	@Override
	public SrzcBVO[] getData() {
		return expvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return new Fieldelement[] {
				new Fieldelement("xm", "项目", false, 0, true,80, false),
				new Fieldelement("monnum", "本月数", true, 2, zeroshownull),
				new Fieldelement("yearnum", "本年累计数", true, 2, zeroshownull) };
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

	public boolean isZeroshownull() {
		return zeroshownull;
	}

	public void setZeroshownull(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}
}
