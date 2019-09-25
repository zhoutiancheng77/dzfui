package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;

public interface IVersionMngService {

    public String[] queryCorpVersion(String pk_corp) throws DZFWarpException;
    
    /**
     * 返回会计公司对应的当前标准产品版本(小版本)
     * @param pk_corp 会计公司或者客户
     * @return 版本，null相当于免费版
     * @throws DZFWarpException
     */
    public String queryKjgsVersion(String pk_corp)throws DZFWarpException;
    
    /**
     * 返回 会计公司大版本
     * @param pk_corp 会计公司或者客户
     * @return
     * @throws DZFWarpException
     */
    public String queryKjgsBigVersion(String pk_corp)throws DZFWarpException;
    
    /**
     * 判断增值服务是否收费
     * @param pk_corp 会计公司或者客户
     * @param product IDzfServiceConst.DzfServiceProduct_04、DzfServiceProduct_05、DzfServiceProduct_03
     * @return
     * @throws DZFWarpException
     */
    public DZFBoolean isChargeByProduct(String pk_corp, String product)throws DZFWarpException;
}
