package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.small;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 利润表 sb29806002vo
@TaxExcelPos(reportID = "C1002", reportname = "利润表")
public class ProfitStatement {
	// 一、营业收入-本月金额
	@TaxExcelPos(row = 4, col = 3)
	private DZFDouble lrb_h1_byje;
	// 一、营业收入-本年累计金额
	@TaxExcelPos(row = 4, col = 2)
	private DZFDouble lrb_h1_bnljje;

	// 减：营业成本-本月金额
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble lrb_h2_byje;
	// 减：营业成本-本年累计金额
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble lrb_h2_bnljje;

	// 营业税金及附加-本月金额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble lrb_h3_byje;
	// 营业税金及附加-本年累计金额
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble lrb_h3_bnljje;

	// 其中：消费税-本月金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble lrb_h4_byje;
	// 其中：消费税-本年累计金额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble lrb_h4_bnljje;

	// 营业税-本月金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble lrb_h5_byje;
	// 营业税-本年累计金额
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble lrb_h5_bnljje;

	// 城市维护建设税-本月金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble lrb_h6_byje;
	// 城市维护建设税-本年累计金额
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble lrb_h6_bnljje;

	// 资源税-本月金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble lrb_h7_byje;
	// 资源税-本年累计金额
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble lrb_h7_bnljje;

	// 土地增值税-本月金额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble lrb_h8_byje;
	// 土地增值税-本年累计金额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble lrb_h8_bnljje;

	// 城镇土地使用税、房产税、车船税、印花税-本月金额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble lrb_h9_byje;
	// 城镇土地使用税、房产税、车船税、印花税-本年累计金额
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble lrb_h9_bnljje;

	// 教育费附加、矿产资源补偿费、排污费-本月金额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble lrb_h10_byje;
	// 教育费附加、矿产资源补偿费、排污费-本年累计金额
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble lrb_h10_bnljje;

	// 销售费用-本月金额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble lrb_h11_byje;
	// 销售费用-本年累计金额
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble lrb_h11_bnljje;

	// 其中：商品维修费-本月金额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble lrb_h12_byje;
	// 其中：商品维修费-本年累计金额
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble lrb_h12_bnljje;

	// 广告费和业务宣传费-本月金额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble lrb_h13_byje;
	// 广告费和业务宣传费-本年累计金额
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble lrb_h13_bnljje;

	// 管理费用-本月金额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble lrb_h14_byje;
	// 管理费用-本年累计金额
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble lrb_h14_bnljje;

	// 其中：开办费-本月金额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble lrb_h15_byje;
	// 其中：开办费-本年累计金额
	@TaxExcelPos(row = 18, col = 2)
	private DZFDouble lrb_h15_bnljje;

	// 业务招待费-本月金额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble lrb_h16_byje;
	// 业务招待费本年累计金额
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble lrb_h16_bnljje;

	// 研究费用-本月金额
	@TaxExcelPos(row = 20, col = 3)
	private DZFDouble lrb_h17_byje;
	// 研究费用-本年累计金额
	@TaxExcelPos(row = 20, col = 2)
	private DZFDouble lrb_h17_bnljje;

	// 财务费用-本月金额
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble lrb_h18_byje;
	// 财务费用-本年累计金额
	@TaxExcelPos(row = 21, col = 2)
	private DZFDouble lrb_h18_bnljje;

	// 其中：利息费用（收入以“-”号填列）-本月金额
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble lrb_h19_byje;
	// 其中：利息费用（收入以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble lrb_h19_bnljje;

	// 加：投资收益（损失以“-”号填列）-本月金额
	@TaxExcelPos(row = 23, col = 3)
	private DZFDouble lrb_h20_byje;
	// 加：投资收益（损失以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 23, col = 2)
	private DZFDouble lrb_h20_bnljje;

