package com.dzf.zxkj.app.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.dzf.zxkj.app.model.app.corp.ScanCorpConst;
import com.dzf.zxkj.app.model.app.corp.ScanCorpInfoVO;
import com.dzf.zxkj.app.service.IScanCorpInfo;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IDomainCont;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

@Service("scanCorpInfoImpl")
public class ScanCorpInfoImpl implements IScanCorpInfo {
    
    private final static String cookie_rkey = "cookie_rkey";

    @Override
    public ScanCorpInfoVO scanPermit(String url) {
        if(url.startsWith(IDomainCont.DOMAIN_BJ)){
            return scanDataBj(url);
        }else if(url.startsWith(IDomainCont.DOMAIN_JS)){
            return scanDataJs(url);
        }
        return null;
    }
    
    /**
     * 获取北京二维码信息
     * @param url
     * @return
     */
    private ScanCorpInfoVO scanDataBj(String url){
        String html=pickData(url);
        return analyzeHTMLByStringBj(html);
    }
    
    /**
     * 获取江苏二维码信息
     * @param url
     * @return
     */
    private ScanCorpInfoVO scanDataJs(String url){
//        url = "http://www.jsgsj.gov.cn:58888/ecipplatform/publicInfoQueryServlet.json?pageView=true&org=E48C279BFF9A488A7FACD9FBFC052C93&id=9F5326E81AEA80880E2DE969AB555388&seqId=6FA3613B2B8FBE49F5141322E03E0DC0&abnormal=&activeTabId=&tmp=33";
//        url = "http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?pt&c=75B161B67BE862B10EBEAC100CFC8F817AFDBCB15E31C82C1BD94FCAA6740C7990795003CA40054E554B33BD3577423643656B58FDACD5C24A6E4436E46BBAA1";
        try {
        	String redictURL = getRedirectUrl(url);
			StringBuffer requrl = new StringBuffer();
	        requrl.append(redictURL.substring(0, redictURL.indexOf("jiangsu.jsp?")));
	        requrl.append("publicInfoQueryServlet.json?pageView=true&");
	        requrl.append(redictURL.substring(redictURL.indexOf("jsp?")+4));
	        String json = pickData(requrl.toString());

	        return analyzeJsonJs(json);
		} catch (Exception e) {
			throw new BusinessException("二维码扫描过于频繁，请稍后再扫描  ");
		}
    }
    
