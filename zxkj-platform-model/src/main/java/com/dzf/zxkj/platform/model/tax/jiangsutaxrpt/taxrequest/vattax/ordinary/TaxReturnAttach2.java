package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表附列资料（二）（本期进项税额明细）  sb10101003vo_01
@TaxExcelPos(reportID = "10101003", reportname = "增值税纳税申报表附列资料（二）")
public class TaxReturnAttach2 {
	// 一、申报抵扣的进项税额

	// （一）认证相符的增值税专用发票
	// 份数
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble rzxfdskzzszyfpsbdkfs;
	// 金额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble rzxfdskzzszyfpsbdkje;
	// 税额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble rzxfdskzzszyfpsbdkse;

	// 其中：本期认证相符且本期申报抵扣
	// 份数
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble bqrzxfqbqsbdkfs;
	// 金额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble bqrzxfqbqsbdkje;
	// 税额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble bqrzxfqbqsbdkse;

	// 前期认证相符且本期申报抵扣
	// 份数
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble qqrzxfqbqsbdkfs;
	// 金额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble qqrzxfqbqsbdkje;
	// 税额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble qqrzxfqbqsbdkse;

	// （二）其他扣税凭证
	// 份数
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble qtkspzfs;
	// 金额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble qtkspzje;
	// 税额
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble qtkspzse;

	// 其中：海关进口增值税专用缴款书
	// 份数
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble hgjkzzszyjksfs;
	// 金额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble hgjkzzszyjksje;
	// 税额
	@TaxExcelPos(row = 11, col = 4)
	private DZFDouble hgjkzzszyjksse;

	// 农产品收购发票或者销售发票
	// 份数
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble ncpsgfphzxsfpfs;
	// 金额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble ncpsgfphzxsfpje;
	// 税额
	@TaxExcelPos(row = 12, col = 4)
	private DZFDouble ncpsgfphzxsfpsbdkse;

	// 代扣代缴税收缴款凭证
	// 份数
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble dkdjssjkpzfs;
	// 税额
	@TaxExcelPos(row = 13, col = 4)
	private DZFDouble dkdjssjkpzse;

	// 加计扣除农产品进项税额 8a
	// 税额
	@TaxExcelPos(row = 14, col = 4)
	private DZFDouble jjkcncpjxse;
	// 其他 8b
	// 份数
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble ysfyjsdjfs;
	// 金额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble ysfyjsdjje;
	// 税额
	@TaxExcelPos(row = 15, col = 4)
	private DZFDouble ysfyjsdjsbdkse;

	// （三）本期用于购建不动产的扣税凭证
	// 份数
	@TaxExcelPos(row = 16, col = 2)
	private DZFDouble bqgjbdckspzfs;
	// 金额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble bqgjbdckspzje;
	// 税额
	@TaxExcelPos(row = 16, col = 4)
	private DZFDouble bqgjbdckspzse;

	// （四）本期不动产允许抵扣进项税额
	// 份数
	@TaxExcelPos(row = 17, col = 2)
	private DZFDouble bqbdcyxdkjxsefs;
	// 金额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble bqbdcyxdkjxseje;
	// 税额
	@TaxExcelPos(row = 17, col = 4)
	private DZFDouble bqbdcyxdkjxsese;

	// （五）外贸企业进项税额抵扣证明
	// 税额
	@TaxExcelPos(row = 18, col = 4)
	private DZFDouble wmqyjxsdkzmse;

	// 当期申报抵扣进项税额合计
	// 份数
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble dqsbdkjxsehjfs;
	// 金额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble dqsbdkjxsehjje;
	// 税额
	@TaxExcelPos(row = 19, col = 4)
	private DZFDouble dqsbdkjxsehjse;

	// 二、进项税额转出额

	// 本期进项税额转出额
	// 税额
	@TaxExcelPos(row = 22, col = 2)
	private DZFDouble bqjxszcese;

	// 其中：免税项目用
	// 税额
	@TaxExcelPos(row = 23, col = 2)
	private DZFDouble msxmyse;

