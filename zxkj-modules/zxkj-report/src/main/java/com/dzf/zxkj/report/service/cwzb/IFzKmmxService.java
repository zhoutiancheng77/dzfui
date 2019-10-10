package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.report.query.cwzb.FzKmMxQueryVO;
import com.dzf.zxkj.report.vo.cwzb.FzKmmxVO;

import java.util.List;
import java.util.Map;

public interface IFzKmmxService {
    Object[] getFzkmmxVos(FzKmMxQueryVO queryVO, DZFBoolean bshowcolumn) throws Exception;
    Map<String, List<FzKmmxVO>> getAllFzKmmxVos(FzKmMxQueryVO paramavo) throws Exception;
}
