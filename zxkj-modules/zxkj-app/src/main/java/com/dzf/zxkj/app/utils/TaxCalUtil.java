package com.dzf.zxkj.app.utils;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;

public class TaxCalUtil {
	
	/**
	 * 根据公司性质+备注计算税率
	 * @param memo
	 * @param gsxz
	 * @return
	 */
	public static DZFDouble taxCal(String memo, String gsxz) {

		if (StringUtil.isEmpty(memo) || StringUtil.isEmpty(gsxz)) {
			return DZFDouble.ZERO_DBL;
		}

		if ("商品收入".equals(memo) || "服务收入".equals(memo) || "其他收入".equals(memo)) {
			if (gsxz.equals("一般纳税人")) {
				return new DZFDouble(0.17);// 一般纳税人都是
			} else {
				return new DZFDouble(0.03);// 小规模纳税人
			}
		} else if ("购买商品".equals(memo) || "购买材料".equals(memo) || "购买资产".equals(memo) || "购买其他".equals(memo)) {
			if (gsxz.equals("一般纳税人")) {
				return new DZFDouble(0.17);
			}
		}
		return DZFDouble.ZERO_DBL;
	}

}
