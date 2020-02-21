package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.*;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.config.TaxJstcConfig;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.BondedSetVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10101_jiangsu;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10102_jiangsu;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.BaseRequestVO;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.InitParse;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.RequestBodyVO;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary.FinancialOrdinaryInit;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.enterprise.FinancialEnterpriseRequest;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.ordinary.FinancialOrdinaryRequest;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.small.FinancialSmallRequest;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a.IncomeTaxInit;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b.IncomeTaxRequest;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary.VATOrdinaryInit;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary.VATOrdinaryRequest;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small.VATSmallInit;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small.VATSmallRequest;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.impl.SpecialSheetSetter;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.service.zncs.image.IBalanceService;
import com.dzf.zxkj.platform.util.taxrpt.TaxReportPath;
import com.dzf.zxkj.platform.util.taxrpt.TaxRptemptools;
import com.dzf.zxkj.platform.util.taxrpt.jiangsu.EncryptDecrypt;
import com.dzf.zxkj.platform.util.taxrpt.jiangsu.HttpUtil;
import com.dzf.zxkj.platform.util.taxrpt.jiangsu.ZipUtil;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

//江苏地区
@Service("taxRptservice_jiangsu")
@Slf4j
public class JsTaxRptServiceImpl extends DefaultTaxRptServiceImpl {

	@Autowired
	private SingleObjectBO sbov;
//	private static Logger log = Logger.getLogger(JsTaxRptServiceImpl.class);
	
//	private ITaxRequestSrv taxrequestsrv;
	@Autowired
	private IFctpubService pubFctService;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private TaxJstcConfig taxJstcConfig;
//	public ITaxRequestSrv getTaxrequestsrv() {
//		if (taxrequestsrv == null) {
//			taxrequestsrv = new TaxRequestSrvImpl();
//		}
//		return taxrequestsrv;
//	}
	/**
	 * 江苏
	 */
	@Override 
	public List<TaxReportVO> getTypeList(CorpVO corpvo, CorpTaxVo corptaxvo, String yearmonth,
										 String operatorid, String operatedate, SingleObjectBO sbo)
			throws DZFWarpException {
		List<TaxReportVO> list = super.getTypeList(corpvo,corptaxvo, yearmonth,
				operatorid, operatedate, sbo);
		return list;
	}

//	@Override
//	public TaxTypeListDetailVO[] getTypeList(CorpVO corpvo, String yearmonth,
//			String operatorid, String operatedate, SingleObjectBO sbo)
//			throws DZFWarpException {
//		String pk_corp = corpvo.getPk_corp();
//		TaxTypeListDetailVO[] vos = null;
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		params.addParam(yearmonth);
//		boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
//		TaxTypeListVO[] voheads = (TaxTypeListVO[]) sbo.queryByCondition(
//				TaxTypeListVO.class,
//				"nvl(dr,0) = 0 and pk_corp=? and yearmonth=?", params);
//		if (voheads != null && voheads.length > 0) {
//			TaxTypeListVO headold = voheads[0];
//			params = new SQLParameter();
//			params.addParam(voheads[0].getPrimaryKey());
//			TaxTypeListDetailVO[] vosold = (TaxTypeListDetailVO[]) sbo.queryByCondition(
//					TaxTypeListDetailVO.class,
//					"nvl(dr,0)=0 and pk_taxtypelist=?", params);
//			sbo.deleteObject(headold);
//			sbo.deleteVOArray(vosold);
//		}
//		if ("on".equals(JSTaxPropUtil.getprop("service_switch"))
//				&& !StringUtil.isEmpty(corpvo.getVsoccrecode())
//				&& !StringUtil.isEmpty(corpvo.getVstatetaxpwd())) {
//			vos = getDeclarableInventory(yearmonth, corpvo);
//		} else {
//			vos = getTaxTypeListDetailVO(showYearInTax, yearmonth, corpvo, sbo);
//		}
//		// 建立主表记录
//		String pk = IDGenerate.getInstance().getNextID(pk_corp);
//		
//		TaxTypeListVO headvo = new TaxTypeListVO();
//		headvo.setPk_taxtypelist(pk);
//		headvo.setPk_corp(pk_corp);
//		headvo.setNsrsbh(corpvo.getTaxcode());
////		headvo.setNsrdzdah(vos[0].getNsrdzdah());
//		headvo.setCoperatorid(operatorid);
//		headvo.setDoperatedate(new DZFDate(operatedate));
//		headvo.setYearmonth(yearmonth);
//		headvo.setDr(0);
//		for (TaxTypeListDetailVO vo : vos) {
//			pk = IDGenerate.getInstance().getNextID(pk_corp);
//			vo.setPk_taxtypelistdetail(pk);
//			vo.setPk_corp(pk_corp);
//			vo.setPk_taxtypelist(headvo.getPrimaryKey());
//			vo.setDr(0);
//			vo.setPeriodtype(getPeriodType(vo.getPeriodfrom(), vo.getPeriodto()));
//		}
//		// 增加主表
//		sbo.insertVOWithPK(headvo);
//		// 增加子表
//		if (vos.length > 0) {
//			sbo.insertVOArr(pk_corp, vos);
//		}
//		return vos;
//	}
	
//	@Override
//	public TaxTypeListDetailVO[] getTaxTypeListDetailVO(boolean showYearInTax, String yearmonth, CorpVO corpvo,
//			SingleObjectBO sbo) throws DZFWarpException {
//		List<TaxTypeListDetailVO> list = new ArrayList<TaxTypeListDetailVO>();
//		// TaxTypeListDetailVO[] vos = new TaxTypeListDetailVO[showYearInTax ? 3
//		// : 2];// TaxTypeListDetailVO[1];
//		//
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("一般纳税人")) {
//			TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//			detailvo.setSb_zlbh("10101");
//
//			// detailvo.setNsrdzdah(null); //纳税人电子档案号，没从江苏网站获取，所以无值
//			String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//			detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo.setZsxm_dm("01"); // 增值税
//			detailvo.setPeriodtype(0); // 月报
//
//			list.add(detailvo);
//
//			// 一般纳税人，所得税季报，A类
//			TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//			detailvo1.setSb_zlbh("10412");
//
//			detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo1.setZsxm_dm("04"); // 所得税
//			detailvo1.setPeriodtype(1); // 季报报
//
//			list.add(detailvo1);
//			// 一般纳税人，所得税季报，B类
//			TaxTypeListDetailVO intaxB = new TaxTypeListDetailVO();
//			intaxB.setSb_zlbh("10413");
//			intaxB.setPeriodfrom(getQuarterStartDate(yearmonth));
//			intaxB.setPeriodto(getQuarterEndDate(yearmonth));
//			intaxB.setZsxm_dm("10413"); // 所得税
//			intaxB.setPeriodtype(1); // 季报报
//			list.add(intaxB);
//		} else if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			int month = Integer.parseInt(yearmonth.substring(5, 7));
//			// 季度下个月才能看到
//			if ("true".equals(JSTaxPropUtil.getprop("dev_mode"))
//					|| month % 3 == 1) {
//				TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//				detailvo.setSb_zlbh("10102");
//				detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
//				detailvo.setPeriodto(getQuarterEndDate(yearmonth));
//				detailvo.setZsxm_dm("01"); // 增值税
//				detailvo.setPeriodtype(1); // 季报
//				
//				list.add(detailvo);
//				
//				// 小规模纳税人，所得税季报，A类
//				TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//				detailvo1.setSb_zlbh("10412");
//				
//				detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//				detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//				detailvo1.setZsxm_dm("04"); // 所得税
//				detailvo1.setPeriodtype(1); // 季报报
//				
//				list.add(detailvo1);
//				
//				// 小规模，所得税季报，B类
//				TaxTypeListDetailVO intaxB = new TaxTypeListDetailVO();
//				intaxB.setSb_zlbh("10413");
//				intaxB.setPeriodfrom(getQuarterStartDate(yearmonth));
//				intaxB.setPeriodto(getQuarterEndDate(yearmonth));
//				intaxB.setZsxm_dm("10413"); // 所得税
//				intaxB.setPeriodtype(1); // 季报报
//				list.add(intaxB);
//			}
//		} else {
//			throw new BusinessException("当前功能仅支持一般纳税人和小规模纳税人增值税申报、所得税申报。");
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
//		addOtherReport(corpvo, yearmonth, list);
//		addLocalTax(corpvo, yearmonth, list);
//		return list.toArray(new TaxTypeListDetailVO[0]);
//	}
	
	// 江苏应申报清册接口
	private List<String> getDeclarableInventory(String yearmonth, CorpVO corpvo,CorpTaxVo taxvo) {
		JSONObject rsJosn = getInventoryJson(yearmonth, corpvo,taxvo);
		String status = rsJosn.getString("RESULT");
		
		List<String> list = new ArrayList<String>();
		if ("0000".equals(status)) {
			List<TaxTypeSBZLVO> zlist = queryTypeSBZLVOs();
			// 按编号、申报周期，确定唯一值
			Map<String, TaxTypeSBZLVO> zlmap = DZfcommonTools
					.hashlizeObjectByPk(zlist,
							new String[] { "sbcode", "sbzq" });

			JSONArray data = (JSONArray) rsJosn.get("DATA");
			Set<String> codes = new HashSet<String>();
			for (Object object : data) {
				JSONObject json = (JSONObject) object;
				String code = json.getString("SB_ZLBH");
				int periodType = getPeriodType(json.getString("SKSSQQ"),
						json.getString("SKSSQZ"));
				if ("10102".equals(code) && periodType == 0) {
					code = TaxRptConst.SB_ZLBH1010201;
				} else if ("29806".equals(code)) {
					code = TaxRptConst.SB_ZLBHC1;
				} else if ("29801".equals(code)) {
					code = TaxRptConst.SB_ZLBHC2;
				}
				
				// 申报种类相同的生成一条记录
				if (codes.contains(code)) {
					continue;
				}
				TaxTypeSBZLVO vo = zlmap.get(code + "," + periodType);
				if (vo != null) {
					list.add(vo.getPk_taxsbzl());
					codes.add(code);
				}
			}
		} else {
			log.error("申报清册查询失败" + rsJosn.getString("MSG"));
//			throw new BusinessException((String) rsJosn.get("MSG"));
		}
		//增加地税
//		addLocalTax(corpvo, yearmonth, list);
		return list;
	
	}
	private List<String> getInventoryCodes(String yearmonth, CorpVO corpvo,CorpTaxVo taxvo) {
		List<String> codes = new ArrayList<String>();
		JSONObject rsJosn = getInventoryJson(yearmonth, corpvo,taxvo);
		String status = rsJosn.getString("RESULT");
		if ("0000".equals(status)) {
			JSONArray data = (JSONArray) rsJosn.get("DATA");
			for (Object object : data) {
				JSONObject json = (JSONObject) object;
				String code = json.getString("SB_ZLBH");
				int periodType = getPeriodType(json.getString("SKSSQQ"),
						json.getString("SKSSQZ"));
				if ("10102".equals(code) && periodType == 0) {
					code = TaxRptConst.SB_ZLBH1010201;
				} else if ("29806".equals(code)) {
					code = TaxRptConst.SB_ZLBHC1;
				} else if ("29801".equals(code)) {
					code = TaxRptConst.SB_ZLBHC2;
				}
				
				// 申报种类相同的生成一条记录
				if (codes.contains(code)) {
					continue;
				}
				codes.add(code);
			}
		} else {
			throw new BusinessException(rsJosn.getString("MSG"));
		}
		return codes;
	}
	
	private JSONObject getInventoryJson(String yearmonth, CorpVO corpvo,CorpTaxVo taxvo ) {
		BaseRequestVO baseReq = getBaseRequset(corpvo,taxvo);

//		baseReq.setServiceid("FW_DZSWJ_YQC");
//		baseReq.getBody().setSign("queryYsbqcService");
		// 已申报未申报
		baseReq.setServiceid("FW_DZSWJ_SBQKCX");
		baseReq.getBody().setSign("queryYsbwsbService");

		Map<String, String> map = new HashMap<String, String>();
		map.put("NSRSBH", corpvo.getVsoccrecode());
		
		String[] period = yearmonth.split("-");
		map.put("ND", period[0]);
		map.put("YF", period[1]);
//		String ywbw = OFastJSON.toJSONString(map);
		String ywbw = JsonUtils.serialize(map);

		ywbw = EncryptDecrypt.encode(ywbw, taxJstcConfig.app_secret);
		baseReq.getBody().setYwbw(ywbw);

		HashMap<String, String> params = new HashMap<String, String>();

//		String bw = OFastJSON.toJSONString(baseReq);
		String bw = JsonUtils.serialize(baseReq);
		String encode = ZipUtil.zipEncode(bw, true);
		params.put("request", encode);

		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);

		String result = HttpUtil.parseRes(rmap.get("response"));
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		return rsJosn;
	}
	
