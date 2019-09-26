
package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

public class KmMxZVO extends SuperVO {
    private String pk_tzpz_h;
    private String otsubject;
    private Integer sx;
    public String titlePeriod;
    public String gs;
    public String rq;
    public String pzh;
    public String zy;
    public DZFDouble jf;
    public DZFDouble df;
    public String fx;
    private Integer level;
    private String day;
    public DZFDouble ye;
    public String kmbm;
    private String pk_accsubj;
    public String isPaging;
    public String km;
    private String bz;
    public String pk_corp;
    public String pzpk;
    private String fzhsx1;
    private String fzhsx2;
    private String fzhsx3;
    private String fzhsx4;
    private String fzhsx5;
    private String fzhsx6;
    private String fzhsx7;
    private String fzhsx8;
    private String fzhsx9;
    private String fzhsx10;
    public DZFDouble ybye;
    public DZFDouble ybjf;
    public DZFDouble ybdf;
    public DZFBoolean isleaf;
    private String pk_tzpz_b;
    private String kmfullname;
    private String hl;
    private DZFBoolean bqc;
    private String comparecode;
    private String fzlbcode;
    private DZFBoolean bsyszy;
    private Integer rowno;
    // 币种
    private String pk_currency;
    private String currency;

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public KmMxZVO() {
    }

    public Integer getRowno() {
        return this.rowno;
    }

    public void setRowno(Integer rowno) {
        this.rowno = rowno;
    }

    public String getPk_tzpz_b() {
        return this.pk_tzpz_b;
    }

    public void setPk_tzpz_b(String pk_tzpz_b) {
        this.pk_tzpz_b = pk_tzpz_b;
    }

    public String getRq() {
        return this.rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getPzh() {
        return this.pzh;
    }

    public void setPzh(String pzh) {
        this.pzh = pzh;
    }

    public String getZy() {
        return this.zy;
    }

    public void setZy(String zy) {
        this.zy = zy;
    }

    public DZFDouble getJf() {
        return this.jf;
    }

    public void setJf(DZFDouble jf) {
        this.jf = jf;
    }

    public DZFDouble getDf() {
        return this.df;
    }

    public void setDf(DZFDouble df) {
        this.df = df;
    }

    public String getFx() {
        return this.fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public DZFDouble getYe() {
        return this.ye;
    }

    public String getPk_accsubj() {
        return this.pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public void setYe(DZFDouble ye) {
        this.ye = ye;
    }

    public String getKmbm() {
        return this.kmbm;
    }

    public void setKmbm(String kmbm) {
        this.kmbm = kmbm;
    }

    public String getKm() {
        return this.km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getBz() {
        return this.bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getParentPKFieldName() {
        return null;
    }

    public String getPKFieldName() {
        return null;
    }

    public String getTableName() {
        return null;
    }

    public String getTitlePeriod() {
        return this.titlePeriod;
    }

    public void setTitlePeriod(String titlePeriod) {
        this.titlePeriod = titlePeriod;
    }

    public String getGs() {
        return this.gs;
    }

    public void setGs(String gs) {
        this.gs = gs;
    }

    public String getIsPaging() {
        return this.isPaging;
    }

    public void setIsPaging(String isPaging) {
        this.isPaging = isPaging;
    }

    public String getPzpk() {
        return this.pzpk;
    }

    public void setPzpk(String pzpk) {
        this.pzpk = pzpk;
    }

    public String getPk_corp() {
        return this.pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_tzpz_h() {
        return this.pk_tzpz_h;
    }

    public void setPk_tzpz_h(String pk_tzpz_h) {
        this.pk_tzpz_h = pk_tzpz_h;
    }

    public String getOtsubject() {
        return this.otsubject;
    }

    public void setOtsubject(String otsubject) {
        this.otsubject = otsubject;
    }

    public Integer getSx() {
        return this.sx;
    }

    public void setSx(Integer sx) {
        this.sx = sx;
    }

    public String getFzhsx1() {
        return this.fzhsx1;
    }

    public void setFzhsx1(String fzhsx1) {
        this.fzhsx1 = fzhsx1;
    }

    public String getFzhsx2() {
        return this.fzhsx2;
    }

    public void setFzhsx2(String fzhsx2) {
        this.fzhsx2 = fzhsx2;
    }

    public String getFzhsx3() {
        return this.fzhsx3;
    }

    public void setFzhsx3(String fzhsx3) {
        this.fzhsx3 = fzhsx3;
    }

    public String getFzhsx4() {
        return this.fzhsx4;
    }

    public void setFzhsx4(String fzhsx4) {
        this.fzhsx4 = fzhsx4;
    }

    public String getFzhsx5() {
        return this.fzhsx5;
    }

    public void setFzhsx5(String fzhsx5) {
        this.fzhsx5 = fzhsx5;
    }

    public String getFzhsx6() {
        return this.fzhsx6;
    }

    public void setFzhsx6(String fzhsx6) {
        this.fzhsx6 = fzhsx6;
    }

    public String getFzhsx7() {
        return this.fzhsx7;
    }

    public void setFzhsx7(String fzhsx7) {
        this.fzhsx7 = fzhsx7;
    }

    public String getFzhsx8() {
        return this.fzhsx8;
    }

    public void setFzhsx8(String fzhsx8) {
        this.fzhsx8 = fzhsx8;
    }

    public String getFzhsx9() {
        return this.fzhsx9;
    }

    public void setFzhsx9(String fzhsx9) {
        this.fzhsx9 = fzhsx9;
    }

    public String getFzhsx10() {
        return this.fzhsx10;
    }

    public void setFzhsx10(String fzhsx10) {
        this.fzhsx10 = fzhsx10;
    }

    public DZFBoolean getIsleaf() {
        return this.isleaf;
    }

    public void setIsleaf(DZFBoolean isleaf) {
        this.isleaf = isleaf;
    }

    public String getKmfullname() {
        return this.kmfullname;
    }

    public void setKmfullname(String kmfullname) {
        this.kmfullname = kmfullname;
    }

    public DZFDouble getYbye() {
        return this.ybye;
    }

    public void setYbye(DZFDouble ybye) {
        this.ybye = ybye;
    }

    public DZFDouble getYbjf() {
        return this.ybjf;
    }

    public void setYbjf(DZFDouble ybjf) {
        this.ybjf = ybjf;
    }

    public DZFDouble getYbdf() {
        return this.ybdf;
    }

    public void setYbdf(DZFDouble ybdf) {
        this.ybdf = ybdf;
    }

    public String getHl() {
        return this.hl;
    }

    public void setHl(String hl) {
        this.hl = hl;
    }

    public DZFBoolean getBqc() {
        return this.bqc;
    }

    public void setBqc(DZFBoolean bqc) {
        this.bqc = bqc;
    }

    public String getComparecode() {
        return this.comparecode;
    }

    public void setComparecode(String comparecode) {
        this.comparecode = comparecode;
    }

    public String getFzlbcode() {
        return this.fzlbcode;
    }

    public void setFzlbcode(String fzlbcode) {
        this.fzlbcode = fzlbcode;
    }

    public DZFBoolean getBsyszy() {
        return this.bsyszy;
    }

    public void setBsyszy(DZFBoolean bsyszy) {
        this.bsyszy = bsyszy;
    }
}