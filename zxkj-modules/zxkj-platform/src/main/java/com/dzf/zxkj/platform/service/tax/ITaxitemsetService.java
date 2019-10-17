package com.dzf.zxkj.platform.service.tax;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.tax.TaxitemPzShowVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;

import java.util.List;

/**
 * 税目节点操作
 */

public interface ITaxitemsetService {

    /**
     * 查询符合当前科目的税目，科目维护，新增、修改 使用
     *
     * @param pk_corp
     * @param subjcode
     * @return
     * @throws DZFWarpException
     */
    List<TaxitemVO> queryItembycode(String userid, String pk_corp, String subjcode) throws DZFWarpException;

    /**
     * 查询符合条件的存在税目的科目信息，凭证维护使用
     *
     * @param userid
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<TaxitemPzShowVO> queryPzShow(String userid, String pk_corp) throws DZFWarpException;

    /**
     * 查询符合条件的存在税目的科目信息，当科目全部加载使用
     *
     * @param userid
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<TaxitemPzShowVO> queryKMShow(String userid, String pk_corp) throws DZFWarpException;

    /**
     * 查询集团税目档案
     *
     * @return
     * @throws DZFWarpException
     */
    List<TaxitemVO> queryAllTaxitems() throws DZFWarpException;

}
