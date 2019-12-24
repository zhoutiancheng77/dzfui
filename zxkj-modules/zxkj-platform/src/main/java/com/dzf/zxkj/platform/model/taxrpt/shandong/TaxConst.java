package com.dzf.zxkj.platform.model.taxrpt.shandong;

public class TaxConst {

	// 山东中税服务编码
	// 纳税人账号登录验证
	public static String SERVICE_CODE_NSRDLYZ = "C00.TY.SFYZ.nsrdlyz";

	// 查询清册
	public static String SERVICE_CODE_QC = "C00.TY.YBSB.nsrsbqccx";

	public static String SERVICE_CODE_ZT = "Fxsw.SB.queryNsrsbjg";

	// 增值税小规模纳税人申报(营改增)申报提交
	public static String SERVICE_CODE_ZZSXGM = "SJJZ.SB.sbSubmit.Zzsxgmnsr";
	public static String XMLTYPE_ZZSXGM = "HXZGSB00045Request";
	public static String CODE_ZZSXGM = "BDA0610611";
	// 增值税小规模申报(营改增)期初
//	public static String SERVICE_CODE_ZZSXGMINIT = "Fxsw.SB.SbInit.91061001060";
	public static String SERVICE_CODE_ZZSXGMINIT = "SDSW.SbInit.ZZSXGMSBSQJKJHQQCSJ";
	public static String XMLTYPE_ZZSXGMQC = "HXZGSB00044Request";
	public static String ZZSXGSCHEMALOCATION="http://www.chinatax.gov.cn/dataspec/TaxMLBw_HXZG_SB_00044_Request_V1.0.xsd";
	// 增值税一般纳税人申报(营改增)申报提交
	public static String SERVICE_CODE_ZZSYBNSR = "SJJZ.SB.sbSubmit.Zzsybnsr";
	public static String XMLTYPE_ZZSYBNSR = "HXZGSB00041Request";
	public static String CODE_ZZSYBNSR = "BDA0610606";
	// 增值税一般纳税人申报(营改增)期初
//	public static String SERVICE_CODE_ZZSYBNSRINIT = "Fxsw.SB.SbInit.91061001059";
	public static String SERVICE_CODE_ZZSYBNSRINIT = "SDSW.SbInit.ZZSYBRSBSQJKJHQQCSJ";
	public static String XMLTYPE_ZZSYBNSRQC = "HXZGSB00040Request";
	public static String ZZSYBNSCHEMALOCATION="http://www.chinatax.gov.cn/dataspec/ TaxMLBw_HXZG_SB_00040_Request_V1.0.xsd";
	// 汇算清缴
	public static String SERVICE_CODE_HSQJ = "SJJZ.SB.sbSubmit.NdsdsAHSQJ";
	public static String XMLTYPE_HSQJ = "HXZGSB00858Request";

	// 一般企业财务报表报送与信息采集申报提交
	public static String SERVICE_CODE_YBQYCB = "SJJZ.SB.sbSubmit.YbqyCwbb";
	public static String XMLTYPE_YBQYCB = "HXZGSB00858Request";
	public static String CODE_YBQYCB = "ZLA0610202";

	// 小企业会计准则类财务报表报送与信息采集申报提交
	public static String SERVICE_CODE_XQYKJZZLCB = "SJJZ.SB.sbSubmit.XqykjzzlCwbb";
	public static String XMLTYPE_XQYKJZZLCB = "HXZGSB01341Request";
	public static String CODE_XQYKJZZLCB = "ZLA0610219";

	// 企业所得税A(查账征收)
	public static String SERVICE_CODE_QYSDSA = "Fxsw.SWZJ.HXZG.SB.QYSDSCZZSYJDSBSAVE";
	public static String XMLTYPE_QYSDSA = "HXZGSB00863Request";
	// public static String SERVICE_CODE_QYSDSAINIT =
	// "Fxsw.SB.SbInit.910610010212";
	//企业所得税 查账期初数获取	
	public static String SERVICE_CODE_QYSDSAINITNEW = "SDSW.SbInit.QYSDSCZZSYJDSBCSH";
	public static String SERVICE_CODE_QYSDSAINIT = "Fxsw.SWZJ.HXZG.SB.QYSDSCZZSYJDSBCSH";