	// 集体福利、个人消费
	// 税额
	@TaxExcelPos(row = 24, col = 2)
	private DZFDouble fysxmyjtflgrxfse;

	// 非正常损失
	// 税额
	@TaxExcelPos(row = 25, col = 2)
	private DZFDouble fzcssse;

	// 简易计税方法征税项目用
	// 税额
	@TaxExcelPos(row = 26, col = 2)
	private DZFDouble jyjsbfzsxmyse;

	// 免抵退税办法不得抵扣的进项税额
	// 税额
	@TaxExcelPos(row = 27, col = 2)
	private DZFDouble mdtsbfbddkdjxsese;

	// 纳税检查调减进项税额
	// 税额
	@TaxExcelPos(row = 28, col = 2)
	private DZFDouble nsjctjjxsese;

	// 红字专用发票信息表注明的进项税额
	// 税额
	@TaxExcelPos(row = 29, col = 2)
	private DZFDouble hzzyfptzdzmdjxsese;

	// 上期留抵税额抵减欠税
	// 税额
	@TaxExcelPos(row = 30, col = 2)
	private DZFDouble sqldsedjqsse;

	// 上期留抵税额退税
	// 税额
	@TaxExcelPos(row = 31, col = 2)
	private DZFDouble sqldsetsse;

	// 其他应作进项税额转出的情形
	// 税额
	@TaxExcelPos(row = 32, col = 2)
	private DZFDouble qtyzjxsezcdqxse;

	// 三、待抵扣进项税额

	// 期初已认证相符但未申报抵扣
	// 份数
	@TaxExcelPos(row = 36, col = 2)
	private DZFDouble qcyrzxfqbqwsbdkfs;
	// 金额
	@TaxExcelPos(row = 36, col = 3)
	private DZFDouble qcyrzxfqbqwsbdkje;
	// 税额
	@TaxExcelPos(row = 36, col = 4)
	private DZFDouble qcyrzxfqbqwsbdkse;

	// 本期认证相符且本期未申报抵扣
	// 份数
	@TaxExcelPos(row = 37, col = 2)
	private DZFDouble bqrzxfqbqwsbdkfs;
	// 金额
	@TaxExcelPos(row = 37, col = 3)
	private DZFDouble bqrzxfqbqwsbdkje;
	// 税额
	@TaxExcelPos(row = 37, col = 4)
	private DZFDouble bqrzxfqbqwsbdkse;

	// 期末已认证相符但未申报抵扣
	// 份数
	@TaxExcelPos(row = 38, col = 2)
	private DZFDouble qmyrzxfdwsbdkfs;
	// 金额
	@TaxExcelPos(row = 38, col = 3)
	private DZFDouble qmyrzxfdwsbdkje;
	// 税额
	@TaxExcelPos(row = 38, col = 4)
	private DZFDouble qmyrzxfdwsbdkse;

	// 其中：按照税法规定不允许抵扣
	// 份数
	@TaxExcelPos(row = 39, col = 2)
	private DZFDouble azsfgdbyxdkfs;
	// 金额
	@TaxExcelPos(row = 39, col = 3)
	private DZFDouble azsfgdbyxdkje;
	// 税额
	@TaxExcelPos(row = 39, col = 4)
	private DZFDouble azsfgdbyxdkse;

	// （二）其他扣税凭证
	// 份数
	@TaxExcelPos(row = 40, col = 2)
	private DZFDouble qtkspzddkjxsefs;
	// 金额
	@TaxExcelPos(row = 40, col = 3)
	private DZFDouble qtkspzddkjxseje;
	// 税额
	@TaxExcelPos(row = 40, col = 4)
	private DZFDouble qtkspzddkjxsese;

	// 其中：海关进口增值税专用缴款书
	// 份数
	@TaxExcelPos(row = 41, col = 2)
	private DZFDouble hgjkzzszyxsfpfs;
	// 金额
	@TaxExcelPos(row = 41, col = 3)
	private DZFDouble hgjkzzszyxsfpje;
	// 税额
	@TaxExcelPos(row = 41, col = 4)
	private DZFDouble hgjkzzszyxsfpse;

