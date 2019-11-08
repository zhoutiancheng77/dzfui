package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 现金流量表
@TaxExcelPos(reportID = "39806003", reportname = "现金流量表")
public class CashFlowStatement {
	// 销售商品、提供劳务收到的现金-本期金额
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble xjllb_h2_bqje;
	// 销售商品、提供劳务收到的现金-上期金额
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble xjllb_h2_sqje;

	// 收到的税费返还-本期金额
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble xjllb_h3_bqje;
	// 收到的税费返还-上期金额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble xjllb_h3_sqje;

	// 收到其他与经营活动有关的现金-本期金额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble xjllb_h4_bqje;
	// 收到其他与经营活动有关的现金-上期金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble xjllb_h4_sqje;

	// 经营活动现金流入小计-本期金额
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble xjllb_h5_bqje;
	// 经营活动现金流入小计-上期金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble xjllb_h5_sqje;

	// 购买商品、接受劳务支付的现金-本期金额
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble xjllb_h6_bqje;
	// 购买商品、接受劳务支付的现金-上期金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble xjllb_h6_sqje;

	// 支付给职工以及为职工支付的现金-本期金额
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble xjllb_h7_bqje;
	// 支付给职工以及为职工支付的现金-上期金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble xjllb_h7_sqje;

	// 支付的各项税费-本期金额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble xjllb_h8_bqje;
	// 支付的各项税费-上期金额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble xjllb_h8_sqje;

	// 支付其他与经营活动有关的现金-本期金额
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble xjllb_h9_bqje;
	// 支付其他与经营活动有关的现金-上期金额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble xjllb_h9_sqje;

	// 经营活动现金流出小计-本期金额
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble xjllb_h10_bqje;
	// 经营活动现金流出小计-上期金额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble xjllb_h10_sqje;

	// 经营活动产生的现金流量净额-本期金额
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble xjllb_h11_bqje;
	// 经营活动产生的现金流量净额-上期金额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble xjllb_h11_sqje;

	// 收回投资收到的现金-本期金额
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble xjllb_h13_bqje;
	// 收回投资收到的现金-上期金额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble xjllb_h13_sqje;

	// 取得投资收益收到的现金-本期金额
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble xjllb_h14_bqje;
	// 取得投资收益收到的现金-上期金额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble xjllb_h14_sqje;

	// 处置固定资产、无形资产和其他长期资产收回的现金净额-本期金额
	@TaxExcelPos(row = 18, col = 2)
	private DZFDouble xjllb_h15_bqje;
	// 处置固定资产、无形资产和其他长期资产收回的现金净额-上期金额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble xjllb_h15_sqje;

	// 处置子公司及其他营业单位收到的现金净额-本期金额
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble xjllb_h16_bqje;
	// 处置子公司及其他营业单位收到的现金净额-上期金额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble xjllb_h16_sqje;

	// 收到其他与投资活动有关的现金-本期金额
	@TaxExcelPos(row = 20, col = 2)
	private DZFDouble xjllb_h17_bqje;
	// 收到其他与投资活动有关的现金-上期金额
	@TaxExcelPos(row = 20, col = 3)
	private DZFDouble xjllb_h17_sqje;

	// 投资活动现金流入小计-本期金额
	@TaxExcelPos(row = 21, col = 2)
	private DZFDouble xjllb_h18_bqje;
	// 投资活动现金流入小计-上期金额
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble xjllb_h18_sqje;

	// 购建固定资产、无形资产和其他长期资产支付的现金-本期金额
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble xjllb_h19_bqje;
	// 购建固定资产、无形资产和其他长期资产支付的现金-上期金额
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble xjllb_h19_sqje;

	// 投资支付的现金-本期金额
	@TaxExcelPos(row = 23, col = 2)
	private DZFDouble xjllb_h20_bqje;
	// 投资支付的现金-上期金额
	@TaxExcelPos(row = 23, col = 3)
	private DZFDouble xjllb_h20_sqje;

