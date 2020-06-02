package com.dzf.zxkj.app.model.app.corp;


import com.dzf.zxkj.common.model.SuperVO;

public class CorpBean extends SuperVO {

	private String corpnm;
	private String corpid;
	private String pk_org;
	private String orgname;
	private String tel;
	private String customsvid;
	private String customservice;
	private String phone;
	private String serviceitem;
	private String customintrodu;
//	private String rescode ;
//	private String resmsg ;
	private String wechat;
	
	
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getCustomsvid() {
		return customsvid;
	}
	public void setCustomsvid(String customsvid) {
		this.customsvid = customsvid;
	}
	public String getCustomservice() {
		return customservice;
	}
	public void setCustomservice(String customservice) {
		this.customservice = customservice;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getServiceitem() {
		return serviceitem;
	}
	public void setServiceitem(String serviceitem) {
		this.serviceitem = serviceitem;
	}
	public String getCustomintrodu() {
		return customintrodu;
	}
	public void setCustomintrodu(String customintrodu) {
		this.customintrodu = customintrodu;
	}
//	public String getRescode() {
//		return rescode;
//	}
//	public void setRescode(String rescode) {
//		this.rescode = rescode;
//	}
//	public String getResmsg() {
//		return resmsg;
//	}
//	public void setResmsg(String resmsg) {
//		this.resmsg = resmsg;
//	}
	public String getCorpnm() {
		return corpnm;
	}
	public void setCorpnm(String corpnm) {
		this.corpnm = corpnm;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