	// 农产品收购发票或者销售发票
	// 份数
	@TaxExcelPos(row = 42, col = 2)
	private DZFDouble ncpsgfphzxsfpddkfs;
	// 金额
	@TaxExcelPos(row = 42, col = 3)
	private DZFDouble ncpsgfphzxsfpddkje;
	// 税额
	@TaxExcelPos(row = 42, col = 4)
	private DZFDouble ncpsgfphzxsfpse;

	// 代扣代缴税收缴款凭证
	// 份数
	@TaxExcelPos(row = 43, col = 2)
	private DZFDouble dkdjshjkpzfs;
	// 金额
	@TaxExcelPos(row = 43, col = 3)
	private DZFDouble dkdjshjkpzje;
	// 税额
	@TaxExcelPos(row = 43, col = 4)
	private DZFDouble dkdjshjkpzse;

	// 其他
	// 份数
	@TaxExcelPos(row = 44, col = 2)
	private DZFDouble ysfyjsdjddkfs;
	// 金额
	@TaxExcelPos(row = 44, col = 3)
	private DZFDouble ysfyjsdjddkje;
	// 税额
	@TaxExcelPos(row = 44, col = 4)
	private DZFDouble ysfyjsdjse;

	// 四、其他

	// 本期认证相符的增值税专用发票
	// 份数
	@TaxExcelPos(row = 48, col = 2)
	private DZFDouble bqrzxfdskzzszyfpfs;
	// 金额
	@TaxExcelPos(row = 48, col = 3)
	private DZFDouble bqrzxfdskzzszyfpje;
	// 税额
	@TaxExcelPos(row = 48, col = 4)
	private DZFDouble bqrzxfdskzzszyfpse;

	// 代扣代缴税额
	// 税额
	@TaxExcelPos(row = 49, col = 4)
	private DZFDouble dkdjsese;

	public DZFDouble getRzxfdskzzszyfpsbdkfs() {
		return rzxfdskzzszyfpsbdkfs;
	}

	public void setRzxfdskzzszyfpsbdkfs(DZFDouble rzxfdskzzszyfpsbdkfs) {
		this.rzxfdskzzszyfpsbdkfs = rzxfdskzzszyfpsbdkfs;
	}

	public DZFDouble getRzxfdskzzszyfpsbdkje() {
		return rzxfdskzzszyfpsbdkje;
	}

	public void setRzxfdskzzszyfpsbdkje(DZFDouble rzxfdskzzszyfpsbdkje) {
		this.rzxfdskzzszyfpsbdkje = rzxfdskzzszyfpsbdkje;
	}

	public DZFDouble getRzxfdskzzszyfpsbdkse() {
		return rzxfdskzzszyfpsbdkse;
	}

	public void setRzxfdskzzszyfpsbdkse(DZFDouble rzxfdskzzszyfpsbdkse) {
		this.rzxfdskzzszyfpsbdkse = rzxfdskzzszyfpsbdkse;
	}

	public DZFDouble getBqrzxfqbqsbdkfs() {
		return bqrzxfqbqsbdkfs;
	}

	public void setBqrzxfqbqsbdkfs(DZFDouble bqrzxfqbqsbdkfs) {
		this.bqrzxfqbqsbdkfs = bqrzxfqbqsbdkfs;
	}

	public DZFDouble getBqrzxfqbqsbdkje() {
		return bqrzxfqbqsbdkje;
	}

	public void setBqrzxfqbqsbdkje(DZFDouble bqrzxfqbqsbdkje) {
		this.bqrzxfqbqsbdkje = bqrzxfqbqsbdkje;
	}

	public DZFDouble getBqrzxfqbqsbdkse() {
		return bqrzxfqbqsbdkse;
	}

	public void setBqrzxfqbqsbdkse(DZFDouble bqrzxfqbqsbdkse) {
		this.bqrzxfqbqsbdkse = bqrzxfqbqsbdkse;
	}

