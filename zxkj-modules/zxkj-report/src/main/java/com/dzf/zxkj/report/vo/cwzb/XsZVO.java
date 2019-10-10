package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class XsZVO {
    private String pzz;

    private String qj;

    private String year;

    // 日期
    private String rq;

    // 凭证号
    private String pzh;

    // 摘要
    private String zy;

    // 科目编码
    private String kmbm;

    // 科目名称
    private String kmmc;

    private String fullkmmc;// 科目全称

    // 方向
    private String fx;

    // 金额
    private DZFDouble je;

    //
    private DZFDouble jfmny;

    private DZFDouble dfmny;

    // 打印时 标题显示的区间区间
    private String titlePeriod;
    // 公司
    private String gs;

    // 主表主键
    private String pk_tzpz_h;

    private DZFDouble hl;

    private DZFDouble ybjf;

    private DZFDouble ybdf;

    private String pk_corp;

    private String bz;
}
