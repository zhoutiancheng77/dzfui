package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.small;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 利润表 sb39806002vo
@TaxExcelPos(reportID = "39806002", reportname = "利润表")
public class ProfitStatement {
	// 一、营业收入-本年累计金额
	@TaxExcelPos(row = 4, col = 2)
	private DZFDouble lrb_h1_bnljje;
	// 一、营业收入-上年金额
	@TaxExcelPos(row = 4, col = 3)
	private DZFDouble lrb_h1_snje;

	// 减：营业成本-本年累计金额
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble lrb_h2_bnljje;
	// 减：营业成本-上年金额
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble lrb_h2_snje;

	// 营业税金及附加-本年累计金额
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble lrb_h3_bnljje;
	// 营业税金及附加-上年金额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble lrb_h3_snje;

	// 其中：消费税-本年累计金额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble lrb_h4_bnljje;
	// 其中：消费税-上年金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble lrb_h4_snje;

	// 营业税-本年累计金额
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble lrb_h5_bnljje;
	// 营业税-上年金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble lrb_h5_snje;

	// 城市维护建设税-本年累计金额
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble lrb_h6_bnljje;
	// 城市维护建设税-上年金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble lrb_h6_snje;

	// 资源税-本年累计金额
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble lrb_h7_bnljje;
	// 资源税-上年金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble lrb_h7_snje;

	// 土地增值税-本年累计金额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble lrb_h8_bnljje;
	// 土地增值税-上年金额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble lrb_h8_snje;

	// 城镇土地使用税、房产税、车船税、印花税-本年累计金额
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble lrb_h9_bnljje;
	// 城镇土地使用税、房产税、车船税、印花税-上年金额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble lrb_h9_snje;

	// 教育费附加、矿产资源补偿费、排污费-本年累计金额
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble lrb_h10_bnljje;
	// 教育费附加、矿产资源补偿费、排污费-上年金额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble lrb_h10_snje;

	// 销售费用-本年累计金额
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble lrb_h11_bnljje;
	// 销售费用-上年金额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble lrb_h11_snje;

	// 其中：商品维修费-本年累计金额
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble lrb_h12_bnljje;
	// 其中：商品维修费-上年金额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble lrb_h12_snje;

	// 广告费和业务宣传费-本年累计金额
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble lrb_h13_bnljje;
	// 广告费和业务宣传费-上年金额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble lrb_h13_snje;

	// 管理费用-本年累计金额
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble lrb_h14_bnljje;
	// 管理费用-上年金额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble lrb_h14_snje;

	// 其中：开办费-本年累计金额
	@TaxExcelPos(row = 18, col = 2)
	private DZFDouble lrb_h15_bnljje;
	// 其中：开办费-上年金额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble lrb_h15_snje;

	// 业务招待费-本年累计金额
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble lrb_h16_bnljje;
	// 业务招待费上年金额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble lrb_h16_snje;

	// 研究费用-本年累计金额
	@TaxExcelPos(row = 20, col = 2)
	private DZFDouble lrb_h17_bnljje;
	// 研究费用-上年金额
	@TaxExcelPos(row = 20, col = 3)
	private DZFDouble lrb_h17_snje;

	// 财务费用-本年累计金额
	@TaxExcelPos(row = 21, col = 2)
	private DZFDouble lrb_h18_bnljje;
	// 财务费用-上年金额
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble lrb_h18_snje;

	// 其中：利息费用（收入以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble lrb_h19_bnljje;
	// 其中：利息费用（收入以“-”号填列）-上年金额
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble lrb_h19_snje;

	// 加：投资收益（损失以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 23, col = 2)
	private DZFDouble lrb_h20_bnljje;
	// 加：投资收益（损失以“-”号填列）-上年金额
	@TaxExcelPos(row = 23, col = 3)
	private DZFDouble lrb_h20_snje;

