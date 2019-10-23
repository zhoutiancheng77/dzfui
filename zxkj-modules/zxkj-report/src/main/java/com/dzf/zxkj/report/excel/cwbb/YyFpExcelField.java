package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.YyFpVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.Date;

/**
 * 盈余分配表
 *
 * @author zhangj
 */
public class YyFpExcelField implements IExceport<YyFpVO> {

    private YyFpVO[] ywhdvos = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    private Fieldelement[] fields = new Fieldelement[]{
            new Fieldelement("xm1", "项目", false, 0, false),
            new Fieldelement("hc1", "行次", false, 0, false),
            new Fieldelement("je1", "金额", true, 2, true),
            new Fieldelement("xm2", "项目", false, 0, false),
            new Fieldelement("hc2", "行次", false, 0, false),
            new Fieldelement("je2", "金额", true, 2, true),
    };

    @Override
    public String getExcelport2007Name() {
        return "盈余分配表-" + ReportUtil.formatQj(qj) + ".xlsx";
    }

    @Override
    public String getExcelport2003Name() {
        return "盈余分配表-" + ReportUtil.formatQj(qj) + ".xls";
    }

    @Override
    public String getExceportHeadName() {
        return "盈余分配表";
    }

    @Override
    public String getSheetName() {
        return "盈余分配表";
    }

    @Override
    public YyFpVO[] getData() {
        return ywhdvos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        return fields;
    }

    public void setYwhdvos(YyFpVO[] ywhdvos) {
        this.ywhdvos = ywhdvos;
    }

    @Override
    public String getQj() {
        return qj;
    }

    @Override
    public String getCreateSheetDate() {
        return now;
    }

    @Override
    public String getCreateor() {
        return creator;
    }

    public void setQj(String qj) {
        this.qj = qj;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getCorpName() {
        return corpname;
    }

    public void setCorpName(String corpname) {
        this.corpname = corpname;
    }

    @Override
    public boolean[] isShowTitDetail() {
        return new boolean[]{true, true, false};
    }

}
