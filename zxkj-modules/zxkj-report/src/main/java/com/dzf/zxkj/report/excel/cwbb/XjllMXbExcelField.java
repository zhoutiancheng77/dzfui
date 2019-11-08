package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.XjllMxvo;

import java.util.Date;


/**
 * 现金流量明细表导出配置
 * 
 * @author zhangj
 * 
 *
 */
public class XjllMXbExcelField implements IExceport<XjllMxvo> {

	private XjllMxvo[] kmmxvos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
//		new Fieldelement("xmcode", "行次",false,0,false),
		new Fieldelement("pzh", "凭证号",false,0,false),
		new Fieldelement("dopedate", "操作日期",false,0,false),
		new Fieldelement("zy", "摘要",false,0,false),
		new Fieldelement("code","对方科目编码",false,0,false),
		new Fieldelement("name", "对方科目名称",false,0,false),
		new Fieldelement("xm", "项目",false,0,false),
		new Fieldelement("jffs", "金额",true,2,true),
	};

	@Override
	public String getExcelport2007Name() {
		return "现金流量明细_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "现金流量明细_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "现金流量明细";
	}

	@Override
	public String getSheetName() {
		return "现金流量明细";
	}

	@Override
	public XjllMxvo[] getData() {
		return kmmxvos;
	}

	public XjllMxvo[] getKmmxvos() {
		return kmmxvos;
	}

	public void setKmmxvos(XjllMxvo[] kmmxvos) {
		this.kmmxvos = kmmxvos;
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
