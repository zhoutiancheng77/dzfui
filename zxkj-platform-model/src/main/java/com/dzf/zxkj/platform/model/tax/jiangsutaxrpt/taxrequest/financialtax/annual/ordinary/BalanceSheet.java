package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 资产负债表
@TaxExcelPos(reportID = "39806001", reportname = "资产负债表")
public class BalanceSheet {
    // 货币资金-期末余额资产
    @TaxExcelPos(row = 5, col = 2)
    private DZFDouble zcfzb_h1_qmye;
    // 货币资金-年初余额资产
    @TaxExcelPos(row = 5, col = 3)
    private DZFDouble zcfzb_h1_ncye;

    // 短期借款-期末余额资产
    @TaxExcelPos(row = 5, col = 6)
    private DZFDouble zcfzb_h32_qmye;
    // 短期借款-年初余额资产
    @TaxExcelPos(row = 5, col = 7)
    private DZFDouble zcfzb_h32_ncye;

    // 以公允价值计量且其变动计入当期损益的金融资产-期末余额资产
    @TaxExcelPos(row = 6, col = 2)
    private DZFDouble zcfzb_h2_qmye;
    // 以公允价值计量且其变动计入当期损益的金融资产-年初余额资产
    @TaxExcelPos(row = 6, col = 3)
    private DZFDouble zcfzb_h2_ncye;

    // 以公允价值计量且其变动计入当期损益的金融负债-期末余额资产
    @TaxExcelPos(row = 6, col = 6)
    private DZFDouble zcfzb_h33_qmye;
    // 以公允价值计量且其变动计入当期损益的金融负债-年初余额资产
    @TaxExcelPos(row = 6, col = 7)
    private DZFDouble zcfzb_h33_ncye;

    // 衍生金融资产-期末余额资产
    @TaxExcelPos(row = 7, col = 2)
    private DZFDouble zcfzb_h63_qmye;
    // 衍生金融资产-年初余额资产
    @TaxExcelPos(row = 7, col = 3)
    private DZFDouble zcfzb_h63_ncye;

    // 衍生金融负债-期末余额资产
    @TaxExcelPos(row = 7, col = 6)
    private DZFDouble zcfzb_h64_qmye;
    // 衍生金融负债-年初余额资产
    @TaxExcelPos(row = 7, col = 7)
    private DZFDouble zcfzb_h64_ncye;

    // 应收票据-期末余额资产
    @TaxExcelPos(row = 8, col = 2)
    private DZFDouble zcfzb_h3_qmye;
    // 应收票据-年初余额资产
    @TaxExcelPos(row = 8, col = 3)
    private DZFDouble zcfzb_h3_ncye;

    // 应付票据-期末余额资产
    @TaxExcelPos(row = 8, col = 6)
    private DZFDouble zcfzb_h34_qmye;
    // 应付票据-年初余额资产
    @TaxExcelPos(row = 8, col = 7)
    private DZFDouble zcfzb_h34_ncye;

    // 应收账款-期末余额资产
    @TaxExcelPos(row = 9, col = 2)
    private DZFDouble zcfzb_h4_qmye;
    // 应收账款-年初余额资产
    @TaxExcelPos(row = 9, col = 3)
    private DZFDouble zcfzb_h4_ncye;

    // 应付账款-期末余额资产
    @TaxExcelPos(row = 9, col = 6)
    private DZFDouble zcfzb_h35_qmye;
    // 应付账款-年初余额资产
    @TaxExcelPos(row = 9, col = 7)
    private DZFDouble zcfzb_h35_ncye;

    // 预付款项-期末余额资产
    @TaxExcelPos(row = 10, col = 2)
    private DZFDouble zcfzb_h5_qmye;
    // 预付款项-年初余额资产
    @TaxExcelPos(row = 10, col = 3)
    private DZFDouble zcfzb_h5_ncye;

    // 预收款项-期末余额资产
    @TaxExcelPos(row = 10, col = 6)
    private DZFDouble zcfzb_h36_qmye;
    // 预收款项-年初余额资产
    @TaxExcelPos(row = 10, col = 7)
    private DZFDouble zcfzb_h36_ncye;

    // 应收利息-期末余额资产
    @TaxExcelPos(row = 11, col = 2)
    private DZFDouble zcfzb_h6_qmye;
    // 应收利息-年初余额资产
    @TaxExcelPos(row = 11, col = 3)
    private DZFDouble zcfzb_h6_ncye;

    // 应付职工薪酬-期末余额资产
    @TaxExcelPos(row = 11, col = 6)
    private DZFDouble zcfzb_h37_qmye;
    // 应付职工薪酬-年初余额资产
    @TaxExcelPos(row = 11, col = 7)
    private DZFDouble zcfzb_h37_ncye;

    // 应收股利-期末余额资产
    @TaxExcelPos(row = 12, col = 2)
    private DZFDouble zcfzb_h7_qmye;
    // 应收股利-年初余额资产
    @TaxExcelPos(row = 12, col = 3)
    private DZFDouble zcfzb_h7_ncye;

    // 应交税费-期末余额资产
    @TaxExcelPos(row = 12, col = 6)
    private DZFDouble zcfzb_h38_qmye;
    // 应交税费-年初余额资产
    @TaxExcelPos(row = 12, col = 7)
    private DZFDouble zcfzb_h38_ncye;

    // 其他应收款-期末余额资产
    @TaxExcelPos(row = 13, col = 2)
    private DZFDouble zcfzb_h8_qmye;
    // 其他应收款-年初余额资产
    @TaxExcelPos(row = 13, col = 3)
    private DZFDouble zcfzb_h8_ncye;

    // 应付利息-期末余额资产
    @TaxExcelPos(row = 13, col = 6)
    private DZFDouble zcfzb_h39_qmye;
    // 应付利息-年初余额资产
    @TaxExcelPos(row = 13, col = 7)
    private DZFDouble zcfzb_h39_ncye;

    // 存货-期末余额资产
    @TaxExcelPos(row = 14, col = 2)
    private DZFDouble zcfzb_h9_qmye;
    // 存货-年初余额资产
    @TaxExcelPos(row = 14, col = 3)
    private DZFDouble zcfzb_h9_ncye;

    // 应付股利-期末余额资产
    @TaxExcelPos(row = 14, col = 6)
    private DZFDouble zcfzb_h40_qmye;
    // 应付股利-年初余额资产
    @TaxExcelPos(row = 14, col = 7)
    private DZFDouble zcfzb_h40_ncye;

    // 持有待售资产-期末余额资产
    @TaxExcelPos(row = 15, col = 2)
    private DZFDouble zcfzb_h65_qmye;
    // 持有待售资产-年初余额资产
    @TaxExcelPos(row = 15, col = 3)
    private DZFDouble zcfzb_h65_ncye;

    // 持有待售负债-期末余额资产
    @TaxExcelPos(row = 15, col = 6)
    private DZFDouble zcfzb_h66_qmye;
    // 持有待售负债-年初余额资产
    @TaxExcelPos(row = 15, col = 7)
    private DZFDouble zcfzb_h66_ncye;

    // 一年内到期的非流动资产-期末余额资产
    @TaxExcelPos(row = 16, col = 2)
    private DZFDouble zcfzb_h10_qmye;
    // 一年内到期的非流动资产-年初余额资产
    @TaxExcelPos(row = 16, col = 3)
    private DZFDouble zcfzb_h10_ncye;

    // 其他应付款-期末余额资产
    @TaxExcelPos(row = 16, col = 6)
    private DZFDouble zcfzb_h41_qmye;
    // 其他应付款-年初余额资产
    @TaxExcelPos(row = 16, col = 7)
    private DZFDouble zcfzb_h41_ncye;

