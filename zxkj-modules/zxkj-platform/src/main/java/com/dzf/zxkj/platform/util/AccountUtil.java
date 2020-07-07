package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;

import java.util.*;

public class AccountUtil {

	public static Map<String, YntCpaccountVO> getAccVOByCode(String pk_corp, YntCpaccountVO[] accvos) {
		Map<String, YntCpaccountVO> map = new HashMap<>();
		if (accvos == null || accvos.length == 0)
			return map;
		for (YntCpaccountVO accvo : accvos) {
			map.put(accvo.getAccountcode(), accvo);
		}
		return map;
	}
	
	public static Map<String, YntCpaccountVO> getAccVOByCodeName(String pk_corp, YntCpaccountVO[] accvos, String separator) {
		Map<String, YntCpaccountVO> map = new HashMap<>();
		if (accvos == null || accvos.length == 0)
			return map;
		for (YntCpaccountVO accvo : accvos) {
			map.put(accvo.getAccountcode() + separator + accvo.getAccountname(), accvo);
		}
		return map;
	}
	
	public static Map<String, AuxiliaryAccountBVO> getAuxiliaryAccountBVOByName(String pk_corp, String pk_auacount_h) {
		IAuxiliaryAccountService gl_fzhsserv = (IAuxiliaryAccountService) SpringUtils.getBean("gl_fzhsserv");
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(pk_auacount_h, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> map = new HashMap<>();
		if (bvos == null || bvos.length == 0)
			return map;
		for (AuxiliaryAccountBVO accvo : bvos) {
			map.put(accvo.getName(), accvo);
		}
		return map;

	}

	public static Map<String, AuxiliaryAccountBVO> getAuxiliaryAccountBVOByCode(String pk_corp, String pk_auacount_h) {
		IAuxiliaryAccountService gl_fzhsserv = (IAuxiliaryAccountService) SpringUtils.getBean("gl_fzhsserv");
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(pk_auacount_h, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> map = new HashMap<>();
		if (bvos == null || bvos.length == 0)
			return map;
		for (AuxiliaryAccountBVO accvo : bvos) {
			map.put(accvo.getCode(), accvo);
		}
		return map;

	}

    public static Map<String, String> getCorpAccountPkByTradeAccountPkWithMsg(Set<String> set, String pk_corp,
                                                                 YntCpaccountVO[] accvos,String msg, boolean isbulid) throws DZFWarpException {
        if (set == null || set.size() == 0)
            return new HashMap<>();
        SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
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

        Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accvos),
                new String[] { "accountcode" });
        String key = null;
        YntCpaccountVO accvo = null;
        Map<String, String> newpkmap = new HashMap<>();
        for (Map.Entry<String, String> entry : codemap.entrySet()) {
            key = entry.getKey();
            accvo = map.get(entry.getValue());
            if (DZFValueCheck.isEmpty(accvo)) {
                if(!isbulid){
                    if(StringUtil.isEmpty(msg)){
                        throw new BusinessException("科目编码为" + key
                                + "的科目不存在，如需继续操作，请配置相应模板。");
                    }else{
                        throw new BusinessException("科目编码为" + key
                                + "的科目不存在，"+msg);
                    }
                }
            }else{
                newpkmap.put(pkmap.get(accvo.getAccountcode()), accvo.getPk_corp_account());
            }
        }
        return newpkmap;

    }

	public static Map<String, AuxiliaryAccountBVO> getAuxiliaryAccountBVOByPk(String pk_corp, String pk_auacount_h) {
		IAuxiliaryAccountService gl_fzhsserv = (IAuxiliaryAccountService) SpringUtils.getBean("gl_fzhsserv");
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(pk_auacount_h, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> map = new HashMap<>();
		if (bvos == null || bvos.length == 0)
			return map;
		for (AuxiliaryAccountBVO accvo : bvos) {
			map.put(accvo.getPk_auacount_b(), accvo);
		}
		return map;
	}

	public static String getFisrtNextLeafAccount(Map<String, YntCpaccountVO> map, String corp_account,
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

	// 查询第一分支的最末级科目
	public static YntCpaccountVO getFisrtNextLeafAccount(String accountcode, YntCpaccountVO[] accvos) {
	    // 存储下级科目
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
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

    public static String getZgkhfz(String zgkhfz , YntCpaccountVO kmvo) {
        boolean isfz = StringUtil.isEmpty(kmvo.getIsfzhs())
                || !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)));
        //非供应商辅助
        if (isfz) {
            if (!StringUtil.isEmpty(zgkhfz)) {
                return null;
            } else {
                throw new BusinessException("暂估入库贷方科目已经启用供应商辅助，请设置供应商辅助！如果界面没有设置供应商辅助选项，请重新打开该节点进行操作");
            }
        }
        return zgkhfz;
    }
}
