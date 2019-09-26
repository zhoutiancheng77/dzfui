package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 资产原值变更 </b>
 */
@SuppressWarnings("serial")
public class ValuemodifyVO extends SuperVO {
	@JsonProperty("id_corp")
	private String pk_corp;
	@JsonProperty("ts")
	private DZFDateTime ts;
	@JsonProperty("coid")
	private String coperatorid;
	@JsonProperty("vappid")
	private String vapproveid;
	@JsonProperty("vappnote")
	private String vapprovenote;
	@JsonProperty("chgvalue")
	private DZFDouble changevalue;
	@JsonProperty("dappdate")
	private DZFDate dapprovedate;
	@JsonProperty("nvalue")
	private DZFDouble newvalue;
	@JsonProperty("id_assetcard")
	private String pk_assetcard;
	@JsonProperty("bdate")
	private DZFDate businessdate;
	@JsonProperty("id_assetvaluemodify")
	private String pk_assetvaluemodify;
	@JsonProperty("id_billtype")
	private String pk_billtype;
	@JsonProperty("vstatus")
	private Integer vbillstatus;
	@JsonProperty("vno")
	private String vbillno;
	@JsonProperty("dr")
	private Integer dr;
	@JsonProperty("ddate")
	private DZFDate doperatedate;
	@JsonProperty("ovalue")
	private DZFDouble originalvalue;
	@JsonProperty("togl")
	private DZFBoolean istogl;
	@JsonProperty("id_voucher")
	private String pk_voucher;
	@JsonProperty("settle")
	private DZFBoolean issettle;
	@JsonProperty("pk_assetcard_name")
	private String pk_assetcard_name;
	@JsonProperty("voucherno")
	private String voucherno;
	@JsonProperty("zckm_id")
	private String pk_zckm;//资产pk
	private String zckm;
	@JsonProperty("bgkm_id")
	private String pk_bgkm;//变更科目pk
	private String bgkm;
	
	@JsonProperty("vf1")
	private String vdef1;
	@JsonProperty("vf2")
	private String vdef2;
	@JsonProperty("vf3")
	private String vdef3;
	@JsonProperty("vf4")
	private String vdef4;
	@JsonProperty("vf5")
	private String vdef5;
	@JsonProperty("vf6")
	private String vdef6;
	@JsonProperty("vf7")
	private String vdef7;
	@JsonProperty("vf8")
	private String vdef8;
	@JsonProperty("vf9")
	private String vdef9;
	@JsonProperty("vf10")
	private String vdef10;

	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String VAPPROVEID = "vapproveid";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String CHANGEVALUE = "changevalue";
	public static final String VDEF7 = "vdef7";
	public static final String DAPPROVEDATE = "dapprovedate";
	public static final String NEWVALUE = "newvalue";
	public static final String PK_ASSETCARD = "pk_assetcard";
	public static final String BUSINESSDATE = "businessdate";
	public static final String PK_ASSETVALUEMODIFY = "pk_assetvaluemodify";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String VBILLSTATUS = "vbillstatus";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String VBILLNO = "vbillno";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String ORIGINALVALUE = "originalvalue";
	
	
	public String getZckm() {
		return zckm;
	}

	public void setZckm(String zckm) {
		this.zckm = zckm;
	}

	public String getBgkm() {
		return bgkm;
	}

	public void setBgkm(String bgkm) {
		this.bgkm = bgkm;
	}

	public String getPk_zckm() {
		return pk_zckm;
	}

	public void setPk_zckm(String pk_zckm) {
		this.pk_zckm = pk_zckm;
	}

	public String getPk_bgkm() {
		return pk_bgkm;
	}

	public void setPk_bgkm(String pk_bgkm) {
		this.pk_bgkm = pk_bgkm;
	}