    // 其他流动资产-期末余额资产
    @TaxExcelPos(row = 17, col = 2)
    private DZFDouble zcfzb_h11_qmye;
    // 其他流动资产-年初余额资产
    @TaxExcelPos(row = 17, col = 3)
    private DZFDouble zcfzb_h11_ncye;

    // 一年内到期的非流动负债-期末余额资产
    @TaxExcelPos(row = 17, col = 6)
    private DZFDouble zcfzb_h42_qmye;
    // 一年内到期的非流动负债-年初余额资产
    @TaxExcelPos(row = 17, col = 7)
    private DZFDouble zcfzb_h42_ncye;

    // 流动资产合计-期末余额资产
    @TaxExcelPos(row = 18, col = 2)
    private DZFDouble zcfzb_h12_qmye;
    // 流动资产合计-年初余额资产
    @TaxExcelPos(row = 18, col = 3)
    private DZFDouble zcfzb_h12_ncye;

    // 其他流动负债-期末余额资产
    @TaxExcelPos(row = 18, col = 6)
    private DZFDouble zcfzb_h43_qmye;
    // 其他流动负债-年初余额资产
    @TaxExcelPos(row = 18, col = 7)
    private DZFDouble zcfzb_h43_ncye;

    // 流动负债合计-期末余额资产
    @TaxExcelPos(row = 19, col = 6)
    private DZFDouble zcfzb_h44_qmye;
    // 流动负债合计-年初余额资产
    @TaxExcelPos(row = 19, col = 7)
    private DZFDouble zcfzb_h44_ncye;

    // 可供出售金融资产-期末余额资产
    @TaxExcelPos(row = 20, col = 2)
    private DZFDouble zcfzb_h13_qmye;
    // 可供出售金融资产-年初余额资产
    @TaxExcelPos(row = 20, col = 3)
    private DZFDouble zcfzb_h13_ncye;


    // 持有至到期投资-期末余额资产
    @TaxExcelPos(row = 21, col = 2)
    private DZFDouble zcfzb_h14_qmye;
    // 持有至到期投资-年初余额资产
    @TaxExcelPos(row = 21, col = 3)
    private DZFDouble zcfzb_h14_ncye;

    // 长期借款-期末余额资产
    @TaxExcelPos(row = 21, col = 6)
    private DZFDouble zcfzb_h45_qmye;
    // 长期借款-年初余额资产
    @TaxExcelPos(row = 21, col = 7)
    private DZFDouble zcfzb_h45_ncye;

    // 长期应收款-期末余额资产
    @TaxExcelPos(row = 22, col = 2)
    private DZFDouble zcfzb_h15_qmye;
    // 长期应收款-年初余额资产
    @TaxExcelPos(row = 22, col = 3)
    private DZFDouble zcfzb_h15_ncye;

    // 应付债券-期末余额资产
    @TaxExcelPos(row = 22, col = 6)
    private DZFDouble zcfzb_h46_qmye;
    // 应付债券-年初余额资产
    @TaxExcelPos(row = 22, col = 7)
    private DZFDouble zcfzb_h46_ncye;

    // 长期股权投资-期末余额资产
    @TaxExcelPos(row = 23, col = 2)
    private DZFDouble zcfzb_h16_qmye;
    // 长期股权投资-年初余额资产
    @TaxExcelPos(row = 23, col = 3)
    private DZFDouble zcfzb_h16_ncye;

    // 优先股-期末余额资产
    @TaxExcelPos(row = 23, col = 6)
    private DZFDouble zcfzb_h67_qmye;
    // 优先股-年初余额资产
    @TaxExcelPos(row = 23, col = 7)
    private DZFDouble zcfzb_h67_ncye;

    // 投资性房地产-期末余额资产
    @TaxExcelPos(row = 24, col = 2)
    private DZFDouble zcfzb_h17_qmye;
    // 投资性房地产-年初余额资产
    @TaxExcelPos(row = 24, col = 3)
    private DZFDouble zcfzb_h17_ncye;

    // 永续债-期末余额资产
    @TaxExcelPos(row = 24, col = 6)
    private DZFDouble zcfzb_h68_qmye;
    // 永续债-年初余额资产
    @TaxExcelPos(row = 24, col = 7)
    private DZFDouble zcfzb_h68_ncye;

    // 固定资产-期末余额资产
    @TaxExcelPos(row = 25, col = 2)
    private DZFDouble zcfzb_h18_qmye;
    // 固定资产-年初余额资产
    @TaxExcelPos(row = 25, col = 3)
    private DZFDouble zcfzb_h18_ncye;

    // 长期应付款-期末余额资产
    @TaxExcelPos(row = 25, col = 6)
    private DZFDouble zcfzb_h47_qmye;
    // 长期应付款-年初余额资产
    @TaxExcelPos(row = 25, col = 7)
    private DZFDouble zcfzb_h47_ncye;

    // 在建工程-期末余额资产
    @TaxExcelPos(row = 26, col = 2)
    private DZFDouble zcfzb_h19_qmye;
    // 在建工程-年初余额资产
    @TaxExcelPos(row = 26, col = 3)
    private DZFDouble zcfzb_h19_ncye;

    // 专项应付款-期末余额资产
    @TaxExcelPos(row = 26, col = 6)
    private DZFDouble zcfzb_h48_qmye;
    // 专项应付款-年初余额资产
    @TaxExcelPos(row = 26, col = 7)
    private DZFDouble zcfzb_h48_ncye;

    // 工程物资-期末余额资产
    @TaxExcelPos(row = 27, col = 2)
    private DZFDouble zcfzb_h20_qmye;
    // 工程物资-年初余额资产
    @TaxExcelPos(row = 27, col = 3)
    private DZFDouble zcfzb_h20_ncye;

    // 预计负债-期末余额资产
    @TaxExcelPos(row = 27, col = 6)
    private DZFDouble zcfzb_h49_qmye;
    // 预计负债-年初余额资产
    @TaxExcelPos(row = 27, col = 7)
    private DZFDouble zcfzb_h49_ncye;

    // 固定资产清理-期末余额资产
    @TaxExcelPos(row = 28, col = 2)
    private DZFDouble zcfzb_h21_qmye;
    // 固定资产清理-年初余额资产
    @TaxExcelPos(row = 28, col = 3)
    private DZFDouble zcfzb_h21_ncye;

    // 递延收益-期末余额资产
    @TaxExcelPos(row = 28, col = 6)
    private DZFDouble zcfzb_h50_qmye;
    // 递延收益-年初余额资产
    @TaxExcelPos(row = 28, col = 7)
    private DZFDouble zcfzb_h50_ncye;

    // 生产性生物资产-期末余额资产
    @TaxExcelPos(row = 29, col = 2)
    private DZFDouble zcfzb_h22_qmye;
    // 生产性生物资产-年初余额资产
    @TaxExcelPos(row = 29, col = 3)
    private DZFDouble zcfzb_h22_ncye;

    // 递延所得税负债-期末余额资产
    @TaxExcelPos(row = 29, col = 6)
    private DZFDouble zcfzb_h51_qmye;
    // 递延所得税负债-年初余额资产
    @TaxExcelPos(row = 29, col = 7)
    private DZFDouble zcfzb_h51_ncye;

    // 油气资产-期末余额资产
    @TaxExcelPos(row = 30, col = 2)
    private DZFDouble zcfzb_h23_qmye;
    // 油气资产-年初余额资产
    @TaxExcelPos(row = 30, col = 3)
    private DZFDouble zcfzb_h23_ncye;

    // 其他非流动负债-期末余额资产
    @TaxExcelPos(row = 30, col = 6)
    private DZFDouble zcfzb_h52_qmye;
    // 其他非流动负债-年初余额资产
    @TaxExcelPos(row = 30, col = 7)
    private DZFDouble zcfzb_h52_ncye;

