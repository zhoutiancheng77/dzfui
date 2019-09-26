package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 期末结转
 * 创建日期:2014-11-04 14:46:55
 *
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class QmclVO extends SuperVO {

    @JsonProperty("pk_gs")
    private String pk_corp;
    private String pk_gs1;//不存库
    @JsonProperty("qj")
    private String period;
    private String qj1;//不存库
    @JsonProperty("pk_id")
    private String pk_qmcl;
    private String pk_id1;//不存库
    @JsonProperty("hdsytz")
    private DZFBoolean ishdsytz;//期末调汇
    @JsonProperty("jtsj")
    private DZFBoolean isjtsj;//计提附加税
    @JsonProperty("cbjz")
    private DZFBoolean iscbjz;//成本结转
    @JsonProperty("qjsyjz")
    private DZFBoolean isqjsyjz;//损益结转
    @JsonProperty("zjjt")
    private DZFBoolean iszjjt;//计提折旧
    @JsonProperty("cbjz1")
    private DZFBoolean cbjz1;//结转材料成本

    //归集制造费用，已经不用
//	@JsonProperty("cbjz2")
//	private DZFBoolean cbjz2;
    @JsonProperty("cbjz3")
    private DZFBoolean cbjz3;//结转辅助生产成本
    @JsonProperty("cbjz4")
    private DZFBoolean cbjz4;//结转制造费用
    @JsonProperty("cbjz5")
    private DZFBoolean cbjz5;//结转完工产品成本
    @JsonProperty("cbjz6")
    private DZFBoolean cbjz6;//结转销售成本
    @JsonProperty("ts")
    private DZFDateTime ts;

    @JsonProperty("xsxjxcf")
    private DZFBoolean xsxjxcf;//先进先出法
//	@JsonProperty("clxjxcf")
//	private DZFBoolean clxjxcf;//前端已经全部注释掉，此字段不用了。

    private DZFBoolean zzsjz;//增值税结转

    private DZFBoolean qysdsjz;//企业所得税结转

    private String vdef9;
    private String coperatorid;
    private String vdef1;
    private String vdef8;
    private String vdef10;
    private String vapproveid;
    private String vapprovenote;
    private String vdef7;
    private DZFBoolean jzfinish;
    private DZFDate dapprovedate;
    private String vdef2;
    private String vdef5;
    //
    private String pk_billtype;
    private Integer vbillstatus;
    private String memo;
    private String vdef3;
    private String vdef6;
    private String vbillno;
    private Integer dr;
    private DZFDate doperatedate;
    private DZFBoolean isgdzjjz;//期末处理该字段没有动过,不用
    //
    private String vdef4;
    private String resvalue;//调汇的结果数据
    private TempInvtoryVO[] zgdata;//暂估数据
    @JsonProperty("costype")
    private Integer icosttype;//成本结转类型


    private DZFBoolean ikc;//是否库存
    private DZFBoolean iwb;//是否外币
    private DZFBoolean igdzc;//是否固定资产
    private String corpname;
    private DZFBoolean isgz;//是否关账
    private DZFBoolean isybr;//是否一般人
    private DZFDate jzdate;//当前qmvo的pk_corp建账日期


    private DZFDouble nlossmny;
    //成本结转、期间损益、汇兑损溢、计提税金、增值税、所得税、计提折旧结转凭证号  多个用逗号隔开
    private String cbjzpzh;
    private String qjsyjzpzh;
    private String hdsytzpzh;
    private String jtsjpzh;
    private String zzsjzpzh;
    private String qysdsjzpzh;
    private String zjjtpzh;

    public String getCbjzpzh() {
        return cbjzpzh;
    }

    public void setCbjzpzh(String cbjzpzh) {
        this.cbjzpzh = cbjzpzh;
    }

    public String getQjsyjzpzh() {
        return qjsyjzpzh;
    }

    public void setQjsyjzpzh(String qjsyjzpzh) {
        this.qjsyjzpzh = qjsyjzpzh;
    }

    public String getHdsytzpzh() {
        return hdsytzpzh;
    }

    public void setHdsytzpzh(String hdsytzpzh) {
        this.hdsytzpzh = hdsytzpzh;
    }

    public String getJtsjpzh() {
        return jtsjpzh;
    }

    public void setJtsjpzh(String jtsjpzh) {
        this.jtsjpzh = jtsjpzh;
    }

    public String getZzsjzpzh() {
        return zzsjzpzh;
    }

    public void setZzsjzpzh(String zzsjzpzh) {
        this.zzsjzpzh = zzsjzpzh;
    }

    public String getQysdsjzpzh() {
        return qysdsjzpzh;
    }

    public void setQysdsjzpzh(String qysdsjzpzh) {
        this.qysdsjzpzh = qysdsjzpzh;
    }

    public String getZjjtpzh() {
        return zjjtpzh;
    }

    public void setZjjtpzh(String zjjtpzh) {
        this.zjjtpzh = zjjtpzh;
    }

    public DZFBoolean getIsgz() {
        return isgz;
    }

    public void setIsgz(DZFBoolean isgz) {
        this.isgz = isgz;
    }

    public String getCorpname() {
        return corpname;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public DZFBoolean getIgdzc() {
        return igdzc;
    }

    public void setIgdzc(DZFBoolean igdzc) {
        this.igdzc = igdzc;
    }

    public DZFBoolean getIkc() {
        return ikc;
    }

    public void setIkc(DZFBoolean ikc) {
        this.ikc = ikc;
    }

    public DZFBoolean getIwb() {
        return iwb;
    }

    public void setIwb(DZFBoolean iwb) {
        this.iwb = iwb;
    }

    /**
     * 属性pk_corp的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getPk_corp() {
        return pk_corp;
    }

    /**
     * 属性pk_corp的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newPk_corp String
     */
    public void setPk_corp(String newPk_corp) {
        this.pk_corp = newPk_corp;
    }

    /**
     * 属性ts的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFDateTime
     */
    public DZFDateTime getTs() {
        return ts;
    }

    /**
     * 属性ts的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newTs DZFDateTime
     */
    public void setTs(DZFDateTime newTs) {
        this.ts = newTs;
    }

    /**
     * 属性vdef9的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef9() {
        return vdef9;
    }

    /**
     * 属性vdef9的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef9 String
     */
    public void setVdef9(String newVdef9) {
        this.vdef9 = newVdef9;
    }

    /**
     * 属性coperatorid的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getCoperatorid() {
        return coperatorid;
    }

    /**
     * 属性coperatorid的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newCoperatorid String
     */
    public void setCoperatorid(String newCoperatorid) {
        this.coperatorid = newCoperatorid;
    }

    /**
     * 属性pk_qmcl的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getPk_qmcl() {
        return pk_qmcl;
    }

    /**
     * 属性pk_qmcl的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newPk_qmcl String
     */
    public void setPk_qmcl(String newPk_qmcl) {
        this.pk_qmcl = newPk_qmcl;
    }

    /**
     * 属性vdef1的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef1() {
        return vdef1;
    }

    /**
     * 属性vdef1的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef1 String
     */
    public void setVdef1(String newVdef1) {
        this.vdef1 = newVdef1;
    }

    /**
     * 属性vdef8的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef8() {
        return vdef8;
    }

    /**
     * 属性vdef8的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef8 String
     */
    public void setVdef8(String newVdef8) {
        this.vdef8 = newVdef8;
    }

    /**
     * 属性vdef10的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef10() {
        return vdef10;
    }

    /**
     * 属性vdef10的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef10 String
     */
    public void setVdef10(String newVdef10) {
        this.vdef10 = newVdef10;
    }

    /**
     * 属性period的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getPeriod() {
        return period;
    }

    /**
     * 属性period的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newPeriod String
     */
    public void setPeriod(String newPeriod) {
        this.period = newPeriod;
    }

    /**
     * 属性vapproveid的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVapproveid() {
        return vapproveid;
    }

    /**
     * 属性vapproveid的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVapproveid String
     */
    public void setVapproveid(String newVapproveid) {
        this.vapproveid = newVapproveid;
    }

    /**
     * 属性vapprovenote的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVapprovenote() {
        return vapprovenote;
    }

    /**
     * 属性vapprovenote的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVapprovenote String
     */
    public void setVapprovenote(String newVapprovenote) {
        this.vapprovenote = newVapprovenote;
    }

    /**
     * 属性ishdsytz的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getIshdsytz() {
        return ishdsytz;
    }

    /**
     * 属性ishdsytz的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newIshdsytz DZFBoolean
     */
    public void setIshdsytz(DZFBoolean newIshdsytz) {
        this.ishdsytz = newIshdsytz;
    }

    /**
     * 属性iscbjz的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getIscbjz() {
        return iscbjz;
    }

    /**
     * 属性iscbjz的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newIscbjz DZFBoolean
     */
    public void setIscbjz(DZFBoolean newIscbjz) {
        this.iscbjz = newIscbjz;
    }

    /**
     * 属性vdef7的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef7() {
        return vdef7;
    }

    /**
     * 属性vdef7的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef7 String
     */
    public void setVdef7(String newVdef7) {
        this.vdef7 = newVdef7;
    }

    /**
     * 属性jzfinish的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getJzfinish() {
        return jzfinish;
    }

    /**
     * 属性jzfinish的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newJzfinish DZFBoolean
     */
    public void setJzfinish(DZFBoolean newJzfinish) {
        this.jzfinish = newJzfinish;
    }

    /**
     * 属性dapprovedate的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFDate
     */
    public DZFDate getDapprovedate() {
        return dapprovedate;
    }

    /**
     * 属性dapprovedate的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newDapprovedate DZFDate
     */
    public void setDapprovedate(DZFDate newDapprovedate) {
        this.dapprovedate = newDapprovedate;
    }

    /**
     * 属性vdef2的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef2() {
        return vdef2;
    }

    /**
     * 属性vdef2的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef2 String
     */
    public void setVdef2(String newVdef2) {
        this.vdef2 = newVdef2;
    }

    /**
     * 属性vdef5的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef5() {
        return vdef5;
    }

    /**
     * 属性vdef5的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef5 String
     */
    public void setVdef5(String newVdef5) {
        this.vdef5 = newVdef5;
    }

    /**
     * 属性isqjsyjz的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getIsqjsyjz() {
        return isqjsyjz;
    }

    /**
     * 属性isqjsyjz的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newIsqjsyjz DZFBoolean
     */
    public void setIsqjsyjz(DZFBoolean newIsqjsyjz) {
        this.isqjsyjz = newIsqjsyjz;
    }

    /**
     * 属性pk_billtype的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getPk_billtype() {
        return pk_billtype;
    }

    /**
     * 属性pk_billtype的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newPk_billtype String
     */
    public void setPk_billtype(String newPk_billtype) {
        this.pk_billtype = newPk_billtype;
    }

    /**
     * 属性vbillstatus的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return UFDouble
     */
    public Integer getVbillstatus() {
        return vbillstatus;
    }

    /**
     * 属性vbillstatus的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVbillstatus UFDouble
     */
    public void setVbillstatus(Integer newVbillstatus) {
        this.vbillstatus = newVbillstatus;
    }

    /**
     * 属性memo的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 属性memo的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newMemo String
     */
    public void setMemo(String newMemo) {
        this.memo = newMemo;
    }

    /**
     * 属性vdef3的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef3() {
        return vdef3;
    }

    /**
     * 属性vdef3的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef3 String
     */
    public void setVdef3(String newVdef3) {
        this.vdef3 = newVdef3;
    }

    /**
     * 属性vdef6的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef6() {
        return vdef6;
    }

    /**
     * 属性vdef6的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef6 String
     */
    public void setVdef6(String newVdef6) {
        this.vdef6 = newVdef6;
    }

    /**
     * 属性vbillno的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVbillno() {
        return vbillno;
    }

    /**
     * 属性vbillno的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVbillno String
     */
    public void setVbillno(String newVbillno) {
        this.vbillno = newVbillno;
    }

    /**
     * 属性dr的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return UFDouble
     */
    public Integer getDr() {
        return dr;
    }

    /**
     * 属性dr的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newDr UFDouble
     */
    public void setDr(Integer newDr) {
        this.dr = newDr;
    }

    /**
     * 属性doperatedate的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFDate
     */
    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    /**
     * 属性doperatedate的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newDoperatedate DZFDate
     */
    public void setDoperatedate(DZFDate newDoperatedate) {
        this.doperatedate = newDoperatedate;
    }

    /**
     * 属性isgdzjjz的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getIsgdzjjz() {
        return isgdzjjz;
    }

    /**
     * 属性isgdzjjz的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newIsgdzjjz DZFBoolean
     */
    public void setIsgdzjjz(DZFBoolean newIsgdzjjz) {
        this.isgdzjjz = newIsgdzjjz;
    }

    /**
     * 属性iszjjt的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return DZFBoolean
     */
    public DZFBoolean getIszjjt() {
        return iszjjt;
    }

    /**
     * 属性iszjjt的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newIszjjt DZFBoolean
     */
    public void setIszjjt(DZFBoolean newIszjjt) {
        this.iszjjt = newIszjjt;
    }

    /**
     * 属性vdef4的Getter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @return String
     */
    public String getVdef4() {
        return vdef4;
    }

    /**
     * 属性vdef4的Setter方法.
     * 创建日期:2014-11-04 14:46:55
     *
     * @param newVdef4 String
     */
    public void setVdef4(String newVdef4) {
        this.vdef4 = newVdef4;
    }

    /**
     * <p>取得父VO主键字段.
     * <p>
     * 创建日期:2014-11-04 14:46:55
     *
     * @return java.lang.String
     */
    public String getParentPKFieldName() {
        return null;
    }

    /**
     * <p>取得表主键.
     * <p>
     * 创建日期:2014-11-04 14:46:55
     *
     * @return java.lang.String
     */
    public String getPKFieldName() {
        return "pk_qmcl";
    }

    /**
     * <p>返回表名称.
     * <p>
     * 创建日期:2014-11-04 14:46:55
     *
     * @return java.lang.String
     */
    public String getTableName() {
        return "YNT_QMCL";
    }

    /**
     * 按照默认方式创建构造子.
     * <p>
     * 创建日期:2014-11-04 14:46:55
     */
    public QmclVO() {
        super();
    }

    public String getResvalue() {
        return resvalue;
    }

    public void setResvalue(String resvalue) {
        this.resvalue = resvalue;
    }

    public TempInvtoryVO[] getZgdata() {
        return zgdata;
    }

    public void setZgdata(TempInvtoryVO[] zgdata) {
        this.zgdata = zgdata;
    }

    public String getPk_gs1() {
        return pk_gs1;
    }

    public void setPk_gs1(String pk_gs1) {
        this.pk_gs1 = pk_gs1;
    }

    public String getQj1() {
        return qj1;
    }

    public void setQj1(String qj1) {
        this.qj1 = qj1;
    }

    public String getPk_id1() {
        return pk_id1;
    }

    public void setPk_id1(String pk_id1) {
        this.pk_id1 = pk_id1;
    }

    public Integer getIcosttype() {
        return icosttype;
    }

    public void setIcosttype(Integer icosttype) {
        this.icosttype = icosttype;
    }

    public DZFBoolean getCbjz1() {
        return cbjz1;
    }

    public void setCbjz1(DZFBoolean cbjz1) {
        this.cbjz1 = cbjz1;
    }

    //	public DZFBoolean getCbjz2() {
//		return cbjz2;
//	}
//	public void setCbjz2(DZFBoolean cbjz2) {
//		this.cbjz2 = cbjz2;
//	}
    public DZFBoolean getCbjz3() {
        return cbjz3;
    }

    public void setCbjz3(DZFBoolean cbjz3) {
        this.cbjz3 = cbjz3;
    }

    public DZFBoolean getCbjz4() {
        return cbjz4;
    }

    public void setCbjz4(DZFBoolean cbjz4) {
        this.cbjz4 = cbjz4;
    }

    public DZFBoolean getCbjz5() {
        return cbjz5;
    }

    public void setCbjz5(DZFBoolean cbjz5) {
        this.cbjz5 = cbjz5;
    }

    public DZFBoolean getCbjz6() {
        return cbjz6;
    }

    public void setCbjz6(DZFBoolean cbjz6) {
        this.cbjz6 = cbjz6;
    }

    public DZFBoolean getXsxjxcf() {
        return xsxjxcf;
    }

    public void setXsxjxcf(DZFBoolean xsxjxcf) {
        this.xsxjxcf = xsxjxcf;
    }

    //	public DZFBoolean getClxjxcf() {
//		return clxjxcf;
//	}
//	public void setClxjxcf(DZFBoolean clxjxcf) {
//		this.clxjxcf = clxjxcf;
//	}
    public DZFBoolean getIsjtsj() {
        return isjtsj;
    }

    public void setIsjtsj(DZFBoolean isjtsj) {
        this.isjtsj = isjtsj;
    }

    public DZFBoolean getZzsjz() {
        return zzsjz;
    }

    public void setZzsjz(DZFBoolean zzsjz) {
        this.zzsjz = zzsjz;
    }

    public DZFBoolean getQysdsjz() {
        return qysdsjz;
    }

    public void setQysdsjz(DZFBoolean qysdsjz) {
        this.qysdsjz = qysdsjz;
    }

    public DZFBoolean getIsybr() {
        return isybr;
    }

    public void setIsybr(DZFBoolean isybr) {
        this.isybr = isybr;
    }

    public DZFDate getJzdate() {
        return jzdate;
    }

    public void setJzdate(DZFDate jzdate) {
        this.jzdate = jzdate;
    }

} 
