package com.dzf.zxkj.platform.model.icset;


import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

@Entity
public class InvclassifyVO extends SuperVO {
  
	@JsonProperty("invcls_id")
	private String pk_invclassify;
	@JsonProperty("cd")
	private String code;
	@JsonProperty("nm")
	private String name;
	@JsonProperty("corp_id")
	private String pk_corp;
	@JsonProperty("maker")
	private String creator;
	@JsonProperty("crtm")
	private String createtime;
	@JsonProperty("mark")
	private String memo;
	private String ts;
	private Integer dr;
	@JsonProperty("vd1")
	private String vdef1;
	@JsonProperty("vd2")
	private String vdef2;
	@JsonProperty("vd3")
	private String vdef3;
	@JsonProperty("vd4")
	private String vdef4;
	@JsonProperty("vd5")
	private String vdef5;
	@JsonProperty("vd6")
	private String vdef6;
	@JsonProperty("vd7")
	private String vdef7;
	@JsonProperty("vd8")
	private String vdef8;
	@JsonProperty("vd9")
	private String vdef9;
	@JsonProperty("vd10")
	private String vdef10;
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getPk_invclassify() {
		return pk_invclassify;
	}

	public void setPk_invclassify(String pk_invclassify) {
		this.pk_invclassify = pk_invclassify;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}
	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}
	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}
	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}
	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}
	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}
	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}
	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}
	public String getVdef9() {
		return vdef9;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}
	public String getVdef10() {
		return vdef10;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invclassify";
	}

	@Override
	public String getTableName() {
		return "ynt_invclassify";
	}
}