    // 无形资产-期末余额资产
    @TaxExcelPos(row = 31, col = 2)
    private DZFDouble zcfzb_h24_qmye;
    // 无形资产-年初余额资产
    @TaxExcelPos(row = 31, col = 3)
    private DZFDouble zcfzb_h24_ncye;

    // 非流动负债合计-期末余额资产
    @TaxExcelPos(row = 31, col = 6)
    private DZFDouble zcfzb_h53_qmye;
    // 非流动负债合计-年初余额资产
    @TaxExcelPos(row = 31, col = 7)
    private DZFDouble zcfzb_h53_ncye;

    // 开发支出-期末余额资产
    @TaxExcelPos(row = 32, col = 2)
    private DZFDouble zcfzb_h25_qmye;
    // 开发支出-年初余额资产
    @TaxExcelPos(row = 32, col = 3)
    private DZFDouble zcfzb_h25_ncye;

    // 负债合计-期末余额资产
    @TaxExcelPos(row = 32, col = 6)
    private DZFDouble zcfzb_h54_qmye;
    // 负债合计-年初余额资产
    @TaxExcelPos(row = 32, col = 7)
    private DZFDouble zcfzb_h54_ncye;

    // 商誉-期末余额资产
    @TaxExcelPos(row = 33, col = 2)
    private DZFDouble zcfzb_h26_qmye;
    // 商誉-年初余额资产
    @TaxExcelPos(row = 33, col = 3)
    private DZFDouble zcfzb_h26_ncye;

    // 长期待摊费用-期末余额资产
    @TaxExcelPos(row = 34, col = 2)
    private DZFDouble zcfzb_h27_qmye;
    // 长期待摊费用-年初余额资产
    @TaxExcelPos(row = 34, col = 3)
    private DZFDouble zcfzb_h27_ncye;

    // 实收资本（或股本）-期末余额资产
    @TaxExcelPos(row = 34, col = 6)
    private DZFDouble zcfzb_h55_qmye;
    // 实收资本（或股本）-年初余额资产
    @TaxExcelPos(row = 34, col = 7)
    private DZFDouble zcfzb_h55_ncye;

    // 递延所得税资产-期末余额资产
    @TaxExcelPos(row = 35, col = 2)
    private DZFDouble zcfzb_h28_qmye;
    // 递延所得税资产-年初余额资产
    @TaxExcelPos(row = 35, col = 3)
    private DZFDouble zcfzb_h28_ncye;

    // 其他权益工具-期末余额资产
    @TaxExcelPos(row = 35, col = 6)
    private DZFDouble zcfzb_h69_qmye;
    // 其他权益工具-年初余额资产
    @TaxExcelPos(row = 35, col = 7)
    private DZFDouble zcfzb_h69_ncye;

    // 其他非流动资产-期末余额资产
    @TaxExcelPos(row = 36, col = 2)
    private DZFDouble zcfzb_h29_qmye;
    // 其他非流动资产-年初余额资产
    @TaxExcelPos(row = 36, col = 3)
    private DZFDouble zcfzb_h29_ncye;

    // 优先股-期末余额资产
    @TaxExcelPos(row = 36, col = 6)
    private DZFDouble zcfzb_h71_qmye;
    // 优先股-年初余额资产
    @TaxExcelPos(row = 36, col = 7)
    private DZFDouble zcfzb_h71_ncye;

    // 非流动资产合计-期末余额资产
    @TaxExcelPos(row = 37, col = 2)
    private DZFDouble zcfzb_h30_qmye;
    // 非流动资产合计-年初余额资产
    @TaxExcelPos(row = 37, col = 3)
    private DZFDouble zcfzb_h30_ncye;

    // 永续债-期末余额资产
    @TaxExcelPos(row = 37, col = 6)
    private DZFDouble zcfzb_h72_qmye;
    // 永续债-年初余额资产
    @TaxExcelPos(row = 37, col = 7)
    private DZFDouble zcfzb_h72_ncye;

    // 资本公积-期末余额资产
    @TaxExcelPos(row = 38, col = 6)
    private DZFDouble zcfzb_h56_qmye;
    // 资本公积-年初余额资产
    @TaxExcelPos(row = 38, col = 7)
    private DZFDouble zcfzb_h56_ncye;

    // 减：库存股-期末余额资产
    @TaxExcelPos(row = 39, col = 6)
    private DZFDouble zcfzb_h57_qmye;
    // 减：库存股-年初余额资产
    @TaxExcelPos(row = 39, col = 7)
    private DZFDouble zcfzb_h57_ncye;

    // 其他综合收益-期末余额资产
    @TaxExcelPos(row = 40, col = 6)
    private DZFDouble zcfzb_h58_qmye;
    // 其他综合收益-年初余额资产
    @TaxExcelPos(row = 40, col = 7)
    private DZFDouble zcfzb_h58_ncye;

    // 盈余公积-期末余额资产
    @TaxExcelPos(row = 41, col = 6)
    private DZFDouble zcfzb_h59_qmye;
    // 盈余公积-年初余额资产
    @TaxExcelPos(row = 41, col = 7)
    private DZFDouble zcfzb_h59_ncye;

    // 未分配利润-期末余额资产
    @TaxExcelPos(row = 42, col = 6)
    private DZFDouble zcfzb_h60_qmye;
    // 未分配利润-年初余额资产
    @TaxExcelPos(row = 42, col = 7)
    private DZFDouble zcfzb_h60_ncye;

    // 所有者权益（或股东权益）合计-期末余额资产
    @TaxExcelPos(row = 43, col = 6)
    private DZFDouble zcfzb_h61_qmye;
    // 所有者权益（或股东权益）合计-年初余额资产
    @TaxExcelPos(row = 43, col = 7)
    private DZFDouble zcfzb_h61_ncye;

    // 资产总计-期末余额资产
    @TaxExcelPos(row = 44, col = 2)
    private DZFDouble zcfzb_h31_qmye;
    // 资产总计-年初余额资产
    @TaxExcelPos(row = 44, col = 3)
    private DZFDouble zcfzb_h31_ncye;

    // 负债和所有者权益（或股东权益）总计-期末余额资产
    @TaxExcelPos(row = 44, col = 6)
    private DZFDouble zcfzb_h62_qmye;
    // 负债和所有者权益（或股东权益）总计-年初余额资产
    @TaxExcelPos(row = 44, col = 7)
    private DZFDouble zcfzb_h62_ncye;

    public DZFDouble getZcfzb_h1_qmye() {
        return zcfzb_h1_qmye;
    }

    public void setZcfzb_h1_qmye(DZFDouble zcfzb_h1_qmye) {
        this.zcfzb_h1_qmye = zcfzb_h1_qmye;
    }

    public DZFDouble getZcfzb_h1_ncye() {
        return zcfzb_h1_ncye;
    }

    public void setZcfzb_h1_ncye(DZFDouble zcfzb_h1_ncye) {
        this.zcfzb_h1_ncye = zcfzb_h1_ncye;
    }

    public DZFDouble getZcfzb_h32_qmye() {
        return zcfzb_h32_qmye;
    }

    public void setZcfzb_h32_qmye(DZFDouble zcfzb_h32_qmye) {
        this.zcfzb_h32_qmye = zcfzb_h32_qmye;
    }

    public DZFDouble getZcfzb_h32_ncye() {
        return zcfzb_h32_ncye;
    }

    public void setZcfzb_h32_ncye(DZFDouble zcfzb_h32_ncye) {
        this.zcfzb_h32_ncye = zcfzb_h32_ncye;
    }

    public DZFDouble getZcfzb_h2_qmye() {
        return zcfzb_h2_qmye;
    }

    public void setZcfzb_h2_qmye(DZFDouble zcfzb_h2_qmye) {
        this.zcfzb_h2_qmye = zcfzb_h2_qmye;
    }

