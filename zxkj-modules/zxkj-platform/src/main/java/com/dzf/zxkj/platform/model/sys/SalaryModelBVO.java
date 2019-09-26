package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class SalaryModelBVO extends SuperVO<SalaryModelBVO> {
	private String pk_model_b;
	private String pk_model_h;
	private String pk_corp;
	private String zy;
	private String kmbm;
	private String kmmc;
	private String pk_accsubj;
	private Integer dr;
	private DZFDateTime ts;
	private Integer direction;

	public String getPk_model_b() {
		return pk_model_b;
	}

	public void setPk_model_b(String pk_model_b) {
		this.pk_model_b = pk_model_b;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
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

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public String getParentPKFieldName() {
		return "pk_model_h";
	}

	public String getPKFieldName() {
		return "pk_model_b";
	}

	public String getTableName() {
		return "ynt_salarymodel_b";
	}
}
