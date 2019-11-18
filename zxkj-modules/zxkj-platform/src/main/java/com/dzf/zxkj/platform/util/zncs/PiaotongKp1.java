package com.dzf.zxkj.platform.util.zncs;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.piaotong.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.alibaba.fastjson.JSON;
import org.jboss.logging.Logger;

public class PiaotongKp1 {
	private Logger logger = Logger.getLogger(this.getClass());
	
//    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIVLAoolDaE7m5oMB1ZrILHkMXMF6qmC8I/FCejz4hwBcj59H3rbtcycBEmExOJTGwexFkNgRakhqM+3uP3VybWu1GBYNmqVzggWKKzThul9VPE3+OTMlxeG4H63RsCO1//J0MoUavXMMkL3txkZBO5EtTqek182eePOV8fC3ZxpAgMBAAECgYBp4Gg3BTGrZaa2mWFmspd41lK1E/kPBrRA7vltMfPj3P47RrYvp7/js/Xv0+d0AyFQXcjaYelTbCokPMJT1nJumb2A/Cqy3yGKX3Z6QibvByBlCKK29lZkw8WVRGFIzCIXhGKdqukXf8RyqfhInqHpZ9AoY2W60bbSP6EXj/rhNQJBAL76SmpQOrnCI8Xu75di0eXBN/bE9tKsf7AgMkpFRhaU8VLbvd27U9vRWqtu67RY3sOeRMh38JZBwAIS8tp5hgcCQQCyrOS6vfXIUxKoWyvGyMyhqoLsiAdnxBKHh8tMINo0ioCbU+jc2dgPDipL0ym5nhvg5fCXZC2rvkKUltLEqq4PAkAqBf9b932EpKCkjFgyUq9nRCYhaeP6JbUPN3Z5e1bZ3zpfBjV4ViE0zJOMB6NcEvYpy2jNR/8rwRoUGsFPq8//AkAklw18RJyJuqFugsUzPznQvad0IuNJV7jnsmJqo6ur6NUvef6NA7ugUalNv9+imINjChO8HRLRQfRGk6B0D/P3AkBt54UBMtFefOLXgUdilwLdCUSw4KpbuBPw+cyWlMjcXCkj4rHoeksekyBH1GrBJkLqDMRqtVQUubuFwSzBAtlc";

//    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJkx3HelhEm/U7jOCor29oHsIjCMSTyKbX5rpoAY8KDIs9mmr5Y9r+jvNJH8pK3u5gNnvleT6rQgJQW1mk0zHuPO00vy62tSA53fkSjtM+n0oC1Fkm4DRFd5qJgoP7uFQHR5OEffMjy2qIuxChY4Au0kq+6RruEgIttb7wUxy8TwIDAQAB";

    private static String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    
    //
    private static String xxptbm = null;
    private static String ptxxurl = null;
    private static String platformCode = null;
    private static String signType = null;
    private static String format = null;
    private static String xxversion = null;
    private static String xxpwd = null;
    public static String xxsize = null;//请求页数
    private static String privateKey = null;
    private static String publicKey = null;
    
	// 请求参数
	
	private String taxpayerNum;//纳税识别号
	private String enterpriseName;//企业名称
	private String startTime;//开始时间
	private String endTime;//结束时间
	private int pageSize;//每页条数
	private int pageIndex;//第几页
	private int totalCount;//发票总数量
	
	// 返回数据构造vos
	private String userid;
	private String pk_corp;
	private int ly;
	
	static{
		ResourceBundle bundle = PropertyResourceBundle.getBundle("caifangtong");
		ptxxurl = bundle.getString("ptxxurl");
		xxptbm = bundle.getString("xxptbm");
		platformCode = bundle.getString("platformCode");
		signType = bundle.getString("signType");
		format = bundle.getString("format");
		xxversion = bundle.getString("xxversion");
		xxpwd = bundle.getString("xxpwd");
		xxsize = bundle.getString("xxsize");
		privateKey = bundle.getString("privateKey");
		publicKey = bundle.getString("publicKey");
	}