	// 二、营业利润（亏损以“-”号填列）-本月金额
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble lrb_h21_byje;
	// 二、营业利润（亏损以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble lrb_h21_bnljje;

	// 加：营业外收入-本月金额
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble lrb_h22_byje;
	// 加：营业外收入-本年累计金额
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble lrb_h22_bnljje;

	// 其中：政府补助-本月金额
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble lrb_h23_byje;
	// 其中：政府补助-本年累计金额
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble lrb_h23_bnljje;

	// 减：营业外支出-本月金额
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble lrb_h24_byje;
	// 减：营业外支出-本年累计金额
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble lrb_h24_bnljje;

	// 其中：坏账损失-本月金额
	@TaxExcelPos(row = 28, col = 3)
	private DZFDouble lrb_h25_byje;
	// 其中：坏账损失-本年累计金额
	@TaxExcelPos(row = 28, col = 2)
	private DZFDouble lrb_h25_bnljje;

	// 无法收回的长期债券投资损失-本月金额
	@TaxExcelPos(row = 29, col = 3)
	private DZFDouble lrb_h26_byje;
	// 无法收回的长期债券投资损失-本年累计金额
	@TaxExcelPos(row = 29, col = 2)
	private DZFDouble lrb_h26_bnljje;

	// 无法收回的长期股权投资损失-本月金额
	@TaxExcelPos(row = 30, col = 3)
	private DZFDouble lrb_h27_byje;
	// 无法收回的长期股权投资损失-本年累计金额
	@TaxExcelPos(row = 30, col = 2)
	private DZFDouble lrb_h27_bnljje;

	// 自然灾害等不可抗力因素造成的损失-本月金额
	@TaxExcelPos(row = 31, col = 3)
	private DZFDouble lrb_h28_byje;
	// 自然灾害等不可抗力因素造成的损失-本年累计金额
	@TaxExcelPos(row = 31, col = 2)
	private DZFDouble lrb_h28_bnljje;

	// 税收滞纳金-本月金额
	@TaxExcelPos(row = 32, col = 3)
	private DZFDouble lrb_h29_byje;
	// 税收滞纳金-本年累计金额
	@TaxExcelPos(row = 32, col = 2)
	private DZFDouble lrb_h29_bnljje;

	// 三、利润总额（亏损总额以“-”号填列）-本月金额
	@TaxExcelPos(row = 33, col = 3)
	private DZFDouble lrb_h30_byje;
	// 三、利润总额（亏损总额以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 33, col = 2)
	private DZFDouble lrb_h30_bnljje;

	// 减：所得税费用-本月金额
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble lrb_h31_byje;
	// 减：所得税费用-本年累计金额
	@TaxExcelPos(row = 34, col = 2)
	private DZFDouble lrb_h31_bnljje;

	// 四、净利润（净亏损以“-”号填列）-本月金额
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble lrb_h32_byje;
	// 四、净利润（净亏损以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 35, col = 2)
	private DZFDouble lrb_h32_bnljje;

	public DZFDouble getLrb_h1_byje() {
		return lrb_h1_byje;
	}

	public void setLrb_h1_byje(DZFDouble lrb_h1_byje) {
		this.lrb_h1_byje = lrb_h1_byje;
	}

	public DZFDouble getLrb_h1_bnljje() {
		return lrb_h1_bnljje;
	}

	public void setLrb_h1_bnljje(DZFDouble lrb_h1_bnljje) {
		this.lrb_h1_bnljje = lrb_h1_bnljje;
	}

	public DZFDouble getLrb_h2_byje() {
		return lrb_h2_byje;
	}

	public void setLrb_h2_byje(DZFDouble lrb_h2_byje) {
		this.lrb_h2_byje = lrb_h2_byje;
	}

	public DZFDouble getLrb_h2_bnljje() {
		return lrb_h2_bnljje;
	}

