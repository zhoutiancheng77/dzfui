package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表（适用小规模纳税人）Data1_01
@TaxExcelPos(reportID = "10102001", reportname = "增值税纳税申报表")
public class TaxReturn {
	// 其中：销售不动产的销售额
	@TaxExcelPos(row = 2, col = 2)
	private DZFDouble xsbdc5fwbdcbqs;
	// 本期数货物及劳务
	// 应征增值税不含税销售额3%
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble yzzzs3_hwlw_bqs;
	// 税务机关代开的增值税专用发票不含税销售额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble dkzzsfp3_hwlw_bqs;
	// 税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble skqjfp3_hwlw_bqs;
	// 销售使用过的应税固定资产不含税销售额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble xssygdysgdzc_hwlw_bqs;
	// 其中：税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble xssy_skqjfp_hwlw_bqs;
	// 免税销售额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble msxse_hwlw_bqs;
	// 小微企业免税销售额
	@TaxExcelPos(row = 15, col = 3)
	private DZFDouble xwqymsxse_hwlw_bqs;
	// 未达起征点销售额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble wdqzdxse_hwlw_bqs;
	// 其他免税销售额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble qtmsxse_hwlw_bqs;
	// 出口免税销售额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble ckmsxse_hwlw_bqs;
	// 出口免税销售额_税控器具开具的普通发票销售额
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble ckms_skqjfp_hwlw_bqs;
	// 本期应纳税额
	@TaxExcelPos(row = 21, col = 3)
	private DZFDouble bqynse_hwlw_bqs;
	// 本期应纳税额减征额
	@TaxExcelPos(row = 22, col = 3)
	private DZFDouble bqynsejze_hwlw_bqs;
	// 本期免税额
	@TaxExcelPos(row = 23, col = 3)
	private DZFDouble bqmse_hwlw_bqs;
	// 小微企业免税额
	@TaxExcelPos(row = 24, col = 3)
	private DZFDouble xwqymse_hwlw_bqs;
	// 未达起征点免税额
	@TaxExcelPos(row = 25, col = 3)
	private DZFDouble wdqzdmse_hwlw_bqs;
	// 应纳税额合计
	@TaxExcelPos(row = 26, col = 3)
	private DZFDouble ynsehj_hwlw_bqs;
	// 本期已缴税额
	@TaxExcelPos(row = 27, col = 3)
	private DZFDouble bqyjse_hwlw_bqs;
	// 本期应补退税额
	@TaxExcelPos(row = 28, col = 3)
	private DZFDouble bqybtse_hwlw_bqs;

	// 本期数服务、不动产和无形资产
	// 应征增值税不含税销售额
	@TaxExcelPos(row = 6, col = 4)
	private DZFDouble yzzzs3_fwbdc_bqs;
	// 税务机关代开的增值税专用发票不含税销售额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble dkzzsfp3_fwbdc_bqs;
	// 税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble skqjfp3_fwbdc_bqs;
	// 销售、出租不动产不含税销售额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble yzzzs5_fwbdc_bqs;
	// 税务机关代开的增值税专用发票不含税销售额(销售、出租不动产)
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble dkzzsfp5_fwbdc_bqs;
	// 税控器具开具的普通发票不含税销售额(销售、出租不动产)
	@TaxExcelPos(row = 11, col = 4)
	private DZFDouble skqjfp5_fwbdc_bqs;
	// 免税销售额
	@TaxExcelPos(row = 14, col = 4)
	private DZFDouble msxse_fwbdc_bqs;
	// 小微企业免税销售额
	@TaxExcelPos(row = 15, col = 4)
	private DZFDouble xwqymsxse_fwbdc_bqs;
	// 未达起征点销售额
	@TaxExcelPos(row = 16, col = 4)
	private DZFDouble wdqzdxse_fwbdc_bqs;
	// 其他免税销售额
	@TaxExcelPos(row = 17, col = 4)
	private DZFDouble qtmsxse_fwbdc_bqs;
	// 出口免税销售额
	@TaxExcelPos(row = 18, col = 4)
	private DZFDouble ckmsxse_fwbdc_bqs;
	// 出口免税销售额_税控器具开具的普通发票销售额
	@TaxExcelPos(row = 19, col = 4)
	private DZFDouble ckms_skqjfp_fwbdc_bqs;
	// 本期应纳税额
	@TaxExcelPos(row = 21, col = 4)
	private DZFDouble bqynse_fwbdc_bqs;
	// 本期应纳税额减征额
	@TaxExcelPos(row = 22, col = 4)
	private DZFDouble bqynsejze_fwbdc_bqs;
	// 本期免税额
	@TaxExcelPos(row = 23, col = 4)
	private DZFDouble bqmse_fwbdc_bqs;
	// 小微企业免税额
	@TaxExcelPos(row = 24, col = 4)
	private DZFDouble xwqymse_fwbdc_bqs;
	// 未达起征点免税额
	@TaxExcelPos(row = 25, col = 4)
	private DZFDouble wdqzdmse_fwbdc_bqs;
	// 应纳税额合计
	@TaxExcelPos(row = 26, col = 4)
	private DZFDouble ynsehj_fwbdc_bqs;
	// 本期已缴税额
	@TaxExcelPos(row = 27, col = 4)
	private DZFDouble bqyjse_fwbdc_bqs;
	// 本期应补退税额
	@TaxExcelPos(row = 28, col = 4)
	private DZFDouble bqybtse_fwbdc_bqs;

