package com.dzf.zxkj.platform.services.report.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.entity.ICodeName;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 公共工具类
 * 
 * 
 */
@Component("yntBoPubUtil")
@SuppressWarnings("all")
public class YntBoPubUtil implements IYntBoPubUtil {
 
	private SingleObjectBO singleObjectBO = null;
	
	private ICpaccountService im ;

	private ICpaccountCodeRuleService ic ;

	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;

	public ICpaccountService getIm() {
		return im;
	}
	@Autowired
	public void setIm(ICpaccountService im) {
		this.im = im;
	}

	public ICpaccountCodeRuleService getIc() {
		return ic;
	}
	@Autowired
	public void setIc(ICpaccountCodeRuleService ic) {
		this.ic = ic;
	}

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	private BdCurrencyVO vo_cny_currency = null;
//
	public String getNewVoucherNo(String pk_corp, DZFDate doperatedate)
			throws DZFWarpException {
		
		//modify by zhangj pzh修改
		if(pk_corp==null || doperatedate == null){
			throw new BusinessException("获取凭证号失败，公司或者日期为空!");
		}
		SQLParameter sp=new SQLParameter();
		sp.addParam((doperatedate.getYear() + "-" +doperatedate.getStrMonth()) + "%");
		sp.addParam(pk_corp);
		String sql="select max(pzh) pzh from  YNT_TZPZ_H where doperatedate like ?  and nvl(dr,0)=0 and pk_corp=? ";
		// 查询凭证号--暂时过滤到老数据
		String pzh=(String) singleObjectBO.executeQuery(sql,sp, new ObjectProcessor());
	//	List<TzpzHVO> pzVOs = (List<TzpzHVO>) singleObjectBO.retrieveByClause(TzpzHVO.class,  "doperatedate like ?  and (dr,0)=0 and pk_corp='" + pk_corp + "' order by pzh desc ",null);	
		if (StringUtil.isEmptyWithTrim(pzh)==false) {
		
				int maxNo = Integer.valueOf(pzh) + 1;
				if(maxNo>9999){
					throw new BusinessException("超过凭证号最大值9999，获取凭证号失败!");
				}
				if (maxNo > 9 && maxNo < 100) {
					pzh = "00" + maxNo;
				} else if (maxNo <= 9) {
					pzh = "000" + maxNo;
				} else if (maxNo >= 100 && maxNo < 1000) {
					pzh = "0" + maxNo;
				} else {
					pzh = "" + maxNo;
				}
				return  pzh;
			
		} 
		return "0001";
		
	}
//
//	/**
//	 * 根据公司主键获取当前日期最新凭证号
//	 * 
//	 * @param pk_corp
//	 * @return
//	 * @throws BusinessException
//	 */
//	public String getNewVoucherNo(String pk_corp) throws BusinessException {
//
//		//modify by zhangj 
//		DZFDate nowDate = DZFDate.getDate(new Long(InvocationInfoProxy
//				.getInstance().getDate()));
//		return getNewVoucherNo(pk_corp, nowDate);
//	}

