package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-11-01 16:41:33
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class AssetcardVO extends SuperVO {
	
	public static final String TABLE_NAME = "ynt_assetcard";
	
	public static final String PK_FILED = "pk_assetcard";
	
	@JsonProperty("id_corp")
	private String pk_corp;//公司
	@JsonProperty("ascode")
	private String assetcode;//卡片编号
	@JsonProperty("ts")
	private DZFDateTime ts;
	@JsonProperty("vf9")
	private String vdef9;
	@JsonProperty("coid")
	private String coperatorid;//制单人
	@JsonProperty("togl")
	private DZFBoolean istogl;//已转总账
	@JsonProperty("vf1")
	private String vdef1;
	@JsonProperty("vf8")
	private String vdef8;
	@JsonProperty("vf10")
	private String vdef10;
	@JsonProperty("periodbegin")
	private DZFBoolean isperiodbegin;//是否期初
	@JsonProperty("sratio")
	private DZFDouble salvageratio;//残值率
	@JsonProperty("amny")
	private DZFDouble accountmny;//建账原值(没有变更的值)
	@JsonProperty("accperiod")
	private Integer accountdepreciationperiod;//建账折旧期间数(月)
	@JsonProperty("asvalue")
	private DZFDouble assetnetvalue;//资产净值
	@JsonProperty("inittion")
	private DZFDouble initdepreciation;//期初累计折旧
	@JsonProperty("atmny")
	private DZFDouble assetmny;//资产原值
	@JsonProperty("initperiod")
	private Integer initusedperiod;//期初已使用期间数(月)
	@JsonProperty("sdate")
	private DZFDate setdate;
	@JsonProperty("asname")
	private String assetname;//资产名称
	@JsonProperty("initciation")
	private Integer initdepreciationperiod;//期初折旧期间数(月)
	@JsonProperty("vf7")
	private String vdef7;
	@JsonProperty("assetcate_id")
	private String assetcategory;//资产类别
	@JsonProperty("settle")
	private DZFBoolean issettle;//已结账
	@JsonProperty("ygcz")
	private DZFDouble plansalvage;//预估残值
	@JsonProperty("adate")
	private DZFDate accountdate;//入账日期(开始使用日期)
	@JsonProperty("id_assetcard")
	private String pk_assetcard;
	@JsonProperty("id_voucher")
	private String pk_voucher;
	@JsonProperty("depperiod")
	private Integer depreciationperiod;//总累计折旧期间数(月)
	@JsonProperty("vf2")
	private String vdef2;
	@JsonProperty("vf5")
	private String vdef5;
	@JsonProperty("memo")
	private String memo;
	@JsonProperty("vf3")
	private String vdef3;
	@JsonProperty("vf6")
	private String vdef6;
	@JsonProperty("depation")
	private DZFDouble depreciation;//总累计折旧
	@JsonProperty("dr")
	private Integer dr;
	@JsonProperty("ddate")
	private DZFDate doperatedate;
	@JsonProperty("vf4")
	private String vdef4;
	@JsonProperty("uperiod")
	private Integer usedperiod;//总累计使用期间数(月)
	@JsonProperty("accounttion")
	private DZFDouble accountdepreciation;//建账累计折旧
	@JsonProperty("clear")
	private DZFBoolean isclear;//已清理
	@JsonProperty("ulimit")
	private Integer uselimit;//可使用月份
	@JsonProperty("usedperiod")
	private Integer accountusedperiod;//建账已使用期间数(月)
	@JsonProperty("depdate")
	private String depreciationdate;//折旧月份
	@JsonProperty("dperiod")
	private DZFDate period;//录入期间
	@JsonProperty("yzrzkm_id")
	private String pk_yzrzkm;//原值入账科目
	@JsonProperty("yzrzkm")
	private String yzrzkm;//原值入账科目
	@JsonProperty("zcbm")
	private String zccode;//资产编码
	@JsonProperty("zjkm_id")
	private String pk_zjkm;//折旧(摊销)科目
	@JsonProperty("zjkm")
	private String zjkm;//折旧(摊销)科目
	@JsonProperty("fykm_id")
	private String pk_fykm;//费用科目
	@JsonProperty("fykm")
	private String fykm;//费用科目
	@JsonProperty("qcvalue")
	private DZFDouble qcnetvalue;//期初净值
	@JsonProperty("nmzj")
	private DZFDouble monthzj;//月折旧
	@JsonProperty("onetimezj")
	private DZFBoolean onetimedep;//录入当期一次提足折旧
	@JsonProperty("zjtype")
	private Integer zjtype;//折旧方式 0:平均年限法,1:工作量法,2:双倍余额递减法，3:年数总和法
	private String zjtypestr;//导出excel使用
	
	@JsonProperty("assetcate")
	private String assetcate;//资产类别名称
	private String assetcateall;//资产类别全称
	
	@JsonProperty("assetproperty")
	private String assetproperty;//资产类别属性
	
	@JsonProperty("voucherno")
	private String voucherno;//凭证号
	
	private DZFDouble gzzl;//工作总量
	
	private String gzldw;//工作量单位
	
	private	DZFDouble syljgzl;//上月累计工作量
	
	private DZFDouble bygzl;//本月工作量
	
	private DZFDouble qcljgzl;//期初累计工作量
	
	private DZFDouble sygzl;//剩余工作量
	
	private DZFDouble dwzj;//单位折旧
	
	private DZFDouble ljgzl;//累计工作量
	
	
	@JsonProperty("zckm_id")
	private String pk_zckm;//资产pk
	private String zckm;
	private String zckmcode;
	@JsonProperty("jskm_id")
    private String pk_jskm;//结算科目
	//---结算科目辅助核算
	@JsonProperty("jsfzhs1")
	private String jsfzhsx1;// 辅助核算客户
	@JsonProperty("jsfzhs2")
	private String jsfzhsx2;// 辅助核算供应商
	@JsonProperty("jsfzhs3")
	private String jsfzhsx3;// 辅助核算职员
	@JsonProperty("jsfzhs4")
	private String jsfzhsx4;// 辅助核算供项目
	@JsonProperty("jsfzhs5")
	private String jsfzhsx5;// 辅助核算部门
	@JsonProperty("jsfzhs6")
	private String jsfzhsx6;// 辅助核算存货
	@JsonProperty("jsfzhs7")
	private String jsfzhsx7;// 辅助核算自定义1
	@JsonProperty("jsfzhs8")
	private String jsfzhsx8;// 辅助核算自定义2
	@JsonProperty("jsfzhs9")
	private String jsfzhsx9;// 辅助核算自定义3
	@JsonProperty("jsfzhs10")
	private String jsfzhsx10;// 辅助核算自定义4
	//------------结算科目辅助核算


	//---折旧费用辅助核算
	@JsonProperty("zjfyfzhs1")
	private String zjfyfzhsx1;// 辅助核算客户
	@JsonProperty("zjfyfzhs2")
	private String zjfyfzhsx2;// 辅助核算供应商
	@JsonProperty("zjfyfzhs3")
	private String zjfyfzhsx3;// 辅助核算职员
	@JsonProperty("zjfyfzhs4")
	private String zjfyfzhsx4;// 辅助核算供项目
	@JsonProperty("zjfyfzhs5")
	private String zjfyfzhsx5;// 辅助核算部门
	@JsonProperty("zjfyfzhs6")
	private String zjfyfzhsx6;// 辅助核算存货
	@JsonProperty("zjfyfzhs7")
	private String zjfyfzhsx7;// 辅助核算自定义1
	@JsonProperty("zjfyfzhs8")
	private String zjfyfzhsx8;// 辅助核算自定义2
	@JsonProperty("zjfyfzhs9")
	private String zjfyfzhsx9;// 辅助核算自定义3
	@JsonProperty("zjfyfzhs10")
	private String zjfyfzhsx10;// 辅助核算自定义4
	//------------折旧费用科目辅助核算

	private String jskm;
	private String jskmcode;
	@JsonProperty("jtzjkm_id")
    private String pk_jtzjkm;//累计折旧科目
	private String jtzjkm;
	private String jtzjkmcode;
	@JsonProperty("zjfykm_id")
    private String pk_zjfykm;//折旧费用科目
	private String zjfykm;
	private String zjfykmcode;
	@JsonProperty("jxsf")
    private DZFDouble njxsf;//进项税费
	@JsonProperty("sl")
	private DZFDouble nsl;//税率
	private DZFDouble sumres;
	//不存库
	private String chargedeptname;
	
	//------资产来源字段 pk_image_group,pk_invoice,pk_invoice_detail
	@JsonProperty("groupid")
	private String pk_image_group;
	@JsonProperty("libraryid")
	private String pk_image_library;
	@JsonProperty("fpid")
	private String pk_invoice;
	@JsonProperty("fphm")
	private String fp_hm;//发票号码
	@JsonProperty("fpmxid")
	private String pk_invoice_detail;
	@JsonProperty("sourtype")
	private String sourcetype;//来源类型
	//------资产来源字段 
	
    public static final String PK_ZCKM ="pk_zckm";
    public String getPk_image_library() {
		return pk_image_library;
	}
	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}
	public static final String PK_JSKM="pk_jskm";
    public static final String PK_JTZJKM="pk_jtzjkm";
    public static final String PK_ZJFYKM="pk_zjfykm";
    public static final String NJXSF ="njxsf";
	public static final String PK_CORP = "pk_corp";
	public static final String ASSETCODE = "assetcode";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String ISTOGL = "istogl";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String ISPERIODBEGIN = "isperiodbegin";
	public static final String SALVAGERATIO = "salvageratio";
	public static final String ACCOUNTDEPRECIATIONPERIOD = "accountdepreciationperiod";
	public static final String ASSETNETVALUE = "assetnetvalue";
	public static final String INITDEPRECIATION = "initdepreciation";
	public static final String ISSALE = "issale";
	public static final String ASSETMNY = "assetmny";
	public static final String INITUSEDPERIOD = "initusedperiod";
	public static final String SETDATE = "setdate";
	public static final String ASSETNAME = "assetname";
	public static final String INITDEPRECIATIONPERIOD = "initdepreciationperiod";
	public static final String VDEF7 = "vdef7";
	public static final String ASSETCATEGORY = "assetcategory";
	public static final String ISSETTLE = "issettle";
	public static final String PLANSALVAGE = "plansalvage";
	public static final String ACCOUNTDATE = "accountdate";
	public static final String PK_ASSETCARD = "pk_assetcard";
	public static final String PK_VOUCHER = "pk_voucher";
	public static final String DEPRECIATIONPERIOD = "depreciationperiod";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String MEMO = "memo";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String DEPRECIATION = "depreciation";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String USEDPERIOD = "usedperiod";
	public static final String ACCOUNTDEPRECIATION = "accountdepreciation";
	public static final String ISCLEAR = "isclear";
	public static final String USELIMIT = "uselimit";
	public static final String ACCOUNTUSEDPERIOD = "accountusedperiod";
	
	
	public DZFBoolean getOnetimedep() {
		return onetimedep;
	}
	public void setOnetimedep(DZFBoolean onetimedep) {
		this.onetimedep = onetimedep;
	}
	public String getSourcetype() {
		return sourcetype;
	}
	public void setSourcetype(String sourcetype) {
		this.sourcetype = sourcetype;
	}
	public String getPk_image_group() {
		return pk_image_group;
	}
	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}
	public String getPk_invoice() {
		return pk_invoice;
	}
	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}
	public String getPk_invoice_detail() {
		return pk_invoice_detail;
	}
	public void setPk_invoice_detail(String pk_invoice_detail) {
		this.pk_invoice_detail = pk_invoice_detail;
	}
	public String getChargedeptname() {
		return chargedeptname;
	}
	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}
	public String getJsfzhsx1() {
		return jsfzhsx1;
	}
	public void setJsfzhsx1(String jsfzhsx1) {
		this.jsfzhsx1 = jsfzhsx1;
	}
	public String getJsfzhsx2() {
		return jsfzhsx2;
	}
	public void setJsfzhsx2(String jsfzhsx2) {
		this.jsfzhsx2 = jsfzhsx2;
	}
	public String getJsfzhsx3() {
		return jsfzhsx3;
	}
	public void setJsfzhsx3(String jsfzhsx3) {
		this.jsfzhsx3 = jsfzhsx3;
	}
	public String getJsfzhsx4() {
		return jsfzhsx4;
	}
	public void setJsfzhsx4(String jsfzhsx4) {
		this.jsfzhsx4 = jsfzhsx4;
	}
	public String getJsfzhsx5() {
		return jsfzhsx5;
	}
	public void setJsfzhsx5(String jsfzhsx5) {
		this.jsfzhsx5 = jsfzhsx5;
	}
	public String getJsfzhsx6() {
		return jsfzhsx6;
	}
	public void setJsfzhsx6(String jsfzhsx6) {
		this.jsfzhsx6 = jsfzhsx6;
	}
	public String getJsfzhsx7() {
		return jsfzhsx7;
	}
	public void setJsfzhsx7(String jsfzhsx7) {
		this.jsfzhsx7 = jsfzhsx7;
	}
	public String getJsfzhsx8() {
		return jsfzhsx8;
	}
	public void setJsfzhsx8(String jsfzhsx8) {
		this.jsfzhsx8 = jsfzhsx8;
	}
	public String getJsfzhsx9() {
		return jsfzhsx9;
	}
	public void setJsfzhsx9(String jsfzhsx9) {
		this.jsfzhsx9 = jsfzhsx9;
	}
	public String getJsfzhsx10() {
		return jsfzhsx10;
	}
	public void setJsfzhsx10(String jsfzhsx10) {
		this.jsfzhsx10 = jsfzhsx10;
	}
	public DZFDouble getNsl() {
		return nsl;
	}
	public void setNsl(DZFDouble nsl) {
		this.nsl = nsl;
	}
	public String getPk_zckm() {
		return pk_zckm;
	}
	public void setPk_zckm(String pk_zckm) {
		this.pk_zckm = pk_zckm;
	}
	public String getPk_jskm() {
		return pk_jskm;
	}
	public void setPk_jskm(String pk_jskm) {
		this.pk_jskm = pk_jskm;
	}
	public String getPk_jtzjkm() {
		return pk_jtzjkm;
	}
	public void setPk_jtzjkm(String pk_jtzjkm) {
		this.pk_jtzjkm = pk_jtzjkm;
	}
	public String getPk_zjfykm() {
		return pk_zjfykm;
	}
	public void setPk_zjfykm(String pk_zjfykm) {
		this.pk_zjfykm = pk_zjfykm;
	}
	public DZFDouble getNjxsf() {
		return njxsf;
	}
	public void setNjxsf(DZFDouble njxsf) {
		this.njxsf = njxsf;
	}

	public String getZjfyfzhsx1() {
		return zjfyfzhsx1;
	}

	public void setZjfyfzhsx1(String zjfyfzhsx1) {
		this.zjfyfzhsx1 = zjfyfzhsx1;
	}

	public String getZjfyfzhsx2() {
		return zjfyfzhsx2;
	}

	public void setZjfyfzhsx2(String zjfyfzhsx2) {
		this.zjfyfzhsx2 = zjfyfzhsx2;
	}

	public String getZjfyfzhsx3() {
		return zjfyfzhsx3;
	}

	public void setZjfyfzhsx3(String zjfyfzhsx3) {
		this.zjfyfzhsx3 = zjfyfzhsx3;
	}

	public String getZjfyfzhsx4() {
		return zjfyfzhsx4;
	}

	public void setZjfyfzhsx4(String zjfyfzhsx4) {
		this.zjfyfzhsx4 = zjfyfzhsx4;
	}

	public String getZjfyfzhsx5() {
		return zjfyfzhsx5;
	}

	public void setZjfyfzhsx5(String zjfyfzhsx5) {
		this.zjfyfzhsx5 = zjfyfzhsx5;
	}

	public String getZjfyfzhsx6() {
		return zjfyfzhsx6;
	}

	public void setZjfyfzhsx6(String zjfyfzhsx6) {
		this.zjfyfzhsx6 = zjfyfzhsx6;
	}

	public String getZjfyfzhsx7() {
		return zjfyfzhsx7;
	}

	public void setZjfyfzhsx7(String zjfyfzhsx7) {
		this.zjfyfzhsx7 = zjfyfzhsx7;
	}

	public String getZjfyfzhsx8() {
		return zjfyfzhsx8;
	}

	public void setZjfyfzhsx8(String zjfyfzhsx8) {
		this.zjfyfzhsx8 = zjfyfzhsx8;
	}

	public String getZjfyfzhsx9() {
		return zjfyfzhsx9;
	}

	public void setZjfyfzhsx9(String zjfyfzhsx9) {
		this.zjfyfzhsx9 = zjfyfzhsx9;
	}

	public String getZjfyfzhsx10() {
		return zjfyfzhsx10;
	}

	public void setZjfyfzhsx10(String zjfyfzhsx10) {
		this.zjfyfzhsx10 = zjfyfzhsx10;
	}

	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性assetcode的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getAssetcode () {
		return assetcode;
	}   
	/**
	 * 属性assetcode的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAssetcode String
	 */
	public void setAssetcode (String newAssetcode ) {
	 	this.assetcode = newAssetcode;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	
	public String getZckm() {
		return zckm;
	}
	public void setZckm(String zckm) {
		this.zckm = zckm;
	}
	public String getJskm() {
		return jskm;
	}
	public void setJskm(String jskm) {
		this.jskm = jskm;
	}
	public String getJtzjkm() {
		return jtzjkm;
	}
	public void setJtzjkm(String jtzjkm) {
		this.jtzjkm = jtzjkm;
	}
	public String getZjfykm() {
		return zjfykm;
	}
	public void setZjfykm(String zjfykm) {
		this.zjfykm = zjfykm;
	}
	public DZFDouble getSumres() {
		return sumres;
	}
	public void setSumres(DZFDouble sumres) {
		this.sumres = sumres;
	}
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性istogl的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFBoolean
	 */
	public DZFBoolean getIstogl () {
		return istogl;
	}   
	/**
	 * 属性istogl的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newIstogl DZFBoolean
	 */
	public void setIstogl (DZFBoolean newIstogl ) {
	 	this.istogl = newIstogl;
	} 	  
	public DZFDouble getAccountmny() {
		return accountmny;
	}
	public void setAccountmny(DZFDouble accountmny) {
		this.accountmny = accountmny;
	}
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性isperiodbegin的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFBoolean
	 */
	public DZFBoolean getIsperiodbegin () {
		return isperiodbegin;
	}   
	/**
	 * 属性isperiodbegin的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newIsperiodbegin DZFBoolean
	 */
	public void setIsperiodbegin (DZFBoolean newIsperiodbegin ) {
	 	this.isperiodbegin = newIsperiodbegin;
	} 	  
	/**
	 * 属性salvageratio的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getSalvageratio () {
		return salvageratio;
	}   
	/**
	 * 属性salvageratio的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newSalvageratio DZFDouble
	 */
	public void setSalvageratio (DZFDouble newSalvageratio ) {
	 	this.salvageratio = newSalvageratio;
	} 	  
	/**
	 * 属性accountdepreciationperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getAccountdepreciationperiod () {
		return accountdepreciationperiod;
	}   
	/**
	 * 属性accountdepreciationperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAccountdepreciationperiod DZFDouble
	 */
	public void setAccountdepreciationperiod (Integer newAccountdepreciationperiod ) {
	 	this.accountdepreciationperiod = newAccountdepreciationperiod;
	} 	  
	/**
	 * 属性assetnetvalue的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getAssetnetvalue () {
		return assetnetvalue;
	}   
	/**
	 * 属性assetnetvalue的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAssetnetvalue DZFDouble
	 */
	public void setAssetnetvalue (DZFDouble newAssetnetvalue ) {
	 	this.assetnetvalue = newAssetnetvalue;
	} 	  
	public String getDepreciationdate() {
		return depreciationdate;
	}
	public void setDepreciationdate(String depreciationdate) {
		this.depreciationdate = depreciationdate;
	}
	/**
	 * 属性initdepreciation的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getInitdepreciation () {
		return initdepreciation;
	}   
	/**
	 * 属性initdepreciation的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newInitdepreciation DZFDouble
	 */
	public void setInitdepreciation (DZFDouble newInitdepreciation ) {
	 	this.initdepreciation = newInitdepreciation;
	} 	   
	/**
	 * 属性assetmny的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getAssetmny () {
		return assetmny;
	}   
	/**
	 * 属性assetmny的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAssetmny DZFDouble
	 */
	public void setAssetmny (DZFDouble newAssetmny ) {
	 	this.assetmny = newAssetmny;
	} 	  
	/**
	 * 属性initusedperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getInitusedperiod () {
		return initusedperiod;
	}   
	/**
	 * 属性initusedperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newInitusedperiod DZFDouble
	 */
	public void setInitusedperiod (Integer newInitusedperiod ) {
	 	this.initusedperiod = newInitusedperiod;
	} 	  
	/**
	 * 属性setdate的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDate
	 */
	public DZFDate getSetdate () {
		return setdate;
	}   
	/**
	 * 属性setdate的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newSetdate DZFDate
	 */
	public void setSetdate (DZFDate newSetdate ) {
	 	this.setdate = newSetdate;
	} 	  
	/**
	 * 属性assetname的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getAssetname () {
		return assetname;
	}   
	/**
	 * 属性assetname的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAssetname String
	 */
	public void setAssetname (String newAssetname ) {
	 	this.assetname = newAssetname;
	} 	  
	/**
	 * 属性initdepreciationperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getInitdepreciationperiod () {
		return initdepreciationperiod;
	}   
	/**
	 * 属性initdepreciationperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newInitdepreciationperiod DZFDouble
	 */
	public void setInitdepreciationperiod (Integer newInitdepreciationperiod ) {
	 	this.initdepreciationperiod = newInitdepreciationperiod;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
	/**
	 * 属性assetcategory的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getAssetcategory () {
		return assetcategory;
	}   
	/**
	 * 属性assetcategory的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAssetcategory String
	 */
	public void setAssetcategory (String newAssetcategory ) {
	 	this.assetcategory = newAssetcategory;
	} 	  
	/**
	 * 属性issettle的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFBoolean
	 */
	public DZFBoolean getIssettle () {
		return issettle;
	}   
	/**
	 * 属性issettle的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newIssettle DZFBoolean
	 */
	public void setIssettle (DZFBoolean newIssettle ) {
	 	this.issettle = newIssettle;
	} 	  
	/**
	 * 属性plansalvage的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getPlansalvage () {
		return plansalvage;
	}   
	/**
	 * 属性plansalvage的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newPlansalvage DZFDouble
	 */
	public void setPlansalvage (DZFDouble newPlansalvage ) {
	 	this.plansalvage = newPlansalvage;
	} 	  
	/**
	 * 属性accountdate的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDate
	 */
	public DZFDate getAccountdate () {
		return accountdate;
	}   
	/**
	 * 属性accountdate的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAccountdate DZFDate
	 */
	public void setAccountdate (DZFDate newAccountdate ) {
	 	this.accountdate = newAccountdate;
	} 	  
	/**
	 * 属性pk_assetcard的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getPk_assetcard () {
		return pk_assetcard;
	}   
	/**
	 * 属性pk_assetcard的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newPk_assetcard String
	 */
	public void setPk_assetcard (String newPk_assetcard ) {
	 	this.pk_assetcard = newPk_assetcard;
	} 	  
	/**
	 * 属性pk_voucher的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getPk_voucher () {
		return pk_voucher;
	}   
	/**
	 * 属性pk_voucher的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newPk_voucher String
	 */
	public void setPk_voucher (String newPk_voucher ) {
	 	this.pk_voucher = newPk_voucher;
	} 	  
	/**
	 * 属性depreciationperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getDepreciationperiod () {
		return depreciationperiod;
	}   
	/**
	 * 属性depreciationperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newDepreciationperiod DZFDouble
	 */
	public void setDepreciationperiod (Integer newDepreciationperiod ) {
	 	this.depreciationperiod = newDepreciationperiod;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性depreciation的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getDepreciation () {
		return depreciation;
	}   
	/**
	 * 属性depreciation的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newDepreciation DZFDouble
	 */
	public void setDepreciation (DZFDouble newDepreciation ) {
	 	this.depreciation = newDepreciation;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性usedperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getUsedperiod () {
		return usedperiod;
	}   
	/**
	 * 属性usedperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newUsedperiod DZFDouble
	 */
	public void setUsedperiod (Integer newUsedperiod ) {
	 	this.usedperiod = newUsedperiod;
	} 	  
	/**
	 * 属性accountdepreciation的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public DZFDouble getAccountdepreciation () {
		return accountdepreciation;
	}   
	/**
	 * 属性accountdepreciation的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAccountdepreciation DZFDouble
	 */
	public void setAccountdepreciation (DZFDouble newAccountdepreciation ) {
	 	this.accountdepreciation = newAccountdepreciation;
	} 	  
	/**
	 * 属性isclear的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFBoolean
	 */
	public DZFBoolean getIsclear () {
		return isclear;
	}   
	/**
	 * 属性isclear的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newIsclear DZFBoolean
	 */
	public void setIsclear (DZFBoolean newIsclear ) {
	 	this.isclear = newIsclear;
	} 	  
	/**
	 * 属性uselimit的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getUselimit () {
		return uselimit;
	}   
	/**
	 * 属性uselimit的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newUselimit DZFDouble
	 */
	public void setUselimit (Integer newUselimit ) {
	 	this.uselimit = newUselimit;
	} 	  
	/**
	 * 属性accountusedperiod的Getter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @return DZFDouble
	 */
	public Integer getAccountusedperiod () {
		return accountusedperiod;
	}   
	/**
	 * 属性accountusedperiod的Setter方法.
	 * 创建日期:2014-11-01 16:41:33
	 * @param newAccountusedperiod DZFDouble
	 */
	public void setAccountusedperiod (Integer newAccountusedperiod ) {
	 	this.accountusedperiod = newAccountusedperiod;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-01 16:41:33
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-01 16:41:33
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return PK_FILED;
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-01 16:41:33
	 * @return java.lang.String
	 */
	public String getTableName() {
		return TABLE_NAME;
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-01 16:41:33
	  */
     public AssetcardVO() {
		super();	
	}

    public String getFp_hm() {
        return fp_hm;
    }

    public void setFp_hm(String fp_hm) {
        this.fp_hm = fp_hm;
    }

    public DZFDate getPeriod() {
		return period;
	}
	public void setPeriod(DZFDate period) {
		this.period = period;
	}
	public String getPk_yzrzkm() {
		return pk_yzrzkm;
	}
	public void setPk_yzrzkm(String pk_yzrzkm) {
		this.pk_yzrzkm = pk_yzrzkm;
	}
	public String getZccode() {
		return zccode;
	}
	public void setZccode(String zccode) {
		this.zccode = zccode;
	}
	public String getPk_zjkm() {
		return pk_zjkm;
	}
	public void setPk_zjkm(String pk_zjkm) {
		this.pk_zjkm = pk_zjkm;
	}
	public String getPk_fykm() {
		return pk_fykm;
	}
	public void setPk_fykm(String pk_fykm) {
		this.pk_fykm = pk_fykm;
	}
	public DZFDouble getQcnetvalue() {
		return qcnetvalue;
	}
	public void setQcnetvalue(DZFDouble qcnetvalue) {
		this.qcnetvalue = qcnetvalue;
	}
	public DZFDouble getMonthzj() {
		return monthzj;
	}
	public void setMonthzj(DZFDouble monthzj) {
		this.monthzj = monthzj;
	}
	public Integer getZjtype() {
		return zjtype;
	}
	public void setZjtype(Integer zjtype) {
		this.zjtype = zjtype;
	}
	public String getAssetproperty() {
		return assetproperty;
	}
	public void setAssetproperty(String assetproperty) {
		this.assetproperty = assetproperty;
	}
	public String getAssetcate() {
		return assetcate;
	}
	public void setAssetcate(String assetcate) {
		this.assetcate = assetcate;
	}
	public String getVoucherno() {
		return voucherno;
	}
	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}
	public String getYzrzkm() {
		return yzrzkm;
	}
	public void setYzrzkm(String yzrzkm) {
		this.yzrzkm = yzrzkm;
	}
	public String getZjkm() {
		return zjkm;
	}
	public void setZjkm(String zjkm) {
		this.zjkm = zjkm;
	}
	public String getFykm() {
		return fykm;
	}
	public void setFykm(String fykm) {
		this.fykm = fykm;
	}
	public DZFDouble getGzzl() {
		return gzzl;
	}
	public void setGzzl(DZFDouble gzzl) {
		this.gzzl = gzzl;
	}
	public String getGzldw() {
		return gzldw;
	}
	public void setGzldw(String gzldw) {
		this.gzldw = gzldw;
	}
	public DZFDouble getSyljgzl() {
		return syljgzl;
	}
	public void setSyljgzl(DZFDouble syljgzl) {
		this.syljgzl = syljgzl;
	}
	public DZFDouble getBygzl() {
		return bygzl;
	}
	public void setBygzl(DZFDouble bygzl) {
		this.bygzl = bygzl;
	}
	public DZFDouble getQcljgzl() {
		return qcljgzl;
	}
	public void setQcljgzl(DZFDouble qcljgzl) {
		this.qcljgzl = qcljgzl;
	}
	public DZFDouble getSygzl() {
		return sygzl;
	}
	public void setSygzl(DZFDouble sygzl) {
		this.sygzl = sygzl;
	}
	public DZFDouble getDwzj() {
		return dwzj;
	}
	public void setDwzj(DZFDouble dwzj) {
		this.dwzj = dwzj;
	}
	public DZFDouble getLjgzl() {
		return ljgzl;
	}
	public void setLjgzl(DZFDouble ljgzl) {
		this.ljgzl = ljgzl;
	}
	public String getZjtypestr() {
		return zjtypestr;
	}
	public void setZjtypestr(String zjtypestr) {
		this.zjtypestr = zjtypestr;
	}
	public String getZckmcode() {
		return zckmcode;
	}
	public void setZckmcode(String zckmcode) {
		this.zckmcode = zckmcode;
	}
	public String getJskmcode() {
		return jskmcode;
	}
	public void setJskmcode(String jskmcode) {
		this.jskmcode = jskmcode;
	}
	public String getJtzjkmcode() {
		return jtzjkmcode;
	}
	public void setJtzjkmcode(String jtzjkmcode) {
		this.jtzjkmcode = jtzjkmcode;
	}
	public String getZjfykmcode() {
		return zjfykmcode;
	}
	public void setZjfykmcode(String zjfykmcode) {
		this.zjfykmcode = zjfykmcode;
	}
	public String getAssetcateall() {
		return assetcateall;
	}
	public void setAssetcateall(String assetcateall) {
		this.assetcateall = assetcateall;
	}   
	
} 


