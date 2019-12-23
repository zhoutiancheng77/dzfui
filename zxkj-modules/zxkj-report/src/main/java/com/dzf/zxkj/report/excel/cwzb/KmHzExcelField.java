package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.FseJyeVO;

import java.util.Date;

/**
 * 科目汇总表导出配置
 * @author zpm
 *
 */
public class KmHzExcelField implements IExceport<FseJyeVO> {
	
	private FseJyeVO[] FseJyeVOs = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("kmlb", "科目类别",false,0,false),
			new Fieldelement("kmbm", "科目编码",false,0,false),
			new Fieldelement("kmmc", "科目名称",false,0,false),
			new Fieldelement("fsjf","本期发生借方",true,2,true),
			new Fieldelement("fsdf", "本期发生贷方",true,2,true)
	};

	@Override
	public String getExcelport2007Name() {
		return "科目汇总表_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "科目汇总表_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "科目汇总表";
	}

	@Override
	public String getSheetName() {
		return "科目汇总表";
	}

	@Override
	public FseJyeVO[] getData() {
		return FseJyeVOs;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setFseJyeVOs(FseJyeVO[] FseJyeVOs) {
		this.FseJyeVOs = FseJyeVOs;
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