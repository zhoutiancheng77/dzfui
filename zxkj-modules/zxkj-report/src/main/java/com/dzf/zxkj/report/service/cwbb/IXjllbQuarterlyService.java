package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.query.cwbb.XjllbQuarterlyQueryVO;
import com.dzf.zxkj.report.vo.cwbb.XjllquarterlyVO;

import java.util.List;

public interface IXjllbQuarterlyService {
    List<XjllquarterlyVO> getXjllQuartervos(XjllbQuarterlyQueryVO queryVO, String jd, CorpVO corpVO) throws Exception;
}
