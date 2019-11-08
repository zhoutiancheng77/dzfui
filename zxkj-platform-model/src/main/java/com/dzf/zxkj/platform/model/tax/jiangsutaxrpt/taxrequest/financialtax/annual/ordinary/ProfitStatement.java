package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 利润表
@TaxExcelPos(reportID = "39806002", reportname = "利润表")
public class ProfitStatement {
	// 一、营业收入-本期金额
	@TaxExcelPos(row = 4, col = 3)
	private DZFDouble lrb_h1_bqje;
	// 一、营业收入-上期金额
	@TaxExcelPos(row = 4, col = 4)
	private DZFDouble lrb_h1_sqje;

	// 减：营业成本-本期金额
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble lrb_h2_bqje;
	// 减：营业成本-上期金额
	@TaxExcelPos(row = 5, col = 4)
	private DZFDouble lrb_h2_sqje;

	// 营业税金及附加-本期金额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble lrb_h3_bqje;
	// 营业税金及附加-上期金额
	@TaxExcelPos(row = 6, col = 4)
	private DZFDouble lrb_h3_sqje;

	// 销售费用-本期金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble lrb_h4_bqje;
	// 销售费用-上期金额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble lrb_h4_sqje;

	// 管理费用-本期金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble lrb_h5_bqje;
	// 管理费用-上期金额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble lrb_h5_sqje;

	// 财务费用-本期金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble lrb_h6_bqje;
	// 财务费用-上期金额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble lrb_h6_sqje;

	// 资产减值损失-本期金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble lrb_h7_bqje;
	// 资产减值损失-上期金额
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble lrb_h7_sqje;

	// 加：公允价值变动收益（损失以“-”号填列）-本期金额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble lrb_h8_bqje;
	// 加：公允价值变动收益（损失以“-”号填列）-上期金额
	@TaxExcelPos(row = 11, col = 4)
	private DZFDouble lrb_h8_sqje;

	// 投资收益（损失以“-”号填列）-本期金额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble lrb_h9_bqje;
	// 投资收益（损失以“-”号填列）-上期金额
	@TaxExcelPos(row = 12, col = 4)
	private DZFDouble lrb_h9_sqje;

	// 其中：对联营企业和合营企业的投资收益-本期金额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble lrb_h10_bqje;
	// 其中：对联营企业和合营企业的投资收益-上期金额
	@TaxExcelPos(row = 13, col = 4)
	private DZFDouble lrb_h10_sqje;

	// 资产处置收益（损失以“-”号填列）-本期金额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble lrb_h33_bqje;
	// 资产处置收益（损失以“-”号填列）-上期金额
	@TaxExcelPos(row = 14, col = 4)
	private DZFDouble lrb_h33_sqje;

	// 其他收益-本期金额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble lrb_h34_bqje;
	// 其他收益-上期金额
	@TaxExcelPos(row = 15, col = 4)
	private DZFDouble lrb_h34_sqje;

	// 二、营业利润（亏损以“-”号填列）-本期金额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble lrb_h11_bqje;
	// 二、营业利润（亏损以“-”号填列）-上期金额
	@TaxExcelPos(row = 16, col = 4)
	private DZFDouble lrb_h11_sqje;

	// 加：营业外收入-本期金额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble lrb_h12_bqje;
	// 加：营业外收入-上期金额
	@TaxExcelPos(row = 17, col = 4)
	private DZFDouble lrb_h12_sqje;

	// 其中:非流动资产处置利得-本期金额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble lrb_h13_bqje;
	// 其中:非流动资产处置利得-上期金额
	@TaxExcelPos(row = 18, col = 4)
	private DZFDouble lrb_h13_sqje;

	// 减：营业外支出-本期金额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble lrb_h14_bqje;
	// 减：营业外支出-上期金额
	@TaxExcelPos(row = 19, col = 4)
	private DZFDouble lrb_h14_sqje;

	// 其中：非流动资产处置损失-本期金额
	@TaxExcelPos(row = 20, col = 3)
	private DZFDouble lrb_h15_bqje;
	// 其中：非流动资产处置损失-上期金额
	@TaxExcelPos(row = 20, col = 4)
	private DZFDouble lrb_h15_sqje;

