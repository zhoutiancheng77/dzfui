package com.dzf.zxkj.platform.model.image;

import java.io.Serializable;

public class DecodeOption implements Serializable{
	public DecodeOption(){
		this(false, 1);
	}
	
	public DecodeOption(boolean inJustDecodeBounds, int inSampleSize){
		this.InJustDecodeBounds = inJustDecodeBounds;
		this.InSampleSize = inSampleSize;
	}
	
	/**
	 * 是否仅仅为了获取图片高度和宽度
	 */
	public boolean InJustDecodeBounds;
	/**
	 * 采样密度，如果是n，则是把n个像素作为一个1个像素显示，图片大小就是原图片的1/n*1/n
	 */
	public int InSampleSize;
}