	public DZFDouble getQqrzxfqbqsbdkfs() {
		return qqrzxfqbqsbdkfs;
	}

	public void setQqrzxfqbqsbdkfs(DZFDouble qqrzxfqbqsbdkfs) {
		this.qqrzxfqbqsbdkfs = qqrzxfqbqsbdkfs;
	}

	public DZFDouble getQqrzxfqbqsbdkje() {
		return qqrzxfqbqsbdkje;
	}

	public void setQqrzxfqbqsbdkje(DZFDouble qqrzxfqbqsbdkje) {
		this.qqrzxfqbqsbdkje = qqrzxfqbqsbdkje;
	}

	public DZFDouble getQqrzxfqbqsbdkse() {
		return qqrzxfqbqsbdkse;
	}

	public void setQqrzxfqbqsbdkse(DZFDouble qqrzxfqbqsbdkse) {
		this.qqrzxfqbqsbdkse = qqrzxfqbqsbdkse;
	}

	public DZFDouble getQtkspzfs() {
		return qtkspzfs;
	}

	public void setQtkspzfs(DZFDouble qtkspzfs) {
		this.qtkspzfs = qtkspzfs;
	}

	public DZFDouble getQtkspzje() {
		return qtkspzje;
	}

	public void setQtkspzje(DZFDouble qtkspzje) {
		this.qtkspzje = qtkspzje;
	}

	public DZFDouble getQtkspzse() {
		return qtkspzse;
	}

	public void setQtkspzse(DZFDouble qtkspzse) {
		this.qtkspzse = qtkspzse;
	}

	public DZFDouble getHgjkzzszyjksfs() {
		return hgjkzzszyjksfs;
	}

	public void setHgjkzzszyjksfs(DZFDouble hgjkzzszyjksfs) {
		this.hgjkzzszyjksfs = hgjkzzszyjksfs;
	}

	public DZFDouble getHgjkzzszyjksje() {
		return hgjkzzszyjksje;
	}

	public void setHgjkzzszyjksje(DZFDouble hgjkzzszyjksje) {
		this.hgjkzzszyjksje = hgjkzzszyjksje;
	}

	public DZFDouble getHgjkzzszyjksse() {
		return hgjkzzszyjksse;
	}

	public void setHgjkzzszyjksse(DZFDouble hgjkzzszyjksse) {
		this.hgjkzzszyjksse = hgjkzzszyjksse;
	}

	public DZFDouble getNcpsgfphzxsfpfs() {
		return ncpsgfphzxsfpfs;
	}

	public void setNcpsgfphzxsfpfs(DZFDouble ncpsgfphzxsfpfs) {
		this.ncpsgfphzxsfpfs = ncpsgfphzxsfpfs;
	}

	public DZFDouble getNcpsgfphzxsfpje() {
		return ncpsgfphzxsfpje;
	}

	public void setNcpsgfphzxsfpje(DZFDouble ncpsgfphzxsfpje) {
		this.ncpsgfphzxsfpje = ncpsgfphzxsfpje;
	}

	public DZFDouble getNcpsgfphzxsfpsbdkse() {
		return ncpsgfphzxsfpsbdkse;
	}

	public void setNcpsgfphzxsfpsbdkse(DZFDouble ncpsgfphzxsfpsbdkse) {
		this.ncpsgfphzxsfpsbdkse = ncpsgfphzxsfpsbdkse;
	}

	public DZFDouble getDkdjssjkpzfs() {
		return dkdjssjkpzfs;
	}

	public void setDkdjssjkpzfs(DZFDouble dkdjssjkpzfs) {
		this.dkdjssjkpzfs = dkdjssjkpzfs;
	}

	public DZFDouble getDkdjssjkpzse() {
		return dkdjssjkpzse;
	}

	public void setDkdjssjkpzse(DZFDouble dkdjssjkpzse) {
		this.dkdjssjkpzse = dkdjssjkpzse;
	}

	public DZFDouble getJjkcncpjxse() {
		return jjkcncpjxse;
	}

