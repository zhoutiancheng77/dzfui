package com.dzf.zxkj.common.constant;

public class IInvoiceApplyConstant {

    /**申请开通**/
    public static int APPLY_STATUS_0 = 0;//未申请
    public static int APPLY_STATUS_1 = 1;//企业主确认中
    public static int APPLY_STATUS_2 = 2;//企业主确认不开通
    public static int APPLY_STATUS_3 = 3;//申请中
    public static int APPLY_STATUS_4 = 4;//申请失败
    public static int APPLY_STATUS_5 = 5;//已开通
    public static int APPLY_STATUS_6 = 6;//关闭申请中
    public static int APPLY_STATUS_7 = 7;//已关闭

    /**开具相关**/
    public static int INV_STATUS_0 = 0;//未开票   STATUS_APPLY

    public static int INV_STATUS_2 = 2;//老数据存储 同  STATUS_SENTOUT 寄出
    public static int INV_STATUS_3 = 3;//老数据存储  同 STATUS_ACCOUNTING 入账
    public static int INV_STATUS_4 = 4;//老数据存储  同 STATUS_TAX  报税

    public static int INV_STATUS_1 = 1;//开票成功
    public static int INV_STATUS_5 = 5;//开票中
    public static int INV_STATUS_6 = 6;//开票失败

    //营业执照副本类型
    public static int FILETYPE_2 = 2;//

    //税率相关
    public static String SL_MIANZHENG = "免税";
    public static String SL_BUZHENGSHUI = "不征税";
    public static String SL_CHUKOULINGSHUI = "出口零税";

    //零税率标识
    public static int ZERO_SL_0 = 0;//出口零税
    public static int ZERO_SL_1 = 1;//免 税
    public static int ZERO_SL_2 = 2;//不征税
    public static int ZERO_SL_3 = 3;// 普通零税率


    //冲红相关
    public static int RED_FLAG_1 = 1;//已冲红
    public static int RED_FLAG_2 = 2;//冲红失败
    public static int RED_FLAG_3 = 3;//冲红中


    //开票处理数据来源
    public static int KP_SOURCE_0 = 0;//手机APP  null 也是代表手机App
    public static int KP_SOURCE_1 = 1;//在线会计
    public static int KP_SOURCE_2 = 2;//节点数据衍生出来的 如 冲红操作


    //票通返回报文中涉及到的code
    public static String ISSUETYPE_1 = "1";//开具蓝票
    public static String ISSUETYPE_2 = "2";//开具红票
//    public static int PT_RES_OPE_10 = 10;//开具蓝票
//    public static int PT_RES_OPE_20 = 20;//开具红票
//    public static int PT_RES_OPE_30 = 30;//作废蓝票
//    public static int PT_RES_OPE_40 = 40;//作废红票

}
