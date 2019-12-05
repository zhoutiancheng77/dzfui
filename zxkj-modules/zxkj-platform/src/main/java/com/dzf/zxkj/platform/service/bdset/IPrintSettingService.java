package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;

/**
 * 打印设置
 */
public interface IPrintSettingService {

    /**
     * 保存，（新增保存，修改保存）
     * @param vo
     * @throws DZFWarpException
     */
    void save(PrintSettingVO vo) throws DZFWarpException;

    /**
     * 保存，（新增保存，修改保存）,适合多个字段
     *  vo里面的updatecolumn 来判断更新的值，如果为空，则更新不了
     * @param vo
     * @throws DZFWarpException
     */
    void saveMulColumn(PrintSettingVO vo) throws DZFWarpException;

    PrintSettingVO query(String pk_corp, String user_id, String nodeName)
            throws DZFWarpException;
}
