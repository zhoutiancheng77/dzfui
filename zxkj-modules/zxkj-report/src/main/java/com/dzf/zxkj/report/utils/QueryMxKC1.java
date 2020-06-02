package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 数量金额明细查询
 * 启用进销存1
 */
@SuppressWarnings("all")
public class QueryMxKC1 {
    private SingleObjectBO singleObjectBO;

    private Map<String, String> precisionMap;

    private int precisionPrice;

    private IZxkjPlatformService zxkjPlatformService;

    public QueryMxKC1(SingleObjectBO singleObjectBO, IZxkjPlatformService zxkjPlatformService,
                      Map<String, String> precisionMap) {
        this.singleObjectBO = singleObjectBO;
        this.zxkjPlatformService = zxkjPlatformService;

        if (precisionMap != null) {
            String value = precisionMap.get(IParameterConstants.DZF010);
            precisionPrice = Integer.parseInt(value);
        } else {
            precisionPrice = 4;
        }
    }

    public QueryMxKC1(SingleObjectBO singleObjectBO, IZxkjPlatformService zxkjPlatformService) {
        this.singleObjectBO = singleObjectBO;
        this.zxkjPlatformService = zxkjPlatformService;
    }


    //主查询
    public List<NumMnyDetailVO> querymx(String startDate, String enddate, String pk_corp, String user_id, QueryParamVO paranVo, String pk_bz, boolean bool, String xsfzhs, DZFDate begdate) throws DZFWarpException {
        //取当前公司建账日期
        //DZFDate jzdate = queryJzDate(pk_corp);
        DZFDate jzdate = begdate;
        HashMap<String, YntCpaccountVO> cpamap = new HashMap<String, YntCpaccountVO>();
        HashMap<String, YntCpaccountVO> cpamap2 = new HashMap<String, YntCpaccountVO>();
        List<YntCpaccountVO> cpalist = Arrays.asList(zxkjPlatformService.queryByPk(pk_corp));
        HashMap<String, YntCpaccountVO> fzhscpamap = new HashMap<String, YntCpaccountVO>();
        for (YntCpaccountVO cpavo : cpalist) {
            if ("1403".equals(cpavo.getAccountcode()) || "1405".equals(cpavo.getAccountcode()))
                cpamap.put(cpavo.getPk_corp_account(), cpavo);
            cpamap2.put(cpavo.getPk_corp_account(), cpavo);
            if (cpavo.getIsnum().booleanValue()) {
//			if(cpavo.getIsnum().booleanValue()){//放开辅助核算，适配启用库存老模式
                fzhscpamap.put(cpavo.getPk_corp_account(), cpavo);
            }
        }
        List<NumMnyDetailVO> list1 = queryDetailVOsNoIC(jzdate.toString(), enddate, pk_corp, paranVo, pk_bz);
        Map<String, List<NumMnyDetailVO>> map1 = hashlizeObject(list1);
        List<String> keylist = getSortKey(list1);
        //查询期初
        Map<String, QcYeVO> map2 = getIcQcMx(pk_corp, paranVo, pk_bz);

        List<NumMnyDetailVO> list = calcDetailVOs(jzdate, keylist, pk_corp, startDate, enddate, map1, map2, cpamap, cpamap2);

        if (bool) {//在期末结转中用到
            return list;
        }

        List<NumMnyDetailVO> fzhslist = new ArrayList<NumMnyDetailVO>();
        list = getcqList(list, paranVo, pk_corp, user_id, cpamap2);
        if ("Y".equals(xsfzhs)) {
            Map<String, String> kmbmmc = new HashMap<String, String>();
//			Map<String, AuxiliaryAccountBVO> auaccountMap = AuAccountCache.getInstance().getMap(pk_corp);
            Map<String, String> jldwmap = new HashMap<String, String>();
            Map<String, InventoryVO> inventoryMap = queryInventory(pk_corp);
            List<String> fzkmbms = new ArrayList<String>();
            Map<String, FzhsqcVO> mapFzhsqc = getFzqcMx(pk_corp, paranVo, pk_bz);
            List<FzhsqcVO> fzqcs = new ArrayList<FzhsqcVO>(mapFzhsqc.values());
            for (FzhsqcVO vo : fzqcs) {
                String s[] = getCombStr(null, vo, inventoryMap, cpamap);
                if (!fzkmbms.contains(s[0])) {
                    fzkmbms.add(s[0]);
                    kmbmmc.put(s[0], s[1]);

                    jldwmap.put(s[0], s[2]);
                }
            }
            List<String> fzhskms = new ArrayList<String>(fzhscpamap.keySet());
            for (NumMnyDetailVO vo : list1) {
                if (fzhskms.contains(vo.getPk_subject())) {
                    NumMnyDetailVO vo1 = (NumMnyDetailVO) vo.clone();
                    String s[] = getCombStr(vo1, null, inventoryMap, cpamap);
                    if (!fzkmbms.contains(s[0])) {
                        fzkmbms.add(s[0]);
                        kmbmmc.put(s[0], s[1]);
                        jldwmap.put(s[0], s[2]);
                    }
                    vo1.setKmbm(s[0]);
                    vo1.setKmmc(s[1]);
                    if (!StringUtil.isEmpty(s[2])) {
                        vo1.setJldw(s[2]);
                    }

                    fzhslist.add(vo1);
                }
            }
            List<String> keys = new ArrayList<String>();
            String startkey = startDate.substring(0, 7);
            keys.add(startkey);
            while (getNextKey(startkey).compareTo(enddate.substring(0, 7)) <= 0) {
                keys.add(getNextKey(startkey));
                startkey = getNextKey(startkey);
            }
            Map<String, NumMnyDetailVO> qcyemap = new HashMap<String, NumMnyDetailVO>();
            for (NumMnyDetailVO vo : list) {
                if ("期初余额".equals(vo.getZy()) && ReportUtil.bSysZy(vo)) {
                    qcyemap.put(vo.getKmbm(), vo);
                }
            }
            for (String kmbm : fzkmbms) {
                int index = kmbm.indexOf("_");
                if (index == -1) {
                    index = kmbm.length();
                }
                NumMnyDetailVO cvo = qcyemap.get(kmbm.substring(0, index));
                if (cvo != null) {
                    cvo = buildfzhsqc(cvo, fzhslist, mapFzhsqc, keys, kmbm, jzdate.toString(), kmbmmc, jldwmap);
                    list.add(cvo);
                    for (String key : keys) {
                        buildOthers(list, cvo, fzhslist, key, kmbm, jzdate.toString(), mapFzhsqc, kmbmmc, jldwmap);
                    }
                }
            }
        }
        list = deleteNull(list);

        Collections.sort(list, new Comparator<NumMnyDetailVO>() {
            public int compare(NumMnyDetailVO arg0, NumMnyDetailVO arg1) {
                if (arg0.getKmbm().compareTo(arg1.getKmbm()) == 0) {
                    if (arg0.getOpdate().compareTo(arg1.getOpdate()) == 0) {
                        if (arg0.getPzh() != null && arg1.getPzh() != null
                                && !"".equals(arg0.getPzh()) && !"".equals(arg1.getPzh())) {
                            return arg0.getPzh().compareTo(arg1.getPzh());
                        } else {
                            return 0;
                        }
                    }
                    return arg0.getOpdate().compareTo(arg1.getOpdate());
                }
                return arg0.getKmbm().compareTo(arg1.getKmbm());
            }
        });
        calcQM(list, cpamap);
        GxhszVO gvo = zxkjPlatformService.queryGxhszVOByPkCorp(pk_corp);
        Integer subjectShow = gvo.getSubjectShow();
        if (list != null && !list.isEmpty()) {
            NumMnyDetailVO[] vos = list.toArray(new NumMnyDetailVO[0]);
            for (NumMnyDetailVO vo : vos) {

                if (subjectShow.intValue() == 0) {//显示本级
                    String[] tempkms = vo.getKmmc().split("/");
                    if (tempkms.length == 1) {
                        vo.setKmmc(vo.getKmmc());
                    }
                    if (tempkms.length > 1) {
                        vo.setKmmc(tempkms[tempkms.length - 1]);
                    }
                } else if (subjectShow.intValue() == 1) {//显示一级+本级
                    String[] tempkms = vo.getKmmc().split("/");
                    if (tempkms.length > 1) {
                        vo.setKmmc(tempkms[0] + "_" + tempkms[tempkms.length - 1]);
                    }
                } else {//逐级显示
                    vo.setKmmc(vo.getKmmc().replaceAll("/", "_"));
                }
            }
        }
        return list;
    }

