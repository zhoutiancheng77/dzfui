package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class PiaoTongResInvVO implements Serializable {

    private String invoiceReqSerialNo;//
    private String invoiceIssueType;//
    private String invoiceIssueResultCode;//
    private String invoiceIssueResultMsg;//

    private PiaoTongResInvHVO invoiceInfo;//

    public String getInvoiceReqSerialNo() {
        return invoiceReqSerialNo;
    }

    public void setInvoiceReqSerialNo(String invoiceReqSerialNo) {
        this.invoiceReqSerialNo = invoiceReqSerialNo;
    }

    public String getInvoiceIssueType() {
        return invoiceIssueType;
    }

    public void setInvoiceIssueType(String invoiceIssueType) {
        this.invoiceIssueType = invoiceIssueType;
    }

    public String getInvoiceIssueResultCode() {
        return invoiceIssueResultCode;
    }

    public void setInvoiceIssueResultCode(String invoiceIssueResultCode) {
        this.invoiceIssueResultCode = invoiceIssueResultCode;
    }

    public String getInvoiceIssueResultMsg() {
        return invoiceIssueResultMsg;
    }

    public void setInvoiceIssueResultMsg(String invoiceIssueResultMsg) {
        this.invoiceIssueResultMsg = invoiceIssueResultMsg;
    }

    public PiaoTongResInvHVO getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(PiaoTongResInvHVO invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }
}
