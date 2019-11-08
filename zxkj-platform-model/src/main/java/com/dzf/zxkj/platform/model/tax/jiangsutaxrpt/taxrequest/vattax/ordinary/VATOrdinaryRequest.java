package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

/**
 * 增值税一般人申报数据
 * 
 * @author liubj
 *
 */
public class VATOrdinaryRequest {

	// 增值税纳税申报表（适用于增值税一般纳税人）
	private TaxReturn sb10101001vo_01;

	// 增值税纳税申报表附表一（本期销售情况明细表）
	private TaxReturnAttach1 sb10101002vo_01;

	// 增值税纳税申报报表附表二（本期进项税额明细表）
	private TaxReturnAttach2 sb10101003vo_01;

	// 增值税纳税申报表附表资料三（应税服务扣除项目明细）
	private TaxReturnAttach3 sb10101004vo_01;

	// 增值税纳税申报表附表四（税额抵减情况表）
	private TaxReturnAttach4 sb10101005vo_01;

	// 增值税纳税申报表附表五（不动产分期抵扣计算表）
//	private TaxReturnAttach5 sb10101023vo_01;

	// 增值税减免税申报明细表合计
	private TaxReliefSum sb10101021vo_04;

	// 增值税减免税申报明细表 减税项目
	private TaxCut[] sb10101021vo_01;

	// 增值税减免税申报明细表 免税项目
	private TaxFree[] sb10101021vo_02;

	// 出口免税 && 其中：跨境服务 免征增值税项目
	private TaxReliefAttach sb10101021vo_03;

	// 本期抵扣进项税额结构明细表
	// private InputTaxDeductDetail sb10101022vo_01;

	// 营改增税负分析测算明细表
//	private BusinessToAddTaxAnalyzeDetail[] sb10101024vo_01;

	// 营改增税负分析测算明细表合计
//	private BusinessToAddTaxAnalyzeSum sb10101024vo_02;

	// 固定资产进项税额抵扣情况表
	// private FixedAssetsDeduct sb10101006vo_01;

	// 代扣代缴税收通用缴款书抵扣清单
	private WithholdTaxDeductList[] sb10101007vo_01;

	private WithholdTaxDeductListSum sb10101007vo_02;

	// 成品油购销存情况明细表
	private OilProductsPurchaseSaleDetail[] sb10101009vo_01;

	// 成品油购销存情况明细表合计
	private OilProductsPurchaseSaleDetailSum sb10101009vo_02;

	// 农产品核定扣除增值税进项税额计算表（汇总表）
	private AgriculturalProductsDeductInputTax sb10101010vo_01;

	// 农产品核定扣除增值税进项税额计算表（汇总表）合计
	private AgriculturalProductsDeductInputTaxSum sb10101010vo_02;

	// 投入产出法核定农产品增值税进项税额计算表(附表一)
	private InOutputMethodAgriculturalProductsInputTax[] sb10101011vo_01;

	// 投入产出法核定农产品增值税进项税额计算表(附表一)合计
	private InOutputMethodAgriculturalProductsInputTaxSum sb10101011vo_02;

	// 成本法核定农产品增值税进项税额计算表
	private CostMethodAgriculturalProductsInputTax[] sb10101012vo_01;

	// 成本法核定农产品增值税进项税额计算表合计
	private CostMethodAgriculturalProductsInputTaxSum sb10101012vo_02;

	// 购进农产品直接销售核定农产品增值税进项税额计算表
	private PurchaseAgriculturalProductsInputTax[] sb10101013vo_01;

	// 购进农产品直接销售核定农产品增值税进项税额计算表合计
	private PurchaseAgriculturalProductsInputTaxSum sb10101013vo_02;

	// 购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额计算表
	private PurchaseAgriculturalProductsNoEntityInputTax[] sb10101014vo_01;

	// 购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额计算表合计
	private PurchaseAgriculturalProductsNoEntityInputTaxSum sb10101014vo_02;

	// 部分产品销售统计表
	private ProductSalesStatistics sb10101020vo_01;

	// 生产企业进料加工抵扣明细表
	private ProcessImportedMaterialDeduct[] sb10101015vo_01;
	// 生产企业出口货物征（免）税明细主表
	private ExportGoodsTaxMaster[] sb10101016vo_01;
	// 生产企业出口货物征（免）税明细从表
	private ExportGoodsTaxSlave[] sb10101017vo_01;
	// 国际运输征免税明细数据
	private InternationalTransportFreeTax[] sb10101018vo_01;
	// 研发、设计服务征免税明细数据
	private ResearchAndDevelopmentAndDesignTax[] sb10101019vo_01;