    private void buildOthers(List<NumMnyDetailVO> list, NumMnyDetailVO cvo, List<NumMnyDetailVO> fzhslist,
                             String key, String kmbm, String jzdate, Map<String, FzhsqcVO> mapFzhsqc, Map<String, String> kmbmmc,
                             Map<String, String> jldwmap) {
        List<NumMnyDetailVO> list1 = new ArrayList<NumMnyDetailVO>();
        for (NumMnyDetailVO vo : fzhslist) {
            if (kmbm.equals(vo.getKmbm())) {
                if (vo.getQj().compareTo(key) == 0) {
                    NumMnyDetailVO dvo = (NumMnyDetailVO) vo.clone();
                    list1.add(dvo);
                }
            }
        }
        //本期合计
        NumMnyDetailVO dvo = (NumMnyDetailVO) cvo.clone();
        dvo.setQj(key);
        dvo.setKmmc(kmbmmc.get(kmbm));
        dvo.setJldw(jldwmap.get(kmbm));
        dvo.setZy("本期合计");
        dvo.setBsyszy(DZFBoolean.TRUE);
        int year = Integer.parseInt(key.substring(0, 4));
        int month = Integer.parseInt(key.substring(5, 7));
        dvo.setOpdate(key + "-" + getMonthLastDay(year, month));
        DZFDouble nmny = DZFDouble.ZERO_DBL;
        DZFDouble nnum = DZFDouble.ZERO_DBL;
        DZFDouble ndmny = DZFDouble.ZERO_DBL;
        DZFDouble ndnum = DZFDouble.ZERO_DBL;
        for (NumMnyDetailVO vo : list1) {
            nmny = SafeCompute.add(nmny, VoUtils.getDZFDouble(vo.getNmny()));
            nnum = SafeCompute.add(nnum, VoUtils.getDZFDouble(vo.getNnum()));
            ndmny = SafeCompute.add(ndmny, VoUtils.getDZFDouble(vo.getNdmny()));
            ndnum = SafeCompute.add(ndnum, VoUtils.getDZFDouble(vo.getNdnum()));
        }
        dvo.setNnum(nnum);
        dvo.setNdnum(ndnum);
        dvo.setNmny(nmny);
        dvo.setNdmny(ndmny);
        if (nmny != null && nnum != null && nnum.doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(nmny, nnum);
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            dvo.setNprice(price);
        }
        if (ndmny != null && ndnum != null && ndnum.doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(ndmny, ndnum);
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            dvo.setNdprice(price);
        }
        list1.add(dvo);
        //本年累计
        NumMnyDetailVO evo = (NumMnyDetailVO) cvo.clone();
        evo.setQj(key);
        evo.setZy("本年累计");
        evo.setBsyszy(DZFBoolean.TRUE);
        evo.setKmmc(kmbmmc.get(kmbm));
        evo.setJldw(jldwmap.get(kmbm));
        evo.setOpdate(key + "-" + getMonthLastDay(year, month));
        DZFDouble nmny1 = DZFDouble.ZERO_DBL;
        DZFDouble nnum1 = DZFDouble.ZERO_DBL;
        DZFDouble ndmny1 = DZFDouble.ZERO_DBL;
        DZFDouble ndnum1 = DZFDouble.ZERO_DBL;
        if (key.substring(0, 4).equals(jzdate.substring(0, 4))) {
            FzhsqcVO qcvo = mapFzhsqc.get(kmbm);
            if (qcvo != null) {
                nnum1 = VoUtils.getDZFDouble(qcvo.getBnfsnum());
                ndnum1 = VoUtils.getDZFDouble(qcvo.getBndffsnum());
                nmny1 = VoUtils.getDZFDouble(qcvo.getYearjffse());
                ndmny1 = VoUtils.getDZFDouble(qcvo.getYeardffse());
            }
        }
        for (NumMnyDetailVO vo : fzhslist) {
            if (kmbm.equals(vo.getKmbm())) {
                if (vo.getQj().compareTo(key) <= 0 && key.substring(0, 4).equals(vo.getQj().substring(0, 4))) {
                    nnum1 = SafeCompute.add(nnum1, VoUtils.getDZFDouble(vo.getNnum()));
                    ndnum1 = SafeCompute.add(ndnum1, VoUtils.getDZFDouble(vo.getNdnum()));
                    nmny1 = SafeCompute.add(nmny1, VoUtils.getDZFDouble(vo.getNmny()));
                    ndmny1 = SafeCompute.add(ndmny1, VoUtils.getDZFDouble(vo.getNdmny()));
                }
            }
        }
        if (nmny1 != null && nnum1 != null && nnum1.doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(nmny1, nnum1);
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            evo.setNprice(price);
        }
        if (ndmny1 != null && ndnum1 != null && ndnum1.doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(ndmny1, ndnum1);
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            evo.setNdprice(price);
        }
        evo.setNnum(nnum1);
        evo.setNdnum(ndnum1);
        evo.setNmny(nmny1);
        evo.setNdmny(ndmny1);
        list1.add(evo);

        list.addAll(list1);
    }

    private NumMnyDetailVO buildfzhsqc(NumMnyDetailVO dvo, List<NumMnyDetailVO> fzhslist,
                                       Map<String, FzhsqcVO> mapFzhsqc, List<String> keys, String kmbm, String jzdate, Map<String, String> kmbmmc,
                                       Map<String, String> jldwmap) {
        NumMnyDetailVO cvo = (NumMnyDetailVO) dvo.clone();
        cvo.setPzh(null);
        cvo.setQj(keys.get(0));
        cvo.setOpdate(keys.get(0) + "-01");
        //cvo.setOpdate(dvo.getQj()+"-01");
        cvo.setKmbm(kmbm);
        cvo.setKmmc(kmbmmc.get(kmbm));
        cvo.setJldw(jldwmap.get(kmbm));
        cvo.setZy("期初余额");
        cvo.setBsyszy(DZFBoolean.TRUE);
        cvo.setNnum(null);
        cvo.setNprice(null);
        cvo.setNmny(null);
        cvo.setNdnum(null);
        cvo.setNdprice(null);
        cvo.setNdmny(null);
        cvo.setNyprice(null);
        cvo.setNynum(null);
        cvo.setNymny(null);
        FzhsqcVO qcvo = mapFzhsqc.get(kmbm);
        DZFDouble mny = DZFDouble.ZERO_DBL;
        DZFDouble num = DZFDouble.ZERO_DBL;
        if (qcvo != null) {
            mny = qcvo.getThismonthqc();
            num = qcvo.getMonthqmnum();
        }
        for (NumMnyDetailVO vo : fzhslist) {
            if (kmbm.equals(vo.getKmbm())) {
                if (vo.getQj().compareTo(keys.get(0)) < 0) {
//					if(keys.get(0).substring(0,4).equals(jzdate.substring(0,4))){

                    DZFDouble num1 = vo.getNnumber();
                    DZFDouble jf = vo.getJfmny();
                    DZFDouble df = vo.getDfmny();
                    if (jf != null && jf.doubleValue() > 0) {
                        num = SafeCompute.add(num, num1);
                        mny = SafeCompute.add(mny, jf);
                    } else if (df != null && df.doubleValue() < 0) {
                        num1 = SafeCompute.multiply(num1, new DZFDouble(-1));
                        num = SafeCompute.add(num, num1);
                        mny = SafeCompute.add(mny, df.multiply(-1));
                    } else if (jf != null && jf.doubleValue() < 0) {
                        num1 = SafeCompute.multiply(num1, new DZFDouble(-1));
                        num = SafeCompute.sub(num, num1);
                        mny = SafeCompute.sub(mny, jf.multiply(-1));
                    } else if (df != null && df.doubleValue() > 0) {
                        num = SafeCompute.sub(num, num1);
                        mny = SafeCompute.sub(mny, df);
                    }
//					}else{
//						
//						
//					}
                }
            }
        }
        if (mny != null && num != null && num.doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(mny, num);
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            cvo.setNyprice(price);
        }
        cvo.setNynum(num);
        cvo.setNymny(mny);
        return cvo;
    }

    private String[] getCombStr(NumMnyDetailVO vo, FzhsqcVO vo1,
                                Map<String, InventoryVO> inventoryMap,
                                HashMap<String, YntCpaccountVO> cpamap) {
        String[] str = new String[3];
        StringBuffer code = null;
        StringBuffer name = null;
        if (vo != null) {
            code = new StringBuffer(vo.getKmbm());
            name = new StringBuffer(vo.getKmmc());
//			if (!StringUtil.isEmpty(vo.getFzhsx6())) {
            InventoryVO inventory = inventoryMap.get(vo.getFzhsx6());
            if (inventory != null) {
                code.append("_");
                code.append(inventory.getCode());
                name.append("_");
                name.append(inventory.getName());

                if (!StringUtil.isEmpty(inventory.getInvspec())) {
                    name.append("(").append(inventory.getInvspec()).append(")");
                }

                if (!StringUtil.isEmpty(inventory.getMeasurename()))
                    str[2] = inventory.getMeasurename();
            }
            if (StringUtil.isEmpty(str[2]) && !StringUtil.isEmpty(vo.getPk_subject())) {
                YntCpaccountVO tcpavo = cpamap.get(vo.getPk_subject());
                if (tcpavo != null) {
                    str[2] = tcpavo.getMeasurename();
                }
            }
//			}
        }
        if (vo1 != null) {
            //  期初开账新增  点确定后只保存科目
            if (vo1.getVcode().indexOf("_") > -1) {
                code = new StringBuffer(vo1.getVcode().substring(0, vo1.getVcode().indexOf("_")));
            } else {
                code = new StringBuffer(vo1.getVcode());
            }
            if (vo1.getVname().indexOf("_") > -1) {
                name = new StringBuffer(vo1.getVname().substring(0, vo1.getVname().indexOf("_")));
            } else {
                name = new StringBuffer(vo1.getVname());
            }
//			if (!StringUtil.isEmpty(vo1.getFzhsx6())) {
            InventoryVO inventory = inventoryMap.get(vo1.getFzhsx6());
            if (inventory != null) {
                code.append("_");
                code.append(inventory.getCode());
                name.append("_");
                name.append(inventory.getName());

                if (!StringUtil.isEmpty(inventory.getInvspec())) {
                    name.append("(").append(inventory.getInvspec()).append(")");
                }

                if (!StringUtil.isEmpty(inventory.getMeasurename()))
                    str[2] = inventory.getMeasurename();
            }

            if (StringUtil.isEmpty(str[2]) && !StringUtil.isEmpty(vo1.getPk_accsubj())) {
                YntCpaccountVO tcpavo = cpamap.get(vo1.getPk_accsubj());
                if (tcpavo != null) {
                    str[2] = tcpavo.getMeasurename();
                }
            }
//			}
        }
        str[0] = code.toString();
        str[1] = name.toString();
        return str;
    }

