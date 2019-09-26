package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDateTime;

public class StNssbInfoVO extends StBaseVO{
	
	//年度纳税申报表
	public static final String TYPE00000 = "TYPE00000";
	//一般纳税人申报表
	public static final String TYPE00001 = "TYPE00001";
	//企业所得税申报表
	public static final String TYPE00002 = "TYPE00002";
	//文化事业建设费申报表
	public static final String TYPE00003 = "TYPE00003";

	
	// 期间
	private String period;
	
	private String type;
	
	private String titlePeriod;
	
	private String gs;
	
	private  String flag; 
	
	private String pk_nssbinfo;
	private String cyear;
	private String pk_corp;
	private String cstatus;
	private String approvepsnid;
	private DZFDateTime approvetime;
	private String createpsnid;
	private DZFDateTime createtime;
	
	private String rp_vmny;
	private String citemclass;
	private String rp_vxsfy;
	private String rp_vxsfyjwzf;
	private String rp_vglfy;
	private String rp_vglfyjwzf;
	private String rp_vcwfy;
	private String rp_vcwfyjwzf;
	
	private String rp_vzzje;
	private String rp_vssje;
	private String rp_vtzje;
	private String rp_vtjje;
	
	private String rp_vssgdkcl;
	private String rp_vljjzkc;
	private String rp_vnstzje;
	private String rp_vjzkcje;
	
	private String gyxjz;
	private String fgyxjz;
	private String rp_vgyzzje;
	private String rp_vgykcxe;
	private String rp_vgyssje;
	private String rp_vgynstzje;
	private String rp_vfgyzzje;
	
	private String zzmny;
	private String ssmny;
	private String nstz;
	private String rp_vzzmny;
	private String rp_vbnzjmny;
	private String rp_vljzjmny;
	private String rp_vjsjcmny;
	private String rp_vbnzj2mny;
	private String rp_vjszjmny;
	private String rp_vjszj2mny;
	private String rp_vljzj2mny;
	private String rp_vnstzmny;
	private String ctzyy;
	
	private String rp_cnd;
	private String rp_vnstzsd;
	private String rp_vkmbks;
	private String rp_vdnkmbks;
	private String yqndksymbmny;
	private String rp_vsjksmb;
	private String rp_vkjzksmb;
	private String rp_vprefourth;
	private String rp_vprethird;
	private String rp_vpresecond;
	private String rp_vprefirst;
	private String rp_vtotal;

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/***
	 * 月份
	 */
	private String cmonth;

	
	public String getCmonth() {
		return cmonth;
	}

	public void setCmonth(String cmonth) {
		this.cmonth = cmonth;
	}

	public String getRp_cnd() {
		return rp_cnd;
	}

	public void setRp_cnd(String rp_cnd) {
		this.rp_cnd = rp_cnd;
	}

	public String getRp_vnstzsd() {
		return rp_vnstzsd;
	}

	public void setRp_vnstzsd(String rp_vnstzsd) {
		this.rp_vnstzsd = rp_vnstzsd;
	}

	public String getRp_vkmbks() {
		return rp_vkmbks;
	}

	public void setRp_vkmbks(String rp_vkmbks) {
		this.rp_vkmbks = rp_vkmbks;
	}

	public String getRp_vdnkmbks() {
		return rp_vdnkmbks;
	}

	public void setRp_vdnkmbks(String rp_vdnkmbks) {
		this.rp_vdnkmbks = rp_vdnkmbks;
	}

	public String getYqndksymbmny() {
		return yqndksymbmny;
	}

	public void setYqndksymbmny(String yqndksymbmny) {
		this.yqndksymbmny = yqndksymbmny;
	}

	public String getRp_vsjksmb() {
		return rp_vsjksmb;
	}

	public void setRp_vsjksmb(String rp_vsjksmb) {
		this.rp_vsjksmb = rp_vsjksmb;
	}

	public String getRp_vkjzksmb() {
		return rp_vkjzksmb;
	}

	public void setRp_vkjzksmb(String rp_vkjzksmb) {
		this.rp_vkjzksmb = rp_vkjzksmb;
	}

	public String getRp_vprefourth() {
		return rp_vprefourth;
	}

	public void setRp_vprefourth(String rp_vprefourth) {
		this.rp_vprefourth = rp_vprefourth;
	}

	public String getRp_vprethird() {
		return rp_vprethird;
	}

	public void setRp_vprethird(String rp_vprethird) {
		this.rp_vprethird = rp_vprethird;
	}

	public String getRp_vpresecond() {
		return rp_vpresecond;
	}

	public void setRp_vpresecond(String rp_vpresecond) {
		this.rp_vpresecond = rp_vpresecond;
	}

	public String getRp_vprefirst() {
		return rp_vprefirst;
	}

	public void setRp_vprefirst(String rp_vprefirst) {
		this.rp_vprefirst = rp_vprefirst;
	}

	public String getRp_vtotal() {
		return rp_vtotal;
	}

	public void setRp_vtotal(String rp_vtotal) {
		this.rp_vtotal = rp_vtotal;
	}

	public String getZzmny() {
		return zzmny;
	}

	public void setZzmny(String zzmny) {
		this.zzmny = zzmny;
	}

	public String getSsmny() {
		return ssmny;
	}

	public void setSsmny(String ssmny) {
		this.ssmny = ssmny;
	}

	public String getNstz() {
		return nstz;
	}

	public void setNstz(String nstz) {
		this.nstz = nstz;
	}

	public String getRp_vzzmny() {
		return rp_vzzmny;
	}

	public void setRp_vzzmny(String rp_vzzmny) {
		this.rp_vzzmny = rp_vzzmny;
	}

	public String getRp_vbnzjmny() {
		return rp_vbnzjmny;
	}