//	@Override
//	public List<RptBillVO> getRptBillVO(TaxReportVO paravo, SingleObjectBO sbo,
//			CorpVO corpvo) throws DZFWarpException {
//		List<RptBillVO> volist = null;
//		SQLParameter params = new SQLParameter();
//		List<String> listRptcode = new ArrayList<String>();
//		
//		if ("on".equals(JSTaxPropUtil.getprop("service_switch"))
//				&& !StringUtil.isEmpty(corpvo.getVsoccrecode())
//				&& !StringUtil.isEmpty(corpvo.getVstatetaxpwd())) {
//			// 通过出表规则查询报表
//			listRptcode.addAll(getReportList(corpvo, paravo));
//		}
//		
//		DZFBoolean isSave = DZFBoolean.FALSE;
//		
//		if(listRptcode.size() == 0) {
//			// 查询客户档案中勾选的报表
//			params.addParam(paravo.getPk_corp());
//			CorpTaxRptVO[] taxrptvos = (CorpTaxRptVO[]) sbo.queryByCondition(
//					CorpTaxRptVO.class,
//					"nvl(dr,0)=0 and pk_corp=? and taxrptcode like '"
//							+ paravo.getSb_zlbh() + "%'", params);
//			if (taxrptvos != null && taxrptvos.length > 0) {
//				
//				isSave = DZFBoolean.TRUE;
//				DZFBoolean isselect;
//				
//				for (CorpTaxRptVO vo : taxrptvos) {
//					isselect = vo.getIsselect();
//					if(isselect != null && isselect.booleanValue()){
//						listRptcode.add(vo.getTaxrptcode());
//					}
//				}
//			}
//		} else if (TaxRptConst.SB_ZLBH1010201.equals(paravo.getSb_zlbh())) {
//			int size = listRptcode.size();
//			for (int i = 0; i < size; i++) {
//				listRptcode.set(i, listRptcode.get(i).replace("10102", "1010201"));
//			}
//		}
//
//		// 查询全部默认必填报表
//		params = new SQLParameter();
//		params.addParam(paravo.getLocation());
//		params.addParam(paravo.getSb_zlbh());
//		TaxRptTempletVO[] votemplets = (TaxRptTempletVO[]) sbo
//				.queryByCondition(
//						TaxRptTempletVO.class,
//						"nvl(dr,0)=0 and rtrim(location)= ? and sb_zlbh=? order by orderno",
//						params);
//		volist = new ArrayList<RptBillVO>();
//
//		RptBillVO vo = null;
//		int iXH = 1;
//		for (TaxRptTempletVO tvo : votemplets) {
//			if (listRptcode.size() > 0) // 客户档案设置过填报列表，没有设置的报表跳过
//			{
//				if (listRptcode.contains(tvo.getReportcode()) == false) {
//					continue;
//				}
//			} else if(isSave.booleanValue()) 
//			{
//				continue;
//			} else // 客户档案没设置过填报列表，只填写必填内容
//			{
//				if (tvo.getIfrequired() == null
//						|| tvo.getIfrequired().booleanValue() == false) {
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
//
//	/**
//	 * 获取token
//	 *
//	 * @param pk_corp
//	 * @return
//	 */
//	private String getToken(String pk_corp, SingleObjectBO sbo)
//			throws DZFWarpException {
//		if (1 == 1)
//			return "";
//		String strReturn = null;
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		UserVO[] uservos = (UserVO[]) sbo
//				.queryByCondition(
//						UserVO.class,
//						"nvl(dr,0)=0 and zfuser_code is not null and zf_refreshtoken is not null and pk_corp=?",
//						params);
//		if (uservos != null && uservos.length > 0) {
//			IZFTokenService zftokenservice = (IZFTokenService) SpringUtils
//					.getBean("zfoauth_tokenserv");
//			strReturn = zftokenservice.getZFToken(uservos[0].getUser_code());
//		}
//		return strReturn;
//	}

	public void checkBeforeProcessApprove(TaxReportVO reportvo,
			SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException {
//		super.checkBeforeProcessApprove(reportvo, sbo, corpvo);
		// 如果不是江苏省客户，审核后同时生成pdf
		// 汇算清缴不生成pdf
//		SQLParameter params = new SQLParameter();
//		if (!TaxRptConst.SB_ZLBH_SETTLEMENT.equals(reportvo.getSb_zlbh())) {
//			params = new SQLParameter();
//			params.addParam(reportvo.getPk_taxreport());
//			TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) sbo
//					.queryByCondition(TaxReportDetailVO.class,
//							"pk_taxreport=?", params);
//			createPdfFile(detailvos, reportvo, corpvo, sbo);
//			sbo.updateAry(detailvos);
//		}
	}

	/**
	 * 生成pdf文件
	 * 
	 * @param detailvos
	 * @throws DZFWarpException
	 */
	private void createPdfFile(TaxReportDetailVO[] detailvos,
							   TaxReportVO reportvo, CorpVO corpvo, CorpTaxVo taxvo, SingleObjectBO sbo)
			throws DZFWarpException {
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils
				.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		String sb_zlbh = "";
		Map hmMapValue = null;
		for (TaxReportDetailVO detailvo : detailvos) {
			if (detailvo.getSb_zlbh().equals(sb_zlbh) == false) {
				String strJson = readFileString(detailvo.getSpreadfile());
				hmMapValue = spreadtool.getMapValue(strJson);
				sb_zlbh = detailvo.getSb_zlbh();
			}
			if (detailvo.getPdffile() != null
					&& detailvo.getPdffile().trim().length() > 0) {
				File oldfile = new File(detailvo.getPdffile());
				if (oldfile.exists()) {
					oldfile.delete();
				}
			}
			TaxRptTempletVO templetvo = (TaxRptTempletVO) sbo
					.queryByPrimaryKey(TaxRptTempletVO.class,
							detailvo.getPk_taxrpttemplet());

			String pdffilename = TaxReportPath.taxReportPath + "pdffile/pdf"
					+ detailvo.getPk_taxreport() + detailvo.getReportcode()
					+ ".pdf";
			detailvo.setPdffile(pdffilename);
			File pdfFileOut = new File(pdffilename);
			spreadtool.fillPDFValue(templetvo.getPdftemplet(), reportvo,taxvo,
					getRptTempletPosVOs(detailvo.getPk_taxrpttemplet(), sbo),
					(HashMap) hmMapValue.get(detailvo.getReportname()),
					pdfFileOut, corpvo);

		}
	}

	private TaxRptTempletPosVO[] getRptTempletPosVOs(String pk_taxrpttemplet,
													 SingleObjectBO sbo) throws DZFWarpException {
		// 查询行列对照数据表
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxrpttemplet);
		return (TaxRptTempletPosVO[]) sbo.queryByCondition(
				TaxRptTempletPosVO.class, "nvl(dr,0)=0 and pk_taxrpttemplet=?",
				params);
	}

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
		HashMap<String, Object>  hmQCData = new HashMap<String, Object> ();
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());

		//调用初始化接口获取期初数据
		if ("on".equals(taxJstcConfig.service_switch)
				&& !StringUtil.isEmpty(corpvo.getVsoccrecode())
				&& !StringUtil.isEmpty(taxvo.getVstatetaxpwd())
				&& corpvo.getVsoccrecode().length() > 1
				&& (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh()))) {
			hmQCData = initTax(corpvo, taxvo,reportvo);
		}
		if (TaxRptConst.SB_ZLBHD1.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH_LOCAL_FUND_FEE.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH50101.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH50102.equals(reportvo.getSb_zlbh())) {
			getQcFromJsonFile(hmQCData, reportvo);
		}
		return hmQCData;
	}

	@Override
	public Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map objMapReport,
			SpreadTool spreadtool, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		if (!"on".equals(taxJstcConfig.service_switch)) {
			throw new BusinessException("申报接口未启用");
		}
		if (!TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH39806.equals(reportvo.getSb_zlbh())) {
			throw new BusinessException("暂不支持上报当前税种");
		}
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpVO.getPk_corp());
		String nsrsbh = corpVO.getVsoccrecode();
		String vstatetaxpwd = taxvo.getVstatetaxpwd();

		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号不能为空");
		}

		if (StringUtil.isEmpty(vstatetaxpwd)) {
			throw new BusinessException("纳税密码不能为空");
		}
		
		int isbzt_dm = Integer.parseInt(reportvo.getSbzt_dm());
		if (!(isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit
				|| isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
			throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能重复申报");
		}
		if (!"true".equals(taxJstcConfig.dev_mode)) {
			if (TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())
					|| TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())
					|| TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh())) {
				DZFDate date = new DZFDate(reportvo.getPeriod() + "-01");
				date = new DZFDate(DateUtils.getNextMonth(date.toDate().getTime()));
				// 财报属期校验
				List<String> codes = getInventoryCodes(DateUtils.getPeriod(date), corpVO,taxvo);
				if (!codes.contains(reportvo.getSb_zlbh())) {
					throw new BusinessException("你所申报的财务报表跟税局核定的不一致，请修改后再申报");
				}
			}
		}
		String userid = userVO == null ? reportvo.getCoperatorid() : userVO.getCuserid();
		// 扣费
		doCharge(corpVO, reportvo, userid);
