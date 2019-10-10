package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DzfpscReqBVO {
    private String pk_dzfpsc_b;

    private String pk_dzfpsc_h;

    //凭证主键
    private String pk_tzpz_h;

    private String pk_corp;

    //开票日期
    private DZFDate kprqb;

    //序列
    private int xulie;

    // 购货单位
    private String ghdwb;

    // 发票号码
    private String fphmb;

    //凭证字号
    private String pzzh;

    //摘要
    private String summary;

    // 名称
    @JsonProperty("DDMX_DDMC")
    private String itemname;

    // 单位
    @JsonProperty("DDMX_DW")
    private String unit;

    // 销售类型
    @JsonProperty("DDMX_XSLX")
    private String xslx;

    // 规格型号
    @JsonProperty("DDMX_GGXH")
    private String ggxh;

    // 含税标志
    @JsonProperty("DDMX_HSBZ")
    private String hsbz;

    // 项目数量
    @JsonProperty("DDMX_XMSL")
    private DZFDouble amount;

    // 税率
    @JsonProperty("DDMX_SL")
    private DZFDouble taxrate;

    //单价
    @JsonProperty("DDMX_DJ")
    private DZFDouble price;

    //金额
    @JsonProperty("DDMX_JE")
    private DZFDouble money;

    //税额
    @JsonProperty("DDMX_SE")
    private DZFDouble taxmny;

    //价税合计
    private DZFDouble jshj;

    private DZFBoolean isselect;
}
