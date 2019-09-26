package com.dzf.zxkj.common.enums;

public enum IFpStyleEnum {
	COMMINVOICE("COMMINVOICE", "增值税普通发票", 1),

	SPECINVOICE("SPECINVOICE", "增值税专用发票", 2),

	NOINVOICE("NOINVOICE", "未开票", 3);

	private final String key;
	private final String name;
	private final int value;

	private IFpStyleEnum(String key, String name, int value) {
		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static IFpStyleEnum getTypeEnumByName(String name) {
		for (IFpStyleEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static IFpStyleEnum getTypeEnumByValue(int value) {
		for (IFpStyleEnum item : values()) {
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