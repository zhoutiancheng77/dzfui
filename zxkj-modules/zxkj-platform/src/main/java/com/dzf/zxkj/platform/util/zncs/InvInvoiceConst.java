package com.dzf.zxkj.platform.util.zncs;

public class InvInvoiceConst {

	/******************* 发票方法接口 ****************/

	/**
	 * #多个发票信息Post：row1，row2，row3， ... multidata =
	 * {'row1':{'dirname':'wentong1',
	 * 'filename':'2373725d-27c2-4b23-a74c-9b10b9d0bfb8.jpg','invcode':
	 * '1100154320', 'invno': '03720515', 'invdate': '20160725',
	 * 'yzmcode':'112550','wherefrom':'来自文通'}, 'row2':{'dirname':'WENTONGTEST',
	 * 'filename':'360c12a3-d1de-4756-b920-d35ba87f0837.jpg', 'invcode':
	 * '1100153130', 'invno': '36155923', 'invdate': '20160706',
	 * 'sumnotax':'65299.14','wherefrom':'来自文通' } } url =
	 * 'http://59.151.37.139:8002/addmultiinvkeys' response = requests.post(url,
	 * json=multidata) print(response.text) #成功写入的发票行数
	 */

	public static String INTERFACE_ADDMULTIINVKEYS = "addmultiinvkeys";

	/**
	 * #单个发票信息POST:data= {'dirname':'wentong1',
	 * 'filename':'2373725d-27c2-4b23-a74c-9b10b9d0bfb9.jpg', 'invcode':
	 * '1100154320', 'invno': '03720515', 'invdate': '20160725',
	 * 'typecode':'3','yzmcode':'112550','wherefrom':'来自文通'} url='http://
	 * 59.151.37.139:8002/addinvkeys' response=requests.post(url,json=data)
	 * 
	 * print(response.text)
	 * #返回值：'Json_ERROR'，'SUCCESS','Data_ERROR','ManyKeyLinesInAFile_ERROR','
	 * DB_ERROR'
	 */
	public static String INTERFACE_ADDINVKEYS = "addinvkeys";
	/**
	 * #得到发票明细信息 get http://59.151.37.139:8002/getinvoice/<string:filename>
	 */
	public static String INTERFACE_GETINVOICEBYFILENAME = "getinvoice";
	/**
	 * #立即返回发票识别结果的POST： url = 'http://localhost:8002/invkeys2info' data =
	 * {'dirname':'WENTONGTEST',
	 * 'filename':'360c12a3-d1de-4756-b920-d35ba87f08355.jpg', 'invcode':
	 * '1100153130', 'invno': '36155923', 'invdate': '20160706',
	 * 'typecode':'1','sumnotax':'65299.14','wherefrom':'来自文通'} response
	 * =requests.post(url, json=data) print(response.text)
	 */
	public static String INTERFACE_GETINVOICEBYTIME = "invkeys2info";
	/**
	 * #给定多个发票代码和号码，返回多个发票识别明细信息POST: multidata = {'row1':{'invcode':
	 * '1100154320', 'invno': '03720515'}, 'row2':{'invcode': '1100153130',
	 * 'invno': '36155923'}} url = 'http://localhost:8002/multiinvoices'
	 * response = requests.post(url, json=multidata) print(response.text)
	 */
	public static String INTERFACE_GETINVOICENO = "multiinvoices";
	/**
	 * get参数：批次号（wherefrom，建议以WT开头，加时间信息），返回这批上传的keys的识别状态。
	 * http://59.151.37.139:8002/getinvstatus/<string:batchname>
	 */
	public static String INTERFACE_GETINVOICESTATUS = "getinvstatus";
	/**
	 * get参数：批次号（wherefrom，建议以WT开头，加时间信息），返回这批上传的keys中识别正确的发票的发票代码和发票号码。
	 * http://59.151.37.139:8002/getinvright/<string:batchname> 测试链接
	 * #http://localhost:8002/getinvright/WT2016102500000001
	 */
	public static String INTERFACE_GETINVOICENOBYBATCHCODE = "getinvright";
	/**
	 * 
	 * 给定一个批次号（batchname），返回此批次的所有多个发票识别状态，正确识别的会返回其所有发票信息含明细信息POST: data =
	 * {"batchname":"WT2016102500000001"}
	 * http://59.151.37.139:8002/batchinvoices' response = requests.post(url,
	 * json=data) print(response.text)
	 */
	public static String INTERFACE_GETINVOICEBYBATCHCODE = "batchinvoices";
	/**
	 * 接口#9.
	 * Get方式，参数：批次号（wherefrom，建议以WT开头，加时间信息），返回这批上传的keys中被深度学习平台处理后状态为“分析正确”
	 * 的发票关键信息条目集合。 http://59.151.37.139:8002/getinvrightkeys/<string:batchname>
	 */
	public static String INTERFACE_GETINVRIGTHKEYS = "getinvrightkeys";

