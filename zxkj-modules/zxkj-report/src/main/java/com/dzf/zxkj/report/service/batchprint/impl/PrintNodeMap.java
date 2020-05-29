package com.dzf.zxkj.report.service.batchprint.impl;

import java.util.HashMap;
import java.util.Map;

public class PrintNodeMap {

    public static Map<String,String> nodemap = new HashMap<>();

    static {
        nodemap.put("voucher","凭证");
        nodemap.put("mly","目录页");
        nodemap.put("gzb","工资表");
        nodemap.put("kmzz","科目总账");
        nodemap.put("kmmx","科目明细账");
        nodemap.put("xjrj","现金银行日记账");
        nodemap.put("fsye","发生额及余额表");
        nodemap.put("crk","出入库单");
        nodemap.put("cbft","成本分摊表");
        nodemap.put("zjmx","折旧明细表");
        nodemap.put("zcfz","资产负债表");
        nodemap.put("lrb","利润表");
        nodemap.put("pzfp","凭证封皮");
        nodemap.put("mxzfp","总账明细账封皮");
    }
}
