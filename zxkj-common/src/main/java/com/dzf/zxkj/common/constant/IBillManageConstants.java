package com.dzf.zxkj.common.constant;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
public interface IBillManageConstants {
    //银行账户常量
    int QIY_STATUS = 0;//启用
    int TINGY_STATUS = 1;//停用

    //销进项票据来源
    int AUTO = 0;//自动导入
    int MANUAL = 1;//手动添加
    int CAISHUI_AUTO = 2;//财税助手导入
    int ZHONGXING_AUTO = 3;//中兴通导入
    //	public static final int PIAOTONGSM_AUTO = 4;//票通扫描仪清单导入
    int ZENGZHIAHUI_AUTO = 5;//增值税认证平台导入
    int CAIFANGTONG = 6;//财房通导入
    int PIAOTONGKP = 7;//票通开票导入
    int PIAOTONGJX = 8;//票通进项导入
    int FAPIAOSAOMA = 10;//发票扫码调用，这个是服务器与服务器，注意与财税助手EXCEL导入的区别
    int OLDEDITION = 11;//老版本导入模板（过滤用）
    int OCR = 15;//OCR识别

    //进项认证结果
    int RSNOPASS = 0;
    int RSPASS = 1;

    String[] SPEC_ACC_CODE = {
            "1122",//应收账款
            "1123",//预付账款
            "2202",//应付账款
            "2203"//预收账款
    };

    /****************合并制单的表单********************/
    String HEBING_YHDZF = "1";//银行对账单
    String HEBING_XXFP  = "2";//销项清单
    String HEBING_JXFP  = "3";//进项清单
    String HEBING_ZNPZ  = "4";//智能凭证
    /****************合并制单的规则********************/
//	public static final int HEBING_GZ_01 = 1;//相同往来单位合并一张
//	public static final int HEBING_GZ_02 = 2;//相同日期合并一张
//	public static final int HEBING_GZ_03 = 3;//相同单位相同日期合并一张
    //凭证合并规则
    int HEBING_GZ_01 = 1;//不合并
    int HEBING_GZ_02 = 2;//同期间 相同科目合并   ——》按往来单位生成凭证
    int HEBING_GZ_03 = 3;//同期间 相同科目不合并  ——》勾选清单生成一张凭证
    //分录规则
    int HEBING_FL_02 = 2;//同方向分录合并
    int HEBING_FL_03 = 3;//分录不合并
    int HEBING_FL_04 = 4;//同方向、不同方向分录合并

    /*****************报错编码***********************/
    String ERROR_FLAG = "-150";//需要和前台二次交互
}
