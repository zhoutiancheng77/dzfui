package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.IYjsbWebService;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.CreateSignUtils;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.ParseJsonData;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.TaxParamUtils;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;

import java.util.HashMap;

/**
 * 
 * 山东中税web服务
 */
@Slf4j
public class WebServiceProxy {

	// 这里是创建一个service
	private static IYjsbWebService yjsbWs = null;
	private static long TIME_OUT = 15000L;
	private static int RETRY_TIMES = 1;
	private static int SO_TIMEOUT = 20000;

	private static String getTaxUrl() {
		String taxurl = TaxParamUtils.PROTAXURL;
		if ("true".equals(TaxParamUtils.ISTEST)) {
			taxurl = TaxParamUtils.TESTTAXURL;
		}
		return taxurl;
	}

	public static IYjsbWebService getWebService() {
		try {
			if (yjsbWs == null) {
				// 这里是创建一个service，需要传入一个接口类，因为我们后面必须调用相应的接口方法
				Service srvcModel = new ObjectServiceFactory().create(IYjsbWebService.class);
				// 代理工厂，这里是为了后面创建相应的接口类
				XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
				yjsbWs = (IYjsbWebService) factory.create(srvcModel, getTaxUrl());

				HttpClientParams params = new HttpClientParams();

				params.setParameter(HttpClientParams.USE_EXPECT_CONTINUE, Boolean.FALSE);

				params.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, TIME_OUT);// 单位是毫秒

				params.setParameter(HttpClientParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler(RETRY_TIMES, false));
				params.setParameter(HttpClientParams.SO_TIMEOUT, SO_TIMEOUT);

				Client c = Client.getInstance(yjsbWs);
				c.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, params);
				c.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, SO_TIMEOUT);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("获取中税服务失败:" + e.getMessage());
		}
		return yjsbWs;
	}

	/**
	 * 纳税人信息登录验证接口
	 * 
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param yzBwXml
	 * @param sing
	 * @return
	 */
	public static String yzNsrxx(String nsrsbh, String supplier, String ywlx, String yzBwXml, String sing) {
		try {
			String reStrYz = getWebService().yzNsrxx(nsrsbh, supplier, ywlx, yzBwXml, sing);
			return reStrYz;
		} catch (XFireRuntimeException e) {
			log.error("登录连接失败", e);
			throw new BusinessException("登录连接失败");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 查询本期应申报清册
	 * 
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param sbqcCxBwXml
	 * @param sing
	 * @param token
	 * @return
	 */
	public static String sbqcCx(String nsrsbh, String supplier, String ywlx, String sbqcCxBwXml, String sing,
			String token) {
		try {
			String reStrYz = getWebService().sbqcCx(nsrsbh, supplier, ywlx, sbqcCxBwXml, sing, token);
			return reStrYz;
		} catch (XFireRuntimeException e) {
			log.error("查询本期应申报清册连接失败", e);
			throw new BusinessException("查询本期应申报清册连接失败");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 申报报文上传接口
	 * 
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param yjsbBwXml
	 * @param sing
	 * @param token
	 * @return
	 */
	public static String yjsbBw(String nsrsbh, String supplier, String ywlx, String yjsbBwXml, String sing,
			String token) {
		try {
			String reStrYz = getWebService().yjsbBw(nsrsbh, supplier, ywlx, yjsbBwXml, sing, token);
			return reStrYz;
		} catch (XFireRuntimeException e) {
			log.error("申报上传连接失败", e);
			throw new BusinessException("申报上传连接失败");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 生成签名信息接口
	 * 
	 * @param privatekey
	 * @param sign
	 * @return
	 */
	public static String setSign(String privatekey, String sign) {
		try {
			String reStrYz = getWebService().setSign(privatekey, privatekey);
			return reStrYz;
		} catch (XFireRuntimeException e) {
			log.error("签名连接失败", e);
			throw new BusinessException("签名连接失败");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	// 验证登录
	public static HashMap<String, String> yzdlToSD(CorpVO corpvo, CorpTaxVo taxvo) throws DZFWarpException {
		String nsrsbh = corpvo.getVsoccrecode();
		String vstatetaxpwd = taxvo.getVstatetaxpwd();

		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号不能为空");
		}

		if (StringUtil.isEmpty(vstatetaxpwd)) {
			throw new BusinessException("纳税密码不能为空");
		}
		String ywlx = TaxConst.SERVICE_CODE_NSRDLYZ;

		// 加密内容，格式为 业务报文xml+ supplier
		String yzBwXml = XMLUtils.createYzdlXML(nsrsbh, vstatetaxpwd);

		yzBwXml = XMLUtils.createScBwXml(yzBwXml, ywlx, null, new DZFDate().toString(), nsrsbh, vstatetaxpwd, null,
				null);
		 log.info("税号：" + corpvo.getVsoccrecode() + "----yzBwXml----验证报文-:\n" + yzBwXml);

		// 私钥加密过程
		String supplier = TaxParamUtils.SUPPLIER;
		String cipher = sbbwEncode(yzBwXml, supplier);

		String reStrYz = WebServiceProxy.yzNsrxx(nsrsbh, supplier, ywlx, yzBwXml, cipher);

		 log.info("税号：" + corpvo.getVsoccrecode() + "验证返回结果" + reStrYz);
		HashMap<String, String> map = getReturyMap(reStrYz, "验证登录失败：");
		return map;
	}

	private static HashMap<String, String> getReturyMap(String reStrYz, String message) {

		if (StringUtil.isEmpty(reStrYz)) {
			dealMessage(message + "返回信息出错");
		}

		HashMap<String, String> map = ParseJsonData.getJsonData(reStrYz);
		if (map == null || map.size() == 0 || map.get(TaxConst.RETURN_ITEMKEY_NO) == null) {
			dealMessage(message + "解析报文失败");
		}

		if (!"00000000".equals(map.get(TaxConst.RETURN_RTNCODE))) {
			if (map.get(TaxConst.RETURN_MSG) != null) {
				dealMessage(message + map.get(TaxConst.RETURN_MSG));
			}
			if (map.get(TaxConst.RETURN_RTNMSG) != null) {
				dealMessage(message + map.get(TaxConst.RETURN_RTNMSG));
			}
		}
		if (!"0".equals(map.get(TaxConst.RETURN_ITEMKEY_NO))) {
			if (!StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_MESSAGE))) {
				if (map.get(TaxConst.RETURN_MSG) != null) {
					dealMessage(message + map.get(TaxConst.RETURN_MSG));
				} else {
					dealMessage(message + map.get(TaxConst.RETURN_ITEMKEY_MESSAGE));
				}
			} else if (!StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_OBJ))) {
				dealMessage(message + map.get(TaxConst.RETURN_ITEMKEY_OBJ));
			} else {
				dealMessage(message + "未知异常");
			}
		}

		if (map.get(TaxConst.RETURN_CODE) != null
				&& (!"0000".equals(map.get(TaxConst.RETURN_CODE)) && !"0".equals(map.get(TaxConst.RETURN_CODE)))) {
			if (map.get(TaxConst.RETURN_MSG) != null) {
				dealMessage(message + map.get(TaxConst.RETURN_MSG));
			} else {
				if (map.get(TaxConst.RETURN_REASON) != null) {
					dealMessage(message + map.get(TaxConst.RETURN_REASON));
				}
				if (map.get(TaxConst.RETURN_ITEMKEY_MESSAGE) != null) {
					dealMessage(message + map.get(TaxConst.RETURN_ITEMKEY_MESSAGE));
				} else {
					dealMessage(message + map.get(TaxConst.RETURN_ITEMKEY_RMESSAGE));
				}
			}
		}
		return map;
	}

	private static void dealMessage(String message) {
		log.info(message);
		throw new BusinessException(message);
	}

	// 上传报文
	public static HashMap<String, String> yjsbToSD(CorpVO corpvo, CorpTaxVo taxvo, HashMap<String, String> map,
												   String yjsbBwXml, TaxQcQueryVO qcvo, int type) {

		String nsrsbh = corpvo.getVsoccrecode();

		String vstatetaxpwd = taxvo.getVstatetaxpwd();

		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号不能为空");
		}

		if (StringUtil.isEmpty(vstatetaxpwd)) {
			throw new BusinessException("纳税密码不能为空");
		}

		if (StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_TOKEN))) {
			throw new BusinessException("返回的token不能为空");
		}

		if (StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_SESSIONID))) {
			throw new BusinessException("返回的sessionId不能为空");
		}

		if (qcvo == null || StringUtil.isEmpty(qcvo.getYwlx())) {
			throw new BusinessException("业务类型不能为空");
		}

		if (StringUtil.isEmpty(yjsbBwXml)) {
			throw new BusinessException("业务报文不能为空");
		}
		// yjsbBwXml = ParseXml.readTxtFile("src/zzs.xml");

		// if (qcvo == null || qcvo.getIsConQc() == null ||
		// !qcvo.getIsConQc().booleanValue()) {
		yjsbBwXml = XMLUtils.createScBwXml(yjsbBwXml, qcvo.getYwlx(), map.get(TaxConst.RETURN_ITEMKEY_SESSIONID),
				new DZFDate().toString(), nsrsbh, vstatetaxpwd, qcvo.getImpl(), qcvo.getDjxh());
		// } else {
		// yjsbBwXml = XMLUtils.createScBwXml(yjsbBwXml, qcvo.getYwlx(),
		// map.get(TaxConst.RETURN_ITEMKEY_SESSIONID),
		// new DZFDate().toString(), nsrsbh, vstatetaxpwd, null,qcvo.getDjxh());
		// }
		String msg1 ="----yzBwXml----上报上传报文-:\n";
		if(type ==1){
			msg1 = "----yzBwXml----获取期初数据上传报文-:\n";
		}else if(type ==2){
			msg1 = "----yzBwXml----获取申报状态上传报文-:\n";
		} 
		log.info("税号：" + corpvo.getVsoccrecode() + msg1 + yjsbBwXml);
