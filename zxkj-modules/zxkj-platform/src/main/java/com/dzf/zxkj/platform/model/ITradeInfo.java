package com.dzf.zxkj.platform.model;

import java.io.Serializable;

public interface ITradeInfo extends Serializable{

	String getPk_fillaccount();// 取数科目

	void setPk_fillaccount(String pk_fillaccount);// 取数科目

	String getAbstracts();// 摘要

	void setAbstracts(String abstracts);// 摘要

	String getPk_creditaccount();// 贷方

	void setPk_creditaccount(String pk_creditaccount);// 贷方

	String getPk_debitaccount();// 借方

	void setPk_debitaccount(String pk_debitaccount);// 借方
}
