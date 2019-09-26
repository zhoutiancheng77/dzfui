package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class EAModelBVO extends SuperVO<EAModelBVO> {
	
	/**
	 * 报销单凭证模板子表
	 */
	private String pk_model_b;//子表主键
	private String pk_model_h;//主表主键
	private String pk_corp;//公司主键
	private String zy;//摘要
	private String kmbm;
	private String kmmc;
	private String pk_accsubj;//科目主键
	private DZFDouble jfmny;//借方金额
	private DZFDouble dfmny;//贷方金额
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private DZFDouble ndef1;
	private DZFDouble ndef2;
	private DZFDouble ndef3;
	private String vnote;//备注
	private Integer dr;
	private DZFDateTime ts;
	private Integer direction;
	private String vfield;

	public String getPk_model_b() {
		return pk_model_b;
	}

	public String getVfield() {
		return vfield;
	}

	public void setVfield(String vfield) {
		this.vfield = vfield;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
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

	public DZFDouble getJfmny() {
		return jfmny;
	}

	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}

	public DZFDouble getDfmny() {
		return dfmny;
	}

	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}

	public String getVnote() {
		return vnote;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
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

	public DZFDouble getNdef1() {
		return ndef1;
	}

	public void setNdef1(DZFDouble ndef1) {
		this.ndef1 = ndef1;
	}

	public DZFDouble getNdef2() {
		return ndef2;
	}

	public void setNdef2(DZFDouble ndef2) {
		this.ndef2 = ndef2;
	}

	public DZFDouble getNdef3() {
		return ndef3;
	}

	public void setNdef3(DZFDouble ndef3) {
		this.ndef3 = ndef3;
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
	

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_model_h";
	}
	@Override
	public String getPKFieldName() {
		return "pk_model_b";
	}
	@Override
	public String getTableName() {
		return "ynt_eamodel_b";
	}
}
