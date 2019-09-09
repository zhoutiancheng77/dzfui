package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.custom.type.DZFBoolean;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.custom.type.DZFDateTime;
import com.dzf.zxkj.custom.type.DZFDouble;
import com.dzf.zxkj.platform.model.ICodeName;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 辅助核算子表
 *
 * @author liubj
 */
public class AuxiliaryAccountBVO extends SuperVO implements ICodeName {
    @JsonProperty("id")
    private String pk_auacount_b;
    @JsonProperty("pid")
    private String pk_auacount_h;
    @JsonProperty("gsid")
    private String pk_corp;
    private String code;
    private String name;
    // 规格
    private String spec;
    // 型号
    private String invtype;
    // 计量单位
    private String unit;
    private DZFDouble jsprice;//结算单价
    private DZFDateTime ts;// 时间
    private Integer dr;
    // 纳税人识别号
    private String taxpayer;
    // 统一社会信用代码
    private String credit_code;
    // 地址
    private String address;
    // 电话
    private String phone_num;
    // 开户行
    private String bank;
    // 账户
    private String account_num;

    private DZFBoolean forcesave;

    private String zjlx;// 证件类型
    private String zjbm;// 证件号码
    private String vphone;// 手机号码
    private String billtype;// 员工类型
    private String varea;// 国籍
    private String lhtype;// 来华适用公式
    private DZFDate lhdate;// 来华时间
    private String cdeptid;// 部门
    private String vdeptname;//
    private String vmemo;// 备注
    private Integer isex = 0;//性别  男 ---- 1  女 ---- 2
    private DZFDate birthdate;// 出生日期
    private DZFDate employedate;// 任职受雇日期
    private DZFDate entrydate;// 首次入境时间
    private DZFDate leavedate;// 预计离境时间
    private String vbirtharea;//出生国家(地区)

    private String taxcode;// 税收编码
    private String taxname;// 货物或应税劳务、服务名称
    private String taxclassify;// 税收品目
    private DZFDouble taxratio;// 税率
    private Integer xslx;// 销售类型
    private DZFBoolean isimp;// 是否导入
    //
    private String kmclassify;//按存货大类成本结转，即科目分类
    private String kmclassifyname;//存货大类名称
    private String chukukmid;//出库科目。
    private String chukukmname;//出库名称
    private String pk_subject;//存货核算科目（暂归启用停用使用 后续处理核算业务）
    private Integer sffc;// 添加封存标识

    //数据展示
    private Integer calcmode;
    private DZFDouble hsl;
    private String pk_accsubj;
    private String subjname;
    private DZFDouble njznum;//结算数

    public DZFDouble getNjznum() {
        return njznum;
    }

    public void setNjznum(DZFDouble njznum) {
        this.njznum = njznum;
    }

    public String getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public String getSubjname() {
        return subjname;
    }

    public void setSubjname(String subjname) {
        this.subjname = subjname;
    }

    public Integer getCalcmode() {
        return calcmode;
    }

    public void setCalcmode(Integer calcmode) {
        this.calcmode = calcmode;
    }

    public DZFDouble getHsl() {
        return hsl;
    }