	// 取得子公司及其他营业单位支付的现金净额-本期金额
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble xjllb_h21_bqje;
	// 取得子公司及其他营业单位支付的现金净额-上期金额
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble xjllb_h21_sqje;

	// 支付其他与投资活动有关的现金-本期金额
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble xjllb_h22_bqje;
	// 支付其他与投资活动有关的现金-上期金额
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble xjllb_h22_sqje;

	// 投资活动现金流出小计-本期金额
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble xjllb_h23_bqje;
	// 投资活动现金流出小计-上期金额
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble xjllb_h23_sqje;

	// 投资活动产生的现金流量净额-本期金额
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble xjllb_h24_bqje;
	// 投资活动产生的现金流量净额-上期金额
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble xjllb_h24_sqje;

	// 吸收投资收到的现金-本期金额
	@TaxExcelPos(row = 29, col = 2)
	private DZFDouble xjllb_h26_bqje;
	// 吸收投资收到的现金-上期金额
	@TaxExcelPos(row = 29, col = 3)
	private DZFDouble xjllb_h26_sqje;

	// 取得借款收到的现金-本期金额
	@TaxExcelPos(row = 30, col = 2)
	private DZFDouble xjllb_h27_bqje;
	// 取得借款收到的现金-上期金额
	@TaxExcelPos(row = 30, col = 3)
	private DZFDouble xjllb_h27_sqje;

	// 收到其他与筹资活动有关的现金-本期金额
	@TaxExcelPos(row = 31, col = 2)
	private DZFDouble xjllb_h28_bqje;
	// 收到其他与筹资活动有关的现金-上期金额
	@TaxExcelPos(row = 31, col = 3)
	private DZFDouble xjllb_h28_sqje;

	// 筹资活动现金流入小计-本期金额
	@TaxExcelPos(row = 32, col = 2)
	private DZFDouble xjllb_h29_bqje;
	// 筹资活动现金流入小计-上期金额
	@TaxExcelPos(row = 32, col = 3)
	private DZFDouble xjllb_h29_sqje;

	// 偿还债务支付的现金-本期金额
	@TaxExcelPos(row = 33, col = 2)
	private DZFDouble xjllb_h30_bqje;
	// 偿还债务支付的现金-上期金额
	@TaxExcelPos(row = 33, col = 3)
	private DZFDouble xjllb_h30_sqje;

	// 分配股利、利润或偿付利息支付的现金-本期金额
	@TaxExcelPos(row = 34, col = 2)
	private DZFDouble xjllb_h31_bqje;
	// 分配股利、利润或偿付利息支付的现金-上期金额
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble xjllb_h31_sqje;

	// 支付其他与筹资活动有关的现金-本期金额
	@TaxExcelPos(row = 35, col = 2)
	private DZFDouble xjllb_h32_bqje;
	// 支付其他与筹资活动有关的现金-上期金额
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble xjllb_h32_sqje;

	// 筹资活动现金流出小计-本期金额
	@TaxExcelPos(row = 36, col = 2)
	private DZFDouble xjllb_h33_bqje;
	// 筹资活动现金流出小计-上期金额
	@TaxExcelPos(row = 36, col = 3)
	private DZFDouble xjllb_h33_sqje;

	// 筹资活动产生的现金流量净额-本期金额
	@TaxExcelPos(row = 37, col = 2)
	private DZFDouble xjllb_h34_bqje;
	// 筹资活动产生的现金流量净额-上期金额
	@TaxExcelPos(row = 37, col = 3)
	private DZFDouble xjllb_h34_sqje;

	// 四、汇率变动对现金及现金等价物的影响-本期金额
	@TaxExcelPos(row = 38, col = 2)
	private DZFDouble xjllb_h35_bqje;
	// 四、汇率变动对现金及现金等价物的影响-上期金额
	@TaxExcelPos(row = 38, col = 3)
	private DZFDouble xjllb_h35_sqje;

