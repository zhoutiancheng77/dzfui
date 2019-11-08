package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//A201030减免所得税优惠明细表 sb10412004VO
@TaxExcelPos(reportID = "10412004", reportname = "A201030减免所得税优惠明细表")
public class IncomeTaxAttach3 {
	// 符合条件的小型微利企业减免企业所得税
	@TaxExcelPos(row = 4, col = 6)
	private DZFDouble h1;
	// 国家需要重点扶持的高新技术企业减按15%的税率征收企业所得税
	@TaxExcelPos(row = 5, col = 6)
	private DZFDouble h2;
	// 经济特区和上海浦东新区新设立的高新技术企业在区内取得的所得定期减免企业所得税
	@TaxExcelPos(row = 6, col = 6)
	private DZFDouble h3;
	// 受灾地区农村信用社免征企业所得税
	@TaxExcelPos(row = 7, col = 6)
	private DZFDouble h4;
	// 动漫企业自主开发、生产动漫产品定期减免企业所得税
	@TaxExcelPos(row = 8, col = 6)
	private DZFDouble h5;
	// 线宽小于0.8微米（含）的集成电路生产企业减免企业所得税
	@TaxExcelPos(row = 9, col = 6)
	private DZFDouble h6;
	// 线宽小于0.25微米的集成电路生产企业减按15%税率征收企业所得税
	@TaxExcelPos(row = 10, col = 6)
	private DZFDouble h7;
	// 投资额超过80亿元的集成电路生产企业减按15%税率征收企业所得税
	@TaxExcelPos(row = 11, col = 6)
	private DZFDouble h8;
	// 线宽小于0.25微米的集成电路生产企业减免企业所得税
	@TaxExcelPos(row = 12, col = 6)
	private DZFDouble h9;
	// 投资额超过80亿元的集成电路生产企业减免企业所得税
	@TaxExcelPos(row = 13, col = 6)
	private DZFDouble h10;
	// 线宽小于130纳米的集成电路生产企业减免企业所得税
	@TaxExcelPos(row = 14, col = 6)
	private DZFDouble h11;
	// 线宽小于65纳米或投资额超过150亿元的集成电路生产企业减免企业所得税
	@TaxExcelPos(row = 15, col = 6)
	private DZFDouble h12;
	// 新办集成电路设计企业减免企业所得税
	@TaxExcelPos(row = 16, col = 6)
	private DZFDouble h13;
	// 国家规划布局内集成电路设计企业可减按10%的税率征收企业所得税
	@TaxExcelPos(row = 17, col = 6)
	private DZFDouble h14;
	// 符合条件的软件企业减免企业所得税
	@TaxExcelPos(row = 18, col = 6)
	private DZFDouble h15;
	// 国家规划布局内重点软件企业可减按10%的税率征收企业所得税
	@TaxExcelPos(row = 19, col = 6)
	private DZFDouble h16;
	// 符合条件的集成电路封装、测试企业定期减免企业所得税
	@TaxExcelPos(row = 20, col = 6)
	private DZFDouble h17;
	// 符合条件的集成电路关键专用材料生产企业、集成电路专用设备生产企业定期减免企业所得税
	@TaxExcelPos(row = 21, col = 6)
	private DZFDouble h18;
	// 经营性文化事业单位转制为企业的免征企业所得税
	@TaxExcelPos(row = 22, col = 6)
	private DZFDouble h19;
	// 二十、符合条件的生产和装配伤残人员专门用品企业免征企业所得税
	@TaxExcelPos(row = 23, col = 6)
	private DZFDouble h20;
	// 技术先进型服务企业减按15%的税率征收企业所得税
	@TaxExcelPos(row = 24, col = 6)
	private DZFDouble h21;
	// 服务贸易创新发展试点地区符合条件的技术先进型服务企业减按15%的税率征收企业所得税
	@TaxExcelPos(row = 25, col = 6)
	private DZFDouble h22;
	// 设在西部地区的鼓励类产业企业减按15%的税率征收企业所得税
	@TaxExcelPos(row = 26, col = 6)
	private DZFDouble h23;
	// 新疆困难地区新办企业定期减免企业所得税
	@TaxExcelPos(row = 27, col = 6)
	private DZFDouble h24;
	// 新疆喀什、霍尔果斯特殊经济开发区新办企业定期免征企业所得税
	@TaxExcelPos(row = 28, col = 6)
	private DZFDouble h25;
	// 广东横琴、福建平潭、深圳前海等地区的鼓励类产业企业减按15%税率征收企业所得税
	@TaxExcelPos(row = 29, col = 6)
	private DZFDouble h26;
	// 北京冬奥组委、北京冬奥会测试赛赛事组委会免征企业所得税
	@TaxExcelPos(row = 30, col = 6)
	private DZFDouble h27;
	// 二十八、其他
	@TaxExcelPos(row = 31, col = 6)
	private DZFDouble h28;
	//  1. 从事污染防治的第三方企业减按15%的税率征收企业所得税
	@TaxExcelPos(row = 32, col = 6)
	private DZFDouble h28new1;
	// 2.其他
	@TaxExcelPos(row = 33, col = 6)
	private DZFDouble h28new2;

