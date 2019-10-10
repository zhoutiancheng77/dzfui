package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.query.cwzb.ZzsMxQueryVO;
import com.dzf.zxkj.report.service.cwzb.IZzsmxService;
import com.dzf.zxkj.report.vo.cwzb.DzfpscReqBVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZzsmxServiceImpl implements IZzsmxService {
    @Override
    public List<DzfpscReqBVO> getZzsmx(ZzsMxQueryVO vo) throws Exception {
        return null;
    }

    @Override
    public long getZzsmxCount(ZzsMxQueryVO vo) throws Exception {
        return 0;
    }

    @Override
    public List<DzfpscReqBVO> saveAsVoucher(CorpVO corpvo, UserVO uservo, ZzsMxQueryVO vo, List<DzfpscReqBVO> vos) throws Exception {
        return null;
    }

    @Override
    public String getFilePath(String fphm) throws Exception {
        return null;
    }
}
