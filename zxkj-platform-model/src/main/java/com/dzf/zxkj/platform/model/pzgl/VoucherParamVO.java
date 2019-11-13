package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.sys.UserVO;

/**
 * 凭证查询条件VO
 */
public class VoucherParamVO extends SuperVO {
    // 公司
    private String pk_corp;

    private String pk_tzpz_h;

    private UserVO uservo;//用户VO

    private DZFDate clientdate;//客户端日期

    private DZFDate begindate;// 开始日期

    private DZFDate enddate;// 结束日期
    private String pzh;                //凭证号-开始
    private String pzh2;            //凭证号-结束
    private DZFDouble mny1;    //最小金额
    private DZFDouble mny2;    //最大金额
    private Integer pz_status;    //凭证状态
    private String zy;                    //摘要
    private String vname; //科目
    private String vcode;
    private String beginPeriod;
    private String endPeriod;
    /**
     * 查询日期类型, 日期1/期间2
     */
    private String dateType;
    private DZFDate zdrq;
    private String fzcode;//辅助核算类别编码
    private String fzhslb;//辅助核算类别主键
    private String fzhsxm;//辅助核算明细主键

    private String sourcebilltype; //凭证来源

    private DZFBoolean cnqz;//出纳签字节点
    private String cn_status;//出纳签字状态

    private Integer iautorecognize; //识别状态
    // 现金流量有误
    private Boolean is_error_cash;
    // 申报表税项有误凭证
    private Boolean is_error_tax;

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_tzpz_h() {
        return pk_tzpz_h;
    }

    public void setPk_tzpz_h(String pk_tzpz_h) {
        this.pk_tzpz_h = pk_tzpz_h;
    }

    public UserVO getUservo() {
        return uservo;
    }

    public void setUservo(UserVO uservo) {
        this.uservo = uservo;
    }

    public DZFDate getClientdate() {
        return clientdate;
    }

    public void setClientdate(DZFDate clientdate) {
        this.clientdate = clientdate;
    }

    public DZFDate getBegindate() {
        return begindate;
    }

    public void setBegindate(DZFDate begindate) {
        this.begindate = begindate;
    }

    public DZFDate getEnddate() {
        return enddate;
    }

    public void setEnddate(DZFDate enddate) {
        this.enddate = enddate;
    }

    public String getPzh() {
        return pzh;
    }

    public void setPzh(String pzh) {
        this.pzh = pzh;
    }

    public String getPzh2() {
        return pzh2;
    }

    public void setPzh2(String pzh2) {
        this.pzh2 = pzh2;
    }

    public DZFDouble getMny1() {
        return mny1;
    }

    public void setMny1(DZFDouble mny1) {
        this.mny1 = mny1;
    }

    public DZFDouble getMny2() {
        return mny2;
    }

    public void setMny2(DZFDouble mny2) {
        this.mny2 = mny2;
    }

    public Integer getPz_status() {
        return pz_status;
    }

    public void setPz_status(Integer pz_status) {
        this.pz_status = pz_status;
    }

    public String getZy() {
        return zy;
    }

    public void setZy(String zy) {
        this.zy = zy;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getBeginPeriod() {
        return beginPeriod;
    }

    public void setBeginPeriod(String beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public DZFDate getZdrq() {
        return zdrq;
    }

    public void setZdrq(DZFDate zdrq) {
        this.zdrq = zdrq;
    }

    public String getFzcode() {
        return fzcode;
    }

    public void setFzcode(String fzcode) {
        this.fzcode = fzcode;
    }

    public String getFzhslb() {
        return fzhslb;
    }

    public void setFzhslb(String fzhslb) {
        this.fzhslb = fzhslb;
    }

    public String getFzhsxm() {
        return fzhsxm;
    }

    public void setFzhsxm(String fzhsxm) {
        this.fzhsxm = fzhsxm;
    }

    public String getSourcebilltype() {
        return sourcebilltype;
    }

    public void setSourcebilltype(String sourcebilltype) {
        this.sourcebilltype = sourcebilltype;
    }

    public DZFBoolean getCnqz() {
        return cnqz;
    }

    public void setCnqz(DZFBoolean cnqz) {
        this.cnqz = cnqz;
    }

    public String getCn_status() {
        return cn_status;
    }

    public void setCn_status(String cn_status) {
        this.cn_status = cn_status;
    }

    public Integer getIautorecognize() {
        return iautorecognize;
    }

    public void setIautorecognize(Integer iautorecognize) {
        this.iautorecognize = iautorecognize;
    }

    public Boolean getIs_error_cash() {
        return is_error_cash;
    }

    public void setIs_error_cash(Boolean is_error_cash) {
        this.is_error_cash = is_error_cash;
    }

    public Boolean getIs_error_tax() {
        return is_error_tax;
    }

    public void setIs_error_tax(Boolean is_error_tax) {
        this.is_error_tax = is_error_tax;
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
