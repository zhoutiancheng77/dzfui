package com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter;

import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DefaultValueGetter {

	public static String getDefaultValue(TaxPosContrastVO vo, String nsrsbh) {

		if ("#curdate".equals(vo.getVdefaultvalue())) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String dateString = formatter.format(new DZFDate().toDate());
			vo.setValue(dateString);
		} else if ("#curtime".equals(vo.getVdefaultvalue())) {
			String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			vo.setValue(dateString);
		} else if ("#nsrsbh".equals(vo.getVdefaultvalue())) {
			vo.setValue(nsrsbh);
		} else {
			if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(vo.getSbzlbh())) {// 企业所得税年报
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#yearstart".equals(vo.getVdefaultvalue())) {
					String yearstart = (new DZFDate().getYear() - 1) + "0101";
					vo.setValue(yearstart);
				} else if ("#yearend".equals(vo.getVdefaultvalue())) {
					String yearend = (new DZFDate().getYear() - 1) + "1231";
					vo.setValue(yearend);
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBH10102.equals(vo.getSbzlbh())) {// 增值税小规模
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
				} else if ("#row".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIrow() - 3)));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBH10101.equals(vo.getSbzlbh())) {// 增值税一般人
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
				} else if ("#row".equals(vo.getVdefaultvalue())) {//
					vo.setValue(Integer.toString((vo.getIrow())));
				} else if ("#col".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIcol())));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBHC2.equals(vo.getSbzlbh())) {// 一般企业财报
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
				} else if ("#row".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIrow()) + 1));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBHC1.equals(vo.getSbzlbh())) {// 小企业财报
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
				} else if ("#row".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIrow()) + 1));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBH10412.equals(vo.getSbzlbh())) {// 企业所得税A
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
					vo.setValue("2017-07-01");
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
					vo.setValue("2017-09-30");
				} else if ("#row".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIrow()) + 1));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			} else if (TaxRptConst.SB_ZLBH10413.equals(vo.getSbzlbh())) {// 企业所得税b
				if (StringUtil.isEmpty(vo.getVdefaultvalue()))
					return vo.getValue();
				if ("#skssqq".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodStartDate(period).toString());
				} else if ("#skssqz".equals(vo.getVdefaultvalue())) {
					String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
					vo.setValue(DateUtils.getPeriodEndDate(period).toString());
				} else if ("#row".equals(vo.getVdefaultvalue())) {
					vo.setValue(Integer.toString((vo.getIrow()) + 1));
				} else {
					vo.setValue(vo.getVdefaultvalue());
				}
			}
		}

		return vo.getValue();
	}
}
