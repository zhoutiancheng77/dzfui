package com.dzf.zxkj.platform.services.sys;


import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpCredenVO;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.CorpTaxInfoVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 公司目录的service类
 * @author zhangj
 *
 */
public interface IBDCorpService {

	/**
	 * 公司查询
	 * @param queryvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] queryCorp(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

	public CorpVO[] queryCorpRef(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;


	public int countCorp(QueryParamVO queryvo) throws DZFWarpException;

	public CorpVO[] queryCorpAndSelf(QueryParamVO queryvo) throws DZFWarpException;

	/**
	 * 保存公司目录
	 * @param accountvo
	 * @throws DZFWarpException
	 */
	public CorpVO insertCorpVO(CorpVO accountvo) throws DZFWarpException;

	/**
	 * 保存增值客户
	 * @param accountvo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO insertZzCorpVO(CorpVO accountvo) throws DZFWarpException;

	/**
	 * 更新数据
	 * @param accountvo
	 * @throws DZFWarpException
	 */
	public void updateCorpVO(CorpVO accountvo) throws DZFWarpException;

	/**
	 * 更新固定资产
	 * @param corpVO
	 * @throws DZFWarpException
	 */
	public void updateHflagSer(CorpVO corpVO) throws DZFWarpException;

	/**
	 * 更新是否建账
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void updateHasaccountSer(CorpVO corpvo) throws DZFWarpException;
	/**
	 * 更新库存
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void updateBuildicSer(CorpVO corpvo) throws DZFWarpException;

	/**
	 * 停用服务
	 */
	public void updateService(CorpVO corpvo) throws DZFWarpException;
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
	 * @param corpVO
	 * @throws DZFWarpException
	 * @throws UifException
	 */
	public void saveCorpAccount(CorpVO corpVO) throws  DZFWarpException;

	/**
	 * 删除
	 * 只能删除未建账的公司
	 */
	public void deleteCorp(CorpVO corpVO, String fathercorp) throws DZFWarpException;

	/**
	 * 根据公司主键查询
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryByPk(String[] pk_corp) throws DZFWarpException;

	public CorpVO queryByID(String pk_corp) throws DZFWarpException;

	/**
	 * 	查询公司数据总条数
	 * @param queryvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public int queryCorpTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

	public CorpVO[] queryCorpRefTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 查询附件信息
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpDocVO[] queryChild(CorpDocVO vo) throws DZFWarpException;

	/**
	 * 查询提醒信息
	 * @param credenvo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpCredenVO[] queryCorpCreden(CorpVO corpVO) throws DZFWarpException;

	/**
	 * 保存提醒信息
	 * @param corpCredenVO
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] insertCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

	/**
	 * 更新提醒信息
	 * @param corpCredenVO
	 * @throws DZFWarpException
	 */
	public void updateCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

	/**
	 * 删除提醒信息
	 * @param corpCredenVOs
	 * @throws DZFWarpException
	 */
	public void deleteCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException;

	/**
	 * 查询上传附件
	 * @param corpVO
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpDocVO[] queryCorpDoc(CorpVO corpVO) throws DZFWarpException;

	/**
	 * 删除上传附件
	 * @param corpDocVOs
	 * @throws DZFWarpException
	 */
	public void deleteCorpDoc(CorpDocVO[] corpDocVOs, String delFilePath) throws DZFWarpException;

	/**
	 * 通过主键查询附件信息
	 * @param pk_doc
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpDocVO queryCorpDocByID(String pk_doc) throws DZFWarpException;

	/**
	 * 查询区域信息
	 * @param parenter_id
	 * @return
	 * @throws DZFWarpException
	 */
	public List queryArea(String parenter_id)throws DZFWarpException;

	/**
	 * 保存方法
	 *
	 * @param corpvo
	 * @param addvos
	 * @param addTaxvos WJX 税率信息
	 * @param docvos
	 * @param files
	 * @param uploadPath
	 * @throws DZFWarpException
	 */
	public void saveCorp(CorpVO corpvo, File[] files, String uploadPath)throws DZFWarpException;

	public void updateCorp(CorpVO corpvo, HashMap sendData, File[] files, String uploadPath) throws DZFWarpException;

	/**
	 * 反建账
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void updateJzCancel(CorpVO corpvo, String topCorpID) throws DZFWarpException;

	/**
	 * 停用固定资产
	 * @param corpVO
	 * @throws DZFWarpException
	 */
	public void updateHflagTy(CorpVO corpvo) throws DZFWarpException;

	/**
	 * 停用库存
	 * @param corpco
	 * @throws DZFWarpException
	 */
	public void updateBuildicTy(CorpVO corpco) throws DZFWarpException;

	/**
	 * 查询税率信息
	 *  WJX CorpTaxInfoVO[]
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpTaxInfoVO[] queryCorpTaxInfo(CorpVO corpVO) throws DZFWarpException;

	/**
	 * 查询益世客户参照
	 * @param paramvo
	 * @param uservo
	 * @return
	 */
	public CorpVO[] queryCorpRefByYS(QueryParamVO paramvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 修改科目方案
	 * @param corpVO
	 * @throws DZFWarpException
	 */
	public void updateAccountType(CorpVO corpVO) throws DZFWarpException;

	/***
	 * 查询停用原因
	 * @param pk_corp
	 * @return
	 */
	public List<String> queryStopReason(String pk_corp)throws DZFWarpException;

	/***
	 * 批量更新主管会计
	 * @param pk_corp
	 * @return
	 */
	public int updateChargeAccount(String[] pk_corps, String chargeAcount)throws DZFWarpException;

	/***
	 * 重新生成激活码
	 * @param pk_corp
	 * @return
	 */
	public int updateActiveCode(String pk_gs, String code)throws DZFWarpException;

	/**
	 * 送票提醒
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void updateTicRemin(CorpVO corpvo) throws DZFWarpException;

	/**
	 * 更新利率
	 * @param pk_corp
	 * @param radio
	 * @throws DZFWarpException
	 */
	public void updateTaxradio(String pk_corp, String radio) throws DZFWarpException;

	/**
	 * 单据处理方式
	 * @param corps
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void processPicMode(CorpVO[] corps, CorpVO corpvo) throws DZFWarpException;

	/**
	 * 查询客户的子信息
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO queryHBodys(CorpVO corpvo) throws DZFWarpException;

	/**
	 * 根据客户名称查询客户信息
	 * @param fathercorp
	 * @param uname
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO queryCorpByName(String fathercorp, String uname) throws DZFWarpException;
	
	   /**
     * 查询代账机构下所有已建账客户数，包括分支机构
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public Integer queryAcountCorps(String pk_corp) throws DZFWarpException;
    
    /**
     * 启用总账存货
     * @param corpvo
     * @throws DZFWarpException
     */
    public void updateStGenledic(CorpVO corpvo) throws DZFWarpException;
    
    /**
     * 停用总账存货
     * @param corpvo
     * @throws DZFWarpException
     */
    public void updateSpGenledic(CorpVO corpvo) throws DZFWarpException;
	
}
