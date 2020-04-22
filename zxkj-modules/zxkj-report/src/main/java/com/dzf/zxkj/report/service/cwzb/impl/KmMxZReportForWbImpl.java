package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmQmJzExtVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IKmMxZReportForWb;
import com.dzf.zxkj.report.utils.BeanUtils;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 外币的科目明细查询
 *
 * @author zhangj
 */
@Service("gl_rep_kmmxjservwb")
@SuppressWarnings("all")
public class KmMxZReportForWbImpl implements IKmMxZReportForWb {

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private SingleObjectBO singleObjectBO;

    private KmQmJzExtVO getKey(HashMap<String, HashMap<String, KmQmJzExtVO>> map, String pk_accsubj, String period) {
        HashMap<String, KmQmJzExtVO> m = map.get(pk_accsubj);
        if (m == null) {
            m = new HashMap<String, KmQmJzExtVO>();
            map.put(pk_accsubj, m);
        }
        KmQmJzExtVO v = m.get(period);
        if (v == null) {
            v = KmQmJzExtVO.newInstance();
            v.setPeriod(period);
            v.setPk_accsubj(pk_accsubj);
            m.put(period, v);
        }
        return v;
    }

    public QcYeVO[] getLastVO(QcYeVO[] qcyeVOs, Map<String, YntCpaccountVO> mpz) {
        if (qcyeVOs == null || qcyeVOs.length == 0 || mpz == null
                || mpz.size() == 0)
            return null;
        Map<String, YntCpaccountVO> mp = new HashMap<String, YntCpaccountVO>();
        mp.putAll(mpz);
        List<QcYeVO> list = new ArrayList<QcYeVO>();
        String pksubj = null;
        for (QcYeVO c : qcyeVOs) {
            pksubj = c.getPk_accsubj();
            mp.remove(pksubj);
            list.add(c);
        }
        Collection<YntCpaccountVO> c = mp.values();
        if (c != null && c.size() > 0) {
            QcYeVO vs = null;
            for (YntCpaccountVO v : c) {
                vs = new QcYeVO();
                vs.setPk_accsubj(v.getPk_corp_account());
                list.add(vs);
            }
        }
        return list.toArray(new QcYeVO[0]);

    }

    public Object[] getKmMxZVOs2(QueryParamVO vo) throws DZFWarpException {
        boolean b = false;
        DZFDate begindate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(new DZFDate(vo.getBegindate1().getYear() + "-01-01")));
        String corpsql = "select begindate from bd_corp where nvl(dr,0)=0 and  pk_corp='" + vo.getPk_corp() + "'";
        DZFDate corpdate = new DZFDate((String) singleObjectBO.executeQuery(corpsql,
                new SQLParameter(), new ColumnProcessor()));

        /** 如果查询的日期在建账日期前，则弹出提示框 */
        if (vo.getEnddate() != null && vo.getBegindate1() != null
                && vo.getEnddate().before(vo.getBegindate1())) {
            throw new BusinessException("查询开始日期，应该在查询结束日期前!");
        }
        if ((vo.getEnddate() != null && vo.getEnddate().before(corpdate))) {
            if (vo.getBegindate1().getYear() < corpdate.getYear()) {
                throw new BusinessException("建账日期不在查询期间内！");
            }
        }
        String jz_max_period = getMAXPeriod(vo.getPk_corp(), begindate);// 开始日期之前最大结账期
        String min_period = DateUtils.getPeriod(begindate);
        String kmwhere = getKmTempTable(vo);
        HashMap<String, HashMap<String, KmQmJzExtVO>> map = new HashMap<String, HashMap<String, KmQmJzExtVO>>();

        String pk_corp = vo.getPk_corp();
        KMQMJZVO[] kmqmjzvos = null;
        DZFDate qcstart = null;
        DZFDate qcend = null;
        int len = 0;
        KMQMJZVO kv = null;
        Map<String, YntCpaccountVO> mp = getKM(pk_corp, kmwhere);

        /** 期初数据（key= 科目key +N个辅助项目key(1~9)） */
        HashMap<String, KmMxZVO> qcfzmap = new HashMap<String, KmMxZVO>();

        /** 期初数据（key= 科目key +N个辅助项目key(1~9)） */
        HashMap<String, DZFDouble[]> corpbegqcmap = new HashMap<String, DZFDouble[]>();

        /** 发生数据（key= 科目key +N个辅助项目key(1~9)） */
        HashMap<String, List<KmMxZVO>> fsfzmap = new HashMap<String, List<KmMxZVO>>();

        Map<String, AuxiliaryAccountBVO> fzmap = getFzXm(pk_corp);

