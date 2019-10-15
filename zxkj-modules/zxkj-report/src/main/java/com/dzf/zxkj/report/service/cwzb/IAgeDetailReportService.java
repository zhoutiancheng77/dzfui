package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.platform.model.report.AgeBalanceVO;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;

import java.util.List;
import java.util.Map;

public interface IAgeDetailReportService {
	public AgeReportResultVO query(AgeReportQueryVO param) throws DZFWarpException;
	public Map<String, AgeBalanceVO> queryDetails(AgeReportQueryVO param, List<String> periods) throws DZFWarpException;
}
