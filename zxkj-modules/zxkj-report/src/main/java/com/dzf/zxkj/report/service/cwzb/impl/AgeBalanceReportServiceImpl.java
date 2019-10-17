package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.AccountAgeVO;
import com.dzf.zxkj.platform.model.report.AgeBalanceVO;
import com.dzf.zxkj.platform.model.report.AgeDetailVO;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IAgeBalanceReportService;
import com.dzf.zxkj.report.service.cwzb.IAgeDetailReportService;
import com.dzf.zxkj.report.utils.AgeReportUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_rep_zlyeb")
public class AgeBalanceReportServiceImpl implements IAgeBalanceReportService {
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAgeDetailReportService gl_rep_zlmxb;

	@Reference(version = "1.0.0")
	private IZxkjPlatformService zxkjPlatformService;
	
	@Override
	public AgeReportResultVO query(AgeReportQueryVO param)
			throws DZFWarpException {
		List<List<String>> periodContens =  AgeReportUtil.queryPeriods(singleObjectBO, param.getAge_type(), param.getPk_age());
		List<String> periods = periodContens.get(0);
		param.setFzhs(!StringUtil.isEmpty(param.getAuaccount_type()));
		Map<String, AgeBalanceVO> resultMap = gl_rep_zlmxb.queryDetails(param, periods);
		if (resultMap.size() > 0) {
			calculatePeriods(resultMap, param, periods);
			setDefaultVal(resultMap, param);
		}
		List<AgeBalanceVO> rsList = new ArrayList<AgeBalanceVO>();
		rsList.addAll(resultMap.values());
		mergeResult(rsList);
		rsList = addParentAccount(rsList, param);
		order(rsList, param);
		//相同科目只第一个显示编码名称
//		Set<String> codeSet = new HashSet<String>();
//		for (AgeBalanceVO balance : rsList) {
//			String code = balance.getAccount_code();
//			if (code != null) {
//				if (codeSet.contains(code)) {
//					balance.setAccount_code(null);
//					balance.setAccount_name(null);
//				} else {
//					codeSet.add(balance.getAccount_code());
//				}
//			}
//		}
		AgeReportResultVO rs = new AgeReportResultVO();
		rs.setPeriods(periods);
		rs.setPeriod_names(periodContens.get(1));
		rs.setResult(rsList);
		return rs;
	}

	/** 根据日期计算期间 */
	private void calculatePeriods (Map<String, AgeBalanceVO> resultMap, AgeReportQueryVO param, List<String> periods) {
		
		for (AgeBalanceVO balance : resultMap.values()) {
			Map<String, DZFDouble> periodMny = new HashMap<String, DZFDouble>();
			balance.setPeriod_mny(periodMny);
			for (AgeDetailVO detail : balance.getDetails()) {
				DZFDate vch_date = detail.getVch_date();
				int days = AgeReportUtil.getDays(vch_date, param.getEnd_date());
				String period = AgeReportUtil.getPeriod(days, periods, param.getAge_type(), param.getAge_unit());
				detail.setDays(days);
				detail.setPeriod(period);
				if (periodMny.containsKey(period)) {
					DZFDouble mny = periodMny.get(period);
					mny = SafeCompute.add(mny, detail.getTotal_mny());
					periodMny.put(period, mny);
				} else {
					periodMny.put(period, detail.getTotal_mny());
				}
				
			}
			balance.setDetails(null);
		}
		
	}
	
	private void setDefaultVal (Map<String, AgeBalanceVO> resultMap, AgeReportQueryVO param) {
		Map<String, AuxiliaryAccountBVO> auMap = null;
		Map<String, YntCpaccountVO> accountMap = zxkjPlatformService.queryMapByPk(param.getPk_corp());
//		if (param.isFzhs())
		auMap = zxkjPlatformService.queryAuxiliaryAccountBVOMap(param.getPk_corp());
		for (String key : resultMap.keySet()) {
			AgeBalanceVO balance = resultMap.get(key);
			String[] keyArr = key.split(",");
			String pk_account = keyArr[0];
			if (pk_account.length() == 24) {
				YntCpaccountVO cpaccount = accountMap.get(pk_account);
				if (cpaccount != null) {
					balance.setAccount_code(cpaccount.getAccountcode());
					balance.setAccount_name(cpaccount.getAccountname());
				}
			} else {
				balance.setAccount_name("合计");
			}
			if (param.isFzhs()) {
				if (auMap.containsKey(balance.getPk_fzhsx())) {
					balance.setFzhsx(auMap.get(balance.getPk_fzhsx()).getName());
				} else {
					balance.setFzhsx(null);
				}
			}
			for (int i = 1; i <= 10; i++) {
				String fzKey = "fzhsx" + i;
				String pkFzhs = (String) balance.getAttributeValue(fzKey);
				if (pkFzhs != null) {
					AuxiliaryAccountBVO vo = auMap.get(pkFzhs);
					if (vo != null) {
						balance.setAttributeValue(fzKey, vo.getName());
					} else {
						balance.setAttributeValue(fzKey, null);
					}
				}
			}
		}
	}
	
