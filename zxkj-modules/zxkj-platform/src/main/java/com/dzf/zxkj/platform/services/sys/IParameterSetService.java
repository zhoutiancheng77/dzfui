package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;

import java.util.List;

/**
 * 参数接口
 */
public interface IParameterSetService {

    //保存
    void saveParamter(String pk_corp, YntParameterSet vo) throws DZFWarpException;

    //查询
    List<YntParameterSet> queryParamter(String pk_corp) throws DZFWarpException;

    //根据公司、编码查询参数值
    YntParameterSet queryParamterbyCode(String pk_corp, String paramcode) throws DZFWarpException;

    //根据公司、编码查询 parametervalue字段的值  	++++不是pardetailvalue的值++++
    String queryParamterValueByCode(String pk_corp, String paramcode) throws DZFWarpException;

    //根据公司、编码查询参数值
    YntParameterSet[] queryParamterbyCodes(String[] pk_corps, String paramcode) throws DZFWarpException;

}
