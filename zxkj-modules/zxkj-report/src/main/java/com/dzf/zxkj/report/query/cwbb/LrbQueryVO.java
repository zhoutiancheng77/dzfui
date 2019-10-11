package com.dzf.zxkj.report.query.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class LrbQueryVO {
    private String begindate;
    private String corpIds;
    private DZFBoolean ishasjz;
    private String xmlbid;
    private String xmmcid;
    private String pk_corp;
    private String qjq;
    private String qjz;
    private DZFDate enddate;
    private DZFDate begindate1;
    private String rptsource;
}
