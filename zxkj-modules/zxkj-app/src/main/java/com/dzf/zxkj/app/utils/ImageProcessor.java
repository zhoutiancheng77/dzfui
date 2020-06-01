package com.dzf.zxkj.app.utils;


import com.dzf.zxkj.app.model.image.ImageObject;
import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 图片处理接口
 *
 */
public interface ImageProcessor {
	/**
	 * 处理图片
	 */
	public void ProcessImage(ImageObject image) throws DZFWarpException;
	
}
