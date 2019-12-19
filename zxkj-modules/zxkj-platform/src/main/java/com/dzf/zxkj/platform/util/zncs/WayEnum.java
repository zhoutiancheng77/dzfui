package com.dzf.zxkj.platform.util.zncs;

public enum WayEnum {
	// ocr自识别
	IOCR("IOCR", "ocr自识别", 0),
	// 网站接口
	IWEB("IWEB", "网站接口", 1),
	// 票通接口
	IPT("IPT", "票通接口", 2),
	// 扫描仪识别
	SCA("SCA", "扫描仪识别", 3);

	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;

	/** 值 */
	private final int value;

	WayEnum(String key, String name, int value) {

		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static WayEnum getWayEnumByName(String name) {

		for (WayEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static WayEnum getWayEnumByValue(int value) {

		for (WayEnum item : values()) {
			if (item.getValue() == value) {
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

	public int getValue() {
		return value;
	}
}
