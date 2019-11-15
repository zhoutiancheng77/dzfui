package com.dzf.zxkj.platform.util.zncs;
/**
 * 业务类型
 * @author wangzhn
 *
 */
public enum PjTypeEnum {

	TZKXSFP("TZKXSFP", "自开销售发票", 0, 0),
	TDKXSFP("TDKXSFP", "代开销售发票", 1, 5),
	TCGZYFP("TCGZYFP", "采购专用发票", 2, 10),
	TCGPTFP("TCGPTFP", "采购普通发票", 3, 15),
	TYHSKHD("TYHSKHD", "银行收款回单", 4, 20),
	TYHFKHD("TYHFKHD", "银行付款回单", 5,25),
	TYHSXFHD("TYHSXFHD", "银行手续费回单", 6, 30),
	TYHLXHD("TYHLXHD", "银行利息回单", 7, 35),
	TSBGJJJCD("TSBGJJJCD", "社保、公积金缴存单", 8, 40),
	
	TZKLW("TZKLW", "自开劳务发票", 17, 45),
	TDKLW("TDKLW", "代开劳务发票", 18, 50),
	
	TJSD("TJSD", "缴税单", 9, 55),
	TGZD("TGZD", "工资单", 10, 60),
	TJTF("TJTF", "交通费", 11, 65),
	TBGF("TBGF", "办公费", 12, 70),
	TZDF("TZDF", "招待费", 13, 75),
	TZLF("TZLF", "差旅费", 14, 80),
	TQTPJ("TQTPJ", "其他单据", 15, 85),
	TWXPJ("TWXPJ", "无效单据", 16, 90),
	ALL("ALL", "全部", 20, 95),
	INV("INV", "存货", 21, 100),
	OTHER("OTHER", "其他", 22, 105);
	
	
	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;
	/** 值 */
	private final int value;
	/** 排序*/
	private final int order;
	
	PjTypeEnum(String key, String name, int value, int order) {

		this.key = key;
		this.name = name;
		this.value = value;
		this.order = order;
	}
	
	public static PjTypeEnum getPjTypeEnumByName(String name){
		for(PjTypeEnum item : values()){
			if(item.getName() == name){
				return item;
			}
		}
		return null;
	}
	public static PjTypeEnum getPjTypeEnumByValue(int value){
		for(PjTypeEnum item : values()){
			if(item.getValue() == value){
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
	
	public int getOrder(){
		return order;
	}
}
