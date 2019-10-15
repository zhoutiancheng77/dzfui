package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IIncomeWarningConstants;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.IncomeTaxVO;
import com.dzf.zxkj.platform.model.tax.SpecDeductHistVO;
import com.dzf.zxkj.platform.model.tax.TaxSettingVO;
import com.dzf.zxkj.platform.service.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.service.bdset.impl.IncomeWarningServiceImpl;
import com.dzf.zxkj.platform.service.report.ILrbQuarterlyReport;
import com.dzf.zxkj.platform.service.report.ILrbReport;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.base.query.QueryParamVO;

import java.util.List;
import java.util.Map;

public class IncomeTaxCalculator {
	private SingleObjectBO singleObjectBO;
	private ILrbQuarterlyReport lrbQuarterlyReport;
	private ILrbReport gl_rep_lrbserv;
	private IIncomeWarningService incomewarningserv;

	public IncomeTaxCalculator(SingleObjectBO singleObjectBO,
                               ILrbQuarterlyReport lrbQuarterlyReport,
                               ILrbReport gl_rep_lrbserv) {
		this.singleObjectBO = singleObjectBO;
		this.lrbQuarterlyReport = lrbQuarterlyReport;
		this.gl_rep_lrbserv = gl_rep_lrbserv;
	}
	
	public IncomeTaxCalculator(SingleObjectBO singleObjectBO,
                               ILrbQuarterlyReport lrbQuarterlyReport,
                               ILrbReport gl_rep_lrbserv,
                               IIncomeWarningService incomewarningserv) {
		this(singleObjectBO, lrbQuarterlyReport, gl_rep_lrbserv);
		this.incomewarningserv = incomewarningserv;
	}

	public DZFDouble calculateIncomeTax(String period, boolean isQuarter,
										CorpVO corpVO, CorpTaxVo taxInfo) throws DZFWarpException {
		String pk_corp = corpVO.getPk_corp();
		DZFDouble calTax = DZFDouble.ZERO_DBL;
		DZFDate jzdate = corpVO.getBegindate();
		if (jzdate == null)
			return calTax;

		// 是否为核定征收
		boolean isFixed = taxInfo.getTaxlevytype() != null
				&& taxInfo.getTaxlevytype() == 0;
		if (isFixed) {
			String beginPeriod = taxInfo.getSxbegperiod();
			String endPeriod = taxInfo.getSxendperiod();
			if (beginPeriod == null && endPeriod == null) {
				throw new BusinessException("核定征收生效期间为空，请到企业信息节点维护");
			} else if ( beginPeriod != null && period.compareTo(beginPeriod) < 0
					|| endPeriod != null && period.compareTo(endPeriod) > 0) {
				throw new BusinessException("计提期间不在核定征收生效期间内，请到企业信息节点维护");
			}
		}
		DZFDouble[] lrbInfo = getInfoFromLrb(pk_corp, period, isQuarter, isFixed, true);
		// 查账征收-利润总额，核定征收-营业收入
		DZFDouble currentIncomeMny = lrbInfo[0];
		DZFDouble yearIncomeMny = lrbInfo[1];
		// 已缴所得税
		DZFDouble paidTax = lrbInfo[2];
		DZFDate beginDate = new DZFDate(period + "-01");

		DZFDouble lossmny = DZFDouble.ZERO_DBL;
		if (!isFixed) {
			lossmny = queryLossmny(period, pk_corp);
		}

		// 个人所得税生产经营所得
		boolean isIndividual = taxInfo.getIncomtaxtype() != null
				&& taxInfo.getIncomtaxtype() == 1;
		if (isFixed) {
			// 应税所得率
			DZFDouble fixedRate = getFixedIncomeTaxRate(taxInfo.getIncometaxrate());
			if (isIndividual) {
				calTax = getIndividualIncomeTaxFixedRate(period,
						currentIncomeMny, yearIncomeMny, paidTax, fixedRate);
			} else {
				//优惠政策是否变动
				String yhzc = buildYhzc(taxInfo.getVyhzc(), period, pk_corp);
				calTax = getCorporateIncomeTaxFixedRate(period, currentIncomeMny,
						yearIncomeMny, paidTax, fixedRate, yhzc);
			}
		} else {
			if (isIndividual) {
				calTax = getIndividualIncomeTaxActual(pk_corp, taxInfo, period,
						currentIncomeMny, yearIncomeMny, lossmny, paidTax);
			} else {
				//优惠政策是否变动
				String yhzc = buildYhzc(taxInfo.getVyhzc(), period, pk_corp);
				calTax = getCorporateIncomeTaxActual(period, currentIncomeMny,
						yearIncomeMny, lossmny, paidTax, yhzc);
			}
		}
		return calTax;
	}
	
