package com.dzf.zxkj.platform.model.zncs;

import java.util.List;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 进销项发票本方公司参照vo
 * @author reny
 *
 */
public class CorpReferenceVO extends SuperVO {
	
	private String pk_corpreferenceset;//主键
	
	private String pk_corp;
	
	private String corpname;//公司名称
	
	private String taxnum;//纳税人识别号
	
	private String addressphone;//地址  电话
	
	private String banknum; //银行   银行账号
	
	private Integer isjinxiang;//0进项           1销项   
	
	private Integer dr;//删除标识
	
	
	
	
	

	public String getPk_corpreferenceset() {
		return pk_corpreferenceset;
	}

	public void setPk_corpreferenceset(String pk_corpreferenceset) {
		this.pk_corpreferenceset = pk_corpreferenceset;
	}

	
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getTaxnum() {
		return taxnum;
	}

	public void setTaxnum(String taxnum) {
		this.taxnum = taxnum;
	}

	public String getAddressphone() {
		return addressphone;
	}

	public void setAddressphone(String addressphone) {
		this.addressphone = addressphone;
	}

	public String getBanknum() {
		return banknum;
	}

	public void setBanknum(String banknum) {
		this.banknum = banknum;
	}

	public Integer getIsjinxiang() {
		return isjinxiang;
	}

	public void setIsjinxiang(Integer isjinxiang) {
		this.isjinxiang = isjinxiang;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		
		return "pk_corpreferenceset";
	}

	@Override
	public String getParentPKFieldName() {
		
		return null;
	}

	@Override
	public String getTableName() {
		
		return "ynt_corpreferenceset";
	}
	
}
