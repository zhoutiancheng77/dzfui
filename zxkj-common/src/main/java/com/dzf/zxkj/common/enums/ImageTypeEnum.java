package com.dzf.zxkj.common.enums;


import com.dzf.zxkj.common.constant.ImageTypeConst;

public enum ImageTypeEnum {

	KHSFKRZTZ("KHSFKRZTZ", ImageTypeConst.KHSFKRZTZ, 0, ImageTypeConst.BANK_UNDEFINED_BILL1),
	TCZDRZHD("TCZDRZHD", ImageTypeConst.TCZDRZHD, 1, ImageTypeConst.BANK_UNDEFINED_BILL1),
	DHPZ("DHPZ", ImageTypeConst.DHPZ, 2, ImageTypeConst.BANK_UNDEFINED_BILL1),
	RZTZ("RZTZ", ImageTypeConst.RZTZ, 3, ImageTypeConst.BANK_UNDEFINED_BILL1),
	
	SK("SK", ImageTypeConst.SK, 4, ImageTypeConst.BANK_RECEIPTS_BILL),
	KHSK("KHSK", ImageTypeConst.KHSK, 5, ImageTypeConst.BANK_RECEIPTS_BILL),
	DJHD("DJHD", ImageTypeConst.DJHD, 6, ImageTypeConst.BANK_RECEIPTS_BILL),
	ZFYWSKHD("ZFYWSKHD", ImageTypeConst.ZFYWSKHD, 7, ImageTypeConst.BANK_RECEIPTS_BILL),
	YHDJ("YHDJ", ImageTypeConst.YHDJ, 8, ImageTypeConst.BANK_RECEIPTS_BILL),
	TCSK("TCSK", ImageTypeConst.TCSK, 9, ImageTypeConst.BANK_RECEIPTS_BILL),
	SKHD("SKHD", ImageTypeConst.SKHD, 10, ImageTypeConst.BANK_RECEIPTS_BILL),
	FKHDTCSK("FKHDTCSK", ImageTypeConst.FKHDTCSK, 11, ImageTypeConst.BANK_RECEIPTS_BILL),
	DZHDPZ("DZHDPZ", ImageTypeConst.DZHDPZ, 12, ImageTypeConst.BANK_RECEIPTS_BILL),
	DJTZ("DJTZ", ImageTypeConst.DJTZ, 13, ImageTypeConst.BANK_RECEIPTS_BILL),
	SKPZ("SKPZ", ImageTypeConst.SKPZ, 14, ImageTypeConst.BANK_RECEIPTS_BILL),
	KHSKHD("KHSKHD", ImageTypeConst.KHSKHD, 15, ImageTypeConst.BANK_RECEIPTS_BILL),
	DZHR("DZHR", ImageTypeConst.DZHR, 16, ImageTypeConst.BANK_RECEIPTS_BILL),
	
	JJHD("JJHD", ImageTypeConst.JJHD, 17, ImageTypeConst.BANK_PAY_BILL),
	FK("FK", ImageTypeConst.FK, 18, ImageTypeConst.BANK_PAY_BILL),
	KHFK("KHFK", ImageTypeConst.KHFK, 19, ImageTypeConst.BANK_PAY_BILL),
	FKPZ("FKPZ", ImageTypeConst.FKPZ, 20, ImageTypeConst.BANK_PAY_BILL),
	JJTZ("JJTZ", ImageTypeConst.JJTZ, 21, ImageTypeConst.BANK_PAY_BILL),
	YCDQKK("YCDQKK", ImageTypeConst.YCDQKK, 22, ImageTypeConst.BANK_PAY_BILL),
	EDZF("EDZF", ImageTypeConst.EDZF, 23,ImageTypeConst.BANK_PAY_BILL),
	ZKHD("ZKHD", ImageTypeConst.ZKHD, 24, ImageTypeConst.BANK_PAY_BILL),
	DIZZPZ("DIZZPZ", ImageTypeConst.DIZZPZ, 25,ImageTypeConst.BANK_PAY_BILL),
	
	SFRZTZS("SFRZTZS", ImageTypeConst.SFRZTZS, 26, ImageTypeConst.BANK_CHARGES_BILL),
	QYWYSXF("QYWYSXF", ImageTypeConst.QYWYSXF, 27, ImageTypeConst.BANK_CHARGES_BILL),
	KFRZTZS("KFRZTZS", ImageTypeConst.KFRZTZS, 28, ImageTypeConst.BANK_CHARGES_BILL),
	FKHD("FKHD", ImageTypeConst.FKHD, 29, ImageTypeConst.BANK_CHARGES_BILL),
	SFPZ("SFPZ", ImageTypeConst.SFPZ, 30, ImageTypeConst.BANK_CHARGES_BILL),
	ZZHKSXF("ZZHKSXF", ImageTypeConst.ZZHKSXF, 31, ImageTypeConst.BANK_CHARGES_BILL),
	QYWYJYSXF("QYWYJYSXF", ImageTypeConst.QYWYJYSXF, 34, ImageTypeConst.BANK_CHARGES_BILL),
	
	CKJXHD("CKJXHD", ImageTypeConst.CKJXHD, 32, ImageTypeConst.BANK_COST_BILL),
	SFHD("ZZHKSXF", ImageTypeConst.SFHD, 33, ImageTypeConst.BANK_COST_BILL),
	LXSRHD("LXSRHD", ImageTypeConst.LXSRHD, 35,ImageTypeConst.BANK_COST_BILL),
	DKLXQD("DKLXQD", ImageTypeConst.DKLXQD, 36,ImageTypeConst.BANK_COST_BILL),
	
	
	DZJSFKPZ("DZJSFKPZ", ImageTypeConst.DZJSFKPZ, 37, ImageTypeConst.BANK_PAYTAXES_BILL),
	
	SBGJJJCD("SBGJJJCD", ImageTypeConst.SBGJJJCD, 38, ImageTypeConst.BANK_SOCIALSECURITY_BILL),
	DGXJCKHD("DGXJCKHD", ImageTypeConst.DGXJCKHD, 39, ImageTypeConst.BANK_EXISTENTIAL_BILL),
	
	HCP("HCP", ImageTypeConst.HCP, 40, ImageTypeConst.QUOTA_TRAVELBUSINESS_BILL),
	DEFP("DEFP", ImageTypeConst.DEFP, 41, ImageTypeConst.QUOTA_COST_BILL);
	 
	
	/** 键 */
	private final String key;
	/** 名字 */
	private final String name;
	/** 值 */
	private final int value;
	/** 票据类型*/
	private final int imagetype;
	
	ImageTypeEnum(String key, String name, int value, int imagetype) {

		this.key = key;
		this.name = name;
		this.value = value;
		this.imagetype = imagetype;
	}
	
	public static ImageTypeEnum getPjTypeEnumByName(String name){
		for(ImageTypeEnum item : values()){
			if(item.getName().equals(name)){
				return item;
			}
		}
		return null;
	}
	public static ImageTypeEnum getPjTypeEnumByValue(int value){
		for(ImageTypeEnum item : values()){
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

	public int getImagetype() {
		return imagetype;
	}
	
}
