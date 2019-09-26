package com.dzf.zxkj.platform.model.yscs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class ZcfzRepVO extends SuperVO {
	
	// 资产
	@JsonProperty("BBCX_ZC")
	private String zc;

	// 行次1
	@JsonProperty("BBCX_HC1")
	private Integer hc1;

	// 期末余额1
	@JsonProperty("BBCX_QMYE1")
	private DZFDouble qmye1;

	// 年初余额
	@JsonProperty("BBCX_NC1")
	private DZFDouble ncye1;

	// 负债和所有者权益(或股东权益）
	@JsonProperty("BBCX_FZHSYZQY")
	private String fzhsyzqy;

	// 行次
	@JsonProperty("BBCX_HC2")
	private Integer hc2;

	// 期末余额
	@JsonProperty("BBCX_QMYE2")
	private DZFDouble qmye2;

	// 年初余额
	@JsonProperty("BBCX_NCYE2")
	private DZFDouble ncye2;
	
	

	public String getZc() {
		return zc;
	}

	public void setZc(String zc) {
		this.zc = zc;
	}

	public Integer getHc1() {
		return hc1;
	}

	public void setHc1(Integer hc1) {
		this.hc1 = hc1;
	}

	public DZFDouble getQmye1() {
		return qmye1;
	}

	public void setQmye1(DZFDouble qmye1) {
		this.qmye1 = qmye1;
	}

	public DZFDouble getNcye1() {
		return ncye1;
	}

	public void setNcye1(DZFDouble ncye1) {
		this.ncye1 = ncye1;
	}

	public String getFzhsyzqy() {
		return fzhsyzqy;
	}

	public void setFzhsyzqy(String fzhsyzqy) {
		this.fzhsyzqy = fzhsyzqy;
	}

	public Integer getHc2() {
		return hc2;
	}

	public void setHc2(Integer hc2) {
		this.hc2 = hc2;
	}

	public DZFDouble getQmye2() {
		return qmye2;
	}

	public void setQmye2(DZFDouble qmye2) {
		this.qmye2 = qmye2;
	}

	public DZFDouble getNcye2() {
		return ncye2;
	}

	public void setNcye2(DZFDouble ncye2) {
		this.ncye2 = ncye2;
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
