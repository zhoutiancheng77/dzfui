package com.dzf.zxkj.report.query.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class CwgyInfoQueryVO {
    private String begindate;
    private String corpIds;
    private String ishasjz;
    private String qjq;
    private String qjz;
    private DZFBoolean nhasyj;

    public String getQjq() {
        return this.begindate.substring(0, 7);
    }

    public String getQjz() {
        return this.begindate.substring(0, 7);
    }

    private String pk_corp;

    private DZFDate enddate;

    private DZFDate begindate1;
}
