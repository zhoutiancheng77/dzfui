package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;

public class KmQmJzExtVO extends KMQMJZVO {
	public DZFDouble getLjdffse() {
		return ljdffse;
	}
	public void setLjdffse(DZFDouble ljdffse) {
		this.ljdffse = ljdffse;
	}
	public DZFDouble getLjjffse() {
		return ljjffse;
	}
	public void setLjjffse(DZFDouble ljjffse) {
		this.ljjffse = ljjffse;
	}
	
	public DZFDouble getYbljjffse() {
		return ybljjffse;
	}
	public void setYbljjffse(DZFDouble ybljjffse) {
		this.ybljjffse = ybljjffse;
	}
	public DZFDouble getYbljdffse() {
		return ybljdffse;
	}
	public void setYbljdffse(DZFDouble ybljdffse) {
		this.ybljdffse = ybljdffse;
	}


	private DZFDouble ljdffse;
	private DZFDouble ljjffse;
	private DZFDouble ybljjffse;//原币累计借方
	private DZFDouble ybljdffse;//原币累计贷方
	private String pzh;
	
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public static KmQmJzExtVO newInstance(){
		KmQmJzExtVO vo=new KmQmJzExtVO();
		vo.setJffse(DZFDouble.ZERO_DBL);
		vo.setDffse(DZFDouble.ZERO_DBL);
		vo.setLjdffse(DZFDouble.ZERO_DBL);
		vo.setLjjffse(DZFDouble.ZERO_DBL);
	return vo;
	}
	
}