package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 企业所得税弥补亏损明细表
 * @author WJX 
 * */
public class StMbksVO extends StBaseVO{
	
	private String pk_mbks;
	
	//"cnd","vnstzsd","vkmbks","dnkmbks","prefourth","prethird","presecond","prefirst","total","sjksmb","kjzksmb"
	private String cnd;//年度
	private DZFDouble vnstzsd;//纳税调整后所得
	private DZFDouble vkmbks;//合并、分立转入（转出）可弥补的亏损额
	private DZFDouble vdnkmbks;//当年可弥补的亏损额
	private DZFDouble vprefourth;//以前年度亏损已弥补额(前四年度)
	private DZFDouble vprethird;//以前年度亏损已弥补额(前三年度)
	private DZFDouble vpresecond;//以前年度亏损已弥补额(前二年度)
	private DZFDouble vprefirst;//以前年度亏损已弥补额(前一年度)
	private DZFDouble vtotal;//以前年度亏损已弥补额(合计)
	private DZFDouble vsjksmb;//本年度实际弥补的以前年度亏损额
	private DZFDouble vkjzksmb;//可结转以后年度弥补的亏损额
	
	
	private String rp_cnd;//年度
	private String rp_vnstzsd;//
	private String rp_vkmbks;//
	private String rp_vdnkmbks;//
	private String rp_vprefourth;//
	private String rp_vprethird;//
	private String rp_vpresecond;//
	private String rp_vprefirst;//
	private String rp_vtotal;//
	private String rp_vsjksmb;//
	private String rp_vkjzksmb;//
	
//	private String pk_corp;//公司主键
//	private String pk_project;//纳税项目主键
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	
	
	
	

	public String getPk_mbks() {
		return pk_mbks;
	}

	public void setPk_mbks(String pk_mbks) {
		this.pk_mbks = pk_mbks;
	}

	public String getCnd() {
		return cnd;
	}

	public void setCnd(String cnd) {
		this.cnd = cnd;
	}

	public DZFDouble getVnstzsd() {
		return vnstzsd;
	}

	public void setVnstzsd(DZFDouble vnstzsd) {
		this.vnstzsd = vnstzsd;
	}

	public DZFDouble getVkmbks() {
		return vkmbks;
	}

	public void setVkmbks(DZFDouble vkmbks) {
		this.vkmbks = vkmbks;
	}
	
	

	public DZFDouble getVdnkmbks() {
		return vdnkmbks;
	}

	public void setVdnkmbks(DZFDouble vdnkmbks) {
		this.vdnkmbks = vdnkmbks;
	}

	public DZFDouble getVprefourth() {
		return vprefourth;
	}

	public void setVprefourth(DZFDouble vprefourth) {
		this.vprefourth = vprefourth;
	}

	public DZFDouble getVprethird() {
		return vprethird;
	}

	public void setVprethird(DZFDouble vprethird) {
		this.vprethird = vprethird;
	}

	public DZFDouble getVpresecond() {
		return vpresecond;
	}

	public void setVpresecond(DZFDouble vpresecond) {
		this.vpresecond = vpresecond;
	}

	public DZFDouble getVprefirst() {
		return vprefirst;
	}

	public void setVprefirst(DZFDouble vprefirst) {
		this.vprefirst = vprefirst;
	}

	public DZFDouble getVtotal() {
		return vtotal;
	}

	public void setVtotal(DZFDouble vtotal) {
		this.vtotal = vtotal;
	}

	public DZFDouble getVsjksmb() {
		return vsjksmb;
	}

	public void setVsjksmb(DZFDouble vsjksmb) {
		this.vsjksmb = vsjksmb;
	}

	public DZFDouble getVkjzksmb() {
		return vkjzksmb;
	}

	public void setVkjzksmb(DZFDouble vkjzksmb) {
		this.vkjzksmb = vkjzksmb;
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
		return "pk_mbks";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_mbks";
	}

}
