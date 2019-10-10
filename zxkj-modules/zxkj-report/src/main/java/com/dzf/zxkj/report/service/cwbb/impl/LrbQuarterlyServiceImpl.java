package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.report.query.cwbb.LrbQuarterlyQueryVO;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyService;
import com.dzf.zxkj.report.vo.cwbb.LrbquarterlyVO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LrbQuarterlyServiceImpl implements ILrbQuarterlyService {
    @Override
    public LrbquarterlyVO[] getLRBquarterlyVOs(LrbQuarterlyQueryVO queryVO) throws Exception {
        return new LrbquarterlyVO[0];
    }

    @Override
    public Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(LrbQuarterlyQueryVO vo, Object[] objs) throws Exception {
        return null;
    }

    @Override
    public Map<String, DZFDouble> getYearLRBquarterlyVOs(String year, String pk_corp) throws Exception {
        return null;
    }
}