    public DZFDouble getZcfzb_h2_ncye() {
        return zcfzb_h2_ncye;
    }

    public void setZcfzb_h2_ncye(DZFDouble zcfzb_h2_ncye) {
        this.zcfzb_h2_ncye = zcfzb_h2_ncye;
    }

    public DZFDouble getZcfzb_h33_qmye() {
        return zcfzb_h33_qmye;
    }

    public void setZcfzb_h33_qmye(DZFDouble zcfzb_h33_qmye) {
        this.zcfzb_h33_qmye = zcfzb_h33_qmye;
    }

    public DZFDouble getZcfzb_h33_ncye() {
        return zcfzb_h33_ncye;
    }

    public void setZcfzb_h33_ncye(DZFDouble zcfzb_h33_ncye) {
        this.zcfzb_h33_ncye = zcfzb_h33_ncye;
    }

    public DZFDouble getZcfzb_h63_qmye() {
        return zcfzb_h63_qmye;
    }

    public void setZcfzb_h63_qmye(DZFDouble zcfzb_h63_qmye) {
        this.zcfzb_h63_qmye = zcfzb_h63_qmye;
    }

    public DZFDouble getZcfzb_h63_ncye() {
        return zcfzb_h63_ncye;
    }

    public void setZcfzb_h63_ncye(DZFDouble zcfzb_h63_ncye) {
        this.zcfzb_h63_ncye = zcfzb_h63_ncye;
    }

    public DZFDouble getZcfzb_h64_qmye() {
        return zcfzb_h64_qmye;
    }

    public void setZcfzb_h64_qmye(DZFDouble zcfzb_h64_qmye) {
        this.zcfzb_h64_qmye = zcfzb_h64_qmye;
    }

    public DZFDouble getZcfzb_h64_ncye() {
        return zcfzb_h64_ncye;
    }

    public void setZcfzb_h64_ncye(DZFDouble zcfzb_h64_ncye) {
        this.zcfzb_h64_ncye = zcfzb_h64_ncye;
    }

    public DZFDouble getZcfzb_h3_qmye() {
        return zcfzb_h3_qmye;
    }

    public void setZcfzb_h3_qmye(DZFDouble zcfzb_h3_qmye) {
        this.zcfzb_h3_qmye = zcfzb_h3_qmye;
    }

    public DZFDouble getZcfzb_h3_ncye() {
        return zcfzb_h3_ncye;
    }

    public void setZcfzb_h3_ncye(DZFDouble zcfzb_h3_ncye) {
        this.zcfzb_h3_ncye = zcfzb_h3_ncye;
    }

    public DZFDouble getZcfzb_h34_qmye() {
        return zcfzb_h34_qmye;
    }

    public void setZcfzb_h34_qmye(DZFDouble zcfzb_h34_qmye) {
        this.zcfzb_h34_qmye = zcfzb_h34_qmye;
    }

    public DZFDouble getZcfzb_h34_ncye() {
        return zcfzb_h34_ncye;
    }

    public void setZcfzb_h34_ncye(DZFDouble zcfzb_h34_ncye) {
        this.zcfzb_h34_ncye = zcfzb_h34_ncye;
    }

    public DZFDouble getZcfzb_h4_qmye() {
        return zcfzb_h4_qmye;
    }

    public void setZcfzb_h4_qmye(DZFDouble zcfzb_h4_qmye) {
        this.zcfzb_h4_qmye = zcfzb_h4_qmye;
    }

    public DZFDouble getZcfzb_h4_ncye() {
        return zcfzb_h4_ncye;
    }

    public void setZcfzb_h4_ncye(DZFDouble zcfzb_h4_ncye) {
        this.zcfzb_h4_ncye = zcfzb_h4_ncye;
    }

    public DZFDouble getZcfzb_h35_qmye() {
        return zcfzb_h35_qmye;
    }

    public void setZcfzb_h35_qmye(DZFDouble zcfzb_h35_qmye) {
        this.zcfzb_h35_qmye = zcfzb_h35_qmye;
    }

    public DZFDouble getZcfzb_h35_ncye() {
        return zcfzb_h35_ncye;
    }

    public void setZcfzb_h35_ncye(DZFDouble zcfzb_h35_ncye) {
        this.zcfzb_h35_ncye = zcfzb_h35_ncye;
    }

    public DZFDouble getZcfzb_h5_qmye() {
        return zcfzb_h5_qmye;
    }

    public void setZcfzb_h5_qmye(DZFDouble zcfzb_h5_qmye) {
        this.zcfzb_h5_qmye = zcfzb_h5_qmye;
    }

    public DZFDouble getZcfzb_h5_ncye() {
        return zcfzb_h5_ncye;
    }

    public void setZcfzb_h5_ncye(DZFDouble zcfzb_h5_ncye) {
        this.zcfzb_h5_ncye = zcfzb_h5_ncye;
    }

    public DZFDouble getZcfzb_h36_qmye() {
        return zcfzb_h36_qmye;
    }

    public void setZcfzb_h36_qmye(DZFDouble zcfzb_h36_qmye) {
        this.zcfzb_h36_qmye = zcfzb_h36_qmye;
    }

    public DZFDouble getZcfzb_h36_ncye() {
        return zcfzb_h36_ncye;
    }

    public void setZcfzb_h36_ncye(DZFDouble zcfzb_h36_ncye) {
        this.zcfzb_h36_ncye = zcfzb_h36_ncye;
    }

    public DZFDouble getZcfzb_h6_qmye() {
        return zcfzb_h6_qmye;
    }

    public void setZcfzb_h6_qmye(DZFDouble zcfzb_h6_qmye) {
        this.zcfzb_h6_qmye = zcfzb_h6_qmye;
    }

    public DZFDouble getZcfzb_h6_ncye() {
        return zcfzb_h6_ncye;
    }

    public void setZcfzb_h6_ncye(DZFDouble zcfzb_h6_ncye) {
        this.zcfzb_h6_ncye = zcfzb_h6_ncye;
    }

    public DZFDouble getZcfzb_h37_qmye() {
        return zcfzb_h37_qmye;
    }

    public void setZcfzb_h37_qmye(DZFDouble zcfzb_h37_qmye) {
        this.zcfzb_h37_qmye = zcfzb_h37_qmye;
    }

    public DZFDouble getZcfzb_h37_ncye() {
        return zcfzb_h37_ncye;
    }

    public void setZcfzb_h37_ncye(DZFDouble zcfzb_h37_ncye) {
        this.zcfzb_h37_ncye = zcfzb_h37_ncye;
    }

    public DZFDouble getZcfzb_h7_qmye() {
        return zcfzb_h7_qmye;
    }

    public void setZcfzb_h7_qmye(DZFDouble zcfzb_h7_qmye) {
        this.zcfzb_h7_qmye = zcfzb_h7_qmye;
    }

    public DZFDouble getZcfzb_h7_ncye() {
        return zcfzb_h7_ncye;
    }

    public void setZcfzb_h7_ncye(DZFDouble zcfzb_h7_ncye) {
        this.zcfzb_h7_ncye = zcfzb_h7_ncye;
    }

    public DZFDouble getZcfzb_h38_qmye() {
        return zcfzb_h38_qmye;
    }

    public void setZcfzb_h38_qmye(DZFDouble zcfzb_h38_qmye) {
        this.zcfzb_h38_qmye = zcfzb_h38_qmye;
    }

    public DZFDouble getZcfzb_h38_ncye() {
        return zcfzb_h38_ncye;
    }

    public void setZcfzb_h38_ncye(DZFDouble zcfzb_h38_ncye) {
        this.zcfzb_h38_ncye = zcfzb_h38_ncye;
    }

    public DZFDouble getZcfzb_h8_qmye() {
        return zcfzb_h8_qmye;
    }

