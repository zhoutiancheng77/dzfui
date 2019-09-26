package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.common.utils.Common;

import java.io.File;

/**
 * 
 * 所有图片上传路径base
 *
 */
public class ImageCommonPath {
	
	static{
		File phothf = new File(Common.imageBasePath +"headphoto");
		if(!phothf.exists())
			phothf.mkdir();
		File phothlogo = new File(Common.imageBasePath +"logo");
		if(!phothlogo.exists())
			phothlogo.mkdir();
		
		File phothpermit = new File(Common.imageBasePath +"permit");
		if(!phothpermit.exists())
			phothpermit.mkdir();
		
		File phothwebsite = new File(Common.imageBasePath +"website");
		if(!phothwebsite.exists())
			phothwebsite.mkdir();
		
		File phothorgcodecer = new File(Common.imageBasePath +"orgcodecer");//组织机构
		if(!phothorgcodecer.exists())
			phothorgcodecer.mkdir();
		
		File phothtaxregcer = new File(Common.imageBasePath +"taxregcer");//税务登记
		if(!phothtaxregcer.exists())
			phothtaxregcer.mkdir();
		
		File phothbankopcer = new File(Common.imageBasePath +"bankopcer");//税务登记
		if(!phothbankopcer.exists())
			phothbankopcer.mkdir();
		
		File phothstacer = new File(Common.imageBasePath +"stacer");//统计证书
		if(!phothstacer.exists())
			phothstacer.mkdir();
		
		File phothjoinapply = new File(Common.imageBasePath +"joinapply");//加盟
		if(!phothjoinapply.exists())
			phothjoinapply.mkdir();
		
		File phothjoinapplyid = new File(Common.imageBasePath +"joinapplyid");//加盟(身份证)
		if(!phothjoinapplyid.exists())
			phothjoinapplyid.mkdir();
		
		File dzfServiceImage = new File(Common.imageBasePath +"dzfservicecategory");//大账房增值服务
		if(!dzfServiceImage.exists())
			dzfServiceImage.mkdir();
		
		File nkjServiceImage = new File(Common.imageBasePath +"nkjserver");//牛会计
		if(!nkjServiceImage.exists())
			nkjServiceImage.mkdir();
		
		File sczlImage = new File(Common.imageBasePath +"website"+ File.separator
				+"jituan"+File.separator+"sczl"+File.separator+"lbt");
		if(!sczlImage.exists())
			sczlImage.mkdir();
		
		File sczlproduct = new File(Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"sczl"+File.separator+"product");
		if(!sczlproduct.exists())
			sczlproduct.mkdir();
	}
	//imageBasePath
	
	//保存压缩文件----压缩目录路径
	public static String getZipPath(String filename){
		String filePath = Common.imageBasePath + "zipfile"+File.separator +  forceFileExt(filename, ".zip");
		return filePath;
	}
	//保存压缩文件----证照压缩目录路径
	public static String getCertZipPath(String account,String filename){
		String filePath = Common.imageBasePath + "zipfile"+File.separator+"cert"+File.separator+account+"-"+  forceFileExt(filename, ".zip");
		return filePath;
	}
	//文件解压缩路径
	public static String getUnZipPath(String unitcode,String date){
		String filePath = Common.imageBasePath +unitcode + File.separator + date + File.separator;
		return filePath;
	}
	//证照解压缩目录路径
	public static String getCertUnZipPath(String unitcode,String date){
		String filePath = Common.imageBasePath +"cert"+File.separator+unitcode + File.separator + date + File.separator;
		return filePath;
	}
	
	//公司营业执照解压缩目录路径
	public static String getPermitUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"permit"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	//公司组织机构解压缩目录路径
	public static String getOrgCodeCerUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"orgcodecer"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	//公司税务登记解压缩目录路径
	public static String getTaxRegCerUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"taxregcer"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	
	//公司银行开户解压缩目录路径
	public static String getBankOpCerUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"bankopcer"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	
	//公司统计证书解压缩目录路径
	public static String getStaCerUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"stacer"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	//公司logo解压缩目录路径
	public static String getLogoUnZipPath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"logo"+File.separator + forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	//app用户头像路径
	public static String getUserHeadPhotoPath(String usercode,String imagetype){
		String filePath = Common.imageBasePath +"headphoto"+File.separator + forceFileExt(usercode, imagetype);
		return filePath;
	}
	
