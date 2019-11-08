package com.dzf.zxkj.platform.query.zcgl;

import com.dzf.zxkj.common.query.ReportPrintParamVO;
import lombok.Data;

@Data
public class ZczjmxPrintParamVO extends ReportPrintParamVO {
    private String data;
    private String lb;
    private String zcbm ;
    private String zjrq;
    private String zjnx ;
    private String xjtotal;
    private String hjtotal ;
    private String isPaging;
}
