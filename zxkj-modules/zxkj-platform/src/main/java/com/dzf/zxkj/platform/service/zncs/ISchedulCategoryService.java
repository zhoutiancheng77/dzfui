package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;
import com.dzf.zxkj.platform.model.zncs.BillcategoryQueryVO;
import com.dzf.zxkj.platform.model.zncs.CheckOcrInvoiceVO;

import java.util.List;

/**
 * 分类树
 * @author mfz
 *
 */
public interface ISchedulCategoryService {

	public List<BillCategoryVO> queryTree(BillcategoryQueryVO paramVO)throws DZFWarpException;
	
	
	public void newSaveCorpCategory(List<OcrInvoiceVO> list, String pk_corp, String period, CorpVO corpVO)throws DZFWarpException;
	
	public List<OcrInvoiceVO> updateInvCategory(List<OcrInvoiceVO> list, String pk_corp, String period, CorpVO corpVO);
	
	public List<CheckOcrInvoiceVO> testingCategory(List<OcrInvoiceVO> list, String pk_corp)throws DZFWarpException;
	
	public List<BillCategoryVO> queryBillCategoryByCorpAndPeriod(String pk_corp, String period) throws DZFWarpException;
}
