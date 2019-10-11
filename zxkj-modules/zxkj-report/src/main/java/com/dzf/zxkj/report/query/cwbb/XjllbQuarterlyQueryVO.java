package com.dzf.zxkj.report.query.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class XjllbQuarterlyQueryVO {
    private String begindate;
    private String corpIds;
    private String jd;
    private String sort;
    private String order;
    private DZFDate begindate1;
    private String pk_corp;
}
