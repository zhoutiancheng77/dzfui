package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

public class SalaryRelationColumn {

	// 导出人员信息字段对照
	public static	String[][] EXPPCOLUMNS_RELATION_PERSON = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "*姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "vdef4", "*性别" }, 
			{ "vdef3", "*出生日期" }, 
			{ "varea", "*国籍(地区)" }, 
			{ "ryzt", "*人员状态" },
			{ "sfyg", "是否残疾" }, 
			{ "sfyg", "是否烈属" }, 
			{ "sfyg", "是否孤老" }, 
			{ "sfgy", "*任职受雇从业类型" }, 
			{ "vphone", "手机号码" },
			{ "lhdate", "来华时间" }, 
			{ "vdef1", "任职受雇从业日期" }, 
			{ "vdef2", "离职日期" }, 
			{ "vdef5", "出生国家(地区)" },
			{ "vdef21", "首次入境时间" }, 
			{ "vdef22", "预计离境时间" } };
	
	// 大账房本身导入模板字段对照(正常薪金)
	public static	String[][] IMPPCOLUMNS_RELATION_DZFNORMAL = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "vphone", "手机号" }, 
			{ "zjlx", "证照类型" }, 
			{ "zjbm", "证照号码" },
			{ "cdeptid", "部门名称" }, 
			{ "fykmid", "费用科目" }, 
			{ "yfgz", "应发工资" },
			{ "yanglaobx", "养老保险" }, 
			{ "yiliaobx", "医疗保险" }, 
			{ "shiyebx", "失业保险" }, 
			{ "zfgjj", "住房公积金" }, 
			{ "ljzxkc", "累计专项扣除" },
			{ "znjyzc", "子女教育支出" }, 
			{ "jxjyzc", "继续教育支出" }, 
			{ "zfdkzc", "住房贷款利息支出" }, 
			{ "zfzjzc", "住房租金支出" },
			{ "sylrzc", "赡养老人" },
			{ "ljsre", "累计收入额" }, 
			{ "ljznjyzc", "累计子女教育支出" }, 
			{ "ljjxjyzc", "累计继续教育支出" }, 
			{ "ljzfdkzc", "累计住房贷款利息支出" }, 
			{ "ljzfzjzc", "累计住房租金支出" }, 
			{ "ljsylrzc", "累计赡养老人支出" }, 
			{ "ljjcfy", "累计减除费用" }, 
			{ "yyjse", "已预缴税额" }
	};
	
	// 大账房本身导入模板字段对照（外籍）
	public static	String[][] IMPPCOLUMNS_RELATION_DZFFOREIGN = { 
			{ "ygbm", "员工编码" }, 
			{ "ygname", "员工姓名" }, 
			{ "vphone", "手机号" }, 
			{ "zjlx", "证件类型" }, 
			{ "zjbm", "证件编码" },
			{ "varea", "国籍" },
			{ "lhdate", "来华时间" },
			{ "lhtype", "适用公式" },
			{ "cdeptid", "部门名称" }, 
			{ "fykmid", "费用科目" }, 
			{ "yfgz", "应发工资" },
	};
		
	// 大账房本身导入模板字段对照（劳务）
	public static	String[][] IMPPCOLUMNS_RELATION_DZFREMUNER = { 
			{ "ygbm", "员工编码" }, 
			{ "ygname", "员工姓名" }, 
			{ "vphone", "手机号" }, 
			{ "zjlx", "证件类型" }, 
			{ "zjbm", "证件编码" },
			{ "cdeptid", "部门名称" }, 
			{ "fykmid", "费用科目" }, 
			{ "yfgz", "应发工资" },
	};
	
	// 金三全字段导入模板字段对照
	public static	String[][] IMPPCOLUMNS_RELATION_JSALL = { 
			{ "ygname", "姓名" }, 
			{ "zjlx", "身份证件类型" }, 
			{ "zjbm", "身份证件号码" },
			{ "vproject", "所得项目" },
			{ "yfgz", "收入" },
			{ "yanglaobx", "基本养老保险费" }, 
			{ "yiliaobx", "基本医疗保险费" }, 
			{ "shiyebx", "失业保险费" }, 
			{ "zfgjj", "住房公积金" }, 
			{ "ljzxkc", "累计专项扣除" },
			{ "ljsre", "累计收入额" }, 
			{ "ljznjyzc", "子女教育" }, 
			{ "ljjxjyzc", "继续教育" }, 
			{ "ljzfdkzc", "住房贷款利息" }, 
			{ "ljzfzjzc", "住房租金" }, 
			{ "ljsylrzc", "赡养老人" }, 
			{ "ljjcfy", "累计减除费用" }, 
			{ "yyjse", "已扣缴税额" }
	};
	
	// 金三全字段导入模板字段对照
	public static	String[][] IMPPCOLUMNS_RELATION_JSALL1 = { 
			{ "ygname", "姓名" }, 
			{ "zjlx", "身份证件类型" }, 
			{ "zjbm", "身份证件号码" },
			{ "vproject", "所得项目" },
			{ "yfgz", "收入" },
			{ "yanglaobx", "基本养老保险费" }, 
			{ "yiliaobx", "基本医疗保险费" }, 
			{ "shiyebx", "失业保险费" }, 
			{ "zfgjj", "住房公积金" }, 
			{ "ljzxkc", "累计专项扣除" },
			{ "ljsre", "累计收入额" }, 
			{ "ljznjyzc", "子女教育" }, 
			{ "ljjxjyzc", "继续教育" }, 
			{ "ljzfdkzc", "住房贷款利息" }, 
			{ "ljzfzjzc", "住房租金" }, 
			{ "ljsylrzc", "赡养老人" }, 
			{ "ljjcfy", "累计减除费用" }, 
			{ "yyjse", "已缴税额" }
	};
	
	// 金三导入模板字段对照(正常薪金)
	public static	String[][] IMPPCOLUMNS_RELATION_JSNORMAL = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "证照类型" }, 
			{ "zjbm", "证照号码" },
			{ "qj", "税款所属期起" },
			{ "vproject", "所得项目" },
			{ "yfgz", "本期收入" },
			{ "yanglaobx", "本期基本养老保险费" }, 
			{ "yiliaobx", "本期基本医疗保险费" }, 
			{ "shiyebx", "本期失业保险费" }, 
			{ "zfgjj", "本期住房公积金" }, 
			{ "ljzxkc", "累计专项扣除" },
			{ "ljsre", "累计收入额" }, 
			{ "ljznjyzc", "累计子女教育支出扣除" }, 
			{ "ljjxjyzc", "累计继续教育支出扣除" }, 
			{ "ljzfdkzc", "累计住房贷款利息支出扣除" }, 
			{ "ljzfzjzc", "累计住房租金支出扣除" }, 
			{ "ljsylrzc", "累计赡养老人支出扣除" }, 
			{ "ljjcfy", "累计减除费用" }, 
			{ "yyjse", "累计已预缴税额" }
	};	
	
	// 金三导入模板字段对照(外籍)
	public static	String[][] IMPPCOLUMNS_RELATION_JSFOREIGN = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "证照类型" }, 
			{ "zjbm", "证照号码" },
			{ "qj", "所得期间起" },
			{ "yfgz", "收入额" },
	};	
		
	// 金三导入模板字段对照(劳务)
	public static	String[][] IMPPCOLUMNS_RELATION_JSREMUNER = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "证照类型" }, 
			{ "zjbm", "证照号码" },
			{ "qj", "所得期间起" },
			{ "yfgz", "收入额" },
	};
	
	// 大账房纳税申报导出模板字段对照(正常薪金)
	public static	String[][] EXPPCOLUMNS_RELATION_DZFNORMAL = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "yfgz", "*本期收入" },
			{ "yanglaobx", "基本养老保险费" }, 
			{ "yiliaobx", "基本医疗保险费" }, 
			{ "shiyebx", "失业保险费" }, 
			{ "zfgjj", "住房公积金" }, 
			{ "ljznjyzc", "累计子女教育" }, 
			{ "ljjxjyzc", "累计继续教育" }, 
			{ "ljzfdkzc", "累计住房贷款利息" }, 
			{ "ljzfzjzc", "累计住房租金" },
			{ "ljsylrzc", "累计赡养老人" },
			
	};
		
	// 大账房纳税申报导出模板字段对照（外籍）
	public static	String[][]  EXPPCOLUMNS_RELATION_DZFFOREIGN = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "lhtype", "适用公式" },
			{ "yfgz", "境内所得境内支付" }, 
			{ "yxjzbl", "允许列支的捐赠比例" }, 
	};
			
	// 大账房纳税申报导出模板字段对照（劳务）
	public static	String[][]  EXPPCOLUMNS_RELATION_DZFREMUNER = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "skfdfs", "税款负担方式" }, 
			{ "yxjzbl", "允许列支的捐赠比例" }, 
			{ "yfgz", "*收入额" },
	};
	
	
	// 金三导出模板字段对照(正常薪金)
	public static	String[][] EXPPCOLUMNS_RELATION_JSNORMAL = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "yfgz", "*收入额" },
			{ "yanglaobx", "基本养老保险费" }, 
			{ "yiliaobx", "基本医疗保险费" }, 
			{ "shiyebx", "失业保险费" }, 
			{ "zfgjj", "住房公积金" }, 
	};	
		
	// 金三导出模板字段对照(外籍)
	public static	String[][] EXPPCOLUMNS_RELATION_JSFOREIGN = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "yfgz", "境内所得境内支付" },
	};	
			
	// 金三导出模板字段对照(劳务)
	public static	String[][] EXPPCOLUMNS_RELATION_JSREMUNER = { 
			{ "ygbm", "工号" }, 
			{ "ygname", "姓名" }, 
			{ "zjlx", "*证照类型" }, 
			{ "zjbm", "*证照号码" },
			{ "yfgz", "*收入额" },
	};
		
		
}
