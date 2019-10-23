package com.dzf.zxkj.excel.util;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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
}