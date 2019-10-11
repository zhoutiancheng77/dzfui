package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class XjllquarterlyVO {
    private String xm;
    private String hc;
    private String hc_id;//(不允许修改)
    private DZFDouble bnlj;// 本年累计
    private DZFDouble bf_bnlj;// 上年本年累计数
    private DZFDouble jd1;// 第一季度
    private DZFDouble jd2;// 第二季度
    private DZFDouble jd3;// 第三季度
    private DZFDouble jd4;// 第四季度

    //去年同期季度数据
    private DZFDouble jd1_last;
    private DZFDouble jd2_last;
    private DZFDouble jd3_last;
    private DZFDouble jd4_last;//

    // 打印时 标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    private String period;// 期间

    private String pk_corp;

    @JsonIgnore
    public DZFDouble getJd(int i){
        switch (i){
            case 1:
                return this.jd1;
            case 2:
                return this.jd2;
            case 3:
                return this.jd3;
            case 4:
                return this.jd4;
            default:
                return DZFDouble.ZERO_DBL;
        }
    }

    public void setJd(int i, DZFDouble v){
        switch (i){
            case 1:
                this.jd1 = v;
                break;
            case 2:
                this.jd2 = v;
                break;
            case 3:
                this.jd3 = v;
                break;
            case 4:
                this.jd4 = v;
                break;
        }
    }
}