	// 五、现金及现金等价物净增加额-本期金额
	@TaxExcelPos(row = 39, col = 2)
	private DZFDouble xjllb_h36_bqje;
	// 五、现金及现金等价物净增加额-上期金额
	@TaxExcelPos(row = 39, col = 3)
	private DZFDouble xjllb_h36_sqje;

	// 加：期初现金及现金等价物余额-本期金额
	@TaxExcelPos(row = 40, col = 2)
	private DZFDouble xjllb_h37_bqje;
	// 加：期初现金及现金等价物余额-上期金额
	@TaxExcelPos(row = 40, col = 3)
	private DZFDouble xjllb_h37_sqje;

	// 六、期末现金及现金等价物余额-本期金额
	@TaxExcelPos(row = 41, col = 2)
	private DZFDouble xjllb_h38_bqje;
	// 六、期末现金及现金等价物余额-上期金额
	@TaxExcelPos(row = 41, col = 3)
	private DZFDouble xjllb_h38_sqje;

	public DZFDouble getXjllb_h2_bqje() {
		return xjllb_h2_bqje;
	}

	public void setXjllb_h2_bqje(DZFDouble xjllb_h2_bqje) {
		this.xjllb_h2_bqje = xjllb_h2_bqje;
	}

	public DZFDouble getXjllb_h2_sqje() {
		return xjllb_h2_sqje;
	}

	public void setXjllb_h2_sqje(DZFDouble xjllb_h2_sqje) {
		this.xjllb_h2_sqje = xjllb_h2_sqje;
	}

	public DZFDouble getXjllb_h3_bqje() {
		return xjllb_h3_bqje;
	}

	public void setXjllb_h3_bqje(DZFDouble xjllb_h3_bqje) {
		this.xjllb_h3_bqje = xjllb_h3_bqje;
	}

	public DZFDouble getXjllb_h3_sqje() {
		return xjllb_h3_sqje;
	}

	public void setXjllb_h3_sqje(DZFDouble xjllb_h3_sqje) {
		this.xjllb_h3_sqje = xjllb_h3_sqje;
	}

	public DZFDouble getXjllb_h4_bqje() {
		return xjllb_h4_bqje;
	}

	public void setXjllb_h4_bqje(DZFDouble xjllb_h4_bqje) {
		this.xjllb_h4_bqje = xjllb_h4_bqje;
	}

	public DZFDouble getXjllb_h4_sqje() {
		return xjllb_h4_sqje;
	}

	public void setXjllb_h4_sqje(DZFDouble xjllb_h4_sqje) {
		this.xjllb_h4_sqje = xjllb_h4_sqje;
	}

	public DZFDouble getXjllb_h5_bqje() {
		return xjllb_h5_bqje;
	}

	public void setXjllb_h5_bqje(DZFDouble xjllb_h5_bqje) {
		this.xjllb_h5_bqje = xjllb_h5_bqje;
	}

	public DZFDouble getXjllb_h5_sqje() {
		return xjllb_h5_sqje;
	}

	public void setXjllb_h5_sqje(DZFDouble xjllb_h5_sqje) {
		this.xjllb_h5_sqje = xjllb_h5_sqje;
	}

	public DZFDouble getXjllb_h6_bqje() {
		return xjllb_h6_bqje;
	}

	public void setXjllb_h6_bqje(DZFDouble xjllb_h6_bqje) {
		this.xjllb_h6_bqje = xjllb_h6_bqje;
	}

	public DZFDouble getXjllb_h6_sqje() {
		return xjllb_h6_sqje;
	}

	public void setXjllb_h6_sqje(DZFDouble xjllb_h6_sqje) {
		this.xjllb_h6_sqje = xjllb_h6_sqje;
	}

	public DZFDouble getXjllb_h7_bqje() {
		return xjllb_h7_bqje;
	}

	public void setXjllb_h7_bqje(DZFDouble xjllb_h7_bqje) {
		this.xjllb_h7_bqje = xjllb_h7_bqje;
	}

	public DZFDouble getXjllb_h7_sqje() {
		return xjllb_h7_sqje;
	}

