package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 期末结账时生成科目的期初、期末数
 * 创建日期:2014-10-16 15:49:56
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class KMQMJZVO extends SuperVO {
	private String pk_corp;
	private DZFDateTime ts;
	private String vdef9;
	@JsonProperty("coid")
	private String coperatorid;
	
	private String vdef8;
	private String vname;
	private String vdef10;
	@JsonProperty("jfse")
	private DZFDouble jffse;
	@JsonProperty("ybjfse")
	private DZFDouble ybjffse;
	@JsonProperty("ybdfse")
	private DZFDouble ybdffse;
	@JsonProperty("tmqm")
	private DZFDouble thismonthqm;
	private String vapproveid;
	private String vapprovenote;
	private String vcode;
	@JsonProperty("tmqc")
	private DZFDouble thismonthqc;
	private String vdef7;
	private DZFDate dapprovedate;
	@JsonProperty("kmqmjzid")
	private String pk_kmqmjz;
	@JsonProperty("accsubjid")
	private String pk_accsubj;
	private Integer vdirect;
	private String vdef2;
	private String vdef5;
	private String pk_billtype;
	private Integer vbillstatus;
	private String memo;
	private String pk_qmjz;
	private String vdef3;
	private String vdef6;
	private String vbillno;
	private Integer dr;
	private DZFDate doperatedate;
	private String dcurrency;
	private String vdef4;
	@JsonProperty("dfse")
	private DZFDouble dffse;
	@JsonProperty("accode")
	private String accountcode;
	@JsonProperty("acname")
	private String accountname;
	private String operatorid;
	
	//期间
	private String period ;

	private String kmmc;
	private String kmbm;
	private String pk_currency ;
	private DZFDouble nrate;
	private DZFDouble ybjfmny;//原币借方金额
	private DZFDouble ybdfmny;//原币贷方金额
	private DZFDouble ybthismonthqc;//原币本月期初
	private DZFDouble ybthismonthqm;//原币本月期末
	
	//辅助核算
	private String vdef1;//是否辅助核算
	private String fzhsx1;
	private String fzhsx2;
	private String fzhsx3;
	private String fzhsx4;
	private String fzhsx5;
	private String fzhsx6;
	private String fzhsx7;
	private String fzhsx8;
	private String fzhsx9;
	private String fzhsx10;
	
	//---------结账数量 待完善
	private DZFDouble  bnqcnum;//本年期初数量
	private DZFDouble  bnfsnum;//本年借方发生数量
	private DZFDouble  bndffsnum;//本年贷方发生数量
	private DZFDouble  monthqmnum;//本月期初数量
	
	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VNAME = "vname";
	public static final String VDEF10 = "vdef10";
	public static final String JFFSE = "jffse";
	public static final String THISMONTHQM = "thismonthqm";
	public static final String VAPPROVEID = "vapproveid";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String VCODE = "vcode";
	public static final String THISMONTHQC = "thismonthqc";
	public static final String VDEF7 = "vdef7";
	public static final String DAPPROVEDATE = "dapprovedate";
	public static final String PK_KMQMJZ = "pk_kmqmjz";
	public static final String PK_ACCSUBJ = "pk_accsubj";
	public static final String VDIRECT = "vdirect";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String VBILLSTATUS = "vbillstatus";
	public static final String MEMO = "memo";
	public static final String PK_QMJZ = "pk_qmjz";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String VBILLNO = "vbillno";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String DCURRENCY = "dcurrency";
	public static final String VDEF4 = "vdef4";
	public static final String DFFSE = "dffse";
	
			
	public DZFDouble getBnqcnum() {
		return bnqcnum;
	}
	public void setBnqcnum(DZFDouble bnqcnum) {
		this.bnqcnum = bnqcnum;
	}
	public DZFDouble getBnfsnum() {
		return bnfsnum;
	}
	public void setBnfsnum(DZFDouble bnfsnum) {
		this.bnfsnum = bnfsnum;
	}
	public DZFDouble getBndffsnum() {
		return bndffsnum;
	}
	public void setBndffsnum(DZFDouble bndffsnum) {
		this.bndffsnum = bndffsnum;
	}
	public DZFDouble getMonthqmnum() {
		return monthqmnum;
	}
	public void setMonthqmnum(DZFDouble monthqmnum) {
		this.monthqmnum = monthqmnum;
	}
	public DZFDouble getYbjffse() {
		return ybjffse;
	}
	public void setYbjffse(DZFDouble ybjffse) {
		this.ybjffse = ybjffse;
	}
	public DZFDouble getYbdffse() {
		return ybdffse;
	}
	public void setYbdffse(DZFDouble ybdffse) {
		this.ybdffse = ybdffse;
	}
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vname的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVname () {
		return vname;
	}   
	/**
	 * 属性vname的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVname String
	 */
	public void setVname (String newVname ) {
	 	this.vname = newVname;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性jffse的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public DZFDouble getJffse () {
		return jffse;
	}   
	/**
	 * 属性jffse的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newJffse DZFDouble
	 */
	public void setJffse (DZFDouble newJffse ) {
	 	this.jffse = newJffse;
	} 	  
	/**
	 * 属性thismonthqm的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public DZFDouble getThismonthqm () {
		return thismonthqm;
	}   
	/**
	 * 属性thismonthqm的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newThismonthqm DZFDouble
	 */
	public void setThismonthqm (DZFDouble newThismonthqm ) {
	 	this.thismonthqm = newThismonthqm;
	} 	  
	/**
	 * 属性vapproveid的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVapproveid () {
		return vapproveid;
	}   
	/**
	 * 属性vapproveid的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVapproveid String
	 */
	public void setVapproveid (String newVapproveid ) {
	 	this.vapproveid = newVapproveid;
	} 	  
	/**
	 * 属性vapprovenote的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVapprovenote () {
		return vapprovenote;
	}   
	/**
	 * 属性vapprovenote的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVapprovenote String
	 */
	public void setVapprovenote (String newVapprovenote ) {
	 	this.vapprovenote = newVapprovenote;
	} 	  
	/**
	 * 属性vcode的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVcode () {
		return vcode;
	}   
	/**
	 * 属性vcode的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVcode String
	 */
	public void setVcode (String newVcode ) {
	 	this.vcode = newVcode;
	} 	  
	/**
	 * 属性thismonthqc的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public DZFDouble getThismonthqc () {
		return thismonthqc;
	}   
	/**
	 * 属性thismonthqc的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newThismonthqc DZFDouble
	 */
	public void setThismonthqc (DZFDouble newThismonthqc ) {
	 	this.thismonthqc = newThismonthqc;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
	/**
	 * 属性dapprovedate的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate () {
		return dapprovedate;
	}   
	/**
	 * 属性dapprovedate的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newDapprovedate DZFDate
	 */
	public void setDapprovedate (DZFDate newDapprovedate ) {
	 	this.dapprovedate = newDapprovedate;
	} 	  
	/**
	 * 属性pk_kmqmjz的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getPk_kmqmjz () {
		return pk_kmqmjz;
	}   
	/**
	 * 属性pk_kmqmjz的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newPk_kmqmjz String
	 */
	public void setPk_kmqmjz (String newPk_kmqmjz ) {
	 	this.pk_kmqmjz = newPk_kmqmjz;
	} 	  
	/**
	 * 属性pk_accsubj的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getPk_accsubj () {
		return pk_accsubj;
	}   
	/**
	 * 属性pk_accsubj的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newPk_accsubj String
	 */
	public void setPk_accsubj (String newPk_accsubj ) {
	 	this.pk_accsubj = newPk_accsubj;
	} 	  
	/**
	 * 属性vdirect的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public Integer getVdirect () {
		return vdirect;
	}   
	/**
	 * 属性vdirect的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdirect DZFDouble
	 */
	public void setVdirect (Integer newVdirect ) {
	 	this.vdirect = newVdirect;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性pk_billtype的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * 属性pk_billtype的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newPk_billtype String
	 */
	public void setPk_billtype (String newPk_billtype ) {
	 	this.pk_billtype = newPk_billtype;
	} 	  
	/**
	 * 属性vbillstatus的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public Integer getVbillstatus () {
		return vbillstatus;
	}   
	/**
	 * 属性vbillstatus的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVbillstatus DZFDouble
	 */
	public void setVbillstatus (Integer newVbillstatus ) {
	 	this.vbillstatus = newVbillstatus;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性pk_qmjz的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getPk_qmjz () {
		return pk_qmjz;
	}   
	/**
	 * 属性pk_qmjz的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newPk_qmjz String
	 */
	public void setPk_qmjz (String newPk_qmjz ) {
	 	this.pk_qmjz = newPk_qmjz;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性vbillno的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVbillno () {
		return vbillno;
	}   
	/**
	 * 属性vbillno的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVbillno String
	 */
	public void setVbillno (String newVbillno ) {
	 	this.vbillno = newVbillno;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dcurrency的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getDcurrency () {
		return dcurrency;
	}   
	/**
	 * 属性dcurrency的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newDcurrency String
	 */
	public void setDcurrency (String newDcurrency ) {
	 	this.dcurrency = newDcurrency;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性dffse的Getter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @return DZFDouble
	 */
	public DZFDouble getDffse () {
		return dffse;
	}   
	/**
	 * 属性dffse的Setter方法.
	 * 创建日期:2014-10-16 15:49:56
	 * @param newDffse DZFDouble
	 */
	public void setDffse (DZFDouble newDffse ) {
	 	this.dffse = newDffse;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-10-16 15:49:56
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-10-16 15:49:56
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_kmqmjz";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-10-16 15:49:56
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "YNT_KMQMJZ";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-10-16 15:49:56
	  */
     public KMQMJZVO() {
		super();	
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public DZFDouble getNrate() {
		return nrate;
	}
	public void setNrate(DZFDouble nrate) {
		this.nrate = nrate;
	}
	public DZFDouble getYbjfmny() {
		return ybjfmny;
	}
	public void setYbjfmny(DZFDouble ybjfmny) {
		this.ybjfmny = ybjfmny;
	}
	public DZFDouble getYbdfmny() {
		return ybdfmny;
	}
	public void setYbdfmny(DZFDouble ybdfmny) {
		this.ybdfmny = ybdfmny;
	}
	public String getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	public DZFDouble getYbthismonthqc() {
		return ybthismonthqc;
	}
	public void setYbthismonthqc(DZFDouble ybthismonthqc) {
		this.ybthismonthqc = ybthismonthqc;
	}
	public DZFDouble getYbthismonthqm() {
		return ybthismonthqm;
	}
	public void setYbthismonthqm(DZFDouble ybthismonthqm) {
		this.ybthismonthqm = ybthismonthqm;
	}
	public String getKmmc() {
		return kmmc;
	}
	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public String getAccountcode() {
		return accountcode;
	}
	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getOperatorid() {
		return operatorid;
	}
	public void setOperatorid(String operatorid) {
		this.operatorid = operatorid;
	}
	public String getFzhsx1() {
		return fzhsx1;
	}
	public void setFzhsx1(String fzhsx1) {
		this.fzhsx1 = fzhsx1;
	}
	public String getFzhsx2() {
		return fzhsx2;
	}
	public void setFzhsx2(String fzhsx2) {
		this.fzhsx2 = fzhsx2;
	}
	public String getFzhsx3() {
		return fzhsx3;
	}
	public void setFzhsx3(String fzhsx3) {
		this.fzhsx3 = fzhsx3;
	}
	public String getFzhsx4() {
		return fzhsx4;
	}
	public void setFzhsx4(String fzhsx4) {
		this.fzhsx4 = fzhsx4;
	}
	public String getFzhsx5() {
		return fzhsx5;
	}
	public void setFzhsx5(String fzhsx5) {
		this.fzhsx5 = fzhsx5;
	}
	public String getFzhsx6() {
		return fzhsx6;
	}
	public void setFzhsx6(String fzhsx6) {
		this.fzhsx6 = fzhsx6;
	}
	public String getFzhsx7() {
		return fzhsx7;
	}
	public void setFzhsx7(String fzhsx7) {
		this.fzhsx7 = fzhsx7;
	}
	public String getFzhsx8() {
		return fzhsx8;
	}
	public void setFzhsx8(String fzhsx8) {
		this.fzhsx8 = fzhsx8;
	}
	public String getFzhsx9() {
		return fzhsx9;
	}
	public void setFzhsx9(String fzhsx9) {
		this.fzhsx9 = fzhsx9;
	}
	public String getFzhsx10() {
		return fzhsx10;
	}
	public void setFzhsx10(String fzhsx10) {
		this.fzhsx10 = fzhsx10;
	}   
	
} 