	// 本年累计货物及劳务
	// 应征增值税不含税销售额3%
	@TaxExcelPos(row = 6, col = 5, isTotal = true)
	private DZFDouble yzzzs3_hwlw_bnlj;
	// 税务机关代开的增值税专用发票不含税销售额
	@TaxExcelPos(row = 7, col = 5, isTotal = true)
	private DZFDouble dkzzsfp3_hwlw_bnlj;
	// 税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 8, col = 5, isTotal = true)
	private DZFDouble skqjfp3_hwlw_bnlj;
	// 销售使用过的应税固定资产不含税销售额
	@TaxExcelPos(row = 12, col = 5, isTotal = true)
	private DZFDouble xssygdysgdzc_hwlw_bnlj;
	// 其中：税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 13, col = 5, isTotal = true)
	private DZFDouble xssy_skqjfp_hwlw_bnlj;
	// 免税销售额
	@TaxExcelPos(row = 14, col = 5, isTotal = true)
	private DZFDouble msxse_hwlw_bnlj;
	// 小微企业免税销售额
	@TaxExcelPos(row = 15, col = 5, isTotal = true)
	private DZFDouble xwqymsxse_hwlw_bnlj;
	// 未达起征点销售额
	@TaxExcelPos(row = 16, col = 5, isTotal = true)
	private DZFDouble wdqzdxse_hwlw_bnlj;
	// 其他免税销售额
	@TaxExcelPos(row = 17, col = 5, isTotal = true)
	private DZFDouble qtmsxse_hwlw_bnlj;
	// 出口免税销售额
	@TaxExcelPos(row = 18, col = 5, isTotal = true)
	private DZFDouble ckmsxse_hwlw_bnlj;
	// 出口免税销售额_税控器具开具的普通发票销售额
	@TaxExcelPos(row = 19, col = 5, isTotal = true)
	private DZFDouble ckms_skqjfp_hwlw_bnlj;

	// 核定销售额
	private DZFDouble hdxsehwlwbnlj;

	// 本期应纳税额
	@TaxExcelPos(row = 21, col = 5, isTotal = true)
	private DZFDouble bqynse_hwlw_bnlj;

	// 核定应纳税额
	private DZFDouble hdynsehwlwbnlj;

	// 本期应纳税额减征额
	@TaxExcelPos(row = 22, col = 5, isTotal = true)
	private DZFDouble bqynsejze_hwlw_bnlj;
	// 本期免税额
	@TaxExcelPos(row = 23, col = 5, isTotal = true)
	private DZFDouble bqmse_hwlw_bnlj;
	// 小微企业免税额
	@TaxExcelPos(row = 24, col = 5, isTotal = true)
	private DZFDouble xwqymse_hwlw_bnlj;
	// 未达起征点免税额
	@TaxExcelPos(row = 25, col = 5, isTotal = true)
	private DZFDouble wdqzdmse_hwlw_bnlj;
	// 应纳税额合计
	@TaxExcelPos(row = 26, col = 5, isTotal = true)
	private DZFDouble ynsehj_hwlw_bnlj;

	// 本期已缴税额
	// @TaxExcelPos(row = 25, col = 5, isTotal = true)
	private DZFDouble bqyjse_hwlw_bnlj;
	// 本期应补退税额
	// @TaxExcelPos(row = 26, col = 5, isTotal = true)
	private DZFDouble bqybtse_hwlw_bnlj;

	// 本年累计服务、不动产和无形资产
	// 应征增值税不含税销售额3%
	@TaxExcelPos(row = 6, col = 6, isTotal = true)
	private DZFDouble yzzzs3_fwbdc_bnlj;
	// 税务机关代开的增值税专用发票不含税销售额
	@TaxExcelPos(row = 7, col = 6, isTotal = true)
	private DZFDouble dkzzsfp3_fwbdc_bnlj;
	// 税控器具开具的普通发票不含税销售额
	@TaxExcelPos(row = 8, col = 6, isTotal = true)
	private DZFDouble skqjfp3_fwbdc_bnlj;
	// 销售、出租不动产不含税销售额
	@TaxExcelPos(row = 9, col = 6, isTotal = true)
	private DZFDouble yzzzs5_fwbdc_bnlj;
	// 税务机关代开的增值税专用发票不含税销售额(销售、出租不动产)
	@TaxExcelPos(row = 10, col = 6, isTotal = true)
	private DZFDouble dkzzsfp5_fwbdc_bnlj;
	// 税控器具开具的普通发票不含税销售额(销售、出租不动产)
	@TaxExcelPos(row = 11, col = 6, isTotal = true)
	private DZFDouble skqjfp5_fwbdc_bnlj;
	// 免税销售额
	@TaxExcelPos(row = 14, col = 6, isTotal = true)
	private DZFDouble msxse_fwbdc_bnlj;
	// 小微企业免税销售额
	@TaxExcelPos(row = 15, col = 6, isTotal = true)
	private DZFDouble xwqymsxse_fwbdc_bnlj;
	// 未达起征点销售额
	@TaxExcelPos(row = 16, col = 6, isTotal = true)
	private DZFDouble wdqzdxse_fwbdc_bnlj;
	// 其他免税销售额
	@TaxExcelPos(row = 17, col = 6, isTotal = true)
	private DZFDouble qtmsxse_fwbdc_bnlj;
	// 出口免税销售额
	@TaxExcelPos(row = 18, col = 6, isTotal = true)
	private DZFDouble ckmsxse_fwbdc_bnlj;
	// 出口免税销售额_税控器具开具的普通发票销售额
	@TaxExcelPos(row = 19, col = 6, isTotal = true)
	private DZFDouble ckms_skqjfp_fwbdc_bnlj;

