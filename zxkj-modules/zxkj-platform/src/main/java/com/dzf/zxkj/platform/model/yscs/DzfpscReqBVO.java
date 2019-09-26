package com.dzf.zxkj.platform.model.yscs;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/****
 * 电子发票信息上传
 * @author asoka
 *
 */
@SuppressWarnings("rawtypes")
public class DzfpscReqBVO extends SuperVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String pk_dzfpsc_b;
	
	private String pk_dzfpsc_h;
	
	//凭证主键
	private String pk_tzpz_h;
	
	private String pk_corp;
	
	//开票日期
	private DZFDate kprqb;
	
	//序列
	private int xulie;
	
	// 购货单位
	private String ghdwb;
	
	// 发票号码
	private String fphmb;
	
	//凭证字号
	private String pzzh;
	
	//摘要
	private String summary;

	// 名称
	@JsonProperty("DDMX_DDMC")
	private String itemname;
	
	// 单位
	@JsonProperty("DDMX_DW")
	private String unit;
	
	// 销售类型
	@JsonProperty("DDMX_XSLX")
	private String xslx;
	
	// 规格型号
	@JsonProperty("DDMX_GGXH")
	private String ggxh;
	
	// 含税标志
	@JsonProperty("DDMX_HSBZ")
	private String hsbz;
	
	// 项目数量
	@JsonProperty("DDMX_XMSL")
	private DZFDouble amount;
	
	// 税率
	@JsonProperty("DDMX_SL")
	private DZFDouble taxrate;
	
	//单价
	@JsonProperty("DDMX_DJ")
	private DZFDouble price;
	
	//金额
	@JsonProperty("DDMX_JE")
	private DZFDouble money;
	
	//税额
	@JsonProperty("DDMX_SE")
	private DZFDouble taxmny;
	
	//价税合计
	private DZFDouble jshj;
	
	private DZFBoolean isselect;

	@Override
	public String getParentPKFieldName() {
		return "pk_dzfpsc_h";
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_dzfpsc_b";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_yscs_dzfpsc_b";
	}

	public String getPk_dzfpsc_b() {
		return pk_dzfpsc_b;
	}

	public void setPk_dzfpsc_b(String pk_dzfpsc_b) {
		this.pk_dzfpsc_b = pk_dzfpsc_b;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}


	public int getXulie() {
		return xulie;
	}

	public void setXulie(int xulie) {
		this.xulie = xulie;
	}

	public String getPzzh() {
		return pzzh;
	}

	public void setPzzh(String pzzh) {
		this.pzzh = pzzh;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getXslx() {
		return xslx;
	}

	public void setXslx(String xslx) {
		this.xslx = xslx;
	}

	public String getGgxh() {
		return ggxh;
	}

	public void setGgxh(String ggxh) {
		this.ggxh = ggxh;
	}

	public String getHsbz() {
		return hsbz;
	}

	public void setHsbz(String hsbz) {
		this.hsbz = hsbz;
	}

	public DZFDouble getAmount() {
		return amount;
	}

	public void setAmount(DZFDouble amount) {
		this.amount = amount;
	}

	public DZFDouble getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(DZFDouble taxrate) {
		this.taxrate = taxrate;
	}

	public DZFDouble getPrice() {
		return price;
	}

	public void setPrice(DZFDouble price) {
		this.price = price;
	}

	public DZFDouble getMoney() {
		return money;
	}

	public void setMoney(DZFDouble money) {
		this.money = money;
	}

	public DZFDouble getTaxmny() {
		return taxmny;
	}

	public void setTaxmny(DZFDouble taxmny) {
		this.taxmny = taxmny;
	}

	public DZFDouble getJshj() {
		return jshj;
	}

	public void setJshj(DZFDouble jshj) {
		this.jshj = jshj;
	}

	public DZFDate getKprqb() {
		return kprqb;
	}

	public void setKprqb(DZFDate kprqb) {
		this.kprqb = kprqb;
	}

	public String getGhdwb() {
		return ghdwb;
	}

	public void setGhdwb(String ghdwb) {
		this.ghdwb = ghdwb;
	}

	public String getFphmb() {
		return fphmb;
	}

	public void setFphmb(String fphmb) {
		this.fphmb = fphmb;
	}

	public String getPk_dzfpsc_h() {
		return pk_dzfpsc_h;
	}

	public void setPk_dzfpsc_h(String pk_dzfpsc_h) {
		this.pk_dzfpsc_h = pk_dzfpsc_h;
	}

	public DZFBoolean getIsselect() {
		return isselect;
	}

	public void setIsselect(DZFBoolean isselect) {
		this.isselect = isselect;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}


}
