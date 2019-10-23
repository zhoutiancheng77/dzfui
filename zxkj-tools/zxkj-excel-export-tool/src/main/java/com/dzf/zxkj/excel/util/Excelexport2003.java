package com.dzf.zxkj.excel.util;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel导出2003版本[2003仅支持导出65535行，超过10W行无法导出，2007没有问题]
 * @author zpm
 *
 */
@SuppressWarnings("all")
@Slf4j
public class Excelexport2003<T extends SuperVO> {

	private Map<Integer,HSSFCellStyle> map = new ConcurrentHashMap<Integer,HSSFCellStyle>();
	
	//默认创建12字体
	private HSSFFont createfont(HSSFWorkbook workbook){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);// 加粗
		return font;
	}
	
	//定义大标题行样式
	private HSSFCellStyle createTitleStyle1(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont f = createfont(workbook);
		f.setFontHeightInPoints((short) 20);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.CENTER);// 内容左右居中  
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义 其他标题行样式 居左
	private HSSFCellStyle createTitleStyle5(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont f = createfont(workbook);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.LEFT);// 内容左右居左
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义 其他标题行样式  居中
	private HSSFCellStyle createTitleStyle6(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont f = createfont(workbook);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.CENTER);// 内容左右居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义 其他标题行样式 居右
	private HSSFCellStyle createTitleStyle7(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont f = createfont(workbook);
		style.setFont(f);
		style.setAlignment(HorizontalAlignment.RIGHT);// 内容左右居右
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
		return style;
	}
	
	//定义小标题栏样式
	private HSSFCellStyle createTitleStyle2(HSSFWorkbook workbook,short color){
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		HSSFFont font  = createfont(workbook);
		// 把字体应用到当前的样式
		style.setFont(font);
		return style;
	}
	
	//普通单元格样式
	private HSSFCellStyle createTitleStyle3(HSSFWorkbook workbook,short color){
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(color);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.LEFT);
		style2.setWrapText(true);//超过长度，自动换行
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		// 把字体应用到当前的样式
		style2.setFont(font2);
		return style2;
	}
	
	//数字单元格样式
	private HSSFCellStyle createTitleStyle4(HSSFWorkbook workbook,short color){
		HSSFCellStyle rightstyle = (HSSFCellStyle)createTitleStyle3(workbook,color);
		rightstyle.setAlignment(HorizontalAlignment.RIGHT);
		return rightstyle;
	}
	
	private int[] merge(HSSFSheet  sheet,int fieldslen,int group,int position){
		int[] s = new int[group];
		s[0] = 0;
		if(group == 1){
			sheet.addMergedRegion(new CellRangeAddress(position, position, 0,fieldslen-1));
		}else if(group == 2){
			s[1] = fieldslen / 2;
			sheet.addMergedRegion(new CellRangeAddress(position, position, 0,s[1]-1));
			sheet.addMergedRegion(new CellRangeAddress(position, position,s[1], fieldslen - 1));
		}else if(group == 3){
			s[1] = fieldslen / 3;
			s[2] = 2*fieldslen / 3;
			sheet.addMergedRegion(new CellRangeAddress(position, position, 0,s[1]-1));
			sheet.addMergedRegion(new CellRangeAddress(position, position,s[1], s[2]-1));
			sheet.addMergedRegion(new CellRangeAddress(position, position,s[2],fieldslen - 1));
		}
		return s;
	}
	
	private void doshowTitleDetail(HSSFRow row_names,IExceport<T>  export,HSSFCellStyle style3,
			HSSFCellStyle style3_center,HSSFCellStyle style3_right,
			int[] ss,boolean[] aa,String qj){
		int i = 0;
		if(aa[0]){
			HSSFCell cell3s = row_names.createCell(ss[i]);
			cell3s.setCellValue("公司：" + export.getCorpName());
			cell3s.setCellStyle(style3);
			i++;
		} 
		if(aa[1]){
			HSSFCell cell3ss = row_names.createCell(ss[i]);
			cell3ss.setCellValue("期间："+qj);
			cell3ss.setCellStyle(style3_center);
			i++;
		} 
		if(aa[2]){
			HSSFCell cell3sss = row_names.createCell(ss[i]);
			if(export != null && export instanceof UnitExceport){
				cell3sss.setCellValue("单位:  "+((UnitExceport)export).getDw());
			}else{
				cell3sss.setCellValue("单位:  元");
			}
			cell3sss.setCellStyle(style3_right);
		}
	}
	
	private void showTitleDetail(HSSFWorkbook workbook, HSSFSheet  sheet, IExceport<T> export, int fieldslen, String qj, int position){
		HSSFCellStyle style3 = createTitleStyle5(workbook);
		HSSFCellStyle style3_center = createTitleStyle6(workbook);
		HSSFCellStyle style3_right = createTitleStyle7(workbook);
		HSSFRow row_names = sheet.createRow(position);//之前默认是3
		if(fieldslen < 3){//列总数小于3列 ，仅显示期间
			HSSFCell cell3s = row_names.createCell(0);
			cell3s.setCellValue("期间："+qj);
			cell3s.setCellStyle(style3);
			sheet.addMergedRegion(new CellRangeAddress(position, position , 0, fieldslen - 1));
		}else{
			int len = 0;
			boolean[] aa = export.isShowTitDetail();
			for(boolean a : aa)
				if(a)len++;
			int[] ss = merge(sheet,fieldslen,len, position);
			doshowTitleDetail(row_names,export,style3,style3_center,style3_right,ss,aa,qj);
		}
	}
	
	private void createExcelSheet(HSSFWorkbook workbook,IExceport<T>  export, OutputStream out,String[] sheetname,List<T[]> data,String[] qjs){
		try {
			if(sheetname==null||sheetname.length==0||data==null||data.size()==0)
				return;
			for(int inx = 0;inx<sheetname.length;inx++){
				String sheetpername = sheetname[inx];
				T[] dataset = data.get(inx);
				List<Fieldelement> lastfiledlist = new ArrayList<Fieldelement>();
				 getLastField(lastfiledlist,export.getFieldInfo());
				 Fieldelement[] fieldinfos = lastfiledlist.toArray(new Fieldelement[0]);
				int fieldslen = fieldinfos.length;
				HSSFSheet  sheet = workbook.createSheet(sheetpername);
				short color = HSSFColor.WHITE.index;
//				sheet.setDefaultColumnWidth(15);//15个字符
				// 处理大标题 行 前3行合并
				int startindex = 0;
				if(export instanceof MuiltSheetAndTitleExceport){
					//单独处理
					startindex = showTitleForCw(workbook, export, fieldslen, sheet);
				}else{
					CellRangeAddress region1 = new CellRangeAddress(0, 2, 0, (fieldslen - 1));
					sheet.addMergedRegion(region1);
					HSSFRow row_name = sheet.createRow(0);
					HSSFCellStyle titlestyle = createTitleStyle1(workbook);
					HSSFCell cell3 = row_name.createCell(0);
					cell3.setCellValue(export.getExceportHeadName());
					cell3.setCellStyle(titlestyle);
					startindex = 3;//
				}
				String qj = qjs[inx];
				//处理中间小标题
				showTitleDetail(workbook,sheet,export,fieldslen,qj,startindex);
				// 处理小标题栏
				HSSFRow row;
				int index = handleHead(workbook, export.getFieldInfo(), fieldslen, sheet, color, startindex);
				HSSFCellStyle style2 = createTitleStyle3(workbook,color);
				for(int a = 0 ;a<dataset.length;a++){
					index++;
					row = sheet.createRow(index);
					T t = dataset[a];
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					for (int i = 0; i < fieldinfos.length; i++) {
						HSSFCell cell = row.createCell(i);
						String fieldName = fieldinfos[i].getCode();;
						try {
							cell.setCellStyle(style2);
							Object value = t.getAttributeValue(fieldName);
							// 判断值的类型后进行强制类型转换
							String textValue = null;
							if(fieldinfos[i].getIsdecimal()){//数字类型特殊处理
								DZFDouble bValue = (DZFDouble) value;
								if(bValue == null || (bValue.doubleValue() == 0 && fieldinfos[i].isZeroshownull())){
//									cell.setCellValue("");
								}else{
									textValue = bValue.toString();
									cell.setCellValue(Double.parseDouble(textValue));
									HSSFCellStyle rightstyle = getDecimalFormatStyle(fieldinfos[i],workbook,color);
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
//								HSSFRichTextString richString = new HSSFRichTextString(textValue);
//								cell.setCellValue(richString);
								if(fieldinfos[i].getComboStrs() != null && fieldinfos[i].getComboStrs().length > 0){
                                    setDropdownValue(sheet, index-1, i,textValue, workbook, cell, true, fieldinfos[i].getComboStrs());
                                }else{
                                    HSSFRichTextString richString = new HSSFRichTextString(textValue);
                                    cell.setCellValue(richString);
                                }
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

	private void getLastField(List<Fieldelement> res ,Fieldelement[] fieldInfo) {
		for(Fieldelement element:fieldInfo){
			if(element.getChilds()!=null && element.getChilds().length>0){
				getLastField(res, element.getChilds());
			}else{
				res.add(element);
			}
		}
	}

	/**
	 * 处理标题栏
	 * @param workbook
	 * @param fieldinfos
	 * @param fieldslen
	 * @param sheet
	 * @param color
	 * @param startindex
	 * @return
	 */
	private int handleHead(HSSFWorkbook workbook, Fieldelement[] fieldinfos, int fieldslen, HSSFSheet sheet,
			short color, int startindex) {
		int index = startindex+1;
		//合并行
		List<CellRangeAddress> region = getHeadReginAddress(fieldinfos,index,0);
		if(region.size()>0){
			for(CellRangeAddress address:region){
				sheet.addMergedRegion(address);
			}
		}
		//拼接每一行的数据
		List<Fieldelement[]> fieldinfo_res = new ArrayList<Fieldelement[]>();
		getHeadRegionFieldInfos(fieldinfo_res,fieldinfos);
		//界面赋值数据
		for (int k = 0; k < fieldinfo_res.size(); k++) {
			fieldinfos = fieldinfo_res.get(k);
			HSSFRow row = sheet.createRow(index);
			HSSFCellStyle style = createTitleStyle2(workbook, color);
			HSSFCell cell = null;
			//赋值空
			for (int i = 0; i < fieldslen; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				HSSFRichTextString text = new HSSFRichTextString("");
				cell.setCellValue(text);
			}
			//添加值
			for (Fieldelement fieldinfo:fieldinfos) {
				cell =	row.getCell(fieldinfo.getCell_pos());
				cell.setCellStyle(style);
				HSSFRichTextString text = new HSSFRichTextString(fieldinfo.getName());
				cell.setCellValue(text);
				// 设置列宽
				sheet.setColumnWidth(fieldinfo.getCell_pos(), fieldinfo.getColwidth() * 256);// 15乘以256代表15个字符
			}
			index = index + 1;
		}
		return index-1;
	}

	private void getHeadRegionFieldInfos(List<Fieldelement[]> list,Fieldelement[] fieldinfos) {
		List<Fieldelement> childlist = new ArrayList<Fieldelement>();
		for (Fieldelement element : fieldinfos) {
			if (element.getChilds() != null && element.getChilds().length > 0) {
				for(Fieldelement child:element.getChilds()){
					childlist.add(child);
				}
			}
		}
		//一层一层叠加
		list.add(fieldinfos);
		if (childlist != null && childlist.size() > 0) {
			getHeadRegionFieldInfos(list, childlist.toArray(new Fieldelement[0]));
		}
	}

	private List<CellRangeAddress> getHeadReginAddress(Fieldelement[] fieldinfos, int index, int startcolumn) {
		List<CellRangeAddress> region = new ArrayList<CellRangeAddress>();
		if (fieldinfos != null && fieldinfos.length > 0) {
			for (Fieldelement field : fieldinfos) {
				Integer rowspan = field.getRowspan() == null ? 1 : field.getRowspan();
				Integer colspan = field.getColspan() == null ? 1 : field.getColspan();
				int firstRow = index;
				int lastRow = index + rowspan - 1;
				int firstCol = startcolumn;
				int lastCol = startcolumn + colspan - 1;

				CellRangeAddress address = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
				field.setCell_pos(firstCol);
				startcolumn = startcolumn + colspan;
				if(firstRow != lastRow || firstCol != lastCol){
					region.add(address);
				}
				// 递归查询
				if (field.getChilds() != null && field.getChilds().length > 0) {
					int startcolumn_child = startcolumn - colspan;
					List<CellRangeAddress> regionchild = getHeadReginAddress(field.getChilds(), index + 1, startcolumn_child);
					region.addAll(regionchild);
				}
			}
		}
		return region;
	}

	private int showTitleForCw(HSSFWorkbook workbook, IExceport<T> export, int fieldslen, HSSFSheet sheet) {
		int startindex;
		TitleColumnExcelport titlecolumn = ((MuiltSheetAndTitleExceport) export).getTitleColumns();
		CellRangeAddress region1 = new CellRangeAddress(0, titlecolumn.getRowspan()-1, 0, (fieldslen - 1));
		sheet.addMergedRegion(region1);
		HSSFRow row_name = sheet.createRow(titlecolumn.getRowspan()-1);
		HSSFCellStyle titlestyle = createTitleStyle1(workbook);
//		row_name.setHeight((short)30);
		HSSFCell cell3 = row_name.createCell(0);
		cell3.setCellValue(export.getExceportHeadName());
		cell3.setCellStyle(titlestyle);
		startindex = titlecolumn.getRowspan();
		//head另外的内容
		List<TitleColumnExcelport> listheads =  ((MuiltSheetAndTitleExceport) export).getHeadColumns();
		for(TitleColumnExcelport tempport : listheads){
			region1 = new CellRangeAddress(startindex, startindex+tempport.getRowspan()-1, 0, (fieldslen - 1));
			sheet.addMergedRegion(region1);
			HSSFRow row_name_extra = sheet.createRow(startindex);
			HSSFCellStyle style_extra = createTitleStyle5(workbook);
			style_extra.setAlignment(tempport.getAlignment());
			HSSFCell cell_extra = row_name_extra.createCell(0);
			cell_extra.setCellValue(tempport.getName());
			cell_extra.setCellStyle(style_extra);
			startindex = startindex+tempport.getRowspan();
		}
		return startindex;
	}
	
	
	private void check(IExceport<T>  export) throws RuntimeException{
		if(export == null)
			throw new RuntimeException("导出设置为空！");
		if(export.isShowTitDetail() == null || export.isShowTitDetail().length != 3)
			throw new RuntimeException("导出显示公司、期间列设置不正确！");
	}

	public void exportExcel(IExceport<T>  export, OutputStream out) throws IOException {
		check(export);
		HSSFWorkbook workbook = new HSSFWorkbook();
		String[] sheetname = null;
		List<T[]> data = null;
		String[] qjs = null;
		if(export != null && export instanceof MuiltSheetExceport){
			sheetname = ((MuiltSheetExceport<T>)export).getAllSheetName();
			data =  ((MuiltSheetExceport<T>)export).getAllSheetData();
			qjs = ((MuiltSheetExceport<T>)export).getAllPeriod();
		} else{
			sheetname = new String[]{export.getSheetName()};
			data = new ArrayList<T[]>();
			T[] dataset = export.getData();
			data.add(dataset);
			qjs = new String[]{export.getQj()};
		}
		createExcelSheet(workbook,export, out,sheetname,data,qjs);
	}
	
	private HSSFCellStyle getDecimalFormatStyle(Fieldelement field,HSSFWorkbook workbook,short color){
		Integer key = field.getDigit();
		if(field.isIspercent()){
			//设置百分比的key 为100
			key = 100;
		}
		if(field.isIspercent() && !map.containsKey(key)){
			HSSFCellStyle rightstyle = createTitleStyle4(workbook,color);
			rightstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
			map.put(key, rightstyle);
		} 
		if(!map.containsKey(key)){
			String style = "#,##0";
			for(int i = 0 ;i<field.getDigit();i++){
				if(i == 0)
					style = style + ".";
				style = style + "0";
			}
			HSSFCellStyle rightstyle = createTitleStyle4(workbook,color);
			HSSFDataFormat fmt = workbook.createDataFormat();
			rightstyle.setDataFormat(fmt.getFormat(style));
			map.put(key, rightstyle);
		}
		return map.get(key);
	}
	
	/**
     * 设置下拉框
     * @author gejw
     * @time 上午9:58:55
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @param textValue
     * @param workbook
     * @param cell
     * @param isingeter
     * @param arytype
     */
   private void setDropdownValue(HSSFSheet sheet, int rowIndex, int colIndex, String textValue, HSSFWorkbook workbook,
            HSSFCell cell,boolean isingeter,String[] arytype) {
        if (!StringUtil.isEmpty(textValue)) {
            List<String> list = Arrays.asList(arytype);
            int index = -1;
            if(isingeter){
                index = Integer.parseInt(textValue);
            }else{
                index = list.indexOf(textValue);
            }
            String typename = arytype[index];
            HSSFRichTextString richString = new HSSFRichTextString(typename);
            HSSFFont font3 = workbook.createFont();
//              font3.setColor(HSSFColor.BLUE.index);
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
}
