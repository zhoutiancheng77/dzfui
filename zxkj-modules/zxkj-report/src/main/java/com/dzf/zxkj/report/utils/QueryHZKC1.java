package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;

import java.util.*;

/**
 * 数量金额汇总表查询 启用   针对库存老模式
 */
@SuppressWarnings("all")
public class QueryHZKC1 {

    private SingleObjectBO singleObjectBO;

    private Map<String, String> precisionMap;

    private int precisionPrice;

    private IZxkjPlatformService zxkjPlatformService;

    public QueryHZKC1(SingleObjectBO singleObjectBO, IZxkjPlatformService zxkjPlatformService,
                      Map<String, String> precisionMap) {
        this.singleObjectBO = singleObjectBO;
        this.zxkjPlatformService = zxkjPlatformService;

        this.precisionMap = precisionMap;

        if (precisionMap != null) {
            String value = precisionMap.get(IParameterConstants.DZF010);
            precisionPrice = Integer.parseInt(value);
        } else {
            precisionPrice = 4;
        }
    }

    public List<NumMnyGlVO> buildData(QueryCondictionVO paramVo)
            throws DZFWarpException {
        String pk_corp = paramVo.getPk_corp();

        if (paramVo.getQjz().compareTo(paramVo.getQjq()) < 0)
            throw new BusinessException("结束日期不能在开始日期前！");
        // 取当前公司建账日期
        DZFDate jzdate = paramVo.getJzdate();
        if (paramVo.getCjq() == null)
            paramVo.setCjq(1);
        if (paramVo.getCjz() == null)
            paramVo.setCjz(10);
        if (paramVo.getCjq() > paramVo.getCjz())
            throw new BusinessException("开始级次不能大于结束级次！");

        if (DateUtils.getPeriod(jzdate).compareTo(paramVo.getQjq()) > 0) {
            StringBuffer sExp = new StringBuffer();
            sExp.append("开始日期不能在建账日期(")
                    .append(DateUtils.getPeriod(jzdate)).append(")前!");
            throw new BusinessException(sExp.toString());
        }
        boolean isFzhs = paramVo.getIsfzhs() != null
                && paramVo.getIsfzhs().booleanValue();
        // 查询期间是否在建账年
        boolean isJzYear = jzdate.getYear() == Integer.parseInt(paramVo
                .getQjq().substring(0, 4));
        //查询期间末年份
        String qjzyear = paramVo.getQjz().substring(0, 4);
        //是否跨期间
        boolean isOverPeriod = !qjzyear.equals(paramVo.getQjq().substring(0, 4));

        // 结果map
        Map<String, NumMnyGlVO> result = new HashMap<String, NumMnyGlVO>();

        List<NumMnyGlVO> detailVOs = queryDetailVOs(jzdate, paramVo);
        Map<String, FzhsqcVO> qcMap = getICQcMx(paramVo);
        Map<String, YntCpaccountVO> accountMap = zxkjPlatformService.queryMapByPk(pk_corp);
        // 计算发生
        List<Map<String, NumMnyGlVO>> fsMap = calFsByPeriod(paramVo.getQjq(),
                detailVOs, isFzhs, isOverPeriod, qjzyear);

        result = createResult(qcMap, fsMap, accountMap, isJzYear, isOverPeriod, qjzyear);
        fsMap = null;

        // 添加上级科目并将数量金额汇总到上级科目
        addParents(result, accountMap, paramVo);

        List<NumMnyGlVO> resultList = filterAndSetValue(result, paramVo,
                accountMap);

        // 根据科目编码排序
        Collections.sort(resultList, new Comparator<NumMnyGlVO>() {
            public int compare(NumMnyGlVO arg0, NumMnyGlVO arg1) {
                return arg0.getKmbm().compareTo(arg1.getKmbm());
            }
        });
        return resultList;
    }