    private List<NumMnyDetailVO> deleteNull(List<NumMnyDetailVO> list) {
        Map<String, List<NumMnyDetailVO>> map = new HashMap<String, List<NumMnyDetailVO>>();
        String key = null;
        for (NumMnyDetailVO vo : list) {
            key = vo.getKmbm();
            if (map.containsKey(key)) {
                map.get(key).add(vo);
            } else {
                List<NumMnyDetailVO> zlist = new ArrayList<NumMnyDetailVO>();
                zlist.add(vo);
                map.put(key, zlist);
            }
        }
        List<String> lis = new ArrayList<String>(map.keySet());
        List<NumMnyDetailVO> delNullNumMnyList = new ArrayList<NumMnyDetailVO>();//删除空行
        for (String str : lis) {
            List<NumMnyDetailVO> zlist = map.get(str);
            DZFDouble d = null;
            for (NumMnyDetailVO vo : zlist) {
                d = DZFDouble.ZERO_DBL;
                d = SafeCompute.add(d, getDou(vo.getNnum()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getNmny()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getNdnum()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getNdmny()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getNynum()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getNymny()));
                if (d.doubleValue() > 0) {
                    continue;
                }
                d = SafeCompute.add(d, getDou(vo.getDfmny()));

                d = SafeCompute.add(d, getDou(vo.getJfmny()));

                d = SafeCompute.add(d, getDou(vo.getNnumber()));

                if ("期初余额".equals(vo.getZy()) && ReportUtil.bSysZy(vo)) {
                    continue;
                }
                if (d.doubleValue() > 0) {
                    continue;
                } else {
                    delNullNumMnyList.add(vo);
                }
            }
            if (d.doubleValue() == 0) {
                map.remove(str);
            }
            if (delNullNumMnyList.size() > 0) {
                for (NumMnyDetailVO vo : delNullNumMnyList) {
                    zlist.remove(vo);
                }
            }
        }
        list.clear();
        for (String str : lis) {
            List<NumMnyDetailVO> zlist = map.get(str);
            if (zlist != null && zlist.size() > 0) {
                list.addAll(zlist);
            }

        }
        return list;
    }

    private DZFDouble getDou(DZFDouble d) {
        if (d == null) {
            return DZFDouble.ZERO_DBL;
        }
        if (d.doubleValue() < 0) {
            return d.multiply(-1);
        } else {
            return d;
        }
    }

    private List<String> getKmParent(Map<String, YntCpaccountVO> mp,
                                     String kmpk) {
        YntCpaccountVO vo1 = mp.get(kmpk);
        if (vo1 == null) {
            return new ArrayList<String>();
        }
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

    private List<NumMnyDetailVO> getcqList(List<NumMnyDetailVO> list, QueryParamVO paramvo, String pk_corp, String user_id, Map<String, YntCpaccountVO> map) throws DZFWarpException {
//		String accrule = cpaccountService.queryAccountRule(pk_corp);
        Map<String, Integer> map1 = new HashMap<String, Integer>();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            YntCpaccountVO val = (YntCpaccountVO) entry.getValue();
            String acode = val.getAccountcode();
            Integer alevel = val.getAccountlevel();
            map1.put(acode, alevel);
        }
        Integer cqj = paramvo.getCjq();
        Integer cqz = paramvo.getCjz();
        if (cqj > cqz) {
            throw new BusinessException("开始级次不能大于结束级次！");
        }
        List<NumMnyDetailVO> zlist = new ArrayList<NumMnyDetailVO>();
        for (int i = 0; i < list.size(); i++) {
            NumMnyDetailVO vo = list.get(i);
            String code = vo.getKmbm();
            Iterator iter1 = map1.entrySet().iterator();
            while (iter1.hasNext()) {
                Map.Entry entry1 = (Map.Entry) iter1.next();
                String key = (String) entry1.getKey();
                if (code.equals(key)) {
                    Integer val = (Integer) entry1.getValue();
                    if (val >= cqj && val <= cqz) {
//						DZFDouble price = SafeCompute.div(vo.getNymny(), vo.getNynum());
//						price = price.setScale(4, DZFDouble.ROUND_HALF_UP);
//						vo.setNyprice(price);
                        zlist.add(vo);
                    }
                }
            }
        }

        return zlist;

    }

    private DZFDate queryJzDate(String pk_corp) throws BusinessException {
        CorpVO vo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        return vo.getBegindate();
    }

    /**
     * 查询明细数据 不启用库存
     */
    public List<NumMnyDetailVO> queryDetailVOsNoIC(String startDate, String enddate, String pk_corp, QueryParamVO paranVo, String pk_bz) throws DZFWarpException {
        String startqj = calcqj(startDate);
        String endqj = calcqj(enddate);
        StringBuffer sf = new StringBuffer();
        SQLParameter pa = new SQLParameter();
        pa.addParam(pk_corp);
        pa.addParam(startqj);
        pa.addParam(endqj);
        sf.append(" select tb.pk_accsubj pk_subject, ");
        sf.append(" tb.kmmchie  kmmc, ");
        sf.append(" tb.vcode  kmbm, ");
        sf.append(" th.period qj, ");
        sf.append(" th.doperatedate opdate, ");
        sf.append(" th.pzh, ");
        sf.append(" th.pk_tzpz_h pzhhid, ");
        sf.append(" tb.zy, ");
        sf.append(" tb.nnumber,");
        sf.append(" tb.jfmny jfmny, ");
        sf.append(" tb.dfmny dfmny, ");
        sf.append(" tb.nprice as npzprice, ");
        sf.append(" ct.measurename jldw ,");
        sf.append(" ct.accountlevel accountlevel, ");

        sf.append(" tb.fzhsx1,tb.fzhsx2,tb.fzhsx3,tb.fzhsx4,tb.fzhsx5, ");
        sf.append(" case when tb.fzhsx6 is null then tb.pk_inventory end fzhsx6, ");
        sf.append(" tb.fzhsx7,tb.fzhsx8,tb.fzhsx9,tb.fzhsx10 ");

        sf.append(" from ynt_tzpz_b tb ");
        sf.append(" join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h ");
        sf.append(" join ynt_cpaccount ct on tb.pk_accsubj = ct.pk_corp_account ");
        sf.append(" where th.pk_corp = ? ");
        sf.append(" and nvl(th.dr, 0) = 0 and nvl(tb.dr, 0) = 0 and nvl(ct.dr,0)=0 ");
        sf.append(" and th.period >= ? and th.period <= ?  and (nvl(ct.isnum,'N') = 'Y'  or tb.nnumber > 0 or tb.nnumber < 0) ");
        if (paranVo.getKms_first() != null && !"".equals(paranVo.getKms_first())) {
            pa.addParam(paranVo.getKms_first());
            sf.append(" and tb.vcode >= ? ");
        }//QueryParamVO paranVo
        if (paranVo.getKms_last() != null && !"".equals(paranVo.getKms_last())) {
            pa.addParam(paranVo.getKms_last());
            sf.append(" and tb.vcode < =? ");
            sf.append(" and tb.vcode in('1403','1405') ");
        }
        if (pk_bz != null && pk_bz.length() > 0) {
            sf.append(" and tb.pk_currency = ? ");
            pa.addParam(pk_bz);
        }
        if (paranVo.getIshasjz() != null) {
            if (paranVo.getIshasjz().toString().equals("N")) {//包含未记账
//				sf.append(" and nvl(th.ishasjz,'N')  ");
            }
            if (paranVo.getIshasjz().toString().equals("Y")) {//只含已记账
                sf.append(" and nvl(th.ishasjz,'N') = 'Y'  ");
            }
        }
//        sf.append(" order by tb.vcode, pk_accsubj,opdate,pzh ");

        List<NumMnyDetailVO> list = (List<NumMnyDetailVO>) singleObjectBO.executeQuery(sf.toString(), pa, new BeanListProcessor(NumMnyDetailVO.class));
        if (list != null && list.size() > 0) {
            VOUtil.ascSort(list, new String[]{"kmbm", "pk_subject", "opdate", "pzh"});
            for (int i = 0; i < list.size(); i++) {
                NumMnyDetailVO v = list.get(i);
                if (v.getJfmny() != null && v.getJfmny().doubleValue() > 0) {
                    v.setNnum(v.getNnumber());
                    v.setNmny(v.getJfmny());
                    v.setNprice(v.getNpzprice());
                } else if (v.getDfmny() != null && v.getDfmny().doubleValue() < 0) {
                    v.setNdnum(v.getNnumber());
                    v.setNdmny(v.getDfmny());
                    v.setNdprice(v.getNpzprice());
                } else if (v.getDfmny() != null && v.getDfmny().doubleValue() > 0) {//出库
                    v.setNdnum(v.getNnumber());
                    v.setNdmny(v.getDfmny());
                    v.setNdprice(v.getNpzprice());
                } else if (v.getJfmny() != null && v.getJfmny().doubleValue() < 0) {
                    v.setNnum(v.getNnumber());
                    v.setNmny(v.getJfmny());
                    v.setNprice(v.getNpzprice());
                }
            }
        }
        return list;
    }

