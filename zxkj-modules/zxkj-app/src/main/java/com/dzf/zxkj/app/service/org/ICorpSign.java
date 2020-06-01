package com.dzf.zxkj.app.service.org;

import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface ICorpSign {

    /**
     * 确认签约
     *
     * @param userBean
     * @param user_code
     * @return
     * @throws DZFWarpException
     */
    public CorpVO confirmSignCorp(UserBeanVO userBean, String user_code) throws DZFWarpException;
}