	//优惠政策是否要调整
	private String buildYhzc(String yhzc, String period, String pk_corp){
		if(!(StringUtil.isEmpty(yhzc) || "0".equals(yhzc)))//前期不是小微 不变更
			return yhzc;
		
		String[] periods = new IncomeWarningServiceImpl().getPeriodRangeSpe(period+"-01", 1);//1月 0,季 1,年 2,连续12月 3或者空
		IncomeWarningVO vo = new IncomeWarningVO();
		vo.setXmmc(IIncomeWarningConstants.QYZCZE);
		DZFDouble value = incomewarningserv.getSpecFsValue(periods[0], periods[1], pk_corp, vo);
		value = value == null ? DZFDouble.ZERO_DBL : value;
		//平均【资产总额】是否<=5000 万元：取计提当期该账套“资产负债表”的
		// 【资产合计】表项的（季初资产总额+季末资产总额）/2 是否<=5000 万元
		if(value.doubleValue() > 5000000){
			yhzc = "9";//不享受优惠
		}
		
		return yhzc;
	}

	/**
	 * 从利润表获取信息
	 *
	 * @param pk_corp
	 * @param period
	 * @param isQuarter
	 * @param isFixed
	 * @param containsPresentTax 包含当期所得税费用
	 * @return 本期收入，本年收入，已缴税额
	 */
	private DZFDouble[] getInfoFromLrb(String pk_corp, String period,
									   boolean isQuarter, boolean isFixed, boolean containsPresentTax) {
		// 查账征收-利润总额，核定征收-营业收入
		DZFDouble currentIncomeMny = null;
		DZFDouble yearIncomeMny = null;
		// 已缴所得税
		DZFDouble paidTax = DZFDouble.ZERO_DBL;
		if (isQuarter) {
			DZFDate beginDate = new DZFDate(period + "-01");
			QueryParamVO paramVO = new QueryParamVO();
			paramVO.setPk_corp(pk_corp);
			paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
			paramVO.setBegindate1(beginDate);
			paramVO.setEnddate(beginDate);
			paramVO.setQjq(period);
			paramVO.setQjz(period);
			LrbquarterlyVO[] vos = lrbQuarterlyReport
					.getLRBquarterlyVOs(paramVO);
			if (vos != null && vos.length > 0) {
				LrbquarterlyVO incomeVO = null;
				LrbquarterlyVO incomeOtherVO = null;
				LrbquarterlyVO taxVO = null;
				for (LrbquarterlyVO vo : vos) {
					if (isFixed
							&& ("一、营业收入".equals(vo.getXm()) || "一、主营业务收入"
							.equals(vo.getXm()))) {
						incomeVO = vo;
					} else if (isFixed
							&& "加：营业外收入".equals(vo.getXm())) {
						incomeOtherVO = vo;
					} else if (!isFixed
							&& ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm())
							|| "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm()) || "四、利润总额（亏损以“-”号填列）"
							.equals(vo.getXm()))) {
						incomeVO = vo;
					} else if ("减：所得税费用".equals(vo.getXm())
							|| "减：所得税".equals(vo.getXm())) {
						taxVO = vo;
					}
				}
				int mon = Integer.valueOf(period.substring(5, 7));
				yearIncomeMny = incomeVO.getBnlj();
				if (incomeOtherVO != null) {
					yearIncomeMny = SafeCompute.add(yearIncomeMny, incomeOtherVO.getBnlj());
				}
				DZFDouble presentTax = null;
				if (mon == 3) {
					currentIncomeMny = incomeVO.getQuarterFirst();
					presentTax = taxVO.getQuarterFirst();
					if (incomeOtherVO != null) {
						currentIncomeMny = SafeCompute.add(currentIncomeMny, incomeOtherVO.getQuarterFirst());
					}
				} else if (mon == 6) {
					currentIncomeMny = incomeVO.getQuarterSecond();
					presentTax = taxVO.getQuarterSecond();
					if (incomeOtherVO != null) {
						currentIncomeMny = SafeCompute.add(currentIncomeMny, incomeOtherVO.getQuarterSecond());
					}
				} else if (mon == 9) {
					currentIncomeMny = incomeVO.getQuarterThird();
					presentTax = taxVO.getQuarterThird();
					if (incomeOtherVO != null) {
						currentIncomeMny = SafeCompute.add(currentIncomeMny, incomeOtherVO.getQuarterThird());
					}
				} else if (mon == 12) {
					currentIncomeMny = incomeVO.getQuarterFourth();
					presentTax = taxVO.getQuarterFourth();
					if (incomeOtherVO != null) {
						currentIncomeMny = SafeCompute.add(currentIncomeMny, incomeOtherVO.getQuarterFourth());
					}
				}
				if (taxVO != null) {
					paidTax = SafeCompute.add(paidTax, taxVO.getBnlj());
					if (!containsPresentTax) {
						paidTax = SafeCompute.sub(paidTax, presentTax);
					}
				}
			}
		} else {
			Map<String, List<LrbVO>> map = gl_rep_lrbserv.getYearLrbMap(
					period.substring(0, 4), pk_corp, null, null, null);
			for (Map.Entry<String, List<LrbVO>> entry : map.entrySet()) {
				String lrbPeriod = entry.getKey();
				List<LrbVO> vos = entry.getValue();
				for (LrbVO vo : vos) {
					int periodCp = lrbPeriod.compareTo(period);
					if (periodCp == 0) {
						if (isFixed
								&& ("一、营业收入".equals(vo.getXm())
								|| "一、主营业务收入".equals(vo.getXm())
								|| "加：营业外收入".equals(vo.getXm()))) {
							currentIncomeMny = SafeCompute.add(currentIncomeMny, vo.getByje());
							yearIncomeMny = SafeCompute.add(yearIncomeMny, vo.getBnljje());
						} else if (!isFixed
								&& ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm())
								|| "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm()) || "四、利润总额（亏损以“-”号填列）"
								.equals(vo.getXm()))) {
							currentIncomeMny = vo.getByje();
							yearIncomeMny = vo.getBnljje();
						}
						if ("减：所得税费用".equals(vo.getXm())
								|| "减：所得税".equals(vo.getXm())) {
							paidTax = SafeCompute.add(paidTax, vo.getBnljje());
							if (!containsPresentTax) {
								paidTax = SafeCompute.sub(paidTax, vo.getByje());
							}
						}
					}
				}
			}
		}
		return new DZFDouble[] {currentIncomeMny, yearIncomeMny, paidTax};
	}
	// 企业所得税-查账征收
	private DZFDouble getCorporateIncomeTaxActual(String period, DZFDouble currentIncomeMny,
			DZFDouble yearIncomeMny, DZFDouble lossmny, DZFDouble paidTax, String yhzc) {
		DZFDouble calTax = DZFDouble.ZERO_DBL;
		if (currentIncomeMny != null && currentIncomeMny.doubleValue() > 0) {
			DZFDouble taxableIncome = SafeCompute.sub(yearIncomeMny, lossmny);
			calTax = calculateCorporateIncomeTax(taxableIncome, paidTax, period, yhzc);
		}
		return calTax;
	}

	// 企业所得税-核定应税所得率征收
	private DZFDouble getCorporateIncomeTaxFixedRate(String period,
													 DZFDouble currentIncomeMny, DZFDouble yearIncomeMny,
			DZFDouble paidTax, DZFDouble fixedRate, String yhzc) {
		DZFDouble calTax = DZFDouble.ZERO_DBL;
		if (currentIncomeMny != null && currentIncomeMny.doubleValue() > 0) {
			DZFDouble taxableIncome = SafeCompute.multiply(yearIncomeMny,
					fixedRate);
			taxableIncome = taxableIncome.setScale(2, DZFDouble.ROUND_HALF_UP);
			calTax = calculateCorporateIncomeTax(taxableIncome, paidTax, period, yhzc);
		}
		return calTax;
	}

	private DZFDouble calculateCorporateIncomeTax(DZFDouble taxableIncome,
			DZFDouble paidTax, String period, String yhzc) {
		DZFDouble calTax = SafeCompute.multiply(taxableIncome, new DZFDouble(0.25))
				.setScale(2, DZFDouble.ROUND_HALF_UP);
		if (yhzc == null || "0".equals(yhzc)) {
			// 符合小微企业优惠政策
			DZFDouble taxPreference = getTaxPreference(taxableIncome, period);
			calTax = SafeCompute.sub(calTax, taxPreference);
		}
		calTax = SafeCompute.sub(calTax, paidTax);
		return calTax;
	}
	// 获取减免税款
	private DZFDouble getTaxPreference(DZFDouble taxableIncome, String period) {
		DZFDouble taxPreference = DZFDouble.ZERO_DBL;
		if (taxableIncome != null && taxableIncome.doubleValue() > 0) {
			if(period.compareTo("2019-01") >= 0){
				// 2019新政策
				if (taxableIncome.doubleValue() <= 1000000) {
					taxPreference = SafeCompute.multiply(taxableIncome, new DZFDouble(0.2));
					taxPreference = taxPreference.setScale(2, DZFDouble.ROUND_HALF_UP);
				}else if(taxableIncome.doubleValue() <= 3000000){
					taxPreference = new DZFDouble(50000);
					taxPreference = taxPreference.add(taxableIncome.multiply(0.15)
							.setScale(2, DZFDouble.ROUND_HALF_UP));
				}
			}else{
				if (taxableIncome.doubleValue() <= 1000000) {
					taxPreference = SafeCompute.multiply(taxableIncome,
							new DZFDouble(0.15));
					taxPreference = taxPreference.setScale(2,
							DZFDouble.ROUND_HALF_UP);
				}
			}
		}
		return taxPreference;
	}

	// 个人所得税-查账征收
	private DZFDouble getIndividualIncomeTaxActual(String pk_corp, CorpTaxVo taxInfo,
			String period, DZFDouble currentIncomeMny, DZFDouble yearIncomeMny,
			DZFDouble lossmny, DZFDouble paidTax) {

		DZFDouble calTax = DZFDouble.ZERO_DBL;
		if (currentIncomeMny != null && currentIncomeMny.doubleValue() > 0) {
			DZFDouble taxableIncome = SafeCompute.sub(yearIncomeMny, lossmny);
			// 开始经营日期
			String beginPeriod = null;
			if (taxInfo.getBegprodate() == null) {
				throw new BusinessException("开始生产经营日期为空，请到企业信息节点维护");
			} else {
				beginPeriod = taxInfo.getBegprodate().toString();
			}
			// 投资者减除
			DZFDouble investorDeduction = getInvestorDeduction(beginPeriod,
					period);
			taxableIncome = SafeCompute.sub(taxableIncome, investorDeduction);
			if (period.compareTo("2019-01") >= 0) {
				IBDCorpTaxService sys_corp_tax_serv = (IBDCorpTaxService) SpringUtils
						.getBean("sys_corp_tax_serv");
				// 专项扣除
				DZFDouble specDeduction = getSpecDeduction(sys_corp_tax_serv.querySpecChargeHis(pk_corp, period),
						beginPeriod, period);
				taxableIncome = SafeCompute.sub(taxableIncome, specDeduction);
			}
			DZFDouble rate = getIndividualIncomeRate(period, taxableIncome);
			calTax = SafeCompute.multiply(taxableIncome, rate);
			calTax = calTax.setScale(2, DZFDouble.ROUND_HALF_UP);
			calTax = SafeCompute.sub(calTax,
					getQuickCalculationDeduction(period, taxableIncome));
			calTax = SafeCompute.sub(calTax, paidTax);
		}
		return calTax;
	}

	// 个人所得税-核定应税所得率征收
	private DZFDouble getIndividualIncomeTaxFixedRate(String period,
			DZFDouble currentIncomeMny, DZFDouble yearIncomeMny,
			DZFDouble paidTax, DZFDouble fixedRate) {
		DZFDouble calTax = DZFDouble.ZERO_DBL;
		if (currentIncomeMny != null && currentIncomeMny.doubleValue() > 0) {
			DZFDouble taxableIncome = SafeCompute.multiply(yearIncomeMny,
					fixedRate);
			taxableIncome = taxableIncome.setScale(2, DZFDouble.ROUND_HALF_UP);
			DZFDouble rate = getIndividualIncomeRate(period, taxableIncome);
			calTax = SafeCompute.multiply(taxableIncome, rate);
			calTax = calTax.setScale(2, DZFDouble.ROUND_HALF_UP);
			calTax = SafeCompute.sub(calTax,
					getQuickCalculationDeduction(period, taxableIncome));
			calTax = SafeCompute.sub(calTax, paidTax);
		}
		return calTax;
	}

	// 投资者减除费用
	public static DZFDouble getInvestorDeduction(String beginPeriod,
			String period) {
		int year = Integer.valueOf(period.substring(0, 4));
		int beginMon = 1;
		if (beginPeriod != null && beginPeriod.length() >= 7) {
			String beginYear = beginPeriod.substring(0, 4);
			String curYear = period.substring(0, 4);
			int cp = beginYear.compareTo(curYear);
			if (cp == 0) {
				// 开始生产经营日期在操作年，从开始经营月开始计算
				beginMon = Integer.valueOf(beginPeriod.substring(5, 7));
			} else if (cp > 0) {
				// 开始生产经营日期在操作日期后
				beginMon = 13;
			}
		}
		int endMon = Integer.valueOf(period.substring(5, 7));
		DZFDouble deduction = DZFDouble.ZERO_DBL;
		for (int i = beginMon; i <= endMon; i++) {
			DZFDouble perMonDeduction = getInvestorDeductionPerMon(year, i);
			deduction = deduction.add(perMonDeduction);
		}
		return deduction;
	}

	public static DZFDouble getInvestorDeductionPerMon(String period) {
		return new DZFDouble(period.compareTo("2018-10") < 0 ? 3500 : 5000);
	}

	public static DZFDouble getInvestorDeductionPerMon(int year, int mon) {
		return new DZFDouble((year < 2018 ||  year == 2018 && mon < 10) ? 3500 : 5000);
	}

	// 个人所得税适应税率
	public static DZFDouble getIndividualIncomeRate(String period,
			DZFDouble incomeMny) {
		double mny = incomeMny.doubleValue();
		DZFDouble rate = null;
		if (period.compareTo("2018-10") < 0) {
			if (mny <= 15000) {
				rate = new DZFDouble(0.05);
			} else if (mny <= 30000) {
				rate = new DZFDouble(0.1);
			} else if (mny <= 60000) {
				rate = new DZFDouble(0.2);
			} else if (mny <= 100000) {
				rate = new DZFDouble(0.3);
			} else {
				rate = new DZFDouble(0.35);
			}
		} else {
			if (mny <= 30000) {
				rate = new DZFDouble(0.05);
			} else if (mny <= 90000) {
				rate = new DZFDouble(0.1);
			} else if (mny <= 300000) {
				rate = new DZFDouble(0.2);
			} else if (mny <= 500000) {
				rate = new DZFDouble(0.3);
			} else {
				rate = new DZFDouble(0.35);
			}
		}
		return rate;
	}

	// 速算扣除数
	public static DZFDouble getQuickCalculationDeduction(String period,
			DZFDouble incomeMny) {
		double mny = incomeMny.doubleValue();
		DZFDouble deduction = null;
		if (period.compareTo("2018-10") < 0) {
			if (mny <= 15000) {
				deduction = new DZFDouble(0);
			} else if (mny <= 30000) {
				deduction = new DZFDouble(750);
			} else if (mny <= 60000) {
				deduction = new DZFDouble(3750);
			} else if (mny <= 100000) {
				deduction = new DZFDouble(9750);
			} else {
				deduction = new DZFDouble(14750);
			}
		} else {
			if (mny <= 30000) {
				deduction = new DZFDouble(0);
			} else if (mny <= 90000) {
				deduction = new DZFDouble(1500);
			} else if (mny <= 300000) {
				deduction = new DZFDouble(10500);
			} else if (mny <= 500000) {
				deduction = new DZFDouble(40500);
			} else {
				deduction = new DZFDouble(65500);
			}
		}
		return deduction;
	}

	public static DZFDouble getSpecDeduction(List<SpecDeductHistVO> hisList,
											 String beginPeriod, String period) {
		if (hisList == null || hisList.size() == 0) {
			return DZFDouble.ZERO_DBL;
		}

		String year = period.substring(0, 4);
		int beginMon = 1;
		if (beginPeriod != null && beginPeriod.length() >= 7) {
			String beginYear = beginPeriod.substring(0, 4);
			int cp = beginYear.compareTo(year);
			if (cp == 0) {
				// 开始生产经营日期在操作年，从开始经营月开始计算
				beginMon = Integer.valueOf(beginPeriod.substring(5, 7));
			} else if (cp > 0) {
				// 开始生产经营日期在操作日期后
				beginMon = 13;
			}
		}
		int endMon = Integer.valueOf(period.substring(5, 7));
		int hisIndex = 0;
		DZFDouble total = DZFDouble.ZERO_DBL;
		for (int i = endMon; i >= beginMon; i--) {
			String temPeriod = year + (i < 10 ? ("-0" + i) : ("-" + i));
			if (hisList.get(hisIndex).getBgperiod().compareTo(temPeriod) > 0) {
				hisIndex++;
				if (hisIndex == hisList.size()) {
					break;
				}
			}
			total = total.add(getToalDeduction(hisList, hisIndex));
		}
		return total;
	}

	private static DZFDouble getToalDeduction(List<SpecDeductHistVO> hisList, int index) {
		SpecDeductHistVO his = hisList.get(index);
		if (his.getZxkcxj() == null) {
			DZFDouble total = SafeCompute.add(his.getYanglaobx(), his.getYiliaobx());
			total = SafeCompute.add(total, his.getShiyebx());
			total = SafeCompute.add(total, his.getZfgjj());
			his.setZxkcxj(total);
		}
		return his.getZxkcxj();
	}

	private DZFDouble queryLossmny(String period, String copid)
			throws DZFWarpException {
		if (StringUtil.isEmpty(copid)) {
			throw new BusinessException("公司为空!");
		}
		if (period == null) {
			throw new BusinessException("期间为空!");
		}
		int year = Integer.valueOf(period.substring(0, 4));
		String condition = " nvl(dr,0)=0 and pk_corp = ? and period =? ";
		SQLParameter params = new SQLParameter();
		params.addParam(copid);
		params.addParam(year);
		QmLossesVO[] vos = (QmLossesVO[]) singleObjectBO.queryByCondition(
				QmLossesVO.class, condition, params);
		DZFDouble lossmny = null;
		if (vos != null && vos.length > 0) {
			lossmny= vos[0].getNlossmny();
		}
		if (lossmny == null) {
			lossmny = DZFDouble.ZERO_DBL;
		}
		return lossmny;
	}

	// 获取核定应税所得率
	private DZFDouble getFixedIncomeTaxRate(DZFDouble fixedRate) {
		if (fixedRate == null) {
			fixedRate = new DZFDouble(5);
		}
		fixedRate = fixedRate.div(100);
		return fixedRate;
	}

	public IncomeTaxVO getIncomeTaxVO(TaxSettingVO setting, String pk_corp, String period, boolean isQuarter) {
		IncomeTaxVO incomeTax = new IncomeTaxVO();
		incomeTax.setTax_type(setting.getIncomeTaxType());
		incomeTax.setTax_levy_type(setting.getIncomeTaxLevyType());
		boolean isFixed = setting.getIncomeTaxLevyType() != null
				&& setting.getIncomeTaxLevyType() == 0;
		boolean isIndividual = setting.getIncomeTaxType() != null
				&& setting.getIncomeTaxType() == 1;
		DZFDouble[] lrbData = getInfoFromLrb(pk_corp, period, isQuarter, isFixed, false);
		if (!isIndividual && !isFixed) {
			// 企业所得税查账征收
			// 利润总额
			incomeTax.setLrze(lrbData[1]);
			// 弥补亏损
			incomeTax.setMbks(queryLossmny(period, pk_corp));
			// 税率
			incomeTax.setRate(new DZFDouble(0.25));
			// 已缴税额
			incomeTax.setSjyjsds(lrbData[2]);
		} else if (!isIndividual && isFixed) {
			// 企业所得税 核定征收
			// 收入总额
			incomeTax.setSrze(lrbData[1]);
			// 核定税率
			incomeTax.setHdsdl(getFixedIncomeTaxRate(setting.getIncomeTaxFixedRate()));
			// 应纳税所得额
			incomeTax.setYnsde(SafeCompute.multiply(incomeTax.getSrze(), incomeTax.getHdsdl()));
			// 税率
			incomeTax.setRate(new DZFDouble(0.25));
			// 已缴税额
			incomeTax.setSjyjsds(lrbData[2]);
		} else if (isIndividual && isFixed) {
			// 个人所得税 核定征收
			// 收入总额
			incomeTax.setSrze(lrbData[1]);
			// 核定税率
			incomeTax.setHdsdl(getFixedIncomeTaxRate(setting.getIncomeTaxFixedRate()));
			// 已缴税额
			incomeTax.setSjyjsds(lrbData[2]);
		} else {
			// 个人所得税查账征收
			// 利润总额
			incomeTax.setLrze(lrbData[1]);
			// 弥补亏损
			incomeTax.setMbks(queryLossmny(period, pk_corp));
			// 开始生产经营日期
			String beginPeriod = setting.getStart_production_date() == null ? null
					: setting.getStart_production_date().toString();
			// 投资者减除费用
			DZFDouble investorDeduction = getInvestorDeduction(beginPeriod, period);
			incomeTax.setTzzjc(investorDeduction);
            IBDCorpTaxService sys_corp_tax_serv = (IBDCorpTaxService) SpringUtils
                    .getBean("sys_corp_tax_serv");
            // 专项扣除
            DZFDouble specDeduction = getSpecDeduction(sys_corp_tax_serv.querySpecChargeHis(pk_corp, period),
                    beginPeriod, period);
            incomeTax.setZxkc(specDeduction);
			// 已缴税额
			incomeTax.setSjyjsds(lrbData[2]);
		}
		return incomeTax;
	}
}
