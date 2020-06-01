package com.dzf.zxkj.app.utils;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import javafx.scene.control.Cell;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportUtil {

	public static String format(DZFDouble val, int jd) {
		String res = "";
		if (val == null) {
			val = DZFDouble.ZERO_DBL;
		}
		NumberFormat fm = NumberFormat.getNumberInstance();

		fm.setRoundingMode(RoundingMode.HALF_UP);
		fm.setMinimumFractionDigits(jd);
		fm.setMaximumFractionDigits(jd);

		res = fm.format(new DZFDouble(val.toString()));
		return res;
	}
	/**
	 * 查询有数据的科目
	 * @param vo
	 * @return
	 */
	public List<YntCpaccountVO> queryAccount (QueryParamVO vo, List<TzpzHVO> vchs) {
		return null;
	}
	/**
	 * 获取辅助核算项pk
	 * @param obj
	 * @return
	 */
	public static String getFzKey (SuperVO svo) {
		StringBuffer key = new StringBuffer("");
		//10个辅助核算项
		for (int i = 1; i <= 10; i++) {
			String fzhs = (String) svo.getAttributeValue("fzhsx" + i);
			if (!StringUtil.isEmpty(fzhs)) {
				key.append(",");
				key.append(fzhs);
			}
		}
		return key.toString();
	}
	
	public static void copyFzPrimary (SuperVO dest, SuperVO src) {
		for (int i = 1; i <= 10; i++) {
			String attr = "fzhsx" + i;
			String id = (String) src.getAttributeValue(attr);
			dest.setAttributeValue(attr, id);
		}
	}


	public static String getCurrencyDw(String currency){
		if(StringUtil.isEmptyWithTrim(currency) || "人民币".equals(currency) || "综合本位币".equals(currency)){
			return "元";
		}else{
			return currency;
		}
	}
	
	public static List<AuxiliaryAccountBVO> getFzhsList (String pk_corp, SuperVO svo, Map<String, AuxiliaryAccountBVO> auxMap) {
		List<AuxiliaryAccountBVO> list = new ArrayList<AuxiliaryAccountBVO>();
		for (int i = 1; i <= 10; i++) {
			String fzhsID = (String) svo.getAttributeValue("fzhsx" + i);
			if (!StringUtil.isEmpty(fzhsID)) {
				list.add(auxMap.get(fzhsID));
			}
		}
		return list;
	}

	
	/**
	 * 获取开始日期 到 结束日期的区间数据
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	public static List<String> getPeriodsByPeriod(String period_beg,String period_end) {
		
		DZFDate begindate = DateUtils.getPeriodStartDate(period_beg);
		DZFDate enddate = DateUtils.getPeriodEndDate(period_end);
		
		List<String> vec_periods = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
		}
		return vec_periods;
	}
	
	/**
	 * 获取开始日期 到 结束日期的区间数据
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	public static List<String> getPeriods(DZFDate begindate, DZFDate enddate) {
		List<String> vec_periods = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
		}
		return vec_periods;
	}
	
	/**
	 * 
	 * @param vo
	 * @param codeField 科目编码字段名称
	 * @param nameField 科目名称自动名称
	 * @param auaccountMap 辅助核算Map
	 * @param invmap 存货Map
	 * @return
	 */
	public static String[] getCombStr (SuperVO vo, String codeField,
                                       String nameField, Map<String, AuxiliaryAccountBVO> auaccountMap,
                                       Map<String, AuxiliaryAccountBVO> invmap) {
		String[] str = new String[3];
		StringBuffer code = new StringBuffer((String) vo.getAttributeValue(codeField));
		StringBuffer name = new StringBuffer((String) vo.getAttributeValue(nameField));
		AuxiliaryAccountBVO auaccount = null;
		
		for (int i = 1; i <= 10; i++) {
			String fzhsId = (String) vo.getAttributeValue("fzhsx" + i);
			
			if (StringUtil.isEmpty(fzhsId))
				continue;
			auaccount = auaccountMap.get(fzhsId);
			
			if (auaccount == null) {
				auaccount = invmap.get(fzhsId);
				str[2] = auaccount.getUnit();
			}
			
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		str[0] = code.toString();
		str[1] = name.toString();
		return str;
	}
	

	
	/**
	 * 赋值科目方向
	 * @param kmmxvos
	 * @return
	 * @throws DZFWarpException
	 */
	public static SuperVO[] updateKFx(SuperVO[] kmmxvos) throws DZFWarpException {
		if(kmmxvos!=null && kmmxvos.length>0){
			DZFDouble ye = DZFDouble.ZERO_DBL;
			for(SuperVO vo:kmmxvos){
				if(vo instanceof KmMxZVO || vo instanceof KmZzVO
						|| vo instanceof FzKmmxVO){
					ye = (DZFDouble) vo.getAttributeValue("ye");
					if(VoUtils.getDZFDouble(ye).doubleValue() == 0){
						vo.setAttributeValue("fx", "平");
					}
				}else if(vo instanceof NumMnyDetailVO){
					ye = (DZFDouble) vo.getAttributeValue("nymny");
					if(VoUtils.getDZFDouble(ye).doubleValue() == 0){
						vo.setAttributeValue("dir", "平");
					}
				}else if( vo instanceof NumMnyGlVO){
					ye = (DZFDouble) vo.getAttributeValue("qmmny");
					if(VoUtils.getDZFDouble(ye).doubleValue() == 0){
						vo.setAttributeValue("dir", "平");
					}
				}
			}
		}
		return kmmxvos;
	}

	
	public static Map<String, String> getLevelKmName(YntCpaccountVO[] cpavos, Integer subjectShow){
		Map<String, String> resultmap = new HashMap<String, String>();
		if(cpavos!=null && cpavos.length>0){
			Arrays.sort(cpavos, new Comparator<YntCpaccountVO>() {
				@Override
				public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
					return o1.getAccountcode().compareTo(o2.getAccountcode());
				}
			});
			
			StringBuffer accountname = new StringBuffer();
			
			for(YntCpaccountVO cpavo: cpavos){
				 accountname = new StringBuffer();
				if(subjectShow.intValue() == 0){//显示本级
					accountname.append(cpavo.getAccountname());
				}else if(subjectShow.intValue() == 1){//显示一级+本级
					if(cpavo.getAccountcode().length()>4){
						accountname.append(resultmap.get(cpavo.getAccountcode().substring(0, 4)) + "_");
					}
					accountname.append(cpavo.getAccountname());
				}else{//显示全部
					for(YntCpaccountVO cpavo1: cpavos){
						if(cpavo.getAccountcode().startsWith(cpavo1.getAccountcode())
								&& cpavo.getAccountcode().length() > cpavo1.getAccountcode().length() ){
							accountname.append(cpavo1.getAccountname() + "_");
						}
					}
					
					accountname.append(cpavo.getAccountname());
				}
				resultmap.put(cpavo.getAccountcode(), accountname.toString());
			}
		}
		return resultmap;
	}
	
	/**
	 * 是否系统生成的摘要
	 * @return
	 */
	public static boolean bSysZy(SuperVO kmmxzvo) {
		if (kmmxzvo == null) {
			return false;
		}

		DZFBoolean bsyszy = (DZFBoolean) kmmxzvo.getAttributeValue("bsyszy");
		
		if (bsyszy != null && bsyszy.booleanValue()) {
			return true;
		}
		return false;
	}
	public static String getNextCode(String code) {
		if (StringUtil.isEmpty(code)) {
			return "001";
		}
		char[] charArray = code.toCharArray();
		int lastIndex = charArray.length - 1;
		// 最后一个字符非数字
		if (charArray[lastIndex] < '0'
				|| charArray[lastIndex] > '9') {
			code = code + "1";
		} else {
			int lastNum = charArray[lastIndex] - 48;
			lastNum = lastNum + 1;
			int carry = lastNum / 10;
			charArray[lastIndex] = (char) (lastNum % 10 + 48);
			int index = lastIndex - 1;
			for (; index >= 0 && carry > 0
					&& charArray[index] >= '0'
					&& charArray[index] <= '9'; index--) {
				int num = charArray[index] - 48;
				int newNum = num + carry;
				carry = newNum / 10;
				charArray[index] = (char) (newNum % 10 + 48);
			}
			if (carry > 0) {
				char[] destCharArray = new char[charArray.length + 1];
				if (index == -1) {
					System.arraycopy(charArray, 0, destCharArray, 1,
							charArray.length);
					destCharArray[0] = (char) (carry + 48);
				} else {
					index++;
					System.arraycopy(charArray, index, destCharArray, index + 1,
							charArray.length - index);
					System.arraycopy(charArray, 0, destCharArray, 0,
							index);
					destCharArray[index] = (char) (carry + 48);
				}
				charArray = destCharArray;
			}
			code = String.valueOf(charArray);
		}
		return code;
	}
	
	/**
	 * 金额是否为空
	 * @param value
	 * @return
	 */
	public static boolean isNullNum(DZFDouble value){
		if(value == null || value.doubleValue() == 0 ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 数组转map
	 * @param cvos1
	 * @return
	 */
	public static Map<String, YntCpaccountVO> conVertMap(YntCpaccountVO[] cvos1){
		if(cvos1 == null || cvos1.length == 0){
			return null;
		}
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		for(YntCpaccountVO c : cvos1){
			String code = c.getAccountcode();
			map.put(code, c);
		}
		return map;
	}
	

	/**
	 * 数组转map
	 * @param cvos1
	 * @return
	 */
	public static Map<String, FseJyeVO> conVertMapFs(List<FseJyeVO> cvos1){
		if(cvos1 == null || cvos1.size() == 0){
			return null;
		}
		Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
		for(FseJyeVO c : cvos1){
			map.put(c.getKmbm(), c);
		}
		return map;
	}
	
	
	public static String[] filterNull(String[] strs) {
		if (strs != null && strs.length > 0) {
			List<String> list = new ArrayList<String>();
			for (String str : strs) {
				if (!StringUtil.isEmpty(str)) {
					list.add(str);
				}
			}
			return list.toArray(new String[0]);
		}
		return null;
	}
	
	
	public static String getKmDirectParent(Map<String, YntCpaccountVO> mp, String kmpk) {
		YntCpaccountVO vo1 = mp.get(kmpk);
		for (YntCpaccountVO vo : mp.values()) {
			if (vo1.getAccountcode().startsWith(vo.getAccountcode())
					&& vo1.getAccountlevel().intValue() == vo.getAccountlevel().intValue() + 1) {
				return vo.getPk_corp_account();
			}
		}
		return "";
	}
	
	public static List<String> getKmParent(Map<String, YntCpaccountVO> mp, String kmpk) {
		YntCpaccountVO vo1 = mp.get(kmpk);
		List<String> ls = new ArrayList<String>();
		for (YntCpaccountVO vo : mp.values()) {
			if (vo1.getAccountcode().startsWith(vo.getAccountcode())
					&& vo1.getAccountcode().length() > vo.getAccountcode().length()) {
				ls.add(vo.getPk_corp_account());
			}
		}
		return ls;
	}
	
	
	public static String formatQj(String qj){
		if(StringUtil.isEmpty(qj)){
			return qj;
		}
		String[] qjs = null;
		if(qj.indexOf("--")>=0){
			qjs = qj.split("--");
		}else if(qj.indexOf("~")>=0){
			qjs = qj.split("~");
		}
		if(qjs ==null || qjs.length!=2){
			if(qj.indexOf("-")>=0){
				return DateUtils.getPeriodEndDate(qj.substring(0, 7)).toString().replace("-", "");
			}else{
				return qj;
			}
		}
		String qj1 = DateUtils.getPeriodStartDate(qjs[0]).toString().replace("-","");
		String qj2 = DateUtils.getPeriodEndDate(qjs[1]).toString().replace("-","");
		return "("+qj1+"-"+qj2+")";
	}
	
	public static String formatqj(String[] periods){
		
		if(periods == null || periods.length ==0){
			return "";
		}
		if(periods.length<2){
			return DateUtils.getPeriodEndDate(periods[0].substring(0, 7)).toString().replace("-", "");
		}
		
		String qj1 = DateUtils.getPeriodStartDate(periods[0]).toString().replace("-", "");
		String qj2 = DateUtils.getPeriodEndDate(periods[periods.length-1]).toString().replace("-", "");
		return "("+qj1+"-"+qj2+")";
		
	}
	/**
	 * 是否收入项目
	 *
	 * @param lrbvo
	 * @return
	 */
	public static boolean bsrxm(LrbVO lrbvo) {
		if ((lrbvo.getXm().indexOf("自营业收入") >= 0 || lrbvo.getXm().indexOf("公允价值损益") >= 0
				|| lrbvo.getXm().indexOf("投资收益") >= 0 || lrbvo.getXm().indexOf("营业外收入") >= 0)
				&& "00000100AA10000000000BMF".equals(lrbvo.getKmfa())) {// 07会计准则
			return true;
		} else if ((lrbvo.getXm().indexOf("营业收入") >= 0 || lrbvo.getXm().indexOf("营业外收入") >= 0
				|| lrbvo.getXm().indexOf("投资收益") >= 0) && !"00000100AA10000000000BMF".equals(lrbvo.getKmfa())) {
			return true;
		}
		return false;
	}
	/**
	 * 净利润
	 * @param lrbvo
	 * @return
	 */
	public static boolean bjlrxm(LrbVO lrbvo) {
		if (lrbvo.getXm().indexOf("净利润") >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否费用
	 *
	 * @param lrbvo
	 * @return
	 */
	public static boolean bfyxm(LrbVO lrbvo) {
		if (lrbvo.getXm().indexOf("营业支出") >= 0 || lrbvo.getXm().indexOf("营业外支出") >= 0
				|| lrbvo.getXm().indexOf("税金及附加") >= 0 || lrbvo.getXm().indexOf("销售费用") >= 0
				|| lrbvo.getXm().indexOf("管理费用") >= 0 || lrbvo.getXm().indexOf("财务费用") >= 0
				|| lrbvo.getXm().indexOf("所得税费用") >= 0) {
			return true;
		}
		return false;
	}
	/**
	 * 是否支出类
	 * @param lrbvo
	 * @return
	 */
	public static boolean bzcxm(LrbVO lrbvo) {
		if (lrbvo.getXm().indexOf("营业成本") >= 0 || lrbvo.getXm().indexOf("营业外成本") >= 0) {
			return true;
		}
		return false;
	}
}
