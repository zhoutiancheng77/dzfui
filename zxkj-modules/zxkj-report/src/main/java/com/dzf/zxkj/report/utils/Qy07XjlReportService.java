package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.enums.KmschemaCash;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Slf4j
@SuppressWarnings("all")
public class Qy07XjlReportService {

    private IZxkjPlatformService zxkjPlatformService;

    public Qy07XjlReportService(IZxkjPlatformService zxkjPlatformService) {
        this.zxkjPlatformService = zxkjPlatformService;
    }

    public XjllbVO[] getXJLLB2007VOs1(Map<Float, XjllbVO> map, String period, String pk_corp, DZFDate corpDate, String corptype,
                                      String pk_trade_accountschema, Map<String, FseJyeVO> fsmap) throws DZFWarpException {

        XjllbVO voWu = getXjllB2007Xm(map);

        // 加：期初现金及现金等价物余额=库存现金、银行存款、其他货币资金的期初余额累计，前面三个科目的本年发生额累计(借方累计-贷方累计)
        XjllbVO vo34 = new XjllbVO();
        vo34.setXm("　　加：期初现金及现金等价物余额");
        vo34.setHc("37");
        vo34.setRowno(37);
        vo34.setBkmqc(DZFBoolean.TRUE);
        // 取库存现金、银行存款、其他货币资金这三个科目的期初
        // 如果查询月份小于建账日期
        DZFDate corpdate = new DZFDate(DateUtils.getPeriod(corpDate) + "-01");
        DZFDate currdate = new DZFDate(period + "-01");
        // ZcFzBVO[] zfzcbvosn = null;
        // if(!currdate.before(corpdate)){//当前期间一般都是在建账日期前
        // zfzcbvosn= gl_rep_zcfzserv.getZCFZBVOs(period, pk_corp, "N",
        // "N");//年初
        // }
        getSanKmQcmny(fsmap, corpdate, vo34, period, pk_corp, corptype, DZFBoolean.TRUE);
        map.put(vo34.getRowno(), vo34);// vec.add(vo34) ;

        XjllbVO voLiu = new XjllbVO();
        voLiu.setXm("六、期末现金及现金等价物余额");
        voLiu.setHc("38");
        voLiu.setRowno(38);
        voLiu.setBkmqm(DZFBoolean.TRUE);
        if (currdate.before(corpdate)) {
            voLiu.setBqje(voWu.getBqje().add(vo34.getBqje()));
            voLiu.setSqje(voWu.getSqje().add(vo34.getSqje()));
        } else {
            getSanKmQmMny(fsmap, voLiu, DZFBoolean.TRUE, pk_corp, corptype);
        }
        map.put(voLiu.getRowno(), voLiu);// vec.add(voLiu) ;

        XjllbVO[] xvos = new XjllbVO[map.size()];
        Float[] fs = (Float[]) map.keySet().toArray(new Float[0]);
        Arrays.sort(fs);
        int len = map.size();
        for (int i = 0; i < len; i++) {
            xvos[i] = map.get(fs[i]);
            try {
                xvos[i].setHc_id("XJLL-" + String.format("%03d", Integer.parseInt(xvos[i].getHc())));
            } catch (NumberFormatException e) {
                log.error("现金流量表:" + e.getMessage());
            }
        }
        map = null;

        return xvos;
    }

    private String getFormula(float s, float e) {
        StringBuffer res = new StringBuffer();
        for (float i = s; i <= e; i += 1) {
            res.append("hc(" + new Float(i).intValue() + ")+");
        }
        return res.toString().substring(0, res.length() - 1);
    }

