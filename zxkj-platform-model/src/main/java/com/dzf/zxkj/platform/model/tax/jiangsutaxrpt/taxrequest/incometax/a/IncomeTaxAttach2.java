package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//A201020固定资产加速折旧(扣除)优惠明细表 sb10412003VO
@TaxExcelPos(reportID = "10412003", reportname = "A201020固定资产加速折旧(扣除)优惠明细表")
public class IncomeTaxAttach2 {
	// 一、固定资产加速折旧（不含一次性扣除，2+3
	// 资产原值
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble zcyzgdzcjszj;
	// 本年累计折旧（扣除）金额 账载折旧金额
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble zzzjjegdzcjszj;
	// 本年累计折旧（扣除）金额 按照税收一般规定计算的折旧金额
	@TaxExcelPos(row = 6, col = 4)
	private DZFDouble ybgdgdzcjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠计算的折旧金额
	@TaxExcelPos(row = 6, col = 5)
	private DZFDouble jszjjsgdzcjszj;
	// 本年累计折旧（扣除）金额 纳税调减金额
	@TaxExcelPos(row = 6, col = 6)
	private DZFDouble nstjgdzcjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠金额
	@TaxExcelPos(row = 6, col = 7)
	private DZFDouble jszjgdzcjszj;

	// （一）重要行业固定资产加速折旧
	// 资产原值
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble zcyzzyhygdzcjszj;
	// 本年累计折旧（扣除）金额 账载折旧金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble zzzjjezyhygdzcjszj;
	// 本年累计折旧（扣除）金额 按照税收一般规定计算的折旧金额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble ybgdzyhygdzcjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠计算的折旧金额
	@TaxExcelPos(row = 7, col = 5)
	private DZFDouble jszjjszyhygdzcjszj;
	// 本年累计折旧（扣除）金额 纳税调减金额
	@TaxExcelPos(row = 7, col = 6)
	private DZFDouble nstjzyhygdzcjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠金额
	@TaxExcelPos(row = 7, col = 7)
	private DZFDouble jszjzyhygdzcjszj;

	// （二）其他行业研发设备加速折旧
	// 资产原值
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble zcyzqthyyfsbjszj;
	// 本年累计折旧（扣除）金额 账载折旧金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble zzzjjeqthyyfsbjszj;
	// 本年累计折旧（扣除）金额 按照税收一般规定计算的折旧金额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble ybgdqthyyfsbjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠计算的折旧金额
	@TaxExcelPos(row = 8, col = 5)
	private DZFDouble jszjjsqthyyfsbjszj;
	// 本年累计折旧（扣除）金额 纳税调减金额
	@TaxExcelPos(row = 8, col = 6)
	private DZFDouble nstjqthyyfsbjszj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠金额
	@TaxExcelPos(row = 8, col = 7)
	private DZFDouble jszjqthyyfsbjszj;

	// 二、固定资产一次性扣除
	// 资产原值
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble zcyzgdzcycxkc;
	// 本年累计折旧（扣除）金额 账载折旧金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble zzzjjegdzcycxkc;
	// 本年累计折旧（扣除）金额 按照税收一般规定计算的折旧金额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble ybgdgdzcycxkc;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠计算的折旧金额
	@TaxExcelPos(row = 9, col = 5)
	private DZFDouble jszjjsgdzcycxkc;
	// 本年累计折旧（扣除）金额 纳税调减金额
	@TaxExcelPos(row = 9, col = 6)
	private DZFDouble nstjgdzcycxkc;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠金额
	@TaxExcelPos(row = 9, col = 7)
	private DZFDouble jszjgdzcycxkc;

	// 合计（1+4）
	// 资产原值
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble zcyzhj;
	// 本年累计折旧（扣除）金额 账载折旧金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble zzzjjehj;
	// 本年累计折旧（扣除）金额 按照税收一般规定计算的折旧金额
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble ybgdhj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠计算的折旧金额
	@TaxExcelPos(row = 10, col = 5)
	private DZFDouble jszjjshj;
	// 本年累计折旧（扣除）金额 纳税调减金额
	@TaxExcelPos(row = 10, col = 6)
	private DZFDouble nstjhj;
	// 本年累计折旧（扣除）金额 享受加速折旧优惠金额
	@TaxExcelPos(row = 10, col = 7)
	private DZFDouble jszjhj;

