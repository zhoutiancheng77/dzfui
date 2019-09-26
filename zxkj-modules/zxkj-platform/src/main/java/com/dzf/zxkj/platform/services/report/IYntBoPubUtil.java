package com.dzf.zxkj.platform.services.report;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.List;
import java.util.Map;

public interface IYntBoPubUtil {


    //	private BDCURRENCYVO vo_cny_currency = null;
//
    String getNewVoucherNo(String pk_corp, DZFDate doperatedate)
            throws DZFWarpException;
//
//	/**
//	 * 根据公司主键获取当前日期最新凭证号
//	 * 
//	 * @param pk_corp
//	 * @return
//	 * @throws BusinessException
//	 */
//	public String getNewVoucherNo(String pk_corp) throws BusinessException;

    /**
     * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
     *
     * @param pk_trade_account
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    String getCorpAccountPkByTradeAccountPk(String pk_trade_account,
                                            String pk_corp) throws DZFWarpException;

    /**
     * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
     *
     * @param pk_trade_account
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    String getCorpAccountPkByTradeAccountPkWithMsg(String pk_trade_account,
                                                   String pk_corp, String msg) throws DZFWarpException;


    /**
     * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于所有科目
     *
     * @param pk_trade_account
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    public String getCorpAccountByTradeAccountPk(String pk_trade_account,
                                                 String pk_corp) throws DZFWarpException;

    /**
     * 根据行业会计科目主键、公司主键，获取公司会计科目主键 只适用于末级科目
     *
     * @param pk_trade_account
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
//	public String queryCAccountPkByTAccountcode(String pk_trade_account,
//			String accountcode, String pk_corp) throws BusinessException ;
//

    /**
     * 获取默人民币VO
     *
     * @return
     * @throws UifException
     */
    public String getCNYPk() throws DZFWarpException;

    /**
     * 根据固定资产取数科目类型，获取资产金额
     * @param assetcardVO
     * @param accountkind
     * @return
     */
//	 public double getAssetAccountMny(AssetcardVO assetcardVO, int
//	 accountkind);

    /**
     * 根据科目、期间查找取数科目的本期发生额(借方：借方发生累计-贷方发生累计；贷方：贷方发生累计-借方发生累计)
     *
     * @param period
     * @param pk_accsubj
     * @return
     * @throws BusinessException
     */
    public DZFDouble getThisPeriodOccurMny(String pk_corp, String period, String pk_accsubj)
            throws DZFWarpException;

    /**
     * 判断传入公司是否使用2007的科目方案
     *
     * @return
     */
    public boolean is2007AccountSchema(String pk_corp) throws DZFWarpException;

    /**
     * 判断传入公司返回科目方案
     *
     * @param pk_corp
     * @return
     */
    public String getCurrentCorpAccountSchema(String pk_corp)
            throws DZFWarpException;

    /**
     * 根据科目、期间查找取数科目的期末余额
     *
     * @param period
     * @param pk_accsubj
     * @return
     * @throws BusinessException
     */
    public DZFDouble getQmMny(String pk_corp, String period, String pk_accsubj)
            throws DZFWarpException;

    /*
     * Example: List sqhlist=[aa,bb,cc,dd,ee,ff,gg] ;
     * Test.getSqlStrByList(sqhList,3,"SHENQINGH")= "SHENQING IN
     * ('aa','bb','cc') OR SHENQINGH IN ('dd','ee','ff') OR SHENQINGH IN ('g
     *
     * 把超过1000的in条件集合拆分成数量splitNum的多组sql的in 集合。
     *
     * @param sqhList in条件的List
     *
     * @param splitNum 拆分的间隔数目,例如： 1000
     *
     * @param columnName SQL中引用的字段名例如： Z.SHENQINGH
     *
     * @return
     */
    public String getSqlStrByList(List sqhList, int splitNum, String columnName);

    /*
     * 把超过1000的in条件数组拆分成数量splitNum的多组sql的in 集合。
     *
     * @param sqhArrays in条件的数组
     *
     * @param splitNum 拆分的间隔数目,例如： 1000
     *
     * @param columnName SQL中引用的字段名例如： Z.SHENQINGH
     *
     * @return
     */
    public String getSqlStrByArrays(String[] sqhArrays, int splitNum,
                                    String columnName);

    /**
     * 根据币种查询汇率
     */
//	public ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws BusinessException;
    public Map<String, YntCpaccountVO> querykm(String pk_corp) throws DZFWarpException;

    public ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws DZFWarpException;

    //根据标准科目编码，根据当前公司的编码规则，生成当前公司新的科目编码
    public String createRulecodebyCorp(String bzaccountcode, String pk_corp) throws DZFWarpException;

    /**
     * 智能凭证或者清单取数，获取辅助编码
     *
     * @param pk_corp
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    public String getFZHsCode(String pk_corp, String pk_auacount_h) throws DZFWarpException;


    /**
     * 智能凭证或者清单取数，获取存货编码
     *
     * @param pk_corp
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    public String getInventoryCode(String pk_corp) throws DZFWarpException;

    /**
     * 智能凭证或者清单取数，获取计量单位编码
     *
     * @param pk_corp
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    public String getMeasureCode(String pk_corp) throws DZFWarpException;

    /**
     * 获取计量存货分类编码
     *
     * @param pk_corp
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    public String getInvclCode(String pk_corp) throws DZFWarpException;

    /**
     * 获取银行账户档案编码
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public String getYhzhCode(String pk_corp) throws DZFWarpException;

    /**
     * 获取行业性质
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public Integer getAccountSchema(String pk_corp) throws DZFWarpException;
}
