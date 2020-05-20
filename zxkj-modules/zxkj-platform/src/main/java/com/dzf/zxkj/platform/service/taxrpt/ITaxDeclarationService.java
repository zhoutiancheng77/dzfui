package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxPaymentVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportInitVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;

import java.util.List;
import java.util.Map;

/****
 * 纳税申报填报
 * @author asoka
 *
 */
public interface ITaxDeclarationService {
	
	/**
	 * 纳税申报查询
	 * @param periodfrom
	 * @param peridto
	 * @param pk_corp
	 * @param sbzt_dm
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TaxReportVO> queryTaxReprotVOs(String periodfrom, String peridto, String pk_corp, String sbzt_dm)throws DZFWarpException;
	/**
	 * 纳税申报查询,查询子表
	 * @param pk_corp
	 * @param pk_taxreport
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TaxReportDetailVO> queryTaxReprotDetailsVOs(String pk_corp, String pk_taxreport)throws DZFWarpException;
//	/****
//	 * 获取征收项目信息
//	 * @throws DZFWarpException
//	 */
//	public List<TaxZsxmVO> getClassifyComboboxData()throws DZFWarpException;
//	/****
//	 * 获取申报种类信息
//	 * @throws DZFWarpException
//	 */
//	public List<TaxSbzlVO> getSbzlComboboxData(String pk_corp, UserVO userVO)throws DZFWarpException;
	
//	/**
//	 * 查询报表VO
//	 * @param paravo
//	 * @param userVO
//	 * @param pk_taxtypelistdetail
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public TaxReportVO[] getTaxReportVO(TaxReportVO paravo, UserVO userVO, String pk_taxtypelistdetail)throws DZFWarpException;
	/**
	 * 获取报表json格式字符串
	 * @param pk_taxreport
	 * @param userVO
	 * @param reportname
	 * @param readonly
	 * @return
	 * @throws Exception
	 */
	public String getSpreadJSData(String pk_taxreport, UserVO userVO, String reportname, Boolean readonly)throws DZFWarpException;
	/**
	 * 获取报表Spread手机发送信息字符串
	 * @param pk_taxreport
	 * @param uservo
	 
	 * @return
	 * @throws Exception
	 */
	public List<DZFDouble> getSpreadSendData(String pk_taxreport, UserVO uservo)throws DZFWarpException;
	
	/**
	 * 保存报表
	 * @param pk_taxreport
	 * @param corpid
	 * @param jsonString
	 * @Param uservo
	 * @Param logindate
	 * @param ts	时间戳
	 * @return 
	 * @throws Exception
	 */
	public String saveReport(String pk_taxreport, String corpid, String jsonString, UserVO uservo, String loginDate, String ts)throws DZFWarpException;

	String createReportDataByTemplate(TaxReportVO reportvo, CorpTaxVo corptaxvo)throws DZFWarpException;
	/**
	 * 保存期初报表
	 * @param taxreportinitvo
	 * @param jsonString
	 * @Param uservo
	 * return pk_taxreportinitvo
	 * @throws Exception
	 */
	public String saveInitReport(TaxReportInitVO taxreportinitvo, String jsonString, UserVO uservo)throws DZFWarpException;