	/**
	 * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
	 * 
	 * @param pk_trade_account
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public String getCorpAccountPkByTradeAccountPk(String pk_trade_account,
			String pk_corp) throws DZFWarpException {
		String pk = getCorpAccountPkByTradeAccountPkWithMsg(pk_trade_account, pk_corp,null);

		return pk;

	}
	
	/**
	 * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
	 * 
	 * @param pk_trade_account
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public String getCorpAccountPkByTradeAccountPkWithMsg(String pk_trade_account,
			String pk_corp,String msg) throws DZFWarpException {
		BdTradeAccountVO jfkmVO = (BdTradeAccountVO) singleObjectBO.queryVOByID(pk_trade_account, BdTradeAccountVO.class);
		if (jfkmVO == null) {
			throw new BusinessException("行业会计科目主键为" + pk_trade_account
					+ "的科目已被删除，请检查");
		}
		SQLParameter sp=new SQLParameter();
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		ICpaccountCodeRuleService  gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
		String newaccount = gl_accountcoderule.getNewRuleCode(jfkmVO.getAccountcode(), olerule, newrule);
		sp.addParam(newaccount);
		sp.addParam(pk_corp);
		
		String condition = "  accountcode=? and pk_corp=? and nvl(dr,0)=0 ";
		YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[]) singleObjectBO
				.queryByCondition(YntCpaccountVO.class,condition,sp);
		if (gsjfkmVOs == null || gsjfkmVOs.length < 1) {
			if(StringUtil.isEmpty(msg)){
				throw new BusinessException("科目编码为" + jfkmVO.getAccountcode()
				+ "的科目不存在，如需继续操作，请配置相应模板。");
			}else{
				throw new BusinessException("科目编码为" + jfkmVO.getAccountcode()
				+ "的科目不存在，"+msg);
			}
		}

		return gsjfkmVOs[0].getPrimaryKey();

	}

//	/**
//	 * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
//	 * 
//	 * @param pk_trade_account
//	 * @param pk_corp
//	 * @return
//	 * @throws BusinessException
//	 */
//	public String queryCAccountPkByTAccountcode(String pk_trade_account,
//			String accountcode, String pk_corp) throws BusinessException {
//		String sql = " isleaf='Y' and accountcode= '" + accountcode
//				+ "' and pk_corp='" + pk_corp + "' and (dr,0)=0 ";
//		YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[]) bo.queryByCondition(
//				YntCpaccountVO.class, sql);
//		if (gsjfkmVOs == null || gsjfkmVOs.length < 1) {
//			throw new BusinessException("根据行业科目没有找到对应的公司科目，行业或公司科目编码为"
//					+ accountcode + "的科目不是末级科目或已被删除，请检查");
//		}
//		return gsjfkmVOs[0].getPrimaryKey();
//
//	}
//
	/**
	 * 获取默人民币VO
	 * 
	 * @return
	 * @throws UifException
	 */
	public String getCNYPk() throws DZFWarpException {
		if (vo_cny_currency == null) {
			BdCurrencyVO[] vos = (BdCurrencyVO[]) singleObjectBO
					.queryByCondition(BdCurrencyVO.class,
							" currencycode='CNY' and nvl(dr,0)=0 ",null);
			if (vos != null && vos.length > 0) {
				vo_cny_currency = vos[0];
			} else {
				throw new BusinessException("币种档案没有定义币种编码为CNY的人民币币种，请检查");
			}
		}
		return vo_cny_currency.getPrimaryKey();
	}

	// /**
	// * 根据固定资产取数科目类型，获取资产金额
	// * @param assetcardVO
	// * @param accountkind
	// * @return
	// */
	// public double getAssetAccountMny(ASSETCARDVO assetcardVO, int
	// accountkind){
	// switch(accountkind){
	// case BDTRADEASSETTEMPLATEBVO.KIND_FYKM: // 费用科目
	// return 0;
	// case BDTRADEASSETTEMPLATEBVO.KIND_GDZC: // 固定资产
	// return assetcardVO.getAssetmny().getDouble();
	// case BDTRADEASSETTEMPLATEBVO.KIND_GDZCQL: // 固定资产清理
	// return assetcardVO.getAssetnetvalue().getDouble();
	// case BDTRADEASSETTEMPLATEBVO.KIND_LJZJ: // 累计折旧
	// return assetcardVO.getDepreciation().getDouble();
	// default:
	// return 0;
	// }
	// }

	/**
	 * 根据科目、期间查找取数科目的本期发生额(借方：借方发生累计-贷方发生累计；贷方：贷方发生累计-借方发生累计)
	 * 
	 * @param period
	 * @param pk_accsubj
	 * @return
	 * @throws BusinessException
	 */
	public DZFDouble getThisPeriodOccurMny(String pk_corp, String period, String pk_accsubj)
			throws DZFWarpException {
		// 本期累计发生额
		DZFDouble ye = DZFDouble.ZERO_DBL;

		if (StringUtil.isEmptyWithTrim(period)) {
			throw new BusinessException("获取本期累计发生额时，传入期间为空");
		}
		if (StringUtil.isEmptyWithTrim(pk_accsubj)) {
			throw new BusinessException("获取本期累计发生额时，传入科目为空");
		}
		SQLParameter sp=new SQLParameter();
		sp.addParam(period+"%");
		sp.addParam(pk_accsubj);
		sp.addParam(pk_corp);
		String sql = " select sum(b.jfmny) as jf,sum(b.dfmny) as df ,b.pk_accsubj,b.vdirect  as fx  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h where h.doperatedate like ? and b.pk_accsubj=?"
				+ " h.pk_corp=? and nvl(b.dr,0)=0 group  by b.pk_accsubj,b.vdirect ";
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sql,sp,
				new BeanListProcessor(KmZzVO.class));