	// 二、营业利润（亏损以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble lrb_h21_bnljje;
	// 二、营业利润（亏损以“-”号填列）-上年金额
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble lrb_h21_snje;

	// 加：营业外收入-本年累计金额
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble lrb_h22_bnljje;
	// 加：营业外收入-上年金额
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble lrb_h22_snje;

	// 其中：政府补助-本年累计金额
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble lrb_h23_bnljje;
	// 其中：政府补助-上年金额
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble lrb_h23_snje;

	// 减：营业外支出-本年累计金额
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble lrb_h24_bnljje;
	// 减：营业外支出-上年金额
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble lrb_h24_snje;

	// 其中：坏账损失-本年累计金额
	@TaxExcelPos(row = 28, col = 2)
	private DZFDouble lrb_h25_bnljje;
	// 其中：坏账损失-上年金额
	@TaxExcelPos(row = 28, col = 3)
	private DZFDouble lrb_h25_snje;

	// 无法收回的长期债券投资损失-本年累计金额
	@TaxExcelPos(row = 29, col = 2)
	private DZFDouble lrb_h26_bnljje;
	// 无法收回的长期债券投资损失-上年金额
	@TaxExcelPos(row = 29, col = 3)
	private DZFDouble lrb_h26_snje;

	// 无法收回的长期股权投资损失-本年累计金额
	@TaxExcelPos(row = 30, col = 2)
	private DZFDouble lrb_h27_bnljje;
	// 无法收回的长期股权投资损失-上年金额
	@TaxExcelPos(row = 30, col = 3)
	private DZFDouble lrb_h27_snje;

	// 自然灾害等不可抗力因素造成的损失-本年累计金额
	@TaxExcelPos(row = 31, col = 2)
	private DZFDouble lrb_h28_bnljje;
	// 自然灾害等不可抗力因素造成的损失-上年金额
	@TaxExcelPos(row = 31, col = 3)
	private DZFDouble lrb_h28_snje;

	// 税收滞纳金-本年累计金额
	@TaxExcelPos(row = 32, col = 2)
	private DZFDouble lrb_h29_bnljje;
	// 税收滞纳金-上年金额
	@TaxExcelPos(row = 32, col = 3)
	private DZFDouble lrb_h29_snje;

	// 三、利润总额（亏损总额以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 33, col = 2)
	private DZFDouble lrb_h30_bnljje;
	// 三、利润总额（亏损总额以“-”号填列）-上年金额
	@TaxExcelPos(row = 33, col = 3)
	private DZFDouble lrb_h30_snje;

	// 减：所得税费用-本年累计金额
	@TaxExcelPos(row = 34, col = 2)
	private DZFDouble lrb_h31_bnljje;
	// 减：所得税费用-上年金额
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble lrb_h31_snje;

	// 四、净利润（净亏损以“-”号填列）-本年累计金额
	@TaxExcelPos(row = 35, col = 2)
	private DZFDouble lrb_h32_bnljje;
	// 四、净利润（净亏损以“-”号填列）-上年金额
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble lrb_h32_snje;

	public DZFDouble getLrb_h1_bnljje() {
		return lrb_h1_bnljje;
	}

	public void setLrb_h1_bnljje(DZFDouble lrb_h1_bnljje) {
		this.lrb_h1_bnljje = lrb_h1_bnljje;
	}

	public DZFDouble getLrb_h1_snje() {
		return lrb_h1_snje;
	}

	public void setLrb_h1_snje(DZFDouble lrb_h1_snje) {
		this.lrb_h1_snje = lrb_h1_snje;
	}

	public DZFDouble getLrb_h2_bnljje() {
		return lrb_h2_bnljje;
	}

	public void setLrb_h2_bnljje(DZFDouble lrb_h2_bnljje) {
		this.lrb_h2_bnljje = lrb_h2_bnljje;
	}

