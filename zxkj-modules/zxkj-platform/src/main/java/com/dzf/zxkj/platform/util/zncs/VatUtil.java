package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongBVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongHVO;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;


@Slf4j
public class VatUtil {

	public static List<CaiFangTongHVO> reGetData(List<CaiFangTongHVO> list) throws DZFWarpException {
		if(list == null || list.size() == 0)
			return null;
		
		String drcode;
		for(CaiFangTongHVO vo : list){
			drcode = getDrCode(vo);
//			System.out.println(drcode);
			//log.info("二维码信息"+drcode);
			if(StringUtil.isEmpty(drcode))
				continue;
			
			FpMsgClient fpclient = new FpMsgClient();
			String value = fpclient.sendPostXml(drcode);
//			String value = "<?xml version='1.0' encoding='UTF-8'?><interface xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"\" version=\"PTYF1.0\" xsi:schemaLocation=\"http://www.chinatax.gov.cn/tirip/dataspec/interfaces.xsd\"><returnStateInfo><returnCode>0000</returnCode><returnMessage>5aSE55CG5oiQ5Yqf</returnMessage></returnStateInfo><globalInfo><appKey>20160825</appKey><appSecret></appSecret><accessToken>6gHnkAQ1HkY=</accessToken><UID>00000000000000000000</UID><version>1.0</version><interfaceCode>REQUEST_FPCY</interfaceCode><passWord>8673458904B3HuQdN7r9QVs8Di6eK4PA==</passWord><requestTime>2018-10-10 10:40:31</requestTime><dataExchangeId>2016082520181010931158267</dataExchangeId></globalInfo><data><dataDescription><codeType>3DES</codeType><encryptCode>1</encryptCode><zipCode>0</zipCode></dataDescription><content>j9EFhwsL7ekIVCsQq4G74Q33fdVRCkp6u7EpuKz79ZGeh6P4ymSU3JB5XkGJcLmHzMR6oGP60eU+/vyIn5FaeULA/8JanGFiG4VJCGzZDhdgdoB7QBvF/RgvhD67Dp04D/NhRBCKwE2iPKB1o9RSYG87BlwKGeBViH207ptf6EeFBMIAs3SbKD7gHYezfKt239ZGTsTxT6oyfoIV43Gqd+3AFZmgglHASrxthBe0oPumGFtCLMPUzc/6trGt5zGEhntmDB7hHioD2P27j3OVGur/nRBoj8gROF+TzFXSu8Gl4nayMoTQnfClVNohQGvGWrppjY9N1na7lNzQCY0Pp1Fvc0r3s1zde4rCxRe0mXcK7Rl0CErVXIlQyNRm0stdeAgTdUpocrM5gx0ArJfohXWPxKICY/DQg5OckxI/OTRuhqhShfvnxPNgyPVR1WkTXZWxl//tAzw3Wc+SaTXpEYCKFoVW0BCwaKBQbZpg0YjFkYWp9M4BJid0T0PNwyBKxXcAdHb3KcF6dPrjG4UcTgA+8NyzSKdfu/fBWA85eUQVuxTi11/dmFE201Z1H7Tc7Tl49NPzQdp6Axzn7tMRblHCsJ88OsWGv/mx0psp1quYIeq5/9ES02BxBuB8WFLT8xXJp6VUTiPHUrN2Amw7hZJNyw1owAAb9ZbPQvkFm1l3V9qAVDTkOy2tvDbdqluKVpSbwCgl2HiNmLPl2wxXnB2ldaizG21VsJB13nwHcPjVFYMdf+Ezb2IFcfTBZ7T6kG7JrMCpRFmwbF5T7hFIbJ5vfPNY7AwJbHBDj7amSELGQ4MPp66pZYox25YH7e0KqhPtYiEZfbePnAXfLEtjpsrBhqnkhIGxQ8UEAGHTi9FcWjs9uZSZfnd81rTbS+EycSqG6RRpi1BWOK74JeIBmK8f9sVXmQF9Tr9gm0IFBZEKGTeVyDmA3KkmH5NP1jHe60w7P+IMWTl7O7q8md8yyWrS2R4C7QcAD3g5NlLepUOcUt17V9zre5Lh5bUAia56jkEToK7cxGHOTsSsugLaYUJkUUZNVtOhytjSy9VhG8nEktLCPDw4ZROn2RoQ1zBffYaCEVje4TDFA3KflgZ5mxxx/67Udms1mKc+1rrXQXhRtbFAI9ThpBOn2RoQ1zBf0WWRmmkuwDipnqrUF5cIAxOn2RoQ1zBfSW1ljyCN2TMT+cxgxYbrNBOn2RoQ1zBf0fuDhWV6wVuuLkD9KKOmhBOn2RoQ1zBfSSSQLeJe15yLf/bUsEWOEmW5woDagBIuUxvurITyDuB1/z4DVoLm17caSyIGh1tdeRP3cTgalXsTp9kaENcwX/Pd9qY5BSIwUA/QZ/nX8CETp9kaENcwX8VOLz3WSslzbqej6UjfMW6Z9kFmJfVt6qTi6ANg7F23ugk+h6+aQ5X9H6NChNKhFQ/FtRg2Bi/olEtnLvfxbr5ODU2XGdB1fQ==</content></data></interface>";
//			String value = "<?xml version='1.0' encoding='UTF-8'?><interface xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"\" version=\"PTYF1.0\" xsi:schemaLocation=\"http://www.chinatax.gov.cn/tirip/dataspec/interfaces.xsd\"><returnStateInfo><returnCode>0000</returnCode><returnMessage>5aSE55CG5oiQ5Yqf</returnMessage></returnStateInfo><globalInfo><appKey>20160825</appKey><appSecret></appSecret><accessToken>6gHnkAQ1HkY=</accessToken><UID>00000000000000000000</UID><version>1.0</version><interfaceCode>REQUEST_FPCY</interfaceCode><passWord>8673458904B3HuQdN7r9QVs8Di6eK4PA==</passWord><requestTime>2018-12-11 16:24:00</requestTime><dataExchangeId>2016082520181211596244208</dataExchangeId></globalInfo><data><dataDescription><codeType>3DES</codeType><encryptCode>1</encryptCode><zipCode>0</zipCode></dataDescription><content>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</content></data></interface>";
			
			if(StringUtil.isEmpty(value))
				continue;
			
			//log.info(value);
			reBuildData(value, vo);

		}
		
		return list;
	}
	
