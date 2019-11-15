package com.dzf.zxkj.platform.service.zncs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.zncs.IAutoMatchName;
import com.dzf.zxkj.platform.util.zncs.HazyMatchKeyWord;
import com.dzf.zxkj.platform.util.zncs.MatchTypeEnum;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ocr_atuomatch")
public class AutoMatchNameImpl implements IAutoMatchName {
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IAccountService accountService;

	private String autoMatchName(String name, List<String> list) {

		if (list == null || list.size() == 0)
			return name;

		Map<String, DZFDouble> countMap = new HashMap<String, DZFDouble>();
		HazyMatchKeyWord word = new HazyMatchKeyWord();
		List<String> slist = new ArrayList<>();
		for (String str : list) {
			double dou = word.compareTowWord(str, name);
			if (dou > 0.8) {
				int slen = StringUtil.isEmpty(str) ? 0 : str.length();
				int nlen = StringUtil.isEmpty(name) ? 0 : name.length();
				if (slen == nlen)
					countMap.put(str, new DZFDouble(dou));
			}
			if (slist.contains(str)) {
				slist.add(str);
			}
		}
		DZFDouble maxNo = DZFDouble.ZERO_DBL;
		String maxKey = null;
		// Set<String> keySet = countMap.keySet();
		for (String key : slist) {
			DZFDouble valueNo = (DZFDouble) countMap.get(key);
			if (valueNo.compareTo(maxNo) > 0) {
				maxNo = valueNo;
				maxKey = key;
			}
		}
		log.info("关键字:" + name + ",匹配字:" + maxKey + ",匹配度:" + maxNo);
		if (StringUtil.isEmpty(maxKey))
			return name;
		return maxKey;
	}

	@Override
	public AuxiliaryAccountBVO autoMatchAuxiliaryAccount(String name, int type, String pk_corp) {

		if (StringUtil.isEmpty(name)) {
			return null;
		}
		String condition = " nvl(dr,0) = 0  and  pk_auacount_h = ?  and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		String pk_auacount_h = null;
		if (type == MatchTypeEnum.CUSTOMER.getValue()) {
			pk_auacount_h = AuxiliaryConstant.ITEM_CUSTOMER;
		} else if (type == MatchTypeEnum.SUPPLIER.getValue()) {
			pk_auacount_h = AuxiliaryConstant.ITEM_SUPPLIER;
		} else if (type == MatchTypeEnum.INVENTORY.getValue()) {
			pk_auacount_h = AuxiliaryConstant.ITEM_INVENTORY;
		} else {
			pk_auacount_h = "11111";
		}
		sp.addParam(pk_auacount_h);
		sp.addParam(pk_corp);
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);

		if (vos == null || vos.length == 0) {
			return null;
		}

		// List<String> list = new ArrayList<String>();

