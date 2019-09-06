package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.util.Optional;

/**
 * 收入预警历史数据
 * @author liubj
 *
 */
public class IncomeHistoryVo extends SuperVO {
	
	private String pk_income_history;
	//收入预警pk
	private String pk_sryj;
	//期间
	private String period;
	//发生额
	private DZFDouble occur_mny;
	private String pk_corp;
	private Integer dr;
	private DZFDateTime ts;

	public Double getOccurMnyValue(){
		return Optional.ofNullable(occur_mny).orElse(DZFDouble.ZERO_DBL).doubleValue();
	}
	
	public String getPk_income_history() {
		return pk_income_history;
	}
	public void setPk_income_history(String pk_income_history) {
		this.pk_income_history = pk_income_history;
	}
	public String getPk_sryj() {
		return pk_sryj;
	}
	public void setPk_sryj(String pk_sryj) {
		this.pk_sryj = pk_sryj;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public DZFDouble getOccur_mny() {
		return occur_mny;
	}
	public void setOccur_mny(DZFDouble occur_mny) {
		this.occur_mny = occur_mny;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	@Override
	public String getParentPKFieldName() {
		return "pk_sryj";
	}
	@Override
	public String getPKFieldName() {
		return "pk_income_history";
	}
	@Override
	public String getTableName() {
		return "ynt_income_history";
	}
}