	// 三、利润总额（亏损总额以“-”号填列）-本期金额
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble lrb_h16_bqje;
	// 三、利润总额（亏损总额以“-”号填列）-上期金额
	@TaxExcelPos(row = 21, col = 4)
	private DZFDouble lrb_h16_sqje;

	// 减：所得税费用-本期金额
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble lrb_h17_bqje;
	// 减：所得税费用-上期金额
	@TaxExcelPos(row = 22, col = 4)
	private DZFDouble lrb_h17_sqje;

	// 四、净利润（净亏损以“-”号填列）-本期金额
	@TaxExcelPos(row = 23, col = 3)
	private DZFDouble lrb_h18_bqje;
	// 四、净利润（净亏损以“-”号填列）-上期金额
	@TaxExcelPos(row = 23, col = 4)
	private DZFDouble lrb_h18_sqje;

	// （一）持续经营净利润（净亏损以“-”号填列）-本期金额
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble lrb_h35_bqje;
	// （一）持续经营净利润（净亏损以“-”号填列）-上期金额
	@TaxExcelPos(row = 24, col = 4)
	private DZFDouble lrb_h35_sqje;

	// （二）终止经营净利润（净亏损以“-”号填列）-本期金额
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble lrb_h36_bqje;
	// （二）终止经营净利润（净亏损以“-”号填列）-上期金额
	@TaxExcelPos(row = 25, col = 4)
	private DZFDouble lrb_h36_sqje;

	// 五、其他综合收益的税后净额-本期金额
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble lrb_h19_bqje;
	// 五、其他综合收益的税后净额-上期金额
	@TaxExcelPos(row = 26, col = 4)
	private DZFDouble lrb_h19_sqje;

	// （一）以后不能重分类进损益的其他综合收益-本期金额
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble lrb_h20_bqje;
	// （一）以后不能重分类进损益的其他综合收益-上期金额
	@TaxExcelPos(row = 27, col = 4)
	private DZFDouble lrb_h20_sqje;

	// 1.重新计量设定收益计划净负债或净资产的变动-本期金额
	@TaxExcelPos(row = 28, col = 3)
	private DZFDouble lrb_h21_bqje;
	// 1.重新计量设定收益计划净负债或净资产的变动-上期金额
	@TaxExcelPos(row = 28, col = 4)
	private DZFDouble lrb_h21_sqje;

	// 2.权益法下在被投资单位不能重分类进损益的其他综合收益中享有的份额-本期金额
	@TaxExcelPos(row = 29, col = 3)
	private DZFDouble lrb_h22_bqje;
	// 2.权益法下在被投资单位不能重分类进损益的其他综合收益中享有的份额-上期金额
	@TaxExcelPos(row = 29, col = 4)
	private DZFDouble lrb_h22_sqje;

	// （二）以后将重分类进损益的其他综合收益-本期金额
	@TaxExcelPos(row = 30, col = 3)
	private DZFDouble lrb_h23_bqje;
	// （二）以后将重分类进损益的其他综合收益-上期金额
	@TaxExcelPos(row = 30, col = 4)
	private DZFDouble lrb_h23_sqje;

	// 1.权益法下在被投资单位以后将重分类进损益的其他综合收益中享有的份额-本期金额
	@TaxExcelPos(row = 31, col = 3)
	private DZFDouble lrb_h24_bqje;
	// 1.权益法下在被投资单位以后将重分类进损益的其他综合收益中享有的份额-上期金额
	@TaxExcelPos(row = 31, col = 4)
	private DZFDouble lrb_h24_sqje;

	// 2.可供出售金融资产公允价值变动损益-本期金额
	@TaxExcelPos(row = 32, col = 3)
	private DZFDouble lrb_h25_bqje;
	// 2.可供出售金融资产公允价值变动损益-上期金额
	@TaxExcelPos(row = 32, col = 4)
	private DZFDouble lrb_h25_sqje;