//		Map<String, DZFDouble> qcdata = initTax(corpVO, reportvo);
		String lsh = reportvo.getRegion_extend1();
		if (StringUtil.isEmpty(lsh)) {
			// 重复申报时流水号不变
			lsh = UUID.randomUUID().toString().replaceAll("-", "");
		}
		
		String bw = constructPostData(corpVO, taxvo,reportvo, objMapReport,
				spreadtool, lsh);

		Map<String, String> params = new HashMap<String, String>();
		String encode = ZipUtil.zipEncode(bw, true);
		params.put("request", encode);
		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);
		
		String result = HttpUtil.parseRes(rmap.get("response"));
		
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		
		String status = rsJosn.getString("RESULT");
		String msg = rsJosn.getString("MSG");

		if (!"0000".equals(status)) {
			throw new BusinessException(msg);
		}
		
		// 记录申报流水号
		reportvo.setRegion_extend1(lsh);
		
		// 更新状态为已提交
		String sbzt_dm = "" + TaxRptConst.iSBZT_DM_Submitted;
		reportvo.setSbzt_dm(sbzt_dm);
		// 申报日期
		reportvo.setDoperatedate(new DZFDate());
		singleObjectBO.update(reportvo, new String[]{"region_extend1", "sbzt_osb", "sbzt_dm", "doperatedate"});

		String sql = "update ynt_taxreportdetail set sbzt_dm = ? where pk_corp = ? and  pk_taxreport = ? and nvl(dr, 0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(sbzt_dm);
		sp.addParam(reportvo.getPk_corp());
		sp.addParam(reportvo.getPk_taxreport());
		singleObjectBO.executeUpdate(sql, sp);
		
		return msg;
	}

	/**
	 * 构造JSON格式申报数据
	 * 
	 * @return
	 */
	private String constructPostData(CorpVO corpVO,CorpTaxVo taxvo, TaxReportVO reportvo,
			Map objMapReport, SpreadTool spreadtool,
			String lsh) {

		BaseRequestVO baseRequest = getBaseRequset(corpVO,taxvo);

		
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, String> params = new HashMap<String, String>();
		params.put("nsrsbh", corpVO.getVsoccrecode());
		params.put("skssqq", reportvo.getPeriodfrom());
		params.put("skssqz", reportvo.getPeriodto());
		params.put("sbzlid", getSbzlbh(reportvo.getSb_zlbh()));
		// 是否异常转办--票表比对失败后是否强制保存 Y为强制保存 S票表比对错误时返回错误信息不申报
		params.put("sfyczb", "S");

		params.put("lsh", lsh);
		boolean jsonString = false;
		// 提交
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_ZZSXGM_TJ");
			baseRequest.getBody().setSign("sb10102Submit");
		} else if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_ZZSYBNSR_TJ");
			baseRequest.getBody().setSign("sb10101Submit");
		} else if (TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_CWBB_XQY_YJ_TJ");
			baseRequest.getBody().setSign("sb29806Submit");
		} else if (TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_CWBB_YBQY_YJ_TJ");
			baseRequest.getBody().setSign("sb29801Submit");
		} else if (TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_CWBB_QY_YJ_TJ");
			baseRequest.getBody().setSign("sb29805Submit");
		} else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
			jsonString = true;
			baseRequest.setServiceid("FW_DZSWJ_QYSDSYJD_A_TJ");
			baseRequest.getBody().setSign("sb10412Submit");
		} else if (TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())) {
			jsonString = true;
			baseRequest.setServiceid("FW_DZSWJ_QYSDSYJD_B_TJ");
			baseRequest.getBody().setSign("sb10413Submit");
		} else if (TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_CWBB_YBQY_ND_TJ");
			baseRequest.getBody().setSign("sb39801Submit");
		} else if (TaxRptConst.SB_ZLBH39806.equals(reportvo.getSb_zlbh())) {
			baseRequest.setServiceid("FW_DZSWJ_CWBB_XQY_ND_TJ");
			baseRequest.getBody().setSign("sb39806Submit");
		}
		

		map.put("params", params);
//		Set<String> reports = getReportList(corpVO, reportvo);
		try {
			Class<?> vClass = getClassByTaxType(reportvo.getSb_zlbh());
			Object datas = vClass.newInstance();
			map.put("datas", datas);
			fillFields(datas, objMapReport, spreadtool);
			filterEmptyReport(reportvo, datas);
		} catch (Exception e) {
			log.error("上报失败", e);
			throw new BusinessException("上报失败");
		}

//		String ywbw = jsonString ? OFastJSON.toJSONStringAllString(map) : OFastJSON.toJSONString(map);
		String ywbw = JsonUtils.serialize(map);
		log.info(corpVO.getVsoccrecode() + "-申报数据:" + ywbw);
		baseRequest.getBody()
				.setYwbw(
						EncryptDecrypt.encode(ywbw,
								taxJstcConfig.app_secret));

//		return OFastJSON.toJSONString(baseRequest);
		return JsonUtils.serialize(baseRequest);
	}

	private BaseRequestVO getBaseRequset(CorpVO corpVO,CorpTaxVo taxvo) {
		BaseRequestVO base = new BaseRequestVO();
		base.setAsynserviceid("");
		base.setBwlx("json");
		base.setClientid(taxJstcConfig.clientid);

		String dealid = UUID.randomUUID().toString().replaceAll("-", "");
		base.setDealid(dealid);

		RequestBodyVO body = new RequestBodyVO();
		base.setBody(body);
		body.setAction(taxJstcConfig.action);
		body.setLogin_user(corpVO.getVsoccrecode());
		body.setLogin_pwd(EncryptDecrypt.encode(taxvo.getVstatetaxpwd(), taxJstcConfig.app_secret));

		return base;
	}

	private Class<?> getClassByTaxType(String type) {
		Class<?> cls = null;
		if (TaxRptConst.SB_ZLBH10102.equals(type)
				|| TaxRptConst.SB_ZLBH1010201.equals(type)) {
			cls = VATSmallRequest.class;
		} else if (TaxRptConst.SB_ZLBH10101.equals(type)) {
			cls = VATOrdinaryRequest.class;
		} else if (TaxRptConst.SB_ZLBHC1.equals(type)) {
			cls = FinancialSmallRequest.class;
		} else if (TaxRptConst.SB_ZLBHC2.equals(type)) {
			cls = FinancialOrdinaryRequest.class;
		} else if (TaxRptConst.SB_ZLBH29805.equals(type)) {
			cls = FinancialEnterpriseRequest.class;
		} else if (TaxRptConst.SB_ZLBH10412.equals(type)) {
			cls = IncomeTaxRequest.class;
		} else if (TaxRptConst.SB_ZLBH10413.equals(type)) {
			cls = com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b.IncomeTaxRequest.class;
		} else if (TaxRptConst.SB_ZLBH39801.equals(type)) {
			cls = com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary.FinancialOrdinaryRequest.class;
		} else if (TaxRptConst.SB_ZLBH39806.equals(type)) {
			cls = com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.small.FinancialSmallRequest.class;
		}
		
		return cls;
	}

	
	private void fillFields(Object datas, Map objMapReport,
			SpreadTool spreadtool) throws Exception {
		for (Field field : datas.getClass().getDeclaredFields()) {
			
			Class<?> fieldType = field.getType();
			boolean isArray = fieldType.isArray();
			
			if (isArray)
				fieldType = fieldType.getComponentType();
			
			TaxExcelPos clsAnno = fieldType
					.getAnnotation(TaxExcelPos.class);
			
			
			Object obj = null;
			if (clsAnno != null && !"".equals(clsAnno.reportname())) {
				if (isArray) {
					if (clsAnno != null) {
						List<Object> list = new ArrayList<Object>();
						if (clsAnno.rowBegin() > -1) {
							String reportname = clsAnno.reportname();
							int rowBegin = clsAnno.rowBegin();
							int rowEnd = clsAnno.rowEnd();
							int col = clsAnno.col();
							for (; rowBegin <= rowEnd; rowBegin++) {
								Object mainVal = spreadtool.getCellValue(
										objMapReport, reportname, rowBegin, col);
								if (mainVal != null
										&& !StringUtil.isEmpty(mainVal.toString())) {
									Object rowObj = getObjectByType(fieldType,
											reportname, objMapReport, spreadtool,
											rowBegin, null);
									if (rowObj == null) {
										continue;
									}
									list.add(rowObj);
								}
							}
						}
						obj = list.toArray((Object[]) Array.newInstance(fieldType,
								0));
					}
				} else {
					if (clsAnno != null) {
						String reportname = clsAnno.reportname();
						obj = getObjectByType(fieldType, reportname, objMapReport,
								spreadtool, null, null);
					}
				}
			
			} else {
				if (isArray) {
					// 数组类型，没有注解，只有一个对象
					obj = Array.newInstance(fieldType, 1);
					Object obj1 = fieldType.newInstance();
					((Object[]) obj)[0] = obj1;
					fillFields(obj1, objMapReport, spreadtool);
				} else {
					obj = fieldType.newInstance();
					fillFields(obj, objMapReport, spreadtool);
				}
			}
			
			String fname = field.getName();
			Method setMtd = datas.getClass().getMethod(
					"set" + fname.substring(0, 1).toUpperCase()
					+ fname.substring(1), field.getType());
			setMtd.invoke(datas, obj);
		}
	}
	
	/**
	 * 根据类从报表取数生成对象
	 * 
	 * @param fieldType
	 * @param reportname
	 * @param objMapReport
	 * @param spreadtool
	 * @param row
	 *            为空则从字段注解取数
	 * @param col
	 *            为空则从字段注解取数
	 * @return
	 * @throws Exception
	 */
	private Object getObjectByType(Class<?> fieldType, String reportname,
			Map objMapReport, SpreadTool spreadtool, Integer row, Integer col)
			throws Exception {
		Object rowObj = fieldType.newInstance();
		for (Field field : fieldType.getDeclaredFields()) {
			String fname = field.getName();
			TaxExcelPos fieldAnno = field.getAnnotation(TaxExcelPos.class);
			// 没有注解
//			if (fieldAnno == null) {
//				continue;
//			}
			Object val = fieldAnno == null ? null : spreadtool.getCellValue(objMapReport, reportname,
					row == null ? fieldAnno.row() : row,
					col == null ? fieldAnno.col() : col);
			// 单元格为空
			if (val == null) {
				val = "";
			}
			String valStr = val.toString();
			if (field.getType().isAssignableFrom(String.class)) {
				if (fieldAnno != null) {
					String[] strArray = valStr.split("\\||_|-");
					int index = -1;
					if (fieldAnno.splitIndex() > index) {
						index = fieldAnno.splitIndex();
					} else if (fieldAnno.isCode()) {
						index = 0;
					} else if (fieldAnno.isName()) {
						index = 1;
					}
					if (index > -1 && index < strArray.length) {
						valStr = strArray[index].trim();
					}
				}
				val = valStr;
			} else if (field.getType().isAssignableFrom(DZFDouble.class)) {
				if (valStr.indexOf("——") > -1) {
//					valStr = null;
					val = DZFDouble.ZERO_DBL;
				} else if (valStr.indexOf("%") > -1) {
					valStr = valStr.substring(0, valStr.indexOf("%"));
					val = new DZFDouble(valStr).div(100);
				} else {
					val = new DZFDouble(valStr);
				}
			} else if (field.getType().isAssignableFrom(Double.class)) {
				if (valStr.indexOf("——") > -1) {
					val = new Double("0");
				} else if (valStr.indexOf("%") > -1) {
					valStr = valStr.substring(0, valStr.indexOf("%"));
					val = new Double(valStr) / 100;
				} else {
					if (StringUtil.isEmpty(valStr)) {
						valStr = "0";
					}
					val = new Double(valStr);
				}
			} else if (field.getType().isAssignableFrom(DZFDate.class)) {
				if (!StringUtil.isEmpty(valStr)) {
					val = new DZFDate(valStr);
				} else {
					val = null;
				}
			}

//			if (fieldAnno.isTotal() && qcdata != null &&  qcdata.containsKey(fname)) {
//				val = ((DZFDouble) val).add(qcdata.get(fname));
//			}
			Method setMtd = fieldType.getMethod("set"
					+ fname.substring(0, 1).toUpperCase() + fname.substring(1),
					field.getType());
			setMtd.invoke(rowObj, val);
		}

		return rowObj;
	}

	/**
	 * 初始化，获取期初数据
	 * 
	 * @param corpVO
	 * @param reportvo
	 * @return
	 */
	private HashMap<String, Object> initTax(CorpVO corpVO, CorpTaxVo taxvo,TaxReportVO reportvo) {
		BaseRequestVO baseReq = getBaseRequset(corpVO,taxvo);
		
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			baseReq.setServiceid("FW_DZSWJ_ZZSXGM_CSH");
			baseReq.getBody().setSign("sb10102InitService");
		} else if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			baseReq.setServiceid("FW_DZSWJ_ZZSYBNSR_CSH");
			baseReq.getBody().setSign("sb10101InitService");
		} else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
			baseReq.setServiceid("FW_DZSWJ_QYSDSYJD_A_CSH");
			baseReq.getBody().setSign("sb10412InitService");
		} else if (TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())) {
			baseReq.setServiceid("FW_DZSWJ_QYSDSYJD_B_CSH");
			baseReq.getBody().setSign("sb10413InitService");
		} else if (TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh())) {
			baseReq.setServiceid("FW_DZSWJ_CWBB_YBQY_ND_CSH");
			baseReq.getBody().setSign("sb39801InitService");
		}
		String lsh = reportvo.getRegion_extend1();
		baseReq.getBody().setYwbw(getInitParams(corpVO, reportvo));

		HashMap<String, String> params = new HashMap<String, String>();

