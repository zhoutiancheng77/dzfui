package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 个性化设置，前台交互，生成解析XML
 */
public class GxhszVO extends SuperVO {
	@JsonProperty("id")
	private String pk_personal;
	@JsonProperty("print")
	//打印类型
	private Integer printType;
	@JsonProperty("bbkm")
	//报表科目显示方式
	private Integer subjectShow;
	@JsonProperty("pzkm")
	//科目名称显示方式 0本级 1一级加末级 3全称
	private Integer pzSubject;
	@JsonProperty("balance")
	//资产负债表默认类型
	private Integer balanceSheet;
	@JsonProperty("style")
	private Integer  uploadstyle;
	//制单时显示科目余额 0不显示 1显示
	@JsonProperty("kmye")
	private Integer balanceShow;
	// 制单时显示科目数量 0不显示 1显示
	private Integer subject_num_show;

	//票通生成凭证方式 0自动1手工
	private Integer pt_gen_vch;
	
	//一键取票生成出入库、凭证方式  0自动 1手工
	@JsonProperty("yjqpway")
	private Integer yjqp_gen_vch;
	
	//是否显示最后修改时间
	@JsonProperty("lastmodifytime")
	private Integer isshowlastmodifytime;
	
	public String getPk_personal() {
		return pk_personal;
	}
	public void setPk_personal(String pk_personal) {
		this.pk_personal = pk_personal;
	}
	public Integer getPrintType() {
		return printType;
	}
	public void setPrintType(Integer printType) {
		this.printType = printType;
	}
	public Integer getSubjectShow() {
		return subjectShow;
	}
	public void setSubjectShow(Integer subjectShow) {
		this.subjectShow = subjectShow;
	}
	public Integer getPzSubject() {
		return pzSubject;
	}
	public void setPzSubject(Integer pzSubject) {
		this.pzSubject = pzSubject;
	}
	public Integer getBalanceSheet() {
		return balanceSheet;
	}
	public void setBalanceSheet(Integer balanceSheet) {
		this.balanceSheet = balanceSheet;
	}
	public Integer getUploadstyle() {
		return uploadstyle;
	}
	public void setUploadstyle(Integer uploadstyle) {
		this.uploadstyle = uploadstyle;
	}
	public Integer getBalanceShow() {
		return balanceShow;
	}
	public void setBalanceShow(Integer balanceShow) {
		this.balanceShow = balanceShow;
	}
	public Integer getSubject_num_show() {
		return subject_num_show;
	}
	public void setSubject_num_show(Integer subject_num_show) {
		this.subject_num_show = subject_num_show;
	}
	public Integer getPt_gen_vch() {
		return pt_gen_vch;
	}
	public void setPt_gen_vch(Integer pt_gen_vch) {
		this.pt_gen_vch = pt_gen_vch;
	}
	
	public Integer getYjqp_gen_vch() {
		return yjqp_gen_vch;
	}
	public void setYjqp_gen_vch(Integer yjqp_gen_vch) {
		this.yjqp_gen_vch = yjqp_gen_vch;
	}
	public Integer getIsshowlastmodifytime() {
		return isshowlastmodifytime;
	}
	public void setIsshowlastmodifytime(Integer isshowlastmodifytime) {
		this.isshowlastmodifytime = isshowlastmodifytime;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
}
