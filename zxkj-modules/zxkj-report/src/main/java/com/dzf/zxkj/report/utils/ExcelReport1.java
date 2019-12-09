package com.dzf.zxkj.report.utils;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.DzfTypeUtils;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.pzgl.PzglPageVo;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 转载时请保留以下信息，注明出处！
 *
 * @author leno
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
@Slf4j
@SuppressWarnings("all")
public class ExcelReport1<T> {

	private String flag = "";
	// private int index = 5;
	private String currency;

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	// llh
	// 缓存表格数据行用到的Style（全部新建会导致性能问题，而完全共用一个Style会导致不同单元格串格式，所以对Style做缓存(按大小对齐精度等)）
	private Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();

	public void exportExcel(Collection<T> dataset, OutputStream out, String gs, String qj,	IZxkjPlatformService userServiceImpl) {
		exportExcel("测试POI导出EXCEL文档", null, null, dataset, out, "yyyy-MM-dd", gs, qj,userServiceImpl);
	}

	public byte[] exportExcel(String sheetName, String[] headers, String[] fields, Collection<T> dataset, String gs,
			String qj, OutputStream out,	IZxkjPlatformService userServiceImpl) {
		return exportExcel(sheetName, headers, fields, dataset, out, "yyyy-MM-dd", gs, qj,userServiceImpl);
	}

	public byte[] exportExcelByPZ(String sheetName, String[] headers, String[] fields, Collection<T> dataset, String gs,
			String qj, OutputStream out,	IZxkjPlatformService userServiceImpl) {
		return exportExcelByPZ(sheetName, headers, fields, dataset, out, "yyyy-MM-dd", gs, qj);
	}

	public byte[] exportExcel(String sheetName, String[] headers, String[] fields, Collection<T> dataset, String gs,
			String qj, OutputStream out, String f,	IZxkjPlatformService userServiceImpl) {
		flag = f;
		return exportExcel(sheetName, headers, fields, dataset, out, "yyyy-MM-dd", gs, qj,userServiceImpl);
	}

	public void exportExcel(String[] headers, String[] fields, Collection<T> dataset, String gs, String qj,
			OutputStream out, String pattern,	IZxkjPlatformService userServiceImpl) {
		exportExcel("测试POI导出EXCEL文档", headers, fields, dataset, out, pattern, gs, qj,userServiceImpl);
	}

	public byte[] exportExcelNSSB(String corpName, List<String> fields, JSONArray array, JSONArray arraySum,
			OutputStream out) throws Exception {

		ByteArrayOutputStream bos = null;
		InputStream is = null;
		try {
			HSSFWorkbook workbook = null;
			Map<String, Object> jsonmap = array == null ? new HashMap<String, Object>()
					: (Map<String, Object>) array.get(0);
			Object obj = jsonmap.get("qj");
			String qj = obj == null ? "" : obj.toString();
			String date[] = qj.split("-");// ((Map<String, Object>)
											// array.get(0)).get("qj").toString().split("-")
			int days = DZFDate.getDaysMonth(DzfTypeUtils.castToInt(date[0]), DzfTypeUtils.castToInt(date[1]));

			Map<Integer, String> fieldColumn = new HashMap<Integer, String>() {// 需要前台获取的值对应的field名字
				{
					put(0, "ygbm");
					put(1, "ygname");
					put(2, "zjlx");
					put(3, "zjbm");
					put(5, "yfgz");
					put(6, "yanglaobx");
					put(7, "yiliaobx");
					put(8, "shiyebx");
					put(9, "zfgjj");
					put(19, "grsds");
				}
			};
			Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET+"salary_NSSBreport11.xls");
			is = exportTemplate.getInputStream();
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			// sheet.shiftRows(9, sheet.getLastRowNum(), array.size());
			// HSSFRow rowDate = sheet.getRow(1);
			// //计算周期日期
			// StringBuffer begingDate = new StringBuffer();
			// begingDate.append(date[0]).append("年").append(date[1]).append("月").append("01日");
			// StringBuffer endDate = new StringBuffer();
			// endDate.append(date[0]).append("年").append(date[1]).append("月").append(days).append("日");
			// rowDate.createCell(2).setCellValue(begingDate.toString());
			// rowDate.createCell(4).setCellValue(endDate.toString());
			// 计算周期日期 end

			// 公司名称
			// HSSFRow rowCorp = sheet.getRow(2);
			// rowCorp.createCell(2).setCellValue(corpName);
			// 公司名称 end

			// HSSFRow rowSum = sheet.getRow(7);
			HSSFRow row = sheet.getRow(0);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

			// 合计行
			/*
			 * for (int colindex=minindex + 6;colindex < maxindex;colindex++){
			 * if(colindex == 16){ HSSFCell c2 = rowSum.createCell(colindex);
			 * c2.setCellValue(new DZFDouble(3500 *
			 * array.size()).doubleValue()); } if
			 * (fieldColumn.containsKey(colindex)) { HSSFCell c2 =
			 * rowSum.createCell(colindex);
			 * c2.setCellStyle(row.getCell(colindex).getCellStyle());
			 * Map<String, Object> map = (Map<String, Object>) arraySum.get(0);
			 * HSSFRichTextString richString = null; String key =
			 * fieldColumn.get(colindex); if (map.get(key) != null) { if
			 * (!key.equals("ygname") && !key.equals("zjbm") &&
			 * !key.equals("qj")) { DZFDouble doublevalue = new
			 * DZFDouble(map.get(key).toString()); doublevalue =
			 * doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
			 * c2.setCellValue(doublevalue.doubleValue()); } else { richString =
			 * new HSSFRichTextString(map.get(key).toString());
			 * c2.setCellValue(richString); } } else {
			 * c2.setCellValue(DZFDouble.ZERO_DBL.setScale(2,
			 * DZFDouble.ROUND_HALF_UP).doubleValue()); } } }
			 */

			HSSFRow rowTo = null;

			HSSFCell c1 = null;
			HSSFCell c2 = null;
			Map<String, Object> map = null;
			HSSFRichTextString richString = null;
			String key = null;
			DZFDouble doublevalue = null;
			int len = array == null ? 0 : array.size();
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(1 + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					c2.setCellStyle(c1.getCellStyle());
					if (fieldColumn.containsKey(colindex)) {

						map = (Map<String, Object>) array.get(i);
						key = fieldColumn.get(colindex);
						if (map.get(key) != null) {
							if (!key.equals("ygname") && !key.equals("zjbm") && !key.equals("ygbm")
									&& !key.equals("zjlx")) {
								doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								c2.setCellValue(doublevalue.doubleValue());
							} else {
								if (key.equals("zjlx")) {
									String name = SalaryReportEnum.getTypeEnumByValue(map.get(key).toString())
											.getName();
									richString = new HSSFRichTextString(name);
									c2.setCellValue(richString);
								} else {
									richString = new HSSFRichTextString(map.get(key).toString());
									c2.setCellValue(richString);
								}

							}
						} else {
							// if (key.equals("ygname") || key.equals("zjbm") ||
							// key.equals("qj")) {
							// richString = new HSSFRichTextString("");
							// c2.setCellValue(richString);
							// } else {
							// c2.setCellValue(DZFDouble.ZERO_DBL.setScale(2,
							// DZFDouble.ROUND_HALF_UP).doubleValue());
							// }
						}
						continue;
					}
					// switch (c1.getCellType()) {
					// case HSSFCell.CELL_TYPE_NUMERIC:
					// c2.setCellValue(c1.getNumericCellValue());
					// break;
					// case HSSFCell.CELL_TYPE_STRING:
					// c2.setCellValue(c1.getRichStringCellValue());
					// break;
					// }
					// if (colindex == 0)
					// c2.setCellValue(i + 1); //行序号
				}
			}

			// rowSum.getCell(20).setCellValue(quickDeduction);//填入速算扣除数合计

