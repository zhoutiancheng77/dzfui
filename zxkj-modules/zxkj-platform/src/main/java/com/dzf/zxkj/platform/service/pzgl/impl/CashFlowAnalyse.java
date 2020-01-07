package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.enums.KmschemaCash;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;

import java.util.*;

public class CashFlowAnalyse {
	
	private SingleObjectBO singleObjectBO = null;
	public CashFlowAnalyse(SingleObjectBO singleObjectBO){
		this.singleObjectBO = singleObjectBO;
	}
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	private static int[] oldRule = new int[]{4,2,2,2,2,2};
	
	
	class XjllJSVo{
		DZFDouble jfCash = DZFDouble.ZERO_DBL;//现金类借方总金额
		DZFDouble dfCash = DZFDouble.ZERO_DBL;//现金类贷方总金额
		DZFDouble jfNCash = DZFDouble.ZERO_DBL;//非现金类借方总金额
		DZFDouble dfNCash = DZFDouble.ZERO_DBL;//非现金类贷方总金额
		public DZFDouble getJfCash() {
			return jfCash;
		}
		public void setJfCash(DZFDouble jfCash) {
			this.jfCash = jfCash;
		}
		public DZFDouble getDfCash() {
			return dfCash;
		}
		public void setDfCash(DZFDouble dfCash) {
			this.dfCash = dfCash;
		}
		public DZFDouble getJfNCash() {
			return jfNCash;
		}
		public void setJfNCash(DZFDouble jfNCash) {
			this.jfNCash = jfNCash;
		}
		public DZFDouble getDfNCash() {
			return dfNCash;
		}
		public void setDfNCash(DZFDouble dfNCash) {
			this.dfNCash = dfNCash;
		}
		
		
	}
	
	public List<XjllVO> autoAnalyse(TzpzHVO hvo, CorpVO corp, YntCpaccountVO[] cpavos){
		if(!StringUtil.isEmpty(corp.getPrimaryKey())){
			CorpVO qrycorpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, corp.getPk_corp());
			if("元年".equals(qrycorpvo.getVcustsource())){
				return null;
			}
		}
		String pk_corp = hvo.getPk_corp();
		HashMap<String, BdtradecashflowVO> idMap = queryCashFlow(corp);
//		BdtradeAccountSchemaVO accSchema = queryAccountSchema(corp);
		IYntBoPubUtil pubUtil = SpringUtils.getBean(IYntBoPubUtil.class);
		Integer accSchema = pubUtil.getAccountSchema(pk_corp);
		String ruleStr = corp.getAccountcoderule();
		if (StringUtil.isEmpty(ruleStr)) {
			ruleStr = DZFConstant.ACCOUNTCODERULE;
		}
		String[]  newRule = ruleStr.split("/");
		TzpzBVO[] bvos = (TzpzBVO[])hvo.getChildren();
		// 合并相同科目
		bvos = mergeSameAccount(bvos, corp.getCorptype(),cpavos);
		LinkedHashMap<String, DZFDouble> jfNCashMap = new LinkedHashMap<String, DZFDouble>();//非现金类借方
		LinkedHashMap<String, DZFDouble> dfNCashMap = new LinkedHashMap<String, DZFDouble>();//非现金类贷方
		XjllJSVo jsvo = new XjllJSVo();
		DZFDouble mny  = new DZFDouble();
		/*
		 * 凭证中用到的借方科目编码前四位
		 * 用于查看是否存在某个科目，现在只在分析进项税额时使用
		 */
		HashSet<String> jfcodeSet = new HashSet<String>();
		for (TzpzBVO bvo : bvos) {
			DZFDouble jfmny = bvo.getJfmny();
//			DZFDouble dfmny = bvo.getDfmny();
			if (jfmny != null && !jfmny.equals(DZFDouble.ZERO_DBL)) {
				jfcodeSet.add(bvo.getVcode().substring(0, 4));
			}
		}
		if (accSchema != null && accSchema.intValue() == DzfUtil.THIRTEENSCHEMA.intValue()) {//2013小会计
			for (TzpzBVO bvo: bvos) {
				create2013XjllFx(bvo, bvos,jsvo, newRule, jfcodeSet, jfNCashMap, dfNCashMap,corp.getCorptype(),cpavos);
			}
		}else if (accSchema != null && accSchema.intValue() == DzfUtil.SEVENSCHEMA.intValue()) {//小企业2007新会计
			for(TzpzBVO bvo: bvos){
				create2007XjllFx(bvo, bvos, jsvo, newRule, jfcodeSet, jfNCashMap, dfNCashMap,corp.getCorptype(),cpavos);
			}
		}else if(accSchema != null  && accSchema.intValue() ==  DzfUtil.POPULARSCHEMA.intValue()){//民间
			for(TzpzBVO bvo:bvos){
				createPopualrXjllFx(bvo, bvos, jsvo, newRule, jfcodeSet, jfNCashMap, dfNCashMap,corp.getCorptype(),cpavos);
			}
		}else if(accSchema !=null && accSchema.intValue() == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){//企业会计制度
			for(TzpzBVO bvo:bvos){
				createPopualrXjllQyKj(bvo, bvos, jsvo, newRule, jfcodeSet, jfNCashMap, dfNCashMap,corp.getCorptype(),cpavos);
			}
		}else{//暂不支持的行业
			return null;
		}
		
