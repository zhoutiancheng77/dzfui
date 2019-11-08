package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10412007", reportname = "技术成果投资入股企业所得税递延纳税备案表", rowBegin = 6, rowEnd = 13, col = 1)
public class TechnicalAchievementDetail {
	// 技术成果名称
	@TaxExcelPos(col = 1)
	private String jscglx;
	// 技术成果类型
	@TaxExcelPos(col = 2)
	private String jscgmc;
	// 技术成果编号
	@TaxExcelPos(col = 3)
	private String jscgbh;

	// 公允价值
	@TaxExcelPos(col = 4)
	private DZFDouble gyjz;
	// 计税基础
	@TaxExcelPos(col = 5)
	private DZFDouble jsjc;
	// 股权取得时间
	@TaxExcelPos(col = 6)
	private String qdgqsj;
	// 递延所得
	@TaxExcelPos(col = 7)
	private DZFDouble dysd;
	// 被投资企业名称
	@TaxExcelPos(col = 9)
	private String qymc;
	// 被投资纳税人识别号
	@TaxExcelPos(col = 10)
	private String nsrsbh;
	// 税务机关名称
	@TaxExcelPos(col = 11)
	private String zgswjg;
	// 与投资方是否为关联企业
	@TaxExcelPos(col = 13)
	private String ytzfsfglqy;
	// 备注
	@TaxExcelPos(col = 14)
	private String bz;

	public String getJscglx() {
		return jscglx;
	}

	public void setJscglx(String jscglx) {
		this.jscglx = jscglx;
	}

	public String getJscgmc() {
		return jscgmc;
	}

	public void setJscgmc(String jscgmc) {
		switch (jscgmc) {
		case "1专利技术":
			jscgmc = "1";
			break;
		case "2计算机软件著作权":
			jscgmc = "2";
			break;
		case "3集成电路布图设计权":
			jscgmc = "3";
			break;
		case "4植物新品种(权)":
			jscgmc = "4";
			break;
		case "5生物医药新品种":
			jscgmc = "5";
			break;
		case "Z其他技术成果":
			jscgmc = "Z";
			break;
		default:
			break;
		}
		this.jscgmc = jscgmc;
	}

	public String getJscgbh() {
		return jscgbh;
	}

	public void setJscgbh(String jscgbh) {
		this.jscgbh = jscgbh;
	}

	public DZFDouble getGyjz() {
		return gyjz;
	}

	public void setGyjz(DZFDouble gyjz) {
		this.gyjz = gyjz;
	}

	public DZFDouble getJsjc() {
		return jsjc;
	}

	public void setJsjc(DZFDouble jsjc) {
		this.jsjc = jsjc;
	}

	public String getQdgqsj() {
		return qdgqsj;
	}

	public void setQdgqsj(String qdgqsj) {
		this.qdgqsj = qdgqsj;
	}

	public DZFDouble getDysd() {
		return dysd;
	}

	public void setDysd(DZFDouble dysd) {
		this.dysd = dysd;
	}

	public String getQymc() {
		return qymc;
	}

	public void setQymc(String qymc) {
		this.qymc = qymc;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getZgswjg() {
		return zgswjg;
	}

	public void setZgswjg(String zgswjg) {
		this.zgswjg = zgswjg;
	}

	public String getYtzfsfglqy() {
		return ytzfsfglqy;
	}

	public void setYtzfsfglqy(String ytzfsfglqy) {
		switch (ytzfsfglqy) {
		case "Y是":
			ytzfsfglqy = "Y";
			break;
		case "N否":
			ytzfsfglqy = "N";
			break;
		default:
			break;
		}
		this.ytzfsfglqy = ytzfsfglqy;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

}
