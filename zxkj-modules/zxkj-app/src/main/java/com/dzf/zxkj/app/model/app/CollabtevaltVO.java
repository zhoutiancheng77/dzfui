package com.dzf.zxkj.app.model.app;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 合作评价VO
 * @author liangyi
 *
 */
public class CollabtevaltVO extends SuperVO {
	
	private String pk_collabtevalt ;
	private String pk_user  ;
	private String pk_corp;
	private String pk_parent;
	private Integer busitype  ;
	private String item  ;
	private String des  ;
	private String message;
	private DZFDate busidate;
	private Integer dr;
	private DZFDateTime ts;
	private String pk_tempcorp;
	private String certctnum;//联系方式
	private String newType;//业务类型
	private String pk_image_group;//上次征兆的key
	
	
	@JsonProperty("ztpj")
	private Integer satisfaction;// 总体评价
	@JsonProperty("yxmc")
	private String appraisalnames;// 印象名称 
	@JsonProperty("fwtd")
	private Integer severbearing ;// 服务态度
	@JsonProperty("zysp")
	private Integer  specialty  ; // 专业水平
	@JsonProperty("jsx")
	private Integer  betimes  ;// 及时性
	
	private String servicexm;//服务项目(不清楚哪里使用)
	
	private String vbusiname;//服务项目
	
	private String pk_bill;//
	
	private String itype;//
	
	public String getVbusiname() {
		return vbusiname;
	}
	public void setVbusiname(String vbusiname) {
		this.vbusiname = vbusiname;
	}
	public String getPk_bill() {
		return pk_bill;
	}
	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}
	public String getItype() {
		return itype;
	}
	public void setItype(String itype) {
		this.itype = itype;
	}
	public String getServicexm() {
		return servicexm;
	}
	public void setServicexm(String servicexm) {
		this.servicexm = servicexm;
	}
	public String getCertctnum() {
		return certctnum;
	}
	public void setCertctnum(String certctnum) {
		this.certctnum = certctnum;
	}
	public DZFDate getBusidate() {
		return busidate;
	}
	public void setBusidate(DZFDate busidate) {
		this.busidate = busidate;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_parent() {
		return pk_parent;
	}
	public void setPk_parent(String pk_parent) {
		this.pk_parent = pk_parent;
	}
	public String getPk_collabtevalt() {
		return pk_collabtevalt;
	}
	public void setPk_collabtevalt(String pk_collabtevalt) {
		this.pk_collabtevalt = pk_collabtevalt;
	}
	public String getPk_user() {
		return pk_user;
	}
	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}
	public Integer getBusitype() {
		return busitype;
	}
	public void setBusitype(Integer busitype) {
		this.busitype = busitype;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
		return "pk_collabtevalt";
	}
	@Override
	public String getTableName() {
		return "app_collabtevalt";
	}
	
	public String getPk_tempcorp() {
		return pk_tempcorp;
	}
	public void setPk_tempcorp(String pk_tempcorp) {
		this.pk_tempcorp = pk_tempcorp;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getNewType() {
		return newType;
	}
	public void setNewType(String newType) {
		this.newType = newType;
	}
	public String getPk_image_group() {
		return pk_image_group;
	}
	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}
	public Integer getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(Integer satisfaction) {
		this.satisfaction = satisfaction;
	}
	public String getAppraisalnames() {
		return appraisalnames;
	}
	public void setAppraisalnames(String appraisalnames) {
		this.appraisalnames = appraisalnames;
	}
	public Integer getSeverbearing() {
		return severbearing;
	}
	public void setSeverbearing(Integer severbearing) {
		this.severbearing = severbearing;
	}
	public Integer getSpecialty() {
		return specialty;
	}
	public void setSpecialty(Integer specialty) {
		this.specialty = specialty;
	}
	public Integer getBetimes() {
		return betimes;
	}
	public void setBetimes(Integer betimes) {
		this.betimes = betimes;
	}
	
}
