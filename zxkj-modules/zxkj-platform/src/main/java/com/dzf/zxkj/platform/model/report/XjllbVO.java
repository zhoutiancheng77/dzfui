package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 现金流量表
 * @author JasonLiu
 *
 */
public class XjllbVO extends SuperVO {

	//项目
	private String xm ;
	
	private String xmid;
	
	//行次
	private String hc ;
	//编码
	private String hc_id;
	
	//本期金额
	private DZFDouble bqje ;
	
	private DZFDouble bqje_last;//上年同期金额(单独查一个期间暂时没，需要再查一次)
	
	//本年累计金额
	private DZFDouble sqje ;//历史遗留，这名字看着很别扭
	private DZFDouble sqje_last;//上期金额
	//打印时  标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;
	
	private String period;//期间
	
	private DZFBoolean isseven;
	
	private float rowno;
	
	private String kmfa;//科目方案
	
	private String pk_project;
	
	private DZFBoolean bxjlltotal;//是否是现金流量的合计
	
	private DZFBoolean bkmqc;//是否科目期初
	
	private DZFBoolean bkmqm;//是否科目期末
	
	private String formula;//公式

	public String getXmid() {
		return xmid;
	}

	public void setXmid(String xmid) {
		this.xmid = xmid;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public DZFBoolean getBxjlltotal() {
		return bxjlltotal;
	}

	public void setBxjlltotal(DZFBoolean bxjlltotal) {
		this.bxjlltotal = bxjlltotal;
	}

	public DZFBoolean getBkmqc() {
		return bkmqc;
	}

	public void setBkmqc(DZFBoolean bkmqc) {
		this.bkmqc = bkmqc;
	}

	public DZFBoolean getBkmqm() {
		return bkmqm;
	}

	public void setBkmqm(DZFBoolean bkmqm) {
		this.bkmqm = bkmqm;
	}

	public String getPk_project() {
		return pk_project;
	}

	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	public String getKmfa() {
		return kmfa;
	}

	public void setKmfa(String kmfa) {
		this.kmfa = kmfa;
	}

	public DZFDouble getSqje_last() {
		return sqje_last;
	}

	public void setSqje_last(DZFDouble sqje_last) {
		this.sqje_last = sqje_last;
	}

	public float getRowno() {
		return rowno;
	}

	public void setRowno(float rowno) {
		this.rowno = rowno;
	}

	public String getHc_id() {
		return hc_id;
	}

	public void setHc_id(String hc_id) {
		this.hc_id = hc_id;
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

	public DZFDouble getBqje() {
		return bqje;
	}

	public void setBqje(DZFDouble bqje) {
		this.bqje = bqje;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public DZFDouble getSqje() {
		return sqje;
	}

	public void setSqje(DZFDouble sqje) {
		this.sqje = sqje;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
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

	public DZFBoolean getIsseven() {
		return isseven;
	}

	public void setIsseven(DZFBoolean isseven) {
		this.isseven = isseven;
	}

	public DZFDouble getBqje_last() {
		return bqje_last;
	}

	public void setBqje_last(DZFDouble bqje_last) {
		this.bqje_last = bqje_last;
	}
	

}
