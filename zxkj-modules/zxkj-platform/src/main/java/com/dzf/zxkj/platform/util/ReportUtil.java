package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
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
import com.dzf.zxkj.platform.services.icset.IInventoryService;
import com.dzf.zxkj.platform.services.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.services.sys.IParameterSetService;
import com.dzf.zxkj.platform.services.sys.IUserService;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportUtil {

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

	public static String getCurrencyByPk(String pk_currency){
		if(StringUtil.isEmpty(pk_currency)){
			return "元";
		}
		IBDCurrencyService ibdCurrencyService = SpringUtils.getBean(IBDCurrencyService.class);
		BdCurrencyVO bdCurrencyVO = ibdCurrencyService.queryCurrencyVOByPk(pk_currency);
		return getCurrencyDw(bdCurrencyVO == null ? "" : bdCurrencyVO.getCurrencyname());
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
	
	public static boolean checkHasRight (String cuserid, String pk_corp) {
		IUserService userService = (IUserService) SpringUtils.getBean("userServiceImpl");
		Set<String> powercorpSet = userService.querypowercorpSet(cuserid);
		if(!powercorpSet.contains(pk_corp))
			return false;
		else
			return true;
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
	 * 启用库存时获取存货辅助核算
	 * @param pk_corp
	 * @return
	 */
	public static Map<String, AuxiliaryAccountBVO> getInvAuaccount(String pk_corp) {
		IInventoryService ic_inventoryserv = (IInventoryService) SpringUtils.getBean("ic_inventoryserv");
		Map<String, AuxiliaryAccountBVO> invmap = new HashMap<String, AuxiliaryAccountBVO>();
		List<InventoryVO> list = ic_inventoryserv.queryInfo(pk_corp,null);

		if (list == null || list.isEmpty()) {
			return invmap;
		}
		AuxiliaryAccountBVO bvo1 = null;
		for (InventoryVO invo : list) {
			if (!invmap.containsKey(invo.getPk_inventory())) {
				bvo1 = new AuxiliaryAccountBVO();
				bvo1.setCode(invo.getCode());
				bvo1.setName(invo.getName());
				bvo1.setPk_auacount_b(invo.getPk_inventory());
				bvo1.setUnit(invo.getMeasurename());
				bvo1.setPk_corp(invo.getPk_corp());
				invmap.put(invo.getPk_inventory(), bvo1);
			}
		}
		return invmap;

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
	
	//获取汇率精度
	public static Integer getHlJd(String pk_corp) {
		
		IParameterSetService sys_parameteract = (IParameterSetService) SpringUtils.getBean("sys_parameteract");
		
		Integer hljd = 6;
		YntParameterSet setvo = sys_parameteract.queryParamterbyCode(pk_corp, "dzf011");

		if (setvo != null && !StringUtil.isEmpty(setvo.getParametervalue())) {
			String[] paramvalu = setvo.getParametervalue().split(";");
			hljd = Integer.parseInt(paramvalu[setvo.getPardetailvalue()]);
		}
		return hljd;
	}
	
	
	/**
	 * 发生额余额查询参数
	 * @param pk_corp 公司
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param kmArray 要查询的科目(4/2/2/2/2)
	 * @param isNewCode 是否是新科目编码
	 * @return
	 */
	public static QueryParamVO getFseQueryParamVO(CorpVO corp, DZFDate beginDate,
												  DZFDate endDate, String[] kmArray, boolean isNewCode){
		String rule = corp.getAccountcoderule();
		List<String> kmList = null;
		if (isNewCode || rule == null || DZFConstant.ACCOUNTCODERULE.equals(rule)) {
			kmList = Arrays.asList(kmArray);
		} else {
			kmList = new ArrayList<String>();
			for (String code : kmArray) {
				String newCode = KmbmUpgrade.getNewCode(code, DZFConstant.ACCOUNTCODERULE, rule);
				kmList.add(newCode);
			}
		}
		Collections.sort(kmList);
		kmList.toArray(kmArray);
		QueryParamVO paramvo = new QueryParamVO();
		String kms = StringUtil.getUnionStr(kmArray, ",", "");
		paramvo.setBegindate1(beginDate);
		paramvo.setEnddate(endDate);
		paramvo.setBtotalyear(DZFBoolean.TRUE);
		paramvo.setCjq(1);
		paramvo.setCjz(6);
		// 包含未记账凭证  
		paramvo.setIshasjz(DZFBoolean.FALSE);
		// 显示辅助项目
		paramvo.setSfzxm(DZFBoolean.FALSE);
		// 无余额无发生不显示
		paramvo.setXswyewfs(DZFBoolean.TRUE);
		// 有余额无发生不显示
		paramvo.setIshowfs(DZFBoolean.TRUE);
		paramvo.setIsnomonthfs(DZFBoolean.TRUE);
		paramvo.setPk_corp(corp.getPk_corp());
		paramvo.setKms_first(kmArray[0]);
		paramvo.setKms_last(kmArray[kmArray.length - 1]);
		paramvo.setKms(kms);
		paramvo.setKmcodelist(kmList);
		return paramvo;
	}
	
	/**
	 *  获取Excel单元格值
	 * @param cell
	 * @param isNum
	 * @return
	 */
	public static String getExcelCellValue(Cell cell, boolean isNum) {
		String val = null;
		if (cell == null) {
			return val;
		}
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			val = cell.getRichStringCellValue().getString();
			val = val.replaceAll("^( | )+|( | )+$", "");
		} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			val = dateFormatter.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			DecimalFormat format = isNum ? new DecimalFormat("#.########") : new DecimalFormat("#");
			double numVal = cell.getNumericCellValue();
			val = format.format(numVal);
		}
		return val;
	}
	
	
	public static Map<String, String> getLevelKmName(YntCpaccountVO[] cpavos,Integer subjectShow){
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
	public static Map<String,YntCpaccountVO> conVertMap(YntCpaccountVO[] cvos1){
		if(cvos1 == null || cvos1.length == 0){
			return null;
		}
		Map<String,YntCpaccountVO> map = new HashMap<String,YntCpaccountVO>();
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
		Map<String,FseJyeVO> map = new HashMap<String,FseJyeVO>();
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
}
