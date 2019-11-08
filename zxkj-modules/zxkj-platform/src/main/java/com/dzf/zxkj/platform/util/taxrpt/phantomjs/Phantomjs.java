package com.dzf.zxkj.platform.util.taxrpt.phantomjs;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.phantomjs.AutoTaxVO;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.io.*;
import java.util.Properties;
@Slf4j
public class Phantomjs {
	
	private static String projectPath = "";
	
	private static String url = null;
	
	private static String devmode = null;
	
	private static String test = null;
	
	private static String savesuccess = "savesuccess";
	
	private static String jsPath = "";
	
	private static String exePath = "";
	
	private static String[] paramters = new String[]{"sbcode","ccounty","pk_taxreport","corp","corp_id","userid","logindate","ssoserver","dzfsso","dzfcorp","dzfuid"};
	
	static{
			Properties prop = new Properties();
			InputStream in = null;
			try {
				in =  Phantomjs.class.getResourceAsStream("/phantomjs.properties");
				prop.load(in);
				url = prop.getProperty("phantomjs_URL");
				devmode = prop.getProperty("devmode");
				test = prop.getProperty("test");
				if("on".equals(devmode)){
					jsPath = projectPath + File.separator + "phantomjs-2.1.1"+ File.separator + "runjs"+ File.separator+"autocalc.js";
					exePath = projectPath + File.separator + "phantomjs-2.1.1" + File.separator + "bin" + File.separator+ "phantomjs.exe";
				}else{
					if("on".equals(test)){//测试
						jsPath = projectPath + File.separator + "opt" +File.separator+"phantomjs"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						exePath = "phantomjs";
					}else{
						jsPath = projectPath + File.separator + "apps" +File.separator+"phantomjs-2.1.1"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						exePath = "phantomjs";
					}
				}
			} catch (Exception e) {
			} finally{
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if(StringUtil.isEmpty(url)){
					url = "http://localhost:8089/DZF_KJ/gl/taxrpt/tax_declaration2.jsp";
				}
			}
	}
	
	// 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
	private static String getParseredHtml2(AutoTaxVO vo) {
		StringBuffer sbf = new StringBuffer();
		try{
			Runtime rt = Runtime.getRuntime();
			StringBuffer param = new StringBuffer();
			param.append(vo.getUrl());
			for(String key : paramters){
				param.append(" "+vo.getAttributeValue(key));
			}
//			LOGGER.info("------------------------------1-------------->");
//			LOGGER.info("::::"+exePath + " " + jsPath + " " + param.toString());
//			LOGGER.info("------------------------------2-------------->");
			Process p = rt.exec(exePath + " " + jsPath + " " + param.toString());
			InputStream is = p.getInputStream();
//			LOGGER.info("------------------------------3-------------->");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				sbf.append(tmp);
				log.info("------------------------------phantomjs 控制台日志开始行::-------------->"+tmp);
				log.info(tmp);
				log.info("------------------------------phantomjs 控制台日志结束行::-------------->"+tmp);
			}
//			LOGGER.info("------------------------------All-------------->"+sbf.toString());
		}catch(IOException e){
			log.error("错误",e);
		}
		return sbf.toString();
	}
	
	
	public static boolean savewrite(String userid, String corpid, String unitname,
									Integer ccounty, String logindate, TaxReportVO rvo, Cookie[] cookies){
		boolean flag = false;
		AutoTaxVO vo = buildTaxVO(userid,corpid,unitname,
				ccounty,logindate,rvo,cookies);
		String p = getParseredHtml2(vo);
		if(!StringUtil.isEmpty(p) &&
				p.contains(savesuccess)){
			flag = true;
		}
		return flag;
	}
	
	private static AutoTaxVO buildTaxVO(String userid,String corpid,String unitname,
			Integer ccounty,String logindate,TaxReportVO rvo,Cookie[] cookies){
		AutoTaxVO vo = new  AutoTaxVO();
		vo.setUrl(url);
		vo.setSbcode(rvo.getSb_zlbh());
		vo.setCcounty(String.valueOf(ccounty));
		vo.setPk_taxreport(rvo.getPk_taxreport());
		vo.setCorp(unitname);
		vo.setCorp_id(corpid);
		vo.setUserid(userid);
		vo.setLogindate(logindate);
		if(cookies!=null && cookies.length>0){
			for(Cookie cook : cookies){
				if("dzfsso".equals(cook.getName())){
					vo.setDzfsso(cook.getValue());
				}else if("dzfcorp".equals(cook.getName())){
					vo.setDzfcorp(cook.getValue());
				}else if("dzfuid".equals(cook.getName())){
					vo.setDzfuid(cook.getValue());
				}
			}
		}
		return vo;
	}
}