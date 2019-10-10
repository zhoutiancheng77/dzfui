package com.dzf.zxkj.report.vo.cwzb;

import lombok.Data;

import java.util.List;
@Data
public class AgeReportResultVO {
    private Object result;
    private List<String> periods;
    private List<String> period_names;
}