	/**
	 * 快捷申报
	 * @param pk_taxreport
	 * @Param userVO
	 * @param ts	时间戳
	 * @throws Exception
	 */
//	public void processShortDeclare(String pk_taxreport, UserVO userVO, String ts)throws DZFWarpException;
	/**
	 * 刷新申报状态
	 * @param pk_taxreport
	 * @param userVO
	 * @throws Exception
	 */
	public TaxReportVO processRefreshDclStatus(String pk_taxreport, UserVO userVO)throws DZFWarpException;
	/**
	 * 审核
	 * @param pk_taxreport
	 * @param uservo
	 * @Param logindate
	 * @param ts	时间戳
	 * @throws Exception
	 */
	public void processApprove(String pk_taxreport, UserVO uservo, String logindate, String ts)throws DZFWarpException;
	/**
	 * 反审核
	 * @param pk_taxreport
	 * @param uservo
	 * @param ts 时间戳
	 * @throws Exception
	 */
	public void processUnApprove(String pk_taxreport, UserVO uservo, String ts)throws DZFWarpException;
	/**
	 * 企业确认
	 * @param pk_taxreport
	 * @param uservo
	 * @param ts
	 * @throws DZFWarpException
	 */
//	public void processEntConfirm(String pk_taxreport, UserVO uservo, String ts)throws DZFWarpException;
//	/**
//	 * 报表数据检查
//	 * @param Map mapJson 报表的spreaadJS转成的json数据
//	 * @param reportvo 报表VO
//	 * @throws Exception
//	 */
//	public void onCheckReportData(Map mapJson, TaxReportVO reportvo)throws DZFWarpException;
	/**
	 * 
	 * @param filepathname  文件存放未知
	 * @return map key : filename, value: String 
	 * 				key: bytedata value: byte[] , 
	 * 				key : filesize value :Integer  
	 */
	public Map getPdfFile(String filepathname)throws DZFWarpException;
	/**
	 * 申报作废
	 * @param pk_taxreport
	 * @param userVO
	 * @param ts 时间戳
	 * @throws Exception
	 */
	public void processDeclareCancel(String pk_taxreport, UserVO userVO, String ts)throws DZFWarpException;
	/**
	 * 删除
	 * @param pk_taxreport
	 * @param loginDate
	 * @Param userVO
	 * @param ts 时间戳
	 * @throws Exception
	 */
	public void processDelete(String pk_taxreport, String corpid, String loginDate, UserVO userVO, String ts)throws DZFWarpException;
	/**
	 * 重算
	 * @param jsonString   报表的json数据
	 * @param pk_taxreport
	 * @param userVO
	 * @param reportname
	 * @param isCalAll 全表计算
	 * @param ts 时间戳
	 * @throws Exception
	 */
	public String onRecal(String jsonString, String pk_taxreport, UserVO userVO, String corpid, String reportname, Boolean isCalAll, String ts)throws DZFWarpException;
	
	/**
	 * 查询填报类型列表
	 * @param pk_corp 待查询填报类型公司pk
	 * @param userVO 当前登录用户
	 * @param yearmonth
	 * @param operatorid
	 * @param operatedate
	 * @return
	 * @throws Exception
	 */
	public List<TaxReportVO> initGetTypeList(String pk_corp, UserVO userVO, String yearmonth, String operatorid, String operatedate)throws DZFWarpException;
	
	/**
	 * 获取期初数据报表
	 * @param pk_corp
	 * @param sb_zlbh
	 * @param period
	 * @param userVO
	 * @return
	 * @throws Exception
	 */
	public TaxReportInitVO getInitSpreadJSData(String pk_corp, String sb_zlbh, String period, UserVO userVO)throws DZFWarpException;
	/**
	 * 读取报表审核检查条件数组
	 * @param pk_taxreport
	 * @param userVO
	 * @return
	 * @throws Exception
	 */
	public String[] getCondition(String pk_taxreport, UserVO userVO)throws DZFWarpException;
	
	/**
	 * 获取指定坐标表格的数值
	 * @param filePath
	 * @param reportName
	 * @param x
	 * @param y
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getQsbbqsData(String filePath, String reportName, String x, String y) throws DZFWarpException;
	
	/**
	 * 获取指定模板
	 * @param filePath
	 * @return
	 * @throws DZFWarpException
	 */
	public String getReportTemplet(String filePath) throws DZFWarpException;
	
	/**
	 * 
	 * @param corpVO
	 *            公司
	 * @param userVO
	 *            用户
	 * @param corpVO  明细
	 * @param pk_taxreport     报表主键
	 * @return
	 * @throws DZFWarpException
	 */
	public Object processSendTaxReport(CorpVO corpVO, UserVO userVO, String corpid, String pk_taxreport) throws DZFWarpException;
	
	/**
	 * 查询完税凭证
	 * @param corpid
	 * @param pk_taxreport
	 * @return
	 * @throws DZFWarpException
	 */
	public TaxPaymentVO[] queryTaxPayment(String corpid, String pk_taxreport) throws DZFWarpException;
	
	/**
	 * 批量填写
	 * @param token
	 * @param clientid
	 * @param clientpk_corp
	 * @param clientuserid
	 * @param logindate
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String saveBatWriteInfo(String token, String clientid, String clientpk_corp,
								   String clientuserid, String logindate, String pk_corp)throws DZFWarpException;
	
	/**
	 * 判断填报列表是否有效数据
	 * @param list
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String qryTaxReportValid(List<TaxReportVO> list, String pk_corp)throws DZFWarpException;
}
