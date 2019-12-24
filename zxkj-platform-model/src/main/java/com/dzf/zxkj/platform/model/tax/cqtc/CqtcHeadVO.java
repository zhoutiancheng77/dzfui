package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.model.SuperVO;

@SuppressWarnings({ "rawtypes", "serial" })
public class CqtcHeadVO extends SuperVO {
	

    //请求来源系统
    private String reqsys;
    //请求时间
    private String reqtime;
    //请求编号
    private String reqno;//yyyyMMdd-no(20160912000001)
    //请求权限校验码
    private String reqauthkey;
    
	public String getReqtime() {
		return reqtime;
	}

	public void setReqtime(String reqtime) {
		this.reqtime = reqtime;
	}

	public String getReqauthkey() {
		return reqauthkey;
	}

	public void setReqauthkey(String reqauthkey) {
		this.reqauthkey = reqauthkey;
	}

	public String getReqsys() {
		return reqsys;
	}

	public void setReqsys(String reqsys) {
		this.reqsys = reqsys;
	}

	public String getReqno() {
		return reqno;
	}

	public void setReqno(String reqno) {
		this.reqno = reqno;
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

}
