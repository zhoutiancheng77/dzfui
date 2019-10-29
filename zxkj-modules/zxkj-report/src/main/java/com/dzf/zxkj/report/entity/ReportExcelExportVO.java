package com.dzf.zxkj.report.entity;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import lombok.Data;

import java.util.List;

@Data
public class ReportExcelExportVO<T> {
    private List<T> data;
    private String corpName;
    private String period;
    private ColumnCellAttr[] columncellattrvos;
    private DZFDate beginDate;
    private DZFDate endDate;
    private String titleName;
}
