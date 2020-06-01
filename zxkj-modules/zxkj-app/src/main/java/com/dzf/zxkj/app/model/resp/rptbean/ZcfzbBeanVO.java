package com.dzf.zxkj.app.model.resp.rptbean;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;


public class ZcfzbBeanVO extends SuperVO {
	private float rowno;

	public float getRowno() {
		return rowno;
	}
	public void setRowno(float rowno) {
		this.rowno = rowno;
	}
	//��Ŀ
	private String projectname ;
	//��ĩ���
	private DZFDouble qmmny ;
	//������
	private DZFDouble ncmny ;
	public DZFDouble getNcmny() {
		return ncmny;
	}
	public void setNcmny(DZFDouble ncmny) {
		this.ncmny = ncmny;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public DZFDouble getQmmny() {
		return qmmny;
	}
	public void setQmmny(DZFDouble qmmny) {
		this.qmmny = qmmny;
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
