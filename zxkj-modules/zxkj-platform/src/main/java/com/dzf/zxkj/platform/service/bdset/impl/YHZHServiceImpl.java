package com.dzf.zxkj.platform.service.bdset.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.RedisCacheConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.dao.YhzhDao;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YHZHServiceImpl implements IYHZHService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private YhzhDao yhzhDao;

    @Override
    @CacheInvalidate(name = RedisCacheConstant.YHZH_NAME, key = "#vo.pk_corp")
    public BankAccountVO save(BankAccountVO vo) throws DZFWarpException {
        checkExist(vo);

        BankAccountVO bvo = (BankAccountVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
        return bvo;
    }

    @Override
    @CacheInvalidate(name = RedisCacheConstant.YHZH_NAME, key = "#vo.pk_corp")
    public void update(BankAccountVO vo, String[] fields) throws DZFWarpException {
        checkExist(vo);
        singleObjectBO.update(vo, fields);

    }

    private void checkExist(BankAccountVO vo) throws DZFWarpException {
        if (StringUtil.isEmpty(vo.getBankcode())
                && StringUtil.isEmpty(vo.getBankname()))
            return;

        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();

        sf.append(" Select 1 ");
        sf.append("   From ynt_bankaccount y Where 1 = 1 ");
        if (!StringUtil.isEmpty(vo.getPrimaryKey())) {
            sf.append(" and y.pk_bankaccount <> ? ");
            sp.addParam(vo.getPrimaryKey());
        }
        sf.append("    and pk_corp = ? ");
        sf.append("    and (y.bankcode = ? or y.bankaccount = ?) ");
        sf.append("    and nvl(y.dr, 0) = 0 ");

        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getBankcode());
        sp.addParam(vo.getBankaccount());

        boolean b = singleObjectBO.isExists(vo.getPk_corp(),
                sf.toString(), sp);

        if (b) {
            throw new BusinessException("银行编码或银行账户已经存在");
        }

    }

    @Override
    public List<BankAccountVO> query(String pk_corp, String isnhsty) throws DZFWarpException {
        List<BankAccountVO> bankAccountVOS = yhzhDao.queryByPkCorp(pk_corp);

        if (!StringUtil.isEmpty(isnhsty)) {
            return bankAccountVOS.stream().filter(v -> IBillManageConstants.TINGY_STATUS != v.getState()).collect(Collectors.toList());
        }

        return bankAccountVOS;
    }

    @Override
    public List<BankAccountVO> querySigning(String pk_corp, String isnhsty, String istatus) throws DZFWarpException {
        List<BankAccountVO> bankAccountVOS = query(pk_corp, isnhsty);

        if (!StringUtils.isEmpty(istatus)) {
            return bankAccountVOS.stream().filter(v -> v.getIstatus() == istatus).collect(Collectors.toList());
        }

        return bankAccountVOS;
    }

    @Override
    public BankAccountVO queryById(String id) throws DZFWarpException {
        BankAccountVO stvo = (BankAccountVO) singleObjectBO.queryVOByID(id, BankAccountVO.class);
        return stvo;
    }

    @Override
    public void delete(BankAccountVO vo) throws DZFWarpException {
        beforeDel(vo);

        singleObjectBO.deleteObject(vo);
    }

    private void beforeDel(BankAccountVO vo) {
        if (StringUtil.isEmpty(vo.getPrimaryKey()))
            throw new BusinessException("该数据参数不完整,请检查");

        if (checkIsRef(vo)) {
            throw new BusinessException("该银行账户已被银行对账单使用，不允许删除。");
        }

        BankAccountVO stvo = queryById(vo.getPrimaryKey());

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
        StringBuffer sf = new StringBuffer();
        sf.append(" Select 1 ");
        sf.append("   From ynt_bankstatement t ");
        sf.append("  Where nvl(t.dr, 0) = 0 ");
        sf.append("    and t.pk_corp = ? ");
        sf.append("    and t.pk_bankaccount = ? ");

        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPrimaryKey());

        boolean b = singleObjectBO.isExists(vo.getPk_corp(),
                sf.toString(), sp);

        return b;
    }

    @Override
    public BankAccountVO[] queryByCode(String code, String pk_corp) throws DZFWarpException {

        List<BankAccountVO> bankAccountVOS = query(pk_corp, String.valueOf(IBillManageConstants.TINGY_STATUS));

        if (StringUtils.isNotBlank(code)) {
            return bankAccountVOS.stream().filter(v -> code.equalsIgnoreCase(v.getBankaccount())).toArray(BankAccountVO[]::new);
        }

        return bankAccountVOS.stream().toArray(BankAccountVO[]::new);
    }

}
