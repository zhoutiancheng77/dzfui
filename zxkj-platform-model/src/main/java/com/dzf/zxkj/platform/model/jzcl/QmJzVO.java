package com.dzf.zxkj.platform.model.jzcl;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 总账期末结账
 * @author zhangj
 *
 */
@SuppressWarnings("serial")
public class QmJzVO extends SuperVO {
	@JsonProperty("cid")
	private String pk_corp;
	private DZFDateTime ts;
	private DZFBoolean gdzchasjz;
	private String vdef9;
	public DZFBoolean sykmwye;
	private String coperatorid;
	private String vdef1;
	private String vdef8;
	private String vdef10;
	private String period;
	private String vapproveid;
	private String vapprovenote;
	private DZFBoolean qmph;
	private String vdef7;
	private DZFBoolean jzfinish;
	private DZFDate dapprovedate;
	private String vdef2;
	private String vdef5;
	private String pk_billtype;
	private Integer vbillstatus;
	private String memo;
	@JsonProperty("qmid")
	private String pk_qmjz;
	private String vdef3;
	private String vdef6;
	private String vbillno;
	private Integer dr;
	private DZFDate doperatedate;
	private String vdef4;
	private DZFBoolean pzhasjz;
	private String corpname;
	// 打印时 标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;

	private DZFBoolean holdflag;// 公司标识(是否启用固定资产)，不存库

	// 辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
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

	public static final String PK_CORP = "pk_corp";
	public static final String GDZCHASJZ = "gdzchasjz";
	public static final String VDEF9 = "vdef9";
	public static final String SYKMWYE = "sykmwye";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String PERIOD = "period";
	public static final String VAPPROVEID = "vapproveid";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String QMPH = "qmph";
	public static final String VDEF7 = "vdef7";
	public static final String JZFINISH = "jzfinish";
	public static final String DAPPROVEDATE = "dapprovedate";
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
	public static final String VDEF4 = "vdef4";
	public static final String PZHASJZ = "pzhasjz";

