package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.SpecDeductHistVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.List;

/**
 * 公司纳税信息维护
 *
 * @author zhangj
 */
public interface IBDCorpTaxService {

    /**
     * 根据id查询纳税信息
     *
     * @param ids
     * @return
     * @throws DZFWarpException
     */
    List<CorpTaxVo> queryTaxVoByIds(String[] ids) throws DZFWarpException;


    /**
     * 根据当前用户查询
     *
     * @param paramvo
     * @param uservo
     * @return
     * @throws DZFWarpException
     */
    List<CorpTaxVo> queryTaxVoByParam(QueryParamVO paramvo, UserVO uservo) throws DZFWarpException;

    /**
     * 更新纳税信息
     *
     * @param corptaxvo
     * @throws DZFWarpException
     */
    void updateCorpTaxVo(CorpTaxVo corptaxvo, String selTaxReportIds, String unselTaxReportIds) throws DZFWarpException;


    /**
     * 查询纳税信息
     *
     * @param corpvo
     * @return
     * @throws DZFWarpException
     */
    CorpTaxVo queryCorpTaxVO(String pk_corp) throws DZFWarpException;

    /**
     * 查询纳税信息
     *
     * @param corpvo
     * @return
     * @throws DZFWarpException
     */
    CorpTaxVo queryCorpTaxVOByType(String pk_corp, String type) throws DZFWarpException;

    void saveCharge(CorpTaxVo vo, String pk_corp, String userid, StringBuffer msg) throws DZFWarpException;

    void deletechargHis(String pk_corp, String pk) throws DZFWarpException;

    List<SpecDeductHistVO> querySpecChargeHis(String pk_corp) throws DZFWarpException;

    void deleteSpecChargHis(String pk_corp, String pk) throws DZFWarpException;

    /**
     * 查询专项扣除历史，period所在年<=period的记录和period所在年之前的最近一条记录
     *
     * @param pk_corp
     * @param period
     * @return
     * @throws DZFWarpException
     */
    List<SpecDeductHistVO> querySpecChargeHis(String pk_corp, String period) throws DZFWarpException;
}
