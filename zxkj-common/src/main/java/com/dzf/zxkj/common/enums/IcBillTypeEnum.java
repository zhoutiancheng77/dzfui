package com.dzf.zxkj.common.enums;

import com.dzf.zxkj.common.constant.IcConst;

public enum IcBillTypeEnum {

	CGTYPE("CGTYPE", "采购入库", IcConst.CGTYPE),

	WGTYPE("WGTYPE", "完工入库", IcConst.WGTYPE),

	QTRTYPE("QTRTYPE", "其他入库", IcConst.QTRTYPE),

	XSTYPE("XSTYPE", "销售出库", IcConst.XSTYPE),

	LLTYPE("LLTYPE", "领料出库", IcConst.LLTYPE),

	QTCTYPE("QTCTYPE", "其他出库", IcConst.QTCTYPE),

	CBTZTYPE("CBTZTYPE", "成本调整", IcConst.CBTZTYPE);

	private final String key;
	private final String name;
	private final String value;

	private IcBillTypeEnum(String key, String name, String value) {
		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static IcBillTypeEnum getTypeEnumByName(String name) {
		for (IcBillTypeEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static IcBillTypeEnum getTypeEnumByValue(String value) {
		for (IcBillTypeEnum item : values()) {
			if (item.getValue().equals(value)) {
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

	public String getValue() {
		return this.value;
	}
}