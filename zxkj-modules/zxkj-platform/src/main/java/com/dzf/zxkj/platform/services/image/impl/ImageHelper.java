package com.dzf.zxkj.platform.services.image.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.DecodeOption;
import com.dzf.zxkj.platform.model.image.ImageParamBeanVO;

public class ImageHelper {
	/**
	 * 克隆图片像素数组
	 * @param pixels
	 * @return
	 */
	public static int[] clonePixels(int[] pixels) {
		if(pixels == null) return null;
		int[] pixels2 = new int[pixels.length];
    	System.arraycopy(pixels, 0, pixels2, 0, pixels.length);
		return pixels2;
	}
	
	/**
	 * 获取文件扩展名
	 * @param filename
	 * @return
	 */
	public static String getFileExt(String filename){
		int dotindex = filename.lastIndexOf(".");
		if(dotindex < 0) return "";
		return filename.substring(dotindex).toLowerCase();
	}
	

	/**
	 * 获取图片处理器
	 * @param paramBeans
	 * @return
	 */
	public static ImageProcessor getImageProcesssor(ImageParamBeanVO[] paramBeans){
		if(paramBeans == null || paramBeans.length == 0) return null;
		ImageCompositeProcessor processor = new ImageCompositeProcessor();
		for(ImageParamBeanVO paramBean: paramBeans){
			switch(paramBean.getProcessorKind()){
			case ImageParamBeanVO.ImageBlurProcessor:
				processor.add(new ImageBlurProcessor(Integer.parseInt(paramBean.getParam1())));
				break;
			case ImageParamBeanVO.ImageCompressProcessor:
				processor.add(new ImageCompressProcessor(Integer.parseInt(paramBean.getParam1()), Integer.parseInt(paramBean.getParam2())));
				break;
			case ImageParamBeanVO.ImageRotateProcessor:
				processor.add(new ImageRotateProcessor(Integer.parseInt(paramBean.getParam1())));
				break;
			case ImageParamBeanVO.ImageScaleProcessor:
				processor.add(new ImageScaleProcessor(Float.parseFloat(paramBean.getParam1())));
				break;
			case ImageParamBeanVO.ImageClipProcessor:
				processor.add(new ImageClipProcessor(Integer.parseInt(paramBean.getParam1()), 
						Integer.parseInt(paramBean.getParam2()), 
						Integer.parseInt(paramBean.getParam3()), 
						Integer.parseInt(paramBean.getParam4())));
				break;
			}
		}
		return processor;
	}
	
	/**
	 * 压缩图片
	 * @param imgfile
	 * @return
	 * @throws Exception
	 */
	public static ImageObject compressImage(String imgfile) throws DZFWarpException {
		DecodeOption option = new DecodeOption();
    	option.InJustDecodeBounds = true;  // 仅仅获取图片宽度和高度
    	ImageObject srcImageObj = ImageObject.decodeFile(imgfile, option);
    	int srcWidth = srcImageObj.getWidth();
    	int srcHeight = srcImageObj.getHeight();
    	int destWidth = 600;
    	int destHeight = 400;
    	
    	// 缩小到宽度为600，或者高度为400
    	double factor = srcWidth*1.0/srcHeight;  // 宽高比
    	int inSampleSize = 1;  // 采样比率
    	if(factor>=1.5){
    		inSampleSize = srcWidth/destWidth;
    	} else {
    		inSampleSize = srcHeight/destHeight;
    	}
    	
    	if(inSampleSize<1)
    		inSampleSize = 1;
    	
        return ImageObject.decodeFile(imgfile, new DecodeOption(false, inSampleSize));
	}
}
