package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.base.model.SuperVO;

import java.util.List;

public class ImgGroupRsBean extends SuperVO {

	private static final long serialVersionUID = 8152313851350667140L;
	
	private String groupKey;
	private String groupCode;
	private String pk_corp;
	private String groupsession;//上传批次
	private String memo;//摘要
	private String paymethod;//结算方式
	private String mny;//金额
	private String groupState;//图片组状态
	private String backReason;//退回原因
	
	private List<String> liburls;//图片路径
	private ImageRecordVO[] imgrecord;//图片详情
	private String approvemsg;// 图片的审核信息
	private String bhand;// 是否待处理
	
	public List<String> getLiburls() {
		return liburls;
	}
	public void setLiburls(List<String> liburls) {
		this.liburls = liburls;
	}
	
	public ImageRecordVO[] getImgrecord() {
		return imgrecord;
	}
	public void setImgrecord(ImageRecordVO[] imgrecord) {
		this.imgrecord = imgrecord;
	}
	public String getBackReason() {
		return backReason;
	}
	public void setBackReason(String backReason) {
		this.backReason = backReason;
	}
	public String getGroupState() {
		return groupState;
	}
	public void setGroupState(String groupState) {
		this.groupState = groupState;
	}
	public String getGroupsession() {
		return groupsession;
	}
	public void setGroupsession(String groupsession) {
		this.groupsession = groupsession;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getMny() {
		return mny;
	}
	public void setMny(String mny) {
		this.mny = mny;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getGroupKey() {
		return groupKey;
	}
	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getApprovemsg() {
		return approvemsg;
	}
	public void setApprovemsg(String approvemsg) {
		this.approvemsg = approvemsg;
	}
	public String getBhand() {
		return bhand;
	}
	public void setBhand(String bhand) {
		this.bhand = bhand;
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
