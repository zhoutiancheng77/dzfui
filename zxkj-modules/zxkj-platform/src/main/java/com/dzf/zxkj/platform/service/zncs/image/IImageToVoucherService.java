package com.dzf.zxkj.platform.service.zncs.image;

import java.util.List;
import java.util.Map;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IImageToVoucherService {

	public TzpzHVO creatTzpzVO(List<OcrInvoiceVO> list, ImageGroupVO grpvo) throws DZFWarpException;

	public TzpzHVO saveTzpzVO(ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo, boolean isRecog)
			throws DZFWarpException;

	public DcModelBVO[] getDcModelBVO(TzpzHVO headVO, ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo)
			throws DZFWarpException;

	public void getTzpzBVOList(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo, OcrInvoiceDetailVO[] details,
							   List<TzpzBVO> tblist, YntCpaccountVO[] accounts) throws DZFWarpException;
	
	public DcModelHVO getModelHVO(Map<String, String> trmap, CorpVO corpvo, String pk_corp)
			throws DZFWarpException;
	/**
	 * 该版先如此处理，11月份该接口与getTzpzBVOList接口合并成一个
	 */
	public void setSpeaclTzpzBVO1(TzpzHVO hvo, OcrImageLibraryVO lib, List<TzpzBVO> tblist)
			throws DZFWarpException;

}