	public void setLrb_h2_bnljje(DZFDouble lrb_h2_bnljje) {
		this.lrb_h2_bnljje = lrb_h2_bnljje;
	}

	public DZFDouble getLrb_h3_byje() {
		return lrb_h3_byje;
	}

	public void setLrb_h3_byje(DZFDouble lrb_h3_byje) {
		this.lrb_h3_byje = lrb_h3_byje;
	}

	public DZFDouble getLrb_h3_bnljje() {
		return lrb_h3_bnljje;
	}

	public void setLrb_h3_bnljje(DZFDouble lrb_h3_bnljje) {
		this.lrb_h3_bnljje = lrb_h3_bnljje;
	}

	public DZFDouble getLrb_h4_byje() {
		return lrb_h4_byje;
	}

	public void setLrb_h4_byje(DZFDouble lrb_h4_byje) {
		this.lrb_h4_byje = lrb_h4_byje;
	}

	public DZFDouble getLrb_h4_bnljje() {
		return lrb_h4_bnljje;
	}

	public void setLrb_h4_bnljje(DZFDouble lrb_h4_bnljje) {
		this.lrb_h4_bnljje = lrb_h4_bnljje;
	}

	public DZFDouble getLrb_h5_byje() {
		return lrb_h5_byje;
	}

	public void setLrb_h5_byje(DZFDouble lrb_h5_byje) {
		this.lrb_h5_byje = lrb_h5_byje;
	}

	public DZFDouble getLrb_h5_bnljje() {
		return lrb_h5_bnljje;
	}

	public void setLrb_h5_bnljje(DZFDouble lrb_h5_bnljje) {
		this.lrb_h5_bnljje = lrb_h5_bnljje;
	}

	public DZFDouble getLrb_h6_byje() {
		return lrb_h6_byje;
	}

	public void setLrb_h6_byje(DZFDouble lrb_h6_byje) {
		this.lrb_h6_byje = lrb_h6_byje;
	}

	public DZFDouble getLrb_h6_bnljje() {
		return lrb_h6_bnljje;
	}

	public void setLrb_h6_bnljje(DZFDouble lrb_h6_bnljje) {
		this.lrb_h6_bnljje = lrb_h6_bnljje;
	}

	public DZFDouble getLrb_h7_byje() {
		return lrb_h7_byje;
	}

	public void setLrb_h7_byje(DZFDouble lrb_h7_byje) {
		this.lrb_h7_byje = lrb_h7_byje;
	}

	public DZFDouble getLrb_h7_bnljje() {
		return lrb_h7_bnljje;
	}

	public void setLrb_h7_bnljje(DZFDouble lrb_h7_bnljje) {
		this.lrb_h7_bnljje = lrb_h7_bnljje;
	}

	public DZFDouble getLrb_h8_byje() {
		return lrb_h8_byje;
	}

	public void setLrb_h8_byje(DZFDouble lrb_h8_byje) {
		this.lrb_h8_byje = lrb_h8_byje;
	}

	public DZFDouble getLrb_h8_bnljje() {
		return lrb_h8_bnljje;
	}

	public void setLrb_h8_bnljje(DZFDouble lrb_h8_bnljje) {
		this.lrb_h8_bnljje = lrb_h8_bnljje;
	}

	public DZFDouble getLrb_h9_byje() {
		return lrb_h9_byje;
	}

	public void setLrb_h9_byje(DZFDouble lrb_h9_byje) {
		this.lrb_h9_byje = lrb_h9_byje;
	}

	public DZFDouble getLrb_h9_bnljje() {
		return lrb_h9_bnljje;
	}

	public void setLrb_h9_bnljje(DZFDouble lrb_h9_bnljje) {
		this.lrb_h9_bnljje = lrb_h9_bnljje;
	}

	public DZFDouble getLrb_h10_byje() {
		return lrb_h10_byje;
	}

