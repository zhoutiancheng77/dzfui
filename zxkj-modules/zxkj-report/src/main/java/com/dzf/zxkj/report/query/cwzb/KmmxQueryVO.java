package com.dzf.zxkj.report.query.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class KmmxQueryVO {
    private String currkmbm;
    private Integer cjq;
    private Integer cjz;
    private String begindate;
    private DZFDate enddate;
    private String kms_first;
    private String kms_last;
    private String corpIds;
    private DZFBoolean ishasjz;
    private String kmsx;
    private String pk_currency;
    private String isleaf;
    private String sfzxm;
    private String isqry;
    private DZFBoolean xswyewfs;
    private String ishowfs;
    private String page;
    private String rows;
    private String sort;
    private String order;
    private DZFBoolean btotalyear;
    private DZFDate begindate1;
    private String pk_corp;
}
