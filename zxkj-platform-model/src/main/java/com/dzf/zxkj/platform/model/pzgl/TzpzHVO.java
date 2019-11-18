package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 填制凭证主表 </b> 创建日期:2014-09-26 12:14:28
 * 
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class TzpzHVO extends SuperVO<TzpzBVO> {
	@JsonProperty("corpId")
	private String pk_corp;
	@JsonProperty("ts")
	private DZFDateTime ts;
	private String vdef9;
	@JsonProperty("zdrid")
	// 制单人
	private String coperatorid;
	private String zd_user;
	private String vdef1;
	private String vdef8;
	private String vdef10;
	@JsonProperty("shr")
	// 审核人
	private String vapproveid;
	private String sh_user;
	private String pzh;
	@JsonProperty("shpy")
	// 审核批语
	private String vapprovenote;
	@JsonProperty("bz")
	// 币种
	private String pk_currency;
	@JsonProperty("jfhj")
	// 借方合计
	private DZFDouble jfmny;
	@JsonProperty("dfhj")
	// 贷方合计
	private DZFDouble dfmny;
	private String vdef7;
	@JsonProperty("shrq")
	private DZFDate dapprovedate;
	private String vdef2;
	private String vdef5;
	@JsonProperty("djlx")
	// 单据类型
	private String pk_billtype;
	@JsonProperty("pzzt")
	// 单据状态 1:审核通过，8：自由态
	private Integer vbillstatus;
	@JsonProperty("memo")
	// 备注
	private String memo;
	@JsonProperty("id")
	// 主键
	private String pk_tzpz_h;
	private String vdef3;
	private String vdef6;
	@JsonProperty("djh")
	// 单据号
	private String vbillno;
	private Integer dr;
	@JsonProperty("zdrq")
	private DZFDate doperatedate;
	private String vdef4;
	@JsonProperty("pzlb")
	private Integer pzlb;
	@JsonProperty("sfjz")
	// 是否已记账 Y(是):已记账，N(否):未记账
	private DZFBoolean ishasjz;
	@JsonProperty("jzr")
	// 记账人
	private String vjzoperatorid;
	private String jz_user;
	@JsonProperty("jzrq")
	// 记账日期
	private DZFDate djzdate;
	@JsonProperty("fdjs")
	// 附单据数
	private Integer nbills;
	@JsonProperty("lydjlx")
	// 来源单据类型
	private String sourcebilltype;
	@JsonProperty("lydjid")
	// 来源单据ID
	private String sourcebillid;
	// 来源
	private PzSourceRelationVO[] source_relation;
	@JsonProperty("sffpxjll")
	// 是否已分配现金流量项目
	private DZFBoolean isfpxjxm;
	// 是否现金流量分析错误
	private Boolean error_cash_analyse;
	// 是否生成税表表项
	private Boolean is_tax_analyse;
	// 税表表项分析有误
	private Boolean error_tax_analyse;
	@JsonProperty("tpgid")
	// 图片主键
	private String pk_image_group;
	@JsonProperty("tplid")
	private String pk_image_library;
	// 如果已经期间损益是否强制保存
	private DZFBoolean isqxsy;
	// 如果关联图片，删除凭证是否退回
	private DZFBoolean issvbk;
	// 如果总账库存 校验不通过，是否保存。
	private DZFBoolean isglicsave;

	private String cbjzCount;// 工业成本结转 步骤 123456

	private int ifptype;// 0-- 销项 // 1---进项 // 2---其他
	private Integer ifeetype;// 费用类型 1  费用类
	private int iautorecognize;// 0-- 非识别 1----识别
	private DZFBoolean isocr;// ocr会预置凭证号 不需要重新生成
	private String pk_model_h;
	private String busitypetempname;

	// 凭证状态
	// FieldValidate("凭证状态不能为空:vbillstatus is not null;") 后台注值
	// @JsonProperty("tpzt")
	// private Integer vbillstatus;
	// 图片上传时间？
	@JsonProperty("ptime")
	private DZFDateTime photots;
	@JsonProperty("pname")
	private String phototname;

	@JsonProperty("qj")
	private String period;// 期间
	@JsonProperty("nd")
	private Integer vyear;// 年度
	// 导入使用[导入凭证字号]
	private String pzh_no;
	
//	private DZFBoolean isNoCreateIc;//启用库存老模式 是否生成出入库,不存库,zpm还原老模式总账推库存钊宁代码

	// 查询专用
	private String qrydope;
	private String qrypzh;
	// 是否导入
	private DZFBoolean isimp;// 会计工厂也使用该字段，生成的凭证不需要重新生成凭证号
	private DZFBoolean autoAnaly;// 保存修改时是否做了现金流量自动分析
	/**
	 * 1普票 2专票3未开票
	 */
	private Integer fp_style;

	@JsonProperty("bqz")
	private DZFBoolean bsign;// 出纳是否签字
	private String cn_user;
	@JsonProperty("cnpeo")
	private String vcashid;// 出纳签字人
	@JsonProperty("cnqzdate")
	private DZFDate dcashdate;// 出纳签字日期

	private String isInsert; // 是否插入凭证
	
//	private DZFBoolean isNumNull;//
	
	private String isMerge; // 是否合并凭证
	// 是否截断摘要 200个字符
	private String iscutzy;
	// 保留设置的凭证号
	private Boolean preserveCode;
	private String vicbillcode;//总账存货模块新增字段 ;库存单据号。
	private String vicbillcodetype;//总账存货模块新增字段 ;单据类型。

	private Object userObject;//智能财税

	// 合并分组
	// 往来
	private Boolean group_connect;
	// 票据类型
	private Boolean group_invoice;
	//add mfz 是否在线会计生成税目
	private DZFBoolean isbsctaxitem;
	
	private String passSyjz;//跳过损益结转校验
	
	public static final String BSIGN = "bsign";
	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String VAPPROVEID = "vapproveid";
	public static final String DFMNY = "dfmny";
	public static final String PZH = "pzh";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String PK_CURRENCY = "pk_currency";
	public static final String JFMNY = "jfmny";
	public static final String VDEF7 = "vdef7";
	public static final String DAPPROVEDATE = "dapprovedate";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String VBILLSTATUS = "vbillstatus";
	public static final String MEMO = "memo";
	public static final String PK_TZPZ_H = "pk_tzpz_h";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String VBILLNO = "vbillno";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String PZLB = "pzlb";
	public static final String VCASHID = "vcashid";
	public static final String DCASHDATE = "dcashdate";
	

	

	public String getPassSyjz() {
		return passSyjz;
	}

	public void setPassSyjz(String passSyjz) {
		this.passSyjz = passSyjz;
	}

	public DZFBoolean getIsbsctaxitem() {
		return isbsctaxitem;
	}

	public void setIsbsctaxitem(DZFBoolean isbsctaxitem) {
		this.isbsctaxitem = isbsctaxitem;
	}

	public Boolean getGroup_connect() {
		return group_connect;
	}

	public void setGroup_connect(Boolean group_connect) {
		this.group_connect = group_connect;
	}

	public Boolean getGroup_invoice() {
		return group_invoice;
	}

	public void setGroup_invoice(Boolean group_invoice) {
		this.group_invoice = group_invoice;
	}

	public Boolean getIs_tax_analyse() {
		return is_tax_analyse;
	}

	public void setIs_tax_analyse(Boolean is_tax_analyse) {
		this.is_tax_analyse = is_tax_analyse;
	}

	public Boolean getError_tax_analyse() {
		return error_tax_analyse;
	}

	public void setError_tax_analyse(Boolean error_tax_analyse) {
		this.error_tax_analyse = error_tax_analyse;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	public String getCbjzCount() {
		return cbjzCount;
	}

	public void setCbjzCount(String cbjzCount) {
		this.cbjzCount = cbjzCount;
	}

	/**
	 * 属性pk_corp的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_corp() {
		return pk_corp;
	}

	/**
	 * 属性pk_corp的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_corp
	 *            String
	 */
	public void setPk_corp(String newPk_corp) {
		this.pk_corp = newPk_corp;
	}

	public DZFDateTime getPhotots() {
		return photots;
	}

	public void setPhotots(DZFDateTime photots) {
		this.photots = photots;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newTs
	 *            DZFDateTime
	 */
	public void setTs(DZFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * 属性vdef9的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef9() {
		return vdef9;
	}

	/**
	 * 属性vdef9的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef9
	 *            String
	 */
	public void setVdef9(String newVdef9) {
		this.vdef9 = newVdef9;
	}

	/**
	 * 属性coperatorid的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getCoperatorid() {
		return coperatorid;
	}

	/**
	 * 属性coperatorid的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newCoperatorid
	 *            String
	 */
	public void setCoperatorid(String newCoperatorid) {
		this.coperatorid = newCoperatorid;
	}

	/**
	 * 属性vdef1的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef1() {
		return vdef1;
	}

	/**
	 * 属性vdef1的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef1
	 *            String
	 */
	public void setVdef1(String newVdef1) {
		this.vdef1 = newVdef1;
	}

	/**
	 * 属性vdef8的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef8() {
		return vdef8;
	}

	/**
	 * 属性vdef8的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef8
	 *            String
	 */
	public void setVdef8(String newVdef8) {
		this.vdef8 = newVdef8;
	}

	/**
	 * 属性vdef10的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef10() {
		return vdef10;
	}

	/**
	 * 属性vdef10的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef10
	 *            String
	 */
	public void setVdef10(String newVdef10) {
		this.vdef10 = newVdef10;
	}

	/**
	 * 属性vapproveid的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVapproveid() {
		return vapproveid;
	}

	/**
	 * 属性vapproveid的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVapproveid
	 *            String
	 */
	public void setVapproveid(String newVapproveid) {
		this.vapproveid = newVapproveid;
	}

	/**
	 * 属性dfmny的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getDfmny() {
		return dfmny;
	}

	/**
	 * 属性dfmny的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDfmny
	 *            DZFDouble
	 */
	public void setDfmny(DZFDouble newDfmny) {
		this.dfmny = newDfmny;
	}

	/**
	 * 属性pzh的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPzh() {
		return pzh;
	}

	/**
	 * 属性pzh的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPzh
	 *            String
	 */
	public void setPzh(String newPzh) {
		this.pzh = newPzh;
	}

	/**
	 * 属性vapprovenote的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVapprovenote() {
		return vapprovenote;
	}

	/**
	 * 属性vapprovenote的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVapprovenote
	 *            String
	 */
	public void setVapprovenote(String newVapprovenote) {
		this.vapprovenote = newVapprovenote;
	}

	/**
	 * 属性pk_currency的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_currency() {
		return pk_currency;
	}

	/**
	 * 属性pk_currency的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_currency
	 *            String
	 */
	public void setPk_currency(String newPk_currency) {
		this.pk_currency = newPk_currency;
	}

	/**
	 * 属性jfmny的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getJfmny() {
		return jfmny;
	}

	/**
	 * 属性jfmny的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newJfmny
	 *            DZFDouble
	 */
	public void setJfmny(DZFDouble newJfmny) {
		this.jfmny = newJfmny;
	}

	/**
	 * 属性vdef7的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef7() {
		return vdef7;
	}

	/**
	 * 属性vdef7的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef7
	 *            String
	 */
	public void setVdef7(String newVdef7) {
		this.vdef7 = newVdef7;
	}

	/**
	 * 属性dapprovedate的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate() {
		return dapprovedate;
	}

	/**
	 * 属性dapprovedate的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDapprovedate
	 *            DZFDate
	 */
	public void setDapprovedate(DZFDate newDapprovedate) {
		this.dapprovedate = newDapprovedate;
	}

	/**
	 * 属性vdef2的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef2() {
		return vdef2;
	}

	/**
	 * 属性vdef2的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef2
	 *            String
	 */
	public void setVdef2(String newVdef2) {
		this.vdef2 = newVdef2;
	}

	/**
	 * 属性vdef5的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef5() {
		return vdef5;
	}

	/**
	 * 属性vdef5的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef5
	 *            String
	 */
	public void setVdef5(String newVdef5) {
		this.vdef5 = newVdef5;
	}

	/**
	 * 属性pk_billtype的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_billtype() {
		return pk_billtype;
	}

	/**
	 * 属性pk_billtype的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_billtype
	 *            String
	 */
	public void setPk_billtype(String newPk_billtype) {
		this.pk_billtype = newPk_billtype;
	}

	/**
	 * 属性vbillstatus的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getVbillstatus() {
		return vbillstatus;
	}

	/**
	 * 属性vbillstatus的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVbillstatus
	 *            DZFDouble
	 */
	public void setVbillstatus(Integer newVbillstatus) {
		this.vbillstatus = newVbillstatus;
	}

	/**
	 * 属性memo的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 属性memo的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newMemo
	 *            String
	 */
	public void setMemo(String newMemo) {
		this.memo = newMemo;
	}

	/**
	 * 属性pk_tzpz_h的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	/**
	 * 属性pk_tzpz_h的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_tzpz_h
	 *            String
	 */
	public void setPk_tzpz_h(String newPk_tzpz_h) {
		this.pk_tzpz_h = newPk_tzpz_h;
	}

	/**
	 * 属性vdef3的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef3() {
		return vdef3;
	}

	/**
	 * 属性vdef3的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef3
	 *            String
	 */
	public void setVdef3(String newVdef3) {
		this.vdef3 = newVdef3;
	}

	/**
	 * 属性vdef6的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef6() {
		return vdef6;
	}

	/**
	 * 属性vdef6的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef6
	 *            String
	 */
	public void setVdef6(String newVdef6) {
		this.vdef6 = newVdef6;
	}

	/**
	 * 属性vbillno的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVbillno() {
		return vbillno;
	}

	/**
	 * 属性vbillno的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVbillno
	 *            String
	 */
	public void setVbillno(String newVbillno) {
		this.vbillno = newVbillno;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDr
	 *            DZFDouble
	 */
	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性doperatedate的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	/**
	 * 属性doperatedate的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDoperatedate
	 *            DZFDate
	 */
	public void setDoperatedate(DZFDate newDoperatedate) {
		this.doperatedate = newDoperatedate;
	}

	/**
	 * 属性vdef4的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef4() {
		return vdef4;
	}

	/**
	 * 属性vdef4的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef4
	 *            String
	 */
	public void setVdef4(String newVdef4) {
		this.vdef4 = newVdef4;
	}

	/**
	 * 属性pzlb的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getPzlb() {
		return pzlb;
	}

	/**
	 * 属性pzlb的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPzlb
	 *            DZFDouble
	 */
	public void setPzlb(Integer newPzlb) {
		this.pzlb = newPzlb;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 * 
	 * @return java.lang.String
	 */
	public String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 * 
	 * @return java.lang.String
	 */
	public String getPKFieldName() {
		return "pk_tzpz_h";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tzpz_h";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2014-09-26 12:14:28
	 */
	public TzpzHVO() {
		super();
	}

	public DZFDate getDjzdate() {
		return djzdate;
	}

	public void setDjzdate(DZFDate djzdate) {
		this.djzdate = djzdate;
	}

	public DZFBoolean getIshasjz() {
		return ishasjz;
	}

	public void setIshasjz(DZFBoolean ishasjz) {
		this.ishasjz = ishasjz;
	}

	public String getVjzoperatorid() {
		return vjzoperatorid;
	}

	public void setVjzoperatorid(String vjzoperatorid) {
		this.vjzoperatorid = vjzoperatorid;
	}

	public String getZd_user() {
		return zd_user;
	}

	public void setZd_user(String zd_user) {
		this.zd_user = zd_user;
	}

	public String getSh_user() {
		return sh_user;
	}

	public void setSh_user(String sh_user) {
		this.sh_user = sh_user;
	}

	public String getJz_user() {
		return jz_user;
	}

	public void setJz_user(String jz_user) {
		this.jz_user = jz_user;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public PzSourceRelationVO[] getSource_relation() {
		return source_relation;
	}

	public void setSource_relation(PzSourceRelationVO[] source_relation) {
		this.source_relation = source_relation;
	}

	public DZFBoolean getIsfpxjxm() {
		return isfpxjxm;
	}

	public void setIsfpxjxm(DZFBoolean isfpxjxm) {
		this.isfpxjxm = isfpxjxm;
	}

	public Boolean getError_cash_analyse() {
		return error_cash_analyse;
	}

	public void setError_cash_analyse(Boolean error_cash_analyse) {
		this.error_cash_analyse = error_cash_analyse;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public String getPhototname() {
		return phototname;
	}

	public void setPhototname(String phototname) {
		this.phototname = phototname;
	}

	public Integer getNbills() {
		return nbills;
	}

	public void setNbills(Integer nbills) {
		this.nbills = nbills;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getVyear() {
		return vyear;
	}

	public void setVyear(Integer vyear) {
		this.vyear = vyear;
	}

	public String getQrydope() {
		return qrydope;
	}

	public void setQrydope(String qrydope) {
		this.qrydope = qrydope;
	}

	public String getQrypzh() {
		return qrypzh;
	}

	public void setQrypzh(String qrypzh) {
		this.qrypzh = qrypzh;
	}

	public String getPzh_no() {
		return pzh_no;
	}

	public void setPzh_no(String pzh_no) {
		this.pzh_no = pzh_no;
	}

	public DZFBoolean getIsimp() {
		return isimp;
	}

	public void setIsimp(DZFBoolean isimp) {
		this.isimp = isimp;
	}

	public DZFBoolean getAutoAnaly() {
		return autoAnaly;
	}

	public void setAutoAnaly(DZFBoolean autoAnaly) {
		this.autoAnaly = autoAnaly;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}

	public DZFBoolean getIsqxsy() {
		return isqxsy;
	}

	public void setIsqxsy(DZFBoolean isqxsy) {
		this.isqxsy = isqxsy;
	}

	public DZFBoolean getIssvbk() {
		return issvbk;
	}

	public void setIssvbk(DZFBoolean issvbk) {
		this.issvbk = issvbk;
	}

	public String getVcashid() {
		return vcashid;
	}

	public void setVcashid(String vcashid) {
		this.vcashid = vcashid;
	}

	public DZFDate getDcashdate() {
		return dcashdate;
	}

	public void setDcashdate(DZFDate dcashdate) {
		this.dcashdate = dcashdate;
	}

	public String getCn_user() {
		return cn_user;
	}

	public void setCn_user(String cn_user) {
		this.cn_user = cn_user;
	}

	public DZFBoolean getBsign() {
		return bsign;
	}

	public void setBsign(DZFBoolean bsign) {
		this.bsign = bsign;
	}

	public String getIsInsert() {
		return isInsert;
	}

	public void setIsInsert(String isInsert) {
		this.isInsert = isInsert;
	}

	public String getIsMerge() {
		return isMerge;
	}

	public void setIsMerge(String isMerge) {
		this.isMerge = isMerge;
	}

	public String getIscutzy() {
		return iscutzy;
	}

	public void setIscutzy(String iscutzy) {
		this.iscutzy = iscutzy;
	}

	public Boolean getPreserveCode() {
		return preserveCode;
	}

	public void setPreserveCode(Boolean preserveCode) {
		this.preserveCode = preserveCode;
	}

	public int getIfptype() {
		return ifptype;
	}

	public void setIfptype(int ifptype) {
		this.ifptype = ifptype;
	}

	public int getIautorecognize() {
		return iautorecognize;
	}

	public void setIautorecognize(int iautorecognize) {
		this.iautorecognize = iautorecognize;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}

	public DZFBoolean getIsocr() {
		return isocr;
	}

	public void setIsocr(DZFBoolean isocr) {
		this.isocr = isocr;
	}

	public Integer getIfeetype() {
		return ifeetype;
	}

	public void setIfeetype(Integer ifeetype) {
		this.ifeetype = ifeetype;
	}

	public String getVicbillcode() {
		return vicbillcode;
	}

	public void setVicbillcode(String vicbillcode) {
		this.vicbillcode = vicbillcode;
	}

	public String getVicbillcodetype() {
		return vicbillcodetype;
	}

	public void setVicbillcodetype(String vicbillcodetype) {
		this.vicbillcodetype = vicbillcodetype;
	}

	public DZFBoolean getIsglicsave() {
		return isglicsave;
	}

	public void setIsglicsave(DZFBoolean isglicsave) {
		this.isglicsave = isglicsave;
	}

//	public DZFBoolean getIsNumNull() {
//		return isNumNull;
//	}
//
//	public void setIsNumNull(DZFBoolean isNumNull) {
//		this.isNumNull = isNumNull;
//	}
	
	// public String getVyear() {
	// return vyear;
	// }
	//
	// public void setVyear(String vyear) {
	// this.vyear = vyear;
	// }

}
