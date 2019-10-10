package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.report.query.cwbb.CwgyInfoQueryVO;
import com.dzf.zxkj.report.service.cwbb.ICwgyInfoService;
import com.dzf.zxkj.report.vo.cwbb.CwgyInfoVO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CwgyInfoServiceImpl implements ICwgyInfoService {

    @Override
    public CwgyInfoVO[] getCwgyInfoVOs(CwgyInfoQueryVO queryVO) throws Exception {
        return new CwgyInfoVO[0];
    }

    @Override
    public Map<String, CwgyInfoVO[]> getCwgyInfoVOs(String year, String pk_corp, Object[] obj) throws Exception {
        return null;
    }
}
