package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrInvoiceVO extends SuperVO {

	/**
	 * 接口 发票信息头
	 */
	private static final long serialVersionUID = -5286071006532256962L;

	private String vinvoicecode;// 发票代码
	private String vinvoiceno;// 发票号
	private String dinvoicedate;// 开票日期
	private String invoicetype;// 发票类型
	private String vpurchname;// 购方企业名称
	private String vpurchtaxno;// 购方纳税号
	private String vsalename;// 销方企业名称
	private String vsaletaxno;// 销方纳税号
	private String vsalename_dk;//代开销方企业名称
	private String vsaletaxno_dk;//代开销方纳税号
	private String ntotaltax;// 价税合计
	private String nmny;// 金额合计
	private String ntaxnmny;// 税额合计
	private String vsaleopenacc;// 销售方开户账号 （银行"银行名称"）,
	private String vsalephoneaddr;// 销售方地址电话 （银行"备注"）,
	private String vpuropenacc;// 购买方开户账号 （ 银行 "批次"）,
	private String vpurphoneaddr;// "购买方地址电话   （ 银行 "识别时间"）
	private String vpurbankname;// 付款银行
	private String vsalebankname;// 收款银行
	private String vfirsrinvname; // 首件货物名称
	private String ocr_id;// ocr信息id
	private String pk_invoice; // 主键
	@JsonProperty("corpId")
	private String pk_corp;// 会计公司
	private Integer dr;// 删除标志
	private DZFDateTime ts; // 时间戳
	private String istate;// 识别状态 

	private String drcode; // 二维码
	private String cycs;// 查验次数
	private String jqbh;// 机器编号
	private String jym;// 校验码分分
	
	private String zfbz;// 作废标志
	private Integer itype;// 类型
	//IOCR("IOCR", "ocr自识别", 0),//IWEB("IWEB", "网站接口", 1),//IPT("IPT", "票通接口", 2),//SCA("SCA", "扫描仪识别", 3);
	private String vmemo;// 备注
	private String uniquecode;// 唯一码 单据标识号
	private String dkbs;// 代开标识
	private String vkeywordinfo;// 唯一信息

	private String pk_image_group;// 图片信息组主键
	
	private String pk_model_h;//匹配业务类型模板pk
	private String keywords;//关键字
	
	private String webid;//聂老师网站id
	
	private String pk_billcategory;//票据类别主键
	private String billcategoryname;//票据类别名称(不存库  进销项银行对账单分类使用)
	private String pk_category_keyword;//票据类别分类规则表主键
	private Integer rowcount;//摘要行数
	private String billtitle;//单据标题
	private String usermanual;//用户干预分类标识
	private String systemmanual;//系统后台数据分析师干预标志
	//手机端使用
	@JsonProperty("zt")
	private Integer sb_status ;//识别状态(0 成功 1失败,2识别中)
	
	private String filepath;//文件路径
	
	private DZFBoolean res;//识别结果
	private DZFBoolean zdres;//制单结果

	private String rzjg;//认证结果,只有进项增值税票有此属性
	private String period;//上传的会计期间
	private Integer pjlxstatus;
	
	private String errordesc;	//分组时问题票据描述
	private String errordesc2;	//检查和第一次入库检查
	private String taxrate;//税率
	
	private String corpCode;
	private String corpName;
	
	private Integer handflag;//0自动1后台2前台
	
	private Integer datasource; //数据来源
	
	private Integer inoutflag;//类别方向
	
	private String categorycode;//类别编码
	
	
	private DZFBoolean updateflag;	//更新数据库标志
	
	private Integer iorder;//ynt_image_ocrlibrary的iorder
	
	private Integer iprovince;
	
	private Integer icity;
	
	private Integer iarea;
	
	private String truthindent;//真伪标识
	
	private String staffname;//汽车票姓名
	
	private DZFDouble version;
	
	//下面这三个字段，是为了进项、销项、银行对账单，转invoiceVO用的
	private String pk_subject;
	private Integer settlement;
	private String pk_settlementaccsubj;
	private String pk_taxaccsubj;
	private String vmxx;//明细项
	private String cyzt;//查验状态
	
	
	
	public String getCyzt() {
		return cyzt;
	}

	public void setCyzt(String cyzt) {
		this.cyzt = cyzt;
	}

	public String getVmxx() {
		return vmxx;
	}

	public void setVmxx(String vmxx) {
		this.vmxx = vmxx;
	}

	public String getPk_taxaccsubj() {
		return pk_taxaccsubj;
	}

	public void setPk_taxaccsubj(String pk_taxaccsubj) {
		this.pk_taxaccsubj = pk_taxaccsubj;
	}

	public String getPk_subject() {
		return pk_subject;
	}

	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}

	public Integer getSettlement() {
		return settlement;
	}

	public void setSettlement(Integer settlement) {
		this.settlement = settlement;
	}

	public String getPk_settlementaccsubj() {
		return pk_settlementaccsubj;
	}

	public void setPk_settlementaccsubj(String pk_settlementaccsubj) {
		this.pk_settlementaccsubj = pk_settlementaccsubj;
	}

	public DZFDouble getVersion() {
		return version;
	}

	public void setVersion(DZFDouble version) {
		this.version = version;
	}

	public String getStaffname() {
		return staffname;
	}

	public void setStaffname(String staffname) {
		this.staffname = staffname;
	}

	public String getTruthindent() {
		return truthindent;
	}

	public void setTruthindent(String truthindent) {
		this.truthindent = truthindent;
	}

	public Integer getIprovince() {
		return iprovince;
	}

	public void setIprovince(Integer iprovince) {
		this.iprovince = iprovince;
	}

	public Integer getIcity() {
		return icity;
	}

	public void setIcity(Integer icity) {
		this.icity = icity;
	}

	public Integer getIarea() {
		return iarea;
	}

	public void setIarea(Integer iarea) {
		this.iarea = iarea;
	}

	public Integer getIorder() {
		return iorder;
	}

	public void setIorder(Integer iorder) {
		this.iorder = iorder;
	}

	public String getBillcategoryname() {
		return billcategoryname;
	}

	public void setBillcategoryname(String billcategoryname) {
		this.billcategoryname = billcategoryname;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public Integer getInoutflag() {
		return inoutflag;
	}

	public void setInoutflag(Integer inoutflag) {
		this.inoutflag = inoutflag;
	}

	public Integer getDatasource() {
		return datasource;
	}

	public void setDatasource(Integer datasource) {
		this.datasource = datasource;
	}

	public String getErrordesc2() {
		return errordesc2;
	}

	public void setErrordesc2(String errordesc2) {
		this.errordesc2 = errordesc2;
	}

	public Integer getHandflag() {
		return handflag;
	}

	public void setHandflag(Integer handflag) {
		this.handflag = handflag;
	}

	public String getCorpCode() {
		return corpCode;
	}

	public void setCorpCode(String corpCode) {
		this.corpCode = corpCode;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}
	
	
	
	public String getVsalename_dk() {
		return vsalename_dk;
	}

	public void setVsalename_dk(String vsalename_dk) {
		this.vsalename_dk = vsalename_dk;
	}

	public String getVsaletaxno_dk() {
		return vsaletaxno_dk;
	}

	public void setVsaletaxno_dk(String vsaletaxno_dk) {
		this.vsaletaxno_dk = vsaletaxno_dk;
	}

	public String getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
	}

	public String getErrordesc() {
		return errordesc;
	}

	public void setErrordesc(String errordesc) {
		this.errordesc = errordesc;
	}

	public String getRzjg() {
		return rzjg;
	}

	public void setRzjg(String rzjg) {
		this.rzjg = rzjg;
	}

	

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getPjlxstatus() {
		return pjlxstatus;
	}

	public void setPjlxstatus(Integer pjlxstatus) {
		this.pjlxstatus = pjlxstatus;
	}



	public String getPk_billcategory() {
		return pk_billcategory;
	}

	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
	}

	public String getPk_category_keyword() {
		return pk_category_keyword;
	}

	public void setPk_category_keyword(String pk_category_keyword) {
		this.pk_category_keyword = pk_category_keyword;
	}

	public Integer getRowcount() {
		return rowcount;
	}

	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}

	public String getBilltitle() {
		return billtitle;
	}

	public void setBilltitle(String billtitle) {
		this.billtitle = billtitle;
	}

	public String getUsermanual() {
		return usermanual;
	}

	public void setUsermanual(String usermanual) {
		this.usermanual = usermanual;
	}

	public String getSystemmanual() {
		return systemmanual;
	}

	public void setSystemmanual(String systemmanual) {
		this.systemmanual = systemmanual;
	}

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public String getIstate() {
		return istate;
	}

	public void setIstate(String istate) {
		this.istate = istate;
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

	public String getDinvoicedate() {
		return dinvoicedate;
	}

	public void setDinvoicedate(String dinvoicedate) {
		this.dinvoicedate = dinvoicedate;
	}

	public String getInvoicetype() {
		return invoicetype;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
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

	public String getVfirsrinvname() {
		return vfirsrinvname;
	}

	public void setVfirsrinvname(String vfirsrinvname) {
		this.vfirsrinvname = vfirsrinvname;
	}

	public String getOcr_id() {
		return ocr_id;
	}

	public void setOcr_id(String ocr_id) {
		this.ocr_id = ocr_id;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getDrcode() {
		return drcode;
	}

	public void setDrcode(String drcode) {
		this.drcode = drcode;
	}

	public String getCycs() {
		return cycs;
	}

	public void setCycs(String cycs) {
		this.cycs = cycs;
	}

	public String getJqbh() {
		return jqbh;
	}

	public void setJqbh(String jqbh) {
		this.jqbh = jqbh;
	}

	public String getJym() {
		return jym;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}

	public String getZfbz() {
		return zfbz;
	}

	public void setZfbz(String zfbz) {
		this.zfbz = zfbz;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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

	public String getVkeywordinfo() {
		return vkeywordinfo;
	}

	public void setVkeywordinfo(String vkeywordinfo) {
		this.vkeywordinfo = vkeywordinfo;
	}

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public void setChildren(OcrInvoiceDetailVO[] children) {
		super.setChildren(children);
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
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

	public String getFilepath() {
		return filepath;
	}

	public DZFBoolean getRes() {
		return res;
	}

	public DZFBoolean getZdres() {
		return zdres;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setRes(DZFBoolean res) {
		this.res = res;
	}

	public void setZdres(DZFBoolean zdres) {
		this.zdres = zdres;
	}

	public Integer getSb_status() {
		return sb_status;
	}

	public void setSb_status(Integer sb_status) {
		this.sb_status = sb_status;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invoice";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_interface_invoice";
	}

	public DZFBoolean getUpdateflag() {
		return updateflag;
	}

	public void setUpdateflag(DZFBoolean updateflag) {
		this.updateflag = updateflag;
	}
}
