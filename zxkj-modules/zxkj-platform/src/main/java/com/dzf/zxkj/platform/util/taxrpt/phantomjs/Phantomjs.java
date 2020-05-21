package com.dzf.zxkj.platform.util.taxrpt.phantomjs;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.config.TaxPhantomjsConfig;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.phantomjs.AutoTaxVO;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
@Slf4j
public class Phantomjs {
	
	private static String projectPath = "";
	
	private static String url = null;
	
	private static String devmode = null;
	
	private static String test = null;
	
	private static String savesuccess = "savesuccess";
	
	private static String jsPath = "";
	
	private static String exePath = "";
	
	private static String[] paramters = new String[]{"sbcode","ccounty","pk_taxreport",
			"corpid","logindate","token","clientid","clientpk_corp","clientuserid"};
	
	static{
			try {
				TaxPhantomjsConfig config = (TaxPhantomjsConfig) SpringUtils.getBean(TaxPhantomjsConfig.class);
				url = config.phantomjs_URL;
				devmode = config.devmode;
				test = config.test;
				if("true".equals(devmode)){
					jsPath = projectPath + File.separator + "phantomjs-2.1.1"+ File.separator + "runjs"+ File.separator+"autocalc.js";
					exePath = projectPath + File.separator + "phantomjs-2.1.1" + File.separator + "bin" + File.separator+ "phantomjs.exe";
				}else{
					if("false".equals(test)){//测试
//						jsPath = projectPath + File.separator + "opt" +File.separator+"phantomjs"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						jsPath = projectPath + File.separator + "apps" +File.separator+"phantomjs"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						exePath = "phantomjs";
					}else{
//						jsPath = projectPath + File.separator + "apps" +File.separator+"phantomjs-2.1.1"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						jsPath = projectPath + File.separator + "apps" +File.separator+"phantomjs"+ File.separator + "runjs" + File.separator+ "autocalc.js";
						exePath = "phantomjs";
					}
				}
			} catch (Exception e) {
			} finally{
				if(StringUtil.isEmpty(url)){
					url = "http://localhost:8521/nssb/taxrpt/tax-declaration";
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
	
	
	public static boolean savewrite(String token, String clientid, String clientpk_corp,
									String clientuserid, String logindate, String corpid, String corpname,
									Integer ccounty, TaxReportVO rvo){
		boolean flag = false;
		AutoTaxVO vo = buildTaxVO(token, clientid, clientpk_corp, clientuserid, logindate,
				corpid, corpname, ccounty, rvo);
		String p = getParseredHtml2(vo);
		if(!StringUtil.isEmpty(p) &&
				p.contains(savesuccess)){
			flag = true;
		}
		return flag;
	}
	
	private static AutoTaxVO buildTaxVO(String token, String clientid, String clientpk_corp,
										String clientuserid, String logindate, String corpid, String corpname,
										Integer ccounty, TaxReportVO rvo){
		AutoTaxVO vo = new  AutoTaxVO();
		vo.setUrl(url);
		vo.setToken(token);
		vo.setClientid(clientid);
		vo.setClientpk_corp(clientpk_corp);
		vo.setClientuserid(clientuserid);
		vo.setLogindate(logindate);
		vo.setCorpid(corpid);
		vo.setCorpname(corpname);
		vo.setCcounty(String.valueOf(ccounty));
		vo.setPk_taxreport(rvo.getPk_taxreport());
		vo.setSbcode(rvo.getSb_zlbh());

		return vo;
	}
}