			// 添加完成后 删除模板行并上移一行
			// sheet.removeRow(sheet.getRow(8));
			// sheet.shiftRows(9, sheet.getLastRowNum(), -1);
			// 添加完成后 删除模板行并上移一行 end

			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {

				}
			}
		}

	}

	public byte[] expPerson(String phone, List<SalaryReportVO> list, OutputStream out, String billtype)
			throws Exception {

		String fileName = "personal_information";
//		if (SalaryTypeEnum.FOREIGNSALARY.getValue().equals(billtype)) {
//			fileName = "personal_information_wj";
//		}

		ByteArrayOutputStream bos = null;
		InputStream is = null;
		try {
			HSSFWorkbook workbook = null;
			Map<Integer, String> fieldColumn = new HashMap<Integer, String>() {// 需要前台获取的值对应的field名字
				{
					put(0, "ygbm");
					put(1, "ygname");
					put(2, "zjlx");
					put(3, "zjbm");
				}
			};
			Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + fileName + ".xls");
			is = exportTemplate.getInputStream();
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow row = sheet.getRow(0);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

			HSSFRow rowTo = null;

			HSSFCell c1 = null;
			HSSFCell c2 = null;
			SalaryReportVO vo = null;
			HSSFRichTextString richString = null;
			String key = null;
			DZFDouble doublevalue = null;
			int len = list == null ? 0 : list.size();
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(1 + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					c2.setCellStyle(c1.getCellStyle());
					vo = list.get(i);
					if (fieldColumn.containsKey(colindex)) {
						key = fieldColumn.get(colindex);
						if (vo.getAttributeValue(key) != null) {
							if (!key.equals("ygname") && !key.equals("zjbm") && !key.equals("ygbm")
									&& !key.equals("zjlx")) {
								doublevalue = new DZFDouble(vo.getAttributeValue(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								c2.setCellValue(doublevalue.doubleValue());
							} else {
								if (key.equals("zjlx")) {
									String name = SalaryReportEnum
											.getTypeEnumByValue(vo.getAttributeValue(key).toString()).getName();
									richString = new HSSFRichTextString(name);
									c2.setCellValue(richString);
								} else {
									richString = new HSSFRichTextString(vo.getAttributeValue(key).toString());
									c2.setCellValue(richString);
								}

							}
						} else {
						}
						continue;
					} else {
						switch (colindex) {
						case 4:
							if (SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()) != null) {
								c2.setCellValue(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getArea());
							}
							break;
						case 7:
							c2.setCellValue(vo.getRyzt());
							break;
						case 8:
							c2.setCellValue("否");
							break;
						case 10:
							c2.setCellValue("是");
							break;
						case 14:
							c2.setCellValue(vo.getVphone());
							break;
						}
					}
				}
			}

			is.close();
			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * 导出Excel通用方法，支持多层表头
	 * <p>
	 * headers参数如：{"科目名称", "期初_借方", "期初_贷方", "本期发生_借方", "本期发生_贷方", …}
	 * </p>
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
	@SuppressWarnings({ "unchecked", "deprecation" })
	public byte[] exportExcel(String title, String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern, String gs, String qj,IZxkjPlatformService userServiceImpl) {
		// 声明一个工作薄

		if (dataset == null || dataset.size() == 0)
			return new byte[0];
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			// 生成一个表格
			HSSFSheet sheet = null;
			if (title.equals("工 资 表")) {
				String[] qjval = qj.split("-");
				StringBuffer sb = new StringBuffer();
				sb.append(qjval[1]).append("月工资表");
				sheet = workbook.createSheet(sb.toString());
			} else {
				sheet = workbook.createSheet(title);
			}
			// 行宽
			sheet.setDefaultColumnWidth(15);

			if (title.equals("利 润 表 季 报")) {
				sheet.setColumnWidth(0, 50 * 256);
			}
			// 声明一个画图的顶级管理器
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			// 定义注释的大小和位置,详见文档
//			HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(1, 1, 1, 1, (short) 4, 2, (short) 6, 5));
			// 设置注释内容
//			comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
			// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//			comment.setAuthor("leno");

			// 报表标题
			HSSFRow row_name = sheet.createRow(0);
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20); // 字号
			f.setBold(true); // 加粗
			HSSFCellStyle style1 = workbook.createCellStyle();
			style1.setFont(f);


			style1.setAlignment(HorizontalAlignment.CENTER); // 水平居中
			style1.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
			HSSFCell cell3 = row_name.createCell(0);
			cell3.setCellValue(title);
			cell3.setCellStyle(style1);
			// 合并单元格
			sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, headers.length - 1));
			HSSFRow row_names = sheet.createRow(3);
			HSSFFont f1 = workbook.createFont();
			f1.setFontHeightInPoints((short) 12); // 字号
            f1.setBold(true);
			if(!StringUtil.isEmpty(currency)){
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (headers.length - 1) / 3));
				sheet.addMergedRegion(new CellRangeAddress(3, 3, ((headers.length - 1) / 3) + 1, (headers.length - 1)*2 / 3));
				sheet.addMergedRegion(new CellRangeAddress(3, 3, ((headers.length - 1) * 2 / 3) + 1, headers.length-1));
				HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
				hssfCellStyle.setFont(f1);
				hssfCellStyle.setAlignment(HorizontalAlignment.LEFT);
				HSSFCell hssfCell = row_names.createCell(0);
				hssfCell.setCellValue("公司：" + gs);
				hssfCell.setCellStyle(hssfCellStyle);
				hssfCell = row_names.createCell(((headers.length - 1) / 3)+1);
				hssfCellStyle = workbook.createCellStyle();
				hssfCellStyle.setFont(f1);
				hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
				hssfCell.setCellValue("期间：" + qj);
				hssfCell.setCellStyle(hssfCellStyle);
				hssfCell = row_names.createCell(((headers.length - 1) * 2 / 3) + 1);
				hssfCellStyle = workbook.createCellStyle();
				hssfCellStyle.setFont(f1);
				hssfCellStyle.setAlignment(HorizontalAlignment.RIGHT);
				hssfCell.setCellValue("单位：" + currency);
				hssfCell.setCellStyle(hssfCellStyle);
			}else{
				// 公司
				HSSFCellStyle style3 = workbook.createCellStyle();
				style3.setFont(f1);
				style3.setAlignment(HorizontalAlignment.LEFT); // 居左
				HSSFCell cell3s = row_names.createCell(0);
				cell3s.setCellValue("公司：" + gs);
				cell3s.setCellStyle(style3);
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (headers.length - 1) / 2));

				// 期间
				HSSFCellStyle style4 = workbook.createCellStyle();
				style4.setFont(f1);
				style4.setAlignment(HorizontalAlignment.RIGHT); // 居右
				// if(!title.toString().equals("工 资 表")){//工资表只有期间
				HSSFCell cell3ss = row_names.createCell(((headers.length - 1) / 2) + 1);
				cell3ss.setCellValue("期间：" + qj);
				cell3ss.setCellStyle(style4);
				// }

				if (title.equals("资 产 负 债 表")) {
					HSSFCell cell4ss = row_names.createCell(((headers.length - 1) / 2) + 4);
					cell4ss.setCellValue("单位：元");
					cell4ss.setCellStyle(style4);
				} else if (title.equals("利 润 表") || title.equals("利 润 表 季 报")) {
					HSSFCell cell4ss = row_names.createCell(((headers.length - 1) / 2) + 2);
					cell4ss.setCellValue("单位：元");
					cell4ss.setCellStyle(style4);
				}  else if (title.startsWith("工 资 表")) {
					if (dataset != null && dataset.size() > 0) {
						T vo = dataset.iterator().next();
						Class tCls = null;
						Method getMethod = null;
						Object value = null;
						// 判断值的类型后进行强制类型转换
						tCls = vo.getClass();
						getMethod = tCls.getMethod("getCoperatorid", new Class[] {});
						value = getMethod.invoke(vo, new Object[] {});
//					IZxkjPlatformService userServiceImpl = SpringUtils.getBean("userServiceImpl");
						UserVO user = userServiceImpl.queryUserById((String) value);// UserCache.getInstance().get((String) value, null);
						if (user != null) {
							HSSFCell cell4ss = row_names.createCell(((headers.length - 1) / 2) + 2);
							cell4ss.setCellStyle(style4);
							cell4ss.setCellValue("制表人："+user.getUser_name());
							sheet.addMergedRegion(new CellRangeAddress(3, 3, ((headers.length - 1) / 2) + 2, (headers.length - 1)));
						}
					}
				} else {
					sheet.addMergedRegion(new CellRangeAddress(3, 3, ((headers.length - 1) / 2) + 1, (headers.length - 1)));
				}
			}

			// 产生表格标题行

			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();
			// 设置这些样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			// font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBold(true); // 加粗
            font.setBold(true);
			// 把字体应用到当前的样式
			style.setFont(font);

			int index;
			if (StringUtil.isEmpty(flag) || Integer.parseInt(flag) < 2) {
				// 通用处理表格标题行：创建多层表头，传入标题开始行index，返回数据行index
				index = processMultiHead(headers, sheet, 4, style);
			} else {
				HSSFCell c = null;
				int len = 0;
				if (flag.equals("2") || flag.equals("3") || flag.equals("4")) {
					index = 6;
					HSSFRow row1 = sheet.createRow(4);
					HSSFRow row2 = sheet.createRow(5);
					len = headers.length;

					for (int i = 0; i < len; i++) {
						c = row1.createCell(i);
						c.setCellValue(new HSSFRichTextString(headers[i]));
						c.setCellStyle(style);
						if (i > 1) {
							c = row2.createCell(i);
							c.setCellValue(new HSSFRichTextString((i - 1) + ""));
							c.setCellStyle(style);
						}
					}

					// 设置合并单元格的区域
					sheet.addMergedRegion(new CellRangeAddress(4, 5, 0, 0));
					sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 1));
				} else if (flag.equals("5")) {
					index = write5(sheet, 4, headers, style);
				} else if (flag.equals("6")) {
					index = write4(sheet, 4, headers, style);
				} else if (flag.equals("7")) {
					index = write6(sheet, 4, headers, style);
				} else {
					HSSFRow row = sheet.createRow(4);
					index = 5;
					len = headers.length;
					for (int i = 0; i < len; i++) {
						c = row.createCell(i);
						c.setCellStyle(style);
						// HSSFRichTextString text = new
						// HSSFRichTextString(headers[i]);
						c.setCellValue(new HSSFRichTextString(headers[i]));
					}
				}
			}

			// 报表金额字段
			String[] decimalCols = new String[] { "ybjf", "jf", "ybdf", "df", "ybjfmny", "jfmny", "ybdfmny", "dfmny",
					"ybbqje", "bqje", "ybbyje", "byje", "ybfsjf", "fsjf", "ybfsdf", "fsdf", "ybjftotal", "jftotal",
					"ybdftotal", "dftotal", "ybbnlj", "bnlj", "ybbnljje", "bnljje", "sqje", "ybye", "ye", "ybqcjf",
					"qcjf", "ybqcdf", "qcdf", "ybqmjf", "qmjf", "ybqmdf", "qmdf", "ybncye1", "ncye1", "hl", "ybjf",
					"ybdf", "ncye2", "qmye1", "qmye2", "busitax", "csmaintax", "partstudytax", "spendtax", "studytax",
					"zztax", "taxsum", "lwsr", "qtywsr", "whsr", "zysr", "insum", "quarterFirst", "quarterSecond",
					"quarterThird", "quarterFourth",
					// 折旧明细账字段
					"assetmny", "depreciationmny", "assetnetmny", "originalvalue",
					// 辅助余额表新增字段
					"qcyejf", "qcyedf", "qmyejf", "qmyedf", "bqfsjf", "bqfsdf", "bnljjf", "bnljdf", "ybqcyejf",
					"ybqcyedf", "ybqmyejf", "ybqmyedf", "ybbqfsjf", "ybbqfsdf", "ybbnljjf", "ybbnljdf",
					// 工资表新增字段
					"yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj", "ynssde", "grsds", "sfgz","shuilv",
					// 收入支出表
					"monnum", "yearnum" ,
					//产成品结转计算结果
					"ncailiao_qc","nrengong_qc","nzhizao_qc","ncailiao_fs","nrengong_fs","nzhizao_fs","ncailiao_wg","nrengong_wg","nzhizao_wg","nnum_wg",
					"ncailiao_nwg","nrengong_nwg","nzhizao_nwg","wgbl"};
			Arrays.sort(decimalCols); // 先排好序，再用binarySearch查找时是O(log(n))的复杂度

			// 遍历集合数据，产生数据行
			Iterator<T> it = dataset.iterator();
			HSSFRow row;
			HSSFCellStyle style2 = null;
			HSSFCell cell = null;
			Pattern p = null;
			Matcher matcher = null;
			String fieldName = null;
			String getMethodName = null;

			Class tCls = null;
			Method getMethod = null;
			Object value = null;
			// 判断值的类型后进行强制类型转换
			String textValue = null;
			// HSSFCellStyle staticst = workbook.createCellStyle();
			while (it.hasNext()) {
				row = sheet.createRow(index);
				T t = (T) it.next();
				String pk_corp = "";
				try {
					pk_corp = (String) t.getClass().getMethod("getPk_corp", new Class[]{}).invoke(t, new Object[]{});
				} catch (Exception e1) {//有可能没这个字段，继续走
				}
				// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
				for (int i = 0; i < fields.length; i++) {
					cell = row.createCell(i);
					style2 = getCommonstyle2(workbook, fields[i], decimalCols,pk_corp);
					cell.setCellStyle(style2);

					fieldName = fields[i];
					getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					try {
						tCls = t.getClass();
						getMethod = tCls.getMethod(getMethodName, new Class[] {});
						value = getMethod.invoke(t, new Object[] {});
						// 判断值的类型后进行强制类型转换
						textValue = null;
						if (value instanceof DZFBoolean) {
							boolean bValue = ((DZFBoolean) value).booleanValue();
							textValue = "是";
							if (!bValue) {
								textValue = "否";
							}
						} else if (value instanceof DZFDouble) {
							DZFDouble bValue = (DZFDouble) value;
							if (title.toString().equals("数 量 金 额 总 账") || title.toString().equals("数 量 金 额 明 细 账")) {
								bValue = bValue.setScale(4, DZFDouble.ROUND_HALF_UP);
							} else if (title.toString().equals("序 时 账") && (fieldName.equals("hl")
									|| fieldName.equals("ybjf") || fieldName.equals("ybdf"))) {
								if(!fieldName.equals("hl")){//汇率不考虑精度 在ACTION 控制
									bValue = bValue.setScale(4, DZFDouble.ROUND_HALF_UP);
								}
							} else if (title.toString().equals("产成品结转计算结果") && (fieldName.equals("nnum_wg"))) {
							} else {
								bValue = bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
							}
							textValue = bValue.toString();
							if (!"ye".equals(fieldName) && !"sl".equals(fieldName) && bValue.doubleValue() == 0) {
								textValue = "";
							}
						} else if (value instanceof Date) {
							Date date = (Date) value;
							SimpleDateFormat sdf = new SimpleDateFormat(pattern);
							textValue = sdf.format(date);
						} else {
							// 其它数据类型都当作字符串简单处理
							textValue = value == null ? null : value.toString();
						}
						// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
						if (textValue != null) {
							// Pattern p = Pattern.compile("^//d+(//.//d+)?$");

							p = Pattern.compile("^(-){0,1}[0-9]+([.]{1}[0-9]+){0,1}$");
							matcher = p.matcher(textValue);
							if (matcher.matches() && !fields[i].equals("km") && !fields[i].equals("qj")
									&& !fields[i].equals("year") && !fields[i].equals("kmbm")
									&& !fields[i].equals("pzh") && !fields[i].equals("hc1") && !fields[i].equals("hc2")
									&& !fields[i].equals("hs") && !fields[i].equals("hc") && !fields[i].equals("swdm")
									&& !fields[i].equals("fzhsxCode") && !fields[i].equals("fzhsxName")
									&& !fields[i].equals("accCode") && !fields[i].equals("accName")
									&& !fields[i].equals("assetcode") && !fields[i].equals("zjbm")
									&& !fields[i].equals("ygbm") && !fields[i].equals("zjlx")
									&& !fields[i].equals("ygname")&& !fields[i].equals("vcode")&& !fields[i].equals("vname")) {
								// 是数字当作double处理



								if(fields[i].equals("wgbl")){
									if (StringUtil.isEmpty(textValue)) {
										cell.setCellValue("0.00%");
									} else {
										cell.setCellValue(textValue + "%");
									}
								}else if(fields[i].equals("ncailiao_qc") || fields[i].equals("nrengong_qc") || fields[i].equals("nzhizao_qc")
										|| fields[i].equals("ncailiao_fs") || fields[i].equals("nrengong_fs") || fields[i].equals("nzhizao_fs")){
									try {
										DZFBoolean	ispercent = (DZFBoolean) t.getClass().getMethod("getIspercent", new Class[]{}).invoke(t, new Object[]{});
										if(ispercent != null && ispercent.booleanValue()){
											if (StringUtil.isEmpty(textValue)) {
												cell.setCellValue("0.00%");
											} else {
												cell.setCellValue(textValue + "%");
											}
										}else{
											cell.setCellValue(textValue);
										}
									} catch (Exception e1) {//有可能没这个字段，继续走
									}
								}else if (fields[i].equals("salvageratio")) {// 资产折旧 残值率
									if (StringUtil.isEmpty(textValue)) {
										cell.setCellValue("0.00%");
									} else {
										cell.setCellValue(Double.parseDouble(textValue) * 100 + "%");
									}
								} else if (fields[i].equals("nnum_wg")) {// 完工数量
									cell.setCellValue(textValue);
								} else {
									cell.setCellValue(Double.parseDouble(textValue));
								}
								/*
								 * HSSFCellStyle cellStyle1 =
								 * cell.getCellStyle(); //获取cell1的style
								 * //HSSFCellStyle st =
								 * workbook.createCellStyle(); //创建新的style
								 * HSSFCellStyle st = staticst;
								 * st.cloneStyleFrom(cellStyle1); //拷贝旧的style
								 * st.setAlignment(HorizontalAlignment.RIGHT);
								 * //然后在新的style上设置对齐方式 cell.setCellStyle(st);
								 * //应用新的style
								 */
							} else {
								// HSSFRichTextString richString = new
								// HSSFRichTextString(textValue);
								cell.setCellValue(new HSSFRichTextString(textValue));
							}
						} else {
							/*
							 * if (fields[i].equals("salvageratio")){
							 * cell.setCellValue("0.00%"); }
							 */
						}
					} catch (Exception e) {
						throw new WiseRunException(e);
					} finally {
						// 清理资源
					}
				}
				index++;
			}
			workbook.write(out); // 里面本质上会调OutputStream
									// out的write()，即使这里抛异常，也要保证外面能关闭out
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		return workbook.getBytes();
	}

	/**
	 * 通用处理表格标题行
	 * <p>
	 * 根据headers创建Excel多层表头标题行，传入标题开始行index，返回数据行index。
	 * </p>
	 *
	 * @author llh
	 * @param headers
	 *            列标题中"_"作为上下表头行的分隔符，相邻两列的标题名中的_前缀相同，即认为合并上层表头行的相邻单元格
	 * @param sheet
	 * @param firstrow
	 * @param style
	 * @return 返回接下来要插入的数据行的行index
	 */
	public static int processMultiHead(String[] headers, HSSFSheet sheet, int firstrow, HSSFCellStyle style) {
		// 具体举例：A_B_C与A_B_D，合并前两层A和B；A_B_C和A_D_E，合并第一层的A；A_X_B和C_X_D，不合并（A下的X和B下的X没有关系，不合并）

		// 先解析一遍columnnames得到多层表头的层数
		int maxcount = 0; // 栏目名称中最多的"_"个数
		for (String column : headers) {
			int count = 0;
			for (int index = -1; (index = column.indexOf("_", index + 1)) != -1; count++)
				;
			if (count > maxcount)
				maxcount = count;
		}
		int headrowcount = maxcount + 1;

		// 先创建标题行
		for (int i = 0; i < headrowcount; i++) {
			sheet.createRow(firstrow + i);
		}
		// 按行缓存每行的左侧相邻单元格
		List<HSSFCell> prevcells = new ArrayList<HSSFCell>();
		for (int i = 0; i < headrowcount; i++) {
			prevcells.add(null);
		}
		HSSFCell cell;
		String colname;
		HSSFCell prevcell;
		boolean bothSame = true;
		String column = null;
		String[] arr = null;
		for (int x = 0; x < headers.length; x++) {
			// 创建并设置所有行列的单元格的边框，避免合并时边框不显示
			for (int i = 0; i < headrowcount; i++) {
				cell = sheet.getRow(firstrow + i).createCell(x);
				cell.setCellStyle(style);
			}

			column = headers[x];
			arr = column.split("_");
			if (arr.length == 1) {
				cell = sheet.getRow(firstrow).getCell(x); // sheet.getRow(firstrow).createCell(x);
				cell.setCellValue(new HSSFRichTextString(column));
				// cell.setRowspan(headrowcount); //headrowcount-i;
//				sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + headrowcount - 1, x, x));
				prevcells.set(0, cell);
			} else {

				for (int y = 0; y < arr.length; y++) {
					colname = arr[y];
					prevcell = prevcells.get(y); // 同层的左侧相邻单元格
					// 与本层及以上的左侧相邻单元格的内容相同，且不是最后一层（最后一层不建议做横向合并）
					bothSame = bothSame && (prevcell != null && prevcell.getStringCellValue().equals(colname));
					if (y < arr.length - 1 && bothSame) {
						// prevcell.setColspan(prevcell.getColspan() + 1);
						// //左侧单元格colspan+1
					} else {
						// 与本层及以上的左侧相邻单元格的内容不同，或者是最后一层时，新建
						cell = sheet.getRow(firstrow + y).getCell(x); // sheet.getRow(firstrow
																		// +
																		// y).createCell(x);
						cell.setCellValue(new HSSFRichTextString(colname));
						// cell.setColspan(1); //colspan先设为1，待定

						// 新建的同时，横向合并左侧相同单元格
						if (prevcell != null && x > prevcell.getColumnIndex() + 1) {
							sheet.addMergedRegion(
									new CellRangeAddress(firstrow + y, firstrow + y, prevcell.getColumnIndex(), x - 1));
						}
						prevcells.set(y, cell);

						// 如果是此栏表头的最后一层，且未到最底部
						if (y == arr.length - 1 && y < headrowcount - 1) {
							// cell.setRowspan(headrowcount - y); //设置rowspan到底
							sheet.addMergedRegion(
									new CellRangeAddress(firstrow + y, firstrow + headrowcount - 1, x, x));
						}
					}
				}
			}
		}

		// 最后一列中需要合并的单元格封一下

		for (int y = 0; y < headrowcount; y++) {
			prevcell = prevcells.get(y); // 同层的左侧相邻单元格
			// 新建的同时，横向合并左侧相同单元格
			if (prevcell != null && headers.length > prevcell.getColumnIndex() + 1) {
				sheet.addMergedRegion(new CellRangeAddress(firstrow + y, firstrow + y, prevcell.getColumnIndex(),
						headers.length - 1));
			}
		}

		// 返回表格数据行开始行index
		return firstrow + headrowcount;
	}

	// 凭证
	public void writeBypz(HSSFSheet sheet, String[] headers, HSSFCellStyle style, String[] fields,
			Collection<T> dataset, int rowf, HSSFWorkbook workbook, String pattern, Iterator<T> it) throws IOException {

		HSSFRow row1 = sheet.createRow(rowf);
		HSSFRow row2 = sheet.createRow(rowf + 1);
		// HSSFRow row1 = sheet.createRow(2);
		// HSSFRow row2 = sheet.createRow(3);

		// 报表金额字段
		String[] decimalCols = new String[] { "jf", "df", "jfmny", "dfmny", "bqje", "byje", "fsjf", "fsdf", "jftotal",
				"dftotal", "bnlj", "bnljje", "sqje", "ye", "qcjf", "qcdf", "qmjf", "qmdf", "ncye1", "ncye2", "qmye1",
				"qmye2", "busitax", "csmaintax", "partstudytax", "spendtax", "studytax", "zztax", "taxsum", "lwsr",
				"qtywsr", "whsr", "zysr", "insum", };
		Arrays.sort(decimalCols); // 先排好序，再用binarySearch查找时是O(log(n))的复杂度

		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.WHITE.index);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		HSSFFont font2 = workbook.createFont();
		// 把字体应用到当前的样式
		style2.setFont(font2);
		HSSFCell cell = null;
		Pattern p = null;
		Matcher matcher = null;
		String fieldName = null;
		String getMethodName = null;

		Class tCls = null;
		Method getMethod = null;
		Object value = null;
		// 判断值的类型后进行强制类型转换
		String textValue = null;
		Date date = null;
		SimpleDateFormat sdf = null;
		// HSSFCellStyle staticst = workbook.createCellStyle();
		T t = (T) it.next();
		for (int i = 0; i < fields.length; i++) {
			// sheet.addMergedRegion(new Region(index, (short) i,
			// index+1,(short) i));
			cell = row1.createCell(i);
			style2 = getCommonstyle2(workbook, fields[i], decimalCols,"");
			cell.setCellStyle(style2);

			fieldName = fields[i];
			getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			try {
				tCls = t.getClass();
				getMethod = tCls.getMethod(getMethodName, new Class[] {});
				value = getMethod.invoke(t, new Object[] {});
				// 判断值的类型后进行强制类型转换
				textValue = null;
				if (value instanceof DZFBoolean) {
					boolean bValue = ((DZFBoolean) value).booleanValue();
					textValue = "是";
					if (!bValue) {
						textValue = "否";
					}
				} else if (value instanceof DZFDouble) {
					DZFDouble bValue = (DZFDouble) value;
					bValue = bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
					textValue = bValue.toString();
					if (!"ye".equals(fieldName) && !"sl".equals(fieldName) && bValue.doubleValue() == 0) {
						textValue = "";
					}
				} else if (value instanceof Date) {
					date = (Date) value;
					sdf = new SimpleDateFormat(pattern);
					textValue = sdf.format(date);
				} else {
					// 其它数据类型都当作字符串简单处理
					textValue = value == null ? null : value.toString();
				}
				// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
				if (textValue != null) {
					// Pattern p = Pattern.compile("^//d+(//.//d+)?$");

					p = Pattern.compile("^(-){0,1}[0-9]+([.]{1}[0-9]+){0,1}$");
					matcher = p.matcher(textValue);
					if (matcher.matches() && !fields[i].equals("km") && !fields[i].equals("qj")
							&& !fields[i].equals("year") && !fields[i].equals("kmbm") && !fields[i].equals("pzh")
							&& !fields[i].equals("hc1") && !fields[i].equals("hc2") && !fields[i].equals("hs")
							&& !fields[i].equals("hc") && !fields[i].equals("swdm")) {
						// 是数字当作double处理
						cell.setCellValue(Double.parseDouble(textValue));
						/*
						 * HSSFCellStyle cellStyle1 = cell.getCellStyle();
						 * //获取cell1的style HSSFCellStyle st =
						 * staticst;//创建新的style st.cloneStyleFrom(cellStyle1);
						 * //拷贝旧的style
						 * st.setAlignment(HorizontalAlignment.RIGHT);
						 * //然后在新的style上设置对齐方式 cell.setCellStyle(st);
						 * //应用新的style
						 */
					} else {
						// HSSFRichTextString richString = new
						// HSSFRichTextString(textValue);
						cell.setCellValue(new HSSFRichTextString(textValue));

					}
				}
			} catch (SecurityException e) {
				// e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				// e.printStackTrace();
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				// 清理资源
			}
			if (i != 2 && i != 3 && i != 4 && i != 5) {
                CellRangeAddress region1 = new CellRangeAddress(rowf, (short) i, (rowf + 1), (short) i);
				sheet.addMergedRegion(region1);
			}
		}

		/*
		 *
		 *
		 * //创建合并单元格的第一个单元格数据
		 *
		 * HSSFRow row3 = sheet.createRow(7); HSSFCell cell0 =
		 * row1.createCell(0); cell0.setCellValue(new HSSFRichTextString("行次"));
		 * cell0.setCellStyle(style); //多这一步解决合并时边框不显示问题 HSSFCell cell01 =
		 * row2.createCell(0); HSSFCell cell02 = row3.createCell(0);
		 * cell01.setCellStyle(style); cell02.setCellStyle(style); // HSSFCell
		 * cell1 = row1.createCell(1); cell1.setCellValue(new
		 * HSSFRichTextString("项目")); cell1.setCellStyle(style); HSSFCell cell2
		 * = row1.createCell(2); cell2.setCellValue(new
		 * HSSFRichTextString("账载金额")); cell2.setCellStyle(style); for(int
		 * i=0;i<2;i++){ HSSFCell cell=row1.createCell(i+3);
		 * cell.setCellStyle(style); } HSSFCell cell3 = row1.createCell(5);
		 * cell3.setCellValue(new HSSFRichTextString("税收金额"));
		 * cell3.setCellStyle(style); for(int i=0;i<4;i++){ HSSFCell
		 * cell=row1.createCell(i+6); cell.setCellStyle(style); } HSSFCell cell4
		 * = row1.createCell(10); cell4.setCellValue(new
		 * HSSFRichTextString("纳税调整")); cell4.setCellStyle(style); HSSFCell
		 * cell4_1=row1.createCell(11); cell4_1.setCellStyle(style); for(int
		 * i=2;i<=11;i++){ HSSFCell c = row2.createCell(i); c.setCellValue(new
		 * HSSFRichTextString(headers[i])); c.setCellStyle(style); HSSFCell c1 =
		 * row3.createCell(i); if(i==10){ c1.setCellValue(new
		 * HSSFRichTextString((i-1)+"(2-5-6)")); } else{ c1.setCellValue(new
		 * HSSFRichTextString((i-1)+"")); } c1.setCellStyle(style); }
		 * //设置合并单元格的区域 Region region1 = new Region(5, (short)0, 7, (short)0);
		 * Region region2 = new Region(5, (short)1, 7, (short)1); Region region3
		 * = new Region(5, (short)2, 5, (short)4); Region region4 = new
		 * Region(5, (short)5, 5, (short)9); Region region5 = new Region(5,
		 * (short)10, 5, (short)11); sheet.addMergedRegion(region1);
		 * sheet.addMergedRegion(region2); sheet.addMergedRegion(region3);
		 * sheet.addMergedRegion(region4); sheet.addMergedRegion(region5); //
		 * 创建Excel文件
		 */ }

	// 检索有几个"</p>"元素
	private int counter = 0;
	public int total = 0;

	public int StringP(String str) {
		if (str.indexOf("<p>") == -1) {
			return 0;
		} else if (str.indexOf("<p>") != -1) {
			counter++;
			StringP(str.substring(str.indexOf("<p>") + 3));
			return counter;
		}
		return 0;
	}

	// 单个凭证检索p元素的个数
	int counter1 = 0;

	public int signP(String str) {
		if (str.indexOf("<p>") == -1) {
			return 0;
		} else if (str.indexOf("<p>") != -1) {
			counter1++;
			signP(str.substring(str.indexOf("<p>") + 3));
			return counter1;
		}
		return 0;
	}

	// 凭证管理导出
	@SuppressWarnings("deprecation")
	public byte[] exportExcelByPZ(String title, String[] headers, String[] fields, Collection<T> dataset,
			OutputStream out, String pattern, String gs, String qj) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			// 声明一个工作薄

			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);
			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();

			// 设置这些样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			// font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBold(true); // 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			// 声明一个画图的顶级管理器
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			// 定义注释的大小和位置,详见文档
//			HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(1, 1, 1, 1, (short) 4, 2, (short) 6, 5));
			// 设置注释内容
