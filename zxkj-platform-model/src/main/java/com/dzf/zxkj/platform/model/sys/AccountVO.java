package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountVO extends SuperVO {
	private static final long serialVersionUID = -7255675917825048433L;
	
	@JsonProperty("bdate")
	public DZFDate begindate;
	
	@JsonProperty("bintro")
	public String briefintro;
	
	@JsonProperty("chcode")
	public String chargedeptcode;
	
	@JsonProperty("chname")
	public String chargedeptname;
	
	@JsonProperty("ccounty")
	public String citycounty;
	
	@JsonProperty("ctype")
	public String corptype;
	
	@JsonProperty("carea")
	public String countryarea;
	
	@JsonProperty("cdate")
	public DZFDate createdate;
	
	@JsonProperty("d1")
	public String def1;
	
	@JsonProperty("longitude")
	public String def10;//经度
	
	@JsonProperty("latitude")
	public String def11;//纬度
	
	@JsonProperty("d12")
	public String def12;//企业识别号
	
	@JsonProperty("icbegindate")
	private DZFDate icbegindate;//库存启用日期
	
	@JsonProperty("d13")
	public String def13;
	
	@JsonProperty("d14")
	public String def14;
	
	@JsonProperty("d15")
	public String def15;
	
	@JsonProperty("d16")
	public String def16;
	
	@JsonProperty("d17")
	public String def17;
	
	@JsonProperty("d18")
	public String def18;
	
	@JsonProperty("d19")
	public String def19;
	
	@JsonProperty("d2")
	public String def2;
	
	@JsonProperty("d20")
	public String def20;//服务网点：F1-非服务网点；F2-启用中；F3-停用中
	
	@JsonProperty("d3")
	public String def3;
	
	@JsonProperty("d4")
	public String def4;
	
	@JsonProperty("d5")
	public String def5;
	
	@JsonProperty("d6")
	public String def6;
	
	@JsonProperty("d7")
	public String def7;
	
	@JsonProperty("d8")
	public String def8;
	
	@JsonProperty("d9")
	public String def9;
	//public Integer ecotype;
	
	@JsonProperty("ectype")
	public String ecotype; 
	
	@JsonProperty("e1")
	public String email1;
	
	@JsonProperty("e2")
	public String email2;
	
	@JsonProperty("e3")
	public String email3;
	
	@JsonProperty("edate")
	public DZFDate enddate;
	
	@JsonProperty("fcorp")
	public String fathercorp;
	
	@JsonProperty("f1")
	public String fax1;
	
	@JsonProperty("f2")
	public String fax2;
	
	@JsonProperty("fname")
	public String foreignname;//销售代表
	
	@JsonProperty("fid")//---------使用他当代理商id，存代理商档案的id
	public String foreignid;
	
	public String dlsname;//代理商名称，不存库
	
	@JsonProperty("hflag")
	public DZFBoolean holdflag;
	
	@JsonProperty("idnum")
	public String idnumber;
	
	@JsonProperty("indus")
	public String industry;
	
	@JsonProperty("incode")
	public String innercode;
	
	@JsonProperty("hasaccount")
	public DZFBoolean ishasaccount ;
	
	@JsonProperty("seal")
	public DZFBoolean isseal;
	
	@JsonProperty("workingunit")
	public DZFBoolean isworkingunit;
	
	@JsonProperty("bodycode")
	public String legalbodycode;
	
	@JsonProperty("l1")
	public String linkman1;
	
	@JsonProperty("l2")
	public String linkman2;
	
	@JsonProperty("l3")
	public String linkman3;
	
	@JsonProperty("backup")
	public boolean m_isbackup;
	
	@JsonProperty("mcode")
	public String maxinnercode;
	
	@JsonProperty("memo")
	public String memo;
	
	@JsonProperty("ownerrate")
	public DZFDouble ownersharerate;
	
	@JsonProperty("p1")
	public String phone1;
	
	@JsonProperty("p2")
	public String phone2;
	
	@JsonProperty("p3")
	public String phone3;
	
	@JsonProperty("pk_gs")
	public String pk_corp;
	
	@JsonProperty("pk_gskind")
	public String pk_corpkind;
	
	@JsonProperty("pk_cur")
	public String pk_currency;
	
	@JsonProperty("postadd")
	public String postaddr;
	
	@JsonProperty("province")
	public String province;
	
	@JsonProperty("regcap")
	public DZFDouble regcapital;
	
	@JsonProperty("region")
	private String region; 
	
	@JsonProperty("saleaddr")
	public String saleaddr;
	
	@JsonProperty("sealeddate")
	public DZFDate sealeddate;
	
	@JsonProperty("payertype")
	private Integer taxpayertype; //纳税人类别
	
	@JsonProperty("ts")
	private DZFDateTime ts;
	
	@JsonProperty("ucode")
	public String unitcode;
	
	@JsonProperty("uinction")
	private String unitdistinction;
	
	@JsonProperty("uname")
	public String unitname;
	
	@JsonProperty("ushortname")
	public String unitshortname;
	
	@JsonProperty("url")
	public String url;//公司logo存放地址
	
	@JsonProperty("zcode")
	public String zipcode;
	
	@JsonProperty("s_order")
	public Integer showorder;
	
	@JsonProperty("bb_date")
	private DZFDate busibegindate; //hr业务开始日期
	
	@JsonProperty("bd_date")
	private DZFDate busienddate; //HR业务结束日期
	
	@JsonProperty("tax_code")
	public String taxcode;; //公司类别(普通,结算中心)
	
	@JsonProperty("accountcorp")
	private DZFBoolean isaccountcorp;//是否会计公司
	
	@JsonProperty("datacorp")
	private DZFBoolean isdatacorp;//是否数据中心
	
	@JsonProperty("curr")
	private DZFBoolean iscurr;//是否多币种
	
	@JsonProperty("costforwardstyle")
	private Integer icostforwardstyle;//成本结转类型
	
//	@JsonProperty("buildic")
//	private DZFBoolean bbuildic;//启用ic模块
	
	@JsonProperty("useretail")
	public DZFBoolean isuseretail; //是否用于零售
	
	@JsonProperty("stsource")
	public String vcustsource;// 客户来源
	@JsonProperty("urcenote")
	public String vsourcenote;// 客户来源说明

	@JsonProperty("books")
	private String accbooks;//对应核算账簿
	
	@JsonProperty("booksname")
	private String booksname;//对应核算账簿名称
	
	@JsonProperty("x1")
	private int x1;
	
	@JsonProperty("y1")
	private int y1;
	
	@JsonProperty("x2")
	private int x2;
	
	@JsonProperty("y2")
	private int y2;
	
	@JsonProperty("imgwh")
	private int imgwidth;//照片宽度
	
	@JsonProperty("imght")
	private int imgheight;//照片高度
	@JsonProperty("bkpwd")
	private DZFBoolean bkpnode;//是否开票网点
	
	@JsonProperty("nkname")
	public String vbankname;// 开户银行
	@JsonProperty("nkcode")
	public String vbankcode;// 账号
	
	private DZFBoolean ischannel;
	
	@JsonProperty("isfactory")
	public DZFBoolean isfactory;//是否会计工厂
	    
	private Integer custtype;// 客户类型
	
    @JsonProperty("ovince")
    public Integer vprovince;// 省
    @JsonProperty("city")
    public Integer vcity;// 市
    @JsonProperty("area")
    public Integer varea;// 区
    
    @JsonProperty("jdate")
    private DZFDate djoindate;//加盟商加盟日期
    
    private Integer invtype;//发票类型  0: 专用发票、 1:普通发票 、2: 电子普通发票
    
    private Integer isweixin;
    
    @JsonProperty("chtype")
    private Integer channeltype;//加盟类型   1-普通加盟商；2-金牌加盟商；8-待变更加盟商；9-演示加盟商
    
    @JsonProperty("dreldate")
    private DZFDate drelievedate;//解约日期
    
    @JsonProperty("ideluse")
    private Integer idelayuse;//延后使用方案；1--延后使用两个月、  2--使用到合同到期
    
    public DZFDate getDrelievedate() {
        return drelievedate;
    }

    public void setDrelievedate(DZFDate drelievedate) {
        this.drelievedate = drelievedate;
    }

    public Integer getIdelayuse() {
        return idelayuse;
    }

    public void setIdelayuse(Integer idelayuse) {
        this.idelayuse = idelayuse;
    }

    public Integer getChanneltype() {
        return channeltype;
    }

    public void setChanneltype(Integer channeltype) {
        this.channeltype = channeltype;
    }
    
    public Integer getIsweixin() {
		return isweixin;
	}

	public void setIsweixin(Integer isweixin) {
		this.isweixin = isweixin;
	}

	public Integer getInvtype() {
        return invtype;
    }

    public void setInvtype(Integer invtype) {
        this.invtype = invtype;
    }

	public DZFDate getDjoindate() {
        return djoindate;
    }

    public void setDjoindate(DZFDate djoindate) {
        this.djoindate = djoindate;
    }

    public Integer getVprovince() {
        return vprovince;
    }

    public void setVprovince(Integer vprovince) {
        this.vprovince = vprovince;
    }

    public Integer getVcity() {
        return vcity;
    }

    public void setVcity(Integer vcity) {
        this.vcity = vcity;
    }

    public Integer getVarea() {
        return varea;
    }

    public void setVarea(Integer varea) {
        this.varea = varea;
    }

    public Integer getCusttype() {
		return custtype;
	}

	public void setCusttype(Integer custtype) {
		this.custtype = custtype;
	}
	public DZFBoolean getIsfactory() {
		return isfactory;
	}

	public void setIsfactory(DZFBoolean isfactory) {
		this.isfactory = isfactory;
	}
	
	public String getVbankname() {
		return vbankname;
	}

	public void setVbankname(String vbankname) {
		this.vbankname = vbankname;
	}

	public String getVbankcode() {
		return vbankcode;
	}

	public void setVbankcode(String vbankcode) {
		this.vbankcode = vbankcode;
	}

	public int getImgwidth() {
		return imgwidth;
	}

	public void setImgwidth(int imgwidth) {
		this.imgwidth = imgwidth;
	}

	public int getImgheight() {
		return imgheight;
	}

	public void setImgheight(int imgheight) {
		this.imgheight = imgheight;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public String getAccbooks() {
		return accbooks;
	}

	public void setAccbooks(String accbooks) {
		this.accbooks = accbooks;
	}

	public String getBooksname() {
		return booksname;
	}

	public void setBooksname(String booksname) {
		this.booksname = booksname;
	}

/**
 * 使用主键字段进行初始化的构造子。
 *
 * 创建日期：(2001-5-16)
 */
public AccountVO() {	
}

/**
 * 使用主键进行初始化的构造子。
 *
 * 创建日期：(2001-5-16)
 * @param ??fieldNameForMethod?? 主键值
 */
public AccountVO(String newPk_corp) {

	// 为主键字段赋值:
	pk_corp = newPk_corp;
	
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return DZFDate
 */
public DZFDate getBegindate() {
	return begindate;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @return String
 */
public String getBriefintro() {
	return briefintro;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return String
 */
public String getChargedeptcode() {
	return chargedeptcode;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return String
 */
public String getChargedeptname() {
	return chargedeptname;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @return String
 */
public String getCitycounty() {
	return citycounty;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 14:11:57)
 * @return String
 */
public String getCorptype() {
	return corptype;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @return String
 */
public String getCountryarea() {
	return countryarea;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-4-18 16:05:35)
 * @return DZFDate
 */
public DZFDate getCreatedate() {
	return createdate;
}

/**
 * 属性def1的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef1() {
	return def1;
}

/**
 * 属性def10的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef10() {
	return def10;
}

/**
 * 属性def11的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef11() {
	return def11;
}

/**
 * 属性def12的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef12() {
	return def12;
}

/**
 * 属性def13的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef13() {
	return def13;
}

/**
 * 属性def14的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef14() {
	return def14;
}

/**
 * 属性def15的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef15() {
	return def15;
}

/**
 * 属性def16的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef16() {
	return def16;
}

/**
 * 属性def17的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef17() {
	return def17;
}

/**
 * 属性def18的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef18() {
	return def18;
}

/**
 * 属性def19的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef19() {
	return def19;
}

/**
 * 属性def2的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef2() {
	return def2;
}

/**
 * 属性def20的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef20() {
	return def20;
}

/**
 * 属性def3的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef3() {
	return def3;
}

/**
 * 属性def4的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef4() {
	return def4;
}

/**
 * 属性def5的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef5() {
	return def5;
}

/**
 * 属性def6的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef6() {
	return def6;
}

/**
 * 属性def7的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef7() {
	return def7;
}

/**
 * 属性def8的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef8() {
	return def8;
}

/**
 * 属性def9的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getDef9() {
	return def9;
}

/**
 * 获得vo里第index个自定义项的值。
 * 典型实现:
 switch(index){
 case 1: return getDef1();
 case 2: return getDef2();
 case 3: return getDef3();
 ...
 }
 return null;
 
 * 创建日期：(01-5-14 10:41:57)
 * @return String
 * @param index int
 */
public String getDefValue(int index) {
	switch (index) {
		case 1: return getDef1();
		case 2: return getDef2();
		case 3: return getDef3();
		case 4: return getDef4();
		case 5: return getDef5();
		case 6: return getDef6();
		case 7: return getDef7();
		case 8: return getDef8();
		case 9: return getDef9();
		case 10: return getDef10();
		case 11: return getDef11();
		case 12: return getDef12();
		case 13: return getDef13();
		case 14: return getDef14();
		case 15: return getDef15();
		case 16: return getDef16();
		case 17: return getDef17();
		case 18: return getDef18();
		case 19: return getDef19();
		case 20: return getDef20();
	}

	return null;
}

/**
 * 属性ecotype的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return Integer
 */
public String getEcotype() {
	return ecotype;
}

/**
 * 属性email1的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getEmail1() {
	return email1;
}

/**
 * 属性email2的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getEmail2() {
	return email2;
}

/**
 * 属性email3的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getEmail3() {
	return email3;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return DZFDate
 */
public DZFDate getEnddate() {
	return enddate;
}

/**
 * 返回数值对象的显示名称。
 *
 * 创建日期：(2001-5-16)
 * @return String 返回数值对象的显示名称。
 */
public String getEntityName() {

	return "Corp";
}

/**
 * 属性fathercorp的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getFathercorp() {
	return fathercorp;
}

/**
 * 属性fax1的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getFax1() {
	return fax1;
}

/**
 * 属性fax2的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getFax2() {
	return fax2;
}

/**
 * 属性foreignname的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getForeignname() {
	return foreignname;
}

/**
 * 属性holdflag的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return DZFBoolean
 */
public DZFBoolean getHoldflag() {
	return holdflag;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return String
 */
public String getIdnumber() {
	return idnumber;
}

/**
 * 属性industry的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getIndustry() {
	return industry;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-12 15:24:21)
 * @return String
 */
public String getInnercode() {
	return innercode;
}

/**
 * 属性ishasaccount 的Getter方法。
 *
 * 创建日期：(2001-8-23)
 * @return DZFBoolean
 */
public DZFBoolean getIshasaccount() {
	return ishasaccount ;
}

/**
 * 属性isseal的Getter方法。
 *
 * 创建日期：(2001-8-23)
 * @return DZFBoolean
 */
public DZFBoolean getIsseal() {
	return isseal==null ? DZFBoolean.FALSE : isseal;
}


/**
 * 属性isworkingunit的Getter方法。
 *
 * 创建日期：(2001-8-23)
 * @return DZFBoolean
 */
public DZFBoolean getIsworkingunit() {
	return isworkingunit;
}

/**
 * 属性legalbodycode的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getLegalbodycode() {
	return legalbodycode;
}

/**
 * 属性linkman1的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getLinkman1() {
	return linkman1;
}

/**
 * 属性linkman2的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getLinkman2() {
	return linkman2;
}

/**
 * 属性linkman3的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getLinkman3() {
	return linkman3;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-12 15:24:42)
 * @return String
 */
public String getMaxinnercode() {
	return maxinnercode;
}

/**
 * 属性memo的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getMemo() {
	return memo;
}

/**
 * 属性ownersharerate的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return DZFDouble
 */
public DZFDouble getOwnersharerate() {
	if(ownersharerate==null)
	  ownersharerate=new DZFDouble(0);
	return ownersharerate;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2004-4-26 13:52:51)
 * @return String
 */
public String getParentPKFieldName() {
	return null;
}

/**
 * 属性phone1的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPhone1() {
	return phone1;
}

/**
 * 属性phone2的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPhone2() {
	return phone2;
}

/**
 * 属性phone3的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPhone3() {
	return phone3;
}

/**
 * 属性pk_corp的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPk_corp() {
	return pk_corp;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-13 10:50:11)
 * @return String
 */
public String getPk_corpkind() {
	return pk_corpkind;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @return String
 */
public String getPk_currency() {
	return pk_currency;
}

/**
 * 此处插入方法说明。
 * 创建日期：(04-3-1 16:51:19)
 * @return String
 * Chenwei
 */
public String getpkCorpkind() {
	//if(pk_corpkind == null || pk_corpkind.length() == 0) {
		//pk_corpkind = nc.bs.bd.CorpDMO.CORPKIND_COMMON;
	//}
	return pk_corpkind;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2004-4-26 13:53:19)
 * @return String
 */
public String getPKFieldName() {
	return "pk_corp";
}



/**
 * 属性postaddr的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPostaddr() {
	return postaddr;
}

/**
 * 返回对象标识，用来唯一定位对象。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getPrimaryKey() {

	return pk_corp;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @return String
 */
public String getProvince() {
	return province;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @return DZFDouble
 */
public DZFDouble getRegcapital() {
	return regcapital;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-6-18 11:53:28)
 * @return String
 */
public String getRegion() {
	return region;
}

/**
 * 属性saleaddr的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getSaleaddr() {
	return saleaddr;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 14:11:57)
 * @return DZFDate
 */
public DZFDate getSealeddate() {
	return sealeddate;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2004-4-26 13:53:52)
 * @return String
 */
public String getTableName() {
	return "bd_account";
}


public Integer getTaxpayertype() {
	return taxpayertype;
}

public DZFDateTime getTs() {
	return ts;
}

/**
 * 属性unitcode的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getUnitcode() {
	return unitcode;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2004-4-26 11:36:28)
 * @return Integer
 */
public String getUnitdistinction() {
	return unitdistinction;
}

/**
 * 属性unitname的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getUnitname() {
	return unitname;
}

/**
 * 属性unitshortname的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getUnitshortname() {
	return unitshortname;
}

/**
 * 属性url的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getUrl() {
	return url;
}

/**
 * 属性zipcode的Getter方法。
 *
 * 创建日期：(2001-5-16)
 * @return String
 */
public String getZipcode() {
	return zipcode;
}

/**
 * 属性showorder的Getter方法。
 *
 * 创建日期：(2008-7-29)
 * @return int
 */
public Integer getShoworder() {
	return showorder;
}

/**
 * accountproject的getter方法
 * @return
 */
//public String getAccountproject() {
//	return accountproject;
//}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 16:39:25)
 * @return boolean
 */
public boolean isBackup() {
	return m_isbackup;
}

/**
 * 判断是否是结算中心。
 * 创建日期：(2004-3-12 12:49:52)
 * @return boolean
 */
public boolean isSettleCenter() {
//	ISettleCenter iISettleCenter = (ISettleCenter) NCLocator.getInstance().lookup(ISettleCenter.class.getName());
//	return iISettleCenter.isSettleCenter(getPk_corp());
	return false;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 16:39:25)
 * @param newM_isbackup boolean
 */
public void setBackup(boolean newM_isbackup) {
	m_isbackup = newM_isbackup;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newBegindate DZFDate
 */
public void setBegindate(DZFDate newBegindate) {
	begindate = newBegindate;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @param newBriefintro String
 */
public void setBriefintro(String newBriefintro) {
	briefintro = newBriefintro;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newChargeDeptCode String
 */
public void setChargedeptcode(String newChargedeptcode) {
    chargedeptcode = newChargedeptcode;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newChargedeptname String
 */
public void setChargedeptname(String newChargedeptname) {
    chargedeptname = newChargedeptname;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @param newCitycounty String
 */
public void setCitycounty(String newCitycounty) {
	citycounty = newCitycounty;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 14:11:57)
 * @param newcorptype String
 */
public void setCorptype(String newcorptype) {
	corptype = newcorptype;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @param newCountryarea String
 */
public void setCountryarea(String newCountryarea) {
	countryarea = newCountryarea;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-4-18 16:05:35)
 * @param newCreatedate DZFDate
 */
public void setCreatedate(DZFDate newCreatedate) {
	createdate = newCreatedate;
}

/**
 * 属性def1的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef1 String
 */
public void setDef1(String newDef1) {

	def1 = newDef1;
}

/**
 * 属性def10的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef10 String
 */
public void setDef10(String newDef10) {

	def10 = newDef10;
}


/**
 * 属性def11的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef11 String
 */
public void setDef11(String newDef11) {

	def11 = newDef11;
}

/**
 * 属性def12的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef12 String
 */
public void setDef12(String newDef12) {

	def12 = newDef12;
}

/**
 * 属性def13的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef13 String
 */
public void setDef13(String newDef13) {

	def13 = newDef13;
}

/**
 * 属性def14的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef14 String
 */
public void setDef14(String newDef14) {

	def14 = newDef14;
}

/**
 * 属性def15的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef15 String
 */
public void setDef15(String newDef15) {

	def15 = newDef15;
}

/**
 * 属性def16的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef16 String
 */
public void setDef16(String newDef16) {

	def16 = newDef16;
}

/**
 * 属性def17的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef17 String
 */
public void setDef17(String newDef17) {

	def17 = newDef17;
}

/**
 * 属性def18的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef18 String
 */
public void setDef18(String newDef18) {

	def18 = newDef18;
}

/**
 * 属性def19的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef19 String
 */
public void setDef19(String newDef19) {

	def19 = newDef19;
}

/**
 * 属性def2的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef2 String
 */
public void setDef2(String newDef2) {

	def2 = newDef2;
}

/**
 * 属性def20的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef20 String
 */
public void setDef20(String newDef20) {

	def20 = newDef20;
}

/**
 * 属性def3的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef3 String
 */
public void setDef3(String newDef3) {

	def3 = newDef3;
}

/**
 * 属性def4的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef4 String
 */
public void setDef4(String newDef4) {

	def4 = newDef4;
}

/**
 * 属性def5的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef5 String
 */
public void setDef5(String newDef5) {

	def5 = newDef5;
}

/**
 * 属性def6的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef6 String
 */
public void setDef6(String newDef6) {

	def6 = newDef6;
}

/**
 * 属性def7的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef7 String
 */
public void setDef7(String newDef7) {

	def7 = newDef7;
}

/**
 * 属性def8的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef8 String
 */
public void setDef8(String newDef8) {

	def8 = newDef8;
}

/**
 * 属性def9的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newDef9 String
 */
public void setDef9(String newDef9) {

	def9 = newDef9;
}

/**
 * 向vo里第index个自定义项设置值。
 * 典型实现:
 switch(index){
 case 1: setDef1(value);break;
 case 2: setDef2(value);break;
 case 3: setDef3(value);break;
 ...
 }

 。
 * 创建日期：(01-5-14 10:42:17)
 * @param value String
 * @param index int
 */
public void setDefValue(String value, int index) {
	switch (index) {
		case 1:setDef1(value);break;
		case 2:setDef2(value);break;
		case 3:setDef3(value);break;
		case 4:setDef4(value);break;
		case 5:setDef5(value);break;
		case 6:setDef6(value);break;
		case 7:setDef7(value);break;
		case 8:setDef8(value);break;
		case 9:setDef9(value);break;
		case 10:setDef10(value);break;
		case 11:setDef11(value);break;
		case 12:setDef12(value);break;
		case 13:setDef13(value);break;
		case 14:setDef14(value);break;
		case 15:setDef15(value);break;
		case 16:setDef16(value);break;
		case 17:setDef17(value);break;
		case 18:setDef18(value);break;
		case 19:setDef19(value);break;
		case 20:setDef20(value);break;
		
	}
}

/**
 * 属性ecotype的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newEcotype Integer
 */
public void setEcotype(String newEcotype) {

	ecotype = newEcotype;
}

/**
 * 属性email1的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newEmail1 String
 */
public void setEmail1(String newEmail1) {

	email1 = newEmail1;
}

/**
 * 属性email2的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newEmail2 String
 */
public void setEmail2(String newEmail2) {

	email2 = newEmail2;
}

/**
 * 属性email3的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newEmail3 String
 */
public void setEmail3(String newEmail3) {

	email3 = newEmail3;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newEnddate DZFDate
 */
public void setEnddate(DZFDate newEnddate) {
	enddate = newEnddate;
}

/**
 * 属性fathercorp的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newFathercorp String
 */
public void setFathercorp(String newFathercorp) {

	fathercorp = newFathercorp;
}

/**
 * 属性fax1的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newFax1 String
 */
public void setFax1(String newFax1) {

	fax1 = newFax1;
}

/**
 * 属性fax2的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newFax2 String
 */
public void setFax2(String newFax2) {

	fax2 = newFax2;
}

/**
 * 属性foreignname的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newForeignname String
 */
public void setForeignname(String newForeignname) {

	foreignname = newForeignname;
}

/**
 * 属性holdflag的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newHoldflag DZFBoolean
 */
public void setHoldflag(DZFBoolean newHoldflag) {

	holdflag = newHoldflag;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newIdnumber String
 */
public void setIdnumber(String newIdnumber) {
	idnumber = newIdnumber;
}

/**
 * 属性industry的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newIndustry String
 */
public void setIndustry(String newIndustry) {

	industry = newIndustry;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-12 15:24:21)
 * @param newInnercode String
 */
public void setInnercode(String newInnercode) {
	innercode = newInnercode;
}

/**
 * 属性ishasaccount 的setter方法。
 *
 * 创建日期：(2001-8-23)
 * @param newishasaccount  DZFBoolean
 */
public void setIshasaccount(DZFBoolean newIshasaccount) {

	ishasaccount  = newIshasaccount;
}

/**
 * 属性isseal的setter方法。
 *
 * 创建日期：(2001-8-23)
 * @param newisseal DZFBoolean
 */
public void setIsseal(DZFBoolean newIsseal) {

	isseal = newIsseal;
}


/**
 * 属性isworkingunit的setter方法。
 *
 * 创建日期：(2001-8-23)
 * @param newisworkingunit DZFBoolean
 */
public void setIsworkingunit(DZFBoolean newIsworkingunit) {

	isworkingunit = newIsworkingunit;
}


/**
 * 属性legalbodycode的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newLegalbodycode String
 */
public void setLegalbodycode(String newLegalbodycode) {

	legalbodycode = newLegalbodycode;
}

/**
 * 属性linkman1的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newLinkman1 String
 */
public void setLinkman1(String newLinkman1) {

	linkman1 = newLinkman1;
}

/**
 * 属性linkman2的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newLinkman2 String
 */
public void setLinkman2(String newLinkman2) {

	linkman2 = newLinkman2;
}

/**
 * 属性linkman3的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newLinkman3 String
 */
public void setLinkman3(String newLinkman3) {

	linkman3 = newLinkman3;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-12 15:24:42)
 * @param newMaxinnercode String
 */
public void setMaxinnercode(String newMaxinnercode) {
	maxinnercode = newMaxinnercode;
}

/**
 * 属性memo的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newMemo String
 */
public void setMemo(String newMemo) {

	memo = newMemo;
}

/**
 * 属性ownersharerate的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newOwnersharerate DZFDouble
 */
public void setOwnersharerate(DZFDouble newOwnersharerate) {

	ownersharerate = newOwnersharerate;
}

/**
 * 属性phone1的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newPhone1 String
 */
public void setPhone1(String newPhone1) {

	phone1 = newPhone1;
}

/**
 * 属性phone2的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newPhone2 String
 */
public void setPhone2(String newPhone2) {

	phone2 = newPhone2;
}

/**
 * 属性phone3的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newPhone3 String
 */
public void setPhone3(String newPhone3) {

	phone3 = newPhone3;
}

/**
 * 属性pk_corp的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newPk_corp String
 */
public void setPk_corp(String newPk_corp) {

	pk_corp = newPk_corp;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-12-13 10:50:11)
 * @param newPk_corpkind String
 */
public void setPk_corpkind(String newPk_corpkind) {
	pk_corpkind = newPk_corpkind;
}

/**
 * --------------------------------------------------
 * 功能：
 * 
 * 
 * 输入：
 * 
 * 输出：
 * 
 * 异常：
 * 
 * 补充：
 * 
 * 
 * 创建日期：(2003-10-31 8:57:13)
 * --------------------------------------------------
 * @param newPk_currency String
 */
public void setPk_currency(String newPk_currency) {
	pk_currency = newPk_currency;
}

/**
 * 此处插入方法说明。
 * 创建日期：(04-3-1 16:51:19)
 * @param  newPkCorpkind String
 * Chenwei
 */
public void setpkCorpkind(String newPkCorpkind) {
	pk_corpkind =  newPkCorpkind;
}



/**
 * 属性postaddr的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newPostaddr String
 */
public void setPostaddr(String newPostaddr) {

	postaddr = newPostaddr;
}

/**
 * 设置对象标识，用来唯一定位对象。
 *
 * 创建日期：(2001-5-16)
 * @param pk_corp String 
 */
public void setPrimaryKey(String newPk_corp) {

	pk_corp = newPk_corp;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @param newProvince String
 */
public void setProvince(String newProvince) {
	province = newProvince;
}

/**
 * 此处插入方法说明。
 * 创建日期：(02-5-23 13:51:19)
 * @param newRegcapital DZFDouble
 */
public void setRegcapital(DZFDouble newRegcapital) {
	regcapital = newRegcapital;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-6-18 11:53:54)
 * @param newRegion String
 */
public void setRegion(String newRegion) {

	region = newRegion;
}

/**
 * 属性saleaddr的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newSaleaddr String
 */
public void setSaleaddr(String newSaleaddr) {

	saleaddr = newSaleaddr;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2003-5-20 14:11:57)
 * @param newsealeddate DZFDate
 */
public void setSealeddate(DZFDate newsealeddate) {
	sealeddate = newsealeddate;
}

public void setTaxpayertype(Integer taxpayertype) {
	this.taxpayertype = taxpayertype;
}

public void setTs(DZFDateTime ts) {
	this.ts = ts;
}

/**
 * 属性unitcode的setter方法。
 *
 * 创建日期：(2001-5-16)
 * @param newUnitcode String
 */
public void setUnitcode(String newUnitcode) {

	unitcode = newUnitcode;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2004-4-26 11:36:59)
 * @param newUnitdistinction Integer
 */
public void setUnitdistinction(String newUnitdistinction) {
	unitdistinction = newUnitdistinction;
}

	/**
	 * 属性unitname的setter方法。
	 *
	 * 创建日期：(2001-5-16)
	 * @param newUnitname String
	 */
	public void setUnitname(String newUnitname) {
	
		unitname = newUnitname;
	}
	/**
	 * 属性unitshortname的setter方法。
	 *
	 * 创建日期：(2001-5-16)
	 * @param newUnitshortname String
	 */
	public void setUnitshortname(String newUnitshortname) {
	
		unitshortname = newUnitshortname;
	}

	/**
	 * 属性url的setter方法。
	 *
	 * 创建日期：(2001-5-16)
	 * @param newUrl String
	 */
	public void setUrl(String newUrl) {
	
		url = newUrl;
	}

	/**
	 * 属性zipcode的setter方法。
	 *
	 * 创建日期：(2001-5-16)
	 * @param newZipcode String
	 */
	public void setZipcode(String newZipcode) {
	
		zipcode = newZipcode;
	}

	/**
	 * 属性showorder_id的setter方法。
	 *
	 * 创建日期：(2008-7-29)
	 * @param showorder int
	 */
	public void setShoworder(Integer showorder) {
	
		this.showorder = showorder;
	}

	/**
	 * accountproject的setter方法
	 * @return
	 */
//	public void setAccountproject(String accountproject) {
//		this.accountproject = accountproject;
//	}

	/**
	 * 返回公司名称。
	 * 创建日期：(2001-8-28 16:20:40)
	 * @return String
	 */
	public String toString() {
		return unitname;
	}



	public DZFBoolean getIsaccountcorp() {
		return isaccountcorp;
	}

	public void setIsaccountcorp(DZFBoolean isaccountcorp) {
		this.isaccountcorp = isaccountcorp;
	}

	public DZFBoolean getIsdatacorp() {
		return isdatacorp;
	}

	public void setIsdatacorp(DZFBoolean isdatacorp) {
		this.isdatacorp = isdatacorp;
	}

	public DZFBoolean getIscurr() {
		return iscurr;
	}

	public void setIscurr(DZFBoolean iscurr) {
		this.iscurr = iscurr;
	}

	public Integer getIcostforwardstyle() {
		return icostforwardstyle;
	}

	public void setIcostforwardstyle(Integer icostforwardstyle) {
		this.icostforwardstyle = icostforwardstyle;
	}

//	public DZFBoolean getBbuildic() {
//		return bbuildic;
//	}
//
//	public void setBbuildic(DZFBoolean bbuildic) {
//		this.bbuildic = bbuildic;
//	}

	public String getTaxcode() {
		return taxcode;
	}

	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}

	public DZFDate getBusibegindate() {
		return busibegindate;
	}

	public void setBusibegindate(DZFDate busibegindate) {
		this.busibegindate = busibegindate;
	}

	public DZFDate getBusienddate() {
		return busienddate;
	}

	public void setBusienddate(DZFDate busienddate) {
		this.busienddate = busienddate;
	}
	
	public DZFBoolean getIsuseretail() {
		return isuseretail == null ? DZFBoolean.FALSE : isuseretail;
	}
	
	public void setIsuseretail(DZFBoolean isuseretail) {
		this.isuseretail = isuseretail;
	}

	public DZFDate getIcbegindate() {
		return icbegindate;
	}

	public void setIcbegindate(DZFDate icbegindate) {
		this.icbegindate = icbegindate;
	}

	public String getVcustsource() {
		return vcustsource;
	}

	public void setVcustsource(String vcustsource) {
		this.vcustsource = vcustsource;
	}

	public String getVsourcenote() {
		return vsourcenote;
	}

	public void setVsourcenote(String vsourcenote) {
		this.vsourcenote = vsourcenote;
	}

	public String getForeignid() {
		return foreignid;
	}

	public void setForeignid(String foreignid) {
		this.foreignid = foreignid;
	}

	public DZFBoolean getBkpnode() {
		return bkpnode;
	}

	public void setBkpnode(DZFBoolean bkpnode) {
		this.bkpnode = bkpnode;
	}

	public String getDlsname() {
		return dlsname;
	}

	public void setDlsname(String dlsname) {
		this.dlsname = dlsname;
	}

	public DZFBoolean getIschannel() {
		return ischannel;
	}

	public void setIschannel(DZFBoolean ischannel) {
		this.ischannel = ischannel;
	}

	
}