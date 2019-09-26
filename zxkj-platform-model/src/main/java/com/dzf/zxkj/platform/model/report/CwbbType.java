package com.dzf.zxkj.platform.model.report;

public enum CwbbType {
    EMPTY("空"),
    ZCFZB("资产负债表"),
    LRB("利润表"),
    XJLLB("现金流量表");

    private String name;

    public String getName() {
        return name;
    }

    CwbbType(String name) {
        this.name = name;
    }
}
