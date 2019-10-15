package com.dzf.zxkj.platform.service.image;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

public interface ICreatePZByFixedService {

	public void newSaveVoucherFromPic(ImageGroupVO groupvo, boolean flag) throws DZFWarpException;
}
