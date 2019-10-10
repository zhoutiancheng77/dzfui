package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.report.query.cwzb.AgeDetailQueryVO;
import com.dzf.zxkj.report.service.cwzb.IAgeDetailService;
import com.dzf.zxkj.report.vo.cwzb.AgeBalanceVO;
import com.dzf.zxkj.report.vo.cwzb.AgeReportResultVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgeDetailServiceImpl implements IAgeDetailService {
    @Override
    public AgeReportResultVO query(AgeDetailQueryVO param) throws Exception {
        return null;
    }

    @Override
    public Map<String, AgeBalanceVO> queryDetails(AgeDetailQueryVO param, List<String> periods) throws Exception {
        return null;
    }
}
