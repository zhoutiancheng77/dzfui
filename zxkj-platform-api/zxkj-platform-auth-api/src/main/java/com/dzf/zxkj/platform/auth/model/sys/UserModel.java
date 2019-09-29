package com.dzf.zxkj.platform.auth.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sm_user")
public class UserModel implements Serializable {
    private static final long serialVersionUID = -1555834926697784989L;
    @TableField("user_code")
    private String user_code;
    @TableField("user_name")
    private String user_name;// 用户名称
    @TableField("cuserid")
    private String cuserid;

    private String dr;
}
