package com.dzf.zxkj.app.model.req;

import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;

/**
 * 客户/订单相关请求vo
 * 
 * @author zhangj
 *
 */
public class BillCustomerBean extends UserBeanVO {

	// -------------税务信息--------
	private String sh;// 税号
	private String kpdh;// 开票电话
	private String khzh;// 开户帐号
	private String khh;// 开户行
	private String grdh;// 个人电话
	private String gryx;// 个人邮箱
	
	private String khname;// 客户名称
	private Long l_date;// 分页时间
	
	private Integer fplx;// 0 销项发票，1进项发票
	private String period;//期间 
	private String id;//id信息 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getFplx() {
		return fplx;
	}

	public void setFplx(Integer fplx) {
		this.fplx = fplx;
	}

	public Long getL_date() {
		return l_date;
	}

	public void setL_date(Long l_date) {
		this.l_date = l_date;
	}

	public String getKhname() {
		return khname;
	}

	public void setKhname(String khname) {
		this.khname = khname;
	}

	public String getGrdh() {
		return grdh;
	}

	public void setGrdh(String grdh) {
		this.grdh = grdh;
	}

	public String getGryx() {
		return gryx;
	}

	public void setGryx(String gryx) {
		this.gryx = gryx;
	}

	public String getSh() {
		return sh;
	}

	public void setSh(String sh) {
		this.sh = sh;
	}

	public String getKpdh() {
		return kpdh;
	}

	public void setKpdh(String kpdh) {
		this.kpdh = kpdh;
	}

	public String getKhzh() {
		return khzh;
	}

	public void setKhzh(String khzh) {
		this.khzh = khzh;
	}

	public String getKhh() {
		return khh;
	}

	public void setKhh(String khh) {
		this.khh = khh;
	}

}
