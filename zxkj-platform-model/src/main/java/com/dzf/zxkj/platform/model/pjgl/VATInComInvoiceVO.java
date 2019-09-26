package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 进项发票主表vo
 * @author wangzhn
 *
 */
public class VATInComInvoiceVO extends SuperVO implements IGlobalPZVO{

	@JsonProperty("id")
	private String pk_vatincominvoice;//主键
	@JsonProperty("cid")
	private String coperatorid;//操作人
	@JsonProperty("ddate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("corpid")
	private String pk_corp;//公司pk
	private String batchflag;//操作批次
	@JsonProperty("iszh")
	private DZFBoolean iszhuan;//是否是专票
	@JsonProperty("fphm")
	private String fp_hm;//发票号码
	@JsonProperty("fpdm")
	private String fp_dm;//发票代码
	@JsonProperty("sxhfmc")
	private String xhfmc;//销货方名称
	@JsonProperty("sspmc")
	private String spmc;//开票项目
	@JsonProperty("sl")
	private DZFDouble spsl;//税率
	@JsonProperty("se")
	private DZFDouble spse;//税额
	@JsonProperty("shjje")
	private DZFDouble hjje;//合计金额
	@JsonProperty("sjshj")
	private DZFDouble jshj;//价税合计
	@JsonProperty("yfphm")
	private String yfp_hm;//原发票号码
	@JsonProperty("yfpdm")
	private String yfp_dm;//原发票代码
	@JsonProperty("skprj")
	private DZFDate kprj;//开票日期
	@JsonProperty("srzrj")
	private DZFDate rzrj;//认证日期
	@JsonProperty("srzjg")
	private Integer rzjg;//认证结果(0未勾选 1勾选)
	private String pzh;//凭证号
	private String vicbillno;//库存单据号
	@JsonProperty("tzpzid")
	private String pk_tzpz_h;//凭证pk
	@JsonProperty("qj")
	private String period;//期间
	@JsonProperty("inqj")
	private String inperiod;//入账期间
	private String uploadperiod;//上传期间
	@JsonProperty("status")
	private String billStatus;//状态
	@JsonProperty("busitypetempid")
	private String pk_model_h;//业务类型pk
	@JsonProperty("busitempname")
	private String busitypetempname;//业务类型模板名称
	@JsonProperty("busisztypecode")
	private String busisztypecode;//业务类型结算方式
	private int sourcetype;//来源
	private Integer iuploadtype;//上传类型   20 ----全部  21 ---存货  22 ----其他
	private Integer ioperatetype;//操作类型   20 ----全部  21 ---存货  22 ----其他
	private Integer isettleway;//结算方式
	private String modifyoperid;//修改人pk
	private DZFDateTime modifydatetime;//修改时间
	
	private String sourcebilltype;//来源单据类型
	private String sourcebillid;//来源单据id
	private String kplx;//开票类型 	界面展示的类型
	
	private String jym;//校验码
	
	@JsonProperty("ipath")
	private String imgpath;//图片路径
	
	private String demo;
	private int dr;
	private DZFDateTime ts;
	
//	@JsonProperty("icflag")
//	private DZFBoolean isic;//是否生成库存
	@JsonProperty("did")
	private String dbillid;// 单据编号
	@JsonProperty("id_ictrade_h")
	private String pk_ictrade_h;//入库单主键
	
	/***********************************财税助手导入*********************************/
	private String cfyz;//重复验证
	private String hgx;//合规性
	private String xhfsbh;//销方识别号
	private String xhfdzdh;//销方地址电话
	private String xhfyhzh;//销方银行账号
	private String ghfmc;//购方名称
	private String ghfsbh;//购方识别号
	private String ghfdzdh;//购方地址电话
	private String ghfyhzh;//购方银行账号
	private String fpzl;//发票种类 该字段值会转化到 isZhuan字段
	
	//下边字段在主表不存库，存子表
	private String bspmc;//商品名称
	private String invspec;//规格
	private String measurename;//单位
	private DZFDouble bnum;//数量
	private DZFDouble bprice;//单价
	private DZFDouble bhjje;//金额
	private DZFDouble bspse;//税额
	private DZFDouble bspsl;//税率
	/*****************************************************************************/
	private DZFDate beginrq;//供导入后前台展示用 不存库
	private DZFDate endrq;//供导入后前台展示用 不存库
	private  String pk_image_group;// 图片主键
	private  String pk_image_group1;//凭证图片主键
	private String pk_image_library;//图片子表id
	private  int count; //发票数量
	private Integer pzstatus;//凭证状态  -1 暂存
	
	public String getPk_vatincominvoice() {
		return pk_vatincominvoice;
	}

	public String getJym() {
		return jym;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}

	public String getBspmc() {
		return bspmc;
	}

	public String getInvspec() {
		return invspec;
	}

	public String getBusisztypecode() {
		return busisztypecode;
	}

	public void setBusisztypecode(String busisztypecode) {
		this.busisztypecode = busisztypecode;
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

	public void setPk_vatincominvoice(String pk_vatincominvoice) {
		this.pk_vatincominvoice = pk_vatincominvoice;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getBatchflag() {
		return batchflag;
	}

	public void setBatchflag(String batchflag) {
		this.batchflag = batchflag;
	}

	

	public DZFBoolean getIszhuan() {
		return iszhuan;
	}

	public void setIszhuan(DZFBoolean iszhuan) {
		this.iszhuan = iszhuan;
	}

	public String getFp_hm() {
		return fp_hm;
	}

	public void setFp_hm(String fp_hm) {
		this.fp_hm = fp_hm;
	}

	public String getFp_dm() {
		return fp_dm;
	}

	public void setFp_dm(String fp_dm) {
		this.fp_dm = fp_dm;
	}

	public String getXhfmc() {
		return xhfmc;
	}

	public void setXhfmc(String xhfmc) {
		this.xhfmc = xhfmc;
	}

	public String getSpmc() {
		return spmc;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public DZFDouble getSpsl() {
		return spsl;
	}

	public void setSpsl(DZFDouble spsl) {
		this.spsl = spsl;
	}

	public DZFDouble getSpse() {
		return spse;
	}

	public void setSpse(DZFDouble spse) {
		this.spse = spse;
	}

	public DZFDouble getHjje() {
		return hjje;
	}

	public void setHjje(DZFDouble hjje) {
		this.hjje = hjje;
	}

	public DZFDouble getJshj() {
		return jshj;
	}

	public void setJshj(DZFDouble jshj) {
		this.jshj = jshj;
	}

	public String getYfp_hm() {
		return yfp_hm;
	}

	public void setYfp_hm(String yfp_hm) {
		this.yfp_hm = yfp_hm;
	}

	public String getYfp_dm() {
		return yfp_dm;
	}

	public void setYfp_dm(String yfp_dm) {
		this.yfp_dm = yfp_dm;
	}

	public DZFDate getKprj() {
		return kprj;
	}

	public void setKprj(DZFDate kprj) {
		this.kprj = kprj;
	}

	public DZFDate getRzrj() {
		return rzrj;
	}

	public void setRzrj(DZFDate rzrj) {
		this.rzrj = rzrj;
	}

	public Integer getRzjg() {
		return rzjg;
	}

	public void setRzjg(Integer rzjg) {
		this.rzjg = rzjg;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
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

	public int getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(int sourcetype) {
		this.sourcetype = sourcetype;
	}

	public String getModifyoperid() {
		return modifyoperid;
	}

	public void setModifyoperid(String modifyoperid) {
		this.modifyoperid = modifyoperid;
	}

	public DZFDateTime getModifydatetime() {
		return modifydatetime;
	}

	public void setModifydatetime(DZFDateTime modifydatetime) {
		this.modifydatetime = modifydatetime;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	public String getXhfsbh() {
		return xhfsbh;
	}

	public void setXhfsbh(String xhfsbh) {
		this.xhfsbh = xhfsbh;
	}

	public String getXhfdzdh() {
		return xhfdzdh;
	}

	public void setXhfdzdh(String xhfdzdh) {
		this.xhfdzdh = xhfdzdh;
	}

	public String getXhfyhzh() {
		return xhfyhzh;
	}

	public void setXhfyhzh(String xhfyhzh) {
		this.xhfyhzh = xhfyhzh;
	}

	public String getGhfmc() {
		return ghfmc;
	}

	public void setGhfmc(String ghfmc) {
		this.ghfmc = ghfmc;
	}

	public String getGhfsbh() {
		return ghfsbh;
	}

	public void setGhfsbh(String ghfsbh) {
		this.ghfsbh = ghfsbh;
	}

	public String getGhfdzdh() {
		return ghfdzdh;
	}

	public void setGhfdzdh(String ghfdzdh) {
		this.ghfdzdh = ghfdzdh;
	}

	public String getGhfyhzh() {
		return ghfyhzh;
	}

	public void setGhfyhzh(String ghfyhzh) {
		this.ghfyhzh = ghfyhzh;
	}

	public String getFpzl() {
		return fpzl;
	}

	public void setFpzl(String fpzl) {
		this.fpzl = fpzl;
	}

	public String getCfyz() {
		return cfyz;
	}

	public void setCfyz(String cfyz) {
		this.cfyz = cfyz;
	}

	public String getHgx() {
		return hgx;
	}

	public void setHgx(String hgx) {
		this.hgx = hgx;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public String getKplx() {
		return kplx;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public void setKplx(String kplx) {
		this.kplx = kplx;
	}

	public DZFDate getBeginrq() {
		return beginrq;
	}

	public DZFDate getEndrq() {
		return endrq;
	}

	public void setBeginrq(DZFDate beginrq) {
		this.beginrq = beginrq;
	}

	public void setEndrq(DZFDate endrq) {
		this.endrq = endrq;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

//	public DZFBoolean getIsic() {
//		return isic;
//	}
//
//	public void setIsic(DZFBoolean isic) {
//		this.isic = isic;
//	}
	
	public String getDbillid() {
		return dbillid;
	}

	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}

	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}

	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}
	
	public String getInperiod() {
		return inperiod;
	}

	public void setInperiod(String inperiod) {
		this.inperiod = inperiod;
	}

	public String getPk_image_group1() {
		return pk_image_group1;
	}

	public void setPk_image_group1(String pk_image_group1) {
		this.pk_image_group1 = pk_image_group1;
	}

	public Integer getIuploadtype() {
		return iuploadtype;
	}

	public Integer getIoperatetype() {
		return ioperatetype;
	}

	public Integer getIsettleway() {
		return isettleway;
	}

	public void setIuploadtype(Integer iuploadtype) {
		this.iuploadtype = iuploadtype;
	}

	public void setIoperatetype(Integer ioperatetype) {
		this.ioperatetype = ioperatetype;
	}

	public void setIsettleway(Integer isettleway) {
		this.isettleway = isettleway;
	}

	public String getUploadperiod() {
		return uploadperiod;
	}

	public void setUploadperiod(String uploadperiod) {
		this.uploadperiod = uploadperiod;
	}

	public String getVicbillno() {
		return vicbillno;
	}

	public void setVicbillno(String vicbillno) {
		this.vicbillno = vicbillno;
	}

	public Integer getPzstatus() {
		return pzstatus;
	}

	public void setPzstatus(Integer pzstatus) {
		this.pzstatus = pzstatus;
	}

	@Override
	public String getPKFieldName() {
		return "pk_vatincominvoice";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_vatincominvoice";
	}

	@Override
	public DZFDouble getTotalmny() {
		return getJshj();
	}

	@Override
	public DZFDouble getMny() {
		return getHjje();
	}

	@Override
	public DZFDouble getWsmny() {
		return getHjje();
	}

	@Override
	public DZFDouble getSmny() {
		return getSpse();
	}

}
