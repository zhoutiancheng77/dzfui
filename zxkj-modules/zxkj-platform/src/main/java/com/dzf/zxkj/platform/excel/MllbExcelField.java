package com.dzf.zxkj.platform.excel;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.MllDetailVO;

import java.util.Date;

public class MllbExcelField implements IExceport<MllDetailVO> {

    public MllbExcelField(int numPrecision, int pricePrecision, int chcbjzfs) {
        this.numPrecision = numPrecision;
        this.pricePrecision = pricePrecision;
        this.chcbjzfs = chcbjzfs;
    }

    private int chcbjzfs;

    private MllDetailVO[] MllDetailVos = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    private Fieldelement[] fields = null;

    private int numPrecision;//数量精度
    private int pricePrecision;//单价精度

    @Override
    public String getExcelport2007Name() {
        return "毛利率明细表_"+now+".xlsx";
    }

    @Override
    public String getExcelport2003Name() {
        return "毛利率明细表_"+now+".xls";
    }

    @Override
    public String getExceportHeadName() {
        return "毛利率明细表";
    }

    @Override
    public String getSheetName() {
        return "毛利率明细表";
    }

    @Override
    public MllDetailVO[] getData() {
        return MllDetailVos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        return fields;
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

    @Override
    public String getCorpName() {
        return corpname;
    }


    @Override
    public boolean[] isShowTitDetail() {
        return new boolean[]{true,true,false};
    }

    public void setMllDetailVos(MllDetailVO[] mllDetailVos) {
        MllDetailVos = mllDetailVos;
    }

    public Fieldelement[] getFields(){
        if(chcbjzfs == 1){
            return new Fieldelement[]{
                    new Fieldelement("code", "存货编码",false,0,false),
                    new Fieldelement("name", "存货名称",false,0,false),
                    new Fieldelement("spec", "规格（型号）",false,0,false),
                    new Fieldelement("unit", "计量单位",false,0,false),
                    new Fieldelement("vname", "存货类别名称",false,0,false),
                    new Fieldelement("cksl", "出库数量",true,numPrecision,false),
                    new Fieldelement("ckdj", "出库单价",true,pricePrecision,false),
                    new Fieldelement("xsdj", "销售单价",true,pricePrecision,false),
                    new Fieldelement("mll", "毛利率",true,0,false,30,true)
            };
        }else{
            return new Fieldelement[]{
                    new Fieldelement("code", "存货编码",false,0,false),
                    new Fieldelement("name", "存货名称",false,0,false),
                    new Fieldelement("spec", "规格（型号）",false,0,false),
                    new Fieldelement("unit", "计量单位",false,0,false),
                    new Fieldelement("cksl", "出库数量",true,numPrecision,false),
                    new Fieldelement("ckdj", "出库单价",true,pricePrecision,false),
                    new Fieldelement("xsdj", "销售单价",true,pricePrecision,false),
                    new Fieldelement("mll", "毛利率",true,0,false,30,true)
            };
        }
    }

    public void setQj(String qj) {
        this.qj = qj;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public void setFields(Fieldelement[] fields) {
        this.fields = fields;
    }

    public void setNumPrecision(int numPrecision) {
        this.numPrecision = numPrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }
}
