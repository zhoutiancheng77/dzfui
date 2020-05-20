package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportNewQcInitVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;

import java.util.List;
import java.util.Map;

public interface IFastTaxService {

	/**
	 * 4、从服务器查询当前征期
	 * @throws DZFWarpException
	 */
	public String getBsPeriod() throws DZFWarpException;
	/**
	 * 5、查询客户列表
	 * @throws DZFWarpException
	 */
	public List<CorpVO> getCustomerList(String loginCorp, UserVO uservo, String userId) throws DZFWarpException;
	
	public List<CorpVO> getCustomerList(String loginCorp, UserVO uservo, String userId, DZFBoolean isAll, Map<String, String> corpMap) throws DZFWarpException;
	/**
	 * 6、查询客户申报报表列表
	 * @param customerId
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<Map<String, Object>> getBsReportList(String loginCorp, UserVO uservo, String customerId, String period) throws DZFWarpException;
	/**
	 * 7、查询客户指定报表的数据
	 * @param customerId
	 * @param rptGroupId
	 * @return
	 * @throws DZFWarpException
	 */
	public String getBsReportDetail(String loginCorp, UserVO uservo, String customerId, String rptGroupId) throws DZFWarpException;
	/**
	 * 8、更新报表申报状态
	 * @param customerId	客户Id		
	 * @param period	征期		
	 * @param reportId	报表Id（或报表名称），表明是哪个sheet。		
	 * @param categoryId	报表类别Id（或名称），表明是哪个excel。可不传。		
	 * @param status	报表当期申报状态		
	 *	0-未填报
	 *	1-已填报
	 *	2-已上传（提交）申报
	 * @param isUpZero 是否零申报
	 * @param taxMny 税额
	 * @return
	 * @throws DZFWarpException
	 */
	public void updateBsReportStatus(String loginCorp, UserVO uservo, String rptGroupId, String status, DZFBoolean isUpZero, DZFDouble taxMny) throws DZFWarpException;
	
//	/**
//	 * 
//	 * @author zhw  
//	 * @param loginCorp  登录公司
//	 * @param uservo  登录用户
//	 * @param infovo  客户纳税信息
//	 * @throws DZFWarpException
//	 */
//	public void updateCustIndustryCode(String loginCorp, UserVO uservo, TaxCustomerInfoVO infovo) throws DZFWarpException;
	
	/**
	 * 更新纳税信息维护
	 * @param loginCorp 登录公司
	 * @param uservo  登录用户
	 * @param customerId 客户Id
	 * @param reportId   更新表样编码
	 * @param isAdd 是否新增
	 * @throws DZFWarpException
	 */
	public void updateBsReportDetail(String loginCorp, UserVO uservo, String customerId, String reportId, String isAdd, String sbzlbh, String sbzq) throws DZFWarpException;
	
	/**
	 * 零申报更新申报状态
	 * @param loginCorp 登录公司
	 * @param uservo 登录用户
	 * @param loginDate  登录时间
	 * @param customerId 客户id
	 * @param period 期间
	 * @param sbzlbh 种类编号
	 * @param status 状态
	 * @param taxMny 税额
	 * @throws DZFWarpException
	 */
	public void updateBsReportByZeroDeclare(String loginCorp, UserVO uservo, String loginDate, String customerId, String period, String sbzlbh, String status, DZFDouble taxMny) throws DZFWarpException;
	
	/**
	 * 获取纳税模板
	 * @param uservo
	 * @param loginCorp
	 * @param sb_zlbh
	 * @param repCodeArr
	 * @param location
	 * @throws DZFWarpException
	 */
//	public String getBsReportTemplet(UserVO uservo, String loginCorp, String sb_zlbh, String[] repCodeArr, String location) throws DZFWarpException;
	
	/**
	 * 更新纳税信息，保存申报结果
	 * @param customerId
	 * @param rptGroupId
	 * @param remark
	 * @throws DZFWarpException
	 */
	public void updateBsReportRemark(String customerId, String rptGroupId, String remark) throws DZFWarpException;
	
	public void updateBsReportQC(TaxReportNewQcInitVO initvo, UserVO uservo) throws DZFWarpException;
	
	/*
	 * 根据公司查询纳税申报情况
	 */
	public List<CorpVO> getBsReportVos(String period, List<CorpVO> corpList) throws DZFWarpException;
	
	/**
	 * 收费标准
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getTaxFee(String pk_corp) throws DZFWarpException;
	/**
	 * 收费接口
	 * @param pk_corp
	 * @param period
	 * @param userid
	 * @param mny
	 * @throws DZFWarpException
	 */
	public void processShoufei(String pk_corp, String period, String userid, DZFDouble mny) throws DZFWarpException;
	
	/**
	 * 根据公司查询当前公司的所有报表
	 * 一键报税使用
	 * @param dq
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TaxRptTempletVO> queryCorpRptTempletVOBydq(String dq) throws DZFWarpException;
	
	/**
	 * 根据公司pk查询
	 * @param corps
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpTaxVo> queryTaxCorpList(List<String> corps) throws DZFWarpException;
}
