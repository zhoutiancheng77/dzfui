package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.enums.IFpStyleEnum;

public class VATInvoiceTypeConst {

	/**
	 * 增值税普通发票
	 */
	public static final int VAT_ORDINARY_INVOICE = IFpStyleEnum.COMMINVOICE.getValue();

	/**
	 * 增值税专用发票
	 */
	public static final int VAT_SPECIA_INVOICE = IFpStyleEnum.SPECINVOICE.getValue();
	/**
	 * 未开票
	 */
	public static final int NON_INVOICE = IFpStyleEnum.NOINVOICE.getValue();

	//--------------------------------------------------------------------
	//--------------------------------------------------------------------
	/**
	 * 未确定
	 */
	public static final int UNDETERMINED_INVOICE = -1;
	/**
	 * 销项发票
	 */
	public static final int VAT_SALE_INVOICE = 0;

	/**
	 * 进项发票
	 */
	public static final int VAT_INCOM_INVOICE = 1;

	/**
	 * 银行回单
	 */
	public static final int VAT_BANK_INVOICE = 2;
	/**
	 * 定额发票
	 */
	public static final int VAT_QUOTA_INVOICE = 3;

	/**
	 * 收款回单
	 */
	public static final int VAT_RECE_INVOICE = 4;

	/**
	 * 付款回单
	 */
	public static final int VAT_PAY_INVOICE = 5;

}