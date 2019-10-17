package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.tax.Taxclassifybodyinfo;
import com.dzf.zxkj.platform.model.tax.Taxclassifyheadinfo;
import io.netty.channel.ConnectTimeoutException;
import jersey.repackaged.com.google.common.collect.Lists;
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

import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 税收分类
 *
 */
@Slf4j
public class TaxClassifyGetValue {
	
	private HttpClient client;
	
	private String reqsys;
	
	private String authkey;
	
	private String taxclassifyurl;
	
	private String unspscname;

	public TaxClassifyGetValue(String unspscname){
		init(unspscname);
	}
	
	private void init(String unspscname){
		ResourceBundle bundle= PropertyResourceBundle.getBundle("taxclassify");
		reqsys = bundle.getString("reqsys");
		authkey = bundle.getString("authkey");
		taxclassifyurl = bundle.getString("taxclassifyurl");
		this.unspscname = unspscname;
	}
	
	public String sendPostData(){
		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpPost post = null;
		String result = null;
		try{
			post = new HttpPost(getVisitURL());
			List<NameValuePair> params = Lists.newArrayList();
	        params.add(new BasicNameValuePair("head", serializeData(buildHeadVO())));
	        params.add(new BasicNameValuePair("body", serializeData(buildBodyVO())));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity responsetEntity = response.getEntity();
			result = EntityUtils.toString(responsetEntity, "UTF-8");
		}catch(Exception e){
			if(post!=null)
				post.abort();
            if(SocketTimeoutException.class.isInstance(e)){
            	log.error("Http请求响应超时",e);
                throw new RuntimeException("Http请求响应超时",e);
            }else if(ConnectTimeoutException.class.isInstance(e)){
            	log.error("Http请求连接超时", e);
                throw new RuntimeException("Http请求连接超时", e);
            }else if(ConnectException.class.isInstance(e)){
            	log.error("Http请求异常", e);
                throw new RuntimeException("Http请求异常", e);
            }else{
            	log.error("其他异常", e);
                throw new RuntimeException("其他异常", e);
            }
		}
		return result;
	}
	
	private String getVisitURL(){
		if(StringUtil.isEmpty(taxclassifyurl)){
			taxclassifyurl="https://kjbm.dazhangfang.com";
		}
		if(taxclassifyurl.endsWith("/")){
			taxclassifyurl = taxclassifyurl.substring(0, taxclassifyurl.length()-1);
		}
		taxclassifyurl = taxclassifyurl+"/commodity/commodityinfo!queryByUnspscName.action";
		return taxclassifyurl;
	}
	
	private String serializeData(Serializable zable){
		FastjsonFilter filter = new FastjsonFilter();
		String jsonString = JsonUtils.serialize(zable);
		return jsonString;
	}
	
	private Taxclassifyheadinfo buildHeadVO() {
		Taxclassifyheadinfo head = new Taxclassifyheadinfo();
		head.setReqsys(reqsys);
		head.setReqtime(getTimeNow());
		head.setReqno(String.valueOf(System.currentTimeMillis()));
		head.setAuthkey(authkey);
		//此句放到最后
		encodeAuthorkey(head);
		return head;
	}
	
	private void encodeAuthorkey(Taxclassifyheadinfo head){
		String time = head.getReqtime();
		String sys = head.getReqsys();
		String no = head.getReqno();
		String unionkey = time+sys+no;
		String jm64 = "";
		try {
			jm64 = Base64CodeUtils.encode(unionkey.getBytes());
		} catch (Exception e) {
			log.error("税收分类项目接口，base64加密失败！", e);
		}
		String authkey = head.getAuthkey();
		String unionkey1 = authkey+jm64;
		String md516 = mD5ECode(unionkey1);
		head.setReqauthkey(md516);
	}
	
	private String mD5ECode(String text)  {
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
//			e.printStackTrace();
		}
		return buf.toString();
	}
	
	private String getTimeNow(){
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = dateFormater.format(new Date());
		return date;
	}
	
	private Taxclassifybodyinfo buildBodyVO(){
		Taxclassifybodyinfo body = new Taxclassifybodyinfo();
		body.setUnspscname(unspscname);
		return body;
	}
}