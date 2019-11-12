package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportNewQcInitVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10101_beijing;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10102_beijing;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.impl.SpecialSheetSetter;
import com.dzf.zxkj.platform.service.taxrpt.impl.TaxDeclarationServiceImpl;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

//  北京地区
@Service("taxRptservice_beijing")
@Slf4j
public class BjTaxRptServiceImpl extends DefaultTaxRptServiceImpl {

	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;

//	@Override
//	public List<RptBillVO> getRptBillVO(TaxReportVO paravo, SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException {
//		// 先查询客户档案中勾选的报表
//
//		SQLParameter params = new SQLParameter();
//		params.addParam(paravo.getPk_corp());
//		CorpTaxRptVO[] taxrptvos = (CorpTaxRptVO[]) sbo.queryByCondition(CorpTaxRptVO.class,
//				"nvl(dr,0)=0 and pk_corp=? and taxrptcode like '" + paravo.getSb_zlbh() + "%'", params);
//		List listRptcode = new ArrayList<String>();
//
//		DZFBoolean isSave = DZFBoolean.FALSE;
//		if (taxrptvos != null && taxrptvos.length > 0) {
//			isSave = DZFBoolean.TRUE;
//			DZFBoolean isselect = null;
//			for (CorpTaxRptVO vo : taxrptvos) {
//				isselect = vo.getIsselect();
//				if (isselect != null && isselect.booleanValue()) {
//					listRptcode.add(vo.getTaxrptcode());
//				}
//			}
//		}
//
//		// 查询全部默认必填报表
//		params = new SQLParameter();
//		params.addParam(paravo.getLocation());
//		params.addParam(paravo.getSb_zlbh());
//		TaxRptTempletVO[] votemplets = (TaxRptTempletVO[]) sbo.queryByCondition(TaxRptTempletVO.class,
//				"nvl(dr,0)=0 and rtrim(location)= ? and sb_zlbh=? order by orderno", params);
//		volist = new ArrayList<RptBillVO>();
//
//		RptBillVO vo = null;
//		int iXH = 1;
//		for (TaxRptTempletVO tvo : votemplets) {
//			// if (listRptcode.size() > 0) // 客户档案设置过填报列表，没有设置的报表跳过
//			if (isSave.booleanValue()) {
//				if (listRptcode.contains(tvo.getReportcode()) == false) {
//					continue;
//				}
//			} else // 客户档案没设置过填报列表，只填写必填内容 （ 印花税只有一张表，默认加载出来）
//			{
//				if ((tvo.getIfrequired() == null || tvo.getIfrequired().booleanValue() == false)
//						&& !TaxRptConst.SB_ZLBHD1.equals(tvo.getSb_zlbh())
//						&& !(TaxRptConst.SB_ZLBH10412.equals(tvo.getSb_zlbh()) && "10412004".equals(tvo.getReportcode()))) {// 减免所得税优惠明细表(附表3)
//					continue;
//				}
//			}
//			vo = new RptBillVO();
//			vo.setXh(String.valueOf(iXH++));
//			vo.setBb_zlid(tvo.getReportcode());
//			vo.setBb_zlmc(tvo.getReportname());
//			volist.add(vo);
//		}
//		return volist;
//	}

	@Override
	public void checkBeforeProcessApprove(TaxReportVO reportvo, SingleObjectBO sbo, CorpVO corpvo)
			throws DZFWarpException {

	}

