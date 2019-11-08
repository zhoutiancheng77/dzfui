package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;

import java.util.HashMap;
import java.util.Map;

/**
 * 江苏增值税小规模初始化
 * 
 * @author lbj
 *
 */
public class VATSmallInit implements InitParse {
	private static String[] fields = new String[] { "zzsxgmnsrqcsxxGrid",
			"sbZzsxgmnsrqtxxVO" , "yjxxGrid"};

	// 货物及劳务
	private static Map<String, String> hwNameMap;
	// 服务、不动产和无形资产
	private static Map<String, String> fwNameMap;
	//
	private static Map<String, String> attachNameMap;
	
	// 本期预缴税额本期数 货物
	private static Map<String, String> yjhwMap;
	// 本期预缴税额本期数 服务
	private static Map<String, String> yjfwMap;
	static {
		hwNameMap = new HashMap<String, String>();
		fwNameMap = new HashMap<String, String>();

		hwNameMap.put("yzzzs3_hwlw_bnlj", "yzzzsbhsxse");
		hwNameMap.put("dkzzsfp3_hwlw_bnlj", "swjgdkdzzszyfpbhsxse");
		hwNameMap.put("skqjfp3_hwlw_bnlj", "skqjkjdptfpbhsxse");
		hwNameMap.put("xssygdysgdzc_hwlw_bnlj", "xssygdysgdzcbhsxse");
		hwNameMap.put("xssy_skqjfp_hwlw_bnlj", "skqjkjdptfpbhsxse1");

		hwNameMap.put("msxse_hwlw_bnlj", "msxse");
		hwNameMap.put("xwqymsxse_hwlw_bnlj", "xwqymsxse");
		hwNameMap.put("wdqzdxse_hwlw_bnlj", "wdqzdxse");
		hwNameMap.put("qtmsxse_hwlw_bnlj", "qtmsxse");
		hwNameMap.put("ckmsxse_hwlw_bnlj", "ckmsxse");
		hwNameMap.put("ckms_skqjfp_hwlw_bnlj", "skqjkjdptfpxse1");
		hwNameMap.put("bqynse_hwlw_bnlj", "bqynse");
		hwNameMap.put("bqynsejze_hwlw_bnlj", "bqynsejze");
		hwNameMap.put("bqmse_hwlw_bnlj", "bqmse");
		hwNameMap.put("xwqymse_hwlw_bnlj", "xwqymse");
		hwNameMap.put("wdqzdmse_hwlw_bnlj", "wdqzdmse");
		hwNameMap.put("ynsehj_hwlw_bnlj", "ynsehj");

		fwNameMap.put("yzzzs3_fwbdc_bnlj", "yzzzsbhsxse");
		fwNameMap.put("dkzzsfp3_fwbdc_bnlj", "swjgdkdzzszyfpbhsxse");
		fwNameMap.put("skqjfp3_fwbdc_bnlj", "skqjkjdptfpbhsxse");
		fwNameMap.put("yzzzs5_fwbdc_bnlj", "xsczbdcbhsxse");
		fwNameMap.put("dkzzsfp5_fwbdc_bnlj", "swjgdkdzzszyfpbhsxse1");
		fwNameMap.put("skqjfp5_fwbdc_bnlj", "skqjkjdptfpbhsxse2");

		fwNameMap.put("msxse_fwbdc_bnlj", "msxse");
		fwNameMap.put("xwqymsxse_fwbdc_bnlj", "xwqymsxse");
		fwNameMap.put("wdqzdxse_fwbdc_bnlj", "wdqzdxse");
		fwNameMap.put("qtmsxse_fwbdc_bnlj", "qtmsxse");
		fwNameMap.put("ckmsxse_fwbdc_bnlj", "ckmsxse");
		fwNameMap.put("ckms_skqjfp_fwbdc_bnlj", "skqjkjdptfpxse1");
		fwNameMap.put("bqynse_fwbdc_bnlj", "bqynse");
		fwNameMap.put("bqynsejze_fwbdc_bnlj", "bqynsejze");
		fwNameMap.put("bqmse_fwbdc_bnlj", "bqmse");
		fwNameMap.put("xwqymse_fwbdc_bnlj", "xwqymse");
		fwNameMap.put("wdqzdmse_fwbdc_bnlj", "wdqzdmse");
		fwNameMap.put("ynsehj_fwbdc_bnlj", "ynsehj");

		attachNameMap = new HashMap<String, String>();
		attachNameMap.put("kce3_qcye", "flzlqcye");
		attachNameMap.put("kce5_qcye", "flzlqcye5");
		// 税务机关代开的增值税专用发票不含税销售额本期 货物
		attachNameMap.put("dkzzsfp3_hwlw_bqs", "yshwlwFpdkbhsxse");
		// 税务机关代开的增值税专用发票不含税销售额本期 劳务
		attachNameMap.put("dkzzsfp3_fwbdc_bqs", "ysfwFpdkbhsxse");
		// 税务机关代开的增值税专用发票不含税销售额本期 劳务 5%征收率
		attachNameMap.put("dkzzsfp5_fwbdc_bqs", "ysfwFpdkbhsxse5");
		
		yjhwMap = new HashMap<String, String>();
		yjhwMap.put("bqyjse_hwlw_bqs", "yjye1");
		yjfwMap = new HashMap<String, String>();
		yjfwMap.put("bqyjse_fwbdc_bqs", "yjye1");
		
	}

	@Override
	public Map<String, String> getNameMap(String key) {
		String[] keys = key.split("--");
		String vo = keys[0];
		String index = keys[1];
		if ("zzsxgmnsrqcsxxGrid".equals(vo) && "3".equals(index)) {
			return hwNameMap;
		} else if ("zzsxgmnsrqcsxxGrid".equals(vo) && "4".equals(index)) {
			return fwNameMap;
		} else if ("sbZzsxgmnsrqtxxVO".equals(vo)) {
			return attachNameMap;
		} else if ("yjxxGrid".equals(vo)) {
			return index.indexOf("101016") == 0 || index.indexOf("101017") == 0 ? yjfwMap
					: yjhwMap;
		}
		return null;
	}

	@Override
	public String getIndexField(String vname) {
		if ("yjxxGrid".equals(vname)) {
			return "zspmDm";
		}
		return "ewblxh";
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
