package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 消息分类
 * @author zhangj
 *
 */
public class MessageTypeResponVo extends SuperVO {

	private Integer msgtype;//消息类型
	private String msgtypename;//消息类型名称
	private Integer unrcount;//某个类型的未读数量
	private String lastdate;//最新的时间
	private String newmsg;//最新消息
	

	public String getNewmsg() {
		return newmsg;
	}

	public void setNewmsg(String newmsg) {
		this.newmsg = newmsg;
	}

	public String getLastdate() {
		return lastdate;
	}

	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

	public Integer getUnrcount() {
		return unrcount;
	}

	public void setUnrcount(Integer unrcount) {
		this.unrcount = unrcount;
	}

	public Integer getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(Integer msgtype) {
		this.msgtype = msgtype;
	}

	public String getMsgtypename() {
		return msgtypename;
	}

	public void setMsgtypename(String msgtypename) {
		this.msgtypename = msgtypename;
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
