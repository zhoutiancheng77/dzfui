package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 销项发票主表vo
 * @author wangzhn
 *
 */
public class VATSaleInvoiceVO extends SuperVO implements IGlobalPZVO{

	@JsonProperty("id")
	private String pk_vatsaleinvoice;//主键
	@JsonProperty("cid")
	private String coperatorid;//操作人
	@JsonProperty("ddate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("corpid")
	private String pk_corp;//公司pk
	private String batchflag;//操作批次
	@JsonProperty("fpzl")
	private String invmodel;//发票类型   税控盘文件导入存储的字段  增值税普票等 
							//数据归集到 isZhuan子弹
	@JsonProperty("fpzt")
	private String invstatus;//发票状态  税控盘文件导入存储的字段 如正常发票等 
							 //数据归集到  kplx字段
	@JsonProperty("iszh")
	private DZFBoolean iszhuan;//是否是专票
	@JsonProperty("fphm")
	private String fp_hm;//发票号码
	@JsonProperty("fpdm")
	private String fp_dm;//发票代码
	@JsonProperty("skhmc")
	private String khmc;//客户名称  购买方名称
	@JsonProperty("sspmc")
	private String spmc;//商品名称
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
	@JsonProperty("tzdbh")
	private String noticebillno;//通知单编号
	@JsonProperty("kprnz")
	private String kprname;//开票人名称
	private String kprid;//开票人pk
	@JsonProperty("skprj")
	private DZFDate kprj;//开票日期
	@JsonProperty("zfrmz")
	private String zfrname;//作废人名称
	private String zfrid;//作废人pk
	@JsonProperty("szfrj")
	private DZFDate zfrj;
	@JsonProperty("khsbh")
	private String custidentno;//客户识别号   购买方  纳税人识别号
	private String pzh;//凭证号
	private String vicbillno;//库存单据号
	@JsonProperty("tzpzid")
	private String pk_tzpz_h;//凭证pk
	@JsonProperty("qj")
	private String period;//期间
	@JsonProperty("inqj")
	private String inperiod;//入账期间
	private String uploadperiod;//上传期间
//	@JsonProperty("accid")
//	private String pk_subject;//入账科目
//	@JsonProperty("kmmc")
//	private String accountname;//科目名称
//	@JsonProperty("kmbm")
//	private String accountcode;//科目编码
	@JsonProperty("busitypetempid")
	private String pk_model_h;//业务类型pk
	@JsonProperty("busitempname")
	private String busitypetempname;//业务类型模板名称
	@JsonProperty("busisztypecode")
	private String busisztypecode;//业务类型结算方式
	@JsonProperty("status")
	private String billStatus;//状态
	private int sourcetype;//来源
	private String modifyoperid;//修改人pk
	private DZFDateTime modifydatetime;//修改时间
	
	private String xhfmc;//销货方名称
	private String xhfsbh;//销方识别号
	private String xhfdzdh;//销方地址电话
	private String xhfyhzh;//销方银行账号
	private String ghfdzdh;//购方地址电话
	private String ghfyhzh;//购方银行账号
	
	private String sourcebilltype;//来源单据类型
	private String sourcebillid;//来源单据id
	private String kplx;//开票类型 	界面展示的类型
	private Integer iuploadtype;//上传类型
	private Integer ioperatetype;//操作类型
	private Integer isettleway;//结算方式
	@JsonProperty("ipath")
	private String imgpath;//图片路径
	
	private String demo;//备注
	private int dr;
	private DZFDateTime ts;
//	@JsonProperty("icflag")
//	private DZFBoolean isic;//是否生成库存  先放着，后期该字段要拿掉***
	@JsonProperty("did")
	private String dbillid;// 单据编号
	@JsonProperty("id_ictrade_h")
	private String pk_ictrade_h;//出库单主键
	
	/*******************航信百旺excel导入 这些字段不存库，存子表**********/
	private String bspmc;//商品名称
	private String invspec;//规格
	private String measurename;//单位
	private DZFDouble bnum;//数量
	private DZFDouble bprice;//单价
	private DZFDouble bhjje;//金额
	private DZFDouble bspsl;//税率
	private DZFDouble bspse;//税额
	/****************************************************************/
	private String tempvalue;//临时使用字段，不存库
	
	/*****************************************************************/
	private DZFDate beginrq;//供导入后前台展示用 不存库
	private DZFDate endrq;//供导入后前台展示用 不存库
	private DZFBoolean isFlag;//是否强制导入
	private  String pk_image_group;// 图片主键
	private  String pk_image_group1;//凭证图片主键
	private String pk_image_library;//图片子表id
	private  int count; //发票数量
	private Integer pzstatus;//凭证状态  -1 暂存
	
	public static final String INVMODEL = "invmodel";
	
	public static final String INVMODEL_1 = "正常发票";//正常发票
	public static final String INVMODEL_2 = "空白作废";//空白作废
	public static final String INVMODEL_3 = "填开作废";//填开作废
	public static final String INVMODEL_4 = "负数发票";//负数发票
	public static final String INVMODEL_5 = "负数作废";//负数作废
	
	public String getPk_vatsaleinvoice() {
		return pk_vatsaleinvoice;
	}

	public void setPk_vatsaleinvoice(String pk_vatsaleinvoice) {
		this.pk_vatsaleinvoice = pk_vatsaleinvoice;
	}

	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}

	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}

	public String getBusisztypecode() {
		return busisztypecode;
	}

	public void setBusisztypecode(String busisztypecode) {
		this.busisztypecode = busisztypecode;
	}

	public String getDbillid() {
		return dbillid;
	}

	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}

	public String getXhfmc() {
		return xhfmc;
	}

	public String getXhfsbh() {
		return xhfsbh;
	}

	public String getXhfdzdh() {
		return xhfdzdh;
	}

	public String getXhfyhzh() {
		return xhfyhzh;
	}

	public String getGhfdzdh() {
		return ghfdzdh;
	}

	public String getGhfyhzh() {
		return ghfyhzh;
	}

	public void setXhfmc(String xhfmc) {
		this.xhfmc = xhfmc;
	}

	public void setXhfsbh(String xhfsbh) {
		this.xhfsbh = xhfsbh;
	}

	public void setXhfdzdh(String xhfdzdh) {
		this.xhfdzdh = xhfdzdh;
	}

	public void setXhfyhzh(String xhfyhzh) {
		this.xhfyhzh = xhfyhzh;
	}

	public void setGhfdzdh(String ghfdzdh) {
		this.ghfdzdh = ghfdzdh;
	}

	public void setGhfyhzh(String ghfyhzh) {
		this.ghfyhzh = ghfyhzh;
	}

	public int getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(int sourcetype) {
		this.sourcetype = sourcetype;
	}

	

	public DZFBoolean getIszhuan() {
		return iszhuan;
	}

	public void setIszhuan(DZFBoolean iszhuan) {
		this.iszhuan = iszhuan;
	}

	public String getKplx() {
		return kplx;
	}

	public void setKplx(String kplx) {
		this.kplx = kplx;
	}

	public DZFDate getZfrj() {
		return zfrj;
	}

	public void setZfrj(DZFDate zfrj) {
		this.zfrj = zfrj;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

//	public String getAccountcode() {
//		return accountcode;
//	}
//
//	public void setAccountcode(String accountcode) {
//		this.accountcode = accountcode;
//	}
//
//	public String getAccountname() {
//		return accountname;
//	}
//
//	public void setAccountname(String accountname) {
//		this.accountname = accountname;
//	}
//
//	public String getPk_subject() {
//		return pk_subject;
//	}
//
//	public void setPk_subject(String pk_subject) {
//		this.pk_subject = pk_subject;
//	}

	public DZFBoolean getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(DZFBoolean isFlag) {
		this.isFlag = isFlag;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
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

	public String getInvmodel() {
		return invmodel;
	}

	public void setInvmodel(String invmodel) {
		this.invmodel = invmodel;
	}

	public String getInvstatus() {
		return invstatus;
	}

	public void setInvstatus(String invstatus) {
		this.invstatus = invstatus;
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

	public String getKhmc() {
		return khmc;
	}

	public void setKhmc(String khmc) {
		this.khmc = khmc;
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

	public String getNoticebillno() {
		return noticebillno;
	}

	public void setNoticebillno(String noticebillno) {
		this.noticebillno = noticebillno;
	}

	public String getKprname() {
		return kprname;
	}

	public void setKprname(String kprname) {
		this.kprname = kprname;
	}

	public String getKprid() {
		return kprid;
	}

	public void setKprid(String kprid) {
		this.kprid = kprid;
	}

	public DZFDate getKprj() {
		return kprj;
	}

	public void setKprj(DZFDate kprj) {
		this.kprj = kprj;
	}

	public String getZfrname() {
		return zfrname;
	}

	public void setZfrname(String zfrname) {
		this.zfrname = zfrname;
	}

	public String getZfrid() {
		return zfrid;
	}

	public void setZfrid(String zfrid) {
		this.zfrid = zfrid;
	}

	public String getCustidentno() {
		return custidentno;
	}

	public void setCustidentno(String custidentno) {
		this.custidentno = custidentno;
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

	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
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

	public DZFDouble getBspsl() {
		return bspsl;
	}

	public DZFDouble getBspse() {
		return bspse;
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

	public void setBspsl(DZFDouble bspsl) {
		this.bspsl = bspsl;
	}

	public void setBspse(DZFDouble bspse) {
		this.bspse = bspse;
	}

	public String getTempvalue() {
		return tempvalue;
	}

	public void setTempvalue(String tempvalue) {
		this.tempvalue = tempvalue;
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

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
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
		return "pk_vatsaleinvoice";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_vatsaleinvoice";
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
