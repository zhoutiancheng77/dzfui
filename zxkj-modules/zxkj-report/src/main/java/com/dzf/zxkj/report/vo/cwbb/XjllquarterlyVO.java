package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFDouble;

public class XjllquarterlyVO {
    private String xm;
    private String hc;
    private String hc_id;//(不允许修改)
    private DZFDouble bnlj;// 本年累计
    private DZFDouble bf_bnlj;// 上年本年累计数
    private DZFDouble jd1;// 第一季度
    private DZFDouble jd2;// 第二季度
    private DZFDouble jd3;// 第三季度
    private DZFDouble jd4;// 第四季度

    //去年同期季度数据
    private DZFDouble jd1_last;
    private DZFDouble jd2_last;
    private DZFDouble jd3_last;
    private DZFDouble jd4_last;//

    // 打印时 标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    private String period;// 期间

    private String pk_corp;
}
