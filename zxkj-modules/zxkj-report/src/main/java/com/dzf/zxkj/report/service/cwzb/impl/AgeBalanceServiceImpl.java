package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.AgeBalanceQueryVO;
import com.dzf.zxkj.report.service.cwzb.IAgeBalanceService;
import com.dzf.zxkj.report.vo.cwzb.AccountAgeVO;
import com.dzf.zxkj.report.vo.cwzb.AgeReportResultVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgeBalanceServiceImpl implements IAgeBalanceService {
    @Override
    public AgeReportResultVO query(AgeBalanceQueryVO param) throws Exception {
        return null;
    }

    @Override
    public List<YntCpaccountVO> queryAccount(String pk_corp) throws Exception {
        return null;
    }

    @Override
    public AccountAgeVO[] queryAgeSetting(String pk_corp) throws Exception {
        return new AccountAgeVO[0];
    }
}
