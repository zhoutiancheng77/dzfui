package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.*;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.tax.SpecDeductHistVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.pzgl.impl.CaclTaxMny;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.TaxRptemptools;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service("gl_tax_formulaimpl")
@Slf4j
public class TAXBalaceCcrSrvImpl implements ITaxBalaceCcrService {

	String glmessage = "00000000000000000000000000";// 内部吃掉的异常消息开头
	private String pk_curr = null;
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IQcye gl_qcyeserv;
	@Autowired
	private IParameterSetService paramterService;
	@Autowired
	private ITaxDeclarationService taxDeclarationService;
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private IQmclService gl_qmclserv;
	@Autowired
	private ICorpService corpService;

	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;

	/**
	 * 期初
	 */
	@Override
	public DZFDouble getGlOpenBal(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)
			throws DZFWarpException {
		try {
			DZFDate date = new DZFDate(period + "-01").getDateBefore(1);
			// 期初及上个月期末数据
			return getGlCloseBal(subjcode, date.toString().substring(0, 7), dc, pk_corp, ishasjz,accountVO);
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}

			throw new WiseRunException(e);
		}

	}

	/**
	 * 年初
	 */
	@Override
	public DZFDouble getGlBalNC(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,YntCpaccountVO[] accountVO)
			throws DZFWarpException {
		try {
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			DZFDate begindate = corpvo.getBegindate();// 建账日期
			int opeYear = Integer.parseInt(period.substring(0, 4));
			DZFDouble qcvalue;
			String sql;
			if (begindate.getYear() - opeYear == 0) {
				sql = " select yearqc from ynt_qcye y Where y.pk_corp = '" + pk_corp + "' and y.pk_accsubj in "
						+ getSubjSQL(subjcode, pk_corp, null, accountVO);
			} else {
				// 求年初余额
				String yearperiod = period.substring(0, 4) + "-12";
				sql = " select sum(YNT_KMQMJZ.Thismonthqc) qc From YNT_KMQMJZ where pk_corp='" + pk_corp
						+ "' and period='" + yearperiod + "' and nvl(dr,0)=0 " + " and  pk_accsubj in "
						+ getSubjSQL(subjcode, pk_corp, null, accountVO);
			}
			qcvalue = executeSqlAsDzfDouble(sql, null);

			YntCpaccountVO accountvo = getpkSubj(subjcode, pk_corp, accountVO);
			if ((dc.equals("dr") && accountvo.getDirection().intValue() == 0) // 都是借方和都是贷方时候不用对方向处理
					|| (dc.equals("cr") && accountvo.getDirection().intValue() == 1)) {
				return qcvalue;
			} else {
				return qcvalue.multiply(-1);
			}
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}

			throw new WiseRunException(e);
		}

	}

	/**
	 * 
	 * 求期末余额
	 */
	@Override
	public DZFDouble getGlCloseBal(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,YntCpaccountVO[] accountVO)
			throws DZFWarpException {
		try {
			// 求年初余额
			String yearperiod = period.substring(0, 4) + "-12";
			SQLParameter sp = new SQLParameter();
			String sql = " select sum(YNT_KMQMJZ.Thismonthqc) qc From YNT_KMQMJZ where pk_corp='" + pk_corp
					+ "' and period='" + yearperiod + "' and nvl(dr,0)=0 " + " and  pk_accsubj in "
					+ getSubjSQL(subjcode, pk_corp, null, accountVO);
			// 年初金额
			DZFDouble qcvalue = executeSqlAsDzfDouble(sql, sp);

			DZFDouble balmoney = null;
			String condition = " and (h.period>='" + period.substring(0, 4) + "-01'  and h.period<= '" + period
					+ "'  )";// 期间
			List<KmZzVO> tt = getKmZzVOByCondition(ishasjz, subjcode, pk_corp, condition, null, null, accountVO);

			YntCpaccountVO accountvo = getpkSubj(subjcode, pk_corp, accountVO);

			DZFDouble jfje = tt.get(0).getJf() == null ? DZFDouble.ZERO_DBL : tt.get(0).getJf();
			DZFDouble dfje = tt.get(0).getDf() == null ? DZFDouble.ZERO_DBL : tt.get(0).getDf();
			if (accountvo.getDirection().intValue() == 0) {// 期末余额=期初+ 借方-贷方
				balmoney = qcvalue.add(jfje).sub(dfje);
			} else {// 净期末余额=期初+ 贷方-借方
				balmoney = qcvalue.add(dfje).sub(jfje);
			}

			if ((dc.equals("dr") && accountvo.getDirection().intValue() == 0) // 都是借方和都是贷方时候不用对方向处理
					|| (dc.equals("cr") && accountvo.getDirection().intValue() == 1)) {
				return balmoney;
			} else {
				return balmoney.multiply(-1);
			}
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}
			throw new WiseRunException(e);
		}

	}

	/****
	 * 获取发生额
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getGlAmtoCcr(String subjcode, String periodFrom, String periodTo, String dc, String pk_corp,
			DZFBoolean ishasjz,YntCpaccountVO[] accountVO) throws DZFWarpException {
		DZFDouble ccrmny = DZFDouble.ZERO_DBL;
		try {
			String condition = (periodFrom.equals(periodTo) ? " and h.period= '" + periodFrom + "' "
					: " and h.period >= '" + periodFrom + "' and h.period <= '" + periodTo + "' ");// 期间
			List<KmZzVO> tt = getKmZzVOByCondition(ishasjz, subjcode, pk_corp, condition, null, null,accountVO);

			if ("dr".equals(dc)) {// 借方
				ccrmny = tt.get(0).getJf();
			} else {
				ccrmny = tt.get(0).getDf();
			}

			if (ccrmny == null) {
				ccrmny = new DZFDouble("0.0");
			}

		} catch (DZFWarpException e) {

//			if (e.getMessage().lastIndexOf(glmessage) != -1) {
//				ccrmny =  DZFDouble.ZERO_DBL;
//			}
			log.error("错误",e);
		}
		return ccrmny;
	}

	/****
	 * 获取发生额算法二，增加了两个参数，开票和税率 invoiceflag 1/2/3 普票/专票/未开票 空：不处理改字段 taxrate 税率 ，
	 * 空代表全部税率
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getGlAmtoCcr2(String subjcode, String periodFrom, String periodTo, String dc, String pk_corp,
			DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) throws DZFWarpException {
		DZFDouble ccrmny = DZFDouble.ZERO_DBL;
		try {
			String condition = (periodFrom.equals(periodTo) ? " and h.period= '" + periodFrom + "' "
					: " and h.period>= '" + periodFrom + "' and h.period<='" + periodTo + "' ");
			;// 期间
			List<KmZzVO> tt = getKmZzVOByCondition(ishasjz, subjcode, pk_corp, condition, invoiceflag, taxrate, accountVO);

			if ("dr".equals(dc)) {// 借方
				ccrmny = tt.get(0).getJf();
			} else {
				ccrmny = tt.get(0).getDf();
			}

			if (ccrmny == null) {
				ccrmny = new DZFDouble("0.0");
			}

			
		} catch (DZFWarpException e) {

//			if (e.getMessage().lastIndexOf(glmessage) != -1) {
//				ccrmny =  DZFDouble.ZERO_DBL;
//			}
			log.error("错误",e);
		}
		return ccrmny;
	}

	/****
	 * 获取季度发生额 获取发生额算法四，增加了两个参数，开票和税率 invoiceflag 1/2/3 普票/专票/未开票 空：不处理改字段
	 * taxrate 税率 ， 空代表全部税率
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getGlAmtoCcr4(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,
			String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) throws DZFWarpException {
		try {
			// DZFDouble ccrmny = getGlAmtoCcr2(subjcode, period, dc, pk_corp,
			// ishasjz, invoiceflag, taxrate);
			//
			// String prevPeriod = new DZFDate(period +
			// "-01").getDateBefore(10).toString().substring(0, 7);
			// ccrmny = ccrmny.add( getGlAmtoCcr2(subjcode, prevPeriod, dc,
			// pk_corp, ishasjz, invoiceflag, taxrate));
			//
			// prevPeriod = new DZFDate(period +
			// "-01").getDateBefore(40).toString().substring(0, 7);
			// ccrmny = ccrmny.add( getGlAmtoCcr2(subjcode, prevPeriod, dc,
			// pk_corp, ishasjz, invoiceflag, taxrate));
			//
			// return ccrmny;

			String prevPeriod = new DZFDate(period + "-01").getDateBefore(40).toString().substring(0, 7);

			return getGlAmtoCcr2(subjcode, prevPeriod, period, dc, pk_corp, ishasjz, invoiceflag, taxrate, accountVO);
		} catch (DZFWarpException e) {
			throw new WiseRunException(e);
		}
	}

	/****
	 * 获取季度发生额 根据传入的期间，倒推两个期间合计
	 * 
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlAmtoCcr3(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,YntCpaccountVO[] accountVO)
			throws DZFWarpException {
		try {
			// DZFDouble ccrmny = getGlAmtoCcr(subjcode, period, dc, pk_corp,
			// ishasjz);
			//
			// String prevPeriod = new DZFDate(period +
			// "-01").getDateBefore(10).toString().substring(0, 7);
			// ccrmny = ccrmny.add( getGlAmtoCcr(subjcode, prevPeriod, dc,
			// pk_corp, ishasjz));
			//
			// prevPeriod = new DZFDate(period +
			// "-01").getDateBefore(40).toString().substring(0, 7);
			// ccrmny = ccrmny.add( getGlAmtoCcr(subjcode, prevPeriod, dc,
			// pk_corp, ishasjz));
			// return ccrmny;
			String prevPeriod = new DZFDate(period + "-01").getDateBefore(40).toString().substring(0, 7);

			return getGlAmtoCcr(subjcode, prevPeriod, period, dc, pk_corp, ishasjz, accountVO);
		} catch (DZFWarpException e) {
			throw new WiseRunException(e);
		}
	}

	/****
	 * 获取凭证张数
	 * 
	 * @throws DZFWarpException
	 */
	public Integer getTrans(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,
			String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) throws DZFWarpException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" select count(*) ");
			sb.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h inner join ");
			sb.append(" ynt_cpaccount a on b.pk_accsubj=a.pk_corp_account where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
			// 增加发票类型 1普票 2专票 3未开票
			if (StringUtil.isEmptyWithTrim(invoiceflag) == false) {
				CorpVO corpvo = corpService.queryByPk(pk_corp);
				String corpNature = corpvo.getChargedeptname();
				String defaultInvoice = "一般纳税人".equals(corpNature) ? Integer.toString(IFpStyleEnum.SPECINVOICE.getValue()) : Integer.toString(IFpStyleEnum.COMMINVOICE.getValue()) ;
				sb.append(" and nvl(h.fp_style,").append(defaultInvoice).append(")=").append(invoiceflag);
			}
			sb.append(" and b.pk_currency ='" + getCNYCurr() + "' ");// 币种
			sb.append(" and h.period= '" + period + "' ");// 期间);
			// 不包含未记账，即只查询已记账的 Y是否包含未记账
			if (!ishasjz.booleanValue()) {
				sb.append(" and h.ishasjz='Y' ");
				sb.append(" and h.vbillstatus=1 ");
			}

			if ("dr".equals(dc)) {// 借方
				sb.append(" and b.jfmny <> 0");
			} else {
				sb.append(" and b.dfmny <> 0");
			}

			sb.append(" and b.pk_accsubj in " + getSubjSQL(subjcode, pk_corp, taxrate,accountVO));
			SQLParameter parameter = new SQLParameter();
			Object tt = getSingleObjectBO().executeQuery(sb.toString(), parameter, new ObjectProcessor());

			return (tt == null ? 0 : Integer.parseInt(tt.toString()));
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return 0;
			}
			throw new WiseRunException(e);
		}
	}

	@Override
	public Integer getTrans1(String taxcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,
			String invoiceflag, DZFDouble taxrate) throws DZFWarpException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" select count(1) from ynt_pztaxitem t ");
			sb.append(" left join ynt_tzpz_b b on b.pk_tzpz_b = t.pk_tzpz_b ")
					.append(" left join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h ")
					.append(" where t.pk_corp = ? and t.taxcode = ?  ")
					.append(" and nvl(t.dr,0)=0 and nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ")
