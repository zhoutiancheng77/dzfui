package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class TaxReportVO extends SuperVO {

	//主键
	private String pk_taxreport;
	//公司	
	private String pk_corp;
	//代账公司
	private String pk_corp_account;
	
	//纳税人识别号
	private String nsrsbh;
	//纳税人电子档案号	
	private String nsrdzdah;
		
	//所属地区	例如：北京，上海，江苏
	private String location;
	//征收项目代码	例如：1:增值税
	private String zsxm_dm;
	//申报种类编号	
	private String sb_zlbh;//原有字段，和sbcode同含义
	//申报编码
	private String sbcode;//不存库
	//申报名称
	private String sbname;//不存库
	//申报种类pk
	private String pk_taxsbzl;//存库，申报种类pk
	//申报期间
	private String period;//存库 /存2018-01  月报 ///2018-03 季报/// 2017 年报
	//spread文件
	private String spreadfile;//存库
	//填报周期	
	private Integer periodtype;
	//税款所属时间起	
	private String periodfrom;
	//税款所属时间止	
	private String periodto;
	//申报状态代码
	private String sbzt_dm;
	//申报状态-零申报
	private DZFBoolean sbzt_osb;
	//单据状态
	private Integer vbillstatus;
	//录入人
	private String coperatorid;
	//录入日期
	private DZFDate doperatedate;
	//审核人
	private String vapproveid;
	//审核日期	
	private String dapprovedate;
	//审核批语
	private String vapprovenote;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	
	//税额
	private DZFDouble taxmny;
	//备注信息
	private String remark;
	/**
	 * 江苏 申报LSH
	 */
	private String region_extend1;
	/**
	 * 江苏 申报成功的凭证序号
	 */
	private String region_extend2;
	private String region_extend3;
	

	public TaxReportVO() {
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
		return "pk_taxreport";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxreport";
	}

	public String getPk_taxreport() {
		return pk_taxreport;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getLocation() {
		return location;
	}

	public String getZsxm_dm() {
		return zsxm_dm;
	}

	public Integer getPeriodtype() {
		return periodtype;
	}

	public String getPeriodfrom() {
		return periodfrom;
	}

	public String getPeriodto() {
		return periodto;
	}

	public String getSbzt_dm() {
		return sbzt_dm;
	}

	public DZFBoolean getSbzt_osb() {
		return sbzt_osb;
	}

	public void setSbzt_osb(DZFBoolean sbzt_osb) {
		this.sbzt_osb = sbzt_osb;
	}

	public Integer getVbillstatus() {
		return vbillstatus;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public String getVapproveid() {
		return vapproveid;
	}

	public String getDapprovedate() {
		return dapprovedate;
	}

	public String getVapprovenote() {
		return vapprovenote;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setZsxm_dm(String zsxm_dm) {
		this.zsxm_dm = zsxm_dm;
	}

	public void setPeriodtype(Integer periodtype) {
		this.periodtype = periodtype;
	}

	public void setPeriodfrom(String periodfrom) {
		this.periodfrom = periodfrom;
	}

	public void setPeriodto(String periodto) {
		this.periodto = periodto;
	}

	public void setSbzt_dm(String sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}

	public void setVbillstatus(Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setVapproveid(String vapproveid) {
		this.vapproveid = vapproveid;
	}

	public void setDapprovedate(String dapprovedate) {
		this.dapprovedate = dapprovedate;
	}

	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}


	public String getPk_corp_account() {
		return pk_corp_account;
	}

	public void setPk_corp_account(String pk_corp_account) {
		this.pk_corp_account = pk_corp_account;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public String getNsrdzdah() {
		return nsrdzdah;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public void setNsrdzdah(String nsrdzdah) {
		this.nsrdzdah = nsrdzdah;
	}

	public DZFDouble getTaxmny() {
		return taxmny;
	}

	public void setTaxmny(DZFDouble taxmny) {
		this.taxmny = taxmny;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRegion_extend1() {
		return region_extend1;
	}

	public void setRegion_extend1(String region_extend1) {
		this.region_extend1 = region_extend1;
	}

	public String getRegion_extend2() {
		return region_extend2;
	}

	public void setRegion_extend2(String region_extend2) {
		this.region_extend2 = region_extend2;
	}

	public String getRegion_extend3() {
		return region_extend3;
	}

	public void setRegion_extend3(String region_extend3) {
		this.region_extend3 = region_extend3;
	}

	public String getSbcode() {
		return sbcode;
	}

	public void setSbcode(String sbcode) {
		this.sbcode = sbcode;
	}

	public String getSbname() {
		return sbname;
	}

	public void setSbname(String sbname) {
		this.sbname = sbname;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSpreadfile() {
		return spreadfile;
	}

	public void setSpreadfile(String spreadfile) {
		this.spreadfile = spreadfile;
	}

	public String getPk_taxsbzl() {
		return pk_taxsbzl;
	}

	public void setPk_taxsbzl(String pk_taxsbzl) {
		this.pk_taxsbzl = pk_taxsbzl;
	}
}