    public List<NumMnyDetailVO> queryFzhsDetailVOs(String startDate, String enddate, String pk_corp, QueryParamVO paranVo, String pk_bz) throws DZFWarpException {
        String startqj = calcqj(startDate);
        String endqj = calcqj(enddate);
        StringBuffer sf = new StringBuffer();
        SQLParameter pa = new SQLParameter();
        pa.addParam(pk_corp);
        pa.addParam(startqj);
        pa.addParam(endqj);
        sf.append(" select tb.pk_accsubj pk_subject, ");
        sf.append(" tb.kmmchie  kmmc, ");
        sf.append(" tb.vcode  kmbm, ");
        sf.append(" th.period qj, ");
        sf.append(" th.doperatedate opdate, ");
        sf.append(" th.pzh, ");
        sf.append(" th.pk_tzpz_h pzhhid, ");
        sf.append(" tb.zy, ");
        sf.append(" tb.nnumber,");
        sf.append(" tb.jfmny jfmny, ");
        sf.append(" tb.dfmny dfmny, ");
        sf.append(" ct.measurename jldw ,");
        sf.append(" ct.accountlevel accountlevel, ");
        sf.append(" tb.fzhsx1,tb.fzhsx2,tb.fzhsx3,tb.fzhsx4,tb.fzhsx5,tb.fzhsx6,tb.fzhsx7,tb.fzhsx8,tb.fzhsx9,tb.fzhsx10 ");
        sf.append(" from ynt_tzpz_b tb ");
        sf.append(" join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h ");
        sf.append(" join ynt_cpaccount ct on tb.pk_accsubj = ct.pk_corp_account ");
        sf.append(" where th.pk_corp = ? ");
        sf.append(" and nvl(th.dr, 0) = 0 and nvl(tb.dr, 0) = 0 and nvl(ct.dr,0)=0 ");
        sf.append(" and th.period >= ? and th.period <= ?  and (nvl(ct.isnum,'N') = 'Y'  or tb.nnumber > 0) ");
        if (paranVo.getKms_first() != null && !"".equals(paranVo.getKms_first())) {
            pa.addParam(paranVo.getKms_first());
            sf.append(" and tb.vcode >= ? ");
        }//QueryParamVO paranVo
        if (paranVo.getKms_last() != null && !"".equals(paranVo.getKms_last())) {
            pa.addParam(paranVo.getKms_last());
            sf.append(" and tb.vcode < =? ");
        }
        if (pk_bz != null && pk_bz.length() > 0) {
            sf.append(" and tb.pk_currency = ? ");
            pa.addParam(pk_bz);
        }
        if (paranVo.getIshasjz() != null) {
            if (paranVo.getIshasjz().toString().equals("N")) {//包含未记账
//				sf.append(" and nvl(th.ishasjz,'N')  ");
            }
            if (paranVo.getIshasjz().toString().equals("Y")) {//只含已记账
                sf.append(" and nvl(th.ishasjz,'N') = 'Y'  ");
            }
        }
        sf.append(" order by tb.vcode, pk_accsubj,opdate,pzh ");
        //
        List<NumMnyDetailVO> list = (List<NumMnyDetailVO>) singleObjectBO.executeQuery(sf.toString(), pa, new BeanListProcessor(NumMnyDetailVO.class));
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                NumMnyDetailVO v = list.get(i);
                if (v.getJfmny() != null && v.getJfmny().doubleValue() > 0) {
                    v.setNnum(v.getNnumber());
                    v.setNmny(v.getJfmny());
                } else if (v.getDfmny() != null && v.getDfmny().doubleValue() < 0) {
//							v.setNdnum(v.getNnumber().multiply(-1));
                    v.setNdnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
                    v.setNdmny(v.getDfmny());
                } else if (v.getDfmny() != null && v.getDfmny().doubleValue() > 0) {//出库
                    v.setNdnum(v.getNnumber());
                    v.setNdmny(v.getDfmny());
                } else if (v.getJfmny() != null && v.getJfmny().doubleValue() < 0) {
//							v.setNnum(v.getNnumber().multiply(-1));
                    v.setNnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
                    v.setNmny(v.getJfmny());
                }
            }
        }
        return list;
    }

    //查询年期初数据
    public Map<String, IcbalanceVO> getQcMx(String pk_corp, String pk_invtory, String startDate) throws DZFWarpException {
        Map<String, IcbalanceVO> map = zxkjPlatformService.queryLastBanlanceVOs_byMap1(startDate, pk_corp, pk_invtory, false);
        return map;
    }

    //查询年期初数据
    public Map<String, QcYeVO> getIcQcMx(String pk_corp, QueryParamVO paranVo, String pk_bz) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select qc.*, it.pk_subject from YNT_ICBALANCE qc ");
        sf.append(" join YNT_INVENTORY it ");
        sf.append(" on it.PK_INVENTORY = qc.PK_INVENTORY ");
        sf.append(" join ynt_cpaccount ct ");
        sf.append(" on it.pk_subject = ct.pk_corp_account ");
        sf.append(" where  qc.pk_corp = ? and nvl(qc.dr,0) = 0 and  (nvl(ct.isnum,'N') = 'Y') ");
        if (paranVo.getKms_first() != null && paranVo.getKms_first().length() > 0) {
            sf.append(" and ct.accountcode >= ? ");
            sp.addParam(paranVo.getKms_first());
        }
        if (paranVo.getKms_last() != null && paranVo.getKms_last().length() > 0) {
            sf.append(" and ct.accountcode <= ? ");
            sp.addParam(paranVo.getKms_last());
        }
        if (pk_bz != null && pk_bz.length() > 0) {
            sf.append(" and qc.pk_currency = ?");
            sp.addParam(pk_bz);
        }
        //这个考虑的时候，没有考虑外币情况
//		QcYeVO[] vos = (QcYeVO[])singleObjectBO.queryByCondition(QcYeVO.class, sf.toString(), sp);
        List<IcbalanceVO> list = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(IcbalanceVO.class));

        if (list == null || list.size() == 0) {
            return new HashMap<String, QcYeVO>();

        }
        List<QcYeVO> ylist = new ArrayList<QcYeVO>();
        QcYeVO yevo = null;
        for (IcbalanceVO vo : list) {
            yevo = new QcYeVO();
//			yevo.setFzhsx6(vo.getPk_inventory());
            yevo.setPk_accsubj(vo.getPk_subject());
            yevo.setThismonthqc(vo.getNcost());
            yevo.setMonthqmnum(vo.getNnum());
            yevo.setVyear(Integer.parseInt(vo.getDbilldate().substring(0, 4)));
            ylist.add(yevo);
        }
        QcYeVO[] vos = (QcYeVO[]) ylist.toArray(new QcYeVO[list.size()]);
        Map<String, QcYeVO> map3 = hashlizeObject(vos);
        return map3;
    }

    //查询年期初数据
    public Map<String, FzhsqcVO> getFzqcMx(String pk_corp, QueryParamVO paranVo, String pk_bz) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select qc.*, it.pk_subject,  it.code inventorycode, ct.accountcode pk_subjectname,it.name inventoryname ,  ct.accountname pk_subject from YNT_ICBALANCE qc ");
        sf.append(" join YNT_INVENTORY it ");
        sf.append(" on it.PK_INVENTORY = qc.PK_INVENTORY ");
        sf.append(" join ynt_cpaccount ct ");
        sf.append(" on it.pk_subject = ct.pk_corp_account ");
        sf.append(" where  qc.pk_corp = ? and nvl(qc.dr,0) = 0 and  (nvl(ct.isnum,'N') = 'Y') ");
        if (paranVo.getKms_first() != null && paranVo.getKms_first().length() > 0) {
            sf.append(" and ct.accountcode >= ? ");
            sp.addParam(paranVo.getKms_first());
        }
        if (paranVo.getKms_last() != null && paranVo.getKms_last().length() > 0) {
            sf.append(" and ct.accountcode <= ? ");
            sp.addParam(paranVo.getKms_last());
        }
        if (pk_bz != null && pk_bz.length() > 0) {
            sf.append(" and qc.pk_currency = ?");
            sp.addParam(pk_bz);
        }
        //这个考虑的时候，没有考虑外币情况