	public void setXjllb_h7_sqje(DZFDouble xjllb_h7_sqje) {
		this.xjllb_h7_sqje = xjllb_h7_sqje;
	}

	public DZFDouble getXjllb_h8_bqje() {
		return xjllb_h8_bqje;
	}

	public void setXjllb_h8_bqje(DZFDouble xjllb_h8_bqje) {
		this.xjllb_h8_bqje = xjllb_h8_bqje;
	}

	public DZFDouble getXjllb_h8_sqje() {
		return xjllb_h8_sqje;
	}

	public void setXjllb_h8_sqje(DZFDouble xjllb_h8_sqje) {
		this.xjllb_h8_sqje = xjllb_h8_sqje;
	}

	public DZFDouble getXjllb_h9_bqje() {
		return xjllb_h9_bqje;
	}

	public void setXjllb_h9_bqje(DZFDouble xjllb_h9_bqje) {
		this.xjllb_h9_bqje = xjllb_h9_bqje;
	}

	public DZFDouble getXjllb_h9_sqje() {
		return xjllb_h9_sqje;
	}

	public void setXjllb_h9_sqje(DZFDouble xjllb_h9_sqje) {
		this.xjllb_h9_sqje = xjllb_h9_sqje;
	}

	public DZFDouble getXjllb_h10_bqje() {
		return xjllb_h10_bqje;
	}

	public void setXjllb_h10_bqje(DZFDouble xjllb_h10_bqje) {
		this.xjllb_h10_bqje = xjllb_h10_bqje;
	}

	public DZFDouble getXjllb_h10_sqje() {
		return xjllb_h10_sqje;
	}

	public void setXjllb_h10_sqje(DZFDouble xjllb_h10_sqje) {
		this.xjllb_h10_sqje = xjllb_h10_sqje;
	}

	public DZFDouble getXjllb_h11_bqje() {
		return xjllb_h11_bqje;
	}

	public void setXjllb_h11_bqje(DZFDouble xjllb_h11_bqje) {
		this.xjllb_h11_bqje = xjllb_h11_bqje;
	}

	public DZFDouble getXjllb_h11_sqje() {
		return xjllb_h11_sqje;
	}

	public void setXjllb_h11_sqje(DZFDouble xjllb_h11_sqje) {
		this.xjllb_h11_sqje = xjllb_h11_sqje;
	}

	public DZFDouble getXjllb_h13_bqje() {
		return xjllb_h13_bqje;
	}

	public void setXjllb_h13_bqje(DZFDouble xjllb_h13_bqje) {
		this.xjllb_h13_bqje = xjllb_h13_bqje;
	}

	public DZFDouble getXjllb_h13_sqje() {
		return xjllb_h13_sqje;
	}

	public void setXjllb_h13_sqje(DZFDouble xjllb_h13_sqje) {
		this.xjllb_h13_sqje = xjllb_h13_sqje;
	}

	public DZFDouble getXjllb_h14_bqje() {
		return xjllb_h14_bqje;
	}

	public void setXjllb_h14_bqje(DZFDouble xjllb_h14_bqje) {
		this.xjllb_h14_bqje = xjllb_h14_bqje;
	}

	public DZFDouble getXjllb_h14_sqje() {
		return xjllb_h14_sqje;
	}

	public void setXjllb_h14_sqje(DZFDouble xjllb_h14_sqje) {
		this.xjllb_h14_sqje = xjllb_h14_sqje;
	}

	public DZFDouble getXjllb_h15_bqje() {
		return xjllb_h15_bqje;
	}

	public void setXjllb_h15_bqje(DZFDouble xjllb_h15_bqje) {
		this.xjllb_h15_bqje = xjllb_h15_bqje;
	}

	public DZFDouble getXjllb_h15_sqje() {
		return xjllb_h15_sqje;
	}

	public void setXjllb_h15_sqje(DZFDouble xjllb_h15_sqje) {
		this.xjllb_h15_sqje = xjllb_h15_sqje;
	}

