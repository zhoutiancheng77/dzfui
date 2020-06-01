package com.dzf.zxkj.app.pub.constant;


/**
 * 客户/票据实例
 * @author zhangj
 *
 */
public interface IBillConstant {

	//------------开票申请信息-----------------
	public static final int QRY_KES = 1;//查询客户列表
	
	public static final int QRY_KH_ID = 2;//查询客户详情
	
	public static final int SAVE_KH = 3;//保存我的客户
	
	public static final int DELETE_KH = 4;//删除我的客户
	
	public static final int QRY_BILLS = 5;//开票统计
	
	public static final int QRY_BILL = 6;//开票详情
	
	public static final int SAVE_BILL = 7;//开票申请
	
	public static final int QRY_FWWD =8;//服务网点
	
	public static final int CORP_BILL_QRY = 9;//公司开票信息查询
	
	public static final int CORP_BILL_UPDATE = 10;//公司开票信息更新
	
	public static final int CORP_APPLY_CONFIRM = 20;//开票申请确认
	
	//--------------商品接口----------------
	public static final int COMMODITY_QRY = 11;//商品查询
	
	public static final int COMMODITY_QRY_DETAIL = 12;//查询商品详情
	
	public static final int COMMODITY_SAVE = 13;//商品保存
	
	public static final int COMMODITY_DEL = 14;//商品删除
	
	public static final int COMMODITY_TYPE_QRY = 15;//商品销售类型查询
	
	//-------------------发票信息--------------------
	public static final int INVOICE_QRY = 16;//发票统计
	
	public static final int INVOICE_TYPE_QRY =17;//发票类型查询
	
	public static final int INVOICE_DETAIL_QRY= 18;//发票详情查询
	
}