	public void setJjkcncpjxse(DZFDouble jjkcncpjxse) {
		this.jjkcncpjxse = jjkcncpjxse;
	}

	public DZFDouble getYsfyjsdjfs() {
		return ysfyjsdjfs;
	}

	public void setYsfyjsdjfs(DZFDouble ysfyjsdjfs) {
		this.ysfyjsdjfs = ysfyjsdjfs;
	}

	public DZFDouble getYsfyjsdjje() {
		return ysfyjsdjje;
	}

	public void setYsfyjsdjje(DZFDouble ysfyjsdjje) {
		this.ysfyjsdjje = ysfyjsdjje;
	}

	public DZFDouble getYsfyjsdjsbdkse() {
		return ysfyjsdjsbdkse;
	}

	public void setYsfyjsdjsbdkse(DZFDouble ysfyjsdjsbdkse) {
		this.ysfyjsdjsbdkse = ysfyjsdjsbdkse;
	}

	public DZFDouble getBqgjbdckspzfs() {
		return bqgjbdckspzfs;
	}

	public void setBqgjbdckspzfs(DZFDouble bqgjbdckspzfs) {
		this.bqgjbdckspzfs = bqgjbdckspzfs;
	}

	public DZFDouble getBqgjbdckspzje() {
		return bqgjbdckspzje;
	}

	public void setBqgjbdckspzje(DZFDouble bqgjbdckspzje) {
		this.bqgjbdckspzje = bqgjbdckspzje;
	}

	public DZFDouble getBqgjbdckspzse() {
		return bqgjbdckspzse;
	}

	public void setBqgjbdckspzse(DZFDouble bqgjbdckspzse) {
		this.bqgjbdckspzse = bqgjbdckspzse;
	}

	public DZFDouble getBqbdcyxdkjxsefs() {
		return bqbdcyxdkjxsefs;
	}

	public void setBqbdcyxdkjxsefs(DZFDouble bqbdcyxdkjxsefs) {
		this.bqbdcyxdkjxsefs = bqbdcyxdkjxsefs;
	}

	public DZFDouble getBqbdcyxdkjxseje() {
		return bqbdcyxdkjxseje;
	}

	public void setBqbdcyxdkjxseje(DZFDouble bqbdcyxdkjxseje) {
		this.bqbdcyxdkjxseje = bqbdcyxdkjxseje;
	}

	public DZFDouble getBqbdcyxdkjxsese() {
		return bqbdcyxdkjxsese;
	}

	public void setBqbdcyxdkjxsese(DZFDouble bqbdcyxdkjxsese) {
		this.bqbdcyxdkjxsese = bqbdcyxdkjxsese;
	}

	public DZFDouble getWmqyjxsdkzmse() {
		return wmqyjxsdkzmse;
	}

	public void setWmqyjxsdkzmse(DZFDouble wmqyjxsdkzmse) {
		this.wmqyjxsdkzmse = wmqyjxsdkzmse;
	}

	public DZFDouble getDqsbdkjxsehjfs() {
		return dqsbdkjxsehjfs;
	}

	public void setDqsbdkjxsehjfs(DZFDouble dqsbdkjxsehjfs) {
		this.dqsbdkjxsehjfs = dqsbdkjxsehjfs;
	}

	public DZFDouble getDqsbdkjxsehjje() {
		return dqsbdkjxsehjje;
	}

	public void setDqsbdkjxsehjje(DZFDouble dqsbdkjxsehjje) {
		this.dqsbdkjxsehjje = dqsbdkjxsehjje;
	}

	public DZFDouble getDqsbdkjxsehjse() {
		return dqsbdkjxsehjse;
	}

	public void setDqsbdkjxsehjse(DZFDouble dqsbdkjxsehjse) {
		this.dqsbdkjxsehjse = dqsbdkjxsehjse;
	}

	public DZFDouble getBqjxszcese() {
		return bqjxszcese;
	}

	public void setBqjxszcese(DZFDouble bqjxszcese) {
		this.bqjxszcese = bqjxszcese;
	}

