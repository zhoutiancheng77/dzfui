package com.dzf.zxkj.platform.service.image.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片旋转处理器
 * @author Administrator
 *
 */
public class ImageRotateProcessor implements ImageProcessor {
	private int degree;
	
	public ImageRotateProcessor(int degree){
		this.degree = degree;
	}
	
	private Rectangle CalcRotatedSize(Rectangle src, int angel) {  
        // if angel is greater than 90 degree, we need to do some conversion  
        if (angel >= 90) {  
            if(angel / 90 % 2 == 1){  
                int temp = src.height;  
                src.height = src.width;  
                src.width = temp;  
            }  
            angel = angel % 90;  
        }  
  
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;  
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;  
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;  
        double angel_dalta_width = Math.atan((double) src.height / src.width);  
        double angel_dalta_height = Math.atan((double) src.width / src.height);  
  
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha  
                - angel_dalta_width));  
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha  
                - angel_dalta_height));  
        int des_width = src.width + len_dalta_width * 2;  
        int des_height = src.height + len_dalta_height * 2;  
        return new Rectangle(new Dimension(des_width, des_height));
    }  
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		if(image.getDecodeOption().InJustDecodeBounds)
			throw new BusinessException("对象ImageObject的decodeOption.InJustDecodeBounds为true，不允许处理图片");
		
		int src_width = image.getWidth();  
        int src_height = image.getHeight();  
        
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(  
                src_width, src_height)), degree);  
        
        BufferedImage buffImg = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = buffImg.createGraphics();
        g.translate((rect_des.width - src_width) / 2,   (rect_des.height - src_height) / 2);  
        g.rotate(Math.toRadians(degree), src_width / 2, src_height / 2);  
  
        g.drawImage(image.getImage(), null, null);  
  
	    g.dispose();  
	    image.getImage().flush();
	    image.setImage(buffImg);
	}

}