	public DZFDouble getLrb_h2_snje() {
		return lrb_h2_snje;
	}

	public void setLrb_h2_snje(DZFDouble lrb_h2_snje) {
		this.lrb_h2_snje = lrb_h2_snje;
	}

	public DZFDouble getLrb_h3_bnljje() {
		return lrb_h3_bnljje;
	}

	public void setLrb_h3_bnljje(DZFDouble lrb_h3_bnljje) {
		this.lrb_h3_bnljje = lrb_h3_bnljje;
	}

	public DZFDouble getLrb_h3_snje() {
		return lrb_h3_snje;
	}

	public void setLrb_h3_snje(DZFDouble lrb_h3_snje) {
		this.lrb_h3_snje = lrb_h3_snje;
	}

	public DZFDouble getLrb_h4_bnljje() {
		return lrb_h4_bnljje;
	}

	public void setLrb_h4_bnljje(DZFDouble lrb_h4_bnljje) {
		this.lrb_h4_bnljje = lrb_h4_bnljje;
	}

	public DZFDouble getLrb_h4_snje() {
		return lrb_h4_snje;
	}

	public void setLrb_h4_snje(DZFDouble lrb_h4_snje) {
		this.lrb_h4_snje = lrb_h4_snje;
	}

	public DZFDouble getLrb_h5_bnljje() {
		return lrb_h5_bnljje;
	}

	public void setLrb_h5_bnljje(DZFDouble lrb_h5_bnljje) {
		this.lrb_h5_bnljje = lrb_h5_bnljje;
	}

	public DZFDouble getLrb_h5_snje() {
		return lrb_h5_snje;
	}

	public void setLrb_h5_snje(DZFDouble lrb_h5_snje) {
		this.lrb_h5_snje = lrb_h5_snje;
	}

	public DZFDouble getLrb_h6_bnljje() {
		return lrb_h6_bnljje;
	}

	public void setLrb_h6_bnljje(DZFDouble lrb_h6_bnljje) {
		this.lrb_h6_bnljje = lrb_h6_bnljje;
	}

	public DZFDouble getLrb_h6_snje() {
		return lrb_h6_snje;
	}

	public void setLrb_h6_snje(DZFDouble lrb_h6_snje) {
		this.lrb_h6_snje = lrb_h6_snje;
	}

	public DZFDouble getLrb_h7_bnljje() {
		return lrb_h7_bnljje;
	}

	public void setLrb_h7_bnljje(DZFDouble lrb_h7_bnljje) {
		this.lrb_h7_bnljje = lrb_h7_bnljje;
	}

	public DZFDouble getLrb_h7_snje() {
		return lrb_h7_snje;
	}

	public void setLrb_h7_snje(DZFDouble lrb_h7_snje) {
		this.lrb_h7_snje = lrb_h7_snje;
	}

	public DZFDouble getLrb_h8_bnljje() {
		return lrb_h8_bnljje;
	}

	public void setLrb_h8_bnljje(DZFDouble lrb_h8_bnljje) {
		this.lrb_h8_bnljje = lrb_h8_bnljje;
	}

	public DZFDouble getLrb_h8_snje() {
		return lrb_h8_snje;
	}

	public void setLrb_h8_snje(DZFDouble lrb_h8_snje) {
		this.lrb_h8_snje = lrb_h8_snje;
	}

	public DZFDouble getLrb_h9_bnljje() {
		return lrb_h9_bnljje;
	}

	public void setLrb_h9_bnljje(DZFDouble lrb_h9_bnljje) {
		this.lrb_h9_bnljje = lrb_h9_bnljje;
	}

	public DZFDouble getLrb_h9_snje() {
		return lrb_h9_snje;
	}

	public void setLrb_h9_snje(DZFDouble lrb_h9_snje) {
		this.lrb_h9_snje = lrb_h9_snje;
	}

	public DZFDouble getLrb_h10_bnljje() {
		return lrb_h10_bnljje;
	}

