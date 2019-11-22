package com.dzf.zxkj.platform.util.zncs;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 *  银行对账单、销进项导出
 * @author wangzhn
 *
 */
@Slf4j
public class VatExportUtils {
	

	public final static int EXP_JX = 0;//进项导出
	public final static int EXP_XX = 1;//销项导出
	public final static int EXP_DZD= 2;//银行对账单导出

	/**
	 * 导出excel
	 * @param fieldColumn  映射关系
	 * @param array  导出数据
	 * @param out    输出流
	 * @param excelName  导出模板
	 * @param sheetno  sheet页编号
	 * @param startrow 开始行
	 * @param cellrow  开始列
	 * @param exptype  导出类型，进项。。。
	 * @param isExp	      是否导出档案
	 * @param expsheetno1  档案导出到第几页
	 * @param busiList1 导出档案数据
	 * @return
	 * @throws Exception
	 */
	public byte[] exportExcel(Map<Integer, String> fieldColumn, 
			JSONArray array,
			OutputStream out, 
			String excelName,
			int sheetno,
			int startrow, 
			int cellrow,
			int exptype,
			boolean isExp,
			int expsheetno1,
			List<String> busiList1,
			int expsheetno2,
			List<String> busiList2) throws Exception{
		if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
			return null;
		}
		Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + excelName);
		
		InputStream is = null;
		HSSFWorkbook workbook = null;
		ByteArrayOutputStream bos = null;
		try{
			is = exportTemplate.getInputStream();
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(sheetno);

			HSSFRow row = sheet.getRow(cellrow);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

			HSSFRow rowTo = null;

			HSSFCell c1 = null;
			HSSFCell c2 = null;
			Map<String, Object> map = null;
			
			String key = null;
			
			Object obj = null;
			int len = array == null ? 0 : array.size();
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(startrow + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					c2.setCellStyle(c1.getCellStyle());
					if (fieldColumn.containsKey(colindex)) {
						map = (Map<String, Object>) array.get(i);
						key = fieldColumn.get(colindex);
						obj = map.get(key);
						if (obj != null) {
							transferValue(key, obj, c2, exptype);
						} else {
						}
						continue;
					}
				}
			}
			
			if(isExp && busiList1 != null && busiList1.size() > 0){//第一个参照
				sheet = workbook.getSheetAt(expsheetno1);
				row = sheet.getRow(0);
				HSSFRichTextString richString = null;
				for(int i = 0; i < busiList1.size(); i++){
					rowTo = sheet.createRow(i);
					rowTo.setHeight(row.getHeight());
					c1 = row.getCell(0);
					c2 = rowTo.createCell(0);
					c2.setCellStyle(c1.getCellStyle());
					richString = new HSSFRichTextString(busiList1.get(i));
					c2.setCellValue(richString);
				}
			}
			if(isExp && busiList2 != null && busiList2.size() > 0){//第二个参照
				sheet = workbook.getSheetAt(expsheetno2);
				row = sheet.getRow(0);
				HSSFRichTextString richString = null;
				for(int i = 0; i < busiList2.size(); i++){
					rowTo = sheet.createRow(i);
					rowTo.setHeight(row.getHeight());
					c1 = row.getCell(0);
					c2 = rowTo.createCell(0);
					c2.setCellStyle(c1.getCellStyle());
					richString = new HSSFRichTextString(busiList2.get(i));
					c2.setCellValue(richString);
				}
			}
			
			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
		}
	
		return null;
	}
	/**
	 * 导出excel
	 * @param fieldColumn  映射关系
	 * @param array  导出数据
	 * @param out    输出流
	 * @param excelName  导出模板
	 * @param sheetno  sheet页编号
	 * @param startrow 开始行
	 * @param cellrow  开始列
	 * @param exptype  导出类型，进项。。。
	 * @param isExp	      是否导出档案
	 * @param expsheetno1  档案导出到第几页
	 * @param busiList1 导出档案数据
	 * @return
	 * @throws Exception
	 */
	public byte[] exportExcelForXlsx(Map<Integer, String> fieldColumn, 
			JSONArray array,
			OutputStream out, 
			String excelName,
			int sheetno,
			int startrow, 
			int cellrow,
			int exptype,
			boolean isExp,
			int expsheetno1,
			List<String> busiList1,
			int expsheetno2,
			List<String> busiList2) throws Exception{
		if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
			return null;
		}
		Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + excelName);
		
		InputStream is = null;
		XSSFWorkbook workbook = null;
		ByteArrayOutputStream bos = null;
		try{
			is = exportTemplate.getInputStream();
			workbook = new XSSFWorkbook(is);
			XSSFSheet sheet = workbook.getSheetAt(sheetno);
			
			XSSFRow row = sheet.getRow(cellrow);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止
			
			XSSFRow rowTo = null;
			
			XSSFCell c1 = null;
			XSSFCell c2 = null;
			Map<String, Object> map = null;
			
			String key = null;
			
			Object obj = null;
			int len = array == null ? 0 : array.size();
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(startrow + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					//c2.setCellStyle(c1.getCellStyle());
					if (fieldColumn.containsKey(colindex)) {
						map = (Map<String, Object>) array.get(i);
						key = fieldColumn.get(colindex);
						obj = map.get(key);
						if (obj != null) {
							transferValueForXlsx(key, obj, c2, exptype);
						} else {
						}
						continue;
					}
				}
			}
			
			if(isExp && busiList1 != null && busiList1.size() > 0){//第一个参照
				sheet = workbook.getSheetAt(expsheetno1);
				row = sheet.getRow(0);
				XSSFRichTextString richString = null;
				for(int i = 0; i < busiList1.size(); i++){
					rowTo = sheet.createRow(i);
					if(row!=null){
						rowTo.setHeight(row.getHeight());
						c1 = row.getCell(0);
						
						c2.setCellStyle(c1.getCellStyle());
					}
					c2 = rowTo.createCell(0);
					richString = new XSSFRichTextString(busiList1.get(i));
					c2.setCellValue(richString);
				}
			}
			if(isExp && busiList2 != null && busiList2.size() > 0){//第二个参照
				sheet = workbook.getSheetAt(expsheetno2);
				row = sheet.getRow(0);
				XSSFRichTextString richString = null;
				for(int i = 0; i < busiList2.size(); i++){
					rowTo = sheet.createRow(i);
					if(row!=null){
						rowTo.setHeight(row.getHeight());
						c1 = row.getCell(0);
						c2.setCellStyle(c1.getCellStyle());
					}
					
					c2 = rowTo.createCell(0);
					
					richString = new XSSFRichTextString(busiList2.get(i));
					c2.setCellValue(richString);
				}
			}
			
			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
		}
		
		return null;
	}
	
	private void transferValue(String key, 
			Object obj, 
			HSSFCell c2, 
			int expType){
		DZFDouble doublevalue = null;
		HSSFRichTextString richString = null;
		if((EXP_JX == expType || EXP_XX == expType) && ("shjje".equals(key) || "se".equals(key))
				|| (EXP_DZD == expType && ( "yhsyje".equals(key) || "yhzcje".equals(key) || "yfye".equals(key)))){
			doublevalue = new DZFDouble(obj.toString());
			doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
			c2.setCellValue(doublevalue.doubleValue());
			return;
		}
		
		richString = new HSSFRichTextString(obj.toString());
		c2.setCellValue(richString);
		
	}
	private void transferValueForXlsx(String key, 
			Object obj, 
			XSSFCell c2, 
			int expType){
		DZFDouble doublevalue = null;
		XSSFRichTextString richString = null;
		if((EXP_JX == expType || EXP_XX == expType) && ("shjje".equals(key) || "se".equals(key))
				|| (EXP_DZD == expType && ( "yhsyje".equals(key) || "yhzcje".equals(key) || "yfye".equals(key)))){
			doublevalue = new DZFDouble(obj.toString());
			doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
			c2.setCellValue(doublevalue.doubleValue());
			return;
		}
		
		richString = new XSSFRichTextString(obj.toString());
		c2.setCellValue(richString);
		
	}
}
