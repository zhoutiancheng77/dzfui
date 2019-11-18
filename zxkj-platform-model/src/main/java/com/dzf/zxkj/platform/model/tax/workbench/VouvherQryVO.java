package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VouvherQryVO extends SuperVO {

	private static final long serialVersionUID = 2766225239718480868L;
	
	@JsonProperty("corpkid")
	private String pk_corpk;//客户主键

	@JsonProperty("auditnum")
	private Integer iauditnum;//已审核数量
	
	@JsonProperty("sumnum")
	private Integer isumnum;//总数量
	
	@JsonProperty("period")
	private String vperiod;//期间
	
	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public Integer getIauditnum() {
		return iauditnum;
	}

	public void setIauditnum(Integer iauditnum) {
		this.iauditnum = iauditnum;
	}

	public Integer getIsumnum() {
		return isumnum;
	}

	public void setIsumnum(Integer isumnum) {
		this.isumnum = isumnum;
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
