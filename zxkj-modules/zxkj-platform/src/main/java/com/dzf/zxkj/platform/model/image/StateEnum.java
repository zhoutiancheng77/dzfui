package com.dzf.zxkj.platform.model.image;

public enum StateEnum {

	// 上传
	INIT("INIT", "初始化", 0),

	// ocr识图
	SUCCESS_OCR("SUCCESS_OCR", "OCR识图成功", 1),

	FAIL_OCR("FAIL_OCR", "OCR识图失败", 2),

	// 生成凭证
	SUCCESS_COMFIRM("SUCCESS_COMFIRM", "确认公司成功", 3),

	FAIL_COMFIRM("FAIL_COMFIRM", "确认公司失败", 4),

	FAIL_GENERATE("FAIL_GENERATE", "图片分配失败", 5),

	SUCCESS_VOCHER("SUCCESS_VOCHER", "生成凭证成功", 6),

	SUCCESS_PREVOCHER("SUCCESS_PREVOCHER", "生成预凭证成功", 7),

	FAIL_VOCHER("FAIL_VOCHER", "生成凭证失败", 8),

	FAIL_PREVOCHER("FAIL_PREVOCHER", "生成预凭证失败", 9),

	// 网站接口识别
	SUCCESS_INTER_INSERT("SUCCESS_INTER_INSERT", "网站接口传入KEY成功", 10),

	FAIL_INTER_INSERT("FAIL_INTER_INSERT", "网站接口传入KEY失败", 11),

	SUCCESS_INTER_GET("SUCCESS_INTER_GET", "网站接口获取信息成功", 12),

	FAIL_INTER_GET("FAIL_INTER_GET", "网站接口获取信息失败", 13),

	// 票通接口识别
	SUCCESS_PTINTER_GET("SUCCESS_PTINTER_GET", "票通接口获取信息成功", 14),

	FAIL_PTINTER_GET("FAIL_PTINTER_GET", "票通接口获取信息失败", 15),

	// 不需要接口
	NO_INTER("NO_INTER", "不需要接口识别", 16),
	// 手动制单
	HAND_BILL("HAND_BILL", "手动制单", 17),
	// 手动退回
	HAND_BACK("HAND_BACK", "退回", 18),
	// 识图完成
	COMPLETE("COMPLETE", "识图完成", 19),
	// 识图失败
	FAIL("FAIL", "识图失败", 20);

	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;

	/** 值 */
	private final int value;

	StateEnum(String key, String name, int value) {

		this.key = key;
		this.name = name;
		this.value = value;
	}

	public static StateEnum getTypeEnumByName(String name) {

		for (StateEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static StateEnum getTypeEnumByValue(int value) {

		for (StateEnum item : values()) {
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
