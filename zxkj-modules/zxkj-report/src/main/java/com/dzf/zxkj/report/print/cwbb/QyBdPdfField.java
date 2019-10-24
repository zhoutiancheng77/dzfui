package com.dzf.zxkj.report.print.cwbb;

import com.dzf.zxkj.common.model.ColumnCellAttr;

public class QyBdPdfField {

    public static String name = "权益变动表";

    public static ColumnCellAttr[] getColumnCellList(){
        ColumnCellAttr[] columnCellList = new ColumnCellAttr[4];
        columnCellList[0] = new ColumnCellAttr("项目","xm", 6);
        columnCellList[1] = new ColumnCellAttr("行数","hc", 1);
        columnCellList[2] = new ColumnCellAttr("上年数","sq_je", 1);
        columnCellList[3] = new ColumnCellAttr("本年数","bn_je", 1);
        return columnCellList;
    }

}
