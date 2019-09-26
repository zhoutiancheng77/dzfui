package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 期间费用
 * @author WJX
 * */
public class StqjfyVO extends StBaseVO{
	
	  private String pk_qjfy;//varchar(100) NOT NULL,
//	  private String vno;//varchar(10) DEFAULT NULL,
//	  private String vprojectname;//varchar(100) DEFAULT NULL,
//	  private String vprojectcode;
//	  private String pk_project;
	  private DZFDouble vxsfy;//double(22,8) DEFAULT NULL,
	  private DZFDouble vxsfyjwzf;//double(22,8) DEFAULT NULL,
	  private DZFDouble vglfy;//double(22,8) DEFAULT NULL,
	  private DZFDouble vglfyjwzf;//double(22,8) DEFAULT NULL,
	  private DZFDouble vcwfy;//double(22,8) DEFAULT NULL,
	  private DZFDouble vcwfyjwzf;//double(22,8) DEFAULT NULL,
	  
	  //报表展现字段
	  private String rp_vxsfy;//double(22,8) DEFAULT NULL,
	  private String rp_vxsfyjwzf;//double(22,8) DEFAULT NULL,
	  private String rp_vglfy;//double(22,8) DEFAULT NULL,
	  private String rp_vglfyjwzf;//double(22,8) DEFAULT NULL,
	  private String rp_vcwfy;//double(22,8) DEFAULT NULL,
	  private String rp_vcwfyjwzf;//double(22,8) DEFAULT NULL,
	  
	  private String vdef5;//varchar(100) DEFAULT NULL,
	  private String vdef4;//varchar(100) DEFAULT NULL,
	  private String vdef3;//varchar(100) DEFAULT NULL,
	  private String vdef2;//varchar(100) DEFAULT NULL,
	  private String vdef1;//varchar(100) DEFAULT NULL,
//	  private String cyear;//int(11) DEFAULT NULL,
//	  private String pk_corp;//varchar(100) DEFAULT NULL,

	  
	  
	  
	  
	public String getPk_qjfy() {
		return pk_qjfy;
	}

	public void setPk_qjfy(String pk_qjfy) {
		this.pk_qjfy = pk_qjfy;
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
//
//	public String getVprojectcode() {
//		return vprojectcode;
//	}
//
//	public void setVprojectcode(String vprojectcode) {
//		this.vprojectcode = vprojectcode;
//	}
//
//	public String getPk_project() {
//		return pk_project;
//	}
//
//	public void setPk_project(String pk_project) {
//		this.pk_project = pk_project;
//	}

	public DZFDouble getVxsfy() {
		return vxsfy;
	}

	public void setVxsfy(DZFDouble vxsfy) {
		this.vxsfy = vxsfy;
	}

	public DZFDouble getVxsfyjwzf() {
		return vxsfyjwzf;
	}

	public void setVxsfyjwzf(DZFDouble vxsfyjwzf) {
		this.vxsfyjwzf = vxsfyjwzf;
	}

	public DZFDouble getVglfy() {
		return vglfy;
	}

	public void setVglfy(DZFDouble vglfy) {
		this.vglfy = vglfy;
	}

	public DZFDouble getVglfyjwzf() {
		return vglfyjwzf;
	}

	public void setVglfyjwzf(DZFDouble vglfyjwzf) {
		this.vglfyjwzf = vglfyjwzf;
	}

	public DZFDouble getVcwfy() {
		return vcwfy;
	}

	public void setVcwfy(DZFDouble vcwfy) {
		this.vcwfy = vcwfy;
	}

	public DZFDouble getVcwfyjwzf() {
		return vcwfyjwzf;
	}

	public void setVcwfyjwzf(DZFDouble vcwfyjwzf) {
		this.vcwfyjwzf = vcwfyjwzf;
	}

	public String getRp_vxsfy() {
		return rp_vxsfy;
	}

	public void setRp_vxsfy(String rp_vxsfy) {
		this.rp_vxsfy = rp_vxsfy;
	}

	public String getRp_vxsfyjwzf() {
		return rp_vxsfyjwzf;
	}

	public void setRp_vxsfyjwzf(String rp_vxsfyjwzf) {
		this.rp_vxsfyjwzf = rp_vxsfyjwzf;
	}

	public String getRp_vglfy() {
		return rp_vglfy;
	}

	public void setRp_vglfy(String rp_vglfy) {
		this.rp_vglfy = rp_vglfy;
	}

	public String getRp_vglfyjwzf() {
		return rp_vglfyjwzf;
	}

	public void setRp_vglfyjwzf(String rp_vglfyjwzf) {
		this.rp_vglfyjwzf = rp_vglfyjwzf;
	}

	public String getRp_vcwfy() {
		return rp_vcwfy;
	}

	public void setRp_vcwfy(String rp_vcwfy) {
		this.rp_vcwfy = rp_vcwfy;
	}

	public String getRp_vcwfyjwzf() {
		return rp_vcwfyjwzf;
	}

	public void setRp_vcwfyjwzf(String rp_vcwfyjwzf) {
		this.rp_vcwfyjwzf = rp_vcwfyjwzf;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

//	public String getCyear() {
//		return cyear;
//	}
//
//	public void setCyear(String cyear) {
//		this.cyear = cyear;
//	}
//
//	public String getPk_corp() {
//		return pk_corp;
//	}
//
//	public void setPk_corp(String pk_corp) {
//		this.pk_corp = pk_corp;
//	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_qjfy";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_qjfy";
	}

}
