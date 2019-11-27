package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 采购单 销售单表头
 * 
 * @author
 *
 */
public class IntradeHVO extends SuperVO {

	// 期间
	private String period;

	@JsonProperty("id_ictrade_h")
	private String pk_ictrade_h; // 主键
	@JsonProperty("id_corp")
	private String pk_corp; // 公司
	@JsonProperty("ddate")
	private DZFDate dbilldate;// 单据日期
	@JsonProperty("did")
	private String dbillid;// 单据编号
	@JsonProperty("invdate")
	private DZFDate dinvdate;// 发票日期
	@JsonProperty("invid")
	private String dinvid;// 发票编号
	@JsonProperty("pk_curr")
	private String pk_currency;// 币种
	@JsonProperty("cbilltype")
	private String cbilltype;// 单据类型
	private String cbusitype;// 业务类型
	private String memo;// 备注
	private Integer dr;// 删除标志
	private String imppzh;// 导入凭证号
	private String pzh;// 凭证号
	@JsonProperty("pzid")
	private String pzid;// 凭证主键
	private DZFDateTime ts;// 时间戳
	@JsonProperty("pk_cust")
	private String pk_cust;// 供应商
	@JsonProperty("iway")
	private Integer ipayway; // 付款方式
	@JsonProperty("jdate")
	private DZFDate djzdate; // 转总账日期
	@JsonProperty("isjz")
	private DZFBoolean isjz;// 是否转总账
	@JsonProperty("isback")
	private DZFBoolean isback; // 是否退回
	@JsonProperty("iszg")
	private DZFBoolean iszg;// 是否暂估
	@JsonProperty("istatus")
	private Integer iarristatus; // 到货状态
	@JsonProperty("mny")
	private DZFDouble nmny;// 金额
	@JsonProperty("tmny")
	private DZFDouble ntaxmny;// 税额
	@JsonProperty("ttmny")
	private DZFDouble ntotaltaxmny;// 价税合计
	@JsonProperty("creator")
	private String creator; // 创建人
	@JsonProperty("mdate")
	private DZFDate modifydate;// 最后修改日期
	@JsonProperty("isczg")
	private DZFBoolean isczg;// 冲暂估
	@JsonProperty("isrz1")
	private DZFBoolean isrz;// 认证

	private String custname;// 客户名称
	private String custcode;// 客户编码

	private String sourcebilltype;// 来源单据类型
	private String sourcebillid;// 来源单据主键

	@JsonProperty("yhzh")
	private String pk_bankaccount;// 银行账号主键
	private DZFDouble vdef1;//
	private DZFDouble vdef2;// 成本单价
	private DZFDouble vdef3;// 成本
	private DZFDouble vdef4;// 平均单价
	private DZFDouble vdef5;// 汇总数量
	private String vdef6;//
	private String vdef7;//
	private String vdef8;//
	private String vdef9;//
	private String vdef10;//
	private Integer vdef11;//
	private Integer vdef12;//
	private Integer vdef13;//
	private DZFBoolean vdef14;//
	private DZFBoolean vdef15;//
	private DZFBoolean isinterface;//新智能识别接口为 DZFBoolean.TRUE  其他 DZFBoolean.FALSE
	
	private String pk_image_group;//图片主表id
	private String pk_image_library;//图片子表id
	
	private Integer fp_style;// 发票类型
	// (1), 普票（开具的普通发票）
	// (2), 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
	// 如果为null 为不区分专、普票
	private Integer pzstatus;//凭证状态  -1 暂存
	public String getPk_bankaccount() {
		return pk_bankaccount;
	}

	public void setPk_bankaccount(String pk_bankaccount) {
		this.pk_bankaccount = pk_bankaccount;
	}

	public DZFDouble getVdef1() {
		return vdef1;
	}

	public void setVdef1(DZFDouble vdef1) {
		this.vdef1 = vdef1;
	}

	public DZFDouble getVdef2() {
		return vdef2;
	}

	public void setVdef2(DZFDouble vdef2) {
		this.vdef2 = vdef2;
	}

	public DZFDouble getVdef3() {
		return vdef3;
	}

