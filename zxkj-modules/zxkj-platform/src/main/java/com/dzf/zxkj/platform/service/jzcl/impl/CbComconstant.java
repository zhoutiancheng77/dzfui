package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工业成本结转用
 *
 */
@Service("gl_cbconstant")
public class CbComconstant implements ICbComconstant {

	private String fzcb2007 = "500102";// 500102 辅助生产成本 2007

	private String zzfy2007 = "5101";// 5101 制造费用

	private String fzcb2013 = "400102";// 400102 辅助生产成本 2013

	private String zzfy2013 = "4101";// 4101 制造费用

	private String jbcb_zjcl2007 = "50010101";// 生产成本--基本生产成本--直接材料 2007
	private String jbcb_zjrg2007 = "50010102";// 生产成本--基本生产成本--直接人工 2007
	private String jbcb_zzfy2007 = "50010103";// 生产成本--基本生产成本--制造费用 2007

	private String jbcb_zjcl2013 = "40010101";// 生产成本--基本生产成本--直接材料 2013
	private String jbcb_zjrg2013 = "40010102";// 生产成本--基本生产成本--直接人工 2013
	private String jbcb_zzfy2013 = "40010103";// 生产成本--基本生产成本--制造费用 2013

	private String kcsp_code = "1405";// 库存商品
	private String ycl_code = "1403";// 原材料
	private String jzcb_ycl2007 = "640201";// 材料销售成本
	private String jzcb_kcsp2007 = "640101";// 商品销售成本

	private String jzcb_ycl2013 = "540201";// 材料销售成本
	private String jzcb_kcsp2013 = "540101";// 商品销售成本

	private String zg_code = "2202";// 暂估凭证 科目[应付账款]

	private String fangan_2013 = "00000100AA10000000000BMD";// 科目方案2013

	private String fangan_2007 = "00000100AA10000000000BMF";// 科目方案2007

	private IYntBoPubUtil yntBoPubUtil;

	public IYntBoPubUtil getYntBoPubUtil() {
		return yntBoPubUtil;
	}

	@Autowired
	public void setYntBoPubUtil(IYntBoPubUtil yntBoPubUtil) {
		this.yntBoPubUtil = yntBoPubUtil;
	}

	@Override
	public String getFzcb2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(fzcb2007, pk_corp);
	}

	@Override
	public String getZzfy2007() {
		return zzfy2007;
	}

	@Override
	public String getFzcb2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(fzcb2013, pk_corp);
	}

	@Override
	public String getZzfy2013() {
		return zzfy2013;
	}

	@Override
	public String getJbcb_zjcl2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zjcl2007, pk_corp);
	}

	@Override
	public String getJbcb_zjrg2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zjrg2007, pk_corp);
	}

	@Override
	public String getJbcb_zzfy2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zzfy2007, pk_corp);
	}

	@Override
	public String getJbcb_zjcl2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zjcl2013, pk_corp);
	}

	@Override
	public String getJbcb_zjrg2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zjrg2013, pk_corp);
	}

	@Override
	public String getJbcb_zzfy2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jbcb_zzfy2013, pk_corp);
	}

	@Override
	public String getKcsp_code() {
		return kcsp_code;
	}

	@Override
	public String getYcl_code() {
		return ycl_code;
	}

	@Override
	public String getZg_code() {
		return zg_code;
	}

	@Override
	public String getFangan_2013() {
		return fangan_2013;
	}

	@Override
	public String getFangan_2007() {
		return fangan_2007;
	}

	@Override
	public String getJzcb_ycl2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jzcb_ycl2007, pk_corp);
	}

	@Override
	public String getJzcb_kcsp2007(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jzcb_kcsp2007, pk_corp);
	}

	@Override
	public String getJzcb_ycl2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jzcb_ycl2013, pk_corp);
	}

	@Override
	public String getJzcb_kcsp2013(String pk_corp) {
		return yntBoPubUtil.createRulecodebyCorp(jzcb_kcsp2013, pk_corp);
	}

}
