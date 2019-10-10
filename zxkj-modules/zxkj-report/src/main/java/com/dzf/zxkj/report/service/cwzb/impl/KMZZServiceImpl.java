package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.report.query.cwzb.KmzzQueryVO;
import com.dzf.zxkj.report.service.cwzb.IKMZZService;
import com.dzf.zxkj.report.vo.cwzb.KmZzVO;
import org.springframework.stereotype.Service;

@Service
public class KMZZServiceImpl implements IKMZZService {
    @Override
    public KmZzVO[] getKMZZVOs(KmzzQueryVO vo, Object[] kmmx_objs) throws Exception {
        return new KmZzVO[0];
    }
}
