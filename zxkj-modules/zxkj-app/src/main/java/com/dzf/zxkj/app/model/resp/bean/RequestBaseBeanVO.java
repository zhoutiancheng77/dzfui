package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 接受客户端Bean基类，满足客户端与服务器常用字段传输，基本JAVAbean可以直接使用
 * @author liangjy
 *
 */
public class RequestBaseBeanVO extends SuperVO {
	//登录用户ID
	private String account_id ;
	//用户编码
	private String account;
	//登录用户所属公司主键
	@JsonProperty("corp")
	private String pk_corp ;
	//用户令牌
	private String token;
	//操作标识
	private String operate;
	//系统类型
	private String systype;
	//操作类型，Servlet类型+手机类型+操作类型
	private String optype;
	//手机端传过来的json
	private String json;
	private Integer versionno;
	public Integer getVersionno() {
		return versionno;
	}
	public void setVersionno(Integer versionno) {
		this.versionno = versionno;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getSystype() {
		return systype;
	}
	public void setSystype(String systype) {
		this.systype = systype;
	}
	public String getOperate() {
		return operate;
	}
	public void setOperate(String operate) {
		this.operate = operate;
	}
	public String getAccount_id() {
		return account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
