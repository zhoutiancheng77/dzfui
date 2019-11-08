package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;

import java.util.HashMap;
import java.util.Map;

/**
 * 财报年报初始化
 * 
 * @author lbj
 *
 */
public class FinancialOrdinaryInit implements InitParse {
	private static String[] fields = new String[] { "39801004vo" };

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		String index = keys[1];
		if ("39801004vo".equals(vo)) {
			return getNameMapByLineNum(index);
		}
		return null;

	}

	private Map<String, String> getNameMapByLineNum(String index) {
		Map<String, String> nameMap = new HashMap<String, String>();
		// 上年实收资本或股本
		nameMap.put("snsszbhgb-" + index, "snsszbhgb");
		// 上年资本公积
		nameMap.put("snzbgj-" + index, "snzbgj");
		// 上年减库存股
		nameMap.put("snjkcg-" + index, "snjkcg");
		// 上年其他综合收益
		nameMap.put("snqtzhsy-" + index, "snqtzhsy");
		// 上年盈余公积
		nameMap.put("snyygj-" + index, "snyygj");
		// 上年未分配利润
		nameMap.put("snwfply-" + index, "snwfply");
		// 上年所有者权益合计
		nameMap.put("snsyzqyhj-" + index, "snsyzqyhj");
		return nameMap;
	}

	@Override
	public String getIndexField(String vname) {
		return "ewbhxh";
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
