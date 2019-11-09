package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * YntCpaccount vo. @author MyEclipse Persistence Tools
 */
public class YntCpaccountVO extends SuperVO<YntCpaccountVO> {

    @JsonProperty("id")
    private String pk_corp_account;
    @JsonProperty("id_sc")
    private String pk_corp_accountschema;
    @JsonProperty("gsid")
    private String pk_corp;
    @JsonProperty("cpsn")
    private String coperatorid;
    @JsonProperty("dopdate")
    private String doperatedate;
    @JsonProperty("extime")
    private String ts;
    private Integer dr;
    @JsonProperty("kmbm")
    private String accountcode;
    @JsonProperty("kmmc")
    private String accountname;
    @JsonProperty("kmcc")
    private Integer accountlevel;
    @JsonProperty("kmlx")
    private Integer accountkind;
    @JsonProperty("kmfx")
    private Integer direction;
    @JsonProperty("yz")
    private DZFBoolean isleaf;
    @JsonProperty("bz")
    private String memo;
    @JsonProperty("v1")
    private String vdef1;
    @JsonProperty("v2")
    private String vdef2;
    @JsonProperty("v3")
    private String vdef3;
    @JsonProperty("v4")
    private String vdef4;
    @JsonProperty("v5")
    private String vdef5;
    @JsonProperty("v6")
    private String vdef6;
    @JsonProperty("v7")
    private String vdef7;
    @JsonProperty("v8")
    private String vdef8;
    @JsonProperty("v9")
    private String vdef9;
    @JsonProperty("v10")
    private String vdef10;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("fc")
    private DZFBoolean bisseal;
    @JsonProperty("bzid")
    private String pk_currency;
    @JsonProperty("bzmc")
    private String currname;
    @JsonProperty("sfsz")
    private DZFBoolean isnum;

    //是否启用辅助核算，改为char(10)，用0和1的位置表示指定段是否启用
    @JsonProperty("isfzhs")
    //@FieldValidate("辅助核算超出范围:isfzhs in ('Y','N');")
    private String isfzhs; //DZFBoolean
    //@JsonProperty("fzhsbm")
    //private String fzhsbm;
    //@JsonProperty("fzhsmc")
    //private String fzhsmc;

    @JsonProperty("jldw")
    private String measurename;
    @JsonProperty("unhdtz")
    private DZFBoolean bunhdtz;//不进行汇兑调整
    @JsonProperty("unxjkm")
    private DZFBoolean buncashkm;//非现金类科目

    private DZFDouble shuilv;
    @JsonProperty("codefullname")
    public String codefullname;

//	private DZFBoolean allow_empty_num;// 允许数量为空

    public String getMeasurename() {
        return measurename;
    }

    public void setMeasurename(String measurename) {
        this.measurename = measurename;
    }

    @JsonProperty("qmth")
    private DZFBoolean isadjust;
    private String __parentId;
    private String codeid;
    private String parentid;
    @JsonProperty("sfyz")
    private DZFBoolean issyscode;//是否系统预置
    @JsonProperty("whhs")
    private DZFBoolean iswhhs;//是否外汇核算
    @JsonProperty("whhsid")
    private String exc_pk_currency;//外汇核算币种主键
    @JsonProperty("whhsbm")
    private String exc_crycode;//外汇核算币种编码

    @JsonProperty("whhsmc")
    private String exc_cryname;//外汇核算币种名称

    @JsonProperty("whhslist")
    private List<BdCurrencyVO> exc_cur_array;

    public List<BdCurrencyVO> getExc_cur_array() {
        String[] pk_cur = exc_pk_currency != null ? exc_pk_currency.split(",") : null;
        String[] code_cur = exc_crycode != null ? exc_crycode.split(",") : null;
        if (code_cur != null && pk_cur != null && code_cur.length > 0 && code_cur.length == pk_cur.length) {
            exc_cur_array = new ArrayList<BdCurrencyVO>();
            for (int i = 0; i < pk_cur.length; i++) {
                BdCurrencyVO v = new BdCurrencyVO();
                v.setPk_currency(pk_cur[i]);
                v.setCurrencycode(code_cur[i]);
                exc_cur_array.add(v);
            }
        }
        return exc_cur_array;
    }

    @JsonProperty("codename")
    public String codename;

    @JsonProperty("fsefxkz")
    private DZFBoolean fsefxkz;//发生额方向控制
    @JsonProperty("yefxkz")
    private DZFBoolean yefxkz;//余额方向控制

    @JsonProperty("wlhx")
    private DZFBoolean isverification;

    private String shuimuid;//税目id
    private String shuimu1;//税目简称
    //是否凭证显示。
    private DZFBoolean shuimushowpz;//是否显示在凭证上.


