package com.dzf.zxkj.platform.model.sys;

//import com.dzf.model.gl.gl_pjgl.VATInComInvoiceVO;

import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.gzgl.*;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.VatBusinessTypeVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.*;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.qcset.VerifyBeginVo;
import com.dzf.zxkj.platform.model.report.XjllQcyeVO;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.st.*;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.model.zcgl.*;

public interface IBackAndRestore {
	
	public static Class[] classes = new Class[] {
		CorpVO.class,
		AssetcardVO.class,
		AssetCleanVO.class,
		BDabstractsVO.class,
		BdAssetCategoryVO.class,
		YntCpaccountVO.class,
		CpcosttransVO.class,
		YntCpmbVO.class,
		YntCpmbBVO.class,
		PzmbhVO.class,
		PzmbbVO.class,
		YntCptransmbHVO.class,
		YntCptransmbBVO.class,
		AssetDepreciaTionVO.class,
		EAModelBVO.class,
		EAModelHVO.class,
		ExpBillBVO.class,
		ExpBillHVO.class,
		ExrateVO.class,
		GdzcjzVO.class,
		IcbalanceVO.class,
		IctradeinVO.class,
		IntradeoutVO.class,
		ImageGroupVO.class,
		ImageLibraryVO.class,
		IncomeWarningVO.class,
//		Industinvtory_qcvo.class,
		InvclassifyVO.class,
		InventoryVO.class,
		KMQMJZVO.class,
		MeasureVO.class,
		NmTaxTab1VO.class,
		NmTaxTab2VO.class,
		PersonalSetVO.class,
		QcYeVO.class,
		QmclVO.class,
		QmJzVO.class,
		RemittanceVO.class,
		StGgywfVO.class,
		StJmsdsVO.class,
		StJzzcVO.class,
		StMbksVO.class,
		StNssbInfoVO.class,
		StNssbMainVO.class,
		StNstzmxVO.class,
		StqjfyVO.class,
		StYbqycbVO.class,
		StYbqysrVO.class,
		StYjdNssbMainVO.class,
		StZgxcNstzVO.class,
		StZjtxNstzVO.class,
		InvCurentVO.class,
		TzpzHVO.class,
		TzpzBVO.class,
		ValuemodifyVO.class,
		XjllVO.class,
		XjllQcyeVO.class,
		AuxiliaryAccountBVO.class,
		AuxiliaryAccountHVO.class,
		FzhsqcVO.class,
		YntCpaccountChangeVO.class,
		//工资设置
		JudgeIsGZVO.class,
		//参数设置
		YntParameterSet.class,
		SalaryAccSetVO.class,
		SalaryReportVO.class,
		SalaryBaseVO.class,
		SalaryKmDeptVO.class,
		//计提税金+工作量
		WorkloadManagementVO.class,
			SurtaxTemplateVO.class,
		//纳税申报
//		TaxTypeListVO.class,
//		TaxTypeListDetailVO.class,
//		TaxZsxmVO.class,
//		TaxSbzlVO.class,
		TaxReportVO.class,
		TaxReportEntVO.class,
		TaxReportDetailVO.class,
		TaxReportInitVO.class,
		TaxRptTempletPosVO.class,
		TaxRptTempletVO.class,
		//采购单,入库，出库
		InvAccSetVO.class,
		IntradeHVO.class,//采购单，销售单表头
		//ocr 中间表  表头  表体
		OcrImageGroupVO.class,
		OcrImageLibraryVO.class,
		//IctradeinVO.class,//采购单表体
		//IntradeoutVO.class,//销售单表体
		BankAccountVO.class,//银行账户
//		BankStatementVO.class,//银行对账单
		BankBillToStatementVO.class,
//		VATSaleInvoiceVO.class,//销项单
//		VATSaleInvoiceBVO.class,
//		VATInComInvoiceVO.class,//进项单
//		VATInComInvoiceBVO.class,
		IncomeHistoryVo.class,//收入预警历史维护
		VerifyBeginVo.class,//未核销期初
		TaxCalculateArchiveVO.class,//税收测算表税种信息
		InventorySetVO.class,
		InventoryAliasVO.class,
		//报税表
		TaxPosContrastVO.class,//山东报税对照表
		TaxReportNewQcInitVO.class,//期初数据存储 存放一键报税接口期初 
//		TaxYjbsUpdateVO.class,//一键报税版本、发票扫描对照VO
		TaxTypeSBZLVO.class,
		OcrInvoiceColumnVO.class,
//		IncomeWarningVO.class,//预警设置
		TaxitemRelationVO.class,
		PZTaxItemRadioVO.class,
		InventoryQcVO.class,
		CorpTaxRptVO.class,
		CommonAssistVO.class,
		VoucherMergeSettingVO.class,
		CorpTaxVo.class,//纳税信息维护
		PzSourceRelationVO.class,//凭证关联关系表
		VatBusinessTypeVO.class,//业务类型勾选记忆
		PrintSettingVO.class,//打印设置
		VatInvoiceSetVO.class,//销项发票设置，
		TaxEffeHistVO.class,//征收历史记录
		SpecDeductHistVO.class,
			// 税费计算-增值税
			AddValueTaxVO.class,
			// 税费计算-增值税
			AddValueTaxCalVO.class,
			// 税费计算-附加税
			SurtaxVO.class,
			// 税费计算-附加税模板
			SurTaxTemplate.class,
			// 税费计算-所得税
			IncomeTaxVO.class
		};
	
}
