package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.report.query.cwzb.FzYeQueryVO;
import com.dzf.zxkj.report.vo.cwzb.FzYeVO;

import java.util.List;

public interface IFzhsYebService {
    List<FzYeVO> getFzYebVOs(FzYeQueryVO queryVO) throws Exception ;
}
