package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息响应信息
 * 
 * @author zhangj
 *
 */
public class MessageResponVo extends SuperVO {

	private String id;// id标识
	private String title;// 标题
	private String content;// 内容
	private String ts;
	private DZFBoolean isread;// 是否已经读取
	private String vdate;// 消息日期
	private Integer msgtype;// 消息类型
	private String msgname;// 消息名字
	private String imgpk;// 图片信息
	private String sourid;// 来源id
	private String sendname;//发送人名称
	private String revicename;//接收人名称
	private String msg_complete;// 消息是否已完成
	private String msg_yj_zt;//消息应缴状态 0 已同意，1不同意，2 待确认
	private String istatus;//消息交接状态
	private String period;// 期间
	private String pk_corp ;//代账公司id
	private String pk_corpk;// 公司信息
	@JsonProperty("fwxm")
	private String vbusiname;//服务项目
	private String fwpj_zt;//服务评价状态0 已评价， 1未评价
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFwpj_zt() {
		return fwpj_zt;
	}

	public void setFwpj_zt(String fwpj_zt) {
		this.fwpj_zt = fwpj_zt;
	}

	public String getVbusiname() {
		return vbusiname;
	}

	public void setVbusiname(String vbusiname) {
		this.vbusiname = vbusiname;
	}

	public String getSendname() {
		return sendname;
	}

	public void setSendname(String sendname) {
		this.sendname = sendname;
	}

	public String getRevicename() {
		return revicename;
	}

	public void setRevicename(String revicename) {
		this.revicename = revicename;
	}

	public String getMsg_yj_zt() {
		return msg_yj_zt;
	}

	public void setMsg_yj_zt(String msg_yj_zt) {
		this.msg_yj_zt = msg_yj_zt;
	}

	public String getIstatus() {
		return istatus;
	}

	public void setIstatus(String istatus) {
		this.istatus = istatus;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getMsg_complete() {
		return msg_complete;
	}

	public void setMsg_complete(String msg_complete) {
		this.msg_complete = msg_complete;
	}

	public String getSourid() {
		return sourid;
	}

	public void setSourid(String sourid) {
		this.sourid = sourid;
	}

	public String getImgpk() {
		return imgpk;
	}

	public void setImgpk(String imgpk) {
		this.imgpk = imgpk;
	}

	public String getMsgname() {
		return msgname;
	}

	public void setMsgname(String msgname) {
		this.msgname = msgname;
	}

	public Integer getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(Integer msgtype) {
		this.msgtype = msgtype;
	}

	public String getVdate() {
		return vdate;
	}

	public void setVdate(String vdate) {
		this.vdate = vdate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DZFBoolean getIsread() {
		return isread;
	}

	public void setIsread(DZFBoolean isread) {
		this.isread = isread;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}
}
