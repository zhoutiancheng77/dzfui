package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

import java.util.HashMap;
import java.util.Map;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;

public class IncomeTaxInit implements InitParse {
	private static String[] fields = new String[] { "sbQysdshdzsyjdndsbqtxxVO" };
	private static Map<String, String> masterNameMap = new HashMap<String, String>();
	// 主表
	static {
		masterNameMap.put("hdzsfs", "zsfsDm");
		masterNameMap.put("h11_hdsdl", "yssdl");
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
		// TODO Auto-generated method stub
		return null;
	}
}
