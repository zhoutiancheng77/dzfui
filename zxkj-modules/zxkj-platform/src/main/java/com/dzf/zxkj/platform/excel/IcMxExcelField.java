package com.dzf.zxkj.platform.excel;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.IcDetailVO;

import java.util.Date;

public class IcMxExcelField implements IExceport<IcDetailVO> {

	private IcDetailVO[] icDetailVos = null;

	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = null;
	
	private int numPrecision;//数量精度
	private int pricePrecision;//单价精度
	
	public IcMxExcelField(int numPrecision, int pricePrecision){
		this.numPrecision = numPrecision;
		this.pricePrecision = pricePrecision;
	}
	
	@Override
	public String getExcelport2007Name() {
		return "库存明细账_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "库存明细账_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "库存明细账";
	}

	@Override
	public String getSheetName() {
		return "库存明细账";
	}
		
	@Override
	public IcDetailVO[] getData(){
		return icDetailVos;
	}
	
	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setIcDetailVos(IcDetailVO[] icDetailVos) {
		this.icDetailVos = icDetailVos;
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
	
	public Fieldelement[] getFields1(){
		return new Fieldelement[]{
				new Fieldelement("km", "科目",false,0,false),
				new Fieldelement("spfl", "存货分类",false,0,false),
				new Fieldelement("spbm", "存货编码",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("spgg", "规格(型号)",false,0,false),
//				new Fieldelement("spxh", "型号",false,0,false),
				new Fieldelement("jldw", "计量单位",false,0,false),
				new Fieldelement("dbilldate", "日期",false,0,false),
				new Fieldelement("dbillid", "单据号",false,0,false),
				new Fieldelement("zy", "摘要",false,0,false),
				new Fieldelement("srsl", "收入数量",true, numPrecision,true),
				new Fieldelement("srdj", "收入单价",true, pricePrecision,true),
				new Fieldelement("srje", "收入金额",true,2,true),
				new Fieldelement("fcsl", "发出数量",true, numPrecision,true),
				new Fieldelement("fcdj", "发出单价",true, pricePrecision,true),
				new Fieldelement("fcje", "发出金额",true,2,true),
				new Fieldelement("jcsl", "结存数量",true, numPrecision,true),
				new Fieldelement("jcdj", "结存单价",true, pricePrecision,true),
				new Fieldelement("jcje", "结存金额",true,2,true)
		};
	}
	
	public Fieldelement[] getFields2(){
		return new Fieldelement[]{
				new Fieldelement("km", "科目",false,0,false),
				new Fieldelement("spfl", "存货分类",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("spgg", "规格(型号)",false,0,false),
//				new Fieldelement("spxh", "型号",false,0,false),
				new Fieldelement("jldw", "计量单位",false,0,false),
				new Fieldelement("dbilldate", "日期",false,0,false),
				new Fieldelement("zy", "摘要",false,0,false),
				new Fieldelement("srsl", "收入数量",true, numPrecision,true),
				new Fieldelement("srdj", "收入单价",true, pricePrecision,true),
				new Fieldelement("srje", "收入金额",true,2,true),
				new Fieldelement("fcsl", "发出数量",true, numPrecision,true),
				new Fieldelement("fcdj", "发出单价",true, pricePrecision,true),
				new Fieldelement("fcje", "发出金额",true,2,true),
				new Fieldelement("jcsl", "结存数量",true, numPrecision,true),
				new Fieldelement("jcdj", "结存单价",true, pricePrecision,true),
				new Fieldelement("jcje", "结存金额",true,2,true)
		};
	}
	
	public Fieldelement[] getFields(){
		return fields;
	}
	
	public void setFields(Fieldelement[] fields) {
		this.fields = fields;
	}
	
	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{true,true,false};
	}
	
}
