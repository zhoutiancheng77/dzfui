package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;

public class ImageFJDBQueryVO extends SuperVO {
	
	private String dbdate;
	private String fjdate;
	private String fjr;
	private String dbr;
	private String fpstylecode;
	private String fpstylename;
	private Integer photonum;
	private String groupid;
	private String pk_image_group;
	
	public String getDbdate() {
		return dbdate;
	}
	public void setDbdate(String dbdate) {
		this.dbdate = dbdate;
	}
	public String getFjdate() {
		return fjdate;
	}
	public void setFjdate(String fjdate) {
		this.fjdate = fjdate;
	}
	public String getFjr() {
		return fjr;
	}
	public void setFjr(String fjr) {
		this.fjr = fjr;
	}
	public String getDbr() {
		return dbr;
	}
	public void setDbr(String dbr) {
		this.dbr = dbr;
	}
	public String getFpstylecode() {
		return fpstylecode;
	}
	public void setFpstylecode(String fpstylecode) {
		this.fpstylecode = fpstylecode;
	}
	public String getFpstylename() {
		return fpstylename;
	}
	public void setFpstylename(String fpstylename) {
		this.fpstylename = fpstylename;
	}
	public Integer getPhotonum() {
		return photonum;
	}
	public void setPhotonum(Integer photonum) {
		this.photonum = photonum;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getPk_image_group() {
		return pk_image_group;
	}
	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
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
