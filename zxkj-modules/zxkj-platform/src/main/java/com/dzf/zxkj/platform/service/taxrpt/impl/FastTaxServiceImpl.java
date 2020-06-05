package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.common.constant.ITaxRptStandFeeConst;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.taxrpt.IFastTaxService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import com.dzf.zxkj.platform.service.taxrpt.bo.RptBillFactory;
import com.dzf.zxkj.platform.service.zncs.image.IBalanceService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.taxrpt.TaxReportPath;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//import com.dzf.zxkj.platform.util.taxrpt.ChargeLock;

@Service
@Slf4j
public class FastTaxServiceImpl implements IFastTaxService {
	@Autowired
	private IUserService iuserService;
	@Autowired
	private SingleObjectBO sbo;
	@Autowired
	private ITaxDeclarationService taxDeclarationService;
	@Autowired
	private IVersionMngService versionServ;
	@Autowired
	private IBalanceService balanceServ;
	@Autowired
	private ICorpTaxService corpTaxService;
	@Autowired
	private IFctpubService fctPubServ;
	@Autowired
	private RptBillFactory rptbillfactory;
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	public FastTaxServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	private boolean checkAuthority(UserVO userVO, String pk_corp) {
		Set<String> corps = iuserService.querypowercorpSet(userVO.getCuserid());
		if (corps == null || !corps.contains(pk_corp)) {

			return false;
		}
		return true;
	}

	@Override
	public String getBsPeriod() throws DZFWarpException {
		// TODO Auto-generated method stub

		return new DZFDate().toString();
	}

	@Override
	public List<CorpVO> getCustomerList(String loginCorp, UserVO uservo, String userId) throws DZFWarpException {
		// TODO Auto-generated method stub
		return getCustomerList(loginCorp, uservo, userId, DZFBoolean.FALSE, null);
	}

