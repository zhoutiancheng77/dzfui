package com.dzf.zxkj.app.model.app;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 意见反馈
 * 
 * @author dzf
 *
 */
public class OpinionVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	private final String TABLE_NAME = "YNT_OPINION";

	@JsonProperty("pk_id")
	private String pk_opinion; // 主键

	@JsonProperty("corpid")
	private String pk_corp; // 所属分部

	@JsonProperty("sdman")
	private String sendman;// 反馈人

	@JsonProperty("sdmanname")
	private String sendmanname;//反馈人姓名
	
	@JsonProperty("dsdate")
	private String dsenddate; // 反馈时间
	
	@JsonProperty("contact")
	private String vcontact;//联系方式

	@JsonProperty("ssend")
	private String sys_send;// 发送端
	
	@JsonProperty("ssname")
	private String sys_sendname;// 发送端

	@JsonProperty("content")
	private String vcontent; // 反馈内容
	
	@JsonProperty("reply")
	private String vreply;//回复

	@JsonProperty("dr")
	private Integer dr; // 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts; // 时间戳
	
	@JsonProperty("bdate")
	private String bdate; // 开始时间 查询
	
	@JsonProperty("edate")
	private String edate; // 结束时间 查询
	
	@JsonProperty("tcorp")
	private String pk_temp_corp;//临时公司主键
	
	public String getSys_sendname() {
		return sys_sendname;
	}

	public void setSys_sendname(String sys_sendname) {
		this.sys_sendname = sys_sendname;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getVreply() {
		return vreply;
	}

	public void setVreply(String vreply) {
		this.vreply = vreply;
	}

	public String getPk_opinion() {
		return pk_opinion;
	}

	public void setPk_opinion(String pk_opinion) {
		this.pk_opinion = pk_opinion;
	}

	public String getDsenddate() {
		return dsenddate;
	}

	public void setDsenddate(String dsenddate) {
		this.dsenddate = dsenddate;
	}

	public String getVcontact() {
		return vcontact;
	}

	public void setVcontact(String vcontact) {
		this.vcontact = vcontact;
	}

	public String getSendmanname() {
		return sendmanname;
	}

	public void setSendmanname(String sendmanname) {
		this.sendmanname = sendmanname;
	}

	
	public String getSendman() {
		return sendman;
	}

	public void setSendman(String sendman) {
		this.sendman = sendman;
	}

	public String getSys_send() {
		return sys_send;
	}

	public void setSys_send(String sys_send) {
		this.sys_send = sys_send;
	}



	public String getVcontent() {
		return vcontent;
	}

	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
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
	
	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_opinion";
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
