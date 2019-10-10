package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class KmZzVO {
    // 期间
    private String period;

    // 打印时 标题显示的区间区间
    private String titlePeriod;

    private String pk_tzpz_h;

    private String gs;

    // 摘要
    private String zy;

    // 借方
    private DZFDouble jf;

    // 贷方
    private DZFDouble df;

    // 方向
    private String fx;

    // 余额
    private DZFDouble ye;
    private String kmbm;

    // 科目
    private String km;
    private String pk_accsubj;

    // 原币借方
    private DZFDouble ybjf;

    // 原币贷方
    private DZFDouble ybdf;

    private Integer rowspan;//合并的行数

    private String day;


    // 币种
    public String pk_currency;
    private String currency;
    // 汇率
    public DZFDouble nrate;

    private Integer level;

    public String isPaging; // 是否分页 Y/N

    //存货ID
    private String pk_inventory;

    //	存货数量
    private DZFDouble nnumber;

    //	存货单价
    private DZFDouble nprice;


    private DZFDouble ybye;//原币余额

    private String fzhsx1;
    private String fzhsx2;
    private String fzhsx3;
    private String fzhsx4;
    private String fzhsx5;
    private String fzhsx6;
    private String fzhsx7;
    private String fzhsx8;
    private String fzhsx9;
    private String fzhsx10;

    private DZFDouble jfnnumber;//借方数量
    private DZFDouble dfnnumber;//贷方数量

    private String kmfullname;//科目全称
}