	// 3.将有至到期投资重分类可供出售金融资产损益-本期金额
	@TaxExcelPos(row = 33, col = 3)
	private DZFDouble lrb_h26_bqje;
	// 3.将有至到期投资重分类可供出售金融资产损益-上期金额
	@TaxExcelPos(row = 33, col = 4)
	private DZFDouble lrb_h26_sqje;

	// 4.现金流经套期损益的有效部分-本期金额
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble lrb_h27_bqje;
	// 4.现金流经套期损益的有效部分-上期金额
	@TaxExcelPos(row = 34, col = 4)
	private DZFDouble lrb_h27_sqje;

	// 5.外币财务报表折算差额-本期金额
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble lrb_h28_bqje;
	// 5.外币财务报表折算差额-上期金额
	@TaxExcelPos(row = 35, col = 4)
	private DZFDouble lrb_h28_sqje;

	// 六、综合收益总额-本期金额
	@TaxExcelPos(row = 36, col = 3)
	private DZFDouble lrb_h29_bqje;
	// 六、综合收益总额-上期金额
	@TaxExcelPos(row = 36, col = 4)
	private DZFDouble lrb_h29_sqje;

	// 七、每股收益：-本期金额
	@TaxExcelPos(row = 37, col = 3)
	private DZFDouble lrb_h30_bqje;
	// 七、每股收益：-上期金额
	@TaxExcelPos(row = 37, col = 4)
	private DZFDouble lrb_h30_sqje;

	// （一）基本每股收益-本期金额
	@TaxExcelPos(row = 38, col = 3)
	private DZFDouble lrb_h31_bqje;
	// （一）基本每股收益-上期金额
	@TaxExcelPos(row = 38, col = 4)
	private DZFDouble lrb_h31_sqje;

	// （二）稀释每股收益-本期金额
	@TaxExcelPos(row = 39, col = 3)
	private DZFDouble lrb_h32_bqje;
	// （二）稀释每股收益-上期金额
	@TaxExcelPos(row = 39, col = 4)
	private DZFDouble lrb_h32_sqje;

	public DZFDouble getLrb_h1_bqje() {
		return lrb_h1_bqje;
	}

	public void setLrb_h1_bqje(DZFDouble lrb_h1_bqje) {
		this.lrb_h1_bqje = lrb_h1_bqje;
	}

	public DZFDouble getLrb_h1_sqje() {
		return lrb_h1_sqje;
	}

	public void setLrb_h1_sqje(DZFDouble lrb_h1_sqje) {
		this.lrb_h1_sqje = lrb_h1_sqje;
	}

	public DZFDouble getLrb_h2_bqje() {
		return lrb_h2_bqje;
	}

	public void setLrb_h2_bqje(DZFDouble lrb_h2_bqje) {
		this.lrb_h2_bqje = lrb_h2_bqje;
	}

	public DZFDouble getLrb_h2_sqje() {
		return lrb_h2_sqje;
	}

	public void setLrb_h2_sqje(DZFDouble lrb_h2_sqje) {
		this.lrb_h2_sqje = lrb_h2_sqje;
	}

	public DZFDouble getLrb_h3_bqje() {
		return lrb_h3_bqje;
	}

	public void setLrb_h3_bqje(DZFDouble lrb_h3_bqje) {
		this.lrb_h3_bqje = lrb_h3_bqje;
	}

	public DZFDouble getLrb_h3_sqje() {
		return lrb_h3_sqje;
	}

	public void setLrb_h3_sqje(DZFDouble lrb_h3_sqje) {
		this.lrb_h3_sqje = lrb_h3_sqje;
	}

	public DZFDouble getLrb_h4_bqje() {
		return lrb_h4_bqje;
	}

	public void setLrb_h4_bqje(DZFDouble lrb_h4_bqje) {
		this.lrb_h4_bqje = lrb_h4_bqje;
	}

	public DZFDouble getLrb_h4_sqje() {
		return lrb_h4_sqje;
	}

	public void setLrb_h4_sqje(DZFDouble lrb_h4_sqje) {
		this.lrb_h4_sqje = lrb_h4_sqje;
	}

