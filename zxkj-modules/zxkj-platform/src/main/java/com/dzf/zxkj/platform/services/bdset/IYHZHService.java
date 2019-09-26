package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;

import java.util.List;

public interface IYHZHService {

    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws DZFWarpException
     */
    BankAccountVO save(BankAccountVO vo) throws DZFWarpException;

    /**
     * 更新
     *
     * @param vo
     * @param fileds
     * @throws DZFWarpException
     */
    void update(BankAccountVO vo, String[] fileds) throws DZFWarpException;

    /**
     * 查询
     *
     * @param pk_corp
     * @param isnhsty
     * @return
     * @throws DZFWarpException
     */
    List<BankAccountVO> query(String pk_corp, String isnhsty) throws DZFWarpException;

    /**
     * 查询
     *
     * @param id
     * @return
     * @throws DZFWarpException
     */
    BankAccountVO queryById(String id) throws DZFWarpException;

    /**
     * 删除
     *
     * @param vo
     * @throws DZFWarpException
     */
    void delete(BankAccountVO vo) throws DZFWarpException;

    BankAccountVO[] queryByCode(String code, String pk_corp) throws DZFWarpException;
}