    public void setZcfzb_h8_qmye(DZFDouble zcfzb_h8_qmye) {
        this.zcfzb_h8_qmye = zcfzb_h8_qmye;
    }

    public DZFDouble getZcfzb_h8_ncye() {
        return zcfzb_h8_ncye;
    }

    public void setZcfzb_h8_ncye(DZFDouble zcfzb_h8_ncye) {
        this.zcfzb_h8_ncye = zcfzb_h8_ncye;
    }

    public DZFDouble getZcfzb_h39_qmye() {
        return zcfzb_h39_qmye;
    }

    public void setZcfzb_h39_qmye(DZFDouble zcfzb_h39_qmye) {
        this.zcfzb_h39_qmye = zcfzb_h39_qmye;
    }

    public DZFDouble getZcfzb_h39_ncye() {
        return zcfzb_h39_ncye;
    }

    public void setZcfzb_h39_ncye(DZFDouble zcfzb_h39_ncye) {
        this.zcfzb_h39_ncye = zcfzb_h39_ncye;
    }

    public DZFDouble getZcfzb_h9_qmye() {
        return zcfzb_h9_qmye;
    }

    public void setZcfzb_h9_qmye(DZFDouble zcfzb_h9_qmye) {
        this.zcfzb_h9_qmye = zcfzb_h9_qmye;
    }

    public DZFDouble getZcfzb_h9_ncye() {
        return zcfzb_h9_ncye;
    }

    public void setZcfzb_h9_ncye(DZFDouble zcfzb_h9_ncye) {
        this.zcfzb_h9_ncye = zcfzb_h9_ncye;
    }

    public DZFDouble getZcfzb_h40_qmye() {
        return zcfzb_h40_qmye;
    }

    public void setZcfzb_h40_qmye(DZFDouble zcfzb_h40_qmye) {
        this.zcfzb_h40_qmye = zcfzb_h40_qmye;
    }

    public DZFDouble getZcfzb_h40_ncye() {
        return zcfzb_h40_ncye;
    }

    public void setZcfzb_h40_ncye(DZFDouble zcfzb_h40_ncye) {
        this.zcfzb_h40_ncye = zcfzb_h40_ncye;
    }

    public DZFDouble getZcfzb_h65_qmye() {
        return zcfzb_h65_qmye;
    }

    public void setZcfzb_h65_qmye(DZFDouble zcfzb_h65_qmye) {
        this.zcfzb_h65_qmye = zcfzb_h65_qmye;
    }

    public DZFDouble getZcfzb_h65_ncye() {
        return zcfzb_h65_ncye;
    }

    public void setZcfzb_h65_ncye(DZFDouble zcfzb_h65_ncye) {
        this.zcfzb_h65_ncye = zcfzb_h65_ncye;
    }

    public DZFDouble getZcfzb_h66_qmye() {
        return zcfzb_h66_qmye;
    }

    public void setZcfzb_h66_qmye(DZFDouble zcfzb_h66_qmye) {
        this.zcfzb_h66_qmye = zcfzb_h66_qmye;
    }

    public DZFDouble getZcfzb_h66_ncye() {
        return zcfzb_h66_ncye;
    }

    public void setZcfzb_h66_ncye(DZFDouble zcfzb_h66_ncye) {
        this.zcfzb_h66_ncye = zcfzb_h66_ncye;
    }

    public DZFDouble getZcfzb_h10_qmye() {
        return zcfzb_h10_qmye;
    }

    public void setZcfzb_h10_qmye(DZFDouble zcfzb_h10_qmye) {
        this.zcfzb_h10_qmye = zcfzb_h10_qmye;
    }

    public DZFDouble getZcfzb_h10_ncye() {
        return zcfzb_h10_ncye;
    }

    public void setZcfzb_h10_ncye(DZFDouble zcfzb_h10_ncye) {
        this.zcfzb_h10_ncye = zcfzb_h10_ncye;
    }

    public DZFDouble getZcfzb_h41_qmye() {
        return zcfzb_h41_qmye;
    }

    public void setZcfzb_h41_qmye(DZFDouble zcfzb_h41_qmye) {
        this.zcfzb_h41_qmye = zcfzb_h41_qmye;
    }

    public DZFDouble getZcfzb_h41_ncye() {
        return zcfzb_h41_ncye;
    }

    public void setZcfzb_h41_ncye(DZFDouble zcfzb_h41_ncye) {
        this.zcfzb_h41_ncye = zcfzb_h41_ncye;
    }

    public DZFDouble getZcfzb_h11_qmye() {
        return zcfzb_h11_qmye;
    }

    public void setZcfzb_h11_qmye(DZFDouble zcfzb_h11_qmye) {
        this.zcfzb_h11_qmye = zcfzb_h11_qmye;
    }

    public DZFDouble getZcfzb_h11_ncye() {
        return zcfzb_h11_ncye;
    }

    public void setZcfzb_h11_ncye(DZFDouble zcfzb_h11_ncye) {
        this.zcfzb_h11_ncye = zcfzb_h11_ncye;
    }

    public DZFDouble getZcfzb_h42_qmye() {
        return zcfzb_h42_qmye;
    }

    public void setZcfzb_h42_qmye(DZFDouble zcfzb_h42_qmye) {
        this.zcfzb_h42_qmye = zcfzb_h42_qmye;
    }

    public DZFDouble getZcfzb_h42_ncye() {
        return zcfzb_h42_ncye;
    }

    public void setZcfzb_h42_ncye(DZFDouble zcfzb_h42_ncye) {
        this.zcfzb_h42_ncye = zcfzb_h42_ncye;
    }

    public DZFDouble getZcfzb_h12_qmye() {
        return zcfzb_h12_qmye;
    }

    public void setZcfzb_h12_qmye(DZFDouble zcfzb_h12_qmye) {
        this.zcfzb_h12_qmye = zcfzb_h12_qmye;
    }

    public DZFDouble getZcfzb_h12_ncye() {
        return zcfzb_h12_ncye;
    }

    public void setZcfzb_h12_ncye(DZFDouble zcfzb_h12_ncye) {
        this.zcfzb_h12_ncye = zcfzb_h12_ncye;
    }

    public DZFDouble getZcfzb_h43_qmye() {
        return zcfzb_h43_qmye;
    }

    public void setZcfzb_h43_qmye(DZFDouble zcfzb_h43_qmye) {
        this.zcfzb_h43_qmye = zcfzb_h43_qmye;
    }

    public DZFDouble getZcfzb_h43_ncye() {
        return zcfzb_h43_ncye;
    }

    public void setZcfzb_h43_ncye(DZFDouble zcfzb_h43_ncye) {
        this.zcfzb_h43_ncye = zcfzb_h43_ncye;
    }

    public DZFDouble getZcfzb_h44_qmye() {
        return zcfzb_h44_qmye;
    }

    public void setZcfzb_h44_qmye(DZFDouble zcfzb_h44_qmye) {
        this.zcfzb_h44_qmye = zcfzb_h44_qmye;
    }

    public DZFDouble getZcfzb_h44_ncye() {
        return zcfzb_h44_ncye;
    }

    public void setZcfzb_h44_ncye(DZFDouble zcfzb_h44_ncye) {
        this.zcfzb_h44_ncye = zcfzb_h44_ncye;
    }

    public DZFDouble getZcfzb_h13_qmye() {
        return zcfzb_h13_qmye;
    }

    public void setZcfzb_h13_qmye(DZFDouble zcfzb_h13_qmye) {
        this.zcfzb_h13_qmye = zcfzb_h13_qmye;
    }

    public DZFDouble getZcfzb_h13_ncye() {
        return zcfzb_h13_ncye;
    }

    public void setZcfzb_h13_ncye(DZFDouble zcfzb_h13_ncye) {
        this.zcfzb_h13_ncye = zcfzb_h13_ncye;
    }

