package com.dzf.zxkj.platform.model.zcgl;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 资产明细账VO
 * @author zhangj
 *
 */
public class ZcMxZVO extends SuperVO {
	
	// 期间
	private String titlePeriod;
	
	private String period;
	
	private String gs;
	
	private DZFDate rq ;
	private String qj;
	private String zcbh ;
	private String zcmc ;
	private String zclb ;
	private String zcsx ;
	private String ywdh ;
	private String pzzh ;
	private String zy ;
	private DZFDouble yzjf ;
	private DZFDouble yzdf ;
	private DZFDouble yzye ;
	private DZFDouble ljjf ;
	private DZFDouble ljdf ;
	private DZFDouble ljye ;
	private DZFDouble jzye ;
	private String pzpk;
	
	
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}
	public DZFDate getRq() {
		return rq;
	}
	public void setRq(DZFDate rq) {
		this.rq = rq;
	}
	public String getQj() {
		return qj;
	}
	public void setQj(String qj) {
		this.qj = qj;
	}
	public String getZcbh() {
		return zcbh;
	}
	public void setZcbh(String zcbh) {
		this.zcbh = zcbh;
	}
	public String getZcmc() {
		return zcmc;
	}
	public void setZcmc(String zcmc) {
		this.zcmc = zcmc;
	}
	public String getZclb() {
		return zclb;
	}
	public void setZclb(String zclb) {
		this.zclb = zclb;
	}
	public String getZcsx() {
		return zcsx;
	}
	public void setZcsx(String zcsx) {
		this.zcsx = zcsx;
	}
	public String getYwdh() {
		return ywdh;
	}
	public void setYwdh(String ywdh) {
		this.ywdh = ywdh;
	}
	public String getPzzh() {
		return pzzh;
	}
	public void setPzzh(String pzzh) {
		this.pzzh = pzzh;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public DZFDouble getYzjf() {
		return yzjf;
	}
	public void setYzjf(DZFDouble yzjf) {
		this.yzjf = yzjf;
	}
	public DZFDouble getYzdf() {
		return yzdf;
	}
	public void setYzdf(DZFDouble yzdf) {
		this.yzdf = yzdf;
	}
	public DZFDouble getYzye() {
		return yzye;
	}
	public void setYzye(DZFDouble yzye) {
		this.yzye = yzye;
	}
	public DZFDouble getLjjf() {
		return ljjf;
	}
	public void setLjjf(DZFDouble ljjf) {
		this.ljjf = ljjf;
	}
	public DZFDouble getLjdf() {
		return ljdf;
	}
	public void setLjdf(DZFDouble ljdf) {
		this.ljdf = ljdf;
	}
	public DZFDouble getLjye() {
		return ljye;
	}
	public void setLjye(DZFDouble ljye) {
		this.ljye = ljye;
	}
	public DZFDouble getJzye() {
		return jzye;
	}
	public void setJzye(DZFDouble jzye) {
		this.jzye = jzye;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return null;
	}
	public String getPzpk() {
		return pzpk;
	}
	public void setPzpk(String pzpk) {
		this.pzpk = pzpk;
	}
	public String getTitlePeriod() {
		return titlePeriod;
	}
	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}
	
	
}
