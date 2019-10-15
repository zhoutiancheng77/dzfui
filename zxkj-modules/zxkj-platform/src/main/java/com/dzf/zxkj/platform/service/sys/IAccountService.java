package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.Map;

public interface IAccountService {
    YntCpaccountVO[] queryByPk(String pk_corp) throws DZFWarpException;
    YntCpaccountVO[] queryByPk(String pk_corp, int kind) throws DZFWarpException;
    Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) throws DZFWarpException;
}
