package com.dzf.zxkj.common.cache;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class CorpBean {
    private static final long serialVersionUID = -2549348901898203434L;
    private DZFDate begindate;// 建账日期
    public String corptype;// 科目方案ID
    public DZFDate icbegindate;// 库存启用日期
    public DZFDate createdate;// 录入日期
    public DZFBoolean ishasaccount;// 是否已建帐
    public DZFBoolean isseal;// 是否已停用
    public DZFBoolean isworkingunit;//是否成本结转
    public String pk_corp;//主键
    public String unitname;// 公司名称
    public String unitshortname;//公司名称简称
}
