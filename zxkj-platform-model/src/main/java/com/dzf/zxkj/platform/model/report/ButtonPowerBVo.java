package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class ButtonPowerBVo extends SuperVO {

	public static final String TABLE_NAME = "sm_power_button_b";

	public static final String PK_FIELD = "pk_power_button_b";

	private String pk_power_button_b;//
	private String pk_power_button;//
	private String pk_account;//
	private String pk_corpk;//
	private Integer ibtnstatus;//0 正常 1 关闭
	private String pk_corp;//
	private Integer dr;//
	private DZFDateTime ts;//
	private String vmemo;//备注
	

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_power_button() {
		return pk_power_button;
	}

	public void setPk_power_button(String pk_power_button) {
		this.pk_power_button = pk_power_button;
	}

	public String getPk_account() {
		return pk_account;
	}

	public void setPk_account(String pk_account) {
		this.pk_account = pk_account;
	}

	public Integer getIbtnstatus() {
		return ibtnstatus;
	}

	public void setIbtnstatus(Integer ibtnstatus) {
		this.ibtnstatus = ibtnstatus;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getPk_power_button_b() {
		return pk_power_button_b;
	}

	public void setPk_power_button_b(String pk_power_button_b) {
		this.pk_power_button_b = pk_power_button_b;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
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
