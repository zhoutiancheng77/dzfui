package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//成品油购销存情况明细表 sb10101009vo_01
@TaxExcelPos(reportID = "10101009", reportname = "成品油购销存情况明细表", rowBegin = 8, rowEnd = 17, col = 0)
public class OilProductsPurchaseSaleDetail {
	// 油品型号
	@TaxExcelPos(col = 0, isCode = true)
	private String ypxh;

	// 油品型号名称
	@TaxExcelPos(col = 0)
	private String ypxhmc;

	// 期初库存数量自购
	@TaxExcelPos(col = 1)
	private DZFDouble zgslqckc;

	// 期初库存数量代储
	@TaxExcelPos(col = 2)
	private DZFDouble dcslqckc;

	// 期初库存金额自购
	@TaxExcelPos(col = 3)
	private DZFDouble zgjeqckc;

	// 期初库存金额代储
	@TaxExcelPos(col = 4)
	private DZFDouble dcjeqckc;

	// 本期入库数量自购
	@TaxExcelPos(col = 5)
	private DZFDouble zgslbqrk;

	// 本期入库数量代储
	@TaxExcelPos(col = 6)
	private DZFDouble dcslbqrk;

	// 本期入库金额自购
	@TaxExcelPos(col = 7)
	private DZFDouble zgjebqrk;

	// 本期入库金额代储
	@TaxExcelPos(col = 8)
	private DZFDouble dcjebqrk;

	// 本期出库应税部分数量
	@TaxExcelPos(col = 9)
	private DZFDouble slysbfbqck;

	// 本期出库应税部分金额
	@TaxExcelPos(col = 10)
	private DZFDouble jeysbfbqck;

	// 本期出库非应税部分数量自用
	@TaxExcelPos(col = 11)
	private DZFDouble zyslfysbfbqck;

	// 本期出库非应税部分数量代储
	@TaxExcelPos(col = 12)
	private DZFDouble dcslfysbfbqck;

	// 本期出库非应税部分金额自用
	@TaxExcelPos(col = 13)
	private DZFDouble zyjefysbfbqck;

	// 本期出库非应税部分金额代储
	@TaxExcelPos(col = 14)
	private DZFDouble dcjefysbfbqck;

	// 期末库存数量自购
	@TaxExcelPos(col = 15)
	private DZFDouble zgslqmkc;

	// 期末库存数量代储
	@TaxExcelPos(col = 16)
	private DZFDouble dcslqmkc;

	// 期末库存金额自购
	@TaxExcelPos(col = 17)
	private DZFDouble zgjeqmkc;

	// 期末库存金额代储
	@TaxExcelPos(col = 18)
	private DZFDouble dcjeqmkc;

	public String getYpxh() {
		return ypxh;
	}

	public void setYpxh(String ypxh) {
		this.ypxh = ypxh;
	}

	public String getYpxhmc() {
		return ypxhmc;
	}

	public void setYpxhmc(String ypxhmc) {
		this.ypxhmc = ypxhmc;
	}

	public DZFDouble getZgslqckc() {
		return zgslqckc;
	}

	public void setZgslqckc(DZFDouble zgslqckc) {
		this.zgslqckc = zgslqckc;
	}

	public DZFDouble getDcslqckc() {
		return dcslqckc;
	}

	public void setDcslqckc(DZFDouble dcslqckc) {
		this.dcslqckc = dcslqckc;
	}

	public DZFDouble getZgjeqckc() {
		return zgjeqckc;
	}

	public void setZgjeqckc(DZFDouble zgjeqckc) {
		this.zgjeqckc = zgjeqckc;
	}

	public DZFDouble getDcjeqckc() {
		return dcjeqckc;
	}

	public void setDcjeqckc(DZFDouble dcjeqckc) {
		this.dcjeqckc = dcjeqckc;
	}

	public DZFDouble getZgslbqrk() {
		return zgslbqrk;
	}

	public void setZgslbqrk(DZFDouble zgslbqrk) {
		this.zgslbqrk = zgslbqrk;
	}

	public DZFDouble getDcslbqrk() {
		return dcslbqrk;
	}

	public void setDcslbqrk(DZFDouble dcslbqrk) {
		this.dcslbqrk = dcslbqrk;
	}

	public DZFDouble getZgjebqrk() {
		return zgjebqrk;
	}

	public void setZgjebqrk(DZFDouble zgjebqrk) {
		this.zgjebqrk = zgjebqrk;
	}

	public DZFDouble getDcjebqrk() {
		return dcjebqrk;
	}

	public void setDcjebqrk(DZFDouble dcjebqrk) {
		this.dcjebqrk = dcjebqrk;
	}

	public DZFDouble getSlysbfbqck() {
		return slysbfbqck;
	}

	public void setSlysbfbqck(DZFDouble slysbfbqck) {
		this.slysbfbqck = slysbfbqck;
	}

	public DZFDouble getJeysbfbqck() {
		return jeysbfbqck;
	}

	public void setJeysbfbqck(DZFDouble jeysbfbqck) {
		this.jeysbfbqck = jeysbfbqck;
	}

	public DZFDouble getZyslfysbfbqck() {
		return zyslfysbfbqck;
	}

	public void setZyslfysbfbqck(DZFDouble zyslfysbfbqck) {
		this.zyslfysbfbqck = zyslfysbfbqck;
	}

	public DZFDouble getDcslfysbfbqck() {
		return dcslfysbfbqck;
	}

	public void setDcslfysbfbqck(DZFDouble dcslfysbfbqck) {
		this.dcslfysbfbqck = dcslfysbfbqck;
	}

	public DZFDouble getZyjefysbfbqck() {
		return zyjefysbfbqck;
	}

	public void setZyjefysbfbqck(DZFDouble zyjefysbfbqck) {
		this.zyjefysbfbqck = zyjefysbfbqck;
	}

	public DZFDouble getDcjefysbfbqck() {
		return dcjefysbfbqck;
	}

	public void setDcjefysbfbqck(DZFDouble dcjefysbfbqck) {
		this.dcjefysbfbqck = dcjefysbfbqck;
	}

	public DZFDouble getZgslqmkc() {
		return zgslqmkc;
	}

	public void setZgslqmkc(DZFDouble zgslqmkc) {
		this.zgslqmkc = zgslqmkc;
	}

	public DZFDouble getDcslqmkc() {
		return dcslqmkc;
	}

	public void setDcslqmkc(DZFDouble dcslqmkc) {
		this.dcslqmkc = dcslqmkc;
	}

	public DZFDouble getZgjeqmkc() {
		return zgjeqmkc;
	}

	public void setZgjeqmkc(DZFDouble zgjeqmkc) {
		this.zgjeqmkc = zgjeqmkc;
	}

	public DZFDouble getDcjeqmkc() {
		return dcjeqmkc;
	}

	public void setDcjeqmkc(DZFDouble dcjeqmkc) {
		this.dcjeqmkc = dcjeqmkc;
	}

}