	public void setLrb_h10_bnljje(DZFDouble lrb_h10_bnljje) {
		this.lrb_h10_bnljje = lrb_h10_bnljje;
	}

	public DZFDouble getLrb_h10_snje() {
		return lrb_h10_snje;
	}

	public void setLrb_h10_snje(DZFDouble lrb_h10_snje) {
		this.lrb_h10_snje = lrb_h10_snje;
	}

	public DZFDouble getLrb_h11_bnljje() {
		return lrb_h11_bnljje;
	}

	public void setLrb_h11_bnljje(DZFDouble lrb_h11_bnljje) {
		this.lrb_h11_bnljje = lrb_h11_bnljje;
	}

	public DZFDouble getLrb_h11_snje() {
		return lrb_h11_snje;
	}

	public void setLrb_h11_snje(DZFDouble lrb_h11_snje) {
		this.lrb_h11_snje = lrb_h11_snje;
	}

	public DZFDouble getLrb_h12_bnljje() {
		return lrb_h12_bnljje;
	}

	public void setLrb_h12_bnljje(DZFDouble lrb_h12_bnljje) {
		this.lrb_h12_bnljje = lrb_h12_bnljje;
	}

	public DZFDouble getLrb_h12_snje() {
		return lrb_h12_snje;
	}

	public void setLrb_h12_snje(DZFDouble lrb_h12_snje) {
		this.lrb_h12_snje = lrb_h12_snje;
	}

	public DZFDouble getLrb_h13_bnljje() {
		return lrb_h13_bnljje;
	}

	public void setLrb_h13_bnljje(DZFDouble lrb_h13_bnljje) {
		this.lrb_h13_bnljje = lrb_h13_bnljje;
	}

	public DZFDouble getLrb_h13_snje() {
		return lrb_h13_snje;
	}

	public void setLrb_h13_snje(DZFDouble lrb_h13_snje) {
		this.lrb_h13_snje = lrb_h13_snje;
	}

	public DZFDouble getLrb_h14_bnljje() {
		return lrb_h14_bnljje;
	}

	public void setLrb_h14_bnljje(DZFDouble lrb_h14_bnljje) {
		this.lrb_h14_bnljje = lrb_h14_bnljje;
	}

	public DZFDouble getLrb_h14_snje() {
		return lrb_h14_snje;
	}

	public void setLrb_h14_snje(DZFDouble lrb_h14_snje) {
		this.lrb_h14_snje = lrb_h14_snje;
	}

	public DZFDouble getLrb_h15_bnljje() {
		return lrb_h15_bnljje;
	}

	public void setLrb_h15_bnljje(DZFDouble lrb_h15_bnljje) {
		this.lrb_h15_bnljje = lrb_h15_bnljje;
	}

	public DZFDouble getLrb_h15_snje() {
		return lrb_h15_snje;
	}

	public void setLrb_h15_snje(DZFDouble lrb_h15_snje) {
		this.lrb_h15_snje = lrb_h15_snje;
	}

	public DZFDouble getLrb_h16_bnljje() {
		return lrb_h16_bnljje;
	}

	public void setLrb_h16_bnljje(DZFDouble lrb_h16_bnljje) {
		this.lrb_h16_bnljje = lrb_h16_bnljje;
	}

	public DZFDouble getLrb_h16_snje() {
		return lrb_h16_snje;
	}

	public void setLrb_h16_snje(DZFDouble lrb_h16_snje) {
		this.lrb_h16_snje = lrb_h16_snje;
	}

	public DZFDouble getLrb_h17_bnljje() {
		return lrb_h17_bnljje;
	}

	public void setLrb_h17_bnljje(DZFDouble lrb_h17_bnljje) {
		this.lrb_h17_bnljje = lrb_h17_bnljje;
	}

	public DZFDouble getLrb_h17_snje() {
		return lrb_h17_snje;
	}

