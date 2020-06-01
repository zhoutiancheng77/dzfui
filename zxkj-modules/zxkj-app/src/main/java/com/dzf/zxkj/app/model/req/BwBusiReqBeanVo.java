package com.dzf.zxkj.app.model.req;


import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;

/**
 * 百望请求参数
 * 
 * @author zhangj
 *
 */
public class BwBusiReqBeanVo extends UserBeanVO {

	private String sh;
	private String khh;
	private String khzh;// 开户帐号
	private String lxdh;// 联系电话
	private String kplx;// 开票类型

	
	public String getKplx() {
		return kplx;
	}

	public void setKplx(String kplx) {
		this.kplx = kplx;
	}

	public String getSh() {
		return sh;
	}

	public void setSh(String sh) {
		this.sh = sh;
	}

	public String getKhh() {
		return khh;
	}

	public void setKhh(String khh) {
		this.khh = khh;
	}

	public String getKhzh() {
		return khzh;
	}

	public void setKhzh(String khzh) {
		this.khzh = khzh;
	}

	public String getLxdh() {
		return lxdh;
	}

	public void setLxdh(String lxdh) {
		this.lxdh = lxdh;
	}

}
