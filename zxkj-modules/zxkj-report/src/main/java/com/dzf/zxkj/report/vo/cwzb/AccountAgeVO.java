package com.dzf.zxkj.report.vo.cwzb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountAgeVO {
    @JsonProperty("id")
    private String pk_age;
    //账龄类型
    @JsonProperty("type")
    private Integer age_type;
    //编号
    private String code;
    private String name;
    @JsonProperty("corp")
    private String pk_corp;
    //单位天数
    private Integer days;

    private Integer dr;
}
