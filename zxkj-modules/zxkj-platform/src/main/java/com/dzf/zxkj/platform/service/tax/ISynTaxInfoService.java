package com.dzf.zxkj.platform.service.tax;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.SynTaxInfoVO;

/**
 * 同步纳税信息、核对税种等。一键报税客户端等使用
 */
public interface ISynTaxInfoService {
    /**
     * 更新纳税人信息
     * @param uservo
     * @param corps
     * @throws DZFWarpException
     */
    void updateTaxCorpVos(UserVO uservo, SynTaxInfoVO[] corps) throws DZFWarpException;

    /**
     * 回写税种报表核定信息及个体户标志等
     * @param loginCorp
     * @param uservo
     * @param corps
     * @throws DZFWarpException
     */
    void updateTaxCorpBodys(String loginCorp, UserVO uservo, SynTaxInfoVO[] corps) throws DZFWarpException;
}
