package com.dzf.zxkj.report.query.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class LrbQuarterlyQueryVO {
    private String begindate;
    private String corpIds;
    private DZFBoolean ishasjz;
    private String sort;
    private String order;
    private String pk_corp;
    private String qjq;
    private String qjz;
    private String rptsource;
    private DZFDate begindate1;
    private DZFDate enddate;
}
