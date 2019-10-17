package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
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
public class XQy13XjlReportService {

    private IZxkjPlatformService zxkjPlatformService;

    public XQy13XjlReportService(IZxkjPlatformService zxkjPlatformService) {
        this.zxkjPlatformService = zxkjPlatformService;
    }

    public XjllbVO[] getXJLLB2013VOs1(Map<Float, XjllbVO> map, String period, String pk_corp, DZFDate corpDate, String corpType,
                                      String pk_trade_accountschema, Map<String, FseJyeVO> fsmap) throws DZFWarpException {
        XjllbVO voSi = getXjllB2013Xm(map);

        XjllbVO voWu = new XjllbVO();
        voWu.setXm("五、期初现金余额");
        voWu.setHc("21");
        voWu.setRowno(21);
        voWu.setBkmqc(DZFBoolean.TRUE);
        // 取库存现金、银行存款、其他货币资金这三个科目的期初
        // 如果查询月份小于建账日期
        DZFDate corpdate = new DZFDate(DateUtils.getPeriod(corpDate) + "-01");
        DZFDate currdate = new DZFDate(period + "-01");
        // ZcFzBVO[] zfzcbvosn = null;
        // if(!currdate.before(corpdate)){
        // zfzcbvosn= gl_rep_zcfzserv.getZCFZBVOs(period, pk_corp, "N",
        // "N");//年初
        // }
        getSanKmQcmny(fsmap, corpdate, corpType, voWu, period, pk_corp, DZFBoolean.FALSE);
        // 取科目的期初值

        map.put(voWu.getRowno(), voWu);// vec.add(voWu) ;

        XjllbVO voLiu = new XjllbVO();
        voLiu.setXm("六、期末现金余额");
        voLiu.setHc("22");
        voLiu.setRowno(22);
        voLiu.setBkmqm(DZFBoolean.TRUE);
        if (currdate.before(corpdate)) {
            voLiu.setBqje(DZFDouble.getUFDouble(voSi.getBqje()).add(DZFDouble.getUFDouble(voWu.getBqje())));
            voLiu.setSqje(DZFDouble.getUFDouble(voSi.getSqje()).add(DZFDouble.getUFDouble(voWu.getSqje())));
        } else {
            getSanKmQmMny(fsmap, voLiu, DZFBoolean.FALSE, pk_corp, corpType);
        }
        map.put(voLiu.getRowno(), voLiu);// vec.add(voLiu) ;
        XjllbVO[] xvos = new XjllbVO[map.size()];
        Float[] fs = (Float[]) map.keySet().toArray(new Float[0]);
        Arrays.sort(fs);
        int len = map.size();
        for (int i = 0; i < len; i++) {
            xvos[i] = map.get(fs[i]);
            if (!StringUtil.isEmpty(xvos[i].getHc())) {
                try {
                    xvos[i].setHc_id("XJLL-" + String.format("%03d", Integer.parseInt(xvos[i].getHc())));
                } catch (NumberFormatException e) {
                    log.error("现金流量表:" + e.getMessage());
                }
            }
        }
        map = null;
        return xvos;
    }

    private String getFormula(float s, float e, String mark) {
        StringBuffer res = new StringBuffer();
        for (float i = s; i <= e; i += 1) {
            res.append("hc(" + new Float(i).intValue() + ")" + mark);
        }
        return res.toString().substring(0, res.length() - 1);
    }

