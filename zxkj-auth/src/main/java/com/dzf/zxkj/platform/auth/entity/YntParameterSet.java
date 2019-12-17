package com.dzf.zxkj.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/*
 * 参数设置vo
 */
@Data
@TableName("ynt_parameter")
public class YntParameterSet {
	@JsonProperty("id")
	private String pk_parameter;
	@JsonProperty("pbm")
	private String parameterbm;
	@JsonProperty("pname")//参数名称
	private String parametername;
	@JsonProperty("pvalue")//参数取值范围
	private String parametervalue;
	private String edittime;
	@JsonProperty("pdvalue")//参数取值
	private Integer pardetailvalue;
	private String detail;
	private String pk_corp;
	private Integer issync;//0同步；1未同步
	private Integer dr;

	private Integer plevel;//参数对应的公司级别0：集团级别1：会计公司级别2：小企业级别
}
