package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/02
 * @Modified By:
 */
public class QmgyjzVOs  extends SuperVO {

    private DZFDouble z_f_cailiao_qc = DZFDouble.ZERO_DBL;//第1行--材料期初
    private DZFDouble z_f_rengong_qc = DZFDouble.ZERO_DBL;//第1行--人工期初
    private DZFDouble z_f_zzfy_qc = DZFDouble.ZERO_DBL;//第1行--制造费用期初
    private DZFDouble z_f_cailiao = DZFDouble.ZERO_DBL;//第1行-材料发生
    private DZFDouble z_f_rengong = DZFDouble.ZERO_DBL;//第1行-人工发生
    private DZFDouble z_f_zzfy = DZFDouble.ZERO_DBL;//第1行-制造费用发生
    //
    private DZFDouble f_cailiao_qc = DZFDouble.ZERO_DBL;//第2行以后--[比例]----材料----期初
    private DZFDouble f_rengong_qc = DZFDouble.ZERO_DBL;
    private DZFDouble f_zzfy_qc = DZFDouble.ZERO_DBL;
    private DZFDouble f_cailiao_bl = DZFDouble.ZERO_DBL;//第2行以后--[比例]---材料----发生
    private DZFDouble f_rengong_bl = DZFDouble.ZERO_DBL;
    private DZFDouble f_zzfy_bl = DZFDouble.ZERO_DBL;
    //
    private DZFDouble cailiao = DZFDouble.ZERO_DBL;//第2行以后----[金额] ---材料---期初
    private DZFDouble rengong = DZFDouble.ZERO_DBL;
    private DZFDouble zzfy = DZFDouble.ZERO_DBL;
    private DZFDouble f_f_cailiao = DZFDouble.ZERO_DBL;//第2行以后---[金额]----材料---发生
    private DZFDouble f_f_rengong = DZFDouble.ZERO_DBL;
    private DZFDouble f_f_zzfy = DZFDouble.ZERO_DBL;

    public DZFDouble getZ_f_cailiao_qc() {
        return z_f_cailiao_qc;
    }

    public void setZ_f_cailiao_qc(DZFDouble z_f_cailiao_qc) {
        this.z_f_cailiao_qc = z_f_cailiao_qc;
    }

    public DZFDouble getZ_f_rengong_qc() {
        return z_f_rengong_qc;
    }

    public void setZ_f_rengong_qc(DZFDouble z_f_rengong_qc) {
        this.z_f_rengong_qc = z_f_rengong_qc;
    }

    public DZFDouble getZ_f_zzfy_qc() {
        return z_f_zzfy_qc;
    }

    public void setZ_f_zzfy_qc(DZFDouble z_f_zzfy_qc) {
        this.z_f_zzfy_qc = z_f_zzfy_qc;
    }

    public DZFDouble getZ_f_cailiao() {
        return z_f_cailiao;
    }

    public void setZ_f_cailiao(DZFDouble z_f_cailiao) {
        this.z_f_cailiao = z_f_cailiao;
    }

    public DZFDouble getZ_f_rengong() {
        return z_f_rengong;
    }

    public void setZ_f_rengong(DZFDouble z_f_rengong) {
        this.z_f_rengong = z_f_rengong;
    }

    public DZFDouble getZ_f_zzfy() {
        return z_f_zzfy;
    }

    public void setZ_f_zzfy(DZFDouble z_f_zzfy) {
        this.z_f_zzfy = z_f_zzfy;
    }

    public DZFDouble getF_cailiao_qc() {
        return f_cailiao_qc;
    }

    public void setF_cailiao_qc(DZFDouble f_cailiao_qc) {
        this.f_cailiao_qc = f_cailiao_qc;
    }

    public DZFDouble getF_rengong_qc() {
        return f_rengong_qc;
    }

    public void setF_rengong_qc(DZFDouble f_rengong_qc) {
        this.f_rengong_qc = f_rengong_qc;
    }

    public DZFDouble getF_zzfy_qc() {
        return f_zzfy_qc;
    }

    public void setF_zzfy_qc(DZFDouble f_zzfy_qc) {
        this.f_zzfy_qc = f_zzfy_qc;
    }

    public DZFDouble getF_cailiao_bl() {
        return f_cailiao_bl;
    }

    public void setF_cailiao_bl(DZFDouble f_cailiao_bl) {
        this.f_cailiao_bl = f_cailiao_bl;
    }

    public DZFDouble getF_rengong_bl() {
        return f_rengong_bl;
    }

    public void setF_rengong_bl(DZFDouble f_rengong_bl) {
        this.f_rengong_bl = f_rengong_bl;
    }

    public DZFDouble getF_zzfy_bl() {
        return f_zzfy_bl;
    }

    public void setF_zzfy_bl(DZFDouble f_zzfy_bl) {
        this.f_zzfy_bl = f_zzfy_bl;
    }

    public DZFDouble getCailiao() {
        return cailiao;
    }

    public void setCailiao(DZFDouble cailiao) {
        this.cailiao = cailiao;
    }

    public DZFDouble getRengong() {
        return rengong;
    }

    public void setRengong(DZFDouble rengong) {
        this.rengong = rengong;
    }

    public DZFDouble getZzfy() {
        return zzfy;
    }

    public void setZzfy(DZFDouble zzfy) {
        this.zzfy = zzfy;
    }

    public DZFDouble getF_f_cailiao() {
        return f_f_cailiao;
    }

    public void setF_f_cailiao(DZFDouble f_f_cailiao) {
        this.f_f_cailiao = f_f_cailiao;
    }

    public DZFDouble getF_f_rengong() {
        return f_f_rengong;
    }

    public void setF_f_rengong(DZFDouble f_f_rengong) {
        this.f_f_rengong = f_f_rengong;
    }

    public DZFDouble getF_f_zzfy() {
        return f_f_zzfy;
    }

    public void setF_f_zzfy(DZFDouble f_f_zzfy) {
        this.f_f_zzfy = f_f_zzfy;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

}