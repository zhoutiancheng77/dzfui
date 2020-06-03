package com.dzf.zxkj.platform.service.pzgl.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.admin.zxkj.model.contract.AppContractQryVO;
import com.dzf.admin.zxkj.model.result.ZxkjResult;
import com.dzf.admin.zxkj.service.contract.IZxkjContractService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.StateEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.CircularlyAccessibleValueObject;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.enums.KmschemaCash;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.PZTaxItemRadioVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.PzSourceRelationVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.DatatruansVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.yscs.DzfpscReqBVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icbill.ISaleoutService;
import com.dzf.zxkj.platform.service.glic.impl.ICbillcodeCreate;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.ISMcbftService;
import com.dzf.zxkj.platform.service.jzcl.impl.CancelCbjz;
import com.dzf.zxkj.platform.service.pjgl.IExpBillService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.tax.ITaxCalculateArchiveService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCard;
import com.dzf.zxkj.platform.service.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.service.zcgl.IYzbgService;
import com.dzf.zxkj.platform.service.zcgl.IZczjmxReport;
import com.dzf.zxkj.platform.service.zncs.IBillcategory;
import com.dzf.zxkj.platform.service.zncs.IZncsVoucher;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.platform.util.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 凭证管理
 * 
 * @author Administrator
 * 
 */
@Service("gl_tzpzserv")
@Slf4j
public class VoucherServiceImpl implements IVoucherService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private IPersonalSetService gl_gxhszserv;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private IQmclService gl_qmclserv = null;
	@Autowired
	private IZxkjContractService zxkjContractService;
	@Autowired
	IDcpzService dcpzjmbserv;
	@Autowired
	IPzglService gl_pzglserv;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private ICbillcodeCreate icbillcode_create;
	@Autowired
	private IImageGroupService gl_pzimageserv;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv = null;
	
	@Autowired
	private ISMcbftService gl_smcbftserv;
	@Autowired(required = false)
	private IZncsVoucher iZncsVoucher;//智能财税
	
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ITaxCalculateArchiveService gl_taxarchive;
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired(required = false)
	private IBillcategory iBillcategory;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	// 检查合同
	private String checkContract(String fatherCorp, String corp, String date) throws DZFWarpException {
		String msg = null;
		AppContractQryVO param = new AppContractQryVO();
		param.setPk_corp(fatherCorp);
		param.setPk_corpk(corp);
		param.setVoucherdate(date);
		try {
			ZxkjResult<String> rs = zxkjContractService.checkCorpContract(param);
			if (rs != null && rs.getCode() == 200) {
				msg = rs.getData();
			} else {
				log.error("调用合同检查接口失败", rs);
			}
		} catch (Exception e) {
			log.error("调用合同检查接口失败", e);
		}
		return msg;
	}

	@Override
	public TzpzHVO saveVoucher(CorpVO corpvo, TzpzHVO hvo) throws DZFWarpException {
		
		// TODO 需要验证科目是否停用，是否启用外币，是否启用存货，外币是否浮动，单价和数量与金额是否匹配
		// saveBefore(hvo);
		String contractMsg = checkContract(corpvo.getFathercorp(), corpvo.getPk_corp(),
				hvo.getDoperatedate().toString());
		if (!StringUtil.isEmpty(contractMsg)) {
			throw new BusinessException(contractMsg);
		}
		//智能新版相关处理
		dealAIInfoByAddBefore(hvo);


		DZFDate doperatedate = hvo.getDoperatedate();
		hvo.setPeriod(DateUtils.getPeriod(doperatedate));
		hvo.setVyear(doperatedate.getYear());

		Boolean isXjll = false;
		Boolean isChange = false;
		Integer modifyStatus = 0;// 会计工厂--凭证修改状态 （1--科目变化,2--金额变化）
		String[] sourcebillids = null;// 合并生成凭证 多个来源id存储

		YntCpaccountVO[] cpavos = accountService.queryByPk(hvo.getPk_corp());
		Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(cpavos), new String[]{"pk_corp_account"});
//		Map<String, YntCpaccountVO> map = AccountCache.getInstance().getMap(null, hvo.getPk_corp());
		TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
		// modify by zhangj 修改科目对应科目编码(考虑科目编码规则升级的情况)
		if(bvos != null && bvos.length>0){
			for (TzpzBVO bvo : bvos) {
				if (map.get(bvo.getPk_accsubj()) != null) {
					bvo.setVcode(map.get(bvo.getPk_accsubj()).getAccountcode());
				}
			}
		}

		// 工资表重复生成凭证
		if (!StringUtil.isEmpty(hvo.getSourcebilltype())) {

			if (hvo.getSourcebilltype().endsWith("gzjt") || hvo.getSourcebilltype().endsWith("gzff")) {
				String wheresql = " pk_corp = ? and nvl(dr,0) = 0 and (sourcebilltype = ? "
						+ " or pk_tzpz_h in (select pk_tzpz_h from ynt_pz_sourcerelation"
						+ " where pk_corp = ? and sourcebilltype = ? and nvl(dr,0) = 0))  ";
				SQLParameter sp = new SQLParameter();
				sp.addParam(hvo.getPk_corp());
				sp.addParam(hvo.getSourcebilltype());
				sp.addParam(hvo.getPk_corp());
				sp.addParam(hvo.getSourcebilltype());
				TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql, sp);
				//
				if (hvos != null && hvos.length > 0) {
					hvo.setPk_tzpz_h(hvos[0].getPk_tzpz_h());
					hvo.setIsfpxjxm(hvos[0].getIsfpxjxm());
				}
			}

			// 判断银行对账饭 销进项 合并生成凭证
			if (StringUtil.isEmpty(hvo.getPrimaryKey()) && (IBillTypeCode.HP85.equals(hvo.getSourcebilltype())
					|| IBillTypeCode.HP90.equals(hvo.getSourcebilltype())
					|| IBillTypeCode.HP95.equals(hvo.getSourcebilltype())
					|| IBillTypeCode.HP70.equals(hvo.getSourcebilltype())
					|| IBillTypeCode.HP75.equals(hvo.getSourcebilltype())//mfz 增加70 75 出入库单汇总转总账
					|| IBillTypeCode.HP135.equals(hvo.getSourcebilltype()))) {
				if (!StringUtil.isEmpty(hvo.getSourcebillid())) {
					sourcebillids = hvo.getSourcebillid().split(",");
					hvo.setSourcebillid(sourcebillids[0]);
				}
			}
		}

		// 凭证税目
		List<PZTaxItemRadioVO> voucherTaxItem = null;
		boolean  isadd = true;
		// end 2016.3.4
		if (!StringUtil.isEmpty(hvo.getPk_tzpz_h())) {
			isadd= false;
			QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
			TzpzBVO[] qbvos = qv.queryBodyVos(hvo.getPk_tzpz_h());
			TzpzHVO qhvo = qv.queryVoucherById(hvo.getPk_tzpz_h());
			hvo.setBsign(qhvo.getBsign());
			// 重新税目分析
			boolean reAnalyseTaxItem = false;
			Integer defaultFpstyle = "一般纳税人".equals(corpvo.getChargedeptname()) ? 2 : 1;
			Integer oldFpStyle = qhvo.getFp_style() == null ? defaultFpstyle : qhvo.getFp_style();
			Integer newFpStyle = hvo.getFp_style() == null ? defaultFpstyle : hvo.getFp_style();
			if (!oldFpStyle.equals(newFpStyle) || !qhvo.getPeriod().equals(hvo.getPeriod())) {
				// 小规模发票类型改变，期间改变,重新分析税目
				reAnalyseTaxItem = true;
			}
			// 赋值来源(如果来源不存在的)
			if (!"Y".equals(hvo.getIsMerge())) {
				if (StringUtil.isEmpty(hvo.getSourcebillid())) {
					hvo.setSourcebillid(qhvo.getSourcebillid());
				}
				// 如果是工资表打开的凭证 重新设置单据类型
				if (StringUtil.isEmpty(hvo.getSourcebilltype())) {
					hvo.setSourcebilltype(qhvo.getSourcebilltype());
				}
			}
			if (!StringUtil.isEmpty(hvo.getSourcebilltype())) {
				// 工业成本结转 步骤
				if (hvo.getSourcebilltype().equals(IBillTypeCode.HP34)) {
					hvo.setCbjzCount(qhvo.getCbjzCount());
				}
			}

			if (hvo.getIsfpxjxm() != null && hvo.getIsfpxjxm().booleanValue()) {
				// 如果修改了凭证删除现金流量数据
				if (!hvo.getDoperatedate().toString().substring(0, 7)
						.equals(qhvo.getDoperatedate().toString().substring(0, 7)))
					isChange = true;
				if (bvos.length != qbvos.length) {
					isChange = true;
				} else {
					for (int i = 0; i < bvos.length; i++) {
						TzpzBVO oldvo = qbvos[i];
						TzpzBVO newvo = bvos[i];
						if (!newvo.getPk_accsubj().equals(oldvo.getPk_accsubj())
								|| !VoUtils.getDZFDouble(oldvo.getDfmny())
										.equals(VoUtils.getDZFDouble(newvo.getDfmny()))
								|| !VoUtils.getDZFDouble(oldvo.getJfmny())
										.equals(VoUtils.getDZFDouble(newvo.getJfmny()))) {
							isChange = true;
							break;
						}
					}
				}
				if (isChange)
					updateXjll(hvo);
			}
			if (bvos != null && qbvos != null) {
				Boolean kmstatus = false;
				Boolean jestatus = false;
				if (bvos.length != qbvos.length) {
					modifyStatus = 3; // 科目改变
				} else {
					for (int i = 0; i < bvos.length; i++) {
						TzpzBVO oldvo = qbvos[i];
						TzpzBVO newvo = bvos[i];
						if (!newvo.getPk_accsubj().equals(oldvo.getPk_accsubj())) {
							kmstatus = true;// 科目改变
						}
						if (!VoUtils.getDZFDouble(oldvo.getDfmny()).equals(VoUtils.getDZFDouble(newvo.getDfmny()))
								|| !VoUtils.getDZFDouble(oldvo.getJfmny())
										.equals(VoUtils.getDZFDouble(newvo.getJfmny()))) {
							jestatus = true;// 金额改变
						}
					}
					if (kmstatus && jestatus) {
						modifyStatus = 3;
					}
					if (kmstatus && !jestatus) {
						modifyStatus = 1;
					}
					if (!kmstatus && jestatus) {
						modifyStatus = 2;
					}
				}
				if (!reAnalyseTaxItem && (bvos.length != qbvos.length || kmstatus || jestatus)) {
					// 科目或金额改变重新分析税目
					reAnalyseTaxItem = true;
				}
			}
			if (qhvo.getIs_tax_analyse() != null
					&& qhvo.getIs_tax_analyse()) {
				if (reAnalyseTaxItem) {
					deleteVoucherTaxItemAlone(hvo.getPk_tzpz_h(), hvo.getPk_corp());
				} else {
					// 取之前状态
					hvo.setError_tax_analyse(qhvo.getError_tax_analyse());
					PZTaxItemRadioVO[] items = getVoucherTaxItem(hvo.getPk_tzpz_h(), hvo.getPk_corp());
					voucherTaxItem = Arrays.asList(items);

					for (PZTaxItemRadioVO item: voucherTaxItem) {
						for (int i = 0; i < qbvos.length; i++) {
							if (qbvos[i].getPk_tzpz_b().equals(item.getPk_tzpz_b())) {
								item.setEntry_index(i);
							}
						}
					}
				}
			}

			singleObjectBO.deleteVOArray(qbvos);// 删除原子表
		}

		Integer accSchema = yntBoPubUtil.getAccountSchema(hvo.getPk_corp());
		if(bvos!=null && bvos.length>0){
			for (int i = 0; i < bvos.length; i++) {
				if (bvos[i].getRowno() == null) {
					bvos[i].setRowno(i + 1);
				}
				YntCpaccountVO accountvo = map.get(bvos[i].getPk_accsubj());
				if (accountvo == null) {// [如果缓存不存在，按主键查询]
					accountvo = queryAccountByid(bvos[i].getPk_accsubj());
				}
				if (accountvo == null || (accountvo.getDr() != null && accountvo.getDr().intValue() == 1)) {
					throw new BusinessException("科目不存在，或已被删除！");
				} else {// [导入的凭证科目，需要拆分，需要后续重新填写，这里不强制要求，填写以下字段信息]
					if (accountvo.getBisseal() != null && accountvo.getBisseal().booleanValue())
						throw new BusinessException("科目已封存！");
//					if (!accountvo.getIsleaf().booleanValue()){
						//期末处理生成的凭证，如果非末级，找末级第一个凭证，保存
						accountvo = handleUnleafPz(hvo,bvos[i],cpavos,accountvo,corpvo);
						
//					}
						
					bvos[i].setVcode(accountvo.getAccountcode());
					bvos[i].setVname(accountvo.getAccountname());
					bvos[i].setKmmchie(accountvo.getFullname());
					bvos[i].setSubj_code(accountvo.getAccountcode());
					bvos[i].setSubj_name(accountvo.getAccountname());
					bvos[i].setPk_tzpz_b(null);// 先删除子表，不需要主键
					if (!isXjll) {
						if (accSchema == DzfUtil.POPULARSCHEMA.intValue() && (accountvo.getAccountcode().startsWith("1001")
								|| accountvo.getAccountcode().startsWith("1002")
								|| accountvo.getAccountcode().startsWith("1009"))) {
							isXjll = true;
						} else if ( accSchema == DzfUtil.SEVENSCHEMA.intValue() && (accountvo.getAccountcode().startsWith("1001")
								|| accountvo.getAccountcode().startsWith("1002")
								|| accountvo.getAccountcode().startsWith("1012")
								|| accountvo.getAccountcode().startsWith("1101")
								)) {
							isXjll = true;
						}  else if (accountvo.getAccountcode().startsWith("1001")
								|| accountvo.getAccountcode().startsWith("1002")
								|| accountvo.getAccountcode().startsWith("1012")) {
							isXjll = true;
						}else if(accSchema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue() && (accountvo.getAccountcode().startsWith("1001")
								|| accountvo.getAccountcode().startsWith("1002")
								|| accountvo.getAccountcode().startsWith("1009"))){
							isXjll = true;
						}
						
					}
					
				}
				bvos[i].setPk_corp(hvo.getPk_corp());
			}
		}
		if (!isXjll || StringUtil.isEmpty(hvo.getPk_tzpz_h())) {
			hvo.setIsfpxjxm(new DZFBoolean(false));
			hvo.setError_cash_analyse(false);
		}
		List<XjllVO> xjllvos = new ArrayList<XjllVO>();
		if (isXjll && (hvo.getIsfpxjxm() == null || !hvo.getIsfpxjxm().booleanValue()) || isChange && isXjll) {// 现金流量自动分析
			CashFlowAnalyse cfAnalyse = new CashFlowAnalyse(singleObjectBO);
			xjllvos = cfAnalyse.autoAnalyse(hvo, corpvo,cpavos);
			if (xjllvos == null) {
				hvo.setError_cash_analyse(false);
			}
			if (xjllvos != null && xjllvos.size() > 0) {
				hvo.setIsfpxjxm(new DZFBoolean(true));
				hvo.setAutoAnaly(new DZFBoolean(true));
			} else if (hvo.getIsfpxjxm() != null && hvo.getIsfpxjxm().booleanValue()) {
				hvo.setIsfpxjxm(new DZFBoolean(false));
			}
		}
		checkBeforeSave(corpvo, hvo, map);

		if (voucherTaxItem == null || voucherTaxItem.size() == 0) {
			voucherTaxItem = getVoucherTaxItemFromEntries(bvos);
		}
		if (voucherTaxItem.isEmpty()
				&& (hvo.getIsbsctaxitem() == null || !hvo.getIsbsctaxitem().booleanValue())) {
			voucherTaxItem = new CaclTaxMny().analyseTaxItem(corpvo, hvo,
					getTaxItems(corpvo.getChargedeptname()));
		}
		if (voucherTaxItem == null || voucherTaxItem.size() == 0) {
			hvo.setIs_tax_analyse(false);
            hvo.setError_tax_analyse(false);
		} else {
            hvo.setIs_tax_analyse(true);
        }
		if ("Y".equals(hvo.getIsInsert())) {
			updateVoucherNumberWhenInsert(hvo);
		}
		// 删除现金流量的数据
		// updateXjll(hvo);
		//没有启用库存，但启用总账存货模块。zpmmm增加
		if(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){
			//
			InventorySetVO vo = gl_ic_invtorysetserv.query(corpvo.getPk_corp());
			int chcbjzfs = InventoryConstant.IC_NO_MXHS;
			if(vo != null){
				chcbjzfs = vo.getChcbjzfs();
			}
			//大类或明细
			if(chcbjzfs == InventoryConstant.IC_CHDLHS 
					|| chcbjzfs == InventoryConstant.IC_FZMXHS){
				doicBillcode(hvo,corpvo,map);
				saveCbftJE(hvo,corpvo);
			}
		}
		singleObjectBO.saveObject(hvo.getPk_corp(), hvo);
		// 给税表表项设置凭证信息
		if (voucherTaxItem != null && voucherTaxItem.size() > 0) {
			Integer fpStyle = hvo.getFp_style();
			if (fpStyle == null) {
				fpStyle = "一般纳税人".equals(corpvo.getChargedeptname()) ? 2 : 1;
			}
			for (PZTaxItemRadioVO item : voucherTaxItem) {
				item.setPk_corp(corpvo.getPk_corp());
				item.setPk_tzpz_h(hvo.getPk_tzpz_h());
                item.setPeriod(hvo.getPeriod());
				item.setFp_style(fpStyle);
				if (item.getEntry_index() != null) {
					TzpzBVO bvo = bvos[item.getEntry_index()];
					item.setPk_tzpz_b(bvo.getPk_tzpz_b());
					Integer direct = bvo.getVdirect();
					if (direct == null) {
						direct = bvo.getDfmny() == null || bvo.getDfmny().doubleValue() == 0 ? 0 : 1;
					}
					item.setVdirect(direct);
				}
				item.setDr(0);
			}
			if (StringUtil.isEmpty(voucherTaxItem.get(0).getPk_pztaxitem())) {
				singleObjectBO.insertVOArr(corpvo.getPk_corp(), voucherTaxItem.toArray(new PZTaxItemRadioVO[0]));
			} else {
				singleObjectBO.updateAry(voucherTaxItem.toArray(new PZTaxItemRadioVO[0]),
						new String[]{ "pk_tzpz_b", "period", "fp_style" });
			}
		}
		// 处理税目
//		dealTaxItem(hvo, corpvo);
		// 调用会计工厂接口，使用范围：针对来源是会计工厂的
