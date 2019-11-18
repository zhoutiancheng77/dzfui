package com.dzf.zxkj.excel.util;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 如果有特殊格式导出，禁止在修改本类的方法，请复制新的方法修改。
 * 
 * @author dzf
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
@Slf4j
@SuppressWarnings("all")
public class ExportExcel<T> {

	public void exportExcel(Collection<T> dataset, OutputStream out) {
		exportExcel("测试POI导出EXCEL文档", null, null, dataset, out, "yyyy-MM-dd");
	}

	public byte[] exportExcel(String sheetName, String[] headers, String[] fields, Collection<T> dataset,
			OutputStream out) {
		return exportExcel(sheetName, headers, fields, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern) {
		exportExcel("测试POI导出EXCEL文档", headers, fields, dataset, out, pattern);
	}
	
	/**
	 * 表格标题行单元格样式
	 * @author gejw
	 * @time 上午11:09:35
	 * @param workbook
	 * @return
	 */
	private HSSFCellStyle createTitleStyle1(HSSFWorkbook workbook){
	    // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        HSSFFont font = createFont(workbook);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
	}
	
	/**
	 * 创建12号加粗字体
	 * @author gejw
	 * @time 上午11:11:50
	 * @param workbook
	 */
	private HSSFFont createFont(HSSFWorkbook workbook){
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        return font;
	}
	
	/**
	 * 创建普通行单元格样式
	 * @author gejw
	 * @time 上午11:13:14
	 * @param workbook
	 * @return
	 */
	private HSSFCellStyle createCellStyle1(HSSFWorkbook workbook){
	    HSSFCellStyle style = workbook.createCellStyle();
	    style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setBorderBottom(BorderStyle.THIN);
	    style.setBorderLeft(BorderStyle.THIN);
	    style.setBorderRight(BorderStyle.THIN);
	    style.setBorderTop(BorderStyle.THIN);
	    style.setAlignment(HorizontalAlignment.CENTER);//内容左右居中
	    style.setVerticalAlignment(VerticalAlignment.CENTER);//内容上下居中
        //生成普通字体
        HSSFFont font2 = workbook.createFont();
        font2.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font2);
        return style;
	}
	
	/**
	 * 创建普通行单元格样式
	 * 字符型使用样式
	 * @author gejw
	 * @time 上午11:15:57
	 * @param workbook
	 * @return
	 */
	private HSSFCellStyle createCellStyle2(HSSFWorkbook workbook){
        HSSFCellStyle style = createCellStyle1(workbook);
        style.setAlignment(HorizontalAlignment.LEFT);//内容居左
        return style;
    }
	
	/**
	 * 创建普通行单元格样式，数字型使用样式
	 * @author gejw
	 * @time 上午11:19:33
	 * @param workbook
	 * @return
	 */
	private HSSFCellStyle createCellStyle3(HSSFWorkbook workbook){
        HSSFCellStyle style = createCellStyle1(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);//内容居右
        return style;
    }

