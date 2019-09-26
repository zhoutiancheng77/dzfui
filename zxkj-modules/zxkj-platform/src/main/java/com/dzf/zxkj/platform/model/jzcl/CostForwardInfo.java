package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class CostForwardInfo extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3322691210357026373L;

	private String pk_inventory = null;
	private String vname = null;
	private String vcode = null;
	// 期初
	private DZFDouble ncailiao_qc = null;
	private DZFDouble nrengong_qc = null;
	private DZFDouble nzhizao_qc = null;
	// 本月发生
	private DZFDouble ncailiao_fs = null;
	private DZFDouble nrengong_fs = null;
	private DZFDouble nzhizao_fs = null;
	// 本月完工
	private DZFDouble ncailiao_wg = null;
	private DZFDouble nrengong_wg = null;
	private DZFDouble nzhizao_wg = null;
	private DZFDouble nnum_wg = null;
	// 本月未完工
	private DZFDouble ncailiao_nwg = null;
	private DZFDouble nrengong_nwg = null;
	private DZFDouble nzhizao_nwg = null;
	// 仅供查询使用
	private DZFDouble mny = null;
	private String accountcode = null;

	private String kmid;
	private String kmbm;
	private String kmmc;

	private String fzid;// 存货辅助
	
	private String kmfzid;

	public String getKmid() {
		return kmid;
	}

	public void setKmid(String kmid) {
		this.kmid = kmid;
	}

	public String getKmbm() {
		return kmbm;
	}

	public String getKmfzid() {
		return kmfzid;
	}

	public void setKmfzid(String kmfzid) {
		this.kmfzid = kmfzid;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public DZFDouble getNcailiao_qc() {
		return ncailiao_qc;
	}

	public void setNcailiao_qc(DZFDouble ncailiao_qc) {
		this.ncailiao_qc = ncailiao_qc;
	}

	public DZFDouble getNrengong_qc() {
		return nrengong_qc;
	}

	public void setNrengong_qc(DZFDouble nrengong_qc) {
		this.nrengong_qc = nrengong_qc;
	}

	public DZFDouble getNzhizao_qc() {
		return nzhizao_qc;
	}

	public void setNzhizao_qc(DZFDouble nzhizao_qc) {
		this.nzhizao_qc = nzhizao_qc;
	}

	public DZFDouble getNcailiao_fs() {
		return ncailiao_fs;
	}

	public void setNcailiao_fs(DZFDouble ncailiao_fs) {
		this.ncailiao_fs = ncailiao_fs;
	}

	public DZFDouble getNrengong_fs() {
		return nrengong_fs;
	}

	public void setNrengong_fs(DZFDouble nrengong_fs) {
		this.nrengong_fs = nrengong_fs;
	}

	public DZFDouble getNzhizao_fs() {
		return nzhizao_fs;
	}

	public void setNzhizao_fs(DZFDouble nzhizao_fs) {
		this.nzhizao_fs = nzhizao_fs;
	}

	public DZFDouble getNcailiao_wg() {
		return ncailiao_wg;
	}

	public void setNcailiao_wg(DZFDouble ncailiao_wg) {
		this.ncailiao_wg = ncailiao_wg;
	}

	public DZFDouble getNrengong_wg() {
		return nrengong_wg;
	}

	public void setNrengong_wg(DZFDouble nrengong_wg) {
		this.nrengong_wg = nrengong_wg;
	}

	public DZFDouble getNzhizao_wg() {
		return nzhizao_wg;
	}

	public void setNzhizao_wg(DZFDouble nzhizao_wg) {
		this.nzhizao_wg = nzhizao_wg;
	}

	public DZFDouble getNnum_wg() {
		return nnum_wg;
	}

	public void setNnum_wg(DZFDouble nnum_wg) {
		this.nnum_wg = nnum_wg;
	}

	public DZFDouble getNcailiao_nwg() {
		return ncailiao_nwg;
	}

	public void setNcailiao_nwg(DZFDouble ncailiao_nwg) {
		this.ncailiao_nwg = ncailiao_nwg;
	}

	public DZFDouble getNrengong_nwg() {
		return nrengong_nwg;
	}

	public void setNrengong_nwg(DZFDouble nrengong_nwg) {
		this.nrengong_nwg = nrengong_nwg;
	}

	public DZFDouble getNzhizao_nwg() {
		return nzhizao_nwg;
	}

	public void setNzhizao_nwg(DZFDouble nzhizao_nwg) {
		this.nzhizao_nwg = nzhizao_nwg;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public String getFzid() {
		return fzid;
	}

	public void setFzid(String fzid) {
		this.fzid = fzid;
	}
	
	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public String getTableName() {
		// TODO 自动生成的方法存根
		return null;
	}
}
