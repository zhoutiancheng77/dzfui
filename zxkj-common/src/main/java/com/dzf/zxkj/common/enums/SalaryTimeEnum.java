package com.dzf.zxkj.common.enums;

public enum SalaryTimeEnum {

	NOTOVER90DAY("NOTOVER90DAY", "不超过90天或183天"),

	OVER90DAY("OVER90DAY", "超过90天或183天但不满1年"),

	OVER1YEAR("OVER1YEAR", "满1年不超过5年(外籍高管满90或183天不超过5年)"),

	OVER5YEAR("OVER5YEAR", "居住满5年以上"),

	NOTOVER90DAYFOR("NOTOVER90DAYFOR", "外籍高管不超过90天或183天");

	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;

	SalaryTimeEnum(String key, String name) {

		this.key = key;
		this.name = name;
	}

	public static SalaryTimeEnum getTypeEnumByName(String name) {

		for (SalaryTimeEnum item : values()) {
			if (item.getName().equals(name)) {
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

}