	public void setLrb_h17_snje(DZFDouble lrb_h17_snje) {
		this.lrb_h17_snje = lrb_h17_snje;
	}

	public DZFDouble getLrb_h18_bnljje() {
		return lrb_h18_bnljje;
	}

	public void setLrb_h18_bnljje(DZFDouble lrb_h18_bnljje) {
		this.lrb_h18_bnljje = lrb_h18_bnljje;
	}

	public DZFDouble getLrb_h18_snje() {
		return lrb_h18_snje;
	}

	public void setLrb_h18_snje(DZFDouble lrb_h18_snje) {
		this.lrb_h18_snje = lrb_h18_snje;
	}

	public DZFDouble getLrb_h19_bnljje() {
		return lrb_h19_bnljje;
	}

	public void setLrb_h19_bnljje(DZFDouble lrb_h19_bnljje) {
		this.lrb_h19_bnljje = lrb_h19_bnljje;
	}

	public DZFDouble getLrb_h19_snje() {
		return lrb_h19_snje;
	}

	public void setLrb_h19_snje(DZFDouble lrb_h19_snje) {
		this.lrb_h19_snje = lrb_h19_snje;
	}

	public DZFDouble getLrb_h20_bnljje() {
		return lrb_h20_bnljje;
	}

	public void setLrb_h20_bnljje(DZFDouble lrb_h20_bnljje) {
		this.lrb_h20_bnljje = lrb_h20_bnljje;
	}

	public DZFDouble getLrb_h20_snje() {
		return lrb_h20_snje;
	}

	public void setLrb_h20_snje(DZFDouble lrb_h20_snje) {
		this.lrb_h20_snje = lrb_h20_snje;
	}

	public DZFDouble getLrb_h21_bnljje() {
		return lrb_h21_bnljje;
	}

	public void setLrb_h21_bnljje(DZFDouble lrb_h21_bnljje) {
		this.lrb_h21_bnljje = lrb_h21_bnljje;
	}

	public DZFDouble getLrb_h21_snje() {
		return lrb_h21_snje;
	}

	public void setLrb_h21_snje(DZFDouble lrb_h21_snje) {
		this.lrb_h21_snje = lrb_h21_snje;
	}

	public DZFDouble getLrb_h22_bnljje() {
		return lrb_h22_bnljje;
	}

	public void setLrb_h22_bnljje(DZFDouble lrb_h22_bnljje) {
		this.lrb_h22_bnljje = lrb_h22_bnljje;
	}

	public DZFDouble getLrb_h22_snje() {
		return lrb_h22_snje;
	}

	public void setLrb_h22_snje(DZFDouble lrb_h22_snje) {
		this.lrb_h22_snje = lrb_h22_snje;
	}

	public DZFDouble getLrb_h23_bnljje() {
		return lrb_h23_bnljje;
	}

	public void setLrb_h23_bnljje(DZFDouble lrb_h23_bnljje) {
		this.lrb_h23_bnljje = lrb_h23_bnljje;
	}

	public DZFDouble getLrb_h23_snje() {
		return lrb_h23_snje;
	}

	public void setLrb_h23_snje(DZFDouble lrb_h23_snje) {
		this.lrb_h23_snje = lrb_h23_snje;
	}

	public DZFDouble getLrb_h24_bnljje() {
		return lrb_h24_bnljje;
	}

	public void setLrb_h24_bnljje(DZFDouble lrb_h24_bnljje) {
		this.lrb_h24_bnljje = lrb_h24_bnljje;
	}

	public DZFDouble getLrb_h24_snje() {
		return lrb_h24_snje;
	}

	public void setLrb_h24_snje(DZFDouble lrb_h24_snje) {
		this.lrb_h24_snje = lrb_h24_snje;
	}

	public DZFDouble getLrb_h25_bnljje() {
		return lrb_h25_bnljje;
	}

	public void setLrb_h25_bnljje(DZFDouble lrb_h25_bnljje) {
		this.lrb_h25_bnljje = lrb_h25_bnljje;
	}

