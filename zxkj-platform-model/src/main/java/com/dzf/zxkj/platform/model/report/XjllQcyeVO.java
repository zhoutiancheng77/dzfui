package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-10-10 17:17:58
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class XjllQcyeVO extends SuperVO {
	private String pk_corp;
	private DZFDateTime ts;
	private String vdef9;
	private String coperatorid;
	private String vdef1;
	private String vdef8;
	private String vname;
	private String vdef10;
	private String vapproveid;
	private String vapprovenote;
	private String vcode;
	private String vdef7;
	private String pk_project;
	private String xmid;
	private DZFDate dapprovedate;
	private DZFDouble nmny;
	private Integer vdirect;
	private String vdef2;
	private String vdef5;
	private String pk_billtype;
	private String pk_xjllqcye;
	private Integer vbillstatus;
	private String memo;
	private String vdef3;
	private String vdef6;
	private String vbillno;
	private Integer dr;
	private DZFDate doperatedate;
	private String dcurrency;
	private String vdef4;
	
	private String year;//期间，不存库
	
	/**
	 *  四个季度金额
	 */
	private DZFDouble q1 = DZFDouble.ZERO_DBL;
	private DZFDouble q2 = DZFDouble.ZERO_DBL;
	private DZFDouble q3 = DZFDouble.ZERO_DBL;
	private DZFDouble q4 = DZFDouble.ZERO_DBL;

	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VNAME = "vname";
	public static final String VDEF10 = "vdef10";
	public static final String VAPPROVEID = "vapproveid";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String VCODE = "vcode";
	public static final String VDEF7 = "vdef7";
	public static final String PK_PROJECT = "pk_project";
	public static final String DAPPROVEDATE = "dapprovedate";
	public static final String NMNY = "nmny";
	public static final String VDIRECT = "vdirect";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String PK_XJLLQCYE = "pk_xjllqcye";
	public static final String VBILLSTATUS = "vbillstatus";
	public static final String MEMO = "memo";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String VBILLNO = "vbillno";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String DCURRENCY = "dcurrency";
	public static final String VDEF4 = "vdef4";
		
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vname的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVname () {
		return vname;
	}   
	/**
	 * 属性vname的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVname String
	 */
	public void setVname (String newVname ) {
	 	this.vname = newVname;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性vapproveid的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVapproveid () {
		return vapproveid;
	}   
	/**
	 * 属性vapproveid的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVapproveid String
	 */
	public void setVapproveid (String newVapproveid ) {
	 	this.vapproveid = newVapproveid;
	} 	  
	/**
	 * 属性vapprovenote的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVapprovenote () {
		return vapprovenote;
	}   
	/**
	 * 属性vapprovenote的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVapprovenote String
	 */
	public void setVapprovenote (String newVapprovenote ) {
	 	this.vapprovenote = newVapprovenote;
	} 	  
	/**
	 * 属性vcode的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVcode () {
		return vcode;
	}   
	/**
	 * 属性vcode的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVcode String
	 */
	public void setVcode (String newVcode ) {
	 	this.vcode = newVcode;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
	/**
	 * 属性pk_project的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getPk_project () {
		return pk_project;
	}   
	/**
	 * 属性pk_project的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newPk_project String
	 */
	public void setPk_project (String newPk_project ) {
	 	this.pk_project = newPk_project;
	} 	  
	/**
	 * 属性dapprovedate的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate () {
		return dapprovedate;
	}   
	/**
	 * 属性dapprovedate的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newDapprovedate DZFDate
	 */
	public void setDapprovedate (DZFDate newDapprovedate ) {
	 	this.dapprovedate = newDapprovedate;
	} 	  
	/**
	 * 属性nmny的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDouble
	 */
	public DZFDouble getNmny () {
		return nmny;
	}   
	/**
	 * 属性nmny的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newNmny DZFDouble
	 */
	public void setNmny (DZFDouble newNmny ) {
	 	this.nmny = newNmny;
	} 	  
	/**
	 * 属性vdirect的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDouble
	 */
	public Integer getVdirect () {
		return vdirect;
	}   
	/**
	 * 属性vdirect的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdirect DZFDouble
	 */
	public void setVdirect (Integer newVdirect ) {
	 	this.vdirect = newVdirect;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性pk_billtype的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * 属性pk_billtype的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newPk_billtype String
	 */
	public void setPk_billtype (String newPk_billtype ) {
	 	this.pk_billtype = newPk_billtype;
	} 	  
	/**
	 * 属性pk_xjllqcye的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getPk_xjllqcye () {
		return pk_xjllqcye;
	}   
	/**
	 * 属性pk_xjllqcye的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newPk_xjllqcye String
	 */
	public void setPk_xjllqcye (String newPk_xjllqcye ) {
	 	this.pk_xjllqcye = newPk_xjllqcye;
	} 	  
	/**
	 * 属性vbillstatus的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDouble
	 */
	public Integer getVbillstatus () {
		return vbillstatus;
	}   
	/**
	 * 属性vbillstatus的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVbillstatus DZFDouble
	 */
	public void setVbillstatus (Integer newVbillstatus ) {
	 	this.vbillstatus = newVbillstatus;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性vbillno的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVbillno () {
		return vbillno;
	}   
	/**
	 * 属性vbillno的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVbillno String
	 */
	public void setVbillno (String newVbillno ) {
	 	this.vbillno = newVbillno;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dcurrency的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getDcurrency () {
		return dcurrency;
	}   
	/**
	 * 属性dcurrency的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newDcurrency String
	 */
	public void setDcurrency (String newDcurrency ) {
	 	this.dcurrency = newDcurrency;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-10-10 17:17:58
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-10-10 17:17:58
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-10-10 17:17:58
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_xjllqcye";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-10-10 17:17:58
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "YNT_XJLLQCYE";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-10-10 17:17:58
	  */
     public XjllQcyeVO() {
		super();	
	}
	public DZFDouble getQ1() {
		return q1;
	}
	public void setQ1(DZFDouble q1) {
		this.q1 = q1;
	}
	
	public DZFDouble getQ2() {
		return q2;
	}
	public void setQ2(DZFDouble q2) {
		this.q2 = q2;
	}
	public DZFDouble getQ3() {
		return q3;
	}
	public void setQ3(DZFDouble q3) {
		this.q3 = q3;
	}
	public DZFDouble getQ4() {
		return q4;
	}
	public void setQ4(DZFDouble q4) {
		this.q4 = q4;
	}
	public String getXmid() {
		return xmid;
	}
	public void setXmid(String xmid) {
		this.xmid = xmid;
	}    
     
     
} 
