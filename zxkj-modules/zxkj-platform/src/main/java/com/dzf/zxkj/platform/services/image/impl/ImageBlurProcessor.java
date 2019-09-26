package com.dzf.zxkj.platform.services.image.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.awt.*;
import java.awt.image.*;

/**
 * 图片模糊化处理器
 * @author Administrator
 *
 */
public class ImageBlurProcessor implements ImageProcessor {
	private int blurWeight;   // 模糊程度
	private ColorModel cm;
	private WritableRaster wr;
	
	public ImageBlurProcessor(int blurWeight){
		cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
		wr = cm.createCompatibleWritableRaster(1, 1);
		this.blurWeight = blurWeight;
	}
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		if(image.getDecodeOption().InJustDecodeBounds)
			throw new BusinessException("对象ImageObject的decodeOption.InJustDecodeBounds为true，不允许处理图片");
		
		
		for(int i =0; i<blurWeight; i++){
			ProcessBlur(image.getPixels(), image.getWidth(), image.getHeight());
		}
		image.setImage(createBufferedImage(image.getPixels(), image.getWidth(),  image.getHeight()));
	}
	
	private Image createBufferedImage(int[] pixels, int width, int height ){
		SampleModel sampleModel = wr.getSampleModel().createCompatibleSampleModel(width, height);
		DataBuffer dataBuffer = new DataBufferInt(pixels, width*height, 0);
		WritableRaster rgbRaster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
		return new BufferedImage(cm, rgbRaster, false, null);
	}
	
	/**
	 * 处理图片模糊化
	 */
	private void ProcessBlur(int[] pixels, int width, int height){
		int p1, p2, p3, p4, p5, p6, p7, p8, p9;
		int[] pixels2 =ImageHelper.clonePixels(pixels);
		int offset, rsum=0, gsum=0, bsum=0;
        int rowOffset = width;
		for (int y=1; y<height-1; y++) {
			offset = 1 + y * width;
			p1 = 0;
			p2 = pixels2[offset-rowOffset-1];
			p3 = pixels2[offset-rowOffset];
			p4 = 0;
			p5 = pixels2[offset-1];
			p6 = pixels2[offset];
			p7 = 0;
			p8 = pixels2[offset+rowOffset-1];
			p9 = pixels2[offset+rowOffset];

			for (int x=1; x<width-1; x++) {
				p1 = p2; p2 = p3;
				p3 = pixels2[offset-rowOffset+1];
				p4 = p5; p5 = p6;
				p6 = pixels2[offset+1];
				p7 = p8; p8 = p9;
				p9 = pixels2[offset+rowOffset+1];
				rsum = (p1 & 0xff0000) + (p2 & 0xff0000) + (p3 & 0xff0000) + (p4 & 0xff0000) + (p5 & 0xff0000)
					+ (p6 & 0xff0000) + (p7 & 0xff0000) + (p8 & 0xff0000) + (p9 & 0xff0000);
				gsum = (p1 & 0xff00) + (p2 & 0xff00) + (p3 & 0xff00) + (p4 & 0xff00) + (p5 & 0xff00)
					+ (p6 & 0xff00) + (p7 & 0xff00) + (p8 & 0xff00) + (p9 & 0xff00);
				bsum = (p1 & 0xff) + (p2 & 0xff) + (p3 & 0xff) + (p4 & 0xff) + (p5 & 0xff)
					+ (p6 & 0xff) + (p7 & 0xff) + (p8 & 0xff) + (p9 & 0xff);
				pixels[offset++] = 0xff000000 | ((rsum/9) & 0xff0000) | ((gsum/9) & 0xff00) | (bsum/9);
			}
		}
	}
}
