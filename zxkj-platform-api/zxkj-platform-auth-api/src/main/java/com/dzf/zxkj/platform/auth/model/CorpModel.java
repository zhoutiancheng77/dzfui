package com.dzf.zxkj.platform.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("bd_corp")
public class CorpModel implements Serializable {
    private static final long serialVersionUID = -2549348901898203434L;
    @TableField("begindate")
    private DZFDate begindate;// 建账日期
    @TableField("corptype")
    public String corptype;// 科目方案ID
    @TableField("icbegindate")
    public DZFDate icbegindate;// 库存启用日期
    @TableField("createdate")
    public DZFDate createdate;// 录入日期
    @TableField("ishasaccount")
    public DZFBoolean ishasaccount;// 是否已建帐
    @TableField("isseal")
    public DZFBoolean isseal;// 是否已停用
    @TableField("isworkingunit")
    public DZFBoolean isworkingunit;//是否成本结转
    @TableField("pk_corp")
    public String pk_corp;//主键
    @TableField("unitname")
    public String unitname;// 公司名称
    @TableField("unitshortname")
    public String unitshortname;//公司名称简称

    public String dr;
}
