package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxReportInitVO extends SuperVO {

	
	//主键
	private String pk_taxreportinit;
	//公司
	private String pk_corp;
	
	//申报种类编号
	private String sb_zlbh;
	//期初期间	
	private String period;
	//spread文件	
	private String spreadfile;
	//录入人
	private String coperatorid;
	//录入日期
	private DZFDate doperatedate;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	
	
	//数据备份使用
	private String spreadfilevalue;
	public TaxReportInitVO() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxreportinit";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxreportinit";
	}

	public String getPk_taxreportinit() {
		return pk_taxreportinit;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getPeriod() {
		return period;
	}

	public String getSpreadfile() {
		return spreadfile;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreportinit(String pk_taxreportinit) {
		this.pk_taxreportinit = pk_taxreportinit;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setSpreadfile(String spreadfile) {
		this.spreadfile = spreadfile;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public String getSpreadfilevalue() {
		return spreadfilevalue;
	}

	public void setSpreadfilevalue(String spreadfilevalue) {
		this.spreadfilevalue = spreadfilevalue;
	}
	
}
