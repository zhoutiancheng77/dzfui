package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.report.query.cwzb.NmnyHZQueryVO;
import com.dzf.zxkj.report.query.cwzb.NmnyMXQueryVO;
import com.dzf.zxkj.report.vo.cwzb.NumMnyDetailVO;
import com.dzf.zxkj.report.vo.cwzb.NumMnyGlVO;

import java.util.List;

public interface INummnyService {
    List<NumMnyDetailVO> getNumMnyDetailVO(String startDate, String enddate,
                                           String pk_inventory, NmnyMXQueryVO paramvo, String pk_corp, String user_id, String pk_bz, String xsfzhs, DZFDate begdate) throws Exception;
    List<NumMnyGlVO> getNumMnyGlVO(NmnyHZQueryVO paramVo) throws  Exception ;
}
