package com.dzf.zxkj.platform.auth.util;

import java.util.*;

public class PermissionFilter {

    public static Map<String, List<String>> nodesFillterByCorpType = new HashMap<>();
    public static Set<String> nodeNames = new HashSet<>();

    //会计制度
    public static final String KJQJ_2013 = "00000100AA10000000000BMD";//小企业会计准则
    public static final String KJQJ_2007 = "00000100AA10000000000BMF";//企业会计准则
    public static final String KJQJ_QYKJZD = "00000100000000Ig4yfE0005";//企业会计制度
    public static final String KJQJ_MINFEI = "00000100AA10000000000BMQ";//民间非营利组织会计制度
    public static final String KJQJ_SHIYE = "00000100000000Ig4yfE0003";//事业单位会计制度
    public static final String KJQJ_CJT = "00000100000000Ig4yfE0004"; //农村集体经济组织
    public static final String KJQJ_HZS = "00000100000000Ig4yfE0006"; //农民专业合作社财务会计制度
    public static final String KJQJ_YY = "00000100000000Ig4yfE0007"; //医院会计制度
    public static final String KJQJ_XZDW = "00000100000000Ig4yfE0008";//行政单位会计制度
    //节点对应pk
    public static final String ZC_FUN_NODE_PK = "WDZF02SS0ESS00000EE00181"; //资产管理
    public static final String KCGL_FUN_NODE_PK = "WDZF02SS0ESS00000EE00517"; //进销存 新模式
    public static final String KCGL1_FUN_NODE_PK = "WDZF02SS0ESS00000EE00217";//进销存 老模式
    public static final String KCGL2_FUN_NODE_PK = "WDZF02SS0ESS00000EE00180";//总账核算存货
    public static final String ZCFZB_FUN_NODE_PK = "WDZF02SS0ESS00000EE00145";//资产负债表
    public static final String LRB_FUN_NODE_PK = "WDZF02SS0ESS00000EE00149"; //利润表
    public static final String LRBJB_FUN_NODE_PK = "00000100000000Hel2pD0lrb";//利润表季报
    public static final String FBLRB_FUN_NODE_PK = "00000100000000Hel2plrbzx"; //分部利润表
    public static final String XJLLB_FUN_NODE_PK = "WDZF02SS0ESS00000EE00153";//现金流量表
    public static final String XJLLBJB_FUN_NODE_PK = "00000100000000IJkYVZ000Q"; //现金流量季报
    public static final String CWGY_FUN_NODE_PK = "WDZF02SS0ESS00000EE01165";//财务概要信息
    public static final String YWHDB_FUN_NODE_PK = "00000100000000ImaluF0002"; //业务活动表
    public static final String YWHDBJB_FUN_NODE_PK = "00000100000000ImaluF0jb2";//业务活动季报
    public static final String YYFPB_FUN_NODE_PK = "WDZF02SS0ESS00000EE00150"; //盈余分配表
    public static final String QYBDB_FUN_NODE_PK = "WDZF02SS0ESS000QyEE00150";//权益变动表
    public static final String SRZCB_FUN_NODE_PK = "00000100000000ImwvC00002"; //收入支出表
    public static final String FKTJ_FUN_NODE_PK = "00000100000000WehbFV0005"; //风控体检

    static {
        //小企业会计准则
        nodesFillterByCorpType.put("00000100AA10000000000BMD", Arrays.asList(ZCFZB_FUN_NODE_PK, LRB_FUN_NODE_PK , LRBJB_FUN_NODE_PK, FBLRB_FUN_NODE_PK, XJLLBJB_FUN_NODE_PK, XJLLB_FUN_NODE_PK,  FKTJ_FUN_NODE_PK, CWGY_FUN_NODE_PK));
        //企业会计准则
        nodesFillterByCorpType.put("00000100AA10000000000BMF", Arrays.asList(ZCFZB_FUN_NODE_PK, LRB_FUN_NODE_PK , LRBJB_FUN_NODE_PK, FBLRB_FUN_NODE_PK, XJLLBJB_FUN_NODE_PK, XJLLB_FUN_NODE_PK,  FKTJ_FUN_NODE_PK, CWGY_FUN_NODE_PK));
        //事业单位会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0003", Arrays.asList(ZCFZB_FUN_NODE_PK, SRZCB_FUN_NODE_PK));
        //民间非营利组织会计制度
        nodesFillterByCorpType.put("00000100AA10000000000BMQ", Arrays.asList(ZCFZB_FUN_NODE_PK, YWHDB_FUN_NODE_PK, YWHDBJB_FUN_NODE_PK, XJLLB_FUN_NODE_PK, XJLLBJB_FUN_NODE_PK));
        //农村集体经济组织
        nodesFillterByCorpType.put("00000100000000Ig4yfE0004", Arrays.asList(ZCFZB_FUN_NODE_PK));
        //农民专业合作社财务会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0006", Arrays.asList(ZCFZB_FUN_NODE_PK, QYBDB_FUN_NODE_PK, YYFPB_FUN_NODE_PK));
        //医院会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0007", new ArrayList());
        //企业会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0005", Arrays.asList(ZCFZB_FUN_NODE_PK, LRB_FUN_NODE_PK , LRBJB_FUN_NODE_PK, FBLRB_FUN_NODE_PK, XJLLBJB_FUN_NODE_PK, XJLLB_FUN_NODE_PK));
        //行政单位会计制度
        nodesFillterByCorpType.put("00000100000000Ig4yfE0008", new ArrayList());

        nodesFillterByCorpType.forEach((k, v) -> nodeNames.addAll(v));


    }
}
