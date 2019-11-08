package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("taxRptservice_gansu")
public class GsRptServiceImpl extends DefaultTaxRptServiceImpl {
	@Override
	public String checkReportData(Map mapJson, CorpVO corpvo,
								  TaxReportVO reportvo,
								  HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo)
			throws DZFWarpException {
		String errmsg = "";
		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10412)){
			errmsg = checkForSB_ZLBH10412(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		}
		
		return errmsg;
	}
	
	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {

		return new String[0];
	}

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "甘肃";
	}
}
