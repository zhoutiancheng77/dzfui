package com.dzf.zxkj.platform.model.end_process.tax_calculator;

import com.dzf.zxkj.platform.model.end_process.tax_calculator.export.ExportTable;

public class ExportData {
    private String corpName;
    private String period;
    private ExportTable addTax;
    private ExportTable surtax;
    private ExportTable incomeTax;

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public ExportTable getAddTax() {
        return addTax;
    }

    public void setAddTax(ExportTable addTax) {
        this.addTax = addTax;
    }

    public ExportTable getSurtax() {
        return surtax;
    }

    public void setSurtax(ExportTable surtax) {
        this.surtax = surtax;
    }

    public ExportTable getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(ExportTable incomeTax) {
        this.incomeTax = incomeTax;
    }

}
