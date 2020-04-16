package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCptranslrHVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.BdTradeLrBVO;
import com.dzf.zxkj.platform.model.sys.BdTradeLrHVO;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.ICptransLrService;
import com.dzf.zxkj.platform.service.jzcl.ITerminalSettle;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * 总账期末结账
 * 
 * @author zhangj
 */
@Service("gl_qmjzserv")
public class TerminalSettleImpl implements ITerminalSettle {

	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;

	private IVoucherService gl_tzpzserv;
	private ICptransLrService gl_cplrmbserv;
	private SingleObjectBO singleObjectBO = null;
	private IYntBoPubUtil gslx;
	private ICpaccountService cpaccountserv;
	private ICpaccountCodeRuleService cpaccountcoderuleserv;
	private IYntBoPubUtil yntBoPubUtil = null;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;

	public IYntBoPubUtil getYntBoPubUtil() {
		return yntBoPubUtil;
	}
	@Autowired
	public void setCpaccountserv(ICpaccountService cpaccountserv) {
		this.cpaccountserv = cpaccountserv;
	}
	@Autowired
	public void setCpaccountcoderuleserv(ICpaccountCodeRuleService cpaccountcoderuleserv) {
		this.cpaccountcoderuleserv = cpaccountcoderuleserv;
	}

	@Autowired
	public void setYntBoPubUtil(IYntBoPubUtil yntBoPubUtil) {
		this.yntBoPubUtil = yntBoPubUtil;
	}
	
	public ICptransLrService getGl_cplrmbserv() {
		return gl_cplrmbserv;
	}
	@Autowired
	public void setGl_cplrmbserv(ICptransLrService gl_cplrmbserv) {
		this.gl_cplrmbserv = gl_cplrmbserv;
	}
	public IYntBoPubUtil getGslx() {
		return gslx;
	}
	@Autowired
	public void setGslx(IYntBoPubUtil gslx) {
		this.gslx = gslx;
	}

	public IVoucherService getGl_tzpzserv() {
		return gl_tzpzserv;
	}
	@Autowired
	public void setGl_tzpzserv(IVoucherService gl_tzpzserv) {
		this.gl_tzpzserv = gl_tzpzserv;
	}
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 结账检查
	 */
	public QmJzVO[] updatecheckTerminalSettleData(QmJzVO[] vos) throws DZFWarpException {
		if (vos == null || vos.length < 1) {
			throw new BusinessException("没有需要结账检查的数据");
		}
		String pk_corp = null;
		String period = null;
		String beginperiod = null;
		String times = null;
		boolean hasjz = false;

		for (QmJzVO vo : vos) {
			pk_corp = vo.getPk_corp();
			period = vo.getPeriod();
			beginperiod = period.substring(0, 4) + "-01";
			times = period.substring(0, 4);
			hasjz = false;
			SQLParameter params = new SQLParameter();
			String sql = null;
			sql = "select 1 from YNT_GDZCJZ where  substr(period,0,4) = ? and pk_corp= ? and nvl(dr,0)=0 and nvl(jzfinish,'N')='N' ";
			params.addParam(times);
			params.addParam(pk_corp);
			hasjz = isExists(pk_corp, sql, params);
			hasjz = hasjz ? false : true;
			vo.setGdzchasjz(new DZFBoolean(hasjz));
			/** 2、检查当前公司、期间是否固定资产结账 */
			sql = "select 1 from YNT_QMCL where  period= ? and pk_corp= ? and nvl(dr,0)=0 and nvl(isqjsyjz,'N')='Y' ";
			SQLParameter paramss = new SQLParameter();
			paramss.addParam(period);
			paramss.addParam(pk_corp);
			hasjz = isExists(pk_corp, sql, paramss);
			vo.setSykmwye(new DZFBoolean(hasjz));
			/** 先判断期末结转是否损益结转,如果期末结转未损益结转，则不允许总账期末结账做结账检查业务。 */
			sql = "select 1 from YNT_TZPZ_H where doperatedate >= ? and doperatedate<= ? and pk_corp= ? and nvl(dr,0)=0 ";
			sql = sql + " and nvl(ishasjz,'N')='N'";
			SQLParameter paramsss = new SQLParameter();
			paramsss.addParam((DateUtils.getPeriodStartDate(beginperiod)));
			paramsss.addParam((DateUtils.getPeriodEndDate(period)));
			paramsss.addParam(pk_corp);
			hasjz = isExists(pk_corp, sql, paramsss);
			vo.setPzhasjz(new DZFBoolean(hasjz == false));
			/** 4、检查当前公司、期间的期末是否试算平衡 */
			/** 查询期初余额 */
			sql = "select sum(jfmny)-sum(dfmny) from YNT_TZPZ_H where doperatedate >= ? and doperatedate<=? and pk_corp=? and nvl(dr,0)=0 ";
			SQLParameter parama = new SQLParameter();
			parama.addParam((DateUtils.getPeriodStartDate(beginperiod)));
			parama.addParam((DateUtils.getPeriodEndDate(period)));
			parama.addParam(pk_corp);
			Object[] value = (Object[]) singleObjectBO.executeQuery(sql, parama, new ArrayProcessor());
			if (value != null && value.length > 0 && value[0] != null) {
				DZFDouble d = new DZFDouble(Double.parseDouble(value[0].toString()));
				vo.setQmph(0 == d.doubleValue() ? DZFBoolean.TRUE : DZFBoolean.FALSE);
			} else {/** 如果发生额未空，则试算平衡 */
				/** 需要查询期末余额对应期间是否做损益结转 ly */
				sql = "select isqjsyjz from YNT_QMCL where period=? and pk_corp= ? and nvl(dr,0)=0 ";
				SQLParameter sparam = new SQLParameter();
				sparam.addParam(period);
				sparam.addParam(pk_corp);
				value = (Object[]) singleObjectBO.executeQuery(sql, sparam, new ArrayProcessor());
				if (value != null && value.length > 0 && value[0] != null) {
					if (DZFBoolean.valueOf(value[0].toString()).booleanValue()) {
						vo.setQmph(DZFBoolean.TRUE);
					} else {
						vo.setQmph(DZFBoolean.FALSE);
					}
				} else {
					vo.setQmph(DZFBoolean.FALSE);
				}
			}
			/** 保存检查结果到数据库 */
			if (StringUtil.isEmptyWithTrim(vo.getPrimaryKey())) {
				String pk = singleObjectBO.saveObject(vo.getPk_corp(), vo).getPrimaryKey();
				vo.setPrimaryKey(pk);
			} else {
				singleObjectBO.update(vo);
			}
			String sqlcorp = "select nvl(holdflag,'N') from bd_corp where pk_corp= ? and nvl(dr,0)=0";
			SQLParameter param = new SQLParameter();
			param.addParam(pk_corp);
			String holdflag = (String) singleObjectBO.executeQuery(sqlcorp, param, new ColumnProcessor());
			if (holdflag.equals("N")) {
				vo.setHoldflag(DZFBoolean.FALSE);
			} else {
				vo.setHoldflag(DZFBoolean.TRUE);
			}

		}
		return vos;
	}
	
