package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 部分产品销售统计表
@TaxExcelPos(reportID = "10101020", reportname = " 部分产品销售统计表")
public class ProductSalesStatistics {
	// 轮胎销售数量
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble ltxssl;
	// 轮胎销售额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble ltxse;
	// 子午线轮胎销售数量
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble zwxltxssl;
	// 子午线轮胎销售额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble zwxltxse;
	// 斜交轮胎销售数量
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble xjltxssl;
	// 斜交轮胎销售额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble xjltxse;
	// 酒精销售数量
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble jjxssl;
	// 酒精销售额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble jjxse;
	// 用于乙醇汽油的酒精销售数量
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble yyycqydjjxssl;
	// 用于乙醇汽油的酒精销售额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble yyycqydjjxse;
	// 食用酒精销售数量
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble syjjxssl;
	// 食用酒精销售额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble syjjxse;
	// 其他酒精销售数量
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble qtjjxssl;
	// 其他酒精销售额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble qtjjxse;
	// 摩托车销售数量
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble mtcxssl;
	// 摩托车销售额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble mtcxse;

	public DZFDouble getLtxssl() {
		return ltxssl;
	}

	public void setLtxssl(DZFDouble ltxssl) {
		this.ltxssl = ltxssl;
	}

	public DZFDouble getLtxse() {
		return ltxse;
	}

	public void setLtxse(DZFDouble ltxse) {
		this.ltxse = ltxse;
	}

	public DZFDouble getZwxltxssl() {
		return zwxltxssl;
	}

	public void setZwxltxssl(DZFDouble zwxltxssl) {
		this.zwxltxssl = zwxltxssl;
	}

	public DZFDouble getZwxltxse() {
		return zwxltxse;
	}

	public void setZwxltxse(DZFDouble zwxltxse) {
		this.zwxltxse = zwxltxse;
	}

	public DZFDouble getXjltxssl() {
		return xjltxssl;
	}

	public void setXjltxssl(DZFDouble xjltxssl) {
		this.xjltxssl = xjltxssl;
	}

	public DZFDouble getXjltxse() {
		return xjltxse;
	}

	public void setXjltxse(DZFDouble xjltxse) {
		this.xjltxse = xjltxse;
	}

	public DZFDouble getJjxssl() {
		return jjxssl;
	}

	public void setJjxssl(DZFDouble jjxssl) {
		this.jjxssl = jjxssl;
	}

	public DZFDouble getJjxse() {
		return jjxse;
	}

	public void setJjxse(DZFDouble jjxse) {
		this.jjxse = jjxse;
	}

	public DZFDouble getYyycqydjjxssl() {
		return yyycqydjjxssl;
	}

	public void setYyycqydjjxssl(DZFDouble yyycqydjjxssl) {
		this.yyycqydjjxssl = yyycqydjjxssl;
	}

	public DZFDouble getYyycqydjjxse() {
		return yyycqydjjxse;
	}

	public void setYyycqydjjxse(DZFDouble yyycqydjjxse) {
		this.yyycqydjjxse = yyycqydjjxse;
	}

	public DZFDouble getSyjjxssl() {
		return syjjxssl;
	}

	public void setSyjjxssl(DZFDouble syjjxssl) {
		this.syjjxssl = syjjxssl;
	}

	public DZFDouble getSyjjxse() {
		return syjjxse;
	}

	public void setSyjjxse(DZFDouble syjjxse) {
		this.syjjxse = syjjxse;
	}

	public DZFDouble getQtjjxssl() {
		return qtjjxssl;
	}

	public void setQtjjxssl(DZFDouble qtjjxssl) {
		this.qtjjxssl = qtjjxssl;
	}

	public DZFDouble getQtjjxse() {
		return qtjjxse;
	}

	public void setQtjjxse(DZFDouble qtjjxse) {
		this.qtjjxse = qtjjxse;
	}

	public DZFDouble getMtcxssl() {
		return mtcxssl;
	}

	public void setMtcxssl(DZFDouble mtcxssl) {
		this.mtcxssl = mtcxssl;
	}

	public DZFDouble getMtcxse() {
		return mtcxse;
	}

	public void setMtcxse(DZFDouble mtcxse) {
		this.mtcxse = mtcxse;
	}
}
