package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFBoolean;

/**
 * 图片请求vo
 * 
 * @author zhangj
 *
 */
public class ImageReqVO extends UserBeanVO {

	private String[] corpids;

	private String startdate;// 开始日期
	private String enddate;// 结束日期
	private String period;// 期间
	private String beginperiod;// 开始期间
	private String endperiod;// 结束期间
	private String groupsession;// 图片批次信息
	private String img_state;//图片状态 0 未处理 80 已退回，100 已制单   其他，处理中
	private DZFBoolean isshowapprove;// 是否显示审核

	public DZFBoolean getIsshowapprove() {
		return isshowapprove;
	}

	public void setIsshowapprove(DZFBoolean isshowapprove) {
		this.isshowapprove = isshowapprove;
	}

	public String getImg_state() {
		return img_state;
	}

	public void setImg_state(String img_state) {
		this.img_state = img_state;
	}

	public String[] getCorpids() {
		return corpids;
	}

	public void setCorpids(String[] corpids) {
		this.corpids = corpids;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getBeginperiod() {
		return beginperiod;
	}

	public void setBeginperiod(String beginperiod) {
		this.beginperiod = beginperiod;
	}

	public String getEndperiod() {
		return endperiod;
	}

	public void setEndperiod(String endperiod) {
		this.endperiod = endperiod;
	}

	public String getGroupsession() {
		return groupsession;
	}

	public void setGroupsession(String groupsession) {
		this.groupsession = groupsession;
	}

}
