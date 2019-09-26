package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IcbalanceVO extends SuperVO {
	
	private   String tittleperiod;

	private   String gs;
	private  String djrq;//查询日期
	
	@JsonProperty("id")
	private String pk_icbalance;
	@JsonProperty("spid")
	private String pk_inventory;
	@JsonProperty("gsid")
	private String pk_corp;
	@JsonProperty("ctime")
	private String createtime;
	@JsonProperty("cpsn")
	private String creator;
	@JsonProperty("pzid")
	private String pk_voucher;
	@JsonProperty("bzid")
	private String pk_currency;
	@JsonProperty("djlx")
	private String cbilltype;
	@JsonProperty("sl")
	private DZFDouble nnum;
	@JsonProperty("je")
	private  DZFDouble nymny;
	@JsonProperty("cb")
	private  DZFDouble ncost;
	@JsonProperty("bz")
	private  String memo;
	@JsonProperty("extime")
	private String ts;
	private Integer dr;
	@JsonProperty("v1")
	private String vdef1;
	@JsonProperty("v2")
	private String vdef2;
	@JsonProperty("v3")
	private String vdef3;
	@JsonProperty("v4")
	private String vdef4;
	@JsonProperty("v5")
	private String vdef5;
	@JsonProperty("v6")
	private String vdef6;
	@JsonProperty("v7")
	private String vdef7;
	@JsonProperty("v8")
	private String vdef8;
	@JsonProperty("v9")
	private String vdef9;
	@JsonProperty("v10")
	private String vdef10;
	private DZFDouble vdef11;
	private DZFDouble vdef12;
	private DZFDouble vdef13;
	private DZFDouble vdef14;
	private DZFDouble vdef15;
	private String vdef16;
	private String vdef17;
	private String vdef18;
	private String vdef19;
	private String vdef20;
	@JsonProperty("djrqa")
	private  String dbilldate;
	@JsonProperty("kjy")
	private  String period;
	@JsonProperty("jc")
	private String bcheck;
	
	@JsonProperty("jldwmc")
	private  String measurename;
	@JsonProperty("spmc")
	private   String inventoryname;
	@JsonProperty("spbm")
	private   String inventorycode;
	@JsonProperty("gg")
	private  String invspec;
	@JsonProperty("xh")
	private  String invtype;
	@JsonProperty("cjrq")
	private DZFDate createdate;
	@JsonProperty("kmid")
	private String pk_subject;//科目主键[必输项目]
	@JsonProperty("kmmc")
	private  String pk_subjectname;
	@JsonProperty("kmbm")
	private String pk_subjectcode;//科目编码
	@JsonProperty("splxmc")
	private  String inventorytype;

	@JsonProperty("dj")
	private DZFDouble nprice;//单价  展示使用
	
	public String getPk_subject() {
		return pk_subject;
	}
	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}
	public DZFDate getCreatedate() {
		return createdate;
	}
	public void setCreatedate(DZFDate createdate) {
		this.createdate = createdate;
	}
	@Column(name = "BCHECK")	
	public String getBcheck() {
		return bcheck;
	}
	public void setBcheck(String bcheck) {
		this.bcheck = bcheck;
	}
	
	@Column(name = "PERIOD")
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
	@Column(name = "DBILLDATE")
	public String getDbilldate() {
		return dbilldate;
	}
	public void setDbilldate(String dbilldate) {
		this.dbilldate = dbilldate;
	}
	
	@Column(name = "CREATETIME")
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
	@Column(name = "CREATOR")
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	@Id
	@Column(name = "PK_ICBALANCE",nullable=false,unique=true)
	public String getPk_icbalance() {
		return pk_icbalance;
	}
	public void setPk_icbalance(String pk_icbalance) {
		this.pk_icbalance = pk_icbalance;
	}
	
	@Column(name = "PK_INVENTORY")
	public String getPk_inventory() {
		return pk_inventory;
	}
	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	
	@Column(name = "PK_CORP")
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	
	@Column(name = "PK_VOUCHER")
	public String getPk_voucher() {
		return pk_voucher;
	}
	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}
	
	@Column(name = "PK_CURRENCY")
	public String getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	
	@Column(name = "CBILLTYPE")
	public String getCbilltype() {
		return cbilltype;
	}
	public void setCbilltype(String cbilltype) {
		this.cbilltype = cbilltype;
	}
	
	@Column(name = "NNUM")
	public DZFDouble getNnum() {
		return nnum;
	}
	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}
	
	@Column(name = "NYMNY")
	public DZFDouble getNymny() {
		return nymny;
	}
	public void setNymny(DZFDouble nymny) {
		this.nymny = nymny;
	}
	
	@Column(name = "NCOST")
	public DZFDouble getNcost() {
		return ncost;
	}
	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}
	
	@Column(name = "MEMO")
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	@Column(name = "TS")
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	
	@Column(name = "DR")
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	
	@Column(name = "VDEF1")
	public String getVdef1() {
		return vdef1;
	}
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}
	
	@Column(name = "VDEF2")
	public String getVdef2() {
		return vdef2;
	}
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}
	
	@Column(name = "VDEF3")
	public String getVdef3() {
		return vdef3;
	}
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}
	
	@Column(name = "VDEF4")
	public String getVdef4() {
		return vdef4;
	}
	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}
	
	@Column(name = "VDEF5")
	public String getVdef5() {
		return vdef5;
	}
	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}
	
	@Column(name = "VDEF6")
	public String getVdef6() {
		return vdef6;
	}
	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}
	
	@Column(name = "VDEF7")
	public String getVdef7() {
		return vdef7;
	}
	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}
	
	@Column(name = "VDEF8")
	public String getVdef8() {
		return vdef8;
	}
	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}
	
	@Column(name = "VDEF9")
	public String getVdef9() {
		return vdef9;
	}
	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}
	
	@Column(name = "VDEF10")
	public String getVdef10() {
		return vdef10;
	}
	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}
	
	@Column(name = "VDEF11")
	public DZFDouble getVdef11() {
		return vdef11;
	}
	public void setVdef11(DZFDouble vdef11) {
		this.vdef11 = vdef11;
	}
	
	@Column(name = "VDEF12")
	public DZFDouble getVdef12() {
		return vdef12;
	}
	public void setVdef12(DZFDouble vdef12) {
		this.vdef12 = vdef12;
	}
	
	@Column(name = "VDEF13")
	public DZFDouble getVdef13() {
		return vdef13;
	}
	public void setVdef13(DZFDouble vdef13) {
		this.vdef13 = vdef13;
	}
	
	@Column(name = "VDEF14")
	public DZFDouble getVdef14() {
		return vdef14;
	}
	public void setVdef14(DZFDouble vdef14) {
		this.vdef14 = vdef14;
	}
	
	@Column(name = "VDEF15")
	public DZFDouble getVdef15() {
		return vdef15;
	}
	public void setVdef15(DZFDouble vdef15) {
		this.vdef15 = vdef15;
	}
	
	@Column(name = "VDEF16")
	public String getVdef16() {
		return vdef16;
	}
	public void setVdef16(String vdef16) {
		this.vdef16 = vdef16;
	}
	
	@Column(name = "VDEF17")
	public String getVdef17() {
		return vdef17;
	}
	public void setVdef17(String vdef17) {
		this.vdef17 = vdef17;
	}
	
	@Column(name = "VDEF18")
	public String getVdef18() {
		return vdef18;
	}
	public void setVdef18(String vdef18) {
		this.vdef18 = vdef18;
	}
	
	@Column(name = "VDEF19")
	public String getVdef19() {
		return vdef19;
	}
	public void setVdef19(String vdef19) {
		this.vdef19 = vdef19;
	}
	
	@Column(name = "VDEF20")
	public String getVdef20() {
		return vdef20;
	}
	public void setVdef20(String vdef20) {
		this.vdef20 = vdef20;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return "pk_icbalance";
	}
	@Override
	public String getTableName() {
		return "ynt_icbalance";
	}
	public String getInventoryname() {
		return inventoryname;
	}
	public void setInventoryname(String inventoryname) {
		this.inventoryname = inventoryname;
	}
	public String getInvspec() {
		return invspec;
	}
	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}
	public String getInvtype() {
		return invtype;
	}
	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}
	public String getMeasurename() {
		return measurename;
	}
	public void setMeasurename(String measurename) {
		this.measurename = measurename;
	}
	public String getPk_subjectname() {
		return pk_subjectname;
	}
	public void setPk_subjectname(String pk_subjectname) {
		this.pk_subjectname = pk_subjectname;
	}
	public String getInventorytype() {
		return inventorytype;
	}
	public void setInventorytype(String inventorytype) {
		this.inventorytype = inventorytype;
	}
	public String getTittleperiod() {
		return tittleperiod;
	}
	public void setTittleperiod(String tittleperiod) {
		this.tittleperiod = tittleperiod;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}
	
	public String getDjrq() {
		return djrq;
	}
	public void setDjrq(String djrq) {
		this.djrq = djrq;
	}
	public String getInventorycode() {
		return inventorycode;
	}
	public void setInventorycode(String inventorycode) {
		this.inventorycode = inventorycode;
	}
	public DZFDouble getNprice() {
		return nprice;
	}
	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}
	public String getPk_subjectcode() {
		return pk_subjectcode;
	}
	public void setPk_subjectcode(String pk_subjectcode) {
		this.pk_subjectcode = pk_subjectcode;
	}
	
}