//			comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
			// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//			comment.setAuthor("leno");
			// 产生表格标题行
			int rowno = 0;
			HSSFRow row = sheet.createRow(rowno++);
			HSSFCellStyle style1 = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20); // 字号
			f.setBold(true); // 加粗
            f.setBold(true);
			style1.setFont(f);
			style1.setAlignment(HorizontalAlignment.CENTER); // 水平居中
			style1.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
			HSSFCellStyle style3 = workbook.createCellStyle();
			HSSFFont f1 = workbook.createFont();
			f1.setFontHeightInPoints((short) 12); // 字号
			f1.setBold(true); // 加粗
			style3.setFont(f1);
			style3.setAlignment(HorizontalAlignment.CENTER);
			style3.setAlignment(HorizontalAlignment.LEFT); // 居左
			HSSFCellStyle style4 = workbook.createCellStyle();
			HSSFFont f2 = workbook.createFont();
			f2.setFontHeightInPoints((short) 12); // 字号
			f2.setBold(true); // 加粗
			style4.setFont(f2);
			style4.setAlignment(HorizontalAlignment.CENTER);
			style4.setAlignment(HorizontalAlignment.RIGHT); // 居右
			// sheet.addMergedRegion(new CellRangeAddress(4, 4, ((headers.length
			// -1)/2)+1, (headers.length -1)));
			// }

			for (int i = 0; i < headers.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style);
				HSSFRichTextString text = new HSSFRichTextString(headers[i]);
				cell.setCellValue(text);

			}

			// 报表金额字段
			String[] decimalCols = new String[] { "jf", "df", "jfmny", "dfmny", "bqje", "byje", "fsjf", "fsdf",
					"jftotal", "dftotal", "bnlj", "bnljje", "sqje", "ye", "qcjf", "qcdf", "qmjf", "qmdf", "ncye1",
					"ncye2", "qmye1", "qmye2", "busitax", "csmaintax", "partstudytax", "spendtax", "studytax", "zztax",
					"taxsum", "lwsr", "qtywsr", "whsr", "zysr", "insum", };
			Arrays.sort(decimalCols); // 先排好序，再用binarySearch查找时是O(log(n))的复杂度

			// 遍历集合数据，产生数据行
			// Iterator<T> it = dataset.iterator();
			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.setFillForegroundColor(HSSFColor.WHITE.index);
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			HSSFFont font2 = workbook.createFont();
			// 把字体应用到当前的样式
			style2.setFont(font2);
			// index=0;
			// int singP = 0;
			// int flag1 = index+1;
			// boolean boo = true;
			// while(it.hasNext()){
			// T t = (T) it.next();
			// Class tCls = t.getClass();
			// for(int i=0;i<fields.length;i++){
			// String fieldName = fields[i];
			// String getMethodName = "get"
			// + fieldName.substring(0, 1).toUpperCase()
			// + fieldName.substring(1);
			// Method getMethod = tCls.getMethod(getMethodName,
			// new Class[]
			// {});
			// if("getKmmchie".equals(getMethodName)){
			// String value = (String)getMethod.invoke(t, new Object[]
			// {});
			// total = total + StringP(value);
			// if(boo){
			// singP = signP(value);
			// boo = false;
			// counter1 = 0;
			// System.out.println(singP);
			// }
			// }
			// }
			//
			// }
			// while (it.hasNext())
			Iterator<T> it1 = dataset.iterator();
			while (it1.hasNext()) {
				PzglPageVo[] vos = (PzglPageVo[]) it1.next();
				int rowfrom = 0;
				int rowto = 0;
				rowfrom = rowno;
				for (PzglPageVo vo : vos) {
					HSSFRow pzrow = sheet.createRow(rowno++);

					for (int i = 0; i < fields.length; i++) {
						HSSFCell cell = pzrow.createCell(i);
						style2 = getCommonstyle2(workbook, fields[i], decimalCols,"");
						cell.setCellStyle(style2);

						String fieldName = fields[i];
						try {
							Object value = vo.getAttributeValue(fieldName);

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
								bValue = bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
								textValue = bValue.toString();
								if (!"ye".equals(fieldName) && !"sl".equals(fieldName) && bValue.doubleValue() == 0) {
									textValue = "";
								}
							} else if (value instanceof Date) {
								Date date = (Date) value;
								SimpleDateFormat sdf = new SimpleDateFormat(pattern);
								textValue = sdf.format(date);
							} else if (value instanceof Integer) {
								if ((Integer) value == 8) {
									textValue = "自由态";
								} else if ((Integer) value == 1) {
									textValue = "审批通过";
								} else if ((Integer) value == -1) {
									textValue = "暂存态";
								}
							} else {
								// 其它数据类型都当作字符串简单处理
								textValue = value == null ? null : value.toString();
							}
							// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
							if (textValue != null) {
								HSSFRichTextString richString = new HSSFRichTextString(textValue);
								cell.setCellValue(richString);

							}
						} catch (Exception e) {
							// e.printStackTrace();
						} finally {
							// 清理资源
						}
					}

				}
				rowto = rowno - 1;
				int[] cols = { 0, 1, 2, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
				for (int col : cols) {
					sheet.addMergedRegion(new CellRangeAddress((short) rowfrom, (short) col, (short) rowto, (short) col));
				}

			}

			try {
				workbook.write(out);
			} catch (IOException e) {
				// e.printStackTrace();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return workbook.getBytes();
	}

	public byte[] exportExcel(String title, List listTab, List<String[]> listcn, List<String[]> listen,
                              List<List<StNssbInfoVO>> vos, OutputStream out) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			String flag = null;
			String[] headers = null;
			String[] fields = null;
			Collection<T> dataset = null;
			String pattern = "yyyy-MM-dd";
			String gs = null;
			String qj = null;
			// 生成一个表格
			int index1 = 0;
			String[] number = null;
			String[] titleName = null;
			HSSFSheet sheet = null;
			// //行宽
			// sheet.setDefaultColumnWidth(15);

			// 声明一个画图的顶级管理器
			HSSFPatriarch patriarch = null;
			// 定义注释的大小和位置,详见文档
