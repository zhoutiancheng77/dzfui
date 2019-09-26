package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;

/*
 * 个性化设置，保存到数据库
 */
public class PersonalSetVO extends SuperVO {
	private String pk_personal;
	private String pk_corp;
	//个性化设置XML格式数据
	private String settings;
	private Integer dr;
	public String getPk_personal() {
		return pk_personal;
	}

	public void setPk_personal(String pk_personal) {
		this.pk_personal = pk_personal;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getSettings() {
		return settings;
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_personal";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_personalset";
	}

}