    private void addParents(Map<String, NumMnyGlVO> result,
                            Map<String, YntCpaccountVO> mp, QueryCondictionVO param) {
        List<String> leaves = new ArrayList<String>(result.keySet());
        for (String leafKey : leaves) {
            if (leafKey.length() > 24)
                continue;
            NumMnyGlVO leafVo = result.get(leafKey);
            String account_id = leafVo.getPk_subject();
            List<String> parents = getKmParent(mp, account_id);
            for (String parent : parents) {
                NumMnyGlVO parentVO = result.get(parent);
                if (parentVO == null) {
                    parentVO = (NumMnyGlVO) leafVo.clone();
                    parentVO.setPk_subject(parent);
                    result.put(parent, parentVO);
                } else {

                    // 累加
                    parentVO.setQcmny(SafeCompute.add(parentVO.getQcmny(),
                            leafVo.getQcmny()));
                    parentVO.setQcnum(SafeCompute.add(parentVO.getQcnum(),
                            leafVo.getQcnum()));
                    parentVO.setBqjfmny(SafeCompute.add(parentVO.getBqjfmny(),
                            leafVo.getBqjfmny()));
                    parentVO.setBqjfnum(SafeCompute.add(parentVO.getBqjfnum(),
                            leafVo.getBqjfnum()));
                    parentVO.setBqdfmny(SafeCompute.add(parentVO.getBqdfmny(),
                            leafVo.getBqdfmny()));
                    parentVO.setBqdfnum(SafeCompute.add(parentVO.getBqdfnum(),
                            leafVo.getBqdfnum()));
                    parentVO.setBndfmny(SafeCompute.add(parentVO.getBndfmny(),
                            leafVo.getBndfmny()));
                    parentVO.setBndfnum(SafeCompute.add(parentVO.getBndfnum(),
                            leafVo.getBndfnum()));
                    parentVO.setBnjfmny(SafeCompute.add(parentVO.getBnjfmny(),
                            leafVo.getBnjfmny()));
                    parentVO.setBnjfnum(SafeCompute.add(parentVO.getBnjfnum(),
                            leafVo.getBnjfnum()));
                    parentVO.setQmmny(SafeCompute.add(parentVO.getQmmny(),
                            leafVo.getQmmny()));
                    parentVO.setQmnum(SafeCompute.add(parentVO.getQmnum(),
                            leafVo.getQmnum()));
                }
            }
        }
    }

    /**
     * 获取所有上级科目
     *
     * @param mp
     * @param account_id
     * @return
     */
    private List<String> getKmParent(Map<String, YntCpaccountVO> mp,
                                     String account_id) {
        YntCpaccountVO vo1 = mp.get(account_id);
        List<String> ls = new ArrayList<String>();
        for (YntCpaccountVO vo : mp.values()) {
            if (vo1.getAccountcode().startsWith(vo.getAccountcode())
                    && vo1.getAccountcode().length() > vo.getAccountcode()
                    .length()) {
                ls.add(vo.getPk_corp_account());
            }
        }
        return ls;
    }

    // 生成末级科目数据
    private Map<String, NumMnyGlVO> createResult(Map<String, FzhsqcVO> qcMap,
                                                 List<Map<String, NumMnyGlVO>> fsMap,
                                                 Map<String, YntCpaccountVO> accountMap,
                                                 boolean isJzYear,
                                                 boolean isOverPeriod,
                                                 String qjzyear) {
        Map<String, NumMnyGlVO> result = new HashMap<String, NumMnyGlVO>();
        Set<String> keySet = new HashSet<String>();
        Map<String, NumMnyGlVO> yearBeforeFs = fsMap.get(0);
        Map<String, NumMnyGlVO> PeriodBeforeFs = fsMap.get(1);
        Map<String, NumMnyGlVO> PeriodFs = fsMap.get(2);
        Map<String, NumMnyGlVO> PeriodEndYearFs = fsMap.get(3);
        keySet.addAll(qcMap.keySet());
        keySet.addAll(yearBeforeFs.keySet());
        keySet.addAll(PeriodBeforeFs.keySet());
        keySet.addAll(PeriodFs.keySet());
        if (keySet.size() > 0) {
            for (String key : keySet) {
                String account_id = key;
                if (account_id.length() > 24)
                    account_id = account_id.substring(0, 24);
                YntCpaccountVO account = accountMap.get(account_id);
                FzhsqcVO qcVo = qcMap.get(key);
                NumMnyGlVO yearBf = yearBeforeFs.get(key);
                NumMnyGlVO periodBf = PeriodBeforeFs.get(key);
                NumMnyGlVO period = PeriodFs.get(key);
                NumMnyGlVO periodEndYear = PeriodEndYearFs.get(key);
                NumMnyGlVO rs = calculateAll(key, qcVo, yearBf, periodBf,
                        period, account, isJzYear, isOverPeriod, qjzyear, periodEndYear);
                result.put(key, rs);
            }
        }

        return result;
    }

