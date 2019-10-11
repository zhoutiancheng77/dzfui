package com.dzf.zxkj.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzf.zxkj.common.lang.DZFDateTime;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ynt_zcfz_set")
public class ZcfzRptSetVo implements Serializable {
    private static final long serialVersionUID = 5181150169973670904L;
    @TableId("pk_zcfz_set")
    private String pk_zcfz_set;// 主键
    @TableField("zcname")
    private String zcname;// 资产名称
    @TableField("fzname")
    private String fzname;// 负债名称
    @TableField("zchc")
    private String zchc;// 资产行次
    @TableField("fzhc")
    private String fzhc;// 负债行次
    @TableField("zckm")
    private String zckm;// 资产科目
    @TableField("zckm_re")
    private String zckm_re;//重分类科目
    @TableField("fzkm")
    private String fzkm;// 负债科目
    @TableField("fzkm_re")
    private String fzkm_re;//负债重分类科目
    @TableField("pk_trade_accountschema")
    private String pk_trade_accountschema;// 行业
    @TableField("ordernum")
    private Integer ordernum;//序号
    @TableField("pk_corp")
    private String pk_corp;// 公司
    @TableField("dr")
    private Integer dr;//
    @TableField("ts")
    private DZFDateTime ts;
    @TableField("zchc_id")
    private String zchc_id;//不可修改
    @TableField("fzhc_id")
    private String fzhc_id;//不可修改
}
