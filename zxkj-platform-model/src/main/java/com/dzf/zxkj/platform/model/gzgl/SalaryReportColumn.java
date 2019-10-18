package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.common.utils.StringUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SalaryReportColumn {

	public static String[] CODES = new String[] { "ygbm", "vphone", "zjlx", "zjbm", "ygname", "varea", "lhdate",
			"lhtype", "vdeptname", "fykmname", "yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj", "ynssde", "shuilv",
			"grsds", "sfgz" };
	public static String[] NORMALNAMES = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "国籍", "来华时间", "适用公式",
			"部门名称", "费用科目", "应发工资", "养老保险", "医疗保险", "失业保险", "住房公积金", "应纳税所得额", "税率%", "个人所得税", "实发工资" };
	public static String[] FOREIGNNAMES = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "国籍", "来华时间", "适用公式",
			"部门名称", "费用科目", "应发工资", "养老保险", "医疗保险", "失业保险", "住房公积金", "应纳税所得额", "税率%", "应扣缴税额", "实发工资" };
	public static String[] ANNUALNAMES = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "国籍", "来华时间", "适用公式",
			"部门名称", "费用科目", "应发奖金", "养老保险", "医疗保险", "失业保险", "住房公积金", "应纳税所得额", "税率%", "应扣缴税额", "实发奖金" };
	public static int[] WIDTHS = new int[] { 3, 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };

	//  正常薪金
	public static int[] ZCHIDEN = new int[] { 5, 6, 7 };
	
	// 劳务报酬
	// 员工编码：工号
	//  手机号：大账房默认
	// 证件类型：证照类型
	//  证件号码：证照号码
	//  员工姓名：姓名
	//  部门名称：大账房默认
	//  费用科目：大账房默认
	//  所得期间起、所得期间止：对应导入工资所属月份
	//  应发工资：收入额

	public static int[] LWHIDEN = new int[] { 5, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18 };
	// 年终奖
	// 员工编码：工号
	//  手机号：大账房默认
	//  证件类型：证照类型
	//  证件号码：证照号码
	// 员工姓名：姓名
	//  部门名称：大账房默认
	//  费用科目：大账房默认
	// 应发奖金：全年一次性奖金额
	//  税率：税率
	//  应扣缴税额：应扣缴税额
	//  实发奖金：应纳税所得额-应扣缴税额
	public static int[] NZJHIDEN = new int[] { 5, 6, 7, 11, 12, 13, 14, 15 };
	// 外籍人员的工资发放
	//  员工编码：工号
	//  证件类型：证照类型
	//  证件号码：证照号码
	//  手机号：大账房默认
	//  应发工资：收入额
	//  应纳税所得额：应纳税所得额
	//  税率：税率
	//  应扣缴税额：应扣缴税额（=应纳税所得额*税率）
	public static int[] WJGZHIDEN = new int[] { 11, 12, 13, 14, 18 };

	public static Map<String, String> getMapColumn(List<Integer> hiddenColList, String type) {

		int len = CODES.length;
		String[] names = getNames(null, type);
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < len; i++) {
			if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
				continue;
			}
			if (!map.containsKey(CODES[i])) {
				map.put(CODES[i], names[i]);
			}
		}
		return map;
	}

	public static String[] getCodes(List<Integer> hiddenColList) {
		int len = CODES.length;
		List<String> list = new ArrayList();
		for (int i = 0; i < len; i++) {
			if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
				continue;
			}
			list.add(CODES[i]);
		}
		return list.toArray(new String[list.size()]);
	}

	public static String[] getNames(List<Integer> hiddenColList, String type) {
		String[] names = null;
		if (StringUtil.isEmpty(type)) {
			names = NORMALNAMES;
		} else {
			if (type.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
				names = FOREIGNNAMES;
			} else if (type.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
				names = ANNUALNAMES;
			} else {
				names = NORMALNAMES;
			}
		}
		int len = names.length;
		List<String> list = new ArrayList();
		for (int i = 0; i < len; i++) {
			if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
				continue;
			}
			list.add(names[i]);
		}
		return list.toArray(new String[list.size()]);
	}

	public static int[] getWidths(List<Integer> hiddenColList) {
		int len = WIDTHS.length;
		List<Integer> list = new ArrayList();
		for (int i = 0; i < len; i++) {
			if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
				continue;
			}
			list.add(WIDTHS[i]);
		}
		int[] d = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			d[i] = list.get(i);
		}
		return d;
	}

	public static String[] CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "qj", "yfgz", "yanglaobx",
			"yiliaobx", "shiyebx", "zfgjj" };
	public static String[] SDJSIMP = new String[] { "工号", "证照类型", "证照号码", "姓名", "所得期间起", "收入额", "基本养老保险费", "基本医疗保险费",
			"失业保险费", "住房公积金" };

	public static String[] CQJSIMP = new String[] { "序号", "证照类型", "证照号码", "纳税人姓名", "所得期间起", "收入额", "基本养老保险费", "基本医疗保险费",
			"失业保险费", "住房公积金" };

	public static String[] CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz", "yanglaobx",
			"yiliaobx", "shiyebx", "zfgjj" };

	public static String[] TYDZFIMP = new String[] { "员工编码", "手机号", "证件类型", "证件编码", "员工姓名", "部门名称", "费用科目", "应发工资",
			"养老保险", "医疗保险", "失业保险", "住房公积金" };

	public static String[] CODESTYIMP = new String[] { "ygbm", "vphone", "zjlx", "zjbm", "ygname", "cdeptid", "fykmid",
			"yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj" };

}
