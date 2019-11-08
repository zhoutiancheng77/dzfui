package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import java.util.HashMap;
import java.util.Map;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;

public class VATOrdinaryInit implements InitParse {

	private static String[] fields = new String[] { "sbZzsybnsrqtxxVO", "sbZzsybnsrsynqcsxxGrid", "sbZzsybnsrqcsxxGrid",
			"sbZzsybnsrfbsanqcsxxGrid", "sbZzsybnsrfbsiqcsxxGrid",
			"sbZzsybnsrfbwuqcsxxGrid", "sbZzsybnsrfbgdzcqcsxxGrid" , "jmxzList"};

	// 主表
	private static Map<String, Map<String, String>> masterNameMap;

	// 主表本期
	private static Map<String, String> masterNameMap1;
	private static Map<String, Map<String, String>> masterNameMap2;

	// 增值税纳税申报表附列资料（三）
	private static Map<String, Map<String, String>> attach3NameMap;
	// 增值税纳税申报表附列资料（四）
	private static Map<String, Map<String, String>> attach4NameMap;
	// 增值税纳税申报表附列资料（五）
	private static Map<String, Map<String, String>> attach5NameMap;
	// 固定资产（不含不动产）进项税额抵扣情况表初始化取数
	private static Map<String, String> FixedAssetsDeductNameMap;

