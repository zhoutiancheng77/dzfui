package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 票据收集子表
 * @author wangzhn
 *
 */
public class PjCollectionBVO extends SuperVO {

	@JsonProperty("bid")
	private String pk_bill_collect_b;// 子表主键
	@JsonProperty("id")
	private String pk_bill_collect_h;// 主表主键
	@JsonProperty("corpid")
	private String pk_corp;
	@JsonProperty("pjzt")
	private Integer billstyle;// 票据类型
	@JsonProperty("bypjzs")
	private Integer billnum;// 本月票据张数
	@JsonProperty("bypjje")
	private DZFDouble billmny;// 本月票据金额
	@JsonProperty("sccgsl")
	private Integer upsucnum;// 上传成功张数
	@JsonProperty("sczzs")
	private Integer uptotalnum;// 上传总张数
	@JsonProperty("beizhu")
	private String memo;// 备注
	private String dr;
	private String ts;
	
	public String getPk_bill_collect_b() {
		return pk_bill_collect_b;
	}
	public String getPk_bill_collect_h() {
		return pk_bill_collect_h;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public Integer getBillstyle() {
		return billstyle;
	}
	public Integer getBillnum() {
		return billnum;
	}
	public DZFDouble getBillmny() {
		return billmny;
	}
	public String getMemo() {
		return memo;
	}
	public String getDr() {
		return dr;
	}
	public String getTs() {
		return ts;
	}
	public void setPk_bill_collect_b(String pk_bill_collect_b) {
		this.pk_bill_collect_b = pk_bill_collect_b;
	}
	public void setPk_bill_collect_h(String pk_bill_collect_h) {
		this.pk_bill_collect_h = pk_bill_collect_h;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public void setBillstyle(Integer billstyle) {
		this.billstyle = billstyle;
	}
	public void setBillnum(Integer billnum) {
		this.billnum = billnum;
	}
	public void setBillmny(DZFDouble billmny) {
		this.billmny = billmny;
	}
	
	public Integer getUpsucnum() {
		return upsucnum;
	}
	public Integer getUptotalnum() {
		return uptotalnum;
	}
	public void setUpsucnum(Integer upsucnum) {
		this.upsucnum = upsucnum;
	}
	public void setUptotalnum(Integer uptotalnum) {
		this.uptotalnum = uptotalnum;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public void setDr(String dr) {
		this.dr = dr;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	@Override
	public String getPKFieldName() {
		return "pk_bill_collect_b";
	}
	@Override
	public String getParentPKFieldName() {
		return "pk_bill_collect_h";
	}
	@Override
	public String getTableName() {
		return "ynt_bill_collect_b";
	}
}