	public DZFDouble getMsxmyse() {
		return msxmyse;
	}

	public void setMsxmyse(DZFDouble msxmyse) {
		this.msxmyse = msxmyse;
	}

	public DZFDouble getFysxmyjtflgrxfse() {
		return fysxmyjtflgrxfse;
	}

	public void setFysxmyjtflgrxfse(DZFDouble fysxmyjtflgrxfse) {
		this.fysxmyjtflgrxfse = fysxmyjtflgrxfse;
	}

	public DZFDouble getFzcssse() {
		return fzcssse;
	}

	public void setFzcssse(DZFDouble fzcssse) {
		this.fzcssse = fzcssse;
	}

	public DZFDouble getJyjsbfzsxmyse() {
		return jyjsbfzsxmyse;
	}

	public void setJyjsbfzsxmyse(DZFDouble jyjsbfzsxmyse) {
		this.jyjsbfzsxmyse = jyjsbfzsxmyse;
	}

	public DZFDouble getMdtsbfbddkdjxsese() {
		return mdtsbfbddkdjxsese;
	}

	public void setMdtsbfbddkdjxsese(DZFDouble mdtsbfbddkdjxsese) {
		this.mdtsbfbddkdjxsese = mdtsbfbddkdjxsese;
	}

	public DZFDouble getNsjctjjxsese() {
		return nsjctjjxsese;
	}

	public void setNsjctjjxsese(DZFDouble nsjctjjxsese) {
		this.nsjctjjxsese = nsjctjjxsese;
	}

	public DZFDouble getHzzyfptzdzmdjxsese() {
		return hzzyfptzdzmdjxsese;
	}

	public void setHzzyfptzdzmdjxsese(DZFDouble hzzyfptzdzmdjxsese) {
		this.hzzyfptzdzmdjxsese = hzzyfptzdzmdjxsese;
	}

	public DZFDouble getSqldsedjqsse() {
		return sqldsedjqsse;
	}

	public void setSqldsedjqsse(DZFDouble sqldsedjqsse) {
		this.sqldsedjqsse = sqldsedjqsse;
	}

	public DZFDouble getSqldsetsse() {
		return sqldsetsse;
	}

	public void setSqldsetsse(DZFDouble sqldsetsse) {
		this.sqldsetsse = sqldsetsse;
	}

	public DZFDouble getQtyzjxsezcdqxse() {
		return qtyzjxsezcdqxse;
	}

	public void setQtyzjxsezcdqxse(DZFDouble qtyzjxsezcdqxse) {
		this.qtyzjxsezcdqxse = qtyzjxsezcdqxse;
	}

	public DZFDouble getQcyrzxfqbqwsbdkfs() {
		return qcyrzxfqbqwsbdkfs;
	}

	public void setQcyrzxfqbqwsbdkfs(DZFDouble qcyrzxfqbqwsbdkfs) {
		this.qcyrzxfqbqwsbdkfs = qcyrzxfqbqwsbdkfs;
	}

	public DZFDouble getQcyrzxfqbqwsbdkje() {
		return qcyrzxfqbqwsbdkje;
	}

	public void setQcyrzxfqbqwsbdkje(DZFDouble qcyrzxfqbqwsbdkje) {
		this.qcyrzxfqbqwsbdkje = qcyrzxfqbqwsbdkje;
	}

	public DZFDouble getQcyrzxfqbqwsbdkse() {
		return qcyrzxfqbqwsbdkse;
	}

	public void setQcyrzxfqbqwsbdkse(DZFDouble qcyrzxfqbqwsbdkse) {
		this.qcyrzxfqbqwsbdkse = qcyrzxfqbqwsbdkse;
	}

	public DZFDouble getBqrzxfqbqwsbdkfs() {
		return bqrzxfqbqwsbdkfs;
	}

	public void setBqrzxfqbqwsbdkfs(DZFDouble bqrzxfqbqwsbdkfs) {
		this.bqrzxfqbqwsbdkfs = bqrzxfqbqwsbdkfs;
	}

