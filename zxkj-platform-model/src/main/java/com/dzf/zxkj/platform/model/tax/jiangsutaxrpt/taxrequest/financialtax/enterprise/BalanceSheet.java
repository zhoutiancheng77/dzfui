package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.enterprise;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 资产负债表 sb29805001vo
@TaxExcelPos(reportID = "29805001", reportname = "资产负债表")
public class BalanceSheet {
	// 货币资金-年初
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble zcfzb_h2_ncs;
	// 货币资金-期末
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble zcfzb_h2_qms;

	// 短期借款-年初
	@TaxExcelPos(row = 5, col = 6)
	private DZFDouble zcfzb_h40_ncs;
	// 短期借款-期末
	@TaxExcelPos(row = 5, col = 7)
	private DZFDouble zcfzb_h40_qms;

	// 短期投资-年初
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble zcfzb_h3_ncs;
	// 短期投资-期末
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble zcfzb_h3_qms;

	// 应付票据-年初
	@TaxExcelPos(row = 6, col = 6)
	private DZFDouble zcfzb_h41_ncs;
	// 应付票据-期末
	@TaxExcelPos(row = 6, col = 7)
	private DZFDouble zcfzb_h41_qms;

	// 应收票据-年初
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble zcfzb_h4_ncs;
	// 应收票据-期末
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble zcfzb_h4_qms;

	// 应付账款-年初
	@TaxExcelPos(row = 7, col = 6)
	private DZFDouble zcfzb_h42_ncs;
	// 应付账款-期末
	@TaxExcelPos(row = 7, col = 7)
	private DZFDouble zcfzb_h42_qms;

	// 应收股利-年初
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble zcfzb_h5_ncs;
	// 应收股利-期末
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble zcfzb_h5_qms;

	// 预收账款-年初
	@TaxExcelPos(row = 8, col = 6)
	private DZFDouble zcfzb_h43_ncs;
	// 预收账款-期末
	@TaxExcelPos(row = 8, col = 7)
	private DZFDouble zcfzb_h43_qms;

	// 应收利息-年初
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble zcfzb_h6_ncs;
	// 应收利息-期末
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble zcfzb_h6_qms;

	// 应付工资-年初
	@TaxExcelPos(row = 9, col = 6)
	private DZFDouble zcfzb_h44_ncs;
	// 应付工资-期末
	@TaxExcelPos(row = 9, col = 7)
	private DZFDouble zcfzb_h44_qms;

	// 应收账款-年初
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble zcfzb_h7_ncs;
	// 应收账款-期末
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble zcfzb_h7_qms;

	// 应付福利费-年初
	@TaxExcelPos(row = 10, col = 6)
	private DZFDouble zcfzb_h45_ncs;
	// 应付福利费-期末
	@TaxExcelPos(row = 10, col = 7)
	private DZFDouble zcfzb_h45_qms;

	// 其他应收款-年初
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble zcfzb_h8_ncs;
	// 其他应收款-期末
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble zcfzb_h8_qms;

	// 应付股利-年初
	@TaxExcelPos(row = 11, col = 6)
	private DZFDouble zcfzb_h46_ncs;
	// 应付股利-期末
	@TaxExcelPos(row = 11, col = 7)
	private DZFDouble zcfzb_h46_qms;

	// 预付账款-年初
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble zcfzb_h9_ncs;
	// 预付账款-期末
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble zcfzb_h9_qms;

	// 应交税金-年初
	@TaxExcelPos(row = 12, col = 6)
	private DZFDouble zcfzb_h47_ncs;
	// 应交税金-期末
	@TaxExcelPos(row = 12, col = 7)
	private DZFDouble zcfzb_h47_qms;

	// 应收补贴款-年初
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble zcfzb_h10_ncs;
	// 应收补贴款-期末
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble zcfzb_h10_qms;

	// 其他应交款-年初
	@TaxExcelPos(row = 13, col = 6)
	private DZFDouble zcfzb_h48_ncs;
	// 其他应交款-期末
	@TaxExcelPos(row = 13, col = 7)
	private DZFDouble zcfzb_h48_qms;

	// 存货-年初
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble zcfzb_h11_ncs;
	// 存货-期末
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble zcfzb_h11_qms;

	// 其他应付款-年初
	@TaxExcelPos(row = 14, col = 6)
	private DZFDouble zcfzb_h49_ncs;
	// 其他应付款-期末
	@TaxExcelPos(row = 14, col = 7)
	private DZFDouble zcfzb_h49_qms;

	// 待摊费用-年初
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble zcfzb_h12_ncs;
	// 待摊费用-期末
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble zcfzb_h12_qms;

	// 预提费用-年初
	@TaxExcelPos(row = 15, col = 6)
	private DZFDouble zcfzb_h50_ncs;
	// 预提费用-期末
	@TaxExcelPos(row = 15, col = 7)
	private DZFDouble zcfzb_h50_qms;

	// 一年内到期的长期债权投资-年初
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble zcfzb_h13_ncs;
	// 一年内到期的长期债权投资-期末
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble zcfzb_h13_qms;

	// 预计负债-年初
	@TaxExcelPos(row = 16, col = 6)
	private DZFDouble zcfzb_h51_ncs;
	// 预计负债-期末
	@TaxExcelPos(row = 16, col = 7)
	private DZFDouble zcfzb_h51_qms;

	// 其他流动资产-年初
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble zcfzb_h14_ncs;
	// 其他流动资产-期末
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble zcfzb_h14_qms;

	// 一年内到期的长期负债-年初
	@TaxExcelPos(row = 17, col = 6)
	private DZFDouble zcfzb_h52_ncs;
	// 一年内到期的长期负债-期末
	@TaxExcelPos(row = 17, col = 7)
	private DZFDouble zcfzb_h52_qms;

	// 流动资产合计-年初
	@TaxExcelPos(row = 18, col = 2)
	private DZFDouble zcfzb_h15_ncs;
	// 流动资产合计-期末
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble zcfzb_h15_qms;

	// 其他流动负债-年初
	@TaxExcelPos(row = 18, col = 6)
	private DZFDouble zcfzb_h53_ncs;
	// 其他流动负债-期末
	@TaxExcelPos(row = 18, col = 7)
	private DZFDouble zcfzb_h53_qms;