	/**
	 * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	 * 
	 * @param title
	 *            表格标题名
	 * @param headers
	 *            表格属性列名数组
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	 */
	@SuppressWarnings("unchecked")
	public byte[] exportExcel(String title, String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 15);
		// 生成一个样式
		HSSFCellStyle style = createTitleStyle1(workbook);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = createCellStyle2(workbook);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("dzf");

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			// Field[] fields = t.getClass().getDeclaredFields();
			for (short i = 0; i < fields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				// Field field = fields[i];
				String fieldName = fields[i];
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof DZFBoolean) {
						boolean bValue = ((DZFBoolean) value).booleanValue();
						textValue = "是";
						if (!bValue) {
							textValue = "否";
						}
					} else if (value instanceof DZFDouble) {
						DZFDouble bValue = (DZFDouble) value;
						bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
						textValue = bValue.toString();
						cell.setCellStyle(createCellStyle3(workbook));
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					}
					else {
						// 其它数据类型都当作字符串简单处理
						textValue = value == null ? null : value.toString();
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
							// cell.setCellValue(textValue);
						} else {
							HSSFRichTextString richString = new HSSFRichTextString(textValue);
							HSSFFont font3 = workbook.createFont();
							font3.setColor(HSSFColor.BLUE.index);
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					log.error("Excel导出错误",e);
				} catch (NoSuchMethodException e) {
					log.error("Excel导出错误",e);
				} catch (IllegalArgumentException e) {
					log.error("Excel导出错误",e);
				} catch (IllegalAccessException e) {
					log.error("Excel导出错误",e);
				} catch (InvocationTargetException e) {
					log.error("Excel导出错误",e);
				} finally {
					// 清理资源
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			log.error("Excel导出错误",e);
		}
		return workbook.getBytes();
	}

	/**
	 * 纳税工作台导出
	 * @param title
	 * @param exptitls
	 * @param hbltitls
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @return
	 */
	public byte[] expBsWorkbenchExcel(String title, String[] exptitls, String[] expfieids, String[] hbltitls,
									  Integer[] hblindexs, String[] hbhtitls, Integer[] hbhindexs, JSONArray array, OutputStream out,
									  String pattern, List<String> strslist, List<String> mnylist, List<String> stalist, List<String> taxlist,
									  String corpname, String period) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 5;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			//数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setFillForegroundColor(HSSFColor.WHITE.index);//设置背景颜色
			numsty.setFillPattern(FillPatternType.SOLID_FOREGROUND);//指定单元格的填充信息模式和纯色填充单元
			numsty.setBorderBottom(BorderStyle.THIN);
			numsty.setBorderLeft(BorderStyle.THIN);
			numsty.setBorderRight(BorderStyle.THIN);
			numsty.setBorderTop(BorderStyle.THIN);
			numsty.setAlignment(HorizontalAlignment.CENTER);

			//字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setFillForegroundColor(HSSFColor.WHITE.index);//设置背景颜色
			strsty.setFillPattern(FillPatternType.SOLID_FOREGROUND);//指定单元格的填充信息模式和纯色填充单元
			strsty.setBorderBottom(BorderStyle.THIN);
			strsty.setBorderLeft(BorderStyle.THIN);
			strsty.setBorderRight(BorderStyle.THIN);
			strsty.setBorderTop(BorderStyle.THIN);
			strsty.setAlignment(HorizontalAlignment.LEFT);

			//常量类型样式
			HSSFCellStyle stasty = workbook.createCellStyle();
			stasty.setFillForegroundColor(HSSFColor.WHITE.index);//设置背景颜色
			stasty.setFillPattern(FillPatternType.SOLID_FOREGROUND);//指定单元格的填充信息模式和纯色填充单元
			stasty.setBorderBottom(BorderStyle.THIN);
			stasty.setBorderLeft(BorderStyle.THIN);
			stasty.setBorderRight(BorderStyle.THIN);
			stasty.setBorderTop(BorderStyle.THIN);
			stasty.setAlignment(HorizontalAlignment.CENTER);

			//通用样式（表头）
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);

			// 生成一个字体
			HSSFFont font = workbook.createFont();
			//font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBold(true);//加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			//靠左样式（表头）
			HSSFCellStyle leftstyle = workbook.createCellStyle();
			leftstyle.setAlignment(HorizontalAlignment.LEFT);
			leftstyle.setVerticalAlignment(VerticalAlignment.CENTER);
			leftstyle.setBorderBottom(BorderStyle.THIN);
			leftstyle.setBorderLeft(BorderStyle.THIN);
			leftstyle.setBorderRight(BorderStyle.THIN);
			leftstyle.setBorderTop(BorderStyle.THIN);
			leftstyle.setFont(font);

			//靠右样式（表头）
			HSSFCellStyle rightstyle = workbook.createCellStyle();
			rightstyle.setAlignment(HorizontalAlignment.CENTER);
			rightstyle.setVerticalAlignment(VerticalAlignment.CENTER);
			rightstyle.setBorderBottom(BorderStyle.THIN);
			rightstyle.setBorderLeft(BorderStyle.THIN);
			rightstyle.setBorderRight(BorderStyle.THIN);
			rightstyle.setBorderTop(BorderStyle.THIN);
			rightstyle.setFont(font);

			//大标题样式
			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f  = workbook.createFont();
			f.setFontHeightInPoints((short) 20);//字号
			f.setBold(true);//加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HorizontalAlignment.CENTER);//内容左右居中
			headstyle.setVerticalAlignment(VerticalAlignment.CENTER);//内容上下居中
			headstyle.setBorderBottom(BorderStyle.THIN);
			headstyle.setBorderLeft(BorderStyle.THIN);
			headstyle.setBorderRight(BorderStyle.THIN);
			headstyle.setBorderTop(BorderStyle.THIN);

			int headerlength = exptitls.length;
			int fieldlength = expfieids.length;
			//大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell=rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			CellRangeAddress region = new CellRangeAddress(0, 2,(short) 0,  (short) (fieldlength-1));//合并0~2行
			sheet.addMergedRegion(region);//合并标题

			HSSFRow rowfive = sheet.createRow(index);

			HSSFRow rowthree = sheet.createRow(3);
			HSSFCell cell3_0 = rowthree.createCell(0);
			cell3_0.setCellValue("公司：" + corpname);
			cell3_0.setCellStyle(leftstyle);
			CellRangeAddress reg3_0 = new CellRangeAddress(3, 3,(short) (0),  (short) ((short) (exptitls.length - 1) / 2));
			sheet.addMergedRegion(reg3_0);

			HSSFCell cell3ss = rowthree.createCell(((exptitls.length - 1) / 2) + 1);
			cell3ss.setCellValue("期间：" + period);
			cell3ss.setCellStyle(rightstyle);
			CellRangeAddress reg3_1 = new CellRangeAddress(3, 3,(short) ((short) (((short)exptitls.length - 1) / 2)+1),
					(short) ((short) (exptitls.length - 1)));
			sheet.addMergedRegion(reg3_1);

			HSSFRow rowfour = sheet.createRow(4);
			//合并行标题赋值 begin##############################################
			if(hbhtitls != null && hbhtitls.length > 0){
				int begindex = 0;
				for(int i = 0; i < hbhtitls.length; i++){
					begindex = hbhindexs[i];
					HSSFCell cell = rowfour.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(hbhtitls[i]));
					cell.setCellStyle(style);
					CellRangeAddress reg = new CellRangeAddress(4, 5,(short) (begindex),  (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			//合并行标题赋值 end################################################
			//合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			if(hbltitls != null && hbltitls.length > 0){
				int begindex = 0;
				for(int i = 0; i < hbltitls.length; i++){
					begindex = hblindexs[i];
					HSSFCell cell = rowfour.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(hbltitls[i]));
					cell.setCellStyle(style);
					CellRangeAddress reg = new CellRangeAddress(4, 4,(short) (begindex),  (short) (begindex+2));
					sheet.addMergedRegion(reg);
				}
			}
			//合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			//添加导出行信息
			HSSFRow row2 =null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfive.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(exptitls[i]));
				cell1.setCellStyle(style);
			}

			//合并单元格设置边框