	/**
	 * #19. 通过"图片ID"(file_id)获取识别结果、识别状态及文件路径；data = {"fileID":"2613"}
	 * http://59.151.37.139:8002/invinfobyfileid' response = requests.post(url,
	 * json=data) rtn = json.loads(response.text) if len(response.text) > 5000:
	 * resfile = open("D:/TEMP/responejson.txt","w") resfile.write(str(rtn))
	 * resfile.close() else: print(response.text) print(str(response.json()))
	 * 
	 * try: strmsg = rtn.get('err_msg') if strmsg: print(str(strmsg)) except:
	 * print(rtn) 结果json样式： {'图片ID': '2687', '目录名':
	 * 'local|20180623221930837181', '文件名': '11200.jpg', '识别状态': '关键信息条目不全',
	 * '批次': '未命名20180623'}
	 */
	public static String INTERFACE_INVINFOBYFILEID = "invinfobyfileid";

	/**
	 * 自动报错或人工标注或人工干预修正识别信息，此函数包括两个功能：一是票据识别错误报告功能(简称a功能)，
	 * 二是对除增票外的其它票据识别结果做人工干预与修正功能（简称b功能）；
	 * a功能通常是针对系统通过对账单或所属公司匹配等方式自动发现单据错误或者人工发现增票或其它票据类别错误的情况，
	 * b功能是针对银行单据及其它费用票做人工标注并修正识别错误内容的情况。 data = {'fileID':'2613','票据类别':
	 * '增值税普通发票','票据错误信息': '收款方与所属公司不匹配、单据类型不匹配等(供后台人工干预参考,
	 * 只有a功能有)','单据类型':'转账','收款方名称': '何修泽','备注': '{”摘要”: ”转账取款”, ”附言”:
	 * ”实时扣税请求3001”}','所属公司名称': '北京大账房信息技术有限公司','所属公司银行账号': '111111','所属公司开户行':
	 * '招商银行'} http://59.151.37.139:8002/invkeysbyhand' response =
	 * requests.post(url, json=data) print(response.text)
	 * #返回值：Success:1；Success:0（没有这个图片ID）; JsonData_Error；DB_Error; etc
	 * 输入json说明： （1）二者都必须有的元素包括"fileID"和"票据类别"这两项，没有这两项或这两项为空就无法写入数据库。"所属公司名称"、
	 * "所属公司银行账号"及"所属公司开户行"这三项a功能和b功能都建议返回，特别是"所属公司名称"对后台有重要的参考价值。
	 * （2）“票据类别”条目列表（共五项）： 增值税发票或机动车销售发票； 银行单据； 火车票或机票行程单； 定额发票、机打发票、出租车发票等其它费用票
	 * 其它票据 （3）对于a功能的必有元素是"票据错误信息"（内容随意，无要求，只要不为空就行），如果没有这个元素，函数就认为是b功能。
	 * （4）其它元素都是可选的，且属于b功能，即人工修正了的内容就返回（即后台识别错了且人工修正正确的值内容，
	 * 比如"单据类型"错了或"收款方名称"错了等等），没有错误或未做修正的内容不必再返回（这些没有返回的关键信息内容仍然使用后台的识别结果），
	 * 元素的可选项如下：
	 * （a）“银行单据＂的可选元素包括：'单据类型'(如'b客户付款','b电子缴税付款凭证'等，必须是b开头，建议程序自动添加),'fileID'，'
	 * 付款方名称','收款方名称','付款方账号','付款银行','收款方账号','收款银行','单据标题','日期','金额','银行名称','备注'
	 * ,'单据标识号','税项明细'
	 * （b）＂火车票或机票行程单＂的可选元素包括：'单据类型','fileID','出发地','目的地','日期','金额','备注'
	 * （c）＂定额发票、机打发票、出租车发票等其它费用票＂的可选元素包括：'单据类型','fileID','发票代码','发票号码','日期','金额'
	 * ,'备注'（要求输入地区名称，如北京）
	 */
	public static String INTERFACE_INVKEYSBYHAND = "invkeysbyhand";
	
	public static  String batchessetinvalid = "batchessetinvalid";//票据作废取消复检 batchessetinvalid
	/************************** 方法类型 ********************************/

	public static String TYPE_METHOD_POST = "POST";
	public static String TYPE_METHOD_GET = "GET";
}