	// 核定销售额
	private DZFDouble hdxseysfwbnlj;

	// 本期应纳税额
	@TaxExcelPos(row = 21, col = 6, isTotal = true)
	private DZFDouble bqynse_fwbdc_bnlj;

	// 核定应纳税额
	private DZFDouble hdynseysfwbnlj;

	// 本期应纳税额减征额
	@TaxExcelPos(row = 22, col = 6, isTotal = true)
	private DZFDouble bqynsejze_fwbdc_bnlj;
	// 本期免税额
	@TaxExcelPos(row = 23, col = 6, isTotal = true)
	private DZFDouble bqmse_fwbdc_bnlj;
	// 小微企业免税额
	@TaxExcelPos(row = 24, col = 6, isTotal = true)
	private DZFDouble xwqymse_fwbdc_bnlj;
	// 未达起征点免税额
	@TaxExcelPos(row = 25, col = 6, isTotal = true)
	private DZFDouble wdqzdmse_fwbdc_bnlj;
	// 应纳税额合计
	@TaxExcelPos(row = 26, col = 6, isTotal = true)
	private DZFDouble ynsehj_fwbdc_bnlj;

	// 本期已缴税额
	// @TaxExcelPos(row = 25, col = 6, isTotal = true)
	private DZFDouble bqyjse_fwbdc_bnlj;
	// 本期应补退税额
	// @TaxExcelPos(row = 26, col = 6, isTotal = true)
	private DZFDouble bqybtse_fwbdc_bnlj;

	public DZFDouble getXsbdc5fwbdcbqs() {
		return xsbdc5fwbdcbqs;
	}

	public void setXsbdc5fwbdcbqs(DZFDouble xsbdc5fwbdcbqs) {
		this.xsbdc5fwbdcbqs = xsbdc5fwbdcbqs;
	}

	public DZFDouble getYzzzs3_hwlw_bqs() {
		return yzzzs3_hwlw_bqs;
	}

	public void setYzzzs3_hwlw_bqs(DZFDouble yzzzs3_hwlw_bqs) {
		this.yzzzs3_hwlw_bqs = yzzzs3_hwlw_bqs;
	}

	public DZFDouble getDkzzsfp3_hwlw_bqs() {
		return dkzzsfp3_hwlw_bqs;
	}

	public void setDkzzsfp3_hwlw_bqs(DZFDouble dkzzsfp3_hwlw_bqs) {
		this.dkzzsfp3_hwlw_bqs = dkzzsfp3_hwlw_bqs;
	}

	public DZFDouble getSkqjfp3_hwlw_bqs() {
		return skqjfp3_hwlw_bqs;
	}

	public void setSkqjfp3_hwlw_bqs(DZFDouble skqjfp3_hwlw_bqs) {
		this.skqjfp3_hwlw_bqs = skqjfp3_hwlw_bqs;
	}

	public DZFDouble getXssygdysgdzc_hwlw_bqs() {
		return xssygdysgdzc_hwlw_bqs;
	}

	public void setXssygdysgdzc_hwlw_bqs(DZFDouble xssygdysgdzc_hwlw_bqs) {
		this.xssygdysgdzc_hwlw_bqs = xssygdysgdzc_hwlw_bqs;
	}

	public DZFDouble getXssy_skqjfp_hwlw_bqs() {
		return xssy_skqjfp_hwlw_bqs;
	}

	public void setXssy_skqjfp_hwlw_bqs(DZFDouble xssy_skqjfp_hwlw_bqs) {
		this.xssy_skqjfp_hwlw_bqs = xssy_skqjfp_hwlw_bqs;
	}

	public DZFDouble getMsxse_hwlw_bqs() {
		return msxse_hwlw_bqs;
	}

	public void setMsxse_hwlw_bqs(DZFDouble msxse_hwlw_bqs) {
		this.msxse_hwlw_bqs = msxse_hwlw_bqs;
	}

	public DZFDouble getXwqymsxse_hwlw_bqs() {
		return xwqymsxse_hwlw_bqs;
	}

	public void setXwqymsxse_hwlw_bqs(DZFDouble xwqymsxse_hwlw_bqs) {
		this.xwqymsxse_hwlw_bqs = xwqymsxse_hwlw_bqs;
	}

