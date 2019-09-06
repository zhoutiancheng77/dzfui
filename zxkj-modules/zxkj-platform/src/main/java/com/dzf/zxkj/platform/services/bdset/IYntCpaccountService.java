package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.Map;

/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
public interface IYntCpaccountService {
    YntCpaccountVO[] get(String pk_corp, int kind) throws DZFWarpException;
    YntCpaccountVO[] get(String pk_corp) throws DZFWarpException;
    Map<String, YntCpaccountVO> getMap(String pk_corp) throws DZFWarpException;
}
