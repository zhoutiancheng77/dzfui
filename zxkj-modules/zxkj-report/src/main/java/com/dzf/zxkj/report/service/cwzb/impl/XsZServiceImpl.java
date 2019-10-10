package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.report.query.cwzb.XsZQueryVO;
import com.dzf.zxkj.report.service.cwzb.IXsZService;
import com.dzf.zxkj.report.vo.cwzb.XsZVO;
import org.springframework.stereotype.Service;

@Service
public class XsZServiceImpl implements IXsZService {
    @Override
    public XsZVO[] getXSZVOs(String pk_corp, String kms, String kmsx, String zdr, String shr, XsZQueryVO queryvo) throws Exception {
        return new XsZVO[0];
    }
}
