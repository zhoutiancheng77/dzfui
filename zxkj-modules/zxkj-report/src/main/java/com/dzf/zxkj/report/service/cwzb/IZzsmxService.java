package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.query.cwzb.ZzsMxQueryVO;
import com.dzf.zxkj.report.vo.cwzb.DzfpscReqBVO;

import java.util.List;

public interface IZzsmxService {
    List<DzfpscReqBVO> getZzsmx(ZzsMxQueryVO vo) throws Exception;

    long getZzsmxCount(ZzsMxQueryVO vo) throws Exception;

    List<DzfpscReqBVO> saveAsVoucher(CorpVO corpvo, UserVO uservo, ZzsMxQueryVO vo, List<DzfpscReqBVO> vos) throws Exception;

    String getFilePath(String fphm) throws Exception;
}
