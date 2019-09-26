package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * <b> 华道读取XML </b>
 * <p>
 */
@SuppressWarnings("serial")
public class ImageHuadaoHVO extends SuperVO {
	private String packagename;
	private String errortag;
	private String ptype;
	private String name;
	private String unitname;//采购方、销售方单位
	private String pname;
	private String bank;
	private DZFDouble totalmny;//金额合计
	
	private DZFDouble wsmny;//"无税金额"
	private DZFDouble smny;//"税额"
	//"金额加税额"、"收款金额"、"确认实缴的金额"、"支付的金额"、"支付的金额"、"借款金额"、
	//"报销金额"、"确认报销的金额"、"单行金额"、"金额"、"借款金额"
	private DZFDouble mny;
	private DZFDouble yjmny;//"预借差旅金额"
	private DZFDouble blmny;//"补领金额"
	private DZFDouble thmny;//"退还金额"
	private DZFDouble sbmny;//"扣社保"
	private DZFDouble gsmny;//"扣个税"
	private DZFDouble sfmny;//"实发工资"
	
	private String vused;//用途
	private String vtype;
	
	public static final String PACKAGENAME = "packagename";
	public static final String ERRORTAG = "errortag";
	public static final String PTYPE = "ptype";
	public static final String NAME = "name";
	public static final String UNITNAME = "unitname";
	public static final String TOTALMNY = "totalmny";
	public static final String MNY = "mny";
	public static final String SMNY = "smny";
	public static final String SBMNY = "sbmny";
	public static final String PNAME = "pname";
	public static final String BANK = "bank";
	public static final String VUSED = "vused";
	public static final String VTYPE = "vtype";
	
 
	public DZFDouble getWsmny() {
		return wsmny;
	}

	public void setWsmny(DZFDouble wsmny) {
		this.wsmny = wsmny;
	}

	public DZFDouble getYjmny() {
		return yjmny;
	}

	public void setYjmny(DZFDouble yjmny) {
		this.yjmny = yjmny;
	}

	public DZFDouble getBlmny() {
		return blmny;
	}

	public void setBlmny(DZFDouble blmny) {
		this.blmny = blmny;
	}

	public DZFDouble getThmny() {
		return thmny;
	}

	public void setThmny(DZFDouble thmny) {
		this.thmny = thmny;
	}

	public DZFDouble getGsmny() {
		return gsmny;
	}

	public void setGsmny(DZFDouble gsmny) {
		this.gsmny = gsmny;
	}

	public DZFDouble getSfmny() {
		return sfmny;
	}

	public void setSfmny(DZFDouble sfmny) {
		this.sfmny = sfmny;
	}

	public DZFDouble getSmny() {
		return smny;
	}

	public void setSmny(DZFDouble smny) {
		this.smny = smny;
	}

	public DZFDouble getSbmny() {
		return sbmny;
	}

	public void setSbmny(DZFDouble sbmny) {
		this.sbmny = sbmny;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getVused() {
		return vused;
	}

	public void setVused(String vused) {
		this.vused = vused;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getErrortag() {
		return errortag;
	}

	public void setErrortag(String errortag) {
		this.errortag = errortag;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public DZFDouble getTotalmny() {
		return totalmny;
	}

	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}

	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return "";
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-06 11:54:12
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-06 11:54:12
	  */
    public ImageHuadaoHVO() {
		super();	
	}


} 
