package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.cwzb.IFkTjBgService;
import com.dzf.zxkj.report.vo.cwzb.FkTjSetVO;
import org.springframework.stereotype.Service;

@Service
public class FkTjBgServiceImpl implements IFkTjBgService {
    @Override
    public FkTjSetVO[] query(String pk_corp, DZFDate begdate, DZFDate enddate) throws Exception {
        return new FkTjSetVO[0];
    }

    @Override
    public void save(FkTjSetVO vo) throws Exception {

    }

    @Override
    public Object[] queryZzsBg(String year, CorpVO cpvo) throws Exception {
        return new Object[0];
    }

    @Override
    public Object[] querySdsBg(String year, CorpVO cpvo) throws Exception {
        return new Object[0];
    }

    @Override
    public FkTjSetVO[] queryFktj(DZFDate enddate, CorpVO cpvo) throws Exception {
        return new FkTjSetVO[0];
    }
}
