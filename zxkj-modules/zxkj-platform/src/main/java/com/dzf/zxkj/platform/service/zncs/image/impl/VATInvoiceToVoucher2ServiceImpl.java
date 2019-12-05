package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemParamVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.IAutoMatchName;
import com.dzf.zxkj.platform.service.zncs.image.IImage2BillServiceImpl;
import com.dzf.zxkj.platform.service.zncs.image.IImageToVoucherExtendService;
import com.dzf.zxkj.platform.util.zncs.MatchTypeEnum;
import com.dzf.zxkj.platform.util.zncs.PjTypeEnum;
import com.dzf.zxkj.platform.util.zncs.TaxItemUtil;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


// 增值税发票（新版识别 完全走关键字）
@Service("aitovoucherserv_vatinvoice2")
public class VATInvoiceToVoucher2ServiceImpl extends DefaultImageToVoucherServiceImpl
		implements IImageToVoucherExtendService {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IInventoryService invservice;
	@Autowired
	private IMeasureService measervice;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICbComconstant gl_cbconstant;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IAccountService accountService;

	@Autowired
	@Qualifier("image2bill_vatsale")
	private IImage2BillServiceImpl image2bill_vatsale;
	@Autowired
	@Qualifier("image2bill_vatincom")
	private IImage2BillServiceImpl image2bill_vatincom;

	private static String sdktax = "代开企业税号";
	private static String sdkcorp = "代开企业名称";

	@Override
	public DcModelBVO[] getDcModelBVO(TzpzHVO hvo, ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo) {
		if (invvo == null)
			return null;

		String invtype = invvo.getInvoicetype();
		if (StringUtil.isEmpty(invtype))
			return null;

		DcModelHVO headvo = getMatchModel(invvo, corpvo, hvo, grpvo);
		if (headvo == null)
			return null;
		// 是否已识别
		hvo.setIautorecognize(1);

		// 判断费用类发票 用于凭证摘要
		if (!StringUtil.isEmpty(headvo.getKeywords()) && headvo.getKeywords().endsWith("费")) {
			hvo.setIfeetype(1);
		}
		// 更新model 信息
		invvo.setKeywords(headvo.getKeywords());
		invvo.setPk_model_h(headvo.getPk_model_h());
		// singleObjectBO.update(invvo, new String[] { "pk_model_h", "keywords"
		// });
		updateKeyWord(invvo, headvo);
		DcModelBVO[] models = headvo.getChildren();
		return models;
	}

	// 获取到匹配合适的模板
	@Override
	public DcModelHVO getMatchModel(OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo1, ImageGroupVO grpvo) {
		if (invvo == null)
			return null;
		List<DcModelBVO> lista = null;
		String billtype = invvo.getInvoicetype();
		String type = null;
		int imagetype = hvo1.getIfptype();
		if (imagetype == VATInvoiceTypeConst.VAT_INCOM_INVOICE) {
			type = "采购";
			if (grpvo != null && grpvo.getPjlxstatus() != null
					&& grpvo.getPjlxstatus().intValue() == PjTypeEnum.OTHER.getValue()) {
				type = "其他采购";
			}
		} else if (VATInvoiceTypeConst.VAT_SALE_INVOICE == imagetype) {
			type = "销售";
			if (grpvo != null && grpvo.getPjlxstatus() != null
					&& grpvo.getPjlxstatus().intValue() == PjTypeEnum.OTHER.getValue()) {
				type = "其他销售";
			}
		}
		String pk_corp = corpvo.getPk_corp();
		List<DcModelHVO> list = queryDcModelHVOs(pk_corp, billtype, type);
		DcModelHVO hvo = filterModelDataByKeyWords(list, corpvo, invvo);
		if (hvo != null) {
			lista = dcpzjmbserv.queryByPId(hvo.getPk_model_h(), pk_corp);
			if (lista != null && lista.size() > 0) {
				hvo.setChildren(lista.toArray(new DcModelBVO[0]));
			}
		}
		return hvo;
	}

	// 查询符合条件的业务类型数据
	private List<DcModelHVO> queryDcModelHVOs(String pk_corp, String billtype, String type) {
		if (StringUtil.isEmpty(billtype) || StringUtil.isEmpty(type))
			return null;
		List<DcModelHVO> list = dcpzjmbserv.queryAccordBankModel(pk_corp, new String[] { billtype, type });
		return list;
	}

	// 通过关键字过滤合适的模板数据
	private DcModelHVO filterModelDataByKeyWords(List<DcModelHVO> list, CorpVO vo, OcrInvoiceVO invvo) {

		if (list == null || list.size() == 0)
			return null;
		if (list.size() == 1)
			return list.get(0);

		String vmome = invvo.getVmemo();
		String pipeistyle = "";
		List<ModelSelectVO> zmselectlist = new ArrayList<ModelSelectVO>();
		List<DcModelHVO> filterlist = new ArrayList<>();

		for (DcModelHVO dc : list) {
			String keywords = dc.getKeywords();// 这个字段不可能为空，但还是判断一下
			if (StringUtil.isEmpty(keywords))
				continue;

			keywords = keywords.replace("*", "&");
			String[] kds = keywords.split("&");

			// 收购 代开
			if (!StringUtil.isEmpty(vmome)
					&& (vmome.contains("收购(左上角标志)") || (vmome.contains("代开企业税号") && vmome.contains("代开企业名称")))) {
				if (kds != null && kds.length > 1) {
					if (kds[1].equals("收购") || kds[1].equals("代开")) {
						filterlist.add(dc);
						continue;
					}
				}
			}
		}

		// 非 收购 代开
		if (filterlist == null || filterlist.size() == 0) {
			// 3关键字 4默认 采购 销售
			for (DcModelHVO dc : list) {
				String keywords = dc.getKeywords();// 这个字段不可能为空，但还是判断一下
				if (StringUtil.isEmpty(keywords))
					continue;
				keywords = keywords.replace("*", "&");
				String[] kds = keywords.split("&");
				if (kds != null && kds.length == 1) {
					pipeistyle = ModelSelectVO.pipeistyle_5;
					zmselectlist.add(buildSelectVO(dc, pipeistyle));
					continue;
				}
				// 按以下顺序识别，优先级
				boolean isexist = isExistKeyWords(kds, invvo.getVfirsrinvname());
				pipeistyle = ModelSelectVO.pipeistyle_3;

				if (isexist) {
					zmselectlist.add(buildSelectVO(dc, pipeistyle));
				}
			}
		} else {

			// 0收购 代开 关键字 1收购 代开 4默认 采购 销售
			for (DcModelHVO dc : list) {
				String keywords = dc.getKeywords();
				if (StringUtil.isEmpty(keywords))
					continue;
				keywords = keywords.replace("*", "&");
				String[] kds = keywords.split("&");
				if (kds != null && kds.length == 1) {
					// 采购销售
					pipeistyle = ModelSelectVO.pipeistyle_5;
					zmselectlist.add(buildSelectVO(dc, pipeistyle));
					continue;
				}
			}

			for (DcModelHVO dc : filterlist) {
				String keywords = dc.getKeywords();
				if (StringUtil.isEmpty(keywords))
					continue;
				keywords = keywords.replace("*", "&");
				String[] kds = keywords.split("&");
				if (kds != null && kds.length == 2) {
					if (kds[1].equals("收购") || kds[1].equals("代开")) {
						pipeistyle = ModelSelectVO.pipeistyle_1;
						zmselectlist.add(buildSelectVO(dc, pipeistyle));
					}
					continue;
				}
				String[] kds1 = new String[kds.length - 2];
				System.arraycopy(kds, 2, kds1, 0, kds.length - 2);
				// 按以下顺序识别，优先级
				boolean isexist = isExistKeyWords(kds1, invvo.getVfirsrinvname());
				pipeistyle = ModelSelectVO.pipeistyle_0;
				if (isexist) {
					zmselectlist.add(buildSelectVO(dc, pipeistyle));
				}
			}
		}

		DcModelHVO defaultmodel = null;
		// 选择其中级别最高的
		if (zmselectlist != null && zmselectlist.size() > 0) {
			Collections.sort(zmselectlist);
			defaultmodel = zmselectlist.get(zmselectlist.size() - 1).getDefaultmodel();
		}
		return defaultmodel;
	}

	private ModelSelectVO buildSelectVO(DcModelHVO defaultmodel, String pipeistyle) {
		ModelSelectVO vo = new ModelSelectVO();
		vo.setDefaultmodel(defaultmodel);
		vo.setPipeistyle(pipeistyle);
		return vo;
	}

	private boolean isExistKeyWords(String[] kds, String value) {
		boolean flag = false;
		if (kds == null || kds.length == 0)
			return flag;
		if (StringUtil.isEmpty(value))
			return flag;
		for (int i = 0; i < kds.length; i++) {
			if (value.contains(kds[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	@Override
	protected int matchImageType(OcrInvoiceVO vo, CorpVO corpvo, ImageGroupVO grpvo) {

		// if (grpvo.getPjlxstatus() != null) {
		// int status = grpvo.getPjlxstatus();
		// if (PjTypeEnum.TZKXSFP.getValue() == status ||
		// PjTypeEnum.TDKXSFP.getValue() == status
		// || PjTypeEnum.TZKLW.getValue() == status ||
		// PjTypeEnum.TDKLW.getValue() == status) {
		// return VATInvoiceTypeConst.VAT_SALE_INVOICE;
		// } else if (PjTypeEnum.TCGZYFP.getValue() == status ||
		// PjTypeEnum.TCGPTFP.getValue() == status) {
		// return VATInvoiceTypeConst.VAT_INCOM_INVOICE;
		// }
		// }

		String saleName = filterName(vo.getVsalename());
		String purName = filterName(vo.getVpurchname());
		String corpName = filterName(corpvo.getUnitname());

		if (!StringUtil.isEmpty(purName) && (purName.contains(corpName) || corpName.contains(purName))) {
			if (!StringUtil.isEmpty(saleName) && isDk(saleName, vo.getVmemo())) {
				// 截取销方名称
				setSaleInfo(vo);
			}
			return VATInvoiceTypeConst.VAT_INCOM_INVOICE;
		} else if (!StringUtil.isEmpty(saleName)
				&& ((saleName.contains(corpName) || corpName.contains(saleName)) || isDk(saleName, vo.getVmemo()))) {
			if (isDk(saleName, vo.getVmemo())) {
				// 截取销方名称
				setSaleInfo(vo);
			}
			saleName = filterName(vo.getVsalename());
			if (!StringUtil.isEmpty(saleName) && (saleName.contains(corpName) || corpName.contains(saleName))) {
				return VATInvoiceTypeConst.VAT_SALE_INVOICE;
			} else {
				return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
			}
		} else {
			return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
		}
	}

	private boolean isDk(String saleName, String vmome) {
		if (!StringUtil.isEmpty(saleName) && (saleName.contains("税务局") || saleName.contains("国税局"))) {
			return true;
		} else if (!StringUtil.isEmpty(vmome) && (vmome.contains(sdktax) || vmome.contains(sdkcorp))) {
			return true;
		}
		return false;

	}

	private void setSaleInfo(OcrInvoiceVO vo) {

		String vmome = vo.getVmemo();

		if (StringUtil.isEmpty(vmome))
			return;
		if(!vmome.startsWith(sdktax)){
			int index =vmome.indexOf(sdktax);
			int len = vmome.length();
			vmome = vmome.substring(index,len);
		}

		Map<String, String> map = new HashMap<>();
		if (vmome.contains(sdktax) && vmome.contains(sdkcorp)) {
			String[] sts = vmome.split(" ");
			int len = sts.length;
			for (int i = 0; i < len; i++) {
				getSplitStr(sts[i], map);
			}
			vo.setVsaletaxno(getVsaletax(map));
			vo.setVsalename(getVsalename(map));
		}
	}

	private String getVsaletax(Map<String, String> map) {
		String vattaxno = map.get(sdktax);

		if(StringUtil.isEmpty(vattaxno)){
			return null;
		}
		return vattaxno;
	}

	private String getVsalename(Map<String, String> map) {
		String salename = map.get(sdkcorp);

		if(StringUtil.isEmpty(salename)){
			return null;
		}
		int len  = salename.length();
		if (salename.indexOf("货物（劳务）名称") > 0) {
			salename = salename.substring(0, salename.indexOf("货物（劳务）名称"));
		}else if (salename.indexOf("工程名称") > 0) {
			salename = salename.substring(0, salename.indexOf("工程名称"));
		}else if(len>30){
			salename = salename.substring(0, 30);
		}
		return salename;
	}

	private void getSplitStr(String temp, Map<String, String> map) {
		if (StringUtil.isEmpty(temp))
			return;

		String key = null;
		String value = null;
		if (temp.indexOf(":") > -1) {
			key = temp.split(":").length > 0 ? temp.split(":")[0] : null;
			value = temp.split(":").length > 0 ? temp.split(":")[1] : null;
		}
		if (!StringUtil.isEmpty(key)) {
			map.put(key, value);
		}

	}


	/**
	 * 设置发票类型 1 普票 2专票 3未开票
	 *
	 * @param pk_corp
	 * @param vo
	 * @return
	 */
	protected Integer getFp_style(String pk_corp, OcrInvoiceVO vo) {
		// 发票类型 如果 无发票类型
		String invoicetype = vo.getInvoicetype();
		if (StringUtil.isEmpty(invoicetype)) {
			CorpVO corpvo = corpService.queryByPk(pk_corp);

			String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人"
					: corpvo.getChargedeptname();

			if ("小规模纳税人".equals(chargedeptname)) {
				// 默认普票
				return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
			} else {
				// 默认专票
				return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
			}
		} else {
			if (invoicetype.indexOf(ImageTypeConst.ORDINARY_INVOICE_SHORTNAME) > -1
					|| invoicetype.indexOf(ImageTypeConst.ORDINARY_INVOICE_NAME) > -1
					|| invoicetype.equals(ImageTypeConst.ORDINARY_INVOICE_CODE)) {// 增值税普通发票：04
				return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
			} else if (invoicetype.indexOf(ImageTypeConst.SPECIA_INVOICE_SHORTNAME) > -1
					|| invoicetype.indexOf(ImageTypeConst.SPECIA_INVOICE_NAME) > -1
					|| invoicetype.equals(ImageTypeConst.SPECIA_INVOICE_CODE)) {// 增值税专用发票：01
				return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
			} else {
				CorpVO corpvo = corpService.queryByPk(pk_corp);

				String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人"
						: corpvo.getChargedeptname();

				if ("小规模纳税人".equals(chargedeptname)) {
					// 默认普票
					return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
				} else {
					// 默认专票
					return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
				}
			}
		}
	}

	@Override
	public void getTzpzBVOList(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo, OcrInvoiceDetailVO[] details,
                               List<TzpzBVO> tblist, YntCpaccountVO[] accounts) {
		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());

		if (!corpvo.getIshasaccount().booleanValue()) {
			throw new BusinessException("公司没有建账!");
		}

		if (accounts == null || accounts.length == 0) {
			throw new BusinessException("获取公司科目出错!");
		}

		// 根据模板的行对应科目 判断是否启用存货辅助
		if (VATInvoiceTypeConst.VAT_SALE_INVOICE == vo.getItype()) {
			// 销项发票
			getModelVATTzpzBVO(hvo, models, vo, details, tblist, accounts, corpvo);
		} else if (VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) {
			// 进项发票
			getModelVATTzpzBVO(hvo, models, vo, details, tblist, accounts, corpvo);
		} else {
			throw new BusinessException("增值税发票类型出错");
		}
	}

	private void getModelVATTzpzBVO(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo,
			OcrInvoiceDetailVO[] details, List<TzpzBVO> tblist, YntCpaccountVO[] accounts, CorpVO corpvo) {

		int len = models.length;

		int counter = 0;

		for (int i = 0; i < len; i++) {
			if ("hsmny".equals(models[i].getVfield())) {// 含税金额
				counter++;
			}
		}
		/// 判断是否按照摘要去匹配模板 （模板上存在取数字段为金额的 超过2个的）
		if (counter <= 1) {
			getTzpzBVO(hvo, models, vo, details, tblist, accounts, corpvo);
		} else {
			getTzpzBVOMatchZy(hvo, models, vo, details, tblist, accounts, corpvo);
		}
	}

	private void getTzpzBVOMatchZy(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo, OcrInvoiceDetailVO[] details,
			List<TzpzBVO> tblist1, YntCpaccountVO[] accounts, CorpVO corpvo) {
		int len = models.length;

		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");
		String newrule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
		List<TzpzBVO> tblist = new ArrayList<>();
		if (details == null || details.length == 0 || vo.getIsinterface() == null
				|| !vo.getIsinterface().booleanValue()) {
			for (int i = 0; i < len; i++) {
				models[i].setRowno(i);
				String kmnew = null;
				if (IDefaultValue.DefaultGroup.equals(models[i].getPk_corp())) {
					kmnew = gl_accountcoderule.getNewRuleCode(models[i].getKmbm(), DZFConstant.ACCOUNTCODERULE,
							newrule);
				} else {
					kmnew = models[i].getKmbm();
				}

				YntCpaccountVO account = getAccountByCode(kmnew, accounts,newrule);

				if (account == null)
					throw new BusinessException("获取科目出错！");
				accounts = accountService.queryByPk(vo.getPk_corp());
				TzpzBVO entry = getVATTzpzBVO(hvo, account, models[i], vo, corpvo, null, null, accounts, newrule);
				tblist.add(entry);
			}
		} else {
			Map<String, TzpzBVO> map = null;
			int dlen = details.length;//
			List<String> matchList = new ArrayList<>();
			DcModelBVO lastmodel = null;
			for (int i = 0; i < len; i++) {
				models[i].setRowno(i);
				String kmnew = null;
				if (IDefaultValue.DefaultGroup.equals(models[i].getPk_corp())) {
					kmnew = gl_accountcoderule.getNewRuleCode(models[i].getKmbm(), DZFConstant.ACCOUNTCODERULE,
							newrule);
				} else {
					kmnew = models[i].getKmbm();
				}

				YntCpaccountVO account = getAccountByCode(kmnew, accounts,newrule);

				if (account == null)
					throw new BusinessException("获取科目出错！");
				map = new LinkedHashMap<String, TzpzBVO>();

				for (OcrInvoiceDetailVO dvo : details) {
					accounts = accountService.queryByPk(vo.getPk_corp());
					if ("hsmny".equals(models[i].getVfield())) {// 金额
						String zy = models[i].getZy();
						if (StringUtil.isEmpty(zy)) {
							continue;
						} else {
							if (StringUtil.isEmpty(dvo.getInvname()))
								continue;

							if (dvo.getInvname().contains(zy)) {
								TzpzBVO entry = getVATTzpzBVO(hvo, account, models[i], vo, corpvo, dvo, details[0],
										accounts, newrule);
								combinTzpzBVO(map, entry);
								matchList.add(dvo.getPk_invoice_detail());
								lastmodel = models[i];
							}
						}
					} else {
						accounts = accountService.queryByPk(vo.getPk_corp());
						TzpzBVO entry = getVATTzpzBVO(hvo, account, models[i], vo, corpvo, dvo, details[0], accounts,
								newrule);
						combinTzpzBVO(map, entry);
					}
				}
				for (TzpzBVO entry : map.values()) {
					tblist.add(entry);
				}
			}

			if (dlen > matchList.size()) {
				hvo.setIautorecognize(0);
				if (lastmodel == null) {
					for (int i = 0; i < len; i++) {
						if ("hsmny".equals(models[i].getVfield())) {// 金额或价税合计
							lastmodel = models[i];
							break;
						}
					}
				}

				String kmnew = null;
				if (IDefaultValue.DefaultGroup.equals(lastmodel.getPk_corp())) {
					kmnew = gl_accountcoderule.getNewRuleCode(lastmodel.getKmbm(), DZFConstant.ACCOUNTCODERULE,
							newrule);
				} else {
					kmnew = lastmodel.getKmbm();
				}

				YntCpaccountVO account = getAccountByCode(kmnew, accounts,newrule);

				if (account == null)
					throw new BusinessException("获取科目出错！");

				map = new LinkedHashMap<String, TzpzBVO>();
				for (OcrInvoiceDetailVO dvo : details) {
					accounts = accountService.queryByPk(vo.getPk_corp());
					if (!matchList.contains(dvo.getPk_invoice_detail())) {
						TzpzBVO entry = getVATTzpzBVO(hvo, account, lastmodel, vo, corpvo, dvo, details[0], accounts,
								newrule);
						combinTzpzBVO(map, entry);
					}
				}
				for (TzpzBVO entry : map.values()) {
					tblist1.add(entry);
				}
			}
		}
		tblist1.addAll(tblist);
	}

	private void getTzpzBVO(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo, OcrInvoiceDetailVO[] details,
			List<TzpzBVO> tblist, YntCpaccountVO[] accounts, CorpVO corpvo) {

		int len = models.length;

		Map<String, TzpzBVO> map = null;
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");
		// 根据模板的行对应科目 判断是否启用存货辅助
		String newrule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
		for (int i = 0; i < len; i++) {
			models[i].setRowno(i);
			String kmnew = null;
			if (IDefaultValue.DefaultGroup.equals(models[i].getPk_corp())) {
				kmnew = gl_accountcoderule.getNewRuleCode(models[i].getKmbm(), DZFConstant.ACCOUNTCODERULE, newrule);
			} else {
				kmnew = models[i].getKmbm();
			}

			YntCpaccountVO account = getAccountByCode(kmnew, accounts,newrule);

			if (account == null)
				throw new BusinessException("获取科目出错！");

			// if (i == row) {
			// 销售收入、 提供劳务
			if (details == null || details.length == 0 || vo.getIsinterface() == null
					|| !vo.getIsinterface().booleanValue()) {
				accounts = accountService.queryByPk(vo.getPk_corp());
				TzpzBVO entry = getVATTzpzBVO(hvo, account, models[i], vo, corpvo, null, null, accounts, newrule);
				tblist.add(entry);
			} else {
				map = new LinkedHashMap<String, TzpzBVO>();
				for (OcrInvoiceDetailVO dvo : details) {
					accounts = accountService.queryByPk(vo.getPk_corp());
					TzpzBVO entry = getVATTzpzBVO(hvo, account, models[i], vo, corpvo, dvo, details[0], accounts,
							newrule);
					combinTzpzBVO(map, entry);
				}
				for (TzpzBVO entry : map.values()) {
					tblist.add(entry);
				}
			}
		}
	}

	private void combinTzpzBVO(Map<String, TzpzBVO> map, TzpzBVO entry) {
		String key = constructTzpzKey(entry);
		if (!map.containsKey(key)) {
			if (entry.getNnumber() != null) {
				if (entry.getDfmny() != null && entry.getDfmny().doubleValue() > 0) {
					entry.setNprice(SafeCompute.div(entry.getDfmny(), entry.getNnumber()));
				}
				if (entry.getJfmny() != null && entry.getJfmny().doubleValue() > 0) {
					entry.setNprice(SafeCompute.div(entry.getJfmny(), entry.getNnumber()));
				}
			}
			map.put(key, entry);
		} else {
			TzpzBVO temp = map.get(key);
			if (temp != null) {
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), entry.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), entry.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), entry.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), entry.getYbjfmny()));
				if (entry.getNnumber() != null) {
					temp.setNnumber(SafeCompute.add(temp.getNnumber(), entry.getNnumber()));
					if (temp.getDfmny() != null && temp.getDfmny().doubleValue() > 0) {
						temp.setNprice(SafeCompute.div(temp.getDfmny(), temp.getNnumber()));
					}
					if (temp.getJfmny() != null && temp.getJfmny().doubleValue() > 0) {
						temp.setNprice(SafeCompute.div(temp.getJfmny(), temp.getNnumber()));
					}
				}
			}
		}

	}

	private TzpzBVO getVATTzpzBVO(TzpzHVO hvo, YntCpaccountVO account, DcModelBVO model, OcrImageLibraryVO vo,
			CorpVO corpvo, OcrInvoiceDetailVO dvo, OcrInvoiceDetailVO firstdvo, YntCpaccountVO[] accounts,
			String newrule) {
		if (account == null)
			throw new BusinessException("根据编码获取公司科目出错!");

		TzpzBVO entry = new TzpzBVO();

		if (vo.getInvoicetype().endsWith(ImageTypeConst.MOTOR_INVOICE)) {
			if (dvo != null)
				dvo.setItemunit("辆");
		}
		getVATInvoiceAccountVO(hvo, account, model, vo, corpvo, dvo, entry, accounts, newrule);

		setDefaultValue(entry, model, vo, dvo, accounts);

		if (firstdvo != null) {

			if (VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) {
//				&& VATInvoiceTypeConst.VAT_ORDINARY_INVOICE == vo.getIfpkind()
				// 所有的进项普票不匹配税目
			} else {

				DZFDouble taxratio = DZFDouble.ZERO_DBL;
				String invname = null;
				if (dvo == null) {
					invname = vo.getInvname();
					taxratio = SafeCompute.div(getSubMny(vo.getNtax()), new DZFDouble(100));
				} else {
					invname = dvo.getInvname();
					taxratio = SafeCompute.div(getSubMny(dvo.getItemtaxrate()), new DZFDouble(100));
				}
				TaxitemParamVO taxparam = new TaxitemParamVO.Builder(entry.getPk_corp(), taxratio)
						.UserId(vo.getCoperatorid()).InvName(invname).Fp_style(vo.getIfpkind()).build();
				TaxItemUtil.dealTaxItem(entry, taxparam, account);
			}
		}

		return entry;

	}

	private void getVATInvoiceAccountVO(TzpzHVO hvo, YntCpaccountVO account, DcModelBVO model, OcrImageLibraryVO vo,
			CorpVO corpvo, OcrInvoiceDetailVO dvo, TzpzBVO entry, YntCpaccountVO[] accounts, String newrule) {
		String pk_corp = corpvo.getPk_corp();
		String name = null;
		String payer = null;
		// 地址
		String address = null;
		// 开户行
		String bank = null;
		if ("smny".equals(model.getVfield())) {
			// 税额 直接取第一个下级的最末级 因为税额一般都设置末级科目 不会新增下级的
			account = getFisrtNextLeafAccount(account, pk_corp, accounts);
		} else {
			// 销售
			if (VATInvoiceTypeConst.VAT_SALE_INVOICE == vo.getItype()) {
				// 贷 主营业务收入 // // 借 应收账款 // 启用客户辅助 匹配辅助
				payer = vo.getVpurchtaxno();
				name = vo.getVpurchname();// 付款方名称
				address = vo.getVpurphoneaddr();
				bank = vo.getVpuropenacc();
			} else if (VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) { // 采购
				// 借 库存 贷 应付 // 启用供应商辅助
				payer = vo.getVsaletaxno();
				name = vo.getVsalename();// 收款方名称
				address = vo.getVsalephoneaddr();
				bank = vo.getVsaleopenacc();
			}

			boolean isleaf = account.getIsleaf() == null ? false : account.getIsleaf().booleanValue();

			// 如果当前科目为末级 则判断是否启用辅助 没启用 则为当前科目 不加辅助
			if (isleaf) {
				// 启用辅助
				// 启用存货辅助
				if (account.getIsfzhs().charAt(5) == '1') {
					getInvFz(account, vo, corpvo, dvo, entry, accounts);
				}

				// 启用供应商辅助
				if (account.getIsfzhs().charAt(1) == '1') {
					AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.SUPPLIER.getValue(), name, payer,
							address, bank);
					if (bvo != null) {
						entry.setAttributeValue("fzhsx2", bvo.getPk_auacount_b());
					}
				}
				// 启用客户辅助 匹配辅助
				if (account.getIsfzhs().charAt(0) == '1') {
					AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.CUSTOMER.getValue(), name, payer,
							address, bank);
					if (bvo != null) {
						entry.setAttributeValue("fzhsx1", bvo.getPk_auacount_b());
					}
				}
			} else {
				// 如果当前科目非末级 则一定有下级 直接放到当前科目的2级

				// 先匹配下下级所有末级科目 看是否能匹配上 如果匹配上 则返回 否则新增该科目的下级

				// 存货类科目(销售收入 库存商品)
				if (account.getAccountcode().startsWith("5001") || account.getAccountcode().startsWith("6001")
						|| account.getAccountcode().startsWith("1405")) {
					if (dvo != null) {
						name = dvo.getInvname();
					} else {
						name = vo.getInvname();
					}
					YntCpaccountVO account1 = matchInvAccount(vo, account, name, accounts, newrule);
					if (account1 != null) {
						account = account1;
					}
				} else if (isMatchAccountCode(account)) {
					// 供应商 客户类科目（应付账款 应收账款）
					// 无客户供应商辅助 匹配明细科目
					YntCpaccountVO account1 = matchAccount(vo, account, name, accounts, newrule);
					if (account1 != null) {
						account = account1;
					}
				}
			}
		}

		if (account.getIsnum() != null && account.getIsnum().booleanValue()) {
			if (dvo != null) {
				entry.setNnumber(getSubMny(dvo.getItemamount()));
				entry.setNprice(getSubMny(dvo.getItemprice()));
			}
		}
		entry.setVname(account.getAccountname());
		entry.setSubj_allname(account.getFullname());
		entry.setKmmchie(account.getFullname());
		entry.setVcode(account.getAccountcode());
		entry.setSubj_code(account.getAccountcode());
		entry.setPk_accsubj(account.getPk_corp_account());

		if (account.getIswhhs() != null && account.getIswhhs().booleanValue()) {
			entry.setExc_pk_currency(account.getExc_pk_currency());
		}
	}

	private boolean isMatchAccountCode(YntCpaccountVO account) {

		String[] accCodes = new String[] { "1121", "1122", "1123", "1221", "2201", "2202", "2203", "2241" };
		boolean isMatch = false;
		for (String accCode : accCodes) {
			if (account.getAccountcode().startsWith(accCode)) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}

	private String getInvVO(YntCpaccountVO account, OcrImageLibraryVO vo, CorpVO corpvo, OcrInvoiceDetailVO dvo,
			YntCpaccountVO[] accounts) {
		// 如果启用库存

		if (vo.getInvoicetype().endsWith(ImageTypeConst.MOTOR_INVOICE)) {
			dvo.setItemunit("辆");
		}

		String invid = null;
		if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			InventoryVO bvo = matchInvtoryIC(account, vo, corpvo, dvo, accounts, false);
			if (bvo != null) {
				invid = bvo.getPk_inventory();
			}
		} else {
			// 不启用库存
			AuxiliaryAccountBVO bvo = matchInvtoryNoIC(vo, corpvo, dvo, false);
			if (bvo != null) {
				invid = bvo.getPk_auacount_b();
			}
		}
		return invid;
	}

	private void getInvFz(YntCpaccountVO account, OcrImageLibraryVO vo, CorpVO corpvo, OcrInvoiceDetailVO dvo,
			TzpzBVO entry, YntCpaccountVO[] accounts) {
		// 如果启用库存
		if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			InventoryVO bvo = matchInvtoryIC(account, vo, corpvo, dvo, accounts, true);
			if (bvo != null) {
				entry.setPk_inventory(bvo.getPk_inventory());
			}
		} else {
			// 不启用库存
			AuxiliaryAccountBVO bvo = matchInvtoryNoIC(vo, corpvo, dvo, true);
			if (bvo != null) {
				entry.setFzhsx6(bvo.getPk_auacount_b());
			}
		}
	}

	// 匹配存货辅助 匹配不上新建
	private InventoryVO matchInvtoryIC(YntCpaccountVO account, OcrImageLibraryVO vo, CorpVO corpvo,
			OcrInvoiceDetailVO dvo, YntCpaccountVO[] accounts, boolean issave) {

		if (corpvo == null)
			return null;
		InventoryVO invvo = null;
		MeasureVO meavo = null;
		String pk_measure = null;

		String pk_inventory = dvo != null ? dvo.getPk_inventory() : "";

		if (!StringUtil.isEmpty(pk_inventory)) {
			invvo = invservice.queryByPrimaryKey(pk_inventory);// 根据主键直接查询
		} else if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue() && dvo != null) {

			meavo = getMeasureVO(vo, corpvo, dvo);
			if (meavo != null)
				pk_measure = meavo.getPk_measure();
			invvo = ocr_atuomatch.getInventoryVOByName(dvo.getInvname(), dvo.getInvtype(), pk_measure,
					corpvo.getPk_corp());
		} else {
			invvo = ocr_atuomatch.autoMatchInventoryVO(vo.getInvname(), MatchTypeEnum.INVENTORY.getValue(),
					corpvo.getPk_corp());
		}
		if (!issave) {

		} else {
			ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
			String newrule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
			if (invvo == null && ((dvo != null && !StringUtil.isEmpty(dvo.getInvname()))
					|| !StringUtil.isEmpty(vo.getInvname()))) {
				invvo = new InventoryVO();
				invvo.setPk_corp(corpvo.getPk_corp());
				invvo.setCreatetime(new DZFDateTime());
				if (meavo != null)
					invvo.setPk_measure(pk_measure);
				invvo.setCreator(vo.getCoperatorid());
				if (dvo != null) {
					invvo.setInvspec(dvo.getInvtype());
					invvo.setName(dvo.getInvname());
				} else {
					invvo.setName(vo.getInvname());
				}
				invvo.setCode(yntBoPubUtil.getInventoryCode(corpvo.getPk_corp()));
				String kcsp = gl_cbconstant.getKcsp_code();
				String ycl = gl_cbconstant.getYcl_code();

				String kmbm = kcsp;
				if (account != null) {
					kmbm = account.getAccountcode();
					if (!StringUtil.isEmpty(kmbm)) {
						if (kmbm.startsWith(kcsp) || kmbm.startsWith(ycl)) {
							invvo.setPk_subject(account.getPk_corp_account());
						}
					}
				}

				if (StringUtil.isEmpty(invvo.getPk_subject())) {
					YntCpaccountVO acc = getAccountByCode(kmbm, accounts,newrule);
					acc = getFisrtNextLeafAccount(acc, corpvo.getPk_corp(), accounts);
					invvo.setPk_subject(acc.getPk_corp_account());
				}
				invservice.save(corpvo.getPk_corp(), new InventoryVO[] { invvo });
			}
		}
		return invvo;
	}

	private AuxiliaryAccountBVO matchInvtoryNoIC(OcrImageLibraryVO vo, CorpVO corpvo, OcrInvoiceDetailVO dvo,
			boolean issave) {
		if (corpvo == null || vo == null)
			return null;
		AuxiliaryAccountBVO bvo = null;
		String pk_auacount_h = AuxiliaryConstant.ITEM_INVENTORY;

		String pk_inventory = dvo != null ? dvo.getPk_inventory() : "";

		if (!StringUtil.isEmpty(pk_inventory)) {
			bvo = gl_fzhsserv.queryBByID(pk_inventory, corpvo.getPk_corp());
		} else if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue() && dvo != null) {
			bvo = ocr_atuomatch.getAuxiliaryAccountBVOByInfo(dvo.getInvname(), dvo.getInvtype(), dvo.getItemunit(),
					corpvo.getPk_corp(), pk_auacount_h);
		} else {
			bvo = ocr_atuomatch.autoMatchAuxiliaryAccount(vo.getInvname(), MatchTypeEnum.INVENTORY.getValue(),
					corpvo.getPk_corp());
		}

		// 如果 匹配存货辅助 匹配不上新建
		if (!issave) {

		} else {
			if (bvo == null && ((dvo != null && StringUtil.isEmpty(dvo.getInvname()))
					|| !StringUtil.isEmpty(vo.getInvname()))) {
				bvo = new AuxiliaryAccountBVO();
				bvo.setCode(yntBoPubUtil.getFZHsCode(corpvo.getPk_corp(), pk_auacount_h));
				if (dvo != null) {
					bvo.setUnit(dvo.getItemunit());
					bvo.setSpec(dvo.getInvtype());
					bvo.setName(dvo.getInvname());
				} else {
					bvo.setName(vo.getInvname());
				}
				bvo.setPk_corp(corpvo.getPk_corp());
				bvo.setDr(0);
				bvo.setPk_auacount_h(AuxiliaryConstant.ITEM_INVENTORY);
				bvo = gl_fzhsserv.saveB(bvo);
			}
		}
		return bvo;
	}

	private MeasureVO getMeasureVO(OcrImageLibraryVO vo, CorpVO corpvo, OcrInvoiceDetailVO dvo) {
		// 查找计量单位

		if (StringUtil.isEmpty(dvo.getItemunit())) {
			return new MeasureVO();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? and nvl(dr,0)=0 and name = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		sp.addParam(dvo.getItemunit());
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		MeasureVO meavo = null;
		if (listVo == null || listVo.size() == 0) {
			meavo = new MeasureVO();
			meavo.setPk_corp(corpvo.getPk_corp());
			meavo.setCreatetime(new DZFDateTime());
			meavo.setCreator(vo.getCoperatorid());
			meavo.setName(dvo.getItemunit());
			meavo.setCode(yntBoPubUtil.getMeasureCode(corpvo.getPk_corp()));
			listVo = new ArrayList<>();
			listVo.add(meavo);
			measervice.updateVOArr(corpvo.getPk_corp(), vo.getCoperatorid(), listVo);
		} else {
			meavo = listVo.get(0);
		}
		return meavo;
	}

	// 收购类发票
	@Override
	protected void speaclDealVO(OcrInvoiceVO vo, OcrInvoiceDetailVO[] details) {
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		if (vo == null || StringUtil.isEmpty(vo.getVmemo()))
			return;

		if (vo == null || details == null || details.length == 0)
			return;

		if (!vo.getVmemo().contains("收购(左上角标志)"))
			return;

		DZFDouble rate = new DZFDouble(0.11);
		DZFDouble taxmny = DZFDouble.ZERO_DBL;
		DZFDouble itemmmny = DZFDouble.ZERO_DBL;
		DZFDouble nmny = DZFDouble.ZERO_DBL;
		DZFDouble ntaxnmny = DZFDouble.ZERO_DBL;
		DZFDouble ntotaltax = DZFDouble.ZERO_DBL;
		for (OcrInvoiceDetailVO detail : details) {
			itemmmny = getSubMny(detail.getItemmny());
			ntotaltax = SafeCompute.add(itemmmny, ntotaltax);

			taxmny = SafeCompute.multiply(itemmmny, rate).setScale(2, 0);
			detail.setItemtaxmny(taxmny.toString());
			ntaxnmny = SafeCompute.add(taxmny, ntaxnmny);
			detail.setItemtaxrate("0.11");

			itemmmny = SafeCompute.sub(itemmmny, taxmny);
			nmny = SafeCompute.add(nmny, itemmmny);
			detail.setItemmny(itemmmny.toString());
			detail.setItemprice(
					SafeCompute.div(itemmmny, getSubMny(detail.getItemamount())).setScale(iprice, 0).toString());
		}
		vo.setNmny(nmny.toString());
		vo.setNtaxnmny(ntaxnmny.toString());
		vo.setNtotaltax(ntotaltax.toString());
	}

	@Override
	protected void throwBusinessException() {
		throw new BusinessException("票据公司与当前公司不匹配");
	}

	@Override
	protected void setSpeaclTzpzBVO(TzpzHVO hvo, OcrImageLibraryVO vo, List<TzpzBVO> tblist) {

		if (tblist == null || tblist.size() == 0)
			return;

		if (StringUtil.isEmpty(vo.getVmemo())) {
			return;
		}
		DZFDouble exrate = null;
		if (vo.getVmemo().contains("汇    率：")) {
			int index = vo.getVmemo().indexOf("汇    率：");
			String srate = vo.getVmemo().substring(index + 7, index + 13);
			try {
				exrate = new DZFDouble(srate);
			} catch (Exception e) {
				exrate = DZFDouble.ZERO_DBL;
			}
		}

		if (exrate != null && exrate.doubleValue() > 0) {
			DZFDouble ybmny = DZFDouble.ZERO_DBL;
			List<TzpzBVO> tblist1 = new ArrayList<>();
			for (TzpzBVO bvo : tblist) {

				if ((bvo.getDfmny() == null || bvo.getDfmny().doubleValue() == 0)
						&& (bvo.getJfmny() == null || bvo.getJfmny().doubleValue() == 0)) {
					continue;
				}
				tblist1.add(bvo);
				if (StringUtil.isEmpty(bvo.getExc_pk_currency()))
					continue;
				bvo.setNrate(exrate);
				if (bvo.getVdirect().intValue() == 0) {// 借方
					ybmny = SafeCompute.div(bvo.getJfmny(), exrate).setScale(2, 0);
					bvo.setYbjfmny(ybmny);
				} else {
					ybmny = SafeCompute.div(bvo.getDfmny(), exrate).setScale(2, 0);
					bvo.setYbdfmny(ybmny);
				}
			}
			tblist.clear();
			tblist.addAll(tblist1);
		} else {
			for (TzpzBVO bvo : tblist) {
				if (!StringUtil.isEmpty(bvo.getExc_pk_currency())) {
					bvo.setExc_pk_currency(null);
				}
			}
		}
	}

	@Override
	public TzpzHVO saveTzpzVO(ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo, boolean isRecog)
			throws DZFWarpException {
		try {

			// 需要判断是否存在银行对账单 如果存在 没有生成凭证 不生成凭证 已经生成凭证 关联凭证
			if (hvo.getIfptype() == VATInvoiceTypeConst.VAT_SALE_INVOICE) { // 销项
				image2bill_vatsale.saveBill(corpvo, hvo, invvo, grpvo, isRecog);
			} else if (hvo.getIfptype() == VATInvoiceTypeConst.VAT_INCOM_INVOICE) {// 进项
				image2bill_vatincom.saveBill(corpvo, hvo, invvo, grpvo, isRecog);
			}
		} catch (Exception e) {
			log.error("错误",e);
			hvo.setPk_tzpz_h(null);
			throw new BusinessException(e.getMessage());
		}
		super.saveTzpzVO(grpvo, invvo, corpvo, hvo, isRecog);
		return hvo;
	}
}
