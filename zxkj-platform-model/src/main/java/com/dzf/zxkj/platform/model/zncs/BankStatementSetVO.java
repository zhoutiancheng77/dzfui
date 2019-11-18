package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 银行对账单
 * @author reny
 *
 */
public class BankStatementSetVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_bankstatement_set;//主键
	@JsonProperty("ccode")
	private String columncode;//列字段名
	@JsonProperty("aname")
	private String columnalias;//列别名

	private int dr;
	private DZFDateTime ts;
	


	

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	

	public String getPk_bankstatement_set() {
		return pk_bankstatement_set;
	}

	public String getColumncode() {
		return columncode;
	}

	public String getColumnalias() {
		return columnalias;
	}

	public void setPk_bankstatement_set(String pk_bankstatement_set) {
		this.pk_bankstatement_set = pk_bankstatement_set;
	}

	public void setColumncode(String columncode) {
		this.columncode = columncode;
	}

	public void setColumnalias(String columnalias) {
		this.columnalias = columnalias;
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
