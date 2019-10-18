package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sm_user")
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 4283983832733084149L;
    @TableField("user_code")
    @JsonProperty("user_code")
    private String username;
    @TableField("cuserid")
    private String userid;
    @JsonProperty("user_password")
    @TableField("user_password")
    private String password;// 密码
    @TableField(exist = false)
    private String token;
    private String dr;
}