	public DZFDouble getLrb_h5_bqje() {
		return lrb_h5_bqje;
	}

	public void setLrb_h5_bqje(DZFDouble lrb_h5_bqje) {
		this.lrb_h5_bqje = lrb_h5_bqje;
	}

	public DZFDouble getLrb_h5_sqje() {
		return lrb_h5_sqje;
	}

	public void setLrb_h5_sqje(DZFDouble lrb_h5_sqje) {
		this.lrb_h5_sqje = lrb_h5_sqje;
	}

	public DZFDouble getLrb_h6_bqje() {
		return lrb_h6_bqje;
	}

	public void setLrb_h6_bqje(DZFDouble lrb_h6_bqje) {
		this.lrb_h6_bqje = lrb_h6_bqje;
	}

	public DZFDouble getLrb_h6_sqje() {
		return lrb_h6_sqje;
	}

	public void setLrb_h6_sqje(DZFDouble lrb_h6_sqje) {
		this.lrb_h6_sqje = lrb_h6_sqje;
	}

	public DZFDouble getLrb_h7_bqje() {
		return lrb_h7_bqje;
	}

	public void setLrb_h7_bqje(DZFDouble lrb_h7_bqje) {
		this.lrb_h7_bqje = lrb_h7_bqje;
	}

	public DZFDouble getLrb_h7_sqje() {
		return lrb_h7_sqje;
	}

	public void setLrb_h7_sqje(DZFDouble lrb_h7_sqje) {
		this.lrb_h7_sqje = lrb_h7_sqje;
	}

	public DZFDouble getLrb_h8_bqje() {
		return lrb_h8_bqje;
	}

	public void setLrb_h8_bqje(DZFDouble lrb_h8_bqje) {
		this.lrb_h8_bqje = lrb_h8_bqje;
	}

	public DZFDouble getLrb_h8_sqje() {
		return lrb_h8_sqje;
	}

	public void setLrb_h8_sqje(DZFDouble lrb_h8_sqje) {
		this.lrb_h8_sqje = lrb_h8_sqje;
	}

	public DZFDouble getLrb_h9_bqje() {
		return lrb_h9_bqje;
	}

	public void setLrb_h9_bqje(DZFDouble lrb_h9_bqje) {
		this.lrb_h9_bqje = lrb_h9_bqje;
	}

	public DZFDouble getLrb_h9_sqje() {
		return lrb_h9_sqje;
	}

	public void setLrb_h9_sqje(DZFDouble lrb_h9_sqje) {
		this.lrb_h9_sqje = lrb_h9_sqje;
	}

	public DZFDouble getLrb_h10_bqje() {
		return lrb_h10_bqje;
	}

	public void setLrb_h10_bqje(DZFDouble lrb_h10_bqje) {
		this.lrb_h10_bqje = lrb_h10_bqje;
	}

	public DZFDouble getLrb_h10_sqje() {
		return lrb_h10_sqje;
	}

	public void setLrb_h10_sqje(DZFDouble lrb_h10_sqje) {
		this.lrb_h10_sqje = lrb_h10_sqje;
	}

	public DZFDouble getLrb_h33_bqje() {
		return lrb_h33_bqje;
	}

	public void setLrb_h33_bqje(DZFDouble lrb_h33_bqje) {
		this.lrb_h33_bqje = lrb_h33_bqje;
	}

	public DZFDouble getLrb_h33_sqje() {
		return lrb_h33_sqje;
	}

	public void setLrb_h33_sqje(DZFDouble lrb_h33_sqje) {
		this.lrb_h33_sqje = lrb_h33_sqje;
	}

	public DZFDouble getLrb_h34_bqje() {
		return lrb_h34_bqje;
	}

	public void setLrb_h34_bqje(DZFDouble lrb_h34_bqje) {
		this.lrb_h34_bqje = lrb_h34_bqje;
	}

	public DZFDouble getLrb_h34_sqje() {
		return lrb_h34_sqje;
	}

	public void setLrb_h34_sqje(DZFDouble lrb_h34_sqje) {
		this.lrb_h34_sqje = lrb_h34_sqje;
	}