	private List<AgeBalanceVO> mergeSameFzhs (List<AgeBalanceVO> rsList) {
		Map<String, AgeBalanceVO> map = new HashMap<String, AgeBalanceVO>();
		for (AgeBalanceVO ageBalanceVO : rsList) {
			String key = ageBalanceVO.getAccount_code() + ageBalanceVO.getPk_fzhsx();
			if (map.containsKey(key)) {
				AgeBalanceVO balance = map.get(key);
				Map<String, DZFDouble> periods = balance.getPeriod_mny();
				for (String period : ageBalanceVO.getPeriod_mny().keySet()) {
					DZFDouble totalMny = ageBalanceVO.getPeriod_mny().get(period);
					if (periods.containsKey(period)) {
						DZFDouble mny = periods.get(period);
						mny = SafeCompute.add(mny, totalMny);
						periods.put(period, mny);
					} else {
						periods.put(period, totalMny);
					}
				}
				balance.setPeriod_mny(periods);
				balance.setTotal_mny(SafeCompute.add(ageBalanceVO.getTotal_mny(), balance.getTotal_mny()));
			} else {
				map.put(key, ageBalanceVO);
			}
		}
		return new ArrayList<AgeBalanceVO>(map.values());
	}

	private List<AgeBalanceVO> mergeSameAccount (List<AgeBalanceVO> rsList, AgeReportQueryVO param) {
		Map<String, AgeBalanceVO> map = new HashMap<String, AgeBalanceVO>();
		for (AgeBalanceVO ageBalanceVO : rsList) {
			String key = ageBalanceVO.getAccount_code();
			if (map.containsKey(key)) {
				AgeBalanceVO balance = map.get(key);
				Map<String, DZFDouble> periods = balance.getPeriod_mny();
				for (String period : ageBalanceVO.getPeriod_mny().keySet()) {
					DZFDouble totalMny = ageBalanceVO.getPeriod_mny().get(period);
					if (periods.containsKey(period)) {
						DZFDouble mny = periods.get(period);
						mny = SafeCompute.add(mny, totalMny);
						periods.put(period, mny);
					} else {
						periods.put(period, totalMny);
					}
				}
				balance.setPeriod_mny(periods);
				balance.setTotal_mny(SafeCompute.add(ageBalanceVO.getTotal_mny(), balance.getTotal_mny()));
			} else {
				map.put(key, ageBalanceVO);
			}
		}
		return new ArrayList<AgeBalanceVO>(map.values());
	}
	
	@Override
	public List<YntCpaccountVO> queryAccount(String pk_corp)
			throws DZFWarpException {
		return AgeReportUtil.queryVerifyAccount(zxkjPlatformService.queryByPk(pk_corp));
	}
	
	private void mergeResult (List<AgeBalanceVO> rsList) {
		AgeBalanceVO sum = new AgeBalanceVO();
		Map<String, DZFDouble> sumPeriods = new HashMap<String, DZFDouble>();
		DZFDouble sumMny = DZFDouble.ZERO_DBL;
		for (Iterator<AgeBalanceVO> iterator = rsList.iterator(); iterator.hasNext();) {
			AgeBalanceVO balance = (AgeBalanceVO) iterator.next();
			if (balance.getTotal_mny().doubleValue() == 0) {
				//删除金额为0的数据
				iterator.remove();
			}
			for (String period : balance.getPeriod_mny().keySet()) {
				DZFDouble totalMny = balance.getPeriod_mny().get(period);
				//合计
				if (sumPeriods.containsKey(period)) {
					DZFDouble mny = sumPeriods.get(period);
					mny = SafeCompute.add(mny, totalMny);
					sumPeriods.put(period, mny);
				} else {
					sumPeriods.put(period, totalMny);
				}
			}
			sumMny = SafeCompute.add(sumMny, balance.getTotal_mny());
		}
		if (sumMny.doubleValue() != 0) {
			sum.setAccount_name("合计");
			sum.setPeriod_mny(sumPeriods);
			sum.setTotal_mny(sumMny);
			rsList.add(sum);
		}
	}
	