    public XjllbVO getXjllB2007Xm(Map<Float, XjllbVO> map) {
        DZFDouble[] ds = null;
        XjllbVO vo0 = new XjllbVO();
        vo0.setXm("一、经营活动产生的现金流量：");
        vo0.setRowno(1f);
        vo0.setHc("1");
        map.put(vo0.getRowno(), vo0);// vec.add(vo0) ;

        ds = getSumXJLLB(map, 2, 4);
        DZFDouble bqje1to3 = ds[0];
        DZFDouble bnje1to3 = ds[1];
        XjllbVO vo4 = new XjllbVO();
        vo4.setXm("　　经营活动现金流入小计");
        vo4.setHc("5");
        vo4.setRowno(5);
        vo4.setBqje(bqje1to3);
        vo4.setSqje(bnje1to3);
        vo4.setFormula(getFormula(2, 4));
        map.put(vo4.getRowno(), vo4);// vec.add(vo4) ;

        // 查询行号为5-8的现金流量项目
        ds = getSumXJLLB(map, 6, 9);
        DZFDouble bqje5to8 = ds[0];
        DZFDouble bnje5to8 = ds[1];
        XjllbVO vo9 = new XjllbVO();
        vo9.setXm("　　经营活动现金流出小计");
        vo9.setHc("10");
        vo9.setRowno(10);
        vo9.setBqje(bqje5to8);
        vo9.setSqje(bnje5to8);
        vo9.setFormula(getFormula(6, 9));
        map.put(vo9.getRowno(), vo9);// vec.add(vo9) ;

        XjllbVO vo10 = new XjllbVO();
        vo10.setXm("　　经营活动产生的现金流量净额");
        vo10.setHc("11");
        vo10.setRowno(11);
        vo10.setBqje(vo4.getBqje().sub(vo9.getBqje()));
        vo10.setSqje(vo4.getSqje().sub(vo9.getSqje()));
        vo10.setFormula("hc(" + vo4.getHc() + ") - hc(" + vo9.getHc() + ")");
        map.put(vo10.getRowno(), vo10);// vec.add(vo10) ;

        XjllbVO voEr = new XjllbVO();
        voEr.setXm("二、投资活动产生的现金流量：");
        voEr.setRowno(12f);
        voEr.setHc("12");
        map.put(voEr.getRowno(), voEr);// vec.add(voEr) ;

        ds = getSumXJLLB(map, 13, 17);
        DZFDouble bqje11to15 = ds[0];
        DZFDouble bnje11to15 = ds[1];
        XjllbVO vo16 = new XjllbVO();
        vo16.setXm("　　投资活动现金流入小计");
        vo16.setHc("18");
        vo16.setRowno(18);
        vo16.setBqje(bqje11to15);
        vo16.setSqje(bnje11to15);
        vo16.setFormula(getFormula(13, 17));
        map.put(vo16.getRowno(), vo16);// vec.add(vo16) ;

        ds = getSumXJLLB(map, 19, 22);
        DZFDouble bqje17to20 = ds[0];
        DZFDouble bnje17to20 = ds[1];
        XjllbVO vo21 = new XjllbVO();
        vo21.setXm("　　投资活动现金流出小计");
        vo21.setHc("23");
        vo21.setRowno(23);
        vo21.setBqje(bqje17to20);
        vo21.setSqje(bnje17to20);
        vo21.setFormula(getFormula(19, 22));
        map.put(vo21.getRowno(), vo21);// vec.add(vo21) ;

        XjllbVO vo22 = new XjllbVO();
        vo22.setXm("　　投资活动产生的现金流量净额");
        vo22.setHc("24");
        vo22.setRowno(24);
        vo22.setBqje(vo16.getBqje().sub(vo21.getBqje()));
        vo22.setSqje(vo16.getSqje().sub(vo21.getSqje()));
        vo22.setFormula("hc(" + vo16.getHc() + ") - hc(" + vo21.getHc() + ")");
        map.put(vo22.getRowno(), vo22);// vec.add(vo22) ;

        XjllbVO voSan = new XjllbVO();
        voSan.setXm("三、筹资活动产生的现金流量：");
        voSan.setRowno(25f);
        voSan.setHc("25");
        map.put(voSan.getRowno(), voSan);// vec.add(voSan) ;

        ds = getSumXJLLB(map, 26, 28);
        DZFDouble bqje23to25 = ds[0];
        DZFDouble bnje23to25 = ds[1];
        XjllbVO vo26 = new XjllbVO();
        vo26.setXm("　　筹资活动现金流入小计");
        vo26.setHc("29");
        vo26.setRowno(29);
        vo26.setBqje(bqje23to25);
        vo26.setSqje(bnje23to25);
        vo26.setFormula(getFormula(26, 28));
        map.put(vo26.getRowno(), vo26);// vec.add(vo26) ;

        ds = getSumXJLLB(map, 30, 32);
        DZFDouble bqje27to29 = ds[0];
        DZFDouble bnje27to29 = ds[1];
        XjllbVO vo30 = new XjllbVO();
        vo30.setXm("　　筹资活动现金流出小计");
        vo30.setHc("33");
        vo30.setRowno(33);
        vo30.setBqje(bqje27to29);
        vo30.setSqje(bnje27to29);
        vo30.setFormula(getFormula(30, 32));
        map.put(vo30.getRowno(), vo30);// vec.add(vo30) ;

        XjllbVO vo31 = new XjllbVO();
        vo31.setXm("　　筹资活动产生的现金流量净额");
        vo31.setHc("34");
        vo31.setRowno(34);
        vo31.setBqje(vo26.getBqje().sub(vo30.getBqje()));
        vo31.setSqje(vo26.getSqje().sub(vo30.getSqje()));
        vo31.setFormula("hc(" + vo26.getHc() + ") - hc(" + vo30.getHc() + ")");
        map.put(vo31.getRowno(), vo31);// vec.add(vo31) ;

        XjllbVO voSi = new XjllbVO();
        voSi.setXm("四、汇率变动对现金及现金等价物的影响");
        voSi.setHc("35");
        voSi.setRowno(35);
        map.put(voSi.getRowno(), voSi);// vec.add(voSi) ;

        XjllbVO voWu = new XjllbVO();
        voWu.setXm("五、现金及现金等价物净增加额");
        voWu.setHc("36");
        voWu.setRowno(36);
        voWu.setBqje(vo10.getBqje().add(vo22.getBqje()).add(vo31.getBqje()));
        voWu.setSqje(vo10.getSqje().add(vo22.getSqje()).add(vo31.getSqje()));
        voWu.setFormula("hc(" + vo10.getHc() + ") + hc(" + vo22.getHc() + ")+ hc(" + vo31.getHc() + ")");
        voWu.setBxjlltotal(DZFBoolean.TRUE);
        map.put(voWu.getRowno(), voWu);// vec.add(voWu) ;
        return voWu;
    }

