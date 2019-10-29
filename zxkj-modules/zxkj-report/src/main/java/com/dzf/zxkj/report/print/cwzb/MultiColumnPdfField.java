package com.dzf.zxkj.report.print.cwzb;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.KmReportDatagridColumn;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class MultiColumnPdfField {
    private String data;
    private String columns;
    private String columns1;
    private String columns2;
    private String title;
    private String period;
    private String corpName;

    public List<String> getHeadList(){
        List<String> headslist=new ArrayList<String>();
        headslist.add("日期");
        headslist.add("凭证号");
        headslist.add("摘要");
        headslist.add("借方");
        headslist.add("贷方");
        headslist.add("方向");
        headslist.add("余额");
        KmReportDatagridColumn[] kmReportDatagridColumns1 = getColumns1();
        if(kmReportDatagridColumns1.length > 1){
            headslist.add(kmReportDatagridColumns1[1].getTitle());
        }

        KmReportDatagridColumn[] kmReportDatagridColumns2 = getColumns2();
        for(KmReportDatagridColumn kmReportDatagridColumn: kmReportDatagridColumns2){
            headslist.add(kmReportDatagridColumn.getTitle());
        }
        return headslist;
    }

    public List<String> getFieldList(){
        List<String> fieldslist=new ArrayList<String>();
        fieldslist.add("rq");
        fieldslist.add("pzh");
        fieldslist.add("zy");
        fieldslist.add("jf");
        fieldslist.add("df");
        fieldslist.add("fx");
        fieldslist.add("ye");
        KmReportDatagridColumn[] kmReportDatagridColumns2 = getColumns2();
        for(KmReportDatagridColumn kmReportDatagridColumn: kmReportDatagridColumns2){
            fieldslist.add(kmReportDatagridColumn.getField());
        }
        return fieldslist;
    }

    public int[] getWidths(){
        int[] widths =new  int[]{4,2,5,3,3,1,3};
        KmReportDatagridColumn[] kmReportDatagridColumns2 = getColumns2();
        for(KmReportDatagridColumn kmReportDatagridColumn: kmReportDatagridColumns2){
            widths = ArrayUtils.addAll(widths, new int[] {3});
        }
        return widths;
    }

    public KmReportDatagridColumn[] getColumns1(){
        if(!StringUtil.isEmptyWithTrim(columns1)){
            return JsonUtils.deserialize(columns1, KmReportDatagridColumn[].class);
        }else{
            return new KmReportDatagridColumn[0];
        }
    }

    public KmReportDatagridColumn[] getColumns2(){
        if(!StringUtil.isEmptyWithTrim(columns2)){
            return JsonUtils.deserialize(columns2, KmReportDatagridColumn[].class);
        }else{
            return new KmReportDatagridColumn[0];
        }
    }
}
