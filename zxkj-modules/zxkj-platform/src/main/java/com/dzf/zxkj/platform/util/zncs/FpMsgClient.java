package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


/**
 * 票通发票客户端请求
 * @author zhangj
 *
 */
@Slf4j
public class FpMsgClient {
	
	private HttpClient client;

	public FpMsgClient() {
	}

	private String requestListFp(String drcode, DZFDateTime requesttime) {
		String content = null;
		try {
			StringBuffer sf = new StringBuffer();
			sf.append("<REQUEST_FPCY>");
			sf.append("<TWODIMENSIONCODE>" + drcode + "</TWODIMENSIONCODE>");// 二维码数据
			sf.append("<REQUESTTIME>").append(requesttime.toString()).append("</REQUESTTIME>");// 请求时间
			sf.append("</REQUEST_FPCY>");
			byte[] jm = CommonXml.encrypt3DES(CommonXml.appSecret, sf.toString().getBytes());
			content = Base64CodeUtils.encode(jm);
		} catch (Exception e) {
			log.error("错误",e);
		}
		return content;
	}

	public static void main(String[] args) {
		
		FpMsgClient client = new FpMsgClient();
		
		String value = client.sendPostXml("01,51,011001600111,18198736,291.26,20160928,72726966242703889025,189C");
		
		System.out.println(value);
	}
	
	public String sendPostXml(String drcode) {
		if (client == null) {
			client = HttpClients.createDefault();
		}
//		String[] requestxmls = null;
		HttpPost post = null;
		try {
			//log.info("开始取票"+CommonXml.fpcyurl);
			post = new HttpPost(CommonXml.fpcyurl);
			String sp = requestListFp(drcode,new DZFDateTime());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("parameter",
					CommonXml.createxml(sp, "REQUEST_FPCY", "", "")));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = client.execute(post);
			HttpEntity responsetEntity = response.getEntity();
			String result = EntityUtils.toString(responsetEntity, "UTF-8");
			//log.info("----------------请求发票列表-----------BEGIN");
			//log.info("取票结果"+result);
			//log.info("----------------请求发票列表-----------END");
			return result;
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
	}
	

}
