package com.dzf.zxkj.platform.service.icset.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InvAccSetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.InvAccModelVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ic_chkmszserv")
public class InvAccSetServiceImpl implements IInvAccSetService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	@Autowired
	private IAccountService accountService;

	@Autowired
	private ICorpService corpService;

	@Override
	public InvAccSetVO query(String pk_corp) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		String condition = " pk_corp = ? and nvl(dr,0) = 0 ";
		InvAccSetVO[] pvos = (InvAccSetVO[]) singleObjectBO.queryByCondition(InvAccSetVO.class, condition, params);
		InvAccSetVO gvo = null;
		if (pvos != null && pvos.length > 0) {
			gvo = pvos[0];
		} else {
			YntCpaccountVO[] accvos = accountService.queryByPk(pk_corp);
			gvo = getModelVO(pk_corp, accvos);
		}
		if (gvo == null)
			gvo = new InvAccSetVO();
		return gvo;
	}

	private InvAccSetVO getModelVO(String pk_corp, YntCpaccountVO[] accvos) {

		CorpVO corpVO = corpService.queryByPk(pk_corp);
		String corpType = corpVO.getCorptype();
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpType);
		InvAccModelVO[] vos = (InvAccModelVO[]) singleObjectBO.queryByCondition(InvAccModelVO.class,
				" pk_corp = ? and pk_trade_accountschema = ? and nvl(dr,0) = 0", sp);

		if (vos == null || vos.length == 0)
			return null;

		Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accvos),
				new String[] { "pk_corp_account" });
		InvAccSetVO setvo = new InvAccSetVO();

		Set<String> set = new HashSet<>();
		for (InvAccModelVO vo : vos) {
			set.add(vo.getPk_accsubj());
		}

		Map<String, String> pkmap = getCorpAccountPkByTradeAccountPk(set, pk_corp, accvos);
		String corp_account = null;
		for (InvAccModelVO vo : vos) {
			corp_account = pkmap.get(vo.getPk_accsubj());
			corp_account = getFisrtNextLeafAccount(map, corp_account, accvos);
			switch (vo.getIcolumntype().intValue()) {
			case 0:
				setvo.setCg_yjjxskm(corp_account);
				break;
			case 1:
				setvo.setCg_yfzkkm(corp_account);
				break;
			case 2:
				setvo.setCg_xjfkkm(corp_account);
				break;
			case 3:
				setvo.setXs_xjskkm(corp_account);
				break;
			case 4:
				setvo.setXs_yszkkm(corp_account);
				break;
			case 5:
				setvo.setXs_yysrkm(corp_account);
				break;
			case 6:
				setvo.setXs_yjxxskm(corp_account);
				break;
			case 7:
				setvo.setLl_clcbkm(corp_account);
				break;
			case 8:
				setvo.setLl_yclkm(corp_account);
				break;
			case 9:
				setvo.setVdef1(corp_account);
				break;
			case 10:
				setvo.setVdef2(corp_account);
				break;
			case 11:
				setvo.setVdef3(corp_account);
				break;
			case 12:
				setvo.setVdef4(corp_account);
				break;
			case 13:
				setvo.setVdef5(corp_account);
				break;
			case 14:
				setvo.setVdef6(corp_account);
				break;
			case 15:
				setvo.setZgrkdfkm(corp_account);
				break;
			case 16:
				setvo.setXs_clsrkm(corp_account);
				break;
			default:
				break;
			}
		}

		setvo.setZgkhfz(null);
		return setvo;

	}

	private String getFisrtNextLeafAccount(Map<String, YntCpaccountVO> map, String corp_account,
			YntCpaccountVO[] accvos) {
		YntCpaccountVO accvo = null;
		if (map != null && map.size() > 0) {
			accvo = map.get(corp_account);
			if (accvo != null) {
				if (accvo.getIsleaf() != null && accvo.getIsleaf().booleanValue()) {

				} else {
					// 获取最末级科目
					accvo = getFisrtNextLeafAccount(accvo.getAccountcode(), accvos);
					if (accvo != null) {
						corp_account = accvo.getPk_corp_account();
					}
				}
			}
		}
		return corp_account;
	}

	private Map<String, String> getCorpAccountPkByTradeAccountPk(Set<String> set, String pk_corp,
			YntCpaccountVO[] accvos) throws DZFWarpException {

		if (set == null || set.size() == 0)
			return new HashMap<>();

		String condition = SqlUtil.buildSqlForIn("pk_trade_account", set.toArray(new String[set.size()]));
		BdTradeAccountVO[] vos = (BdTradeAccountVO[]) singleObjectBO.queryByCondition(BdTradeAccountVO.class, condition,
				null);

		if (vos == null || vos.length == 0)
			return new HashMap<>();

		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");

		Map<String, String> codemap = new HashMap<>();
		Map<String, String> pkmap = new HashMap<>();
		String newaccount = null;
		for (BdTradeAccountVO vo : vos) {
			newaccount = gl_accountcoderule.getNewRuleCode(vo.getAccountcode(), olerule, newrule);
			codemap.put(vo.getAccountcode(), newaccount);
			pkmap.put(newaccount, vo.getPk_trade_account());
		}
		// SQLParameter sp = new SQLParameter();
		// sp.addParam(pk_corp);
		// condition = "pk_corp=? and nvl(dr,0)=0 and"
		// + SqlUtil.buildSqlForIn("accountcode", codemap.values().toArray(new
		// String[set.size()]));
		// YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[])
		// singleObjectBO.queryByCondition(YntCpaccountVO.class, condition,
		// sp);
		//
		// if (gsjfkmVOs == null || gsjfkmVOs.length == 0)
		// return new HashMap<>();
		//
		// Map<String, YntCpaccountVO> map =
		// DZfcommonTools.hashlizeObjectByPk(Arrays.asList(gsjfkmVOs),
		// new String[] { "accountcode" });
		Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accvos),
				new String[] { "accountcode" });
		String key = null;
		YntCpaccountVO accvo = null;
		Map<String, String> newpkmap = new HashMap<>();
		for (Map.Entry<String, String> entry : codemap.entrySet()) {
			key = entry.getKey();
			accvo = map.get(entry.getValue());
			if (DZFValueCheck.isEmpty(accvo)) {
				throw new BusinessException("科目编码为" + key + "的科目不是末级科目或已被删除，如需继续操作，请配置相应模板。");
			}
			newpkmap.put(pkmap.get(accvo.getAccountcode()), accvo.getPk_corp_account());
		}

		return newpkmap;

	}

	// 查询第一分支的最末级科目
	private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, YntCpaccountVO[] accvos) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
		for (YntCpaccountVO accvo : accvos) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(accountcode)) {
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
	public InvAccSetVO save(InvAccSetVO vo1) throws DZFWarpException {

		validate(vo1, vo1.getPk_corp());

		if (StringUtil.isEmpty(vo1.getPk_invaccset())) {
			InvAccSetVO vo = query(vo1.getPk_corp());
			if (vo != null) {
				vo1.setPk_invaccset(vo.getPk_invaccset());
			}
		}

		YntCpaccountVO kmvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class,
				vo1.getZgrkdfkm());
		if (kmvo == null) {
			throw new BusinessException("暂估入库贷方科目不存在！");
		}
		if (!StringUtil.isEmpty(vo1.getZgkhfz())) {
			if (StringUtil.isEmpty(kmvo.getIsfzhs()) || !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))) {// 供应商辅助
				vo1.setZgkhfz(null);
			}
		} else {
			if (!StringUtil.isEmpty(kmvo.getIsfzhs()) && "1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))) {// 供应商辅助
				throw new BusinessException("暂估入库贷方科目已经启用供应商辅助，请设置供应商辅助！如果界面没有设置供应商辅助选项，请重新打开该节点进行操作");
			}
		}
		// String[] names = new String[] { "cg_ztwzkm", "cg_yjjxskm",
		// "cg_yfzkkm", "cg_xjfkkm", "xs_xjskkm", "xs_yszkkm",
		// "xs_yysrkm", "xs_yjxxskm", "ll_clcbkm", "ll_yclchppjscgzkm", "vdef1",
		// "vdef2", "vdef3", "vdef4", "vdef5",
		// "vdef6", "vdef7", "vdef8", "vdef9", "vdef10" };//, "chppjscgz"
		//
		// boolean isallNull = true;
		// if (names != null && names.length > 0) {
		//
		// for (String name : names) {
		// if (vo1.getAttributeValue(name) != null
		// &&!StringUtil.isEmpty((String)vo1.getAttributeValue(name))) {
		// isallNull = false;
		// break;
		// }
		// }
		// }
		InvAccSetVO vo = null;
		// if (isallNull) {
		// vo = new InvAccSetVO();
		// singleObjectBO.deleteObject(vo1);
		// } else {
		// vo1.setDr(0);
		vo = (InvAccSetVO) singleObjectBO.saveObject(vo1.getPk_corp(), vo1);
		// }

		return vo;
	}

	private void validate(InvAccSetVO vo, String pk_corp) {
		if (StringUtil.isEmpty(vo.getCg_xjfkkm()) || vo.getCg_xjfkkm().indexOf(pk_corp) == -1) {
			vo.setCg_xjfkkm(null);
		}
		if (StringUtil.isEmpty(vo.getCg_yfzkkm()) || vo.getCg_yfzkkm().indexOf(pk_corp) == -1) {
			vo.setCg_yfzkkm(null);
		}
		if (StringUtil.isEmpty(vo.getCg_yjjxskm()) || vo.getCg_yjjxskm().indexOf(pk_corp) == -1) {
			vo.setCg_yjjxskm(null);
		}
		if (StringUtil.isEmpty(vo.getCg_ztwzkm()) || vo.getCg_ztwzkm().indexOf(pk_corp) == -1) {
			vo.setCg_ztwzkm(null);
		}
		if (StringUtil.isEmpty(vo.getXs_xjskkm()) || vo.getXs_xjskkm().indexOf(pk_corp) == -1) {
			vo.setXs_xjskkm(null);
		}
		if (StringUtil.isEmpty(vo.getXs_yjxxskm()) || vo.getXs_yjxxskm().indexOf(pk_corp) == -1) {
			vo.setXs_yjxxskm(null);
		}
		if (StringUtil.isEmpty(vo.getXs_yszkkm()) || vo.getXs_yszkkm().indexOf(pk_corp) == -1) {
			vo.setXs_yszkkm(null);
		}
		if (StringUtil.isEmpty(vo.getXs_yysrkm()) || vo.getXs_yysrkm().indexOf(pk_corp) == -1) {
			vo.setXs_yysrkm(null);
		}
	}

	@Override
	public InvAccSetVO saveGroupVO(CorpVO cpvo) throws DZFWarpException {

		if (cpvo == null) {
			throw new BusinessException("传入公司为空");
		}

		String pk_corp = cpvo.getPk_corp();
		String[] names = new String[] { "cg_ztwzkm", "cg_yjjxskm", "cg_yfzkkm", "cg_xjfkkm", "xs_xjskkm", "xs_yszkkm",
				"xs_yysrkm", "xs_yjxxskm", "ll_clcbkm", "ll_yclkm", "vdef1", "vdef2", "vdef3", "vdef4", "vdef5",
				"vdef6", "vdef7", "vdef8", "vdef9", "vdef10", "zgrkdfkm", "zgkhfz","zgrkdfkm","xs_clsrkm" };// ,
		// "chppjscgz"
		YntCpaccountVO[] accvos = accountService.queryByPk(pk_corp);
		InvAccSetVO vo = query(pk_corp);
		InvAccSetVO gvo = getModelVO(pk_corp, accvos);
		if (gvo != null) {
			for (String name : names) {
				vo.setAttributeValue(name, gvo.getAttributeValue(name));
			}
		}
		if (StringUtil.isEmpty(vo.getPk_invaccset())) {
			vo.setPk_corp(pk_corp);
		}
		vo.setZgkhfz(null);
		// 默认暂估
		AuxiliaryAccountBVO[] bodyvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_SUPPLIER, pk_corp, null);
		String zgfz = null;
		if (bodyvos != null && bodyvos.length > 0) {
			for (AuxiliaryAccountBVO bvo : bodyvos) {
				if (!StringUtil.isEmpty(bvo.getName()) && bvo.getName().trim().contains("暂估")) {
					zgfz = bvo.getPk_auacount_b();
					break;
				}
			}
		}
		vo.setZgkhfz(zgfz);// 默认供应商辅助
		vo = (InvAccSetVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);

		return vo;
	}
}
