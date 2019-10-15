package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.SrzcBVO;

/**
 * 收入支出(事业单位)
 *
 * @author zhangj
 */
public interface ISrzcReport {

    SrzcBVO[] queryVos(QueryParamVO paramvo) throws DZFWarpException;

}