	public String getVoucherno() {
		return voucherno;
	}

	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}

	public String getPk_assetcard_name() {
		return pk_assetcard_name;
	}

	public void setPk_assetcard_name(String pk_assetcard_name) {
		this.pk_assetcard_name = pk_assetcard_name;
	}

	/**
	 * 属性pk_corp的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getPk_corp() {
		return pk_corp;
	}

	/**
	 * 属性pk_corp的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newPk_corp
	 *            String
	 */
	public void setPk_corp(String newPk_corp) {
		this.pk_corp = newPk_corp;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newTs
	 *            DZFDateTime
	 */
	public void setTs(DZFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * 属性vdef9的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef9() {
		return vdef9;
	}

	/**
	 * 属性vdef9的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef9
	 *            String
	 */
	public void setVdef9(String newVdef9) {
		this.vdef9 = newVdef9;
	}

	public DZFBoolean getIstogl() {
		return istogl;
	}

	public void setIstogl(DZFBoolean istogl) {
		this.istogl = istogl;
	}

	public String getPk_voucher() {
		return pk_voucher;
	}

	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}

	/**
	 * 属性coperatorid的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getCoperatorid() {
		return coperatorid;
	}

	/**
	 * 属性coperatorid的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newCoperatorid
	 *            String
	 */
	public void setCoperatorid(String newCoperatorid) {
		this.coperatorid = newCoperatorid;
	}

	/**
	 * 属性vdef1的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef1() {
		return vdef1;
	}

	/**
	 * 属性vdef1的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef1
	 *            String
	 */
	public void setVdef1(String newVdef1) {
		this.vdef1 = newVdef1;
	}

	/**
	 * 属性vdef8的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef8() {
		return vdef8;
	}

	/**
	 * 属性vdef8的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef8
	 *            String
	 */
	public void setVdef8(String newVdef8) {
		this.vdef8 = newVdef8;
	}

	/**
	 * 属性vdef10的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef10() {
		return vdef10;
	}

	/**
	 * 属性vdef10的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef10
	 *            String
	 */
	public void setVdef10(String newVdef10) {
		this.vdef10 = newVdef10;
	}

	/**
	 * 属性vapproveid的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVapproveid() {
		return vapproveid;
	}

	/**
	 * 属性vapproveid的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVapproveid
	 *            String
	 */
	public void setVapproveid(String newVapproveid) {
		this.vapproveid = newVapproveid;
	}

	/**
	 * 属性vapprovenote的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVapprovenote() {
		return vapprovenote;
	}

	/**
	 * 属性vapprovenote的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVapprovenote
	 *            String
	 */
	public void setVapprovenote(String newVapprovenote) {
		this.vapprovenote = newVapprovenote;
	}

	/**
	 * 属性changevalue的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getChangevalue() {
		return changevalue;
	}

	/**
	 * 属性changevalue的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newChangevalue
	 *            DZFDouble
	 */
	public void setChangevalue(DZFDouble newChangevalue) {
		this.changevalue = newChangevalue;
	}

	/**
	 * 属性vdef7的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef7() {
		return vdef7;
	}

	/**
	 * 属性vdef7的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef7
	 *            String
	 */
	public void setVdef7(String newVdef7) {
		this.vdef7 = newVdef7;
	}

	public DZFBoolean getIssettle() {
		return issettle;
	}

	public void setIssettle(DZFBoolean issettle) {
		this.issettle = issettle;
	}

	/**
	 * 属性dapprovedate的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate() {
		return dapprovedate;
	}

	/**
	 * 属性dapprovedate的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newDapprovedate
	 *            DZFDate
	 */
	public void setDapprovedate(DZFDate newDapprovedate) {
		this.dapprovedate = newDapprovedate;
	}

	/**
	 * 属性newvalue的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getNewvalue() {
		return newvalue;
	}

	/**
	 * 属性newvalue的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newNewvalue
	 *            DZFDouble
	 */
	public void setNewvalue(DZFDouble newNewvalue) {
		this.newvalue = newNewvalue;
	}

	/**
	 * 属性pk_assetcard的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getPk_assetcard() {
		return pk_assetcard;
	}

	/**
	 * 属性pk_assetcard的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newPk_assetcard
	 *            String
	 */
	public void setPk_assetcard(String newPk_assetcard) {
		this.pk_assetcard = newPk_assetcard;
	}

	/**
	 * 属性businessdate的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDate
	 */
	public DZFDate getBusinessdate() {
		return businessdate;
	}

	/**
	 * 属性businessdate的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newBusinessdate
	 *            DZFDate
	 */
	public void setBusinessdate(DZFDate newBusinessdate) {
		this.businessdate = newBusinessdate;
	}

	/**
	 * 属性pk_assetvaluemodify的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getPk_assetvaluemodify() {
		return pk_assetvaluemodify;
	}

	/**
	 * 属性pk_assetvaluemodify的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newPk_assetvaluemodify
	 *            String
	 */
	public void setPk_assetvaluemodify(String newPk_assetvaluemodify) {
		this.pk_assetvaluemodify = newPk_assetvaluemodify;
	}

	/**
	 * 属性vdef2的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef2() {
		return vdef2;
	}

	/**
	 * 属性vdef2的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef2
	 *            String
	 */
	public void setVdef2(String newVdef2) {
		this.vdef2 = newVdef2;
	}

	/**
	 * 属性vdef5的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef5() {
		return vdef5;
	}

	/**
	 * 属性vdef5的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef5
	 *            String
	 */
	public void setVdef5(String newVdef5) {
		this.vdef5 = newVdef5;
	}

	/**
	 * 属性pk_billtype的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getPk_billtype() {
		return pk_billtype;
	}

	/**
	 * 属性pk_billtype的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newPk_billtype
	 *            String
	 */
	public void setPk_billtype(String newPk_billtype) {
		this.pk_billtype = newPk_billtype;
	}

	/**
	 * 属性vbillstatus的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDouble
	 */
	public Integer getVbillstatus() {
		return vbillstatus;
	}

	/**
	 * 属性vbillstatus的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVbillstatus
	 *            DZFDouble
	 */
	public void setVbillstatus(Integer newVbillstatus) {
		this.vbillstatus = newVbillstatus;
	}

	/**
	 * 属性vdef3的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef3() {
		return vdef3;
	}

	/**
	 * 属性vdef3的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef3
	 *            String
	 */
	public void setVdef3(String newVdef3) {
		this.vdef3 = newVdef3;
	}

	/**
	 * 属性vdef6的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef6() {
		return vdef6;
	}

	/**
	 * 属性vdef6的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef6
	 *            String
	 */
	public void setVdef6(String newVdef6) {
		this.vdef6 = newVdef6;
	}

	/**
	 * 属性vbillno的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVbillno() {
		return vbillno;
	}

	/**
	 * 属性vbillno的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVbillno
	 *            String
	 */
	public void setVbillno(String newVbillno) {
		this.vbillno = newVbillno;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDouble
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newDr
	 *            DZFDouble
	 */
	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性doperatedate的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	/**
	 * 属性doperatedate的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newDoperatedate
	 *            DZFDate
	 */
	public void setDoperatedate(DZFDate newDoperatedate) {
		this.doperatedate = newDoperatedate;
	}

	/**
	 * 属性vdef4的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return String
	 */
	public String getVdef4() {
		return vdef4;
	}

	/**
	 * 属性vdef4的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newVdef4
	 *            String
	 */
	public void setVdef4(String newVdef4) {
		this.vdef4 = newVdef4;
	}

	/**
	 * 属性originalvalue的Getter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getOriginalvalue() {
		return originalvalue;
	}

	/**
	 * 属性originalvalue的Setter方法. 创建日期:2014-10-23 16:30:41
	 * 
	 * @param newOriginalvalue
	 *            DZFDouble
	 */
	public void setOriginalvalue(DZFDouble newOriginalvalue) {
		this.originalvalue = newOriginalvalue;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2014-10-23 16:30:41
	 * 
	 * @return java.lang.String
	 */
	public String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2014-10-23 16:30:41
	 *
	 * @return java.lang.String
	 */
	public String getPKFieldName() {
		return "pk_assetvaluemodify";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2014-10-23 16:30:41
	 *
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_valuemodify";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2014-10-23 16:30:41
	 */
	public ValuemodifyVO() {
		super();
	}
}
