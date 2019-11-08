package com.dzf.zxkj.platform.service.taxrpt.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.gl.gl_bdset.YntCpaccountVO;
import com.dzf.model.gl.jiangsutaxrpt.TaxRptConst;
import com.dzf.model.gl.taxrpt.PeriodType;
import com.dzf.model.gl.taxrpt.TaxReportDetailVO;
import com.dzf.model.gl.taxrpt.TaxReportNewQcInitVO;
import com.dzf.model.gl.taxrpt.TaxReportVO;
import com.dzf.model.gl.taxrpt.chk.TaxRptChk10102_chongqing;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.AccountCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.service.gl.taxrpt.ITaxBalaceCcrService;
import com.dzf.service.gl.taxrpt.impl.TaxDeclarationServiceImpl;
import com.dzf.service.gl.taxrpt.shandong.impl.TaxReportServiceImpl;
import com.dzf.service.spreadjs.SpreadTool;
import com.dzf.spring.SpringUtils;

// 重庆地区
@Service("taxRptservice_congqin")
public class CqTaxRptServiceImpl extends DefaultTaxRptServiceImpl {

	private static Logger log = Logger.getLogger(TaxReportServiceImpl.class);

	@Autowired
	private SingleObjectBO sbo;

//	@Override
//	public TaxTypeListDetailVO[] getTypeList(CorpVO corpvo, String yearmonth, String operatorid, String operatedate,
//			SingleObjectBO sbo) throws DZFWarpException {
//		TaxTypeListDetailVO[] vos = null;
//
//		String pk_corp = corpvo.getPk_corp();
//
//		TaxTypeListVO headold = null;
//		TaxTypeListDetailVO[] vosold = null;
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		params.addParam(yearmonth);
//		TaxTypeListVO[] voheads = (TaxTypeListVO[]) sbo.queryByCondition(TaxTypeListVO.class,
//				"nvl(dr,0) = 0 and pk_corp=? and yearmonth=?", params);
//		if (voheads != null && voheads.length > 0) {
//			headold = voheads[0];
//			params = new SQLParameter();
//			params.addParam(headold.getPrimaryKey());
//			vosold = (TaxTypeListDetailVO[]) sbo.queryByCondition(TaxTypeListDetailVO.class,
//					"nvl(dr,0)=0 and pk_taxtypelist=?", params);
//		}
//		boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
//		vos = getTaxTypeListDetailVO(showYearInTax, yearmonth, corpvo, sbo);
//
//		// 建立主表记录
//		String pk = IDGenerate.getInstance().getNextID(pk_corp);
//
//		TaxTypeListVO headvo = new TaxTypeListVO();
//		headvo.setPk_taxtypelist(pk);
//		headvo.setPk_corp(pk_corp);
//		headvo.setNsrsbh(corpvo.getTaxcode());
//		headvo.setNsrdzdah(vos[0].getNsrdzdah());
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
//		if (headold != null) {
//			sbo.deleteObject(headold);
//		}
//		if (vosold != null) {
//			sbo.deleteVOArray(vosold);
//		}
//		// 增加主表
//		sbo.insertVOWithPK(headvo);
//		// 增加子表
//		if (vos.length > 0) {
//			sbo.insertVOArr(pk_corp, vos);
//		}
//
//		return vos;
//	}

//	@Override
//	public TaxTypeListDetailVO[] getTaxTypeListDetailVO(boolean showYearInTax, String yearmonth, CorpVO corpvo,
//			SingleObjectBO sbo) throws DZFWarpException {
//		List<TaxTypeListDetailVO> list = new ArrayList<TaxTypeListDetailVO>();
//		StringBuffer sb = new StringBuffer();
//		SQLParameter params = new SQLParameter();
//		sb.append("select * from YNT_TAXRPTTEMPLET where rtrim(location)=? and nvl(dr,0)=0 and ");
//		sb.append("PK_TAXRPTTEMPLET in (select PK_TAXRPTTEMPLET from ynt_taxrpt where pk_corp = ? ");
//		sb.append(" and ISSELECT='Y' and nvl(dr,0)=0 ) ");
//		params.addParam("重庆");
//		params.addParam(corpvo.getPk_corp());
//		List<TaxRptTempletVO> votemplets = (List<TaxRptTempletVO>) sbo.executeQuery(sb.toString(), params,
//				new BeanListProcessor(TaxRptTempletVO.class));
//		TaxTypeListDetailVO detailvo1 = new TaxTypeListDetailVO();
//		TaxTypeListDetailVO detailvo2 = new TaxTypeListDetailVO();
//		TaxTypeListDetailVO detailvo3 = new TaxTypeListDetailVO();
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("一般纳税人")) {
//			for (TaxRptTempletVO tvo : votemplets) {
//				if (tvo.getSb_zlbh().equals("10101") && StringUtil.isEmpty(detailvo1.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo1.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo1.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo1.setSb_zlbh("10101");
//					detailvo1.setZsxm_dm("01"); // 增值税
//					detailvo1.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo1);
//				} else if (tvo.getSb_zlbh().equals("10414") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo2.setSb_zlbh("10414");
//					detailvo2.setZsxm_dm("04"); // 所得税A
//					detailvo2.setPeriodtype(PeriodType.jidureport); // 季报报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("1041401") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo2.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo2.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo2.setSb_zlbh("1041401");
//					detailvo2.setZsxm_dm("04"); // 所得税A
//					detailvo2.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("10415") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo2.setSb_zlbh("10415");
//					detailvo2.setZsxm_dm("10413"); // 所得税B
//					detailvo2.setPeriodtype(PeriodType.jidureport); // 季报报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("1041501") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo2.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo2.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo2.setSb_zlbh("1041501");
//					detailvo2.setZsxm_dm("10413"); // 所得税B
//					detailvo2.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo2);
//
//				} else if ((tvo.getSb_zlbh().equals("C3") || tvo.getSb_zlbh().equals("C4"))
//						&& StringUtil.isEmpty(detailvo3.getZsxm_dm())) {
//					if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC3);
//					} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC4);
//					}
//					detailvo3.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo3.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo3.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//					detailvo3.setPeriodtype(1);// 季报
//					list.add(detailvo3);
//				} else if ((tvo.getSb_zlbh().equals("C301") || tvo.getSb_zlbh().equals("C401"))
//						&& StringUtil.isEmpty(detailvo3.getZsxm_dm())) {
//					if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC301);
//					} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC401);
//					}
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo3.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo3.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo3.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//					detailvo3.setPeriodtype(0);// 月报
//					list.add(detailvo3);
//				}
//			}
//
//		} else if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			for (TaxRptTempletVO tvo : votemplets) {
//				if (tvo.getSb_zlbh().equals("10102") && StringUtil.isEmpty(detailvo1.getZsxm_dm())) {
//					detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo1.setSb_zlbh("10102");
//					detailvo1.setZsxm_dm("01"); // 增值税
//					detailvo1.setPeriodtype(PeriodType.jidureport); // 季报
//					list.add(detailvo1);
//				} else if (tvo.getSb_zlbh().equals("1010201") && StringUtil.isEmpty(detailvo1.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo1.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo1.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo1.setSb_zlbh("1010201");
//					detailvo1.setZsxm_dm("01"); // 增值税
//					detailvo1.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo1);
//				} else if (tvo.getSb_zlbh().equals("10412") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo2.setSb_zlbh("10412");
//					detailvo2.setZsxm_dm("04"); // 所得税A
//					detailvo2.setPeriodtype(PeriodType.jidureport); // 季报报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("1041201") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo2.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo2.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo2.setSb_zlbh("1041201");
//					detailvo2.setZsxm_dm("04"); // 所得税A
//					detailvo2.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("10413") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo2.setSb_zlbh("10413");
//					detailvo2.setZsxm_dm("10413"); // 所得税B
//					detailvo2.setPeriodtype(PeriodType.jidureport); // 季报报
//					list.add(detailvo2);
//
//				} else if (tvo.getSb_zlbh().equals("1041301") && StringUtil.isEmpty(detailvo2.getZsxm_dm())) {
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo2.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo2.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo2.setSb_zlbh("1041301");
//					detailvo2.setZsxm_dm("10413"); // 所得税B
//					detailvo2.setPeriodtype(PeriodType.monthreport); // 月报
//					list.add(detailvo2);
//
//				} else if ((tvo.getSb_zlbh().equals("C1") || tvo.getSb_zlbh().equals("C2"))
//						&& StringUtil.isEmpty(detailvo3.getZsxm_dm())) {
//					if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC1);
//					} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC2);
//					}
//					detailvo3.setPeriodfrom(getQuarterStartDate(yearmonth));
//					detailvo3.setPeriodto(getQuarterEndDate(yearmonth));
//					detailvo3.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//					detailvo3.setPeriodtype(1);// 季报
//					list.add(detailvo3);
//				} else if ((tvo.getSb_zlbh().equals("C101") || tvo.getSb_zlbh().equals("C201"))
//						&& StringUtil.isEmpty(detailvo3.getZsxm_dm())) {
//					if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC101);
//					} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//						// 会计期间
//						detailvo3.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC201);
//					}
//					String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//					detailvo3.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//					detailvo3.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//					detailvo3.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//					detailvo3.setPeriodtype(0);// 月报
//					list.add(detailvo3);
//				}
//			}
//		} else {
//			throw new BusinessException("当前功能仅支持小规模纳税人增值税申报、所得税、财报申报。");
//		}
//		if (showYearInTax) {
//			// 所得税年度汇算清缴
//			TaxTypeListDetailVO detailvo4 = new TaxTypeListDetailVO();
//			detailvo4.setSb_zlbh("A");
//			int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
//			detailvo4.setPeriodfrom(year + "-01-01");
//			detailvo4.setPeriodto(year + "-12-31");
//			detailvo4.setZsxm_dm("04"); // 所得税
//			detailvo4.setPeriodtype(2); // 年报
//			list.add(detailvo4);
//		}
//		return list.toArray(new TaxTypeListDetailVO[0]);
//	}

//	@Override
//	public List<RptBillVO> getRptBillVO(TaxReportVO paravo, SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException {
//
//		List<RptBillVO> volist = null;
//		SQLParameter params = new SQLParameter();
//		List<String> listRptcode = new ArrayList<String>();
//		// 查询客户档案中勾选的报表
//		params.addParam(paravo.getPk_corp());
//		CorpTaxRptVO[] taxrptvos = (CorpTaxRptVO[]) sbo.queryByCondition(CorpTaxRptVO.class,
//				"nvl(dr,0)=0 and pk_corp=? and taxrptcode like '" + paravo.getSb_zlbh() + "%'", params);
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
//
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
//			if (listRptcode.size() > 0) // 客户档案设置过填报列表，没有设置的报表跳过
//			{
//				if (listRptcode.contains(tvo.getReportcode()) == false) {
//					continue;
//				}
//
//			} else // 客户档案没设置过填报列表，只填写必填内容
//			{
//				if (tvo.getIfrequired() == null || tvo.getIfrequired().booleanValue() == false) {
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
//
//	}

	public String checkReportData(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10412)
//				|| reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH1041201)
//				|| reportvo.getSb_zlbh().equals(CqtcZLBHConst.SB_ZLBH10414)
//				|| reportvo.getSb_zlbh().equals(CqtcZLBHConst.SB_ZLBH1041401)
				) {
			errmsg = checkForCQReport(mapJson, corpvo, reportvo, hmRptDetail, sbo);
			errmsg += checkForSB_ZLBH10412(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10413)){
			
			errmsg = checkForSB_ZLBH10413(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} 
		return errmsg;
	}

	private String checkForCQReport(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo2) {
		String errmsg = "";
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		String maintablename = "固定资产加速折旧(扣除)明细表（附表2）";
		if (listReportName.contains(maintablename)) {
			Object value_obj = spreadtool.getCellValue(mapJson, maintablename, 8, 1);
			if (value_obj != null && value_obj.toString().trim().startsWith("Z")) {
				errmsg = "<" + maintablename + ">B9,所选择的行业编码错误，请选择下级行业编码";
			}
		}
		return errmsg;
	}

	public String checkReportDataWarning(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
//		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10102)
//				|| reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH1010201)) {
//			errmsg = checkForSB_ZLBH10102_cq(mapJson, corpvo, reportvo, hmRptDetail, sbo);
//		}
		return errmsg;
	}

	private String checkForSB_ZLBH10102_cq(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo2) {
		String errmsg = "";
		YntCpaccountVO[] accountVO = AccountCache.getInstance().get(null, corpvo.getPk_corp());
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		String maintablename = "增值税纳税申报表";

		if (listReportName.contains(maintablename)) {
			DZFDouble v = DZFDouble.ZERO_DBL;
			if (reportvo.getPeriodtype() == PeriodType.jidureport) {
				DZFDouble yjse = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 27, 4)).setScale(2,
						DZFDouble.ROUND_HALF_UP);
				v = getDzfDouble(spreadtool.getFormulaValue(null,
						"SubjectAmt2((\"123\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null, corpvo, reportvo,
						accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
				if (yjse.equals(v) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += "重庆报税数据有如下错误:<" + maintablename + ">  从局端获取的税务机关代开的增值税专用发票不含税销售额[" + yjse.toString()
							+ "]与您账中的[" + v.toString() + "]不一致，请您检查下凭证!";
				}

				DZFDouble yjse2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 27, 5)).setScale(2,
						DZFDouble.ROUND_HALF_UP);
				v = getDzfDouble(spreadtool.getFormulaValue(null,
						"SubjectAmt2((\"124\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null, corpvo, reportvo,
						accountVO))
								.setScale(2, DZFDouble.ROUND_HALF_UP)
								.add(getDzfDouble(spreadtool.getFormulaValue(null,
										"SubjectAmt2((\"127\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null,
										corpvo, reportvo, accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP));
				v = v.setScale(2, DZFDouble.ROUND_HALF_UP);
				if (yjse2.equals(v) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += "重庆报税数据有如下错误:<" + maintablename + ">  从局端获取的税务机关代开的增值税专用发票不含税销售额[" + yjse2.toString()
							+ "]与您账中的[" + v.toString() + "]不一致，请您检查下凭证!";
				}

			} else if (reportvo.getPeriodtype() == PeriodType.monthreport) {
				DZFDouble yjse = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 27, 4)).setScale(2,
						DZFDouble.ROUND_HALF_UP);
				v = getDzfDouble(spreadtool.getFormulaValue(null,
						"SubjectAmt((\"123\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null, corpvo, reportvo,
						accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);
				if (yjse.equals(v) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
							+ ">  从局端获取的税务机关代开的增值税专用发票不含税销售额[" + yjse.toString() + "]与您账中的[" + v.toString()
							+ "]不一致，请您检查下凭证!";
				}

				DZFDouble yjse2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 27, 5)).setScale(2,
						DZFDouble.ROUND_HALF_UP);
				v = getDzfDouble(spreadtool.getFormulaValue(null,
						"SubjectAmt((\"124\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null, corpvo, reportvo,
						accountVO))
								.setScale(2, DZFDouble.ROUND_HALF_UP)
								.add(getDzfDouble(spreadtool.getFormulaValue(null,
										"SubjectAmt((\"127\",\"2\",\"税额\",\"@period\",\"@year\",\"@corp\")", null,
										corpvo, reportvo, accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP));
				v = v.setScale(2, DZFDouble.ROUND_HALF_UP);
				if (yjse2.equals(v) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
							+ ">  从局端获取的税务机关代开的增值税专用发票不含税销售额[" + yjse2.toString() + "]与您账中的[" + v.toString()
							+ "]不一致，请您检查下凭证!";
				}

			}
		}
		return errmsg;
	}

	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();
		if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10102_chongqing.saCheckCondition;
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
			String[] sacondition = TaxRptChk10102_chongqing.saCheckCondition;
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
	public Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map objMapReport, SpreadTool spreadtool,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
		throw new BusinessException("请启动重庆一键报税客户端。");
	}

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		HashMap hmQCData = new HashMap();

		String sb_zlbh = reportvo.getSb_zlbh();
		if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh) || TaxRptConst.SB_ZLBH10102.equals(sb_zlbh)||
				TaxRptConst.SB_ZLBH1010101.equals(sb_zlbh) || TaxRptConst.SB_ZLBH1010201.equals(sb_zlbh)
				||TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)) {

			hmQCData = initTax(reportvo);
		}
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
//			String sb_zlbh = reportVO.getSb_zlbh();//
//			String period = reportVO.getPeriodto().substring(0, 7);// 期间
			String period = reportVO.getPeriod();
			Integer periodType = reportVO.getPeriodtype();
			String pk_sbzl = reportVO.getPk_taxsbzl();

			if (periodType == null)
				return;

			TaxReportNewQcInitVO initvo = queryReportQcByInit(pk_corp, pk_sbzl, period);
			if (initvo == null || StringUtil.isEmpty(initvo.getSpreadfile()))
				return;
			String fileName = initvo.getSpreadfile();
			// String fileName = TaxReportPath.taxReportPath + pk_corp + "_" +
			// period + "_" + sb_zlbh + "_" + periodType + ".ssjson";

			String qcStr = new TaxDeclarationServiceImpl().getReportTemplet(fileName);

			if (StringUtil.isEmpty(qcStr))
				return;
			Map<String, Object> map = (Map<String, Object>) JSON.parse(qcStr);

			if (map != null && map.size() > 0) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					initData.put(entry.getKey(), entry.getValue());
				}
			}

		} catch (Exception e) {
			log.error("错误",e);
		}

	}

	private TaxReportNewQcInitVO queryReportQcByInit(String pk_corp, String pk_taxsbzl, String period) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_taxsbzl);
		TaxReportNewQcInitVO initvo = (TaxReportNewQcInitVO) sbo.executeQuery(
				" Select * From ynt_taxreportnewqcinit t Where nvl(dr,0) = 0 and t.pk_corp = ? and t.period = ? and t.pk_taxsbzl = ? ", sp,
				new BeanProcessor(TaxReportNewQcInitVO.class));

		return initvo;
	}

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "重庆";
	}
}
