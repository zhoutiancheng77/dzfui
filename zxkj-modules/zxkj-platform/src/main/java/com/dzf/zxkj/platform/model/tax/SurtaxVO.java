package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 附加税
 * 
 * @author lbj
 *
 */
public class SurtaxVO extends SuperVO implements Comparable<SurtaxVO>{
	private String pk_tax;
	// 期间
	private String period;
	// 公司
	private String pk_corp;
	// 操作人
	private String coperatorid;
	private DZFDateTime ts;
	private Integer dr;
	// 期间类型
	private Integer period_type;
	// 税种主键
	private String pk_archive;
	// 税名
	private String tax_name;
	// 是否为附加税
	private Boolean is_surtax;
	// 序号
	private Integer snumber;
	// 是否结转
	private Boolean carryover;
	private String pk_voucher;
	// 计税依据
	private DZFDouble base_tax;
	// 税率
	private DZFDouble rate;
	// 本期应纳税额
	private DZFDouble ynse;
	// 减免税额
	private DZFDouble jmse;
	// 已缴税额
	private DZFDouble yjse;
	// 增值税小规模纳税人减征额
	private DZFDouble xgmjze;
	// 应补退税额
	private DZFDouble ybtse;

	private String voucher_num;
	// 计税依据
	private String pk_base_subject;

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

	public Integer getPeriod_type() {
		return period_type;
	}

	public void setPeriod_type(Integer period_type) {
		this.period_type = period_type;
	}

	public String getPk_archive() {
		return pk_archive;
	}

	public void setPk_archive(String pk_archive) {
		this.pk_archive = pk_archive;
	}

	public String getTax_name() {
		return tax_name;
	}

	public void setTax_name(String tax_name) {
		this.tax_name = tax_name;
	}

	public Boolean getIs_surtax() {
		return is_surtax;
	}

	public void setIs_surtax(Boolean is_surtax) {
		this.is_surtax = is_surtax;
	}

	public Integer getSnumber() {
		return snumber;
	}

	public void setSnumber(Integer snumber) {
		this.snumber = snumber;
	}

	public Boolean getCarryover() {
		return carryover;
	}

	public void setCarryover(Boolean carryover) {
		this.carryover = carryover;
	}

	public String getPk_voucher() {
		return pk_voucher;
	}

	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}

	public DZFDouble getBase_tax() {
		return base_tax;
	}

	public void setBase_tax(DZFDouble base_tax) {
		this.base_tax = base_tax;
	}

	public DZFDouble getRate() {
		return rate;
	}

	public void setRate(DZFDouble rate) {
		this.rate = rate;
	}

	public DZFDouble getYnse() {
		return ynse;
	}

	public void setYnse(DZFDouble ynse) {
		this.ynse = ynse;
	}

	public DZFDouble getJmse() {
		return jmse;
	}

	public void setJmse(DZFDouble jmse) {
		this.jmse = jmse;
	}

	public DZFDouble getYjse() {
		return yjse;
	}

	public void setYjse(DZFDouble yjse) {
		this.yjse = yjse;
	}

	public DZFDouble getXgmjze() {
		return xgmjze;
	}

	public void setXgmjze(DZFDouble xgmjze) {
		this.xgmjze = xgmjze;
	}

	public DZFDouble getYbtse() {
		return ybtse;
	}

	public void setYbtse(DZFDouble ybtse) {
		this.ybtse = ybtse;
	}

	public String getVoucher_num() {
		return voucher_num;
	}

	public void setVoucher_num(String voucher_num) {
		this.voucher_num = voucher_num;
	}

	public String getPk_base_subject() {
		return pk_base_subject;
	}

	public void setPk_base_subject(String pk_base_subject) {
		this.pk_base_subject = pk_base_subject;
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
		return "ynt_taxcal_surtax";
	}

	@Override
	public int compareTo(SurtaxVO o) {
		return getSnumber().compareTo(o.getSnumber());
	}
}
