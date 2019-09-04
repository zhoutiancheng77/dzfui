package com.dzf.zxkj.jbsz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDGenerate;
import com.dzf.zxkj.jbsz.mapper.BankAccountMapper;
import com.dzf.zxkj.jbsz.service.IBankAccountService;
import com.dzf.zxkj.jbsz.vo.BankAccountVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@Service
@Transactional
public class BankAccountServiceImpl implements IBankAccountService {

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Override
    public BankAccountVO save(BankAccountVO vo) throws DZFWarpException {
        checkExist(vo);
        vo.setPk_bankaccount(IDGenerate.getInstance().getNextID(vo.getPk_corp()));
        int i = bankAccountMapper.insert(vo);
        return i == 0 ? null : vo;
    }

    @Override
    public void update(BankAccountVO vo) throws DZFWarpException {
        checkExist(vo);
        bankAccountMapper.updateById(vo);
    }

    @Override
    public List<BankAccountVO> query(String pk_corp, String isnhsty) throws DZFWarpException {
        return bankAccountMapper.query(pk_corp, isnhsty);
    }

    @Override
    public IPage<BankAccountVO> query(Page<BankAccountVO> page, String pk_corp, String isnhsty) throws DZFWarpException {
        return bankAccountMapper.query(page, pk_corp, isnhsty);
    }

    @Override
    public BankAccountVO queryById(String id) throws DZFWarpException {
        BankAccountVO bankAccountVO = bankAccountMapper.selectById(id);
        return bankAccountVO;
    }

    @Override
    public void delete(BankAccountVO vo) throws DZFWarpException {
        beforeDel(vo);
        bankAccountMapper.deleteById(vo.getPk_bankaccount());
    }

    private void beforeDel(BankAccountVO vo) {
        if (StringUtils.isEmpty(vo.getPk_bankaccount()))
            throw new BusinessException("该数据参数不完整,请检查");

        if (checkIsRef(vo)) {
            throw new BusinessException("该银行账户已被银行对账单使用，不允许删除。");
        }

        BankAccountVO stvo = queryById(vo.getPk_bankaccount());

        if (stvo == null)
            throw new BusinessException("该数据不存在或已删除，请检查");

    }

    /**
     * 校验是否被引用
     *
     * @param vo
     * @return
     */
    private boolean checkIsRef(BankAccountVO vo) {

        int i = bankAccountMapper.existsInBankstatement(vo.getPk_corp(), vo.getBankaccount());

        return  i >= 1;
    }

    @Override
    public List<BankAccountVO> queryByCode(String code, String pk_corp) throws DZFWarpException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("code", code);
        queryWrapper.ne("state", 1);
        queryWrapper.ne("dr", 0);
        queryWrapper.ne("pk_corp", pk_corp);
        return bankAccountMapper.selectList(queryWrapper);
    }

    private void checkExist(BankAccountVO vo) throws DZFWarpException {
        Assert.notNull(vo.getBankcode(), "银行账户编码不能为空！");
        Assert.notNull(vo.getBankname(), "银行账户名称不能为空！");
        Assert.notNull(vo.getBankcode(),"银行账号不能为空");
        Assert.notNull(vo.getRelatedsubj(),"关联会计科目不能为空！");

        QueryWrapper<BankAccountVO> queryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(vo.getPk_bankaccount())) {
            queryWrapper.ne("pk_bankaccount", vo.getPk_bankaccount());
        }

        queryWrapper.eq("pk_corp", vo.getPk_corp());
        queryWrapper.eq("dr", 0);

        queryWrapper.and(wrapper -> wrapper.eq("bankcode", vo.getBankcode()).or().eq("bankaccount", vo.getBankaccount()));

        int i = bankAccountMapper.selectCount(queryWrapper);

        if (i > 0) {
            throw new BusinessException("银行编码或银行账户已经存在");
        }
    }
}
