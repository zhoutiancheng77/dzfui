package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class JtsjVO extends SuperVO<JtsjVO> {
	
	  private String pk_jtsjtemplate;
	  private String kmmethod ;
	  private String jfkmmc;
	  private String dfkmmc;
	  private String jfkm_id;           
	  private String dfkm_id;       
	  private Integer dr;                  
	  private String pk_corp ;             
	  private DZFDouble tax ;
	  private String memo;                
	  private DZFDateTime ts;
	  private String pk_group;
	  private String vdef1;
	  private String vdef2;
	  private String vdef3;
	  private String vdef4;
	  private String vdef5;
	  
	  
	  
	public String getPk_jtsjtemplate() {
		return pk_jtsjtemplate;
	}
	public void setPk_jtsjtemplate(String pk_jtsjtemplate) {
		this.pk_jtsjtemplate = pk_jtsjtemplate;
	}
	public String getKmmethod() {
		return kmmethod;
	}
	public void setKmmethod(String kmmethod) {
		this.kmmethod = kmmethod;
	}
	
	public String getJfkmmc() {
		return jfkmmc;
	}
	public void setJfkmmc(String jfkmmc) {
		this.jfkmmc = jfkmmc;
	}
	public String getDfkmmc() {
		return dfkmmc;
	}
	public void setDfkmmc(String dfkmmc) {
		this.dfkmmc = dfkmmc;
	}
	public String getJfkm_id() {
		return jfkm_id;
	}
	public void setJfkm_id(String jfkm_id) {
		this.jfkm_id = jfkm_id;
	}
	public String getDfkm_id() {
		return dfkm_id;
	}
	public void setDfkm_id(String dfkm_id) {
		this.dfkm_id = dfkm_id;
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
	public DZFDouble getTax() {
		return tax;
	}
	public void setTax(DZFDouble tax) {
		this.tax = tax;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public String getPk_group() {
		return pk_group;
	}
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}
	public String getVdef1() {
		return vdef1;
	}
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}
	public String getVdef2() {
		return vdef2;
	}
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}
	public String getVdef3() {
		return vdef3;
	}
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}
	public String getVdef4() {
		return vdef4;
	}
	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}
	public String getVdef5() {
		return vdef5;
	}
	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}
	@Override
	public String getPKFieldName() {
		return "pk_jtsjtemplate";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "YNT_JTSJ";
	}      
}
