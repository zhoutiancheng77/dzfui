package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 税收测试表 税种信息
 * 
 * @author lbj
 *
 */
public class TaxCalculateArchiveVO extends SuperVO {

	@JsonProperty("id")
	private String pk_taxarchive;
	// 编号
	private Integer snumber;
	// 税名
	@JsonProperty("name")
	private String tax_name;
	// 税率
	private DZFDouble rate;

	@JsonProperty("corp")
	private String pk_corp;

	private Integer dr;

	public String getPk_taxarchive() {
		return pk_taxarchive;
	}

	public void setPk_taxarchive(String pk_taxarchive) {
		this.pk_taxarchive = pk_taxarchive;
	}

	public Integer getSnumber() {
		return snumber;
	}

	public void setSnumber(Integer snumber) {
		this.snumber = snumber;
	}

	public String getTax_name() {
		return tax_name;
	}

	public void setTax_name(String tax_name) {
		this.tax_name = tax_name;
	}

	public DZFDouble getRate() {
		return rate;
	}

	public void setRate(DZFDouble rate) {
		this.rate = rate;
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

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_taxarchive";
	}

	@Override
	public String getTableName() {
		return "ynt_taxarchive";
	}

}
