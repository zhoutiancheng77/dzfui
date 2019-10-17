package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.report.AgeBalanceVO;
import com.dzf.zxkj.platform.model.report.AgeDetailVO;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IAgeDetailReportService;
import com.dzf.zxkj.report.utils.AgeReportUtil;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_rep_zlmxb")
public class AgeDetailReportServiceImpl implements IAgeDetailReportService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0")
	private IZxkjPlatformService zxkjPlatformService;
	
	@Override
	public AgeReportResultVO query(AgeReportQueryVO param)
			throws DZFWarpException {
		List<List<String>> periodContens =  AgeReportUtil.queryPeriods(singleObjectBO, param.getAge_type(), param.getPk_age());
		List<String> periods = periodContens.get(0);
		param.setFzhs(!StringUtil.isEmpty(param.getAuaccount_type()));
		Map<String, AgeBalanceVO> resultMap = queryDetails(param, periods);
		if (resultMap.size() > 0) {
			calculateDays(resultMap, param, periods);
			setDefaultVal(resultMap, param);
//			if (param.isFzhs())
//				resultMap = mergeResult(resultMap);
		}
		List<AgeDetailVO> rsList = mergeDetails(resultMap);
		addParentAccount(rsList, param);
		//根据科目编码排序，合计行在最后
		order(rsList, param);
		//相同科目只第一个显示编码名称
//		Set<String> codeSet = new HashSet<String>();
		for (AgeDetailVO balance : rsList) {
			String code = balance.getAccount_code();
			if (code != null) {
				if ("小计".equals(balance.getAccount_name()))
					balance.setAccount_code(null);
//				if (codeSet.contains(code)) {
//					balance.setAccount_code(null);
//				} else {
//					codeSet.add(balance.getAccount_code());
//				}
			}
		}
		AgeReportResultVO rs = new AgeReportResultVO();
		rs.setPeriods(periods);
		rs.setPeriod_names(periodContens.get(1));
		rs.setResult(rsList);
		return rs;
	}

	@Override
	public Map<String, AgeBalanceVO> queryDetails(AgeReportQueryVO param,
			List<String> periods) throws DZFWarpException {
		Map<String, FzhsqcVO> qcMap = queryQc(param);
		Map<String, AgeBalanceVO> resultMap = initResutByQc(qcMap, param, periods);
		List<TzpzBVO> vouchers = queryVerifyBegin(param);
		vouchers.addAll(queryVoucher(param));
		//核销科目
		verifyAccountByVoucher(resultMap, vouchers, param, periods);
		return resultMap;
	}
	
	private Map<String, AgeBalanceVO> initResutByQc(Map<String, FzhsqcVO> qcMap, AgeReportQueryVO param, List<String> periods) {
		HashMap<String, AgeBalanceVO> resultMap = new HashMap<String, AgeBalanceVO>();
		if (qcMap == null || qcMap.size() == 0)
			return resultMap;
		DZFDate startDate = DZFDate.getDate(DateUtils.getPeriod(param.getJz_date()) + "-01");
		for (String key : qcMap.keySet()) {
			FzhsqcVO qcvo = qcMap.get(key);
			AgeBalanceVO balance = new AgeBalanceVO();
			List<AgeDetailVO> details = new ArrayList<AgeDetailVO>();
			AgeDetailVO detail = new AgeDetailVO();
			detail.setTotal_mny(qcvo.getThismonthqc());
			detail.setVoucher_number("期初");
			detail.setVch_date(startDate);
			ReportUtil.copyFzPrimary(detail, qcvo);
			if (param.isFzhs())
				detail.setPk_fzhsx((String) qcvo.getAttributeValue(param.getAuaccount_type()));
			details.add(detail);
			if (param.isFzhs())
				balance.setPk_fzhsx((String) qcvo.getAttributeValue(param.getAuaccount_type()));
			balance.setTotal_mny(qcvo.getThismonthqc());
			ReportUtil.copyFzPrimary(balance, qcvo);
			balance.setDetails(details);
			resultMap.put(key, balance);
		}
		return resultMap;
	}

	/**
	 * 根据凭证核销科目
	 */
	private void verifyAccountByVoucher (Map<String, AgeBalanceVO> resultMap, List<TzpzBVO> vouchers, AgeReportQueryVO param, List<String> periods) {
		for (TzpzBVO voucher : vouchers) {
			int days = AgeReportUtil.getDays(voucher.getDoperatedate(), param.getEnd_date());
			String period = AgeReportUtil.getPeriod(days, periods, param.getAge_type(), param.getAge_unit());
			if (period == null)
				continue;
			if (voucher.getFzhsx6() == null && voucher.getPk_inventory() != null)
				voucher.setFzhsx6(voucher.getPk_inventory());
			String subjKey = voucher.getPk_accsubj() + ReportUtil.getFzKey(voucher);
//			String subjKey = param.isFzhs() ? voucher.getPk_accsubj() + "," + voucher.getAttributeValue(param.getAuaccount_type())
//					: voucher.getPk_accsubj();
			if (resultMap.containsKey(subjKey)) {
				AgeBalanceVO balance = resultMap.get(subjKey);
				verifyMny(voucher, balance, param);
			} else {
				AgeBalanceVO balance = new AgeBalanceVO();
				if (param.isFzhs())
					balance.setPk_fzhsx((String) voucher.getAttributeValue(param.getAuaccount_type()));
				ReportUtil.copyFzPrimary(balance, voucher);
				verifyMny(voucher, balance, param);
				resultMap.put(subjKey, balance);
			}
		}
	}
	
	/**
	 * 查询凭证数据
	 * @param param
	 * @return
	 */
	private List<TzpzBVO> queryVoucher(AgeReportQueryVO param) {
		SQLParameter sp = new SQLParameter();
		StringBuilder sb = new StringBuilder();
		sp.addParam(param.getPk_corp());
		sp.addParam(param.getAccount_code() + "%");
		sp.addParam(param.getEnd_date());
		sb.append(" select vch_b.pk_accsubj, vch_b.jfmny, vch_b.dfmny, account.direction as vdirect, vch_h.pzh, vch_b.pk_tzpz_h, ");
//		if (param.isFzhs())
//			sb.append(param.getAuaccount_type()).append(",");
		sb.append(" fzhsx1, fzhsx2, fzhsx3, fzhsx4, fzhsx5, fzhsx6, vch_b.pk_inventory, fzhsx7, fzhsx8, fzhsx9, fzhsx10, ");
		sb.append(" vch_h.doperatedate, account.accountcode as vcode from ynt_tzpz_b vch_b ");
		sb.append(" left join ynt_cpaccount account on ");
		sb.append(" (account.pk_corp_account = vch_b.pk_accsubj and account.pk_corp = vch_b.pk_corp ) ");
		sb.append(" left join ynt_tzpz_h vch_h on ");
		sb.append(" (vch_h.pk_tzpz_h = vch_b.pk_tzpz_h and vch_h.pk_corp = vch_b.pk_corp ) ");
		sb.append(" where vch_b.pk_corp = ? and account.accountcode like ? and nvl(vch_b.dr, 0) = 0 and vch_h.doperatedate <= ? ");
		sb.append(" and account.isverification = 'Y' ");
		if (!StringUtil.isEmpty(param.getAuaccount_type())) {
			if ("fzhsx6".equals(param.getAuaccount_type())) {
				sb.append("and (vch_b.").append(param.getAuaccount_type()).append(" is not null ");
				sb.append(" or vch_b.pk_inventory is not null ) ");
				if (!StringUtil.isEmpty(param.getAuaccount_detail())) {
					sp.addParam(param.getAuaccount_detail());
					sp.addParam(param.getAuaccount_detail());
					sb.append("and (vch_b.").append(param.getAuaccount_type()).append(" = ? ");
					sb.append(" or vch_b.pk_inventory = ? ) ");
				}
			} else {
				sb.append("and vch_b.").append(param.getAuaccount_type()).append(" is not null ");
				if (!StringUtil.isEmpty(param.getAuaccount_detail())) {
					sp.addParam(param.getAuaccount_detail());
					sb.append("and vch_b.").append(param.getAuaccount_type()).append(" = ? ");
				}
			}
		}
		sb.append(" order by vch_h.doperatedate, pzh, rowno ");
		List<TzpzBVO> rs = (List<TzpzBVO>) singleObjectBO.executeQuery(
				sb.toString(), sp, new BeanListProcessor(TzpzBVO.class));
		return rs;
	}

	/**
	 * 
	 * @param details
	 * @param type 0科目1辅助核算2全部辅助核算
	 */
	private void mergeSameVoucher (List<AgeDetailVO> details, int type) {
		if (details == null || details.size() == 0)
			return;
		Map<String, Integer> voucherMap = new HashMap<String, Integer>();
		List<Integer> removeLsit = new ArrayList<Integer>();
		for (int i = 0; i < details.size(); i++) {
			AgeDetailVO detail = details.get(i);
			String code = detail.getAccount_code();
			if (code != null) {
				String key = code;
				String pk_voucher = detail.getPk_voucher();
				if (pk_voucher == null) {
					if (detail.getVoucher_number() == null)
						key += detail.getAccount_name();//小计
					else
						key += detail.getVoucher_number();//期初
				} else {
					key += pk_voucher;
				}
				if (type == 1) {
					if (detail.getPk_fzhsx() != null) {
						key += detail.getPk_fzhsx();
					}
				} else if (type == 2) {
					key += ReportUtil.getFzKey(detail);
				}
				if (voucherMap.containsKey(key)) {
					AgeDetailVO preDetail = details.get(voucherMap.get(key));
					preDetail.setTotal_mny(SafeCompute.add(preDetail.getTotal_mny(), detail.getTotal_mny()));
					preDetail.setVerify_mny(SafeCompute.add(preDetail.getVerify_mny(), detail.getVerify_mny()));
					Map<String, DZFDouble> prePeriods = preDetail.getPeriod_mny();
					Map<String, DZFDouble> periods = detail.getPeriod_mny();
					for (String period : periods.keySet()) {
						if (prePeriods.containsKey(period)) {
							DZFDouble mny = SafeCompute.add(prePeriods.get(period), periods.get(period));
							prePeriods.put(period, mny);
						} else {
							prePeriods.put(period, periods.get(period));
						}
					}
					removeLsit.add(i);
				} else {
					voucherMap.put(key, i);
				}
			}
		}
		for (int i = removeLsit.size() - 1; i >= 0; i--) {
			details.remove(removeLsit.get(i).intValue());
		}
	}
	
	private Map<String, FzhsqcVO> queryQc(AgeReportQueryVO param) {
		SQLParameter sp = new SQLParameter();
		StringBuilder sb = new StringBuilder();

		sb.append(" select a.pk_accsubj, a.thismonthqc from ynt_qcye a ")
		.append(" left join ynt_verify_begin b on a.pk_accsubj = b.pk_accsubj ")
		.append(" where a.pk_accsubj in (").append(queryAccountSql(param, sp))
		.append(" and isfzhs = '0000000000') ")
		.append(" and b.pk_accsubj is null and a.pk_corp = ? and nvl(a.dr, 0) = 0 and a.thismonthqc is not null and a.thismonthqc <> 0 ");
		sp.addParam(param.getPk_corp());
		List<FzhsqcVO> rs = (List<FzhsqcVO>) singleObjectBO.executeQuery(
				sb.toString(), sp, new BeanListProcessor(FzhsqcVO.class));
		
		sb = new StringBuilder();
		sp.clearParams();
//			sb.append(" select pk_accsubj, thismonthqc, ").append(param.getAuaccount_type()).append(" from ynt_fzhsqc ");
		sb.append(" select a.pk_accsubj, a.thismonthqc, a.fzhsx1, a.fzhsx2, a.fzhsx3, a.fzhsx4, a.fzhsx5, ");
		sb.append(" a.fzhsx6, a.fzhsx7, a.fzhsx8, a.fzhsx9, a.fzhsx10  from ynt_fzhsqc a ")
		.append(" left join ynt_verify_begin b on a.pk_accsubj = b.pk_accsubj ");
		sb.append(" where a.pk_accsubj in (").append(queryAccountSql(param, sp))
		.append(" ) and b.pk_accsubj is null and a.pk_corp = ? and nvl(a.dr, 0) = 0 ");
		sb.append(" and a.thismonthqc is not null and a.thismonthqc <> 0 ");
		sp.addParam(param.getPk_corp());
		if (param.isFzhs()) {
			sb.append(" and a.").append(param.getAuaccount_type()).append(" is not null ");
			if (!StringUtil.isEmpty(param.getAuaccount_detail())) {
				sp.addParam(param.getAuaccount_detail());
				sb.append(" and a.").append(param.getAuaccount_type()).append(" = ? ");
			}
		}
		List<FzhsqcVO> fshsQc = (List<FzhsqcVO>) singleObjectBO.executeQuery(
				sb.toString(), sp, new BeanListProcessor(FzhsqcVO.class));
		rs.addAll(fshsQc);
		Map<String, FzhsqcVO> qcMap = mergeQc(rs, param.getAuaccount_type());
		return qcMap;
	}

	private Map<String, FzhsqcVO> mergeQc(List<FzhsqcVO> qcvos, String auaccount_type) {
		Map<String, FzhsqcVO> qcMap = new HashMap<String, FzhsqcVO>();
		for (FzhsqcVO qcVO : qcvos) {
//			String key = StringUtil.isEmpty(auaccount_type) ? qcVO.getPk_accsubj()
//					: qcVO.getPk_accsubj() + "," + qcVO.getAttributeValue(auaccount_type);
			String key = qcVO.getPk_accsubj() + ReportUtil.getFzKey(qcVO);
			if (qcMap.containsKey(key)) {
				FzhsqcVO vo = qcMap.get(key);
				DZFDouble qcmny = qcVO.getThismonthqc();
				vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), qcmny));
			} else {
				qcMap.put(key, qcVO);
			}
		}
		return qcMap;
	}

	private String queryAccountSql(AgeReportQueryVO param, SQLParameter sp) {
		sp.addParam(param.getPk_corp());
		sp.addParam(param.getAccount_code() + "%");
		String sql = "select pk_corp_account from ynt_cpaccount where "
				+ " pk_corp = ? and nvl(dr, 0) = 0 and isleaf = 'Y' "
				+ " and accountcode like ? and isverification = 'Y' ";
		return sql;
	}
	
	private  List<TzpzBVO> queryVerifyBegin(AgeReportQueryVO param) {
		SQLParameter sp = new SQLParameter();
		StringBuilder sb = new StringBuilder();
		sp.addParam(param.getPk_corp());
		sp.addParam(param.getAccount_code() + "%");
		sb.append(" select vbegin.pk_accsubj, vbegin.verify_mny as jfmny, '未核销期初' as pzh, vbegin.pk_verify_qc as pk_tzpz_h, ");
//		if (param.isFzhs())
//			sb.append(param.getAuaccount_type()).append(",");
		sb.append(" fzhsx1, fzhsx2, fzhsx3, fzhsx4, fzhsx5, fzhsx6, fzhsx7, fzhsx8, fzhsx9, fzhsx10, ");
		sb.append(" vbegin.occur_date as doperatedate, account.accountcode as vcode from ynt_verify_begin vbegin ");
		sb.append(" left join ynt_cpaccount account on ");
		sb.append(" (account.pk_corp_account = vbegin.pk_accsubj and account.pk_corp = vbegin.pk_corp ) ");
		sb.append(" where vbegin.pk_corp = ? and account.accountcode like ? and nvl(vbegin.dr, 0) = 0 ");
		sb.append(" and account.isverification = 'Y' ");
		if (!StringUtil.isEmpty(param.getAuaccount_type())) {
			sb.append("and vbegin.").append(param.getAuaccount_type()).append(" is not null ");
			if (!StringUtil.isEmpty(param.getAuaccount_detail())) {
				sp.addParam(param.getAuaccount_detail());
				sb.append("and vbegin.").append(param.getAuaccount_type()).append(" = ? ");
			}
		}
		sb.append(" order by vbegin.occur_date ");
		List<TzpzBVO> rs = (List<TzpzBVO>) singleObjectBO.executeQuery(
				sb.toString(), sp, new BeanListProcessor(TzpzBVO.class));
		return rs;
	}

	
	
	/**
	 * 核销金额
	 * @param entry
	 * @param balance
	 * @param param
	 */
	private void verifyMny (TzpzBVO entry, AgeBalanceVO balance, AgeReportQueryVO param) {
		DZFDouble mny = DZFDouble.ZERO_DBL;
		DZFDouble jfmny = entry.getJfmny();
		DZFDouble dfmny = entry.getDfmny();
		if (entry.getVdirect() == null) {
			mny = entry.getJfmny();
		} else {
			if (entry.getVdirect() == 0) {
				//借方科目
				mny = SafeCompute.sub(jfmny, dfmny);
			}
			if (entry.getVdirect() == 1) {
				//贷方科目
				mny = SafeCompute.sub(dfmny, jfmny);
			}
		}
		
		List<AgeDetailVO> details = balance.getDetails();
		if (details == null) {
			details = new ArrayList<AgeDetailVO>();
			balance.setDetails(details);
		}
		DZFDouble total = balance.getTotal_mny();
		total = SafeCompute.add(total, mny);
		balance.setTotal_mny(total);
		
		
		if (details.size() == 0) {
			mny = total;
			//记录分录
			AgeDetailVO detail = new AgeDetailVO();
			detail.setTotal_mny(mny);
			detail.setVoucher_number(entry.getPzh());
			detail.setVch_date(entry.getDoperatedate());
			detail.setPk_voucher(entry.getPk_tzpz_h());
			ReportUtil.copyFzPrimary(detail, entry);
			if (param.isFzhs())
				detail.setPk_fzhsx((String) entry.getAttributeValue(param.getAuaccount_type()));
			details.add(detail);
		} else {
			for (Iterator<AgeDetailVO> iterator = details.iterator(); iterator.hasNext();) {
				AgeDetailVO detail = iterator.next();
				DZFDouble detail_total = detail.getTotal_mny();
				if ((detail_total.doubleValue() >= 0) ^ (mny.doubleValue() < 0)) {
					//同号记录分录
					AgeDetailVO new_detail = new AgeDetailVO();
					new_detail.setTotal_mny(mny);
					new_detail.setVoucher_number(entry.getPzh());
					new_detail.setVch_date(entry.getDoperatedate());
					new_detail.setPk_voucher(entry.getPk_tzpz_h());
					ReportUtil.copyFzPrimary(new_detail, entry);
					if (param.isFzhs())
						new_detail.setPk_fzhsx((String) entry.getAttributeValue(param.getAuaccount_type()));
					details.add(new_detail);
					mny = null;
					break;
				} else {
					//异号冲销
					DZFDouble subVal = detail_total.add(mny);
					if (subVal.doubleValue() != 0 && (subVal.doubleValue() > 0) ^ (detail_total.doubleValue() < 0)) {
						//未冲销完当前明细，退出循环
						detail.setTotal_mny(subVal);
						detail.setVerify_mny(SafeCompute.add(detail.getVerify_mny(), mny.multiply(-1)));
						detail.setPk_verify_voucher(entry.getPk_tzpz_h());
						mny = null;
						break;
					} else {
						//冲销完当前明细, 移除明细
						iterator.remove();
						mny = subVal;
						//余额为零，退出循环
						if (mny.doubleValue() == 0) {
							break;
						}
						//继续冲销下笔
					}
				}
			}
			
			//仍有余额，记录明细
			if (mny != null && mny.doubleValue() != 0) {
				AgeDetailVO new_detail = new AgeDetailVO();
				new_detail.setTotal_mny(mny);
				new_detail.setVoucher_number(entry.getPzh());
				new_detail.setVch_date(entry.getDoperatedate());
				new_detail.setPk_voucher(entry.getPk_tzpz_h());
				ReportUtil.copyFzPrimary(new_detail, entry);
				if (param.isFzhs())
					new_detail.setPk_fzhsx((String) entry.getAttributeValue(param.getAuaccount_type()));
				details.add(new_detail);
			}
			
		}
	}

	/**
	 * 计算天数，所属期间
	 * @param resultMap
	 * @param param
	 * @param periods
	 */
	private void calculateDays (Map<String, AgeBalanceVO> resultMap, AgeReportQueryVO param, List<String> periods) {
		for (AgeBalanceVO balance : resultMap.values()) {
			Map<String, DZFDouble> periodMny = new HashMap<String, DZFDouble>();
			balance.setPeriod_mny(periodMny);
			for (AgeDetailVO detail : balance.getDetails()) {
				DZFDate vch_date = detail.getVch_date();
				int days = AgeReportUtil.getDays(vch_date, param.getEnd_date());
				String period = AgeReportUtil.getPeriod(days, periods, param.getAge_type(), param.getAge_unit());
				detail.setDays(days);
				detail.setPeriod(period);
				HashMap<String, DZFDouble> period_mny = new HashMap<String, DZFDouble>();
				period_mny.put(period, detail.getTotal_mny());
				detail.setPeriod_mny(period_mny);
			}
			
		}
	}

	/**
	 * 赋值，科目名称 辅助核算项名称
	 * @param resultMap
	 * @param param
	 */
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
				YntCpaccountVO cpaccount= accountMap.get(pk_account);
				if (cpaccount != null) {
					balance.setAccount_code(cpaccount.getAccountcode());
					balance.setAccount_name(cpaccount.getAccountname());
				}
			}
			if (param.isFzhs() && balance.getPk_fzhsx() != null) {
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
			
			if (balance.getDetails() != null) {
				for (AgeDetailVO detail : balance.getDetails()) {
					detail.setAccount_code(balance.getAccount_code());
					detail.setAccount_name(balance.getAccount_name());
					detail.setFzhsx(balance.getFzhsx());
					detail.setFzhsx1(balance.getFzhsx1());
					detail.setFzhsx2(balance.getFzhsx2());
					detail.setFzhsx3(balance.getFzhsx3());
					detail.setFzhsx4(balance.getFzhsx4());
					detail.setFzhsx5(balance.getFzhsx5());
					detail.setFzhsx6(balance.getFzhsx6());
					detail.setFzhsx7(balance.getFzhsx7());
					detail.setFzhsx8(balance.getFzhsx8());
					detail.setFzhsx9(balance.getFzhsx9());
					detail.setFzhsx10(balance.getFzhsx10());
				}
			}
		}
	}
	/** 合并辅助核算 */
	private Map<String, AgeBalanceVO> mergeResult (Map<String, AgeBalanceVO> resultMap) {
		Map<String, AgeBalanceVO> mergedMap = new HashMap<String, AgeBalanceVO>();
		for (AgeBalanceVO balance : resultMap.values()) {
			String code = balance.getAccount_code();
			if (mergedMap.containsKey(code)) {
				AgeBalanceVO pre = mergedMap.get(code);
				pre.setTotal_mny(SafeCompute.add(pre.getTotal_mny(), balance.getTotal_mny()));
				List<AgeDetailVO> preList = pre.getDetails();
				preList.addAll(balance.getDetails());
				pre.setDetails(preList);
			} else {
				mergedMap.put(code, balance);
			}
		}
		return mergedMap;
	}
	
	/**
	 * 合并明细VO 添加小计合计
	 * 
	 */
	private List<AgeDetailVO> mergeDetails (Map<String, AgeBalanceVO> resultMap) {
		List<AgeDetailVO> details = new ArrayList<AgeDetailVO>();
		AgeDetailVO sum = new AgeDetailVO();
		HashMap<String, DZFDouble> sumPeriods = new HashMap<String, DZFDouble>();
		DZFDouble sumMny = DZFDouble.ZERO_DBL;
		DZFDouble sumVerifyMny = DZFDouble.ZERO_DBL;
		HashMap<String, AgeDetailVO> subTotals = new HashMap<String, AgeDetailVO>();
		
		for (AgeBalanceVO balance : resultMap.values()) {
			if (balance.getTotal_mny().doubleValue() == 0)
				continue;
			String key = balance.getAccount_code() + ReportUtil.getFzKey(balance);
//					+ (balance.getPk_fzhsx() == null ? "" : balance.getPk_fzhsx());
			AgeDetailVO subTotal = null;
			HashMap<String, DZFDouble> subPeriods = null;
			DZFDouble subMny = null;
			DZFDouble subVerifyMny = null;
			if (subTotals.containsKey(key)) {
				subTotal = subTotals.get(key);
				subPeriods = subTotal.getPeriod_mny();
				subMny = subTotal.getTotal_mny();
				subVerifyMny = subTotal.getVerify_mny();
			} else {
				subTotal = new AgeDetailVO();
				subPeriods = new HashMap<String, DZFDouble>();
				subMny = DZFDouble.ZERO_DBL;
				subVerifyMny = DZFDouble.ZERO_DBL;
				ReportUtil.copyFzPrimary(subTotal, balance);
				subTotal.setPk_fzhsx(balance.getPk_fzhsx());
				subTotal.setAccount_name("小计");
				subTotal.setAccount_code(balance.getAccount_code());
				subTotals.put(key, subTotal);
			}
			for (AgeDetailVO detail : balance.getDetails()) {
				String period = detail.getPeriod();
				//小计
				if (subPeriods.containsKey(period)) {
					DZFDouble mny = subPeriods.get(period);
					mny = SafeCompute.add(mny, detail.getTotal_mny());
					subPeriods.put(period, mny);
				} else {
					subPeriods.put(period, detail.getTotal_mny());
				}
				//合计
				if (sumPeriods.containsKey(period)) {
					DZFDouble mny = sumPeriods.get(period);
					mny = SafeCompute.add(mny, detail.getTotal_mny());
					sumPeriods.put(period, mny);
				} else {
					sumPeriods.put(period, detail.getTotal_mny());
				}
				subMny = SafeCompute.add(subMny, detail.getTotal_mny());
				subVerifyMny = SafeCompute.add(subVerifyMny, detail.getVerify_mny());
				sumVerifyMny = SafeCompute.add(sumVerifyMny, detail.getVerify_mny());
			}
			//小计金额
			subTotal.setTotal_mny(subMny);
			subTotal.setVerify_mny(subVerifyMny);
			subTotal.setPeriod_mny(subPeriods);
			
			details.addAll(balance.getDetails());
//			details.add(subTotal);
			//合计金额
			sumMny = SafeCompute.add(sumMny, balance.getTotal_mny());
		}
		if (sumMny.doubleValue() != 0) {
			sum.setAccount_name("合计");
			sum.setPeriod_mny(sumPeriods);
			sum.setTotal_mny(sumMny);
			sum.setVerify_mny(sumVerifyMny);
			details.add(sum);
		}
		details.addAll(subTotals.values());
		return details;
	}
	/**
	 * 排序
	 * @param rsList
	 * @param param
	 */
	private void order (List<AgeDetailVO> rsList, AgeReportQueryVO param) {
		final boolean isfzhs = param.isFzhs();
		final boolean isParent = param.isParent();
		Collections.sort(rsList, new Comparator<AgeDetailVO>() {
			@Override
			public int compare(AgeDetailVO o1, AgeDetailVO o2) {
				int cp = 0;
				//合计科目编码为空
				String code1 = o1.getAccount_code() == null ? "9999" : o1.getAccount_code();
				String code2 = o2.getAccount_code() == null ? "9999" : o2.getAccount_code();
				cp = code1.compareTo(code2);
				if (cp == 0) {
					String date1 = o1.getVch_date() == null ? "9999" : o1.getVch_date().toString();
					String date2 = o2.getVch_date() == null ? "9999" : o2.getVch_date().toString();
					if (isParent) {
						cp = date1.compareTo(date2);
					} else {
						String key1 = isfzhs ? o1.getPk_fzhsx() : ReportUtil.getFzKey(o1);
						String key2 = isfzhs ? o2.getPk_fzhsx() : ReportUtil.getFzKey(o2);
						if (key1.equals(key2)) {
							cp = date1.compareTo(date2);
						} else {
							cp = key1.compareTo(key2);
						}
					}
					if (cp == 0) {
						String num1 = "期初".equals(o1.getVoucher_number()) ? "0000" : o1.getVoucher_number();
						String num2 = "期初".equals(o2.getVoucher_number()) ? "0000" : o2.getVoucher_number();
						cp = num1.compareTo(num2);
					}
				}
				return cp;
			}
		});
	}
	
	//汇总到上级科目
	private void addParentAccount (List<AgeDetailVO> rsList, AgeReportQueryVO param) {
		List<YntCpaccountVO> parentAccounts = AgeReportUtil.queryVerifyAccountByCode(zxkjPlatformService.queryByPk(param.getPk_corp()), param.getAccount_code());
		boolean isParent = parentAccounts.size() > 0;
		param.setParent(isParent);
		if (!isParent) {
			if (param.isFzhs()) {
				mergeSameVoucher(rsList, 1);
			} else {
				mergeSameVoucher(rsList, 2);
			}
			return;
		} else {
			mergeSameVoucher(rsList, 0);
		}
		List<AgeDetailVO> parents = new ArrayList<AgeDetailVO>();
		HashMap<String, AgeDetailVO> subTotals = new HashMap<String, AgeDetailVO>();
		for (AgeDetailVO ageDetailVO : rsList) {
			String code = ageDetailVO.getAccount_code();
			if (code != null) {
				if ("小计".equals(ageDetailVO.getAccount_name())) {
					for (YntCpaccountVO parentAccount : parentAccounts) {
						String parentCode = parentAccount.getAccountcode();
						if (code.startsWith(parentCode)) {
							AgeDetailVO subTotal = null;
							HashMap<String, DZFDouble> subPeriods = null;
							DZFDouble subMny = null;
							DZFDouble verifyMny = null;
							if (subTotals.containsKey(parentCode)) {
								subTotal = subTotals.get(parentCode);
								subPeriods = subTotal.getPeriod_mny();
								subMny = subTotal.getTotal_mny();
								verifyMny = subTotal.getVerify_mny();
								for (String period : ageDetailVO.getPeriod_mny().keySet()) {
									DZFDouble totalMny = ageDetailVO.getPeriod_mny().get(period);
									if (subPeriods.containsKey(period)) {
										DZFDouble mny = subPeriods.get(period);
										mny = SafeCompute.add(mny, totalMny);
										subPeriods.put(period, mny);
									} else {
										subPeriods.put(period, totalMny);
									}
								}
								subMny = SafeCompute.add(subMny, ageDetailVO.getTotal_mny());
								verifyMny = SafeCompute.add(verifyMny, ageDetailVO.getVerify_mny());
								subTotal.setPeriod_mny(subPeriods);
								subTotal.setTotal_mny(subMny);
								subTotal.setVerify_mny(verifyMny);
							} else {
								subTotal = new AgeDetailVO();
								subTotal.setAccount_code(parentCode);
								subTotal.setAccount_name(ageDetailVO.getAccount_name());
								subTotal.setVch_date(ageDetailVO.getVch_date());
								subTotal.setTotal_mny(ageDetailVO.getTotal_mny());
								subTotal.setVerify_mny(ageDetailVO.getVerify_mny());
								subTotal.setPeriod_mny((HashMap<String, DZFDouble>)ageDetailVO.getPeriod_mny().clone());
								subTotals.put(parentCode, subTotal);
							}
							
						}
					}
				} else {
					for (YntCpaccountVO parentAccount : parentAccounts) {
						String parentCode = parentAccount.getAccountcode();
						if (code.startsWith(parentCode)) {
							AgeDetailVO parentDetail = new AgeDetailVO();
							parentDetail.setAccount_code(parentCode);
							parentDetail.setAccount_name(parentAccount.getAccountname());
							parentDetail.setVch_date(ageDetailVO.getVch_date());
							parentDetail.setTotal_mny(ageDetailVO.getTotal_mny());
							parentDetail.setVerify_mny(ageDetailVO.getVerify_mny());
							parentDetail.setPk_verify_voucher(ageDetailVO.getPk_verify_voucher());
							parentDetail.setPeriod_mny(ageDetailVO.getPeriod_mny());
							parentDetail.setDays(ageDetailVO.getDays());
							parentDetail.setPk_voucher(ageDetailVO.getPk_voucher());
							parentDetail.setVoucher_number(ageDetailVO.getVoucher_number());
							parents.add(parentDetail);
						}
					}
				}
			}
		}
		parents.addAll(subTotals.values());
		rsList.addAll(parents);
	}
}
