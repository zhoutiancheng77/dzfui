package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 暂估数据
 */
public class TempInvtoryVO extends SuperVO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("invid")
    private String pk_invtory;
    @JsonProperty("newnum")
    private DZFDouble nnumber;
    @JsonProperty("oldnum")
    private DZFDouble nnumber_old;// 原始数量
    @JsonProperty("price")
    private DZFDouble nprice;
    @JsonProperty("mny")
    private DZFDouble nmny;
    @JsonProperty("invname")
    private String invname;// 存货名称
    @JsonProperty("kmname")
    private String kmname;// 科目名称
    @JsonProperty("kmbm")
    private String kmbm;// 科目编码
    @JsonProperty("kmid")
    private String kmid;// 科目id
    private String fzhs;// 辅助核算
    @JsonProperty("fzid")
    private String fzid;// 存货辅助
    private String isnum;

    //
    private String spfl;//存货类别
    private String spbm;//存货编码
    private String spgg;//规格型号
    private String jldw;//计量单位

    private String pk_gs;//公司主键
    private String gsname;//公司名称
    private String period;//期间

    public String getPk_gs() {
        return pk_gs;
    }

    public String getGsname() {
        return gsname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPk_gs(String pk_gs) {
        this.pk_gs = pk_gs;
    }

    public void setGsname(String gsname) {
        this.gsname = gsname;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSpfl() {
        return spfl;
    }

    public String getSpbm() {
        return spbm;
    }

    public String getSpgg() {
        return spgg;
    }

    public String getJldw() {
        return jldw;
    }

    public void setSpfl(String spfl) {
        this.spfl = spfl;
    }

    public void setSpbm(String spbm) {
        this.spbm = spbm;
    }

    public void setSpgg(String spgg) {
        this.spgg = spgg;
    }

    public void setJldw(String jldw) {
        this.jldw = jldw;
    }

    public String getPk_invtory() {
        return pk_invtory;
    }

    public void setPk_invtory(String pk_invtory) {
        this.pk_invtory = pk_invtory;
    }

    public DZFDouble getNnumber() {
        return nnumber;
    }

    public void setNnumber(DZFDouble nnumber) {
        this.nnumber = nnumber;
    }

    public DZFDouble getNmny() {
        return nmny;
    }

    public void setNmny(DZFDouble nmny) {
        this.nmny = nmny;
    }

    public DZFDouble getNprice() {
        return nprice;
    }

    public void setNprice(DZFDouble nprice) {
        this.nprice = nprice;
    }

    public DZFDouble getNnumber_old() {
        return nnumber_old;
    }

    public void setNnumber_old(DZFDouble nnumber_old) {
        this.nnumber_old = nnumber_old;
    }

    public String getInvname() {
        return invname;
    }

    public void setInvname(String invname) {
        this.invname = invname;
    }

    public String getKmname() {
        return kmname;
    }

    public void setKmname(String kmname) {
        this.kmname = kmname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKmbm() {
        return kmbm;
    }

    public void setKmbm(String kmbm) {
        this.kmbm = kmbm;
    }

    public String getKmid() {
        return kmid;
    }

    public void setKmid(String kmid) {
        this.kmid = kmid;
    }

    public String getFzhs() {
        return fzhs;
    }

    public void setFzhs(String fzhs) {
        this.fzhs = fzhs;
    }

    public String getIsnum() {
        return isnum;
    }

    public void setIsnum(String isnum) {
        this.isnum = isnum;
    }

    public String getFzid() {
        return fzid;
    }

    public void setFzid(String fzid) {
        this.fzid = fzid;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

}
