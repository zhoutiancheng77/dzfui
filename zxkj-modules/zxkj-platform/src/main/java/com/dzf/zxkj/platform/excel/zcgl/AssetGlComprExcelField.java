package com.dzf.zxkj.platform.excel.zcgl;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;

import java.util.Date;

/**
 * 总账对账
 * @author wangzhn
 *
 */
public class AssetGlComprExcelField implements IExceport<ZcdzVO> {

	private ZcdzVO[] vos = null;

	private  String nodename;//节点名字

	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("zcsx", "资产属性",false,0,false),
			new Fieldelement("zclb", "资产类别",false,0,false),
			new Fieldelement("zckm", "资产科目",false,0,false),
			new Fieldelement("zzkmbh", "总账科目编号",false,0,false),
			new Fieldelement("zzkmmc", "总账科目名称",true,2,true),
			new Fieldelement("zcje", "资产金额",true,2,true),
			new Fieldelement("zzje", "总账",true,2,true),
		};

	@Override
	public String getExcelport2007Name() {
		return nodename+"_"+now+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return nodename+"_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return nodename;
	}

	@Override
	public String getSheetName() {
		return nodename;
	}

	@Override
	public ZcdzVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setVos(ZcdzVO[] vos) {
		this.vos = vos;
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
