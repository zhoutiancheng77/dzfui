package com.dzf.zxkj.platform.service.bdset;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.CodeName;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICpaccountService {

    void updateSeal(YntCpaccountVO vo) throws DZFWarpException;

    void updateUnSeal(YntCpaccountVO vo) throws DZFWarpException;

    YntCpaccountVO queryById(String id);

    YntCpaccountVO saveNew(YntCpaccountVO vo) throws DZFWarpException;

    void update(YntCpaccountVO vo, CorpVO corpvo) throws DZFWarpException;

    void deleteInfovo(YntCpaccountVO bean) throws DZFWarpException;

    YntCpaccountVO[] queryAccountByPz(String pk_corp) throws DZFWarpException;

    YntCpaccountVO[] queryAccountVOS(String pk_corp, Integer acckind) throws DZFWarpException;

    YntCpaccountVO[] queryAccountVOSByCorp(String pk_corp, Integer acckind) throws DZFWarpException;

    Map<String, List<YntCpaccountVO>> queryAccountVO(String userid, String pk_corp, boolean excludeSealed) throws DZFWarpException;

    /**
     * 增加下级科目时，校验是否被引用
     */
    boolean checkIsQuote(String corpId, String subjectCode) throws DZFWarpException;

    // 凭证引用
    boolean checkIsPzRef(String corpId, String subjectId) throws DZFWarpException;

    // 期初引用
    boolean checkBeginDataRef(String corpId, String subjectId) throws DZFWarpException;

    void saveSyncTdAccount(CorpVO cvo) throws DZFWarpException;

    String queryAccountRule(String pk_corp) throws DZFWarpException;

    /**
     * 会计科目新加、追加、取消辅助项时验证科目辅助项的最终余额
     */
    String checkFsye(YntCpaccountVO vo, CorpVO corpvo) throws DZFWarpException;

    /**
     * 上级科目是否核销科目
     *
     */
    boolean checkParentVerification(String corpId, String subjectCode) throws DZFWarpException;

    /**
     * 查询非末级的科目
     */
    List<CodeName> queryNoleafKm(String userid, String pk_corp) throws DZFWarpException;

    /**
     * 查询辅助项目
     */
    List<CodeName> queryFZhsName(String userid, String pk_corp) throws DZFWarpException;

    /**
     * 科目下级转辅助
     */
    void saveKmzhuanFz(String userid, String pk_corp, String pk_km, String pk_fz) throws DZFWarpException;

    /**
     * 新增科目前检查二级 三级 是否有权限增加
     */
    void checkBeforeAdd(String subjectId, CorpVO corpvo);

    /**
     * 增加下级，获取下级编码
     *
     * @param vo
     * @param corpvo
     * @return
     */
    String getNewSubCode(YntCpaccountVO vo, CorpVO corpvo);

    /**
     * 查询科目币种是否被引用
     *
     */
    boolean checkCurrencyRef(String corpId, String subjectId) throws DZFWarpException;
    /**
     * 查询被使用的币种pk
     *
     */
    Set<String> getCurrencyRefSet(String corpId, String subjectId) throws DZFWarpException;

    /**
     * 查询所有的现金类科目
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    String[] queryXjkm(String pk_corp) throws DZFWarpException;

    /**
     * @param corpType
     * @return [accountCode: Object]
     * 获取行业会计科目
     */
    Map<String, BdTradeAccountVO> getTradeKmMap(String corpType) throws DZFWarpException;
}