	//汇总到上级科目
	private List<AgeBalanceVO> addParentAccount (List<AgeBalanceVO> rsList, AgeReportQueryVO param) {
		List<YntCpaccountVO> parentAccounts = AgeReportUtil.queryVerifyAccountByCode(zxkjPlatformService.queryByPk(param.getPk_corp()), param.getAccount_code());
		if (parentAccounts.size() == 0) {
			if (param.isFzhs())
				rsList = mergeSameFzhs(rsList);
			return rsList;
		} else {
			rsList = mergeSameAccount(rsList, param);
		}
		Map<String, AgeBalanceVO> parents = new HashMap<String, AgeBalanceVO>();
		for (AgeBalanceVO balance : rsList) {
			String code = balance.getAccount_code();
			if (code != null) {
				for (YntCpaccountVO parentAccount : parentAccounts) {
					String parentCode = parentAccount.getAccountcode();
					if (code.startsWith(parentCode)) {
						AgeBalanceVO parentVO = null;
						Map<String, DZFDouble> parentPeriods = null;
						DZFDouble parentMny = null;
						if (parents.containsKey(parentCode)) {
							parentVO = parents.get(parentCode);
							parentPeriods = parentVO.getPeriod_mny();
							parentMny = parentVO.getTotal_mny();
						} else {
							parentVO = new AgeBalanceVO();
							parentVO.setAccount_code(parentCode);
							parentVO.setAccount_name(parentAccount.getAccountname());
							parentPeriods = new HashMap<String, DZFDouble>();
							parentMny = DZFDouble.ZERO_DBL;
							parents.put(parentCode, parentVO);
						}
						for (String period : balance.getPeriod_mny().keySet()) {
							DZFDouble totalMny = balance.getPeriod_mny().get(period);
							if (parentPeriods.containsKey(period)) {
								DZFDouble mny = parentPeriods.get(period);
								mny = SafeCompute.add(mny, totalMny);
								parentPeriods.put(period, mny);
							} else {
								parentPeriods.put(period, totalMny);
							}
						}
						parentMny = SafeCompute.add(parentMny, balance.getTotal_mny());
						parentVO.setPeriod_mny(parentPeriods);
						parentVO.setTotal_mny(parentMny);
					}
					
				}
			}
		}
		rsList.addAll(parents.values());
		return rsList;
	}

	private void order (List<AgeBalanceVO> rsList, AgeReportQueryVO param) {
		Collections.sort(rsList, new Comparator<AgeBalanceVO>() {
			@Override
			public int compare(AgeBalanceVO o1, AgeBalanceVO o2) {
				int cp = 0;
				//合计科目编码为空
				String code1 = o1.getAccount_code() == null ? "9999" : o1.getAccount_code();
				String code2 = o2.getAccount_code() == null ? "9999" : o2.getAccount_code();
				cp = code1.compareTo(code2);
				if (cp == 0) {
					String fzhs1 = o1.getFzhsx() == null ? "" : o1.getFzhsx();
					String fzhs2 = o2.getFzhsx() == null ? "" : o2.getFzhsx();
					cp = fzhs1.compareTo(fzhs2);
				}
				return cp;
			}
		});
	}
	
	@Override
	public AccountAgeVO[] queryAgeSetting(String pk_corp)
			throws DZFWarpException {
		String condition = " nvl(dr,0) = 0 and pk_corp = ? order by code ";
		SQLParameter params = new SQLParameter();
		params.addParam(IDefaultValue.DefaultGroup);
		return (AccountAgeVO[]) singleObjectBO.queryByCondition(AccountAgeVO.class, condition, params);
	}
}
