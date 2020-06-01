package com.dzf.zxkj.app.model.app;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 扫码VO
 * @author wangzhn
 *
 */
public class LoginQRVO extends SuperVO {
	
	private String pk_login_qrcode;
	private String coperatorid;//操作人
	private String pk_corp;//公司
	private String modules;//模块名
	private String uuid;//唯一标识
	private String loginip;//登录ip
	private int istatus;//状态
	private String sessionid;//sessionid
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_login_qrcode() {
		return pk_login_qrcode;
	}

	public void setPk_login_qrcode(String pk_login_qrcode) {
		this.pk_login_qrcode = pk_login_qrcode;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLoginip() {
		return loginip;
	}

	public void setLoginip(String loginip) {
		this.loginip = loginip;
	}

	public int getIstatus() {
		return istatus;
	}

	public void setIstatus(int istatus) {
		this.istatus = istatus;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
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
		return "pk_login_qrcode";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_login_qrcode";
	}

}
