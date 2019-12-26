package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@TableName("sm_funnode")
public class FunNode {
    @TableField("module")
    private String module;
    @TableField("fun_code")
    private String code;
    @TableField("router")
    private String router;
    @TableId
    private String pk_funnode;
    @TableField("fun_name")
    private String name;
    @TableField("nodeurl")
    private String nodeurl;
    @JsonIgnore
    private String dr;
    @TableField("pk_parent")
    private String pk_parent;
    @TableField("file_dir")
    private String component;
    @TableField("show_order")
    private Integer orderNum;
}
