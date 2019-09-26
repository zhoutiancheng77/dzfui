package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxRptTempletPosVO extends SuperVO {

	
	//主键	
	private String pk_taxrpttempletpos;
	//税务申报模板表主键	
	private String pk_taxrpttemplet;
	//公司主键
	private String pk_corp;
	//行	
	private Integer rptrow;
	//列
	private Integer rptcol;
	//数据项	
	private String itemkey;
	//数据项名称	
	private String itemname;
	//数据项期初变量名
	private String itemkeyinitname;
	//大账房公式	
	private String dzfformula;
	//spread公式	
	private String spreadformula;
	//是否锁定	
	private DZFBoolean readonly;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	
	public TaxRptTempletPosVO() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxrpttemplet";
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxrpttempletpos";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxrpttempletpos";
	}

	public String getPk_taxrpttempletpos() {
		return pk_taxrpttempletpos;
	}

	public String getPk_taxrpttemplet() {
		return pk_taxrpttemplet;
	}

	public Integer getRptrow() {
		return rptrow;
	}

	public Integer getRptcol() {
		return rptcol;
	}

	public String getItemkey() {
		return itemkey;
	}

	public String getItemname() {
		return itemname;
	}

	public String getDzfformula() {
		return dzfformula;
	}

	public String getSpreadformula() {
		return spreadformula;
	}

	public DZFBoolean getReadonly() {
		return readonly;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxrpttempletpos(String pk_taxrpttempletpos) {
		this.pk_taxrpttempletpos = pk_taxrpttempletpos;
	}

	public void setPk_taxrpttemplet(String pk_taxrpttemplet) {
		this.pk_taxrpttemplet = pk_taxrpttemplet;
	}

	public void setRptrow(Integer rptrow) {
		this.rptrow = rptrow;
	}

	public void setRptcol(Integer rptcol) {
		this.rptcol = rptcol;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public void setDzfformula(String dzfformula) {
		this.dzfformula = dzfformula;
	}

	public void setSpreadformula(String spreadformula) {
		this.spreadformula = spreadformula;
	}

	public void setReadonly(DZFBoolean readonly) {
		this.readonly = readonly;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
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
	public String getItemkeyinitname() {
		return itemkeyinitname;
	}

	public void setItemkeyinitname(String itemkeyinitname) {
		this.itemkeyinitname = itemkeyinitname;
	}

}
