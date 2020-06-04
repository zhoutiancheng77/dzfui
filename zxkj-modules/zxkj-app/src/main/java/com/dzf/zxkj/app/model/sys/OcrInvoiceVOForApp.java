package com.dzf.zxkj.app.model.sys;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrInvoiceVOForApp extends SuperVO {

	/**
	 * 接口 发票信息头
	 */
	private static final long serialVersionUID = 5286071006532256962L;

	@JsonProperty("fpdm")
	private String vinvoicecode;// 发票代码
	@JsonProperty("fphm")
	private String vinvoiceno;// 发票号
	@JsonProperty("kprq")
	private String dinvoicedate;// 开票日期
	@JsonProperty("fplx")
	private String invoicetype;// 发票类型
	@JsonProperty("gmf")
	private String vpurchname;// 购方企业名称
	@JsonProperty("gmfsbh")
	private String vpurchtaxno;// 购方纳税号
	@JsonProperty("xsf")
	private String vsalename;// 销方企业名称
	@JsonProperty("xsfsbh")
	private String vsaletaxno;// 销方纳税号
	@JsonProperty("jshj")
	private String ntotaltax;// 价税合计
	@JsonProperty("kpje")
	private String nmny;// 金额合计
	@JsonProperty("se")
	private String ntaxnmny;// 税额合计
	private String vsaleopenacc;// 销售方开户账号
	private String vsalephoneaddr;// 销售方地址电话
	private String vpuropenacc;// 购买方开户账号
	private String vpurphoneaddr;// "购买方地址电话
	private String vfirsrinvname; // 首件货物名称
	private String ocr_id;// ocr信息id
	@JsonProperty("id")
	private String pk_invoice; // 主键
	@JsonProperty("corpId")
	private String pk_corp;// 会计公司
	private Integer dr;// 删除标志
	private DZFDateTime ts; // 时间戳
	private String istate;// 识别状态

	private String drcode; // 二维码
	private String cycs;// 查验次数
	private String jqbh;// 机器编号
	@JsonProperty("jym")
	private String jym;// 校验码
	private String zfbz;// 作废标志
	private Integer itype;// 类型
	@JsonProperty("zt")
	private Integer sb_status ;//识别状态(0 成功 1失败,2识别中)
	
	private String filepath;//文件路径
	
	private DZFBoolean res;//识别结果
	private DZFBoolean zdres;//制单结果
	
	

	public DZFBoolean getRes() {
		return res;
	}

	public void setRes(DZFBoolean res) {
		this.res = res;
	}

	public DZFBoolean getZdres() {
		return zdres;
	}

	public void setZdres(DZFBoolean zdres) {
		this.zdres = zdres;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Integer getSb_status() {
		return sb_status;
	}

	public void setSb_status(Integer sb_status) {
		this.sb_status = sb_status;
	}

	private String pk_image_group;//图片信息组主键

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

	public void setChildren(OcrInvoiceDetailVO[] children) {
		super.setChildren(children);
	}

	
}
