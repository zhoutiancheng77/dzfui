package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 银行回单生成对账单的中间表
 *
 */
public class BankBillToStatementVO extends SuperVO {

	private String pk_bankbilltostatement;//主键
	private String coperatorid;//操作人
	private DZFDate doperatedate;//操作日期
	private String pk_corp;//公司pk
	private DZFDate tradingdate;//交易日期
	private String zy;//摘要
	private DZFDouble syje;//收入金额
	private DZFDouble zcje;//支出金额
	private String othaccountname;//对方账户名称
	private String othaccountcode;//对方账户
	private String pk_model_h;//业务类型主键
	private String busitypetempname;//业务类型名称
	private String period;//入账期间
	
	private String sourcebillid;//来源id  图片明细
	private String imgpath;//图片路径
	private String pk_image_group;//图片组号
	private String pk_image_ocrlibrary;//图片识别信息表
	private String pk_image_library;//图片子表id
	private String pk_bankstatement;// 
	private String pk_bankaccount;//银行账户档案
	
	private String myaccountname;//本方账户名称
	private String myaccountcode;//本方账户
	
	private String pk_tzpz_h;//凭证主键 不存库
	private String pzh; //不存库
	
	private String memo;//操作的详细信息 
	
	private int dr;
	private DZFDateTime ts;
	private String accountname;//银行名称
	
	

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getPk_bankbilltostatement() {
		return pk_bankbilltostatement;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public DZFDate getTradingdate() {
		return tradingdate;
	}

	public String getZy() {
		return zy;
	}

	public DZFDouble getSyje() {
		return syje;
	}

	public DZFDouble getZcje() {
		return zcje;
	}

	public String getOthaccountname() {
		return othaccountname;
	}

	public String getOthaccountcode() {
		return othaccountcode;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public String getPeriod() {
		return period;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public String getImgpath() {
		return imgpath;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public String getPk_image_ocrlibrary() {
		return pk_image_ocrlibrary;
	}

	public String getPk_bankstatement() {
		return pk_bankstatement;
	}

	public String getPk_bankaccount() {
		return pk_bankaccount;
	}

	public String getMyaccountname() {
		return myaccountname;
	}

	public String getMyaccountcode() {
		return myaccountcode;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public String getPzh() {
		return pzh;
	}

	public String getMemo() {
		return memo;
	}

	public int getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_bankbilltostatement(String pk_bankbilltostatement) {
		this.pk_bankbilltostatement = pk_bankbilltostatement;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setTradingdate(DZFDate tradingdate) {
		this.tradingdate = tradingdate;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public void setSyje(DZFDouble syje) {
		this.syje = syje;
	}

	public void setZcje(DZFDouble zcje) {
		this.zcje = zcje;
	}

	public void setOthaccountname(String othaccountname) {
		this.othaccountname = othaccountname;
	}

	public void setOthaccountcode(String othaccountcode) {
		this.othaccountcode = othaccountcode;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public void setPk_image_ocrlibrary(String pk_image_ocrlibrary) {
		this.pk_image_ocrlibrary = pk_image_ocrlibrary;
	}

	public void setPk_bankstatement(String pk_bankstatement) {
		this.pk_bankstatement = pk_bankstatement;
	}

	public void setPk_bankaccount(String pk_bankaccount) {
		this.pk_bankaccount = pk_bankaccount;
	}

	public void setMyaccountname(String myaccountname) {
		this.myaccountname = myaccountname;
	}

	public void setMyaccountcode(String myaccountcode) {
		this.myaccountcode = myaccountcode;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	@Override
	public String getPKFieldName() {
		return "pk_bankbilltostatement";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_bankbilltostatement";
	}

}
