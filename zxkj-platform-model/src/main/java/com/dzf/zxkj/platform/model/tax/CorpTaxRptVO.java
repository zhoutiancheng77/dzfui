package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 申报表信息
 * 
 * */
@SuppressWarnings({ "rawtypes", "serial" })
public class CorpTaxRptVO extends SuperVO {
	
	@JsonProperty("txptcode")
	private String taxrptcode;//报表编码
	
	@JsonProperty("txptname")
	private String taxrptname;//报表名称
	
	@JsonProperty("id")
	private String pk_taxrpt;//主键
	
	@JsonProperty("gsid")
	private String pk_corp;//公司主键
	
	private String pk_taxrpttemplet;
	
//	private DZFBoolean isselect;

//	public DZFBoolean getIsselect() {
//        return isselect;
//    }

//    public void setIsselect(DZFBoolean isselect) {
//        this.isselect = isselect;
//    }

    public String getTaxrptcode() {
		return taxrptcode;
	}

	public void setTaxrptcode(String taxrptcode) {
		this.taxrptcode = taxrptcode;
	}

	public String getTaxrptname() {
		return taxrptname;
	}

	public void setTaxrptname(String taxrptname) {
		this.taxrptname = taxrptname;
	}

	public String getPk_taxrpt() {
		return pk_taxrpt;
	}

	public void setPk_taxrpt(String pk_taxrpt) {
		this.pk_taxrpt = pk_taxrpt;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_taxrpttemplet() {
		return pk_taxrpttemplet;
	}

	public void setPk_taxrpttemplet(String pk_taxrpttemplet) {
		this.pk_taxrpttemplet = pk_taxrpttemplet;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxrpt";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxrpt";
	}

	
}