	@Override
	public String checkReportData(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
								  HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10101)) {
			errmsg = checkForSB_ZLBH10101_bj(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10412)) {
			errmsg = checkForSB_ZLBH10412_bj(mapJson, corpvo, reportvo, hmRptDetail, sbo);
			errmsg += checkForSB_ZLBH10412(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		}
		
//		else if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10102)) {
//			errmsg = checkForSB_ZLBH10102_bj(mapJson, corpvo, reportvo, hmRptDetail, sbo);
//		}
		
		return errmsg;
	}
	
	@Override
	public String checkReportDataWarning(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String warningmsg = "";
		//赵丹说先去掉
//		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10102)) {
//			warningmsg = checkForSB_ZLBH10102_bj(mapJson, corpvo, reportvo, hmRptDetail, sbo);
//		}
		return warningmsg;
	}
	
	private String checkForSB_ZLBH10412_bj(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";

		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		String rpt1 = "A200000所得税月(季)度预缴纳税申报表";
		String rpt2 = "A201010免税收入、减计收入、所得减免等优惠明细表";
		String rpt3 = "A201020固定资产加速折旧(扣除)优惠明细表";
		String rpt4 = "A201030减免所得税优惠明细表";
		DZFDouble val1 = null;
		DZFDouble val2 = null;
		if(listReportName.contains(rpt1) && listReportName.contains(rpt2)){
			val1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 13, 8));
			val2 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 48, 5));
			
			if(val1.doubleValue() != 0 && val1.compareTo(val2) != 0){
				errmsg += "A200000所得税月(季)度预缴纳税申报表!I14 == A201010免税收入、减计收入、所得减免等优惠明细表!F49<br>";
			}
		}
		if(listReportName.contains(rpt1) && listReportName.contains(rpt3)){
			val1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 14, 8));
			val2 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt3, 10, 6));
			
			if(val1.doubleValue() != 0 && val1.compareTo(val2) != 0){
				errmsg += "A200000所得税月(季)度预缴纳税申报表!I15 == A201020固定资产加速折旧(扣除)优惠明细表!G11<br>";
			}
		}
		if(listReportName.contains(rpt1) && listReportName.contains(rpt4)){
			val1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 16, 8));
			val2 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt4, 4, 2));
			
			if(val1.doubleValue() != 0 && val1.compareTo(val2) != 0){
				errmsg += "A200000所得税月(季)度预缴纳税申报表!I17 == A201030减免所得税优惠明细表!C5<br>";
			}
		}
		if(listReportName.contains(rpt1) && listReportName.contains(rpt4)){
			val1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 19, 8));
			val2 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt4, 37, 6));
			
			if(val1.doubleValue() != 0 && val1.compareTo(val2) != 0){
				errmsg += "A200000所得税月(季)度预缴纳税申报表!I20 == A201030减免所得税优惠明细表!G38<br>";
			}
		}
		
		return errmsg;
	}

	private String checkForSB_ZLBH10102_bj(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";

		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		String maintablename = "增值税纳税申报表";
		YntBoPubUtil yntBoPubUtil = (YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		Integer corpschema = yntBoPubUtil.getAccountSchema(corpvo.getPk_corp());

		String tglwsr = "500102";// 提供劳务收入
		String spxssr = "500101";// 商品销售收入
		String queryAccountRule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
			// 2007会计准则
			spxssr = gl_accountcoderule.getNewRuleCode("600101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
			tglwsr = gl_accountcoderule.getNewRuleCode("600102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
			// 2013会计准则
			spxssr = gl_accountcoderule.getNewRuleCode("500101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
			tglwsr = gl_accountcoderule.getNewRuleCode("500102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		}

		if (listReportName.contains(maintablename)) {
			YntCpaccountVO[] accountVO  = accountService.queryByPk(corpvo.getPk_corp());
			// E7+F7+F10 6行4列和5列 9行5列
			DZFDouble dkfp = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 6, 4))
					.add(getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 6, 5)))
					.add(getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 9, 5)));

			DZFDouble v3 = DZFDouble.ZERO_DBL;
			if (reportvo.getPeriodtype() == PeriodType.jidureport) {
				v3 = getDzfDouble(spreadtool.getFormulaValue(null,
						"glamtoccr4(\"" + spxssr + "+" + tglwsr
								+ "\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\",\"2\",\"\")",
						null, corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
			} else if (reportvo.getPeriodtype() == PeriodType.monthreport) {// 月报
				v3 = getDzfDouble(spreadtool.getFormulaValue(null,
						"glamtoccr2(\"" + spxssr + "+" + tglwsr
								+ "\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\",\"2\",\"\")",
						null, corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
			}

			if (dkfp.doubleValue() != 0) {
				DZFDouble d1 = dkfp.setScale(2, DZFDouble.ROUND_HALF_UP);

				if (d1.equals(v3) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
							+ ">  从局端获取的税务机关代开的增值税专用发票不含税销售额[" + d1.toString() + "]与您账中的专票销售[" + v3.toString()
							+ "]不一致，请您检查下凭证!";
				}
			}
		}
		return errmsg;
	}

	private String checkForSB_ZLBH10101_bj(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		String maintablename = "增值税纳税申报表（适用于增值税一般纳税人）";
		if (listReportName.contains(maintablename)) {
//			YntCpaccountVO[] accountVO  = AccountCache.getInstance().get(null, corpvo.getPk_corp());
			// 本期应补(退)税额=glamtoccr("222109+2221009","@year","@period","y","cr","@corp")
			// G43+L43 42行6列和11列
//			DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 44, 6));
//			DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 44, 11));
//
//			DZFDouble v3 = getDzfDouble(spreadtool.getFormulaValue(null,
//					"glamtoccr(\"222109+2221009\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\")",
//					getQcData(corpvo, reportvo, sbo), corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
//
//			if (v1.add(v2).doubleValue() != 0) {
//				DZFDouble d1 = v1.add(v2).setScale(2, DZFDouble.ROUND_HALF_UP);
//
//				if (d1.equals(v3) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
//				{
//					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
//							+ ">  [本期应补(退)税额(G45+L45) 与记账凭证中计提增值税额数不符，请检查] ; <br>(数值：" + d1.toString() + " = "
//							+ v3.toString() + ")";
//				}
//			}
			
			
			// 当主表24行1、3列之和大于0时，附表四第4列2、3、4、5行合计值只能小于等于主表24行1、3列合计数。请修改附表四第4列2、3、4、5行数据。
			// "增值税纳税申报表（适用于增值税一般纳税人）!G35 +增值税纳税申报表（适用于增值税一般纳税人）!L35 >=
			// SUM(增值税纳税申报表附列资料（四）!F8 : F11) and 增值税纳税申报表（适用于增值税一般纳税人）!G35
			// +增值税纳税申报表（适用于增值税一般纳税人）!L35 > 0"

			String fb4 = "增值税纳税申报表附列资料（四）";
			if (listReportName.contains(fb4)) {
				DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 34, 6)); // G35
				DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 39, 6)); // L35
				DZFDouble v3 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 7, 5)); // F8
				DZFDouble v4 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 8, 5)); // F9
				DZFDouble v5 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 9, 5)); // F10
				DZFDouble v6 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 10, 5)); // F11
				if (v1.add(v2).compareTo(DZFDouble.ZERO_DBL) > 0) {
					if (v1.add(v2).compareTo(v3.add(v4).add(v5).add(v6)) < 0) {
						errmsg += (errmsg.length() > 0 ? "<br>" : "")
								+ "当主表24行1、3列之和大于0时，附表四第4列2、3、4、5行合计值只能小于等于主表24行1、3列合计数。请修改附表四第4列2、3、4、5行数据。<br>(数值: "
								+ v3.add(v4).add(v5).add(v6).setScale(2, DZFDouble.ROUND_HALF_UP).toString() + " <= "
								+ v1.add(v2).setScale(2, DZFDouble.ROUND_HALF_UP).toString() + ")";
					}
				}
			}
		}
		return errmsg;
	}

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		// 年度汇算清缴不需要期初
		HashMap hmQCData = new HashMap();

		String sb_zlbh = reportvo.getSb_zlbh();
		if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh) 
				|| TaxRptConst.SB_ZLBH10102.equals(sb_zlbh)
				|| TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)
				|| TaxRptConst.SB_ZLBH50101.equals(sb_zlbh)
				|| TaxRptConst.SB_ZLBH50102.equals(sb_zlbh)
				|| TaxRptConst.SB_ZLBH1010201.equals(sb_zlbh)
				|| TaxRptConst.SB_ZLBH10601.equals(sb_zlbh)) {

			hmQCData = initTax(reportvo);
		}
		// if ("04".equals(reportvo.getZsxm_dm()) &&
		// TaxRptConst.SB_ZLBH_SETTLEMENT.equals(reportvo.getSb_zlbh())) {
		// return hmQCData;
		// }
		return hmQCData;
	}

	private HashMap<String, Object> initTax(TaxReportVO reportvo) {

		HashMap<String, Object> initData = new HashMap<String, Object>();

		parseInitData(initData, reportvo);
		return initData;
	}

	private void parseInitData(HashMap<String, Object> initData, TaxReportVO reportVO) {
		try {
			String pk_corp = reportVO.getPk_corp();
			String sb_zlbh = reportVO.getSb_zlbh();//
//			String period = reportVO.getPeriodto().substring(0, 7);// 期间
			String period = reportVO.getPeriod();
			String pk_sbzl = reportVO.getPk_taxsbzl();
			
//			Integer periodType = null;
//			if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh) 
//					|| TaxRptConst.SB_ZLBH50101.equals(sb_zlbh)) {
//				periodType = PeriodType.monthreport;
//			} else if (TaxRptConst.SB_ZLBH10102.equals(sb_zlbh) 
//					|| TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)
//					|| TaxRptConst.SB_ZLBH50102.equals(sb_zlbh)) {
//				periodType = PeriodType.jidureport;
//			}
//
//			if (periodType == null)
//				return;

			TaxReportNewQcInitVO initvo = queryReportQcByInit(pk_corp,pk_sbzl , period);
			if (initvo == null || StringUtil.isEmpty(initvo.getSpreadfile()))
				return;
			String fileName = initvo.getSpreadfile();
			// String fileName = TaxReportPath.taxReportPath + pk_corp + "_" +
			// period + "_" + sb_zlbh + "_" + periodType + ".ssjson";

			String qcStr = new TaxDeclarationServiceImpl().getReportTemplet(fileName);

			if (StringUtil.isEmpty(qcStr))
				return;
//			Map<String, Object> map = (Map<String, Object>) JSON.parse(qcStr);
			JSONObject map = (JSONObject) JSON.parse(qcStr);
			initData(map, initData,"", 1);
//			if (map != null && map.size() > 0) {
//				for (Map.Entry<String, Object> entry : map.entrySet()) {
//					initData.put(entry.getKey(), entry.getValue());
//				}
//			}

		} catch (Exception e) {
			log.error("错误",e);
		}

	}
	
	private void initData(JSONObject map, HashMap<String, Object> initData, String prefix, int count){
		if(count > 10)
			return;
		
		if (map != null && map.size() > 0) {
			String key;
			Object obj;
			JSONArray arr;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				key = entry.getKey();
				key = key.toLowerCase();
				obj = entry.getValue();
				if(obj == null){
					continue;
				}else if(obj instanceof JSONObject){
					initData((JSONObject)obj, initData, prefix + key + ".", count+1);
				}else if(obj instanceof JSONArray){
					arr = (JSONArray)obj;
					for(int i = 0; i < arr.size(); i++){
						Object oo = arr.get(i);
						if(oo == null){
							continue;
						}else if(oo instanceof JSONObject){
							initData((JSONObject)oo, initData, prefix + key + "[" + i + "]" + ".", count+1);
						}else{
							initData.put(prefix + key + "[" + i + "]", obj);
						}
						
					}
				}else{
					initData.put(prefix + key, obj);
				}
			}
		}
	}

	private TaxReportNewQcInitVO queryReportQcByInit(String pk_corp, String pk_sbzl, String period) {
		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_sbzl);
		TaxReportNewQcInitVO initvo = (TaxReportNewQcInitVO) singleObjectBO.executeQuery(
				" Select * From ynt_taxreportnewqcinit t Where nvl(dr,0) = 0 and t.pk_corp = ? and t.period = ? and t.pk_taxsbzl  = ?  ",
				sp, new BeanProcessor(TaxReportNewQcInitVO.class));

		return initvo;
	}

