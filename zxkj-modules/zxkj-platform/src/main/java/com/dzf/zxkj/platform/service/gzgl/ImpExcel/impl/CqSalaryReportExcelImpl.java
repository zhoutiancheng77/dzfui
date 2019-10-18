package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import org.springframework.stereotype.Service;


@Service("salaryservice_chongqing")
public class CqSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {

	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		return "重庆市";
	}
}
