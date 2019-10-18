package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import org.springframework.stereotype.Service;


@Service("salaryservice_hebei")
public class HeBSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {

	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		return "河北省";
	}

}
