package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 利润表
 * @author JasonLiu
 *
 */
public class LrbVO extends SuperVO {

	//项目
	public String xm ;
	
	//行数
	public String hs ;
	
	//本年累计金额	
	public DZFDouble bnljje ;
	
	//本月金额
	public DZFDouble byje ;
	
	private Integer level;
	
	//打印时  标题显示的区间区间
	public String titlePeriod;
	// 公司
	public String gs;
	
	public String period;//期间
	
	private String vconkms; 

	
	public String pk_corp;
	
	public DZFBoolean isseven;
	
	public String kmfa;//当前公司所属科目方案 值参照 DzfUtil类
	
	//上年同期累计数
	private DZFDouble lastyear_bnljje;
	//项目
	private String xm2 ;
	
	//行数
	private String hs2 ;
	
	//本年累计金额	
	private DZFDouble bnljje2 ;
	
	//本月金额
	private DZFDouble byje2 ;
	
	private String vconkms2; 
	
	//行数id
	public String hs_id;
	
	//执行公式
	private String formula;
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

	public String getVconkms() {
		return vconkms;
	}

	public void setVconkms(String vconkms) {
		this.vconkms = vconkms;
	}

	public String getKmfa() {
		return kmfa;
	}

	public void setKmfa(String kmfa) {
		this.kmfa = kmfa;
	}

	public DZFDouble getLastyear_bnljje() {
		return lastyear_bnljje;
	}

	public void setLastyear_bnljje(DZFDouble lastyear_bnljje) {
		this.lastyear_bnljje = lastyear_bnljje;
	}

	public String getXm2() {
		return xm2;
	}

	public void setXm2(String xm2) {
		this.xm2 = xm2;
	}

	public String getHs2() {
		return hs2;
	}

	public void setHs2(String hs2) {
		this.hs2 = hs2;
	}

	public DZFDouble getBnljje2() {
		return bnljje2;
	}

	public void setBnljje2(DZFDouble bnljje2) {
		this.bnljje2 = bnljje2;
	}

	public DZFDouble getByje2() {
		return byje2;
	}

	public void setByje2(DZFDouble byje2) {
		this.byje2 = byje2;
	}

	public String getVconkms2() {
		return vconkms2;
	}

	public void setVconkms2(String vconkms2) {
		this.vconkms2 = vconkms2;
	}

	public String getHs_id() {
		return hs_id;
	}

	public void setHs_id(String hs_id) {
		this.hs_id = hs_id;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

	
}
