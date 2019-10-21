package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.base.utils.CalculationUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.QyBdVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IQyBdService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 权益变动表
 * @author zhangj
 *
 */
@Service("qybdser")
@SuppressWarnings("all")
public class QyBdServiceImpl implements IQyBdService {

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;
	
	@Autowired
	private IFsYeReport gl_rep_fsyebserv;
	@Reference(version = "2.0.0")
	private IZxkjPlatformService zxkjPlatformService;
	
	private String[][] getDefaultstr (){
		String[][] strs = new String[][]{
			{"一、股金","1",""},
			{"（一）年初余额","2","ncye(3001)"},
			{"（二）本年增加数","3","qmye(3001)-ncye(3001)"},
			{"　　　其中：公积金转入","4","bndffs(3001,jfkm_3201)"},
			{"　　　　　　盈余分配转入","5","bndffs(3001,jfkm_3202)"},
			{"（三）本年减少数","6","bnjffs(3001)"},
			{"（四）年末余额","7","qmye(3001)"},
			
			
			{"二、专项基金","8",""},
			{"（一）年初余额","9","ncye(3101)"},
			{"（二）本年增加数","10","qmye(3101)-ncye(3101)"},
			{"　　　其中：国家财政直接补助","11","bndffs(3101,jfkm_2305)"},
			{"　　　　　　接受捐赠转入","12","bndffs(3101)-hc(11)"},
			{"（三）年末余额","13","qmye(3101)"},
			
			
			{"三、资本公积","14",""},
			{"（一）年初余额","15","ncye(3201)"},
			{"（二）本年增加数","16","qmye(3201)-ncye(3201)"},
			{"　　　其中：股金溢价","17","bndffs(3201,jfkm_1001&jfkm_1002&jfkm_1103,dfkm_3001)"},
			{"　　　　　　 资产评估增值","18","bndffs(3201,jfkm_1401&jfkm_1402&jfkm_1501&jfkm_1503&jfkm_1601,undfkm_3001)"},//比较麻烦
			{"（三）本年减少数","19","bnjffs(3201)"},
			{"　　　其中：转增股金","20","bnjffs(3201,dfkm_3001)"},
			{"　　　　　　转入损益","21","bnjffs(3201,dfkm_3302)"},
			{"（四）年末余额","22","qmye(3201)"},
			
			
			{"四、盈余公积","23",""},
			{"（一）年初余额","24","ncye(3202)"},
			{"（二）本年增加数","25","qmye(3202)-ncye(3202)"},
			{"　　　其中：从盈余中提取","26","bndffs(3202,jfkm_330202)"},
			{"（三）本年减少数","27","bnjffs(3202)"},
			{"　　　其中：转增股金","28","bnjffs(3202,dfkm_3001)"},
			{"　　　　　　转入损益","29","bnjffs(3202,dfkm_3302)"},
			{"五、未分配盈余","30",""},
			{"（一）年初未分配盈余","31","ncye(3302)"},
			{"（二）本年盈余（净亏损用“-”号填列）","32","bndffs(5001)+bndffs(5101)-bnjffs(5201)-bnjffs(5202)+bndffs(5002)-bnjffs(5209)"},
			{"（三）其他转入","33","bndffs(330201)"},
			{"（四）本年盈余分配","34","bnjffs(330202)+bnjffs(330203)+bnjffs(330204)"},
			
			
			{"（五）年末未分配盈余（未弥补亏损用“-”号填列）","35","hc(31)+hc(32)+hc(33)-hc(34)"},
		};
		
		return strs;
	}
	private List<QyBdVO> getDefaultList() {
		List<QyBdVO> list = new ArrayList<QyBdVO>();

		for (String[] str : getDefaultstr()) {
			QyBdVO vo = new QyBdVO();
			vo.setXm(str[0]);
			vo.setHc(str[1]);
			vo.setFormula(str[2]);
			list.add(vo);
		}

		return list;
	}
	
