package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表附列资料（四）（税额抵减情况表）  sb10101005vo_01
@TaxExcelPos(reportID = "10101005", reportname = "增值税纳税申报表附列资料（四）")
public class TaxReturnAttach4 {
	// 增值税税控系统专用设备费及技术维护费
	// 期初余额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble zzsskxtzysbfjjswhfqcye;
	// 本期应抵减税额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble zzsskxtzysbfjjswhfbqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(row = 7, col = 5)
	private DZFDouble zzsskxtzysbfjjswhfbqsjdjse;
	// 本期发生额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble zzsskxtzysbfjjswhfbqfse;
	// 期末余额
	@TaxExcelPos(row = 7, col = 6, isTotal = true)
	private DZFDouble zzsskxtzysbfjjswhfqmye;

	// 分支机构预征缴纳税款
	// 期初余额
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble fzjgyzsjnskqcye;
	// 本期应抵减税额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble fzjgyzsjnskbqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(row = 8, col = 5)
	private DZFDouble fzjgyzsjnskbqsjdjse;
	// 本期发生额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble fzjgyzsjnskbqfse;
	// 期末余额
	@TaxExcelPos(row = 8, col = 6, isTotal = true)
	private DZFDouble fzjgyzsjnskqmye;

	// 建筑服务预征缴纳税款
	// 期初余额
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble jzfwyzjnskqcye;
	// 本期应抵减税额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble jzfwyzjnskbqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(row = 9, col = 5)
	private DZFDouble jzfwyzjnskbqsjdjse;
	// 本期发生额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble jzfwyzjnskbqfse;
	// 期末余额
	@TaxExcelPos(row = 9, col = 6, isTotal = true)
	private DZFDouble jzfwyzjnskqmye;

	// 销售不动产预征缴纳税款
	// 期初余额
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble xsbdcyzjnskqcye;
	// 本期应抵减税额
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble xsbdcyzjnskbqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(row = 10, col = 5)
	private DZFDouble xsbdcyzjnskbqsjdjse;
	// 本期发生额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble xsbdcyzjnskbqfse;
	// 期末余额
	@TaxExcelPos(row = 10, col = 6, isTotal = true)
	private DZFDouble xsbdcyzjnskqmye;

	// 出租不动产预征缴纳税款
	// 期初余额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble czbdcyzjnskqcye;
	// 本期应抵减税额
	@TaxExcelPos(row = 11, col = 4)
	private DZFDouble czbdcyzjnskbqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(row = 11, col = 5)
	private DZFDouble czbdcyzjnskbqsjdjse;
	// 本期发生额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble czbdcyzjnskbqfse;
	// 期末余额
	@TaxExcelPos(row = 11, col = 6, isTotal = true)
	private DZFDouble czbdcyzjnskqmye;

	// 二、加计抵减情况
	// 一般项目加计抵减额计算
	// 期初余额
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble ybxmjjdjeqcye;
	// 本期发生额
	@TaxExcelPos(row = 15, col = 4)
	private DZFDouble ybxmjjdjebqfse;
	// 本期调减额
	@TaxExcelPos(row = 15, col = 5)
	private DZFDouble ybxmjjdjebqtje;
	// 本期可抵减额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble ybxmjjdjebqkdje;
	// 本期实际抵减额
	@TaxExcelPos(row = 15, col = 6)
	private DZFDouble ybxmjjdjebqsjdje;
	// 期末余额
	@TaxExcelPos(row = 15, col = 7)
	private DZFDouble ybxmjjdjeqmye;

	// 即征即退项目加计抵减额计算
	// 期初余额
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble jzjtxmjjdjeqcye;
	// 本期发生额
	@TaxExcelPos(row = 16, col = 4)
	private DZFDouble jzjtxmjjdjebqfse;
	// 本期调减额
	@TaxExcelPos(row = 16, col = 5)
	private DZFDouble jzjtxmjjdjebqtje;
	// 本期可抵减额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble jzjtxmjjdjebqkdje;
	// 本期实际抵减额
	@TaxExcelPos(row = 16, col = 6)
	private DZFDouble jzjtxmjjdjebqsjdje;
	// 期末余额
	@TaxExcelPos(row = 16, col = 7)
	private DZFDouble jzjtxmjjdjeqmye;

	// 加计抵减项目 合计
	// 期初余额
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble jjdjxmqcyehj;
	// 本期发生额
	@TaxExcelPos(row = 17, col = 4)
	private DZFDouble jjdjxmbqfsehj;
	// 本期调减额
	@TaxExcelPos(row = 17, col = 5)
	private DZFDouble jjdjxmbqtjehj;
	// 本期可抵减额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble jjdjxmbqkdjehj;
	// 本期实际抵减额
	@TaxExcelPos(row = 17, col = 6)
	private DZFDouble jjdjxmbqsjdjehj;
	// 期末余额
	@TaxExcelPos(row = 17, col = 7)
	private DZFDouble jjdjxmqmtehj;

