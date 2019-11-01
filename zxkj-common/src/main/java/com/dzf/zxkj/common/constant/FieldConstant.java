package com.dzf.zxkj.common.constant;

import java.io.File;

public class FieldConstant {

    /**
     * 收支类型
     */
    public final static String SZSTYLE_01 = "01";//现金收入
    public final static String SZSTYLE_02 = "02";//现金支出
    public final static String SZSTYLE_03 = "03";//银行收入
    public final static String SZSTYLE_04 = "04";//银行支出
    public final static String SZSTYLE_05 = "05";//其他收入
    public final static String SZSTYLE_06 = "06";//其他支出
    
    /**
     * 发票类型
     */
    public final static String FPSTYLE_01 = "01";//增值税专用发票
    public final static String FPSTYLE_02 = "02";//增值税普通发票
    public final static String FPSTYLE_03 = "03";//银行收款单
    public final static String FPSTYLE_04 = "04";//缴纳税款
    public final static String FPSTYLE_05 = "05";//支付社保
    public final static String FPSTYLE_06 = "06";//银行转账回单
    public final static String FPSTYLE_07 = "07";//借款类票据
    public final static String FPSTYLE_08 = "08";//差旅报销单
    public final static String FPSTYLE_09 = "09";//费用报销单
    public final static String FPSTYLE_10 = "10";//支出单报销
    public final static String FPSTYLE_11 = "11";//服务发票
    public final static String FPSTYLE_12 = "12";//存现
    public final static String FPSTYLE_13 = "13";//工资发放表
    public final static String FPSTYLE_14 = "14";//借支票
    public final static String FPSTYLE_15 = "15";//手续费
    public final static String FPSTYLE_16 = "16";//领料单
    public final static String FPSTYLE_17 = "17";//固定资产购入
    public final static String FPSTYLE_18 = "18";//增值税专用发票-劳务
    public final static String FPSTYLE_19 = "19";//增值税普通发票-材料
    
    public final static String FPSTYLE_20 = "20";//银行收付款回单 
    public final static String FPSTYLE_21 = "21";//其他票据
    
    /**
     * 业务类型
     */
    public final static String YWSTYLE_01 = "员工借款";//
    public final static String YWSTYLE_02 = "报办公费";//
    public final static String YWSTYLE_03 = "付货款";//
    public final static String YWSTYLE_04 = "报办公费";//
    public final static String YWSTYLE_05 = "销售商品";
    public final static String YWSTYLE_06 = "收到货款";
    public final static String YWSTYLE_07 = "缴纳税款";
    public final static String YWSTYLE_08 = "支付社保";
    public final static String YWSTYLE_09 = "存现";
    public final static String YWSTYLE_10 = "薪资费用";
    public final static String YWSTYLE_11 = "差旅费报销";
    public final static String YWSTYLE_12 = "支付银行手续费";
    public final static String YWSTYLE_13 = "领料";
    public final static String YWSTYLE_14 = "购买固定资产";
    public final static String YWSTYLE_15 = "服务收入";
    public final static String YWSTYLE_16 = "购买材料";
    public final static String YWSTYLE_17 = "借出现金";
    public final static String YWSTYLE_18 = "借出支票";
    public final static String YWSTYLE_19 = "购买材料";
    public final static String YWSTYLE_20 = "报差旅费";
    public final static String YWSTYLE_21 = "费用报销单";
    
    public final static String YWSTYLE_22 = "销售收入";
    public final static String YWSTYLE_23 = "销售商品";
    public final static String YWSTYLE_24 = "购买商品";
    public final static String YWSTYLE_24_01 = "购买商品（小规模）";
    public final static String YWSTYLE_24_02 = "购买商品（一般人）";
    public final static String YWSTYLE_25 = "银行收款单";
    public final static String YWSTYLE_26 = "银行付款单";
    public final static String YWSTYLE_27 = "银行手续费";
    public final static String YWSTYLE_28 = "银行费用";
    public final static String YWSTYLE_29 = "其他费用";
    public final static String YWSTYLE_30 = "报招待费";
    public final static String YWSTYLE_31 = "差旅报销单";
    /************************2018-01-13  暂时修改************************/
    public final static String YWSTYLE_32 = "劳务费";
    public final static String YWSTYLE_33 = "咨询顾问费";
    public final static String YWSTYLE_34 = "租赁费";
    public final static String YWSTYLE_35 = "研究费用";
    public final static String YWSTYLE_36 = "业务招待费";
    public final static String YWSTYLE_37 = "差旅费";
    public final static String YWSTYLE_38 = "车杂费";
    
    
    /**
     * 字段常量
     */
    public final static String FIELD_01_01_1 = "销售方的单位";//
    public final static String FIELD_01_01_2 = "购买方单位";//
    public final static String FIELD_01_02 = "价税合计金额";//
    public final static String FIELD_01_03_1 = "购买商品的名称及规格型号";//
    public final static String FIELD_01_03_2 = "商品的名称及规格型号";//
    public final static String FIELD_01_04 = "数量";//
    public final static String FIELD_01_05 = "无税金额";//
    public final static String FIELD_01_06 = "税额";//
    
