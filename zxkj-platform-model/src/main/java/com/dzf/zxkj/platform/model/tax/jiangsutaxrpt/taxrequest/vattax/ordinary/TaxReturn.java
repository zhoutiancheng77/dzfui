package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表（适用于增值税一般纳税人） sb10101001vo_01
@TaxExcelPos(reportID = "10101001", reportname = "增值税纳税申报表（适用于增值税一般纳税人）")
public class TaxReturn {
	// 一般货物及劳务和应税服务本月数
	// 按适用税率计税销售额
	@TaxExcelPos(row = 11, col = 6)
	private DZFDouble asysljsxseybby;
	// 应税货物销售额
	@TaxExcelPos(row = 12, col = 6)
	private DZFDouble yshwxseybby;
	// 应税劳务销售额
	@TaxExcelPos(row = 13, col = 6)
	private DZFDouble yslwxseybby;
	// 纳税检查调整的销售额_适用税率
	@TaxExcelPos(row = 14, col = 6)
	private DZFDouble nsjcdzxseybby;
	// 按简易办法计税销售额
	@TaxExcelPos(row = 15, col = 6)
	private DZFDouble ajyfsjsxseybby;
	// 纳税检查调整的销售额_简易办法
	@TaxExcelPos(row = 16, col = 6)
	private DZFDouble nsjctzxseybby;
	// 免抵退办法出口销售额
	@TaxExcelPos(row = 17, col = 6)
	private DZFDouble mdtbfckxseybby;
	// 免税销售额
	@TaxExcelPos(row = 18, col = 6)
	private DZFDouble msxseybby;
	// 免税货物销售额
	@TaxExcelPos(row = 19, col = 6)
	private DZFDouble mshwxseybby;
	// 免税劳务销售额
	@TaxExcelPos(row = 20, col = 6)
	private DZFDouble mslwxseybby;
	// 销项税额
	@TaxExcelPos(row = 21, col = 6)
	private DZFDouble xxseybby;
	// 进项税额
	@TaxExcelPos(row = 22, col = 6)
	private DZFDouble jxseybby;
	// 上期留抵税额
	@TaxExcelPos(row = 23, col = 6)
	private DZFDouble sqldseybby;
	// 进项税额转出
	@TaxExcelPos(row = 24, col = 6)
	private DZFDouble jxsezcybby;
	// 免、抵、退应退税额
	@TaxExcelPos(row = 25, col = 6)
	private DZFDouble mdtytseybby;
	// 按适用税率计算的纳税检查应补缴税额
	@TaxExcelPos(row = 26, col = 6)
	private DZFDouble asysljsnsjcybjseybby;
	// 应抵扣税额合计
	@TaxExcelPos(row = 27, col = 6)
	private DZFDouble ydksehjybby;
	// 实际抵扣税额
	@TaxExcelPos(row = 28, col = 6)
	private DZFDouble sjdkseybby;
	// 应纳税额
	@TaxExcelPos(row = 29, col = 6)
	private DZFDouble ynseybby;
	// 期末留抵税额
	@TaxExcelPos(row = 30, col = 6)
	private DZFDouble qmldseybby;
	// 简易计税办法计算的应纳税额
	@TaxExcelPos(row = 31, col = 6)
	private DZFDouble jyjsynseybby;
	// 按简易计税办法计算的纳税检查应补缴税额
	@TaxExcelPos(row = 32, col = 6)
	private DZFDouble ajynsjcybjseybby;
	// 应纳税额减征额
	@TaxExcelPos(row = 33, col = 6)
	private DZFDouble ynsejzeybby;
	// 应纳税额合计
	@TaxExcelPos(row = 34, col = 6)
	private DZFDouble ynsehjybby;
	// 期初未缴税额
	@TaxExcelPos(row = 35, col = 6)
	private DZFDouble qcmjseybby;
	// 实收出口开具专用缴款书退税额
	@TaxExcelPos(row = 36, col = 6)
	private DZFDouble ssckkjzyjkstkeybby;
	// 本期预缴税额
	@TaxExcelPos(row = 37, col = 6)
	private DZFDouble bqyjseybby;
	// 分次预缴税额
	@TaxExcelPos(row = 38, col = 6)
	private DZFDouble cyjseybby;
	// 出口开具专用缴款书预缴税额
	@TaxExcelPos(row = 39, col = 6)
	private DZFDouble ckkjzyjksyjseybby;
	// 本期缴纳上期应纳税额
	@TaxExcelPos(row = 40, col = 6)
	private DZFDouble jqjnsqynseybby;
	// 本期缴纳欠缴税额
	@TaxExcelPos(row = 41, col = 6)
	private DZFDouble bqjnqjseybby;
	// 期末未缴税额（多缴为负数）
	@TaxExcelPos(row = 42, col = 6)
	private DZFDouble qmwjseybby;
	// 其中:欠缴税额≥0
	@TaxExcelPos(row = 43, col = 6)
	private DZFDouble qzqjseybby;
	// 本期应补退税额
	@TaxExcelPos(row = 44, col = 6)
	private DZFDouble bqybtseybby;
	// 期初未缴查补税额
	@TaxExcelPos(row = 46, col = 6)
	private DZFDouble qcmjcbseybby;
	// 本期入库查补税额
	@TaxExcelPos(row = 47, col = 6)
	private DZFDouble bqrkcbseybby;
	// 期末未缴款补税额
	@TaxExcelPos(row = 48, col = 6)
	private DZFDouble qmmjcbseybby;

