package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 隐藏显示列设置VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ColumnSetupVO extends SuperVO {

private static final long serialVersionUID = 1L;
	
	@JsonProperty("cid")
	private String pk_col_setup;

	@JsonProperty("corpid")
	private String pk_corp;//会计公司主键
	
	@JsonProperty("nodecode")
	private String vnodecode;// 节点编码
	
	@JsonProperty("colsetup")
	private String vcolsetup;//列设置
	
	@JsonProperty("settype")
	private Integer isettype;//设置类型：1：表格列；2：开关按钮；
	
	@JsonProperty("copid")
	private String coperatorid; // 录入人主键
	
	@JsonProperty("dopdate")
	private DZFDate doperatedate; // 录入日期
	
	private DZFDateTime ts;
	
	private Integer dr;

	public Integer getIsettype() {
		return isettype;
	}

	public void setIsettype(Integer isettype) {
		this.isettype = isettype;
	}

	public String getPk_col_setup() {
		return pk_col_setup;
	}

	public void setPk_col_setup(String pk_col_setup) {
		this.pk_col_setup = pk_col_setup;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVnodecode() {
		return vnodecode;
	}

	public void setVnodecode(String vnodecode) {
		this.vnodecode = vnodecode;
	}

	public String getVcolsetup() {
		return vcolsetup;
	}

	public void setVcolsetup(String vcolsetup) {
		this.vcolsetup = vcolsetup;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
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
		return "pk_col_setup";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_col_setup";
	}

}
