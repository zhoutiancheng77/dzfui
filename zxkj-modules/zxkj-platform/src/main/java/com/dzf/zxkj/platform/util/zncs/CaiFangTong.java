package com.dzf.zxkj.platform.util.zncs;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.piaotong.*;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Slf4j
public class CaiFangTong {

	private static String ENDES = "1";//3DES加密
	
	private static String busiurl = null;
	
	private static String getcorpnameurl = null;
	
	private static String appId = null;
	
	private static String version = null;
	//注册密码
	public static String regpwd = null;
	//随机数
	public static String randnum = null;
	
	private static String encryptCode = null;
	
	static{
		ResourceBundle bundle = PropertyResourceBundle.getBundle("caifangtong");
		
		busiurl = bundle.getString("busiurl");
		
		getcorpnameurl = bundle.getString("getcorpnameurl");
		
		appId = bundle.getString("appId");
		
		version = bundle.getString("version");
		
		randnum = bundle.getString("randnum");
		
		regpwd = bundle.getString("regpwd");
		
		encryptCode = bundle.getString("encryptCode");
	}
	
	//请求参数
	private String nsrsbh;//纳税人识别号
	private String unitname;//公司名称
	private String bdm;//绑定码
//	private String lastTime;//最后请求时间
	private String period;//请求期间
	
	//返回数据构造vos
	private String userid;
	private String pk_corp;
	private int ly;
	private int size;//返回数据集合的数量
	
	public CaiFangTong(String nsrsbh, 
			String unitname,
			String bdm,
			String period, 
			String userid,
			String pk_corp,
			int ly){
		this.nsrsbh = nsrsbh;
		this.unitname = unitname;
		this.bdm = bdm;
//		this.lastTime = lastTime;
		this.period = period;
		
		this.userid = userid;
		this.pk_corp = pk_corp;
		this.ly = ly;
	}
	
	public CaiFangTongHVO[] getVOs(int start, int end, StringBuffer msg){
		
		Map<String, String> params = getBusiParams(start, end);
		
		String result = null;
		try {
			result = new HttpClientUtil().doPostEntity(busiurl, params, "UTF-8");
		} catch (Exception e) {
			log.error("错误",e);
		}
		
		CaiFangTongHVO[] hvos = parseResult(result, msg);
		
		return hvos;
	}
	public String getCorpName(){
		StringBuffer url = new StringBuffer();
		url.append(getcorpnameurl);
		url.append("/"+nsrsbh+"/"+bdm);
		
		String result = null;
		String companyName = null;
		result = new HttpClientUtil().doGetEntity(url.toString(), "UTF-8");

		if (StringUtil.isEmpty(result)) {
			throw new BusinessException("一键取票获取数据失败，请联系管理员");
		}

		CaiFangTongVO cftvo = JSON.parseObject(result, CaiFangTongVO.class);

		CaiFangTongStateVO statevo = cftvo.getState();

		if (statevo == null || StringUtil.isEmpty(statevo.getReturnCode()) || StringUtil.isEmpty(cftvo.getPassWord())) {
			throw new BusinessException("一键取票解析数据失败，请联系管理员");
		} else if (!ICaiFangTongConstant.SUCCESS.equals(statevo.getReturnCode())) {
			throw new BusinessException(
					StringUtil.isEmpty(statevo.getReturnMessage()) ? "一键取票返回信息为空，请联系管理员" : statevo.getReturnMessage());
		}

		String secret = cftvo.getPassWord().substring(10);

		String content = cftvo.getContent();

		content = jiem(content, secret);

		if (StringUtil.isEmpty(content)) {
			throw new BusinessException("一键取票获取公司名称失败，请联系管理员");
		}
		CaiFangTongCorpVO corpVO = JSON.parseObject(content, CaiFangTongCorpVO.class);
		companyName = corpVO.getEnterpriseName();

		return companyName;
	}
	
	private CaiFangTongHVO[] parseResult(String result, StringBuffer msg){
		if(StringUtil.isEmpty(result)){
			throw new BusinessException("一键取票获取数据失败，请联系管理员");
		}
		
		CaiFangTongVO cftvo = JSON.parseObject(result, CaiFangTongVO.class);
		
		CaiFangTongStateVO statevo = cftvo.getState();
		
		if(statevo == null 
				|| StringUtil.isEmpty(statevo.getReturnCode())
				|| StringUtil.isEmpty(cftvo.getPassWord())){
			throw new BusinessException("一键取票解析数据失败，请联系管理员");
		}else if(!ICaiFangTongConstant.SUCCESS.equals(statevo.getReturnCode())){
			throw new BusinessException(
						StringUtil.isEmpty(statevo.getReturnMessage()) 
							? "一键取票返回信息为空，请联系管理员" : statevo.getReturnMessage()
					);
		}
		
		String secret = cftvo.getPassWord().substring(10);
		
		String content = cftvo.getContent();
		
		content = jiem(content, secret);
		
		if(StringUtil.isEmpty(content)){
			return null;
		}

		CaiFangTongContentVO contentvo = JSON.parseObject(content, CaiFangTongContentVO.class);
		
		CaiFangTongHVO[] hvos = contentvo.getFpkj();
		
		String kprq = contentvo.getMaxkprq();
		int size = contentvo.getSize();
		
		setDefaultValues(hvos, size, kprq, msg);
		
		return hvos;
	}
	
