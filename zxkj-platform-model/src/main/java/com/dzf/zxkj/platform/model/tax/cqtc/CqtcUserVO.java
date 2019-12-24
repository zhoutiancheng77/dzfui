package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
    * Cqtc_user 实体类
    * Tue Aug 15 11:19:23 CST 2017 qixiang
    */ 


public class CqtcUserVO extends SuperVO {
	
	private String pk_cqtcuser;
	private String cuserid;
	private String openid;
	private DZFDateTime logintime;
	private Integer dr;
	private DZFDateTime ts;
	private int timeout;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setPk_cqtcuser(String pk_cqtcuser){
		this.pk_cqtcuser=pk_cqtcuser;
	}

	public String getPk_cqtcuser(){
		return pk_cqtcuser;
	}

	public void setCuserid(String cuserid){
		this.cuserid=cuserid;
	}

	public String getCuserid(){
		return cuserid;
	}

	public void setOpenid(String openid){
		this.openid=openid;
	}

	public String getOpenid(){
		return openid;
	}

	public void setLogintime(DZFDateTime logintime){
		this.logintime=logintime;
	}

	public DZFDateTime getLogintime(){
		return logintime;
	}

	public void setDr(Integer dr){
		this.dr=dr;
	}

	public Integer getDr(){
		return dr;
	}

	public void setTs(DZFDateTime ts){
		this.ts=ts;
	}

	public DZFDateTime getTs(){
		return ts;
	}
	
	@Override
	public String getParentPKFieldName() {
		return "";
	}
	
	@Override
	public String getPKFieldName() {
		return "pk_cqtcuser";
	}
	
	@Override
	public String getTableName()  {
		return "CQTC_USER";
	}
	
}

