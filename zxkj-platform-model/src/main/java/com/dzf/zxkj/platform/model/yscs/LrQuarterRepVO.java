package com.dzf.zxkj.platform.model.yscs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class LrQuarterRepVO extends SuperVO {

	// 项目
	@JsonProperty("BBCX_KMMC")
	private String xm;

	// 行数
	@JsonProperty("BBCX_HC")
	private Integer hs;

	// 本年累计
	@JsonProperty("BBCX_BNLJ")
	private DZFDouble bnlj;

	@JsonProperty("BBCX_DYJD")
	private DZFDouble quarterFirst;// 第一季度
	
	@JsonProperty("BBCX_DEJD")
	private DZFDouble quarterSecond;// 第二季度
	
	@JsonProperty("BBCX_DSJD")
	private DZFDouble quarterThird;// 第三季度
	
	@JsonProperty("BBCX_DFJD")
	private DZFDouble quarterFourth;// 第四季度

	@JsonProperty("BBCX_YJDSNTQ")
	private DZFDouble lastquarterFirst;// 上年同期第一季度
	
	@JsonProperty("BBCX_EJDSNTQ")
	private DZFDouble lastquarterSecond;// 上年同期第二季度
	
	@JsonProperty("BBCX_SJDSNTQ")
	private DZFDouble lastquarterThird;// 上年同期第三季度
	
	@JsonProperty("BBCX_FJDSNTQ")
	private DZFDouble lastquarterFourth;// 上年同期第四季度

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public Integer getHs() {
		return hs;
	}

	public void setHs(Integer hs) {
		this.hs = hs;
	}

	public DZFDouble getBnlj() {
		return bnlj;
	}

	public void setBnlj(DZFDouble bnlj) {
		this.bnlj = bnlj;
	}

	public DZFDouble getQuarterFirst() {
		return quarterFirst;
	}

	public void setQuarterFirst(DZFDouble quarterFirst) {
		this.quarterFirst = quarterFirst;
	}

	public DZFDouble getQuarterSecond() {
		return quarterSecond;
	}

	public void setQuarterSecond(DZFDouble quarterSecond) {
		this.quarterSecond = quarterSecond;
	}

	public DZFDouble getQuarterThird() {
		return quarterThird;
	}

	public void setQuarterThird(DZFDouble quarterThird) {
		this.quarterThird = quarterThird;
	}

	public DZFDouble getQuarterFourth() {
		return quarterFourth;
	}

	public void setQuarterFourth(DZFDouble quarterFourth) {
		this.quarterFourth = quarterFourth;
	}

	public DZFDouble getLastquarterFirst() {
		return lastquarterFirst;
	}

	public void setLastquarterFirst(DZFDouble lastquarterFirst) {
		this.lastquarterFirst = lastquarterFirst;
	}

	public DZFDouble getLastquarterSecond() {
		return lastquarterSecond;
	}

	public void setLastquarterSecond(DZFDouble lastquarterSecond) {
		this.lastquarterSecond = lastquarterSecond;
	}

	public DZFDouble getLastquarterThird() {
		return lastquarterThird;
	}

	public void setLastquarterThird(DZFDouble lastquarterThird) {
		this.lastquarterThird = lastquarterThird;
	}

	public DZFDouble getLastquarterFourth() {
		return lastquarterFourth;
	}

	public void setLastquarterFourth(DZFDouble lastquarterFourth) {
		this.lastquarterFourth = lastquarterFourth;
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
