package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class XjllTaxVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_report_tax_xjll";

	public static final String PK_FIELD = "pk_report_tax_xjll";

	private String pk_report_tax_xjll;// 主键
	private Integer hc;// 行次(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private Integer ordernum;//排序
	private String corptype;// 行业
	private String vname;// 名称(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private String vname_ref;// 名称
	private Integer hc_ref;//利润表和现金流量表对照
	private Integer area_type;//地区(0深圳，1河北)
	private String pk_corp;
	private DZFDateTime ts;
	private Integer dr;
	
	public String getPk_report_tax_xjll() {
		return pk_report_tax_xjll;
	}

	public void setPk_report_tax_xjll(String pk_report_tax_xjll) {
		this.pk_report_tax_xjll = pk_report_tax_xjll;
	}

	public Integer getArea_type() {
		return area_type;
	}

	public void setArea_type(Integer area_type) {
		this.area_type = area_type;
	}

	public Integer getHc_ref() {
		return hc_ref;
	}

	public void setHc_ref(Integer hc_ref) {
		this.hc_ref = hc_ref;
	}

	public String getVname_ref() {
		return vname_ref;
	}

	public void setVname_ref(String vname_ref) {
		this.vname_ref = vname_ref;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Integer getHc() {
		return hc;
	}

	public void setHc(Integer hc) {
		this.hc = hc;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
