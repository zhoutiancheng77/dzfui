package com.dzf.zxkj.platform.model.pzgl;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 填制凭证主表 </b> 创建日期:2014-09-26 12:14:28
 * 
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class PzglPageVo extends SuperVO {
	@JsonProperty("bzcode")
//	币种
	private String currency_code;
//	@FieldValidate("公司编码不能为空:pk_corp is not null;凭证要有明细数据:arraysize(children)>0;")
	@JsonProperty("corpId")
	private String pk_corp;
	@JsonProperty("ts")
	private DZFDateTime ts;
	private String vdef9;
	@JsonProperty("zdr")
//	制单人
	private String zd_user;
	private String coperatorid;
	@JsonProperty("shr")
//	审核人
	private String sh_user;
	private String vapproveid;
//	@FieldValidate("凭证号不能为空:pzh is not null;")
	private String pzh;
	@JsonProperty("shpy")
//	审核批语
	private String vapprovenote;
	@JsonProperty("bz")
//	币种
	private String pk_currency;
//	@FieldValidate("凭证金额:voucherstatus in(1,8) and (jfmny is not null or dfmny is not null);")
	@JsonProperty("jfhj")
//	借方合计
	private DZFDouble jfmny;
	@JsonProperty("dfhj")
//	贷方合计
	private DZFDouble dfmny;
	@JsonProperty("shrq")
	private DZFDate dapprovedate;
	@JsonProperty("djlx")
//	单据类型
	private String pk_billtype;
//	单据状态
	@JsonProperty("pzzt")
	private Integer vbillstatus;
	@JsonProperty("memo")
//	备注
	private String memo;
	@JsonProperty("id")
//	主键
	private String pk_tzpz_h;
	@JsonProperty("djh")
//	单据号
	private String vbillno;
	@JsonProperty("fullname")
	private String kmmchie;
	private Integer dr;
//	@FieldValidate("制定日期不能为空:doperatedate is not null;")
	@JsonProperty("zdrq")
	private DZFDate doperatedate;
	private String vdef4;
	@JsonProperty("pzlb")
	private Integer pzlb;
	@JsonProperty("sfjz")
//	是否已记账
	private DZFBoolean ishasjz;
//	记账人
	private String vjzoperatorid;
	@JsonProperty("jzr")
	private String jz_user;
	@JsonProperty("jzrq")
//	记账日期
	private DZFDate djzdate;
	@JsonProperty("fdjs")
//	附单据数
	private Integer nbills;
	@JsonProperty("lydjlx")
	// 来源单据类型
	private String sourcebilltype;
	@JsonProperty("lydjid")
	// 来源单据ID
	private String sourcebillid;
	@JsonProperty("sffpxjll")
	// 是否已分配现金流量项目
	private DZFBoolean isfpxjxm;
	@JsonProperty("tpgid")
	// 图片主键
	private String pk_image_group;
	@JsonProperty("tplid")
	private String pk_image_library;

	// 凭证状态
	//FieldValidate("凭证状态不能为空:voucherstatus is not null;") 后台注值
	@JsonProperty("tpzt")
	private Integer voucherstatus;
	
	// 0-- 非识别  1----识别
	private int iautorecognize;
	
	//图片上传时间？
	@JsonProperty("ptime")
	private DZFDateTime photots;
	@JsonProperty("pname")
	private String phototname;
	
	@JsonProperty("qj")
	private String period;//期间
//	@FieldValidate("年度不能为空:period is not null;")
	@JsonProperty("nd")
	private String vyear;//年度
//	private SuperVO[] children;
//	@Override
//	  public SuperVO[] getChildren() {
//		return children;
//	}
	
//	子表VO字段
	@JsonProperty("cId")
	private String childer_id;	
	@JsonProperty("zy")
	private String zy;
	@JsonProperty("kmname")
	private String vname;
	@JsonProperty("cJfmny")
//	借方金额
	private String childer_jfmny;
	@JsonProperty("cDfmny")
//	贷方金额
	private String childer_dfmny;
//	存货数量
	@JsonProperty("chnum")
	private String nnumber;
	@JsonProperty("chdj")
//	存货单价
	private String nprice;
	
	// 发票类型
	private Integer fp_style;
	private String fp_stylename;
	@JsonProperty("cnpeo")
	private String vcashid;//出纳签字人
	@JsonProperty("cnr")
	private String cn_user;
	@JsonProperty("cnqzrq")
	private DZFDate dcashdate;//出纳签字日期
	@JsonProperty("bqz")
	private DZFBoolean bsign;//是否已签字
	
	public String getFp_stylename() {
		return fp_stylename;
	}

	public void setFp_stylename(String fp_stylename) {
		this.fp_stylename = fp_stylename;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
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



	public String getZd_user() {
		return zd_user;
	}

	public void setZd_user(String zd_user) {
		this.zd_user = zd_user;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getSh_user() {
		return sh_user;
	}

	public void setSh_user(String sh_user) {
		this.sh_user = sh_user;
	}

	public String getVapproveid() {
		return vapproveid;
	}

	public void setVapproveid(String vapproveid) {
		this.vapproveid = vapproveid;
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
	public java.lang.String getParentPKFieldName() {
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
	public java.lang.String getPKFieldName() {
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
	public java.lang.String getTableName() {
		return "YNT_TZPZ_H";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2014-09-26 12:14:28
	 */
	public PzglPageVo() {
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

	public DZFBoolean getIsfpxjxm() {
		return isfpxjxm;
	}

	public void setIsfpxjxm(DZFBoolean isfpxjxm) {
		this.isfpxjxm = isfpxjxm;
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

	public Integer getVoucherstatus() {
		return voucherstatus;
	}

	public void setVoucherstatus(Integer voucherstatus) {
		this.voucherstatus = voucherstatus;
	}

	public int getIautorecognize() {
		return iautorecognize;
	}

	public void setIautorecognize(int iautorecognize) {
		this.iautorecognize = iautorecognize;
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

	public String getVyear() {
		return vyear;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	public String getChilder_id() {
		return childer_id;
	}

	public void setChilder_id(String childer_id) {
		this.childer_id = childer_id;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getKmmchie() {
		return kmmchie;
	}

	public void setKmmchie(String kmmchie) {
		this.kmmchie = kmmchie;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getChilder_jfmny() {
		return childer_jfmny;
	}

	public void setChilder_jfmny(String childer_jfmny) {
		this.childer_jfmny = childer_jfmny;
	}

	public String getChilder_dfmny() {
		return childer_dfmny;
	}

	public void setChilder_dfmny(String childer_dfmny) {
		this.childer_dfmny = childer_dfmny;
	}

	public String getNnumber() {
		return nnumber;
	}

	public void setNnumber(String nnumber) {
		this.nnumber = nnumber;
	}

	public String getNprice() {
		return nprice;
	}

	public void setNprice(String nprice) {
		this.nprice = nprice;
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
	
	
}