    public XjllbVO getXjllB2013Xm(Map<Float, XjllbVO> map) {
        DZFDouble[] ds = null;
        XjllbVO vo0 = new XjllbVO();
        vo0.setXm("一、经营活动产生的现金流量：");
        vo0.setRowno(0.5f);
//		vo0.setHc("0.5");
        map.put(vo0.getRowno(), vo0);

        // 查询行号为2-3的现金流量项目
        ds = getSumXJLLB(map, 1, 2);
        DZFDouble bqje1to3 = ds[0];
        DZFDouble bnje1to3 = ds[1];

        // 查询行号为4-7的现金流量项目
        ds = getSumXJLLB(map, 3, 6);
        DZFDouble bqje4to6 = ds[0];
        DZFDouble bnje4to6 = ds[1];

        XjllbVO vo7 = new XjllbVO();
        vo7.setXm("　　经营活动产生的现金流量净额");
        vo7.setHc("7");
        vo7.setRowno(7);
        vo7.setBqje(bqje1to3.sub(bqje4to6));
        vo7.setSqje(bnje1to3.sub(bnje4to6));
        vo7.setFormula(getFormula(1, 2, "+") + "-" + getFormula(3, 6, "-"));
        map.put(vo7.getRowno(), vo7);

        XjllbVO voEr = new XjllbVO();
        voEr.setXm("二、投资活动产生的现金流量");
        voEr.setRowno(7.5f);
//		voEr.setHc("7.5");
        map.put(voEr.getRowno(), voEr);

        ds = getSumXJLLB(map, 8, 10);
        DZFDouble bqje8to10 = ds[0];
        DZFDouble bnje8to10 = ds[1];
        ds = getSumXJLLB(map, 11, 12);
        DZFDouble bqje11to12 = ds[0];
        DZFDouble bnje11to12 = ds[1];
        XjllbVO vo13 = new XjllbVO();
        vo13.setXm("　　投资活动产生的现金流量净额");
        vo13.setHc("13");
        vo13.setRowno(13);
        vo13.setBqje(bqje8to10.sub(bqje11to12));
        vo13.setSqje(bnje8to10.sub(bnje11to12));
        vo13.setFormula(getFormula(8, 10, "+") + "-" + getFormula(11, 12, "-"));
        map.put(vo13.getRowno(), vo13);

        XjllbVO voSan = new XjllbVO();
        voSan.setXm("三、筹资活动产生的现金流量：");
        voSan.setRowno(13.5f);
//		voSan.setHc("13.5");
        map.put(voSan.getRowno(), voSan);

        ds = getSumXJLLB(map, 14, 15);
        DZFDouble bqje14to15 = ds[0];
        DZFDouble bnje14to15 = ds[1];

        ds = getSumXJLLB(map, 16, 18);
        DZFDouble bqje16to18 = ds[0];
        DZFDouble bnje16to18 = ds[1];
        XjllbVO vo19 = new XjllbVO();
        vo19.setXm("　　筹资活动产生的现金流量净额");
        vo19.setHc("19");
        vo19.setRowno(19);
        vo19.setBqje(bqje14to15.sub(bqje16to18));
        vo19.setSqje(bnje14to15.sub(bnje16to18));
        vo19.setFormula(getFormula(14, 15, "+") + "-" + getFormula(16, 18, "-"));
        map.put(vo19.getRowno(), vo19);// vec.add(vo19) ;

        XjllbVO voSi = new XjllbVO();
        voSi.setXm("四、现金净增加额");
        voSi.setHc("20");
        voSi.setRowno(20);
        voSi.setBqje(vo7.getBqje().add(vo13.getBqje()).add(vo19.getBqje()));
        voSi.setSqje(vo7.getSqje().add(vo13.getSqje()).add(vo19.getSqje()));
        voSi.setFormula("hc(" + vo7.getHc() + ") + hc(" + vo13.getHc() + ")+ hc(" + vo19.getHc() + ")");
        voSi.setBxjlltotal(DZFBoolean.TRUE);
        map.put(voSi.getRowno(), voSi);// vec.add(voSi) ;
        return voSi;
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

    /**
     * 取库存现金、银行存款、其他货币资金这三个科目的期初，及本年累计发生
     *
     * @param vo
     * @param period
     * @param pk_corp
     * @throws DZFWarpException
     */
    private void getSanKmQcmny(Map<String, FseJyeVO> fsmap, DZFDate corpdate, String corpType,
                               XjllbVO vo, String period, String pk_corp, DZFBoolean is07) throws DZFWarpException {
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

    private void getSanKmQmMny(Map<String, FseJyeVO> fsmap, XjllbVO voLiu, DZFBoolean is07, String pk_corp, String corpType) {
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        Set<String> kmcode = KmschemaCash.getCashSubjectCode1(cpavos, corpType, DZFBoolean.TRUE);
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
}
