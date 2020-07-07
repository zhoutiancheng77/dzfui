package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class IntradeoutVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String period;

	private String titlePeriod;

	private String gs;

	@JsonProperty("id_ictradeout")
	private String pk_ictradeout;
	@JsonProperty("id_voucher")
	private String pk_voucher;
	@JsonProperty("id_voucher_b")
	private String pk_voucher_b;
	@JsonProperty("pk_curr")
	private String pk_currency;
	@JsonProperty("cbill")
	private String cbilltype;
	@JsonProperty("num")
	private DZFDouble nnum;
	@JsonProperty("mny")
	private DZFDouble nymny;
	@JsonProperty("cost")
	private DZFDouble ncost;
	@JsonProperty("memo")
	private String memo;
	@JsonProperty("ts")
	private DZFDateTime ts;
	@JsonProperty("dr")
	private Integer dr;
	@JsonProperty("vf1")
	private String vdef1;// 成本单价
	@JsonProperty("vf2")
	private String vdef2;
	@JsonProperty("vf3")
	private String vdef3;
	@JsonProperty("vf4")
	private String vdef4;
	@JsonProperty("vf5")
	private String vdef5;
	@JsonProperty("vf6")
	private String vdef6;
	@JsonProperty("vf7")
	private String vdef7;
	@JsonProperty("vf8")
	private String vdef8;
	@JsonProperty("vf9")
	private String vdef9;
	@JsonProperty("vf10")
	private String vdef10;
	@JsonProperty("vf11")
	private DZFDouble vdef11;
	@JsonProperty("vf12")
	private DZFDouble vdef12;
	@JsonProperty("vf13")
	private DZFDouble vdef13;
	@JsonProperty("vf14")
	private DZFDouble vdef14;
	@JsonProperty("vf15")
	private DZFDouble vdef15;
	@JsonProperty("vf16")
	private DZFBoolean vdef16;
	@JsonProperty("vf17")
	private DZFBoolean vdef17;
	@JsonProperty("vf18")
	private DZFBoolean vdef18;
	@JsonProperty("vf19")
	private DZFDate vdef19;
	@JsonProperty("vf20")
	private DZFDate vdef20;
	@JsonProperty("id_inventory")
	private String pk_inventory;
	@JsonProperty("id_corp")
	private String pk_corp;
	@JsonProperty("ddate")
	private DZFDate dbilldate;
	@JsonProperty("id_billmaker")
	private String pk_billmaker;

	private String kmbm;// 科目编码
	private String kmmc;// 科目名称
	// ------------以下字段存库
	private String pk_subject;// 科目
	private String zy;// 摘要
	private String imppzh;// 导入凭证号
	private String pzh;// 凭证号
	// ---------------
	@JsonProperty("invclassname")
	private String invclassname;// 分类
	@JsonProperty("invcode")
	private String invcode;// 商品编码
	@JsonProperty("invname")
	private String invname;// 商品名称
	@JsonProperty("invspec")
	private String invspec;// 规格
	@JsonProperty("invtype")
	private String invtype;// 型号
	@JsonProperty("measure")
	private String measure;// 计量单位
	@JsonProperty("rowno")
	private Integer rowno;
	// 销售单用
	@JsonProperty("ntax")
	private DZFDouble ntax;// 税率
	@JsonProperty("tmny")
	private DZFDouble ntaxmny;// 税额
	@JsonProperty("ttmny")
	private DZFDouble ntotaltaxmny;// 价税合计
	@JsonProperty("price")
	private DZFDouble nprice;// 单价

	// //去掉重复的 zpm 去掉
	// private String vnote;//摘要
	// private String vouchno;//凭证号
	@JsonProperty("hid")
	private String pk_ictrade_h;// 销售单主表

	public static String pkFieldName = "pk_ictradeout";

	private String dbillid; // 单据号
	private String creator;// 制单人
	private String cbusitype;// 业务类型
	private String custname;// 客户名称

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

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
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

	public String getPk_voucher_b() {
		return pk_voucher_b;
	}

	public void setPk_voucher_b(String pk_voucher_b) {
		this.pk_voucher_b = pk_voucher_b;
	}

	public String getPk_ictradeout() {
		return pk_ictradeout;
	}

	public void setPk_ictradeout(String pk_ictradeout) {
		this.pk_ictradeout = pk_ictradeout;
	}

	public String getPk_voucher() {
		return pk_voucher;
	}

	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}

	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}

	public String getCbilltype() {
		return cbilltype;
	}

	public void setCbilltype(String cbilltype) {
		this.cbilltype = cbilltype;
	}

	public DZFDouble getNnum() {
		return nnum;
	}

	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}

	public DZFDouble getNymny() {
		return nymny;
	}

	public void setNymny(DZFDouble nymny) {
		this.nymny = nymny;
	}

	public DZFDouble getNcost() {
		return ncost;
	}

	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public DZFDouble getVdef11() {
		return vdef11;
	}

	public void setVdef11(DZFDouble vdef11) {
		this.vdef11 = vdef11;
	}

	public DZFDouble getVdef12() {
		return vdef12;
	}

	public void setVdef12(DZFDouble vdef12) {
		this.vdef12 = vdef12;
	}

	public DZFDouble getVdef13() {
		return vdef13;
	}

	public void setVdef13(DZFDouble vdef13) {
		this.vdef13 = vdef13;
	}

	public DZFDouble getVdef14() {
		return vdef14;
	}

	public void setVdef14(DZFDouble vdef14) {
		this.vdef14 = vdef14;
	}

	public DZFDouble getVdef15() {
		return vdef15;
	}

	public void setVdef15(DZFDouble vdef15) {
		this.vdef15 = vdef15;
	}

	public DZFBoolean getVdef16() {
		return vdef16;
	}

	public void setVdef16(DZFBoolean vdef16) {
		this.vdef16 = vdef16;
	}

	public DZFBoolean getVdef17() {
		return vdef17;
	}

	public void setVdef17(DZFBoolean vdef17) {
		this.vdef17 = vdef17;
	}

	public DZFBoolean getVdef18() {
		return vdef18;
	}

	public void setVdef18(DZFBoolean vdef18) {
		this.vdef18 = vdef18;
	}

	public DZFDate getVdef19() {
		return vdef19;
	}

	public void setVdef19(DZFDate vdef19) {
		this.vdef19 = vdef19;
	}

	public DZFDate getVdef20() {
		return vdef20;
	}

	public void setVdef20(DZFDate vdef20) {
		this.vdef20 = vdef20;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDate getDbilldate() {
		return dbilldate;
	}

	public void setDbilldate(DZFDate dbilldate) {
		this.dbilldate = dbilldate;
	}

	public String getPk_billmaker() {
		return pk_billmaker;
	}

	public void setPk_billmaker(String pk_billmaker) {
		this.pk_billmaker = pk_billmaker;
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

	public String getImppzh() {
		return imppzh;
	}

	public void setImppzh(String imppzh) {
		this.imppzh = imppzh;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}

	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}

	@Override
	public String getTableName() {
		return "ynt_ictradeout";
	}

	public String getPKFieldName() {
		return pkFieldName;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_ictrade_h";
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
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

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public String getDbillid() {
		return dbillid;
	}

	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getCbusitype() {
		return cbusitype;
	}

	public void setCbusitype(String cbusitype) {
		this.cbusitype = cbusitype;
	}
	
	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public static Map<Integer, String> getExcelFieldColumn() {
		Map<Integer, String> fieldColumn = new HashMap<Integer, String>();
		fieldColumn.put(0, "dbilldate");
		fieldColumn.put(1, "vcorpname");
		fieldColumn.put(2, "cbusitype");
		fieldColumn.put(3, "dbillid");
		fieldColumn.put(4, "invcode");
		fieldColumn.put(5, "invname");
		fieldColumn.put(6, "invclassname");
		fieldColumn.put(7, "invspec");
//		fieldColumn.put(8, "invtype");
		fieldColumn.put(8, "measure");
		fieldColumn.put(9, "nnum");
		fieldColumn.put(10, "nprice");
		fieldColumn.put(11, "nymny");
		fieldColumn.put(12, "ntax");
		fieldColumn.put(13, "ntaxmny");
		fieldColumn.put(14, "ntotaltaxmny");
		fieldColumn.put(15, "vdef1");
		fieldColumn.put(16, "ncost");
		fieldColumn.put(17, "vpaywayname");
		fieldColumn.put(18, "fp_style");
		fieldColumn.put(19, "dinvid");
		fieldColumn.put(20, "dinvdate");
		fieldColumn.put(21, "vbankaccname");
		fieldColumn.put(22, "vcustname");
		return fieldColumn;
	}
	
	public static Map<Integer, String> getExcelFieldColumnGL2IC() {
		Map<Integer, String> fieldColumn = new HashMap<Integer, String>();
		fieldColumn.put(0, "dbilldate");
		fieldColumn.put(1, "vcorpname");
		fieldColumn.put(2, "zy");
		fieldColumn.put(3, "kmmc");
		fieldColumn.put(4, "invname");
		fieldColumn.put(5, "invspec");
//		fieldColumn.put(6, "invtype");
		fieldColumn.put(6, "measure");
		fieldColumn.put(7, "nnum");
		fieldColumn.put(8, "ncost");
		fieldColumn.put(9, "memo");
		
		return fieldColumn;
	}
}