	// 一般货物及劳务和应税服务本年累计
	// 按适用税率计税销售额
	@TaxExcelPos(row = 11, col = 10, isTotal = true)
	private DZFDouble asysljsxseybbnlj;
	// 应税货物销售额
	@TaxExcelPos(row = 12, col = 10, isTotal = true)
	private DZFDouble yshwxseybbnlj;
	// 应税劳务销售额
	@TaxExcelPos(row = 13, col = 10, isTotal = true)
	private DZFDouble yslwxseybbnlj;
	// 纳税检查调整的销售额_适用税率
	@TaxExcelPos(row = 14, col = 10, isTotal = true)
	private DZFDouble nsjcdzxseybbnlj;
	// 按简易办法计税销售额
	@TaxExcelPos(row = 15, col = 10, isTotal = true)
	private DZFDouble ajyfsjsxseybbnlj;
	// 纳税检查调整的销售额_简易办法
	@TaxExcelPos(row = 16, col = 10, isTotal = true)
	private DZFDouble nsjctzxseybbnlj;
	// 免抵退办法出口销售额
	@TaxExcelPos(row = 17, col = 10, isTotal = true)
	private DZFDouble mdtbfckxseybbnlj;
	// 免税销售额
	@TaxExcelPos(row = 18, col = 10, isTotal = true)
	private DZFDouble msxseybbnlj;
	// 免税货物销售额
	@TaxExcelPos(row = 19, col = 10, isTotal = true)
	private DZFDouble mshwxseybbnlj;
	// 免税劳务销售额
	@TaxExcelPos(row = 20, col = 10, isTotal = true)
	private DZFDouble mslwxseybbnlj;
	// 销项税额
	@TaxExcelPos(row = 21, col = 10, isTotal = true)
	private DZFDouble xxseybbnlj;
	// 进项税额
	@TaxExcelPos(row = 22, col = 10, isTotal = true)
	private DZFDouble jxseybbnlj;
	// 上期留抵税额
	@TaxExcelPos(row = 23, col = 10, isTotal = true)
	private DZFDouble sqldseybbnlj;
	// 进项税额转出
	@TaxExcelPos(row = 24, col = 10, isTotal = true)
	private DZFDouble jxsezcybbnlj;
	// 免、抵、退应退税额
	@TaxExcelPos(row = 25, col = 10, isTotal = true)
	private DZFDouble mdtytseybbnlj;
	// 按适用税率计算的纳税检查应补缴税额
	@TaxExcelPos(row = 26, col = 10, isTotal = true)
	private DZFDouble asysljsnsjcybjseybbnlj;
	// 实际抵扣税额
	@TaxExcelPos(row = 28, col = 10, isTotal = true)
	private DZFDouble sjdkseybbnlj;
	// 应纳税额
	@TaxExcelPos(row = 29, col = 10, isTotal = true)
	private DZFDouble ynseybbnlj;
	// 期末留抵税额
	@TaxExcelPos(row = 30, col = 10, isTotal = true)
	private DZFDouble qmldseybbnlj;
	// 简易计税办法计算的应纳税额
	@TaxExcelPos(row = 31, col = 10, isTotal = true)
	private DZFDouble jyjsynseybbnlj;
	// 按简易计税办法计算的纳税检查应补缴税额
	@TaxExcelPos(row = 32, col = 10, isTotal = true)
	private DZFDouble ajynsjcybjseybbnlj;
	// 应纳税额减征额
	@TaxExcelPos(row = 33, col = 10, isTotal = true)
	private DZFDouble ynsejzeybbnlj;
	// 应纳税额合计
	@TaxExcelPos(row = 34, col = 10, isTotal = true)
	private DZFDouble ynsehjybbnlj;
	// 期初未缴税额
	@TaxExcelPos(row = 35, col = 10, isTotal = true)
	private DZFDouble qcmjseybbnlj;
	// 实收出口开具专用缴款书退税额
	@TaxExcelPos(row = 36, col = 10, isTotal = true)
	private DZFDouble ssckkjzyjkstkeybbnlj;
	// 本期预缴税额
	@TaxExcelPos(row = 37, col = 10, isTotal = true)
	private DZFDouble bqyjseybbnlj;
	// 本期缴纳上期应纳税额
	@TaxExcelPos(row = 40, col = 10, isTotal = true)
	private DZFDouble jqjnsqynseybbnlj;
	// 本期缴纳欠缴税额
	@TaxExcelPos(row = 41, col = 10, isTotal = true)
	private DZFDouble bqjnqjseybbnlj;
	// 期末未缴税额（多缴为负数）
	@TaxExcelPos(row = 42, col = 10, isTotal = true)
	private DZFDouble qmwjseybbnlj;
	// 期初未缴查补税额
	@TaxExcelPos(row = 46, col = 10, isTotal = true)
	private DZFDouble qcmjcbseybbnlj;
	// 本期入库查补税额
	@TaxExcelPos(row = 47, col = 10, isTotal = true)
	private DZFDouble bqrkcbseybbnlj;
	// 期末未缴款补税额
	@TaxExcelPos(row = 48, col = 10, isTotal = true)
	private DZFDouble qmmjcbseybbnlj;