    // 根据期初，发生计算
    private NumMnyGlVO calculateAll(String key, FzhsqcVO qcVo,
                                    NumMnyGlVO yearBf, NumMnyGlVO periodBf, NumMnyGlVO period,
                                    YntCpaccountVO account, boolean isJzYear,
                                    boolean isOverPeriod,
                                    String qjzyear,
                                    NumMnyGlVO periodEndYear) {
        DZFDouble qcmny = DZFDouble.ZERO_DBL;
        DZFDouble qcnum = DZFDouble.ZERO_DBL;
        DZFDouble bqjfmny = DZFDouble.ZERO_DBL;
        DZFDouble bqjfnum = DZFDouble.ZERO_DBL;
        DZFDouble bqdfmny = DZFDouble.ZERO_DBL;
        DZFDouble bqdfnum = DZFDouble.ZERO_DBL;
        DZFDouble bndfmny = DZFDouble.ZERO_DBL;
        DZFDouble bndfnum = DZFDouble.ZERO_DBL;
        DZFDouble bnjfmny = DZFDouble.ZERO_DBL;
        DZFDouble bnjfnum = DZFDouble.ZERO_DBL;
        DZFDouble qmmny = DZFDouble.ZERO_DBL;
        DZFDouble qmnum = DZFDouble.ZERO_DBL;

        if (qcVo != null) {
            // 期初
            qcmny = qcVo.getThismonthqc();
            qcnum = qcVo.getMonthqmnum();

            if (isJzYear && !isOverPeriod) {
                // 建账年本年累计要计算期初的发生
                bnjfmny = qcVo.getYearjffse();
                bndfmny = qcVo.getYeardffse();
                bnjfnum = qcVo.getBnfsnum();
                bndfnum = qcVo.getBndffsnum();
            }
        }

        Integer direction = account.getDirection();
        boolean isJf = direction == 0;
        if (yearBf != null) {
            // 期初+本年前发生
            DZFDouble yearBfQc = null;
            DZFDouble yearBfQcNum = null;
            if (isJf) {
                yearBfQc = SafeCompute.sub(yearBf.getBqjfmny(),
                        yearBf.getBqdfmny());
                yearBfQcNum = SafeCompute.sub(yearBf.getBqjfnum(),
                        yearBf.getBqdfnum());
            } else {
                yearBfQc = SafeCompute.sub(yearBf.getBqdfmny(),
                        yearBf.getBqjfmny());
                yearBfQcNum = SafeCompute.sub(yearBf.getBqdfnum(),
                        yearBf.getBqjfnum());
            }
            qcmny = SafeCompute.add(qcmny, yearBfQc);
            qcnum = SafeCompute.add(qcnum, yearBfQcNum);

        }

        if (periodBf != null) {

            // 本年累计
            if (!isOverPeriod) {//不跨越期间
                bnjfmny = SafeCompute.add(bnjfmny, periodBf.getBqjfmny());
                bndfmny = SafeCompute.add(bndfmny, periodBf.getBqdfmny());
                bnjfnum = SafeCompute.add(bnjfnum, periodBf.getBqjfnum());
                bndfnum = SafeCompute.add(bndfnum, periodBf.getBqdfnum());
            }

            // 期初加本年发生
            DZFDouble periodBfQc = null;
            DZFDouble periodBfQcNum = null;

            if (isJf) {
                periodBfQc = SafeCompute.sub(periodBf.getBqjfmny(),
                        periodBf.getBqdfmny());
                periodBfQcNum = SafeCompute.sub(periodBf.getBqjfnum(),
                        periodBf.getBqdfnum());
            } else {
                periodBfQc = SafeCompute.sub(periodBf.getBqdfmny(),
                        periodBf.getBqjfmny());
                periodBfQcNum = SafeCompute.sub(periodBf.getBqdfnum(),
                        periodBf.getBqjfnum());
            }
            qcmny = SafeCompute.add(qcmny, periodBfQc);
            qcnum = SafeCompute.add(qcnum, periodBfQcNum);
        }
        qmmny = qcmny;
        qmnum = qcnum;
        if (period != null) {
            // 本期发生
            bqjfmny = period.getBqjfmny();
            bqdfmny = period.getBqdfmny();
            bqjfnum = period.getBqjfnum();
            bqdfnum = period.getBqdfnum();

            DZFDouble periodFs = null;
            DZFDouble periodFsNum = null;
            if (isJf) {
                periodFs = SafeCompute.sub(period.getBqjfmny(),
                        period.getBqdfmny());
                periodFsNum = SafeCompute.sub(period.getBqjfnum(),
                        period.getBqdfnum());
            } else {
                periodFs = SafeCompute.sub(period.getBqdfmny(),
                        period.getBqjfmny());
                periodFsNum = SafeCompute.sub(period.getBqdfnum(),
                        period.getBqjfnum());
            }

            //重算本年累计
            if (isOverPeriod && periodEndYear != null && qjzyear.equals(periodEndYear.getOpdate().substring(0, 4))) {
                DZFDouble tempbqjfmny = periodEndYear.getBqjfmny();
                DZFDouble tempbqdfmny = periodEndYear.getBqdfmny();
                DZFDouble tempbqjfnum = periodEndYear.getBqjfnum();
                DZFDouble tempbqdfnum = periodEndYear.getBqdfnum();
                bnjfmny = SafeCompute.add(tempbqjfmny, bnjfmny);
                bndfmny = SafeCompute.add(tempbqdfmny, bndfmny);
                bnjfnum = SafeCompute.add(tempbqjfnum, bnjfnum);
                bndfnum = SafeCompute.add(tempbqdfnum, bndfnum);
            } else if (qjzyear.equals(period.getOpdate().substring(0, 4))) {
                bnjfmny = SafeCompute.add(bqjfmny, bnjfmny);
                bndfmny = SafeCompute.add(bqdfmny, bndfmny);
                bnjfnum = SafeCompute.add(bqjfnum, bnjfnum);
                bndfnum = SafeCompute.add(bqdfnum, bndfnum);
            }

            qmmny = SafeCompute.add(qmmny, periodFs);
            qmnum = SafeCompute.add(qmnum, periodFsNum);
        }

        NumMnyGlVO vo = new NumMnyGlVO();
        vo.setPk_subject(key);
        vo.setQcmny(qcmny);
        vo.setQcnum(qcnum);
        vo.setBqjfmny(bqjfmny);
        vo.setBqjfnum(bqjfnum);
        vo.setBqdfmny(bqdfmny);
        vo.setBqdfnum(bqdfnum);
        vo.setBndfmny(bndfmny);
        vo.setBndfnum(bndfnum);
        vo.setBnjfmny(bnjfmny);
        vo.setBnjfnum(bnjfnum);
        vo.setQmmny(qmmny);
        vo.setQmnum(qmnum);
        return vo;
    }

