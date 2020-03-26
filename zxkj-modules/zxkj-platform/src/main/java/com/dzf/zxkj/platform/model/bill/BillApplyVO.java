package com.dzf.zxkj.platform.model.bill;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 开票申请vo
 *
 * @author zhangj
 */
public class BillApplyVO extends SuperVO {

    public static final String TABLE_NAME = "ynt_app_billapply";
    public static final String PK_FIELD = "pk_app_billapply";

    @JsonProperty("id")
    private String pk_app_billapply;// 主键
    @JsonProperty("kh_id")
    private String pk_app_customer;// 客户主键
    @JsonProperty("fplx")
    private Integer vbilltype;// 发票类型
    @JsonProperty("xmmc")
    private String vproject;// 项目名称
    @JsonProperty("mny")
    private DZFDouble nmny;// 金额
    @JsonProperty("sl")
    private DZFDouble ntaxrate;// 税率
    @JsonProperty("se")
    private DZFDouble ntaxmny;// 税额
    @JsonProperty("jshj")
    private DZFDouble ntaxtotal;// 价税合计
    @JsonProperty("corp")
    private String pk_corp;// 公司
    @JsonProperty("tcorp")
    private String pk_temp_corp;// 临时公司
    private DZFDateTime ts;// ts
    private Integer dr;// dr
    @JsonProperty("org_id")
    private String pk_account;// 服务网点
    @JsonProperty("pjzt")
    private Integer ibillstatus;//票据状态
    @JsonProperty("account_id")
    private String vapplytor;//申请人
    @JsonProperty("sqrcode")
    private String vapplycode;//申请人code
    @JsonProperty("sqrq")
    private DZFDateTime dapplydate;//申请日期
    private Long sqsjl;//申请时间
    @JsonProperty("kpr")
    private String vbilltor;//开票人
    @JsonProperty("kprq")
    private DZFDateTime ddate;//开票日期
    @JsonProperty("jcsj")
    private DZFDateTime dsenddate;//寄出时间
    @JsonProperty("jcr")
    private String vsendtor;//寄出人
    @JsonProperty("jzr")
    private String vaccountor;//记账人
    @JsonProperty("jzsj")
    private DZFDateTime daccountdate;//记账时间
    @JsonProperty("bsr")
    private String vtaxer;//报税人
    @JsonProperty("bssj")
    private DZFDateTime dtaxdate;//报税时间
    @JsonProperty("serino")
    private String invoserino;//发票请求流水号
    @JsonProperty("dm")
    private String fpdm;//发票代码
    @JsonProperty("hm")
    private String fphm;//发票号码
    @JsonProperty("ydm")
    private String yfpdm;//原发票代码
    @JsonProperty("yhm")
    private String yfphm;//原发票号码

    //查询字段
    private String apply_name;//申请人名称
    private String billing_name;//开票人
    private String sendout_name;//寄出人
    private String accounting_name;//记账人
    private String taxer_name;//报税人
    //-----------客户详情--------
    @JsonProperty("mail")
    private String vmail;//邮箱
    @JsonProperty("qymc")
    private String vcompanyname;// 企业名称
    @JsonProperty("qylx")
    private Integer vcompanytype;// 企业类型
    @JsonProperty("sh")
    private String vtaxcode;// 税号
    @JsonProperty("gsdz")
    private String vcompanyaddr;// 公司地址
    @JsonProperty("kpdh")
    private String vphone;// 开票电话
    @JsonProperty("khh")
    private String vbank;// 开户行
    @JsonProperty("khzh")
    private String vbankcode;// 开户账号

    public String getInvoserino() {
        return invoserino;
    }