    public DZFDouble getZcfzb_h14_qmye() {
        return zcfzb_h14_qmye;
    }

    public void setZcfzb_h14_qmye(DZFDouble zcfzb_h14_qmye) {
        this.zcfzb_h14_qmye = zcfzb_h14_qmye;
    }

    public DZFDouble getZcfzb_h14_ncye() {
        return zcfzb_h14_ncye;
    }

    public void setZcfzb_h14_ncye(DZFDouble zcfzb_h14_ncye) {
        this.zcfzb_h14_ncye = zcfzb_h14_ncye;
    }

    public DZFDouble getZcfzb_h45_qmye() {
        return zcfzb_h45_qmye;
    }

    public void setZcfzb_h45_qmye(DZFDouble zcfzb_h45_qmye) {
        this.zcfzb_h45_qmye = zcfzb_h45_qmye;
    }

    public DZFDouble getZcfzb_h45_ncye() {
        return zcfzb_h45_ncye;
    }

    public void setZcfzb_h45_ncye(DZFDouble zcfzb_h45_ncye) {
        this.zcfzb_h45_ncye = zcfzb_h45_ncye;
    }

    public DZFDouble getZcfzb_h15_qmye() {
        return zcfzb_h15_qmye;
    }

    public void setZcfzb_h15_qmye(DZFDouble zcfzb_h15_qmye) {
        this.zcfzb_h15_qmye = zcfzb_h15_qmye;
    }

    public DZFDouble getZcfzb_h15_ncye() {
        return zcfzb_h15_ncye;
    }

    public void setZcfzb_h15_ncye(DZFDouble zcfzb_h15_ncye) {
        this.zcfzb_h15_ncye = zcfzb_h15_ncye;
    }

    public DZFDouble getZcfzb_h46_qmye() {
        return zcfzb_h46_qmye;
    }

    public void setZcfzb_h46_qmye(DZFDouble zcfzb_h46_qmye) {
        this.zcfzb_h46_qmye = zcfzb_h46_qmye;
    }

    public DZFDouble getZcfzb_h46_ncye() {
        return zcfzb_h46_ncye;
    }

    public void setZcfzb_h46_ncye(DZFDouble zcfzb_h46_ncye) {
        this.zcfzb_h46_ncye = zcfzb_h46_ncye;
    }

    public DZFDouble getZcfzb_h16_qmye() {
        return zcfzb_h16_qmye;
    }

    public void setZcfzb_h16_qmye(DZFDouble zcfzb_h16_qmye) {
        this.zcfzb_h16_qmye = zcfzb_h16_qmye;
    }

    public DZFDouble getZcfzb_h16_ncye() {
        return zcfzb_h16_ncye;
    }

    public void setZcfzb_h16_ncye(DZFDouble zcfzb_h16_ncye) {
        this.zcfzb_h16_ncye = zcfzb_h16_ncye;
    }

    public DZFDouble getZcfzb_h67_qmye() {
        return zcfzb_h67_qmye;
    }

    public void setZcfzb_h67_qmye(DZFDouble zcfzb_h67_qmye) {
        this.zcfzb_h67_qmye = zcfzb_h67_qmye;
    }

    public DZFDouble getZcfzb_h67_ncye() {
        return zcfzb_h67_ncye;
    }

    public void setZcfzb_h67_ncye(DZFDouble zcfzb_h67_ncye) {
        this.zcfzb_h67_ncye = zcfzb_h67_ncye;
    }

    public DZFDouble getZcfzb_h17_qmye() {
        return zcfzb_h17_qmye;
    }

    public void setZcfzb_h17_qmye(DZFDouble zcfzb_h17_qmye) {
        this.zcfzb_h17_qmye = zcfzb_h17_qmye;
    }

    public DZFDouble getZcfzb_h17_ncye() {
        return zcfzb_h17_ncye;
    }

    public void setZcfzb_h17_ncye(DZFDouble zcfzb_h17_ncye) {
        this.zcfzb_h17_ncye = zcfzb_h17_ncye;
    }

    public DZFDouble getZcfzb_h68_qmye() {
        return zcfzb_h68_qmye;
    }

    public void setZcfzb_h68_qmye(DZFDouble zcfzb_h68_qmye) {
        this.zcfzb_h68_qmye = zcfzb_h68_qmye;
    }

    public DZFDouble getZcfzb_h68_ncye() {
        return zcfzb_h68_ncye;
    }

    public void setZcfzb_h68_ncye(DZFDouble zcfzb_h68_ncye) {
        this.zcfzb_h68_ncye = zcfzb_h68_ncye;
    }

    public DZFDouble getZcfzb_h18_qmye() {
        return zcfzb_h18_qmye;
    }

    public void setZcfzb_h18_qmye(DZFDouble zcfzb_h18_qmye) {
        this.zcfzb_h18_qmye = zcfzb_h18_qmye;
    }

    public DZFDouble getZcfzb_h18_ncye() {
        return zcfzb_h18_ncye;
    }

    public void setZcfzb_h18_ncye(DZFDouble zcfzb_h18_ncye) {
        this.zcfzb_h18_ncye = zcfzb_h18_ncye;
    }

    public DZFDouble getZcfzb_h47_qmye() {
        return zcfzb_h47_qmye;
    }

    public void setZcfzb_h47_qmye(DZFDouble zcfzb_h47_qmye) {
        this.zcfzb_h47_qmye = zcfzb_h47_qmye;
    }

    public DZFDouble getZcfzb_h47_ncye() {
        return zcfzb_h47_ncye;
    }

    public void setZcfzb_h47_ncye(DZFDouble zcfzb_h47_ncye) {
        this.zcfzb_h47_ncye = zcfzb_h47_ncye;
    }

    public DZFDouble getZcfzb_h19_qmye() {
        return zcfzb_h19_qmye;
    }

    public void setZcfzb_h19_qmye(DZFDouble zcfzb_h19_qmye) {
        this.zcfzb_h19_qmye = zcfzb_h19_qmye;
    }

    public DZFDouble getZcfzb_h19_ncye() {
        return zcfzb_h19_ncye;
    }

    public void setZcfzb_h19_ncye(DZFDouble zcfzb_h19_ncye) {
        this.zcfzb_h19_ncye = zcfzb_h19_ncye;
    }

    public DZFDouble getZcfzb_h48_qmye() {
        return zcfzb_h48_qmye;
    }

    public void setZcfzb_h48_qmye(DZFDouble zcfzb_h48_qmye) {
        this.zcfzb_h48_qmye = zcfzb_h48_qmye;
    }

    public DZFDouble getZcfzb_h48_ncye() {
        return zcfzb_h48_ncye;
    }

    public void setZcfzb_h48_ncye(DZFDouble zcfzb_h48_ncye) {
        this.zcfzb_h48_ncye = zcfzb_h48_ncye;
    }

    public DZFDouble getZcfzb_h20_qmye() {
        return zcfzb_h20_qmye;
    }

    public void setZcfzb_h20_qmye(DZFDouble zcfzb_h20_qmye) {
        this.zcfzb_h20_qmye = zcfzb_h20_qmye;
    }

    public DZFDouble getZcfzb_h20_ncye() {
        return zcfzb_h20_ncye;
    }

    public void setZcfzb_h20_ncye(DZFDouble zcfzb_h20_ncye) {
        this.zcfzb_h20_ncye = zcfzb_h20_ncye;
    }

    public DZFDouble getZcfzb_h49_qmye() {
        return zcfzb_h49_qmye;
    }

    public void setZcfzb_h49_qmye(DZFDouble zcfzb_h49_qmye) {
        this.zcfzb_h49_qmye = zcfzb_h49_qmye;
    }

    public DZFDouble getZcfzb_h49_ncye() {
        return zcfzb_h49_ncye;
    }