//		String bw = OFastJSON.toJSONString(baseReq);
		String bw = JsonUtils.serialize(baseReq);

		String encode = ZipUtil.zipEncode(bw, true);
		params.put("request", encode);

		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);

		String result = HttpUtil.parseRes(rmap.get("response"));
		log.info(corpVO.getVsoccrecode() + "-initData:" + result);
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		String status = rsJosn.getString("RESULT");

		HashMap<String, Object> initData = new HashMap<String, Object>();
		if ("0000".equals(status)) {
			JSONObject data = (JSONObject) rsJosn.get("DATA");
			parseInitData(initData, data, reportvo);
		} else {
			throw new BusinessException((String) rsJosn.get("MSG"));
		}
		
		// 记录流水号
		if (StringUtil.isEmpty(lsh)) {
			singleObjectBO.update(reportvo, new String[]{"region_extend1"});
		}
		
		return initData;
	}

	/**
	 * 解析期初数据为Map
	 * 
	 * @param data
	 * @param reportvo
	 * @return
	 */
	private void parseInitData(Map<String, Object> initData,  JSONObject data,
			TaxReportVO reportvo) {

		InitParse intParse = null;
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			intParse = new VATSmallInit();
		} else if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			intParse = new VATOrdinaryInit();
		} else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
			intParse = new IncomeTaxInit();
		} else if (TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())) {
			intParse = new com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b.IncomeTaxInit();
		} else if (TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh())) {
			intParse = new FinancialOrdinaryInit();
		} else {
			return;
		}

		String[] fields = intParse.getFields();
		for (String voName : fields) {
			Object vo = data.get(voName);
			if ("jmxzList".equals(voName) && vo instanceof JSONArray) {
				dealJmxzList(initData, (JSONArray) vo);
				continue;
			}
			if (vo instanceof JSONObject) {
				//找到末级对象或数组
				while (true) {
					Object obj = ((JSONObject) vo).values().iterator().next();
					if (obj instanceof JSONObject) {
						vo = obj;
						continue;
					}
					if (obj instanceof JSONArray) {
						vo = obj;
					}
					break;
				}
				String indexName = intParse.getIndexField(voName);
				if (vo instanceof JSONArray) {
					JSONArray gridLb = (JSONArray) vo;
					if ("rawType".equals(indexName)) {
						Map<String, String> nameMap = intParse
								.getNameMap(voName + "--0");
						// 直接存放List
						int size = gridLb.size();
						Class<?> type = intParse.getFieldType(voName);
						Object[] ary = (Object[]) Array.newInstance(type, size);
						try {
							ary = (Object[]) DzfTypeUtils.cast(gridLb,
									FieldMapping.getFieldMapping((SuperVO) type.newInstance()),
									ary.getClass(), JSONConvtoJAVA.getParserConfig());
						} catch (Exception e) {
							log.error("JSONArray转换失败", e);
						}
						for (Entry<String, String> entry : nameMap
								.entrySet()) {
							initData.put(entry.getKey(), Arrays.asList(ary));
						}
					} else {
						for (Object object : gridLb) {
							JSONObject qcVO = (JSONObject) object;
							Map<String, String> nameMap = intParse
									.getNameMap(voName + "--"
											+ (String) qcVO.get(indexName));
							if (nameMap != null) {
								for (Entry<String, String> entry : nameMap
										.entrySet()) {
									putInitMapValue(initData, entry.getKey(),
											(String) qcVO.get(entry.getValue()));
								}
							}
							
						}
					}
				} else {
					JSONObject qcVO = (JSONObject) vo;
					Map<String, String> nameMap = intParse.getNameMap(voName
							+ "--" + (String) qcVO.get(indexName));
					if (nameMap != null) {
						for (Entry<String, String> entry : nameMap.entrySet()) {
							putInitMapValue(initData, entry.getKey(),
									(String) qcVO.get(entry.getValue()));
						}
					}
				}
			}
		}

	}
	
	private void putInitMapValue(Map<String, Object> initData, String key,
			String value) {
		DZFDouble doubleVal = new DZFDouble(value);
		if (initData.containsKey(key)) {
			doubleVal = doubleVal.add((DZFDouble) initData.get(key));
		}
		initData.put(key, doubleVal);

	}

	private String getInitParams(CorpVO corpVO, TaxReportVO reportvo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("nsrsbh", corpVO.getVsoccrecode());
		params.put("skssqq", reportvo.getPeriodfrom());
		params.put("skssqz", reportvo.getPeriodto());
		params.put("sbzlid", getSbzlbh(reportvo.getSb_zlbh()));

		String yzpzzlDm = null;
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			yzpzzlDm = "BDA0610611";
		} else if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			yzpzzlDm = "BDA0610606";
		} else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
			yzpzzlDm = "BDA0611033";
		} else if (TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())) {
			yzpzzlDm = "BDA0611038";
		}
		params.put("yzpzzlDm", yzpzzlDm);
		
		String uuid = null;
		if (StringUtil.isEmpty(reportvo.getRegion_extend1())) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			reportvo.setRegion_extend1(uuid);
		} else {
			uuid = reportvo.getRegion_extend1();
		}
		
		params.put("sbuuid", uuid);

//		String ywbw = OFastJSON.toJSONString(params);
		String ywbw = JsonUtils.serialize(params);

		ywbw = EncryptDecrypt.encode(ywbw, taxJstcConfig.app_secret);

		return ywbw;
	}
	
	@Override
	public void getDeclareStatus(CorpVO corpvo, CorpTaxVo taxvo,TaxReportVO reportvo)
			throws DZFWarpException {
		if (!"on".equals(taxJstcConfig.service_switch)) {
			return;
		}
		if (StringUtil.isEmpty(corpvo.getVsoccrecode())
				|| StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
			return;
		}
		
		if (!TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH39801.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH39806.equals(reportvo.getSb_zlbh())) {
			return;
		}
		
		String lsh =  reportvo.getRegion_extend1();
		boolean isSaveFile = !StringUtil.isEmpty(reportvo.getSpreadfile());
		if (!isSaveFile || lsh == null || reportvo.getSbzt_dm() != null
				&& Integer.valueOf(reportvo.getSbzt_dm()).intValue() == TaxRptConst.iSBZT_DM_UnSubmit) {
			// 未填写、未提交、无流水号，调用已申报未申报接口查询状态
			String status = queryReportStatusBeforeInit(corpvo, taxvo,reportvo);
			String sbzt_dm = "已申报".equals(status) ? (TaxRptConst.iSBZT_DM_ReportSuccess + "")
					: (TaxRptConst.iSBZT_DM_UnSubmit + "");
			reportvo.setSbzt_dm(sbzt_dm);
		} else {
			BaseRequestVO baseRequest = getBaseRequset(corpvo,taxvo);
			
			baseRequest.setServiceid("FW_DZSWJ_DYBBSBQKCX");
			baseRequest.getBody().setSign("querySbqk");
			
			String ywbw = EncryptDecrypt.encode("{\"LSH\":\"''" + lsh + "''\"}", taxJstcConfig.app_secret);
			
			baseRequest.getBody().setYwbw(ywbw);
			
//			String bw = OFastJSON.toJSONString(baseRequest);
			String bw = JsonUtils.serialize(baseRequest);

			String encode = ZipUtil.zipEncode(bw, true);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("request", encode);
			
			Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
					params);
			
			String result = HttpUtil.parseRes(rmap.get("response"));
			JSONObject rsJosn = (JSONObject) JSON.parse(result);
			String status = rsJosn.getString("RESULT");
			
			if (!"0000".equals(status)) {
				log.error("更新申报状态失败:" + rsJosn);
//				throw new BusinessException((String) rsJosn.get("MSG"));
				return;
			}

			JSONArray dataArray = (JSONArray) rsJosn.get("DATA");
			JSONObject data = (JSONObject) dataArray.get(0);
			String sbzt_dm = data.getString("SBZT_DM");
			//申报成功记录凭证序号
			if (TaxRptConst.iSBZT_DM_ReportSuccess == Integer.valueOf(sbzt_dm)) {
				String yzpzxh = data.getString("YZPZXH");
				reportvo.setRegion_extend2(yzpzxh);
				BigDecimal tax = data.getBigDecimal("YBTSE");
				reportvo.setTaxmny(new DZFDouble(tax));
			}
			String remark = data.getString("SB_CWXX");
			// 票表比对错误信息
			Object pbcw = data.get("PBCWXX");
			if (pbcw instanceof JSONArray) {
				JSONArray pbcwList = (JSONArray) pbcw;
				if (pbcwList.size() > 0) {
					StringBuilder sb = new StringBuilder();
					if (remark != null) {
						sb.append(remark).append("。");
					}
					for (Object obj : pbcwList) {
						JSONObject cwxx = (JSONObject) obj;
						sb.append(cwxx.getString("BDBFJGBCNR"));
					}
					remark = sb.toString();
				}
			}
			reportvo.setRemark(remark);
			//更新申报状态
			reportvo.setSbzt_dm(sbzt_dm);
		}

		TaxReportDetailVO[] details = (TaxReportDetailVO[]) reportvo.getChildren();
		if (details != null) {
			for (TaxReportDetailVO taxReportDetailVO : details) {
				taxReportDetailVO.setSbzt_dm(reportvo.getSbzt_dm());
			}
		}

		updateSBZTJS(reportvo);
	}
	
	private void updateSBZTJS(TaxReportVO reportvo){
		singleObjectBO.update(reportvo, new String[]{"sbzt_dm", "remark", "region_extend2", "taxmny"});
		String sql = "update ynt_taxreportdetail set sbzt_dm = ? where pk_corp = ? and  pk_taxreport = ? and nvl(dr, 0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getSbzt_dm());
		sp.addParam(reportvo.getPk_corp());
		sp.addParam(reportvo.getPk_taxreport());
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public void processObsoleteDeclare(CorpVO corpvo, TaxReportVO reportvo)
			throws DZFWarpException {
		
		if (!"on".equals(taxJstcConfig.service_switch)) {
			return;
		}
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
		if (StringUtil.isEmpty(corpvo.getVsoccrecode())
				|| StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
			return;
		}
		
		if (Integer.valueOf(reportvo.getSbzt_dm()) != TaxRptConst.iSBZT_DM_ReportSuccess) {
			throw new BusinessException("非申报成功状态，不能申报作废");
		}
		
		String zflag = "other";
		String yzpz = reportvo.getRegion_extend2();
		if ("C".equals(reportvo.getZsxm_dm())
				|| "39".equals(reportvo.getZsxm_dm())) {
			// 财报标志
			zflag = "cwbb";
			yzpz = reportvo.getRegion_extend1();
		}
		
		if (StringUtil.isEmpty(yzpz)) {
			throw new BusinessException("非在本系统申报，不能申报作废");
		}
		
		BaseRequestVO baseRequest = getBaseRequset(corpvo,taxvo);

		baseRequest.setServiceid("FW_DZSWJ_SBZF");
		baseRequest.getBody().setSign("sbZfService");
		
		String ywbw = EncryptDecrypt.encode("{\"YZPZXH\":\""
				+ yzpz + "\", \"ZFLAG\":\"" + zflag + "\"}",
				taxJstcConfig.app_secret);
		
		baseRequest.getBody().setYwbw(ywbw);
		
//		String bw = OFastJSON.toJSONString(baseRequest);
		String bw = JsonUtils.serialize(baseRequest);

		String encode = ZipUtil.zipEncode(bw, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("request", encode);

		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);
		
		String result = HttpUtil.parseRes(rmap.get("response"));
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		String status = rsJosn.getString("RESULT");

		if (!"0000".equals(status)) {
			log.error("作废失败:" + result);
			String msg = rsJosn.getString("MSG");
			Object data = rsJosn.get("DATA");
			if (data != null && data instanceof JSONObject) {
				JSONObject dataJson = (JSONObject) data;
				String ycxx = dataJson.getString("YCXX");
				if (!StringUtil.isEmpty(ycxx)) {
					msg += ":" + ycxx;
				}
			}
			throw new BusinessException(msg);
		}
		
		//更新申报状态
		reportvo.setSbzt_dm(TaxRptConst.iSBZT_DM_ReportCancel + "");
		singleObjectBO.update(reportvo, new String[]{"sbzt_dm"});
		
	}
	
	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();
		if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10101_jiangsu.saCheckCondition;
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
		} else if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10102_jiangsu.saCheckCondition;
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
		return listCondition.toArray(new String[0]);
	}
	
	@Override
	public String checkReportData(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		SpreadTool spreadtool = new SpreadTool();
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
			String rpt1 = "增值税纳税申报表（小规模纳税人适用）附列资料";
			String rpt2 = "增值税纳税申报表";
			String rpt3 = "增值税减免税申报明细表";
			DZFDouble val1 = null;
			DZFDouble val2 = null;
			if (listReportName.contains(rpt1) && listReportName.contains(rpt2)) {
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 11, 3));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 6, 4));
				if (val1.doubleValue() != 0 && val1.compareTo(val2) != 0) {
					errmsg += "主表服务、不动产和无形资产应征增值税不含税销售额（3%征收率）本期数《增值税纳税申报表（适用于增值税小规模纳税人）附列资料》第8栏的“应税行为（3%征收率）不含税销售额”<br>";
				}
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 19, 3));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 9, 4));
				if (val1.doubleValue() != 0 && val1.compareTo(val2) != 0) {
					errmsg += "主表服务、不动产和无形资产应征增值税不含税销售额（5%征收率）不等于本期数《增值税纳税申报表（适用于增值税小规模纳税人）附列资料》第16栏的“应税行为（5%征收率）不含税销售额”<br>";
				}
				
			}
			if (listReportName.contains(rpt2)) {
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 16, 3));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 15, 3));
				if (val1.doubleValue() != 0 && val2.doubleValue() != 0) {
					errmsg += "主表第11栏“未达起征点销售额”和主表第10栏“小微企业免税销售额“不能同时填写。<br>";
				} else {
					val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 16, 4));
					val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 15, 4));
					if (val1.doubleValue() != 0 && val2.doubleValue() != 0) {
						errmsg += "主表第11栏“未达起征点销售额”和主表第10栏“小微企业免税销售额“不能同时填写。<br>";
					}
				}
			}
			if (listReportName.contains(rpt3) && listReportName.contains(rpt2)) {
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 22, 4));
				val1 = val1.add(getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 22, 5)));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt3, 7, 6));
				if (val1.compareTo(val2) != 0) {
					errmsg += "增值税纳税申报表（小规模纳税人适用）》本期应纳税减征额货物及劳务本月加服务、不动产和无形资产本月之和"
							+ "应等于《增值税减免税申报明细表》“一、减税项目”第1栏“本期实际抵减税额”<br>";
				}

				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 17, 3));
				val1 = val1.add(getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 17, 4)));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt3, 16, 3));
				if (val1.compareTo(val2) != 0) {
					errmsg += "主表第12行其他免税销售额"
							+ "应等于《增值税减免税申报明细表》免税项目第1列免征增值税项目销售额合计值<br>";
				}
			}
			
