package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;

import java.util.HashMap;
import java.util.Map;

public class EnterpriseIncomeTaxBInitMapping implements InitFiledMapParse {

	private static String[] fields = new String[] { "sbQysdshdzsyjdndsbqtxxVO" };
	private static Map<String, Map<String, String>> masterNameMap = new HashMap<String, Map<String, String>>();
	// 主表
	static {

		// 税务机关核定应纳所得税额
		Map<String, String> mainNameMap1 = new HashMap<String, String>();
		masterNameMap.put("402", mainNameMap1);
		mainNameMap1.put("h21_hdsdje", "nhdsdse");

		// 税务机关核定应纳所得税额
		Map<String, String> mainNameMap2 = new HashMap<String, String>();
		masterNameMap.put("403", mainNameMap2);
		mainNameMap2.put("h10_yssdl", "yssdl");
		mainNameMap2.put("h19_ljje_sqyjje", "sqyjje");
		mainNameMap2.put("h19_ljje_kyyjye", "kyyjye");
		mainNameMap2.put("h19_ljje_yyyjje", "yyyjje");

		// 按成本费用核定应纳税所得额
		Map<String, String> mainNameMap3 = new HashMap<String, String>();
		masterNameMap.put("404", mainNameMap3);
		mainNameMap3.put("h13_hdsdl", "yssdl");
	}

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		String index = keys[1];
		if ("sbQysdshdzsyjdndsbqtxxVO".equals(vo)) {
			return masterNameMap.get(index);
		}
		return null;
	}

	@Override
	public String getIndexField(String vname) {
		return "zsfsDm";
	}

	@Override
	public String[] getFields() {
		return fields;
	}

	@Override
	public Class<?> getFieldType(String fieldName) {
		return null;
	}
}
