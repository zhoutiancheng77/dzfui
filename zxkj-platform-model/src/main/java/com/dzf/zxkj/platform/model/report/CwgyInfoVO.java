package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 财务概要信息
 *
 */
@SuppressWarnings("rawtypes")
public class CwgyInfoVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 项目
	private String xm;
	// 项目分类
	private String xmfl;

	// 行数
	private String hs;

	// 本月金额
	private DZFDouble byje;
	// 本月金额
	private String sbyje;
	// 本月同比
	private DZFDouble bybl;
	private String sbybl;

	// 本月环比
	private DZFDouble byhb;
	private String sbyhb;

	// 本年累计金额 (发生的累计，不包含期初)
	private DZFDouble bnljje;
	private String sbnljje;
	// 本年累计同比
	private DZFDouble bnljbl;
	private String sbnljbl;

	// 上年同期数
	private DZFDouble sntqs;

	// 去年累计金额
	private DZFDouble qnljje;

	private Integer level;

	// 本年累计(包含期初的本年累计)
	private DZFDouble bnlj;

	// 打印时 标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;

	private String period;// 期间

	private String pk_corp;

	private DZFBoolean isseven;

	private Integer rowspan;
	private Integer row;

	private Integer colspan;
	private Integer col;
	
	//上年同期数，和上面的重复，避免冲突用心的字段
	private DZFDouble byje_pre;
	private DZFDouble bnljje_pre;
	

	public DZFDouble getByje_pre() {
		return byje_pre;
	}

	public void setByje_pre(DZFDouble byje_pre) {
		this.byje_pre = byje_pre;
	}

	public DZFDouble getBnljje_pre() {
		return bnljje_pre;
	}

	public void setBnljje_pre(DZFDouble bnljje_pre) {
		this.bnljje_pre = bnljje_pre;
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

	public DZFDouble getBnljje() {
		return bnljje;
	}

	public void setBnljje(DZFDouble bnljje) {
		this.bnljje = bnljje;
	}

	public DZFDouble getByje() {
		return byje;
	}

	public void setByje(DZFDouble byje) {
		this.byje = byje;
	}

	public String getHs() {
		return hs;
	}

	public void setHs(String hs) {
		this.hs = hs;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFBoolean getIsseven() {
		return isseven;
	}

	public void setIsseven(DZFBoolean isseven) {
		this.isseven = isseven;
	}

	public DZFDouble getBnlj() {
		return bnlj;
	}

	public void setBnlj(DZFDouble bnlj) {
		this.bnlj = bnlj;
	}

	public DZFDouble getQnljje() {
		return qnljje;
	}

	public void setQnljje(DZFDouble qnljje) {
		this.qnljje = qnljje;
	}

	public DZFDouble getSntqs() {
		return sntqs;
	}

	public void setSntqs(DZFDouble sntqs) {
		this.sntqs = sntqs;
	}

	public DZFDouble getBybl() {
		return bybl;
	}

	public void setBybl(DZFDouble bybl) {
		this.bybl = bybl;
	}

	public DZFDouble getBnljbl() {
		return bnljbl;
	}

	public void setBnljbl(DZFDouble bnljbl) {
		this.bnljbl = bnljbl;
	}

	public String getXmfl() {
		return xmfl;
	}

	public void setXmfl(String xmfl) {
		this.xmfl = xmfl;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	public Integer getRow() {
		return row;
	}

	public Integer getColspan() {
		return colspan;
	}

	public Integer getCol() {
		return col;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public void setCol(Integer col) {
		this.col = col;
	}

	public String getSbyje() {
		return sbyje;
	}

	public String getSbybl() {
		return sbybl;
	}

	public String getSbnljje() {
		return sbnljje;
	}

	public String getSbnljbl() {
		return sbnljbl;
	}

	public void setSbyje(String sbyje) {
		this.sbyje = sbyje;
	}

	public void setSbybl(String sbybl) {
		this.sbybl = sbybl;
	}

	public void setSbnljje(String sbnljje) {
		this.sbnljje = sbnljje;
	}

	public void setSbnljbl(String sbnljbl) {
		this.sbnljbl = sbnljbl;
	}

	public DZFDouble getByhb() {
		return byhb;
	}

	public void setByhb(DZFDouble byhb) {
		this.byhb = byhb;
	}

	public String getSbyhb() {
		return sbyhb;
	}

	public void setSbyhb(String sbyhb) {
		this.sbyhb = sbyhb;
	}

}
