package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class AddValueTaxVO extends SuperVO implements Comparable<AddValueTaxVO> {
	/** 销项 */
	public static int TYPE_OUTTAX = 1;
	/** 进项 */
	public static int TYPE_INTAX = 2;

	private String pk_tax;
	// 期间
	private String period;
	// 公司
	private String pk_corp;
	// 操作人
	private String coperatorid;
	private DZFDateTime ts;
	private Integer dr;
	// 税名
	private String tax_name;
	// 类型
	private Integer tax_type;
	// 序号
	private Integer snumber;
	// 税目
	private String pk_taxitem;
	// 税率
	private DZFDouble rate;
	// 金额
	private DZFDouble mny;
	// 税额
	private DZFDouble taxmny;
	// 专票-金额
	private DZFDouble mny_spec;
	// 专票-税额
	private DZFDouble taxmny_spec;
	// 普票-金额
	private DZFDouble mny_gen;
	// 普票-税额
	private DZFDouble taxmny_gen;
	// 未开票-金额
	private DZFDouble mny_not;
	// 未开票-税额
	private DZFDouble taxmny_not;
	// 货物-金额
	private DZFDouble mny_cargo;
	// 货物-税额
	private DZFDouble taxmny_cargo;
	// 服务-金额
	private DZFDouble mny_service;
	// 服务-税额
	private DZFDouble taxmny_service;
	// 份数
	private Integer num_count;
	// 期间类型
	private Integer period_type;

	// 发票类型
	private Integer fp_style;

	// 发票清单查询
	private String bspmc;
	private String busitypetempname;
	private DZFBoolean isZhuan;

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

	public String getTax_name() {
		return tax_name;
	}

	public void setTax_name(String tax_name) {
		this.tax_name = tax_name;
	}

	public Integer getTax_type() {
		return tax_type;
	}

	public void setTax_type(Integer tax_type) {
		this.tax_type = tax_type;
	}

	public Integer getSnumber() {
		return snumber;
	}

	public void setSnumber(Integer snumber) {
		this.snumber = snumber;
	}

	public String getPk_taxitem() {
		return pk_taxitem;
	}

	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
	}

	public DZFDouble getRate() {
		return rate;
	}

	public void setRate(DZFDouble rate) {
		this.rate = rate;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public DZFDouble getTaxmny() {
		return taxmny;
	}

	public void setTaxmny(DZFDouble taxmny) {
		this.taxmny = taxmny;
	}

	public DZFDouble getMny_spec() {
		return mny_spec;
	}

	public void setMny_spec(DZFDouble mny_spec) {
		this.mny_spec = mny_spec;
	}

	public DZFDouble getTaxmny_spec() {
		return taxmny_spec;
	}

	public void setTaxmny_spec(DZFDouble taxmny_spec) {
		this.taxmny_spec = taxmny_spec;
	}

	public DZFDouble getMny_gen() {
		return mny_gen;
	}

	public void setMny_gen(DZFDouble mny_gen) {
		this.mny_gen = mny_gen;
	}

	public DZFDouble getTaxmny_gen() {
		return taxmny_gen;
	}

	public void setTaxmny_gen(DZFDouble taxmny_gen) {
		this.taxmny_gen = taxmny_gen;
	}

	public DZFDouble getMny_not() {
		return mny_not;
	}

	public void setMny_not(DZFDouble mny_not) {
		this.mny_not = mny_not;
	}

	public DZFDouble getTaxmny_not() {
		return taxmny_not;
	}

	public void setTaxmny_not(DZFDouble taxmny_not) {
		this.taxmny_not = taxmny_not;
	}

	public DZFDouble getMny_cargo() {
		return mny_cargo;
	}

	public void setMny_cargo(DZFDouble mny_cargo) {
		this.mny_cargo = mny_cargo;
	}

	public DZFDouble getTaxmny_cargo() {
		return taxmny_cargo;
	}

	public void setTaxmny_cargo(DZFDouble taxmny_cargo) {
		this.taxmny_cargo = taxmny_cargo;
	}

	public DZFDouble getMny_service() {
		return mny_service;
	}

	public void setMny_service(DZFDouble mny_service) {
		this.mny_service = mny_service;
	}

	public DZFDouble getTaxmny_service() {
		return taxmny_service;
	}

	public void setTaxmny_service(DZFDouble taxmny_service) {
		this.taxmny_service = taxmny_service;
	}

	public Integer getNum_count() {
		return num_count;
	}

	public void setNum_count(Integer num_count) {
		this.num_count = num_count;
	}

	public Integer getPeriod_type() {
		return period_type;
	}

	public void setPeriod_type(Integer period_type) {
		this.period_type = period_type;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}

	public String getBspmc() {
		return bspmc;
	}

	public void setBspmc(String bspmc) {
		this.bspmc = bspmc;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}

	public DZFBoolean getIsZhuan() {
		return isZhuan;
	}

	public void setIsZhuan(DZFBoolean isZhuan) {
		this.isZhuan = isZhuan;
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
		return "ynt_taxcal_addtax";
	}

	@Override
	public int compareTo(AddValueTaxVO another) {
		Integer i1 = getSnumber();
		Integer i2 = another.getSnumber();
		if (i1 == null) {
			i1 = new Integer(999);
		}
		if (i2 == null) {
			i2 = new Integer(999);
		}
		return i1.compareTo(i2);
	}
}