	public boolean isExistsJZ(String pk_corp, DZFDate ufd) throws DZFWarpException {
		String period = String.valueOf(ufd.getMonth());
		period =DateUtils.getPeriod(ufd);
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(period);
		return isExists(pk_corp, "select 1 from YNT_QMJZ where pk_corp= ? and period>= ? and nvl(dr,0)=0 ",params);

	}

	/**
	 * 查询公司科目
	 */
	public HashMap<String, YntCpaccountVO[]> queryCorpAccountVOS(QmJzVO[] vos)
			throws DZFWarpException {
		HashMap<String, YntCpaccountVO[]> map = new HashMap<String, YntCpaccountVO[]>();
		for (QmJzVO qmjzvo : vos) {
			if (map.containsKey(qmjzvo.getPk_corp())) {
				continue;
			}
			YntCpaccountVO[] kmVOs = accountService.queryByPk(qmjzvo.getPk_corp());
			map.put(qmjzvo.getPk_corp(), kmVOs);
		}
		return map;
	}

	/**
	 * 查科目期末
	 * 
	 * @param qmjzvo
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, KMQMJZVO> queryKmqmjzVO(QmJzVO qmjzvo) throws DZFWarpException {
		HashMap<String, KMQMJZVO> map = new HashMap<String, KMQMJZVO>();
		StringBuffer sb = new StringBuffer();
		SQLParameter sparam = new SQLParameter();
		sb.append(" pk_corp=? ");
		sb.append(" and period=? ");
		sb.append(" and nvl(dr,0)=0 ");
		sparam.addParam(qmjzvo.getPk_corp());
		sparam.addParam(qmjzvo.getPeriod());
		KMQMJZVO[] dbKmqmVOs = (KMQMJZVO[]) singleObjectBO.queryByCondition(
				KMQMJZVO.class, sb.toString(), sparam);
		if (dbKmqmVOs != null && dbKmqmVOs.length > 0) {
			for (KMQMJZVO dbKmqmVO : dbKmqmVOs) {
				map.put(dbKmqmVO.getPk_accsubj(), dbKmqmVO);
			}
		}
		return map;
	}

	/**
	 * 查询起初
	 * 
	 * @param qmjzvo
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, QcYeVO> queryKmqcyeVO(QmJzVO qmjzvo) throws DZFWarpException {
		HashMap<String, QcYeVO> map = new HashMap<String, QcYeVO>();
		SQLParameter sparam = new SQLParameter();
		sparam.addParam(qmjzvo.getPk_corp());
		QcYeVO[] qcyeVOs = (QcYeVO[]) singleObjectBO.queryByCondition(QcYeVO.class, " pk_corp= ? and nvl(dr,0)=0 ", sparam);
		if (qcyeVOs != null && qcyeVOs.length > 0) {
			for (QcYeVO qcyevo : qcyeVOs) {
				map.put(qcyevo.getPk_accsubj(), qcyevo); 
			}
		}
		return map;
	}

	/**
	 * 查询科目发生汇总
	 * 
	 * @param qmjzvo
	 * @param qjDate
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, KmZzVO> queryKmzzVO(QmJzVO qmjzvo, DZFDate qjDate) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sparam = new SQLParameter();
		sql.append(" select sum(b.jfmny) as jf,sum(b.dfmny) as df , b.pk_accsubj ");
		sql.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sql.append(" where h.doperatedate <= ? ");
		sql.append(" and h.pk_corp= ? ");
		sql.append(" and nvl(b.dr,0)=0 ");
		sql.append(" group by b.pk_accsubj");
		
		sparam.addParam(qmjzvo.getPeriod() + "-"+ qjDate.getDaysMonth());
		sparam.addParam(qmjzvo.getPk_corp());
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sql
				.toString(), sparam, new BeanListProcessor(
				KmZzVO.class));
		HashMap<String, KmZzVO> map = new HashMap<String, KmZzVO>();
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				KmZzVO kmzzVO = (KmZzVO) result.get(i);
				map.put(kmzzVO.getPk_accsubj(), kmzzVO);
			}
		}
		return map;
	}

	/**
	 * 查询科目发生汇总
	 * 
	 * @param qmjzvo
	 * @param qjDate
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, KmZzVO> queryKmzzVO2(QmJzVO qmjzvo, DZFDate qjDate) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sparam = new SQLParameter();
		sql.append(" select sum(b.jfmny) as jf,sum(b.dfmny) as df  ,b.pk_accsubj ");
		sql.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sql.append(" where substr(h.doperatedate,1,7) = ? ");
		sql.append(" and h.pk_corp= ? and nvl(b.dr,0)=0 ");
		sql.append(" group by b.pk_accsubj");
		sparam.addParam(qmjzvo.getPeriod());
		sparam.addParam(qmjzvo.getPk_corp());
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sql
				.toString(), sparam , new BeanListProcessor(
				KmZzVO.class));
		HashMap<String, KmZzVO> map = new HashMap<String, KmZzVO>();
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				KmZzVO kmzzVO = (KmZzVO) result.get(i);
				map.put(kmzzVO.getPk_accsubj(), kmzzVO);
			}
		}
		return map;
	}

	/**
	 * 结账，保存期末结账、并存入期末、期初数
	 */
	public QmJzVO[] saveTerminalSettleData(QmJzVO[] vos,DZFDate logdate,String userid) throws DZFWarpException {
		if (vos == null || vos.length < 1) {
			throw new BusinessException("没有需要结账检查的数据");
		}
		
		//结账校验
		checkSettleDate(vos,logdate);

		HashMap<String, YntCpaccountVO[]> map = queryCorpAccountVOS(vos);

		for (QmJzVO qmjzvo : vos) {
			/**  插入结账表，勾选结账标识 */
			String pk_qmjz = "";
			if (StringUtil.isEmptyWithTrim(qmjzvo.getPrimaryKey())) {
				qmjzvo.setJzfinish(DZFBoolean.TRUE);
				pk_qmjz = singleObjectBO.saveObject(qmjzvo.getPk_corp(), qmjzvo).getPrimaryKey();
			} else {
				pk_qmjz = qmjzvo.getPrimaryKey();
				qmjzvo.setJzfinish(DZFBoolean.TRUE);
				singleObjectBO.update(qmjzvo);
			}
			HashMap<String, KMQMJZVO> mapKmqmjz = queryKmqmjzVO(qmjzvo);

			DZFDate qjDate = DZFDate.getDate(qmjzvo.getPeriod() + "-01");

			HashMap<String, KmZzVO> mapKmzz = queryKmzzVO(qmjzvo, qjDate);

			HashMap<String, KmZzVO> mapKmzz2 = queryKmzzVO2(qmjzvo, qjDate);

			HashMap<String, QcYeVO> mapQcye = queryKmqcyeVO(qmjzvo);

			/**  查找所有科目 */
			YntCpaccountVO[] kmVOs = map.get(qmjzvo.getPk_corp());
			if (kmVOs != null && kmVOs.length > 0) {

				for (YntCpaccountVO kmVO : kmVOs) {
					Integer fx = kmVO.getDirection() == null ? 0 : kmVO
							.getDirection();

					/** 查询本科目、本期间的期末、期初表 */
					KMQMJZVO dbKmqmVO = mapKmqmjz
							.get(kmVO.getPk_corp_account());

					if (dbKmqmVO != null) {
						/** 已经存在期初数，只需要写入期末数、及本期累计发生 */
						dbKmqmVO.setPk_qmjz(pk_qmjz); 

						DZFDouble jf = DZFDouble.ZERO_DBL;
						DZFDouble df = DZFDouble.ZERO_DBL;
						if (mapKmzz2 != null && mapKmzz2.size() > 0) {

							KmZzVO kmzzVO = (KmZzVO) mapKmzz2.get(kmVO
									.getPrimaryKey());
							if (kmzzVO != null) {
								/** 本期本科目累计借方发生额 */
								jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL
										: kmzzVO.getJf();
								/** 本期本科目累计贷方发生额 */
								df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL
										: kmzzVO.getDf();
							}
						}

						/** 本期借方发生累计 */
						dbKmqmVO.setJffse(jf);
						/** 本期贷方发生累计 */
						dbKmqmVO.setDffse(df);
						/** 结账日期 */
						dbKmqmVO.setCoperatorid(userid);
						dbKmqmVO.setDoperatedate(qmjzvo.getDoperatedate());
						/** 余额 */
						DZFDouble ye = DZFDouble.ZERO_DBL;
						DZFDouble thismonthqc = dbKmqmVO.getThismonthqc() == null ? DZFDouble.ZERO_DBL
								: dbKmqmVO.getThismonthqc();
						if (0 == fx.intValue()) {
							/** 借方 */
							ye = jf.sub(df);
							dbKmqmVO.setThismonthqm(thismonthqc.add(ye));
						} else {
							/** 贷方 */
							ye = df.sub(jf);
							dbKmqmVO.setThismonthqm(thismonthqc.add(ye));
						}

					} else {
						/** 本科目没有结过账的 */
						/** 科目的期初、期末表 */
						dbKmqmVO = new KMQMJZVO();
						dbKmqmVO.setPk_qmjz(pk_qmjz);// 记录期末结账的PK
						dbKmqmVO.setPk_corp(qmjzvo.getPk_corp());
						/** 科目主键 */
						dbKmqmVO.setPk_accsubj(kmVO.getPrimaryKey());
						/** 结账日期 */
						dbKmqmVO.setDoperatedate(qmjzvo.getDoperatedate());
						/** 结账人 */
						dbKmqmVO.setCoperatorid(userid);
						/** 期间 */
						dbKmqmVO.setPeriod(qmjzvo.getPeriod());

						DZFDouble jf = DZFDouble.ZERO_DBL;
						DZFDouble df = DZFDouble.ZERO_DBL;
						if (map != null && map.size() > 0) {
							KmZzVO kmzzVO = (KmZzVO) mapKmzz.get(kmVO
									.getPrimaryKey());
							if (kmzzVO != null) {
								/** 本期本科目累计借方发生额 */
								jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL: kmzzVO.getJf();
								/** 本期本科目累计贷方发生额 */
								df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL: kmzzVO.getDf();
							}
						}
						/** 本期借方发生累计 */
						dbKmqmVO.setJffse(jf);
						/** 本期贷方发生累计 */
						dbKmqmVO.setDffse(df);

						/** 期初数 */
						QcYeVO qcyeVO = mapQcye.get(kmVO.getPrimaryKey());

						DZFDouble qcyeMny = DZFDouble.ZERO_DBL;
						if (qcyeVO != null) {
							qcyeMny = qcyeVO.getThismonthqc() == null ? DZFDouble.ZERO_DBL: qcyeVO.getThismonthqc();
						}
						dbKmqmVO.setThismonthqc(qcyeMny);

						/** 期末数据 */
						DZFDouble ye = DZFDouble.ZERO_DBL;
						if (0 == fx.intValue()) {
							/** 借方期末余额=期初+借方-贷方 */
							ye = qcyeMny.add(jf).sub(df);
							dbKmqmVO.setThismonthqm(ye);
						} else {
							/** 贷方期末余额=期初+贷方—贷方 */
							ye = qcyeMny.add(df).sub(jf);
							dbKmqmVO.setThismonthqm(ye);
						}

					}

					if (dbKmqmVO != null) {
						/** 保存本期期末数据 */
						if (StringUtil.isEmptyWithTrim(dbKmqmVO.getPrimaryKey())) {
							/** 新增 */
							singleObjectBO.saveObject(dbKmqmVO.getPk_corp(), dbKmqmVO);
						} else {
							/** 修改 */
							singleObjectBO.update(dbKmqmVO);
						}

						/** 下一步，写下一期的期初 */
						KMQMJZVO nextKmqmjzVO = new KMQMJZVO();
						nextKmqmjzVO.setPk_qmjz(pk_qmjz);// 记录期末结账的PK
						nextKmqmjzVO.setPk_corp(qmjzvo.getPk_corp());
						/** 科目主键 */
						nextKmqmjzVO.setPk_accsubj(kmVO.getPrimaryKey());
						/** 结账日期 */
						nextKmqmjzVO.setDoperatedate(qmjzvo.getDoperatedate());
						/** 结账人 */
						nextKmqmjzVO.setCoperatorid(userid);
						/** 期间,为下一期间 */
						DZFDate nextDate = new DZFDate(qmjzvo.getPeriod()
								+ "-01");
						String nextPeriod = "";
						if (nextDate.getMonth() == 12) {
							nextDate = DZFDate.getDate((nextDate.getYear() + 1)
									+ "-01-01");
							nextPeriod = (nextDate.getYear() + 1) + "-01";
						} else {
							int nextMonth = nextDate.getMonth() + 1;
							String nextMonthStr = nextMonth < 10 ? "0"
									+ nextMonth : nextMonth + "";
							nextPeriod = (nextDate.getYear()) + "-"
									+ nextMonthStr;
						}

						nextKmqmjzVO.setPeriod(nextPeriod);
						/** 期初数= 上期的期末数 */
						nextKmqmjzVO.setThismonthqc(dbKmqmVO.getThismonthqm());

						/** 保存下一期的期初 */
						singleObjectBO.saveObject(nextKmqmjzVO.getPk_corp(), nextKmqmjzVO);
					}

				}
			}
		}

		return vos;
	}

