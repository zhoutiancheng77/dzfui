package com.dzf.zxkj.report.service.cwbb.impl;

import java.util.*;
import java.util.Map.Entry;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.ExpendpTionVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IAppReport;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 为手机端报表使用
 * @author zhangj
 *
 */
@Service("appservice")
public class AppReportImpl  implements IAppReport {
	
	private IFsYeReport gl_rep_fsyebserv;//发生额及余额表
	
	private ILrbReport gl_rep_lrbserv;//利润表报表
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjPlatformService iZxkjPlatformService;

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public ExpendpTionVO[] getAppSjMny(String period, String pk_corp, String pk_currency) throws BusinessException {

		String newrule = iZxkjPlatformService.queryAccountRule(pk_corp);
		//period="2015-01";
		DZFDate begindate = new DZFDate(period+"-01");
	    DZFDate enddate = new DZFDate(period+"-"+begindate.getDaysMonth());
	    
	    //日期不在建账日期内，则返回错误
	    String corpsql = "select begindate from bd_corp where nvl(dr,0)=0 and  pk_corp='" + pk_corp + "'";
		DZFDate corpdate = new DZFDate((String) singleObjectBO.executeQuery(corpsql,
				new SQLParameter(), new ColumnProcessor()));
	    if (begindate.getYear() < corpdate.getYear()) {
			throw new BusinessException("建账日期不在查询期间内！");
		}
	    
	    StringBuffer kms = new StringBuffer();
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setPk_corp(pk_corp);
		paramvo.setBegindate1(begindate);
		paramvo.setEnddate(enddate);
		paramvo.setPk_currency(pk_currency);
		paramvo.setIshasjz(DZFBoolean.FALSE);
		
		List<String> accountlist = new ArrayList<String>();
		//税金
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222107", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222108", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222102", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222103", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222104", DZFConstant.ACCOUNTCODERULE, newrule));
		accountlist.add(iZxkjPlatformService.getNewRuleCode("222106", DZFConstant.ACCOUNTCODERULE, newrule));
		DZFBoolean is2007sche = DZFBoolean.TRUE;//是否07科目
		Integer corpschema = iZxkjPlatformService.getAccountSchema(pk_corp);
		//取管理费用和销售费用所有的二级科目
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
			//成本
			accountlist.add("6401");
			accountlist.add("6402");
			//费用
			accountlist.add("6601");
			accountlist.add("6602");
		}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
			is2007sche = DZFBoolean.FALSE;
			//成本:
			accountlist.add("5401");
			accountlist.add("5402");
			//销售费用，管理费用
			accountlist.add("5601");
			accountlist.add("5602");
		}else{
			throw new BusinessException("该制度暂不支持利润表,敬请期待!");
		}
		for(int i=0;i<accountlist.size();i++){
			kms.append(accountlist.get(i)+",");
		}
		//paramvo.setKms(kms.substring(0, kms.length()-1));//报表用，不要去掉
		paramvo.setKmcodelist(accountlist);
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		paramvo.setXsyljfs(DZFBoolean.TRUE);
	
		FseJyeVO[] fsjyvos = gl_rep_fsyebserv.getFsJyeVOs(paramvo, 0);
		
		
		DZFDouble cbmny =  DZFDouble.ZERO_DBL;//成本金额
		DZFDouble sjmny = DZFDouble.ZERO_DBL;//税金金额
		DZFDouble qtmny = DZFDouble.ZERO_DBL;//其他金额
		DZFDouble fymny = DZFDouble.ZERO_DBL;//费用金额1
		DZFDouble fymny2 = DZFDouble.ZERO_DBL;//费用金额2
		
		List<ExpendpTionVO> listres = new ArrayList<ExpendpTionVO>();
		
		ExpendpTionVO tioncb = new ExpendpTionVO();
		tioncb.setName("成本");
		tioncb.setValue("0");
		listres.add(tioncb);
		
		ExpendpTionVO tionsj = new ExpendpTionVO();
		tionsj.setName("税金");
		tionsj.setValue("0");
		listres.add(tionsj);
		
		ExpendpTionVO tionqt = new ExpendpTionVO();
		tionqt.setName("其他");
		tionqt.setValue("0");
		listres.add(tionqt);
		
		ExpendpTionVO tionfy1 = new ExpendpTionVO();
		tionfy1.setName("费用1");
		tionfy1.setValue("0");
		listres.add(tionfy1);
		
		ExpendpTionVO tionfy2 = new ExpendpTionVO();
		tionfy2.setName("费用2");
		tionfy2.setValue("0");
		listres.add(tionfy2);
		
		Map<String,Double> fymap = new HashMap<String,Double>();
		if(fsjyvos !=null && fsjyvos.length >0){
			for(FseJyeVO jyevo:fsjyvos){
				//是否包含成本
				if(isContainCb(jyevo.getKmbm())){
					cbmny= cbmny.add(jyevo.getFsjf()==null?DZFDouble.ZERO_DBL:jyevo.getFsjf());
				}else if(isContainSj(jyevo.getKmbm(),pk_corp)){//是否包含税金
					if(jyevo.getFsdf()!=null && jyevo.getFsdf().doubleValue()!=0){
						sjmny = sjmny.add(jyevo.getFsdf()==null ? DZFDouble.ZERO_DBL:jyevo.getFsdf());
					}else{
						sjmny = sjmny.add(jyevo.getFsjf()==null ? DZFDouble.ZERO_DBL:jyevo.getFsjf().multiply(-1));
					}
				}else {
					//根据层级过滤
					if (jyevo.getAlevel().intValue() >=2 &&  jyevo.getAlevel().intValue() <= 2) {
						String kmbm = jyevo.getKmbm();
						if(kmbm!=null && (kmbm.startsWith("5601") || kmbm.startsWith("5602") ||  kmbm.startsWith("6601") || kmbm.startsWith("6602"))){
							fymap.put(jyevo.getKmbm(), jyevo.getFsjf() == null?0:jyevo.getFsjf().doubleValue());
						}
					}
				}
			}
		
			listres.get(0).setValue(cbmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());
			
			listres.get(1).setValue(sjmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());
			
			
			//根据科目编码+公司获取公司科目名称
			String wherepart = " nvl(dr,0)=0 and  pk_corp='"+pk_corp+"'";
			YntCpaccountVO[] cpavos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, wherepart, new SQLParameter());
			
			Map<String, YntCpaccountVO> cpamap =new HashMap<String, YntCpaccountVO>();
			
			for(YntCpaccountVO cpavo:cpavos){
				cpamap.put(cpavo.getAccountcode(), cpavo);
			}
			
			//需要根据一定的规则合并map
			Map<String, Double> resmap = new HashMap<String, Double>();
			if(is2007sche.booleanValue()){
				resmap = re2007PutMap(fymap,pk_corp);
			}else{
				resmap = re2013PutMap(fymap,pk_corp);
			}
			
			resmap =  sortMap(resmap);//排序map
			Set<String> keyset = resmap.keySet();
			int count=0;
			for(String key:keyset){
				if(count==keyset.size()-2){
					//费用1
					fymny = fymny.add(resmap.get(key)==null?0:resmap.get(key));
					listres.get(3).setName(cpamap.get(key).getAccountname());
					listres.get(3).setValue(fymny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());
				}else if(count==keyset.size()-1){
					//费用2
					fymny2= fymny2.add(resmap.get(key)==null?0:resmap.get(key));
					listres.get(4).setName(cpamap.get(key).getAccountname());
					listres.get(4).setValue(fymny2.setScale(2, DZFDouble.ROUND_HALF_UP).toString());
				}else{
					qtmny = qtmny.add(resmap.get(key)==null?0:resmap.get(key));
				}
				count++;
			}
			
			listres.get(2).setValue(qtmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());
		}
		
		return listres.toArray(new ExpendpTionVO[0]);
	}
	
	private String formatkm(String pk_corp,String km){
		String newrule = iZxkjPlatformService.queryAccountRule(pk_corp);
		String kmnew = iZxkjPlatformService.getNewRuleCode(km, DZFConstant.ACCOUNTCODERULE, newrule);
		return kmnew;
	}
	
	
	/**
	 * map重新赋值
	 * @param fymap
	 */
	private Map<String, Double> re2013PutMap(Map<String, Double> fymap,String pk_corp) {
		Map<String,Double> fymapres = new HashMap<String,Double>();
		String[][] strs = { { formatkm(pk_corp, "560101"), formatkm(pk_corp, "560201") },
				{ formatkm(pk_corp, "560102"), formatkm(pk_corp, "560202") },
				{ formatkm(pk_corp, "560103"), formatkm(pk_corp, "560203") },
				{ formatkm(pk_corp, "560104"), formatkm(pk_corp, "560204") },
				{ formatkm(pk_corp, "560105"), formatkm(pk_corp, "560205") },
				{ formatkm(pk_corp, "560106"), formatkm(pk_corp, "560206") },
				{ formatkm(pk_corp, "560107"), formatkm(pk_corp, "560207") },
				{ formatkm(pk_corp, "560108"), formatkm(pk_corp, "560209") },
				{ formatkm(pk_corp, "560109"), formatkm(pk_corp, "560210") },
				{ formatkm(pk_corp, "560110"), formatkm(pk_corp, "560211") },
				{ formatkm(pk_corp, "560111"), formatkm(pk_corp, "560212") },
				{ formatkm(pk_corp, "560112"), formatkm(pk_corp, "560213") },
				{ formatkm(pk_corp, "560113"), formatkm(pk_corp, "560214") },
				{ formatkm(pk_corp, "560114"), formatkm(pk_corp, "560215") },
				{ formatkm(pk_corp, "560115"), formatkm(pk_corp, "560216") },
				{ formatkm(pk_corp, "560116"), formatkm(pk_corp, "560217") },
				{ formatkm(pk_corp, "560117"), formatkm(pk_corp, "560218") },
				{ formatkm(pk_corp, "560118"), formatkm(pk_corp, "560219") },
				{ formatkm(pk_corp, "560119"), formatkm(pk_corp, "560220") },
				{ formatkm(pk_corp, "560120"), formatkm(pk_corp, "560222") },
				{ formatkm(pk_corp, "560121"), formatkm(pk_corp, "560221"), formatkm(pk_corp, "560208") }, };
		
		for(String[] str:strs){
			Double value = new Double(0);
			for (int i = 0; i < str.length; i++) {
				Double value1 = fymap.get(str[i]) == null ? 0.0 : fymap.get(str[i]);
				value = value.doubleValue() + value1;
			}
			if(value.doubleValue() ==0){
				continue;
			}
			fymapres.put(str[0], value);
		}
		return fymapres;
	}
	
	/**
	 * map重新赋值
	 * @param fymap
	 */
	private Map<String, Double> re2007PutMap(Map<String, Double> fymap,String pk_corp) {
		Map<String,Double> fymapres = new HashMap<String,Double>();
		String[][] strs = { { formatkm(pk_corp, "660101"), formatkm(pk_corp, "660201") },
				{ formatkm(pk_corp, "660102"), formatkm(pk_corp, "660202") },
				{ formatkm(pk_corp, "660103"), formatkm(pk_corp, "660203") },
				{ formatkm(pk_corp, "660104"), formatkm(pk_corp, "660204") },
				{ formatkm(pk_corp, "660105"), formatkm(pk_corp, "660205") },
				{ formatkm(pk_corp, "660106"), formatkm(pk_corp, "660206") },
				{ formatkm(pk_corp, "660107"), formatkm(pk_corp, "660207") },
				{ formatkm(pk_corp, "660108"), formatkm(pk_corp, "660208") },
				{ formatkm(pk_corp, "660109"), formatkm(pk_corp, "660209") },
				{ formatkm(pk_corp, "660110"), formatkm(pk_corp, "660210") },
				{ formatkm(pk_corp, "660111"), formatkm(pk_corp, "660211") },
				{ formatkm(pk_corp, "660112"), formatkm(pk_corp, "660212") },
				{ formatkm(pk_corp, "660113"), formatkm(pk_corp, "660213") },
				{ formatkm(pk_corp, "660114"), formatkm(pk_corp, "660214") },
				{ formatkm(pk_corp, "660115"), formatkm(pk_corp, "660215") },
				{ formatkm(pk_corp, "660116"), formatkm(pk_corp, "660216") },
				{ formatkm(pk_corp, "660117"), formatkm(pk_corp, "660217") },
				{ formatkm(pk_corp, "660118"), formatkm(pk_corp, "660218") },
				{ formatkm(pk_corp, "660119"), formatkm(pk_corp, "660219") },
				{ formatkm(pk_corp, "660120"), formatkm(pk_corp, "660221") },
				{ formatkm(pk_corp, "660121"), formatkm(pk_corp, "660220") }, };
		
		for(String[] str:strs){
			Double value = new Double(0);
			for (int i = 0; i < str.length; i++) {
				Double value1 = fymap.get(str[i]) == null ? 0.0 : fymap.get(str[i]);
				value = value.doubleValue() + value1;
			}
			if(value.doubleValue() ==0){
				continue;
			}
			fymapres.put(str[0], value);
		}
		return fymapres;
	}

	/**
	 * map按照值排序
	 * @param oldMap
	 * @return
	 */
	public static Map sortMap(Map oldMap) {
		ArrayList<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(oldMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Map newMap = new LinkedHashMap();
		for (int i = 0; i < list.size(); i++) {
			newMap.put(list.get(i).getKey(), list.get(i).getValue());
		}
		return newMap;
	}  

	private boolean isContainCb(String kmbm) {
		if(kmbm!=null && (kmbm.equals("6401") || kmbm.equals("6402") ||  kmbm.equals("5401") || kmbm.equals("5402"))){
			return true;
		}
		return false;
	}

	private boolean isContainSj(String kmbm,String pk_corp) {
		if (kmbm != null && (kmbm.equals(formatkm(pk_corp, "222109")) || kmbm.equals(formatkm(pk_corp, "222107"))
				|| kmbm.equals(formatkm(pk_corp, "222108")) || kmbm.equals(formatkm(pk_corp, "222102"))
				|| kmbm.equals(formatkm(pk_corp, "222103")) || kmbm.equals(formatkm(pk_corp, "222104"))
				|| kmbm.equals(formatkm(pk_corp, "222106")))){
			return true;
		}
		return false;
	}

	public IFsYeReport getGl_rep_fsyebserv() {
		return gl_rep_fsyebserv;
	}

	@Autowired
	public void setGl_rep_fsyebserv(IFsYeReport gl_rep_fsyebserv) {
		this.gl_rep_fsyebserv = gl_rep_fsyebserv;
	}

	/**
	 * 取整年的值
	 */
	@Override
	public DZFDouble[] getAppNetProfit(String year, String pk_corp)throws BusinessException {
		
		Map<String, DZFDouble> lrbmap = gl_rep_lrbserv.getYearLRBVOs(year, pk_corp,null);//这里产生的是12个月
		
		Map<String, DZFDouble> resmap =  sortMapByKey(lrbmap);//排序map
		
		DZFDouble[] resmny = new DZFDouble[12];
		Set<String>  keyset = resmap.keySet();
		int count = 0;
		for(String str:keyset){
			resmny[count]= resmap.get(str);
			count++;
		}
		return resmny;
	}
	
	/** 
     * 使用 Map按key进行排序 
     * @param map 
     * @return 
     */  
    public static Map<String, DZFDouble> sortMapByKey(Map<String, DZFDouble> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, DZFDouble> sortMap = new TreeMap<String, DZFDouble>(new Comparator<String>() {
        	    public int compare(String str1, String str2) {  
        	        return str1.compareTo(str2);  
        	    }  
			}
        );  
        sortMap.putAll(map);  
        
        return sortMap;  
    }  
  

	public ILrbReport getGl_rep_lrbserv() {
		return gl_rep_lrbserv;
	}

	@Autowired
	public void setGl_rep_lrbserv(ILrbReport gl_rep_lrbserv) {
		this.gl_rep_lrbserv = gl_rep_lrbserv;
	}

}