	public DZFDouble getWdqzdxse_hwlw_bqs() {
		return wdqzdxse_hwlw_bqs;
	}

	public void setWdqzdxse_hwlw_bqs(DZFDouble wdqzdxse_hwlw_bqs) {
		this.wdqzdxse_hwlw_bqs = wdqzdxse_hwlw_bqs;
	}

	public DZFDouble getQtmsxse_hwlw_bqs() {
		return qtmsxse_hwlw_bqs;
	}

	public void setQtmsxse_hwlw_bqs(DZFDouble qtmsxse_hwlw_bqs) {
		this.qtmsxse_hwlw_bqs = qtmsxse_hwlw_bqs;
	}

	public DZFDouble getCkmsxse_hwlw_bqs() {
		return ckmsxse_hwlw_bqs;
	}

	public void setCkmsxse_hwlw_bqs(DZFDouble ckmsxse_hwlw_bqs) {
		this.ckmsxse_hwlw_bqs = ckmsxse_hwlw_bqs;
	}

	public DZFDouble getCkms_skqjfp_hwlw_bqs() {
		return ckms_skqjfp_hwlw_bqs;
	}

	public void setCkms_skqjfp_hwlw_bqs(DZFDouble ckms_skqjfp_hwlw_bqs) {
		this.ckms_skqjfp_hwlw_bqs = ckms_skqjfp_hwlw_bqs;
	}

	public DZFDouble getBqynse_hwlw_bqs() {
		return bqynse_hwlw_bqs;
	}

	public void setBqynse_hwlw_bqs(DZFDouble bqynse_hwlw_bqs) {
		this.bqynse_hwlw_bqs = bqynse_hwlw_bqs;
	}

	public DZFDouble getBqynsejze_hwlw_bqs() {
		return bqynsejze_hwlw_bqs;
	}

	public void setBqynsejze_hwlw_bqs(DZFDouble bqynsejze_hwlw_bqs) {
		this.bqynsejze_hwlw_bqs = bqynsejze_hwlw_bqs;
	}

	public DZFDouble getBqmse_hwlw_bqs() {
		return bqmse_hwlw_bqs;
	}

	public void setBqmse_hwlw_bqs(DZFDouble bqmse_hwlw_bqs) {
		this.bqmse_hwlw_bqs = bqmse_hwlw_bqs;
	}

	public DZFDouble getXwqymse_hwlw_bqs() {
		return xwqymse_hwlw_bqs;
	}

	public void setXwqymse_hwlw_bqs(DZFDouble xwqymse_hwlw_bqs) {
		this.xwqymse_hwlw_bqs = xwqymse_hwlw_bqs;
	}

	public DZFDouble getWdqzdmse_hwlw_bqs() {
		return wdqzdmse_hwlw_bqs;
	}

	public void setWdqzdmse_hwlw_bqs(DZFDouble wdqzdmse_hwlw_bqs) {
		this.wdqzdmse_hwlw_bqs = wdqzdmse_hwlw_bqs;
	}

	public DZFDouble getYnsehj_hwlw_bqs() {
		return ynsehj_hwlw_bqs;
	}

	public void setYnsehj_hwlw_bqs(DZFDouble ynsehj_hwlw_bqs) {
		this.ynsehj_hwlw_bqs = ynsehj_hwlw_bqs;
	}

	public DZFDouble getBqyjse_hwlw_bqs() {
		return bqyjse_hwlw_bqs;
	}

	public void setBqyjse_hwlw_bqs(DZFDouble bqyjse_hwlw_bqs) {
		this.bqyjse_hwlw_bqs = bqyjse_hwlw_bqs;
	}

	public DZFDouble getBqybtse_hwlw_bqs() {
		return bqybtse_hwlw_bqs;
	}

	public void setBqybtse_hwlw_bqs(DZFDouble bqybtse_hwlw_bqs) {
		this.bqybtse_hwlw_bqs = bqybtse_hwlw_bqs;
	}

	public DZFDouble getYzzzs3_fwbdc_bqs() {
		return yzzzs3_fwbdc_bqs;
	}

	public void setYzzzs3_fwbdc_bqs(DZFDouble yzzzs3_fwbdc_bqs) {
		this.yzzzs3_fwbdc_bqs = yzzzs3_fwbdc_bqs;
	}

	public DZFDouble getDkzzsfp3_fwbdc_bqs() {
		return dkzzsfp3_fwbdc_bqs;
	}

	public void setDkzzsfp3_fwbdc_bqs(DZFDouble dkzzsfp3_fwbdc_bqs) {
		this.dkzzsfp3_fwbdc_bqs = dkzzsfp3_fwbdc_bqs;
	}

	public DZFDouble getSkqjfp3_fwbdc_bqs() {
		return skqjfp3_fwbdc_bqs;
	}

	public void setSkqjfp3_fwbdc_bqs(DZFDouble skqjfp3_fwbdc_bqs) {
		this.skqjfp3_fwbdc_bqs = skqjfp3_fwbdc_bqs;
	}

	public DZFDouble getYzzzs5_fwbdc_bqs() {
		return yzzzs5_fwbdc_bqs;
	}

