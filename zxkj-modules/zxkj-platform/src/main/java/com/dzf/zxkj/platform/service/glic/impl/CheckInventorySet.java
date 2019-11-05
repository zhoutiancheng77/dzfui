package com.dzf.zxkj.platform.service.glic.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("inventory_setcheck")
public class CheckInventorySet {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
//	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 总账存货校验
	 */
	public String checkInventorySet(String userid, String pk_corp, InventorySetVO vo) throws DZFWarpException {
		
		return checkInventorySet(userid, pk_corp, vo, null);
	}
	
	/**
	 * 总账存货校验
	 */
	public String checkInventorySet(String userid,String pk_corp,InventorySetVO vo,String pk_auacount_b) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			return "";
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		if(cpvo == null)
			return "";
		//启用总账存货的参与校验
		if(!IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic()))
			return "";
		int chcbjzfs = InventoryConstant.IC_NO_MXHS;//不核算存货
		if(vo != null)
			chcbjzfs = vo.getChcbjzfs();
		if(chcbjzfs == InventoryConstant.IC_NO_MXHS)//不核算存货
			return "";
		StringBuffer sbf = new StringBuffer();
		YntCpaccountVO[] accountvos = accountService.queryByPk(cpvo.getPk_corp());
		if(chcbjzfs == InventoryConstant.IC_CHDLHS){//大类
			StringBuffer sf = new StringBuffer();
			Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accountvos), new String[]{"accountcode"});
			checkInventoryDoc_DL(pk_corp,chcbjzfs,sf,pk_auacount_b);//校验存货
			checkKmDoc_DL(cpvo,userid,chcbjzfs,sf,map,accountvos);//校验科目
			checkChukuKM(cpvo,userid,sf,map,accountvos);//校验出库科目
			if(sf.length()>0){
				sbf.append("启用存货大类：<br>");
				sbf.append(sf);
			}
		}else if(chcbjzfs == InventoryConstant.IC_FZMXHS){//明细
			StringBuffer sf = new StringBuffer();
			Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accountvos), new String[]{"accountcode"});
			checkKmDoc_MX(cpvo,userid,chcbjzfs,sf,map,accountvos);//校验科目
			checkChukuKM(cpvo,userid,sf,map,accountvos);//校验出库科目
			if(sf.length()>0){
				sbf.append("启用明细核算：<br>");
				sbf.append(sf);
			}
		}
		return sbf.toString();
	}
	
	//大类核算校验 存货档案
	private void checkInventoryDoc_DL(String pk_corp,int hsstyle,StringBuffer sf,String pk_auacount_b) throws BusinessException {
		if(InventoryConstant.IC_CHDLHS != hsstyle)//存货大类
			return;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam("000001000000000000000006");
		String condition = " pk_corp = ? and nvl(dr,0) = 0 and pk_auacount_h = ? ";
		if(DZFValueCheck.isNotEmpty(pk_auacount_b)){
			sp.addParam(pk_auacount_b);
			condition =condition+" and pk_auacount_b=? ";
		}
		AuxiliaryAccountBVO[] bodyvos = (AuxiliaryAccountBVO[])singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);
		if(bodyvos == null || bodyvos.length == 0)
			return;
		for(AuxiliaryAccountBVO b : bodyvos){
			if(StringUtil.isEmpty(b.getKmclassify())){
				sf.append("存货档案的存货分类不能为空。<br>");
				break;
			}
		}
	}
	//大类核算校验 科目
	private void checkKmDoc_DL(CorpVO cpvo,String userid,int hsstyle,StringBuffer sf,Map<String, YntCpaccountVO> map,YntCpaccountVO[] accountvos) throws BusinessException{
		if(InventoryConstant.IC_CHDLHS != hsstyle)//存货大类
			return;
		String corptype = cpvo.getCorptype();//取科目方案
		//取库存商品、原材料。
		//如果 科目只有1405 或者 1403 ，不校验。
		//如果 科目 1405 或者 1403  有2级，则校验 2级是否符合规定。
		//如果 科目 1405 或者 1403  有3级及以下，则校验 3级及以下是否符合规定。如果2级校验是 非末级。则后面不用校验。
		List<String> list1 = Kmschema.getAllCunhuo_KM(corptype ,accountvos);
		if(list1 != null && list1.size() > 0){
			Map<String,List<String>> mapvalue = new HashMap<String,List<String>>();
			for(String key : list1){
				YntCpaccountVO kmvo = map.get(key);
				StringBuffer sb = new StringBuffer();
				if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 1){//1级
					if(kmvo.getIsleaf()!=null && kmvo.getIsleaf().booleanValue()){
						sb.append("必须增加二级，二级作为大类启用数量、存货辅助");
					}
				}else if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 2){//2级
					if(kmvo.getIsleaf()!=null && !kmvo.getIsleaf().booleanValue()){
						sb.append("必须为末级");
					}
					if(kmvo.getIsfzhs() == null || (kmvo.getIsfzhs()!=null && !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5))))){
						if(sb.length()>0){
							sb.append("、启用存货辅助");
						}else{
							sb.append("必须启用存货辅助");
						}
					}
					if(kmvo.getIsnum() == null ||(kmvo.getIsnum()!=null && !kmvo.getIsnum().booleanValue())){
						if(sb.length()>0){
							sb.append("、启用数量核算");
						}else{
							sb.append("必须启用数量核算");
						}
					}
				}
				if(sb.length() > 0){
					if(mapvalue.containsKey(sb.toString())){
						mapvalue.get(sb.toString()).add(kmvo.getAccountcode());
					}else{
						List<String> bmlist = new ArrayList<String>();
						bmlist.add(kmvo.getAccountcode());
						mapvalue.put(sb.toString(), bmlist);
					}
				}
			}
			if(mapvalue.size()>0){
				for(String key : mapvalue.keySet()){
					sf.append("入库"+getPromptName(mapvalue.get(key))+"科目，");
					sf.append(key+"。<br>");
				}
			}
			mapvalue.clear();
		}
	}
	
	//明细核算校验 科目
	private void checkKmDoc_MX(CorpVO cpvo,String userid,int hsstyle,StringBuffer sf,Map<String, YntCpaccountVO> map,YntCpaccountVO[] accountvos) throws BusinessException{
		if(InventoryConstant.IC_FZMXHS != hsstyle)//辅助明细
			return;
		String corptype = cpvo.getCorptype();//取科目方案
		//取库存商品、原材料。
		//如果 科目只有1405 或者 1403 一级必须为末级。
		List<String> list1 = Kmschema.getAllCunhuo_KM(corptype ,accountvos);
		if(list1 != null && list1.size() > 0){
			Map<String,List<String>> mapvalue = new HashMap<String,List<String>>();
			for(String key : list1){
				YntCpaccountVO kmvo = map.get(key);
				StringBuffer sb = new StringBuffer();
				if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 1){//1级
					if(kmvo.getIsleaf()!=null && !kmvo.getIsleaf().booleanValue()){
						sb.append("必须为末级");
					}
					if(kmvo.getIsfzhs() == null || (kmvo.getIsfzhs()!=null && !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5))))){
						if(sb.length()>0){
							sb.append("、启用存货辅助");
						}else{
							sb.append("必须启用存货辅助");
						}
					}
					if(kmvo.getIsnum() == null ||(kmvo.getIsnum()!=null && !kmvo.getIsnum().booleanValue())){
						if(sb.length()>0){
							sb.append("、启用数量核算");
						}else{
							sb.append("必须启用数量核算");
						}
					}
				}
				if(sb.length() > 0){
					if(mapvalue.containsKey(sb.toString())){
						mapvalue.get(sb.toString()).add(kmvo.getAccountcode());
					}else{
						List<String> bmlist = new ArrayList<String>();
						bmlist.add(kmvo.getAccountcode());
						mapvalue.put(sb.toString(), bmlist);
					}
				}
			}
			if(mapvalue.size()>0){
				for(String key : mapvalue.keySet()){
					sf.append("入库"+getPromptName(mapvalue.get(key))+"科目，");
					sf.append(key+"。<br>");
				}
			}
			mapvalue.clear();
		}
	}
	
	private void checkChukuKM(CorpVO cpvo,String userid,StringBuffer sf,Map<String, YntCpaccountVO> map,YntCpaccountVO[] accountvos){
		//校验 收入类科目是否启用存货辅助  和  数量核算。
		String corptype = cpvo.getCorptype();//取科目方案
		List<String> list2 = Kmschema.getAllChuku_KM(corptype ,accountvos);
		if(list2 != null && list2.size() > 0){
			Map<String,List<String>> mapvalue = new HashMap<String,List<String>>();
			for(String key : list2){
				YntCpaccountVO kmvo = map.get(key);
				StringBuffer sb = new StringBuffer();
				if(kmvo.getIsfzhs() == null || (kmvo.getIsfzhs()!=null && !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5))))){
					sb.append("必须启用存货辅助");
				}
				if(kmvo.getIsnum() == null ||(kmvo.getIsnum()!=null && !kmvo.getIsnum().booleanValue())){
					if(sb.length()>0){
						sb.append("、启用数量核算");
					}else{
						sb.append("必须启用数量核算");
					}
				}
				if(sb.length() > 0){
					if(mapvalue.containsKey(sb.toString())){
						mapvalue.get(sb.toString()).add(kmvo.getAccountcode());
					}else{
						List<String> bmlist = new ArrayList<String>();
						bmlist.add(kmvo.getAccountcode());
						mapvalue.put(sb.toString(), bmlist);
					}
				}
			}
			if(mapvalue.size()>0){
				for(String key : mapvalue.keySet()){
					sf.append("出库"+getPromptName(mapvalue.get(key))+"科目，");
					sf.append(key+"。<br>");
				}
			}
			mapvalue.clear();
		}
	}
	
	
	private String getPromptName(List<String> list){
		if(list == null || list.size() ==0)
			return "";
		StringBuffer sf = new StringBuffer();
		sf.append("[");
		for(int i=0;i<list.size();i++){
			if(i>3){
				sf.append(" ...");
				break;
			}else{
				if(i ==list.size()-1){
					sf.append(list.get(i));
				}else{
					sf.append(list.get(i)+",");
				}
			}
		}
		sf.append("]");
		return sf.toString();
	}
	
	/**
	 * 总账存货校验---仅校验凭证
	 */
	public String checkInventorySetByPZ(String userid, CorpVO cpvo, TzpzHVO headvo) throws DZFWarpException {
		if(cpvo == null
				|| headvo == null 
				|| headvo.getChildren() == null 
				|| headvo.getChildren().length == 0)
			return "";
		
		Map<String, Set<String>> pzkmidmap = getPZKMID(headvo);
		
		return  checkInventorySetCommon(userid, cpvo, pzkmidmap);
	}
	
	
	/**
	 * 总账存货校验--- 传入组装后的map  校验科目 和存货
	 */
	public String checkInventorySetCommon(String userid,CorpVO cpvo,Map<String,Set<String>> pzkmidmap) throws DZFWarpException {
		
		if(pzkmidmap == null || pzkmidmap.size()==0)
			return "";
		
		Set<String> kmids = pzkmidmap.get("KMID");
		Set<String> chids = pzkmidmap.get("CHID");
		
		String pk_corp = cpvo.getPk_corp();
		if(StringUtil.isEmpty(pk_corp))
			return "";
		//启用总账存货的参与校验
		if(!IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic()))
			return "";
		InventorySetVO vo = gl_ic_invtorysetserv.query(pk_corp);
		int chcbjzfs = InventoryConstant.IC_NO_MXHS;//不核算存货
		if(vo != null)
			chcbjzfs = vo.getChcbjzfs();
		if(chcbjzfs == InventoryConstant.IC_NO_MXHS)//不核算存货
			return "";
		YntCpaccountVO[] accountvos = accountService.queryByPk(cpvo.getPk_corp());
		Map<String, YntCpaccountVO> kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accountvos), new String[]{"pk_corp_account"});
		boolean isDl = true;
		String result = "";
		if(chcbjzfs == InventoryConstant.IC_CHDLHS){//大类
			isDl =  true;
			result = "<font color='blue'>存货大类核算</font>";
		}else if(chcbjzfs == InventoryConstant.IC_FZMXHS){//明细
			isDl =  false;
			result = "<font color='blue'>辅助明细核算</font>";
		}
		//校验科目 
		String error1 = checkKM(cpvo.getCorptype(), kmids,kmmap,isDl);
		//校验大类
		String error2 = checkDL(pk_corp,chids,isDl);
		StringBuffer sbf = new StringBuffer();
		if(!StringUtil.isEmpty(error1) || !StringUtil.isEmpty(error2)){
			sbf.append(result+"<br>");
			sbf.append(error1);
			sbf.append(error2);
		}		
		return sbf.toString();
	}
	/**
	 * 取得科目ID
	 */
	private Map<String,Set<String>> getPZKMID(TzpzHVO headvo){
		if(headvo == null 
				|| headvo.getChildren() == null 
				|| headvo.getChildren().length == 0)
			return null;
		TzpzBVO[] bodyvos = (TzpzBVO[])headvo.getChildren();
		Set<String> set1 = new HashSet<String>();
		Set<String> set2= new HashSet<String>();
		for(TzpzBVO bvo : bodyvos){
			String pk_accsubj = bvo.getPk_accsubj();
			String fzhsx6 = bvo.getFzhsx6();
			if(!StringUtil.isEmpty(pk_accsubj)){
				set1.add(pk_accsubj);
			}
			if(!StringUtil.isEmpty(fzhsx6)){
				set2.add(fzhsx6);
			}
		}
		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
		map.put("KMID", set1);
		map.put("CHID", set2);
		return map;
	}
	
	private String checkDL(String pk_corp,Set<String> chids,boolean isDl){
		if(chids == null || chids.size() ==0)
			return "";
		if(!isDl)
			return "";
		SQLParameter sp = new SQLParameter();
		sp.addParam(AuxiliaryConstant.ITEM_INVENTORY);
		sp.addParam(pk_corp);
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[] )singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, " nvl(dr,0) = 0 and  pk_auacount_h = ? and pk_corp = ? ", sp);
		Map<String, AuxiliaryAccountBVO> fzmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos), new String[]{"pk_auacount_b"});
		StringBuffer sbf = new StringBuffer();
		for(String key : chids){
			StringBuffer af = new StringBuffer();
			AuxiliaryAccountBVO bvo = fzmap.get(key);
			if(bvo == null)
				continue;
			if(StringUtil.isEmpty(bvo.getKmclassify())){
				af.append("存货分类不允许为空");
			}
			if(af.length()>0){
				sbf.append("辅助存货："+bvo.getCode()+"_"+bvo.getName()+"，");
				sbf.append(af);
				sbf.append("<br>");
			}
		}
		return sbf.toString();
	}
	
	private String checkKM(String corptype, Set<String> kmids,Map<String, YntCpaccountVO> kmmap,boolean isDl){
		if(kmids == null || kmids.size() ==0)
			return "";
		StringBuffer sbf = new StringBuffer();
		for(String key : kmids){
			YntCpaccountVO kmvo = kmmap.get(key);
			if(kmvo==null)
				continue;
			StringBuffer sf = new StringBuffer();
			if(Kmschema.isKcspbm(corptype, kmvo.getAccountcode())){
				//1、大类必须为2级科目，明细必须为1级科目
				if(isDl){//大类
					if(kmvo.getAccountlevel() !=2){
						sf.append("二级科目为末级科目");
					}
				}else{//明细
					if(kmvo.getAccountlevel() !=1){
						sf.append("一级科目为末级科目");
					}
				}
				//2、必须启用 存货辅助
				if(StringUtil.isEmpty(kmvo.getIsfzhs()) || !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5)))){
					if(sf.length()>0){
						sf.append("、启用存货辅助");
					}else{
						sf.append("启用存货辅助");
					}
				}
				//3、必须启用数量核算
				if(kmvo.getIsnum() == null || !kmvo.getIsnum().booleanValue()){
					if(sf.length()>0){
						sf.append("、启用数量核算");
					}else{
						sf.append("启用数量核算");
					}
				}
			}else if(Kmschema.isshouru(corptype, kmvo.getAccountcode())){
				//1、必须启用 存货辅助
				if(StringUtil.isEmpty(kmvo.getIsfzhs()) || !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5)))){
					sf.append("启用存货辅助");
				}
				//2、必须启用数量核算
				if(kmvo.getIsnum() == null || !kmvo.getIsnum().booleanValue()){
					if(sf.length()>0){
						sf.append("、启用数量核算");
					}else{
						sf.append("启用数量核算");
					}
				}
			}
			if(sf.length()>0){
				sbf.append("科目："+kmvo.getCodename()+"，需要");
				sbf.append(sf);
				sbf.append("<br>");
			}
		}
		return sbf.toString();
	}
}