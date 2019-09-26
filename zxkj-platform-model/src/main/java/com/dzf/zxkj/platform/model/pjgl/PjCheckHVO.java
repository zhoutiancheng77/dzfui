package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 票据检查主表
 * @author wangzhn
 *
 */
public class PjCheckHVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_bill_check_h;//主键
	@JsonProperty("custid")
	private String pk_customer;//客户id
	private String coperatorid;//操作人
	private String doperatedate;//操作时间
	@JsonProperty("qj")
	private String period;//期间	
	@JsonProperty("qpfs")
	private Integer colltype;//取票方式
	@JsonProperty("yqc")
	private DZFBoolean isclearcard;//清卡
	@JsonProperty("ycs")
	private DZFBoolean iscopytax;//抄税
	@JsonProperty("kpzt")
	private Integer kpstatus;//开票状态
	@JsonProperty("pjzzs")
	private Integer totalnum;//票据总张数
	@JsonProperty("pkzje")
	private DZFDouble totalmny;//票据总金额
	
	private String modifyid;//修改人
	private DZFDateTime modifydatetime;//修改时间
	private String vdef1;//自定义1
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	@JsonProperty("corpid")
	private String pk_corp;
	private Integer dr;
	private String ts;
	
	/******************供展示使用**************/
	private String khname;
	private String chname;
	private String linkman;//客户联系人
	private String linkphoto;//客户联系方式
	
	public String getPk_bill_check_h() {
		return pk_bill_check_h;
	}

	public String getPk_customer() {
		return pk_customer;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public String getDoperatedate() {
		return doperatedate;
	}

	public String getPeriod() {
		return period;
	}

	public Integer getColltype() {
		return colltype;
	}

	public DZFBoolean getIsclearcard() {
		return isclearcard;
	}

	public DZFBoolean getIscopytax() {
		return iscopytax;
	}

	public Integer getTotalnum() {
		return totalnum;
	}

	public DZFDouble getTotalmny() {
		return totalmny;
	}

	public String getVdef1() {
		return vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getTs() {
		return ts;
	}

	public void setPk_bill_check_h(String pk_bill_check_h) {
		this.pk_bill_check_h = pk_bill_check_h;
	}

	public void setPk_customer(String pk_customer) {
		this.pk_customer = pk_customer;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setColltype(Integer colltype) {
		this.colltype = colltype;
	}

	public void setIsclearcard(DZFBoolean isclearcard) {
		this.isclearcard = isclearcard;
	}

	public void setIscopytax(DZFBoolean iscopytax) {
		this.iscopytax = iscopytax;
	}

	public void setTotalnum(Integer totalnum) {
		this.totalnum = totalnum;
	}

	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}

	public String getModifyid() {
		return modifyid;
	}

	public void setModifyid(String modifyid) {
		this.modifyid = modifyid;
	}

	public DZFDateTime getModifydatetime() {
		return modifydatetime;
	}

	public void setModifydatetime(DZFDateTime modifydatetime) {
		this.modifydatetime = modifydatetime;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public Integer getKpstatus() {
		return kpstatus;
	}

	public Integer getDr() {
		return dr;
	}

	public String getKhname() {
		return khname;
	}

	public String getChname() {
		return chname;
	}

	public String getLinkman() {
		return linkman;
	}

	public String getLinkphoto() {
		return linkphoto;
	}

	public void setKpstatus(Integer kpstatus) {
		this.kpstatus = kpstatus;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setKhname(String khname) {
		this.khname = khname;
	}

	public void setChname(String chname) {
		this.chname = chname;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public void setLinkphoto(String linkphoto) {
		this.linkphoto = linkphoto;
	}

	@Override
	public String getPKFieldName() {
		return "pk_bill_check_h";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_bill_check_h";
	}

}