//	@Override
//	public TaxTypeListDetailVO[] getTaxTypeListDetailVO(boolean showYearInTax, String yearmonth, CorpVO corpvo,
//			SingleObjectBO sbo) throws DZFWarpException {
//		// TaxTypeListDetailVO[] vos = new TaxTypeListDetailVO[showYearInTax ? 4
//		// : 3];
//		List<TaxTypeListDetailVO> list = new ArrayList<TaxTypeListDetailVO>();
//		// 新增北京报表
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("一般纳税人")) {
//			TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//			detailvo.setSb_zlbh("10101");
//
//			// detailvo.setNsrdzdah(null); //纳税人电子档案号，无值
//			String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//			detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo.setZsxm_dm("01"); // 增值税
//			detailvo.setPeriodtype(0); // 月报
//
//			list.add(detailvo);
//
//			// 代征地税
//			TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//			detailvo1.setSb_zlbh("50101");
//
//			detailvo1.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo1.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo1.setZsxm_dm("80"); // 代征地税
//			detailvo1.setPeriodtype(0); // 月报
//
//			list.add(detailvo1);
//
//			// 一般纳税人，所得税季报，A类
//			TaxTypeListDetailVO detailvo2 = new TaxTypeListDetailVO();
//			detailvo2.setSb_zlbh("10412");
//
//			detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo2.setZsxm_dm("04"); // 所得税
//			detailvo2.setPeriodtype(1); // 季报报
//
//			list.add(detailvo2);
//
//			// 一般纳税人 ，文化事业建设费
//			TaxTypeListDetailVO detailvo3 = new TaxTypeListDetailVO();
//			detailvo3.setSb_zlbh(TaxRptConst.SB_ZLBH10601);
//			detailvo3.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo3.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo3.setZsxm_dm(TaxRptConst.ZSXMDM_WHJS);
//			detailvo3.setPeriodtype(PeriodType.monthreport);
//
//			list.add(detailvo3);
//		} else if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//			detailvo.setSb_zlbh("10102");
//			// detailvo.setNsrdzdah(null); //纳税人电子档案号，无值
//			// String sQueryPeriod = new DZFDate(yearmonth +
//			// "-01").getDateBefore(1).toString().substring(0, 7);
//
//			detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo.setZsxm_dm("01"); // 增值税
//			detailvo.setPeriodtype(1); // 季报
//
//			list.add(detailvo);
//
//			// 代征地税
//			TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//			detailvo1.setSb_zlbh("50102");
//
//			detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo1.setZsxm_dm("80"); // 代征地税
//			detailvo1.setPeriodtype(1); // 季报
//
//			list.add(detailvo1);
//
//			// 小规模纳税人，所得税季报，A类
//			TaxTypeListDetailVO detailvo2 = new TaxTypeListDetailVO();
//			detailvo2.setSb_zlbh("10412");
//
//			detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo2.setZsxm_dm("04"); // 所得税
//			detailvo2.setPeriodtype(1); // 季报报
//
//			list.add(detailvo2);
//
//			// 小规模纳税人， 文件事业建设费
//			TaxTypeListDetailVO detailvo3 = new TaxTypeListDetailVO();
//			detailvo3.setSb_zlbh(TaxRptConst.SB_ZLBH10601);
//			detailvo3.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo3.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo3.setZsxm_dm(TaxRptConst.ZSXMDM_WHJS);
//			detailvo3.setPeriodtype(PeriodType.jidureport);
//
//			list.add(detailvo3);
//		} else {
//			throw new BusinessException("当前功能仅支持一般纳税人和小规模纳税人增值税申报、所得税申报、代征地税申报、文化事业建设费申报。");
//		}
//		if (showYearInTax) {
//			// 所得税年度汇算清缴
//			TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH_SETTLEMENT);
//			int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
//			detailvo.setPeriodfrom(year + "-01-01");
//			detailvo.setPeriodto(year + "-12-31");
//			detailvo.setZsxm_dm("04"); // 所得税
//			detailvo.setPeriodtype(2); // 年报
//			list.add(detailvo);
//		}
//		// 增加财报
//		addFinReport(corpvo, yearmonth, list);
//		addStampReport(corpvo, yearmonth, list);
//		return list.toArray(new TaxTypeListDetailVO[0]);
//	}

	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();
		if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10101_beijing.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
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
			String[] sacondition = TaxRptChk10102_beijing.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
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
//		else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
//			String[] sacondition = TaxRptChk10412_beijing.saCheckCondition;
//			// 读取报表内容
//			SQLParameter params = new SQLParameter();
//			params.addParam(reportvo.getPk_taxreport());
//			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
//					"pk_taxreport = ? and nvl(dr,0) = 0", params);
//			HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
//			for (TaxReportDetailVO detailvo : vos) {
//				hmDetail.put(detailvo.getReportname().trim(), detailvo);
//			}
//
//			// 排除公式中含有没有显示报表的公式
//
//			lab1: for (String condition : sacondition) {
//				String[] saReportname = getReportNameFromCondition(condition);
//				for (String reportname : saReportname) {
//					if (hmDetail.containsKey(reportname) == false) {
//						continue lab1;
//					}
//				}
//				listCondition.add(condition);
//			}
//		}
		return listCondition.toArray(new String[0]);
	}

	// @Override
	// public Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map
	// objMapReport, SpreadTool spreadtool,
	// TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
	// ITaxReportService taxReportSer = (ITaxReportService)
	// SpringUtils.getBean("taxReportSer");
	// taxReportSer.sendTaxReport(corpVO, objMapReport, spreadtool, reportvo);
	// return null;
	// }

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "北京";
	}
	
	@Override
	public void processZeroDeclaration(TaxReportVO reportvo,
									   CorpVO corpvo, CorpTaxVo corptaxvo, SingleObjectBO singleObjectBO)
			throws DZFWarpException {
		if(reportvo == null)
			return;
		
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[])reportvo.getChildren();
		
		String pk_templet = "";
		if(detailvos!=null && detailvos.length>0){
			pk_templet =detailvos[0].getPk_taxrpttemplet() ;
		}
		TaxRptTempletVO rpttempletvo = (TaxRptTempletVO) singleObjectBO
				.queryByPrimaryKey(TaxRptTempletVO.class, pk_templet);
		String strJson = readFileString(rpttempletvo.getSpreadtemplet());
		ObjectMapper objectMapper = getObjectMapper();
		Map objMap = null;
		try {
			objMap = (Map) objectMapper.readValue(strJson,
					LinkedHashMap.class);
		} catch (IOException e) {
		}
		if(objMap == null){
			objMap = new LinkedHashMap();
		}
		List<String> listRptName = new ArrayList<String>();
		if(detailvos!=null && detailvos.length>0){
			for (TaxReportDetailVO detailvo : detailvos) {
				listRptName.add(detailvo.getReportname().trim());
			}
		}
		HashMap<String, Object> qcdata = getQcData(corpvo, reportvo, null);
		Map objMapRet = new SpreadTool().fillReportWithZero(objMap,listRptName, reportvo,qcdata, corpvo,corptaxvo);
		// 设置企业所得税的封面字段
		SpecialSheetSetter setter = new SpecialSheetSetter(
				singleObjectBO, new SpreadTool(), corpService);
		String sReturn = null;
		try {
			setter.setSpecialSheetDefaultValue(reportvo, objMapRet);
			sReturn = objectMapper.writeValueAsString(objMapRet);
		} catch (Exception e) {
			log.error("零申报-解析失败", e);
			throw new BusinessException("解析报表失败");
		}
		
		processSaveFile(sReturn, reportvo, corpvo);
		
		if(detailvos!=null && detailvos.length>0){
			//更新子表字段
			singleObjectBO.updateAry(detailvos, new String[]{"spreadfile"});
			
			reportvo.setSpreadfile(detailvos[0].getSpreadfile());
			
			singleObjectBO.update(reportvo, new String[]{"spreadfile"});
		}
	}
	
	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getSerializerProvider().setNullValueSerializer(
				new JsonSerializer<Object>() {
					@Override
					public void serialize(Object value, JsonGenerator jg,
							SerializerProvider sp) throws IOException,
							JsonProcessingException {
						jg.writeString("");
					}
				});
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(
				SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,
				false);
		return objectMapper;
	}
}
