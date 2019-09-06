package com.dzf.zxkj.platform.model.am.zcgl;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 资产卡片显示列
 * 
 * @author zhangj
 *
 */
public class AssetCardDisplayColumnVO extends SuperVO {

	public static final String TABLE_NAME = "ynt_assetcard_displaycolumn";

	public static final String PK_FIELD = "pk_assetcard_displaycolumn";

	private String pk_assetcard_displaycolumn;//
	private String pk_corp;//
	private String setting;//设置
	private Integer dr;//
	private DZFDateTime ts;//

	public String getPk_assetcard_displaycolumn() {
		return pk_assetcard_displaycolumn;
	}

	public void setPk_assetcard_displaycolumn(String pk_assetcard_displaycolumn) {
		this.pk_assetcard_displaycolumn = pk_assetcard_displaycolumn;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
