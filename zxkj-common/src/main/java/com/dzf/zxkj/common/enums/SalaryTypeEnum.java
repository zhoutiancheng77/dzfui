package com.dzf.zxkj.common.enums;

public enum SalaryTypeEnum {

	NORMALSALARY("NORMALSALARY", "正常薪金", "01"),
	
	REMUNERATION("REMUNERATION", "劳务报酬", "02"),

	FOREIGNSALARY("FOREIGNSALARY", "外籍薪资", "03"),

	ANNUALBONUS("ANNUALBONUS", "年终奖", "04"),
	
	NONORMAL("NONORMAL", "其他", "05"),

	TOTAL("TOTAL", "工资概况", "10");



	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;

	/** 值 */
	private final String value;

	SalaryTypeEnum(String key, String name, String value) {

		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static SalaryTypeEnum getTypeEnumByName(String name) {

		for (SalaryTypeEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static SalaryTypeEnum getTypeEnumByValue(String value) {

		for (SalaryTypeEnum item : values()) {
			if (item.getValue().equals(value)) {
				return item;
			}
		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
