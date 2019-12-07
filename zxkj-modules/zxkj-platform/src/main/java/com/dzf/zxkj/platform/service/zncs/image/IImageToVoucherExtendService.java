package com.dzf.zxkj.platform.service.zncs.image;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IImageToVoucherExtendService {

	public DcModelHVO getMatchModel(OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo1, ImageGroupVO grpvo)
			throws DZFWarpException;
}
