package com.dzf.zxkj.platform.model.bill;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 开票历史
 *
 * @author zhangj
 */
public class BillHistoryVO extends SuperVO {

    public static final String TABLE_NAME = "ynt_app_billhis";
    public static final String PK_FIELD = "pk_app_billhis";

    @JsonProperty("id")
    private String pk_app_billhis;
    @JsonProperty("bill_id")
    private String pk_app_billapply;
    @JsonProperty("czr")
    private String voperatetor;
    @JsonProperty("czrq")
    private DZFDateTime dopedate;
    @JsonProperty("cznr")
    private String vopecontent;
    private DZFDateTime ts;
    private Integer dr;
    @JsonProperty("corp")
    private String pk_corp;
    @JsonProperty("tcorp")
    private String pk_temp_corp;

    public String getPk_app_billhis() {
        return pk_app_billhis;
    }

    public void setPk_app_billhis(String pk_app_billhis) {
        this.pk_app_billhis = pk_app_billhis;
    }

    public String getPk_app_billapply() {
        return pk_app_billapply;
    }

    public void setPk_app_billapply(String pk_app_billapply) {
        this.pk_app_billapply = pk_app_billapply;
    }

    public String getVoperatetor() {
        return voperatetor;
    }

    public void setVoperatetor(String voperatetor) {
        this.voperatetor = voperatetor;
    }

    public DZFDateTime getDopedate() {
        return dopedate;
    }

    public void setDopedate(DZFDateTime dopedate) {
        this.dopedate = dopedate;
    }

    public String getVopecontent() {
        return vopecontent;
    }

    public void setVopecontent(String vopecontent) {
        this.vopecontent = vopecontent;
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