	public void setYzzzs5_fwbdc_bqs(DZFDouble yzzzs5_fwbdc_bqs) {
		this.yzzzs5_fwbdc_bqs = yzzzs5_fwbdc_bqs;
	}

	public DZFDouble getDkzzsfp5_fwbdc_bqs() {
		return dkzzsfp5_fwbdc_bqs;
	}

	public void setDkzzsfp5_fwbdc_bqs(DZFDouble dkzzsfp5_fwbdc_bqs) {
		this.dkzzsfp5_fwbdc_bqs = dkzzsfp5_fwbdc_bqs;
	}

	public DZFDouble getSkqjfp5_fwbdc_bqs() {
		return skqjfp5_fwbdc_bqs;
	}

	public void setSkqjfp5_fwbdc_bqs(DZFDouble skqjfp5_fwbdc_bqs) {
		this.skqjfp5_fwbdc_bqs = skqjfp5_fwbdc_bqs;
	}

	public DZFDouble getMsxse_fwbdc_bqs() {
		return msxse_fwbdc_bqs;
	}

	public void setMsxse_fwbdc_bqs(DZFDouble msxse_fwbdc_bqs) {
		this.msxse_fwbdc_bqs = msxse_fwbdc_bqs;
	}

	public DZFDouble getXwqymsxse_fwbdc_bqs() {
		return xwqymsxse_fwbdc_bqs;
	}

	public void setXwqymsxse_fwbdc_bqs(DZFDouble xwqymsxse_fwbdc_bqs) {
		this.xwqymsxse_fwbdc_bqs = xwqymsxse_fwbdc_bqs;
	}

	public DZFDouble getWdqzdxse_fwbdc_bqs() {
		return wdqzdxse_fwbdc_bqs;
	}

	public void setWdqzdxse_fwbdc_bqs(DZFDouble wdqzdxse_fwbdc_bqs) {
		this.wdqzdxse_fwbdc_bqs = wdqzdxse_fwbdc_bqs;
	}

	public DZFDouble getQtmsxse_fwbdc_bqs() {
		return qtmsxse_fwbdc_bqs;
	}

	public void setQtmsxse_fwbdc_bqs(DZFDouble qtmsxse_fwbdc_bqs) {
		this.qtmsxse_fwbdc_bqs = qtmsxse_fwbdc_bqs;
	}

	public DZFDouble getCkmsxse_fwbdc_bqs() {
		return ckmsxse_fwbdc_bqs;
	}

	public void setCkmsxse_fwbdc_bqs(DZFDouble ckmsxse_fwbdc_bqs) {
		this.ckmsxse_fwbdc_bqs = ckmsxse_fwbdc_bqs;
	}

	public DZFDouble getCkms_skqjfp_fwbdc_bqs() {
		return ckms_skqjfp_fwbdc_bqs;
	}

	public void setCkms_skqjfp_fwbdc_bqs(DZFDouble ckms_skqjfp_fwbdc_bqs) {
		this.ckms_skqjfp_fwbdc_bqs = ckms_skqjfp_fwbdc_bqs;
	}

	public DZFDouble getBqynse_fwbdc_bqs() {
		return bqynse_fwbdc_bqs;
	}

	public void setBqynse_fwbdc_bqs(DZFDouble bqynse_fwbdc_bqs) {
		this.bqynse_fwbdc_bqs = bqynse_fwbdc_bqs;
	}

	public DZFDouble getBqynsejze_fwbdc_bqs() {
		return bqynsejze_fwbdc_bqs;
	}

	public void setBqynsejze_fwbdc_bqs(DZFDouble bqynsejze_fwbdc_bqs) {
		this.bqynsejze_fwbdc_bqs = bqynsejze_fwbdc_bqs;
	}

	public DZFDouble getBqmse_fwbdc_bqs() {
		return bqmse_fwbdc_bqs;
	}

	public void setBqmse_fwbdc_bqs(DZFDouble bqmse_fwbdc_bqs) {
		this.bqmse_fwbdc_bqs = bqmse_fwbdc_bqs;
	}

	public DZFDouble getXwqymse_fwbdc_bqs() {
		return xwqymse_fwbdc_bqs;
	}

	public void setXwqymse_fwbdc_bqs(DZFDouble xwqymse_fwbdc_bqs) {
		this.xwqymse_fwbdc_bqs = xwqymse_fwbdc_bqs;
	}

	public DZFDouble getWdqzdmse_fwbdc_bqs() {
		return wdqzdmse_fwbdc_bqs;
	}

	public void setWdqzdmse_fwbdc_bqs(DZFDouble wdqzdmse_fwbdc_bqs) {
		this.wdqzdmse_fwbdc_bqs = wdqzdmse_fwbdc_bqs;
	}

	public DZFDouble getYnsehj_fwbdc_bqs() {
		return ynsehj_fwbdc_bqs;
	}

	public void setYnsehj_fwbdc_bqs(DZFDouble ynsehj_fwbdc_bqs) {
		this.ynsehj_fwbdc_bqs = ynsehj_fwbdc_bqs;
	}

	public DZFDouble getBqyjse_fwbdc_bqs() {
		return bqyjse_fwbdc_bqs;
	}

