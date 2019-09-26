package com.dzf.zxkj.platform.services.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.CorpTaxInfoVO;
import com.dzf.zxkj.platform.model.tax.TaxEffeHistVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.HashMap;
import java.util.List;

/**
 * 纳税信息维护的service类
 *
 * @author zhangj
 */
public interface ICorpTaxService {


    /**
     * 保存税率信息 WJX
     */
    String[] insertCorpTaxinfo(CorpTaxInfoVO[] corpTaxinfoVOs) throws DZFWarpException;

    /**
     * 查询税率信息
     * WJX CorpTaxInfoVO[]
     *
     * @return
     * @throws DZFWarpException
     */
    CorpTaxInfoVO[] queryCorpTaxInfo(CorpVO corpVO) throws DZFWarpException;


    /****
     *
     * @param corpvo
     * @param sendData
     * @param files
     * @param uploadPath
     * @throws DZFWarpException
     */
    @SuppressWarnings("rawtypes")
    void updateCorp(CorpVO corpvo, HashMap<String, SuperVO[]> sendData, String[] taxRptids, String[] taxUnRptids) throws DZFWarpException;


    /***
     * 查询公司纳税申报表信息
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<TaxRptTempletVO> queryCorpTaxRpt(QueryParamVO paramvo) throws DZFWarpException;


    /**
     * 更新利率
     *
     * @param pk_corp
     * @param radio
     * @throws DZFWarpException
     */
    void updateTaxradio(String pk_corp, String radio) throws DZFWarpException;


    /**
     * 初始化公司的纳税申报报表模板，默认的，目前给罗力华用  20180606
     */
    void saveInitCorpRptVOs(CorpVO pvo) throws DZFWarpException;

    /**
     * 根据公司获取纳税申报报表模板的地理位置(像山东/江苏/北京等,找不到返回"通用")
     *
     * @param pvo
     * @return
     * @throws DZFWarpException
     */
    String queryTaxrpttmpLoc(CorpTaxVo taxvo) throws DZFWarpException;

    /**
     * 查询纳税信息
     *
     * @param corpvo
     * @return
     * @throws DZFWarpException
     */
    CorpTaxVo queryCorpTaxVO(String pk_corp) throws DZFWarpException;

    /**
     * @param pk_corp
     * @param period
     * @return
     * @throws DZFWarpException
     */
    TaxEffeHistVO queryTaxEffHisVO(String pk_corp, String period) throws DZFWarpException;

    /**
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<TaxEffeHistVO> queryChargeHis(String pk_corp) throws DZFWarpException;
}
