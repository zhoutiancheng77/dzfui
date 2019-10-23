package com.dzf.zxkj.excel.util;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户导出
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
public class ExportArrayExcel<T> {

	public void exportExcel(Collection<T> dataset, OutputStream out) {
		exportExcel("测试POI导出EXCEL文档", null, null, dataset, out, "yyyy-MM-dd", null, null);
	}

	public byte[] exportExcel(String sheetName, String[] headers, String[] fields, Collection<T> dataset,
			OutputStream out, List<String> arrayList, Map<String,String[]> arrayMap) {
		return exportExcel(sheetName, headers, fields, dataset, out, "yyyy-MM-dd", arrayList, arrayMap);
	}

	public void exportExcel(String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern) {
		exportExcel("测试POI导出EXCEL文档", headers, fields, dataset, out, pattern, null, null);
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
			String pattern, List<String> arrayList, Map<String,String[]> arrayMap) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 15);
		// 生成一个样式
		HSSFCellStyle style = getCellStyle(workbook);
		
		// 生成并设置另一个样式
		HSSFCellStyle style2 = getCellStyle2(workbook);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("dzf");

		HSSFFont font3 = workbook.createFont();
		font3.setColor(HSSFColor.BLUE.index);
		
		HSSFDataFormat format = workbook.createDataFormat(); 
		
//		HSSFFont font5 = workbook.createFont();
		
