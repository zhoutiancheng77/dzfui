package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 凭证保存，生成相应的销项、进项税率表
 * @author Administrator
 *
 */
public class PZTaxItemRadioVO extends SuperVO {
	
	private String pk_pztaxitem;
	private String pk_taxitem;//税目id
	private String taxcode;// 税目code
	private String taxname;// 税目名称
	private DZFDouble taxratio;//税率
	private DZFDateTime ts;//
	private Integer dr;//
	private String pk_corp;
	private String pk_tzpz_h;
	private String pk_tzpz_b;
	private DZFDouble taxmny;//税额
	private DZFDouble mny;//金额
	private String period;//期间
	private Integer fp_style;//发票类型
	//1	普票（开具的普通发票）
	//2	专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
	//3	未开票（指一般人未开票的收入）
	private Integer vdirect;//凭证科目方向 0, 1
	// 科目编码
	private String vcode;
	// 是否为销售货物
	private boolean cargo;
	// 凭证分录index
	private Integer entry_index;
	// 税种 进项|销项
	private String tax_style;
	// 用户手工保存
	private Boolean user_save;
	public String getPk_taxitem() {
		return pk_taxitem;
	}
	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
	}
	public String getTaxcode() {
		return taxcode;
	}
	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}
	public String getTaxname() {
		return taxname;
	}
	public void setTaxname(String taxname) {
		this.taxname = taxname;
	}
	public DZFDouble getTaxratio() {
		return taxratio;
	}
	public void setTaxratio(DZFDouble taxratio) {
		this.taxratio = taxratio;
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
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}
	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}
	public String getPk_tzpz_b() {
		return pk_tzpz_b;
	}
	public void setPk_tzpz_b(String pk_tzpz_b) {
		this.pk_tzpz_b = pk_tzpz_b;
	}
	public String getPk_pztaxitem() {
		return pk_pztaxitem;
	}
	
	public DZFDouble getTaxmny() {
		return taxmny;
	}
	public void setTaxmny(DZFDouble taxmny) {
		this.taxmny = taxmny;
	}
	public DZFDouble getMny() {
		return mny;
	}
	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public void setPk_pztaxitem(String pk_pztaxitem) {
		this.pk_pztaxitem = pk_pztaxitem;
	}
	public Integer getFp_style() {
		return fp_style;
	}
	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}
	public Integer getVdirect() {
		return vdirect;
	}
	public void setVdirect(Integer vdirect) {
		this.vdirect = vdirect;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public boolean isCargo() {
		return cargo;
	}

	public void setCargo(boolean cargo) {
		this.cargo = cargo;
	}

	public Integer getEntry_index() {
		return entry_index;
	}

	public void setEntry_index(Integer entry_index) {
		this.entry_index = entry_index;
	}

	public String getTax_style() {
		return tax_style;
	}

	public void setTax_style(String tax_style) {
		this.tax_style = tax_style;
	}

	public Boolean getUser_save() {
		return user_save;
	}

	public void setUser_save(Boolean user_save) {
		this.user_save = user_save;
	}

	@Override
	public String getPKFieldName() {
		return "pk_pztaxitem";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "ynt_pztaxitem";
	}
}
