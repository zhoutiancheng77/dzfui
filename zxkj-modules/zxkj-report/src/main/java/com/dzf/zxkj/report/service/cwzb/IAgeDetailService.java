package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.report.query.cwzb.AgeDetailQueryVO;
import com.dzf.zxkj.report.vo.cwzb.AgeBalanceVO;
import com.dzf.zxkj.report.vo.cwzb.AgeReportResultVO;

import java.util.List;
import java.util.Map;

public interface IAgeDetailService {
    AgeReportResultVO query (AgeDetailQueryVO param) throws Exception;
    Map<String, AgeBalanceVO> queryDetails (AgeDetailQueryVO param, List<String> periods) throws Exception;
}
