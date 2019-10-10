package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

public class CwgyInfoVO {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // 项目
    private String xm;
    // 项目分类
    private String xmfl;

    // 行数
    private String hs;

    // 本月金额
    private DZFDouble byje;
    // 本月金额
    private String sbyje;
    // 本月同比
    private DZFDouble bybl;
    private String sbybl;

    // 本月环比
    private DZFDouble byhb;
    private String sbyhb;

    // 本年累计金额 (发生的累计，不包含期初)
    private DZFDouble bnljje;
    private String sbnljje;
    // 本年累计同比
    private DZFDouble bnljbl;
    private String sbnljbl;

    // 上年同期数
    private DZFDouble sntqs;

    // 去年累计金额
    private DZFDouble qnljje;

    private Integer level;

    // 本年累计(包含期初的本年累计)
    private DZFDouble bnlj;

    // 打印时 标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    private String period;// 期间

    private String pk_corp;

    private DZFBoolean isseven;

    private Integer rowspan;
    private Integer row;

    private Integer colspan;
    private Integer col;

    //上年同期数，和上面的重复，避免冲突用心的字段
    private DZFDouble byje_pre;
    private DZFDouble bnljje_pre;
}
