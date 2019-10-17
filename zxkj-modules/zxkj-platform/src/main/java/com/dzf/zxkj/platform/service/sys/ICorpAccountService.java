package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.DatatruansVO;

public interface ICorpAccountService {

    /**
     * 
     * 校验允许建账客户数
     * @param cvo 需要建账的客户
     * @param pk_corp 一级代账机构ID
     * @param totalNums 总可建账户数
     * @param isReadRedis 是否读取缓存
     * @param corpkid 客户主键
     * @throws DZFWarpException
     */
    public String checkCorpAccount(String pk_corp, Integer totalNums, boolean isReadRedis, String corpkid) throws DZFWarpException;

    /**
     * 加盟商校验是否存在有效服务合同
     * @author gejw
     * @time 下午4:15:47
     * @param pk_corp 代账公司ID
     * @param corpkid 客户ID
     * @return
     * @throws DZFWarpException
     */
    public String checkChannelContract(String pk_corp, String corpkid) throws DZFWarpException;

    /**
     *
     * 校验允许建账客户数
     * @param cvo 需要建账的客户
     * @param pk_corp 一级代账机构ID
     * @param totalNums 总可建账户数
     * @param isReadRedis 是否读取缓存
     * @param corpkid 客户主键
     * @throws DZFWarpException
     */
    public String checkCorpAccountNums(String pk_corp, Integer totalNums, boolean isReadRedis, String corpkid) throws DZFWarpException;

    /**
     * 校验机构购买服务是否到期。
     * @param pk_corp 登录公司ID
     * @return
     * @throws DZFWarpException
     */
    public void checkServicePeriod(String pk_corp) throws DZFWarpException;


    /**
     * 查询总代账机构总可建账户数
     * @param cvo
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public DatatruansVO queryBuyRecords(String pk_corp) throws DZFWarpException;
     /**
     * 缓存递减
     * @param pk_corp
     */
//    public void decr(final String pk_corp);
    
    /**
     * 是否购买智能凭证产品
     * @author gejw
     * @time 下午3:05:36
     * @param pk_corpkjgs
     * @return
     * @throws DZFWarpException
     */
    public DZFBoolean hasBuyVoucher(String pk_corpkjgs)throws DZFWarpException;
    
    /**
     * 查询最新购买记录
     * @author gejw
     * @time 上午9:33:36
     * @param cvo
     * @return
     * @throws DZFWarpException
     */
    public DatatruansVO queryBuyRecord(CorpVO cvo) throws DZFWarpException;
}