//					.append(" and b.pk_currency ='" + IGlobalConstants.RMB_currency_id + "' ")
					.append(" and h.period = ? ");
			// 发票类型
			if (!StringUtil.isEmptyWithTrim(invoiceflag)) {
				String[] invoiceflags = invoiceflag.split("\\+");
				CorpVO corpvo = corpService.queryByPk(pk_corp);
				String corpNature = corpvo.getChargedeptname();
				String defaultInvoice = "一般纳税人".equals(corpNature) ? Integer.toString(IFpStyleEnum.SPECINVOICE.getValue()) : Integer.toString(IFpStyleEnum.COMMINVOICE.getValue()) ;
				sb.append(" and nvl(h.fp_style,").append(defaultInvoice).append(") in (");
				for (int i = 0; i < invoiceflags.length; i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(invoiceflags[i]);
				}
				sb.append(")");
			}
			// 不包含未记账，即只查询已记账的 Y是否包含未记账
			if (!ishasjz.booleanValue()) {
				sb.append(" and h.ishasjz = 'Y' ");
				sb.append(" and h.vbillstatus = 1 ");
			}
			if ("dr".equals(dc)) {// 借方
				sb.append(" and b.jfmny <> 0");
			} else {
				sb.append(" and b.dfmny <> 0");
			}
			SQLParameter param = new SQLParameter();
			param.addParam(pk_corp);
			param.addParam(taxcode);
			param.addParam(period);
			BigDecimal rs = (BigDecimal) getSingleObjectBO().executeQuery(sb.toString(), param, new ColumnProcessor());
			return (rs == null ? 0 : rs.intValue());
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return 0;
			}
			throw new WiseRunException(e);
		}
	}

	/****
	 * 获取净发生额
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getGlNetamtoCcr(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,YntCpaccountVO[] accountVO)
			throws DZFWarpException {
		try {
			DZFDouble ccrmny = null;

			String condition = " and h.period= '" + period + "'  ";// 期间
			List<KmZzVO> tt = getKmZzVOByCondition(ishasjz, subjcode, pk_corp, condition, null, null, accountVO);
			DZFDouble jfje = tt.get(0).getJf() == null ? DZFDouble.ZERO_DBL : tt.get(0).getJf();
			DZFDouble dfje = tt.get(0).getDf() == null ? DZFDouble.ZERO_DBL : tt.get(0).getDf();
			if ("dr".equals(dc)) {// 净发生借方 借方-贷方
				ccrmny = jfje.sub(dfje);
			} else {// 净发生贷方 贷方 -借方
				ccrmny = dfje.sub(jfje);
			}

			if (ccrmny == null) {
				ccrmny = DZFDouble.ZERO_DBL;
			}

			return ccrmny;
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}
			throw new WiseRunException(e);
		}
	}

	/****
	 * 获取累计发生额
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getGlCumulamtoCcr(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,YntCpaccountVO[] accountVO)
			throws DZFWarpException {

		try {
			DZFDouble ccrmny = null;
			String condition = " and (h.period>='" + period.substring(0, 4) + "-01'  and h.period<= '" + period
					+ "'  )";// 期间
			List<KmZzVO> tt = getKmZzVOByCondition(ishasjz, subjcode, pk_corp, condition, null, null, accountVO);
			if ("dr".equals(dc)) {// 借方
				ccrmny = tt.get(0).getJf();
			} else {
				ccrmny = tt.get(0).getDf();
			}

			if (ccrmny == null) {
				ccrmny = new DZFDouble("0.0");
			}

			return ccrmny;
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}
			throw new WiseRunException(e);
		}

	}

	/**
	 * 通过条件获取发生额
	 */

	private List<KmZzVO> getKmZzVOByCondition(DZFBoolean ishasjz, String subjcode, String pk_corp, String condition,
			String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) {

		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(b.jfmny) as jf ,sum(b.dfmny) as df ");// List<KmZzVO>
		sb.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h inner join  ");
		sb.append(" ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account where nvl(b.dr,0)=0 ");
		// 增加发票类型 1普票 2专票 3未开票
		if (StringUtil.isEmptyWithTrim(invoiceflag) == false) {
			CorpVO corpVO = corpService.queryByPk(pk_corp);
			String corpNature = corpVO.getChargedeptname();
			String defaultInvoice = "一般纳税人".equals(corpNature) ? Integer.toString(IFpStyleEnum.SPECINVOICE.getValue()) : Integer.toString(IFpStyleEnum.COMMINVOICE.getValue()) ;
			sb.append(" and nvl(h.fp_style, ").append(defaultInvoice).append(")=").append(invoiceflag);
		}
		sb.append(" and b.pk_currency ='" + getCNYCurr() + "' ");// 币种
		sb.append(" and b.pk_corp = '" + pk_corp + "' ");
		sb.append(condition);
		// 不包含未记账，即只查询已记账的 Y是否包含未记账
		if (!ishasjz.booleanValue()) {
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		sb.append(" and b.pk_accsubj in " + getSubjSQL(subjcode, pk_corp, taxrate, accountVO));
		SQLParameter parameter = new SQLParameter();
		List<KmZzVO> tt = (List<KmZzVO>) getSingleObjectBO().executeQuery(sb.toString(), parameter,
				new BeanListProcessor(KmZzVO.class));

		return tt;

	}

	/**
	 * 获取科目条件
	 */

	private String getSubjSQL(String subjcode, String pk_corp, DZFDouble taxrate,YntCpaccountVO[] accountVO) throws DZFWarpException {
		YntCpaccountVO accountvo = getpkSubj(subjcode, pk_corp,accountVO);
		DZFBoolean isleaf = accountvo.getIsleaf();

		if (isleaf.booleanValue()) {
			if (taxrate == null || accountvo.getShuilv() != null && taxrate.div(100).equals(accountvo.getShuilv())) {
				return "('" + accountvo.getPrimaryKey() + "')";
			} else {
				throw new BusinessException(glmessage + "科目编码" + subjcode + "不存在，请检查!");
			}
		} else {

			return " ( select pk_corp_account From ynt_cpaccount where pk_corp='" + pk_corp + "' and accountcode like '"
					+ subjcode + "%' and nvl(dr,0)=0 "
					+ (taxrate == null ? "" : " and shuilv =" + String.valueOf(taxrate.div(100))) + ") ";
		}
	}

	/****
	 * 获取人民币币种PK
	 * 
	 * @throws BusinessException
	 */

	private String getCNYCurr() {
		if (pk_curr == null) {
			SQLParameter params = new SQLParameter();
			BdCurrencyVO[] currvo = (BdCurrencyVO[]) getSingleObjectBO().queryByCondition(BdCurrencyVO.class,
					"currencycode='CNY'", params);
			pk_curr = currvo[0].getPrimaryKey();
		}
		return pk_curr;
	}

	/****
	 * 获取科目VO
	 * 
	 * @throws BusinessException
	 */
	private YntCpaccountVO getpkSubj(String subjcode, String pk_corp,YntCpaccountVO[] accountVO) throws DZFWarpException {

		for (int i = 0; i < accountVO.length; i++) {
			if (accountVO[i].getAccountcode().equals(subjcode)) {
				return accountVO[i];
			}
		}
		throw new BusinessException(glmessage + "科目编码" + subjcode + "不存在，请检查!");
	}

	private DZFDouble executeSqlAsDzfDouble(String sql, SQLParameter sp) throws DZFWarpException {
		Object[] values = (Object[]) getSingleObjectBO().executeQuery(sql, sp, new ArrayProcessor());
		if ((values == null) || (values.length == 0))
			return DZFDouble.ZERO_DBL;

		return values[0] == null ? DZFDouble.ZERO_DBL : new DZFDouble(values[0].toString());
	}

	private SingleObjectBO getSingleObjectBO() {
		if (singleObjectBO == null) {
			singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return singleObjectBO;
	}

	/**
	 * 代表取营业收入（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getRevenue(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {

		return getLRB_Month(pk_corp, period, ishasjz, "一、营业收入");
	}

	/**
	 * 代表取季度营业收入（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getRevenue2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Quarter(pk_corp, period, ishasjz, "一、营业收入");

	}

	public DZFDouble getRevenue3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Cumulative(pk_corp, period, ishasjz, "一、营业收入|一、主营业务收入");
	}

	/**
	 * 代表取营业成本（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCosts(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Month(pk_corp, period, ishasjz, "减：营业成本");
	}

	/**
	 * 代表取季度营业成本（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCosts2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Quarter(pk_corp, period, ishasjz, "减：营业成本");
	}

	public DZFDouble getCosts3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Cumulative(pk_corp, period, ishasjz, "减：营业成本|减：主营业务成本");
	}

	/**
	 * 代表取利润总额（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getProfitBeforeTax(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Month(pk_corp, period, ishasjz, "三、利润总额（亏损总额以“-”号填列）");
	}

	/**
	 * 代表取季度利润总额（当前公司、当前年、当前期间、是包含未记账）
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getProfitBeforeTax2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		DZFDouble d1 = getLRB_Quarter(pk_corp, period, ishasjz, "三、利润总额（亏损总额以“-”号填列）"); // 2007
		return d1.add(getLRB_Quarter(pk_corp, period, ishasjz, "三、利润总额（亏损总额以“-”填列）")); // 2013
	}

	public DZFDouble getProfitBeforeTax3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException {
		return getLRB_Cumulative(pk_corp, period, ishasjz, "三、利润总额（亏损总额以“-”号填列）|四、利润总额（亏损以“-”号填列）");
	}

	/**
	 * 按项目取取利润表月报金额
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @param xmmc
	 * @return
	 */
	private DZFDouble getLRB_Month(String pk_corp, String period, DZFBoolean ishasjz, String xmmc) {
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean(!ishasjz.booleanValue()));// 此处含义是仅包含记账，跟客户端含义相反
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		
		try {
			LrbVO[] vos = zxkjReportService.getLRBVOs(paramVO);
			if (vos != null && vos.length > 0) {
					for (LrbVO vo : vos) {
						if (xmmc.equals(vo.getXm())) {
							return (vo.getByje() == null ? DZFDouble.ZERO_DBL : vo.getByje());
						}
					}
			}
		} catch (Exception e) {//异常日志打印
			log.error(e.getMessage(), e);
		}
		
		return DZFDouble.ZERO_DBL;
	}

	/**
	 * 按项目取利润表季报金额
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @param xmmc
	 * @return
	 */
	private DZFDouble getLRB_Quarter(String pk_corp, String period, DZFBoolean ishasjz, String xmmc) {
		int iMonth = Integer.parseInt(period.substring(5, 7));
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean(!ishasjz.booleanValue())); // 此处含义是仅包含记账，跟客户端含义相反
		// 按季度查询是，开始日期也传递季度末月份的首日期
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		
		try {
			LrbquarterlyVO[] vos = zxkjReportService.getLRBquarterlyVOs(paramVO);
			if (vos != null && vos.length > 0) {
				for (LrbquarterlyVO vo : vos) {
					if (xmmc.equals(vo.getXm())) {
						switch (iMonth) {
						case 3:
							return (vo.getQuarterFirst() == null ? DZFDouble.ZERO_DBL : vo.getQuarterFirst());
						case 6:
							return (vo.getQuarterSecond() == null ? DZFDouble.ZERO_DBL : vo.getQuarterSecond());
						case 9:
							return (vo.getQuarterThird() == null ? DZFDouble.ZERO_DBL : vo.getQuarterThird());
						case 12:
							return (vo.getQuarterFourth() == null ? DZFDouble.ZERO_DBL : vo.getQuarterFourth());
						default:
							return DZFDouble.ZERO_DBL;
						}
					}
				}
			}
		} catch (Exception e) {//异常日志打印
			log.error(e.getMessage(), e);
		}
		
		return DZFDouble.ZERO_DBL;
	}

	/**
	 * 按项目取取利润表年初到现在的金额
	 * 
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @param xmmc
	 * @return
	 */
	private DZFDouble getLRB_Cumulative(String pk_corp, String period, DZFBoolean ishasjz, String xmmc) {
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean(!ishasjz.booleanValue()));// 此处含义是仅包含记账，跟客户端含义相反
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		
		try {
			LrbVO[] vos = zxkjReportService.getLRBVOs(paramVO);
			if (vos != null && vos.length > 0) {
					for (LrbVO vo : vos) {
//						if (xmmc.equals(vo.getXm())) {
						if (xmmc.contains(vo.getXm())) {
							return (vo.getBnljje() == null ? DZFDouble.ZERO_DBL : vo.getBnljje());
						}
					}
			}
		} catch (Exception e) {//异常日志打印
			log.error(e.getMessage(), e);
		}
		
		return DZFDouble.ZERO_DBL;
	}

	/**
	 * 是否设置带税率属性的科目（当前、当前）
	 * 
	 * @param subjcode
	 *            科目
	 * @param pk_corp
	 *            公司
	 * @return 返回值=0为未设置，返回值=1为已设置
	 * @throws DZFWarpException
	 */
	public Integer getTax(String subjcode, String pk_corp) throws DZFWarpException {
		String sql = " select * from ynt_cpaccount where pk_corp=? and accountcode like '" + subjcode
				+ "%' and nvl(dr,0)=0  and nvl(shuilv,0)<>0";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_corp);
		List<YntCpaccountVO> tt = (List<YntCpaccountVO>) getSingleObjectBO().executeQuery(sql, parameter,
				new BeanListProcessor(YntCpaccountVO.class));
		return (tt != null && tt.size() > 0 ? 1 : 0);
	}

	/**
	 * 清单取数
	 * 
	 * @param pk_corp
	 * @return 返回值=0为销项进项清单取数 返回值=1为总账取数
	 * @throws DZFWarpException
	 */
	@Override
	public Integer getDatasources(String pk_corp) throws DZFWarpException {
		YntParameterSet paravo = paramterService.queryParamterbyCode(pk_corp, IParameterConstants.DZF004);

		if (paravo == null || paravo.getPardetailvalue() == null)
			return IParameterConstants.FROMSALT;

		return paravo.getPardetailvalue();
	}

	/**
	 * 取进项清单金额 (专票、本年、本期间、17税率、发票认证) 取子表税率
	 * 
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @param ivflag
	 * @return
	 * @throws DZFWarpException
	 */