	public DZFDouble getXjllb_h16_bqje() {
		return xjllb_h16_bqje;
	}

	public void setXjllb_h16_bqje(DZFDouble xjllb_h16_bqje) {
		this.xjllb_h16_bqje = xjllb_h16_bqje;
	}

	public DZFDouble getXjllb_h16_sqje() {
		return xjllb_h16_sqje;
	}

	public void setXjllb_h16_sqje(DZFDouble xjllb_h16_sqje) {
		this.xjllb_h16_sqje = xjllb_h16_sqje;
	}

	public DZFDouble getXjllb_h17_bqje() {
		return xjllb_h17_bqje;
	}

	public void setXjllb_h17_bqje(DZFDouble xjllb_h17_bqje) {
		this.xjllb_h17_bqje = xjllb_h17_bqje;
	}

	public DZFDouble getXjllb_h17_sqje() {
		return xjllb_h17_sqje;
	}

	public void setXjllb_h17_sqje(DZFDouble xjllb_h17_sqje) {
		this.xjllb_h17_sqje = xjllb_h17_sqje;
	}

	public DZFDouble getXjllb_h18_bqje() {
		return xjllb_h18_bqje;
	}

	public void setXjllb_h18_bqje(DZFDouble xjllb_h18_bqje) {
		this.xjllb_h18_bqje = xjllb_h18_bqje;
	}

	public DZFDouble getXjllb_h18_sqje() {
		return xjllb_h18_sqje;
	}

	public void setXjllb_h18_sqje(DZFDouble xjllb_h18_sqje) {
		this.xjllb_h18_sqje = xjllb_h18_sqje;
	}

	public DZFDouble getXjllb_h19_bqje() {
		return xjllb_h19_bqje;
	}

	public void setXjllb_h19_bqje(DZFDouble xjllb_h19_bqje) {
		this.xjllb_h19_bqje = xjllb_h19_bqje;
	}

	public DZFDouble getXjllb_h19_sqje() {
		return xjllb_h19_sqje;
	}

	public void setXjllb_h19_sqje(DZFDouble xjllb_h19_sqje) {
		this.xjllb_h19_sqje = xjllb_h19_sqje;
	}

	public DZFDouble getXjllb_h20_bqje() {
		return xjllb_h20_bqje;
	}

	public void setXjllb_h20_bqje(DZFDouble xjllb_h20_bqje) {
		this.xjllb_h20_bqje = xjllb_h20_bqje;
	}

	public DZFDouble getXjllb_h20_sqje() {
		return xjllb_h20_sqje;
	}

	public void setXjllb_h20_sqje(DZFDouble xjllb_h20_sqje) {
		this.xjllb_h20_sqje = xjllb_h20_sqje;
	}

	public DZFDouble getXjllb_h21_bqje() {
		return xjllb_h21_bqje;
	}

	public void setXjllb_h21_bqje(DZFDouble xjllb_h21_bqje) {
		this.xjllb_h21_bqje = xjllb_h21_bqje;
	}

	public DZFDouble getXjllb_h21_sqje() {
		return xjllb_h21_sqje;
	}

	public void setXjllb_h21_sqje(DZFDouble xjllb_h21_sqje) {
		this.xjllb_h21_sqje = xjllb_h21_sqje;
	}

	public DZFDouble getXjllb_h22_bqje() {
		return xjllb_h22_bqje;
	}

	public void setXjllb_h22_bqje(DZFDouble xjllb_h22_bqje) {
		this.xjllb_h22_bqje = xjllb_h22_bqje;
	}

	public DZFDouble getXjllb_h22_sqje() {
		return xjllb_h22_sqje;
	}

	public void setXjllb_h22_sqje(DZFDouble xjllb_h22_sqje) {
		this.xjllb_h22_sqje = xjllb_h22_sqje;
	}

	public DZFDouble getXjllb_h23_bqje() {
		return xjllb_h23_bqje;
	}

