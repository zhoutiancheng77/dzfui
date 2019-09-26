package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 
 * @author:
 */
public class AdjustExrateVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("pk_cur")
	private String pk_currency;// 币种
	@JsonProperty("id_rate")
	private String pk_exrate;
	@JsonProperty("erate")
	private DZFDouble exrate;// 汇率
	@JsonProperty("adjrate")
	private DZFDouble adjustrate;// 调整汇率
	private String currname;
	private String currcode;
	@JsonProperty("cmode")
	private Integer convmode;
	
	private String pk_gs1;
	private DZFBoolean iscbjz;
	private DZFBoolean ishdsytz;
	private DZFBoolean isqjsyjz;
	private DZFBoolean iszjjt;
	private String  pk_id1;
	private String qj1;
	


	public Integer getConvmode() {
		return convmode;
	}

	public void setConvmode(Integer convmode) {
		this.convmode = convmode;
	}

	public String getCurrname() {
		return currname;
	}

	public void setCurrname(String currname) {
		this.currname = currname;
	}

	public String getCurrcode() {
		return currcode;
	}

	public void setCurrcode(String currcode) {
		this.currcode = currcode;
	}

	public DZFDouble getExrate() {
		return exrate;
	}

	public void setExrate(DZFDouble exrate) {
		this.exrate = exrate;
	}

	public DZFDouble getAdjustrate() {
		return adjustrate;
	}

	public void setAdjustrate(DZFDouble adjustrate) {
		this.adjustrate = adjustrate;
	}



	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}




	public String getPk_exrate() {
		return pk_exrate;
	}

	public void setPk_exrate(String pk_exrate) {
		this.pk_exrate = pk_exrate;
	}







	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
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
	 * 
	 * @return java.lang.String
	 */
	public String getPKFieldName() {

		return "";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {

		return "";
	}

	/**
	 * 使用主键字段进行初始化的构造子.
	 * 
	 */
	public AdjustExrateVO() {
		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * 
	 * @param Pk_exratescheme
	 *            主键值
	 */
	public AdjustExrateVO(String newPk_exrate) {
		super();

		// 为主键字段赋值:
		pk_exrate = newPk_exrate;
	}




	/**
	 * 返回数值对象的显示名称.
	 * 
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	public String getEntityName() {

		return "Exratescheme";
	}

	public String getPk_gs1() {
		return pk_gs1;
	}

	public void setPk_gs1(String pk_gs1) {
		this.pk_gs1 = pk_gs1;
	}

	public DZFBoolean getIscbjz() {
		return iscbjz;
	}

	public void setIscbjz(DZFBoolean iscbjz) {
		this.iscbjz = iscbjz;
	}

	public DZFBoolean getIshdsytz() {
		return ishdsytz;
	}

	public void setIshdsytz(DZFBoolean ishdsytz) {
		this.ishdsytz = ishdsytz;
	}

	public DZFBoolean getIsqjsyjz() {
		return isqjsyjz;
	}

	public void setIsqjsyjz(DZFBoolean isqjsyjz) {
		this.isqjsyjz = isqjsyjz;
	}

	public DZFBoolean getIszjjt() {
		return iszjjt;
	}

	public void setIszjjt(DZFBoolean iszjjt) {
		this.iszjjt = iszjjt;
	}

	public String getPk_id1() {
		return pk_id1;
	}

	public void setPk_id1(String pk_id1) {
		this.pk_id1 = pk_id1;
	}

	public String getQj1() {
		return qj1;
	}

	public void setQj1(String qj1) {
		this.qj1 = qj1;
	}
	
	
}