	public void setLrb_h10_byje(DZFDouble lrb_h10_byje) {
		this.lrb_h10_byje = lrb_h10_byje;
	}

	public DZFDouble getLrb_h10_bnljje() {
		return lrb_h10_bnljje;
	}

	public void setLrb_h10_bnljje(DZFDouble lrb_h10_bnljje) {
		this.lrb_h10_bnljje = lrb_h10_bnljje;
	}

	public DZFDouble getLrb_h11_byje() {
		return lrb_h11_byje;
	}

	public void setLrb_h11_byje(DZFDouble lrb_h11_byje) {
		this.lrb_h11_byje = lrb_h11_byje;
	}

	public DZFDouble getLrb_h11_bnljje() {
		return lrb_h11_bnljje;
	}

	public void setLrb_h11_bnljje(DZFDouble lrb_h11_bnljje) {
		this.lrb_h11_bnljje = lrb_h11_bnljje;
	}

	public DZFDouble getLrb_h12_byje() {
		return lrb_h12_byje;
	}

	public void setLrb_h12_byje(DZFDouble lrb_h12_byje) {
		this.lrb_h12_byje = lrb_h12_byje;
	}

	public DZFDouble getLrb_h12_bnljje() {
		return lrb_h12_bnljje;
	}

	public void setLrb_h12_bnljje(DZFDouble lrb_h12_bnljje) {
		this.lrb_h12_bnljje = lrb_h12_bnljje;
	}

	public DZFDouble getLrb_h13_byje() {
		return lrb_h13_byje;
	}

	public void setLrb_h13_byje(DZFDouble lrb_h13_byje) {
		this.lrb_h13_byje = lrb_h13_byje;
	}

	public DZFDouble getLrb_h13_bnljje() {
		return lrb_h13_bnljje;
	}

	public void setLrb_h13_bnljje(DZFDouble lrb_h13_bnljje) {
		this.lrb_h13_bnljje = lrb_h13_bnljje;
	}

	public DZFDouble getLrb_h14_byje() {
		return lrb_h14_byje;
	}

	public void setLrb_h14_byje(DZFDouble lrb_h14_byje) {
		this.lrb_h14_byje = lrb_h14_byje;
	}

	public DZFDouble getLrb_h14_bnljje() {
		return lrb_h14_bnljje;
	}

	public void setLrb_h14_bnljje(DZFDouble lrb_h14_bnljje) {
		this.lrb_h14_bnljje = lrb_h14_bnljje;
	}

	public DZFDouble getLrb_h15_byje() {
		return lrb_h15_byje;
	}

	public void setLrb_h15_byje(DZFDouble lrb_h15_byje) {
		this.lrb_h15_byje = lrb_h15_byje;
	}

	public DZFDouble getLrb_h15_bnljje() {
		return lrb_h15_bnljje;
	}

	public void setLrb_h15_bnljje(DZFDouble lrb_h15_bnljje) {
		this.lrb_h15_bnljje = lrb_h15_bnljje;
	}

	public DZFDouble getLrb_h16_byje() {
		return lrb_h16_byje;
	}

	public void setLrb_h16_byje(DZFDouble lrb_h16_byje) {
		this.lrb_h16_byje = lrb_h16_byje;
	}

	public DZFDouble getLrb_h16_bnljje() {
		return lrb_h16_bnljje;
	}

	public void setLrb_h16_bnljje(DZFDouble lrb_h16_bnljje) {
		this.lrb_h16_bnljje = lrb_h16_bnljje;
	}

	public DZFDouble getLrb_h17_byje() {
		return lrb_h17_byje;
	}

	public void setLrb_h17_byje(DZFDouble lrb_h17_byje) {
		this.lrb_h17_byje = lrb_h17_byje;
	}

	public DZFDouble getLrb_h17_bnljje() {
		return lrb_h17_bnljje;
	}

	public void setLrb_h17_bnljje(DZFDouble lrb_h17_bnljje) {
		this.lrb_h17_bnljje = lrb_h17_bnljje;
	}

