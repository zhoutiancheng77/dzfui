package com.dzf.zxkj.app.service.pub;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;

import java.util.List;

/**
 * 参数接口
 */
public interface IParameterSetService {


    //根据公司、编码查询参数值
    YntParameterSet queryParamterbyCode(String pk_corp, String paramcode) throws DZFWarpException;

}
