package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportUtil {

    /**
     * 数组转map
     * @param cvos1
     * @return
     */
    public static Map<String, YntCpaccountVO> conVertMap(YntCpaccountVO[] cvos1){
        if(cvos1 == null || cvos1.length == 0){
            return null;
        }
        Map<String,YntCpaccountVO> map = new HashMap<String,YntCpaccountVO>();
        for(YntCpaccountVO c : cvos1){
            String code = c.getAccountcode();
            map.put(code, c);
        }
        return map;
    }
    /**
     * 获取开始日期 到 结束日期的区间数据
     *
     * @param begindate
     * @param enddate
     * @return
     */
    public static List<String> getPeriods(DZFDate begindate, DZFDate enddate) {
        List<String> vec_periods = new ArrayList<String>();
        int nb = begindate.getYear() * 12 + begindate.getMonth();
        int ne = enddate.getYear() * 12 + enddate.getMonth();
        int year = 0;
        int month = 0;
        for (int i = nb; i <= ne; i++) {
            month = i % 12;
            year = i / 12;
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
        }
        return vec_periods;
    }


    /**
     * 数组转map
     * @param cvos1
     * @return
     */
    public static Map<String, FseJyeVO> conVertMapFs(List<FseJyeVO> cvos1){
        if(cvos1 == null || cvos1.size() == 0){
            return null;
        }
        Map<String,FseJyeVO> map = new HashMap<String,FseJyeVO>();
        for(FseJyeVO c : cvos1){
            map.put(c.getKmbm(), c);
        }
        return map;
    }
    public static List<String> getPeriodsByPeriod(String period_beg, String period_end) {

        DZFDate begindate = DateUtils.getPeriodStartDate(period_beg);
        DZFDate enddate = DateUtils.getPeriodEndDate(period_end);
        return getPeriods(begindate, enddate);
    }
}
