package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class NumMnyDetailVO {
    private DZFDouble nnumber;
    private DZFDouble dfmny;
    private DZFDouble jfmny;

    //辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
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

    private Integer accountlevel;
    private String kmmc;
    private String kmbm;
    private String spmc;
    private String qj;
    private String opdate;
    private String pzh;
    private String pzhhid;
    private String zy;
    private DZFDouble nnum;
    private DZFDouble nprice;
    private DZFDouble nmny;
    private DZFDouble ndnum;
    private DZFDouble ndprice;
    private DZFDouble ndmny;
    private String dir;
    private DZFDouble nynum;
    private DZFDouble nyprice;
    private DZFDouble nymny;
    private String pk_inventory;
    private String pk_subject;
    private String id;
    private String checked;//是否被选中
    private DZFDouble npzprice;//凭证单价
    //打印时  标题区间
    private String titlePeriod;
    private String gs;  //公司
    private String jldw;

    private String pk_corp;//记录公司主键
    private Integer vdirect;//方向
    private DZFDouble xsprice;//销售单价 ，平均单价。
    private DZFBoolean bsyszy;//是否系统摘要

    private DZFDouble zgxsnum;//暂估销售数量(用于成本结转暂估单价取数计算)
    private DZFDouble zgxsmny;//暂估销售金额(用于成本结转暂估单价取数计算)

    private DZFDouble zgcgnum;//暂估采购数量(用于成本结转暂估单价取数计算)
    private DZFDouble zgcgmny;//暂估采购金额(用于成本结转暂估单价取数计算)
}
