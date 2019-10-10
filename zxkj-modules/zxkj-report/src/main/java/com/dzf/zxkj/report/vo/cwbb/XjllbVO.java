package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class XjllbVO {
    //项目
    private String xm ;

    private String xmid;

    //行次
    private String hc ;
    //编码
    private String hc_id;

    //本期金额
    private DZFDouble bqje ;

    private DZFDouble bqje_last;//上年同期金额(单独查一个期间暂时没，需要再查一次)

    //本年累计金额
    private DZFDouble sqje ;//历史遗留，这名字看着很别扭
    private DZFDouble sqje_last;//上期金额
    //打印时  标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    private String period;//期间

    private DZFBoolean isseven;

    private float rowno;

    private String kmfa;//科目方案

    private String pk_project;

    private DZFBoolean bxjlltotal;//是否是现金流量的合计

    private DZFBoolean bkmqc;//是否科目期初

    private DZFBoolean bkmqm;//是否科目期末

    private String formula;//公式
}
