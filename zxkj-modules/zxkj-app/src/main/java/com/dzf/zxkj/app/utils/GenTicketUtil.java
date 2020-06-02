package com.dzf.zxkj.app.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.model.ticket.*;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

/**
 * 生成票据信息vo
 * 
 * @author zhangj
 *
 */
@Slf4j
@Component
public class GenTicketUtil {

	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;

	public SuperVO genTickMsgVO(String xmlvalue, String drcode, String account_id) throws DZFWarpException {
		
		if (StringUtil.isEmpty(xmlvalue))
			return null;
		List<ImageTicketHVO> ticketvos = null;
		SuperVO headvo = null;
		try {
			if(drcode.indexOf(",")<0){
				throw new BusinessException("扫描二维码信息出错，信息不合法！");
			}
			Document document = DocumentHelper.parseText(xmlvalue);
			Element celement = document.getRootElement().element("data").element("content");
			String content = celement.getText();

			Element returncode = document.getRootElement().element("returnStateInfo").element("returnCode");
			Element returnMessage = document.getRootElement().element("returnStateInfo").element("returnMessage");
			if (returncode.getTextTrim().equals("0000")) {
				// 先判断是否压缩、加密
				Element desc = document.getRootElement().element("data").element("dataDescription");
				String zip = desc.element("zipCode").getText();//
				String encry = desc.element("encryptCode").getText();
				// 生成content元素
				Element contentele = iZxkjRemoteAppService.getContentElement(zip, encry, content);
				if(contentele == null){
					throw new BusinessException("获取票据内容失败!");
				}
				// 获取发票类型
				String fpzl = null;
				if(contentele.element("FPZL") == null){
					fpzl = drcode.split(",")[1];
				}else{
					fpzl = contentele.element("FPZL").getTextTrim();
				} 
				headvo = getAggTicketVOs(fpzl, contentele);
			} else if (returncode.getTextTrim().equals("9999")) {
				byte[] bytes = Base64CodeUtils.decode(returnMessage.getTextTrim());// 先base64解码
				String strs = new String(bytes, "UTF-8");

				throw new BusinessException(strs);
			} else {
				byte[] bytes = Base64CodeUtils.decode(returnMessage.getTextTrim());// 先base64解码
				String strs = new String(bytes, "UTF-8");

				throw new BusinessException(strs);
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
			throw new WiseRunException(e);
		} catch (DocumentException e) {
			log.error(e.getMessage(),e);
			throw new WiseRunException(e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}
			throw new WiseRunException(e);
		}  

		return headvo;

	}

	private SuperVO getAggTicketVOs(String fpzl, Element contentele) throws DZFWarpException {

		SuperVO svo = null;

		if (contentele == null) {
			return svo;
		}

		String bodyname = null;

		List<SuperVO> bodyvos = new ArrayList();

		//增值税专用发票：01,增值税普通发票：04,增值税普通发票（电子）：10或51
		if (fpzl.equals("01") || fpzl.equals("04") || fpzl.equals("10") || fpzl.equals("51")) {
			svo = new ZzsTicketHVO();
			bodyname = ZzsTicketBVO.class.getName();
		} else if (fpzl.equals("02")) {// 货运运输业增值税专用发票：02
			svo = new HyZzsTicketHVO();
			bodyname = HyZzsTicketBVO.class.getName();
		} else if (fpzl.equals("03")) {// 机动车销售统一发票：03
			svo = new JdcXsTicketVO();
		}
		
		if(svo==null){
			throw new BusinessException("暂不支持该发票");
		}

		List<Element> elements = contentele.elements();

		List<Element> elementbodys = null;

		List<Element> elementbodys2 = null;

		StringBuffer xms = new StringBuffer();

		StringBuffer jes = new StringBuffer();
		
		String tempvalue = null;

		try {
			for (Element ment : elements) {
				if (ment.getName().equalsIgnoreCase("DETAILLIST")) {
					elementbodys = ment.elements();
					for (Element mentbody : elementbodys) {
						elementbodys2 = mentbody.elements();
						SuperVO bvo = (SuperVO) Class.forName(bodyname).newInstance();
						for (Element mentbody2 : elementbodys2) {
							if(StringUtil.isEmpty(mentbody2.getTextTrim())){
								continue;
							}
							if(mentbody2.getName().equalsIgnoreCase("dj") || mentbody2.getName().equalsIgnoreCase("je")
									|| mentbody2.getName().equalsIgnoreCase("se")){
								tempvalue = mentbody2.getTextTrim() ==null? "0.00": DZfcommonTools.formatDouble(new DZFDouble(mentbody2.getTextTrim()));
								bvo.setAttributeValue(mentbody2.getName().toLowerCase(), tempvalue);
							}else{
								bvo.setAttributeValue(mentbody2.getName().toLowerCase(), mentbody2.getTextTrim());
							}
						}
						bodyvos.add(bvo);
					}
				}
				if(StringUtil.isEmpty(ment.getTextTrim())){
					continue;
				}
				
				if(ment.getName().equalsIgnoreCase("kprq")){
					String valuetrim = ment.getTextTrim();
					svo.setAttributeValue(ment.getName().toLowerCase(),valuetrim.substring(0,4)+"-"+valuetrim.substring(4,6)+"-"+valuetrim.substring(6,8));
				}else if(ment.getName().equalsIgnoreCase("je") || ment.getName().equalsIgnoreCase("se")
						|| ment.getName().equalsIgnoreCase("jshj")){
					tempvalue = ment.getTextTrim() ==null? "0.00":DZfcommonTools.formatDouble(new DZFDouble(ment.getTextTrim()));
					svo.setAttributeValue(ment.getName(), tempvalue);
				}else{
					svo.setAttributeValue(ment.getName().toLowerCase(), ment.getTextTrim());
				}
			}
			
//			if(svo.getAttributeValue("file_type")!=null && svo.getAttributeValue("file")!=null){
//				String file = (String) svo.getAttributeValue("file");
//				String file_type = (String) svo.getAttributeValue("file_type");
//				if(file_type.equalsIgnoreCase("IMAGE")){
//					file_type = ".jpg";
//				}
//				//字符转码
//				byte[] fpbytes = Base64Util.getFromBASE64(file);
//				String uuid = UUID.randomUUID().toString();
//				String id = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).upload(fpbytes, uuid+file_type, null);//上传文件服务器
//				svo.setAttributeValue("img_url", id);
//			}
			
			if(svo!=null){
				svo.setAttributeValue("fpzl", fpzl);
			}

			SuperVO[] bvos = new SuperVO[bodyvos.size()];
			for (int i = 0; i < bodyvos.size(); i++) {
				if (fpzl.equals("01") || fpzl.equals("04") || fpzl.equals("10") || fpzl.equals("51")) {
					if (bodyvos.get(i).getAttributeValue("hwmc") != null) {
						xms.append(bodyvos.get(i).getAttributeValue("hwmc") + ";");
					}
					if (bodyvos.get(i).getAttributeValue("je") != null) {
						String je = (String) bodyvos.get(i).getAttributeValue("je");
						String se = (String) bodyvos.get(i).getAttributeValue("se");
						jes.append(SafeCompute.add(getDzfDouble(je), getDzfDouble(se)).setScale(2, DZFDouble.ROUND_HALF_UP)  + ";");
					}
				}
				bvos[i] = bodyvos.get(i);
			}

			if (xms.length() > 0) {
				svo.setAttributeValue("fpxms", xms.substring(0, xms.length() - 1));
			}

			if (jes.length() > 0) {
				svo.setAttributeValue("fpjes", jes.substring(0, jes.length() - 1));
			}
			
			svo.setChildren(bvos);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
			throw new WiseRunException(e);
		}

		return svo;
	}
	
	public DZFDouble getDzfDouble(String value){
		
		DZFDouble mny = DZFDouble.ZERO_DBL;
		
		if(!StringUtil.isEmpty(value)){
			mny = new DZFDouble(value);
		}
		
		return mny;
	}

	/**
	 * 将压缩后的 byte[] 数据解压缩
	 *
	 * @param compressed
	 *            压缩后的 byte[] 数据
	 * @return 解压后的字符串
	 */
	public static String unZipString(byte[] compressed) {
		if (compressed == null)
			return null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		GZIPInputStream zin = null;
		String decompressed;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}

	// public static void main(String[] args) {
	// GenTicketUtil util = new GenTicketUtil();
	// util.genTickMsgVO(
	// "<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface xmlns=\"\"
	// version=\"PTYF1.0\"><returnStateInfo><returnCode>0000</returnCode><returnMessage>5aSE55CG5oiQ5Yqf</returnMessage></returnStateInfo><globalInfo><appKey>20160825</appKey><appSecret>TgJnk@MqDHzHRv2oZPj#KaS1</appSecret><UID>00000000000000000000</UID><version>1.0</version><interfaceCode>REQUEST_FPCY</interfaceCode><passWord>8673458904B3HuQdN7r9QVs8Di6eK4PA==</passWord><requestTime>2016-10-17
	// 16:05:29</requestTime><dataExchangeId>2016082520160912000000001</dataExchangeId></globalInfo><data><dataDescription><codeType>0</codeType><encryptCode>0</encryptCode><zipCode>0</zipCode></dataDescription><content>PFJFU1BPTlNFPjxGUEhNPjE4OTA5ODgwPC9GUEhNPjxGUERNPjAxMTAwMTYwMDExMTwvRlBETT48U0U+MTcuNDg8L1NFPjxKU0hKPjYwMDwvSlNISj48WkZCWj5OPC9aRkJaPjxYRk1DPuS6rOawuOS/oe+8iOWMl+S6rO+8iei0ouWKoeWSqOivouaciemZkOWFrOWPuDwvWEZNQz48WEZTSD48L1hGU0g+PFhGRFpESD7ljJfkuqzmtbfmt4DljLrkuIrlnLDljYHooZcx5Y+36ZmiNOWPt+alvDIz5bGCMjAzNCwxODkxMTkwMDYxOTwvWEZEWkRIPjxYRllIWkg+5Lit5Zu96ZO26KGM5LiK5Zyw5pSv6KGMLDEwMDM0MDI1NzYzMzc1MjwvWEZZSFpIPjxHRk1DPuWMl+S6rOS4reaxieWbveiJuuaWh+WMluiJuuacr+aciemZkOWFrOWPuDwvR0ZNQz48R0ZTSD48L0dGU0g+PEdGRFpESD4sPC9HRkRaREg+PEdGWUhaSD48L0dGWUhaSD48SllNPjwvSllNPjxTQkJIPjwvU0JCSD48SkU+NTgyLjUyPC9KRT48REVUQUlMTElTVD48REVUQUlMPjxIV01DPuS7o+eQhuacjeWKoei0uTwvSFdNQz48R0dYSD4yMDE2LjctOTwvR0dYSD48RFc+5pyIPC9EVz48U0w+My4wPC9TTD48REo+MTk0LjE3NDc1NzI4PC9ESj48SkU+NTgyLjUyPC9KRT48U0xWPjM8L1NMVj48U0U+MTcuNDg8L1NFPjwvREVUQUlMPjwvREVUQUlMTElTVD48L1JFU1BPTlNFPg==</content></data></interface>");
	// }

}
