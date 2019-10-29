package com.dzf.zxkj.report.entity;

import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

@Data
public class ReportExcelExportVO {
    private String data;
    private String corpName;
    private String period;
    private String columncellattrvos;
    private DZFDate beginDate;
    private DZFDate endDate;
    private String titleName;
}
