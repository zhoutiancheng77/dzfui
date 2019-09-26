package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class KmZzVO extends SuperVO {

	// 期间
	private String period;

	// 打印时 标题显示的区间区间
	private String titlePeriod;
	
	private String pk_tzpz_h;

	private String gs;

	// 摘要
	private String zy;

	// 借方
	private DZFDouble jf;

	// 贷方
	private DZFDouble df;

	// 方向
	private String fx;

	// 余额
	private DZFDouble ye;
	private String kmbm;

	// 科目
	private String km;
	private String pk_accsubj;

	// 原币借方
	private DZFDouble ybjf;

	// 原币贷方
	private DZFDouble ybdf;
	
	private Integer rowspan;//合并的行数

	private String day;
	

	// 币种
	public String pk_currency;
	private String currency;
	// 汇率
	public DZFDouble nrate;

	private Integer level;

	public String isPaging; // 是否分页 Y/N
	
	//存货ID
	private String pk_inventory;
	
//	存货数量
	private DZFDouble nnumber;

//	存货单价
	private DZFDouble nprice;
	
	
	private DZFDouble ybye;//原币余额

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
	
	private DZFDouble jfnnumber;//借方数量
	private DZFDouble dfnnumber;//贷方数量
	
	private String kmfullname;//科目全称

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getKmfullname() {
		return kmfullname;
	}

	public void setKmfullname(String kmfullname) {
		this.kmfullname = kmfullname;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public DZFDouble getJfnnumber() {
		return jfnnumber;
	}

	public void setJfnnumber(DZFDouble jfnnumber) {
		this.jfnnumber = jfnnumber;
	}

	public DZFDouble getDfnnumber() {
		return dfnnumber;
	}

	public void setDfnnumber(DZFDouble dfnnumber) {
		this.dfnnumber = dfnnumber;
	}

	public DZFDouble getYbye() {
		return ybye;
	}

	public void setYbye(DZFDouble ybye) {
		this.ybye = ybye;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}
	

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public DZFDouble getNnumber() {
		return nnumber;
	}

	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
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

	public DZFDouble getYbjf() {
		return ybjf;
	}

	public void setYbjf(DZFDouble ybjf) {
		this.ybjf = ybjf;
	}

	public DZFDouble getYbdf() {
		return ybdf;
	}

	public void setYbdf(DZFDouble ybdf) {
		this.ybdf = ybdf;
	}

	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}

	public DZFDouble getNrate() {
		return nrate;
	}

	public void setNrate(DZFDouble nrate) {
		this.nrate = nrate;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	public DZFDouble getDf() {
		return df;
	}

	public void setDf(DZFDouble df) {
		this.df = df;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public DZFDouble getJf() {
		return jf;
	}

	public void setJf(DZFDouble jf) {
		this.jf = jf;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public DZFDouble getYe() {
		return ye;
	}

	public void setYe(DZFDouble ye) {
		this.ye = ye;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getIsPaging() {
		return isPaging;
	}

	public void setIsPaging(String isPaging) {
		this.isPaging = isPaging;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

}
