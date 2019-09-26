package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

public class QmphVO extends SuperVO {

	private DZFDouble jf;// 本年借方期末累计;
	private DZFDouble df;// 本年贷方期末累计;
	private DZFDouble ce;// 差额;
	private String num;
	private String dhres;//凭证断号的数字
	private DZFBoolean pztemp;//凭证暂存
	private String res;// 结果;
    private String kmred;//科目是否存在赤字
    private String zcfzblan;//资产负债是否平衡 
    private String kmcz;//查询科目是否存在赤字
	private DZFBoolean ishasjz;//检查科目是否存在没记账
	private DZFBoolean issyjz;//是否损益结转
	private boolean success = false;
	
	public DZFBoolean getPztemp() {
		return pztemp;
	}
	public void setPztemp(DZFBoolean pztemp) {
		this.pztemp = pztemp;
	}
	public DZFBoolean getIssyjz() {
		return issyjz;
	}
	public void setIssyjz(DZFBoolean issyjz) {
		this.issyjz = issyjz;
	}
	public String getKmcz() {
		return kmcz;
	}
	public void setKmcz(String kmcz) {
		this.kmcz = kmcz;
	}
	public String getKmred() {
		return kmred;
	}
	public void setKmred(String kmred) {
		this.kmred = kmred;
	}
	public String getZcfzblan() {
		return zcfzblan;
	}
	public void setZcfzblan(String zcfzblan) {
		this.zcfzblan = zcfzblan;
	}
	public DZFBoolean getIshasjz() {
		return ishasjz;
	}
	public void setIshasjz(DZFBoolean ishasjz) {
		this.ishasjz = ishasjz;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getDhres() {
		return dhres;
	}
	public void setDhres(String dhres) {
		this.dhres = dhres;
	}
	public DZFDouble getJf() {
		return jf;
	}
	public void setJf(DZFDouble jf) {
		this.jf = jf;
	}
	public DZFDouble getDf() {
		return df;
	}
	public void setDf(DZFDouble df) {
		this.df = df;
	}
	public DZFDouble getCe() {
		return ce;
	}
	public void setCe(DZFDouble ce) {
		this.ce = ce;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
