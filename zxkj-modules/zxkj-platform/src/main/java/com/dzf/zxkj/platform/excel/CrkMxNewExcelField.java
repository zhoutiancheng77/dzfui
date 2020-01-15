package com.dzf.zxkj.platform.excel;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;

import java.util.Date;

public class CrkMxNewExcelField implements IExceport<IcDetailVO> {

	private IcDetailVO[] icDetailVos = null;

	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = null;
	
	private int numPrecision;//数量精度
	private int pricePrecision;//单价精度
	
	public CrkMxNewExcelField(int numPrecision, int pricePrecision){
		this.numPrecision = numPrecision;
		this.pricePrecision = pricePrecision;
	}
	
	@Override
	public String getExcelport2007Name() {
		return "出入库明细表_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "出入库明细表_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "出入库明细表";
	}

	@Override
	public String getSheetName() {
		return "出入库明细表";
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
				new Fieldelement("dbilldate", "日期",false,0,false),
				new Fieldelement("spfl", "存货类别",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("zy", "摘要",false,0,false),
				new Fieldelement("vicbillcode", "出入库单号",false,0,false),
				new Fieldelement("pzh", "凭证号",false,0,false),
				new Fieldelement("spgg", "规格(型号)",false,0,false),
				new Fieldelement("jldw", "计量单位",false,0,false),
				new Fieldelement("srsl", "入库数量",true, numPrecision,true),
				new Fieldelement("srdj", "入库单价",true, pricePrecision,true),
				new Fieldelement("srje", "入库金额",true,2,true),
				new Fieldelement("fcsl", "出库数量",true, numPrecision,true),
				new Fieldelement("fcdj", "出库单价",true, pricePrecision,true),
				new Fieldelement("fcje", "出库金额",true,2,true),
				new Fieldelement("jcsl", "结存数量",true, numPrecision,true),
				new Fieldelement("jcdj", "结存单价",true, pricePrecision,true),
				new Fieldelement("jcje", "结存金额",true,2,true)
		};
	}
	public Fieldelement[] getFields2(){
		return new Fieldelement[]{
				new Fieldelement("dbilldate", "日期",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("zy", "摘要",false,0,false),
				new Fieldelement("vicbillcode", "出入库单号",false,0,false),
				new Fieldelement("pzh", "凭证号",false,0,false),
				new Fieldelement("spgg", "规格(型号)",false,0,false),
				new Fieldelement("jldw", "计量单位",false,0,false),
				new Fieldelement("srsl", "入库数量",true, numPrecision,true),
				new Fieldelement("srdj", "入库单价",true, pricePrecision,true),
				new Fieldelement("srje", "入库金额",true,2,true),
				new Fieldelement("fcsl", "出库数量",true, numPrecision,true),
				new Fieldelement("fcdj", "出库单价",true, pricePrecision,true),
				new Fieldelement("fcje", "出库金额",true,2,true),
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
