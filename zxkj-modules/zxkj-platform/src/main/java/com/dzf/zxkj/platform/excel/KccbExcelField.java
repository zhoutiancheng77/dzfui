package com.dzf.zxkj.platform.excel;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;

import java.util.Date;

/**
 * 库存成本表
 * @author wangzhn
 *
 */
public class KccbExcelField implements IExceport<IcbalanceVO> {

	private IcbalanceVO[] expvos = null;
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private int numPrecision;//数量精度
	private int pricePrecision;//单价精度
	
	public KccbExcelField(int numPrecision, int pricePrecision){
		this.numPrecision = numPrecision;
		this.pricePrecision = pricePrecision;
	}
	
//	private Fieldelement[] fields = null;
	
	@Override
	public String getExcelport2007Name() {
		return "库存成本表_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "库存成本表_"+now+".xls";
	}
	
	@Override
	public String getExceportHeadName() {
		return "库存成本表";
	}

	@Override
	public String getSheetName() {
		return "库存成本表";
	}

	public void setExpvos (IcbalanceVO[] vos) {
		this.expvos = vos;
	}
	@Override
	public IcbalanceVO[] getData() {
		return expvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return new Fieldelement[] {
				new Fieldelement("inventorycode", "存货编码", false, 0, true),
				new Fieldelement("inventoryname", "存货名称", false, 0, true),
				new Fieldelement("invspec", "规格(型号)", false, 0, true),
//				new Fieldelement("invtype", "型号", false, 0, true),
				new Fieldelement("measurename", "计量单位", false, 0, true),
				new Fieldelement("inventorytype", "存货分类", false, 0, true),
				new Fieldelement("pk_subjectname", "科目名称", false, 0, true),
				new Fieldelement("nnum", "结存数量", true, numPrecision, true),
				new Fieldelement("nprice", "结存单价", true, pricePrecision, true),
				new Fieldelement("ncost", "结存成本", true, 2, true)};
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
