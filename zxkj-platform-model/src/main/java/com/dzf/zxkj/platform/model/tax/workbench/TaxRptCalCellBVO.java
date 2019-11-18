package com.dzf.zxkj.platform.model.tax.workbench;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 纳税工作台调用参数子VO
 * @author wangzhn
 *
 */
public class TaxRptCalCellBVO extends SuperVO {
	
	private Integer iuptaxcode;//更新税种编码
	
	private String pk_corp;//公司
	
	private String period;//期间
	
	private String periodtype;//申报周期
	
	private String sbzlbh;//申报种类编号
	
	private String reportname;//报表名称
	
	private String x;//坐标x
	
	private String y;//坐标y
	
	private DZFDouble mny;//金额
	
	private Integer vbillstatus;//状态
	
	private String sbzt_dm;//申报状态
	
	private Integer txstatus;//填写状态 	spreadfile字段是否有内容
	
	public Integer getTxstatus() {
		return txstatus;
	}

	public void setTxstatus(Integer txstatus) {
		this.txstatus = txstatus;
	}

	public Integer getIuptaxcode() {
		return iuptaxcode;
	}

	public void setIuptaxcode(Integer iuptaxcode) {
		this.iuptaxcode = iuptaxcode;
	}

	public String getSbzt_dm() {
		return sbzt_dm;
	}

	public void setSbzt_dm(String sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getPeriod() {
		return period;
	}

	public String getPeriodtype() {
		return periodtype;
	}

	public String getSbzlbh() {
		return sbzlbh;
	}

	public String getReportname() {
		return reportname;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public Integer getVbillstatus() {
		return vbillstatus;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setPeriodtype(String periodtype) {
		this.periodtype = periodtype;
	}

	public void setSbzlbh(String sbzlbh) {
		this.sbzlbh = sbzlbh;
	}

	public void setReportname(String reportname) {
		this.reportname = reportname;
	}

	public void setX(String x) {
		this.x = x;
	}

	public void setY(String y) {
		this.y = y;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public void setVbillstatus(Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