    public final static String FIELD_02_01_1 = "销售单位";//
    public final static String FIELD_02_01_2 = "购买方单位";//
    public final static String FIELD_02_02 = "价税合计金额";//
    public final static String FIELD_02_03_1 = "商品的名称及规格型号";//
    public final static String FIELD_02_03_2 = "确认销售的商品的名称及规格型号";//
    public final static String FIELD_02_04_1 = "数量";//
    public final static String FIELD_02_04_2 = "销售数量";//
    public final static String FIELD_02_05 = "无税金额";//
    public final static String FIELD_02_06 = "税额";//
    public final static String FIELD_02_07 = "金额加税额";//
    
    public final static String FIELD_03_01 = "收款的银行";//
    public final static String FIELD_03_02 = "付款人名称";//
    public final static String FIELD_03_03 = "收款金额";//
    public final static String FIELD_03_04 = "用途";//
    
    public final static String FIELD_04_01 = "确认税种";//
    public final static String FIELD_04_02 = "确认实缴的金额";//
    
    public final static String FIELD_05_01 = "收款人名称";//
    public final static String FIELD_05_02 = "支付的金额";//
    public final static String FIELD_05_03 = "付款银行";//
    
    public final static String FIELD_06_01 = "付款银行";//
    public final static String FIELD_06_02 = "收款人名称";//
    public final static String FIELD_06_03 = "支付的金额";//
    public final static String FIELD_06_04 = "用途";//
    
    public final static String FIELD_07_01 = "借款金额";//
    public final static String FIELD_07_02 = "借款人";//
    public final static String FIELD_07_03 = "支付方式";//
    
    public final static String FIELD_08_01 = "报销的类型";//
    public final static String FIELD_08_02 = "报销的部门";//
    public final static String FIELD_08_03 = "报销人员";//
    public final static String FIELD_08_04 = "报销费用的总金额";//
    public final static String FIELD_08_05 = "预借差旅金额";//
    public final static String FIELD_08_06 = "补领金额";//
    public final static String FIELD_08_07 = "退还金额";//
    
    public final static String FIELD_09_01 = "报销类型";//
    public final static String FIELD_09_02 = "报销的具体内容";//
    public final static String FIELD_09_03 = "报销金额";//
    public final static String FIELD_09_04 = "报销人员";//
    
    public final static String FIELD_10_01 = "确认报销内容";//
    public final static String FIELD_10_02 = "确认报销的金额";//
    
    public final static String FIELD_11_01 = "付款单位";
    public final static String FIELD_11_02 = "确认销售的商品的名称及规格型号";
    public final static String FIELD_11_03 = "单行金额";
    public final static String FIELD_11_04 = "总金额";
    
    public final static String FIELD_12_01 = "收款银行";//
    public final static String FIELD_12_02 = "金额";//
    public final static String FIELD_12_03 = "备注";//
    
    public final static String FIELD_13_01 = "扣社保";//
    public final static String FIELD_13_02 = "扣个税";//
    public final static String FIELD_13_03 = "实发工资";//
    
    public final static String FIELD_14_01 = "借款金额";//
    public final static String FIELD_14_02 = "借款人";//
    public final static String FIELD_14_03 = "支付方式";//
    
//  public final static String XML_DIR = "C:/decompFolder";
    
//  public final static String XML_DIR = "c://home/CDG/CDG_DATA_DEV/decompFolder";
//  
//  public final static String XML_BACKDIR = "c://home/CDG/CDG_DATA_DEV/decompFolder_back";
    
    public final static String XML_DIR = File.separator+"home"+File.separator+"CDG"+File.separator+"CDG_DATA_DEV"+File.separator+"decompFolder";//"c://home/CDG/CDG_DATA_DEV/decompFolder";
    
    public final static String XML_BACKDIR = File.separator+"home"+File.separator+"CDG"+File.separator+"CDG_DATA_DEV"+File.separator+"decompFolder_back";//"c://home/CDG/CDG_DATA_DEV/decompFolder_back";
    
    
}
