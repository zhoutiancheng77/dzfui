package com.dzf.zxkj.platform.excel.zcgl;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;

import java.util.Date;

public class ZchzExcelField implements IExceport<AssetDepreciaTionVO> {
    private AssetDepreciaTionVO[] assdetivos = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    private Fieldelement[] fields = new Fieldelement[]{
            new Fieldelement("assetproperty", "资产属性",false,0,false),
            new Fieldelement("catename", "资产类别",false,0,false),
            new Fieldelement("assetmny", "资产原值",true,2,true),
            new Fieldelement("originalvalue", "本期折旧额",true,2,true),
            new Fieldelement("depreciationmny", "累计折旧额",true,2,true),
            new Fieldelement("assetnetmny", "资产净值",true,2,true)
    };

    @Override
    public String getExcelport2007Name() {
        return "折旧汇总表_"+now+".xlsx";
    }

    @Override
    public String getExcelport2003Name() {
        return "折旧汇总表_"+now+".xls";
    }

    @Override
    public String getExceportHeadName() {
        return "折旧汇总表";
    }

    @Override
    public String getSheetName() {
        return "折旧汇总表";
    }

    @Override
    public AssetDepreciaTionVO[] getData() {
        return assdetivos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        return fields;
    }

    public void setAssdetivos(AssetDepreciaTionVO[] assdetivos) {
        this.assdetivos = assdetivos;
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

    public void setCorpName(String corpname){
        this.corpname = corpname;
    }

    @Override
    public boolean[] isShowTitDetail() {
        return new boolean[]{true,true,false};
    }
}
