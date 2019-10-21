package com.dzf.zxkj.platform.model.glic;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 存货期初VO
 * 
 */
public class InventoryQcVO extends SuperVO {
	@JsonProperty("qcid")
	private String pk_icqc;
	private String pk_corp;
	@JsonProperty("chid")
	private String pk_inventory;
	@JsonProperty("userid")
	private String coperatorid;
	// 本月期初金额
	@JsonProperty("qcje")
	private DZFDouble thismonthqc;
	// 本月期初数量
	@JsonProperty("qcsl")
	private DZFDouble monthqmnum;
	// 本月期初单价
	@JsonProperty("qcdj")
	private DZFDouble monthqc_price;
	// 启用期间
	@JsonProperty("opdate")
	private DZFDate doperatedate;
	// 备注
	private String memo;

	@JsonProperty("ndf")
	private DZFDouble yeardffse;
	@JsonProperty("njf")
	private DZFDouble yearjffse;
	@JsonProperty("nqc")
	private DZFDouble yearqc;

	// 原币本年借方发生
	@JsonProperty("ybnjf")
	private DZFDouble ybyearjffse;
	// 原币本年贷方发生
	@JsonProperty("ybndf")
	private DZFDouble ybyeardffse;
	// 原币本年期初
	@JsonProperty("ybnqc")
	private DZFDouble ybyearqc;
	// 原币本月期初
	@JsonProperty("ybmonthqc")
	private DZFDouble ybthismonthqc;
	// 本年期初数量
	private DZFDouble bnqcnum;
	// 本年借方发生数量
	private DZFDouble bnfsnum;
	// 本年贷方发生数量
	private DZFDouble bndffsnum;
	private Integer dr;

	// 登录日期
	private String date;
	// 存货类别编码
	private String chlbbm;
	// 存货类别
	private String chlb;
	// 存货编码
	private String chbm;
	// 存货名称
	private String chmc;
	// 规格型号
	private String spec;
	// 计量单位
	private String jldw;
	// 币种
	private String pk_currency;

	private DZFDateTime ts;
	private DZFDouble nrate;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private String vdef9;
	private String vdef10;

	private String period;

	// 辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
	@JsonProperty("fzhs1")
	private String fzhsx1;
	@JsonProperty("fzhs2")
	private String fzhsx2;
	@JsonProperty("fzhs3")
	private String fzhsx3;
	@JsonProperty("fzhs4")
	private String fzhsx4;
	@JsonProperty("fzhs5")
	private String fzhsx5;
	@JsonProperty("fzhs6")
	private String fzhsx6;
	@JsonProperty("fzhs7")
	private String fzhsx7;
	@JsonProperty("fzhs8")
	private String fzhsx8;
	@JsonProperty("fzhs9")
	private String fzhsx9;
	@JsonProperty("fzhs10")
	private String fzhsx10;

	public String getPk_icqc() {
		return pk_icqc;
	}

