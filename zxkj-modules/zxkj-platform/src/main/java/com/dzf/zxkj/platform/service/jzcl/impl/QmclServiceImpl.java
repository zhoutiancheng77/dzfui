package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.SurTaxEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.config.QmjzByDzfConfig;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.tax.TaxCalculateVO;
import com.dzf.zxkj.platform.model.tax.TaxEffeHistVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.ISurtaxTemplateService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.IVoucherTemplate;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IJtsjTemService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.tax.ITaxCalculateArchiveService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCard;
import com.dzf.zxkj.platform.service.zcgl.IKpglService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 期末结转业务逻辑类
 *
 * @author zhangj
 *
 */
@Service("gl_qmclserv")
public class QmclServiceImpl implements IQmclService {
	private static int[] oldRule = new int[] { 4, 2, 2, 2, 2, 2 };
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IAssetCard assetCardImpl;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule;
	@Autowired
	private ICpaccountService gl_cpacckmserv;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	private IJtsjTemService gl_jtsjtemserv;
	@Autowired
	private IPurchInService ic_purchinserv;
	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private IKpglService am_kpglserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICorpTaxService sys_corp_tax_serv;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IVoucherTemplate vouchertempser;
	@Autowired
	private ITaxCalculateArchiveService gl_taxarchive;
	@Autowired
	private ISurtaxTemplateService gl_surtaxtempserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private QmjzByDzfConfig qmjzByDzfConfig;

	public void doQmVO(QmclVO vo) throws DZFWarpException {
		// 成本结转处理
		vo.setIscbjz(DZFBoolean.TRUE);
		// 保存期末结转VO
		if (StringUtil.isEmpty(vo.getPrimaryKey())) {
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		} else {
			singleObjectBO.update(vo);
		}
	}

	/**
	 * 取凭证VO制单日期
	 */
	private DZFDate getDoperateDate(TzpzHVO billvo) {
		TzpzHVO headvo = (TzpzHVO) billvo;
		return headvo.getDoperatedate();
	}

	public void saveVoucher(TzpzHVO billvo, DZFDate tradedate) throws DZFWarpException {

		singleObjectBO.saveObject(billvo.getPk_corp(), billvo);
		// 走毛脚本，直接保存
		// IplatFormEntry iIplatFormEntry = (IplatFormEntry)
		// NCLocator.getInstance().lookup(IplatFormEntry.class.getName());
		// iIplatFormEntry.processAction("WRITE", IBillTypeCode.HP40, tradedate,
		// null, billvo,null, null);
	}

	/**
	 * 期末调汇
	 */
	public QmclVO updateHuiDuiSunYiTiaoZheng(QmclVO vo, Map<String, AdjustExrateVO> mapExrate, String userid) throws DZFWarpException {

		if (vo == null) {
			throw new BusinessException("请选择要期末调汇的数据。");
		}
		vo = checkisGz(vo, "不能期末调汇！");
		// if(!checkCorp(vo.getPk_corp())){
		// throw new BusinessException("期间:"+vo.getPeriod()+" 未启用多币种，不需调汇。");
		// }

		QmclVO voa = queryQmclVO(vo.getPk_corp(), vo.getPeriod());
		DZFBoolean iscbjz = voa == null ? vo.getIshdsytz() : voa.getIshdsytz();
		if (iscbjz != null && iscbjz.booleanValue()) {
			throw new BusinessException("已经期末调汇，不能重复调整！");
		}
		// int repeatNum = checkVoucher(vo);
		// if (repeatNum > 0) {
		// throw new BusinessException("本期存在未记账凭证，不能期末调汇。");
		// }

		// 如果已经汇兑完毕 则不能再次汇兑
		SQLParameter sp = new SQLParameter();
		String qrysql = "select min(period) from ynt_qmcl where period> ?  and nvl(dr,0)=0 and nvl(ishdsytz,'N')='Y' and pk_corp= ? ";
		sp.addParam(vo.getPeriod());
		sp.addParam(vo.getPk_corp());
		String period = (String) singleObjectBO.executeQuery(qrysql, sp, new ColumnProcessor());

		if (period != null && period.trim().length() > 0) {
			throw new BusinessException("期间(" + period + ")已经汇兑!");
		}

		vo = new AdjustRateDMO(singleObjectBO, yntBoPubUtil, gl_tzpzserv, gl_cpacckmserv,vouchertempser).onAdjustRate(vo, mapExrate,userid);
		return vo;
	}

	private Integer checkVoucher(QmclVO vo) throws DZFWarpException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		/** ********************************************************** */
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPeriod());
		StringBuilder sbCode = new StringBuilder(
				"select count(1) from ynt_tzpz_h where pk_corp=? and nvl(dr,0) = 0 and period=? and nvl(ishasjz,'N') = 'N' ");
		BigDecimal repeatNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp, new ColumnProcessor());

		return repeatNum.intValue();

	}

	/**
	 * 公司校验
	 *
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkCorp(String pk_corp) throws DZFWarpException {
		CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (corpVO.getIscurr() != null && corpVO.getIscurr().booleanValue()) {// 是否启用多币种
			return true;
		}
		return false;
	}

	/**
	 * 反期末调汇
	 */
	public QmclVO updateFanHuiDuiSunYiTiaoZheng(QmclVO vo) throws DZFWarpException {

		DZFBoolean isqjsyjz = vo.getIsqjsyjz();
		//年结不能反汇兑
		updateCheckNj(vo);
		//关账不能返汇兑
		vo = checkisGz(vo, "不能反期末调汇！");
		//已经损益结转不能反汇兑
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+vo.getPeriod()+"，已经损益结转，不能反期末调汇！");
		}
		//后续是否有汇兑凭证
		SQLParameter sp = new SQLParameter();
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period>?  and pk_corp= ? order by period  ";
		sp.addParam(vo.getPeriod());
		sp.addParam(vo.getPk_corp());
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		if (value != null && value.size() > 0) {
			if (value.get(0).getIshdsytz() != null && value.get(0).getIshdsytz().booleanValue()) {
				throw new BusinessException("存在后续期间的汇兑数据，不能跨月反期末调汇!");
			}
		}

		if (vo.getIshdsytz() == null || !vo.getIshdsytz().booleanValue()) {
			throw new BusinessException("没有进行期末调汇，不能反期末调汇！");
		}

		vo = new AdjustRateDMO(singleObjectBO, yntBoPubUtil, gl_tzpzserv, gl_cpacckmserv,vouchertempser).cancelAdjustRate(vo);
		return vo;
	}

	/**
	 * 计提折旧
	 */
	public QmclVO updateJiTiZheJiu(QmclVO qmvo,String userid) throws DZFWarpException {
		// 批量计提折旧(该公司如果启用库存和外币则不能批量操作)
		// zpm修改提交
		QmclVO voa = queryQmclVO(qmvo.getPk_corp(), qmvo.getPeriod());
		DZFBoolean iscbjz = voa == null ? qmvo.getIszjjt() : voa.getIszjjt();
		if (iscbjz != null && iscbjz.booleanValue()) {
			throw new BusinessException("期间("+qmvo.getPeriod()+")已经计提折旧，不能重复计提！");
		}
		// 检查关账，不通过抛出异常
		qmvo = checkisGz(qmvo, "不能计提折旧！");
		// 禁止多期间同时操作
		// Vector<String> vec_period = new Vector<String>();
		// if (vec_period.isEmpty()) {
		// vec_period.add(qmvo.getPeriod());
		// } else {
		// if (!vec_period.contains(qmvo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行计提折旧");
		// }
		// }

		// 查询公司的资产建账日期

		// corpvo 下边有用到holdflag 所以不从缓存取
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, qmvo.getPk_corp());
		DZFDate busibegin = corpvo.getBusibegindate();
		DZFDate periodbegin = DateUtils.getPeriodEndDate(qmvo.getPeriod());
		if (busibegin == null && (corpvo.getHoldflag() == null || !corpvo.getHoldflag().booleanValue())) {
			throw new BusinessException("公司:" + deCodename(corpvo.getUnitname()) + "，没启用固定资产，不能计提折旧！");
		} else if (busibegin != null && periodbegin.before(busibegin)
				&& (corpvo.getHoldflag() != null || corpvo.getHoldflag().booleanValue())) {
			throw new BusinessException(
					"公司:" + deCodename(corpvo.getUnitname()) + "，固定资产启用日期(" + busibegin.toString() + ")，不需要计提折旧!");
		}

		String begperiod = am_kpglserv.getMinAssetPeriod(qmvo.getPk_corp());
		if (!StringUtil.isEmpty(begperiod)) {// 有资产才提示
			// 折旧添加校验
			SQLParameter sp = new SQLParameter();
			sp.addParam(qmvo.getPeriod());
			sp.addParam(begperiod);
			sp.addParam(qmvo.getPk_corp());
			String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period< ? and period >= ?  and pk_corp= ?  order by period desc";
			List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
					new BeanListProcessor(QmclVO.class));
			if (value != null && value.size() > 0) {
				String periodtemp = value.get(0).getPeriod() + "-01";
				DZFDate perioddatetemp = new DZFDate(periodtemp);
				DZFDate perioddate = new DZFDate(qmvo.getPeriod() + "-01").getDateBefore(1);
				if (value.get(0).getIszjjt() == null || !value.get(0).getIszjjt().booleanValue()) {
					throw new BusinessException("公司:" + deCodename(corpvo.getUnitname()) + "，存在先前期间的没计提数据，不能跨月计提折旧!");
				}
				if (perioddatetemp.getYear() != perioddate.getYear()
						|| perioddatetemp.getMonth() != perioddate.getMonth()) {
					throw new BusinessException("公司:" + deCodename(corpvo.getUnitname()) + "，存在先前期间的没计提数据，不能跨月计提折旧!");
				}
			}
		}
		qmvo = assetCardImpl.updateDepreciate(corpvo, qmvo,userid);
		return qmvo;
	}

	/**
	 * 损益结转
	 */
	public QmclVO updateQiJianSunYiJieZhuan(QmclVO qmvo,String userid) throws DZFWarpException {
		if (qmvo == null) {
			throw new BusinessException("请选择要损益结转的数据！");
		}
		qmvo = checkisGz(qmvo, "不能损益结转！");

		// 控制损益结转后不能再结转，这里重新在查询一次。
		QmclVO voa = queryQmclVO(qmvo.getPk_corp(), qmvo.getPeriod());
		if (voa != null) {
			qmvo = voa;
		}
		checkIsAdjust(qmvo);
		// DZFBoolean isqjsyjz = vos[0].getIsqjsyjz();
		DZFBoolean isqjsyjz = qmvo.getIsqjsyjz();
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+qmvo.getPeriod()+"，已经损益结转，不能重复结转！");
		}

		// 禁止多期间同时成本结转
		// Vector<String> vec_period = new Vector<String>();
		// for (QmclVO vo : vos) {
		// if (vec_period.isEmpty()) {
		// vec_period.add(vo.getPeriod());
		// } else {
		// if (!vec_period.contains(vo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行损益结转");
		// }
		// }
		// }

		// if (qmvo == null) {
		// throw new BusinessException("没有需要损益结转的数据");
		// }

		// 勾上损益结转
		// vo.setIsqjsyjz(DZFBoolean.TRUE) ;
		// 判断上一期是否损益结转，如果上一期没有损益结转，则提示本期不能损益结转
		checkLastPeriod(qmvo);
		// 根据公司查找公司损益结转模板
		YntCptransmbHVO[] aggvos = queryCorpTemplateAggvo(qmvo);
		if (aggvos == null || aggvos.length < 1) {
			doHyJz(qmvo,userid);
		} else {
			doCorpJz(qmvo, aggvos,userid);
		}
		return qmvo;
	}

	/**
	 * 校验是否期末调汇
	 *
	 * @param qmclvo
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkIsAdjust(QmclVO qmclvo) throws DZFWarpException {
		if (checkCorp(qmclvo.getPk_corp())) {
			if (qmclvo.getIshdsytz() != null && qmclvo.getIshdsytz().booleanValue()) {
				return true;
			} else {
				throw new BusinessException("没有期末调汇，不能结转。");
			}
		}
		return true;
	}

	/**
	 * 校验上一期间是否结转
	 *
	 * @throws BusinessException
	 */
	public void checkLastPeriod(QmclVO vo) throws DZFWarpException {
		ICorpService corpService = SpringUtils.getBean(ICorpService.class);
		CorpVO corpVO = corpService.queryByPk(vo.getPk_corp());
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPeriod());
		sp.addParam(DateUtils.getPeriod(corpVO.getBegindate()));
		sp.addParam(vo.getPk_corp());
		String wherepart = "select period,isqjsyjz from  ynt_qmcl where nvl(dr,0)=0 and period< ? and period >= ?  and pk_corp= ?  order by period desc";
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		if (value != null && value.size() > 0) {
			String periodtemp = value.get(0).getPeriod() + "-01";
			DZFDate perioddatetemp = new DZFDate(periodtemp);
			DZFDate perioddate = new DZFDate(vo.getPeriod() + "-01").getDateBefore(1);
			if (value.get(0).getIsqjsyjz() == null || !value.get(0).getIsqjsyjz().booleanValue()) {
				throw new BusinessException("期间:" + DateUtils.getPeriod(perioddatetemp) + "尚未进行损益结转，不能操作!");
			}
			if (perioddatetemp.getYear() != perioddate.getYear()
					|| perioddatetemp.getMonth() != perioddate.getMonth()) {
				throw new BusinessException("期间:" + DateUtils.getPeriod(perioddatetemp) + " 尚未进行损益结转，不能操作!");
			}
		}
	}

	/**
	 * 检查下一期间是否结转
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkNextPeriod(QmclVO vo) throws DZFWarpException {
		// 判断上一期是否损益结转，如果上一期没有损益结转，则提示本期不能损益结转
		DZFDate nowPeriod = new DZFDate(vo.getPeriod() + "-01");
		String nextPeriod = "";

		if (nowPeriod.getMonth() == 12) {
			// 上一期
			nextPeriod = (nowPeriod.getYear() + 1) + "-01";
		} else {
			nextPeriod = (nowPeriod.getYear()) + "-"
					+ ((nowPeriod.getMonth() + 1) < 10 ? "0" + (nowPeriod.getMonth() + 1) : (nowPeriod.getMonth() + 1));
		}
		DZFDate nextPeriodDate = new DZFDate(nextPeriod + "-01");
		// CorpVO corpVO = (CorpVO)
		// singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		ICorpService corpService = SpringUtils.getBean(ICorpService.class);
		CorpVO corpVO = corpService.queryByPk(vo.getPk_corp());
		if (corpVO != null) {
			if (corpVO.getBegindate() == null) {
				throw new BusinessException("公司:'" + deCodename(corpVO.getUnitname()) + "'的建账日期为空，可能尚未建账，请检查!");
			}
			DZFDate corpdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate()));
			if (corpdate.before(nextPeriodDate)) {
				SQLParameter sp = new SQLParameter();
				sp.addParam(nextPeriod);
				sp.addParam(vo.getPk_corp());
				String sqlWhere = " period=? and pk_corp=? and nvl(dr,0)=0 ";
				QmclVO[] dbVOs = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, sqlWhere, sp);
				if (dbVOs != null && dbVOs.length > 0) {
					if (dbVOs[0].getIsqjsyjz() != null && dbVOs[0].getIsqjsyjz().booleanValue()) {
						throw new BusinessException("下一期" + nextPeriod + "已经损益结转，不能操作！");
					}
				}
			}
		}
	}

	/**
	 * 根据行业科目期间模板设置结转
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void doHyJz(QmclVO vo,String userid) throws DZFWarpException {
		BdTradeTranstemplateHVO[] aggvos = queryTradeTemplateAggvo(vo);
		// 保存期末结转VO
		// 默认人民币
		String pk_curr = yntBoPubUtil.getCNYPk();

		// int schevalue = yntBoPubUtil.getAccountSchema(vo.getPk_corp());

		// if(DzfUtil.SEVENSCHEMA.intValue() == schevalue ||
		// DzfUtil.THIRTEENSCHEMA.intValue() == schevalue){
		// syHyGen07or13Pz(vo, aggvos, pk_corp,
		// pk_curr);//07和13期间损益生成的凭证，这些不走期间损益表体的模板(为了历史数据问题)
		// }else{
		syHyGenOther(vo, aggvos, vo.getPk_corp(), pk_curr,userid);
		// }
		doSave(vo);

	}

	/**
	 * 其他科目方案，根据期间损益模板生成数据
	 *
	 * @param vo
	 * @param aggvos
	 * @param pk_corp
	 * @param pk_curr
	 */
	private void syHyGenOther(QmclVO vo, BdTradeTranstemplateHVO[] aggvos, String pk_corp, String pk_curr,String userid) {

		// 查询科目的发生额
		HashMap<String, List<KmZzVO>> mapPz = queryCorpKmfs(vo);
		if (mapPz == null || mapPz.size() < 1) {
			return;
		}

		// 根据行业科目期间模板表体转出科目查询对应公司科目，通过科目编码匹配
		YntCpaccountVO[] corpAccVOS = querySykm(pk_corp); // 损益科目

		YntCpaccountVO[] corpvos = querykm(pk_corp);// 科目

		HashMap<String, YntCpaccountVO> mapCorpAcount = new HashMap<String, YntCpaccountVO>();

		for (YntCpaccountVO votemp : corpvos) {
			mapCorpAcount.put(votemp.getPk_corp_account(), votemp);
		}

		if (aggvos == null || aggvos.length == 0) {
			throw new BusinessException("科目方案对应的损益模板为空，请配置!");
		}

		StringBuffer wherepart = new StringBuffer();
		for (BdTradeTranstemplateHVO aggvo : aggvos) {
			wherepart.append("'" + aggvo.getPk_trade_transtemplate_h() + "',");
		}

		String sybsql = "select * from  ynt_tdtransmb_b  where nvl(dr,0)=0 and  pk_trade_transtemplate_h in ("
				+ wherepart.substring(0, wherepart.length() - 1) + ") order by accountcode asc";

		// 期间损益表体模板
		List<BdTradeTranStemPlateBVO> syblist = (List<BdTradeTranStemPlateBVO>) singleObjectBO.executeQuery(sybsql,
				new SQLParameter(), new BeanListProcessor(BdTradeTranStemPlateBVO.class));

		Map<String, List<BdTradeTranStemPlateBVO>> sybmap = new HashMap<String, List<BdTradeTranStemPlateBVO>>();

		if (syblist != null && syblist.size() > 0) {
			for (BdTradeTranStemPlateBVO sybvo : syblist) {
				if (sybmap.containsKey(sybvo.getPk_trade_transtemplate_h())) {
					sybmap.get(sybvo.getPk_trade_transtemplate_h()).add(sybvo);
				} else {
					List<BdTradeTranStemPlateBVO> templist = new ArrayList<BdTradeTranStemPlateBVO>();
					templist.add(sybvo);
					sybmap.put(sybvo.getPk_trade_transtemplate_h(), templist);
				}
			}
		}

		for (BdTradeTranstemplateHVO aggvo : aggvos) {
			// BdTradeTranstemplateHVO hymbVO = (BdTradeTranstemplateHVO)aggvo;
			// 转入科目，金额放在贷方，金额=转出科目(贷方本期发生额)-转出科目(借方本期发生额)
			String zrkm = aggvo.getPk_transferinaccount();
			if (aggvo.getAccountcode() == null) {
				throw new BusinessException("行业损益结转模板转入科目编码不能为空，请检查！");
			}
			// 根据行业会计科目主键找到公司会计科目主键
			zrkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(zrkm, pk_corp);

			// 找到表体的损益模板
			List<BdTradeTranStemPlateBVO> syblisttemp = sybmap.get(aggvo.getPk_trade_transtemplate_h());

			String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
			// 如果表体为空则损益类科目全部生成一张凭证
			if (syblisttemp == null || syblisttemp.size() == 0) {
				genHyVoucherForSy(corpAccVOS, mapPz, aggvo, pk_curr, zrkm, vo, mapCorpAcount,userid);// 收入
			} else {
				List<YntCpaccountVO> reslist = new ArrayList<YntCpaccountVO>();
				// 如果不为空则根据对应的科目生成
				for (YntCpaccountVO cpavo : corpvos) {
					for (BdTradeTranStemPlateBVO bvotemp : syblisttemp) {
						String tempcode = gl_accountcoderule.getNewRuleCode(bvotemp.getAccountcode(),
								DZFConstant.ACCOUNTCODERULE, newrule);
						if (cpavo.getAccountcode().startsWith(tempcode)) {
							YntCpaccountVO cpavotemp = new YntCpaccountVO();
							BeanUtils.copyProperties(cpavo, cpavotemp);
							cpavotemp.setDirection(bvotemp.getDirection());// 取模板的方向
							reslist.add(cpavotemp);
						}
					}
				}
				genHyVoucherForSy(reslist.toArray(new YntCpaccountVO[0]), mapPz, aggvo, pk_curr, zrkm, vo,
						mapCorpAcount,userid);// 收入
			}
		}
	}

	/**
	 * 生成07或者13的凭证(目前不走模板)
	 *
	 * @param vo
	 * @param aggvos
	 * @param pk_corp
	 * @param pk_curr
	 */
