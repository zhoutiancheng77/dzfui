package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class OcrInvoiceColumnVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_interface_column;// 主键
	private String webid;// 聂老师网站id
	private String pk_image_library;// 图片id
	private String pk_image_ocrlibrary;// ocr信息id
	private String pk_invoice; // 识别信息主键
	private String vmemo;// 备注
	private String vcolumnname;// 字段名称
	private String vcolumnkey;// 字段key
	private String vrtnmsg;// 错误信息
	private String vcolumninfo;// 修改后信息
	private String pk_corp;// 会计公司
	private Integer dr;// 删除标志
	private DZFDateTime ts;// 时间戳
	private String coperatorid;//修订人

	private Integer iversionno;// 版本号
	private DZFBoolean islatestversion;// 最新版本
	private Integer isource;// 来源

	public static int DataCenterSource = 1;// 数据中心
	public static int OnLineSource = 2;// 在线会计

	public String getPk_interface_column() {
		return pk_interface_column;
	}

	public String getWebid() {
		return webid;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public String getPk_image_ocrlibrary() {
		return pk_image_ocrlibrary;
	}

	public String getVmemo() {
		return vmemo;
	}

	public String getVrtnmsg() {
		return vrtnmsg;
	}

	public String getVcolumninfo() {
		return vcolumninfo;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_interface_column(String pk_interface_column) {
		this.pk_interface_column = pk_interface_column;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public void setPk_image_ocrlibrary(String pk_image_ocrlibrary) {
		this.pk_image_ocrlibrary = pk_image_ocrlibrary;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public void setVrtnmsg(String vrtnmsg) {
		this.vrtnmsg = vrtnmsg;
	}

	public void setVcolumninfo(String vcolumninfo) {
		this.vcolumninfo = vcolumninfo;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcolumnname() {
		return vcolumnname;
	}

	public String getVcolumnkey() {
		return vcolumnkey;
	}

	public void setVcolumnname(String vcolumnname) {
		this.vcolumnname = vcolumnname;
	}

	public void setVcolumnkey(String vcolumnkey) {
		this.vcolumnkey = vcolumnkey;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}
	
	public Integer getIversionno() {
		return iversionno;
	}

	public DZFBoolean getIslatestversion() {
		return islatestversion;
	}

	public Integer getIsource() {
		return isource;
	}

	public void setIversionno(Integer iversionno) {
		this.iversionno = iversionno;
	}

	public void setIslatestversion(DZFBoolean islatestversion) {
		this.islatestversion = islatestversion;
	}

	public void setIsource(Integer isource) {
		this.isource = isource;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	@Override
	public String getPKFieldName() {
		return "pk_interface_column";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_interface_column";
	}
}
