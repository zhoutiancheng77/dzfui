package com.dzf.zxkj.platform.util.zncs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

public class RemoteClient {

	private static HttpClient client;
	
	private static Logger logger = Logger.getLogger(RemoteClient.class);
	
	public static String sendGetData(String url){
		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpGet get = null;
		String result = null;
		try{
			get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity responsetEntity = response.getEntity();
			result = EntityUtils.toString(responsetEntity, "UTF-8");
		}catch(Exception e){
			if(get!=null)
				get.abort();
            if(SocketTimeoutException.class.isInstance(e)){
            	logger.error("Http请求响应超时",e);
                throw new RuntimeException("Http请求响应超时",e);
            }else if(ConnectTimeoutException.class.isInstance(e)){
            	logger.error("Http请求连接超时", e);
                throw new RuntimeException("Http请求连接超时", e);
            }else if(ConnectException.class.isInstance(e)){
            	logger.error("Http请求异常", e);
                throw new RuntimeException("Http请求异常", e);
            }else{
            	logger.error("其他异常", e);
                throw new RuntimeException("其他异常", e);
            }
		}
		return result;
	}
	
	private static String createStringJson(List<NameValuePair> params){
		if(params == null || params.size() == 0)
			return "";
		String json = "{";
		for(int i = 0 ;i<params.size();i++){
			if(i == params.size()-1){
				json = json + "\""+params.get(i).getName()+"\":"+params.get(i).getValue();
			}else{
				json = json + "\""+params.get(i).getName()+"\":"+params.get(i).getValue()+",";
			}
		}
		json = json+"}";
		return json;
	}
	
	public static String sendPostData(String url,List<NameValuePair> params){
		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpPost post = null;
		String result = null;
		try{
			post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json; charset=UTF-8");
			if(params!=null && params.size()>0){
				StringEntity entity = new StringEntity(createStringJson(params),"utf-8");
				post.setEntity(entity);
			}
			HttpResponse response = client.execute(post);
			HttpEntity responsetEntity = response.getEntity();
			result = EntityUtils.toString(responsetEntity, "UTF-8");
		}catch(Exception e){
			if(post!=null)
				post.abort();
            if(SocketTimeoutException.class.isInstance(e)){
            	logger.error("Http请求响应超时",e);
                throw new RuntimeException("Http请求响应超时",e);
            }else if(ConnectTimeoutException.class.isInstance(e)){
            	logger.error("Http请求连接超时", e);
                throw new RuntimeException("Http请求连接超时", e);
            }else if(ConnectException.class.isInstance(e)){
            	logger.error("Http请求异常", e);
                throw new RuntimeException("Http请求异常", e);
            }else{
            	logger.error("其他异常", e);
                throw new RuntimeException("其他异常", e);
            }
		}
		return result;
	}	
}