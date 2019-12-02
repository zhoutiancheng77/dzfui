package com.dzf.zxkj.platform.service.zncs.image.impl;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;

public class OcrVoucherNoGenerator {

	// 按照顺序生成凭证
	public static synchronized String getPzh(String pk_corp, DZFDate doperatedate, ImageGroupVO groupvo) {
		String pzh = null;
		if (!StringUtil.isEmpty(groupvo.getPzh())) {
			pzh = groupvo.getPzh();
		} else {
			YntBoPubUtil yntBoPubUtil = (YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
			pzh = yntBoPubUtil.getNewVoucherNo(pk_corp, doperatedate);
		}
//		System.out.println(pzh+"    "+groupvo.getPk_image_group());
		return pzh;
	}
}
