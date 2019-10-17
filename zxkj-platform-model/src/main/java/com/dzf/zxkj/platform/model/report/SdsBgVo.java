package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 所得税报告
 * 
 * @author zhangj
 *
 */
public class SdsBgVo extends SuperVO {

	private String month;// 季度

	private String sds;// 本季度所得税

	private String jdsf;// 季度税负

	private String ljsf;// 累计税负

	public String getSds() {
		return sds;
	}

	public void setSds(String sds) {
		this.sds = sds;
	}

	public String getJdsf() {
		return jdsf;
	}

	public void setJdsf(String jdsf) {
		this.jdsf = jdsf;
	}

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
