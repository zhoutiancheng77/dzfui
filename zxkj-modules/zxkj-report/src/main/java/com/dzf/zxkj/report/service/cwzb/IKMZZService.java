package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.report.query.cwzb.KmzzQueryVO;
import com.dzf.zxkj.report.vo.cwzb.KmZzVO;

public interface IKMZZService {
    KmZzVO[] getKMZZVOs(KmzzQueryVO vo, Object[] kmmx_objs) throws Exception;
}
