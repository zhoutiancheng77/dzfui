package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;

import java.util.HashMap;
import java.util.Map;

public class EnterpriseIncomeTaxBInitMappingNew implements InitFiledMapParse {

	private static String[] fields = new String[] { "sbQysdshdzsyjdndsbqtxxVO" };
	private static Map<String, String> masterNameMap = new HashMap<String, String>();
	// 主表
	static {

		// 核定征收方式
		masterNameMap.put("hdzsfs", "zsfsDm");
		// 税务机关核定的应税所得率（%）
		masterNameMap.put("h11_hdsdl", "yssdl");
		// 减：实际已缴纳所得税额
		masterNameMap.put("h16_sjyjnsdseje_sqyjje", "sqyjje");
		masterNameMap.put("h16_sjyjnsdseje_kyyjye", "kyyjye");
		masterNameMap.put("h16_sjyjnsdseje_yyyjje", "yyyjje");
	}

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		if ("sbQysdshdzsyjdndsbqtxxVO".equals(vo)) {
			return masterNameMap;
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
