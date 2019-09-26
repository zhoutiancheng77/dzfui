package com.dzf.zxkj.common.constant;
/**
 * 
 * 凭证状态常量类
 *
 */
public class IVoucherConstants {
	public static final int TEMPORARY = -1;//暂存
	public static final int FREE = 8;//自由态
	public static final int AUDITED = 1;//审核通过
	
	//业务处理响应码值 BEGIN
	public static final int STATUS_ERROR_CODE = -200;//响应失败
	public static final int STATUS_RECONFM_CODE = -150;//再次确认(损益结转)
	public static final int STATUS_INVGL_CODE = -160;//再次确认(总账存货保存)
	
	
	public static final String EXE_RECONFM_CODE = "-150";//异常确认编码
	public static final String EXE_INVGL_CODE = "INVGL_";//异常确认编码
	
	//业务处理响应码值 END

	//凭证打印
	/** A4两版 */
	public static final int PRINT_A4_TWO = 1;
	/** A4两版 */
	public static final int PRINT_A4_TREE = 2;
	/** 发票版 14*24cm */
	public static final int PRINT_INVOICE = 3;
	/** 凭证纸 12*21cm 纵向  */
	public static final int PRINT_VOUHER_PORTRAIT = 4;
	/** 凭证纸 12*21cm 横向 */
	public static final int PRINT_VOUHER_LAND = 6;
	
	/** A5版*/
	public static final int PRINT_A5 = 5;
	
	/** B5版(25.7*18.2cm) */
	public static final int PRINT_B5 = 7;
	
	/** A4两版10行 */
	public static final int PRINT_A4_TENROW = 8;
	/** A510行 */
	public static final int PRINT_A5_TENROW = 9;
	
	/** 原制单人 */
	public static final int ORIGIN_USER = 0;
	/** 当前操作员 */
	public static final int CURRENT_USER = 1;
	/** 指定 */
	public static final int ASSIGN_USER = 2;
}
