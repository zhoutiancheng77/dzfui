package com.dzf.zxkj.app.pub.map;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import com.dzf.zxkj.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * 百度地图客户端请求
 * @author zhangj
 *
 */
@Slf4j
public class BaiduMapClient {
	
	private HttpClient client;

	public BaiduMapClient() {
	}


	public static void main(String[] args) {
		
		BaiduMapClient client = new BaiduMapClient();
		
		String city  = client.getMapAddressCity("39.915,116.404");
		
		System.out.println(city);
	}
	
	public String getMapAddressCity(String address) {
		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpGet get = null;
		try {
			String url = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="+address+"&output=json&pois=1&ak=UGfOnfICVWZT4kKoQmBjA2XASoG24rYG";
			get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity responsetEntity = response.getEntity();
			String result = EntityUtils.toString(responsetEntity, "UTF-8");
			log.info("----------------定位地址-----------BEGIN");
			log.info(result);
			log.info("----------------定位地址-----------END");
			String city = getContent(result);
			log.info("城市:"+city);
			return city;
		} catch (Exception e) {
			if (get != null)
				get.abort();
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


	private String getContent(String result) {
		int startcount = result.indexOf("renderReverse&&renderReverse(");
		if(startcount <0){
			throw new BusinessException("解析失败,结果值:"+result);
		}
		String result_sub_temp  = result.replace("renderReverse&&renderReverse(", "");
		String result_sub = result_sub_temp.substring(0, result_sub_temp.length()-1);
		JsonParser parse = new JsonParser();
		JsonObject json= (JsonObject) parse.parse(result_sub);
		int status = json.get("status").getAsInt();
		if(status != 0){
			throw new BusinessException("请求结果失败,状态码:"+status);
		}
		String city = getCity(json);
		return city;
	}


	private String getCity(JsonObject json) {
		JsonObject resultobj= json.get("result").getAsJsonObject();
		if(resultobj!=null){
			JsonObject addressComponent = resultobj.get("addressComponent").getAsJsonObject();
			if(addressComponent!=null){
				String city = addressComponent.get("city").getAsString();
				return city;
			}
		}
		return null;
	}
	

}