	//Common.imageBasePath
	
	//数据公司图片上传
	public static String getDataCenterPhotoPath(){
		String filePath = Common.imageBasePath.replaceAll("\\\\","/");
		return filePath;
	}
	
	
	//数据公司图片下载
	
	
	
	//app图片上传
	
	
	
	//app证照上传
	
	//企业附件目录路径
	//企业附件目录路径
	public static String getCorpFilePath(String unitcode,String imagetype){
		String filePath =  Common.imageBasePath +"upload"+File.separator +unitcode;
		return filePath;
	}

	
	//业务处理附件目录路径
	public static String getBusiFilePath(String pk,String imagetype){
		String filePath =  Common.imageBasePath +"upload" + File.separator + "dzfadmin" + File.separator + "busideal" + File.separator +pk;
		return filePath;
	}

	/**
	 * 管理平台--接单人LOGO路径
	 * @param unitcode
	 * @param pk_billperson
	 * @param imagetype
	 * @return
	 */
	public static String getBillpersonLogoPath(String pk_billperson,String imagetype){
		String filePath = Common.imageBasePath +"website"+File.separator+"dzfadmin"+File.separator+"userlogo"+File.separator+ forceFileExt(pk_billperson, imagetype);
		return filePath;
	}
	
	/**
	 * 管理平台--接单人证件路径
	 * @param unitcode
	 * @param pk_billperson
	 * @param imagetype
	 * @return
	 */
	public static String getBillpersonCardPath(String pk_billperson,String imagetype){
		String filePath = Common.imageBasePath +"website"+File.separator+"dzfadmin"+File.separator+"usercard"+File.separator + pk_billperson;// forceFileExt(pk_billperson, imagetype);
		return filePath;
	}
	
	/**
	 * 管理平台--代账公司LOGO路径
	 * @param unitcode
	 * @param pk_billperson
	 * @param imagetype
	 * @return
	 */
	public static String getAccountLogoPath(String unitcode,String imagetype){
		String filePath = Common.imageBasePath +"website"+File.separator+"dzfadmin"+File.separator+"accountlogo"+File.separator+ forceFileExt(unitcode, imagetype);
		return filePath;
	}
	
	
	/**
	 * 管理平台--服务项目信息
	 * @param busitypecode
	 * @param imagetype
	 * @return
	 */
	public static String getUnitProinfoImagePath(String unitcode,String pk_busitype,String imagetype){
		String filePath = Common.imageBasePath +"website"+File.separator+"dzfadmin"+File.separator+"proinfo"+File.separator+unitcode+File.separator+ forceFileExt(pk_busitype, imagetype);
		return filePath;
	}
	
	/**
	 * 管理平台--合同附件
	 * @param vcontcode
	 * @param imagetype
	 * @return
	 */
	public static String getContractFilePath(String unitcode,String vcontcode,String imagetype){
        String filePath =  Common.imageBasePath +"upload"+File.separator + "dzfadmin"+ File.separator+ unitcode + File.separator +"contract"+File.separator+vcontcode;
        return filePath;
    }
	
	/**
	 * 任务处理
	 * @author gejw
	 * @time 上午9:38:14
	 * @param unitcode
	 * @param taskdealid
	 * @param imagetype
	 * @return
	 */
	public static String getTaskDealFilePath(String unitcode,String taskdealid,String imagetype){
        String filePath =  Common.imageBasePath +"upload"+File.separator + "dzfadmin"+ File.separator+ unitcode + File.separator +"taskdeal"+File.separator+taskdealid;
        return filePath;
    }
	
	/**
	 * 管理平台-加盟商-付款单
	 * @param unitcode
	 * @param vcontcode
	 * @param imagetype
	 * @return
	 */
	public static String getPaybillFilePath(String unitcode,String billcode,String imagetype){
        String filePath =  Common.imageBasePath +"upload"+File.separator + "dzfadmin"+ File.separator+ unitcode + File.separator +"chnnelpay"+File.separator+billcode;
        return filePath;
    }
	