//			errmsg +=checkForSB_ZLBH10102_js(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String rpt1 = "增值税纳税申报表附列资料（一）";
			/*if (listReportName.contains(rpt1)) {
				DZFDouble val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 7, 12));
				DZFDouble val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 9, 12));
				DZFDouble val3 = null;
				DZFDouble val4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 10, 12));
				if (val1.doubleValue() > 0 && val2.doubleValue() > 0) {
					val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 13, 12));
					if (val1.add(val2).add(val4).compareTo(val3) < 0) {
						errmsg += "增值税纳税申报表附列资料一第6行第9列数据应小于等于本表第1、3、4a行第9列之和<br>";
					}
				}
				
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 7, 13));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 9, 13));
				val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 10, 13));
				if (val1.doubleValue() > 0 && val2.doubleValue() > 0
						&& val3.doubleValue() > 0) {
					val4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 13, 13));
					if (val1.add(val2).add(val3).compareTo(val4) < 0) {
						errmsg += "增值税纳税申报表附列资料一第6行第10列数据应小于等于本表第1、3、4a行第10列之和<br>";
					}
				}
				
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 8, 12));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 11, 12));
				val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 12, 12));
				if (val1.doubleValue() > 0 && val2.doubleValue() > 0
						&& val3.doubleValue() > 0) {
					val4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 14, 12));
					if (val1.add(val2).add(val3).compareTo(val4) < 0) {
						errmsg += "增值税纳税申报表附列资料一第7行第9列数据应小于等于本表第2、4b、5行第9列之和<br>";
					}
				}
				
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 8, 13));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 11, 13));
				val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 12, 13));
				if (val1.doubleValue() > 0 && val2.doubleValue() > 0
						&& val3.doubleValue() > 0) {
					val4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 14, 13));
					if (val1.add(val2).add(val3).compareTo(val4) < 0) {
						errmsg += "增值税纳税申报表附列资料一第7行第10列数据应小于等于本表第2、4b、5行第10列之和<br>";
					}
				}
				
				val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 8, 17));
				val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 11, 17));
				val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 12, 17));
				if (val1.doubleValue() > 0 && val2.doubleValue() > 0
						&& val3.doubleValue() > 0) {
					val4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 14, 17));
					if (val1.add(val2).add(val3).compareTo(val4) < 0) {
						errmsg += "增值税纳税申报表附列资料一第7行第14列数据应小于等于本表第2、4b、5行第14列之和<br>";
					}
				}
				
			}*/
			String rpt2 = "增值税纳税申报表附列资料（三）";
			if (listReportName.contains(rpt2)) {
				for (int i = 0; i <= 7; i++) {
					DZFDouble val1 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 7 + i, 2));
					DZFDouble val2 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 7 + i, 5));
					DZFDouble val3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, 7 + i, 6));
					if (val1.doubleValue() > 0) {
						if (val3.compareTo(val1) > 0 || val3.compareTo(val2) > 0) {
							errmsg += "增值税纳税申报表附列资料三第" + (i + 1) + "栏第5列数据应小于等于本表第1列数据，并且本表第5列数据应小于等于本表第4列数据<br>";
						}
					} else if (val1.doubleValue() < 0) {
						if (val3.compareTo(val2) > 0) {
							errmsg += "增值税纳税申报表附列资料三第" + (i + 1) + "栏第5列数据应小于等于本表第4列数据<br>";
						}
					}
					
				}
			}
		} else if (TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())) {
			String rpt1 = "资产负债表";
			if (listReportName.contains(rpt1)) {
				if (!getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 35, 2))
						.equals(getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 35, 6)))
						|| !getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 35, 3))
						.equals(getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 35, 7)))) {
					errmsg += "资产负债表-资产总计应等于负债和所有者权益（或股东权益）总计<br>";
				}
			}
		} else if (TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())) {
			String rpt1 = "资产负债表";
			if (listReportName.contains(rpt1)) {
				if (!getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 44, 2))
						.equals(getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 44, 6)))
						|| !getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 44, 3))
						.equals(getCellNumber(spreadtool.getCellValue(mapJson, rpt1, 44, 7)))) {
					errmsg += "资产负债表-资产总计应等于负债和所有者权益（或股东权益）总计<br>";
				}
			}
		} else if (TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())) {
			String mainTable = "A200000所得税月(季)度预缴纳税申报表";
			if (listReportName.contains(mainTable)) {
				DZFDouble r11 = getCellNumber(spreadtool.getCellValue(mapJson, mainTable, 18, 8));
				DZFDouble r12 = getCellNumber(spreadtool.getCellValue(mapJson, mainTable, 19, 8));
				if (r11.compareTo(r12) < 0 || r12.compareTo(DZFDouble.ZERO_DBL) < 0) {
					errmsg += "主表第12行应<=主表第11行应纳所得税额本年累计金额，且第12行>=0。<br>";
				}
			}
			String freeTable = "A201010免税收入、减计收入、所得减免等优惠明细表";
			if (listReportName.contains(freeTable)) {
				DZFDouble total = getCellNumber(spreadtool.getCellValue(mapJson, freeTable, 7, 5));
				DZFDouble subTotal = DZFDouble.ZERO_DBL;
				for (int i = 8; i <= 11; i++) {
					subTotal = subTotal
							.add(getCellNumber(spreadtool.getCellValue(mapJson, freeTable, i, 5)));
				}
				if (total.compareTo(subTotal) < 0) {
					errmsg += "A201010免税收入、减计收入、所得减免等优惠明细表第3行应>=第4+5+6+7行<br>";
				}

				total = getCellNumber(spreadtool.getCellValue(mapJson, freeTable, 39, 5));
				subTotal = getCellNumber(spreadtool.getCellValue(mapJson, freeTable, 40, 5));
				if (total.compareTo(subTotal) < 0) {
					errmsg += "A201010免税收入、减计收入、所得减免等优惠明细表第33行应>=第33.1行<br>";
				}
			}
			String rpt2 = "A201020固定资产加速折旧(扣除)优惠明细表";
			if (listReportName.contains(rpt2)) {
				for (int i = 6; i < 9; i++) {
					DZFDouble col3 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, i, 4));
					DZFDouble col4 = getCellNumber(spreadtool.getCellValue(mapJson, rpt2, i, 5));
					if (col4.doubleValue() > 0 && col3.doubleValue() <= 0) {
						errmsg += "《A201020固定资产加速折旧（扣除）优惠明细表》当第1、2、3行第4列＞0时，相应行次第3列必须＞0<br>";
						break;
					}
				}
			}
			
			errmsg += checkForSB_ZLBH10412(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh()) && reportvo.getPeriodtype() == 1) {
			String rpt1 = "主表";
			if (listReportName.contains(rpt1)) {
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 30, 8))) {//小型微利企业
					errmsg += "纳税人必须填写是否小型微利企业<br>";
				}
				if (getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 28, 8)).doubleValue() <= 0) {
					errmsg += "纳税人必须填写季末从业人数，且人数必须大于0且为整数<br>";
				}

				if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 29, 3)).doubleValue() < 0) {
					errmsg += "纳税人必须填写季初资产总额（万元），且季初资产总额必须大于等于0<br>";
				}
				if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 29, 8)).doubleValue() < 0) {
					errmsg += "纳税人必须填写季末资产总额（万元），且季末资产总额必须大于等于0<br>";
				}
			}