    /**
     * 爬取网页信息
     */
    private static String pickData(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            if(url.startsWith(IDomainCont.DOMAIN_BJ)){
                httpget.addHeader( "User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
            }else if(url.startsWith(IDomainCont.DOMAIN_COM)){
                httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                httpget.setHeader("Accept-Encoding", "gzip, deflate");
                httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
                httpget.setHeader("Cache-Control", "max-age=0");
                httpget.setHeader("Connection", "keep-alive");
                //httpget.setHeader("Cookie", "__jsluid=01374d9d1000eb2a2d3d34b2473d091a; __jsl_clearance=1526429734.933|0|g2CgxvN8y2%2FxymdKnrDpdJ5nVLs%3D;");
                httpget.setHeader("Cookie", getCookie(url));// getCookie(url)//getRedisCookie(url)

                httpget.setHeader("Host", "www.gsxt.gov.cn");
                httpget.setHeader("Upgrade-Insecure-Requests", "1");
                httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
            }else{
                throw new  BusinessException("输入的URL地址不正确，请检查");
            }
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity,"utf-8");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 使用jsoup解析网页信息--北京
     * @author gejw
     * @time 上午9:51:08
     * @param html
     */
    private ScanCorpInfoVO analyzeHTMLByStringBj(String html){
        Document document = Jsoup.parse(html);
        Elements lists = document.getElementsByTag("dd");
        for (int i =0;i<lists.size();i++) {
            Element ele = lists.get(i);
            if(ele.hasAttr("style")){
                lists.remove(i);
            }
        }
        String text = null;
        ScanCorpInfoVO civo = new ScanCorpInfoVO();
        for(int i=0;i < lists.size();i++){
            Element ele = lists.get(i);
            text = ele.text();
            if(i == 0){
                civo.setVsoccrecode(text);
            }else if(i == 1){
                civo.setUnitname(text);
            }else if(i == 2){//公司类型
            	civo.setVcompanytypenm(text);//公司类型
            }else if(i == 3){
                civo.setLegalbodycode(text);
            }else if(i == 4){
                civo.setDef9(text);
            }else if(i == 5){
                civo.setDestablishdate(new DZFDate(text));
            }else if(i == 6){
                civo.setSaleaddr(text);
            }else if(i == 7){//营业期限自
                
            }else if(i == 8){//营业期限至
                
            }else if(i == 9){//经营范围
                civo.setVbusinescope(text);
            }else if(i == 10){//登记机关
                civo.setVregistorgans(text);
            }else if(i == 11){//核准日期
                civo.setDapprovaldate(new DZFDate(text));;
            }
        }
        if(!StringUtil.isEmpty(civo.getVcompanytypenm())){//设置该公司类型
        	SetCorpType(civo);
        }
        return civo;
    }
    
    /**
     * 使用jsoup解析网页信息--北京
     * @author gejw
     * @time 上午9:51:08
     * @param html
     */
    private ScanCorpInfoVO analyzeJsonJs(String jsonStr){
        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
        ScanCorpInfoVO civo = new ScanCorpInfoVO();
        civo.setVsoccrecode(jsonObj.getString("REG_NO"));//统一社会信息代码
        civo.setUnitname(jsonObj.getString("CORP_NAME"));//公司名称
        civo.setLegalbodycode(jsonObj.getString("OPER_MAN_NAME"));//法人
        civo.setVbusinescope(jsonObj.getString("FARE_SCOPE"));//经营范围
        civo.setVregistorgans(jsonObj.getString("BELONG_ORG"));//登记机关
        civo.setDapprovaldate(new DZFDate(jsonObj.getString("CHECK_DATE").replace("月", "-").replace("年", "-").replace("日", "")));//核准日期
        civo.setSaleaddr(jsonObj.getString("ADDR"));//住所
        civo.setDef9(jsonObj.getString("REG_CAPI"));//注册资金
        civo.setDestablishdate(new DZFDate(jsonObj.getString("START_DATE1")));//成立日期
        civo.setVcompanytypenm(jsonObj.getString("ZJ_ECON_KIND"));//公司类型

        if(!StringUtil.isEmpty(civo.getVcompanytypenm())){//设置该公司类型
        	SetCorpType(civo);
        }
        return civo;
    }

    /**
     * 获取公司类型
     *  1 有限责任公司  
		2  个人独资企业  
		3  有限合伙企业  
		4  有限责任公司(自然人独资)  
		5  有限责任公司(国内合资)  
		6  自然人有限责任公司   
		7  有限责任公司(自然人投资或控股)  
		8  有限责任公司(外商投资企业法人独资)  
		9  有限责任公司(台港澳法人独资)  
		10  有限责任公司(中外合资)   
		11  有限责任公司(国有独资)   
		12  有限责任公司(非自然人投资或控股的法人独资)  
		13  其他股份有限公司(非上市)   
		14  其他股份有限公司分公司(非上市)  
		15  一人有限责任公司  
		16  其他有限责任公司  
		17  其他有限责任公司(分公司)  
		18  全民所有制 
//		19其他
     * @return
     * @throws DZFWarpException
     */
    private Map<String,Integer> getCorpType() {
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	map.put("有限责任公司", 1);
    	map.put("个人独资企业", 2);
    	map.put("有限合伙企业", 3);
    	map.put("有限责任公司(自然人独资)", 4);
    	map.put("有限责任公司(国内合资)", 5);
    	map.put("自然人有限责任公司", 6);
    	map.put("有限责任公司(自然人投资或控股)", 7);
    	map.put("有限责任公司(外商投资企业法人独资)", 8);
    	map.put("有限责任公司(台港澳法人独资)", 9);
    	map.put("有限责任公司(中外合资)", 10);
    	map.put("有限责任公司(国有独资)", 11);
    	map.put("有限责任公司(非自然人投资或控股的法人独资)", 12);
    	map.put("其他股份有限公司(非上市)", 13);
    	map.put("其他股份有限公司分公司(非上市)", 14);
    	map.put("一人有限责任公司", 15);
    	map.put("其他有限责任公司", 16);
    	map.put("其他有限责任公司(分公司)", 17);
    	map.put("全民所有制", 18);
//    	map.put("其他", 19);
    	return map;
    }
    
    /**
     * 设置公司类型
     * @param civo
     */
    private void SetCorpType(ScanCorpInfoVO civo){
        Map<String,Integer> typemap = getCorpType();
    	if(typemap != null && !typemap.isEmpty()){
    		Integer corptype = typemap.get(civo.getVcompanytypenm());
    		if(corptype != null){
    			civo.setIcompanytype(corptype);
    		}else{
    			civo.setVcompanytypenm("其他");
    			civo.setIcompanytype(19);
    		}
    	}
    }
    
    /** 
     * 获取重定向地址 
     * @param path 
     * @return 
     * @throws Exception 
     */  
	private String getRedirectUrl(String path) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setConnectTimeout(5000);
		return conn.getHeaderField("Location");
	}

