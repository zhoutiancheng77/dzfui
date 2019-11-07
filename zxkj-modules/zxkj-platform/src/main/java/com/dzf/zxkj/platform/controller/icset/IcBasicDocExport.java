package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IcBasicDocExport {
	/**
	 * 
	 * @param vos
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public byte[] exportExcel(HttpSession session, SuperVO[] vos, OutputStream out, Map<String, Integer> preMap)
			throws Exception {

		if (vos == null || vos.length == 0) {
			throw new BusinessException("导出数据异常,请修改!");
		}
		String excelName = null;
		Map<Integer, String> fieldColumn = null;
		if (vos[0] instanceof InventoryVO) {
			fieldColumn = getInvExcelFieldColumn();
			excelName = "shangpin.xls";

		} else if (vos[0] instanceof IcbalanceVO) {
			fieldColumn = getQcExcelFieldColumn();
			excelName = "kucunqichu.xls";
		}
		int startrow = 1;
		int cellrow = 1;
		return exportExcelNSSB1(session, fieldColumn, vos, out, excelName, startrow, cellrow, preMap);
	}

	private Map<Integer, String> getInvExcelFieldColumn() {
		Map<Integer, String> fieldColumn = new HashMap<Integer, String>();
		fieldColumn.put(0, "kmcode");
		fieldColumn.put(1, "kmname");
		fieldColumn.put(2, "code");
		fieldColumn.put(3, "name");
		fieldColumn.put(4, "shortname");
		fieldColumn.put(5, "invclassname");
		fieldColumn.put(6, "invspec");
//		fieldColumn.put(7, "invtype");
		fieldColumn.put(7, "measurename");
		fieldColumn.put(8, "jsprice");
		fieldColumn.put(9, "memo");
		return fieldColumn;
	}

	private Map<Integer, String> getQcExcelFieldColumn() {
		Map<Integer, String> fieldColumn = new HashMap<Integer, String>();
		fieldColumn.put(0, "inventoryname");
		fieldColumn.put(1, "invspec");
//		fieldColumn.put(2, "invtype");
		fieldColumn.put(2, "measurename");
		fieldColumn.put(3, "pk_subjectcode");
		fieldColumn.put(4, "pk_subjectname");
		fieldColumn.put(5, "inventorytype");
		fieldColumn.put(6, "nnum");
		fieldColumn.put(7, "ncost");
		fieldColumn.put(8, "memo");
		return fieldColumn;
	}

	private byte[] exportExcelNSSB1(HttpSession session, Map<Integer, String> fieldColumn, SuperVO[] vos,
			OutputStream out, String excelName, int startrow, int cellrow, Map<String, Integer> preMap)
			throws IOException {
		//
		if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
			return null;
		}
		// 精度
		Integer num = preMap.get(IParameterConstants.DZF009);// 数量
		Integer price = preMap.get(IParameterConstants.DZF010);// 单价
		Map<Integer, Short> styleMap = new HashMap<Integer, Short>();

		InputStream is = null;
		HSSFWorkbook workbook = null;
		ByteArrayOutputStream bos = null;
		try {

			String filepath = session.getServletContext().getRealPath("/") + "files/template/" + excelName;
			File file = new File(filepath);
			is = new FileInputStream(file);
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);

			HSSFRow row = sheet.getRow(cellrow);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

			HSSFRow rowTo = null;

			HSSFCell c1 = null;
			HSSFCell c2 = null;
			HSSFRichTextString richString = null;
			String key = null;
			DZFDouble doublevalue = null;
			int len = vos == null ? 0 : vos.length;
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(startrow + i);
				rowTo.setHeight(row.getHeight());
				SuperVO aggvo = vos[i];
				if (aggvo == null)
					continue;
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					c2.setCellStyle(c1.getCellStyle());
					if (fieldColumn.containsKey(colindex)) {
						key = fieldColumn.get(colindex);
						if (aggvo.getAttributeValue(key) != null) {
							if (key.equals("nnum") || key.equals("nprice") || key.equals("jsprice")
									|| key.equals("nymny") || key.equals("ntax") || key.equals("ntaxmny")
									|| key.equals("ncost")) {
								doublevalue = new DZFDouble(aggvo.getAttributeValue(key).toString());
								if (key.equals("nprice") || key.equals("jsprice")) {
									doublevalue = doublevalue.setScale(price, DZFDouble.ROUND_HALF_UP);
									resetCellStyle(styleMap, workbook, c2.getCellStyle(), price);
								} else if (key.equals("nnum")) {
									doublevalue = doublevalue.setScale(num, DZFDouble.ROUND_HALF_UP);
									resetCellStyle(styleMap, workbook, c2.getCellStyle(), num);
								} else {
									doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								}

								c2.setCellValue(doublevalue.doubleValue());
							} else {
								richString = new HSSFRichTextString(aggvo.getAttributeValue(key).toString());
								c2.setCellValue(richString);
							}
						} else {
						}
						continue;
					}
				}
			}

			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

		}
	}

	private void resetCellStyle(Map<Integer, Short> styleMap, HSSFWorkbook workbook, HSSFCellStyle cellStyle,
			int digit) {

		if (!styleMap.containsKey(digit)) {
			HSSFDataFormat fmt = workbook.createDataFormat();
			String style = "#,##0";
			for (int i = 0; i < digit; i++) {
				if (i == 0)
					style = (new StringBuilder(String.valueOf(style))).append(".").toString();
				style = (new StringBuilder(String.valueOf(style))).append("0").toString();
			}

			cellStyle.setDataFormat(fmt.getFormat(style));

			styleMap.put(digit, fmt.getFormat(style));
		} else {
			cellStyle.setDataFormat(styleMap.get(digit));
		}

	}
}