	public DZFDouble getZcyzgdzcjszj() {
		return zcyzgdzcjszj;
	}

	public void setZcyzgdzcjszj(DZFDouble zcyzgdzcjszj) {
		this.zcyzgdzcjszj = zcyzgdzcjszj;
	}

	public DZFDouble getZzzjjegdzcjszj() {
		return zzzjjegdzcjszj;
	}

	public void setZzzjjegdzcjszj(DZFDouble zzzjjegdzcjszj) {
		this.zzzjjegdzcjszj = zzzjjegdzcjszj;
	}

	public DZFDouble getYbgdgdzcjszj() {
		return ybgdgdzcjszj;
	}

	public void setYbgdgdzcjszj(DZFDouble ybgdgdzcjszj) {
		this.ybgdgdzcjszj = ybgdgdzcjszj;
	}

	public DZFDouble getJszjjsgdzcjszj() {
		return jszjjsgdzcjszj;
	}

	public void setJszjjsgdzcjszj(DZFDouble jszjjsgdzcjszj) {
		this.jszjjsgdzcjszj = jszjjsgdzcjszj;
	}

	public DZFDouble getNstjgdzcjszj() {
		return nstjgdzcjszj;
	}

	public void setNstjgdzcjszj(DZFDouble nstjgdzcjszj) {
		this.nstjgdzcjszj = nstjgdzcjszj;
	}

	public DZFDouble getJszjgdzcjszj() {
		return jszjgdzcjszj;
	}

	public void setJszjgdzcjszj(DZFDouble jszjgdzcjszj) {
		this.jszjgdzcjszj = jszjgdzcjszj;
	}

	public DZFDouble getZcyzzyhygdzcjszj() {
		return zcyzzyhygdzcjszj;
	}

	public void setZcyzzyhygdzcjszj(DZFDouble zcyzzyhygdzcjszj) {
		this.zcyzzyhygdzcjszj = zcyzzyhygdzcjszj;
	}

	public DZFDouble getZzzjjezyhygdzcjszj() {
		return zzzjjezyhygdzcjszj;
	}

	public void setZzzjjezyhygdzcjszj(DZFDouble zzzjjezyhygdzcjszj) {
		this.zzzjjezyhygdzcjszj = zzzjjezyhygdzcjszj;
	}

	public DZFDouble getYbgdzyhygdzcjszj() {
		return ybgdzyhygdzcjszj;
	}

	public void setYbgdzyhygdzcjszj(DZFDouble ybgdzyhygdzcjszj) {
		this.ybgdzyhygdzcjszj = ybgdzyhygdzcjszj;
	}

	public DZFDouble getJszjjszyhygdzcjszj() {
		return jszjjszyhygdzcjszj;
	}

	public void setJszjjszyhygdzcjszj(DZFDouble jszjjszyhygdzcjszj) {
		this.jszjjszyhygdzcjszj = jszjjszyhygdzcjszj;
	}

	public DZFDouble getNstjzyhygdzcjszj() {
		return nstjzyhygdzcjszj;
	}

	public void setNstjzyhygdzcjszj(DZFDouble nstjzyhygdzcjszj) {
		this.nstjzyhygdzcjszj = nstjzyhygdzcjszj;
	}

	public DZFDouble getJszjzyhygdzcjszj() {
		return jszjzyhygdzcjszj;
	}

	public void setJszjzyhygdzcjszj(DZFDouble jszjzyhygdzcjszj) {
		this.jszjzyhygdzcjszj = jszjzyhygdzcjszj;
	}

	public DZFDouble getZcyzqthyyfsbjszj() {
		return zcyzqthyyfsbjszj;
	}

	public void setZcyzqthyyfsbjszj(DZFDouble zcyzqthyyfsbjszj) {
		this.zcyzqthyyfsbjszj = zcyzqthyyfsbjszj;
	}

	public DZFDouble getZzzjjeqthyyfsbjszj() {
		return zzzjjeqthyyfsbjszj;
	}

	public void setZzzjjeqthyyfsbjszj(DZFDouble zzzjjeqthyyfsbjszj) {
		this.zzzjjeqthyyfsbjszj = zzzjjeqthyyfsbjszj;
	}

