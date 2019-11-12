package com.dzf.zxkj.common.query;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 数量金额账表 查询条件
 * 
 * @author liangyi
 *
 */
public class QueryCondictionVO extends SuperVO {
	
	@JsonProperty("corp")
	private String pk_corp;// 公司
	// 开始期间
	@JsonProperty("qjq")
	private String qjq;
	// 结束期间
	@JsonProperty("qjz")
	private String qjz;
	// 是否结账
	private DZFBoolean ishasjz;
	// 是否结账
	private DZFBoolean isfzhs;
	//科目层级
	@JsonProperty("cjq")
	private Integer cjq;
	@JsonProperty("cjz")
	private Integer cjz;
	// 科目区间
	private String kms_first;
	private String kms_last;
	// 币种
	private String pk_currency;
	//是否启用库存
	private DZFBoolean isic;
	//公司建账日期
	private DZFDate jzdate;
	
	//有余额无发生不显示
	private DZFBoolean ishowfs;
	private DZFBoolean xswyewfs;
	private String pk_inventory;
	
	public DZFBoolean getXswyewfs() {
		return xswyewfs;
	}
	public void setXswyewfs(DZFBoolean xswyewfs) {
		this.xswyewfs = xswyewfs;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getQjq() {
		return qjq;
	}
	public void setQjq(String qjq) {
		this.qjq = qjq;
	}
	public String getQjz() {
		return qjz;
	}
	public void setQjz(String qjz) {
		this.qjz = qjz;
	}
	public DZFBoolean getIshasjz() {
		return ishasjz;
	}
	public void setIshasjz(DZFBoolean ishasjz) {
		this.ishasjz = ishasjz;
	}
	public DZFBoolean getIsfzhs() {
		return isfzhs;
	}
	public void setIsfzhs(DZFBoolean isfzhs) {
		this.isfzhs = isfzhs;
	}
	public Integer getCjq() {
		return cjq;
	}
	public void setCjq(Integer cjq) {
		this.cjq = cjq;
	}
	public Integer getCjz() {
		return cjz;
	}
	public void setCjz(Integer cjz) {
		this.cjz = cjz;
	}
	public String getKms_first() {
		return kms_first;
	}
	public void setKms_first(String kms_first) {
		this.kms_first = kms_first;
	}
	public String getKms_last() {
		return kms_last;
	}
	public void setKms_last(String kms_last) {
		this.kms_last = kms_last;
	}
	public String getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	public DZFBoolean getIsic() {
		return isic;
	}
	public void setIsic(DZFBoolean isic) {
		this.isic = isic;
	}
	
	
	public DZFDate getJzdate() {
		return jzdate;
	}
	public void setJzdate(DZFDate jzdate) {
		this.jzdate = jzdate;
	}
	
	public String getPk_inventory() {
		return pk_inventory;
	}
	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
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
	public DZFBoolean getIshowfs() {
		return ishowfs;
	}
	public void setIshowfs(DZFBoolean ishowfs) {
		this.ishowfs = ishowfs;
	}

}
