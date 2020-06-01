package com.dzf.zxkj.app.model.resp.rptbean;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;


public class XjllbBeanVO extends SuperVO {
	private DZFDouble bqmny ;
	public DZFDouble getBqmny() {
		return bqmny;
	}
	public void setBqmny(DZFDouble bqmny) {
		this.bqmny = bqmny;
	}
	//��Ŀ
	private String projectname ;
	//������
	private DZFDouble bnmny ;
	public DZFDouble getBnmny() {
		return bnmny;
	}
	public void setBnmny(DZFDouble bnmny) {
		this.bnmny = bnmny;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
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
