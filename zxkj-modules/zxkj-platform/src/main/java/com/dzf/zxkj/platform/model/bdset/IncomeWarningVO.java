package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 收入预警VO
 */

public class IncomeWarningVO extends SuperVO {
	// 收入预警主键
	private String pk_sryj;
	private String pk_corp;
	private Integer dr;
	// 项目名称
	private String xmmc;
	// 科目
	private String km;
	// 科目编码
	private String kmbm;
	// 科目主键
	private String pk_accsubj;
	// 收入上限
	private DZFDouble srsx;
	// 预警值
	private DZFDouble yjz;
	// 是否登录提醒
	@JsonProperty("isLoginRemind")
	private String isloginremind;
	// 是否录入收入科目时提示
	@JsonProperty("isInputRemind")
	private String isinputremind;
	// 连续12月发生
	@JsonProperty("fsTotal")
	private DZFDouble fstotal;
	// 可新增收入
	@JsonProperty("infoNumber")
	private DZFDouble infonumber;
	/**
	 * 期间类型
	 * 月 0,季 1,年 2,连续12月 3或者空
	 */
	private Integer period_type;

	private DZFBoolean has_history;

	private DZFDouble Dftotal;//dzf_weixin 使用
	@JsonProperty("flg")
	private DZFBoolean speflg;//与其他预警参数予以区分

	private DZFDateTime ts;
	
	public DZFBoolean getSpeflg() {
		return speflg;
	}

	public void setSpeflg(DZFBoolean speflg) {
		this.speflg = speflg;
	}

	public DZFDouble getDftotal() {
		return Dftotal;
	}

	public void setDftotal(DZFDouble dftotal) {
		Dftotal = dftotal;
	}


	public String getPk_sryj() {
		return pk_sryj;
	}

	public void setPk_sryj(String pk_sryj) {
		this.pk_sryj = pk_sryj;
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

	public String getXmmc() {
		return xmmc;
	}

	public void setXmmc(String xmmc) {
		this.xmmc = xmmc;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public DZFDouble getSrsx() {
		return srsx;
	}

	public void setSrsx(DZFDouble srsx) {
		this.srsx = srsx;
	}

	public DZFDouble getYjz() {
		return yjz;
	}

	public void setYjz(DZFDouble yjz) {
		this.yjz = yjz;
	}

	public String getIsloginremind() {
		return isloginremind;
	}

	public void setIsloginremind(String isloginremind) {
		this.isloginremind = isloginremind;
	}

	public String getIsinputremind() {
		return isinputremind;
	}

	public void setIsinputremind(String isinputremind) {
		this.isinputremind = isinputremind;
	}

	public DZFDouble getFstotal() {
		return fstotal;
	}

	public void setFstotal(DZFDouble fstotal) {
		this.fstotal = fstotal;
	}

	public DZFDouble getInfonumber() {
		return infonumber;
	}

	public void setInfonumber(DZFDouble infonumber) {
		this.infonumber = infonumber;
	}

	public DZFBoolean getHas_history() {
		return has_history;
	}

	public void setHas_history(DZFBoolean has_history) {
		this.has_history = has_history;
	}

	public Integer getPeriod_type() {
		return period_type;
	}

	public void setPeriod_type(Integer period_type) {
		this.period_type = period_type;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_sryj";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_IncomeWarning";
	}

}
