package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KmbmUpgrade {

	private final static String SPLIT = "/";

	/**
	 * 返回科目升级后科目编码的Map ///   4/2/2/2/2
	 * 
	 * Map<key,value>-----------key-----升级后的科目编码
	 * 				 -----------value---升级前的科目编码
	 */
	public static Map<String,String> getKmUpgradeinfo(CorpVO vo, String[] vos){
		if(vo == null||vos==null||vos.length ==0)
			return null;
		Map<String,String> updatemap = new HashMap<String,String>();
		String rule = vo.getAccountcoderule();
		boolean flag = false;
		if(!StringUtil.isEmpty(rule)//非空
				&& !rule.startsWith("4/2/2")){//非4/2/2开头
			flag = true;
		}
		for(String code : vos){
			if(StringUtil.isEmpty(code))
				continue;
			if(flag){//科目编码规则存在升级
				String newcode = getNewCode(code,"4/2/2/2/2",rule);
				if(!StringUtil.isEmpty(newcode)){
					updatemap.put(newcode, code);
				}
			}else{
				updatemap.put(code, code);
			}
		}
		return updatemap;
	}
	
	public static String getNewCode(String oldcode,String oldrule,String newrule) {
		String newcode = "";
		try {
			if(StringUtil.isEmpty(oldrule)){
				oldrule = "4/2/2/2/2";
			}
			if(StringUtil.isEmpty(newrule)){
				newrule = "4/2/2/2/2";
			}
			String[] odru = oldrule.split(SPLIT);
			String[] newru = newrule.split(SPLIT);
			int startIndex = 0;
		    for(int i=0;i<odru.length;i++){
		    	int codelen = new BigInteger(String.valueOf(odru[i])).intValue();
		    	String oldpartCode = oldcode.substring(startIndex, startIndex+codelen);
		    	startIndex+=codelen;
		    	String newpartCode = getNewPartCode(newru[i],oldpartCode);
		    	newcode+=newpartCode;
		    	if(startIndex==oldcode.trim().length()){
		    		break;
		    	}
		    }
		} catch (Exception e) {
			log.error("错误",e);
		}
		return newcode;
	}
	
	/**
	 * 反推4/2/2/2/2的原始科目。
	 * 比如：
	 * 存在比如升级后4/3/3/3/3的科目新增1001999返回null
	 * 存在比如升级后4/3/3/3/3的科目新增1001099返回100199
	 */
	public static String getOriginalCode(String nowcode,String nowrule){
		if(StringUtil.isEmpty(nowcode) 
				|| StringUtil.isEmpty(nowrule) 
				|| nowrule.equals("4/2/2")){
			return nowcode;
		}
		String code = "";
		String[] nowru = nowrule.split(SPLIT);
		int startIndex = 0;
		for(int i=0;i<nowru.length;i++){
			int inx = Integer.valueOf(nowru[i]);
			if(inx>nowcode.length()){
				inx = nowcode.length();
			}
			int endindex = startIndex+inx;
			if(endindex>nowcode.length()){
				endindex = nowcode.length();
			}
			String splitcode = nowcode.substring(startIndex, endindex);
			if(i>0 && inx>2 && splitcode.length()-2 > 0) {
				for(int z = 0 ;z<splitcode.length();z++){
					if(z < splitcode.length()-2
							&& !String.valueOf(splitcode.charAt(z)).equals("0")){
						return null;
					}
				}
				splitcode = splitcode.substring(splitcode.length()-2, splitcode.length());
			}
			code = code+ splitcode;
			startIndex = endindex;
			if(startIndex>=nowcode.length()){
				break;
			}
		}
		return code;
	}
	
	private static String getNewPartCode(String newcodeRulePart,String oldpartCode){
		String newPartCode = oldpartCode;
		int newPartLen = Integer.parseInt(newcodeRulePart);
		int oldPartLen = oldpartCode.trim().length();
		if(oldPartLen==newPartLen){
			return newPartCode;
		}
		for(int i=0;i<(newPartLen-oldPartLen);i++){
			newPartCode = "0" + newPartCode;
		}
		return newPartCode;
	}
	
}