	public void setXjllb_h23_bqje(DZFDouble xjllb_h23_bqje) {
		this.xjllb_h23_bqje = xjllb_h23_bqje;
	}

	public DZFDouble getXjllb_h23_sqje() {
		return xjllb_h23_sqje;
	}

	public void setXjllb_h23_sqje(DZFDouble xjllb_h23_sqje) {
		this.xjllb_h23_sqje = xjllb_h23_sqje;
	}

	public DZFDouble getXjllb_h24_bqje() {
		return xjllb_h24_bqje;
	}

	public void setXjllb_h24_bqje(DZFDouble xjllb_h24_bqje) {
		this.xjllb_h24_bqje = xjllb_h24_bqje;
	}

	public DZFDouble getXjllb_h24_sqje() {
		return xjllb_h24_sqje;
	}

	public void setXjllb_h24_sqje(DZFDouble xjllb_h24_sqje) {
		this.xjllb_h24_sqje = xjllb_h24_sqje;
	}

	public DZFDouble getXjllb_h26_bqje() {
		return xjllb_h26_bqje;
	}

	public void setXjllb_h26_bqje(DZFDouble xjllb_h26_bqje) {
		this.xjllb_h26_bqje = xjllb_h26_bqje;
	}

	public DZFDouble getXjllb_h26_sqje() {
		return xjllb_h26_sqje;
	}

	public void setXjllb_h26_sqje(DZFDouble xjllb_h26_sqje) {
		this.xjllb_h26_sqje = xjllb_h26_sqje;
	}

	public DZFDouble getXjllb_h27_bqje() {
		return xjllb_h27_bqje;
	}

	public void setXjllb_h27_bqje(DZFDouble xjllb_h27_bqje) {
		this.xjllb_h27_bqje = xjllb_h27_bqje;
	}

	public DZFDouble getXjllb_h27_sqje() {
		return xjllb_h27_sqje;
	}

	public void setXjllb_h27_sqje(DZFDouble xjllb_h27_sqje) {
		this.xjllb_h27_sqje = xjllb_h27_sqje;
	}

	public DZFDouble getXjllb_h28_bqje() {
		return xjllb_h28_bqje;
	}

	public void setXjllb_h28_bqje(DZFDouble xjllb_h28_bqje) {
		this.xjllb_h28_bqje = xjllb_h28_bqje;
	}

	public DZFDouble getXjllb_h28_sqje() {
		return xjllb_h28_sqje;
	}

	public void setXjllb_h28_sqje(DZFDouble xjllb_h28_sqje) {
		this.xjllb_h28_sqje = xjllb_h28_sqje;
	}

	public DZFDouble getXjllb_h29_bqje() {
		return xjllb_h29_bqje;
	}

	public void setXjllb_h29_bqje(DZFDouble xjllb_h29_bqje) {
		this.xjllb_h29_bqje = xjllb_h29_bqje;
	}

	public DZFDouble getXjllb_h29_sqje() {
		return xjllb_h29_sqje;
	}

	public void setXjllb_h29_sqje(DZFDouble xjllb_h29_sqje) {
		this.xjllb_h29_sqje = xjllb_h29_sqje;
	}

	public DZFDouble getXjllb_h30_bqje() {
		return xjllb_h30_bqje;
	}

	public void setXjllb_h30_bqje(DZFDouble xjllb_h30_bqje) {
		this.xjllb_h30_bqje = xjllb_h30_bqje;
	}

	public DZFDouble getXjllb_h30_sqje() {
		return xjllb_h30_sqje;
	}

	public void setXjllb_h30_sqje(DZFDouble xjllb_h30_sqje) {
		this.xjllb_h30_sqje = xjllb_h30_sqje;
	}

	public DZFDouble getXjllb_h31_bqje() {
		return xjllb_h31_bqje;
	}

	public void setXjllb_h31_bqje(DZFDouble xjllb_h31_bqje) {
		this.xjllb_h31_bqje = xjllb_h31_bqje;
	}

	public DZFDouble getXjllb_h31_sqje() {
		return xjllb_h31_sqje;
	}

