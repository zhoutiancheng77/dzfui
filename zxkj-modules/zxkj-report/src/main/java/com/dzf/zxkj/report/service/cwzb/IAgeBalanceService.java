package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.AgeBalanceQueryVO;
import com.dzf.zxkj.report.vo.cwzb.AccountAgeVO;
import com.dzf.zxkj.report.vo.cwzb.AgeReportResultVO;

import java.util.List;

public interface IAgeBalanceService {
    AgeReportResultVO query (AgeBalanceQueryVO param) throws Exception;
    List<YntCpaccountVO> queryAccount (String pk_corp) throws Exception;
    AccountAgeVO[] queryAgeSetting (String pk_corp) throws Exception;
}
