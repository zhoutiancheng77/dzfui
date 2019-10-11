package com.dzf.zxkj.report.query.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class XjllQueryVO {
    private String qjq;
    private String qjz;
    private String startYear;
    private String startMonth;
    private String corpIds;
    private String pk_corp;
    private DZFBoolean ishasjz;
    private DZFDate begindate1;
}
