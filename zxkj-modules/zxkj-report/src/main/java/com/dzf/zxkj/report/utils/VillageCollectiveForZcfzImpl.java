package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.ArrayUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资产负债取数(村集体)
 *
 * @author zhangj
 */
@SuppressWarnings("all")
public class VillageCollectiveForZcfzImpl {

    public ZcFzBVO[] getCauseZCFZBVOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc) throws Exception {
        ZcFzBVO votemp = new ZcFzBVO();

        ZcFzBVO vo0 = new ZcFzBVO();
        vo0.setZc("流动资产：");
        vo0.setFzhsyzqy("流动负债：");

        ZcFzBVO vo1 = new ZcFzBVO();
        vo1 = getZCFZBVO(map, vo1, true, "1001", "1002");
        vo1 = getZCFZBVO(map, vo1, false, "2001");
        vo1.setZc("　货币资金");
        vo1.setFzhsyzqy("　短期借款");
        vo1.setHc1("1");
        vo1.setHc2("35");


        ZcFzBVO vo2 = new ZcFzBVO();
        vo2 = getZCFZBVO(map, vo2, true, "1101");
        vo2 = getZCFZBVO(map, vo2, false, "2002", "1103");
        vo2.setZc("　短期投资");
        vo2.setFzhsyzqy("　应付款项");
        vo2.setHc1("2");
        vo2.setHc2("36");


        ZcFzBVO vo3 = new ZcFzBVO();
        vo3 = getZCFZBVO(map, vo3, true, "1102", "1103");
        vo3 = getZCFZBVO(map, vo3, false, "2101");
        vo3.setZc("　应收款项");
        vo3.setFzhsyzqy("　应付工资");
        vo3.setHc1("5");
        vo3.setHc2("37");

        ZcFzBVO vo4 = new ZcFzBVO();
        vo4 = getZCFZBVO(map, vo4, true, "1201", "4001");
        vo4 = getZCFZBVO(map, vo4, false, "2102");
        vo4.setZc("　存货");
        vo4.setFzhsyzqy("　应付福利费");
        vo4.setHc1("8");
        vo4.setHc2("38");

        ZcFzBVO vo10 = new ZcFzBVO();
        vo10.setNcye1(VoUtils.getDZFDouble(vo1.getNcye1()).add(VoUtils.getDZFDouble(vo2.getNcye1())).add(VoUtils.getDZFDouble(vo3.getNcye1())).add(VoUtils.getDZFDouble(vo4.getNcye1())));
        vo10.setQmye1(VoUtils.getDZFDouble(vo1.getQmye1()).add(VoUtils.getDZFDouble(vo2.getQmye1())).add(VoUtils.getDZFDouble(vo3.getQmye1())).add(VoUtils.getDZFDouble(vo4.getQmye1())));
        vo10.setZc("流动资产合计");
        vo10.setNcye2(VoUtils.getDZFDouble(vo1.getNcye2()).add(VoUtils.getDZFDouble(vo2.getNcye2())).
                add(VoUtils.getDZFDouble(vo3.getNcye2())).add(VoUtils.getDZFDouble(vo4.getNcye2())));
        vo10.setQmye2(VoUtils.getDZFDouble(vo1.getQmye2()).add(VoUtils.getDZFDouble(vo2.getQmye2()))
                .add(VoUtils.getDZFDouble(vo3.getQmye2())).add(VoUtils.getDZFDouble(vo4.getQmye2())));
        vo10.setFzhsyzqy("流动负债合计");
        vo10.setHc1("9");
        vo10.setHc2("41");

        ZcFzBVO vo11 = new ZcFzBVO();
        vo11.setZc("农业资产：");
        vo11.setFzhsyzqy("长期负债：");


        ZcFzBVO vo12 = new ZcFzBVO();
        vo12 = getZCFZBVO(map, vo12, true, "1301");
        vo12 = getZCFZBVO(map, vo12, false, "2201");
        vo12.setZc("　牲畜（禽）资产");
        vo12.setFzhsyzqy("　长期借款及应付款");
        vo12.setHc1("10");
        vo12.setHc2("42");


        ZcFzBVO vo13 = new ZcFzBVO();
        vo13 = getZCFZBVO(map, vo13, true, "1302");
        vo13 = getZCFZBVO(map, vo13, false, "2301");
        vo13.setZc("　林木资产");
        vo13.setFzhsyzqy("　一事一议资金");
        vo13.setHc1("11");
        vo13.setHc2("43");

        ZcFzBVO vo14 = new ZcFzBVO();
        vo14.setZc("农业资产合计");
        vo14.setFzhsyzqy("长期负债合计");
        vo14.setNcye1(VoUtils.getDZFDouble(vo12.getNcye1()).add(VoUtils.getDZFDouble(vo13.getNcye1())));
        vo14.setQmye1(VoUtils.getDZFDouble(vo12.getQmye1()).add(VoUtils.getDZFDouble(vo13.getQmye1())));
        vo14.setNcye2(VoUtils.getDZFDouble(vo12.getNcye2()).add(VoUtils.getDZFDouble(vo13.getNcye2())));
        vo14.setQmye2(VoUtils.getDZFDouble(vo12.getQmye2()).add(VoUtils.getDZFDouble(vo13.getQmye2())));
        vo14.setHc1("15");
        vo14.setHc2("46");


        ZcFzBVO vo15 = new ZcFzBVO();
        vo15.setQmye2(VoUtils.getDZFDouble(vo10.getQmye2()).add(VoUtils.getDZFDouble(vo14.getQmye2())));
        vo15.setNcye2(VoUtils.getDZFDouble(vo10.getNcye2()).add(VoUtils.getDZFDouble(vo14.getNcye2())));
        vo15.setZc("长期资产：");
        vo15.setFzhsyzqy("负债合计");
        vo15.setHc1(null);
        vo15.setHc2("49");

        ZcFzBVO vo16 = new ZcFzBVO();
        vo16 = getZCFZBVO(map, vo16, true, "1401");
        vo16.setZc("　长期投资");
        vo16.setFzhsyzqy(null);
        vo16.setHc1("16");
        vo16.setHc2(null);

        ZcFzBVO vo17 = new ZcFzBVO();
        vo17.setZc("固定资产");
        vo17.setFzhsyzqy(null);

        ZcFzBVO vo18 = new ZcFzBVO();
        vo18 = getZCFZBVO(map, vo18, true, "1501");
        vo18.setZc("　　固定资产原价");
        vo18.setFzhsyzqy("所有者权益：");
        vo18.setHc1("19");

        ZcFzBVO vo19 = new ZcFzBVO();
        vo19 = getZCFZBVO(map, vo19, true, "1502");
        vo19 = getZCFZBVO(map, vo19, false, "3001");
        vo19.setZc("　　减：累计折旧");
        vo19.setFzhsyzqy("　资本");
        vo19.setHc1("20");
        vo19.setHc2("50");

        ZcFzBVO vo20 = new ZcFzBVO();
        vo20.setNcye1(VoUtils.getDZFDouble(vo18.getNcye1()).sub(VoUtils.getDZFDouble(vo19.getNcye1())));
        vo20.setQmye1(VoUtils.getDZFDouble(vo18.getQmye1()).sub(VoUtils.getDZFDouble(vo19.getQmye1())));
        vo20 = getZCFZBVO(map, vo20, false, "3101");
        vo20.setZc("　固定资产净值");
        vo20.setFzhsyzqy("　公积公益金");
        vo20.setHc1("21");
        vo20.setHc2("51");

        ZcFzBVO vo21 = new ZcFzBVO();
        vo21 = getZCFZBVO(map, vo21, true, "1503");
        vo21 = getZCFZBVO(map, vo21, false, "3201", "3202");
        vo21.setZc("　固定资产清理");
        vo21.setFzhsyzqy("　未分配收益");
        vo21.setHc1("22");
        vo21.setHc2("52");

        ZcFzBVO vo22 = new ZcFzBVO();
        vo22 = getZCFZBVO(map, vo22, true, "1504");
        vo22.setNcye2(VoUtils.getDZFDouble(vo19.getNcye2()).add(VoUtils.getDZFDouble(vo20.getNcye2())).add(VoUtils.getDZFDouble(vo21.getNcye2())));
        vo22.setQmye2(VoUtils.getDZFDouble(vo19.getQmye2()).add(VoUtils.getDZFDouble(vo20.getQmye2())).add(VoUtils.getDZFDouble(vo21.getQmye2())));
        vo22.setZc("在建工程");
        vo22.setFzhsyzqy("所有者权益合计");
        vo22.setHc1("23");
        vo22.setHc2("53");

        ZcFzBVO vo23 = new ZcFzBVO();
        vo23.setNcye1(VoUtils.getDZFDouble(vo20.getNcye1()).add(VoUtils.getDZFDouble(vo21.getNcye1())).add(VoUtils.getDZFDouble(vo22.getNcye1())));
        vo23.setQmye1(VoUtils.getDZFDouble(vo20.getQmye1()).add(VoUtils.getDZFDouble(vo21.getQmye1())).add(VoUtils.getDZFDouble(vo22.getQmye1())));
        vo23.setZc("　固定资产合计");
        vo23.setFzhsyzqy(null);
        vo23.setHc1("26");
        vo23.setHc2("53");

        ZcFzBVO vo24 = new ZcFzBVO();
        vo24.setNcye1(VoUtils.getDZFDouble(vo10.getNcye1()).add(VoUtils.getDZFDouble(vo14.getNcye1()))
                .add(VoUtils.getDZFDouble(vo16.getNcye1())).add(VoUtils.getDZFDouble(vo23.getNcye1())));
        vo24.setQmye1(VoUtils.getDZFDouble(vo10.getQmye1()).add(VoUtils.getDZFDouble(vo14.getQmye1()))
                .add(VoUtils.getDZFDouble(vo16.getQmye1())).add(VoUtils.getDZFDouble(vo23.getQmye1())));
        vo24.setQmye2(VoUtils.getDZFDouble(vo15.getQmye2()).add(VoUtils.getDZFDouble(vo22.getQmye2())));
        vo24.setNcye2(VoUtils.getDZFDouble(vo15.getNcye2()).add(VoUtils.getDZFDouble(vo22.getNcye2())));
        vo24.setZc("资产总计");
        vo24.setFzhsyzqy("负债和所有者权益合计");
        vo24.setHc1("32");
        vo24.setHc2("56");

        ZcFzBVO vo25 = new ZcFzBVO();
        vo25.setZc("补充资料:");
        vo25.setColspan(8);

        String[][] xms = new String[][]{{"项   目", "金额"},
                {"无法收回、尚未批准核销的短期投资", ""},
                {"确实无法收回、尚未批准核销的应收款项", ""},
                {"盘亏、毁损和报废、尚未批准核销的存货", ""},
                {"死亡毁损、尚未批准核销的农业资产", ""},
                {"无法收回、尚未批准核销的长期投资", ""},
                {"盘亏和毁损、尚未批准核销的固定资产", ""},
                {"毁损和报废、尚未批准核销的在建工程", ""}};

        ZcFzBVO[] bvoxms = new ZcFzBVO[xms.length];
        int count = 0;
        for (String[] str : xms) {
            ZcFzBVO bvotemp = new ZcFzBVO();
            bvotemp.setZc(str[0]);
            bvotemp.setFzhsyzqy(str[1]);
            bvotemp.setColspan(4);
            bvoxms[count] = bvotemp;
            count++;
        }

        return ArrayUtil.mergeArray(new ZcFzBVO[]{vo0, vo1, vo2, vo3, vo4, vo10, vo11, vo12, vo13, vo14, vo15, vo16, vo17, vo18, vo19, vo20
                , vo21, vo22, vo23, vo24, vo25}, bvoxms);

    }

    public void clearTempVO(ZcFzBVO votemp) {
        votemp.setNcye1(DZFDouble.ZERO_DBL);
        votemp.setQmye1(DZFDouble.ZERO_DBL);
        votemp.setNcye2(DZFDouble.ZERO_DBL);
        votemp.setQmye2(DZFDouble.ZERO_DBL);
    }


    private ZcFzBVO getZCFZBVO(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, String... kms) {

        List<FseJyeVO> ls = null;
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;

        StringBuffer appkms = new StringBuffer();
        if (is1) {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms() + ",");
        } else {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms() + ",");
        }
        for (int i = 0; i < len; i++) {
            ls = getData(map, kms[i]);
            appkms.append(kms[i] + ",");
            if (ls == null) continue;
            for (FseJyeVO fvo : ls) {
                if (is1) {
                    ufd = VoUtils.getDZFDouble(vo.getQmye1());
                    DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qmjf.doubleValue() < 0) {
                            qmjf = DZFDouble.ZERO_DBL;
                        }
                        if (qmdf.doubleValue() < 0) {
                            qmdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    ufd = ufd.add(qmjf).add(qmdf);

                    vo.setQmye1(ufd);
                    ufd = VoUtils.getDZFDouble(vo.getNcye1());
                    DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qcjf.doubleValue() < 0) {
                            qcjf = DZFDouble.ZERO_DBL;
                        }
                        if (qcdf.doubleValue() < 0) {
                            qcdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    ufd = ufd.add(qcjf).add(qcdf);

                    vo.setNcye1(ufd);


                    //本月期初
                    ufd = VoUtils.getDZFDouble(vo.getQcye1());
                    DZFDouble byqcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcjf());
                    DZFDouble bqqcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcdf());
                    ufd = ufd.add(byqcjf).add(bqqcdf);
                    vo.setQcye1(ufd);
                } else {
                    ufd = VoUtils.getDZFDouble(vo.getQmye2());
                    DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qmjf.doubleValue() < 0) {
                            qmjf = qmjf.multiply(-1);
                        } else {
                            qmjf = DZFDouble.ZERO_DBL;
                        }
                        if (qmdf.doubleValue() < 0) {
                            qmdf = qmdf.multiply(-1);
                        } else {
                            qmdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    ufd = ufd.add(qmjf).add(qmdf);//只有一方有值
                    vo.setQmye2(ufd);


                    //年初
                    ufd = VoUtils.getDZFDouble(vo.getNcye2());
                    DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qcjf.doubleValue() < 0) {
                            qcjf = qcjf.multiply(-1);
                        } else {
                            qcjf = DZFDouble.ZERO_DBL;
                        }
                        if (qcdf.doubleValue() < 0) {
                            qcdf = qcdf.multiply(-1);
                        } else {
                            qcdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    ufd = ufd.add(qcjf).add(qcdf);//只有一方有值
                    vo.setNcye2(ufd);

                    //本月期初
                    ufd = VoUtils.getDZFDouble(vo.getQcye2());
                    ufd = ufd.add(VoUtils.getDZFDouble(fvo.getLastmqcjf())).add(VoUtils.getDZFDouble(fvo.getLastmqcdf()));//只有一方有值
                    vo.setQcye2(ufd);
                }
            }

            if (is1) {
                vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
            } else {
                vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
            }
        }
        return vo;
    }

    private ZcFzBVO getZCFZBVO1(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, Map<String, YntCpaccountVO> mapc, String... kms) {
        List<FseJyeVO> ls = null;
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;

        StringBuffer appkms = new StringBuffer();
        if (is1) {
            appkms = new StringBuffer(vo.getZcconkms() == null ? "" : vo.getZcconkms());
        } else {
            appkms = new StringBuffer(vo.getFzconkms() == null ? "" : vo.getFzconkms());
        }
        for (int i = 0; i < len; i++) {
            ls = getData1(map, kms[i], mapc);
            appkms.append(kms[i] + ",");
            if (ls == null) continue;
            for (FseJyeVO fvo : ls) {
                if (is1) {//资产
                    ufd = VoUtils.getDZFDouble(vo.getQmye1());
                    DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qmjf.doubleValue() < 0) {
                            qmjf = DZFDouble.ZERO_DBL;
                        }
                        if (qmdf.doubleValue() < 0) {
                            qmdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    if ("借".equals(fvo.getFx()) && qmjf.doubleValue() > 0) {
                        ufd = ufd.add(qmjf);
                    } else if (qmdf.doubleValue() < 0) {
                        ufd = ufd.sub(qmdf);
                    }
                    vo.setQmye1(ufd);
                    //
                    ufd = VoUtils.getDZFDouble(vo.getNcye1());
                    DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qcjf.doubleValue() < 0) {
                            qcjf = DZFDouble.ZERO_DBL;
                        }
                        if (qcdf.doubleValue() < 0) {
                            qcdf = DZFDouble.ZERO_DBL;
                        }
                    }
                    if ("借".equals(fvo.getFx()) && qcjf.doubleValue() > 0) {
                        ufd = ufd.add(qcjf);
                    } else if (qcdf.doubleValue() < 0) {
                        ufd = ufd.sub(qcdf);
                    }
                    vo.setNcye1(ufd);

                    //往来分析不计算本月期初
                } else {//负债
                    ufd = VoUtils.getDZFDouble(vo.getQmye2());
                    DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qmjf.doubleValue() < 0) {
                            qmjf = qmjf.multiply(-1);
                        }
                        if (qmdf.doubleValue() < 0) {
                            qmdf = qmdf.multiply(-1);
                        }
                    }
                    if ("贷".equals(fvo.getFx()) && qmdf.doubleValue() > 0) {
                        ufd = ufd.add(qmdf);
                    } else if (qmjf.doubleValue() < 0) {
                        ufd = ufd.sub(qmjf);
                    }
                    vo.setQmye2(ufd);
                    //
                    ufd = VoUtils.getDZFDouble(vo.getNcye2());
                    DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
                    if (fvo.getKmbm().equals("1103")) {
                        if (qcjf.doubleValue() < 0) {
                            qcjf = qcjf.multiply(-1);
                        }
                        if (qcdf.doubleValue() < 0) {
                            qcdf = qcdf.multiply(-1);
                        }
                    }
                    if ("贷".equals(fvo.getFx()) && qcdf.doubleValue() > 0) {
                        ufd = ufd.add(qcdf);
                    } else if (qcjf.doubleValue() < 0) {
                        ufd = ufd.sub(qcjf);
                    }
                    vo.setNcye2(ufd);

                    //往来分析不计算本月期初
                }
            }

            if (is1) {
                vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
            } else {
                vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
            }
        }
        return vo;
    }

    private List<FseJyeVO> getData1(Map<String, FseJyeVO> map, String km, Map<String, YntCpaccountVO> mapc) {
        List<FseJyeVO> list = new ArrayList<FseJyeVO>();
        for (FseJyeVO fsejyevo : map.values()) {
//			String kmbm = fsejyevo.getKmbm();
//			if(kmbm.startsWith(km)){
//				YntCpaccountVO xs = mapc.get(kmbm);
//				if(xs.getIsleaf() != null && xs.getIsleaf().booleanValue()){
//					list.add(fsejyevo);
//				}
//			}

            String kmbm = null;
            if (fsejyevo.getKmbm().indexOf("_") > 0) {
                kmbm = fsejyevo.getKmbm().split("_")[0];
            } else {
                kmbm = fsejyevo.getKmbm();
            }
            if (kmbm.startsWith(km)) {
                YntCpaccountVO xs = mapc.get(kmbm);
                if (xs.getIsleaf() != null && xs.getIsleaf().booleanValue()) {
                    if (xs.getIsfzhs().indexOf("1") >= 0 && fsejyevo.getKmbm().indexOf("_") >= 0) {//包含辅助项目
                        list.add(fsejyevo);
                    } else if (xs.getIsfzhs().indexOf("1") < 0) {//如果不包含辅助项目处理
                        list.add(fsejyevo);
                    }
                }
            }
        }
        return list;
    }

    private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km) {
        List<FseJyeVO> list = new ArrayList<FseJyeVO>();
        for (FseJyeVO fsejyevo : map.values()) {
            if (fsejyevo.getKmbm().equals(km)) {//已经合计过了，不要用startwidh
                list.add(fsejyevo);
                break;
            }
        }
        return list;
    }


}
