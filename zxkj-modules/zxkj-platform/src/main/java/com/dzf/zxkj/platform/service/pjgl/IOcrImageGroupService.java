package com.dzf.zxkj.platform.service.pjgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IOcrImageGroupService {

	void saveData(CorpVO corpvo1, ImageLibraryVO ilib, String pjlxType, int ibusinesstype, String filename)
			throws DZFWarpException;
}
