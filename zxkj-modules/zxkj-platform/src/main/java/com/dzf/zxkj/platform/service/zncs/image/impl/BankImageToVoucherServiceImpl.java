package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.IAutoMatchName;
import com.dzf.zxkj.platform.service.zncs.image.IImage2BillServiceImpl;
import com.dzf.zxkj.platform.util.zncs.MatchTypeEnum;
import com.dzf.zxkj.platform.util.zncs.TransPjlxTypeModel;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

// 银行回单
@Service("aitovoucherserv_bank")
public class BankImageToVoucherServiceImpl extends DefaultImageToVoucherServiceImpl {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAutoMatchName ocr_atuomatch;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	@Autowired
	private ICpaccountService cpaccountService;

	@Autowired
	private MatchBankKeyWords match_bankwords;

	@Autowired
	@Qualifier("image2bill_bank")
	private IImage2BillServiceImpl image2bill_bank;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;


	@Override
	public DcModelBVO[] getDcModelBVO(TzpzHVO hvo, ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo) {
		DcModelBVO[] models = null;
		if (invvo == null) {
			return null;
		} else {
			DcModelHVO headvo = match_bankwords.getMatchModel(invvo, corpvo);
			if (headvo != null) {
				models = (DcModelBVO[]) headvo.getChildren();
				int ifptype = matchType(invvo, corpvo);
				hvo.setIfptype(ifptype);
				hvo.setIautorecognize(1);
				// 更新model 信息
				invvo.setKeywords(headvo.getKeywords());
				invvo.setPk_model_h(headvo.getPk_model_h());
				// singleObjectBO.update(invvo, new String[] { "pk_model_h",
				// "keywords" });
				updateKeyWord(invvo, headvo);
			}
			//
			// String invtype = invvo.getInvoicetype();
			// if (!StringUtil.isEmpty(invtype)) {
			// invtype = invtype.substring(1, invtype.length());
			// invtype = invvo.getVsaleopenacc() + invtype; //银行名称 + 单据类型
			// models = getCorpOrGroupModelByKeyWord(hvo, invtype, invvo,
			// corpvo);
			// }
		}

		if (models != null && models.length > 0) {
			hvo.setIautorecognize(1);
		} else {
			// 收款回单
			Map<String, String> trmap = TransPjlxTypeModel.tranPjlx(4, corpvo.getChargedeptname());
			models = getCorpOrGroupModel(trmap, corpvo);
		}

		return models;
	}

	private DcModelBVO[] getCorpOrGroupModelByKeyWord(TzpzHVO hvo, String businame, OcrInvoiceVO invvo, CorpVO corpvo) {
		DcModelBVO[] models = getModelByKeyWord(hvo, businame, invvo, corpvo, corpvo.getPk_corp());
		if (models == null || models.length == 0) {
			models = getModelByKeyWord(hvo, businame, invvo, corpvo, IDefaultValue.DefaultGroup);
		}
		return models;
	}

	private DcModelBVO[] getModelByKeyWord(TzpzHVO hvo, String businame, OcrInvoiceVO invvo, CorpVO corpvo,
			String pk_corp) {
		List<DcModelHVO> dcmodelhList = null;
		DcModelBVO[] bvos = null;
		DcModelHVO[] headvo = null;

		if (!IDefaultValue.DefaultGroup.equals(pk_corp)) {
			headvo = queryGsModelHVOByKeyWord(pk_corp, businame);
		}

		if (headvo == null || headvo.length == 0) {
			headvo = queryJtModelHVOByKeyWord(pk_corp, corpvo, businame);
		}
		if (headvo != null && headvo.length > 0) {
			dcmodelhList = new ArrayList<DcModelHVO>(Arrays.asList(headvo));
		}
		dcmodelhList = filterByDcModel(hvo, dcmodelhList, invvo, corpvo, pk_corp);

		if (dcmodelhList != null && dcmodelhList.size() > 0) {
			List<DcModelBVO> models = dcpzjmbserv.queryByPId(dcmodelhList.get(0).getPk_model_h(), pk_corp);
			if (models != null && models.size() > 0) {// 排序
				Collections.sort(models, new Comparator<DcModelBVO>() {
					@Override
					public int compare(DcModelBVO o1, DcModelBVO o2) {
						return o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
					}
				});
				bvos = models.toArray(new DcModelBVO[0]);
			}
		}
		return bvos;
	}

