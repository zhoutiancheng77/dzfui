package com.dzf.zxkj.app.service.sms;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.app.config.AppConfig;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.sys.SMSBVO;
import com.dzf.zxkj.platform.model.sys.SMSHVO;
import com.dzf.zxkj.platform.model.sys.SMSResVO;
import com.google.common.collect.Lists;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class SMSServiceNew {

	private HttpClient client;

	private String reqsys;

	private String ipaddr;

	private String authkey;

	private String url;

	private String[] phone;

	private String templatecode;

	private Map<String, String> params;

	private String dxqm;

	public static String appid = null;//
	public static String appkey = null;//
	public static String smsurl = null; //
	static {
		AppConfig appConfig = (AppConfig) SpringUtils.getBean(AppConfig.class);
		// / 加载属性列表
		appid = appConfig.appid;
		appkey = appConfig.appkey;
		smsurl = appConfig.smsurl;
	}

	public SMSServiceNew(SMSBVO smsVO) {
		init(smsVO);
	}

	private void init(SMSBVO smsVO) {
		reqsys = appid;
		authkey = appkey;
		url = smsurl;
		this.params = smsVO.getParams();
		this.phone = smsVO.getPhone();
		this.templatecode = smsVO.getTemplatecode();
		this.dxqm = smsVO.getDxqm();
		this.ipaddr = smsVO.getSmsip();
	}

	public SMSResVO sendPostData() {

		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpPost post = null;
		SMSResVO headvo = null;
		try {
			post = new HttpPost(url);
			List<NameValuePair> params = Lists.newArrayList();
			params.add(new BasicNameValuePair("head", JSON.toJSONString(buildHeadVO())));
			params.add(new BasicNameValuePair("body", JSON.toJSONString(buildBodyVO())));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity responsetEntity = response.getEntity();
			String result = EntityUtils.toString(responsetEntity, "UTF-8");
			headvo = JsonUtils.deserialize(result, SMSResVO.class);
//			JSON headjs = (JSON) JSON.parse(result);
//			Map<String, String> headmaping = FieldMapping.getFieldMapping(new SMSResVO());
//			headvo = DzfTypeUtils.cast(headjs,headmaping, SMSResVO.class, JSONConvtoJAVA.getParserConfig());
		} catch (Exception e) {
			if (post != null)
				post.abort();
			if (SocketTimeoutException.class.isInstance(e)) {
				log.error("Http请求响应超时", e);
				throw new RuntimeException("Http请求响应超时", e);
			} else if (ConnectTimeoutException.class.isInstance(e)) {
				log.error("Http请求连接超时", e);
				throw new RuntimeException("Http请求连接超时", e);
			} else if (ConnectException.class.isInstance(e)) {
				log.error("Http请求异常", e);
				throw new RuntimeException("Http请求异常", e);
			} else {
				log.error("其他异常", e);
				throw new RuntimeException("其他异常", e);
			}
		}
		return headvo;

	}

	private SMSHVO buildHeadVO() {
		SMSHVO head = new SMSHVO();
		head.setReqsys(reqsys);
		head.setReqtime(getTimeNow());
		head.setReqno(reqsys + "_" + String.valueOf(System.currentTimeMillis()));
		head.setAuthkey(authkey);
		encodeAuthorkey(head);
		return head;
	}

	private void encodeAuthorkey(SMSHVO head) {
		String time = head.getReqtime();
		String sys = head.getReqsys();
		String no = head.getReqno();
		String unionkey = time + sys + no;
		String jm64 = "";
		try {
			jm64 = Base64CodeUtils.encode(unionkey.getBytes());
		} catch (Exception e) {
			log.error("短信通道接口，base64加密失败！", e);
		}
		String authkey = head.getAuthkey();
		String unionkey1 = authkey + jm64;
		String md516 = mD5ECode(unionkey1);
		head.setReqauthkey(md516);
	}

	private String getTimeNow() {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = dateFormater.format(new Date());
		return date;
	}

	private SMSBVO buildBodyVO() {
		SMSBVO body = new SMSBVO();
		body.setPhone(phone);
		body.setAppid(reqsys);
		body.setTemplatecode(templatecode);
		body.setDxqm(dxqm);
		body.setParams(params);
		body.setSmsip(ipaddr);
		return body;
	}

	private String mD5ECode(String text) {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
}