	public DZFDouble getLrb_h18_byje() {
		return lrb_h18_byje;
	}

	public void setLrb_h18_byje(DZFDouble lrb_h18_byje) {
		this.lrb_h18_byje = lrb_h18_byje;
	}

	public DZFDouble getLrb_h18_bnljje() {
		return lrb_h18_bnljje;
	}

	public void setLrb_h18_bnljje(DZFDouble lrb_h18_bnljje) {
		this.lrb_h18_bnljje = lrb_h18_bnljje;
	}

	public DZFDouble getLrb_h19_byje() {
		return lrb_h19_byje;
	}

	public void setLrb_h19_byje(DZFDouble lrb_h19_byje) {
		this.lrb_h19_byje = lrb_h19_byje;
	}

	public DZFDouble getLrb_h19_bnljje() {
		return lrb_h19_bnljje;
	}

	public void setLrb_h19_bnljje(DZFDouble lrb_h19_bnljje) {
		this.lrb_h19_bnljje = lrb_h19_bnljje;
	}

	public DZFDouble getLrb_h20_byje() {
		return lrb_h20_byje;
	}

	public void setLrb_h20_byje(DZFDouble lrb_h20_byje) {
		this.lrb_h20_byje = lrb_h20_byje;
	}

	public DZFDouble getLrb_h20_bnljje() {
		return lrb_h20_bnljje;
	}

	public void setLrb_h20_bnljje(DZFDouble lrb_h20_bnljje) {
		this.lrb_h20_bnljje = lrb_h20_bnljje;
	}

	public DZFDouble getLrb_h21_byje() {
		return lrb_h21_byje;
	}

	public void setLrb_h21_byje(DZFDouble lrb_h21_byje) {
		this.lrb_h21_byje = lrb_h21_byje;
	}

	public DZFDouble getLrb_h21_bnljje() {
		return lrb_h21_bnljje;
	}

	public void setLrb_h21_bnljje(DZFDouble lrb_h21_bnljje) {
		this.lrb_h21_bnljje = lrb_h21_bnljje;
	}

	public DZFDouble getLrb_h22_byje() {
		return lrb_h22_byje;
	}

	public void setLrb_h22_byje(DZFDouble lrb_h22_byje) {
		this.lrb_h22_byje = lrb_h22_byje;
	}

	public DZFDouble getLrb_h22_bnljje() {
		return lrb_h22_bnljje;
	}

	public void setLrb_h22_bnljje(DZFDouble lrb_h22_bnljje) {
		this.lrb_h22_bnljje = lrb_h22_bnljje;
	}

	public DZFDouble getLrb_h23_byje() {
		return lrb_h23_byje;
	}

	public void setLrb_h23_byje(DZFDouble lrb_h23_byje) {
		this.lrb_h23_byje = lrb_h23_byje;
	}

	public DZFDouble getLrb_h23_bnljje() {
		return lrb_h23_bnljje;
	}

	public void setLrb_h23_bnljje(DZFDouble lrb_h23_bnljje) {
		this.lrb_h23_bnljje = lrb_h23_bnljje;
	}

	public DZFDouble getLrb_h24_byje() {
		return lrb_h24_byje;
	}

	public void setLrb_h24_byje(DZFDouble lrb_h24_byje) {
		this.lrb_h24_byje = lrb_h24_byje;
	}

	public DZFDouble getLrb_h24_bnljje() {
		return lrb_h24_bnljje;
	}

	public void setLrb_h24_bnljje(DZFDouble lrb_h24_bnljje) {
		this.lrb_h24_bnljje = lrb_h24_bnljje;
	}

	public DZFDouble getLrb_h25_byje() {
		return lrb_h25_byje;
	}

	public void setLrb_h25_byje(DZFDouble lrb_h25_byje) {
		this.lrb_h25_byje = lrb_h25_byje;
	}

