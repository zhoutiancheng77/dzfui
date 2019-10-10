package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.report.query.cwzb.MultiColumnQueryVO;

public interface IMultiColumnService {
    Object[] getMulColumns(MultiColumnQueryVO vo) throws  Exception ;

}
