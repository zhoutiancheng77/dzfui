package com.dzf.admin.model.app.transfer.filetrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetAppFiletransHVO implements Serializable {

    @JsonProperty("ista")
    private Integer istatus;//消息状态： 1、待确认；2、已借出；3、已收到；

    @JsonProperty("istnm")
    private String istaname;//消息状态状态名称

    @JsonProperty("vsuernm")
    private String vsuername;//交出人名称

    @JsonProperty("vcaernm")
    private String vcaername;//接手人名称

    @JsonProperty("dttime")
    private String dtranstime;//时间

    @JsonProperty("sdate")
    private String vshowdate;//显示日期

    @JsonProperty("btnm")
    private String vbtnname;//按钮名称

    @JsonProperty("id")
    private String pk_zj;//主键

    private List<RetAppFiletransBVO> files;

}