    //是否启用辅助核算，改为char(10)，用0和1的位置表示指定段是否启用
    public String getIsfzhs() {
        return isfzhs;
    }

    public void setIsfzhs(String isfzhs) {
        this.isfzhs = isfzhs;
    }
	/*
	public String getFzhsbm() {
		return fzhsbm;
	}

	public void setFzhsbm(String fzhsbm) {
		this.fzhsbm = fzhsbm;
	}

	public String getFzhsmc() {
		return fzhsmc;
	}

	public void setFzhsmc(String fzhsmc) {
		this.fzhsmc = fzhsmc;
	}
	*/

    public void setExc_cur_array(List<BdCurrencyVO> exc_cur_array) {
        this.exc_cur_array = exc_cur_array;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPk_corp_account() {
        return pk_corp_account;
    }

    public void setPk_corp_account(String pk_corp_account) {
        this.pk_corp_account = pk_corp_account;
    }

    public String getPk_corp_accountschema() {
        return pk_corp_accountschema;
    }

    public void setPk_corp_accountschema(String pk_corp_accountschema) {
        this.pk_corp_accountschema = pk_corp_accountschema;
    }
    // Constructors

    public DZFBoolean getIswhhs() {
        return iswhhs;
    }

    public void setIswhhs(DZFBoolean iswhhs) {
        this.iswhhs = iswhhs;
    }

    public String getExc_pk_currency() {
        return exc_pk_currency;
    }

    public void setExc_pk_currency(String exc_pk_currency) {
        this.exc_pk_currency = exc_pk_currency;
    }

    public String getExc_crycode() {
        return exc_crycode;
    }

    public void setExc_crycode(String exc_crycode) {
        this.exc_crycode = exc_crycode;
    }

    public DZFBoolean getIssyscode() {
        return issyscode;
    }

    public void setIssyscode(DZFBoolean issyscode) {
        this.issyscode = issyscode;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    /**
     * default constructor
     */
    public YntCpaccountVO() {
    }

    @Column(name = "COPERATORID", length = 20)
    public String getCoperatorid() {
        return this.coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    @Column(name = "DOPERATEDATE", length = 10)
    public String getDoperatedate() {
        return this.doperatedate;
    }

    public void setDoperatedate(String doperatedate) {
        this.doperatedate = doperatedate;
    }

    @Column(name = "TS", length = 19)
    public String getTs() {
        return this.ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Column(name = "DR", precision = 22, scale = 0)
    public Integer getDr() {
        return this.dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    @Column(name = "ACCOUNTCODE", length = 50)
    public String getAccountcode() {
        return this.accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
        this.codeid = accountcode;
        if (accountcode != null && accountcode.length() > 4) {
            this.__parentId = accountcode.substring(0, accountcode.length() - 2);
        } else {
            this.__parentId = null;
        }
    }

    @Column(name = "ACCOUNTNAME", length = 100)
    public String getAccountname() {
        return this.accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    @Column(name = "ACCOUNTLEVEL", precision = 22, scale = 0)
    public Integer getAccountlevel() {
        return this.accountlevel;
    }

    public void setAccountlevel(Integer accountlevel) {
        this.accountlevel = accountlevel;
    }

    @Column(name = "ACCOUNTKIND", precision = 22, scale = 0)
    public Integer getAccountkind() {
        return this.accountkind;
    }

    public void setAccountkind(Integer accountkind) {
        this.accountkind = accountkind;
    }

    @Column(name = "DIRECTION", precision = 22, scale = 0)
    public Integer getDirection() {
        return this.direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    @Column(name = "ISLEAF", length = 1)
    public DZFBoolean getIsleaf() {
        return this.isleaf;
    }

    public void setIsleaf(DZFBoolean isleaf) {
        this.isleaf = isleaf;
    }

    @Column(name = "MEMO", length = 500)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "VDEF1", length = 100)
    public String getVdef1() {
        return this.vdef1;
    }

    public void setVdef1(String vdef1) {
        this.vdef1 = vdef1;
    }

    @Column(name = "VDEF2", length = 100)
    public String getVdef2() {
        return this.vdef2;
    }

    public void setVdef2(String vdef2) {
        this.vdef2 = vdef2;
    }

    @Column(name = "VDEF3", length = 100)
    public String getVdef3() {
        return this.vdef3;
    }

    public void setVdef3(String vdef3) {
        this.vdef3 = vdef3;
    }

    @Column(name = "VDEF4", length = 100)
    public String getVdef4() {
        return this.vdef4;
    }

    public void setVdef4(String vdef4) {
        this.vdef4 = vdef4;
    }

    @Column(name = "VDEF5", length = 100)
    public String getVdef5() {
        return this.vdef5;
    }

    public void setVdef5(String vdef5) {
        this.vdef5 = vdef5;
    }

    @Column(name = "VDEF6", length = 100)
    public String getVdef6() {
        return this.vdef6;
    }

    public void setVdef6(String vdef6) {
        this.vdef6 = vdef6;
    }

    @Column(name = "VDEF7", length = 100)
    public String getVdef7() {
        return this.vdef7;
    }

    public void setVdef7(String vdef7) {
        this.vdef7 = vdef7;
    }

    @Column(name = "VDEF8", length = 100)
    public String getVdef8() {
        return this.vdef8;
    }

    public void setVdef8(String vdef8) {
        this.vdef8 = vdef8;
    }

    @Column(name = "VDEF9", length = 100)
    public String getVdef9() {
        return this.vdef9;
    }

    public void setVdef9(String vdef9) {
        this.vdef9 = vdef9;
    }

    @Column(name = "VDEF10", length = 100)
    public String getVdef10() {
        return this.vdef10;
    }

    public void setVdef10(String vdef10) {
        this.vdef10 = vdef10;
    }


    public String get__parentId() {
        return __parentId;
    }

    public void set__parentId(String __parentId) {
        this.__parentId = __parentId;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    @Column(name = "BISSEAL", length = 1)
    public DZFBoolean getBisseal() {
        return this.bisseal == null ? DZFBoolean.FALSE : this.bisseal;
    }

    public void setBisseal(DZFBoolean bisseal) {
        this.bisseal = bisseal;
    }

    @Column(name = "CURRNAME", length = 30)
    public String getCurrname() {
        return this.currname == null ? "人民币" : this.currname;
    }

    public void setCurrname(String currname) {
        this.currname = currname;
    }

    @Column(name = "ISNUM", length = 1)
    public DZFBoolean getIsnum() {
        return this.isnum == null ? DZFBoolean.FALSE : this.isnum;
    }

    public void setIsnum(DZFBoolean isnum) {
        this.isnum = isnum;
    }

    @Column(name = "ISADJUST", length = 1)
    public DZFBoolean getIsadjust() {
        return isadjust == null ? DZFBoolean.FALSE : this.isadjust;
    }

    public void setIsadjust(DZFBoolean isadjust) {
        this.isadjust = isadjust;
    }


    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public String getExc_cryname() {
        return exc_cryname;
    }

    public void setExc_cryname(String exc_cryname) {
        this.exc_cryname = exc_cryname;
    }


    public DZFDouble getShuilv() {
        return shuilv;
    }

    public void setShuilv(DZFDouble shuilv) {
        this.shuilv = shuilv;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_corp_account";
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_cpaccount";
    }

    public String getCodename() {
        return getAccountcode() + "_" + getAccountname();
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public DZFBoolean getFsefxkz() {
        return fsefxkz;
    }

    public void setFsefxkz(DZFBoolean fsefxkz) {
        this.fsefxkz = fsefxkz;
    }

    public DZFBoolean getYefxkz() {
        return yefxkz;
    }

    public void setYefxkz(DZFBoolean yefxkz) {
        this.yefxkz = yefxkz;
    }

    public DZFBoolean getIsverification() {
        return isverification;
    }

    public void setIsverification(DZFBoolean isverification) {
        this.isverification = isverification;
    }

    public String getShuimuid() {
        return shuimuid;
    }

    public void setShuimuid(String shuimuid) {
        this.shuimuid = shuimuid;
    }

    public String getShuimu1() {
        return shuimu1;
    }

    public void setShuimu1(String shuimu1) {
        this.shuimu1 = shuimu1;
    }

    public DZFBoolean getShuimushowpz() {
        return shuimushowpz;
    }

    public void setShuimushowpz(DZFBoolean shuimushowpz) {
        this.shuimushowpz = shuimushowpz;
    }

    public DZFBoolean getBunhdtz() {
        return bunhdtz;
    }

    public void setBunhdtz(DZFBoolean bunhdtz) {
        this.bunhdtz = bunhdtz;
    }

    public String getCodefullname() {
        if (StringUtil.isEmpty(getFullname())) {
            return getAccountcode() + " " + getAccountname();
        }
        return getAccountcode() + " " + getFullname();
    }

    public void setCodefullname(String codefullname) {
        this.codefullname = codefullname;
    }
//	public DZFBoolean getAllow_empty_num() {
//		return allow_empty_num;
//	}
//
//	public void setAllow_empty_num(DZFBoolean allow_empty_num) {
//		this.allow_empty_num = allow_empty_num;
//	}

    public DZFBoolean getBuncashkm() {
        return buncashkm;
    }

    public void setBuncashkm(DZFBoolean buncashkm) {
        this.buncashkm = buncashkm;
    }


}