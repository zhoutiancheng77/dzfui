package com.dzf.zxkj.platform.service.glic;

import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.MllDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface IMllbReport {
    List<MllDetailVO> queryMllMx(QueryParamVO queryParamvo, CorpVO loginCorpInfo, String currsp);
}
