package com.dzf.zxkj.platform.service.taxrpt;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.workbench.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 纳税工作台接口
 * @author zy
 *
 */
@SuppressWarnings("all")
public interface IbsWorkbenchService {
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @param vo
	 * @param corpks
	 * @param fcorpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BsWorkbenchVO> query(QueryParamVO paramvo, UserVO vo, String[] corpks, CorpVO fcorpvo) throws DZFWarpException;
	
	public BsWorkbenchVO queryById(String id) throws DZFWarpException;

	public Map<String, BsWorkbenchVO> queryByCorp(QueryParamVO paramvo) throws DZFWarpException;

	/**
	 * 纳税工作台保存方法
	 * 为控制并发问题，app进度管理保存与纳税工作台保存调用同一接口
	 * msgtype 为app传入，web端直接传入null值
	 * @param vo
	 * @param msgtype
	 * @return
	 * @throws DZFWarpException
	 */
	public BsWorkbenchVO save(BsWorkbenchVO vo, Integer msgtype, UserVO uservo) throws DZFWarpException;

	/**
	 * 从财务核算端取数
	 *
	 * @param pk_corp
	 * @param user
	 * @param period
	 * @throws DZFWarpException
	 */
	public List<BsWorkbenchVO> saveFetchData(String pk_corp, UserVO user, String period, String[] corpks) throws DZFWarpException;

	public Set<String> queryPowerCorpSet(UserVO user, String pk_corp) throws DZFWarpException;

	public void saveRemindMsg(String pk_corp, String pk_corpk, String msgtype, UserVO uvo, String qj) throws DZFWarpException;

	public CorpMsgVO[] queryMsgAdminVO(String pk_corp, String pk_corpk, String qj) throws DZFWarpException;

	/**
	 * 获取客户的纳税申报信息
	 * @param pk_corp
	 * @param uservo
	 * @param period
	 * @param corpks
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BsWorkbenchVO> getTaxDeclare(String pk_corp, UserVO uservo, String period, String[] corpks)
			throws DZFWarpException;

	/**
	 * 客户的纳税申报信息
	 * @param uplist
	 * @throws DZFWarpException
	 */
	public void updateTaxDeclare(List<BsWorkbenchVO> uplist) throws DZFWarpException;

//	/**
//	 * 零申报
//	 * @param pk_corp
//	 * @param uservo
//	 * @param period
//	 * @param bsVOs
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<BsWorkbenchVO> updatezeroDec(String pk_corp, String userid, String period,
//			BsWorkbenchVO[] bsVOs, List<String> zsxm_dms) throws DZFWarpException;

    /**
     * 提醒设置保存
     * @param bsVOs
     * @param remVOs
     * @param remap
     * @param pk_corp
     * @param cuserid
     * @return
     * @throws DZFWarpException
     */
	public void saveRemindSet(BsWorkbenchVO bsvo, RemindSetVO[] remVOs, Map<Integer, RemindSetVO> remap,
							  String pk_corp, String cuserid) throws DZFWarpException;

	/**
	 * 查询提醒设置
	 * @param paramvo
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public RemindSetVO[] qryRemSet(BsWorkbenchVO paramvo, String pk_corp) throws DZFWarpException;

	/**
	 * 上传附件
	 * @param fathercorp
	 * @param pk_corpperiod
	 * @param filenames
	 * @param files
	 * @param uservo
	 * @param pk_corp
	 * @param period
	 * @throws DZFWarpException
	 */
	public void uploadFile(String fathercorp, String pk_corpperiod, String[] filenames, File[] files, UserVO uservo,
                           String pk_corp, String period) throws DZFWarpException;

	/**
	 * 获取附件
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BsWorkDocVO> getAttatches(BsWorkDocVO qvo) throws DZFWarpException;

	/**
	 * 删除附件
	 * @param corpvo
	 * @param ids
	 * @throws DZFWarpException
	 */
	public void delAttaches(String fathercorp, String[] ids) throws DZFWarpException;
	
	/**
	 * 查询主办会计
	 * @param fathercorp
	 * @param rolecode
	 * @return
	 * @throws DZFWarpException
	 */
	public UserVO[] queryUser(String fathercorp, String rolecode) throws DZFWarpException;

	/**
	 * 获取财务进度
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	BsWorkbenchVO getFinanceProgress(String pk_corp, String period) throws DZFWarpException;

	/**
	 * 更新财务进度
	 * @param pk_corp
	 * @param period
	 * @param field
	 * @param status
	 * @throws DZFWarpException
	 */
	void updateFinanceProgress(String pk_corp, String period, String field, Integer status) throws DZFWarpException;
	
	/**
	 * 保存设置
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	public void saveCol(ColumnSetupVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询设置
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ColumnSetupVO queryCol(ColumnSetupVO pamvo) throws DZFWarpException;

	/**
	 * 上传附件
	 * @throws DZFWarpException
	 */
	public void uploadFile(String fathercorp, String pk_corpperiod, String[] filenames, List<byte[]> files, UserVO uservo,
						   String pk_corp, String period) throws DZFWarpException;
}
