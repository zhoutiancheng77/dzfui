package com.dzf.zxkj.report.excel.rptexp.enums;


import com.dzf.zxkj.report.excel.rptexp.ExcelExportHander;
import com.dzf.zxkj.report.excel.rptexp.handler.TaxExportHander;

public enum ExportTemplateEnum {

    EN("en", "TaxEnHander", "0"),
    SHENZHEN("0", "TaxExportShenZhenHander", "1"),
    HEBEI("1", "ExcelExportHeBeiHander", "0"),
    ZHEJIANG("2", "ExcelExportZheJiangHander","0"),
    QINGDAO("3", "ExcelExportQingDaoHander", "0"),
    BEIJING("4", "ExcelExportBeiJingHander", "0"),
    SHANGHAI("5", "ExcelExportShangHaiHander", "0"),
    JIANGSU("6", "TaxExportJiangSuHander", "1"),
    HUBEI("7", "ExcelExportHuBeiHander", "0"),
    HENAN("8", "ExcelExportHenanHander", "0"),
    GUANGXI("9", "ExcelExportGuangXiHander", "0"),
    HAINAN("10", "ExcelExportHaiNanHander", "0"),
    SHANDONG("11", "ExcelExportStandardHander", "0"),
    CHONGQING("12", "ExcelExportStandardHander", "0"),
    FUJIAN("13", "ExcelExportFuJianHander", "0"),
    HUNAN("14", "ExcelExportHuNanHander", "0"),
    LIAONING("15", "ExcelExportStandardHander", "0"),
    XIAMEN("16", "ExcelExportXiaMenHander", "0"),
    ANHUI("17", "ExcelExportAnHuiHander", "0"),
    GUANGDONG("18", "ExcelExportGuangDongHander", "0"),
    GANSU("19", "ExcelExportGanSuHander", "0"),
    GUIZHOU("20", "ExcelExportGuiZhouHander", "0"),
    JILIN("21", "ExcelExportJiLinHander", "0"),
    NEIMENGGU("22", "ExcelExportNeiMengGuHander", "0"),
    SICHUAN("23", "ExcelExportSiChuanHander", "0"),
    YUNNAN("24", "ExcelExportYunNanHander", "0"),
    //llh 对于未定义专用Handler的地区，统一使用通用的 ExcelExportPubHandler
    //山西 25
    //陕西 26
    //新疆 27
    //大连 28
    //宁波 29
    //江西 30
    //黑龙江 31
    //青海 32
    //西藏 33
    PUB("pub", "ExcelExportPubHandler", "0"), //通用Handler
    STANDARD("standard", "ExcelExportStandardHander", "0");

    private String areaType;
    private String hander;
    private String fileType;  // 0 excel  1 tax

    ExportTemplateEnum(String areaType, String hander, String fileType){
        this.areaType = areaType;
        this.hander = hander;
        this.fileType = fileType;
    }

    public static String getFileType(String areaType){
        for(ExportTemplateEnum e : ExportTemplateEnum.values()){
            if(e.areaType.equals(areaType)){
                return e.fileType;
            }
        }
        return "0";
    }

    public static TaxExportHander getTaxHander(String areaType) {
        TaxExportHander tzxExportHander = null;
        for (ExportTemplateEnum e : ExportTemplateEnum.values()) {
            if (e.fileType.equals("1") && e.areaType.equals(areaType)) {
                try {
                    tzxExportHander = (TaxExportHander) Class.forName("com.dzf.zxkj.report.excel.rptexp.handler." + e.hander).newInstance();
                } catch (Exception ex) {
                }
                break;
            }
        }
        return tzxExportHander;
    }

    public static ExcelExportHander getExcelHandler(String areaType) {
        String handlerName = null;
        for (ExportTemplateEnum e : ExportTemplateEnum.values()) {
            if (e.fileType.equals("0") && e.areaType.equals(areaType)) {
                handlerName = e.hander;
                break;
            }
        }
        //llh 对于未定义专用Handler的地区，统一使用通用的 ExcelExportPubHandler
        if (handlerName == null || handlerName.length() == 0) {
            handlerName = "ExcelExportPubHandler";
        }

        ExcelExportHander excelExportHander = null;
        try {
            excelExportHander = (ExcelExportHander) Class.forName("com.dzf.zxkj.report.excel.rptexp.handler." + handlerName).newInstance();
        } catch (Exception ex) {
        }
        return excelExportHander;
    }

    public String getAreaType() {
        return areaType;
    }

    public String getHander() {
        return hander;
    }

    public String getFileType() {
        return fileType;
    }
}
