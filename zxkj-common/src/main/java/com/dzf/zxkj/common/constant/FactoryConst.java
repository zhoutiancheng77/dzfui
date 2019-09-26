package com.dzf.zxkj.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 会计工厂常量
 * 
 * @author mfz
 * 
 */
public interface FactoryConst {

//	public final static String DEFAULT_ROLE_ID="000001000000016NLVQP000E";//默认会计工厂角色
	//
	public final static String DEFAULT_ROLE_ZD = "000001000000016NLVQPmfz3";//制单岗
	public final static String DEFAULT_ROLE_SH = "000001000000016NLVQPmfz4";//审核岗
	public final static String DEFAULT_ROLE_GLY = "000001000000016NLVQPmfz6";//超级管理员

	/**
	 * 业务申请、业务确认
	 */
	// 合同状态
	public final static int ContractStatus_0 = 0;// 已保存
	public final static int ContractStatus_1 = 1;// 已审批
	public final static int ContractStatus_2 = 2;// 已申请
	public final static int ContractStatus_3 = 3;// 在执行
	public final static int ContractStatus_4 = 4;// 已终止
	public final static String ContractStatusName_0 = "已保存";
	public final static String ContractStatusName_1 = "已审批";
	public final static String ContractStatusName_2 = "已申请";
	public final static String ContractStatusName_3 = "在执行";
	public final static String ContractStatusName_4 = "已终止";

	public final static Map<Integer, String> contractStatus = new HashMap<Integer, String>() {
		{
			put(ContractStatus_0, ContractStatusName_0);
			put(ContractStatus_1, ContractStatusName_1);
			put(ContractStatus_2, ContractStatusName_2);
			put(ContractStatus_3, ContractStatusName_3);
			put(ContractStatus_4, ContractStatusName_4);
		}
	};

	/**
	 * 单证领用规则
	 */
	// 启用状态
	public final static int ReceiveRuleStatus_0 = 0;// 已保存
	public final static int ReceiveRuleStatus_1 = 1;// 已启用
	public final static int ReceiveRuleStatus_2 = 2;// 已停用
	// 领取方式
	public final static int ReceiveModeStatus_0 = 0;// 按条件逐一领取
	public final static int ReceiveModeStatus_1 = 1;// 按制单员分配
	// 条件的key
	public final static String ReceiveCondKey_0 = "customer";// 客户
	public final static String ReceiveCondKey_1 = "industry";// 行业
	public final static String ReceiveCondKey_2 = "taxpaytype";// 纳税人类型
	public final static String ReceiveCondKey_3 = "servicelevel";// 服务级别
	// 纳税人类型
	public final static int ReceiveTaxpaytype_0 = 0;// 一般纳税人
	public final static int ReceiveTaxpaytype_1 = 1;// 小规模纳税人
	// 自动生成流水号的常量
	public final static String SerialNumber_ReceiverRule = "LYGZ";// 单证领用规则
	//自动生成流水号的常量---会计工厂ID
	public final static String SerialNumber_KJGC_Rule = "KJGC";

	/**
	 * 任务表 状态
	 */
	public final static int FactoryTask_0 = 0;//制单中
	public final static int FactoryTask_1 = 1;//已制单
	public final static int FactoryTask_2 = 2;//已复核
	public final static int FactoryTask_3 = 3;//已确认
	public final static int FactoryTask_4 = 4;//已过期
	public final static int FactoryTask_5 = 5;//已退回
	public final static int FactoryTask_6 = 6;//已删除(在线端删除凭证)
}
