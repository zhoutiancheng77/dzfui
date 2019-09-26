package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class FzhsqcVO extends SuperVO {
    private String date;//登录日期
    private String vapproveid;
    private String vapprovenote;
    private DZFDate dapprovedate;
    private String pk_billtype;
    private DZFDouble vbillstatus;
    private String vbillno;
    private Integer dr;
    private DZFDateTime ts;
    private String dcurrency;
    private DZFDouble nrate;
    private String vdef1;
    private String vdef2;
    private String vdef3;
    private String vdef4;
    private String vdef5;
    private String vdef6;
    private String vdef7;
    private String vdef8;
    private String vdef9;
    private String vdef10;
    private DZFBoolean isleaf;//是否末级

    private DZFDouble bnqcnum;//本年期初数量
    private DZFDouble bnfsnum;//本年借方发生数量
    private DZFDouble bndffsnum;//本年贷方发生数量
    private DZFDouble monthqmnum;//本月期初数量

    private DZFBoolean isnum;
    // 规格型号
    private String spec;
    // 计量单位
    private String jldw;

    private Integer slwh;

    //使用以下字段
    @JsonProperty("cpid")
    private String pk_corp;
    @JsonProperty("userid")
    private String coperatorid;
    @JsonProperty("kmname")
    private String vname;
    @JsonProperty("qcid")
    private String pk_qcye;
    @JsonProperty("ndf")
    private DZFDouble yeardffse;
    @JsonProperty("kmcode")
    private String vcode;
    @JsonProperty("monthqc")
    private DZFDouble thismonthqc;
    @JsonProperty("njf")
    private DZFDouble yearjffse;
    @JsonProperty("pk_km")
    private String pk_accsubj;
    @JsonProperty("nqc")
    private DZFDouble yearqc;
    @JsonProperty("momo")
    private String memo;
    @JsonProperty("opdate")
    private DZFDate doperatedate;
    @JsonProperty("ybnjf")
    private DZFDouble ybyearjffse;//原币本年借方发生
    @JsonProperty("ybndf")
    private DZFDouble ybyeardffse;//原币本年贷方发生
    @JsonProperty("ybnqc")
    private DZFDouble ybyearqc;//原币本年期初
    @JsonProperty("ybmonthqc")
    private DZFDouble ybthismonthqc;//原币本月期初
    @JsonProperty("rmb")
    private String pk_currency;//币种
    @JsonProperty("kmdir")
    private Integer direct;//科目方向
    @JsonProperty("kmlev")
    private Integer vlevel;//科目层级
    @JsonProperty("kind")
    private Integer accountkind;
    @JsonProperty("excy")
    private String exc_pk_currency;

    private String period;
    private Integer vyear;

    private String isfzhs;

    //辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
    @JsonProperty("fzhs1")
    private String fzhsx1;
    @JsonProperty("fzhs2")
    private String fzhsx2;
    @JsonProperty("fzhs3")
    private String fzhsx3;
    @JsonProperty("fzhs4")
    private String fzhsx4;
    @JsonProperty("fzhs5")
    private String fzhsx5;
    @JsonProperty("fzhs6")
    private String fzhsx6;
    @JsonProperty("fzhs7")
    private String fzhsx7;
    @JsonProperty("fzhs8")
    private String fzhsx8;
    @JsonProperty("fzhs9")
    private String fzhsx9;
    @JsonProperty("fzhs10")
    private String fzhsx10;

    public String getIsfzhs() {
        return isfzhs;
    }

    public void setIsfzhs(String isfzhs) {
        this.isfzhs = isfzhs;
    }

    public String getFzhsx1() {
        return fzhsx1;
    }

    public void setFzhsx1(String fzhsx1) {
        this.fzhsx1 = fzhsx1;
    }

    public String getFzhsx2() {
        return fzhsx2;
    }

    public void setFzhsx2(String fzhsx2) {
        this.fzhsx2 = fzhsx2;
    }

    public String getFzhsx3() {
        return fzhsx3;
    }

    public void setFzhsx3(String fzhsx3) {
        this.fzhsx3 = fzhsx3;
    }

    public String getFzhsx4() {
        return fzhsx4;
    }

    public void setFzhsx4(String fzhsx4) {
        this.fzhsx4 = fzhsx4;
    }

    public String getFzhsx5() {
        return fzhsx5;
    }

    public void setFzhsx5(String fzhsx5) {
        this.fzhsx5 = fzhsx5;
    }

    public String getFzhsx6() {
        return fzhsx6;
    }

    public void setFzhsx6(String fzhsx6) {
        this.fzhsx6 = fzhsx6;
    }

    public String getFzhsx7() {
        return fzhsx7;
    }

    public void setFzhsx7(String fzhsx7) {
        this.fzhsx7 = fzhsx7;
    }

    public String getFzhsx8() {
        return fzhsx8;
    }

    public void setFzhsx8(String fzhsx8) {
        this.fzhsx8 = fzhsx8;
    }

    public String getFzhsx9() {
        return fzhsx9;
    }

    public void setFzhsx9(String fzhsx9) {
        this.fzhsx9 = fzhsx9;
    }

    public String getFzhsx10() {
        return fzhsx10;
    }

    public void setFzhsx10(String fzhsx10) {
        this.fzhsx10 = fzhsx10;
    }

    public Integer getSlwh() {
        return slwh;
    }

    public void setSlwh(Integer slwh) {
        this.slwh = slwh;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getVyear() {
        return vyear;
    }

    public void setVyear(Integer vyear) {
        this.vyear = vyear;
    }


    public DZFDouble getYbyearqc() {
        return ybyearqc;
    }

    public void setYbyearqc(DZFDouble ybyearqc) {
        this.ybyearqc = ybyearqc;
    }

    public DZFDouble getYbthismonthqc() {
        return ybthismonthqc;
    }

    public void setYbthismonthqc(DZFDouble ybthismonthqc) {
        this.ybthismonthqc = ybthismonthqc;
    }

    public DZFDouble getNrate() {
        return nrate;
    }

    public void setNrate(DZFDouble nrate) {
        this.nrate = nrate;
    }

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public DZFBoolean getIsleaf() {
        return isleaf;
    }

    public void setIsleaf(DZFBoolean isleaf) {
        this.isleaf = isleaf;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String newPk_corp) {
        this.pk_corp = newPk_corp;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime newTs) {
        this.ts = newTs;
    }

    public String getVdef9() {
        return vdef9;
    }

    public void setVdef9(String newVdef9) {
        this.vdef9 = newVdef9;
    }

    /**
     * 属性coperatorid的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getCoperatorid() {
        return coperatorid;
    }

    /**
     * 属性coperatorid的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newCoperatorid String
     */
    public void setCoperatorid(String newCoperatorid) {
        this.coperatorid = newCoperatorid;
    }

    /**
     * 属性vdef1的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef1() {
        return vdef1;
    }

    /**
     * 属性vdef1的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef1 String
     */
    public void setVdef1(String newVdef1) {
        this.vdef1 = newVdef1;
    }

    /**
     * 属性vdef8的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef8() {
        return vdef8;
    }

    /**
     * 属性vdef8的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef8 String
     */
    public void setVdef8(String newVdef8) {
        this.vdef8 = newVdef8;
    }

    /**
     * 属性vname的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVname() {
        return vname;
    }

    /**
     * 属性vname的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVname String
     */
    public void setVname(String newVname) {
        this.vname = newVname;
    }

    /**
     * 属性vdef10的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef10() {
        return vdef10;
    }

    /**
     * 属性vdef10的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef10 String
     */
    public void setVdef10(String newVdef10) {
        this.vdef10 = newVdef10;
    }

    /**
     * 属性pk_qcye的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getPk_qcye() {
        return pk_qcye;
    }

    /**
     * 属性pk_qcye的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newPk_qcye String
     */
    public void setPk_qcye(String newPk_qcye) {
        this.pk_qcye = newPk_qcye;
    }

    /**
     * 属性vapproveid的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVapproveid() {
        return vapproveid;
    }

    /**
     * 属性vapproveid的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVapproveid String
     */
    public void setVapproveid(String newVapproveid) {
        this.vapproveid = newVapproveid;
    }

    /**
     * 属性yeardffse的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public DZFDouble getYeardffse() {
        return yeardffse;
    }

    /**
     * 属性yeardffse的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newYeardffse DZFDouble
     */
    public void setYeardffse(DZFDouble newYeardffse) {
        this.yeardffse = newYeardffse;
    }

    /**
     * 属性vapprovenote的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVapprovenote() {
        return vapprovenote;
    }

    /**
     * 属性vapprovenote的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVapprovenote String
     */
    public void setVapprovenote(String newVapprovenote) {
        this.vapprovenote = newVapprovenote;
    }

    /**
     * 属性vcode的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVcode() {
        return vcode;
    }

    /**
     * 属性vcode的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVcode String
     */
    public void setVcode(String newVcode) {
        this.vcode = newVcode;
    }

    /**
     * 属性thismonthqc的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public DZFDouble getThismonthqc() {
        return thismonthqc;
    }

    /**
     * 属性thismonthqc的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newThismonthqc DZFDouble
     */
    public void setThismonthqc(DZFDouble newThismonthqc) {
        this.thismonthqc = newThismonthqc;
    }

    /**
     * 属性vdef7的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef7() {
        return vdef7;
    }

    /**
     * 属性vdef7的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef7 String
     */
    public void setVdef7(String newVdef7) {
        this.vdef7 = newVdef7;
    }

    /**
     * 属性dapprovedate的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDate
     */
    public DZFDate getDapprovedate() {
        return dapprovedate;
    }

    /**
     * 属性dapprovedate的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newDapprovedate DZFDate
     */
    public void setDapprovedate(DZFDate newDapprovedate) {
        this.dapprovedate = newDapprovedate;
    }

    /**
     * 属性yearjffse的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public DZFDouble getYearjffse() {
        return yearjffse;
    }

    /**
     * 属性yearjffse的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newYearjffse DZFDouble
     */
    public void setYearjffse(DZFDouble newYearjffse) {
        this.yearjffse = newYearjffse;
    }

    /**
     * 属性pk_accsubj的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getPk_accsubj() {
        return pk_accsubj;
    }

    /**
     * 属性pk_accsubj的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newPk_accsubj String
     */
    public void setPk_accsubj(String newPk_accsubj) {
        this.pk_accsubj = newPk_accsubj;
    }

    /**
     * 属性vdef2的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef2() {
        return vdef2;
    }

    /**
     * 属性vdef2的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef2 String
     */
    public void setVdef2(String newVdef2) {
        this.vdef2 = newVdef2;
    }

    /**
     * 属性vdef5的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef5() {
        return vdef5;
    }

    /**
     * 属性vdef5的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef5 String
     */
    public void setVdef5(String newVdef5) {
        this.vdef5 = newVdef5;
    }

    /**
     * 属性yearqc的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public DZFDouble getYearqc() {
        return yearqc;
    }

    /**
     * 属性yearqc的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newYearqc DZFDouble
     */
    public void setYearqc(DZFDouble newYearqc) {
        this.yearqc = newYearqc;
    }

    /**
     * 属性pk_billtype的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getPk_billtype() {
        return pk_billtype;
    }

    /**
     * 属性pk_billtype的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newPk_billtype String
     */
    public void setPk_billtype(String newPk_billtype) {
        this.pk_billtype = newPk_billtype;
    }

    /**
     * 属性vbillstatus的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public DZFDouble getVbillstatus() {
        return vbillstatus;
    }

    /**
     * 属性vbillstatus的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVbillstatus DZFDouble
     */
    public void setVbillstatus(DZFDouble newVbillstatus) {
        this.vbillstatus = newVbillstatus;
    }

    /**
     * 属性memo的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 属性memo的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newMemo String
     */
    public void setMemo(String newMemo) {
        this.memo = newMemo;
    }

    /**
     * 属性vdef3的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef3() {
        return vdef3;
    }

    /**
     * 属性vdef3的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef3 String
     */
    public void setVdef3(String newVdef3) {
        this.vdef3 = newVdef3;
    }

    /**
     * 属性vdef6的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVdef6() {
        return vdef6;
    }

    /**
     * 属性vdef6的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVdef6 String
     */
    public void setVdef6(String newVdef6) {
        this.vdef6 = newVdef6;
    }

    /**
     * 属性vbillno的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return String
     */
    public String getVbillno() {
        return vbillno;
    }

    /**
     * 属性vbillno的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newVbillno String
     */
    public void setVbillno(String newVbillno) {
        this.vbillno = newVbillno;
    }

    /**
     * 属性dr的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDouble
     */
    public Integer getDr() {
        return dr;
    }

    /**
     * 属性dr的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newDr DZFDouble
     */
    public void setDr(Integer newDr) {
        this.dr = newDr;
    }

    /**
     * 属性doperatedate的Getter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @return DZFDate
     */
    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    /**
     * 属性doperatedate的Setter方法.
     * 创建日期:2014-09-19 01:06:23
     *
     * @param newDoperatedate DZFDate
     */
    public void setDoperatedate(DZFDate newDoperatedate) {
        this.doperatedate = newDoperatedate;
    }

    public String getDcurrency() {
        return dcurrency;
    }

    public void setDcurrency(String newDcurrency) {
        this.dcurrency = newDcurrency;
    }

    public String getVdef4() {
        return vdef4;
    }

    public void setVdef4(String newVdef4) {
        this.vdef4 = newVdef4;
    }

    public Integer getDirect() {
        return direct;
    }

    public void setDirect(Integer direct) {
        this.direct = direct;
    }

    public Integer getVlevel() {
        return vlevel;
    }

    public void setVlevel(Integer vlevel) {
        this.vlevel = vlevel;
    }

    public DZFDouble getYbyearjffse() {
        return ybyearjffse;
    }

    public void setYbyearjffse(DZFDouble ybyearjffse) {
        this.ybyearjffse = ybyearjffse;
    }

    public DZFDouble getYbyeardffse() {
        return ybyeardffse;
    }

    public void setYbyeardffse(DZFDouble ybyeardffse) {
        this.ybyeardffse = ybyeardffse;
    }

    public String getParentPKFieldName() {
        return "pk_qcye";
    }

    public String getPKFieldName() {
        return "pk_qcye";
    }

    public Integer getAccountkind() {
        return accountkind;
    }

    public void setAccountkind(Integer accountkind) {
        this.accountkind = accountkind;
    }

    public String getExc_pk_currency() {
        return exc_pk_currency;
    }

    public void setExc_pk_currency(String exc_pk_currency) {
        this.exc_pk_currency = exc_pk_currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 返回表名称
     */
    public String getTableName() {
        return "ynt_fzhsqc";
    }

    public DZFDouble getBnqcnum() {
        return bnqcnum;
    }

    public void setBnqcnum(DZFDouble bnqcnum) {
        this.bnqcnum = bnqcnum;
    }

    public DZFDouble getBnfsnum() {
        return bnfsnum;
    }

    public void setBnfsnum(DZFDouble bnfsnum) {
        this.bnfsnum = bnfsnum;
    }

    public DZFDouble getMonthqmnum() {
        return monthqmnum;
    }

    public void setMonthqmnum(DZFDouble monthqmnum) {
        this.monthqmnum = monthqmnum;
    }

    public DZFBoolean getIsnum() {
        return isnum;
    }

    public void setIsnum(DZFBoolean isnum) {
        this.isnum = isnum;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getJldw() {
        return jldw;
    }

    public void setJldw(String jldw) {
        this.jldw = jldw;
    }

    public DZFDouble getBndffsnum() {
        return bndffsnum;
    }

    public void setBndffsnum(DZFDouble bndffsnum) {
        this.bndffsnum = bndffsnum;
    }
}
