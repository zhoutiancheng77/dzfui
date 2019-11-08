package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.platform.model.report.FzjgRptVO;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;

import java.util.HashMap;
import java.util.Map;

public class IncomeTaxInit implements InitParse {
	private static String[] fields = new String[] { "sbQysdsczzsyjdsbqtxxVO",
			"fzjgxxGrid" };
	private static Map<String, String> mainNameMap = new HashMap<String, String>();
	private static Map<String, String> mainNameMap_1 = new HashMap<String, String>();

	private static Map<String, String> fzjgMap = new HashMap<String, String>();

	// 主表
	static {
		// 预缴方式
		mainNameMap_1.put("yjfs", "yjfs");
		// 企业类型
		mainNameMap_1.put("qylx", "sbqylx");
		// 总机构分摊比例
		mainNameMap_1.put("zjgftbl", "zjgFtbl");
		// 财政集中分配比例
		mainNameMap_1.put("zjgczjzftbl", "zjgCzjzFtbl");
		// 全部分支机构分摊比例
		mainNameMap_1.put("fzjgftbl", "fzjgFtbl");
		
		mainNameMap_1.put("mbyqndksbnljje", "mbksehj");
		// 上期已缴金额，可用预缴余额，已用预缴余额
		mainNameMap_1.put("sjyjnsesebnljje_sqyjje", "sqyjje");
		mainNameMap_1.put("sjyjnsesebnljje_kyyjye", "kyyjye");
		mainNameMap_1.put("sjyjnsesebnljje_yyyjje", "yyyjje");
		// 总分机构类别0：一般企业||1：按比例预缴的二级分支机构||2：只申报不缴纳的企业||3：非跨地区转移的汇总企业总机构||4：分支机构||5：跨省或者跨市或者跨县区的汇总企业总机构
		mainNameMap_1.put("zfjglb", "zfjglb");
		fzjgMap.put("sb10412005vo.data1.fzjgqk", null);

	}

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		if ("sbQysdsczzsyjdsbQcsVO".equals(vo)) {
			return mainNameMap;
		} else if ("sbQysdsczzsyjdsbqtxxVO".equals(vo)) {
			return mainNameMap_1;
		} else if ("fzjgxxGrid".equals(vo)) {
			return fzjgMap;
		}
		return null;

	}

	@Override
	public String getIndexField(String vname) {
		if ("fzjgxxGrid".equals(vname)) {
			return "rawType";
		}
		return "ewbhxh";
	}

	@Override
	public String[] getFields() {
		return fields;
	}

	@Override
	public Class<?> getFieldType(String fieldName) {
		if ("fzjgxxGrid".equals(fieldName)) {
			return FzjgRptVO.class;
		}
		return null;
	}
}
