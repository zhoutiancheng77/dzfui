package com.dzf.zxkj.platform.services.image;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

public interface IOcrAutoParseService {

	public void processOcrPase(ImageGroupVO groupVO) throws DZFWarpException;
}