	public DZFDouble getLrb_h11_bqje() {
		return lrb_h11_bqje;
	}

	public void setLrb_h11_bqje(DZFDouble lrb_h11_bqje) {
		this.lrb_h11_bqje = lrb_h11_bqje;
	}

	public DZFDouble getLrb_h11_sqje() {
		return lrb_h11_sqje;
	}

	public void setLrb_h11_sqje(DZFDouble lrb_h11_sqje) {
		this.lrb_h11_sqje = lrb_h11_sqje;
	}

	public DZFDouble getLrb_h12_bqje() {
		return lrb_h12_bqje;
	}

	public void setLrb_h12_bqje(DZFDouble lrb_h12_bqje) {
		this.lrb_h12_bqje = lrb_h12_bqje;
	}

	public DZFDouble getLrb_h12_sqje() {
		return lrb_h12_sqje;
	}

	public void setLrb_h12_sqje(DZFDouble lrb_h12_sqje) {
		this.lrb_h12_sqje = lrb_h12_sqje;
	}

	public DZFDouble getLrb_h13_bqje() {
		return lrb_h13_bqje;
	}

	public void setLrb_h13_bqje(DZFDouble lrb_h13_bqje) {
		this.lrb_h13_bqje = lrb_h13_bqje;
	}

	public DZFDouble getLrb_h13_sqje() {
		return lrb_h13_sqje;
	}

	public void setLrb_h13_sqje(DZFDouble lrb_h13_sqje) {
		this.lrb_h13_sqje = lrb_h13_sqje;
	}

	public DZFDouble getLrb_h14_bqje() {
		return lrb_h14_bqje;
	}

	public void setLrb_h14_bqje(DZFDouble lrb_h14_bqje) {
		this.lrb_h14_bqje = lrb_h14_bqje;
	}

	public DZFDouble getLrb_h14_sqje() {
		return lrb_h14_sqje;
	}

	public void setLrb_h14_sqje(DZFDouble lrb_h14_sqje) {
		this.lrb_h14_sqje = lrb_h14_sqje;
	}

	public DZFDouble getLrb_h15_bqje() {
		return lrb_h15_bqje;
	}

	public void setLrb_h15_bqje(DZFDouble lrb_h15_bqje) {
		this.lrb_h15_bqje = lrb_h15_bqje;
	}

	public DZFDouble getLrb_h15_sqje() {
		return lrb_h15_sqje;
	}

	public void setLrb_h15_sqje(DZFDouble lrb_h15_sqje) {
		this.lrb_h15_sqje = lrb_h15_sqje;
	}

	public DZFDouble getLrb_h16_bqje() {
		return lrb_h16_bqje;
	}

	public void setLrb_h16_bqje(DZFDouble lrb_h16_bqje) {
		this.lrb_h16_bqje = lrb_h16_bqje;
	}

	public DZFDouble getLrb_h16_sqje() {
		return lrb_h16_sqje;
	}

	public void setLrb_h16_sqje(DZFDouble lrb_h16_sqje) {
		this.lrb_h16_sqje = lrb_h16_sqje;
	}

	public DZFDouble getLrb_h17_bqje() {
		return lrb_h17_bqje;
	}

	public void setLrb_h17_bqje(DZFDouble lrb_h17_bqje) {
		this.lrb_h17_bqje = lrb_h17_bqje;
	}

	public DZFDouble getLrb_h17_sqje() {
		return lrb_h17_sqje;
	}

	public void setLrb_h17_sqje(DZFDouble lrb_h17_sqje) {
		this.lrb_h17_sqje = lrb_h17_sqje;
	}

	public DZFDouble getLrb_h18_bqje() {
		return lrb_h18_bqje;
	}

	public void setLrb_h18_bqje(DZFDouble lrb_h18_bqje) {
		this.lrb_h18_bqje = lrb_h18_bqje;
	}

	public DZFDouble getLrb_h18_sqje() {
		return lrb_h18_sqje;
	}

	public void setLrb_h18_sqje(DZFDouble lrb_h18_sqje) {
		this.lrb_h18_sqje = lrb_h18_sqje;
	}

