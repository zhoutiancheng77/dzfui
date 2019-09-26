package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 广告费和业务宣传费跨年度纳税调整明细表
 * @author WJX 
 * */
public class StZjtxNstzVO extends StBaseVO{

	private String pk_zjtxnstz;//主键
//	private String vno;//序号
//	private String vprojectname;//项目名称
	private DZFDouble vzzmny;//账载金额(资产账载金额)
	private DZFDouble vbnzjmny;//账载金额(本年折旧、摊销额)
	private DZFDouble vljzjmny;//账载金额(累计折旧、摊销额)
	private DZFDouble vjsjcmny;//税收金额(资产计税基础)
	private DZFDouble vbnzj2mny;//税收金额(按税收一般规定计算的本年折旧、摊销额)
	private DZFDouble vjszjmny;//税收金额(本年加速折旧额)
	private DZFDouble vjszj2mny;//税收金额(其中：2014年及以后年度新增固定资产加速折旧额（填写A105081）)
	private DZFDouble vljzj2mny;//税收金额(累计折旧、摊销额)
	private DZFDouble vnstzmny;//纳税调整(金额)
	private String ctzyy;//纳税调整(调整原因)
	
	
	
	private String rp_vzzmny;//账载金额(资产账载金额)
	private String rp_vbnzjmny;//账载金额(本年折旧、摊销额)
	private String rp_vljzjmny;//账载金额(累计折旧、摊销额)
	private String rp_vjsjcmny;//税收金额(资产计税基础)
	private String rp_vbnzj2mny;//税收金额(按税收一般规定计算的本年折旧、摊销额)
	private String rp_vjszjmny;//税收金额(本年加速折旧额)
	private String rp_vjszj2mny;//税收金额(其中：2014年及以后年度新增固定资产加速折旧额（填写A105081）)
	private String rp_vljzj2mny;//税收金额(累计折旧、摊销额)
	private String rp_vnstzmny;//纳税调整(金额)
	
//	private String pk_corp;//公司主键
//	private String pk_project;//纳税项目主键
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	
	

	public String getPk_zjtxnstz() {
		return pk_zjtxnstz;
	}

	public void setPk_zjtxnstz(String pk_zjtxnstz) {
		this.pk_zjtxnstz = pk_zjtxnstz;
	}

	public DZFDouble getVzzmny() {
		return vzzmny;
	}

	public void setVzzmny(DZFDouble vzzmny) {
		this.vzzmny = vzzmny;
	}

	public DZFDouble getVbnzjmny() {
		return vbnzjmny;
	}

	public void setVbnzjmny(DZFDouble vbnzjmny) {
		this.vbnzjmny = vbnzjmny;
	}

	public DZFDouble getVljzjmny() {
		return vljzjmny;
	}

	public void setVljzjmny(DZFDouble vljzjmny) {
		this.vljzjmny = vljzjmny;
	}

	public DZFDouble getVjsjcmny() {
		return vjsjcmny;
	}

	public void setVjsjcmny(DZFDouble vjsjcmny) {
		this.vjsjcmny = vjsjcmny;
	}

	public DZFDouble getVbnzj2mny() {
		return vbnzj2mny;
	}

	public void setVbnzj2mny(DZFDouble vbnzj2mny) {
		this.vbnzj2mny = vbnzj2mny;
	}

	public DZFDouble getVjszjmny() {
		return vjszjmny;
	}

	public void setVjszjmny(DZFDouble vjszjmny) {
		this.vjszjmny = vjszjmny;
	}

	public DZFDouble getVjszj2mny() {
		return vjszj2mny;
	}

	public void setVjszj2mny(DZFDouble vjszj2mny) {
		this.vjszj2mny = vjszj2mny;
	}

	public DZFDouble getVljzj2mny() {
		return vljzj2mny;
	}

	public void setVljzj2mny(DZFDouble vljzj2mny) {
		this.vljzj2mny = vljzj2mny;
	}

	public DZFDouble getVnstzmny() {
		return vnstzmny;
	}

	public void setVnstzmny(DZFDouble vnstzmny) {
		this.vnstzmny = vnstzmny;
	}

	public String getCtzyy() {
		return ctzyy;
	}

	public void setCtzyy(String ctzyy) {
		this.ctzyy = ctzyy;
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
		return "pk_zjtxnstz";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_st_zjtxnstz";
	}

}
