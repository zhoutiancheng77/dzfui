package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.cqtc.CQTaxReportVO;
import com.dzf.zxkj.platform.model.tax.cqtc.CqtcParamVO;

import java.util.List;

public interface ICqTaxInfoService {
	
	
	public String processSendTaxReport(CQTaxReportVO taxInfo) throws DZFWarpException;

	public void saveInitReport(CqtcParamVO vo) throws DZFWarpException;

	public String saveTaxInfo(List<CorpVO> corp_list) throws DZFWarpException;
	
	public void saveSbzt10102(CorpVO corpvo) throws DZFWarpException;

}
