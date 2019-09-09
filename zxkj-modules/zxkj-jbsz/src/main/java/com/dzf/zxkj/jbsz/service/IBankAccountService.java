package com.dzf.zxkj.jbsz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.jbsz.vo.BankAccountVO;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
public interface IBankAccountService {
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
     * @throws DZFWarpException
     */
    void update(BankAccountVO vo) throws DZFWarpException;

    /**
     * 查询
     *
     * @param pk_corp
     * @param isnhsty
     * @return
     * @throws DZFWarpException
     */
    List<BankAccountVO> query(String pk_corp, String isnhsty) throws DZFWarpException;

    IPage<BankAccountVO> query(Page<BankAccountVO> page, String pk_corp, String isnhsty) throws DZFWarpException;

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

    List<BankAccountVO> queryByCode(String code, String pk_corp) throws DZFWarpException;
}
