package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 税收计算-增值税-税费计算
 *
 * @author lbj
 */
public class AddValueTaxCalVO extends SuperVO {
    // 主键
    private String pk_tax;
    // 期间
    private String period;
    // 公司
    private String pk_corp;
    // 操作人
    private String coperatorid;
    private DZFDateTime ts;
    private Integer dr;
    // 是否根据本数据结转
    private Boolean carryover;
    private String pk_voucher;
    private DZFDateTime modifyDate;
    // 上期留抵税额
    private DZFDouble sqld;
    // 进项税额转出
    private DZFDouble jxszc;
    // 加计抵减本期实际抵减额
    private DZFDouble jjdj;
    // 出口退税
    private DZFDouble ckts;
    // 转出多交增值税
    private DZFDouble zcdjzzs;

    // 应纳税额
    private DZFDouble ynse;

    // 税控设备及维修费
    private DZFDouble sksb;
    // 本期已缴税额
    private DZFDouble yjse;
    // 出口抵减内销产品应纳税额
    private DZFDouble ckdj;
    // 销项税额抵减
    private DZFDouble xxdj;
    // 期末留抵税额
    private DZFDouble qmld;
    // 本期应补（退）税额
    private DZFDouble ybtse;

    // 小规模
    // 本期应纳税额
    private DZFDouble bqynse;
    // 本期免税额
    private DZFDouble bqmse;

    public String getPk_tax() {
        return pk_tax;
    }

    public void setPk_tax(String pk_tax) {
        this.pk_tax = pk_tax;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
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

    public Boolean getCarryover() {
        return carryover;
    }

    public void setCarryover(Boolean carryover) {
        this.carryover = carryover;
    }

    public String getPk_voucher() {
        return pk_voucher;
    }

    public void setPk_voucher(String pk_voucher) {
        this.pk_voucher = pk_voucher;
    }

    public DZFDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(DZFDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public DZFDouble getSqld() {
        return sqld;
    }

    public void setSqld(DZFDouble sqld) {
        this.sqld = sqld;
    }

    public DZFDouble getJxszc() {
        return jxszc;
    }

    public void setJxszc(DZFDouble jxszc) {
        this.jxszc = jxszc;
    }

    public DZFDouble getJjdj() {
        return jjdj;
    }

    public void setJjdj(DZFDouble jjdj) {
        this.jjdj = jjdj;
    }

    public DZFDouble getCkts() {
        return ckts;
    }

    public void setCkts(DZFDouble ckts) {
        this.ckts = ckts;
    }

    public DZFDouble getZcdjzzs() {
        return zcdjzzs;
    }

    public void setZcdjzzs(DZFDouble zcdjzzs) {
        this.zcdjzzs = zcdjzzs;
    }

    public DZFDouble getYnse() {
        return ynse;
    }

    public void setYnse(DZFDouble ynse) {
        this.ynse = ynse;
    }

    public DZFDouble getSksb() {
        return sksb;
    }

    public void setSksb(DZFDouble sksb) {
        this.sksb = sksb;
    }

    public DZFDouble getYjse() {
        return yjse;
    }

    public void setYjse(DZFDouble yjse) {
        this.yjse = yjse;
    }

    public DZFDouble getCkdj() {
        return ckdj;
    }

    public void setCkdj(DZFDouble ckdj) {
        this.ckdj = ckdj;
    }

    public DZFDouble getXxdj() {
        return xxdj;
    }

    public void setXxdj(DZFDouble xxdj) {
        this.xxdj = xxdj;
    }

    public DZFDouble getQmld() {
        return qmld;
    }

    public void setQmld(DZFDouble qmld) {
        this.qmld = qmld;
    }

    public DZFDouble getYbtse() {
        return ybtse;
    }

    public void setYbtse(DZFDouble ybtse) {
        this.ybtse = ybtse;
    }

    public DZFDouble getBqynse() {
        return bqynse;
    }

    public void setBqynse(DZFDouble bqynse) {
        this.bqynse = bqynse;
    }

    public DZFDouble getBqmse() {
        return bqmse;
    }

    public void setBqmse(DZFDouble bqmse) {
        this.bqmse = bqmse;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_tax";
    }

    @Override
    public String getTableName() {
        return "ynt_taxcal_addtax_info";
    }
}
