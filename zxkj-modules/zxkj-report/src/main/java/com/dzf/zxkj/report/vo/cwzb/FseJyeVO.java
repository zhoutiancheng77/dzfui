package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class FseJyeVO {
    // 打印时 标题区间
    private String titlePeriod;

    private String gs;

    /**
     * 币种pk
     */
    private String pk_currency;
    /**
     * 币种名称
     */
    private String currency;
    private String pk_km;
    private String pk_km_parent;//父科目id
    private String kmlb;
    private String kmbm;
    private String kmmc;
    private String fx;
    private DZFDouble ncjf;
    private DZFDouble ncdf;//年初借方
    private DZFDouble qcjf;//年初贷方
    private DZFDouble qcdf;
    private DZFDouble fsjf;
    private DZFDouble fsdf;
    private DZFDouble qmjf;
    private DZFDouble qmdf;
    //----------原币
    private DZFDouble ybncjf;
    private DZFDouble ybncdf;//年初借方
    private DZFDouble ybqcjf;//年初贷方
    private DZFDouble ybqcdf;
    private DZFDouble ybfsjf;
    private DZFDouble ybfsdf;
    private DZFDouble ybqmjf;
    private DZFDouble ybqmdf;
    private Integer direction;
    private Integer bs;
    private String rq;// 期间

    private String bsh;// 凭证数
    private String bills;// 附单据数
    private String pzqj;// 凭证期间

    private DZFDouble endfsjf;
    private DZFDouble endfsdf;

    private DZFDouble jftotal;// 本年累计发生借方
    private DZFDouble dftotal;// 本年累计发生贷方

    //-----------原币
    private DZFDouble ybendfsjf;
    private DZFDouble ybendfsdf;

    private DZFDouble ybjftotal;// 本年累计发生借方
    private DZFDouble ybdftotal;// 本年累计发生贷方

    // 当前区间最后一个月的期初，发生
    private DZFDouble lastmqcjf;
    private DZFDouble lastmqcdf;
    private DZFDouble lastmfsjf;
    private DZFDouble lastmfsdf;

    // 科目层次
    private Integer alevel;
    private String endrq;

    private String fzlbcode;//辅助类别编码（1_2_3等）
}
