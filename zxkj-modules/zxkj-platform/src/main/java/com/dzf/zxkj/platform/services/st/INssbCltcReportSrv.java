package com.dzf.zxkj.platform.services.st;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.st.CallInfoVO;

import java.util.List;
import java.util.Map;

/***
 * 文化事业建设费申报表
 * @author asoka
 *
 */
public interface INssbCltcReportSrv {
	
	
	/***
	 * 加载报表数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, SuperVO[]> loadDefaultData(CallInfoVO callInfo)throws DZFWarpException;
	
    /***
     *  生成税表
     * @param callInfo
     * @return
     * @throws BusinessException
     */
	@SuppressWarnings("rawtypes")
	public SuperVO[] generateReportData(CallInfoVO callInfo)throws DZFWarpException;
	
	
	/****
	 * 全表计算
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] calcuteAll(CallInfoVO callInfo)throws DZFWarpException;
	
	
	/***
	 * 重新计算
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] reCalculate(CallInfoVO callInfo)throws DZFWarpException;
	
	/****
	 * 更新单个报表数据
	 * @param callInfo
	 * @throws BusinessException
	 */
	public void updateSingleReport(CallInfoVO callInfo)throws DZFWarpException;
	
	/****
	 * 审核报表
	 * @param callInfo
	 * @throws BusinessException
	 */
	public void approveRpinfo(CallInfoVO callInfo)throws DZFWarpException;
	
	
	/****
	 * 放弃审核
	 * @param callInfo
	 * @throws BusinessException
	 */
	public void unaApproveRpinfo(CallInfoVO callInfo)throws DZFWarpException;
	
	
	/****
	 * 获取前台还未加载页签的数据
	 *                    目前前台报表页签的数据是单个加载的，用户点导出的时候可能
	 *                    还有其他页签的数据没有加载  这是需要从后台把数据查出来
	 * @param callInfo
	 * @param loadTabId 需要从后台加载的数据 
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,SuperVO[]> getUnLoadReportVos(CallInfoVO callInfo, List<String> loadTabId)throws DZFWarpException;
	
	
}
