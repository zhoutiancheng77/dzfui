package com.dzf.zxkj.platform.filter;

import java.util.*;

public class NodeFillter {

    private static Map<String, List<String>> nodesFillterByCorpType = new HashMap<>();
    private static Set<String> nodeNames = new HashSet<>();
    private static List<String> qyxxFiller= Arrays.asList("00000100AA10000000000BMQ","00000100000000Ig4yfE0003","00000100000000Ig4yfE0004","00000100000000Ig4yfE0006","00000100000000Ig4yfE0007","00000100000000Ig4yfE0005","00000100000000Ig4yfE0008");

    static {
        //小企业会计准则
        nodesFillterByCorpType.put("00000100AA10000000000BMD", Arrays.asList("资产负债表", "利润表", "利润表季报", "分部利润表", "现金流量季报", "现金流量表", "纳税查询", "风控体检", "财务概要信息"));
        //企业会计准则
        nodesFillterByCorpType.put("00000100AA10000000000BMF", Arrays.asList("资产负债表", "利润表", "利润表季报", "分部利润表", "现金流量季报", "现金流量表", "纳税查询", "风控体检", "财务概要信息"));
        //事业单位会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0003", Arrays.asList("资产负债表", "收入支出表"));
        //民间非营利组织会计制度
        nodesFillterByCorpType.put("00000100AA10000000000BMQ", Arrays.asList("资产负债表", "业务活动表", "业务活动季报", "现金流量表", "现金流量季报"));
        //农村集体经济组织
        nodesFillterByCorpType.put("00000100000000Ig4yfE0004", Arrays.asList("资产负债表", "收益及收益分配表", "纳税查询"));
        //农民专业合作社财务会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0006", Arrays.asList("资产负债表","纳税查询", "权益变动表", "盈余分配表"));
        //医院会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0007", Arrays.asList("收入费用总表", "纳税查询"));
        //企业会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0005", Arrays.asList("资产负债表", "利润表", "利润表季报", "分部利润表", "现金流量季报", "现金流量表", "纳税查询"));

        nodesFillterByCorpType.put("00000100000000Ig4yfE0008", new ArrayList());


        nodesFillterByCorpType.forEach((k, v) -> nodeNames.addAll(v));
    }

    public static boolean isShowInQyxx(String corpType){
        return !qyxxFiller.contains(corpType);
    }

    public static boolean isCheckNode(String nodeName) {
        return nodeNames.contains(nodeName);
    }

    public static boolean isHasNode(String corpType, String nodeName) {
        return nodesFillterByCorpType.containsKey(corpType) && nodesFillterByCorpType.get(corpType).contains(nodeName);
    }

    public static boolean isCheckChildrenNode(String nodeName){
        return "财务报表".equals(nodeName);
    }

    public static boolean isHasChildrenNode(String nodeName, Map nodeMap){
        return  nodeMap.containsKey(nodeName);
    }
}
