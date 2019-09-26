package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 报销单VO
 */
public class ExpBillHVO extends SuperVO {
	@JsonProperty("id")
	private String pk_expbill_h;
	private String pk_corp;
	//生成的凭证id
	private String pk_voucher;
	//接收的报销单ID
	private String bill_id;
	//申请人
	private String pk_applicant;
	@JsonProperty("sqr")
	private String applicant;
	//申请人电话
	private String aplphone;
	//金额
	private DZFDouble mny;
	//大写金额
	private String chinesemny;
	//上传日期
	@JsonProperty("scsj")
	private DZFDate submitdate;
	//报销日期
	@JsonProperty("bxsj")
	private DZFDate reimbursedate;
	//支付方式
	private Integer paytype;
	//支付日期
	private DZFDateTime paydate;
	//状态
	@JsonProperty("zt")
	private Integer billstatus;
	//摘要
	@JsonProperty("zy")
	private String summary;
	//备注
	@JsonProperty("bz")
	private String memo;
	//附单据数
	private Integer nbills;
	private Integer dr;
	
	public String getPk_expbill_h() {
		return pk_expbill_h;
	}
	public void setPk_expbill_h(String pk_expbill_h) {
		this.pk_expbill_h = pk_expbill_h;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_voucher() {
		return pk_voucher;
	}
	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}
	public String getBill_id() {
		return bill_id;
	}
	public void setBill_id(String bill_id) {
		this.bill_id = bill_id;
	}
	public String getPk_applicant() {
		return pk_applicant;
	}
	public void setPk_applicant(String pk_applicant) {
		this.pk_applicant = pk_applicant;
	}
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public DZFDouble getMny() {
		return mny;
	}
	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getNbills() {
		return nbills;
	}
	public void setNbills(Integer nbills) {
		this.nbills = nbills;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_expbill_h";
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_expbill_h";
	}
	public String getAplphone() {
		return aplphone;
	}
	public void setAplphone(String aplphone) {
		this.aplphone = aplphone;
	}
	public String getChinesemny() {
		return chinesemny;
	}
	public void setChinesemny(String chinesemny) {
		this.chinesemny = chinesemny;
	}
	public DZFDate getSubmitdate() {
		return submitdate;
	}
	public void setSubmitdate(DZFDate submitdate) {
		this.submitdate = submitdate;
	}
	public DZFDate getReimbursedate() {
		return reimbursedate;
	}
	public void setReimbursedate(DZFDate reimbursedate) {
		this.reimbursedate = reimbursedate;
	}
	public Integer getPaytype() {
		return paytype;
	}
	public void setPaytype(Integer paytype) {
		this.paytype = paytype;
	}
	public DZFDateTime getPaydate() {
		return paydate;
	}
	public void setPaydate(DZFDateTime paydate) {
		this.paydate = paydate;
	}
	public Integer getBillstatus() {
		return billstatus;
	}
	public void setBillstatus(Integer billstatus) {
		this.billstatus = billstatus;
	}
	
	
}
