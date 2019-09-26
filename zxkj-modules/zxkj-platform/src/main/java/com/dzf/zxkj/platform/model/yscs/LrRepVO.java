package com.dzf.zxkj.platform.model.yscs;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class LrRepVO extends SuperVO {

	// 科目名称
	@JsonProperty("BBCX_KMMC")
	private String kmmc;
	// 行次
	@JsonProperty("BBCX_HC")
	private Integer hc;
	// 本期金额
	@JsonProperty("BBCX_BQJE")
	private DZFDouble bqje;
	// 本年累计金额
	@JsonProperty("BBCX_BNLJ")
	private DZFDouble bnlj;

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public Integer getHc() {
		return hc;
	}

	public void setHc(Integer hc) {
		this.hc = hc;
	}

	public DZFDouble getBqje() {
		return bqje;
	}

	public void setBqje(DZFDouble bqje) {
		this.bqje = bqje;
	}

	public DZFDouble getBnlj() {
		return bnlj;
	}

	public void setBnlj(DZFDouble bnlj) {
		this.bnlj = bnlj;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
