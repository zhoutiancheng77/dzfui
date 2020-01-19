package com.dzf.zxkj.platform.service.common;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.model.SuperVO;

public interface ISecurityService {
    /**
     *
     * @param vos 数组数据（数据中包含pk_corp）
     * @param corps 公司数据
     * @param cuserid 用户 （不传默认登录用户）
     * @param isCheckData 是否校验数据有效性
     * @throws DZFWarpException
     */
    void checkSecurityData(SuperVO[] vos, String[] corps, String cuserid, boolean isCheckData)throws DZFWarpException;
}
