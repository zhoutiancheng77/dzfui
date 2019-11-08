package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 所有者权益变更表
@TaxExcelPos(reportID = "39806004", reportname = "所有者权益变更表", rowBegin = 6, rowEnd = 26, col = 0)
public class ChangeOfOwnersEquity {
	// 二维表行序号
	@TaxExcelPos(col = 16)
	private String ewbhxh;

	// 行名称
	@TaxExcelPos(col = 0)
	private String hmc;

	// 本年实收资本或股本
	@TaxExcelPos(col = 2)
	private DZFDouble bnsszbhgb;

	// 本年资本公积
	@TaxExcelPos(col = 3)
	private DZFDouble bnzbgj;

	// 本年减库存股
	@TaxExcelPos(col = 4)
	private DZFDouble bnjkcg;

	// 本年其他综合收益
	@TaxExcelPos(col = 5)
	private DZFDouble bnqtzhsy;

	// 本年盈余公积
	@TaxExcelPos(col = 6)
	private DZFDouble bnyygj;

	// 本年未分配利润
	@TaxExcelPos(col = 7)
	private DZFDouble bnwfply;

	// 本年所有者权益合计
	@TaxExcelPos(col = 8)
	private DZFDouble bnsyzqyhj;

	// 上年实收资本或股本
	@TaxExcelPos(col = 9)
	private DZFDouble snsszbhgb;

	// 上年资本公积
	@TaxExcelPos(col = 10)
	private DZFDouble snzbgj;

	// 上年减库存股
	@TaxExcelPos(col = 11)
	private DZFDouble snjkcg;

	// 上年其他综合收益
	@TaxExcelPos(col = 12)
	private DZFDouble snqtzhsy;

	// 上年盈余公积
	@TaxExcelPos(col = 13)
	private DZFDouble snyygj;

	// 上年未分配利润
	@TaxExcelPos(col = 14)
	private DZFDouble snwfply;

	// 上年所有者权益合计
	@TaxExcelPos(col = 15)
	private DZFDouble snsyzqyhj;

	public String getEwbhxh() {
		return ewbhxh;
	}

	public void setEwbhxh(String ewbhxh) {
		this.ewbhxh = ewbhxh;
	}

	public String getHmc() {
		return hmc;
	}

	public void setHmc(String hmc) {
		this.hmc = hmc;
	}

	public DZFDouble getBnsszbhgb() {
		return bnsszbhgb;
	}

	public void setBnsszbhgb(DZFDouble bnsszbhgb) {
		this.bnsszbhgb = bnsszbhgb;
	}

	public DZFDouble getBnzbgj() {
		return bnzbgj;
	}

	public void setBnzbgj(DZFDouble bnzbgj) {
		this.bnzbgj = bnzbgj;
	}

	public DZFDouble getBnjkcg() {
		return bnjkcg;
	}

	public void setBnjkcg(DZFDouble bnjkcg) {
		this.bnjkcg = bnjkcg;
	}

	public DZFDouble getBnqtzhsy() {
		return bnqtzhsy;
	}

	public void setBnqtzhsy(DZFDouble bnqtzhsy) {
		this.bnqtzhsy = bnqtzhsy;
	}

	public DZFDouble getBnyygj() {
		return bnyygj;
	}

	public void setBnyygj(DZFDouble bnyygj) {
		this.bnyygj = bnyygj;
	}

	public DZFDouble getBnwfply() {
		return bnwfply;
	}

	public void setBnwfply(DZFDouble bnwfply) {
		this.bnwfply = bnwfply;
	}

	public DZFDouble getBnsyzqyhj() {
		return bnsyzqyhj;
	}

	public void setBnsyzqyhj(DZFDouble bnsyzqyhj) {
		this.bnsyzqyhj = bnsyzqyhj;
	}

	public DZFDouble getSnsszbhgb() {
		return snsszbhgb;
	}

	public void setSnsszbhgb(DZFDouble snsszbhgb) {
		this.snsszbhgb = snsszbhgb;
	}

	public DZFDouble getSnzbgj() {
		return snzbgj;
	}

	public void setSnzbgj(DZFDouble snzbgj) {
		this.snzbgj = snzbgj;
	}

	public DZFDouble getSnjkcg() {
		return snjkcg;
	}

	public void setSnjkcg(DZFDouble snjkcg) {
		this.snjkcg = snjkcg;
	}

	public DZFDouble getSnqtzhsy() {
		return snqtzhsy;
	}

	public void setSnqtzhsy(DZFDouble snqtzhsy) {
		this.snqtzhsy = snqtzhsy;
	}

	public DZFDouble getSnyygj() {
		return snyygj;
	}

	public void setSnyygj(DZFDouble snyygj) {
		this.snyygj = snyygj;
	}

	public DZFDouble getSnwfply() {
		return snwfply;
	}

	public void setSnwfply(DZFDouble snwfply) {
		this.snwfply = snwfply;
	}

	public DZFDouble getSnsyzqyhj() {
		return snsyzqyhj;
	}

	public void setSnsyzqyhj(DZFDouble snsyzqyhj) {
		this.snsyzqyhj = snsyzqyhj;
	}

}
