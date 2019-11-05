package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/10/21
 * @Modified By:
 */
public interface ISynCpaccountService {


    /**
     * 获取行业的科目信息
     * @return
     * @throws BusinessException
     */
    public YntCpaccountVO[] getHyKMVOS(String pk_corp) throws DZFWarpException;


    /**
     * 获取公司的科目信息
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    public YntCpaccountVO[] getGsKmVOS(String pk_corp,YntCpaccountVO[] addvos) throws DZFWarpException;



    /**
     * 同步客户数据
     * @param cpavos
     * @return
     * @throws BusinessException
     */
    public String saveCpacountVOS(YntCpaccountVO[] cpavos,String pk_corp ) throws DZFWarpException;


}
