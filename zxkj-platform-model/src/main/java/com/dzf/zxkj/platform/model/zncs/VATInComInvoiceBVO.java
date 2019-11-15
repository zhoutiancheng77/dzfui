package com.dzf.zxkj.platform.model.zncs;

/**
 * 进项发票子表vo
 * @author wangzhn
 *
 */
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.pjgl.IGlobalPZVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VATInComInvoiceBVO extends SuperVO implements IGlobalPZVO {
	
	@JsonProperty("bid")
	private String pk_vatincominvoice_b;
	@JsonProperty("pid")
	private String pk_vatincominvoice;
	@JsonProperty("rowno")
	private Integer rowno;//序号
	@JsonProperty("bspmc")
	private String bspmc;//商品名称
	@JsonProperty("gg")
	private String invspec;//规格
	@JsonProperty("jldw")
	private String measurename;//单位
	@JsonProperty("bnum")
	private DZFDouble bnum;//数量
	@JsonProperty("bdj")
	private DZFDouble bprice;//单价
	@JsonProperty("bje")
	private DZFDouble bhjje;//金额
	@JsonProperty("bse")
	private DZFDouble bspse;//税额
	@JsonProperty("bsl")
	private DZFDouble bspsl;//税率
	
	@JsonProperty("taxid")
	private String pk_taxitem;//税目
	private String taxname;
	@JsonProperty("chid")
	private String pk_inventory;
	private String pk_accsubj;//字段  不存库 
	private String fphm;//存货匹配界面 仅展示用 不存库
	
	private String pk_corp;
	private Integer dr;
	private DZFDateTime ts;
	
	public String getTaxname() {
		return taxname;
	}

	public void setTaxname(String taxname) {
		this.taxname = taxname;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public String getPk_taxitem() {
		return pk_taxitem;
	}

	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
	}

	public String getPk_vatincominvoice_b() {
		return pk_vatincominvoice_b;
	}

	public String getPk_vatincominvoice() {
		return pk_vatincominvoice;
	}

	public String getBspmc() {
		return bspmc;
	}

	public String getInvspec() {
		return invspec;
	}

	public String getMeasurename() {
		return measurename;
	}

	public DZFDouble getBnum() {
		return bnum;
	}

	public DZFDouble getBprice() {
		return bprice;
	}

	public DZFDouble getBhjje() {
		return bhjje;
	}

	public DZFDouble getBspse() {
		return bspse;
	}

	public DZFDouble getBspsl() {
		return bspsl;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_vatincominvoice_b(String pk_vatincominvoice_b) {
		this.pk_vatincominvoice_b = pk_vatincominvoice_b;
	}

	public void setPk_vatincominvoice(String pk_vatincominvoice) {
		this.pk_vatincominvoice = pk_vatincominvoice;
	}

	public void setBspmc(String bspmc) {
		this.bspmc = bspmc;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public void setMeasurename(String measurename) {
		this.measurename = measurename;
	}

	public void setBnum(DZFDouble bnum) {
		this.bnum = bnum;
	}

	public void setBprice(DZFDouble bprice) {
		this.bprice = bprice;
	}

	public void setBhjje(DZFDouble bhjje) {
		this.bhjje = bhjje;
	}

	public void setBspse(DZFDouble bspse) {
		this.bspse = bspse;
	}

	public void setBspsl(DZFDouble bspsl) {
		this.bspsl = bspsl;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	@Override
	public String getPKFieldName() {
		return "pk_vatincominvoice_b";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_vatincominvoice";
	}

	@Override
	public String getTableName() {
		return "ynt_vatincominvoice_b";
	}
	
	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	
	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	@Override
	public DZFDouble getTotalmny() {
		return SafeCompute.add(getBhjje(), getBspse());
	}

	@Override
	public DZFDouble getMny() {
		return getBhjje();
	}

	@Override
	public DZFDouble getWsmny() {
		return getBhjje();
	}

	@Override
	public DZFDouble getSmny() {
		return getBspse();
	}
}
