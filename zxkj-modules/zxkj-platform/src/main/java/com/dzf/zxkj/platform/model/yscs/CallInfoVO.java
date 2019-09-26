package com.dzf.zxkj.platform.model.yscs;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

@SuppressWarnings({ "rawtypes", "serial" })
public class CallInfoVO extends SuperVO {
	
	private String pk_service_recored;
	
    //请求ip
    private String reqip;
	//请求系统
    private String reqsys;
    //请求类型编码
    private String reqtypecode;
    //请求类型名称
    private String reqtypename;
    //请求编号
    private String reqno;
    //请求时间
    private DZFDateTime reqtime;
    //响应时间
    private DZFDateTime reptime;
    //调用状态
    private String callstatus;
    //返回编码 
    private String repcode;
    //返回内容
    private String repmsg;
    //调用方法
    private String callmethod;
    
    private String pk_corp;
    
    private String inout;
    
    

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_service_recored";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_service_recored";
	}

	public String getReqip() {
		return reqip;
	}

	public void setReqip(String reqip) {
		this.reqip = reqip;
	}

	public String getReqsys() {
		return reqsys;
	}

	public void setReqsys(String reqsys) {
		this.reqsys = reqsys;
	}

	public String getReqtypecode() {
		return reqtypecode;
	}

	public void setReqtypecode(String reqtypecode) {
		this.reqtypecode = reqtypecode;
	}

	public String getReqtypename() {
		return reqtypename;
	}

	public void setReqtypename(String reqtypename) {
		this.reqtypename = reqtypename;
	}

	public String getReqno() {
		return reqno;
	}

	public void setReqno(String reqno) {
		this.reqno = reqno;
	}

	public DZFDateTime getReqtime() {
		return reqtime;
	}

	public void setReqtime(DZFDateTime reqtime) {
		this.reqtime = reqtime;
	}

	public DZFDateTime getReptime() {
		return reptime;
	}

	public void setReptime(DZFDateTime reptime) {
		this.reptime = reptime;
	}

	public String getCallstatus() {
		return callstatus;
	}

	public void setCallstatus(String callstatus) {
		this.callstatus = callstatus;
	}

	public String getRepcode() {
		return repcode;
	}

	public void setRepcode(String repcode) {
		this.repcode = repcode;
	}

	public String getRepmsg() {
		return repmsg;
	}

	public void setRepmsg(String repmsg) {
		this.repmsg = repmsg;
	}

	public String getCallmethod() {
		return callmethod;
	}

	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}

	public String getPk_service_recored() {
		return pk_service_recored;
	}

	public void setPk_service_recored(String pk_service_recored) {
		this.pk_service_recored = pk_service_recored;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	
	public String getInout() {
		return inout;
	}

	public void setInout(String inout) {
		this.inout = inout;
	}

}