//			setRegionStyle(sheet, region, headstyle);
//			setRegionStyle(sheet, reg3_0, leftstyle);
//			setRegionStyle(sheet, reg3_1, rightstyle);

			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : expfieids) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if(map.get(key) != null){
							if(mnylist != null && mnylist.contains(key)){
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString(),2);
								cell.setCellValue(doublevalue.toString());
							}else if(strslist != null && strslist.contains(key)){
								String value = map.get(key).toString();
								if("period".equals(key)){
									value = value.substring(5);
								}
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}else if(stalist != null && stalist.contains(key)){
								String textValue = String.valueOf(map.get(key));
								textValue = (textValue == null || "N".equals(textValue) || "否".equals(textValue)) ? "×" : "√";
								richString = new HSSFRichTextString(textValue);
								cell.setCellValue(richString);
							}else if(taxlist != null && taxlist.contains(key)){
								String textValue = map.get(key).toString();
								textValue = (textValue == null || "0".equals(textValue)) ? "×" : "√";
								richString = new HSSFRichTextString(textValue);
								cell.setCellValue(richString);
							}
						}else{
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if(mnylist != null && mnylist.contains(key)){
							cell.setCellStyle(numsty);
						}else if((stalist != null && stalist.contains(key)) ||
								(taxlist != null && taxlist.contains(key))){
							cell.setCellStyle(stasty);
						}else{
							cell.setCellStyle(strsty);
						}
						count++;
					} catch (SecurityException e) {
						throw new SecurityException(e);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException(e);
					} catch (Exception e) {
						log.error("文件打印",e);
					} finally {
						// 清理资源
					}
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			log.error("文件打印",e);
		}
		return workbook.getBytes();
	}

	/**
	 * 合并单元格设置边框
	 * @param sheet
	 * @param region
	 * @param cs
	 */
	@SuppressWarnings("deprecation")
	private void setRegionStyle(HSSFSheet sheet, CellRangeAddress region, HSSFCellStyle cs) {
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			HSSFRow row = sheet.getRow(i);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				HSSFCell cell = row.getCell(j);
				cell.setCellStyle(cs);
			}
		}
	}

}