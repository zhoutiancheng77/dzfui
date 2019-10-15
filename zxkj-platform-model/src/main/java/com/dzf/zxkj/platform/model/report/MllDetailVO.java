package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class MllDetailVO extends SuperVO {

    private String fzid;
    private String code;  //存货编码
    private String name;  //存货名称
    private String spec;  //规格型号
    private String unit;  //核算单位
    private String vcode;
    private String vid;
    private String vname; //存货类别名称
    private DZFDouble cksl;   //出库数量
    private DZFDouble ckdj;  //出库单价
    private DZFDouble xsdj; //销售单价
    private DZFDouble mll;

    //打印相关
    private String titlePeriod;
    private String gs;
    private String isPaging;
    private String pk_corp;

    public String getFzid() {
        return fzid;
    }

    public void setFzid(String fzid) {
        this.fzid = fzid;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public DZFDouble getCksl() {
        return cksl;
    }

    public void setCksl(DZFDouble cksl) {
        this.cksl = cksl;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }


    public DZFDouble getCkdj() {
        return ckdj;
    }

    public void setCkdj(DZFDouble ckdj) {
        this.ckdj = ckdj;
    }

    public DZFDouble getXsdj() {
        return xsdj;
    }

    public void setXsdj(DZFDouble xsdj) {
        this.xsdj = xsdj;
    }

    public DZFDouble getMll() {
        return mll;
    }

    public void setMll(DZFDouble mll) {
        this.mll = mll;
    }

    public String getTitlePeriod() {
        return titlePeriod;
    }

    public void setTitlePeriod(String titlePeriod) {
        this.titlePeriod = titlePeriod;
    }

    public String getGs() {
        return gs;
    }

    public void setGs(String gs) {
        this.gs = gs;
    }

    public String getIsPaging() {
        return isPaging;
    }

    public void setIsPaging(String isPaging) {
        this.isPaging = isPaging;
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