	public PiaotongKp1(String taxpayerNum,
			    String enterpriseName,
			    String startTime,
			    String endTime,
				String userid,
				String pk_corp,
				int ly){
			this.taxpayerNum = taxpayerNum;
			this.enterpriseName = enterpriseName;
			this.startTime = startTime;
			this.endTime = endTime;
		
			this.userid = userid;
			this.pk_corp = pk_corp;
			this.ly = ly;
		}

	public PiaoTongResHVO[] getVOs(int pageSize, int pageIndex) {

		String result = null;
		try {
			Map<String, String> map = getBusiParams(pageSize, pageIndex);
			List<NameValuePair> params = getParam(map);
			String url = ptxxurl;
			result = RemoteClient.sendPostData(url, params);
			
			logger.info("----------------请求发票列表-----------BEGIN");
			logger.info(result);
			logger.info("----------------请求发票列表-----------END");
		} catch (Exception e) {
			logger.error("错误",e);
		}

		PiaoTongResHVO[] hvos = parseResult(result);

		return hvos;
	}

	private PiaoTongResHVO[] parseResult(String result){
		if (StringUtil.isEmpty(result))
			throw new BusinessException("一键取票获取数据失败，请联系管理员");
		
		PiaoTongResVO resvo = JSON.parseObject(result, PiaoTongResVO.class);
		
		if(resvo == null
				|| StringUtil.isEmpty(resvo.getCode())
				|| StringUtil.isEmpty(resvo.getMsg())){
					throw new BusinessException("一键取票解析数据失败，请联系管理员");
		}else if(!ICaiFangTongConstant.SUCCESS.equals(resvo.getCode())){
			throw new BusinessException(
					StringUtil.isEmpty(resvo.getMsg()) 
						? "一键取票返回信息为空，请联系管理员" : resvo.getMsg()
				);
		}
		
		String content = resvo.getContent();
		
		if(StringUtil.isEmpty(content))
			return null;
		
		byte[] bytes;
		try {
			bytes = CommonXml.decrypt3DES(xxpwd, Base64CodeUtils.decode(content));
			content = new String(bytes, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		
		PiaoTongResContentVO contentvo = JSON.parseObject(content, PiaoTongResContentVO.class);
		setTotalCount(contentvo.getTotalCount());
		PiaoTongResHVO[] hvos = contentvo.getData();
		
		return hvos;
	}
	
	private void setDefaultValues(CaiFangTongHVO[] hvos, int size, String kprq) {
		if (hvos == null || hvos.length == 0 || hvos.length != size || StringUtil.isEmpty(kprq)) {
			throw new BusinessException("解析数据失败，请联系管理员");
		}

		CaiFangTongBVO[] bvos = null;
		for (CaiFangTongHVO hvo : hvos) {
			hvo.setCoperatorid(userid);
			hvo.setDoperatedate(new DZFDate());
			hvo.setPk_corp(pk_corp);
			hvo.setLy(ly);// 是销项还是进项
//			hvo.setMaxkprq(kprq);

			bvos = hvo.getFp_kjmx();

			if (bvos == null || bvos.length == 0)
				throw new BusinessException("解析表体数据失败，请联系管理员");

			hvo.setChildren(bvos);// 设置子表
			if (bvos != null && bvos.length > 0) {
				for (CaiFangTongBVO bvo : bvos) {
					bvo.setPk_corp(pk_corp);
				}
			}
		}
	}

	private Map<String, String> getBusiParams(int pageSize, int pageIndex) throws Exception {
		PiaoTongReqContentVO reqcontvo = new PiaoTongReqContentVO();
		reqcontvo.setTaxpayerNum(taxpayerNum);
		reqcontvo.setEnterpriseName(enterpriseName);
		reqcontvo.setStartTime(startTime);
		reqcontvo.setEndTime(endTime);
		reqcontvo.setPageSize(pageSize);
		reqcontvo.setPageIndex(pageIndex);
		
//		String content = OFastJSON.toJSONString(reqcontvo);
		String content = JSON.toJSONString(reqcontvo);
		byte[] bytes = CommonXml.encrypt3DES(xxpwd, content.getBytes("UTF-8"));
		content = Base64CodeUtils.encode(bytes);
		content = content.replace("\r\n", "").replace("\n", "");
		Map<String, String> map = new HashMap<String, String>();
		map.put("platformCode", platformCode);
	    map.put("signType", signType);
	    map.put("format", format);
	    map.put("version", xxversion);
	    map.put("content", content);
	    map.put("timestamp", new DZFDateTime().toString());
	    map.put("serialNo", getSerialNo(xxptbm));//getSerialNo("DEMO"));
	    String sign = sign(getSignatureContent(map), privateKey);
	    sign = sign.replace("\r\n", "").replace("\n", "");
	    map.put("sign", sign);
		return map;
	}
	
	private List<NameValuePair> getParam(Map<String, String> map) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			params.add(new BasicNameValuePair(entry.getKey(), "\"" + entry.getValue() + "\""));
		}

		return params;
	}
	
