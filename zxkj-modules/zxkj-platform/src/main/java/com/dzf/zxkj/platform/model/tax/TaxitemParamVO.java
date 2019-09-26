package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class TaxitemParamVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String pk_corp;
	private final DZFDouble taxratio;
	private final String userid;
	private final String invname;
	private final Integer fp_style;// 发票类型
	// (1), 普票（开具的普通发票）
	// (2), 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
	// 如果为null 为不区分专、普票

	public static class Builder {
		private String pk_corp;
		private DZFDouble taxratio;

		private Integer fp_style;
		private String invname;
		private String userid;

		public Builder(String pk_corp, DZFDouble taxratio) {
			this.pk_corp = pk_corp;
			this.taxratio = taxratio;
		}

		public Builder Fp_style(Integer val) {
			fp_style = val;
			return this;
		}

		public Builder InvName(String val) {
			invname = val;
			return this;
		}

		public Builder UserId(String val) {
			userid = val;
			return this;
		}

		public TaxitemParamVO build() {
			return new TaxitemParamVO(this);
		}
	}

	private TaxitemParamVO(Builder builder) {
		pk_corp = builder.pk_corp;
		taxratio = builder.taxratio;
		fp_style = builder.fp_style;
		invname = builder.invname;
		userid = builder.userid;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public DZFDouble getTaxratio() {
		return taxratio;
	}

	public String getUserid() {
		return userid;
	}

	public String getInvname() {
		return invname;
	}

	public Integer getFp_style() {
		return fp_style;
	}

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

}
