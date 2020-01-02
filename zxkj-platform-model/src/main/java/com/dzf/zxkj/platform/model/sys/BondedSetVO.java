package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 一键报税设置VO
 * @author yin698
 *
 */
public class BondedSetVO extends SuperVO {
	
	@JsonProperty("pk_id")
	private String pk_bondedset;//主键
	@JsonProperty("corpid")
	private String pk_corp;//公司			
	@JsonProperty("inum")
	private String identificationnum;//企业识别号
	@JsonProperty("corpname")
	private String corpname;//公司名称
	@JsonProperty("zamount")
	private DZFDouble zeroamount;//零申报金额
	@JsonProperty("gamount")
	private DZFDouble generalamount;//一般人
	@JsonProperty("samount")
	private DZFDouble scaleamount;//小规模
	@JsonProperty("bdate")
	private DZFDate begindate;//生效时间
	@JsonProperty("edate")
	private DZFDate enddate;//失效时间
	@JsonProperty("puser")
	private String pk_user;//最后修改人
	@JsonProperty("uname")
	private String username;//名称
	@JsonProperty("editdate")
	private DZFDateTime editdate;//修改时间
	private Integer DR;
	private DZFDateTime TS;
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_bondedset";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "wz_bondedset";
	}   
	
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public String getPk_bondedset() {
		return pk_bondedset;
	}
	public void setPk_bondedset(String pk_bondedset) {
		this.pk_bondedset = pk_bondedset;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getIdentificationnum() {
		return identificationnum;
	}
	public void setIdentificationnum(String identificationnum) {
		this.identificationnum = identificationnum;
	}
	public DZFDouble getZeroamount() {
		return zeroamount;
	}
	public void setZeroamount(DZFDouble zeroamount) {
		this.zeroamount = zeroamount;
	}
	public DZFDouble getGeneralamount() {
		return generalamount;
	}
	public void setGeneralamount(DZFDouble generalamount) {
		this.generalamount = generalamount;
	}
	public DZFDouble getScaleamount() {
		return scaleamount;
	}
	public void setScaleamount(DZFDouble scaleamount) {
		this.scaleamount = scaleamount;
	}
	public DZFDate getBegindate() {
		return begindate;
	}
	public void setBegindate(DZFDate begindate) {
		this.begindate = begindate;
	}
	public DZFDate getEnddate() {
		return enddate;
	}
	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}
	public String getPk_user() {
		return pk_user;
	}
	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public DZFDateTime getEditdate() {
		return editdate;
	}
	public void setEditdate(DZFDateTime editdate) {
		this.editdate = editdate;
	}
	public Integer getDR() {
		return DR;
	}
	public void setDR(Integer dR) {
		DR = dR;
	}
	public DZFDateTime getTS() {
		return TS;
	}
	public void setTS(DZFDateTime tS) {
		TS = tS;
	}


	
}