	/**
     * 管理平台-加盟商-修改客户名称
     * @param unitcode
     * @param vcontcode
     * @param imagetype
     * @return
     */
	public static String getCorpNEditFilePath(String unitcode,String pk_id,String imagetype){
        String filePath =  Common.imageBasePath +"upload"+File.separator + "dzfadmin"+ File.separator+ unitcode + File.separator +"cpnameedit"+File.separator+pk_id;
        return filePath;
    }
	
    /**
     * 管理平台-加盟商-客户签约
     * @param unitcode
     * @param pk_customno
     * @param imagetype
     * @return
     */
    public static String getCustSignFilePath(String unitcode,String pk_signinfo,String imagetype){
        String filePath =  Common.imageBasePath +"upload"+File.separator + "dzfadmin"+ File.separator+ unitcode 
                + File.separator +"chnnelcust"+File.separator + forceFileExt(pk_signinfo, imagetype);
        return filePath;
    }

	
	/**
	 * 业务处理附件解压路径
	 * @param pk_busimang
	 * @return
	 */
	public static String getBusiUnZipPath(String pk_busimang){
		String filePath = Common.imageBasePath +"dzfadminzip"+ File.separator + pk_busimang + File.separator;
		return filePath;
	}

	/**
	 * 大帐房后台--首页轮播图
	 * @param busitypecode
	 * @param imagetype
	 * @return
	 */
	public static String getHomePageImagePath(String unitcode){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"homepage"+File.separator+unitcode+File.separator;
		return filePath;
	}
	
	/**
	 * 服务类别图片
	 * @param unitcode
	 * @return
	 */
	public static String getServiceCategoryImagePath(String unitcode){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"servicecategory"+File.separator+unitcode+File.separator;
		return filePath;
	}
	public static String getDzfServiceImagePath(String unitcode){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"dzfservicecategory"+File.separator+unitcode+File.separator;
		return filePath;
	}
	
	public static String getNkjServiceImagePath(){
		String filePath = Common.imageBasePath +"nkjserver"+File.separator;
		return filePath;
	}
	/**
	 * 大账房聊天内容----图片
	 */
	public static String getChatImagePath(String userid){
		String filePath = Common.imageBasePath +"dzfchat"+File.separator+"chatimage"+File.separator+userid+File.separator;
		return filePath;
	}
	
	/**
	 * 大账房聊天用户------图像
	 */
	public static String getChatHeadPath(String userid){
		String filePath = Common.imageBasePath +"dzfchat"+File.separator+"headchat"+File.separator+userid+File.separator;
		return filePath;
	}

	/**
	 * 网站--加盟申请表
	 * @param busitypecode
	 * @param imagetype
	 * @return
	 */
	public static String getJoinApplyImagePath(){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"joinapply"+File.separator;
		return filePath;
	}
	/**
	 * 网站--加盟申请表（手持身份证正面）
	 * @param busitypecode
	 * @param imagetype
	 * @return
	 */
	public static String getJoinApplyIDImagePath(){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"joinapplyid"+File.separator;
		return filePath;
	}
	/**
	 * 为文件名添加扩展名
	 */
	private static String forceFileExt(String filename, String extname){
		if(!extname.startsWith("."))
			extname = "." + extname;
		int dotindex = filename.lastIndexOf(".");
		if(dotindex<0) return filename + extname;
		return filename.substring(0, dotindex) + extname;
	}
	
	////华道数据上传路径
	public static String getHduploadbasepath(){
//		return "c:"+File.separator+"ImageUpload";
		return File.separator+"home/CDG/CDG_DATA_DEV/srcFolder";
	}

	/**
	 * 网站税筹专栏_轮播图
	 * @return
	 */
	public static String getSczlLbtImagePath(){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"sczl"+File.separator+"lbt"+File.separator;
		return filePath;
	}
	/**
	 * 网站税筹专栏_产品
	 * @return
	 */
	public static String getSczlProImagePath(){
		String filePath = Common.imageBasePath +"website"+File.separator
				+"jituan"+File.separator+"sczl"+File.separator+"product"+File.separator;
		return filePath;
	}
}
