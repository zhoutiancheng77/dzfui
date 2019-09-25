package com.dzf.zxkj.platform.services.sys;


import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UserVO;

public interface IZxkjTaxService {

    /**
     * 用户查询(当前公司的代账公司的用户)
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    UserVO[] queryUser(String pk_corp) throws DZFWarpException;

}
