package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 一般企业收入
 * @author WJX
 * */
public class StYbqysrVO extends StBaseVO{

	private String pk_ybqysr;//主键
//	private String vno;//序号
//	private String vprojectname;//项目名称
	private DZFDouble vmny;//金额
	private String rp_vmny;//金额（报表用金额）
//	private String pk_corp;//公司主键
//	private String pk_project;//纳税项目主键
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
//	private String cyear;//会计年度
	
	public void setRp_vmny(String rp_vmny) {
		this.rp_vmny = rp_vmny;
	}
	
	public String getRp_vmny() {
		return rp_vmny;
	}
	
//	public void setCyear(String cyear) {
//		this.cyear = cyear;
//	}
//	
//	public String getCyear() {
//		return cyear;
//	}
	
	
	public String getPk_ybqysr() {
		return pk_ybqysr;
	}

	public void setPk_ybqysr(String pk_ybqysr) {
		this.pk_ybqysr = pk_ybqysr;
	}

//	public String getVno() {
//		return vno;
//	}
//
//	public void setVno(String vno) {
//		this.vno = vno;
//	}
//
//	public String getVprojectname() {
//		return vprojectname;
//	}
//
//	public void setVprojectname(String vprojectname) {
//		this.vprojectname = vprojectname;
//	}

	public DZFDouble getVmny() {
		return vmny;
	}

	public void setVmny(DZFDouble vmny) {
		this.vmny = vmny;
	}

//	public String getPk_corp() {
//		return pk_corp;
//	}
//
//	public void setPk_corp(String pk_corp) {
//		this.pk_corp = pk_corp;
//	}
//
//	public String getPk_project() {
//		return pk_project;
//	}
//
//	public void setPk_project(String pk_project) {
//		this.pk_project = pk_project;
//	}

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
		return "pk_ybqysr";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_ybqysr";
	}

}