	private List<DcModelHVO> filterByDcModel(TzpzHVO hvo, List<DcModelHVO> dcmodelhList, OcrInvoiceVO invvo,
			CorpVO corpvo, String pk_corp) {
		if (dcmodelhList == null || dcmodelhList.size() == 0)
			return null;

		List<DcModelHVO> hlist = new ArrayList<>();
		// 匹配顺序 按照关键字 按照公司个人 按照名字
		String[] keyWords = getKeyWord();
		String invtype = invvo.getInvoicetype();
		if (!StringUtil.isEmpty(invtype)) {
			invtype = invtype.substring(1, invtype.length());
			invtype = invvo.getVsaleopenacc() + invtype;
		}

		for (DcModelHVO dhvo : dcmodelhList) {
			for (String key : keyWords) {
				String keyinfo = getBankKeyWord(invvo, key);
				if (StringUtil.isEmpty(keyinfo))
					continue;
				String keytype = invtype + keyinfo;
				String bkey = dhvo.getBusitypetempname() + dhvo.getKeywords();
				if (bkey.equals(keytype))
					hlist.add(dhvo);
			}
		}

		if (hlist == null || hlist.size() == 0) {
			for (DcModelHVO dhvo : dcmodelhList) {
				String tmptype = matchInvoiceType(hvo, invtype, invvo, corpvo);
				if (!StringUtil.isEmpty(tmptype)) {
					String keytype = invtype + tmptype;
					if (dhvo.getBusitypetempname().equals(keytype)) {
						if (StringUtil.isEmpty(dhvo.getKeywords())) {
							hlist.add(dhvo);
						}
					}
				}
			}
		}
		if (hlist == null || hlist.size() == 0) {
			for (DcModelHVO dhvo : dcmodelhList) {
				if (invtype.equals(dhvo.getBusitypetempname())) {
					if (StringUtil.isEmpty(dhvo.getKeywords())) {
						hlist.add(dhvo);
					}
				}
			}
		}

		return hlist;
	}

	private String[] getKeyWord() {
		return new String[] { "摘要", "用途", "备注", "附言" };
	}

	private String getBankKeyWord(OcrInvoiceVO vo, String key) {
		// 获取摘要信息
		try {
			if (vo != null && !StringUtil.isEmpty(vo.getVsalephoneaddr())) {
				String vsalephoneaddr = vo.getVsalephoneaddr();
				vsalephoneaddr = vsalephoneaddr.replaceAll("”", "'");

				JSONObject rowobject = JSON.parseObject(vsalephoneaddr);
				if (rowobject != null && rowobject.size() > 0) {
					String zy = (String) rowobject.get(key);
					return zy;
				}
			}
		} catch (Exception e) {
			throw new BusinessException("JSON数据格式出错");
		}
		return null;
	}

	private String matchInvoiceType(TzpzHVO hvo, String invtype, OcrInvoiceVO invvo, CorpVO corpvo) {

		int ifptype = VATInvoiceTypeConst.VAT_BANK_INVOICE;
		String corpname = null;
		if (matchPayType(invtype)) {
			ifptype = VATInvoiceTypeConst.VAT_PAY_INVOICE;
			corpname = invvo.getVsalename();
		} else if (matchReceType(invtype)) {
			ifptype = VATInvoiceTypeConst.VAT_RECE_INVOICE;
			corpname = invvo.getVpurchname();
		} else if (matchPayorReceType(invtype)) {
			ifptype = matchType(invvo, corpvo);
			if (ifptype == VATInvoiceTypeConst.VAT_RECE_INVOICE) {
				corpname = invvo.getVpurchname();
			} else if (ifptype == VATInvoiceTypeConst.VAT_PAY_INVOICE) {
				corpname = invvo.getVsalename();
			}
		}
		hvo.setIfptype(ifptype);
		invtype = isContainCorp(hvo, corpname);
		return invtype;
	}

