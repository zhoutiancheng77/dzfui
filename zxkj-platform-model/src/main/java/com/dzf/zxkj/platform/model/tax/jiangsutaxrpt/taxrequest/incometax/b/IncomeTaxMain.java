package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 主表 sb10413001VO
@TaxExcelPos(reportID = "10413001", reportname = "主表")
public class IncomeTaxMain {
    // 征收方式代码
    @TaxExcelPos(row = 4, col = 2)
    private String hdzsfs;

    // 收入总额累计
    @TaxExcelPos(row = 6, col = 9)
    private DZFDouble h1_srzje;
    // 不征税收入累计
    @TaxExcelPos(row = 7, col = 9)
    private DZFDouble h2_bzssrje;
    // 免税收入累计
    @TaxExcelPos(row = 8, col = 9)
    private DZFDouble h3_mssrje;
    // 国债利息收入累计金额
    @TaxExcelPos(row = 9, col = 9)
    private DZFDouble h4_gzlxsrje;
    // 符合条件居民企业之间股息红利等权益性收益累计
    @TaxExcelPos(row = 10, col = 9)
    private DZFDouble h5_fhtjjmqysrje;
    // 其中：通过沪港通投资且连续持有H股满12个月取得的股息红利所得免征企业所得税l累计
    @TaxExcelPos(row = 11, col = 9)
    private DZFDouble h6_hgttzsrje;
    // 通过深港通投资且连续持有H股满12个月取得的股息红利所得免征企业所得税累计
    @TaxExcelPos(row = 12, col = 9)
    private DZFDouble h7_sgttzsrje;
    // 居民企业持有创新企业CDR取得的股息红利所得免征企业所得税
    @TaxExcelPos(row = 13, col = 5)
    private DZFDouble h8_cxqygxhlmzqysds;
    // 符合条件的居民企业之间属于股息、红利性质的永续债利息收入免征企业所得税
    @TaxExcelPos(row = 14, col = 5)
    private DZFDouble h9_yxzlxsrmzqysds;
    // 投资者从证券投资基金分配中取得的收入免征企业所得税累计
    @TaxExcelPos(row = 15, col = 9)
    private DZFDouble h10_tzzqjjsrje;
    // 地方政府债券利息收入累计金额
    @TaxExcelPos(row = 16, col = 9)
    private DZFDouble h11_dfzfzqsrje;
    // 应税收入额||成本费用总额
    @TaxExcelPos(row = 17, col = 9)
    private DZFDouble h12_yssrcbsrje;
    // 税务机关核定的应税所得率
    @TaxExcelPos(row = 18, col = 9)
    private DZFDouble h13_hdsdl;
    // 应纳税所得额累计
    @TaxExcelPos(row = 19, col = 9)
    private DZFDouble h14_ynssdeje;
    // 税率
    @TaxExcelPos(row = 20, col = 9)
    private DZFDouble h15_sl;
    // 应纳所得税额累计
    @TaxExcelPos(row = 21, col = 9)
    private DZFDouble h16_ynsdseje;
    // 符合条件的小型微利企业减免所得税额累计
    @TaxExcelPos(row = 22, col = 9)
    private DZFDouble h17_fhtjxwqysrje;
    // 已预缴所得税额
    @TaxExcelPos(row = 23, col = 9)
    private DZFDouble h18_sjyjnsdseje;
    // 应补（退）所得税额累计
    @TaxExcelPos(row = 24, col = 9)
    private DZFDouble h19_bqybtsdse;
    // 民族自治地方的自治机关对本民族自治地方的企业应缴纳的企业所得税中属于地方分享的部分减征或免征
    @TaxExcelPos(row = 25, col = 9)
    private DZFDouble h20_mzzzdfjme;
    // 本期实际应补（退）所得税额
    @TaxExcelPos(row = 26, col = 9)
    private DZFDouble h21_sjybtsdse;

    // 是否属于小型微利企业
    @TaxExcelPos(row = 30, col = 5)
    private String sfxxwl_mc;
    @TaxExcelPos(row = 30, col = 5)
    private String sfxxwl;

