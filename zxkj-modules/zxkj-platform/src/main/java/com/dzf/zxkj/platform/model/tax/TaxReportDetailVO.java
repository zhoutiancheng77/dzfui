package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxReportDetailVO extends SuperVO {

	//主键	
	private String pk_taxreportdetail;
	//主表主键	
	private String pk_taxreport;
	//公司主键
	private String pk_corp;
	//模板主键	
	private String pk_taxrpttemplet;
	//申报种类编号	例如：10101:增值税纳税申报表
	private String sb_zlbh;
	//报表编码
	private String reportcode;
	//报表名称
	private String reportname;
	//pdf文件
	private String pdffile;
	//spread文件
	private String spreadfile;
	//申报流水号
	private String sb_lsh;
	//申报状态代码
	private String sbzt_dm;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	//序号
	private Integer orderno;
	//填报序号-----------------------------------此字段没有用。税种排序通过ynt_tax_sbzl表中的showorder
	private Integer taxorder;
	
	//数据备份使用
	private String pdffilevalue;
	private String spreadfilevalue;
	//end
	
	public TaxReportDetailVO() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxreport";
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxreportdetail";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxreportdetail";
	}

	public String getPk_taxreportdetail() {
		return pk_taxreportdetail;
	}

	public String getPk_taxreport() {
		return pk_taxreport;
	}

	public String getPk_taxrpttemplet() {
		return pk_taxrpttemplet;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public String getReportcode() {
		return reportcode;
	}

	public String getReportname() {
		return reportname;
	}

	public String getPdffile() {
		return pdffile;
	}

	public String getSpreadfile() {
		return spreadfile;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreportdetail(String pk_taxreportdetail) {
		this.pk_taxreportdetail = pk_taxreportdetail;
	}

	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}

	public void setPk_taxrpttemplet(String pk_taxrpttemplet) {
		this.pk_taxrpttemplet = pk_taxrpttemplet;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public void setReportcode(String reportcode) {
		this.reportcode = reportcode;
	}

	public void setReportname(String reportname) {
		this.reportname = reportname;
	}

	public void setPdffile(String pdffile) {
		this.pdffile = pdffile;
	}

	public void setSpreadfile(String spreadfile) {
		this.spreadfile = spreadfile;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getSb_lsh() {
		return sb_lsh;
	}

	public void setSb_lsh(String sb_lsh) {
		this.sb_lsh = sb_lsh;
	}
	public String getSbzt_dm() {
		return sbzt_dm;
	}
	public void setSbzt_dm(String sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}

	public Integer getOrderno() {
		return orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}
	
	public Integer getTaxorder() {
		return taxorder;
	}

	public void setTaxorder(Integer taxorder) {
		this.taxorder = taxorder;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPdffilevalue() {
		return pdffilevalue;
	}

	public void setPdffilevalue(String pdffilevalue) {
		this.pdffilevalue = pdffilevalue;
	}

	public String getSpreadfilevalue() {
		return spreadfilevalue;
	}

	public void setSpreadfilevalue(String spreadfilevalue) {
		this.spreadfilevalue = spreadfilevalue;
	}
	
	
}
