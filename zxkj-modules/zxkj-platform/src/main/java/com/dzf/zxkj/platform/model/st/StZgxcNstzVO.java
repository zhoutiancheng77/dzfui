package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 职工薪酬纳税调整VO
 * */
public class StZgxcNstzVO extends StBaseVO{

	private String pk_zgxcnstz;//主键
	
	private DZFDouble vzzje;//账载金额
	private DZFDouble vssgdkcl;//税收规定扣除率
	private DZFDouble vljjzkc;//以前年度累计结转扣除额
	private DZFDouble vssje;//税收金额
	private DZFDouble vnstzje;//纳税调整金额
	private DZFDouble vjzkcje;//累计结转以后年度扣除额
	
	private String rp_vzzje;
	private String rp_vssgdkcl;
	private String rp_vljjzkc;
	private String rp_vssje;
	private String rp_vnstzje;
	private String rp_vjzkcje;
	
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	
	
	public void setPk_zgxcnstz(String pk_zgxcnstz) {
		this.pk_zgxcnstz = pk_zgxcnstz;
	}
	
	public String getPk_zgxcnstz() {
		return pk_zgxcnstz;
	}
	
	
	
	
	public DZFDouble getVzzje() {
		return vzzje;
	}

	public void setVzzje(DZFDouble vzzje) {
		this.vzzje = vzzje;
	}

	public DZFDouble getVssgdkcl() {
		return vssgdkcl;
	}

	public void setVssgdkcl(DZFDouble vssgdkcl) {
		this.vssgdkcl = vssgdkcl;
	}

	public DZFDouble getVljjzkc() {
		return vljjzkc;
	}

	public void setVljjzkc(DZFDouble vljjzkc) {
		this.vljjzkc = vljjzkc;
	}

	public DZFDouble getVssje() {
		return vssje;
	}

	public void setVssje(DZFDouble vssje) {
		this.vssje = vssje;
	}

	public DZFDouble getVnstzje() {
		return vnstzje;
	}

	public void setVnstzje(DZFDouble vnstzje) {
		this.vnstzje = vnstzje;
	}

	public DZFDouble getVjzkcje() {
		return vjzkcje;
	}

	public void setVjzkcje(DZFDouble vjzkcje) {
		this.vjzkcje = vjzkcje;
	}

	public String getRp_vzzje() {
		return rp_vzzje;
	}

	public void setRp_vzzje(String rp_vzzje) {
		this.rp_vzzje = rp_vzzje;
	}

	public String getRp_vssgdkcl() {
		return rp_vssgdkcl;
	}

	public void setRp_vssgdkcl(String rp_vssgdkcl) {
		this.rp_vssgdkcl = rp_vssgdkcl;
	}

	public String getRp_vljjzkc() {
		return rp_vljjzkc;
	}

	public void setRp_vljjzkc(String rp_vljjzkc) {
		this.rp_vljjzkc = rp_vljjzkc;
	}

	public String getRp_vssje() {
		return rp_vssje;
	}

	public void setRp_vssje(String rp_vssje) {
		this.rp_vssje = rp_vssje;
	}

	public String getRp_vnstzje() {
		return rp_vnstzje;
	}

	public void setRp_vnstzje(String rp_vnstzje) {
		this.rp_vnstzje = rp_vnstzje;
	}

	public String getRp_vjzkcje() {
		return rp_vjzkcje;
	}

	public void setRp_vjzkcje(String rp_vjzkcje) {
		this.rp_vjzkcje = rp_vjzkcje;
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

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_zgxcnstz";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_zgxcnstz";
	}

	
	
}