//		if ("factory".equals(hvo.getSourcebilltype())) {
//			try {
//				IFctPzService iFctPzService = (IFctPzService) SpringUtils.getBean("fctPzService");
//				iFctPzService.modifyPzglChange(modifyStatus, hvo, FactoryConst.FactoryTask_3);// 传修改状态
//			} catch (Exception e) {
//				log.error("调用会计工厂接口报错", e);// 调用会计工厂接口，如有异常，此处不向上层抛出
//			}
//			// end
//		}

		if (xjllvos != null && xjllvos.size() > 0) {
			for (XjllVO xjllVO : xjllvos) {
				xjllVO.setPk_tzpz_h(hvo.getPk_tzpz_h());
			}
			singleObjectBO.insertVOArr(hvo.getPk_corp(), xjllvos.toArray(new XjllVO[0]));
		}
		PzSourceRelationVO[] relations = hvo.getSource_relation();
		if (relations == null || relations.length == 0) {
			relations = getSourceRelations(hvo.getPk_tzpz_h(), hvo.getPk_corp());
		} else {
			for (int i = 0; i < relations.length; i++) {
				relations[i].setPk_tzpz_h(hvo.getPk_tzpz_h());
			}
			singleObjectBO.insertVOArr(hvo.getPk_corp(), relations);
		}
		if (!StringUtil.isEmpty(hvo.getSourcebilltype())) {
			List<PzSourceRelationVO> list = new ArrayList<PzSourceRelationVO>();
			if (relations != null && relations.length > 0) {
				list.addAll(Arrays.asList(relations));
			}
			PzSourceRelationVO relationVO = new PzSourceRelationVO();
			relationVO.setSourcebilltype(hvo.getSourcebilltype());
			relationVO.setSourcebillid(hvo.getSourcebillid());
			list.add(relationVO);
			relations = list.toArray(new PzSourceRelationVO[0]);
		}
		if (relations != null && relations.length > 0) {
			for (PzSourceRelationVO relationVO : relations) {
				if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP59)) { // 来源于固定资产
					IAssetCard assetCard = (IAssetCard) SpringUtils.getBean("assetCardImpl");
					if (!StringUtil.isEmpty(relationVO.getSourcebillid())) {// 批量的不校验
						assetCard.updateToGLState(relationVO.getSourcebillid(), true, hvo.getPrimaryKey(), 0);
					}
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP60)) { // 来源于资产原值变更单
					IYzbgService am_yzbgserv = (IYzbgService) SpringUtils.getBean("am_yzbgserv");
					am_yzbgserv.updateAVToGLState(relationVO.getSourcebillid(), true, hvo.getPrimaryKey());
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP61)) { // 来源于资产清理单
					IAssetCleanService cleanService = (IAssetCleanService) SpringUtils.getBean("am_assetclsserv");
					cleanService.updateACToGLState(relationVO.getSourcebillid(), true, hvo.getPrimaryKey());
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP66)) { // 来源于资产折旧
//					IAssetCard assetCard = (IAssetCard) SpringUtils.getBean("assetCardImpl");
//					assetCard.updateDepToGLState(relationVO.getSourcebillid(), true, hvo.getPrimaryKey());
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP67)) { // 来源于资产折旧,多个资产明细生成一张凭证

				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP34)) { // 更新完工入库单
					// 凭证号
					updateIcBill(corpvo, hvo);
				}else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP70)) { // 更新采购单
					// 凭证号
					updateIcBill(corpvo, hvo);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP75)) {// 更新销售单凭证号
					updateIcBill(corpvo, hvo);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP80)) {// 更新票通转总账标识
					if (StringUtil.isEmptyWithTrim(relationVO.getSourcebillid())) {
						throw new BusinessException("凭证保存：票通单据未找到来源，请检查");
					}
					SQLParameter sp = new SQLParameter();
					sp.addParam(DZFBoolean.TRUE.toString());
					sp.addParam(relationVO.getSourcebillid());
					singleObjectBO.executeUpdate(" update ynt_ticket_h h set h.istogl = ? where h.pk_ticket_h = ? ", sp);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP85)) {// 银行对账单
					if (StringUtil.isEmptyWithTrim(relationVO.getSourcebillid())) {
						throw new BusinessException("银行对账单未找到来源，请检查");
					}

					if (sourcebillids != null && sourcebillids.length > 0) {
						StringBuffer sf = new StringBuffer();
						sf.append(" update ynt_bankstatement y set y.pk_tzpz_h = ?,y.pzh = ? Where  ")
								.append(SqlUtil.buildSqlForIn("pk_bankstatement", sourcebillids));
						SQLParameter sp = new SQLParameter();
						sp.addParam(hvo.getPrimaryKey());
						sp.addParam(hvo.getPzh());
						// sp.addParam(relationVO.getSourcebillid());

						singleObjectBO.executeUpdate(sf.toString(), sp);
					}

				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP90)) {// 销项发票
					if (StringUtil.isEmptyWithTrim(relationVO.getSourcebillid())) {
						throw new BusinessException("销项发票未找到来源，请检查");
					}

					if (sourcebillids != null && sourcebillids.length > 0) {
						StringBuffer sf = new StringBuffer();
						sf.append("  update ynt_vatsaleinvoice y set y.pk_tzpz_h = ?,y.pzh = ? Where ")
								.append(SqlUtil.buildSqlForIn("pk_vatsaleinvoice", sourcebillids));
						SQLParameter sp = new SQLParameter();
						sp.addParam(hvo.getPrimaryKey());
						sp.addParam(hvo.getPzh());
						singleObjectBO.executeUpdate(sf.toString(), sp);
					}

				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP95)) {// 进项发票
					if (StringUtil.isEmptyWithTrim(relationVO.getSourcebillid())) {
						throw new BusinessException("进项发票未找到来源，请检查");
					}

					if (sourcebillids != null && sourcebillids.length > 0) {
						StringBuffer sf = new StringBuffer();
						sf.append("  update ynt_vatincominvoice y set y.pk_tzpz_h = ?,y.pzh = ? Where ");
						sf.append(SqlUtil.buildSqlForIn("pk_vatincominvoice", sourcebillids));
						SQLParameter sp = new SQLParameter();
						sp.addParam(hvo.getPrimaryKey());
						sp.addParam(hvo.getPzh());
						singleObjectBO.executeUpdate(sf.toString(), sp);
					}

				} else if (IBillTypeCode.HP135.equals(relationVO.getSourcebilltype())) {
					// 关单
					if (StringUtil.isEmptyWithTrim(relationVO.getSourcebillid())) {
						throw new BusinessException("关单未找到来源，请检查");
					}

					if (sourcebillids != null && sourcebillids.length > 0) {
						StringBuilder sf = new StringBuilder();
						sf.append("  update ynt_customsform set pk_voucher = ? Where pk_corp = ? and ");
						sf.append(SqlUtil.buildSqlForIn("pk_customsform", sourcebillids));
						SQLParameter sp = new SQLParameter();
						sp.addParam(hvo.getPrimaryKey());
						sp.addParam(hvo.getPk_corp());

						singleObjectBO.executeUpdate(sf.toString(), sp);
					}
				}
			}
		}
		// 如果有关联图片
		if (!StringUtil.isEmpty(hvo.getPk_image_group())) {
			String requestid = UUID.randomUUID().toString();
			try {
				updateImageVo(hvo.getPk_corp(), hvo.getPk_image_group(), hvo.getPk_image_library(), "2", null, hvo,
						false, hvo.getCoperatorid(),isadd);
			} catch (Exception e) {
				log.error("错误",e);
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
			}

		}
		// 启用库存
		if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			if (hvo.getIsimp() == null || !hvo.getIsimp().booleanValue()) {// 导入的数据，不生成库存单据。zpm
				//zpm还原老模式总账推库存钊宁代码
				// 启用库存的时候 流程凭证生成库存 空代表 凭证生成库存
				if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() == 0) {// 库存老模式
					if (!IBillTypeCode.HP32.equals(hvo.getSourcebilltype())) {// 非损益结转
						CreateicBill icCreate = new CreateicBill(gl_cbconstant, singleObjectBO);// 凭证生成库存数据，包括入库、出库数据。
						icCreate.saveOldIcBill(hvo);
						CreateSaleBill saCreate = new CreateSaleBill(gl_cbconstant, singleObjectBO); // 由收入凭证生成销售数据
						saCreate.saveOldicSaleBill(corpvo,hvo);
					}
				} else if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {// 新模式
					// 代码在其它地方处理。
				}
			}
		}
		// else{
		// throw new BusinessException("当前公司没有启用进销存！");不启用进销存则不生成出入库单
		// }
		dealAIInfoByAddAfter(hvo);
		return hvo;
	}
	
	/**
	 * 根据来源判断是否生成下级科目及对应的辅助(只是处理生成凭证,生成的凭证，修改保存，则不村里)
	 * @param hvo
	 * @param tzpzBVO
	 * @param cpavos
	 * @param curr_cpa
	 * @return
	 */
	private YntCpaccountVO handleUnleafPz(TzpzHVO hvo, TzpzBVO tzpzBVO, YntCpaccountVO[] cpavos,
			YntCpaccountVO curr_cpa,CorpVO cpvo) {
		//只是处理新增的，修改的不处理
		if(StringUtil.isEmpty(hvo.getPrimaryKey())){
			if (IBillTypeCode.HP67.equals(hvo.getSourcebilltype())//资产计提
					|| IBillTypeCode.HP66.equals(hvo.getSourcebilltype())//资产清理
					|| IBillTypeCode.HP34.equals(hvo.getSourcebilltype())//成本结转
					|| IBillTypeCode.HP120.equals(hvo.getSourcebilltype())//增值税结转
					|| IBillTypeCode.HP39.equals(hvo.getSourcebilltype())//附加税
					|| IBillTypeCode.HP125.equals(hvo.getSourcebilltype())//计提所得税
					|| IBillTypeCode.HP32.equals(hvo.getSourcebilltype())//损益结转
					|| IBillTypeCode.HP28.equals(hvo.getSourcebilltype())//利润结转
					) {
				String curr_code = curr_cpa.getAccountcode();
				YntCpaccountVO newcpavo = curr_cpa;
				for (YntCpaccountVO vo : cpavos) {
					if (vo.getIsleaf() != null && vo.getIsleaf().booleanValue()) {// 是否末级
						if (vo.getAccountcode().startsWith(curr_code) && !curr_code.equals(vo.getAccountcode())) {
							//修改当前凭证为暂存态(存在下级科目)
							hvo.setVbillstatus(IVoucherConstants.TEMPORARY);//暂存态
							newcpavo = vo;
							break;
						}
					}
				}
				//如果存在，则凭证改为暂存凭证，生成新的科目
				if (newcpavo != null) {
					tzpzBVO.setPk_accsubj(newcpavo.getPk_corp_account());
					//如果存在辅助，则查询对应辅助的第一项
					if(!StringUtil.isEmpty(newcpavo.getIsfzhs()) && newcpavo.getIsfzhs().indexOf("1") >= 0
							&& bnullFzid(tzpzBVO,cpvo)) {
						String fzlb = "";
						for (int i = 0; i < newcpavo.getIsfzhs().length(); i++) {
							if(newcpavo.getIsfzhs().substring(i, i + 1).equals("1")){
								fzlb = "" + (i + 1);
								//根据辅助类别+公司查找辅助项目
								AuxiliaryAccountBVO[] fzvos = gl_fzhsserv.queryAllBByLb(hvo.getPk_corp(), fzlb);
								if (fzvos != null && fzvos.length > 0){
									if("6".equals(fzlb)){//存货辅助核算
										if(IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){//启用库存时
											tzpzBVO.setPk_inventory(fzvos[0].getPk_auacount_b());
											continue;
										}
									}
									tzpzBVO.setAttributeValue("fzhsx"+fzlb, fzvos[0].getPk_auacount_b());
								}
							}
						}
						//修改当前凭证为暂存态(有辅助项目)
						hvo.setVbillstatus(IVoucherConstants.TEMPORARY);//暂存态
					}
					return newcpavo;
				}
			}
		}

		if (!curr_cpa.getIsleaf().booleanValue()){
			if (StringUtil.isEmpty(curr_cpa.getAccountcode())) {
				throw new BusinessException("非末级科目不让保存！");
			} else {
				throw new BusinessException("非末级科目[" + curr_cpa.getAccountcode() + "]不让保存！");
			}
		}
		return curr_cpa;

	}

	/**
	 * 是否空辅助项目
	 * @param tzpzBVO
	 * @return
	 */
	private boolean bnullFzid(TzpzBVO tzpzBVO,CorpVO cpvo) {
		for(int i =1;i<=10;i++){
			if(i ==6){
				if(IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){
					if(!StringUtil.isEmpty(tzpzBVO.getPk_inventory())){
						return false;
					}
				}
			}
			String fzhsid = (String) tzpzBVO.getAttributeValue("fzhsx"+i);
			if(!StringUtil.isEmpty(fzhsid)){
				return false;
			}
		}
		return true;
	}

	private void updateIcBill(CorpVO corpvo, TzpzHVO hvo) {
		// 库存新模式
		if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {
			SQLParameter sqlp = new SQLParameter();
			sqlp.addParam(hvo.getPzh());
			sqlp.addParam(hvo.getPrimaryKey());
			sqlp.addParam(corpvo.getPrimaryKey());
			singleObjectBO.executeUpdate(" update ynt_ictrade_h set pzh= ? where pzid= ? and pk_corp = ? ", sqlp);
			if (IBillTypeCode.HP70.equals(hvo.getSourcebilltype())) {// 入库单
				singleObjectBO.executeUpdate(" update ynt_ictradein set pzh= ? where pk_voucher= ? and pk_corp = ? ",
						sqlp);
			} else if (IBillTypeCode.HP75.equals(hvo.getSourcebilltype())) {// 出库单
				singleObjectBO.executeUpdate(" update ynt_ictradeout set pzh= ? where pk_voucher= ? and pk_corp = ? ",
						sqlp);
			} else if (IBillTypeCode.HP34.equals(hvo.getSourcebilltype())) {// 期末结转的完工入库单
				singleObjectBO.executeUpdate(" update ynt_ictradein set pzh= ? where pk_voucher= ? and pk_corp = ? ",
						sqlp);
			}
		}

	}
	
	/**
	 * 参与条件 
	 * 1、启用总账存货。(并且是启用明细核算和大类核算)
	 * 2、工业或者商贸结转。
	 * 3、当修改期末处理，成本结转的凭证，并且带有com.dzf.pub.IBillTypeCode.HP34结转模板。需要重新参与分摊。
	 */
	private void saveCbftJE(TzpzHVO headVO,CorpVO corpVo){
		gl_smcbftserv.saveCBFt(headVO, corpVo);
	}
	
	private void doicBillcode(TzpzHVO hvo,CorpVO corpvo,Map<String, YntCpaccountVO> map)throws BusinessException {
		TzpzBVO[] bodyvos = (TzpzBVO[])hvo.getChildren();
		if(bodyvos == null || bodyvos.length == 0)
			return;
		for(TzpzBVO v : bodyvos){
			YntCpaccountVO vo = map.get(v.getPk_accsubj());
			if (vo == null)
				continue;
			if((Kmschema.isKcspbm(corpvo.getCorptype(), vo.getAccountcode()) //库存商品
					|| Kmschema.isYclbm(corpvo.getCorptype(), vo.getAccountcode()))//原材料科目
					&& vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
					)
			{
				if(v.getVdirect() ==0){//借方//入库
					icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_IN, hvo);
					v.setVicbillcodetype(InventoryConstant.IC_STYLE_IN);
					v.setGlcgmny(v.getJfmny());
					v.setGlchhsnum(v.getNnumber());
					//这里没有break,因为合并凭证后，一个凭证上面，可能有出库，可能也有入库。
				}else if(v.getVdirect() ==1){//当前凭证除有成本类科目外，都认为是出库。该凭证有成本类科目，不认为是出库。
					if(!Kmschema.ischengbenpz(corpvo,bodyvos)){//非成本类
						//这块处理有问题。
						//两种情况。1是蓝冲。2是费用出库。（比如说买了一个包子吃了）
						//有损益类的，就认为是出库。
						//无损益类的，就认为是蓝冲。
						////////////////--------------注意这里和QmclNoicServiceImpl那里处理的不一样的。2018.12.20号 zpm
						if(Kmschema.isSunYipz(corpvo,bodyvos)){//出库,,,这个费用出库。
							icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_OUT, hvo);
							v.setVicbillcodetype(InventoryConstant.IC_STYLE_OUT);
							v.setXsjzcb(v.getDfmny());
							v.setGlchhsnum(v.getNnumber());
						}else{//非损益类 的，认为是蓝冲。乘以-1改成红冲,,,比如说成是调账。
//							icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_IN, hvo);
//							v.setVicbillcodetype(InventoryConstant.IC_STYLE_IN);
//							v.setGlcgmny(SafeCompute.multiply(v.getDfmny(), new DZFDouble(-1)));
//							v.setGlchhsnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
							
							icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_OUT, hvo);
							v.setVicbillcodetype(InventoryConstant.IC_STYLE_OUT);
							v.setXsjzcb(v.getDfmny());
							v.setGlchhsnum(v.getNnumber());
						}
						
					}else if(Kmschema.ischengbenpz(corpvo,bodyvos) 
							&& (v.getNnumber()==null || v.getNnumber().doubleValue() == 0)){//成本类
						//zpm 增加，，2018.11.26，，生成的成本调整的单子，数量为空。此种情况单独考虑
						v.setVicbillcodetype(InventoryConstant.IC_STYLE_OUT);
						v.setXsjzcb(v.getDfmny());
						v.setGlchhsnum(null);
					}
				}
			}else if(Kmschema.isshouru(corpvo.getCorptype(), vo.getAccountcode())//收入类科目
					&& vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
					)//贷方
			{//成本结转金额 需要回写
				if(!Kmschema.isbennianlirunpz(corpvo,bodyvos)){//过滤掉本年利润的凭证
					if(v.getVdirect() ==1){//贷方//出库
						icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_OUT, hvo);
						v.setVicbillcodetype(InventoryConstant.IC_STYLE_OUT);
						v.setGlchhsnum(v.getNnumber());
					}else if(v.getVdirect() ==0){//借方//这个要当作负的出库
						icbillcode_create.setICbillcode(hvo.getPk_corp(), hvo.getPeriod(), InventoryConstant.IC_STYLE_OUT, hvo);
						v.setVicbillcodetype(InventoryConstant.IC_STYLE_OUT);
						v.setGlchhsnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
					}
				}
			}
		}
	}

	private YntCpaccountVO queryAccountByid(String pkid) {
		return (YntCpaccountVO) singleObjectBO.queryVOByID(pkid, YntCpaccountVO.class);
	}

	private void dealAIInfoByAddBefore(TzpzHVO hvo){
		iZncsVoucher.saveVoucherBefore(hvo);//来源职能财税凭证_其他处理
	}
	private void dealAIInfoByDelBefore(TzpzHVO hvo){
		iZncsVoucher.deleteVoucherBefore(hvo);//来源职能财税凭证_其他处理
	}
	private void dealAIInfoByAddAfter(TzpzHVO hvo){
		iZncsVoucher.saveVoucherAfter(hvo);//来源职能财税凭证_其他处理
	}
	private void dealAIInfoByDelAfter(TzpzHVO hvo){
		iZncsVoucher.deleteVoucherAfter(hvo);//来源职能财税凭证_其他处理
	}
	@Override
	public TzpzHVO deleteVoucher(TzpzHVO headVO) {

		Map<String, YntCpaccountVO> cpamap = accountService.queryMapByPk(headVO.getPk_corp());
		checkBeforeDelete(headVO, cpamap);
		dealAIInfoByDelBefore(headVO);
		TzpzBVO[] bodyvos = (TzpzBVO[]) headVO.getChildren();
		if (bodyvos != null && bodyvos.length != 0) {
			Map<String, YntCpaccountVO> kmmap = new HashMap<String, YntCpaccountVO>();

			for (int i = 0; i < bodyvos.length; i++) {
				String pk_accsubj = bodyvos[i].getPk_accsubj();
				if (!StringUtil.isEmptyWithTrim(pk_accsubj)) {
					YntCpaccountVO kmvo = cpamap.get(pk_accsubj);
					if (kmvo != null) {
						kmmap.put(pk_accsubj, kmvo);
						Map<String, YntCpaccountVO> kmparentmap = getKmParent(cpamap, pk_accsubj);
						kmmap.putAll(kmparentmap);
					}
				}
			}
			checkyefxkz(kmmap, bodyvos, headVO.getPk_corp(), "delete");
		}

		// 删除现金流量
		XjllVO[] xjllList = queryCashFlow(headVO.getPk_tzpz_h(), headVO.getPk_corp());
		if (xjllList != null && xjllList.length > 0) {
			for (XjllVO xjll : xjllList) {
				singleObjectBO.deleteObject(xjll);
			}
		}
		// 删除税表表项
		if (headVO.getIs_tax_analyse() != null && headVO.getIs_tax_analyse()) {
			deleteVoucherTaxItemAlone(headVO.getPk_tzpz_h(), headVO.getPk_corp());
		}

		// 删除税目
		SQLParameter param1 = new SQLParameter();
		param1.addParam(headVO.getPk_corp());
		param1.addParam(headVO.getPk_tzpz_h());
		singleObjectBO.executeUpdate(" delete from ynt_pztaxitem where pk_corp = ? and pk_tzpz_h = ? ", param1);

		// 如果凭证有图片，删除凭证时，退回图片
		if (headVO.getPk_image_group() != null && headVO.getPk_image_group().trim().length() > 0) {
			ImageTurnMsgVO imageTurnMsgVO = new ImageTurnMsgVO();
			imageTurnMsgVO.setCoperatorid(headVO.getCoperatorid());
			imageTurnMsgVO.setDoperatedate(new DZFDate(new Date()));
			imageTurnMsgVO.setMessage("凭证删除，图片变更为未处理");
			imageTurnMsgVO.setPk_corp(headVO.getPk_corp());
			// imageTurnMsgVO.setPk_image_returnmsg("凭证删除，图片退回");
			imageTurnMsgVO.setPk_image_group(headVO.getPk_image_group());
			imageTurnMsgVO.setPk_image_librarys(headVO.getPk_image_library());

			DZFBoolean issvbk = headVO.getIssvbk();
			String ident = "3";// 未处理
			if (issvbk != null && DZFBoolean.TRUE.equals(issvbk)) {
				ident = "1";// 退回
			}
			
			updateImageVo(headVO.getPk_corp(), headVO.getPk_image_group(), headVO.getPk_image_library(), ident,
					imageTurnMsgVO, headVO, true, headVO.getCoperatorid(),false);
//			gl_pzimageserv.processSplitGroup(headVO.getPk_corp(), headVO.getPk_image_group());
			clearPzInfoByBank(headVO);
			clearPzInfoByVatIncom(headVO);
			clearPzInfoByVatSale(headVO);
			// 拆分图片组
			if (!"Y".equals(headVO.getIsMerge())) {
				gl_pzimageserv.processSplitMergedGroup(headVO.getPk_corp(), headVO.getPk_image_group());
			}
		}

		// 调用会计工厂接口，使用范围：针对有图片的凭证 begin
//		try {
//			if ("factory".equals(headVO.getSourcebilltype())) {// 会计工厂来的凭证，删除会计工厂端对应的凭证
//				IFctPzService iFctPzService = (IFctPzService) SpringUtils.getBean("fctPzService");
//				iFctPzService.delFctPz(headVO);
//			}
//		} catch (DZFWarpException e) {
//			log.error("调用会计工厂接口报错", e);// 调用会计工厂接口，如有异常，此处不向上层抛出
//		}
		// end

		PzSourceRelationVO[] relations = headVO.getSource_relation();
		if (relations == null) {
			relations = getSourceRelations(headVO.getPk_tzpz_h(), headVO.getPk_corp());
		}
		if (!StringUtil.isEmpty(headVO.getSourcebilltype())) {
			List<PzSourceRelationVO> list = new ArrayList<PzSourceRelationVO>();
			if (relations != null && relations.length > 0) {
				list.addAll(Arrays.asList(relations));
			}
			PzSourceRelationVO relationVO = new PzSourceRelationVO();
			relationVO.setSourcebilltype(headVO.getSourcebilltype());
			relationVO.setSourcebillid(headVO.getSourcebillid());
			list.add(relationVO);
			relations = list.toArray(new PzSourceRelationVO[0]);
		}
		if (relations != null && relations.length > 0) {
			for (PzSourceRelationVO relationVO : relations) {
				if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP59)) { // 来源于固定资产
					IAssetCard assetCard = (IAssetCard) SpringUtils.getBean("assetCardImpl");
					assetCard.updateToGLState(relationVO.getSourcebillid(), false, headVO.getPrimaryKey(), 1);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP60)) { // 来源于资产原值变更单
					IYzbgService am_yzbgserv = (IYzbgService) SpringUtils.getBean("am_yzbgserv");
					am_yzbgserv.updateAVToGLState(relationVO.getSourcebillid(), false, "");
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP61)) { // 来源于资产清理单
					IAssetCleanService cleanService = (IAssetCleanService) SpringUtils.getBean("am_assetclsserv");
					cleanService.updateACToGLState(relationVO.getSourcebillid(), false, "");
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP66)) { // 来源于资产清理（清理折旧）
					IAssetCard assetCard = (IAssetCard) SpringUtils.getBean("assetCardImpl");
					assetCard.updateDepToGLState(relationVO.getSourcebillid(), false, headVO.getPrimaryKey());
					SQLParameter sqlp = new SQLParameter();
					sqlp.addParam(headVO.getPk_corp());
					sqlp.addParam(headVO.getDoperatedate().toString().substring(0, 7));
					QmclVO[] vo = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class,
							"pk_corp= ? and nvl(dr,0)=0 and period = ? ", sqlp);
					if (vo != null && vo.length > 0) {
						vo[0].setIszjjt(null);
						singleObjectBO.update(vo[0], new String[]{ "iszjjt" });
					}
					//删除折旧明细
					IZczjmxReport am_rep_zczjmxserv = (IZczjmxReport) SpringUtils.getBean("am_rep_zczjmxserv");
					am_rep_zczjmxserv.deleteZjmx(relationVO.getSourcebillid(),headVO.getPrimaryKey());
				}else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP67)) { // 来源于资产折旧,多个资产明细生成一张凭证
					SQLParameter sqlp = new SQLParameter();
					sqlp.addParam(headVO.getPk_corp());
					sqlp.addParam(headVO.getDoperatedate().toString().substring(0, 7));
					QmclVO[] vos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class,
							"pk_corp= ? and nvl(dr,0)=0 and period = ? ", sqlp);
					gl_qmclserv.updateFanJiTiZheJiu(vos[0]);

				}else if (relationVO.getSourcebilltype().equals("HP26")) { // 来源于预凭证
					/**
					 * 在线会计中凭证管理删除单据（来源于数据中心预凭证）时，
					 * 删除预凭证信息，该操作在图片更改操作时已做，如有后续操作可在此处增加逻辑
					 */
					// getImgIntf().updatePreVoucherGLState(headVO.getSourcebillid(),
					// true, "");
				} else if (relationVO.getSourcebilltype().equals("HP34")) {// 成本---
					// 来源成本结转，删除凭证时，调用取消成本结转
					rollbackCbjz(headVO);

				} else if (relationVO.getSourcebilltype().equals("HP32")) {
					// 来源损益结转,删除凭证时，反写去掉是否损益结转的勾选项
					String sourceid = relationVO.getSourcebillid();
					QmclVO vo = (QmclVO) singleObjectBO.queryByPrimaryKey(QmclVO.class, sourceid);
					if (vo != null) {
						vo.setIsqjsyjz(null);
						singleObjectBO.update(vo, new String[]{ "isqjsyjz" });
					}

				} else if (relationVO.getSourcebilltype().equals("HP23")) {// 切图工作台
					// String pk_image_group = relationVO.getSourcebillid();
					// IMAGEGROUPVO groupbvo = (IMAGEGROUPVO)
					// singleObjectBO.queryByPrimaryKey(IMAGEGROUPVO.class,
					// pk_image_group);
					// if(groupbvo!=null){
					// groupbvo.setDr(1);
					// singleObjectBO.update(groupbvo) ;
					// }
				} else if (relationVO.getSourcebilltype().equals("HCH10535")) {
					// 来源汇兑损益结转,删除凭证时，反写去掉是否汇兑损益结转的勾选项
					String sourceid = relationVO.getSourcebillid();
					QmclVO vo = (QmclVO) singleObjectBO.queryByPrimaryKey(QmclVO.class, sourceid);
					if (vo != null) {
						vo.setIshdsytz(DZFBoolean.FALSE);
						singleObjectBO.update(vo, new String[]{ "ishdsytz" });
					}
				} else if (relationVO.getSourcebilltype().equals("HP50")) {
					IExpBillService gl_bxdserv = (IExpBillService) SpringUtils.getBean("gl_bxdserv");
					gl_bxdserv.updateFromVch(headVO);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP39)) {// 计提税金，反写去掉勾选项
					String sourceid = relationVO.getSourcebillid();
					QmclVO vo = (QmclVO) singleObjectBO.queryByPrimaryKey(QmclVO.class, sourceid);
					if (vo != null) {
						vo.setIsjtsj(DZFBoolean.FALSE);
						singleObjectBO.update(vo, new String[]{ "isjtsj" });
						gl_taxarchive.updateSurtaxUnCarryover(headVO.getPk_corp(), headVO.getPeriod());
					}
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP70)) {// 采购单
					IPurchInService ic_purchinserv = (IPurchInService) SpringUtils.getBean("ic_purchinserv");
					ic_purchinserv.deleteIntradeBill(headVO);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP75)) {// 库存销售单
					ISaleoutService ic_saleoutserv = (ISaleoutService) SpringUtils.getBean("ic_saleoutserv");
					ic_saleoutserv.deleteIntradeoutBill(headVO);
				} else if (relationVO.getSourcebilltype().equals("SFFK")) {// 收费审核生成付款凭证
					String sql = "update ynt_charge set vpaypzid=null,vpaypzh=null,ispaypz='N' where pk_charge = ?";
					SQLParameter param = new SQLParameter();
					param.addParam(relationVO.getSourcebillid());
					singleObjectBO.executeUpdate(sql, param);
				} else if (relationVO.getSourcebilltype().equals("SFSK")) {// 收费审核生成收款凭证
					String sql = "update ynt_charge set vreceivepzid=null,vreceivepzh=null,isreceivepz='N' where pk_charge = ?";
					SQLParameter param = new SQLParameter();
					param.addParam(relationVO.getSourcebillid());
					singleObjectBO.executeUpdate(sql, param);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP85)) {// 银行对账单
					clearPzInfoByBank(headVO);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP90)) {// 销项发票
					clearPzInfoByVatSale(headVO);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP95)) {// 进项发票
					clearPzInfoByVatIncom(headVO);
				} else if (relationVO.getSourcebilltype().equals(IBillTypeCode.HP141)) {
					// 税费计算
					gl_taxarchive.updateOtherTaxOnVoucherDelete(relationVO.getSourcebillid());
				} else if (IBillTypeCode.HP120.equals(relationVO.getSourcebilltype())// 增值税结转
						|| IBillTypeCode.HP125.equals(relationVO.getSourcebilltype())) {// 企业所得税结转
					String sourceid = relationVO.getSourcebillid();
					QmclVO vo = (QmclVO) singleObjectBO.queryByPrimaryKey(QmclVO.class, sourceid);
					if (vo != null) {
						if (IBillTypeCode.HP120.equals(relationVO.getSourcebilltype())) {
							vo.setZzsjz(DZFBoolean.FALSE);
							singleObjectBO.update(vo, new String[] { "zzsjz" });
							gl_taxarchive.updateAddTaxUnCarryover(headVO.getPk_corp(), headVO.getPeriod());
						}
						if (IBillTypeCode.HP125.equals(relationVO.getSourcebilltype())) {
							vo.setQysdsjz(DZFBoolean.FALSE);
							singleObjectBO.update(vo, new String[] { "qysdsjz" });
							gl_taxarchive.updateIncomeTaxUnCarryover(headVO.getPk_corp(), headVO.getPeriod());
						}
					}
				} else if (IBillTypeCode.HP135.equals(relationVO.getSourcebilltype())) {
					String sql = "update ynt_customsform set pk_voucher = null where pk_corp = ? and pk_voucher = ? ";
					SQLParameter sp = new SQLParameter();
					sp.addParam(headVO.getPk_corp());
					sp.addParam(headVO.getPrimaryKey());
					singleObjectBO.executeUpdate(sql, sp);
				}else if(IBillTypeCode.HP28.equals(relationVO.getSourcebilltype())){
					if(!StringUtil.isEmpty(relationVO.getSourcebillid())){
						QmJzVO qmjzvo = (QmJzVO) singleObjectBO.queryByPrimaryKey(QmJzVO.class, relationVO.getSourcebillid());
						if(qmjzvo!=null){
							qmjzvo.setVdef10("0");
							singleObjectBO.update(qmjzvo, new String[]{"vdef10"});
						}
					}
				}
			}
			// 删除来源表
			SQLParameter rlParam = new SQLParameter();
			rlParam.addParam(headVO.getPk_corp());
			rlParam.addParam(headVO.getPk_tzpz_h());
			singleObjectBO.executeUpdate(" delete from ynt_pz_sourcerelation where pk_corp = ? and pk_tzpz_h = ? ",
					rlParam);
		}
		CircularlyAccessibleValueObject[] bvos = headVO.getChildren();
		List<String> list = new ArrayList<String>();
		StringBuffer wherepart = new StringBuffer();
		if (bvos != null && bvos.length > 0) {// 删除子表
			for (CircularlyAccessibleValueObject bvo : bvos) {

				TzpzBVO tzpzbvo = (TzpzBVO) bvo;
				list.add(tzpzbvo.getPk_tzpz_b());
				// wherepart.append("'" + tzpzbvo.getPk_tzpz_b() + "',");
			}

			if (wherepart.length() > 0) {
				String sql = SQLHelper.getInSQL(list);
				SQLParameter sp_xjll = SQLHelper.getSQLParameter(list);
				sql = "update YNT_XJLL set dr=1 where pk_tzpz_b in" + sql + " and pk_corp = ?";
				sp_xjll.addParam(headVO.getPk_corp());
				singleObjectBO.executeUpdate(sql,sp_xjll);
			}
		}
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, headVO.getPk_corp());
		// if (corpvo == null || corpvo.getBbuildic() == null
		// || !corpvo.getBbuildic().booleanValue()) {
		// // throw new BusinessException("当前公司没有启用进销存！");不启用进销存则不删除出入库单
		// } else {
		// if (headVO.getIsimp() == null || !headVO.getIsimp().booleanValue())
		// {// 导入的时候，不需要重新设置凭证号zpm
		//
		// // 启用库存的时候 流程凭证生成库存 空代表 凭证生成库存
		// if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() !=
		// 1) {
		// CreateicBill icCreate = new CreateicBill(gl_cbconstant,
		// singleObjectBO);// 凭证生成库存数据icCreate.deleteBill(headVO);
		// icCreate.deleteBill(headVO);
		// CreateSaleBill saCreate = new CreateSaleBill(gl_cbconstant,
		// singleObjectBO); // 凭证生成销售数据
		// saCreate.deleteBill(headVO);
		// }
		//
		//
		// }
		// }
		// 启用库存
		if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			if (headVO.getIsimp() == null || !headVO.getIsimp().booleanValue()) {// 导入的数据，不删除库存单据。zpm
				// 启用库存的时候 流程凭证生成库存 空代表 凭证生成库存
				if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() == 0) {// 库存老模式
					CreateicBill icCreate = new CreateicBill(gl_cbconstant, singleObjectBO);// 凭证生成库存数据icCreate.deleteBill(headVO);
					icCreate.deleteBill(headVO);
					CreateSaleBill saCreate = new CreateSaleBill(gl_cbconstant, singleObjectBO); // 凭证生成销售数据
					saCreate.deleteBill(headVO);
				} else if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {// 新模式
					// 代码在其它地方处理。
				}
			}
		}
		// else{
		// throw new BusinessException("当前公司没有启用进销存！");不启用进销存则不删除出入库单
		// }

		updateZzsmx(headVO);

		singleObjectBO.deleteVOArray((SuperVO[]) bvos);
		headVO.setChildren(null);
		singleObjectBO.deleteObject(headVO);
		dealAIInfoByDelAfter(headVO);
		return headVO;

	}
	
	
	private void clearPzInfoByVatSale(TzpzHVO headVO) {
		
		String sql = "update ynt_vatsaleinvoice y set y.pk_tzpz_h = null,y.pzh = null,y.vicbillno =null Where y.pk_tzpz_h = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		// sp.addParam(headVO.getSourcebillid());
		sp.addParam(headVO.getPrimaryKey());
		sp.addParam(headVO.getPk_corp());
		int row	=singleObjectBO.executeUpdate(sql, sp);
//		if(row>0){
//			sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
//			sp = new SQLParameter();
//			sp.addParam(PhotoState.state1);
//			sp.addParam(headVO.getPk_image_group());
//			singleObjectBO.executeUpdate(sql, sp);
//		}
	}

	private void clearPzInfoByVatIncom(TzpzHVO headVO) {

		String sql = "update ynt_vatincominvoice y set y.pk_tzpz_h = null,y.pzh = null,y.vicbillno =null Where y.pk_tzpz_h = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		// sp.addParam(headVO.getSourcebillid());
		sp.addParam(headVO.getPrimaryKey());
		sp.addParam(headVO.getPk_corp());
		int row	=singleObjectBO.executeUpdate(sql, sp);
//		if(row>0){
//			sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
//			sp = new SQLParameter();
//			sp.addParam(PhotoState.state1);
//			sp.addParam(headVO.getPk_image_group());
//			singleObjectBO.executeUpdate(sql, sp);
//		}
	}
	
	private void clearPzInfoByBank(TzpzHVO headVO) {
		
		String sql = "update ynt_bankstatement y set y.pk_tzpz_h = null,y.pzh = null Where y.pk_tzpz_h = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(headVO.getPrimaryKey());
		sp.addParam(headVO.getPk_corp());
		int row	=singleObjectBO.executeUpdate(sql, sp);
//		if(row>0){
//			sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
//			sp = new SQLParameter();
//			sp.addParam(PhotoState.state1);
//			sp.addParam(headVO.getPk_image_group());
//			singleObjectBO.executeUpdate(sql, sp);
//		}
	}

	
	private void rollbackCbjz(TzpzHVO headVO) {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, headVO.getPk_corp());
		if(corpvo == null){
			throw new BusinessException("公司信息不存在");
		}
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(headVO.getPk_corp());
		sqlp.addParam(headVO.getSourcebillid());
		QmclVO[] vos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class,
				"pk_corp= ? and nvl(dr,0)=0 and pk_qmcl = ? ", sqlp);
		if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			CancelCbjz cbjz = new CancelCbjz(singleObjectBO);
			cbjz.rollbackCbjz(vos);
		} else {
			// 不启用库存
			Integer style = corpvo.getIcostforwardstyle() == null ? 0 : corpvo.getIcostforwardstyle();
			if (IQmclConstant.z3 == style) {// 如果是工业结转
				CancelCbjz cbjz = new CancelCbjz(singleObjectBO);
				String cbjzcount = headVO.getCbjzCount();
				cbjz.rollbackCbjzNoic(vos, cbjzcount);
			} else {
				CancelCbjz cbjz = new CancelCbjz(singleObjectBO);
				cbjz.rollbackCbjz(vos);
			}
		}
	}

	private void checkBeforeDelete(TzpzHVO headVO, Map<String, YntCpaccountVO> accountMap) {

		checkIsJz(headVO.getPk_corp(), headVO.getPeriod().substring(0, 4) + "-12");

		boolean isgz = qmgzService.isGz(headVO.getPk_corp(), headVO.getDoperatedate().toString());
		if (isgz) {// 是否关账
			throw new BusinessException(headVO.getPeriod() + "期间已经关账，不能删除凭证！");
		}

//		if (IBillTypeCode.HP67.equals(headVO.getSourcebilltype())) { // 来源于资产折旧(合并的计提)
//			throw new BusinessException("计提折旧生成的凭证不能删除!");
//		}

		if (IBillTypeCode.HP80.equals(headVO.getSourcebilltype())) {
			throw new BusinessException("电子发票生成的凭证不能删除!");
		}

		checkIcIntrade(headVO.getPk_corp(), headVO.getPk_tzpz_h());

		if (headVO.getIsqxsy() == null || !headVO.getIsqxsy().booleanValue()) {

			boolean hasProfitAndLoss = false;
			// 已经经过期间损益的月份删除凭证时，如果凭证不涉及损益类科目的话，不给提示：当期已经损益结转
			TzpzBVO[] bvos = (TzpzBVO[]) headVO.getChildren();
			for (TzpzBVO tzpzBVO : bvos) {
				YntCpaccountVO account = accountMap.get(tzpzBVO.getPk_accsubj());
				if (account != null) {
					if (account.getAccountkind() == 5) {
						hasProfitAndLoss = true;
						break;
					}
				}
			}

			if (hasProfitAndLoss) {
				StringBuilder sbCode = new StringBuilder(
						"select count(1) from  ynt_qmcl where pk_corp=? and period=?  and nvl(isqjsyjz,'N')='Y'");
				SQLParameter sp = new SQLParameter();
				sp.addParam(headVO.getPk_corp());
				sp.addParam(headVO.getPeriod());
				BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp,
						new ColumnProcessor());

				if (repeatCodeNum.intValue() > 0) {
					String errCodeStr = "当月已结转损益，不能操作！";
					throw new BusinessException(errCodeStr);
				}
			}

		}

		if (DZFBoolean.TRUE.equals(headVO.getIshasjz())) {
			throw new BusinessException("已记账凭证不能删除!");
		}

		if (headVO.getVbillstatus() != IVoucherConstants.FREE
				&& headVO.getVbillstatus() != IVoucherConstants.TEMPORARY) {
			throw new BusinessException("已审核凭证不能删除!");
		}
		YntParameterSet setvo = sys_parameteract.queryParamterbyCode(headVO.getPk_corp(), "dzf003");
		if (setvo != null && setvo.getPardetailvalue() == 0 && headVO.getBsign() != null
				&& headVO.getBsign().booleanValue()) {
			throw new BusinessException("已签字不能删除!");
		}
	}

	/****
	 * 更新增值税明细
	 * 
	 * @param headVO
	 */
	@SuppressWarnings("unchecked")
	private void updateZzsmx(TzpzHVO headVO) {

		if (headVO == null || StringUtil.isEmpty(headVO.getPk_tzpz_h())) {
			return;
		}
		String condition = " nvl(dr,0)=0 and pk_tzpz_h=?";
		SQLParameter params = new SQLParameter();
		params.addParam(headVO.getPk_tzpz_h());
		List<DzfpscReqBVO> vos = (List<DzfpscReqBVO>) singleObjectBO.retrieveByClause(DzfpscReqBVO.class, condition,
				params);
		if (vos != null && vos.size() > 0) {
			for (DzfpscReqBVO vo : vos) {
				vo.setPk_tzpz_h(null);
				vo.setPzzh(null);
				vo.setSummary(null);
			}
			singleObjectBO.updateAry(vos.toArray(new DzfpscReqBVO[0]), new String[] { "pk_tzpz_h", "pzzh", "summary" });
		}

	}

	/**
	 * 删除现金流量
	 */
	private void updateXjll(TzpzHVO hvo) {
		SQLParameter xjllSp = new SQLParameter();
		xjllSp.addParam(hvo.getPk_tzpz_h());
		xjllSp.addParam(hvo.getPk_corp());
		String xjllSql = "update YNT_XJLL set dr=1 where pk_tzpz_h = ?  and pk_corp = ?";
		singleObjectBO.executeUpdate(xjllSql, xjllSp);
		hvo.setIsfpxjxm(new DZFBoolean(false));
		// // 删除现金流量的数据
		// CircularlyAccessibleValueObject[] bvos = hvo.getChildren().clone();
		//
		// Map<String, TzpzBVO> pzmp = new HashMap<String, TzpzBVO>();
		// List<String> list = new ArrayList<String>();
		// // StringBuffer wherepart = new StringBuffer();
		// // 现金流量
		// String pk_head = hvo.getPk_tzpz_h();
		// Vector<YntCpaccountVO> vec = new Vector<YntCpaccountVO>();
		// if (pk_head != null && pk_head.trim().length() > 0) {
		// TzpzBVO[] tzpzbvos = (TzpzBVO[]) hvo.getChildren();
		// Map<String,YntCpaccountVO>
		// mp=com.dzf.pub.cache.AccountCache.getInstance().getMap(null,hvo.getPk_corp());
		// for (TzpzBVO bvo : tzpzbvos) {
		// YntCpaccountVO cpavo =mp.get(bvo.getPk_accsubj());// (YntCpaccountVO)
		// singleObjectBO
		// // .queryVOByID(bvo.getPk_accsubj(), YntCpaccountVO.class);
		// if (cpavo != null) {
		// if (cpavo.getAccountcode().startsWith("1001")
		// || cpavo.getAccountcode().startsWith("1002")
		// || cpavo.getAccountcode().startsWith("1012")) {
		// vec.add(cpavo);
		// }
		// }
		// }
		// if (tzpzbvos.length == vec.size()) {
		// for (TzpzBVO bvo : tzpzbvos) {
		// list.add(bvo.getPk_tzpz_b());
		// // wherepart.append("'" + bvo.getPk_tzpz_b() + "',");
		// }
		// }
		// }
		//
		// // 如果这个科目更改了，也要删除的
		// for (CircularlyAccessibleValueObject bvo : bvos) {
		// TzpzBVO tzpzbvo = (TzpzBVO) bvo;
		// String pk_tzpz_b = tzpzbvo.getPk_tzpz_b();
		// if (tzpzbvo.getStatus() == 1) {// VOStatus.DELETED
		// list.add(tzpzbvo.getPk_tzpz_b());
		// // wherepart.append("'" + tzpzbvo.getPk_tzpz_b() + "',");
		// } else {
		// if (pk_tzpz_b != null && pk_tzpz_b.trim().length() > 0) {
		// String pk_accsubj = tzpzbvo.getPk_accsubj();
		// TzpzBVO tzpzbvocom = pzmp.get(tzpzbvo.getPk_tzpz_b());
		// if (tzpzbvocom != null) {
		// String pk_accsubjcom = tzpzbvocom.getPk_accsubj();
		// if (!pk_accsubjcom.equals(pk_accsubj)) {
		// list.add(tzpzbvo.getPk_tzpz_b());
		// // wherepart.append("'" + tzpzbvo.getPk_tzpz_b()+
		// // "',");
		// }
		// }
		// }
		// }
		// }
		//
		// if (list.size() > 0) {
		// String sql = SQLHelper.getInSQL(list);
		// sql = "update YNT_XJLL set dr=1 where pk_tzpz_b in" + sql;
		// singleObjectBO.executeUpdate(sql, SQLHelper.getSQLParameter(list));
		// }
	}

	/**
	 * 保存前校验
	 * 
	 * @param hvo
	 */
	private void checkBeforeSave(CorpVO corpvo, TzpzHVO hvo, Map<String, YntCpaccountVO> accountMap) {

		// String doperatedate = hvo.getDoperatedate() == null ? null : hvo
		// .getDoperatedate().toString();
		// if (StringUtil.isEmptyWithTrim(doperatedate)) {
		// throw new BusinessException("制单日期不能为空");
		// }
		// hvo.setVoucherstatus(8);
		// FieldValidateUtils.Validate(hvo);
		/**
		 * 已经年结不能反操作
		 */
		checkIsJz(hvo.getPk_corp(), hvo.getPeriod().substring(0, 4) + "-12");

		boolean isgz = qmgzService.isGz(hvo.getPk_corp(), hvo.getDoperatedate().toString());
		if (isgz) {// 是否关账
			throw new BusinessException("制单日期所在的月份已关账，不能增加或修改凭证！");
		}

		/**
		 * 月末结转损益不能反操作
		 */
		if (hvo.getIsqxsy() == null || !hvo.getIsqxsy().booleanValue()) {
			boolean hasProfitAndLoss = false;
			// 已经经过期间损益的月份修改或新增凭证时，如果凭证不涉及损益类科目的话，不给提示：当期已经损益结转
			TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
			for (TzpzBVO tzpzBVO : bvos) {
				YntCpaccountVO account = accountMap.get(tzpzBVO.getPk_accsubj());
				if(account != null){
					if (account.getAccountkind() == 5) {
						hasProfitAndLoss = true;
						break;
					}
				}
			}

			if (hasProfitAndLoss&&!"Y".equals(hvo.getPassSyjz())) {
				StringBuilder sbCode = new StringBuilder(
						"select count(1) from  ynt_qmcl where pk_corp=? and period=?  and nvl(isqjsyjz,'N')='Y'");
				SQLParameter sp2 = new SQLParameter();
				sp2.addParam(hvo.getPk_corp());
				sp2.addParam(hvo.getPeriod());
				BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp2,
						new ColumnProcessor());

				if (repeatCodeNum.intValue() > 0) {
					String errCodeStr = "当月已结转损益，不能操作！";
					throw new BusinessException(errCodeStr);
				}
			}
		}
		if ("Y".equals(hvo.getIsMerge())) {
			// 合并凭证
			hvo.setBsign(null);
			hvo.setVcashid(null);
			hvo.setDcashdate(null);
		} else {
			YntParameterSet setvo = sys_parameteract.queryParamterbyCode(hvo.getPk_corp(), "dzf003");
			if (setvo != null && setvo.getPardetailvalue() == 0 && hvo.getBsign() != null
					&& hvo.getBsign().booleanValue()) {
				throw new BusinessException("已签字不能修改!");
			}
		}
		TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
		// // 之前有现金流量，本次修改科目了，不存在现金流量项目了
		// if(hvo.getIsfpxjxm() != null && hvo.getIsfpxjxm().booleanValue()){
		// int pz_xjll_num = 0;
		// if(bodyvos != null && bodyvos.length > 0){
		// for(TzpzBVO vo :bodyvos){
		// String kmcode =vo.getSubj_code() == null ? vo.getVcode()
		// :vo.getSubj_code();
		// if(kmcode != null &&
		// IGlobalConstants.xjll_km.get(DZfcommonTools.getFirstCode(kmcode,DZFConstant.ACCOUNTCODERULE))
		// != null){
		// pz_xjll_num++;
		// }
		// }
		// }else{
		// throw new BusinessException("凭证无分录信息!");
		// }
		//
		// if(pz_xjll_num == 0){
		// throw new BusinessException("请删除现金流量分析！");
		// }
		//
		// if(pz_xjll_num == bodyvos.length){
		// throw new BusinessException("凭证分录全部为现金类科目，请删除现金流量分析!");
		// }
		// }
