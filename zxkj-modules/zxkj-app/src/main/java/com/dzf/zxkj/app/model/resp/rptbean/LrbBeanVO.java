package com.dzf.zxkj.app.model.resp.rptbean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;


public class LrbBeanVO extends SuperVO {

	//��Ŀ
	private String projectname ;
	//���ڽ��
	private DZFDouble bqmny ;
	//�����ۼ�
	private DZFDouble bnmny ;
	//����
	private Integer level;
	
	
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public DZFDouble getBnmny() {
		return bnmny;
	}
	public void setBnmny(DZFDouble bnmny) {
		this.bnmny = bnmny;
	}
	public DZFDouble getBqmny() {
		return bqmny;
	}
	public void setBqmny(DZFDouble bqmny) {
		this.bqmny = bqmny;
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
