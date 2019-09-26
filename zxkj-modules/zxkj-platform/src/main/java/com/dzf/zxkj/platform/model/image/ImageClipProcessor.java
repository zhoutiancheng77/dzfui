package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片切块处理器
 * @author Administrator
 *
 */
public class ImageClipProcessor implements ImageProcessor {
	private int width;
	private int height;
	private int x;
	private int y;
	
	public ImageClipProcessor(int x, int y, int width, int height){
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		if(image.getDecodeOption().InJustDecodeBounds)
			throw new BusinessException("对象ImageObject的decodeOption.InJustDecodeBounds为true，不允许处理图片");
		
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.drawImage(image.getImage(), 0, 0, width, height,x, y, x+width, y+height, null);  
	    g.dispose();  
	    image.getImage().flush();
	    image.setImage(buffImg);
	}

}