    private DZFDouble[] getSumXJLLB(Map<Float, XjllbVO> map, float s, float e) {
        DZFDouble bqje = DZFDouble.ZERO_DBL;
        DZFDouble bnje = DZFDouble.ZERO_DBL;
        XjllbVO xvo = null;
        if (map != null && map.size() > 0) {
            for (float i = s; i <= e; i += 1) {
                xvo = map.get(i);
                if (xvo == null) {
                    continue;
                }
                bqje = SafeCompute.add(bqje, DZFDouble.getUFDouble(xvo.getBqje()));
                bnje = SafeCompute.add(bnje, DZFDouble.getUFDouble(xvo.getSqje()));
            }
        }
        return new DZFDouble[]{bqje, bnje};
    }

    private void getSanKmQmMny(Map<String, FseJyeVO> fsmap, XjllbVO voLiu, DZFBoolean is07, String pk_corp, String corptype) {
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        Set<String> kmcode = KmschemaCash.getCashSubjectCode1(cpavos, corptype, DZFBoolean.TRUE);
        DZFDouble sqje = DZFDouble.ZERO_DBL;
        DZFDouble bqje = DZFDouble.ZERO_DBL;
        FseJyeVO fvo1 = null;
        for (String str : kmcode) {
            fvo1 = fsmap.get(str);
            if (fvo1 != null) {
                sqje = sqje.add(SafeCompute.add(fvo1.getQmjf(), fvo1.getQmdf()));
                bqje = bqje.add(SafeCompute.add(fvo1.getQmjf(), fvo1.getQmdf()));
            }
        }
        voLiu.setBqje(bqje);
        voLiu.setSqje(sqje);
    }

    /**
     * 取库存现金、银行存款、其他货币资金这三个科目的期初，及本年累计发生
     *
     * @param vo
     * @param period
     * @param pk_corp
     * @throws BusinessException
     */
    private void getSanKmQcmny(Map<String, FseJyeVO> fsmap, DZFDate corpdate,
                               XjllbVO vo, String period, String pk_corp, String corpType, DZFBoolean is07) throws DZFWarpException {
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        Set<String> kmcode = KmschemaCash.getCashSubjectCode1(cpavos, corpType, DZFBoolean.TRUE);
        DZFDouble sqje = DZFDouble.ZERO_DBL;
        DZFDouble bqje = DZFDouble.ZERO_DBL;
        FseJyeVO fvo1 = null;
        for (String str : kmcode) {
            fvo1 = fsmap.get(str);
            if (fvo1 != null) {
                sqje = sqje.add(SafeCompute.add(fvo1.getQcjf(), fvo1.getQcdf()));
                bqje = bqje.add(SafeCompute.add(fvo1.getLastmqcjf(), fvo1.getLastmqcdf()));
            }
        }
        vo.setSqje(sqje);
        vo.setBqje(bqje);
    }

}
