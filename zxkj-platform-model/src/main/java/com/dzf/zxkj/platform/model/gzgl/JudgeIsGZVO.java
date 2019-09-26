package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;

public class JudgeIsGZVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_judgeisgz;
	private String pk_corp;
	private String qj; 
	private Integer dr;
	private DZFBoolean isGz;

	public String getPk_judgeisgz() {
		return pk_judgeisgz;
	}

	public void setPk_judgeisgz(String pk_judgeisgz) {
		this.pk_judgeisgz = pk_judgeisgz;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getQj() {
		return qj;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFBoolean getIsGz() {
		return isGz;
	}

	public void setIsGz(DZFBoolean isGz) {
		this.isGz = isGz;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_judgeisgz";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_judgeisgz";
	}

}
