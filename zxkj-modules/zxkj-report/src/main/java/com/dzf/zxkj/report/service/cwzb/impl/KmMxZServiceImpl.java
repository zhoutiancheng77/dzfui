package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.KmmxQueryVO;
import com.dzf.zxkj.report.service.cwzb.IKmMxZService;
import com.dzf.zxkj.report.vo.cwzb.KmMxZVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KmMxZServiceImpl implements IKmMxZService {
    @Override
    public KmMxZVO[] getKMMXZVOs(KmmxQueryVO vo, Object[] qryobj) throws Exception {
        return new KmMxZVO[0];
    }

    @Override
    public KmMxZVO[] getKMMXZConFzVOs(KmmxQueryVO vo, Object[] qryobjs) throws Exception {
        return new KmMxZVO[0];
    }

    @Override
    public Object[] getKMMXZVOs1(KmmxQueryVO vo, boolean b) throws Exception {
        return new Object[0];
    }

    @Override
    public String getKmTempTable(KmmxQueryVO vo) {
        return null;
    }

    @Override
    public List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz, DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere) throws Exception {
        return null;
    }

    @Override
    public Map<String, YntCpaccountVO> getKM(String pk_corp, String kmwhere) throws Exception {
        return null;
    }

    @Override
    public List<KmMxZVO> getResultVos(Map<String, KmMxZVO> qcmapvos, Map<String, List<KmMxZVO>> fsmapvos, HashMap<String, DZFDouble[]> corpbegqcmap, List<String> periods, Map<String, YntCpaccountVO> kmmap, String pk_corp, DZFBoolean ishowfs, String kmslast, DZFBoolean btotalyear) throws Exception {
        return null;
    }

    @Override
    public YntCpaccountVO[] getkm_first(String kms_first, String pk_corp) {
        return new YntCpaccountVO[0];
    }
}