	private static void reBuildData(String xmlvalue, CaiFangTongHVO vo) throws DZFWarpException{
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlvalue);
		} catch (DocumentException e) {
		}
		
		if(document == null)
			return;
		
		Element celement = document.getRootElement().element("data").element("content");
		String content = celement.getText();

		Element returncode = document.getRootElement().element("returnStateInfo").element("returnCode");
//		Element returnMessage = document.getRootElement().element("returnStateInfo").element("returnMessage");
//		String text = returnMessage.getText();
//		BASE64Decoder decoder = new BASE64Decoder();
//    	try {
//			System.out.println(new String(decoder.decodeBuffer(text)));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (returncode.getTextTrim().equals("0000")) {
			// 先判断是否压缩、加密
			Element desc = document.getRootElement().element("data").element("dataDescription");
			String zip = desc.element("zipCode").getText();//
			String encry = desc.element("encryptCode").getText();
			// 生成content元素
			Element contentele = getContentElement(zip, encry, content);
			if(contentele == null){
				return;
			}
			//log.info("认证平台返回"+contentele);
			getVOs(vo, contentele);

		}
		
	}
	
	private static void getVOs(CaiFangTongHVO svo, Element contentele){
		if(contentele == null){
			return;
		}
		
		List<Element> elements = contentele.elements();

		List<Element> elementbodys = null;

		List<Element> elementbodys2 = null;

		CaiFangTongBVO bvo;
		Map<String, String> headMapping = getHeadMapping();
		Map<String, String> bodyMapping = getBodyMapping();
		List<CaiFangTongBVO> bodyvos = new ArrayList();
		
		String temp;
		String name;
		String map_value;
		
		try {
			for (Element ment : elements) {
				if (ment.getName().equalsIgnoreCase("DETAILLIST")) {
					elementbodys = ment.elements();
					for (Element mentbody : elementbodys) {
						elementbodys2 = mentbody.elements();
						boolean flag = false;
						bvo = new CaiFangTongBVO();
						
						for (Element mentbody2 : elementbodys2) {
							temp = mentbody2.getTextTrim();
							if(StringUtil.isEmpty(temp)){
								continue;
							}
							name = mentbody2.getName();
							
							if(!StringUtil.isEmpty(name) 
									&& "HWMC".equals(name)
									&& !StringUtil.isEmpty(temp)
									&& ("原价合计".equals(temp)
											|| "折扣额合计".equals(temp))){
								flag = true;
								break;
							}
							
							if(bodyMapping.containsKey(name)){
								map_value = bodyMapping.get(name);
								bvo.setAttributeValue(map_value, temp);
							}
							
						}
						
						if(flag)
							continue;
						
						bvo.setPk_corp(svo.getPk_corp());
						bodyvos.add(bvo);
					}
				}
				if(StringUtil.isEmpty(ment.getTextTrim())){
					continue;
				}
				
				name = ment.getName();
				if(headMapping.containsKey(name)){
					temp = ment.getTextTrim();
					map_value = headMapping.get(name);
					svo.setAttributeValue(map_value, temp);
				}
			}
			
			svo.setChildren(bodyvos.toArray(new CaiFangTongBVO[0]));
		} catch (Exception e) {
//			throw new WiseRunException(e);
			log.error(e.getMessage(), e);
		}
		
	}
	
	private static Map<String, String> getHeadMapping(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("FPZL", "fp_zldm");
		map.put("SE", "kphjse");
		map.put("JSHJ", "kphjje");
		map.put("ZFBZ", "kplx");
		map.put("GFMC", "gmf_nsrmc");
		map.put("GFSBH", "gmf_nsrsbh");//购货方纳税人识别号
		map.put("GFDZDH", "gmf_dz");
		map.put("GFYHZH", "gmf_yh");
		map.put("XFMC", "xsf_nsrmc");
		map.put("XFSBH", "xsf_nsrsbh");
		map.put("XFDZDH", "xsf_dz");
		map.put("XFYHZH", "xsf_yh");
		map.put("JQBH", "jqbh");
		map.put("JYM", "jym");
		map.put("BZ", "bz");
		
//-----------------机动车发票---------
		map.put("GHDW", "gmf_nsrmc");//购方名称
		map.put("CLLX", "spmc");// 商品名称
		map.put("CPXH", "ggxh");// 规格型号
		map.put("BHSJ", "spdj");// 商品单价
		map.put("SLV", "spsl");// 商品税率
		map.put("SE", "spse");// 商品税额
		map.put("JSHJ", "jshj");// 价税合计
		map.put("XHDWMC", "xsf_nsrmc");// 销方名称
		map.put("NSRSBH", "xsf_nsrsbh");// 销方识别号
		map.put("DZ", "xsf_dz");// 销方地址
		map.put("DH", "xsf_dh");// 销方电话
		map.put("KHYH", "xsf_yh");// 销方银行
		map.put("ZH", "xsf_yhzh");// 销方账号
		
		return map;
	}
	
	private static Map<String, String> getBodyMapping(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("HWMC", "spmc");
		map.put("GGXH", "ggxh");
		map.put("DW", "dw");
		map.put("SL", "spsl");
		map.put("DJ", "spdj");
		map.put("JE", "spje");
		map.put("SLV", "sl");
		map.put("SE", "se");
		
		return map;
	}
	
	public static Element getContentElement(String zip, String encry, String content) {
		Element root = null;
		try {
			byte[] bytes = Base64CodeUtils.decode(content);// 先base64解码
			String strs = new String(bytes, "UTF-8");
			// 解压缩
			if (!CommonXml.unzip.equals(zip)) {
//				strs = unZipString(bytes);
				bytes = CommonXml.decompress(bytes);
			}
			// 解密
			if (CommonXml.endes.equals(encry)) {// 3des加密、解密
				byte[] jm = CommonXml.decrypt3DES(CommonXml.appSecret, bytes);
				strs = new String(jm, "UTF-8");
				log.info("认证平台返回"+strs);
			} else if (CommonXml.enca.equals(encry)) {

			}
			Document document = DocumentHelper.parseText(strs);
			root = document.getRootElement();
		} catch (Exception e) {
		}
		return root;
	}
	
	private static String getDrCode(CaiFangTongHVO vo){
		
		StringBuilder builder = new StringBuilder();
		//查验规则：01,类型,代码,号码,金额,日期,校验码,xxxx
		builder.append("01,");
		//类型
		String invoiceType=null;
		//12 位代码，第 1 位是 0，第 11、12 位是 11，属于电子普通发票，编号 10；第 1 位是 0，第 11、12 位是04 或 05，普通发票，编号 04
		if(vo.getFpdm().length()==12){
			if(vo.getFpdm().substring(0, 1).equals("0")){
				
				if(vo.getFpdm().substring(10, 12).equals("11")){
					builder.append("10,");
					invoiceType="10";
				}else if(vo.getFpdm().substring(10, 12).equals("04") 
						|| vo.getFpdm().substring(10, 12).equals("05")){
					builder.append("04,");
					invoiceType="04";
				}else{
					return null;
//					throw new Exception("未能识别发票类型");
				}
				
			}else if(!StringUtil.isEmpty(vo.getFplx())&&vo.getFplx().equals("机动车发票")){
				builder.append("03,");
				invoiceType="03";
			}
			else{
//				throw new Exception("发票代码输入有误");
				return null;
			}
		//10 位代码，第 8 位是 1 或 5，专票，编号 01；第 8位是 6 或 3，普票，编号 04
		}else if(vo.getFpdm().length()==10){
			if(vo.getFpdm().substring(7, 8).equals("1") 
					|| vo.getFpdm().substring(7, 8).equals("5")){
				builder.append("01,");
				invoiceType="01";
			}else if(vo.getFpdm().substring(7, 8).equals("6")
					|| vo.getFpdm().substring(7, 8).equals("3")){
				builder.append("04,");
				invoiceType="04";
			}else{
				return null;
//				throw new Exception("未能识别发票类型");
			}
		}else{
//			throw new Exception("发票代码输入有误");
			return null;
		}
		//代码
		builder.append(vo.getFpdm()+",");
		//号码
		builder.append(vo.getFphm()+",");
		//金额
		String je = vo.getHjbhsje() == null ? "" : vo.getHjbhsje().toString();
		builder.append(je + ",");
		//日期
		builder.append(vo.getKprq().replace("-", "")+",");
		//校验码
		if(vo.getJym() == null){
			vo.setJym("");
		}
		builder.append(vo.getJym() + ",");
		//xxxx随机四位
		builder.append(new Random().nextInt(9000)+1000);
		/*普票04 必须有校验码
		专票01 必须有金额
		电子10 和普票一样*/
		if(invoiceType.equals("04")||invoiceType.equals("10")){
			if(StringUtil.isEmpty(vo.getJym())){
//				throw new Exception("校验码不能为空");
				return null;
			}
		}else if(invoiceType.equals("01") 
				&& SafeCompute.sub(new DZFDouble(vo.getHjbhsje()), new DZFDouble(0)).doubleValue() == 0){//金额
//			throw new Exception("金额不能为空");
			return null;
		}
		
		if(invoiceType.equals("01") && vo.getHjbhsje() != null){
			if(SafeCompute.sub(new DZFDouble(vo.getHjbhsje()), new DZFDouble(0)).doubleValue() == 0){
//				throw new Exception("金额不能为空");
				return null;
			}
		}

		return builder.toString();
	}
}