	public TaxReturn getSb10101001vo_01() {
		return sb10101001vo_01;
	}

	public void setSb10101001vo_01(TaxReturn sb10101001vo_01) {
		this.sb10101001vo_01 = sb10101001vo_01;
	}

	public TaxReturnAttach1 getSb10101002vo_01() {
		return sb10101002vo_01;
	}

	public void setSb10101002vo_01(TaxReturnAttach1 sb10101002vo_01) {
		this.sb10101002vo_01 = sb10101002vo_01;
	}

	public TaxReturnAttach2 getSb10101003vo_01() {
		return sb10101003vo_01;
	}

	public void setSb10101003vo_01(TaxReturnAttach2 sb10101003vo_01) {
		this.sb10101003vo_01 = sb10101003vo_01;
	}

	public TaxReturnAttach3 getSb10101004vo_01() {
		return sb10101004vo_01;
	}

	public void setSb10101004vo_01(TaxReturnAttach3 sb10101004vo_01) {
		this.sb10101004vo_01 = sb10101004vo_01;
	}

	public TaxReturnAttach4 getSb10101005vo_01() {
		return sb10101005vo_01;
	}

	public void setSb10101005vo_01(TaxReturnAttach4 sb10101005vo_01) {
		this.sb10101005vo_01 = sb10101005vo_01;
	}

	public TaxReliefSum getSb10101021vo_04() {
		return sb10101021vo_04;
	}

	public void setSb10101021vo_04(TaxReliefSum sb10101021vo_04) {
		this.sb10101021vo_04 = sb10101021vo_04;
	}

	public TaxCut[] getSb10101021vo_01() {
		return sb10101021vo_01;
	}

	public void setSb10101021vo_01(TaxCut[] sb10101021vo_01) {
		this.sb10101021vo_01 = sb10101021vo_01;
	}

	public TaxFree[] getSb10101021vo_02() {
		return sb10101021vo_02;
	}

	public void setSb10101021vo_02(TaxFree[] sb10101021vo_02) {
		this.sb10101021vo_02 = sb10101021vo_02;
	}

	public TaxReliefAttach getSb10101021vo_03() {
		return sb10101021vo_03;
	}

	public void setSb10101021vo_03(TaxReliefAttach sb10101021vo_03) {
		this.sb10101021vo_03 = sb10101021vo_03;
	}

	// public InputTaxDeductDetail getSb10101022vo_01() {
	// return sb10101022vo_01;
	// }
	//
	// public void setSb10101022vo_01(InputTaxDeductDetail sb10101022vo_01) {
	// this.sb10101022vo_01 = sb10101022vo_01;
	// }
	// public FixedAssetsDeduct getSb10101006vo_01() {
	// return sb10101006vo_01;
	// }
	//
	// public void setSb10101006vo_01(FixedAssetsDeduct sb10101006vo_01) {
	// this.sb10101006vo_01 = sb10101006vo_01;
	// }

	public WithholdTaxDeductList[] getSb10101007vo_01() {
		return sb10101007vo_01;
	}

	public void setSb10101007vo_01(WithholdTaxDeductList[] sb10101007vo_01) {
		this.sb10101007vo_01 = sb10101007vo_01;
	}

	public WithholdTaxDeductListSum getSb10101007vo_02() {
		return sb10101007vo_02;
	}

	public void setSb10101007vo_02(WithholdTaxDeductListSum sb10101007vo_02) {
		this.sb10101007vo_02 = sb10101007vo_02;
	}

	public OilProductsPurchaseSaleDetail[] getSb10101009vo_01() {
		return sb10101009vo_01;
	}

	public void setSb10101009vo_01(
			OilProductsPurchaseSaleDetail[] sb10101009vo_01) {
		this.sb10101009vo_01 = sb10101009vo_01;
	}

	public OilProductsPurchaseSaleDetailSum getSb10101009vo_02() {
		return sb10101009vo_02;
	}

	public void setSb10101009vo_02(
			OilProductsPurchaseSaleDetailSum sb10101009vo_02) {
		this.sb10101009vo_02 = sb10101009vo_02;
	}

	public AgriculturalProductsDeductInputTax getSb10101010vo_01() {
		return sb10101010vo_01;
	}

	public void setSb10101010vo_01(
			AgriculturalProductsDeductInputTax sb10101010vo_01) {
		this.sb10101010vo_01 = sb10101010vo_01;
	}

	public AgriculturalProductsDeductInputTaxSum getSb10101010vo_02() {
		return sb10101010vo_02;
	}