	public static String CODE_QYSDSA = "BDA0611033";
//	public static String CODE_QYSDSA = "BDA0610756";
	public static String SDS_YZPZZLDM_A = "BDA0611033";

	// 企业所得税B(核定征收)
	// public static String SERVICE_CODE_QYSDSB =
	// "SJJZ.SB.sbSubmit.Qysds2008JdB";
	public static String SERVICE_CODE_QYSDSB = "Fxsw.SWZJ.HXZG.SB.BCJMQYHDZSQYSDSSB2018";
	public static String XMLTYPE_QYSDSB = "HXZGSB00006Request";
//	企业所得税核定初始化期初数获取	
	public static String SERVICE_CODE_QYSDSBINIT = "Fxsw.SWZJ.HXZG.SB.JMQYHDZSQYSDSSBSQJKJHQQCS2018";
	public static String SERVICE_CODE_QYSDSBINITNEW = "SDSW.SbInit.JMQYHDZSQYSDSSBSQJKJHQQCS2018";
	public static String CODE_QYSDSB = "BDA0611038";
//	public static String CODE_QYSDSB = "BDA0610764";
	public static String SDS_YZPZZLDM_B = "BDA0611038";

	public static String SDS_ZGSWJDM = "24406050300";
	public static String SDS_YZPZZLDM_AND = "BDA0610994";
	public static String SDS_YZPZZLDM_AYJD = "BDA0610756";
	public static String SDS_YZPZZLDM_BND = "BDA0610764";
	public static String SDS_YZPZZLDM_BYJD = "BDA0610764";
	public static String SDS_ZGSWKFJDM = "24406050300";

	// 文化事业建设费
	public static String SERVICE_CODE_WHSYJSF = "Fxsw.SB.SbSubmit.910610010612";
	public static String XMLTYPE_WHSYJSF = "HXZGSB00048Request";
	public static String SERVICE_CODE_WHSYJSFINIT = "Fxsw.SB.SbInit.910610010612";
	public static String CODE_WHSYJSF = "BDA0610334";

	public static String IMPLVALUE = "BASE64";

	// 0代表本次申报成功，1代表申报失败，-1代表身份验证失败（包括sign验证失败和token验证失败）,-2 业务自定义信息
	public static String RETURN_ITEMKEY_NO = "no";

	// SUCCESS表示通过，ERROR表示失败
	public static String RETURN_ITEMKEY_MSG = "msg";

	// 业务报文编码 0000 代表成功
	public static String RETURN_CODE = "rtnCode";
	public static String RETURN_RTNCODE = "returnCode";
	

	// 业务报文信息
	public static String RETURN_MSG = "rtnMsg";
	public static String RETURN_RTNMSG = "returnMessage";
	public static String RETURN_REASON = "reason";

	// 返回的具体信息
	public static String RETURN_ITEMKEY_OBJ = "obj";

	// 返回的报文信息
	public static String RETURN_ITEMKEY_XML = "xml";

	// 返回的清册信息
	public static String RETURN_ITEMKEY_QC = "qc";
	
	// 返回的djxh
	public static String RETURN_ITEMKEY_DJXH = "djxh";

	// 返回的token
	public static String RETURN_ITEMKEY_TOKEN = "token";

	// 返回的sessionId
	public static String RETURN_ITEMKEY_SESSIONID = "sessionId";

	// 返回的sessionId
	public static String RETURN_ITEMKEY_RMESSAGE = "returnMessage";
	
	// 返回的sessionId
	public static String RETURN_ITEMKEY_MESSAGE = "message";
}
