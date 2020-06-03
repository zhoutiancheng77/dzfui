package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class ZcfzRptSetVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_zcfz_set";

	public static final String PK_FIELD = "pk_zcfz_set";

	private String pk_zcfz_set;// 主键
	private String zcname;// 资产名称
	private String fzname;// 负债名称
	private String zchc;// 资产行次
	private String fzhc;// 负债行次
	private String zckm;// 资产科目
	private String zckm_re;//重分类科目
	private String fzkm;// 负债科目
	private String fzkm_re;//负债重分类科目
	private String pk_trade_accountschema;// 行业
	private Integer ordernum;//序号
	private String pk_corp;// 公司
	private Integer dr;//
	private DZFDateTime ts;
	private String zchc_id;//不可修改
	private String fzhc_id;//不可修改
	private String versionno;//版本号


	public String getVersionno() {
		return versionno;
	}

	public void setVersionno(String versionno) {
		this.versionno = versionno;
	}

	public String getZchc_id() {
		return zchc_id;
	}

	public void setZchc_id(String zchc_id) {
		this.zchc_id = zchc_id;
	}

	public String getFzhc_id() {
		return fzhc_id;
	}

	public void setFzhc_id(String fzhc_id) {
		this.fzhc_id = fzhc_id;
	}

	public String getZckm_re() {
		return zckm_re;
	}

	public void setZckm_re(String zckm_re) {
		this.zckm_re = zckm_re;
	}

	public String getFzkm_re() {
		return fzkm_re;
	}

	public void setFzkm_re(String fzkm_re) {
		this.fzkm_re = fzkm_re;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	public String getPk_zcfz_set() {
		return pk_zcfz_set;
	}

	public void setPk_zcfz_set(String pk_zcfz_set) {
		this.pk_zcfz_set = pk_zcfz_set;
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

	public String getZchc() {
		return zchc;
	}

	public void setZchc(String zchc) {
		this.zchc = zchc;
	}

	public String getFzhc() {
		return fzhc;
	}

	public void setFzhc(String fzhc) {
		this.fzhc = fzhc;
	}

	public String getZckm() {
		return zckm;
	}

	public void setZckm(String zckm) {
		this.zckm = zckm;
	}

	public String getFzkm() {
		return fzkm;
	}

	public void setFzkm(String fzkm) {
		this.fzkm = fzkm;
	}

	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}

	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
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
