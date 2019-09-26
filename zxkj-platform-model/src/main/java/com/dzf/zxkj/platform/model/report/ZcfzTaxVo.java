package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class ZcfzTaxVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_report_tax_zcfz";

	public static final String PK_FIELD = "pk_report_tax_zcfz";

	private String pk_report_tax_zcfz;// 主键
	private Integer hc;// 行次(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private Integer hc_1; // 负债行次(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private Integer ordernum;// 排序
	private String zcname;// 资产名称(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private String fzname;// 负债名称(报税地区  如果行次是空，则按照报税地区的名称对照***************)
	private Integer zchc_ref;// 资产行次对照(系统行次)
	private Integer fzhc_ref;// 负债行次(系统行次)
	private String corptype;// 行业
	private Integer area_type;// 地区(0深圳，1河北,2浙江,3青岛)
	private String pk_corp;
	private DZFDateTime ts;
	private Integer dr;

	public Integer getHc_1() {
		return hc_1;
	}

	public void setHc_1(Integer hc_1) {
		this.hc_1 = hc_1;
	}

	public Integer getArea_type() {
		return area_type;
	}

	public void setArea_type(Integer area_type) {
		this.area_type = area_type;
	}

	public String getPk_report_tax_zcfz() {
		return pk_report_tax_zcfz;
	}

	public void setPk_report_tax_zcfz(String pk_report_tax_zcfz) {
		this.pk_report_tax_zcfz = pk_report_tax_zcfz;
	}

	public Integer getZchc_ref() {
		return zchc_ref;
	}

	public void setZchc_ref(Integer zchc_ref) {
		this.zchc_ref = zchc_ref;
	}

	public Integer getFzhc_ref() {
		return fzhc_ref;
	}

	public void setFzhc_ref(Integer fzhc_ref) {
		this.fzhc_ref = fzhc_ref;
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

	public String getZcname() {
		return zcname;
	}

	public void setZcname(String zcname) {
		this.zcname = zcname;
	}

	public String getFzname() {
		return fzname;
	}

	public void setFzname(String fzname) {
		this.fzname = fzname;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
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