	// 减征免征类型
	private String jzmzlx;
	// 减征幅度
	private DZFDouble jzfd;
	// 民族自治地方的自治机关对本民族自治地方的企业应缴纳的企业所得税中属于地方分享的部分减征或免征
	@TaxExcelPos(row = 34, col = 6)
	private DZFDouble h29;
	// 合计
	@TaxExcelPos(row = 35, col = 6)
	private DZFDouble h30;

	public DZFDouble getH1() {
		return h1;
	}

	public void setH1(DZFDouble h1) {
		this.h1 = h1;
	}

	public DZFDouble getH2() {
		return h2;
	}

	public void setH2(DZFDouble h2) {
		this.h2 = h2;
	}

	public DZFDouble getH3() {
		return h3;
	}

	public void setH3(DZFDouble h3) {
		this.h3 = h3;
	}

	public DZFDouble getH4() {
		return h4;
	}

	public void setH4(DZFDouble h4) {
		this.h4 = h4;
	}

	public DZFDouble getH5() {
		return h5;
	}

	public void setH5(DZFDouble h5) {
		this.h5 = h5;
	}

	public DZFDouble getH6() {
		return h6;
	}

	public void setH6(DZFDouble h6) {
		this.h6 = h6;
	}

	public DZFDouble getH7() {
		return h7;
	}

	public void setH7(DZFDouble h7) {
		this.h7 = h7;
	}

	public DZFDouble getH8() {
		return h8;
	}

	public void setH8(DZFDouble h8) {
		this.h8 = h8;
	}

	public DZFDouble getH9() {
		return h9;
	}

	public void setH9(DZFDouble h9) {
		this.h9 = h9;
	}

	public DZFDouble getH10() {
		return h10;
	}

	public void setH10(DZFDouble h10) {
		this.h10 = h10;
	}

	public DZFDouble getH11() {
		return h11;
	}

	public void setH11(DZFDouble h11) {
		this.h11 = h11;
	}

	public DZFDouble getH12() {
		return h12;
	}

	public void setH12(DZFDouble h12) {
		this.h12 = h12;
	}

	public DZFDouble getH13() {
		return h13;
	}

	public void setH13(DZFDouble h13) {
		this.h13 = h13;
	}

	public DZFDouble getH14() {
		return h14;
	}

	public void setH14(DZFDouble h14) {
		this.h14 = h14;
	}

	public DZFDouble getH15() {
		return h15;
	}

	public void setH15(DZFDouble h15) {
		this.h15 = h15;
	}

	public DZFDouble getH16() {
		return h16;
	}

	public void setH16(DZFDouble h16) {
		this.h16 = h16;
	}

	public DZFDouble getH17() {
		return h17;
	}

	public void setH17(DZFDouble h17) {
		this.h17 = h17;
	}

	public DZFDouble getH18() {
		return h18;
	}

	public void setH18(DZFDouble h18) {
		this.h18 = h18;
	}

	public DZFDouble getH19() {
		return h19;
	}

	public void setH19(DZFDouble h19) {
		this.h19 = h19;
	}

	public DZFDouble getH20() {
		return h20;
	}

	public void setH20(DZFDouble h20) {
		this.h20 = h20;
	}

	public DZFDouble getH21() {
		return h21;
	}

	public void setH21(DZFDouble h21) {
		this.h21 = h21;
	}

	public DZFDouble getH22() {
		return h22;
	}

	public void setH22(DZFDouble h22) {
		this.h22 = h22;
	}

	public DZFDouble getH23() {
		return h23;
	}

	public void setH23(DZFDouble h23) {
		this.h23 = h23;
	}

	public DZFDouble getH24() {
		return h24;
	}

	public void setH24(DZFDouble h24) {
		this.h24 = h24;
	}

	public DZFDouble getH25() {
		return h25;
	}

	public void setH25(DZFDouble h25) {
		this.h25 = h25;
	}

	public DZFDouble getH26() {
		return h26;
	}

	public void setH26(DZFDouble h26) {
		this.h26 = h26;
	}

	public DZFDouble getH27() {
		return h27;
	}

	public void setH27(DZFDouble h27) {
		this.h27 = h27;
	}

	public DZFDouble getH28() {
		return h28;
	}

	public void setH28(DZFDouble h28) {
		this.h28 = h28;
	}

	public DZFDouble getH28new1() {
		return h28new1;
	}

	public void setH28new1(DZFDouble h28new1) {
		this.h28new1 = h28new1;
	}

	public DZFDouble getH28new2() {
		return h28new2;
	}

	public void setH28new2(DZFDouble h28new2) {
		this.h28new2 = h28new2;
	}

	public String getJzmzlx() {
		return jzmzlx;
	}

	public void setJzmzlx(String jzmzlx) {
		this.jzmzlx = jzmzlx;
	}

	public DZFDouble getJzfd() {
		return jzfd;
	}

	public void setJzfd(DZFDouble jzfd) {
		this.jzfd = jzfd;
	}

	public DZFDouble getH29() {
		return h29;
	}

	public void setH29(DZFDouble h29) {
		this.h29 = h29;
	}

	public DZFDouble getH30() {
		return h30;
	}

	public void setH30(DZFDouble h30) {
		this.h30 = h30;
	}

}
