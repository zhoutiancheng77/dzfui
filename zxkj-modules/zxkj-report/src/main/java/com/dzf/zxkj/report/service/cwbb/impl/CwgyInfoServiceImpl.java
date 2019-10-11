package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.query.cwbb.CwgyInfoQueryVO;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.service.cwbb.ICwgyInfoService;
import com.dzf.zxkj.report.service.cwbb.IZcFzBService;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.vo.cwbb.CwgyInfoVO;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class CwgyInfoServiceImpl implements ICwgyInfoService {

    @Reference(version = "1.0.0")
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    private IFsYeService fsYeService;

    @Autowired
    private IZcFzBService zcFzBService;

    @Override
    public CwgyInfoVO[] getCwgyInfoVOs(CwgyInfoQueryVO queryVO, CorpVO corpVO) throws Exception {
        String qj = queryVO.getQjq();
        String pk_corp = corpVO.getPk_corp();
        String period = queryVO.getQjz();
        if (corpVO.getBegindate().toString().substring(0, 7).compareTo(qj) > 0) {
            throw new RuntimeException("查询日期不能在建账日期前");
        }
        Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
        CwgyInfoVO[] curvos = null;
        CwgyInfoVO[] lastvos = null;
        curvos = getCurVo(corpVO, corpschema, queryVO);
        lastvos = getLastVo(corpVO, corpschema, queryVO);
        CwgyInfoVO[] previousvos = getPreviousVo(corpVO, period, corpschema);
        calculationProportion(curvos, lastvos);
        calculationChainGrowth(curvos, previousvos);

        CwgyInfoVO[] invos = addInvoiceInfo(pk_corp, qj);
        CwgyInfoVO[] mervos = ArrayUtil.mergeArray(curvos, invos);

        int num1 = 0;
        int col = mervos.length;
        if (!StringUtil.isEmpty(mervos[mervos.length - 1].getHs())) {
            num1 = Integer.parseInt(mervos[mervos.length - 1].getHs()) + 1;
        }

        if (queryVO.getNhasyj() == null || !queryVO.getNhasyj().booleanValue()) {//预警是否添加进去
            List<CwgyInfoVO> list = addWarnInfo(corpVO, qj, col, num1);
            mervos = ArrayUtil.mergeArray(mervos, list.toArray(new CwgyInfoVO[list.size()]));
        }

        int len = mervos.length;

        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> map1 = new HashMap<>();
        int num = 1;
        for (int i = 0; i < len; i++) {
            String hs = mervos[i].getHs();
            if (!StringUtil.isEmpty(hs)) {

                if (map.containsKey(hs)) {
                    num = num + 1;
                } else {
                    num = 1;
                    map1.put(hs, i);
                }
                map.put(hs, num);
            }
        }

        for (int i = 0; i < len; i++) {
            String hs = mervos[i].getHs();
            if (!StringUtil.isEmpty(hs)) {
                int index = map1.get(hs);
                if (index == i) {
                    mervos[i].setRow(index);
                    mervos[i].setRowspan(map.get(hs));
                }
            }
        }

        return mervos;
    }

    private List<CwgyInfoVO> addWarnInfo(CorpVO corpVO, String qj, int col, int num) {
        String pk_corp = corpVO.getPk_corp();
        IncomeWarningVO[] ivos = zxkjPlatformService.queryIncomeWarningVOs(pk_corp);
        List<CwgyInfoVO> list = new ArrayList<>();
        if (ivos != null && ivos.length > 0) {
            DZFDate jzdate = corpVO.getBegindate();
            DZFDate endDate = new DZFDate(qj + "-01");
            // 结束日期在建账日期前则取建账日期为结束日期
            if (endDate.before(jzdate)) {
                endDate = jzdate;
            }
            ivos = zxkjPlatformService.queryFseInfo(ivos, pk_corp, endDate.toString());
            CwgyInfoVO vo1 = new CwgyInfoVO();
            vo1.setColspan(7);
            vo1.setCol(col);
            list.add(vo1);
            col = vo1.getCol();
            for (IncomeWarningVO invo : ivos) {
                CwgyInfoVO info = new CwgyInfoVO();
                info.setXmfl(invo.getXmmc());
                info.setXm("连续12月收入");
                info.setHs(Integer.toString(num));
                info.setBnljje(invo.getFstotal());
                info.setColspan(4);
                info.setCol(++col);
                list.add(info);
                CwgyInfoVO info1 = new CwgyInfoVO();
                info1.setXmfl(invo.getXmmc());
                info1.setXm("预警上限");
                info1.setHs(Integer.toString(num));
                info1.setBnljje(invo.getSrsx());
                info1.setColspan(4);
                info1.setCol(++col);
                list.add(info1);
                CwgyInfoVO info2 = new CwgyInfoVO();
                info2.setXmfl(invo.getXmmc());
                info2.setXm("本月还可新增收入");
                info2.setHs(Integer.toString(num));
                info2.setBnljje(invo.getInfonumber());
                info2.setColspan(4);
                info2.setCol(++col);
                list.add(info2);
                num++;
            }
        } else {
            CwgyInfoVO vo1 = new CwgyInfoVO();
            vo1.setColspan(7);
            vo1.setCol(col);
            list.add(vo1);
            col = vo1.getCol();
            CwgyInfoVO info = new CwgyInfoVO();
            info.setXmfl("预警设置");
            info.setXm("连续12月收入");
            info.setHs(Integer.toString(num));
            info.setColspan(4);
            info.setCol(++col);
            list.add(info);
            CwgyInfoVO info1 = new CwgyInfoVO();
            info1.setXmfl("预警设置");
            info1.setXm("预警上线");
            info1.setHs(Integer.toString(num));
            info1.setColspan(4);
            info1.setCol(++col);
            list.add(info1);
            CwgyInfoVO info2 = new CwgyInfoVO();
            info2.setXmfl("预警设置");
            info2.setXm("本月还可新增收入");
            info2.setHs(Integer.toString(num));
            info2.setColspan(4);
            info2.setCol(++col);
            list.add(info2);
        }
        return list;
    }

    private CwgyInfoVO[] addInvoiceInfo(String pk_corp, String qj) {
        List<VATSaleInvoiceVO> list = quyerSaleInvoiceVO(qj, pk_corp);
        List<VATInComInvoiceVO> list2 = quyerInComInvoiceVO(qj, pk_corp);
        CwgyInfoVO vo1 = new CwgyInfoVO();
        vo1.setColspan(7);
        vo1.setCol(27);
        CwgyInfoVO vo11 = new CwgyInfoVO();
        vo11.setXmfl("增值税发票");
        // 营业收入取自利润表中本年和本期金额
        vo11.setXm("");
        vo11.setHs("10");
        vo11.setBnljje(new DZFDouble(100));
        vo11.setByje(new DZFDouble(101));
        vo11.setColspan(2);
        vo11.setCol(28);
        // 增加增值税发票
        CwgyInfoVO vo12 = new CwgyInfoVO();
        vo12.setXmfl("增值税发票");
        vo12.setXm("张数");
        vo12.setHs("10");
        vo12.setColspan(2);
        vo12.setCol(29);
        if (list != null && list.size() > 0) {
            vo12.setBnljje(new DZFDouble(list.get(0).getCount()));
        }
        if (list2 != null && list2.size() > 0) {
            vo12.setByje(new DZFDouble(list2.get(0).getCount()));
        }

        CwgyInfoVO vo13 = new CwgyInfoVO();
        vo13.setXmfl("增值税发票");
        vo13.setXm("价税合计");
        vo13.setHs("10");
        vo13.setColspan(2);
        vo13.setCol(30);
        if (list != null && list.size() > 0) {
            vo13.setBnljje(list.get(0).getJshj());
        }
        if (list2 != null && list2.size() > 0) {
            vo13.setByje(list2.get(0).getJshj());
        }
        CwgyInfoVO vo14 = new CwgyInfoVO();
        vo14.setXmfl("增值税发票");
        vo14.setXm("税额");
        vo14.setHs("10");
        vo14.setColspan(2);
        vo14.setCol(31);
        if (list != null && list.size() > 0) {
            vo14.setBnljje(list.get(0).getSpse());
        }
        if (list2 != null && list2.size() > 0) {
            vo14.setByje(list2.get(0).getSpse());
        }

        return new CwgyInfoVO[]{vo1, vo11, vo12, vo13, vo14};
    }

    private List<VATInComInvoiceVO> quyerInComInvoiceVO(String qj, String pk_corp) {
        //todo
        return null;
    }

    private List<VATSaleInvoiceVO> quyerSaleInvoiceVO(String qj, String pk_corp) {
        //todo
        return null;
    }

    private void calculationChainGrowth(CwgyInfoVO[] curvos, CwgyInfoVO[] previousvos) {
        if (curvos == null || curvos.length == 0 || previousvos == null || previousvos.length == 0)
            return;

        DZFDouble d100 = new DZFDouble(100);
        for (CwgyInfoVO cur : curvos) {
            for (CwgyInfoVO previous : previousvos) {
                if (cur.getXm().equals(previous.getXm())) {
                    cur.setByhb(getDZFDouble(cur.getByje()).sub(getDZFDouble(previous.getByje()))
                            .div(getDZFDouble(previous.getByje())).multiply(d100));
                }
            }
        }
    }

    private DZFDouble getDZFDouble(DZFDouble ufd) {
        return ufd == null ? DZFDouble.ZERO_DBL : ufd;
    }

    private void calculationProportion(CwgyInfoVO[] curvos, CwgyInfoVO[] lastvos) {
        if (curvos == null || curvos.length == 0 || lastvos == null || lastvos.length == 0)
            return;

        DZFDouble d100 = new DZFDouble(100);
        for (CwgyInfoVO cur : curvos) {
            for (CwgyInfoVO last : lastvos) {
                if (cur.getXm().equals(last.getXm())) {
                    cur.setBybl(getDZFDouble(cur.getByje()).sub(getDZFDouble(last.getByje()))
                            .div(getDZFDouble(last.getByje())).multiply(d100));
                    cur.setBnljbl(getDZFDouble(cur.getBnljje()).sub(getDZFDouble(last.getBnljje()))
                            .div(getDZFDouble(last.getBnljje())).multiply(d100));
                }
            }
        }
    }

    private CwgyInfoVO[] getPreviousVo(CorpVO corpVO, String period, Integer corpschema) throws Exception {
        CwgyInfoVO[] vos = null;
        String pk_corp = corpVO.getPk_corp();
        period = DateUtils.getPreviousPeriod(period);
        FsYeQueryVO queryVO = new FsYeQueryVO();
        // 上一年
        int year = Integer.valueOf(period.substring(0, 4));
        queryVO.setPk_corp(pk_corp);
        queryVO.setQjq(year + "-01");
        queryVO.setQjz(period);

        String corpdate = corpVO.getBegindate().toString();
        int periodCompare = period.compareTo(corpdate.substring(0, 7));
        // 查询日期在建账日期之前 直接返回
        if (periodCompare < 0) {
            return null;
        }
        DZFDate date = new DZFDate(period +
                (periodCompare == 0 ? corpdate.substring(7) : "-01"));
        queryVO.setEnddate(date);
        queryVO.setBegindate1(date);
        vos = getRawVos(corpVO, corpschema, queryVO);
        return vos;
    }

    private CwgyInfoVO[] getRawVos(CorpVO corpvo, Integer corpschema, FsYeQueryVO vo) throws Exception {
        CwgyInfoVO[] vos = null;
        Object[] obj = fsYeService.getFsJyeVOs1(vo);
        if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
            // 2007会计准则
            vos = getLRB2007VOs(corpvo, vo.getQjz(), obj, vo, DZFBoolean.TRUE);
        } else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
            vos = getLRB2013VOs(corpvo, vo.getQjz(), obj, vo, DZFBoolean.TRUE);
        } else {
            throw new RuntimeException("该制度暂不支持财务概要信息表!");
        }
        dealPrecision(vos);
        return vos;
    }

    // 处理精度
    private void dealPrecision(CwgyInfoVO[] vos) {
        if (vos == null) {
            return;
        }
        for (CwgyInfoVO vo : vos) {
            vo.setBnljje(vo.getBnljje() == null
                    ? DZFDouble.ZERO_DBL : vo.getBnljje().setScale(2, DZFDouble.ROUND_HALF_DOWN));
            vo.setByje(vo.getByje() == null
                    ? DZFDouble.ZERO_DBL : vo.getByje().setScale(2, DZFDouble.ROUND_HALF_DOWN));
        }
    }

    private Map<String, YntCpaccountVO> convert(Map<String, YntCpaccountVO> mp) {
        Map<String, YntCpaccountVO> mp1 = new HashMap<String, YntCpaccountVO>();
        for (YntCpaccountVO b : mp.values()) {
            mp1.put(b.getAccountcode(), b);
        }
        return mp1;
    }

    private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km) {
        List<FseJyeVO> list = new ArrayList<FseJyeVO>();
        for (FseJyeVO fsejyevo : map.values()) {
            if (fsejyevo.getKmbm().equals(km)) {
                list.add(fsejyevo);
            }
        }
        return list;
    }

    // 按传入科目方向
    private CwgyInfoVO getCwgyInfoVO(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq,
                                     CwgyInfoVO vo, String... kms) {

        DZFDouble ufd = null;
        int direction = 0;
        int len = kms == null ? 0 : kms.length;
        YntCpaccountVO km = null;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            km = mp.get(kms[i]);
            if (km == null)
                continue;
            direction = km.getDirection();
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {

                if (direction == 0) {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(getDZFDouble(fvo.getEndfsjf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(getDZFDouble(fvo.getJftotal())));
                } else {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(getDZFDouble(fvo.getEndfsdf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(getDZFDouble(fvo.getDftotal())));
                }
            }
        }
        return vo;
    }

    // 按传入科目方向 取净额(借-贷，或 贷-借)
    private CwgyInfoVO getCwgyInfoVOToJe(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq,
                                         CwgyInfoVO vo, String... kms) {

        DZFDouble ufd = null;
        int direction = 0;
        int len = kms == null ? 0 : kms.length;
        YntCpaccountVO km = null;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            km = mp.get(kms[i]);
            if (km == null)
                continue;
            direction = km.getDirection();
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {
                if (direction == 0) {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsjf(), fvo.getEndfsdf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getJftotal(), fvo.getDftotal())));
                } else {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsdf(), fvo.getEndfsjf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getDftotal(), fvo.getJftotal())));
                }
            }
        }
        return vo;
    }


    // 按传入方向计算
    private CwgyInfoVO getCwgyInfoVOFX(Map<String, FseJyeVO> map, int direction, String rq, CwgyInfoVO vo,
                                       String... kms) {
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {

                if (direction == 0) {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(getDZFDouble(fvo.getEndfsjf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(getDZFDouble(fvo.getJftotal())));
                } else {
                    if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                        ufd = getDZFDouble(vo.getByje());
                        vo.setByje(ufd.add(getDZFDouble(fvo.getEndfsdf())));
                    }
                    ufd = getDZFDouble(vo.getBnljje());
                    vo.setBnljje(ufd.add(getDZFDouble(fvo.getDftotal())));
                }
            }
        }
        return vo;
    }


    private CwgyInfoVO getCwgyInfoVOYe(Map<String, FseJyeVO> map, String rq, CwgyInfoVO vo,
                                       String... kms) {
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {
                if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                    ufd = getDZFDouble(vo.getByje());
                    vo.setByje(ufd.add(SafeCompute.add(fvo.getQmjf(), fvo.getQmdf())));
                }
                ufd = getDZFDouble(vo.getBnljje());
                vo.setBnljje(ufd.add(SafeCompute.add(fvo.getQmjf(), fvo.getQmdf())));
            }
        }
        return vo;
    }


    // 按传入科目取期初
    private CwgyInfoVO getCwgyInfoVOQc(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq,
                                       CwgyInfoVO vo, String... kms) {
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        YntCpaccountVO km = null;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            km = mp.get(kms[i]);
            if (km == null)
                continue;
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {
                vo.setByje(fvo.getLastmqcdf());
            }
        }
        return vo;
    }

    // 计算贷方减借方
    private CwgyInfoVO getCwgyInfoVOD_J(Map<String, FseJyeVO> map, String rq, CwgyInfoVO vo, String... kms) {
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        List<FseJyeVO> ls = null;
        for (int i = 0; i < len; i++) {
            ls = getData(map, kms[i]);
            for (FseJyeVO fvo : ls) {

                if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
                    ufd = getDZFDouble(vo.getByje());
                    ufd = ufd.add(getDZFDouble(fvo.getEndfsdf())).sub(getDZFDouble(fvo.getEndfsjf()));
                    vo.setByje(ufd);
                }
                ufd = getDZFDouble(vo.getBnljje());
                ufd = ufd.add(getDZFDouble(fvo.getDftotal())).sub(getDZFDouble(fvo.getJftotal()));
                vo.setBnljje(ufd);
            }
        }
        return vo;
    }


    private CwgyInfoVO getZcfzXmInfo(ZcFzBVO[] zcfzbvos, String xmname, boolean isfz) {
        CwgyInfoVO vo71 = new CwgyInfoVO();

        if (zcfzbvos != null && zcfzbvos.length > 0) {
            for (ZcFzBVO zcfz : zcfzbvos) {
                if (isfz) {
                    if (StringUtil.isEmpty(zcfz.getFzhsyzqy())) {
                        continue;
                    }
                    if (xmname.equals(zcfz.getFzhsyzqy().trim())) {
                        vo71.setByje(zcfz.getQmye2());
                        break;
                    }
                } else {
                    if (StringUtil.isEmpty(zcfz.getZc())) {
                        continue;
                    }
                    if (xmname.equals(zcfz.getZc().trim())) {
                        vo71.setByje(zcfz.getQmye1());
                        break;
                    }
                }
            }
        }
        return vo71;
    }

    private Object[] getLastFseJyeVO(FsYeQueryVO vo, CorpVO corpvo) throws Exception {

        DZFDate enddate = vo.getEnddate();
        String prePeriod = (new StringBuilder(String.valueOf(enddate.getYear() - 1))).append("-")
                .append(enddate.getMonth() >= 10 ? ((Object) (Integer.valueOf(enddate.getMonth())))
                        : ((Object) ((new StringBuilder("0")).append(enddate.getMonth()).toString())))
                .toString();

        vo.setBegindate1(DateUtils.getPeriodStartDate(prePeriod));
        vo.setEnddate(DateUtils.getPeriodEndDate(prePeriod));

        DZFDate corpdate = corpvo.getBegindate();
        // 查询日期在建账日期之前 直接返回
        if ((vo.getEnddate() != null && vo.getEnddate().before(corpdate))) {
            // if (voclone.getBegindate1().getYear() < corpdate.getYear()) {
            return null;
            // }
        }
        Object[] obj = fsYeService.getFsJyeVOs1(vo);

        return obj;
    }


    private CwgyInfoVO getLastJlr2013(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq) {

        CwgyInfoVO vo11 = new CwgyInfoVO();
        vo11.setXm("营业收入");
        // 营业收入取自利润表中本年和本期金额
        vo11 = getCwgyInfoVO(map, mp, rq, vo11, "5001", "5051");
        CwgyInfoVO vo12 = new CwgyInfoVO();
        // vo2.setXm("减：营业成本");
        vo12 = getCwgyInfoVO(map, mp, rq, vo12, "5401", "5402");

        CwgyInfoVO vo13 = new CwgyInfoVO();
        // vo3.setXm("税金及附加");
        vo13 = getCwgyInfoVO(map, mp, rq, vo13, "5403");

        CwgyInfoVO vo21 = new CwgyInfoVO();
        vo21.setXm("销售费用");
        // 销售费用 贷方金额
        vo21 = getCwgyInfoVO(map, mp, rq, vo21, "5601");

        CwgyInfoVO vo22 = new CwgyInfoVO();
        vo22.setXm("管理费用");
        // 管理费用 贷方金额
        vo22 = getCwgyInfoVO(map, mp, rq, vo22, "5602");

        CwgyInfoVO vo23 = new CwgyInfoVO();
        vo23.setXm("财务费用");
        // 财务费用贷方金额
        vo23 = getCwgyInfoVO(map, mp, rq, vo23, "5603");

        CwgyInfoVO vo15 = new CwgyInfoVO();
        // vo20.setXm("加：投资收益（损失以“-”号填列）");
        vo15 = getCwgyInfoVO(map, mp, rq, vo15, "5111");

        CwgyInfoVO vo16 = new CwgyInfoVO();
        // vo21.setXm("二、营业利润（亏损以“-”号填列） ");
        vo16.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(vo12.getByje())).sub(getDZFDouble(vo13.getByje()))
                .sub(getDZFDouble(vo21.getByje())).sub(getDZFDouble(vo22.getByje())).sub(getDZFDouble(vo23.getByje()))
                .add(getDZFDouble(vo15.getByje())));
        vo16.setBnljje(
                getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(vo12.getBnljje())).sub(getDZFDouble(vo13.getBnljje()))
                        .sub(getDZFDouble(vo21.getBnljje())).sub(getDZFDouble(vo22.getBnljje()))
                        .sub(getDZFDouble(vo23.getBnljje())).add(getDZFDouble(vo15.getBnljje())));

        CwgyInfoVO vo17 = new CwgyInfoVO();
        // vo22.setXm("加：营业外收入");
        vo17 = getCwgyInfoVO(map, mp, rq, vo17, "5301");

        CwgyInfoVO vo18 = new CwgyInfoVO();
        // vo24.setXm("减：营业外支出");
        vo18 = getCwgyInfoVO(map, mp, rq, vo18, "5711");

        CwgyInfoVO vo19 = new CwgyInfoVO();
        // vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
        vo19.setByje(getDZFDouble(vo16.getByje()).add(getDZFDouble(vo17.getByje())).sub(getDZFDouble(vo18.getByje())));
        vo19.setBnljje(
                getDZFDouble(vo16.getBnljje()).add(getDZFDouble(vo17.getBnljje())).sub(getDZFDouble(vo18.getBnljje())));

        CwgyInfoVO vo191 = new CwgyInfoVO();
        // vo31.setXm("减：所得税费用");
        vo191 = getCwgyInfoVO(map, mp, rq, vo191, "5801");

        CwgyInfoVO vo14 = new CwgyInfoVO();
        // 净利润取自利润表中本年和本期金额
        vo14.setByje(getDZFDouble(vo19.getByje()).sub(getDZFDouble(vo191.getByje())));
        vo14.setBnljje(getDZFDouble(vo19.getBnljje()).sub(getDZFDouble(vo191.getBnljje())));

        return vo14;

    }

    private CwgyInfoVO getLastJlr2007(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq) {

        CwgyInfoVO vo11 = new CwgyInfoVO();
        vo11.setXm("营业收入");
        // 营业收入取自利润表中本年和本期金额
        vo11 = getCwgyInfoVO(map, mp, rq, vo11, "6001", "6051");
        CwgyInfoVO vo12 = new CwgyInfoVO();
        // vo2.setXm("减：营业成本");
        vo12 = getCwgyInfoVO(map, mp, rq, vo12, "6401", "6402");

        CwgyInfoVO vo13 = new CwgyInfoVO();
        // vo3.setXm("税金及附加");
        vo13 = getCwgyInfoVO(map, mp, rq, vo13, "6403");

        CwgyInfoVO vo21 = new CwgyInfoVO();
        vo21.setXm("销售费用");
        // 销售费用 贷方金额
        vo21 = getCwgyInfoVO(map, mp, rq, vo21, "6601");

        CwgyInfoVO vo22 = new CwgyInfoVO();
        vo22.setXm("管理费用");
        // 管理费用 贷方金额
        vo22 = getCwgyInfoVO(map, mp, rq, vo22, "6602");

        CwgyInfoVO vo23 = new CwgyInfoVO();
        vo23.setXm("财务费用");
        // 财务费用贷方金额
        vo23 = getCwgyInfoVO(map, mp, rq, vo23, "6603");

        CwgyInfoVO vo111 = new CwgyInfoVO();
        // 资产减值损失
        vo111 = getCwgyInfoVO(map, mp, rq, vo111, "6701");

        CwgyInfoVO vo112 = new CwgyInfoVO();
        // 加：公允价值变动收益（损失以“-”号填列
        vo112 = getCwgyInfoVO(map, mp, rq, vo112, "6101");

        CwgyInfoVO vo15 = new CwgyInfoVO();
        // vo20.setXm("加：投资收益（损失以“-”号填列）");
        vo15 = getCwgyInfoVO(map, mp, rq, vo15, "6111");

        CwgyInfoVO vo16 = new CwgyInfoVO();
        // vo21.setXm("二、营业利润（亏损以“-”号填列） ");
        vo16.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(vo12.getByje())).sub(getDZFDouble(vo13.getByje()))
                .sub(getDZFDouble(vo21.getByje())).sub(getDZFDouble(vo22.getByje())).sub(getDZFDouble(vo23.getByje()))
                .add(getDZFDouble(vo15.getByje())).sub(getDZFDouble(vo111.getByje()))
                .add(getDZFDouble(vo112.getByje())));
        vo16.setBnljje(
                getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(vo12.getBnljje())).sub(getDZFDouble(vo13.getBnljje()))
                        .sub(getDZFDouble(vo21.getBnljje())).sub(getDZFDouble(vo22.getBnljje()))
                        .sub(getDZFDouble(vo23.getBnljje())).add(getDZFDouble(vo15.getBnljje()))
                        .sub(getDZFDouble(vo111.getBnljje())).add(getDZFDouble(vo112.getBnljje())));

        CwgyInfoVO vo17 = new CwgyInfoVO();
        // vo22.setXm("加：营业外收入");
        vo17 = getCwgyInfoVO(map, mp, rq, vo17, "6301");

        CwgyInfoVO vo18 = new CwgyInfoVO();
        // vo24.setXm("减：营业外支出");
        vo18 = getCwgyInfoVO(map, mp, rq, vo18, "6711");

        CwgyInfoVO vo19 = new CwgyInfoVO();
        // vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
        vo19.setByje(getDZFDouble(vo16.getByje()).add(getDZFDouble(vo17.getByje())).sub(getDZFDouble(vo18.getByje())));
        vo19.setBnljje(
                getDZFDouble(vo16.getBnljje()).add(getDZFDouble(vo17.getBnljje())).sub(getDZFDouble(vo18.getBnljje())));

        CwgyInfoVO vo191 = new CwgyInfoVO();
        // vo31.setXm("减：所得税费用");
        vo191 = getCwgyInfoVO(map, mp, rq, vo191, "6801");

        CwgyInfoVO vo14 = new CwgyInfoVO();
        // 净利润取自利润表中本年和本期金额
        vo14.setByje(getDZFDouble(vo19.getByje()).sub(getDZFDouble(vo191.getByje())));
        vo14.setBnljje(getDZFDouble(vo19.getBnljje()).sub(getDZFDouble(vo191.getBnljje())));

        return vo14;

    }

    private CwgyInfoVO[] getLRB2013VOs(CorpVO corpvo, String rq, Object[] obj, FsYeQueryVO vo, DZFBoolean bzxkj)
            throws Exception {
        String pk_corp = corpvo.getPk_corp();
        // String rq = vo.getQjz();
        // Object[] obj = gl_rep_fsyebserv.getFsJyeVOs1(vo);
        FseJyeVO[] fvos = (FseJyeVO[]) obj[0];
        Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
        mp = convert(mp);
        Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
        int len = fvos == null ? 0 : fvos.length;
        for (int i = 0; i < len; i++) {
            if (fvos[i] != null) {
                map.put(fvos[i].getKmbm(), fvos[i]);
            }
        }

        DZFDouble d100 = new DZFDouble(100);
        CwgyInfoVO vo11 = new CwgyInfoVO();
        vo11.setXm("营业收入");
        // 营业收入取自利润表中本年和本期金额
        vo11 = getCwgyInfoVO(map, mp, rq, vo11, "5001", "5051");
        vo11.setXmfl("盈利状况");
        vo11.setHs("1");

        CwgyInfoVO vo12 = new CwgyInfoVO();
        // vo2.setXm("减：营业成本");
        vo12 = getCwgyInfoVO(map, mp, rq, vo12, "5401", "5402");

        CwgyInfoVO vo13 = new CwgyInfoVO();
        // vo3.setXm("税金及附加");
        vo13 = getCwgyInfoVO(map, mp, rq, vo13, "5403");

        CwgyInfoVO vo21 = new CwgyInfoVO();

        vo21.setXm("销售费用");
        // 销售费用 贷方金额
        vo21 = getCwgyInfoVO(map, mp, rq, vo21, "5601");
        vo21.setHs("2");
        vo21.setXmfl("三项费用");
        CwgyInfoVO vo22 = new CwgyInfoVO();
        vo22.setXm("管理费用");
        // 管理费用 贷方金额
        vo22 = getCwgyInfoVO(map, mp, rq, vo22, "5602");
        vo22.setHs("2");
        vo22.setXmfl("三项费用");
        CwgyInfoVO vo23 = new CwgyInfoVO();
        vo23.setXm("财务费用");
        // 财务费用贷方金额
        vo23 = getCwgyInfoVO(map, mp, rq, vo23, "5603");
        vo23.setHs("2");
        vo23.setXmfl("三项费用");
        CwgyInfoVO vo24 = new CwgyInfoVO();
        // （管理费用+销售费用+财务费用）/营业收入
        vo24.setXm("费用比率");
        vo24.setByje(getDZFDouble(vo21.getByje()).add(getDZFDouble(vo22.getByje())).add(getDZFDouble(vo23.getByje()))
                .div(getDZFDouble(vo11.getByje())).multiply(d100));
        vo24.setBnljje(getDZFDouble(vo21.getBnljje()).add(getDZFDouble(vo22.getBnljje()))
                .add(getDZFDouble(vo23.getBnljje())).div(getDZFDouble(vo11.getBnljje())).multiply(d100));

        vo24.setHs("2");
        vo24.setXmfl("三项费用");

        CwgyInfoVO vo15 = new CwgyInfoVO();
        // vo20.setXm("加：投资收益（损失以“-”号填列）");
        vo15 = getCwgyInfoVO(map, mp, rq, vo15, "5111");

        CwgyInfoVO vo16 = new CwgyInfoVO();
        // vo21.setXm("二、营业利润（亏损以“-”号填列） ");
        vo16.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(vo12.getByje())).sub(getDZFDouble(vo13.getByje()))
                .sub(getDZFDouble(vo21.getByje())).sub(getDZFDouble(vo22.getByje())).sub(getDZFDouble(vo23.getByje()))
                .add(getDZFDouble(vo15.getByje())));
        vo16.setBnljje(
                getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(vo12.getBnljje())).sub(getDZFDouble(vo13.getBnljje()))
                        .sub(getDZFDouble(vo21.getBnljje())).sub(getDZFDouble(vo22.getBnljje()))
                        .sub(getDZFDouble(vo23.getBnljje())).add(getDZFDouble(vo15.getBnljje())));

        CwgyInfoVO vo17 = new CwgyInfoVO();
        // vo22.setXm("加：营业外收入");
        vo17 = getCwgyInfoVO(map, mp, rq, vo17, "5301");

        CwgyInfoVO vo18 = new CwgyInfoVO();
        // vo24.setXm("减：营业外支出");
        vo18 = getCwgyInfoVO(map, mp, rq, vo18, "5711");

        CwgyInfoVO vo19 = new CwgyInfoVO();
        // vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
        vo19.setByje(getDZFDouble(vo16.getByje()).add(getDZFDouble(vo17.getByje())).sub(getDZFDouble(vo18.getByje())));
        vo19.setBnljje(
                getDZFDouble(vo16.getBnljje()).add(getDZFDouble(vo17.getBnljje())).sub(getDZFDouble(vo18.getBnljje())));

        CwgyInfoVO vo191 = new CwgyInfoVO();
        // vo31.setXm("减：所得税费用");
        vo191 = getCwgyInfoVO(map, mp, rq, vo191, "5801");

        CwgyInfoVO vo14 = new CwgyInfoVO();
        vo14.setXm("净利润");
        // 净利润取自利润表中本年和本期金额
        vo14.setByje(getDZFDouble(vo19.getByje()).sub(getDZFDouble(vo191.getByje())));
        vo14.setBnljje(getDZFDouble(vo19.getBnljje()).sub(getDZFDouble(vo191.getBnljje())));
        vo14.setXmfl("盈利状况");
        vo14.setHs("1");
        CwgyInfoVO vo141 = new CwgyInfoVO();
        // 净利润/营业收入
        vo141.setXm("营业收入净利率");
        vo141.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo11.getByje())).multiply(d100));
        vo141.setBnljje(getDZFDouble(vo14.getBnljje()).div(getDZFDouble(vo11.getBnljje())).multiply(d100));
        vo141.setXmfl("盈利状况");
        vo141.setHs("1");

        CwgyInfoVO vo31 = new CwgyInfoVO();
        vo31.setXm("应收账款");
        // （应收账款、其他应收款）借方金额
        vo31 = getCwgyInfoVOToJe(map, mp, rq, vo31, "1122");//, "1221"
        vo31.setXmfl("应收应付");
        vo31.setHs("3");
        CwgyInfoVO vo32 = new CwgyInfoVO();
        vo32.setXm("应付账款");
        // 应付账款、其他应付款）贷方金额
        vo32 = getCwgyInfoVOToJe(map, mp, rq, vo32, "2202");//, "2241"
        vo32.setXmfl("应收应付");
        vo32.setHs("3");

        CwgyInfoVO vo41 = new CwgyInfoVO();
        vo41.setXm("资金收入");
        // 库存现金、银行存款及其他货币资金 借方
        vo41 = getCwgyInfoVO(map, mp, rq, vo41, "1001", "1002", "1012");
        vo41.setXmfl("资金状况");
        vo41.setHs("4");
        CwgyInfoVO vo42 = new CwgyInfoVO();
        vo42.setXm("资金支付");
        // 应付账款、其他应付款 贷方
        vo42 = getCwgyInfoVO(map, mp, rq, vo42, "2202", "2241");
        vo42.setXmfl("资金状况");
        vo42.setHs("4");
        CwgyInfoVO vo43 = new CwgyInfoVO();
        vo43.setXm("资金收支");
        // vo43 = getCwgyInfoVO(map, mp, rq, vo43, "5603");
        // 资金收入-资金支付
        vo43.setByje(getDZFDouble(vo41.getByje()).sub(getDZFDouble(vo42.getByje())));
        vo43.setBnljje(getDZFDouble(vo41.getBnljje()).sub(getDZFDouble(vo42.getBnljje())));
        vo43.setXmfl("资金状况");
        vo43.setHs("4");

        CwgyInfoVO vo51 = new CwgyInfoVO();
        vo51.setXm("增值税");

        String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人" : corpvo.getChargedeptname();
        String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
        String newRuleCode = null;
        if ("小规模纳税人".equals(chargedeptname)) {
            // 应交税费/应交增值税/销项税额的借方金额（小规模纳税人）
            //“22210102销项税贷方发生额-22210106减免税款借方发生额”
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo51 = getCwgyInfoVOFX(map, 1, rq, vo51, newRuleCode);
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210106", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo51_temp = new CwgyInfoVO();
            vo51_temp = getCwgyInfoVOFX(map, 0, rq, vo51_temp, newRuleCode);
            vo51.setBnljje(SafeCompute.sub(vo51.getBnljje(), vo51_temp.getBnljje()));
            vo51.setByje(SafeCompute.sub(vo51.getByje(), vo51_temp.getByje()));
        } else {
            // 应交税费/未交增值税的借方金额（一般纳税人）
//			newRuleCode = gl_accountcoderule.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
//			vo51 = getCwgyInfoVOFX(map, 0, rq, vo51, newRuleCode);
            //本年累计“222101应交税费/应交增值税”余额
            newRuleCode = zxkjPlatformService.getNewRuleCode("222101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo51 = getCwgyInfoVOYe(map, rq, vo51, newRuleCode);
        }
        vo51.setXmfl("税金状况");
        vo51.setHs("5");

        CwgyInfoVO vo52 = new CwgyInfoVO();
        vo52.setXm("城建税");
        // 应交税费/应交城建税借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo52 = getCwgyInfoVOFX(map, 1, rq, vo52, newRuleCode);
        vo52.setXmfl("税金状况");
        vo52.setHs("5");
        CwgyInfoVO vo53 = new CwgyInfoVO();
        vo53.setXm("教育附加");
        // 应交税费/应交教育附加借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222103", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo53 = getCwgyInfoVOFX(map, 1, rq, vo53, newRuleCode);
        vo53.setXmfl("税金状况");
        vo53.setHs("5");
        CwgyInfoVO vo54 = new CwgyInfoVO();
        vo54.setXm("地方教育附加");
        // 应交税费/应交地方教育附加借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222104", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo54 = getCwgyInfoVOFX(map, 1, rq, vo54, newRuleCode);
        vo54.setXmfl("税金状况");
        vo54.setHs("5");
        CwgyInfoVO vo55 = new CwgyInfoVO();
        vo55.setXm("个税");
        // 应交税费/应交个人所得税借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222105", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo55 = getCwgyInfoVOFX(map, 1, rq, vo55, newRuleCode);
        vo55.setXmfl("税金状况");
        vo55.setHs("5");
        CwgyInfoVO vo56 = new CwgyInfoVO();
        vo56.setXm("增值税税负率");

        CwgyInfoVO vo562 = new CwgyInfoVO();
        vo562.setXm("主营业务收入");
        vo562 = getCwgyInfoVO(map, mp, rq, vo562, "5001", "5051");

        if ("小规模纳税人".equals(chargedeptname)) {
            // 当月销项净发生额=（当月贷方发生额-当月借方发生额）
            // 2.当月主营业务收入净发生=（当月贷方发生额-当月借方发生额）
            // 3.当月税负率=当月销项净发生额/ 当月主营业务收入净发生*100％
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo561 = new CwgyInfoVO();
            vo561.setXm("销项税额");//这个没用，暂时先留着
            vo561 = getCwgyInfoVO(map, mp, rq, vo561, newRuleCode);

            vo56.setByje(getDZFDouble(vo51.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));
            vo56.setBnljje(getDZFDouble(vo51.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        } else {
            // 本期税负率
            // 1.当月主营业务收入净发生=（当月贷方发生额-当月借方发生额）
            // 判断如果上期留底税额=未交增值税本期期初负的贷方余额=（上期销项发生-上期进项发生）<0
            // 那么，本期税负率=（本期销项发净生-本期进项净发生）+（上期留底税额是个负数）／当月主营业务收入净发生
            // 判断如果上期留底税额=未交增值税本期期初负的贷方余额=（上期销项发生-上期进项发生>=0
            // 那么，本期税负率=（本期销项净发生-本期进项净发生）／当月主营业务收入净发生
            CwgyInfoVO vo564 = new CwgyInfoVO();
            vo564.setXm("进项税额");
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo564 = getCwgyInfoVOFX(map, 0, rq, vo564, newRuleCode);

            CwgyInfoVO vo565 = new CwgyInfoVO();
//			vo565.setXm("销项税额(与小规模共用)");
            vo565.setXm("销项税额");
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo565 = getCwgyInfoVO(map, mp, rq, vo565, newRuleCode);

            DZFDouble sqld = DZFDouble.ZERO_DBL;
            // 上期留底税额
            // sqld = getLastPeriodSQLD(vo, queryAccountRule);
            newRuleCode = zxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo569 = new CwgyInfoVO();
            vo569.setXm("未交增值税期初");
            vo569 = getCwgyInfoVOQc(map, mp, rq, vo569, newRuleCode);
            sqld = getDZFDouble(vo569.getByje());

            if (sqld.compareTo(DZFDouble.ZERO_DBL) > 0)
                sqld = DZFDouble.ZERO_DBL;

//			vo56.setByje(getDZFDouble(vo565.getByje()).sub(getDZFDouble(vo564.getByje())).add(sqld)
//					.div(getDZFDouble(vo562.getByje())).multiply(d100));
            vo56.setBnljje(getDZFDouble(vo51.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));

            // 累计税负率
            // 累计税负=应交税费未交增值税累计贷方净发生/主营业务收入累计贷方发生*100％，
            // 应交税费未交增值税累计贷方净发生=累计贷方发生-累计借方发生
            CwgyInfoVO vo561 = new CwgyInfoVO();
            vo561.setXm("未交增值税");
            newRuleCode = zxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo561 = getCwgyInfoVOD_J(map, rq, vo561, newRuleCode);

//			vo56.setBnljje(getDZFDouble(vo561.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
            vo56.setBnljje(getDZFDouble(vo51.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        }
        //
        vo56.setXmfl("税金状况");
        vo56.setHs("5");

        CwgyInfoVO vo61 = new CwgyInfoVO();
        vo61.setXm("净利率");
        // 净利率 = 净利润/ 主营业务收入*100
        vo61.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));
        vo61.setBnljje(getDZFDouble(vo14.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        vo61.setXmfl("盈利能力");
        vo61.setHs("6");

        CwgyInfoVO vo62 = new CwgyInfoVO();
        CwgyInfoVO vo621 = new CwgyInfoVO();
        vo621.setXm("主营业成本");
        vo621 = getCwgyInfoVO(map, mp, rq, vo621, "5401");
        vo62.setXm("毛利率");
        // 毛利率 = （主营业务收入-主营业成本）/主营业务收入*100
        vo62.setByje(getDZFDouble(vo562.getByje()).sub(getDZFDouble(vo621.getByje())).div(getDZFDouble(vo562.getByje()))
                .multiply(d100));
        vo62.setBnljje(getDZFDouble(vo562.getBnljje()).sub(getDZFDouble(vo621.getBnljje()))
                .div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        vo62.setXmfl("盈利能力");
        vo62.setHs("6");
        String[] hasyes = new String[]{"N", "N", "N", "N", "N"};
        ZcFzBVO[] zcfzbvos = zcFzBService.getZcfzVOs(pk_corp, hasyes, mp, fvos);

        CwgyInfoVO vo71 = new CwgyInfoVO();
        vo71.setXm("资产负债率");
        // 负债率 = 负债总额/资产总和*100
        CwgyInfoVO vo711 = getZcfzXmInfo(zcfzbvos, "负债合计", true);
        CwgyInfoVO vo712 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo71.setByje(getDZFDouble(vo711.getByje()).div(getDZFDouble(vo712.getByje())).multiply(d100));
        vo71.setXmfl("偿债能力");
        vo71.setHs("7");

        CwgyInfoVO vo72 = new CwgyInfoVO();
        vo72.setXm("流动比率");
        // 流动比率 = 流动资产合计/流动负债合计*100
        CwgyInfoVO vo721 = getZcfzXmInfo(zcfzbvos, "流动负债合计", true);
        CwgyInfoVO vo722 = getZcfzXmInfo(zcfzbvos, "流动资产合计", false);
        vo72.setByje(getDZFDouble(vo722.getByje()).div(getDZFDouble(vo721.getByje())).multiply(d100));
        vo72.setXmfl("偿债能力");
        vo72.setHs("7");

        CwgyInfoVO vo81 = new CwgyInfoVO();
        vo81.setXm("应收账款周转率");
        // 应收账款周转率 = 营业收入 /应收账款
        CwgyInfoVO vo811 = getZcfzXmInfo(zcfzbvos, "　应收账款", false);
        vo81.setByje(getDZFDouble(vo11.getByje()).div(getDZFDouble(vo811.getByje())).multiply(d100));
        vo81.setXmfl("运营能力");
        vo81.setHs("8");

        CwgyInfoVO vo82 = new CwgyInfoVO();
        vo82.setXm("资产周转率");
        // 资产周转率 = 营业收入 /资产总额
        CwgyInfoVO vo821 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo82.setByje(getDZFDouble(vo11.getByje()).div(getDZFDouble(vo821.getByje())).multiply(d100));
        vo82.setXmfl("运营能力");
        vo82.setHs("8");

        CwgyInfoVO vo91 = new CwgyInfoVO();
        vo91.setXm("收入增长率");
        vo91.setXmfl("发展能力");
        vo91.setHs("9");

        CwgyInfoVO vo92 = new CwgyInfoVO();
        vo92.setXm("净利润增长率");
        vo92.setXmfl("发展能力");
        vo92.setHs("9");

        if (vo != null) {
            Object[] objs = getLastFseJyeVO(vo, corpvo);

            if (objs != null && objs.length > 0) {
                Map<String, YntCpaccountVO> lastmp = (Map<String, YntCpaccountVO>) objs[1];
                lastmp = convert(lastmp);
                FseJyeVO[] lastfvos = (FseJyeVO[]) objs[0];
                Map<String, FseJyeVO> lastmap = new HashMap<String, FseJyeVO>();
                int lastlen = lastfvos == null ? 0 : lastfvos.length;
                for (int i = 0; i < lastlen; i++) {
                    if (lastfvos[i] != null) {
                        lastmap.put(lastfvos[i].getKmbm(), lastfvos[i]);
                    }
                }

                DZFDate enddate = vo.getEnddate();
                String prePeriod = (new StringBuilder(String.valueOf(enddate.getYear() - 1))).append("-")
                        .append(enddate.getMonth() >= 10 ? ((Object) (Integer.valueOf(enddate.getMonth())))
                                : ((Object) ((new StringBuilder("0")).append(enddate.getMonth()).toString())))
                        .toString();
                CwgyInfoVO lastvo = new CwgyInfoVO();
                // 营业收入取自利润表中本年和本期金额
                // 本期营业收入增长额＝本期营业收入总额一上年同期营业收入总额
                // 收入增长率：(营业收入增长额/上年营业收入总额)×100%
                lastvo = getCwgyInfoVO(lastmap, lastmp, prePeriod, lastvo, "5001", "5051");
                vo91.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(lastvo.getByje()))
                        .div(getDZFDouble(lastvo.getByje())).multiply(d100));
                vo91.setBnljje(getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(lastvo.getBnljje()))
                        .div(getDZFDouble(lastvo.getBnljje())).multiply(d100));
                vo11.setByje_pre(lastvo.getByje());//上年同期数
                vo11.setBnljje_pre(lastvo.getBnljje());//上年同期累计

                // 本期净利润增长率：(当期净利润-上期净利润)/上期净利润*100%
                // 取数：（利润表当期净利润-上期净利润）/上期净利润*100%
                CwgyInfoVO lastjlr = getLastJlr2013(lastmap, lastmp, prePeriod);

                vo92.setByje(getDZFDouble(vo14.getByje()).sub(getDZFDouble(lastjlr.getByje()))
                        .div(getDZFDouble(lastjlr.getByje())).multiply(d100));
                vo92.setBnljje(getDZFDouble(vo14.getBnljje()).sub(getDZFDouble(lastjlr.getBnljje()))
                        .div(getDZFDouble(lastjlr.getBnljje())).multiply(d100));
                vo14.setByje_pre(lastjlr.getByje());//上年同期数据
                vo14.setBnljje_pre(lastjlr.getBnljje());//上年同期累计
            }
        }

        CwgyInfoVO vo93 = new CwgyInfoVO();
        vo93.setXm("净资产收益率");
        // 净资产收益率 = 净利润/资产总计
        CwgyInfoVO vo931 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo93.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo931.getByje())).multiply(d100));
        vo93.setXmfl("发展能力");
        vo93.setHs("9");
        if (bzxkj != null && bzxkj.booleanValue()) {
            return new CwgyInfoVO[]{vo11, vo14, vo141, vo21, vo22, vo23, vo24, vo31, vo32, vo41, vo42, vo43, vo51, vo52,
                    vo53, vo54, vo55, vo56, vo61, vo62, vo71, vo72, vo81, vo82, vo91, vo92, vo93};
        } else {
            return new CwgyInfoVO[]{vo562, vo621, vo11, vo14, vo141, vo21, vo22, vo23, vo24, vo31, vo32, vo41, vo42, vo43, vo51, vo52,
                    vo53, vo54, vo55, vo56, vo61, vo62, vo71, vo72, vo81, vo82, vo91, vo92, vo93};
        }
    }

    private CwgyInfoVO[] getLRB2007VOs(CorpVO corpvo, String rq, Object[] obj, FsYeQueryVO vo, DZFBoolean bfzxkj)
            throws Exception {
        String pk_corp = corpvo.getPk_corp();
        // String rq = vo.getQjz();
        // Object[] obj = gl_rep_fsyebserv.getFsJyeVOs1(vo);
        FseJyeVO[] fvos = (FseJyeVO[]) obj[0];
        Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
        mp = convert(mp);
        Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
        int len = fvos == null ? 0 : fvos.length;
        for (int i = 0; i < len; i++) {
            if (fvos[i] != null) {
                map.put(fvos[i].getKmbm(), fvos[i]);
            }
        }

        DZFDouble d100 = new DZFDouble(100);
        CwgyInfoVO vo11 = new CwgyInfoVO();
        vo11.setXm("营业收入");
        // 营业收入取自利润表中本年和本期金额
        vo11 = getCwgyInfoVO(map, mp, rq, vo11, "6001", "6051");
        vo11.setXmfl("盈利状况");
        vo11.setHs("1");

        CwgyInfoVO vo12 = new CwgyInfoVO();
        // vo2.setXm("减：营业成本");
        vo12 = getCwgyInfoVO(map, mp, rq, vo12, "6401", "6402");

        CwgyInfoVO vo13 = new CwgyInfoVO();
        // vo3.setXm("税金及附加");
        vo13 = getCwgyInfoVO(map, mp, rq, vo13, "6403");

        CwgyInfoVO vo21 = new CwgyInfoVO();

        vo21.setXm("销售费用");
        // 销售费用 贷方金额
        vo21 = getCwgyInfoVO(map, mp, rq, vo21, "6601");
        vo21.setHs("2");
        vo21.setXmfl("三项费用");
        CwgyInfoVO vo22 = new CwgyInfoVO();
        vo22.setXm("管理费用");
        // 管理费用 贷方金额
        vo22 = getCwgyInfoVO(map, mp, rq, vo22, "6602");
        vo22.setHs("2");
        vo22.setXmfl("三项费用");
        CwgyInfoVO vo23 = new CwgyInfoVO();
        vo23.setXm("财务费用");
        // 财务费用贷方金额
        vo23 = getCwgyInfoVO(map, mp, rq, vo23, "6603");
        vo23.setHs("2");
        vo23.setXmfl("三项费用");
        CwgyInfoVO vo24 = new CwgyInfoVO();
        // （管理费用+销售费用+财务费用）/营业收入
        vo24.setXm("费用比率");
        vo24.setByje(getDZFDouble(vo21.getByje()).add(getDZFDouble(vo22.getByje())).add(getDZFDouble(vo23.getByje()))
                .div(getDZFDouble(vo11.getByje())).multiply(d100));
        vo24.setBnljje(getDZFDouble(vo21.getBnljje()).add(getDZFDouble(vo22.getBnljje()))
                .add(getDZFDouble(vo23.getBnljje())).div(getDZFDouble(vo11.getBnljje())).multiply(d100));

        vo24.setHs("2");
        vo24.setXmfl("三项费用");

        CwgyInfoVO vo111 = new CwgyInfoVO();
        // 资产减值损失
        vo111 = getCwgyInfoVO(map, mp, rq, vo111, "6701");

        CwgyInfoVO vo112 = new CwgyInfoVO();
        // 加：公允价值变动收益（损失以“-”号填列
        vo112 = getCwgyInfoVO(map, mp, rq, vo112, "6101");

        CwgyInfoVO vo15 = new CwgyInfoVO();
        // vo20.setXm("加：投资收益（损失以“-”号填列）");
        vo15 = getCwgyInfoVO(map, mp, rq, vo15, "6111");

        CwgyInfoVO vo16 = new CwgyInfoVO();
        // vo21.setXm("二、营业利润（亏损以“-”号填列） ");
        vo16.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(vo12.getByje())).sub(getDZFDouble(vo13.getByje()))
                .sub(getDZFDouble(vo21.getByje())).sub(getDZFDouble(vo22.getByje())).sub(getDZFDouble(vo23.getByje()))
                .add(getDZFDouble(vo15.getByje())).sub(getDZFDouble(vo111.getByje()))
                .add(getDZFDouble(vo112.getByje())));
        vo16.setBnljje(
                getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(vo12.getBnljje())).sub(getDZFDouble(vo13.getBnljje()))
                        .sub(getDZFDouble(vo21.getBnljje())).sub(getDZFDouble(vo22.getBnljje()))
                        .sub(getDZFDouble(vo23.getBnljje())).add(getDZFDouble(vo15.getBnljje()))
                        .sub(getDZFDouble(vo111.getBnljje())).add(getDZFDouble(vo112.getBnljje())));

        CwgyInfoVO vo17 = new CwgyInfoVO();
        // vo22.setXm("加：营业外收入");
        vo17 = getCwgyInfoVO(map, mp, rq, vo17, "6301");

        CwgyInfoVO vo18 = new CwgyInfoVO();
        // vo24.setXm("减：营业外支出");
        vo18 = getCwgyInfoVO(map, mp, rq, vo18, "6711");

        CwgyInfoVO vo19 = new CwgyInfoVO();
        // vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
        vo19.setByje(getDZFDouble(vo16.getByje()).add(getDZFDouble(vo17.getByje())).sub(getDZFDouble(vo18.getByje())));
        vo19.setBnljje(
                getDZFDouble(vo16.getBnljje()).add(getDZFDouble(vo17.getBnljje())).sub(getDZFDouble(vo18.getBnljje())));

        CwgyInfoVO vo191 = new CwgyInfoVO();
        // vo31.setXm("减：所得税费用");
        vo191 = getCwgyInfoVO(map, mp, rq, vo191, "6801");

        CwgyInfoVO vo14 = new CwgyInfoVO();
        vo14.setXm("净利润");
        // 净利润取自利润表中本年和本期金额
        vo14.setByje(getDZFDouble(vo19.getByje()).sub(getDZFDouble(vo191.getByje())));
        vo14.setBnljje(getDZFDouble(vo19.getBnljje()).sub(getDZFDouble(vo191.getBnljje())));
        vo14.setXmfl("盈利状况");
        vo14.setHs("1");
        CwgyInfoVO vo141 = new CwgyInfoVO();
        // 净利润/营业收入
        vo141.setXm("营业收入净利率");
        vo141.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo11.getByje())).multiply(d100));
        vo141.setBnljje(getDZFDouble(vo14.getBnljje()).div(getDZFDouble(vo11.getBnljje())).multiply(d100));
        vo141.setXmfl("盈利状况");
        vo141.setHs("1");

        CwgyInfoVO vo31 = new CwgyInfoVO();
        vo31.setXm("应收账款");
        // （应收账款、其他应收款）借方金额
        vo31 = getCwgyInfoVOToJe(map, mp, rq, vo31, "1122");//, "1221"
        vo31.setXmfl("应收应付");
        vo31.setHs("3");
        CwgyInfoVO vo32 = new CwgyInfoVO();
        vo32.setXm("应付账款");
        // 应付账款、其他应付款）贷方金额
        vo32 = getCwgyInfoVOToJe(map, mp, rq, vo32, "2202");//, "2241"
        vo32.setXmfl("应收应付");
        vo32.setHs("3");

        CwgyInfoVO vo41 = new CwgyInfoVO();
        vo41.setXm("资金收入");
        // 库存现金、银行存款及其他货币资金 借方
        vo41 = getCwgyInfoVO(map, mp, rq, vo41, "1001", "1002", "1012");
        vo41.setXmfl("资金状况");
        vo41.setHs("4");
        CwgyInfoVO vo42 = new CwgyInfoVO();
        vo42.setXm("资金支付");
        // 应付账款、其他应付款 贷方
        vo42 = getCwgyInfoVO(map, mp, rq, vo42, "2202", "2241");
        vo42.setXmfl("资金状况");
        vo42.setHs("4");
        CwgyInfoVO vo43 = new CwgyInfoVO();
        vo43.setXm("资金收支");
        // vo43 = getCwgyInfoVO(map, mp, rq, vo43, "5603");
        // 资金收入-资金支付
        vo43.setByje(getDZFDouble(vo41.getByje()).sub(getDZFDouble(vo42.getByje())));
        vo43.setBnljje(getDZFDouble(vo41.getBnljje()).sub(getDZFDouble(vo42.getBnljje())));
        vo43.setXmfl("资金状况");
        vo43.setHs("4");

        CwgyInfoVO vo51 = new CwgyInfoVO();
        vo51.setXm("增值税");

        String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人" : corpvo.getChargedeptname();
        String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
        String newRuleCode = null;
        if ("小规模纳税人".equals(chargedeptname)) {
            // 应交税费/应交增值税/销项税额的借方金额（小规模纳税人）
            //“22210102销项税贷方发生额-22210106减免税款借方发生额”
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo51 = getCwgyInfoVOFX(map, 1, rq, vo51, newRuleCode);
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210106", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo51_temp = new CwgyInfoVO();
            vo51_temp = getCwgyInfoVOFX(map, 0, rq, vo51_temp, newRuleCode);
            vo51.setBnljje(SafeCompute.sub(vo51.getBnljje(), vo51_temp.getBnljje()));
            vo51.setByje(SafeCompute.sub(vo51.getByje(), vo51_temp.getByje()));
        } else {
            // 应交税费/未交增值税的借方金额（一般纳税人）
//			newRuleCode = gl_accountcoderule.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
//			vo51 = getCwgyInfoVOFX(map, 0, rq, vo51, newRuleCode);
            //本年累计“222101应交税费/应交增值税”余额
            newRuleCode = zxkjPlatformService.getNewRuleCode("222101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo51 = getCwgyInfoVOYe(map, rq, vo51, newRuleCode);
        }
        vo51.setXmfl("税金状况");
        vo51.setHs("5");

        CwgyInfoVO vo52 = new CwgyInfoVO();
        vo52.setXm("城建税");
        // 应交税费/应交城建税借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo52 = getCwgyInfoVOFX(map, 1, rq, vo52, newRuleCode);
        vo52.setXmfl("税金状况");
        vo52.setHs("5");
        CwgyInfoVO vo53 = new CwgyInfoVO();
        vo53.setXm("教育附加");
        // 应交税费/应交教育附加借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222103", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo53 = getCwgyInfoVOFX(map, 1, rq, vo53, newRuleCode);
        vo53.setXmfl("税金状况");
        vo53.setHs("5");
        CwgyInfoVO vo54 = new CwgyInfoVO();
        vo54.setXm("地方教育附加");
        // 应交税费/应交地方教育附加借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222104", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo54 = getCwgyInfoVOFX(map, 1, rq, vo54, newRuleCode);
        vo54.setXmfl("税金状况");
        vo54.setHs("5");
        CwgyInfoVO vo55 = new CwgyInfoVO();
        vo55.setXm("个税");
        // 应交税费/应交个人所得税借方金额
        newRuleCode = zxkjPlatformService.getNewRuleCode("222105", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        vo55 = getCwgyInfoVOFX(map, 1, rq, vo55, newRuleCode);
        vo55.setXmfl("税金状况");
        vo55.setHs("5");
        CwgyInfoVO vo56 = new CwgyInfoVO();

        CwgyInfoVO vo562 = new CwgyInfoVO();
        vo562.setXm("主营业务收入");
        vo562 = getCwgyInfoVO(map, mp, rq, vo562, "6001", "6051");
        vo56.setXm("增值税税负率");
        if ("小规模纳税人".equals(chargedeptname)) {
            // 1.当月销项税额=“发生额及余额表”当月贷方发生额
            // 2.当月主营业务收入发生额=“发生额及余额表”当月贷方发生额
            // 3.当月税负率=当月销项发生额/ 当月主营业务收入发生*100％
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo561 = new CwgyInfoVO();
            vo561.setXm("销项税额");
            vo561 = getCwgyInfoVO(map, mp, rq, vo561, newRuleCode);
//			vo56.setByje(getDZFDouble(vo561.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));
//			// 1.销项累计发生额=“发生额及余额表”贷方累计发生额
//			// 2.主营业务收入累计发生额=“发生额及余额表”贷方累计发生额
//			// 3.累计税负率=销项累计发生额/主营业务收入累计发生额*100％
//			vo56.setBnljje(getDZFDouble(vo561.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));

            vo56.setByje(getDZFDouble(vo51.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));
            // 1.销项累计发生额=“发生额及余额表”贷方累计发生额
            // 2.主营业务收入累计发生额=“发生额及余额表”贷方累计发生额
            // 3.累计税负率=销项累计发生额/主营业务收入累计发生额*100％
            vo56.setBnljje(getDZFDouble(vo51.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        } else {
            // 本期税负率
            // 1.当月主营业务收入净发生=（当月贷方发生额-当月借方发生额）
            // 判断如果上期留底税额=未交增值税本期期初负的贷方余额=（上期销项发生-上期进项发生）<0
            // 那么，本期税负率=（本期销项发净生-本期进项净发生）+（上期留底税额是个负数）／当月主营业务收入净发生
            // 判断如果上期留底税额=未交增值税本期期初负的贷方余额=（上期销项发生-上期进项发生>=0
            // 那么，本期税负率=（本期销项净发生-本期进项净发生）／当月主营业务收入净发生
            CwgyInfoVO vo564 = new CwgyInfoVO();
            vo564.setXm("进项税额");
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo564 = getCwgyInfoVOFX(map, 0, rq, vo564, newRuleCode);

            CwgyInfoVO vo565 = new CwgyInfoVO();
//			vo565.setXm("销项税额(与小规模共用)");
            vo565.setXm("销项税额");
            newRuleCode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo565 = getCwgyInfoVO(map, mp, rq, vo565, newRuleCode);

            DZFDouble sqld = DZFDouble.ZERO_DBL;
            // 上期留底税额
            // sqld = getLastPeriodSQLD(vo, queryAccountRule);
            newRuleCode = zxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            CwgyInfoVO vo569 = new CwgyInfoVO();
            vo569.setXm("未交增值税期初");
            vo569 = getCwgyInfoVOQc(map, mp, rq, vo569, newRuleCode);
            sqld = getDZFDouble(vo569.getByje());

            if (sqld.compareTo(DZFDouble.ZERO_DBL) > 0)
                sqld = DZFDouble.ZERO_DBL;

//			vo56.setByje(getDZFDouble(vo565.getByje()).sub(getDZFDouble(vo564.getByje())).add(sqld)
//					.div(getDZFDouble(vo562.getByje())).multiply(d100));
            vo56.setByje(getDZFDouble(vo51.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));

            // 累计税负=应交税费未交增值税累计贷方净发生/主营业务收入累计贷方发生*100％，
            // 应交税费未交增值税累计贷方净发生=累计贷方发生-累计借方发生
            CwgyInfoVO vo561 = new CwgyInfoVO();
            vo561.setXm("未交增值税");
            newRuleCode = zxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
            vo561 = getCwgyInfoVOD_J(map, rq, vo561, newRuleCode);
//			vo56.setBnljje(getDZFDouble(vo561.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
            vo56.setBnljje(getDZFDouble(vo51.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        }
        //
        vo56.setXmfl("税金状况");
        vo56.setHs("5");

        vo56.setXmfl("税金状况");
        vo56.setHs("5");

        CwgyInfoVO vo61 = new CwgyInfoVO();
        vo61.setXm("净利率");
        // 净利率 = 净利润/ 主营业务收入*100
        vo61.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo562.getByje())).multiply(d100));
        vo61.setBnljje(getDZFDouble(vo14.getBnljje()).div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        vo61.setXmfl("盈利能力");
        vo61.setHs("6");

        CwgyInfoVO vo62 = new CwgyInfoVO();
        CwgyInfoVO vo621 = new CwgyInfoVO();
        vo621.setXm("主营业成本");
        vo621 = getCwgyInfoVO(map, mp, rq, vo621, "6401");
        vo62.setXm("毛利率");
        // 毛利率 = （主营业务收入-主营业成本）/主营业务收入*100
        vo62.setByje(getDZFDouble(vo562.getByje()).sub(getDZFDouble(vo621.getByje())).div(getDZFDouble(vo562.getByje()))
                .multiply(d100));
        vo62.setBnljje(getDZFDouble(vo562.getBnljje()).sub(getDZFDouble(vo621.getBnljje()))
                .div(getDZFDouble(vo562.getBnljje())).multiply(d100));
        vo62.setXmfl("盈利能力");
        vo62.setHs("6");
        String[] hasyes = new String[]{"N", "N", "N", "N", "N"};
        ZcFzBVO[] zcfzbvos = zcFzBService.getZcfzVOs(pk_corp, hasyes, mp, fvos);

        CwgyInfoVO vo71 = new CwgyInfoVO();
        vo71.setXm("资产负债率");
        // 负债率 = 负债总额/资产总和*100
        CwgyInfoVO vo711 = getZcfzXmInfo(zcfzbvos, "负债合计", true);
        CwgyInfoVO vo712 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo71.setByje(getDZFDouble(vo711.getByje()).div(getDZFDouble(vo712.getByje())).multiply(d100));
        vo71.setXmfl("偿债能力");
        vo71.setHs("7");

        CwgyInfoVO vo72 = new CwgyInfoVO();
        vo72.setXm("流动比率");
        // 流动比率 = 流动资产合计/流动负债合计*100
        CwgyInfoVO vo721 = getZcfzXmInfo(zcfzbvos, "流动负债合计", true);
        CwgyInfoVO vo722 = getZcfzXmInfo(zcfzbvos, "流动资产合计", false);
        vo72.setByje(getDZFDouble(vo722.getByje()).div(getDZFDouble(vo721.getByje())).multiply(d100));
        vo72.setXmfl("偿债能力");
        vo72.setHs("7");

        CwgyInfoVO vo81 = new CwgyInfoVO();
        vo81.setXm("应收账款周转率");
        // 应收账款周转率 = 营业收入 /应收账款
        CwgyInfoVO vo811 = getZcfzXmInfo(zcfzbvos, "　应收账款", false);
        vo81.setByje(getDZFDouble(vo11.getByje()).div(getDZFDouble(vo811.getByje())).multiply(d100));
        vo81.setXmfl("运营能力");
        vo81.setHs("8");

        CwgyInfoVO vo82 = new CwgyInfoVO();
        vo82.setXm("资产周转率");
        // 资产周转率 = 营业收入 /资产总额
        CwgyInfoVO vo821 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo82.setByje(getDZFDouble(vo11.getByje()).div(getDZFDouble(vo821.getByje())).multiply(d100));
        vo82.setXmfl("运营能力");
        vo82.setHs("8");

        CwgyInfoVO vo91 = new CwgyInfoVO();
        vo91.setXm("收入增长率");
        vo91.setXmfl("发展能力");
        vo91.setHs("9");

        CwgyInfoVO vo92 = new CwgyInfoVO();
        vo92.setXm("净利润增长率");
        vo92.setXmfl("发展能力");
        vo92.setHs("9");

        if (vo != null) {
            Object[] objs = getLastFseJyeVO(vo, corpvo);

            if (objs != null && objs.length > 0) {
                Map<String, YntCpaccountVO> lastmp = (Map<String, YntCpaccountVO>) objs[1];
                lastmp = convert(lastmp);
                FseJyeVO[] lastfvos = (FseJyeVO[]) objs[0];
                Map<String, FseJyeVO> lastmap = new HashMap<String, FseJyeVO>();
                int lastlen = lastfvos == null ? 0 : lastfvos.length;
                for (int i = 0; i < lastlen; i++) {
                    if (lastfvos[i] != null) {
                        lastmap.put(lastfvos[i].getKmbm(), lastfvos[i]);
                    }
                }

                DZFDate enddate = vo.getEnddate();
                String prePeriod = (new StringBuilder(String.valueOf(enddate.getYear() - 1))).append("-")
                        .append(enddate.getMonth() >= 10 ? ((Object) (Integer.valueOf(enddate.getMonth())))
                                : ((Object) ((new StringBuilder("0")).append(enddate.getMonth()).toString())))
                        .toString();
                CwgyInfoVO lastvo = new CwgyInfoVO();
                // 营业收入取自利润表中本年和本期金额
                // 本期营业收入增长额＝本期营业收入总额一上年同期营业收入总额
                // 收入增长率：(营业收入增长额/上年营业收入总额)×100%
                lastvo = getCwgyInfoVO(lastmap, lastmp, prePeriod, lastvo, "6001", "6051");
                vo91.setByje(getDZFDouble(vo11.getByje()).sub(getDZFDouble(lastvo.getByje()))
                        .div(getDZFDouble(lastvo.getByje())).multiply(d100));
                vo91.setBnljje(getDZFDouble(vo11.getBnljje()).sub(getDZFDouble(lastvo.getBnljje()))
                        .div(getDZFDouble(lastvo.getBnljje())).multiply(d100));
                vo11.setByje_pre(lastvo.getByje());
                vo11.setBnljje_pre(lastvo.getBnljje());

                // 本期净利润增长率：(当期净利润-上期净利润)/上期净利润*100%
                // 取数：（利润表当期净利润-上期净利润）/上期净利润*100%
                CwgyInfoVO lastjlr = getLastJlr2007(lastmap, lastmp, prePeriod);

                vo92.setByje(getDZFDouble(vo14.getByje()).sub(getDZFDouble(lastjlr.getByje()))
                        .div(getDZFDouble(lastjlr.getByje())).multiply(d100));
                vo92.setBnljje(getDZFDouble(vo14.getBnljje()).sub(getDZFDouble(lastjlr.getBnljje()))
                        .div(getDZFDouble(lastjlr.getBnljje())).multiply(d100));
                vo14.setByje_pre(lastjlr.getByje());
                vo14.setBnljje_pre(lastjlr.getBnljje());
            }
        }

        CwgyInfoVO vo93 = new CwgyInfoVO();
        vo93.setXm("净资产收益率");
        // 净资产收益率 = 净利润/资产总计
        CwgyInfoVO vo931 = getZcfzXmInfo(zcfzbvos, "资产总计", false);
        vo93.setByje(getDZFDouble(vo14.getByje()).div(getDZFDouble(vo931.getByje())).multiply(d100));
        vo93.setXmfl("发展能力");
        vo93.setHs("9");
        if (bfzxkj != null && bfzxkj.booleanValue()) {
            return new CwgyInfoVO[]{vo11, vo14, vo141, vo21, vo22, vo23, vo24, vo31, vo32, vo41, vo42, vo43, vo51, vo52,
                    vo53, vo54, vo55, vo56, vo61, vo62, vo71, vo72, vo81, vo82, vo91, vo92, vo93};
        } else {
            return new CwgyInfoVO[]{vo562, vo621, vo11, vo14, vo141, vo21, vo22, vo23, vo24, vo31, vo32, vo41, vo42, vo43, vo51, vo52,
                    vo53, vo54, vo55, vo56, vo61, vo62, vo71, vo72, vo81, vo82, vo91, vo92, vo93};
        }
    }

    private CwgyInfoVO[] getLastVo(CorpVO corpVO, Integer corpschema, CwgyInfoQueryVO queryVO) {
        return null;
    }

    private CwgyInfoVO[] getCurVo(CorpVO corpVO, Integer corpschema, CwgyInfoQueryVO queryVO) {
        return null;
    }

    @Override
    public Map<String, CwgyInfoVO[]> getCwgyInfoVOs(String year, CorpVO corpVO, Object[] obj) throws Exception {
        Map<String, CwgyInfoVO[]> resmap = new HashMap<>();
        Integer corpschema = zxkjPlatformService.getAccountSchema(corpVO.getPk_corp());
        // Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp);
        // 获取期间
        Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
        Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
        if (monthmap != null && monthmap.size() > 0) {
            CwgyInfoVO[] vos = null;
            String period = "";
            FsYeQueryVO paramvo = new FsYeQueryVO();
            paramvo.setPk_corp(corpVO.getPk_corp());
            paramvo.setIshasjz(DZFBoolean.FALSE);
            paramvo.setXswyewfs(DZFBoolean.FALSE);
            paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
            paramvo.setCjq(1);
            paramvo.setCjz(6);
            for (Map.Entry entry : monthmap.entrySet()) {
                period = (String) entry.getKey();
                paramvo.setQjq(period);
                paramvo.setQjz(period);
                paramvo.setBegindate1(DateUtils.getPeriodStartDate(period));
                paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
                List<FseJyeVO> fslist = (List<FseJyeVO>) entry.getValue();
                Object[] objfs = new Object[2];
                objfs[0] = fslist.toArray(new FseJyeVO[0]);
                objfs[1] = mp;
                if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
                    // 2007会计准则
                    vos = getLRB2007VOs(corpVO, period, objfs, paramvo, DZFBoolean.FALSE);
                } else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
                    vos = getLRB2013VOs(corpVO, period, objfs, paramvo, DZFBoolean.FALSE);
                } else {
                    throw new RuntimeException("该制度暂不支持财务概要信息表!");
                }
                resmap.put(period, vos);
            }
        }
        return resmap;
    }
}
