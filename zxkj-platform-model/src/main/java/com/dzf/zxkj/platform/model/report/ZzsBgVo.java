package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 增值税报告
 * 
 * @author zhangj
 *
 */
public class ZzsBgVo extends SuperVO {

	private String month;//月份
	
	private String yysr;//营业收入

	private String zzs;//增值税

	private String sf;//税负
	
	private String ljsf;//累计税负
	
	public String getLjsf() {
		return ljsf;
	}

	public void setLjsf(String ljsf) {
		this.ljsf = ljsf;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYysr() {
		return yysr;
	}

	public void setYysr(String yysr) {
		this.yysr = yysr;
	}

	public String getZzs() {
		return zzs;
	}

	public void setZzs(String zzs) {
		this.zzs = zzs;
	}

	public String getSf() {
		return sf;
	}

	public void setSf(String sf) {
		this.sf = sf;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
