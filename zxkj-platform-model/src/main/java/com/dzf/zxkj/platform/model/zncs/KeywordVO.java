package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 关键字表
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class KeywordVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_keyword;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	private Integer dr;
	private DZFDateTime ts;
	@JsonProperty("kw")
	private String keyword;//关键字
	@JsonProperty("isenable")
	private DZFBoolean useflag;//是否启用
	@JsonProperty("nt")
	private String note;//说明
	@JsonProperty("oid")
	private String coperatorid;//操作人
	@JsonProperty("odate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("eid")
	private String cenableid;//启用人
	@JsonProperty("edate")
	private DZFDate denabledate;//启用日期
	
	@JsonProperty("oname")
	private String coperatorname;//操作人名称
	@JsonProperty("ename")
	private String cenablename;//启用人名称
	
	public String getCoperatorname() {
		return coperatorname;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public String getCenablename() {
		return cenablename;
	}

	public void setCenablename(String cenablename) {
		this.cenablename = cenablename;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPk_keyword() {
		return pk_keyword;
	}

	public void setPk_keyword(String pk_keyword) {
		this.pk_keyword = pk_keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public DZFBoolean getUseflag() {
		return useflag;
	}

	public void setUseflag(DZFBoolean useflag) {
		this.useflag = useflag;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getCenableid() {
		return cenableid;
	}

	public void setCenableid(String cenableid) {
		this.cenableid = cenableid;
	}

	public DZFDate getDenabledate() {
		return denabledate;
	}

	public void setDenabledate(DZFDate denabledate) {
		this.denabledate = denabledate;
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
		return "pk_keyword";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_keyword";
	}
	
	
}
