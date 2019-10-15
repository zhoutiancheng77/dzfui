package com.dzf.zxkj.platform.service.report.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbRptSetVo;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.report.IFsYeReport;
import com.dzf.zxkj.platform.service.report.ILrbQuarterlyReport;
import com.dzf.zxkj.platform.service.report.ILrbReport;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 利润表季报
 * 
 * 
 */
@Service("gl_rep_lrbquarterlyserv")
@SuppressWarnings("all")
public class LrbQuarterlyReportImpl implements ILrbQuarterlyReport {

	@Autowired
	private ICpaccountService gl_cpacckmserv ;

	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule ;
	
	@Autowired
	private IFsYeReport gl_rep_fsyebserv;


	@Autowired
	private IYntBoPubUtil yntBoPubUtil = null;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	
	@Autowired
	private ILrbReport gl_rep_lrbserv;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
	
	/**
	 * 利润表季报取数
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO vo) throws DZFWarpException {
		int year = new DZFDate(vo.getQjq() + "-01").getYear();
		int lastyear = year -1 ;
		String pk_corp = vo.getPk_corp();
		DZFBoolean ishasjz = vo.getIshasjz();
		vo.setPk_corp(pk_corp);
		vo.setRptsource("lrb");
		CorpVO corpvo=corpService.queryByPk(vo.getPk_corp());
		DZFDate corpdate =corpvo.getBegindate();
		Object[] lastyearobj = null;
		if (lastyear < corpdate.getYear()) {
		}else{
			lastyearobj = gl_rep_fsyebserv.getYearFsJyeVOsLrbquarter(lastyear+"",pk_corp,corpdate,ishasjz,vo.getRptsource());
		}
		Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOsLrbquarter(year+"",pk_corp,corpdate,ishasjz,vo.getRptsource());
		LrbquarterlyVO[] lrbquartervos = null;
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		if(monthmap==null||monthmap.size()==0){
			return null;
		}
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		if(mp==null||mp.size()==0){
			return null;
		}
		if(lastyearobj!=null&&lastyearobj.length>0){
			Map<String,List<FseJyeVO>> lastyearmonthmap = (Map<String, List<FseJyeVO>>) lastyearobj[0];
			Map<String, YntCpaccountVO> lastyearmp = (Map<String, YntCpaccountVO>) lastyearobj[1];
			lastyearmonthmap.putAll(monthmap);
			lastyearmp.putAll(mp);
			monthmap = lastyearmonthmap;
			mp = lastyearmp;
		}
		mp = convert(mp);
		//按月排序后再遍历
		List<Map.Entry<String, List<FseJyeVO>>> sortedMonthmap = new ArrayList<Map.Entry<String, List<FseJyeVO>>>(monthmap.entrySet());
		Collections.sort(sortedMonthmap, new Comparator<Map.Entry<String, List<FseJyeVO>>>() {
		    public int compare(Map.Entry<String, List<FseJyeVO>> o1, Map.Entry<String, List<FseJyeVO>> o2) {      
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		Map.Entry<String, List<FseJyeVO>> entry = null;
		String key = null;
		Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
		for (int i = 0; i < sortedMonthmap.size(); i++) {
			entry= sortedMonthmap.get(i);
			key = (String) entry.getKey();
			Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
			if(key.compareTo(vo.getQjz())<=0){
				List<FseJyeVO> val = (List<FseJyeVO>) entry.getValue();
				int len = val == null ? 0 : val.size();
				for (int j = 0; j < len; j++) {
					if (val.get(j) != null) {
						map.put(val.get(j).getKmbm(), val.get(j));
					}
				}
				if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
					// 2007会计准则
					lrbquartervos =  getLRB2007VOs(lrbquartervos,map, mp, key,year, pk_corp );
				} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
					// 2013会计准则
					lrbquartervos =  getLRB2013VOs(lrbquartervos,map, mp, key,year,pk_corp);
				} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
					lrbquartervos =  getCompanyAccountVos(lrbquartervos,map, mp, key,year,pk_corp);
				}else{
					throw new BusinessException("该制度暂不支持利润表,敬请期待!");
				}
			}else{
				if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
					// 2007会计准则
					lrbquartervos =  getLRB2007VOs(lrbquartervos,map, mp, key,year, pk_corp );
				} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
					// 2013会计准则()
					lrbquartervos =  getLRB2013VOs(lrbquartervos,map, mp, key,year,pk_corp);
				} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
					//企业会计制度
					lrbquartervos = getCompanyAccountVos(lrbquartervos,map, mp, key,year,pk_corp);
				}else{
					throw new BusinessException("该制度暂不支持利润表,敬请期待!");
				}
			}
		}
		return lrbquartervos;

	}


	private LrbquarterlyVO[] getCompanyAccountVos(LrbquarterlyVO[] lrbquartervos,Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String rq,int year,String pk_corp) {
		
		SQLParameter sp = new SQLParameter();
		sp.addParam("00000100000000Ig4yfE0005");
		LrbRptSetVo[] setvos = (LrbRptSetVo[]) singleObjectBO.queryByCondition(LrbRptSetVo.class,
				"nvl(dr,0)=0 and pk_trade_accountschema = ? ", sp);

		if (setvos == null || setvos.length == 0) {
			throw new BusinessException("该制度暂不支持,敬请期待");
		}
		
		if(lrbquartervos == null){
			lrbquartervos = new LrbquarterlyVO[setvos.length];
			for(int i =0;i<setvos.length;i++){
				lrbquartervos[i] = new LrbquarterlyVO();
				if(!StringUtil.isEmpty(setvos[i].getXm())){
					lrbquartervos[i].setXm(setvos[i].getXm().replace("  ", "　"));
				}
				lrbquartervos[i].setHs(setvos[i].getHc());
				lrbquartervos[i].setHs_id(setvos[i].getHc_id());
				lrbquartervos[i].setLevel(setvos[i].getIlevel());
			}
		}
		
		for(int i =0;i<setvos.length;i++){
			String[] kms = getKms(setvos[i].getKm());
			if (kms != null && kms.length > 0) {
				if (kms[0].indexOf("=") >= 0) {
					totalLrb(lrbquartervos, lrbquartervos[i] , kms,year,rq);
				} else {
					lrbquartervos[i] = getLRBVOCal(map, mp, rq,year, lrbquartervos[i], kms);
				}
			}
		}

		return lrbquartervos;
	}
	
	private void totalLrb(LrbquarterlyVO[] list, LrbquarterlyVO lrbvo, String[] kms,int year,String rq) {

		DZFDouble byje =DZFDouble.ZERO_DBL;

		DZFDouble bnljje = DZFDouble.ZERO_DBL;
		
		DZFDouble qnljje = DZFDouble.ZERO_DBL;
		
		DZFDouble bnlj = DZFDouble.ZERO_DBL;

		String tvalue = "";
		for (int i = 0; i < kms.length; i++) {
			if (i == 0) {
				tvalue = kms[i].substring(kms[i].indexOf("=") + 1);
			} else {
				tvalue = kms[i];
			}
			Integer multify = 1;
			if(tvalue.startsWith("-")){
				multify = -1;
				tvalue = tvalue.substring(1);
			}
			for (LrbquarterlyVO vo : list) {
				if (tvalue.equals(vo.getHs())) {
					byje = SafeCompute.add(byje, VoUtils.getDZFDouble(vo.getByje()).multiply(multify));
					bnljje = SafeCompute.add(bnljje,VoUtils.getDZFDouble(vo.getBnljje()).multiply(multify)  );
					bnlj = SafeCompute.add(bnlj, VoUtils.getDZFDouble(vo.getBnlj()).multiply(multify) );
					qnljje = SafeCompute.add(qnljje, VoUtils.getDZFDouble(vo.getQnljje()).multiply(multify) );
				}
			}
		}
		int rqyear =Integer.parseInt(rq.substring(0,4));
		if(rqyear == year-1){//去年同期数
			lrbvo.setQnljje(qnljje);
		}else{
			lrbvo.setByje(byje);
			lrbvo.setBnljje(bnljje);
			lrbvo.setBnlj(bnlj);
		}
		
		lrbvo = setQuarterMny(rq,year,lrbvo,lrbvo.getBnlj());

	}
	
	private String[] getKms(String km) {
		String regex = ",";
		if(StringUtil.isEmpty(km)){
			return null;
		}
		String[] strs = km.split(regex);
		//去掉借贷括号
		List<String> list = new ArrayList<String>();
		String value = "";
		for(String str:strs){
			value = str.replace("(借)", "");
			value = value.replace("(贷)", "");
			list.add(value);
		}
		return list.toArray(new String[0]);
	}

	private LrbquarterlyVO getLRBVO(Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String rq,int year, LrbquarterlyVO vo, String... kms) {
		DZFDouble ufd = null;
		int direction = 0;
		int len = kms == null ? 0 : kms.length;
		YntCpaccountVO km = null;
		List<FseJyeVO> ls = null;
		DZFDouble sumdouble = DZFDouble.ZERO_DBL;
		for (int i = 0; i < len; i++) {
			km = mp.get(kms[i]);
			if (km == null)
				continue;
			direction = km.getDirection();
			ls = getData(map, kms[i]);
			for (FseJyeVO fvo : ls) {
				int rqyear =Integer.parseInt(rq.substring(0,4));
				if (direction == 0) {
					if(rqyear==year-1){
						ufd = getDZFDouble(i ==0 ? DZFDouble.ZERO_DBL:vo.getQnljje());
						//去年累计数(发生的累计，不包含期初,计算季度数使用)
						vo.setQnljje(ufd.add(SafeCompute.sub(fvo.getJftotal(),fvo.getDftotal())));//借-贷
					}
					if(rqyear==year){
						ufd = getDZFDouble(vo.getBnljje());
						//本年累计数(发生的累计，不包含期初,计算季度数使用)
						vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getEndfsjf(),fvo.getEndfsdf())));//借-贷
						sumdouble = SafeCompute.add(sumdouble,SafeCompute.sub(fvo.getJftotal(), fvo.getDftotal()));
					}
					vo = setQuarterMny(rq,year,vo,sumdouble);
				} else {
					if(rqyear==year-1){
						ufd = getDZFDouble(i ==0 ? DZFDouble.ZERO_DBL:vo.getQnljje());
						vo.setQnljje(ufd.add(SafeCompute.sub(fvo.getDftotal(),fvo.getJftotal())));//贷-借
					}
					if(rqyear==year){
						ufd = getDZFDouble(vo.getBnljje());
						vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getEndfsdf(),fvo.getEndfsjf())));
						sumdouble =SafeCompute.add(sumdouble,SafeCompute.sub(fvo.getDftotal(), fvo.getJftotal()));
					}
					vo = setQuarterMny(rq,year,vo,sumdouble);
				}
			}
		}
		return vo;
	}
	
	/**
	 * 按照公式计算，以后最好用这个不要用getLRBVO
	 * @param map
	 * @param mp
	 * @param rq
	 * @param year
	 * @param vo
	 * @param kms
	 * @return
	 */
	private LrbquarterlyVO getLRBVOCal(Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String rq,int year, LrbquarterlyVO vo, String... kms) {

		DZFDouble ufd = null;
		int direction = 0;
		int len = kms == null ? 0 : kms.length;
		YntCpaccountVO km = null;
		List<FseJyeVO> ls = null;
		DZFDouble sumdouble = DZFDouble.ZERO_DBL;
		for (int i = 0; i < len; i++) {
			String tvalue = kms[i];
			Integer multify = 1;
			if(tvalue.startsWith("-")){
				tvalue = tvalue.substring(1);
				multify = -1;
			}
			km = mp.get(tvalue);
			if (km == null)
				continue;
			direction = km.getDirection();
			ls = getData(map, tvalue);
			for (FseJyeVO fvo : ls) {
				int rqyear =Integer.parseInt(rq.substring(0,4));
				if (direction == 0) {
					if(rqyear==year-1){
						ufd = getDZFDouble(i == 0 ? DZFDouble.ZERO_DBL:vo.getQnljje());//每次重新计算
						vo.setQnljje(ufd.add(SafeCompute.sub(fvo.getJftotal(),fvo.getDftotal()).multiply(multify)));//借-贷
					}
					if(rqyear==year){
						ufd = getDZFDouble(vo.getBnljje());
						vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getEndfsjf(),fvo.getEndfsdf()).multiply(multify)));//借-贷
						sumdouble = SafeCompute.add(sumdouble,SafeCompute.sub(fvo.getJftotal(), fvo.getDftotal()).multiply(multify));
					}
					vo = setQuarterMny(rq,year,vo,sumdouble);
				} else {
					if(rqyear==year-1){
						ufd = getDZFDouble(i == 0 ? DZFDouble.ZERO_DBL: vo.getQnljje());
						vo.setQnljje(ufd.add(SafeCompute.sub(fvo.getDftotal(),fvo.getJftotal()).multiply(multify)));//贷-借
					}
					if(rqyear==year){
						ufd = getDZFDouble(vo.getBnljje());
						vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getEndfsdf(),fvo.getEndfsjf()).multiply(multify)));
						sumdouble =SafeCompute.add(sumdouble,SafeCompute.sub(fvo.getDftotal(), fvo.getJftotal()).multiply(multify));
					}
					vo = setQuarterMny(rq,year,vo,sumdouble);
				}
			}
		}
		return vo;
	}

	private Map<String, YntCpaccountVO> convert(Map<String, YntCpaccountVO> mp) {
		Map<String, YntCpaccountVO> mp1 = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO b : mp.values()) {
			mp1.put(b.getAccountcode(), b);
		}
		return mp1;
	}

	private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if (fsejyevo.getKmbm().equals(km)) {
				list.add(fsejyevo);
			}
		}
		return list;
	}

	private DZFDouble getDZFDouble(Object ufd) {
		return ufd == null ? DZFDouble.ZERO_DBL : (DZFDouble)ufd;
	}

	/**
	 * 2007会计准则的利润表
	 * @param lrbquartervos 
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbquarterlyVO[] getLRB2007VOs(LrbquarterlyVO[] lrbquartervos, Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String qjz,int year,String pk_corp )
			throws DZFWarpException {
		//从利润表取表项
		LrbVO[] lrbvos = gl_rep_lrbserv.getLrbVos(new QueryParamVO(), pk_corp, new HashMap<String,YntCpaccountVO>(), null, "");
		lrbquartervos =	createLrbQueryVos(lrbquartervos,lrbvos,map,mp,qjz,year,pk_corp) ;
		return lrbquartervos;
	}

	private LrbquarterlyVO getLrbQuarterlyVoByHs(LrbquarterlyVO[] vos,String hs){
		if(vos== null || vos.length ==0){
			return null;
		}
		if(StringUtil.isEmpty(hs)){
			return null;
		}
		for(LrbquarterlyVO vo:vos){
			if(hs.equals(vo.getHs())){
				return vo;
			}
		}
		return null;
	}
	
	private LrbquarterlyVO[] createLrbQueryVos(LrbquarterlyVO[] lrbjdvos,LrbVO[] lrbvos, Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String qjz, int year, String pk_corp) {
		if (lrbvos == null || lrbvos.length <= 0) {
			return null;
		}
		if(lrbjdvos == null){
			lrbjdvos = new LrbquarterlyVO[lrbvos.length];
		}
		for (int i = 0; i < lrbvos.length; i++) {
			if(lrbjdvos[i] == null){
				lrbjdvos[i] = new LrbquarterlyVO();
				lrbjdvos[i].setXm(lrbvos[i].getXm());
				lrbjdvos[i].setHs(lrbvos[i].getHs());
				lrbjdvos[i].setHs_id(lrbvos[i].getHs_id());
				lrbjdvos[i].setLevel(lrbvos[i].getLevel());
			}
			if (!StringUtil.isEmpty(lrbvos[i].getVconkms())) {
				lrbjdvos[i] = getLRBVO(map, mp, qjz, year, lrbjdvos[i], lrbvos[i].getVconkms().split(","));
			}else if(!StringUtil.isEmpty(lrbvos[i].getFormula())){
				int rqyear =Integer.parseInt(qjz.substring(0,4));
				if(rqyear==year-1){
					lrbjdvos[i].setQnljje(getValueFromFormula(lrbjdvos,lrbvos[i].getFormula(),"qnljje"));
				}
				if(rqyear==year){
					lrbjdvos[i].setByje(getValueFromFormula(lrbjdvos,lrbvos[i].getFormula(),"byje"));
					lrbjdvos[i].setBnljje(getValueFromFormula(lrbjdvos,lrbvos[i].getFormula(),"bnljje"));
					lrbjdvos[i].setBnlj(getValueFromFormula(lrbjdvos,lrbvos[i].getFormula(),"bnlj"));
				}
				lrbjdvos[i] = setQuarterMny(qjz,year,lrbjdvos[i],lrbjdvos[i].getBnlj());
			}
		}
		return lrbjdvos;
	}


	private DZFDouble getValueFromFormula(LrbquarterlyVO[] jdvos,String formula, String columnkey) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		if(StringUtil.isEmpty(formula) || StringUtil.isEmpty(columnkey)){
			return res;
		}
		String[] formulas = formula.split(",");
		if(formulas == null || formulas.length ==0){
			return res;
		}
		for(String str:formulas){
			for(LrbquarterlyVO jdvo:jdvos){
				if(jdvo == null || StringUtil.isEmpty(jdvo.getHs_id())){
					continue;
				}
				if (str.indexOf(jdvo.getHs_id()) >= 0) {// 包含的情况
					DZFDouble tmpvalue = getDZFDouble(jdvo.getAttributeValue(columnkey));
					if (str.startsWith("-")) {
						tmpvalue = tmpvalue.multiply(-1);
					}
					res = SafeCompute.add(res, tmpvalue);
				}
			}
		}
		return res;
	}


	/**
	 * 2013会计准则的利润表
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbquarterlyVO[] getLRB2013VOs(LrbquarterlyVO[] lrbquartervos,Map<String, FseJyeVO> map,
			Map<String, YntCpaccountVO> mp, String rq,int year,String pk_corp)
			throws DZFWarpException {
		LrbVO[] lrbvos = gl_rep_lrbserv.getLrbVos(new QueryParamVO(), pk_corp, new HashMap<String,YntCpaccountVO>(), null, "");
		lrbquartervos =	createLrbQueryVos(lrbquartervos,lrbvos,map,mp,rq,year,pk_corp) ;
		return lrbquartervos;
	}
	/**
	 * 给vo设置四个季度的金额
	 * @param rq
	 * @param vo
	 * @return
	 */
	private LrbquarterlyVO setQuarterMny(String rq,int year, LrbquarterlyVO vo,DZFDouble sumnvalue){
		//上年
		if(rq.compareTo(((year-1)+"-03"))<=0){
			vo.setLastquarterFirst(getDZFDouble(vo.getQnljje()));
		}
		else if(rq.compareTo(((year-1)+"-06"))<=0){
			vo.setLastquarterSecond(getDZFDouble(vo.getQnljje()));
		}
		else if(rq.compareTo(((year-1)+"-09"))<=0){
			vo.setLastquarterThird(getDZFDouble(vo.getQnljje()));
		}
		else if(rq.compareTo(((year-1)+"-12"))<=0){
			vo.setLastquarterFourth(getDZFDouble(vo.getQnljje()));
		}
		
		
		//今年
		else if(rq.compareTo(((year)+"-03"))<=0){
			//本年累计(包含期初的本年累计)
			vo.setBnlj(getDZFDouble(sumnvalue!=null?sumnvalue: vo.getBnljje()));
			vo.setQuarterFirst(getDZFDouble(vo.getBnljje()));
		}
		else if(rq.compareTo(((year)+"-06"))<=0){
			//本年累计(包含期初的本年累计)
			vo.setBnlj(getDZFDouble(sumnvalue!=null?sumnvalue:vo.getBnljje()));
			vo.setQuarterSecond(getDZFDouble(vo.getBnljje()).sub(getDZFDouble(vo.getQuarterFirst())));
		}
		else if(rq.compareTo(((year)+"-09"))<=0){
			//本年累计(包含期初的本年累计)
			vo.setBnlj(getDZFDouble(sumnvalue!=null?sumnvalue:vo.getBnljje()));
			vo.setQuarterThird(getDZFDouble(vo.getBnljje()).sub(getDZFDouble(vo.getQuarterFirst())).sub(getDZFDouble(vo.getQuarterSecond())));
		}
		else if(rq.compareTo(((year)+"-12"))<=0){
			//本年累计(包含期初的本年累计)
			vo.setBnlj(getDZFDouble(sumnvalue!=null?sumnvalue:vo.getBnljje()));
			vo.setQuarterFourth(getDZFDouble(vo.getBnljje()).sub(getDZFDouble(vo.getQuarterFirst())).sub(getDZFDouble(vo.getQuarterSecond())).sub(getDZFDouble(vo.getQuarterThird())));
		}
		return vo;
	}
	/**
	 * 按照年取每年的利润表
	 */
	@Override
	public Map<String,DZFDouble> getYearLRBquarterlyVOs(String year,String pk_corp) throws DZFWarpException {
		Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year,pk_corp,null,"lrb");
		Map<String, DZFDouble> mapyearmny= new HashMap<String, DZFDouble>();
		
		//为了防止空值，每个月都有值
		mapyearmny.put(year+"-01", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-02", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-03", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-04", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-05", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-06", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-07", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-08", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-09", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-10", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-11", DZFDouble.ZERO_DBL);
		mapyearmny.put(year+"-12", DZFDouble.ZERO_DBL);
		
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		
		Set<String> yearset = monthmap.keySet();
		for(String str: yearset){
			List<FseJyeVO> listfs = monthmap.get(str);
			DZFDate datevalue = new DZFDate(str+"-01");
			str = str+"-"+datevalue.getDaysMonth();
			FseJyeVO[] fvos = (FseJyeVO[]) listfs.toArray(new FseJyeVO[0]);
			mp = convert(mp);
			Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
			int len = fvos == null ? 0 : fvos.length;
			for (int i = 0; i < len; i++) {
				if (fvos[i] != null) {
					map.put(fvos[i].getKmbm(), fvos[i]);
				}
			}

			LrbquarterlyVO[] lrbvos = null;
			Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				// 2007会计准则
				lrbvos =  getLRB2007VOs(lrbvos,map, mp, str,-1,pk_corp );
				mapyearmny.put(str.substring(0, 7), lrbvos[16].getByje()==null?DZFDouble.ZERO_DBL:lrbvos[16].getByje());
			} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
				// 2013会计准则
				lrbvos =  getLRB2013VOs(lrbvos,map, mp, str,-1,pk_corp);
				mapyearmny.put(str.substring(0, 7), lrbvos[31].getByje()==null?DZFDouble.ZERO_DBL:lrbvos[31].getByje());
			} else{
				throw new BusinessException("该制度暂不支持利润表,敬请期待!");
			}
		}
		
		return mapyearmny;
	}

	@Override
	public Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(QueryParamVO vo,Object[] objs) throws DZFWarpException {
		
		if(objs == null || objs.length ==0){
			throw new BusinessException("明细账数据为空!");
		}
		DZFDate period_beg = vo.getBegindate1();
		DZFDate period_end = vo.getEnddate();
		vo.setRptsource("lrb");
		List<String> periods=  ReportUtil.getPeriods(period_beg, period_end);
		Map<String, LrbquarterlyVO[]> lrbjbmap = new LinkedHashMap<String, LrbquarterlyVO[]>();
		String month = "";
		String jd = "";
		String yearstr = "";
		for(String str:periods){
		    month = str.substring(5, 7);
		    yearstr = str.substring(0, 4);
			if("03".equals(month) || "06".equals(month) || "09".equals(month) || "12".equals(month)){
				vo.setBegindate1(DateUtils.getPeriodStartDate(str.substring(0, 4)+"-01"));
				vo.setEnddate(DateUtils.getPeriodEndDate(str));
				vo.setQjq(str);
				vo.setQjz(str);
				int year = new DZFDate(vo.getQjq() + "-01").getYear();
				int lastyear = year -1 ;
				String pk_corp = vo.getPk_corp();
				vo.setPk_corp(pk_corp);
				
				CorpVO corpvo=corpService.queryByPk(vo.getPk_corp());
				DZFDate corpdate =corpvo.getBegindate();
				LrbquarterlyVO[] lrbquartervos = null;
				DZFDate startdate = vo.getBegindate1();
			    DZFDate enddate = vo.getEnddate();
				if (lastyear >= corpdate.getYear()) {
					startdate = new DZFDate(lastyear+"-01-01");
				}
				Object[]  fsobjs = gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(startdate, enddate, pk_corp, objs,"lrb",vo.getIshasjz());
				Map<String,List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) fsobjs[0];
				Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) fsobjs[1];
				
				mp = convert(mp);
				//按月排序后再遍历
				List<Map.Entry<String, List<FseJyeVO>>> sortedMonthmap = new ArrayList<Map.Entry<String, List<FseJyeVO>>>(monthmap.entrySet());
				Collections.sort(sortedMonthmap, new Comparator<Map.Entry<String, List<FseJyeVO>>>() {   
				    public int compare(Map.Entry<String, List<FseJyeVO>> o1, Map.Entry<String, List<FseJyeVO>> o2) {      
				        return (o1.getKey()).toString().compareTo(o2.getKey());
				    }
				}); 
				Map.Entry<String, List<FseJyeVO>> entry = null;
				String key = null;
				Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
				for (int i = 0; i < sortedMonthmap.size(); i++) {
					entry= sortedMonthmap.get(i);
					key = (String) entry.getKey();
					Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
					if(key.compareTo(vo.getQjz())<=0){//存在数据的时候
						List<FseJyeVO> val = (List<FseJyeVO>) entry.getValue();
						int len = val == null ? 0 : val.size();
						for (int j = 0; j < len; j++) {
							if (val.get(j) != null) {
								map.put(val.get(j).getKmbm(), val.get(j));
							}
						}
						if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
							// 2007会计准则
							lrbquartervos =  getLRB2007VOs(lrbquartervos,map, mp, key,year, pk_corp );
						} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
							// 2013会计准则
							lrbquartervos =  getLRB2013VOs(lrbquartervos,map, mp, key,year,pk_corp);
						}  else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
							lrbquartervos =  getCompanyAccountVos(lrbquartervos,map, mp, key,year,pk_corp);
						}else{
							throw new BusinessException("该制度暂不支持利润表,敬请期待!");
						}
					}else{
						//没发生的时候
						if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
							// 2007会计准则
							lrbquartervos =  getLRB2007VOs(lrbquartervos,map, mp, key,year, pk_corp );
						} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
							// 2013会计准则
							lrbquartervos =  getLRB2013VOs(lrbquartervos,map, mp, key,year,pk_corp);
						} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
							lrbquartervos =  getCompanyAccountVos(lrbquartervos,map, mp, key,year,pk_corp);
						}else{
							throw new BusinessException("该制度暂不支持利润表,敬请期待!");
						}
					}
				}
				if ("03".equals(month)) {
					jd = yearstr + "第一季度";
				} else if ("06".equals(month)) {
					jd = yearstr + "第二季度";
				} else if ("09".equals(month)) {
					jd = yearstr + "第三季度";
				} else if ("12".equals(month)) {
					jd = yearstr + "第四季度";
				}
				
				for(LrbquarterlyVO lrbjdvo:lrbquartervos){
					if("03".equals(month)){
						lrbjdvo.setSntqs(lrbjdvo.getLastquarterFirst());
					} else if ("06".equals(month)) {
						lrbjdvo.setSntqs(lrbjdvo.getLastquarterSecond());
					} else if ("09".equals(month)) {
						lrbjdvo.setSntqs(lrbjdvo.getLastquarterThird());
					} else if ("12".equals(month)) {
						lrbjdvo.setSntqs(lrbjdvo.getLastquarterFourth());
					}
				}
				lrbjbmap.put(jd, lrbquartervos);
			}
		}
		return lrbjbmap;
	}

}
