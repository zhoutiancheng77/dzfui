package com.dzf.zxkj.app.model.app.remote;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;

@SuppressWarnings("rawtypes")
public class AppCorpCtrlVO extends SuperVO {

	private static final long serialVersionUID = 2793841044185690836L;
	
	private String pk_corpk;//客户主键
	
	private String fun_name;//节点名称
	
	private DZFBoolean ishasupload;//“上传图片”功能节点权限
	
	private DZFBoolean ishasmake;//“填制凭证”功能节点权限

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getFun_name() {
		return fun_name;
	}

	public void setFun_name(String fun_name) {
		this.fun_name = fun_name;
	}

	public DZFBoolean getIshasupload() {
		return ishasupload;
	}

	public void setIshasupload(DZFBoolean ishasupload) {
		this.ishasupload = ishasupload;
	}

	public DZFBoolean getIshasmake() {
		return ishasmake;
	}

	public void setIshasmake(DZFBoolean ishasmake) {
		this.ishasmake = ishasmake;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
