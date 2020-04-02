package com.dzf.zxkj.platform.model.batchprint;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 批量打印设置（BatchPrintSetVo 是具体的任务）
 */
public class BatchPrintFileSetVo extends SuperVO {

    public static final String TABLE_NAME = "ynt_batch_print_file_set";

    public static final String PK_FIELD = "pk_batch_print_file_set";

    @JsonProperty("id")
    private String pk_batch_print_file_set;// 主键
    @JsonProperty("cid")
    private String pk_corp;// 公司
    private String cname;//公司名称
    private DZFDateTime ts;//
    private Integer dr;//
    @JsonProperty("memo")
    private String vmemo;//备注
    @JsonProperty("dynr")
    private String vprintname;// 打印名称
    @JsonProperty("mbxz")
    private String vmobelsel;// 模板选择 A4,B5,A5
    @JsonProperty("ylfx")
    private String reviewdir;// 预览方向 横向，纵向
    @JsonProperty("kmfy")
    private String kmpage;// 科目分页
    @JsonProperty("ztdx")
    private DZFDouble vfontsize;// 字体大小
    @JsonProperty("dyrq")
    private DZFDate dprintdate;// 打印日期
    @JsonProperty("left")
    private DZFDouble dleftmargin;// 左边距
    @JsonProperty("top")
    private DZFDouble dtopmargin;// 上边距


    public String getVmobelsel() {
        return vmobelsel;
    }

    public void setVmobelsel(String vmobelsel) {
        this.vmobelsel = vmobelsel;
    }

    public String getReviewdir() {
        return reviewdir;
    }

    public void setReviewdir(String reviewdir) {
        this.reviewdir = reviewdir;
    }

    public String getKmpage() {
        return kmpage;
    }

    public void setKmpage(String kmpage) {
        this.kmpage = kmpage;
    }

    public String getVprintname() {
        return vprintname;
    }

    public void setVprintname(String vprintname) {
        this.vprintname = vprintname;
    }


    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
    }

    public String getPk_batch_print_file_set() {
        return pk_batch_print_file_set;
    }

    public void setPk_batch_print_file_set(String pk_batch_print_file_set) {
        this.pk_batch_print_file_set = pk_batch_print_file_set;
    }

    public DZFDouble getVfontsize() {
        return vfontsize;
    }

    public void setVfontsize(DZFDouble vfontsize) {
        this.vfontsize = vfontsize;
    }

    public DZFDate getDprintdate() {
        return dprintdate;
    }

    public void setDprintdate(DZFDate dprintdate) {
        this.dprintdate = dprintdate;
    }

    public DZFDouble getDleftmargin() {
        return dleftmargin;
    }

    public void setDleftmargin(DZFDouble dleftmargin) {
        this.dleftmargin = dleftmargin;
    }

    public DZFDouble getDtopmargin() {
        return dtopmargin;
    }

    public void setDtopmargin(DZFDouble dtopmargin) {
        this.dtopmargin = dtopmargin;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }


    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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
