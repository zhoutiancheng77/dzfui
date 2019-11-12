package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntArea;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

@Service("salaryservice_default")
@Slf4j
public class DefaultSalaryReportExcelImpl implements ISalaryReportExcel {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Map<Integer, String> exportExcelFieldColumn(Sheet sheets1, String billtype) {
		Map<Integer, String> fieldColumn = getExpColumnMap(sheets1, billtype);
		return fieldColumn;
	}

	protected Map<String, String> getExportMap(String billtype) {
		String[] CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz", "yanglaobx", "yiliaobx",
				"shiyebx", "zfgjj", "grsds" };

		String[] SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "*收入额", "基本养老保险费", "基本医疗保险费", "失业保险费", "住房公积金",
				"已扣缴税额" };

		if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "*适用公式", "境内所得境内支付", "允许列支的捐赠比例" };
			CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "lhtype", "yfgz", "yxjzbl" };
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "*收入额", "税款负担方式", "允许列支的捐赠比例" };
			CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz", "skfdfs", "yxjzbl" };
		}

		Map<String, String> map = getMapColumn(null, SDJSEXP, CODESJSEXP);
		return map;
	}

	protected Map<Integer, String> getExpColumnMap(Sheet sheets1, String billtype) {

		int iBegin = getTitleColumnRow();
		Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, iBegin);

		Map<String, String> jsimp = getExportMap(billtype);
		Map<Integer, String> map = getColumnMap(map1, jsimp);

		return map;
	}

	@Override
	public byte[] exportExcel(JSONArray array, OutputStream out, String billtype, CorpTaxVo corptaxvo,
			UserVO loginUserVO) throws Exception {

		InputStream is = null;
		ByteArrayOutputStream bos = null;
		try {

			String excelName = getExcelModelName(billtype);
			Resource exportTemplate = new ClassPathResource("template/report/taxdeclaration/" + excelName);
			is = exportTemplate.getInputStream();
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);

			Map<Integer, String> fieldColumn = exportExcelFieldColumn(sheet, billtype);

			int startrow = getExpStartrow();

			int cellrow = getExpCellrow();
			if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
				return null;
			}
			HSSFRow row = sheet.getRow(cellrow);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

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
				rowTo = sheet.getRow(startrow + i);
				if (rowTo == null)
					rowTo = sheet.createRow(startrow + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					if (c1 == null)
						continue;
					c2 = rowTo.getCell(colindex);
					if (c2 == null) {
						c2 = rowTo.createCell(colindex);
						c2.setCellStyle(c1.getCellStyle());
					}
					if (!fieldColumn.containsKey(colindex)) {
					} else {
						map = (Map<String, Object>) array.get(i);
						key = fieldColumn.get(colindex);

						if (key.equals("yfgz")) {
							c2.setCellValue(0.00);
						}
						if (map.get(key) != null) {
							if (!key.equals("sdqjq") && !key.equals("sdqjz") && !key.equals("sfmx")
									&& !key.equals("ygname") && !key.equals("zjbm") && !key.equals("ygbm")
									&& !key.equals("zjlx") && !key.equals("project") && !key.equals("end")
									&& !key.equals("start") && !key.equals("lhtype")) {
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
							if (key.equals("skfdfs")) {
								c2.setCellValue("自行负担");
							} else if (key.equals("yxjzbl")) {
								doublevalue = new DZFDouble(0.3);
								c2.setCellValue(doublevalue.doubleValue());
							}
						}
						continue;
					}
				}
			}
			setSpecial(array, sheet, corptaxvo, loginUserVO, billtype);
			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
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

	protected void setSpecial(JSONArray array, HSSFSheet sheet, CorpTaxVo corptaxvo, UserVO loginUserVO,
			String billtype) {

	}

	@Override
	public int getExpStartrow() {
		return 1;
	}

	@Override
	public int getExpCellrow() {
		return 0;
	}

	@Override
	public String getExcelModelName(String billtype) {
		String excelName = "salary_NSSBreport11.xls";
		if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			excelName = "salary_NSSBreport_wj_bj.xls";
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			excelName = "salary_NSSBreport_lw_bj.xls";
		}
		return excelName;
	}

	protected DZFDouble getDZFDoubleIsNull(Object o) {
		DZFDouble dou = DZFDouble.ZERO_DBL;
		if (o != null) {
			dou = new DZFDouble(o.toString());
		}
		return dou;
	}

	protected double getkcnum(String billtype, String qj) {
		double kcnum = 0.0D;
		if ((StringUtil.isEmpty(billtype)) || (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
			if ("2018-10".compareTo(qj) <= 0)
				kcnum = 5000.0D;
			else
				kcnum = 3500.0D;
		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			kcnum = 4800.0D;
		}
		return kcnum;
	}

	protected double getQuickdeduction(double salaryBeforeTax, double kcnum, String billtype, String qj) {
		double taxbase = 0.0D;
		if ((StringUtil.isEmpty(billtype)) || (!billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())))
			taxbase = getPersonQuickdeduction(salaryBeforeTax, kcnum, qj);
		else {
			taxbase = getRemunerationQuickdeduction(salaryBeforeTax, kcnum);
		}
		return taxbase;
	}

	private double getRemunerationQuickdeduction(double salaryBeforeTax, double kcnum) {
		double taxbase = 0.0D;

		taxbase = salaryBeforeTax;
		double Quickdeduction = 0.0D;
		if (taxbase <= 0.0D) {
			return 0.0D;
		}
		if (taxbase <= 800.0D)
			return 0.0D;
		if (taxbase < 4000.0D)
			return 0.0D;
		if (taxbase < 20000.0D) {
			return 0.0D;
		} else if (taxbase < 50000.0D) {
			Quickdeduction = 2000.0D;
		} else {
			Quickdeduction = 7000.0D;
		}
		return Quickdeduction;
	}

	private double getPersonQuickdeduction(double salaryBeforeTax, double kcnum, String qj) {
		double taxbase = salaryBeforeTax - kcnum;
		double Quickdeduction = 0.0D;

		if ("2018-10".compareTo(qj) <= 0) {
			if (taxbase <= 0.0D) {
				return 0.0D;
			}
			if (taxbase <= 3000.0D) {
				Quickdeduction = 0.0D;
			} else if (taxbase <= 12000.0D) {
				Quickdeduction = 210.0D;
			} else if (taxbase <= 25000.0D) {
				Quickdeduction = 1410.0D;
			} else if (taxbase <= 35000.0D) {
				Quickdeduction = 2660.0D;
			} else if (taxbase <= 55000.0D) {
				Quickdeduction = 4410.0D;
			} else if (taxbase <= 80000.0D) {
				Quickdeduction = 7160.0D;
			} else {
				Quickdeduction = 15160.0D;
			}
		} else {
			if (taxbase <= 0.0D) {
				return 0.0D;
			}
			if (taxbase <= 1500.0D) {
				Quickdeduction = 0.0D;
			} else if (taxbase <= 4500.0D) {
				Quickdeduction = 105.0D;
			} else if (taxbase <= 9000.0D) {
				Quickdeduction = 555.0D;
			} else if (taxbase <= 35000.0D) {
				Quickdeduction = 1005.0D;
			} else if (taxbase <= 55000.0D) {
				Quickdeduction = 2755.0D;
			} else if (taxbase <= 80000.0D) {
				Quickdeduction = 5505.0D;
			} else {
				Quickdeduction = 13505.0D;
			}
		}

		return Quickdeduction;
	}

	@Override
	public String getAreaName(CorpTaxVo corptaxvo) {
		if (corptaxvo.getTax_area() == null) {
			return "";
		} else {
			YntArea vo = (YntArea) singleObjectBO.queryByPrimaryKey(YntArea.class,
					Integer.toString(corptaxvo.getTax_area()));
			if (vo == null || StringUtil.isEmpty(vo.getRegion_name())) {
				return "";
			} else {
				return vo.getRegion_name();
			}
		}
	}

	public SalaryReportVO[] impExcel(String filepath, Sheet sheets1, String qj, String billtype, CorpTaxVo corpvo)
			throws BusinessException {
		List<SalaryReportVO> clist = null;
		SalaryReportVO[] vos = null;
		try {

			clist = new ArrayList<SalaryReportVO>();
			Cell aCell = null;
			String sTmp = "";
			String nullcheck = "";
			SalaryReportVO excelvo = null;
			Map<Integer, String> STYLE_1 = getColumnMap(sheets1, billtype, corpvo);
			int iBegin = getImpStartrow();
			String imptype = STYLE_1.get(100000);
			Set<String> set = new HashSet<>();
			Collection list = STYLE_1.values();

			// if(sheets1.getLastRowNum()>1000){
			// throw new BusinessException("最多可导入1000行");
			// }
			for (; iBegin < (sheets1.getLastRowNum() + 1); iBegin++) {
				Row row = sheets1.getRow(iBegin);
				if (row == null || isRowEmpty(row)) {
					continue;
				}
				nullcheck = "";

				excelvo = new SalaryReportVO();
				for (Integer key : STYLE_1.keySet()) {
					String column = STYLE_1.get(key);
					aCell = row.getCell(key.intValue());
					if (aCell == null)
						continue;
					if (!list.contains("qj")) {
						excelvo.setAttributeValue("qj", qj);
					}

					if (!StringUtil.isEmpty(imptype)) {
						excelvo.setImpmodeltype(Integer.parseInt(imptype));
					}
					if ("qj".equals(column)) {
						aCell = sheets1.getRow(iBegin).getCell(key.intValue());
						sTmp = getExcelCellValue(aCell, column);
						try {
							if (!StringUtil.isEmpty(sTmp)) {
								if (sTmp.length() > 10) {
									sTmp = sTmp.substring(0, 10);
								}
								sTmp = sTmp.replaceAll("[￥%$*—]", "");
							}
							if (!StringUtil.isEmpty(sTmp)) {
								DZFDate date = new DZFDate(sTmp);
								sTmp = date.toString();
							}
						} catch (Exception e) {
							throw new BusinessException("日期" + sTmp + "格式不正确，例如:" + new DZFDate().toString());
						}
						if (!StringUtil.isEmpty(sTmp)) {
							sTmp = sTmp.substring(0, 7);
						}
						if (!StringUtil.isEmpty(sTmp))
							set.add(sTmp);
					} else if ("zjlx".equals(column)) {
						aCell.setCellType(HSSFCell.CELL_TYPE_STRING);
						aCell = sheets1.getRow(iBegin).getCell(key.intValue());
						sTmp = (String) aCell.getStringCellValue();
					} else {
						aCell = sheets1.getRow(iBegin).getCell(key.intValue());
						sTmp = getExcelCellValue(aCell, column);
					}
					if (sTmp != null && !"".equals(sTmp)) {
						excelvo.setAttributeValue(column, sTmp.replaceAll(" ", ""));
						spercialTreat(excelvo, column);
					}
					if ("zjlx".equals(column) || "ygname".equals(column) || "zjbm".equals(column)) {
						if (!StringUtil.isEmpty(sTmp))
							nullcheck = nullcheck + sTmp;
					}

				}
				if (StringUtil.isEmpty(nullcheck))
					continue;
				if (excelvo != null) {
					if (StringUtil.isEmpty(excelvo.getQj()))
						throw new BusinessException(getAreaName(corpvo) + "地区所得期间起列数据不完整，请检查");
					clist.add(excelvo);
				}
			}
			if (list.contains("qj")) {
				if (set.size() != 1) {
					throw new BusinessException(getAreaName(corpvo) + "地区所得期间存在多个期间，请检查");
				}
			}

			if (clist.size() > 0) {
				vos = clist.toArray(new SalaryReportVO[clist.size()]);
			} else {
				throw new BusinessException(getAreaName(corpvo) + "地区导入工资数据为空，请检查");
			}
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(getAreaName(corpvo) + "地区导入文件失败");
		}
		return vos;
	}

	private boolean isRowEmpty(Row row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
				return false;
		}
		return true;
	}

	protected void spercialTreat(SalaryReportVO excelvo, String column) {
		if ("zjlx".equals(column)) {// 对应于 STYLE_1[1]
			String str = (String) excelvo.getAttributeValue(column);
			if (!StringUtil.isEmptyWithTrim(str)) {
				String repstr = null;
				if (SalaryReportEnum.IDCARD.getName().equals(str)) {
					repstr = SalaryReportEnum.IDCARD.getValue();
				} else if (SalaryReportEnum.CHINACARD.getName().equals(str)) {
					repstr = SalaryReportEnum.CHINACARD.getValue();
				}
				if (!StringUtil.isEmptyWithTrim(repstr)) {
					excelvo.setAttributeValue(column, repstr);
				}
			}
		}
	}

	protected String getExcelCellValue(Cell cell, String columnvalue) {
		String ret = "";
		try {
			Field field = SalaryReportVO.class.getDeclaredField(columnvalue);
			if (cell == null) {
				ret = null;
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {// 格式化日期字符串
				ret = cell.getRichStringCellValue().getString();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				if (field.getType().toString().endsWith("String")) {
					if (!"qj".equals(columnvalue)) {
						ret = new DecimalFormat("#").format(cell.getNumericCellValue()).toString();
					} else {
						try {
							ret = new DZFDate(cell.getDateCellValue()).toString();
						} catch (Exception ex) {
							log.error("错误",ex);
							ret = new DecimalFormat("#").format(cell.getNumericCellValue()).toString();
						}
					}
				} else if (field.getType().toString().endsWith("class com.dzf.pub.lang.DZFDate")) {
					ret = new DZFDate(cell.getDateCellValue()).toString();
				} else {
					ret = "" + Double.valueOf(cell.getNumericCellValue()).doubleValue();
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
				String value1 = null;
				try {
					DecimalFormat formatter = new DecimalFormat("#############.##");
					value1 = formatter.format(cell.getNumericCellValue());
					ret = value1;
				}
				catch (Exception e)
				{}
				if (StringUtil.isEmpty(value1) || "0.00".equals(ret))
				{
					try {
						FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();  
					    CellValue cellValue = evaluator.evaluate(cell);
					    ret = String.valueOf(cellValue.getNumberValue());
					}
					catch (Exception e)
					{}
				}
			}else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
				ret = "" + cell.getErrorCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				ret = "" + cell.getBooleanCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
				ret = null;
			}

		} catch (Exception ex) {
			log.error("错误",ex);
			ret = null;
		}
		return ret;
	}

	protected Map<String, String> getJsImportMap(String billtype) {
		String[] CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "qj", "yfgz", "yanglaobx", "yiliaobx",
				"shiyebx", "zfgjj" };
		String[] SDJSIMP = new String[] { "工号", "证照类型", "证照号码", "姓名", "所得期间起", "收入额", "基本养老保险费", "基本医疗保险费", "失业保险费",
				"住房公积金" };

		if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			SDJSIMP = new String[] { "工号", "证照类型", "证照号码", "姓名", "所得期间起", "收入额" };
			CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "qj", "yfgz" };
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			SDJSIMP = new String[] { "工号", "证照类型", "证照号码", "姓名", "所得期间起", "收入额" };
			CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "qj", "yfgz" };
		}

		Map<String, String> map = getMapColumn(null, SDJSIMP, CODESIMP);
		return map;
	}

	protected Map<String, String> getJsAllTypeImportMap(String billtype) {
		String[] CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "vproject", "ygname", "qj", "yfgz", "yanglaobx",
				"yiliaobx", "shiyebx", "zfgjj", "jcfy" };
		String[] SDJSIMP = new String[] { "工号", "证照类型", "证照号码", "所得项目", "姓名", "所得期间起", "收入额", "基本养老保险费", "基本医疗保险费",
				"失业保险费", "住房公积金" };
		Map<String, String> map = getMapColumn(null, SDJSIMP, CODESIMP);
		return map;
	}

	protected Map<String, String> getJsExportMap(String billtype) {
		String[] CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz", "yanglaobx", "yiliaobx",
				"shiyebx", "zfgjj" };

		String[] SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "*收入额", "基本养老保险费", "基本医疗保险费", "失业保险费",
				"住房公积金" };

		if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "境内所得境内支付" };
			CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz" };
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			SDJSEXP = new String[] { "工号", "*证照类型", "*证照号码", "姓名", "*收入额" };
			CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz" };
		}

		Map<String, String> map = getMapColumn(null, SDJSEXP, CODESJSEXP);
		return map;
	}

	protected Map<String, String> getDzfImportMap(String billtype) {
		String[] TYDZFIMP = new String[] { "工号", "手机号", "证照类型", "证照号码", "姓名", "部门名称", "费用科目", "应发工资", "养老保险",
				"医疗保险", "失业保险", "住房公积金" };

		String[] CODESTYIMP = new String[] { "ygbm", "vphone", "zjlx", "zjbm", "ygname", "cdeptid", "fykmid", "yfgz",
				"yanglaobx", "yiliaobx", "shiyebx", "zfgjj" };
		if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			TYDZFIMP = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "国籍", "来华时间", "适用公式", "部门名称", "费用科目",
					"应发工资" };
			CODESTYIMP = new String[] { "ygbm", "vphone", "zjlx", "zjbm", "ygname", "varea", "lhdate", "lhtype",
					"cdeptid", "fykmid", "yfgz" };
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			TYDZFIMP = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "部门名称", "费用科目", "应发工资" };
			CODESTYIMP = new String[] { "ygbm", "vphone", "zjlx", "zjbm", "ygname", "cdeptid", "fykmid", "yfgz" };
		}

		Map<String, String> map = getMapColumn(null, TYDZFIMP, CODESTYIMP);

		return map;

	}

	protected Map<String, String> getMapColumn(List<Integer> hiddenColList, String[] names, String[] codes) {

		int len = names.length;
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < len; i++) {
			if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
				continue;
			}
			if (!map.containsKey(names[i])) {
				map.put(names[i], codes[i]);
			}
		}
		return map;
	}

	protected Map<Integer, String> getColumnMap(Sheet sheets1, String billtype, CorpTaxVo corpvo) {

		int iBegin = getTitleColumnRow();
		Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, iBegin);
		if (map1 == null || map1.size() == 0) {
			throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
		}

		Map<String, String> jsimp = getJsAllTypeImportMap(billtype);
		Map<Integer, String> map = getColumnMap(map1, jsimp);

		if (map.size() == jsimp.size())
			return map;

		jsimp = getJsImportMap(billtype);
		map = getColumnMap(map1, jsimp);

		if (map.size() == jsimp.size())
			return map;

		jsimp = getJsExportMap(billtype);
		map = getColumnMap(map1, jsimp);

		if (map.size() == jsimp.size())
			return map;
		boolean isMatch = false;
		jsimp = getDzfImportMap(billtype);
		map = getColumnMap(map1, jsimp);
		// 记录大账房自己导入类型
		if (map.size() == jsimp.size()) {
			isMatch = true;
			map.put(100000, "1");
		}

		if (map == null || map.size() == 0) {
			throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
		}

		if (!isMatch) {
			throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
		}
		return map;
	}

	protected Map<Integer, String> getColumnMap(Map<String, Integer> map1, Map<String, String> jsimp) {

		Map<Integer, String> map = new HashMap<>();

		for (Map.Entry<String, Integer> entry : map1.entrySet()) {
			String key = entry.getKey().toString();
			if (!StringUtil.isEmpty(jsimp.get(key))) {
				Integer value = entry.getValue();
				map.put(value, jsimp.get(key));
			}
		}

		return map;

	}

	protected Map<String, Integer> getCommonGzTableHeadTiTle(Sheet sheets1, int iBegin) {

		int count = 0;
		if (sheets1.getRow(iBegin) == null) {
			count = 45;
		} else {
			count = sheets1.getRow(iBegin).getLastCellNum();
		}

		Map<String, Integer> map = getTableHeadTiTle(sheets1, iBegin, count);
		return map;

	}

	protected Map<String, Integer> getTableHeadTiTle(Sheet sheets1, int iBegin, int count) {

		Map<String, Integer> map = new HashMap<>();
		Cell cell = null;
		for (int i = 0; i < count; i++) {
			if (sheets1.getRow(iBegin) != null) {
				cell = sheets1.getRow(iBegin).getCell(i);
				if (cell != null) {
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						String ret = cell.getRichStringCellValue().getString();
						if (!StringUtil.isEmpty(ret))
							map.put(ret, i);

					}
				}
			}
		}
		return map;
	}

	@Override
	public int getTitleColumnRow() {
		return 0;
	}

	@Override
	public byte[] expPerson(JSONArray array, OutputStream out, String billtype) throws Exception {

		String fileName = expPersonFileName();

		ByteArrayOutputStream bos = null;
		InputStream is = null;
		try {
			HSSFWorkbook workbook = null;

			Resource exportTemplate = new ClassPathResource("template/report/taxdeclaration/" + fileName + ".xls");
			is = exportTemplate.getInputStream();
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow row = sheet.getRow(0);
			int minindex = row.getFirstCellNum(); // 列起
			int maxindex = row.getLastCellNum(); // 列止

			HSSFRow rowTo = null;
			Map<String, Object> map = null;
			HSSFCell c1 = null;
			HSSFCell c2 = null;
			HSSFRichTextString richString = null;
			String key = null;
			DZFDouble doublevalue = null;
			int len = array == null ? 0 : array.size();
			Map<Integer, String> fieldColumn = getExpPersonColumnMap(sheet, billtype);
			// 数据行
			for (int i = 0; i < len; i++) {
				rowTo = sheet.createRow(1 + i);
				rowTo.setHeight(row.getHeight());
				for (int colindex = minindex; colindex < maxindex; colindex++) {
					c1 = row.getCell(colindex);
					c2 = rowTo.createCell(colindex);
					c2.setCellStyle(c1.getCellStyle());
					map = (Map<String, Object>) array.get(i);
					if (fieldColumn.containsKey(colindex)) {
						key = fieldColumn.get(colindex);
						if (map.get(key) != null) {
							if (key.equals("zjlx")) {
								String name = SalaryReportEnum.getTypeEnumByValue(map.get(key).toString()).getName();
								richString = new HSSFRichTextString(name);
								c2.setCellValue(richString);
							} else {
								richString = new HSSFRichTextString(map.get(key).toString());
								c2.setCellValue(richString);
							}
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

	protected String expPersonFileName() {
		String fileName = "personal_information";
		return fileName;
	}

	protected Map<Integer, String> getExpPersonColumnMap(Sheet sheets1, String billtype) {

		int iBegin = getTitleColumnRow();
		Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, iBegin);

		String[] CODESJSEXP = new String[] { "ygbm", "ygname", "zjlx", "zjbm", "varea", "ryzt", "sfyg", "sfyg", "sfyg",
				"sfgy", "vphone", "lhdate", "vdef1","vdef2" };

		String[] SDJSEXP = new String[] { "工号", "*姓名", "*证照类型", "*证照号码", "*国籍(地区)", "*人员状态", "是否残疾", "是否烈属", "是否孤老",
				"*是否雇员", "*手机号码", "来华时间", "任职受雇日期", "离职日期" };

		Map<String, String> jsimp = getMapColumn(null, SDJSEXP, CODESJSEXP);
		Map<Integer, String> map = getColumnMap(map1, jsimp);

		return map;
	}

	@Override
	public int getImpStartrow() {
		return 1;
	}

	@Override
	public int getImpCellrow() {
		return 0;
	}

}
