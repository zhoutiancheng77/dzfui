package com.dzf.zxkj.platform.vo;

import com.dzf.zxkj.common.query.ReportPrintParamVO;
import lombok.Data;

@Data
public class QmjzPrintVo extends ReportPrintParamVO {
    private String data;
    private String period;
}