	private void checkSettleDate(QmJzVO[] vos,DZFDate logdate) throws DZFWarpException{
		if(vos==null||vos.length<1){
			throw new BusinessException("请选择要结账的数据");
		}
		DZFDate currdate = logdate;
		Integer year = currdate.getYear();
		Integer month = currdate.getMonth();
		for(QmJzVO qmjz:vos){
		}
		
		HashMap<String, CorpVO> mapCorp = new HashMap<String, CorpVO>();
		String tips = "" ;
		for (QmJzVO jzvo : vos) {
			String tipstemp ="";
			CorpVO corpVO = null;
			/** 判断当前公司是否需要启用固定资产 */
			if (mapCorp.containsKey(jzvo.getPk_corp())) {
				corpVO = mapCorp.get(jzvo.getPk_corp());
			} else {
				corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, jzvo.getPk_corp());
				mapCorp.put(corpVO.getPk_corp(), corpVO);
			}
			Integer yeartemp =Integer.parseInt(jzvo.getPeriod().substring(0, 4));
			if(yeartemp.intValue()>year.intValue()){
				tipstemp +=  "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【当前登录日期小于结账日期，不能结账】 ";
			}
			if(year.intValue()==yeartemp.intValue() &&  month<12){
				if (!tipstemp.equals("")) {
					tipstemp +=  "【最后一月才能结账】 ";
				}else{
					tipstemp += "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【最后一月才能结账】 ";
				}
			}
			
			if (jzvo.getPzhasjz() == null || !jzvo.getPzhasjz().booleanValue()) {
				if (!tipstemp.equals("")) {
					tipstemp += "【凭证记账检查未通过】 ";
				}else{
					tipstemp += "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【凭证记账检查未通过】 ";
				}
			}
			if (corpVO == null) {
				throw new BusinessException("公司主键为" + jzvo.getPk_corp() + "的公司已被删除");
			}
			if (jzvo.getSykmwye() == null || !jzvo.getSykmwye().booleanValue()) {
				if (!tipstemp.equals("")) {
					tipstemp += "【损益期间结转检查未通过】 ";
				} else {
					tipstemp += "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【损益期间结转检查未通过】 ";
				}
			}
			if (jzvo.getQmph() == null || !jzvo.getQmph().booleanValue()) {
				if (!tipstemp.equals("")) {
					tipstemp += "【期末试算平衡检查未通过】";
				} else {
					tipstemp += "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【期末试算平衡检查未通过】 ";
				}
			}
			if (jzvo.getJzfinish() != null && jzvo.getJzfinish().booleanValue()) {
				if (!tipstemp.equals("")) {
					tipstemp += "【凭证已结账】";
				} else {
					tipstemp += "公司："+ SecretCodeUtils.deCode(corpVO.getUnitname())  + "，所在期间:"+jzvo.getPeriod()+"【凭证已结账】,不能重复结账";
				}
			}

			if (!tipstemp.equals("")) {
				tips += tipstemp;
			}
		}
		
