package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.BeanHelper;
import lombok.Data;

@Data
public class LrbquarterlyVO {
    //项目
    private String xm ;

    //行数
    private String hs ;

    //行数id
    private String hs_id;


    //本年累计(包含期初的本年累计)
    private DZFDouble bnlj;

    private DZFDouble quarterFirst ;//第一季度
    private DZFDouble quarterSecond ;//第二季度
    private DZFDouble quarterThird ;//第三季度
    private DZFDouble quarterFourth ;//第四季度

    private DZFDouble lastquarterFirst ;//上年同期第一季度
    private DZFDouble lastquarterSecond ;//上年同期第二季度
    private DZFDouble lastquarterThird ;//上年同期第三季度
    private DZFDouble lastquarterFourth ;//上年同期第四季度


    //上年同期数
    private DZFDouble sntqs;
    //本年累计金额	 (发生的累计，不包含期初,计算季度数使用)
    private DZFDouble bnljje ;

    //去年累计金额
    private DZFDouble qnljje ;

    //本月金额
    private DZFDouble byje ;

    private Integer level;

    //打印时  标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    private String period;//期间

    private String pk_corp;

    private DZFBoolean isseven;

    public void setAttributeValue(String name, Object value) {
        BeanHelper.setProperty(this, name, value);
    }

    public Object getAttributeValue(String name) {

        return BeanHelper.getProperty(this, name);
    }

}