//	@Override
//	public DZFDouble getQDInAmt(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate, String ivflag)
//			throws DZFWarpException {
//
//		return getQDInCommon(pk_corp, period, tickFlag, taxRate, ivflag, "sum(bhjje) as sum");
//	}

	/**
	 * 取进项清单税额 (专票、本年、本期间、17税率、发票认证)
	 * 
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @param ivflag
	 * @return
	 * @throws DZFWarpException
	 */
//	@Override
//	public DZFDouble getQDInTxm(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate, String ivflag)
//			throws DZFWarpException {
//		return getQDInCommon(pk_corp, period, tickFlag, taxRate, ivflag, "sum(bspse) as sum");
//	}
//
//	public static String PU = "1";// 普票
//	public static String ZHUAN = "2";// 专票
//	public static String NOTICK = "3";// 未开票
//	public static String IV = "IV";// 认证通过
//	public static String NIV = "NIV";// 没有发票认证
//
//	private DZFDouble getQDInCommon(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate, String ivflag,
//			String whereSql) throws DZFWarpException {
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("  Select ");
//		sb.append(whereSql);
//		sb.append("    From ynt_vatincominvoice y ");
//		sb.append("    left join ynt_vatincominvoice_b b on y.pk_vatincominvoice = b.pk_vatincominvoice ");
//		sb.append("   Where nvl(y.dr, 0) = 0 ");
//		sb.append("     and nvl(b.dr, 0) = 0 ");
//		sb.append("     and y.pk_corp = ? ");
//		sb.append("     and y.period = ? ");
//		if (tickFlag != null) {
//			if (ZHUAN.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.TRUE);
//			} else if (PU.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.FALSE);
//			} else {
//				sb.append("     and 1 <> 1 ");
//			}
//		}
//		if (!StringUtil.isEmpty(ivflag)) {
//			sb.append("     and y.rzjg = ? ");
//			sp.addParam(IV.equals(ivflag.toUpperCase()) ? IBillManageConstants.RSPASS : IBillManageConstants.RSNOPASS);
//		}
//		if (taxRate != null && taxRate.length > 0) {
//			sb.append("     and b.bspsl in ( ");
//			for (int i = 0; i < taxRate.length; i++) {
//				sb.append(taxRate[i]);
//
//				if (i != taxRate.length - 1) {
//					sb.append(", ");
//				}
//			}
//			sb.append(" ) ");
//		}
//
//		Object[] result = (Object[]) getSingleObjectBO().executeQuery(sb.toString(), sp, new ArrayProcessor());
//
//		if (result == null || result.length == 0)
//			return DZFDouble.ZERO_DBL;
//
//		return result[0] == null ? DZFDouble.ZERO_DBL : new DZFDouble(result[0].toString());
//	}


	@Override
	public DZFDouble getQDInc(String pk_corp, String period, Integer tickFlag, String[] taxRate, String mnytype,
			String bstype, String ivflag, Integer periodtype, Map<String, List<DZFDouble>> mnyoutmap)
			throws DZFWarpException {
//		DZFDouble dzfReturn = DZFDouble.ZERO_DBL;
//		String key =  "jx," + pk_corp + "," + period
//				+ "," + tickFlag + "," + bstype + "," + ivflag;
//		if(taxRate != null && taxRate.length > 0){
//			for(String tax : taxRate){
//				key = key + "," + tax;
//			}
//		}
//
//		List<DZFDouble> mnylist = null;
//		if(mnyoutmap != null && mnyoutmap.containsKey(key)){
//			mnylist = mnyoutmap.get(key);
//		}
//
//		if(mnylist == null || mnylist.size() == 0){
//			List<DZFDouble> temp;
//			Integer bs = StringUtil.isEmpty(bstype)? null : Integer.parseInt(bstype);//标识
//			Integer rzbs = "iv".equals(ivflag) ?
//					Integer.valueOf(0) : "niv".equals(ivflag) ? Integer.valueOf(1) : null;
//			try {
//				if(periodtype == PeriodType.jidureport){
//					mnylist = new ArrayList<DZFDouble>();
//					mnylist.add(DZFDouble.ZERO_DBL);
//					mnylist.add(DZFDouble.ZERO_DBL);
//					temp = zncsserv.queryVATIncomeInvoiceMny(pk_corp, period, tickFlag, taxRate, bs, rzbs);
//					caculateMny(mnylist, temp);
//					String prevPeriod = DateUtils.getPreviousPeriod(period);
//					temp = zncsserv.queryVATIncomeInvoiceMny(pk_corp, prevPeriod, tickFlag, taxRate, bs, rzbs);
//					caculateMny(mnylist, temp);
//					prevPeriod = DateUtils.getPreviousPeriod(prevPeriod);
//					temp = zncsserv.queryVATIncomeInvoiceMny(pk_corp, prevPeriod, tickFlag, taxRate, bs, rzbs);
//					caculateMny(mnylist, temp);
//				}else{
//					mnylist = zncsserv.queryVATIncomeInvoiceMny(pk_corp, period, tickFlag, taxRate, bs, rzbs);
//				}
//			} catch (Exception e) {
//				log.error("错误",e);
//			}
//
//			mnyoutmap.put(key, mnylist);
//		}
//
//		if(mnylist != null && mnylist.size() == 2
//				&& !StringUtil.isEmpty(mnytype)){
//			if("金额".equals(mnytype)){
//				dzfReturn = SafeCompute.add(mnylist.get(0), dzfReturn);
//			}
//			if("税额".equals(mnytype)){
//				dzfReturn = SafeCompute.add(mnylist.get(1), dzfReturn);
//			}
//		}
//
//		return dzfReturn;
		return null;
	}
	
	/**
	 * 取销项清单货物、劳务金额 税额(专普票、本年、本期间、17税率、金额/税额、货物/货物) 取子表税率
	 * 
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public DZFDouble getQDOut(String pk_corp, String period, Integer tickFlag,
				String[] taxRate, String mnytype, String busitype, Integer periodtype, Map<String, List<DZFDouble>> mnyoutmap) throws DZFWarpException {
//		DZFDouble dzfReturn = DZFDouble.ZERO_DBL;
//		String key =  "xx," + pk_corp + "," + period + "," + tickFlag;
//		if(taxRate != null && taxRate.length > 0){
//			for(String tax : taxRate){
//				key = key + "," + tax;
//			}
//		}
//
//		List<DZFDouble> mnylist = null;
//		if(mnyoutmap != null && mnyoutmap.containsKey(key)){
//			mnylist = mnyoutmap.get(key);
//		}
//
//		if(mnylist == null || mnylist.size() == 0){
//			List<DZFDouble> temp;
//			try {
//				if(periodtype == PeriodType.jidureport){
//					mnylist = new ArrayList<DZFDouble>();
//					mnylist.add(DZFDouble.ZERO_DBL);
//					mnylist.add(DZFDouble.ZERO_DBL);
//					mnylist.add(DZFDouble.ZERO_DBL);
//					mnylist.add(DZFDouble.ZERO_DBL);
//					temp = zncsserv.queryVATSaleInvoiceMny(pk_corp, period, tickFlag, taxRate);
//					caculateMny(mnylist, temp);
//					String prevPeriod = DateUtils.getPreviousPeriod(period);
//					temp = zncsserv.queryVATSaleInvoiceMny(pk_corp, prevPeriod, tickFlag, taxRate);
//					caculateMny(mnylist, temp);
//					prevPeriod = DateUtils.getPreviousPeriod(prevPeriod);
//					temp = zncsserv.queryVATSaleInvoiceMny(pk_corp, prevPeriod, tickFlag, taxRate);
//					caculateMny(mnylist, temp);
//				}else{
//					mnylist = zncsserv.queryVATSaleInvoiceMny(pk_corp, period, tickFlag, taxRate);
//				}
//			} catch (Exception e) {
//				log.error("错误",e);
//			}
//
//			mnyoutmap.put(key, mnylist);
//		}
//
//		if(mnylist != null && mnylist.size() == 4
//				&& !StringUtil.isEmpty(mnytype) && !StringUtil.isEmpty(busitype)){
//			if("金额".equals(mnytype) && busitype.contains("服务")){
//				dzfReturn = SafeCompute.add(mnylist.get(0), dzfReturn);
//			}
//
//			if("金额".equals(mnytype) && busitype.contains("货物")){
//				dzfReturn = SafeCompute.add(mnylist.get(2), dzfReturn);
//			}
//
//			if("税额".equals(mnytype) && busitype.contains("服务")){
//				dzfReturn = SafeCompute.add(mnylist.get(1), dzfReturn);
//			}
//
//			if("税额".equals(mnytype) && busitype.contains("货物")){
//				dzfReturn = SafeCompute.add(mnylist.get(3), dzfReturn);;
//			}
//		}
//
//		return dzfReturn;
		return null;
	}
	
	private void caculateMny(List<DZFDouble> list, List<DZFDouble> temp){
		if(temp != null && temp.size() == 4){
			for(int i = 0; i < list.size(); i++){
				list.set(i, SafeCompute.add(list.get(i), temp.get(i)));
			}
		}
	}

	/**
	 * 取销项清单税额 (专票、本年、本期间、17税率)
	 * 
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @return
	 * @throws DZFWarpException
	 */
