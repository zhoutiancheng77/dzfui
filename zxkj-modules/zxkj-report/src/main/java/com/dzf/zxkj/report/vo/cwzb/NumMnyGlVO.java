package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.Data;

@Data
public class NumMnyGlVO {
    private String titlePeriod;


    private DZFDouble nnumber;
    private DZFDouble dfmny;
    private DZFDouble jfmny;

    private String period;
    private String gs;
    private String beginqj;
    private String endqj;
    private String kmmc;
    private String spmc;
    private DZFDouble qcnum;
    private DZFDouble qcprice;
    private DZFDouble qcmny;
    private DZFDouble bqjfnum;
    private DZFDouble bqjfmny;
    private DZFDouble bqdfnum;
    private DZFDouble bqdfmny;
    private DZFDouble bnjfnum;
    private DZFDouble bnjfmny;
    private DZFDouble bndfnum;
    private DZFDouble bndfmny;
    private DZFDouble qmnum;
    private DZFDouble qmprice;
    private DZFDouble qmmny;
    private String pk_inventory;
    private String pk_subject;
    private String opdate;

    private String kmbm;
    private String dw;
    private  String  pk_tzpz_h;
    private Integer accountlevel;
    private  String  dir;

    //辅助核算
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

    private String pk_corp;//记录公司主键
}