	@Override
	public List<QyBdVO> queryList(QueryParamVO paramvo) throws DZFWarpException {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(paramvo.getPk_corp());
		String queryAccountRule = zxkjPlatformService.queryAccountRule(paramvo.getPk_corp());
		String regex = "(ncye|qmye|bndffs|bnjffs|hc)\\([^\\(\\)]*?\\)";
		Pattern p = Pattern.compile(regex);
		Matcher m = null;
		// 查询科目期末结账数据
		List<QyBdVO> list = getDefaultList();
		DZFDate begindate = paramvo.getBegindate1();
		//赋值金额
		putJe(paramvo, queryAccountRule, p, list,"bn_je",cpvo);
		//赋值去年金额
		String year = begindate.getYear()-1+"";
		String month = begindate.getMonth()+"";
		DZFDate sn_date =new DZFDate(year+"-"+month+"-01");
		if(DateUtils.getPeriod(sn_date).compareTo(DateUtils.getPeriod(cpvo.getBegindate()))>=0){
			paramvo.setBegindate1(sn_date);
			putJe(paramvo, queryAccountRule, p, list,"sq_je",cpvo);
		}
		return list;
	}
	private void putJe(QueryParamVO paramvo, String queryAccountRule, Pattern p, List<QyBdVO> list,String column,CorpVO cpvo) {
		// 查询科目明细账数据
		if(DateUtils.getPeriod(paramvo.getBegindate1()).compareTo(DateUtils.getPeriod(cpvo.getBegindate()))<0){
			paramvo.setQjq(cpvo.getBegindate().getYear()+"-01");
		}else{
			paramvo.setQjq(paramvo.getBegindate1().getYear()+"-01");
		}
		paramvo.setQjz(DateUtils.getPeriod(paramvo.getBegindate1()));
		paramvo.setEnddate(paramvo.getBegindate1());
		paramvo.setBegindate1(DateUtils.getPeriodStartDate(paramvo.getQjq()));
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		Object[] kmmx_objs = gl_rep_kmmxjserv.getKMMXZVOs1(paramvo, false);
		FseJyeVO[] fsvos =  gl_rep_fsyebserv.getFsJyeVOs(paramvo, kmmx_objs);
		Map<String, DZFDouble> keymap =  new LinkedHashMap<String,DZFDouble>();
		if(kmmx_objs!=null && kmmx_objs.length>0){
			List[] lists = (List[]) kmmx_objs[0];
			int len = lists == null ? 0 : lists.length;
			KmMxZVO[] kvos = (KmMxZVO[]) ((len > 0) ? lists[0].toArray(new KmMxZVO[0]) : null);
			//当前凭证包含的科目
			Map<String, Set<String>> tzpzmap = new HashMap<String, Set<String>>();
			putTzpzMap(tzpzmap, kvos);
			//获取keymap
			getKeyMap(queryAccountRule, list, p, keymap);
			//keymap赋值
			putKeyMapValue(kvos,fsvos, keymap,tzpzmap);
			//最后合计hc 因为有可能hc 还没有值
			putKeyMapValue_hc(list, p, keymap);
			//keymap替换
			for(QyBdVO bgvo:list){
				DZFDouble value = getCalFormulaRes(p, keymap, bgvo);
				bgvo.setAttributeValue(column, value);
			}
		}
	}
	private void putKeyMapValue_hc(List<QyBdVO> list, Pattern p, Map<String, DZFDouble> keymap) {
		Matcher m;
		for (Entry<String, DZFDouble> entry : keymap.entrySet()) {
			String key = entry.getKey();
			DZFDouble value = entry.getValue();
			if (!StringUtil.isEmpty(key)) {
				if(key.indexOf("hc")>=0){
					for(QyBdVO bgvo:list){
						if(key.equals("hc("+bgvo.getHc()+")")){
							value = getCalFormulaRes(p, keymap, bgvo);
						}
					}
					entry.setValue(value);
				}
			}
		}
	}
	/**
	 * 获取计算结果
	 * @param p
	 * @param keymap
	 * @param bgvo
	 * @return
	 */
	private DZFDouble getCalFormulaRes(Pattern p, Map<String, DZFDouble> keymap, QyBdVO bgvo) {
		Matcher m;
		DZFDouble value;
		String formula = bgvo.getFormula();
		m = p.matcher(formula);
		while(m.find()){
			DZFDouble tempvalue = keymap.get(m.group(0));
			if(tempvalue.doubleValue()<0){
				formula = formula.replace(m.group(0), "("+tempvalue.abs()+"*-1)");
			}else{
				formula = formula.replace(m.group(0), ""+keymap.get(m.group(0)));
			}
		}
		Object res = CalculationUtil.CalculationFormula(formula);
		value = new DZFDouble(res ==null?"0": String.valueOf(res));
		return value;
	}
	private void putKeyMapValue(KmMxZVO[] kvos ,FseJyeVO[] fsvos, Map<String, DZFDouble> keymap,Map<String, Set<String>> tzpzmap) {
		for (Entry<String, DZFDouble> entry : keymap.entrySet()) {
			String key = entry.getKey();
			DZFDouble value = entry.getValue();
			if (!StringUtil.isEmpty(key)) {
				if(key.indexOf("ncye")>=0 || key.indexOf("qmye")>=0){
					for (FseJyeVO fsvo : fsvos) {
						if(key.indexOf(fsvo.getKmbm())>=0){
							if(key.indexOf("ncye")>=0){
								value = SafeCompute.add(fsvo.getQcjf(), fsvo.getQcdf());
							}else if(key.indexOf("qmye")>=0){
								value =  SafeCompute.add(fsvo.getQmjf(), fsvo.getQmdf());
							}
						}
					}
				}
				if(key.indexOf("bndffs")>=0 || key.indexOf("bnjffs")>=0){
					key = key.substring(0, key.length()-1);
					String[] temps = key.split(",");
					if(temps.length>0){
						for (KmMxZVO kmmxvo : kvos) {
							if (temps.length > 1) {//只是过滤凭证
								boolean bcontinue = true;
								if (!StringUtil.isEmpty(kmmxvo.getPk_tzpz_h()) && temps[0].indexOf(kmmxvo.getKmbm()) >= 0) {// 是否包含该科目
									Set<String> tzpzset = tzpzmap.get(kmmxvo.getPk_tzpz_h());
									if (tzpzset != null && tzpzset.size() > 0) {
										for (int i = 1; i < temps.length; i++) {
											// 不包含对照科目，则不进行运算
											if (temps[i].startsWith("un") ) {
												if(bcontain(tzpzset, temps[i].substring(2))){
													bcontinue = false;
												}
											}else if(temps[i].indexOf("&")>=0){
												bcontinue = filterArray(temps, tzpzset, i);
											}else if(!bcontain(tzpzset, temps[i])){
												bcontinue = false;
											}
										}
									}
									if (bcontinue) {
										if (key.indexOf("bndffs") >= 0) {
											value = SafeCompute.add(value, kmmxvo.getDf());
										} else {
											value = SafeCompute.add(value, kmmxvo.getJf());
										}
									}
								}
							} else {//只是计算
								if (ReportUtil.bSysZy(kmmxvo) && "本年累计".equals(kmmxvo.getZy()) &&  key.indexOf(kmmxvo.getKmbm()) >= 0) {
									if (key.indexOf("bndffs") >= 0) {
										value = VoUtils.getDZFDouble(kmmxvo.getDf());
									} else {
										value = VoUtils.getDZFDouble(kmmxvo.getJf());
									}
								}
							}
						}
					}
				}
			}
			entry.setValue(value);
		}
	}
	