    public void setHsl(DZFDouble hsl) {
        this.hsl = hsl;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public AuxiliaryAccountBVO() {
        super();
    }

    public String getPk_auacount_b() {
        return pk_auacount_b;
    }

    public void setPk_auacount_b(String pk_auacount_b) {
        this.pk_auacount_b = pk_auacount_b;
    }

    public String getPk_auacount_h() {
        return pk_auacount_h;
    }

    public void setPk_auacount_h(String pk_auacount_h) {
        this.pk_auacount_h = pk_auacount_h;
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

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getTaxpayer() {
        return taxpayer;
    }

    public void setTaxpayer(String taxpayer) {
        this.taxpayer = taxpayer;
    }

    public String getCredit_code() {
        return credit_code;
    }

    public void setCredit_code(String credit_code) {
        this.credit_code = credit_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount_num() {
        return account_num;
    }

    public void setAccount_num(String account_num) {
        this.account_num = account_num;
    }

    public DZFBoolean getForcesave() {
        return forcesave;
    }

    public void setForcesave(DZFBoolean forcesave) {
        this.forcesave = forcesave;
    }

    public String getTaxcode() {
        return taxcode;
    }

    public void setTaxcode(String taxcode) {
        this.taxcode = taxcode;
    }

    public String getTaxname() {
        return taxname;
    }

    public void setTaxname(String taxname) {
        this.taxname = taxname;
    }

    public String getTaxclassify() {
        return taxclassify;
    }

    public void setTaxclassify(String taxclassify) {
        this.taxclassify = taxclassify;
    }

    public DZFDouble getTaxratio() {
        return taxratio;
    }

    public void setTaxratio(DZFDouble taxratio) {
        this.taxratio = taxratio;
    }

    public Integer getXslx() {
        return xslx;
    }

    public void setXslx(Integer xslx) {
        this.xslx = xslx;
    }

    public String getZjlx() {
        return zjlx;
    }

    public String getZjbm() {
        return zjbm;
    }

    public String getCdeptid() {
        return cdeptid;
    }

    public String getVarea() {
        return varea;
    }

    public String getVphone() {
        return vphone;
    }

    public String getBilltype() {
        return billtype;
    }

    public String getLhtype() {
        return lhtype;
    }

    public void setZjlx(String zjlx) {
        this.zjlx = zjlx;
    }

    public void setZjbm(String zjbm) {
        this.zjbm = zjbm;
    }

    public void setCdeptid(String cdeptid) {
        this.cdeptid = cdeptid;
    }

    public void setVarea(String varea) {
        this.varea = varea;
    }

    public void setVphone(String vphone) {
        this.vphone = vphone;
    }

    public void setBilltype(String billtype) {
        this.billtype = billtype;
    }

    public void setLhtype(String lhtype) {
        this.lhtype = lhtype;
    }

    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
    }

    public String getVdeptname() {
        return vdeptname;
    }

    public void setVdeptname(String vdeptname) {
        this.vdeptname = vdeptname;
    }

    public DZFBoolean getIsimp() {
        return isimp;
    }

    public void setIsimp(DZFBoolean isimp) {
        this.isimp = isimp;
    }

    public DZFDate getLhdate() {
        return lhdate;
    }

    public void setLhdate(DZFDate lhdate) {
        this.lhdate = lhdate;
    }

    public String getInvtype() {
        return invtype;
    }

    public void setInvtype(String invtype) {
        this.invtype = invtype;
    }

    public String getKmclassify() {
        return kmclassify;
    }

    public void setKmclassify(String kmclassify) {
        this.kmclassify = kmclassify;
    }

    public String getKmclassifyname() {
        return kmclassifyname;
    }

    public String getChukukmname() {
        return chukukmname;
    }

    public void setKmclassifyname(String kmclassifyname) {
        this.kmclassifyname = kmclassifyname;
    }

    public void setChukukmname(String chukukmname) {
        this.chukukmname = chukukmname;
    }

    public String getChukukmid() {
        return chukukmid;
    }

    public void setChukukmid(String chukukmid) {
        this.chukukmid = chukukmid;
    }

    public DZFDouble getJsprice() {
        return jsprice;
    }

    public void setJsprice(DZFDouble jsprice) {
        this.jsprice = jsprice;
    }


    public DZFDate getBirthdate() {
        return birthdate;
    }

    public DZFDate getEmployedate() {
        return employedate;
    }

    public DZFDate getEntrydate() {
        return entrydate;
    }

    public DZFDate getLeavedate() {
        return leavedate;
    }

    public String getVbirtharea() {
        return vbirtharea;
    }

    public void setBirthdate(DZFDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setEmployedate(DZFDate employedate) {
        this.employedate = employedate;
    }

    public void setEntrydate(DZFDate entrydate) {
        this.entrydate = entrydate;
    }

    public void setLeavedate(DZFDate leavedate) {
        this.leavedate = leavedate;
    }

    public void setVbirtharea(String vbirtharea) {
        this.vbirtharea = vbirtharea;
    }

    public Integer getIsex() {
        return isex;
    }

    public void setIsex(Integer isex) {
        this.isex = isex;
    }

    public Integer getSffc() {
        return sffc;
    }

    public void setSffc(Integer sffc) {
        this.sffc = sffc;
    }

    public String getPk_subject() {
        return pk_subject;
    }

    public void setPk_subject(String pk_subject) {
        this.pk_subject = pk_subject;
    }

    @Override
    public String getPKFieldName() {
        return "pk_auacount_b";
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_auacount_h";
    }

    @Override
    public String getTableName() {
        return "ynt_fzhs_b";
    }

}