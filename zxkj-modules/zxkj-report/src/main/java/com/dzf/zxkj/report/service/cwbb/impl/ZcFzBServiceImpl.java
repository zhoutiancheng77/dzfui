package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.ZcfzMsgVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.service.cwbb.IRptSetService;
import com.dzf.zxkj.report.service.cwbb.IZcFzBService;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.utils.*;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class ZcFzBServiceImpl implements IZcFzBService {

    @Autowired
    private IRptSetService rptSetService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IFsYeService fsYeService;

    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String ishasye) throws Exception {
        String[] hasyes = null;
        if ("Y".equals(ishasye)) {
            hasyes = new String[]{"Y", "Y", "Y", "Y", "Y"};
        } else {
            hasyes = new String[]{"N", "N", "N", "N", "N"};
        }
        return getZCFZBVOs(period, pk_corp, ishasjz, hasyes);
    }

    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String[] hasyes) throws Exception {
        return getZCFZBVOsConXmids(period, pk_corp, ishasjz, hasyes, null);
    }

    @Override
    public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) throws Exception {
        if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
            hasyes = new String[]{"N", "N", "N", "N", "N"};
        }
        int year = new DZFDate(period + "-01").getYear();
        FsYeQueryVO vo = new FsYeQueryVO();
        vo.setPk_corp(pk_corp);
        vo.setQjq(year + "-01");
        vo.setQjz(period);
        vo.setIshasjz(new DZFBoolean(ishasjz));
        vo.setIshassh(DZFBoolean.TRUE);
        vo.setFirstlevelkms(rptSetService.queryZcfzKmFromDaima(pk_corp, xmids, hasyes));
        Map<String, YntCpaccountVO> mapc = null;
        if ("Y".equals(hasyes[0])) {
            vo.setCjq(1);
            vo.setCjz(6);
        }
        YntCpaccountVO[] cvos1 = zxkjPlatformService.queryByPk(pk_corp);
        mapc = ReportUtil.conVertMap(cvos1);
        FseJyeVO[] fvos = fsYeService.getFsJyeVOs(vo, 1);

        ZcFzBVO[] zcbvos = getZcfzVOs(pk_corp, hasyes, mapc, fvos);
        if (xmids != null && xmids.size() > 0
                && zcbvos != null && zcbvos.length > 0) {
            List<ZcFzBVO> bvoList = new ArrayList<ZcFzBVO>();
            for (ZcFzBVO bvo : zcbvos) {
                //按行次
                if (xmids.contains(bvo.getHc1())
                        || xmids.contains(bvo.getHc2())) {
                    bvoList.add(bvo);
                }
            }

            if (bvoList.size() > 0) {
                zcbvos = bvoList.toArray(new ZcFzBVO[0]);
            }
        }

        return zcbvos;
    }

    @Override
    public Object[] getZCFZBVOsConMsg(String period, String pk_corp, String ishasjz, String[] hasyes, CorpVO corpVO) throws Exception {
        Object[] objs = new Object[3];

        if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
            hasyes = new String[]{"N", "N", "N", "N", "N"};
        }
        int year = new DZFDate(period + "-01").getYear();
        FsYeQueryVO vo = new FsYeQueryVO();
        vo.setSfzxm(DZFBoolean.TRUE);
        vo.setPk_corp(pk_corp);
        vo.setQjq(year + "-01");
        vo.setQjz(period);
        vo.setIshasjz(new DZFBoolean(ishasjz));
        vo.setIshassh(DZFBoolean.TRUE);

        Map<String, YntCpaccountVO> mapc = null;
        YntCpaccountVO[] cvos1 = zxkjPlatformService.queryByPk(pk_corp);
        mapc = ReportUtil.conVertMap(cvos1);

        if ("Y".equals(hasyes[0])) {
            vo.setCjq(1);
            vo.setCjz(6);
        }

        FseJyeVO[] fvos = fsYeService.getFsJyeVOs(vo, 1);

        ZcFzBVO[] zcfzbvos = getZcfzVOs(pk_corp, hasyes, mapc, fvos);

        objs[0] = zcfzbvos;

        objs[1] = getNoBlanceMsg(fvos, corpVO, mapc, period);

        objs[2] = getZcfzJyx(zcfzbvos, pk_corp, corpVO);

        return objs;
    }


    /**
     * 获取校验项(项目全在一个vobean里面)
     *
     * @param fvos
     * @param zcfzbvos
     * @return
     */
    private ZcfzMsgVo getZcfzJyx(ZcFzBVO[] zcfzbvos, String pk_corp, CorpVO cpvo) {
        ZcfzMsgVo msgvo = new ZcfzMsgVo();

        Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);

        putJyxVo1(zcfzbvos, pk_corp, corpschema, msgvo);

        return msgvo;
    }


    private void putJyxVo1(ZcFzBVO[] zcfzbvos, String pk_corp, Integer corpschema, ZcfzMsgVo msgvo) {
        String zctotalname = "";
        String fztotalname = "";
        String qytotalname = "";
        DZFDouble zctotal = DZFDouble.ZERO_DBL;
        DZFDouble fztotal = DZFDouble.ZERO_DBL;
        DZFDouble qytotal = DZFDouble.ZERO_DBL;
        if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则 , 小企业会计准则
            zctotalname = "资产总计";
            fztotalname = "负债合计";
            qytotalname = "所有者权益（或股东权益)合计";
        } else if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则 , 企业会计准则
            zctotalname = "资产总计";
            fztotalname = "负债合计";
            qytotalname = "所有者权益(或股东权益)合计";
        } else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
            zctotalname = "资产总计";
            fztotalname = "负债合计";
            qytotalname = "净资产合计";
            msgvo.setJyxname("校验项:资产=负债+净资产");
        } else if (corpschema == DzfUtil.CAUSESCHEMA.intValue()) {// 事业
            zctotalname = "资产总计";
            fztotalname = "负债合计";
            qytotalname = "净资产合计";
            msgvo.setJyxname("校验项:资产=负债+净资产");
        } else if (corpschema == DzfUtil.VILLAGECOLLECTIVE.intValue()) {// 村集体
            zctotalname = "资产总计";
            fztotalname = "负债合计";
            qytotalname = "所有者权益合计";
        } else if (corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()) {// 企业会计制度
            zctotalname = "资产合计";
            fztotalname = "负债合计";
            qytotalname = "所有者权益（或股东权益）合计";
        } else if (corpschema == DzfUtil.RURALCOOPERATIVE.intValue()) {
            zctotalname = "资产总计";
            fztotalname = "负债和所有者权益总计";
        }
        for (ZcFzBVO bvo : zcfzbvos) {
            if (zctotalname.equals(bvo.getZc())) {
                zctotal = bvo.getQmye1() == null ? DZFDouble.ZERO_DBL : bvo.getQmye1();
            }
            if (fztotalname.equals(bvo.getFzhsyzqy())) {
                fztotal = bvo.getQmye2() == null ? DZFDouble.ZERO_DBL : bvo.getQmye2();
            }
            if (qytotalname.equals(bvo.getFzhsyzqy())) {
                qytotal = bvo.getQmye2() == null ? DZFDouble.ZERO_DBL : bvo.getQmye2();
            }
        }
        msgvo.setZcvalue(zctotal);
        msgvo.setFzvalue(fztotal);
        msgvo.setQyvalue(qytotal);
    }

    private String getPubParam(CorpVO cpvo) {
        return "source=gzbg&corpIds=" + cpvo.getPk_corp() + "&gsname=" + cpvo.getUnitname();
    }

    private String getNoBlanceMsg(FseJyeVO[] fvos, CorpVO corpVO, Map<String, YntCpaccountVO> mapc, String period) {
        String pk_corp = corpVO.getPk_corp();
        int index = 0;
        List<String> msglist = new ArrayList<String>();
        if (fvos == null || fvos.length == 0) {
            return "";
        }

        StringBuffer msg = new StringBuffer();

        //期初试算是否平衡
        SsphRes phres = zxkjPlatformService.qcyeSsph(pk_corp);

        if (phres != null && (phres.getYearres().equals("不平衡") || phres.getMonthres().equals("不平衡"))) {
            index++;
            String url = "gl/gl_qcset/gl_qcye.jsp?" + getPubParam(corpVO);
            String name = "科目期初";
            //msglist.add("期初试算不平衡，请修改科目期初！ <a style=\"display: block; height: 16px;float: right;\" href= \"javascript:void(0)\" onclick = linkUrl(\""+name+"\",\""+url+"\")>查看</a>");

            msglist.add("<div style=\"font-size: 12px;\">&emsp;&nbsp;" + index + "、期初试算不平衡，请修改科目期初！<a href=\"javascript:void(0)\" class=\"check\"   onclick=linkUrl(\"" + name + "\",\"" + url + "\")>查看</a></div>");
        }

        //当前损益科目是否有期初余额 ,当前损益科目是否有期末余额
        DZFBoolean qcsy = DZFBoolean.FALSE;
        DZFBoolean qmsy = DZFBoolean.FALSE;
        for (FseJyeVO fvo : fvos) {
            if (mapc.get(fvo.getKmbm()) == null) {
                continue;
            }
            if (mapc.get(fvo.getKmbm()).getAccountkind() == 5) {
                if (!qcsy.booleanValue() && (VoUtils.getDZFDouble(fvo.getLastmqcjf()).doubleValue() != 0 || VoUtils.getDZFDouble(fvo.getLastmqcdf()).doubleValue() != 0)) {
                    qcsy = DZFBoolean.TRUE;
                }
                if (!qmsy.booleanValue() && (VoUtils.getDZFDouble(fvo.getQmjf()).doubleValue() != 0 || VoUtils.getDZFDouble(fvo.getQmdf()).doubleValue() != 0)) {
                    qmsy = DZFBoolean.TRUE;
                }
            }
        }

        if (qcsy.booleanValue()) {
            index++;
            String url = "gl/gl_jzcl/gl_qmcl.jsp?" + getPubParam(corpVO) + "&rq=" + DateUtils.getPeriodStartDate(period);
            String name = "期末结转";
//			msglist.add("当期损益科目有期初余额！<a style=\"display: block; height: 16px;float: right;\" href= \"javascript:void(0)\"  onclick = linkUrl(\""+name+"\",\""+url+"\")>查看</a>");
            msglist.add("<div style=\"font-size: 12px;\">&emsp;&nbsp;" + index + "、当期损益科目有期初余额！<a href=\"javascript:void(0)\" class=\"check\"   onclick=linkUrl(\"" + name + "\",\"" + url + "\")>查看</a></div>");
        }

        if (qmsy.booleanValue()) {
            index++;
            String url = "gl/gl_jzcl/gl_qmcl.jsp?" + getPubParam(corpVO) + "&rq=" + DateUtils.getPeriodStartDate(period);
            String name = "期末结转";
            //msglist.add("当前损益科目余额未结转为零！<a style=\"display: block; height: 16px;float: right;\" href= \"javascript:void(0)\"  onclick = linkUrl(\""+name+"\",\""+url+"\")>查看</a>");
            msglist.add(" <div style=\"font-size: 12px;\">&emsp;&nbsp;" + index + "、当前损益科目余额未结转为零！  <a href=\"javascript:void(0)\" class=\"check\"   onclick=linkUrl(\"" + name + "\",\"" + url + "\")>查看</a></div>");
        }

        //是否和下级科目是否数据一致
        YntCpaccountVO tempvo = null;
        YntCpaccountVO tempchildvo = null;
        for (FseJyeVO fvo : fvos) {
            tempvo = mapc.get(fvo.getKmbm());
            String pfx = fvo.getFx();
            DZFDouble qcvalue = DZFDouble.ZERO_DBL;
            DZFDouble qmvalue = DZFDouble.ZERO_DBL;
            if (pfx.equals("借")) {
                qcvalue = SafeCompute.sub(fvo.getQcjf(), fvo.getQcdf());
                qmvalue = SafeCompute.sub(fvo.getQmjf(), fvo.getQmdf());
            } else {
                qcvalue = SafeCompute.sub(fvo.getQcdf(), fvo.getQcjf());
                qmvalue = SafeCompute.sub(fvo.getQmdf(), fvo.getQmjf());
            }
            if (tempvo != null && tempvo.getAccountlevel() == 1) {
                for (FseJyeVO fvo1 : fvos) {
                    tempchildvo = mapc.get(fvo1.getKmbm());
                    if (tempchildvo == null) {
                        continue;
                    }
                    String cfx = fvo1.getFx();
                    DZFDouble cqcvalue = DZFDouble.ZERO_DBL;
                    DZFDouble cqmvalue = DZFDouble.ZERO_DBL;
                    if (tempchildvo.getIsleaf().booleanValue() && tempchildvo.getAccountcode().startsWith(tempvo.getAccountcode())) {
                        if (cfx.equals("借")) {
                            cqcvalue = SafeCompute.sub(fvo1.getQcjf(), fvo1.getQcdf());
                            cqmvalue = SafeCompute.sub(fvo1.getQmjf(), fvo1.getQmdf());
                        } else {
                            cqcvalue = SafeCompute.sub(fvo1.getQcdf(), fvo1.getQcjf());
                            cqmvalue = SafeCompute.sub(fvo1.getQmdf(), fvo1.getQmjf());
                        }

                        if (cfx.equals(pfx)) {
                            qcvalue = qcvalue.sub(cqcvalue);
                            qmvalue = qmvalue.sub(cqmvalue);
                        } else {
                            qcvalue = qcvalue.add(cqcvalue);
                            qmvalue = qmvalue.add(cqmvalue);
                        }
                    }
                }

                if (qcvalue.doubleValue() != 0 && qmvalue.doubleValue() != 0) {
                    index++;
                    //msglist.add(fvo.getKmmc() +"科目数据与其下级科目汇总的数据不一致，请检查！");
                    msglist.add("<div style=\"font-size: 12px;\">&emsp;&nbsp;" + index + "、" + fvo.getKmmc() + "科目数据与其下级科目汇总的数据不一致，请检查！  </div>");
                    break;
                }
            }
        }

        if (msglist.size() > 0) {
            for (int i = 0; i < msglist.size(); i++) {
//				msg.append( "<li>" + (i+1)+"、"+msglist.get(i) +"</li>");
                msg.append(msglist.get(i));
            }
        }
        return msg.toString();
    }

    @Override
    public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) throws Exception {
        Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
        if (fvos != null && fvos.length > 0) {
            int len = fvos == null ? 0 : fvos.length;
            for (int i = 0; i < len; i++) {
                if(!StringUtil.isEmpty(fvos[i].getPk_km())
                        && fvos[i].getPk_km().length()>24 && map.containsKey(fvos[i].getKmbm())){//说明存在辅助核算
                    //处理存在辅助核算的
                    map.put(fvos[i].getPk_km(), fvos[i]);
                }else{
                    map.put(fvos[i].getKmbm(), fvos[i]);
                }
            }
        }

        String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
        Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);

        if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则 小企业会计准则
            return new XqyZcfzBReportService(zxkjPlatformService).getZCFZB2013VOs(map, hasyes, mapc,queryAccountRule);
        } else if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {//2007会计准则 企业会计准则
            return new QyZcfzBReportService(zxkjPlatformService).getZCFZB2007VOs(map, hasyes, mapc,queryAccountRule);
        } else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
            PopularRpForZcfzImpl popularimpl = new PopularRpForZcfzImpl(zxkjPlatformService);
            return popularimpl.getPopularZCFZBVOs(map, hasyes, mapc, pk_corp);
        } else if (corpschema == DzfUtil.CAUSESCHEMA.intValue()) {// 事业
            CauseRpForZcfzImpl causeimpl = new CauseRpForZcfzImpl();
            return causeimpl.getCauseZCFZBVOs(map, hasyes, mapc);
        } else if(corpschema == DzfUtil.VILLAGECOLLECTIVE.intValue()){//村集体
            VillageCollectiveForZcfzImpl  villageimpl = new VillageCollectiveForZcfzImpl();
            return villageimpl.getCauseZCFZBVOs(map, hasyes, mapc);
        }else if(corpschema ==  DzfUtil.COMPANYACCOUNTSYSTEM.intValue() ){//企业会计制度
            OtherSystemForZcfzImpl  companyimpl = new OtherSystemForZcfzImpl(rptSetService, zxkjPlatformService);
            return companyimpl.getCompanyZCFZBVOs(map, hasyes, mapc,"00000100000000Ig4yfE0005",pk_corp);
        }  else if(corpschema == DzfUtil.RURALCOOPERATIVE.intValue()){//农村合作社
            OtherSystemForZcfzImpl  companyimpl = new OtherSystemForZcfzImpl(rptSetService, zxkjPlatformService);
            return companyimpl.getCompanyZCFZBVOs(map, hasyes, mapc,"00000100000000Ig4yfE0006",pk_corp);
        }
        else {
            throw new RuntimeException("该制度暂不支持资产负债表,敬请期待!");
        }
    }

    @Override
    public List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate, String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs) throws Exception {
        List<ZcFzBVO[]> results = new ArrayList<ZcFzBVO[]>();

        if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
            hasyes = new String[] { "N", "N", "N", "N","N" };
        }

        DZFBoolean bhasjz = StringUtil.isEmpty(ishasjz) ? null : new DZFBoolean(ishasjz);

        Object[] objs = fsYeService.getEveryPeriodFsJyeVOs(begdate, enddate, pk_corp, qryobjs, "zcfz", bhasjz);

        Map<String, YntCpaccountVO> mapc = null;
        YntCpaccountVO[] cvos1 = zxkjPlatformService.queryByPk(pk_corp);
        mapc = ReportUtil.conVertMap(cvos1);

        Map<String, List<FseJyeVO>> fsmap = (Map<String, List<FseJyeVO>>) objs[0];

        List<String> periods = ReportUtil.getPeriods(begdate, enddate);

        List<FseJyeVO> periodfs = null;

        ZcFzBVO[] zcfzbvos = null;

        for (String period : periods) {
            periodfs = fsmap.get(period);

            if (periodfs == null) {
                continue;
            }

            // 从年初开始取
            FseJyeVO[] restemp = fsYeService.getBetweenPeriodFs(
                    DateUtils.getPeriodStartDate(period.substring(0, 4) + "-01"), new DZFDate(period + "-01"), fsmap);

            zcfzbvos = getZcfzVOs(pk_corp, hasyes, mapc, restemp);

            if (zcfzbvos != null && zcfzbvos.length > 0) {
                for (ZcFzBVO bvo : zcfzbvos) {
                    bvo.setPeriod(period);
                }
            }

            results.add(zcfzbvos);
        }
        return results;
    }
}
