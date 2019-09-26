package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片缩放处理器
 * @author Administrator
 *
 */
public class ImageScaleProcessor implements ImageProcessor {
	private float scale = 1.0f;

	public ImageScaleProcessor(float scale){
		this.scale = scale;
	}
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		if(image.getDecodeOption().InJustDecodeBounds)
			throw new BusinessException("对象ImageObject的decodeOption.InJustDecodeBounds为true，不允许处理图片");
		
		int imageX = 0;
		int imageY = 0;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		int width = (int)(imageWidth * scale);
		int height = (int)(imageHeight * scale);
		
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.drawImage(image.getImage(), 0, 0, width, height, imageX, imageY, imageX + imageWidth, imageY + imageHeight, null);  
	    g.dispose();  
	    image.getImage().flush();
	    image.setImage(buffImg);
	}

}
