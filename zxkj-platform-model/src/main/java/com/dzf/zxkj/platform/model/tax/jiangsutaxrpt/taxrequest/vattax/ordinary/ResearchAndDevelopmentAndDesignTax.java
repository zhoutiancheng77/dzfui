package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//研发、设计服务征免税明细数据 sb10101019vo_01
@TaxExcelPos(reportID = "10101019", reportname = "研发、设计服务征免税明细数据", rowBegin = 6, rowEnd = 25, col = 1)
public class ResearchAndDevelopmentAndDesignTax {
	// 申报序号
	@TaxExcelPos(col = 0)
	private String sbxh;

	// 计量单位
	@TaxExcelPos(col = 1)
	private String jldw;

	// 合同总金额折美元
	@TaxExcelPos(col = 2)
	private DZFDouble htzjezmy;

	// 合同总金额折人民币
	@TaxExcelPos(col = 3)
	private DZFDouble htzjezrmb;

	// 本期收款凭证份数
	@TaxExcelPos(col = 4)
	private DZFDouble bqskpzfs;

	// 本期确认应税服务营业收入人民币乘征退税率之差
	@TaxExcelPos(col = 5)
	private DZFDouble bqqrysfwyysrrmbcztslzc;

	// 本期确认应税服务营业收入人民币乘退税率
	@TaxExcelPos(col = 6)
	private DZFDouble bqqrysfwyysrrmbctsl;

	// 应税服务免抵退税计税金额乘退税率
	@TaxExcelPos(col = 7)
	private DZFDouble ysfwmdtsjsjectsl;

	// 本期收款金额
	@TaxExcelPos(col = 8)
	private DZFDouble bqskje;

	// 应税服务营业额折人民币
	@TaxExcelPos(col = 9)
	private DZFDouble ysfwyyezrmb;

	// 应税服务营业额支付给非试点纳税人价款（人民币）
	@TaxExcelPos(col = 10)
	private DZFDouble ysfwyyezfgfsdnsrjk;

	// 应税服务营业额免抵退税计税金额
	@TaxExcelPos(col = 11)
	private DZFDouble ysfwyyemdtsjsje;

	// 征税率
	@TaxExcelPos(col = 12)
	private DZFDouble zse;

	// 退税率
	@TaxExcelPos(col = 13)
	private DZFDouble tse;

	// 应税服务免抵退税计税金额乘征退税率之差
	@TaxExcelPos(col = 14)
	private DZFDouble ysfwmdtsjsjecztslzc;

	// 应税服务销售额乘退税率
	@TaxExcelPos(col = 15)
	private DZFDouble ysfwxsectsl;

	public String getSbxh() {
		return sbxh;
	}

	public void setSbxh(String sbxh) {
		this.sbxh = sbxh;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public DZFDouble getHtzjezmy() {
		return htzjezmy;
	}

	public void setHtzjezmy(DZFDouble htzjezmy) {
		this.htzjezmy = htzjezmy;
	}

	public DZFDouble getHtzjezrmb() {
		return htzjezrmb;
	}

	public void setHtzjezrmb(DZFDouble htzjezrmb) {
		this.htzjezrmb = htzjezrmb;
	}

	public DZFDouble getBqskpzfs() {
		return bqskpzfs;
	}

	public void setBqskpzfs(DZFDouble bqskpzfs) {
		this.bqskpzfs = bqskpzfs;
	}

	public DZFDouble getBqqrysfwyysrrmbcztslzc() {
		return bqqrysfwyysrrmbcztslzc;
	}

	public void setBqqrysfwyysrrmbcztslzc(DZFDouble bqqrysfwyysrrmbcztslzc) {
		this.bqqrysfwyysrrmbcztslzc = bqqrysfwyysrrmbcztslzc;
	}

	public DZFDouble getBqqrysfwyysrrmbctsl() {
		return bqqrysfwyysrrmbctsl;
	}

	public void setBqqrysfwyysrrmbctsl(DZFDouble bqqrysfwyysrrmbctsl) {
		this.bqqrysfwyysrrmbctsl = bqqrysfwyysrrmbctsl;
	}

	public DZFDouble getYsfwmdtsjsjectsl() {
		return ysfwmdtsjsjectsl;
	}

	public void setYsfwmdtsjsjectsl(DZFDouble ysfwmdtsjsjectsl) {
		this.ysfwmdtsjsjectsl = ysfwmdtsjsjectsl;
	}

	public DZFDouble getBqskje() {
		return bqskje;
	}

	public void setBqskje(DZFDouble bqskje) {
		this.bqskje = bqskje;
	}

	public DZFDouble getYsfwyyezrmb() {
		return ysfwyyezrmb;
	}

	public void setYsfwyyezrmb(DZFDouble ysfwyyezrmb) {
		this.ysfwyyezrmb = ysfwyyezrmb;
	}

	public DZFDouble getYsfwyyezfgfsdnsrjk() {
		return ysfwyyezfgfsdnsrjk;
	}

	public void setYsfwyyezfgfsdnsrjk(DZFDouble ysfwyyezfgfsdnsrjk) {
		this.ysfwyyezfgfsdnsrjk = ysfwyyezfgfsdnsrjk;
	}

	public DZFDouble getYsfwyyemdtsjsje() {
		return ysfwyyemdtsjsje;
	}

	public void setYsfwyyemdtsjsje(DZFDouble ysfwyyemdtsjsje) {
		this.ysfwyyemdtsjsje = ysfwyyemdtsjsje;
	}

	public DZFDouble getZse() {
		return zse;
	}

	public void setZse(DZFDouble zse) {
		this.zse = zse;
	}

	public DZFDouble getTse() {
		return tse;
	}

	public void setTse(DZFDouble tse) {
		this.tse = tse;
	}

	public DZFDouble getYsfwmdtsjsjecztslzc() {
		return ysfwmdtsjsjecztslzc;
	}

	public void setYsfwmdtsjsjecztslzc(DZFDouble ysfwmdtsjsjecztslzc) {
		this.ysfwmdtsjsjecztslzc = ysfwmdtsjsjecztslzc;
	}

	public DZFDouble getYsfwxsectsl() {
		return ysfwxsectsl;
	}

	public void setYsfwxsectsl(DZFDouble ysfwxsectsl) {
		this.ysfwxsectsl = ysfwxsectsl;
	}

}