//		CorpVO fatherCorp = CorpCache.getInstance().get(null, corpvo.getFathercorp());
//		// 是否为加盟商
//		boolean isChannel = fatherCorp != null
//				&& fatherCorp.getIschannel() != null && fatherCorp.getIschannel().booleanValue();
		// 摘要过长时是否截取
		boolean isCutZy = "Y".equals(hvo.getIscutzy());
		// 金额不能大于10亿
		for (int i = 0; i < bodyvos.length; i++) {
			TzpzBVO bvo = bodyvos[i];
			bvo.setDfmny(bvo.getDfmny() == null
					? DZFDouble.ZERO_DBL : bvo.getDfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
			bvo.setJfmny(bvo.getJfmny() == null ? DZFDouble.ZERO_DBL
					: bvo.getJfmny().setScale(2, DZFDouble.ROUND_HALF_UP));

			if (bvo.getDfmny().doubleValue() == 0) {
				bvo.setVdirect(0);
			} else {
				bvo.setVdirect(1);
			}
			if (bvo.getDfmny().compareTo(new DZFDouble(1e9)) >= 0
					|| bvo.getJfmny().compareTo(new DZFDouble(1e9)) >= 0) {
				throw new BusinessException("只能输入10亿以下的金额！");
			}
			if (bvo.getZy() != null && bvo.getZy().length() > 200) {
				if (isCutZy) {
					bvo.setZy(bvo.getZy().substring(0, 200));
				} else {
					throw new BusinessException("摘要超过上限（200字符）");
				}
			}
			////zpm后台不做此强制性校验  2018.12.20
//			YntCpaccountVO account = accountMap.get(bvo.getPk_accsubj());
			//数量是否可空
//			if(hvo.getIsNumNull() == null || !hvo.getIsNumNull().booleanValue()){
//				if (account != null
//						&& account.getIsnum() != null && account.getIsnum().booleanValue()
//						&& (isChannel || account.getAllow_empty_num() == null
//							|| !account.getAllow_empty_num().booleanValue())
//						&& (bvo.getNnumber() == null || bvo.getNnumber().doubleValue() == 0)) {
//					throw new BusinessException("科目" + account.getAccountcode() + "启用数量核算，数量不能为空");
//				}
//			}
		}

		if (hvo.getMemo() != null && hvo.getMemo().length() > 200) {
			hvo.setMemo(hvo.getMemo().substring(0, 200));
		}
		checkAssistExist(corpvo, hvo);
		
		// TODO 需要查询科目去对比，到底是不是商品或外币类科目
//		String error = isSaveInvName(bodyvos);
//		if (error != null && error.length() > 0) {
//			throw new BusinessException("商品类科目,请录入商品、单价、数量!");
//		}
		String error = isSaveCurName(bodyvos);
		if (error != null && error.length() > 0) {
			throw new BusinessException("外币类科目,请录入汇率!");
		}

		// 操作日期在建账日期前也不能修改
		DZFDate begindate = corpvo.getBegindate();
		if (hvo.getDoperatedate().before(begindate)) {
			throw new BusinessException("录入日期在建账日期(" + begindate.toString() + ")前，不能保存!");
		}

		Vector<Integer> vec = new Vector<Integer>();

		Vector<Integer> vec_subj = new Vector<Integer>();
		checkInvNull(bodyvos, corpvo);

		// for (int i = 0; i < bodyvos.length; i++) {
		//
		// if (bodyvos[i].getSubj_code() == null) {
		// vec.add(i);
		// } else {
		// vec_subj.add(i);
		// }
		// }
		// if (vec_subj.isEmpty()) {
		// throw new BusinessException("表体科目不能为空");
		// }

		// 判断制单日期，不能在建账日期之前
		if (hvo.getDoperatedate() != null) {
			if (corpvo.getBegindate() == null) {
				throw new BusinessException("当前公司建账日期为空，可能尚未建账，请检查");
			}
			if (hvo.getDoperatedate().before(corpvo.getBegindate())) {
				throw new BusinessException("制单日期不能在公司建账日期之前，请检查");
			}

		} else {
			throw new BusinessException("制单日期不能为空");
		}

		// 修改凭证状态为正常态
		// hvo.setVoucherstatus(Integer.valueOf(1));
		// OCR不重新更新状态 修改保存时 前台会把状态修改给为 自由态
		if (!StringUtil.isEmpty(hvo.getSourcebilltype())
				&& (IBillTypeCode.HP110.equals(hvo.getSourcebilltype())
						|| "HP34".equals(hvo.getSourcebilltype())
						|| "HP90".equals(hvo.getSourcebilltype())
						|| "HP95".equals(hvo.getSourcebilltype()))) {
			if (!StringUtil.isEmpty(hvo.getPk_tzpz_h()))
				if (!"Y".equals(hvo.getIsMerge())
						&& (hvo.getIsocr() == null || !hvo.getIsocr().booleanValue())) {//不是合并的情况下  不是识别
					hvo.setVbillstatus(Integer.valueOf(8));
				}
		} else if (!StringUtil.isEmpty(hvo.getPk_image_group())) {
			// 上传图片的无需处理
		}else if(hvo.getVbillstatus()!=null && hvo.getVbillstatus().intValue() == IVoucherConstants.TEMPORARY){
			//暂存的不处理
		} else {
			hvo.setVbillstatus(Integer.valueOf(8));
		}
		// 查询pzh
		if ((hvo.getIsimp() == null || !hvo.getIsimp().booleanValue())) {// 导入的时候，不需要重新设置凭证号zpm

			DZFDate dopedate = hvo.getDoperatedate();// new
														// DZFDate(doperatedate);
			String pk = hvo.getPk_tzpz_h();
			if (StringUtil.isEmptyWithTrim(pk) && !"Y".equals(hvo.getIsInsert())
					&& (hvo.getPreserveCode() == null || !hvo.getPreserveCode().booleanValue())) {
				String pzhvalue = yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(), dopedate);
				hvo.setPzh(pzhvalue);
			} else {
				// 如果跨月的话重新生成凭证号
				DZFDate dopedatetemp = hvo.getDoperatedate();
				if (!(dopedatetemp.getYear() == dopedate.getYear() && dopedatetemp.getMonth() == dopedate.getMonth())) {
					String pzhvalue = yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(), dopedate);
					hvo.setPzh(pzhvalue);
				}
			}
		}
		// }

		// 验证凭证号是否重复
		String where = " nvl(dr,0)=0 and pk_corp = ? and pzh = ? and period = ? and pk_tzpz_h <> ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPk_corp());
		sp.addParam(hvo.getPzh());
		sp.addParam(DateUtils.getPeriod(hvo.getDoperatedate()));
		sp.addParam(hvo.getPk_tzpz_h());
		TzpzHVO[] tCount = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, where, sp);
		if (tCount != null && tCount.length > 0) {
			throw new BusinessException("凭证号重复！");
		}

		DZFDouble totaldf = DZFDouble.ZERO_DBL;
		DZFDouble totaljf = DZFDouble.ZERO_DBL;

		SQLParameter sp1 = new SQLParameter();
		String kmsql = "select  * from ynt_cpaccount where pk_corp =? and nvl(dr,0)=0";
		sp1.addParam(hvo.getPk_corp());
