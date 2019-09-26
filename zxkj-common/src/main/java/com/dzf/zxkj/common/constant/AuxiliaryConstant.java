package com.dzf.zxkj.common.constant;

//辅助核算常量
public class AuxiliaryConstant {
	/**
	 * 科目辅助核算(isfzhs)默认值
	 */
	public final static String ACCOUNT_FZHS_DEFAULT = "0000000000";
	//客户
	public final static String ITEM_CUSTOMER = "000001000000000000000001";
	//供应商
	public final static String ITEM_SUPPLIER = "000001000000000000000002";
	//职员
	public final static String ITEM_STAFF = "000001000000000000000003";
	//项目
	public final static String ITEM_PROJECT = "000001000000000000000004";
	//部门
	public final static String ITEM_DEPARTMENT = "000001000000000000000005";
	//存货
	public final static String ITEM_INVENTORY = "000001000000000000000006";
	/**
	 * 添加辅助明细，名称重复时抛的异常
	 */
	public final static String NAME_REPEAT_EXCEEPTION = "-150";
	/**
	 *  封存状态
	 */
	public final static Integer SEAL = 1;
	/**
	 *  解封状态
	 */
	public final static Integer UNSEAL = 0;
}