    @Override
    public ScanCorpInfoVO pickEnterpriseData(String url) {
        if(StringUtil.isEmpty(url)){
            return null;
        }
        return scanDataCommon(url);
    } 
    
    private ScanCorpInfoVO scanDataCommon(String url){
        String html=pickData(url);
        if(StringUtil.isEmpty(html)){
            return null;
        }
        return analyzeHTMLByStringCom(html);
    }
    
    /**
     * 使用jsoup解析网页信息--全国统一
     * @author gejw
     * @time 上午9:51:08
     * @param html
     */
    private ScanCorpInfoVO analyzeHTMLByStringCom(String html){
        Document document = Jsoup.parse(html);
        
        Element elem = document.getElementById("primaryInfo");
        if(elem == null){
            throw new BusinessException(document.getElementsByClass("prom").text());
        }
        Elements listss = elem.getElementsByTag("dl");
//        Elements lists = elem.getElementsByTag("dd"); 
        String name = null;
        String text = null;
        ScanCorpInfoVO civo = new ScanCorpInfoVO();
        int key = 100;
        for(Element ele : listss){
            name = ele.child(0).text();
            name = name.substring(0, name.length()-1);
            text = ele.child(1).text();
            key = ScanCorpConst.corpInfoMap.get(name) == null ? 100 : ScanCorpConst.corpInfoMap.get(name);
            if(key == 0){//统一社会信用代码
                civo.setVsoccrecode(text);
            }else if(key == 1){//企业名称
                civo.setUnitname(text);
            }else if(key == 2){//公司类型
                civo.setVcompanytypenm(text);//公司类型
            }else if(key == 3){//法定代表人
                civo.setLegalbodycode(text);
            }else if(key == 4){//注册资本
                civo.setDef9(text);
            }else if(key == 5){//成立日期
                civo.setDestablishdate(convetDate(text));
            }else if(key == 6){//营业期限自
            }else if(key == 7){//营业期限至
            }else if(key == 8){//登记机关
                civo.setVregistorgans(text);
            }else if(key == 9){//核准日期
                civo.setDapprovaldate(convetDate(text));
            }else if(key == 10){//登记状态
            }else if(key == 11){//住所
                civo.setSaleaddr(text);;
            }else if(key == 12){//经营范围
                civo.setVbusinescope(text);;
            }
        }
        return civo;
    }
    
    /**
     * 2017年07月14日 >>2017-07-14
     * @author gejw
     * @time 上午11:14:47
     * @param strDate
     * @return
     */
    private DZFDate convetDate(String strDate){
        DateFormat df7 = new SimpleDateFormat("yyyy年M月d日"); 
        try {
            Date date7 = df7.parse(strDate);
            return new DZFDate(date7);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    private static String getCookie(String url) {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("dzfadmin");
        String phantomjs = bundle.getString("phantomjs");
        String phrenderjs = bundle.getString("phrenderjs");
        String cookie = "";
//        System.out.println(cookie);
        String cmdString = phantomjs + " " + phrenderjs +"/js/render.js " + url;
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(cmdString);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            Map<String,String> cookieMap = new TreeMap<String,String>();
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                if(line.indexOf("=") > 0){
                    String[] tmp = line.split("=");
                    if(tmp.length == 2){
                        cookieMap.put(tmp[0], tmp[1]);
                    }
                }
            }
            for (Map.Entry<String,String> entry : cookieMap.entrySet()) {
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                cookie += " " + entry.getKey() + "=" + entry.getValue() + ";";
            }
//            cookie = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println("cookie=" + cookie);
        return cookie;
    }

//    private static String getRedisCookie(final String url){
//        String cookie = ((String ) SessionRedisClient.getInstance().exec(new IRedisSessionCallback() {
//            public Object exec(Jedis jedis) {
//                if(jedis == null){
//                    return getCookie(url);
//                }
//                String cookie = jedis.get(cookie_rkey);
//                if(StringUtil.isEmpty(cookie)){
//                    cookie = getCookie(url);
//                    jedis.setex(cookie_rkey, 600, cookie);
//                }
//                return cookie;
//            }
//        }));
//        return cookie;
//    }
}