//		List<YntCpaccountVO> cpalist = (List<YntCpaccountVO>) singleObjectBO.executeQuery(kmsql, sp1,
//				new BeanListProcessor(YntCpaccountVO.class));
//		HashMap<String, YntCpaccountVO> cpamap = new HashMap<String, YntCpaccountVO>();
//		for (YntCpaccountVO cpavo : cpalist) {
//			cpamap.put(cpavo.getPk_corp_account(), cpavo);
//		}
		Map<String, YntCpaccountVO> kmmap = new HashMap<String, YntCpaccountVO>();
		// 判断是否都为0
		boolean iszero = true;

		// 判断是否

		for (int i = 0; i < bodyvos.length; i++) {

			DZFDouble jfmny = VoUtils.getDZFDouble(bodyvos[i].getJfmny());
			DZFDouble dfmny = VoUtils.getDZFDouble(bodyvos[i].getDfmny());
			if (jfmny.doubleValue() != 0) {
				iszero = false;
			} else if (dfmny.doubleValue() != 0) {
				iszero = false;
			}

			// 科目
			String pk_accsubj = bodyvos[i].getPk_accsubj();

			if (!StringUtil.isEmptyWithTrim(pk_accsubj)) {

//				YntCpaccountVO kmvo = queryAccountByid(pk_accsubj);
				YntCpaccountVO kmvo = accountMap.get(pk_accsubj);
				kmmap.put(pk_accsubj, kmvo);
				Map<String, YntCpaccountVO> kmparentmap = getKmParent(accountMap, pk_accsubj);
				kmmap.putAll(kmparentmap);
				DZFBoolean fsefxkz = kmvo.getFsefxkz();
				if (fsefxkz != null && fsefxkz.booleanValue()) {// 开启发生额方向控制
					if ("HP32".equals(hvo.getSourcebilltype())) {
						// 来源于损益结转不受控制
					} else {
						Integer dir = kmvo.getDirection();
						if (dir != null && dir.intValue() == 0) {
							if (jfmny.doubleValue() == 0 && dfmny.doubleValue() != 0) {
								throw new BusinessException("借方科目 " + kmvo.getAccountcode() + " 发生额不能在贷方，请检查！");
							}
						}
						if (dir != null && dir.intValue() == 1) {
							if (jfmny.doubleValue() != 0 && dfmny.doubleValue() == 0) {
								throw new BusinessException("贷方科目 " + kmvo.getAccountcode() + " 发生额不能在借方，请检查！");
							}
						}
					}
				}

				if(!IBillTypeCode.HP32.equals(hvo.getSourcebilltype())//损益结转的凭证，金额为零也让过 
						&& !IBillTypeCode.HP34.equals(hvo.getSourcebilltype())&& !IBillTypeCode.HP70.equals(hvo.getSourcebilltype())){//成本的凭证，金额为零也让过
					if (hvo.getVbillstatus() == null || hvo.getVbillstatus().intValue() != IVoucherConstants.TEMPORARY) {
						if (jfmny.doubleValue() == 0 && dfmny.doubleValue() == 0) {
							// 为零
							throw new BusinessException("科目：" + bodyvos[i].getSubj_code() + ",金额不能为零");
						}
					}
				}
			}

			totaldf = totaldf.add(dfmny);
			totaljf = totaljf.add(jfmny);
		}
		String status = "modify";
		if (StringUtil.isEmpty(hvo.getPk_tzpz_h())) {// 新增
			status = "add";
		}
		checkyefxkz(kmmap, bodyvos, hvo.getPk_corp(), status);

		if (hvo.getVbillstatus() == null || hvo.getVbillstatus().intValue() != IVoucherConstants.TEMPORARY) {
			if(!IBillTypeCode.HP32.equals(hvo.getSourcebilltype())//损益结转的凭证，金额为零也让过 
					&& !IBillTypeCode.HP34.equals(hvo.getSourcebilltype())){//成本的凭证，金额为零也让过 
				if (totaldf.doubleValue() == 0 && iszero) {
					throw new BusinessException("贷方累计金额不能为零！");
				}
				if (totaljf.doubleValue() == 0 && iszero) {
					throw new BusinessException("借方累计金额不能为零！");
				}
			}
		}

		if (totaldf.doubleValue() != totaljf.doubleValue() && !iszero) {
			throw new BusinessException("凭证号:"+hvo.getPzh()+"，期间:"+hvo.getPeriod()+"，借贷不平衡，不能保存！");
		}
		hvo.setJfmny(totaljf);
		hvo.setDfmny(totaldf);

		// 判断当前制单日期所属期间是否已经结账，已结账的，不能保持
		/*
		 * if (hvo != null && !hvo.getIsqxsy().booleanValue()) { DZFDate date =
		 * hvo.getDoperatedate(); if (date != null) { SQLParameter sqlp = new
		 * SQLParameter(); sqlp.addParam(corpvo.getPk_corp());
		 * sqlp.addParam(date.getYear() + "-12"); QmJzVO[] qmjzVOs = (QmJzVO[])
		 * singleObjectBO.queryByCondition( QmJzVO.class,
		 * " pk_corp=? and period=? and nvl(dr,0)=0 ", sqlp); for (QmJzVO vo :
		 * qmjzVOs) { if (vo.getJzfinish() != null &&
		 * vo.getJzfinish().booleanValue()) { throw new
		 * BusinessException("-150"); //凭证所在期间已结账，不能再做凭证 } } } }
		 */
		// add by zhangj 有时候填制人不一定是修改人，所以每次保存时再赋值
		// getBillCardPanelWrapper().getBillCardPanel().getTailItem("coperatorid").setValue(_getOperator());
		// end 2015.5.29
	}
	
	/**
	 * 检查凭证引用的辅助核算和存货是否存在
	 * @param corpvo
	 * @param hvo
	 */
	private void checkAssistExist(CorpVO corpvo, TzpzHVO hvo) {
		Set<String> assists = new HashSet<String>();
		Set<String> inventorys = new HashSet<String>();
		TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
		boolean isic = IcCostStyle.IC_ON.equals(corpvo.getBbuildic());
		for (TzpzBVO tzpzBVO : bodyvos) {
			for (int i = 1; i <= 10; i++) {
				String id = (String) tzpzBVO.getAttributeValue("fzhsx" + i);
				if (!StringUtil.isEmpty(id)) {
					if (isic && i == 6) {
						inventorys.add(id);
					} else {
						assists.add(id);
					}
				}
				
			}
			if (!StringUtil.isEmpty(tzpzBVO.getPk_inventory())) {
				inventorys.add(tzpzBVO.getPk_inventory());
			}
		}
		if (assists.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("select count(1) from ynt_fzhs_b where pk_corp = ? and ")
            .append(SqlUtil.buildSqlForIn("pk_auacount_b", assists.toArray(new String[0])))
			.append(" and nvl(dr,0)=0 ");
			SQLParameter sp = new SQLParameter();
			sp.addParam(corpvo.getPk_corp());
			
			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sb.toString(), sp, new ColumnProcessor());
			if (count == null || count.intValue() != assists.size()) {
				throw new BusinessException("辅助核算不存在，或已被删除，请检查");
			}
		}
		
		if (inventorys.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("select count(1) from ynt_inventory where pk_corp = ? and ")
            .append(SqlUtil.buildSqlForIn("pk_inventory", inventorys.toArray(new String[0])))
			.append(" and nvl(dr,0)=0 ");
			SQLParameter sp = new SQLParameter();
			sp.addParam(corpvo.getPk_corp());
			
			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sb.toString(), sp, new ColumnProcessor());
			if (count == null || count.intValue() != inventorys.size()) {
				throw new BusinessException("存货不存在，或已被删除，请检查");
			}
		}
		
	}

	private void checkyefxkz(Map<String, YntCpaccountVO> kmmap, TzpzBVO[] bodyvos, String pk_corp, String status) {
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>(kmmap.values());
		for (YntCpaccountVO vo : list) {
			if (vo.getYefxkz() != null && vo.getYefxkz().booleanValue()) {
				String kmpk = vo.getPk_corp_account();
				SQLParameter sp = new SQLParameter();
				String sql = " select * from ynt_tzpz_b where pk_accsubj = ? and pk_corp = ? and nvl(dr,0) = 0 ";
				sp.addParam(kmpk);
				sp.addParam(pk_corp);
				List<TzpzBVO> bvolist = (List<TzpzBVO>) singleObjectBO.executeQuery(sql, sp,
						new BeanListProcessor(TzpzBVO.class));
				String sql1 = " select * from ynt_qcye where pk_accsubj = ? and pk_corp = ? and nvl(dr,0) = 0 ";
				List<QcYeVO> qclist = (List<QcYeVO>) singleObjectBO.executeQuery(sql1, sp,
						new BeanListProcessor(QcYeVO.class));
				DZFDouble qcmny = DZFDouble.ZERO_DBL;
				DZFDouble ye = DZFDouble.ZERO_DBL;
				if (qclist.size() == 1) {
					qcmny = VoUtils.getDZFDouble(qclist.get(0).getThismonthqc());
				}
				List<String> pkbs = new ArrayList<String>();
				List<TzpzBVO> bvolistnew = new ArrayList<TzpzBVO>();
				if (status.equals("add")) {// 新增 直接加上
					bvolistnew.addAll(bvolist);
					for (TzpzBVO bvo : bodyvos) {
						if (kmpk.equals(bvo.getPk_accsubj())) {
							bvolistnew.add(bvo);
						}
					}
				}
				if (status.equals("modify")) {// 修改
					for (TzpzBVO bvo : bodyvos) {
						if (kmpk.equals(bvo.getPk_accsubj())) {
							bvolistnew.add(bvo);
							String pkb = bvo.getPk_tzpz_b();
							pkbs.add(pkb);
						}
					}
					for (TzpzBVO bvo : bvolist) {
						if (!pkbs.contains(bvo.getPk_tzpz_b())) {
							bvolistnew.add(bvo);
						}
					}
				}
				if (status.equals("delete")) {// 删除
					for (TzpzBVO bvo : bodyvos) {
						if (kmpk.equals(bvo.getPk_accsubj())) {
							// bvolistnew.add(bvo);
							String pkb = bvo.getPk_tzpz_b();
							pkbs.add(pkb);
						}
					}
					for (TzpzBVO bvo : bvolist) {
						if (!pkbs.contains(bvo.getPk_tzpz_b())) {
							bvolistnew.add(bvo);
						}
					}
				}
				for (TzpzBVO bvo : bvolistnew) {
					if (vo.getDirection() == 0) {// 借方
						DZFDouble jd = SafeCompute.sub(VoUtils.getDZFDouble(bvo.getJfmny()),
								VoUtils.getDZFDouble(bvo.getDfmny()));
						ye = SafeCompute.add(ye, jd);
					}
					if (vo.getDirection() == 1) {// dai方
						DZFDouble jd = SafeCompute.sub(VoUtils.getDZFDouble(bvo.getDfmny()),
								VoUtils.getDZFDouble(bvo.getJfmny()));
						ye = SafeCompute.add(ye, jd);
					}
				}
				DZFDouble rest = SafeCompute.add(qcmny, ye);
				if (rest.doubleValue() < 0) {
					throw new BusinessException("科目 " + vo.getAccountcode() + " 设置了余额方向控制，请检查！");
				}
			}
		}
	}

	private Map<String, YntCpaccountVO> getKmParent(Map<String, YntCpaccountVO> mp, String kmpk) {
		YntCpaccountVO vo1 = mp.get(kmpk);
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO vo : mp.values()) {
			if (vo1.getAccountcode().startsWith(vo.getAccountcode())
					&& vo1.getAccountcode().length() > vo.getAccountcode().length()) {
				if (!map.containsKey(vo.getPk_corp_account())) {
					map.put(vo.getPk_corp_account(), vo);
				}
			}
		}
		return map;
	}

