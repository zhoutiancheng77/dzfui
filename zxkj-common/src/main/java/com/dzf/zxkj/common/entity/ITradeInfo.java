package com.dzf.zxkj.common.entity;

import java.io.Serializable;

public interface ITradeInfo extends Serializable{

	public String getPk_fillaccount();// 取数科目

	public void setPk_fillaccount(String pk_fillaccount);// 取数科目

	public String getAbstracts();// 摘要

	public void setAbstracts(String abstracts);// 摘要

	public String getPk_creditaccount();// 贷方

	public void setPk_creditaccount(String pk_creditaccount);// 贷方

	public String getPk_debitaccount();// 借方

	public void setPk_debitaccount(String pk_debitaccount);// 借方

}
