package com.dzf.zxkj.common.enums;

public enum SurTaxEnum {
    URBAN_CONSTRUCTION_TAX("城建税", "001"),
    EDUCATION_SURTAX("教育费附加", "002"),
    LOCAL_EDUCATION_SURTAX("地方教育费附加", "003"),
    LOCAL_WATER_CONSTRUCTION_FUND("地方水利建设基金", "004"),
    CHANNEL_WORKS_FEE("河道工程修建维护管理费", "005"),
    FLOOD_PROTECTION_FEE("防洪费", "006"),
    CULTURE_CONSTRUCTION_FEE("文化事业建设费","007"),
    LABOR_UNION_EXPENDITURE("工会经费", "008"),
    STAMP_TAX("印花税", "009"),
    CONSUMPTION_TAX("消费税", "010");
    // 名称
    private String name;
    // 编码
    private String code;

    SurTaxEnum(String name, String code) {
        this.name = name;
        this.code = code;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static SurTaxEnum getTaxByName(String name) {
        for (SurTaxEnum tax : SurTaxEnum.values()) {
            if (tax.getName().equals(name)) {
                return tax;
            }
        }
        return null;
    }
}