        /** 期初余额 */
        if (jz_max_period == null || corpdate.after(begindate)) {
            String kmwhere1 = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where  (1=1)"
                    + kmwhere + ")";
            QcYeVO[] qcyeVOs = null;
            String qcyesql = "select a.* from YNT_QCYE a inner join ynt_cpaccount  b on a.pk_accsubj=b.pk_corp_account where   a.pk_accsubj in"
                    + kmwhere1
                    + " and a.pk_corp=? and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(a.pk_currency,'" + DzfUtil.PK_CNY + "')=?";
            SQLParameter sp = new SQLParameter();
            sp.addParam(vo.getPk_corp());
            sp.addParam(vo.getPk_currency());
            List<QcYeVO> listqcvo = (List<QcYeVO>) singleObjectBO.executeQuery(
                    qcyesql, sp, new BeanListProcessor(
                            QcYeVO.class));
            qcyeVOs = listqcvo.toArray(new QcYeVO[0]);
            qcyeVOs = getLastVO(qcyeVOs, mp);
            qcend = begindate.getDateBefore(1);// begindate;
            len = qcyeVOs == null ? 0 : qcyeVOs.length;
            QcYeVO v = null;
            for (int i = 0; i < len; i++) {
                v = qcyeVOs[i];
                kv = getKey(map, v.getPk_accsubj(), min_period);
                if (corpdate.getYear() > vo.getBegindate1().getYear()
                        || (corpdate.getYear() == vo.getBegindate1().getYear() && corpdate
                        .getMonth() > begindate.getMonth())) {
                    kv.setThismonthqc(v.getYearqc());
                    kv.setYbthismonthqc(v.getYbyearqc());
                } else {
                    kv.setThismonthqc(v.getThismonthqc());
                    kv.setThismonthqm(v.getThismonthqc());
                    kv.setYbthismonthqc(v.getYbthismonthqc());
                    kv.setYbthismonthqm(v.getYbthismonthqc());
                }
            }
            qcyeVOs = null;
            sp.clearParams();
            sp.addParam(vo.getPk_corp());
            sp.addParam(vo.getPk_currency());
            if (vo.getSfzxm() != null && vo.getSfzxm().booleanValue()) {
                FzhsqcVO[] fzhsqcvos = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class, " nvl(dr,0)=0 and pk_corp = ? and nvl(pk_currency,'" + DzfUtil.PK_CNY + "')=?  ", sp);
                putQCFzMap(fzhsqcvos, mp, qcfzmap, corpbegqcmap, fzmap, begindate, corpdate);
            }
        } else if (min_period.compareTo(jz_max_period) > 0) {/** 已结账 */
            String kmwhere1 = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where 1=1 "
                    + kmwhere + ")";
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam(jz_max_period);
            sp.addParam(vo.getPk_currency());
            kmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(
                    KMQMJZVO.class,
                    " pk_accsubj in" + kmwhere1 + " and pk_corp=?  and period=? and nvl(dr,0)=0 and nvl(pk_currency,'" + DzfUtil.PK_CNY + "')=? order by period",
                    sp);
            qcend = begindate.getDateBefore(1);
            qcstart = new DZFDate(jz_max_period + "-01");
            qcstart = new DZFDate(new Date(qcstart.toDate().getYear(), qcstart.toDate().getMonth() + 1, 1));

            len = kmqmjzvos == null ? 0 : kmqmjzvos.length;
            KMQMJZVO v = null;
            for (int i = 0; i < len; i++) {
                v = kmqmjzvos[i];
                kv = getKey(map, v.getPk_accsubj(), min_period);
                kv.setThismonthqc(v.getThismonthqm());
                kv.setThismonthqm(v.getThismonthqm());
                kv.setYbthismonthqc(v.getYbthismonthqm());
                kv.setYbthismonthqm(v.getYbthismonthqm());
            }

            if (vo.getSfzxm() != null && vo.getSfzxm().booleanValue()) {
                /** 期初数据赋值 */
                putNJQCFzMap(kmqmjzvos, mp, qcfzmap, fzmap, begindate, corpdate);
            }

            kmqmjzvos = null;
            upTotal(min_period, mp, map);

        } else {/** 已结账 */
            String kmwhere1 = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where 1=1  " + kmwhere + ")";
            String nextperiod = String.valueOf((Integer.parseInt(jz_max_period.substring(0, 4)) + 1));
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam((nextperiod + "-12"));
            sp.addParam((nextperiod + "-12"));
            sp.addParam(vo.getPk_currency());
            kmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(
                    KMQMJZVO.class,
                    " pk_accsubj in" + kmwhere1 + " and pk_corp=? and period>=? and period<=? and nvl(dr,0)=0   and nvl(pk_currency,'" + DzfUtil.PK_CNY + "')=? order by period ",
                    new SQLParameter());
            len = kmqmjzvos == null ? 0 : kmqmjzvos.length;
            KMQMJZVO v = null;
            for (int i = 0; i < len; i++) {
                v = kmqmjzvos[i];
                kv = getKey(map, v.getPk_accsubj(), nextperiod + "-01");

                kv.setThismonthqc(v.getThismonthqc());
                kv.setThismonthqm(v.getThismonthqc());

                kv.setYbthismonthqc(v.getYbthismonthqc());
                kv.setYbthismonthqm(v.getYbthismonthqc());
            }

            if (vo.getSfzxm() != null && vo.getSfzxm().booleanValue()) {
                /** 期初数据赋值 */
                putNJQCFzMap(kmqmjzvos, mp, qcfzmap, fzmap, begindate, corpdate);
            }

            kmqmjzvos = null;
            upTotal(nextperiod + "-01", mp, map);
        }
        DZFDouble ufd = null;

        boolean bb = qcstart != null || qcend != null;
        if (bb && qcstart != null && qcend != null) {
            bb = qcstart.compareTo(qcend) < 0;
        }

        if (bb) {// 期初
            List<KmZzVO> vec0 = null;
            vec0 = getQCKmFSByPeriod(pk_corp, vo.getIshasjz(), vo.getIshassh(),
                    qcstart, qcend, kmwhere, vo.getPk_currency());
            sumFsToQC(min_period, mp, map, vec0);

            if (vo.getSfzxm() != null && vo.getSfzxm().booleanValue()) {
                putFsToQCMap(vec0, mp, qcfzmap, fzmap);
            }

        }
        HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap = new HashMap<String, HashMap<String, List<KmMxZVO>>>();
        /** 期初的"本期发生借方","本期发生贷方" */
        List<KmMxZVO> vec = null;
        if (corpdate.after(begindate)) {
            vec = getKmFSByPeriodQC(pk_corp, vo.getIshasjz(), vo.getIshassh(),
                    begindate, vo.getEnddate(), kmwhere, vo.getPk_currency(), vo.getSfzxm());
        } else {
            vec = getKmFSByPeriod(pk_corp, vo.getIshasjz(), vo.getIshassh(),
                    begindate, vo.getEnddate(), kmwhere, vo.getPk_currency());
        }

        /** 把vec的值 变成一个map赋值用 */
        Map<String, HashMap<String, List<KmMxZVO>>> vecmap = new HashMap<String, HashMap<String, List<KmMxZVO>>>();
        StringBuffer key = null;
        String tempkey = null;
        KmMxZVO fzmxzvo = null;
        for (KmMxZVO mxvo : vec) {
            String keytemp1 = mxvo.getRq().substring(0, 7);
            String keytemp2 = mp.get(mxvo.getKm()).getAccountcode();
            if (vecmap.containsKey(keytemp1)) {
                if (vecmap.get(keytemp1).containsKey(keytemp2)) {
                    vecmap.get(keytemp1).get(keytemp2).add(mxvo);
                } else {
                    List<KmMxZVO> listtemp = new ArrayList<KmMxZVO>();
                    listtemp.add(mxvo);
                    vecmap.get(keytemp1).put(keytemp2, listtemp);
                }
            } else {
                List<KmMxZVO> listtemp = new ArrayList<KmMxZVO>();
                listtemp.add(mxvo);
                HashMap<String, List<KmMxZVO>> submap = new HashMap<String, List<KmMxZVO>>();
                submap.put(keytemp2, listtemp);
                vecmap.put(keytemp1, submap);
            }

            if (vo.getSfzxm() != null && vo.getSfzxm().booleanValue()) {
                key = new StringBuffer();
                key.append(mxvo.getKm() + "_");

                for (int i = 1; i < 11; i++) {
                    tempkey = (String) mxvo.getAttributeValue("fzhsx" + i);
                    if (!StringUtil.isEmpty(tempkey) && !"0".equals(tempkey)) {
                        key.append(tempkey + "_");
                    }
                }

                key = new StringBuffer(key.subSequence(0, key.length() - 1));
                /** 辅助项目处理 */
                if (key.length() > 24) {
                    fzmxzvo = new KmMxZVO();
                    BeanUtils.copyProperties(mxvo, fzmxzvo);
                    fzmxzvo.setPk_accsubj(key.toString());
                    if (fsfzmap.containsKey(key.toString())) {
                        fsfzmap.get(key.toString()).add(fzmxzvo);
                    } else {
                        List<KmMxZVO> listtempvo = new ArrayList<KmMxZVO>();
                        listtempvo.add(fzmxzvo);
                        fsfzmap.put(key.toString(), listtempvo);
                    }
                }
            }
        }

        len = vec == null ? 0 : vec.size();
        KmMxZVO vv = null;
        List<String> lsp = null;
        int len1 = 0;
        List<String> kmparentlist = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            vv = vec.get(i);
            kmparentlist.add(vv.getKm() + "_" + DateUtils.getPeriod(new DZFDate(vv.getRq())));
            lsp = getKmParent(mp, vv.getKm());
            len1 = lsp == null ? 0 : lsp.size();
            kv = getKey(map, vv.getKm(), DateUtils.getPeriod(new DZFDate(vv.getRq())));
            ufd = VoUtils.getDZFDouble(kv.getJffse());
            ufd = ufd.add(VoUtils.getDZFDouble(vv.getJf()));
            kv.setJffse(ufd);

            ufd = VoUtils.getDZFDouble(kv.getDffse());
            ufd = ufd.add(VoUtils.getDZFDouble(vv.getDf()));
            kv.setDffse(ufd);


            /** -------------原币------------ */
            ufd = VoUtils.getDZFDouble(kv.getYbjffse());
            ufd = ufd.add(VoUtils.getDZFDouble(vv.getYbjf()));
            kv.setYbjffse(ufd);

            ufd = VoUtils.getDZFDouble(kv.getYbdffse());
            ufd = ufd.add(VoUtils.getDZFDouble(vv.getYbdf()));
            kv.setYbdffse(ufd);


            put(fsmap, vv);
            for (int j = 0; j < len1; j++) {
                kv = getKey(map, lsp.get(j), DateUtils.getPeriod(new DZFDate(vv.getRq())));
                ufd = VoUtils.getDZFDouble(kv.getJffse());
                ufd = ufd.add(VoUtils.getDZFDouble(vv.getJf()));
                kv.setJffse(ufd);

                ufd = VoUtils.getDZFDouble(kv.getDffse());
                ufd = ufd.add(VoUtils.getDZFDouble(vv.getDf()));
                kv.setDffse(ufd);

                /** -----------原币---------- */
                ufd = VoUtils.getDZFDouble(kv.getYbjffse());
                ufd = ufd.add(VoUtils.getDZFDouble(vv.getYbjf()));
                kv.setYbjffse(ufd);

                ufd = VoUtils.getDZFDouble(kv.getYbdffse());
                ufd = ufd.add(VoUtils.getDZFDouble(vv.getYbdf()));
                kv.setYbdffse(ufd);

                kmparentlist.add(lsp.get(j) + "_" + DateUtils.getPeriod(new DZFDate(vv.getRq())));
            }
        }
        vec = null;
        List[] ls = tzDate(map, ReportUtil.getPeriods(begindate, vo.getEnddate()), mp,
                fsmap, kmparentlist, vo.getXswyewfs(), b, vo.getIshowfs(), vo.getIsnomonthfs(), vo.getBtotalyear());
        return new Object[]{ls, mp, vecmap, qcfzmap, fsfzmap, corpbegqcmap};

    }


    /**
     * 获取辅助项目的key
     *
     * @param pk_corp
     * @return
     */
    private Map<String, AuxiliaryAccountBVO> getFzXm(String pk_corp) {
        Map<String, AuxiliaryAccountBVO> resmap = new HashMap<String, AuxiliaryAccountBVO>();
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryAllB(pk_corp);
        if (bvos != null && bvos.length > 0) {
            for (AuxiliaryAccountBVO bvo : bvos) {
                resmap.put(bvo.getPrimaryKey(), bvo);
            }
        }
        return resmap;
    }

    private void put(HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap,
                     KmMxZVO vo) {
        HashMap<String, List<KmMxZVO>> obj = fsmap.get(vo.getKm());
        if (obj == null) {
            obj = new HashMap<String, List<KmMxZVO>>();
            fsmap.put(vo.getKm(), obj);
        }
        String p = DateUtils.getPeriod(new DZFDate(vo.getRq()));
        List<KmMxZVO> l = obj.get(p);
        if (l == null) {
            l = new ArrayList<KmMxZVO>();
            obj.put(p, l);
        }
        l.add(vo);
    }

    private Map<String, YntCpaccountVO> getKM(String pk_corp, String kmwhere)
            throws DZFWarpException {
        /** 个性化设置vo */
        GxhszVO myselfset = zxkjPlatformService.queryGxhszVOByPkCorp(pk_corp);
        /** 科目现在方式，默认显示本级 */
        Integer subjectShow = myselfset.getSubjectShow();
        kmwhere = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where (1=1) "
                + kmwhere + ")";
        YntCpaccountVO[] kmVOs = (YntCpaccountVO[]) singleObjectBO
                .queryByCondition(YntCpaccountVO.class, " pk_corp='" + pk_corp + "' and pk_corp_account in " + kmwhere + "",
                        new SQLParameter());
        Arrays.sort(kmVOs, new Comparator<YntCpaccountVO>() {
            @Override
            public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
                if (StringUtil.isEmpty(o1.getAccountcode())) {
                    return -1;
                }
                return o1.getAccountcode().compareTo(o2.getAccountcode());
            }
        });
        int len = kmVOs == null ? 0 : kmVOs.length;
        Map<String, YntCpaccountVO> mp = new HashMap<String, YntCpaccountVO>();
        for (int i = 0; i < len; i++) {
            /** 显示本级 */
            if (subjectShow.intValue() == 0) {

            } else if (subjectShow.intValue() == 1) {/** 显示一级+本级 */
                String firstname = "";
                if (kmVOs[i].getAccountcode().length() > 4) {/** 非一级 */
                    for (YntCpaccountVO cpaccVo1 : kmVOs) {
                        if (cpaccVo1.getAccountcode().equals(kmVOs[i].getAccountcode().substring(0, 4))) {
                            firstname = cpaccVo1.getAccountname() + "_";
                            break;
                        }
                    }
                }

                kmVOs[i].setAccountname(firstname + kmVOs[i].getAccountname());
            } else {/** 逐级显示 */
                StringBuffer parentfullname = new StringBuffer();
                for (YntCpaccountVO cpaccVo1 : kmVOs) {
                    if (kmVOs[i].getAccountcode().startsWith(cpaccVo1.getAccountcode())
                            && ((kmVOs[i].getAccountcode().length() - cpaccVo1.getAccountcode().length()) == 2
                            || (kmVOs[i].getAccountcode().length() - cpaccVo1.getAccountcode().length()) == 3)) {/** 只是找最近的 */
                        parentfullname.append(cpaccVo1.getAccountname() + "_");
                        break;
                    }
                }
                kmVOs[i].setAccountname(parentfullname.toString() + kmVOs[i].getAccountname());
            }
            mp.put(kmVOs[i].getPk_corp_account(), kmVOs[i]);
        }
        return mp;
    }

    String getKmTempTable(QueryParamVO vo) {
        String pk_corp = vo.getPk_corp();
        String kms = vo.getKms();
        String kmsx = vo.getKmsx();
        String kms_first = vo.getKms_first();
        StringBuffer wherpartfirst = new StringBuffer();
        StringBuffer kmwhere = new StringBuffer();
        /** 查询所有科目，总账只查询一级科目 */
        String jcSql = "";
        if (vo.getIsLevel().booleanValue()) {
            jcSql = " and accountlevel=1 ";
            if (vo.getIsmj().booleanValue()) {
                /** 只查询末级 */
                jcSql = " and isleaf='Y' ";
            } else if (vo.getLevelq() != null && vo.getLevelz() != null) {
                /** 查询级次 */
                jcSql = " and ( accountlevel>=" + vo.getLevelq() + " and accountlevel<=" + vo.getLevelz() + " ) ";
            }
        }
        String sx = "";
        /** 查询所有科目 */
        if (kmsx != null && !"".equals(kmsx)) {
            if (kmsx.indexOf(",") > 0) {
                String kmsxs = kmsx.substring(0, kmsx.length() - 1);
                sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and a.accountkind in (" + kmsxs + ") and nvl(a.dr,0)=0 ";
            } else {
                sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and a.accountkind =" + kmsx + " and nvl(a.dr,0)=0";
            }
        } else {
            sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and nvl(a.dr,0)=0";
        }
        if (kms != null && kms.length() > 0) {
            if (kms.indexOf(",") > 0) {
                StringBuffer wherpart1 = new StringBuffer();
                for (int i = 0; i < vo.getKmcodelist().size(); i++) {
                    wherpart1.append(" a.accountcode like '" + vo.getKmcodelist().get(i) + "%' or");
                }
                kmwhere.append(" and  (" + wherpart1.toString().substring(0, wherpart1.length() - 2) + ")" + " and nvl(a.dr,0)=0");
            } else {/** 查询单个科目 */
                kmwhere.append(" and  a.pk_corp_account='" + kms + "'");
                StringBuffer wherpart1 = new StringBuffer();
                wherpart1.append("  a.accountcode like '" + kms + "%' ");
                kmwhere.append(" and (" + wherpart1.toString() + ")" + " and nvl(a.dr,0)=0");

            }
        }
        if (kms_first != null && kms_first.length() > 0) {
            wherpartfirst.append("  a.accountcode >= '" + kms_first + "' ");
            kmwhere.append(" and (" + wherpartfirst.toString() + ")" + " and nvl(a.dr,0)=0");
        }


        return kmwhere.toString() + sx;
    }

    public List<KmZzVO> getQCKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,
                                          DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere,
                                          String pk_currency) throws DZFWarpException {
        if (start == null && end == null)
            return null;
        StringBuffer sb = new StringBuffer();

        sb.append(" select sum(b.jfmny) as jf ,sum(b.dfmny) as df, ");
        sb.append(" sum(b.ybjfmny) as ybjf,sum(b.ybdfmny) ybdf, ");
        sb.append(" b.pk_accsubj as km  ");
        sb.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
        sb.append(" inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account   ");
        sb.append(" where nvl(b.dr,0)=0 ");
        Date dd = null;
        if (start != null) {
            dd = start.toDate();
            dd = new Date(dd.getYear(), dd.getMonth(), 1);
            DZFDate d1 = new DZFDate(dd);
            sb.append(" and h.doperatedate>='").append(d1).append("'");
        }
        if (end != null) {
            dd = end.toDate();
            dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
            DZFDate d1 = new DZFDate(dd);
            sb.append(" and h.doperatedate<='").append(d1).append("'");
        }
        if (!StringUtil.isEmptyWithTrim(kmwhere)) {
            sb.append("  ").append(kmwhere);
        }

        sb.append(" and nvl(b.pk_currency,'" + DzfUtil.PK_CNY + "')='" + pk_currency + "'");

        sb.append(" and h.pk_corp='" + pk_corp + "'");

        if (ishasjz.booleanValue()) {
            /** 不包含未记账，即只查询已记账的 */
            sb.append(" and h.ishasjz='Y' ");
            sb.append(" and h.vbillstatus=1 ");
        }
        sb.append(" group by b.pk_accsubj ");

        List<KmZzVO> reslist = (List<KmZzVO>) singleObjectBO.executeQuery(sb.toString(),
                new SQLParameter(), new BeanListProcessor(KmZzVO.class));
        Collections.sort(reslist, new Comparator<KmZzVO>() {
            @Override
            public int compare(KmZzVO o1, KmZzVO o2) {
                if (StringUtil.isEmpty(o1.getPk_accsubj())) {
                    return -1;
                }
                return o1.getPk_accsubj().compareTo(o2.getPk_accsubj());
            }
        });
        return reslist;
    }

    public List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,
                                         DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere,
                                         String pk_currency) throws DZFWarpException {
        String sql1 = getQuerySqlByPeriod(start, end, kmwhere, pk_corp,
                ishasjz, ishassh, pk_currency);
        ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1,
                new SQLParameter(), new BeanListProcessor(KmMxZVO.class));

        List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
        if (result1 != null && !result1.isEmpty()) {
            for (Object o : result1) {
                KmMxZVO vo = (KmMxZVO) o;
                vo.setJf(VoUtils.getDZFDouble(vo.getJf()));
                vo.setDf(VoUtils.getDZFDouble(vo.getDf()));
                vo.setYbjf(VoUtils.getDZFDouble(vo.getYbjf()));
                vo.setYbdf(VoUtils.getDZFDouble(vo.getYbdf()));
                if ("0".equals(vo.getFx())) {
                    vo.setFx("借");
                } else {
                    vo.setFx("贷");
                }

                vec_details.add(vo);
            }
        }
        return vec_details;
    }

    private String getMAXPeriod(String pk_corp, DZFDate enddate)
            throws DZFWarpException {
        String period = DateUtils.getPeriod(enddate);
        String sql = "select max(period) from YNT_QMJZ where pk_corp='"
                + pk_corp + "' and period<'" + period
                + "' and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ";
        Object[] obj = (Object[]) singleObjectBO.executeQuery(sql,
                new SQLParameter(), new ArrayProcessor());
        if (obj != null && obj.length > 0) {
            String i = (String) obj[0];
            return i;
        }
        return null;
    }

    private List[] tzDate(HashMap<String, HashMap<String, KmQmJzExtVO>> map, List<String> period,
                          Map<String, YntCpaccountVO> mp, HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap,
                          List<String> kmparentlist, DZFBoolean xswyewfs,
                          boolean isfs, DZFBoolean ishowfs, DZFBoolean isnomonthfs, DZFBoolean btotalyear) {
        if (period == null || period.size() == 0) {
            throw new BusinessException("区间不正确,请检查");
        }

        String showperiod = period.get(0);// DateUtils.getPeriod(showStartDate);
        int len = period == null ? 0 : period.size();
        String p = null;
        KmQmJzExtVO v1 = null;
        DZFDouble ufd = null;
        DZFDouble ufdljjf = null;
        DZFDouble ufdljdf = null;
        DZFDouble uft = null;
        /** ----------原币----- */
        DZFDouble ybufd = null;
        DZFDouble ybufdljjf = null;
        DZFDouble ybufdljdf = null;
        DZFDouble ybuft = null;

        int direction = 0;
        int len1 = map.keySet().size();
        List[] lists = new List[len];
        List<KmMxZVO> list = null;
        List<KmMxZVO> listr = new ArrayList<KmMxZVO>();
        String[] keys = map.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        HashMap<String, KmQmJzExtVO> m = null;
        int month = 0;
        YntCpaccountVO kmvo = null;
        for (int i = 0; i < len1; i++) {
            m = map.get(keys[i]);
            ufd = DZFDouble.ZERO_DBL;
            ybufd = DZFDouble.ZERO_DBL;
            int count = 0;
            Map<String, DZFDouble[]> yearmapjf = new HashMap<String, DZFDouble[]>();
            Map<String, DZFDouble[]> yearmapdf = new HashMap<String, DZFDouble[]>();
            for (int j = 0; j < len; j++) {
                p = period.get(j);
                month = new DZFDate(p + "-01").getMonth();

                v1 = m.get(p);
                if (v1 == null) {
                    v1 = KmQmJzExtVO.newInstance();
                    v1.setPk_accsubj(keys[i].substring(0, 24));
                    v1.setPeriod(p);
                    m.put(p, v1);
                } else {
                    count++;
                }
                if (count == 1) {
                    ufd = VoUtils.getDZFDouble(v1.getThismonthqc());
                    ybufd = VoUtils.getDZFDouble(v1.getYbthismonthqc());
                    count++;
                }

                v1.setThismonthqc(ufd);
                v1.setYbthismonthqc(ybufd);
                kmvo = mp.get(v1.getPk_accsubj());
                if (j == 0) {
                    ufdljjf = DZFDouble.ZERO_DBL;
                    ufdljdf = DZFDouble.ZERO_DBL;

                    ybufdljjf = DZFDouble.ZERO_DBL;
                    ybufdljdf = DZFDouble.ZERO_DBL;
                }
                if (kmvo == null)
                    continue;
                direction = kmvo.getDirection();
                if (ufdljjf == null) {
                    ufdljjf = DZFDouble.ZERO_DBL;
                }
                if (ufdljdf == null) {
                    ufdljdf = DZFDouble.ZERO_DBL;
                }
                /** ----------原币------------ */
                if (ybufdljjf == null) {
                    ybufdljjf = DZFDouble.ZERO_DBL;
                }
                if (ybufdljdf == null) {
                    ybufdljdf = DZFDouble.ZERO_DBL;
                }
                if (direction == 0) {
                    uft = v1.getJffse();
                    ybuft = VoUtils.getDZFDouble(v1.getYbjffse());//原币
                    if (uft == null)
                        uft = DZFDouble.ZERO_DBL;
                    ufdljjf = uft.add(ufdljjf);
                    ybufdljjf = ybuft.add(ybufdljjf);//原币
                    ufd = ufd.add(uft);
                    ybufd = ybufd.add(ybuft);//原币
                    uft = v1.getDffse();
                    ybuft = v1.getYbdffse();//原币
                    if (uft == null)
                        uft = DZFDouble.ZERO_DBL;
                    if (ybuft == null)
                        ybuft = DZFDouble.ZERO_DBL;
                    ufdljdf = uft.add(ufdljdf);
                    ybufdljdf = ybuft.add(ybufdljdf);
                    /**  如果有期初的话不算期初的发生 */
                    ufd = ufd.sub(uft);
                    ybufd = ybufd.sub(ybuft);
                    v1.setThismonthqm(ufd);
                    v1.setYbthismonthqm(ybufd);//原币
                    v1.setLjjffse(ufdljjf);
                    v1.setLjdffse(ufdljdf);

                    v1.setYbljjffse(ybufdljjf);//原币累计借方
                    v1.setYbljdffse(ybufdljdf);//原币累计贷方
                } else {
                    uft = v1.getDffse();
                    ybuft = VoUtils.getDZFDouble(v1.getYbdffse());//原币
                    if (uft == null)
                        uft = DZFDouble.ZERO_DBL;
                    if (ybuft == null)
                        ybuft = DZFDouble.ZERO_DBL;
                    ufdljdf = uft.add(ufdljdf);
                    ybufdljdf = ybuft.add(ybufdljdf);//原币
                    ufd = ufd.add(uft);
                    ybufd = ybufd.add(ybuft);//原币
                    uft = v1.getJffse();
                    ybuft = v1.getYbjffse();//原币借方发生
                    if (uft == null)
                        uft = DZFDouble.ZERO_DBL;
                    if (ybuft == null)
                        ybuft = DZFDouble.ZERO_DBL;
                    ufdljjf = uft.add(ufdljjf);
                    ybufdljjf = ybuft.add(ybufdljjf);//原币
                    ufd = ufd.sub(uft);
                    ybufd = ybufd.sub(ybuft);//原币
                    v1.setThismonthqm(ufd);
                    v1.setLjjffse(ufdljjf);
                    v1.setLjdffse(ufdljdf);

                    //----原币---
                    v1.setYbthismonthqm(ybufd);
                    v1.setYbljjffse(ybufdljjf);
                    v1.setYbljdffse(ybufdljdf);
                }
                if (v1.getPeriod().compareTo(showperiod) >= 0) {
                    if (lists[j] == null)
                        lists[j] = new ArrayList<KmMxZVO>();
                    list = lists[j];


                    if (ishowfs != null && !ishowfs.booleanValue()) {//无发生不显示
                        if ((v1.getJffse() == null || v1.getJffse().doubleValue() == 0)
                                && (v1.getDffse() == null || v1.getDffse().doubleValue() == 0)
                        ) {
                            if ((isnomonthfs != null && isnomonthfs.booleanValue())
                                    || !kmparentlist.contains(v1.getPk_accsubj() + "_" + v1.getPeriod())) {
                                continue;
                            }
                        }
                    }

                    addMxvo(isfs ? listr : list, v1, mp, fsmap, kmparentlist, xswyewfs, isnomonthfs, btotalyear, yearmapjf, yearmapdf);
                }
            }
        }

        return isfs ? new List[]{listr} : lists;

    }


    /**
     * 通过发生补足部分科目期初数据，先生成当前科目期初，之后向上累加发生
     *
     * @param min_period
     * @param mp
     * @param map
     * @param vec0
     */
    private void sumFsToQC(String min_period, Map<String, YntCpaccountVO> mp,
                           HashMap<String, HashMap<String, KmQmJzExtVO>> map, List<KmZzVO> vec0) {
        int len = vec0 == null ? 0 : vec0.size();
        int len1 = 0;
        KmZzVO v = null;
        int ntemp = 0;
        KmQmJzExtVO kv = null;
        DZFDouble ufd = null;
        DZFDouble ufd1 = null;
        DZFDouble ybufd1 = null;//原币发生
        DZFDouble ybufd = null;//原币
        List<String> plist = null;
        String kmpk = null;
        for (int i = 0; i < len; i++) {
            v = vec0.get(i);
            kv = getKey(map, v.getKm(), min_period);
            kmpk = v.getKm();

            plist = getKmParent(mp, kmpk);
            Integer fxleaf = mp.get(kmpk).getDirection();
            ufd = kv.getThismonthqc();
            if (ufd == null)
                ufd = DZFDouble.ZERO_DBL;
            ntemp = mp.get(v.getKm()).getDirection();
            if (ntemp == 0) {
                ufd1 = VoUtils.getDZFDouble(v.getJf()).sub(VoUtils.getDZFDouble(v.getDf()));
                ufd = ufd.add(ufd1);
            } else {
                ufd1 = VoUtils.getDZFDouble(v.getDf()).sub(VoUtils.getDZFDouble(v.getJf()));
                ufd = ufd.add(ufd1);
            }
            kv.setThismonthqc(ufd);
            kv.setThismonthqm(ufd);

            /** ---------------原币------------------ */
            ybufd = VoUtils.getDZFDouble(kv.getYbthismonthqc());
            if (ntemp == 0) {
                ybufd1 = VoUtils.getDZFDouble(v.getYbjf()).sub(VoUtils.getDZFDouble(v.getYbdf()));
                ybufd = ybufd.add(ybufd1);
            } else {
                ybufd1 = VoUtils.getDZFDouble(v.getYbdf()).sub(VoUtils.getDZFDouble(v.getYbjf()));
                ybufd = ybufd.add(ybufd1);
            }
            kv.setYbthismonthqc(ybufd);
            kv.setYbthismonthqm(ybufd);


            len1 = plist == null ? 0 : plist.size();
            for (int j = 0; j < len1; j++) {
                kmpk = plist.get(j);
                Integer pafx = mp.get(kmpk).getDirection();
                kv = getKey(map, kmpk, min_period);
                ufd = VoUtils.getDZFDouble(kv.getThismonthqc());
                if (fxleaf.intValue() != pafx.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(ufd1));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(ufd1));
                }
                kv.setThismonthqc(ufd);

                ufd = VoUtils.getDZFDouble(kv.getThismonthqm());
                if (fxleaf.intValue() != pafx.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(ufd1));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(ufd1));
                }
                kv.setThismonthqm(ufd);

                /** ----------------原币期初--------------- */
                ybufd = VoUtils.getDZFDouble(kv.getYbthismonthqc());
                if (fxleaf.intValue() != pafx.intValue()) {
                    ybufd = ybufd.sub(VoUtils.getDZFDouble(ybufd1));
                } else {
                    ybufd = ybufd.add(VoUtils.getDZFDouble(ybufd1));
                }
                kv.setYbthismonthqc(ybufd);


                /** --------------原币期末--------------- */
                ybufd = VoUtils.getDZFDouble(kv.getYbthismonthqm());
                if (fxleaf.intValue() != pafx.intValue()) {
                    ybufd = ybufd.sub(VoUtils.getDZFDouble(ybufd1));
                } else {
                    ybufd = ybufd.add(VoUtils.getDZFDouble(ybufd1));
                }
                kv.setYbthismonthqm(ybufd);

            }

        }

    }

    private void upTotal(String min_period, Map<String, YntCpaccountVO> mp,
                         HashMap<String, HashMap<String, KmQmJzExtVO>> map) {
        List<YntCpaccountVO> leaflist = new ArrayList<YntCpaccountVO>();
        for (YntCpaccountVO YntCpaccountVO : mp.values()) {
            if (YntCpaccountVO.getIsleaf().booleanValue()) {
                leaflist.add(YntCpaccountVO);
            }
        }
        KmQmJzExtVO kvoleaf = null;
        KmQmJzExtVO kvo = null;
        List<String> plist = null;
        DZFDouble ufd = null;
        String kmpk = null;
        int len = 0;
        HashMap<String, KmQmJzExtVO> hm = null;
        for (YntCpaccountVO bvo : leaflist) {
            kmpk = bvo.getPk_corp_account();
            hm = map.get(kmpk);
            if (hm == null)
                continue;
            kvoleaf = hm.get(min_period);
            Integer fxleaf = mp.get(kmpk).getDirection();
            plist = getKmParent(mp, kmpk);
            if (plist == null)
                continue;
            len = plist == null ? 0 : plist.size();
            for (int i = 0; i < len; i++) {
                kmpk = plist.get(i);
                kvo = getKey(map, kmpk, min_period);
                ufd = VoUtils.getDZFDouble(kvo.getThismonthqc());
                /** 判断下方向，如果方向不一样不是加应该是减 */
                Integer pafx = mp.get(kmpk).getDirection();
                if (pafx.intValue() != fxleaf.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getThismonthqc()));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getThismonthqc()));
                }
                kvo.setThismonthqc(ufd);

                ufd = VoUtils.getDZFDouble(kvo.getThismonthqm());
                if (pafx.intValue() != fxleaf.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getThismonthqm()));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getThismonthqm()));
                }
                kvo.setThismonthqm(ufd);

                /** --------------原币期初---------- */
                ufd = VoUtils.getDZFDouble(kvo.getYbthismonthqc());

                if (pafx.intValue() != fxleaf.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getYbthismonthqc()));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getYbthismonthqc()));
                }
                kvo.setYbthismonthqc(ufd);

                /** --------------原币期末-------------- */
                ufd = VoUtils.getDZFDouble(kvo.getYbthismonthqm());

                if (pafx.intValue() != fxleaf.intValue()) {
                    ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getYbthismonthqm()));
                } else {
                    ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getYbthismonthqm()));
                }
                kvo.setYbthismonthqm(ufd);
            }

        }

    }


    private List<String> getKmParent(Map<String, YntCpaccountVO> mp,
                                     String kmpk) {
        YntCpaccountVO vo1 = mp.get(kmpk);
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

    private void addMxvo(List<KmMxZVO> list, KmQmJzExtVO jzvo,
                         Map<String, YntCpaccountVO> mp,
                         HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap, List<String> kmparentlist,
                         DZFBoolean xswyewfs, DZFBoolean isnomonthfs, DZFBoolean btotalyear,
                         Map<String, DZFDouble[]> yearmapjf, Map<String, DZFDouble[]> yearmapdf) {
        xswyewfs = xswyewfs == null ? DZFBoolean.TRUE : xswyewfs;
        KmMxZVO vo1 = new KmMxZVO();
        vo1.setRq(jzvo.getPeriod());
        DZFDate datevalue = new DZFDate(jzvo.getPeriod() + "-01");
        vo1.setPzh(null);
        YntCpaccountVO km = mp.get(jzvo.getPk_accsubj());
        /** 根据科目层级来过滤科目 */
        StringBuffer nullstr = new StringBuffer();
        vo1.setKm(nullstr.toString() + km.getAccountname());
        vo1.setPk_accsubj(jzvo.getPk_accsubj());
        vo1.setLevel(km.getAccountlevel());
        vo1.setDay("-01");
        vo1.setKmbm(km.getAccountcode());
        vo1.setZy("期初余额");
        vo1.setBsyszy(DZFBoolean.TRUE);
        vo1.setJf(DZFDouble.ZERO_DBL);
        vo1.setDf(DZFDouble.ZERO_DBL);
        if (0 == km.getDirection()) {
            vo1.setFx("借");
        } else {
            vo1.setFx("贷");
        }
        vo1.setYe(jzvo.getThismonthqc());
        vo1.setYbye(jzvo.getYbthismonthqc());
        /** 倒数第二行：日期（空值）、凭证号（空值）、摘要（固定值：本月合计）、借方（本月借方发生额累计）、贷方（本月贷方发生额累计）、方向（根据科目方向显示）、余额（借：期初+借方累计-贷方累计；贷：期初+贷方累计-借方累计；）------------------------- */
        KmMxZVO vo_2 = new KmMxZVO();
        vo_2.setRq(jzvo.getPeriod());
        vo_2.setDay("-" + datevalue.getDaysMonth());
        vo_2.setPzh(null);
        vo_2.setKm(nullstr.toString() + km.getAccountname());
        vo_2.setPk_accsubj(km.getPk_corp_account());
        vo_2.setKmbm(km.getAccountcode());
        vo_2.setZy("本月合计");
        vo_2.setBsyszy(DZFBoolean.TRUE);
        vo_2.setJf(jzvo.getJffse());
        vo_2.setDf(jzvo.getDffse());
        vo_2.setYbjf(jzvo.getYbjffse());//原币
        vo_2.setYbdf(jzvo.getYbdffse());//原币贷方
        vo_2.setYe(jzvo.getThismonthqm());
        vo_2.setYbye(jzvo.getYbthismonthqm());//原币
        if (0 == km.getDirection()) {
            vo_2.setFx("借");
        } else {
            vo_2.setFx("贷");
        }
        KmMxZVO vo3 = new KmMxZVO();
        vo3.setRq(jzvo.getPeriod());
        vo3.setPzh(null);
        vo3.setDay("-" + datevalue.getDaysMonth());
        vo3.setKm(nullstr.toString() + km.getAccountname());
        vo3.setPk_accsubj(km.getPk_corp_account());
        vo3.setKmbm(km.getAccountcode());
        vo3.setZy("本年累计");
        vo3.setBsyszy(DZFBoolean.TRUE);

        if (btotalyear != null && btotalyear.booleanValue()) {
            /** 借方赋值 */
            DZFDouble[] tempjf = new DZFDouble[]{DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL};
            DZFDouble[] tempdf = new DZFDouble[]{DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL};
            String befperiod = String.valueOf(new Integer(jzvo.getPeriod().substring(0, 4)) - 1);
            if (yearmapjf.containsKey(befperiod)) {
                tempjf = yearmapjf.get(befperiod);
                vo3.setJf(SafeCompute.sub(jzvo.getLjjffse(), tempjf[0]));
                vo3.setYbjf(SafeCompute.sub(jzvo.getYbljjffse(), tempjf[1]));
            } else {
                vo3.setJf(jzvo.getLjjffse());
                vo3.setYbjf(jzvo.getYbljjffse());
            }
            yearmapjf.put(jzvo.getPeriod().substring(0, 4), new DZFDouble[]{jzvo.getLjjffse(), jzvo.getYbljjffse()});

            /** 贷方赋值 */
            if (yearmapdf.containsKey(befperiod)) {
                tempdf = yearmapdf.get(befperiod);
                vo3.setDf(SafeCompute.sub(jzvo.getLjdffse(), tempdf[0]));
                vo3.setYbdf(SafeCompute.sub(jzvo.getYbljdffse(), tempdf[1]));
            } else {
                vo3.setDf(jzvo.getLjdffse());
                vo3.setYbdf(jzvo.getYbljdffse());
            }
            yearmapdf.put(jzvo.getPeriod().substring(0, 4), new DZFDouble[]{jzvo.getLjdffse(), jzvo.getYbljdffse()});
        } else {
            vo3.setJf(jzvo.getLjjffse());
            vo3.setDf(jzvo.getLjdffse());
            vo3.setYbjf(jzvo.getYbljjffse());//原币
            vo3.setYbdf(jzvo.getYbljdffse());
        }
        vo3.setYe(jzvo.getThismonthqm());
        vo3.setYbye(jzvo.getYbthismonthqm());//原币
        if (0 == km.getDirection()) {
            vo3.setFx("借");
        } else {
            vo3.setFx("贷");
        }

        boolean isShow = false;
        if (xswyewfs.booleanValue()) {
            /** 需要显示无余额无发生 */
            if (vo1.getYe().doubleValue() == 0
                    && vo_2.getJf().doubleValue() == 0
                    && vo_2.getDf().doubleValue() == 0
            ) {
                if ((isnomonthfs == null || !isnomonthfs.booleanValue()) && (
                        kmparentlist.contains(vo1.getPk_accsubj() + "_" + jzvo.getPeriod()))) {
                    isShow = true;
                } else {
                    isShow = false;
                }
            } else {
                isShow = true;
            }
        } else {
            /** 不需要显示无余额无发生 */
            isShow = true;
        }

        List<KmMxZVO> list1 = null;
        HashMap<String, List<KmMxZVO>> mm = fsmap.get(km.getPk_corp_account());
        if (mm == null)
            list1 = new ArrayList<KmMxZVO>();
        else
            list1 = mm.get(jzvo.getPeriod());
        Integer direction = mp.get(km.getPk_corp_account()).getDirection();
        DZFDouble uft = null;
        DZFDouble ybuft = DZFDouble.ZERO_DBL;
        DZFDouble ufd = vo1.getYe();
        DZFDouble ybufd = VoUtils.getDZFDouble(vo1.getYbye());
        if (isShow) {
            list.add(vo1);
            if (list1 != null)
                for (KmMxZVO vo : list1) {
                    vo.setKm(nullstr.toString() + km.getAccountname());
                    vo.setPk_accsubj(km.getPk_corp_account());
                    vo.setKmbm(km.getAccountcode());
                    if (direction == 0) {

                        uft = vo.getJf();
                        ybuft = vo.getYbjf();
                        if (uft == null)
                            uft = DZFDouble.ZERO_DBL;
                        if (ybuft == null)
                            ybuft = DZFDouble.ZERO_DBL;
                        ufd = ufd.add(uft);
                        ybufd = ybufd.add(ybuft);
                        uft = vo.getDf();
                        ybuft = vo.getYbdf();
                        if (uft == null)
                            uft = DZFDouble.ZERO_DBL;
                        if (ybuft == null)
                            ybuft = DZFDouble.ZERO_DBL;
                        ufd = ufd.sub(uft);
                        ybufd = ybufd.sub(ybuft);
                        vo.setYe(ufd);
                        vo.setYbye(ybufd);
                    } else {
                        uft = vo.getDf();
                        ybuft = vo.getYbdf();
                        if (uft == null)
                            uft = DZFDouble.ZERO_DBL;
                        if (ybuft == null)
                            ybuft = DZFDouble.ZERO_DBL;

                        ufd = ufd.add(uft);
                        ybufd = ybufd.add(ybuft);
                        uft = vo.getJf();
                        ybuft = vo.getYbjf();
                        if (uft == null)
                            uft = DZFDouble.ZERO_DBL;
                        if (ybuft == null)
                            ybuft = DZFDouble.ZERO_DBL;
                        ufd = ufd.sub(uft);
                        ybufd = ybufd.sub(ybuft);
                        vo.setYe(ufd);
                        vo.setYbye(ybufd);

                    }
                    list.add(vo);
                }
            list.add(vo_2);
            list.add(vo3);
        }
    }

    protected String getQuerySqlByPeriod(DZFDate start, DZFDate end,
                                         String kmwhere, String pk_corp, DZFBoolean ishasjz,
                                         DZFBoolean ishassh, String pk_currency) throws DZFWarpException {
        StringBuffer sb = new StringBuffer();
        sb.append(" select substr(h.doperatedate,1,7) as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,b.pk_tzpz_h,");
        sb.append("        b.pk_currency as bz ,b.jfmny as jf ,b.dfmny as df,a.direction as fx, b.pk_tzpz_h ,b.pk_tzpz_b ,");
        sb.append("        b.fzhsx1,  b.fzhsx2,  b.fzhsx3, b.fzhsx4, b.fzhsx5,");
        /**   启用库存  存货作为辅助核算 */
        sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
        sb.append("      b.fzhsx7,  b.fzhsx8, b.fzhsx9, b.fzhsx10 , ");
        sb.append(" a.pk_corp_account as km ,b.zy , c.currencycode as bz  , b.nrate as hl,");
        sb.append(" b.jfmny  as jf,  ");
        sb.append(" b.dfmny  as df , ");
        sb.append(" b.ybjfmny as ybjf,");
        sb.append(" b.ybdfmny   as ybdf  ");
        sb.append(" ,a.direction as fx ");
        sb.append("  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h  ");
        sb.append("       inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  ");
        sb.append("       left join  ynt_bd_currency c  on c.pk_currency=b.pk_currency  ");
        sb.append("             where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");

        Date dd = start.toDate();
        dd = new Date(dd.getYear(), dd.getMonth(), 1);
        DZFDate d1 = new DZFDate(dd);
        sb.append(" and h.doperatedate>='").append(d1);
        dd = end.toDate();
        dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
        d1 = new DZFDate(dd);
        sb.append("' and h.doperatedate<='").append(d1).append("'");

        if (!StringUtil.isEmptyWithTrim(kmwhere)) {
            sb.append("  ").append(kmwhere);
        }

        sb.append(" and h.pk_corp='" + pk_corp + "'");

        if (ishasjz.booleanValue()) {
            /** 不包含未记账，即只查询已记账的 */
            sb.append(" and h.ishasjz='Y' ");
            sb.append(" and h.vbillstatus=1 ");
        }
        sb.append(" and nvl(b.pk_currency,'" + DzfUtil.PK_CNY + "')='" + pk_currency + "'");
        sb.append(" order by h.pzh asc ");

        return sb.toString();
    }

    public List<KmMxZVO> getKmFSByPeriodQC(String pk_corp, DZFBoolean ishasjz,
                                           DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere,
                                           String pk_currency, DZFBoolean bshowfz) throws DZFWarpException {
        /** 根据币种查询币种编码和汇率 */
        String sql1 = getQuerySqlByPeriodForQC(start, end, kmwhere, pk_corp,
                ishasjz, ishassh, pk_currency, bshowfz);
        ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1,
                new SQLParameter(), new BeanListProcessor(KmMxZVO.class));

        List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
        if (result1 != null && !result1.isEmpty()) {
            for (Object o : result1) {
                KmMxZVO vo = (KmMxZVO) o;
                vo.setJf(VoUtils.getDZFDouble(vo.getJf()));
                vo.setDf(VoUtils.getDZFDouble(vo.getDf()));
                vo.setYbjf(VoUtils.getDZFDouble(vo.getYbjf()));
                vo.setYbdf(VoUtils.getDZFDouble(vo.getYbdf()));
                if ("0".equals(vo.getFx())) {
                    vo.setFx("借");
                } else {
                    vo.setFx("贷");
                }
                vec_details.add(vo);
            }
        }
        return vec_details;
    }

    /**
     * 包含期初
     *
     * @param start
     * @param end
     * @param kmwhere
     * @param pk_corp
     * @param ishasjz
     * @param ishassh
     * @return
     * @throws BusinessException
     */
    protected String getQuerySqlByPeriodForQC(DZFDate start, DZFDate end,
                                              String kmwhere, String pk_corp, DZFBoolean ishasjz,
                                              DZFBoolean ishassh, String pk_currency, DZFBoolean bshowfz) throws DZFWarpException {
        StringBuffer sb = new StringBuffer();
        sb.append("select * from (");
        sb.append(" select substr(h.doperatedate,1,7) as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,");
        sb.append(" a.pk_corp_account as km ,b.zy ,c.currencycode as bz  , to_char(b.nrate) as hl,");
        sb.append(" b.jfmny  as jf,  ");
        sb.append(" b.dfmny  as df ,  ");
        sb.append(" b.ybjfmny as ybjf , ");
        sb.append(" b.ybdfmny as ybdf ");
        sb.append(" ,a.direction as fx , b.pk_tzpz_h,b.pk_tzpz_b , ");
        sb.append("    b.fzhsx1,  b.fzhsx2,  b.fzhsx3, b.fzhsx4, b.fzhsx5,");
        /**   启用库存  存货作为辅助核算 */
        sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6  end fzhsx6, ");
        sb.append("      b.fzhsx7,  b.fzhsx8, b.fzhsx9, b.fzhsx10");
        sb.append("  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h  ");
        sb.append("       inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  ");
        sb.append("       left join  ynt_bd_currency c  on c.pk_currency=b.pk_currency  ");
        sb.append("             where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
        Date dd = start.toDate();
        dd = new Date(dd.getYear(), dd.getMonth(), 1);
        DZFDate d1 = new DZFDate(dd);
        sb.append(" and h.doperatedate>='").append(d1);
        dd = end.toDate();
        dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
        d1 = new DZFDate(dd);
        sb.append("' and h.doperatedate<='").append(d1).append("'");

        if (!StringUtil.isEmptyWithTrim(kmwhere)) {
            sb.append("  ").append(kmwhere);
        }
        sb.append(" and nvl(b.pk_currency,'" + DzfUtil.PK_CNY + "')='" + pk_currency + "'");

        sb.append(" and h.pk_corp='" + pk_corp + "'");

        if (ishasjz.booleanValue()) {
            /** 不包含未记账，即只查询已记账的 */
            sb.append(" and h.ishasjz='Y' ");
            sb.append(" and h.vbillstatus=1 ");
        }
        sb.append(" order by h.pzh asc  )  b3 ");
        StringBuffer qcyesql = new StringBuffer();
        qcyesql.append(" union all ");
        qcyesql.append("  select substr(b.doperatedate, 1, 4)||'-01' as qj, substr(b.doperatedate, 1, 4)||'-01'||'-01' as rq,  ");
        qcyesql.append("  ''as pzh,a.accountcode,a.pk_corp_account as km,'' as zy,c.currencycode as  bz, '0.00' as hl, ");
        qcyesql.append("  b.yearjffse as jf, b.yeardffse as df, ");
        qcyesql.append("  b.ybyearjffse as ybjf, b.ybyeardffse as ybdf ");
        qcyesql.append("  , 1 as fx ,'' pk_tzpz_h, ''  pk_tzpz_b, ");
        qcyesql.append("   '' as fzhsx1, '' as fzhsx2,  '' as fzhsx3, '' as fzhsx4, '' as fzhsx5,");
        /** 启用库存  存货作为辅助核算 */
        qcyesql.append(" '' as fzhsx6,  '' as fzhsx7,  '' as fzhsx8, '' as fzhsx9, '' as fzhsx10 ");
        qcyesql.append("    from ynt_qcye b  ");
        qcyesql.append(" inner join ynt_cpaccount a  on b.pk_accsubj = a.pk_corp_account and a.isleaf='Y'  ");
        qcyesql.append("    left join  ynt_bd_currency c  on c.pk_currency=a.pk_currency ");
        qcyesql.append("    where  (1=1) " + kmwhere + " and b.pk_corp='" + pk_corp + "' and nvl(b.dr,0)=0 ");
        qcyesql.append(" and nvl(b.pk_currency,'" + DzfUtil.PK_CNY + "')='" + pk_currency + "'");

        return sb.toString() + qcyesql.toString();
    }

    /**
     * 期末辅助项目赋值
     *
     * @param fzhsqcvos
     * @param mp
     * @param qcfzmap
     */
    private void putQCFzMap(FzhsqcVO[] fzhsqcvos, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap,
                            HashMap<String, DZFDouble[]> corpbegqcmapfzmap,
                            Map<String, AuxiliaryAccountBVO> fzmap
            , DZFDate begindate, DZFDate corpdate) {
        if (fzhsqcvos != null && fzhsqcvos.length > 0) {
            StringBuffer sb = null;
            StringBuffer sbcode = null;
            StringBuffer sbname = null;
            KmMxZVO tempkmmx = null;
            YntCpaccountVO tempkmvo = null;
            for (FzhsqcVO qcvo : fzhsqcvos) {
                sb = new StringBuffer();
                sbcode = new StringBuffer();
                sbname = new StringBuffer();
                sb.append(qcvo.getPk_accsubj() + "_");
                if (mp.get(qcvo.getPk_accsubj()) == null) {
                    continue;
                }
                tempkmvo = mp.get(qcvo.getPk_accsubj());
                sbcode.append(tempkmvo.getAccountcode() + "_");
                sbname.append(tempkmvo.getAccountname() + "_");
                for (int i = 1; i < 11; i++) {
                    String key = (String) qcvo.getAttributeValue("fzhsx" + i);
                    if (!StringUtil.isEmpty(key)) {
                        sb.append(key + "_");
                        sbcode.append(fzmap.get(key).getCode());
                        sbname.append(fzmap.get(key).getName());
                    }
                }
                sb = new StringBuffer(sb.substring(0, sb.length() - 1));

                /** 科目的长度不处理 */
                if (sb.length() == 24) {
                    continue;
                }
                DZFDouble qcmny = qcvo.getThismonthqc() == null ? DZFDouble.ZERO_DBL : qcvo.getThismonthqc();
                DZFDouble ybqcmny = qcvo.getYbthismonthqc() == null ? DZFDouble.ZERO_DBL : qcvo.getYbthismonthqc();
                if (!qcfzmap.containsKey(sb.toString())) {
                    tempkmmx = new KmMxZVO();
                    tempkmmx.setPk_accsubj(sb.toString());
                    if (begindate.getYear() == corpdate.getYear() && corpdate.getMonth() != 1) {
                        tempkmmx.setJf(qcvo.getYearjffse());
                        tempkmmx.setDf(qcvo.getYeardffse());

                        tempkmmx.setYbjf(qcvo.getYbyearjffse());
                        tempkmmx.setYbdf(qcvo.getYbyeardffse());
                    }
                    if (0 == tempkmvo.getDirection()) {
                        tempkmmx.setFx("借");
                    } else {
                        tempkmmx.setFx("贷");
                    }
                    tempkmmx.setYe(qcmny);
                    tempkmmx.setYbye(ybqcmny);
                    qcfzmap.put(sb.toString(), tempkmmx);
                } else {
                    tempkmmx = qcfzmap.get(sb.toString());
                    if (0 == tempkmvo.getDirection()) {
                        qcmny = SafeCompute.add(tempkmmx.getYe(), qcmny);
                        ybqcmny = SafeCompute.add(tempkmmx.getYbye(), ybqcmny);
                        tempkmmx.setJf(SafeCompute.add(tempkmmx.getJf(), qcmny));
                        tempkmmx.setYbjf(SafeCompute.add(tempkmmx.getYbjf(), ybqcmny));
                    } else {
                        qcmny = SafeCompute.add(tempkmmx.getYe(), qcmny.multiply(-1));
                        ybqcmny = SafeCompute.add(tempkmmx.getYbye(), ybqcmny.multiply(-1));
                    }
                    tempkmmx.setYe(qcmny);
                    tempkmmx.setYbye(ybqcmny);
                }

                if (corpbegqcmapfzmap != null) {
                    corpbegqcmapfzmap.put(sb.toString(), new DZFDouble[]{qcvo.getYearqc(), qcvo.getYbyearqc()});
                }
            }
        }
    }

    /**
     * 年结账数据赋值
     *
     * @param kmqmjzvos
     * @param mp
     * @param qcfzmap
     * @param fzmap
     */
    private void putNJQCFzMap(KMQMJZVO[] kmqmjzvos, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap,
                              Map<String, AuxiliaryAccountBVO> fzmap, DZFDate begindate, DZFDate corpdate) {
        if (kmqmjzvos == null || kmqmjzvos.length == 0) {
            return;
        }
        FzhsqcVO[] fzhsqcvos = new FzhsqcVO[kmqmjzvos.length];
        for (int i = 0; i < kmqmjzvos.length; i++) {
            fzhsqcvos[i] = new FzhsqcVO();
            fzhsqcvos[i].setPk_accsubj(kmqmjzvos[i].getPk_accsubj());
            fzhsqcvos[i].setThismonthqc(kmqmjzvos[i].getThismonthqm());
            fzhsqcvos[i].setYbthismonthqc(kmqmjzvos[i].getYbthismonthqm());
            for (int k = 1; k < 11; k++) {
                fzhsqcvos[i].setAttributeValue("fzhsx" + k, kmqmjzvos[i].getAttributeValue("fzhsx" + k));
            }
        }
        putQCFzMap(fzhsqcvos, mp, qcfzmap, null, fzmap, begindate, corpdate);
    }

    /**
     * 发生的数据添加的期初里面
     *
     * @param vec0
     * @param mp
     * @param qcfzmap
     * @param fzmap
     */
    private void putFsToQCMap(List<KmZzVO> vec0, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap,
                              Map<String, AuxiliaryAccountBVO> fzmap) {
        if (vec0 == null || vec0.size() == 0) {
            return;
        }
        KmMxZVO mxzvo = null;
        YntCpaccountVO accountvo = null;
        DZFDouble tempvalue = DZFDouble.ZERO_DBL;
        DZFDouble ybtempvalue = DZFDouble.ZERO_DBL;
        String fzhsx = null;
        StringBuffer key = null;
        for (KmZzVO zzvo : vec0) {
            key = new StringBuffer();
            key.append(zzvo.getKm() + "_");
            accountvo = mp.get(zzvo.getKm());
            for (int i = 1; i < 11; i++) {
                fzhsx = (String) zzvo.getAttributeValue("fzhsx" + i);
                if (!StringUtil.isEmpty(fzhsx)) {
                    key.append(fzhsx + "_");
                }
            }

            key = new StringBuffer(key.substring(0, key.length() - 1));

            if (key.length() == 24) {
                continue;
            }
            if (qcfzmap.containsKey(key.toString())) {
                mxzvo = qcfzmap.get(key.toString());
                if (accountvo.getDirection() == 0) {
                    mxzvo.setFx("借");
                    tempvalue = SafeCompute.sub(zzvo.getJf(), zzvo.getDf());
                    ybtempvalue = SafeCompute.sub(zzvo.getYbjf(), zzvo.getYbdf());
                } else {
                    mxzvo.setFx("贷");
                    tempvalue = SafeCompute.sub(zzvo.getDf(), zzvo.getJf());
                    ybtempvalue = SafeCompute.sub(zzvo.getYbdf(), zzvo.getYbjf());
                }
                mxzvo.setYe(SafeCompute.add(mxzvo.getYe(), tempvalue));
                mxzvo.setYbye(SafeCompute.add(mxzvo.getYbye(), ybtempvalue));
            } else {
                mxzvo = new KmMxZVO();
                if (accountvo.getDirection() == 0) {
                    mxzvo.setFx("借");
                    tempvalue = SafeCompute.sub(zzvo.getJf(), zzvo.getDf());
                    ybtempvalue = SafeCompute.sub(zzvo.getYbjf(), zzvo.getYbdf());
                } else {
                    mxzvo.setFx("贷");
                    tempvalue = SafeCompute.sub(zzvo.getDf(), zzvo.getJf());

                    ybtempvalue = SafeCompute.sub(zzvo.getYbdf(), zzvo.getYbjf());
                }
                mxzvo.setPk_accsubj(key.toString());
                mxzvo.setYe(tempvalue);
                mxzvo.setYbye(ybtempvalue);
                qcfzmap.put(key.toString(), mxzvo);
            }
        }
    }


}
