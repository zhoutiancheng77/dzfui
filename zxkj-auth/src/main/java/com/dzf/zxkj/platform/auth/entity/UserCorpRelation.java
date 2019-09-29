package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sm_user_corp")
public class UserCorpRelation implements Serializable {
    @TableId("pk_user_corp")
    private String pk_user_corp;
    @TableField("cuserid")
    private String userid;
    @TableField("pk_corp")
    private String pk_corp;// 代账机构ID
    @TableField("pk_corpk")
    private String pk_corpk;// 客户ID
    @TableField("dr")
    private Integer dr; // 删除标记
}
