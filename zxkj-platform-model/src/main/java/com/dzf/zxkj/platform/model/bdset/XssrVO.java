package com.dzf.zxkj.platform.model.bdset;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

/****
 * 销售收入
 * @author asoka
 *
 */
@SuppressWarnings("rawtypes")
public class XssrVO extends SuperVO {


    private static final long serialVersionUID = 1L;


    private String pk_xssrtemplate;

    //应收或现金科目
    private String ysxjkmmc;
    private String ysxjkm_id;
    private String ysxjkm_code;

    //收入类
    private String srlkmmc;
    private String srlkm_id;
    private String srlkm_code;


    //应交税费
    private String yjsfkmmc;
    private String yjsfkm_id;
    private String yjsfkm_code;


    private Integer dr;
    private String pk_corp;
    private String memo;
    private DZFDateTime ts;


    public String getPk_xssrtemplate() {
        return pk_xssrtemplate;
    }

    public void setPk_xssrtemplate(String pk_xssrtemplate) {
        this.pk_xssrtemplate = pk_xssrtemplate;
    }

    public String getYsxjkmmc() {
        return ysxjkmmc;
    }

    public void setYsxjkmmc(String ysxjkmmc) {
        this.ysxjkmmc = ysxjkmmc;
    }

    public String getYsxjkm_id() {
        return ysxjkm_id;
    }

    public void setYsxjkm_id(String ysxjkm_id) {
        this.ysxjkm_id = ysxjkm_id;
    }

    public String getSrlkmmc() {
        return srlkmmc;
    }

    public void setSrlkmmc(String srlkmmc) {
        this.srlkmmc = srlkmmc;
    }

    public String getSrlkm_id() {
        return srlkm_id;
    }

    public void setSrlkm_id(String srlkm_id) {
        this.srlkm_id = srlkm_id;
    }

    public String getYjsfkmmc() {
        return yjsfkmmc;
    }

    public void setYjsfkmmc(String yjsfkmmc) {
        this.yjsfkmmc = yjsfkmmc;
    }

    public String getYjsfkm_id() {
        return yjsfkm_id;
    }

    public void setYjsfkm_id(String yjsfkm_id) {
        this.yjsfkm_id = yjsfkm_id;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_xssrtemplate";
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "YNT_XSSR";
    }

    public String getYsxjkm_code() {
        return ysxjkm_code;
    }

    public void setYsxjkm_code(String ysxjkm_code) {
        this.ysxjkm_code = ysxjkm_code;
    }

    public String getSrlkm_code() {
        return srlkm_code;
    }

    public void setSrlkm_code(String srlkm_code) {
        this.srlkm_code = srlkm_code;
    }

    public String getYjsfkm_code() {
        return yjsfkm_code;
    }

    public void setYjsfkm_code(String yjsfkm_code) {
        this.yjsfkm_code = yjsfkm_code;
    }


}
