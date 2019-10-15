package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IXjRjZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 现金/银行日记账
 *
 * @author jiaozj
 */
@Service("gl_rep_xjyhrjzserv")
public class XjRjZReportImpl implements IXjRjZReport {


    @Autowired
    private SingleObjectBO singleObjectBO;

    private IKMMXZReport gl_rep_kmmxjserv = null;

    public IKMMXZReport getGl_rep_kmmxjserv() {
        return gl_rep_kmmxjserv;
    }

    @Autowired
    public void setGl_rep_kmmxjserv(IKMMXZReport gl_rep_kmmxjserv) {
        this.gl_rep_kmmxjserv = gl_rep_kmmxjserv;
    }

    public KmMxZVO[] getXJRJZVOs(String pk_corp, String kms, DZFDate begindate,
                                 DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                                 DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency)
            throws DZFWarpException {
        // 这个方法不用了暂时传递空
        return null;
    }

    /**
     * 现金日记账取数
     */
    public KmMxZVO[] getXJRJZVOs(String pk_corp, String kms, DZFDate begindate,
                                 DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                                 DZFBoolean ishasjz, DZFBoolean ishassh) throws DZFWarpException {
        return getXJRJZVOs(pk_corp, kms, begindate, enddate, xswyewfs, xsyljfs, ishasjz, ishassh, "00000100AA10000000000BKT");// 人民币
    }

    public KmMxZVO[] getSpecCashPay(String pk_corp, DZFDate beginDate, DZFDate endDate, DZFBoolean includesg, String pk_currency, String kms, List<String> kmcodelist) throws DZFWarpException {
        KmMxZVO[] repVOs = getXJRJZVOsConMo(pk_corp, kms, null, beginDate, endDate, DZFBoolean.FALSE, DZFBoolean.FALSE, includesg, DZFBoolean.FALSE, pk_currency, kmcodelist, null);

        List<KmMxZVO> resultVOs = new ArrayList<KmMxZVO>();
        if (repVOs != null && resultVOs.size() > 0) {
            for (KmMxZVO mxvo : repVOs) {
                if (mxvo.getLevel() != null && mxvo.getLevel() == 1) {
                    resultVOs.add(mxvo);
                }
            }

            resultVOs = conbinOtAcc(resultVOs, pk_corp);
        }
        return resultVOs.toArray(new KmMxZVO[resultVOs.size()]);
    }

    private List<KmMxZVO> conbinOtAcc(List<KmMxZVO> repVOs, String pk_corp) throws DZFWarpException {

        HashMap<String, String> hash = new HashMap<String, String>();
        StringBuffer accSql = new StringBuffer("b.pk_tzpz_h in(");
        boolean qry = false;
        for (KmMxZVO vo : repVOs) {

            if (vo.getPk_tzpz_h() == null)
                continue;
            accSql.append(",'" + vo.getPk_tzpz_h() + "'");
            qry = true;
        }

        if (qry) {
            accSql.append(")");
            int fir = accSql.indexOf(",");
            accSql = accSql.replace(fir, fir + 1, "");

            StringBuffer sb = new StringBuffer("select b.pk_tzpz_h,CASE nvl(b.jfmny,0) WHEN  0 THEN 1 ELSE 0 END  direct,c.accountcode vcode,c.accountname vname from ynt_tzpz_b b ");
            sb.append(" inner join ynt_cpaccount c on b.pk_accsubj=c.pk_corp_account and c.pk_corp=? where nvl(b.dr,0)=0 and ")
                    .append(accSql);
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            List<TzpzBVO> tzpzB = (List<TzpzBVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(TzpzBVO.class));
            if (tzpzB != null && tzpzB.size() > 0) {
                String value, key;
                for (TzpzBVO bVO : tzpzB) {
                    value = bVO.getVname();
                    key = bVO.getPk_tzpz_h() + "" + bVO.getDirect();
                    if (hash.containsKey(key)) {
                        value = value + "/" + hash.get(key);
                    }
                    hash.put(key, value);
                }
            }

            for (KmMxZVO vo : repVOs) {
                if (vo.getPk_tzpz_h() != null) {

                    if (vo.getJf() != null && vo.getJf().compareTo(DZFDouble.ZERO_DBL) != 0) {
                        vo.setOtsubject(hash.get(vo.getPk_tzpz_h() + "" + "1"));
                    } else {
                        vo.setOtsubject(hash.get(vo.getPk_tzpz_h() + "" + "0"));
                    }
                }
            }
        }
        return repVOs;
    }

    public KmMxZVO[] getXJRJZVOsConMo(String pk_corp, String kms, String kms_last,
                                      DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs,
                                      DZFBoolean xsyljfs, DZFBoolean ishasjz, DZFBoolean ishassh,
                                      String pk_currency, List<String> kmcodelist, Object[] qryobjs)
            throws DZFWarpException {
        QueryParamVO vo = new QueryParamVO();
        vo.setPk_corp(pk_corp);
        vo.setBegindate1(begindate);
        vo.setEnddate(enddate);
        vo.setQjq(DateUtils.getPeriod(begindate));
        vo.setQjz(DateUtils.getPeriod(enddate));
        vo.setIshasjz(ishasjz);
        vo.setIshassh(ishassh);
        vo.setXswyewfs(xswyewfs);
        vo.setXsyljfs(xsyljfs);
        vo.setKms_first(StringUtil.isEmpty(kms) ? "1001" : kms);
        vo.setKms_last(StringUtil.isEmpty(kms_last) ? "1002" : kms_last);
        vo.setPk_currency(pk_currency);
        vo.setKmcodelist(kmcodelist);
        vo.setBtotalyear(DZFBoolean.TRUE);/** 是否按照年显示本年累计 */
        Object[] obj = null;

        if (qryobjs == null || qryobjs.length == 0) {
            obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
        } else {
            obj = qryobjs;
        }
        List[] lists = (List[]) obj[0];
        Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];