	public DZFDouble getBqrzxfqbqwsbdkje() {
		return bqrzxfqbqwsbdkje;
	}

	public void setBqrzxfqbqwsbdkje(DZFDouble bqrzxfqbqwsbdkje) {
		this.bqrzxfqbqwsbdkje = bqrzxfqbqwsbdkje;
	}

	public DZFDouble getBqrzxfqbqwsbdkse() {
		return bqrzxfqbqwsbdkse;
	}

	public void setBqrzxfqbqwsbdkse(DZFDouble bqrzxfqbqwsbdkse) {
		this.bqrzxfqbqwsbdkse = bqrzxfqbqwsbdkse;
	}

	public DZFDouble getQmyrzxfdwsbdkfs() {
		return qmyrzxfdwsbdkfs;
	}

	public void setQmyrzxfdwsbdkfs(DZFDouble qmyrzxfdwsbdkfs) {
		this.qmyrzxfdwsbdkfs = qmyrzxfdwsbdkfs;
	}

	public DZFDouble getQmyrzxfdwsbdkje() {
		return qmyrzxfdwsbdkje;
	}

	public void setQmyrzxfdwsbdkje(DZFDouble qmyrzxfdwsbdkje) {
		this.qmyrzxfdwsbdkje = qmyrzxfdwsbdkje;
	}

	public DZFDouble getQmyrzxfdwsbdkse() {
		return qmyrzxfdwsbdkse;
	}

	public void setQmyrzxfdwsbdkse(DZFDouble qmyrzxfdwsbdkse) {
		this.qmyrzxfdwsbdkse = qmyrzxfdwsbdkse;
	}

	public DZFDouble getAzsfgdbyxdkfs() {
		return azsfgdbyxdkfs;
	}

	public void setAzsfgdbyxdkfs(DZFDouble azsfgdbyxdkfs) {
		this.azsfgdbyxdkfs = azsfgdbyxdkfs;
	}

	public DZFDouble getAzsfgdbyxdkje() {
		return azsfgdbyxdkje;
	}

	public void setAzsfgdbyxdkje(DZFDouble azsfgdbyxdkje) {
		this.azsfgdbyxdkje = azsfgdbyxdkje;
	}

	public DZFDouble getAzsfgdbyxdkse() {
		return azsfgdbyxdkse;
	}

	public void setAzsfgdbyxdkse(DZFDouble azsfgdbyxdkse) {
		this.azsfgdbyxdkse = azsfgdbyxdkse;
	}

	public DZFDouble getQtkspzddkjxsefs() {
		return qtkspzddkjxsefs;
	}

	public void setQtkspzddkjxsefs(DZFDouble qtkspzddkjxsefs) {
		this.qtkspzddkjxsefs = qtkspzddkjxsefs;
	}

	public DZFDouble getQtkspzddkjxseje() {
		return qtkspzddkjxseje;
	}

	public void setQtkspzddkjxseje(DZFDouble qtkspzddkjxseje) {
		this.qtkspzddkjxseje = qtkspzddkjxseje;
	}

	public DZFDouble getQtkspzddkjxsese() {
		return qtkspzddkjxsese;
	}

	public void setQtkspzddkjxsese(DZFDouble qtkspzddkjxsese) {
		this.qtkspzddkjxsese = qtkspzddkjxsese;
	}

	public DZFDouble getHgjkzzszyxsfpfs() {
		return hgjkzzszyxsfpfs;
	}

	public void setHgjkzzszyxsfpfs(DZFDouble hgjkzzszyxsfpfs) {
		this.hgjkzzszyxsfpfs = hgjkzzszyxsfpfs;
	}

	public DZFDouble getHgjkzzszyxsfpje() {
		return hgjkzzszyxsfpje;
	}

	public void setHgjkzzszyxsfpje(DZFDouble hgjkzzszyxsfpje) {
		this.hgjkzzszyxsfpje = hgjkzzszyxsfpje;
	}

	public DZFDouble getHgjkzzszyxsfpse() {
		return hgjkzzszyxsfpse;
	}

