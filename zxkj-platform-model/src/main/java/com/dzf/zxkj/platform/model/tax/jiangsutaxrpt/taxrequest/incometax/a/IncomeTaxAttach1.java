package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// A201010免税收入、减计收入、所得减免等优惠明细表 sb10412002VO
@TaxExcelPos(reportID = "10412002", reportname = "A201010免税收入、减计收入、所得减免等优惠明细表")
public class IncomeTaxAttach1 {
    // 一、免税收入-累计金额
    @TaxExcelPos(row = 5, col = 5)
    private DZFDouble h1;
    // （一）国债利息收入免征企业所得税-累计金额
    @TaxExcelPos(row = 6, col = 5)
    private DZFDouble h2;
    // （二）符合条件的居民企业之间的股息、红利等权益性投资收益免征企业所得税-累计金额
    @TaxExcelPos(row = 7, col = 5)
    private DZFDouble h3;
    // 其中：内地居民企业通过沪港通投资且连续持有H股满12个月取得的股息红利所得免征企业所得税-累计金额
    @TaxExcelPos(row = 8, col = 5)
    private DZFDouble h4;
    // 内地居民企业通过深港通投资且连续持有H股满12个月取得的股息红利所得免征企业所得税-累计金额
    @TaxExcelPos(row = 9, col = 5)
    private DZFDouble h5;
    // 居民企业持有创新企业CDR取得的股息红利所得免征企业所得税
    @TaxExcelPos(row = 10, col = 5)
    private DZFDouble h6new;
    // 符合条件的居民企业之间属于股息、红利性质的永续债利息收入免征企业所得税
    @TaxExcelPos(row = 11, col = 5)
    private DZFDouble h7new;
    // （三）符合条件的非营利组织的收入免征企业所得税-累计金额
    @TaxExcelPos(row = 12, col = 5)
    private DZFDouble h6;

    // （四）中国清洁发展机制基金取得的收入免征企业所得税-累计金额
    @TaxExcelPos(row = 13, col = 5)
    private DZFDouble h9;
    // （五）投资者从证券投资基金分配中取得的收入免征企业所得税-累计金额
    @TaxExcelPos(row = 14, col = 5)
    private DZFDouble h10;
    // （六）取得的地方政府债券利息收入免征企业所得税-累计金额
    @TaxExcelPos(row = 15, col = 5)
    private DZFDouble h11;
    // （七）中国保险保障基金有限责任公司取得的保险保障基金等收入免征企业所得税-累计金额
    @TaxExcelPos(row = 16, col = 5)
    private DZFDouble h12;
    // （八）中国奥委会取得北京冬奥组委支付的收入免征企业所得税-累计金额
    @TaxExcelPos(row = 17, col = 5)
    private DZFDouble h13;
    // （九）中国残奥委会取得北京冬奥组委分期支付的收入免征企业所得税-累计金额
    @TaxExcelPos(row = 18, col = 5)
    private DZFDouble h14;
    // （十）其他-累计金额
    @TaxExcelPos(row = 19, col = 5)
    private DZFDouble h15;

    // 二、减计收入-累计金额
    @TaxExcelPos(row = 20, col = 5)
    private DZFDouble h16;
    // （一）综合利用资源生产产品取得的收入在计算应纳税所得额时减计收入-累计金额
    @TaxExcelPos(row = 21, col = 5)
    private DZFDouble h17;
    // （二）金融、保险等机构取得的涉农利息、保费减计收入-累计金额
    @TaxExcelPos(row = 22, col = 5)
    private DZFDouble h18;
    // 1.金融机构取得的涉农贷款利息收入在计算应纳税所得额时减计收入-累计金额
    @TaxExcelPos(row = 23, col = 5)
    private DZFDouble h19;
    // 2.保险机构取得的涉农保费收入在计算应纳税所得额时减计收入-累计金额
    @TaxExcelPos(row = 24, col = 5)
    private DZFDouble h20;
    // 3.小额贷款公司取得的农户小额贷款利息收入在计算应纳税所得额时减计收入-累计金额
    @TaxExcelPos(row = 25, col = 5)
    private DZFDouble h21;
    // （三）取得铁路债券利息收入减半征收企业所得税-累计金额
    @TaxExcelPos(row = 26, col = 5)
    private DZFDouble h22;
    // （四）其他-累计金额
    @TaxExcelPos(row = 27, col = 5)
    private DZFDouble h23;
    // 1.取得的社区家庭服务收入在计算应纳税所得额时减计收入
    @TaxExcelPos(row = 28, col = 5)
    private DZFDouble h23new1;
    //  2.其他
    @TaxExcelPos(row = 29, col = 5)
    private DZFDouble h23new2;

    // 三、加计扣除-累计金额
    @TaxExcelPos(row = 30, col = 5)
    private DZFDouble h24;
    // （一）开发新技术、新产品、新工艺发生的研究开发费用加计扣除-累计金额
    @TaxExcelPos(row = 31, col = 5)
    private DZFDouble h25;
    // （二）科技型中小企业开发新技术、新产品、新工艺发生的研究开发费用加计扣除-累计金额
    @TaxExcelPos(row = 32, col = 5)
    private DZFDouble h26;
    // （三）企业为获得创新性、创意性、突破性的产品进行创意设计活动而发生的相关费用加计扣除-累计金额
    @TaxExcelPos(row = 33, col = 5)
    private DZFDouble h27;
    // （四）安置残疾人员所支付的工资加计扣除-累计金额
    @TaxExcelPos(row = 34, col = 5)
    private DZFDouble h28;

