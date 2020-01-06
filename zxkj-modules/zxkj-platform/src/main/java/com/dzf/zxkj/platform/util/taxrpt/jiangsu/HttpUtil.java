package com.dzf.zxkj.platform.util.taxrpt.jiangsu;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
@Slf4j
public class HttpUtil {
//	private static Logger log = Logger.getLogger(HttpUtil.class);

	/**
	 * pose方式请求
	 * 
	 * @param url
	 * @return {statusCode : "请求结果状态代码", responseString : "请求结果响应字符串"}
	 */
	public static Map post(String url, Map<String, String> params) {
		log.info("江苏纳税申报request：" + params.get("request"));
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = postForm(url, params);
		// post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "UTF-8");
		// post.addHeader("Content-Type","text/html;charset=UTF-8");
		// post.setHeader("Content-Type","text/html;charset=UTF-8");
		Map reponseMap = invoke(httpclient, post);
		httpclient.getConnectionManager().shutdown();

		return reponseMap;
	}

	public static Map get(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet post = new HttpGet(url);
		// post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "UTF-8");
		// post.addHeader("Content-Type","text/html;charset=UTF-8");
		// post.setHeader("Content-Type","text/html;charset=UTF-8");
		Map reponseMap = invoke(httpclient, post);
		httpclient.getConnectionManager().shutdown();

		return reponseMap;
	}

	public static Map postJson(String url, String json) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		StringEntity s;
		Map reponseMap = null;
		s = new StringEntity(json.toString(), "utf-8");
		s.setContentEncoding("UTF-8");
		s.setContentType("application/json; charset=utf-8");
		HttpPost post = new HttpPost(url);
		reponseMap = invoke(httpclient, post);
		httpclient.getConnectionManager().shutdown();

		return reponseMap;

	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		}

		return httpost;
	}

	private static Map invoke(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {

		Map returnMap = new HashMap();

		HttpResponse response = sendRequest(httpclient, httpost);
		// System.out.println("return code:"+response.getStatusLine().getStatusCode());
		String body = paseResponse(response);
		// System.out.println(body);

		returnMap.put("statusCode", response.getStatusLine().getStatusCode()); // 请求返回结果状态
		returnMap.put("response", body);
		return returnMap;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {

		HttpResponse response = null;
		try {
			// httpost.setHeader(new BasicHeader("Accept",
			// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
			// httpost.setHeader(new BasicHeader("Accept-Encoding",
			// "gzip, deflate, sdch"));
			// httpost.setHeader(new BasicHeader("Accept-Language",
			// "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4"));
			// httpost.setHeader(new BasicHeader("Cookie",
			// "gwdshare_firstime=1489107643071; JSESSIONID1=Mz7Lr_ABVzLwt7MtlVT98wD9GVJyNUB_abmjmy3j6kUO4Cw5dyya!262353211; SSOSESSIONID=Xy3TYHZVmn2jjSrpRDk2PWmZvpQ9hx7JJFbmFCnpDy6WWLhd9BNF!341600644; JSESSIONID=tyzLsAgapszalXSkTNZqaM5p7ct2z1Kv840j0RVEA01ybvVbAnCM!-2024187659; _gscs_420267342=89475983q4lmqb48|pv:3; _gscbrs_420267342=1; cookie=36427284; _gscu_420267342=891076426ekv5h16"));
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return response;
	}

	private static String paseResponse(HttpResponse response) {

		HttpEntity entity = response.getEntity();
		String charset = EntityUtils.getContentCharSet(entity);

		String body = null;
		try {
			body = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}

		return body;
	}

	/**
	 * 解析返回结果
	 * @param response
	 * @return
	 */
	public static String parseRes(String response) {
		log.info("江苏纳税申报response:" + response);
		String result = null;
		String res = ZipUtil.unzipDecode(response, true);
		try {
			Document doc = DocumentHelper.parseText(res);
			Element ebody = doc.getRootElement().element("BODY");
			// 请求响应状态代码
			Element RESULTCODE = doc.getRootElement().element("HEAD")
					.element("RESULT").element("RESULTCODE");
			// 请求响应状态描述
			// Element RESULTMSG = doc.getRootElement().element("HEAD")
			// .element("RESULT").element("RESULTMSG");
			// String message = RESULTMSG.getText();
			String rcode = RESULTCODE.getText();
			if ("0000".equals(rcode)) {
				return decodeBase64(ebody.getText());
			} else {
				log.error("调用申报接口失败 :" + res);
				return "{\"RESULT\": \"9999\",\"MSG\": \"调用申报接口失败\"}";
			}
		} catch (DocumentException e) {
			log.error(e.getMessage(),e);
		}

		return result;

	}

	/** base64解码 */
	private static String decodeBase64(String str) {
		BASE64Decoder base64 = new BASE64Decoder();
		String decode = null;
		try {
			decode = new String(base64.decodeBuffer(str), "utf-8");
		} catch (Exception e) {

		}
		return decode;
	}

}
