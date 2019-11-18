package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息通知VO
 * 
 * @author dzf
 *
 */
public class CorpMsgVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	private final String TABLE_NAME = "ynt_corpmsg";

	@JsonProperty("pk_id")
	private String pk_corpmsg; // 主键

	@JsonProperty("corpid")
	private String pk_corp; // 所属分部

	@JsonProperty("corpkid")
	private String pk_corpk; // 所属分部
	
	@JsonProperty("sdman")
	private String sendman;//发送人

	@JsonProperty("vsdate")
	private String vsenddate; // 发送时间
	
	private String vdate;//提醒日期
	
	@JsonProperty("uid")
	private String cuserid; // 接收人

	@JsonProperty("content")
	private String vcontent; // 消息内容

	@JsonProperty("isread")
	private DZFBoolean isread;// 是否已读

	@JsonProperty("dr")
	private Integer dr; // 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts; // 时间戳
	
	private Integer msgtype;//消息类型
	
	private String msgtypename;//消息类型名称
	
	private String vtitle;//标题
	
	private String vperiod;
	
	private String user_name;
	
	public String getVdate() {
		return vdate;
	}

	public void setVdate(String vdate) {
		this.vdate = vdate;
	}

	public String getPk_corpmsg() {
		return pk_corpmsg;
	}

	public void setPk_corpmsg(String pk_corpmsg) {
		this.pk_corpmsg = pk_corpmsg;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getVtitle() {
		return vtitle;
	}

	public void setVtitle(String vtitle) {
		this.vtitle = vtitle;
	}

	public String getMsgtypename() {
		return msgtypename;
	}

	public void setMsgtypename(String msgtypename) {
		this.msgtypename = msgtypename;
	}

	public String getSendman() {
		return sendman;
	}

	public void setSendman(String sendman) {
		this.sendman = sendman;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public Integer getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(Integer msgtype) {
		this.msgtype = msgtype;
	}

	public DZFBoolean getIsread() {
		return isread;
	}

	public void setIsread(DZFBoolean isread) {
		this.isread = isread;
	}


	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getVsenddate() {
		return vsenddate;
	}

	public void setVsenddate(String vsenddate) {
		this.vsenddate = vsenddate;
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

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_corpmsg";
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
