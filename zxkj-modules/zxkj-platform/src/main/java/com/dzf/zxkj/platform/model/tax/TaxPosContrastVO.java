package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

@SuppressWarnings("rawtypes")
public class TaxPosContrastVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6255464478095496318L;

	private String pk_taxtemplet_sd_pos;// 主键
	private String pk_parent;// 上级
	private String itemname;// 字段名称
	private String itemkey;// 字段编码
	private String fromcell;// 模板单元格位置
	private String pk_taxtemplet;// 模板
	private String reportcode;// 报表编码
	private Integer rowno;// 序号
	private DZFBoolean isspecial;// 是否特殊处理
	private DZFBoolean isdynamic;// 是否动态行
	private Integer ibeginrow;// 开始行
	private Integer irowcount;// 行数
	private String vdefaultvalue;// 默认值
	private DZFDateTime ts;// 时间戳
	private Integer dr;// 删除标志
	private String pk_corp;// 公司
	private String sbzlbh;// 申报种类编号
	private String value;// 认值
	private int irow; // 动态第几行
	private int icol; // 动态列数

	private String djxh;// 单据序号
	private String yjntype;// 月记年类型

	public int getIcol() {
		return icol;
	}

	public void setIcol(int icol) {
		this.icol = icol;
	}

	public int getIrow() {
		return irow;
	}

	public void setIrow(int irow) {
		this.irow = irow;
	}

	public DZFBoolean getIsspecial() {
		return isspecial;
	}

	public void setIsspecial(DZFBoolean isspecial) {
		this.isspecial = isspecial;
	}

	public String getSbzlbh() {
		return sbzlbh;
	}

	public void setSbzlbh(String sbzlbh) {
		this.sbzlbh = sbzlbh;
	}

	public DZFBoolean getIsdynamic() {
		return isdynamic;
	}

	public void setIsdynamic(DZFBoolean isdynamic) {
		this.isdynamic = isdynamic;
	}

	public Integer getIbeginrow() {
		return ibeginrow;
	}

	public void setIbeginrow(Integer ibeginrow) {
		this.ibeginrow = ibeginrow;
	}

	public Integer getIrowcount() {
		return irowcount;
	}

	public void setIrowcount(Integer irowcount) {
		this.irowcount = irowcount;
	}

	public String getPk_taxtemplet_sd_pos() {
		return pk_taxtemplet_sd_pos;
	}

	public void setPk_taxtemplet_sd_pos(String pk_taxtemplet_sd_pos) {
		this.pk_taxtemplet_sd_pos = pk_taxtemplet_sd_pos;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_taxtemplet() {
		return pk_taxtemplet;
	}

	public void setPk_taxtemplet(String pk_taxtemplet) {
		this.pk_taxtemplet = pk_taxtemplet;
	}

	public String getFromcell() {
		return fromcell;
	}

	public void setFromcell(String fromcell) {
		this.fromcell = fromcell;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getPk_parent() {
		return pk_parent;
	}

	public void setPk_parent(String pk_parent) {
		this.pk_parent = pk_parent;
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

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public String getVdefaultvalue() {
		return vdefaultvalue;
	}

	public void setVdefaultvalue(String vdefaultvalue) {
		this.vdefaultvalue = vdefaultvalue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReportcode() {
		return reportcode;
	}

	public void setReportcode(String reportcode) {
		this.reportcode = reportcode;
	}

	public String getDjxh() {
		return djxh;
	}

	public String getYjntype() {
		return yjntype;
	}

	public void setDjxh(String djxh) {
		this.djxh = djxh;
	}

	public void setYjntype(String yjntype) {
		this.yjntype = yjntype;
	}

	@Override
	public String getPKFieldName() {
		return "pk_taxtemplet_sd_pos";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_parent";
	}

	@Override
	public String getTableName() {
		return "ynt_taxtemplet_sd_pos";
	}

}
