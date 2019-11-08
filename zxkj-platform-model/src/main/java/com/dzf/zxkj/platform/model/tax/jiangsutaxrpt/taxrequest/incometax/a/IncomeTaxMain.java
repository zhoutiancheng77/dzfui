package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//A200000所得税月(季)度预缴纳税申报表  sb10412001VO
@TaxExcelPos(reportID = "10412001", reportname = "A200000所得税月(季)度预缴纳税申报表")
public class IncomeTaxMain {
	// 预缴方式
	@TaxExcelPos(row = 4, col = 1)
	private String yjfs;
	// 申报企业类型
	@TaxExcelPos(row = 5, col = 1)
	private String qylx;

	// 营业收入累计
	@TaxExcelPos(row = 8, col = 8)
	private DZFDouble yysrbnljje;
	// 营业成本累计
	@TaxExcelPos(row = 9, col = 8)
	private DZFDouble yycbbnljje;
	// 利润总额累计
	@TaxExcelPos(row = 10, col = 8)
	private DZFDouble lrzebnljje;
	// 特定业务计算的应纳税所得额累计
	@TaxExcelPos(row = 11, col = 8)
	private DZFDouble tdywjsynsdebnljje;
	// 不征税收入累计
	@TaxExcelPos(row = 12, col = 8)
	private DZFDouble bzssrbnljje;
	// 免税收入、减计收入、所得减免等优惠金额累计
	@TaxExcelPos(row = 13, col = 8)
	private DZFDouble msjjsdjmyhjebnljje;
	// 固定资产加速折旧（扣除）调减额累计金额
	@TaxExcelPos(row = 14, col = 8)
	private DZFDouble gdzcjszjkctjebnljje;
	// 弥补以前年度亏损累计
	@TaxExcelPos(row = 15, col = 8)
	private DZFDouble mbyqndksbnljje;
	// 实际利润额累计\按照上一纳税年度应纳税所得额平均额确定的应纳税所得额累计
	@TaxExcelPos(row = 16, col = 8)
	private DZFDouble sjlrebnljje;
	// 税率累计
	@TaxExcelPos(row = 17, col = 8)
	private DZFDouble sl;
	// 应纳所得税额累计
	@TaxExcelPos(row = 18, col = 8)
	private DZFDouble ynsdsebnljje;
	// 减免所得税额累计
	@TaxExcelPos(row = 19, col = 8)
	private DZFDouble jmsesebnljje;
	// 实际已缴所得税额累计
	@TaxExcelPos(row = 20, col = 8)
	private DZFDouble sjyjnsesebnljje;
	// 特定业务预缴（征）所得税额累计
	@TaxExcelPos(row = 21, col = 8)
	private DZFDouble tdywyjsdsebnljje;
	// 应补（退）所得税额累计\税务机关确定的本期应纳所得税额
	@TaxExcelPos(row = 22, col = 8)
	private DZFDouble bqybtsdsebnljje;

	// 总机构本期分摊应补（退）所得税额
	@TaxExcelPos(row = 24, col = 8)
	private DZFDouble zjgbqftybtsdsebnljje;

	// 总机构填报-总机构分摊比例
	@TaxExcelPos(row = 25, col = 7)
	private DZFDouble zjgftbl;
	// 总机构填报-其中：总机构分摊应补（退）所得税额
	@TaxExcelPos(row = 25, col = 8)
	private DZFDouble zjgftybtsdsebnljje;

	// 总机构填报-财政集中分配比例
	@TaxExcelPos(row = 26, col = 7)
	private DZFDouble zjgczjzftbl;
	// 总机构填报-财政集中分配应补（退）所得税额
	@TaxExcelPos(row = 26, col = 8)
	private DZFDouble czjzfpybtsdsebnljje;

	// 总机构填报-全部分支机构分摊比例
	@TaxExcelPos(row = 27, col = 4)
	private DZFDouble fzjgftbl;
	// 总机构填报-总机构具有主体生产经营职能部门分摊比例
	@TaxExcelPos(row = 27, col = 7)
	private DZFDouble dlscjybmftbl;
	// 总机构填报-总机构具有主体生产经营职能的部门分摊所得税额
	@TaxExcelPos(row = 27, col = 8)
	private DZFDouble zjgztscjybtftsdsebnljje;

	// 分支机构填报-分支机构本期分摊比例
	@TaxExcelPos(row = 28, col = 8)
	private DZFDouble fzjgbqftbl;
	// 分支机构填报-分支机构本期分摊应补（退）所得税额
	@TaxExcelPos(row = 29, col = 8)
	private DZFDouble fzjgbqftybtsdsebnljje;

	// 是否科技型中小企业
	@TaxExcelPos(row = 31, col = 8)
	private String kjxzxqy;
	// 是否高新技术企业
	@TaxExcelPos(row = 31, col = 3)
	private String gxjsqy;
	// 是否技术入股递延纳税事项
	@TaxExcelPos(row = 32, col = 3)
	private String kjrgdynssx;


