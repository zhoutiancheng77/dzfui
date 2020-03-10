package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
// 工资表概况
public class SalaryTotalVO extends SuperVO {

    private static final long serialVersionUID = 3493349094603750955L;

    private String billtype;// 工资表类型
    private String qj;//期间
    private int personcount;//人数
    private DZFDouble nlaborcost;// 人力成本总额
    private DZFDouble nempincome;// 员工收入总额
    private DZFDouble npaidtax;// 实缴税额合计
    private String pzh;// 凭证号
    private String lasttime;//更新时间
    private String pk_corp;

    public String getBilltype() {
        return billtype;
    }

    public void setBilltype(String billtype) {
        this.billtype = billtype;
    }

    public String getQj() {
        return qj;
    }

    public void setQj(String qj) {
        this.qj = qj;
    }

    public int getPersoncount() {
        return personcount;
    }

    public void setPersoncount(int personcount) {
        this.personcount = personcount;
    }

    public DZFDouble getNlaborcost() {
        return nlaborcost;
    }

    public void setNlaborcost(DZFDouble nlaborcost) {
        this.nlaborcost = nlaborcost;
    }

    public DZFDouble getNempincome() {
        return nempincome;
    }

    public void setNempincome(DZFDouble nempincome) {
        this.nempincome = nempincome;
    }

    public DZFDouble getNpaidtax() {
        return npaidtax;
    }

    public void setNpaidtax(DZFDouble npaidtax) {
        this.npaidtax = npaidtax;
    }

    public String getPzh() {
        return pzh;
    }

    public void setPzh(String pzh) {
        this.pzh = pzh;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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