    // 四、所得减免-累计金额
    @TaxExcelPos(row = 35, col = 5)
    private DZFDouble h29;
    // （一）从事农、林、牧、渔业项目的所得减免征收企业所得税-累计金额
    @TaxExcelPos(row = 36, col = 5)
    private DZFDouble h30;
    // 1.免税项目-累计金额
    @TaxExcelPos(row = 37, col = 5)
    private DZFDouble h31;
    // 2.减半征收项目-累计金额
    @TaxExcelPos(row = 38, col = 5)
    private DZFDouble h32;
    // （二）从事国家重点扶持的公共基础设施项目投资经营的所得定期减免企业所得税-累计金额
    @TaxExcelPos(row = 39, col = 5)
    private DZFDouble h33;
    // 其中：从事农村饮水工程项目投资经营的所得定期减免企业所得税
    @TaxExcelPos(row = 40, col = 5)
    private DZFDouble h33new1;
    // （三）从事符合条件的环境保护、节能节水项目的所得定期减免企业所得税-累计金额
    @TaxExcelPos(row = 41, col = 5)
    private DZFDouble h34;
    // （四）符合条件的技术转让所得减免征收企业所得税-累计金额
    @TaxExcelPos(row = 42, col = 5)
    private DZFDouble h35;
    // （五）实施清洁发展机制项目的所得定期减免企业所得税-累计金额
    @TaxExcelPos(row = 43, col = 5)
    private DZFDouble h36;
    // （六）符合条件的节能服务公司实施合同能源管理项目的所得定期减免企业所得税-累计金额
    @TaxExcelPos(row = 44, col = 5)
    private DZFDouble h37;
    // （七）线宽小于130纳米的集成电路生产项目的所得减免企业所得税-累计金额
    @TaxExcelPos(row = 45, col = 5)
    private DZFDouble h38;
    // （八）线宽小于65纳米或投资额超过150亿元的集成电路生产项目的所得减免企业所得税-累计金额
    @TaxExcelPos(row = 46, col = 5)
    private DZFDouble h39;
    // （九）其他-累计金额
    @TaxExcelPos(row = 47, col = 5)
    private DZFDouble h40;

    // 合计-累计金额
    @TaxExcelPos(row = 48, col = 5)
    private DZFDouble h41;

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

    public DZFDouble getH6new() {
        return h6new;
    }

    public void setH6new(DZFDouble h6new) {
        this.h6new = h6new;
    }

    public DZFDouble getH7new() {
        return h7new;
    }

    public void setH7new(DZFDouble h7new) {
        this.h7new = h7new;
    }

    public DZFDouble getH6() {
        return h6;
    }

    public void setH6(DZFDouble h6) {
        this.h6 = h6;
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

    public DZFDouble getH23new1() {
        return h23new1;
    }

    public void setH23new1(DZFDouble h23new1) {
        this.h23new1 = h23new1;
    }

    public DZFDouble getH23new2() {
        return h23new2;
    }

    public void setH23new2(DZFDouble h23new2) {
        this.h23new2 = h23new2;
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

    public DZFDouble getH31() {
        return h31;
    }

    public void setH31(DZFDouble h31) {
        this.h31 = h31;
    }

    public DZFDouble getH32() {
        return h32;
    }

    public void setH32(DZFDouble h32) {
        this.h32 = h32;
    }

    public DZFDouble getH33() {
        return h33;
    }

    public void setH33(DZFDouble h33) {
        this.h33 = h33;
    }

    public DZFDouble getH33new1() {
        return h33new1;
    }

    public void setH33new1(DZFDouble h33new1) {
        this.h33new1 = h33new1;
    }

    public DZFDouble getH34() {
        return h34;
    }

    public void setH34(DZFDouble h34) {
        this.h34 = h34;
    }

    public DZFDouble getH35() {
        return h35;
    }

    public void setH35(DZFDouble h35) {
        this.h35 = h35;
    }

    public DZFDouble getH36() {
        return h36;
    }

    public void setH36(DZFDouble h36) {
        this.h36 = h36;
    }

    public DZFDouble getH37() {
        return h37;
    }

    public void setH37(DZFDouble h37) {
        this.h37 = h37;
    }

    public DZFDouble getH38() {
        return h38;
    }

    public void setH38(DZFDouble h38) {
        this.h38 = h38;
    }

    public DZFDouble getH39() {
        return h39;
    }

    public void setH39(DZFDouble h39) {
        this.h39 = h39;
    }

    public DZFDouble getH40() {
        return h40;
    }

    public void setH40(DZFDouble h40) {
        this.h40 = h40;
    }

    public DZFDouble getH41() {
        return h41;
    }

    public void setH41(DZFDouble h41) {
        this.h41 = h41;
    }
}