	// 季初从业人数
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble qccyrs;
	// 季末从业人数
	@TaxExcelPos(row = 34, col = 8)
	private DZFDouble qmcyrs;
	// 季初资产总额（万元）
	@TaxExcelPos(row = 35, col = 3)
	private DZFDouble qczcze;
	// 季末资产总额（万元）
	@TaxExcelPos(row = 35, col = 8)
	private DZFDouble qmzcze;
	// 国家限制或禁止行业（Y是，N否）
	@TaxExcelPos(row = 36, col = 3)
	private String gjxzhjzhy;
	// 是否属于小型微利企业
	@TaxExcelPos(row = 36, col = 8)
	private String xxwlqy;

	public String getYjfs() {
		return yjfs;
	}

	public void setYjfs(String yjfs) {
		if ("按照实际利润额预缴".equals(yjfs)) {
			yjfs = "1";
		} else if ("按照上一纳税年度应纳税所得额平均额预缴".equals(yjfs)) {
			yjfs = "2";
		} else if ("按照税务机关确定的其他方法预缴".equals(yjfs)) {
			yjfs = "3";
		}
		this.yjfs = yjfs;
	}

	public String getQylx() {
		return qylx;
	}

	public void setQylx(String qylx) {
		if ("一般企业".equals(qylx)) {
			qylx = "0";
		} else if ("跨地区经营汇总纳税企业总机构".equals(qylx)) {
			qylx = "1";
		} else if ("跨地区经营汇总纳税企业分支机构".equals(qylx)) {
			qylx = "2";
		}
		this.qylx = qylx;
	}

	public DZFDouble getYysrbnljje() {
		return yysrbnljje;
	}

	public void setYysrbnljje(DZFDouble yysrbnljje) {
		this.yysrbnljje = yysrbnljje;
	}

	public DZFDouble getYycbbnljje() {
		return yycbbnljje;
	}

	public void setYycbbnljje(DZFDouble yycbbnljje) {
		this.yycbbnljje = yycbbnljje;
	}

	public DZFDouble getLrzebnljje() {
		return lrzebnljje;
	}

	public void setLrzebnljje(DZFDouble lrzebnljje) {
		this.lrzebnljje = lrzebnljje;
	}

	public DZFDouble getTdywjsynsdebnljje() {
		return tdywjsynsdebnljje;
	}

	public void setTdywjsynsdebnljje(DZFDouble tdywjsynsdebnljje) {
		this.tdywjsynsdebnljje = tdywjsynsdebnljje;
	}

	public DZFDouble getBzssrbnljje() {
		return bzssrbnljje;
	}

	public void setBzssrbnljje(DZFDouble bzssrbnljje) {
		this.bzssrbnljje = bzssrbnljje;
	}

	public DZFDouble getMsjjsdjmyhjebnljje() {
		return msjjsdjmyhjebnljje;
	}

	public void setMsjjsdjmyhjebnljje(DZFDouble msjjsdjmyhjebnljje) {
		this.msjjsdjmyhjebnljje = msjjsdjmyhjebnljje;
	}

	public DZFDouble getGdzcjszjkctjebnljje() {
		return gdzcjszjkctjebnljje;
	}

	public void setGdzcjszjkctjebnljje(DZFDouble gdzcjszjkctjebnljje) {
		this.gdzcjszjkctjebnljje = gdzcjszjkctjebnljje;
	}

	public DZFDouble getMbyqndksbnljje() {
		return mbyqndksbnljje;
	}

	public void setMbyqndksbnljje(DZFDouble mbyqndksbnljje) {
		this.mbyqndksbnljje = mbyqndksbnljje;
	}

	public DZFDouble getSjlrebnljje() {
		return sjlrebnljje;
	}

	public void setSjlrebnljje(DZFDouble sjlrebnljje) {
		this.sjlrebnljje = sjlrebnljje;
	}

	public DZFDouble getSl() {
		return sl;
	}

	public void setSl(DZFDouble sl) {
		this.sl = sl;
	}

	public DZFDouble getYnsdsebnljje() {
		return ynsdsebnljje;
	}

	public void setYnsdsebnljje(DZFDouble ynsdsebnljje) {
		this.ynsdsebnljje = ynsdsebnljje;
	}

	public DZFDouble getJmsesebnljje() {
		return jmsesebnljje;
	}

	public void setJmsesebnljje(DZFDouble jmsesebnljje) {
		this.jmsesebnljje = jmsesebnljje;
	}

	public DZFDouble getSjyjnsesebnljje() {
		return sjyjnsesebnljje;
	}

	public void setSjyjnsesebnljje(DZFDouble sjyjnsesebnljje) {
		this.sjyjnsesebnljje = sjyjnsesebnljje;
	}

	public DZFDouble getTdywyjsdsebnljje() {
		return tdywyjsdsebnljje;
	}

	public void setTdywyjsdsebnljje(DZFDouble tdywyjsdsebnljje) {
		this.tdywyjsdsebnljje = tdywyjsdsebnljje;
	}

	public DZFDouble getBqybtsdsebnljje() {
		return bqybtsdsebnljje;
	}

