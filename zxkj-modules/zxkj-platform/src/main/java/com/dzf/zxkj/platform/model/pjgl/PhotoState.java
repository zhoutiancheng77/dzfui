package com.dzf.zxkj.platform.model.pjgl;
public class PhotoState
{

    public PhotoState()
    {
    }

    /****************************图片状态*************************************/
    public static final int state0 = 0;//自由态(自由态)
    public static final int state1 = 1;//已占用(处理中)
    public static final int state2 = 2;//未知状态(未知)
    public static final int state3 = 3;//重传
    public static final int state4 = 4;//转会计
    public static final int state8 = 8;//会计工厂占用(处理中)
    public static final int state10 = 10;//切图识图（原来系统状态）
    public static final int state20 = 20;//已切图(处理中)
    public static final int state30 = 30;//已识图(处理中)
    public static final int state40 = 40;//已分拣(处理中)
    public static final int state50 = 50;//已打包(处理中)
    public static final int state60 = 60;//已发送(处理中)
    public static final int state70 = 70;//已接收(处理中)
    public static final int state80 = 80;//已退回(已退回)
    public static final int state90 = 90;//预凭证(处理中)
    public static final int state100 = 100;//已生成凭证(已制证)
    public static final int state101 = 101;//已生成凭证(但凭证为暂存态)
    public static final int state102 = 102;//已生成凭证(信息重复)
    public static final int state200 = 200;//暂存态，（走大账房app的审批流）
    
    public static final int state201 = 201;
    
    public static final int state205 = 205;//票据作废
    
    /****************************图片来源*************************************/
    public static final int SOURCEMODE_01 = 1;//手机上传标识
    public static final int SOURCEMODE_05 = 5;//票通图片
    public static final int SOURCEMODE_10 = 10; //  ocr图片
    public static final int SOURCEMODE_15 = 15;//微信公众号上传标识
    public static final int SOURCEMODE_20 = 20;//扫描仪上传
    
    
    /****************************图片处理方式*********************************
	 * 单据处理方式（图片） 0、会计公司自行处理	 1、智能识别 	2、平台审单	
	 * 4、自身审单  3、 智能识别 + 平台审单	5、智能识别 + 自身审核 6、智能识别 + 清单入账
	 * 7、智能识别 （新版智能识别）
	 */
	public static int TREAT_TYPE_0 = 0;
	
	//以下不要了
//	public static int TREAT_TYPE_1 = 1;
//	public static int TREAT_TYPE_2 = 2;
//	public static int TREAT_TYPE_3 = 3;
//	public static int TREAT_TYPE_4 = 4;
//	public static int TREAT_TYPE_5 = 5;
//	public static int TREAT_TYPE_6 = 6;
	
	
	public static int TREAT_TYPE_7 = 7;
    
    public static String getUserState(int stateInt){
    	String userState = "";
    	if(stateInt == 0){
    		userState = "未处理";
    	}else if(stateInt == 2){
    		userState = "未知";
    	}else if(stateInt == 80){
    		userState = "已退回";
    	}else if(stateInt == 100 || stateInt == state101){
    		userState = "已制证";
    	}else{
    		userState = "处理中";
    	}
    	
    	return userState;
    }
}