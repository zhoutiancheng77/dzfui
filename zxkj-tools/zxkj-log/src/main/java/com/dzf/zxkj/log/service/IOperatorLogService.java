package com.dzf.zxkj.log.service;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.log.model.LogRecordVo;
import com.dzf.zxkj.log.vo.LogQueryParamVO;

import java.util.List;

/**
 * 获取操作日志
 *
 * @author zhangj
 */
public interface IOperatorLogService {

    List<LogRecordVo> query(LogQueryParamVO paramvo) throws DZFWarpException;

    void saveLog(String pk_corp, String username, String ip, Integer type, String msg, Integer ident, String userid) throws DZFWarpException;
}