	public void setBqyjse_fwbdc_bqs(DZFDouble bqyjse_fwbdc_bqs) {
		this.bqyjse_fwbdc_bqs = bqyjse_fwbdc_bqs;
	}

	public DZFDouble getBqybtse_fwbdc_bqs() {
		return bqybtse_fwbdc_bqs;
	}

	public void setBqybtse_fwbdc_bqs(DZFDouble bqybtse_fwbdc_bqs) {
		this.bqybtse_fwbdc_bqs = bqybtse_fwbdc_bqs;
	}

	public DZFDouble getYzzzs3_hwlw_bnlj() {
		return yzzzs3_hwlw_bnlj;
	}

	public void setYzzzs3_hwlw_bnlj(DZFDouble yzzzs3_hwlw_bnlj) {
		this.yzzzs3_hwlw_bnlj = yzzzs3_hwlw_bnlj;
	}

	public DZFDouble getDkzzsfp3_hwlw_bnlj() {
		return dkzzsfp3_hwlw_bnlj;
	}

	public void setDkzzsfp3_hwlw_bnlj(DZFDouble dkzzsfp3_hwlw_bnlj) {
		this.dkzzsfp3_hwlw_bnlj = dkzzsfp3_hwlw_bnlj;
	}

	public DZFDouble getSkqjfp3_hwlw_bnlj() {
		return skqjfp3_hwlw_bnlj;
	}

	public void setSkqjfp3_hwlw_bnlj(DZFDouble skqjfp3_hwlw_bnlj) {
		this.skqjfp3_hwlw_bnlj = skqjfp3_hwlw_bnlj;
	}

	public DZFDouble getXssygdysgdzc_hwlw_bnlj() {
		return xssygdysgdzc_hwlw_bnlj;
	}

	public void setXssygdysgdzc_hwlw_bnlj(DZFDouble xssygdysgdzc_hwlw_bnlj) {
		this.xssygdysgdzc_hwlw_bnlj = xssygdysgdzc_hwlw_bnlj;
	}

	public DZFDouble getXssy_skqjfp_hwlw_bnlj() {
		return xssy_skqjfp_hwlw_bnlj;
	}

	public void setXssy_skqjfp_hwlw_bnlj(DZFDouble xssy_skqjfp_hwlw_bnlj) {
		this.xssy_skqjfp_hwlw_bnlj = xssy_skqjfp_hwlw_bnlj;
	}

	public DZFDouble getMsxse_hwlw_bnlj() {
		return msxse_hwlw_bnlj;
	}

	public void setMsxse_hwlw_bnlj(DZFDouble msxse_hwlw_bnlj) {
		this.msxse_hwlw_bnlj = msxse_hwlw_bnlj;
	}

	public DZFDouble getXwqymsxse_hwlw_bnlj() {
		return xwqymsxse_hwlw_bnlj;
	}

	public void setXwqymsxse_hwlw_bnlj(DZFDouble xwqymsxse_hwlw_bnlj) {
		this.xwqymsxse_hwlw_bnlj = xwqymsxse_hwlw_bnlj;
	}

	public DZFDouble getWdqzdxse_hwlw_bnlj() {
		return wdqzdxse_hwlw_bnlj;
	}

	public void setWdqzdxse_hwlw_bnlj(DZFDouble wdqzdxse_hwlw_bnlj) {
		this.wdqzdxse_hwlw_bnlj = wdqzdxse_hwlw_bnlj;
	}

	public DZFDouble getQtmsxse_hwlw_bnlj() {
		return qtmsxse_hwlw_bnlj;
	}

	public void setQtmsxse_hwlw_bnlj(DZFDouble qtmsxse_hwlw_bnlj) {
		this.qtmsxse_hwlw_bnlj = qtmsxse_hwlw_bnlj;
	}

	public DZFDouble getCkmsxse_hwlw_bnlj() {
		return ckmsxse_hwlw_bnlj;
	}

	public void setCkmsxse_hwlw_bnlj(DZFDouble ckmsxse_hwlw_bnlj) {
		this.ckmsxse_hwlw_bnlj = ckmsxse_hwlw_bnlj;
	}

	public DZFDouble getCkms_skqjfp_hwlw_bnlj() {
		return ckms_skqjfp_hwlw_bnlj;
	}

	public void setCkms_skqjfp_hwlw_bnlj(DZFDouble ckms_skqjfp_hwlw_bnlj) {
		this.ckms_skqjfp_hwlw_bnlj = ckms_skqjfp_hwlw_bnlj;
	}

	public DZFDouble getHdxsehwlwbnlj() {
		return hdxsehwlwbnlj;
	}

	public void setHdxsehwlwbnlj(DZFDouble hdxsehwlwbnlj) {
		this.hdxsehwlwbnlj = hdxsehwlwbnlj;
	}

	public DZFDouble getBqynse_hwlw_bnlj() {
		return bqynse_hwlw_bnlj;
	}

	public void setBqynse_hwlw_bnlj(DZFDouble bqynse_hwlw_bnlj) {
		this.bqynse_hwlw_bnlj = bqynse_hwlw_bnlj;
	}

	public DZFDouble getHdynsehwlwbnlj() {
		return hdynsehwlwbnlj;
	}