//	@Override
//	public DZFDouble getQDOutTxm(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate)
//			throws DZFWarpException {
//		return getQDOutCommon(pk_corp, period, tickFlag, taxRate, "sum(bspse) as sum");
//	}
//
//	private DZFDouble getQDOutCommon(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate,
//			String whereSql) throws DZFWarpException {
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(period);
//		sp.addParam(pk_corp);
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("  Select ");
//		sb.append(whereSql);
//		sb.append("    From ynt_vatsaleinvoice y ");
//		sb.append("   left join ynt_vatsaleinvoice_b b on y.pk_vatsaleinvoice = b.pk_vatsaleinvoice ");
//		sb.append("   Where nvl(y.dr, 0) = 0 ");
//		sb.append("     and nvl(b.dr, 0) = 0 ");
//		sb.append("     and y.period = ? ");
//		sb.append("     and y.pk_corp = ? ");
//		if (tickFlag != null) {
//
//			if (ZHUAN.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.TRUE);
//			} else if (PU.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.FALSE);
//			} else {
//				sb.append("     and 1 <> 1 ");
//			}
//		}
//		if (taxRate != null && taxRate.length > 0) {
//			sb.append("     and b.bspsl in ( ");
//			for (int i = 0; i < taxRate.length; i++) {
//				sb.append(taxRate[i]);
//
//				if (i != taxRate.length - 1) {
//					sb.append(", ");
//				}
//			}
//			sb.append(" ) ");
//		}
//
//		Object[] result = (Object[]) getSingleObjectBO().executeQuery(sb.toString(), sp, new ArrayProcessor());
//
//		if (result == null || result.length == 0)
//			return DZFDouble.ZERO_DBL;
//
//		return result[0] == null ? DZFDouble.ZERO_DBL : new DZFDouble(result[0].toString());
//	}

	/**
	 * 取认证发票的数量
	 */
	@Override
	public DZFDouble getIVnumber(String pk_corp, String period, Integer tickFlag, 
			String bstype, String ivflag, Integer periodtype) throws DZFWarpException {
		
//
		return null;
	}

	/****
	 * 取除此科目分录行本期发生 invoiceflag 1/2/3 普票/专票/未开票 空：不处理改字段 taxrate 税率 ， 空代表全部税率
	 * 
	 * @throws BusinessException
	 */
	@Override
	public DZFDouble getThsmtAmt(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz,
			String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) throws DZFWarpException {
		try {
			DZFDouble fsje = getExceptKmVOByCondition(subjcode, period, dc, pk_corp, ishasjz, invoiceflag, taxrate, accountVO);
			return fsje;
		} catch (DZFWarpException e) {

			if (e.getMessage().lastIndexOf(glmessage) != -1) {
				return DZFDouble.ZERO_DBL;
			}
			throw new WiseRunException(e);
		}

	}

	private DZFDouble getExceptKmVOByCondition(String subjcode, String period, String dc, String pk_corp,
			DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate,YntCpaccountVO[] accountVO) {

		SQLParameter sp = new SQLParameter();
		// sp.addParam(yntBoPubUtil.getCNYPk());

		String mnyField = "dr".equals(dc) ? "jfmny" : "dfmny";
		String whereSQL = getExceptKmWhereSql(subjcode, pk_corp, taxrate,accountVO);
		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(b2.jfmny) as jf, sum(b2.dfmny) as df ");
		sb.append("   from (select * ");
		sb.append("           from ynt_tzpz_b b ");
		sb.append("          Where b.pk_tzpz_h in (select distinct pk_tzpz_h ");
		sb.append("            from ynt_tzpz_b b1 ");
		sb.append(
				"              Where nvl(b1.dr,0) = 0 and b1.pk_accsubj in " + whereSQL + " and b1." + mnyField + " <> 0)) b2 ");
		sb.append("  inner join ynt_tzpz_h h ");
		sb.append("     on b2.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append("  Where b2.pk_accsubj not in " + whereSQL + " ");
		sb.append("    and nvl(b2.dr, 0) = 0 ");
		sb.append("    and nvl(h.dr,0) = 0 ");
		sb.append(" and h.pk_corp = ? ");
		sb.append(" and h.period = ? ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		// sb.append(" and h.pk_currency = ? ");
		if (!StringUtil.isEmpty(invoiceflag)) {
			CorpVO corpVO = corpService.queryByPk(pk_corp);
			String corpNature = corpVO.getChargedeptname();
			String defaultInvoice = "一般纳税人".equals(corpNature) ? Integer.toString(IFpStyleEnum.SPECINVOICE.getValue()) : Integer.toString(IFpStyleEnum.COMMINVOICE.getValue()) ;
			sb.append(" and nvl(h.fp_style, ").append(defaultInvoice).append(")= ? ");
			sp.addParam(invoiceflag);
		}

		// 不包含未记账，即只查询已记账的 Y是否包含未记账
		if (!ishasjz.booleanValue()) {
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		// if(ishasjz != null && ishasjz.booleanValue()){
		// sb.append(" and h.ishasjz = ? ");
		// sb.append(" and h.vbillstatus= 1 ");
		// sp.addParam(DZFBoolean.TRUE.toString());
		// }else if(ishasjz != null){
		// sb.append(" and h.ishasjz != ? ");
		// sp.addParam(DZFBoolean.TRUE.toString());
		// }

		List<KmZzVO> tt = (List<KmZzVO>) getSingleObjectBO().executeQuery(sb.toString(), sp,
				new BeanListProcessor(KmZzVO.class));

		DZFDouble ccrmny = null;
		if ("dr".equals(dc)) {// 借方
			ccrmny = tt.get(0).getJf();
		} else {
			ccrmny = tt.get(0).getDf();
		}

		if (ccrmny == null) {
			ccrmny = new DZFDouble("0.0");
		}

		return ccrmny;
	}

	private String getExceptKmWhereSql(String subjcode, String pk_corp, DZFDouble taxrate,YntCpaccountVO[] accountVO) {
		YntCpaccountVO accountvo = getpkSubj(subjcode, pk_corp,accountVO);
		DZFBoolean isleaf = accountvo.getIsleaf();

		if (isleaf.booleanValue()) {
			if (taxrate == null || accountvo.getShuilv() != null && taxrate.div(100).equals(accountvo.getShuilv())) {
				return "('" + accountvo.getPrimaryKey() + "')";
			} else {
				throw new BusinessException(glmessage + "科目编码" + subjcode + "不存在，请检查!");
			}
		} else {
			String where = " select pk_corp_account From ynt_cpaccount where pk_corp='" + pk_corp
					+ "' and accountcode like '" + subjcode + "%' and nvl(dr,0)=0 "
					+ (taxrate == null ? "" : " and shuilv =" + String.valueOf(taxrate.div(100)));

			List<YntCpaccountVO> objs = (List<YntCpaccountVO>) getSingleObjectBO().executeQuery(where, null,
					new BeanListProcessor(YntCpaccountVO.class));

			if (objs == null || objs.size() == 0)
				return "( '' )";
			StringBuffer sb = new StringBuffer();
			sb.append(" ( ");
			for (int i = 0; i < objs.size(); i++) {
				sb.append("'").append(objs.get(i).getPk_corp_account()).append("'");
				if (i != objs.size() - 1)
					sb.append(", ");
			}
			sb.append(" ) ");

			return sb.toString();
		}
	}

//	@Override
//	public DZFDouble getQDOutAmt2(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate)
//			throws DZFWarpException {
//		String prevPeriod = DateUtils.getPreviousPeriod(period);
//		prevPeriod = DateUtils.getPreviousPeriod(prevPeriod);
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(prevPeriod);
//		sp.addParam(period);
//		sp.addParam(pk_corp);
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("  Select ");
//		sb.append("  sum(bhjje) as sum ");
//		sb.append("    From ynt_vatsaleinvoice y ");
//		sb.append("   left join ynt_vatsaleinvoice_b b on y.pk_vatsaleinvoice = b.pk_vatsaleinvoice ");
//		sb.append("   Where nvl(y.dr, 0) = 0 ");
//		sb.append("     and nvl(b.dr, 0) = 0 ");
//		sb.append("     and y.period >= ? ");
//		sb.append("     and y.period <= ? ");
//		sb.append("     and y.pk_corp = ? ");
//		if (tickFlag != null) {
//
//			if (ZHUAN.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.TRUE);
//			} else if (PU.equals(tickFlag + "")) {
//				sb.append("     and nvl(y.iszhuan,'Y') = ? ");
//				sp.addParam(DZFBoolean.FALSE);
//			} else {
//				sb.append("     and 1 <> 1 ");
//			}
//		}
//		if (taxRate != null && taxRate.length > 0) {
//			sb.append("     and b.bspsl in ( ");
//			for (int i = 0; i < taxRate.length; i++) {
//				sb.append(taxRate[i]);
//
//				if (i != taxRate.length - 1) {
//					sb.append(", ");
//				}
//			}
//			sb.append(" ) ");
//		}
//
//		Object[] result = (Object[]) getSingleObjectBO().executeQuery(sb.toString(), sp, new ArrayProcessor());
//
//		if (result == null || result.length == 0)
//			return DZFDouble.ZERO_DBL;
//
//		return result[0] == null ? DZFDouble.ZERO_DBL : new DZFDouble(result[0].toString());
//	}

	/**
	 * 取表格某行某列的数
	 * 
	 * @param corpType
	 *            公司性质
	 * @param reportName
	 *            报表名称
	 * @param coordinate
	 *            坐标
	 * @param pk_corp
	 *            公司pk
	 * @param period
	 *            期间
	 * @return
	 * @throws DZFWarpException
	 */
	public static String SMALL_TAX = "0";
	public static String GENERAL_TAX = "1";

	@Override
	public DZFDouble getQsbbqs(String corpType, String reportName, String coordinate, String pk_corp, String period)
			throws DZFWarpException {
		DZFDouble dReturn = null;
		String sb_zlbh = null;
		if (SMALL_TAX.equals(corpType)) {

			sb_zlbh = TaxRptConst.SB_ZLBH10102;// 小规模纳税人增值税
		} else if (GENERAL_TAX.equals(corpType)) {

			sb_zlbh = TaxRptConst.SB_ZLBH10101;// 一般纳税人增值税
		} else{
			return new DZFDouble("0.0");
		}

		String sql = "Select * From ynt_taxreport y Where pk_corp = ? and nvl(dr,0) = 0 and y.sb_zlbh like ? and y.period = ? ";
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(sb_zlbh + "%");
		sp.addParam(period);
		
		List<TaxReportVO> vos = (List<TaxReportVO>) getSingleObjectBO().executeQuery(sql, sp,
				new BeanListProcessor(TaxReportVO.class));
		
		if(vos == null || vos.size() != 1){//0 或者1个直接返回零
			return new DZFDouble("0.0");
		}
		
		TaxReportVO reportvo = vos.get(0);
		
		String spreadfile = reportvo.getSpreadfile();
		
		if(StringUtil.isEmpty(reportvo.getPrimaryKey())){
			return new DZFDouble("0.0");
		}else if(StringUtil.isEmpty(spreadfile)){
			sp = new SQLParameter();
			sp.addParam(reportvo.getPrimaryKey());
			sp.addParam(reportName);
			TaxReportDetailVO[] vodetails = (TaxReportDetailVO[]) getSingleObjectBO().queryByCondition(
					TaxReportDetailVO.class, "nvl(dr,0)=0 and pk_taxreport=? and reportname=?", sp);
			
			if (vodetails != null && vodetails.length > 0 && !StringUtil.isEmpty(vodetails[0].getSpreadfile())) {
				spreadfile = vodetails[0].getSpreadfile();
			}
		}
		

		if (!StringUtil.isEmpty(spreadfile)) {
			String[] xy = getCoordinate(coordinate);
			dReturn = taxDeclarationService.getQsbbqsData(spreadfile, reportName, xy[0], xy[1]);
		}

		if (dReturn == null) {
			dReturn = new DZFDouble("0.0");
		}

		return dReturn;
	}

	private String[] getCoordinate(String coordinate) {
		String[] arr = new String[2];
		for (int i = 0; i < coordinate.length(); i++) {
			char c = Character.toUpperCase(coordinate.charAt(i));
			if (c < 'A') {
				arr[0] = fromNumSystem26(coordinate.substring(0, i)) - 1 + "";
				arr[1] = Integer.parseInt(coordinate.substring(i, coordinate.length())) - 1 + "";
				break;
			}
		}

		return arr;
	}

	/**
	 * 26进制转化10进制
	 * 
	 * @param str
	 * @return
	 */
	public static int fromNumSystem26(String str) {
		int n = 0;

		if (StringUtil.isEmptyWithTrim(str))
			return n;

		for (int i = str.length() - 1, j = 1; i >= 0; i--, j *= 26) {
			char c = Character.toUpperCase(str.charAt(i));

			if (c < 'A' || c > 'Z')
				return 0;

			n += ((int) c - 64) * j;
		}

		return n;
	}

	/**
	 * 取优惠政策 参数值=0 不享受； =1 享受 规则：小微企业0 高新技术企业1 软件企业2
	 * 
	 * @param yhzc
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public DZFDouble getYhzc(String yhzc, String pk_corp) throws DZFWarpException {
		boolean isyh = false;
		if (!StringUtil.isEmptyWithTrim(yhzc)) {
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
			String vyhzc = taxvo.getVyhzc();
			if (!StringUtil.isEmptyWithTrim(vyhzc)) {
				isyh = tranYhzc(yhzc, vyhzc);
			}
		}

		return isyh ? new DZFDouble(1) : new DZFDouble(0);
	}

	private static final String XWQY = "xwqy";// 代表小微企业优惠0
	private static final String GXJS = "gxjs";// 代表高新技术企业优惠1
	private static final String RJQY = "rjqy";// 代表软件企业优惠2

	private boolean tranYhzc(String fyhzc, String byhzc) {

		if (fyhzc.toLowerCase().equals(XWQY) && "0".equals(byhzc))
			return true;

		if (fyhzc.toLowerCase().equals(RJQY) && "2".equals(byhzc))
			return true;

		if (fyhzc.toLowerCase().equals(GXJS) && "1".equals(byhzc))
			return true;

		return false;
	}

	/**
	 * 资产负债表取数
	 * 
	 * @param projnames
	 * @param descColname
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public DZFDouble getZcfzb(String[] projnames, String descColname, String period, String pk_corp)
			throws DZFWarpException {
		if (StringUtil.isEmptyWithTrim(descColname))
			return new DZFDouble(0);

		String alis = null;
		if ("期末余额".equals(descColname)) {
			alis = "qmye1";
		} else if ("年初余额".equals(descColname))
			alis = "ncye1";
		else {
			return new DZFDouble(0);
		}

		ZcFzBVO[] kmmxvos = (ZcFzBVO[]) zxkjReportService.getZCFZBVOsConXmids(period, pk_corp, "N",
				new String[] { "N", "N", "N", "N","N" }, null);

		if (kmmxvos == null || kmmxvos.length == 0) {
			return new DZFDouble(0);
		}

		DZFDouble dReturn = new DZFDouble(0);
		Map<String, DZFDouble> mmap = constructMap(kmmxvos, alis);

		for (String proj : projnames) {
			dReturn = SafeCompute.add(dReturn, mmap.get(alis));
		}

		return dReturn;
	}

	private Map<String, DZFDouble> constructMap(ZcFzBVO[] kmmxvos, String alis) {
		Map<String, DZFDouble> mmap = new HashMap<String, DZFDouble>();
		ZcFzBVO newzvo = null;// 结果集中vo拆分
		for (ZcFzBVO bvo : kmmxvos) {
			newzvo = new ZcFzBVO();// 1资产
			newzvo.setZc(bvo.getZc());
			newzvo.setNcye1(bvo.getNcye1());
			newzvo.setQmye1(bvo.getQmye1());

			if (!StringUtil.isEmptyWithTrim(newzvo.getZc())) {
				mmap.put(newzvo.getZc().trim(), (DZFDouble) newzvo.getAttributeValue(alis));
			}
			newzvo = new ZcFzBVO();// 2负债
			newzvo.setZc(bvo.getFzhsyzqy());
			newzvo.setNcye1(bvo.getNcye2());
			newzvo.setQmye1(bvo.getQmye2());

			if (!StringUtil.isEmptyWithTrim(newzvo.getZc())) {
				mmap.put(newzvo.getZc().trim(), (DZFDouble) newzvo.getAttributeValue(alis));
			}
		}

		return mmap;
	}

	/**
	 * 取其他年度税务报表
	 * 
	 * @param reportName
	 * @param coordinate
	 * @param pk_corp
	 * @param year
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public DZFDouble getSwbb(String reportName, String coordinate, String pk_corp, String year)
			throws DZFWarpException {
		DZFDouble dReturn = null;
		DZFDate startDate = DateUtils.getPeriodStartDate(year + "-01");
		DZFDate endDate = DateUtils.getPeriodEndDate(year + "-12");
		reportName = reportName.toUpperCase();
		String sb_zlbh = "A";// 暂时不取数，前台配好再调整

		SQLParameter sp = new SQLParameter();
		sp.addParam(startDate);
		sp.addParam(endDate);
		sp.addParam(pk_corp);
		sp.addParam(sb_zlbh);
		TaxReportVO reportvo = (TaxReportVO) getSingleObjectBO().executeQuery(
				"Select * From ynt_taxreport y Where y.periodfrom = ? and y.periodto = ? and pk_corp = ? and nvl(dr,0) = 0 and y.sb_zlbh = ? ",
				sp, new BeanProcessor(TaxReportVO.class));

		if (reportvo == null || StringUtil.isEmptyWithTrim(reportvo.getPrimaryKey())) {
			return new DZFDouble("0.0");
		}

		sp = new SQLParameter();
		sp.addParam(reportvo.getPrimaryKey());
		sp.addParam(sb_zlbh);
		sp.addParam(reportName + "%");
		TaxReportDetailVO[] vodetails = (TaxReportDetailVO[]) getSingleObjectBO().queryByCondition(
				TaxReportDetailVO.class, "nvl(dr,0)=0 and pk_taxreport=? and sb_zlbh=? and reportname like ?", sp);

		if (vodetails != null && vodetails.length > 0 && !StringUtil.isEmpty(vodetails[0].getSpreadfile())) {
			String[] xy = getCoordinate(coordinate);
			dReturn = taxDeclarationService.getQsbbqsData(vodetails[0].getSpreadfile(), reportName, xy[0], xy[1]);
		}

		if (dReturn == null) {
			dReturn = new DZFDouble("0.0");
		}

		return dReturn;
	}

	@Override
	public DZFDouble getLrb(String subjname, String nmnycol, DZFBoolean ishasjz, String pk_corp, int iYear)
			throws DZFWarpException {
		QueryParamVO queryParamvo = new QueryParamVO();
		queryParamvo.setQjz(iYear + "-12");
		queryParamvo.setEnddate(new DZFDate(iYear + "-12-01"));
		queryParamvo.setBegindate1(new DZFDate(iYear + "-12-01"));
		queryParamvo.setPk_corp(pk_corp);
		queryParamvo.setQjq(iYear + "-01");
		queryParamvo.setIshasjz(ishasjz);
		LrbVO[] fsejyevos = zxkjReportService.getLRBVOs(queryParamvo);

		if (fsejyevos != null && fsejyevos.length > 0) {
			for (LrbVO lrb : fsejyevos) {
				if (StringUtil.isEmpty(lrb.getXm())) {
					continue;
				}
				if (lrb.getXm().contains(subjname)) {
					if (lrb.getBnljje() == null)
						return DZFDouble.ZERO_DBL;
					return lrb.getBnljje();
				}
			}
		}
		return DZFDouble.ZERO_DBL;
	}

	@Override
	public DZFDouble getJmbl(String yhzc, String pk_corp, int iYear) throws DZFWarpException {
		yhzc = yhzc.toLowerCase().trim();
		// CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
		CorpVO corpvo = (CorpVO) getSingleObjectBO().queryByPrimaryKey(CorpVO.class, pk_corp);
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		String vyhzc = taxvo.getVyhzc();
		DZFDouble result = new DZFDouble(0);
		if ((XWQY.equals(yhzc) && "0".equals(vyhzc)) || (GXJS.equals(yhzc) && "1".equals(vyhzc))) {

			result = new DZFDouble(1);
		} else if (RJQY.equals(yhzc) && "2".equals(vyhzc)) {

			DZFDate dcertificationtime = taxvo.getDrzsj();// 认证日期
			String vschlnd = taxvo.getVschlnd();// 首次获利年度

			if (dcertificationtime != null && !StringUtil.isEmptyWithTrim(vschlnd)) {
				int firstprofityear = Integer.parseInt(vschlnd);
				result = ((dcertificationtime.getYear() > iYear || iYear < firstprofityear) ? new DZFDouble(0)
						: (iYear - firstprofityear < 2 ? new DZFDouble(1)
								: (iYear - firstprofityear < 5 ? new DZFDouble(0.5) : new DZFDouble(0))));
			}

		}
		return result;
	}

	@Override
	public DZFDouble getSqldata(String reportname, String coordinate, String pk_corp, String period)
			throws DZFWarpException {
		DZFDouble dReturn = null;
		DZFDate startDate = DateUtils.getPeriodStartDate(period);// 2017-02-01
		DZFDate endDate = DateUtils.getPeriodEndDate(period);// 2017-02-28
		String sb_zlbh = "10101";

		SQLParameter sp = new SQLParameter();
		sp.addParam(startDate);
		sp.addParam(endDate);
		sp.addParam(pk_corp);
		sp.addParam(sb_zlbh);
		TaxReportVO reportvo = (TaxReportVO) getSingleObjectBO().executeQuery(
				"Select * From ynt_taxreport y Where y.periodfrom = ? and y.periodto = ? and pk_corp = ? and nvl(dr,0) = 0 and y.sb_zlbh = ? ",
				sp, new BeanProcessor(TaxReportVO.class));

		if (reportvo == null || StringUtil.isEmptyWithTrim(reportvo.getPrimaryKey())) {
			return new DZFDouble("0.0");
		}

		sp = new SQLParameter();
		sp.addParam(reportvo.getPrimaryKey());
		sp.addParam(sb_zlbh);
		sp.addParam(reportname);
		TaxReportDetailVO[] vodetails = (TaxReportDetailVO[]) getSingleObjectBO().queryByCondition(
				TaxReportDetailVO.class, "nvl(dr,0)=0 and pk_taxreport=? and sb_zlbh=? and reportname = ? ", sp);

		if (vodetails != null && vodetails.length > 0 && !StringUtil.isEmpty(vodetails[0].getSpreadfile())) {
			String[] xy = getCoordinate(coordinate);
			dReturn = taxDeclarationService.getQsbbqsData(vodetails[0].getSpreadfile(), reportname, xy[0], xy[1]);
		}

		if (dReturn == null) {
			dReturn = new DZFDouble("0.0");
		}

		return dReturn;
	}

	@Override
	public DZFDouble getAccountPlan(String pk_corp) throws DZFWarpException {
		DZFDouble result = new DZFDouble(-1);
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		if (corpVO != null && !StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
			BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) getSingleObjectBO()
					.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
			result = new DZFDouble(schemaVO.getAcccode());
		}
		return result;
	}

	@Override
	public Object getSwitch(String[] objs) throws DZFWarpException {
		Object result = null;
		if (objs != null && objs.length > 0) {
			for (int i = 1; i < objs.length - 1; i = i + 2) {
				if (SafeCompute.sub(new DZFDouble((String) objs[0]), new DZFDouble((String) objs[i])).intValue() == 0) {
					result = objs[i + 1];
					break;
				}
			}
		}

		return result;
	}

	@Override
	public Map<String, ZcFzBVO[]> getZcfzInfo(String pk_corp, String period) throws DZFWarpException {
		ZcFzBVO[] kmmxvos = null;
		try {
			kmmxvos = (ZcFzBVO[]) zxkjReportService.getZCFZBVOs(period, pk_corp, "N",
					new String[] { "N", "N", "N", "N" });
		} catch (Exception e) {
			log.error("错误",e);
		}
		Map<String, ZcFzBVO[]> map = new HashMap<String, ZcFzBVO[]>();
		String key = pk_corp + "," + period;
		map.put(key, kmmxvos);
		return map;
	}

	@Override
	public DZFDouble getHczcfzValue(ZcFzBVO[] zcfzvos, String hc, String qmye) throws DZFWarpException {
		if (zcfzvos == null || zcfzvos.length == 0)
			return null;
		DZFDouble z = null;
		for (ZcFzBVO zcfz : zcfzvos) {
			if (hc.equals(zcfz.getHc1())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye1();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye1();
					break;
				}
			} else if (hc.equals(zcfz.getHc2())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye2();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye2();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getBmzcfzValue(ZcFzBVO[] zcfzvos, String bm, String qmye) throws DZFWarpException {
		if (zcfzvos == null || zcfzvos.length == 0)
			return null;
		DZFDouble z = null;
		for (ZcFzBVO zcfz : zcfzvos) {
			if (bm.equalsIgnoreCase(zcfz.getHc1_id())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye1();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye1();
					break;
				}
			} else if (bm.equalsIgnoreCase(zcfz.getHc2_id())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye2();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye2();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getMczcfzValue(ZcFzBVO[] zcfzvos, String mc, String qmye) throws DZFWarpException {
		if (zcfzvos == null || zcfzvos.length == 0)
			return null;
		DZFDouble z = null;
		for (ZcFzBVO zcfz : zcfzvos) {
			if (mc.equals(zcfz.getZc())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye1();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye1();
					break;
				}
			} else if (mc.equals(zcfz.getFzhsyzqy())) {
				if ("qmye".equals(qmye)) {
					z = zcfz.getQmye2();
					break;
				} else if ("ncye".equals(qmye)) {
					z = zcfz.getNcye2();
					break;
				}
			}
		}
		return z;
	}


	@Override
	public Map<String, LrbVO[]> getLrbInfo(String pk_corp, String period) throws DZFWarpException {
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N"));//
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		LrbVO[] lrbvos = null;
		try {
			lrbvos = zxkjReportService.getLRBVOs(paramVO);
		} catch (Exception e) {// 此异常吃掉。因为有时候查询的期间不在范围内。
			log.error("错误",e);
		}
		Map<String, LrbVO[]> map = new HashMap<String, LrbVO[]>();
		String key = pk_corp + "," + period;
		map.put(key, lrbvos);
		return map;
	}

	@Override
	public DZFDouble getHclrbValue(LrbVO[] lrbvos, String hc, String qmye) throws DZFWarpException {
		if (lrbvos == null || lrbvos.length == 0)
			return null;
		DZFDouble z = null;
		for (LrbVO lrb : lrbvos) {
			if (hc.equals(lrb.getHs())) {
				if ("byje".equals(qmye)) {
					z = lrb.getByje();
					break;
				} else if ("bnljje".equals(qmye)) {
					z = lrb.getBnljje();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getBmlrbValue(LrbVO[] lrbvos, String bm, String qmye) throws DZFWarpException {
		if (lrbvos == null || lrbvos.length == 0)
			return null;
		DZFDouble z = null;
		for (LrbVO lrb : lrbvos) {
			if (bm.equalsIgnoreCase(lrb.getHs_id())) {
				if ("byje".equals(qmye)) {
					z = lrb.getByje();
					break;
				} else if ("ljje".equals(qmye)
						|| "snje".equals(qmye)) {
					z = lrb.getBnljje();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getMclrbValue(LrbVO[] lrbvos, String mc, String qmye) throws DZFWarpException {
		if (lrbvos == null || lrbvos.length == 0)
			return null;
		DZFDouble z = null;
		for (LrbVO lrb : lrbvos) {
			if (mc.equals(lrb.getXm())) {
				if ("byje".equals(qmye)) {
					z = lrb.getByje();
					break;
				} else if ("ljje".equals(qmye)
						|| "snje".equals(qmye)) {
					z = lrb.getBnljje();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public Map<String, LrbVO[]> getLrbQuarter(String pk_corp, String period, String lastperiod)
			throws DZFWarpException {
		// 本期
		Map<String, LrbVO[]> map = new HashMap<String, LrbVO[]>();
		addLrbQuarter(pk_corp, period, map);
		// 上期
		addLrbQuarter(pk_corp, lastperiod, map);
		return map;
	}

	private void addLrbQuarter(String pk_corp, String period, Map<String, LrbVO[]> map) throws DZFWarpException {
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N"));//
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		try {
			LrbVO[] lrbvos = zxkjReportService.getLRBVOs(paramVO);
			String key = pk_corp + "," + period;
			map.put(key, lrbvos);
		} catch (Exception e) {// 此异常吃掉。因为有时候查询的期间不在范围内。
			log.error("错误",e);
		}
	}

	@Override
	public DZFDouble getHcLrbQuarterValue(LrbVO[] nowlrbvos, LrbVO[] lastlrbvos, String hc, String qmye)
			throws DZFWarpException {
		DZFDouble z = null;
		if ("bqje".equals(qmye)) {
			if (nowlrbvos == null || nowlrbvos.length == 0)
				return z;
			for (int i = 0; i < nowlrbvos.length; i++) {
				if (hc.equals(nowlrbvos[i].getHs())) {
					z = nowlrbvos[i].getBnljje();
					break;
				}
			}
		} else if ("sqje".equals(qmye)) {
			if (lastlrbvos == null || lastlrbvos.length == 0)
				return z;
			for (int i = 0; i < lastlrbvos.length; i++) {
				if (hc.equals(lastlrbvos[i].getHs())) {
					z = lastlrbvos[i].getBnljje();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getBmLrbQuarterValue(LrbVO[] nowlrbvos, LrbVO[] lastlrbvos, String bm, String qmye)
			throws DZFWarpException {
		DZFDouble z = null;
		if ("bqje".equals(qmye)) {
			if (nowlrbvos == null || nowlrbvos.length == 0)
				return z;
			for (int i = 0; i < nowlrbvos.length; i++) {
				if (bm.equalsIgnoreCase(nowlrbvos[i].getHs_id())) {
					z = nowlrbvos[i].getBnljje();
					break;
				}
			}
		} else if ("sqje".equals(qmye)) {
			if (lastlrbvos == null || lastlrbvos.length == 0)
				return z;
			for (int i = 0; i < lastlrbvos.length; i++) {
				if (bm.equalsIgnoreCase(lastlrbvos[i].getHs_id())) {
					z = lastlrbvos[i].getBnljje();
					break;
				}
			}
		}
		return z;
	}

	@Override
	public Map<String, LrbquarterlyVO[]> getLrbjidu(String pk_corp, String period) throws DZFWarpException {
		// int iMonth = Integer.parseInt(period.substring(5, 7));
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
		// 按季度查询是，开始日期也传递季度末月份的首日期
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		LrbquarterlyVO[] vos = null;
		try {
			vos = zxkjReportService.getLRBquarterlyVOs(paramVO);
		} catch (Exception e) {// 此异常吃掉。因为有时候查询的期间不在范围内。
			log.error("错误",e);
		}
		Map<String, LrbquarterlyVO[]> map = new HashMap<String, LrbquarterlyVO[]>();
		String key = pk_corp + "," + period;
		map.put(key, vos);
		return map;
	}

	@Override
	public DZFDouble getHcLrbJiduValue(LrbquarterlyVO[] lrbvos, String hc, String period) throws DZFWarpException {
		if (lrbvos == null || lrbvos.length == 0)
			return null;
		DZFDouble z = null;
		int iMonth = Integer.parseInt(period.substring(5, 7));
		for (int i = 0; i < lrbvos.length; i++) {
			if (hc.equals(lrbvos[i].getHs())) {// 取本季金额
				if (1 <= iMonth && iMonth <= 3) {
					z = lrbvos[i].getQuarterFirst();
					break;
				} else if (4 <= iMonth && iMonth <= 6) {
					z = lrbvos[i].getQuarterSecond();
					break;
				} else if (7 <= iMonth && iMonth <= 9) {
					z = lrbvos[i].getQuarterThird();
					break;
				} else if (10 <= iMonth && iMonth <= 12) {
					z = lrbvos[i].getQuarterFourth();
					break;
				}
			}
		}
		return z;
	}
	@Override
	public DZFDouble getBmLrbJiduValue(LrbquarterlyVO[] lrbvos, String bm, String period) throws DZFWarpException {
		if (lrbvos == null || lrbvos.length == 0)
			return null;
		DZFDouble z = null;
		int iMonth = Integer.parseInt(period.substring(5, 7));
		for (int i = 0; i < lrbvos.length; i++) {
			if (bm.equalsIgnoreCase(lrbvos[i].getHs_id())) {// 取本季金额
				if (1 <= iMonth && iMonth <= 3) {
					z = lrbvos[i].getQuarterFirst();
					break;
				} else if (4 <= iMonth && iMonth <= 6) {
					z = lrbvos[i].getQuarterSecond();
					break;
				} else if (7 <= iMonth && iMonth <= 9) {
					z = lrbvos[i].getQuarterThird();
					break;
				} else if (10 <= iMonth && iMonth <= 12) {
					z = lrbvos[i].getQuarterFourth();
					break;
				}
			}
		}
		return z;
	}
	@Override
	public Map<String, XjllbVO[]> getxjllInfo(String pk_corp, String period) throws DZFWarpException {
		Map<String, XjllbVO[]> map = new HashMap<String, XjllbVO[]>();
		addXjllVOs(pk_corp, period, map);
		return map;
	}

	@Override
	public DZFDouble getHcxjllValue(XjllbVO[] xjllvos, String hc, String qmye) throws DZFWarpException {
		if (xjllvos == null || xjllvos.length == 0)
			return null;
		DZFDouble z = null;
		for (XjllbVO xjll : xjllvos) {
			if (hc.equals(xjll.getHc())) {
				if ("byje".equals(qmye)) {
					z = xjll.getBqje();
					break;
				} else if ("ljje".equals(qmye)) {
					z = xjll.getSqje();
					break;
				}
			}
		}
		return z;
	}
	@Override
	public DZFDouble getBmxjllValue(XjllbVO[] xjllvos, String bm, String qmye) throws DZFWarpException {
		if (xjllvos == null || xjllvos.length == 0)
			return null;
		DZFDouble z = null;
		for (XjllbVO xjll : xjllvos) {
			if (bm.equalsIgnoreCase(xjll.getHc_id())) {
				if ("byje".equals(qmye)) {
					z = xjll.getBqje();
					break;
				} else if ("ljje".equals(qmye)
						|| "snje".equals(qmye)) {
					z = xjll.getSqje();
					break;
				}
			}
		}
		return z;
	}
	private void addXjllVOs(String pk_corp, String period, Map<String, XjllbVO[]> map) throws DZFWarpException {
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setQjq(period);
		try {
			XjllbVO[] xjllbvos = zxkjReportService.getXJLLVOs(paramVO);
			String key = pk_corp + "," + period;
			map.put(key, xjllbvos);
		} catch (Exception e) {// 此异常吃掉。因为有时候查询的期间不在范围内。
			log.error("错误",e);
		}
	}

	@Override
	public Map<String, XjllbVO[]> getxjllsqInfo(String pk_corp, String period, String preperiod)
			throws DZFWarpException {
		Map<String, XjllbVO[]> map = new HashMap<String, XjllbVO[]>();
		// 本期
		addXjllVOs(pk_corp, period, map);
		// 上期
		addXjllVOs(pk_corp, preperiod, map);
		return map;
	}

	@Override
	public DZFDouble getHcxjllsqValue(XjllbVO[] xjllvos, XjllbVO[] prexjllvos, String hc, String qmye)
			throws DZFWarpException {
		DZFDouble z = null;
		if ("bqje".equals(qmye)) {
			if (xjllvos == null || xjllvos.length == 0)
				return z;
			for (int i = 0; i < xjllvos.length; i++) {
				if (hc.equals(xjllvos[i].getHc())) {
					z = xjllvos[i].getSqje();
					break;
				}
			}
		} else if ("sqje".equals(qmye)) {
			if (prexjllvos == null || prexjllvos.length == 0)
				return z;
			for (int i = 0; i < prexjllvos.length; i++) {
				if (hc.equals(prexjllvos[i].getHc())) {
					z = prexjllvos[i].getSqje();
					break;
				}
			}
		}
		return z;
	}
	
	@Override
	public DZFDouble getBmxjllsqValue(XjllbVO[] xjllvos, XjllbVO[] prexjllvos, String bm, String qmye)
			throws DZFWarpException {
		DZFDouble z = null;
		if ("bqje".equals(qmye)) {
			if (xjllvos == null || xjllvos.length == 0)
				return z;
			for (int i = 0; i < xjllvos.length; i++) {
				if (bm.equalsIgnoreCase(xjllvos[i].getHc_id())) {
					z = xjllvos[i].getSqje();
					break;
				}
			}
		} else if ("sqje".equals(qmye)) {
			if (prexjllvos == null || prexjllvos.length == 0)
				return z;
			for (int i = 0; i < prexjllvos.length; i++) {
				if (bm.equalsIgnoreCase(prexjllvos[i].getHc_id())) {
					z = prexjllvos[i].getSqje();
					break;
				}
			}
		}
		return z;
	}

	@Override
	public DZFDouble getCjssl(String pk_corp) throws DZFWarpException {
//		CorpVO corpvo = (CorpVO) getSingleObjectBO().queryByPrimaryKey(CorpVO.class, pk_corp);
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		return taxvo.getCitybuildtax();
	}
	
	@Override
	public Object getGsxx(String pk_corp, String key) throws DZFWarpException{
		Object obj = null;
		CorpVO corpvo = (CorpVO) getSingleObjectBO().queryByPrimaryKey(CorpVO.class, pk_corp);
		obj = corpvo.getAttributeValue(key);//先从公司信息找
		if(obj == null){//再从税务信息找
			CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
			obj = corptaxvo.getAttributeValue(key);
		}
		return obj;
	}

	@Override
	public DZFDouble getGslx(String pk_corp) throws DZFWarpException {
		CorpVO corpvo = (CorpVO) getSingleObjectBO().queryByPrimaryKey(CorpVO.class, pk_corp);
		DZFBoolean isPerson = corpvo.getIspersonal();
		return isPerson != null && isPerson.booleanValue() ? new DZFDouble(1) : new DZFDouble(0);
	}

	@Override
	public DZFDouble getNational(String pk_corp) throws DZFWarpException {
//		CorpVO corpvo = (CorpVO) getSingleObjectBO().queryByPrimaryKey(CorpVO.class, pk_corp);
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		Integer isdsbsjg = taxvo.getIsdsbsjg();

		return isdsbsjg != null ? new DZFDouble(isdsbsjg) : DZFDouble.ZERO_DBL;// 所得税报送机关是国税是1,否则是地税
	}

	@Override
	public DZFDouble queryzzsFpValue(String[] taxcodes, String fpstyle, String style, String dc, String period2, String pk_corp)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		// sp.addParam(taxcode);

		StringBuffer sf = new StringBuffer();
		String part = "";
		if ("税额".equals(style)) {
			part = "taxmny";
		} else if ("金额".equals(style)) {
			part = "mny";
		}
		
		Integer vdirect = 0;
		if("dr".equals(dc)){//借方
			vdirect = 0;
		}else{//贷方
			vdirect = 1;
		}
		
		String taxsql = SqlUtil.buildSqlForIn("taxcode", taxcodes);

		sf.append(" select sum(");
		sf.append(part);
		sf.append(") mny from ynt_pztaxitem where ");
		sf.append(taxsql);
		sf.append(" and nvl(vdirect,0) = ? ");
		sp.addParam(vdirect);

		if (!StringUtil.isEmpty(fpstyle)) {// fpstyle 如果未空，取所有的
			String[] fpstyles = fpstyle.split("\\+");
			sf.append(" and fp_style in (");
			for (int i = 0; i < fpstyles.length; i++) {
				if (i > 0) {
					sf.append(",");
				}
				sf.append("?");
				sp.addParam(fpstyles[i]);
			}
			sf.append(")");
		}

		sf.append(" and period = ? and pk_corp = ? and nvl(dr,0) = 0 ");
		sp.addParam(period2);
		sp.addParam(pk_corp);

		DZFDouble zz = (DZFDouble) getSingleObjectBO().executeQuery(sf.toString(), sp, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				DZFDouble mny = null;
				if (rs.next()) {
					mny = new DZFDouble(rs.getDouble("mny"));
				}
				return mny;
			}

		});
		return zz;
	}

	@Override
	public DZFDouble queryzzsFpValue2(String[] taxcodes, String fpstyle, String style, String dc, String period2, String pk_corp)
			throws DZFWarpException {
		String prevPeriod = DateUtils.getPreviousPeriod(period2);
		prevPeriod = DateUtils.getPreviousPeriod(prevPeriod);

		SQLParameter sp = new SQLParameter();
		// sp.addParam(taxcode);
		//
		StringBuffer sf = new StringBuffer();
		String part = "";
		if ("税额".equals(style)) {
			part = "taxmny";
			// sql = " select sum(taxmny) mny from ynt_pztaxitem "
			// + " where taxcode = ? and fp_style = ? and period >= ? and period
			// <= ? and pk_corp = ? and nvl(dr,0) = 0 ";
		} else if ("金额".equals(style)) {
			part = "mny";
			// sql = " select sum(mny) mny from ynt_pztaxitem "
			// + " where taxcode = ? and fp_style = ? and period >= ? and period
			// <= ? and pk_corp = ? and nvl(dr,0) = 0 ";
		}
		
		Integer vdirect = 0;
		if("dr".equals(dc)){//借方
			vdirect = 0;
		}else{//贷方
			vdirect = 1;
		}

		String taxsql = SqlUtil.buildSqlForIn("taxcode", taxcodes);

		sf.append(" select sum(");
		sf.append(part);
		sf.append(") mny from ynt_pztaxitem where ");
		sf.append(taxsql);
		sf.append(" and nvl(vdirect,0) = ? ");
		sp.addParam(vdirect);
		
		if (!StringUtil.isEmpty(fpstyle)) {// fpstyle 如果未空，取所有的
			String[] fpstyles = fpstyle.split("\\+");
			sf.append(" and fp_style in (");
			for (int i = 0; i < fpstyles.length; i++) {
				if (i > 0) {
					sf.append(",");
				}
				sf.append("?");
				sp.addParam(fpstyles[i]);
			}
			sf.append(")");
		}
		
		sf.append(" and period >= ? and period <= ? and pk_corp = ? and nvl(dr,0) = 0 ");
		sp.addParam(prevPeriod);
		sp.addParam(period2);
		sp.addParam(pk_corp);

		DZFDouble zz = (DZFDouble) getSingleObjectBO().executeQuery(sf.toString(), sp, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				DZFDouble mny = null;
				if (rs.next()) {
					mny = new DZFDouble(rs.getDouble("mny"));
				}
				return mny;
			}

		});
		return zz;
	}

	@Override
	public DZFDouble getGlBeginningfse(String subjcode,String dc,String pk_corp,Map<String,List<QcYeVO>> beginMap)throws DZFWarpException {
		if(beginMap==null || beginMap.size()==0){
			beginMap = queryQcyebycorp(pk_corp);
		}
		DZFDouble result = DZFDouble.ZERO_DBL;
		List<QcYeVO> list =  null;
		if(beginMap != null){
			list = beginMap.get(subjcode);
		}
		if(list == null || list.size() == 0)
			return result;
		if ("dr".equals(dc)) {// 借方
			for(QcYeVO vo : list){
				result = SafeCompute.add(result, vo.getYearjffse());
			}
		}else{//贷方
			for(QcYeVO vo : list){
				result = SafeCompute.add(result, vo.getYeardffse());
			}
		}
		return result;
	}
	
	private Map<String,List<QcYeVO>> queryQcyebycorp(String pk_corp){
		//查询综合本位币
		List<QcYeVO> list = gl_qcyeserv.queryAllQcInfo(pk_corp, DZFConstant.ZHBWB, null);
		Map<String,List<QcYeVO>> map = DZfcommonTools.hashlizeObject(list, new String[]{"vcode"});
		return map;
	}

	@Override
	public Map<String, XjllquarterlyVo[]> getxjllQuarterInfo(String pk_corp,
															 String period) throws DZFWarpException {
		int mon = Integer.valueOf(period.substring(5, 7));
		int quarter = (mon - 1) / 3 + 1;
		QueryParamVO param = new QueryParamVO();
		param.setPk_corp(pk_corp);
		param.setBegindate1(new DZFDate(period + "-01"));
		XjllquarterlyVo[] vos = null;
		try {
			List<XjllquarterlyVo> list = zxkjReportService.getXjllQuartervos(param, String.valueOf(quarter));
			if (list != null) {
				vos = list.toArray(new XjllquarterlyVo[0]);
			}
		} catch (Exception e) {// 此异常吃掉。因为有时候查询的期间不在范围内。
			log.error("错误",e);
		}
		Map<String, XjllquarterlyVo[]> map = new HashMap<String, XjllquarterlyVo[]>();
		String key = pk_corp + "," + period;
		map.put(key, vos);
		return map;
	}

	@Override
	public DZFDouble getHcxjllQuarterValue(XjllquarterlyVo[] vos, String hc,
			String period) throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return null;
		DZFDouble z = null;
		int iMonth = Integer.parseInt(period.substring(5, 7));
		for (int i = 0; i < vos.length; i++) {
			if (hc.equals(vos[i].getHc())) {// 取本季金额
				if (1 <= iMonth && iMonth <= 3) {
					z = vos[i].getJd1();
					break;
				} else if (4 <= iMonth && iMonth <= 6) {
					z = vos[i].getJd2();
					break;
				} else if (7 <= iMonth && iMonth <= 9) {
					z = vos[i].getJd3();
					break;
				} else if (10 <= iMonth && iMonth <= 12) {
					z = vos[i].getJd4();
					break;
				}
			}
		}
		return z;
	}
	@Override
	public DZFDouble getBmxjllQuarterValue(XjllquarterlyVo[] vos, String bm,
			String period) throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return null;
		DZFDouble z = null;
		int iMonth = Integer.parseInt(period.substring(5, 7));
		for (int i = 0; i < vos.length; i++) {
			if (bm.equalsIgnoreCase(vos[i].getHc_id())) {// 取本季金额
				if (1 <= iMonth && iMonth <= 3) {
					z = vos[i].getJd1();
					break;
				} else if (4 <= iMonth && iMonth <= 6) {
					z = vos[i].getJd2();
					break;
				} else if (7 <= iMonth && iMonth <= 9) {
					z = vos[i].getJd3();
					break;
				} else if (10 <= iMonth && iMonth <= 12) {
					z = vos[i].getJd4();
					break;
				}
			}
		}
		return z;
	}
	@Override
	public DZFDouble getDeficit(String pk_corp, String year) throws DZFWarpException {
		QmLossesVO lossvo = gl_qmclserv.queryLossmny(new DZFDate(year + "-01-01"), pk_corp);
		
		DZFDouble mny = null;
		if(lossvo != null)
			mny = lossvo.getNlossmny();
		return mny;
	}

	/**
	 * 
	 */
	@Override
	public DZFDouble getSpecialdDeductionCumul(List list,DZFDate  begprodate,String bperiod, String eperiod, String pk_corp, String key)
			throws DZFWarpException {
		// 如果 key 空 获取三险一金的本年累计 不为空 获取其中一项的本年累计
		DZFDouble zxkc = DZFDouble.ZERO_DBL;
		if ("2019-01".compareTo(eperiod) > 0) {
			return zxkc;
		}
		if(DZFValueCheck.isEmpty(list))
			return zxkc;
					
		// 开始经营生产日期
		String begproperiod = null;
		if (begprodate != null) {
			begproperiod = DateUtils.getPeriod(begprodate);
			if (!DZFValueCheck.isEmpty(bperiod)) {
				if (bperiod.compareTo(begproperiod) < 0) {
					throw new BusinessException("查询专项扣除累计数的开始期间不能早于开始经营生产日期!");
				}
			}
		}

		if (DZFValueCheck.isEmpty(eperiod)) {
			throw new BusinessException("查询专项扣除累计数的结束期间不能为空!");
		}
		int year = DateUtils.getPeriodStartDate(eperiod).getYear();
		if (DZFValueCheck.isEmpty(bperiod)) {
			bperiod = year + "-01";
			if (!DZFValueCheck.isEmpty(begproperiod)) {
				if (eperiod.compareTo(begproperiod) < 0) {
					throw new BusinessException("查询专项扣除累计数的结束期间不能早于开始经营生产日期!");
				}
				if (bperiod.compareTo(begproperiod) < 0) {
					bperiod = begproperiod;
				}
			}
		}

		int syear = DateUtils.getPeriodStartDate(bperiod).getYear();
		if (year != syear) {
			throw new BusinessException("查询专项扣除累计数的开始期间与结束期间必须在同一年!");
		}

		SpecDeductHistVO vo = (SpecDeductHistVO) list.get(0);
		// 如果变更期间最大的日期早于查询的开始期间 则 取变更期间最大的专项扣除数 累计
		DZFDouble tempnum = DZFDouble.ZERO_DBL;
		String tempperiod = null;
		for (int i = 1; i < 13; i++) {
			tempperiod = year + (i < 10 ? ("-0" + i) : ("-" + i));
			if (bperiod.compareTo(tempperiod) > 0) {
				continue;
			}
			vo = getLastPeriodVO(list, tempperiod);
			tempnum = getZxkc(vo, key);
			zxkc = SafeCompute.add(zxkc, tempnum);
			if (tempperiod.equals(eperiod)) {
				break;
			}
		}
		return zxkc;
	}
	
	private SpecDeductHistVO getLastPeriodVO(List list, String tempperiod) {
		if (DZFValueCheck.isEmpty(list)) {
			return new SpecDeductHistVO();
		}
		SpecDeductHistVO vo = null;
		for (Object o : list) {
			vo = (SpecDeductHistVO) o;
			if (tempperiod.compareTo(vo.getBgperiod()) >= 0) {
				break;
			}
			vo =null;
		}
		if(vo == null)
			vo= new SpecDeductHistVO();
		return vo;
	}
	
	private DZFDouble getZxkc(SpecDeductHistVO vo, String key) {
		DZFDouble zxkcxj = DZFDouble.ZERO_DBL;
		if (DZFValueCheck.isEmpty(key)) {
			zxkcxj = SafeCompute.add(vo.getYanglaobx(), vo.getYiliaobx());
			zxkcxj = SafeCompute.add(zxkcxj, vo.getShiyebx());
			zxkcxj = SafeCompute.add(zxkcxj, vo.getZfgjj());
		} else {
			zxkcxj = (DZFDouble) vo.getAttributeValue(key);
		}
		return zxkcxj;
	}
	
	public CorpTaxVo queryCorpTaxVO(String pk_corp ) {
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		return corptaxvo;
	}
	
	public List querySpecChargeHis(String pk_corp ) {
		List list = sys_corp_tax_serv.querySpecChargeHis(pk_corp);
		return list;
	}

	@Override
	public DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs) throws DZFWarpException {
		DZFDouble res = DZFDouble.ZERO_DBL;
		//根据期间，编号，类型，查询对应的主表数据
		SQLParameter sp = new SQLParameter();
		List<String> list = getJDListPeriod(period);
		String wherepart = SqlUtil.buildSqlForIn("periodfrom||periodto", list.toArray(new String[0]));
		SpreadTool tool = new SpreadTool(this);
		if(cpvo.getChargedeptname().equals("一般纳税人")){
			if("增值税纳税申报表（适用于增值税一般纳税人）".equals(rptname)){//按照月查询
				sp.addParam(cpvo.getPk_corp());
				sp.addParam(TaxRptConst.SB_ZLBH10101);//一般纳税人增值税
				sp.addParam(0);//月
				res = getTaxValue1(rptname, zbs, sp, tool,wherepart);
			}else if("所得税月(季)度纳税申报表(A类）".equals(rptname)){
				sp.clearParams();
				sp.addParam(cpvo.getPk_corp());
				sp.addParam(TaxRptConst.SB_ZLBH10412);//企业所得税A
				sp.addParam(1);//季
				res = getTaxValue1(rptname,zbs, sp, tool," periodfrom||periodto = '"+(list.get(0).substring(0, 10)+list.get(list.size()-1).substring(10))+"'");
			}
		}else{
			if("增值税纳税申报表".equals(rptname)){
				sp.clearParams();
				sp.addParam(cpvo.getPk_corp());
				sp.addParam(TaxRptConst.SB_ZLBH10102);//小规模纳税人增值税
				sp.addParam(1);//季
				res = getTaxValue1(rptname, zbs, sp, tool," periodfrom||periodto = '"+(list.get(0).substring(0, 10)+list.get(list.size()-1).substring(10))+"'");
			}else if("所得税月(季)度纳税申报表(A类）".equals(rptname)){
				sp.clearParams();
				sp.addParam(cpvo.getPk_corp());
				sp.addParam(TaxRptConst.SB_ZLBH10412);//企业所得税A
				sp.addParam(1);//季
				res = getTaxValue1(rptname, zbs, sp, tool," periodfrom||periodto = '"+(list.get(0).substring(0, 10)+list.get(list.size()-1).substring(10))+"'");
			}
		}
		return res;
	}


	public static List<String> getJDListPeriod(String period) {
		String year = period.substring(0, 4);
		int month = Integer.parseInt(period.substring(5, 7));
		List<String> list = new ArrayList<String>();
		String periodtemp = "";
		String qj = "";
		for(int i =0;i<3;i++){
			periodtemp = year+"-"+ String.format("%02d", (month-i));
			qj = DateUtils.getPeriodStartDate(periodtemp).toString()+DateUtils.getPeriodEndDate(periodtemp).toString();
			list.add(qj);
			if((month-i)%3 ==1){
				break;
			}
		}
		for(int i =1;i<3;i++){
			if((month+i)>12 || month%3 == 0){
				break;
			}

			periodtemp = year+"-"+ String.format("%02d", (month+i));
			qj = DateUtils.getPeriodStartDate(periodtemp).toString()+DateUtils.getPeriodEndDate(periodtemp).toString();
			list.add(qj);
			if((month+i)%3 ==0){
				break;
			}
		}

		Collections.sort(list);

		return list;
	}

	private DZFDouble getTaxValue1(String rptname, int[][] zbs,  SQLParameter sp,
								   SpreadTool tool,String periodwherepart) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		TaxReportVO[] reportvos = (TaxReportVO[]) singleObjectBO.queryByCondition(TaxReportVO.class,
				"pk_corp = ? and sb_zlbh=? and nvl(dr,0)=0 and  periodtype = ? "+"and "+periodwherepart, sp);
		//根据主表查询子表数据
		if (reportvos != null && reportvos.length > 0) {
			List<String> list = new ArrayList<String>();
			for(TaxReportVO vo:reportvos){
				if(!StringUtil.isEmpty(vo.getPk_taxreport())){
					list.add(vo.getPk_taxreport());
				}
			}
			sp.clearParams();
			sp.addParam(rptname);
			String wherepart = SqlUtil.buildSqlForIn("pk_taxreport", list.toArray(new String[0]));
			TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) singleObjectBO.queryByCondition(TaxReportDetailVO.class	,
					"nvl(dr,0)=0 and "+wherepart+"and reportname =? ", sp);
			if(detailvos!=null && detailvos.length>0){
				for(TaxReportDetailVO  bvo:detailvos){
					if(!StringUtil.isEmpty(bvo.getSpreadfile())){
						res = SafeCompute.add(res, getTaxValueFromJson(rptname, zbs, tool, bvo));
					}
				}
			}
		}
		return res;
	}


	private DZFDouble getTaxValueFromJson(String rptname,int[][] zbs, SpreadTool tool, TaxReportDetailVO bvo) {
		String strJson  = TaxRptemptools.readFileString(bvo.getSpreadfile());
		DZFDouble dbSE =DZFDouble.ZERO_DBL;
		Map objMap = JsonUtils.deserialize(strJson, Map.class);
		Object objValue;
		DZFDouble objdouble;
		for(int[] zb:zbs){
			objValue= tool.getCellValue(objMap, rptname, zb[0], zb[1]);
			if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
				try {
					objdouble =  new DZFDouble(objValue.toString());
				} catch (NumberFormatException e) {
					log.info("内容:"+objValue);
					objdouble = DZFDouble.ZERO_DBL;
				}
				dbSE  = SafeCompute.add(dbSE, objdouble) ;
			}
		}
		return dbSE;
	}

	public DZFDouble getTaxSale6(String pk_corp) throws DZFWarpException{
		String item = CaclTaxMny.getTaxItemPercent6(pk_corp);
		DZFDouble result;
		if("abcd12345678efaabd000106".equals(item)){//简易计税
			result = new DZFDouble(1);
		}else{
			result = DZFDouble.ZERO_DBL;
		}
		return result;
	}

}