package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 *
 * 凭证打印设置-辅助核算
 */
public class VoucherPrintAssitSetVO extends SuperVO {
    private String pk_print_assist_set;
    private String pk_corp;
    private String pk_accsubj;
    private Boolean printfzhs1;
    private Boolean printfzhs2;
    private Boolean printfzhs3;
    private Boolean printfzhs4;
    private Boolean printfzhs5;
    private Boolean printfzhs6;
    private Boolean printfzhs7;
    private Boolean printfzhs8;
    private Boolean printfzhs9;
    private Boolean printfzhs10;

    private String coperatorid;
    private Integer dr;
    private DZFDateTime ts;

    public String getPk_print_assist_set() {
        return pk_print_assist_set;
    }

    public void setPk_print_assist_set(String pk_print_assist_set) {
        this.pk_print_assist_set = pk_print_assist_set;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public Boolean getPrintfzhs1() {
        return printfzhs1;
    }

    public void setPrintfzhs1(Boolean printfzhs1) {
        this.printfzhs1 = printfzhs1;
    }

    public Boolean getPrintfzhs2() {
        return printfzhs2;
    }

    public void setPrintfzhs2(Boolean printfzhs2) {
        this.printfzhs2 = printfzhs2;
    }

    public Boolean getPrintfzhs3() {
        return printfzhs3;
    }

    public void setPrintfzhs3(Boolean printfzhs3) {
        this.printfzhs3 = printfzhs3;
    }

    public Boolean getPrintfzhs4() {
        return printfzhs4;
    }

    public void setPrintfzhs4(Boolean printfzhs4) {
        this.printfzhs4 = printfzhs4;
    }

    public Boolean getPrintfzhs5() {
        return printfzhs5;
    }

    public void setPrintfzhs5(Boolean printfzhs5) {
        this.printfzhs5 = printfzhs5;
    }

    public Boolean getPrintfzhs6() {
        return printfzhs6;
    }

    public void setPrintfzhs6(Boolean printfzhs6) {
        this.printfzhs6 = printfzhs6;
    }

    public Boolean getPrintfzhs7() {
        return printfzhs7;
    }

    public void setPrintfzhs7(Boolean printfzhs7) {
        this.printfzhs7 = printfzhs7;
    }

    public Boolean getPrintfzhs8() {
        return printfzhs8;
    }

    public void setPrintfzhs8(Boolean printfzhs8) {
        this.printfzhs8 = printfzhs8;
    }

    public Boolean getPrintfzhs9() {
        return printfzhs9;
    }

    public void setPrintfzhs9(Boolean printfzhs9) {
        this.printfzhs9 = printfzhs9;
    }

    public Boolean getPrintfzhs10() {
        return printfzhs10;
    }

    public void setPrintfzhs10(Boolean printfzhs10) {
        this.printfzhs10 = printfzhs10;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_print_assist_set";
    }

    @Override
    public String getTableName() {
        return "ynt_settings_assistprint";
    }
}
