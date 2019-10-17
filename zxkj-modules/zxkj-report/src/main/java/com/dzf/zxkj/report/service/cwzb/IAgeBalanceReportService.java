package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.AccountAgeVO;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;

import java.util.List;


public interface IAgeBalanceReportService {
	public AgeReportResultVO query(AgeReportQueryVO param) throws DZFWarpException;

	public List<YntCpaccountVO> queryAccount(String pk_corp) throws DZFWarpException;

	public AccountAgeVO[] queryAgeSetting(String pk_corp) throws DZFWarpException;
	
}
