package com.dzf.zxkj.report.excel.rptexp.enums;

public enum ExportRecordEnum {
    EN("en", "英文"),
    SHENZHEN("0", "深圳"),
    HEBEI("1", "河北"),
    ZHEJIANG("2", "浙江"),
    QINGDAO("3", "青岛"),
    BEIJING("4", "北京"),
    SHANGHAI("5", "上海"),
    JIANGSU("6", "江苏"),
    HUBEI("7", "湖北"),
    HENAN("8", "河南"),
    GUANGXI("9", "广西"),
    HAINAN("10", "海南"),
    SHANDONG("11", "山东"),
    CHONGQING("12", "重庆"),
    FUJIAN("13", "福建"),
    HUNAN("14", "湖南"),
    LIAONING("15", "辽宁"),
    XIAMEN("16", "厦门"),
    ANHUI("17", "安徽"),
    GUANGDONG("18", "广东"),
    GANSU("19", "甘肃"),
    GUIZHOU("20", "贵州"),
    JILIN("21", "吉林"),
    NEIMENGGU("22", "内蒙"),
    SICHUAN("23", "四川"),
    YUNNAN("24", "云南"),
    SHANXI("25", "山西"),
    SHAANXI("26", "陕西"),
    XINJIANG("27", "新疆"),
    DALIAN("28", "大连"),
    NINGBO("29", "宁波"),
    JIANGXI("30", "江西"),
    HEILONGJIANG("31", "黑龙江"),
    QINGHAI("32", "青海"),
    NINGXIA("34", "宁夏");

    private String areaType;
    private String areaName;

    ExportRecordEnum(String areaType, String areaName) {
        this.areaType = areaType;
        this.areaName = areaName;
    }

    public static String getRecordMessage(String areaType, String qj, String qjlx) {
        ExportRecordEnum recordEnum = null;
        for (ExportRecordEnum e : ExportRecordEnum.values()) {
            if (e.areaType.equals("0")) {
                recordEnum = e;
                break;
            }
        }
        if (recordEnum == null) {
            return "";
        }

        if ("1".equals(qjlx)) {
            String year = qj.substring(0, 4);
            String month = qj.substring(5, 7);
            return year + "-" + getJd(month) + "," + recordEnum.areaName;
        } else {
            return qj + "," + recordEnum.areaName;
        }
    }

    private static String getJd(String month) {
        switch (month) {
            case "03":
                return "第一季度";
            case "06":
                return "第二季度";
            case "09":
                return "第三季度";
            case "12":
                return "第四季度";
            default:
                return "";
        }
    }
}
