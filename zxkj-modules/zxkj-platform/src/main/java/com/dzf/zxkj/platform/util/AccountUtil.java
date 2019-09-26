package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.services.bdset.IAuxiliaryAccountService;

import java.util.HashMap;
import java.util.Map;

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
}
