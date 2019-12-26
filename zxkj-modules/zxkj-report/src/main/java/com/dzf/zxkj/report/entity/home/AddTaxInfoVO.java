package com.dzf.zxkj.report.entity.home;

import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class AddTaxInfoVO {
    private String period;
    // 增值税
    private DZFDouble taxMny;
    // 月税负
    private DZFDouble monthBurden;
    // 累计税负
    private DZFDouble accumulatedBurden;
}
