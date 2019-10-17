package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.constant.AgeReportConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AgeReportUtil {

	public static int getDays (DZFDate startDate, DZFDate endDate) {
		long mseconds = endDate.getMillis() - startDate.getMillis();
		int days =  (int) TimeUnit.DAYS.convert(mseconds, TimeUnit.MILLISECONDS) + 1;
		return days;
	}
	
	public static int getMonths (DZFDate startDate, DZFDate endDate, Integer periodDays) {
		int days = getDays(startDate, endDate);
		int months = getMonths(days, periodDays);
		return months;
	}
	
	public static int getMonths (int days, Integer periodDays) {
		periodDays = periodDays == null ? 30 : periodDays;
		int months = days/periodDays;
		months = days%periodDays > 0 ? months + 1 : months;
		return months;
	}
	
	public static int getYears (DZFDate startDate, DZFDate endDate, Integer periodDays) {
		int days = getDays(startDate, endDate);
		int years = getYears(days, periodDays);
		return years;
	}
	
	public static int getYears (int days, Integer periodDays) {
		periodDays = periodDays == null ? 360 : periodDays;
		int years = days/periodDays;
		years = days%periodDays > 0 ? years + 1 : years;
		return years;
	}
	
	/**
	 * 根据开始日期距结束日期天数和账龄设置获取所属账龄期间
	 * @param days
	 * @param periods
	 * @param ageType
	 * @return
	 */
	public static String getPeriod (int days, List<String> periods, Integer ageType, Integer periodDays) {
		String period = null;
		if (days < 0)
			return period;
		if (ageType == AgeReportConstant.DAY_AGE) {
			for (String str : periods) {
				String[] curPeriods = str.split("-");
				Integer begin = Integer.valueOf(curPeriods[0]);
				if (curPeriods.length == 1 && days >= begin 
						|| days >= begin && days <= Integer.valueOf(curPeriods[1])) {
					period = str;
					break;
				}
			}
		} else if (ageType == AgeReportConstant.MONTH_AGE) {
			int months = getMonths(days, periodDays);
			for (String str : periods) {
				String[] curPeriods = str.split("-");
				Integer begin = Integer.valueOf(curPeriods[0]);
				if (curPeriods.length == 1 && months > begin
						|| months > begin && months <= Integer.valueOf(curPeriods[1]) ) {
					period = str;
					break;
				}
			}
		} else if (ageType == AgeReportConstant.YEAR_AGE) {
			int years = getYears(days, periodDays);
			for (String str : periods) {
				String[] curPeriods = str.split("-");
				Integer begin = Integer.valueOf(curPeriods[0]);
				if (curPeriods.length == 1 && years > begin
						|| years > begin && years <= Integer.valueOf(curPeriods[1])) {
					period = str;
					break;
				}
			}
		}
		return period;

	}
	/**
	 * 根据账龄期间类型查询期间
	 * @param ageType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	public static List<List<String>> queryPeriods (SingleObjectBO singleObjectBO, Integer ageType, String pk_age) {
		String sql = " select content, period_begin, period_end from sys_account_age_b where pk_corp = ? and pk_age = ? and nvl(dr, 0) = 0 order by code ";
		SQLParameter param = new SQLParameter();
		param.addParam(IDefaultValue.DefaultGroup);
		param.addParam(pk_age);
		List<List<String>> periodList = (List<List<String>>) singleObjectBO.executeQuery(sql, param, new ResultSetProcessor() {
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<List<String>> periodList = new ArrayList<List<String>>();
				List<String> result = new ArrayList<String>();
				List<String> contentList = new ArrayList<String>();
				while (rs.next()) {
					String period_begin = rs.getString("period_begin") == null ? "0" : rs.getString("period_begin");
					String period_end = rs.getString("period_end") == null ? "" : rs.getString("period_end");
					String period = period_begin + "-" + period_end;
					result.add(period);
					contentList.add(rs.getString("content"));
				}
				periodList.add(result);
				periodList.add(contentList);
				return periodList;
			}
		});
		return periodList;
	}
	
	public static List<YntCpaccountVO> queryVerifyAccount (YntCpaccountVO[] account) {
		List<YntCpaccountVO> accountList = new ArrayList<YntCpaccountVO>();
		for (YntCpaccountVO vo : account) {
			if (vo.getIsverification() != null && vo.getIsverification().booleanValue()) {
				accountList.add(vo);
			}
		}
		return accountList;
	}
	
	public static List<YntCpaccountVO> queryVerifyAccountByCode (YntCpaccountVO[] account, String code) {
		List<YntCpaccountVO> accountList = new ArrayList<YntCpaccountVO>();
		for (YntCpaccountVO vo : account) {
			if (vo.getIsverification() != null && vo.getIsverification().booleanValue()
					&& vo.getAccountcode().startsWith(code)
//					&& vo.getAccountcode().length() > code.length()
					&& (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue())) {
				accountList.add(vo);
			}
		}
		return accountList;
	}
}