		DZFDouble jfCash =jsvo.getJfCash() ;//现金类借方总金额
		DZFDouble dfCash = jsvo.getDfCash() ;//现金类贷方总金额
		DZFDouble jfNCash = jsvo.getJfNCash() ;//非现金类借方总金额
		DZFDouble dfNCash = jsvo.getDfNCash() ;//非现金类贷方总金额
		
		if (hvo.getJfmny().compareTo(jfCash) == 0 && hvo.getDfmny().compareTo(dfCash) == 0) {
			// 全为现金科目
			return null;
		}
		List<XjllVO> xjllList = new ArrayList<XjllVO>();
		//现金类科目借方
		Integer lineint =null;
		Integer imark = 1;
		BdtradecashflowVO  xjlxmvo= null;
		if(jfCash.compareTo(dfNCash) > -1){
			for(String line: dfNCashMap.keySet()){
				XjllVO vo = new XjllVO();
				lineint = Integer.parseInt(line);
				if(lineint<0){
					imark =-1;
				}else{
					imark =1;
				}
				xjlxmvo = idMap.get(String.valueOf(Math.abs(lineint)));
				vo.setNmny(dfNCashMap.get(line).multiply(imark));
				jfCash  = jfCash.sub(dfNCashMap.get(line));
				vo.setVdirect(xjlxmvo !=null ?  xjlxmvo.getDirection() :0);
				vo.setPk_xjllxm(xjlxmvo !=null ? xjlxmvo.getPk_trade_cashflow() :"");
				vo = setDefault(vo, hvo);
				xjllList.add(vo);
			}
		} else {
			for(String line: dfNCashMap.keySet()){
				lineint = Integer.parseInt(line);
				if(lineint<0){
					imark =-1;
				}else{
					imark =1;
				}
				xjlxmvo = idMap.get(String.valueOf(Math.abs(lineint)));
				mny = dfNCashMap.get(line).multiply(imark);
				if (jfCash.doubleValue() == 0)
					break;
				XjllVO vo = new XjllVO();
				if((jfCash.compareTo(mny) < 0 && jfCash.doubleValue() > 0)
						||( jfCash.abs().compareTo(mny.abs()) < 0 && mny.doubleValue() < 0)) {
					vo.setNmny(jfCash.multiply(imark));
					jfCash  = DZFDouble.ZERO_DBL;
				}
				else{
					vo.setNmny(mny);
					jfCash  = imark > 0 ? jfCash.sub(mny) : jfCash.add(mny);
				}
				vo.setVdirect(xjlxmvo != null ?  xjlxmvo.getDirection():0);
				vo.setPk_xjllxm(xjlxmvo != null ? xjlxmvo.getPk_trade_cashflow():"");
				vo = setDefault(vo, hvo);
				xjllList.add(vo);
			}
		}
		//现金类科目贷方
		if(jfNCash.compareTo(dfCash) > -1){
			for(String line: jfNCashMap.keySet()){
				lineint = Integer.parseInt(line);
				if(lineint<0){
					imark =-1;
				}else{
					imark =1;
				}
				xjlxmvo = idMap.get(String.valueOf(Math.abs(lineint)));
				mny = jfNCashMap.get(line).multiply(imark);
				if (dfCash.doubleValue() == 0)
					break;
				XjllVO vo = new XjllVO();
				if((dfCash.compareTo(mny) < 0 && dfCash.doubleValue() > 0)
						|| (dfCash.abs().compareTo(mny.abs()) <0 && mny.doubleValue() < 0)){
					vo.setNmny(dfCash.multiply(imark));
					dfCash = DZFDouble.ZERO_DBL;
				}
				else {
					vo.setNmny(mny);
					dfCash = imark > 0 ? dfCash.sub(mny) : dfCash.add(mny);
				}
				vo.setVdirect(xjlxmvo !=null ? xjlxmvo.getDirection():1);
				vo.setPk_xjllxm(xjlxmvo !=null ?  xjlxmvo.getPk_trade_cashflow():"");
				vo = setDefault(vo, hvo);
				xjllList.add(vo);
			}
		} else {
			for(String line: jfNCashMap.keySet()){
				lineint = Integer.parseInt(line);
				if(lineint<0){
					imark =-1;
				}else{
					imark =1;
				}
				xjlxmvo = idMap.get(String.valueOf(Math.abs(lineint)));
				XjllVO vo = new XjllVO();
				vo.setNmny(jfNCashMap.get(line).multiply(imark));
				dfCash = dfCash.sub(jfNCashMap.get(line));
				vo.setVdirect(xjlxmvo !=null ? xjlxmvo.getDirection():1);
				vo.setPk_xjllxm(xjlxmvo !=null ?  xjlxmvo.getPk_trade_cashflow():"");
				vo = setDefault(vo, hvo);
				xjllList.add(vo);
			}
		}
		if (xjllList.size() > 0) {
			hvo.setIsfpxjxm(DZFBoolean.TRUE);
			hvo.setError_cash_analyse(checkError(hvo, xjllList, null, corp,cpavos));
		} else {
			// 未分析出来也标识错误
			hvo.setError_cash_analyse(true);
		}
		return xjllList;
	}
	
	/**
	 * 企业会计制度  现金流量分析
	 * @param bvo
	 * @param bvos
	 * @param jsvo
	 * @param newRule
	 * @param jfcodeSet
	 * @param jfNCashMap
	 * @param dfNCashMap
	 */
	private void createPopualrXjllQyKj(TzpzBVO bvo, TzpzBVO[] bvos, XjllJSVo jsvo, String[] newRule,
			HashSet<String> jfcodeSet, LinkedHashMap<String, DZFDouble> jfNCashMap,
			LinkedHashMap<String, DZFDouble> dfNCashMap,String corptype,YntCpaccountVO[] cpavos) {
		DZFDouble jfCash =jsvo.getJfCash() ;//现金类借方总金额
		DZFDouble dfCash = jsvo.getDfCash() ;//现金类贷方总金额
		DZFDouble jfNCash = jsvo.getJfNCash() ;//非现金类借方总金额
		DZFDouble dfNCash = jsvo.getDfNCash() ;//非现金类贷方总金额
		String vcode = bvo.getVcode();
		DZFDouble jfmny = bvo.getJfmny() == null ? DZFDouble.ZERO_DBL:bvo.getJfmny();
		DZFDouble dfmny = bvo.getDfmny() == null ? DZFDouble.ZERO_DBL:bvo.getDfmny();
		String[] slcode =new String[]{getNewCode(newRule, "21710101", 3)};
		if(KmschemaCash.isCashAccount(cpavos,vcode, corptype)){
			if(jfmny.doubleValue()!=0){
				jfCash = jfCash.add(jfmny);
			}else if(dfmny.doubleValue() != 0){
				dfCash = dfCash.add(dfmny);
			}
		} else if(vcode.startsWith("5101") || vcode.startsWith("5102")
				|| vcode.startsWith("1111") || vcode.startsWith("1131")
				|| vcode.startsWith("2131") || vcode.startsWith(getNewCode(newRule, "21710105", 3))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-1",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "1",bvo,bvos,corptype,vcode,cpavos);
			}
			
		}else if(vcode.startsWith("1161") ||vcode.startsWith("5203")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-3",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "3",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1211")|| vcode.startsWith("1201")
				|| vcode.startsWith("1221") || vcode.startsWith("1231")
				|| vcode.startsWith("2121") || vcode.startsWith("2111") 
				|| vcode.startsWith("1151") || vcode.startsWith("1251")  || vcode.startsWith("5405")
				|| vcode.startsWith(getNewCode(newRule, "21710101", 3))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"10",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-10",bvo,bvos,corptype,vcode,cpavos);
			}
			
		}else if(vcode.startsWith("2151") || vcode.startsWith(getNewCode(newRule, "550101", 2))  
				|| vcode.startsWith(getNewCode(newRule, "550201", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"12",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-12",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if((vcode.startsWith("2171") && !vcode.startsWith(getNewCode(newRule, "217101", 2))
				 && !vcode.startsWith(getNewCode(newRule, "217102", 2)) )
				 || vcode.startsWith(getNewCode(newRule, "550120", 2))
				 || vcode.startsWith(getNewCode(newRule, "550221", 2))
				 || vcode.startsWith("5402") 
				){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"13",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-13",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1401") || vcode.startsWith("1101")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"31",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"22",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("5201")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-23",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"23",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1701")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-25",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"25",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1801")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"30",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"25",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1501") || vcode.startsWith("1603")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"30",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-30",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1121") || vcode.startsWith("1122") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"35",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-35",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("3101") || vcode.startsWith("3111") || vcode.startsWith("2502")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-38",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"38",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("2101") || vcode.startsWith("2301")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"45",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"40",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("2161")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"46",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-46",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith(getNewCode(newRule, "217102", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"13",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode, "1", bvo, bvos,corptype, vcode,cpavos);
			}
		}else {
//			if(vcode.startsWith(getNewCode(newRule, "21710101", 3))){//21710101的算法
//				return;
//			}
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "18",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode, "8", bvo, bvos,
						corptype, vcode,cpavos);
			}
		}
		
		jsvo.setJfCash(jfCash);
		jsvo.setDfCash(dfCash);
		jsvo.setJfNCash(jfNCash);
		jsvo.setDfNCash(dfNCash);
	}
	/**
	 * 民间现金流量分析
	 * @param bvo
	 * @param bvos
	 * @param jsvo
	 * @param newRule
	 * @param jfcodeSet
	 * @param jfNCashMap
	 * @param dfNCashMap
	 */
	private void createPopualrXjllFx(TzpzBVO bvo, TzpzBVO[] bvos, XjllJSVo jsvo, String[] newRule,
			HashSet<String> jfcodeSet, LinkedHashMap<String, DZFDouble> jfNCashMap,
			LinkedHashMap<String, DZFDouble> dfNCashMap,String corptype,YntCpaccountVO[] cpavos) {
		DZFDouble jfCash =jsvo.getJfCash() ;//现金类借方总金额
		DZFDouble dfCash = jsvo.getDfCash() ;//现金类贷方总金额
		DZFDouble jfNCash = jsvo.getJfNCash() ;//非现金类借方总金额
		DZFDouble dfNCash = jsvo.getDfNCash() ;//非现金类贷方总金额
		String vcode = bvo.getVcode();
		DZFDouble jfmny = bvo.getJfmny() == null ? DZFDouble.ZERO_DBL:bvo.getJfmny();
		DZFDouble dfmny = bvo.getDfmny() == null ? DZFDouble.ZERO_DBL:bvo.getDfmny();
		String[] slcode = new String[]{"2206"};
		if(KmschemaCash.isCashAccount(cpavos,vcode, corptype)){
			if(jfmny.doubleValue()!=0){
				jfCash = jfCash.add(jfmny);
			}else if(dfmny.doubleValue() != 0){
				dfCash = dfCash.add(dfmny);
			}
		} else if(vcode.startsWith("4101")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-1",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "1",bvo,bvos,corptype,vcode,cpavos);
			}
			
		}else if(vcode.startsWith("4201")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-2",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "2",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("4301")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-3",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "3",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("4501")|| vcode.startsWith("1111")
				|| vcode.startsWith("1121") || vcode.startsWith("2203")){
				//|| vcode.startsWith(getNewCode(newRule, "22060102", 3)) ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-4",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"4",bvo,bvos,corptype,vcode,cpavos);
			}
			
		}else if(vcode.startsWith("4401") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-5",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"5",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1122") || vcode.startsWith("4901")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-8",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"8",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else if(vcode.startsWith("5101") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"14",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-14",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("2204") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"15",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-15",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1141") || vcode.startsWith("1201")
				|| vcode.startsWith("2201")|| vcode.startsWith("2202")){
				//|| vcode.startsWith(getNewCode(newRule, "22060101", 3)) ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"16",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-16",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1301") || vcode.startsWith("2209")
				|| vcode.startsWith("2301")|| vcode.startsWith("2401")
				|| vcode.startsWith("5201") || vcode.startsWith("5401")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"19",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-19",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("2206")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"19",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"8",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("4601") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-26",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"26",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1509")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-27",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"27",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1501")||vcode.startsWith("1505")
				|| vcode.startsWith("1506")||vcode.startsWith("1601") ||vcode.startsWith("1701")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"35",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-35",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1101")||vcode.startsWith("1401")
				|| vcode.startsWith("1402")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"36",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"25",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("3101")||vcode.startsWith("3102")  ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"39",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"48",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("2101")||vcode.startsWith("2501")  ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"51",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"45",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("2502") || vcode.startsWith("5301")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"55",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-55",bvo,bvos,corptype,vcode,cpavos);
			}
		}else {
//			if(vcode.startsWith(getNewCode(newRule, "22210101", 3))){//22210101的算法
//				return;
//			}
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "14",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "3",bvo,bvos,corptype,vcode,cpavos);
			}
		}
		jsvo.setJfCash(jfCash);
		jsvo.setDfCash(dfCash);
		jsvo.setJfNCash(jfNCash);
		jsvo.setDfNCash(dfNCash);
		
	}
	
	private HashMap<String, BdtradecashflowVO> queryCashFlow(CorpVO corp){
		SQLParameter sp = new SQLParameter();
		HashMap<String, BdtradecashflowVO> idMap = new HashMap<String, BdtradecashflowVO>();
		String condition = " nvl(dr,0) = 0 and pk_trade_accountschema = ? ";
		sp.addParam(corp.getCorptype());
		BdtradecashflowVO[] result = (BdtradecashflowVO[]) getSingleObjectBO().queryByCondition(BdtradecashflowVO.class, condition, sp);
		if (result != null && result.length > 0){
			for(BdtradecashflowVO cashvo: result){
				idMap.put(cashvo.getItemcode(), cashvo);
			}
		}
		return idMap;
	}
	
	private String getNewCode (String[] newRule, String code, int level) {
		int beginIndex = 0;
		String newCode = "";
		for (int i = 0; i < level; i++) {
			int codelen = oldRule[i];
			String oldpartCode = code.substring(beginIndex, beginIndex + codelen);
			beginIndex += codelen;
			String newPartCode = getNewPartCode(newRule[i], oldpartCode);
			newCode += newPartCode;
		}
		return newCode;
	}
	
	private String getNewPartCode(String newcodeRulePart, String oldpartCode){
		
		String newPartCode = oldpartCode;
		int newPartLen = Integer.parseInt(newcodeRulePart);
		int oldPartLen = oldpartCode.trim().length();
		if(oldPartLen==newPartLen){
			return newPartCode;
		}
	    
		for(int i = 0; i < (newPartLen - oldPartLen); i++){
			newPartCode = "0" + newPartCode;
		}
		
		return newPartCode;
	}
	
	private void create2007XjllFx(TzpzBVO bvo, TzpzBVO[] bvos,XjllJSVo jsvo,String[] newRule,HashSet<String> jfcodeSet,
			LinkedHashMap<String, DZFDouble> jfNCashMap,LinkedHashMap<String, DZFDouble> dfNCashMap,String corptype,YntCpaccountVO[] cpavos){
		DZFDouble jfCash =jsvo.getJfCash() ;//现金类借方总金额
		DZFDouble dfCash = jsvo.getDfCash() ;//现金类贷方总金额
		DZFDouble jfNCash = jsvo.getJfNCash() ;//非现金类借方总金额
		DZFDouble dfNCash = jsvo.getDfNCash() ;//非现金类贷方总金额
		String vcode = bvo.getVcode();
		DZFDouble jfmny = bvo.getJfmny() == null ? DZFDouble.ZERO_DBL:bvo.getJfmny();
		DZFDouble dfmny = bvo.getDfmny() == null ? DZFDouble.ZERO_DBL:bvo.getDfmny();
		String[] slcode = new String[]{getNewCode(newRule, "22210101", 3),getNewCode(newRule, "222110", 2)};
		if(KmschemaCash.isCashAccount(cpavos,vcode, corptype)){
			if(jfmny.doubleValue()!=0){
				jfCash = jfCash.add(jfmny);
			}else if(dfmny.doubleValue() != 0){
				dfCash = dfCash.add(dfmny);
			}
		} else if(vcode.startsWith("1403") || vcode.startsWith("1405") || vcode.startsWith("1401")
				|| vcode.startsWith("1411") || vcode.startsWith("2202") || vcode.startsWith("2201")
				|| vcode.startsWith("1123") || vcode.startsWith("1408") || vcode.startsWith("6402") || vcode.startsWith("6401")
				|| vcode.startsWith(getNewCode(newRule, "22210101", 3)) || vcode.startsWith(getNewCode(newRule, "222110", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "6",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-6",bvo,bvos,corptype,vcode,cpavos);
			}
			
		} else if(vcode.startsWith("2211") || vcode.startsWith(getNewCode(newRule, "660101", 2)) || vcode.startsWith(getNewCode(newRule, "660201", 2))
				|| vcode.startsWith(getNewCode(newRule, "222105", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"7",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"-7",bvo,bvos,corptype,vcode,cpavos);
			}
			
		} else if( (vcode.startsWith("2221") && !vcode.startsWith(getNewCode(newRule, "222101", 2)) 
				&& !vcode.startsWith(getNewCode(newRule, "222105", 2)) && !vcode.startsWith(getNewCode(newRule, "222109", 2)))
				|| vcode.startsWith(getNewCode(newRule, "660120", 2))// || vcode.startsWith("6051")
				|| vcode.startsWith(getNewCode(newRule, "660221", 2))
				|| vcode.startsWith("6403")
				){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"8",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-8",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1601") || vcode.startsWith("1604") || vcode.startsWith("5301") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "19",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-19",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1511") || vcode.startsWith("1501") ){//|| vcode.startsWith("1101")
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "20",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "13",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1131") || vcode.startsWith("1132")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "22",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-22",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("2001") || vcode.startsWith("2501")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "30",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "27",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if (vcode.startsWith("2232")
				|| vcode.startsWith(getNewCode(newRule, "660301", 2)) && jfmny.doubleValue() > 0) {
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "31",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-31",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("6001")   || vcode.startsWith("6051")
				|| vcode.startsWith("1121") || vcode.startsWith("1122") || vcode.startsWith("2203")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-2",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "2",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if( vcode.startsWith(getNewCode(newRule, "22210102", 3))  ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "8",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "2",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith(getNewCode(newRule, "630101", 2)) ||    vcode.startsWith("1503")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"-13",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny, slcode,"13",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else if(vcode.startsWith("6111")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-14",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "14",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1606")){//1606在贷方，计为正数
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-15",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "15",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1701")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "19",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "15",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1511")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-16",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "16",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("4001") || vcode.startsWith("4002") || vcode.startsWith("2502")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-26",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "26",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else if(vcode.startsWith(getNewCode(newRule, "222109", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "8",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "2",bvo,bvos,corptype,vcode,cpavos);
			}
		} else {
//			if(vcode.startsWith(getNewCode(newRule, "22210101", 3))){//22210101的算法
//				return;
//			}
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "9",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "4",bvo,bvos,corptype,vcode,cpavos);
			}
		}
		
		jsvo.setJfCash(jfCash);
		jsvo.setDfCash(dfCash);
		jsvo.setJfNCash(jfNCash);
		jsvo.setDfNCash(dfNCash);
		
	}
	
	
	
	private void create2013XjllFx(TzpzBVO bvo, TzpzBVO[] bvos, XjllJSVo jsvo,String[] newRule,HashSet<String> jfcodeSet,
			LinkedHashMap<String, DZFDouble> jfNCashMap,LinkedHashMap<String, DZFDouble> dfNCashMap,String corptype
			,YntCpaccountVO[] cpavos){
		
		DZFDouble jfCash =jsvo.getJfCash() ;//现金类借方总金额
		DZFDouble dfCash = jsvo.getDfCash() ;//现金类贷方总金额
		DZFDouble jfNCash = jsvo.getJfNCash() ;//非现金类借方总金额
		DZFDouble dfNCash = jsvo.getDfNCash() ;//非现金类贷方总金额
		String vcode = bvo.getVcode();
		DZFDouble jfmny = bvo.getJfmny() == null ? DZFDouble.ZERO_DBL:bvo.getJfmny();
		DZFDouble dfmny = bvo.getDfmny() == null ? DZFDouble.ZERO_DBL:bvo.getDfmny();
		String[] slcode =new String[]{getNewCode(newRule, "22210101", 3),getNewCode(newRule, "222110", 2)};
		//是否是现金类科目
		if(KmschemaCash.isCashAccount(cpavos,vcode, corptype)){
			if(jfmny.doubleValue()!=0){
				jfCash = jfCash.add(jfmny);
			}else if(dfmny.doubleValue()!=0){
				dfCash = dfCash.add(dfmny);
			}
		} else  if(vcode.startsWith("1403") || vcode.startsWith("1405") || vcode.startsWith("1401") || vcode.startsWith("1411") || vcode.startsWith("2202")
				|| vcode.startsWith("2201") || vcode.startsWith("1123") || vcode.startsWith("5401")
				|| vcode.startsWith(getNewCode(newRule, "22210101", 3)) || vcode.startsWith(getNewCode(newRule, "222110", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "3",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-3",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else if(vcode.startsWith("2211") || vcode.startsWith(getNewCode(newRule, "560101", 2)) || vcode.startsWith(getNewCode(newRule, "560201", 2))
				|| vcode.startsWith(getNewCode(newRule, "222105", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"4",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-4",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if((vcode.startsWith("2221") && !vcode.startsWith(getNewCode(newRule, "222101", 2))
				&& !vcode.startsWith(getNewCode(newRule, "222109", 2)))
				|| vcode.startsWith(getNewCode(newRule, "560120", 2)) || vcode.startsWith(getNewCode(newRule, "560222", 2))
				|| vcode.startsWith("5403")
				){
			
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "5",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-5",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1601") || vcode.startsWith("1604") || vcode.startsWith("4301") ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny, slcode,"12",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-12",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("2001") || vcode.startsWith("2501")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "16",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "14",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith(getNewCode(newRule, "56030101", 3))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "17",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-17",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("2232")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "18",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "-18",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else if(vcode.startsWith("5001")   || vcode.startsWith("5051") || vcode.startsWith("1121")
				|| vcode.startsWith("1122") || vcode.startsWith("2203")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-1",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "1",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith(getNewCode(newRule, "22210102", 3)) ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "5",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "1",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith("1511") || vcode.startsWith("1101") || vcode.startsWith("1131") || vcode.startsWith("1132")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "12",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "8",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("5111")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-9",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "9",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1606")   ){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-10",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "10",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("1701")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "11",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "10",bvo,bvos,corptype,vcode,cpavos);
			}
		} else if(vcode.startsWith("3001") || vcode.startsWith("3002")){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "-15",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "15",bvo,bvos,corptype,vcode,cpavos);
			}
		}else if(vcode.startsWith(getNewCode(newRule, "222109", 2))){
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "5",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "1",bvo,bvos,corptype,vcode,cpavos);
			}
		}  else  {
//			if(vcode.startsWith(getNewCode(newRule, "22210101", 3))){//22210101的算法
//				return;
//			}
			if(jfmny.doubleValue()!=0){
				jfNCash = putMapAndMnyValue(jfNCashMap, jfNCash, jfmny,slcode, "6",bvo,bvos,corptype,vcode,cpavos);
			}else if(dfmny.doubleValue()!=0){
				dfNCash = putMapAndMnyValue(dfNCashMap, dfNCash, dfmny,slcode, "2",bvo,bvos,corptype,vcode,cpavos);
			}
		}
		
		jsvo.setJfCash(jfCash);
		jsvo.setDfCash(dfCash);
		jsvo.setJfNCash(jfNCash);
		jsvo.setDfNCash(dfNCash);
	}
	
	
	public DZFDouble putMapAndMnyValue(LinkedHashMap<String, DZFDouble> cashMap,DZFDouble sumdouble,DZFDouble tempmny,String[] slcode,String xmcode,
			TzpzBVO bvo,TzpzBVO[] bvos,String corptype,String currcode,YntCpaccountVO[] cpavos){
		//如果包含22210101的科目则tempmny 的值不一样
		//取当前科目同方向的值
		DZFDouble jfmnyvlaue = bvo.getJfmny() == null? DZFDouble.ZERO_DBL:bvo.getJfmny();
		
		DZFDouble dfmnyvalue = bvo.getDfmny() == null ? DZFDouble.ZERO_DBL:bvo.getDfmny();
		
		DZFDouble sumvalue = DZFDouble.ZERO_DBL;
		
		DZFDouble slvalue = DZFDouble.ZERO_DBL;
		
		DZFBoolean isconsl = DZFBoolean.FALSE;
		
		String vcode = null;
		
		DZFDouble tempres = DZFDouble.ZERO_DBL;
		for(TzpzBVO bvotemp:bvos){
			vcode = bvotemp.getVcode();
			if(StringUtil.isEmpty(vcode)){
				continue;
			}
			if(KmschemaCash.isCashAccount(cpavos,vcode, corptype)){
				continue;
			}
			
			DZFDouble tjfmnyvlaue = bvotemp.getJfmny() == null? DZFDouble.ZERO_DBL:bvotemp.getJfmny();
			
			DZFDouble tdfmnyvalue = bvotemp.getDfmny() == null ? DZFDouble.ZERO_DBL:bvotemp.getDfmny();
			
			if(isStartSl(slcode,bvotemp.getVcode())){
				//同方向
				if(jfmnyvlaue.doubleValue() != 0 && tjfmnyvlaue.doubleValue()!=0){
					isconsl = DZFBoolean.TRUE;
					slvalue = SafeCompute.add(slvalue,  tjfmnyvlaue) ;
				}else if(dfmnyvalue.doubleValue() !=0 && tdfmnyvalue.doubleValue()!=0){
					isconsl = DZFBoolean.TRUE;
					slvalue = SafeCompute.add(slvalue, tdfmnyvalue);
				}
			}else{
				if(jfmnyvlaue.doubleValue() != 0 && tjfmnyvlaue.doubleValue()!=0 ){
//					if(!isconsl.booleanValue()){
//						slvalue =SafeCompute.add(slvalue, bvotemp.getJfmny());   
//					}
					sumvalue = SafeCompute.add(sumvalue, tjfmnyvlaue);
				}else if(dfmnyvalue.doubleValue() !=0 && tdfmnyvalue.doubleValue()!=0){
//					if(!isconsl.booleanValue()){
//						slvalue = SafeCompute.add(slvalue, bvotemp.getDfmny());  
//					}
					sumvalue = SafeCompute.add(sumvalue, tdfmnyvalue);
				}
			}
		}
		
		if(isconsl.booleanValue()){
			if(sumvalue != null && sumvalue.doubleValue() !=0){//如果有非应交增值税下的，或者当前科目是应交增值税，则不分析
				if(isEqualsSl(slcode,currcode)){
					return sumdouble;//直接返回
				}
			}
			tempres = tempmny.add(slvalue.div(sumvalue).multiply(tempmny).add(new DZFDouble(0.5)).setScale(2, DZFDouble.ROUND_HALF_UP));
		}else{
			tempres = tempmny;
		}
		
		cashMap.put(xmcode, cashMap.get(xmcode) == null ? tempres : cashMap.get(xmcode).add(tempres));
		sumdouble = sumdouble.add(tempres);
		
		return sumdouble;
	}
	
	private boolean isEqualsSl(String[] slcodes,String code){
		for(String slcode:slcodes){
			if(code.equals(slcode)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isStartSl(String[] slcodes,String code){
		for(String slcode:slcodes){
			if(code.startsWith(slcode)){
				return true;
			}
		}
		return false;
	}
	
	private XjllVO setDefault (XjllVO vo, TzpzHVO hvo) {
		vo.setPk_tzpz_h(hvo.getPk_tzpz_h());
		vo.setPk_corp(hvo.getPk_corp());
		vo.setPzh(hvo.getPzh());
		vo.setCoperatorid(hvo.getCoperatorid());
		vo.setDoperatedate(hvo.getDoperatedate());
		vo.setDr(0);
		return vo;
	}

//	private boolean isCashAccount(String vcode, Integer accSchema) {
//		boolean isCashAccount = false;
//		if (accSchema == null) {
//			return false;
//		} else if (accSchema.equals(DzfUtil.THIRTEENSCHEMA)) {
//			isCashAccount = vcode.startsWith("1001") || vcode.startsWith("1002") || vcode.startsWith("1012");
//		} else if (accSchema.equals(DzfUtil.SEVENSCHEMA)) {
//			isCashAccount = vcode.startsWith("1001") || vcode.startsWith("1002") || vcode.startsWith("1012") || vcode.startsWith("1101");
//		} else if (accSchema.equals(DzfUtil.POPULARSCHEMA)) {
//			isCashAccount = vcode.startsWith("1001") || vcode.startsWith("1002") || vcode.startsWith("1009");
//		} else if (accSchema.equals(DzfUtil.COMPANYACCOUNTSYSTEM)) {
//			isCashAccount = vcode.startsWith("1001") || vcode.startsWith("1002") || vcode.startsWith("1009");
//		}
//		return isCashAccount;
//	}
	
//	public static Set<String> getCashSubjectCode(String corpType) {
//		Set<String> codeSet = new HashSet<>();
//		codeSet.add("1001");
//		codeSet.add("1002");
//		if ("00000100AA10000000000BMQ".equals(corpType)
//				|| "00000100000000Ig4yfE0005".equals(corpType)) {
//			// 民间非营利组织会计制度 企业会计制度
//			codeSet.add("1009");
//		} else if ("00000100AA10000000000BMF".equals(corpType)) {
//			// 企业会计准则
//			codeSet.add("1012");
//			codeSet.add("1101");
//		} else if ("00000100AA10000000000BMD".equals(corpType)) {
//			// 小企业会计准则
//			codeSet.add("1012");
//		}
//		return codeSet;
//	}
	
	private TzpzBVO[] mergeSameAccount(TzpzBVO[] bvos, String corptype,YntCpaccountVO[] cpavos) {
		Map<String, TzpzBVO> mergedMap = new LinkedHashMap<String, TzpzBVO>();
		for (TzpzBVO tzpzBVO : bvos) {
			if (tzpzBVO.getJfmny() != null
					&& tzpzBVO.getJfmny().doubleValue() != 0) {
				tzpzBVO.setDirect(0);
			} else {
				tzpzBVO.setDirect(1);
			}
			String vcode = tzpzBVO.getVcode();
			String key = tzpzBVO.getVcode();
			if (KmschemaCash.isCashAccount(cpavos,vcode, corptype)) {
				key += tzpzBVO.getDirect();
			}
			if (mergedMap.containsKey(key)) {
				TzpzBVO mergedEntry = mergedMap.get(key);
				if (mergedEntry.getDirect() == 0) {
					if (tzpzBVO.getDirect() == 0) {
						mergedEntry.setJfmny(SafeCompute.add(
								mergedEntry.getJfmny(), tzpzBVO.getJfmny()));
					} else {
						mergedEntry.setJfmny(SafeCompute.sub(
								mergedEntry.getJfmny(), tzpzBVO.getDfmny()));
					}
				} else {
					if (tzpzBVO.getDirect() == 0) {
						mergedEntry.setDfmny(SafeCompute.sub(
								mergedEntry.getDfmny(), tzpzBVO.getJfmny()));
					} else {
						mergedEntry.setDfmny(SafeCompute.add(
								mergedEntry.getDfmny(), tzpzBVO.getDfmny()));
					}
				}
			} else {
				TzpzBVO mergedEntry = new TzpzBVO();
				mergedEntry.setVcode(vcode);
				mergedEntry.setJfmny(tzpzBVO.getJfmny());
				mergedEntry.setDfmny(tzpzBVO.getDfmny());
				mergedEntry.setDirect(tzpzBVO.getDirect());
				mergedMap.put(key, mergedEntry);
			}
		}
		return mergedMap.values().toArray(new TzpzBVO[0]);
	}

	public static boolean checkError(TzpzHVO hvo, List<XjllVO> cashVOs, Set<String> cashSubjs, CorpVO corp,YntCpaccountVO[] cpavos) {
		if (cashSubjs == null) {
			cashSubjs = KmschemaCash.getCashSubjectCode(cpavos,corp.getCorptype());
		}
		boolean isError = false;
		if (hvo.getIsfpxjxm() != null && hvo.getIsfpxjxm().booleanValue()) {
			// 凭证现金科目金额合计
			DZFDouble voucherCashSum = DZFDouble.ZERO_DBL;
			// 现金流量项目金额合计
			DZFDouble cashItemSum = DZFDouble.ZERO_DBL;
			if (cashVOs != null) {
				for (XjllVO cashVO: cashVOs) {
					if (cashVO.getNmny() != null) {
						if (cashVO.getVdirect() != null
								&& cashVO.getVdirect() == 0) {
							cashItemSum = cashItemSum.add(cashVO.getNmny());
						} else {
							cashItemSum = cashItemSum.sub(cashVO.getNmny());
						}
					}
				}
			}
			TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
			for (TzpzBVO bvo: bvos) {
				boolean isCashSubj = cashSubjs.contains(bvo.getVcode());
//				boolean isCashSubj = cashSubjs.contains(DZfcommonTools
//						.getFirstCode(bvo.getVcode(), DZFConstant.ACCOUNTCODERULE));
				if (isCashSubj) {
					if (bvo.getJfmny() == null || bvo.getJfmny().doubleValue() == 0) {
						voucherCashSum = voucherCashSum.sub(bvo.getDfmny());
					} else {
						voucherCashSum = voucherCashSum.add(bvo.getJfmny());
					}
				}
			}
			if (cashItemSum.compareTo(voucherCashSum) != 0) {
				isError = true;
			}
		}
		return isError;
	}
}