//	private void syHyGen07or13Pz(QmclVO vo, BdTradeTranstemplateHVO[] aggvos, String pk_corp, String pk_curr,String userid) {
//		for (BdTradeTranstemplateHVO aggvo : aggvos) {
//			// BdTradeTranstemplateHVO hymbVO = (BdTradeTranstemplateHVO)aggvo;
//			// 转入科目，金额放在贷方，金额=转出科目(贷方本期发生额)-转出科目(借方本期发生额)
//			String zrkm = aggvo.getPk_transferinaccount();
//			if (aggvo.getAccountcode() == null) {
//				throw new BusinessException("行业损益结转模板转入科目编码不能为空，请检查！");
//			}
//			// 根据行业会计科目主键找到公司会计科目主键
//			zrkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(zrkm, pk_corp);
//			// 根据行业科目期间模板表体转出科目查询对应公司科目，通过科目编码匹配
//			YntCpaccountVO[] corpAccVOS = querySykm(pk_corp);
//
//			YntCpaccountVO[] corpvos = querykm(pk_corp);
//			HashMap<String, YntCpaccountVO> mapCorpAcount = new HashMap<String, YntCpaccountVO>();
//
//			for (YntCpaccountVO votemp : corpvos) {
//				mapCorpAcount.put(votemp.getPk_corp_account(), votemp);
//			}
//
//			// 转出科目发生额
//			HashMap<String, List<KmZzVO>> mapPz = queryCorpKmfs(vo);
//			if (mapPz == null || mapPz.size() < 1) {
//				continue;
//			}
//
//			// 循环两次，一次收入类科目，一次费用+成本科目
//			List<YntCpaccountVO> incomelist = new ArrayList<YntCpaccountVO>();
//			List<YntCpaccountVO> unincomelist = new ArrayList<YntCpaccountVO>();
//
//			if (yntBoPubUtil.is2007AccountSchema(vo.getPk_corp())) {
//				for (YntCpaccountVO cpavo : corpAccVOS) {
//					if (cpavo.getAccountcode().startsWith("6001") || cpavo.getAccountcode().startsWith("6051")
//							|| cpavo.getAccountcode().startsWith("6111") || cpavo.getAccountcode().startsWith("6301")) {
//						incomelist.add(cpavo);
//					} else {
//						unincomelist.add(cpavo);
//					}
//
//				}
//			} else {
//				for (YntCpaccountVO cpavo : corpAccVOS) {
//					if (cpavo.getAccountcode().startsWith("5001") || cpavo.getAccountcode().startsWith("5051")
//							|| cpavo.getAccountcode().startsWith("5111") || cpavo.getAccountcode().startsWith("5301")) {
//						incomelist.add(cpavo);
//					} else {
//						unincomelist.add(cpavo);
//					}
//
//				}
//
//			}
//			genHyVoucherForSy(incomelist.toArray(new YntCpaccountVO[0]), mapPz, aggvo, pk_curr, zrkm, vo,
//					mapCorpAcount,userid);// 收入
//			genHyVoucherForSy(unincomelist.toArray(new YntCpaccountVO[0]), mapPz, aggvo, pk_curr, zrkm, vo,
//					mapCorpAcount,userid);// (成本+费用)
//		}
//	}

	private void genHyVoucherForSy(YntCpaccountVO[] corpAccVOS, HashMap<String, List<KmZzVO>> mapPz,
								   BdTradeTranstemplateHVO hymbVO, String pk_curr, String zrkm, QmclVO vo,
								   HashMap<String, YntCpaccountVO> mapCorpAcount,String userid) throws DZFWarpException {
		YntCpaccountVO zrkmVO = gl_cpacckmserv.queryById(zrkm);
//		if (zrkmVO.getIsfzhs() != null && !"0000000000".equals(zrkmVO.getIsfzhs())) {
//			throw new BusinessException(zrkmVO.getAccountcode() + "科目的辅助核算项不允许为空！");
//		}
		// 本期转出科目（借方累计）
		DZFDouble zckm_jflj = DZFDouble.ZERO_DBL;
		// 本期转出科目（贷方累计）
		DZFDouble zckm_dflj = DZFDouble.ZERO_DBL;
		// 转出科目S
		Vector<TzpzBVO> vec_zckms = new Vector<TzpzBVO>();
		boolean isJfMny = false;
		for (YntCpaccountVO bodyVO : corpAccVOS) {

			String pk_accsubj = bodyVO.getPk_corp_account();

			// 判断该科目的方向
			YntCpaccountVO zckmVO = null;
			DZFDouble ye = DZFDouble.ZERO_DBL;
			Integer fx = null;
			DZFDouble jf = DZFDouble.ZERO_DBL;
			DZFDouble df = DZFDouble.ZERO_DBL;
			List<KmZzVO> kmzzVOlist = null;

			if (bodyVO.getIsfzhs() != null && !"0000000000".equals(bodyVO.getIsfzhs())) {

				zckmVO = bodyVO;
				List<KmZzVO> mapPZ = queryCorpKmFzhsfs(vo, zckmVO);

				if (mapPZ != null && mapPZ.size() > 0) {
					KmZzVO kmzzVO = null;
					for (int i = 0; i < mapPZ.size(); i++) {
						kmzzVO = (KmZzVO) mapPZ.get(i);
						if (kmzzVO == null) {
							continue;
						}
						jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getJf();
						df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getDf();

						fx = zckmVO.getDirection() == null ? 0 : zckmVO.getDirection();
						if (0 == fx.intValue()) {
							isJfMny = true;
						}

						if (0 == mapCorpAcount.get(pk_accsubj).getDirection().intValue()) {
							// 借方
							ye = jf.sub(df);
						} else {
							// 贷方
							ye = df.sub(jf);
						}
						if (jf.doubleValue() == 0 && df.doubleValue() == 0) {
							// 此科目此期间没有发生额，则不生成此分录
							continue;
						}
						TzpzBVO bvo = new TzpzBVO();
						bvo.setPk_accsubj(zckmVO.getPk_corp_account());
						bvo.setPk_inventory(kmzzVO.getPk_inventory());
						if (kmzzVO.getNnumber() != null && kmzzVO.getNnumber().doubleValue() != 0) {
							bvo.setNnumber(kmzzVO.getNnumber());
							bvo.setNprice(ye.div(bvo.getNnumber()));
						}
						bvo.setFzhsx1(kmzzVO.getFzhsx1());
						bvo.setFzhsx2(kmzzVO.getFzhsx2());
						bvo.setFzhsx3(kmzzVO.getFzhsx3());
						bvo.setFzhsx4(kmzzVO.getFzhsx4());
						bvo.setFzhsx5(kmzzVO.getFzhsx5());
						bvo.setFzhsx6(kmzzVO.getFzhsx6());
						bvo.setFzhsx7(kmzzVO.getFzhsx7());
						bvo.setFzhsx8(kmzzVO.getFzhsx8());
						bvo.setFzhsx9(kmzzVO.getFzhsx9());
						bvo.setFzhsx10(kmzzVO.getFzhsx10());
						if (0 == fx.intValue()) {
							// 借方科目，要放在贷方
							bvo.setJfmny(ye);
							zckm_jflj = zckm_jflj.add(ye);
						} else {
							bvo.setDfmny(ye);
							zckm_dflj = zckm_dflj.add(ye);
						}
						bvo.setZy(hymbVO.getAbstracts());
						// 币种，默认人民币
						bvo.setPk_currency(pk_curr);
						vec_zckms.add(bvo);
					}
				} else {
					continue;
				}
			} else {
				zckmVO = bodyVO;

				fx = zckmVO.getDirection() == null ? 0 : zckmVO.getDirection();
				if (0 == fx.intValue()) {
					isJfMny = true;
				}

				kmzzVOlist = (List<KmZzVO>) mapPz.get(pk_accsubj);

				if (kmzzVOlist == null || kmzzVOlist.size() == 0) {
					continue;
				}
				for (KmZzVO kmzzVO : kmzzVOlist) {
					if (kmzzVO == null) {
						continue;
					}
					jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getJf();
					df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getDf();

					if (0 == mapCorpAcount.get(pk_accsubj).getDirection().intValue()) {
						// 借方
						ye = jf.sub(df);
					} else {
						// 贷方
						ye = df.sub(jf);
					}

					if (jf.doubleValue() == 0 && df.doubleValue() == 0) {
						// 此科目此期间没有发生额，则不生成此分录
						continue;
					}
					TzpzBVO bvo = new TzpzBVO();
					bvo.setPk_accsubj(zckmVO.getPk_corp_account());
					bvo.setPk_inventory(kmzzVO.getPk_inventory());
					bvo.setNnumber(kmzzVO.getNnumber());
					if (bvo.getNnumber() != null && bvo.getNnumber().doubleValue() != 0) {
						bvo.setNprice(ye.div(bvo.getNnumber()));
					}
					bvo.setFzhsx1(kmzzVO.getFzhsx1());
					bvo.setFzhsx2(kmzzVO.getFzhsx2());
					bvo.setFzhsx3(kmzzVO.getFzhsx3());
					bvo.setFzhsx4(kmzzVO.getFzhsx4());
					bvo.setFzhsx5(kmzzVO.getFzhsx5());
					bvo.setFzhsx6(kmzzVO.getFzhsx6());
					bvo.setFzhsx7(kmzzVO.getFzhsx7());
					bvo.setFzhsx8(kmzzVO.getFzhsx8());
					bvo.setFzhsx9(kmzzVO.getFzhsx9());
					bvo.setFzhsx10(kmzzVO.getFzhsx10());
					if (0 == fx.intValue()) {
						// 借方科目，要放在贷方
						bvo.setJfmny(ye);
						zckm_jflj = zckm_jflj.add(ye);
					} else {
						bvo.setDfmny(ye);
						zckm_dflj = zckm_dflj.add(ye);
					}
					bvo.setZy(hymbVO.getAbstracts());
					// 币种，默认人民币
					bvo.setPk_currency(pk_curr);
					vec_zckms.add(bvo);
				}

			}
		}

		DZFDouble res = zckm_jflj.sub(zckm_dflj);

		// 凭证转入科目的分录：本年利润（借）
		TzpzBVO pzbvo_jf = new TzpzBVO();
		pzbvo_jf.setPk_accsubj(zrkm);
		if (res.doubleValue() > 0) {
			pzbvo_jf.setJfmny(DZFDouble.ZERO_DBL);
			pzbvo_jf.setDfmny(res);
		} else {
			pzbvo_jf.setJfmny(res.multiply(-1));
			pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
		}
		pzbvo_jf.setZy(hymbVO.getAbstracts());// 摘要
		// 币种，默认人民币
		pzbvo_jf.setPk_currency(pk_curr);

		TzpzBVO[] pzBodyVOs = null;
		// 凭证表体VOs
		pzBodyVOs = new TzpzBVO[vec_zckms.size() + 1];
		ArrayList<TzpzBVO> listBvos = new ArrayList<>();
		ArrayList<TzpzBVO> dfBvos = new ArrayList<>();
		for (TzpzBVO pzBodyVO : vec_zckms) {
			if (pzBodyVO.getDfmny() != null && pzBodyVO.getDfmny().compareTo(DZFDouble.ZERO_DBL) != 0) {
				dfBvos.add(pzBodyVO);
			}
			if (pzBodyVO.getJfmny() != null && pzBodyVO.getJfmny().compareTo(DZFDouble.ZERO_DBL) != 0) {//借方在上，贷方在下
				listBvos.add(pzBodyVO);
			}
		}
		listBvos.addAll(dfBvos);
		// 转入科目始终放在第一行
		if (res.doubleValue() > 0) {
			listBvos.add(pzbvo_jf);
		} else {
			listBvos.add(0, pzbvo_jf);
		}
		pzBodyVOs = listBvos.toArray(new TzpzBVO[0]);
		if (zckm_jflj.doubleValue() == 0 && zckm_dflj.doubleValue() == 0
				&& pzBodyVOs.length==1) {
			// 只有本年利润一条分录时不生成凭证,如果是多条，本年利润为零也生成凭证
			return;
		}

		// 凭证表头VO
		TzpzHVO pzHeadVO = getTzpzHvo(vo, pzBodyVOs,userid);

		for (TzpzBVO bvo : pzBodyVOs) {
			if(mapCorpAcount.containsKey(bvo.getPk_accsubj())){
				bvo.setVcode(mapCorpAcount.get(bvo.getPk_accsubj()).getAccountcode());
				bvo.setVname(mapCorpAcount.get(bvo.getPk_accsubj()).getAccountname());
			}
			bvo.setPk_corp(pzHeadVO.getPk_corp());
		}
//暂时先不重新排序
//		Arrays.sort(pzBodyVOs, new Comparator<TzpzBVO>() {
//			@Override
//			public int compare(TzpzBVO o1, TzpzBVO o2) {
//				int direct1 = o1.getJfmny() != null && o1.getJfmny().equals(DZFDouble.ZERO_DBL) ? 1 : 0;
//				int direct2 = o2.getJfmny() != null && o2.getJfmny().equals(DZFDouble.ZERO_DBL) ? 1 : 0;
//
//				if(direct1 == direct2){
//					return o1.getVcode().compareTo(o2.getVcode());
//				}
//				return direct1 - direct2;
//			}
//		});


		pzHeadVO.setChildren(pzBodyVOs);

		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pzHeadVO.getPk_corp());
		gl_tzpzserv.saveVoucher(corpvo, pzHeadVO);

	}

	/**
	 * 行业期间损益模板结转设置
	 *
	 * @throws BusinessException
	 */
	public BdTradeTranstemplateHVO[] queryTradeTemplateAggvo(QmclVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		// sp.addParam(vo.getPk_corp());
		String sqlHY = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp= ? and nvl(dr,0)=0 )  and nvl(dr,0)=0  order by iordernum ";
		sp.addParam(vo.getPk_corp());
		// BdTradeTranstemplateHVO[] hvos =(BdTradeTranstemplateHVO[])
		// singleObjectBO.executeQuery(sqlHY, sp, new
		// Class[]{BdTradeTranstemplateHVO.class,BdTradeTranStemPlateBVO.class});
		BdTradeTranstemplateHVO[] hvos = (BdTradeTranstemplateHVO[]) singleObjectBO
				.queryByCondition(BdTradeTranstemplateHVO.class, sqlHY, sp);

		if (hvos == null || hvos.length < 1) {
			throw new BusinessException("公司及行业未设置损益结转模板，请检查！");
		} else {
			// for (BdTradeTranstemplateHVO hvo : hvos) {
			// BdTradeTranStemPlateBVO[] bvos = (BdTradeTranStemPlateBVO[])
			// singleObjectBO
			// .queryByCondition(
			// BdTradeTranStemPlateBVO.class,
			// "pk_trade_transtemplate_h='"
			// + hvo.getPk_trade_transtemplate_h()
			// + "'", new SQLParameter());
			// if (bvos == null || bvos.length < 1) {
			// throw new BusinessException("行业损益结转模板表体转出科目为空，请检查");
			// }
			// hvo.setChildren(bvos);
			// }
		}

		return hvos;
	}

	/**
	 * //根据行业科目期间模板表体转出科目查询对应公司科目，通过科目编码匹配
	 *
	 * @param pk_corp
	 * @param hymbVO
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, YntCpaccountVO> queryCorpAccount(String pk_corp, BdTradeTranstemplateHVO hymbVO)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(hymbVO.getPrimaryKey());
		StringBuffer sqlKm = new StringBuffer();
		// 根据行业科目期间模板表体转出科目查询对应公司科目，通过科目编码匹配
		sqlKm.append(" select corpa.* from ynt_tdtransmb h");
		sqlKm.append(" inner join ynt_tdtransmb_b b on h.pk_trade_transtemplate_h = b.pk_trade_transtemplate_h");
		sqlKm.append(" inner join ynt_cpaccount corpa on corpa.accountcode = b.accountcode");
		sqlKm.append(" where corpa.pk_corp= ?");
		sqlKm.append(" and h.pk_trade_transtemplate_h = ?");
		sqlKm.append(" and nvl(b.dr,0) = 0");
		sqlKm.append(" and nvl(corpa.dr,0) = 0");
		HashMap<String, YntCpaccountVO> mapCorpAcount = new HashMap<String, YntCpaccountVO>();
		ArrayList<YntCpaccountVO> result = (ArrayList<YntCpaccountVO>) singleObjectBO.executeQuery(sqlKm.toString(), sp,
				new BeanListProcessor(YntCpaccountVO.class));
		if (result == null || result.size() == 0) {
			throw new BusinessException("根据行业科目没有找到对应的公司科目，请检查！");
		}
		for (int i = 0; i < result.size(); i++) {
			YntCpaccountVO zckmVO = (YntCpaccountVO) result.get(i);
			mapCorpAcount.put(zckmVO.getAccountcode(), zckmVO);
		}
		return mapCorpAcount;
	}

	// /**
	// * 行业科目期间模板设置转出科目对应的公司科目的本期发生额
	// *
	// * @param pk_corp
	// * @param hymbVO
	// * @return
	// * @throws BusinessException
	// */
	// public HashMap<String, KmZzVO> queryHyKmfs(QmclVO vo,
	// BdTradeTranstemplateHVO hymbVO) throws BusinessException {
	// SQLParameter sp=new SQLParameter();
	// sp.addParam(vo.getPk_corp());
	// sp.addParam(vo.getPeriod());
	// sp.addParam(hymbVO.getPrimaryKey());
	// // 找此科目的本期发生额
	// StringBuffer sqlKmfs = new StringBuffer();
	// sqlKmfs.append(" select b.pk_accsubj,corpa.accountcode kmbm,sum(b.jfmny)
	// as jf,sum(b.dfmny) as df from ynt_tzpz_b b ");
	// sqlKmfs.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
	// sqlKmfs.append(" inner join ynt_cpaccount corpa on corpa.pk_corp_account
	// = b.pk_accsubj ");
	// sqlKmfs.append(" inner join ynt_tdtransmb_b tradeb on tradeb.accountcode
	// = corpa.accountcode ");
	// sqlKmfs.append(" where corpa.pk_corp = ?");
	// sqlKmfs.append(" and h.period = ?");
	// sqlKmfs.append(" and tradeb.pk_trade_transtemplate_h=?");
	// sqlKmfs.append(" and nvl(tradeb.dr,0)=0 ");
	// sqlKmfs.append(" and nvl(b.dr,0)=0 ");
	// sqlKmfs.append(" group by b.pk_accsubj,corpa.accountcode ");
	// ArrayList<KmZzVO> result = (ArrayList<KmZzVO>) singleObjectBO
	// .executeQuery(sqlKmfs.toString(), sp,
	// new BeanListProcessor(KmZzVO.class));
	// if (result == null || result.size() == 0) {
	// return null;
	// }
	// HashMap<String, KmZzVO> mapPz = new HashMap<String, KmZzVO>();
	// for (int i = 0; i < result.size(); i++) {
	// KmZzVO kmzzvo = (KmZzVO) result.get(i);
	// mapPz.put(kmzzvo.getKmbm(), kmzzvo);
	// }
	// return mapPz;
	//
	// }

	/**
	 * 根据公司制定的损益结转模板生成凭证
	 *
	 * @param vo
	 * @param aggvos
	 * @throws BusinessException
	 */
	public void doCorpJz(QmclVO vo, YntCptransmbHVO[] aggvos,String userid) throws DZFWarpException {
		// 默认币种
		String pk_curr = yntBoPubUtil.getCNYPk();
		String abstracts = "结转期间损益";

		int schevalue = yntBoPubUtil.getAccountSchema(vo.getPk_corp());
		// if(DzfUtil.SEVENSCHEMA.intValue() == schevalue ||
		// DzfUtil.THIRTEENSCHEMA.intValue() == schevalue){
		// syCorpGen07or13Pz(vo, aggvos, pk_curr, abstracts);
		// }else{
		syCorpGenOther(vo, aggvos, pk_curr, abstracts,userid);
		// }
		doSave(vo);
	}

	/**
	 * 公司生成模板
	 *
	 * @param vo
	 * @param aggvos
	 * @param pk_curr
	 * @param abstracts
	 */
	private void syCorpGenOther(QmclVO vo, YntCptransmbHVO[] aggvos, String pk_curr, String abstracts,String userid) {
		HashMap<String, List<KmZzVO>> mapPz = queryCorpKmfs(vo);

		if (aggvos == null || aggvos.length == 0) {
			throw new BusinessException("科目方案对应的损益模板为空，请配置!");
		}

		StringBuffer wherepart = new StringBuffer();
		for (YntCptransmbHVO aggvo : aggvos) {
			wherepart.append("'" + aggvo.getPk_corp_transtemplate_h() + "',");
		}

		// YntCptransmbBVO
		String sybsql = "select ynt_cptransmb_b.*,ynt_cpaccount.accountcode as vcode,ynt_cpaccount.accountname as vname from  ynt_cptransmb_b inner join ynt_cpaccount  on pk_corp_account = pk_transferoutaccount where nvl(ynt_cptransmb_b.dr,0)=0 and  ynt_cptransmb_b.pk_corp_transtemplate_h in ("
				+ wherepart.substring(0, wherepart.length() - 1) + ")";

		// 期间损益表体模板
		List<YntCptransmbBVO> syblist = (List<YntCptransmbBVO>) singleObjectBO.executeQuery(sybsql, new SQLParameter(),
				new BeanListProcessor(YntCptransmbBVO.class));

		Map<String, List<YntCptransmbBVO>> sybmap = new HashMap<String, List<YntCptransmbBVO>>();

		if (syblist != null && syblist.size() > 0) {
			for (YntCptransmbBVO sybvo : syblist) {
				if (sybmap.containsKey(sybvo.getPk_corp_transtemplate_h())) {
					sybmap.get(sybvo.getPk_corp_transtemplate_h()).add(sybvo);
				} else {
					List<YntCptransmbBVO> templist = new ArrayList<YntCptransmbBVO>();
					templist.add(sybvo);
					sybmap.put(sybvo.getPk_corp_transtemplate_h(), templist);
				}
			}
		}
		YntCpaccountVO[] corpAccVos = querySykm(vo.getPk_corp());// 查询损益科目

		for (YntCptransmbHVO aggvo : aggvos) {
			if (mapPz == null || mapPz.size() < 1) {
				continue;
			}
			YntCptransmbHVO gsmbVO = (YntCptransmbHVO) aggvo;

			List<YntCptransmbBVO> sylist = sybmap.get(aggvo.getPk_corp_transtemplate_h());// 损益表体vo

			if (sylist == null || sylist.size() == 0) {
				genCorpVoucherForSy(vo, gsmbVO, corpAccVos, mapPz, abstracts, pk_curr,userid);
			} else {
				List<YntCpaccountVO> reslist = new ArrayList<YntCpaccountVO>();
				for (YntCpaccountVO cpavotemp : corpAccVos) {
					for (YntCptransmbBVO cpbvo : sylist) {
						if (cpavotemp.getAccountcode().startsWith(cpbvo.getVcode())) {
							cpavotemp.setDirection(cpavotemp.getDirection());// 取模板的方向
							cpavotemp.setVdef1(cpbvo.getAbstracts() == null ? abstracts : cpbvo.getAbstracts());// 默认是摘要(不存库)
							reslist.add(cpavotemp);
						}
					}
				}
				genCorpVoucherForSy(vo, gsmbVO, reslist.toArray(new YntCpaccountVO[0]), mapPz, abstracts, pk_curr,userid);
			}
		}
	}

	/**
	 * 07和13的凭证生成
	 *
	 * @param vo
	 * @param aggvos
	 * @param pk_curr
	 * @param abstracts
	 */
