package com.dzf.zxkj.common.enums;

public enum IcPayWayEnum {
	CASH("CASH", "现金", 0),

	ARREARS("ARREARS", "往来", 1),

	BANK("BANK", "银行", 2);

	private final String key;
	private final String name;
	private final int value;

	private IcPayWayEnum(String key, String name, int value) {
		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static IcPayWayEnum getTypeEnumByName(String name) {
		for (IcPayWayEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static IcPayWayEnum getTypeEnumByValue(int value) {
		for (IcPayWayEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

	public String getKey() {
		return this.key;
	}

	public String getName() {
		return this.name;
	}

	public int getValue() {
		return this.value;
	}
}