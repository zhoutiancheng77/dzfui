package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.service.cwbb.IZcFzBService;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ZcFzBServiceImpl implements IZcFzBService {
    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String ishasye) throws Exception {
        return new ZcFzBVO[0];
    }

    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String[] hasyes) throws Exception {
        return new ZcFzBVO[0];
    }

    @Override
    public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) throws Exception {
        return new ZcFzBVO[0];
    }

    @Override
    public Object[] getZCFZBVOsConMsg(String period, String pk_corp, String ishasjz, String[] hasyes) throws Exception {
        return new Object[0];
    }

    @Override
    public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) throws Exception {
        return new ZcFzBVO[0];
    }

    @Override
    public List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate, String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs) throws Exception {
        return null;
    }
}