	// 即征即退货物及劳务和应税服务本月数
	// 按适用税率计税销售额
	@TaxExcelPos(row = 11, col = 11)
	private DZFDouble asysljsxsejzby;
	// 应税货物销售额
	@TaxExcelPos(row = 12, col = 11)
	private DZFDouble yshwxsejzby;
	// 应税劳务销售额
	@TaxExcelPos(row = 13, col = 11)
	private DZFDouble yslwxsejzby;
	// 纳税检查调整的销售额_适用税率
	@TaxExcelPos(row = 14, col = 11)
	private DZFDouble nsjcdzxsejzby;
	// 按简易办法计税销售额
	@TaxExcelPos(row = 15, col = 11)
	private DZFDouble ajyfsjsxsejzby;
	// 纳税检查调整的销售额_简易办法
	@TaxExcelPos(row = 16, col = 11)
	private DZFDouble nsjctzxsejzby;
	// 销项税额
	@TaxExcelPos(row = 21, col = 11)
	private DZFDouble xxsejzby;
	// 进项税额
	@TaxExcelPos(row = 22, col = 11)
	private DZFDouble jxsejzby;
	// 上期留抵税额
	@TaxExcelPos(row = 23, col = 11)
	private DZFDouble sqldsejzby;
	// 进项税额转出
	@TaxExcelPos(row = 24, col = 11)
	private DZFDouble jxsezcjzby;
	// 应抵扣税额合计
	@TaxExcelPos(row = 27, col = 11)
	private DZFDouble ydksehjjzby;
	// 实际抵扣税额
	@TaxExcelPos(row = 28, col = 11)
	private DZFDouble sjdksejzby;
	// 应纳税额
	@TaxExcelPos(row = 29, col = 11)
	private DZFDouble ynsejzby;
	// 期末留抵税额
	@TaxExcelPos(row = 30, col = 11)
	private DZFDouble qmldsejzby;
	// 简易计税办法计算的应纳税额
	@TaxExcelPos(row = 31, col = 11)
	private DZFDouble jyjsynsejzby;
	// 应纳税额减征额
	@TaxExcelPos(row = 33, col = 11)
	private DZFDouble ynsejzejzby;
	// 应纳税额合计
	@TaxExcelPos(row = 34, col = 11)
	private DZFDouble ynsehjjzby;
	// 期初未缴税额
	@TaxExcelPos(row = 35, col = 11)
	private DZFDouble qcmjsejzby;
	// 本期预缴税额
	@TaxExcelPos(row = 37, col = 11)
	private DZFDouble bqyjsejzby;
	// 分次预缴税额
	@TaxExcelPos(row = 38, col = 11)
	private DZFDouble cyjsejzby;
	// 本期缴纳上期应纳税额
	@TaxExcelPos(row = 40, col = 11)
	private DZFDouble jqjnsqynsejzby;
	// 本期缴纳欠缴税额
	@TaxExcelPos(row = 41, col = 11)
	private DZFDouble bqjnqjsejzby;
	// 期末未缴税额（多缴为负数）
	@TaxExcelPos(row = 42, col = 11)
	private DZFDouble qmwjsejzby;
	// 其中:欠缴税额≥0
	@TaxExcelPos(row = 43, col = 11)
	private DZFDouble qzqjsejzby;
	// 本期应补退税额
	@TaxExcelPos(row = 44, col = 11)
	private DZFDouble bqybtsejzby;
	// 即征即退实际退税额
	@TaxExcelPos(row = 45, col = 11)
	private DZFDouble jzjtsjtsejzby;