	public void setRp_vbnzjmny(String rp_vbnzjmny) {
		this.rp_vbnzjmny = rp_vbnzjmny;
	}

	public String getRp_vljzjmny() {
		return rp_vljzjmny;
	}

	public void setRp_vljzjmny(String rp_vljzjmny) {
		this.rp_vljzjmny = rp_vljzjmny;
	}

	public String getRp_vjsjcmny() {
		return rp_vjsjcmny;
	}

	public void setRp_vjsjcmny(String rp_vjsjcmny) {
		this.rp_vjsjcmny = rp_vjsjcmny;
	}

	public String getRp_vbnzj2mny() {
		return rp_vbnzj2mny;
	}

	public void setRp_vbnzj2mny(String rp_vbnzj2mny) {
		this.rp_vbnzj2mny = rp_vbnzj2mny;
	}

	public String getRp_vjszjmny() {
		return rp_vjszjmny;
	}

	public void setRp_vjszjmny(String rp_vjszjmny) {
		this.rp_vjszjmny = rp_vjszjmny;
	}

	public String getRp_vjszj2mny() {
		return rp_vjszj2mny;
	}

	public void setRp_vjszj2mny(String rp_vjszj2mny) {
		this.rp_vjszj2mny = rp_vjszj2mny;
	}

	public String getRp_vljzj2mny() {
		return rp_vljzj2mny;
	}

	public void setRp_vljzj2mny(String rp_vljzj2mny) {
		this.rp_vljzj2mny = rp_vljzj2mny;
	}

	public String getRp_vnstzmny() {
		return rp_vnstzmny;
	}

	public void setRp_vnstzmny(String rp_vnstzmny) {
		this.rp_vnstzmny = rp_vnstzmny;
	}

	public String getCtzyy() {
		return ctzyy;
	}

	public void setCtzyy(String ctzyy) {
		this.ctzyy = ctzyy;
	}

	public String getGyxjz() {
		return gyxjz;
	}

	public void setGyxjz(String gyxjz) {
		this.gyxjz = gyxjz;
	}

	public String getFgyxjz() {
		return fgyxjz;
	}

	public void setFgyxjz(String fgyxjz) {
		this.fgyxjz = fgyxjz;
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

	public String getRp_vzzje() {
		return rp_vzzje;
	}

	public void setRp_vzzje(String rp_vzzje) {
		this.rp_vzzje = rp_vzzje;
	}

	public String getRp_vssje() {
		return rp_vssje;
	}

	public void setRp_vssje(String rp_vssje) {
		this.rp_vssje = rp_vssje;
	}

	public String getRp_vtzje() {
		return rp_vtzje;
	}

	public void setRp_vtzje(String rp_vtzje) {
		this.rp_vtzje = rp_vtzje;
	}

	public String getRp_vtjje() {
		return rp_vtjje;
	}

	public void setRp_vtjje(String rp_vtjje) {
		this.rp_vtjje = rp_vtjje;
	}

	public String getRp_vxsfy() {
		return rp_vxsfy;
	}

	public void setRp_vxsfy(String rp_vxsfy) {
		this.rp_vxsfy = rp_vxsfy;
	}

	public String getRp_vxsfyjwzf() {
		return rp_vxsfyjwzf;
	}

	public void setRp_vxsfyjwzf(String rp_vxsfyjwzf) {
		this.rp_vxsfyjwzf = rp_vxsfyjwzf;
	}

	public String getRp_vglfy() {
		return rp_vglfy;
	}

	public void setRp_vglfy(String rp_vglfy) {
		this.rp_vglfy = rp_vglfy;
	}

	public String getRp_vglfyjwzf() {
		return rp_vglfyjwzf;
	}

	public void setRp_vglfyjwzf(String rp_vglfyjwzf) {
		this.rp_vglfyjwzf = rp_vglfyjwzf;
	}

	public String getRp_vcwfy() {
		return rp_vcwfy;
	}

	public void setRp_vcwfy(String rp_vcwfy) {
		this.rp_vcwfy = rp_vcwfy;
	}

	public String getRp_vcwfyjwzf() {
		return rp_vcwfyjwzf;
	}

	public void setRp_vcwfyjwzf(String rp_vcwfyjwzf) {
		this.rp_vcwfyjwzf = rp_vcwfyjwzf;
	}

	public String getCitemclass() {
		return citemclass;
	}

	public void setCitemclass(String citemclass) {
		this.citemclass = citemclass;
	}

	public String getRp_vmny() {
		return rp_vmny;
	}

	public void setRp_vmny(String rp_vmny) {
		this.rp_vmny = rp_vmny;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getPk_nssbinfo() {
		return pk_nssbinfo;
	}

	public void setPk_nssbinfo(String pk_nssbinfo) {
		this.pk_nssbinfo = pk_nssbinfo;
	}

	public String getCyear() {
		return cyear;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCstatus() {
		return cstatus;
	}

	public void setCstatus(String cstatus) {
		this.cstatus = cstatus;
	}

	public String getApprovepsnid() {
		return approvepsnid;
	}

	public void setApprovepsnid(String approvepsnid) {
		this.approvepsnid = approvepsnid;
	}

	public DZFDateTime getApprovetime() {
		return approvetime;
	}

	public void setApprovetime(DZFDateTime approvetime) {
		this.approvetime = approvetime;
	}

	
	
	
	public String getCreatepsnid() {
		return createpsnid;
	}

	public void setCreatepsnid(String createpsnid) {
		this.createpsnid = createpsnid;
	}

	public DZFDateTime getCreatetime() {
		return createtime;
	}

	public void setCreatetime(DZFDateTime createtime) {
		this.createtime = createtime;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_nssbinfo";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_nssbinfo";
	}
	
	

}