    public void setInvoserino(String invoserino) {
        this.invoserino = invoserino;
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

    public String getYfpdm() {
        return yfpdm;
    }

    public void setYfpdm(String yfpdm) {
        this.yfpdm = yfpdm;
    }

    public String getYfphm() {
        return yfphm;
    }

    public void setYfphm(String yfphm) {
        this.yfphm = yfphm;
    }

    public String getPk_app_billapply() {
        return pk_app_billapply;
    }

    public void setPk_app_billapply(String pk_app_billapply) {
        this.pk_app_billapply = pk_app_billapply;
    }

    public String getPk_app_customer() {
        return pk_app_customer;
    }

    public void setPk_app_customer(String pk_app_customer) {
        this.pk_app_customer = pk_app_customer;
    }

    public Integer getVbilltype() {
        return vbilltype;
    }

    public void setVbilltype(Integer vbilltype) {
        this.vbilltype = vbilltype;
    }

    public String getVproject() {
        return vproject;
    }

    public void setVproject(String vproject) {
        this.vproject = vproject;
    }

    public DZFDouble getNmny() {
        return nmny;
    }

    public void setNmny(DZFDouble nmny) {
        this.nmny = nmny;
    }

    public DZFDouble getNtaxrate() {
        return ntaxrate;
    }

    public void setNtaxrate(DZFDouble ntaxrate) {
        this.ntaxrate = ntaxrate;
    }

    public DZFDouble getNtaxmny() {
        return ntaxmny;
    }

    public void setNtaxmny(DZFDouble ntaxmny) {
        this.ntaxmny = ntaxmny;
    }

    public DZFDouble getNtaxtotal() {
        return ntaxtotal;
    }

    public void setNtaxtotal(DZFDouble ntaxtotal) {
        this.ntaxtotal = ntaxtotal;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_temp_corp() {
        return pk_temp_corp;
    }

    public void setPk_temp_corp(String pk_temp_corp) {
        this.pk_temp_corp = pk_temp_corp;
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

    public String getPk_account() {
        return pk_account;
    }

    public void setPk_account(String pk_account) {
        this.pk_account = pk_account;
    }

    public Integer getIbillstatus() {
        return ibillstatus;
    }

    public void setIbillstatus(Integer ibillstatus) {
        this.ibillstatus = ibillstatus;
    }

    public String getVapplytor() {
        return vapplytor;
    }

    public void setVapplytor(String vapplytor) {
        this.vapplytor = vapplytor;
    }

    public String getVapplycode() {
        return vapplycode;
    }

    public void setVapplycode(String vapplycode) {
        this.vapplycode = vapplycode;
    }

    public DZFDateTime getDapplydate() {
        return dapplydate;
    }

    public void setDapplydate(DZFDateTime dapplydate) {
        this.dapplydate = dapplydate;
    }

    public Long getSqsjl() {
        return sqsjl;
    }

    public void setSqsjl(Long sqsjl) {
        this.sqsjl = sqsjl;
    }

    public String getVbilltor() {
        return vbilltor;
    }

    public void setVbilltor(String vbilltor) {
        this.vbilltor = vbilltor;
    }

    public DZFDateTime getDdate() {
        return ddate;
    }

    public void setDdate(DZFDateTime ddate) {
        this.ddate = ddate;
    }

    public DZFDateTime getDsenddate() {
        return dsenddate;
    }

    public void setDsenddate(DZFDateTime dsenddate) {
        this.dsenddate = dsenddate;
    }

    public String getVsendtor() {
        return vsendtor;
    }

    public void setVsendtor(String vsendtor) {
        this.vsendtor = vsendtor;
    }

    public String getVaccountor() {
        return vaccountor;
    }

    public void setVaccountor(String vaccountor) {
        this.vaccountor = vaccountor;
    }

    public DZFDateTime getDaccountdate() {
        return daccountdate;
    }

    public void setDaccountdate(DZFDateTime daccountdate) {
        this.daccountdate = daccountdate;
    }

    public String getVtaxer() {
        return vtaxer;
    }

    public void setVtaxer(String vtaxer) {
        this.vtaxer = vtaxer;
    }

    public DZFDateTime getDtaxdate() {
        return dtaxdate;
    }

    public void setDtaxdate(DZFDateTime dtaxdate) {
        this.dtaxdate = dtaxdate;
    }

    public String getApply_name() {
        return apply_name;
    }

    public void setApply_name(String apply_name) {
        this.apply_name = apply_name;
    }

    public String getAccounting_name() {
        return accounting_name;
    }

    public void setAccounting_name(String accounting_name) {
        this.accounting_name = accounting_name;
    }

    public String getSendout_name() {
        return sendout_name;
    }

    public void setSendout_name(String sendout_name) {
        this.sendout_name = sendout_name;
    }

    public String getBilling_name() {
        return billing_name;
    }

    public void setBilling_name(String billing_name) {
        this.billing_name = billing_name;
    }

    public String getTaxer_name() {
        return taxer_name;
    }

    public void setTaxer_name(String taxer_name) {
        this.taxer_name = taxer_name;
    }

    public String getVmail() {
        return vmail;
    }

    public void setVmail(String vmail) {
        this.vmail = vmail;
    }

    public String getVcompanyname() {
        return vcompanyname;
    }

    public void setVcompanyname(String vcompanyname) {
        this.vcompanyname = vcompanyname;
    }

    public Integer getVcompanytype() {
        return vcompanytype;
    }

    public void setVcompanytype(Integer vcompanytype) {
        this.vcompanytype = vcompanytype;
    }

    public String getVtaxcode() {
        return vtaxcode;
    }

    public void setVtaxcode(String vtaxcode) {
        this.vtaxcode = vtaxcode;
    }

    public String getVcompanyaddr() {
        return vcompanyaddr;
    }

    public void setVcompanyaddr(String vcompanyaddr) {
        this.vcompanyaddr = vcompanyaddr;
    }

    public String getVphone() {
        return vphone;
    }

    public void setVphone(String vphone) {
        this.vphone = vphone;
    }

    public String getVbank() {
        return vbank;
    }

    public void setVbank(String vbank) {
        this.vbank = vbank;
    }

    public String getVbankcode() {
        return vbankcode;
    }

    public void setVbankcode(String vbankcode) {
        this.vbankcode = vbankcode;
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