	public void setPk_icqc(String pk_icqc) {
		this.pk_icqc = pk_icqc;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDouble getThismonthqc() {
		return thismonthqc;
	}

	public void setThismonthqc(DZFDouble thismonthqc) {
		this.thismonthqc = thismonthqc;
	}

	public DZFDouble getMonthqmnum() {
		return monthqmnum;
	}

	public void setMonthqmnum(DZFDouble monthqmnum) {
		this.monthqmnum = monthqmnum;
	}

	public DZFDouble getMonthqc_price() {
		return monthqc_price;
	}

	public void setMonthqc_price(DZFDouble monthqc_price) {
		this.monthqc_price = monthqc_price;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public DZFDouble getYeardffse() {
		return yeardffse;
	}

	public void setYeardffse(DZFDouble yeardffse) {
		this.yeardffse = yeardffse;
	}

	public DZFDouble getYearjffse() {
		return yearjffse;
	}

	public void setYearjffse(DZFDouble yearjffse) {
		this.yearjffse = yearjffse;
	}

	public DZFDouble getYearqc() {
		return yearqc;
	}

	public void setYearqc(DZFDouble yearqc) {
		this.yearqc = yearqc;
	}

	public DZFDouble getYbyearjffse() {
		return ybyearjffse;
	}

	public void setYbyearjffse(DZFDouble ybyearjffse) {
		this.ybyearjffse = ybyearjffse;
	}

	public DZFDouble getYbyeardffse() {
		return ybyeardffse;
	}

	public void setYbyeardffse(DZFDouble ybyeardffse) {
		this.ybyeardffse = ybyeardffse;
	}

	public DZFDouble getYbyearqc() {
		return ybyearqc;
	}

	public void setYbyearqc(DZFDouble ybyearqc) {
		this.ybyearqc = ybyearqc;
	}

	public DZFDouble getYbthismonthqc() {
		return ybthismonthqc;
	}

	public void setYbthismonthqc(DZFDouble ybthismonthqc) {
		this.ybthismonthqc = ybthismonthqc;
	}

	public DZFDouble getBnqcnum() {
		return bnqcnum;
	}

	public void setBnqcnum(DZFDouble bnqcnum) {
		this.bnqcnum = bnqcnum;
	}

	public DZFDouble getBnfsnum() {
		return bnfsnum;
	}

	public void setBnfsnum(DZFDouble bnfsnum) {
		this.bnfsnum = bnfsnum;
	}

	public DZFDouble getBndffsnum() {
		return bndffsnum;
	}

	public void setBndffsnum(DZFDouble bndffsnum) {
		this.bndffsnum = bndffsnum;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getChlbbm() {
		return chlbbm;
	}

	public void setChlbbm(String chlbbm) {
		this.chlbbm = chlbbm;
	}

	public String getChlb() {
		return chlb;
	}

	public void setChlb(String chlb) {
		this.chlb = chlb;
	}

	public String getChbm() {
		return chbm;
	}

	public void setChbm(String chbm) {
		this.chbm = chbm;
	}

	public String getChmc() {
		return chmc;
	}

	public void setChmc(String chmc) {
		this.chmc = chmc;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public DZFDouble getNrate() {
		return nrate;
	}

	public void setNrate(DZFDouble nrate) {
		this.nrate = nrate;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public String getVdef9() {
		return vdef9;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	public String getVdef10() {
		return vdef10;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getFzhsx1() {
		return fzhsx1;
	}

	public void setFzhsx1(String fzhsx1) {
		this.fzhsx1 = fzhsx1;
	}

	public String getFzhsx2() {
		return fzhsx2;
	}

	public void setFzhsx2(String fzhsx2) {
		this.fzhsx2 = fzhsx2;
	}

	public String getFzhsx3() {
		return fzhsx3;
	}

	public void setFzhsx3(String fzhsx3) {
		this.fzhsx3 = fzhsx3;
	}

	public String getFzhsx4() {
		return fzhsx4;
	}

	public void setFzhsx4(String fzhsx4) {
		this.fzhsx4 = fzhsx4;
	}

	public String getFzhsx5() {
		return fzhsx5;
	}

	public void setFzhsx5(String fzhsx5) {
		this.fzhsx5 = fzhsx5;
	}

	public String getFzhsx6() {
		return fzhsx6;
	}

	public void setFzhsx6(String fzhsx6) {
		this.fzhsx6 = fzhsx6;
	}

	public String getFzhsx7() {
		return fzhsx7;
	}

	public void setFzhsx7(String fzhsx7) {
		this.fzhsx7 = fzhsx7;
	}

	public String getFzhsx8() {
		return fzhsx8;
	}

	public void setFzhsx8(String fzhsx8) {
		this.fzhsx8 = fzhsx8;
	}

	public String getFzhsx9() {
		return fzhsx9;
	}

	public void setFzhsx9(String fzhsx9) {
		this.fzhsx9 = fzhsx9;
	}

	public String getFzhsx10() {
		return fzhsx10;
	}

	public void setFzhsx10(String fzhsx10) {
		this.fzhsx10 = fzhsx10;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_icqc";
	}

	@Override
	public String getTableName() {
		return "ynt_glicqc";
	}

}
