package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.pjgl.IGlobalPZVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 银行对账单
 * @author reny
 *
 */
public class BankStatementVO2 extends SuperVO implements IGlobalPZVO {
	
	@JsonProperty("id")
	private String pk_bankstatement;//主键
	@JsonProperty("cid")
	private String coperatorid;//操作人
	@JsonProperty("ddate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("corpid")
	private String pk_corp;//公司pk
	private String batchflag;//操作批次
	
	@JsonProperty("bankaccid")
	private String pk_bankaccount;//银行账户档案pk
	@JsonProperty("jyrq")
	private DZFDate tradingdate;//交易日期
	private String zy;//摘要
	
	@JsonProperty("yhsyje")
	private DZFDouble syje;//收入金额
	@JsonProperty("yhzcje")
	private DZFDouble zcje;//支出金额
	@JsonProperty("dfzhmc")
	private String othaccountname;//对方账户名称
	@JsonProperty("dfzhbm")
	private String othaccountcode;//对方账户
	@JsonProperty("yfye")
	private DZFDouble ye;//余额
	private String pzh;//凭证号
	@JsonProperty("tzpzid")
	private String pk_tzpz_h;//凭证pk
	@JsonProperty("lylx")
	private int sourcetem;//数据来源
	
	private int sourcetype;//来源
	@JsonProperty("qj")
	private String period;//期间
	@JsonProperty("inqj")
	private String inperiod;//入账期间
	@JsonProperty("status")
	private int billstatus;//单据状态
	
	private String modifyoperid;//修改人pk
	private DZFDateTime modifydatetime;//修改时间
	private int dr;
	private DZFDateTime ts;
	
	@JsonProperty("busitypetempid")
	private String pk_model_h;//业务类型pk(类别主键)
	@JsonProperty("busitempname")
	private String busitypetempname;//业务类型模板名称
	
	private Integer settlement;//结算方式 0往来 1现金 2银行
	private String pk_subject;//入账科目
	private String pk_settlementaccsubj;//结算科目
	
	private String sourcebillid;//来源单据id
	@JsonProperty("ipath")
	private String imgpath;//图片路径
	private String pk_image_group;//图片组号
	private String pk_image_library;//图片子表id
//	private String memo;//备注
	private  int count; //发票数量
	private String pk_category_keyword;//关键字主键
	private String bankname;//本方账户名称（展示）
	private String bankaccount;//本方账户（展示）
	
	private DZFDouble version;//版本  区别在线会计老版本数据
	
	private String szflag;		//收支标志 数据库中没有，仅用于计算
	private String fufangdwmc;	//付方单位名称    数据库中没有，仅用于计算
	private String shoufangdwmc;//收方单位名称   数据库中没有，仅用于计算
	//自定义项1~15
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private String vdef9;
	private String vdef10;//分类时临时用
	private String vdef11;
	private String vdef12;
	private String vdef13;//存pk_invoice
	private String vdef14;
	private String vdef15;
	
	private int serialNum;//导入临时存储的行号
	private String tempvalue;//临时使用字段，不存库
	private String yhzhcode;//银行账号
	private int yhdzdsl;//银行对账单数量 不存库
	private int yhhdsl;//银行回单数量 不存库
	private int wsdhdsl;//未收到回单数量 不存库
	
	private String aliname;//key
	//查询期间
	private String serdate;//选择的期间
	private String startyear1;//交易日期起始年
	private String startmonth1;//交易日期起始月
	private String endyear1;//交易日期终止年
	private String endmonth1;//交易日期终止月
	private String startyear2;//认证日期起始年
	private String startmonth2;//认证日期起始月
	private String endyear2;//认证日期终止年
	private String endmonth2;//认证日期终止月
	private String ispz;//是否生成凭证
	private DZFBoolean isFlag;//是否强制导入
	private String inoutflag;//收支类型 0:无方向1：收入(销售)2: 支出(采购
	
	private String mony;//金额 显示用
	
	@JsonProperty("bfzhmc")
	private String accountname;//本分账户名称显示用
	@JsonProperty("bfzhbm")
	private String accountcode;//本分账户显示用
	
	private String tradecode;//交易流水号 显示用



	private String flag;	//原查询时的参数
	//常量
	public static final int SOURCE_0 = 0;//手工
	public static final int SOURCE_1 = 1;//来源通用模板
	public static final int SOURCE_2 = 2;//来源中国银行
	public static final int SOURCE_3 = 3;//来源工商银行
	public static final int SOURCE_4 = 4;//来源北京农商银行
	public static final int SOURCE_5 = 5;//来源江苏民丰农商行 
	public static final int SOURCE_6 = 6;//来源中国农业银行
	public static final int SOURCE_7 = 7;//来源重庆银行
	public static final int SOURCE_8 = 8;//来源重庆农村商业银行
	public static final int SOURCE_9 = 9;//来源中国农业银行—苏州支行
	public static final int SOURCE_10 = 10;//来源苏州银行
	public static final int SOURCE_11 = 11;//来源中国建设银行
	public static final int SOURCE_12 = 12;//来源中国建设银行苏州分行
	public static final int SOURCE_13 = 13;//来源青岛银行
	
	public static final int SOURCE_14 = 14;//来源兴业银行青岛分行
	public static final int SOURCE_15 = 15;//来源渤海银行
	public static final int SOURCE_16 = 16;//来源交通银行
	public static final int SOURCE_17 = 17;//来源青岛农村商业银行
	public static final int SOURCE_18 = 18;//来源招商银行杭州分行
	public static final int SOURCE_19 = 19;//来源民生银行
	
	public static final int SOURCE_20 = 20;//来源浙江民泰商业银行
	public static final int SOURCE_21 = 21;//来源成都农商银行
	public static final int SOURCE_22 = 22;//来源库尔勒银行
	public static final int SOURCE_23 = 23;//来源昆仑银行
	public static final int SOURCE_24 = 24;//来源北京银行
	public static final int SOURCE_25 = 25;//来源华夏银行
	public static final int SOURCE_26 = 26;//来源中国建设银行成都分行
	public static final int SOURCE_27 = 27;//来源兰州银行
	public static final int SOURCE_28 = 28;//来源交通银行上海分行
	public static final int SOURCE_29 = 29;//来源南京银行上海分行
	public static final int SOURCE_30 = 30;//来源工商银行上海分行
	public static final int SOURCE_31 = 31;//来源上海银行
	public static final int SOURCE_32 = 32;//来源中国民生银行成都分行
	public static final int SOURCE_33 = 33;//来源交通银行长春地区
	
	public static final int SOURCE_34 = 34;//来源中国工商银行重庆分行
	public static final int SOURCE_35 = 35;//来源中国工商银行江西分行
	public static final int SOURCE_36 = 36;//来源中国工商银行苏州分行
	public static final int SOURCE_37 = 37;//来源中国工商银行成都分行
	public static final int SOURCE_38 = 38;//来源中国工商银行泉州分行
	public static final int SOURCE_39 = 39;//来源中国银行上海分行
	public static final int SOURCE_40 = 40;//来源中国银行宿迁分行
	public static final int SOURCE_41 = 41;//来源吉林九台农村商业银行
	public static final int SOURCE_42 = 42;//来源中国建设银行临河分行
	public static final int SOURCE_43 = 43;//来源中国农业银行临河分行
	public static final int SOURCE_44 = 44;//来源中国银行临河分行
	public static final int SOURCE_45 = 45;//来源中国工商银行石狮分行
	public static final int SOURCE_46 = 46;//来源中国农业银行乌鲁木齐支行
	public static final int SOURCE_47 = 47;//来源中国民生银行太原支行
	
	public static final int SOURCE_48 = 48;//来源平安银行深圳分行
	public static final int SOURCE_49 = 49;//来源兴业银行深圳分行
	public static final int SOURCE_50 = 50;//来源浦东发展银行深圳分行
	public static final int SOURCE_51 = 51;//来源深圳农村商业银行
	public static final int SOURCE_52 = 52;//来源中国银行内蒙古分行
	public static final int SOURCE_53 = 53;//来源长沙银行
	public static final int SOURCE_54 = 54;//来源济宁银行泰安分行
	public static final int SOURCE_55 = 55;//来源中国工商银行泰安分行
	public static final int SOURCE_56 = 56;//平安银行长沙分行
	public static final int SOURCE_57 = 57;//中原银行西峡支行
	public static final int SOURCE_58 = 58;//长沙农商银行
	
	public static final int SOURCE_100= 100;//来源于上传图片/扫描客户端
	
	public static final int SOURCE_999 = 999;//来源于通用银行模板
	/**************************数据的演变规律********************/
	public static final int STATUS_0 = 0;//银行对账单导入、录入  未绑定
	public static final int STATUS_1 = 1;//回单回传， 未绑定
	public static final int STATUS_2 = 2;//银行对账单、回单 绑定
	
//	public String getMemo() {
//		return memo;
//	}
//
//	public void setMemo(String memo) {
//		this.memo = memo;
//	}

	
	
	
	public String getPk_image_group() {
		return pk_image_group;
	}


	public String getInoutflag() {
		return inoutflag;
	}


	public void setInoutflag(String inoutflag) {
		this.inoutflag = inoutflag;
	}


	public DZFDouble getVersion() {
		return version;
	}



	public void setVersion(DZFDouble version) {
		this.version = version;
	}



	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public String getTradecode() {
		return tradecode;
	}

	public void setTradecode(String tradecode) {
		this.tradecode = tradecode;
	}

	public String getMony() {
		return mony;
	}

	public void setMony(String mony) {
		this.mony = mony;
	}

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public String getPk_category_keyword() {
		return pk_category_keyword;
	}

	public void setPk_category_keyword(String pk_category_keyword) {
		this.pk_category_keyword = pk_category_keyword;
	}

	public String getAliname() {
		return aliname;
	}

	public void setAliname(String aliname) {
		this.aliname = aliname;
	}

	public String getIspz() {
		return ispz;
	}

	public void setIspz(String ispz) {
		this.ispz = ispz;
	}

	public DZFBoolean getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(DZFBoolean isFlag) {
		this.isFlag = isFlag;
	}

	public String getInperiod() {
		return inperiod;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setInperiod(String inperiod) {
		this.inperiod = inperiod;
	}

	public String getYhzhcode() {
		return yhzhcode;
	}

	public void setYhzhcode(String yhzhcode) {
		this.yhzhcode = yhzhcode;
	}

	public int getYhdzdsl() {
		return yhdzdsl;
	}

	public int getYhhdsl() {
		return yhhdsl;
	}

	public int getWsdhdsl() {
		return wsdhdsl;
	}

	public void setYhdzdsl(int yhdzdsl) {
		this.yhdzdsl = yhdzdsl;
	}

	public void setYhhdsl(int yhhdsl) {
		this.yhhdsl = yhhdsl;
	}

	public void setWsdhdsl(int wsdhdsl) {
		this.wsdhdsl = wsdhdsl;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public String getPk_bankstatement() {
		return pk_bankstatement;
	}

	public void setPk_bankstatement(String pk_bankstatement) {
		this.pk_bankstatement = pk_bankstatement;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getBatchflag() {
		return batchflag;
	}

	public void setBatchflag(String batchflag) {
		this.batchflag = batchflag;
	}

	public String getPk_bankaccount() {
		return pk_bankaccount;
	}

	public void setPk_bankaccount(String pk_bankaccount) {
		this.pk_bankaccount = pk_bankaccount;
	}

	public DZFDate getTradingdate() {
		return tradingdate;
	}

	public void setTradingdate(DZFDate tradingdate) {
		this.tradingdate = tradingdate;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}
	
	public int getBillstatus() {
		return billstatus;
	}

	public void setBillstatus(int billstatus) {
		this.billstatus = billstatus;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	public DZFDouble getSyje() {
		return syje;
	}

	public void setSyje(DZFDouble syje) {
		this.syje = syje;
	}

	public DZFDouble getZcje() {
		return zcje;
	}

	public void setZcje(DZFDouble zcje) {
		this.zcje = zcje;
	}

	public String getOthaccountname() {
		return othaccountname;
	}

	public void setOthaccountname(String othaccountname) {
		this.othaccountname = othaccountname;
	}

	public String getOthaccountcode() {
		return othaccountcode;
	}

	public void setOthaccountcode(String othaccountcode) {
		this.othaccountcode = othaccountcode;
	}

	public DZFDouble getYe() {
		return ye;
	}

	public void setYe(DZFDouble ye) {
		this.ye = ye;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public int getSourcetem() {
		return sourcetem;
	}

	public void setSourcetem(int sourcetem) {
		this.sourcetem = sourcetem;
	}

	public int getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(int sourcetype) {
		this.sourcetype = sourcetype;
	}

	public String getVdef1() {
		return vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public String getVdef9() {
		return vdef9;
	}

	public String getVdef10() {
		return vdef10;
	}

	public String getVdef11() {
		return vdef11;
	}

	public String getVdef12() {
		return vdef12;
	}

	public String getVdef13() {
		return vdef13;
	}

	public String getVdef14() {
		return vdef14;
	}

	public String getVdef15() {
		return vdef15;
	}

	public String getTempvalue() {
		return tempvalue;
	}

	public void setTempvalue(String tempvalue) {
		this.tempvalue = tempvalue;
	}

	public void setVdef11(String vdef11) {
		this.vdef11 = vdef11;
	}

	public void setVdef12(String vdef12) {
		this.vdef12 = vdef12;
	}

	public void setVdef13(String vdef13) {
		this.vdef13 = vdef13;
	}

	public void setVdef14(String vdef14) {
		this.vdef14 = vdef14;
	}

	public void setVdef15(String vdef15) {
		this.vdef15 = vdef15;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}


	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getModifyoperid() {
		return modifyoperid;
	}

	public void setModifyoperid(String modifyoperid) {
		this.modifyoperid = modifyoperid;
	}

	public DZFDateTime getModifydatetime() {
		return modifydatetime;
	}

	public void setModifydatetime(DZFDateTime modifydatetime) {
		this.modifydatetime = modifydatetime;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	public String getStartyear1() {
		return startyear1;
	}

	public String getStartmonth1() {
		return startmonth1;
	}

	public String getEndyear1() {
		return endyear1;
	}

	public String getEndmonth1() {
		return endmonth1;
	}

	public void setStartyear1(String startyear1) {
		this.startyear1 = startyear1;
	}

	public void setStartmonth1(String startmonth1) {
		this.startmonth1 = startmonth1;
	}

	public void setEndyear1(String endyear1) {
		this.endyear1 = endyear1;
	}

	public void setEndmonth1(String endmonth1) {
		this.endmonth1 = endmonth1;
	}
	
	public String getStartyear2() {
		return startyear2;
	}



	public void setStartyear2(String startyear2) {
		this.startyear2 = startyear2;
	}



	public String getStartmonth2() {
		return startmonth2;
	}



	public void setStartmonth2(String startmonth2) {
		this.startmonth2 = startmonth2;
	}



	public String getEndyear2() {
		return endyear2;
	}



	public void setEndyear2(String endyear2) {
		this.endyear2 = endyear2;
	}



	public String getEndmonth2() {
		return endmonth2;
	}



	public void setEndmonth2(String endmonth2) {
		this.endmonth2 = endmonth2;
	}



	public String getSerdate() {
		return serdate;
	}



	public void setSerdate(String serdate) {
		this.serdate = serdate;
	}



	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	@Override
	public String getPKFieldName() {
		return "pk_bankstatement";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_bankstatement";
	}

	@Override
	public DZFDouble getTotalmny() {//获取金额
		return SafeCompute.add(getSyje(), getZcje());
	}

	@Override
	public DZFDouble getMny() {//获取金额
		return SafeCompute.add(getSyje(), getZcje());
	}

	@Override
	public DZFDouble getWsmny() {//获取金额
		return SafeCompute.add(getSyje(), getZcje());
	}

	@Override
	public DZFDouble getSmny() {//获取金额
		return SafeCompute.add(getSyje(), getZcje());
	}

	public String getSzflag() {
		return szflag;
	}

	public void setSzflag(String szflag) {
		this.szflag = szflag;
	}

	public String getFufangdwmc() {
		return fufangdwmc;
	}

	public String getShoufangdwmc() {
		return shoufangdwmc;
	}

	public void setFufangdwmc(String fufangdwmc) {
		this.fufangdwmc = fufangdwmc;
	}

	public void setShoufangdwmc(String shoufangdwmc) {
		this.shoufangdwmc = shoufangdwmc;
	}



	public Integer getSettlement() {
		return settlement;
	}



	public void setSettlement(Integer settlement) {
		this.settlement = settlement;
	}



	public String getPk_subject() {
		return pk_subject;
	}



	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}



	public String getPk_settlementaccsubj() {
		return pk_settlementaccsubj;
	}



	public void setPk_settlementaccsubj(String pk_settlementaccsubj) {
		this.pk_settlementaccsubj = pk_settlementaccsubj;
	}
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}
