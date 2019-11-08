package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.enterprise;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 利润表 sb29805002vo
@TaxExcelPos(reportID = "29805002", reportname = "利润表")
public class ProfitStatement {
	// 一、主营业务收入
	// 本期数
	@TaxExcelPos(row = 4, col = 2)
	private DZFDouble lrb_h1_bys;
	// 本年累计数
	@TaxExcelPos(row = 4, col = 3)
	private DZFDouble lrb_h1_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h1_sqbnljs;

	// 减：主营业务成本
	// 本期数
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble lrb_h2_bys;
	// 本年累计数
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble lrb_h2_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h2_sqbnljs;

	// 主营业务税金及附加
	// 本期数
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble lrb_h3_bys;
	// 本年累计数
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble lrb_h3_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h3_sqbnljs;

	// 二、主营业务利润（亏损以“－”号填列）
	// 本期数
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble lrb_h4_bys;
	// 本年累计数
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble lrb_h4_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h4_sqbnljs;

	// 加：其他业务利润（亏损以“－”号填列）
	// 本期数
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble lrb_h5_bys;
	// 本年累计数
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble lrb_h5_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h5_sqbnljs;

	// 减：营业费用
	// 本期数
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble lrb_h6_bys;
	// 本年累计数
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble lrb_h6_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h6_sqbnljs;

	// 管理费用
	// 本期数
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble lrb_h7_bys;
	// 本年累计数
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble lrb_h7_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h7_sqbnljs;

	// 财务费用
	// 本期数
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble lrb_h8_bys;
	// 本年累计数
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble lrb_h8_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h8_sqbnljs;

	// 三、营业利润（亏损以“－”号填列）
	// 本期数
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble lrb_h9_bys;
	// 本年累计数
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble lrb_h9_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h9_sqbnljs;

	// 加：投资收益（亏损以“－”填列）
	// 本期数
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble lrb_h10_bys;
	// 本年累计数
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble lrb_h10_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h10_sqbnljs;

	// 补贴收入
	// 本期数
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble lrb_h11_bys;
	// 本年累计数
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble lrb_h11_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h11_sqbnljs;

	// 营业外收入
	// 本期数
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble lrb_h12_bys;
	// 本年累计数
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble lrb_h12_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h12_sqbnljs;

	// 减：营业外支出
	// 本期数
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble lrb_h13_bys;
	// 本年累计数
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble lrb_h13_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h13_sqbnljs;

	// 四、利润总额（亏损总额以“－”号填列）
	// 本期数
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble lrb_h14_bys;
	// 本年累计数
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble lrb_h14_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h14_sqbnljs;

	// 减：所得税
	// 本期数
	@TaxExcelPos(row = 18, col = 2)
	private DZFDouble lrb_h15_bys;
	// 本年累计数
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble lrb_h15_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h15_sqbnljs;

	// 五、净利润（净亏损以“－”号填列）
	// 本期数
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble lrb_h16_bys;
	// 本年累计数
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble lrb_h16_bnljs;
	// 上期本年累计数
	private DZFDouble lrb_h16_sqbnljs;

	// 1．出售、处置部门或被投资单位所得收益
	// 本年累计数
	@TaxExcelPos(row = 22, col = 1)
	private DZFDouble lrb_bc_h1_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble lrb_bc_h1_snsjs;

