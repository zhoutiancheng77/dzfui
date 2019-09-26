package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.entity.ICodeName;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasureVO extends SuperVO implements ICodeName {

	@JsonProperty("id")
	private String pk_measure;
	@JsonProperty("jldwbm")
	private String code;
	@JsonProperty("jldwmc")
	private String name;
	@JsonProperty("jc")
	private String shortname;
	@JsonProperty("gsid")
	private String pk_corp;
	@JsonProperty("cpsn")
	private String creator;
	@JsonProperty("ctime")
	private DZFDateTime createtime;
	@JsonProperty("bz")
	private String memo;
	@JsonProperty("extime")
	private DZFDateTime ts;
	private Integer dr;
	@JsonProperty("v1")
	private String vdef1;
	@JsonProperty("v2")
	private String vdef2;
	@JsonProperty("v3")
	private String vdef3;
	@JsonProperty("v4")
	private String vdef4;
	@JsonProperty("v5")
	private String vdef5;
	@JsonProperty("v6")
	private String vdef6;
	@JsonProperty("v7")
	private String vdef7;
	@JsonProperty("v8")
	private String vdef8;
	@JsonProperty("v9")
	private String vdef9;
	@JsonProperty("v10")
	private String vdef10;


	public String getPk_measure() {
		return pk_measure;
	}

	public void setPk_measure(String pk_measure) {
		this.pk_measure = pk_measure;
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

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public DZFDateTime getCreatetime() {
		return createtime;
	}

	public void setCreatetime(DZFDateTime createtime) {
		this.createtime = createtime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
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
		return "pk_measure";
	}

	@Override
	public String getTableName() {
		return "ynt_measure";
	}

}