    public void setZcfzb_h49_ncye(DZFDouble zcfzb_h49_ncye) {
        this.zcfzb_h49_ncye = zcfzb_h49_ncye;
    }

    public DZFDouble getZcfzb_h21_qmye() {
        return zcfzb_h21_qmye;
    }

    public void setZcfzb_h21_qmye(DZFDouble zcfzb_h21_qmye) {
        this.zcfzb_h21_qmye = zcfzb_h21_qmye;
    }

    public DZFDouble getZcfzb_h21_ncye() {
        return zcfzb_h21_ncye;
    }

    public void setZcfzb_h21_ncye(DZFDouble zcfzb_h21_ncye) {
        this.zcfzb_h21_ncye = zcfzb_h21_ncye;
    }

    public DZFDouble getZcfzb_h50_qmye() {
        return zcfzb_h50_qmye;
    }

    public void setZcfzb_h50_qmye(DZFDouble zcfzb_h50_qmye) {
        this.zcfzb_h50_qmye = zcfzb_h50_qmye;
    }

    public DZFDouble getZcfzb_h50_ncye() {
        return zcfzb_h50_ncye;
    }

    public void setZcfzb_h50_ncye(DZFDouble zcfzb_h50_ncye) {
        this.zcfzb_h50_ncye = zcfzb_h50_ncye;
    }

    public DZFDouble getZcfzb_h22_qmye() {
        return zcfzb_h22_qmye;
    }

    public void setZcfzb_h22_qmye(DZFDouble zcfzb_h22_qmye) {
        this.zcfzb_h22_qmye = zcfzb_h22_qmye;
    }

    public DZFDouble getZcfzb_h22_ncye() {
        return zcfzb_h22_ncye;
    }

    public void setZcfzb_h22_ncye(DZFDouble zcfzb_h22_ncye) {
        this.zcfzb_h22_ncye = zcfzb_h22_ncye;
    }

    public DZFDouble getZcfzb_h51_qmye() {
        return zcfzb_h51_qmye;
    }

    public void setZcfzb_h51_qmye(DZFDouble zcfzb_h51_qmye) {
        this.zcfzb_h51_qmye = zcfzb_h51_qmye;
    }

    public DZFDouble getZcfzb_h51_ncye() {
        return zcfzb_h51_ncye;
    }

    public void setZcfzb_h51_ncye(DZFDouble zcfzb_h51_ncye) {
        this.zcfzb_h51_ncye = zcfzb_h51_ncye;
    }

    public DZFDouble getZcfzb_h23_qmye() {
        return zcfzb_h23_qmye;
    }

    public void setZcfzb_h23_qmye(DZFDouble zcfzb_h23_qmye) {
        this.zcfzb_h23_qmye = zcfzb_h23_qmye;
    }

    public DZFDouble getZcfzb_h23_ncye() {
        return zcfzb_h23_ncye;
    }

    public void setZcfzb_h23_ncye(DZFDouble zcfzb_h23_ncye) {
        this.zcfzb_h23_ncye = zcfzb_h23_ncye;
    }

    public DZFDouble getZcfzb_h52_qmye() {
        return zcfzb_h52_qmye;
    }

    public void setZcfzb_h52_qmye(DZFDouble zcfzb_h52_qmye) {
        this.zcfzb_h52_qmye = zcfzb_h52_qmye;
    }

    public DZFDouble getZcfzb_h52_ncye() {
        return zcfzb_h52_ncye;
    }

    public void setZcfzb_h52_ncye(DZFDouble zcfzb_h52_ncye) {
        this.zcfzb_h52_ncye = zcfzb_h52_ncye;
    }

    public DZFDouble getZcfzb_h24_qmye() {
        return zcfzb_h24_qmye;
    }

    public void setZcfzb_h24_qmye(DZFDouble zcfzb_h24_qmye) {
        this.zcfzb_h24_qmye = zcfzb_h24_qmye;
    }

    public DZFDouble getZcfzb_h24_ncye() {
        return zcfzb_h24_ncye;
    }

    public void setZcfzb_h24_ncye(DZFDouble zcfzb_h24_ncye) {
        this.zcfzb_h24_ncye = zcfzb_h24_ncye;
    }

    public DZFDouble getZcfzb_h53_qmye() {
        return zcfzb_h53_qmye;
    }

    public void setZcfzb_h53_qmye(DZFDouble zcfzb_h53_qmye) {
        this.zcfzb_h53_qmye = zcfzb_h53_qmye;
    }

    public DZFDouble getZcfzb_h53_ncye() {
        return zcfzb_h53_ncye;
    }

    public void setZcfzb_h53_ncye(DZFDouble zcfzb_h53_ncye) {
        this.zcfzb_h53_ncye = zcfzb_h53_ncye;
    }

    public DZFDouble getZcfzb_h25_qmye() {
        return zcfzb_h25_qmye;
    }

    public void setZcfzb_h25_qmye(DZFDouble zcfzb_h25_qmye) {
        this.zcfzb_h25_qmye = zcfzb_h25_qmye;
    }

    public DZFDouble getZcfzb_h25_ncye() {
        return zcfzb_h25_ncye;
    }

    public void setZcfzb_h25_ncye(DZFDouble zcfzb_h25_ncye) {
        this.zcfzb_h25_ncye = zcfzb_h25_ncye;
    }

    public DZFDouble getZcfzb_h54_qmye() {
        return zcfzb_h54_qmye;
    }

    public void setZcfzb_h54_qmye(DZFDouble zcfzb_h54_qmye) {
        this.zcfzb_h54_qmye = zcfzb_h54_qmye;
    }

    public DZFDouble getZcfzb_h54_ncye() {
        return zcfzb_h54_ncye;
    }

    public void setZcfzb_h54_ncye(DZFDouble zcfzb_h54_ncye) {
        this.zcfzb_h54_ncye = zcfzb_h54_ncye;
    }

    public DZFDouble getZcfzb_h26_qmye() {
        return zcfzb_h26_qmye;
    }

    public void setZcfzb_h26_qmye(DZFDouble zcfzb_h26_qmye) {
        this.zcfzb_h26_qmye = zcfzb_h26_qmye;
    }

    public DZFDouble getZcfzb_h26_ncye() {
        return zcfzb_h26_ncye;
    }

    public void setZcfzb_h26_ncye(DZFDouble zcfzb_h26_ncye) {
        this.zcfzb_h26_ncye = zcfzb_h26_ncye;
    }

    public DZFDouble getZcfzb_h27_qmye() {
        return zcfzb_h27_qmye;
    }

    public void setZcfzb_h27_qmye(DZFDouble zcfzb_h27_qmye) {
        this.zcfzb_h27_qmye = zcfzb_h27_qmye;
    }

    public DZFDouble getZcfzb_h27_ncye() {
        return zcfzb_h27_ncye;
    }

    public void setZcfzb_h27_ncye(DZFDouble zcfzb_h27_ncye) {
        this.zcfzb_h27_ncye = zcfzb_h27_ncye;
    }

    public DZFDouble getZcfzb_h55_qmye() {
        return zcfzb_h55_qmye;
    }

    public void setZcfzb_h55_qmye(DZFDouble zcfzb_h55_qmye) {
        this.zcfzb_h55_qmye = zcfzb_h55_qmye;
    }

    public DZFDouble getZcfzb_h55_ncye() {
        return zcfzb_h55_ncye;
    }

    public void setZcfzb_h55_ncye(DZFDouble zcfzb_h55_ncye) {
        this.zcfzb_h55_ncye = zcfzb_h55_ncye;
    }

    public DZFDouble getZcfzb_h28_qmye() {
        return zcfzb_h28_qmye;
    }