	// 2．自然灾害发生的损失
	// 本年累计数
	@TaxExcelPos(row = 23, col = 1)
	private DZFDouble lrb_bc_h2_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 23, col = 2)
	private DZFDouble lrb_bc_h2_snsjs;

	// 3．会计政策变更增加(或减少)利润总额
	// 本年累计数
	@TaxExcelPos(row = 24, col = 1)
	private DZFDouble lrb_bc_h3_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble lrb_bc_h3_snsjs;

	// 4．会计估计变更增加(或减少)利润总额
	// 本年累计数
	@TaxExcelPos(row = 25, col = 1)
	private DZFDouble lrb_bc_h4_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble lrb_bc_h4_snsjs;

	// 5．债务重组损失
	// 本年累计数
	@TaxExcelPos(row = 26, col = 1)
	private DZFDouble lrb_bc_h5_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble lrb_bc_h5_snsjs;

	// 6．其他
	// 本年累计数
	@TaxExcelPos(row = 27, col = 1)
	private DZFDouble lrb_bc_h6_bnlj;
	// 上年实际数
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble lrb_bc_h6_snsjs;

	public DZFDouble getLrb_h1_bys() {
		return lrb_h1_bys;
	}

	public void setLrb_h1_bys(DZFDouble lrb_h1_bys) {
		this.lrb_h1_bys = lrb_h1_bys;
	}

	public DZFDouble getLrb_h1_bnljs() {
		return lrb_h1_bnljs;
	}

	public void setLrb_h1_bnljs(DZFDouble lrb_h1_bnljs) {
		this.lrb_h1_bnljs = lrb_h1_bnljs;
	}

	public DZFDouble getLrb_h1_sqbnljs() {
		return lrb_h1_sqbnljs;
	}

	public void setLrb_h1_sqbnljs(DZFDouble lrb_h1_sqbnljs) {
		this.lrb_h1_sqbnljs = lrb_h1_sqbnljs;
	}

	public DZFDouble getLrb_h2_bys() {
		return lrb_h2_bys;
	}

	public void setLrb_h2_bys(DZFDouble lrb_h2_bys) {
		this.lrb_h2_bys = lrb_h2_bys;
	}

	public DZFDouble getLrb_h2_bnljs() {
		return lrb_h2_bnljs;
	}

	public void setLrb_h2_bnljs(DZFDouble lrb_h2_bnljs) {
		this.lrb_h2_bnljs = lrb_h2_bnljs;
	}

	public DZFDouble getLrb_h2_sqbnljs() {
		return lrb_h2_sqbnljs;
	}

	public void setLrb_h2_sqbnljs(DZFDouble lrb_h2_sqbnljs) {
		this.lrb_h2_sqbnljs = lrb_h2_sqbnljs;
	}

	public DZFDouble getLrb_h3_bys() {
		return lrb_h3_bys;
	}

	public void setLrb_h3_bys(DZFDouble lrb_h3_bys) {
		this.lrb_h3_bys = lrb_h3_bys;
	}

	public DZFDouble getLrb_h3_bnljs() {
		return lrb_h3_bnljs;
	}

	public void setLrb_h3_bnljs(DZFDouble lrb_h3_bnljs) {
		this.lrb_h3_bnljs = lrb_h3_bnljs;
	}

	public DZFDouble getLrb_h3_sqbnljs() {
		return lrb_h3_sqbnljs;
	}

	public void setLrb_h3_sqbnljs(DZFDouble lrb_h3_sqbnljs) {
		this.lrb_h3_sqbnljs = lrb_h3_sqbnljs;
	}

	public DZFDouble getLrb_h4_bys() {
		return lrb_h4_bys;
	}

	public void setLrb_h4_bys(DZFDouble lrb_h4_bys) {
		this.lrb_h4_bys = lrb_h4_bys;
	}

	public DZFDouble getLrb_h4_bnljs() {
		return lrb_h4_bnljs;
	}

	public void setLrb_h4_bnljs(DZFDouble lrb_h4_bnljs) {
		this.lrb_h4_bnljs = lrb_h4_bnljs;
	}

	public DZFDouble getLrb_h4_sqbnljs() {
		return lrb_h4_sqbnljs;
	}

	public void setLrb_h4_sqbnljs(DZFDouble lrb_h4_sqbnljs) {
		this.lrb_h4_sqbnljs = lrb_h4_sqbnljs;
	}

	public DZFDouble getLrb_h5_bys() {
		return lrb_h5_bys;
	}

	public void setLrb_h5_bys(DZFDouble lrb_h5_bys) {
		this.lrb_h5_bys = lrb_h5_bys;
	}

	public DZFDouble getLrb_h5_bnljs() {
		return lrb_h5_bnljs;
	}

	public void setLrb_h5_bnljs(DZFDouble lrb_h5_bnljs) {
		this.lrb_h5_bnljs = lrb_h5_bnljs;
	}

	public DZFDouble getLrb_h5_sqbnljs() {
		return lrb_h5_sqbnljs;
	}

	public void setLrb_h5_sqbnljs(DZFDouble lrb_h5_sqbnljs) {
		this.lrb_h5_sqbnljs = lrb_h5_sqbnljs;
	}

	public DZFDouble getLrb_h6_bys() {
		return lrb_h6_bys;
	}

	public void setLrb_h6_bys(DZFDouble lrb_h6_bys) {
		this.lrb_h6_bys = lrb_h6_bys;
	}

	public DZFDouble getLrb_h6_bnljs() {
		return lrb_h6_bnljs;
	}

	public void setLrb_h6_bnljs(DZFDouble lrb_h6_bnljs) {
		this.lrb_h6_bnljs = lrb_h6_bnljs;
	}

	public DZFDouble getLrb_h6_sqbnljs() {
		return lrb_h6_sqbnljs;
	}

	public void setLrb_h6_sqbnljs(DZFDouble lrb_h6_sqbnljs) {
		this.lrb_h6_sqbnljs = lrb_h6_sqbnljs;
	}

	public DZFDouble getLrb_h7_bys() {
		return lrb_h7_bys;
	}

	public void setLrb_h7_bys(DZFDouble lrb_h7_bys) {
		this.lrb_h7_bys = lrb_h7_bys;
	}

	public DZFDouble getLrb_h7_bnljs() {
		return lrb_h7_bnljs;
	}

	public void setLrb_h7_bnljs(DZFDouble lrb_h7_bnljs) {
		this.lrb_h7_bnljs = lrb_h7_bnljs;
	}

	public DZFDouble getLrb_h7_sqbnljs() {
		return lrb_h7_sqbnljs;
	}

	public void setLrb_h7_sqbnljs(DZFDouble lrb_h7_sqbnljs) {
		this.lrb_h7_sqbnljs = lrb_h7_sqbnljs;
	}

	public DZFDouble getLrb_h8_bys() {
		return lrb_h8_bys;
	}

	public void setLrb_h8_bys(DZFDouble lrb_h8_bys) {
		this.lrb_h8_bys = lrb_h8_bys;
	}

	public DZFDouble getLrb_h8_bnljs() {
		return lrb_h8_bnljs;
	}

	public void setLrb_h8_bnljs(DZFDouble lrb_h8_bnljs) {
		this.lrb_h8_bnljs = lrb_h8_bnljs;
	}

	public DZFDouble getLrb_h8_sqbnljs() {
		return lrb_h8_sqbnljs;
	}

	public void setLrb_h8_sqbnljs(DZFDouble lrb_h8_sqbnljs) {
		this.lrb_h8_sqbnljs = lrb_h8_sqbnljs;
	}

	public DZFDouble getLrb_h9_bys() {
		return lrb_h9_bys;
	}

	public void setLrb_h9_bys(DZFDouble lrb_h9_bys) {
		this.lrb_h9_bys = lrb_h9_bys;
	}

	public DZFDouble getLrb_h9_bnljs() {
		return lrb_h9_bnljs;
	}

	public void setLrb_h9_bnljs(DZFDouble lrb_h9_bnljs) {
		this.lrb_h9_bnljs = lrb_h9_bnljs;
	}

	public DZFDouble getLrb_h9_sqbnljs() {
		return lrb_h9_sqbnljs;
	}

	public void setLrb_h9_sqbnljs(DZFDouble lrb_h9_sqbnljs) {
		this.lrb_h9_sqbnljs = lrb_h9_sqbnljs;
	}

	public DZFDouble getLrb_h10_bys() {
		return lrb_h10_bys;
	}

	public void setLrb_h10_bys(DZFDouble lrb_h10_bys) {
		this.lrb_h10_bys = lrb_h10_bys;
	}

	public DZFDouble getLrb_h10_bnljs() {
		return lrb_h10_bnljs;
	}

	public void setLrb_h10_bnljs(DZFDouble lrb_h10_bnljs) {
		this.lrb_h10_bnljs = lrb_h10_bnljs;
	}

	public DZFDouble getLrb_h10_sqbnljs() {
		return lrb_h10_sqbnljs;
	}

	public void setLrb_h10_sqbnljs(DZFDouble lrb_h10_sqbnljs) {
		this.lrb_h10_sqbnljs = lrb_h10_sqbnljs;
	}

	public DZFDouble getLrb_h11_bys() {
		return lrb_h11_bys;
	}

	public void setLrb_h11_bys(DZFDouble lrb_h11_bys) {
		this.lrb_h11_bys = lrb_h11_bys;
	}

	public DZFDouble getLrb_h11_bnljs() {
		return lrb_h11_bnljs;
	}

	public void setLrb_h11_bnljs(DZFDouble lrb_h11_bnljs) {
		this.lrb_h11_bnljs = lrb_h11_bnljs;
	}

	public DZFDouble getLrb_h11_sqbnljs() {
		return lrb_h11_sqbnljs;
	}

	public void setLrb_h11_sqbnljs(DZFDouble lrb_h11_sqbnljs) {
		this.lrb_h11_sqbnljs = lrb_h11_sqbnljs;
	}

	public DZFDouble getLrb_h12_bys() {
		return lrb_h12_bys;
	}

	public void setLrb_h12_bys(DZFDouble lrb_h12_bys) {
		this.lrb_h12_bys = lrb_h12_bys;
	}

	public DZFDouble getLrb_h12_bnljs() {
		return lrb_h12_bnljs;
	}

	public void setLrb_h12_bnljs(DZFDouble lrb_h12_bnljs) {
		this.lrb_h12_bnljs = lrb_h12_bnljs;
	}

	public DZFDouble getLrb_h12_sqbnljs() {
		return lrb_h12_sqbnljs;
	}

	public void setLrb_h12_sqbnljs(DZFDouble lrb_h12_sqbnljs) {
		this.lrb_h12_sqbnljs = lrb_h12_sqbnljs;
	}

	public DZFDouble getLrb_h13_bys() {
		return lrb_h13_bys;
	}

	public void setLrb_h13_bys(DZFDouble lrb_h13_bys) {
		this.lrb_h13_bys = lrb_h13_bys;
	}

	public DZFDouble getLrb_h13_bnljs() {
		return lrb_h13_bnljs;
	}

	public void setLrb_h13_bnljs(DZFDouble lrb_h13_bnljs) {
		this.lrb_h13_bnljs = lrb_h13_bnljs;
	}

	public DZFDouble getLrb_h13_sqbnljs() {
		return lrb_h13_sqbnljs;
	}

	public void setLrb_h13_sqbnljs(DZFDouble lrb_h13_sqbnljs) {
		this.lrb_h13_sqbnljs = lrb_h13_sqbnljs;
	}

	public DZFDouble getLrb_h14_bys() {
		return lrb_h14_bys;
	}

	public void setLrb_h14_bys(DZFDouble lrb_h14_bys) {
		this.lrb_h14_bys = lrb_h14_bys;
	}

	public DZFDouble getLrb_h14_bnljs() {
		return lrb_h14_bnljs;
	}

	public void setLrb_h14_bnljs(DZFDouble lrb_h14_bnljs) {
		this.lrb_h14_bnljs = lrb_h14_bnljs;
	}

	public DZFDouble getLrb_h14_sqbnljs() {
		return lrb_h14_sqbnljs;
	}

	public void setLrb_h14_sqbnljs(DZFDouble lrb_h14_sqbnljs) {
		this.lrb_h14_sqbnljs = lrb_h14_sqbnljs;
	}

	public DZFDouble getLrb_h15_bys() {
		return lrb_h15_bys;
	}

	public void setLrb_h15_bys(DZFDouble lrb_h15_bys) {
		this.lrb_h15_bys = lrb_h15_bys;
	}

	public DZFDouble getLrb_h15_bnljs() {
		return lrb_h15_bnljs;
	}

	public void setLrb_h15_bnljs(DZFDouble lrb_h15_bnljs) {
		this.lrb_h15_bnljs = lrb_h15_bnljs;
	}

	public DZFDouble getLrb_h15_sqbnljs() {
		return lrb_h15_sqbnljs;
	}

	public void setLrb_h15_sqbnljs(DZFDouble lrb_h15_sqbnljs) {
		this.lrb_h15_sqbnljs = lrb_h15_sqbnljs;
	}

	public DZFDouble getLrb_h16_bys() {
		return lrb_h16_bys;
	}

	public void setLrb_h16_bys(DZFDouble lrb_h16_bys) {
		this.lrb_h16_bys = lrb_h16_bys;
	}

	public DZFDouble getLrb_h16_bnljs() {
		return lrb_h16_bnljs;
	}

	public void setLrb_h16_bnljs(DZFDouble lrb_h16_bnljs) {
		this.lrb_h16_bnljs = lrb_h16_bnljs;
	}

	public DZFDouble getLrb_h16_sqbnljs() {
		return lrb_h16_sqbnljs;
	}

	public void setLrb_h16_sqbnljs(DZFDouble lrb_h16_sqbnljs) {
		this.lrb_h16_sqbnljs = lrb_h16_sqbnljs;
	}

	public DZFDouble getLrb_bc_h1_bnlj() {
		return lrb_bc_h1_bnlj;
	}

	public void setLrb_bc_h1_bnlj(DZFDouble lrb_bc_h1_bnlj) {
		this.lrb_bc_h1_bnlj = lrb_bc_h1_bnlj;
	}

	public DZFDouble getLrb_bc_h1_snsjs() {
		return lrb_bc_h1_snsjs;
	}

	public void setLrb_bc_h1_snsjs(DZFDouble lrb_bc_h1_snsjs) {
		this.lrb_bc_h1_snsjs = lrb_bc_h1_snsjs;
	}

	public DZFDouble getLrb_bc_h2_bnlj() {
		return lrb_bc_h2_bnlj;
	}

	public void setLrb_bc_h2_bnlj(DZFDouble lrb_bc_h2_bnlj) {
		this.lrb_bc_h2_bnlj = lrb_bc_h2_bnlj;
	}

	public DZFDouble getLrb_bc_h2_snsjs() {
		return lrb_bc_h2_snsjs;
	}

	public void setLrb_bc_h2_snsjs(DZFDouble lrb_bc_h2_snsjs) {
		this.lrb_bc_h2_snsjs = lrb_bc_h2_snsjs;
	}

	public DZFDouble getLrb_bc_h3_bnlj() {
		return lrb_bc_h3_bnlj;
	}

	public void setLrb_bc_h3_bnlj(DZFDouble lrb_bc_h3_bnlj) {
		this.lrb_bc_h3_bnlj = lrb_bc_h3_bnlj;
	}

	public DZFDouble getLrb_bc_h3_snsjs() {
		return lrb_bc_h3_snsjs;
	}

	public void setLrb_bc_h3_snsjs(DZFDouble lrb_bc_h3_snsjs) {
		this.lrb_bc_h3_snsjs = lrb_bc_h3_snsjs;
	}

	public DZFDouble getLrb_bc_h4_bnlj() {
		return lrb_bc_h4_bnlj;
	}

	public void setLrb_bc_h4_bnlj(DZFDouble lrb_bc_h4_bnlj) {
		this.lrb_bc_h4_bnlj = lrb_bc_h4_bnlj;
	}

	public DZFDouble getLrb_bc_h4_snsjs() {
		return lrb_bc_h4_snsjs;
	}

	public void setLrb_bc_h4_snsjs(DZFDouble lrb_bc_h4_snsjs) {
		this.lrb_bc_h4_snsjs = lrb_bc_h4_snsjs;
	}

	public DZFDouble getLrb_bc_h5_bnlj() {
		return lrb_bc_h5_bnlj;
	}

	public void setLrb_bc_h5_bnlj(DZFDouble lrb_bc_h5_bnlj) {
		this.lrb_bc_h5_bnlj = lrb_bc_h5_bnlj;
	}

	public DZFDouble getLrb_bc_h5_snsjs() {
		return lrb_bc_h5_snsjs;
	}

	public void setLrb_bc_h5_snsjs(DZFDouble lrb_bc_h5_snsjs) {
		this.lrb_bc_h5_snsjs = lrb_bc_h5_snsjs;
	}

	public DZFDouble getLrb_bc_h6_bnlj() {
		return lrb_bc_h6_bnlj;
	}

	public void setLrb_bc_h6_bnlj(DZFDouble lrb_bc_h6_bnlj) {
		this.lrb_bc_h6_bnlj = lrb_bc_h6_bnlj;
	}

	public DZFDouble getLrb_bc_h6_snsjs() {
		return lrb_bc_h6_snsjs;
	}

	public void setLrb_bc_h6_snsjs(DZFDouble lrb_bc_h6_snsjs) {
		this.lrb_bc_h6_snsjs = lrb_bc_h6_snsjs;
	}

}