//	private void syCorpGen07or13Pz(QmclVO vo, YntCptransmbHVO[] aggvos, String pk_curr, String abstracts,String userid) {
//		for (YntCptransmbHVO aggvo : aggvos) {
//			YntCpaccountVO[] corpAccVos = querySykm(vo.getPk_corp());
//
//			HashMap<String, List<KmZzVO>> mapPz = queryCorpKmfs(vo);
//
//			if (mapPz == null || mapPz.size() < 1) {
//				continue;
//			}
//			YntCptransmbHVO gsmbVO = (YntCptransmbHVO) aggvo;
//
//			// 循环两次，一次收入类科目，一次费用+成本科目
//			List<YntCpaccountVO> incomelist = new ArrayList<YntCpaccountVO>();
//			List<YntCpaccountVO> unincomelist = new ArrayList<YntCpaccountVO>();
//
//			if (yntBoPubUtil.is2007AccountSchema(vo.getPk_corp())) {
//				for (YntCpaccountVO cpavo : corpAccVos) {
//					if (cpavo.getAccountcode().startsWith("6001") || cpavo.getAccountcode().startsWith("6051")
//							|| cpavo.getAccountcode().startsWith("6111") || cpavo.getAccountcode().startsWith("6301")) {
//						incomelist.add(cpavo);
//					} else {
//						unincomelist.add(cpavo);
//					}
//
//				}
//			} else {
//				for (YntCpaccountVO cpavo : corpAccVos) {
//					if (cpavo.getAccountcode().startsWith("5001") || cpavo.getAccountcode().startsWith("5051")
//							|| cpavo.getAccountcode().startsWith("5111") || cpavo.getAccountcode().startsWith("5301")) {
//						incomelist.add(cpavo);
//					} else {
//						unincomelist.add(cpavo);
//					}
//
//				}
//
//			}
//			genCorpVoucherForSy(vo, gsmbVO, incomelist.toArray(new YntCpaccountVO[0]), mapPz, abstracts, pk_curr,userid);// 损益生产凭证(收入)
//			genCorpVoucherForSy(vo, gsmbVO, unincomelist.toArray(new YntCpaccountVO[0]), mapPz, abstracts, pk_curr,userid);// 损益生产凭证(成本+费用)
//		}
//	}

	private void genCorpVoucherForSy(QmclVO vo, YntCptransmbHVO gsmbVO, YntCpaccountVO[] corpAccVos,
									 HashMap<String, List<KmZzVO>> mapPz, String abstracts, String pk_curr,String userid) throws DZFWarpException {
		// 转入科目
		String zrkm = gsmbVO.getPk_transferinaccount();
		YntCpaccountVO zrkmVO = gl_cpacckmserv.queryById(zrkm);
//		if (zrkmVO.getIsfzhs() != null && !"0000000000".equals(zrkmVO.getIsfzhs())) {
//			throw new BusinessException(zrkmVO.getAccountcode() + "科目的辅助核算项不允许为空！");
//		}
		// 本期转出科目（借方累计）
		DZFDouble zckm_jflj = DZFDouble.ZERO_DBL;
		// 本期转出科目（贷方累计）
		DZFDouble zckm_dflj = DZFDouble.ZERO_DBL;

		// 转出科目S
		Vector<TzpzBVO> vec_zckms = new Vector<TzpzBVO>();

		for (YntCpaccountVO zckmVO : corpAccVos) {
			// 转出科目
			String zckm = zckmVO.getPk_corp_account();

			DZFDouble ye = DZFDouble.ZERO_DBL;
			DZFDouble jf = DZFDouble.ZERO_DBL;
			DZFDouble df = DZFDouble.ZERO_DBL;
			Integer fx = null;
			List<KmZzVO> kmzzVOlist = null;

			if (zckmVO.getIsfzhs() != null && !"0000000000".equals(zckmVO.getIsfzhs())) {
				List<KmZzVO> mapPZ = queryCorpKmFzhsfs(vo, zckmVO);
				KmZzVO kmzzVO = null;
				if (mapPZ != null && mapPZ.size() > 0) {
					for (int i = 0; i < mapPZ.size(); i++) {
						kmzzVO = (KmZzVO) mapPZ.get(i);
						fx = zckmVO.getDirection() == null ? 0 : zckmVO.getDirection();
						if (kmzzVO == null)
							continue;
						jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getJf();
						df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getDf();

						if (0 == fx.intValue()) {
							// 借方
							ye = jf.sub(df);
							zckm_jflj = zckm_jflj.add(ye);
						} else {
							// 贷方
							ye = df.sub(jf);
							zckm_dflj = zckm_dflj.add(ye);
						}

						if ((jf.doubleValue() == 0 && df.doubleValue() == 0) || ye.doubleValue() == 0) {
							// 此科目此期间没有发生额，则不生成此分录
							continue;
						}
						TzpzBVO bvo = new TzpzBVO();
						bvo.setPk_accsubj(zckm);
						bvo.setPk_inventory(kmzzVO.getPk_inventory());
						bvo.setNnumber(kmzzVO.getNnumber());
						if (bvo.getNnumber() != null && bvo.getNnumber().doubleValue() != 0) {
							bvo.setNprice(ye.div(bvo.getNnumber()));
						}
						bvo.setFzhsx1(kmzzVO.getFzhsx1());
						bvo.setFzhsx2(kmzzVO.getFzhsx2());
						bvo.setFzhsx3(kmzzVO.getFzhsx3());
						bvo.setFzhsx4(kmzzVO.getFzhsx4());
						bvo.setFzhsx5(kmzzVO.getFzhsx5());
						bvo.setFzhsx6(kmzzVO.getFzhsx6());
						bvo.setFzhsx7(kmzzVO.getFzhsx7());
						bvo.setFzhsx8(kmzzVO.getFzhsx8());
						bvo.setFzhsx9(kmzzVO.getFzhsx9());
						bvo.setFzhsx10(kmzzVO.getFzhsx10());
						if (0 == fx.intValue()) {
							// 借方科目，要放在贷方
							bvo.setDfmny(ye);
						} else {
							bvo.setJfmny(ye);
						}
						bvo.setZy(zckmVO.getVdef1());// 摘要
						// 币种，默认人民币
						bvo.setPk_currency(pk_curr);

						vec_zckms.add(bvo);
					}
				} else {
					continue;
				}
			} else {
				fx = zckmVO.getDirection() == null ? 0 : zckmVO.getDirection();

				kmzzVOlist = (List<KmZzVO>) mapPz.get(zckm);

				if (kmzzVOlist == null || kmzzVOlist.size() == 0) {
					continue;
				}
				for (KmZzVO kmzzVO : kmzzVOlist) {
					if (kmzzVO == null)
						continue;
					jf = kmzzVO.getJf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getJf();
					df = kmzzVO.getDf() == null ? DZFDouble.ZERO_DBL : kmzzVO.getDf();

					if (0 == fx.intValue()) {
						// 借方
						ye = jf.sub(df);
						zckm_jflj = zckm_jflj.add(ye);
					} else {
						// 贷方
						ye = df.sub(jf);
						zckm_dflj = zckm_dflj.add(ye);
					}
					if ((jf.doubleValue() == 0 && df.doubleValue() == 0) || ye.doubleValue() == 0) {
						// 此科目此期间没有发生额，则不生成此分录
						continue;
					}
					TzpzBVO bvo = new TzpzBVO();
					bvo.setPk_accsubj(zckm);
					bvo.setPk_inventory(kmzzVO.getPk_inventory());
					bvo.setNnumber(kmzzVO.getNnumber());
					if (bvo.getNnumber() != null && bvo.getNnumber().doubleValue() != 0) {
						bvo.setNprice(ye.div(bvo.getNnumber()));
					}
					bvo.setFzhsx1(kmzzVO.getFzhsx1());
					bvo.setFzhsx2(kmzzVO.getFzhsx2());
					bvo.setFzhsx3(kmzzVO.getFzhsx3());
					bvo.setFzhsx4(kmzzVO.getFzhsx4());
					bvo.setFzhsx5(kmzzVO.getFzhsx5());
					bvo.setFzhsx6(kmzzVO.getFzhsx6());
					bvo.setFzhsx7(kmzzVO.getFzhsx7());
					bvo.setFzhsx8(kmzzVO.getFzhsx8());
					bvo.setFzhsx9(kmzzVO.getFzhsx9());
					bvo.setFzhsx10(kmzzVO.getFzhsx10());
					if (0 == fx.intValue()) {
						// 借方科目，要放在贷方
						bvo.setDfmny(ye);
					} else {
						bvo.setJfmny(ye);
					}
					bvo.setZy(zckmVO.getVdef1());// 摘要
					// 币种，默认人民币
					bvo.setPk_currency(pk_curr);

					vec_zckms.add(bvo);
				}
			}
		}

//		if (zckm_jflj.doubleValue() == 0 && zckm_dflj.doubleValue() == 0) {
//			// 本期未发生，不生成凭证
//			return;
//		}

		DZFDouble res = zckm_jflj.sub(zckm_dflj);
		// 凭证转入科目的分录（本年利润:借方）
		TzpzBVO pzbvo_jf = new TzpzBVO();
		pzbvo_jf.setPk_accsubj(zrkm);
		if (res.doubleValue() > 0) {
			pzbvo_jf.setJfmny(res);
			pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
		} else {
			pzbvo_jf.setJfmny(DZFDouble.ZERO_DBL);
			pzbvo_jf.setDfmny(res.multiply(-1));
		}
		pzbvo_jf.setZy(gsmbVO.getAbstracts());// 摘要
		// 币种，默认人民币
		pzbvo_jf.setPk_currency(pk_curr);

		TzpzBVO[] pzBodyVOs = null;
		// 凭证表体VOs
//		vec_zckms.add(0, pzbvo_jf);

		ArrayList<TzpzBVO> listBvos = new ArrayList<>();
		ArrayList<TzpzBVO> dfBvos = new ArrayList<>();
		for (TzpzBVO pzBodyVO : vec_zckms) {
			if (pzBodyVO.getDfmny() != null && pzBodyVO.getDfmny().compareTo(DZFDouble.ZERO_DBL) != 0) {
				dfBvos.add(pzBodyVO);
			}
			if (pzBodyVO.getJfmny() != null && pzBodyVO.getJfmny().compareTo(DZFDouble.ZERO_DBL) != 0) {
				listBvos.add(pzBodyVO);
			}
		}
		listBvos.addAll(dfBvos);
		// 转入科目始终放在第一行
		listBvos.add(0, pzbvo_jf);
		pzBodyVOs = listBvos.toArray(new TzpzBVO[0]);

		if (zckm_jflj.doubleValue() == 0 && zckm_dflj.doubleValue() == 0
				&& pzBodyVOs.length==1) {
			// 只有本年利润一条分录时不生成凭证,如果是多条，本年利润为零也生成凭证
			return;
		}
		// 凭证表头VO
		TzpzHVO pzHeadVO = getTzpzHvo(vo, pzBodyVOs,userid);

		pzHeadVO.setChildren(pzBodyVOs);

		// 生成凭证
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pzHeadVO.getPk_corp());
		gl_tzpzserv.saveVoucher(corpvo, pzHeadVO);

	}

	/**
	 * 查询公司损益科目
	 *
	 * @throws BusinessException
	 */
	public YntCpaccountVO[] querySykm(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" nvl(dr,0)=0 and nvl(isleaf,'N')='Y'");
		sql.append(" and nvl(accountkind,-1)=5");
		sql.append(" and pk_corp=?");
		YntCpaccountVO[] zckmVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				sql.toString(), sp);
		if (zckmVOs == null || zckmVOs.length < 1) {
			throw new BusinessException("本公司无损益科目，无需结转。");
		}
		HashMap<String, YntCpaccountVO> mapKm = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO zckmVO : zckmVOs) {
			mapKm.put(zckmVO.getPk_corp_account(), zckmVO);
		}
		return zckmVOs;
	}

	/**
	 * 查询公司损益科目
	 *
	 * @throws BusinessException
	 */
	public YntCpaccountVO[] querykm(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" nvl(dr,0)=0 and nvl(isleaf,'N')='Y'");
		sql.append(" and pk_corp=?");
		YntCpaccountVO[] zckmVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				sql.toString(), sp);
		HashMap<String, YntCpaccountVO> mapKm = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO zckmVO : zckmVOs) {
			mapKm.put(zckmVO.getPk_corp_account(), zckmVO);
		}
		return zckmVOs;
	}

	/**
	 * 查询：公司设置的损益结转模板表体转出科目本期发生额。
	 */
	public HashMap<String, List<KmZzVO>> queryCorpKmfs(QmclVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String sql = getCorpSykmpkSql(vo.getPk_corp(), sp);
		StringBuffer sqlKm = new StringBuffer();
		sqlKm.append(
				" select b.pk_accsubj,b.jfmny as jf,b.dfmny as df,b.nnumber as nnumber,b.pk_inventory,")
				.append(" t.direction as fx from ynt_tzpz_b b ");
		sqlKm.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sqlKm.append(" inner join (" + sql + ") t on b.pk_accsubj=t.pk_corp_account ");
		sqlKm.append(" where h.period= ? and nvl(b.dr,0)=0 and nvl(h.dr,0)=0  ");
		sp.addParam(vo.getPeriod());
//		sqlKm.append(" group by b.pk_accsubj,b.pk_inventory ");

		ArrayList<KmZzVO> result = (ArrayList<KmZzVO>) singleObjectBO.executeQuery(sqlKm.toString(), sp,
				new BeanListProcessor(KmZzVO.class));
		if (result == null || result.size() == 0) {
			return null;
		}
		Map<String, KmZzVO> kmVoMap = new HashMap<String, KmZzVO>();
		for (KmZzVO kmZzVO : result) {
			String key = kmZzVO.getPk_accsubj() + kmZzVO.getPk_inventory();
			if (kmVoMap.containsKey(key)) {
				KmZzVO existVo = kmVoMap.get(key);
				existVo.setJf(SafeCompute.add(existVo.getJf(), kmZzVO.getJf()));
				existVo.setDf(SafeCompute.add(existVo.getDf(), kmZzVO.getDf()));
				if ("0".equals(kmZzVO.getFx()) && kmZzVO.getJf() != null
						&& kmZzVO.getJf().doubleValue() != 0
						|| "1".equals(kmZzVO.getFx()) && kmZzVO.getDf() != null
						&& kmZzVO.getDf().doubleValue() != 0) {
					existVo.setNnumber(SafeCompute.add(existVo.getNnumber(), kmZzVO.getNnumber()));
				} else {
					existVo.setNnumber(SafeCompute.sub(existVo.getNnumber(), kmZzVO.getNnumber()));
				}
			} else {
				if ("0".equals(kmZzVO.getFx()) && kmZzVO.getJf() != null
						&& kmZzVO.getJf().doubleValue() != 0
						|| "1".equals(kmZzVO.getFx()) && kmZzVO.getDf() != null
						&& kmZzVO.getDf().doubleValue() != 0) {
					kmZzVO.setNnumber(kmZzVO.getNnumber());
				} else {
					kmZzVO.setNnumber(SafeCompute.sub(DZFDouble.ZERO_DBL, kmZzVO.getNnumber()));
				}
				kmVoMap.put(key, kmZzVO);
			}
		}
		result.clear();
		result.addAll(kmVoMap.values());
		HashMap<String, List<KmZzVO>> mapPz = new HashMap<String, List<KmZzVO>>();
		for (int i = 0; i < result.size(); i++) {
			KmZzVO kmzzvo = (KmZzVO) result.get(i);
			if (mapPz.containsKey(kmzzvo.getPk_accsubj())) {
				mapPz.get(kmzzvo.getPk_accsubj()).add(kmzzvo);
			} else {
				List<KmZzVO> templist = new ArrayList<KmZzVO>();
				templist.add(kmzzvo);
				mapPz.put(kmzzvo.getPk_accsubj(), templist);
			}
		}
		return mapPz;
	}

	/**
	 * 查询：公司设置的损益结转模板表体转出科目辅助核算本期发生额。
	 */
	public List<KmZzVO> queryCorpKmFzhsfs(QmclVO vo, YntCpaccountVO accvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String sql = accvo.getPk_corp_account();
		StringBuffer sqlKm = new StringBuffer();
		sqlKm.append(
				" select b.pk_accsubj,b.jfmny as jf,b.dfmny as df,b.nnumber as nnumber,")
				.append("b.pk_inventory,b.fzhsx1,b.fzhsx2,b.fzhsx3,b.fzhsx4,")
				.append("b.fzhsx5,b.fzhsx6,b.fzhsx7,b.fzhsx8,b.fzhsx9,b.fzhsx10, t.direction as fx from ynt_tzpz_b b ");
		sqlKm.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sqlKm.append(
				" inner join ( select pk_corp_account, direction from ynt_cpaccount where pk_corp_account = ?) t on b.pk_accsubj=t.pk_corp_account ");
		sqlKm.append(" where h.period= ? and nvl(b.dr,0)=0 and nvl(h.dr,0)=0  ");
//		sqlKm.append(
//				" group by fzhsx1,fzhsx2,fzhsx3,fzhsx4,fzhsx5,fzhsx6,fzhsx7,fzhsx8,fzhsx9,fzhsx10,b.pk_accsubj,b.pk_inventory ");
		sp.addParam(sql);
		sp.addParam(vo.getPeriod());

		ArrayList<KmZzVO> result = (ArrayList<KmZzVO>) singleObjectBO.executeQuery(sqlKm.toString(), sp,
				new BeanListProcessor(KmZzVO.class));
		if (result == null || result.size() == 0) {
			return null;
		}
		Map<String, KmZzVO> kmVoMap = new HashMap<String, KmZzVO>();
		for (KmZzVO kmZzVO : result) {
			StringBuilder key = new StringBuilder();
			key.append(kmZzVO.getPk_accsubj()).append(",").append(kmZzVO.getPk_inventory())
					.append(ReportUtil.getFzKey(kmZzVO));
			String keyStr = key.toString();
			if (kmVoMap.containsKey(keyStr)) {
				KmZzVO existVo = kmVoMap.get(keyStr);
				existVo.setJf(SafeCompute.add(existVo.getJf(), kmZzVO.getJf()));
				existVo.setDf(SafeCompute.add(existVo.getDf(), kmZzVO.getDf()));
				if ("0".equals(kmZzVO.getFx()) && kmZzVO.getJf() != null
						&& kmZzVO.getJf().doubleValue() != 0
						|| "1".equals(kmZzVO.getFx()) && kmZzVO.getDf() != null
						&& kmZzVO.getDf().doubleValue() != 0) {
					existVo.setNnumber(SafeCompute.add(existVo.getNnumber(), kmZzVO.getNnumber()));
				} else {
					existVo.setNnumber(SafeCompute.sub(existVo.getNnumber(), kmZzVO.getNnumber()));
				}
			} else {
				if ("0".equals(kmZzVO.getFx()) && kmZzVO.getJf() != null
						&& kmZzVO.getJf().doubleValue() != 0
						|| "1".equals(kmZzVO.getFx()) && kmZzVO.getDf() != null
						&& kmZzVO.getDf().doubleValue() != 0) {
					kmZzVO.setNnumber(kmZzVO.getNnumber());
				} else {
					kmZzVO.setNnumber(SafeCompute.sub(DZFDouble.ZERO_DBL, kmZzVO.getNnumber()));
				}
				kmVoMap.put(keyStr, kmZzVO);
			}
		}
		result.clear();
		result.addAll(kmVoMap.values());
		/*
		 * HashMap<String, KmZzVO> mapPz = new HashMap<String, KmZzVO>(); for
		 * (int i = 0; i < result.size(); i++) { KmZzVO kmzzvo = (KmZzVO)
		 * result.get(i); mapPz.put(kmzzvo.getPk_accsubj(), kmzzvo); }
		 */
		// return mapPz;
		return result;
	}

	/**
	 * 公司损益科目主键查询语句
	 *
	 * @return
	 */
	public String getCorpSykmpkSql(String pk_corp, SQLParameter sp) {
		// sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" select pk_corp_account, direction from ynt_cpaccount");
		sql.append(" where nvl(dr,0)=0 and nvl(isleaf,'N')='Y'");
		sql.append(" and nvl(accountkind,-1)=5");
		sql.append(" and pk_corp='" + pk_corp + "'");
		return sql.toString();
	}

	/**
	 * 组织凭证主表数据
	 *
	 * @param vo
	 * @param pzBodyVOs
	 * @param userid
	 * @throws BusinessException
	 */
	public TzpzHVO getTzpzHvo(QmclVO vo, TzpzBVO[] pzBodyVOs,String userid) throws DZFWarpException {
		DZFDouble headJfmny = DZFDouble.ZERO_DBL;
		DZFDouble headDfmny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bodyVO : pzBodyVOs) {
			headJfmny = headJfmny.add(bodyVO.getJfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getJfmny());
			headDfmny = headDfmny.add(bodyVO.getDfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getDfmny());
		}
		// 凭证表头VO
		TzpzHVO pzHeadVO = new TzpzHVO();
		pzHeadVO.setPk_corp(vo.getPk_corp());
		pzHeadVO.setVyear(Integer.parseInt(vo.getPeriod().substring(0, 4)));
		pzHeadVO.setPzlb(0);// 凭证类别：记账
		pzHeadVO.setPeriod(vo.getPeriod());
		pzHeadVO.setJfmny(headJfmny);
		pzHeadVO.setDfmny(headDfmny);

		pzHeadVO.setCoperatorid(userid);
		pzHeadVO.setIshasjz(DZFBoolean.FALSE);

		DZFDate doperatedate = getPeroidDZFDate(vo);
		pzHeadVO.setDoperatedate(doperatedate);
		pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), doperatedate));
		pzHeadVO.setVbillstatus(8);

		// 记录来源单据
		pzHeadVO.setSourcebilltype(IBillTypeCode.HP32);
		pzHeadVO.setSourcebillid(vo.getPk_qmcl());
		return pzHeadVO;
	}

	/**
	 * 取期间所属月的最好一天
	 *
	 * @param vo
	 * @return
	 */
	public DZFDate getPeroidDZFDate(QmclVO vo) {
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth());
		return period;
	}

	/**
	 * 公司期间损益模板结转设置
	 *
	 * @throws BusinessException
	 */
	public YntCptransmbHVO[] queryCorpTemplateAggvo(QmclVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String sql = "select * from ynt_cptransmb where pk_corp= ? and nvl(dr,0)=0 ";
		sp.addParam(vo.getPk_corp());

		// YntCptransmbHVO[] hvos = (YntCptransmbHVO[])
		// singleObjectBO.executeQuery(sql, sp,new
		// Class[]{YntCptransmbHVO.class,YntCptransmbBVO.class});
		List<YntCptransmbHVO> cplist = (List<YntCptransmbHVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(YntCptransmbHVO.class));
		YntCptransmbHVO[] hvos = cplist.toArray(new YntCptransmbHVO[0]);

		// for (YntCptransmbHVO hvo : hvos) {
		//
		// YntCptransmbBVO[] bvos = (YntCptransmbBVO[]) singleObjectBO
		// .queryByCondition(
		// YntCptransmbBVO.class,
		// "pk_corp_transtemplate_h='"
		// + hvo.getPk_corp_transtemplate_h()
		// + "' and nvl(dr,0)=0", new SQLParameter());
		//
		// hvo.setChildren(bvos);
		// }

		return hvos;
	}

	private String[] getCorps(SuperVO[] vos) {
		if (vos == null)
			return null;
		HashSet<String> hs = new HashSet<String>();
		for (SuperVO qmclvo : vos) {
			hs.add((String) qmclvo.getAttributeValue("pk_corp"));
		}
		return hs.toArray(new String[0]);
	}

	private String[] getNotInCorps(String[] corps, SuperVO[] vos) {
		if (vos == null)
			return null;
		HashSet<String> hs = new HashSet<String>();
		for (SuperVO qmclvo : vos) {
			hs.add((String) qmclvo.getAttributeValue("pk_corp"));
		}
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < corps.length; i++) {
			if (hs.contains(corps[i]) == false)
				l.add(corps[i]);
		}
		return l.size() == 0 ? null : l.toArray(new String[0]);
	}

	private void doSave(QmclVO[] vos) throws DZFWarpException {
		List<QmclVO> q1 = new ArrayList<QmclVO>();
		List<QmclVO> q2 = new ArrayList<QmclVO>();
		for (QmclVO qmclvo : q2) {
			qmclvo.setIsqjsyjz(DZFBoolean.TRUE);
			if (StringUtil.isEmpty(qmclvo.getPrimaryKey()))
				q2.add(qmclvo);
			else
				q1.add(qmclvo);
		}
		singleObjectBO.insertVOArr(q2.get(0).getPk_corp(), q2.toArray(new SuperVO[0]));
		singleObjectBO.updateAry(q1.toArray(new SuperVO[0]));
	}

	private void doSave(QmclVO vo) throws DZFWarpException {
		vo.setIsqjsyjz(DZFBoolean.TRUE);
		if (StringUtil.isEmpty(vo.getPrimaryKey())) {
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		} else
			singleObjectBO.update(vo, new String[] { "isqjsyjz" });
	}

	/*
	 * 反计提折旧
	 */
	public QmclVO updateFanJiTiZheJiu(QmclVO vos) throws DZFWarpException {

		DZFBoolean isqjsyjz = vos.getIsqjsyjz();

		// 如果已经年结账，则不能反计提折旧
		updateCheckNj(vos);
		// 如果关账，抛出异常
		vos = checkisGz(vos, "不能反计提折旧！");
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+vos.getPeriod()+"，已经损益结转，不能反计提折旧！");
		}

		SQLParameter sp = new SQLParameter();
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period>?  and pk_corp= ? order by period  ";
		sp.addParam(vos.getPeriod());
		sp.addParam(vos.getPk_corp());
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		if (value != null && value.size() > 0) {
			if (value.get(0).getIszjjt() != null && value.get(0).getIszjjt().booleanValue()) {
				throw new BusinessException("存在后续期间的计提数据，不能跨月反计提折旧!");
			}
		}

		if (vos.getIszjjt() == null || !vos.getIszjjt().booleanValue()) {
			throw new BusinessException("没有进行计提折旧，不能反计提！");
		}

		// 禁止多期间同时操作
		// Vector<String> vec_period = new Vector<String>();
		// for (QmclVO vo : vos) {
		// if (vec_period.isEmpty()) {
		// vec_period.add(vo.getPeriod());
		// } else {
		// if (!vec_period.contains(vo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行反计提折旧");
		// }
		// }
		// // ly 判断选择期间之后是否已经结转，如果结转不能反结转
		// // if(!canReverse(vo,"nvl(iszjjt,null)='Y'")){
		// // throw new BusinessException("此期间后期已经已经计提折旧，不能反计提折旧");
		// // }
		// // end
		// }

		vos = assetCardImpl.rollbackDepreciate(vos);
		return vos;
	}

	/**
	 * 已经年结不能反操作
	 *
	 * @param vos
	 */
	private void updateCheckNj(QmclVO vos) {
		SQLParameter sp = new SQLParameter();
		String qmjzsqlwhere = "select * from YNT_QMJZ  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp.addParam(vos.getPk_corp());
		sp.addParam(vos.getPeriod().substring(0, 4) + "-12");
		List<QmJzVO> qmjzlist = (List<QmJzVO>) singleObjectBO.executeQuery(qmjzsqlwhere, sp,
				new BeanListProcessor(QmJzVO.class));
		if (qmjzlist != null && qmjzlist.size() > 0) {
			if (qmjzlist.get(0).getJzfinish() != null && qmjzlist.get(0).getJzfinish().booleanValue()) {
				throw new BusinessException("公司已经年结,不能反操作!");
			}
		}
	}

	/**
	 * 校验是否可做反操作
	 *
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	private boolean canReverse(QmclVO vo, String strWhere) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String strwhere = "select pk_qmcl from ynt_qmcl where  pk_corp= ? and  period>? and period<?  and " + strWhere;
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPeriod());
		sp.addParam(vo.getDoperatedate().getYear() + "-12");
		Object pk_qmcl = singleObjectBO.executeQuery(strwhere, sp, new ColumnProcessor());
		if (pk_qmcl != null) {
			return false;
		}
		return true;
	}

	/*
	 * 反损益结转
	 */
	public QmclVO updateFanQiJianSunYiJieZhuan(QmclVO vos) throws DZFWarpException {

		if (vos == null) {
			throw new BusinessException("没有需要反损益结转的数据！");
		}

		updateCheckNj(vos);

		// 如果关账，抛出异常
		vos = checkisGz(vos, "不能反损益结转！");

		// Vector<String> vec_period = new Vector<String>();
		// for (QmclVO vo : vos) {
		// if (vec_period.isEmpty()) {
		// vec_period.add(vo.getPeriod());
		// } else {
		// if (!vec_period.contains(vo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行反损益结转");
		// }
		// }
		// }

		// for (QmclVO vo : vos) {
		QmclVO vo = vos;
		checkLirunjz(vo.getPeriod(), vo.getPk_corp());
		DZFBoolean isqjsyjz = vo.getIsqjsyjz();
		if (isqjsyjz == null || !isqjsyjz.booleanValue()) {
			throw new BusinessException("期间" + vos.getPeriod() + "没进行损益结转!");
		}

		checkNextPeriod(vo);
		// 公司

		// 检查公司是否需要损益结转
//		CorpVO corpVO = CorpCache.getInstance().get(null, vo.getPk_corp());// (CorpVO)
		// singleObjectBO.queryByPrimaryKey(
		// CorpVO.class, pk_corp);
		ICorpService corpService = SpringUtils.getBean(ICorpService.class);
		CorpVO corpVO = corpService.queryByPk(vo.getPk_corp());
		if (corpVO == null) {
			throw new BusinessException("公司主键为" + vo.getPk_corp() + "的公司已被删除！");
		}
		// 查找已经生成过的损益结转凭证，并删除之
		String pk_qmcl = vo.getPrimaryKey();
		SQLParameter sp = new SQLParameter();
		String wppzh = " select * from ynt_tzpz_h where  sourcebillid= ? and sourcebilltype= ? and nvl(dr,0)=0 ";
		sp.addParam(pk_qmcl);
		sp.addParam(IBillTypeCode.HP32);
		List<TzpzHVO> tzlist = (List<TzpzHVO>) singleObjectBO.executeQuery(wppzh, sp,
				new BeanListProcessor(TzpzHVO.class));
		TzpzHVO[] pzHeadVOs = tzlist.toArray(new TzpzHVO[0]);
		if (pzHeadVOs != null && pzHeadVOs.length > 0) {
			for (TzpzHVO headVO : pzHeadVOs) {
				if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
					// 已有凭证记账
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能反操作！");
				}
				if (headVO.getVbillstatus() == 1) {
					// 已有凭证审核通过
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能反操作！");
				}
				// 先删除表体
				sp.clearParams();
				String updatebsql = "update ynt_tzpz_b  set dr =1   where  pk_tzpz_h= ?   ";
				sp.addParam(headVO.getPrimaryKey());
				singleObjectBO.executeUpdate(updatebsql, sp);
				// 再删除表头
				String updatehsql = "update ynt_tzpz_h  set dr = 1 where  pk_tzpz_h= ? ";
				sp.clearParams();
				sp.addParam(headVO.getPrimaryKey());
				singleObjectBO.executeUpdate(updatehsql, sp);
				// 删除收入数量辅助表，这里之所以这么加，是因为存在错误数据了。源头那里我已经改了
				// gl_tzpzserv.saveVoucher 这里 CreateicBill
				String delsubinv = "delete ynt_subinvtory  where  pk_tzpz_h= ? ";
				sp.clearParams();
				sp.addParam(headVO.getPrimaryKey());
				singleObjectBO.executeUpdate(delsubinv, sp);
			}
		}

		// 去掉勾上损益结转
		vo.setIsqjsyjz(DZFBoolean.FALSE);
		// 更新期末结转
		singleObjectBO.update(vo, new String[]{ "isqjsyjz" });

		// }
		return vos;
	}

	@Override
	public List<QmclVO> initquery(List<String> corppks, DZFDate dateq, DZFDate datez, String userid, DZFDate dopedate,
								  DZFBoolean iscarover, DZFBoolean isuncarover) throws DZFWarpException {
		// 处理结账逻辑
		// 1、检查所选公司、期间的所有凭证是否全部都记账；
		// String[] pk_corps = pane.getRefPKs();
		if (corppks == null || corppks.size() == 0)
			return null;
		String[] pk_corps = corppks.toArray(new String[0]);
		if (pk_corps == null || pk_corps.length < 1) {
			throw new BusinessException("公司不能为空！");
		}
		// 期间起
		if (dateq == null) {
			throw new BusinessException("期间起不能为空！");
		}
		if (datez == null) {
			throw new BusinessException("期间至不能为空！");
		}
		if (datez.before(dateq)) {
			throw new BusinessException("期间至不能在期间起之前！");
		}

		if (iscarover != null && iscarover.booleanValue() && isuncarover != null && isuncarover.booleanValue()) {
			throw new BusinessException("数据为空!");
		}

		Map<String, CorpVO> corpmap = new HashMap<String, CorpVO>();

		getCorpList(pk_corps, corpmap);

		List<QmclVO> vec = new ArrayList<QmclVO>();
		if (dateq.getYear() == datez.getYear()) {
			for (String pk_corp : pk_corps) {
				for (int i = dateq.getMonth(); i <= datez.getMonth(); i++) {
					QmclVO qmclVO = new QmclVO();
					qmclVO.setPk_corp(pk_corp);
					qmclVO.setCoperatorid(userid);
					qmclVO.setDoperatedate(dopedate);
					qmclVO.setPeriod(dateq.getYear() + "-" + (i < 10 ? "0" + i : i));

					// 查询公司、期间是已经做过期末结转
					try {
						// 检查期间起不能在公司建账日期之前
						CorpVO corpVO = (CorpVO) corpmap.get(pk_corp);
						if (corpVO == null) {
							throw new BusinessException("该公司不存在！");
						}
						if (corpVO.getBegindate() == null) {
							throw new BusinessException(
									"公司:'" + deCodename(corpVO.getUnitname()) + "'的建账日期为空，可能尚未建账，请检查!");
						}
						DZFDate corpdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate()));
						if (dateq.before(corpdate)) {
							throw new BusinessException("期间起不能在所选公司:'" + deCodename(corpVO.getUnitname()) + "'的建账日期之前！");
						}
						String wpcl = " select * from ynt_qmcl  where pk_corp= ? and period= ? and nvl(dr,0)=0 ";
						SQLParameter sp = new SQLParameter();
						sp.addParam(pk_corp);
						sp.addParam(qmclVO.getPeriod());
						List<QmclVO> listqmcl = (List<QmclVO>) singleObjectBO.executeQuery(wpcl, sp,
								new BeanListProcessor(QmclVO.class));
						QmclVO[] vos = listqmcl.toArray(new QmclVO[0]);
						if (vos != null && vos.length > 0) {
							qmclVO = vos[0];
						}
						String wbsql = "select count(1) from YNT_CPACCOUNT  WHERE PK_CORP = ? AND  nvl(iswhhs,'N')='Y'  and nvl(dr,0)=0 ";
						sp.clearParams();
						sp.addParam(pk_corp);
						BigDecimal wbvalue = (BigDecimal) singleObjectBO.executeQuery(wbsql, sp, new ColumnProcessor());
						qmclVO.setIkc(new DZFBoolean(IcCostStyle.IC_ON.equals(corpVO.getBbuildic())));// 库存
						if (corpVO.getIcostforwardstyle() != null
								&& corpVO.getIcostforwardstyle().intValue() == IQmclConstant.z0) {// 如果是直接成本结转也不提示
							qmclVO.setIkc(DZFBoolean.FALSE);
						}
						qmclVO.setIgdzc(corpVO.getHoldflag() == null ? DZFBoolean.FALSE : corpVO.getHoldflag());// 是否启用固定资产

						if (wbvalue != null && wbvalue.intValue() > 0) {
							qmclVO.setIwb(DZFBoolean.TRUE);// 外币
						} else {
							qmclVO.setIwb(DZFBoolean.FALSE);
						}
						//zpm 2018.8.30
						Integer icostype = corpVO.getIcostforwardstyle() == null ? IQmclConstant.z0 : corpVO.getIcostforwardstyle();
						qmclVO.setIcosttype(icostype);
						if ("一般纳税人".equals(corpVO.getChargedeptname())) {
							qmclVO.setIsybr(DZFBoolean.TRUE);
						} else {
							qmclVO.setIsybr(DZFBoolean.FALSE);
						}
						qmclVO.setJzdate(corpVO.getBegindate());
						vec.add(qmclVO);
					} catch (BusinessException e1) {
						throw new BusinessException("检查出错：" + e1.getMessage());
					}
				}
			}
		} else {
			// 存在跨年查询
			// 先循环年
			int yearcount = datez.getYear() - dateq.getYear();
			DZFDate comdateq = DateUtils.getPeriodStartDate(DateUtils.getPeriod(dateq));
			DZFDate comdatez = DateUtils.getPeriodEndDate(DateUtils.getPeriod(datez));
			for (String pk_corp : pk_corps) {
				for (int k = 0; k < yearcount + 1; k++) {
					for (int i = 1; i <= 12; i++) {
						DZFDate tempdate = DateUtils
								.getPeriodStartDate((dateq.getYear() + k) + "-" + (i < 10 ? "0" + i : i));
						if (tempdate.before(comdateq) || tempdate.after(comdatez)) {
							continue;
						}
						QmclVO qmclVO = new QmclVO();
						qmclVO.setPk_corp(pk_corp);
						qmclVO.setCoperatorid(userid);
						qmclVO.setDoperatedate(dopedate);
						qmclVO.setPeriod((dateq.getYear() + k) + "-" + (i < 10 ? "0" + i : i));
						// 查询公司、期间是已经做过期末结转
						try {
							// 检查期间起不能在公司建账日期之前
							CorpVO corpVO = (CorpVO) corpmap.get(pk_corp);
							if (corpVO == null) {
								throw new BusinessException("该公司不存在！");
							}
							if (corpVO.getBegindate() == null) {
								throw new BusinessException(
										"公司:'" + deCodename(corpVO.getUnitname()) + "'的建账日期为空，可能尚未建账，请检查!");
							}
							DZFDate corpdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate()));
							if (dateq.before(corpdate)) {
								throw new BusinessException("期间起不能在所选公司:'" + deCodename(corpVO.getUnitname())
										+ "'的建账日期:" + corpVO.getBegindate() + "之前！");
							}

							String wpqmcl = " pk_corp= ? and period= ? and nvl(dr,0)=0 ";
							SQLParameter sp = new SQLParameter();
							sp.addParam(pk_corp);
							sp.addParam(qmclVO.getPeriod());
							QmclVO[] vos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, wpqmcl, sp);
							if (vos != null && vos.length > 0) {
								qmclVO = vos[0];
							}
							String wbsql = "select count(1) from YNT_CPACCOUNT  WHERE PK_CORP = ?  AND  nvl(iswhhs,'N')='Y' and nvl(dr,0)=0";
							sp.clearParams();
							sp.addParam(pk_corp);
							BigDecimal wbvalue = (BigDecimal) singleObjectBO.executeQuery(wbsql, sp,
									new ColumnProcessor());
							qmclVO.setIkc(new DZFBoolean(IcCostStyle.IC_ON.equals(corpVO.getBbuildic())));// 库存
							if (corpVO.getIcostforwardstyle() != null
									&& corpVO.getIcostforwardstyle().intValue() == IQmclConstant.z0) {// 如果是直接成本结转也不提示
								qmclVO.setIkc(DZFBoolean.FALSE);
							}
							qmclVO.setIgdzc(corpVO.getHoldflag() == null ? DZFBoolean.FALSE : corpVO.getHoldflag());// 是否启用固定资产
							if (wbvalue != null && wbvalue.intValue() > 0) {
								qmclVO.setIwb(DZFBoolean.TRUE);// 外币
							} else {
								qmclVO.setIwb(DZFBoolean.FALSE);
							}
							qmclVO.setIcosttype(corpVO.getIcostforwardstyle() == null ? IQmclConstant.z0
									: corpVO.getIcostforwardstyle());

							if ("一般纳税人".equals(corpVO.getChargedeptname())) {
								qmclVO.setIsybr(DZFBoolean.TRUE);
							} else {
								qmclVO.setIsybr(DZFBoolean.FALSE);
							}
							qmclVO.setJzdate(corpVO.getBegindate());
							vec.add(qmclVO);
						} catch (BusinessException e1) {
							throw new BusinessException("检查出错：" + e1.getMessage());
						}
					}

				}
			}

		}

		DZFBoolean isjzover = null;

		if (iscarover != null && iscarover.booleanValue()) {
			isjzover = DZFBoolean.TRUE;
		}
		if (isuncarover != null && isuncarover.booleanValue()) {
			isjzover = DZFBoolean.FALSE;
		}

		// 如果数据没存储自动存储
		List<QmclVO> reslist = new ArrayList<QmclVO>();
		for (QmclVO qmclvo : vec) {
			if (StringUtil.isEmpty(qmclvo.getPrimaryKey())) {
				// 自动保存
				QmclVO votemp = (QmclVO) singleObjectBO.saveObject(qmclvo.getPk_corp(), qmclvo);
				votemp.setIsqjsyjz(votemp.getIsqjsyjz() == null ? DZFBoolean.FALSE : votemp.getIsqjsyjz());
				if (isjzover == null) {
					reslist.add(votemp);
				} else if (isjzover != null && isjzover.booleanValue() == votemp.getIsqjsyjz().booleanValue()) {
					reslist.add(votemp);
				}
			} else {
				qmclvo.setIsqjsyjz(qmclvo.getIsqjsyjz() == null ? DZFBoolean.FALSE : qmclvo.getIsqjsyjz());
				if (isjzover == null) {
					reslist.add(qmclvo);
				} else if (isjzover != null && isjzover.booleanValue() == qmclvo.getIsqjsyjz().booleanValue()) {
					reslist.add(qmclvo);
				}
			}
		}

		for (QmclVO qmcl : reslist) {
			CorpVO cpvo = corpmap.get(qmcl.getPk_corp());
			qmcl.setIscbjz(qmcl.getIscbjz() == null ? DZFBoolean.FALSE : qmcl.getIscbjz());
			qmcl.setCbjz1(qmcl.getCbjz1() == null ? DZFBoolean.FALSE : qmcl.getCbjz1());
			// qmcl.setCbjz2(qmcl.getCbjz2() == null ? DZFBoolean.FALSE :
			// qmcl.getCbjz2());
			qmcl.setCbjz3(qmcl.getCbjz3() == null ? DZFBoolean.FALSE : qmcl.getCbjz3());
			qmcl.setCbjz4(qmcl.getCbjz4() == null ? DZFBoolean.FALSE : qmcl.getCbjz4());
			qmcl.setCbjz5(qmcl.getCbjz5() == null ? DZFBoolean.FALSE : qmcl.getCbjz5());
			qmcl.setCbjz6(qmcl.getCbjz6() == null ? DZFBoolean.FALSE : qmcl.getCbjz6());
			if (cpvo != null) {
				qmcl.setCorpname(CorpSecretUtil.deCode(cpvo.getUnitname()));
			}

		}
		return reslist;
	}

	public List<TzpzHVO> queryQmclGlpz(String period, String pk_corp, String sourcebilltype) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sql = new StringBuffer("select * from YNT_TZPZ_H  where nvl(dr,0)=0");
		if(!StringUtil.isEmpty(pk_corp)){
			sql.append(" and pk_corp = ?");
			sp.addParam(pk_corp);
		}

		if(!StringUtil.isEmpty(period)){
			sql.append(" and period = ?");
			sp.addParam(period);
		}

		if(!StringUtil.isEmpty(sourcebilltype)){
			sql.append(" and sourcebilltype = ?");
			sp.addParam(sourcebilltype);
		}else{
			sql.append(" and sourcebilltype in ('HP34','HP32','HCH10535','HP39','HP120','HP125','HP67')");
		}

		List<TzpzHVO> tzpzHVOS = (List<TzpzHVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		return tzpzHVOS;
	}

	private void getCorpList(String[] pk_corps, Map<String, CorpVO> corpmap) {
		String corpsql = "select * from bd_corp where nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_corp", pk_corps);

		List<CorpVO> corplist = (List<CorpVO>) singleObjectBO.executeQuery(corpsql, new SQLParameter(),
				new BeanListProcessor(CorpVO.class));

		if (corplist != null && corplist.size() > 0) {
			for (CorpVO cpvo : corplist) {
				corpmap.put(cpvo.getPk_corp(), cpvo);
			}
		}
	}

	// 汇兑
	@Override
	public ExrateVO[] queryAdjust(QmclVO qmvo) throws DZFWarpException {
		// String[] corps = new String[qmvos.length];
		// for(int i = 0 ;i<qmvos.length;i++){
		// corps[i] = qmvos[i].getPk_corp();
		// }
		// String wpex = " nvl(dr,0) = ? and "+SqlUtil.buildSqlForIn("pk_corp",
		// corps);
		String wpex = " nvl(dr,0) = ? and pk_corp = ?  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(0);
		sp.addParam(qmvo.getPk_corp());
		ExrateVO[] values = (ExrateVO[]) singleObjectBO.queryByCondition(ExrateVO.class, wpex, sp);
		if(values != null && values.length > 0 ){
			BdCurrencyVO[] vos = queryCurrency();
			Map<String,BdCurrencyVO> maps = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos),new String[]{"pk_currency"});
			for (ExrateVO vo : values) {
				vo.setAdjustrate(vo.getExrate());
				vo.setCurrcode(maps.get(vo.getPk_currency()).getCurrencycode());
				vo.setCurrname(maps.get(vo.getPk_currency()).getCurrencyname());
				vo.setExratecode(maps.get(vo.getPk_currency()).getCurrencycode());
				vo.setExratename(maps.get(vo.getPk_currency()).getCurrencyname());
			}
		}
		return values;
	}

	public BdCurrencyVO[] queryCurrency() throws DZFWarpException {
		String condition = " pk_corp =  ? and nvl(dr,0) = 0 order by currencycode  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IGlobalConstants.DefaultGroup);
		BdCurrencyVO[] vos = (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, condition, sp);
		if (vos == null || vos.length == 0)
			return null;
		return vos;
	}

	private String deCodename(String corpName) {
		String realName = "";
		try {
			realName = CorpSecretUtil.deCode(corpName);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		return realName;
	}

	/*
		期末处理增加校验 暂估未识别凭证
	 */
	public void checkTemporaryIsExist(String pk_corp, String period, boolean isbat,String message) {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		String sql = "select * from ynt_tzpz_h where VBILLSTATUS = -1 and nvl(IAUTORECOGNIZE,0) != 1 and PERIOD = ? and PK_CORP = ? and nvl(dr,0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(period);
		sp.addParam(pk_corp);

		StringBuffer error = new StringBuffer();

		if(singleObjectBO.isExists(pk_corp,sql,sp)){
			error.append(", 存在暂存未识别凭证");
			if(!isbat && error.length() > 0){
				error = new StringBuffer("公司:" + deCodename(corpvo.getUnitname()) + ",期间:"+period).append(error);
			}
		}

		if(error.toString().length()>0){
			throw new BusinessException(error.toString()+", "+message);
		}
	}
	/**
	 * 成本结转
	 */
	@Override
	public QmclVO saveCbjz(QmclVO qmvo,String userid) throws DZFWarpException {
		// 成本结转校验
		checkisGz(qmvo, "不能成本结转！");
		String key = qmvo.getPk_corp() + "," + qmvo.getPeriod();
		QmclVO result = null;
		try {
			if (!StringUtil.isEmpty(qmvo.getPrimaryKey())) {
				QmclVO vo1 = (QmclVO) singleObjectBO.queryByPrimaryKey(QmclVO.class, qmvo.getPrimaryKey());
				if (vo1 != null && vo1.getIscbjz() != null && vo1.getIscbjz().booleanValue()) {
					throw new BusinessException("成本已经结转，不能重复结转！");
				}
			}
			CorpVO corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, qmvo.getPk_corp());// 防止vo信息有变化
			boolean dzfflag = qmjzByDzfConfig.dzf_pk_gs.equals(qmvo.getPk_corp());
			if(dzfflag){
				Cbjz_DZF dzf = new Cbjz_DZF(yntBoPubUtil, singleObjectBO, voucher, gl_cpacckmserv, gl_accountcoderule, gl_fzhsserv);
				result = dzf.save(corpVo, qmvo, userid);
			}else if (IQmclConstant.z1 == corpVo.getIcostforwardstyle()) {// 比例结转
				Cbjz_2 cj = new Cbjz_2(yntBoPubUtil, singleObjectBO, voucher);
				result = cj.save(corpVo, qmvo,userid);
			} else if (IQmclConstant.z2 == corpVo.getIcostforwardstyle()) {// 商贸业销售结转，，不启用库存的商贸结转，走gl/gl_qmclnoicact!savetopz.action
				Cbjz_3 cj = new Cbjz_3(gl_cbconstant, yntBoPubUtil, singleObjectBO, voucher, ic_rep_cbbserv,parameterserv);
				// 老模式 启用库存
				if (corpVo.getIbuildicstyle() == null || corpVo.getIbuildicstyle() != 1) {
					result = cj.save(corpVo, qmvo,userid);// 库存老模式
				} else {
					result = cj.savemode2(corpVo, qmvo,userid);// 库存新模式
				}
			} else if (IQmclConstant.z3 == corpVo.getIcostforwardstyle()) {// 工业销售结转，专门的代码
			} else {// 当有情于 0 或者空的情况
				Cbjz_1 cj = new Cbjz_1(yntBoPubUtil, singleObjectBO, voucher);
				result = cj.save(corpVo, qmvo);
			}
			if (result != null) {
				result.setZgdata(null);
			}
		} catch (ExBusinessException e1) {
			throw e1;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}
//			else if(e instanceof DAOException){
//				throw new BusinessException("数据库异常");
//			}
			else{
				throw e;
			}
		} finally {
		}
		return result;
	}

	/**
	 * 工业成本结转
	 */
	public QmclVO saveIndustryJZ(TransFerVOInfo fervos, String userid) throws DZFWarpException {
		Cbjz_4 cj = new Cbjz_4(gl_cbconstant, yntBoPubUtil, singleObjectBO, voucher, ic_rep_cbbserv, ic_purchinserv,parameterserv);
		QmclVO qmvo = fervos.getQmvo();
		CorpVO corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, qmvo.getPk_corp());
		QmclVO vo = cj.save(corpVo, fervos,userid);
		return vo;
	}

	/**
	 * 反成本结转
	 */
	@Override
	public QmclVO rollbackCbjz(QmclVO vos) throws DZFWarpException {
		if(vos!=null && (vos.getIscbjz() == null ||  !vos.getIscbjz().booleanValue())){
			throw new BusinessException("未成本结转不能反结转！");
		}
		updateCheckNj(vos);
		vos = checkisGz(vos, "不能反成本结转！");
		CancelCbjz cbjz = new CancelCbjz(singleObjectBO);
		List<QmclVO> list = cbjz.rollbackCbjz(new QmclVO[] { vos });
		return list.get(0);
	}

	public CorpVO queryCorpVOByid(String pk_id) throws DZFWarpException {
		CorpVO corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_id);
		return corpVo;
	}

	@Override
	public String getMjkmbm(String str, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		YntCpaccountVO[] vos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" accountcode like '" + str + "%' and pk_corp = ? and nvl(dr,0) = 0 ", sp);
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
		if (vos != null && vos.length > 0) {
			for (YntCpaccountVO vo : vos) {
				if (vo.getIsleaf().booleanValue()) {
					list.add(vo);
				}
			}
		}
		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<YntCpaccountVO>() {
				public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
					return arg0.getAccountcode().compareTo(arg1.getAccountcode());
				}
			});
			return list.get(0).getAccountcode();
		}

		return "";
	}

	@Override
	public List<String> getMjkmbms(String str, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		YntCpaccountVO[] vos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" accountcode like '" + str + "%' and pk_corp = ? and nvl(dr,0) = 0 ", sp);
		List<String> list = new ArrayList<String>();
		if (vos != null && vos.length > 0) {
			for (YntCpaccountVO vo : vos) {
				if (vo.getIsleaf().booleanValue()) {
					list.add(vo.getAccountcode());
				}
			}
		}
		return list;
	}

	private void checkLirunjz(String qj, String pk_corp) throws DZFWarpException {
		qj = qj.substring(0, 4) + "-12";
		SQLParameter sp = new SQLParameter();
		sp.addParam(qj);
		sp.addParam(pk_corp);
		String sql = "select * from ynt_qmjz a where a.period = ? and a.pk_corp = ?";
		List<QmJzVO> qmjzlist = (List<QmJzVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(QmJzVO.class));
		if (qmjzlist != null && qmjzlist.size() > 0) {
			for (QmJzVO vo : qmjzlist) {
				if (vo.getVdef10() != null && vo.getVdef10().equals("1")) {
					throw new BusinessException(qj + "已利润结转，不能反损益结转！");
				}
			}
		}
	}

	@Override
	public QmclVO updateJiTiShuiJin(QmclVO qmvo, String kmmethod, String pk_corp,String userid) throws DZFWarpException {
		// 如果关账，抛出异常
		checkisGz(qmvo, "不能计提附加税！");
		// zpm修改
		QmclVO voa = queryQmclVO(qmvo.getPk_corp(), qmvo.getPeriod());
		DZFBoolean isjtsj = voa == null ? qmvo.getIsjtsj() : voa.getIsjtsj();
		if (isjtsj != null && isjtsj.booleanValue()) {
			throw new BusinessException("已经计提附加税，不能重复计提！");
		}
		// 禁止多期间同时操作
		// Vector<String> vec_period = new Vector<String>();
		// for (QmclVO vo : vos) {
		// if (vec_period.isEmpty()) {
		// vec_period.add(vo.getPeriod());
		// } else {
		// if (!vec_period.contains(vo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行计提税金");
		// }
		// }
		// }
		// //查询公司的资产建账日期
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, qmvo.getPk_corp());
		DZFDate busibegin = corpvo.getBegindate();

		CorpVO corpVO = corpService.queryByPk(pk_corp);
		// 是否季度结转
		boolean isQuarter = false;
		if (StringUtil.isEmpty(corpVO.getChargedeptname()) || "小规模纳税人".equals(corpVO.getChargedeptname())) {
			YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpvo.getPk_corp(), "dzf007");
			if (paramSet == null || paramSet.getPardetailvalue() == 0) {
				isQuarter = true;
			}
			// 不是季末直接结转
			if (isQuarter && Integer.valueOf(qmvo.getPeriod().substring(5, 7)) % 3 != 0) {
				qmvo.setIsjtsj(DZFBoolean.TRUE);
				singleObjectBO.update(qmvo, new String[] { "isjtsj" });
				return qmvo;
			}
		}

