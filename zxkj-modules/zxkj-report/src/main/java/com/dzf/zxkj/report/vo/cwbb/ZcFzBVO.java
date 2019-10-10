package com.dzf.zxkj.report.vo.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class ZcFzBVO {
    //打印时  标题显示的区间区间
    public String titlePeriod;

    public String period;//期间
    // 公司
    public String gs;
    // 资产
    public String zc;

    // 行次1
    public String hc1;

    // 期末余额1
    public DZFDouble qmye1;

    // 年初余额
    public DZFDouble ncye1;

    // 负债和所有者权益(或股东权益）
    public String fzhsyzqy;

    // 行次
    public String hc2;

    // 期末余额
    public DZFDouble qmye2;

    // 年初余额
    public DZFDouble ncye2;

    public String pk_corp;

    public DZFBoolean isseven;

    private String zcconkms;

    private String fzconkms;

    //期初
    public DZFDouble qcye1;
    public DZFDouble qcye2;

    public Integer colspan;//合并的行数

    public String hc1_id;//系统行次(不可随意更改)

    public String hc2_id;//系统行次(不可随意更改)
}