	public DZFDouble getZzsskxtzysbfjjswhfqcye() {
		return zzsskxtzysbfjjswhfqcye;
	}

	public void setZzsskxtzysbfjjswhfqcye(DZFDouble zzsskxtzysbfjjswhfqcye) {
		this.zzsskxtzysbfjjswhfqcye = zzsskxtzysbfjjswhfqcye;
	}

	public DZFDouble getZzsskxtzysbfjjswhfbqydjse() {
		return zzsskxtzysbfjjswhfbqydjse;
	}

	public void setZzsskxtzysbfjjswhfbqydjse(DZFDouble zzsskxtzysbfjjswhfbqydjse) {
		this.zzsskxtzysbfjjswhfbqydjse = zzsskxtzysbfjjswhfbqydjse;
	}

	public DZFDouble getZzsskxtzysbfjjswhfbqsjdjse() {
		return zzsskxtzysbfjjswhfbqsjdjse;
	}

	public void setZzsskxtzysbfjjswhfbqsjdjse(
			DZFDouble zzsskxtzysbfjjswhfbqsjdjse) {
		this.zzsskxtzysbfjjswhfbqsjdjse = zzsskxtzysbfjjswhfbqsjdjse;
	}

	public DZFDouble getZzsskxtzysbfjjswhfbqfse() {
		return zzsskxtzysbfjjswhfbqfse;
	}

	public void setZzsskxtzysbfjjswhfbqfse(DZFDouble zzsskxtzysbfjjswhfbqfse) {
		this.zzsskxtzysbfjjswhfbqfse = zzsskxtzysbfjjswhfbqfse;
	}

	public DZFDouble getZzsskxtzysbfjjswhfqmye() {
		return zzsskxtzysbfjjswhfqmye;
	}

	public void setZzsskxtzysbfjjswhfqmye(DZFDouble zzsskxtzysbfjjswhfqmye) {
		this.zzsskxtzysbfjjswhfqmye = zzsskxtzysbfjjswhfqmye;
	}

	public DZFDouble getFzjgyzsjnskqcye() {
		return fzjgyzsjnskqcye;
	}

	public void setFzjgyzsjnskqcye(DZFDouble fzjgyzsjnskqcye) {
		this.fzjgyzsjnskqcye = fzjgyzsjnskqcye;
	}

	public DZFDouble getFzjgyzsjnskbqydjse() {
		return fzjgyzsjnskbqydjse;
	}

	public void setFzjgyzsjnskbqydjse(DZFDouble fzjgyzsjnskbqydjse) {
		this.fzjgyzsjnskbqydjse = fzjgyzsjnskbqydjse;
	}

	public DZFDouble getFzjgyzsjnskbqsjdjse() {
		return fzjgyzsjnskbqsjdjse;
	}

	public void setFzjgyzsjnskbqsjdjse(DZFDouble fzjgyzsjnskbqsjdjse) {
		this.fzjgyzsjnskbqsjdjse = fzjgyzsjnskbqsjdjse;
	}

	public DZFDouble getFzjgyzsjnskbqfse() {
		return fzjgyzsjnskbqfse;
	}

	public void setFzjgyzsjnskbqfse(DZFDouble fzjgyzsjnskbqfse) {
		this.fzjgyzsjnskbqfse = fzjgyzsjnskbqfse;
	}

	public DZFDouble getFzjgyzsjnskqmye() {
		return fzjgyzsjnskqmye;
	}

	public void setFzjgyzsjnskqmye(DZFDouble fzjgyzsjnskqmye) {
		this.fzjgyzsjnskqmye = fzjgyzsjnskqmye;
	}

	public DZFDouble getJzfwyzjnskqcye() {
		return jzfwyzjnskqcye;
	}

	public void setJzfwyzjnskqcye(DZFDouble jzfwyzjnskqcye) {
		this.jzfwyzjnskqcye = jzfwyzjnskqcye;
	}

	public DZFDouble getJzfwyzjnskbqydjse() {
		return jzfwyzjnskbqydjse;
	}

	public void setJzfwyzjnskbqydjse(DZFDouble jzfwyzjnskbqydjse) {
		this.jzfwyzjnskbqydjse = jzfwyzjnskbqydjse;
	}

	public DZFDouble getJzfwyzjnskbqsjdjse() {
		return jzfwyzjnskbqsjdjse;
	}

