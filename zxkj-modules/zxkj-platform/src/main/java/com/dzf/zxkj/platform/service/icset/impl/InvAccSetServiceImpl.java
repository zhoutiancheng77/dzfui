package com.dzf.zxkj.platform.service.icset.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InvAccSetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.InvAccModelVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.AccountUtil;
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
		if(!StringUtil.isEmpty(gvo.getZgkhfz())){
			boolean b = gl_fzhsserv.isExistFz(gvo.getPk_corp(), gvo.getZgkhfz(), AuxiliaryConstant.ITEM_SUPPLIER);
			if (!b) {
				gvo.setZgkhfz(null);
			}
		}
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

		Map<String, String> pkmap = AccountUtil.getCorpAccountPkByTradeAccountPkWithMsg(set, pk_corp, accvos,"请到数据维护-标准科目节点，升级会计科目。");

        String corp_account = null;
		Map<Integer,String> colmap = new HashMap<>();
		for (InvAccModelVO vo : vos) {
			corp_account = pkmap.get(vo.getPk_accsubj());
			corp_account = AccountUtil.getFisrtNextLeafAccount(map, corp_account, accvos);
			colmap.put(vo.getIcolumntype(),corp_account);
		}
        InvAccSetVO.setDefaultValue(colmap, setvo);
		setvo.setZgkhfz(null);
		return setvo;

	}

	@Override
	public InvAccSetVO save(InvAccSetVO invaccvo) throws DZFWarpException {

		validate(invaccvo, invaccvo.getPk_corp());

		if (StringUtil.isEmpty(invaccvo.getPk_invaccset())) {
			InvAccSetVO vo = query(invaccvo.getPk_corp());
			if (vo != null) {
                invaccvo.setPk_invaccset(vo.getPk_invaccset());
			}
		}

		YntCpaccountVO kmvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class,
                invaccvo.getZgrkdfkm());
		if (kmvo == null) {
			throw new BusinessException("暂估入库贷方科目不存在！");
		}
        invaccvo.setZgkhfz(AccountUtil.getZgkhfz(invaccvo.getZgkhfz(),kmvo));
		InvAccSetVO vo  = (InvAccSetVO) singleObjectBO.saveObject(invaccvo.getPk_corp(), invaccvo);
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
	public InvAccSetVO saveGroupVO(CorpVO cpvo, boolean isbulid) throws DZFWarpException {

		if (cpvo == null) {
			throw new BusinessException("传入公司为空");
		}

		String pk_corp = cpvo.getPk_corp();
		String[] names =InvAccSetVO.NAMES;
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
