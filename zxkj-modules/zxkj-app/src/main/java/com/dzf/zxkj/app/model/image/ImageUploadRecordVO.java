package com.dzf.zxkj.app.model.image;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.image.ImgGroupRsBean;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageUploadRecordVO extends ImgGroupRsBean {

	private String up_ope;// 上传人
	private String up_cname;// 上传公司名称
	private String up_corpid;// 上传公司id
	private String uploadtime;// 上传时间
	private Long l_uploadtime;//上传时间long类型
	private Long l_uploadtime_ser;//上传时间服务器
	private Integer ope_power;// 操作权限:   0无任何权限只能查看 ,1制单+退回, 2 重传
	private String memo1;//备注
	private String imgcounts;// 图片数量
	private String imagegroupid;// 图片组标识=YNT_IMAGE_GROUP.sessionflag
	private String reviewpath;// 概览图片URL；
	private Integer count;// 组数
	private Integer normalCount;// 正常状态组数
	private String imgState;// 进度状态
	private DZFDateTime imgtime;// 最后更新时间
	@JsonProperty("isource")
	private Integer imagesource;// 图片来源0 扫码生成的图片，1上传的图片
	private String bhand;// 是否待处理
	
	public Long getL_uploadtime_ser() {
		return l_uploadtime_ser;
	}

	public void setL_uploadtime_ser(Long l_uploadtime_ser) {
		this.l_uploadtime_ser = l_uploadtime_ser;
	}

	public Long getL_uploadtime() {
		return l_uploadtime;
	}

	public void setL_uploadtime(Long l_uploadtime) {
		this.l_uploadtime = l_uploadtime;
	}

	public String getMemo1() {
		return memo1;
	}

	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	public Integer getOpe_power() {
		return ope_power;
	}

	public void setOpe_power(Integer ope_power) {
		this.ope_power = ope_power;
	}

	public String getUp_corpid() {
		return up_corpid;
	}

	public void setUp_corpid(String up_corpid) {
		this.up_corpid = up_corpid;
	}

	public String getUp_ope() {
		return up_ope;
	}

	public void setUp_ope(String up_ope) {
		this.up_ope = up_ope;
	}

	public String getUp_cname() {
		return up_cname;
	}

	public void setUp_cname(String up_cname) {
		this.up_cname = up_cname;
	}

	public String getBhand() {
		return bhand;
	}

	public void setBhand(String bhand) {
		this.bhand = bhand;
	}

	public Integer getNormalCount() {
		return normalCount;
	}

	public void setNormalCount(Integer normalCount) {
		this.normalCount = normalCount;
	}

	public DZFDateTime getImgtime() {
		return imgtime;
	}

	public void setImgtime(DZFDateTime imgtime) {
		this.imgtime = imgtime;
	}

	public String getImgState() {
		return imgState;
	}

	public void setImgState(String imgState) {
		this.imgState = imgState;
	}

	public String getReviewpath() {
		return reviewpath;
	}

	public void setReviewpath(String reviewpath) {
		this.reviewpath = reviewpath;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getImagegroupid() {
		return imagegroupid;
	}

	public void setImagegroupid(String imagegroupid) {
		this.imagegroupid = imagegroupid;
	}

	public String getUploadtime() {
		return uploadtime;
	}

	public void setUploadtime(String uploadtime) {
		this.uploadtime = uploadtime;
	}


	public String getImgcounts() {
		return imgcounts;
	}

	public void setImgcounts(String imgcounts) {
		this.imgcounts = imgcounts;
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

	public Integer getImagesource() {
		return imagesource;
	}

	public void setImagesource(Integer imagesource) {
		this.imagesource = imagesource;
	}

}