	public void setXjllb_h31_sqje(DZFDouble xjllb_h31_sqje) {
		this.xjllb_h31_sqje = xjllb_h31_sqje;
	}

	public DZFDouble getXjllb_h32_bqje() {
		return xjllb_h32_bqje;
	}

	public void setXjllb_h32_bqje(DZFDouble xjllb_h32_bqje) {
		this.xjllb_h32_bqje = xjllb_h32_bqje;
	}

	public DZFDouble getXjllb_h32_sqje() {
		return xjllb_h32_sqje;
	}

	public void setXjllb_h32_sqje(DZFDouble xjllb_h32_sqje) {
		this.xjllb_h32_sqje = xjllb_h32_sqje;
	}

	public DZFDouble getXjllb_h33_bqje() {
		return xjllb_h33_bqje;
	}

	public void setXjllb_h33_bqje(DZFDouble xjllb_h33_bqje) {
		this.xjllb_h33_bqje = xjllb_h33_bqje;
	}

	public DZFDouble getXjllb_h33_sqje() {
		return xjllb_h33_sqje;
	}

	public void setXjllb_h33_sqje(DZFDouble xjllb_h33_sqje) {
		this.xjllb_h33_sqje = xjllb_h33_sqje;
	}

	public DZFDouble getXjllb_h34_bqje() {
		return xjllb_h34_bqje;
	}

	public void setXjllb_h34_bqje(DZFDouble xjllb_h34_bqje) {
		this.xjllb_h34_bqje = xjllb_h34_bqje;
	}

	public DZFDouble getXjllb_h34_sqje() {
		return xjllb_h34_sqje;
	}

	public void setXjllb_h34_sqje(DZFDouble xjllb_h34_sqje) {
		this.xjllb_h34_sqje = xjllb_h34_sqje;
	}

	public DZFDouble getXjllb_h35_bqje() {
		return xjllb_h35_bqje;
	}

	public void setXjllb_h35_bqje(DZFDouble xjllb_h35_bqje) {
		this.xjllb_h35_bqje = xjllb_h35_bqje;
	}

	public DZFDouble getXjllb_h35_sqje() {
		return xjllb_h35_sqje;
	}

	public void setXjllb_h35_sqje(DZFDouble xjllb_h35_sqje) {
		this.xjllb_h35_sqje = xjllb_h35_sqje;
	}

	public DZFDouble getXjllb_h36_bqje() {
		return xjllb_h36_bqje;
	}

	public void setXjllb_h36_bqje(DZFDouble xjllb_h36_bqje) {
		this.xjllb_h36_bqje = xjllb_h36_bqje;
	}

	public DZFDouble getXjllb_h36_sqje() {
		return xjllb_h36_sqje;
	}

	public void setXjllb_h36_sqje(DZFDouble xjllb_h36_sqje) {
		this.xjllb_h36_sqje = xjllb_h36_sqje;
	}

	public DZFDouble getXjllb_h37_bqje() {
		return xjllb_h37_bqje;
	}

	public void setXjllb_h37_bqje(DZFDouble xjllb_h37_bqje) {
		this.xjllb_h37_bqje = xjllb_h37_bqje;
	}

	public DZFDouble getXjllb_h37_sqje() {
		return xjllb_h37_sqje;
	}

	public void setXjllb_h37_sqje(DZFDouble xjllb_h37_sqje) {
		this.xjllb_h37_sqje = xjllb_h37_sqje;
	}

	public DZFDouble getXjllb_h38_bqje() {
		return xjllb_h38_bqje;
	}

	public void setXjllb_h38_bqje(DZFDouble xjllb_h38_bqje) {
		this.xjllb_h38_bqje = xjllb_h38_bqje;
	}

	public DZFDouble getXjllb_h38_sqje() {
		return xjllb_h38_sqje;
	}

	public void setXjllb_h38_sqje(DZFDouble xjllb_h38_sqje) {
		this.xjllb_h38_sqje = xjllb_h38_sqje;
	}

}