	public DZFDouble getLrb_h35_bqje() {
		return lrb_h35_bqje;
	}

	public void setLrb_h35_bqje(DZFDouble lrb_h35_bqje) {
		this.lrb_h35_bqje = lrb_h35_bqje;
	}

	public DZFDouble getLrb_h35_sqje() {
		return lrb_h35_sqje;
	}

	public void setLrb_h35_sqje(DZFDouble lrb_h35_sqje) {
		this.lrb_h35_sqje = lrb_h35_sqje;
	}

	public DZFDouble getLrb_h36_bqje() {
		return lrb_h36_bqje;
	}

	public void setLrb_h36_bqje(DZFDouble lrb_h36_bqje) {
		this.lrb_h36_bqje = lrb_h36_bqje;
	}

	public DZFDouble getLrb_h36_sqje() {
		return lrb_h36_sqje;
	}

	public void setLrb_h36_sqje(DZFDouble lrb_h36_sqje) {
		this.lrb_h36_sqje = lrb_h36_sqje;
	}

	public DZFDouble getLrb_h19_bqje() {
		return lrb_h19_bqje;
	}

	public void setLrb_h19_bqje(DZFDouble lrb_h19_bqje) {
		this.lrb_h19_bqje = lrb_h19_bqje;
	}

	public DZFDouble getLrb_h19_sqje() {
		return lrb_h19_sqje;
	}

	public void setLrb_h19_sqje(DZFDouble lrb_h19_sqje) {
		this.lrb_h19_sqje = lrb_h19_sqje;
	}

	public DZFDouble getLrb_h20_bqje() {
		return lrb_h20_bqje;
	}

	public void setLrb_h20_bqje(DZFDouble lrb_h20_bqje) {
		this.lrb_h20_bqje = lrb_h20_bqje;
	}

	public DZFDouble getLrb_h20_sqje() {
		return lrb_h20_sqje;
	}

	public void setLrb_h20_sqje(DZFDouble lrb_h20_sqje) {
		this.lrb_h20_sqje = lrb_h20_sqje;
	}

	public DZFDouble getLrb_h21_bqje() {
		return lrb_h21_bqje;
	}

	public void setLrb_h21_bqje(DZFDouble lrb_h21_bqje) {
		this.lrb_h21_bqje = lrb_h21_bqje;
	}

	public DZFDouble getLrb_h21_sqje() {
		return lrb_h21_sqje;
	}

	public void setLrb_h21_sqje(DZFDouble lrb_h21_sqje) {
		this.lrb_h21_sqje = lrb_h21_sqje;
	}

	public DZFDouble getLrb_h22_bqje() {
		return lrb_h22_bqje;
	}

	public void setLrb_h22_bqje(DZFDouble lrb_h22_bqje) {
		this.lrb_h22_bqje = lrb_h22_bqje;
	}

	public DZFDouble getLrb_h22_sqje() {
		return lrb_h22_sqje;
	}

	public void setLrb_h22_sqje(DZFDouble lrb_h22_sqje) {
		this.lrb_h22_sqje = lrb_h22_sqje;
	}

	public DZFDouble getLrb_h23_bqje() {
		return lrb_h23_bqje;
	}

	public void setLrb_h23_bqje(DZFDouble lrb_h23_bqje) {
		this.lrb_h23_bqje = lrb_h23_bqje;
	}

	public DZFDouble getLrb_h23_sqje() {
		return lrb_h23_sqje;
	}

	public void setLrb_h23_sqje(DZFDouble lrb_h23_sqje) {
		this.lrb_h23_sqje = lrb_h23_sqje;
	}

	public DZFDouble getLrb_h24_bqje() {
		return lrb_h24_bqje;
	}

	public void setLrb_h24_bqje(DZFDouble lrb_h24_bqje) {
		this.lrb_h24_bqje = lrb_h24_bqje;
	}

	public DZFDouble getLrb_h24_sqje() {
		return lrb_h24_sqje;
	}

	public void setLrb_h24_sqje(DZFDouble lrb_h24_sqje) {
		this.lrb_h24_sqje = lrb_h24_sqje;
	}

