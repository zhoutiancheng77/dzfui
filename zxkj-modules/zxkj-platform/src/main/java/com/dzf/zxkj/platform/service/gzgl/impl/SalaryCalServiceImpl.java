package com.dzf.zxkj.platform.service.gzgl.impl;

import com.dzf.zxkj.base.utils.ValueUtils;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.service.gzgl.ISalaryCalService;
import org.springframework.stereotype.Service;


@Service("gl_gzbcalserv")
public class SalaryCalServiceImpl implements ISalaryCalService {

	public void calSbGjj(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo) {
		calSbGjj(vo, setvo, basevo, SalaryTypeEnum.NORMALSALARY.getValue(), true, false);
	}

	// 设置社保公积金
	public void calSbGjj(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo, String billtype,
			boolean isAllowNull, boolean isCopy) {

		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			String[] columns = new String[] { "yfbx_js", "yfbx_bl", "yfbx_mny", "ylbx_js", "ylbx_bl", "ylbx_mny",
					"sybx_js", "sybx_bl", "sybx_mny", "gjj_js", "gjj_bl", "gjj_mny", "gsbx_js", "shybx_js", "qygjj_bl",
					"qyyfbx_bl", "qyylbx_bl", "qysybx_bl", "qygsbx_bl", "qyshybx_bl" };

			setBaseValues(vo, setvo, basevo, columns);

			setPersonValue(vo, new String[] { "yfbx_js", "yfbx_bl", "yfbx_mny", "yanglaobx" }, isAllowNull, isCopy);
			setPersonValue(vo, new String[] { "ylbx_js", "ylbx_bl", "ylbx_mny", "yiliaobx" }, isAllowNull, isCopy);
			setPersonValue(vo, new String[] { "sybx_js", "sybx_bl", "sybx_mny", "shiyebx" }, isAllowNull, isCopy);
			setPersonValue(vo, new String[] { "gjj_js", "gjj_bl", "gjj_mny", "zfgjj" }, isAllowNull, isCopy);

			setCorpValue(vo, new String[] { "yfbx_js", "qyyfbx_bl", "qyyanglaobx" }, isAllowNull, isCopy);
			setCorpValue(vo, new String[] { "ylbx_js", "qyylbx_bl", "qyyiliaobx" }, isAllowNull, isCopy);
			setCorpValue(vo, new String[] { "sybx_js", "qysybx_bl", "qyshiyebx" }, isAllowNull, isCopy);
			setCorpValue(vo, new String[] { "gjj_js", "qygjj_bl", "qyzfgjj" }, isAllowNull, isCopy);
			setCorpValue(vo, new String[] { "gsbx_js", "qygsbx_bl", "qygsbx" }, isAllowNull, isCopy);
			setCorpValue(vo, new String[] { "shybx_js", "qyshybx_bl", "qyshybx" }, isAllowNull, isCopy);
		}
	}

	private void setPersonValue(SalaryReportVO vo, String[] columns, boolean isAllowNull, boolean isCopy) {
		if (vo == null)
			return;
		DZFDouble js = ValueUtils.getDZFDouble(vo.getAttributeValue(columns[0]));
		DZFDouble bl = ValueUtils.getDZFDouble(vo.getAttributeValue(columns[1]));
		DZFDouble mny = ValueUtils.getDZFDouble(vo.getAttributeValue(columns[2]));

		DZFDouble value = SafeCompute.multiply(js, bl).div(100).setScale(2, 0);
		value = SafeCompute.add(value, mny).setScale(2, 0);
		setValue(vo, columns[3], value, isAllowNull, isCopy);

	}

	private void setCorpValue(SalaryReportVO vo, String[] columns, boolean isAllowNull, boolean isCopy) {
		if (vo == null)
			return;
		DZFDouble js = ValueUtils.getDZFDouble(vo.getAttributeValue(columns[0]));
		DZFDouble bl = ValueUtils.getDZFDouble(vo.getAttributeValue(columns[1]));

		DZFDouble value = SafeCompute.multiply(js, bl).div(100).setScale(2, 0);
		setValue(vo, columns[2], value, isAllowNull, isCopy);
	}

	private void setValue(SalaryReportVO vo, String column, DZFDouble value, boolean isAllowNull, boolean isCopy) {
		if (vo == null)
			return;
		if (!isCopy) {
			if (!isAllowNull) {
				vo.setAttributeValue(column, value);
			} else {
				if (vo.getAttributeValue(column) == null)
					vo.setAttributeValue(column, value);
			}
		} else {
			if (DZFDouble.ZERO_DBL.equals(value)) {
			} else {
				vo.setAttributeValue(column, value);
			}
		}
	}

	private void setBaseValues(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo, String[] columns) {

		if (vo == null)
			return;
		for (String column : columns) {
			vo.setAttributeValue(column, getBaseValue(vo, setvo, basevo, column));
		}
	}

	private Object getBaseValue(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo, String column) {

		Object value = null;
		// 优先基数表
		if (basevo != null && basevo.getAttributeValue(column) != null) {
			value = basevo.getAttributeValue(column);
		}

		if (value != null) {
			return value;
		}

		// 通用设置
		if (setvo != null && setvo.getAttributeValue(column) != null) {
			value = setvo.getAttributeValue(column);
		}
		
		if (value != null) {
			return value;
		}
		
		// 工资表本身
		if (vo != null && vo.getAttributeValue(column) != null) {
			value = vo.getAttributeValue(column);
		}

		return value;

	}

}
