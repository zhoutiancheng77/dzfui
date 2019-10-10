package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.report.query.cwzb.MultiColumnQueryVO;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnService;
import org.springframework.stereotype.Service;

@Service
public class MultiColumnServiceImpl implements IMultiColumnService {

    @Override
    public Object[] getMulColumns(MultiColumnQueryVO vo) throws Exception {
        return new Object[0];
    }
}
