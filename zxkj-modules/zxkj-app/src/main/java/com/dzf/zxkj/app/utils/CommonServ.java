package com.dzf.zxkj.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dzf.zxkj.app.model.log.AppOptLogVO;
import com.dzf.zxkj.app.model.resp.bean.RequestBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.JSONProcessor;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.serializer.SerializerFeature;

import sun.misc.BASE64Decoder;

public class CommonServ {

	public static String publicKeyStr1 = "";
	private static int validsendcon = 10;

	public static BaseTimerCache<String, String> user_identify = new BaseTimerCache<String, String>();
	static {
	}

	/**
	 * 将APP发送参数列表转换为javabean
	 * 
	 * @author liangjy
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 * @throws InstantiationException
	 * @throws Exception
	 */
	public static Object parseStr2Bean(String str, Class obj) throws Exception {

		Object cls = obj.newInstance();
		String[] items = str.split("&");
		Method[] methods = obj.getMethods();
		for (String item : items) {
			String[] fieldVl = item.split("=");
			if (fieldVl.length == 2) {
				for (Method method : methods) {
					String methodNm = method.getName();
					if (methodNm.startsWith("set")) {
						String fieldNm = methodNm.substring(methodNm.indexOf("set") + 3, methodNm.length());
						if (fieldNm.toLowerCase().equals(fieldVl[0].toString())) {
							method.invoke(cls, fieldVl[1]);
						}
					}

				}
			}
		}
		return cls;
	}

	/**
	 * 从请求体中读取客户端发送的JSON串
	 * 
	 * @param request
	 *            请求对象
	 * @return String 类型，接收到的JSON串
	 */
	public static String readArgFromRequestBody(HttpServletRequest req) {
		StringBuffer jsonBuf = new StringBuffer();
		char[] buf = new char[2048];
		int len = -1;
		try {
			BufferedReader reader = req.getReader();
			while ((len = reader.read(buf)) != -1) {
				jsonBuf.append(new String(buf, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonBuf.toString();
	}

	public static String getOperate(String arg) {
		return null;
	}

	/**
	 * @param userBean
	 * @throws Exception
	 */
	public static void encodeUserBn(UserBeanVO userBean) throws Exception {

		if (Integer.parseInt(userBean.getSystype()) == IConstant.ONE) {
			BASE64Decoder bd = new BASE64Decoder();
			if (userBean.getCorpname() != null) {
				userBean.setCorpname(enCodeByteToStr(userBean.getCorpname(), bd));
			}
			if (userBean.getAccount() != null) {
				userBean.setAccount(enCodeByteToStr(userBean.getAccount(), bd));
			}
			if (userBean.getUsername() != null) {
				userBean.setUsername(enCodeByteToStr(userBean.getUsername(), bd));
			}
		}
	}

	public static String enCodeByteToStr(String value, BASE64Decoder bd) throws Exception {
		byte[] bs = bd.decodeBuffer(URLDecoder.decode(value));
		return new String(new String(bs, "UTF-8").getBytes(), "gb2312");
	}

	public static void initUser(RequestBaseBeanVO userBean) {
	}

	// 返回管理员手机号
	public static String getExistCorp(String corpName) throws DZFWarpException {
		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, IGlobalConstants.DefaultGroup));
		String qryCorp = "select phone2 from bd_corp where unitname=? union select tel from app_temp_corp where corpname = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpName);
		sp.addParam(corpName);
		String phone = (String) sbo.executeQuery(qryCorp, sp, new ColumnProcessor());
		if (phone != null && phone.length() > 0) {
			return phone;
		} else {
			return null;
		}
	}

	/**
	 * 重新调整验证吗
	 * 
	 * @param userBean
	 * @param checkFlag
	 *            生成/校验使用标识
	 * @return 使用校验时，如果不一致则提示
	 */
	public static String resetUserIdentify(UserBeanVO userBean, String newIdentify, boolean checkFlag) {
		if (checkFlag) {
			if (user_identify.containsKey(userBean.getPhone())) {
				String exitIdentifyStr = user_identify.getValue(userBean.getPhone());
				int firstIdx = exitIdentifyStr.indexOf("@");
				int lastIdx = exitIdentifyStr.lastIndexOf("@");
				// 使用(注册)，验证码及有效期校验
				String identify = exitIdentifyStr.substring(0, firstIdx);
				if (!identify.equals(userBean.getIdentify())) {
					return "验证码不正确";
				}
				long validtime = validsendcon * 60000;
				if (System.currentTimeMillis() - Long
						.parseLong(exitIdentifyStr.substring(lastIdx + 1, exitIdentifyStr.length())) > validtime) {
					user_identify.remove(userBean.getPhone());
					return "验证码过期";
				}
				// 校验使用都成功，删掉
				user_identify.remove(userBean.getPhone());
			} else {
				return "验证码无效";
			}
		} else {
			// 注册和忘记密码生成新的验证码，需要把之前的删掉
			user_identify.put(userBean.getPhone(),
					newIdentify + "@" + userBean.getOperate() + "@" + System.currentTimeMillis());
		}

		return "";
	}

	public static boolean isnull(Object obj) {
		if (obj == null || "".equals(obj))
			return true;
		return false;
	}

	public synchronized static void log(String accout, String optcontent, String msg) {
		AppOptLogVO logVO = new AppOptLogVO();
		logVO.setAccout(accout);
		logVO.setOptcontent(optcontent);
		logVO.setMsg(msg);

	}

}