	static {
		masterNameMap1 = new HashMap<String, String>();
		// 上期留抵税额一般货物及劳务和应税服务本月
		masterNameMap1.put("sqldseybby", "sqldsebys");
		// 上期留抵税额即征即退货物及劳务和应税服务本月
		masterNameMap1.put("sqldsejzby", "sqldsejzjtbys");
		// 免、抵、退应退税额本月
		masterNameMap1.put("mdtytseybby", "mdtytse");
		// 上期留抵税额本年累计
		masterNameMap1.put("sqldseybbnlj", "sqldsebnlj");
		
		// 期末留抵税额一般本月
		masterNameMap1.put("qmldseybby", "sqldsebys");
		// 期末留抵税额一般本年累计
		masterNameMap1.put("qmldseybbnlj", "sqldsebnlj");
		// 期末留抵税额即征本月
		masterNameMap1.put("qmldsejzby", "sqldsejzjtbys");
		
		masterNameMap = new HashMap<String, Map<String, String>>();
		// 一般项目
		HashMap<String, String> generalNameMap = new HashMap<String, String>();
		masterNameMap.put("2", generalNameMap);
		// 即征即退项目
		HashMap<String, String> immediateNameMap = new HashMap<String, String>();
		masterNameMap.put("4", immediateNameMap);
		
		HashMap<String, String> master1 = new HashMap<String, String>();
		// 一般货物及劳务和应税服务本月数-期初未缴税额
		master1.put("qcmjseybby", "qmwjse");
		master1.put("qcmjcbseybby", "qmwjcbse");
		masterNameMap.put("1", master1);
		
		HashMap<String, String> master3 = new HashMap<String, String>();
		// 即征即退货物及劳务和应税服务本月数-期初未缴税额
		master3.put("qcmjsejzby", "qmwjse");
		masterNameMap.put("3", master3);
		
		masterNameMap2 = new HashMap<String, Map<String,String>>();
		HashMap<String, String> masterNameMap2_2 = new HashMap<String, String>();
		masterNameMap2_2.put("qcmjseybbnlj", "qmwjse");
		masterNameMap2.put("2", masterNameMap2_2);
		
		HashMap<String, String> masterNameMap2_4 = new HashMap<String, String>();
		masterNameMap2_4.put("qcmjsejzbnlj", "qmwjse");
		masterNameMap2.put("4", masterNameMap2_4);
		
		// 按适用税率计税销售额
		generalNameMap.put("asysljsxseybbnlj", "asysljsxse");
		// 应税货物销售额
		generalNameMap.put("yshwxseybbnlj", "yshwxse");
		// 应税劳务销售额
		generalNameMap.put("yslwxseybbnlj", "yslwxse");
		// 纳税检查调整的销售额_适用税率
		generalNameMap.put("nsjcdzxseybbnlj", "syslNsjctzxse");
		// 按简易办法计税销售额
		generalNameMap.put("ajyfsjsxseybbnlj", "ajybfjsxse");
		// 纳税检查调整的销售额_简易办法
		generalNameMap.put("nsjctzxseybbnlj", "jybfNsjctzxse");
		// 免抵退办法出口销售额
		generalNameMap.put("mdtbfckxseybbnlj", "mdtbfckxse");
		// 免税销售额
		generalNameMap.put("msxseybbnlj", "msxse");
		// 免税货物销售额
		generalNameMap.put("mshwxseybbnlj", "mshwxse");
		// 免税劳务销售额
		generalNameMap.put("mslwxseybbnlj", "mslwxse");
		// 销项税额
		generalNameMap.put("xxseybbnlj", "xxse");
		// 进项税额
		generalNameMap.put("jxseybbnlj", "jxse");
		// 上期留抵税额
//		generalNameMap.put("sqldseybbnlj", "sqldsebnlj");
		// 进项税额转出
		generalNameMap.put("jxsezcybbnlj", "jxsezc");
		// 免、抵、退应退税额
		generalNameMap.put("mdtytseybbnlj", "mdtytse");
		// 按适用税率计算的纳税检查应补缴税额
		generalNameMap.put("asysljsnsjcybjseybbnlj", "syslNsjcybjse");
		// 实际抵扣税额
		generalNameMap.put("sjdkseybbnlj", "sjdkse");
		// 应纳税额
		generalNameMap.put("ynseybbnlj", "ynse");
		// 期末留抵税额
		generalNameMap.put("qmldseybbnlj", "qmldse");
		// 简易计税办法计算的应纳税额
		generalNameMap.put("jyjsynseybbnlj", "jybfYnse");
		// 按简易计税办法计算的纳税检查应补缴税额
		generalNameMap.put("ajynsjcybjseybbnlj", "jybfNsjcybjse");
		// 应纳税额减征额
		generalNameMap.put("ynsejzeybbnlj", "ynsejze");
		// 应纳税额合计
		generalNameMap.put("ynsehjybbnlj", "ynsehj");
		// 期初未缴税额
//		generalNameMap.put("qcmjseybbnlj", "qmwjse");
		// 实收出口开具专用缴款书退税额
		generalNameMap.put("ssckkjzyjkstkeybbnlj", "ssckkjzyjkstse");
		// 本期预缴税额
		generalNameMap.put("bqyjseybbnlj", "bqyjse");
		// 本期缴纳上期应纳税额
		generalNameMap.put("jqjnsqynseybbnlj", "bqjnsqynse");
		// 本期缴纳欠缴税额
		generalNameMap.put("bqjnqjseybbnlj", "bqjnqjse");
		// 期末未缴税额（多缴为负数）
		generalNameMap.put("qmwjseybbnlj", "qmwjse");
		// 期初未缴查补税额
		generalNameMap.put("qcmjcbseybbnlj", "qcwjcbse");
		// 本期入库查补税额
		generalNameMap.put("bqrkcbseybbnlj", "bqrkcbse");
		// 期末未缴款补税额
		generalNameMap.put("qmmjcbseybbnlj", "qmwjcbse");

		// 按适用税率计税销售额
		immediateNameMap.put("asysljsxsejzbnlj", "asysljsxse");
		// 应税货物销售额
		immediateNameMap.put("yshwxsejzbnlj", "yshwxse");
		// 应税劳务销售额
		immediateNameMap.put("yslwxsejzbnlj", "yslwxse");
		// 纳税检查调整的销售额_适用税率
		immediateNameMap.put("nsjcdzxsejzbnlj", "syslNsjctzxse");
		// 按简易办法计税销售额
		immediateNameMap.put("ajyfsjsxsejzbnlj", "ajybfjsxse");
		// 纳税检查调整的销售额_简易办法
		immediateNameMap.put("nsjctzxsejzbnlj", "jybfNsjctzxse");
		// 销项税额
		immediateNameMap.put("xxsejzbnlj", "xxse");
		// 进项税额
		immediateNameMap.put("jxsejzbnlj", "jxse");
		// 进项税额转出
		immediateNameMap.put("jxsezcjzbnlj", "jxsezc");
		// 实际抵扣税额
		immediateNameMap.put("sjdksejzbnlj", "sjdkse");
		// 应纳税额
		immediateNameMap.put("ynsejzbnlj", "ynse");
		// 简易计税办法计算的应纳税额
		immediateNameMap.put("jyjsynsejzbnlj", "jybfYnse");
		// 应纳税额减征额
		immediateNameMap.put("ynsejzejzbnlj", "ynsejze");
		// 应纳税额合计
		immediateNameMap.put("ynsehjjzbnlj", "ynsehj");
		// 期初未缴税额
//		immediateNameMap.put("qcmjsejzbnlj", "qmwjse");
		// 本期预缴税额
		immediateNameMap.put("bqyjsejzbnlj", "bqyjse");
		// 本期缴纳上期应纳税额
		immediateNameMap.put("jqjnsqynsejzbnlj", "bqjnsqynse");
		// 本期缴纳欠缴税额
		immediateNameMap.put("bqjnqjsejzbnlj", "bqjnqjse");
		// 期末未缴税额（多缴为负数）
		immediateNameMap.put("qmwjsejzbnlj", "qmwjse");
		// 即征即退实际退税额
		immediateNameMap.put("jzjtsjtsejzbnlj", "jzjtsjtse");

		attach3NameMap = new HashMap<String, Map<String, String>>();
		// 17%税率的项目-期初余额
		Map<String, String> attach3NameMap1 = new HashMap<String, String>();
		attach3NameMap1.put("yxdcznfwqcye", "qmye");
		attach3NameMap.put("1", attach3NameMap1);
		// 11%税率的项目
		Map<String, String> attach3NameMap2 = new HashMap<String, String>();
		attach3NameMap2.put("eslysfwqcye", "qmye");
		attach3NameMap.put("2", attach3NameMap2);
		// 6%税率的项目（不含金融商品转让）
		Map<String, String> attach3NameMap3 = new HashMap<String, String>();
		attach3NameMap3.put("sslysfwqcye", "qmye");
		attach3NameMap.put("3", attach3NameMap3);
		// 6%税率的金融商品转让项目
		Map<String, String> attach3NameMap4 = new HashMap<String, String>();
		attach3NameMap4.put("sslysfwqcyenew", "qmye");
		attach3NameMap.put("4", attach3NameMap4);
		// 5%征收率的项目
		Map<String, String> attach3NameMap5 = new HashMap<String, String>();
		attach3NameMap5.put("sslysfw5qcyenew", "qmye");
		attach3NameMap.put("5", attach3NameMap5);
		// 3%征收率的项目
		Map<String, String> attach3NameMap6 = new HashMap<String, String>();
		attach3NameMap6.put("tslysfwqcye", "qmye");
		attach3NameMap.put("6", attach3NameMap6);
		// 免抵退税的项目
		Map<String, String> attach3NameMap7 = new HashMap<String, String>();
		attach3NameMap7.put("mdtsdysfwqcye", "qmye");
		attach3NameMap.put("7", attach3NameMap7);
		// 免税的项目
		Map<String, String> attach3NameMap8 = new HashMap<String, String>();
		attach3NameMap8.put("msdysfwqcye", "qmye");
		attach3NameMap.put("8", attach3NameMap8);

		attach4NameMap = new HashMap<String, Map<String, String>>();
		// 增值税税控系统专用设备费及技术维护费
		Map<String, String> attach4NameMap1 = new HashMap<String, String>();
		attach4NameMap1.put("zzsskxtzysbfjjswhfqcye", "qmye");
		attach4NameMap.put("1", attach4NameMap1);
		// 分支机构预征缴纳税款
		Map<String, String> attach4NameMap2 = new HashMap<String, String>();
		attach4NameMap2.put("fzjgyzsjnskqcye", "qmye");
		attach4NameMap2.put("fzjgyzsjnskbqfse", "bqfse");
		attach4NameMap.put("2", attach4NameMap2);
		// 建筑服务预征缴纳税款
		Map<String, String> attach4NameMap3 = new HashMap<String, String>();
		attach4NameMap3.put("jzfwyzjnskqcye", "qmye");
		attach4NameMap.put("3", attach4NameMap3);
		// 销售不动产预征缴纳税款
		Map<String, String> attach4NameMap4 = new HashMap<String, String>();
		attach4NameMap4.put("xsbdcyzjnskqcye", "qmye");
		attach4NameMap.put("4", attach4NameMap4);
		// 出租不动产预征缴纳税款
		Map<String, String> attach4NameMap5 = new HashMap<String, String>();
		attach4NameMap5.put("czbdcyzjnskqcye", "qmye");
		attach4NameMap.put("5", attach4NameMap5);

		attach5NameMap = new HashMap<String, Map<String, String>>();
		// 增值税税控系统专用设备费及技术维护费
		Map<String, String> attach5NameMap1 = new HashMap<String, String>();
		attach5NameMap1.put("qcddkbdcjxse", "qmye");
		attach5NameMap.put("1", attach5NameMap1);

		FixedAssetsDeductNameMap = new HashMap<String, String>();
		FixedAssetsDeductNameMap.put("zzszyfpsbdkdgdzcjxselj", "zzszyfpJxselj");
		FixedAssetsDeductNameMap.put("hgjkzzszyjkssbdkdgdzcjxselj",
				"hgjkzzszyjksJxselj");
	}