	public DZFDouble getLrb_h25_snje() {
		return lrb_h25_snje;
	}

	public void setLrb_h25_snje(DZFDouble lrb_h25_snje) {
		this.lrb_h25_snje = lrb_h25_snje;
	}

	public DZFDouble getLrb_h26_bnljje() {
		return lrb_h26_bnljje;
	}

	public void setLrb_h26_bnljje(DZFDouble lrb_h26_bnljje) {
		this.lrb_h26_bnljje = lrb_h26_bnljje;
	}

	public DZFDouble getLrb_h26_snje() {
		return lrb_h26_snje;
	}

	public void setLrb_h26_snje(DZFDouble lrb_h26_snje) {
		this.lrb_h26_snje = lrb_h26_snje;
	}

	public DZFDouble getLrb_h27_bnljje() {
		return lrb_h27_bnljje;
	}

	public void setLrb_h27_bnljje(DZFDouble lrb_h27_bnljje) {
		this.lrb_h27_bnljje = lrb_h27_bnljje;
	}

	public DZFDouble getLrb_h27_snje() {
		return lrb_h27_snje;
	}

	public void setLrb_h27_snje(DZFDouble lrb_h27_snje) {
		this.lrb_h27_snje = lrb_h27_snje;
	}

	public DZFDouble getLrb_h28_bnljje() {
		return lrb_h28_bnljje;
	}

	public void setLrb_h28_bnljje(DZFDouble lrb_h28_bnljje) {
		this.lrb_h28_bnljje = lrb_h28_bnljje;
	}

	public DZFDouble getLrb_h28_snje() {
		return lrb_h28_snje;
	}

	public void setLrb_h28_snje(DZFDouble lrb_h28_snje) {
		this.lrb_h28_snje = lrb_h28_snje;
	}

	public DZFDouble getLrb_h29_bnljje() {
		return lrb_h29_bnljje;
	}

	public void setLrb_h29_bnljje(DZFDouble lrb_h29_bnljje) {
		this.lrb_h29_bnljje = lrb_h29_bnljje;
	}

	public DZFDouble getLrb_h29_snje() {
		return lrb_h29_snje;
	}

	public void setLrb_h29_snje(DZFDouble lrb_h29_snje) {
		this.lrb_h29_snje = lrb_h29_snje;
	}

	public DZFDouble getLrb_h30_bnljje() {
		return lrb_h30_bnljje;
	}

	public void setLrb_h30_bnljje(DZFDouble lrb_h30_bnljje) {
		this.lrb_h30_bnljje = lrb_h30_bnljje;
	}

	public DZFDouble getLrb_h30_snje() {
		return lrb_h30_snje;
	}

	public void setLrb_h30_snje(DZFDouble lrb_h30_snje) {
		this.lrb_h30_snje = lrb_h30_snje;
	}

	public DZFDouble getLrb_h31_bnljje() {
		return lrb_h31_bnljje;
	}

	public void setLrb_h31_bnljje(DZFDouble lrb_h31_bnljje) {
		this.lrb_h31_bnljje = lrb_h31_bnljje;
	}

	public DZFDouble getLrb_h31_snje() {
		return lrb_h31_snje;
	}

	public void setLrb_h31_snje(DZFDouble lrb_h31_snje) {
		this.lrb_h31_snje = lrb_h31_snje;
	}

	public DZFDouble getLrb_h32_bnljje() {
		return lrb_h32_bnljje;
	}

	public void setLrb_h32_bnljje(DZFDouble lrb_h32_bnljje) {
		this.lrb_h32_bnljje = lrb_h32_bnljje;
	}

	public DZFDouble getLrb_h32_snje() {
		return lrb_h32_snje;
	}

	public void setLrb_h32_snje(DZFDouble lrb_h32_snje) {
		this.lrb_h32_snje = lrb_h32_snje;
	}

}