//		if (!isQuarter) {
//			// 校验上期状态
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(qmvo.getPeriod());
//			sp.addParam(DateUtils.getPeriod(corpvo.getBegindate()));
//			sp.addParam(qmvo.getPk_corp());
//			String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period< ? and period >= ?  and pk_corp= ?  order by period desc";
//			List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
//					new BeanListProcessor(QmclVO.class));
//			if (value != null && value.size() > 0) {
//				String periodtemp = value.get(0).getPeriod() + "-01";
//				DZFDate perioddatetemp = new DZFDate(periodtemp);
//				DZFDate perioddate = new DZFDate(qmvo.getPeriod() + "-01").getDateBefore(1);
//				if (value.get(0).getIsjtsj() == null || !value.get(0).getIsjtsj().booleanValue()) {
//					throw new BusinessException("公司:" + deCodename(corpvo.getUnitname()) + "，存在先前期间的没计提数据，不能跨月计提附加税!");
//				}
//				if (perioddatetemp.getYear() != perioddate.getYear()
//						|| perioddatetemp.getMonth() != perioddate.getMonth()) {
//					throw new BusinessException("公司:" + deCodename(corpvo.getUnitname()) + "，存在先前期间的没计提数据，不能跨月计提附加税!");
//				}
//			}
//		}

		List<SurtaxTemplateVO> tempLsit = gl_surtaxtempserv.query(corpVO);
		for (SurtaxTemplateVO temp: tempLsit) {
			if (temp.getJfkmmc() == null || temp.getDfkmmc() == null) {
				throw new BusinessException("模板科目不存在，请检查");
			}
		}
		if (tempLsit.size() == 0) {
			throw new BusinessException("没有已启用的模板");
		}
		doJtsj(qmvo, tempLsit, corpVO, qmvo.getPeriod(), isQuarter,userid);
		return qmvo;
	}

	/**
	 * 计提附加税模板
	 * @param kmmethod
	 * @param pk_corp
	 * @return
	 */
	private List<JtsjVO> getFjsTemp(String kmmethod, String pk_corp) {
		List<JtsjVO> tempLsit = new ArrayList<JtsjVO>();
		// 停用的集团模板
		Set<String> sysDisSet = new HashSet<String>();
		List<JtsjVO> listVO = gl_jtsjtemserv.queryByKmmethod(kmmethod, pk_corp, false);// 查找公司模板
		if (listVO != null && listVO.size() > 0) {
			Iterator<JtsjVO> it = listVO.iterator();
			while (it.hasNext()) {
				JtsjVO jtsjVO = it.next();
				if (!StringUtil.isEmpty(jtsjVO.getPk_group())) {
					if ("N".equals(jtsjVO.getVdef1())) {
						sysDisSet.add(jtsjVO.getPk_group());
					}
					it.remove();
				} else if ("N".equals(jtsjVO.getVdef1())) {
					it.remove();
				}
			}
		}
		List<JtsjVO> listVOForJiTuan = gl_jtsjtemserv.queryByKmmethod(kmmethod, IDefaultValue.DefaultGroup, false);// 公司没有，取集团预设模板
		if(listVOForJiTuan != null) {
			Iterator<JtsjVO> it = listVOForJiTuan.iterator();
			while (it.hasNext()) {
				JtsjVO jtsjVO = it.next();
				if (sysDisSet.contains(jtsjVO.getPk_jtsjtemplate())) {
					it.remove();
				}
			}
			listVOForJiTuan = getCorpKmid(listVOForJiTuan, pk_corp);// 将集团科目id转换为该公司的科目id
			tempLsit.addAll(listVOForJiTuan);
		}
		if (listVO != null) {
			tempLsit.addAll(listVO);
		}
		return tempLsit;
	}
	private List<JtsjVO> getCorpKmid(List<JtsjVO> listVOForJiTuan, String pk_corp) {
		String jfkmid = null;
		String dfkmid = null;
		for (JtsjVO vo : listVOForJiTuan) {
			jfkmid = vo.getJfkm_id();
			dfkmid = vo.getDfkm_id();
			vo.setJfkm_id(yntBoPubUtil.getCorpAccountPkByTradeAccountPk(jfkmid, pk_corp));
			vo.setDfkm_id(yntBoPubUtil.getCorpAccountPkByTradeAccountPk(dfkmid, pk_corp));
		}
		return listVOForJiTuan;
	}

	private void doJtsj(QmclVO qmvo, List<SurtaxTemplateVO> list, CorpVO corpVO, String period, boolean isQuarter,String userid) {
		boolean isSmall = !"一般纳税人".equals(corpVO.getChargedeptname());
		String pk_corp = corpVO.getPk_corp();

		DZFDouble calData = null;
		DZFDate endDate = new DZFDate(period + "-01");
		DZFDate beginDate = endDate;
		DZFDouble incomeMny = DZFDouble.ZERO_DBL;
		String incomeKm = null;
		String incomeKm2 = null;
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
			incomeKm = "5001";
			incomeKm2 = "5051";
		} else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
			incomeKm = "6001";
			incomeKm2 = "6051";
		} else if("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())){
			incomeKm = "5101";
			incomeKm2 = "5102";
		} else {
			throw new BusinessException("不支持当前科目方案结转");
		}
		if (isSmall) {
			if (isQuarter) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate.toDate());
				calendar.add(Calendar.MONTH, -2);
				beginDate = new DZFDate(calendar.getTime());
			}
			calData = getVatMnyByInvoiceType(corpVO, DateUtils.getPeriod(beginDate), period)[1];

			String[] kms = new String[] { incomeKm, incomeKm2 };
			QueryParamVO pramavo = getFseQueryParamVO(pk_corp, beginDate, endDate, kms);

			FseJyeVO[] vos = (FseJyeVO[]) zxkjReportService.getFsJyeVOs1(pramavo)[0];
			if (vos != null && vos.length > 0) {
				for (FseJyeVO vo : vos) {
					if (incomeKm.equals(vo.getKmbm()) || incomeKm2.equals(vo.getKmbm())) {
						incomeMny = SafeCompute.add(incomeMny, vo.getFsdf());
					}
				}
			}
		} else {
			// 进项
			String inTax = getUpdateKmcode(corpVO, getJxkmcode(corpVO.getCorptype()));
			// 销项
			String outTax = getUpdateKmcode(corpVO, getXxkmCode(corpVO.getCorptype()));
			// 留底
			String stayTax = getUpdateKmcode(corpVO, getLdkmCode(corpVO.getCorptype()));
			String[] kms = new String[] { inTax, outTax, stayTax, incomeKm, incomeKm2};
			QueryParamVO pramavo = getFseQueryParamVO(pk_corp, beginDate, endDate, kms);

			FseJyeVO[] vos = (FseJyeVO[]) zxkjReportService.getFsJyeVOs1(pramavo)[0];
			calData = DZFDouble.ZERO_DBL;
			if (vos != null && vos.length > 0) {
				for (FseJyeVO vo : vos) {
					/*if (inTax.equals(vo.getKmbm())) {
						calData = SafeCompute.sub(calData, vo.getFsjf());
					} else if (outTax.equals(vo.getKmbm())) {
						calData = SafeCompute.add(calData, vo.getFsdf());
					} else if (stayTax.equals(vo.getKmbm())
							&& vo.getQcdf() != null
							&& vo.getQcdf().doubleValue() < 0) {
						calData = SafeCompute.add(calData, vo.getQcdf());
					} */
					if (stayTax.equals(vo.getKmbm())) {
						calData = SafeCompute.add(calData, vo.getFsdf());;
						if (calData.doubleValue() > 0
								&& vo.getQcdf().doubleValue() < 0) {
							calData = SafeCompute.add(calData, vo.getQcdf());
						}
					} else if (incomeKm.equals(vo.getKmbm()) || incomeKm2.equals(vo.getKmbm())) {
						incomeMny = SafeCompute.add(incomeMny, vo.getFsdf());
					}
				}
			}
		}
		// 删除之前生成的凭证
		deleteVoucherForSurtax(qmvo.getPk_qmcl());

		if (calData == null || calData.doubleValue() <= 0) {
			qmvo.setIsjtsj(DZFBoolean.TRUE);
			singleObjectBO.update(qmvo, new String[] { "isjtsj" });
			return;
		}

		DZFDouble data = null;

		List<TzpzBVO> bvoList = new ArrayList<TzpzBVO>();
		String rel_jfkmid = null;
		String curPk = yntBoPubUtil.getCNYPk();
		for (SurtaxTemplateVO vo : list) {
			DZFDouble rate = vo.getTax().div(100);
			data = (rate.multiply(calData)).setScale(2, DZFDouble.ROUND_HALF_UP);
			if (data.doubleValue() == 0) {
				continue;
			}
			TzpzBVO bvo1 = new TzpzBVO();
			rel_jfkmid = gl_jtsjtemserv.getCpidFromTd(vo.getJfkm_id(), pk_corp, null);
			bvo1.setPk_accsubj(rel_jfkmid);// vo.getJfkm_id()
			bvo1.setJfmny(data);
			bvo1.setPk_currency(curPk);
			bvo1.setZy(vo.getMemo());
			bvoList.add(bvo1);

		}
		String rel_dfkmid = null;
		List<TzpzBVO> taxBvoList = new ArrayList<TzpzBVO>();
		// 月销售收入≤10 万(季30万)，免征两个教育附加
		boolean qualifyForFreeEdu = isQuarter && incomeMny.doubleValue() <= 300000
				|| !isQuarter && incomeMny.doubleValue() <= 100000;
		// 2019后附加税减半征收
		boolean qualifyForReduction = isSmall && period.compareTo("2019-01") >= 0;
		for (SurtaxTemplateVO vo : list) {
			DZFDouble rate = vo.getTax().div(100);

			data = (rate.multiply(calData)).setScale(2, DZFDouble.ROUND_HALF_UP);
			if (data.doubleValue() == 0) {
				continue;
			}
			TzpzBVO bvo2 = new TzpzBVO();
			rel_dfkmid = gl_jtsjtemserv.getCpidFromTd(vo.getDfkm_id(), pk_corp, null);
			bvo2.setPk_accsubj(rel_dfkmid);// vo.getDfkm_id()
			bvo2.setDfmny(data);
			bvo2.setPk_currency(curPk);
			bvo2.setZy(vo.getMemo());
			bvoList.add(bvo2);

			if ((qualifyForReduction || qualifyForFreeEdu) && vo.getDfkmmc() != null) {
				TzpzBVO taxBvo = new TzpzBVO();
				if (SurTaxEnum.EDUCATION_SURTAX.getName().equals(vo.getTax_name())
						|| SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(vo.getTax_name())) {
					if (qualifyForFreeEdu) {
						taxBvo.setJfmny(data);
					} else {
						taxBvo.setJfmny(data.multiply(0.5).setScale(2, DZFDouble.ROUND_HALF_UP));
					}
				} else if (qualifyForReduction
						&& SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(vo.getTax_name())) {
					taxBvo.setJfmny(data.multiply(0.5).setScale(2, DZFDouble.ROUND_HALF_UP));
				} else if (qualifyForFreeEdu
						&& SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getName().equals(vo.getTax_name())) {
					taxBvo.setJfmny(data);
				} else {
					continue;
				}
				taxBvo.setPk_accsubj(rel_dfkmid);
				taxBvo.setPk_currency(curPk);
				taxBvo.setZy(vo.getMemo());
				taxBvoList.add(taxBvo);
			}
		}
		if (bvoList.size() > 1) {
			createVoucherForJtsj(qmvo, bvoList,userid);
		}
		if (taxBvoList.size() > 0) {
			DZFDouble mny = DZFDouble.ZERO_DBL;
			for (TzpzBVO tzpzBVO : taxBvoList) {
				mny = mny.add(tzpzBVO.getJfmny());
			}
			TzpzBVO subsidyVo = new TzpzBVO();
			String subsidyKm = null;
			if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
				// 政府补助
				subsidyKm = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpVO, "530104"));
			} else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
				subsidyKm = getAccsubjfromcode(pk_corp, "6117");
				if (subsidyKm == null) {
					subsidyKm = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpVO, "630104"));
				}
			}
			if (subsidyKm != null) {
				subsidyVo.setPk_accsubj(subsidyKm);
				subsidyVo.setDfmny(mny);
				subsidyVo.setPk_currency(curPk);
				subsidyVo.setZy("政府补助");
				taxBvoList.add(subsidyVo);
				createVoucherForJtsj(qmvo, taxBvoList, userid);
			}
		}
		qmvo.setIsjtsj(DZFBoolean.TRUE);
		singleObjectBO.update(qmvo, new String[] { "isjtsj" });
	}

	private String getLdkmCode(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "217102";
		}
		return "222109";
	}

	/**
	 * 销项科目
	 * @param corptype
	 * @return
	 */
	private String getXxkmCode(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "21710105";
		}
		return "22210102";
	}

	/**
	 * 进项科目
	 * @param corptype
	 * @return
	 */
	private String getJxkmcode(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "21710101";
		}
		return "22210101";
	}

	/**
	 * 减免税款科目
	 * @param corptype
	 * @return
	 */
	private String getDeductCode(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "21710104";
		}
		return "22210106";
	}

	@Override
	public DZFDouble[] getVatMnyByInvoiceType(CorpVO corpVO,
											  String beginPeriod, String endPeriod) {
		String codeRule = corpVO.getAccountcoderule();
		String incomeParentCode1 = null;
		String incomeParentCode2 = null;
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
			incomeParentCode1 = "5001";
			incomeParentCode2 = "5051";
		} else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
			incomeParentCode1 = "6001";
			incomeParentCode2 = "6051";
		} else if("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())){
			incomeParentCode1 = "5101";
			incomeParentCode2 = "5102";
		}
		String twoLevelCode = (codeRule != null && codeRule.startsWith("4/3")) ? "001" : "01";
		String cargoPattern = new StringBuilder("^(").append(incomeParentCode1).append(twoLevelCode)
				.append("|").append(incomeParentCode2).append(twoLevelCode).append(")").toString();
		String zzsKm = getUpdateKmcode(corpVO, getXxkmCode(corpVO.getCorptype()));

		StringBuilder sb = new StringBuilder();
		sb.append(
				"select b.vcode, b.pk_tzpz_h, h.fp_style, sum(b.dfmny) as dfmny")
				.append("  from ynt_tzpz_h h")
				.append("  left join ynt_tzpz_b b on h.pk_tzpz_h = b.pk_tzpz_h")
				.append(" where h.pk_corp = ?")
				.append("   and nvl(h.dr, 0) = 0 and nvl(b.dr, 0) = 0")
				.append("   and (b.vcode like ? or b.vcode like ? or b.vcode like ? )")
				.append("   and h.period between ? and ? ")
				.append(" group by b.vcode,b.pk_tzpz_h,h.fp_style ")
				.append(" order by pk_tzpz_h, vcode");

		SQLParameter sp = new SQLParameter();
		sp.addParam(corpVO.getPk_corp());
		sp.addParam(zzsKm + "%");
		sp.addParam(incomeParentCode1 + "%");
		sp.addParam(incomeParentCode2 + "%");

		sp.addParam(beginPeriod);
		sp.addParam(endPeriod);

		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(
				sb.toString(), sp, new BeanListProcessor(TzpzBVO.class));
		Map<String, List<TzpzBVO>> hvoMap = DZfcommonTools.hashlizeObject(bvos,
				new String[] { "pk_tzpz_h" });

		// 收入map
		DZFDouble cargoIncomeMny = DZFDouble.ZERO_DBL;
		DZFDouble serviceIncomeMny = DZFDouble.ZERO_DBL;
