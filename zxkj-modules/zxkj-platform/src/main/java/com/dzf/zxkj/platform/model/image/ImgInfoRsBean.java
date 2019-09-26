package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;

public class ImgInfoRsBean  extends SuperVO {

	private static final long serialVersionUID = 8152313851350667140L;
	
	private String groupKey;
	private String imageKey;
	private String imageName;
	private String imagePath;
	private String createdBy;
	private String createdOn;
	private String imageState;
	

	public String getImageState() {
		return imageState;
	}
	public void setImageState(String imageState) {
		this.imageState = imageState;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
	public String getGroupKey() {
		return groupKey;
	}
	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}
	public String getImageKey() {
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
