package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FzYeVO {
    //打印时的抬头标签
    public String titlePeriod;
    public String gs;

    //期初余额的期间，给明细账取期初时用。余额表结果本身不需要期间。
    private String rq; //日期
    private String period;

    //当前行科目，当前行没有科目时显示为空（如快速切换点选辅助项目不点下面的科目(明细账才有快速切换)，或者参数不显示科目）
    private String pk_acc;
    public String accCode;
    private String accName;
    private Integer accLevel; //科目层级

    //当前科目方向（明细行取明细科目方向，汇总行取上级科目方向，辅助项汇总行(无科目级)方向取空）
    private Integer accDirection;

    //暂不支持按币种和原币金额展示
    private String pk_currency;
    @JsonProperty("fzlbid")
    private String pk_fzlb;//辅助类别
    //private String currCode;
    //private String currName;

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

    private String fzhsx1Code;
    private String fzhsx2Code;
    private String fzhsx3Code;
    private String fzhsx4Code;
    private String fzhsx5Code;
    private String fzhsx6Code;
    private String fzhsx7Code;
    private String fzhsx8Code;
    private String fzhsx9Code;
    private String fzhsx10Code;

    private String fzhsx1Name;
    private String fzhsx2Name;
    private String fzhsx3Name;
    private String fzhsx4Name;
    private String fzhsx5Name;
    private String fzhsx6Name;
    private String fzhsx7Name;
    private String fzhsx8Name;
    private String fzhsx9Name;
    private String fzhsx10Name;

    //辅助项、科目合并显示列
    public String fzhsxCode;
    public String fzhsxName;

    public DZFDouble qcye;
    public DZFDouble bqfsjf;
    public DZFDouble bqfsdf;
    public DZFDouble bnljjf;
    public DZFDouble bnljdf;
    public DZFDouble qmye;
    public Integer bsjf; //笔数
    public Integer bsdf;

    //余额表最终结果余额按借贷两栏展示
    public DZFDouble qcyejf;
    public DZFDouble qcyedf;
    public DZFDouble qmyejf;
    public DZFDouble qmyedf;


    //---------------------原币数据 -------------
    public DZFDouble ybqcye;
    public DZFDouble ybbqfsjf;
    public DZFDouble ybbqfsdf;
    public DZFDouble ybbnljjf;
    public DZFDouble ybbnljdf;
    public DZFDouble ybqmye;
    public Integer ybbsjf; //笔数
    public Integer ybbsdf;

    //余额表最终结果余额按借贷两栏展示
    public DZFDouble ybqcyejf;
    public DZFDouble ybqcyedf;
    public DZFDouble ybqmyejf;
    public DZFDouble ybqmyedf;
}
