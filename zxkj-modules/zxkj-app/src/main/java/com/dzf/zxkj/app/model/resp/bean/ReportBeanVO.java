package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.lang.DZFBoolean;

public class ReportBeanVO extends UserBeanVO {

	private String startdate;
	private String enddate;

	private String period;
	private String beginperiod;
	private String endperiod;
	private String kmsx;// 科目属性
	private String groupsession;

	private String[] corpids;

	private DZFBoolean isshowapprove;// 是否显示审核
	private String id;// 表id
	private String year;//查询年度
	private String jd;//查询季度

	private String fzlb;



	public String[] getCorpids() {
		return corpids;
	}

	public void setCorpids(String[] corpids) {
		this.corpids = corpids;
	}

	public DZFBoolean getIsshowapprove() {
		return isshowapprove;
	}

	public void setIsshowapprove(DZFBoolean isshowapprove) {
		this.isshowapprove = isshowapprove;
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

	public String getKmsx() {
		return kmsx;
	}

	public void setKmsx(String kmsx) {
		this.kmsx = kmsx;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public String getGroupsession() {
		return groupsession;
	}

	public void setGroupsession(String groupsession) {
		this.groupsession = groupsession;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getJd() {
		return jd;
	}

	public void setJd(String jd) {
		this.jd = jd;
	}

	public String getFzlb() {
		return fzlb;
	}

	public void setFzlb(String fzlb) {
		this.fzlb = fzlb;
	}
}