        int len = lists == null ? 0 : lists.length;
        List<KmMxZVO> ls = (len > 0) ? lists[0] : null;
        KmMxZVO kvo = null;
        len = ls == null ? 0 : ls.size();
        for (int i = len - 1; i >= 0; i--) {
            kvo = ls.get(i);
            if ((kvo.getKmbm().startsWith("1001") || kvo.getKmbm().startsWith("1002")) == false) {
                ls.remove(i);
            }
        }


        KmMxZVO vo1 = null;
        YntCpaccountVO km = null;
        len = ls == null ? 0 : ls.size();
        List<KmMxZVO> reslit = new ArrayList<KmMxZVO>();
        kvo = null;
        for (int i = 0; i < len; i++) {
            vo1 = ls.get(i);
            km = mp.get(vo1.getPk_accsubj());
            /** 根据科目层级来过滤科目 */
            StringBuffer nullstr = new StringBuffer();
            for (int k = 1; k < km.getAccountlevel(); k++) {
                nullstr.append("    ");
            }

            /** 重新加入本日合计(有凭证号的时候才加入) --是否重新开始 */
            if (StringUtil.isEmpty(vo1.getPzh()) == false) {
                if (kvo != null && (kvo.getRq() + kvo.getDay()).equals(vo1.getRq() + vo1.getDay())) {
                    kvo.setJf(VoUtils.getDZFDouble(kvo.getJf()).add(VoUtils.getDZFDouble(vo1.getJf())));
                    kvo.setDf(VoUtils.getDZFDouble(kvo.getDf()).add(VoUtils.getDZFDouble(vo1.getDf())));
                    if (km.getDirection() == 0) {
                        kvo.setYe(VoUtils.getDZFDouble(kvo.getYe()).add(VoUtils.getDZFDouble(vo1.getJf()).sub(VoUtils.getDZFDouble(vo1.getDf()))));
                    } else
                        kvo.setYe(VoUtils.getDZFDouble(kvo.getYe()).add(VoUtils.getDZFDouble(vo1.getDf()).sub(VoUtils.getDZFDouble(vo1.getJf()))));
                } else if (kvo == null) {
                    kvo = new KmMxZVO();
                    kvo.setZy("本日合计");
                    kvo.setBsyszy(DZFBoolean.TRUE);
                    kvo.setKm(nullstr.toString() + km.getAccountname());
                    kvo.setPk_accsubj(km.getPk_corp_account());
                    kvo.setKmbm(km.getAccountcode());
                    kvo.setRq(vo1.getRq());
                    kvo.setFx(vo1.getFx());
                    kvo.setDay(vo1.getDay());
                    kvo.setJf(VoUtils.getDZFDouble(kvo.getJf()).add(VoUtils.getDZFDouble(vo1.getJf())));
                    kvo.setDf(VoUtils.getDZFDouble(kvo.getDf()).add(VoUtils.getDZFDouble(vo1.getDf())));
                    kvo.setYe(VoUtils.getDZFDouble(kvo.getYe()).add(vo1.getYe()));
                    ;
                } else {
                    reslit.add(kvo);
                    kvo = new KmMxZVO();
                    kvo.setZy("本日合计");
                    kvo.setBsyszy(DZFBoolean.TRUE);
                    kvo.setKm(nullstr.toString() + km.getAccountname());
                    kvo.setPk_accsubj(km.getPk_corp_account());
                    kvo.setKmbm(km.getAccountcode());
                    kvo.setRq(vo1.getRq());
                    kvo.setDay(vo1.getDay());
                    kvo.setFx(vo1.getFx());
                    kvo.setJf(VoUtils.getDZFDouble(kvo.getJf()).add(VoUtils.getDZFDouble(vo1.getJf())));
                    kvo.setDf(VoUtils.getDZFDouble(kvo.getDf()).add(VoUtils.getDZFDouble(vo1.getDf())));
                    kvo.setYe(VoUtils.getDZFDouble(kvo.getYe()).add(vo1.getYe()));
                    ;
                }
            } else if (StringUtil.isEmpty(vo1.getPzh()) && vo1.getZy() != null && vo1.getZy().equals("本月合计") &&
                    ReportUtil.bSysZy(vo1)) {
                if (kvo != null) {
                    reslit.add(kvo);
                }
                kvo = null;
            }

            if (vo1.getZy() != null && vo1.getZy().equals("本月合计") && ReportUtil.bSysZy(vo1)) {
                DZFDate datetemp = new DZFDate(vo1.getRq().substring(0, 7) + vo1.getDay());
                vo1.setDay("-" + String.valueOf(datetemp.getDaysMonth()));
            }
            reslit.add(vo1);
        }
        return reslit.toArray(new KmMxZVO[0]);
    }
}