//			errmsg = checkForSB_ZLBH10413(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		}
		return errmsg;
	}
	
	
	@Override
	public String checkReportDataWarning(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String warningmsg = "";
		if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				|| TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {	
//			warningmsg = checkForSB_ZLBH10102_js(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		}
		return warningmsg;
	}


	private String checkForSB_ZLBH10102_js(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
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
//			YntCpaccountVO[] accountVO  = AccountCache.getInstance().get(null, corpvo.getPk_corp());
			YntCpaccountVO[] accountVO = accountService.queryByPk(corpvo.getPk_corp());
					// E7+F7+F10 6行4列和5列 9行5列
			DZFDouble dkfp = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 6, 3))
					.add(getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 6, 4)))
					.add(getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 9, 4)));
			DZFDouble v3 = DZFDouble.ZERO_DBL;
			if (reportvo.getPeriodtype() == PeriodType.jidureport) {
				v3 = getDzfDouble(spreadtool.getFormulaValue(null,
						"glamtoccr4(\"" + spxssr + "+" + tglwsr
								+ "\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\",\"2\",\"\")",
						null, corpvo, reportvo, accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
			} else if (reportvo.getPeriodtype() == PeriodType.monthreport) {// 月报
				v3 = getDzfDouble(spreadtool.getFormulaValue(null,
						"glamtoccr2(\"" + spxssr + "+" + tglwsr
								+ "\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\",\"2\",\"\")",
						null, corpvo, reportvo, accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
			}

			if (dkfp.doubleValue() != 0) {
				DZFDouble d1 = dkfp.setScale(2, DZFDouble.ROUND_HALF_UP);

				if (d1.equals(v3) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
							+ ">  从局端获取的税务机关代开的增值税专用发票不含税销售额["+d1.toString()+"]与您账中的专票销售["+ v3.toString()+"]不一致，请您检查下凭证!";
				}
			}
		}
		return errmsg;
	}
	
	/**
	 * 出表规则
	 * @param corpVO
	 * @param reportvo
	 * @return
	 */
	private Set<String> getReportList(CorpVO corpVO, CorpTaxVo taxvo,TaxReportVO reportvo) {
		Set<String> reports = new HashSet<String>();
		
		if (!TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10412.equals(reportvo.getSb_zlbh())
				&& !TaxRptConst.SB_ZLBH10413.equals(reportvo.getSb_zlbh())) {
			return reports;
		}
		
		BaseRequestVO baseReq = getBaseRequset(corpVO,taxvo);

		baseReq.setServiceid("FW_DZSWJ_CBGZCX");
		baseReq.getBody().setSign("querycbgzService");

		Map<String, String> map = new HashMap<String, String>();
		map.put("nsrsbh", corpVO.getVsoccrecode());
		map.put("skssqq", reportvo.getPeriodfrom());
		map.put("skssqz", reportvo.getPeriodto());
		map.put("sbzlid", getSbzlbh(reportvo.getSb_zlbh()));
		
//		String ywbw = OFastJSON.toJSONString(map);
		String ywbw = JsonUtils.serialize(map);

		ywbw = EncryptDecrypt.encode(ywbw, taxJstcConfig.app_secret);
		baseReq.getBody().setYwbw(ywbw);

		HashMap<String, String> params = new HashMap<String, String>();

//		String bw = OFastJSON.toJSONString(baseReq);
		String bw = JsonUtils.serialize(baseReq);

		String encode = ZipUtil.zipEncode(bw, true);
		params.put("request", encode);

		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);

		String result = HttpUtil.parseRes(rmap.get("response"));
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		String status = rsJosn.getString("RESULT");

		if ("0000".equals(status)) {
			JSONArray data = (JSONArray) rsJosn.get("DATA");
			for (Object object : data) {
				reports.add((String) object);
			}
		} else {
			log.error(rsJosn.getString("MSG"));
//			throw new BusinessException((String) rsJosn.get("MSG"));
		}
		return reports;
	}
	
	private DZFDouble getCellNumber(Object val) {
		DZFDouble numberVal = null;
		String valStr = null;
		try {
			if (val != null) {
				valStr = val.toString().replace("——", "");
			}
			numberVal = new DZFDouble(valStr, 2);
		} catch (Exception e) {
			numberVal = DZFDouble.ZERO_DBL;
		}
		return numberVal;
	}
	
	/**
	 * 调用已申报未申报接口获取申报状态
	 * 
	 * @param corpVO
	 * @param reportvo
	 * @return "已申报"|"未申报"|null
	 */
	private String queryReportStatusBeforeInit (CorpVO corpVO,CorpTaxVo taxvo, TaxReportVO reportvo) {
		BaseRequestVO baseReq = getBaseRequset(corpVO,taxvo);
		baseReq.setServiceid("FW_DZSWJ_SBQKCX");
		baseReq.getBody().setSign("queryYsbwsbService");

		String period = reportvo.getPeriodto().substring(0, 7);
		period =new DZFDate(DateUtils.getNextMonth(
				DateUtils.getPeriodStartDate(period)
				.getMillis())).toString().substring(0, 7);
		Map<String, String> map = new HashMap<String, String>();
		map.put("NSRSBH", corpVO.getVsoccrecode());
		map.put("ND", period.substring(0, 4));
		map.put("YF", period.substring(5, 7));
		
//		String ywbw = OFastJSON.toJSONString(map);
		String ywbw = JsonUtils.serialize(map);

		ywbw = EncryptDecrypt.encode(ywbw, taxJstcConfig.app_secret);
		baseReq.getBody().setYwbw(ywbw);

		HashMap<String, String> params = new HashMap<String, String>();

//		String bw = OFastJSON.toJSONString(baseReq);
		String bw = JsonUtils.serialize(baseReq);

		String encode = ZipUtil.zipEncode(bw, true);
		params.put("request", encode);

		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				params);

		String result = HttpUtil.parseRes(rmap.get("response"));
		if (StringUtil.isEmpty(result)) {
			return null;
		}
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		String status = rsJosn.getString("RESULT");
		if ("0000".equals(status)) {
			String sb_zlbh = getSbzlbh(reportvo.getSb_zlbh());
			JSONArray data = (JSONArray) rsJosn.get("DATA");
			for (Object object : data) {
				JSONObject jsonObj = (JSONObject) object;
				String code = jsonObj.getString("SB_ZLBH");
				if (sb_zlbh.equals(code)) {
					return jsonObj.getString("SBZT");
				}
			}
		} else {
//			throw new BusinessException((String) rsJosn.get("MSG"));
			return null;
		}
		return null;
	}
	
	
//	/**
//	 * 江苏地区财报为月报
//	 */
//	@Override
//	protected void addFinReport(CorpVO corpvo, String yearmonth, List<TaxTypeListDetailVO> list) {
//		TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//		if(TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())){//2003 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC1);
//		}else if(TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())){//2007 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC2);
//		}
//		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//		detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//		detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//		detailvo.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//		detailvo.setPeriodtype(0);//月报
//		if(StringUtil.isEmpty(detailvo.getSb_zlbh()))
//			return;
//		list.add(detailvo);
//		
//		boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
//		if (showYearInTax) {
//			int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
//			if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {
//				TaxTypeListDetailVO finTax = new TaxTypeListDetailVO();
//				finTax.setSb_zlbh(TaxRptConst.SB_ZLBH39806);//小企业财报年报
//				finTax.setPeriodfrom(year + "-01-01");
//				finTax.setPeriodto(year + "-12-31");
//				finTax.setZsxm_dm("C"); // 财报
//				finTax.setPeriodtype(2); // 年报
//				list.add(finTax);
//			} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {
//				TaxTypeListDetailVO finTax = new TaxTypeListDetailVO();
//				finTax.setSb_zlbh(TaxRptConst.SB_ZLBH39801);//一般企业财报年报
//				finTax.setPeriodfrom(year + "-01-01");
//				finTax.setPeriodto(year + "-12-31");
//				finTax.setZsxm_dm("C"); // 财报
//				finTax.setPeriodtype(2); // 年报
//				list.add(finTax);
//			}
//		}
//	}
	
	// 地税
//	private void addLocalTax(CorpVO corpvo, String yearmonth, List<TaxTypeListDetailVO> list) {
//		TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("一般纳税人")) {
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH50101);
//			String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//			detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo.setZsxm_dm("80");
//			detailvo.setPeriodtype(0);
//		} else {
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH50102);
//			detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo.setZsxm_dm("80"); // 代征地税
//			detailvo.setPeriodtype(1); // 季报
//		}
//		list.add(detailvo);
//	}
	// 企业所得税月报
