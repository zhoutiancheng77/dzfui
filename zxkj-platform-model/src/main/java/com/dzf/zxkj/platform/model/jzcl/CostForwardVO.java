package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class CostForwardVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2545758843871916314L;

	private String zy;
	private String pk_corp;
	private String pk_accsubj;
	private String vcode;
	private String vname;
	private DZFDouble jfmny;
	private DZFDouble dfmny;
	private String vnote;
	//后加
	private String pk_inventory;
	private String invname;
	private DZFDouble nnum;

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public DZFDouble getJfmny() {
		return jfmny;
	}

	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}

	public DZFDouble getDfmny() {
		return dfmny;
	}

	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}

	public String getVnote() {
		return vnote;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public DZFDouble getNnum() {
		return nnum;
	}

	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public String getTableName() {
		// TODO 自动生成的方法存根
		return null;
	}
}