    // 按区间计算合计
    private List<Map<String, NumMnyGlVO>> calFsByPeriod(String qjq,
                                                        List<NumMnyGlVO> list, boolean isFzhs, boolean isOverPeriod,
                                                        String qjzyear) {
        List<Map<String, NumMnyGlVO>> rs = new ArrayList<Map<String, NumMnyGlVO>>();
        // 查询年之前
        Map<String, NumMnyGlVO> yearBefore = new HashMap<String, NumMnyGlVO>();
        // 查询年初到查询期间
        Map<String, NumMnyGlVO> periodBefore = new HashMap<String, NumMnyGlVO>();
        // 查询期间
        Map<String, NumMnyGlVO> periodSum = new HashMap<String, NumMnyGlVO>();
        // 查询期间中最后一个年份
        Map<String, NumMnyGlVO> periodEndYearSum = new HashMap<String, NumMnyGlVO>();
        rs.add(yearBefore);
        rs.add(periodBefore);
        rs.add(periodSum);
        rs.add(periodEndYearSum);

        String yearBegin = qjq.substring(0, 4) + "-01";

        if (list == null || list.size() == 0)
            return rs;

        for (NumMnyGlVO vo : list) {
            if (vo.getOpdate().compareTo(yearBegin) < 0) {
                sumNum(yearBefore, vo, isFzhs);
            } else if (vo.getOpdate().compareTo(qjq) < 0) {
                sumNum(periodBefore, vo, isFzhs);
            } else {
                sumNum(periodSum, vo, isFzhs);

                if (isOverPeriod && vo.getOpdate().substring(0, 4).equals(qjzyear)) {//最后一个年份 跨年份特殊处理
                    sumNum(periodEndYearSum, (NumMnyGlVO) vo.clone(), isFzhs);
                }
            }
        }

        return rs;
    }

    /**
     * 计算合计
     *
     * @param sumMap
     * @param vo
     * @param isFzhs
     */
    private void sumNum(Map<String, NumMnyGlVO> sumMap, NumMnyGlVO vo,
                        boolean isFzhs) {
        String key = vo.getPk_subject();
        if (isFzhs) {
            //有辅助核算时，科目单独存一个vo
            String fzKey = ReportUtil.getFzKey(vo);
            if (fzKey.length() > 0) {
                NumMnyGlVO sumVO_km = sumMap.get(key);
                if (sumVO_km == null) {
                    sumVO_km = (NumMnyGlVO) vo.clone();
                    sumMap.put(key, sumVO_km);
                } else {
                    sumVO_km.setBqjfmny(SafeCompute.add(vo.getBqjfmny(),
                            sumVO_km.getBqjfmny()));
                    sumVO_km.setBqdfmny(SafeCompute.add(vo.getBqdfmny(),
                            sumVO_km.getBqdfmny()));
                    sumVO_km.setBqjfnum(SafeCompute.add(vo.getBqjfnum(),
                            sumVO_km.getBqjfnum()));
                    sumVO_km.setBqdfnum(SafeCompute.add(vo.getBqdfnum(),
                            sumVO_km.getBqdfnum()));
                }
                key += fzKey;
            }
        }
        NumMnyGlVO sumVO = sumMap.get(key);
        if (sumMap.get(key) == null) {
            sumMap.put(key, vo);
        } else {
            sumVO.setBqjfmny(SafeCompute.add(vo.getBqjfmny(),
                    sumVO.getBqjfmny()));
            sumVO.setBqdfmny(SafeCompute.add(vo.getBqdfmny(),
                    sumVO.getBqdfmny()));
            sumVO.setBqjfnum(SafeCompute.add(vo.getBqjfnum(),
                    sumVO.getBqjfnum()));
            sumVO.setBqdfnum(SafeCompute.add(vo.getBqdfnum(),
                    sumVO.getBqdfnum()));
        }
    }

