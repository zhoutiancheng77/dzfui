package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.common.model.SuperVO;

/****
 *  一般纳税人父vo
 * @author asoka
 *
 */
@SuppressWarnings("serial")
public class NtBaseVO extends SuperVO {

  private String vprojectname;//varchar(100) DEFAULT NULL,
  private String cyear;
  private String pk_corp;
  private String cmonth;
  private Integer vno;
	  
	  
	public String getCyear() {
		return cyear;
	}
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getVprojectname() {
		return vprojectname;
	}
	public void setVprojectname(String vprojectname) {
		this.vprojectname = vprojectname;
	}
	public String getCmonth() {
		return cmonth;
	}
	public void setCmonth(String cmonth) {
		this.cmonth = cmonth;
	}
	public Integer getVno() {
		return vno;
	}
	public void setVno(Integer vno) {
		this.vno = vno;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}
