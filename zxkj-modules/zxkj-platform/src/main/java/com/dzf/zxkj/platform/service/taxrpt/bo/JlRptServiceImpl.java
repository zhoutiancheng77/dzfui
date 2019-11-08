package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taxRptservice_jilin")
public class JlRptServiceImpl extends DefaultTaxRptServiceImpl {
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
	
	protected String checkForSB_ZLBH10412(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		
		String rpt1 = "A200000所得税月(季)度预缴纳税申报表";
		if (listReportName.contains(rpt1)) {
			String qylx = (String) spreadtool.getCellValue(mapJson, rpt1, 5, 1);
			if ("一般企业".equals(qylx) || "跨地区经营汇总纳税企业总机构".equals(qylx)) {
				
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 31, 8))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否科技型中小企业<br>";
				}
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 31, 3))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否高新技术企业<br>";
				}
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 32, 3))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否技术入股递延纳税事项<br>";
				}
				
//				if(PeriodType.jidureport == reportvo.getPeriodtype()){//季报才校验
					if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 36, 8))) {//小型微利企业
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否小型微利企业<br>";
					}
					if (getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 34, 8)).doubleValue() <= 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季末从业人数，且人数必须大于0且为整数<br>";
					}
					
					if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 35, 3)).doubleValue() < 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季初资产总额（万元），且季初资产总额必须大于等于0<br>";
					}
					if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 35, 8)).doubleValue() < 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季末资产总额（万元），且季末资产总额必须大于等于0<br>";
					}
//				}
				
			}
			
		}
		
		return errmsg;
	}
	
	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {

		return new String[0];
	}

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "吉林";
	}
}