//		Map<String, DZFDouble> incomeMnyMap = new HashMap<String, DZFDouble>();
		// 普票销项税额map
		Map<String, DZFDouble> genTaxMap = new HashMap<String, DZFDouble>();
		// 计税基数-增值税
		DZFDouble baseTax = DZFDouble.ZERO_DBL;
		//  增值税合计
		DZFDouble spcTax = DZFDouble.ZERO_DBL;
		for (Map.Entry<String, List<TzpzBVO>> entry : hvoMap.entrySet()) {
			// 收入科目编码
			String incomeCode = null;
			List<TzpzBVO> vos = entry.getValue();
			for (TzpzBVO bvo : vos) {
				DZFDouble dfmny = bvo.getDfmny();
				if (dfmny == null || dfmny.doubleValue() == 0) {
					continue;
				}
				if (bvo.getVcode().startsWith(incomeParentCode1)
						|| bvo.getVcode().startsWith(incomeParentCode2)) {
					// 取第一个科目
					incomeCode = bvo.getVcode();
					break;
				}
			}
			if (incomeCode == null) {
				continue;
			}
			for (TzpzBVO bvo : vos) {
				DZFDouble dfmny = bvo.getDfmny();
				if (dfmny == null || dfmny.doubleValue() == 0) {
					continue;
				}
				if (bvo.getVcode().startsWith(incomeParentCode1)
						|| bvo.getVcode().startsWith(incomeParentCode2)) {
					if (bvo.getVcode().matches(cargoPattern)) {
						cargoIncomeMny = cargoIncomeMny.add(dfmny);
					} else {
						serviceIncomeMny = serviceIncomeMny.add(dfmny);
					}
//					if (incomeMnyMap.containsKey(bvo.getVcode())) {
//						dfmny = incomeMnyMap.get(bvo.getVcode()).add(dfmny);
//					}
//					incomeMnyMap.put(bvo.getVcode(), dfmny);
				} else if (bvo.getVcode().startsWith(zzsKm)) {
					spcTax =SafeCompute.add(spcTax,dfmny);
					if (bvo.getFp_style() != null
							&& bvo.getFp_style() == IFpStyleEnum.SPECINVOICE.getValue()) {
						// 专票销项税额直接相加
						baseTax = baseTax.add(dfmny);
					} else {
						// 普票销项税额按科目分组
						if (genTaxMap.containsKey(incomeCode)) {
							dfmny = genTaxMap.get(incomeCode).add(dfmny);
						}
						genTaxMap.put(incomeCode, dfmny);
					}
				}
			}
		}

		int year = Integer.valueOf(endPeriod.substring(0, 4));
		// 税收优惠
		DZFDouble reduce = DZFDouble.ZERO_DBL;
        YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpVO.getPk_corp(), "dzf024");
        boolean isAllFree = false;
        if (paramSet != null && paramSet.getPardetailvalue() == 0) {
            isAllFree = true;
        }
		if(year >= 2020 && isAllFree){
			reduce = spcTax;
			baseTax = DZFDouble.ZERO_DBL;
		}else{
			double totalIncome = SafeCompute.add(cargoIncomeMny, serviceIncomeMny).doubleValue();
			int limit = beginPeriod.equals(endPeriod) ? (year >= 2019 ? 100000 : 30000) : (year >= 2019 ? 300000 : 90000);
			for (Map.Entry<String, DZFDouble> entry : genTaxMap.entrySet()) {
				String code = entry.getKey();
				DZFDouble genTax = entry.getValue();
				if (genTax != null) {
					if(year < 2019){
						// 收入小于limit时，免普票中的销项税额  2019前政策
						if ((code.matches(cargoPattern) ? cargoIncomeMny : serviceIncomeMny).doubleValue() <= limit) {
							// 计入税收优惠
							reduce = reduce.add(genTax);
						} else {
							// 计入增值税
							baseTax = baseTax.add(genTax);
						}
					}else{
						// 收入小于limit时，免普票中的销项税额 2019开始执行
						if (totalIncome <= limit) {
							// 计入税收优惠
							reduce = reduce.add(genTax);
						} else {
							// 计入增值税
							baseTax = baseTax.add(genTax);
						}
					}
				}
			}
			// 减免税款
			DZFDouble deductTax = null;
			String deductCode = getUpdateKmcode(corpVO, getDeductCode(corpVO.getCorptype()));
			QueryParamVO param = ReportUtil.getFseQueryParamVO(corpVO,
					new DZFDate(beginPeriod + "-01"), new DZFDate(endPeriod + "-01"),
					new String[]{deductCode}, true);
			FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(param, 1);
			if (fsejyevos != null && fsejyevos.length > 0) {
				for (FseJyeVO fseVO : fsejyevos) {
					if (deductCode.equals(fseVO.getKmbm())) {
						deductTax = fseVO.getFsjf();
						break;
					}
				}
			}
			if (deductTax != null) {
				baseTax = baseTax.sub(deductTax);
			}
		}
		return new DZFDouble[] { reduce, baseTax };
	}

	private void createVoucherForJtsj(QmclVO vo, List<TzpzBVO> bvoList,String userid) {

		// 凭证表头VO
		TzpzHVO pzHeadVO = getTzpzHvoForJtsj(vo, bvoList,userid);

		// for(TzpzBVO bvo:pzBodyVOs){
		// bvo.setVcode(mapCorpAcount.get(bvo.getPk_accsubj()).getAccountcode());
		// bvo.setVname(mapCorpAcount.get(bvo.getPk_accsubj()).getAccountname());
		// bvo.setPk_corp(pzHeadVO.getPk_corp());
		// }
		int size = bvoList.size();
		pzHeadVO.setChildren(bvoList.toArray(new TzpzBVO[size]));

		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pzHeadVO.getPk_corp());
		gl_tzpzserv.saveVoucher(corpvo, pzHeadVO);
	}

	private TzpzHVO getTzpzHvoForJtsj(QmclVO vo, List<TzpzBVO> bvoList,String userid) throws DZFWarpException {
		DZFDouble headJfmny = DZFDouble.ZERO_DBL;
		DZFDouble headDfmny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bodyVO : bvoList) {
			headJfmny = headJfmny.add(bodyVO.getJfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getJfmny());
			headDfmny = headDfmny.add(bodyVO.getDfmny() == null ? DZFDouble.ZERO_DBL : bodyVO.getDfmny());
		}
		// 凭证表头VO
		TzpzHVO pzHeadVO = new TzpzHVO();
		pzHeadVO.setPk_corp(vo.getPk_corp());
		pzHeadVO.setVyear(Integer.parseInt(vo.getPeriod().substring(0, 4)));
		pzHeadVO.setPzlb(0);// 凭证类别：记账
		pzHeadVO.setPeriod(vo.getPeriod());
		pzHeadVO.setJfmny(headJfmny);
		pzHeadVO.setDfmny(headDfmny);
		pzHeadVO.setPk_currency(yntBoPubUtil.getCNYPk());
		pzHeadVO.setCoperatorid(userid);
		pzHeadVO.setIshasjz(DZFBoolean.FALSE);

		DZFDate doperatedate = getPeroidDZFDate(vo);
		pzHeadVO.setDoperatedate(doperatedate);
		pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), doperatedate));
		pzHeadVO.setVbillstatus(8);

		// 记录来源单据
		pzHeadVO.setSourcebilltype(IBillTypeCode.HP39);
		pzHeadVO.setSourcebillid(vo.getPk_qmcl());
		return pzHeadVO;
	}

	private String getNewCode(String[] newRule, String code, int level) {
		int beginIndex = 0;
		String newCode = "";
		for (int i = 0; i < level; i++) {
			int codelen = oldRule[i];
			String oldpartCode = code.substring(beginIndex, beginIndex + codelen);
			beginIndex += codelen;
			String newPartCode = getNewPartCode(newRule[i], oldpartCode);
			newCode += newPartCode;
		}
		return newCode;
	}

	private String getNewPartCode(String newcodeRulePart, String oldpartCode) {

		String newPartCode = oldpartCode;
		int newPartLen = Integer.parseInt(newcodeRulePart);
		int oldPartLen = oldpartCode.trim().length();
		if (oldPartLen == newPartLen) {
			return newPartCode;
		}

		for (int i = 0; i < (newPartLen - oldPartLen); i++) {
			newPartCode = "0" + newPartCode;
		}

		return newPartCode;
	}

	@Override
	public QmclVO updateFanJiTiShuiJin(QmclVO vos) throws DZFWarpException {
		DZFBoolean isqjsyjz = vos.getIsqjsyjz();

		// 如果已经年结账，则不能反计提税金
		updateCheckNj(vos);
		// 如果已经结转不能反计提税金
		vos = checkisGz(vos, "不能反计提附加税！");
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+vos.getPeriod()+"，已经损益结转，不能反计提附加税！");
		}

		SQLParameter sp = new SQLParameter();
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period>?  and pk_corp= ? order by period  ";
		sp.addParam(vos.getPeriod());
		sp.addParam(vos.getPk_corp());
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		if (value != null && value.size() > 0) {
			if (value.get(0).getIsjtsj() != null && value.get(0).getIsjtsj().booleanValue()) {
				throw new BusinessException("存在后续期间的计提数据，不能跨月反计提附加税!");
			}
		}

		if (vos.getIsjtsj() == null || !vos.getIsjtsj().booleanValue()) {
			throw new BusinessException("没有进行计提附加税，不能反计提！");
		}

		// 禁止多期间同时操作
		// Vector<String> vec_period = new Vector<String>();
		// for (QmclVO vo : vos) {
		// if (vec_period.isEmpty()) {
		// vec_period.add(vo.getPeriod());
		// } else {
		// if (!vec_period.contains(vo.getPeriod())) {
		// throw new BusinessException("不能同时对多个期间进行反计提税金");
		// }
		// }
		//
		// }
		// for (QmclVO vo : vos) {
		deleteVoucherForSurtax(vos.getPk_qmcl());
		vos.setIsjtsj(DZFBoolean.FALSE);
		// }
		singleObjectBO.update(vos, new String[]{ "isjtsj" });
		gl_taxarchive.updateSurtaxUnCarryover(vos.getPk_corp(), vos.getPeriod());

		return vos;
	}

	/**
	 * 删除计提附加税凭证
	 * @param pk_qmcl
	 */
	public void deleteVoucherForSurtax(String pk_qmcl) {
		String wppzh = " select * from ynt_tzpz_h where  sourcebillid= ? and sourcebilltype= ? and nvl(dr,0)=0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_qmcl);
		sp.addParam(IBillTypeCode.HP39);
		List<TzpzHVO> tzlist = (List<TzpzHVO>) singleObjectBO.executeQuery(wppzh, sp,
				new BeanListProcessor(TzpzHVO.class));
		if (tzlist != null && tzlist.size() > 0) {
			TzpzHVO[] pzHeadVOs = tzlist.toArray(new TzpzHVO[0]);
			for (TzpzHVO headVO : pzHeadVOs) {
				if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
					// 已有凭证记账
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能操作！");
				}
				if (headVO.getVbillstatus() == 1) {
					// 已有凭证审核通过
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能操作！");
				}
				// 先删除表体
				sp.clearParams();
				String updatebsql = "update ynt_tzpz_b  set dr =1   where  pk_tzpz_h= ?   ";
				sp.addParam(headVO.getPrimaryKey());
				singleObjectBO.executeUpdate(updatebsql, sp);
				// 再删除表头
				String updatehsql = "update ynt_tzpz_h  set dr = 1 where  pk_tzpz_h= ? ";
				sp.clearParams();
				sp.addParam(headVO.getPrimaryKey());
				singleObjectBO.executeUpdate(updatehsql, sp);
				// 删除现金流量表的数据
				String updatexjll = " update ynt_xjll set dr = 1 where pk_tzpz_h = ?  and pk_corp  = ?";
				sp.clearParams();
				sp.addParam(headVO.getPrimaryKey());
				sp.addParam(headVO.getPk_corp());
				singleObjectBO.executeUpdate(updatexjll, sp);
			}
		}
	}

	private QmclVO checkisGz(QmclVO votemp, String str) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(votemp.getPk_corp());
		boolean isgz = qmgzService.isGz(votemp.getPk_corp(), votemp.getPeriod().toString());
		if (isgz) {// 是否关账
			throw new BusinessException("公司" + corpvo.getUnitname() + votemp.getPeriod().toString() + "月份已关账，" + str);
		}
		votemp.setIsgz(new DZFBoolean(isgz));
		return votemp;
	}

	@Override
	public QmclVO queryQmclVO(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		String where = " pk_corp = ? and period = ? and nvl(dr,0) = 0  ";
		QmclVO[] vos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, where, sp);
		QmclVO qmvo = null;
		if (vos != null && vos.length > 0)
			qmvo = vos[0];
		return qmvo;
	}
	/**
	 * 增值税结转
	 */
	@Override
	public QmclVO onzzsjz(String userid, QmclVO qmvo) throws DZFWarpException {
		// 判断小规模不增值税结转
		CorpVO corpvo = corpService.queryByPk(qmvo.getPk_corp());
		// 校验
		checkisGz(qmvo, "不能增值税结转！");
		//
		QmclVO voa = queryQmclVO(qmvo.getPk_corp(), qmvo.getPeriod());
		DZFBoolean iszzs = voa == null ? qmvo.getZzsjz() : voa.getZzsjz();
		if (iszzs != null && iszzs.booleanValue()) {
			throw new BusinessException("增值税已经结转，不能重复结转！");
		}
		// 判断上期是否已经结转增值税
		// DZFDate begindate = corpvo.getBegindate();
		// String bperiod = DateUtils.getPeriod(begindate);
		// if(!bperiod.equals(qmvo.getPeriod())){
		// QmclVO vv = queryLastQmcl(qmvo,bperiod);
		// if(vv==null){
		// throw new BusinessException("期末处理数据不存在，请重新查询后操作！");
		// }
		// if(vv.getZzsjz() == null || !vv.getZzsjz().booleanValue()){
		// throw new BusinessException("增值税上期没有结转！");
		// }
		// }

		String pk_corp = qmvo.getPk_corp();
		String period = qmvo.getPeriod();
		// 取截止上个月末的 应交税费 ---未交增值税 余额，//////////////这个是查找上期留抵的，生成次月的交税凭证
		// String lastperiod = DateUtils.getPreviousPeriod(period);
		// QueryParamVO paramvo =
		// getZZsjzQueryParamVO(pk_corp,lastperiod,"222109");
		// DZFDouble liudishui = DZFDouble.ZERO_DBL;
		// FseJyeVO[] fsejyevos = gl_rep_fsyebserv.getFsJyeVOs(paramvo,1);
		// if(fsejyevos != null && fsejyevos.length > 0){
		// for(FseJyeVO yevo : fsejyevos){
		// if("222109".equals(yevo.getKmbm())){
		// liudishui = yevo.getQmjf();
		// if(liudishui!=null && liudishui.doubleValue() > 0)
		// break;
		// liudishui = yevo.getQmdf();
		// if(liudishui!=null && liudishui.doubleValue() < 0){
		// liudishui = liudishui.multiply(new DZFDouble(-1));
		// break;
		// }
		// }
		// }
		// }
		if (!"一般纳税人".equals(corpvo.getChargedeptname())) {
			zzsjzSmall(corpvo, qmvo,userid);
		} else {
			//应交增值税为借方余额时结转到未交增值税
			boolean isForwardNegative = true;
			YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpvo.getPk_corp(), "dzf022");
			if (paramSet != null) {
				CorpVO fathercorpcorpVo =corpService.queryByPk(corpvo.getFathercorp());
				if (fathercorpcorpVo.getIschannel() != null
						&& fathercorpcorpVo.getIschannel().booleanValue()) {
					String checkSql = "select 1 from ynt_parameter where pk_corp in (?, ?) and nvl(dr,0)=0 and parameterbm = ? ";
					SQLParameter sp = new SQLParameter();
					sp.addParam(pk_corp);
					sp.addParam(fathercorpcorpVo.getPk_corp());
					sp.addParam("dzf022");
					if (!singleObjectBO.isExists(pk_corp, checkSql, sp)) {
						// 加盟商默认否
						isForwardNegative = false;
					} else {
						isForwardNegative = paramSet.getPardetailvalue() == 0;
					}
				} else {
					isForwardNegative = paramSet.getPardetailvalue() == 0;
				}
			}
			// 取本月末的 应交增值税---二级科目余额.
			String zzscode = getUpdateKmcode(corpvo,getZzsCode(corpvo.getCorptype()) );
			DZFDate queryDate = new DZFDate(period + "-01");
			QueryParamVO paramvo = getFseQueryParamVO(pk_corp, queryDate, queryDate, new String[] { zzscode });
			DZFDouble yyzzs = DZFDouble.ZERO_DBL;
			FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(paramvo, 1);
			if (fsejyevos != null && fsejyevos.length > 0) {
				for (FseJyeVO yevo : fsejyevos) {
					if (zzscode.equals(yevo.getKmbm())) {
						yyzzs = yevo.getQmdf();
						break;
					}
				}
			}
			// 生成结转凭证//////////目的，将应交增值税科目 ，本期余额，结转为0////////////结转到未交增值税//////
			if (yyzzs != null && (isForwardNegative && yyzzs.doubleValue() != 0
					|| !isForwardNegative && yyzzs.doubleValue() > 0)) {
				TzpzHVO pzhvos = createpzinfo(qmvo, yyzzs, IBillTypeCode.HP120,userid);
				TzpzBVO[] children = createpzBVO(corpvo, qmvo.getPk_corp(), yyzzs);
				pzhvos.setChildren(children);
				gl_tzpzserv.saveVoucher(corpvo, pzhvos);
			}
		}
		// 更改qmvo处理状态
		qmvo.setZzsjz(new DZFBoolean(true));
		singleObjectBO.update(qmvo, new String[] { "zzsjz" });
		return qmvo;
	}

	private String getZzsCode(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "217101";
		}
		return "222101";
	}

	// 小规模增值税结转
	private void zzsjzSmall(CorpVO corpvo, QmclVO qmvo,String userid) {
		String pk_corp = qmvo.getPk_corp();
		String period = qmvo.getPeriod();
		YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpvo.getPk_corp(), "dzf007");
		boolean isQuarter = true;
		if (paramSet != null && paramSet.getPardetailvalue() == 1) {
			isQuarter = false;
		}
		// 不是季末直接结转
		if (isQuarter && Integer.valueOf(period.substring(5, 7)) % 3 != 0) {
			return;
		}

		if("00000100000000Ig4yfE0005".equals(corpvo.getCorptype())){
			throw new BusinessException("企业会计制度暂不支持增值税结转，请手工结转");
		}

		DZFDate endDate = new DZFDate(period + "-01");
		DZFDate beginDate = endDate;
		if (isQuarter) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate.toDate());
			calendar.add(Calendar.MONTH, -2);
			beginDate = new DZFDate(calendar.getTime());
		}
		DZFDouble taxMny = getVatMnyByInvoiceType(corpvo,DateUtils.getPeriod(beginDate), period)[0];
		if (taxMny != null && taxMny.doubleValue() > 0) {
			TzpzHVO pzhvos = createVoucherByZzsSmall(qmvo, corpvo, pk_corp, taxMny,userid);
			gl_tzpzserv.saveVoucher(corpvo, pzhvos);
		}
	}

	private String getAccsubjfromcode(String pk_corp, String kmcode) {
		String pkaccsubj = null;
		StringBuffer sf = new StringBuffer();
		sf.append(
				" select * from ynt_cpaccount o where o.pk_corp = ? and nvl(o.dr,0) = 0 and accountcode like ? order by accountcode  ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(kmcode + "%");
		List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(YntCpaccountVO.class));
		if (list == null || list.size() == 0)
			return pkaccsubj;
		for (YntCpaccountVO ycp : list) {
			if (ycp.getIsleaf() != null && ycp.getIsleaf().booleanValue()) {
				pkaccsubj = ycp.getPrimaryKey();
				break;
			}
		}
		return pkaccsubj;
	}

	/**
	 * 生成增值税凭证子表
	 *
	 * @param yyzzs
	 */
	private TzpzBVO[] createpzBVO(CorpVO rpvo, String pk_corp, DZFDouble yyzzs) {
		TzpzBVO[] pzbvs = new TzpzBVO[2];
		TzpzBVO pzbvo_jf = new TzpzBVO();
		TzpzBVO pzbvo_df = new TzpzBVO();
		String cnypk = yntBoPubUtil.getCNYPk();
		String zcwjzzs = getAccsubjfromcode(pk_corp, getUpdateKmcode(rpvo, getZzsJfkm(rpvo.getCorptype())));// 转出未交增值税
		String wjzzs = getAccsubjfromcode(pk_corp, getUpdateKmcode(rpvo,  getZzsDfKm(rpvo.getCorptype())));// 未交增值税
		if (yyzzs.doubleValue() > 0) {// 大于0 .
			// 借--转出未交增值税
			// 贷--未交增值税
			pzbvo_jf.setPk_accsubj(zcwjzzs);
			pzbvo_jf.setJfmny(yyzzs);
			pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
			pzbvo_df.setPk_accsubj(wjzzs);
			pzbvo_df.setJfmny(DZFDouble.ZERO_DBL);
			pzbvo_df.setDfmny(yyzzs);
		} else {
			// 借--未交增值税
			// 贷--转出未交增值税
			pzbvo_jf.setPk_accsubj(wjzzs);
			pzbvo_jf.setJfmny(yyzzs.abs());
			pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
			pzbvo_df.setPk_accsubj(zcwjzzs);
			pzbvo_df.setJfmny(DZFDouble.ZERO_DBL);
			pzbvo_df.setDfmny(yyzzs.abs());
		}
		pzbvo_jf.setZy("结转增值税");// 摘要
		pzbvo_df.setZy("结转增值税");// 摘要
		// 币种，默认人民币
		pzbvo_jf.setPk_currency(cnypk);
		pzbvo_df.setPk_currency(cnypk);
		pzbvs[0] = pzbvo_jf;
		pzbvs[1] = pzbvo_df;
		return pzbvs;
	}

	private String getZzsDfKm(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "217102";
		}
		return "222109";
	}

	private String getZzsJfkm(String corptype) {
		if("00000100000000Ig4yfE0005".equals(corptype)){
			return "21710103";
		}
		return "22210103";
	}

	// 小规模增值税结转生成凭证
	private TzpzHVO createVoucherByZzsSmall(QmclVO qmvo, CorpVO corpvo, String pk_corp, DZFDouble taxMny,String userid) {
		TzpzHVO pzhvos = createpzinfo(qmvo, taxMny, IBillTypeCode.HP120,userid);
		TzpzBVO[] pzbvs = new TzpzBVO[2];
		TzpzBVO pzbvo_jf = new TzpzBVO();
		TzpzBVO pzbvo_df = new TzpzBVO();
		String cnypk = yntBoPubUtil.getCNYPk();
		// 销项税额
		String taxKm = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpvo, "22210102"));
		String subsidyKm = null;
		if ("00000100AA10000000000BMD".equals(corpvo.getCorptype())) {
			// 政府补助
			subsidyKm = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpvo, "530104"));
		} else if ("00000100AA10000000000BMF".equals(corpvo.getCorptype())) {
			subsidyKm = getAccsubjfromcode(pk_corp, "6117");
			if (subsidyKm == null) {
				subsidyKm = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpvo, "630104"));
			}
		}
		pzbvo_jf.setPk_accsubj(taxKm);
		pzbvo_jf.setJfmny(taxMny);
		pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
		pzbvo_df.setPk_accsubj(subsidyKm);
		pzbvo_df.setJfmny(DZFDouble.ZERO_DBL);
		pzbvo_df.setDfmny(taxMny);
		pzbvo_jf.setZy("结转增值税");
		pzbvo_df.setZy("结转增值税");
		// 币种，默认人民币
		pzbvo_jf.setPk_currency(cnypk);
		pzbvo_df.setPk_currency(cnypk);
		pzbvs[0] = pzbvo_jf;
		pzbvs[1] = pzbvo_df;
		pzhvos.setChildren(pzbvs);
		return pzhvos;
	}

	/**
	 * 生成增值税凭证主表
	 *
	 * @param qmvo
	 * @param zzsmny
	 */
	private TzpzHVO createpzinfo(QmclVO qmvo, DZFDouble zzsmny, String billtype,String userid) {
		TzpzHVO pzHeadVO = new TzpzHVO();
		pzHeadVO.setPk_corp(qmvo.getPk_corp());
		pzHeadVO.setVyear(Integer.parseInt(qmvo.getPeriod().substring(0, 4)));
		pzHeadVO.setPzlb(0);// 凭证类别：记账
		pzHeadVO.setPeriod(qmvo.getPeriod());
		pzHeadVO.setJfmny(zzsmny.abs());
		pzHeadVO.setDfmny(zzsmny.abs());
		pzHeadVO.setPk_currency(yntBoPubUtil.getCNYPk());
		pzHeadVO.setCoperatorid(userid);
		pzHeadVO.setIshasjz(DZFBoolean.FALSE);

		DZFDate doperatedate = getPeroidDZFDate(qmvo);
		pzHeadVO.setDoperatedate(doperatedate);
		pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(qmvo.getPk_corp(), doperatedate));
		pzHeadVO.setVbillstatus(8);

		// 记录来源单据
		pzHeadVO.setSourcebilltype(billtype);
		pzHeadVO.setSourcebillid(qmvo.getPk_qmcl());
		return pzHeadVO;
	}

	public QueryParamVO getZZsjzQueryParamVO(String pk_corp, String period, String kmcode) {
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setBegindate1(new DZFDate(period + "-01"));
		paramvo.setEnddate(new DZFDate(period + "-01"));
		paramvo.setBtotalyear(new DZFBoolean(true));// 是否按年累计
		paramvo.setCjq(1);// 科目级次开始
		paramvo.setCjz(2);// 科目级次结束
		paramvo.setIshasjz(new DZFBoolean(false));// 包含未记账凭证-------------反过来
		paramvo.setSfzxm(new DZFBoolean(false));// 显示辅助项目
		paramvo.setXswyewfs(new DZFBoolean(true));// 无余额无发生不显示
		paramvo.setIshowfs(new DZFBoolean(true));// 有余额无发生不显示-----------反过来
		paramvo.setIsnomonthfs(new DZFBoolean(true));// 本月合计无发生是否显示
		paramvo.setPk_corp(pk_corp);
		paramvo.setKms_first(kmcode);
		paramvo.setKms_last(kmcode);
		return paramvo;
	}

	// 查询发生额余额参数
	private QueryParamVO getFseQueryParamVO(String pk_corp, DZFDate beginDate, DZFDate endDate, String[] kmArray) {
		QueryParamVO paramvo = new QueryParamVO();
		String kms = StringUtil.getUnionStr(kmArray, ",", "");
		paramvo.setBegindate1(beginDate);
		paramvo.setEnddate(endDate);
		paramvo.setBtotalyear(new DZFBoolean(true));
		paramvo.setCjq(1);
		paramvo.setCjz(6);
		paramvo.setIshasjz(new DZFBoolean(false));
		paramvo.setSfzxm(new DZFBoolean(false));
		paramvo.setXswyewfs(new DZFBoolean(true));
		paramvo.setIshowfs(new DZFBoolean(true));
		paramvo.setIsnomonthfs(new DZFBoolean(true));
		paramvo.setPk_corp(pk_corp);
		paramvo.setKms_first(kmArray[0]);
		paramvo.setKms_last(kmArray[kmArray.length - 1]);
		paramvo.setKms(kms);
		paramvo.setKmcodelist(Arrays.asList(kmArray));
		return paramvo;
	}

	@Override
	public QmclVO onfzzsjz(QmclVO qmvo) throws DZFWarpException {
		if(qmvo!=null && (qmvo.getZzsjz()== null ||  !qmvo.getZzsjz().booleanValue())){
			throw new BusinessException("未增值税结转不能反结转！");
		}
		updateCheckNj(qmvo);
		checkisGz(qmvo, "不能反增值税结转！");
		DZFBoolean isqjsyjz = qmvo.getIsqjsyjz();
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+qmvo.getPeriod()+"，已经损益结转，不能反增值税结转！");
		}
		//
		SQLParameter sp = new SQLParameter();
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period>?  and pk_corp= ? order by period  ";
		sp.addParam(qmvo.getPeriod());
		sp.addParam(qmvo.getPk_corp());
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		for (QmclVO qmclVO : value) {
			if (qmclVO.getZzsjz() != null && qmclVO.getZzsjz().booleanValue()) {
				throw new BusinessException("存在后续期间的结转数据，不能跨月反增值税结转！");
			}
		}
		//
		// 校验
		String wp = "pk_corp=? and sourcebillid= ? and sourcebilltype=? and nvl(dr,0)=0 ";
		SQLParameter sp1 = new SQLParameter();
		sp1.addParam(qmvo.getPk_corp());
		sp1.addParam(qmvo.getPk_qmcl());
		sp1.addParam(IBillTypeCode.HP120);
		TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wp, sp1);
		if (pzHeadVOs != null && pzHeadVOs.length > 0) {
			for (TzpzHVO headVO : pzHeadVOs) {
				if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
					// 已有凭证记账
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能反操作！");
				}
				if (headVO.getVbillstatus() == 1) {
					// 已有凭证审核通过
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能反操作！");
				}
			}
		}
		// 删除本月生成的增值税凭证
		String delsql1 = " update ynt_tzpz_b set dr = 1 where pk_tzpz_h in (select pk_tzpz_h from ynt_tzpz_h where pk_corp = ? and period=?  and sourcebilltype = ? ) ";
		String delsql2 = " update ynt_tzpz_h set dr = 1 where pk_corp = ? and period=?  and sourcebilltype = ? ";
		sp.clearParams();
		sp.addParam(qmvo.getPk_corp());
		sp.addParam(qmvo.getPeriod());
		sp.addParam(IBillTypeCode.HP120);
		singleObjectBO.executeUpdate(delsql1, sp);
		singleObjectBO.executeUpdate(delsql2, sp);
		sp.clearParams();
		sp.addParam(qmvo.getPk_corp());
		sp.addParam(qmvo.getPeriod());
		String updsql3 = " update ynt_qmcl set zzsjz = 'N' where pk_corp = ? and period=? ";
		singleObjectBO.executeUpdate(updsql3, sp);
		qmvo.setZzsjz(new DZFBoolean(false));
		gl_taxarchive.updateAddTaxUnCarryover(qmvo.getPk_corp(), qmvo.getPeriod());
		return qmvo;
	}

	private QmclVO queryLastQmcl(QmclVO qmvo, String bperiod) {
		String period = qmvo.getPeriod();
		String pk_corp = qmvo.getPk_corp();
		String lastperiod = DateUtils.getPreviousPeriod(period);
		SQLParameter sp = new SQLParameter();
		sp.addParam(lastperiod);
		sp.addParam(pk_corp);
		sp.addParam(bperiod);
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period = ?  and pk_corp = ? and period >= ?  ";
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		QmclVO qmvo1 = null;
		if (value != null && value.size() > 0) {
			qmvo1 = value.get(0);
		}
		return qmvo1;
	}

	@Override
	public QmclVO onsdsjz(QmclVO qmvo, String userid) throws DZFWarpException {
		// 校验
		checkisGz(qmvo, "不能所得税计提！");
		//
		QmclVO voa = queryQmclVO(qmvo.getPk_corp(), qmvo.getPeriod());
		DZFBoolean sds = voa == null ? qmvo.getQysdsjz() : voa.getQysdsjz();
		if (sds != null && sds.booleanValue()) {
			throw new BusinessException("企业所得税已经计提，不能重复计提！");
		}
		// 判断上月是否已经结转?
		CorpVO corpvo = corpService.queryByPk(qmvo.getPk_corp());
		// DZFDate begindate = corpvo.getBegindate();
		// String bperiod = DateUtils.getPeriod(begindate);
		// if(!bperiod.equals(qmvo.getPeriod())){
		// QmclVO vv = queryLastQmcl(qmvo,bperiod);
		// if(vv==null){
		// throw new BusinessException("期末结转数据不存在，请重新查询后操作！");
		// }
		// if(vv.getQysdsjz() == null || !vv.getQysdsjz().booleanValue()){
		// throw new BusinessException("企业所得税上期没有计提！");
		// }
		// }
		//
		boolean isQuarter = true;
		YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpvo.getPk_corp(), "dzf013");
		if (paramSet != null && paramSet.getPardetailvalue() != 0) {
			isQuarter = false;
		}

		// 季度末计提、结转
		int month = new DZFDate(qmvo.getPeriod() + "-01").getMonth();
		if (!isQuarter || isQuarter && month % 3 == 0) {
			CorpTaxVo taxInfo = sys_corp_tax_serv.queryCorpTaxVO(qmvo.getPk_corp());
			if ("00000100AA10000000000BMD".equals(corpvo.getCorptype())) {
				// 小企业会计准则才按设置选择对应算法
				TaxEffeHistVO effvo = sys_corp_tax_serv.queryTaxEffHisVO(qmvo.getPk_corp(), qmvo.getPeriod());
				taxInfo.setTaxlevytype(effvo.getTaxlevytype());
				taxInfo.setIncomtaxtype(effvo.getIncomtaxtype());
				taxInfo.setIncometaxrate(effvo.getIncometaxrate());
				taxInfo.setSxbegperiod(effvo.getSxbegperiod());
				taxInfo.setSxendperiod(effvo.getSxendperiod());
			} else {
				taxInfo.setTaxlevytype(null);
				taxInfo.setIncomtaxtype(null);
				taxInfo.setIncometaxrate(null);
				taxInfo.setSxbegperiod(null);
				taxInfo.setSxendperiod(null);
			}
			boolean isIndividual = taxInfo.getIncomtaxtype() != null
					&& taxInfo.getIncomtaxtype() == 1;
			DZFDouble shuikuan = null;
			IncomeTaxCalculator calculator = new IncomeTaxCalculator(singleObjectBO,
					zxkjReportService);
			shuikuan = calculator.calculateIncomeTax(qmvo.getPeriod(), isQuarter, corpvo,
					taxInfo);
			if (shuikuan != null && shuikuan.doubleValue() > 0) {
				TzpzHVO hvo1 = createsdsjtpz(qmvo, shuikuan, corpvo,userid,
						isIndividual);
				gl_tzpzserv.saveVoucher(corpvo, hvo1);
			}
		}
		qmvo.setQysdsjz(new DZFBoolean(true));
		singleObjectBO.update(qmvo, new String[] { "qysdsjz" });
		return qmvo;
	}

	// 生成所得税计提凭证
	private TzpzHVO createsdsjtpz(QmclVO qmvo, DZFDouble sdsmny, CorpVO corpvo,String userid,
								  boolean isIndividual) {
		TzpzHVO pzvo = createpzinfo(qmvo, sdsmny, IBillTypeCode.HP125,userid);
		TzpzBVO[] bvos = createsdsjtBVO(qmvo.getPk_corp(), sdsmny, corpvo, isIndividual);
		pzvo.setChildren(bvos);
		return pzvo;
	}

	// 生成所得税计提子表
	private TzpzBVO[] createsdsjtBVO(String pk_corp, DZFDouble sdsmny, CorpVO corpvo,
									 boolean isIndividual) {
		TzpzBVO[] pzbvs = new TzpzBVO[2];
		TzpzBVO pzbvo_jf = new TzpzBVO();
		TzpzBVO pzbvo_df = new TzpzBVO();
		String cnypk = yntBoPubUtil.getCNYPk();
		String kmcode = null;
		String dfkmcode = isIndividual ? "222105" : "222106";
		if ("00000100AA10000000000BMF".equals(corpvo.getCorptype())) {
			kmcode = "6801";
		}else if("00000100000000Ig4yfE0005".equals(corpvo.getCorptype())){//企业会计制度
			kmcode = "5701";
			dfkmcode = isIndividual ? "217112" : "217106";
		} else {
			kmcode = "5801";
		}
		String jfpk = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpvo, kmcode));// 所得税费用
		// 所得税费用5801
		// 6801
		String dfpk = getAccsubjfromcode(pk_corp, getUpdateKmcode(corpvo,dfkmcode));// 应交企业所得税
		//
		pzbvo_jf.setPk_accsubj(jfpk);
		pzbvo_jf.setJfmny(sdsmny);
		pzbvo_jf.setDfmny(DZFDouble.ZERO_DBL);
		//
		pzbvo_df.setPk_accsubj(dfpk);
		pzbvo_df.setJfmny(DZFDouble.ZERO_DBL);
		pzbvo_df.setDfmny(sdsmny);
		//
		pzbvo_jf.setZy("计提所得税");// 摘要
		pzbvo_df.setZy("计提所得税");// 摘要
		// 币种，默认人民币
		pzbvo_jf.setPk_currency(cnypk);
		pzbvo_df.setPk_currency(cnypk);
		pzbvs[0] = pzbvo_jf;
		pzbvs[1] = pzbvo_df;
		return pzbvs;
	}

	@Override
	public DZFDouble getQuarterlySdsShui(String pk_corp, String period) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司不能为空！");
		}

		if (StringUtil.isEmpty(period)) {
			throw new BusinessException("期间不能为空！");
		}

		int month = new DZFDate(period + "-01").getMonth();

		DZFDouble shuikuan = DZFDouble.ZERO_DBL;
		// 查询利润表季报
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
		// 按季度查询是，开始日期也传递季度末月份的首日期
		DZFDate beginDate = new DZFDate(period + "-01");
		DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(period);
		paramVO.setQjz(period);
		LrbquarterlyVO[] vos = zxkjReportService.getLRBquarterlyVOs(paramVO);
		shuikuan = getQuarterlySdsShui1(pk_corp,period, vos);
		return shuikuan;
	}

	/**
	 * @param pk_corp
	 * @param period
	 * @param vos
	 * @return
	 */
	@Override
	public DZFDouble getQuarterlySdsShui1(String pk_corp, String period, LrbquarterlyVO[] vos) {
		DZFDouble shuikuan = DZFDouble.ZERO_DBL;
		int month = new DZFDate(period + "-01").getMonth();
		DZFDate beginDate = new DZFDate(period + "-01");
		LrbquarterlyVO qvo = null;
		LrbquarterlyVO sdsvo = null;
		if(vos !=null && vos.length > 0){
			for (LrbquarterlyVO vo : vos) {
				if ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm()) || "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm())
						|| "四、利润总额（亏损以“-”号填列）".equals(vo.getXm())) {
					qvo = vo;
				} else if ("减：所得税费用".equals(vo.getXm())
						|| "减：所得税".equals(vo.getXm())) {
					sdsvo = vo;
				}
			}
		}
		if (qvo == null)
			return shuikuan;
		// 计算总额(不包含期初的本年累计)
		DZFDouble first = qvo.getQuarterFirst();
		DZFDouble second = qvo.getQuarterSecond();
		DZFDouble third = qvo.getQuarterThird();
		DZFDouble fourth = qvo.getQuarterFourth();

		DZFDouble firstsds = DZFDouble.ZERO_DBL;
		DZFDouble secondsds = DZFDouble.ZERO_DBL;
		DZFDouble thirdsds = DZFDouble.ZERO_DBL;
		if (sdsvo != null) {
			firstsds = sdsvo.getQuarterFirst();
			secondsds = sdsvo.getQuarterSecond();
			thirdsds = sdsvo.getQuarterThird();
		}

		DZFDouble bnlj = qvo.getBnlj();
		DZFDouble nlossmny = DZFDouble.ZERO_DBL;
		// zpm start
		CorpVO pvo = corpService.queryByPk(pk_corp);
		DZFDate jzdate = pvo.getBegindate();
		if (jzdate == null)
			return shuikuan;
		QmLossesVO lossvo = queryLossmny(beginDate, pk_corp);

		if (lossvo != null) {
			nlossmny = lossvo.getNlossmny();
			if (nlossmny == null)
				nlossmny = DZFDouble.ZERO_DBL;
		}

		// zpm end

		// 计算 3 6 9 12 月份的税额
		switch (month) {
			case 3: {
				if (first != null && first.compareTo(DZFDouble.ZERO_DBL) > 0) {
					shuikuan = calcshuikuan2(period, bnlj, nlossmny);
				}
				break;
			}
			case 6: {
				if (second != null && second.compareTo(DZFDouble.ZERO_DBL) > 0) {
					shuikuan = calcshuikuan2(period, bnlj, nlossmny);
					shuikuan = SafeCompute.sub(shuikuan, firstsds);
				}
				break;
			}
			case 9: {
				if (third != null && third.compareTo(DZFDouble.ZERO_DBL) > 0) {
					shuikuan = calcshuikuan2(period, bnlj, nlossmny);
					shuikuan = SafeCompute.sub(shuikuan, SafeCompute.add(firstsds, secondsds));
				}
				break;
			}
			case 12: {
				if (fourth != null && fourth.compareTo(DZFDouble.ZERO_DBL) > 0) {
					shuikuan = calcshuikuan2(period, bnlj, nlossmny);
					shuikuan = SafeCompute.sub(shuikuan, SafeCompute.add(thirdsds, SafeCompute.add(firstsds, secondsds)));
				}
				break;
			}
			default:
				break;

		}
		return shuikuan;
	}

	/**
	 * @param pk_corp
	 * @param period
	 * @param vos
	 * @return
	 */
	@Override
	public DZFDouble getQuarterlySdsShuiYear1(String pk_corp, String period, LrbquarterlyVO[] vos) {
		DZFDouble shuikuan = DZFDouble.ZERO_DBL;
		int month = new DZFDate(period + "-01").getMonth();
		DZFDate beginDate = new DZFDate(period + "-01");
		LrbquarterlyVO qvo = null;
		LrbquarterlyVO sdsvo = null;
		for (LrbquarterlyVO vo : vos) {
			if ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm()) || "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm())) {
				qvo = vo;
			} else if ("减：所得税费用".equals(vo.getXm())) {
				sdsvo = vo;
			}
		}
		if (qvo == null)
			return shuikuan;
		// 计算总额(不包含期初的本年累计)
		DZFDouble first = qvo.getQuarterFirst();
		DZFDouble second = qvo.getQuarterSecond();
		DZFDouble third = qvo.getQuarterThird();
		DZFDouble fourth = qvo.getQuarterFourth();

		DZFDouble firstsds = DZFDouble.ZERO_DBL;
		DZFDouble secondsds = DZFDouble.ZERO_DBL;
		DZFDouble thirdsds = DZFDouble.ZERO_DBL;
		if (sdsvo != null) {
			firstsds = sdsvo.getQuarterFirst();
			secondsds = sdsvo.getQuarterSecond();
			thirdsds = sdsvo.getQuarterThird();
		}

		DZFDouble bnlj = qvo.getBnlj();
		DZFDouble nlossmny = DZFDouble.ZERO_DBL;
		// zpm start
		CorpVO pvo = corpService.queryByPk(pk_corp);
		DZFDate jzdate = pvo.getBegindate();
		if (jzdate == null)
			return shuikuan;
		QmLossesVO lossvo = queryLossmny(beginDate, pk_corp);

		if (lossvo != null) {
			nlossmny = lossvo.getNlossmny();
			if (nlossmny == null)
				nlossmny = DZFDouble.ZERO_DBL;
		}

		// zpm end

		// 计算 3 6 9 12 月份的税额
		switch (month) {
			case 3: {
				shuikuan = calcshuikuan2(period, bnlj, nlossmny);
				break;
			}
			case 6: {
				shuikuan = calcshuikuan2(period, bnlj, nlossmny);
				break;
			}
			case 9: {
				shuikuan = calcshuikuan2(period, bnlj, nlossmny);
				break;
			}
			case 12: {
				shuikuan = calcshuikuan2(period, bnlj, nlossmny);
				break;
			}
			default:
				break;

		}
		return shuikuan;
	}

	// 计算本季度的所得税
	private DZFDouble calcCurrentSuodeshui2(QmclVO qmvo, int month) {
		String pk_corp = qmvo.getPk_corp();
		String period = qmvo.getPeriod();
		return getQuarterlySdsShui(pk_corp, period);
	}

	/**
	 * 获取月度所得税
	 * @param qmvo
	 * @return
	 */
	private DZFDouble getMonthIncomeTax(QmclVO qmvo) {
		String pk_corp = qmvo.getPk_corp();
		String period = qmvo.getPeriod();
		Map<String, List<LrbVO>> map = zxkjReportService.getYearLrbMap(period.substring(0, 4), pk_corp, null, null,null);
		// 本月利润总额
		DZFDouble profit = null;
		// 所得税
		DZFDouble[] taxes = new DZFDouble[12];

		DZFDate beginDate = new DZFDate(period + "-01");
		// 本年累计利润总额
		DZFDouble bnlj = DZFDouble.ZERO_DBL;
		for (Map.Entry<String, List<LrbVO>> entry : map.entrySet()) {
			String mon = entry.getKey();
			int index = Integer.valueOf(mon.substring(5, 7)) - 1;
			List<LrbVO> vos = entry.getValue();
			for (LrbVO vo : vos) {
				if (mon.equals(period) && ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm()) || "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm())
						|| "四、利润总额（亏损以“-”号填列）".equals(vo.getXm()))) {
					profit = vo.getByje();
					bnlj = vo.getBnljje();
				} else if ("减：所得税费用".equals(vo.getXm())
						|| "减：所得税".equals(vo.getXm())) {
					taxes[index] = vo.getByje();
				}
			}
		}

		DZFDouble shuikuan = DZFDouble.ZERO_DBL;
		DZFDouble nlossmny = DZFDouble.ZERO_DBL;
		CorpVO pvo = corpService.queryByPk(pk_corp);
		DZFDate jzdate = pvo.getBegindate();
		if (jzdate == null)
			return shuikuan;
		QmLossesVO lossvo = queryLossmny(beginDate, pk_corp);

		if (lossvo != null) {
			nlossmny = lossvo.getNlossmny();
			if (nlossmny == null)
				nlossmny = DZFDouble.ZERO_DBL;
		}

		int mon = Integer.valueOf(period.substring(5, 7));
		if (profit != null && profit.doubleValue() > 0) {
			shuikuan = calcshuikuan2(period, bnlj, nlossmny);
			for (int i = 0; i < mon - 1; i++) {
				shuikuan = SafeCompute.sub(shuikuan, taxes[i]);
			}
		}
		return shuikuan;

	}

	private DZFDouble calcshuikuan2(String period, DZFDouble bnlj, DZFDouble nlossmny) {
		DZFDouble shuikuan1 = SafeCompute.sub(bnlj, nlossmny);
		if (shuikuan1.compareTo(DZFDouble.ZERO_DBL) < 0) {
			shuikuan1 = DZFDouble.ZERO_DBL;
		} else {
			if (period.compareTo("2019-01") < 0) {
				if (shuikuan1.compareTo(new DZFDouble(1000000)) <= 0) {
					shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.1));
					shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
				} else {
					shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.25));
					shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
				}
			} else {
				if (shuikuan1.compareTo(new DZFDouble(1000000)) <= 0) {
					shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.05));
				} else if (shuikuan1.compareTo(new DZFDouble(3000000)) <= 0) {
					shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.1));
				} else {
					shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.25));
				}
				shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
			}
		}
		if (shuikuan1.compareTo(DZFDouble.ZERO_DBL) < 0) {
			shuikuan1 = DZFDouble.ZERO_DBL;
		}
		return shuikuan1;
	}

	private LrbVO getLrbVO(LrbVO[] vos) {
		if (vos == null || vos.length == 0)
			return null;
		LrbVO qvo = null;
		for (LrbVO vo : vos) {
			if ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm()) || "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm())) {
				qvo = vo;
				break;
			}
		}
		return qvo;
	}

	private LrbVO[] getFiveYearsBeforeLrbVO(QmclVO qmvo) {
		String pk_corp = qmvo.getPk_corp();

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate jzdate = corpvo.getBegindate();
		String period = qmvo.getPeriod();

		String lastend = getQuarterEndDate(period);
		DZFDate enddate = new DZFDate(lastend);

		int endyear = enddate.getYear();
		int jzyear = jzdate.getYear();

		if (endyear < jzyear) {
			return new LrbVO[0];
		}

		Calendar calendar = Calendar.getInstance();
		Date date = new Date(enddate.getMillis());
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -5);
		date = calendar.getTime();
		DZFDate begindate = new DZFDate(date);
		// 查询之前五年数据
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
		paramVO.setBegindate1(begindate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(DateUtils.getPeriod(begindate));
		paramVO.setQjz(DateUtils.getPeriod(enddate));
		LrbVO[] vos = zxkjReportService.getLRBVOsByPeriod(paramVO);
		return vos;
	}

	private LrbVO[] getCurrentPeriodLrbVO(QmclVO qmvo, int month) {
		String pk_corp = qmvo.getPk_corp();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String period = qmvo.getPeriod();
		QueryParamVO paramVO = new QueryParamVO();
		paramVO.setPk_corp(pk_corp);
		paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
		DZFDate date = new DZFDate(period + "-01");
		int beginmonth = month - 2;
		String beginperiod = (new StringBuilder(String.valueOf(date.getYear()))).append("-")
				.append(beginmonth >= 10 ? ((Object) (Integer.valueOf(beginmonth)))
						: ((Object) ((new StringBuilder("0")).append(beginmonth).toString())))
				.toString();
		DZFDate beginDate = new DZFDate(beginperiod + "-01");
		DZFDate enddate = new DZFDate(period + "-" + date.getDaysMonth());
		paramVO.setBegindate1(beginDate);
		paramVO.setEnddate(enddate);
		paramVO.setQjq(beginperiod);
		paramVO.setQjz(period);
		LrbVO[] vos = zxkjReportService.getLRBVOsByPeriod(paramVO);
		return vos;
	}

	private String getQuarterEndDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
			case 1:
			case 2:
			case 3: {
				return "" + (iYear - 1) + "-12-31";
			}
			case 4:
			case 5:
			case 6: {
				return "" + iYear + "-03-31";
			}
			case 7:
			case 8:
			case 9: {
				return "" + iYear + "-06-30";
			}
			case 10:
			case 11:
			case 12: {
				return "" + iYear + "-09-30";
			}
		}
		return null;

	}

	@Override
	public QmclVO onfsdsjz(QmclVO qmvo) throws DZFWarpException {
		if(qmvo!=null && (qmvo.getQysdsjz() == null ||  !qmvo.getQysdsjz().booleanValue())){
			throw new BusinessException("所得税未计提不能反计提！");
		}
		updateCheckNj(qmvo);
		checkisGz(qmvo, "不能反企业所得税计提！");
		DZFBoolean isqjsyjz = qmvo.getIsqjsyjz();
		if (isqjsyjz != null && isqjsyjz.booleanValue()) {
			throw new BusinessException("期间:"+qmvo.getPeriod()+"，已经损益结转，不能反企业所得税计提！");
		}
		//
		SQLParameter sp = new SQLParameter();
		String wherepart = "select * from  ynt_qmcl where nvl(dr,0)=0 and period>?  and pk_corp= ? order by period  ";
		sp.addParam(qmvo.getPeriod());
		sp.addParam(qmvo.getPk_corp());
		List<QmclVO> value = (List<QmclVO>) singleObjectBO.executeQuery(wherepart, sp,
				new BeanListProcessor(QmclVO.class));
		for (QmclVO qmclVO : value) {
			if (qmclVO.getQysdsjz() != null && qmclVO.getQysdsjz().booleanValue()) {
				throw new BusinessException("存在后续期间（" + qmclVO.getPeriod() + "）的计提数据，不能跨月反企业所得税计提！");
			}
		}
		// 校验
		String wp = "pk_corp=? and sourcebillid= ? and sourcebilltype=? and nvl(dr,0)=0 ";
		SQLParameter sp1 = new SQLParameter();
		sp1.addParam(qmvo.getPk_corp());
		sp1.addParam(qmvo.getPk_qmcl());
		sp1.addParam(IBillTypeCode.HP125);
		TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wp, sp1);
		if (pzHeadVOs != null && pzHeadVOs.length > 0) {
			for (TzpzHVO headVO : pzHeadVOs) {
				if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
					// 已有凭证记账
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能反操作！");
				}
				if (headVO.getVbillstatus() == 1) {
					// 已有凭证审核通过
					throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能反操作！");
				}
			}
		}
		// 删除本月生成的增值税凭证
		String delsql1 = " update ynt_tzpz_b set dr = 1 where pk_tzpz_h in (select pk_tzpz_h from ynt_tzpz_h where pk_corp = ? and period=?  and sourcebilltype = ? ) ";
		String delsql2 = " update ynt_tzpz_h set dr = 1 where pk_corp = ? and period=?  and sourcebilltype = ? ";
		sp.clearParams();
		sp.addParam(qmvo.getPk_corp());
		sp.addParam(qmvo.getPeriod());
		sp.addParam(IBillTypeCode.HP125);
		singleObjectBO.executeUpdate(delsql1, sp);
		singleObjectBO.executeUpdate(delsql2, sp);
		sp.clearParams();
		sp.addParam(qmvo.getPk_corp());
		sp.addParam(qmvo.getPeriod());
		String updsql3 = " update ynt_qmcl set qysdsjz = 'N',nlossmny=0.00 where pk_corp = ? and period=? ";
		singleObjectBO.executeUpdate(updsql3, sp);
		qmvo.setQysdsjz(new DZFBoolean(false));
		gl_taxarchive.updateIncomeTaxUnCarryover(qmvo.getPk_corp(), qmvo.getPeriod());
		return qmvo;
	}

	@Override
	public void updatehdsyzt(QmclVO qmvo,String userid) throws DZFWarpException {
		// 更新期末调汇
		qmvo.setIshdsytz(new DZFBoolean(true));
		singleObjectBO.update(qmvo, new String[] { "ishdsytz" });
	}

	private String getUpdateKmcode(CorpVO rpvo, String code) {
		Map<String, String> map = KmbmUpgrade.getKmUpgradeinfo(rpvo, new String[] { code });
		String result = code;
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				if (code.equals(map.get(key))) {
					result = key;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public QmLossesVO queryLossmny(DZFDate dateq, String copid) throws DZFWarpException {
		if (StringUtil.isEmpty(copid)) {
			throw new BusinessException("公司为空!");
		}
		if (dateq == null) {
			throw new BusinessException("期间为空!");
		}
		int year = dateq.getYear();
		String condition = " nvl(dr,0)=0 and pk_corp = ? and period =? ";
		SQLParameter params = new SQLParameter();
		params.addParam(copid);
		params.addParam(year);
		QmLossesVO[] vos = (QmLossesVO[]) singleObjectBO.queryByCondition(QmLossesVO.class, condition, params);
		QmLossesVO  vo= null;
		if(vos != null && vos.length>0){
			vo = vos[0];
		}else{
			vo = new QmLossesVO();
			vo.setPeriod(String.valueOf(year));
			vo.setPk_corp(copid);
			vo.setNlossmny(DZFDouble.ZERO_DBL);
			vo.setDr(0);
		}
		return vo;
	}

	@Override
	public QmLossesVO updateLossmny(DZFDate dateq, String copid, DZFDouble mny) throws DZFWarpException {
		if (StringUtil.isEmpty(copid)) {
			throw new BusinessException("公司为空!");
		}

		if (dateq == null) {
			throw new BusinessException("期间为空!");
		}
		/*boolean isQuarter = true;
		YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(copid, "dzf013");
		if (paramSet != null && paramSet.getPardetailvalue() != 0) {
			isQuarter = false;
		}
		if (isQuarter) {
			int year = dateq.getYear();
			SQLParameter sp = new SQLParameter();
			sp.addParam(copid);
			sp.addParam(year+"-03");
			sp.addParam(year+"-06");
			sp.addParam(year+"-09");
			sp.addParam(year+"-12");
			String where = " pk_corp = ? and period in(?,?,?,?) and nvl(dr,0) = 0 and qysdsjz ='Y' ";
			QmclVO[] vos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, where, sp);
			QmclVO qmvo = null;
			if (vos != null && vos.length > 0){
				throw new BusinessException(year+"年度存在季度计提的所得税,不可修改。");
			}
		} else {
			int year = dateq.getYear();
			SQLParameter sp = new SQLParameter();
			sp.addParam(copid);
			sp.addParam(year + "-%");
			String sql = "select 1 from ynt_qmcl where pk_corp = ? and period like ? and nvl(dr,0) = 0 and qysdsjz ='Y' ";
			boolean isExist = singleObjectBO.isExists(copid, sql, sp);
			if (isExist) {
				throw new BusinessException(year + "年度存在月度计提的所得税,不可修改。");
			}
		}*/

		QmLossesVO vo = queryLossmny(dateq, copid);
		vo.setNlossmny(mny);
		singleObjectBO.saveObject(copid, vo);
		return vo;
	}

	@Override
	public void checkQmclForKc(String pk_corp, String period, boolean isbat) throws DZFWarpException {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp1 = new SQLParameter();
		qrysql.append("  select *  ");
		qrysql.append("  from ynt_ictrade_h   ");
		qrysql.append("   where pk_corp = ?  ");
		qrysql.append("   and dbilldate like ? ");
		qrysql.append("   and nvl(dr,0)=0 ");
		qrysql.append("   and nvl(iszg,'N')= 'N' ");
		qrysql.append("   and nvl(isjz,'N')= 'N' ");//尚未生成凭证的数据
		sp1.addParam(pk_corp);
		sp1.addParam(period+"%");

		List<IntradeHVO> inhvos =  (List<IntradeHVO>) singleObjectBO.executeQuery(qrysql.toString(), sp1, new BeanListProcessor(IntradeHVO.class));

		StringBuffer error = new StringBuffer();

		if (inhvos != null && inhvos.size() > 0) {
			int in_count = 0;
			int out_count = 0;
			for(IntradeHVO hvo:inhvos){
				if(IBillTypeCode.HP70.equals(hvo.getCbilltype())){//入库单
					in_count++;
				}else if(IBillTypeCode.HP75.equals(hvo.getCbilltype())){//出库单
					out_count++;
				}
			}

			if(in_count>0){
				error.append(",有"+in_count+"张入库单未生成凭证");
			}

			if(out_count>0){
				error.append(",有"+out_count+"张出库单未生成凭证");
			}

			if(!isbat && error.length() > 0){
				error = new StringBuffer("公司:" + deCodename(corpvo.getUnitname()) + ",期间:"+period).append(error);
			}
		}

		if(error.toString().length()>0){
			throw new BusinessException(error.toString()+", 不能成本结转!");
		}
	}

	@Override
	public TzpzHVO createVoucherByTaxCalculator(CorpVO corpVO, TaxCalculateVO taxCal,
												Integer taxType, QmclVO qmvo, String userid) throws DZFWarpException {
		TzpzHVO voucher = null;
		String period = taxCal.getPeriod();
		if (taxType == TaxCalculateVO.TYPE_ADDTAX) {
			if ("一般纳税人".equals(corpVO.getChargedeptname())) {
				DZFDouble taxMny = null;
				DZFDouble ynse = taxCal.getAddtax_info().getYbtse();
				if (ynse != null && ynse.doubleValue() > 0) {
					taxMny = ynse;
				} else {
					DZFDouble qmld = taxCal.getAddtax_info().getQmld();
					if (qmld != null && qmld.doubleValue() > 0) {
						taxMny = qmld.multiply(-1);
					}
				}
				if (taxMny != null && taxMny.doubleValue() != 0) {
					TzpzBVO[] children = createpzBVO(corpVO, qmvo.getPk_corp(), taxMny);
					voucher = createpzinfo(qmvo, taxMny, IBillTypeCode.HP120, userid);
					voucher.setChildren(children);
					gl_tzpzserv.saveVoucher(corpVO, voucher);
				}
			} else {
				DZFDouble taxMny = taxCal.getAddtax_info().getBqmse();
				if (taxMny != null && taxMny.doubleValue() > 0) {
					voucher = createVoucherByZzsSmall(qmvo, corpVO, corpVO.getPk_corp(), taxMny,userid);
					gl_tzpzserv.saveVoucher(corpVO, voucher);
				}
			}
			qmvo.setZzsjz(new DZFBoolean(true));
			singleObjectBO.update(qmvo, new String[] { "zzsjz" });
		}else if (taxType == TaxCalculateVO.TYPE_INCOMETAX) {
			DZFDouble taxMny = taxCal.getIncometax().getSjybtsds();
			if (taxMny != null && taxMny.doubleValue() > 0) {
				boolean isIndividual = taxCal.getSettings().getIncomeTaxType() != null
						&& taxCal.getSettings().getIncomeTaxType() == 1;
				voucher = createsdsjtpz(qmvo, taxMny, corpVO,userid,
						isIndividual);
				gl_tzpzserv.saveVoucher(corpVO, voucher);
			}
			qmvo.setQysdsjz(new DZFBoolean(true));
			singleObjectBO.update(qmvo, new String[] { "qysdsjz" });
		}
		return voucher;
	}

	@Override
	public QmWgcpVO[] impExcel(MultipartFile infile, String pk_corp, String fileType, String cuserid) throws DZFWarpException {
        InputStream is = null;
		try {
            is = infile.getInputStream();
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);

			Map<Integer, String> fieldColumn = QmWgcpVO.getExcelFieldColumn();
			Cell codeCell = null;
			String key = null;
			int length = sheet1.getLastRowNum();
			if (length > 1000) {
				throw new BusinessException("最多可导入1000行");
			}
			Map<String, InventoryVO> invmap = new HashMap<>();
			List<InventoryVO> invVO = queryInventoryVO(pk_corp);

			if (invVO != null && invVO.size() > 0) {
				for (InventoryVO invvo : invVO) {
					String key1 = getCheckKey(invvo);
					invmap.put(key1, invvo);
				}
			}
			List<QmWgcpVO> billlist = new ArrayList<>();
			QmWgcpVO vo = null;
			InventoryVO tempvo = null;
			boolean isrownull = true;
			for (int iBegin = 8; iBegin <= length; iBegin++) {
				isrownull = true;
				vo = new QmWgcpVO();
				for (Map.Entry<Integer, String> entry : fieldColumn.entrySet()) {

					if (sheet1.getRow(iBegin) == null)
						continue;

					codeCell = sheet1.getRow(iBegin).getCell(entry.getKey());
					if (codeCell == null)
						continue;
					else {
						if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							if (codeCell.getNumericCellValue() == 0) {
								continue;
							} else {
								isrownull = false;
							}
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							if (codeCell.getRichStringCellValue() == null) {
								continue;
							} else {
								isrownull = false;
							}
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
						} else {
							continue;
						}
					}

					key = entry.getValue();
					if (key.endsWith("_qc") || key.endsWith("_fs") || key.endsWith("_wg") || key.endsWith("_nwg")) {
						if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							vo.setAttributeValue(key, codeCell.getNumericCellValue());
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							vo.setAttributeValue(key, replaceBlank(codeCell.getRichStringCellValue().getString()));
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
							String value1 = null;
							try {
								java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
								value1 = formatter.format(codeCell.getNumericCellValue());
								vo.setAttributeValue(key, replaceBlank(value1));
							} catch (Exception e) {
							}
							if (StringUtil.isEmpty(value1) || "0.00".equals(value1)) {
								try {
									FormulaEvaluator evaluator = codeCell.getSheet().getWorkbook().getCreationHelper()
											.createFormulaEvaluator();
									CellValue cellValue = evaluator.evaluate(codeCell);
									vo.setAttributeValue(key, cellValue.getNumberValue());
								} catch (Exception e) {
								}
							}
						}
					} else {
						String value = null;
						if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							value = codeCell.getRichStringCellValue().getString();
							value = replaceBlank(value.trim());
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							int codeVal = Double.valueOf(codeCell.getNumericCellValue()).intValue();
							value = String.valueOf(codeVal);
						}
						if(value.indexOf("%") >0){
							value= value.replace("%","");
						}
						vo.setAttributeValue(key, value);
					}
				}
				if(!isrownull){
					tempvo= invmap.get(getCheckKey(vo));
					if(tempvo == null)
						throw new BusinessException("存货编码"+vo.getVcode()+"未匹配到系统存货！");
					vo.setPk_inventory(tempvo.getPk_inventory());
					billlist.add(vo);
				}
			}
			return billlist.toArray(new QmWgcpVO[billlist.size()]);
		} catch (BusinessException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		} catch (Exception e) {
			throw new BusinessException("导入文件格式错误");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

    private List<InventoryVO> queryInventoryVO(String pk_corp) {
        StringBuffer sb = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sb.append("pk_corp=? and nvl(dr,0)=0");
        sp.addParam(pk_corp);
        List<InventoryVO> listVo = (List<InventoryVO>) singleObjectBO.retrieveByClause(InventoryVO.class, sb.toString(),
                sp);
        return listVo;
    }

    private String getCheckKey(InventoryVO invvo) {
        StringBuffer sb = new StringBuffer();

        if (StringUtil.isEmpty(invvo.getCode())) {
            sb.append(" ");
        } else {
            sb.append(replaceBlank(invvo.getCode().trim()));
        }

        if (StringUtil.isEmpty(invvo.getName())) {
            sb.append(" ");
        } else {
            sb.append(replaceBlank(invvo.getName().trim()));
        }
        return sb.toString();
    }

    private String getCheckKey(QmWgcpVO invvo) {
        StringBuffer sb = new StringBuffer();

        if (StringUtil.isEmpty(invvo.getVcode())) {
            sb.append(" ");
        } else {
            sb.append(replaceBlank(invvo.getVcode().trim()));
        }

        if (StringUtil.isEmpty(invvo.getVname())) {
            sb.append(" ");
        } else {
            sb.append(replaceBlank(invvo.getVname().trim()));
        }
        return sb.toString();
    }

    private String replaceBlank(String str) {
        String dest = "";
        if (!StringUtil.isEmpty(str)) {
            dest= StringUtil.replaceBlank(str);
        }
        if(dest.indexOf("%") >0){
            dest= dest.replace("%","");
        }
        return dest;
    }
}