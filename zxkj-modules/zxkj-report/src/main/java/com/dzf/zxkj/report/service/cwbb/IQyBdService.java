package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.QyBdVO;

import java.util.List;

/**
 * 权益变动表-(农村合作社)
 *
 * @author admin
 */
public interface IQyBdService {

    List<QyBdVO> queryList(QueryParamVO paramvo) throws DZFWarpException;

}
