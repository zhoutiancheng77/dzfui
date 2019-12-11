package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author wangzhn
 *
 */
public class VatGoosInventoryRelationVO extends SuperVO {
	

	private String pk_goodsinvenrela;
	
	private String spmc;//货物或劳务名称

	private String invspec;//规格型号
	

	private String pk_inventory_old;//旧的存货主键
	

	private String pk_inventory;//存货主键
	
	private String code;//存货编码
	
	private String name;//存货名称

	private String coperatorid;//操作人

	private DZFDate doperatedate;//操作时间
	private String pk_corp;//
	private Integer dr;
	private DZFDateTime ts;

	private String pk_billcategory;
	private String unit;//计量单位
	private Integer calcmode;
	private DZFDouble hsl;
	private String subjname;//科目名称
	private String pk_subj;//科目主键
	
	private String fphm;
	
	private String mid;

	private DZFDouble saleNumber;//销售数量

	private DZFDouble salePrice;//销售价格



	public DZFDouble getSaleNumber() {
		return saleNumber;
	}

	public void setSaleNumber(DZFDouble saleNumber) {
		this.saleNumber = saleNumber;
	}

	public DZFDouble getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(DZFDouble salePrice) {
		this.salePrice = salePrice;
	}
	
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getSubjname() {
		return subjname;
	}

	public void setSubjname(String subjname) {
		this.subjname = subjname;
	}

	public String getPk_subj() {
		return pk_subj;
	}

	public void setPk_subj(String pk_subj) {
		this.pk_subj = pk_subj;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public Integer getCalcmode() {
		return calcmode;
	}

	public void setCalcmode(Integer calcmode) {
		this.calcmode = calcmode;
	}

	public DZFDouble getHsl() {
		return hsl;
	}

	public void setHsl(DZFDouble hsl) {
		this.hsl = hsl;
	}

	public String getPk_billcategory() {
		return pk_billcategory;
	}

	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPk_inventory_old() {
		return pk_inventory_old;
	}

	public void setPk_inventory_old(String pk_inventory_old) {
		this.pk_inventory_old = pk_inventory_old;
	}

	public String getPk_goodsinvenrela() {
		return pk_goodsinvenrela;
	}

	public String getSpmc() {
		return spmc;
	}

	public String getInvspec() {
		return invspec;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_goodsinvenrela(String pk_goodsinvenrela) {
		this.pk_goodsinvenrela = pk_goodsinvenrela;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_goodsinvenrela";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_goodsinvenrela";
	}

}