	public void setBqybtsdsebnljje(DZFDouble bqybtsdsebnljje) {
		this.bqybtsdsebnljje = bqybtsdsebnljje;
	}

	public DZFDouble getZjgbqftybtsdsebnljje() {
		return zjgbqftybtsdsebnljje;
	}

	public void setZjgbqftybtsdsebnljje(DZFDouble zjgbqftybtsdsebnljje) {
		this.zjgbqftybtsdsebnljje = zjgbqftybtsdsebnljje;
	}

	public DZFDouble getZjgftbl() {
		return zjgftbl;
	}

	public void setZjgftbl(DZFDouble zjgftbl) {
		this.zjgftbl = zjgftbl;
	}

	public DZFDouble getZjgftybtsdsebnljje() {
		return zjgftybtsdsebnljje;
	}

	public void setZjgftybtsdsebnljje(DZFDouble zjgftybtsdsebnljje) {
		this.zjgftybtsdsebnljje = zjgftybtsdsebnljje;
	}

	public DZFDouble getZjgczjzftbl() {
		return zjgczjzftbl;
	}

	public void setZjgczjzftbl(DZFDouble zjgczjzftbl) {
		this.zjgczjzftbl = zjgczjzftbl;
	}

	public DZFDouble getCzjzfpybtsdsebnljje() {
		return czjzfpybtsdsebnljje;
	}

	public void setCzjzfpybtsdsebnljje(DZFDouble czjzfpybtsdsebnljje) {
		this.czjzfpybtsdsebnljje = czjzfpybtsdsebnljje;
	}

	public DZFDouble getFzjgftbl() {
		return fzjgftbl;
	}

	public void setFzjgftbl(DZFDouble fzjgftbl) {
		this.fzjgftbl = fzjgftbl;
	}

	public DZFDouble getDlscjybmftbl() {
		return dlscjybmftbl;
	}

	public void setDlscjybmftbl(DZFDouble dlscjybmftbl) {
		this.dlscjybmftbl = dlscjybmftbl;
	}

	public DZFDouble getZjgztscjybtftsdsebnljje() {
		return zjgztscjybtftsdsebnljje;
	}

	public void setZjgztscjybtftsdsebnljje(DZFDouble zjgztscjybtftsdsebnljje) {
		this.zjgztscjybtftsdsebnljje = zjgztscjybtftsdsebnljje;
	}

	public DZFDouble getFzjgbqftbl() {
		return fzjgbqftbl;
	}

	public void setFzjgbqftbl(DZFDouble fzjgbqftbl) {
		this.fzjgbqftbl = fzjgbqftbl;
	}

	public DZFDouble getFzjgbqftybtsdsebnljje() {
		return fzjgbqftybtsdsebnljje;
	}

	public void setFzjgbqftybtsdsebnljje(DZFDouble fzjgbqftybtsdsebnljje) {
		this.fzjgbqftybtsdsebnljje = fzjgbqftybtsdsebnljje;
	}

	public String getXxwlqy() {
		return xxwlqy;
	}

	public void setXxwlqy(String xxwlqy) {
		switch (xxwlqy) {
		case "是":
			xxwlqy = "1";
			break;
		case "否":
			xxwlqy = "0";
			break;
		default:
			break;
		}
		this.xxwlqy = xxwlqy;
	}

	public String getKjxzxqy() {
		return kjxzxqy;
	}

	public void setKjxzxqy(String kjxzxqy) {
		this.kjxzxqy = castString(kjxzxqy);
	}

	public String getGxjsqy() {
		return gxjsqy;
	}

	public void setGxjsqy(String gxjsqy) {
		this.gxjsqy = castString(gxjsqy);
	}

	public String getKjrgdynssx() {
		return kjrgdynssx;
	}

	public void setKjrgdynssx(String kjrgdynssx) {
		this.kjrgdynssx = castString(kjrgdynssx);
	}

	public DZFDouble getQmcyrs() {
		return qmcyrs;
	}

	public void setQmcyrs(DZFDouble qmcyrs) {
		this.qmcyrs = qmcyrs;
	}

	public DZFDouble getQccyrs() {
		return qccyrs;
	}

	public void setQccyrs(DZFDouble qccyrs) {
		this.qccyrs = qccyrs;
	}

	public DZFDouble getQczcze() {
		return qczcze;
	}

	public void setQczcze(DZFDouble qczcze) {
		this.qczcze = qczcze;
	}

	public DZFDouble getQmzcze() {
		return qmzcze;
	}

	public void setQmzcze(DZFDouble qmzcze) {
		this.qmzcze = qmzcze;
	}

	public String getGjxzhjzhy() {
		return gjxzhjzhy;
	}

	public void setGjxzhjzhy(String gjxzhjzhy) {
		this.gjxzhjzhy = castString(gjxzhjzhy);
	}

	private String castString(String str) {
		if ("是".equals(str)) {
			str = "Y";
		} else if ("否".equals(str)) {
			str = "N";
		}
		return str;
	}
}