	public void setVdef3(DZFDouble vdef3) {
		this.vdef3 = vdef3;
	}

	public DZFDouble getVdef4() {
		return vdef4;
	}

	public void setVdef4(DZFDouble vdef4) {
		this.vdef4 = vdef4;
	}

	public DZFDouble getVdef5() {
		return vdef5;
	}

	public void setVdef5(DZFDouble vdef5) {
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

	public Integer getVdef11() {
		return vdef11;
	}

	public void setVdef11(Integer vdef11) {
		this.vdef11 = vdef11;
	}

	public Integer getVdef12() {
		return vdef12;
	}

	public void setVdef12(Integer vdef12) {
		this.vdef12 = vdef12;
	}

	public Integer getVdef13() {
		return vdef13;
	}

	public void setVdef13(Integer vdef13) {
		this.vdef13 = vdef13;
	}

	public DZFBoolean getVdef14() {
		return vdef14;
	}

	public void setVdef14(DZFBoolean vdef14) {
		this.vdef14 = vdef14;
	}

	public DZFBoolean getVdef15() {
		return vdef15;
	}

	public void setVdef15(DZFBoolean vdef15) {
		this.vdef15 = vdef15;
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

	public DZFDate getDbilldate() {
		return dbilldate;
	}

	public void setDbilldate(DZFDate dbilldate) {
		this.dbilldate = dbilldate;
	}

	public String getDinvid() {
		return dinvid;
	}

	public void setDinvid(String dinvid) {
		this.dinvid = dinvid;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
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

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
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

	public String getPzid() {
		return pzid;
	}

	public void setPzid(String pzid) {
		this.pzid = pzid;
	}

	public String getPk_cust() {
		return pk_cust;
	}

	public void setPk_cust(String pk_cust) {
		this.pk_cust = pk_cust;
	}

	public Integer getIpayway() {
		return ipayway;
	}

	public void setIpayway(Integer ipayway) {
		this.ipayway = ipayway;
	}

	public DZFDate getDjzdate() {
		return djzdate;
	}

	public void setDjzdate(DZFDate djzdate) {
		this.djzdate = djzdate;
	}

	public DZFBoolean getIsjz() {
		return isjz;
	}

	public void setIsjz(DZFBoolean isjz) {
		this.isjz = isjz;
	}

	public DZFBoolean getIsback() {
		return isback;
	}

	public void setIsback(DZFBoolean isback) {
		this.isback = isback;
	}

	public DZFBoolean getIszg() {
		return iszg;
	}

	public void setIszg(DZFBoolean iszg) {
		this.iszg = iszg;
	}

	public Integer getIarristatus() {
		return iarristatus;
	}

	public void setIarristatus(Integer iarristatus) {
		this.iarristatus = iarristatus;
	}

	public DZFDouble getNmny() {
		return nmny;
	}

	public void setNmny(DZFDouble nmny) {
		this.nmny = nmny;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public DZFDate getModifydate() {
		return modifydate;
	}

	public void setModifydate(DZFDate modifydate) {
		this.modifydate = modifydate;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getCustcode() {
		return custcode;
	}

	public void setCustcode(String custcode) {
		this.custcode = custcode;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public DZFBoolean getIsczg() {
		return isczg;
	}

	public void setIsczg(DZFBoolean isczg) {
		this.isczg = isczg;
	}

	public String getCbusitype() {
		return cbusitype;
	}

	public void setCbusitype(String cbusitype) {
		this.cbusitype = cbusitype;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}
	
	public DZFBoolean getIsrz() {
		return isrz;
	}

	public void setIsrz(DZFBoolean isrz) {
		this.isrz = isrz;
	}
	
	public DZFBoolean getIsinterface() {
		return isinterface;
	}

	public void setIsinterface(DZFBoolean isinterface) {
		this.isinterface = isinterface;
	}
	
	public Integer getPzstatus() {
		return pzstatus;
	}

	public void setPzstatus(Integer pzstatus) {
		this.pzstatus = pzstatus;
	}

	@Override
	public String getPKFieldName() {
		return "pk_ictrade_h";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_ictrade_h";
	}

}
