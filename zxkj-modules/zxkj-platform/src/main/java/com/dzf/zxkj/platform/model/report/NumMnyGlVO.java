package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 数量金额总账
 * @author Administrator
 *
 */
public class NumMnyGlVO extends SuperVO {
	
	private String titlePeriod;
	
	
	private DZFDouble nnumber;
	private DZFDouble dfmny;
	private DZFDouble jfmny;
	
	private String period;
	private String gs;
	private String beginqj;
	private String endqj;
	private String kmmc;
	private String spmc;
	private DZFDouble qcnum;
	private DZFDouble qcprice;
	private DZFDouble qcmny;
	private DZFDouble bqjfnum;
	private DZFDouble bqjfmny;
	private DZFDouble bqdfnum;
	private DZFDouble bqdfmny;
	private DZFDouble bnjfnum;
	private DZFDouble bnjfmny;
	private DZFDouble bndfnum;
	private DZFDouble bndfmny;
	private DZFDouble qmnum;
	private DZFDouble qmprice;
	private DZFDouble qmmny;
	private String pk_inventory;
	private String pk_subject;
	private String opdate;
	
	private String kmbm;
	private String dw;
	private  String  pk_tzpz_h;
	private Integer accountlevel;
	private  String  dir;
	
	//辅助核算
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
	
	private String pk_corp;//记录公司主键
	
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public Integer getAccountlevel() {
		return accountlevel;
	}
	public void setAccountlevel(Integer accountlevel) {
		this.accountlevel = accountlevel;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public String getDw() {
		return dw;
	}
	public void setDw(String dw) {
		this.dw = dw;
	}
	public DZFDouble getDfmny() {
		return dfmny;
	}
	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}
	public DZFDouble getJfmny() {
		return jfmny;
	}
	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}
	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}
	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}
	public String getBeginqj() {
		return beginqj;
	}
	public void setBeginqj(String beginqj) {
		this.beginqj = beginqj;
	}
	public String getEndqj() {
		return endqj;
	}
	public void setEndqj(String endqj) {
		this.endqj = endqj;
	}
	public String getKmmc() {
		return kmmc;
	}
	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}
	public String getSpmc() {
		return spmc;
	}
	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}
	public DZFDouble getQcnum() {
		return qcnum;
	}
	public void setQcnum(DZFDouble qcnum) {
		this.qcnum = qcnum;
	}
	public DZFDouble getQcprice() {
		return qcprice;
	}
	public void setQcprice(DZFDouble qcprice) {
		this.qcprice = qcprice;
	}
	public DZFDouble getQcmny() {
		return qcmny;
	}
	public void setQcmny(DZFDouble qcmny) {
		this.qcmny = qcmny;
	}
	public DZFDouble getBqjfnum() {
		return bqjfnum;
	}
	public void setBqjfnum(DZFDouble bqjfnum) {
		this.bqjfnum = bqjfnum;
	}
	public DZFDouble getBqjfmny() {
		return bqjfmny;
	}
	public void setBqjfmny(DZFDouble bqjfmny) {
		this.bqjfmny = bqjfmny;
	}
	public DZFDouble getBqdfnum() {
		return bqdfnum;
	}
	public void setBqdfnum(DZFDouble bqdfnum) {
		this.bqdfnum = bqdfnum;
	}
	public DZFDouble getBqdfmny() {
		return bqdfmny;
	}
	public void setBqdfmny(DZFDouble bqdfmny) {
		this.bqdfmny = bqdfmny;
	}
	public DZFDouble getBnjfnum() {
		return bnjfnum;
	}
	public void setBnjfnum(DZFDouble bnjfnum) {
		this.bnjfnum = bnjfnum;
	}
	public DZFDouble getBnjfmny() {
		return bnjfmny;
	}
	public void setBnjfmny(DZFDouble bnjfmny) {
		this.bnjfmny = bnjfmny;
	}
	public DZFDouble getBndfnum() {
		return bndfnum;
	}
	public void setBndfnum(DZFDouble bndfnum) {
		this.bndfnum = bndfnum;
	}
	public DZFDouble getBndfmny() {
		return bndfmny;
	}
	public void setBndfmny(DZFDouble bndfmny) {
		this.bndfmny = bndfmny;
	}
	public DZFDouble getQmnum() {
		return qmnum;
	}
	public void setQmnum(DZFDouble qmnum) {
		this.qmnum = qmnum;
	}
	public DZFDouble getQmprice() {
		return qmprice;
	}
	public void setQmprice(DZFDouble qmprice) {
		this.qmprice = qmprice;
	}
	public DZFDouble getQmmny() {
		return qmmny;
	}
	public void setQmmny(DZFDouble qmmny) {
		this.qmmny = qmmny;
	}
	public String getPk_inventory() {
		return pk_inventory;
	}
	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	public String getPk_subject() {
		return pk_subject;
	}
	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}
	public String getOpdate() {
		return opdate;
	}
	public void setOpdate(String opdate) {
		this.opdate = opdate;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return null;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}
	public String getTitlePeriod() {
		return titlePeriod;
	}
	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}
	public DZFDouble getNnumber() {
		return nnumber;
	}
	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
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
