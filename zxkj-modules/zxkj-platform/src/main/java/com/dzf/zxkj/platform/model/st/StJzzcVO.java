package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 捐赠支出纳税调整明细表
 * @author WJX
 * */
public class StJzzcVO extends StBaseVO{

	private String pk_jzzcnstz;//主键
//	private String vno;//序号
//	private String vprojectname;//项目名称
	private DZFDouble vgyzzje;//公益账载金额
	private DZFDouble vgykcxe;//按税收规定计算的扣除限额
	private DZFDouble vgyssje;//公益税收金额
	private DZFDouble vgynstzje;//公益纳税调整金额
	private DZFDouble vfgyzzje;//非公益账载金额
	private DZFDouble vnstzje;//纳税调整金额
	
	private String rp_vgyzzje;//公益账载金额
	private String rp_vgykcxe;//按税收规定计算的扣除限额
	private String rp_vgyssje;//公益税收金额
	private String rp_vgynstzje;//公益纳税调整金额
	private String rp_vfgyzzje;//非公益账载金额
	private String rp_vnstzje;//纳税调整金额
//	private String pk_corp;//公司主键
//	private String pk_project;//纳税项目主键
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
//	private String cyear;//会计年度
	
	public void setVnstzje(DZFDouble vnstzje) {
		this.vnstzje = vnstzje;
	}
	
	public DZFDouble getVnstzje() {
		return vnstzje;
	}
	
	public void setRp_vnstzje(String rp_vnstzje) {
		this.rp_vnstzje = rp_vnstzje;
	}
	
	public String getRp_vnstzje() {
		return rp_vnstzje;
	}
	
	public void setPk_jzzcnstz(String pk_jzzcnstz) {
		this.pk_jzzcnstz = pk_jzzcnstz;
	}
	
	public String getPk_jzzcnstz() {
		return pk_jzzcnstz;
	}

	public DZFDouble getVgyzzje() {
		return vgyzzje;
	}

	public void setVgyzzje(DZFDouble vgyzzje) {
		this.vgyzzje = vgyzzje;
	}

	public DZFDouble getVgykcxe() {
		return vgykcxe;
	}

	public void setVgykcxe(DZFDouble vgykcxe) {
		this.vgykcxe = vgykcxe;
	}

	public DZFDouble getVgyssje() {
		return vgyssje;
	}

	public void setVgyssje(DZFDouble vgyssje) {
		this.vgyssje = vgyssje;
	}

	public DZFDouble getVgynstzje() {
		return vgynstzje;
	}

	public void setVgynstzje(DZFDouble vgynstzje) {
		this.vgynstzje = vgynstzje;
	}

	public DZFDouble getVfgyzzje() {
		return vfgyzzje;
	}

	public void setVfgyzzje(DZFDouble vfgyzzje) {
		this.vfgyzzje = vfgyzzje;
	}

	public String getRp_vgyzzje() {
		return rp_vgyzzje;
	}

	public void setRp_vgyzzje(String rp_vgyzzje) {
		this.rp_vgyzzje = rp_vgyzzje;
	}

	public String getRp_vgykcxe() {
		return rp_vgykcxe;
	}

	public void setRp_vgykcxe(String rp_vgykcxe) {
		this.rp_vgykcxe = rp_vgykcxe;
	}

	public String getRp_vgyssje() {
		return rp_vgyssje;
	}

	public void setRp_vgyssje(String rp_vgyssje) {
		this.rp_vgyssje = rp_vgyssje;
	}

	public String getRp_vgynstzje() {
		return rp_vgynstzje;
	}

	public void setRp_vgynstzje(String rp_vgynstzje) {
		this.rp_vgynstzje = rp_vgynstzje;
	}

	public String getRp_vfgyzzje() {
		return rp_vfgyzzje;
	}

	public void setRp_vfgyzzje(String rp_vfgyzzje) {
		this.rp_vfgyzzje = rp_vfgyzzje;
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
		return "pk_jzzcnstz";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_jzzcnstz";
	}

}
