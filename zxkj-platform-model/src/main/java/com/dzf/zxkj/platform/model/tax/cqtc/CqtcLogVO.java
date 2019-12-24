package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
    * Cqtc_log 实体类
    * Tue Aug 15 10:12:21 CST 2017 qixiang
    */ 


public class CqtcLogVO extends SuperVO {
	private String pk_cqtclog;
	  //请求ip
    private String reqip;
	//请求系统
    private String reqsys;
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
    
	private DZFDateTime ts;

	private Integer dr;

	public void setPk_cqtclog(String pk_cqtclog){
		this.pk_cqtclog=pk_cqtclog;
	}

	public String getPk_cqtclog(){
		return pk_cqtclog;
	}

	public void setReqip(String reqip){
		this.reqip=reqip;
	}

	public String getReqip(){
		return reqip;
	}

	public void setReqsys(String reqsys){
		this.reqsys=reqsys;
	}

	public String getReqsys(){
		return reqsys;
	}

	public void setReqtypename(String reqtypename){
		this.reqtypename=reqtypename;
	}

	public String getReqtypename(){
		return reqtypename;
	}

	public void setReqno(String reqno){
		this.reqno=reqno;
	}

	public String getReqno(){
		return reqno;
	}

	public void setReqtime(DZFDateTime reqtime){
		this.reqtime=reqtime;
	}

	public DZFDateTime getReqtime(){
		return reqtime;
	}

	public void setReptime(DZFDateTime reptime){
		this.reptime=reptime;
	}

	public DZFDateTime getReptime(){
		return reptime;
	}

	public void setCallstatus(String callstatus){
		this.callstatus=callstatus;
	}

	public String getCallstatus(){
		return callstatus;
	}

	public void setRepcode(String repcode){
		this.repcode=repcode;
	}

	public String getRepcode(){
		return repcode;
	}

	public void setRepmsg(String repmsg){
		this.repmsg=repmsg;
	}

	public String getRepmsg(){
		return repmsg;
	}

	public void setCallmethod(String callmethod){
		this.callmethod=callmethod;
	}

	public String getCallmethod(){
		return callmethod;
	}

	public void setPk_corp(String pk_corp){
		this.pk_corp=pk_corp;
	}

	public String getPk_corp(){
		return pk_corp;
	}

	public void setInout(String inout){
		this.inout=inout;
	}

	public String getInout(){
		return inout;
	}

	public void setDr(Integer dr){
		this.dr=dr;
	}

	public Integer getDr(){
		return dr;
	}

	public void setTs(DZFDateTime ts){
		this.ts=ts;
	}

	public DZFDateTime getTs(){
		return ts;
	}
	
	@Override
	public String getParentPKFieldName() {
		return "";
	}
	
	@Override
	public String getPKFieldName() {
		return "pk_cqtclog";
	}
	
	@Override
	public String getTableName()  {
		return "CQTC_LOG";
	}
	
}

