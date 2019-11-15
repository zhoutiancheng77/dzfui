package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class VatImpConfSetVO extends SuperVO {
	
	private String pk_vatimpconfset;//主键
	private String stype;//类型  1银行对账单  2销项发票  3进项发票 
	private String filed;//导入的VO 字段列
	private String colname;//excel 的列名
	private String require;//是否必输项
	private String pk_corp;
	private DZFDateTime ts;
	private Integer dr;

	public String getPk_vatimpconfset() {
		return pk_vatimpconfset;
	}

	public String getStype() {
		return stype;
	}

	public String getFiled() {
		return filed;
	}

	public String getColname() {
		return colname;
	}

	public String getRequire() {
		return require;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_vatimpconfset(String pk_vatimpconfset) {
		this.pk_vatimpconfset = pk_vatimpconfset;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}

	public void setRequire(String require) {
		this.require = require;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		return "pk_vatimpconfset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_vatimpconfset";
	}

}
