package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;

import java.util.List;

public interface IPrebillService {
	
	public List<OcrInvoiceVO> queryNotCategory()throws DZFWarpException;
	
	public List<OcrInvoiceDetailVO> queryDetailByInvList(List<OcrInvoiceVO> list)throws DZFWarpException;
	
	public List<OcrInvoiceDetailVO> queryDetailByCondition(String condition) throws DZFWarpException ;
	
	public void updateOcrInv(OcrInvoiceVO vo)throws DZFWarpException;
	
	public void updateErrorDesc(OcrInvoiceVO vo)throws DZFWarpException;
	
	public void updateOcrInvDetail(OcrInvoiceDetailVO vo)throws DZFWarpException;
	
	public void updateInvoiceById(List<OcrInvoiceVO> list)throws DZFWarpException;
	
	public void updateInvoiceDetailByInvId(List<OcrInvoiceVO> list)throws DZFWarpException;
	
	public List<OcrInvoiceVO> queryOcrInvoiceVOByBillId(List<String> arrayList, String pk_corp, String period)throws DZFWarpException;
	
	public List<OcrInvoiceVO> queryOcrVOIsOnly(OcrInvoiceVO ocrInvoiceVO, String pk_corp)throws DZFWarpException;
	
	public List<OcrInvoiceVO> queryOcrVOByPkcorpAndPeriod(String pk_corp, String period)throws DZFWarpException;
}