	public void setHgjkzzszyxsfpse(DZFDouble hgjkzzszyxsfpse) {
		this.hgjkzzszyxsfpse = hgjkzzszyxsfpse;
	}

	public DZFDouble getNcpsgfphzxsfpddkfs() {
		return ncpsgfphzxsfpddkfs;
	}

	public void setNcpsgfphzxsfpddkfs(DZFDouble ncpsgfphzxsfpddkfs) {
		this.ncpsgfphzxsfpddkfs = ncpsgfphzxsfpddkfs;
	}

	public DZFDouble getNcpsgfphzxsfpddkje() {
		return ncpsgfphzxsfpddkje;
	}

	public void setNcpsgfphzxsfpddkje(DZFDouble ncpsgfphzxsfpddkje) {
		this.ncpsgfphzxsfpddkje = ncpsgfphzxsfpddkje;
	}

	public DZFDouble getNcpsgfphzxsfpse() {
		return ncpsgfphzxsfpse;
	}

	public void setNcpsgfphzxsfpse(DZFDouble ncpsgfphzxsfpse) {
		this.ncpsgfphzxsfpse = ncpsgfphzxsfpse;
	}

	public DZFDouble getDkdjshjkpzfs() {
		return dkdjshjkpzfs;
	}

	public void setDkdjshjkpzfs(DZFDouble dkdjshjkpzfs) {
		this.dkdjshjkpzfs = dkdjshjkpzfs;
	}

	public DZFDouble getDkdjshjkpzje() {
		return dkdjshjkpzje;
	}

	public void setDkdjshjkpzje(DZFDouble dkdjshjkpzje) {
		this.dkdjshjkpzje = dkdjshjkpzje;
	}

	public DZFDouble getDkdjshjkpzse() {
		return dkdjshjkpzse;
	}

	public void setDkdjshjkpzse(DZFDouble dkdjshjkpzse) {
		this.dkdjshjkpzse = dkdjshjkpzse;
	}

	public DZFDouble getYsfyjsdjddkfs() {
		return ysfyjsdjddkfs;
	}

	public void setYsfyjsdjddkfs(DZFDouble ysfyjsdjddkfs) {
		this.ysfyjsdjddkfs = ysfyjsdjddkfs;
	}

	public DZFDouble getYsfyjsdjddkje() {
		return ysfyjsdjddkje;
	}

	public void setYsfyjsdjddkje(DZFDouble ysfyjsdjddkje) {
		this.ysfyjsdjddkje = ysfyjsdjddkje;
	}

	public DZFDouble getYsfyjsdjse() {
		return ysfyjsdjse;
	}

	public void setYsfyjsdjse(DZFDouble ysfyjsdjse) {
		this.ysfyjsdjse = ysfyjsdjse;
	}

	public DZFDouble getBqrzxfdskzzszyfpfs() {
		return bqrzxfdskzzszyfpfs;
	}

	public void setBqrzxfdskzzszyfpfs(DZFDouble bqrzxfdskzzszyfpfs) {
		this.bqrzxfdskzzszyfpfs = bqrzxfdskzzszyfpfs;
	}

	public DZFDouble getBqrzxfdskzzszyfpje() {
		return bqrzxfdskzzszyfpje;
	}

	public void setBqrzxfdskzzszyfpje(DZFDouble bqrzxfdskzzszyfpje) {
		this.bqrzxfdskzzszyfpje = bqrzxfdskzzszyfpje;
	}

	public DZFDouble getBqrzxfdskzzszyfpse() {
		return bqrzxfdskzzszyfpse;
	}

	public void setBqrzxfdskzzszyfpse(DZFDouble bqrzxfdskzzszyfpse) {
		this.bqrzxfdskzzszyfpse = bqrzxfdskzzszyfpse;
	}

	public DZFDouble getDkdjsese() {
		return dkdjsese;
	}

	public void setDkdjsese(DZFDouble dkdjsese) {
		this.dkdjsese = dkdjsese;
	}

}