//	@Override
//	protected void addOtherReport(CorpVO corpvo, String yearmonth,
//			List<TaxTypeListDetailVO> list) {
//		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//		String periodBegin = DateUtils.getPeriodStartDate(sQueryPeriod).toString();
//		String periodEnd = DateUtils.getPeriodEndDate(sQueryPeriod).toString();
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			TaxTypeListDetailVO addTax = new TaxTypeListDetailVO();
//			addTax.setSb_zlbh("1010201");
//			addTax.setPeriodfrom(periodBegin);
//			addTax.setPeriodto(periodEnd);
//			addTax.setZsxm_dm("01"); // 增值税
//			addTax.setPeriodtype(0); // 月报
//			list.add(addTax);
//		}
//		TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//		detailvo1.setSb_zlbh("10412");
//		detailvo1.setPeriodfrom(periodBegin);
//		detailvo1.setPeriodto(periodEnd);
//		detailvo1.setZsxm_dm("04"); // 所得税
//		detailvo1.setPeriodtype(0); // 月报
//		list.add(detailvo1);
//		
//		TaxTypeListDetailVO inTaxB = new TaxTypeListDetailVO();
//		inTaxB.setSb_zlbh("10413");
//		inTaxB.setPeriodfrom(periodBegin);
//		inTaxB.setPeriodto(periodEnd);
//		inTaxB.setZsxm_dm("10413"); // 所得税
//		inTaxB.setPeriodtype(0); // 月报
//		list.add(inTaxB);
//	}
	public void updateStatusCallback(JSONObject data) {
		String lsh = data.getString("LSH");
		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils
				.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(lsh);
		String condition = " region_extend1 = ? and nvl(dr, 0) = 0";
		TaxReportVO[] rpts = (TaxReportVO[]) singleObjectBO.queryByCondition(
				TaxReportVO.class, condition, sp);
		if (rpts.length > 0) {
			TaxReportVO reportvo = rpts[0];
			Object rptData = data.get("DATA");
			if (rptData != null && !"".equals(rptData)) {
				JSONArray jsonArray = (JSONArray) rptData;
				JSONObject rptJson = (JSONObject) jsonArray.get(0);
				String sbzt_dm = rptJson.getString("SBZT_DM");

				// 申报成功记录凭证序号
				if (TaxRptConst.iSBZT_DM_ReportSuccess == Integer
						.valueOf(sbzt_dm)) {
					String yzpzxh = rptJson.getString("YZPZXH");
					reportvo.setRegion_extend2(yzpzxh);
				}
				String remark = rptJson.getString("YCXX");
				reportvo.setRemark(remark);
				// 更新申报状态
				reportvo.setSbzt_dm(sbzt_dm);

				singleObjectBO.update(reportvo, new String[] { "sbzt_dm",
						"remark", "region_extend2" });
				String sql = "update ynt_taxreportdetail set sbzt_dm = ? where pk_corp = ? and  pk_taxreport = ? and nvl(dr, 0) = 0";
				sp.clearParams();
				sp.addParam(reportvo.getSbzt_dm());
				sp.addParam(reportvo.getPk_corp());
				sp.addParam(reportvo.getPk_taxreport());
				singleObjectBO.executeUpdate(sql, sp);
			}
		} else {
			log.error("未找到对应纳税申报表，流水号" + lsh);
		}

	}
	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "江苏";
	}
	
	/**
	 * 转换申报种类编号
	 * @param sbzl_bh
	 * @return
	 */
	private String getSbzlbh(String sbzl_bh) {
		if (TaxRptConst.SB_ZLBH1010201.equals(sbzl_bh)) {
			sbzl_bh = "10102";
		} else if (TaxRptConst.SB_ZLBHC1.equals(sbzl_bh)) {
			sbzl_bh = "29806";
		} else if (TaxRptConst.SB_ZLBHC2.equals(sbzl_bh)) {
			sbzl_bh = "29801";
		}
		return sbzl_bh;
	}
	
	private void setTaxTypeListValue(TaxReportVO detailvo,String pk_corp, JSONObject json,Map<String,TaxTypeSBZLVO> zlmap) {
		String sb_zlbh = json.getString("SB_ZLBH");
//		String zsxm_dm = null;
//		
//		switch (sb_zlbh) {
//		case "29806":
//		case "29801":
//		case "39806":
//		case "39801":
//			zsxm_dm = "C";
//			break;
//		case "10101":
//		case "10102":
//			zsxm_dm = "01";
//			break;
//		case "10412":
//			zsxm_dm = "04";
//			break;
//		case "10413":
//			zsxm_dm = "10413";
//			break;
//		default:
//			break;
//		}
		
		detailvo.setPeriodfrom(json.getString("SKSSQQ"));
		detailvo.setPeriodto(json.getString("SKSSQZ"));
		
		int periodType = getPeriodType(detailvo.getPeriodfrom(), detailvo.getPeriodto());
		if ("10102".equals(sb_zlbh) && periodType == 0) {
			sb_zlbh = TaxRptConst.SB_ZLBH1010201;
		} else if ("29806".equals(sb_zlbh)) {
			sb_zlbh = TaxRptConst.SB_ZLBHC1;
		} else if ("29801".equals(sb_zlbh)) {
			sb_zlbh = TaxRptConst.SB_ZLBHC2;
		}
		detailvo.setPeriodtype(periodType);
		detailvo.setSb_zlbh(sb_zlbh);
//		detailvo.setZsxm_dm(zsxm_dm); // 增值税
		detailvo.setPk_corp(pk_corp);
		detailvo.setLocation("江苏");
		detailvo.setVbillstatus(TaxRptConst.IBILLSTATUS_UNAPPROVE);
		detailvo.setSbzt_dm(String.valueOf(TaxRptConst.iSBZT_DM_UnSubmit));
		//
		TaxTypeSBZLVO vo = zlmap.get(sb_zlbh+","+periodType);
		if(vo!=null){
			detailvo.setPk_taxsbzl(vo.getPk_taxsbzl());
			
		}
	}
	
	/**
	 * 过滤未填写的非必填报表
	 * 
	 * @param data
	 */
	private void filterEmptyReport(TaxReportVO reportvo, Object data) {
		Set<String> requiredReport = new HashSet<String>();
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_taxsbzl());
		TaxRptTempletVO[] votemplets = (TaxRptTempletVO[]) singleObjectBO
				.queryByCondition(
						TaxRptTempletVO.class,
						" nvl(dr,0) = 0 and pk_taxsbzl = ? ",
						params);
		for (TaxRptTempletVO taxRptTempletVO : votemplets) {
			if (taxRptTempletVO.getIfrequired() != null
					&& taxRptTempletVO.getIfrequired().booleanValue()) {
				requiredReport.add(taxRptTempletVO.getReportname());
			}
		}
		
		Map<String, Boolean> rptCheck = new HashMap<String, Boolean>();
		Map<Field, String> fieldRptInfo = new HashMap<Field, String>();
		for (Field field : data.getClass().getDeclaredFields()) {
			String fname = field.getName();
			Object val = null;
			try {
				Method getMtd = data.getClass().getMethod(
						"get" + fname.substring(0, 1).toUpperCase()
						+ fname.substring(1));
				val = getMtd.invoke(data);
				
				
				Class<?> fieldType = val.getClass();
				boolean isArray = fieldType.isArray();
				
				if (isArray)
					fieldType = fieldType.getComponentType();
				
				TaxExcelPos clsAnno = fieldType
						.getAnnotation(TaxExcelPos.class);
				
				boolean isEmpty = checkReportIsEmpty(requiredReport, val);
				if (clsAnno != null && !"".equals(clsAnno.reportname())) {
					fieldRptInfo.put(field, clsAnno.reportname());
					if (!isEmpty) {
						rptCheck.put(clsAnno.reportname(), isEmpty);
					}
				} else {
					if (isEmpty) {
						Method setMtd = data.getClass().getMethod(
								"set" + fname.substring(0, 1).toUpperCase()
								+ fname.substring(1), field.getType());
						setMtd.invoke(data, new Object[]{null});
					}
				}
			} catch (Exception e) {
				log.error("校验申报表是否为空失败", e);
			}
		}
		
		for (Entry<Field, String> fieldEntry : fieldRptInfo.entrySet()) {
			Field field = fieldEntry.getKey();
			String fname = field.getName();
			Boolean isEmpty = rptCheck.get(fieldEntry.getValue());
			if (isEmpty == null || isEmpty.booleanValue()) {
				Method setMtd;
				try {
					setMtd = data.getClass().getMethod(
							"set" + fname.substring(0, 1).toUpperCase()
							+ fname.substring(1), field.getType());
					setMtd.invoke(data, new Object[]{null});
				} catch (Exception e) {
				}
			}
		}
	}
	
	private boolean checkReportIsEmpty(Set<String> requiredReport, Object data) throws Exception {
		boolean isEmpty = true;
		
		if (data == null) {
			return isEmpty;
		}
		
		Class<?> fieldType = data.getClass();
		boolean isArray = fieldType.isArray();
		if (isArray)
			fieldType = fieldType.getComponentType();
		TaxExcelPos clsAnno = fieldType
				.getAnnotation(TaxExcelPos.class);
		
		if (clsAnno != null && !"".equals(clsAnno.reportname())
				&& requiredReport.contains(clsAnno.reportname())) {
			// 必填表返回false
			return false;
		}
		
		if (isArray) {
			Object[] dataArray= (Object[]) data;
			if (clsAnno == null || "".equals(clsAnno.reportname())) {
				// 数组类型，且没有表名，取第一个元素校验
				Object obj1 = dataArray[0];
				isEmpty = checkReportIsEmpty(requiredReport, obj1);
			} else if (dataArray.length > 0){
				// 有表名且数组不为空，校验为非空
				isEmpty = false;
			}
		} else {
			for (Field field : data.getClass().getDeclaredFields()) {
				String fname = field.getName();
				Method getMtd = data.getClass().getMethod(
						"get" + fname.substring(0, 1).toUpperCase()
						+ fname.substring(1));
				Object val = getMtd.invoke(data);
				if (val == null) {
					continue;
				}
				if (clsAnno == null || "".equals(clsAnno.reportname())) {
					isEmpty = checkReportIsEmpty(requiredReport, val);
				} else {
					// 有表名，则判断字段值是否为空
					if (val instanceof String && !"".equals(val)
							|| val instanceof DZFDouble && !DZFDouble.ZERO_DBL.equals(val)
							|| val instanceof Object[] && ((Object[]) val).length > 0) {
						// 有字段不为空且不为空字符串、0或者空数组则报表不为空
						isEmpty = false;
					}
				}
				if (!isEmpty) {
					break;
				}
			}
		}
		return isEmpty;
	}

	@Override
	public void processZeroDeclaration(TaxReportVO reportvo,
			CorpVO corpvo,CorpTaxVo corptaxvo, SingleObjectBO singleObjectBO) {
		if(reportvo == null)
			return;
		
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[])reportvo.getChildren();
		
		//zpm
		
		// 无模板，重新生成
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
			log.error("零申报-解析JSON失败", e);
			throw new BusinessException("解析报表失败");
		}
		processSaveFile(sReturn, reportvo, corpvo);
		
		//zpm
//		if (isNewReport) {
//			singleObjectBO.insertVOWithPK(reportvo);
//			singleObjectBO.insertVOArr(corpvo.getPk_corp(), detailvos);
//		} else {
			
		    //更新子表字段
		singleObjectBO.updateAry(detailvos);
		singleObjectBO.update(reportvo, new String[]{ "spreadfile" });
