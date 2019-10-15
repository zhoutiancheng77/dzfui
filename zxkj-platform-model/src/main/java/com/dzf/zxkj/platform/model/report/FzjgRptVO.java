package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 企业所得税汇总纳税分支机构所得税分配表
 * 江苏表样
 *
 */

public class FzjgRptVO extends SuperVO {

	private DZFDouble fpse;//分配所得税额
	private String fzjgdjxh;//分支机构登记序号
	private String zgswjgmc;
	private DZFDouble fzjggzze;//职工薪酬
	private String fzjgnsrsbh;//分支机构纳税人识别号
	private String fzjgmc;//分支机构名称
	private DZFDouble fpbl;//分配比例
	private DZFDouble xsdfjmfd;
	private DZFDouble fzjgzcze;//资产总额
	private String fzjglxlb;//分支机构类型类别
	private DZFDouble xsdfjmje;
	private DZFDouble fzjgsrze;//营业收入
	
	public DZFDouble getFpse() {
		return fpse;
	}
	public void setFpse(DZFDouble fpse) {
		this.fpse = fpse;
	}
	public String getFzjgdjxh() {
		return fzjgdjxh;
	}
	public void setFzjgdjxh(String fzjgdjxh) {
		this.fzjgdjxh = fzjgdjxh;
	}
	public String getZgswjgmc() {
		return zgswjgmc;
	}
	public void setZgswjgmc(String zgswjgmc) {
		this.zgswjgmc = zgswjgmc;
	}
	public DZFDouble getFzjggzze() {
		return fzjggzze;
	}
	public void setFzjggzze(DZFDouble fzjggzze) {
		this.fzjggzze = fzjggzze;
	}
	public String getFzjgnsrsbh() {
		return fzjgnsrsbh;
	}
	public void setFzjgnsrsbh(String fzjgnsrsbh) {
		this.fzjgnsrsbh = fzjgnsrsbh;
	}
	public String getFzjgmc() {
		return fzjgmc;
	}
	public void setFzjgmc(String fzjgmc) {
		this.fzjgmc = fzjgmc;
	}
	public DZFDouble getFpbl() {
		return fpbl;
	}
	public void setFpbl(DZFDouble fpbl) {
		this.fpbl = fpbl;
	}
	public DZFDouble getXsdfjmfd() {
		return xsdfjmfd;
	}
	public void setXsdfjmfd(DZFDouble xsdfjmfd) {
		this.xsdfjmfd = xsdfjmfd;
	}
	public DZFDouble getFzjgzcze() {
		return fzjgzcze;
	}
	public void setFzjgzcze(DZFDouble fzjgzcze) {
		this.fzjgzcze = fzjgzcze;
	}
	public String getFzjglxlb() {
		return fzjglxlb;
	}
	public void setFzjglxlb(String fzjglxlb) {
		this.fzjglxlb = fzjglxlb;
	}
	public DZFDouble getXsdfjmje() {
		return xsdfjmje;
	}
	public void setXsdfjmje(DZFDouble xsdfjmje) {
		this.xsdfjmje = xsdfjmje;
	}
	public DZFDouble getFzjgsrze() {
		return fzjgsrze;
	}
	public void setFzjgsrze(DZFDouble fzjgsrze) {
		this.fzjgsrze = fzjgsrze;
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
}