	public void setHdynsehwlwbnlj(DZFDouble hdynsehwlwbnlj) {
		this.hdynsehwlwbnlj = hdynsehwlwbnlj;
	}

	public DZFDouble getBqynsejze_hwlw_bnlj() {
		return bqynsejze_hwlw_bnlj;
	}

	public void setBqynsejze_hwlw_bnlj(DZFDouble bqynsejze_hwlw_bnlj) {
		this.bqynsejze_hwlw_bnlj = bqynsejze_hwlw_bnlj;
	}

	public DZFDouble getBqmse_hwlw_bnlj() {
		return bqmse_hwlw_bnlj;
	}

	public void setBqmse_hwlw_bnlj(DZFDouble bqmse_hwlw_bnlj) {
		this.bqmse_hwlw_bnlj = bqmse_hwlw_bnlj;
	}

	public DZFDouble getXwqymse_hwlw_bnlj() {
		return xwqymse_hwlw_bnlj;
	}

	public void setXwqymse_hwlw_bnlj(DZFDouble xwqymse_hwlw_bnlj) {
		this.xwqymse_hwlw_bnlj = xwqymse_hwlw_bnlj;
	}

	public DZFDouble getWdqzdmse_hwlw_bnlj() {
		return wdqzdmse_hwlw_bnlj;
	}

	public void setWdqzdmse_hwlw_bnlj(DZFDouble wdqzdmse_hwlw_bnlj) {
		this.wdqzdmse_hwlw_bnlj = wdqzdmse_hwlw_bnlj;
	}

	public DZFDouble getYnsehj_hwlw_bnlj() {
		return ynsehj_hwlw_bnlj;
	}

	public void setYnsehj_hwlw_bnlj(DZFDouble ynsehj_hwlw_bnlj) {
		this.ynsehj_hwlw_bnlj = ynsehj_hwlw_bnlj;
	}

	public DZFDouble getBqyjse_hwlw_bnlj() {
		return bqyjse_hwlw_bnlj;
	}

	public void setBqyjse_hwlw_bnlj(DZFDouble bqyjse_hwlw_bnlj) {
		this.bqyjse_hwlw_bnlj = bqyjse_hwlw_bnlj;
	}

	public DZFDouble getBqybtse_hwlw_bnlj() {
		return bqybtse_hwlw_bnlj;
	}

	public void setBqybtse_hwlw_bnlj(DZFDouble bqybtse_hwlw_bnlj) {
		this.bqybtse_hwlw_bnlj = bqybtse_hwlw_bnlj;
	}

	public DZFDouble getYzzzs3_fwbdc_bnlj() {
		return yzzzs3_fwbdc_bnlj;
	}

	public void setYzzzs3_fwbdc_bnlj(DZFDouble yzzzs3_fwbdc_bnlj) {
		this.yzzzs3_fwbdc_bnlj = yzzzs3_fwbdc_bnlj;
	}

	public DZFDouble getDkzzsfp3_fwbdc_bnlj() {
		return dkzzsfp3_fwbdc_bnlj;
	}

	public void setDkzzsfp3_fwbdc_bnlj(DZFDouble dkzzsfp3_fwbdc_bnlj) {
		this.dkzzsfp3_fwbdc_bnlj = dkzzsfp3_fwbdc_bnlj;
	}

	public DZFDouble getSkqjfp3_fwbdc_bnlj() {
		return skqjfp3_fwbdc_bnlj;
	}

	public void setSkqjfp3_fwbdc_bnlj(DZFDouble skqjfp3_fwbdc_bnlj) {
		this.skqjfp3_fwbdc_bnlj = skqjfp3_fwbdc_bnlj;
	}

	public DZFDouble getYzzzs5_fwbdc_bnlj() {
		return yzzzs5_fwbdc_bnlj;
	}

	public void setYzzzs5_fwbdc_bnlj(DZFDouble yzzzs5_fwbdc_bnlj) {
		this.yzzzs5_fwbdc_bnlj = yzzzs5_fwbdc_bnlj;
	}

	public DZFDouble getDkzzsfp5_fwbdc_bnlj() {
		return dkzzsfp5_fwbdc_bnlj;
	}

	public void setDkzzsfp5_fwbdc_bnlj(DZFDouble dkzzsfp5_fwbdc_bnlj) {
		this.dkzzsfp5_fwbdc_bnlj = dkzzsfp5_fwbdc_bnlj;
	}

	public DZFDouble getSkqjfp5_fwbdc_bnlj() {
		return skqjfp5_fwbdc_bnlj;
	}

	public void setSkqjfp5_fwbdc_bnlj(DZFDouble skqjfp5_fwbdc_bnlj) {
		this.skqjfp5_fwbdc_bnlj = skqjfp5_fwbdc_bnlj;
	}

	public DZFDouble getMsxse_fwbdc_bnlj() {
		return msxse_fwbdc_bnlj;
	}

	public void setMsxse_fwbdc_bnlj(DZFDouble msxse_fwbdc_bnlj) {
		this.msxse_fwbdc_bnlj = msxse_fwbdc_bnlj;
	}

