package com.dzf.zxkj.platform.util.taxrpt.shandong;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.TaxParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

/**
 * 生成上传报文文件
 */
@Slf4j
public class XMLUtils {

	/**
	 * 创建一个上传报文
	 * 
	 * @param xml
	 * @return
	 */
	public static String createScBwXml(String xml, String serviceId, String sessionId, String tranReqDate,
			String nsrsbh, String pwd, String impl, String djxh) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("tiripPackage", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema");
		root.addAttribute("xsi:type", "tiripPackage");
		root.addAttribute("version", "1");

		Element em1 = root.addElement("sessionId");
		if (!StringUtil.isEmpty(sessionId)) {
			em1.addText(sessionId);
		}
		Element em2 = root.addElement("service");
		Element em21 = em2.addElement("serviceId");// 登录验证报文固定格式
		em21.addText(serviceId);
		Element em22 = em2.addElement("clientNo");// 客户端号
		em22.addText(TaxParamUtils.CLIENTNO);
		Element em23 = em2.addElement("tranSeq");// 随机生成的流水号
		em23.addText(UUID.randomUUID().toString());
		// em23.addText("PERSON_TRANSEQ");
		Element em24 = em2.addElement("repeatFlag");
		em24.addText("0");
		Element em25 = em2.addElement("tranReqDate");// 日期
		em25.addText(tranReqDate);

		Element em3 = root.addElement("identity");
		Element em31 = em3.addElement("application");
		Element em311 = em31.addElement("applicationId");// id
		em311.addText(TaxParamUtils.APPLICATIONID);
		Element em312 = em31.addElement("supplier");// 大账房
		em312.addText(TaxParamUtils.SUPPLIER);
		Element em313 = em31.addElement("version");// 版本
		em313.addText("1");
		Element em314 = em31.addElement("authenticateType");
		em314.addText("2");
		em31.addElement("cert");
		Element em316 = em31.addElement("password");// 密码
		em316.addText(TaxParamUtils.PASSWORD);

		Element em32 = em3.addElement("customer");
		Element em321 = em32.addElement("customerId");// 纳税人识别号
		em321.addText(nsrsbh);
		Element em322 = em32.addElement("authenticateType");
		em322.addText("2");
		Element em323 = em32.addElement("password");//
		em323.addText(pwd);
		em32.addElement("cert");
		Element em324 = em32.addElement("nsrsbh");// 纳税人识别号
		em324.addText(nsrsbh);
		em32.addElement("djxh");//登记序号
		if (!StringUtil.isEmpty(djxh)) {
			em32.addText(djxh);
		} else {
			em32.addText("");
		}

		Element em4 = root.addElement("routerSession");
		Element em41 = em4.addElement("paramList");
		Element em411 = em41.addElement("name");
		em411.addText("SENDER");
		Element em412 = em41.addElement("value");
		em412.addText(nsrsbh);

		Element em5 = root.addElement("signData");
		Element em51 = em5.addElement("signType");
		em51.addText("0");
		Element em52 = em5.addElement("signSource");
		em52.addText("000");
		Element em53 = em5.addElement("signValue");
		em53.addText("000");

		Element em6 = root.addElement("contentControl");
		Element em61 = em6.addElement("control");

		Element em611 = em61.addElement("id");
		em611.addText("1");
		Element em612 = em61.addElement("type");
		em612.addText("code");
		Element em613 = em61.addElement("impl");
		if (!StringUtil.isEmpty(impl)) {
			em613.addText(impl);
		}

		Element em7 = root.addElement("businessContent");
		Element em71 = em7.addElement("subPackage");
		Element em711 = em71.addElement("id");
		em711.addText("1");
		Element em712 = em71.addElement("content");
		// xml = xml.replaceAll("<","&lt;").replaceAll(">","&gt;");
		if (!StringUtil.isEmpty(impl)) {
			em712.addText(xml);
		} else {
			em712.addCDATA(xml);
		}

//		Element em713 = em71.addElement("paramList");
//		Element em7131 = em713.addElement("name");
//		em7131.addText("nsrsbh");
//		Element em7132 = em713.addElement("value");
//		em7132.addText(nsrsbh);
//
//		Element em714 = em71.addElement("paramList");
//		Element em7141 = em714.addElement("name");
//		em7141.addText("djxh");
//		Element em7142 = em714.addElement("value");
//		if (!StringUtil.isEmpty(djxh)) {
//			em7142.addText(djxh);
//		} else {
//			em7142.addText("");
//		}
//
//		Element em715 = em71.addElement("paramList");
//		Element em7151 = em715.addElement("name");
//		em7151.addText("swjgDm");
//		Element em7152 = em715.addElement("value");
//		em7152.addText("13713250000");

		return convertToString(document);
	}

