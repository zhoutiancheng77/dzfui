package com.dzf.zxkj.app.pub.constant;

public interface IConstant {

	public static final String CHATPHOTO = "/Chat/dzf_head.jpg";

	public static final String PRIID_COM = "@0000000000@";
	
	public static final String DEFAULTPHONE = "4006009365";
	
	// -----------app权限---------------
	public static final String REPORT = "0";
	public static final String IMAGE = "1";
	public static final String ALLPOW = "2";
	public static final String BILLAPPLY = "3";

	// ----------------审批信息(审批角色)-----------------
	public static final String NORMAL = "normal";
	public static final String ADMIN = "admin";
	public static final String CASH = "cash";
	// ----------------审批信息(审批操作)-----------------
	public static final String COMMIT = "commit";// 提交
	public static final String APPROVE = "approve";// 审批
	public static final String APPRETURN = "appreturn";// 驳回
	public static final String RETURN = "return";// 退回
	public static final String VOUCHER = "voucher";// 已制证

	public static String DEFAULT = "0";
	public static String FIRDES = "1";

	/*
	 * 用户模块标识： 0-注册获取验证码 1-修改密码获取验证码 2-注册 3-修改密码 4-用户登录 5-人员管理,查询用户列表
	 * 51-人员管理--审核 52-人员管理--取消权限 53-人员管理--删除 6-用户设置,根据公司，查询服务机构
	 * 7-我的客服，查询用户对应客服信息 71-联系客服，查询用户及客服信息 8-系统消息 9-根据公司查询其附近组织机构 111 查询图像历史
	 */
	public static String operateFlag = "operate";
	public static int ZORE = 0;

	public static int ONE = 1;
	public static int GeogPrecision = 1;
	public static int ELEVEN = 11;
	public static int TWELVE = 12;// 支出比重分析 WJX
	public static int THIRTEEN = 13;// 利润增长分析 WJX
	public static int FOURTEEN = 14;// 税负预警 WJX
	public static int FIFTEEN = 15;// 纳税明细 WJX
	public static int SALARY_QRY= 16;//工资表查询
	public static int SALARY_DETAIL_QRY =17;//工资表详情
	public static int NSSB_QRY =18;//纳税申报查询
	public static int ONE_NINE = 19;//利润情况
	public static int ONE_ZERO_ZERO = 100;//税负率预警
	public static int ONE_ZERO_ONE = 101;//征期日历
	public static int ONE_ZERO_TWO = 102;//辅助余额表
	public static int ImageHis = 111;
	public static int TEN = 10;

	public static int TWO = 2;// 老版本注册
	public static int TWO_ZERO_ZERO = 200;// 新版本公司注册
	public static int TWO_ZERO_ONE = 201;// 新版本用户注册
	public static int TWO_TWO = 22;// 完善用户信息 WJX
	public static int TWO_THREE = 23;// 验证公司是否存在 WJX
	public static int TWO_FOUR = 24;// 手机端调用接口注册环信IM账号
	public static int TWO_FIVE = 25;// 获取聊天对象
	public static int TWO_ZERO_TWO = 202;// 新用户登录
	public static int TWO_ZERO_THREE = 203;// 用户获取公司
	public static int TWO_ZERO_FOUR = 204;// 更改手机号信息
	public static int TWO_ZERO_FOUR_ONE = 2041;// 更改手机号信息
	public static int TWO_ZERO_FIVE = 205;// 用户添加公司
	public static int TWO_ONE_FIVE = 215;// 用户添加公司,服务机构版
	public static int TWO_ZERO_FIVE_ONE = 2051;// 确定提交USERADDCORP_ONE
	public static int TWO_ZERO_FIVE_TWO = 2052;// 审核提交USERADDCORP_TWO
	public static int TWO_ZERO_SIX = 206;// 公司信息上传CORPADDMSG
	public static int TWO_ZERO_SEVEN = 207;// 验证码-找回密码INDETIFYBACKMDY
	public static int TWO_ZERO_EIGTH = 208;// 找回(重置)密码BACKDMY
	public static int TWO_ZERO_NINE = 209;// 获取公司信息CORPMSG
	public static int TWO_ONE_ZERO = 210;// 修改密码MOIDYFYPSW
	public static int TWO_ONE_ONE = 211;// 激活公司
	public static int TWO_ONE_TWO = 212;// 微信绑定
	public static int TWO_ONE_THREE = 213;// 百旺iot号注册
	public static int TWO_ONE_FOUR = 214;// 服务机构版注册
	public static int TWO_ONE_SIX = 216;//微信授权登录
	public static int TWO_ONE_SERVEN = 217;//解除绑定
	public static int TWO_ONE_EIGHT = 218;//登录成功绑定
	public static int TWO_ONE_NINE = 219;//获取昵称
	public static int TWO_TWO_ZERO = 220;//扫码二维码(扫码登录)
	public static int TWO_TWO_ONE = 221;//扫描确认(扫码登录)
	public static int TWO_TWO_TWO = 222;//获取图片(发送短信验证码)
	public static int TWO_TWO_THREE = 223;//验证是否正确(发送短信验证码)
	public static int TWO_TWO_FOUR = 224;//扫描公司二维码(加入公司)
	public static int TWO_TWO_FIVE = 225;//
	public static int TWO_TWO_SIX = 226;//微信小程序，获取公司信息(如果该用户存在该信息，则登录)
	

