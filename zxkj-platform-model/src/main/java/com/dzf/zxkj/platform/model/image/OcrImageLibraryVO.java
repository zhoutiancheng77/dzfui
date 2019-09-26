package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrImageLibraryVO extends SuperVO {

	/**
	 * ocr图片临时存储
	 */
	private static final long serialVersionUID = -8154848473795897038L;

	private String pk_custcorp;// 小微客户公司
	@JsonProperty("corpId")
	private String pk_corp; // 会计公司
	private DZFDateTime ts;// 时间戳
	private String imgpath;// 图片路径
	private String smallimgpath;// 小图路径
	private String middleimgpath;// 图片路径
	private String pdfpath;// pdf原文件路径
	private String coperatorid;// 制单人
	private Integer dr;// 删除标志
	private DZFDate doperatedate;// 上传日期
	@JsonProperty("bid")
	private String pk_image_ocrlibrary;// 主键
	@JsonProperty("pid")
	private String pk_image_ocrgroup;// 分组主键
	private String imgname;// 图片名称
	private String oldfilename; // 原名
	private String imgmd; // MD5值
	private Integer istate;// 识图状态 StateEnum
	// private DZFBoolean isfirst;// 是否是第一张图片
	private Integer iorder;// 上传顺序
	private DZFDate cvoucherdate;
	private DZFBoolean iszd;// 是否制单
	private String pk_model_h;//匹配模板pk
	private String keywords;//关键字
	private String keycode;//票据取票key码

	/******************* 图片字段信息 ********************************/
	private String vinvoicecode;// 发票代码
	private String vinvoiceno;// 发票号
	private String dinvoicedate;// 开票日期
	private String vpurchname;// 购方企业名称
	private String vpurchtaxno;// 购方纳税号
	private String vsalename;// 销方企业名称
	private String vsaletaxno;// 销方纳税号
	private String vsaleopenacc;// 销售方开户账号
	private String vsalephoneaddr;// 销售方地址电话
	private String vpuropenacc;// 购买方开户账号
	private String vpurphoneaddr;// 购买方地址电话
	private String ntotaltax;// 价税合计
	private String nmny;// 金额
	private String ntaxnmny;// 税额
	private String vtotaltaxcapital;// 价税合计大写
	private String items;// 明细
	private String vmemo;// 备注
	private String ntax; // 税率
	private String invoicetype;// 发票类型
	private String checkcode; // 校验码
	private String reason; // 原因
	private String vpurbankname;// 付款银行
	private String vsalebankname;// 收款银行

	/************************* 接口识别的信息 *********************/
	private String vinvoicecode1;// 发票代码
	private String vinvoiceno1;// 发票号
	private String dinvoicedate1;// 开票日期
	private String invoicetype1;// 发票类型
	private String vpurchname1;// 购方企业名称
	private String vpurchtaxno1;// 购方纳税号
	private String vsalename1;// 销方企业名称
	private String vsaletaxno1;// 销方纳税号
	private String ntotaltax1;// 价税合计
	private String nmny1;// 金额合计
	private String ntaxnmny1;// 税额合计
	private String checkcode1;// 校验码

	/******************* 隔板信息 ***********************************/

	private String vunitname; // 隔板名称
	private String vunitcode;// 隔板编码
	private String corp;// 隔板id 加密的公司id
	private DZFBoolean ispartition; // 是否隔板

	/******************* 匹配后信息 ***********************************/
	private String pk_salecorp;// 销方公司
	private String pk_purcorp;// 购方公司
	private Integer itype; // 发票类型 0 ----- 销方 1 ------ 购方
	private Integer ifpkind;// 1-----普票 2----专票 3--- 未开票
	private Integer iinvoicetype;// 1 费用类
	private String invname;
	private int imodel;

	private DZFBoolean isinterface;// 是否接口识别成功
	private int iway; // 识别方式 ocr 0 网站 1 票通 2 扫描仪 3
	private String batchcode; // 批次信息
	private Integer imagecounts;// 组数
	private String vocrpurchname;// ocr识别的购方公司名称;
	private String vocrsalename;// ocr识别的销方公司名称;
	private String vserialno;// 流水号;
	private String vdrcode;// 二维码;
	private String vocrdrcode;// ocr识别出的二维码
	private Integer ibusinesstype;// 业务类型 0-管理端 1-在线端 2-手机端
	private String crelationid;// 关联id
	private String uniquecode;// 唯一码
	private String dkbs;// 代开标识
	private String system;// 来源系统
	private String sourceid;// 来源id
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	private String def6;
	private String def7;
	private String def8;
	private String def9;
	private String def10;// 重复的凭证号

	private String vdef1;// 上传人
	private String vdef2;// 所属年月
	private String vdef3;// 凭证制单人
	private String vdef4;// 凭证日期
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private String vdef9;
	private String vdef10;

	private String pzh;// 显示的凭证号
	private Integer pjlxstatus;// 票据类型状态 仅展示使用
	private Integer pistate;// image_group 状态
	private String exc_pk_currency; // 外币字段
	 private Integer direction;// //记录外币的科目方向
	 
	private String zyFromBillZy;//不存库   值不为空，凭证的摘要取该字段

	public static final String PK_CORP = "pk_corp";
	public static final String IMGPATH = "imgpath";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_IMAGE_LIBRARY = "pk_image_ocrlibrary";
	public static final String PK_IMAGE_GROUP = "pk_image_ocrgroup";
	public static final String IMGNAME = "imgname";

	
	
	public String getKeycode() {
		return keycode;
	}

	public void setKeycode(String keycode) {
		this.keycode = keycode;
	}

	public String getZyFromBillZy() {
		return zyFromBillZy;
	}

	public void setZyFromBillZy(String zyFromBillZy) {
		this.zyFromBillZy = zyFromBillZy;
	}

	public DZFBoolean getIszd() {
		return iszd;
	}

	public void setIszd(DZFBoolean iszd) {
		this.iszd = iszd;
	}

	public Integer getIbusinesstype() {
		return ibusinesstype;
	}

	public void setIbusinesstype(Integer ibusinesstype) {
		this.ibusinesstype = ibusinesstype;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getVocrdrcode() {
		return vocrdrcode;
	}

	public void setVocrdrcode(String vocrdrcode) {
		this.vocrdrcode = vocrdrcode;
	}

	public String getVserialno() {
		return vserialno;
	}

	public void setVserialno(String vserialno) {
		this.vserialno = vserialno;
	}

	public String getVdrcode() {
		return vdrcode;
	}

	public void setVdrcode(String vdrcode) {
		this.vdrcode = vdrcode;
	}

	public String getVocrpurchname() {
		return vocrpurchname;
	}

	public void setVocrpurchname(String vocrpurchname) {
		this.vocrpurchname = vocrpurchname;
	}

	public String getVocrsalename() {
		return vocrsalename;
	}

	public void setVocrsalename(String vocrsalename) {
		this.vocrsalename = vocrsalename;
	}

	public int getIway() {
		return iway;
	}

	public void setIway(int iway) {
		this.iway = iway;
	}

	public DZFBoolean getIsinterface() {
		return isinterface;
	}

	public void setIsinterface(DZFBoolean isinterface) {
		this.isinterface = isinterface;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public int getImodel() {
		return imodel;
	}

	public void setImodel(int imodel) {
		this.imodel = imodel;
	}

	public Integer getImagecounts() {
		return imagecounts;
	}

	public void setImagecounts(Integer imagecounts) {
		this.imagecounts = imagecounts;
	}

	public String getOldfilename() {
		return oldfilename;
	}

	public void setOldfilename(String oldfilename) {
		this.oldfilename = oldfilename;
	}

	public String getPk_custcorp() {
		return pk_custcorp;
	}

	public void setPk_custcorp(String pk_custcorp) {
		this.pk_custcorp = pk_custcorp;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_image_ocrlibrary() {
		return pk_image_ocrlibrary;
	}

	public void setPk_image_ocrlibrary(String pk_image_ocrlibrary) {
		this.pk_image_ocrlibrary = pk_image_ocrlibrary;
	}

	public String getPk_image_ocrgroup() {
		return pk_image_ocrgroup;
	}

	public void setPk_image_ocrgroup(String pk_image_ocrgroup) {
		this.pk_image_ocrgroup = pk_image_ocrgroup;
	}

	public String getImgname() {
		return imgname;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

	public Integer getIstate() {
		return istate;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}

	public String getImgmd() {
		return imgmd;
	}

	public void setImgmd(String imgmd) {
		this.imgmd = imgmd;
	}

	public String getVinvoicecode() {
		return vinvoicecode;
	}

	public void setVinvoicecode(String vinvoicecode) {
		this.vinvoicecode = vinvoicecode;
	}

	public String getVinvoiceno() {
		return vinvoiceno;
	}

	public void setVinvoiceno(String vinvoiceno) {
		this.vinvoiceno = vinvoiceno;
	}

	public String getVpurchname() {
		return vpurchname;
	}

	public void setVpurchname(String vpurchname) {
		this.vpurchname = vpurchname;
	}

	public String getVpurchtaxno() {
		return vpurchtaxno;
	}

	public void setVpurchtaxno(String vpurchtaxno) {
		this.vpurchtaxno = vpurchtaxno;
	}

	public String getVsalename() {
		return vsalename;
	}

	public void setVsalename(String vsalename) {
		this.vsalename = vsalename;
	}

	public String getVsaletaxno() {
		return vsaletaxno;
	}

	public void setVsaletaxno(String vsaletaxno) {
		this.vsaletaxno = vsaletaxno;
	}

	public String getVtotaltaxcapital() {
		return vtotaltaxcapital;
	}

	public void setVtotaltaxcapital(String vtotaltaxcapital) {
		this.vtotaltaxcapital = vtotaltaxcapital;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getDinvoicedate() {
		return dinvoicedate;
	}

	public void setDinvoicedate(String dinvoicedate) {
		this.dinvoicedate = dinvoicedate;
	}

	public String getNtotaltax() {
		return ntotaltax;
	}

	public void setNtotaltax(String ntotaltax) {
		this.ntotaltax = ntotaltax;
	}

	public String getNmny() {
		return nmny;
	}

	public void setNmny(String nmny) {
		this.nmny = nmny;
	}

	public String getNtaxnmny() {
		return ntaxnmny;
	}

	public void setNtaxnmny(String ntaxnmny) {
		this.ntaxnmny = ntaxnmny;
	}

	public String getNtax() {
		return ntax;
	}

	public void setNtax(String ntax) {
		this.ntax = ntax;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getVunitname() {
		return vunitname;
	}

	public void setVunitname(String vunitname) {
		this.vunitname = vunitname;
	}

	public String getVunitcode() {
		return vunitcode;
	}

	public void setVunitcode(String vunitcode) {
		this.vunitcode = vunitcode;
	}

	public String getCorp() {
		return corp;
	}

	public void setCorp(String corp) {
		this.corp = corp;
	}

	public DZFBoolean getIspartition() {
		return ispartition;
	}

	public void setIspartition(DZFBoolean ispartition) {
		this.ispartition = ispartition;
	}

	// public DZFBoolean getIsfirst() {
	// return isfirst;
	// }
	//
	// public void setIsfirst(DZFBoolean isfirst) {
	// this.isfirst = isfirst;
	// }

	public Integer getIorder() {
		return iorder;
	}

	public void setIorder(Integer iorder) {
		this.iorder = iorder;
	}

	public String getPk_salecorp() {
		return pk_salecorp;
	}

	public void setPk_salecorp(String pk_salecorp) {
		this.pk_salecorp = pk_salecorp;
	}

	public String getPk_purcorp() {
		return pk_purcorp;
	}

	public void setPk_purcorp(String pk_purcorp) {
		this.pk_purcorp = pk_purcorp;
	}

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public DZFDate getCvoucherdate() {
		return cvoucherdate;
	}

	public void setCvoucherdate(DZFDate cvoucherdate) {
		this.cvoucherdate = cvoucherdate;
	}

	public String getInvoicetype() {
		return invoicetype;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public String getBatchcode() {
		return batchcode;
	}

	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}

	public String getVinvoicecode1() {
		return vinvoicecode1;
	}

	public void setVinvoicecode1(String vinvoicecode1) {
		this.vinvoicecode1 = vinvoicecode1;
	}

	public String getVinvoiceno1() {
		return vinvoiceno1;
	}

	public void setVinvoiceno1(String vinvoiceno1) {
		this.vinvoiceno1 = vinvoiceno1;
	}

	public String getDinvoicedate1() {
		return dinvoicedate1;
	}

	public void setDinvoicedate1(String dinvoicedate1) {
		this.dinvoicedate1 = dinvoicedate1;
	}

	public String getInvoicetype1() {
		return invoicetype1;
	}

	public void setInvoicetype1(String invoicetype1) {
		this.invoicetype1 = invoicetype1;
	}

	public String getVpurchname1() {
		return vpurchname1;
	}

	public void setVpurchname1(String vpurchname1) {
		this.vpurchname1 = vpurchname1;
	}

	public String getVpurchtaxno1() {
		return vpurchtaxno1;
	}

	public void setVpurchtaxno1(String vpurchtaxno1) {
		this.vpurchtaxno1 = vpurchtaxno1;
	}

	public String getVsalename1() {
		return vsalename1;
	}

	public void setVsalename1(String vsalename1) {
		this.vsalename1 = vsalename1;
	}

	public String getVsaletaxno1() {
		return vsaletaxno1;
	}

	public void setVsaletaxno1(String vsaletaxno1) {
		this.vsaletaxno1 = vsaletaxno1;
	}

	public String getNtotaltax1() {
		return ntotaltax1;
	}

	public void setNtotaltax1(String ntotaltax1) {
		this.ntotaltax1 = ntotaltax1;
	}

	public String getNmny1() {
		return nmny1;
	}

	public void setNmny1(String nmny1) {
		this.nmny1 = nmny1;
	}

	public String getNtaxnmny1() {
		return ntaxnmny1;
	}

	public void setNtaxnmny1(String ntaxnmny1) {
		this.ntaxnmny1 = ntaxnmny1;
	}

	public String getCheckcode1() {
		return checkcode1;
	}

	public void setCheckcode1(String checkcode1) {
		this.checkcode1 = checkcode1;
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

	public String getSmallimgpath() {
		return smallimgpath;
	}

	public void setSmallimgpath(String smallimgpath) {
		this.smallimgpath = smallimgpath;
	}

	public String getMiddleimgpath() {
		return middleimgpath;
	}

	public String getPdfpath() {
		return pdfpath;
	}

	public void setPdfpath(String pdfpath) {
		this.pdfpath = pdfpath;
	}

	public void setMiddleimgpath(String middleimgpath) {
		this.middleimgpath = middleimgpath;
	}

	public Integer getPjlxstatus() {
		return pjlxstatus;
	}

	public void setPjlxstatus(Integer pjlxstatus) {
		this.pjlxstatus = pjlxstatus;
	}

	public String getCrelationid() {
		return crelationid;
	}

	public void setCrelationid(String crelationid) {
		this.crelationid = crelationid;
	}

	public Integer getPistate() {
		return pistate;
	}

	public void setPistate(Integer pistate) {
		this.pistate = pistate;
	}

	public String getSystem() {
		return system;
	}

	public String getSourceid() {
		return sourceid;
	}

	public String getDef1() {
		return def1;
	}

	public String getDef2() {
		return def2;
	}

	public String getDef3() {
		return def3;
	}

	public String getDef4() {
		return def4;
	}

	public String getDef5() {
		return def5;
	}

	public String getDef6() {
		return def6;
	}

	public String getDef7() {
		return def7;
	}

	public String getDef8() {
		return def8;
	}

	public String getDef9() {
		return def9;
	}

	public String getDef10() {
		return def10;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	public String getUniquecode() {
		return uniquecode;
	}

	public void setUniquecode(String uniquecode) {
		this.uniquecode = uniquecode;
	}

	public String getDkbs() {
		return dkbs;
	}

	public void setDkbs(String dkbs) {
		this.dkbs = dkbs;
	}

	public String getVsaleopenacc() {
		return vsaleopenacc;
	}

	public void setVsaleopenacc(String vsaleopenacc) {
		this.vsaleopenacc = vsaleopenacc;
	}

	public String getVsalephoneaddr() {
		return vsalephoneaddr;
	}

	public void setVsalephoneaddr(String vsalephoneaddr) {
		this.vsalephoneaddr = vsalephoneaddr;
	}

	public String getVpuropenacc() {
		return vpuropenacc;
	}

	public void setVpuropenacc(String vpuropenacc) {
		this.vpuropenacc = vpuropenacc;
	}

	public String getVpurphoneaddr() {
		return vpurphoneaddr;
	}

	public void setVpurphoneaddr(String vpurphoneaddr) {
		this.vpurphoneaddr = vpurphoneaddr;
	}

	public Integer getIfpkind() {
		return ifpkind;
	}

	public void setIfpkind(Integer ifpkind) {
		this.ifpkind = ifpkind;
	}

	public Integer getIinvoicetype() {
		return iinvoicetype;
	}

	public void setIinvoicetype(Integer iinvoicetype) {
		this.iinvoicetype = iinvoicetype;
	}

	public String getExc_pk_currency() {
		return exc_pk_currency;
	}

	public void setExc_pk_currency(String exc_pk_currency) {
		this.exc_pk_currency = exc_pk_currency;
	}
	
	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public String getVpurbankname() {
		return vpurbankname;
	}

	public String getVsalebankname() {
		return vsalebankname;
	}

	public void setVpurbankname(String vpurbankname) {
		this.vpurbankname = vpurbankname;
	}

	public void setVsalebankname(String vsalebankname) {
		this.vsalebankname = vsalebankname;
	}
	
	public String getPk_model_h() {
		return pk_model_h;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Override
	public String getPKFieldName() {
		return "pk_image_ocrlibrary";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_image_ocrgroup";
	}

	@Override
	public String getTableName() {
		return "ynt_image_ocrlibrary";
	}

	public void setChildren(OcrInvoiceVO[] children) {
		super.setChildren(children);
	}
}