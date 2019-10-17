package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 序时账
 * 
 * @author jason
 *
 */
public class XsZVO extends SuperVO {

	private String pzz;

	private String qj;

	private String year;

	// 日期
	private String rq;

	// 凭证号
	private String pzh;

	// 摘要
	private String zy;

	// 科目编码
	private String kmbm;

	// 科目名称
	private String kmmc;

	private String fullkmmc;// 科目全称

	// 方向
	private String fx;

	// 金额
	private DZFDouble je;

	//
	private DZFDouble jfmny;

	private DZFDouble dfmny;

	// 打印时 标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;

	// 主表主键
	private String pk_tzpz_h;

	private DZFDouble hl;

	private DZFDouble ybjf;

	private DZFDouble ybdf;
	
	private String pk_corp;
	
	private String bz;

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public DZFDouble getJe() {
		return je;
	}

	public void setJe(DZFDouble je) {
		this.je = je;
	}

	public String getKmbm() {
		return kmbm;
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

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public DZFDouble getDfmny() {
		return dfmny;
	}

	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}

	public DZFDouble getJfmny() {
		return jfmny;
	}

	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
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

	public String getPzz() {
		return pzz;
	}

	public void setPzz(String pzz) {
		this.pzz = pzz;
	}

	public String getQj() {
		return qj;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFullkmmc() {
		return fullkmmc;
	}

	public void setFullkmmc(String fullkmmc) {
		this.fullkmmc = fullkmmc;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public DZFDouble getHl() {
		return hl;
	}

	public void setHl(DZFDouble hl) {
		this.hl = hl;
	}

	public DZFDouble getYbjf() {
		return ybjf;
	}

	public void setYbjf(DZFDouble ybjf) {
		this.ybjf = ybjf;
	}

	public DZFDouble getYbdf() {
		return ybdf;
	}

	public void setYbdf(DZFDouble ybdf) {
		this.ybdf = ybdf;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

}