	public DZFDouble getYbgdqthyyfsbjszj() {
		return ybgdqthyyfsbjszj;
	}

	public void setYbgdqthyyfsbjszj(DZFDouble ybgdqthyyfsbjszj) {
		this.ybgdqthyyfsbjszj = ybgdqthyyfsbjszj;
	}

	public DZFDouble getJszjjsqthyyfsbjszj() {
		return jszjjsqthyyfsbjszj;
	}

	public void setJszjjsqthyyfsbjszj(DZFDouble jszjjsqthyyfsbjszj) {
		this.jszjjsqthyyfsbjszj = jszjjsqthyyfsbjszj;
	}

	public DZFDouble getNstjqthyyfsbjszj() {
		return nstjqthyyfsbjszj;
	}

	public void setNstjqthyyfsbjszj(DZFDouble nstjqthyyfsbjszj) {
		this.nstjqthyyfsbjszj = nstjqthyyfsbjszj;
	}

	public DZFDouble getJszjqthyyfsbjszj() {
		return jszjqthyyfsbjszj;
	}

	public void setJszjqthyyfsbjszj(DZFDouble jszjqthyyfsbjszj) {
		this.jszjqthyyfsbjszj = jszjqthyyfsbjszj;
	}

	public DZFDouble getZcyzgdzcycxkc() {
		return zcyzgdzcycxkc;
	}

	public void setZcyzgdzcycxkc(DZFDouble zcyzgdzcycxkc) {
		this.zcyzgdzcycxkc = zcyzgdzcycxkc;
	}

	public DZFDouble getZzzjjegdzcycxkc() {
		return zzzjjegdzcycxkc;
	}

	public void setZzzjjegdzcycxkc(DZFDouble zzzjjegdzcycxkc) {
		this.zzzjjegdzcycxkc = zzzjjegdzcycxkc;
	}

	public DZFDouble getYbgdgdzcycxkc() {
		return ybgdgdzcycxkc;
	}

	public void setYbgdgdzcycxkc(DZFDouble ybgdgdzcycxkc) {
		this.ybgdgdzcycxkc = ybgdgdzcycxkc;
	}

	public DZFDouble getJszjjsgdzcycxkc() {
		return jszjjsgdzcycxkc;
	}

	public void setJszjjsgdzcycxkc(DZFDouble jszjjsgdzcycxkc) {
		this.jszjjsgdzcycxkc = jszjjsgdzcycxkc;
	}

	public DZFDouble getNstjgdzcycxkc() {
		return nstjgdzcycxkc;
	}

	public void setNstjgdzcycxkc(DZFDouble nstjgdzcycxkc) {
		this.nstjgdzcycxkc = nstjgdzcycxkc;
	}

	public DZFDouble getJszjgdzcycxkc() {
		return jszjgdzcycxkc;
	}

	public void setJszjgdzcycxkc(DZFDouble jszjgdzcycxkc) {
		this.jszjgdzcycxkc = jszjgdzcycxkc;
	}

	public DZFDouble getZcyzhj() {
		return zcyzhj;
	}

	public void setZcyzhj(DZFDouble zcyzhj) {
		this.zcyzhj = zcyzhj;
	}

	public DZFDouble getZzzjjehj() {
		return zzzjjehj;
	}

	public void setZzzjjehj(DZFDouble zzzjjehj) {
		this.zzzjjehj = zzzjjehj;
	}

	public DZFDouble getYbgdhj() {
		return ybgdhj;
	}

	public void setYbgdhj(DZFDouble ybgdhj) {
		this.ybgdhj = ybgdhj;
	}

	public DZFDouble getJszjjshj() {
		return jszjjshj;
	}

	public void setJszjjshj(DZFDouble jszjjshj) {
		this.jszjjshj = jszjjshj;
	}

	public DZFDouble getNstjhj() {
		return nstjhj;
	}

	public void setNstjhj(DZFDouble nstjhj) {
		this.nstjhj = nstjhj;
	}

	public DZFDouble getJszjhj() {
		return jszjhj;
	}

	public void setJszjhj(DZFDouble jszjhj) {
		this.jszjhj = jszjhj;
	}

}
