package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

public class AggIcTradeVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 期间
	private String period;

	private String pk_ictrade_h; // 主键
	private String pk_corp; // 公司
	private String vcorpname;// 公司名称
	private String dbilldate;// 单据日期
	private String dbillid;// 单据编号
	private DZFDate dinvdate;// 发票日期
	private String dinvid;// 发票编号
	private String cbusitype;// 业务类型
	private String pk_cust;// 供应商
	private String vcustname;// 客户名称 、供应商名称
	private Integer ipayway; // 付款方式
	private String vpaywayname; // 付款方式
	private String pk_bankaccount;// 银行账号主键
	private String vbankaccname;// 银行账户
	private String iszg;//是否暂估

	private String pk_subject;// 科目
	private String kmmc;//科目名称
	private String pk_inventory;
	private String invclassname;// 分类
	private String invcode;// 商品编码
	private String invname;// 商品名称
	private String invspec;// 规格
	private String invtype;// 型号
	private String measure;// 计量单位

	private DZFDouble nnum;// 数量
	private DZFDouble nprice;// 单价
	private DZFDouble nymny;
	private DZFDouble ntax;// 税率
	private DZFDouble ntaxmny;// 税额
	private DZFDouble ntotaltaxmny;// 价税合计
	private String vdef1;// 成本单价
	private DZFDouble ncost;// 成本金额
	private String zy;//摘要
	private String memo;//备注
	private String fp_style;// 发票类型

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}

	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcorpname() {
		return vcorpname;
	}

	public void setVcorpname(String vcorpname) {
		this.vcorpname = vcorpname;
	}
	
	public String getDbilldate() {
		return dbilldate;
	}

	public void setDbilldate(String dbilldate) {
		this.dbilldate = dbilldate;
	}

	public String getDbillid() {
		return dbillid;
	}

	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}

	public DZFDate getDinvdate() {
		return dinvdate;
	}

	public void setDinvdate(DZFDate dinvdate) {
		this.dinvdate = dinvdate;
	}

	public String getDinvid() {
		return dinvid;
	}

	public void setDinvid(String dinvid) {
		this.dinvid = dinvid;
	}

	public String getCbusitype() {
		return cbusitype;
	}

	public void setCbusitype(String cbusitype) {
		this.cbusitype = cbusitype;
	}

	public String getPk_cust() {
		return pk_cust;
	}

	public void setPk_cust(String pk_cust) {
		this.pk_cust = pk_cust;
	}

	public String getVcustname() {
		return vcustname;
	}

	public void setVcustname(String vcustname) {
		this.vcustname = vcustname;
	}

	public Integer getIpayway() {
		return ipayway;
	}

	public void setIpayway(Integer ipayway) {
		this.ipayway = ipayway;
	}

	public String getPk_bankaccount() {
		return pk_bankaccount;
	}

	public void setPk_bankaccount(String pk_bankaccount) {
		this.pk_bankaccount = pk_bankaccount;
	}

	public String getVbankaccname() {
		return vbankaccname;
	}

	public void setVbankaccname(String vbankaccname) {
		this.vbankaccname = vbankaccname;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getInvclassname() {
		return invclassname;
	}

	public void setInvclassname(String invclassname) {
		this.invclassname = invclassname;
	}

	public String getInvcode() {
		return invcode;
	}

	public void setInvcode(String invcode) {
		this.invcode = invcode;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public DZFDouble getNnum() {
		return nnum;
	}

	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public DZFDouble getNymny() {
		return nymny;
	}

	public void setNymny(DZFDouble nymny) {
		this.nymny = nymny;
	}

	public DZFDouble getNtax() {
		return ntax;
	}

	public void setNtax(DZFDouble ntax) {
		this.ntax = ntax;
	}

	public DZFDouble getNtaxmny() {
		return ntaxmny;
	}

	public void setNtaxmny(DZFDouble ntaxmny) {
		this.ntaxmny = ntaxmny;
	}

	public DZFDouble getNtotaltaxmny() {
		return ntotaltaxmny;
	}

	public void setNtotaltaxmny(DZFDouble ntotaltaxmny) {
		this.ntotaltaxmny = ntotaltaxmny;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public DZFDouble getNcost() {
		return ncost;
	}

	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}

	public String getVpaywayname() {
		return vpaywayname;
	}

	public void setVpaywayname(String vpaywayname) {
		this.vpaywayname = vpaywayname;
	}
	
	public String getIszg() {
		return iszg;
	}

	public void setIszg(String iszg) {
		this.iszg = iszg;
	}

	public String getPk_subject() {
		return pk_subject;
	}

	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getFp_style() {
		return fp_style;
	}

	public void setFp_style(String fp_style) {
		this.fp_style = fp_style;
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
