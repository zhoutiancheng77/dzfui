package com.dzf.zxkj.platform.model.pzgl;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class InvCurentVO extends SuperVO {

	private static final long serialVersionUID = 6354099516998533958L;
	
	private String dbillid;// 单据编号
	
	private String pk_subinvtory;
	
	private String pk_tzpz_b;
	
	private String pk_tzpz_h;
	
	private String pk_corp;
	
	private String pk_accsubj;
	
	private String pk_inventory;
	
	private String pk_ictradeout;
	
	private String accountcode;
	
	private String accountname;
	
	private String invname;
	
	private DZFDouble nnumber;
	
	private DZFDouble ndef1;
	
	private DZFDouble ndef2;
	
	private DZFDouble ndef3;
	
	private DZFDouble ndef4;
	
	private DZFDouble ndef5;
	
	private String vdef1;
	
	private String vdef2;
	
	private String vdef3;
	
	private String vdef4;
	
	private String vdef5;
	
	private DZFDateTime ts;
	
	private Integer dr;
	
	private DZFBoolean isback;
	
	public String getPk_subinvtory() {
		return pk_subinvtory;
	}

	public void setPk_subinvtory(String pk_subinvtory) {
		this.pk_subinvtory = pk_subinvtory;
	}

	public String getPk_tzpz_b() {
		return pk_tzpz_b;
	}

	public void setPk_tzpz_b(String pk_tzpz_b) {
		this.pk_tzpz_b = pk_tzpz_b;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public DZFDouble getNnumber() {
		return nnumber;
	}

	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
	}

	public DZFDouble getNdef1() {
		return ndef1;
	}

	public void setNdef1(DZFDouble ndef1) {
		this.ndef1 = ndef1;
	}

	public DZFDouble getNdef2() {
		return ndef2;
	}

	public void setNdef2(DZFDouble ndef2) {
		this.ndef2 = ndef2;
	}

	public DZFDouble getNdef3() {
		return ndef3;
	}

	public void setNdef3(DZFDouble ndef3) {
		this.ndef3 = ndef3;
	}

	public DZFDouble getNdef4() {
		return ndef4;
	}

	public void setNdef4(DZFDouble ndef4) {
		this.ndef4 = ndef4;
	}

	public DZFDouble getNdef5() {
		return ndef5;
	}

	public void setNdef5(DZFDouble ndef5) {
		this.ndef5 = ndef5;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
	
	public DZFBoolean getIsback() {
		return isback;
	}

	public void setIsback(DZFBoolean isback) {
		this.isback = isback;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_tzpz_b";
	}
	
	@Override
	public String getPKFieldName() {
		return "pk_subinvtory";
	}
	
	@Override
	public String getTableName() {
		return "ynt_subinvtory";
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getPk_ictradeout() {
		return pk_ictradeout;
	}

	public void setPk_ictradeout(String pk_ictradeout) {
		this.pk_ictradeout = pk_ictradeout;
	}

	public String getDbillid() {
		return dbillid;
	}

	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}
}