    // 查询年期初数据
    private Map<String, FzhsqcVO> getQcMx(QueryCondictionVO paramVo)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String pk_bz = paramVo.getPk_currency();
        sp.addParam(paramVo.getPk_corp());
        sp.addParam(paramVo.getPk_corp());

        // 是否显示辅助核算
        boolean isfzhs = paramVo.getIsfzhs() != null
                && paramVo.getIsfzhs().booleanValue();
        StringBuffer sf = new StringBuffer();

        StringBuffer whereSql = new StringBuffer();
        sf.append(" select * from ynt_qcye ");
        whereSql.append(" where pk_corp = ? and nvl(dr, 0) = 0 and pk_accsubj in ");
        whereSql.append(" (select pk_corp_account from ynt_cpaccount cp where cp.pk_corp = ? and nvl(cp.dr, 0) = 0 and isnum = 'Y' and isleaf = 'Y' ");
        // if (isfzhs) {
        // whereSql.append(" and isfzhs = '00000000' ");
        // }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_first())) {
            whereSql.append(" and accountcode >= ? ");
            sp.addParam(paramVo.getKms_first());
        }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_last())) {
            whereSql.append(" and accountcode <= ? ");
            sp.addParam(paramVo.getKms_last());
        }
        whereSql.append(")");
        if (pk_bz != null && pk_bz.length() > 0) {
            whereSql.append(" and pk_currency = ?");
            sp.addParam(pk_bz);
        }
        sf.append(whereSql);
        List<FzhsqcVO> qcrs = (List<FzhsqcVO>) singleObjectBO.executeQuery(
                sf.toString(), sp, new BeanListProcessor(FzhsqcVO.class));
        if (isfzhs) {
            //查询辅助核算期初
            String sql = " select * from ynt_fzhsqc " + whereSql.toString();
            List<FzhsqcVO> fzrs = (List<FzhsqcVO>) singleObjectBO.executeQuery(
                    sql, sp, new BeanListProcessor(FzhsqcVO.class));
            qcrs.addAll(fzrs);

        }

        Map<String, FzhsqcVO> qcMap = hashlizeObject(qcrs, isfzhs);
        return qcMap;
    }

    // 查询库存期初数据
    private Map<String, FzhsqcVO> getICQcMx(QueryCondictionVO paramVo)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String pk_bz = paramVo.getPk_currency();
        sp.addParam(paramVo.getPk_corp());

        // 是否显示辅助核算
        boolean isfzhs = paramVo.getIsfzhs() != null
                && paramVo.getIsfzhs().booleanValue();
        StringBuffer sf = new StringBuffer();

        sf.append(" select qc.*, it.pk_subject,  it.code inventorycode, ct.accountcode pk_subjectcode,it.name inventoryname ,  ct.accountname pk_subjectname from YNT_ICBALANCE qc ");
        sf.append(" join YNT_INVENTORY it ");
        sf.append(" on it.PK_INVENTORY = qc.PK_INVENTORY ");
        sf.append(" join ynt_cpaccount ct ");
        sf.append(" on it.pk_subject = ct.pk_corp_account ");
        sf.append(" where  qc.pk_corp = ? and nvl(qc.dr,0) = 0 and nvl(qc.dr,0) = 0 and  (nvl(ct.isnum,'N') = 'Y') ");
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_first())) {
            sf.append(" and ct.accountcode >= ? ");
            sp.addParam(paramVo.getKms_first());
        }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_last())) {
            sf.append(" and ct.accountcode <= ? ");
            sp.addParam(paramVo.getKms_last());
        }
        if (!StringUtil.isEmptyWithTrim(pk_bz)) {
            sf.append(" and qc.pk_currency = ?");
            sp.addParam(pk_bz);
        }

        List<IcbalanceVO> list = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(IcbalanceVO.class));

        if (list == null || list.size() == 0) {
            return new HashMap<String, FzhsqcVO>();
        }

        List<FzhsqcVO> ylist = new ArrayList<FzhsqcVO>();
        FzhsqcVO yevo = null;
        List<FzhsqcVO> fzlist = new ArrayList<FzhsqcVO>();
        FzhsqcVO fzvo = null;
        for (IcbalanceVO vo : list) {
            yevo = new FzhsqcVO();
            yevo.setPk_accsubj(vo.getPk_subject());
            yevo.setThismonthqc(vo.getNcost());
            yevo.setMonthqmnum(vo.getNnum());
            yevo.setVyear(Integer.parseInt(vo.getDbilldate().substring(0, 4)));
            yevo.setVcode(vo.getPk_subjectcode());
            yevo.setVname(vo.getPk_subjectname());
            ylist.add(yevo);

            if (isfzhs) {
                fzvo = new FzhsqcVO();
                fzvo.setFzhsx6(vo.getPk_inventory());
                fzvo.setPk_accsubj(vo.getPk_subject());
                fzvo.setThismonthqc(vo.getNcost());
                fzvo.setMonthqmnum(vo.getNnum());
                fzvo.setVyear(Integer.parseInt(vo.getDbilldate().substring(0, 4)));
                fzvo.setVcode(vo.getPk_subjectcode() + "_" + vo.getInventorycode());
                fzvo.setVname(vo.getPk_subjectname() + "_" + vo.getInventoryname());
                fzlist.add(fzvo);
            }
        }

        List<FzhsqcVO> tempylist = hashlizeObject(ylist);

        if (isfzhs) {
            tempylist.addAll(fzlist);
        }

        Map<String, FzhsqcVO> qcMap = hashlizeObject(tempylist, isfzhs);

        putAllCorpFzqc(paramVo, qcMap);
        return qcMap;
    }

    private void putAllCorpFzqc(QueryCondictionVO paramVo, Map<String, FzhsqcVO> qcMap) {
        SQLParameter sp = new SQLParameter();
        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select cp.pk_corp_account as pk_accsubj, cp.* from  ynt_cpaccount cp");
        qrysql.append(" where cp.pk_corp = ? and nvl(cp.dr, 0) = 0 and isnum = 'Y' and isleaf = 'Y' ");
        sp.addParam(paramVo.getPk_corp());
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_first())) {
            qrysql.append(" and cp.accountcode >= ? ");
            sp.addParam(paramVo.getKms_first());
        }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_last())) {
            qrysql.append(" and accountcode <= ? ");
            sp.addParam(paramVo.getKms_last());
        }

        List<FzhsqcVO> listvos = (List<FzhsqcVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(FzhsqcVO.class));

        boolean isFzhs = paramVo.getIsfzhs() == null ? false : paramVo.getIsfzhs().booleanValue();

        if (listvos != null && listvos.size() > 0) {
            for (FzhsqcVO vo : listvos) {
                String key = isFzhs ? vo.getPk_accsubj() + ReportUtil.getFzKey(vo)
                        : vo.getPk_accsubj();
                if (!qcMap.containsKey(key)) {
                    qcMap.put(key, vo);
                }
            }
        }
    }

    //第一次加工
    public List<FzhsqcVO> hashlizeObject(List<FzhsqcVO> tempylist) {
        List<FzhsqcVO> result = new ArrayList<FzhsqcVO>();
        Map<String, FzhsqcVO> jMap = new HashMap<String, FzhsqcVO>();
        if (tempylist == null || tempylist.size() == 0) {
            return result;
        }
        String key = null;
        for (FzhsqcVO v : tempylist) {
            key = v.getPk_accsubj();
            if (!jMap.containsKey(key)) {
                jMap.put(key, v);
            } else {
                DZFDouble mny = SafeCompute.add(v.getThismonthqc(), jMap.get(key).getThismonthqc());
                DZFDouble num = SafeCompute.add(v.getMonthqmnum(), jMap.get(key).getMonthqmnum());
                jMap.get(key).setThismonthqc(mny);
                jMap.get(key).setMonthqmnum(num);
            }
        }
        for (Map.Entry<String, FzhsqcVO> entry : jMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    // 根据科目pk生成Map
    private Map<String, FzhsqcVO> hashlizeObject(List<FzhsqcVO> vos,
                                                 boolean isFzhs) {
        Map<String, FzhsqcVO> result = new HashMap<String, FzhsqcVO>();
        if (vos == null || vos.size() == 0) {
            return result;
        }
        for (FzhsqcVO vo : vos) {
            String key = isFzhs ? vo.getPk_accsubj() + ReportUtil.getFzKey(vo)
                    : vo.getPk_accsubj();
            if (!result.containsKey(key)) {
                result.put(key, vo);
            }
        }
        return result;
    }

    /**
     * 查询明细数据
     */
    private List<NumMnyGlVO> queryDetailVOs(DZFDate jzDate,
                                            QueryCondictionVO paramVo) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(paramVo.getPk_corp());
        sp.addParam(DateUtils.getPeriod(jzDate));
        sp.addParam(paramVo.getQjz());
        String pk_bz = paramVo.getPk_currency();
        DZFBoolean ishasjz = paramVo.getIshasjz();
        StringBuffer sf = new StringBuffer();
        sf.append(" select period as opdate, b.pk_accsubj as pk_subject,b.vcode as kmbm, ");
        sf.append(" b.kmmchie as kmmc, b.nnumber, b.jfmny, b.dfmny ");
        if (paramVo.getIsfzhs() != null && paramVo.getIsfzhs().booleanValue()) {
            sf.append(" , b.fzhsx1, b.fzhsx2, b.fzhsx3, b.fzhsx4, b.fzhsx5, ");
            sf.append(" case when b.fzhsx6 is null then b.pk_inventory end fzhsx6, ");
            sf.append("  b.fzhsx7, b.fzhsx8, b.fzhsx9, b.fzhsx10  ");
        }
        sf.append(" from ynt_tzpz_b b ");
        sf.append(" join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h ");
        sf.append(" join ynt_cpaccount ct on b.pk_accsubj = ct.pk_corp_account ");
        sf.append(" where b.pk_corp = ? ");
        sf.append(" and nvl(b.dr,0) = 0 and (nvl(ct.isnum,'N') = 'Y' or b.nnumber > 0 or b.nnumber < 0 ) and h.period >= ? and h.period <= ? ");
        if (pk_bz != null && pk_bz.length() > 0) {
            sf.append(" and b.pk_currency = ? ");
            sp.addParam(pk_bz);
        }
        if (ishasjz != null && ishasjz.booleanValue()) {
            sf.append(" and h.ishasjz = 'Y' ");
        }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_first())) {
            sf.append(" and b.vcode >= ? ");
            sp.addParam(paramVo.getKms_first());
        }
        if (!StringUtil.isEmptyWithTrim(paramVo.getKms_last())) {
            sf.append(" and b.vcode <= ? ");
            sp.addParam(paramVo.getKms_last());
            sf.append(" and b.vcode in('1403','1405') ");
        }
        List<NumMnyGlVO> list = (List<NumMnyGlVO>) singleObjectBO.executeQuery(
                sf.toString(), sp, new BeanListProcessor(NumMnyGlVO.class));
        bulidData(list);
        return list;
    }

    // 转换成数量金额总账数据
    private void bulidData(List<NumMnyGlVO> list) {
        if (list == null || list.size() == 0)
            return;
        for (NumMnyGlVO v : list) {
            DZFDouble num = v.getNnumber();
            if (v.getJfmny() != null && v.getJfmny().doubleValue() > 0) {
                v.setBqjfnum(num);
                v.setBqjfmny(v.getJfmny());
            } else if (v.getJfmny() != null && v.getJfmny().doubleValue() < 0) {
                v.setBqjfnum(num);
                v.setBqjfmny(v.getJfmny());
            } else if (v.getDfmny() != null && v.getDfmny().doubleValue() > 0) {
                v.setBqdfnum(num);
                v.setBqdfmny(v.getDfmny());
            } else if (v.getDfmny() != null && v.getDfmny().doubleValue() < 0) {
                v.setBqdfnum(num);
                v.setBqdfmny(v.getDfmny());
            }
        }
    }

    /**
     * 过滤结果并赋值
     *
     * @param result
     * @param paramvo
     * @param map
     * @return
     * @throws DZFWarpException
     */
    private List<NumMnyGlVO> filterAndSetValue(Map<String, NumMnyGlVO> result,
                                               QueryCondictionVO paramvo, Map<String, YntCpaccountVO> map)
            throws DZFWarpException {

        GxhszVO myselfset = zxkjPlatformService.queryGxhszVOByPkCorp(paramvo.getPk_corp());// 个性化设置vo
        Integer subjectShow = myselfset.getSubjectShow();

        Map<String, AuxiliaryAccountBVO> auaccountMap = zxkjPlatformService.queryAuxiliaryAccountBVOMap(paramvo.getPk_corp());
        Map<String, InventoryVO> inventoryMap = queryInventory(paramvo.getPk_corp());
        boolean isFzhs = paramvo.getIsfzhs() != null && paramvo.getIsfzhs().booleanValue();
        List<NumMnyGlVO> resultList = new ArrayList<NumMnyGlVO>();
        Integer cjq = paramvo.getCjq() == null ? 1 : paramvo.getCjq();
        Integer cjz = paramvo.getCjz() == null ? 10 : paramvo.getCjz();
        for (String rs_key : result.keySet()) {
            String[] keyArray = rs_key.split(",");
            NumMnyGlVO vo = result.get(rs_key);
            YntCpaccountVO account = map.get(keyArray[0]);
            if (account.getAccountlevel() >= cjq
                    && account.getAccountlevel() <= cjz && !checkEmpty(vo)) {

                vo.setKmbm(account.getAccountcode());
                vo.setDw(account.getMeasurename());
                vo.setKmmc(getKmmcBySetting(subjectShow, account));
                if (isFzhs && rs_key.length() > 24) {
                    String[] fzStr = getFzCombStr(keyArray, auaccountMap, inventoryMap);
                    vo.setKmbm(vo.getKmbm() + fzStr[0]);
                    vo.setKmmc(vo.getKmmc() + fzStr[1]);
                    if (!StringUtil.isEmpty(fzStr[2])) {
                        vo.setDw(fzStr[2]);
                    }
                }
                vo.setDir(account.getDirection().toString());

                DZFDouble qcPrice = SafeCompute.div(vo.getQcmny(),
                        vo.getQcnum());
                vo.setQcprice(qcPrice.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP));
                DZFDouble qmPrice = SafeCompute.div(vo.getQmmny(),
                        vo.getQmnum());
                vo.setQmprice(qmPrice.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP));
                DZFDouble bqjfprice = SafeCompute.div(vo.getBqjfmny(),
                        vo.getBqjfnum());
                vo.setBqjfprice(bqjfprice.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP));
                DZFDouble bqdfprice = SafeCompute.div(vo.getBqdfmny(),
                        vo.getBqdfnum());
                vo.setBqdfmny(bqdfprice.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP));

                vo.setBeginqj(paramvo.getQjq());
                vo.setEndqj(paramvo.getQjz());
                resultList.add(vo);
            }

        }
        return resultList;
    }

    private Map<String, InventoryVO> queryInventory(String pk_corp) {
        Map<String, InventoryVO> inventoryMap = new HashMap<String, InventoryVO>();
        List<InventoryVO> list = zxkjPlatformService.queryInventoryVOs(pk_corp);
        if (list != null && list.size() > 0) {
            for (InventoryVO vo : list) {
                inventoryMap.put(vo.getPrimaryKey(), vo);
            }
        }
        return inventoryMap;
    }

    //根据个性化设置获取科目名称
    private String getKmmcBySetting(Integer subjectShow, YntCpaccountVO account) {
        String kmmc = null;
        if (subjectShow.intValue() == 0) {
            // 显示本级
            kmmc = account.getAccountname();
        } else if (subjectShow.intValue() == 1) {
            // 显示一级+本级
            String[] tempkms = account.getFullname().split("/");
            if (tempkms.length > 1) {
                kmmc = tempkms[0] + "_" + tempkms[tempkms.length - 1];
            } else if (tempkms.length == 1) {
                kmmc = tempkms[0];
            }
        } else {
            // 逐级显示
            kmmc = account.getFullname().replaceAll("/", "_");
        }
        return kmmc;
    }

    //获取辅助核算编码、名称、单位
    private String[] getFzCombStr(String[] keyArray, Map<String, AuxiliaryAccountBVO> auaccountMap,
                                  Map<String, InventoryVO> inventoryMap) {
        String[] str = new String[3];
        StringBuffer code = new StringBuffer();
        StringBuffer name = new StringBuffer();
        AuxiliaryAccountBVO auaccount = null;
        InventoryVO inventory = null;
        for (int i = 1, len = keyArray.length; i < len; i++) {
            inventory = inventoryMap.get(keyArray[i]);
            if (inventory != null) {//存货
                code.append("_");
                code.append(inventory.getCode());
                name.append("_");
                name.append(inventory.getName());

                if (!StringUtil.isEmpty(inventory.getInvspec())) {
                    name.append("(").append(inventory.getInvspec()).append(")");
                }

                if (!StringUtil.isEmpty(inventory.getMeasurename()))//设置计量单位
                    str[2] = inventory.getMeasurename();
            } else {//辅助核算
                auaccount = auaccountMap.get(keyArray[i]);
                if (auaccount == null)
                    continue;
                code.append("_");
                code.append(auaccount.getCode());
                name.append("_");
                name.append(auaccount.getName());
                if (!StringUtil.isEmpty(auaccount.getUnit()))
                    str[2] = auaccount.getUnit();
            }

        }
        str[0] = code.toString();
        str[1] = name.toString();
        return str;
    }

    //检查数据是否为空
    private boolean checkEmpty(NumMnyGlVO vo) {
        return (vo.getQcmny() == null || vo.getQcmny().doubleValue() == 0)
                && (vo.getQcnum() == null || vo.getQcnum().doubleValue() == 0)
                && (vo.getBqdfmny() == null || vo.getBqdfmny().doubleValue() == 0)
                && (vo.getBqdfnum() == null || vo.getBqdfnum().doubleValue() == 0)
                && (vo.getBqjfmny() == null || vo.getBqjfmny().doubleValue() == 0)
                && (vo.getBqjfnum() == null || vo.getBqjfnum().doubleValue() == 0)
                && (vo.getBndfmny() == null || vo.getBndfmny().doubleValue() == 0)
                && (vo.getBndfnum() == null || vo.getBndfnum().doubleValue() == 0)
                && (vo.getBnjfmny() == null || vo.getBnjfmny().doubleValue() == 0)
                && (vo.getBnjfnum() == null || vo.getBnjfnum().doubleValue() == 0);
    }
}
