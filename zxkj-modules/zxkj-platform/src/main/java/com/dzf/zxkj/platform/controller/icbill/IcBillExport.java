package com.dzf.zxkj.platform.controller.icbill;

import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class IcBillExport {
	/**
	 * 
	 * @author 王敏
	 * @param aggvos
	 * @param out
	 * @param itype
	 *            0 --- 出库 1 ----入库 2 --- 出库（老模式） 3 ---入库（老模式）
	 * @return
	 * @throws Exception
	 */
	public byte[] exportExcel(AggIcTradeVO[] aggvos, OutputStream out, int itype, boolean isexp,
							  Map<String, Integer> preMap) throws Exception {

		String excelName = null;
		Map<Integer, String> fieldColumn = null;
		switch (itype) {
		case 0:
			fieldColumn = IntradeoutVO.getExcelFieldColumn();
			excelName = "icoutbill.xls";
			break;
		case 1:
			fieldColumn = IctradeinVO.getExcelFieldColumn();
			excelName = "icinbill.xls";
			break;
		case 2:
			fieldColumn = IntradeoutVO.getExcelFieldColumnGL2IC();
			excelName = "icoutbillgl2ic.xls";
			break;
		case 3:
			fieldColumn = IctradeinVO.getExcelFieldColumnGL2IC();
			excelName = "icinbillgl2ic.xls";
			break;
		}

		int startrow = 1;
		int cellrow = 1;

		return exportExcelNSSB1(fieldColumn, aggvos, out, excelName, startrow, cellrow, isexp, preMap);
	}

	private byte[] exportExcelNSSB1(Map<Integer, String> fieldColumn, AggIcTradeVO[] aggvos, OutputStream out,
			String excelName, int startrow, int cellrow, boolean isexp, Map<String, Integer> preMap)
			throws IOException {
		//
		if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
			return null;
		}

		// 精度
		Integer num = preMap.get(IParameterConstants.DZF009);// 数量
		Integer price = preMap.get(IParameterConstants.DZF010);// 单价
		Map<Integer, Short> styleMap = new HashMap<Integer, Short>();

		Resource exportTemplate = new ClassPathResource("template/report/taxdeclaration/" + excelName);
		InputStream is = null;
		HSSFWorkbook workbook = null;
		ByteArrayOutputStream bos = null;
		try {
			is = exportTemplate.getInputStream();
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
			int len = aggvos == null ? 0 : aggvos.length;
			// 导出的时候设置 公司 日期 单据号
			if (isexp) {
				row = sheet.getRow(startrow);
				AggIcTradeVO aggvo = aggvos[0];
				if (aggvo != null){
					for (int colindex = minindex; colindex < maxindex; colindex++) {
						c2 = row.getCell(colindex);
						if (fieldColumn.containsKey(colindex)) {
							key = fieldColumn.get(colindex);
							if (aggvo.getAttributeValue(key) != null) {
								if (key.equals("nnum") || key.equals("nprice") || key.equals("nymny") || key.equals("ntax")
										|| key.equals("ntaxmny") || key.equals("vdef1") || key.equals("ncost")|| key.equals("ntotaltaxmny")) {
									doublevalue = new DZFDouble(aggvo.getAttributeValue(key).toString());
									if (key.equals("vdef1") || key.equals("nprice")) {
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
			} else {
				// 数据行
				for (int i = 0; i < len; i++) {
					rowTo = sheet.createRow(startrow + i);
					rowTo.setHeight(row.getHeight());
					AggIcTradeVO aggvo = aggvos[i];
					if (aggvo == null)
						continue;
					for (int colindex = minindex; colindex < maxindex; colindex++) {
						c1 = row.getCell(colindex);
						c2 = rowTo.createCell(colindex);
						c2.setCellStyle(c1.getCellStyle());
						if (fieldColumn.containsKey(colindex)) {
							key = fieldColumn.get(colindex);
							if (aggvo.getAttributeValue(key) != null) {
								if (key.equals("nnum") || key.equals("nprice") || key.equals("nymny")
										|| key.equals("ntax") || key.equals("ntaxmny") || key.equals("vdef1")
										|| key.equals("ncost")|| key.equals("ntotaltaxmny")) {
									doublevalue = new DZFDouble(aggvo.getAttributeValue(key).toString());
									if (key.equals("vdef1") || key.equals("nprice")) {
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

			// if (out != null) {
			// try {
			// out.close();
			// } catch (IOException e) {
			// log.error(e.getMessage(), e);
			// }
			// }
			//
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
