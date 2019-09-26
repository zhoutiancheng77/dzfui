package com.dzf.zxkj.platform.model.yscs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class XjllRepVO extends SuperVO {

	//项目
	@JsonProperty("BBCX_KMMC")
	private String xm ;
	
	//行次
	@JsonProperty("BBCX_HC")
	private Integer hc ;
	
	//本期金额
	@JsonProperty("BBCX_BQJE")
	private DZFDouble bqje ;
	
	//上期金额
	@JsonProperty("BBCX_BNLJ")
	private DZFDouble sqje ;
	

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
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

	public DZFDouble getSqje() {
		return sqje;
	}

	public void setSqje(DZFDouble sqje) {
		this.sqje = sqje;
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
