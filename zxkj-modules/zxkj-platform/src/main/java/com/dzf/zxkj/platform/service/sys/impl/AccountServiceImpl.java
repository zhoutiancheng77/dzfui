package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.dao.YntCpaccountDao;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private YntCpaccountDao yntCpaccountDao;

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp) throws DZFWarpException {
        return yntCpaccountDao.queryByPkCorp(pk_corp);
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp, int kind) throws DZFWarpException {
        return Arrays.stream(queryByPk(pk_corp)).filter(v -> v.getAccountkind() == kind).toArray(YntCpaccountVO[]::new);
    }

    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) throws DZFWarpException {
        return Arrays.stream(queryByPk(pk_corp)).collect(Collectors.toMap(YntCpaccountVO::getPrimaryKey, v -> v, (k1, k2) -> k1));
    }

    public List<YntCpaccountVO> queryVerifyAccount (String pk_corp) {
        return Arrays.stream(queryByPk(pk_corp)).filter(vo -> vo.getIsverification() != null && vo.getIsverification().booleanValue()).collect(Collectors.toList());
    }

    public List<YntCpaccountVO> queryVerifyAccountByCode (String pk_corp, String code) {
        return Arrays.stream(queryByPk(pk_corp)).filter(vo -> vo.getIsverification() != null && vo.getIsverification().booleanValue() && vo.getAccountcode().startsWith(code) && (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue())).collect(Collectors.toList());
    }
}
