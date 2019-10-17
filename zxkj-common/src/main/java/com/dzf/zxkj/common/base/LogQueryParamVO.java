package com.dzf.zxkj.common.base;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogQueryParamVO extends SuperVO {

	@JsonProperty("begindate")
	private DZFDate begindate1;// 开始日期

	@JsonProperty("enddate")
	private DZFDate enddate;// 结束日期

	private String otpye;// 操作类型

	private String omsg;//操作信息
	
	private String opeuser;//操作用户
	
	@JsonProperty("cid")
	private String pk_corp;//公司信息
	
	@JsonProperty("ident")
	private Integer sys_ident;//系统类型0:集团,1:管理端,2会计端,3、加盟商系统

	public Integer getSys_ident() {
        return sys_ident;
    }

    public void setSys_ident(Integer sys_ident) {
        this.sys_ident = sys_ident;
    }

    public DZFDate getBegindate1() {
		return begindate1;
	}

	public void setBegindate1(DZFDate begindate1) {
		this.begindate1 = begindate1;
	}

	public DZFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}

	public String getOtpye() {
		return otpye;
	}

	public void setOtpye(String otpye) {
		this.otpye = otpye;
	}

	public String getOmsg() {
		return omsg;
	}

	public void setOmsg(String omsg) {
		this.omsg = omsg;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getOpeuser() {
		return opeuser;
	}

	public void setOpeuser(String opeuser) {
		this.opeuser = opeuser;
	}
}