	private boolean bcontain(Set<String> set,String value){
		boolean res = false;
		for(String str:set){
			if(str.startsWith(value)){
				res = true;
				break;
			}
		}
		return res;
	}
	
	private boolean filterArray(String[] temps, Set<String> tzpzset, int i) {
		boolean res = false;
		String[] tt = temps[i].split("&");
		for(String str_tt:tt){
			if(tzpzset.contains(str_tt)){
				return true;
			}
		}
		return res;
	}
	private void getKeyMap(String queryAccountRule, List<QyBdVO> list, Pattern p, Map<String, DZFDouble> keymap) {
		Matcher m;
		for(QyBdVO vo:list){
			if(!StringUtil.isEmpty(vo.getFormula())){
				m = p.matcher(vo.getFormula());
				String key = "";
				while(m.find()){
					key = getNewKey(m.group(0), queryAccountRule);
					vo.setFormula(vo.getFormula().replace(m.group(0), key));//替换
					keymap.put(key, DZFDouble.ZERO_DBL);
				}
			}
		}
	}
	private void putTzpzMap(Map<String, Set<String>> tzpzmap, KmMxZVO[] kvos) {
		for(KmMxZVO vo:kvos){
			if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
				String tzpzmap_value = getTzpzMapKey(vo);
				if(tzpzmap.containsKey(vo.getPk_tzpz_h())){
					tzpzmap.get(vo.getPk_tzpz_h()).add(tzpzmap_value);
				}else{
					Set<String> valueset = new HashSet<String>();
					valueset.add(tzpzmap_value);
					tzpzmap.put(vo.getPk_tzpz_h(), valueset);
				}
			}
		}
	}
	private String getTzpzMapKey(KmMxZVO vo) {
		String tzpzmap_value = null;
		if(VoUtils.getDZFDouble(vo.getJf()).doubleValue()!=0){
			tzpzmap_value = "jfkm_"+vo.getKmbm();
		}else{
			tzpzmap_value = "dfkm_"+vo.getKmbm();
		}
		return tzpzmap_value;
	}
	
	private String getNewKey(String key,String queryAccountRule){
		if(key.indexOf("hc")>=0){
			return key;
		}
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(key);   
		String oldkmbm = m.replaceAll("").trim();
		
		String newkmbm = zxkjPlatformService.getNewRuleCode(oldkmbm, DZFConstant.ACCOUNTCODERULE, queryAccountRule);// 销项
		
		key = key.replace(oldkmbm, newkmbm);
		
		return key;
	}


}