	public void setJzfwyzjnskbqsjdjse(DZFDouble jzfwyzjnskbqsjdjse) {
		this.jzfwyzjnskbqsjdjse = jzfwyzjnskbqsjdjse;
	}

	public DZFDouble getJzfwyzjnskbqfse() {
		return jzfwyzjnskbqfse;
	}

	public void setJzfwyzjnskbqfse(DZFDouble jzfwyzjnskbqfse) {
		this.jzfwyzjnskbqfse = jzfwyzjnskbqfse;
	}

	public DZFDouble getJzfwyzjnskqmye() {
		return jzfwyzjnskqmye;
	}

	public void setJzfwyzjnskqmye(DZFDouble jzfwyzjnskqmye) {
		this.jzfwyzjnskqmye = jzfwyzjnskqmye;
	}

	public DZFDouble getXsbdcyzjnskqcye() {
		return xsbdcyzjnskqcye;
	}

	public void setXsbdcyzjnskqcye(DZFDouble xsbdcyzjnskqcye) {
		this.xsbdcyzjnskqcye = xsbdcyzjnskqcye;
	}

	public DZFDouble getXsbdcyzjnskbqydjse() {
		return xsbdcyzjnskbqydjse;
	}

	public void setXsbdcyzjnskbqydjse(DZFDouble xsbdcyzjnskbqydjse) {
		this.xsbdcyzjnskbqydjse = xsbdcyzjnskbqydjse;
	}

	public DZFDouble getXsbdcyzjnskbqsjdjse() {
		return xsbdcyzjnskbqsjdjse;
	}

	public void setXsbdcyzjnskbqsjdjse(DZFDouble xsbdcyzjnskbqsjdjse) {
		this.xsbdcyzjnskbqsjdjse = xsbdcyzjnskbqsjdjse;
	}

	public DZFDouble getXsbdcyzjnskbqfse() {
		return xsbdcyzjnskbqfse;
	}

	public void setXsbdcyzjnskbqfse(DZFDouble xsbdcyzjnskbqfse) {
		this.xsbdcyzjnskbqfse = xsbdcyzjnskbqfse;
	}

	public DZFDouble getXsbdcyzjnskqmye() {
		return xsbdcyzjnskqmye;
	}

	public void setXsbdcyzjnskqmye(DZFDouble xsbdcyzjnskqmye) {
		this.xsbdcyzjnskqmye = xsbdcyzjnskqmye;
	}

	public DZFDouble getCzbdcyzjnskqcye() {
		return czbdcyzjnskqcye;
	}

	public void setCzbdcyzjnskqcye(DZFDouble czbdcyzjnskqcye) {
		this.czbdcyzjnskqcye = czbdcyzjnskqcye;
	}

	public DZFDouble getCzbdcyzjnskbqydjse() {
		return czbdcyzjnskbqydjse;
	}

	public void setCzbdcyzjnskbqydjse(DZFDouble czbdcyzjnskbqydjse) {
		this.czbdcyzjnskbqydjse = czbdcyzjnskbqydjse;
	}

	public DZFDouble getCzbdcyzjnskbqsjdjse() {
		return czbdcyzjnskbqsjdjse;
	}

	public void setCzbdcyzjnskbqsjdjse(DZFDouble czbdcyzjnskbqsjdjse) {
		this.czbdcyzjnskbqsjdjse = czbdcyzjnskbqsjdjse;
	}

	public DZFDouble getCzbdcyzjnskbqfse() {
		return czbdcyzjnskbqfse;
	}

	public void setCzbdcyzjnskbqfse(DZFDouble czbdcyzjnskbqfse) {
		this.czbdcyzjnskbqfse = czbdcyzjnskbqfse;
	}

	public DZFDouble getCzbdcyzjnskqmye() {
		return czbdcyzjnskqmye;
	}

	public void setCzbdcyzjnskqmye(DZFDouble czbdcyzjnskqmye) {
		this.czbdcyzjnskqmye = czbdcyzjnskqmye;
	}

	public DZFDouble getYbxmjjdjeqcye() {
		return ybxmjjdjeqcye;
	}

	public void setYbxmjjdjeqcye(DZFDouble ybxmjjdjeqcye) {
		this.ybxmjjdjeqcye = ybxmjjdjeqcye;
	}

	public DZFDouble getYbxmjjdjebqfse() {
		return ybxmjjdjebqfse;
	}

	public void setYbxmjjdjebqfse(DZFDouble ybxmjjdjebqfse) {
		this.ybxmjjdjebqfse = ybxmjjdjebqfse;
	}

	public DZFDouble getYbxmjjdjebqtje() {
		return ybxmjjdjebqtje;
	}

