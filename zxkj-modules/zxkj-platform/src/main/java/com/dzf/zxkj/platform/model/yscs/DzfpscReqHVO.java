package com.dzf.zxkj.platform.model.yscs;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/****
 * 电子发票信息上传
 * @author asoka
 *
 */
@SuppressWarnings("rawtypes")
public class DzfpscReqHVO extends SuperVO {
	
	
	private static final long serialVersionUID = 1L;
	
	private String pk_dzfpsc_h;

	// 公司编码
	@JsonProperty("DZFP_GSBM")
	private String gsbm;
	
	// 发票请求流水号
	@JsonProperty("DZFP_FPLS")
	private String fpls;
	
	// 订单号
	@JsonProperty("DZFP_DDH")
	private String ddh;
	
	// 防伪码
	@JsonProperty("DZFP_FWM")
	private String fwm;
	
	// 二维码
	@JsonProperty("DZFP_EWM")
	private String ewm;
	
	// 发票代码
	@JsonProperty("DZFP_FPDM")
	private String fpdm;
	
	// 发票号码
	@JsonProperty("DZFP_FPHM")
	private String fphm;
	
	//开票日期
	@JsonProperty("DZFP_KPRQ")
	private String kprq;
	
	//开票类型
	@JsonProperty("DZFP_KPLX")
	private Integer kplx;
	
	//不含税金额
	@JsonProperty("DZFP_BHSJE")
	private DZFDouble bhsje;
	
	//税额
	@JsonProperty("DZFP_SHUIE")
	private DZFDouble shuie;
	
	// Base64（pdf文件）
	@JsonProperty("DZFP_PDF_FILE")
	private String pdffile;
	
	// 购货单位
	@JsonProperty("DZFP_GHDW")
	private String ghdw;
	
	@JsonProperty("DZFP_FPMX")
	private List<DzfpscReqBVO> details;
	
	private String pk_corp;

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_dzfpsc_h";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_yscs_dzfpsc_h";
	}


	public String getGsbm() {
		return gsbm;
	}

	public void setGsbm(String gsbm) {
		this.gsbm = gsbm;
	}


	public String getFpls() {
		return fpls;
	}

	public void setFpls(String fpls) {
		this.fpls = fpls;
	}

	public String getDdh() {
		return ddh;
	}

	public void setDdh(String ddh) {
		this.ddh = ddh;
	}

	public String getFwm() {
		return fwm;
	}

	public void setFwm(String fwm) {
		this.fwm = fwm;
	}

	public String getEwm() {
		return ewm;
	}

	public void setEwm(String ewm) {
		this.ewm = ewm;
	}

	public String getFpdm() {
		return fpdm;
	}

	public void setFpdm(String fpdm) {
		this.fpdm = fpdm;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public String getKprq() {
		return kprq;
	}

	public void setKprq(String kprq) {
		this.kprq = kprq;
	}

	public Integer getKplx() {
		return kplx;
	}

	public void setKplx(Integer kplx) {
		this.kplx = kplx;
	}

	public DZFDouble getBhsje() {
		return bhsje;
	}

	public void setBhsje(DZFDouble bhsje) {
		this.bhsje = bhsje;
	}

	public DZFDouble getShuie() {
		return shuie;
	}

	public void setShuie(DZFDouble shuie) {
		this.shuie = shuie;
	}

	public String getPdffile() {
		return pdffile;
	}

	public void setPdffile(String pdffile) {
		this.pdffile = pdffile;
	}

	public String getGhdw() {
		return ghdw;
	}

	public void setGhdw(String ghdw) {
		this.ghdw = ghdw;
	}

	public String getPk_dzfpsc_h() {
		return pk_dzfpsc_h;
	}

	public void setPk_dzfpsc_h(String pk_dzfpsc_h) {
		this.pk_dzfpsc_h = pk_dzfpsc_h;
	}

	public List<DzfpscReqBVO> getDetails() {
		return details;
	}

	public void setDetails(List<DzfpscReqBVO> details) {
		this.details = details;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	

}
