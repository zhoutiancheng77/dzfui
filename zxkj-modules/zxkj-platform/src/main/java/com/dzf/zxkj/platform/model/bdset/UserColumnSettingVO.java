package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 表格列显示隐藏设置
 * 
 * @author lbj
 *
 */
public class UserColumnSettingVO extends SuperVO {

	@JsonProperty("id")
	private String pk_col_setting;
	// 用户ID
	private String cuserid;
	// 节点名称
	private String nodename;
	// 设置
	private String col_setting;
	private DZFDateTime ts;
	private Integer dr;

	public String getPk_col_setting() {
		return pk_col_setting;
	}

	public void setPk_col_setting(String pk_col_setting) {
		this.pk_col_setting = pk_col_setting;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public String getCol_setting() {
		return col_setting;
	}

	public void setCol_setting(String col_setting) {
		this.col_setting = col_setting;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		return "pk_col_setting";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "user_ext_colsetting";
	}

}
