package com.dzf.zxkj.platform.services.jzcl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TransFerVOInfo;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxCalculateVO;

import java.util.List;
import java.util.Map;

/**
 * 期末结转
 * 
 * 
 */
public interface IQmclService {
	// 查询
	public List<QmclVO> initquery(List<String> corppks, DZFDate dateq, DZFDate datez, String userid, DZFDate dopedate, DZFBoolean iscarover, DZFBoolean isuncarover) throws DZFWarpException;


	//查询币种信息
	public BdCurrencyVO[] queryCurrency() throws DZFWarpException;


	public ExrateVO[] queryAdjust(QmclVO qmvo) throws DZFWarpException;

	/**
	 * 工业成本结转
	 */
	public QmclVO saveIndustryJZ(TransFerVOInfo fervos, String userid)throws DZFWarpException;
	/**
	 * 成本结转
	 */
	public QmclVO saveCbjz(QmclVO vos, String userid) throws DZFWarpException ;
	/**
	 * 反成本结转
	 */
	public QmclVO rollbackCbjz(QmclVO vos) throws DZFWarpException ;


	/**
	 * 期末调汇
	 * @param vos
	 * @throws BusinessException
	 */
	public QmclVO updateHuiDuiSunYiTiaoZheng(QmclVO vos, Map<String, AdjustExrateVO> mapExrate, String userid) throws DZFWarpException ;

	/**
	 * 反期末调汇
	 * @param vos
	 * @param mapExrate
	 * @return
	 * @throws BusinessException
	 */
	public QmclVO updateFanHuiDuiSunYiTiaoZheng(QmclVO vos) throws DZFWarpException ;


	/**
	 * 损益结转
	 * @param vos
	 * @throws BusinessException
	 */
	public QmclVO updateQiJianSunYiJieZhuan(QmclVO vos, String userid) throws DZFWarpException ;

	/**
	 * 反损益结转
	 * @param vos
	 * @throws BusinessException
	 */
	public QmclVO updateFanQiJianSunYiJieZhuan(QmclVO vos) throws DZFWarpException ;

	/**
	 * 计提折旧
	 * @param vos
	 * @throws BusinessException
	 */
	public QmclVO updateJiTiZheJiu(QmclVO vos, String userid) throws DZFWarpException ;

	/**
	 * 反计提折旧
	 * @param vos
	 * @throws BusinessException
	 */
	public QmclVO updateFanJiTiZheJiu(QmclVO vos) throws DZFWarpException ;

	/**
	 * 计提税金
	 */
	public QmclVO updateJiTiShuiJin(QmclVO qmvo, String kmmethod, String pk_corp, String userid) throws DZFWarpException ;
	/**
	 * 反计提税金
	 */
	public QmclVO updateFanJiTiShuiJin(QmclVO vos) throws DZFWarpException ;
	/**
	 * 查询公司
	 */
	public CorpVO queryCorpVOByid(String pk_id)throws DZFWarpException;

	public String getMjkmbm(String str, String pk_corp);

	public List<String> getMjkmbms(String str, String pk_corp);

	//取增值税结转
	public QmclVO onzzsjz(String userid, QmclVO qmvo) throws DZFWarpException ;

	//反增值税结转
	public QmclVO onfzzsjz(QmclVO qmvo) throws DZFWarpException ;

	//企业所得税结转
	public QmclVO onsdsjz(QmclVO qmvo, String userid) throws DZFWarpException ;

	//反企业所得税结转
	public QmclVO onfsdsjz(QmclVO qmvo) throws DZFWarpException ;

	//汇兑损益状态
	public void updatehdsyzt(QmclVO qmvo, String userid) throws DZFWarpException ;

	// 查询
	public QmLossesVO queryLossmny(DZFDate dateq, String copid) throws DZFWarpException;

	// 更新弥补金额
	public QmLossesVO updateLossmny(DZFDate dateq, String copid, DZFDouble mny) throws DZFWarpException;

	public   DZFDouble  getQuarterlySdsShui(String pk_corp, String period) throws DZFWarpException;


	/**
	 * 计算所得税税负
	 * @param pk_corp
	 * @param period
	 * @param vos
	 * @param byear 是否本年累计
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getQuarterlySdsShui1(String pk_corp, String period, LrbquarterlyVO[] vos) throws DZFWarpException;


	/**
	 * 计算本年所得税税负
	 * @param pk_corp
	 * @param period
	 * @param vos
	 * @param byear 是否本年累计
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getQuarterlySdsShuiYear1(String pk_corp, String period, LrbquarterlyVO[] vos) throws DZFWarpException;

	/**
	 * 获取小规模增值税
	 * @param corpVO
	 * @param beginPeriod
	 * @param endPeriod
	 * @return 税收优惠，计税基数
	 */
	public DZFDouble[] getVatMnyByInvoiceType(CorpVO corpVO, String beginPeriod, String endPeriod) throws DZFWarpException;
	/**
	 * 判断公司某时期暂存未识别凭证
	 * @param pk_corp
	 * @param period
	 */
	public void checkTemporaryIsExist(String pk_corp, String period, String message) throws DZFWarpException;

	/**
	 * 期末处理校验和库存相关的
	 * @param pk_corp
	 * @param period
	 * @param message
	 * @throws DZFWarpException
	 */
	public void checkQmclForKc(String pk_corp, String period, String message)  throws DZFWarpException;


	/**
	 * 根据公司期间查询期末处理数据
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public QmclVO queryQmclVO(String pk_corp, String period) throws DZFWarpException;

	/**
	 * 期末处理凭证联查
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TzpzHVO> queryQmclGlpz(String period, String pk_corp, String sourcebilltype) throws DZFWarpException;

	void deleteVoucherForSurtax(String pk_qmcl);

	TzpzHVO createVoucherByTaxCalculator(CorpVO corpVO, TaxCalculateVO taxCal,
                                         Integer taxType, QmclVO qmvo, String userid) throws DZFWarpException;
}