	public DZFDouble getLrb_h25_bnljje() {
		return lrb_h25_bnljje;
	}

	public void setLrb_h25_bnljje(DZFDouble lrb_h25_bnljje) {
		this.lrb_h25_bnljje = lrb_h25_bnljje;
	}

	public DZFDouble getLrb_h26_byje() {
		return lrb_h26_byje;
	}

	public void setLrb_h26_byje(DZFDouble lrb_h26_byje) {
		this.lrb_h26_byje = lrb_h26_byje;
	}

	public DZFDouble getLrb_h26_bnljje() {
		return lrb_h26_bnljje;
	}

	public void setLrb_h26_bnljje(DZFDouble lrb_h26_bnljje) {
		this.lrb_h26_bnljje = lrb_h26_bnljje;
	}

	public DZFDouble getLrb_h27_byje() {
		return lrb_h27_byje;
	}

	public void setLrb_h27_byje(DZFDouble lrb_h27_byje) {
		this.lrb_h27_byje = lrb_h27_byje;
	}

	public DZFDouble getLrb_h27_bnljje() {
		return lrb_h27_bnljje;
	}

	public void setLrb_h27_bnljje(DZFDouble lrb_h27_bnljje) {
		this.lrb_h27_bnljje = lrb_h27_bnljje;
	}

	public DZFDouble getLrb_h28_byje() {
		return lrb_h28_byje;
	}

	public void setLrb_h28_byje(DZFDouble lrb_h28_byje) {
		this.lrb_h28_byje = lrb_h28_byje;
	}

	public DZFDouble getLrb_h28_bnljje() {
		return lrb_h28_bnljje;
	}

	public void setLrb_h28_bnljje(DZFDouble lrb_h28_bnljje) {
		this.lrb_h28_bnljje = lrb_h28_bnljje;
	}

	public DZFDouble getLrb_h29_byje() {
		return lrb_h29_byje;
	}

	public void setLrb_h29_byje(DZFDouble lrb_h29_byje) {
		this.lrb_h29_byje = lrb_h29_byje;
	}

	public DZFDouble getLrb_h29_bnljje() {
		return lrb_h29_bnljje;
	}

	public void setLrb_h29_bnljje(DZFDouble lrb_h29_bnljje) {
		this.lrb_h29_bnljje = lrb_h29_bnljje;
	}

	public DZFDouble getLrb_h30_byje() {
		return lrb_h30_byje;
	}

	public void setLrb_h30_byje(DZFDouble lrb_h30_byje) {
		this.lrb_h30_byje = lrb_h30_byje;
	}

	public DZFDouble getLrb_h30_bnljje() {
		return lrb_h30_bnljje;
	}

	public void setLrb_h30_bnljje(DZFDouble lrb_h30_bnljje) {
		this.lrb_h30_bnljje = lrb_h30_bnljje;
	}

	public DZFDouble getLrb_h31_byje() {
		return lrb_h31_byje;
	}

	public void setLrb_h31_byje(DZFDouble lrb_h31_byje) {
		this.lrb_h31_byje = lrb_h31_byje;
	}

	public DZFDouble getLrb_h31_bnljje() {
		return lrb_h31_bnljje;
	}

	public void setLrb_h31_bnljje(DZFDouble lrb_h31_bnljje) {
		this.lrb_h31_bnljje = lrb_h31_bnljje;
	}

	public DZFDouble getLrb_h32_byje() {
		return lrb_h32_byje;
	}

	public void setLrb_h32_byje(DZFDouble lrb_h32_byje) {
		this.lrb_h32_byje = lrb_h32_byje;
	}

	public DZFDouble getLrb_h32_bnljje() {
		return lrb_h32_bnljje;
	}

	public void setLrb_h32_bnljje(DZFDouble lrb_h32_bnljje) {
		this.lrb_h32_bnljje = lrb_h32_bnljje;
	}

}
