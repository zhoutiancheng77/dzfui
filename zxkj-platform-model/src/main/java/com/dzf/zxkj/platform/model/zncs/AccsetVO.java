package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 分类入账
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class AccsetVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_accset;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	private Integer dr;
	private DZFDateTime ts;
	@JsonProperty("baseid")
	private String pk_basecategory;//基础票据类别主键
	@JsonProperty("tradeid")
	private String pk_trade;//行业
	@JsonProperty("hykmid")
	private String pk_accountschema;//科目体系
	@JsonProperty("fx")
	private Integer vdirect;//入账科目方向
	@JsonProperty("mrkm")
	private String pk_accsubj;//默认入账科目
	@JsonProperty("xjkm")
	private String pk_settlementaccsubj_cash;//默认现金结算科目
	@JsonProperty("yhkm")
	private String pk_settlementaccsubj_bank;//默认银行结算科目
	@JsonProperty("wlkm")
	private String pk_settlementaccsubj_exg;//默认往来结算科目
	private String zy;//摘要
	@JsonProperty("sxkm1")
	private String pk_taxaccsubj1;//一般人已认证税行科目
	@JsonProperty("sxkm2")
	private String pk_taxaccsubj2;//一般人未认证税行科目
	@JsonProperty("sxkm3")
	private String pk_taxaccsubj3;//小规模已认证税行科目
	@JsonProperty("sxkm4")
	private String pk_taxaccsubj4;//小规模未认证税行科目
	@JsonProperty("oid")
	private String coperatorid;//操作人
	@JsonProperty("odate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("mid")
	private String cmodifyid;//修改人
	@JsonProperty("mdate")
	private DZFDate dmodifydate;//修改日期
	@JsonProperty("isenable")
	private DZFBoolean useflag;//是否启用
	@JsonProperty("eid")
	private String cenableid;//启用人
	@JsonProperty("edate")
	private DZFDate denabledate;//启用日期
	@JsonProperty("nt")
	private String note;//说明
	
	private String catalogname;//分类的组合名称
	@JsonProperty("oname")
	private String coperatorname;//操作人名称
	@JsonProperty("mname")
	private String cmodifyname;//修改人名称
	@JsonProperty("ename")
	private String cenablename;//启用人名称
	private String hyname;//行业名称
	private String txname;//科目体系名称
	private Integer jsfs;//结算方式
	private Integer hbfs;//合并方式
	private String mrname;//默认科目名称
	private String xjname;//现金科目名称
	private String yhname;//银行科目名称
	private String wlname;//往来科目名称
	private String sxname1;//一般人已认证税行科目
	private String sxname2;//一般人未认证税行科目
	private String sxname3;//小规模已认证税行科目
	private String sxname4;//小规模未认证税行科目
	
	private Integer inflag;//收方判断标志 0忽略  1个人
	
	private Integer outflag;//付方判断标志 0忽略  1个人

	
	
	
	public String getPk_taxaccsubj3() {
		return pk_taxaccsubj3;
	}

	public void setPk_taxaccsubj3(String pk_taxaccsubj3) {
		this.pk_taxaccsubj3 = pk_taxaccsubj3;
	}

	public String getPk_taxaccsubj4() {
		return pk_taxaccsubj4;
	}

	public void setPk_taxaccsubj4(String pk_taxaccsubj4) {
		this.pk_taxaccsubj4 = pk_taxaccsubj4;
	}

	public String getSxname3() {
		return sxname3;
	}

	public void setSxname3(String sxname3) {
		this.sxname3 = sxname3;
	}

	public String getSxname4() {
		return sxname4;
	}

	public void setSxname4(String sxname4) {
		this.sxname4 = sxname4;
	}

	public Integer getInflag() {
		return inflag;
	}

	public void setInflag(Integer inflag) {
		this.inflag = inflag;
	}

	public Integer getOutflag() {
		return outflag;
	}

	public void setOutflag(Integer outflag) {
		this.outflag = outflag;
	}

	public Integer getJsfs() {
		return jsfs;
	}

	public void setJsfs(Integer jsfs) {
		this.jsfs = jsfs;
	}

	public Integer getHbfs() {
		return hbfs;
	}

	public void setHbfs(Integer hbfs) {
		this.hbfs = hbfs;
	}

	public String getPk_accset() {
		return pk_accset;
	}

	public void setPk_accset(String pk_accset) {
		this.pk_accset = pk_accset;
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

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}

	public String getPk_trade() {
		return pk_trade;
	}

	public void setPk_trade(String pk_trade) {
		this.pk_trade = pk_trade;
	}

	public String getPk_accountschema() {
		return pk_accountschema;
	}

	public void setPk_accountschema(String pk_accountschema) {
		this.pk_accountschema = pk_accountschema;
	}

	public Integer getVdirect() {
		return vdirect;
	}

	public void setVdirect(Integer vdirect) {
		this.vdirect = vdirect;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getPk_settlementaccsubj_cash() {
		return pk_settlementaccsubj_cash;
	}

	public void setPk_settlementaccsubj_cash(String pk_settlementaccsubj_cash) {
		this.pk_settlementaccsubj_cash = pk_settlementaccsubj_cash;
	}

	public String getPk_settlementaccsubj_bank() {
		return pk_settlementaccsubj_bank;
	}

	public void setPk_settlementaccsubj_bank(String pk_settlementaccsubj_bank) {
		this.pk_settlementaccsubj_bank = pk_settlementaccsubj_bank;
	}

	public String getPk_settlementaccsubj_exg() {
		return pk_settlementaccsubj_exg;
	}

	public void setPk_settlementaccsubj_exg(String pk_settlementaccsubj_exg) {
		this.pk_settlementaccsubj_exg = pk_settlementaccsubj_exg;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getPk_taxaccsubj1() {
		return pk_taxaccsubj1;
	}

	public void setPk_taxaccsubj1(String pk_taxaccsubj1) {
		this.pk_taxaccsubj1 = pk_taxaccsubj1;
	}

	public String getPk_taxaccsubj2() {
		return pk_taxaccsubj2;
	}

	public void setPk_taxaccsubj2(String pk_taxaccsubj2) {
		this.pk_taxaccsubj2 = pk_taxaccsubj2;
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

	public String getCmodifyid() {
		return cmodifyid;
	}

	public void setCmodifyid(String cmodifyid) {
		this.cmodifyid = cmodifyid;
	}

	public DZFDate getDmodifydate() {
		return dmodifydate;
	}

	public void setDmodifydate(DZFDate dmodifydate) {
		this.dmodifydate = dmodifydate;
	}

	public DZFBoolean getUseflag() {
		return useflag;
	}

	public void setUseflag(DZFBoolean useflag) {
		this.useflag = useflag;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCatalogname() {
		return catalogname;
	}

	public void setCatalogname(String catalogname) {
		this.catalogname = catalogname;
	}

	public String getCoperatorname() {
		return coperatorname;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public String getCmodifyname() {
		return cmodifyname;
	}

	public void setCmodifyname(String cmodifyname) {
		this.cmodifyname = cmodifyname;
	}

	public String getCenablename() {
		return cenablename;
	}

	public void setCenablename(String cenablename) {
		this.cenablename = cenablename;
	}

	public String getHyname() {
		return hyname;
	}

	public void setHyname(String hyname) {
		this.hyname = hyname;
	}

	public String getTxname() {
		return txname;
	}

	public void setTxname(String txname) {
		this.txname = txname;
	}

	public String getMrname() {
		return mrname;
	}

	public void setMrname(String mrname) {
		this.mrname = mrname;
	}

	public String getXjname() {
		return xjname;
	}

	public void setXjname(String xjname) {
		this.xjname = xjname;
	}

	public String getYhname() {
		return yhname;
	}

	public void setYhname(String yhname) {
		this.yhname = yhname;
	}

	public String getWlname() {
		return wlname;
	}

	public void setWlname(String wlname) {
		this.wlname = wlname;
	}

	public String getSxname1() {
		return sxname1;
	}

	public void setSxname1(String sxname1) {
		this.sxname1 = sxname1;
	}

	public String getSxname2() {
		return sxname2;
	}

	public void setSxname2(String sxname2) {
		this.sxname2 = sxname2;
	}

	@Override
	public String getPKFieldName() {
		return "pk_accset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_accset";
	}
	
	
}
