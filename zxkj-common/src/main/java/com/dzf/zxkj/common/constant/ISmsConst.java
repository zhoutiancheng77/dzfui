package com.dzf.zxkj.common.constant;

/**
 * 短信常量类
 * @author mfz
 *
 */
public interface ISmsConst {
	
	/**
	 * 内部系统appId
	 */
	public static final String APPID_JITUAN="jituan";//集团
	public static final String APPID_KJ="dzf_kj";//在线会计
	public static final String APPID_ADMIN="admin_kj";//管理端
	public static final String APPID_WEBSITE="website";//网站
	public static final String APPID_OWNER="dzf_owner";//企业主
	public static final String APPID_WEIXIN="dzf_weixin";//微信公众号
	public static final String APPID_APPLET="wx_applet";//微信小程序
	public static final String APPID_XJR="xjr";//小巨人
	public static final String APPID_KCDR="kcdr";//卡车达人
	public static final String APPID_PST="pst";//票税通
	public static final String APPID_CST="cst";//财税通
	public static final String APPID_FINANCE="dzf_finance";//金融超市
	public static final String APPID_APP="dzf_app";//大账房app
	public static final String APPID_FK="dzf_fk";//汇算清缴风控系统
	public static final String APPID_SSO="dzf_ssoserver";//单点服务器
	public static final String APPID_YSDQ="ysdq";//益世点晴
	public static final String APPID_FPSM="dzf_fpsm";//发票扫码(罗力华)
	public static final String APPID_PLATFORM="dzf_platform";//微信第三方平台
	public static final String APPID_PJFJ="pjfj";//票据复检
	public static final String APPID_FAT="dzf_fat";//全才人，葛经纬
	public static final String APPID_LZB="lzb";//绿账本，王宝库
	/*
	 * 内部系统appkey(可以定义在配置文件里)
	 */
	public static final String APPKEY_JITUAN="11db0fbe-801b-4bcd-b34c-0eec9d1477c8";//集团
	public static final String APPKEY_KJ="7c744a34-65b3-492e-b088-21efe46bffc1";//在线会计
	public static final String APPKEY_ADMIN="86511827-514a-476a-82e4-2d4405424e63";//管理端
	public static final String APPKEY_WEBSITE="49603d5c-58f9-42e0-971f-737c5dd0ea9a";//网站
	public static final String APPKEY_OWNER="53500122-5c74-4e7c-9684-64ddea53bde5";//企业主
	public static final String APPKEY_WEIXIN="23588473-d476-45f9-b248-9c345bf8c8ec";//微信公众号
	public static final String APPKEY_APPLET="0bc2cadf-2b6d-4a04-9e1e-1da3cbab901e";//微信小程序
	public static final String APPKEY_XJR="bcb6071e-8c8a-4acc-82e6-022686cb51f1";//小巨人
	public static final String APPKEY_KCDR="b474e1b5-8296-4e06-a84b-53c827fda9e0";//卡车达人
	public static final String APPKEY_PST="23158a4a-b46f-474a-b749-1e2a1606e094";//票税通
	public static final String APPKEY_CST="2b3fba8a-313d-44c6-ae9a-6ba4091dc63d";//财税通
	public static final String APPKEY_FINANCE="5ddf3fc5-94c2-420f-b327-0e0503505748";//金融超市
	public static final String APPKEY_APP="a7c6476e-f9ec-458e-a572-f5f0659fed09";//大账房app
	public static final String APPKEY_FK="e732818c-5155-4412-8a65-6f475d0d30e8";//汇算清缴风控系统
	public static final String APPKEY_SSO="d90fee36-c24e-405e-b551-19d8cab44419";//单点服务器
	public static final String APPKEY_YSDQ="7319a9c0-4425-474f-9d6d-dc64c21aa779";//益世点晴
	public static final String APPKEY_FPSM="21c90888-76e0-4a8d-8a8f-f21294800808";//发票扫码(罗力华)
	public static final String APPKEY_PLATFORM="e79a29f4-241d-4fbb-b9ab-ab9ac2d507f8";//微信第三方平台
	public static final String APPKEY_PJFJ="415dc2bb-03fd-45c1-bb4e-0ca3180f918d";//票据复检
	public static final String APPKEY_FAT="e28addaa-1a1d-4034-a296-ba37e6891e2b";//全才人
	public static final String APPKEY_LZB="3e21a0b0-5220-42db-b1c3-6a9e43f40966";//绿账本
	/**
	/**
	 * 短信签名
	 */
	public static final String SIGN_01="01";//亿美通道：【大账房】;梦网通道：【大账房】
	public static final String SIGN_02="02";//亿美通道：【财务在线】;梦网通道：【大账房】
	public static final String SIGN_03="03";//亿美通道：【宗兴会计企业服务】;梦网通道：【云通知】
	public static final String SIGN_04="04";//亿美通道：【企业助手】;梦网通道：【云通知】
	public static final String SIGN_05="05";//亿美通道：【擎天科技】;梦网通道：【云通知】
	public static final String SIGN_06="06";//亿美通道：【全才人】;梦网通道：【云通知】
	public static final String SIGN_07="07";//亿美通道：【绿账本】;梦网通道：【云通知】
	/**
	 * 短信模板
	 */
	public static final String TEMPLATECODE_0001="SMS0001";
	//验证码:${verify}，（大账房绝对不会索取此验证码，切勿告知他人），请您10分钟内在页面中输入以完成验证。
	public static final String TEMPLATECODE_0002="SMS0002";
	//您已成功购买大账房${commodityname}增值服务业务，订单号：${vordercode}。
	public static final String TEMPLATECODE_0003="SMS0003";
	//您已成功购买大账房${commodityname}增值服务业务，订单号：${vordercode}，下载安装后，如首次登录系统，登录用户名为：${vphone}，密码为：${password}，登录即可使用该产品，如有问题可以联系我们：400-600-9365。
	public static final String TEMPLATECODE_0004="SMS0004";
	//${vphone}客户已成功购买${commodityname}增值服务产品，付款金额：${norderprice}元，订单号：${vordercode}，请及时处理。
	public static final String TEMPLATECODE_0005="SMS0005";
	//您已成功购买${vbusitypename}，订单号：${vordercode}，我们会及时处理的您的订单业务，如有问题可以联系我们：400-600-9365。
	public static final String TEMPLATECODE_0006="SMS0006";
	//您发布的${vbusitypename}已被购买，姓名：${vusername} 联系方式：${vphone}，请联系用户并在24小时内生成合同。用心为客户服务。
	public static final String TEMPLATECODE_0007="SMS0007";
	//您发布的${busitypename}业务已经被${clientname}选中，联系电话为${clienttelphone}，请及时与客户联系哦！（请按照北京大账房提供的价格为${nprice}元报价）。
	public static final String TEMPLATECODE_0008="SMS0008";
	//手机号${vphone}正申请加入${corpname}公司，请登录${sysname}app审核。
	public static final String TEMPLATECODE_0009="SMS0009";
	//套餐:${ordername}已被购买，姓名：${uname}联系方式:${vphone}，请联系用户并在24小时内生成合同。用心为客户服务。
	public static final String TEMPLATECODE_0010="SMS0010";
	//套餐:${ordername}，订单号：${ordercode}，我们会及时处理的您的订单业务，如有问题可以联系我们：400-600-9365保存服务机构。
	public static final String TEMPLATECODE_0011="SMS0011";
	//恭喜您，${corpname}公司${phone}申请与您签约，作为您的潜在客户，请贵司安排人员及时联系回复并登录进行相关操作：http://gs.dazhangfang.com 如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0012="SMS0012";
	//温馨提示：手机${vphone}选择您作为代理服务机构，请尽快与该用户联系!
	public static final String TEMPLATECODE_0013="SMS0013";
	//恭喜您，${corpaddr}客户${corpname}公司${vphone}申请与您签约，作为您的潜在客户，请贵司安排人员及时联系回复并登录进行相关操作：http://gs.dazhangfang.com 如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0014="SMS0014";
	//恭喜您，您已成功签约${corpname}代账机构，请保持手机畅通以便及时联系您。快捷报账尽在${sysname}。如有疑问请致电客服：${kfphone} 感谢您的使用！
	public static final String TEMPLATECODE_0015="SMS0015";
	//恭喜您，客户${vphone}已与您签约，请及时回复或登录客户端进行相关操作， 如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0016="SMS0016";
	//验证码:${identify}，请勿告诉他人，请在10分钟内完成验证！如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0017="SMS0017";
	//温馨提示：手机${vphone}正在修改密码验证，由于该用户未在系统中维护手机号，现将验证码发送给您，请确认该手机号为贵公司员工，如为公司员工，请10分钟内将验证码转发，如不是请勿理会，验证码：${identify}
	public static final String TEMPLATECODE_0018="SMS0018";
	//温馨提示：${corpname}公司，手机号${vphone}邀请您使用移动账务，请在移动账务手机端注册并绑定服务机构后查看，快捷报账尽在移动账务。如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0019="SMS0019";
	//温馨提示：${corpname}公司，手机号${vphone}邀请您加入大账房会计报账，请在大账房手机端查看，快捷报账尽在大账房。如有疑问请致电客服：${kfphone}感谢您的使用！
	public static final String TEMPLATECODE_0020="SMS0020";
	//验证码：${identify}手机号${vphone}正申请加入${corpname}公司并将成为该公司管理员。如果您同意，请告知对方验证码。切勿告诉他人，${sysname}管理员也不会向您索取验证码。请在10分钟内完成验证，如有疑问请致电客服：${kfphone}，感谢您的使用！
	public static final String TEMPLATECODE_0021="SMS0021";
	//手机号${vphone}正申请加入${corpname}公司，请登录${sysname}app审核。
	public static final String TEMPLATECODE_0022="SMS0022";
	//验证码：${identify}请勿告诉他人，请在10分钟内完成验证！ 如有疑问请致电客服：4006009365感谢您的使用！
	public static final String TEMPLATECODE_0023="SMS0023";
	//验证码：${identify},请10分钟内完成验证。
	public static final String TEMPLATECODE_0024="SMS0024";
	//尊敬的${customer}，贵公司的${business}的${contract}合同截止${senddate}应收${receivemny}元，应收月份是${receivemonth}，请知晓！如您已付款，请忽略此短信。服务商：${accountcompname}。
	public static final String TEMPLATECODE_0025="SMS0025";
	//${customer}，贵公司的${business}已办理到${stepname}，请知晓！服务商：${accountcompname}。
	public static final String TEMPLATECODE_0026="SMS0026";
	//${customer}，贵公司的${business}的${contract}合同${date}到期，请知晓！服务商：${accountcompname}。
	public static final String TEMPLATECODE_0027="SMS0027";
	//报表查询验证码：${identify}，请在10分钟内完成验证！
	public static final String TEMPLATECODE_0028="SMS0028";
	//尊敬的大账房客户，您公司申请的大账房系统已经审批通过！大账房系统中会计公司管理平台登录地址是http://gs.dazhangfang.com。您公司的管理员用户为:${user_code},密码为${user_password},您可以用此用户创建客户及您公司的会计用户等操作（会计在线做账平台登录网址是http://kj.dazhangfang.com）。大账房公司提醒你及时登录系统修改初始密码！如有问题请致：4006009365及时与客服人员取得联系!
	public static final String TEMPLATECODE_0029="SMS0029";
	//潜在客户${corpname}申请与${orgname}代账机构签约业务合作，请及时关注，防止客户流失。
	public static final String TEMPLATECODE_0030="SMS0030";
	//潜在客户${corpaddr}客户${vphone}申请与${orgname1}代账机构和代账机构${orgname2}签约业务合作，请及时关注，防止客户流失。
	public static final String TEMPLATECODE_0031="SMS0031";
	//尊敬的${customer}，贵公司的${business}的${contract}合同截止${senddate}总应收${receivemny}元，应收详情是【应收月份${month1}，应收金额${money1}元，应收月份${month2}，应收金额${money2}元...】，请知晓！如您已付款，请忽略此短信。服务商：${accountcompname}。
	public static final String TEMPLATECODE_0032="SMS0032";
	//${customer}激活码为${code},请勿向陌生人提供此激活码。服务商：${accountcompname}。
	public static final String TEMPLATECODE_0033="SMS0033";
	//验证码：${identify}请勿告诉他人，请在10分钟内完成验证！ 如有疑问请致电客服：（025）962166,感谢您的使用！
	public static final String TEMPLATECODE_0034="SMS0034";
	//验证码：${identify}请勿告诉他人，请在10分钟内完成验证！ 如有疑问请致电客服：025-962166，感谢您的使用！
	public static final String TEMPLATECODE_0035="SMS0035";
	//尊敬的大账房客户，您公司申请的大账房系统已经审批通过！大账房系统中会计公司管理平台登录地址是http://gs.dazhangfang.com。您公司的管理员用户为:${user_code},密码为${user_password},您可以用此用户创建客户及您公司的会计用户等操作（会计在线做账平台登录网址是http://kj.dazhangfang.com）。大账房公司提醒您及时登录系统修改初始密码！如有问题请致：4006009365及时与客服人员取得联系!
	public static final String TEMPLATECODE_0036="SMS0036";
	//尊敬的大账房客户，您公司申请的大账房系统已经审批通过！大账房系统中会计公司管理平台登录地址是http://gs.dazhangfang.com。您公司的管理员用户为:${user_code},密码为${user_password},您可以用此用户创建客户及您公司的会计用户等操作（会计在线做账平台登录网址是http://kj.dazhangfang.com）。大账房公司提醒您及时登录系统修改初始密码！如有问题请致：4006009365及时与客服人员取得联系!
	public static final String TEMPLATECODE_0037="SMS0037";
	//尊敬的${corpname}公司，已为您免费开通移动财务管理 APP（账号${phone}，默认密码${pwd}，服务机构识别号${qysbh}）。您可点击https://www.dazhangfang.com/xiaoweiwuyou_mobile/downapp.html下载 APP，在首页-公司管理-点击待激活公司输入激活码即可使用。移动账务APP，老板的移动财务部！
	public static final String TEMPLATECODE_0038="SMS0038";
	//尊敬的${corpname},${fcorpname}已为您免费开通移动财务管理APP（账号${phone}，密码${pwd}）。您可点击https://www.dazhangfang.com/dzfmobile/other/index.html下载APP，在首页-公司管理-点击待激活公司输入激活码即可使用。大账房APP，老板的移动财税管理专家！
	public static final String TEMPLATECODE_0039="SMS0039";
	//尊敬的${corpname}公司，已为您免费开通移动账务 APP（账号${phone}，默认密码${pwd}，服务机构识别号${qysbh}）。您可点击https://www.dazhangfang.com/xiaoweiwuyou_mobile/downapp.html，下载 APP后在首页-公司管理-点击待激活公司输入激活码即可使用。移动账务APP，老板的移动财务部！
	public static final String TEMPLATECODE_0040="SMS0040";
	//尊敬的${corpname}，${fcorpname}已为您免费开通大账房APP（账号${phone}，密码${pwd}）。您可点击https://www.dazhangfang.com/dzfmobile/other/index.html，下载APP后在首页-公司管理-点击待激活公司输入激活码即可使用。大账房APP，老板的移动财税管理专家！
	public static final String TEMPLATECODE_0041="SMS0041";
	//亲爱的客户，在这个特别的日子，${corpname}祝您生日快乐，幸福美满！
	public static final String TEMPLATECODE_1001="SMS1001";//（益世点晴）
	//${code}（动态验证码），请勿告诉他人，请在10分钟内完成验证。
	public static final String TEMPLATECODE_2001="SMS2001";//（票据复检）
	//提醒：有${num}条票据等待复检，请有关责任人尽快开始复检。
	public static final String TEMPLATECODE_3001="SMS3001";
	//初始登录密码是：${invitecode}。${customer}，欢迎登录微信小程序“全才人”，为了及时收到与您合作平台的费用，请尽快完善资料。
	public static final String TEMPLATECODE_3002="SMS3002";
	//验证码：${invitecode}。欢迎登录微信小程序“全才人”，为了及时收到与您合作平台的费用，请尽快完善资料。
	public static final String TEMPLATECODE_3003="SMS3003";
	//您开通钱包的验证码是${invitecode}。请不要把验证码泄露给他人！10分钟内有效。
	public static final String TEMPLATECODE_3004="SMS3004";
	//您绑定银行卡的验证码是${invitecode}。请不要把验证码泄露给他人！10分钟内有效。
	public static final String TEMPLATECODE_4001="SMS4001";
	//${customer}，欢迎登录微信小程序“绿账本”，为了及时收到与您合作企业的费用，请尽快完善资料。验证码：${verify}
	public static final String TEMPLATECODE_4002="SMS4002";
	//验证码：${invitecode}，欢迎登录微信小程序“绿账本”，10分钟内有效。
	public static final String TEMPLATECODE_4003="SMS4003";
	//验证码：${invitecode}，10分钟内有效。
}
