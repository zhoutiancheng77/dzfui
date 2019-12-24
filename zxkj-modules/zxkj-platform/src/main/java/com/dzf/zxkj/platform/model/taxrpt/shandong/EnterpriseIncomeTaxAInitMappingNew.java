package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.platform.model.report.FzjgRptVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;

import java.util.HashMap;
import java.util.Map;

public class EnterpriseIncomeTaxAInitMappingNew implements InitFiledMapParse {

	private static String[] fields = new String[] { "sbQysdsczzsyjdsbqtxxVO", "fzjgxxGridlb" };

	private static Map<String, String> mainNameMap = new HashMap<String, String>();
	private static Map<String, String> fzjgxxMap = new HashMap<String, String>();

	// 主表
	static {
		// 预缴方式
		mainNameMap.put("yjfs", "yjfs");
		// 企业类型
		mainNameMap.put("qylx", "sbqylx");
		// 实际已预缴所得税额
		mainNameMap.put("sjyjnsesebnljje_sqyjje", "sqyjje");
		mainNameMap.put("sjyjnsesebnljje_kyyjye", "kyyjye");
		mainNameMap.put("sjyjnsesebnljje_yyyjje", "yyyjje");
		// 弥补以前年度亏损
		mainNameMap.put("mbyqndksbnljje", "mbksehj");
		// 总机构分摊比例
		mainNameMap.put("zjgftbl", "zjgFtbl");
		// 财政集中分配比例
		mainNameMap.put("zjgczjzftbl", "zjgCzjzFtbl");
		// 全部分支机构分摊比例
		mainNameMap.put("fzjgftbl", "fzjgFtbl");
		// 总机构具有主体生产经营职能部门分摊比例

		// 分支机构本期分摊比例

		// 总机构统一社会信用代码（纳税人识别号）
		fzjgxxMap.put("zjgnsrsbh", "zjgnsrsbh");
	}

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		if ("sbQysdsczzsyjdsbqtxxVO".equals(vo)) {
			return mainNameMap;
		} else if ("fzjgxxGridlb".equals(vo)) {
			return fzjgxxMap;
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
