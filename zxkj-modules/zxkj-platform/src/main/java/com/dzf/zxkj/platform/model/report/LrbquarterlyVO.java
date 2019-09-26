package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 利润表季报
 * @author JasonLiu
 *
 */
public class LrbquarterlyVO extends SuperVO {

	//项目
	private String xm ;
	
	//行数
	private String hs ;
	
	//行数id
	private String hs_id;
	
	
	//本年累计(包含期初的本年累计)
	private DZFDouble bnlj;
	
	private DZFDouble quarterFirst ;//第一季度
	private DZFDouble quarterSecond ;//第二季度
	private DZFDouble quarterThird ;//第三季度
	private DZFDouble quarterFourth ;//第四季度
	
	private DZFDouble lastquarterFirst ;//上年同期第一季度
	private DZFDouble lastquarterSecond ;//上年同期第二季度
	private DZFDouble lastquarterThird ;//上年同期第三季度
	private DZFDouble lastquarterFourth ;//上年同期第四季度
	
	
	//上年同期数
	private DZFDouble sntqs;
	//本年累计金额	 (发生的累计，不包含期初,计算季度数使用)
	private DZFDouble bnljje ;
	
	//去年累计金额	
	private DZFDouble qnljje ;
	
	//本月金额
	private DZFDouble byje ;
	
	private Integer level;
	
	//打印时  标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;
	
	private String period;//期间
	
	private String pk_corp;
	
	private DZFBoolean isseven;
	

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

	public DZFDouble getQuarterFirst() {
		return quarterFirst;
	}

	public void setQuarterFirst(DZFDouble quarterFirst) {
		this.quarterFirst = quarterFirst;
	}

	public DZFDouble getQuarterSecond() {
		return quarterSecond;
	}

	public void setQuarterSecond(DZFDouble quarterSecond) {
		this.quarterSecond = quarterSecond;
	}

	public DZFDouble getQuarterThird() {
		return quarterThird;
	}

	public void setQuarterThird(DZFDouble quarterThird) {
		this.quarterThird = quarterThird;
	}

	public DZFDouble getQuarterFourth() {
		return quarterFourth;
	}

	public void setQuarterFourth(DZFDouble quarterFourth) {
		this.quarterFourth = quarterFourth;
	}

	public DZFDouble getLastquarterFirst() {
		return lastquarterFirst;
	}

	public void setLastquarterFirst(DZFDouble lastquarterFirst) {
		this.lastquarterFirst = lastquarterFirst;
	}

	public DZFDouble getLastquarterSecond() {
		return lastquarterSecond;
	}

	public void setLastquarterSecond(DZFDouble lastquarterSecond) {
		this.lastquarterSecond = lastquarterSecond;
	}

	public DZFDouble getLastquarterThird() {
		return lastquarterThird;
	}

	public void setLastquarterThird(DZFDouble lastquarterThird) {
		this.lastquarterThird = lastquarterThird;
	}

	public DZFDouble getLastquarterFourth() {
		return lastquarterFourth;
	}

	public void setLastquarterFourth(DZFDouble lastquarterFourth) {
		this.lastquarterFourth = lastquarterFourth;
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

	public String getHs_id() {
		return hs_id;
	}

	public void setHs_id(String hs_id) {
		this.hs_id = hs_id;
	}
	

	
}
