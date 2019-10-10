package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class KmMxZVO {
    private String pk_tzpz_h;
    private String otsubject;
    private Integer sx;
    public String titlePeriod;
    public String gs;
    public String rq;
    public String pzh;
    public String zy;
    public DZFDouble jf;
    public DZFDouble df;
    public String fx;
    private Integer level;
    private String day;
    public DZFDouble ye;
    public String kmbm;
    private String pk_accsubj;
    public String isPaging;
    public String km;
    private String bz;
    public String pk_corp;
    public String pzpk;
    private String fzhsx1;
    private String fzhsx2;
    private String fzhsx3;
    private String fzhsx4;
    private String fzhsx5;
    private String fzhsx6;
    private String fzhsx7;
    private String fzhsx8;
    private String fzhsx9;
    private String fzhsx10;
    public DZFDouble ybye;
    public DZFDouble ybjf;
    public DZFDouble ybdf;
    public DZFBoolean isleaf;
    private String pk_tzpz_b;
    private String kmfullname;
    private String hl;
    private DZFBoolean bqc;
    private String comparecode;
    private String fzlbcode;
    private DZFBoolean bsyszy;
    private Integer rowno;
    // 币种
    private String pk_currency;
    private String currency;
}
