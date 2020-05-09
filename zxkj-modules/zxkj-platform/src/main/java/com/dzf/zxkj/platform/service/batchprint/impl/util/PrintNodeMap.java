package com.dzf.zxkj.platform.service.batchprint.impl.util;

import com.dzf.zxkj.common.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class PrintNodeMap {


    public static Map<String,String> nodemap = new HashMap<>();

    static {
         nodemap.put("mly","目录页");
         nodemap.put("gzb","工资表");
         nodemap.put("kmzz","科目总账");
         nodemap.put("kmmx","科目明细账");
         nodemap.put("xjrj","现金/银行日记账");
         nodemap.put("fsye","发生额及余额表");
         nodemap.put("crk","出入库单");
         nodemap.put("cbft","成本分摊表");
         nodemap.put("zjmx","折旧明细表");
         nodemap.put("zcfz","资产负债表");
         nodemap.put("lrb","利润表");
         nodemap.put("pzfp","凭证封皮");
         nodemap.put("mxzfp","总账明细账封皮");
    }

    public static String getVprintName(String code) {
        if (!StringUtil.isEmpty(code)) {
            String[] codes = code.split(",");
            StringBuffer namestr = new StringBuffer();
            for (String temp : codes) {
                namestr.append(nodemap.get(temp) + ",");
            }
            return namestr.toString().substring(0,namestr.length()-1);
        }
        return "";
    }

}
