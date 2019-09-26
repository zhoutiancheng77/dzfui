package com.dzf.zxkj.common.enums;

public enum SalaryReportEnum {

	IDCARD("IDCARD", "居民身份证", "身份证", "中国"),

	CHINACARD("CHINACARD", "中国护照", "护照", "中国"),

	OFFICERCARD("OFFICERCARD", "军官证", "军官证", "中国"),

	SOLDIERCARD("SOLDIERCARD", "士兵证", "士兵证", "中国"),

	GACARD("GACARD", "港澳居民来往内地通行证", "港澳居民来往内地通行证", "中国香港"),

	XGYJCARD("XGYJCARD", "香港永久性居民身份证", "香港永久性居民身份证", "中国香港"),
	
	GACARD1("GACARD1", "港澳居民居住证", "港澳居民居住证", "中国香港"),
	
	AMYJCARD("AMYJCARD", "澳门特别行政区永久性居民身份证", "澳门特别行政区永久性居民身份证", "中国澳门"),

	TAICARD("TAICARD", "台湾居民来往大陆通行证", "台湾居民来往大陆通行证", "台湾"),

	TWCARD("TWCARD", "台湾居民居住证", "台湾身份证", "台湾"),

	FOREGINCARD("FOREGINCARD ", "外国护照", "外国护照", "外国护照"),

	FOREGINCYJARD("FOREGINCYJARD", "外国人永久居留身份证", "外国人永久居留身份证（外国人永久居留证）", "中国"),

	WJCARD("WJCARD", "武警警官证", "武警警官证", "中国"),
	
	WJGCARD("WJGCARD", "外交官证", "外交官证", "中国"),
	
	FOREGINCARDA("FOREGINCARDA", "外国人工作许可证（A 类）", "外国人工作许可证（A 类）", "中国"),
	
	FOREGINCARDB("FOREGINCARDB", "外国人工作许可证（B 类）", "外国人工作许可证（B 类）", "中国"),
	
	FOREGINCARDC("FOREGINCARDC", "外国人工作许可证（C 类）", "外国人工作许可证（C 类）", "中国"),

	OTHER("OTHER", "其他", "其他", "中国");

	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;

	/** 值 */
	private final String value;

	/** 国籍地区 */
	private final String area;

	SalaryReportEnum(String key, String name, String value, String area) {

		this.key = key;
		this.name = name;
		this.value = value;
		this.area = area;
	}

	public static SalaryReportEnum getTypeEnumByName(String name) {

		for (SalaryReportEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static SalaryReportEnum getTypeEnumByValue(String value) {

		for (SalaryReportEnum item : values()) {
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

	public String getArea() {
		return area;
	}

}
