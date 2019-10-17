package com.dzf.zxkj.common.base;

import java.util.List;

public interface IOperatorLogService {
    List<LogRecordVo> query(LogQueryParamVO paramvo) throws Exception;
    void saveLog(String pk_corp, String username, String ip, Integer type, String msg, Integer ident, String userid) throws Exception;
}