	public void setSb10101010vo_02(
			AgriculturalProductsDeductInputTaxSum sb10101010vo_02) {
		this.sb10101010vo_02 = sb10101010vo_02;
	}

	public InOutputMethodAgriculturalProductsInputTax[] getSb10101011vo_01() {
		return sb10101011vo_01;
	}

	public void setSb10101011vo_01(
			InOutputMethodAgriculturalProductsInputTax[] sb10101011vo_01) {
		this.sb10101011vo_01 = sb10101011vo_01;
	}

	public InOutputMethodAgriculturalProductsInputTaxSum getSb10101011vo_02() {
		return sb10101011vo_02;
	}

	public void setSb10101011vo_02(
			InOutputMethodAgriculturalProductsInputTaxSum sb10101011vo_02) {
		this.sb10101011vo_02 = sb10101011vo_02;
	}

	public CostMethodAgriculturalProductsInputTax[] getSb10101012vo_01() {
		return sb10101012vo_01;
	}

	public void setSb10101012vo_01(
			CostMethodAgriculturalProductsInputTax[] sb10101012vo_01) {
		this.sb10101012vo_01 = sb10101012vo_01;
	}

	public CostMethodAgriculturalProductsInputTaxSum getSb10101012vo_02() {
		return sb10101012vo_02;
	}

	public void setSb10101012vo_02(
			CostMethodAgriculturalProductsInputTaxSum sb10101012vo_02) {
		this.sb10101012vo_02 = sb10101012vo_02;
	}

	public PurchaseAgriculturalProductsInputTax[] getSb10101013vo_01() {
		return sb10101013vo_01;
	}

	public void setSb10101013vo_01(
			PurchaseAgriculturalProductsInputTax[] sb10101013vo_01) {
		this.sb10101013vo_01 = sb10101013vo_01;
	}

	public PurchaseAgriculturalProductsInputTaxSum getSb10101013vo_02() {
		return sb10101013vo_02;
	}

	public void setSb10101013vo_02(
			PurchaseAgriculturalProductsInputTaxSum sb10101013vo_02) {
		this.sb10101013vo_02 = sb10101013vo_02;
	}

	public PurchaseAgriculturalProductsNoEntityInputTax[] getSb10101014vo_01() {
		return sb10101014vo_01;
	}

	public void setSb10101014vo_01(
			PurchaseAgriculturalProductsNoEntityInputTax[] sb10101014vo_01) {
		this.sb10101014vo_01 = sb10101014vo_01;
	}

	public PurchaseAgriculturalProductsNoEntityInputTaxSum getSb10101014vo_02() {
		return sb10101014vo_02;
	}

	public void setSb10101014vo_02(
			PurchaseAgriculturalProductsNoEntityInputTaxSum sb10101014vo_02) {
		this.sb10101014vo_02 = sb10101014vo_02;
	}

	public ProductSalesStatistics getSb10101020vo_01() {
		return sb10101020vo_01;
	}

	public void setSb10101020vo_01(ProductSalesStatistics sb10101020vo_01) {
		this.sb10101020vo_01 = sb10101020vo_01;
	}

	public ProcessImportedMaterialDeduct[] getSb10101015vo_01() {
		return sb10101015vo_01;
	}

	public void setSb10101015vo_01(
			ProcessImportedMaterialDeduct[] sb10101015vo_01) {
		this.sb10101015vo_01 = sb10101015vo_01;
	}

	public ExportGoodsTaxMaster[] getSb10101016vo_01() {
		return sb10101016vo_01;
	}

	public void setSb10101016vo_01(ExportGoodsTaxMaster[] sb10101016vo_01) {
		this.sb10101016vo_01 = sb10101016vo_01;
	}

	public ExportGoodsTaxSlave[] getSb10101017vo_01() {
		return sb10101017vo_01;
	}

	public void setSb10101017vo_01(ExportGoodsTaxSlave[] sb10101017vo_01) {
		this.sb10101017vo_01 = sb10101017vo_01;
	}

	public InternationalTransportFreeTax[] getSb10101018vo_01() {
		return sb10101018vo_01;
	}

	public void setSb10101018vo_01(
			InternationalTransportFreeTax[] sb10101018vo_01) {
		this.sb10101018vo_01 = sb10101018vo_01;
	}

	public ResearchAndDevelopmentAndDesignTax[] getSb10101019vo_01() {
		return sb10101019vo_01;
	}

	public void setSb10101019vo_01(
			ResearchAndDevelopmentAndDesignTax[] sb10101019vo_01) {
		this.sb10101019vo_01 = sb10101019vo_01;
	}

}
