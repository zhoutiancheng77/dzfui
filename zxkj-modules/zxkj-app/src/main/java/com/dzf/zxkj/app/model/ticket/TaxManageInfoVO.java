package com.dzf.zxkj.app.model.ticket;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;


/**
 * 抄报税信息
 *
 */
public class TaxManageInfoVO extends SuperVO {
	
	public static final String TABLE_NAME = "ynt_bw_taxmanage";

	public static final String PK_FIELD = "pk_taxmanageinfo";

	public String pk_taxmanageinfo;
	
	//开票类型 , 004：增值税专用发票 007：增值税普通发票 026：增值税电子普通发票 025：增值税卷式普通发票
	public String InvoiceType;
	//开票截止日期, 格式：20170517
	public String InvoiceStopDate;
	//数据报送起始日期
	public String DataSendStartDate;
	//数据报送截止日期
	public String DataSendStopDate;
	//单张发票开票金额限额
	public DZFDouble MaxAmount;
	//最新报税日期
	public String NewInvoiceDate;
	//上传截止日期
	public String UploadStopDate;
	//离线开票时长, 单位：小时
	public DZFDouble StayTime;
	//离线开具累计金额
	public DZFDouble InvoiceTotalAmount;
	//当期允许最大开票量
	public Integer MaxCount;
	public String pk_corp;
	//IOT
	public String iotid;
	
	public DZFDateTime ts;
	
	public Integer dr;
	

	public String getPk_taxmanageinfo() {
		return pk_taxmanageinfo;
	}

	public void setPk_taxmanageinfo(String pk_taxmanageinfo) {
		this.pk_taxmanageinfo = pk_taxmanageinfo;
	}

	public String getInvoiceType() {
		return InvoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		InvoiceType = invoiceType;
	}

	public String getInvoiceStopDate() {
		return InvoiceStopDate;
	}

	public void setInvoiceStopDate(String invoiceStopDate) {
		InvoiceStopDate = invoiceStopDate;
	}

	public String getDataSendStartDate() {
		return DataSendStartDate;
	}

	public void setDataSendStartDate(String dataSendStartDate) {
		DataSendStartDate = dataSendStartDate;
	}

	public String getDataSendStopDate() {
		return DataSendStopDate;
	}

	public void setDataSendStopDate(String dataSendStopDate) {
		DataSendStopDate = dataSendStopDate;
	}

	public DZFDouble getMaxAmount() {
		return MaxAmount;
	}

	public void setMaxAmount(DZFDouble maxAmount) {
		MaxAmount = maxAmount;
	}

	public String getNewInvoiceDate() {
		return NewInvoiceDate;
	}

	public void setNewInvoiceDate(String newInvoiceDate) {
		NewInvoiceDate = newInvoiceDate;
	}

	public String getUploadStopDate() {
		return UploadStopDate;
	}

	public void setUploadStopDate(String uploadStopDate) {
		UploadStopDate = uploadStopDate;
	}

	public DZFDouble getStayTime() {
		return StayTime;
	}

	public void setStayTime(DZFDouble stayTime) {
		StayTime = stayTime;
	}

	public DZFDouble getInvoiceTotalAmount() {
		return InvoiceTotalAmount;
	}

	public void setInvoiceTotalAmount(DZFDouble invoiceTotalAmount) {
		InvoiceTotalAmount = invoiceTotalAmount;
	}

	public Integer getMaxCount() {
		return MaxCount;
	}

	public void setMaxCount(Integer maxCount) {
		MaxCount = maxCount;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getIotid() {
		return iotid;
	}

	public void setIotid(String iotid) {
		this.iotid = iotid;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
