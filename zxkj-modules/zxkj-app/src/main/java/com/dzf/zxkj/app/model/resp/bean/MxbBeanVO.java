package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;

public class MxbBeanVO {
	private String rq ;
	private String zy ;
	private String km ;
	private DZFDouble jfmny ;
	private DZFDouble dfmny ;
	private DZFDouble ye ;
	
	private String pzh ;
	private String fx ;
	private String otsubject;//对方科目
	
	private String dfkbmb;
	private String dfkmmc;
	private String dfkm;
	
	
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public String getDfkbmb() {
		return dfkbmb;
	}
	public void setDfkbmb(String dfkbmb) {
		this.dfkbmb = dfkbmb;
	}
	public String getDfkmmc() {
		return dfkmmc;
	}
	public void setDfkmmc(String dfkmmc) {
		this.dfkmmc = dfkmmc;
	}
	public String getDfkm() {
		return dfkm;
	}
	public void setDfkm(String dfkm) {
		this.dfkm = dfkm;
	}
	public String getOtsubject() {
		return otsubject;
	}
	public void setOtsubject(String otsubject) {
		this.otsubject = otsubject;
	}
	public String getDfmny() {
		if(dfmny == null || new DZFDouble(dfmny).compareTo(DZFDouble.ZERO_DBL)==0){
			return "-";
		}
		return Common.format(dfmny);
	}
	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}
	public String getJfmny() {
		if(jfmny == null || new DZFDouble(jfmny).compareTo(DZFDouble.ZERO_DBL)==0){
			return "-";
		}
		return Common.format(jfmny);
	}
	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}
	public String getKm() {
		return km;
	}
	public void setKm(String km) {
		this.km = km;
	}
	public String getRq() {
		return rq;
	}
	public void setRq(String rq) {
		this.rq = rq;
	}
public DZFDouble getYe() {
		return ye;
	}
	public void setYe(DZFDouble ye) {
		this.ye = ye;
	}
	//	public String getYe() {
//		if(ye == null || new DZFDouble(ye).compareTo(DZFDouble.ZERO_DBL)==0){
//			return "--";
//		}
//		return Common.format(ye);
//	}
//	public void setYe(DZFDouble ye) {
//		this.ye = ye;
//	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public String getFx() {
		return fx;
	}
	public void setFx(String fx) {
		this.fx = fx;
	}
	
}