	public DZFDouble getLrb_h25_bqje() {
		return lrb_h25_bqje;
	}

	public void setLrb_h25_bqje(DZFDouble lrb_h25_bqje) {
		this.lrb_h25_bqje = lrb_h25_bqje;
	}

	public DZFDouble getLrb_h25_sqje() {
		return lrb_h25_sqje;
	}

	public void setLrb_h25_sqje(DZFDouble lrb_h25_sqje) {
		this.lrb_h25_sqje = lrb_h25_sqje;
	}

	public DZFDouble getLrb_h26_bqje() {
		return lrb_h26_bqje;
	}

	public void setLrb_h26_bqje(DZFDouble lrb_h26_bqje) {
		this.lrb_h26_bqje = lrb_h26_bqje;
	}

	public DZFDouble getLrb_h26_sqje() {
		return lrb_h26_sqje;
	}

	public void setLrb_h26_sqje(DZFDouble lrb_h26_sqje) {
		this.lrb_h26_sqje = lrb_h26_sqje;
	}

	public DZFDouble getLrb_h27_bqje() {
		return lrb_h27_bqje;
	}

	public void setLrb_h27_bqje(DZFDouble lrb_h27_bqje) {
		this.lrb_h27_bqje = lrb_h27_bqje;
	}

	public DZFDouble getLrb_h27_sqje() {
		return lrb_h27_sqje;
	}

	public void setLrb_h27_sqje(DZFDouble lrb_h27_sqje) {
		this.lrb_h27_sqje = lrb_h27_sqje;
	}

	public DZFDouble getLrb_h28_bqje() {
		return lrb_h28_bqje;
	}

	public void setLrb_h28_bqje(DZFDouble lrb_h28_bqje) {
		this.lrb_h28_bqje = lrb_h28_bqje;
	}

	public DZFDouble getLrb_h28_sqje() {
		return lrb_h28_sqje;
	}

	public void setLrb_h28_sqje(DZFDouble lrb_h28_sqje) {
		this.lrb_h28_sqje = lrb_h28_sqje;
	}

	public DZFDouble getLrb_h29_bqje() {
		return lrb_h29_bqje;
	}

	public void setLrb_h29_bqje(DZFDouble lrb_h29_bqje) {
		this.lrb_h29_bqje = lrb_h29_bqje;
	}

	public DZFDouble getLrb_h29_sqje() {
		return lrb_h29_sqje;
	}

	public void setLrb_h29_sqje(DZFDouble lrb_h29_sqje) {
		this.lrb_h29_sqje = lrb_h29_sqje;
	}

	public DZFDouble getLrb_h30_bqje() {
		return lrb_h30_bqje;
	}

	public void setLrb_h30_bqje(DZFDouble lrb_h30_bqje) {
		this.lrb_h30_bqje = lrb_h30_bqje;
	}

	public DZFDouble getLrb_h30_sqje() {
		return lrb_h30_sqje;
	}

	public void setLrb_h30_sqje(DZFDouble lrb_h30_sqje) {
		this.lrb_h30_sqje = lrb_h30_sqje;
	}

	public DZFDouble getLrb_h31_bqje() {
		return lrb_h31_bqje;
	}

	public void setLrb_h31_bqje(DZFDouble lrb_h31_bqje) {
		this.lrb_h31_bqje = lrb_h31_bqje;
	}

	public DZFDouble getLrb_h31_sqje() {
		return lrb_h31_sqje;
	}

	public void setLrb_h31_sqje(DZFDouble lrb_h31_sqje) {
		this.lrb_h31_sqje = lrb_h31_sqje;
	}

	public DZFDouble getLrb_h32_bqje() {
		return lrb_h32_bqje;
	}

	public void setLrb_h32_bqje(DZFDouble lrb_h32_bqje) {
		this.lrb_h32_bqje = lrb_h32_bqje;
	}

	public DZFDouble getLrb_h32_sqje() {
		return lrb_h32_sqje;
	}

	public void setLrb_h32_sqje(DZFDouble lrb_h32_sqje) {
		this.lrb_h32_sqje = lrb_h32_sqje;
	}
}
