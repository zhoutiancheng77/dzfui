
package com.dzf.zxkj.platform.util.taxrpt.shandong.deal;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ParseXml {

	public ParseXml() {
	}

//	static final Logger log = Logger.getLogger(ParseXml.class);

	public static String readTxtFile(String filepath) {
		String returnStr = "";
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		FileInputStream in = null;
		String encoding = "GBK";
		File file = new File(filepath);

		if (file.isFile() && file.exists()) {
			try {
				in =new FileInputStream(file);
				read = new InputStreamReader(in, encoding);
				bufferedReader = new BufferedReader(read);
				for (String lineTxt = null; (lineTxt = bufferedReader.readLine()) != null;) {
					returnStr = (new StringBuilder(String.valueOf(returnStr))).append(lineTxt).toString();
				}
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				throw new BusinessException(e.getMessage());
			} catch (IOException e) {
				throw new BusinessException(e.getMessage());
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						log.error(e.getMessage(),e);
					}
				}

				if (read != null) {
					try {
						read.close();
					} catch (IOException e) {
						log.error(e.getMessage(),e);
					}
				}
				
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
		return returnStr;
	}

	public static HashMap<String, String> readXml(String xmlStr, HashMap<String, String> hm) {
		// xmlStr = xmlStr.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		xmlStr = xmlStr.replace("\n", "").replaceAll("    ", "");
		Document document;
		try {
			document = DocumentHelper.parseText(xmlStr);
			Element root = document.getRootElement();
			getValues((DefaultElement) root, hm);
		} catch (DocumentException e) {
			throw new BusinessException(e.getMessage());
		}
		return hm;
	}

	private static void getValues(DefaultElement root, HashMap<String, String> hm) throws DocumentException {

		List elementList = root.content();
		if (elementList == null || elementList.size() == 0)
			return;

		String name = root.getName();
		if (name.equals("body")) {// 返回值 body信息
			StringBuffer strb = new StringBuffer();
			for (Iterator it = elementList.iterator(); it.hasNext();) {
				Object item = it.next();
				if (item instanceof DefaultText) {
					String value = ((DefaultText) item).getText();
					if (!StringUtil.isEmpty(value)) {
						strb.append(value);
					}
				}
			}
			if (!StringUtil.isEmpty(strb.toString())) {
				// ht.put(name, strb.toString());
				String  xml =strb.toString().trim();
				if(!StringUtil.isEmpty(xml)){
					Document document = DocumentHelper.parseText(strb.toString());
					Element root1 = document.getRootElement();
					getValues((DefaultElement) root1, hm);
				}
			}
		} else {
			for (Iterator it = elementList.iterator(); it.hasNext();) {
				Object item = it.next();

				if (item instanceof DefaultElement) {
					getValues(((DefaultElement) item), hm);
				} else if (item instanceof DefaultText) {
					String value = ((DefaultText) item).getText();
					if (!StringUtil.isEmpty(value)) {
						hm.put(name, value);
					}
				} else if (item instanceof DefaultCDATA) {
					String value = ((DefaultCDATA) item).getText();
					Document document = DocumentHelper.parseText(value);
					Element root1 = document.getRootElement();
					getValues((DefaultElement) root1, hm);
				}
			}
		}
	}

	public static String getBodyXml(String xmlStr) {
		// xmlStr = xmlStr.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		xmlStr = xmlStr.replace("\n", "").replaceAll("    ", "");
//		System.out.println(xmlStr);
		Document document = null;
		String bodyxml = null;
		try {
			document = DocumentHelper.parseText(xmlStr);
			Element root = document.getRootElement();
			bodyxml = getBodyDocument((DefaultElement) root);
		} catch (DocumentException e) {
			throw new BusinessException(e.getMessage());
		}
		return bodyxml;
	}

	private static String getBodyDocument(DefaultElement root) throws DocumentException {

		String bodyxml = null;
		List elementList = root.content();
		if (elementList == null || elementList.size() == 0)
			return null;

		String name = root.getName();
		if (name.equals("body")) {// 返回值 body信息
			StringBuffer strb = new StringBuffer();
			for (Iterator it = elementList.iterator(); it.hasNext();) {
				Object item = it.next();
				if (item instanceof DefaultText) {
					String value = ((DefaultText) item).getText();
					if (!StringUtil.isEmpty(value)) {
						strb.append(value);
					}
				}
			}
			if (!StringUtil.isEmpty(strb.toString())) {
				return strb.toString();
			}
		} else {
			for (Iterator it = elementList.iterator(); it.hasNext();) {
				Object item = it.next();

				if (item instanceof DefaultElement) {
					bodyxml = getBodyDocument(((DefaultElement) item));
				} else if (item instanceof DefaultCDATA) {
					String value = ((DefaultCDATA) item).getText();
					Document document = DocumentHelper.parseText(value);
					Element root1 = document.getRootElement();
					bodyxml = getBodyDocument((DefaultElement) root1);
				}
				
				if(!StringUtil.isEmpty(bodyxml)){
					break;
				}
			}
		}
		return bodyxml;
	}

	public static Element getElementByName(DefaultElement root, String sname) throws DocumentException {
		Element elem = null;
		List elementList = root.content();
		if (elementList == null || elementList.size() == 0)
			return null;

		String name = root.getName();
		if (name.equals(sname)) {// 返回值 body信息
			elem =root;
		} else {
			for (Iterator it = elementList.iterator(); it.hasNext();) {
				Object item = it.next();
				if (item instanceof DefaultElement) {
					elem = getElementByName(((DefaultElement) item), sname);
				} else if (item instanceof DefaultCDATA) {
					String value = ((DefaultCDATA) item).getText();
					Document document = DocumentHelper.parseText(value);
					Element root1 = document.getRootElement();
					elem = getElementByName((DefaultElement) root1, sname);
				}
				if(elem != null){
					break;
				}
			}
		}
		return elem;
	}

	public static String getValueByName(DefaultElement root, String sname) throws DocumentException {

		String text = null;
		List elementList = root.content();
		if (elementList == null || elementList.size() == 0)
			return null;

		String name = root.getName();

		for (Iterator it = elementList.iterator(); it.hasNext();) {
			Object item = it.next();

			if (item instanceof DefaultElement) {
				text = getValueByName(((DefaultElement) item), sname);
			} else if (item instanceof DefaultText) {
				String value = ((DefaultText) item).getText();
				if (!StringUtil.isEmpty(value)) {
					if (name.equalsIgnoreCase(sname)) {
						return value;
					}
				}
			} else if (item instanceof DefaultCDATA) {
				String value = ((DefaultCDATA) item).getText();
				Document document = DocumentHelper.parseText(value);
				Element root1 = document.getRootElement();
				text = getValueByName((DefaultElement) root1, sname);
			}
			
			if(!StringUtil.isEmpty(text))
				break;
		}
		return text;
	}

}
