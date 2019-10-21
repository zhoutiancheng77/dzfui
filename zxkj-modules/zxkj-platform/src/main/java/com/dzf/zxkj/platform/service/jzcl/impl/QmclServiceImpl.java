package com.dzf.zxkj.platform.service.jzcl.impl;


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
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 期末结转业务逻辑类
 * 
 * @author zhangj
 * 
 */
@Service("gl_qmclserv")
public class QmclServiceImpl implements IQmclService {

	@Override
	public List<QmclVO> initquery(List<String> corppks, DZFDate dateq, DZFDate datez, String userid, DZFDate dopedate, DZFBoolean iscarover, DZFBoolean isuncarover) throws DZFWarpException {
		return null;
	}

	@Override
	public BdCurrencyVO[] queryCurrency() throws DZFWarpException {
		return new BdCurrencyVO[0];
	}

	@Override
	public ExrateVO[] queryAdjust(QmclVO qmvo) throws DZFWarpException {
		return new ExrateVO[0];
	}

	@Override
	public QmclVO saveIndustryJZ(TransFerVOInfo fervos, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO saveCbjz(QmclVO vos, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO rollbackCbjz(QmclVO vos) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateHuiDuiSunYiTiaoZheng(QmclVO vos, Map<String, AdjustExrateVO> mapExrate, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateFanHuiDuiSunYiTiaoZheng(QmclVO vos) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateQiJianSunYiJieZhuan(QmclVO vos, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateFanQiJianSunYiJieZhuan(QmclVO vos) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateJiTiZheJiu(QmclVO vos, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateFanJiTiZheJiu(QmclVO vos) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateJiTiShuiJin(QmclVO qmvo, String kmmethod, String pk_corp, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO updateFanJiTiShuiJin(QmclVO vos) throws DZFWarpException {
		return null;
	}

	@Override
	public CorpVO queryCorpVOByid(String pk_id) throws DZFWarpException {
		return null;
	}

	@Override
	public String getMjkmbm(String str, String pk_corp) {
		return null;
	}

	@Override
	public List<String> getMjkmbms(String str, String pk_corp) {
		return null;
	}

	@Override
	public QmclVO onzzsjz(String userid, QmclVO qmvo) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO onfzzsjz(QmclVO qmvo) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO onsdsjz(QmclVO qmvo, String userid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmclVO onfsdsjz(QmclVO qmvo) throws DZFWarpException {
		return null;
	}

	@Override
	public void updatehdsyzt(QmclVO qmvo, String userid) throws DZFWarpException {

	}

	@Override
	public QmLossesVO queryLossmny(DZFDate dateq, String copid) throws DZFWarpException {
		return null;
	}

	@Override
	public QmLossesVO updateLossmny(DZFDate dateq, String copid, DZFDouble mny) throws DZFWarpException {
		return null;
	}

	@Override
	public DZFDouble getQuarterlySdsShui(String pk_corp, String period) throws DZFWarpException {
		return null;
	}

	@Override
	public DZFDouble getQuarterlySdsShui1(String pk_corp, String period, LrbquarterlyVO[] vos) throws DZFWarpException {
		return null;
	}

	@Override
	public DZFDouble getQuarterlySdsShuiYear1(String pk_corp, String period, LrbquarterlyVO[] vos) throws DZFWarpException {
		return null;
	}

	@Override
	public DZFDouble[] getVatMnyByInvoiceType(CorpVO corpVO, String beginPeriod, String endPeriod) throws DZFWarpException {
		return new DZFDouble[0];
	}

	@Override
	public void checkTemporaryIsExist(String pk_corp, String period, String message) throws DZFWarpException {

	}

	@Override
	public void checkQmclForKc(String pk_corp, String period, String message) throws DZFWarpException {

	}

	@Override
	public QmclVO queryQmclVO(String pk_corp, String period) throws DZFWarpException {
		return null;
	}

	@Override
	public List<TzpzHVO> queryQmclGlpz(String period, String pk_corp, String sourcebilltype) throws DZFWarpException {
		return null;
	}

	@Override
	public void deleteVoucherForSurtax(String pk_qmcl) {

	}

	@Override
	public TzpzHVO createVoucherByTaxCalculator(CorpVO corpVO, TaxCalculateVO taxCal, Integer taxType, QmclVO qmvo, String userid) throws DZFWarpException {
		return null;
	}
}