package com.dzf.zxkj.app.model.approve;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 审批流设置vo
 * 
 * @author zhangj
 *
 */
public class ApproveSetVo extends SuperVO {

	public static final String TABLE_NAME = "app_approve_set";
	public static final String PK_FIELDNAME = "pk_approve_set";

	private String pk_approve_set;// 主键
	@JsonProperty("corp")
	private String pk_corp;// 公司
	@JsonProperty("staff")
	private String vstaff;// 员工
	@JsonProperty("boss")
	private String vboss;// 老板
	@JsonProperty("cash")
	private String vcash;// 出纳
	@JsonProperty("use")
	private DZFBoolean buse;// 是否启用审批流
	@JsonProperty("tcorp")
	private String pk_temp_corp;// 临时公司

	public String getPk_approve_set() {
		return pk_approve_set;
	}

	public void setPk_approve_set(String pk_approve_set) {
		this.pk_approve_set = pk_approve_set;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVstaff() {
		return vstaff;
	}

	public void setVstaff(String vstaff) {
		this.vstaff = vstaff;
	}

	public String getVboss() {
		return vboss;
	}

	public void setVboss(String vboss) {
		this.vboss = vboss;
	}

	public String getVcash() {
		return vcash;
	}

	public void setVcash(String vcash) {
		this.vcash = vcash;
	}

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public DZFBoolean getBuse() {
		return buse;
	}

	public void setBuse(DZFBoolean buse) {
		this.buse = buse;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELDNAME;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
