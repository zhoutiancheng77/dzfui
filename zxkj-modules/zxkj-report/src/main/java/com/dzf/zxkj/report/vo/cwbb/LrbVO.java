package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.BeanHelper;
import lombok.Data;

@Data
public class LrbVO {
    //项目
    public String xm ;

    //行数
    public String hs ;

    //本年累计金额
    public DZFDouble bnljje ;

    //本月金额
    public DZFDouble byje ;

    private Integer level;

    //打印时  标题显示的区间区间
    public String titlePeriod;
    // 公司
    public String gs;

    public String period;//期间

    private String vconkms;


    public String pk_corp;

    public DZFBoolean isseven;

    public String kmfa;//当前公司所属科目方案 值参照 DzfUtil类

    //上年同期累计数
    private DZFDouble lastyear_bnljje;
    //项目
    private String xm2 ;

    //行数
    private String hs2 ;

    //本年累计金额
    private DZFDouble bnljje2 ;

    //本月金额
    private DZFDouble byje2 ;

    private String vconkms2;

    //行数id
    public String hs_id;

    //执行公式
    private String formula;

    public void setAttributeValue(String name, Object value) {
        BeanHelper.setProperty(this, name, value);
    }

    public Object getAttributeValue(String name) {

        return BeanHelper.getProperty(this, name);
    }
}
