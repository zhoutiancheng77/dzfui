package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class ImageDbVO extends SuperVO {
	
	private String pk_imagedb;
	private String pk_image_group;
	private Integer fpstyle;//发票类型索引
	private String fpstylecode;//发票类型编码
	private String  fpstylename;//发票类型名称
	private DZFDate dbdate;//打包日期
	private String cdbcorpid;//打包人
	private String pk_corp;//数据中心id
	
	private DZFDate fjdate;//分检日期
	private String fjr;//分检人
	private String groupid;//打包组标示
	private DZFBoolean issend;//是否发送
	private String szstylecode;//收支编码
	private String szstylename;//收支名称
	//
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private DZFDateTime ts;
	private Integer dr;

	public String getPk_imagedb() {
		return pk_imagedb;
	}

	public void setPk_imagedb(String pk_imagedb) {
		this.pk_imagedb = pk_imagedb;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public DZFDate getDbdate() {
		return dbdate;
	}

	public void setDbdate(DZFDate dbdate) {
		this.dbdate = dbdate;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
	

	public DZFBoolean getIssend() {
		return issend;
	}

	public void setIssend(DZFBoolean issend) {
		this.issend = issend;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_imagedb";
	}

	@Override
	public String getTableName() {
		return "ynt_imagedb";
	}

	public Integer getFpstyle() {
		return fpstyle;
	}

	public void setFpstyle(Integer fpstyle) {
		this.fpstyle = fpstyle;
	}

	public String getFpstylecode() {
		return fpstylecode;
	}

	public void setFpstylecode(String fpstylecode) {
		this.fpstylecode = fpstylecode;
	}

	public String getFpstylename() {
		return fpstylename;
	}

	public void setFpstylename(String fpstylename) {
		this.fpstylename = fpstylename;
	}

	public String getCdbcorpid() {
		return cdbcorpid;
	}

	public void setCdbcorpid(String cdbcorpid) {
		this.cdbcorpid = cdbcorpid;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDate getFjdate() {
		return fjdate;
	}

	public void setFjdate(DZFDate fjdate) {
		this.fjdate = fjdate;
	}

	public String getFjr() {
		return fjr;
	}

	public void setFjr(String fjr) {
		this.fjr = fjr;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getSzstylecode() {
		return szstylecode;
	}

	public void setSzstylecode(String szstylecode) {
		this.szstylecode = szstylecode;
	}

	public String getSzstylename() {
		return szstylename;
	}

	public void setSzstylename(String szstylename) {
		this.szstylename = szstylename;
	}

}