	public static int THREE = 3;
	public static int THIRTY_ONE = 31;
	public static int THIRTY_TWO = 32; // 设置密码
	public static int TAXMSGQRY = 307;// 申报信息查询
	public static int TAXSTAEDIT = 308;// 申报申报状态修改

	// ------------获取验证码---------------------
	public static int FORTH = 4;
	public static int FORTH_ONE = 41;// 公司存在获取验证码
	public static int FORTH_TWO = 42;// 新增公司重新获取验证码
	public static int FORTH_THREE = 43;// 更改手机号获取验证码
	public static int FORTH_FORTH = 44;// 微信获取验证码
	public static int FORTH_FIVE = 45;//大账房app获取验证码
	public static int FORTH_SIX  = 46;//微信邀请人加入获取验证码
	// ------------获取验证码结束------------------

	// ------------人员信息---------------------
	public static int FIVE = 5;
	public static int FIFTY_ONE = 51;
	public static int FIFTY_TWO = 52;
	public static int FIFTY_THREE = 53;
	public static int FIFTY_FOUR = 54;
	public static int FIFTY_FIVE = 55;
	public static int FIFTY_SIX = 56;
	public static int FIFTY_SEVEN = 57;
	public static int FIFTY_EIGHT = 58;
	public static int FIFTY_NINE = 59;//获取注册公司信息
	public static int FIFTY_ZERO_ONE = 501;
	public static int FIFTY_ZERO_TWO = 502;
	// -------------人员信息结束---------------

	// ---------------组织机构信息-------------
	public static int SIX = 6;
	public static int SIXTY = 60;
	public static int SIXTY_ONE = 61;
	public static int SIXTY_TWO = 62;//查询签约机构
	public static int SIXTY_THREE = 63;
	public static int SIXTY_FOUR = 64;//服务机构版查询签约代账机构
	public static int SIXTY_FIVE = 65;//服务机构版申请代账机构
	// ---------------组织机构信息结束-------------

	// --------------------报表+业务+订单处理逻辑接口-----------
	public static int SEVEN = 7;
	public static int SEVENTY_ONE = 71;
	public static int SEVENTY_TWO = 72;
	public static int EIGTH = 8;//
	public static int EIGTH_TWO = 82;// 聊天上传图片
	public static int NINE = 9;
	public static int NINTY_ONE = 91;
	public static int VERSION = 99;
	public static int NINE_FOUR = 94;// 服务评价获取信息
	public static int NINE_ZERO_NINE = 909;// 提醒送票完成
	public static int EIGHT_ZERO_ONE = 801;// 申报表
}
