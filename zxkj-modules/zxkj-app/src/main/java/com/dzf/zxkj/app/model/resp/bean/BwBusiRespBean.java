package com.dzf.zxkj.app.model.resp.bean;

/**
 * 百望对外接口返回值（返回值加密处理，只适合百望的处理）
 * 
 * @author zhangj
 *
 */
public class BwBusiRespBean extends ResponseBaseBeanVO {

	private String account;
	private String account_id;
	private String token;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
