package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountQryVO extends SuperVO {

	private static final long serialVersionUID = -2460391330185001016L;
	
	@JsonProperty("corpid")
	private String pk_corp;
	
	private String khCode;// 客户编码

	private String khName;// 客户名称

	private String[] zjStatusForMonth;// 每月记账状态数组

	private DZFBoolean isqjsyjz;// 是否损益结转

	private DZFBoolean ishasaccount;// 是否建账

	private int vouchersum;// 每月凭证总数

	private String period;// 期间

	@JsonProperty("pcount")
	public String pcountname;// 主管会计名称

	@JsonProperty("pcountid")
	public String vsuperaccount;// 主管会计主键

	private String jzstatus;//记账状态

	private String khTaxType;// 纳税人性质
	
	@JsonProperty("ckstatus")
	private String vcheckstatus;//账务检查状态

	private String yjxx; //预警信息
	
	@JsonProperty("austatus")
	private String vauditstatus;//凭证审核状态

	public String getVauditstatus() {
		return vauditstatus;
	}

	public void setVauditstatus(String vauditstatus) {
		this.vauditstatus = vauditstatus;
	}

	public String getYjxx() {
		return yjxx;
	}

	public void setYjxx(String yjxx) {
		this.yjxx = yjxx;
	}

	public String getVcheckstatus() {
		return vcheckstatus;
	}

	public void setVcheckstatus(String vcheckstatus) {
		this.vcheckstatus = vcheckstatus;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getKhCode() {
		return khCode;
	}

	public void setKhCode(String khCode) {
		this.khCode = khCode;
	}

	public String getKhName() {
		return khName;
	}

	public void setKhName(String khName) {
		this.khName = khName;
	}

	public String[] getZjStatusForMonth() {
		return zjStatusForMonth;
	}

	public void setZjStatusForMonth(String[] zjStatusForMonth) {
		this.zjStatusForMonth = zjStatusForMonth;
	}

	public DZFBoolean getIsqjsyjz() {
		return isqjsyjz;
	}

	public void setIsqjsyjz(DZFBoolean isqjsyjz) {
		this.isqjsyjz = isqjsyjz;
	}

	public DZFBoolean getIshasaccount() {
		return ishasaccount;
	}

	public void setIshasaccount(DZFBoolean ishasaccount) {
		this.ishasaccount = ishasaccount;
	}

	public int getVouchersum() {
		return vouchersum;
	}

	public void setVouchersum(int vouchersum) {
		this.vouchersum = vouchersum;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPcountname() {
		return pcountname;
	}

	public void setPcountname(String pcountname) {
		this.pcountname = pcountname;
	}

	public String getVsuperaccount() {
		return vsuperaccount;
	}

	public void setVsuperaccount(String vsuperaccount) {
		this.vsuperaccount = vsuperaccount;
	}

	public String getJzstatus() {
		return jzstatus;
	}

	public void setJzstatus(String jzstatus) {
		this.jzstatus = jzstatus;
	}

	public String getKhTaxType() {
		return khTaxType;
	}

	public void setKhTaxType(String khTaxType) {
		this.khTaxType = khTaxType;
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

}