	// 长期股权投资-年初
	@TaxExcelPos(row = 20, col = 2)
	private DZFDouble zcfzb_h17_ncs;
	// 长期股权投资-期末
	@TaxExcelPos(row = 20, col = 3)
	private DZFDouble zcfzb_h17_qms;

	// 流动负债合计-年初
	@TaxExcelPos(row = 20, col = 6)
	private DZFDouble zcfzb_h55_ncs;
	// 流动负债合计-期末
	@TaxExcelPos(row = 20, col = 7)
	private DZFDouble zcfzb_h55_qms;

	// 长期债权投资-年初
	@TaxExcelPos(row = 21, col = 2)
	private DZFDouble zcfzb_h18_ncs;
	// 长期债权投资-期末
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble zcfzb_h18_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 6)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 7)
	// private DZFDouble ;

	// 长期投资合计-年初
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble zcfzb_h19_ncs;
	// 长期投资合计-期末
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble zcfzb_h19_qms;

	// 长期借款-年初
	@TaxExcelPos(row = 22, col = 6)
	private DZFDouble zcfzb_h57_ncs;
	// 长期借款-期末
	@TaxExcelPos(row = 22, col = 7)
	private DZFDouble zcfzb_h57_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 2)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 3)
	// private DZFDouble ;

	// 应付债券-年初
	@TaxExcelPos(row = 23, col = 6)
	private DZFDouble zcfzb_h58_ncs;
	// 应付债券-期末
	@TaxExcelPos(row = 23, col = 7)
	private DZFDouble zcfzb_h58_qms;

	// 固定资产原价-年初
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble zcfzb_h21_ncs;
	// 固定资产原价-期末
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble zcfzb_h21_qms;

	// 长期应付款-年初
	@TaxExcelPos(row = 24, col = 6)
	private DZFDouble zcfzb_h59_ncs;
	// 长期应付款-期末
	@TaxExcelPos(row = 24, col = 7)
	private DZFDouble zcfzb_h59_qms;

	// 减：累计折旧-年初
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble zcfzb_h22_ncs;
	// 减：累计折旧-期末
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble zcfzb_h22_qms;

	// 专项应付款-年初
	@TaxExcelPos(row = 25, col = 6)
	private DZFDouble zcfzb_h60_ncs;
	// 专项应付款-期末
	@TaxExcelPos(row = 25, col = 7)
	private DZFDouble zcfzb_h60_qms;

	// 固定资产净值-年初
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble zcfzb_h23_ncs;
	// 固定资产净值-期末
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble zcfzb_h23_qms;

	// 其他长期负债-年初
	@TaxExcelPos(row = 26, col = 6)
	private DZFDouble zcfzb_h61_ncs;
	// 其他长期负债-期末
	@TaxExcelPos(row = 26, col = 7)
	private DZFDouble zcfzb_h61_qms;

	// 减：固定资产减值准备-年初
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble zcfzb_h24_ncs;
	// 减：固定资产减值准备-期末
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble zcfzb_h24_qms;

	// 长期负债合计-年初
	@TaxExcelPos(row = 27, col = 6)
	private DZFDouble zcfzb_h62_ncs;
	// 长期负债合计-期末
	@TaxExcelPos(row = 27, col = 7)
	private DZFDouble zcfzb_h62_qms;

	// 固定资产净额-年初
	@TaxExcelPos(row = 28, col = 2)
	private DZFDouble zcfzb_h25_ncs;
	// 固定资产净额-期末
	@TaxExcelPos(row = 28, col = 3)
	private DZFDouble zcfzb_h25_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 6)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 7)
	// private DZFDouble ;

	// 工程物资-年初
	@TaxExcelPos(row = 29, col = 2)
	private DZFDouble zcfzb_h26_ncs;
	// 工程物资-期末
	@TaxExcelPos(row = 29, col = 3)
	private DZFDouble zcfzb_h26_qms;

	// 递延税款贷项-年初
	@TaxExcelPos(row = 29, col = 6)
	private DZFDouble zcfzb_h64_ncs;
	// 递延税款贷项-期末
	@TaxExcelPos(row = 29, col = 7)
	private DZFDouble zcfzb_h64_qms;

	// 在建工程-年初
	@TaxExcelPos(row = 30, col = 2)
	private DZFDouble zcfzb_h27_ncs;
	// 在建工程-期末
	@TaxExcelPos(row = 30, col = 3)
	private DZFDouble zcfzb_h27_qms;

	// 负债合计-年初
	@TaxExcelPos(row = 30, col = 6)
	private DZFDouble zcfzb_h65_ncs;
	// 负债合计-期末
	@TaxExcelPos(row = 30, col = 7)
	private DZFDouble zcfzb_h65_qms;

	// 固定资产清理-年初
	@TaxExcelPos(row = 31, col = 2)
	private DZFDouble zcfzb_h28_ncs;
	// 固定资产清理-期末
	@TaxExcelPos(row = 31, col = 3)
	private DZFDouble zcfzb_h28_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 6)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 7)
	// private DZFDouble ;

	// 固定资产合计-年初
	@TaxExcelPos(row = 32, col = 2)
	private DZFDouble zcfzb_h29_ncs;
	// 固定资产合计-期末
	@TaxExcelPos(row = 32, col = 3)
	private DZFDouble zcfzb_h29_qms;

	// 实收资本(或股本)-年初
	@TaxExcelPos(row = 33, col = 6)
	private DZFDouble zcfzb_h68_ncs;
	// 实收资本(或股本)-期末
	@TaxExcelPos(row = 33, col = 7)
	private DZFDouble zcfzb_h68_qms;

	// 无形资产-年初
	@TaxExcelPos(row = 34, col = 2)
	private DZFDouble zcfzb_h31_ncs;
	// 无形资产-期末
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble zcfzb_h31_qms;

	// 减：已归还投资-年初
	@TaxExcelPos(row = 34, col = 6)
	private DZFDouble zcfzb_h69_ncs;
	// 减：已归还投资-期末
	@TaxExcelPos(row = 34, col = 7)
	private DZFDouble zcfzb_h69_qms;

	// 长期待摊费用-年初
	@TaxExcelPos(row = 35, col = 2)
	private DZFDouble zcfzb_h32_ncs;
	// 长期待摊费用-期末
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble zcfzb_h32_qms;

	// 实收资本(或股本)净额-年初
	@TaxExcelPos(row = 35, col = 6)
	private DZFDouble zcfzb_h70_ncs;
	// 实收资本(或股本)净额-期末
	@TaxExcelPos(row = 35, col = 7)
	private DZFDouble zcfzb_h70_qms;

	// 其他长期资产-年初
	@TaxExcelPos(row = 36, col = 2)
	private DZFDouble zcfzb_h33_ncs;
	// 其他长期资产-期末
	@TaxExcelPos(row = 36, col = 3)
	private DZFDouble zcfzb_h33_qms;

	// 资本公积-年初
	@TaxExcelPos(row = 36, col = 6)
	private DZFDouble zcfzb_h71_ncs;
	// 资本公积-期末
	@TaxExcelPos(row = 36, col = 7)
	private DZFDouble zcfzb_h71_qms;

	// 无形资产及其他资产合计-年初
	@TaxExcelPos(row = 37, col = 2)
	private DZFDouble zcfzb_h34_ncs;
	// 无形资产及其他资产合计-期末
	@TaxExcelPos(row = 37, col = 3)
	private DZFDouble zcfzb_h34_qms;

	// 盈余公积-年初
	@TaxExcelPos(row = 37, col = 6)
	private DZFDouble zcfzb_h72_ncs;
	// 盈余公积-期末
	@TaxExcelPos(row = 37, col = 7)
	private DZFDouble zcfzb_h72_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 2)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 3)
	// private DZFDouble ;

	// 其中：法定公益金-年初
	@TaxExcelPos(row = 38, col = 6)
	private DZFDouble zcfzb_h73_ncs;
	// 其中：法定公益金-期末
	@TaxExcelPos(row = 38, col = 7)
	private DZFDouble zcfzb_h73_qms;

	// // -年初
	// @TaxExcelPos(row = , col = 2)
	// private DZFDouble ;
	// // -期末
	// @TaxExcelPos(row = , col = 3)
	// private DZFDouble ;

	// 未分配利润-年初
	@TaxExcelPos(row = 39, col = 6)
	private DZFDouble zcfzb_h74_ncs;
	// 未分配利润-期末
	@TaxExcelPos(row = 39, col = 7)
	private DZFDouble zcfzb_h74_qms;

	// 递延税款借项-年初
	@TaxExcelPos(row = 40, col = 2)
	private DZFDouble zcfzb_h37_ncs;
	// 递延税款借项-期末
	@TaxExcelPos(row = 40, col = 3)
	private DZFDouble zcfzb_h37_qms;

	// 所有者权益（或股东权益）合计-年初
	@TaxExcelPos(row = 40, col = 6)
	private DZFDouble zcfzb_h75_ncs;
	// 所有者权益（或股东权益）合计-期末
	@TaxExcelPos(row = 40, col = 7)
	private DZFDouble zcfzb_h75_qms;

	// 资产合计-年初
	@TaxExcelPos(row = 41, col = 2)
	private DZFDouble zcfzb_h38_ncs;
	// 资产合计-期末
	@TaxExcelPos(row = 41, col = 3)
	private DZFDouble zcfzb_h38_qms;

	// 负债和所有者权益（或股东权益）总计-年初
	@TaxExcelPos(row = 41, col = 6)
	private DZFDouble zcfzb_h76_ncs;
	// 负债和所有者权益（或股东权益）总计-期末
	@TaxExcelPos(row = 41, col = 7)
	private DZFDouble zcfzb_h76_qms;

	public DZFDouble getZcfzb_h2_ncs() {
		return zcfzb_h2_ncs;
	}

	public void setZcfzb_h2_ncs(DZFDouble zcfzb_h2_ncs) {
		this.zcfzb_h2_ncs = zcfzb_h2_ncs;
	}

	public DZFDouble getZcfzb_h2_qms() {
		return zcfzb_h2_qms;
	}

	public void setZcfzb_h2_qms(DZFDouble zcfzb_h2_qms) {
		this.zcfzb_h2_qms = zcfzb_h2_qms;
	}

	public DZFDouble getZcfzb_h40_ncs() {
		return zcfzb_h40_ncs;
	}

	public void setZcfzb_h40_ncs(DZFDouble zcfzb_h40_ncs) {
		this.zcfzb_h40_ncs = zcfzb_h40_ncs;
	}

	public DZFDouble getZcfzb_h40_qms() {
		return zcfzb_h40_qms;
	}

	public void setZcfzb_h40_qms(DZFDouble zcfzb_h40_qms) {
		this.zcfzb_h40_qms = zcfzb_h40_qms;
	}

	public DZFDouble getZcfzb_h3_ncs() {
		return zcfzb_h3_ncs;
	}

	public void setZcfzb_h3_ncs(DZFDouble zcfzb_h3_ncs) {
		this.zcfzb_h3_ncs = zcfzb_h3_ncs;
	}

	public DZFDouble getZcfzb_h3_qms() {
		return zcfzb_h3_qms;
	}

	public void setZcfzb_h3_qms(DZFDouble zcfzb_h3_qms) {
		this.zcfzb_h3_qms = zcfzb_h3_qms;
	}

	public DZFDouble getZcfzb_h41_ncs() {
		return zcfzb_h41_ncs;
	}

	public void setZcfzb_h41_ncs(DZFDouble zcfzb_h41_ncs) {
		this.zcfzb_h41_ncs = zcfzb_h41_ncs;
	}

	public DZFDouble getZcfzb_h41_qms() {
		return zcfzb_h41_qms;
	}

	public void setZcfzb_h41_qms(DZFDouble zcfzb_h41_qms) {
		this.zcfzb_h41_qms = zcfzb_h41_qms;
	}

	public DZFDouble getZcfzb_h4_ncs() {
		return zcfzb_h4_ncs;
	}

	public void setZcfzb_h4_ncs(DZFDouble zcfzb_h4_ncs) {
		this.zcfzb_h4_ncs = zcfzb_h4_ncs;
	}

	public DZFDouble getZcfzb_h4_qms() {
		return zcfzb_h4_qms;
	}

	public void setZcfzb_h4_qms(DZFDouble zcfzb_h4_qms) {
		this.zcfzb_h4_qms = zcfzb_h4_qms;
	}

	public DZFDouble getZcfzb_h42_ncs() {
		return zcfzb_h42_ncs;
	}

	public void setZcfzb_h42_ncs(DZFDouble zcfzb_h42_ncs) {
		this.zcfzb_h42_ncs = zcfzb_h42_ncs;
	}

	public DZFDouble getZcfzb_h42_qms() {
		return zcfzb_h42_qms;
	}

	public void setZcfzb_h42_qms(DZFDouble zcfzb_h42_qms) {
		this.zcfzb_h42_qms = zcfzb_h42_qms;
	}

	public DZFDouble getZcfzb_h5_ncs() {
		return zcfzb_h5_ncs;
	}

	public void setZcfzb_h5_ncs(DZFDouble zcfzb_h5_ncs) {
		this.zcfzb_h5_ncs = zcfzb_h5_ncs;
	}

	public DZFDouble getZcfzb_h5_qms() {
		return zcfzb_h5_qms;
	}

	public void setZcfzb_h5_qms(DZFDouble zcfzb_h5_qms) {
		this.zcfzb_h5_qms = zcfzb_h5_qms;
	}

	public DZFDouble getZcfzb_h43_ncs() {
		return zcfzb_h43_ncs;
	}

	public void setZcfzb_h43_ncs(DZFDouble zcfzb_h43_ncs) {
		this.zcfzb_h43_ncs = zcfzb_h43_ncs;
	}

	public DZFDouble getZcfzb_h43_qms() {
		return zcfzb_h43_qms;
	}

	public void setZcfzb_h43_qms(DZFDouble zcfzb_h43_qms) {
		this.zcfzb_h43_qms = zcfzb_h43_qms;
	}

	public DZFDouble getZcfzb_h6_ncs() {
		return zcfzb_h6_ncs;
	}

	public void setZcfzb_h6_ncs(DZFDouble zcfzb_h6_ncs) {
		this.zcfzb_h6_ncs = zcfzb_h6_ncs;
	}

	public DZFDouble getZcfzb_h6_qms() {
		return zcfzb_h6_qms;
	}

	public void setZcfzb_h6_qms(DZFDouble zcfzb_h6_qms) {
		this.zcfzb_h6_qms = zcfzb_h6_qms;
	}

	public DZFDouble getZcfzb_h44_ncs() {
		return zcfzb_h44_ncs;
	}

	public void setZcfzb_h44_ncs(DZFDouble zcfzb_h44_ncs) {
		this.zcfzb_h44_ncs = zcfzb_h44_ncs;
	}

	public DZFDouble getZcfzb_h44_qms() {
		return zcfzb_h44_qms;
	}

	public void setZcfzb_h44_qms(DZFDouble zcfzb_h44_qms) {
		this.zcfzb_h44_qms = zcfzb_h44_qms;
	}

	public DZFDouble getZcfzb_h7_ncs() {
		return zcfzb_h7_ncs;
	}

	public void setZcfzb_h7_ncs(DZFDouble zcfzb_h7_ncs) {
		this.zcfzb_h7_ncs = zcfzb_h7_ncs;
	}

	public DZFDouble getZcfzb_h7_qms() {
		return zcfzb_h7_qms;
	}

	public void setZcfzb_h7_qms(DZFDouble zcfzb_h7_qms) {
		this.zcfzb_h7_qms = zcfzb_h7_qms;
	}

	public DZFDouble getZcfzb_h45_ncs() {
		return zcfzb_h45_ncs;
	}

	public void setZcfzb_h45_ncs(DZFDouble zcfzb_h45_ncs) {
		this.zcfzb_h45_ncs = zcfzb_h45_ncs;
	}

	public DZFDouble getZcfzb_h45_qms() {
		return zcfzb_h45_qms;
	}

	public void setZcfzb_h45_qms(DZFDouble zcfzb_h45_qms) {
		this.zcfzb_h45_qms = zcfzb_h45_qms;
	}

	public DZFDouble getZcfzb_h8_ncs() {
		return zcfzb_h8_ncs;
	}

	public void setZcfzb_h8_ncs(DZFDouble zcfzb_h8_ncs) {
		this.zcfzb_h8_ncs = zcfzb_h8_ncs;
	}

	public DZFDouble getZcfzb_h8_qms() {
		return zcfzb_h8_qms;
	}

	public void setZcfzb_h8_qms(DZFDouble zcfzb_h8_qms) {
		this.zcfzb_h8_qms = zcfzb_h8_qms;
	}

	public DZFDouble getZcfzb_h46_ncs() {
		return zcfzb_h46_ncs;
	}

	public void setZcfzb_h46_ncs(DZFDouble zcfzb_h46_ncs) {
		this.zcfzb_h46_ncs = zcfzb_h46_ncs;
	}

	public DZFDouble getZcfzb_h46_qms() {
		return zcfzb_h46_qms;
	}

	public void setZcfzb_h46_qms(DZFDouble zcfzb_h46_qms) {
		this.zcfzb_h46_qms = zcfzb_h46_qms;
	}

	public DZFDouble getZcfzb_h9_ncs() {
		return zcfzb_h9_ncs;
	}

	public void setZcfzb_h9_ncs(DZFDouble zcfzb_h9_ncs) {
		this.zcfzb_h9_ncs = zcfzb_h9_ncs;
	}

	public DZFDouble getZcfzb_h9_qms() {
		return zcfzb_h9_qms;
	}

	public void setZcfzb_h9_qms(DZFDouble zcfzb_h9_qms) {
		this.zcfzb_h9_qms = zcfzb_h9_qms;
	}

	public DZFDouble getZcfzb_h47_ncs() {
		return zcfzb_h47_ncs;
	}

	public void setZcfzb_h47_ncs(DZFDouble zcfzb_h47_ncs) {
		this.zcfzb_h47_ncs = zcfzb_h47_ncs;
	}

	public DZFDouble getZcfzb_h47_qms() {
		return zcfzb_h47_qms;
	}

	public void setZcfzb_h47_qms(DZFDouble zcfzb_h47_qms) {
		this.zcfzb_h47_qms = zcfzb_h47_qms;
	}

	public DZFDouble getZcfzb_h10_ncs() {
		return zcfzb_h10_ncs;
	}

	public void setZcfzb_h10_ncs(DZFDouble zcfzb_h10_ncs) {
		this.zcfzb_h10_ncs = zcfzb_h10_ncs;
	}

	public DZFDouble getZcfzb_h10_qms() {
		return zcfzb_h10_qms;
	}

	public void setZcfzb_h10_qms(DZFDouble zcfzb_h10_qms) {
		this.zcfzb_h10_qms = zcfzb_h10_qms;
	}

	public DZFDouble getZcfzb_h48_ncs() {
		return zcfzb_h48_ncs;
	}

	public void setZcfzb_h48_ncs(DZFDouble zcfzb_h48_ncs) {
		this.zcfzb_h48_ncs = zcfzb_h48_ncs;
	}

	public DZFDouble getZcfzb_h48_qms() {
		return zcfzb_h48_qms;
	}

	public void setZcfzb_h48_qms(DZFDouble zcfzb_h48_qms) {
		this.zcfzb_h48_qms = zcfzb_h48_qms;
	}

	public DZFDouble getZcfzb_h11_ncs() {
		return zcfzb_h11_ncs;
	}

	public void setZcfzb_h11_ncs(DZFDouble zcfzb_h11_ncs) {
		this.zcfzb_h11_ncs = zcfzb_h11_ncs;
	}

	public DZFDouble getZcfzb_h11_qms() {
		return zcfzb_h11_qms;
	}

	public void setZcfzb_h11_qms(DZFDouble zcfzb_h11_qms) {
		this.zcfzb_h11_qms = zcfzb_h11_qms;
	}

	public DZFDouble getZcfzb_h49_ncs() {
		return zcfzb_h49_ncs;
	}

	public void setZcfzb_h49_ncs(DZFDouble zcfzb_h49_ncs) {
		this.zcfzb_h49_ncs = zcfzb_h49_ncs;
	}

	public DZFDouble getZcfzb_h49_qms() {
		return zcfzb_h49_qms;
	}

	public void setZcfzb_h49_qms(DZFDouble zcfzb_h49_qms) {
		this.zcfzb_h49_qms = zcfzb_h49_qms;
	}

	public DZFDouble getZcfzb_h12_ncs() {
		return zcfzb_h12_ncs;
	}

	public void setZcfzb_h12_ncs(DZFDouble zcfzb_h12_ncs) {
		this.zcfzb_h12_ncs = zcfzb_h12_ncs;
	}

	public DZFDouble getZcfzb_h12_qms() {
		return zcfzb_h12_qms;
	}

	public void setZcfzb_h12_qms(DZFDouble zcfzb_h12_qms) {
		this.zcfzb_h12_qms = zcfzb_h12_qms;
	}

	public DZFDouble getZcfzb_h50_ncs() {
		return zcfzb_h50_ncs;
	}

	public void setZcfzb_h50_ncs(DZFDouble zcfzb_h50_ncs) {
		this.zcfzb_h50_ncs = zcfzb_h50_ncs;
	}

	public DZFDouble getZcfzb_h50_qms() {
		return zcfzb_h50_qms;
	}

	public void setZcfzb_h50_qms(DZFDouble zcfzb_h50_qms) {
		this.zcfzb_h50_qms = zcfzb_h50_qms;
	}

	public DZFDouble getZcfzb_h13_ncs() {
		return zcfzb_h13_ncs;
	}

	public void setZcfzb_h13_ncs(DZFDouble zcfzb_h13_ncs) {
		this.zcfzb_h13_ncs = zcfzb_h13_ncs;
	}

	public DZFDouble getZcfzb_h13_qms() {
		return zcfzb_h13_qms;
	}

	public void setZcfzb_h13_qms(DZFDouble zcfzb_h13_qms) {
		this.zcfzb_h13_qms = zcfzb_h13_qms;
	}

	public DZFDouble getZcfzb_h51_ncs() {
		return zcfzb_h51_ncs;
	}

	public void setZcfzb_h51_ncs(DZFDouble zcfzb_h51_ncs) {
		this.zcfzb_h51_ncs = zcfzb_h51_ncs;
	}

	public DZFDouble getZcfzb_h51_qms() {
		return zcfzb_h51_qms;
	}

	public void setZcfzb_h51_qms(DZFDouble zcfzb_h51_qms) {
		this.zcfzb_h51_qms = zcfzb_h51_qms;
	}

	public DZFDouble getZcfzb_h14_ncs() {
		return zcfzb_h14_ncs;
	}

	public void setZcfzb_h14_ncs(DZFDouble zcfzb_h14_ncs) {
		this.zcfzb_h14_ncs = zcfzb_h14_ncs;
	}

	public DZFDouble getZcfzb_h14_qms() {
		return zcfzb_h14_qms;
	}

	public void setZcfzb_h14_qms(DZFDouble zcfzb_h14_qms) {
		this.zcfzb_h14_qms = zcfzb_h14_qms;
	}

	public DZFDouble getZcfzb_h52_ncs() {
		return zcfzb_h52_ncs;
	}

	public void setZcfzb_h52_ncs(DZFDouble zcfzb_h52_ncs) {
		this.zcfzb_h52_ncs = zcfzb_h52_ncs;
	}

	public DZFDouble getZcfzb_h52_qms() {
		return zcfzb_h52_qms;
	}

	public void setZcfzb_h52_qms(DZFDouble zcfzb_h52_qms) {
		this.zcfzb_h52_qms = zcfzb_h52_qms;
	}

	public DZFDouble getZcfzb_h15_ncs() {
		return zcfzb_h15_ncs;
	}

	public void setZcfzb_h15_ncs(DZFDouble zcfzb_h15_ncs) {
		this.zcfzb_h15_ncs = zcfzb_h15_ncs;
	}

	public DZFDouble getZcfzb_h15_qms() {
		return zcfzb_h15_qms;
	}

	public void setZcfzb_h15_qms(DZFDouble zcfzb_h15_qms) {
		this.zcfzb_h15_qms = zcfzb_h15_qms;
	}

	public DZFDouble getZcfzb_h53_ncs() {
		return zcfzb_h53_ncs;
	}

	public void setZcfzb_h53_ncs(DZFDouble zcfzb_h53_ncs) {
		this.zcfzb_h53_ncs = zcfzb_h53_ncs;
	}

	public DZFDouble getZcfzb_h53_qms() {
		return zcfzb_h53_qms;
	}

	public void setZcfzb_h53_qms(DZFDouble zcfzb_h53_qms) {
		this.zcfzb_h53_qms = zcfzb_h53_qms;
	}

	public DZFDouble getZcfzb_h17_ncs() {
		return zcfzb_h17_ncs;
	}

	public void setZcfzb_h17_ncs(DZFDouble zcfzb_h17_ncs) {
		this.zcfzb_h17_ncs = zcfzb_h17_ncs;
	}

	public DZFDouble getZcfzb_h17_qms() {
		return zcfzb_h17_qms;
	}

	public void setZcfzb_h17_qms(DZFDouble zcfzb_h17_qms) {
		this.zcfzb_h17_qms = zcfzb_h17_qms;
	}

	public DZFDouble getZcfzb_h55_ncs() {
		return zcfzb_h55_ncs;
	}

	public void setZcfzb_h55_ncs(DZFDouble zcfzb_h55_ncs) {
		this.zcfzb_h55_ncs = zcfzb_h55_ncs;
	}

	public DZFDouble getZcfzb_h55_qms() {
		return zcfzb_h55_qms;
	}

	public void setZcfzb_h55_qms(DZFDouble zcfzb_h55_qms) {
		this.zcfzb_h55_qms = zcfzb_h55_qms;
	}

	public DZFDouble getZcfzb_h18_ncs() {
		return zcfzb_h18_ncs;
	}

	public void setZcfzb_h18_ncs(DZFDouble zcfzb_h18_ncs) {
		this.zcfzb_h18_ncs = zcfzb_h18_ncs;
	}

	public DZFDouble getZcfzb_h18_qms() {
		return zcfzb_h18_qms;
	}

	public void setZcfzb_h18_qms(DZFDouble zcfzb_h18_qms) {
		this.zcfzb_h18_qms = zcfzb_h18_qms;
	}

	public DZFDouble getZcfzb_h19_ncs() {
		return zcfzb_h19_ncs;
	}

	public void setZcfzb_h19_ncs(DZFDouble zcfzb_h19_ncs) {
		this.zcfzb_h19_ncs = zcfzb_h19_ncs;
	}

	public DZFDouble getZcfzb_h19_qms() {
		return zcfzb_h19_qms;
	}

	public void setZcfzb_h19_qms(DZFDouble zcfzb_h19_qms) {
		this.zcfzb_h19_qms = zcfzb_h19_qms;
	}

	public DZFDouble getZcfzb_h57_ncs() {
		return zcfzb_h57_ncs;
	}

	public void setZcfzb_h57_ncs(DZFDouble zcfzb_h57_ncs) {
		this.zcfzb_h57_ncs = zcfzb_h57_ncs;
	}

	public DZFDouble getZcfzb_h57_qms() {
		return zcfzb_h57_qms;
	}

	public void setZcfzb_h57_qms(DZFDouble zcfzb_h57_qms) {
		this.zcfzb_h57_qms = zcfzb_h57_qms;
	}

	public DZFDouble getZcfzb_h58_ncs() {
		return zcfzb_h58_ncs;
	}

	public void setZcfzb_h58_ncs(DZFDouble zcfzb_h58_ncs) {
		this.zcfzb_h58_ncs = zcfzb_h58_ncs;
	}

	public DZFDouble getZcfzb_h58_qms() {
		return zcfzb_h58_qms;
	}

	public void setZcfzb_h58_qms(DZFDouble zcfzb_h58_qms) {
		this.zcfzb_h58_qms = zcfzb_h58_qms;
	}

	public DZFDouble getZcfzb_h21_ncs() {
		return zcfzb_h21_ncs;
	}

	public void setZcfzb_h21_ncs(DZFDouble zcfzb_h21_ncs) {
		this.zcfzb_h21_ncs = zcfzb_h21_ncs;
	}

	public DZFDouble getZcfzb_h21_qms() {
		return zcfzb_h21_qms;
	}

	public void setZcfzb_h21_qms(DZFDouble zcfzb_h21_qms) {
		this.zcfzb_h21_qms = zcfzb_h21_qms;
	}

	public DZFDouble getZcfzb_h59_ncs() {
		return zcfzb_h59_ncs;
	}

	public void setZcfzb_h59_ncs(DZFDouble zcfzb_h59_ncs) {
		this.zcfzb_h59_ncs = zcfzb_h59_ncs;
	}

	public DZFDouble getZcfzb_h59_qms() {
		return zcfzb_h59_qms;
	}

	public void setZcfzb_h59_qms(DZFDouble zcfzb_h59_qms) {
		this.zcfzb_h59_qms = zcfzb_h59_qms;
	}

	public DZFDouble getZcfzb_h22_ncs() {
		return zcfzb_h22_ncs;
	}

	public void setZcfzb_h22_ncs(DZFDouble zcfzb_h22_ncs) {
		this.zcfzb_h22_ncs = zcfzb_h22_ncs;
	}

	public DZFDouble getZcfzb_h22_qms() {
		return zcfzb_h22_qms;
	}

	public void setZcfzb_h22_qms(DZFDouble zcfzb_h22_qms) {
		this.zcfzb_h22_qms = zcfzb_h22_qms;
	}

	public DZFDouble getZcfzb_h60_ncs() {
		return zcfzb_h60_ncs;
	}

	public void setZcfzb_h60_ncs(DZFDouble zcfzb_h60_ncs) {
		this.zcfzb_h60_ncs = zcfzb_h60_ncs;
	}

	public DZFDouble getZcfzb_h60_qms() {
		return zcfzb_h60_qms;
	}

	public void setZcfzb_h60_qms(DZFDouble zcfzb_h60_qms) {
		this.zcfzb_h60_qms = zcfzb_h60_qms;
	}

	public DZFDouble getZcfzb_h23_ncs() {
		return zcfzb_h23_ncs;
	}

	public void setZcfzb_h23_ncs(DZFDouble zcfzb_h23_ncs) {
		this.zcfzb_h23_ncs = zcfzb_h23_ncs;
	}

	public DZFDouble getZcfzb_h23_qms() {
		return zcfzb_h23_qms;
	}

	public void setZcfzb_h23_qms(DZFDouble zcfzb_h23_qms) {
		this.zcfzb_h23_qms = zcfzb_h23_qms;
	}

	public DZFDouble getZcfzb_h61_ncs() {
		return zcfzb_h61_ncs;
	}

	public void setZcfzb_h61_ncs(DZFDouble zcfzb_h61_ncs) {
		this.zcfzb_h61_ncs = zcfzb_h61_ncs;
	}

	public DZFDouble getZcfzb_h61_qms() {
		return zcfzb_h61_qms;
	}

	public void setZcfzb_h61_qms(DZFDouble zcfzb_h61_qms) {
		this.zcfzb_h61_qms = zcfzb_h61_qms;
	}

	public DZFDouble getZcfzb_h24_ncs() {
		return zcfzb_h24_ncs;
	}

	public void setZcfzb_h24_ncs(DZFDouble zcfzb_h24_ncs) {
		this.zcfzb_h24_ncs = zcfzb_h24_ncs;
	}

	public DZFDouble getZcfzb_h24_qms() {
		return zcfzb_h24_qms;
	}

	public void setZcfzb_h24_qms(DZFDouble zcfzb_h24_qms) {
		this.zcfzb_h24_qms = zcfzb_h24_qms;
	}

	public DZFDouble getZcfzb_h62_ncs() {
		return zcfzb_h62_ncs;
	}

	public void setZcfzb_h62_ncs(DZFDouble zcfzb_h62_ncs) {
		this.zcfzb_h62_ncs = zcfzb_h62_ncs;
	}

	public DZFDouble getZcfzb_h62_qms() {
		return zcfzb_h62_qms;
	}

	public void setZcfzb_h62_qms(DZFDouble zcfzb_h62_qms) {
		this.zcfzb_h62_qms = zcfzb_h62_qms;
	}

	public DZFDouble getZcfzb_h25_ncs() {
		return zcfzb_h25_ncs;
	}

	public void setZcfzb_h25_ncs(DZFDouble zcfzb_h25_ncs) {
		this.zcfzb_h25_ncs = zcfzb_h25_ncs;
	}

	public DZFDouble getZcfzb_h25_qms() {
		return zcfzb_h25_qms;
	}

	public void setZcfzb_h25_qms(DZFDouble zcfzb_h25_qms) {
		this.zcfzb_h25_qms = zcfzb_h25_qms;
	}

	public DZFDouble getZcfzb_h26_ncs() {
		return zcfzb_h26_ncs;
	}

	public void setZcfzb_h26_ncs(DZFDouble zcfzb_h26_ncs) {
		this.zcfzb_h26_ncs = zcfzb_h26_ncs;
	}

	public DZFDouble getZcfzb_h26_qms() {
		return zcfzb_h26_qms;
	}

	public void setZcfzb_h26_qms(DZFDouble zcfzb_h26_qms) {
		this.zcfzb_h26_qms = zcfzb_h26_qms;
	}

	public DZFDouble getZcfzb_h64_ncs() {
		return zcfzb_h64_ncs;
	}

	public void setZcfzb_h64_ncs(DZFDouble zcfzb_h64_ncs) {
		this.zcfzb_h64_ncs = zcfzb_h64_ncs;
	}

	public DZFDouble getZcfzb_h64_qms() {
		return zcfzb_h64_qms;
	}

	public void setZcfzb_h64_qms(DZFDouble zcfzb_h64_qms) {
		this.zcfzb_h64_qms = zcfzb_h64_qms;
	}

	public DZFDouble getZcfzb_h27_ncs() {
		return zcfzb_h27_ncs;
	}

	public void setZcfzb_h27_ncs(DZFDouble zcfzb_h27_ncs) {
		this.zcfzb_h27_ncs = zcfzb_h27_ncs;
	}

	public DZFDouble getZcfzb_h27_qms() {
		return zcfzb_h27_qms;
	}

	public void setZcfzb_h27_qms(DZFDouble zcfzb_h27_qms) {
		this.zcfzb_h27_qms = zcfzb_h27_qms;
	}

	public DZFDouble getZcfzb_h65_ncs() {
		return zcfzb_h65_ncs;
	}

	public void setZcfzb_h65_ncs(DZFDouble zcfzb_h65_ncs) {
		this.zcfzb_h65_ncs = zcfzb_h65_ncs;
	}

	public DZFDouble getZcfzb_h65_qms() {
		return zcfzb_h65_qms;
	}

	public void setZcfzb_h65_qms(DZFDouble zcfzb_h65_qms) {
		this.zcfzb_h65_qms = zcfzb_h65_qms;
	}

	public DZFDouble getZcfzb_h28_ncs() {
		return zcfzb_h28_ncs;
	}

	public void setZcfzb_h28_ncs(DZFDouble zcfzb_h28_ncs) {
		this.zcfzb_h28_ncs = zcfzb_h28_ncs;
	}

	public DZFDouble getZcfzb_h28_qms() {
		return zcfzb_h28_qms;
	}

	public void setZcfzb_h28_qms(DZFDouble zcfzb_h28_qms) {
		this.zcfzb_h28_qms = zcfzb_h28_qms;
	}

	public DZFDouble getZcfzb_h29_ncs() {
		return zcfzb_h29_ncs;
	}

	public void setZcfzb_h29_ncs(DZFDouble zcfzb_h29_ncs) {
		this.zcfzb_h29_ncs = zcfzb_h29_ncs;
	}

	public DZFDouble getZcfzb_h29_qms() {
		return zcfzb_h29_qms;
	}

	public void setZcfzb_h29_qms(DZFDouble zcfzb_h29_qms) {
		this.zcfzb_h29_qms = zcfzb_h29_qms;
	}

	public DZFDouble getZcfzb_h68_ncs() {
		return zcfzb_h68_ncs;
	}

	public void setZcfzb_h68_ncs(DZFDouble zcfzb_h68_ncs) {
		this.zcfzb_h68_ncs = zcfzb_h68_ncs;
	}

	public DZFDouble getZcfzb_h68_qms() {
		return zcfzb_h68_qms;
	}

	public void setZcfzb_h68_qms(DZFDouble zcfzb_h68_qms) {
		this.zcfzb_h68_qms = zcfzb_h68_qms;
	}

	public DZFDouble getZcfzb_h31_ncs() {
		return zcfzb_h31_ncs;
	}

	public void setZcfzb_h31_ncs(DZFDouble zcfzb_h31_ncs) {
		this.zcfzb_h31_ncs = zcfzb_h31_ncs;
	}

	public DZFDouble getZcfzb_h31_qms() {
		return zcfzb_h31_qms;
	}

	public void setZcfzb_h31_qms(DZFDouble zcfzb_h31_qms) {
		this.zcfzb_h31_qms = zcfzb_h31_qms;
	}

	public DZFDouble getZcfzb_h69_ncs() {
		return zcfzb_h69_ncs;
	}

	public void setZcfzb_h69_ncs(DZFDouble zcfzb_h69_ncs) {
		this.zcfzb_h69_ncs = zcfzb_h69_ncs;
	}

	public DZFDouble getZcfzb_h69_qms() {
		return zcfzb_h69_qms;
	}

	public void setZcfzb_h69_qms(DZFDouble zcfzb_h69_qms) {
		this.zcfzb_h69_qms = zcfzb_h69_qms;
	}

	public DZFDouble getZcfzb_h32_ncs() {
		return zcfzb_h32_ncs;
	}

	public void setZcfzb_h32_ncs(DZFDouble zcfzb_h32_ncs) {
		this.zcfzb_h32_ncs = zcfzb_h32_ncs;
	}

	public DZFDouble getZcfzb_h32_qms() {
		return zcfzb_h32_qms;
	}

	public void setZcfzb_h32_qms(DZFDouble zcfzb_h32_qms) {
		this.zcfzb_h32_qms = zcfzb_h32_qms;
	}

	public DZFDouble getZcfzb_h70_ncs() {
		return zcfzb_h70_ncs;
	}

	public void setZcfzb_h70_ncs(DZFDouble zcfzb_h70_ncs) {
		this.zcfzb_h70_ncs = zcfzb_h70_ncs;
	}

	public DZFDouble getZcfzb_h70_qms() {
		return zcfzb_h70_qms;
	}

	public void setZcfzb_h70_qms(DZFDouble zcfzb_h70_qms) {
		this.zcfzb_h70_qms = zcfzb_h70_qms;
	}

	public DZFDouble getZcfzb_h33_ncs() {
		return zcfzb_h33_ncs;
	}

	public void setZcfzb_h33_ncs(DZFDouble zcfzb_h33_ncs) {
		this.zcfzb_h33_ncs = zcfzb_h33_ncs;
	}

	public DZFDouble getZcfzb_h33_qms() {
		return zcfzb_h33_qms;
	}

	public void setZcfzb_h33_qms(DZFDouble zcfzb_h33_qms) {
		this.zcfzb_h33_qms = zcfzb_h33_qms;
	}

	public DZFDouble getZcfzb_h71_ncs() {
		return zcfzb_h71_ncs;
	}

	public void setZcfzb_h71_ncs(DZFDouble zcfzb_h71_ncs) {
		this.zcfzb_h71_ncs = zcfzb_h71_ncs;
	}

	public DZFDouble getZcfzb_h71_qms() {
		return zcfzb_h71_qms;
	}

	public void setZcfzb_h71_qms(DZFDouble zcfzb_h71_qms) {
		this.zcfzb_h71_qms = zcfzb_h71_qms;
	}

	public DZFDouble getZcfzb_h34_ncs() {
		return zcfzb_h34_ncs;
	}

	public void setZcfzb_h34_ncs(DZFDouble zcfzb_h34_ncs) {
		this.zcfzb_h34_ncs = zcfzb_h34_ncs;
	}

	public DZFDouble getZcfzb_h34_qms() {
		return zcfzb_h34_qms;
	}

	public void setZcfzb_h34_qms(DZFDouble zcfzb_h34_qms) {
		this.zcfzb_h34_qms = zcfzb_h34_qms;
	}

	public DZFDouble getZcfzb_h72_ncs() {
		return zcfzb_h72_ncs;
	}

	public void setZcfzb_h72_ncs(DZFDouble zcfzb_h72_ncs) {
		this.zcfzb_h72_ncs = zcfzb_h72_ncs;
	}

	public DZFDouble getZcfzb_h72_qms() {
		return zcfzb_h72_qms;
	}

	public void setZcfzb_h72_qms(DZFDouble zcfzb_h72_qms) {
		this.zcfzb_h72_qms = zcfzb_h72_qms;
	}

	public DZFDouble getZcfzb_h73_ncs() {
		return zcfzb_h73_ncs;
	}

	public void setZcfzb_h73_ncs(DZFDouble zcfzb_h73_ncs) {
		this.zcfzb_h73_ncs = zcfzb_h73_ncs;
	}

	public DZFDouble getZcfzb_h73_qms() {
		return zcfzb_h73_qms;
	}

	public void setZcfzb_h73_qms(DZFDouble zcfzb_h73_qms) {
		this.zcfzb_h73_qms = zcfzb_h73_qms;
	}

	public DZFDouble getZcfzb_h74_ncs() {
		return zcfzb_h74_ncs;
	}

	public void setZcfzb_h74_ncs(DZFDouble zcfzb_h74_ncs) {
		this.zcfzb_h74_ncs = zcfzb_h74_ncs;
	}

	public DZFDouble getZcfzb_h74_qms() {
		return zcfzb_h74_qms;
	}

	public void setZcfzb_h74_qms(DZFDouble zcfzb_h74_qms) {
		this.zcfzb_h74_qms = zcfzb_h74_qms;
	}

	public DZFDouble getZcfzb_h37_ncs() {
		return zcfzb_h37_ncs;
	}

	public void setZcfzb_h37_ncs(DZFDouble zcfzb_h37_ncs) {
		this.zcfzb_h37_ncs = zcfzb_h37_ncs;
	}

	public DZFDouble getZcfzb_h37_qms() {
		return zcfzb_h37_qms;
	}

	public void setZcfzb_h37_qms(DZFDouble zcfzb_h37_qms) {
		this.zcfzb_h37_qms = zcfzb_h37_qms;
	}

	public DZFDouble getZcfzb_h75_ncs() {
		return zcfzb_h75_ncs;
	}

	public void setZcfzb_h75_ncs(DZFDouble zcfzb_h75_ncs) {
		this.zcfzb_h75_ncs = zcfzb_h75_ncs;
	}

	public DZFDouble getZcfzb_h75_qms() {
		return zcfzb_h75_qms;
	}

	public void setZcfzb_h75_qms(DZFDouble zcfzb_h75_qms) {
		this.zcfzb_h75_qms = zcfzb_h75_qms;
	}

	public DZFDouble getZcfzb_h38_ncs() {
		return zcfzb_h38_ncs;
	}

	public void setZcfzb_h38_ncs(DZFDouble zcfzb_h38_ncs) {
		this.zcfzb_h38_ncs = zcfzb_h38_ncs;
	}

	public DZFDouble getZcfzb_h38_qms() {
		return zcfzb_h38_qms;
	}

	public void setZcfzb_h38_qms(DZFDouble zcfzb_h38_qms) {
		this.zcfzb_h38_qms = zcfzb_h38_qms;
	}

	public DZFDouble getZcfzb_h76_ncs() {
		return zcfzb_h76_ncs;
	}

	public void setZcfzb_h76_ncs(DZFDouble zcfzb_h76_ncs) {
		this.zcfzb_h76_ncs = zcfzb_h76_ncs;
	}

	public DZFDouble getZcfzb_h76_qms() {
		return zcfzb_h76_qms;
	}

	public void setZcfzb_h76_qms(DZFDouble zcfzb_h76_qms) {
		this.zcfzb_h76_qms = zcfzb_h76_qms;
	}

}
