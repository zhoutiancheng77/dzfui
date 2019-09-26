package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class IncomeTaxVO extends SuperVO {
	// 主键
	private String pk_tax;
	// 期间
	private String period;
	// 公司
	private String pk_corp;
	private DZFDateTime modifyDate;
	// 操作人
	private String coperatorid;
	private DZFDateTime ts;
	private Integer dr;
	// 是否根据本数据结转
	private Boolean carryover;
	// 所得税征收方式
	private Integer tax_levy_type;
	// 所得税类型
	private Integer tax_type;

	// 利润总额
	private DZFDouble lrze;
	// 不征税收入和免税收入等
	private DZFDouble bzsmssr;
	// 固定资产加速折旧调减额
	private DZFDouble gdzczj;
	// 弥补以前年度亏损
	private DZFDouble mbks;

	// 收入总额
	private DZFDouble srze;
	// 不征税收入
	private DZFDouble bzssr;
	// 免税收入
	private DZFDouble mssr;
	// 税务机关核定的应税所得率（%）
	private DZFDouble hdsdl;

	// 应纳所得额|实际利润额
	private DZFDouble ynsde;
	// 税率
	private DZFDouble rate;
	// 减免所得税额|减：符合条件的小型微利企业减免企业所得税
	private DZFDouble jmsds;
	// 实际已缴纳所得税额
	private DZFDouble sjyjsds;
	// 本期实际应补（退）所得税额
	private DZFDouble sjybtsds;

	// 速算扣除数
	private DZFDouble sskcs;
	// 投资者减除费用
	private DZFDouble tzzjc;
	// 专项扣除
	private DZFDouble zxkc;

	public String getPk_tax() {
		return pk_tax;
	}

	public void setPk_tax(String pk_tax) {
		this.pk_tax = pk_tax;
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

	public DZFDateTime getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(DZFDateTime modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Boolean getCarryover() {
		return carryover;
	}

	public void setCarryover(Boolean carryover) {
		this.carryover = carryover;
	}

	public Integer getTax_levy_type() {
		return tax_levy_type;
	}

	public void setTax_levy_type(Integer tax_levy_type) {
		this.tax_levy_type = tax_levy_type;
	}

	public Integer getTax_type() {
		return tax_type;
	}

	public void setTax_type(Integer tax_type) {
		this.tax_type = tax_type;
	}

	public DZFDouble getLrze() {
		return lrze;
	}

	public void setLrze(DZFDouble lrze) {
		this.lrze = lrze;
	}

	public DZFDouble getBzsmssr() {
		return bzsmssr;
	}

	public void setBzsmssr(DZFDouble bzsmssr) {
		this.bzsmssr = bzsmssr;
	}

	public DZFDouble getGdzczj() {
		return gdzczj;
	}

	public void setGdzczj(DZFDouble gdzczj) {
		this.gdzczj = gdzczj;
	}

	public DZFDouble getMbks() {
		return mbks;
	}

	public void setMbks(DZFDouble mbks) {
		this.mbks = mbks;
	}

	public DZFDouble getSrze() {
		return srze;
	}

	public void setSrze(DZFDouble srze) {
		this.srze = srze;
	}

	public DZFDouble getBzssr() {
		return bzssr;
	}

	public void setBzssr(DZFDouble bzssr) {
		this.bzssr = bzssr;
	}

	public DZFDouble getMssr() {
		return mssr;
	}

	public void setMssr(DZFDouble mssr) {
		this.mssr = mssr;
	}

	public DZFDouble getHdsdl() {
		return hdsdl;
	}

	public void setHdsdl(DZFDouble hdsdl) {
		this.hdsdl = hdsdl;
	}

	public DZFDouble getYnsde() {
		return ynsde;
	}

	public void setYnsde(DZFDouble ynsde) {
		this.ynsde = ynsde;
	}

	public DZFDouble getRate() {
		return rate;
	}

	public void setRate(DZFDouble rate) {
		this.rate = rate;
	}

	public DZFDouble getJmsds() {
		return jmsds;
	}

	public void setJmsds(DZFDouble jmsds) {
		this.jmsds = jmsds;
	}

	public DZFDouble getSjyjsds() {
		return sjyjsds;
	}

	public void setSjyjsds(DZFDouble sjyjsds) {
		this.sjyjsds = sjyjsds;
	}

	public DZFDouble getSjybtsds() {
		return sjybtsds;
	}

	public void setSjybtsds(DZFDouble sjybtsds) {
		this.sjybtsds = sjybtsds;
	}

	public DZFDouble getSskcs() {
		return sskcs;
	}

	public void setSskcs(DZFDouble sskcs) {
		this.sskcs = sskcs;
	}

	public DZFDouble getTzzjc() {
		return tzzjc;
	}

	public void setTzzjc(DZFDouble tzzjc) {
		this.tzzjc = tzzjc;
	}

	public DZFDouble getZxkc() {
		return zxkc;
	}

	public void setZxkc(DZFDouble zxkc) {
		this.zxkc = zxkc;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_tax";
	}

	@Override
	public String getTableName() {
		return "ynt_taxcal_incometax";
	}
}
