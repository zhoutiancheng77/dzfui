package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.report.query.cwbb.XjllQueryVO;
import com.dzf.zxkj.report.service.cwbb.IXjllService;
import com.dzf.zxkj.report.vo.cwbb.XjllMxvo;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class XjllServiceImpl implements IXjllService {
    @Override
    public XjllbVO[] query(XjllQueryVO vo) throws Exception {
        return new XjllbVO[0];
    }

    @Override
    public Map<String, XjllbVO[]> queryEveryPeriod(XjllQueryVO vo) throws Exception {
        return null;
    }

    @Override
    public XjllMxvo[] getXJllMX(String period, String pk_corp, String hc) throws Exception {
        return new XjllMxvo[0];
    }

    @Override
    public XjllbVO[] getXjllDataForCwBs(String qj, String corpIds, String qjlx) throws Exception {
        return new XjllbVO[0];
    }

    @Override
    public List<YntXjllqcyePageVO> bulidXjllQcData(List<YntXjllqcyePageVO> listvo, String pk_corp) throws Exception {
        return null;
    }
}