		// for (AuxiliaryAccountBVO vo : vos) {
		// list.add(vo.getName());
		// }
		// String tempname = autoMatchName(name, list);
		return getAuxiliaryAccountBVOByName(name, pk_corp, pk_auacount_h);
	}

	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByName(String name, String pk_corp, String pk_auacount_h) {

		if (StringUtil.isEmpty(name)) {
			return null;
		}

		AuxiliaryAccountBVO[] vos = gl_fzhsserv.queryB(pk_auacount_h, pk_corp, null);

		if (vos == null || vos.length == 0)
			return null;
		AuxiliaryAccountBVO bvo = null;
		name = filterName(name);
		for (AuxiliaryAccountBVO vo : vos) {
			String saleName = filterName(vo.getName());
			if (name.equals(saleName)) {
				bvo = vo;
				break;
			}
		}

		return bvo;

	}

	private String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		name = getHanzi(name);
		return name;
	}

	private String getHanzi(String string) {
		if (StringUtil.isEmpty(string))
			return null;
		String reg1 = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(reg1);
		Matcher m = p.matcher(string);

		String lasrChar = "";
		while (m.find()) {
			lasrChar = lasrChar + m.group();
		}
		return lasrChar;
	}

	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByTaxNo(String taxno, String pk_corp, String pk_auacount_h) {

		if (StringUtil.isEmpty(taxno)) {
			return null;
		}
		String condition = " nvl(dr,0) = 0  and  pk_auacount_h = ?  and pk_corp = ? and  taxpayer = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_auacount_h);
		sp.addParam(pk_corp);
		sp.addParam(taxno);
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);
		return vos == null || vos.length == 0 ? null : vos[0];

	}

	@Override
	public InventoryVO autoMatchInventoryVO(String name, int type, String pk_corp) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		String condition = " nvl(dr,0) = 0   and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		InventoryVO[] vos = (InventoryVO[]) singleObjectBO.queryByCondition(InventoryVO.class, condition, sp);

		if (vos == null || vos.length == 0) {
			return null;
		}

		// List<String> list = new ArrayList<String>();
		//
		// for (InventoryVO vo : vos) {
		// list.add(vo.getName());
		// }
		// String tempname = autoMatchName(name, list);
		return getInventoryVOByName(name, pk_corp);
	}

	public InventoryVO getInventoryVOByName(String name, String pk_corp) {

		if (StringUtil.isEmpty(name)) {
			return null;
		}
		String condition = " nvl(dr,0) = 0   and pk_corp = ? and  name = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(name);
		InventoryVO[] vos = (InventoryVO[]) singleObjectBO.queryByCondition(InventoryVO.class, condition, sp);
		return vos == null || vos.length == 0 ? null : vos[0];

	}

	@Override
	public YntCpaccountVO getXJAccountVOByName(String name, String pcode, String pk_corp) {

		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		if (accounts == null || accounts.length == 0)
			return null;
		for (YntCpaccountVO accvo : accounts) {
			boolean ifleaf = accvo.getIsleaf() == null ? false : accvo.getIsleaf().booleanValue();
			if (ifleaf && accvo.getAccountcode().startsWith(pcode)) {
				if (accvo.getAccountname().equals(name)) {
					return accvo;
				}
			}
		}
		return null;
	}

	@Override
	public YntCpaccountVO atuoMatchXJAccountVOByName(String name, String pcode, String pk_corp) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		// YntCpaccountVO[] accounts = AccountCache.getInstance().get(null,
		// pk_corp);
		// if (accounts == null || accounts.length == 0)
		// return null;
		//
		// List<String> list = new ArrayList<String>();
		//
		// for (YntCpaccountVO vo : accounts) {
		// boolean ifleaf = vo.getIsleaf() == null ? false :
		// vo.getIsleaf().booleanValue();
		// if (ifleaf && vo.getAccountcode().startsWith(pcode)) {
		// list.add(vo.getAccountname());
		// }
		//
		// }
		// String tempname = autoMatchName(name, list);
		return getXJAccountVOByName(name, pcode, pk_corp);
	}

	@Override
	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByInfo(String invname, String invtype, String invunit,
			String pk_corp, String pk_auacount_h) {
		if (StringUtil.isEmpty(invname)) {
			return null;
		}
		String condition = " nvl(dr,0) = 0  and  pk_auacount_h = ?  and pk_corp = ? and  name = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_auacount_h);
		sp.addParam(pk_corp);
		sp.addParam(invname);
		if (StringUtil.isEmpty(invtype)) {
			condition = condition + " and spec is null ";
		} else {
			condition = condition + " and spec =? ";
			sp.addParam(invtype);
		}

		if (StringUtil.isEmpty(invunit)) {
			condition = condition + " and unit is null ";
		} else {
			condition = condition + " and unit =? ";
			sp.addParam(invunit);
		}
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);
		return vos == null || vos.length == 0 ? null : vos[0];
	}

	@Override
	public InventoryVO getInventoryVOByName(String invname, String invspec, String invunit, String pk_corp) {
		if (StringUtil.isEmpty(invname)) {
			return null;
		}
		SQLParameter sp = new SQLParameter();
		String condition = " nvl(dr,0) = 0   and pk_corp = ? and  name = ?";
		sp.addParam(pk_corp);
		sp.addParam(invname);
		if (StringUtil.isEmpty(invspec)) {
			condition = condition + " and invspec is null ";
		} else {
			condition = condition + " and invspec =? ";
			sp.addParam(invspec);
		}

		if (StringUtil.isEmpty(invunit)) {
			condition = condition + " and pk_measure is null ";
		} else {
			condition = condition + " and pk_measure =? ";
			sp.addParam(invunit);
		}
		InventoryVO[] vos = (InventoryVO[]) singleObjectBO.queryByCondition(InventoryVO.class, condition, sp);
		return vos == null || vos.length == 0 ? null : vos[0];
	}

}
