package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzf.zxkj.common.lang.DZFDateTime;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("YNT_LOGIN_LOG")
public class LoginLogVo{
    @TableId(type = IdType.ASSIGN_ID)
    private String pk_login;
    private String memo;
    private String loginip;
    private String pk_corp;
    private String pk_user;
    private String loginsession;
    private String project_name = "dzf_kj";
    private Timestamp logindate;
    private Timestamp logoutdate;
    private Integer dr;
    //	1:用户正常退出，2：被其它人强制退出，3：session失效
//	IGlobalConstants.logoutType
    private Integer logouttype;
    private Integer loginstatus;
    private String sys_version = "1.0.0";
    private DZFDateTime ts;

}