//	private String isSaveInvName(TzpzBVO[] bodyvos) {
//		StringBuffer sf = new StringBuffer();
//		for (int i = 0; i < bodyvos.length; i++) {
//			TzpzBVO v = bodyvos[i];
//			DZFBoolean isnum = v.getIsnum();
//			DZFDouble num = v.getNnumber();
//			DZFDouble nprice = v.getNprice();
//			String pk_invtory = v.getPk_inventory();
//			if (isnum != null && isnum.booleanValue()) {
//				if (pk_invtory == null || num == null || num.compareTo(DZFDouble.ZERO_DBL) == 0 || nprice == null
//						|| nprice.compareTo(new DZFDouble
//
//						(0)) == 0) {
//					sf.append("第 【" + (i + 1) + "】行， 存货、数量、单价不能为空! \n");
//				}
//			}
//		}
//		return sf.toString();
//	}

	private String isSaveCurName(TzpzBVO[] bodyvos) {
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < bodyvos.length; i++) {
			TzpzBVO v = bodyvos[i];
			if (v.getPk_currency() == null) {
				v.setPk_currency(IGlobalConstants.RMB_currency_id);
			}
			DZFBoolean isCur = v.getPk_currency().equals(IGlobalConstants.RMB_currency_id) ? DZFBoolean.FALSE
					: DZFBoolean.TRUE;
			if (isCur != null && isCur.booleanValue()) {
				DZFDouble nrate = v.getNrate();
				if (nrate == null) {
					sf.append("第 【" + (i + 1) + "】行， 汇率不能为空! \n");
				}
			}
		}
		return sf.toString();
	}

	private void checkInvNull(TzpzBVO[] bodyvos, CorpVO corpvo) throws DZFWarpException {
		/*
		 * liangyi 校验库存管理公司科目必填
		 */
		SQLParameter sqlp = new SQLParameter();
		// 判断当前公司是否为库存管理，
		String sql = "select sc.acccode from ynt_tdaccschema sc"
				+ " inner join bd_corp c on sc.pk_trade_accountschema=c.corptype"
				+ " where sc.acccode in('02','04') and c.pk_corp=? and nvl(c.dr,0)=0 and bbuildic='"+IcCostStyle.IC_ON+"'";//
		sqlp.addParam(corpvo.getPk_corp());
		Object obj = singleObjectBO.executeQuery(sql, sqlp, new ArrayProcessor());
		if (obj != null) {
			Object[] accCode = ((Object[]) obj);
			StringBuffer ckSubjAndInv = new StringBuffer();
			for (int i = 0; i < bodyvos.length; i++) {
				Object subj = bodyvos[i].getPk_accsubj();
				if (subj != null) {
					if ("02".equals(accCode[0])) {
						// 13年会计准则
						if ("1405".equals(subj) || "5001".equals(subj.toString()) || "500101".equals(subj.toString())
								|| "5401".equals(subj.toString()) || "540101".equals(subj.toString())) {
							Object pk_inventory = bodyvos[i].getPk_inventory();
							if (pk_inventory == null) {
								Object subjName = bodyvos[i].getSubj_name();
								if (ckSubjAndInv.length() > 0) {
									ckSubjAndInv.append("\n");
								}
								ckSubjAndInv.append("科目" + subjName + "， 存货不能为空");
							}
						}
					} else {
						// 07年会计准则
						if ("1405".equals(subj) || "6001".equals(subj.toString()) || "600101".equals(subj.toString())
								|| "6401".equals(subj.toString()) || "640101".equals(subj.toString())) {
							Object pk_inventory = bodyvos[i].getPk_inventory();
							if (pk_inventory == null) {
								Object subjName = bodyvos[i].getSubj_name();
								if (ckSubjAndInv.length() > 0) {
									ckSubjAndInv.append("\n");
								}
								ckSubjAndInv.append("科目" + subjName + "， 存货不能为空");
							}
						}
					}
				}
			}
			if (ckSubjAndInv.length() > 0) {
				throw new BusinessException(ckSubjAndInv.toString());
			}
		}
	}

	@Override
	public TzpzHVO queryVoucherById(String id) throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryVoucherById(id);
	}

	@Override
	public TzpzHVO queryHeadVoById(String id) throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryHeadVOById(id);
	}

	/**
	 * 根据图片组查询图片信息
	 */
	@Override
	public ImageLibraryVO[] queryImageVO(String pk_image_group) throws DZFWarpException {
		// 取下结算方式
		// ImageGroupVO groupvo = (ImageGroupVO)
		// singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
		// String settname = "";
		// if (groupvo.getSettlemode() != null) {
		// if (groupvo.getSettlemode().equals("0")) {
		// settname = "现金";
		// } else if (groupvo.getSettlemode().equals("1")) {
		// settname = "银行";
		// } else if (groupvo.getSettlemode().equals("2")) {
		// settname = "其他";
		// }
		// }
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(pk_image_group);
		ImageLibraryVO[] libs=null;
		if(pk_image_group.contains(",")){
			libs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class,"nvl(dr,0) = 0 and "+SqlUtil.buildSqlForIn("pk_image_group", pk_image_group.split(",")), new SQLParameter());
		}else{
			libs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class,
					" pk_image_group = ? and nvl(dr,0) = 0  ", sqlp);
		}
		
		if (libs != null && libs.length > 0) {
			addInvoiceInfo(libs[0].getPk_corp(), Arrays.asList(libs));
		}
		return libs;
	}

	/**
	 * 查询需要做凭证的图片组
	 */
	private Map<String, List<ImageLibraryVO>> queryImageVOToVoucher(ImageParamVO param, boolean ispt)
			throws DZFWarpException {
		String corpcode = param.getPk_corp();
		String begindate = param.getBegindate();
		String endDate = param.getEnddate();
		// 获取需要的全部的数据
		Map<String, List<ImageLibraryVO>> mapvalue = new HashMap<String, List<ImageLibraryVO>>();
		// 现在默认的查询日期只是限于一个月内，如果数据量再多的情况下，再改善
		SQLParameter sqlp = new SQLParameter();
		StringBuffer groupsql = new StringBuffer();
		groupsql.append("select a.pk_image_group,c.*, pz.vbillstatus as pzdt, pz.pk_tzpz_h as pk_ticket_h, ")
				.append(" case a.settlemode when '0' then '现金' ")
				.append(" when '1' then '银行' when '2' then '其他' end as settname from ynt_image_group a ")
				.append(" left join bd_corp b on  a.pk_corp = b.pk_corp")
				.append(" left join ynt_image_library c on a.pk_image_group = c.pk_image_group ")
				.append(" left join ynt_tzpz_h pz on a.pk_corp = pz.pk_corp")
				.append(" and a.pk_image_group = pz.pk_image_group and nvl(pz.dr, 0) = 0 ");

		// groupsql.append(" join ynt_parameter yp ");// 系统参数控制 逻辑前移
		// groupsql.append(" on yp.pk_corp = b.pk_corp and yp.parameterbm = ?
		// and yp.issync = 0 and nvl(yp.dr, 0) = 0 and yp.pardetailvalue = ? ");

		// 查询条件doperatedate更改为cvoucherdate
		groupsql.append(" where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(c.dr,0)=0 ");
		if (!StringUtil.isEmpty(param.getImgIds())) {
			String[] ids = param.getImgIds().split(",");
			if (ids.length > 1000) {
				ids = Arrays.copyOf(ids, 1000);
			}
			groupsql.append(" and ")
			.append(SqlUtil.buildSqlForIn("c.pk_image_library", ids));
		}
		if (!StringUtil.isEmpty(param.getImgGroupIds())) {
			String[] ids = param.getImgGroupIds().split(",");
			if (ids.length > 1000) {
				ids = Arrays.copyOf(ids, 1000);
			}
			groupsql.append(" and ")
					.append(SqlUtil.buildSqlForIn("a.pk_image_group", ids));
		}
		if (!StringUtil.isEmpty(corpcode)) {
			groupsql.append(" and b.pk_corp= ? ");
			sqlp.addParam(corpcode);
		}
		if (!StringUtil.isEmpty(begindate)) {
			groupsql.append(" and a.cvoucherdate>= ? ");
			sqlp.addParam(begindate);
		}
		if (!StringUtil.isEmpty(endDate)) {
			groupsql.append(" and a.cvoucherdate<= ?");
			sqlp.addParam(endDate);
		}

		groupsql.append(" and (a.istate = ?  or a.istate = ? or (pz.vbillstatus = -1 and pz.iautorecognize <> 1)) ");// 未处理单据或暂存态凭证
		sqlp.addParam(PhotoState.state0);
		sqlp.addParam(PhotoState.state1);
		if (ispt) {// 过滤票通图片
			groupsql.append(" and nvl(a.sourcemode,0) != ").append(PhotoState.SOURCEMODE_05);
		}
		if (PhotoParaCtlState.IsUseFactoryModel) {
			// 委托公司控制,会计工厂上线后需更改
			groupsql.append("    and not exists ( ");
			groupsql.append("        select fb.pk_customer ");
			groupsql.append("          from fct_busiapply_b fb ");
			groupsql.append("          left join fct_busiapply fc ");
			groupsql.append("            on fc.pk_busiapply = fb.pk_busiapply ");
			groupsql.append("           and nvl(fc.dr, 0) = 0 ");
			groupsql.append("           and nvl(fb.dr, 0) = 0 ");
			groupsql.append("         Where fb.pk_customer = b.pk_corp ");
			groupsql.append("           and fb.vstatus = ? ");
			// groupsql.append(" and (fb.begindate <= ? or fb.enddate >= ? ) ");
			groupsql.append("           and (fb.begindate <= a.cvoucherdate and fb.enddate >= a.cvoucherdate ) ");
			groupsql.append("    ) ");
			sqlp.addParam(PhotoState.state0);
		}
		groupsql.append(" order by a.pk_image_group,c.pk_image_library ");
		// sqlp.addParam(PhotoParaCtlState.PhotoParaCtlCode);
		// sqlp.addParam(PhotoParaCtlState.PhotoParaCtlValue_Manual);


		if (PhotoParaCtlState.IsUseFactoryModel) {
			sqlp.addParam(FactoryConst.ContractStatus_3);
		}
		List<ImageLibraryVO> grouplist = (List<ImageLibraryVO>) singleObjectBO.executeQuery(groupsql.toString(), sqlp,
				new BeanListProcessor(ImageLibraryVO.class));
		List<ImageLibraryVO> libvoslist = null;
		for (ImageLibraryVO libvo : grouplist) {
			if (mapvalue.containsKey(libvo.getPk_image_group())) {
				mapvalue.get(libvo.getPk_image_group()).add(libvo);
			} else {
				libvoslist = new ArrayList<ImageLibraryVO>();
				libvoslist.add(libvo);
				mapvalue.put(libvo.getPk_image_group(), libvoslist);
			}
		}

		addInvoiceInfo(corpcode, grouplist);
		return mapvalue;
	}
	/**
	 * 给图片增加识别信息
	 */
	private void addInvoiceInfo(String pk_corp, Collection<ImageLibraryVO> libs) {
		Set<String> imgIds = new HashSet<String>();

		for (ImageLibraryVO libvo : libs) {
			imgIds.add(libvo.getPk_image_library());
		}

		Map<String, OcrInvoiceVO> invoices = gl_pzglserv.queryInvoiceInfo(pk_corp, imgIds);
		for (ImageLibraryVO libvo : libs) {
			if (invoices.containsKey(libvo.getPk_image_library())) {
				libvo.setInvoice_info(invoices.get(libvo.getPk_image_library()));
			}
		}
	}

	/**
	 * 图片更改操作 ident ==1 是退回 
	 * ident ==2 是回写isuer标识 
	 * ident ==3 是未处理
	 */
	@Override
	public void updateImageVo(String pk_corp, String pk_image_group, String pk_image_library, String ident,
			ImageTurnMsgVO msgvo, TzpzHVO hvo, boolean delVch, String curropeid,boolean isadd) throws DZFWarpException {

		// 更新图片标识更新数据
		if (ident.equals("1")) {
			// 已经被使用的图片不能能加，先删除凭证

			ImageGroupVO igvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
			if (!delVch && igvo != null && igvo.getIsuer() != null && igvo.getIsuer().booleanValue()) {
				throw new BusinessException("图片已经被使用，删除凭证后再退回！");
			}

			// 如果图片所属公司不是登录公司，不能退回图片
			if (igvo != null && !igvo.getPk_corp().equals(pk_corp)) {
				throw new BusinessException("无权操作该图片！");
			}

			// 凭证所在期间已结账，不能退回
			// DZFDate date = (hvo != null ? hvo.getDoperatedate() : null);
			// if (date != null) {
			// SQLParameter sqlp = new SQLParameter();
			// sqlp.addParam(pk_corp);
			// sqlp.addParam(date.getYear() + "-" + date.getStrMonth());
			// QmJzVO[] qmjzVOs = (QmJzVO[]) singleObjectBO.queryByCondition(
			// QmJzVO.class,
			// " pk_corp= ? and period= ? and nvl(dr,0)=0 ", sqlp);
			// for (QmJzVO vo : qmjzVOs) {
			// if (vo.getJzfinish() != null
			// && vo.getJzfinish().booleanValue()) {
			// throw new BusinessException("凭证所在期间已结账，不能退回");
			// }
			// }
			// }

			try {
				// 退回重拍
				ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
						msgvo.getPk_image_group());
				// 备份图片组信息
				if (groupVO != null) {
					saveImageGroupBackUp(groupVO);

					innerSkipClipedImage(groupVO, msgvo.getCoperatorid(), msgvo.getTs());

				}

				// 更新图片信息状态
				ImageLibraryVO[] libraryVOs = (ImageLibraryVO[]) queryImageByImageGroup(groupVO);
				StringBuffer sf = new StringBuffer();
				List<String> list = new ArrayList<>();
				if (libraryVOs != null && libraryVOs.length != 0) {
					ImageLibraryVO libvo = null;
					for (int i = 0; i < libraryVOs.length; i++) {
						libvo = libraryVOs[i];
						libvo.setAttributeValue("isback", DZFBoolean.TRUE);
						sf.append(libvo.getPrimaryKey());

						if (i != libraryVOs.length - 1) {
							sf.append(",");
						}
						list.add(libvo.getPk_image_library());
					}
					singleObjectBO.updateAry(libraryVOs, new String[] { "isback" });
				}
				// 管理端上传的图片 在线端退回时 同步更新退回
				// if(groupVO != null && groupVO.getSourcemode() != null &&
				// groupVO.getSourcemode().intValue()
				// ==PhotoState.SOURCEMODE_10){

				if (list != null && list.size() > 0) {
					String wheresql = SqlUtil.buildSqlForIn("crelationid", list.toArray(new String[0]));
					SQLParameter sp = new SQLParameter();
					sp.addParam(StateEnum.HAND_BACK.getValue());
					UserVO user = userServiceImpl.queryUserJmVOByID(curropeid);
					sp.addParam("图片被" + user.getUser_name() + "退回");
					StringBuffer strb = new StringBuffer();
					strb.append(" update ynt_image_ocrlibrary set istate =? ,reason = ?  where " + wheresql);
					singleObjectBO.executeUpdate(strb.toString(), sp);
				}

				// }
				// 保存
				if (!StringUtil.isEmpty(sf.toString())) {
					msgvo.setPk_image_librarys(sf.toString());
				}
				singleObjectBO.saveObject(msgvo.getPk_corp(), msgvo);

				imageMsgHandle(pk_corp, curropeid, msgvo, igvo);// 图片处理

			} catch (Exception e) {
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
			}

			// ImageGroupVO groupVO = (ImageGroupVO)
			// singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
			// pk_image_group);
			// innerSkipClipedImage(groupVO, msgvo.getCoperatorid(), null);
			//
			// msgvo.setPk_image_group(pk_image_group);
			// singleObjectBO.saveObject(pk_corp, msgvo);

		} else if ("2".equals(ident)) {
			ImageGroupVO igvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);

			// 如果图片所属公司不是登录公司，不能生成凭证
			if (igvo != null && !pk_corp.equals(igvo.getPk_corp())) {
				throw new BusinessException("无权操作该图片！");
			}

			String queryimgSql = "select pk_image_group from ynt_tzpz_h where pk_tzpz_h = ?";
			SQLParameter sp = new SQLParameter();
			sp.addParam(hvo.getPk_tzpz_h());
			String pz_pk_image_group = (String) singleObjectBO.executeQuery(queryimgSql, sp, new ResultSetProcessor() {

				@Override
				public Object handleResultSet(ResultSet rs) throws SQLException {
					String pk_image_group = null;
					if (rs.next()) {
						pk_image_group = rs.getString("pk_image_group");
					}
					return pk_image_group;
				}

			});
			// 更新管理端图片已制证 2017-09-07
			ImageLibraryVO[] libraryVOs = (ImageLibraryVO[]) queryImageByImageGroup(igvo);
			List<String> list = new ArrayList<>();
			if (libraryVOs != null && libraryVOs.length != 0) {
				ImageLibraryVO libvo = null;
				for (int i = 0; i < libraryVOs.length; i++) {
					libvo = libraryVOs[i];
					list.add(libvo.getPk_image_library());
				}
			}
			if (list != null && list.size() > 0) {
				String wheresql = SqlUtil.buildSqlForIn("crelationid", list.toArray(new String[0]));
				sp.clearParams();
				sp.addParam(StateEnum.SUCCESS_VOCHER.getValue());
				sp.addParam("手动生成凭证成功");
				sp.addParam(DZFBoolean.TRUE);
				StringBuffer strb = new StringBuffer();
				strb.append(" update ynt_image_ocrlibrary set istate =? ,reason = ?,iszd=?  where " + wheresql);
				singleObjectBO.executeUpdate(strb.toString(), sp);
			}

			// 看当前图片是不是已经使用
			if (pz_pk_image_group == null || !pz_pk_image_group.equals(pk_image_group)) {
				SQLParameter sqlpa = new SQLParameter();
				sqlpa.addParam(pk_image_group);
				String querysql = "select count(1) from ynt_image_group where pk_image_group = ?  and nvl(isuer,'N')='N' ";
				BigDecimal res = (BigDecimal) singleObjectBO.executeQuery(querysql, sqlpa, new ColumnProcessor());
				if (res != null && res.doubleValue() == 0) {
					throw new BusinessException("图片已经引用，不能再次引用！");
				}
			}

			sp = new SQLParameter();
			sp.addParam(hvo.getPk_image_group());
			sp.addParam(hvo.getPrimaryKey());
			String queryPzsql = "select * from ynt_tzpz_h where nvl(dr,0) = 0 and pk_image_group = ? and pk_tzpz_h <> ?";
			List<TzpzHVO> pzRes = (List<TzpzHVO>) singleObjectBO.executeQuery(queryPzsql, sp,
					new BeanListProcessor(TzpzHVO.class));
			if (pzRes != null) {
				TzpzHVO tvo = null;
				if (pzRes.size() == 1) {
					tvo = pzRes.get(0);
					if (tvo.getVbillstatus() == IVoucherConstants.TEMPORARY) {
						singleObjectBO.deleteObjectByID(tvo.getPrimaryKey(),
								new Class[] { TzpzHVO.class, TzpzBVO.class });
					}else{
						if(isadd){
							throw new BusinessException("图片已经制单，不能再次制单!");
						}
					}
				} else if (pzRes.size() > 1) {
					throw new BusinessException("图片已经制单，不能再次制单!");
				}

			}

			// SQLParameter sqlp = new SQLParameter();
			if (hvo.getVbillstatus() != null && hvo.getVbillstatus().intValue() == IVoucherConstants.TEMPORARY) {
				// sqlp.addParam(PhotoState.state101);
				igvo.setIstate(PhotoState.state101);
			} else {
				// sqlp.addParam(PhotoState.state100);
				igvo.setIstate(PhotoState.state100);
			}
			igvo.setIsuer(DZFBoolean.TRUE);
			// sqlp.addParam(pk_image_group);
			// String updategroupsql = "update ynt_image_group set
			// isuer='Y',istate= ? where pk_image_group = ? ";// 回写已经被引用标识;
			// singleObjectBO.executeUpdate(updategroupsql, sqlp);

			igvo.setIsuer(DZFBoolean.TRUE);
			singleObjectBO.update(igvo, new String[] { "isuer", "istate" });// 回写已经被引用标识;

			// 备份图片组信息
			saveImageGroupBackUp(igvo);

			// 查询图片是否来源于票通
			ImageGroupVO grp = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
			if (grp != null && !StringUtil.isEmptyWithTrim(grp.getPk_ticket_h())) {
				sp = new SQLParameter();
				sp.addParam(DZFBoolean.TRUE.toString());
				sp.addParam(grp.getPk_ticket_h());
				singleObjectBO.executeUpdate(" update ynt_ticket_h h set h.istogl = ? where h.pk_ticket_h = ? ", sp);
			}

			imageMsgHandle(pk_corp, curropeid, msgvo, igvo);// 图片处理

		} else if (ident.equals("3")) {// 图片更改为未处理
			// 已经被使用的图片不能增加，先删除凭证
			ImageGroupVO igvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
			if (!delVch && igvo != null && igvo.getIsuer() != null && igvo.getIsuer().booleanValue()) {
				throw new BusinessException("图片已经被使用，删除凭证后再更改！");
			}

			if (igvo != null) {
				// 如果图片所属公司不是登录公司，不能退回图片
				if (!igvo.getPk_corp().equals(pk_corp)) {
					throw new BusinessException("无权操作该图片！");
				}
			}

			// 凭证所在期间已结账，不能退回
			DZFDate date = (hvo != null ? hvo.getDoperatedate() : null);
			if (date != null) {
				SQLParameter sqlp = new SQLParameter();
				sqlp.addParam(pk_corp);
				sqlp.addParam(date.getYear() + "-" + date.getStrMonth());
				QmJzVO[] qmjzVOs = (QmJzVO[]) singleObjectBO.queryByCondition(QmJzVO.class,
						" pk_corp= ? and period= ? and nvl(dr,0)=0 ", sqlp);
				for (QmJzVO vo : qmjzVOs) {
					if (vo.getJzfinish() != null && vo.getJzfinish().booleanValue()) {
						throw new BusinessException("凭证所在期间已结账，图片不能更改");
					}
				}
			}

			try {

				ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
						msgvo.getPk_image_group());
				// 备份图片组信息
				if (groupVO != null) {
					saveImageGroupBackUp(groupVO);

					innerSkipClipedImage(groupVO, msgvo.getCoperatorid(), msgvo.getTs());
					// groupVO二次更新，后期需优化
					groupVO.setIsskiped(DZFBoolean.FALSE);
					groupVO.setIsuer(DZFBoolean.FALSE);// 设置为未使用
					groupVO.setIstate(PhotoState.state0);// 自由态
					singleObjectBO.update(groupVO);
				}

				// 保存
				singleObjectBO.saveObject(msgvo.getPk_corp(), msgvo);

				imageMsgHandle(pk_corp, curropeid, msgvo, igvo);// 图片处理
			} catch (Exception e) {
				throw new BusinessException(e.getMessage());
			}

		}
	}

	private void imageMsgHandle(String pk_corp, String currid, ImageTurnMsgVO msgvo, ImageGroupVO groupVO) {
		// 消息回写
		IMsgService sys_msgtzserv = (IMsgService) SpringUtils.getBean("sys_msgtzserv");

		sys_msgtzserv.saveMsgVoFromImage(pk_corp, currid, msgvo, groupVO);

		sys_msgtzserv.deleteMsg(groupVO);
	}

	private void saveImageGroupBackUp(ImageGroupVO groupVO) throws DZFWarpException {
		Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
		imageGroupMap.put(groupVO.getPrimaryKey(), groupVO);
		IImageGroupService iImageGroupService = (IImageGroupService) SpringUtils.getBean("gl_pzimageserv");
		iImageGroupService.saveImageGroupBackUp(imageGroupMap, PhotoState.state80);// 退回状态
	}

	private void innerSkipClipedImage(ImageGroupVO groupVO, String skipedBy, DZFDateTime skipedOn)
			throws DZFWarpException {
		if (groupVO == null)
			return;
		// 更新图片的时间戳，防止并发问题
		groupVO.setIsskiped(new DZFBoolean(true));
		groupVO.setSkipedby(skipedBy);
		groupVO.setSkipedon(skipedOn);
		// groupVO.setDr(1); // 置删除状态
		groupVO.setIstate(PhotoState.state80);// 图片退回
		groupVO.setIsuer(new DZFBoolean(false));// 图片状态置为未使用
		// groupVO.setIstate(0);
		singleObjectBO.update(groupVO);

		// 删除凭证信息
		TzpzHVO[] headvos = queryVoucherByImageGroup(groupVO);
		deleteBD(headvos);
		
		//以下zpm删除
		// 删除预凭证信息
		//deleteBD(queryPreVoucherByImageGroup(groupVO));
		// 删除识图信息
		//deleteBD(queryIdentByImageGroup(groupVO));
		// 删除切图信息
		//deleteBD(queryClipingByImageGroup(groupVO));
		// 删除图片信息
		// deleteBD(queryImageByImageGroup(groupVO));
	}

	private void dealBillList() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 根据图片组pk获取所有该图片组对应的凭证记录
	 * 
	 * @param groupVO
	 * @return
	 * @throws BusinessException
	 */
	private TzpzHVO[] queryVoucherByImageGroup(ImageGroupVO groupVO) throws DZFWarpException {
		if (groupVO == null)
			return null;
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(groupVO.getPrimaryKey());
		sqlp.addParam(groupVO.getPk_corp());
		TzpzHVO[] headvos = (TzpzHVO[])
				singleObjectBO.queryByCondition(TzpzHVO.class, " nvl(dr,0)=0 and pk_image_group= ? and pk_corp = ? ", sqlp);
		if (headvos == null || headvos.length == 0)
			return null;
		for(TzpzHVO head : headvos){
			sqlp.clearParams();
			sqlp.addParam(head.getPk_tzpz_h());
			TzpzBVO[] bodys = (TzpzBVO[])
					singleObjectBO.queryByCondition(TzpzBVO.class, " nvl(dr,0)=0 and pk_tzpz_h = ?  ", sqlp);
			head.setChildren(bodys);
		}
		return headvos;
	}


	/**
	 * 根据图片组pk获取所有该图片组对应的预凭证记录
	 * 
	 * @param groupVO
	 * @return
	 * @throws BusinessException
	 */
	private IMageVoucherVO[] queryPreVoucherByImageGroup(ImageGroupVO groupVO) throws DZFWarpException {
		if (groupVO == null)
			return null;

		StringBuffer sf = new StringBuffer();
		sf.append(
				" nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group= ? ) ");

		SQLParameter param = new SQLParameter();
		param.addParam(groupVO.getPrimaryKey());

		List<IMageVoucherVO> headVOs = (List<IMageVoucherVO>) singleObjectBO.executeQuery(sf.toString(), param,
				new Class[] { IMageVoucherVO.class, IMageVoucherBVO.class });

		if (headVOs == null || headVOs.size() == 0)
			return null;

		return headVOs.toArray(new IMageVoucherVO[0]);
	}

	/**
	 * 根据图片组pk获取所有该图片组对应的识图记录
	 * 
	 * @param groupVO
	 * @return
	 * @throws BusinessException
	 */
	private ImageIdentVO[] queryIdentByImageGroup(ImageGroupVO groupVO) throws DZFWarpException {
		if (groupVO == null)
			return null;
		String where = String.format(
				"nvl(dr,0)=0 and pk_image_meta in (select a.pk_image_meta from ynt_image_meta a inner join  ynt_image_library b on a.pk_image_library=b.pk_image_library where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.pk_image_group='%s')",
				groupVO.getPrimaryKey());
		ImageIdentVO[] identVOs = (ImageIdentVO[]) singleObjectBO.queryByCondition(ImageIdentVO.class, where,
				new SQLParameter());
		if (identVOs == null || identVOs.length == 0)
			return null;
		return identVOs;
	}

	/**
	 * 根据图片组pk获取所有该图片组对应的切图记录
	 * 
	 * @param groupVO
	 * @return
	 * @throws BusinessException
	 */
	private ImageMetaVO[] queryClipingByImageGroup(ImageGroupVO groupVO) throws DZFWarpException {
		if (groupVO == null)
			return null;
		String where = String.format(
				"nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group='%s')",
				groupVO.getPrimaryKey());
		ImageMetaVO[] metaVOs = (ImageMetaVO[]) singleObjectBO.queryByCondition(ImageMetaVO.class, where,
				new SQLParameter());
		if (metaVOs == null || metaVOs.length == 0)
			return null;
		return metaVOs;
	}

	/**
	 * 根据图片组pk获取所有该图片组对应的切图记录
	 * 
	 * @param groupVO
	 * @return
	 * @throws BusinessException
	 */
	private ImageLibraryVO[] queryImageByImageGroup(ImageGroupVO groupVO) throws DZFWarpException {
		if (groupVO == null)
			return null;
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(groupVO.getPrimaryKey());
		ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class,
				"nvl(dr,0)=0 and pk_image_group= ? ", sqlp);
		if (imageVOs == null || imageVOs.length == 0)
			return null;
		return imageVOs;
	}

	private void deleteBD(SuperVO[] billVOs) throws DZFWarpException {
		if (billVOs == null || billVOs.length == 0)
			return;
		List<SuperVO> childrenList = new ArrayList<SuperVO>();
		for (int i = 0; i < billVOs.length; i++) {
			billVOs[i].setAttributeValue("dr", 1);// zpm
			// 存在子表，子表行dr置为删除态
			SuperVO[] childrenVOs = billVOs[i].getChildren();
			if (childrenVOs != null && childrenVOs.length != 0) {
				for (SuperVO child : childrenVOs) {
					child.setAttributeValue("dr", 1);// 删除态
					childrenList.add(child);
				}
			}
		}
		singleObjectBO.updateAry(billVOs);
		// singleObjectBO目前不支持主子表一同更新，所以采用此方法，后续singleObjectBO支持主子表更新，该代码可删除
		if (childrenList.size() != 0)
			singleObjectBO.updateAry(childrenList.toArray(new SuperVO[0]));
	}

	@Override
	public List<XjllVO> addCashFlow(XjllVO[] xjllvos) throws DZFWarpException {// 保存
		if (xjllvos == null || xjllvos.length < 1) {
			throw new BusinessException("现金流量数据为空");
		}

		try {
			// String pk_tzpz_b = xjllvos[0].getPk_tzpz_b();
			String pk_tzpz_h = xjllvos[0].getPk_tzpz_h();
			// if (pk_tzpz_h == null || pk_tzpz_h.length() <= 0
			// || /* pk_tzpz_b==null|| */pk_tzpz_h.length() <= 0)
			if (StringUtil.isEmpty(pk_tzpz_h))
				throw new BusinessException("没有凭证数据，请刷新后重新编辑提交!");

			TzpzHVO hvo = (TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_tzpz_h);
			if (hvo == null) {
				throw new BusinessException("未找到凭证数据!");
			}

			String sql = " pk_tzpz_h=? and pk_corp=? and nvl(dr,0)=0";
			SQLParameter sp = new SQLParameter();
			sp.addParam(hvo.getPk_tzpz_h());
			sp.addParam(hvo.getPk_corp());
			TzpzBVO[] bvo = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class, sql, sp);
			// DZFDate dopedate = hvo.getDoperatedate();

			// 取合计
			DZFDouble sumvalue = DZFDouble.ZERO_DBL;

			for (XjllVO vo : xjllvos) {
				// vo.setPk_accsubj(bvo.getPk_accsubj()) ;
				vo.setPzh(hvo.getPzh());
				// vo.setPk_corp() ;
				// vo.setCoperatorid(clientUI._getOperator()) ;
				// vo.setDoperatedate(dopedate) ;
				vo.setPk_tzpz_h(pk_tzpz_h);
				vo.setDoperatedate(hvo.getDoperatedate());
				// vo.setPk_tzpz_b(pk_tzpz_b) ;
				if (vo.getVdirect() == 0) {
					sumvalue = sumvalue.add(vo.getNmny());
				} else {
					sumvalue = sumvalue.sub(vo.getNmny());
				}

			}

			DZFDouble value = DZFDouble.ZERO_DBL;// bvo.getJfmny() ==null
													// ?bvo.getDfmny():bvo.getJfmny();

			int pz_xjll_num = 0;
			if (bvo != null && bvo.length > 0) {
			    CorpVO corp = corpService.queryByPk(hvo.getPk_corp());
			    YntCpaccountVO[] cpavos = accountService.queryByPk(corp.getPk_corp());
			    Set<String> cashSubj = KmschemaCash.getCashSubjectCode(cpavos,corp.getCorptype());
				String kmcode = null;
				for (TzpzBVO vo : bvo) {
					kmcode = vo.getSubj_code() == null ? vo.getVcode() : vo.getSubj_code();
					if (kmcode != null && cashSubj.contains(DZfcommonTools.getFirstCode(kmcode, DZFConstant.ACCOUNTCODERULE))) {
						pz_xjll_num++;
						if (vo.getJfmny() == null || vo.getJfmny().compareTo(DZFDouble.ZERO_DBL) == 0) {
							value = value.sub(vo.getDfmny());
						} else {
							value = value.add(vo.getJfmny());
						}

					}
				}
			} else {
				throw new BusinessException("凭证无分录信息!");
			}

			if (pz_xjll_num == 0) {
				throw new BusinessException("凭证无现金类科目!");
			}

			if (pz_xjll_num == bvo.length) {
				throw new BusinessException("凭证分录全部为现金类科目，无需手工录入!");
			}

			if (sumvalue.compareTo(value) != 0) {
				throw new BusinessException("现金流量合计与凭证表体值不等!");
			}

			String pk_corp = xjllvos[0].getPk_corp();
			// 插入前先删除
			String cd = " delete from YNT_XJLL where pk_tzpz_h = ? and pk_corp = ? ";
			sp.clearParams();
			sp.addParam(pk_tzpz_h);
			sp.addParam(pk_corp);
			singleObjectBO.executeUpdate(cd, sp);

			// 保存
			String[] pks = singleObjectBO.insertVOArr(pk_corp, xjllvos);

			// 反写凭证的是否已分配现金流量
			hvo.setIsfpxjxm(DZFBoolean.TRUE);
			hvo.setError_cash_analyse(false);
			singleObjectBO.update(hvo, new String[] { "isfpxjxm", "error_cash_analyse" });

			List<XjllVO> rslist = new ArrayList<XjllVO>();
			for (int i = 0; i < pks.length; i++) {
				xjllvos[i].setPk_xjll(pks[i]);
				rslist.add(xjllvos[i]);
			}

			return rslist;
		} catch (Exception e1) {
			log.error("操作失败", e1);
			throw new BusinessException(e1.getMessage());
		}
	}

    @Override
    public void deleteCashFlow(String pk_tzpz_h, String pk_corp) throws DZFWarpException {
        // 删除
        String updateCashFlow = "update ynt_xjll set dr = 1" +
                " where pk_tzpz_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_tzpz_h);
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(updateCashFlow, sp);

        String updateVoucher = "update ynt_tzpz_h set isfpxjxm = 'N', error_cash_analyse = '1' " +
                " where pk_tzpz_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        singleObjectBO.executeUpdate(updateVoucher, sp);
    }

	@Override
	public XjllVO[] queryCashFlow(String pk_tzpz_h, String pk_corp) throws DZFWarpException {
		StringBuilder sb = new StringBuilder();
		sb.append("select a.pk_xjll, a.coperatorid, a.doperatedate, a.pk_corp, a.pk_tzpz_h, a.pk_xjllxm, a.vdirect,")
				.append(" a.nmny, b.pzh from YNT_XJLL a ")
				// .append("left join ynt_tzpz_h b on a.pk_tzpz_h = b.pk_tzpz_h
				// and a.pk_corp = b.pk_corp ")
				.append(" join ynt_tzpz_h b on a.pk_tzpz_h = b.pk_tzpz_h   ")
				.append("where a.pk_tzpz_h = ? and a.pk_corp = ? and nvl(a.dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);
		List<XjllVO> rs = (List<XjllVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(XjllVO.class));
		return rs.toArray(new XjllVO[0]);
	}

	@Override
	public void checkQjsy(TzpzHVO headVO) throws DZFWarpException {
		if (headVO.getIsqxsy() == null || !headVO.getIsqxsy().booleanValue()) {
			boolean hasProfitAndLoss = false;
			if (headVO.getChildren() == null) {
				hasProfitAndLoss = true;
			} else {
				// 已经经过期间损益的月份修改或新增凭证时，如果凭证不涉及损益类科目的话，不给提示：当期已经损益结转
				Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(headVO.getPk_corp());
				TzpzBVO[] bvos = (TzpzBVO[]) headVO.getChildren();
				for (TzpzBVO tzpzBVO : bvos) {
					YntCpaccountVO account = accountMap.get(tzpzBVO.getPk_accsubj());
					if (account == null) {// [如果缓存不存在，按主键查询]
						account = queryAccountByid(tzpzBVO.getPk_accsubj());
					}
					if (account != null && account.getAccountkind() == 5) {
						hasProfitAndLoss = true;
						break;
					}
				}
			}

			if (hasProfitAndLoss) {
				StringBuilder sbCode = new StringBuilder(
						"select count(1) from  ynt_qmcl where pk_corp=? and period=? and nvl(dr,0) = 0 and nvl(isqjsyjz,'N')='Y'");
				SQLParameter sp = new SQLParameter();
				sp.addParam(headVO.getPk_corp());
				headVO.setPeriod(DateUtils.getPeriod(headVO.getDoperatedate()));
				sp.addParam(headVO.getPeriod());
				BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp,
						new ColumnProcessor());

				if (repeatCodeNum.intValue() > 0) {
					String errCodeStr = IVoucherConstants.EXE_RECONFM_CODE;// "-150"
					throw new BusinessException(errCodeStr);
				}
			}
		}
	}

	@Override
	public List<TzpzHVO> processCopyVoucher(CorpVO corpvo, List<String> ids, String copyPeriod,
			String aimPeriod, String aimDate, String userId) throws DZFWarpException {
		List<TzpzHVO> copiedVOs = new ArrayList<>();
		QueryVoucher voucher = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		List<TzpzHVO> hvos;
		if (ids != null && ids.size() > 0) {
			hvos = voucher.queryVoucherByids(ids);
		} else {
			hvos = voucher.queryByPeriod(copyPeriod, corpvo.getPk_corp());
		}
		// 目标日期是否为固定日期
		boolean isFixedDate = !StringUtil.isEmpty(aimDate);
		DZFDate newDate = null;
		if (isFixedDate) {
			if (aimDate.length() == 10) {
				newDate = new DZFDate(aimDate);
			} else {
				if (aimDate.length() < 2) {
					aimDate = "0" + aimDate;
				}
				newDate = new DZFDate(aimPeriod + "-" + aimDate);
			}
		}
		int year = Integer.valueOf(aimPeriod.substring(0, 4));
		int month = Integer.valueOf(aimPeriod.substring(5, 7));
		for (TzpzHVO hvo : hvos) {
			// 有来源凭证不复制
			if (StringUtil.isEmpty(hvo.getPk_image_group())
					&& (hvo.getSource_relation() != null || !StringUtil.isEmpty(hvo.getSourcebilltype())))
				continue;

			// 暂存凭证不复制
			if (Integer.valueOf(IVoucherConstants.TEMPORARY).equals(hvo.getVbillstatus())
					&& hvo.getIautorecognize() != 1) {
				continue;
			}
			if (!isFixedDate) {
				int day = Integer.valueOf(hvo.getDoperatedate().toString().substring(8, 10));
				int daymax = DZFDate.getDaysMonth(year, month);
				if (day > daymax) {
					newDate = new DZFDate(aimPeriod + "-" + daymax);
				} else {
					newDate = new DZFDate(aimPeriod + "-" + hvo.getDoperatedate().toString().substring(8, 10));
				}
			}
			hvo.setIsqxsy(new DZFBoolean(true));
			hvo.setTs(new DZFDateTime());
			hvo.setCoperatorid(userId);
			hvo.setPeriod(DateUtils.getPeriod(newDate));
			hvo.setDoperatedate(newDate);
			hvo.setVapproveid(null);
			hvo.setDapprovedate(null);
			hvo.setPk_billtype(null);
			hvo.setVbillstatus(8);
			hvo.setPk_tzpz_h(null);
			hvo.setIshasjz(new DZFBoolean("N"));
			hvo.setVjzoperatorid(null);
			hvo.setDjzdate(null);
			hvo.setIsfpxjxm(new DZFBoolean("N"));
			hvo.setVyear(newDate.getYear());
			hvo.setIsimp(null);
			hvo.setBsign(null);
			hvo.setVcashid(null);
			hvo.setDcashdate(null);
			hvo.setSourcebilltype(null);
			hvo.setSourcebillid(null);
			hvo.setPk_image_group(null);
			hvo.setPk_image_library(null);
			hvo.setVicbillcode(null);
			hvo.setVicbillcodetype(null);
			hvo.setNbills(0);
			// 设置凭证号为目标月最大号
			// hvo.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, new
			// DZFDate(aimTime + "-01")));
			// 保存凭证
			// singleObjectBO.saveObject(hvo.getPk_corp(), hvo);
			TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
			if (bvos != null) {
				for (TzpzBVO tzpzBVO : bvos) {
					tzpzBVO.setVicbillcodetype(null);
					tzpzBVO.setXsjzcb(null);
					tzpzBVO.setGlchhsnum(null);
					tzpzBVO.setGlcgmny(null);
				}
			}
			saveVoucher(corpvo, hvo);
			copiedVOs.add(hvo);
		}
		return copiedVOs;
	}

	// 查询红字回冲的摘要
	@Override
	public List<TzpzHVO> serHasRedBack(VoucherParamVO paramvo) throws DZFWarpException {
		if (StringUtil.isEmpty(paramvo.getPk_corp()) || StringUtil.isEmpty(paramvo.getPzh())
				|| paramvo.getZdrq() == null) {
			throw new BusinessException("红字回冲失败！");
		}
		// String sql = "select * from YNT_TZPZ_B where pk_corp=? " +
		// "and nvl(dr,0)=0 and zy like '红冲%' " +
		// "and zy like '%" + paramvo.getZdrq() + "%' and zy like '%" +
		// paramvo.getPzh() + "%' ";

		// String sql = "select * from YNT_TZPZ_B where pk_corp=? "
		// + "and nvl(dr,0)=0 and zy like '红冲%' "
		// + "and zy like ? and zy like ? ";
		StringBuffer sf = new StringBuffer();
		sf.append("select * from YNT_TZPZ_B where pk_corp=? ");
		sf.append("and nvl(dr,0)=0 and zy like '红冲%' ");
		sf.append("and zy like ? and zy like ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		sp.addParam("%" + paramvo.getZdrq() + "%");
		sp.addParam("%" + paramvo.getPzh() + "%");

		@SuppressWarnings("unchecked")
		List<TzpzHVO> hvo = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));

		return hvo;
	}

	// 根据图片id查询凭证id
	@Override
	public List<TzpzHVO> serVoucherByImgPk(VoucherParamVO paramvo) throws DZFWarpException {
		// action使用pk_tzpz_h接收前台传过来的pk_image_group
		if (StringUtil.isEmpty(paramvo.getPk_corp()) || StringUtil.isEmpty(paramvo.getPk_tzpz_h())) {
			throw new BusinessException("联查凭证失败！");
		}
		// 代码感觉有问题，联查凭证应取前台pk_image_group，而非pk_image_library
		// String sql =
		// "select * from ynt_tzpz_h where pk_corp = ? and pk_image_library =
		// ?";
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_tzpz_h where nvl(dr,0) = 0 and pk_corp = ? and pk_image_group = ( ");
		sf.append("        select pk_image_group from ynt_image_library Where pk_image_library = ? ");
		sf.append("   ) ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(paramvo.getPk_tzpz_h());

		List<TzpzHVO> hvo = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		return hvo;

	}

	@Override
	public ImageGroupVO queryImageGroupByPrimaryKey(String pk_image_group) throws DZFWarpException {
		ImageGroupVO groupVO = null;
		if (StringUtil.isEmpty(pk_image_group)) {
			return groupVO;
		}
		if (pk_image_group.contains(",")) {
			// 多组多张图片，按一组处理
			ImageParamVO param = new ImageParamVO();
			param.setImgGroupIds(pk_image_group);
			List<ImageGroupVO> groups = queryImageGroupByPicture(param);
			if (groups.size() == 1) {
				groupVO = groups.get(0);
			} else if (groups.size() > 1) {
				groupVO = groups.get(0);
				List<ImageLibraryVO> libs = new ArrayList<>();
				for (ImageGroupVO group: groups) {
					group.getChildren();
					libs.addAll(Arrays.asList((ImageLibraryVO[]) group.getChildren()));
				}
				for (ImageLibraryVO lib: libs) {
					lib.setPk_image_group(pk_image_group);
				}
				groupVO.setPk_image_group(pk_image_group);
				groupVO.setChildren(libs.toArray(new ImageLibraryVO[0]));
			}
		} else {
			groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
			if(groupVO != null){
				CorpVO corpvo =corpService.queryByPk(groupVO.getPk_corp());
				groupVO.setCorpcode(corpvo.getUnitcode());
			}
		}
		return groupVO;
	}

	private void checkIsJz(String pk_corp, String period) {
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(period);
		String qmjzsqlwhere = "select jzfinish from YNT_QMJZ  where nvl(dr,0) = 0 and pk_corp = ? and period = ? ";
		String jzFinish = (String) singleObjectBO.executeQuery(qmjzsqlwhere, sqlp, new ColumnProcessor());
		if ("Y".equals(jzFinish)) {
			throw new BusinessException(period.substring(0, 4) + "年度已经年结,不能操作!");
		}
	}

	@Override
	public List<ImageGroupVO> queryImageGroupByPicture(ImageParamVO param)
			throws DZFWarpException {
		// 取参数设置之图片生成凭证方式 去掉
		// YntParameterSet parameter = paramService.queryParamterbyCode(pk_corp,
		// PhotoParaCtlState.PhotoParaCtlCode);
		// Integer pardetailValue = parameter.getPardetailvalue();
		// if(parameter == null
		// || pardetailValue ==
		// PhotoParaCtlState.PhotoParaCtlValue_Sys){//参数如果不是会计公司生成，返回
		// return null;
		// }

		// 判断是否启用票通，启用后，判断票通图片生成方式，如自动，则不取票通图片
		boolean ispt = true;
		String pk_corp = param.getPk_corp();
		if (pk_corp != null) {
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			if (corpvo != null && corpvo.getDef12() != null
					&& DZFBoolean.TRUE.toString().equalsIgnoreCase(corpvo.getDef12())) {
				GxhszVO gxh = gl_gxhszserv.query(pk_corp);// 个性化设置
				if (gxh.getPt_gen_vch() != null && gxh.getPt_gen_vch() == 1) {
					ispt = false;
				}
			}
		}

		Map<String, List<ImageLibraryVO>> library = queryImageVOToVoucher(param, ispt);

		List<ImageGroupVO> list = new ArrayList<ImageGroupVO>();
		if (library != null && library.size() > 0) {
			ImageGroupVO libraryGroup = null;
			List<ImageLibraryVO> lvo = null;
			ImageLibraryVO[] imageLibrarys = null;

			Map<String, ImageGroupVO> imgGrpMap = queryImageGroupByPks(library);
			for (Map.Entry<String, List<ImageLibraryVO>> map : library.entrySet()) {
				if (imgGrpMap.containsKey(map.getKey())) {
					libraryGroup = imgGrpMap.get(map.getKey());
				}
				if (libraryGroup == null) {
					libraryGroup = new ImageGroupVO();
					libraryGroup.setPrimaryKey(map.getKey());// 为排序增加
				}

				lvo = map.getValue();
				if(lvo == null || lvo.size() == 0){
					continue;
				}
				if (lvo.get(0).getImgpath() == null) {
					continue;
				}
				imageLibrarys = lvo.toArray(new ImageLibraryVO[lvo.size()]);
				Arrays.sort(imageLibrarys, new Comparator<ImageLibraryVO>() {

					@Override
					public int compare(ImageLibraryVO o1, ImageLibraryVO o2) {
						int i = o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
						return i;
					}

				});
				libraryGroup.setChildren(imageLibrarys);// (lvo.toArray());
				list.add(libraryGroup);
			}
		}
		// 排序
		Collections.sort(list, new ImageGroupSort());
        // imgIds合并为一个group
        if (!StringUtil.isEmpty(param.getImgIds()) && list.size() > 0) {
            ImageGroupVO firstGroup = list.get(0);
            StringBuilder groupId = new StringBuilder(firstGroup.getPk_image_group());
            List<ImageLibraryVO> childList = new ArrayList<>();
            childList.addAll(Arrays.asList((ImageLibraryVO[]) firstGroup.getChildren()));
            for (int i = 1; i < list.size(); i++) {
                ImageGroupVO groupVO = list.get(i);
                groupId.append(",").append(groupVO.getPk_image_group());
                childList.addAll(Arrays.asList((ImageLibraryVO[]) groupVO.getChildren()));
            }
            String groupIdStr = groupId.toString();
            for (ImageLibraryVO child: childList) {
                child.setPk_image_group(groupIdStr);
            }
            firstGroup.setPk_image_group(groupIdStr);
            firstGroup.setChildren(childList.toArray(new ImageLibraryVO[0]));
            list = new ArrayList<>();
            list.add(firstGroup);
        }
		return list;
	}

    @Override
    public boolean checkImageGroupExist(String id) throws DZFWarpException {
	    if (id.length() > 24) {
            id = id.substring(0, 24);
        }
	    String sql = "select 1 from ynt_image_group where pk_image_group = ? ";
	    SQLParameter sp = new SQLParameter();
	    sp.addParam(id);
        return singleObjectBO.isExists(null, sql, sp);
    }

    private Map<String, ImageGroupVO> queryImageGroupByPks(Map<String, List<ImageLibraryVO>> library)
			throws DZFWarpException {
		Map<String, ImageGroupVO> imgGrpMap = new HashMap<String, ImageGroupVO>();
		String[] imgGrppks = library.keySet().toArray(new String[0]);
		String where = SqlUtil.buildSqlForIn("pk_image_group", imgGrppks);
		StringBuffer sf = new StringBuffer();
		sf.append("nvl(dr,0) = 0 and ");
		sf.append(where);
		ImageGroupVO[] imgGrpVOs = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, sf.toString(),
				new SQLParameter());

		if (imgGrpVOs == null || imgGrpVOs.length == 0) {

		} else {
			for (ImageGroupVO imgGrpVO : imgGrpVOs) {
				imgGrpMap.put(imgGrpVO.getPk_image_group(), imgGrpVO);
			}
		}
		return imgGrpMap;
	}

	@Override
	public void checkIcIntrade(String pk_corp, String pk_Intrade_h) {

		IntradeHVO intradevo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, pk_Intrade_h);
		if (intradevo != null) {
			String sourcetype = intradevo.getSourcebilltype();
			if (IBillTypeCode.HP70.equals(sourcetype)) {
				throw new BusinessException("暂估单生成的入库单,不能删除凭证!");
			}
		}
	}

	@Override
	public TzpzHVO createVoucherQuick(CorpVO corp, String user_id, JSONObject rawData) throws DZFWarpException {
		String pk_model_h = (String) rawData.get("pk_model_h");
		String vch_date = (String) rawData.get("vch_date");

		// 是否暂存态
		boolean isTemporary = false;
		String pk_corp = corp.getPk_corp();
		if (StringUtil.isEmpty(pk_model_h)) {
			throw new BusinessException("找不到对应模板！");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_model_h);
		String billType = (String) singleObjectBO.executeQuery(
				"select vspstylecode from ynt_dcmodel_h where pk_model_h = ? and nvl(dr,0)=0", sp,
				new ColumnProcessor());
		List<DcModelBVO> modelVos = dcpzjmbserv.queryByPId(pk_model_h, corp.getPk_corp());
		String summary = (String) rawData.get("summary");
		String remark = (String) rawData.get("remark");
		String pk_image_group = (String) rawData.get("pk_image_group");

		if (!StringUtil.isEmpty(pk_image_group)) {
			ImageGroupVO oldImageGroupVO=(ImageGroupVO)singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
			if(oldImageGroupVO!=null){
				if(oldImageGroupVO.getIstate()!=PhotoState.state0&&oldImageGroupVO.getIstate()!=PhotoState.state1){
					throw new BusinessException("该图片已制单或作废，请刷新！");
				}
			}
			JSONArray mergeGroup = rawData.getJSONArray("mergeGroup");
			if (mergeGroup != null && mergeGroup.size() > 0) {
				// List<String> list = new ArrayList<>();
				// // 存在管理的 不允许合并生单
				// sp.clearParams();
				// sp.addParam(pk_corp);
				// for(Object ss :mergeGroup){
				// list.add((String)ss);
				// sp.addParam((String)ss);
				// }
				// list.add(pk_image_group);
				// sp.addParam(pk_image_group);
				// String inSQL1 = SQLHelper.getInSQL(list);
				//
				//
				// String sql = " select pk_image_group from ynt_image_group
				// where pk_corp = ? and sourcemode =10 and nvl(dr,0)=0 and
				// pk_image_group in "+ inSQL1;
				// boolean isexists = singleObjectBO.isExists(pk_corp, sql, sp);
				// if(isexists)
				// throw new BusinessException("存在管理上传的图片,不允许合并生单！");
				// 合并图片组
				List<String> groups = Arrays.asList(mergeGroup.toArray(new String[0]));
				List<String> newGroups = new ArrayList<String>(groups);
				gl_pzimageserv.processMergeGroup(pk_corp, pk_image_group, newGroups);
			}
		}

		DZFDouble jfTotal = DZFDouble.ZERO_DBL;
		DZFDouble dfTotal = DZFDouble.ZERO_DBL;


		List<TzpzBVO> voucherEntries = new ArrayList<TzpzBVO>();
		for (DcModelBVO dcModelBVO : modelVos) {
			TzpzBVO entry = new TzpzBVO();
			String zy = summary;
			String mny = rawData.getString(dcModelBVO.getVfield());
			if (mny == null) {
				throw new BusinessException("金额不能为空!");
			}
			// 税目
			entry.setPk_taxitem(rawData.getString("tax_" + dcModelBVO.getPk_accsubj()));
			DZFDouble dzfMny = new DZFDouble(mny);
			if (dcModelBVO.getDirection() == 0) {
				// 借方
				entry.setJfmny(dzfMny);
				entry.setDfmny(DZFDouble.ZERO_DBL);
				jfTotal = jfTotal.add(dzfMny);
			} else {
				entry.setDfmny(dzfMny);
				entry.setJfmny(DZFDouble.ZERO_DBL);
				dfTotal = dfTotal.add(dzfMny);
			}
			if (StringUtil.isEmpty(zy)) {
				zy = dcModelBVO.getZy();
			}
			if (remark != null)
				zy += remark;
			entry.setZy(zy);

			entry.setPk_currency(yntBoPubUtil.getCNYPk());

			entry.setDirect(dcModelBVO.getDirection());

			sp.clearParams();
			sp.addParam(pk_corp);
			sp.addParam(dcModelBVO.getPk_accsubj());

			YntCpaccountVO[] accounts = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
					" pk_corp = ? and pk_corp_account = ? and nvl(dr,0)=0", sp);

			if (accounts.length > 0) {
				YntCpaccountVO account = accounts[0];
				if (account.getIsleaf() == null || !account.getIsleaf().booleanValue()) {
					sp.clearParams();
					sp.addParam(pk_corp);
					sp.addParam(account.getAccountcode() + "%");
					accounts = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
							" pk_corp = ? and accountcode like ? and isleaf = 'Y' and nvl(dr,0)=0 order by accountcode",
							sp);
					account = accounts[0];
				}
				if (account.getIsnum() != null && account.getIsnum().booleanValue()) {
					String num = rawData.getString("number");
					if (!StringUtil.isEmpty(num)) {
						DZFDouble number = new DZFDouble(num);
						if (dzfMny != null && number.doubleValue() != 0) {
							entry.setNnumber(new DZFDouble(num));
							entry.setNprice(dzfMny.div(number, 4));
						}
					}
				}
				entry.setVcode(account.getAccountcode());
				entry.setVname(account.getAccountname());
				entry.setKmmchie(account.getFullname());
				entry.setSubj_allname(account.getFullname());
				entry.setSubj_code(account.getAccountcode());
				entry.setSubj_name(account.getAccountname());
				entry.setPk_accsubj(account.getPk_corp_account());
				String isfz = account.getIsfzhs();
				// 辅助核算
				for (int i = 1; i <= 10; i++) {
					if (isfz.charAt(i - 1) == '1') {
						String fzhsId = rawData.getString("fzhs" + i);
						if (i == 6 && IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
							if (StringUtil.isEmpty(fzhsId)) {
								sp.clearParams();
								sp.addParam(pk_corp);
								sp.addParam(account.getPk_corp_account());
								fzhsId = (String) singleObjectBO.executeQuery(
										"select PK_INVENTORY from " + "(select PK_INVENTORY from ynt_inventory"
												+ " where pk_corp = ? and pk_accsubj = ? and nvl(dr,0)=0 order by code) where rownum <= 1 ",
										sp, new ColumnProcessor());
								if (fzhsId == null) {
									isTemporary = true;
									break;
								}
							}
							entry.setPk_inventory(fzhsId);
						} else {
							if (StringUtil.isEmpty(fzhsId)) {
								sp.clearParams();
								sp.addParam(pk_corp);
								sp.addParam(IDefaultValue.DefaultGroup);
								sp.addParam(i);
								String fzhs = (String) singleObjectBO.executeQuery(
										"select pk_auacount_h from ynt_fzhs_h where pk_corp = ? or pk_corp = ? and code = ? and nvl(dr,0)=0 ",
										sp, new ColumnProcessor());
								sp.clearParams();
								sp.addParam(pk_corp);
								sp.addParam(fzhs);
								fzhsId = (String) singleObjectBO.executeQuery(
										"select pk_auacount_b from " + "(select pk_auacount_b from ynt_fzhs_b"
												+ " where pk_corp = ? and pk_auacount_h = ? and nvl(dr,0)=0 order by code) where rownum <= 1 ",
										sp, new ColumnProcessor());
								if (fzhsId == null) {
									isTemporary = true;
									break;
								}
							}
							entry.setAttributeValue("fzhsx" + i, fzhsId);
						}

					}

				}
			} else {
				isTemporary = true;
				break;
			}
			voucherEntries.add(entry);
		}
		if (!isTemporary) {
			isTemporary = !jfTotal.equals(dfTotal);
			if(isTemporary){
				throw new BusinessException("生成凭证失败:借贷方金额不等！");
			}
		}
		if (isTemporary) {
			throw new BusinessException("生成凭证失败！");
			// headVO = (TzpzHVO) singleObjectBO.saveObject(pk_corp, headVO);
			// sp.clearParams();
			// sp.addParam(PhotoState.state100);
			// sp.addParam(pk_corp);
			// sp.addParam(pk_image_group);
			// singleObjectBO.executeUpdate("update ynt_image_group set istate =
			// ? where pk_corp = ? and pk_image_group = ?", sp);
		}
		TzpzHVO headVO = new TzpzHVO();
		DZFDate date = StringUtil.isEmpty(vch_date) ? new DZFDate() : new DZFDate(vch_date);
		headVO.setDr(0);
		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(jfTotal);
		headVO.setDfmny(dfTotal);
		headVO.setCoperatorid(user_id);
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(date);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, date));
		headVO.setVbillstatus(Integer.valueOf(IVoucherConstants.FREE));// 默认自由态--isTemporary 已经是false了
		//headVO.setVbillstatus(isTemporary ? Integer.valueOf(IVoucherConstants.TEMPORARY) : Integer.valueOf(IVoucherConstants.FREE));// 默认自由态
		headVO.setPeriod(date.toString().substring(0, 7));
		headVO.setVyear(Integer.valueOf(date.toString().substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		if ("01".equals(billType)) {
			headVO.setFp_style(IFpStyleEnum.SPECINVOICE.getValue());
		} else if ("02".equals(billType)) {
			headVO.setFp_style(IFpStyleEnum.COMMINVOICE.getValue());
		}
		String issyjz = (String) rawData.get("issyjz");
		if(!StringUtil.isEmpty(issyjz)&&issyjz.equals("Y")){
			headVO.setPassSyjz("Y");
		}
		if (!StringUtil.isEmpty(pk_image_group)) {
			sp.clearParams();
			sp.addParam(pk_corp);
			sp.addParam(pk_image_group);
			String imgQuery = " select count(1) from ynt_image_library where nvl(dr,0)=0 and pk_corp = ? and pk_image_group = ? ";
			headVO.setPk_image_group(pk_image_group);
			BigDecimal imgNum = (BigDecimal) singleObjectBO.executeQuery(imgQuery, sp, new ColumnProcessor());
			// 设置单据张数
			headVO.setNbills(imgNum.intValue());
		}

		headVO.setChildren(voucherEntries.toArray(new TzpzBVO[0]));
		//add mfz
		if(!StringUtil.isEmpty(pk_image_group)){
			headVO.setUserObject(queryInvouceKeyByImageGroup(pk_image_group));
			headVO.setSourcebilltype(IBillTypeCode.HP110);
			headVO.setIautorecognize(2);
		}
		//end mfz
		headVO = saveVoucher(corp, headVO);
		return headVO;

	}
	
	private String queryInvouceKeyByImageGroup(String pk_image_group)throws DZFWarpException{
		String returnKey=null;
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_interface_invoice ");
		sb.append(" where nvl(dr,0)=0 and pk_image_group=? ");
		sp.addParam(pk_image_group);
		List<OcrInvoiceVO> invoiceList=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		StringBuffer invoiceKey=new StringBuffer();;
		if(invoiceList!=null&&invoiceList.size()>0){
			List<OcrInvoiceDetailVO> detailList=queryInvoiceDetail(invoiceList);
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			for (int i = 0; i < invoiceList.size(); i++) {
				OcrInvoiceVO invoiceVO = invoiceList.get(i);
				if(StringUtil.isEmpty(invoiceVO.getPk_billcategory())){
					throw new BusinessException("票据正在识别中，请稍后再试！");
				}
				invoiceVO.setChildren(detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
			if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveZckp(invoiceList.toArray(new OcrInvoiceVO[0])))){
				throw new BusinessException("所选票据已生成后续单据，请在资产卡片节点处理！");
			}
			if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveIctrade(invoiceList.toArray(new OcrInvoiceVO[0])))){
				throw new BusinessException("所选票据已生成后续单据，请在出入库单节点处理！");
			}
			for(int i=0;i<invoiceList.size();i++){
				invoiceKey.append(invoiceList.get(i).getPk_invoice()+",");
			}
			returnKey=invoiceKey.toString().substring(0, invoiceKey.length()-1);
		}else{
			String sql="select * from ynt_image_ocrlibrary where crelationid in(select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group=?)";
			List<OcrImageLibraryVO> ocrlibVO=(List<OcrImageLibraryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(OcrImageLibraryVO.class));
			if(ocrlibVO!=null&&ocrlibVO.size()>0&&(ocrlibVO.get(0).getIstate()==10||ocrlibVO.get(0).getIstate()==0)){
				throw new BusinessException("票据正在识别中，请稍后再试！");
			}
			return null;
		}
		return returnKey;
	}
	private List<OcrInvoiceDetailVO> queryInvoiceDetail(List<OcrInvoiceVO> list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> returnList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}
	@Override
	public List<TzpzHVO> queryVoucherByIds(List<String> pklists, boolean containsChildren, boolean containsRelation, boolean containsTaxItem) throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryVoucherByids(pklists, containsChildren, containsRelation, containsTaxItem);
	}

	/**
	 * 根据主键pks获取vo信息
	 */
	@Override
	public List<TzpzHVO> queryVoucherByIds(List<String> pklists) throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryVoucherByids(pklists);
	}

	private void updateVoucherNumberWhenInsert(TzpzHVO hvo) {
		String sql = "select 1 from ynt_tzpz_h where pk_corp = ? " + "and period = ? and pzh = ? and nvl(dr,0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPk_corp());
		sp.addParam(hvo.getPeriod());
		sp.addParam(hvo.getPzh());
		if (singleObjectBO.isExists(hvo.getPk_corp(), sql, sp)) {
			sql = " pk_corp = ? and period = ? and nvl(dr, 0) = 0 and pzh >= ? ";
			TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, sql, sp);
			for (TzpzHVO tzpzHVO : hvos) {
				String pzh = tzpzHVO.getPzh();
				int pzhInt = Integer.valueOf(pzh);
				pzhInt++;
				tzpzHVO.setPzh(String.format("%04d", pzhInt));
				updateIctradePzh(tzpzHVO);
			}
			singleObjectBO.updateAry(hvos, new String[] { "pzh" });
		}
	}

	private void updateIctradePzh(TzpzHVO hvo) {
		// 更新采购单 销售单凭证号
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(hvo.getPzh());
		sqlp.addParam(hvo.getPrimaryKey());
		sqlp.addParam(hvo.getPk_corp());
		CorpVO corp = corpService.queryByPk(hvo.getPk_corp());
		if (corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 1) {
			singleObjectBO.executeUpdate(" update ynt_ictrade_h set pzh= ? where pzid= ? and pk_corp = ? ",
					sqlp);
			singleObjectBO.executeUpdate(
					" update ynt_ictradein set pzh= ? where pk_voucher= ? and pk_corp = ? ", sqlp);
			singleObjectBO.executeUpdate(
					" update ynt_ictradeout set pzh= ? where pk_voucher= ? and pk_corp = ? ", sqlp);
		}
	}

	private void dealTaxItem(TzpzHVO hvo, CorpVO corpvo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPk_corp());
		sp.addParam(hvo.getPk_tzpz_h());
		String sql = " delete from ynt_pztaxitem where pk_corp = ? and pk_tzpz_h = ? ";
		singleObjectBO.executeUpdate(sql, sp);

		TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
		if (bvos == null) {
			return;
		}
		Map<String, List<PZTaxItemRadioVO>> taxMap = new HashMap<String, List<PZTaxItemRadioVO>>();
