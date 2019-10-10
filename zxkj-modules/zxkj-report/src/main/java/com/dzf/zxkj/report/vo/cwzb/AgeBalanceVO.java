package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class AgeBalanceVO {
    @JsonProperty("kmbm")
    private String account_code;
    @JsonProperty("kmmc")
    private String account_name;
    //未核销金额
    @JsonProperty("total")
    private DZFDouble total_mny;
    @JsonProperty("periodsmny")
    private Map<String, DZFDouble> period_mny;

    private List<AgeDetailVO> details;

    private String fzhsx;
    private String pk_fzhsx;

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
