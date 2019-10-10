package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class FzKmmxVO {
    private String rq;
    private String pzh;
    private String vmemo;
    private DZFDouble ybjf;// 原币借方
    private DZFDouble jf;
    private DZFDouble ybdf;// 原币贷方
    private DZFDouble df;
    private String fx;
    private DZFDouble ybye;// 原币余额
    private DZFDouble ye;
    private DZFDouble jfnum;// 借方数量
    private DZFDouble dfnum;// 贷方数量
    private DZFDouble yenum;// 数量
    private String fzxm;
    private String fzxmname;
    private String kmmx;
    private String kmmc;
    private String zy;
    private String pk_accsubj;
    private String fzcode;// 辅助项目编码
    private String fzname;// 辅助项目名称
    private String kmcode;// 科目编码
    private String kmname;// 科目名称
    private String fzlb;//辅助类别
    // 树状显示
    private String id;
    private String text;
    private String code;
    private String state;// 是否展开
    private DZFBoolean iskmid;// 是否是科目
    private String bdefault;//是否默认选中
    private String checked;//是否被选中
    public String bz;

    private String sourcebilltype;// 来源类型
}