	public DZFDouble getXwqymsxse_fwbdc_bnlj() {
		return xwqymsxse_fwbdc_bnlj;
	}

	public void setXwqymsxse_fwbdc_bnlj(DZFDouble xwqymsxse_fwbdc_bnlj) {
		this.xwqymsxse_fwbdc_bnlj = xwqymsxse_fwbdc_bnlj;
	}

	public DZFDouble getWdqzdxse_fwbdc_bnlj() {
		return wdqzdxse_fwbdc_bnlj;
	}

	public void setWdqzdxse_fwbdc_bnlj(DZFDouble wdqzdxse_fwbdc_bnlj) {
		this.wdqzdxse_fwbdc_bnlj = wdqzdxse_fwbdc_bnlj;
	}

	public DZFDouble getQtmsxse_fwbdc_bnlj() {
		return qtmsxse_fwbdc_bnlj;
	}

	public void setQtmsxse_fwbdc_bnlj(DZFDouble qtmsxse_fwbdc_bnlj) {
		this.qtmsxse_fwbdc_bnlj = qtmsxse_fwbdc_bnlj;
	}

	public DZFDouble getCkmsxse_fwbdc_bnlj() {
		return ckmsxse_fwbdc_bnlj;
	}

	public void setCkmsxse_fwbdc_bnlj(DZFDouble ckmsxse_fwbdc_bnlj) {
		this.ckmsxse_fwbdc_bnlj = ckmsxse_fwbdc_bnlj;
	}

	public DZFDouble getCkms_skqjfp_fwbdc_bnlj() {
		return ckms_skqjfp_fwbdc_bnlj;
	}

	public void setCkms_skqjfp_fwbdc_bnlj(DZFDouble ckms_skqjfp_fwbdc_bnlj) {
		this.ckms_skqjfp_fwbdc_bnlj = ckms_skqjfp_fwbdc_bnlj;
	}

	public DZFDouble getHdxseysfwbnlj() {
		return hdxseysfwbnlj;
	}

	public void setHdxseysfwbnlj(DZFDouble hdxseysfwbnlj) {
		this.hdxseysfwbnlj = hdxseysfwbnlj;
	}

	public DZFDouble getBqynse_fwbdc_bnlj() {
		return bqynse_fwbdc_bnlj;
	}

	public void setBqynse_fwbdc_bnlj(DZFDouble bqynse_fwbdc_bnlj) {
		this.bqynse_fwbdc_bnlj = bqynse_fwbdc_bnlj;
	}

	public DZFDouble getHdynseysfwbnlj() {
		return hdynseysfwbnlj;
	}

	public void setHdynseysfwbnlj(DZFDouble hdynseysfwbnlj) {
		this.hdynseysfwbnlj = hdynseysfwbnlj;
	}

	public DZFDouble getBqynsejze_fwbdc_bnlj() {
		return bqynsejze_fwbdc_bnlj;
	}

	public void setBqynsejze_fwbdc_bnlj(DZFDouble bqynsejze_fwbdc_bnlj) {
		this.bqynsejze_fwbdc_bnlj = bqynsejze_fwbdc_bnlj;
	}

	public DZFDouble getBqmse_fwbdc_bnlj() {
		return bqmse_fwbdc_bnlj;
	}

	public void setBqmse_fwbdc_bnlj(DZFDouble bqmse_fwbdc_bnlj) {
		this.bqmse_fwbdc_bnlj = bqmse_fwbdc_bnlj;
	}

	public DZFDouble getXwqymse_fwbdc_bnlj() {
		return xwqymse_fwbdc_bnlj;
	}

	public void setXwqymse_fwbdc_bnlj(DZFDouble xwqymse_fwbdc_bnlj) {
		this.xwqymse_fwbdc_bnlj = xwqymse_fwbdc_bnlj;
	}

	public DZFDouble getWdqzdmse_fwbdc_bnlj() {
		return wdqzdmse_fwbdc_bnlj;
	}

	public void setWdqzdmse_fwbdc_bnlj(DZFDouble wdqzdmse_fwbdc_bnlj) {
		this.wdqzdmse_fwbdc_bnlj = wdqzdmse_fwbdc_bnlj;
	}

	public DZFDouble getYnsehj_fwbdc_bnlj() {
		return ynsehj_fwbdc_bnlj;
	}

	public void setYnsehj_fwbdc_bnlj(DZFDouble ynsehj_fwbdc_bnlj) {
		this.ynsehj_fwbdc_bnlj = ynsehj_fwbdc_bnlj;
	}

	public DZFDouble getBqyjse_fwbdc_bnlj() {
		return bqyjse_fwbdc_bnlj;
	}

	public void setBqyjse_fwbdc_bnlj(DZFDouble bqyjse_fwbdc_bnlj) {
		this.bqyjse_fwbdc_bnlj = bqyjse_fwbdc_bnlj;
	}

	public DZFDouble getBqybtse_fwbdc_bnlj() {
		return bqybtse_fwbdc_bnlj;
	}

	public void setBqybtse_fwbdc_bnlj(DZFDouble bqybtse_fwbdc_bnlj) {
		this.bqybtse_fwbdc_bnlj = bqybtse_fwbdc_bnlj;
	}
}
