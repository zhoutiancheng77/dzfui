package com.dzf.zxkj.app.model.req;

import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 业务请求的bean
 * 
 * @author zhangj
 *
 */
public class BusiReqBeanVo extends UserBeanVO {

	@JsonProperty("imgid")
	private String pk_image_group;

	private String vmemo;// 备注信息

	private String invoice_req_bw;// 百旺发票信息

	private String invalids;// 作废信息
	
	private String qyear;//年度
	
	//--------扫描营业执照--------
	private String drcode;

	// -------资料交接-----------

	private String id;

	private String fileid;// 资料档案id

	private Integer num;// 资料数量

	private String fileids;// ids

	private String qrid;// 二维码id

	private String pk_jjid;// 交接单主键

	private String pk_msgid;// 消息主键

	private String pk_zj;// 会计主键
	
	private String bodys;//
	
	@JsonProperty("vbperiod")
	private String vbegperiod;
	
	@JsonProperty("veperiod")
	private String vendperiod;
	
	private String memo;//备注1
	
	//---------行业
	private String hymc;// 名称

	@JsonProperty("qrytype")
	private Integer qrytype;

	public Integer getQrytype() {
		return qrytype;
	}

	public void setQrytype(Integer qrytype) {
		this.qrytype = qrytype;
	}

	public String getDrcode() {
		return drcode;
	}

	public void setDrcode(String drcode) {
		this.drcode = drcode;
	}

	public String getBodys() {
		return bodys;
	}

	public void setBodys(String bodys) {
		this.bodys = bodys;
	}

	public String getHymc() {
		return hymc;
	}

	public void setHymc(String hymc) {
		this.hymc = hymc;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getQyear() {
		return qyear;
	}

	public void setQyear(String qyear) {
		this.qyear = qyear;
	}

	public String getVbegperiod() {
		return vbegperiod;
	}

	public void setVbegperiod(String vbegperiod) {
		this.vbegperiod = vbegperiod;
	}

	public String getVendperiod() {
		return vendperiod;
	}

	public void setVendperiod(String vendperiod) {
		this.vendperiod = vendperiod;
	}

	public String getPk_zj() {
		return pk_zj;
	}

	public void setPk_zj(String pk_zj) {
		this.pk_zj = pk_zj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getFileids() {
		return fileids;
	}

	public void setFileids(String fileids) {
		this.fileids = fileids;
	}

	public String getQrid() {
		return qrid;
	}

	public void setQrid(String qrid) {
		this.qrid = qrid;
	}

	public String getPk_jjid() {
		return pk_jjid;
	}

	public void setPk_jjid(String pk_jjid) {
		this.pk_jjid = pk_jjid;
	}

	public String getPk_msgid() {
		return pk_msgid;
	}

	public void setPk_msgid(String pk_msgid) {
		this.pk_msgid = pk_msgid;
	}

	public String getInvoice_req_bw() {
		return invoice_req_bw;
	}

	public void setInvoice_req_bw(String invoice_req_bw) {
		this.invoice_req_bw = invoice_req_bw;
	}

	public String getInvalids() {
		return invalids;
	}

	public void setInvalids(String invalids) {
		this.invalids = invalids;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

}
