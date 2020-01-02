package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.platform.service.taxrpt.shandong.ITaxCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("taxcatefct")
public class TaxCategoryFactory {
	ITaxCategoryService taxcateserv = null;
	@Autowired
	@Qualifier("taxcateserv_default")
	private ITaxCategoryService taxcateserv_default;

	@Autowired
	@Qualifier("taxcateserv_10101")
	private ITaxCategoryService taxcateserv_10101;

	@Autowired
	@Qualifier("taxcateserv_10102")
	private ITaxCategoryService taxcateserv_10102;

	@Autowired
	@Qualifier("taxcateserv_10412new")
	private ITaxCategoryService taxcateserv_10412;

	@Autowired
	@Qualifier("taxcateserv_10413new")
	private ITaxCategoryService taxcateserv_10413;

	@Autowired
	@Qualifier("taxcateserv_A")
	private ITaxCategoryService taxcateserv_A;

	@Autowired
	@Qualifier("taxcateserv_C1")
	private ITaxCategoryService taxcateserv_C1;

	@Autowired
	@Qualifier("taxcateserv_C2")
	private ITaxCategoryService taxcateserv_C2;
	@Autowired
	@Qualifier("taxcateserv_C29805")
	private ITaxCategoryService taxcateserv_C29805;

	@Autowired
	@Qualifier("taxcateserv_10601")
	private ITaxCategoryService taxcateserv_10601;
	
	@Autowired
	@Qualifier("taxcateserv_D1")
	private ITaxCategoryService taxcateserv_D1;

	public ITaxCategoryService produce(String sb_zlbh) {
		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(sb_zlbh)) { // 企业所得税年度汇算清缴
			taxcateserv = taxcateserv_A;
		} else if (TaxRptConst.SB_ZLBH10102.equals(sb_zlbh) || TaxRptConst.SB_ZLBH1010201.equals(sb_zlbh)) {// 增值税小规模纳税人申报表季报
			taxcateserv = taxcateserv_10102;
		} else if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh)) {// 增值税一般纳税人申报表月报
			taxcateserv = taxcateserv_10101;
		} else if (TaxRptConst.SB_ZLBHC1.equals(sb_zlbh)) {// 财报-小企业季报
			taxcateserv = taxcateserv_C1;
		} else if (TaxRptConst.SB_ZLBHC2.equals(sb_zlbh)) {// 财报-一般企业季报
			taxcateserv = taxcateserv_C2;
		}  else if (TaxRptConst.SB_ZLBH29805.equals(sb_zlbh)) {// 财报-企业会计制度
			taxcateserv = taxcateserv_C29805;
		} else if (TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)) {// 所得税季度纳税申报表(A类),不区分一般人和小规模
			taxcateserv = taxcateserv_10412;
		} else if (TaxRptConst.SB_ZLBH10413.equals(sb_zlbh)) {// 所得税季度纳税申报表(B类),不区分一般人和小规模
			taxcateserv = taxcateserv_10413;
		} else if (TaxRptConst.SB_ZLBH10601.equals(sb_zlbh)) {// 文化事业建设费
			taxcateserv = taxcateserv_10601;
		}  else if (TaxRptConst.SB_ZLBHD1.equals(sb_zlbh)) {//印花税
			taxcateserv = taxcateserv_D1;
		} else {
			taxcateserv = taxcateserv_default;
		}
		return taxcateserv;
	}
}