	// 即征即退货物及劳务和应税服务本年累计
	// 按适用税率计税销售额
	@TaxExcelPos(row = 11, col = 12, isTotal = true)
	private DZFDouble asysljsxsejzbnlj;
	// 应税货物销售额
	@TaxExcelPos(row = 12, col = 12, isTotal = true)
	private DZFDouble yshwxsejzbnlj;
	// 应税劳务销售额
	@TaxExcelPos(row = 13, col = 12, isTotal = true)
	private DZFDouble yslwxsejzbnlj;
	// 纳税检查调整的销售额_适用税率
	@TaxExcelPos(row = 14, col = 12, isTotal = true)
	private DZFDouble nsjcdzxsejzbnlj;
	// 按简易办法计税销售额
	@TaxExcelPos(row = 15, col = 12, isTotal = true)
	private DZFDouble ajyfsjsxsejzbnlj;
	// 纳税检查调整的销售额_简易办法
	@TaxExcelPos(row = 16, col = 12, isTotal = true)
	private DZFDouble nsjctzxsejzbnlj;
	// 销项税额
	@TaxExcelPos(row = 21, col = 12, isTotal = true)
	private DZFDouble xxsejzbnlj;
	// 进项税额
	@TaxExcelPos(row = 22, col = 12, isTotal = true)
	private DZFDouble jxsejzbnlj;
	// 进项税额转出
	@TaxExcelPos(row = 24, col = 12, isTotal = true)
	private DZFDouble jxsezcjzbnlj;
	// 实际抵扣税额
	@TaxExcelPos(row = 28, col = 12, isTotal = true)
	private DZFDouble sjdksejzbnlj;
	// 应纳税额
	@TaxExcelPos(row = 29, col = 12, isTotal = true)
	private DZFDouble ynsejzbnlj;
	// 简易计税办法计算的应纳税额
	@TaxExcelPos(row = 31, col = 12, isTotal = true)
	private DZFDouble jyjsynsejzbnlj;
	// 应纳税额减征额
	@TaxExcelPos(row = 33, col = 12, isTotal = true)
	private DZFDouble ynsejzejzbnlj;
	// 应纳税额合计
	@TaxExcelPos(row = 34, col = 12, isTotal = true)
	private DZFDouble ynsehjjzbnlj;
	// 期初未缴税额
	@TaxExcelPos(row = 35, col = 12, isTotal = true)
	private DZFDouble qcmjsejzbnlj;
	// 本期预缴税额
	@TaxExcelPos(row = 37, col = 12, isTotal = true)
	private DZFDouble bqyjsejzbnlj;
	// 本期缴纳上期应纳税额
	@TaxExcelPos(row = 40, col = 12, isTotal = true)
	private DZFDouble jqjnsqynsejzbnlj;
	// 本期缴纳欠缴税额
	@TaxExcelPos(row = 41, col = 12, isTotal = true)
	private DZFDouble bqjnqjsejzbnlj;
	// 期末未缴税额（多缴为负数）
	@TaxExcelPos(row = 42, col = 12, isTotal = true)
	private DZFDouble qmwjsejzbnlj;
	// 即征即退实际退税额
	@TaxExcelPos(row = 45, col = 12, isTotal = true)
	private DZFDouble jzjtsjtsejzbnlj;
	public DZFDouble getAsysljsxseybby() {
		return asysljsxseybby;
	}
	public void setAsysljsxseybby(DZFDouble asysljsxseybby) {
		this.asysljsxseybby = asysljsxseybby;
	}
	public DZFDouble getYshwxseybby() {
		return yshwxseybby;
	}
	public void setYshwxseybby(DZFDouble yshwxseybby) {
		this.yshwxseybby = yshwxseybby;
	}
	public DZFDouble getYslwxseybby() {
		return yslwxseybby;
	}
	public void setYslwxseybby(DZFDouble yslwxseybby) {
		this.yslwxseybby = yslwxseybby;
	}
	public DZFDouble getNsjcdzxseybby() {
		return nsjcdzxseybby;
	}
	public void setNsjcdzxseybby(DZFDouble nsjcdzxseybby) {
		this.nsjcdzxseybby = nsjcdzxseybby;
	}
	public DZFDouble getAjyfsjsxseybby() {
		return ajyfsjsxseybby;
	}
	public void setAjyfsjsxseybby(DZFDouble ajyfsjsxseybby) {
		this.ajyfsjsxseybby = ajyfsjsxseybby;
	}
	public DZFDouble getNsjctzxseybby() {
		return nsjctzxseybby;
	}
	public void setNsjctzxseybby(DZFDouble nsjctzxseybby) {
		this.nsjctzxseybby = nsjctzxseybby;
	}
	public DZFDouble getMdtbfckxseybby() {
		return mdtbfckxseybby;
	}
	public void setMdtbfckxseybby(DZFDouble mdtbfckxseybby) {
		this.mdtbfckxseybby = mdtbfckxseybby;
	}
	public DZFDouble getMsxseybby() {
		return msxseybby;
	}
	public void setMsxseybby(DZFDouble msxseybby) {
		this.msxseybby = msxseybby;
	}
	public DZFDouble getMshwxseybby() {
		return mshwxseybby;
	}
	public void setMshwxseybby(DZFDouble mshwxseybby) {
		this.mshwxseybby = mshwxseybby;
	}
	public DZFDouble getMslwxseybby() {
		return mslwxseybby;
	}
	public void setMslwxseybby(DZFDouble mslwxseybby) {
		this.mslwxseybby = mslwxseybby;
	}
	public DZFDouble getXxseybby() {
		return xxseybby;
	}
	public void setXxseybby(DZFDouble xxseybby) {
		this.xxseybby = xxseybby;
	}
	public DZFDouble getJxseybby() {
		return jxseybby;
	}
	public void setJxseybby(DZFDouble jxseybby) {
		this.jxseybby = jxseybby;
	}
	public DZFDouble getSqldseybby() {
		return sqldseybby;
	}
	public void setSqldseybby(DZFDouble sqldseybby) {
		this.sqldseybby = sqldseybby;
	}
	public DZFDouble getJxsezcybby() {
		return jxsezcybby;
	}
	public void setJxsezcybby(DZFDouble jxsezcybby) {
		this.jxsezcybby = jxsezcybby;
	}
	public DZFDouble getMdtytseybby() {
		return mdtytseybby;
	}
	public void setMdtytseybby(DZFDouble mdtytseybby) {
		this.mdtytseybby = mdtytseybby;
	}
	public DZFDouble getAsysljsnsjcybjseybby() {
		return asysljsnsjcybjseybby;
	}
	public void setAsysljsnsjcybjseybby(DZFDouble asysljsnsjcybjseybby) {
		this.asysljsnsjcybjseybby = asysljsnsjcybjseybby;
	}
	public DZFDouble getYdksehjybby() {
		return ydksehjybby;
	}
	public void setYdksehjybby(DZFDouble ydksehjybby) {
		this.ydksehjybby = ydksehjybby;
	}
	public DZFDouble getSjdkseybby() {
		return sjdkseybby;
	}
	public void setSjdkseybby(DZFDouble sjdkseybby) {
		this.sjdkseybby = sjdkseybby;
	}
	public DZFDouble getYnseybby() {
		return ynseybby;
	}
	public void setYnseybby(DZFDouble ynseybby) {
		this.ynseybby = ynseybby;
	}
	public DZFDouble getQmldseybby() {
		return qmldseybby;
	}
	public void setQmldseybby(DZFDouble qmldseybby) {
		this.qmldseybby = qmldseybby;
	}
	public DZFDouble getJyjsynseybby() {
		return jyjsynseybby;
	}
	public void setJyjsynseybby(DZFDouble jyjsynseybby) {
		this.jyjsynseybby = jyjsynseybby;
	}
	public DZFDouble getAjynsjcybjseybby() {
		return ajynsjcybjseybby;
	}
	public void setAjynsjcybjseybby(DZFDouble ajynsjcybjseybby) {
		this.ajynsjcybjseybby = ajynsjcybjseybby;
	}
	public DZFDouble getYnsejzeybby() {
		return ynsejzeybby;
	}
	public void setYnsejzeybby(DZFDouble ynsejzeybby) {
		this.ynsejzeybby = ynsejzeybby;
	}
	public DZFDouble getYnsehjybby() {
		return ynsehjybby;
	}
	public void setYnsehjybby(DZFDouble ynsehjybby) {
		this.ynsehjybby = ynsehjybby;
	}
	public DZFDouble getQcmjseybby() {
		return qcmjseybby;
	}
	public void setQcmjseybby(DZFDouble qcmjseybby) {
		this.qcmjseybby = qcmjseybby;
	}
	public DZFDouble getSsckkjzyjkstkeybby() {
		return ssckkjzyjkstkeybby;
	}
	public void setSsckkjzyjkstkeybby(DZFDouble ssckkjzyjkstkeybby) {
		this.ssckkjzyjkstkeybby = ssckkjzyjkstkeybby;
	}
	public DZFDouble getBqyjseybby() {
		return bqyjseybby;
	}
	public void setBqyjseybby(DZFDouble bqyjseybby) {
		this.bqyjseybby = bqyjseybby;
	}
	public DZFDouble getCyjseybby() {
		return cyjseybby;
	}
	public void setCyjseybby(DZFDouble cyjseybby) {
		this.cyjseybby = cyjseybby;
	}
	public DZFDouble getCkkjzyjksyjseybby() {
		return ckkjzyjksyjseybby;
	}
	public void setCkkjzyjksyjseybby(DZFDouble ckkjzyjksyjseybby) {
		this.ckkjzyjksyjseybby = ckkjzyjksyjseybby;
	}
	public DZFDouble getJqjnsqynseybby() {
		return jqjnsqynseybby;
	}
	public void setJqjnsqynseybby(DZFDouble jqjnsqynseybby) {
		this.jqjnsqynseybby = jqjnsqynseybby;
	}
	public DZFDouble getBqjnqjseybby() {
		return bqjnqjseybby;
	}
	public void setBqjnqjseybby(DZFDouble bqjnqjseybby) {
		this.bqjnqjseybby = bqjnqjseybby;
	}
	public DZFDouble getQmwjseybby() {
		return qmwjseybby;
	}
	public void setQmwjseybby(DZFDouble qmwjseybby) {
		this.qmwjseybby = qmwjseybby;
	}
	public DZFDouble getQzqjseybby() {
		return qzqjseybby;
	}
	public void setQzqjseybby(DZFDouble qzqjseybby) {
		this.qzqjseybby = qzqjseybby;
	}
	public DZFDouble getBqybtseybby() {
		return bqybtseybby;
	}
	public void setBqybtseybby(DZFDouble bqybtseybby) {
		this.bqybtseybby = bqybtseybby;
	}
	public DZFDouble getQcmjcbseybby() {
		return qcmjcbseybby;
	}
	public void setQcmjcbseybby(DZFDouble qcmjcbseybby) {
		this.qcmjcbseybby = qcmjcbseybby;
	}
	public DZFDouble getBqrkcbseybby() {
		return bqrkcbseybby;
	}
	public void setBqrkcbseybby(DZFDouble bqrkcbseybby) {
		this.bqrkcbseybby = bqrkcbseybby;
	}
	public DZFDouble getQmmjcbseybby() {
		return qmmjcbseybby;
	}
	public void setQmmjcbseybby(DZFDouble qmmjcbseybby) {
		this.qmmjcbseybby = qmmjcbseybby;
	}
	public DZFDouble getAsysljsxseybbnlj() {
		return asysljsxseybbnlj;
	}
	public void setAsysljsxseybbnlj(DZFDouble asysljsxseybbnlj) {
		this.asysljsxseybbnlj = asysljsxseybbnlj;
	}
	public DZFDouble getYshwxseybbnlj() {
		return yshwxseybbnlj;
	}
	public void setYshwxseybbnlj(DZFDouble yshwxseybbnlj) {
		this.yshwxseybbnlj = yshwxseybbnlj;
	}
	public DZFDouble getYslwxseybbnlj() {
		return yslwxseybbnlj;
	}
	public void setYslwxseybbnlj(DZFDouble yslwxseybbnlj) {
		this.yslwxseybbnlj = yslwxseybbnlj;
	}
	public DZFDouble getNsjcdzxseybbnlj() {
		return nsjcdzxseybbnlj;
	}
	public void setNsjcdzxseybbnlj(DZFDouble nsjcdzxseybbnlj) {
		this.nsjcdzxseybbnlj = nsjcdzxseybbnlj;
	}
	public DZFDouble getAjyfsjsxseybbnlj() {
		return ajyfsjsxseybbnlj;
	}
	public void setAjyfsjsxseybbnlj(DZFDouble ajyfsjsxseybbnlj) {
		this.ajyfsjsxseybbnlj = ajyfsjsxseybbnlj;
	}
	public DZFDouble getNsjctzxseybbnlj() {
		return nsjctzxseybbnlj;
	}
	public void setNsjctzxseybbnlj(DZFDouble nsjctzxseybbnlj) {
		this.nsjctzxseybbnlj = nsjctzxseybbnlj;
	}
	public DZFDouble getMdtbfckxseybbnlj() {
		return mdtbfckxseybbnlj;
	}
	public void setMdtbfckxseybbnlj(DZFDouble mdtbfckxseybbnlj) {
		this.mdtbfckxseybbnlj = mdtbfckxseybbnlj;
	}
	public DZFDouble getMsxseybbnlj() {
		return msxseybbnlj;
	}
	public void setMsxseybbnlj(DZFDouble msxseybbnlj) {
		this.msxseybbnlj = msxseybbnlj;
	}
	public DZFDouble getMshwxseybbnlj() {
		return mshwxseybbnlj;
	}
	public void setMshwxseybbnlj(DZFDouble mshwxseybbnlj) {
		this.mshwxseybbnlj = mshwxseybbnlj;
	}
	public DZFDouble getMslwxseybbnlj() {
		return mslwxseybbnlj;
	}
	public void setMslwxseybbnlj(DZFDouble mslwxseybbnlj) {
		this.mslwxseybbnlj = mslwxseybbnlj;
	}
	public DZFDouble getXxseybbnlj() {
		return xxseybbnlj;
	}
	public void setXxseybbnlj(DZFDouble xxseybbnlj) {
		this.xxseybbnlj = xxseybbnlj;
	}
	public DZFDouble getJxseybbnlj() {
		return jxseybbnlj;
	}
	public void setJxseybbnlj(DZFDouble jxseybbnlj) {
		this.jxseybbnlj = jxseybbnlj;
	}
	public DZFDouble getSqldseybbnlj() {
		return sqldseybbnlj;
	}
	public void setSqldseybbnlj(DZFDouble sqldseybbnlj) {
		this.sqldseybbnlj = sqldseybbnlj;
	}
	public DZFDouble getJxsezcybbnlj() {
		return jxsezcybbnlj;
	}
	public void setJxsezcybbnlj(DZFDouble jxsezcybbnlj) {
		this.jxsezcybbnlj = jxsezcybbnlj;
	}
	public DZFDouble getMdtytseybbnlj() {
		return mdtytseybbnlj;
	}
	public void setMdtytseybbnlj(DZFDouble mdtytseybbnlj) {
		this.mdtytseybbnlj = mdtytseybbnlj;
	}
	public DZFDouble getAsysljsnsjcybjseybbnlj() {
		return asysljsnsjcybjseybbnlj;
	}
	public void setAsysljsnsjcybjseybbnlj(DZFDouble asysljsnsjcybjseybbnlj) {
		this.asysljsnsjcybjseybbnlj = asysljsnsjcybjseybbnlj;
	}
	public DZFDouble getSjdkseybbnlj() {
		return sjdkseybbnlj;
	}
	public void setSjdkseybbnlj(DZFDouble sjdkseybbnlj) {
		this.sjdkseybbnlj = sjdkseybbnlj;
	}
	public DZFDouble getYnseybbnlj() {
		return ynseybbnlj;
	}
	public void setYnseybbnlj(DZFDouble ynseybbnlj) {
		this.ynseybbnlj = ynseybbnlj;
	}
	public DZFDouble getQmldseybbnlj() {
		return qmldseybbnlj;
	}
	public void setQmldseybbnlj(DZFDouble qmldseybbnlj) {
		this.qmldseybbnlj = qmldseybbnlj;
	}
	public DZFDouble getJyjsynseybbnlj() {
		return jyjsynseybbnlj;
	}
	public void setJyjsynseybbnlj(DZFDouble jyjsynseybbnlj) {
		this.jyjsynseybbnlj = jyjsynseybbnlj;
	}
	public DZFDouble getAjynsjcybjseybbnlj() {
		return ajynsjcybjseybbnlj;
	}
	public void setAjynsjcybjseybbnlj(DZFDouble ajynsjcybjseybbnlj) {
		this.ajynsjcybjseybbnlj = ajynsjcybjseybbnlj;
	}
	public DZFDouble getYnsejzeybbnlj() {
		return ynsejzeybbnlj;
	}
	public void setYnsejzeybbnlj(DZFDouble ynsejzeybbnlj) {
		this.ynsejzeybbnlj = ynsejzeybbnlj;
	}
	public DZFDouble getYnsehjybbnlj() {
		return ynsehjybbnlj;
	}
	public void setYnsehjybbnlj(DZFDouble ynsehjybbnlj) {
		this.ynsehjybbnlj = ynsehjybbnlj;
	}
	public DZFDouble getQcmjseybbnlj() {
		return qcmjseybbnlj;
	}
	public void setQcmjseybbnlj(DZFDouble qcmjseybbnlj) {
		this.qcmjseybbnlj = qcmjseybbnlj;
	}
	public DZFDouble getSsckkjzyjkstkeybbnlj() {
		return ssckkjzyjkstkeybbnlj;
	}
	public void setSsckkjzyjkstkeybbnlj(DZFDouble ssckkjzyjkstkeybbnlj) {
		this.ssckkjzyjkstkeybbnlj = ssckkjzyjkstkeybbnlj;
	}
	public DZFDouble getBqyjseybbnlj() {
		return bqyjseybbnlj;
	}
	public void setBqyjseybbnlj(DZFDouble bqyjseybbnlj) {
		this.bqyjseybbnlj = bqyjseybbnlj;
	}
	public DZFDouble getJqjnsqynseybbnlj() {
		return jqjnsqynseybbnlj;
	}
	public void setJqjnsqynseybbnlj(DZFDouble jqjnsqynseybbnlj) {
		this.jqjnsqynseybbnlj = jqjnsqynseybbnlj;
	}
	public DZFDouble getBqjnqjseybbnlj() {
		return bqjnqjseybbnlj;
	}
	public void setBqjnqjseybbnlj(DZFDouble bqjnqjseybbnlj) {
		this.bqjnqjseybbnlj = bqjnqjseybbnlj;
	}
	public DZFDouble getQmwjseybbnlj() {
		return qmwjseybbnlj;
	}
	public void setQmwjseybbnlj(DZFDouble qmwjseybbnlj) {
		this.qmwjseybbnlj = qmwjseybbnlj;
	}
	public DZFDouble getQcmjcbseybbnlj() {
		return qcmjcbseybbnlj;
	}
	public void setQcmjcbseybbnlj(DZFDouble qcmjcbseybbnlj) {
		this.qcmjcbseybbnlj = qcmjcbseybbnlj;
	}
	public DZFDouble getBqrkcbseybbnlj() {
		return bqrkcbseybbnlj;
	}
	public void setBqrkcbseybbnlj(DZFDouble bqrkcbseybbnlj) {
		this.bqrkcbseybbnlj = bqrkcbseybbnlj;
	}
	public DZFDouble getQmmjcbseybbnlj() {
		return qmmjcbseybbnlj;
	}
	public void setQmmjcbseybbnlj(DZFDouble qmmjcbseybbnlj) {
		this.qmmjcbseybbnlj = qmmjcbseybbnlj;
	}
	public DZFDouble getAsysljsxsejzby() {
		return asysljsxsejzby;
	}
	public void setAsysljsxsejzby(DZFDouble asysljsxsejzby) {
		this.asysljsxsejzby = asysljsxsejzby;
	}
	public DZFDouble getYshwxsejzby() {
		return yshwxsejzby;
	}
	public void setYshwxsejzby(DZFDouble yshwxsejzby) {
		this.yshwxsejzby = yshwxsejzby;
	}
	public DZFDouble getYslwxsejzby() {
		return yslwxsejzby;
	}
	public void setYslwxsejzby(DZFDouble yslwxsejzby) {
		this.yslwxsejzby = yslwxsejzby;
	}
	public DZFDouble getNsjcdzxsejzby() {
		return nsjcdzxsejzby;
	}
	public void setNsjcdzxsejzby(DZFDouble nsjcdzxsejzby) {
		this.nsjcdzxsejzby = nsjcdzxsejzby;
	}
	public DZFDouble getAjyfsjsxsejzby() {
		return ajyfsjsxsejzby;
	}
	public void setAjyfsjsxsejzby(DZFDouble ajyfsjsxsejzby) {
		this.ajyfsjsxsejzby = ajyfsjsxsejzby;
	}
	public DZFDouble getNsjctzxsejzby() {
		return nsjctzxsejzby;
	}
	public void setNsjctzxsejzby(DZFDouble nsjctzxsejzby) {
		this.nsjctzxsejzby = nsjctzxsejzby;
	}
	public DZFDouble getXxsejzby() {
		return xxsejzby;
	}
	public void setXxsejzby(DZFDouble xxsejzby) {
		this.xxsejzby = xxsejzby;
	}
	public DZFDouble getJxsejzby() {
		return jxsejzby;
	}
	public void setJxsejzby(DZFDouble jxsejzby) {
		this.jxsejzby = jxsejzby;
	}
	public DZFDouble getSqldsejzby() {
		return sqldsejzby;
	}
	public void setSqldsejzby(DZFDouble sqldsejzby) {
		this.sqldsejzby = sqldsejzby;
	}
	public DZFDouble getJxsezcjzby() {
		return jxsezcjzby;
	}
	public void setJxsezcjzby(DZFDouble jxsezcjzby) {
		this.jxsezcjzby = jxsezcjzby;
	}
	public DZFDouble getYdksehjjzby() {
		return ydksehjjzby;
	}
	public void setYdksehjjzby(DZFDouble ydksehjjzby) {
		this.ydksehjjzby = ydksehjjzby;
	}
	public DZFDouble getSjdksejzby() {
		return sjdksejzby;
	}
	public void setSjdksejzby(DZFDouble sjdksejzby) {
		this.sjdksejzby = sjdksejzby;
	}
	public DZFDouble getYnsejzby() {
		return ynsejzby;
	}
	public void setYnsejzby(DZFDouble ynsejzby) {
		this.ynsejzby = ynsejzby;
	}
	public DZFDouble getQmldsejzby() {
		return qmldsejzby;
	}
	public void setQmldsejzby(DZFDouble qmldsejzby) {
		this.qmldsejzby = qmldsejzby;
	}
	public DZFDouble getJyjsynsejzby() {
		return jyjsynsejzby;
	}
	public void setJyjsynsejzby(DZFDouble jyjsynsejzby) {
		this.jyjsynsejzby = jyjsynsejzby;
	}
	public DZFDouble getYnsejzejzby() {
		return ynsejzejzby;
	}
	public void setYnsejzejzby(DZFDouble ynsejzejzby) {
		this.ynsejzejzby = ynsejzejzby;
	}
	public DZFDouble getYnsehjjzby() {
		return ynsehjjzby;
	}
	public void setYnsehjjzby(DZFDouble ynsehjjzby) {
		this.ynsehjjzby = ynsehjjzby;
	}
	public DZFDouble getQcmjsejzby() {
		return qcmjsejzby;
	}
	public void setQcmjsejzby(DZFDouble qcmjsejzby) {
		this.qcmjsejzby = qcmjsejzby;
	}
	public DZFDouble getBqyjsejzby() {
		return bqyjsejzby;
	}
	public void setBqyjsejzby(DZFDouble bqyjsejzby) {
		this.bqyjsejzby = bqyjsejzby;
	}
	public DZFDouble getCyjsejzby() {
		return cyjsejzby;
	}
	public void setCyjsejzby(DZFDouble cyjsejzby) {
		this.cyjsejzby = cyjsejzby;
	}
	public DZFDouble getJqjnsqynsejzby() {
		return jqjnsqynsejzby;
	}
	public void setJqjnsqynsejzby(DZFDouble jqjnsqynsejzby) {
		this.jqjnsqynsejzby = jqjnsqynsejzby;
	}
	public DZFDouble getBqjnqjsejzby() {
		return bqjnqjsejzby;
	}
	public void setBqjnqjsejzby(DZFDouble bqjnqjsejzby) {
		this.bqjnqjsejzby = bqjnqjsejzby;
	}
	public DZFDouble getQmwjsejzby() {
		return qmwjsejzby;
	}
	public void setQmwjsejzby(DZFDouble qmwjsejzby) {
		this.qmwjsejzby = qmwjsejzby;
	}
	public DZFDouble getQzqjsejzby() {
		return qzqjsejzby;
	}
	public void setQzqjsejzby(DZFDouble qzqjsejzby) {
		this.qzqjsejzby = qzqjsejzby;
	}
	public DZFDouble getBqybtsejzby() {
		return bqybtsejzby;
	}
	public void setBqybtsejzby(DZFDouble bqybtsejzby) {
		this.bqybtsejzby = bqybtsejzby;
	}
	public DZFDouble getJzjtsjtsejzby() {
		return jzjtsjtsejzby;
	}
	public void setJzjtsjtsejzby(DZFDouble jzjtsjtsejzby) {
		this.jzjtsjtsejzby = jzjtsjtsejzby;
	}
	public DZFDouble getAsysljsxsejzbnlj() {
		return asysljsxsejzbnlj;
	}
	public void setAsysljsxsejzbnlj(DZFDouble asysljsxsejzbnlj) {
		this.asysljsxsejzbnlj = asysljsxsejzbnlj;
	}
	public DZFDouble getYshwxsejzbnlj() {
		return yshwxsejzbnlj;
	}
	public void setYshwxsejzbnlj(DZFDouble yshwxsejzbnlj) {
		this.yshwxsejzbnlj = yshwxsejzbnlj;
	}
	public DZFDouble getYslwxsejzbnlj() {
		return yslwxsejzbnlj;
	}
	public void setYslwxsejzbnlj(DZFDouble yslwxsejzbnlj) {
		this.yslwxsejzbnlj = yslwxsejzbnlj;
	}
	public DZFDouble getNsjcdzxsejzbnlj() {
		return nsjcdzxsejzbnlj;
	}
	public void setNsjcdzxsejzbnlj(DZFDouble nsjcdzxsejzbnlj) {
		this.nsjcdzxsejzbnlj = nsjcdzxsejzbnlj;
	}
	public DZFDouble getAjyfsjsxsejzbnlj() {
		return ajyfsjsxsejzbnlj;
	}
	public void setAjyfsjsxsejzbnlj(DZFDouble ajyfsjsxsejzbnlj) {
		this.ajyfsjsxsejzbnlj = ajyfsjsxsejzbnlj;
	}
	public DZFDouble getNsjctzxsejzbnlj() {
		return nsjctzxsejzbnlj;
	}
	public void setNsjctzxsejzbnlj(DZFDouble nsjctzxsejzbnlj) {
		this.nsjctzxsejzbnlj = nsjctzxsejzbnlj;
	}
	public DZFDouble getXxsejzbnlj() {
		return xxsejzbnlj;
	}
	public void setXxsejzbnlj(DZFDouble xxsejzbnlj) {
		this.xxsejzbnlj = xxsejzbnlj;
	}
	public DZFDouble getJxsejzbnlj() {
		return jxsejzbnlj;
	}
	public void setJxsejzbnlj(DZFDouble jxsejzbnlj) {
		this.jxsejzbnlj = jxsejzbnlj;
	}
	public DZFDouble getJxsezcjzbnlj() {
		return jxsezcjzbnlj;
	}
	public void setJxsezcjzbnlj(DZFDouble jxsezcjzbnlj) {
		this.jxsezcjzbnlj = jxsezcjzbnlj;
	}
	public DZFDouble getSjdksejzbnlj() {
		return sjdksejzbnlj;
	}
	public void setSjdksejzbnlj(DZFDouble sjdksejzbnlj) {
		this.sjdksejzbnlj = sjdksejzbnlj;
	}
	public DZFDouble getYnsejzbnlj() {
		return ynsejzbnlj;
	}
	public void setYnsejzbnlj(DZFDouble ynsejzbnlj) {
		this.ynsejzbnlj = ynsejzbnlj;
	}
	public DZFDouble getJyjsynsejzbnlj() {
		return jyjsynsejzbnlj;
	}
	public void setJyjsynsejzbnlj(DZFDouble jyjsynsejzbnlj) {
		this.jyjsynsejzbnlj = jyjsynsejzbnlj;
	}
	public DZFDouble getYnsejzejzbnlj() {
		return ynsejzejzbnlj;
	}
	public void setYnsejzejzbnlj(DZFDouble ynsejzejzbnlj) {
		this.ynsejzejzbnlj = ynsejzejzbnlj;
	}
	public DZFDouble getYnsehjjzbnlj() {
		return ynsehjjzbnlj;
	}
	public void setYnsehjjzbnlj(DZFDouble ynsehjjzbnlj) {
		this.ynsehjjzbnlj = ynsehjjzbnlj;
	}
	public DZFDouble getQcmjsejzbnlj() {
		return qcmjsejzbnlj;
	}
	public void setQcmjsejzbnlj(DZFDouble qcmjsejzbnlj) {
		this.qcmjsejzbnlj = qcmjsejzbnlj;
	}
	public DZFDouble getBqyjsejzbnlj() {
		return bqyjsejzbnlj;
	}
	public void setBqyjsejzbnlj(DZFDouble bqyjsejzbnlj) {
		this.bqyjsejzbnlj = bqyjsejzbnlj;
	}
	public DZFDouble getJqjnsqynsejzbnlj() {
		return jqjnsqynsejzbnlj;
	}
	public void setJqjnsqynsejzbnlj(DZFDouble jqjnsqynsejzbnlj) {
		this.jqjnsqynsejzbnlj = jqjnsqynsejzbnlj;
	}
	public DZFDouble getBqjnqjsejzbnlj() {
		return bqjnqjsejzbnlj;
	}
	public void setBqjnqjsejzbnlj(DZFDouble bqjnqjsejzbnlj) {
		this.bqjnqjsejzbnlj = bqjnqjsejzbnlj;
	}
	public DZFDouble getQmwjsejzbnlj() {
		return qmwjsejzbnlj;
	}
	public void setQmwjsejzbnlj(DZFDouble qmwjsejzbnlj) {
		this.qmwjsejzbnlj = qmwjsejzbnlj;
	}
	public DZFDouble getJzjtsjtsejzbnlj() {
		return jzjtsjtsejzbnlj;
	}
	public void setJzjtsjtsejzbnlj(DZFDouble jzjtsjtsejzbnlj) {
		this.jzjtsjtsejzbnlj = jzjtsjtsejzbnlj;
	}

}
