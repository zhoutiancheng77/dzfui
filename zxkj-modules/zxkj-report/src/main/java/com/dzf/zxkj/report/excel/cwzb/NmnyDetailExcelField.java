package com.dzf.zxkj.report.excel.cwzb;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数量金额明细表导出配置
 * @author zpm
 *
 */
public class NmnyDetailExcelField implements IExceport<NumMnyDetailVO> {
	
	private NumMnyDetailVO[] NumMnyDetailVOs = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = null;
	
	private int numPrecision;//数量精度
	private int pricePrecision;//单价精度
	
	private String showjfdj;
	private String showdfdj;
	private String showyedj;
	
	public NmnyDetailExcelField(int numPrecision, int pricePrecision, String showjfdj, String showdfdj,
			String showyedj) {
		this.numPrecision = numPrecision;
		this.pricePrecision = pricePrecision;
		this.showjfdj = showjfdj;
		this.showdfdj = showdfdj;
		this.showyedj = showyedj;
	}
	
	@Override
	public String getExcelport2007Name() {
		return "数量金额明细账-"+ ReportUtil.formatQj(qj)+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "数量金额明细账-"+ReportUtil.formatQj(qj)+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "数量金额明细账";
	}

	@Override
	public String getSheetName() {
		return "数量金额明细账";
	}

	@Override
	public NumMnyDetailVO[] getData() {
		return NumMnyDetailVOs;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setNumMnyDetailVOs(NumMnyDetailVO[] NumMnyDetailVOs) {
		this.NumMnyDetailVOs = NumMnyDetailVOs;
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
	
	public Fieldelement[] getFields1() {
		List<Fieldelement> list = new ArrayList<Fieldelement>();
		list.add(new Fieldelement("kmbm", "科目编码", false, 0, false));
		list.add(new Fieldelement("kmmc", "科目名称", false, 0, false));
		list.add(new Fieldelement("jldw", "计量单位", false, 0, false));
		list.add(new Fieldelement("opdate", "日期", false, 0, false));
		list.add(new Fieldelement("pzh", "凭证号", false, 0, false));
		list.add(new Fieldelement("zy", "摘要", false, 0, false));
		list.add(new Fieldelement("nnum", "借方数量", true, numPrecision, true));
		if("Y".equals(showjfdj)){
			list.add(new Fieldelement("nprice", "借方单价", true, pricePrecision, true));
		}
		list.add(new Fieldelement("nmny", "借方金额", true, 2, true));
		list.add(new Fieldelement("ndnum", "贷方数量", true, numPrecision, true));
		if("Y".equals(showdfdj)){
			list.add(new Fieldelement("ndprice", "贷方单价", true, pricePrecision, true));
		}
		list.add(new Fieldelement("ndmny", "贷方金额", true, 2, true));
		list.add(new Fieldelement("dir", "方向", false, 0, false));
		list.add(new Fieldelement("nynum", "期末数量", true, numPrecision, true));
		if("Y".equals(showyedj)){
			list.add(new Fieldelement("nyprice", "期末单价", true, pricePrecision, true));
		}
		list.add(new Fieldelement("nymny", "期末金额", true, 2, true));
		return list.toArray(new Fieldelement[0]);
	}

	public Fieldelement[] getFields2() {
		return new Fieldelement[]{
				new Fieldelement("kmmc", "科目名称",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("opdate", "日期",false,0,false),
				new Fieldelement("pzh", "凭证号",false,0,false),
				new Fieldelement("zy", "摘要",false,0,false),
				new Fieldelement("nnum", "借方数量",true, numPrecision,true),
				new Fieldelement("nprice", "借方单价",true, pricePrecision,true),
				new Fieldelement("nmny", "借方金额",true,2,true),
				new Fieldelement("ndnum", "贷方数量",true, numPrecision,true),
				new Fieldelement("ndprice", "贷方单价",true, pricePrecision,true),
				new Fieldelement("ndmny", "贷方金额",true,2,true),
				new Fieldelement("dir", "方向",false,0,false),
				new Fieldelement("nynum", "期末数量",true, numPrecision,true),
				new Fieldelement("nyprice", "期末单价",true, pricePrecision,true),
				new Fieldelement("nymny", "期末金额",true,2,true),
		};
	}

	public void setFields(Fieldelement[] fields) {
		this.fields = fields;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{true,true,false};
	}
}