//			QcYeVO[] vos = (QcYeVO[])singleObjectBO.queryByCondition(QcYeVO.class, sf.toString(), sp);
        List<IcbalanceVO> list = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(IcbalanceVO.class));

        if (list == null || list.size() == 0) {
            return new HashMap<String, FzhsqcVO>();

        }


        List<FzhsqcVO> ylist = new ArrayList<FzhsqcVO>();
        FzhsqcVO yevo = null;
        for (IcbalanceVO vo : list) {
            yevo = new FzhsqcVO();
            yevo.setFzhsx6(vo.getPk_inventory());
            yevo.setPk_accsubj(vo.getPk_subject());
            yevo.setThismonthqc(vo.getNcost());
            yevo.setMonthqmnum(vo.getNnum());
            yevo.setVyear(Integer.parseInt(vo.getDbilldate().substring(0, 4)));
            yevo.setVcode(vo.getPk_subjectname() + "_" + vo.getInventorycode());
            yevo.setVname(vo.getPk_subject() + "_" + vo.getInventoryname());
            ylist.add(yevo);
        }
        FzhsqcVO[] vos = (FzhsqcVO[]) ylist.toArray(new FzhsqcVO[list.size()]);
        Map<String, FzhsqcVO> map3 = hashlizeObject(vos);
        return map3;
    }

    /**
     * 这个考虑的时候，没有考虑外币情况
     */
    public Map<String, QcYeVO> hashlizeObject(QcYeVO[] vos) {
        Map<String, QcYeVO> result = new HashMap<String, QcYeVO>();
        if (vos == null || vos.length == 0) {
            return result;
        }
        String key = null;
        for (QcYeVO v : vos) {
            key = v.getPk_accsubj();
            if (!result.containsKey(key)) {
                result.put(key, v);
            } else {
                DZFDouble mny = SafeCompute.add(v.getThismonthqc(), result.get(key).getThismonthqc());
                DZFDouble num = SafeCompute.add(v.getMonthqmnum(), result.get(key).getMonthqmnum());
                result.get(key).setThismonthqc(mny);
                result.get(key).setMonthqmnum(num);
            }
        }
        return result;
    }

    public Map<String, FzhsqcVO> hashlizeObject(FzhsqcVO[] vos) {
        Map<String, FzhsqcVO> result = new HashMap<String, FzhsqcVO>();
        if (vos == null || vos.length == 0) {
            return result;
        }
        for (FzhsqcVO v : vos) {
            String key = v.getVcode();
            if (!result.containsKey(key)) {
                result.put(key, v);
            }
        }
        return result;
    }

    public Map<String, List<NumMnyDetailVO>> hashlizeObject(List<NumMnyDetailVO> objs)
            throws DZFWarpException {
        Map<String, List<NumMnyDetailVO>> result = new HashMap<String, List<NumMnyDetailVO>>();
        if (objs == null || objs.isEmpty())
            return result;
        String key = null;
        List<NumMnyDetailVO> zlist = null;
        for (int i = 0; i < objs.size(); i++) {
            key = objs.get(i).getPk_subject();
            if (result.containsKey(key)) {
                result.get(key).add(objs.get(i));
            } else {
                zlist = new ArrayList<NumMnyDetailVO>();
                zlist.add(objs.get(i));
                result.put(key, zlist);
            }
        }
        return result;
    }

    public List<String> getSortKey(List<NumMnyDetailVO> objs) throws DZFWarpException {
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        if (objs == null || objs.isEmpty())
            return list;
        String key = null;
        for (int i = 0; i < objs.size(); i++) {
            key = objs.get(i).getPk_subject();
            if (map.containsKey(key)) {
                continue;
            } else {
                map.put(key, key);
                list.add(key);
            }
        }
        return list;
    }

    public void calcprice(List<NumMnyDetailVO> zlist1) {
        if (zlist1 == null || zlist1.size() == 0)
            return;
        for (int i = 0; i < zlist1.size(); i++) {
            if ("期初余额".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                continue;
            DZFDouble z1 = zlist1.get(i).getNmny();
            DZFDouble z2 = zlist1.get(i).getNnum();

            DZFDouble z3 = zlist1.get(i).getNdmny();
            DZFDouble z4 = zlist1.get(i).getNdnum();
            if (z1 != null && z2 != null && z2.doubleValue() != 0) {
                DZFDouble price = SafeCompute.div(z1, z2);
                price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
                if (("本期合计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                        || ("本年累计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                ) {
                    zlist1.get(i).setNprice(price);
                }
//				zlist1.get(i).setNprice(price);
            }
            if (z3 != null && z4 != null && z4.doubleValue() != 0) {
                DZFDouble price = SafeCompute.div(z3, z4);
                price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
//				zlist1.get(i).setNdprice(price);
                if (("本期合计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                        || ("本年累计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                ) {
                    zlist1.get(i).setNdprice(price);
                }
            }
        }
    }

    public void calcQM(List<NumMnyDetailVO> zlist1, HashMap<String, YntCpaccountVO> cpamap) {
        if (zlist1 == null || zlist1.size() == 0)
            return;
        for (int i = 0; i < zlist1.size(); i++) {
            if ("期初余额".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i))) {
                Integer t = getkmdir(cpamap, zlist1.get(i).getPk_subject());
                if(t != null){
                    zlist1.get(i).setDir(t.toString());
                }

                continue;
            } else if (("本年累计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))
                    || ("本期合计".equals(zlist1.get(i).getZy()) && ReportUtil.bSysZy(zlist1.get(i)))) {
                zlist1.get(i).setNynum(zlist1.get(i - 1).getNynum());
                zlist1.get(i).setNyprice(zlist1.get(i - 1).getNyprice());
                zlist1.get(i).setNymny(zlist1.get(i - 1).getNymny());
                zlist1.get(i).setDir(zlist1.get(i - 1).getDir());
            } else {
                Integer t = getkmdir(cpamap, zlist1.get(i).getPk_subject());
                if(t != null){
                    zlist1.get(i).setDir(t.toString());
                }

                DZFDouble z1 = zlist1.get(i - 1).getNynum();
                DZFDouble z2 = zlist1.get(i - 1).getNymny();
                DZFDouble z3 = DZFDouble.ZERO_DBL;
                DZFDouble z4 = DZFDouble.ZERO_DBL;
                if ("0".equals(zlist1.get(i).getDir())) {//借方
                    z3 = SafeCompute.sub(SafeCompute.add(z1, zlist1.get(i).getNnum()), zlist1.get(i).getNdnum());
                    z4 = SafeCompute.sub(SafeCompute.add(z2, zlist1.get(i).getNmny()), zlist1.get(i).getNdmny());
                }
                if ("1".equals(zlist1.get(i).getDir())) {//贷方
                    z3 = SafeCompute.add(SafeCompute.sub(z1, zlist1.get(i).getNnum()), zlist1.get(i).getNdnum());
                    z4 = SafeCompute.add(SafeCompute.sub(z2, zlist1.get(i).getNmny()), zlist1.get(i).getNdmny());
                }
                zlist1.get(i).setNynum(z3);
                zlist1.get(i).setNymny(z4);
                if (z4 != null && z3 != null && z3.doubleValue() != 0) {
                    DZFDouble price = SafeCompute.div(z4, z3);
                    price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
                    zlist1.get(i).setNyprice(price);
                }
            }
        }
    }

    //根据科目id获取科目方向
    private Integer getkmdir(HashMap<String, YntCpaccountVO> cpamap, String pk_accsubj) {
        if (cpamap == null || cpamap.size() == 0 || pk_accsubj == null || pk_accsubj.length() == 0) {
            return null;
        }
        Iterator iter = cpamap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            YntCpaccountVO val = (YntCpaccountVO) entry.getValue();
            if (pk_accsubj.equals(key)) {
                return val.getDirection();
            }
        }

        return null;
    }

    public List<NumMnyDetailVO> calcDetailVOs(DZFDate jzdate, List<String> keylist, String pk_corp, String startDate, String enddate,
                                              Map<String, List<NumMnyDetailVO>> map1, Map<String, QcYeVO> map2, HashMap<String, YntCpaccountVO> cpamap, Map<String, YntCpaccountVO> map) throws DZFWarpException {
        Map<String, NumMnyDetailVO> qcyevo = new HashMap<String, NumMnyDetailVO>();
        List<NumMnyDetailVO> zlist = new ArrayList<NumMnyDetailVO>();
        List<NumMnyDetailVO> alllist = new ArrayList<NumMnyDetailVO>();
        List<QcYeVO> qcyevos = null;
        if (map2 == null || map2.size() == 0) {
            qcyevos = new ArrayList<QcYeVO>();
        } else {
            qcyevos = new ArrayList<QcYeVO>(map2.values());
        }
        if (map1 != null && map1.size() > 0) {
            for (String key : keylist) {
                //String key = it.next();
                List<NumMnyDetailVO> list = map1.get(key);
                //先构造期初
                List<NumMnyDetailVO> zlist1 = new ArrayList<NumMnyDetailVO>();
                NumMnyDetailVO[] qcvos = null;
                NumMnyDetailVO[] fsvos = null;
                NumMnyDetailVO qcvo = null;
                NumMnyDetailVO[] detailvos = null;
                if (list != null && list.size() > 0) {
                    QcYeVO banvo = map2.get(key);
                    List<NumMnyDetailVO[]> qclist = calcsplitvo(jzdate.toString(), startDate, list);
                    if (qclist != null && qclist.size() > 0) {
                        qcvos = qclist.get(0);//期初
                        fsvos = qclist.get(1);//发生
                    }
                    qcvo = bulidQC(jzdate, list.get(0), startDate, qcvos, banvo, cpamap);
                    qcyevo.put(qcvo.getKmbm(), qcvo);
                    zlist1.add(qcvo);
                    alllist.addAll(list);
                    detailvos = list.toArray(new NumMnyDetailVO[list.size()]);
                }
//				if(fsvos!=null && fsvos.length>0){
                NumMnyDetailVO c1 = qcvo != null ? (NumMnyDetailVO) qcvo.clone() : null;
                calcbq(detailvos, zlist1, startDate, enddate, list, c1, qcyevos, pk_corp, map);
                //计算期末
//					calcQM(zlist1,cpamap);
                //计算单价
                calcprice(zlist1);

                map2.remove(key);
//				}
//				}
                zlist.addAll(zlist1);
            }
        }
        List<NumMnyDetailVO> zlist2 = zlist;
        List<String> parentkms = new ArrayList<String>();
        for (NumMnyDetailVO vo : zlist2) {
            String kmid = vo.getPk_subject();//科目
            List<String> parentkmid = getKmParent(cpamap, kmid);//父级科目
            parentkmid.removeAll(parentkms);
            parentkms.addAll(parentkmid);//去重并集
        }
        if (map2 != null && map2.size() > 0) {
            Iterator<String> it = map2.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                List<String> parentkmid = getKmParent(cpamap, key);//父级科目
                parentkmid.removeAll(parentkms);
                parentkms.addAll(parentkmid);//去重并集
            }
        }
        if (map2 != null) {
            List<String> listkms = new ArrayList(map2.keySet());
            parentkms.removeAll(listkms);
            for (String str : parentkms) {
                QcYeVO vo = new QcYeVO();
                vo.setPk_accsubj(str);
                map2.put(str, vo);
            }
        }
        if (map2 != null && map2.size() > 0) {
            Iterator<String> it = map2.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                QcYeVO vo = map2.get(key);
                NumMnyDetailVO qcvo = new NumMnyDetailVO();
                qcvo.setPk_subject(key);
                qcvo.setKmbm(getKmbmByPk(key, pk_corp, map));
                qcvo.setNymny(VoUtils.getDZFDouble(vo.getThismonthqc()));
                qcvo.setNynum(VoUtils.getDZFDouble(vo.getMonthqmnum()));
                List<String> keylis = new ArrayList<String>(qcyevo.keySet());
                if (!keylis.contains(getKmbmByPk(key, pk_corp, map))) {
                    qcyevo.put(qcvo.getKmbm(), qcvo);
                }
            }
        }
        if (map2 != null && map2.size() > 0) {
            Iterator<String> it = map2.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                QcYeVO vo = map2.get(key);
                List<NumMnyDetailVO> zlist1 = new ArrayList<NumMnyDetailVO>();
                NumMnyDetailVO zvo = build(jzdate, vo, startDate, pk_corp, qcyevo, zlist, cpamap, map);
                zlist1.add(zvo);
                List<NumMnyDetailVO> fsvos = new ArrayList<NumMnyDetailVO>();
                List<NumMnyDetailVO> bnljvos = new ArrayList<NumMnyDetailVO>();
                for (NumMnyDetailVO allvo : alllist) {
                    NumMnyDetailVO nvoclone = (NumMnyDetailVO) allvo.clone();
                    boolean bool = judgeisparent(nvoclone.getKmbm(), zvo.getKmbm());
                    if (bool && nvoclone.getPzh() != null && nvoclone.getPzh().length() > 0) {
                        nvoclone.setPk_subject(zvo.getPk_subject());
                        nvoclone.setKmbm(zvo.getKmbm());
                        nvoclone.setKmmc(zvo.getKmmc());
                        if (!fsvos.contains(nvoclone)) {
                            fsvos.add(nvoclone);
                        }
                    }
                    if (nvoclone.getPzh() != null && nvoclone.getPzh().length() > 0 && (bool || nvoclone.getKmbm().equals(zvo.getKmbm()))) {
                        if (!bnljvos.contains(nvoclone)) {
                            bnljvos.add(nvoclone);
                        }
                    }
//					else if(nvoclone.getPzh()!=null &&nvoclone.getPzh().length()>0&&nvoclone.getKmbm().equals(zvo.getKmbm())){
//						if(!anfsvos.contains(nvoclone)){
//							anfsvos.add(nvoclone);
//						}
//					}
                }
                if ((fsvos != null && fsvos.size() > 0) || (bnljvos != null && bnljvos.size() > 0)) {
                    NumMnyDetailVO c1 = (NumMnyDetailVO) zvo.clone();
                    calcbq(fsvos.toArray(new NumMnyDetailVO[fsvos.size()]), zlist1, startDate, enddate, bnljvos, c1, qcyevos, pk_corp, map);
                    //计算期末
                    calcQM(zlist1, cpamap);
                    //计算单价
                    calcprice(zlist1);
                    zlist.addAll(zlist1);
                } else {
                    String[] keys = new String[]{startDate.substring(0, 7)};
                    List<String> keylist1 = new ArrayList<String>();
                    keylist1.add(startDate.substring(0, 7));
                    boolean bool = true;
                    if (keys != null && keys.length > 0) {
                        while (bool) {
                            String keyLast = keylist1.get(keylist1.size() - 1);
                            if (keyLast.compareTo(enddate.substring(0, 7)) < 0) {
                                String newKey = getNextKey(keyLast);
                                keylist1.add(newKey);
                            } else {
                                bool = false;
                            }
                        }
                        keys = keylist1.toArray(new String[0]);
                    }
                    zlist.add(zvo);
                    for (String tempKey : keys) {
                        NumMnyDetailVO c1 = (NumMnyDetailVO) zvo.clone();
                        NumMnyDetailVO c2 = (NumMnyDetailVO) zvo.clone();
                        int qcyear = Integer.parseInt(startDate.substring(0, 4));
                        for (QcYeVO qc : qcyevos) {
                            if (qcyear == qc.getVyear().intValue() && qc.getMonthqmnum().doubleValue() != 0 && c2.getKmbm().equals(getKmbmByPk(qc.getPk_accsubj(), pk_corp, map))) {
//							c2.set qc.getBnfsnum();
//							z3 = qc.getYearjffse();
//							z4 = qc.getBndffsnum();
//							z6 = qc.getYeardffse();
                                c2.setNnum(qc.getBnfsnum());
                                c2.setNmny(qc.getYearjffse());
                                if (qc.getBnfsnum() != null && qc.getBnfsnum().doubleValue() != 0) {
                                    c2.setNprice(SafeCompute.div(qc.getYearjffse(), qc.getBnfsnum()));
                                } else {
                                    c2.setNprice(null);
                                }
                                c2.setNdnum(qc.getBndffsnum());
                                c2.setNdmny(qc.getYeardffse());
                                if (qc.getBndffsnum() != null && qc.getBndffsnum().doubleValue() != 0) {
                                    c2.setNdprice(SafeCompute.div(qc.getYeardffse(), qc.getBndffsnum()));
                                } else {
                                    c2.setNdprice(null);
                                }
                                break;
                            }
                        }
                        c1.setZy("本期合计");
                        c1.setBsyszy(DZFBoolean.TRUE);
                        String s = tempKey.substring(0, 7);
                        c1.setQj(s);
                        c2.setQj(s);
                        int y = Integer.parseInt(s.substring(0, 4));
                        int m = Integer.parseInt(s.substring(5));
                        c1.setOpdate(s + "-" + getMonthLastDay(y, m));
                        c2.setOpdate(s + "-" + getMonthLastDay(y, m));
                        c2.setZy("本年累计");
                        c2.setBsyszy(DZFBoolean.TRUE);
                        zlist.addAll(fsvos);
                        zlist.add(c1);
                        zlist.add(c2);

                    }
                }
            }
        }
        return zlist;
    }

    //根据编码判断2科目编码是否是1编码的直接上级 2是1的上级 改成判断是上级就行
    private boolean judgeisparent(String kmbm1, String kmbm2) {
		/*String accrule = cpaccountService.queryAccountRule(pk_corp);
		if(kmbm1==null||kmbm1.length()==0||kmbm2==null||kmbm2.length()==0){
			return false;
		}
		boolean flag1 = false;
		boolean flag2 = false;
		int leng = 0;
		String rules[] = accrule.split("/");
		for(int i =0;i<rules.length;i++){
			String s = rules[i];
			int len = Integer.parseInt(s);
			leng+=len;
			if(leng==kmbm2.length()){
				String str = rules[i+1];
				int len1 = Integer.parseInt(str);
				if(kmbm1.length()==leng+len1){
					flag1 = true;
				}
			}
		}
		if(kmbm1.startsWith(kmbm2)){
			flag2 = true;
		}
		return flag1&&flag2;*/
        if (kmbm1 == null || kmbm1.length() == 0 || kmbm2 == null || kmbm2.length() == 0) {
            return false;
        }
        if (kmbm1.startsWith(kmbm2) && kmbm1.length() > kmbm2.length()) {
            return true;
        }
        return false;
    }


    public NumMnyDetailVO queryINfo(String pk_accsubj, String pk_corp, Map<String, YntCpaccountVO> map) throws DZFWarpException {

        if (map == null || map.size() == 0)
            return new NumMnyDetailVO();
        YntCpaccountVO accvo = map.get(pk_accsubj);

        if (accvo != null) {
            NumMnyDetailVO vo = new NumMnyDetailVO();
            vo.setKmmc(accvo.getFullname());
            vo.setPk_subject(accvo.getPk_corp_account());
            vo.setJldw(accvo.getMeasurename());
            vo.setKmbm(accvo.getAccountcode());
            if (accvo.getDirection() != null)
                vo.setDir(accvo.getDirection().toString());
            return vo;
        } else {
            StringBuffer sf = new StringBuffer();
            sf.append(
                    " select t.fullname,t.pk_corp_account,t.accountcode,t.measurename,t.direction  from ynt_cpaccount t ");
            sf.append(" where t.pk_corp = ? and t.pk_corp_account = ? ");
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam(pk_accsubj);
            NumMnyDetailVO cua = (NumMnyDetailVO) singleObjectBO.executeQuery(sf.toString(), sp, new ResultSetProcessor() {
                public Object handleResultSet(ResultSet rs) throws SQLException {
                    NumMnyDetailVO vo = new NumMnyDetailVO();
                    if (rs.next()) {
                        vo.setKmmc(rs.getString("fullname"));
                        // vo.setPk_inventory(rs.getString("pk_inventory"));
                        vo.setPk_subject(rs.getString("pk_corp_account"));
                        vo.setJldw(rs.getString("measurename"));
                        vo.setKmbm(rs.getString("accountcode"));
                        vo.setDir(rs.getString("direction"));
                    }
                    return vo;
                }
            });
            return cua;
        }
    }

    public NumMnyDetailVO build(DZFDate jzdate, QcYeVO banvo, String startDate, String pk_corp, Map<String, NumMnyDetailVO> qcyevomap,
                                List<NumMnyDetailVO> zlist1, HashMap<String, YntCpaccountVO> cpamap, Map<String, YntCpaccountVO> map) throws DZFWarpException {
        if (jzdate != null && jzdate.after(new DZFDate(startDate))) {
            startDate = jzdate.toString();
        }
        String startqj = calcqj(startDate);
        NumMnyDetailVO cvo = new NumMnyDetailVO();
        cvo.setPk_subject(banvo.getPk_accsubj());
//		NumMnyDetailVO mvo = queryINfo(banvo.getPk_accsubj(),pk_corp);
        YntCpaccountVO cpavo = cpamap.get(banvo.getPk_accsubj());
        if (cpavo == null)
            cpavo = new YntCpaccountVO();
        cvo.setPk_subject(cpavo.getPrimaryKey());
        cvo.setJldw(cpavo.getMeasurename());
        cvo.setKmbm(cpavo.getAccountcode());
        cvo.setKmmc(cpavo.getFullname());
//		cvo.setSpmc(mvo.getSpmc());
        cvo.setDir(cpavo.getDirection() + "");
        cvo.setQj(startqj);
        cvo.setOpdate(startqj + "-01");
        cvo.setPzh(null);
        cvo.setZy("期初余额");
        cvo.setBsyszy(DZFBoolean.TRUE);
        cvo.setNnum(null);
        cvo.setNprice(null);
        cvo.setNmny(null);
        cvo.setNdnum(null);
        cvo.setNdprice(null);
        cvo.setNdmny(null);
        banvo = calqc(banvo, qcyevomap, pk_corp, zlist1, map);
        cvo.setNynum(banvo.getMonthqmnum());
        if (banvo.getThismonthqc() != null && banvo.getMonthqmnum() != null && banvo.getMonthqmnum().doubleValue() != 0) {
            DZFDouble price = SafeCompute.div(banvo.getThismonthqc(), banvo.getMonthqmnum());
            price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
            cvo.setNyprice(price);
        }
        cvo.setNymny(banvo.getThismonthqc());
        return cvo;
    }

    private QcYeVO calqc(QcYeVO banvo, Map<String, NumMnyDetailVO> qcyevomap, String pk_corp, List<NumMnyDetailVO> zlist1, Map<String, YntCpaccountVO> map) {
        Iterator iter = qcyevomap.entrySet().iterator();
        DZFDouble d1 = DZFDouble.ZERO_DBL;
        DZFDouble d2 = DZFDouble.ZERO_DBL;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();//科目bm
            NumMnyDetailVO val = (NumMnyDetailVO) entry.getValue();
            String temp = getKmbmByPk(banvo.getPk_accsubj(), pk_corp, map);
            if (key.startsWith(temp) && key.length() > temp.length()) {
                boolean isadd = true;
                for (Map.Entry<String, NumMnyDetailVO> en : qcyevomap.entrySet()) {
                    String senkey = en.getKey();
                    if (senkey.startsWith(key) && senkey.length() > key.length()) {
                        isadd = false;
                        break;
                    }
                }
                if (isadd) {
                    d1 = SafeCompute.add(d1, val.getNymny());
                    d2 = SafeCompute.add(d2, val.getNynum());
                    banvo.setThismonthqc(d1);
                    banvo.setMonthqmnum(d2);
                }

            }
        }

        return banvo;
    }

    private String getKmbmByPk(String kmid, String pk_corp, Map<String, YntCpaccountVO> map) {
        if (kmid == null | kmid.length() == 0)
            return null;

        if (map == null || map.size() == 0)
            return null;
        YntCpaccountVO accvo = map.get(kmid);

        if (accvo != null) {
            return accvo.getAccountcode();
        } else {
            YntCpaccountVO vo = zxkjPlatformService.queryById(kmid);
            if (vo != null) {
                return vo.getAccountcode();
            }
        }
        return null;
    }

    public Map<String, List<NumMnyDetailVO>> hashlizeObject1(List<NumMnyDetailVO> objs)
            throws DZFWarpException {
        Map<String, List<NumMnyDetailVO>> result = new HashMap<String, List<NumMnyDetailVO>>();
        if (objs == null || objs.isEmpty())
            return result;
        String key = null;
        for (int i = 0; i < objs.size(); i++) {
            key = objs.get(i).getQj();
            if (result.containsKey(key)) {
                result.get(key).add(objs.get(i));
            } else {
                List<NumMnyDetailVO> zlist = new ArrayList<NumMnyDetailVO>();
                zlist.add(objs.get(i));
                result.put(key, zlist);
            }
        }
        return result;
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

    public void calcbq(NumMnyDetailVO[] vos, List<NumMnyDetailVO> zlist1, String startDate, String enddate, List<NumMnyDetailVO> bnljvos, NumMnyDetailVO c1, List<QcYeVO> qcyevos, String pk_corp, Map<String, YntCpaccountVO> map) throws DZFWarpException {
        if ((vos == null || vos.length == 0) && (bnljvos == null || bnljvos.size() == 0))
            return;
        List<NumMnyDetailVO> list = new ArrayList<NumMnyDetailVO>(Arrays.asList(vos));
        Map<String, List<NumMnyDetailVO>> maps = hashlizeObject1(list);
//		String[] keys = maps.keySet().toArray(new String[0]);
        String[] keys = new String[]{startDate.substring(0, 7)};
//		if(keys==null || keys.length==0){
//			keys = new String[]{startDate.substring(0,7)};
//		}
//		Arrays.sort(keys); 
        List<String> keylist = Arrays.asList(keys);
        List<String> keylist1 = new ArrayList<String>();
        keylist1.addAll(keylist);
        boolean bool = true;
        if (keys != null && keys.length > 0) {
            while (bool) {
                String keyLast = keylist1.get(keylist1.size() - 1);
                if (keyLast.compareTo(enddate.substring(0, 7)) < 0) {
                    String newKey = getNextKey(keyLast);
                    keylist1.add(newKey);
                } else {
                    bool = false;
                }
            }
            keys = keylist1.toArray(new String[0]);
        }
        for (String key : keys) {
            List<NumMnyDetailVO> zlist = new ArrayList<NumMnyDetailVO>();
            if ((startDate.substring(0, 7)).compareTo(key) <= 0) {
                zlist = maps.get(key);
            }
            if (zlist != null && zlist.size() > 0) {
                zlist1.addAll(zlist);
                NumMnyDetailVO vo = calcbqhj(zlist);
                int y = Integer.parseInt(key.substring(0, 4));
                int m = Integer.parseInt(key.substring(5));
                vo.setOpdate(key + "-" + getMonthLastDay(y, m));
                zlist1.add(vo);
            } else {
                if ((startDate.substring(0, 7)).compareTo(key) <= 0) {
                    NumMnyDetailVO vo1 = (NumMnyDetailVO) c1.clone();
                    vo1.setQj(key);
                    int y = Integer.parseInt(key.substring(0, 4));
                    int m = Integer.parseInt(key.substring(5));
                    vo1.setOpdate(key + "-" + getMonthLastDay(y, m));
                    vo1.setPzh(null);
                    vo1.setZy("本期合计");
                    vo1.setBsyszy(DZFBoolean.TRUE);
                    vo1.setNnum(null);
                    vo1.setNprice(null);
                    vo1.setNmny(null);
                    vo1.setNdnum(null);
                    vo1.setNdprice(null);
                    vo1.setNdmny(null);
                    zlist1.add(vo1);
                }
            }
            if (bnljvos != null && bnljvos.size() > 0) {
                if ((startDate.substring(0, 7)).compareTo(key) <= 0) {
                    NumMnyDetailVO vo1 = calcbnlj(bnljvos, qcyevos, startDate, key, pk_corp, map);
                    zlist1.add(vo1);
                }
            } else {
                if ((startDate.substring(0, 7)).compareTo(key) <= 0) {
                    NumMnyDetailVO vo1 = (NumMnyDetailVO) c1.clone();
                    vo1.setQj(key);
                    vo1.setPzh(null);
                    vo1.setZy("本年累计");
                    vo1.setBsyszy(DZFBoolean.TRUE);
                    vo1.setNnum(null);
                    vo1.setNprice(null);
                    vo1.setNmny(null);
                    vo1.setNdnum(null);
                    vo1.setNdprice(null);
                    vo1.setNdmny(null);
                    zlist1.add(vo1);
                }
            }
        }
    }

    private int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public NumMnyDetailVO calcbnlj(List<NumMnyDetailVO> bqhjlist, List<QcYeVO> qcyevos, String startdate, String key, String pk_corp, Map<String, YntCpaccountVO> map) {//2016-01
        if (bqhjlist == null || bqhjlist.size() == 0)
            return null;
        int year = Integer.parseInt(key.substring(0, 4));
        int month = Integer.parseInt(key.substring(5, 7));
        NumMnyDetailVO vo1 = (NumMnyDetailVO) bqhjlist.get(bqhjlist.size() - 1).clone();
        vo1.setQj(key.substring(0, 7));
        if (key.length() == 7) {//2016-01-01
            vo1.setOpdate(key + "-" + getMonthLastDay(year, month));
        } else {
            int year1 = Integer.parseInt(key.substring(0, 4));
            int month1 = Integer.parseInt(key.substring(5, 7));
            vo1.setOpdate(key.substring(0, 7) + "-" + getMonthLastDay(year1, month1));
        }
        vo1.setPzh(null);
        vo1.setZy("本年累计");
        vo1.setBsyszy(DZFBoolean.TRUE);
        DZFDouble z1 = null;
        DZFDouble z3 = null;
        DZFDouble z4 = null;
        DZFDouble z6 = null;
        int qcyear = Integer.parseInt(startdate.substring(0, 4));
//		for(NumMnyDetailVO c : bqhjlist){
        for (QcYeVO qc : qcyevos) {
            if (qc.getMonthqmnum() == null) {
                qc.setMonthqmnum(DZFDouble.ZERO_DBL);
            }
            if (qcyear == qc.getVyear().intValue() && qc.getMonthqmnum().doubleValue() != 0 && vo1.getKmbm().equals(getKmbmByPk(qc.getPk_accsubj(), pk_corp, map))) {
                z1 = qc.getBnfsnum();
                z3 = qc.getYearjffse();
                z4 = qc.getBndffsnum();
                z6 = qc.getYeardffse();
                break;
            }
        }
//		}
        for (NumMnyDetailVO c : bqhjlist) {
            if (c.getOpdate().compareTo(startdate.substring(0, 4) + "-01") >= 0
                    && c.getOpdate().substring(0, 7).compareTo(key) <= 0
                    && key.startsWith(c.getOpdate().substring(0, 4))) {
                z1 = SafeCompute.add(z1, c.getNnum());
                z3 = SafeCompute.add(z3, c.getNmny());
                z4 = SafeCompute.add(z4, c.getNdnum());
                z6 = SafeCompute.add(z6, c.getNdmny());
            }
        }
        vo1.setNnum(z1);
        vo1.setNprice(null);
        vo1.setNmny(z3);
        vo1.setNdnum(z4);
        vo1.setNdprice(null);
        vo1.setNdmny(z6);
        return vo1;
    }

    public NumMnyDetailVO calcbqhj(List<NumMnyDetailVO> qjlist) {
        if (qjlist == null || qjlist.size() == 0) {
            return null;
        }
        NumMnyDetailVO vo1 = (NumMnyDetailVO) qjlist.get(qjlist.size() - 1).clone();
        vo1.setPzh(null);
//		vo1.setOpdate(opdate);
        vo1.setZy("本期合计");
        vo1.setBsyszy(DZFBoolean.TRUE);
        DZFDouble z1 = null;
        DZFDouble z3 = null;
        DZFDouble z4 = null;
        DZFDouble z6 = null;
        for (NumMnyDetailVO c : qjlist) {
            z1 = SafeCompute.add(z1, c.getNnum());
            z3 = SafeCompute.add(z3, c.getNmny());
            z4 = SafeCompute.add(z4, c.getNdnum());
            z6 = SafeCompute.add(z6, c.getNdmny());
        }
        vo1.setNnum(z1);
        vo1.setNprice(null);
        vo1.setNmny(z3);
        vo1.setNdnum(z4);
        vo1.setNdprice(null);
        vo1.setNdmny(z6);
        return vo1;
    }

    private QcYeVO calcnumqc(NumMnyDetailVO[] qcvos, QcYeVO banvo) throws DZFWarpException {
        if (banvo == null)
            return null;
        DZFDouble bc = banvo == null ? DZFDouble.ZERO_DBL : banvo.getMonthqmnum();//数量期初
        DZFDouble ac = banvo == null ? DZFDouble.ZERO_DBL : banvo.getThismonthqc();//金额期初
        if (qcvos != null && qcvos.length > 0) {
            for (NumMnyDetailVO v : qcvos) {
                DZFDouble num = v.getNnumber() == null ? DZFDouble.ZERO_DBL : v.getNnumber();
                DZFDouble jf = v.getJfmny();
                DZFDouble df = v.getDfmny();
                if (jf != null && jf.doubleValue() > 0) {
                    bc = SafeCompute.add(bc, num);
                    ac = SafeCompute.add(ac, jf);
                } else if (df != null && df.doubleValue() < 0) {
                    bc = SafeCompute.add(bc, num.multiply(-1));
                    ac = SafeCompute.add(ac, df.multiply(-1));
                } else if (jf != null && jf.doubleValue() < 0) {
                    bc = SafeCompute.sub(bc, num.multiply(-1));
                    ac = SafeCompute.sub(ac, jf.multiply(-1));
                } else if (df != null && df.doubleValue() > 0) {
                    bc = SafeCompute.sub(bc, num);
                    ac = SafeCompute.sub(ac, df);
                }
            }
        }
        banvo.setMonthqmnum(bc);
        banvo.setThismonthqc(ac);
        return banvo;
    }

    private List<NumMnyDetailVO[]> calcsplitvo(String jzdate, String startDate, List<NumMnyDetailVO> list) {
        List<NumMnyDetailVO[]> z = new ArrayList<NumMnyDetailVO[]>();
        if (startDate == null || startDate.length() == 0)
            return z;
        if (list == null || list.size() == 0)
            return z;
        NumMnyDetailVO[] vos = new NumMnyDetailVO[list.size()];
        list.toArray(vos);
        DZFDate d1 = new DZFDate(startDate);
        int index = -1;
        for (int i = 0; i < vos.length; i++) {
            DZFDate d2 = new DZFDate(vos[i].getOpdate());
            if (d2.before(d1)) {//期初
                index = i;
            }
        }
        if (index == -1) {
            z.add(null);//期初
            z.add(vos);//发生
        } else {
            int len = vos.length;
            NumMnyDetailVO[] v1 = new NumMnyDetailVO[index + 1];//期初
            NumMnyDetailVO[] v2 = new NumMnyDetailVO[len - (index + 1)];//发生
            System.arraycopy(vos, 0, v1, 0, index + 1);
            System.arraycopy(vos, index + 1, v2, 0, len - (index + 1));
            z.add(v1);
            z.add(v2);
        }
        return z;
    }

    public NumMnyDetailVO bulidQC(DZFDate jzdate, NumMnyDetailVO dvo, String startDate, NumMnyDetailVO[] qcvos, QcYeVO banvo, HashMap<String, YntCpaccountVO> cpamap) throws DZFWarpException {
        if (dvo == null)
            return null;
        if (jzdate != null && jzdate.after(new DZFDate(startDate))) {
            startDate = jzdate.toString();
        }
        String startqj = calcqj(startDate);
        NumMnyDetailVO cvo = (NumMnyDetailVO) dvo.clone();
        cvo.setPzh(null);
        cvo.setQj(startqj);
        cvo.setOpdate(startqj + "-01");
        //cvo.setOpdate(dvo.getQj()+"-01");
        cvo.setZy("期初余额");
        cvo.setBsyszy(DZFBoolean.TRUE);
        cvo.setNnum(null);
        cvo.setNprice(null);
        cvo.setNmny(null);
        cvo.setNdnum(null);
        cvo.setNdprice(null);
        cvo.setNdmny(null);
        if ((qcvos != null && qcvos.length > 0) || (banvo != null)) {
            if (banvo == null)
                banvo = new QcYeVO();
            calcnumqc(qcvos, banvo);
            DZFDouble mny = banvo.getThismonthqc();
            DZFDouble num = banvo.getMonthqmnum();
            Integer t = getkmdir(cpamap, cvo.getPk_subject());
            if(t != null){
                cvo.setDir(t.toString());
            }

            if ("1".equals(cvo.getDir())) {
                num = SafeCompute.multiply(num, new DZFDouble(-1));
                mny = SafeCompute.multiply(mny, new DZFDouble(-1));
            }
            cvo.setNynum(num);
            cvo.setNymny(mny);
            if (mny != null && num != null && num.doubleValue() != 0) {
                DZFDouble price = SafeCompute.div(mny, num);
                price = price.setScale(precisionPrice, DZFDouble.ROUND_HALF_UP);
                cvo.setNyprice(price);
            }
        } else {
            cvo.setNynum(null);
            cvo.setNyprice(null);
            cvo.setNymny(null);
        }
        return cvo;
    }


    public String calcqj(String date) throws DZFWarpException {
        if (date == null)
            throw new BusinessException("查询期间数据为空");
        return date.substring(0, 7);
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
}

