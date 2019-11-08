package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10101_tianjin;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10102_tianjin;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taxRptservice_tianjin")
public class TjTaxRptServiceImpl extends DefaultTaxRptServiceImpl {
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

		List<String> listCondition = new ArrayList<String>();
		if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10101_tianjin.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo
					.queryByCondition(TaxReportDetailVO.class,
							"pk_taxreport = ? and nvl(dr,0) = 0", params);
			HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
			for (TaxReportDetailVO detailvo : vos) {
				hmDetail.put(detailvo.getReportname().trim(), detailvo);
			}

			// 排除公式中含有没有显示报表的公式
			lab1: for (String condition : sacondition) {
				String[] saReportname = getReportNameFromCondition(condition);
				for (String reportname : saReportname) {
					if (hmDetail.containsKey(reportname.trim()) == false) {
						continue lab1;
					}
				}
				listCondition.add(condition);
			}
		} else if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10102_tianjin.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo
					.queryByCondition(TaxReportDetailVO.class,
							"pk_taxreport = ? and nvl(dr,0) = 0", params);
			HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
			for (TaxReportDetailVO detailvo : vos) {
				hmDetail.put(detailvo.getReportname().trim(), detailvo);
			}

			// 排除公式中含有没有显示报表的公式

			lab1: for (String condition : sacondition) {
				String[] saReportname = getReportNameFromCondition(condition);
				for (String reportname : saReportname) {
					if (hmDetail.containsKey(reportname) == false) {
						continue lab1;
					}
				}
				listCondition.add(condition);
			}
		}
		return listCondition.toArray(new String[0]);
	}

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "天津";
	}
}