    // 季初从业人数
    @TaxExcelPos(row = 28, col = 3)
    private DZFDouble jccyrs;
    // 季末从业人数
    @TaxExcelPos(row = 28, col = 8)
    private DZFDouble jmcyrs;
    // 季初资产总额（万元）
    @TaxExcelPos(row = 29, col = 3)
    private DZFDouble jczcze;
    // 季末资产总额（万元）
    @TaxExcelPos(row = 29, col = 8)
    private DZFDouble jmzcze;
    // 国家限制或禁止行业（Y是，N否）
    @TaxExcelPos(row = 30, col = 3)
    private String gjxzjzhy;
    // 国家限制或禁止行业（Y是，N否）
    @TaxExcelPos(row = 30, col = 3)
    private String gjxzjzhy_mc;

    // 减征免征类型名称
    private String jzmzlx_mc;
    // 减征免征类型
    private String jzmzlx;
    // 减征幅度
    private DZFDouble jzfd;

    public String getHdzsfs() {
        return hdzsfs;
    }

    public void setHdzsfs(String hdzsfs) {
        if ("核定应税所得率(能核算收入总额的)".equals(hdzsfs)) {
            hdzsfs = "403";
        } else if ("核定应税所得率(能核算成本费用总额的)".equals(hdzsfs)) {
            hdzsfs = "404";
        } else if ("核定应纳所得税额".equals(hdzsfs)) {
            hdzsfs = "402";
        }
        this.hdzsfs = hdzsfs;
    }

    public DZFDouble getH1_srzje() {
        return h1_srzje;
    }

    public void setH1_srzje(DZFDouble h1_srzje) {
        this.h1_srzje = h1_srzje;
    }

    public DZFDouble getH2_bzssrje() {
        return h2_bzssrje;
    }

    public void setH2_bzssrje(DZFDouble h2_bzssrje) {
        this.h2_bzssrje = h2_bzssrje;
    }

    public DZFDouble getH3_mssrje() {
        return h3_mssrje;
    }

    public void setH3_mssrje(DZFDouble h3_mssrje) {
        this.h3_mssrje = h3_mssrje;
    }

    public DZFDouble getH4_gzlxsrje() {
        return h4_gzlxsrje;
    }

    public void setH4_gzlxsrje(DZFDouble h4_gzlxsrje) {
        this.h4_gzlxsrje = h4_gzlxsrje;
    }

    public DZFDouble getH5_fhtjjmqysrje() {
        return h5_fhtjjmqysrje;
    }

    public void setH5_fhtjjmqysrje(DZFDouble h5_fhtjjmqysrje) {
        this.h5_fhtjjmqysrje = h5_fhtjjmqysrje;
    }

    public DZFDouble getH6_hgttzsrje() {
        return h6_hgttzsrje;
    }

    public void setH6_hgttzsrje(DZFDouble h6_hgttzsrje) {
        this.h6_hgttzsrje = h6_hgttzsrje;
    }

    public DZFDouble getH7_sgttzsrje() {
        return h7_sgttzsrje;
    }

    public void setH7_sgttzsrje(DZFDouble h7_sgttzsrje) {
        this.h7_sgttzsrje = h7_sgttzsrje;
    }

    public DZFDouble getH8_cxqygxhlmzqysds() {
        return h8_cxqygxhlmzqysds;
    }

    public void setH8_cxqygxhlmzqysds(DZFDouble h8_cxqygxhlmzqysds) {
        this.h8_cxqygxhlmzqysds = h8_cxqygxhlmzqysds;
    }

    public DZFDouble getH9_yxzlxsrmzqysds() {
        return h9_yxzlxsrmzqysds;
    }

    public void setH9_yxzlxsrmzqysds(DZFDouble h9_yxzlxsrmzqysds) {
        this.h9_yxzlxsrmzqysds = h9_yxzlxsrmzqysds;
    }

    public DZFDouble getH10_tzzqjjsrje() {
        return h10_tzzqjjsrje;
    }

    public void setH10_tzzqjjsrje(DZFDouble h10_tzzqjjsrje) {
        this.h10_tzzqjjsrje = h10_tzzqjjsrje;
    }

    public DZFDouble getH11_dfzfzqsrje() {
        return h11_dfzfzqsrje;
    }

    public void setH11_dfzfzqsrje(DZFDouble h11_dfzfzqsrje) {
        this.h11_dfzfzqsrje = h11_dfzfzqsrje;
    }

    public DZFDouble getH12_yssrcbsrje() {
        return h12_yssrcbsrje;
    }

    public void setH12_yssrcbsrje(DZFDouble h12_yssrcbsrje) {
        this.h12_yssrcbsrje = h12_yssrcbsrje;
    }

