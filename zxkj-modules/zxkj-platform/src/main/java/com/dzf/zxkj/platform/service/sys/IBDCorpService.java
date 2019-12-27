package com.dzf.zxkj.platform.service.sys;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.sys.CorpCredenVO;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.CorpTaxInfoVO;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 公司目录的service类
 *
 * @author zhangj
 */
public interface IBDCorpService {

    /**
     * 公司查询
     *
     * @param queryvo
     * @param uservo
     * @return
     * @throws DZFWarpException
     */
    CorpVO[] queryCorp(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

    CorpVO[] queryCorpRef(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;


    int countCorp(QueryParamVO queryvo) throws DZFWarpException;

    CorpVO[] queryCorpAndSelf(QueryParamVO queryvo) throws DZFWarpException;

    /**
     * 保存公司目录
     *
     * @param accountvo
     * @throws DZFWarpException
     */
    CorpVO insertCorpVO(CorpVO accountvo) throws DZFWarpException;

    /**
     * 保存增值客户
     *
     * @param accountvo
     * @return
     * @throws DZFWarpException
     */
    CorpVO insertZzCorpVO(CorpVO accountvo) throws DZFWarpException;

    /**
     * 更新数据
     *
     * @param accountvo
     * @throws DZFWarpException
     */
    void updateCorpVO(CorpVO accountvo) throws DZFWarpException;

    /**
     * 更新固定资产
     *
     * @param corpVO
     * @throws DZFWarpException
     */
    void updateHflagSer(CorpVO corpVO) throws DZFWarpException;

    /**
     * 更新是否建账
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateHasaccountSer(CorpVO corpvo) throws DZFWarpException;

    /**
     * 更新库存
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateBuildicSer(CorpVO corpvo) throws DZFWarpException;

    /**
     * 停用服务
     */
    void updateService(CorpVO corpvo) throws DZFWarpException;
    /**
     * 建账逻辑
     * @param corpVO
     * @throws DZFWarpException
     */
//	public void insertCreatCorp(CorpVO corpVO) throws DZFWarpException;

    /**
     * 查询左侧数据逻辑
     */
//	public CorpVO[] queryCorp(String pk_corp)throws DZFWarpException;

    /**
     * 小企业建账前校验科目方案,并初始化会计科目
     *
     * @param corpVO
     * @throws DZFWarpException
     * @throws UifException
     */
    void saveCorpAccount(CorpVO corpVO) throws DZFWarpException;

    /**
     * 删除
     * 只能删除未建账的公司
     */
    void deleteCorp(CorpVO corpVO, String fathercorp) throws DZFWarpException;

    /**
     * 根据公司主键查询
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<CorpVO> queryByPk(String[] pk_corp) throws DZFWarpException;

    CorpVO queryByID(String pk_corp) throws DZFWarpException;

    /**
     * 查询公司数据总条数
     *
     * @param queryvo
     * @param uservo
     * @return
     * @throws DZFWarpException
     */
    int queryCorpTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

    CorpVO[] queryCorpRefTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

    /**
     * 查询附件信息
     *
     * @param vo
     * @return
     * @throws DZFWarpException
     */
    CorpDocVO[] queryChild(CorpDocVO vo) throws DZFWarpException;

    /**
     * 查询提醒信息
     *
     * @param credenvo
     * @return
     * @throws DZFWarpException
     */
    CorpCredenVO[] queryCorpCreden(CorpVO corpVO) throws DZFWarpException;

    /**
     * 保存提醒信息
     *
     * @param corpCredenVO
     * @return
     * @throws DZFWarpException
     */
    String[] insertCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

    /**
     * 更新提醒信息
     *
     * @param corpCredenVO
     * @throws DZFWarpException
     */
    void updateCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

    /**
     * 删除提醒信息
     *
     * @param corpCredenVOs
     * @throws DZFWarpException
     */
    void deleteCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

    /**
     * 查询上传附件
     *
     * @param corpVO
     * @return
     * @throws DZFWarpException
     */
    CorpDocVO[] queryCorpDoc(CorpVO corpVO) throws DZFWarpException;

    /**
     * 删除上传附件
     *
     * @param corpDocVOs
     * @throws DZFWarpException
     */
    void deleteCorpDoc(CorpDocVO[] corpDocVOs, String delFilePath) throws DZFWarpException;

    /**
     * 通过主键查询附件信息
     *
     * @param pk_doc
     * @return
     * @throws DZFWarpException
     */
    CorpDocVO queryCorpDocByID(String pk_doc) throws DZFWarpException;

    /**
     * 查询区域信息
     *
     * @param parenter_id
     * @return
     * @throws DZFWarpException
     */
    List queryArea(String parenter_id) throws DZFWarpException;

    /**
     * 保存方法
     *
     * @param corpvo
     * @param addvos
     * @param addTaxvos  WJX 税率信息
     * @param docvos
     * @param files
     * @param uploadPath
     * @throws DZFWarpException
     */
    void saveCorp(CorpVO corpvo, File[] files, String uploadPath) throws DZFWarpException;

    void updateCorp(CorpVO corpvo, HashMap sendData, File[] files, String uploadPath) throws DZFWarpException;

    /**
     * 反建账
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateJzCancel(CorpVO corpvo, String topCorpID) throws DZFWarpException;

    /**
     * 停用固定资产
     *
     * @param corpVO
     * @throws DZFWarpException
     */
    void updateHflagTy(CorpVO corpvo) throws DZFWarpException;

    /**
     * 停用库存
     *
     * @param corpco
     * @throws DZFWarpException
     */
    void updateBuildicTy(CorpVO corpco) throws DZFWarpException;

    /**
     * 查询税率信息
     * WJX CorpTaxInfoVO[]
     *
     * @return
     * @throws DZFWarpException
     */
    CorpTaxInfoVO[] queryCorpTaxInfo(CorpVO corpVO) throws DZFWarpException;

    /**
     * 查询益世客户参照
     *
     * @param paramvo
     * @param uservo
     * @return
     */
    CorpVO[] queryCorpRefByYS(QueryParamVO paramvo, UserVO uservo) throws DZFWarpException;

    /**
     * 修改科目方案
     *
     * @param corpVO
     * @throws DZFWarpException
     */
    void updateAccountType(CorpVO corpVO) throws DZFWarpException;

    /***
     * 查询停用原因
     * @param pk_corp
     * @return
     */
    List<String> queryStopReason(String pk_corp) throws DZFWarpException;

    /***
     * 批量更新主管会计
     * @param pk_corp
     * @return
     */
    int updateChargeAccount(String[] pk_corps, String chargeAcount) throws DZFWarpException;

    /***
     * 重新生成激活码
     * @param pk_corp
     * @return
     */
    int updateActiveCode(String pk_gs, String code) throws DZFWarpException;

    /**
     * 送票提醒
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateTicRemin(CorpVO corpvo) throws DZFWarpException;

    /**
     * 更新利率
     *
     * @param pk_corp
     * @param radio
     * @throws DZFWarpException
     */
    void updateTaxradio(String pk_corp, String radio) throws DZFWarpException;

    /**
     * 查询公司税负预警线
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    DZFDouble getTaxWarningRate(String pk_corp) throws DZFWarpException;

    /**
     * 单据处理方式
     *
     * @param corps
     * @param corpvo
     * @throws DZFWarpException
     */
    void processPicMode(CorpVO[] corps, CorpVO corpvo) throws DZFWarpException;

    /**
     * 查询客户的子信息
     *
     * @param corpvo
     * @return
     * @throws DZFWarpException
     */
    CorpVO queryHBodys(CorpVO corpvo) throws DZFWarpException;

    /**
     * 根据客户名称查询客户信息
     *
     * @param fathercorp
     * @param uname
     * @return
     * @throws DZFWarpException
     */
    CorpVO queryCorpByName(String fathercorp, String uname) throws DZFWarpException;

    /**
     * 查询代账机构下所有已建账客户数，包括分支机构
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    Integer queryAcountCorps(String pk_corp) throws DZFWarpException;

    /**
     * 启用总账存货
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateStGenledic(CorpVO corpvo) throws DZFWarpException;

    /**
     * 停用总账存货
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    void updateSpGenledic(CorpVO corpvo) throws DZFWarpException;

}
