package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxRptTempletVO extends SuperVO<TaxRptTempletVO> {

    private static final long serialVersionUID = 1L;

    //主键
    private String pk_taxrpttemplet;
    //公司主键
    private String pk_corp;
    //所属地区	例如：北京，上海，江苏
    private String location;
    //征收项目代码	例如：1:增值税
    private String zsxm_dm;
    //申报种类，原有的字段。和sbcode一样
    private String sb_zlbh;//存库
    //申报编码
    private String sbcode;//不存库
    //申报名称
    private String sbname;//不存库
    //申报周期
    private int sbzq = -1;//不存库
    //申报税种id
    private String pk_taxsbzl;//存库
    //treegrid加载状态
    private String state;//不存库
    //报表编码
    private String reportcode;
    //报表名称
    private String reportname;
    //报表文件存放路径
    private String rptcontent;
    //pdf模板文件
    private String pdftemplet;
    //spread模板文件
    private String spreadtemplet;
    private Integer dr;
    private DZFDateTime ts;

    //是否必填 ifrequired
    private DZFBoolean ifrequired;

    //填报顺序
    private Integer orderno;
    ////填报序号-----------------------------------新版优化后，此字段没有用。税种排序通过ynt_tax_sbzl表中的showorder
    private Integer taxorder;

    private boolean isselect = false; //是否选中//不存库

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_taxrpttemplet";
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_taxrpttemplet";
    }

    public String getPk_taxrpttemplet() {
        return pk_taxrpttemplet;
    }

    public void setPk_taxrpttemplet(String pk_taxrpttemplet) {
        this.pk_taxrpttemplet = pk_taxrpttemplet;
    }

    public String getReportcode() {
        return reportcode;
    }

    public void setReportcode(String reportcode) {
        this.reportcode = reportcode;
    }

    public String getReportname() {
        return reportname;
    }

    public void setReportname(String reportname) {
        this.reportname = reportname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getRptcontent() {
        return rptcontent;
    }

    public void setRptcontent(String rptcontent) {
        this.rptcontent = rptcontent;
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

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getZsxm_dm() {
        return zsxm_dm;
    }

    public String getSb_zlbh() {
        return sb_zlbh;
    }

    public String getPdftemplet() {
        return pdftemplet;
    }

    public String getSpreadtemplet() {
        return spreadtemplet;
    }

    public void setZsxm_dm(String zsxm_dm) {
        this.zsxm_dm = zsxm_dm;
    }

    public void setSb_zlbh(String sb_zlbh) {
        this.sb_zlbh = sb_zlbh;
    }

    public void setPdftemplet(String pdftemplet) {
        this.pdftemplet = pdftemplet;
    }

    public void setSpreadtemplet(String spreadtemplet) {
        this.spreadtemplet = spreadtemplet;
    }

    public DZFBoolean getIfrequired() {
        return ifrequired;
    }

    public void setIfrequired(DZFBoolean ifrequired) {
        this.ifrequired = ifrequired;
    }

    public Integer getOrderno() {
        return orderno;
    }

    public void setOrderno(Integer orderno) {
        this.orderno = orderno;
    }

    public Integer getTaxorder() {
        return taxorder;
    }

    public void setTaxorder(Integer taxorder) {
        this.taxorder = taxorder;
    }

    public String getSbname() {
        return sbname;
    }

    public void setSbname(String sbname) {
        this.sbname = sbname;
    }

    public int getSbzq() {
        return sbzq;
    }

    public void setSbzq(int sbzq) {
        this.sbzq = sbzq;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSbcode() {
        return sbcode;
    }

    public void setSbcode(String sbcode) {
        this.sbcode = sbcode;
    }

    public String getPk_taxsbzl() {
        return pk_taxsbzl;
    }

    public void setPk_taxsbzl(String pk_taxsbzl) {
        this.pk_taxsbzl = pk_taxsbzl;
    }

}