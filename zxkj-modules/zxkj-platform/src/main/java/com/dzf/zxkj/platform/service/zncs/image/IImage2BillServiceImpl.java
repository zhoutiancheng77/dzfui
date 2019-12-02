package com.dzf.zxkj.platform.service.zncs.image;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IImage2BillServiceImpl {

	public TzpzHVO saveBill(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo, boolean isRecog)
			throws DZFWarpException;
}
