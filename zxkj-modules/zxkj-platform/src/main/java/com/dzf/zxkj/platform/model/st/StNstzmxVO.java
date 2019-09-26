package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 纳税调整项目明细表
 * @author WJX
 * */
public class StNstzmxVO extends StBaseVO{

	private String pk_nstzmx;//主键
//	private String vno;//序号
//	private String vprojectname;//项目名称
	private DZFDouble vzzje;//账载金额
	private DZFDouble vssje;//税收金额
	private DZFDouble vtzje;//调增金额
	private DZFDouble vtjje;//调减金额
	
	private String rp_vzzje;//账载金额
	private String rp_vssje;//税收金额
	private String rp_vtzje;//调增金额
	private String rp_vtjje;//调减金额
//	private String pk_corp;//公司主键
//	private String pk_project;//纳税项目主键
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
//	private String cyear;//会计年度
	
	
	

	public String getVdef1() {
		return vdef1;
	}

	public String getPk_nstzmx() {
		return pk_nstzmx;
	}

	public void setPk_nstzmx(String pk_nstzmx) {
		this.pk_nstzmx = pk_nstzmx;
	}

	public DZFDouble getVzzje() {
		return vzzje;
	}

	public void setVzzje(DZFDouble vzzje) {
		this.vzzje = vzzje;
	}

	public DZFDouble getVssje() {
		return vssje;
	}

	public void setVssje(DZFDouble vssje) {
		this.vssje = vssje;
	}

	public DZFDouble getVtzje() {
		return vtzje;
	}

	public void setVtzje(DZFDouble vtzje) {
		this.vtzje = vtzje;
	}

	public DZFDouble getVtjje() {
		return vtjje;
	}

	public void setVtjje(DZFDouble vtjje) {
		this.vtjje = vtjje;
	}

	public String getRp_vzzje() {
		return rp_vzzje;
	}

	public void setRp_vzzje(String rp_vzzje) {
		this.rp_vzzje = rp_vzzje;
	}

	public String getRp_vssje() {
		return rp_vssje;
	}

	public void setRp_vssje(String rp_vssje) {
		this.rp_vssje = rp_vssje;
	}

	public String getRp_vtzje() {
		return rp_vtzje;
	}

	public void setRp_vtzje(String rp_vtzje) {
		this.rp_vtzje = rp_vtzje;
	}

	public String getRp_vtjje() {
		return rp_vtjje;
	}

	public void setRp_vtjje(String rp_vtjje) {
		this.rp_vtjje = rp_vtjje;
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

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_nstzmx";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_nstzmx";
	}

}
