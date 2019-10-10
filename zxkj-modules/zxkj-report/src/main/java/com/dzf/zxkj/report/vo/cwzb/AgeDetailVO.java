package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class AgeDetailVO {
    @JsonProperty("kmbm")
    private String account_code;
    @JsonProperty("kmmc")
    private String account_name;
    //未核销金额
    @JsonProperty("total")
    private DZFDouble total_mny;
    // 核销金额
    private DZFDouble verify_mny;
    // 核销凭证
    private String pk_verify_voucher;
    //凭证号
    @JsonProperty("vchnum")
    private String voucher_number;
    //凭证日期
    @JsonProperty("vchdate")
    private DZFDate vch_date;
    //天数
    private Integer days;
    //期间
    private String period;
    //辅助核算项
    private String fzhsx;
    private String pk_fzhsx;
    private String pk_voucher;

    @JsonProperty("periodsmny")
    private HashMap<String, DZFDouble> period_mny;

    private String fzhsx1;
    private String fzhsx2;
    private String fzhsx3;
    private String fzhsx4;
    private String fzhsx5;
    private String fzhsx6;
    private String fzhsx7;
    private String fzhsx8;
    private String fzhsx9;
    private String fzhsx10;
}