		YntCpaccountVO qscorpAccountVO = accountService.queryMapByPk(pk_corp).get(pk_accsubj);//(YntCpaccountVO) singleObjectBO.queryVOByID(pk_accsubj, YntCpaccountVO.class);

		if (qscorpAccountVO == null) {
			throw new BusinessException("获取本期累计发生额时，传入科目的科目不存在，请检查");
		}

		if (qscorpAccountVO.getDirection() == null) {
			throw new BusinessException("获取本期累计发生额时，传入科目的科目【"
					+ qscorpAccountVO.getAccountcode() + ","
					+ qscorpAccountVO.getAccountname() + "】方向不明确，请检查");
		}

		Integer fx = qscorpAccountVO.getDirection() == null ? 0
				: qscorpAccountVO.getDirection();

		// 本年合计
		DZFDouble jf = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;

		if (result != null && result.size() > 0) {
			KmZzVO kmzzVO = (KmZzVO) result.get(0);
			jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getJf();
			df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getDf();

			if (fx.equals(0)) {
				// 借方
				ye = jf.sub(df);
			} else {
				// 贷方
				ye = df.sub(jf);
			}
		}

		return ye;
	}

	/**
	 * 判断传入公司是否使用2007的科目方案
	 * 
	 * @return
	 */
	public boolean is2007AccountSchema(String pk_corp) throws DZFWarpException {
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		//corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,CorpVO.class);

		if (corpVO != null) {
			if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
				// ynt_tdaccschema
				BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) singleObjectBO.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
				if (schemaVO != null) {
					if (schemaVO.getAccountstandard() == 1) {
						// 为2007新会计准则
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断所使用的科目方案
	 * 
	 * @return
	 */
	public Integer getAccountSchema(String pk_corp) throws DZFWarpException {
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		if (corpVO != null) {
			if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
				BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) singleObjectBO.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
				if (schemaVO != null) {
					return schemaVO.getAccountstandard();
				}
			}
		}
		return -1;
	}
	

	/**
	 * 判断传入公司返回科目方案
	 * 
	 * @param pk_corp
	 * @return
	 */
	public String getCurrentCorpAccountSchema(String pk_corp)
			throws DZFWarpException {
		 CorpVO corpVO = corpService.queryByPk(pk_corp);
		//corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp, CorpVO.class);

		if (corpVO != null) {
			if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
				// ynt_tdaccschema
				return corpVO.getCorptype();

			}
		}
		return null;
	}

	/**
	 * 根据科目、期间查找取数科目的期末余额
	 * 
	 * @param period
	 * @param pk_accsubj
	 * @return
	 * @throws BusinessException
	 */
	public DZFDouble getQmMny(String pk_corp,String period, String pk_accsubj)
			throws DZFWarpException {
		DZFDouble bqfseMny = DZFDouble.ZERO_DBL;

		// 根据科目编码找到科目主键
		YntCpaccountVO kmVO =accountService.queryMapByPk(pk_corp).get(pk_accsubj);//  (YntCpaccountVO) singleObjectBO.queryVOByID(pk_accsubj, YntCpaccountVO.class);
		if (kmVO == null) {
			throw new BusinessException("查询期末余额根据传入科目主键" + pk_accsubj
					+ "已不存在，可能被删除，请检查！");
		}

		String pk_km = kmVO.getPk_corp_account();
		int fx = kmVO.getDirection() == null ? 0 : kmVO.getDirection();

		// 查询期初、期末表中，是否存在当笔期末数据
		SQLParameter params = new SQLParameter();
		params.addParam(kmVO.getPk_corp());
		params.addParam(period);
		params.addParam(pk_km);
		
		KMQMJZVO[] qmjzVOs = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class,
				" pk_corp=? and period=? and pk_accsubj=? and nvl(dr,0)=0 ",params);
		if (qmjzVOs != null && qmjzVOs.length > 0) {
			if (qmjzVOs[0].getThismonthqm() != null
					&& qmjzVOs[0].getThismonthqm().doubleValue() != 0.00) {
				// 说明已经做过期末结账，则直接取期末数即可
				return qmjzVOs[0].getThismonthqm();
			}
		}
		params.clearParams();
		DZFDate qjDate = DZFDate.getDate(period + "-01");
		params.addParam(kmVO.getPk_corp());
		params.addParam(pk_km);
		params.addParam(period + "-" + qjDate.getDaysMonth());
		// 说明没有做过期末结账，则取期初+截止本期的累计发生额
		QcYeVO[] qcyeVOs = (QcYeVO[]) singleObjectBO.queryByCondition(
				QcYeVO.class," pk_corp=? and pk_accsubj=? and doperatedate<=? and nvl(dr,0)=0 ",params);
		DZFDouble qcye = DZFDouble.ZERO_DBL;
		if (qcyeVOs != null && qcyeVOs.length > 0) {
			qcye = qcyeVOs[0].getThismonthqc() == null ? DZFDouble.ZERO_DBL
					: qcyeVOs[0].getThismonthqc();
		}

		// 截止本期的累计发生额
		//String sql = " select sum(b.jfmny) as jf,sum(b.dfmny) as df  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h where h.ishasjz='Y' and h.doperatedate<='"
		String sql = " select sum(b.jfmny) as jf,sum(b.dfmny) as df  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h where h.doperatedate<='"
				+ (period + "-" + qjDate.getDaysMonth())
				+ "' and b.pk_accsubj in ( select pk_corp_account from ynt_cpaccount where accountcode  like '"
				+ kmVO.getAccountcode()
				+ "%' and pk_corp='"
				+ kmVO.getPk_corp()
				+ "' )  and nvl(b.dr,0)=0  ";
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sql,null,
				new BeanListProcessor(KmZzVO.class));

		if (result != null && result.size() > 0) {

			KmZzVO kmzzVO = (KmZzVO) result.get(0);
			// 截止本期初本科目累计借方发生额
			DZFDouble jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO
					.getJf();
			// 截止本期初本科目累计贷方发生额
			DZFDouble df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO
					.getDf();

			if (0 == fx) {
				// 借方=期初+借方累计-贷方累计
				bqfseMny = qcye.add(jf).sub(df);
			} else {
				// 贷方=期初+贷方累计-借方累计
				bqfseMny = qcye.add(df).sub(jf);
			}

		}
		return bqfseMny;

	}

	/*
	 * Example: List sqhlist=[aa,bb,cc,dd,ee,ff,gg] ;
	 * Test.getSqlStrByList(sqhList,3,"SHENQINGH")= "SHENQING IN
	 * ('aa','bb','cc') OR SHENQINGH IN ('dd','ee','ff') OR SHENQINGH IN ('g
	 * 
	 * 把超过1000的in条件集合拆分成数量splitNum的多组sql的in 集合。
	 * 
	 * @param sqhList in条件的List
	 * 
	 * @param splitNum 拆分的间隔数目,例如： 1000
	 * 
	 * @param columnName SQL中引用的字段名例如： Z.SHENQINGH
	 * 
	 * @return
	 */
	public String getSqlStrByList(List sqhList, int splitNum, String columnName) {
		if (splitNum > 1000) // 因为数据库的列表sql限制，不能超过1000.
			return null;
		StringBuffer sql = new StringBuffer("");
		if (sqhList != null) {
			sql.append(" ").append(columnName).append(" IN ( ");
			for (int i = 0; i < sqhList.size(); i++) {
				sql.append("'").append(sqhList.get(i) + "',");
				if ((i + 1) % splitNum == 0 && (i + 1) < sqhList.size()) {
					sql.deleteCharAt(sql.length() - 1);
					sql.append(" ) OR ").append(columnName).append(" IN (");
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" )");
		}
		return sql.toString();
	}

	/*
	 * 把超过1000的in条件数组拆分成数量splitNum的多组sql的in 集合。
	 * 
	 * @param sqhArrays in条件的数组
	 * 
	 * @param splitNum 拆分的间隔数目,例如： 1000
	 * 
	 * @param columnName SQL中引用的字段名例如： Z.SHENQINGH
	 * 
	 * @return
	 */
	public String getSqlStrByArrays(String[] sqhArrays, int splitNum,
			String columnName) {
		return getSqlStrByList(Arrays.asList(sqhArrays), splitNum, columnName);
	}

	/**
	 * 根据币种查询汇率
	 */
//	public ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws BusinessException {
//		
//		BaseDAO dao = new BaseDAO();
//		List<ExrateVO> listres = (List<ExrateVO>) dao.retrieveByClause(ExrateVO.class, "pk_currency='"+pk_currency+"' and pk_corp ='"+pk_corp+"'  and (dr,0)=0");
//		return listres.toArray(new ExrateVO[0]);
//	}
	
	/**
	 * 根据币种查询汇率
	 */
	public ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws DZFWarpException {
		SQLParameter sp=	new SQLParameter();
		sp.addParam(pk_currency);
		sp.addParam(pk_corp);
		List<ExrateVO> listres = (List<ExrateVO>) singleObjectBO.retrieveByClause(ExrateVO.class, "pk_currency=? and pk_corp =? and nvl(dr,0)=0",sp);
		return listres.toArray(new ExrateVO[0]);
	}

	@Override
	public Map<String, YntCpaccountVO> querykm(String pk_corp)
			throws DZFWarpException {
		Map<String, YntCpaccountVO> resmap = new HashMap<String, YntCpaccountVO>();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" nvl(dr,0)=0 and nvl(isleaf,'N')='Y'");
		sql.append(" and pk_corp=?");
		YntCpaccountVO[] zckmVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, sql.toString(),sp);
		for (YntCpaccountVO zckmVO : zckmVOs) {
			resmap.put(zckmVO.getPk_corp_account(), zckmVO);
		}
		return resmap;
	}
	@Override
	public String createRulecodebyCorp(String bzaccountcode,String pk_corp) throws DZFWarpException {
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		ICpaccountCodeRuleService  gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
		String newaccount = gl_accountcoderule.getNewRuleCode(bzaccountcode, olerule, newrule);
		return newaccount;
	}
	@Override
	public String getCorpAccountByTradeAccountPk(String pk_trade_account, String pk_corp) throws DZFWarpException {
		BdTradeAccountVO jfkmVO = (BdTradeAccountVO) singleObjectBO.queryVOByID(pk_trade_account, BdTradeAccountVO.class);
		if (jfkmVO == null) {
			throw new BusinessException("行业会计科目主键为" + pk_trade_account
					+ "的科目已被删除，请检查");
		}
		SQLParameter sp=new SQLParameter();
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		ICpaccountCodeRuleService  gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
		String newaccount = gl_accountcoderule.getNewRuleCode(jfkmVO.getAccountcode(), olerule, newrule);
		sp.addParam(newaccount);
		sp.addParam(pk_corp);
		
		String condition = " accountcode=? and pk_corp=? and nvl(dr,0)=0 ";
		YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[]) singleObjectBO
				.queryByCondition(YntCpaccountVO.class,condition,sp);
		if (gsjfkmVOs == null || gsjfkmVOs.length < 1) {
			return null;
		}

		return gsjfkmVOs[0].getPrimaryKey();
	}
	@Override
	public String getMeasureCode(String pk_corp) throws DZFWarpException{
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("获取计量单位编码失败!");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql =" select code from ynt_measure where pk_corp=? and nvl(dr,0) = 0  ";
		List<MeasureVO> listbvo = (List<MeasureVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(MeasureVO.class));
		List<ICodeName> list= new ArrayList<ICodeName>();
		if(listbvo!=null && listbvo.size()>0){
			Collections.addAll(list, listbvo.toArray(new MeasureVO[0]));
		}
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}
	
	@Override
	public String getInventoryCode(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("获取存货编码失败!");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql =" select code from ynt_inventory where pk_corp=? and nvl(dr,0) = 0  ";
		List<InventoryVO> listbvo = (List<InventoryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(InventoryVO.class));
		List<ICodeName> list= new ArrayList<ICodeName>();
		if(listbvo!=null && listbvo.size()>0){
			Collections.addAll(list, listbvo.toArray(new InventoryVO[0]));
		}
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}
	
	@Override
	public String getFZHsCode(String pk_corp, String pk_auacount_h) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(pk_auacount_h)) {
			throw new BusinessException("获取辅助编码失败!");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_auacount_h);
		String sql =" select code from ynt_fzhs_b where pk_corp=? and pk_auacount_h= ? and nvl(dr,0) = 0  ";
		List<AuxiliaryAccountBVO> listbvo = (List<AuxiliaryAccountBVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(AuxiliaryAccountBVO.class));
		List<ICodeName> list= new ArrayList<ICodeName>();
		if(listbvo!=null && listbvo.size()>0){
			Collections.addAll(list, listbvo.toArray(new AuxiliaryAccountBVO[0]));
		}
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}
	
	private String getFinalcode(Long code){
		String str = "";
		if(code > 0 && code < 10){
			str = "00"+String.valueOf(code);
		}else if(code > 9 && code < 100){
			str = "0"+String.valueOf(code);
		}else{
			str = String.valueOf(code);
		}
		return str;
	}
	
	private Long getMaxCode(List<ICodeName> listbvo){
//		Collections.sort(listbvo, new Comparator<ICodeName>(){
//			@Override
//			public int compare(ICodeName t1, ICodeName t2) {
//				if(!StringUtil.isEmpty(t1.getCode()) && !StringUtil.isEmpty(t2.getCode())){
//					return t1.getCode().compareTo(t2.getCode());
//				}else if(StringUtil.isEmpty(t1.getCode()) && !StringUtil.isEmpty(t2.getCode())){
//					return -1;
//				}else if(!StringUtil.isEmpty(t1.getCode()) && StringUtil.isEmpty(t2.getCode())){
//					return 1;
//				}else{
//					return 0;
//				}
//			}
//			
//		});
		
		if(listbvo == null || listbvo.size()==0)
			return 1L;
		Long result = 1L;
		Long maxNum=1L;
		int size = listbvo.size();
		ICodeName bvo = null;
		for(int i =0;i<size;i++){
			bvo = listbvo.get(i);
			String code = bvo.getCode();
			if(StringUtil.isEmpty(code))
				continue;
			try{
				result = Long.parseLong(code.trim())+1;
				if(result>maxNum){
					maxNum = result;
				}
			}catch(Exception e){
				//吃掉异常
			}
		}
		return maxNum;
	}
	@Override
	public String getInvclCode(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("获取存货分类编码失败!");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql =" select code from ynt_invclassify where pk_corp=? and nvl(dr,0) = 0  ";
		List<MeasureVO> listbvo = (List<MeasureVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(MeasureVO.class));
		List<ICodeName> list= new ArrayList<ICodeName>();
		if(listbvo!=null && listbvo.size()>0){
			Collections.addAll(list, listbvo.toArray(new MeasureVO[0]));
		}
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}
	@Override
	public String getYhzhCode(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp)){
			throw new BusinessException("获取银行账户编码失败!");
		}
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = " select bankcode from ynt_bankaccount where pk_corp=? and nvl(dr,0) = 0 ";
		List<BankAccountVO> listbvo = (List<BankAccountVO>) singleObjectBO.executeQuery(sql, 
				sp, new BeanListProcessor(BankAccountVO.class));
		
		List<ICodeName> list = new ArrayList<ICodeName>();
		if(listbvo != null && listbvo.size() > 0){
			Collections.addAll(list, listbvo.toArray(new BankAccountVO[0]));
		}
		
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}
}
