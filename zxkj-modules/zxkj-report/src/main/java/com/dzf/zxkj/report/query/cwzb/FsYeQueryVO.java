package com.dzf.zxkj.report.query.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

import java.util.List;

@Data
public class FsYeQueryVO {
    private Integer cjq;
    private Integer  cjz;
    private String begindate;
    private DZFDate enddate;
    private String kms_first;
    private String kms_last;
    private String corpIds;
    private DZFBoolean xswyewfs;
    private DZFBoolean ishasjz;
    private DZFBoolean sfzxm;
    private String kmsx;
    private String pk_currency;
    private String currency;
    private String ishowfs;
    private String xswyewfs_bn;
    private String sort;
    private String order;
    private Integer direction;
    private String period;
    private String pk_corp;
    private String qjq;
    private String qjz;
    private DZFDate begindate1;
    private DZFBoolean ishassh;
    private DZFBoolean btotalyear;

    private String rptsource;
    private List<String> firstlevelkms;
}
