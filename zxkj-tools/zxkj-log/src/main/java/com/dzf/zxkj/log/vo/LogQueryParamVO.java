package com.dzf.zxkj.log.vo;

import com.dzf.zxkj.custom.type.DZFDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Data
public class LogQueryParamVO {
    @JsonProperty("begindate")
    private DZFDate begindate1;// 开始日期

    @JsonProperty("enddate")
    private DZFDate enddate;// 结束日期

    private String otpye;// 操作类型

    private String omsg;//操作信息

    private String opeuser;//操作用户

    @JsonProperty("cid")
    private String pk_corp;//公司信息

    @JsonProperty("ident")
    private Integer sys_ident;//系统类型0:集团,1:管理端,2会计端,3、加盟商系统
}
