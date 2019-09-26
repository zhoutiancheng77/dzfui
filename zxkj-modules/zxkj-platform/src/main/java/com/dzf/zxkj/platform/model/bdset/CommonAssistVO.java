package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 常用辅助核算
 * 
 * @author lbj
 *
 */
public class CommonAssistVO extends SuperVO {
	@JsonProperty("id")
	private String pk_common_assist;
	private String pk_accsubj;
	// 税目
	private String pk_tax_item;
	@JsonProperty("fzhs1")
	private String fzhsx1;
	@JsonProperty("fzhs2")
	private String fzhsx2;
	@JsonProperty("fzhs3")
	private String fzhsx3;
	@JsonProperty("fzhs4")
	private String fzhsx4;
	@JsonProperty("fzhs5")
	private String fzhsx5;
	@JsonProperty("fzhs6")
	private String fzhsx6;
	@JsonProperty("fzhs7")
	private String fzhsx7;
	@JsonProperty("fzhs8")
	private String fzhsx8;
	@JsonProperty("fzhs9")
	private String fzhsx9;
	@JsonProperty("fzhs10")
	private String fzhsx10;
	private String pk_corp;
	private String coperatorid;
	private Integer dr;
	private DZFDateTime ts;

	private String code;
	private String isnum;
	private String isfzhs;
	private List<AuxiliaryAccountBVO> assistData;

	public String getPk_common_assist() {
		return pk_common_assist;
	}

	public void setPk_common_assist(String pk_common_assist) {
		this.pk_common_assist = pk_common_assist;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getPk_tax_item() {
		return pk_tax_item;
	}

	public void setPk_tax_item(String pk_tax_item) {
		this.pk_tax_item = pk_tax_item;
	}

	public String getFzhsx1() {
		return fzhsx1;
	}

	public void setFzhsx1(String fzhsx1) {
		this.fzhsx1 = fzhsx1;
	}

	public String getFzhsx2() {
		return fzhsx2;
	}

	public void setFzhsx2(String fzhsx2) {
		this.fzhsx2 = fzhsx2;
	}

	public String getFzhsx3() {
		return fzhsx3;
	}

	public void setFzhsx3(String fzhsx3) {
		this.fzhsx3 = fzhsx3;
	}

	public String getFzhsx4() {
		return fzhsx4;
	}

	public void setFzhsx4(String fzhsx4) {
		this.fzhsx4 = fzhsx4;
	}

	public String getFzhsx5() {
		return fzhsx5;
	}

	public void setFzhsx5(String fzhsx5) {
		this.fzhsx5 = fzhsx5;
	}

	public String getFzhsx6() {
		return fzhsx6;
	}

	public void setFzhsx6(String fzhsx6) {
		this.fzhsx6 = fzhsx6;
	}

	public String getFzhsx7() {
		return fzhsx7;
	}

	public void setFzhsx7(String fzhsx7) {
		this.fzhsx7 = fzhsx7;
	}

	public String getFzhsx8() {
		return fzhsx8;
	}

	public void setFzhsx8(String fzhsx8) {
		this.fzhsx8 = fzhsx8;
	}

	public String getFzhsx9() {
		return fzhsx9;
	}

	public void setFzhsx9(String fzhsx9) {
		this.fzhsx9 = fzhsx9;
	}

	public String getFzhsx10() {
		return fzhsx10;
	}

	public void setFzhsx10(String fzhsx10) {
		this.fzhsx10 = fzhsx10;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIsnum() {
		return isnum;
	}

	public void setIsnum(String isnum) {
		this.isnum = isnum;
	}

	public String getIsfzhs() {
		return isfzhs;
	}

	public void setIsfzhs(String isfzhs) {
		this.isfzhs = isfzhs;
	}

	public List<AuxiliaryAccountBVO> getAssistData() {
		return assistData;
	}

	public void setAssistData(List<AuxiliaryAccountBVO> assistData) {
		this.assistData = assistData;
	}

	@Override
	public String getPKFieldName() {
		return "pk_common_assist";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_commonassist";
	}

}
