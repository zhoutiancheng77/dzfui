package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("salaryreportfactory")
public class SalaryReportExcelFactory {

	@Autowired
	@Qualifier("salaryservice_bejing")
	private ISalaryReportExcel salaryservice_bejing;

	@Autowired
	@Qualifier("salaryservice_chongqing")
	private ISalaryReportExcel salaryservice_chongqing;

	@Autowired
	@Qualifier("salaryservice_default")
	private ISalaryReportExcel salaryservice_default;

	@Autowired
	@Qualifier("salaryservice_henan")
	private ISalaryReportExcel salaryservice_henan;

	@Autowired
	@Qualifier("salaryservice_other")
	private ISalaryReportExcel salaryservice_other;

	@Autowired
	@Qualifier("salaryservice_shandong")
	private ISalaryReportExcel salaryservice_shandong;

	@Autowired
	@Qualifier("salaryservice_hainan")
	private ISalaryReportExcel salaryservice_hainan;

	@Autowired
	@Qualifier("salaryservice_guangxi")
	private ISalaryReportExcel salaryservice_guangxi;

	@Autowired
	@Qualifier("salaryservice_hebei")
	private ISalaryReportExcel salaryservice_hebei;

	@Autowired
	@Qualifier("salaryservice_jiangsu")
	private ISalaryReportExcel salaryservice_jiangsu;

	@Autowired
	@Qualifier("salaryservice_default2019")
	private ISalaryReportExcel salaryservice_default2019;

	public ISalaryReportExcel produce(CorpTaxVo corpvo) {
		ISalaryReportExcel salExcel = null;

		if (corpvo.getTax_area() == null) {
			return salaryservice_other;
		}
		switch (corpvo.getTax_area()) {
		case 2:
			salExcel = salaryservice_bejing;
			break;
		case 4:// 河北
			salExcel = salaryservice_hebei;
			break;
		case 11:// 江苏
			salExcel = salaryservice_jiangsu;
			break;
		case 16:
			// 山东
			salExcel = salaryservice_shandong;//
			break;
		case 22:
			// 海南
			salExcel = salaryservice_hainan;//
			break;
		case 23:// 调整成北京的
			// 重庆
			salExcel = salaryservice_chongqing;
			break;
		case 17:
			// 河南
			salExcel = salaryservice_henan;
			break;
		case 21:
			// 广西
			salExcel = salaryservice_guangxi;
			break;
		default:
			salExcel = salaryservice_other;
		}
		return salExcel;
	}

	public ISalaryReportExcel produce2019(CorpTaxVo corpvo) {
		ISalaryReportExcel salExcel = salaryservice_default2019;
		return salExcel;

	}
}