	@Override
	public Map<String, String> getNameMap(String key) {
		String[] keys = key.split("--");
		String vo = keys[0];
		String index = keys[1];
		if ("sbZzsybnsrqtxxVO".equals(vo)) {
			return masterNameMap1;
		} else if ("sbZzsybnsrsynqcsxxGrid".equals(vo)) {
			return masterNameMap2.get(index);
		}else if ("sbZzsybnsrqcsxxGrid".equals(vo)) {
			return masterNameMap.get(index);
		} else if ("sbZzsybnsrfbsanqcsxxGrid".equals(vo)) {
			return attach3NameMap.get(index);
		} else if ("sbZzsybnsrfbsiqcsxxGrid".equals(vo)) {
			return attach4NameMap.get(index);
		} else if ("sbZzsybnsrfbwuqcsxxGrid".equals(vo)) {
			return attach5NameMap.get(index);
		} else if ("sbZzsybnsrfbgdzcqcsxxGrid".equals(vo)) {
			return FixedAssetsDeductNameMap;
		}
		return null;
	}

	@Override
	public String getIndexField(String vname) {
		return "sbZzsybnsrqcsxxGrid".equals(vname) || "sbZzsybnsrsynqcsxxGrid".equals(vname) ? "ewblxh" : "ewbhxh";
	}

	@Override
	public String[] getFields() {
		return fields;
	}

	@Override
	public Class<?> getFieldType(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

}
