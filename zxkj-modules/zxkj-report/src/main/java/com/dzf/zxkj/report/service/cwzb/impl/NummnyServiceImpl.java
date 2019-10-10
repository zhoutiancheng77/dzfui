package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.report.query.cwzb.NmnyHZQueryVO;
import com.dzf.zxkj.report.query.cwzb.NmnyMXQueryVO;
import com.dzf.zxkj.report.service.cwzb.INummnyService;
import com.dzf.zxkj.report.vo.cwzb.NumMnyDetailVO;
import com.dzf.zxkj.report.vo.cwzb.NumMnyGlVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NummnyServiceImpl implements INummnyService {
    @Override
    public List<NumMnyDetailVO> getNumMnyDetailVO(String startDate, String enddate, String pk_inventory, NmnyMXQueryVO paramvo, String pk_corp, String user_id, String pk_bz, String xsfzhs, DZFDate begdate) throws Exception {
        return null;
    }

    @Override
    public List<NumMnyGlVO> getNumMnyGlVO(NmnyHZQueryVO paramVo) throws Exception {
        return null;
    }
}
