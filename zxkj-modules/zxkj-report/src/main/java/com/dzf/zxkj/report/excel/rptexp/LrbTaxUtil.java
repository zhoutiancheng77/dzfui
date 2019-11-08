package com.dzf.zxkj.report.excel.rptexp;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.LrbVO;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


@Deprecated
public class LrbTaxUtil {

	/**
	 * 用DOM写XML文档，把学生信息以XML文档的形式存储
	 * 
	 * @param outFile
	 *            输出XML文档的路径
	 * @param studentGeans
	 *            学生信息
	 * @throws Exception
	 */
	public static Document writeLrbXMLFile(LrbVO[] lrbvo) throws Exception {
		// 新建一个空文档
		Document doc = null;
		doc = DocumentHelper.createDocument();

		// 下面是建立XML文档内容的过程.
		// 先建立根元素"学生花名册"，并添加到文档中
		Element root = doc.addElement("taxML");
		root.addAttribute("xmlns", "http://www.chinatax.gov.cn/dataspec/");
		root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute("xsi:type", "xqyRequest");
		root.addAttribute("cnName", "String");
		root.addAttribute("name", "xqyRequest");
		root.addAttribute("version", "1.0");
		// 创建基础数据
		Element xqycwbbxx = root.addElement("xqycwbbxx");
		xqycwbbxx.addAttribute("cnName", "String");
		xqycwbbxx.addAttribute("name", "String");
		xqycwbbxx.addAttribute("version", "1.0");

		// 创建bblx
		Element bblx = root.addElement("bblx");
		bblx.setText("01");

		// 创建syxqyzcfzb
		Element syxqylrb = root.addElement("syxqylrb");
		// 创建syxqyzcfzbGrid
		Element syxqylrbGrid = syxqylrb.addElement("syxqylrbGrid");

		// 取学生信息的Bean列表
		String[] contents = new String[] { "ewbhxh", "hmc", "bnljje", "byje" };
		for (int i = 0; i < lrbvo.length; i++) {
			// 依次取每个学生的信息
			LrbVO zcfzbvo = (LrbVO) lrbvo[i];
			if ("补充资料".equals(zcfzbvo.getXm())) {
				break;
			}
			Element syxqylrbGridlb = syxqylrbGrid.addElement("syxqylrbGridlb");
			// 创建具体内容
			for (String str : contents) {
				Element ele_str = syxqylrbGridlb.addElement(str);
				String value = "";
				if (str.equals("ewbhxh")) {
					value = (i + 1) + "";
				} else if (str.equals("hmc")) {
					value = zcfzbvo.getXm();
					if (!StringUtil.isEmpty(value)) {
						value = value.replace("　", "");
					}
				} else if (str.equals("bnljje")) {
					value = zcfzbvo.getBnljje() == null ? "0"
							: zcfzbvo.getBnljje().setScale(2, DZFDouble.ROUND_HALF_UP).toString();
				} else if (str.equals("byje")) {
					value = zcfzbvo.getByje() == null ? "0"
							: zcfzbvo.getByje().setScale(2, DZFDouble.ROUND_HALF_UP).toString();
				}
				if (!StringUtil.isEmpty(value)) {
					ele_str.addText(value);
				}
			}
		}

		return doc;
	}
}
