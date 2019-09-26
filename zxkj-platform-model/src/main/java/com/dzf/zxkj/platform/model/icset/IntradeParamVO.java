package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 库存查询条件VO
 */
public class IntradeParamVO extends SuperVO {

	public IntradeParamVO() {
	}

	private String pk_corp;// 公司
	@JsonProperty("id_ictrade_h")
	private String pk_ictrade_h;

	private UserVO uservo;// 用户VO

	private DZFDate clientdate;// 客户端日期
	@JsonProperty("begindate")
	private DZFDate begindate;// 开始日期
	@JsonProperty("enddate")
	private DZFDate enddate;// 结束日期
	private String djh1; // 单据号-开始
	private String djh2; // 单据号-结束
	private DZFDouble mny1; // 最小金额
	private DZFDouble mny2; // 最大金额
	private String qcorpid;// 供应商
	private String qcorpname;// 供应商名称
	private String qinvid;// 存货
	private String qinvname;// 存货名称
	private String cbusitype;// 业务类型
	private String startYear;// 按月查询-开始年份
	private String startMonth;// 按月查询-开始月份
	private String endYear;// 按月查询-结束年份
	private String endMonth;// 按月查询-结束月份
	private String serdate;
	private DZFBoolean iszg;//是否暂估

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public UserVO getUservo() {
		return uservo;
	}

	public void setUservo(UserVO uservo) {
		this.uservo = uservo;
	}

	public DZFDate getClientdate() {
		return clientdate;
	}

	public void setClientdate(DZFDate clientdate) {
		this.clientdate = clientdate;
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
	
	public DZFBoolean getIszg() {
		return iszg;
	}

	public void setIszg(DZFBoolean iszg) {
		this.iszg = iszg;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public DZFDouble getMny1() {
		return mny1;
	}

	public void setMny1(DZFDouble mny1) {
		this.mny1 = mny1;
	}

	public DZFDouble getMny2() {
		return mny2;
	}

	public void setMny2(DZFDouble mny2) {
		this.mny2 = mny2;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getSerdate() {
		return serdate;
	}

	public void setSerdate(String serdate) {
		this.serdate = serdate;
	}

	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}

	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}

	public String getDjh1() {
		return djh1;
	}

	public void setDjh1(String djh1) {
		this.djh1 = djh1;
	}

	public String getDjh2() {
		return djh2;
	}

	public void setDjh2(String djh2) {
		this.djh2 = djh2;
	}

	public String getQcorpid() {
		return qcorpid;
	}

	public void setQcorpid(String qcorpid) {
		this.qcorpid = qcorpid;
	}

	public String getQcorpname() {
		return qcorpname;
	}

	public void setQcorpname(String qcorpname) {
		this.qcorpname = qcorpname;
	}

	public String getCbusitype() {
		return cbusitype;
	}

	public void setCbusitype(String cbusitype) {
		this.cbusitype = cbusitype;
	}

	public String getQinvid() {
		return qinvid;
	}

	public void setQinvid(String qinvid) {
		this.qinvid = qinvid;
	}

	public String getQinvname() {
		return qinvname;
	}

	public void setQinvname(String qinvname) {
		this.qinvname = qinvname;
	}
}
