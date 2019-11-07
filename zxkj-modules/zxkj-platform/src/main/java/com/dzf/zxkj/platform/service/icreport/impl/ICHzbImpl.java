package com.dzf.zxkj.platform.service.icreport.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.report.IcQcVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icreport.IICHzb;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ic_rep_hzbserv")
@Slf4j
public class ICHzbImpl implements IICHzb {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAccountService accountService;
	@Override
	public Map<String, IcDetailVO> queryDetail(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {
		// 查询采购销售单数据
		List<IcDetailVO> detailVOs = queryFsDetails(paramvo, corpvo);
		// 查询库存期初数据
		Map<String, IcQcVO> qcMap = getICQcMx(paramvo);

		Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk( paramvo.getPk_corp());

		// 计算出入库单
		List<Map<String, List<IcDetailVO>>> fsMap = calFsByPeriod(paramvo, detailVOs);

		// 获取结果
		Map<String, IcDetailVO> result = new HashMap<String, IcDetailVO>();

		result = createResult(qcMap, fsMap, accountMap, paramvo);

		// 计算单价
		calPrice(result);

		return result;
	}

	private List<IcDetailVO> queryFsDetails(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {

		List<IcDetailVO> list = null;
		// 老模式 启用库存
		if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
			list = queryIcDetailsMode(paramvo, corpvo);// 库存老模式
		} else {
			list = queryIcDetailsMode2(paramvo, corpvo);// 库存新模式
		}

		convertData(list);//
		return list;
	}

	private List<IcDetailVO> queryIcDetailsMode(QueryParamVO paramvo, CorpVO corpvo) {
		DZFDate jzdate = corpvo.getIcbegindate();
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		StringBuffer sf = new StringBuffer();
		sf.append(" Select i.dbilldate,i.pk_ictradein pk_ictrade_b,i.pk_inventory pk_sp,i.nnum,i.nymny,i.ncost,i.zy, ");
		sf.append(" nvl(i.cbilltype,'" + IBillTypeCode.HP70 + "') cbilltype,");
		sf.append(
				" ry.pk_subject pk_accsubj,nt.accountcode kmbm,nt.accountname km,fy.name spfl, fy.code spflcode,fy.pk_invclassify spflid,");
		sf.append(" ry.invspec spgg, ry.invtype spxh, re.name jldw , ry.code spbm, ry.name spmc ");
		sf.append(" From ynt_ictradein i ");
		sf.append(" left join ynt_inventory ry on ry.pk_inventory = i.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on fy.pk_invclassify = ry.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" Where  i.pk_corp = ? ");
		boolean dataflag = false;
		if (paramvo.getBegindate1() != null && !StringUtil.isEmpty(paramvo.getBegindate1().toString())
				&& paramvo.getEnddate() != null && !StringUtil.isEmpty(paramvo.getEnddate().toString())) {
			dataflag = true;
			sf.append(" and i.dbilldate >= ? ");
			sf.append(" and i.dbilldate <= ? ");
			String period = DateUtils.getPeriod(paramvo.getEnddate());
			sp.addParam(jzdate);// paramvo.getBegindate1()
			sp.addParam(DateUtils.getPeriodEndDate(period));
		} else {
			sf.append(" and <> ");
		}
		String wheInv = null;
		if (!StringUtil.isEmptyWithTrim(paramvo.getPk_inventory())) {
			String[] spris = paramvo.getPk_inventory().split(",");
			wheInv = SqlUtil.buildSqlConditionForIn(spris);
			sf.append(" and i.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		String wheInvcl = null;
		if (!StringUtil.isEmptyWithTrim(paramvo.getXmlbid())) {

			if ("all".equals(paramvo.getXmlbid())) {

			} else if ("noclass".equals(paramvo.getXmlbid())) {
				sf.append(" and ry.pk_invclassify is null");
			} else {
				String[] spris = paramvo.getXmlbid().split(",");
				wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
				sf.append(" and fy.pk_invclassify in ( ");
				sf.append(wheInvcl);
				sf.append(" ) ");
			}
		}

		sf.append("   and nvl(i.dr,0) = 0   ");
		sf.append(" union all ");
		sf.append(
				" Select o.dbilldate,o.pk_ictradeout pk_ictrade_b,o.pk_inventory pk_sp,o.nnum,o.nymny,o.ncost,o.zy, ");
		sf.append(" nvl(o.cbilltype,'" + IBillTypeCode.HP75 + "') cbilltype,");
		sf.append(
				" ry.pk_subject pk_accsubj,nt.accountcode kmbm,nt.accountname km,fy.name spfl, fy.code spflcode,fy.pk_invclassify spflid,");
		sf.append(" ry.invspec spgg, ry.invtype spxh, re.name jldw , ry.code spbm, ry.name spmc ");
		sf.append(" From  ynt_ictradeout o ");
		sf.append(" left join ynt_inventory ry on ry.pk_inventory = o.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on fy.pk_invclassify = ry.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" Where o.pk_corp = ? ");
		sf.append("   and nvl(o.dr,0) = 0   ");

		sp.addParam(paramvo.getPk_corp());
		if (dataflag) {
			sf.append(" and o.dbilldate >= ? ");
			sf.append(" and o.dbilldate <= ? ");
			String period = DateUtils.getPeriod(paramvo.getEnddate());
			sp.addParam(jzdate);// paramvo.getBegindate1()
			sp.addParam(DateUtils.getPeriodEndDate(period));
		} else {
			sf.append(" and 1 != 1 ");
		}
		if (!StringUtil.isEmptyWithTrim(wheInv)) {
			sf.append(" and o.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		if (!StringUtil.isEmptyWithTrim(wheInvcl)) {
			sf.append(" and fy.pk_invclassify in ( ");
			sf.append(wheInvcl);
			sf.append(" ) ");
		} else {
			if ("all".equals(paramvo.getXmlbid())) {

			} else if ("noclass".equals(paramvo.getXmlbid())) {
				sf.append(" and ry.pk_invclassify is null");
			}
		}

		List<IcDetailVO> list = (List<IcDetailVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcDetailVO.class));
		return list;
	}

	private List<IcDetailVO> queryIcDetailsMode2(QueryParamVO paramvo, CorpVO corpvo) {
		DZFDate jzdate = corpvo.getIcbegindate();
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP70);
		sp.addParam(paramvo.getPk_corp());

		StringBuffer sf = new StringBuffer();
		sf.append(
				" Select h1.pk_ictrade_h,h1.dbilldate,h1.dbillid,h1.cbilltype,i.pk_ictradein pk_ictrade_b,i.pk_inventory pk_sp,i.nnum,");
		boolean isChargedept = !StringUtil.isEmpty(corpvo.getChargedeptname())
				&& corpvo.getChargedeptname().equals("一般纳税人") ? true : false;
		if (isChargedept) {
			sf.append(
					"case  when nvl(i.ncost1,0)<>0 then i.ncost1 else (CASE nvl(h1.fp_style,2) WHEN 2 THEN  i.nymny ELSE i.ntotaltaxmny END) end nymny,");
			sf.append(
					"case  when nvl(i.ncost1,0)<>0 then i.ncost1 else (CASE nvl(h1.fp_style,2) WHEN 2 THEN  i.nymny ELSE i.ntotaltaxmny END) end ncost,");
		} else {
			sf.append("case  when nvl(i.ncost1,0)<>0 then i.ncost1 else (i.ntotaltaxmny) end nymny,");
			sf.append("case  when nvl(i.ncost1,0)<>0 then i.ncost1 else (i.ntotaltaxmny) end ncost,");
		}

		sf.append(" CASE h1.cbusitype WHEN '42' THEN '完工入库' WHEN '43' THEN '其他入库' ELSE '采购商品' END zy,");
		sf.append(
				" ry.pk_subject pk_accsubj,nt.accountcode kmbm,nt.accountname km,fy.name spfl, fy.code spflcode,fy.pk_invclassify spflid, ");
		sf.append(" ry.invspec spgg, ry.invtype spxh, re.name jldw , ry.code spbm, ry.name spmc,h1.ts ");
		sf.append(" From ynt_ictrade_h h1 ");
		sf.append(" left join ynt_ictradein i ");
		sf.append("   on h1.pk_ictrade_h = i.pk_ictrade_h ");
		sf.append(" left join ynt_inventory ry on ry.pk_inventory = i.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on fy.pk_invclassify = ry.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" Where  ");
		sf.append("   h1.cbilltype = ? ");
		sf.append("   and h1.pk_corp = ? ");
		boolean dataflag = false;
		if (paramvo.getBegindate1() != null && !StringUtil.isEmpty(paramvo.getBegindate1().toString())
				&& paramvo.getEnddate() != null && !StringUtil.isEmpty(paramvo.getEnddate().toString())) {
			dataflag = true;
			sf.append(" and h1.dbilldate >= ? ");
			sf.append(" and h1.dbilldate <= ? ");
			String period = DateUtils.getPeriod(paramvo.getEnddate());
			sp.addParam(jzdate);// paramvo.getBegindate1()
			sp.addParam(DateUtils.getPeriodEndDate(period));
		} else {
			sf.append(" and 1 <> 1 ");
		}
		String wheInv = null;
		if (!StringUtil.isEmptyWithTrim(paramvo.getPk_inventory())) {
			String[] spris = paramvo.getPk_inventory().split(",");
			wheInv = SqlUtil.buildSqlConditionForIn(spris);
			sf.append(" and i.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}

		String wheInvcl = null;
		if (!StringUtil.isEmptyWithTrim(paramvo.getXmlbid())) {
			if ("all".equals(paramvo.getXmlbid())) {

			} else if ("noclass".equals(paramvo.getXmlbid())) {
				sf.append(" and ry.pk_invclassify is null");
			} else {
				String[] spris = paramvo.getXmlbid().split(",");
				wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
				sf.append(" and fy.pk_invclassify in ( ");
				sf.append(wheInvcl);
				sf.append(" ) ");
			}
		}
		sf.append("   and nvl(h1.dr,0) = 0 ");
		sf.append("   and nvl(i.dr,0) = 0   ");
		sf.append(" union all ");
		sf.append(
				" Select h1.pk_ictrade_h,h1.dbilldate,h1.dbillid,h1.cbilltype,o.pk_ictradeout pk_ictrade_b,o.pk_inventory pk_sp,o.nnum,o.nymny,o.ncost, ");
		sf.append(
				" CASE h1.cbusitype WHEN '47' THEN '领料出库' WHEN '48' THEN '其他出库' WHEN '49' THEN '成本调整' ELSE '销售商品' END zy,");
		sf.append(
				"  ry.pk_subject pk_accsubj,nt.accountcode kmbm,nt.accountname km,fy.name spfl, fy.code spflcode,fy.pk_invclassify spflid, ");
		sf.append(" ry.invspec spgg, ry.invtype spxh, re.name jldw , ry.code spbm, ry.name spmc, h1.ts ");
		sf.append(" From ynt_ictrade_h h1 ");
		sf.append(" left join ynt_ictradeout o ");
		sf.append("   on h1.pk_ictrade_h = o.pk_ictrade_h ");
		sf.append(" left join ynt_inventory ry on ry.pk_inventory = o.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on fy.pk_invclassify = ry.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" Where  ");
		sf.append("   h1.cbilltype = ? ");
		sf.append("   and h1.pk_corp = ? ");
		sf.append("   and nvl(h1.dr,0) = 0 ");
		sf.append("   and nvl(o.dr,0) = 0   ");

		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(paramvo.getPk_corp());
		if (dataflag) {
			sf.append(" and h1.dbilldate >= ? ");
			sf.append(" and h1.dbilldate <= ? ");
			String period = DateUtils.getPeriod(paramvo.getEnddate());
			sp.addParam(jzdate);// paramvo.getBegindate1()
			sp.addParam(DateUtils.getPeriodEndDate(period));
		} else {
			sf.append(" and 1 <> 1 ");
		}
		if (!StringUtil.isEmptyWithTrim(wheInv)) {
			sf.append(" and o.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		if (!StringUtil.isEmptyWithTrim(wheInvcl)) {
			sf.append(" and fy.pk_invclassify in ( ");
			sf.append(wheInvcl);
			sf.append(" ) ");
		} else {
			if ("all".equals(paramvo.getXmlbid())) {

			} else if ("noclass".equals(paramvo.getXmlbid())) {
				sf.append(" and ry.pk_invclassify is null");
			}
		}

		List<IcDetailVO> list = (List<IcDetailVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcDetailVO.class));
		return list;
	}

	/**
	 * 查询库存期初数据
	 * 
	 * @param paramVo
	 * @return
	 */
	private Map<String, IcQcVO> getICQcMx(QueryParamVO paramVo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramVo.getPk_corp());

		StringBuffer sf = new StringBuffer();
		sf.append(" Select qc.*, ");
		sf.append("        ry.pk_subject  pk_accsubj, ");
		sf.append("        nt.accountcode kmbm, ");
		sf.append("        nt.accountname km, ");
		sf.append("        fy.name spfl, fy.code spflcode,fy.pk_invclassify spflid, ");
		sf.append("        ry.invspec     spgg, ");
		sf.append("        ry.invtype     spxh, ");
		sf.append("        re.name        measurename, ");
		sf.append("        ry.code        spbm, ");
		sf.append("        ry.name        spmc ");
		sf.append("   From ynt_icbalance qc ");
		sf.append("   join ynt_inventory ry ");
		sf.append("     on ry.pk_inventory = qc.pk_inventory ");
		sf.append("   left join ynt_invclassify fy ");
		sf.append("     on fy.pk_invclassify = ry.pk_invclassify ");
		sf.append("   left join ynt_measure re ");
		sf.append("     on re.pk_measure = ry.pk_measure ");
		sf.append("   left join ynt_cpaccount nt ");
		sf.append("     on nt.pk_corp_account = ry.pk_subject ");
		sf.append("  Where qc.pk_corp = ? ");
		sf.append("    and nvl(qc.dr, 0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(paramVo.getPk_inventory())) {
			String[] spris = paramVo.getPk_inventory().split(",");
			String wheInv = SqlUtil.buildSqlConditionForIn(spris);
			sf.append(" and qc.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		String wheInvcl = null;
		if (!StringUtil.isEmptyWithTrim(paramVo.getXmlbid())) {
			if ("all".equals(paramVo.getXmlbid())) {

			} else if ("noclass".equals(paramVo.getXmlbid())) {
				sf.append(" and ry.pk_invclassify is null");
			} else {
				String[] spris = paramVo.getXmlbid().split(",");
				wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
				sf.append(" and fy.pk_invclassify in ( ");
				sf.append(wheInvcl);
				sf.append(" ) ");
			}
		}

		List<IcQcVO> fzrs = (List<IcQcVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcQcVO.class));

		Map<String, IcQcVO> qcMap = hashlizeObject(fzrs);

		return qcMap;
	}

	/**
	 * 转化成库存明细账数据
	 * 
	 * @param list
	 */
	private void convertData(List<IcDetailVO> list) {

		if (list == null || list.size() == 0)
			return;

		DZFDouble num = null;
		for (IcDetailVO v : list) {
			num = v.getNnum();
			if (IBillTypeCode.HP70.equals(v.getCbilltype())) {// 采购
				v.setSrsl(num);
				v.setSrje(v.getNymny());
			} else if (IBillTypeCode.HP75.equals(v.getCbilltype())) {// 销售
				v.setFcsl(num);
				// v.setFcje(v.getNymny());
				v.setFcje(v.getNcost());
			}
		}

	}

	private Map<String, IcQcVO> hashlizeObject(List<IcQcVO> fzrs) {
		Map<String, IcQcVO> result = new HashMap<String, IcQcVO>();

		if (fzrs == null || fzrs.size() == 0) {
			return result;
		}

		String key = null;
		for (IcQcVO vo : fzrs) {
			key = vo.getPk_accsubj() + "," + vo.getPk_inventory();

			if (!result.containsKey(key)) {
				result.put(key, vo);
			}
		}

		return result;
	}

	/**
	 * 按区间计算合计
	 * 
	 * @return
	 */
	private List<Map<String, List<IcDetailVO>>> calFsByPeriod(QueryParamVO paramVo, List<IcDetailVO> list) {
		List<Map<String, List<IcDetailVO>>> rs = new ArrayList<Map<String, List<IcDetailVO>>>();

		// 查询期间前
		Map<String, List<IcDetailVO>> periodBefore = new HashMap<String, List<IcDetailVO>>();
		// 查询期间
		Map<String, List<IcDetailVO>> periodSum = new HashMap<String, List<IcDetailVO>>();

		rs.add(periodBefore);
		rs.add(periodSum);

		DZFDate qjc = paramVo.getBegindate1();

		for (IcDetailVO vo : list) {
			if (vo.getDbilldate().before(qjc)) {
				sumDetail(periodBefore, vo, true);
			} else {
				sumDetail(periodSum, vo, false);
			}
		}

		return rs;
	}

	/**
	 * 计算合计
	 * 
	 * @param sumMap
	 * @param vo
	 * @param mergeFlag
	 */
	private void sumDetail(Map<String, List<IcDetailVO>> sumMap, IcDetailVO vo, boolean mergeFlag) {

		String key = vo.getPk_accsubj() + "," + vo.getPk_sp(); // +
																// ReportUtil.getFzKey(vo);

		List<IcDetailVO> icdList = sumMap.get(key);
		IcDetailVO icdvo = null;
		if (icdList == null || icdList.size() == 0) {
			icdvo = (IcDetailVO) vo.clone();
			icdList = new ArrayList<IcDetailVO>();
			icdList.add(icdvo);
			sumMap.put(key, icdList);
		} else {
			if (mergeFlag) {
				icdvo = icdList.get(0);
				icdvo.setSrsl(SafeCompute.add(vo.getSrsl(), icdvo.getSrsl()));
				icdvo.setSrje(SafeCompute.add(vo.getSrje(), icdvo.getSrje()));

				icdvo.setFcsl(SafeCompute.add(vo.getFcsl(), icdvo.getFcsl()));
				icdvo.setFcje(SafeCompute.add(vo.getFcje(), icdvo.getFcje()));
			} else {
				icdvo = (IcDetailVO) vo.clone();
				icdList.add(icdvo);
			}
		}
	}

	/**
	 * 封装结果数据
	 * 
	 * @param qcMap
	 * @param fsMap
	 * @param accountMap
	 * @param paramvo
	 * @return
	 */
	private Map<String, IcDetailVO> createResult(Map<String, IcQcVO> qcMap, List<Map<String, List<IcDetailVO>>> fsMap,
			Map<String, YntCpaccountVO> accountMap, QueryParamVO paramvo) {
		Map<String, IcDetailVO> result = new LinkedHashMap<String, IcDetailVO>();
		Set<String> keySet = new HashSet<String>();

		String[] periodKeys = getPeriodKeyArr(paramvo);

		Map<String, List<IcDetailVO>> PeriodBeforeFs = fsMap.get(0);// new
																	// HashMap<String,
																	// IcDetailVO>();
		Map<String, List<IcDetailVO>> PeriodFs = fsMap.get(1);// new
																// HashMap<String,
																// IcDetailVO>();

		keySet.addAll(qcMap.keySet());
		keySet.addAll(PeriodBeforeFs.keySet());
		keySet.addAll(PeriodFs.keySet());

		if (keySet.size() > 0) {
			String account_id = null;
			YntCpaccountVO account = null;
			IcQcVO qcVo = null;
			List<IcDetailVO> periodBf = null;
			List<IcDetailVO> period = null;
			IcDetailVO qcrs = null;
			for (String key : keySet) {
				account_id = key;
				if (account_id.length() > 24) {
					account_id = account_id.substring(0, 24);
				}
				account = accountMap.get(account_id);
				qcVo = qcMap.get(key);
				periodBf = PeriodBeforeFs.get(key);
				period = PeriodFs.get(key);

				qcrs = calculateQC(key, qcVo, periodBf, period, account, paramvo);// 计算
				result.put(key, qcrs);
				Map<String, List<IcDetailVO>> maps = hashlizeObjectByPeriod(period);
				// 按月份展示
				for (String pkey : periodKeys) {
					if (maps != null && !maps.isEmpty()) {
						List<IcDetailVO> peList = maps.get(pkey);
						if (peList != null && peList.size() > 0) {
							for (IcDetailVO vo : peList) {
								sumDetail(result, vo);
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 计算合计
	 * 
	 * @param sumMap
	 * @param vo
	 * @param mergeFlag
	 */
	private void sumDetail(Map<String, IcDetailVO> sumMap, IcDetailVO vo) {
		String key = vo.getPk_accsubj() + "," + vo.getPk_sp();
		IcDetailVO icdvo = sumMap.get(key);
		if (icdvo == null) {
			sumMap.put(key, vo);
		} else {
			icdvo.setSrsl(SafeCompute.add(vo.getSrsl(), icdvo.getSrsl()));
			icdvo.setSrje(SafeCompute.add(vo.getSrje(), icdvo.getSrje()));
			icdvo.setFcsl(SafeCompute.add(vo.getFcsl(), icdvo.getFcsl()));
			icdvo.setFcje(SafeCompute.add(vo.getFcje(), icdvo.getFcje()));
			icdvo.setJcje(SafeCompute.sub(SafeCompute.add(icdvo.getQcje(), icdvo.getSrje()), icdvo.getFcje()));
			icdvo.setJcsl(SafeCompute.sub(SafeCompute.add(icdvo.getQcsl(), icdvo.getSrsl()), icdvo.getFcsl()));
		}
	}

	private Map<String, List<IcDetailVO>> hashlizeObjectByPeriod(List<IcDetailVO> objs) {
		Map<String, List<IcDetailVO>> result = new HashMap<String, List<IcDetailVO>>();
		if (objs == null || objs.isEmpty())
			return result;
		String key = null;
		List<IcDetailVO> zlist = null;
		for (int i = 0; i < objs.size(); i++) {
			key = DateUtils.getPeriod(objs.get(i).getDbilldate());
			if (result.containsKey(key)) {
				result.get(key).add(objs.get(i));
			} else {
				zlist = new ArrayList<IcDetailVO>();
				zlist.add(objs.get(i));
				result.put(key, zlist);
			}
		}
		return result;
	}

	/**
	 * 根据期初，计算发生
	 * 
	 * @param key
	 * @param qcVo
	 * @param periodBf
	 * @param period
	 * @param account
	 * @return
	 */
	private IcDetailVO calculateQC(String key, IcQcVO qcVo, List<IcDetailVO> periodBf, List<IcDetailVO> period,
			YntCpaccountVO account, QueryParamVO paramvo) {
		DZFDouble srsl = DZFDouble.ZERO_DBL;
		DZFDouble srje = DZFDouble.ZERO_DBL;

		IcDetailVO icq = new IcDetailVO();
		if (qcVo != null) {// 期初
			icq.setPk_accsubj(account.getPk_corp_account());
			icq.setKmbm(account.getAccountcode());
			icq.setKm(account.getAccountname());
			icq.setPk_sp(qcVo.getPk_inventory());// 第二位 {科目pk} + {商品pk} +[单据编号]
			icq.setSpbm(qcVo.getSpbm());
			icq.setSpmc(qcVo.getSpmc());
			icq.setSpflcode(qcVo.getSpflcode());
			icq.setSpflid(qcVo.getSpflid());
			icq.setSpfl(qcVo.getSpfl());
			icq.setSpgg(qcVo.getSpgg());
			icq.setSpxh(qcVo.getSpxh());
			icq.setJldw(qcVo.getMeasurename());
			icq.setDbilldate(paramvo.getBegindate1());

			srsl = qcVo.getNnum();
			srje = qcVo.getNcost();
		}

		if (periodBf != null && periodBf.size() > 0) {// 期间前
			icq.setPk_accsubj(account.getPk_corp_account());
			icq.setKmbm(account.getAccountcode());
			icq.setKm(account.getAccountname());
			icq.setPk_sp(periodBf.get(0).getPk_sp());// 第二位 {科目pk} + {商品pk}
														// +[单据编号]
			icq.setSpbm(periodBf.get(0).getSpbm());
			icq.setSpmc(periodBf.get(0).getSpmc());
			icq.setSpflcode(periodBf.get(0).getSpflcode());
			icq.setSpflid(periodBf.get(0).getSpflid());
			icq.setSpfl(periodBf.get(0).getSpfl());
			icq.setSpgg(periodBf.get(0).getSpgg());
			icq.setSpxh(periodBf.get(0).getSpxh());
			icq.setJldw(periodBf.get(0).getJldw());
			icq.setDbilldate(paramvo.getBegindate1());

			srsl = SafeCompute.sub(SafeCompute.add(srsl, periodBf.get(0).getSrsl()), periodBf.get(0).getFcsl());
			srje = SafeCompute.sub(SafeCompute.add(srje, periodBf.get(0).getSrje()), periodBf.get(0).getFcje());

			// skipvo.setPk_sp(icq.getPk_inventory());//存值后续用
		}

		if (StringUtil.isEmpty(icq.getSpbm()) && StringUtil.isEmpty(icq.getSpmc())) {
			if (period != null && period.size() > 0) {
				icq.setPk_accsubj(account.getPk_corp_account());
				icq.setKmbm(account.getAccountcode());
				icq.setKm(account.getAccountname());
				icq.setPk_sp(period.get(0).getPk_sp());// 第二位 {科目pk} + {商品pk}
														// +[单据编号]
				icq.setSpbm(period.get(0).getSpbm());
				icq.setSpmc(period.get(0).getSpmc());
				icq.setSpflcode(period.get(0).getSpflcode());
				icq.setSpflid(period.get(0).getSpflid());
				icq.setSpfl(period.get(0).getSpfl());
				icq.setSpgg(period.get(0).getSpgg());
				icq.setSpxh(period.get(0).getSpxh());
				icq.setJldw(period.get(0).getJldw());
				icq.setDbilldate(paramvo.getBegindate1());
			}
		}
		icq.setQcsl(srsl);
		icq.setQcje(srje);
		return icq;
	}

	private String[] getPeriodKeyArr(QueryParamVO paramvo) {
		String startdate = paramvo.getBegindate1().toString();
		String enddate = paramvo.getEnddate().toString();
		String endKey = enddate.substring(0, 7);
		List<String> keyList = new ArrayList<String>();
		keyList.add(startdate.substring(0, 7));

		boolean bool = true;
		String keyLast = null;
		String newKey = null;
		if (keyList != null && keyList.size() > 0) {
			while (bool) {
				keyLast = keyList.get(keyList.size() - 1);
				if (keyLast.compareTo(endKey) < 0) {
					newKey = getNextKey(keyLast);
					keyList.add(newKey);
				} else {
					bool = false;
				}
			}
		}

		return keyList.toArray(new String[0]);

	}

	private String getNextKey(String key) {
		int year = Integer.parseInt(key.substring(0, 4));
		int month = Integer.parseInt(key.substring(5, 7));
		int newM = month + 1;
		if (newM <= 12) {
			String nextMonth = newM < 10 ? "0" + newM : newM + "";
			return year + "-" + nextMonth;
		} else {
			return year + 1 + "-01";
		}
	}

	private void calPrice(Map<String, IcDetailVO> result) {
		if (result == null) {
			return;
		}
		IcDetailVO vo = null;
		DZFDouble qcje = null;
		DZFDouble qcsl = null;
		DZFDouble qcdj = null;
		DZFDouble srje = null;
		DZFDouble srsl = null;
		DZFDouble srdj = null;
		DZFDouble fcje = null;
		DZFDouble fcsl = null;
		DZFDouble fcdj = null;
		DZFDouble jcje = null;
		DZFDouble jcsl = null;
		DZFDouble jcdj = null;
		for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
			vo = entry.getValue();

			qcje = vo.getQcje();
			qcsl = vo.getQcsl();
			qcdj = SafeCompute.div(qcje, qcsl);
			vo.setQcdj(qcdj);

			srje = vo.getSrje();
			srsl = vo.getSrsl();
			srdj = SafeCompute.div(srje, srsl);
			vo.setSrdj(srdj);

			fcje = vo.getFcje();
			fcsl = vo.getFcsl();
			fcdj = SafeCompute.div(fcje, fcsl);
			vo.setFcdj(fcdj);

			jcje = vo.getJcje();
			jcsl = vo.getJcsl();
			jcdj = SafeCompute.div(jcje, jcsl);
			vo.setJcdj(jcdj);
		}
	}
}
