package com.dzf.zxkj.report.enums;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 现金类科目方案
 * 
 * @author zhangj
 *
 */
public enum KmschemaCash {

	XJKM_13("00000100AA10000000000BMD", "1001,1002", "1012"), 
	XJKM_07("00000100AA10000000000BMF", "1001,1002,1101","1012"), 
	XJKM_QK("00000100000000Ig4yfE0005", "1001,1002", "1009"), // 企业会计制度
	XJKM_MJ("00000100AA10000000000BMQ", "1001,1002", "1009");// 民间非盈利

	private String corptype;
	private String xjkm;// 现金科目(,号分割)
	private String filterxjkm;// 过滤的现金科目(,号分割)

	KmschemaCash(String corptype, String xjkm, String filterxjkm) {
		this.corptype = corptype;
		this.xjkm = xjkm;
		this.filterxjkm = filterxjkm;
	}


	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	public String getXjkm() {
		return xjkm;
	}

	public void setXjkm(String xjkm) {
		this.xjkm = xjkm;
	}

	public String getFilterxjkm() {
		return filterxjkm;
	}

	public void setFilterxjkm(String filterxjkm) {
		this.filterxjkm = filterxjkm;
	}
	
	/**
	 * 数组合并
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String[] merArray(String[] str1, String[] str2) {
		String[] res = new String[str1.length + str2.length];
		for (int i = 0; i < str1.length; i++) {
			res[i] = str1[i];
		}
		for (int i = 0; i < str2.length; i++) {
			res[str1.length + i] = str2[i];
		}
		return res;
	}

	private static String[] fileUnxjkm(YntCpaccountVO[] cpavos, String[] strs, DZFBoolean isonlyleaf) {
		List<String> list = new ArrayList<String>();
		if (cpavos != null && cpavos.length > 0) {
			for (YntCpaccountVO vo : cpavos) {
				if (isonlyleaf.booleanValue() && (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue())) {
					continue;
				}
				for (String str : strs) {
					// 非现金科目，过滤
					if (vo.getAccountcode().startsWith(str)
							&& (vo.getBuncashkm() == null || !vo.getBuncashkm().booleanValue())) {
						list.add(vo.getAccountcode());
					}
				}

			}
		}
		return list.toArray(new String[0]);
	}
	
	
	
	/**
	 * 获取所有现金科目，包含本身+下级的。
	 * @param cpavos
	 * @param corpType
	 * @return
	 */
	public static Set<String> getCashSubjectCode(YntCpaccountVO[] cpavos, String corpType) {
		return getCashSubjectCode1(cpavos, corpType,DZFBoolean.FALSE);
	}


	/**
	 * 获取现金类科目
	 * @param cpavos
	 * @param corpType
	 * @param isleaf 是否只有末级
	 * @return
	 */
	public static Set<String> getCashSubjectCode1(YntCpaccountVO[] cpavos, String corpType,DZFBoolean bonlyleaf) {
		Set<String> set = new HashSet<String>();
		if (StringUtil.isEmpty(corpType)) {
			return set;
		}
		String[] xjkm ;
		String[] fileterxjkm ;
		for (int i = 0; i < KmschemaCash.values().length; i++) {
			if (corpType.equals(KmschemaCash.values()[i].getCorptype())) {
				//现金科目
				if(!StringUtil.isEmpty(KmschemaCash.values()[i].getXjkm())){
					xjkm = KmschemaCash.values()[i].getXjkm().split(",");
					for(int j = 0; j < xjkm.length; j++){
						for(YntCpaccountVO cpavo:cpavos){
							if(bonlyleaf.booleanValue() && 
									(cpavo.getIsleaf() ==null || !cpavo.getIsleaf().booleanValue())){
								continue;
							}
							if(cpavo.getAccountcode().startsWith(xjkm[j])){
								set.add(cpavo.getAccountcode());
							}
						}
					}
				}
				//过滤的现金科目
				if(!StringUtil.isEmpty(KmschemaCash.values()[i].getFilterxjkm())){
					fileterxjkm = fileUnxjkm(cpavos, KmschemaCash.values()[i].getFilterxjkm().split(","),bonlyleaf);
					for(int j =0;j<fileterxjkm.length;j++){
						set.add(fileterxjkm[j]);
					}
				}
			}
		}

		return set;
	}

	public static boolean isCashAccount(YntCpaccountVO[] cpavos, String vcode, String corpType) {
		Set<String> codeset = getCashSubjectCode(cpavos,corpType);
		if (codeset.size() > 0) {
			if(codeset.contains(vcode)){
				return true;
			}
		}
		return false;
	}

}
