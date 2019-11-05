package com.dzf.zxkj.platform.service.image.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片压缩处理器
 * @author Administrator
 *
 */
public class ImageCompressProcessor implements ImageProcessor {
	private int width;
	private int height;
	
	public ImageCompressProcessor(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		if(image.getDecodeOption().InJustDecodeBounds)
			throw new BusinessException("对象ImageObject的decodeOption.InJustDecodeBounds为true，不允许处理图片");
		
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		Image scaleImg = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		g.drawImage(scaleImg,  0, 0, width, height, null);  
	    g.dispose();  
	    image.getImage().flush();
	    image.setImage(buffImg);
	}

}