	@Override
	public List<CorpVO> getCustomerList(String loginCorp, UserVO uservo, String userId, DZFBoolean isAll, Map<String, String> corpMap)
			throws DZFWarpException {
		// TODO Auto-generated method stub
		if (uservo == null || uservo.getPrimaryKey().equals(userId) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		List<CorpVO> listRet = new ArrayList<CorpVO>();

		List<CorpVO> list = iuserService.queryPowerCorpKj(uservo.getCuserid());
		if (list != null && list.size() > 0) {
			for (CorpVO corpVO : list) {
				String cityCountry = corpVO.getCitycounty();
				if (isAll == null || !isAll.booleanValue()) {// 是否包含全部
					
//					if (corpVO.getVprovince() == null 
//							|| !(corpVO.getVprovince() == 2 || corpVO.getVprovince() == 11 || corpVO.getVprovince() == 16
//									|| corpVO.getVprovince() == 23)) {//1北京  11 江苏  16 山东 23 重庆
//						continue;
//					}
					//应产品要求 报税地区为空的过滤掉
					CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpVO.getPk_corp());
					if(corptaxvo.getTax_area() == null){//报税地区
						continue;
					}
					
					if(corpMap != null && corpMap.size() > 0){
						if(!corpMap.containsKey(corpVO.getPk_corp())){//如果corpMap不为空，那么判断公司是否在corpMap里，不在直接把数据过滤掉
							continue;
						}
					}
					
				}

				try {
					corpVO.setUnitname(CorpSecretUtil.deCode(corpVO.getUnitname()));
					corpVO.setVcorporatephone(CorpSecretUtil.deCode(corpVO.getVcorporatephone()));// 法人电话

					corpVO.setPhone1(CorpSecretUtil.deCode(corpVO.getPhone1()));// 联系人电话

					// corpVO.setKeyname(CorpSecretUtil.deCode(corpVO.getKeyname()));//纳税人名称
					if (corpVO.getLegalbodycode() != null) {
						corpVO.setLegalbodycode(CorpSecretUtil.deCode(corpVO.getLegalbodycode()));
					}
					listRet.add(corpVO);
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}
		return listRet;

	}
	
	private Map<String, TaxTypeSBZLVO> queryzlvos(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[])sbo.queryByCondition(TaxTypeSBZLVO.class, " nvl(dr,0) =0 and pk_corp = ? ", sp);
		if(vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		Map<String,TaxTypeSBZLVO> map = DZfcommonTools.hashlizeObjectByPk(ltax, new String[]{"pk_taxsbzl"});
		return map;
	}
	
	private String getCategoryName(TaxReportVO reportvo, Map<String,TaxTypeSBZLVO> map){
		String name = "未定义";
		if(reportvo == null || map == null || map.size() == 0)
			return name;
		String pk_zl = reportvo.getPk_taxsbzl();
		if(!StringUtil.isEmpty(pk_zl)){
			TaxTypeSBZLVO vo = map.get(pk_zl);
			name = vo.getSbname();
		}else{
			String zlbh = reportvo.getSb_zlbh();
			for(TaxTypeSBZLVO v1 : map.values()){
				if(v1.getSbcode().equals(zlbh)){
					name = v1.getSbname();
					break;
				}
			}
		}
		return name;
	}

//	private String getCategoryName(String reportcode) {
//		if (TaxRptConst.SB_ZLBH10101.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH10101_NAME;
//		} else if (TaxRptConst.SB_ZLBH10102.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH10102_NAME;
//		} else if (TaxRptConst.SB_ZLBH50101.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH50101_NAME;
//		} else if (TaxRptConst.SB_ZLBH50102.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH50102_NAME;
//		} else if (TaxRptConst.SB_ZLBH10412.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH10412_NAME;
//		} else if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBHA_NAME;
//		} else if (TaxRptConst.SB_ZLBHC1.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBHC1_NAME;
//		} else if (TaxRptConst.SB_ZLBHC2.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBHC2_NAME;
//		} else if (TaxRptConst.SB_ZLBH10601.equals(reportcode)) {
//			return ITaxReportConst.SB_ZLBH10601_NAME;
//		} else if (TaxRptConst.SB_ZLBHD1.equals(reportcode)) {
//			return ITaxReportConst.SB_ZS_YHS;
//		} else if (TaxRptConst.SB_ZLBH39801.equals(reportcode)
//				|| TaxRptConst.SB_ZLBH39806.equals(reportcode)){//财报年报
//			return ITaxReportConst.SB_ZLBHCBNB_NAME;
//		}// 重庆新增编码
//		else if (CqtcZLBHConst.SB_ZLBH1010201.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH1010201_NAME;
//		} 	else if (CqtcZLBHConst.SB_ZLBH10413.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH10413_NAME;
//		} else if (CqtcZLBHConst.SB_ZLBH1041301.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH1041301_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBH1041201.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH1041201_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBH10415.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH10415_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBH1041501.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH1041501_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBH10414.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH10414_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBH1041401.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBH1041401_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC101.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC101_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC201.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC201_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC301.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC301_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC401.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC401_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC3.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC3_NAME;
//		}else if (CqtcZLBHConst.SB_ZLBHC4.equals(reportcode)) {
//			return CqtcZLBHConst.SB_ZLBHC4_NAME;
//		}
//		return "未定义";
//	}

	@Override
	public List<Map<String, Object>> getBsReportList(String loginCorp, UserVO uservo, String customerId, String period)
			throws DZFWarpException {
		// TODO Auto-generated method stub
		if (checkAuthority(uservo, customerId) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
//		SQLParameter params = new SQLParameter();
		// DZFDate preMonthLastDate = new DZFDate(period +
		// "-01").getDateBefore(1);

		// params.addParam(customerId);
		// params.addParam(preMonthLastDate.toString());
		// params.addParam(TaxRptConst.IBILLSTATUS_UNAPPROVE);
		// TaxReportVO[] reportvos = (TaxReportVO[])
		// sbo.queryByCondition(TaxReportVO.class,
		// "nvl(dr,0)=0 and pk_corp= ? and periodto=? and vbillstatus<>?",
		// params);
		// TaxReportVO[] reportvos = getTaxReport(customerId, period);

		TaxReportVO[] reportvos = getTaxReport_New(customerId, period, uservo.getPrimaryKey());
		List<Map<String, Object>> listRpt = new ArrayList<Map<String, Object>>();
		if (reportvos != null && reportvos.length > 0) {
			Map map = null;
			Map<String,TaxTypeSBZLVO> zlvomap = queryzlvos();
			for (TaxReportVO reportvo : reportvos) {

//				params = new SQLParameter();
//				params.addParam(reportvo.getPrimaryKey());
//				TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=? and nvl(dr,0) = 0 order by orderno", params);
				
				TaxReportDetailVO[] detailvos = (TaxReportDetailVO[])reportvo.getChildren();

				if (detailvos == null || detailvos.length == 0)
					continue;

				map = new HashMap();
				map.put("rptGroupId", reportvo.getPk_taxreport());
				map.put("categoryCode", reportvo.getSb_zlbh());
				map.put("categoryName", getCategoryName(reportvo,zlvomap));
				map.put("sbzq", reportvo.getPeriodtype());// 申报周期
				map.put("sbzt", reportvo.getSbzt_dm());// 申报状态
				map.put("period", reportvo.getPeriod());//申报期间
				if (reportvo.getRegion_extend2() != null) {
					map.put("yzpzh", reportvo.getRegion_extend2());// 应征凭证号
				}
				if (reportvo.getTaxmny() != null) {
					map.put("taxmny", reportvo.getTaxmny());// 税额
				}
				map.put("doperatedate", reportvo.getDoperatedate().toString());// 日期
				if (detailvos != null && detailvos.length > 0) {
					if (StringUtil.isEmpty(detailvos[0].getSpreadfile())) {
						map.put("tbzt", "未填写");// 填报状态
					} else {
						map.put("tbzt", "已填写");// 填报状态
					}
				} else {
					map.put("tbzt", "未填写");// 填报状态
				}

				List<Map<String, Object>> listRptdetails = new ArrayList<Map<String, Object>>();
				for (TaxReportDetailVO detailvo : detailvos) {
					Map mapdetail = new HashMap();
					mapdetail.put("reporttempletId", detailvo.getPk_taxrpttemplet());
					mapdetail.put("reportdetailId", detailvo.getPk_taxreportdetail());
					mapdetail.put("reportId", detailvo.getReportcode());
					mapdetail.put("reportName", detailvo.getReportname());
					//主表、附表等
					mapdetail.put("reportType",
							((!detailvo.getSb_zlbh().startsWith("C") && (detailvo.getReportcode().endsWith("001")
									|| detailvo.getReportcode().endsWith("100000")))
									|| (detailvo.getSb_zlbh().startsWith("C")
											&& (detailvo.getReportcode().endsWith("C2002")
													|| detailvo.getReportcode().endsWith("C1002")))) ? "1" : "2");
					mapdetail.put("bsOrder", detailvo.getOrderno());
					listRptdetails.add(mapdetail);
				}
				map.put("reportList", listRptdetails);
				listRpt.add(map);
			}
		}

		return listRpt;
	}

//	private TaxReportVO[] getTaxReport(String customerId, String period) {
//		DZFDate preMonthLastDate = new DZFDate(period + "-01").getDateBefore(1);
//		Integer preYear = Integer.parseInt(period.substring(0, 4)) - 1;// 期间上一年度
//		Integer currMonth = Integer.parseInt(period.substring(5, period.length()));// 当前月
//		DZFDate preYearLastDate = new DZFDate(preYear + "-12-31");// 上年最后一天
//
//		SQLParameter sp = new SQLParameter();
//
//		StringBuffer sf = new StringBuffer();
//		sf.append(" nvl(dr,0) = 0 and pk_corp = ? and ( vbillstatus<> ? or location = ?) and ( ");// and
//																									// periodto=?
//		sp.addParam(customerId);
//		sp.addParam(TaxRptConst.IBILLSTATUS_UNAPPROVE);
//		sp.addParam("北京");
//
//		if (currMonth <= 5) {// 5月31前可以填报汇算清缴
//			sf.append(" (periodto= ? and sb_zlbh = ? ) or  ");
//			sp.addParam(preYearLastDate);
//			sp.addParam(TaxRptConst.SB_ZLBH_SETTLEMENT);
//		}
//
//		sf.append(" (periodto= ? and sb_zlbh <> ? ) ) ");
//		sp.addParam(preMonthLastDate);
//		sp.addParam(TaxRptConst.SB_ZLBH_SETTLEMENT);
//
//		TaxReportVO[] reportvos = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, sf.toString(), sp);
//
//		return reportvos;
//
//	}

	private TaxReportVO[] getTaxReport_New(String customerId, String period, String userId) {
		DZFDate preMonthLastDate = new DZFDate(period + "-01").getDateBefore(1);

		UserVO uservo = iuserService.queryUserJmVOByID(userId);
		List<TaxReportVO> list = taxDeclarationService.initGetTypeList(customerId, uservo, period, userId,
				preMonthLastDate.toString());

		if (list == null || list.size() == 0) {
			return new TaxReportVO[0];
		}
		
//		List<TaxReportVO> list = new ArrayList<>();
//		for (TaxTypeListDetailVO detail : vos) {
//			TaxReportVO vo = new TaxReportVO();
//			vo.setCoperatorid(userId);
//			vo.setPk_corp(customerId);
//			vo.setPk_corp_account(customerId); // 代账公司pk_corp
//			TaxReportVO[] reportvos = taxDeclarationService.getTaxReportVO(vo, uservo,
//					detail.getPk_taxtypelistdetail());
//			if (reportvos != null && reportvos.length > 0) {
//				list.addAll(Arrays.asList(reportvos));
//			}
//		}

		return list.toArray(new TaxReportVO[list.size()]);

	}

	@Override
	public String getBsReportDetail(String loginCorp, UserVO uservo, String customerId, String rptGroupId)
			throws DZFWarpException {
		// TODO Auto-generated method stub
		if (checkAuthority(uservo, customerId) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		String strRet = "无内容";
		SQLParameter params = new SQLParameter();
		params.addParam(rptGroupId);
		TaxReportDetailVO[] reportdetailvos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
				"pk_taxreport=? and nvl(dr,0)=0", params);
		if (reportdetailvos != null && reportdetailvos.length > 0) {

			TaxReportVO reportvo = (TaxReportVO) sbo.queryByPrimaryKey(TaxReportVO.class, rptGroupId);
			if (reportvo != null) {// 判断是否为空
				// String period = reportvo.getPeriodto().substring(0, 7);
				// //收费检查
				// DZFDouble mny = getTaxFee(customerId);
				// processShoufei(customerId, period, uservo.getPrimaryKey(),
				// mny);

				strRet = taxDeclarationService.getSpreadJSData(rptGroupId, uservo,null,true);
			}

		}
		return strRet;
	}

	public DZFDouble getTaxFee(String pk_corp) {
		DZFDouble mny = null;
		CorpVO corpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		String chargeDeptName = corpvo.getChargedeptname();
		//会计工厂
		String wtcorp = fctPubServ.getAthorizeFactoryCorp(new DZFDate(), pk_corp);
		String pkcorp = pk_corp;
		if(!StringUtil.isEmpty(wtcorp)){//是否委托会计工厂
			pkcorp = wtcorp;
		}else{
			pkcorp = queryCascadeCorps(pk_corp);//代账公司
			
		}
		//一键报税设置取金额
		SQLParameter param = new SQLParameter();
		DZFDate date = new DZFDate();
		param.addParam(pkcorp);
		param.addParam(date.toString());
		param.addParam(date.toString());
		BondedSetVO[] vos = (BondedSetVO[])sbo.queryByCondition(BondedSetVO.class, "pk_corp = ? and nvl(dr,0) = 0 and begindate <=? and enddate >=?", param);
		if(vos!=null && vos.length>0){
			if ("一般纳税人".equals(chargeDeptName)) {
				mny = vos[0].getGeneralamount();
			} else {
				mny = vos[0].getScaleamount();
			}
			
		}else{
			if ("一般纳税人".equals(chargeDeptName)) {
				mny = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_YBR);
			} else {
				mny = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_XGM);
			}
			
		}
		
		
		return mny;
	}

	/**
	 * 0-未填报 1-已填报 2-已上传（提交）申报
	 */
	@Override
	public void updateBsReportStatus(String loginCorp, UserVO uservo, String rptGroupId, String status,
			DZFBoolean isUpZero, DZFDouble taxMny) throws DZFWarpException {
		// TODO Auto-generated method stub
		SQLParameter params = new SQLParameter();
		int iStatus = Integer.parseInt(status);
		// params.addParam(iStatus == 0 ? 101 : (iStatus == 1 ? 0 : 2));
		int tStatus = iStatus == 0 ? 101 : (iStatus == 2 ? 0 : iStatus);// modify
																		// 2017-07-06
																		// 老数据参照上行
		params.addParam(tStatus);
		params.addParam(rptGroupId);

		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_taxreportdetail set sbzt_dm = ? where pk_taxreport = ? ");
		sbo.executeUpdate(sf.toString(), params);

		sf.delete(0, sf.length());
		params.clearParams();
		params.addParam(tStatus);
		sf.append(" update ynt_taxreport set sbzt_dm = ? ");
		if (taxMny != null) {
			sf.append(" , taxmny = ? ");
			params.addParam(taxMny);
		}
		if (isUpZero != null) {
			sf.append(" , sbzt_osb = ? ");
			params.addParam(isUpZero);
		}
		sf.append(" where pk_taxreport = ? ");
		params.addParam(rptGroupId);
		sbo.executeUpdate(sf.toString(), params);

	}

//	@Override
//	public void updateCustIndustryCode(String loginCorp, UserVO uservo, TaxCustomerInfoVO infovo)
//			throws DZFWarpException {
//
//		if (checkAuthority(uservo, infovo.getCustomerId()) == false) {
//			throw new BusinessException("对不起，您无操作权限！");
//		}
//
//		StringBuffer strb = new StringBuffer();
//		SQLParameter params = new SQLParameter();
//		strb.append(" update bd_corp set ");
//
//		int i = 0;
//		// industrycode 行业代码
//		if (!StringUtil.isEmpty(infovo.getIndustryCode())) {
//			strb.append(" industrycode = ?");
//			params.addParam(infovo.getIndustryCode());
//		}
//
//		// vprovince 地区
//		if (!StringUtil.isEmpty(infovo.getVprovince())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" vprovince = ?");
//
//			params.addParam(infovo.getVprovince());
//		}
//
//		// taxcode // 纳税人识别号
//
//		if (!StringUtil.isEmpty(infovo.getTaxcode())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" vsoccrecode = ?");
//
//			params.addParam(infovo.getTaxcode());
//		}
//		// chargedeptname 公司性质
//		if (!StringUtil.isEmpty(infovo.getChargedeptname())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" chargedeptname = ?");
//
//			params.addParam(infovo.getChargedeptname());
//		}
//		// legalbodycode 法人代表
//		if (!StringUtil.isEmpty(infovo.getLegalbodycode())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" legalbodycode = ?");
//
//			params.addParam(infovo.getLegalbodycode());
//		}
//		// isdsbsjg; 所得税报送机关(0-国税局、1-地税局)
//		if (!StringUtil.isEmpty(infovo.getIsdsbsjg())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" isdsbsjg = ?");
//
//			params.addParam(infovo.getIsdsbsjg());
//		}
//		// linkman2 联系人
//		if (!StringUtil.isEmpty(infovo.getLinkman2())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" linkman2 = ?");
//
//			params.addParam(infovo.getLinkman2());
//		}
//		// phone1 联系人电话
//		if (!StringUtil.isEmpty(infovo.getPhone1())) {
//			i++;
//			if (i > 0) {
//				strb.append(",");
//			}
//			strb.append(" phone1 = ?");
//
//			params.addParam(infovo.getPhone1());
//		}
//
//		params.addParam(infovo.getCustomerId());
//
//		strb.append(" where pk_corp=? ");
//
//		if (i > 0) {
//			int num = sbo.executeUpdate(strb.toString(), params);
//
//			if (num == 0) {
//				throw new BusinessException("客户不存在");
//			}
//		} else {
//			throw new BusinessException("参数不存在");
//		}
//	}

	@Override
	public void updateBsReportDetail(String loginCorp, UserVO uservo, String customerId, String rptcode, String isAdd,String sbzlbh,String sbzq)
			throws DZFWarpException {
		if (checkAuthority(uservo, customerId) == false) {
			throw new BusinessException("对不起，您无操作权限!");
		}
		if(StringUtil.isEmpty(sbzq)){
			throw new BusinessException("sbzq参数为空");
		}
		if(StringUtil.isEmpty(sbzlbh)){
			throw new BusinessException("sbzlbh参数为空");
		}
		if(StringUtil.isEmpty(rptcode)){
			throw new BusinessException("reportId参数为空");
		}
		boolean isAddFlag = false;
		if (StringUtil.isEmpty(isAdd) || !DZFBoolean.FALSE.toString().equals(isAdd.toUpperCase())) {
			isAddFlag = true;
		}
		//查询模板信息
		CorpTaxVo corptaxvo = corpTaxService.queryCorpTaxVO(customerId);
		//通过公司vo获取报税地区(地区、申报种类编号、报表编号、申报周期，这四个可以唯一)
		String dq = corpTaxService.queryTaxrpttmpLoc(corptaxvo);
		StringBuffer sf = new StringBuffer();
		sf.append(" select et.* from ynt_taxrpttemplet et ");
		sf.append(" join ynt_tax_sbzl zl on et.pk_taxsbzl = zl.pk_taxsbzl ");
		sf.append(" where et.location = ? and zl.sbcode = ? ");
		sf.append(" and zl.sbzq = ? and et.reportcode = ? ");
		sf.append(" and nvl(et.dr,0) = 0 and nvl(zl.dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(dq);
		sp.addParam(sbzlbh);
		sp.addParam(sbzq);
		sp.addParam(rptcode);
		TaxRptTempletVO rptvo = (TaxRptTempletVO) sbo.executeQuery(sf.toString(), sp,new BeanProcessor(TaxRptTempletVO.class));
		if (rptvo == null) {
			throw new BusinessException("当前报表编码没有对应的模板数据，请检查!");
		}
		String pk_taxrpttemplet = rptvo.getPk_taxrpttemplet();
		String sql;
		if (isAddFlag) {
			sql = " select 1 From ynt_taxrpt y Where y.pk_corp = ? and nvl(dr,0) = 0 and y.pk_taxrpttemplet = ? ";
			sp.clearParams();
			sp.addParam(customerId);
			sp.addParam(pk_taxrpttemplet);
			boolean ishave = sbo.isExists(customerId, sql, sp);
			if (ishave)
				return;
			// 赋值
			CorpTaxRptVO taxvo = new CorpTaxRptVO();
			taxvo.setPk_taxrpttemplet(rptvo.getPk_taxrpttemplet());
			taxvo.setTaxrptcode(rptvo.getReportcode());
			taxvo.setTaxrptname(rptvo.getReportname());
			taxvo.setPk_corp(customerId);
			sbo.insertVOWithPK(taxvo);
		} else {
			sql = " delete From ynt_taxrpt y Where y.pk_corp = ? and nvl(dr,0) = 0 and y.pk_taxrpttemplet = ? ";
			sp.clearParams();
			sp.addParam(customerId);
			sp.addParam(pk_taxrpttemplet);
			sbo.executeUpdate(sql, sp);
		}
	}

	@Override
	public void updateBsReportByZeroDeclare(String loginCorp, UserVO uservo, String loginDate, String customerId,
			String period, String sbzlbh, String status, DZFDouble taxMny) throws DZFWarpException {

		checkParamIsNull(
				new String[][] { { customerId, "客户" }, { period, "期间" }, { sbzlbh, "申报种类编号" }, { status, "状态" } });

		String errorMsg = null;

		if (TaxRptConst.SB_ZLBHGS.equals(sbzlbh)) {// 个税更新
			errorMsg = updateBsReportByGS(loginCorp, uservo, loginDate, customerId, period, sbzlbh, status, taxMny);
		} else {// 纳税申报零申报更新
			List<TaxReportVO> typevos = taxDeclarationService.initGetTypeList(customerId, uservo, period,
					uservo.getPrimaryKey(), loginDate);

			if(typevos == null || typevos.size() == 0){
				errorMsg = "查询填报清单失败:无待填报清单";
			}else{
				CorpVO corpvo = getCorpVO(customerId);
				CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(customerId);
				ITaxRptService rptService = rptbillfactory.produce(corptaxvo);
				
				for (TaxReportVO typevo : typevos) {
					if(sbzlbh.equals(typevo.getSb_zlbh())){
						rptService.processZeroDeclaration( typevo, corpvo,corptaxvo, sbo);
					
						updateBsReportStatus(loginCorp, uservo, typevo.getPk_taxreport(), status,
								DZFBoolean.TRUE, taxMny);
					}
					
				}
			}
			
			//zpm
			
//			TaxReportVO reportvo = null;
//			TaxReportVO[] reportvos = null;
//			if (typevos != null && typevos.length > 0) {
//				for (TaxTypeListDetailVO typevo : typevos) {
//					if (sbzlbh.equals(typevo.getSb_zlbh())) {
//						reportvo = new TaxReportVO();
//						reportvo.setCoperatorid(uservo.getPrimaryKey());
//						reportvo.setPk_corp(customerId);
//						// reportvo.setPk_corp_account(loginCorp);
//						reportvos = taxDeclarationService.getTaxReportVO(reportvo, uservo,
//								typevo.getPk_taxtypelistdetail());
//						if (reportvos != null) {
//							updateBsReportStatus(loginCorp, uservo, reportvos[0].getPk_taxreport(), status,
//									DZFBoolean.TRUE, taxMny);
//							break;
//						} else {
//							errorMsg = "更新报表状态失败：无申报表";
//						}
//					}
//				}
//			} else {
//				errorMsg = "查询填报清单失败:无待填报清单";
//			}
		}

		if (!StringUtil.isEmpty(errorMsg)) {
			throw new BusinessException(errorMsg);
		}

	}

	private String updateBsReportByGS(String loginCorp, UserVO uservo, String loginDate, String customerId,
			String period, String sbzlbh, String status, DZFDouble taxMny) throws DZFWarpException {
		String errorMsg = null;
		String sQueryPeriod = DateUtils.getPreviousPeriod(period);
		
		DZFDate startDate = DateUtils.getPeriodStartDate(sQueryPeriod);
		DZFDate endDate = DateUtils.getPeriodEndDate(sQueryPeriod);

		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" Select * ");
		sf.append("   From ynt_taxreport y ");
		sf.append("  Where y.pk_corp = ? ");
		sf.append("    and y.zsxm_dm = ? ");
		sf.append("    and y.sb_zlbh = ? ");
		sf.append("    and y.periodfrom = ? ");
		sf.append("    and y.periodto = ? ");
		sf.append("    and nvl(dr, 0) = 0 ");

		sp.addParam(customerId);
		sp.addParam(TaxRptConst.SB_ZLBHGS);
		sp.addParam(TaxRptConst.SB_ZLBHGS);
		sp.addParam(startDate);
		sp.addParam(endDate);
		TaxReportVO taxvo = (TaxReportVO) sbo.executeQuery(sf.toString(), sp, new BeanProcessor(TaxReportVO.class));

		int iStatus = transYjbsStatus(status);
		
		int dr = iStatus == TaxRptConst.iSBZT_DM_UnSubmit ? 1 : 0;//如果状态是未提交，则将该条记录做逻辑删除
		
		if (taxvo != null && !StringUtil.isEmpty(taxvo.getPrimaryKey())) {// 已存在
			List<String> fieldList = new ArrayList<String>();
			fieldList.add("dr");
			fieldList.add("sbzt_dm");
			
			taxvo.setDr(dr);
			taxvo.setSbzt_dm(iStatus + "");
			if (taxMny != null) {
				fieldList.add("taxmny");
				taxvo.setTaxmny(taxMny);
			}
			sbo.update(taxvo, fieldList.toArray(new String[fieldList.size()]));
		} else {
			CorpVO corpvo = getCorpVO(customerId);
			if(corpvo == null){
				throw new BusinessException("公司信息不存在");
			}
			taxvo = new TaxReportVO();
			taxvo.setCoperatorid(uservo.getPrimaryKey());
			taxvo.setPk_corp(customerId);
			taxvo.setLocation(null);
			taxvo.setZsxm_dm(TaxRptConst.SB_ZLBHGS);
			taxvo.setSb_zlbh(TaxRptConst.SB_ZLBHGS);
			taxvo.setPeriodfrom(startDate.toString());
			taxvo.setPeriodto(endDate.toString());
			taxvo.setDoperatedate(new DZFDate());
			taxvo.setSbzt_dm(iStatus + "");
			taxvo.setPeriodtype(PeriodType.monthreport);
			taxvo.setVbillstatus(TaxRptConst.IBILLSTATUS_UNAPPROVE);
			taxvo.setDoperatedate(new DZFDate());
			taxvo.setTaxmny(taxMny);
			taxvo.setDr(dr);
			
			//查询个税pk_tax_sbzl
			TaxTypeSBZLVO sbzlvo = getTaxTypeSBZL(taxvo);
			if(sbzlvo == null || StringUtil.isEmpty(sbzlvo.getPrimaryKey())){
				errorMsg = "未找到集团预制的申报种类，请联系管理员!";
			}else{
				taxvo.setPk_taxsbzl(sbzlvo.getPk_taxsbzl());//申报种类
				taxvo.setPeriod(sQueryPeriod);
			}
			
			// 处理代账会计公司
			if (corpvo != null && corpvo.getIsaccountcorp() != null && corpvo.getIsaccountcorp().booleanValue()) {
				taxvo.setPk_corp_account(loginCorp);
			} else {// 父公司就是代账公司或者代账公司的分支机构
				if (StringUtil.isEmpty(corpvo.getFathercorp())) {
					errorMsg = "公司 [" + corpvo.getUnitname() + "] 没有代账公司，不能报税!";
				} else {
					taxvo.setPk_corp_account(corpvo.getFathercorp());
				}
			}

			if (StringUtil.isEmpty(errorMsg)) {
				sbo.saveObject(customerId, taxvo);
			}
		}

		return errorMsg;
	}

	private TaxTypeSBZLVO getTaxTypeSBZL(TaxReportVO taxvo) throws DZFWarpException{
		String sql = "select * from ynt_tax_sbzl y Where y.sbcode = ? and y.sbzq = ? and y.zsxmcode = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(taxvo.getSb_zlbh());//申报种类编号
		sp.addParam(taxvo.getPeriodtype());
		sp.addParam(taxvo.getZsxm_dm());//征收项目代码
		
		TaxTypeSBZLVO sbzlvo = (TaxTypeSBZLVO) sbo.executeQuery(sql, sp, 
				new BeanProcessor(TaxTypeSBZLVO.class));
		
		return sbzlvo;
	}
	
	private CorpVO getCorpVO(String pk_corp) {
		CorpVO corpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, pk_corp);
		SuperVO[] corpvos = QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, new SuperVO[] { corpvo }, 1);
		return (CorpVO) corpvos[0];
	}

