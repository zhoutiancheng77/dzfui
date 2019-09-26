package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 账表清单取数对比
 * 
 * @author liubj
 *
 */
public class AddValueTaxDiffVO {
	// 期间
	private String period;
	// 税名
	private String tax_name;
	// 类型
	private Integer tax_type;
	// 序号
	private Integer snumber;
	// 税目
	private String pk_taxitem;
	// 税率
	private DZFDouble rate;
	// 金额
	private DZFDouble mny_voucher;
	// 税额
	private DZFDouble taxmny_voucher;
	// 金额
	private DZFDouble mny_invoice;
	// 税额
	private DZFDouble taxmny_invoice;
	// 发票类型
	private Integer fp_style;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getTax_name() {
		return tax_name;
	}

	public void setTax_name(String tax_name) {
		this.tax_name = tax_name;
	}

	public Integer getTax_type() {
		return tax_type;
	}

	public void setTax_type(Integer tax_type) {
		this.tax_type = tax_type;
	}

	public Integer getSnumber() {
		return snumber;
	}

	public void setSnumber(Integer snumber) {
		this.snumber = snumber;
	}

	public String getPk_taxitem() {
		return pk_taxitem;
	}

	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
	}

	public DZFDouble getRate() {
		return rate;
	}

	public void setRate(DZFDouble rate) {
		this.rate = rate;
	}

	public DZFDouble getMny_voucher() {
		return mny_voucher;
	}

	public void setMny_voucher(DZFDouble mny_voucher) {
		this.mny_voucher = mny_voucher;
	}

	public DZFDouble getTaxmny_voucher() {
		return taxmny_voucher;
	}

	public void setTaxmny_voucher(DZFDouble taxmny_voucher) {
		this.taxmny_voucher = taxmny_voucher;
	}

	public DZFDouble getMny_invoice() {
		return mny_invoice;
	}

	public void setMny_invoice(DZFDouble mny_invoice) {
		this.mny_invoice = mny_invoice;
	}

	public DZFDouble getTaxmny_invoice() {
		return taxmny_invoice;
	}

	public void setTaxmny_invoice(DZFDouble taxmny_invoice) {
		this.taxmny_invoice = taxmny_invoice;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}

}