	/**
	 * 属性pk_corp的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getPk_corp() {
		return pk_corp;
	}

	/**
	 * 属性pk_corp的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newPk_corp
	 *            String
	 */
	public void setPk_corp(String newPk_corp) {
		this.pk_corp = newPk_corp;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newTs
	 *            DZFDateTime
	 */
	public void setTs(DZFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * 属性gdzchasjz的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFBoolean
	 */
	public DZFBoolean getGdzchasjz() {
		return gdzchasjz;
	}

	/**
	 * 属性gdzchasjz的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newGdzchasjz
	 *            DZFBoolean
	 */
	public void setGdzchasjz(DZFBoolean newGdzchasjz) {
		this.gdzchasjz = newGdzchasjz;
	}

	/**
	 * 属性vdef9的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef9() {
		return vdef9;
	}

	/**
	 * 属性vdef9的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef9
	 *            String
	 */
	public void setVdef9(String newVdef9) {
		this.vdef9 = newVdef9;
	}

	/**
	 * 属性sykmwye的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFBoolean
	 */
	public DZFBoolean getSykmwye() {
		return sykmwye;
	}

	/**
	 * 属性sykmwye的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newSykmwye
	 *            DZFBoolean
	 */
	public void setSykmwye(DZFBoolean newSykmwye) {
		this.sykmwye = newSykmwye;
	}

	/**
	 * 属性coperatorid的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getCoperatorid() {
		return coperatorid;
	}

	/**
	 * 属性coperatorid的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newCoperatorid
	 *            String
	 */
	public void setCoperatorid(String newCoperatorid) {
		this.coperatorid = newCoperatorid;
	}

	/**
	 * 属性vdef1的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef1() {
		return vdef1;
	}

	/**
	 * 属性vdef1的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef1
	 *            String
	 */
	public void setVdef1(String newVdef1) {
		this.vdef1 = newVdef1;
	}

	/**
	 * 属性vdef8的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef8() {
		return vdef8;
	}

	/**
	 * 属性vdef8的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef8
	 *            String
	 */
	public void setVdef8(String newVdef8) {
		this.vdef8 = newVdef8;
	}

	/**
	 * 属性vdef10的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef10() {
		return vdef10;
	}

	/**
	 * 属性vdef10的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef10
	 *            String
	 */
	public void setVdef10(String newVdef10) {
		this.vdef10 = newVdef10;
	}

	/**
	 * 属性period的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * 属性period的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newPeriod
	 *            String
	 */
	public void setPeriod(String newPeriod) {
		this.period = newPeriod;
	}

	/**
	 * 属性vapproveid的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVapproveid() {
		return vapproveid;
	}

	/**
	 * 属性vapproveid的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVapproveid
	 *            String
	 */
	public void setVapproveid(String newVapproveid) {
		this.vapproveid = newVapproveid;
	}

	/**
	 * 属性vapprovenote的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVapprovenote() {
		return vapprovenote;
	}

	/**
	 * 属性vapprovenote的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVapprovenote
	 *            String
	 */
	public void setVapprovenote(String newVapprovenote) {
		this.vapprovenote = newVapprovenote;
	}

	/**
	 * 属性qmph的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFBoolean
	 */
	public DZFBoolean getQmph() {
		return qmph;
	}

	/**
	 * 属性qmph的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newQmph
	 *            DZFBoolean
	 */
	public void setQmph(DZFBoolean newQmph) {
		this.qmph = newQmph;
	}

	/**
	 * 属性vdef7的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef7() {
		return vdef7;
	}

	/**
	 * 属性vdef7的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef7
	 *            String
	 */
	public void setVdef7(String newVdef7) {
		this.vdef7 = newVdef7;
	}

	/**
	 * 属性jzfinish的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFBoolean
	 */
	public DZFBoolean getJzfinish() {
		return jzfinish;
	}

	/**
	 * 属性jzfinish的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newJzfinish
	 *            DZFBoolean
	 */
	public void setJzfinish(DZFBoolean newJzfinish) {
		this.jzfinish = newJzfinish;
	}

	/**
	 * 属性dapprovedate的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate() {
		return dapprovedate;
	}

	/**
	 * 属性dapprovedate的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newDapprovedate
	 *            DZFDate
	 */
	public void setDapprovedate(DZFDate newDapprovedate) {
		this.dapprovedate = newDapprovedate;
	}

	/**
	 * 属性vdef2的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef2() {
		return vdef2;
	}

	/**
	 * 属性vdef2的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef2
	 *            String
	 */
	public void setVdef2(String newVdef2) {
		this.vdef2 = newVdef2;
	}

	/**
	 * 属性vdef5的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef5() {
		return vdef5;
	}

	/**
	 * 属性vdef5的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef5
	 *            String
	 */
	public void setVdef5(String newVdef5) {
		this.vdef5 = newVdef5;
	}

	/**
	 * 属性pk_billtype的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getPk_billtype() {
		return pk_billtype;
	}

	/**
	 * 属性pk_billtype的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newPk_billtype
	 *            String
	 */
	public void setPk_billtype(String newPk_billtype) {
		this.pk_billtype = newPk_billtype;
	}

	/**
	 * 属性vbillstatus的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return UFDouble
	 */
	public Integer getVbillstatus() {
		return vbillstatus;
	}

	/**
	 * 属性vbillstatus的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVbillstatus
	 *            UFDouble
	 */
	public void setVbillstatus(Integer newVbillstatus) {
		this.vbillstatus = newVbillstatus;
	}

	/**
	 * 属性memo的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 属性memo的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newMemo
	 *            String
	 */
	public void setMemo(String newMemo) {
		this.memo = newMemo;
	}

	/**
	 * 属性pk_qmjz的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getPk_qmjz() {
		return pk_qmjz;
	}

	/**
	 * 属性pk_qmjz的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newPk_qmjz
	 *            String
	 */
	public void setPk_qmjz(String newPk_qmjz) {
		this.pk_qmjz = newPk_qmjz;
	}

	/**
	 * 属性vdef3的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef3() {
		return vdef3;
	}

	/**
	 * 属性vdef3的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef3
	 *            String
	 */
	public void setVdef3(String newVdef3) {
		this.vdef3 = newVdef3;
	}

	/**
	 * 属性vdef6的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef6() {
		return vdef6;
	}

	/**
	 * 属性vdef6的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef6
	 *            String
	 */
	public void setVdef6(String newVdef6) {
		this.vdef6 = newVdef6;
	}

	/**
	 * 属性vbillno的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVbillno() {
		return vbillno;
	}

	/**
	 * 属性vbillno的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVbillno
	 *            String
	 */
	public void setVbillno(String newVbillno) {
		this.vbillno = newVbillno;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return UFDouble
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newDr
	 *            UFDouble
	 */
	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性doperatedate的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	/**
	 * 属性doperatedate的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newDoperatedate
	 *            DZFDate
	 */
	public void setDoperatedate(DZFDate newDoperatedate) {
		this.doperatedate = newDoperatedate;
	}

	/**
	 * 属性vdef4的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return String
	 */
	public String getVdef4() {
		return vdef4;
	}

	/**
	 * 属性vdef4的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newVdef4
	 *            String
	 */
	public void setVdef4(String newVdef4) {
		this.vdef4 = newVdef4;
	}

	/**
	 * 属性pzhasjz的Getter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @return DZFBoolean
	 */
	public DZFBoolean getPzhasjz() {
		return pzhasjz;
	}

	/**
	 * 属性pzhasjz的Setter方法. 创建日期:2014-10-16 15:49:19
	 * 
	 * @param newPzhasjz
	 *            DZFBoolean
	 */
	public void setPzhasjz(DZFBoolean newPzhasjz) {
		this.pzhasjz = newPzhasjz;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2014-10-16 15:49:19
	 * 
	 * @return java.lang.String
	 */
	public String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2014-10-16 15:49:19
	 * 
	 * @return java.lang.String
	 */
	public String getPKFieldName() {
		return "pk_qmjz";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2014-10-16 15:49:19
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "YNT_QMJZ";
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2014-10-16 15:49:19
	 */
	public QmJzVO() {
		super();
	}

	public DZFBoolean getHoldflag() {
		return holdflag;
	}

	public void setHoldflag(DZFBoolean holdflag) {
		this.holdflag = holdflag;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

} 
