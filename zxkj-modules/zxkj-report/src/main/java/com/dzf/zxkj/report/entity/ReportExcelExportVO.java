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
    private String list;// 当前页面数据
    private String isPaging;// 是否分页
    private String export_all;// 是否导出全部
    private String fzlb_name;//辅助类别名称
    private String excelsel;//导出选择
    private String xmmcid;
    private String columnOrder;
    private  String currjd;//
    private String titleperiod;//查询期间段
    private String areaType;
    private String qjlx;
}
