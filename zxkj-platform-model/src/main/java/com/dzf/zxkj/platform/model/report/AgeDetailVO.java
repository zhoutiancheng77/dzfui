package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * 往来账龄明细vo
 * @author liubj
 *
 */
public class AgeDetailVO extends SuperVO {

	@JsonProperty("kmbm")
	private String account_code;
	@JsonProperty("kmmc")
	private String account_name;
	//未核销金额
	@JsonProperty("total")
	private DZFDouble total_mny;
	// 核销金额
	private DZFDouble verify_mny;
	// 核销凭证
	private String pk_verify_voucher;
	//凭证号
	@JsonProperty("vchnum")
	private String voucher_number;
	//凭证日期
	@JsonProperty("vchdate")
	private DZFDate vch_date;
	//天数
	private Integer days;
	//期间
	private String period;
	//辅助核算项
	private String fzhsx;
	private String pk_fzhsx;
	private String pk_voucher;
	
	@JsonProperty("periodsmny")
	private HashMap<String, DZFDouble> period_mny;
	
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
	
	public String getAccount_code() {
		return account_code;
	}

	public void setAccount_code(String account_code) {
		this.account_code = account_code;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public DZFDouble getTotal_mny() {
		return total_mny;
	}

	public void setTotal_mny(DZFDouble total_mny) {
		this.total_mny = total_mny;
	}

	public DZFDouble getVerify_mny() {
		return verify_mny;
	}

	public void setVerify_mny(DZFDouble verify_mny) {
		this.verify_mny = verify_mny;
	}

	public String getPk_verify_voucher() {
		return pk_verify_voucher;
	}

	public void setPk_verify_voucher(String pk_verify_voucher) {
		this.pk_verify_voucher = pk_verify_voucher;
	}

	public String getVoucher_number() {
		return voucher_number;
	}

	public void setVoucher_number(String voucher_number) {
		this.voucher_number = voucher_number;
	}

	public DZFDate getVch_date() {
		return vch_date;
	}

	public void setVch_date(DZFDate vch_date) {
		this.vch_date = vch_date;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public HashMap<String, DZFDouble> getPeriod_mny() {
		return period_mny;
	}

	public void setPeriod_mny(HashMap<String, DZFDouble> period_mny) {
		this.period_mny = period_mny;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getFzhsx() {
		return fzhsx;
	}

	public void setFzhsx(String fzhsx) {
		this.fzhsx = fzhsx;
	}

	public String getPk_voucher() {
		return pk_voucher;
	}

	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}

	public String getPk_fzhsx() {
		return pk_fzhsx;
	}

	public void setPk_fzhsx(String pk_fzhsx) {
		this.pk_fzhsx = pk_fzhsx;
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

}
