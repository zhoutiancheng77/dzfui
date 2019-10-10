package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.report.query.cwzb.FzKmMxQueryVO;
import com.dzf.zxkj.report.service.cwzb.IFzKmmxService;
import com.dzf.zxkj.report.vo.cwzb.FzKmmxVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FzKmmxServiceImpl implements IFzKmmxService {
    @Override
    public Object[] getFzkmmxVos(FzKmMxQueryVO queryVO, DZFBoolean bshowcolumn) throws Exception {
        return new Object[0];
    }

    @Override
    public Map<String, List<FzKmmxVO>> getAllFzKmmxVos(FzKmMxQueryVO paramavo) throws Exception {
        return null;
    }
}
