package com.dzf.zxkj.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzf.zxkj.common.lang.DZFDateTime;
import lombok.Data;

import java.io.Serializable;

@TableName("ynt_lrb_set")
@Data
public class LrbRptSetVo implements Serializable {
	@TableId("pk_lrb_set")
	private String	pk_lrb_set;//主键
	@TableField("xm")
	private String	xm;//项目名称
	@TableField("hc")
	private String	hc;//行次
	@TableField("hc_id")
	private String hc_id;//不可编辑
	@TableField("km")
	private String	km;//科目取数
	@TableField("xm2")
	private String	xm2;//项目名称
	@TableField("hc2")
	private String	hc2;//行次
	@TableField("km2")
	private String	km2;//科目取数
	@TableField("pk_corp")
	private String	pk_corp;//公司
	@TableField("ordernum")
	private Integer	ordernum;//序号
	@TableField("pk_trade_accountschema")
	private String pk_trade_accountschema;// 行业
	@TableField("ilevel")
	private Integer ilevel;//层级
	@TableField("dr")
	private Integer dr;//
	@TableField("ts")
	private DZFDateTime ts;
}
