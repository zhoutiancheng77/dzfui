package com.dzf.zxkj.platform.util.zncs;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * ����HttpClient����post����Ĺ�����
 */
@Slf4j
public class HttpClientUtil {

	
	private HttpClient httpClient = null;
	private HttpClient getHttpClient()
	{
		if (httpClient == null)
		{
			try {
				httpClient = new SSLClient();
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
				
			} catch (Exception ex) {
				log.error("错误",ex);
			}
		}
		return httpClient;
	}
	public String doPost(String url, Map<String, String> map, String charset) {

		HttpPost httpPost = null;
		String result = "";
		try {

			httpPost = new HttpPost(url);

			// ���ò���
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,
						charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = getHttpClient().execute(httpPost);
			if (response != null) {
				if (response.getAllHeaders() != null && response.getAllHeaders().length > 0)
				{
					for (Header header : response.getAllHeaders())
					{
						result += header.getName() + " : " + header.getValue() + "\n";
					}
				}
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result += "\n---------Entity---------\n\n";
					result += EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			log.error("错误",ex);
		}
		return result;
	}
	public String doPostEntity(String url, Map<String, String> map, String charset) throws Exception {

		HttpPost httpPost = null;
		String result = null;


		httpPost = new HttpPost(url);

		// ���ò���
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> elem = (Entry<String, String>) iterator
					.next();
			list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		if (list.size() > 0) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,
					charset);
			httpPost.setEntity(entity);
		}
		HttpResponse response = getHttpClient().execute(httpPost);
		if (response != null) {
			
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {

				result = EntityUtils.toString(resEntity, charset);
			}
		}
	
		return result;
	}
	public String doGet(String url, String charset) {
		
		HttpGet httpGet = null;
		String result = "";
		try {
			
			
			httpGet = new HttpGet(url);

			// ���ò���
			
			HttpResponse response = getHttpClient().execute(httpGet);
			
			if (response != null) {
				if (response.getAllHeaders() != null && response.getAllHeaders().length > 0)
				{
					for (Header header : response.getAllHeaders())
					{
						result += header.getName() + " : " + header.getValue() + "\n";
					}
				}
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result += "\n---------Entity---------\n\n";
					result += EntityUtils.toString(resEntity, charset);
				}
			}

		} catch (Exception ex) {
			log.error("错误",ex);
		}
		return result;
	}
	public String doGetEntity(String url, String charset) {
		
		HttpGet httpGet = null;
		String result = "";
		try {
			
			
			httpGet = new HttpGet(url);

			// ���ò���
			
			HttpResponse response = getHttpClient().execute(httpGet);
			
			if (response != null) {
				
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {

					result = EntityUtils.toString(resEntity, charset);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}


}