    public DZFDouble getH13_hdsdl() {
        return h13_hdsdl;
    }

    public void setH13_hdsdl(DZFDouble h13_hdsdl) {
        this.h13_hdsdl = h13_hdsdl;
    }

    public DZFDouble getH14_ynssdeje() {
        return h14_ynssdeje;
    }

    public void setH14_ynssdeje(DZFDouble h14_ynssdeje) {
        this.h14_ynssdeje = h14_ynssdeje;
    }

    public DZFDouble getH15_sl() {
        return h15_sl;
    }

    public void setH15_sl(DZFDouble h15_sl) {
        this.h15_sl = h15_sl;
    }

    public DZFDouble getH16_ynsdseje() {
        return h16_ynsdseje;
    }

    public void setH16_ynsdseje(DZFDouble h16_ynsdseje) {
        this.h16_ynsdseje = h16_ynsdseje;
    }

    public DZFDouble getH17_fhtjxwqysrje() {
        return h17_fhtjxwqysrje;
    }

    public void setH17_fhtjxwqysrje(DZFDouble h17_fhtjxwqysrje) {
        this.h17_fhtjxwqysrje = h17_fhtjxwqysrje;
    }

    public DZFDouble getH18_sjyjnsdseje() {
        return h18_sjyjnsdseje;
    }

    public void setH18_sjyjnsdseje(DZFDouble h18_sjyjnsdseje) {
        this.h18_sjyjnsdseje = h18_sjyjnsdseje;
    }

    public DZFDouble getH19_bqybtsdse() {
        return h19_bqybtsdse;
    }

    public void setH19_bqybtsdse(DZFDouble h19_bqybtsdse) {
        this.h19_bqybtsdse = h19_bqybtsdse;
    }

    public DZFDouble getH20_mzzzdfjme() {
        return h20_mzzzdfjme;
    }

    public void setH20_mzzzdfjme(DZFDouble h20_mzzzdfjme) {
        this.h20_mzzzdfjme = h20_mzzzdfjme;
    }

    public DZFDouble getH21_sjybtsdse() {
        return h21_sjybtsdse;
    }

    public void setH21_sjybtsdse(DZFDouble h21_sjybtsdse) {
        this.h21_sjybtsdse = h21_sjybtsdse;
    }

    public String getSfxxwl_mc() {
        return sfxxwl_mc;
    }

    public void setSfxxwl_mc(String sfxxwl_mc) {
        this.sfxxwl_mc = sfxxwl_mc;
    }

    public String getSfxxwl() {
        return sfxxwl;
    }

    public void setSfxxwl(String sfxxwl) {
        if ("是".equals(sfxxwl)) {
            sfxxwl = "Y";
        } else if ("否".equals(sfxxwl)) {
            sfxxwl = "N";
        }
        this.sfxxwl = sfxxwl;
    }

    public DZFDouble getJccyrs() {
        return jccyrs;
    }

    public void setJccyrs(DZFDouble jccyrs) {
        this.jccyrs = jccyrs;
    }

    public DZFDouble getJmcyrs() {
        return jmcyrs;
    }

    public void setJmcyrs(DZFDouble jmcyrs) {
        this.jmcyrs = jmcyrs;
    }

    public DZFDouble getJczcze() {
        return jczcze;
    }

    public void setJczcze(DZFDouble jczcze) {
        this.jczcze = jczcze;
    }

    public DZFDouble getJmzcze() {
        return jmzcze;
    }

    public void setJmzcze(DZFDouble jmzcze) {
        this.jmzcze = jmzcze;
    }

    public String getGjxzjzhy() {
        return gjxzjzhy;
    }

    public void setGjxzjzhy(String gjxzjzhy) {
        this.gjxzjzhy = castString(gjxzjzhy);
    }

    public String getGjxzjzhy_mc() {
        return gjxzjzhy_mc;
    }

    public void setGjxzjzhy_mc(String gjxzjzhy_mc) {
        this.gjxzjzhy_mc = gjxzjzhy_mc;
    }

    public String getJzmzlx_mc() {
        return jzmzlx_mc;
    }

    public void setJzmzlx_mc(String jzmzlx_mc) {
        this.jzmzlx_mc = jzmzlx_mc;
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

    private String castString(String str) {
        if ("是".equals(str)) {
            str = "Y";
        } else if ("否".equals(str)) {
            str = "N";
        }
        return str;
    }
}
