package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class RemindSetVO extends SuperVO {

	private static final long serialVersionUID = 5141227774847467506L;
	
	@JsonProperty("rsid")
	private String pk_remindset;//主键
	
	@JsonProperty("corpid")
	private String pk_corp;//会计公司主键
	
	@JsonProperty("corpkid")
	private String pk_corpk;//客户主键
	
	@JsonProperty("retype")
	private Integer iremindtype;//提醒类型  38：送票；39：抄税；40：清卡；
	
	@JsonProperty("bday")
	private Integer ibeginday;//开始日
	
	@JsonProperty("eday")
	private Integer iendday;//截止日
	
    @JsonProperty("operatorid")
    private String coperatorid; // 录入人

    @JsonProperty("zddate")
    private DZFDate doperatedate; // 录入日期

	public String getPk_remindset() {
		return pk_remindset;
	}

	public void setPk_remindset(String pk_remindset) {
		this.pk_remindset = pk_remindset;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public Integer getIremindtype() {
		return iremindtype;
	}

	public void setIremindtype(Integer iremindtype) {
		this.iremindtype = iremindtype;
	}

	public Integer getIbeginday() {
		return ibeginday;
	}

	public void setIbeginday(Integer ibeginday) {
		this.ibeginday = ibeginday;
	}

	public Integer getIendday() {
		return iendday;
	}

	public void setIendday(Integer iendday) {
		this.iendday = iendday;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	@Override
	public String getPKFieldName() {
		return "pk_remindset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_remindset";
	}

}
