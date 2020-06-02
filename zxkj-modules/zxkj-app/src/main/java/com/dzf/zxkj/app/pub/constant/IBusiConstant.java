package com.dzf.zxkj.app.pub.constant;

/**
 * 业务类型参数
 * 
 * @author zhangj
 *
 */
public interface IBusiConstant {
	// --------------百望前台对接------------
	public static int NINE_ZERO_SEVEN = 907;// 百旺发票信息，生成凭证
	public static int NINE_ZERO_EIGTH = 908;// 百旺发票信息，作废
	public static int NINE_TEN = 910;// 网络抄送
	// --------------百望前台对接-----------

	public static int SEVENTY_THREE = 73;// 工作提醒
	public static int NINE_TWO = 92;// 获取票据信息
	public static int NINE_THREE = 93;// 图片生成凭证

	public static int NINE_FIVE = 95;// 订单查看订单
	public static int NINE_SIX = 96;// 订单查看订单详情
	public static int NINE_SEVEN = 97;// 订单查看进度
	public static int NINE_EIGTH = 98;// 订单服务评价
	public static int NINE_NINE = 99;// 订单申请退款

	public static int NINE_ZERO_ONE = 901;// 订单删除
	public static int NINE_ZERO_TWO = 902;// 确认完成
	public static int NINE_ZERO_THREE = 903;// 审批流设置
	public static int NINE_ZERO_FOUR = 904;// 审批流审核
	public static int NINE_ZERO_FIVE = 905;// 审批流查询
	public static int NINE_ZERO_SIX = 906;// 审批流驳回
	
	//资料交接
	public static int ZERO = 0;// 资料交接列表
	public static int ONE = 1;// 资料交接列表-确认
	public static int TWO = 2;// 查询资料档案
	public static int THREE = 3;// 保存添加资料
	public static int FOUR = 4;// 当面交(生成二维码数据)
	public static int FIVE = 5;// 当面收(根据二维码生成清单)
	public static int SIX = 6;// 当面收-确认
	public static int SEVEN = 7;// 转交（查询接收人信息）
	public static int EIGTH = 8;// 转交确认发送
	public static int NINE = 9;// 查询资料消息详情
	public static int TEN = 10;// 整单确认
	public static int ELEVEN = 11;//获取二维码状态
	public static int TWELVE = 12;//查询票据交接列表
	public static int THIRTEEN = 13;//查询票据清单
	public static int FOURTEEN = 14;//常见问题
	public static int EIGHTEEN = 18;//查询资料交接类型
	
	//---------------------行业档案------------
	public static final int TRADE_QRY = 15;//行业档案
	
	
	//--------------------扫描营业执照-----------
	public static final int SCAN_YYZZ = 16;//扫描营业执照
	
	//-----------------获取首页信息(目前有 营业执照链接，征期信息)-------
	public static final int INDEX_MSG = 17;//营业执照连接
	
	//-----------------更多服务--------------
	public static final int MORE_SERVICE =19;//更多服务
	
	public static final int MORE_SERVICE_DETAIL = 20;//服务详情
	

}