	private void setDefaultValues(CaiFangTongHVO[] hvos, int size, String kprq, StringBuffer msg){
		if(hvos == null 
				|| hvos.length == 0
				|| hvos.length != size
//				|| StringUtil.isEmpty(kprq)
				){
			throw new BusinessException("解析数据失败，请联系管理员");
		}
		
		CaiFangTongBVO[] bvos = null;
		for(CaiFangTongHVO hvo : hvos){
			hvo.setCoperatorid(userid);
			hvo.setDoperatedate(new DZFDate());
			hvo.setPk_corp(pk_corp);
			hvo.setLy(ly);//是销项还是进项
//			hvo.setMaxkprq(kprq);
			//机动车
			if(!StringUtil.isEmpty(hvo.getFp_zldm())&&hvo.getFp_zldm().toUpperCase().equals("JDC")){
				CaiFangTongBVO bvo = new CaiFangTongBVO();
				bvo.setSphxh("1");//商品行序号
				bvo.setSpmc(hvo.getCllx());//商品名称
				bvo.setSpsl("1");//商品数量
				bvo.setSpje(hvo.getHjbhsje());//金额
				bvo.setSpdj(hvo.getHjbhsje());//单价
				bvo.setDw("辆");//单位
				bvo.setGgxh(hvo.getCpxh());//规格型号
				bvo.setSe(hvo.getKphjse());//税额
				bvo.setSl(hvo.getSlv());//税率
				bvo.setDr(0);
				bvos=new CaiFangTongBVO[]{bvo};
			}else{
				bvos = hvo.getFp_kjmx();
				
				if(bvos == null || bvos.length == 0){
//					throw new BusinessException("解析表体数据失败，请联系管理员");
					msg.append("<p>[").append(hvo.getFpdm()).append(",").append(hvo.getFphm()).append("]数据未同步</p>");
					continue;
				}
			}
			

			
			hvo.setChildren(bvos);//设置子表
			if(bvos != null && bvos.length > 0){
				for(CaiFangTongBVO bvo : bvos){
					bvo.setPk_corp(pk_corp);
				}
			}
		}
	}
	
	private Map<String, String> getBusiParams(int start, int end){
		Map<String, String> params = new HashMap<String, String>();
		
		CaiFangTongVO cftvo = new CaiFangTongVO();
		cftvo.setAppId(appId);
		cftvo.setVersion(version);
		cftvo.setEncryptCode(encryptCode);
		
		String pwd = getPwd();
		cftvo.setPassWord(pwd);
		
		String secret = pwd.substring(10);
		String content = getContent(secret, start, end);
		cftvo.setContent(content);
		
		CaiFangTongStateVO statevo = new CaiFangTongStateVO();
		statevo.setReturnCode(ICaiFangTongConstant.SUCCESS);
		statevo.setReturnMessage("SUCCESS");
		cftvo.setState(statevo);

//		String jstr = OFastJSON.toJSONString(cftvo);
		String jstr = JSON.toJSONString(cftvo);
		
		params.put("param", jstr);
		
		return params;
	}
	
	private String getContent(String appSecret, int start, int end){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"NSRSBH\":\"");
		sb.append(nsrsbh);
		sb.append("\",\"NSRMC\":\"");
		sb.append(unitname);
		
		if(!StringUtil.isEmpty(bdm)){
			sb.append("\",\"BDM\":\"");
			sb.append(bdm);
		}
		
		
		sb.append("\",\"STARTCOUNT\":");
		sb.append(start);
		sb.append(",\"ENDCOUNT\":");
		sb.append(end);
		
		sb.append(",\"KPYF\":\"");
		sb.append(period);
		sb.append("\"}");
		String content = sb.toString();
		
		content = jiam(content, appSecret);
		
		return content;
	}
	
	private String jiam(String data, String appSecret){
		String content = null;
		try {
			byte[] jm = data.getBytes();
			if(ENDES.equals(encryptCode)){
				jm = CommonXml.encrypt3DES(appSecret, jm);//3des加密
			}
			content = Base64CodeUtils.encode(jm);//base64编码
		} catch (Exception e) {
			log.error("错误",e);
		}
		
		return content;
	}
	
	private String jiem(String data, String appSecret){
		String content = null;
		try {
			byte[] jm = Base64CodeUtils.decode(data);
			
			if(ENDES.equals(encryptCode)){
				jm = CommonXml.decrypt3DES(appSecret, jm);
			}
			
			content = new String(jm, "UTF-8");
		} catch (Exception e) {
			log.error("错误",e);
		}
		
		return content;
	}
	
	private String getPwd(){
		String content = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((randnum + regpwd).getBytes("UTF-8"));
            byte[] md5Byte = md.digest();
			content = Base64CodeUtils.encode(md5Byte);
		} catch (Exception e) {
			log.error("错误",e);
		}
		return randnum + content;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
