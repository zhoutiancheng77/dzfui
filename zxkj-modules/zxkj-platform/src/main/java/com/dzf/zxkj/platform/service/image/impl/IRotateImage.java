package com.dzf.zxkj.platform.service.image.impl;

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
