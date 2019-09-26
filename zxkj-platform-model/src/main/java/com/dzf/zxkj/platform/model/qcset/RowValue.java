package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.common.lang.DZFDouble;

public class RowValue implements java.io.Serializable{
	
	/**
	 * 
	 */

	private DZFDouble qc = DZFDouble.ZERO_DBL;
	
	private DZFDouble jf = DZFDouble.ZERO_DBL;
	
	private DZFDouble df = DZFDouble.ZERO_DBL;
	
	private DZFDouble qm = DZFDouble.ZERO_DBL;
	
	private DZFDouble ybqc = DZFDouble.ZERO_DBL;
	
	private DZFDouble ybjf = DZFDouble.ZERO_DBL;
	
	private DZFDouble ybdf = DZFDouble.ZERO_DBL;
	
	private DZFDouble ybqm = DZFDouble.ZERO_DBL;
	
	private DZFDouble bnqcnum = DZFDouble.ZERO_DBL;
	
	private DZFDouble bnfsnum = DZFDouble.ZERO_DBL;
	
	private DZFDouble bndffsnum = DZFDouble.ZERO_DBL;
	
	private DZFDouble monthqmnum = DZFDouble.ZERO_DBL;
	
	
	
	private Integer direct ;
	
	public DZFDouble getYbqc() {
		return ybqc;
	}
	public void setYbqc(DZFDouble ybqc) {
		this.ybqc = ybqc;
	}
	public DZFDouble getYbjf() {
		return ybjf;
	}
	public void setYbjf(DZFDouble ybjf) {
		this.ybjf = ybjf;
	}
	public DZFDouble getYbdf() {
		return ybdf;
	}
	public void setYbdf(DZFDouble ybdf) {
		this.ybdf = ybdf;
	}
	public DZFDouble getYbqm() {
		return ybqm;
	}
	public void setYbqm(DZFDouble ybqm) {
		this.ybqm = ybqm;
	}
	public DZFDouble getQc() {
		return qc;
	}
	public void setQc(DZFDouble qc) {
		this.qc = qc;
	}
	public DZFDouble getJf() {
		return jf;
	}
	public void setJf(DZFDouble jf) {
		this.jf = jf;
	}
	public DZFDouble getDf() {
		return df;
	}
	public void setDf(DZFDouble df) {
		this.df = df;
	}
	public DZFDouble getQm() {
		return qm;
	}
	public void setQm(DZFDouble qm) {
		this.qm = qm;
	}
	public Integer getDirect() {
		return direct;
	}
	public void setDirect(Integer direct) {
		this.direct = direct;
	}
	public DZFDouble getBnqcnum() {
		return bnqcnum;
	}
	public void setBnqcnum(DZFDouble bnqcnum) {
		this.bnqcnum = bnqcnum;
	}
	public DZFDouble getBnfsnum() {
		return bnfsnum;
	}
	public void setBnfsnum(DZFDouble bnfsnum) {
		this.bnfsnum = bnfsnum;
	}
	public DZFDouble getBndffsnum() {
		return bndffsnum;
	}
	public void setBndffsnum(DZFDouble bndffsnum) {
		this.bndffsnum = bndffsnum;
	}
	public DZFDouble getMonthqmnum() {
		return monthqmnum;
	}
	public void setMonthqmnum(DZFDouble monthqmnum) {
		this.monthqmnum = monthqmnum;
	}
	
	
}
