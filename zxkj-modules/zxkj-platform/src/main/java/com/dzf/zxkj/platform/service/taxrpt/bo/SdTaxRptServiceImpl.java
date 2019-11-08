package com.dzf.zxkj.platform.service.taxrpt.bo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.model.gl.jiangsutaxrpt.TaxRptConst;
import com.dzf.model.gl.shandong.deal.TaxParamUtils;
import com.dzf.model.gl.shandong.taxrpt.TaxConst;
import com.dzf.model.gl.shandong.taxrpt.TaxQcQueryVO;
import com.dzf.model.gl.shandong.util.XMLUtils;
import com.dzf.model.gl.taxrpt.TaxReportVO;
import com.dzf.model.sys.sys_power.CorpTaxVo;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.gl.gl_pzgl.impl.ThreadImgPoolExecutorFactory;
import com.dzf.service.gl.taxrpt.shandong.ITaxCategoryService;
import com.dzf.service.gl.taxrpt.shandong.impl.TaxCategoryFactory;
import com.dzf.service.gl.taxrpt.shandong.impl.WebServiceProxy;
import com.dzf.service.spreadjs.SpreadTool;

// 山东地区
@Service("taxRptservice_shandong")
public class SdTaxRptServiceImpl extends DefaultTaxRptServiceImpl {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private TaxCategoryFactory taxFact;

	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		ITaxCategoryService taxCate = taxFact.produce(reportvo.getSb_zlbh());
		return taxCate.getCondition(pk_taxreport, userVO, reportvo);
	}

	@Override
	public Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map objMapReport, SpreadTool spreadtool,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {

		if ("true".equals(TaxParamUtils.ENABLED)) {
			ITaxCategoryService taxCate = taxFact.produce(reportvo.getSb_zlbh());
			taxCate.sendTaxReport(corpVO, objMapReport, spreadtool, reportvo, userVO);
			// 更新 reportvo 状态为已提交
			reportvo.setSbzt_dm(String.valueOf(TaxRptConst.iSBZT_DM_Submitted));
			sbo.update(reportvo, new String[] { "sbzt_dm" });
		} else {
			throw new BusinessException("当前地区不支持上报。");
		}
		return null;
	}

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		HashMap<String, Object> hmQCData = new HashMap<String, Object>();
		// try {
		// 调用初始化接口获取期初数据
		if ("true".equals(TaxParamUtils.ENABLED) &&reportvo.getSbzt_dm() =="101") {
			// 申报成功的不在获取期初
			ITaxCategoryService taxCate = taxFact.produce(reportvo.getSb_zlbh());
			hmQCData = taxCate.getQcData(corpvo, reportvo);
		}

		// } catch (Exception e) {
		// log.error("获取期初失败,"+e.getMessage(), e);
		// }
		return hmQCData;
	}

	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "山东";
	}

	public void getQcInfoData(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException {
		// 调用清册数据

		// 验证登录
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
		HashMap<String, String> map = WebServiceProxy.yzdlToSD(corpvo, taxvo);

		// 组装报文
		String yjsbBwXml = XMLUtils.createQcInfoXML(corpvo.getVsoccrecode());
		if (StringUtil.isEmpty(yjsbBwXml)) {
			throw new BusinessException("报文异常");
		}
		// 上报数据
		TaxQcQueryVO qcvo = new TaxQcQueryVO();
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QC);
		map = WebServiceProxy.sbqcCxToSD(corpvo, taxvo, map, yjsbBwXml, qcvo);
		// String xmlStr = map.get("xml");
		// System.out.println(xmlStr);
	}

	@Override
	public void getDeclareStatus(CorpVO corpvo, CorpTaxVo corptaxvo, TaxReportVO reportvo) throws DZFWarpException {
		// try {
		// 调用初始化接口获取期初数据
		if ("true".equals(TaxParamUtils.ENABLED)) {
			queryDeclareStatus(corpvo, corptaxvo, reportvo);
		}
	}

	private void queryDeclareStatus(final CorpVO corpvo, final CorpTaxVo corptaxvo, final TaxReportVO reportvo)
			throws DZFWarpException {
		// 先失眠500豪秒
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			log.error(e1.getMessage(), e1);
		}
		ThreadImgPoolExecutorFactory.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					ITaxCategoryService taxCate = taxFact.produce(reportvo.getSb_zlbh());
					taxCate.queryDeclareStatus(corpvo, corptaxvo, reportvo);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}

	// @Override
	// public TaxTypeListDetailVO[] getTaxTypeListDetailVO(boolean
	// showYearInTax, String yearmonth, CorpVO corpvo,
	// SingleObjectBO sbo) throws DZFWarpException {
	// List<TaxTypeListDetailVO> list = new ArrayList<TaxTypeListDetailVO>();
	//
	// // 增值税报表
	// addVatTaxReport(corpvo, yearmonth, list);
	// // 增加财报
	// addFinReport(corpvo, yearmonth, list);
	// // 所得税
	// addIncTax(corpvo, yearmonth, list);
	//
	// return list.toArray(new TaxTypeListDetailVO[0]);
	// }

	// protected void addFinReport(CorpVO corpvo, String yearmonth,
	// List<TaxTypeListDetailVO> list) {
	//
	// int month = Integer.parseInt(yearmonth.substring(5, 7));
	// if (month % 3 == 1) {
	// TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
	// if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
	// // 会计期间
	// detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC1);
	// } else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
	// // 会计期间
	// detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC2);
	// }
	// detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
	// detailvo.setPeriodto(getQuarterEndDate(yearmonth));
	// detailvo.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
	// detailvo.setPeriodtype(1);// 季报
	// if (StringUtil.isEmpty(detailvo.getSb_zlbh()))
	// return;
	// list.add(detailvo);
	// }
	// }

	// private void addVatTaxReport(CorpVO corpvo, String yearmonth,
	// List<TaxTypeListDetailVO> list) {
	// if (corpvo.getChargedeptname() != null &&
	// corpvo.getChargedeptname().startsWith("一般纳税人")) {
	// TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
	// detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH10101);
	// String sQueryPeriod = new DZFDate(yearmonth +
	// "-01").getDateBefore(1).toString().substring(0, 7);
	// detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
	// detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
	// detailvo.setZsxm_dm("01"); // 增值税
	// detailvo.setPeriodtype(0); // 月报
	// list.add(detailvo);
	// } else if (corpvo.getChargedeptname() != null &&
	// corpvo.getChargedeptname().startsWith("小规模纳税人")) {
	// int month = Integer.parseInt(yearmonth.substring(5, 7));
	// if (month % 3 == 1) {
	// TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
	// detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH10102);
	//
	// detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
	// detailvo.setPeriodto(getQuarterEndDate(yearmonth));
	// detailvo.setZsxm_dm("01"); // 增值税
	// detailvo.setPeriodtype(1); // 季报
	//
	// list.add(detailvo);
	// }
	//
	// String sQueryPeriod = new DZFDate(yearmonth +
	// "-01").getDateBefore(1).toString().substring(0, 7);
	// String periodBegin =
	// DateUtils.getPeriodStartDate(sQueryPeriod).toString();
	// String periodEnd = DateUtils.getPeriodEndDate(sQueryPeriod).toString();
	// TaxTypeListDetailVO addTax = new TaxTypeListDetailVO();
	// addTax.setSb_zlbh(TaxRptConst.SB_ZLBH1010201);
	// addTax.setPeriodfrom(periodBegin);
	// addTax.setPeriodto(periodEnd);
	// addTax.setZsxm_dm("01"); // 增值税
	// addTax.setPeriodtype(0); // 月报
	// list.add(addTax);
	// }
	// }

	// private void addIncTax(CorpVO corpvo, String yearmonth,
	// List<TaxTypeListDetailVO> list) {
	//
	// String periodBegin = getQuarterStartDate(yearmonth);
	// String periodEnd = getQuarterEndDate(yearmonth);
	// boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
	// if (showYearInTax) {
	// // 所得税年度汇算清缴
	// TaxTypeListDetailVO detailvo = new TaxTypeListDetailVO();
	// detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH_SETTLEMENT);
	// int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
	// detailvo.setPeriodfrom(year + "-01-01");
	// detailvo.setPeriodto(year + "-12-31");
	// detailvo.setZsxm_dm("04"); // 所得税
	// detailvo.setPeriodtype(2); // 年报
	// list.add(detailvo);
	// }
	//
	// int month = Integer.parseInt(yearmonth.substring(5, 7));
	// if (month % 3 == 1) {
	// TaxTypeListDetailVO intaxa1 = new TaxTypeListDetailVO();
	// intaxa1.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
	//
	// intaxa1.setPeriodfrom(periodBegin);
	// intaxa1.setPeriodto(periodEnd);
	// intaxa1.setZsxm_dm("04"); // 所得税
	// intaxa1.setPeriodtype(1); // 季报报
	//
	// list.add(intaxa1);
	//
	// // 一般纳税人，所得税季报，B类
	// TaxTypeListDetailVO intaxB1 = new TaxTypeListDetailVO();
	// intaxB1.setSb_zlbh(TaxRptConst.SB_ZLBH10413);
	// intaxB1.setPeriodfrom(periodBegin);
	// intaxB1.setPeriodto(periodEnd);
	// intaxB1.setZsxm_dm(TaxRptConst.SB_ZLBH10413); // 所得税
	// intaxB1.setPeriodtype(1); // 季报报
	// list.add(intaxB1);
	// }
	//
	// String sQueryPeriod = new DZFDate(yearmonth +
	// "-01").getDateBefore(1).toString().substring(0, 7);
	// periodBegin = DateUtils.getPeriodStartDate(sQueryPeriod).toString();
	// periodEnd = DateUtils.getPeriodEndDate(sQueryPeriod).toString();
	// TaxTypeListDetailVO intaxa0 = new TaxTypeListDetailVO();
	// intaxa0.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
	// intaxa0.setPeriodfrom(periodBegin);
	// intaxa0.setPeriodto(periodEnd);
	// intaxa0.setZsxm_dm("04"); // 所得税
	// intaxa0.setPeriodtype(0); // 月报
	// list.add(intaxa0);
	//
	// TaxTypeListDetailVO inTaxB0 = new TaxTypeListDetailVO();
	// inTaxB0.setSb_zlbh(TaxRptConst.SB_ZLBH10413);
	// inTaxB0.setPeriodfrom(periodBegin);
	// inTaxB0.setPeriodto(periodEnd);
	// inTaxB0.setZsxm_dm(TaxRptConst.SB_ZLBH10413); // 所得税
	// inTaxB0.setPeriodtype(0); // 月报
	// list.add(inTaxB0);
	//
	// }

}