		/** 提示错误信息*/
		if(!"".equals(tips)){
			throw new BusinessException(tips);
		}
	}

	/**
	 * 结账按照凭证进行结账，支持多币种
	 */
	public QmJzVO[] saveTerminalSettleDataFromPZ(QmJzVO[] vos,DZFDate logdate,String userid)
			throws DZFWarpException {
		
		/** 先结账检查 */
		QmJzVO[] vostemp = updatecheckTerminalSettleData(vos);
		
		/** 结账校验 */
		checkSettleDate(vostemp, logdate);
		
		
		return new TerminalCurrSettleDMO(singleObjectBO).saveTerminalSettleDataFromPZ(vostemp,logdate,userid,null);
	}

	/**
	 * 反结账，如果下一期已结账，则不能删除，如果下一期未结账，则可以删除期末结账，并删除已存入期末、期初数
	 */
	public QmJzVO[] updatecancelTerminalSettleData(QmJzVO[] vos)
			throws DZFWarpException {
		if (vos == null || vos.length < 1) {
			throw new BusinessException("没有需要结账检查的数据");
		}
		
		String tips = "" ;
		for(QmJzVO jzvo:vos){
				if(jzvo.getJzfinish()==null||!jzvo.getJzfinish().booleanValue()){
					tips += "当前期间"+jzvo.getPeriod()+"【尚未结账】" ;
				}
//
//				if(!tips.equals("")){
//					tips += "\n" ;
//				}
				
		}
		
		/** 提示错误信息 */
		if(!"".equals(tips)){
			throw new BusinessException(tips);
		}
		
		CorpVO cpvo = null;
		for (QmJzVO qmjzvo : vos) {
			cpvo = corpService.queryByPk(qmjzvo.getPk_corp());
			if(cpvo == null){
				throw new BusinessException("公司不存在");
			}
			if(cpvo.getBegindate() == null){
				throw new BusinessException("公司尚未建账");
			}
			String pk_qmjz = qmjzvo.getPrimaryKey();

			/** 查找下一期，判断是否已经结账 */
			DZFDate periodDate = DZFDate.getDate(qmjzvo.getPeriod() + "-01");
			String nextPeriod = "";
			nextPeriod = (periodDate.getYear() + 1) + "-12";
			SQLParameter sparam = new SQLParameter();
			sparam.addParam(qmjzvo.getPk_corp());
			sparam.addParam(nextPeriod);
			QmJzVO[] nextQmjzVOs = (QmJzVO[]) singleObjectBO.queryByCondition(
					QmJzVO.class, " pk_corp= ? and period= ? and nvl(dr,0)=0 ", sparam);
			if (nextQmjzVOs != null && nextQmjzVOs.length > 0) {
				for (QmJzVO nextQmjzVO : nextQmjzVOs) {
					if (nextQmjzVO.getJzfinish() != null && nextQmjzVO.getJzfinish().booleanValue()) {
						throw new BusinessException("下一期【" + nextPeriod+ "】已经结账，当前不能反结账");
					}
				}
			}

			/** 下一期没有结账，则可以反结账 */
			/** 1、删除本期期末数、及删除下一期期初数 */
			/** 先查再删除 */
			SQLParameter sparamg = new SQLParameter();
			sparamg.addParam(qmjzvo.getPk_corp());
			sparamg.addParam(periodDate.getYear() + 1 + "-12");
			/** 删除上一年的 */
			KMQMJZVO[] kmqmzjvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class, " pk_corp= ? and nvl(dr,0)=0 and period = ? ", sparamg);
			singleObjectBO.deleteVOArray(kmqmzjvos);
			/** 修改本年的 */
			sparamg.clearParams();
			sparamg.addParam(qmjzvo.getPk_corp());
			sparamg.addParam(periodDate.getYear() + "-12");
			KMQMJZVO[] nextkmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class, " pk_corp= ? and nvl(dr,0)=0 and period = ? ", sparamg);
			if(periodDate.getYear() == cpvo.getBegindate().getYear()){
				/** 如果是当年的则删除，为了计算期初 */
				singleObjectBO.deleteVOArray(nextkmqmjzvos);
			}else{
				for(KMQMJZVO  nextKmqmjzVO:nextkmqmjzvos){
					nextKmqmjzVO.setJffse(null);
					nextKmqmjzVO.setYbjfmny(null);
					nextKmqmjzVO.setDffse(null);
					nextKmqmjzVO.setYbdfmny(null);
					nextKmqmjzVO.setThismonthqm(null);
					nextKmqmjzVO.setYbthismonthqm(null);
				}
				singleObjectBO.updateAry(nextkmqmjzvos, new String[]{"pk_kmqmjz","jffse","ybjfmny","dffse","ybdfmny","thismonthqm","ybthismonthqm"});
			}
			/**  2、反结账 */
			if (!StringUtil.isEmptyWithTrim(pk_qmjz)) {
				qmjzvo.setJzfinish(DZFBoolean.FALSE);
				singleObjectBO.update(qmjzvo);
			}

		}
		return vos;
	}

	public boolean isExists(String pk_corp, String sql,SQLParameter params) throws DZFWarpException {
		return singleObjectBO.isExists(pk_corp, sql, params );
	}

	@Override
	public QmJzVO[] initQueryQmJzVO(QueryParamVO queryvo) throws DZFWarpException {
		/** 根据公司+日期 获取对应的期末JZVO */
		List<String> pk_corps = queryvo.getCorpslist();
		
		if (pk_corps == null || pk_corps.size() < 1) {
			throw new BusinessException("公司不能为空");
		}
		/** 根据公司查询对应的公司主键 */
		String period = DateUtils.getPeriod(queryvo.getBegindate1());
		DZFDate dateq =  DateUtils.getPeriodEndDate(period);//取一个月的最后一天
		DZFDate datez =DateUtils.getPeriodEndDate(DateUtils.getPeriod(queryvo.getEnddate()));// queryvo.getEnddate();

		if (dateq == null) {
			throw new BusinessException("期间起不能为空");
		}
		if(dateq.after(datez)){
			throw new BusinessException("查询开始日期，应该在查询结束日期前!");
		}

		/** 如果数据没存储自动存储 */
		List<QmJzVO> reslist;
		Vector<QmJzVO> vec = new Vector<QmJzVO>();
		/** 跨年 ---差多少年 */
		int chaYear = datez.getYear() - dateq.getYear();
		for (int i = 0; i <=chaYear; i++) {
			for (String pk_corp : pk_corps) {
				QmJzVO qmclVO = new QmJzVO();
				qmclVO.setPk_corp(pk_corp);
				qmclVO.setCoperatorid(queryvo.getUserid());
				qmclVO.setDoperatedate(queryvo.getClientdate());
				qmclVO.setPeriod((dateq.getYear() + (i)) + "-" + "12");
				/** 查询公司、期间是已经做过期末结账 */
				/** 检查期间起不能在公司建账日期之前 */
				CorpVO corpVO = corpService.queryByPk(pk_corp);
				if (corpVO == null) {
					throw new BusinessException("该公司不存在");
				}
				if (corpVO.getBegindate() == null) {
					throw new BusinessException("公司:'" + corpVO.getUnitname() + "'的建账日期为空，可能尚未建账，请检查!");
				}
				if (dateq.before(corpVO.getBegindate())) {
					throw new BusinessException("期间起不能在所选公司:'" + corpVO.getUnitname() + "'的建账日期之前");
				}
				SQLParameter sparam = new SQLParameter();
				sparam.addParam(pk_corp);
				sparam.addParam(qmclVO.getPeriod());
				QmJzVO[] vos = (QmJzVO[]) singleObjectBO.queryByCondition(QmJzVO.class,
						" pk_corp= ? and period= ? and nvl(dr,0)=0 ", sparam);
				if (vos != null && vos.length > 0) {
					qmclVO = vos[0];
				}
				qmclVO.setCorpname(corpVO.getUnitname());
				vec.add(qmclVO);
			}
		}
		
		reslist = new ArrayList<QmJzVO>();
		for(QmJzVO qmclvo : vec){
			if(StringUtil.isEmpty(qmclvo.getPrimaryKey())){
				//自动保存
				QmJzVO votemp =  (QmJzVO) singleObjectBO.saveObject(qmclvo.getPk_corp(), qmclvo);
				reslist.add(votemp);
			}else{
				reslist.add(qmclvo);
			}
		}
//		List<QmJzVO> jzvos = new ArrayList<QmJzVO>();//存储需要更改的vo
//		for(QmJzVO jzvo:reslist){
//			if(!isExistPzVO(jzvo)){
//				jzvo.setVdef10("0");//如果利润结转没有生成凭证，就设状态为未结转（防止手动删除凭证后，结转状态不跟着变）
//				jzvos.add(jzvo);
//			}
//		}
//		if(jzvos!=null&&jzvos.size()>0){
//			singleObjectBO.updateAry(jzvos.toArray(new QmJzVO[jzvos.size()]));
//		}
		return reslist.toArray(new QmJzVO[0]);
	}
	/**
	 * 利润结转
	 */
	@Override
	public void updateProfitJz(String pk_corp, DZFDate period,QmJzVO qmvo,DZFBoolean b ,String userid)
			throws DZFWarpException {
		
		if(qmvo.getJzfinish()!=null && qmvo.getJzfinish().booleanValue()){
			throw new BusinessException("公司已经年结,不能结转!");
		}
		if(queryTzpzHvo(pk_corp,period)){
			throw new BusinessException("已经结转，请勿重复操作！");
		}
		boolean isqjsyjz = checkIssyjz(period.toString(), pk_corp);
		if(!isqjsyjz){
			throw new BusinessException("年底未损益结转！");
		}
		FseJyeVO[] fsyeVos = getFseVO(pk_corp, period);
		DZFDouble jf = DZFDouble.ZERO_DBL;
		String lrbm,wfplr,zyr,zyc=null;
		List<YntCptranslrHVO> list = gl_cplrmbserv.query(pk_corp,false);
		if(list==null||list.size()==0){
			BdTradeLrHVO[] hvos =getLrHvo(pk_corp);
			if(hvos==null||hvos.length==0){
				throw new BusinessException("利润结转模版不存在");
			}
			BdTradeLrHVO hvo=hvos[0];
			
			if (hvo != null) {
				BdTradeLrBVO[] bvos = getLrbvo(hvo);
				if (bvos == null || bvos.length == 0) {
					throw new BusinessException("利润结转模版子表不存在");
				}
				zyr = hvo.getAbstracts();
				String oldrule = cpaccountserv.queryAccountRule(IDefaultValue.DefaultGroup);
				String newrule = cpaccountserv.queryAccountRule(pk_corp);
				wfplr = cpaccountcoderuleserv.getNewRuleCode(hvo.getAccountcode(), oldrule, newrule);
				
				BdTradeLrBVO bvo = bvos[0];
				zyc = bvo.getAbstracts();
				lrbm = cpaccountcoderuleserv.getNewRuleCode(bvo.getAccountcode(), oldrule, newrule);
			}else{
				throw new BusinessException("利润结转模版不存在");
			}
		}else{
			zyr=list.get(0).getAbstracts();
			zyc = list.get(0).getAbstracts1();
			lrbm=list.get(0).getVcode();//不同行业科目的利润科目编码
//			wfplr=list.get(0).getAccountcode();//不同行业科目的未分配利润科目编码
			YntCpaccountVO yntCpaccountVO = cpaccountserv.queryById(list.get(0).getPk_transferinaccount());
			wfplr = yntCpaccountVO.getAccountcode();
		}
		if(lrbm==null||wfplr==null){
			throw new BusinessException("利润结转模版不存在！");
		}
		Vector<TzpzBVO> vec_zckms = getPzBVO(fsyeVos, pk_corp, lrbm, jf, zyr, zyc, wfplr);
		if(vec_zckms!=null && vec_zckms.size()>0){
			TzpzBVO[] pzBodyVOs = null ;
			/**  凭证表体VOs */
			pzBodyVOs = new TzpzBVO[vec_zckms.size()+1] ;
			pzBodyVOs = vec_zckms.toArray(new TzpzBVO[0]);
			/** 凭证表头VO */
			TzpzHVO pzHeadVO = getTzpzHvo(qmvo,pzBodyVOs,period,userid);
			for(TzpzBVO bvo:pzBodyVOs){
				bvo.setPk_corp(pzHeadVO.getPk_corp());
			}
			pzHeadVO.setChildren(pzBodyVOs);
			pzHeadVO.setIsqxsy(b);
			CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pzHeadVO.getPk_corp());
			gl_tzpzserv.saveVoucher(corpvo, pzHeadVO);
		}
		singleObjectBO.update(qmvo);
	}
	
	/**
	 * 跟据模版从发生额及余额表以及科目表 里查询数据组装凭证
	 * @param fsyeVos
	 * @param pk_corp
	 * @param lrbm
	 * @param jf
	 * @param zyr
	 * @param zyc
	 * @param wfplr
	 * @return
	 */
	private Vector<TzpzBVO> getPzBVO(FseJyeVO[] fsyeVos, String pk_corp,String lrbm,DZFDouble jf,String zyr,String zyc,String wfplr ){
		Vector<TzpzBVO> vec_zckms = new Vector<TzpzBVO>() ; 
		for(FseJyeVO vo :fsyeVos){
			if(vo.getKmbm().equals(lrbm)){
				jf=vo.getQmdf()==null?DZFDouble.ZERO_DBL:vo.getQmdf();
				TzpzBVO pzbvo_jf = new TzpzBVO();
				pzbvo_jf.setPk_accsubj(vo.getPk_km());
				pzbvo_jf.setVcode(vo.getKmbm());
				pzbvo_jf.setVname(vo.getKmmc());
				if (jf.doubleValue() > 0) {
					pzbvo_jf.setJfmny(jf);
					pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
				} else {
					pzbvo_jf.setJfmny(DZFDouble.ZERO_DBL);
					pzbvo_jf.setDfmny(jf.multiply(-1));
				}
				pzbvo_jf.setZy(zyr);
				/** 币种，默认人民币 */
				pzbvo_jf.setPk_currency(yntBoPubUtil.getCNYPk()) ;
				if(jf.doubleValue()!=0){
					vec_zckms.add(pzbvo_jf) ;
				}
			}
		}
		/** 录入贷方未分配利润 */
		String dfpk_account="";
		TzpzBVO pzbvo_Df = new TzpzBVO();
		YntCpaccountVO[] listupvo = accountService.queryByPk(pk_corp, 3);
		for(YntCpaccountVO km :listupvo){
			if(km.getAccountcode().startsWith(wfplr) && km.getIsleaf().booleanValue()){
				dfpk_account=km.getPk_corp_account();
				pzbvo_Df.setPk_accsubj(dfpk_account);
				pzbvo_Df.setVcode(km.getAccountcode());
				pzbvo_Df.setVname(km.getAccountname());
				pzbvo_Df.setZy(zyc);
				/** 币种，默认人民币 */
				pzbvo_Df.setPk_currency(yntBoPubUtil.getCNYPk());
				if (jf.doubleValue() > 0) {
					pzbvo_Df.setJfmny(DZFDouble.ZERO_DBL);
					pzbvo_Df.setDfmny(jf);
					vec_zckms.add(pzbvo_Df) ;
				} else if(jf.doubleValue() < 0) {//0 的不新增
					pzbvo_Df.setJfmny(jf.multiply(-1));
					pzbvo_Df.setDfmny(DZFDouble.ZERO_DBL);
					vec_zckms.add(0,pzbvo_Df) ;
				}
				break;
			}
		}
		return vec_zckms;
	}
	
	/**
	 * 获取发生额及余额表
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	private FseJyeVO[] getFseVO(String pk_corp,DZFDate period) throws DZFWarpException {
		QueryParamVO queryParam=new QueryParamVO();
		queryParam.setPk_corp(pk_corp);
		queryParam.setCjq(1);
		queryParam.setCjz(1);
		queryParam.setIshasjz(new DZFBoolean(false));
		queryParam.setBegindate1(period);
		queryParam.setEnddate(period);
		FseJyeVO[] fsyeVos= zxkjReportService.getFsJyeVOs(queryParam,1);
		if(fsyeVos==null||fsyeVos.length==0){
			throw new BusinessException("发生额及余额表为空！");
		}
		return fsyeVos;
		
	}
	/**
	 * 反利润结转
	 */
	public void updateFanLiRunJz(String pk_corp,QmJzVO vo,DZFDate period)throws DZFWarpException {
		if(vo.getJzfinish()!=null && vo.getJzfinish().booleanValue()){
			throw new BusinessException("公司已经年结,不能反利润结转!");
		}
//		if(!queryTzpzHvo(pk_corp,period)){
//			throw new BusinessException("利润未结转，请先结转！");
//		}
		String pk_qmjz = vo.getPrimaryKey();
		SQLParameter sp = new SQLParameter();
		String wppzh = " select * from ynt_tzpz_h where  sourcebillid= ? and sourcebilltype= ? and nvl(dr,0)=0 ";
		sp.addParam(pk_qmjz);
		sp.addParam(IBillTypeCode.HP28);
		List<TzpzHVO> tzlist = 	(List<TzpzHVO>) singleObjectBO.executeQuery(wppzh, sp, new BeanListProcessor(TzpzHVO.class));
		TzpzHVO[] pzHeadVOs = tzlist.toArray(new TzpzHVO[0]);
		if (pzHeadVOs != null && pzHeadVOs.length > 0) {
			StringBuffer b =new StringBuffer();
			for (TzpzHVO headVO : pzHeadVOs) {
				if (headVO.getIshasjz() != null
						&& headVO.getIshasjz().booleanValue()) {
					/** 已有凭证记账 */
					throw new BusinessException("凭证号：" + headVO.getPzh()
							+ "已记账，不能反操作");
				}
				if (headVO.getVbillstatus() == 1) {
					/** 已有凭证审核通过 */
					throw new BusinessException("凭证号：" + headVO.getPzh()
							+ "已审核，不能反操作");
				}
				b.append("'"+headVO.getPrimaryKey()+"',");
			}
			sp.clearParams();
			sp.addParam(pk_corp);
			String updatesql = "update ynt_tzpz_b  set dr =1   where pk_corp = ? and  pk_tzpz_h in (" + b.substring(0,b.length()-1)+")" ;
			singleObjectBO.executeUpdate(updatesql, sp);//先删表体
			singleObjectBO.deleteVOArray(pzHeadVOs);//后删表头
		}
		singleObjectBO.update(vo);
	}

	@Override
	public boolean checkIsYearClose(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(period);
		String qmjzsqlwhere = "select jzfinish from YNT_QMJZ  where nvl(dr,0) = 0 and pk_corp = ? and period = ? ";
		String jzFinish = (String) singleObjectBO.executeQuery(qmjzsqlwhere, sqlp, new ColumnProcessor());
		return "Y".equals(jzFinish);
	}


	/**
	 * 组织凭证主表数据
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public TzpzHVO getTzpzHvo(QmJzVO vo, TzpzBVO[] pzBodyVOs,DZFDate period ,String userid)
			throws DZFWarpException {
		DZFDouble headJfmny = DZFDouble.ZERO_DBL;
		DZFDouble headDfmny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bodyVO : pzBodyVOs) {
			headJfmny = headJfmny.add(bodyVO.getJfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getJfmny());
			headDfmny = headDfmny.add(bodyVO.getDfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getDfmny());
		}
		/** 凭证表头VO */
		TzpzHVO pzHeadVO = new TzpzHVO();
		pzHeadVO.setPk_corp(vo.getPk_corp());
		pzHeadVO.setVyear(Integer.parseInt(vo.getPeriod().substring(0, 4)));
		pzHeadVO.setPzlb(0);// 凭证类别：记账
		pzHeadVO.setPeriod(vo.getPeriod());
		pzHeadVO.setJfmny(headJfmny);
		pzHeadVO.setDfmny(headDfmny);
		pzHeadVO.setCoperatorid(userid);
		pzHeadVO.setIshasjz(DZFBoolean.FALSE);

		DZFDate doperatedate = period;
		pzHeadVO.setDoperatedate(doperatedate);
		pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(),
				doperatedate));
		pzHeadVO.setVbillstatus(8);

		/** 记录来源单据 */
		pzHeadVO.setSourcebilltype(IBillTypeCode.HP28);
		pzHeadVO.setSourcebillid(vo.getPk_qmjz());
		return pzHeadVO;
	}

	public boolean queryTzpzHvo(String pk_corp,DZFDate period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String sql = "select 1 from ynt_tzpz_h where pk_corp = ? and period = ? " +
				" and sourcebilltype = ? and nvl(dr,0) = 0";
		sp.addParam(pk_corp);
		sp.addParam(period.toString().substring(0,7));
		sp.addParam(IBillTypeCode.HP28);
		return singleObjectBO.isExists(pk_corp, sql, sp);
	}
	private YntCpaccountVO getChildKm(YntCpaccountVO km){
		if (!km.getIsleaf().booleanValue()) {
			if (km.getChildren() != null) {
				km = (YntCpaccountVO) km.getChildren()[0];
				km = getChildKm(km);
			}
		}
		return km;
		
	}
	public BdtradeAccountSchemaVO getHymc(String pk_corp){
		BdtradeAccountSchemaVO vo= (BdtradeAccountSchemaVO) singleObjectBO
				.queryByPrimaryKey(BdtradeAccountSchemaVO.class, yntBoPubUtil.getCurrentCorpAccountSchema(pk_corp));
		
		return vo;
	}
	public BdTradeLrHVO[] getLrHvo(String pk_corp){
		SQLParameter sp = new SQLParameter();
		sp.addParam(yntBoPubUtil.getCurrentCorpAccountSchema(pk_corp));
		String sql=" PK_TRADE_ACCOUNTSCHEMA = ? and nvl(dr,0) = 0 ";
		BdTradeLrHVO[] hvo =(BdTradeLrHVO[]) singleObjectBO.queryByCondition(BdTradeLrHVO.class, sql, sp);
		return hvo;
	}
	public BdTradeLrBVO[] getLrbvo(BdTradeLrHVO hvo){
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPrimaryKey());
		String sql=" PK_TRADE_TRANSTEMPLATE_H = ? and nvl(dr,0) = 0 ";
		BdTradeLrBVO[] bvo =(BdTradeLrBVO[]) singleObjectBO.queryByCondition(BdTradeLrBVO.class, sql, sp);
		return bvo;
	}

	private boolean checkIssyjz(String period , String pk_corp ){
		period= period.substring(0, 7);
		boolean hasjz = false;
		String	sql = "select isqjsyjz from YNT_QMCL where period=? and pk_corp= ? and nvl(dr,0)=0 and nvl(isqjsyjz,'N')='Y' ";
		SQLParameter sparam = new SQLParameter();
		sparam.addParam(period);
		sparam.addParam(pk_corp);
		hasjz = isExists(pk_corp, sql,sparam);
		return hasjz;
		
	}
	/**
	 *查询是否有利润结转后的凭证
	 * 
	 */
	private  boolean isExistPzVO(QmJzVO vo){
		String pk_qmjz = vo.getPrimaryKey();
		SQLParameter sp = new SQLParameter();
		String wppzh = " select * from ynt_tzpz_h where  sourcebillid= ? and sourcebilltype = ? and vyear = ? and nvl(dr,0)=0 ";
		sp.addParam(pk_qmjz);
		sp.addParam(IBillTypeCode.HP28);
		sp.addParam(vo.getPeriod().substring(0,4));
		return singleObjectBO.isExists(vo.getPk_corp(), wppzh, sp);
		
	}
}