//	private String getLocation(CorpVO corpvo) {
//		if (corpvo.getVprovince() != null && corpvo.getVprovince() == 2) {
//			return "北京";
//		} else if (corpvo.getVprovince() != null && corpvo.getVprovince() == 11) {
//			return "江苏";
//		} else if (corpvo.getVprovince() != null && corpvo.getVprovince() == 16) {
//			return "山东";
//		} else {
//			return "通用";
//		}
//
//	}

	private int transYjbsStatus(String status) {
		int iStatus = Integer.parseInt(status);
		return iStatus == 0 ? 101 : (iStatus == 2 ? 0 : iStatus);
	}

	private void checkParamIsNull(String[][] params) {
		for (String[] arr : params) {
			if (StringUtil.isEmpty(arr[0]))
				throw new BusinessException(arr[1] + "不允许为空，请检查");
		}
	}


	//没有人调用，注掉
//	@Override
//	public String getBsReportTemplet(UserVO uservo, String loginCorp, String sb_zlbh, String[] repCodeArr,
//			String location) throws DZFWarpException {
//
////		checkParamIsNull(new String[][] { { sb_zlbh, "申报种类编号" },
////				{ repCodeArr == null || repCodeArr.length == 0 ? null : "$", "报表编码" } });
//		
//		
//
//		Boolean isReadonly = false;
//
//		String wherePart = SqlUtil.buildSqlForIn("reportcode", repCodeArr);
//
//		StringBuffer sf = new StringBuffer();
//		sf.append(" select * from ynt_taxrpttemplet ");
//		sf.append(" where nvl(dr,0) = 0 and pk_taxrpttemplet = ? and location = ? and ");
//		sf.append(wherePart);
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(sb_zlbh);
//		sp.addParam(location);
//
//		List<TaxRptTempletVO> taxList = (List<TaxRptTempletVO>) sbo.executeQuery(sf.toString(), sp,
//				new BeanListProcessor(TaxRptTempletVO.class));
//
//		if (taxList == null || taxList.size() == 0) {
//			return null;
//		}
//
//		// 同一申报种类编号的模板组
//		// List<String> listRptName = new ArrayList<String>();
//		// for(TaxRptTempletVO tvo : taxList){
//		// listRptName.add(tvo.getReportname());
//		// }
//
//		String sReturn = null;
//		try {
//			String spreadTemplet = taxList.get(0).getSpreadtemplet();
//			sReturn = taxDeclarationService.getReportTemplet(spreadTemplet);
//		} catch (BusinessException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new WiseRunException(e);
//		}
//
//		return sReturn;
//
//	}

	@Override
	public void updateBsReportRemark(String customerId, String rptGroupId, String remark) throws DZFWarpException {
		checkParamIsNull(new String[][] { { customerId, "公司" }, { rptGroupId, "申报表信息" }, { remark, "备注" } });

		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_taxreport y ");
		sf.append("    set y.remark = ? ");
		sf.append("  Where y.pk_corp = ? ");
		sf.append("    and y.pk_taxreport = ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(remark);
		sp.addParam(customerId);
		sp.addParam(rptGroupId);

		sbo.executeUpdate(sf.toString(), sp);

	}

	@Override
	public List<CorpVO> getBsReportVos(String period, List<CorpVO> corpList) throws DZFWarpException {

		if (corpList == null || corpList.size() == 0) {
			return null;
		}

		if (StringUtil.isEmpty(period)) {
			return corpList;
		}
		//向上推一个期间，例如 是 2018-01，向前推一个月 2017-12
		period = DateUtils.getPeriod(new DZFDate(period + "-01").getDateBefore(1));

		String[] pks = new String[corpList.size()];
		Map<String, List<TaxReportVO>> trMap = new HashMap<String, List<TaxReportVO>>();

		String pk_corp = null;
		for (int i = 0; i < corpList.size(); i++) {
			pk_corp = corpList.get(i).getPrimaryKey();
			pks[i] = pk_corp;
			trMap.put(pk_corp, new ArrayList<TaxReportVO>());
		}

		List<TaxReportVO> reportList = executeBsReportsVos(period, pks);

		pk_corp = null;
		List<TaxReportVO> tpList = null;
		if (reportList != null && reportList.size() > 0) {
			for (TaxReportVO rvo : reportList) {
				pk_corp = rvo.getPk_corp();
				if (trMap.containsKey(pk_corp)) {
					tpList = trMap.get(pk_corp);
					tpList.add(rvo);
				}
			}
		}

		pk_corp = null;
		tpList = null;
		for (CorpVO cvo : corpList) {
			pk_corp = cvo.getPrimaryKey();
			if (trMap.containsKey(pk_corp)) {
				tpList = trMap.get(pk_corp);
				if (tpList != null && tpList.size() > 0) {
					cvo.setChildren(tpList.toArray(new TaxReportVO[0]));
				}
			}
		}

		return corpList;
	}
	
	/**
	 * 查询当期填报清单
	 * @param pk_corps 公司
	 * @param period 当前日期所在期间的，前一期间
	 */
	private List<TaxReportVO> querytypeDetails(String[] pk_corps,String period){
		if(pk_corps == null || pk_corps.length == 0 || StringUtil.isEmpty(period))
			return null;
		SQLParameter params = new SQLParameter();
		params.addParam(period);
		//取上一年度，查询年度报表
		params.addParam(String.valueOf(Integer.valueOf(period.substring(0, 4))-1));
		StringBuffer sf = new StringBuffer();
		sf.append("  select t2.sbcode,t2.sbname,t2.sbzq periodtype ,t1.*  ");
		sf.append(" from ynt_taxreport t1 ");
		sf.append("    join ynt_tax_sbzl t2 on t1.pk_taxsbzl = t2.pk_taxsbzl ");
		sf.append("  where nvl(t1.dr,0)=0 and nvl(t2.dr,0) = 0 and ");
		sf.append(SqlUtil.buildSqlForIn("t1.pk_corp", pk_corps));
		sf.append("   and t1.period in(?,?) order by t1.pk_corp,t2.showorder ");
		List<TaxReportVO> list = (List<TaxReportVO>)sbo.executeQuery(sf.toString(), params,
				new BeanListProcessor(TaxReportVO.class));
		return list;
	}

	private List<TaxReportVO> executeBsReportsVos(String period, String[] pk_corps) {
		List<TaxReportVO> list1 = querytypeDetails(pk_corps,period);
		if(list1 == null || list1.size() == 0)
			return null;
		List<TaxReportVO> list3 = new ArrayList<TaxReportVO>();
		DZFDate date = new DZFDate();
		int year = date.getYear();
		DZFDate five31 = new DZFDate(String.valueOf(year)+"-05-31");
		for(TaxReportVO vo : list1){
			//特殊情况考虑---年报
			if(vo.getPeriodtype() == PeriodType.yearreport 
					&& date.compareTo(five31)>0){
				continue;
			}else{
				list3.add(vo);
			}
		}
		return list3;
//
//		boolean showYearInTax = Integer.valueOf(period.substring(5)) <= 6;// 判断期间是否需要展示年报
//
//		int year = Integer.valueOf(period.substring(0, 4)) - 1;// 上一年
//
//		String prePeriod = DateUtils.getPreviousPeriod(period);// 前一个月
//
//		boolean showMonthInTax = Integer.valueOf(prePeriod.substring(5, 7)) % 3 == 0;// 是否需要展示季报
//
//		DZFDate beginDate = DateUtils.getPeriodStartDate(prePeriod);
//		DZFDate endDate = DateUtils.getPeriodEndDate(prePeriod);
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(PeriodType.monthreport);
//		sp.addParam(beginDate);
//		sp.addParam(endDate);
//
//		StringBuffer sf = new StringBuffer();
//		sf.append(" Select * From ynt_taxreport y ");
//		sf.append(" Where nvl(dr, 0) = 0 and ");
//		sf.append(SqlUtil.buildSqlForIn("pk_corp", pks));
//		sf.append(" and ( ");
//		sf.append(" ( periodtype = ? and periodfrom = ? and periodto = ? ) ");
//
//		if (showMonthInTax) {
//			sf.append(" or ( periodtype = ? and periodfrom = ? and periodto = ? ) ");
//
//			prePeriod = DateUtils.getPreviousPeriod(prePeriod);// 前两个月
//			prePeriod = DateUtils.getPreviousPeriod(prePeriod);// 前三个月
//			DZFDate jiDate = DateUtils.getPeriodStartDate(prePeriod);
//
//			sp.addParam(PeriodType.jidureport);
//			sp.addParam(jiDate);
//			sp.addParam(endDate);
//		}
//
//		if (showYearInTax) {
//			sf.append(" or ( periodtype = ? and periodfrom = ? and periodto = ? ) ");
//
//			sp.addParam(PeriodType.yearreport);
//			sp.addParam(year + "-01-01");
//			sp.addParam(year + "-12-31");
//		}
//
//		sf.append(" ) ");
//		List<TaxReportVO> reportList = (List<TaxReportVO>) sbo.executeQuery(sf.toString(), sp,
//				new BeanListProcessor(TaxReportVO.class));
//
//		return reportList;
	}

	
	private List<TaxTypeSBZLVO> queryTypeSBZLVOs(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[])sbo.queryByCondition(TaxTypeSBZLVO.class, " nvl(dr,0) =0 and pk_corp = ? ", sp);
		if(vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		return ltax;
	}
	/**
	 * 按编号、申报周期，确定唯一值
	 * @return
	 */
	private Map<String,TaxTypeSBZLVO> getSbzlMapbycodezq(){
		List<TaxTypeSBZLVO> zlist = queryTypeSBZLVOs();
		//
		Map<String,TaxTypeSBZLVO> zlmap = DZfcommonTools.hashlizeObjectByPk(zlist, new String[]{"sbcode","sbzq"});
		return zlmap;
	}
	
	@Override
	public void updateBsReportQC(TaxReportNewQcInitVO initvo, UserVO uservo) throws DZFWarpException {

		String period = initvo.getPeriod();

		checkParamIsNull(new String[][] { { initvo.getPk_corp(), "公司" }, { period, "期间" } });

		String[][] rule = new String[][] {
				{ "jdsdsQc", String.valueOf(PeriodType.jidureport), TaxRptConst.SB_ZLBH10412 },
				{ "xgmzzsQc", String.valueOf(PeriodType.jidureport), TaxRptConst.SB_ZLBH10102 },
				{ "ybzzsQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH10101 },
				{ "ybrdzdsQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH50101 },
				{ "xgmdzdsQc", String.valueOf(PeriodType.jidureport), TaxRptConst.SB_ZLBH50102 },
				
				{ "xgmzzsybQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH1010201},//小规模增值税月报
				{ "xgmdzdsybQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH50102},//小规模代征地税月报
				{ "whsyjsQc", String.valueOf(PeriodType.jidureport), TaxRptConst.SB_ZLBH10601},//文化事业建设费季报
				{ "whsyjsybQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH10601},//文化事业建设费月报
				// 印花税月报
				{ "yhsQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBHD1},
				// 地方各项基金费
				{ "dfjfQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH_LOCAL_FUND_FEE},
				// 附加税
				{ "fjsQc", String.valueOf(PeriodType.monthreport), TaxRptConst.SB_ZLBH50101}
				
		};

		// 重置期间， 传入的期间上调一月，传11月，存的一般是10月的期末，在11月的月报表里加载
		String prePeriod = new DZFDate(period + "-01").getDateBefore(1).toString().substring(0, 7);
		initvo.setPeriod(prePeriod);
		// 纳税信息
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(initvo.getPk_corp());
		Object qcmap = null;
		String fileStr = null;
		String fileName = null;
		TaxReportNewQcInitVO qcvo = null;
		TaxDeclarationServiceImpl taximpl = new TaxDeclarationServiceImpl();
		String id = null;
		Map<String,TaxTypeSBZLVO> zlmap = getSbzlMapbycodezq();
		String periodtype = "";
		String sbcode = "";
		for (String[] arr : rule) {
			boolean createReportData = false;
			qcmap = initvo.getAttributeValue(arr[0]);
			if (qcmap == null) {
				continue;
			}
			if (qcmap instanceof Map) {
				if (qcmap instanceof Map && ((Map) qcmap).size() == 0) {
					continue;
				}
				fileStr = JsonUtils.serialize(qcmap);
			} else if (qcmap instanceof TaxReportQcSubVO) {
				TaxReportQcSubVO subVO = (TaxReportQcSubVO) qcmap;
				arr[1] = subVO.getPeriodtype() + "";
				fileStr = subVO.getQcdata();
				if(subVO.getCreateReportData() != null)
					createReportData = subVO.getCreateReportData();
			}

			periodtype = arr[1];
			sbcode = arr[2];
			if (sbcode.equals(TaxRptConst.SB_ZLBH50101) && !"一般纳税人".equals(corptaxvo.getChargedeptname())) {
				sbcode = TaxRptConst.SB_ZLBH50102;
			}

			TaxTypeSBZLVO vo  = zlmap.get(sbcode+","+periodtype);
			if(vo == null)
				continue;

			if (!StringUtil.isEmpty(fileStr)) {
				qcvo = queryReportQcByInit(initvo, vo.getPk_taxsbzl());// jdsdsQc、xgmzzsQc、ybzzsQc有且仅有一种

				if (qcvo == null) {
					qcvo = buildReportQcByInit(initvo, arr, uservo,vo.getPk_taxsbzl());
				} else if (!StringUtil.isEmpty(qcvo.getSpreadfile())) {
					taximpl.delTaxReportFile(qcvo.getSpreadfile());
				}

				qcvo.setPeriodtype(Integer.parseInt(arr[1]));

				fileName = TaxReportPath.taxReportPath + qcvo.getPk_corp() + "_" + qcvo.getPeriod() + "_"
						+ qcvo.getSb_zlbh() + "_" + qcvo.getPeriodtype() + ".ssjson";

				id = taximpl.uploadTaxReportFile(fileStr, fileName);

				qcvo.setSpreadfile(id);

				sbo.saveObject(qcvo.getPk_corp(), qcvo);;
			}
			if (createReportData) {
				createReportData(initvo.getPk_corp(), initvo.getPeriod(),
						vo.getPk_taxsbzl(), corptaxvo);
			}
		}

	}

	private TaxReportNewQcInitVO buildReportQcByInit(TaxReportNewQcInitVO paramvo, String[] arr, UserVO uservo,String pk_taxsbzl) {
		TaxReportNewQcInitVO vo = new TaxReportNewQcInitVO();
		vo.setPk_corp(paramvo.getPk_corp());
		vo.setPeriod(paramvo.getPeriod());
		vo.setPeriodtype(Integer.parseInt(arr[1]));
		vo.setSb_zlbh(arr[2]);
		vo.setCoperatorid(uservo.getPrimaryKey());
		vo.setDoperatedate(new DZFDate());
		vo.setPk_taxsbzl(pk_taxsbzl);

		return vo;
	}

	private TaxReportNewQcInitVO queryReportQcByInit(TaxReportNewQcInitVO paramvo, String pk_taxsbzl) {
		String sql = "Select * From ynt_taxreportnewqcinit t Where "
				+ " nvl(dr,0) = 0 and t.pk_corp = ? and t.period = ? and t.pk_taxsbzl = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(paramvo.getPeriod());
		sp.addParam(pk_taxsbzl);

		TaxReportNewQcInitVO queryvo = (TaxReportNewQcInitVO) sbo.executeQuery(sql, sp,
				new BeanProcessor(TaxReportNewQcInitVO.class));

		return queryvo;
	}

	private void createReportData(String pk_corp, String period,
								  String pk_taxsbzl, CorpTaxVo corptaxvo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_taxsbzl);
		String sql = " pk_corp = ? and period = ? and pk_taxsbzl = ? and nvl(dr, 0) = 0 ";
		TaxReportVO[] reportVOs = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, sql, sp);
		if (reportVOs != null && reportVOs.length > 0) {
			TaxReportVO reportVO = reportVOs[0];
			taxDeclarationService.createReportDataByTemplate(reportVO, corptaxvo);
		}
	}

	/**
	 * 收费接口
	 */
	public void processShoufei(String pk_corp, String period, String userid, DZFDouble fee) throws DZFWarpException {
		// 加锁控制
		String key = "charge_" + pk_corp + "_" + period;
//		ReentrantLock lock = ChargeLock.getInstance().get(key);
		log.info(key + "一键报税收费begin...");
		try {
			// 加锁
//			lock.lock();
			// 是否收费接口
			DZFBoolean isCharge = versionServ.isChargeByProduct(pk_corp, IDzfServiceConst.DzfServiceProduct_03);// 一键报税
			log.info("isCharge:" + isCharge);
			if (isCharge != null && isCharge.booleanValue()) {// 是
				CorpVO corpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
				DZFBoolean isUser = balanceServ.isAlreadyConsumption(IDzfServiceConst.DzfServiceProduct_03, period,
						pk_corp);
				log.info("isUser:" + isUser);
				if (isUser == null || !isUser.booleanValue()) {// 否 扣费业务处理
					DZFBalanceBVO bvo = new DZFBalanceBVO();
					bvo.setChangedcount(fee);// 使用金额
					bvo.setIsadd(1);// 减少
					bvo.setPk_corp(pk_corp);
					bvo.setPk_user(userid);//使用人
					bvo.setPeriod(period);// 期间
					bvo.setPk_corpkjgs(corpvo.getFathercorp());// 会计公司
					bvo.setPk_dzfservicedes(IDzfServiceConst.DzfServiceProduct_03);
					
					DZFDate dzfdate = new DZFDate(period + "-15");
					//查询委托公司
					String wtcorp = fctPubServ.getAthorizeFactoryCorp(dzfdate, pk_corp);
					if(StringUtil.isEmpty(wtcorp)){
						balanceServ.consumption(bvo);// 扣费
					}else{
						// 扣委托公司费用
						balanceServ.consumptionByFct(bvo, dzfdate);
					}
					
//					String pk_corp_yy = queryCascadeCorps(pk_corp);
//					pk_corp_yy = StringUtil.isEmpty(pk_corp_yy) ? pk_corp : pk_corp_yy;
//					List<DZFBalanceVO> balanceList = queryBanlanceVO(pk_corp_yy, IDzfServiceConst.DzfServiceProduct_03);
//
//					if (balanceList != null && balanceList.size() > 0) {
//						DZFBalanceBVO bvo = new DZFBalanceBVO();
//						bvo.setPk_balance(balanceList.get(0).getPk_balance());
//						bvo.setChangedcount(fee);// 使用金额
//						bvo.setIsadd(1);// 减少
//						bvo.setPk_corp(pk_corp);
//						bvo.setPeriod(period);// 期间
//						bvo.setPk_corpkjgs(corpvo.getFathercorp());// 会计公司
//						bvo.setPk_dzfservicedes(IDzfServiceConst.DzfServiceProduct_03);
//
//						balanceServ.consumption(bvo);// 扣费
//					} else {
//						throw new BusinessException("余额不足，重新选择报税公司或充值后再进行申报");
//					}

				}
				// 是 直接使用
			}
			// 否 相当于免费使用，直接使用
			log.info(key + "一键报税收费end...");
		} catch (Exception e) {
			log.error("错误",e);
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
//			if (lock != null) {
//				lock.unlock();
//			}
		}

	}
	private List<DZFBalanceVO> queryBanlanceVO(String pk_corp_yy, String pk_dzfservicedes) {
		String sql = "select * from DZF_BALANCE where pk_corp_yy = ? and pk_dzfservicedes=? and nvl(dr,0) = 0 ";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp_yy);
		sp.addParam(pk_dzfservicedes);

		List<DZFBalanceVO> list = (List<DZFBalanceVO>) sbo.executeQuery(sql, sp,
				new BeanListProcessor(DZFBalanceVO.class));

		return list;
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
		List<CorpVO> list = (List<CorpVO>) sbo.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() >= 2) {
			return list.get(list.size() - 2).getPk_corp();
		}
		return null;

	}
	
    /**
     * 一键报税使用
     */
    @Override
    public List<TaxRptTempletVO> queryCorpRptTempletVOBydq(String dq){
		String bsdq = "通用";
    	if(StringUtil.isEmpty(dq))
    		dq = bsdq;
    	dq = dq.substring(0, 2);
    	dq = queryTaxrpttmpLoc(dq);
		//加载模板
    	StringBuffer sf = new StringBuffer();
		sf.append(" select t2.sbcode,t2.sbname,t2.sbzq,t1.* from ynt_taxrpttemplet t1 ");
		sf.append(" join ynt_tax_sbzl t2 on t1.pk_taxsbzl = t2.pk_taxsbzl ");
		sf.append(" where location like ?  ");
		sf.append(" and nvl(t2.dr,0) = 0 and nvl(t1.dr,0) = 0 ");
		sf.append(" order by t2.showorder,t1.orderno ");
		SQLParameter params = new SQLParameter();
		params.addParam(dq+"%");
		List<TaxRptTempletVO> list1 = (List<TaxRptTempletVO>)sbo.executeQuery(sf.toString(), params, new BeanListProcessor(TaxRptTempletVO.class));
		params.clearParams();
		return list1;
    }
    
    /**
     * 开通报税的地区
     * @return
     */
	private List<String> queryFromKtBsdq(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String sql = " select distinct location dq from ynt_taxrpttemplet where pk_corp = ? and nvl(dr,0) = 0 ";
		List<String> list = (List<String>)sbo.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				while(rs.next()){
					list.add(rs.getString("dq"));
				}
				return list;
			}
		});
		return list;
	}
    
    private String queryTaxrpttmpLoc(String dq)throws DZFWarpException {
    	String bsdq = "通用";
    	if(StringUtil.isEmpty(dq))
    		return bsdq;
    	dq = dq.substring(0, 2);
    	List<String> list = queryFromKtBsdq();
    	for(String key : list){
    		if(dq.startsWith(key)){
    			bsdq = key;
    			break;
    		}
    	}
    	return bsdq;
    }

	@Override
	public List<CorpTaxVo> queryTaxCorpList(List<String> corps) throws DZFWarpException {
		if(corps == null || corps.size() ==0)
			return null;
		StringBuffer sbf = new StringBuffer();
		sbf.append(" select * from bd_corp_tax where nvl(dr,0) = 0 and  ");
		String in = SqlUtil.buildSqlForIn("pk_corp",corps.toArray(new String[0]));
		sbf.append(in);
		List<CorpTaxVo> list = (List<CorpTaxVo>)sbo.executeQuery(sbf.toString(), null, new BeanListProcessor(CorpTaxVo.class));
		return list;
	}
}