package com.dzf.zxkj.report.print.cwbb;

import com.dzf.zxkj.common.model.ColumnCellAttr;

public class YyFpPdfField {

    public static ColumnCellAttr[] getColumnCellList(){
        ColumnCellAttr[] columnCellList = new ColumnCellAttr[6];
        columnCellList[0] = new ColumnCellAttr("项目","xm1", 4);
        columnCellList[1] = new ColumnCellAttr("行数","hc1", 1);
        columnCellList[2] = new ColumnCellAttr("金额","je1", 1);
        columnCellList[3] = new ColumnCellAttr("项目","xm2", 4);
        columnCellList[4] = new ColumnCellAttr("行数","hc2", 1);
        columnCellList[5] = new ColumnCellAttr("金额","je2", 1);
        return columnCellList;
    }

}
