package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.YwHdVO;

import java.util.Date;


public class YwHdQuarterlyExcelField implements IExceport<YwHdVO> {

	private YwHdVO[] ywhdvos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("xm", "项目",false,0,false),
			new Fieldelement("hs", "行次",false,0,false),
			new Fieldelement("monfxdx", "本季度限定性",true,2,true),
			new Fieldelement("monxdx", "本季度非限定性",true,2,true),
			new Fieldelement("monhj", "本季度合计",true,2,true),
			new Fieldelement("yearfxdx", "本年非限定性",true,2,true),
			new Fieldelement("yearxdx", "本年限定性",true,2,true),
			new Fieldelement("yearhj", "本年合计",true,2,true)
	};
	
	@Override
	public String getExcelport2007Name() {
		return "业务活动季报-"+ qj.replace("-", "")+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "业务活动季报-"+ qj.replace("-", "")+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "业务活动季报";
	}

	@Override
	public String getSheetName() {
		return "业务活动季报";
	}

	@Override
	public YwHdVO[] getData() {
		return ywhdvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}
	
	public void setYwhdvos(YwHdVO[] ywhdvos) {
		this.ywhdvos = ywhdvos;
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