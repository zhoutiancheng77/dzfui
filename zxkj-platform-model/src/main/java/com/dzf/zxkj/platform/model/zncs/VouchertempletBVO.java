package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 自定义凭证模板子表
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class VouchertempletBVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_vouchertemplet_b;//主键
	@JsonProperty("pid")
	private String pk_vouchertemplet_h;//主表主键
	@JsonProperty("corpid")
	private String pk_corp;	
	
	private String zy;//摘要
	@JsonProperty("accid")
	private String pk_accsubj;//科目主键
	@JsonProperty("debit")
	private Integer debitmny;//借方金额0金额1税额2价税合计3-金额4-税额5-价税合计
	@JsonProperty("credit")
	private Integer creditmny;//贷方金额0金额1税额2价税合计3-金额4-税额5-价税合计
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_vouchertemplet_b() {
		return pk_vouchertemplet_b;
	}

	public void setPk_vouchertemplet_b(String pk_vouchertemplet_b) {
		this.pk_vouchertemplet_b = pk_vouchertemplet_b;
	}

	public String getPk_vouchertemplet_h() {
		return pk_vouchertemplet_h;
	}

	public void setPk_vouchertemplet_h(String pk_vouchertemplet_h) {
		this.pk_vouchertemplet_h = pk_vouchertemplet_h;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public Integer getDebitmny() {
		return debitmny;
	}

	public void setDebitmny(Integer debitmny) {
		this.debitmny = debitmny;
	}

	public Integer getCreditmny() {
		return creditmny;
	}

	public void setCreditmny(Integer creditmny) {
		this.creditmny = creditmny;
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
	public String getPKFieldName() {
		return "pk_vouchertemplet_b";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_vouchertemplet_h";
	}

	@Override
	public String getTableName() {
		return "ynt_vouchertemplet_b";
	}
	
	
}
