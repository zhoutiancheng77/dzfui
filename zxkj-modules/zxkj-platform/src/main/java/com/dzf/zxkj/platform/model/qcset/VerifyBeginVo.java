package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyBeginVo extends SuperVO {

    @JsonProperty("id")
    private String pk_verify_qc;

    //科目id
    private String pk_accsubj;

    @JsonProperty("kmcode")
    private String vcode;
    @JsonProperty("kmname")
    private String vname;

    //业务发生日期
    @JsonProperty("date")
    private DZFDate occur_date;

    private String pk_corp;
    //未核销金额
    @JsonProperty("mny")
    private DZFDouble verify_mny;

    //辅助核算项
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

    private DZFDateTime ts;

    private Integer dr;


    private String isfzhs;

    public String getPk_verify_qc() {
        return pk_verify_qc;
    }

    public void setPk_verify_qc(String pk_verify_qc) {
        this.pk_verify_qc = pk_verify_qc;
    }

    public String getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public DZFDate getOccur_date() {
        return occur_date;
    }

    public void setOccur_date(DZFDate occur_date) {
        this.occur_date = occur_date;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public DZFDouble getVerify_mny() {
        return verify_mny;
    }

    public void setVerify_mny(DZFDouble verify_mny) {
        this.verify_mny = verify_mny;
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

    public String getIsfzhs() {
        return isfzhs;
    }

    public void setIsfzhs(String isfzhs) {
        this.isfzhs = isfzhs;
    }

    @Override
    public String getPKFieldName() {
        return "pk_verify_qc";
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_verify_begin";
    }

}
