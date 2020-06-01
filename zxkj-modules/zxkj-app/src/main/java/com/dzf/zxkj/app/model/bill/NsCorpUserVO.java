package com.dzf.zxkj.app.model.bill;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 用户开票信息
 * @author zhangj
 *
 */
public class NsCorpUserVO extends SuperVO {
	
	public static final String TABLE_NAME = "bd_corp_ns_app_user";
	public static final String PK_FIELD = "pk_corp_ns_app_user";

	private String pk_corp_ns_app_user;//
	private String pk_corp;//
	private String pk_temp_corp;//临时公司id
	private String taxcode;// 纳税号
	private String iot;//iot号
	private String vbankname;// 开户行
	private String vbankcode;// 开户帐号
	private String vinvoicetype;// 票种类型
	private String phone1;//
	private String postaddr;//
	private DZFDateTime ts;//
	private Integer dr;//
	private String pk_user;//用户id
	private String phone2;//个人电话
	private String vmail;//邮箱
	
	
	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public String getPk_user() {
		return pk_user;
	}

	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getVmail() {
		return vmail;
	}

	public void setVmail(String vmail) {
		this.vmail = vmail;
	}

	public String getIot() {
		return iot;
	}

	public void setIot(String iot) {
		this.iot = iot;
	}

	public String getVinvoicetype() {
		return vinvoicetype;
	}

	public void setVinvoicetype(String vinvoicetype) {
		this.vinvoicetype = vinvoicetype;
	}

	public String getPk_corp_ns_app_user() {
		return pk_corp_ns_app_user;
	}

	public void setPk_corp_ns_app_user(String pk_corp_ns_app_user) {
		this.pk_corp_ns_app_user = pk_corp_ns_app_user;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getTaxcode() {
		return taxcode;
	}

	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}

	public String getVbankname() {
		return vbankname;
	}

	public void setVbankname(String vbankname) {
		this.vbankname = vbankname;
	}

	public String getVbankcode() {
		return vbankcode;
	}

	public void setVbankcode(String vbankcode) {
		this.vbankcode = vbankcode;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPostaddr() {
		return postaddr;
	}

	public void setPostaddr(String postaddr) {
		this.postaddr = postaddr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