    public void setZcfzb_h28_qmye(DZFDouble zcfzb_h28_qmye) {
        this.zcfzb_h28_qmye = zcfzb_h28_qmye;
    }

    public DZFDouble getZcfzb_h28_ncye() {
        return zcfzb_h28_ncye;
    }

    public void setZcfzb_h28_ncye(DZFDouble zcfzb_h28_ncye) {
        this.zcfzb_h28_ncye = zcfzb_h28_ncye;
    }

    public DZFDouble getZcfzb_h69_qmye() {
        return zcfzb_h69_qmye;
    }

    public void setZcfzb_h69_qmye(DZFDouble zcfzb_h69_qmye) {
        this.zcfzb_h69_qmye = zcfzb_h69_qmye;
    }

    public DZFDouble getZcfzb_h69_ncye() {
        return zcfzb_h69_ncye;
    }

    public void setZcfzb_h69_ncye(DZFDouble zcfzb_h69_ncye) {
        this.zcfzb_h69_ncye = zcfzb_h69_ncye;
    }

    public DZFDouble getZcfzb_h29_qmye() {
        return zcfzb_h29_qmye;
    }

    public void setZcfzb_h29_qmye(DZFDouble zcfzb_h29_qmye) {
        this.zcfzb_h29_qmye = zcfzb_h29_qmye;
    }

    public DZFDouble getZcfzb_h29_ncye() {
        return zcfzb_h29_ncye;
    }

    public void setZcfzb_h29_ncye(DZFDouble zcfzb_h29_ncye) {
        this.zcfzb_h29_ncye = zcfzb_h29_ncye;
    }

    public DZFDouble getZcfzb_h71_qmye() {
        return zcfzb_h71_qmye;
    }

    public void setZcfzb_h71_qmye(DZFDouble zcfzb_h71_qmye) {
        this.zcfzb_h71_qmye = zcfzb_h71_qmye;
    }

    public DZFDouble getZcfzb_h71_ncye() {
        return zcfzb_h71_ncye;
    }

    public void setZcfzb_h71_ncye(DZFDouble zcfzb_h71_ncye) {
        this.zcfzb_h71_ncye = zcfzb_h71_ncye;
    }

    public DZFDouble getZcfzb_h30_qmye() {
        return zcfzb_h30_qmye;
    }

    public void setZcfzb_h30_qmye(DZFDouble zcfzb_h30_qmye) {
        this.zcfzb_h30_qmye = zcfzb_h30_qmye;
    }

    public DZFDouble getZcfzb_h30_ncye() {
        return zcfzb_h30_ncye;
    }

    public void setZcfzb_h30_ncye(DZFDouble zcfzb_h30_ncye) {
        this.zcfzb_h30_ncye = zcfzb_h30_ncye;
    }

    public DZFDouble getZcfzb_h72_qmye() {
        return zcfzb_h72_qmye;
    }

    public void setZcfzb_h72_qmye(DZFDouble zcfzb_h72_qmye) {
        this.zcfzb_h72_qmye = zcfzb_h72_qmye;
    }

    public DZFDouble getZcfzb_h72_ncye() {
        return zcfzb_h72_ncye;
    }

    public void setZcfzb_h72_ncye(DZFDouble zcfzb_h72_ncye) {
        this.zcfzb_h72_ncye = zcfzb_h72_ncye;
    }

    public DZFDouble getZcfzb_h56_qmye() {
        return zcfzb_h56_qmye;
    }

    public void setZcfzb_h56_qmye(DZFDouble zcfzb_h56_qmye) {
        this.zcfzb_h56_qmye = zcfzb_h56_qmye;
    }

    public DZFDouble getZcfzb_h56_ncye() {
        return zcfzb_h56_ncye;
    }

    public void setZcfzb_h56_ncye(DZFDouble zcfzb_h56_ncye) {
        this.zcfzb_h56_ncye = zcfzb_h56_ncye;
    }

    public DZFDouble getZcfzb_h57_qmye() {
        return zcfzb_h57_qmye;
    }

    public void setZcfzb_h57_qmye(DZFDouble zcfzb_h57_qmye) {
        this.zcfzb_h57_qmye = zcfzb_h57_qmye;
    }

    public DZFDouble getZcfzb_h57_ncye() {
        return zcfzb_h57_ncye;
    }

    public void setZcfzb_h57_ncye(DZFDouble zcfzb_h57_ncye) {
        this.zcfzb_h57_ncye = zcfzb_h57_ncye;
    }

    public DZFDouble getZcfzb_h58_qmye() {
        return zcfzb_h58_qmye;
    }

    public void setZcfzb_h58_qmye(DZFDouble zcfzb_h58_qmye) {
        this.zcfzb_h58_qmye = zcfzb_h58_qmye;
    }

    public DZFDouble getZcfzb_h58_ncye() {
        return zcfzb_h58_ncye;
    }

    public void setZcfzb_h58_ncye(DZFDouble zcfzb_h58_ncye) {
        this.zcfzb_h58_ncye = zcfzb_h58_ncye;
    }

    public DZFDouble getZcfzb_h59_qmye() {
        return zcfzb_h59_qmye;
    }

    public void setZcfzb_h59_qmye(DZFDouble zcfzb_h59_qmye) {
        this.zcfzb_h59_qmye = zcfzb_h59_qmye;
    }

    public DZFDouble getZcfzb_h59_ncye() {
        return zcfzb_h59_ncye;
    }

    public void setZcfzb_h59_ncye(DZFDouble zcfzb_h59_ncye) {
        this.zcfzb_h59_ncye = zcfzb_h59_ncye;
    }

    public DZFDouble getZcfzb_h60_qmye() {
        return zcfzb_h60_qmye;
    }

    public void setZcfzb_h60_qmye(DZFDouble zcfzb_h60_qmye) {
        this.zcfzb_h60_qmye = zcfzb_h60_qmye;
    }

    public DZFDouble getZcfzb_h60_ncye() {
        return zcfzb_h60_ncye;
    }

    public void setZcfzb_h60_ncye(DZFDouble zcfzb_h60_ncye) {
        this.zcfzb_h60_ncye = zcfzb_h60_ncye;
    }

    public DZFDouble getZcfzb_h61_qmye() {
        return zcfzb_h61_qmye;
    }

    public void setZcfzb_h61_qmye(DZFDouble zcfzb_h61_qmye) {
        this.zcfzb_h61_qmye = zcfzb_h61_qmye;
    }

    public DZFDouble getZcfzb_h61_ncye() {
        return zcfzb_h61_ncye;
    }

    public void setZcfzb_h61_ncye(DZFDouble zcfzb_h61_ncye) {
        this.zcfzb_h61_ncye = zcfzb_h61_ncye;
    }

    public DZFDouble getZcfzb_h31_qmye() {
        return zcfzb_h31_qmye;
    }

    public void setZcfzb_h31_qmye(DZFDouble zcfzb_h31_qmye) {
        this.zcfzb_h31_qmye = zcfzb_h31_qmye;
    }

    public DZFDouble getZcfzb_h31_ncye() {
        return zcfzb_h31_ncye;
    }

    public void setZcfzb_h31_ncye(DZFDouble zcfzb_h31_ncye) {
        this.zcfzb_h31_ncye = zcfzb_h31_ncye;
    }

    public DZFDouble getZcfzb_h62_qmye() {
        return zcfzb_h62_qmye;
    }

    public void setZcfzb_h62_qmye(DZFDouble zcfzb_h62_qmye) {
        this.zcfzb_h62_qmye = zcfzb_h62_qmye;
    }

    public DZFDouble getZcfzb_h62_ncye() {
        return zcfzb_h62_ncye;
    }

    public void setZcfzb_h62_ncye(DZFDouble zcfzb_h62_ncye) {
        this.zcfzb_h62_ncye = zcfzb_h62_ncye;
    }
}
