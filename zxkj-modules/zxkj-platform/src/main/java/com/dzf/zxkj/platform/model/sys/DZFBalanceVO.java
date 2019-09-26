package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class DZFBalanceVO extends SuperVO {
	private String pk_balance;
	private String pk_corp_yy;// 会计总公司
	private String pk_dzfservicedes;// 增值服务类型
	private DZFDouble totalcount;// 总数
	private DZFDouble usedcount;// 使用数
	private DZFDouble remainingcount;// 剩余数
	private Integer dr;
	private DZFDateTime ts;

	public void setPk_balance(String pk_balance) {
		this.pk_balance = pk_balance;
	}

	public String getPk_balance() {
		return pk_balance;
	}

	public String getPk_corp_yy() {
		return pk_corp_yy;
	}

	public void setPk_corp_yy(String pk_corp_yy) {
		this.pk_corp_yy = pk_corp_yy;
	}

	public void setPk_dzfservicedes(String pk_dzfservicedes) {
		this.pk_dzfservicedes = pk_dzfservicedes;
	}

	public String getPk_dzfservicedes() {
		return pk_dzfservicedes;
	}

	public void setTotalcount(DZFDouble totalcount) {
		this.totalcount = totalcount;
	}

	public DZFDouble getTotalcount() {
		return totalcount;
	}

	public void setUsedcount(DZFDouble usedcount) {
		this.usedcount = usedcount;
	}

	public DZFDouble getUsedcount() {
		return usedcount;
	}

	public void setRemainingcount(DZFDouble remainingcount) {
		this.remainingcount = remainingcount;
	}

	public DZFDouble getRemainingcount() {
		return remainingcount;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Integer getDr() {
		return dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	@Override
	public String getParentPKFieldName() {
		return "";
	}

	@Override
	public String getPKFieldName() {
		return "pk_balance";
	}

	@Override
	public String getTableName() {
		return "DZF_BALANCE";
	}

}
