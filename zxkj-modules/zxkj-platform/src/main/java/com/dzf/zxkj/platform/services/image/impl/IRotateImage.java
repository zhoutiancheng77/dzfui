package com.dzf.zxkj.platform.services.image.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;

public interface IRotateImage {
	/**
	 * 旋转图片
	 * @param filePath
	 * @param degree
	 * @throws BusinessException
	 */
	public void rotateImage(String filePath, int degree) throws DZFWarpException;
}
