package com.dzf.zxkj.excel.util;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.excel.param.MuiltSheetExceport;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel导出2007版本[2003仅支持导出65535行，超过10W行无法导出，2007没有问题]
 * @author zpm
 *
 */
@Slf4j
public class Excelexport2007<T extends SuperVO> {

	private Map<Integer,XSSFCellStyle> map = new ConcurrentHashMap<Integer,XSSFCellStyle>();
	
	//默认创建12字体
	private XSSFFont createfont(XSSFWorkbook workbook){
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);// 加粗
		return font;
	}
	
	//定义大标题行样式
	private XSSFCellStyle createTitleStyle1(XSSFWorkbook workbook){
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont f = createfont(workbook);
		f.setFontHeightInPoints((short) 20);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.CENTER);// 内容左右居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义 其他标题行样式
	private XSSFCellStyle createTitleStyle5(XSSFWorkbook workbook){
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont f = createfont(workbook);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.LEFT);// 内容左右居左
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义小标题栏样式
	private XSSFCellStyle createTitleStyle2(XSSFWorkbook workbook,XSSFColor myColor){
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(myColor);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont font  = createfont(workbook);
		// 把字体应用到当前的样式
		style.setFont(font);
		return style;
	}
	
	//普通单元格样式
	private XSSFCellStyle createTitleStyle3(XSSFWorkbook workbook,XSSFColor myColor){
		XSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(myColor);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.LEFT);
		style2.setWrapText(true);//超过长度，自动换行
		// 生成另一个字体
		XSSFFont font2 = workbook.createFont();
		// 把字体应用到当前的样式
		style2.setFont(font2);
		return style2;
	}
	
	//数字单元格样式
	private XSSFCellStyle createTitleStyle4(XSSFWorkbook workbook,XSSFColor myColor){
		XSSFCellStyle rightstyle = createTitleStyle3(workbook,myColor);
		rightstyle.setAlignment(HorizontalAlignment.CENTER);
		return rightstyle;
	}
	
	private int[] merge(XSSFSheet  sheet,int fieldslen,int group){
		int[] s = new int[group];
		s[0] = 0;
		if(group == 1){
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0,fieldslen-1));
		}else if(group == 2){
			s[1] = fieldslen / 2;
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0,s[1]-1));
			sheet.addMergedRegion(new CellRangeAddress(3, 3,s[1], fieldslen - 1));
		}else if(group == 3){
			s[1] = fieldslen / 3;
			s[2] = 2*fieldslen / 3;
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0,s[1]-1));
			sheet.addMergedRegion(new CellRangeAddress(3, 3,s[1], s[2]-1));
			sheet.addMergedRegion(new CellRangeAddress(3, 3,s[2],fieldslen - 1));
		}
		return s;
	}
	
	private void doshowTitleDetail(XSSFRow row_names, IExceport<T> export, XSSFCellStyle style3, int[] ss, boolean[] aa, String qj){
		int i = 0;
		if(aa[0]){
			XSSFCell cell3s = row_names.createCell(ss[i]);
			cell3s.setCellValue("公司：" + export.getCorpName());
			cell3s.setCellStyle(style3);
			i++;
		} 
		if(aa[1]){
			XSSFCell cell3ss = row_names.createCell(ss[i]);
			cell3ss.setCellValue("期间："+qj);
			cell3ss.setCellStyle(style3);
			i++;
		} 
		if(aa[2]){
			XSSFCell cell3sss = row_names.createCell(ss[i]);
			cell3sss.setCellValue("单位:  元");
			cell3sss.setCellStyle(style3);
		}
	}
	
	private void showTitleDetail(XSSFWorkbook workbook,XSSFSheet  sheet,IExceport<T>  export,int fieldslen,String qj){
		XSSFCellStyle style3 = createTitleStyle5(workbook);
		XSSFRow row_names = sheet.createRow(3);
		if(fieldslen < 3){//列总数小于3列 ，仅显示期间
			XSSFCell cell3s = row_names.createCell(0);
			cell3s.setCellValue("期间："+qj);
			cell3s.setCellStyle(style3);
			sheet.addMergedRegion(new CellRangeAddress(3, 3 , 0, fieldslen - 1));
		}else{
			int len = 0;
			boolean[] aa = export.isShowTitDetail();
			for(boolean a : aa)
				if(a)len++;
			int[] ss = merge(sheet,fieldslen,len);
			doshowTitleDetail(row_names,export,style3,ss,aa,qj);
		}
	}
	
	private void createSheet(XSSFWorkbook workbook,IExceport<T>  export, OutputStream out,String[] sheetname,List<T[]> data,String[] qjs){
		try {
			if(sheetname==null||sheetname.length==0||data==null||data.size()==0)
				return;
			for(int inx = 0;inx<sheetname.length;inx++){
				String sheetpername = sheetname[inx];
				T[] dataset = data.get(inx);
				Fieldelement[] fieldinfos = export.getFieldInfo();
				int fieldslen = fieldinfos.length;
				XSSFSheet  sheet = workbook.createSheet(sheetpername);
				XSSFColor myColor = new XSSFColor(Color.WHITE);
//				sheet.setDefaultColumnWidth(15);
				// 处理大标题 行 前3行合并
				CellRangeAddress region1 = new CellRangeAddress(0, 2, 0, (fieldslen - 1));
				sheet.addMergedRegion(region1);
				XSSFRow row_name = sheet.createRow(0);
				XSSFCellStyle titlestyle = createTitleStyle1(workbook);
				XSSFCell cell3 = row_name.createCell(0);
				cell3.setCellValue(export.getExceportHeadName());
				cell3.setCellStyle(titlestyle);
				String qj = qjs[inx];
				//处理中间小标题
				showTitleDetail(workbook,sheet,export,fieldslen,qj);
				// 处理小标题栏
				int index = 4;
				XSSFRow row = sheet.createRow(index);
				XSSFCellStyle style = createTitleStyle2(workbook,myColor);
				for (int i = 0; i < fieldslen; i++) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(style);
					XSSFRichTextString text = new XSSFRichTextString(fieldinfos[i].getName());
					cell.setCellValue(text);
					//设置列宽
					sheet.setColumnWidth(i, fieldinfos[i].getColwidth()*256);//15乘以256代表15个字符
				}
				XSSFCellStyle style2 = createTitleStyle3(workbook,myColor);
				for(int a = 0 ;a<dataset.length;a++){
					index++;
					row = sheet.createRow(index);
					T t = dataset[a];
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					for (int i = 0; i < fieldinfos.length; i++) {
						XSSFCell cell = row.createCell(i);
						String fieldName = fieldinfos[i].getCode();
						try {
							cell.setCellStyle(style2);
							Object value = t.getAttributeValue(fieldName);
							// 判断值的类型后进行强制类型转换
							String textValue = null;
							if(fieldinfos[i].getIsdecimal()){//数字类型特殊处理
								DZFDouble bValue = (DZFDouble) value;
								if(bValue == null || (bValue.doubleValue() == 0 && fieldinfos[i].isZeroshownull())){
									cell.setCellValue("");
								}else{
									textValue = bValue.toString();
									cell.setCellValue(Double.parseDouble(textValue));
									XSSFCellStyle rightstyle = getDecimalFormatStyle(fieldinfos[i],workbook,myColor);
									cell.setCellStyle(rightstyle);
								}
							}else{
								if (value instanceof DZFBoolean) {
									boolean bValue = ((DZFBoolean) value).booleanValue();
									textValue = "是";
									if (!bValue) {
										textValue = "否";
									}
								} else {
									// 其它数据类型都当作字符串简单处理
									textValue = value == null ? null : value.toString();
								}
								XSSFRichTextString richString = new XSSFRichTextString(textValue);
								cell.setCellValue(richString);
							}
						} catch (Exception e) {
							log.error("导出失败！", e);
						} 
					}
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				log.error("IO异常！", e);
			}
		} catch (Exception e) {
			log.error("导出失败！", e);
		}
	}

	public void exportExcel(IExceport<T>  export, OutputStream out) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] sheetname = null;
		List<T[]> data = null;
		String[] qjs = null;
		if(export != null && export instanceof MuiltSheetExceport){
			sheetname = ((MuiltSheetExceport<T>)export).getAllSheetName();
			data =  ((MuiltSheetExceport<T>)export).getAllSheetData();
			qjs = ((MuiltSheetExceport<T>)export).getAllPeriod();
		}else{
			sheetname = new String[]{export.getSheetName()};
			data = new ArrayList<T[]>();
			T[] dataset = export.getData();
			data.add(dataset);
			qjs = new String[]{export.getQj()};
		}
		createSheet(workbook,export,out,sheetname,data,qjs);
	}
	
	private XSSFCellStyle getDecimalFormatStyle(Fieldelement field,XSSFWorkbook workbook,XSSFColor myColor){
		Integer key = field.getDigit();
		if(field.isIspercent()){
			//设置百分比的key 为100
			key = 100;
		}
		if(field.isIspercent() && !map.containsKey(key)){
			XSSFCellStyle rightstyle = createTitleStyle4(workbook,myColor);
			XSSFDataFormat fmt = workbook.createDataFormat();
			rightstyle.setDataFormat(fmt.getFormat("0.00%"));
			map.put(key, rightstyle);
		} 
		if(!map.containsKey(key)){
			String style = "#,##0";
			for(int i = 0 ;i<field.getDigit();i++){
				if(i == 0)
					style = style + ".";
				style = style + "0";
			}
			XSSFCellStyle rightstyle = createTitleStyle4(workbook,myColor);
			XSSFDataFormat fmt = workbook.createDataFormat();
			rightstyle.setDataFormat(fmt.getFormat(style));
			map.put(key, rightstyle);
		}
		return map.get(key);
	}
}