package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 银行对账单  返回提示信息和数组对象
 * @author reny
 *
 */
public class BankStatement2ResponseVO extends SuperVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	private BankStatementVO2[] vos;
	
	
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public BankStatementVO2[] getVos() {
		return vos;
	}

	public void setVos(BankStatementVO2[] vos) {
		this.vos = vos;
	}

	@Override
	public String getPKFieldName() {
		return "pk_bankstatement_set";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_bankstatement_set";
	}


}