//		HSSFCellStyle cellStyle2 = workbook.createCellStyle();  
		
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		String[] typeArrays = getTypeArrays();
		HashMap<String, String> map = getTypeMaps();
        genearteOtherSheet(workbook, typeArrays,0);
		// 遍历集合数据，产生数据行
        int rows = dataset.size();
		Iterator<T> it = dataset.iterator();
		int index = 0;
		boolean isingeter = false;//是否整数
		HSSFRichTextString richString = null;
		Pattern p = Pattern.compile("^//d+(//.//d+)?$");
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			Class tCls = t.getClass();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			for (short i = 0; i < fields.length; i++) {
				isingeter = false;//是否整数
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				String fieldName = fields[i];
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
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
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					} else if(value instanceof Integer){
						isingeter = true;
						textValue = value == null ? null : value.toString();
					} else {
						// 其它数据类型都当作字符串简单处理
						textValue = value == null ? null : value.toString();
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if(arrayList != null && arrayList.contains(fieldName)){
					    if("icompanytype".equals(fieldName)){
					        // 设置下拉列表值绑定对哪一页起作用
					        richString = new HSSFRichTextString(map.get(textValue));
                            
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
					        sheet.addValidationData(SetDataValidation(workbook, "公司类型!$A$1:$A$" + typeArrays.length, 1, 2, rows, 2));
					    }else{
					        setDropdownValue(sheet, index-1, i, fieldName, textValue, workbook, cell, arrayMap, isingeter,font3);
					    }
					}else{
						if (textValue != null) {
							Matcher matcher = p.matcher(textValue);
							if (matcher.matches()) {// 是数字当作double处理
								cell.setCellValue(Double.parseDouble(textValue));
							} else {
								if("vtradecode".equals(fieldName)){
						            style2.setDataFormat(format.getFormat("@"));
						            cell.setCellStyle(style2);
						            richString = new HSSFRichTextString(textValue);
                                    richString.applyFont(font3);
                                    cell.setCellValue(richString);
								}else{
									richString = new HSSFRichTextString(textValue);
									richString.applyFont(font3);
									cell.setCellValue(richString);
								}
							}
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
	
    private HSSFCellStyle getCellStyle(HSSFWorkbook workbook){
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
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        
        style.setFont(font);
        return style;
    }
	
	private HSSFCellStyle getCellStyle2(HSSFWorkbook workbook){
	    HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBold(false);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        return style2;
	}
	
    /**
     * 创建下拉列表值存储工作表并设置值
     * @author gejw
     * @time 上午10:20:10
     * @param wb
     * @param typeArrays
     * @param cel
     */
    private static void genearteOtherSheet(Workbook wb, String[]typeArrays,int cel) {
        // 创建下拉列表值存储工作表
        Sheet sheet = wb.createSheet("公司类型");
        // 循环往该sheet中设置添加下拉列表的值
        for (int i = 0; i < typeArrays.length; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(cel);
            cell.setCellValue(typeArrays[i]);
        }
    }
    /**
     * 公司类型下拉列表
     * @author gejw
     * @time 上午10:19:41
     * @return
     */
    private String[] getTypeArrays(){
        return new String[]{"有限责任公司","个人独资企业","有限合伙企业","有限责任公司(自然人独资)"," 有限责任公司(国内合资)",
              "自然人有限责任公司","有限责任公司(自然人投资或控股)","有限责任公司(外商投资企业法人独资)","有限责任公司(台港澳法人独资)","有限责任公司(中外合资)",
              "有限责任公司(国有独资)","有限责任公司(非自然人投资或控股的法人独资)","其他股份有限公司(非上市)","其他股份有限公司分公司(非上市)"," 一人有限责任公司",
              "其他有限责任公司","其他有限责任公司(分公司)","全民所有制","其他","个体工商户","承包、承租经营者","普通合伙企业","集体经营单位(非法人)"};
    }
    
    private HashMap<String, String> getTypeMaps(){
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "有限责任公司");
        map.put("2", "个人独资企业");
        map.put("3", "有限合伙企业");
        map.put("4", "有限责任公司(自然人独资)");
        map.put("5", "有限责任公司(国内合资)");
        map.put("6", "自然人有限责任公司");
        map.put("7", "有限责任公司(自然人投资或控股)");
        map.put("8", "有限责任公司(外商投资企业法人独资)");
        map.put("9", "有限责任公司(台港澳法人独资)");
        map.put("10", "有限责任公司(中外合资)");
        map.put("11", "有限责任公司(国有独资)");
        map.put("12", "有限责任公司(非自然人投资或控股的法人独资)");
        map.put("13", "其他股份有限公司(非上市)");
        map.put("14", "其他股份有限公司分公司(非上市)");
        map.put("15", "一人有限责任公司");
        map.put("16", "其他有限责任公司");
        map.put("17", "其他有限责任公司(分公司)");
        map.put("18", "全民所有制");
        map.put("19", "其他");
        map.put("20", "个体工商户");
        map.put("21", "承包、承租经营者");
        map.put("22", "普通合伙企业");
        map.put("23", "集体经营单位(非法人)");
        return map;
    }
    
    /**
     * 设置并引用其他Sheet作为绑定下拉列表数据
     * @author gejw
     * @time 上午9:35:57
     * @param wb
     * @param strFormula
     * @param firstRow
     * @param firstCol
     * @param endRow
     * @param endCol
     * @return
     */
    public static DataValidation SetDataValidation(Workbook wb, String strFormula, int firstRow, int firstCol, int endRow, int endCol) {
        // 表示A列1-59行作为下拉列表来源数据
        // String formula = "typelist!$A$1:$A$59" ;
        // 原顺序为 起始行 起始列 终止行 终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidationHelper dvHelper = new HSSFDataValidationHelper((HSSFSheet)wb.getSheet("typelist"));
        DataValidationConstraint formulaListConstraint = dvHelper.createFormulaListConstraint(strFormula);
        DataValidation dataValidation = dvHelper.createValidation(formulaListConstraint, regions);

        return dataValidation;
    }

	
	/**
	 * 设置下来值
	 * @param sheet
	 * @param rowIndex   行id
	 * @param colIndex   列id
	 * @param fieldName
	 * @param textValue
	 * @param workbook
	 * @param cell
	 */
	private void setDropdownValue(HSSFSheet sheet, int rowIndex, int colIndex, String fieldName,String textValue, HSSFWorkbook workbook,
			HSSFCell cell, Map<String,String[]> arrayMap,boolean isingeter,HSSFFont font3) {
		String[] arytype = getStrArray(fieldName, arrayMap);
		if (!StringUtil.isEmpty(textValue)) {
			List<String> list = Arrays.asList(arytype);
			int index = -1;
			if(isingeter){
				index = Integer.parseInt(textValue);
			}else{
				index = list.indexOf(textValue);
			}
			String typename = "";
			if("icompanytype".equals(fieldName)){
				typename = arytype[index-1];
			}else if("vprovince".equals(fieldName)){
				typename = arytype[index-2];
			}else{
				typename = arytype[index];
			}
			HSSFRichTextString richString = new HSSFRichTextString(typename);
			font3.setColor(HSSFColor.BLUE.index);
			richString.applyFont(font3);
			cell.setCellValue(richString);
		}
		CellRangeAddressList regions = new CellRangeAddressList(rowIndex + 1, rowIndex + 1, colIndex, colIndex);
		// 生成下拉框内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(arytype);
		// 绑定下拉框和作用区域
		HSSFDataValidation data_validation = new HSSFDataValidation(regions, constraint);
		// 对sheet页生效
		sheet.addValidationData(data_validation);
	}
	
	/**
	 * 获取数组的值
	 * @param fieldName
	 * @return
	 */
	private String[] getStrArray(String fieldName, Map<String,String[]> arrayMap){
		if(arrayMap != null && !arrayMap.isEmpty()){
			return arrayMap.get(fieldName);
		}
		return null;
	}
}