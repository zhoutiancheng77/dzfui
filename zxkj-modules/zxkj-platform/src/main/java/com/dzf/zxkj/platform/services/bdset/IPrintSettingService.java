package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;

/**
 * 打印设置
 */
public interface IPrintSettingService {

    void save(PrintSettingVO vo) throws DZFWarpException;

    PrintSettingVO query(String pk_corp, String user_id, String nodeName)
            throws DZFWarpException;
}
