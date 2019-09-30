package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sm_funnode")
public class FunNode {
    @TableField("module")
    private String module;
    @TableId
    private String pk_funnode;
    @TableField("nodeurl")
    private String nodeurl;
    private String dr;
}
