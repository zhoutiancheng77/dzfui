package com.dzf.zxkj.jbsz.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.jbsz.handler.mybatis.DZFDateTimeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@Data
@TableName("ynt_bankaccount")
public class BankAccountVO implements Serializable {
    private static final long serialVersionUID = 2579496229640721886L;

    @JsonProperty("id")
    @TableId(value = "pk_bankaccount")
    private String  pk_bankaccount;//主键

    @JsonProperty("cid")
    private String  coperatorid;//操作人

    @JsonProperty("ddate")
    private DZFDate doperatedate;//操作时间

    @JsonProperty("corpid")
    private String  pk_corp;

    @JsonProperty("serialnum")
    private int serialnum;//序号

    @JsonProperty("bkcode")
    private String  bankcode;//编码

    @JsonProperty("bkname")
    private String  bankname;//银行账户名称

    @JsonProperty("bkaccout")
    private String  bankaccount;//银行账号

    @JsonProperty("accname_id")
    private String  relatedsubj;//关联会计科目

    @JsonProperty("accode")
    @TableField(exist = false)
    private String  accountcode;

    @TableField(exist = false)
    @JsonProperty("accname")
    private String  accountname;

    @JsonProperty("bkstatus")
    private int state;//银行账户状态

    @JsonProperty("modifyoperid")
    private String modifyoperid;//修改人

    @JsonProperty("modifydatetime")
    @TableField(jdbcType = JdbcType.CHAR)
    private DZFDateTime modifydatetime;//修改时间

    @JsonProperty("dr")
    @TableLogic
    private int dr;

    @JsonProperty("ts")
    @TableField(jdbcType = JdbcType.TIMESTAMP)
    private DZFDateTime ts;
}
