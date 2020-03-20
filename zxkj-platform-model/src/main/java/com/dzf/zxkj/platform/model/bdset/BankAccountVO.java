package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.entity.ICodeName;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 银行账户
 *
 * @author wangzhn
 */
public class BankAccountVO extends SuperVO implements ICodeName {

    @JsonProperty("id")
    private String pk_bankaccount;//主键
    @JsonProperty("cid")
    private String coperatorid;//操作人
    @JsonProperty("ddate")
    private DZFDate doperatedate;//操作时间
    @JsonProperty("corpid")
    private String pk_corp;
    //	private String  batchflag;//批次号
    private int serialnum;//序号
    @JsonProperty("bkcode")
    private String bankcode;//编码
    @JsonProperty("bkname")
    private String bankname;//银行账户名称
    @JsonProperty("bkaccout")
    private String bankaccount;//银行账号
    @JsonProperty("accname_id")
    private String relatedsubj;//关联会计科目
    @JsonProperty("accode")
    private String accountcode;
    @JsonProperty("accname")
    private String accountname;
    private int state;//银行账户状态

    private String modifyoperid;//修改人
    private DZFDateTime modifydatetime;//修改时间
    private int dr;
    private DZFDateTime ts;


    // add 2020.03.17
    private String istatus;//	签约状态 0-未上送1-未签约2-已签约3-签约失败
    @JsonProperty("banktype")
    private String banktype;//	银行类型
    @JsonProperty("bankTypeCode")
    private String bankTypeCode; //银行账户编码  同步于手机app   1 工商银行
    @JsonProperty("applycode")
    private String vapplycode;//	申请码
    private String ly; //来源  0 签约生成  1 手工生成

    public String getIstatus() {
        return istatus;
    }

    public void setIstatus(String istatus) {
        this.istatus = istatus;
    }

    public String getBanktype() {
        return banktype;
    }

    public void setBanktype(String banktype) {
        this.banktype = banktype;
    }

    public String getBankTypeCode() {
        return bankTypeCode;
    }

    public void setBankTypeCode(String bankTypeCode) {
        this.bankTypeCode = bankTypeCode;
    }

    public String getVapplycode() {
        return vapplycode;
    }

    public void setVapplycode(String vapplycode) {
        this.vapplycode = vapplycode;
    }

    public String getLy() {
        return ly;
    }

    public void setLy(String ly) {
        this.ly = ly;
    }

    public String getPk_bankaccount() {
        return pk_bankaccount;
    }

    public void setPk_bankaccount(String pk_bankaccount) {
        this.pk_bankaccount = pk_bankaccount;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

//	public String getBatchflag() {
//		return batchflag;
//	}
//
//	public void setBatchflag(String batchflag) {
//		this.batchflag = batchflag;
//	}

    public int getSerialnum() {
        return serialnum;
    }

    public void setSerialnum(int serialnum) {
        this.serialnum = serialnum;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getRelatedsubj() {
        return relatedsubj;
    }

    public void setRelatedsubj(String relatedsubj) {
        this.relatedsubj = relatedsubj;
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getModifyoperid() {
        return modifyoperid;
    }

    public void setModifyoperid(String modifyoperid) {
        this.modifyoperid = modifyoperid;
    }

    public DZFDateTime getModifydatetime() {
        return modifydatetime;
    }

    public void setModifydatetime(DZFDateTime modifydatetime) {
        this.modifydatetime = modifydatetime;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getPKFieldName() {
        return "pk_bankaccount";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_bankaccount";
    }

    @Override
    public String getCode() {
        return getBankcode();
    }

    @Override
    public void setCode(String code) {
        setBankcode(code);
    }

    @Override
    public String getName() {
        return getBankname();
    }

    @Override
    public void setName(String name) {
        setBankname(name);
    }

}
