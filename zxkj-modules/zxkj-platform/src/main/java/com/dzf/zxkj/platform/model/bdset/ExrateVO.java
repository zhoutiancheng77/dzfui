package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * 汇率档案
 * @author Administrator
 *
 */
@Entity
public class ExrateVO extends SuperVO {//
	
	@JsonProperty("pk_cur")
	private String pk_currency;// 币种
	@JsonProperty("ctime")
	private DZFDateTime createtime;
	@JsonProperty("user")
	private String creator;
	@JsonProperty("ecode")
	private String exratecode;// 汇率编码
	@JsonProperty("ename")
	private String exratename;// 汇率名称
	@JsonProperty("dr")
	private Integer dr;
	@JsonProperty("modr")
	private String modifier;
	@JsonProperty("modt")
	private DZFDateTime modifytime;
	@JsonProperty("momo")
	private String memo;
	@JsonProperty("slag")
	private String sealflag;
	@JsonProperty("ts")
	private DZFDateTime ts;
	@JsonProperty("id_rate")
	private String pk_exrate;
	//
	@JsonProperty("cmode")
	private Integer convmode;// 折算模式

	@JsonProperty("erate")
	private DZFDouble exrate;// 汇率
	@JsonProperty("adjrate")
	private DZFDouble adjustrate;// 调整汇率
	@JsonProperty("flaote")
	private DZFBoolean isfloatrate;// 是否浮动汇率
	@JsonProperty("corpid")
	private String pk_corp;
	@JsonProperty("encyname")
	private String currencyname;
	@JsonProperty("cratename")
	private String creatorname;
	
	
	public String getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	public DZFDateTime getCreatetime() {
		return createtime;
	}
	public void setCreatetime(DZFDateTime createtime) {
		this.createtime = createtime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getExratecode() {
		return exratecode;
	}
	public void setExratecode(String exratecode) {
		this.exratecode = exratecode;
	}
	public String getExratename() {
		return exratename;
	}
	public void setExratename(String exratename) {
		this.exratename = exratename;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public DZFDateTime getModifytime() {
		return modifytime;
	}
	public void setModifytime(DZFDateTime modifytime) {
		this.modifytime = modifytime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getSealflag() {
		return sealflag;
	}
	public void setSealflag(String sealflag) {
		this.sealflag = sealflag;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public String getPk_exrate() {
		return pk_exrate;
	}
	public void setPk_exrate(String pk_exrate) {
		this.pk_exrate = pk_exrate;
	}
	public Integer getConvmode() {
		return convmode;
	}
	public void setConvmode(Integer convmode) {
		this.convmode = convmode;
	}
	public DZFDouble getExrate() {
		return exrate;
	}
	public void setExrate(DZFDouble exrate) {
		this.exrate = exrate;
	}
	public DZFDouble getAdjustrate() {
		return adjustrate;
	}
	public void setAdjustrate(DZFDouble adjustrate) {
		this.adjustrate = adjustrate;
	}
	public DZFBoolean getIsfloatrate() {
		return isfloatrate;
	}
	public void setIsfloatrate(DZFBoolean isfloatrate) {
		this.isfloatrate = isfloatrate;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getCurrencyname() {
		return currencyname;
	}
	public void setCurrencyname(String currencyname) {
		this.currencyname = currencyname;
	}
	public String getCreatorname() {
		return creatorname;
	}
	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return "pk_exrate";
	}
	@Override
	public String getTableName() {
		return "ynt_exrate";
	}
}