	private String sign(String content, String privatekey){
		try
	    {
	      PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64CodeUtils.decode(privateKey));
	      KeyFactory keyf = KeyFactory.getInstance("RSA");
	      PrivateKey priKey = keyf.generatePrivate(priPKCS8);

	      Signature signature = Signature.getInstance("SHA1WithRSA");

	      signature.initSign(priKey);
	      signature.update(content.getBytes("UTF-8"));

	      byte[] signed = signature.sign();

	      return Base64CodeUtils.encode(signed);
	    }
	    catch (Exception e) {
//	      e.printStackTrace();
	    	logger.error("错误",e);
	    }

	    return null;
	}
	
	public static String getSignatureContent(Map<String, String> params)
	  {
	    if (params == null) {
	      return null;
	    }
	    StringBuffer content = new StringBuffer();
	    List keys = new ArrayList(params.keySet());
	    Collections.sort(keys);
	    for (int i = 0; i < keys.size(); i++) {
	      String key = (String)keys.get(i);
	      if (params.get(key) != null)
	      {
	        String value = String.valueOf(params.get(key));
	        content.append(new StringBuilder().append(i == 0 ? "" : "&").append(key).append("=").append(value).toString());
	      }
	    }
	    return content.toString();
	  }
	
	private String generateShortUuid()
	  {
	    StringBuffer shortBuffer = new StringBuffer();
	    String uuid = UUID.randomUUID().toString().replace("-", "");
	    for (int i = 0; i < 8; i++) {
	      String str = uuid.substring(i * 4, i * 4 + 4);
	      int x = Integer.parseInt(str, 16);
	      shortBuffer.append(chars[(x % 62)]);
	    }
	    return shortBuffer.toString();
	  }

	private String getSerialNo(String prefix)
	{
	    return prefix
	    		+ new DZFDateTime().toString().replace(" ", "").replace(":", "").replace("-", "")
	    		+ generateShortUuid();
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

//	private String requestListFp(TicketNssbhVO nssbvo){
//		String content = null;
//		try{
//			StringBuffer sf = new StringBuffer();
//			sf.append("<REQUEST_FPCY_QYSH>");
//			sf.append("<NSRSBH>" + nssbvo.getNssbh() + "</NSRSBH>");//开票纳税人识别号
//			sf.append("<TIMESTAMP>").append(lastTime)//SaveFaInfo.getMaxCurtimestamp(nssbvo)
//				.append("</TIMESTAMP>");//增量时间戳
//			sf.append("<PAGESIZE>" + CommonXml.pagesize + "</PAGESIZE>");
//			sf.append("</REQUEST_FPCY_QYSH>");
//			byte[] jm = CommonXml.encrypt3DES(CommonXml.appSecret, sf.toString().getBytes());
//			content = Base64CodeUtils.encode(jm);
//		}catch(Exception e){
//			logger.error("错误",e);
//		}
//		return content;
//	}

}
