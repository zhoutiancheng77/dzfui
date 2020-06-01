package com.dzf.zxkj.app.model.resp.bean;

public class OrgRespBean extends ResponseBaseBeanVO {

	private String pk_zt;
	private String pk_svorg;
	private String pk_acccorp;
	private String orgname;
	private String orgaddr;
	private double longitude;
	private double latitude;
	private String tel;
	private double distance;
	private String setting;

	private String qysbh;//企业识别号
	private String memo;

	public String getQysbh() {
		return qysbh;
	}

	public void setQysbh(String qysbh) {
		this.qysbh = qysbh;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	private String orgshortname;

	public String getOrgshortname() {
		return orgshortname;
	}

	public void setOrgshortname(String orgshortname) {
		this.orgshortname = orgshortname;
	}

	public String getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}

	public String getPk_acccorp() {
		return pk_acccorp;
	}

	public void setPk_acccorp(String pk_acccorp) {
		this.pk_acccorp = pk_acccorp;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getOrgaddr() {
		return orgaddr;
	}

	public void setOrgaddr(String orgaddr) {
		this.orgaddr = orgaddr;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getPk_zt() {
		return pk_zt;
	}

	public void setPk_zt(String pk_zt) {
		this.pk_zt = pk_zt;
	}

}