	public void setYbxmjjdjebqtje(DZFDouble ybxmjjdjebqtje) {
		this.ybxmjjdjebqtje = ybxmjjdjebqtje;
	}

	public DZFDouble getYbxmjjdjebqkdje() {
		return ybxmjjdjebqkdje;
	}

	public void setYbxmjjdjebqkdje(DZFDouble ybxmjjdjebqkdje) {
		this.ybxmjjdjebqkdje = ybxmjjdjebqkdje;
	}

	public DZFDouble getYbxmjjdjebqsjdje() {
		return ybxmjjdjebqsjdje;
	}

	public void setYbxmjjdjebqsjdje(DZFDouble ybxmjjdjebqsjdje) {
		this.ybxmjjdjebqsjdje = ybxmjjdjebqsjdje;
	}

	public DZFDouble getYbxmjjdjeqmye() {
		return ybxmjjdjeqmye;
	}

	public void setYbxmjjdjeqmye(DZFDouble ybxmjjdjeqmye) {
		this.ybxmjjdjeqmye = ybxmjjdjeqmye;
	}

	public DZFDouble getJzjtxmjjdjeqcye() {
		return jzjtxmjjdjeqcye;
	}

	public void setJzjtxmjjdjeqcye(DZFDouble jzjtxmjjdjeqcye) {
		this.jzjtxmjjdjeqcye = jzjtxmjjdjeqcye;
	}

	public DZFDouble getJzjtxmjjdjebqfse() {
		return jzjtxmjjdjebqfse;
	}

	public void setJzjtxmjjdjebqfse(DZFDouble jzjtxmjjdjebqfse) {
		this.jzjtxmjjdjebqfse = jzjtxmjjdjebqfse;
	}

	public DZFDouble getJzjtxmjjdjebqtje() {
		return jzjtxmjjdjebqtje;
	}

	public void setJzjtxmjjdjebqtje(DZFDouble jzjtxmjjdjebqtje) {
		this.jzjtxmjjdjebqtje = jzjtxmjjdjebqtje;
	}

	public DZFDouble getJzjtxmjjdjebqkdje() {
		return jzjtxmjjdjebqkdje;
	}

	public void setJzjtxmjjdjebqkdje(DZFDouble jzjtxmjjdjebqkdje) {
		this.jzjtxmjjdjebqkdje = jzjtxmjjdjebqkdje;
	}

	public DZFDouble getJzjtxmjjdjebqsjdje() {
		return jzjtxmjjdjebqsjdje;
	}

	public void setJzjtxmjjdjebqsjdje(DZFDouble jzjtxmjjdjebqsjdje) {
		this.jzjtxmjjdjebqsjdje = jzjtxmjjdjebqsjdje;
	}

	public DZFDouble getJzjtxmjjdjeqmye() {
		return jzjtxmjjdjeqmye;
	}

	public void setJzjtxmjjdjeqmye(DZFDouble jzjtxmjjdjeqmye) {
		this.jzjtxmjjdjeqmye = jzjtxmjjdjeqmye;
	}

	public DZFDouble getJjdjxmqcyehj() {
		return jjdjxmqcyehj;
	}

	public void setJjdjxmqcyehj(DZFDouble jjdjxmqcyehj) {
		this.jjdjxmqcyehj = jjdjxmqcyehj;
	}

	public DZFDouble getJjdjxmbqfsehj() {
		return jjdjxmbqfsehj;
	}

	public void setJjdjxmbqfsehj(DZFDouble jjdjxmbqfsehj) {
		this.jjdjxmbqfsehj = jjdjxmbqfsehj;
	}

	public DZFDouble getJjdjxmbqtjehj() {
		return jjdjxmbqtjehj;
	}

	public void setJjdjxmbqtjehj(DZFDouble jjdjxmbqtjehj) {
		this.jjdjxmbqtjehj = jjdjxmbqtjehj;
	}

	public DZFDouble getJjdjxmbqkdjehj() {
		return jjdjxmbqkdjehj;
	}

	public void setJjdjxmbqkdjehj(DZFDouble jjdjxmbqkdjehj) {
		this.jjdjxmbqkdjehj = jjdjxmbqkdjehj;
	}

	public DZFDouble getJjdjxmbqsjdjehj() {
		return jjdjxmbqsjdjehj;
	}

	public void setJjdjxmbqsjdjehj(DZFDouble jjdjxmbqsjdjehj) {
		this.jjdjxmbqsjdjehj = jjdjxmbqsjdjehj;
	}

	public DZFDouble getJjdjxmqmtehj() {
		return jjdjxmqmtehj;
	}

	public void setJjdjxmqmtehj(DZFDouble jjdjxmqmtehj) {
		this.jjdjxmqmtehj = jjdjxmqmtehj;
	}
}