//		}
		Map objMapReport = readJsonValue(
				readFileString(((TaxReportDetailVO) reportvo.getChildren()[0])
						.getSpreadfile()), LinkedHashMap.class);
		// 是否零申报
		reportvo.setSbzt_osb(DZFBoolean.TRUE);
		sendTaxReport(corpvo, null, objMapReport, new SpreadTool(),
				reportvo, singleObjectBO);
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
	
	// 收费
	private void doCharge(CorpVO corpVO, TaxReportVO reportvo, String userId) {
		IVersionMngService verionMng = (IVersionMngService) SpringUtils
				.getBean("sys_funnodeversionserv");
		// 是否收费
		DZFBoolean isCharge = verionMng.isChargeByProduct(
				reportvo.getPk_corp(), IDzfServiceConst.DzfServiceProduct_03);
		if (isCharge.booleanValue()) {
			String period = reportvo.getPeriodto().toString().substring(0, 7);
			String key = "charge_" + reportvo.getPk_corp() + "_" + period;
//			ReentrantLock lock = ChargeLock.getInstance().get(key);
			log.info(key + "一键报税收费begin...");
			try {
//				lock.lock();
				IBalanceService chargeService = (IBalanceService) SpringUtils
						.getBean("balanceServImpl");
				DZFDate dzfdate = new DZFDate(period + "-15");
				// 是否已收费
				DZFBoolean charged = chargeService.isAlreadyConsumption(
						IDzfServiceConst.DzfServiceProduct_03, period,
						reportvo.getPk_corp());
				if (!charged.booleanValue()) {
					DZFDouble price = null;
//					if ("一般纳税人".equals(corpVO.getChargedeptname())) {
//						price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_YBR);
//					} else {
//						price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_XGM);
//					}
					//会计工厂
					String wtcorp = pubFctService.getAthorizeFactoryCorp(dzfdate, reportvo.getPk_corp());
					String pkcorp = corpVO.getPk_corp();
					if(!StringUtil.isEmpty(wtcorp)){//是否委托会计工厂
						pkcorp = wtcorp;
					}else{
						pkcorp = queryCascadeCorps(corpVO.getPk_corp());//代帐公司
					}
					//一键报税设置取金额
					SQLParameter param = new SQLParameter();
					DZFDate date = new DZFDate();
					param.addParam(pkcorp);
					param.addParam(date.toString());
					param.addParam(date.toString());
					BondedSetVO[] vos = (BondedSetVO[])sbov.queryByCondition(BondedSetVO.class, "pk_corp = ? and nvl(dr,0) = 0 and begindate <=? and enddate >=?", param);
					if(vos!=null && vos.length>0){
						if ("一般纳税人".equals(corpVO.getChargedeptname())) {
							price = vos[0].getGeneralamount();
						} else {
							price = vos[0].getScaleamount();
						}
					}else{
						if ("一般纳税人".equals(corpVO.getChargedeptname())) {
							price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_YBR);
						} else {
							price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_XGM);
						}
					}
					DZFBalanceBVO bvo = new DZFBalanceBVO();
					bvo.setPeriod(period);
					// 使用数量
					bvo.setChangedcount(price);
					// 减少
					bvo.setIsadd(1);
					bvo.setPk_corp(reportvo.getPk_corp());
					bvo.setPk_dzfservicedes(IDzfServiceConst.DzfServiceProduct_03);
					bvo.setPk_user(userId);
					bvo.setPk_corpkjgs(corpVO.getFathercorp());			
					//DZFDate dzfdate = new DZFDate(period + "-15");
					//查询委托公司
					//String wtcorp = pubFctService.getAthorizeFactoryCorp(dzfdate, reportvo.getPk_corp());
					if(StringUtil.isEmpty(wtcorp)){
						chargeService.consumption(bvo);// 扣费
					}else{
						// 扣委托公司费用
						chargeService.consumptionByFct(bvo, dzfdate);
					}
				}
				log.info(key + "一键报税收费end...");
			} catch (Exception e) {
				log.error("收费失败", e);
				if (e instanceof BusinessException) {
					if(!StringUtil.isEmpty(e.getMessage()) && e.getMessage().startsWith("余额不足")){
						throw new BusinessException("余额不足，重新选择报税公司或充值后再进行申报");
					}else{
						throw new BusinessException(e.getMessage());
					}
					
				} else {
					throw new WiseRunException(e);
				}
			} finally {
				// 解锁
//				if (lock != null) {
//					lock.unlock();
//				}
			}
		}
	}
	
	/**
	 * 减免类型
	 * @param initData
	 * @param gridLb
	 */
	private void dealJmxzList(Map<String, Object> initData, JSONArray gridLb) {
		if (gridLb.size() == 0) {
			return;
		}
		StringBuilder jsStr = new StringBuilder();
		StringBuilder msStr = new StringBuilder();
		jsStr.append("\"");
		msStr.append("\"");
		StringBuilder showName = new StringBuilder();
		for (Object object : gridLb) {
			JSONObject qcVO = (JSONObject) object;
			showName.append(qcVO.getString("ssjmxz_dm"))
			.append("|").append(qcVO.getString("swsx_dm"))
			.append("|").append(qcVO.getString("swsxmc"))
			.append("|").append(qcVO.getString("ssjmxzmc"));
			// 减免性质
			String jmzlx_dm = qcVO.getString("jmzlx_dm");
			if ("01".equals(jmzlx_dm)) {
				// 减税（减征）
				jsStr.append(showName).append(",");
			} else if ("02".equals(jmzlx_dm)) {
				// 免税（免征）
				msStr.append(showName).append(",");
			}
			showName.setLength(0);
		}
		jsStr.setCharAt(jsStr.length() - 1, '"');
		msStr.setCharAt(msStr.length() - 1, '"');
		initData.put("jmxzlist-js", jsStr.toString());
		initData.put("jmxzlist-ms", msStr.toString());
	}
	
	@Override
	protected List<String> queryCorpTaxRptVO(CorpVO corpvo, String period) {
		// 用户勾选的报表
		List<String> checkedRpt = super.queryCorpTaxRptVO(corpvo, period);
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
		if ("on".equals(taxJstcConfig.service_switch)
				&& !"true".equals(taxJstcConfig.dev_mode)
				&& !StringUtil.isEmpty(corpvo.getVsoccrecode())
				&& !StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
			// 国税接口查询的税种
			// 江苏的接口与本地的期间不一样。period已经往前取一个月，这个接口period在往后取一个月。//zpm
			period = DateUtils.getNextPeriod(period);
			List<String> queryList = getDeclarableInventory(period, corpvo,taxvo);
			if (checkedRpt != null) {
				for (String pk_taxsbzl : checkedRpt) {
					if (pk_taxsbzl.equals("000001acbcdadiew00000018")
							|| pk_taxsbzl.equals("000001acbcdadiew00000019")
							|| pk_taxsbzl.equals("000001acbcdadiew00000020")
							|| pk_taxsbzl.equals("000001acbcdadiew00000021")
							|| pk_taxsbzl.equals("000001acbcdadiew00000022")
							// 个人所得税生产经营所得纳税申报表（A表）
							|| pk_taxsbzl.equals("000001acbcdadiew00000026")
							// 地方各项基金费（工会经费、垃圾处理费）申报表
							|| pk_taxsbzl.equals("000001acbcdadiew00000027")) {
						// 印花税和附加税
						queryList.add(pk_taxsbzl);
					}
				}
			}
			checkedRpt = queryList;
		}
		return checkedRpt;
	}
	
	@Override
	protected List<TaxReportDetailVO> queryReportDetailVOs(CorpVO corpVO,
			List<TaxReportVO> adddetails,String period) {
		List<TaxReportDetailVO> allDetails = new ArrayList<TaxReportDetailVO>();
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpVO.getPk_corp());
		if ("on".equals(taxJstcConfig.service_switch)
				&& !StringUtil.isEmpty(corpVO.getVsoccrecode())
				&& !StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
			// 用户勾选的表
			List<TaxRptTempletVO> checkedTemp = queryRptTempletVOs(corpVO.getPk_corp(),period);
			// 所有表
			List<TaxRptTempletVO> allTemp = queryAllRptTempletVOs(corpVO);
			
			Map<String, List<TaxRptTempletVO>> checkedTempMap = DZfcommonTools
					.hashlizeObject(checkedTemp, new String[] { "pk_taxsbzl" });
			
			Map<String, List<TaxRptTempletVO>> allTempMap = DZfcommonTools
					.hashlizeObject(allTemp, new String[] { "pk_taxsbzl" });
			for (TaxReportVO reportvo : adddetails) {
				// 出表规则
				List<String> listRptcode = new ArrayList<String>();
				listRptcode.addAll(getReportList(corpVO, taxvo,reportvo));
				if (TaxRptConst.SB_ZLBH1010201.equals(reportvo.getSb_zlbh())) {
					int size = listRptcode.size();
					for (int i = 0; i < size; i++) {
						listRptcode.set(i,
								listRptcode.get(i).replace("10102", "1010201"));
					}
				}
				List<TaxRptTempletVO> tempList = null;
				if (listRptcode.size() > 0) {
					// 出表规则
					tempList = allTempMap.get(reportvo.getPk_taxsbzl());
					Iterator<TaxRptTempletVO> it = tempList.iterator();
					while (it.hasNext()) {
						TaxRptTempletVO tmp = it.next();
						if (!listRptcode.contains(tmp.getReportcode())) {
							it.remove();
						}
					}
				} else {
					tempList = checkedTempMap.get(reportvo.getPk_taxsbzl());
					if (tempList == null && (TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())
							|| TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())
							|| TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh()))) {
						// 处理财报月季勾选错误情况
						tempList = dealPeriodCheckError(reportvo, checkedTempMap, allTempMap);
					}
				}
				List<TaxReportDetailVO> detailList = buildTaxReportDetailVO(
						reportvo, tempList);
				if (detailList != null && detailList.size() > 0)
					allDetails.addAll(detailList);
			}
		} else {
			allDetails = super.queryReportDetailVOs(corpVO, adddetails,period);
		}
		return allDetails;
	}

	/**
	 * 处理月季报勾选错误的情况
	 * @param reportvo
	 * @param checkedTempMap
	 * @param allTempMap
	 * @return
	 */
	private List<TaxRptTempletVO> dealPeriodCheckError(TaxReportVO reportvo, Map<String, List<TaxRptTempletVO>> checkedTempMap,
			Map<String, List<TaxRptTempletVO>> allTempMap) {
		String sql = "select pk_taxsbzl from ynt_tax_sbzl where sbcode = ? and pk_taxsbzl <> ? and nvl(dr,0)=0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getSb_zlbh());
		sp.addParam(reportvo.getPk_taxsbzl());
		// 查询相同税种不同期间类型的记录
		List<String> types = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());
		Set<String> checkedCodes = new HashSet<String>();
		for (String type : types) {
			// 已勾选税表中包含符合税种的期间
			if (checkedTempMap.containsKey(type)) {
				List<TaxRptTempletVO> temps = checkedTempMap.get(type);
				for (TaxRptTempletVO temp : temps) {
					checkedCodes.add(temp.getReportcode());
				}
				break;
			}
		}
		
		List<TaxRptTempletVO> tempList = allTempMap.get(reportvo.getPk_taxsbzl());
		if (tempList != null) {
			if (checkedCodes.size() == 0) {
				// 没有勾表取必填表
				for (TaxRptTempletVO temp : tempList) {
					if (temp.getIfrequired() != null && temp.getIfrequired().booleanValue()) {
						checkedCodes.add(temp.getReportcode());
					}
				}
			}
			Iterator<TaxRptTempletVO> it = tempList.iterator();
			while (it.hasNext()) {
				TaxRptTempletVO tmp = it.next();
				// 取其他期间勾的表
				if (!checkedCodes.contains(tmp.getReportcode())) {
					it.remove();
				}
			}
		}
		return tempList;
	}
	
	private List<TaxRptTempletVO> queryAllRptTempletVOs(CorpVO corpVO) {
		SQLParameter params = new SQLParameter();
		params.addParam(getLocation(corpVO));
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_taxrpttemplet where  ");
		sf.append(" nvl(dr,0)=0 and rtrim(location) = ? order by pk_taxsbzl,orderno");
		List<TaxRptTempletVO> list = (List<TaxRptTempletVO>)
				singleObjectBO.executeQuery(sf.toString(), params,new BeanListProcessor(TaxRptTempletVO.class));
		return list;
	
	}
	
	public TaxPaymentVO[] queryTaxPayment(CorpVO corpvo, CorpTaxVo taxvo,TaxReportVO reportvo) {
		BaseRequestVO baseRequest = getBaseRequset(corpvo,taxvo);
		baseRequest.setServiceid("FW_DZSWJ_WSPZ");
		baseRequest.getBody().setSign("wspznfService");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("NSRSBH", corpvo.getVsoccrecode());
		// 缴款日期起
		params.put("JKRQQ", reportvo.getPeriodfrom());
		// 缴款日期止
		params.put("JKRQZ", reportvo.getPeriodto());

//		String ywbw = EncryptDecrypt.encode(OFastJSON.toJSONString(params), JSTaxPropUtil.getprop("app_secret"));
		String ywbw = EncryptDecrypt.encode(JsonUtils.serialize(params), taxJstcConfig.app_secret);

		baseRequest.getBody().setYwbw(ywbw);
		
//		String bw = OFastJSON.toJSONString(baseRequest);
		String bw = JsonUtils.serialize(baseRequest);
		
		String encode = ZipUtil.zipEncode(bw, true);
		HashMap<String, String> requestParam = new HashMap<String, String>();
		requestParam.put("request", encode);
		
		Map<String, String> rmap = HttpUtil.post(taxJstcConfig.url,
				requestParam);
		
		String result = HttpUtil.parseRes(rmap.get("response"));
		JSONObject rsJosn = (JSONObject) JSON.parse(result);
		String status = rsJosn.getString("RESULT");
		
		if (!"0000".equals(status)) {
			log.error("查询完税凭证失败" + rsJosn);
			throw new BusinessException((String) rsJosn.get("MSG"));
		}
		TaxPaymentVO[] payments = null;
		JSONArray dataArray = (JSONArray) rsJosn.get("DATA");
		if (dataArray != null && dataArray.size() > 0) {
			String zsxmdm = reportvo.getZsxm_dm();
			Iterator<Object> it = dataArray.iterator();
			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();
				// 过滤非查询税种，目前只有增值税和企业所得税
				String zsxmmc = json.getString("zsxmmc");
				if ("01".equals(zsxmdm) && "增值税".equals(zsxmmc)
						|| ("04".equals(zsxmdm) || "05".equals(zsxmdm)) && "企业所得税".equals(zsxmmc)) {
					continue;
				}
				it.remove();
				
			}
			payments = DzfTypeUtils.cast(dataArray,
					FieldMapping.getFieldMapping(new TaxPaymentVO()),
					TaxPaymentVO[].class, JSONConvtoJAVA.getParserConfig());
		}
		return payments;
	}
	
	@Override
	public String checkReportList(CorpVO corpvo,CorpTaxVo taxvo, List<TaxReportVO> list)
			throws DZFWarpException {
		String msg = null;
		if (StringUtil.isEmpty(corpvo.getVsoccrecode())) {
			msg = "纳税人识别号为空";
		} else if (StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
			msg = "国税密码为空";
		} else if ("on".equals(taxJstcConfig.service_switch)) {
			if (list != null && list.size() > 0) {
				String period = DateUtils.getPeriod(new DZFDate());
				JSONObject rsJosn = getInventoryJson(period, corpvo,taxvo);
				String status = rsJosn.getString("RESULT");
				if ("0000".equals(status)) {
//					JSONArray data = (JSONArray) rsJosn.get("DATA");
//					for (Object object : data) {
//						JSONObject json = (JSONObject) object;
//						String code = json.getString("SB_ZLBH");
//						if ("39805".equals(code)) {
//							msg = "该公司在税局备案的会计制度是企业会计制度，申报的财务报表种类是企业会计制度，暂不支持企业会计制度财报种类申报";
//						}
//					}
				} else {
					msg = rsJosn.getString("MSG");
				}
			}
		}
		if (msg != null) {
			msg = "<font color='red'>" + msg + "</font><br>";
		}
		return msg;
	}
	
	
	/**
	 * 级联查询总公司
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private String queryCascadeCorps(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = "select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
		List<CorpVO> list = (List<CorpVO>) sbov.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() >= 2) {
			return list.get(list.size() - 2).getPk_corp();
		}
		return null;

	}
	
	private void getQcFromJsonFile(HashMap<String, Object> qcData, TaxReportVO reportvo) {
		TaxReportNewQcInitVO qcVO = null;

		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getPk_corp());
		sp.addParam(reportvo.getPeriod());
		sp.addParam(reportvo.getPk_taxsbzl());
		String condition = " pk_corp = ? and period = ? and pk_taxsbzl = ? ";
		TaxReportNewQcInitVO[] rs = (TaxReportNewQcInitVO[]) singleObjectBO.queryByCondition(TaxReportNewQcInitVO.class, condition, sp);
		if (rs != null && rs.length > 0) {
			qcVO = rs[0];
		}
		
		if (qcVO != null) {
			String file = qcVO.getSpreadfile();
			String strJson = TaxRptemptools.readFileString(file);
			try {
				Object json = JSON.parse(strJson);
				qcData.put(reportvo.getSb_zlbh() + "qc", json);
			} catch (Exception e) {
				log.error("解析期初JSON失败", e);
			}
		}
	}
}