package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

/**
 * 客户档案查询
 *
 * @author gejw
 * @time 2018年5月23日 上午9:45:47
 */
public interface ICorpQryService {


    /**
     * 查询顶级代账机构
     *
     * @param pk_corp 客户ID或分支机构ID
     * @return
     * @throws DZFWarpException
     * @author gejw
     * @time 上午9:46:08
     */
    public CorpVO queryTopCorp(String pk_corp) throws DZFWarpException;


}
