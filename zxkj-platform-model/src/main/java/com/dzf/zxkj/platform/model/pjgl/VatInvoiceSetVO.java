package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VatInvoiceSetVO extends SuperVO {
	@JsonProperty("setid")
	private String pk_invoiceset;
	private String pk_corp;
	private String style;//类型  1银行对账单  2销项发票  3进项发票 
	private Integer value;//设置的规则  ——》凭证合并的
	private DZFBoolean isbank;//是否包含银行科目
	
	private Integer entry_type;//分录合并规则
	private String zy;
	
	private DZFDateTime ts;
	private Integer dr;
	
	public String getPk_invoiceset() {
		return pk_invoiceset;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getStyle() {
		return style;
	}

	public Integer getValue() {
		return value;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_invoiceset(String pk_invoiceset) {
		this.pk_invoiceset = pk_invoiceset;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFBoolean getIsbank() {
		return isbank;
	}

	public void setIsbank(DZFBoolean isbank) {
		this.isbank = isbank;
	}

	public Integer getEntry_type() {
		return entry_type;
	}

	public String getZy() {
		return zy;
	}

	public void setEntry_type(Integer entry_type) {
		this.entry_type = entry_type;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invoiceset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_invoiceset";
	}

}