//		Map<String, TaxitemVO> itemMap = null;
		for (TzpzBVO bvo : bvos) {
//			if (!StringUtil.isEmpty(bvo.getPk_taxitem())) {
//				if (itemMap == null) {
//					itemMap = getTaxItemMap(); 
//				}
//				TaxitemVO item = itemMap.get(bvo.getPk_taxitem());
//				if(item == null)
//					continue;
//				String taxCode = item.getTaxcode();
//				PZTaxItemRadioVO taxvo = new PZTaxItemRadioVO();
//				taxvo.setPk_taxitem(bvo.getPk_taxitem());
//				taxvo.setTaxcode(taxCode);
//				taxvo.setTaxname(item.getTaxname());
//				taxvo.setTaxratio(item.getTaxratio());
			if(bvo.getTax_items()!=null&&bvo.getTax_items().size()>0){
				for (PZTaxItemRadioVO taxvo : bvo.getTax_items()) {
					taxvo.setPk_corp(hvo.getPk_corp());
					taxvo.setPk_tzpz_h(hvo.getPk_tzpz_h());
					taxvo.setPk_tzpz_b(bvo.getPk_tzpz_b());
					taxvo.setPeriod(hvo.getPeriod());
					if ("一般纳税人".equals(corpvo.getChargedeptname())) {
						// 一般人
						taxvo.setFp_style(hvo.getFp_style() == null ? IFpStyleEnum.SPECINVOICE.getValue() : hvo.getFp_style());// 默认专票
					} else {
						// 小规模
						taxvo.setFp_style(hvo.getFp_style() == null ? IFpStyleEnum.COMMINVOICE.getValue() : hvo.getFp_style());// 默认普票
					}

					DZFDouble mny = null;
					int vdirect = 0;
					if (bvo.getJfmny() == null || bvo.getJfmny().compareTo(DZFDouble.ZERO_DBL) == 0) {
						vdirect = 1;
						mny = bvo.getDfmny();
					} else {
						mny = bvo.getJfmny();
					}
					taxvo.setMny(mny);
					taxvo.setVdirect(vdirect);
					taxvo.setDr(0);
					if (taxMap.containsKey(taxvo.getTaxcode())) {
						taxMap.get(taxvo.getTaxcode()).add(taxvo);
					} else {
						List<PZTaxItemRadioVO> taxList = new ArrayList<PZTaxItemRadioVO>();
						taxList.add(taxvo);
						taxMap.put(taxvo.getTaxcode(), taxList);
					}
				}
				
			}
		}

		if (taxMap.size() == 0) {
			return;
		}
		List<PZTaxItemRadioVO> taxList = CaclTaxMny.calTaxMny(taxMap, bvos);
		if (taxList.size() > 0) {
			singleObjectBO.insertVOArr(hvo.getPk_corp(), taxList.toArray(new PZTaxItemRadioVO[0]));
		}
	}
	
	private Map<String, TaxitemVO> getTaxItemMap() {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IGlobalConstants.DefaultGroup);
		TaxitemVO[] items = (TaxitemVO[]) singleObjectBO.queryByCondition(TaxitemVO.class,
				" nvl(dr, 0) = 0 and pk_corp = ? ", sp);
		Map<String, TaxitemVO> map = new HashMap<String, TaxitemVO>();
		for (TaxitemVO taxitemVO : items) {
			map.put(taxitemVO.getPk_taxitem(), taxitemVO);
		}
		return map;
	}
	
	public void saveTaxItem(TzpzHVO hvo, CorpVO corpvo) throws DZFWarpException{
		dealTaxItem(hvo, corpvo);//处理税目
	}

	@Override
	public String getLastVoucherDate(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String date = (String) singleObjectBO.executeQuery("select max(doperatedate) from ynt_tzpz_h where pk_corp = ? and nvl(dr,0)=0",
				sp, new ColumnProcessor());
		return date;
	}
	
	private PzSourceRelationVO[] getSourceRelations(String pk_tzpz_h,
			String pk_corp) {
		String condition = " pk_tzpz_h = ? and pk_corp = ? and nvl(dr, 0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);
		PzSourceRelationVO[] rs = (PzSourceRelationVO[]) singleObjectBO
				.queryByCondition(PzSourceRelationVO.class, condition, sp);
		return rs;
	}

	@Override
	public QueryPageVO query(VoucherParamVO paramvo)
			throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryVoucherPaged(paramvo, false);
	}

	@Override
	public QueryPageVO processQueryVoucherPaged(VoucherParamVO paramvo)
			throws DZFWarpException {
		QueryVoucher qv = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		return qv.queryVoucherPaged(paramvo, true);
	}

	@Override
	public List<TaxitemVO> getTaxItems(String chargeType) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IGlobalConstants.DefaultGroup);
		sp.addParam(chargeType == null ? "小规模纳税人" : chargeType);
		TaxitemVO[] items = (TaxitemVO[]) singleObjectBO.queryByCondition(TaxitemVO.class,
				" nvl(dr, 0) = 0 and pk_corp = ? and chargedeptname = ? order by iorder", sp);
		return Arrays.asList(items);
	}

	@Override
	public PZTaxItemRadioVO[] getVoucherTaxItem(String pk_tzpz_h, String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_tzpz_h);
		return (PZTaxItemRadioVO[]) singleObjectBO.queryByCondition(PZTaxItemRadioVO.class,
				" pk_corp = ? and nvl(dr,0)=0 and pk_tzpz_h = ? ", sp);
	}

	@Override
	public void saveVoucherTaxItem(PZTaxItemRadioVO[] items, String pk_corp) throws DZFWarpException {
		if (items != null && items.length > 0) {
			String pk_voucher = items[0].getPk_tzpz_h();
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_voucher);
			TzpzBVO[] bvos = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class,
					" pk_corp = ? and nvl(dr,0) = 0 and pk_tzpz_h = ? ", sp);
			if (bvos.length == 0) {
				throw new BusinessException("凭证不存在，请检查");
			}
			String msg = CaclTaxMny.checkVoucherTaxItem(corpService.queryByPk(pk_corp),
					items, bvos);
			if (msg != null) {
				throw new BusinessException(msg);
			}
			// 插入前先删除
			deleteVoucherTaxItemAlone(pk_voucher, pk_corp);
			for (PZTaxItemRadioVO item: items) {
				item.setPk_corp(pk_corp);
				item.setUser_save(true);
			}
			singleObjectBO.insertVOArr(pk_corp, items);
            // 更新凭证状态
            String statusSql = " update ynt_tzpz_h set is_tax_analyse = '1', error_tax_analyse = '0' where pk_tzpz_h = ? ";
            SQLParameter statusSp = new SQLParameter();
            statusSp.addParam(pk_voucher);
            singleObjectBO.executeUpdate(statusSql, statusSp);
		}
	}

	@Override
	public void deleteVoucherTaxItem(String pk_voucher, String pk_corp) throws DZFWarpException {
        deleteVoucherTaxItemAlone(pk_voucher, pk_corp);
        // 更新凭证状态
        String sql = " update ynt_tzpz_h set is_tax_analyse = '0', error_tax_analyse = '0' where pk_tzpz_h = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_voucher);
        singleObjectBO.executeUpdate(sql, sp);
	}

    private void deleteVoucherTaxItemAlone(String pk_voucher, String pk_corp) throws DZFWarpException {
        String delSql = " delete from ynt_pztaxitem where pk_corp = ? and pk_tzpz_h = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_voucher);
        singleObjectBO.executeUpdate(delSql, sp);
    }
    // 获取所有分录的税表表项
    private List<PZTaxItemRadioVO> getVoucherTaxItemFromEntries(TzpzBVO[] bvos) {
		List<PZTaxItemRadioVO> taxItems = new ArrayList<>();
		for (int i = 0; i < bvos.length; i++) {
			List<PZTaxItemRadioVO> entryItems = bvos[i].getTax_items();
			if (entryItems != null) {
				for (PZTaxItemRadioVO item : entryItems) {
					item.setPk_pztaxitem(null);
					item.setEntry_index(i);
				}
				taxItems.addAll(entryItems);
			}
		}
		return taxItems;
	}
    //此接口只针对 加盟商，代码没有考虑分支机构多种情况
	@Override
	public String checkChannelContract(String gs) throws DZFWarpException {
		if(StringUtil.isEmpty(gs))
			return null;
		
		//过滤数据 找到里边的公司
		String[] ids = gs.split(",");
		Set<String> set = new HashSet<String>();
		for(String id : ids){
			if(StringUtil.isEmpty(id))
				continue;
			set.add(id);
		}
		if(set.size() == 0)
			return null;
		
		//根据相同上级机构合并同类项  加盟商的
		String sql = "select pk_corp, fathercorp from bd_corp bd where nvl(dr,0)=0 and nvl(ischannel,'N') = 'Y' and nvl(isncust, 'N')= 'N' and "
				+ SqlUtil.buildSqlForIn("pk_corp", set.toArray(new String[0]));
		
		List<DatatruansVO> list = (List<DatatruansVO>) singleObjectBO.executeQuery(sql,
				new SQLParameter(), new BeanListProcessor(DatatruansVO.class));
		if(list == null || list.size() == 0)
			return null;
		
		//判断是否到期 集合
		String msg = null;
		List<String> warns = new ArrayList<String>();
		String date = new DZFDate().toString();
		for(DatatruansVO dvo : list){
			msg = checkContract(dvo.getFathercorp(), dvo.getPk_corp(), date);
			if(!StringUtil.isEmpty(msg)){
				warns.add(dvo.getFathercorp());
			}
		}
		
		if(warns.size() == 0)
			return null;
		//找上级的名字
		sql = "select pk_corp, unitname from bd_corp bd where nvl(dr,0)=0 and nvl(ischannel,'N') = 'Y' and "
				+ SqlUtil.buildSqlForIn("pk_corp", warns.toArray(new String[0]));
		
		
		List<CorpVO> fathers = (List<CorpVO>) singleObjectBO.executeQuery(sql, 
				new SQLParameter(), new BeanListProcessor(CorpVO.class));
		if(fathers == null || fathers.size() == 0)
			return null;
		
		StringBuffer sf = new StringBuffer();
		String unitname;
		for(int i = 0; i < fathers.size(); i++){
			unitname = SecretCodeUtils.deCode(fathers.get(i).getUnitname());
			sf.append(unitname);
			sf.append(",");
			
			if(i == 2){
				sf.append("....");
				break;
			}
				
		}
		
		msg = null;
		msg = "【" + sf.substring(0, sf.length() - 1) + "】的合同已到期不能新增凭证";
		
		return msg;
	}
}
