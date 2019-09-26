package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * <b> 华道读取XML </b>
 * <p>
 * </p>
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageHuadaoBVO extends SuperVO {
	private String invtype;//商品名称及类型
	private DZFDouble nnum;//数量
	
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
	
	public static final String INVTYPE = "invtype";
	public static final String NNUM = "nnum";
	public static final String MNY = "mny";
	public static final String SMNY = "smny";
	
	
	
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

	public DZFDouble getSbmny() {
		return sbmny;
	}

	public void setSbmny(DZFDouble sbmny) {
		this.sbmny = sbmny;
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

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public DZFDouble getNnum() {
		return nnum;
	}

	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public DZFDouble getSmny() {
		return smny;
	}

	public void setSmny(DZFDouble smny) {
		this.smny = smny;
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
    public ImageHuadaoBVO() {
		super();	
	}
	
} 
