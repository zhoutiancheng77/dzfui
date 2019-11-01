package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.entity.Grid;

/**
 * 科目汇总表
 * 
 * @author zhangj
 *
 */
public class SubjectCollectGrid extends Grid {
	/** 凭证数 */
	private Integer voucherCount;
	/** 附单据数 */
	private Integer billCount;

	public Integer getVoucherCount() {
		return voucherCount;
	}

	public void setVoucherCount(Integer voucherCount) {
		this.voucherCount = voucherCount;
	}

	public Integer getBillCount() {
		return billCount;
	}

	public void setBillCount(Integer billCount) {
		this.billCount = billCount;
	}
}