//			HSSFComment comment = null;

			HSSFCellStyle style4 = null;
			HSSFFont f2 = null;

			// 代码
			HSSFRow row0 = null;
			HSSFCell cell0 = null;
			// 报表标题
			HSSFRow row_name = null;
			HSSFCellStyle style1 = null;
			HSSFFont f = null; // 加粗

			HSSFCell cell3 = null;

			HSSFRow row_names = null;
			HSSFCell cell2s = null;
			HSSFCell cell3s = null;
			// 生成一个样式
			HSSFCellStyle style = null;

			// 生成一个字体
			HSSFFont font = null;

			int index;
			HSSFRow row1 = null;
			HSSFRow row2 = null;

			HSSFCell c = null;

			for (int j = 0; j < vos.size(); j++) {
				flag = vos.get(j).get(0).getFlag();
				headers = listcn.get(j);
				fields = listen.get(j);
				dataset = (Collection<T>) vos.get(j);
				// String pattern = "yyyy-MM-dd";
				gs = vos.get(j).get(0).getGs();
				qj = vos.get(j).get(0).getPeriod();
				// 生成一个表格
				index1 = Integer.parseInt((String) listTab.get(j));
				number = daima();
				titleName = title();
				sheet = workbook.createSheet(number[index1] + titleName[index1]);
				// 行宽
				sheet.setDefaultColumnWidth(15);

				// 声明一个画图的顶级管理器
				patriarch = sheet.createDrawingPatriarch();
				// 定义注释的大小和位置,详见文档
//				comment = patriarch.createComment(new HSSFClientAnchor(1, 1, 1, 1, (short) 4, 2, (short) 6, 5));
				// 设置注释内容
//				comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
				// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//				comment.setAuthor("leno");

				style4 = workbook.createCellStyle();
				f2 = workbook.createFont();
				f2.setFontHeightInPoints((short) 12); // 字号
				f2.setBold(true); // 加粗
				style4.setFont(f2);
				style4.setAlignment(HorizontalAlignment.LEFT); // 居左

				// 代码
				row0 = sheet.createRow(0);
				cell0 = row0.createCell(0);
				cell0.setCellValue(number[index1]);
				cell0.setCellStyle(style4);
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (headers.length - 1)));

				// 报表标题
				row_name = sheet.createRow(1);
				style1 = workbook.createCellStyle();
				f = workbook.createFont();
				f.setFontHeightInPoints((short) 20); // 字号
				f.setBold(true); // 加粗
				style1.setFont(f);
				style1.setAlignment(HorizontalAlignment.CENTER); // 水平居中
				style1.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
				cell3 = row_name.createCell(0);
				cell3.setCellStyle(style1);
				cell3.setCellValue(titleName[index1]);
				// 合并单元格
				sheet.addMergedRegion(new CellRangeAddress(1, 3, 0, (headers.length - 1)));

				// 公司和期间
				/*
				 * HSSFRow row_names = sheet.createRow(4);
				 * row_names.setHeight((short) (0 * 0)); HSSFCellStyle style3 =
				 * workbook.createCellStyle(); HSSFFont f1 =
				 * workbook.createFont(); f1.setFontHeightInPoints((short) 12);
				 * //字号 f1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //加粗
				 * style3.setFont(f1);
				 * style3.setAlignment(HorizontalAlignment.LEFT); //居左 HSSFCell
				 * cell3s = row_names.createCell(0);
				 * cell3s.setCellValue("公司："+gs); cell3s.setCellStyle(style3);
				 * sheet.addMergedRegion(new CellRangeAddress(3, 4,
				 * 0,(headers.length -1)/2));
				 */
				row_names = sheet.createRow(4);
				cell2s = row_names.createCell(0);
				cell3s = row_names.createCell(headers.length - 1);
				cell2s.setCellValue("公司：" + gs);
				cell3s.setCellValue("期间：" + qj);
				cell2s.setCellStyle(style4);
				cell3s.setCellStyle(style4);
				sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, (headers.length - 2)));

				/*
				 * if(title.toString().equals("资 产 负 债 表")){ HSSFCell cell4ss =
				 * row_names.createCell(((headers.length -1)/2)+4);
				 * cell4ss.setCellValue("单位：元"); cell4ss.setCellStyle(style4);
				 * }else if(title.toString().equals("利 润 表"
				 * )||title.toString().equals("利 润 表 季 报")){ HSSFCell cell4ss =
				 * row_names.createCell(((headers.length -1)/2)+2);
				 * cell4ss.setCellValue("单位：元"); cell4ss.setCellStyle(style4); }
				 * else{ sheet.addMergedRegion(new CellRangeAddress(4, 4,
				 * ((headers.length -1)/2)+1, (headers.length -1))); }
				 */

				// 产生表格标题行

				// 生成一个样式
				style = workbook.createCellStyle();
				// 设置这些样式
				style.setFillForegroundColor(HSSFColor.WHITE.index);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				// 生成一个字体
				font = workbook.createFont();
				// font.setColor(HSSFColor.VIOLET.index);
				font.setFontHeightInPoints((short) 12);
				font.setBold(true); // 加粗
				// 把字体应用到当前的样式
				style.setFont(font);

				// int index;
				if (StringUtil.isEmpty(flag) || Integer.parseInt(flag) < 2) {
					// 通用处理表格标题行：创建多层表头，传入标题开始行index，返回数据行index
					index = processMultiHead(headers, sheet, 5, style);
				} else {
					if (flag.equals("2") || flag.equals("3") || flag.equals("4")) {
						index = 7;
						row1 = sheet.createRow(5);
						row2 = sheet.createRow(6);
						for (int i = 0; i < headers.length; i++) {
							c = row1.createCell(i);
							c.setCellValue(new HSSFRichTextString(headers[i]));
							c.setCellStyle(style);
							if (i > 1) {
								c = row2.createCell(i);
								c.setCellValue(new HSSFRichTextString((i - 1) + ""));
								c.setCellStyle(style);
							}
						}

						// 设置合并单元格的区域
						sheet.addMergedRegion(new CellRangeAddress(5, 6, 0, 0));
						sheet.addMergedRegion(new CellRangeAddress(5, 6, 1, 1));
					} else if (flag.equals("5")) {
						index = write5(sheet, 5, headers, style);
					} else if (flag.equals("6")) {
						index = write4(sheet, 5, headers, style);
					} else if (flag.equals("7")) {
						index = write6(sheet, 5, headers, style);
					} else {
						HSSFRow row = sheet.createRow(5);
						index = 6;
						for (int i = 0; i < headers.length; i++) {
							c = row.createCell(i);
							c.setCellStyle(style);
							// HSSFRichTextString text = new
							// HSSFRichTextString(headers[i]);
							c.setCellValue(new HSSFRichTextString(headers[i]));
						}
					}
				}

				getColumnWidths(sheet, Integer.parseInt(flag));
				HSSFRow row;
				// 遍历集合数据，产生数据行
				Iterator<T> it = dataset.iterator();
				HSSFCellStyle bodyStyle = workbook.createCellStyle();
				bodyStyle.setFillForegroundColor(HSSFColor.WHITE.index);
				bodyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				bodyStyle.setBorderBottom(BorderStyle.THIN);
				bodyStyle.setBorderLeft(BorderStyle.THIN);
				bodyStyle.setBorderRight(BorderStyle.THIN);
				bodyStyle.setBorderTop(BorderStyle.THIN);
				bodyStyle.setAlignment(HorizontalAlignment.LEFT);
				// 生成另一个字体
				HSSFFont font2 = workbook.createFont();
				// 把字体应用到当前的样式
				bodyStyle.setFont(font2);
				while (it.hasNext()) {
					row = sheet.createRow(index);
					T t = (T) it.next();
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					for (int i = 0; i < fields.length; i++) {
						c = row.createCell(i);
						String fieldName = fields[i];
						String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						try {
							bodyStyle.setAlignment(HorizontalAlignment.LEFT);
							c.setCellStyle(bodyStyle);
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
								bValue = bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
								textValue = bValue.toString();
								if (!"ye".equals(fieldName) && !"sl".equals(fieldName) && bValue.doubleValue() == 0) {
									textValue = "";
								}
							} else if (value instanceof Date) {
								Date date = (Date) value;
								SimpleDateFormat sdf = new SimpleDateFormat(pattern);
								textValue = sdf.format(date);
							} else {
								// 其它数据类型都当作字符串简单处理
								textValue = value == null ? null : value.toString();
								if (textValue != null) {
									if (textValue.equals("STA_")) {
										textValue = "*";
									}
									if (textValue.equals("NA_")) {
										textValue = "";
									}
								}
							}
							// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
							if (textValue != null) {
								Pattern p = Pattern.compile("^(-){0,1}[0-9]+([.]{1}[0-9]+){0,1}$");
								Matcher matcher = p.matcher(textValue);
								if (matcher.matches() && !fields[i].equals("vno") && !fields[i].equals("rp_cnd")) {
									// 是数字当作double处理
									HSSFCellStyle cellStyle1 = c.getCellStyle(); // 获取cell1的style
									HSSFCellStyle st = workbook.createCellStyle(); // 创建新的style
									st.cloneStyleFrom(cellStyle1); // 拷贝旧的style
									st.setAlignment(HorizontalAlignment.RIGHT); // 然后在新的style上设置对齐方式
									if (fields[i].equals("rp_vssgdkcl")) {
										HSSFRichTextString richString = null;
										// 直接取整
										if (4 == textValue.length() && 1 == textValue.lastIndexOf(".")
												|| 1 == Double.parseDouble(textValue)) {
											richString = new HSSFRichTextString(
													(int) convert(Double.parseDouble(textValue) * 100) + "%");
										} else {
											richString = new HSSFRichTextString(
													convert(Double.parseDouble(textValue) * 100) + "%");
										}
										c.setCellValue(richString);
										c.setCellStyle(st);
									} else {
										c.setCellValue(Double.parseDouble(textValue));
										HSSFDataFormat df = workbook.createDataFormat();
										st.setDataFormat(df.getFormat("#,##0.00"));
										c.setCellStyle(st); // 应用新的style
									}

								} else if (fields[i].equals("vno") || fields[i].equals("rp_cnd")) {
									HSSFRichTextString richString = new HSSFRichTextString(textValue);
									c.setCellValue(richString);
									HSSFCellStyle cellStyle1 = c.getCellStyle();
									HSSFCellStyle st = workbook.createCellStyle(); // 创建新的style
									st.cloneStyleFrom(cellStyle1); // 拷贝旧的style
									st.setAlignment(HorizontalAlignment.CENTER); // 然后在新的style上设置对齐方式

									c.setCellStyle(st); // 应用新的style
								} else {
									HSSFRichTextString richString = new HSSFRichTextString(textValue);
									c.setCellValue(richString);
									if (fields[i].equals("citemclass")) {
										// HSSFCellStyle cellStyle1 =
										// cell.getCellStyle();
										HSSFCellStyle st = workbook.createCellStyle(); // 创建新的style
										st.cloneStyleFrom(style); // 拷贝旧的style
										// st.setAlignment(HorizontalAlignment.CENTER);
										// //然后在新的style上设置对齐方式
										st.setFont(font2);
										c.setCellStyle(st); // 应用新的style
									}
									if (textValue.equals("*")) {
										HSSFCellStyle cellStyle1 = c.getCellStyle();
										HSSFCellStyle st = workbook.createCellStyle(); // 创建新的style
										st.cloneStyleFrom(cellStyle1); // 拷贝旧的style
										st.setAlignment(HorizontalAlignment.RIGHT); // 然后在新的style上设置对齐方式

										c.setCellStyle(st); // 应用新的style
									}

								}
							}
						} catch (SecurityException e) {
							// e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// e.printStackTrace();
						} catch (IllegalAccessException e) {
							// e.printStackTrace();
						} catch (InvocationTargetException e) {
							// e.printStackTrace();
						} catch (Exception e) {
							// e.printStackTrace();
						} finally {
							// 清理资源
						}
					}
					index++;
				}
				if (listTab.get(j).toString().equals("9")) {// 弥补亏损
					sheet.addMergedRegion(new CellRangeAddress(vos.get(j).size() + 7, vos.get(j).size() + 7, 1, 11));
				} else if (listTab.get(j).toString().equals("0")) {// 主表
					sheet.addMergedRegion(new CellRangeAddress(6, 18, 1, 1));
					sheet.addMergedRegion(new CellRangeAddress(19, 28, 1, 1));
					sheet.addMergedRegion(new CellRangeAddress(29, 41, 1, 1));
					sheet.addMergedRegion(new CellRangeAddress(42, 43, 1, 1));
				}
			}
			try {
				workbook.write(out); // 里面本质上会调OutputStream
										// out的write()，即使这里抛异常，也要保证外面能关闭out
			} catch (IOException e) {
				// e.printStackTrace();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return workbook.getBytes();
	}

	/**
	 * 获取数据行单元格格式，并按具体字段调整
	 *
	 * @param workbook
	 * @param field
	 * @param decimalCols
	 *            金额字段名称数组（显示为#,##0.00格式），使用前需先排序。此参数可以传null
	 * @return
	 */
	private HSSFCellStyle getCommonstyle2(HSSFWorkbook workbook, String field, String[] decimalCols,String pk_corp) {
		if (StringUtil.isEmpty(field))
			return null;


		// llh 按字体大小、对齐方式、精度等做key，缓存Style
		String key;
		if (decimalCols != null && Arrays.binarySearch(decimalCols, field) >= 0) { // 右对齐，精度为2
			if (field.equals("ybjf") || field.equals("ybdf") || field.equals("ybye")
					|| field.equals("ybqcdf") || field.equals("ybqcjf") || field.equals("ybfsjf") || field.equals("ybfsdf")
					|| field.equals("ybjftotal") || field.equals("ybdftotal") || field.equals("ybqmjf") || field.equals("ybqmdf")
					|| field.equals("ybqcyejf") || field.equals("ybqcyedf") || field.equals("ybbqfsjf") || field.equals("ybbqfsdf")
					|| field.equals("ybbnljjf") || field.equals("ybbnljdf") || field.equals("ybqmyejf") || field.equals("ybqmyedf")
				) {
				key = "right_4";
			}else if(field.equals("hl") ){
				if(!StringUtil.isEmpty(pk_corp)){
					IZxkjPlatformService zxkjPlatformService = (IZxkjPlatformService)SpringUtils.getBean("zxkjPlatformService");
					Integer jd =  new ReportUtil(zxkjPlatformService).getHlJd(pk_corp);
					key = "right_"+jd;
				}else{
					key = "right_4";
				}
			} else {
				key = "right_2";
			}
		} else { // 左对齐
			key = "left";
		}

		HSSFCellStyle style2;
		if (styleMap.containsKey(key)) {
			style2 = styleMap.get(key);
		} else {
			// 缓存中没有，则新建一个样式
			style2 = workbook.createCellStyle();
			style2.setFillForegroundColor(HSSFColor.WHITE.index);
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			// 生成另一个字体
			HSSFFont font2 = workbook.createFont();
			// 把字体应用到当前的样式
			style2.setFont(font2);

			if (key.equals("left")) {
				style2.setAlignment(HorizontalAlignment.LEFT);
			} else if (key.indexOf("right_")>=0) {
				String[] strs = key.split("_");
				StringBuffer buffer = new StringBuffer();
				for(int i=0;i<Integer.parseInt(strs[1]);i++){
					buffer.append("0");
				}
				HSSFDataFormat df = workbook.createDataFormat(); // 此处设置数据格式
				style2.setDataFormat(df.getFormat("#,##0."+buffer)); // 保留小数点后2位
				style2.setAlignment(HorizontalAlignment.RIGHT);
			}
			styleMap.put(key, style2);
		}

		return style2;
	}

	public byte[] exportExcel1(String title, List<String> headers, List<String> fields, JSONArray array,
			OutputStream out, String pattern, String gs, String qj) {
		// 格式化header数据
		Map<String, List<String>> headermap = new HashMap<String, List<String>>();
		String[] temps = null;
		for (String str : headers) {
			if (str.indexOf("_") > 0) {
				temps = str.split("_");
				if (headermap.containsKey(temps[0])) {
					headermap.get(temps[0]).add(temps[1]);
				} else {
					List<String> listemp = new ArrayList<String>();
					listemp.add(temps[1]);
					headermap.put(temps[0], listemp);
				}
			}
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();
			HSSFCellStyle st = workbook.createCellStyle();
			st.setBorderBottom(BorderStyle.THIN);
			st.setBorderLeft(BorderStyle.THIN);
			st.setBorderRight(BorderStyle.THIN);
			st.setBorderTop(BorderStyle.THIN);
			st.setAlignment(HorizontalAlignment.RIGHT);

			HSSFCellStyle st1 = workbook.createCellStyle();
			st1.setBorderBottom(BorderStyle.THIN);
			st1.setBorderLeft(BorderStyle.THIN);
			st1.setBorderRight(BorderStyle.THIN);
			st1.setBorderTop(BorderStyle.THIN);
			st1.setAlignment(HorizontalAlignment.LEFT);

			// 设置这些样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			// font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBold(true); // 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			HSSFCellStyle style1 = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20); // 字号
			f.setBold(true); // 加粗
			style1.setFont(f);
			style1.setAlignment(HorizontalAlignment.CENTER); // 水平居中
			style1.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

			HSSFCellStyle style4 = workbook.createCellStyle(); // 表头样式
			HSSFFont f2 = workbook.createFont();
			f2.setFontHeightInPoints((short) 12); // 字号
			f2.setBold(true); // 加粗
			style4.setFont(f2);
			style4.setAlignment(HorizontalAlignment.CENTER);
			style4.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
			// style4.setAlignment(HorizontalAlignment.LEFT); //居左
			int headerlength = headers.size();
			int fieldlength = fields.size();
			// 合并标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell celltitle = rowtitle.createCell(0);
			celltitle.setCellValue(title);
			celltitle.setCellStyle(style1);
            CellRangeAddress region = new CellRangeAddress(0, (short) 0, 2, (short) (fieldlength - 1));
			sheet.addMergedRegion(region); // 合并标题


			HSSFRow row_names = sheet.createRow(3);
			HSSFFont f1 = workbook.createFont();
			f1.setFontHeightInPoints((short) 12); // 字号
			f1.setBold(true); // 加粗
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (fieldlength - 1) / 3));
			sheet.addMergedRegion(new CellRangeAddress(3, 3, ((fieldlength - 1) / 3) + 1, (fieldlength - 1)*2 / 3));
			sheet.addMergedRegion(new CellRangeAddress(3, 3, ((fieldlength - 1) * 2 / 3) + 1, fieldlength-1));
			HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
			hssfCellStyle.setFont(f1);
			hssfCellStyle.setAlignment(HorizontalAlignment.LEFT);
			HSSFCell hssfCell = row_names.createCell(0);
			hssfCell.setCellValue("公司：" + gs);
			hssfCell.setCellStyle(hssfCellStyle);
			hssfCell = row_names.createCell(((fieldlength - 1) / 3)+1);
			hssfCellStyle = workbook.createCellStyle();
			hssfCellStyle.setFont(f1);
			hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hssfCell.setCellValue("期间：" + qj);
			hssfCell.setCellStyle(hssfCellStyle);
			hssfCell = row_names.createCell(((fieldlength - 1) * 2 / 3) + 1);
			hssfCellStyle = workbook.createCellStyle();
			hssfCellStyle.setFont(f1);
			hssfCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			hssfCell.setCellValue("单位：元");
			hssfCell.setCellStyle(hssfCellStyle);


			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			int newxtvalue = 0;
			for (int i = 0; i < headerlength; i++) {
				if (headerlength != fieldlength) {
					HSSFCell cell1 = row.createCell(i);
					HSSFCell cell2 = row2.createCell(i);
					if (i < 7) {
                        CellRangeAddress region1 = new CellRangeAddress(4, (short) i, 5, (short) i);
						sheet.addMergedRegion(region1);
						cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
					}
					if (i == 7) {
						cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
						cell2.setCellValue(new HSSFRichTextString(headers.get(i + 1).split("_")[1]));
                        CellRangeAddress region1 = new CellRangeAddress(4, (short) 7, 4,
								(short) (headermap.get(headers.get(i)).size() + 6));
						sheet.addMergedRegion(region1);
						newxtvalue = headermap.get(headers.get(i)).size();
					}

					if (i == (newxtvalue + 7) && (i + 1) < headers.size()) {
						cell1.setCellValue(new HSSFRichTextString(headers.get(i + 1)));
						cell2.setCellValue(new HSSFRichTextString(headers.get(i + 2).split("_")[1]));
                        CellRangeAddress region1 = new CellRangeAddress(4, (short) (newxtvalue + 7), 4, (short) (fieldlength - 1));
						sheet.addMergedRegion(region1);
					}
					if (i < fieldlength) {
						if (i > 7 && i < (newxtvalue + 7)) {
							cell2.setCellValue(new HSSFRichTextString(headers.get(i + 1).split("_")[1]));
						} else if (i >= (newxtvalue + 7) && i != (newxtvalue + 7)) {
							cell2.setCellValue(new HSSFRichTextString(headers.get(i + 2).split("_")[1]));
						}
						cell2.setCellStyle(style);
						cell1.setCellStyle(style);
					}
				} else {
					HSSFCell cell1 = row.createCell(i);
					cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
					cell1.setCellStyle(style);
				}
			}
			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, String> map = (Map<String, String>) array.get(i);
				Set<String> keySet = map.keySet();
				keySet.remove("pk_tzpz_h");
				keySet.remove("pk_accsubj");
				int count = 0;
				for (String key : fields) {
					try {
						HSSFCell cell = row1.createCell(count);
						HSSFRichTextString richString = new HSSFRichTextString(map.get(key));
						cell.setCellValue(richString);
						if (!key.equals("rq") && !key.equals("zy") && !key.equals("fx") && !key.equals("pzh")) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						// e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// e.printStackTrace();
					} catch (Exception e) {
						// e.printStackTrace();
					} finally {
						// 清理资源
					}
				}
			}

			try {
				workbook.write(out);
			} catch (IOException e) {
				// e.printStackTrace();
			}
		} catch (Exception e) {
			log.error("错误",e);
		}
		return workbook.getBytes();

	}

	/*
	 * 三行数据表头拼接情况1
	 */
	public static int write5(HSSFSheet sheet, int firstrow, String[] headers, HSSFCellStyle style) throws IOException {
		// 创建合并单元格的第一个单元格数据
		HSSFRow row1 = sheet.createRow(firstrow);
		HSSFRow row2 = sheet.createRow(firstrow + 1);
		HSSFRow row3 = sheet.createRow(firstrow + 2);
		HSSFCell cell0 = row1.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("行次"));
		cell0.setCellStyle(style);
		// 多这一步解决合并时边框不显示问题
		HSSFCell cell01 = row2.createCell(0);
		HSSFCell cell02 = row3.createCell(0);
		cell01.setCellStyle(style);
		cell02.setCellStyle(style);
		//
		HSSFCell cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("受赠单位名称"));
		cell1.setCellStyle(style);
		HSSFCell cell2 = row1.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("公益性捐助"));
		cell2.setCellStyle(style);
		for (int i = 0; i < 3; i++) {
			HSSFCell cell = row1.createCell(i + 3);
			cell.setCellStyle(style);
		}
		HSSFCell cell3 = row1.createCell(6);
		cell3.setCellValue(new HSSFRichTextString("非公益性捐助"));
		cell3.setCellStyle(style);
		HSSFCell cell4 = row1.createCell(7);
		HSSFCell cell_4 = row2.createCell(7);
		cell4.setCellValue(new HSSFRichTextString("纳税调整金额"));
		cell4.setCellStyle(style);
		cell_4.setCellStyle(style);
		for (int i = 2; i < 7; i++) {
			HSSFCell c = row2.createCell(i);
			c.setCellValue(new HSSFRichTextString(headers[i]));
			c.setCellStyle(style);
		}
		for (int i = 1; i < 8; i++) {
			HSSFCell c1 = row3.createCell(i);
			if (i == 5) {
				c1.setCellValue(new HSSFRichTextString((i) + "(2-4)"));
				c1.setCellStyle(style);
			} else if (i == 7) {
				c1.setCellValue(new HSSFRichTextString((i) + "(5+6)"));
				c1.setCellStyle(style);
			} else {
				c1.setCellValue(new HSSFRichTextString((i) + ""));
				c1.setCellStyle(style);
			}
		}
		// 设置合并单元格的区域
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow, 2, 5));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 7, 7));
		// sheet.addMergedRegion(new CellRangeAddress(6, 6, 10, 11));

		return firstrow + 3;
	}

	/*
	 * 三行数据表头拼接情况2
	 */
	public static int write6(HSSFSheet sheet, int firstrow, String[] headers, HSSFCellStyle style) throws IOException {
		// 创建合并单元格的第一个单元格数据
		HSSFRow row1 = sheet.createRow(firstrow);
		HSSFRow row2 = sheet.createRow(firstrow + 1);
		HSSFRow row3 = sheet.createRow(firstrow + 2);
		// 多这一步解决合并时边框不显示问题
		HSSFCell cell01 = row2.createCell(0);
		HSSFCell cell02 = row3.createCell(0);
		cell01.setCellStyle(style);
		cell02.setCellStyle(style);
		for (int i = 0; i < 4; i++) {
			HSSFCell c1 = row1.createCell(i + 7);
			c1.setCellStyle(style);
		}
		//
		for (int i = 0; i < 7; i++) {
			/*
			 * if(i>6){ HSSFCell c = row1.createCell(i+4); HSSFCell c2 =
			 * row2.createCell(i+4); c.setCellValue(new
			 * HSSFRichTextString(headers[i])); c.setCellStyle(style);
			 * c2.setCellStyle(style); }
			 */
			HSSFCell c = row1.createCell(i);
			HSSFCell c2 = row2.createCell(i);
			if (i == 6) {
				c.setCellValue("以前年度亏损已弥补额");
			} else {
				c.setCellValue(new HSSFRichTextString(headers[i]));
			}
			c.setCellStyle(style);
			c2.setCellStyle(style);
		}
		for (int i = 0; i < 5; i++) {
			HSSFCell c = row2.createCell(i + 6);
			c.setCellValue(new HSSFRichTextString(headers[i + 6]));
			c.setCellStyle(style);
		}
		HSSFCell c = row1.createCell(11);
		HSSFCell c_1 = row2.createCell(11);
		HSSFCell c2 = row1.createCell(12);
		HSSFCell c_2 = row2.createCell(12);
		c.setCellValue(headers[11]);
		c2.setCellValue(headers[12]);
		c.setCellStyle(style);
		c2.setCellStyle(style);
		c_1.setCellStyle(style);
		c_2.setCellStyle(style);
		for (int i = 1; i < 12; i++) {
			HSSFCell c1 = row3.createCell(i + 1);
			c1.setCellValue(new HSSFRichTextString((i) + ""));
			c1.setCellStyle(style);
		}
		// 设置合并单元格的区域
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 4, 4));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 5, 5));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow, 6, 10));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 11, 11));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 1, 12, 12));

		return firstrow + 3;
	}

	/*
	 * 三行数据拼接情况3
	 */
	public static int write4(HSSFSheet sheet, int firstrow, String[] headers, HSSFCellStyle style) throws IOException {
		// 创建合并单元格的第一个单元格数据
		HSSFRow row1 = sheet.createRow(firstrow);
		HSSFRow row2 = sheet.createRow(firstrow + 1);
		HSSFRow row3 = sheet.createRow(firstrow + 2);
		HSSFCell cell0 = row1.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("行次"));
		cell0.setCellStyle(style);
		// 多这一步解决合并时边框不显示问题
		HSSFCell cell01 = row2.createCell(0);
		HSSFCell cell02 = row3.createCell(0);
		cell01.setCellStyle(style);
		cell02.setCellStyle(style);
		//
		HSSFCell cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("项目"));
		cell1.setCellStyle(style);
		HSSFCell cell2 = row1.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("账载金额"));
		cell2.setCellStyle(style);
		for (int i = 0; i < 2; i++) {
			HSSFCell cell = row1.createCell(i + 3);
			cell.setCellStyle(style);
		}
		HSSFCell cell3 = row1.createCell(5);
		cell3.setCellValue(new HSSFRichTextString("税收金额"));
		cell3.setCellStyle(style);
		for (int i = 0; i < 4; i++) {
			HSSFCell cell = row1.createCell(i + 6);
			cell.setCellStyle(style);
		}
		HSSFCell cell4 = row1.createCell(10);
		cell4.setCellValue(new HSSFRichTextString("纳税调整"));
		cell4.setCellStyle(style);
		HSSFCell cell4_1 = row1.createCell(11);
		cell4_1.setCellStyle(style);
		for (int i = 2; i <= 11; i++) {
			HSSFCell c = row2.createCell(i);
			c.setCellValue(new HSSFRichTextString(headers[i]));
			c.setCellStyle(style);
			HSSFCell c1 = row3.createCell(i);
			if (i == 10) {
				c1.setCellValue(new HSSFRichTextString((i - 1) + "(2-5-6)"));
			} else {
				c1.setCellValue(new HSSFRichTextString((i - 1) + ""));
			}
			c1.setCellStyle(style);
		}
		// 设置合并单元格的区域
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow + 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow, 2, 4));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow, 5, 9));
		sheet.addMergedRegion(new CellRangeAddress(firstrow, firstrow, 10, 11));
		// 创建Excel文件

		return firstrow + 3;
	}

	static double convert(double value) {
		long l1 = Math.round(value * 100); // 四舍五入
		double ret = l1 / 100.0; // 注意：使用 100.0 而不是 100
		return ret;
	}

	private void getColumnWidths(HSSFSheet sheet, int i) {
		if (i == 0) {
			sheet.setColumnWidth(2, 20000);
		}
		if (i == 1) {
			sheet.setColumnWidth(1, 25000);
		}
		if (i == 2) {
			sheet.setColumnWidth(1, 10000);
		}
		if (i == 3) {
			sheet.setColumnWidth(1, 20000);
		}
		if (i == 4) {
			sheet.setColumnWidth(1, 12000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
		}
		/*
		 * if(i==6){ sheet.setColumnWidth(1, 25000); }
		 */
		if (i == 6) {
			sheet.setColumnWidth(1, 15000);
		}
		/*
		 * if(i==7){ sheet.setColumnWidth(1, 25000); }
		 */
		sheet.setColumnWidth(0, 2000);
		return;
	}

	private String[] daima() {
		String[] number = new String[11];
		number[0] = "A100000";
		number[1] = "A101010";
		number[2] = "A102010";
		number[3] = "A104000";
		number[4] = "A105000";
		number[5] = "A105050";
		number[6] = "A105060";
		number[7] = "A105070";
		number[8] = "A105080";
		number[9] = "A106000";
		number[10] = "A107020";
		return number;
	}

	private String[] title() {
		String[] number = new String[11];
		number[0] = "中华人民共和国企业所得税年度纳税申报表（A类）";
		number[1] = "一般企业收入明细表";
		number[2] = "一般企业成本支出明细表";
		number[3] = "期间费用明细表";
		number[4] = "纳税调整项目明细表";
		number[5] = "职工薪酬纳税调整明细表";
		number[6] = "广告费和业务宣传费跨年度纳税调整明细表";
		number[7] = "捐赠支出纳税调整明细表";
		number[8] = "资产折旧、摊销情况及纳税调整明细表 ";
		number[9] = "企业所得税弥补亏损明细表";
		number[10] = "减免所得税优惠明细表";
		return number;
	}

	public byte[] expFile(OutputStream out, String fileName) throws Exception {
		ByteArrayOutputStream bos = null;
		InputStream is = null;
		try {
			Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + fileName);
			is = exportTemplate.getInputStream();
			bos = new ByteArrayOutputStream();
			if(fileName.indexOf(".docx") > 0){
				int byteRead = 0;
				byte[] buffer = new byte[512];
				while ((byteRead = is.read(buffer)) != -1) {
					out.write(buffer, 0, byteRead);
				}
				is.close();
				bos = new ByteArrayOutputStream();
			}else if (fileName.indexOf(".xlsx") > 0) {
				XSSFWorkbook xworkbook = new XSSFWorkbook(is);
				is.close();
				bos = new ByteArrayOutputStream();
				xworkbook.write(bos);
			} else {
				HSSFWorkbook gworkbook = new HSSFWorkbook(is);
				is.close();
				bos = new ByteArrayOutputStream();
				gworkbook.write(bos);
			}
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}