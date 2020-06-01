package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.lang.DZFBoolean;

import java.util.List;
import java.util.Map;
/**
 * 业务凭证信息
 * 
 * @author zhangj
 *
 */
public class BusinessResonseBeanVO extends ResponseBaseBeanVO {

	private String pzmsg;// 凭证信息
	private Integer count;// 个数
	private String orderid;// 订单编号
	private List<UserLsBean> staff;// 员工

	private UserLsBean boss;// boss

	private UserLsBean cash;// 出纳

	private DZFBoolean use;// 是否启用审批流

	private String qrid;// 二维码id信息
	
	private String qrstatus;//二维码状态
	
	//---------首页信息------------
	private Map<String,Object> indexmsg ;
	
	private List<MoreServiceHVo> morelist;//更多服务
	
	public List<MoreServiceHVo> getMorelist() {
		return morelist;
	}

	public void setMorelist(List<MoreServiceHVo> morelist) {
		this.morelist = morelist;
	}

	public Map<String, Object> getIndexmsg() {
		return indexmsg;
	}

	public void setIndexmsg(Map<String, Object> indexmsg) {
		this.indexmsg = indexmsg;
	}

	public String getQrstatus() {
		return qrstatus;
	}

	public void setQrstatus(String qrstatus) {
		this.qrstatus = qrstatus;
	}

	public String getQrid() {
		return qrid;
	}

	public void setQrid(String qrid) {
		this.qrid = qrid;
	}

	public DZFBoolean getUse() {
		return use;
	}

	public void setUse(DZFBoolean use) {
		this.use = use;
	}

	public List<UserLsBean> getStaff() {
		return staff;
	}

	public void setStaff(List<UserLsBean> staff) {
		this.staff = staff;
	}

	public UserLsBean getBoss() {
		return boss;
	}

	public void setBoss(UserLsBean boss) {
		this.boss = boss;
	}

	public UserLsBean getCash() {
		return cash;
	}

	public void setCash(UserLsBean cash) {
		this.cash = cash;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getPzmsg() {
		return pzmsg;
	}

	public void setPzmsg(String pzmsg) {
		this.pzmsg = pzmsg;
	}
}