	public static String createYzdlXML(String nsrzh, String password) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "NfnsrdlyzRequest");
		root.addAttribute("bbh", "");
		root.addAttribute("xmlbh", "");
		root.addAttribute("xmlmc", "");

		Element em2 = root.addElement("authenticateType");
		em2.addText("2");

		Element em3 = root.addElement("nsrzh");
		em3.addText(nsrzh);

		Element em4 = root.addElement("password");
		em4.addText(password);
		root.addElement("cert");
		root.addElement("random");
		root.addElement("signValue");
		root.addElement("caType");
		root.addElement("keyNo");
		root.addElement("zzjgDm");
		return convertToString(document);
	}

	/**
	 * 参照对照表生成业务报文
	 * 
	 * @param vos
	 *            // 对照表组织信息
	 * @return
	 */
	public static String createBusinessXML(TaxPosContrastVO[] vos, String xmlType) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", xmlType);

		for (TaxPosContrastVO vo : vos) {
			addChildElement(vo, root);
		}
		return convertToString(document);
	}

	private static String convertToString(Document document) {

		// 创建字符串缓冲区
		StringWriter stringWriter = new StringWriter();
		// 设置文件编码
		OutputFormat xmlFormat = new OutputFormat();
		xmlFormat.setEncoding("UTF-8");
		// 设置换行
		xmlFormat.setNewlines(true);
		// 生成缩进
		xmlFormat.setIndent(true);
		// 使用4个空格进行缩进, 可以兼容文本编辑器
		// xmlFormat.setIndent(" ");

		// 创建写文件方法
		XMLWriter xmlWriter = new XMLWriter(stringWriter, xmlFormat);
		// 写入文件
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		} finally {
			// 关闭
			try {
				xmlWriter.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			} finally {
			}
		}
		// 输出xml
		String xml = stringWriter.toString();
		xml = xml.replaceAll("\n", "");
		return xml;
	}

	// 添加子节点
	private static void addChildElement(TaxPosContrastVO vo, Element root) {
		Element em1 = root.addElement(vo.getItemkey());
		if (vo.getValue() != null) {
			em1.addText(vo.getValue().toString());
		}
		if (vo.getChildren() != null && vo.getChildren().length > 0) {
			TaxPosContrastVO[] vos = (TaxPosContrastVO[]) vo.getChildren();
			for (TaxPosContrastVO vo1 : vos) {
				addChildElement(vo1, em1);
			}
		}
	}

	/**
	 * 查询本期应申报清册
	 * 
	 * @param nsrsbh
	 * @return
	 */
	public static String createQcInfoXML(String nsrsbh) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "NfnsrdlyzRequest");

		Element em1 = root.addElement("nsrsbh");
		if (!StringUtil.isEmpty(nsrsbh)) {
			em1.addText(nsrsbh);
		}

		return convertToString(document);
	}

	public static String createQcXML(String nsrzh, String password, String year, TaxQcQueryVO qcvo) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "sbCommonCshReq");
		root.addAttribute("cnName", "");
		root.addAttribute("name", "sbCommonCshReq");
		root.addAttribute("version", "SW5001-" + year);

		Element em2 = root.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em2.addText(qcvo.getDjxh());
		}

		Element em3 = root.addElement("gdslxDm");
		if (!StringUtil.isEmpty(qcvo.getGdslxDm())) {
			em3.addText(qcvo.getGdslxDm());
		}

		Element em4 = root.addElement("sssqQ");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em4.addText(qcvo.getSssqQ());
		}

		Element em5 = root.addElement("sssqZ");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em5.addText(qcvo.getSssqZ());
		}

		Element em6 = root.addElement("dzbzdszlDm");
		if (!StringUtil.isEmpty(qcvo.getDzbzdszlDm())) {
			em6.addText(qcvo.getDzbzdszlDm());
		}

		Element em7 = root.addElement("nsrxx");

		Element em8 = em7.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em8.addText(qcvo.getDjxh());
		}

		Element em9 = em7.addElement("nsrsbh");
		if (!StringUtil.isEmpty(nsrzh)) {
			em9.addText(nsrzh);
		}

		Element em10 = em7.addElement("nsrMc");
		if (!StringUtil.isEmpty(qcvo.getNsrMc())) {
			em10.addText(qcvo.getNsrMc());
		}

		Element em11 = em7.addElement("zgswjDm");
		if (!StringUtil.isEmpty(qcvo.getZgswjDm())) {
			em11.addText(qcvo.getZgswjDm());
		}
		Element em12 = em7.addElement("swjgDm");
		if (!StringUtil.isEmpty(qcvo.getSwjgDm())) {
			em12.addText(qcvo.getSwjgDm());
		}

		return convertToString(document);
	}

	//所得税期初
	public static String createQcXMLNew(String nsrzh, String password, String year, TaxQcQueryVO qcvo) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/gt3nf");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "sbCommonCshReq");
		root.addAttribute("cnName", "");
		root.addAttribute("name", "sbCommonCshReq");
		root.addAttribute("version", "SW5001-" + year);

		Element em2 = root.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em2.addText(qcvo.getDjxh());
		}

		Element em4 = root.addElement("sssqQ");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em4.addText(qcvo.getSssqQ());
		}

		Element em5 = root.addElement("sssqZ");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em5.addText(qcvo.getSssqZ());
		}

		Element em11 = root.addElement("zgswjDm");
		if (!StringUtil.isEmpty(qcvo.getZgswjDm())) {
			em11.addText(qcvo.getZgswjDm());
		}

		Element em12 = root.addElement("yzpzzldm_and");
		if (!StringUtil.isEmpty(qcvo.getYzpzzldm_and())) {
			em12.addText(qcvo.getYzpzzldm_and());
		}

		Element em13 = root.addElement("yzpzzldm_ayjd");
		if (!StringUtil.isEmpty(qcvo.getYzpzzldm_ayjd())) {
			em13.addText(qcvo.getYzpzzldm_ayjd());
		}

		Element em14 = root.addElement("yzpzzldm_bnd");
		if (!StringUtil.isEmpty(qcvo.getYzpzzldm_bnd())) {
			em14.addText(qcvo.getYzpzzldm_bnd());
		}

		Element em15 = root.addElement("yzpzzldm_byjd");
		if (!StringUtil.isEmpty(qcvo.getYzpzzldm_byjd())) {
			em15.addText(qcvo.getYzpzzldm_byjd());
		}

		Element em16 = root.addElement("yzpzzlDm");
		if (!StringUtil.isEmpty(qcvo.getYzpzzlDm())) {
			em16.addText(qcvo.getYzpzzlDm());
		}

		Element em7 = root.addElement("nsrxx");

		Element em8 = em7.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em8.addText(qcvo.getDjxh());
		}

		Element em9 = em7.addElement("nsrsbh");
		if (!StringUtil.isEmpty(nsrzh)) {
			em9.addText(nsrzh);
		}

		Element em10 = em7.addElement("zgswjDm");
		if (!StringUtil.isEmpty(qcvo.getZgswjDm())) {
			em10.addText(qcvo.getZgswjDm());
		}

		Element em21 = em7.addElement("zgswkfjDm");
		if (!StringUtil.isEmpty(qcvo.getZgswkfjDm())) {
			em21.addText(qcvo.getZgswkfjDm());
		}
		return convertToString(document);
	}

	//增值税新期初
	public static String createQcXMLNew1(String nsrzh, String password, String year, TaxQcQueryVO qcvo) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", qcvo.getType());
		root.addAttribute("bbh", "String");
		root.addAttribute("xmlbh", "String");
		root.addAttribute("xmlmc", "String");
		root.addAttribute("xsi:schemaLocation", qcvo.getSchemaLocation());
		
		Element em1 = root.addElement("sbNsrxxJhVO");
		
		Element em2 = em1.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em2.addText(qcvo.getDjxh());
		}

		Element em4 = em1.addElement("skssqq");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em4.addText(qcvo.getSssqQ());
		}

		Element em5 = em1.addElement("skssqz");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em5.addText(qcvo.getSssqZ());
		}

		Element em15 = em1.addElement("sbsxDm1");
		em15.addText("11");
		Element em16 = em1.addElement("yzpzzlDm");
		if (!StringUtil.isEmpty(qcvo.getYzpzzlDm())) {
			em16.addText(qcvo.getYzpzzlDm());
		}

		return convertToString(document);
	}
	


	public static String createSbztXML(String nsrzh, String password, String year, TaxQcQueryVO qcvo) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/gt3nf");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "sbCommonCshReq");
		root.addAttribute("cnName", "");
		root.addAttribute("name", "sbCommonCshReq");
		root.addAttribute("version", "SW5001-" + year);

		Element em2 = root.addElement("yzpzzlDm");
		if (!StringUtil.isEmpty(qcvo.getYzpzzlDm())) {
			em2.addText(qcvo.getYzpzzlDm());
		}

		Element em9 = root.addElement("nsrsbh");
		if (!StringUtil.isEmpty(nsrzh)) {
			em9.addText(nsrzh);
		}

		Element em4 = root.addElement("sssqQ");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em4.addText(qcvo.getSssqQ());
		}

		Element em5 = root.addElement("sssqZ");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em5.addText(qcvo.getSssqZ());
		}

		return convertToString(document);
	}

	public static String createCbXML(String nsrzh, String year, TaxQcQueryVO qcvo, String xml) {

		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("taxML", "http://www.chinatax.gov.cn/dataspec/");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", qcvo.getXmlType());
		root.addAttribute("bbh", "String");
		root.addAttribute("xmlbh", "String");
		root.addAttribute("xmlmc", "String" + year);
		root.addAttribute("xsi:schemaLocation",
				"http://www.chinatax.gov.cn/dataspec/TaxMLBw_HXZG_SB_01341_Request_V1.0.xsd");

		Element em2 = root.addElement("SB100VO");

		Element em21 = em2.addElement("SB100BdxxVO");

		Element em211 = em21.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em211.addText(qcvo.getDjxh());
		}
		Element em212 = em21.addElement("nsrsbh");
		if (!StringUtil.isEmpty(qcvo.getNsrsbh())) {
			em212.addText(qcvo.getDjxh());
		}
		Element em213 = em21.addElement("nsrmc");
		if (!StringUtil.isEmpty(qcvo.getNsrMc())) {
			em213.addText(qcvo.getDjxh());
		}
		Element em214 = em21.addElement("zlbsdlDm");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em214.addText("ZL1001");
		// }
		Element em215 = em21.addElement("zlbsxlDm");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em215.addText("ZL1001003");
		// }
		Element em216 = em21.addElement("zllx");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em216.addText("1");
		// }

		Element em217 = em21.addElement("zlsl");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em217.addText("3");
		// }
		Element em218 = em21.addElement("cjbdxml");
		if (!StringUtil.isEmpty(xml)) {
			em218.addText(xml);
		}
		Element em219 = em21.addElement("dzbzdszlDm");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em219.addText(qcvo.getDjxh());
		// }
		Element em2110 = em21.addElement("SBCjbmxGrid");
		Element em21101 = em2110.addElement("SBCjbmxGridlb");

		Element em211011 = em21101.addElement("djxh");
		if (!StringUtil.isEmpty(qcvo.getDjxh())) {
			em211011.addText(qcvo.getDjxh());
		}

		Element em211012 = em21101.addElement("ssqq");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em211012.addText(qcvo.getSssqQ());
		}

		Element em211013 = em21101.addElement("ssqz");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em211013.addText(qcvo.getSssqZ());
		}

		Element em211014 = em21101.addElement("bszlDm");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em211014.addText("ZLA0610215");
		// }

		Element em211015 = em21101.addElement("zlbscjuuid");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em211015.addText(qcvo.getDjxh());
		// }

		Element em22 = em2.addElement("gzbz");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em22.addText("0");
		// }

		Element em23 = em2.addElement("slswsxDm");
		// if (!StringUtil.isEmpty(qcvo.getDjxh())) {
		// em23.addText("SLSXA061008064");
		// }

		Element em3 = root.addElement("ZlbssldjNsrxxVO");

		Element em31 = em3.addElement("ssqq");
		if (!StringUtil.isEmpty(qcvo.getSssqQ())) {
			em31.addText(qcvo.getSssqQ());
		}

		Element em32 = em3.addElement("ssqz");
		if (!StringUtil.isEmpty(qcvo.getSssqZ())) {
			em32.addText(qcvo.getSssqZ());
		}

		return convertToString(document);
	}

}