	private boolean matchPayType(String invtype) {
		if ("中国农业银行(付款)".equals(invtype) || "中国农业银行收付款入账木料款".equals(invtype) || "中国工商银行(付款)".equals(invtype)
				|| "中国民生银行(付款)".equals(invtype) || "中国邮政储蓄银行付款凭证".equals(invtype) || "招商银行付款回单".equals(invtype)
				|| "重庆农村商业银行付款回单".equals(invtype) || "兴业银行付款回单".equals(invtype) || "上海浦东发展银行付款业务凭证".equals(invtype)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean matchReceType(String invtype) {
		if ("中国农业银行(收款)".equals(invtype) || "中国工商银行(收款)".equals(invtype) || "中国民生银行(收款)".equals(invtype)
				|| "中国邮政储蓄银行收款凭证".equals(invtype) || "招商银行收款回单".equals(invtype) || "重庆农村商业银行收款回单".equals(invtype)
				|| "兴业银行收款回单".equals(invtype) || "上海浦东发展银行收款业务凭证".equals(invtype)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean matchPayorReceType(String invtype) {
		if ("中国农业银行入账货款".equals(invtype) || "中国农业银行客户收付款入账通知".equals(invtype) || "中国农业银行入账钢材款".equals(invtype)) {
			return true;
		} else {
			return false;
		}
	}

	// 增加收付款关键字
	private String isContainCorp(TzpzHVO hvo, String corpname) {

		String invtype = "";
		if (VATInvoiceTypeConst.VAT_PAY_INVOICE == hvo.getIfptype()) {
			invtype = "付款";
		} else if (VATInvoiceTypeConst.VAT_RECE_INVOICE == hvo.getIfptype()) {
			invtype = "收款";
		}

		if (StringUtil.isEmpty(corpname)) {
			return "";
		} else {
			if (corpname.contains("公司")) {
				return invtype + "公司";
			} else {
				return invtype + "个人";
			}
		}
	}

	// 判断收付款
	private int matchType(OcrInvoiceVO vo, CorpVO corpvo) {

		String saleName = filterName(vo.getVsalename());
		String purName = filterName(vo.getVpurchname());
		String corpName = filterName(corpvo.getUnitname());

		if (!StringUtil.isEmpty(purName) && purName.equals(corpName)) {
			return VATInvoiceTypeConst.VAT_PAY_INVOICE;
		} else if (!StringUtil.isEmpty(saleName) && saleName.equals(corpName)) {
			return VATInvoiceTypeConst.VAT_RECE_INVOICE;
		} else {
			return VATInvoiceTypeConst.VAT_BANK_INVOICE;
		}
	}

	@Override
	protected int matchImageType(OcrInvoiceVO vo, CorpVO corpvo, ImageGroupVO grpvo) {
		return VATInvoiceTypeConst.VAT_BANK_INVOICE;
	}

	/**
	 * 设置发票类型 1 普票 2专票 3未开票
	 * 
	 * @param pk_corp
	 * @param vo
	 * @return
	 */
	protected Integer getFp_style(String pk_corp, OcrInvoiceVO vo) {
		return null;
	}

	public void getTzpzBVOList(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo, OcrInvoiceDetailVO[] details,
							   List<TzpzBVO> tblist, YntCpaccountVO[] accounts) {
		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());

		if (!corpvo.getIshasaccount().booleanValue()) {
			throw new BusinessException("公司没有建账!");
		}

		if (accounts == null || accounts.length == 0) {
			throw new BusinessException("获取公司科目出错!");
		}
		// 银行回单

		String invtype = vo.getInvoicetype();
		if (!StringUtil.isEmpty(invtype) && (invtype.equals("b电子缴税付款凭证") ||invtype.equals("b社保"))&& !StringUtil.isEmpty(vo.getVmemo())) {
			getModelBankTzpzBVO1(hvo, models, vo, details, tblist, accounts, corpvo);
		} else {
			getModelBankTzpzBVO(hvo, models, vo, details, tblist, accounts);
		}
	}

	private void getModelBankTzpzBVO1(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo,
			OcrInvoiceDetailVO[] details, List<TzpzBVO> tblist, YntCpaccountVO[] accounts, CorpVO corpvo) {
		int len = models.length;

		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");

		// 税项明细
		String vmemo = vo.getVmemo();
		vmemo = vmemo.replaceAll("”", "'");
		JSONObject rowobject = null;
		try {
			rowobject = JSON.parseObject(vmemo);
		} catch (Exception e) {
			throw new BusinessException("JSON数据格式出错");
		}

		if (rowobject == null || rowobject.size() == 0) {
			getModelBankTzpzBVO(hvo, models, vo, details, tblist, accounts);
		} else {
			String newrule = gl_cpacckmserv.queryAccountRule(vo.getPk_corp());
			// 根据模板的行对应科目
			for (int i = 0; i < len; i++) {
				models[i].setRowno(i);

				String kmnew = null;
				if (IDefaultValue.DefaultGroup.equals(models[i].getPk_corp())) {
					kmnew = gl_accountcoderule.getNewRuleCode(models[i].getKmbm(), DZFConstant.ACCOUNTCODERULE,
							newrule);
				} else {
					kmnew = models[i].getKmbm();
				}

				YntCpaccountVO account = getAccountByCode(kmnew, accounts, newrule);

				if (account == null)
					throw new BusinessException("获取科目出错！");

				TzpzBVO entry = new TzpzBVO();
				getBankInvoiceAccountVO(hvo, account, models[i], vo, entry, accounts, newrule);

				if (models[i].getDirection() == 1) {
					setDefaultValue(entry, models[i], vo, null, accounts);
					tblist.add(entry);
				} else {
					YntCpaccountVO accvo = getTzpzAccount(entry.getPk_accsubj(), accounts);
					boolean isMatch = false;
					String value = null;
					String accName = accvo.getAccountname();

					DZFDouble nmny = DZFDouble.ZERO_DBL;

					String zy = null;
					String accname = null;
					for (String key : rowobject.keySet()) {
						
						Object o = rowobject.get(key);
						
						if(o  instanceof BigDecimal){
							value = ((BigDecimal)o).toString();
						}else if(o  instanceof String){
							value = (String) rowobject.get(key);
						}else{
							
						}
						accname = key.substring(1, key.length());
						key = getKeyWordAccName(accname, corpvo);

						if ("销项税额".equals(key)) {
							if (accName.contains(key)) {
								nmny = SafeCompute.add(nmny, getSubMny(value));
								vo.setNmny(nmny.toString());
								isMatch = true;
								zy = accname;
							}
						} else {
							if (accName.endsWith(key)) {
								nmny = SafeCompute.add(nmny, getSubMny(value));
								vo.setNmny(nmny.toString());
								isMatch = true;
								zy = accname;
							}
						}

					}
					if (isMatch) {
						models[i].setZy(zy);
						setDefaultValue(entry, models[i], vo, null, accounts);
						tblist.add(entry);
					}
				}
			}
			// 如果只匹配到一个数据 重新匹配全部模板
			if (tblist != null && tblist.size() == 1) {
				tblist.clear();
				getModelBankTzpzBVO(hvo, models, vo, details, tblist, accounts);
			} else if (tblist.size() == 2) {
				String zy = null;
				int i = 0;
				for (TzpzBVO bvo : tblist) {
					if (i == 1) {
						if (!StringUtil.isEmpty(zy))
							bvo.setZy(zy);
					}
					i++;
					zy = bvo.getZy();
				}
			}
		}
	}

	private String getKeyWordAccName(String key, CorpVO corpvo) {

		if ("增值税".equals(key)) {
			// 小规模就是销项税额，一般人是未交增值税
			String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人"
					: corpvo.getChargedeptname();
			if ("小规模纳税人".equals(chargedeptname)) {
				key = "销项税额";
			}
		} else if ("城市维护建设税".equals(key)) {
			key = "城建税";
		} else if (key.contains("医疗保险")) {
			key = "医疗保险";
		} else if (key.contains("养老保险")) {
			key = "养老保险";
		} else if (key.contains("工伤保险")) {
			key = "工伤保险";
		} else if (key.contains("失业保险")) {
			key = "失业保险";
		}  else if (key.contains("生育保险")) {
			key = "生育保险";
		} else if ("车辆购置税".equals(key)) {
			key = "其他";
		} else if ("水利建设专项收入".equals(key)) {
			key = "其他";
		} else if ("残疾人就业保障金".equals(key) ||"残保金".equals(key)) {
			key = "残保金";
		} else if ("城镇土地使用税".equals(key)) {
			key = "土地使用税";
		} 
		return key;

	}

	private void getModelBankTzpzBVO(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo,
			OcrInvoiceDetailVO[] details, List<TzpzBVO> tblist, YntCpaccountVO[] accounts) {
		int len = models.length;

		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");
		String newrule = gl_cpacckmserv.queryAccountRule(vo.getPk_corp());
		// 根据模板的行对应科目
		for (int i = 0; i < len; i++) {
			models[i].setRowno(i);

			String kmnew = null;
			if (IDefaultValue.DefaultGroup.equals(models[i].getPk_corp())) {
				kmnew = gl_accountcoderule.getNewRuleCode(models[i].getKmbm(), DZFConstant.ACCOUNTCODERULE, newrule);
			} else {
				kmnew = models[i].getKmbm();
			}
			models[i].setRowno(i);
			YntCpaccountVO account = getAccountByCode(kmnew, accounts, newrule);

			if (account == null)
				throw new BusinessException("获取科目出错！");

			TzpzBVO entry = new TzpzBVO();
			accounts = accountService.queryByPk(vo.getPk_corp());
			getBankInvoiceAccountVO(hvo, account, models[i], vo, entry, accounts, newrule);
			setDefaultValue(entry, models[i], vo, null, accounts);
			tblist.add(entry);

		}
	}

	private void getBankInvoiceAccountVO(TzpzHVO hvo, YntCpaccountVO account, DcModelBVO model, OcrImageLibraryVO vo,
			TzpzBVO entry, YntCpaccountVO[] accounts, String newrule) {

		boolean isleaf = account.getIsleaf() == null ? false : account.getIsleaf().booleanValue();
		// 银行类科目 支持 外币 银行辅助
		if (account != null && !StringUtil.isEmpty(account.getAccountcode())
				&& account.getAccountcode().startsWith("1002")) {
			String bankName = vo.getVsaleopenacc();

			String keyword = vo.getKeywords();
			if (!StringUtil.isEmpty(keyword)) {
				if (keyword.contains("户间转账")) {
					if ("付款银行".equals(model.getZy())) {
						if (!StringUtil.isEmpty(vo.getVpurbankname())) {
							bankName = vo.getVpurbankname();
						}
					} else if ("收款银行".equals(model.getZy())) {
						if (!StringUtil.isEmpty(vo.getVsalebankname())) {
							bankName = vo.getVsalebankname();
						}
					}
				}
			}

			// 非末级 先匹配银行 在找银行下 科目
			if (!isleaf) {

				if(StringUtil.isEmpty(bankName)){
					account = getFisrtNextLeafAccount(account, vo.getPk_corp(), accounts);
				}else{
					YntCpaccountVO bankvo = getAccountVOByBank(bankName, account.getAccountcode(), vo.getPk_corp(),
							accounts);
					if (bankvo == null) {
						// 匹配银行下科目
						// 按照银行新增一个科目
						YntCpaccountVO nextvo = getnext(account, account.getAccountcode(), accounts, bankName,
								vo.getPk_corp(), newrule);
						account = cpaccountService.saveNew(nextvo);
						if (!StringUtil.isEmpty(vo.getNtotaltax()) && vo.getNtotaltax().indexOf("$") >= 0) {
							hvo.setIautorecognize(0);
						}
					} else {
						boolean isleafb = bankvo.getIsleaf() == null ? false : bankvo.getIsleaf().booleanValue();
						// 判断银行科目是否为末级
						if (isleafb) {
							// 如果是 返回银行科目
							account = bankvo;
							if (!StringUtil.isEmpty(vo.getNtotaltax()) && vo.getNtotaltax().indexOf("$") >= 0) {
								hvo.setIautorecognize(0);
							}
						} else {
							// 匹配外币科目 和人民币科目
							YntCpaccountVO account1 = matchYhkm(hvo, bankvo, model, vo, entry, accounts);
							if (account1 != null) {
								account = account1;
							}
						}
					}
				}
			} else {
				// 如果是末级 看是否启用银行辅助
				// 匹配银行辅助 找第一个自定义的辅助
				int fz = getYhfz(account);
				if (fz > 6) {
					SQLParameter params = new SQLParameter();
					params.addParam(vo.getPk_corp());
					params.addParam(fz);
					AuxiliaryAccountHVO[] hvos = (AuxiliaryAccountHVO[]) singleObjectBO.queryByCondition(
							AuxiliaryAccountHVO.class, "nvl(dr,0)= 0 and pk_corp =? and code = ?", params);
					if (hvos != null && hvos.length > 0) {
						AuxiliaryAccountHVO ahvo = hvos[0];
						AuxiliaryAccountBVO bvo = ocr_atuomatch.getAuxiliaryAccountBVOByName(bankName, vo.getPk_corp(),
								ahvo.getPk_auacount_h());
						if (bvo == null && !StringUtil.isEmpty(bankName)) {
							bvo = new AuxiliaryAccountBVO();
							bvo.setCode(yntBoPubUtil.getFZHsCode(vo.getPk_corp(), ahvo.getPk_auacount_h()));
							bvo.setName(bankName);
							bvo.setPk_corp(vo.getPk_corp());
							bvo.setDr(0);
							bvo.setPk_auacount_h(ahvo.getPk_auacount_h());
							bvo = gl_fzhsserv.saveB(bvo);
						}
						if (bvo != null) {
							entry.setAttributeValue("fzhsx" + fz, bvo.getPk_auacount_b());
						}
					}
				}
			}
		} else {
			// 非银行类科目
			// 如果是末级 看是否启用客户或供应商辅助 如果非末级 找下级科目
			// 付款
			if (VATInvoiceTypeConst.VAT_PAY_INVOICE == vo.getItype()) {// 付款
																		// 取收款方名称
				if (!isleaf) {
					String name = vo.getVsalename();// 收款方名称
					if (StringUtil.isEmpty(name)) {
						account = getFisrtNextLeafAccount(account, vo.getPk_corp(), accounts);
					} else {
						YntCpaccountVO account1 = matchAccount(vo, account, name, accounts, newrule);
						if (account1 != null) {
							account = account1;
						}
					}
				} else {
					// 启用供应商辅助
					if (account.getIsfzhs().charAt(1) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.SUPPLIER.getValue(),
								vo.getVsalename(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx2", bvo.getPk_auacount_b());
						}
					}
					// 启用客户辅助 匹配辅助
					if (account.getIsfzhs().charAt(0) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.CUSTOMER.getValue(),
								vo.getVsalename(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx1", bvo.getPk_auacount_b());
						}
					}
				}
			} else if (VATInvoiceTypeConst.VAT_RECE_INVOICE == vo.getItype()) {// 收款
																				// 取付款方名称
				if (!isleaf) {
					String name = vo.getVpurchname();// 付款方名称
					if (StringUtil.isEmpty(name)) {
						account = getFisrtNextLeafAccount(account, vo.getPk_corp(), accounts);
					} else {
						YntCpaccountVO account1 = matchAccount(vo, account, name, accounts, newrule);
						if (account1 != null) {
							account = account1;
						}
					}
				} else {
					// 启用供应商辅助
					if (account.getIsfzhs().charAt(1) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.SUPPLIER.getValue(),
								vo.getVpurchname(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx2", bvo.getPk_auacount_b());
						}
					}
					// 启用客户辅助 匹配辅助
					if (account.getIsfzhs().charAt(0) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.CUSTOMER.getValue(),
								vo.getVpurchname(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx1", bvo.getPk_auacount_b());
						}
					}
				}
			} else {// 无法确认收付款的 按照默认收款 取付款方名称
				if (!isleaf) {
					if (StringUtil.isEmpty(vo.getVpurchname())) {
						account = getFisrtNextLeafAccount(account, vo.getPk_corp(), accounts);
					} else {
						YntCpaccountVO account1 = matchAccount(vo, account, vo.getVpurchname(), accounts, newrule);
						if (account1 != null) {
							account = account1;
						}
					}

				} else {
					// 启用供应商辅助
					if (account.getIsfzhs().charAt(1) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.SUPPLIER.getValue(),
								vo.getVpurchname(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx2", bvo.getPk_auacount_b());
						}
					}
					// 启用客户辅助 匹配辅助
					if (account.getIsfzhs().charAt(0) == '1') {
						AuxiliaryAccountBVO bvo = matchCustOrSupplier(vo, MatchTypeEnum.CUSTOMER.getValue(),
								vo.getVpurchname(), null, null, null);
						if (bvo != null) {
							entry.setAttributeValue("fzhsx1", bvo.getPk_auacount_b());
						}
					}
				}
			}
		}
		entry.setVname(account.getAccountname());
		entry.setSubj_allname(account.getFullname());
		entry.setKmmchie(account.getFullname());
		entry.setVcode(account.getAccountcode());
		entry.setSubj_code(account.getAccountcode());
		entry.setPk_accsubj(account.getPk_corp_account());
	}

	@Override
	protected void setSpeaclTzpzBVO(TzpzHVO hvo, OcrImageLibraryVO vo, List<TzpzBVO> tblist) {

		if (tblist == null || tblist.size() == 0)
			return;

		if (!StringUtil.isEmpty(vo.getExc_pk_currency())) {
			SQLParameter param = new SQLParameter();
			param.addParam(vo.getExc_pk_currency());
			param.addParam(vo.getPk_corp());
			ExrateVO[] vos = (ExrateVO[]) singleObjectBO.queryByCondition(ExrateVO.class,
					"nvl(dr,0) = 0  and pk_currency = ? and pk_corp = ?", param);

			if (vos == null || vos.length == 0) {
				throw new BusinessException("外币币种的汇率档案未定义，请检查");
			}
			DZFDouble exrate = vos[0].getExrate();
			DZFDouble mny = DZFDouble.ZERO_DBL;
			DZFDouble ybmny = DZFDouble.ZERO_DBL;
			for (TzpzBVO bvo : tblist) {
				if (bvo.getVdirect().intValue() == 0) {// 借方
					ybmny = bvo.getYbjfmny();
				} else {
					ybmny = bvo.getYbdfmny();
				}
				if (vos[0].getConvmode() == 1) {
					mny = SafeCompute.div(ybmny, exrate).setScale(2, 0);
				} else {
					mny = SafeCompute.multiply(ybmny, exrate).setScale(2, 0);
				}

				if (vo.getExc_pk_currency().equals(bvo.getPk_currency())) {// 外币一方
					bvo.setNrate(exrate);
					if (bvo.getVdirect().intValue() == 0) {// 借方
						bvo.setJfmny(mny);
						bvo.setYbjfmny(ybmny);
					} else {
						bvo.setDfmny(mny);
						bvo.setYbdfmny(ybmny);
					}
				} else {
					if (bvo.getVdirect().intValue() == 0) {// 借方
						bvo.setJfmny(mny);
						bvo.setYbjfmny(mny);
					} else {
						bvo.setDfmny(mny);
						bvo.setYbdfmny(mny);
					}
				}
			}
		}
	}

	private int getYhfz(YntCpaccountVO bankvo) {

		int[] fzindex = new int[] { 6, 7, 8, 9 };

		for (int index : fzindex) {
			if (bankvo.getIsfzhs().charAt(index) == '1') {
				return index + 1;
			}
		}
		return -1;
	}

	private YntCpaccountVO getAccountVOByBank(String name, String pcode, String pk_corp, YntCpaccountVO[] accounts) {

		if (accounts == null || accounts.length == 0)
			return null;

		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getAccountcode().startsWith(pcode)) {
				if (accvo.getAccountname().equals(name)) {
					return accvo;
				}
			}
		}
		return null;
	}

	// 是否外币 如果外币 匹配外币科目 非 外币 匹配正常科目
	private YntCpaccountVO matchYhkm(TzpzHVO hvo, YntCpaccountVO account, DcModelBVO model, OcrImageLibraryVO vo,
			TzpzBVO entry, YntCpaccountVO[] accounts) {

		YntCpaccountVO tempacc = (YntCpaccountVO) account.clone();
		// 判断是否有外币金额 如果有 设置外币币种 匹配银行下的外币科目
		if (!StringUtil.isEmpty(vo.getNtotaltax()) && vo.getNtotaltax().indexOf("$") >= 0) {
			YntCpaccountVO account1 = getWbAccount(account, vo.getPk_corp(), accounts);
			if (account1 != null) {
				account = account1;
				vo.setExc_pk_currency(account1.getExc_pk_currency());
				if ("b外币兑换人民币".equals(vo.getInvoicetype())) {
					if (model.getDirection().intValue() == 1) {
						account1 = getFisrtNextLeafAccountNoWb(tempacc, vo.getPk_corp(), accounts);
						if (account1 != null) {
							account = account1;
						}
					}
				}
			} else {
				account1 = getFisrtNextLeafAccountNoWb(account, vo.getPk_corp(), accounts);
				if (account1 != null) {
					account = account1;
					hvo.setIautorecognize(0);
				}
			}
			entry.setPk_currency(account.getExc_pk_currency());
		} else {
			// 匹配银行下的非外币科目
			YntCpaccountVO account1 = getFisrtNextLeafAccountNoWb(account, vo.getPk_corp(), accounts);
			if (account1 != null) {
				account = account1;
				// hvo.setIautorecognize(0);
			}
		}
		return account;
	}

	// 查询第一分支的最末级非外币科目
	private YntCpaccountVO getFisrtNextLeafAccountNoWb(YntCpaccountVO account, String pk_corp,
			YntCpaccountVO[] accounts) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目

		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(account.getAccountcode())
					&& (accvo.getIswhhs() == null || !accvo.getIswhhs().booleanValue())) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	// 查询科目下的外币科目
	private YntCpaccountVO getWbAccount(YntCpaccountVO account, String pk_corp, YntCpaccountVO[] accounts) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目

		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(account.getAccountcode()) && accvo.getIswhhs() != null
					&& accvo.getIswhhs().booleanValue()) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	@Override
	public TzpzHVO saveTzpzVO(ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo, boolean isRecog)
			throws DZFWarpException {
		try {
			image2bill_bank.saveBill(corpvo, hvo, invvo, grpvo, isRecog);
		} catch (Exception e) {
			log.error("错误",e);
			hvo.setPk_tzpz_h(null);
			throw new BusinessException(e.getMessage());
		}
		super.saveTzpzVO(grpvo, invvo, corpvo, hvo, isRecog);
		return hvo;
	}
}
