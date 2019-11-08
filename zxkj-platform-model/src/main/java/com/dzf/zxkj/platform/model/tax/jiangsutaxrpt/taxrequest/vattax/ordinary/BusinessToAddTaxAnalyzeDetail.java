package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 营改增税负分析测算明细表
@TaxExcelPos(reportID = "10101024", reportname = "营改增税负分析测算明细表", rowBegin = 9, rowEnd = 19, col = 0)
public class BusinessToAddTaxAnalyzeDetail {
	// 应税项目代码及名称
	@TaxExcelPos(col = 0, isCode = true)
	private String ysxmdm;
	// 应税项目代码及名称
	@TaxExcelPos(col = 0)
	private String ysxmmc;
	// 增值税税率或征收率
	@TaxExcelPos(col = 1)
	private DZFDouble zzsslhzsl;
	// 营业税税率
	@TaxExcelPos(col = 2)
	private DZFDouble yyssl;
	// 增值税不含税销售额
	@TaxExcelPos(col = 3)
	private DZFDouble zzsbhssxe;
	// 增值税销项应纳税额
	@TaxExcelPos(col = 4)
	private DZFDouble zzsxxynse;
	// 增值税价税合计
	@TaxExcelPos(col = 5)
	private DZFDouble zzsjshj;
	// 增值税本期实际扣除金额
	@TaxExcelPos(col = 6)
	private DZFDouble zzssjkcje;
	// 增值税扣除后含税销售额
	@TaxExcelPos(col = 7)
	private DZFDouble zzskchhsxse;
	// 增值税扣除后销项应纳税额
	@TaxExcelPos(col = 8)
	private DZFDouble zzskchxxynse;
	// 增值税应纳税额
	@TaxExcelPos(col = 9)
	private DZFDouble zzszzsynseys;
	// 营业税期初余额
	@TaxExcelPos(col = 10)
	private DZFDouble yysqcye;
	// 营业税本期发生额
	@TaxExcelPos(col = 11)
	private DZFDouble yysbqfse;
	// 营业税本期应扣除金额
	@TaxExcelPos(col = 12)
	private DZFDouble yysbqykce;
	// 营业税本期实际扣除金额
	@TaxExcelPos(col = 13)
	private DZFDouble yysbqsjkce;
	// 营业税期末余额
	@TaxExcelPos(col = 14)
	private DZFDouble yysqmye;
	// 营业税应税营业额
	@TaxExcelPos(col = 15)
	private DZFDouble yysysyye;
	// 营业税应纳税额
	@TaxExcelPos(col = 16)
	private DZFDouble yysyysynse;

	public String getYsxmdm() {
		return ysxmdm;
	}

	public void setYsxmdm(String ysxmdm) {
		this.ysxmdm = ysxmdm;
	}

	public String getYsxmmc() {
		return ysxmmc;
	}

	public void setYsxmmc(String ysxmmc) {
		this.ysxmmc = ysxmmc;
	}

	public DZFDouble getZzsslhzsl() {
		return zzsslhzsl;
	}

	public void setZzsslhzsl(DZFDouble zzsslhzsl) {
		this.zzsslhzsl = zzsslhzsl;
	}

	public DZFDouble getYyssl() {
		return yyssl;
	}

	public void setYyssl(DZFDouble yyssl) {
		this.yyssl = yyssl;
	}

	public DZFDouble getZzsbhssxe() {
		return zzsbhssxe;
	}

	public void setZzsbhssxe(DZFDouble zzsbhssxe) {
		this.zzsbhssxe = zzsbhssxe;
	}

	public DZFDouble getZzsxxynse() {
		return zzsxxynse;
	}

	public void setZzsxxynse(DZFDouble zzsxxynse) {
		this.zzsxxynse = zzsxxynse;
	}

	public DZFDouble getZzsjshj() {
		return zzsjshj;
	}

	public void setZzsjshj(DZFDouble zzsjshj) {
		this.zzsjshj = zzsjshj;
	}

	public DZFDouble getZzssjkcje() {
		return zzssjkcje;
	}

	public void setZzssjkcje(DZFDouble zzssjkcje) {
		this.zzssjkcje = zzssjkcje;
	}

	public DZFDouble getZzskchhsxse() {
		return zzskchhsxse;
	}

	public void setZzskchhsxse(DZFDouble zzskchhsxse) {
		this.zzskchhsxse = zzskchhsxse;
	}

	public DZFDouble getZzskchxxynse() {
		return zzskchxxynse;
	}

	public void setZzskchxxynse(DZFDouble zzskchxxynse) {
		this.zzskchxxynse = zzskchxxynse;
	}

	public DZFDouble getZzszzsynseys() {
		return zzszzsynseys;
	}

	public void setZzszzsynseys(DZFDouble zzszzsynseys) {
		this.zzszzsynseys = zzszzsynseys;
	}

	public DZFDouble getYysqcye() {
		return yysqcye;
	}

	public void setYysqcye(DZFDouble yysqcye) {
		this.yysqcye = yysqcye;
	}

	public DZFDouble getYysbqfse() {
		return yysbqfse;
	}

	public void setYysbqfse(DZFDouble yysbqfse) {
		this.yysbqfse = yysbqfse;
	}

	public DZFDouble getYysbqykce() {
		return yysbqykce;
	}

	public void setYysbqykce(DZFDouble yysbqykce) {
		this.yysbqykce = yysbqykce;
	}

	public DZFDouble getYysbqsjkce() {
		return yysbqsjkce;
	}

	public void setYysbqsjkce(DZFDouble yysbqsjkce) {
		this.yysbqsjkce = yysbqsjkce;
	}

	public DZFDouble getYysqmye() {
		return yysqmye;
	}

	public void setYysqmye(DZFDouble yysqmye) {
		this.yysqmye = yysqmye;
	}

	public DZFDouble getYysysyye() {
		return yysysyye;
	}

	public void setYysysyye(DZFDouble yysysyye) {
		this.yysysyye = yysysyye;
	}

	public DZFDouble getYysyysynse() {
		return yysyysynse;
	}

	public void setYysyysynse(DZFDouble yysyysynse) {
		this.yysyysynse = yysyysynse;
	}

}
