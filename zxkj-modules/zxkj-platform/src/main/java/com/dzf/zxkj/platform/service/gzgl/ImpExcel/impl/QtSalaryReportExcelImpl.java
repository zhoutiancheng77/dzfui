package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.YntArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("salaryservice_other")
public class QtSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		if(corpvo.getTax_area() == null){
			return "";
		}else{
			YntArea vo = (YntArea) singleObjectBO.queryByPrimaryKey(YntArea.class, Integer.toString(corpvo.getTax_area()));
			if (vo == null || StringUtil.isEmpty(vo.getRegion_name())) {
				return "";
			} else {
				return vo.getRegion_name();
			}
		}
	}

}
