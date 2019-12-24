package com.dzf.zxkj.platform.util.taxrpt.cqtc;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.tax.cqtc.CqtcRequestVO;
import com.dzf.zxkj.platform.model.tax.cqtc.CqtcSbtzResultVO;
import com.dzf.zxkj.platform.model.tax.cqtc.MessageTypeVO;
import javassist.Modifier;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
@Slf4j
public class CQXMLUtil {
	/**
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public static List<CqtcRequestVO> getResultList(String result) throws Exception {
		try {
			Document document = DocumentHelper.parseText(result);
			List<CqtcRequestVO> list_result = new ArrayList<CqtcRequestVO>();
			Map<String, String> returnMap = isSuccess(document);
			if ("00000000".equals(returnMap.get("return_code"))) {
				List node_list = document.selectNodes("/service/body/data/business_content/business_rerurn/NSRXX");
				List node_list2 = document.selectNodes("/service/body/data/business_content/business_rerurn/CWXX");			
				CqtcRequestVO qcvo = new CqtcRequestVO();
				if(node_list2 !=null && node_list2.size()>0){
					Map<String, Object> cwxx_mapXml = new HashMap<String, Object>();
					element2Map(cwxx_mapXml, (DefaultElement) node_list2.get(0));
					String error_msg = cwxx_mapXml.get("CWXX")==null ? "":cwxx_mapXml.get("CWXX").toString();					
					qcvo.setError_message(error_msg);
					list_result.add(qcvo);
				}			
				
				Map<String, Object> nsr_mapXml = new HashMap<String, Object>();
				for (int i = 0; i < node_list.size(); i++) {
					element2Map(nsr_mapXml, (DefaultElement) node_list.get(i));
					qcvo = (CqtcRequestVO) mapToObject((Map<String, Object>) nsr_mapXml.get("JBXX"),
							CqtcRequestVO.class);
					qcvo.setZzs0(new DZFBoolean(false));
					qcvo.setZzs1(new DZFBoolean(false));
					qcvo.setSds0(new DZFBoolean(false));
					qcvo.setSds1(new DZFBoolean(false));
					// qcvo.setZzsyjb("30");
					String res = nsr_mapXml.get("SBSZ").toString();

					if (!StringUtil.isEmpty(res) && res.indexOf("zzs0") != -1) {
						qcvo.setZzs0(new DZFBoolean(true));
					}
					if (!StringUtil.isEmpty(res) && res.indexOf("zzs1") != -1) {
						qcvo.setZzs1(new DZFBoolean(true));
					}
					if (!StringUtil.isEmpty(res) && res.indexOf("sds0") != -1) {
						qcvo.setSds0(new DZFBoolean(true));
					}
					if (!StringUtil.isEmpty(res) && res.indexOf("sds1") != -1) {
						qcvo.setSds1(new DZFBoolean(true));
					}
					Map<String, Object> qc_xml = (Map<String, Object>) nsr_mapXml.get("QCSJ");
					List new_mapXml = (ArrayList<CqtcRequestVO>) qc_xml.get("list");
					if (new_mapXml != null) {
						Map<String, Object> qc_zzs0 = new HashMap<String, Object>();
						Map<String, Object> qc_zzs1 = new HashMap<String, Object>();
						Map<String, Object> qc_sds0 = new HashMap<String, Object>();
						Map<String, Object> qc_sds1 = new HashMap<String, Object>();
						for (int j = 0; j < new_mapXml.size(); j++) {
							MessageTypeVO qc = (MessageTypeVO) mapToObject((Map<String, Object>) new_mapXml.get(j),
									MessageTypeVO.class);
							if (qc.getCode().startsWith("zzs0")) {
								qc_zzs0.put(qc.getCode(), qc.getValue());
							} else if (qc.getCode().startsWith("zzs1")) {
								qc_zzs1.put(qc.getCode(), qc.getValue());
							} else if (qc.getCode().startsWith("sds0")) {
								qc_sds0.put(qc.getCode(), qc.getValue());
							} else if (qc.getCode().startsWith("sds1")) {
								qc_sds1.put(qc.getCode(), qc.getValue());
							}
						}
						String message_zzs0 = getMessage(qc_zzs0);
						String message_zzs1 = getMessage(qc_zzs1);
						String message_sds0 = getMessage(qc_sds0);
						String message_sds1 = getMessage(qc_sds1);
						qcvo.setMessage_zzs0(message_zzs0);
						qcvo.setMessage_zzs1(message_zzs1);
						qcvo.setMessage_sds0(message_sds0);
						qcvo.setMessage_sds1(message_sds1);
					}
					list_result.add(qcvo);

				}
			} else

			{
				log.error("调用重庆天畅接口失败:" + returnMap.get("return_msg"));
				throw new BusinessException("调用重庆天畅接口失败！");
			}
			return list_result;

		} catch (

		Exception e) {
			log.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

	}

	private static Map<String, String> isSuccess(Document document) throws Exception {
		try {
			Map<String, String> returnMap = new HashMap<String, String>();
			Map<String, Object> code_msg_map = new HashMap<String, Object>();
			List backResult = document.selectNodes("/service/body/data/return_jk");
			CQXMLUtil.element2Map(code_msg_map, (Element) backResult.get(0));
			Map<String, Object> codeMap = (Map<String, Object>) code_msg_map.get("rerurn_code");
			String return_code = codeMap.get("rerurn_code").toString();
			returnMap.put("return_code", return_code);
			Map<String, Object> msgMap = (Map<String, Object>) code_msg_map.get("rerurn_msg");
			String return_msg = msgMap.get("rerurn_msg").toString();
			returnMap.put("return_msg", return_msg);
			return returnMap;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BusinessException("解析重庆接口数据失败：" + e.getMessage());
		}
	}

	private static String getMessage(Map<String, Object> qc_data) {
		if (qc_data.isEmpty()) {
			return null;
		}
//		JSONArray jsonArray = JSONArray.fromObject(qc_data);
//		String result = jsonArray.toString().substring(1, jsonArray.toString().length() - 1);
		String result = JsonUtils.serialize(qc_data);

		return result;
	}

	public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
		if (map == null)
			return null;

		Object obj = beanClass.newInstance();

		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
				continue;
			}

			field.setAccessible(true);
			Map<String, Object> fieldmap = (Map<String, Object>) map.get(field.getName());
			if (fieldmap != null) {
				field.set(obj, fieldmap.get(field.getName()));
			}
		}

		return obj;
	}

	/**
	 * 使用递归调用将多层级xml转为map
	 * 
	 * @param map
	 * @param rootElement
	 */
	public static void element2Map(Map<String, Object> map, Element rootElement) {

		// 获得当前节点的子节点
		List<Element> elements = rootElement.elements();
		if (elements.size() == 0) {
			// 没有子节点说明当前节点是叶子节点，直接取值
			map.put(rootElement.getName(), rootElement.getText());
		} else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，继续递归
			Map<String, Object> tempMap = new HashMap<String, Object>();
			element2Map(tempMap, elements.get(0));
			map.put(rootElement.getName(), tempMap);
		} else {
			// 多个子节点的话就要考虑list的情况了，特别是当多个子节点有名称相同的字段时
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (Element element : elements) {
				tempMap.put(element.getName(), null);
			}
			Set<String> keySet = tempMap.keySet();
			for (String string : keySet) {
				Namespace namespace = elements.get(0).getNamespace();
				List<Element> sameElements = rootElement.elements(new QName(string, namespace));
				// 如果同名的数目大于1则表示要构建list
				if (sameElements.size() > 1) {
					List<Map> list = new ArrayList<Map>();
					for (Element element : sameElements) {
						Map<String, Object> sameTempMap = new HashMap<String, Object>();
						element2Map(sameTempMap, element);
						list.add(sameTempMap);
					}
					map.put(string, list);
				} else {
					// 同名的数量不大于1直接递归
					Map<String, Object> sameTempMap = new HashMap<String, Object>();
					element2Map(sameTempMap, sameElements.get(0));
					map.put(string, sameTempMap);
				}
			}
		}
	}

	public static String createResultXML(String vscode) {

		// 创建Document对象

		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("service");

		Element body = root.addElement("body");
		Element data = body.addElement("data");
		Element business_content = data.addElement("business_content");
		Element business_id = business_content.addElement("business_id");
		business_id.setText("");
		Element business_param = business_content.addElement("business_param");
		business_param.setText("");
		Element business_return = business_content.addElement("business_rerurn");

		Element code = business_return.addElement("code");
		code.setText("111");
		Element msg = business_return.addElement("msg");
		msg.setText("成功");
		for (int i = 1; i <= 5; i++) {
			addElement(business_return, i);
		}

		Element content_control = data.addElement("content_control");
		Element is_zip = content_control.addElement("is_zip");
		is_zip.setText("true");
		Element zip_type = content_control.addElement("zip_type");
		zip_type.setText("Zlib");
		Element is_encrypt = content_control.addElement("is_encrypt");
		is_encrypt.setText("true");
		Element encrypt_type = content_control.addElement("encrypt_type");
		encrypt_type.setText("DES");
		Element is_code = content_control.addElement("is_code");
		is_code.setText("true");
		Element code_type = content_control.addElement("code_type");
		code_type.setText("BASE64");

		Element return_jk = data.addElement("return_jk");
		Element rerurn_code = return_jk.addElement("rerurn_code");
		rerurn_code.setText("00000000");
		Element rerurn_msg = return_jk.addElement("rerurn_msg");
		rerurn_msg.setText("");

		Element transfer_info = data.addElement("transfer_info");
		Element source_id = transfer_info.addElement("source_id");
		source_id.setText("8");
		Element source_name = transfer_info.addElement("source_name");
		source_name.setText("大账房");
		Element send_time = transfer_info.addElement("send_time");
		send_time.setText("");

		Element head = root.addElement("head");

		Element head_code = head.addElement("code");
		head_code.setText("0");
		Element head_msg = head.addElement("msg");
		head_msg.setText("成功");
		Element tran_date = head.addElement("tran_date");
		tran_date.setText("2016-12-28 09:08:18");
		Element tran_time = head.addElement("tran_time");
		tran_time.setText("20161228");

		return CQXMLUtil.convertToString(document);
	}

	public static String createSbztResultXML() {

		// 创建Document对象

		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("service");

		Element body = root.addElement("body");
		Element data = body.addElement("data");
		Element business_content = data.addElement("business_content");
		Element business_id = business_content.addElement("business_id");
		business_id.setText("");
		Element business_param = business_content.addElement("business_param");
		business_param.setText("");
		Element business_return = business_content.addElement("business_return");

		for (int i = 1; i <= 5; i++) {
			addSbztElement(business_return);
		}

		Element content_control = data.addElement("content_control");
		Element is_zip = content_control.addElement("is_zip");
		is_zip.setText("true");
		Element zip_type = content_control.addElement("zip_type");
		zip_type.setText("Zlib");
		Element is_encrypt = content_control.addElement("is_encrypt");
		is_encrypt.setText("true");
		Element encrypt_type = content_control.addElement("encrypt_type");
		encrypt_type.setText("DES");
		Element is_code = content_control.addElement("is_code");
		is_code.setText("true");
		Element code_type = content_control.addElement("code_type");
		code_type.setText("BASE64");

		Element return_jk = data.addElement("return_jk");
		Element rerurn_code = return_jk.addElement("rerurn_code");
		rerurn_code.setText("00000000");
		Element rerurn_msg = return_jk.addElement("rerurn_msg");
		rerurn_msg.setText("接口执行成功");

		Element transfer_info = data.addElement("transfer_info");
		Element source_id = transfer_info.addElement("source_id");
		source_id.setText("8");
		Element source_name = transfer_info.addElement("source_name");
		source_name.setText("大账房");

		Element head = root.addElement("head");

		Element head_code = head.addElement("code");
		head_code.setText("0");
		Element head_msg = head.addElement("msg");
		head_msg.setText("成功");
		Element tran_date = head.addElement("tran_date");
		tran_date.setText("2016-12-28 09:08:18");
		Element tran_time = head.addElement("tran_time");
		tran_time.setText("20171222");

		return CQXMLUtil.convertToString(document);
	}

	public static String createQcXML(String vscode) {

		// 创建Document对象

		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("service");

		Element transfer_info = root.addElement("transfer_info");

		Element source_id = transfer_info.addElement("source_id");
		source_id.setText("8");
		Element source_name = transfer_info.addElement("source_name");
		source_name.setText("大账房");
		Element send_time = transfer_info.addElement("send_time");
		send_time.setText(new DZFDateTime().toString());

		Element content_control = root.addElement("content_control");
		Element is_zip = content_control.addElement("is_zip");
		is_zip.setText("true");
		Element zip_type = content_control.addElement("zip_type");
		zip_type.setText("Zlib");
		Element is_encrypt = content_control.addElement("is_encrypt");
		is_encrypt.setText("true");
		Element encrypt_type = content_control.addElement("encrypt_type");
		encrypt_type.setText("DES");
		Element is_code = content_control.addElement("is_code");
		is_code.setText("true");
		Element code_type = content_control.addElement("code_type");
		code_type.setText("BASE64");

		Element business_content = root.addElement("business_content");
		Element business_id = business_content.addElement("business_id");
		business_id.setText("serviceFW_PT_DZF.getDzf_007");
		Element business_param = business_content.addElement("business_param");
		Element period = business_param.addElement("PERIOD");		
		period.setText(new DZFDate(new DZFDate().toString().substring(0, 7) + "-01").getDateBefore(1).toString().substring(0, 7).replaceAll("-", ""));
		//period.setText("201802");
		Element taxno = business_param.addElement("taxno");
		taxno.setText(vscode);

		Element business_return = business_content.addElement("business_return");
		business_return.setText("");
		Element return_jk = root.addElement("return_jk");
		Element rerurn_code = return_jk.addElement("rerurn_code");
		rerurn_code.setText("");
		Element rerurn_msg = return_jk.addElement("rerurn_msg");
		rerurn_msg.setText("");

		return convertToString(document);
	}

	public static String createSbztReqXML(String vscodes) {

		// 创建Document对象

		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("service");

		Element transfer_info = root.addElement("transfer_info");

		Element source_id = transfer_info.addElement("source_id");
		source_id.setText("8");
		Element source_name = transfer_info.addElement("source_name");
		source_name.setText("大账房");
		Element send_time = transfer_info.addElement("send_time");
		send_time.setText(new DZFDateTime().toString());

		Element content_control = root.addElement("content_control");
		Element is_zip = content_control.addElement("is_zip");
		is_zip.setText("true");
		Element zip_type = content_control.addElement("zip_type");
		zip_type.setText("Zlib");
		Element is_encrypt = content_control.addElement("is_encrypt");
		is_encrypt.setText("true");
		Element encrypt_type = content_control.addElement("encrypt_type");
		encrypt_type.setText("DES");
		Element is_code = content_control.addElement("is_code");
		is_code.setText("true");
		Element code_type = content_control.addElement("code_type");
		code_type.setText("BASE64");

		Element business_content = root.addElement("business_content");
		Element business_id = business_content.addElement("business_id");
		business_id.setText("serviceFW_PT_DZF.getSbjg_2002");
		Element business_param = business_content.addElement("business_param");
		
		Element taxno = business_param.addElement("taxno");
		taxno.setText(vscodes);
		/*
		 * Element taxno = business_param.addElement("taxno");
		 * taxno.setText("91500000066150829B");
		 */
		Element business_return = business_content.addElement("business_return");
		business_return.setText("");
		return convertToString(document);
	}

	
	public static String createSbztReqXML_OtherCHN(String vscodes,String djxh) {

		//如果没有等级序号，直接返回
		if(StringUtils.isEmpty(djxh)){
			return null;
		}
		
		// 创建Document对象

		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("service");

		Element transfer_info = root.addElement("transfer_info");

		Element source_id = transfer_info.addElement("source_id");
		source_id.setText("8");
		Element source_name = transfer_info.addElement("source_name");
		source_name.setText("大账房");
		Element send_time = transfer_info.addElement("send_time");
		send_time.setText(new DZFDateTime().toString());

		Element content_control = root.addElement("content_control");
		Element is_zip = content_control.addElement("is_zip");
		is_zip.setText("true");
		Element zip_type = content_control.addElement("zip_type");
		zip_type.setText("Zlib");
		Element is_encrypt = content_control.addElement("is_encrypt");
		is_encrypt.setText("true");
		Element encrypt_type = content_control.addElement("encrypt_type");
		encrypt_type.setText("DES");
		Element is_code = content_control.addElement("is_code");
		is_code.setText("true");
		Element code_type = content_control.addElement("code_type");
		code_type.setText("BASE64");

		Element business_content = root.addElement("business_content");
		Element business_id = business_content.addElement("business_id");
		business_id.setText("serviceFW_PT_DZF.getSbjg_1031");
		Element business_param = business_content.addElement("business_param");
		Element djxhe = business_param.addElement("DJXH");
		djxhe.setText(djxh);
		Element taxno = business_param.addElement("taxno");
		taxno.setText(vscodes);
		/*
		 * Element taxno = business_param.addElement("taxno");
		 * taxno.setText("91500000066150829B");
		 */
		Element business_return = business_content.addElement("business_return");
		business_return.setText("");
		return convertToString(document);
	}
	
	public static List<CqtcSbtzResultVO> getSbztResultList(String result) throws Exception {
		try {
			Document document = DocumentHelper.parseText(result);
			List<CqtcSbtzResultVO> sbztList = new ArrayList<CqtcSbtzResultVO>();
			Map<String, String> returnMap = isSuccess(document);
			if ("00000000".equals(returnMap.get("return_code"))) {// 成功
				List CWXX =document.selectNodes("/service/body/data/business_content/business_rerurn/CWXX");
				if(CWXX.size()!=0){
					log.error("调用重庆天畅接口失败: 60秒内只能调用一次");
					throw new BusinessException("调用重庆天畅接口失败！ 60秒内只能调用一次！");
				}
				
						
				List node_list = document.selectNodes("/service/body/data/business_content/business_rerurn/NSRSBJG");
				Map<String, Object> sbzt_map = new HashMap<String, Object>();
				Map<String, Object> sbxx_map = new HashMap<String, Object>();
				for (int i = 0; i < node_list.size(); i++) {
					CQXMLUtil.element2Map(sbzt_map, (Element) node_list.get(i));
					Map<String, Object> taxnoMap = (Map<String, Object>) sbzt_map.get("taxno");
					if (taxnoMap != null) {
						String taxno = taxnoMap.get("taxno").toString(); // 纳税人识别号
						if (sbzt_map.get("SBXX") != null) {
							//多个税种
							if(sbzt_map.get("SBXX") instanceof ArrayList){
								List SBXXList = (ArrayList<CqtcSbtzResultVO>) sbzt_map.get("SBXX");
								for(int j = 0; j < SBXXList.size(); j++){
									CqtcSbtzResultVO vo = (CqtcSbtzResultVO) mapToObject(
											(Map<String, Object>) SBXXList.get(j), CqtcSbtzResultVO.class);
								
									vo.setTaxno(taxno);
									sbztList.add(vo);
								}
							}else{
								CqtcSbtzResultVO vo = (CqtcSbtzResultVO) mapToObject(	
										(Map<String, Object>) sbzt_map.get("SBXX"), CqtcSbtzResultVO.class);
								vo.setTaxno(taxno);
								sbztList.add(vo);
							}

						}
					}
				}
			} else {
				log.error("调用重庆天畅接口失败:" + returnMap.get("rerurn_msg"));
				throw new BusinessException("调用重庆天畅接口失败！");
			}
			return sbztList;
		} catch (Exception e) {
			throw new BusinessException("解析重庆天畅接口数据失败:" + e.getMessage());
		}

	}

	private static Element addElement(Element business_rerurn, int i) {

		Element nsrxx = business_rerurn.addElement("NSRXX");
		nsrxx.setText("");

		Element jbxx = nsrxx.addElement("JBXX");

		Element taxno = jbxx.addElement("taxno");
		taxno.setText("1");
		Element taxname = jbxx.addElement("taxname");
		taxname.setText("2");
		Element fr = jbxx.addElement("fr");
		fr.setText("3");
		Element hy = jbxx.addElement("hy");
		hy.setText("4");
		Element zcdz = jbxx.addElement("zcdz");
		zcdz.setText("5");
		Element nsrzg = jbxx.addElement("nsrzg");
		nsrzg.setText("6");
		Element kjzd = jbxx.addElement("kjzd");
		kjzd.setText("7");
		Element dh = jbxx.addElement("dh");
		dh.setText("8");
		Element kysj = jbxx.addElement("kysj");
		kysj.setText("9");
		Element yylx = jbxx.addElement("yylx");
		yylx.setText("10");
		Element zzsyjb = jbxx.addElement("zzsyjb");
		zzsyjb.setText("11");
		Element sdszs = jbxx.addElement("sdszs");
		sdszs.setText("12");
		Element sdsyjb = jbxx.addElement("sdsyjb");
		sdsyjb.setText("13");

		Element sbsz = nsrxx.addElement("sbsz");
		Element szdm1 = sbsz.addElement("szdm");
		szdm1.setText("zzs0");
		Element szdm2 = sbsz.addElement("szdm");
		szdm2.setText("sds0");

		Element qcsj = nsrxx.addElement("QCSJ");
		Element list1 = qcsj.addElement("list");
		Element qc_code1 = list1.addElement("code");
		qc_code1.setText("zzs1_t0_1_3");
		Element value1 = list1.addElement("value");
		value1.setText("20000" + i);

		Element list2 = qcsj.addElement("list");
		Element qc_code2 = list2.addElement("code");
		qc_code2.setText("zzs1_t0_1_4");
		Element value2 = list2.addElement("value");
		value2.setText("20000" + i);

		Element list3 = qcsj.addElement("list");
		Element qc_code3 = list3.addElement("code");
		qc_code3.setText("zzs1_t0_12_3");
		Element value3 = list3.addElement("value");
		value3.setText("10000" + i);
		return nsrxx;
	}

	private static void addSbztElement(Element business_rerurn) {

		Element NSRSBJG = business_rerurn.addElement("NSRSBJG");

		Element taxno = NSRSBJG.addElement("taxno");
		taxno.setText("91500000066150829B");

		Element SBXX1 = NSRSBJG.addElement("SBXX");
		Element note1 = SBXX1.addElement("note");
		note1.setText("【核心征管反馈】：(S_1)申报成功");
		Element sbzt1 = SBXX1.addElement("sbzt");
		sbzt1.setText("2");
		Element szdm1 = SBXX1.addElement("szdm");
		szdm1.setText("zzs0");

		Element SBXX = NSRSBJG.addElement("SBXX");
		Element note = SBXX.addElement("note");
		note.setText("【核心征管反馈】：(S_2)申报成功");
		Element sbzt = SBXX.addElement("sbzt");
		sbzt.setText("2");
		Element szdm = SBXX.addElement("szdm");
		szdm.setText("zzs1");

	}

	public static String convertToString(Document document) {

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
			log.error(e.getMessage());
		} finally {
			// 关闭
			try {
				xmlWriter.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			} finally {
			}
		}
		// 输出xml
		String xml = stringWriter.toString();
		xml = xml.replaceAll("\n", "");
		//System.out.println(xml);
		return xml;
	}
}
