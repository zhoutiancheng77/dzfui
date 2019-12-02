package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class DZFBalanceBVO extends SuperVO {
	private String pk_balance_b;
	private String pk_balance;
	private String pk_dzfservicedes;// 增值服务类型
	private DZFDouble changedcount;// 使用数量
	private Integer isadd;// 标识：0增加，1减少
	private String vordercode;// 订单号
	private String pk_corp;// 使用公司(客户)
	private String pk_user;// 下单人或使用人
	private String pk_corpkjgs;// 会计公司
	private String pk_corp_yy;//总会计公司
	private String description;// 备注
	private String period;// 会计期间
	private DZFDate opdate;// 日期
	private Integer dr;
	private DZFDateTime ts;
	private String pk_temp_user;

	public String getPk_temp_user() {
		return pk_temp_user;
	}

	public void setPk_temp_user(String pk_temp_user) {
		this.pk_temp_user = pk_temp_user;
	}

	public String getPk_corp_yy() {
		return pk_corp_yy;
	}

	public void setPk_corp_yy(String pk_corp_yy) {
		this.pk_corp_yy = pk_corp_yy;
	}

	public String getPk_dzfservicedes() {
		return pk_dzfservicedes;
	}

	public void setPk_dzfservicedes(String pk_dzfservicedes) {
		this.pk_dzfservicedes = pk_dzfservicedes;
	}

	public DZFDate getOpdate() {
		return opdate;
	}

	public void setOpdate(DZFDate opdate) {
		this.opdate = opdate;
	}

	public void setPk_balance_b(String pk_balance_b) {
		this.pk_balance_b = pk_balance_b;
	}

	public String getPk_balance_b() {
		return pk_balance_b;
	}

	public void setPk_balance(String pk_balance) {
		this.pk_balance = pk_balance;
	}

	public String getPk_balance() {
		return pk_balance;
	}

	public void setChangedcount(DZFDouble changedcount) {
		this.changedcount = changedcount;
	}

	public DZFDouble getChangedcount() {
		return changedcount;
	}

	public void setIsadd(Integer isadd) {
		this.isadd = isadd;
	}

	public Integer getIsadd() {
		return isadd;
	}

	public void setVordercode(String vordercode) {
		this.vordercode = vordercode;
	}

	public String getVordercode() {
		return vordercode;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}

	public String getPk_user() {
		return pk_user;
	}

	public void setPk_corpkjgs(String pk_corpkjgs) {
		this.pk_corpkjgs = pk_corpkjgs;
	}

	public String getPk_corpkjgs() {
		return pk_corpkjgs;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPeriod() {
		return period;
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
		return "pk_balance";
	}

	@Override
	public String getPKFieldName() {
		return "pk_balance_b";
	}

	@Override
	public String getTableName() {
		return "DZF_BALANCE_B";
	}

}