//		 System.out.println(yjsbBwXml);
		String supplier = TaxParamUtils.SUPPLIER;
		String cipher = sbbwEncode(yjsbBwXml, supplier);
//		System.out.println(yjsbBwXml);
		String reStrYz = WebServiceProxy.yjsbBw(nsrsbh, supplier, qcvo.getYwlx(), yjsbBwXml, cipher,
				map.get(TaxConst.RETURN_ITEMKEY_TOKEN));

		log.info("税号：" + corpvo.getVsoccrecode() +"----yzBwXml----验证返回结果-:\n" + reStrYz);
		// System.out.println(reStrYz);
		String msg = "上报失败：";
		if(type ==1){
			msg = "获取期初数据失败：";
		}else if(type ==2){
			msg = "获取申报状态失败：";
		}
		
		
		HashMap<String, String> rtnmap = getReturyMap(reStrYz,msg);
		return rtnmap;
	}

	// 清册数据
	public static HashMap<String, String> sbqcCxToSD(CorpVO corpvo, CorpTaxVo taxvo, HashMap<String, String> map,
			String yjsbBwXml, TaxQcQueryVO qcvo) {

		String nsrsbh = corpvo.getVsoccrecode();

		String vstatetaxpwd = taxvo.getVstatetaxpwd();

		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号不能为空");
		}

		if (StringUtil.isEmpty(vstatetaxpwd)) {
			throw new BusinessException("纳税密码不能为空");
		}

		if (StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_TOKEN))) {
			throw new BusinessException("返回的token不能为空");
		}

		if (StringUtil.isEmpty(map.get(TaxConst.RETURN_ITEMKEY_SESSIONID))) {
			throw new BusinessException("返回的sessionId不能为空");
		}

		if (StringUtil.isEmpty(qcvo.getYwlx())) {
			throw new BusinessException("业务类型不能为空");
		}

		if (StringUtil.isEmpty(yjsbBwXml)) {
			throw new BusinessException("业务报文不能为空");
		}

		yjsbBwXml = XMLUtils.createScBwXml(yjsbBwXml, qcvo.getYwlx(), map.get(TaxConst.RETURN_ITEMKEY_SESSIONID),
				new DZFDate().toString(), nsrsbh, vstatetaxpwd, qcvo.getImpl(), qcvo.getDjxh());
		 log.info("税号：" + corpvo.getVsoccrecode() + "----yzBwXml----获取清册上传报文-:\n" + yjsbBwXml);

		String supplier = TaxParamUtils.SUPPLIER;
		String cipher = sbbwEncode(yjsbBwXml, supplier);

		String reStrYz = WebServiceProxy.sbqcCx(nsrsbh, supplier, qcvo.getYwlx(), yjsbBwXml, cipher,
				map.get(TaxConst.RETURN_ITEMKEY_TOKEN));

		 log.info("税号：" + corpvo.getVsoccrecode() + "----yzBwXml----验证返回结果-:\n" + reStrYz);
		HashMap<String, String> rtnmap = getReturyMap(reStrYz, "获取清册失败：");
		return rtnmap;
	}

	private static String sbbwEncode(String yjsbBwXml, String supplier) {
		// 私钥加密过程
		String sign = yjsbBwXml + supplier;
		String cipher = CreateSignUtils.getSign(sign, true);
		// log.info("加密后sign密码:\n" + cipher);
		return cipher;
	}

}