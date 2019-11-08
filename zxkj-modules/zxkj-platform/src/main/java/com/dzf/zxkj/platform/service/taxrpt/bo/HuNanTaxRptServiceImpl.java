package com.dzf.zxkj.platform.service.taxrpt.bo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.model.gl.jiangsutaxrpt.TaxRptConst;
import com.dzf.model.gl.taxrpt.TaxReportDetailVO;
import com.dzf.model.gl.taxrpt.TaxReportVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

@Service("taxRptservice_hunan")
public class HuNanTaxRptServiceImpl extends DefaultTaxRptServiceImpl {
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
		return "湖南";
	}
}
