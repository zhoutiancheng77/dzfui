package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATInComInvoiceVO2;

import java.util.List;

/**
 * ocr生成进项销项银行单
 * @author yin698
 *
 */
public interface IOcrBillCreate {
	public String changBillCorpInfo(List<CorpVO> corpvos, OcrImageLibraryVO imagevo, OcrInvoiceVO invvo, ImageLibraryVO imglibs, ImageGroupVO groupvo, boolean isqual)throws DZFWarpException;
	
	public void createBill(OcrInvoiceVO invvo, ImageGroupVO grpvo, ImageLibraryVO imglibs, VATInComInvoiceVO2 incomvos[])throws DZFWarpException;

}
