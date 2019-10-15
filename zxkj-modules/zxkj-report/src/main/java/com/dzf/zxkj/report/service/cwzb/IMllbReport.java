package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.MllDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface IMllbReport {
    List<MllDetailVO> queryMllMx(QueryParamVO queryParamvo, CorpVO loginCorpInfo, String currsp);
}
