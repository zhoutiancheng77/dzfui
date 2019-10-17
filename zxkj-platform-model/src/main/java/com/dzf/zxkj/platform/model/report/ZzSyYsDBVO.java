package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 增值税和营业税月度申报对比表
 * @author JasonLiu
 *
 */
public class ZzSyYsDBVO extends SuperVO {

	//单位		
	private String dw ;
	
	//税务代码
	private String swdm ;
	
	//税率
	private DZFDouble sl ;
	
	private DZFDouble zysr ;
	
	private DZFDouble whsr ;
	
	private DZFDouble lwsr ;
	
	private DZFDouble qtywsr ;
	
	private DZFDouble yysjjfj ;
	
	private DZFDouble zs ;
	
	private DZFDouble fj ;
	
	private DZFDouble hj ;
	
	private String bz ;
	
	private DZFDouble mny;//行转列使用
	
	private DZFDouble bnljmny;//本年累计金额
	
	private String xm;//项目
	
	private String hc ;//行次
	
	private DZFDouble insum;//收入合计
	
	private DZFDouble zztax;//增值税
	
	private DZFDouble busitax;//营业税
	
	private DZFDouble spendtax;//消费税
	
	private DZFDouble csmaintax;//城市维护建设税
	
	private DZFDouble studytax;//教育费附加
	
	private DZFDouble partstudytax;//地方教育费附加
	
	private DZFDouble taxsum;//税金合计
	
	private String period;//期间

	//打印时  标题显示的区间区间
	private String titlePeriod;
	
	private String gs; // 公司
	
	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public DZFDouble getFj() {
		return fj;
	}

	public void setFj(DZFDouble fj) {
		this.fj = fj;
	}

	public DZFDouble getHj() {
		return hj;
	}

	public void setHj(DZFDouble hj) {
		this.hj = hj;
	}

	public DZFDouble getLwsr() {
		return lwsr;
	}

	public void setLwsr(DZFDouble lwsr) {
		this.lwsr = lwsr;
	}

	public DZFDouble getQtywsr() {
		return qtywsr;
	}

	public void setQtywsr(DZFDouble qtywsr) {
		this.qtywsr = qtywsr;
	}

	public DZFDouble getSl() {
		return sl;
	}

	public void setSl(DZFDouble sl) {
		this.sl = sl;
	}

	public String getSwdm() {
		return swdm;
	}

	public void setSwdm(String swdm) {
		this.swdm = swdm;
	}

	public DZFDouble getWhsr() {
		return whsr;
	}

	public void setWhsr(DZFDouble whsr) {
		this.whsr = whsr;
	}

	public DZFDouble getYysjjfj() {
		return yysjjfj;
	}

	public void setYysjjfj(DZFDouble yysjjfj) {
		this.yysjjfj = yysjjfj;
	}

	public DZFDouble getZs() {
		return zs;
	}

	public void setZs(DZFDouble zs) {
		this.zs = zs;
	}

	public DZFDouble getZysr() {
		return zysr;
	}

	public void setZysr(DZFDouble zysr) {
		this.zysr = zysr;
	}

	public DZFDouble getInsum() {
		return insum;
	}

	public void setInsum(DZFDouble insum) {
		this.insum = insum;
	}

	public DZFDouble getZztax() {
		return zztax;
	}

	public void setZztax(DZFDouble zztax) {
		this.zztax = zztax;
	}

	public DZFDouble getBusitax() {
		return busitax;
	}

	public void setBusitax(DZFDouble busitax) {
		this.busitax = busitax;
	}

	public DZFDouble getSpendtax() {
		return spendtax;
	}

	public void setSpendtax(DZFDouble spendtax) {
		this.spendtax = spendtax;
	}

	public DZFDouble getCsmaintax() {
		return csmaintax;
	}

	public void setCsmaintax(DZFDouble csmaintax) {
		this.csmaintax = csmaintax;
	}

	public DZFDouble getStudytax() {
		return studytax;
	}

	public void setStudytax(DZFDouble studytax) {
		this.studytax = studytax;
	}

	public DZFDouble getPartstudytax() {
		return partstudytax;
	}

	public void setPartstudytax(DZFDouble partstudytax) {
		this.partstudytax = partstudytax;
	}

	public DZFDouble getTaxsum() {
		return taxsum;
	}

	public void setTaxsum(DZFDouble taxsum) {
		this.taxsum = taxsum;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}
	
	
	
}
