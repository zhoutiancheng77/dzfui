package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class XjllMxvo {
    private String xm;
    private String xmcode;
    private String pzh;
    private DZFDate dopedate;
    private String kmcode;
    private String kmmc;
    private DZFDouble jffs;
    private DZFDouble ddfs;
    private String currname;
    private String pk_curreny;// 币种
    @JsonProperty("pzid")
    private String pk_tzpz_h;
    private String zy;
    private String code;
    private String name;

    private String gs;

    private String period;// 期间

    // 打印时 标题显示的区间区间
    private String titlePeriod;
}
