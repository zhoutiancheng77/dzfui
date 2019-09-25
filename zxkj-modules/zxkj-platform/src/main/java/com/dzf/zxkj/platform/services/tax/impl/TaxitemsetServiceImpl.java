package com.dzf.zxkj.platform.services.tax.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.tree.BDTreeCreator;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AccountTreeStrategy;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemPzShowVO;
import com.dzf.zxkj.platform.model.tax.TaxitemRelationVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.tax.ITaxitemsetService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sys_taxsetserv")
public class TaxitemsetServiceImpl implements ITaxitemsetService {

    @Autowired
    private SingleObjectBO singleObjectBO = null;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IAccountService accountService;
	
	@Override
	public List<TaxitemVO> queryItembycode(String userid, String pk_corp, String subjcode) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(subjcode))
			return null;
		CorpVO rpvo = corpService.queryByPk(pk_corp);
		String corptype = rpvo.getCorptype();
		String chargename = StringUtil.isEmpty(rpvo.getChargedeptname()) ? "小规模纳税人":rpvo.getChargedeptname();
		String coderule = StringUtil.isEmpty(rpvo.getAccountcoderule()) ? DZFConstant.ACCOUNTCODERULE:rpvo.getAccountcoderule();
		TaxitemVO[] vos = null;
		if("00000100AA10000000000BMD".equals(corptype)){//2013
			if("一般纳税人".equals(chargename)){
				if(subjcode.startsWith("1403") || subjcode.startsWith("1405") || subjcode.startsWith("1601")){//资产类，库存商品和原材料，固定资产
					vos = queryTaxitems(chargename,"2");
				}else if(subjcode.startsWith("5001") || subjcode.startsWith("5051") || subjcode.startsWith("5301")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}else if(subjcode.startsWith("5401") || subjcode.startsWith("5402")
						|| subjcode.startsWith("5601") || subjcode.startsWith("5602")
						|| subjcode.startsWith("5603")){//损益，成本、费用
					vos = queryTaxitems(chargename,"2");
				}
			}else{
				if(subjcode.startsWith("5001") || subjcode.startsWith("5051") || subjcode.startsWith("5301")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}
			}
		}else if("00000100AA10000000000BMF".equals(corptype)){//2007
			if("一般纳税人".equals(chargename)){
				if(subjcode.startsWith("1403") || subjcode.startsWith("1405") || subjcode.startsWith("1601")){//资产类，库存商品和原材料，固定资产
					vos = queryTaxitems(chargename,"2");
				}else if(subjcode.startsWith("6001") || subjcode.startsWith("6051") || subjcode.startsWith("6301")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}else if(subjcode.startsWith("6401") || subjcode.startsWith("6402")
						|| subjcode.startsWith("6601") || subjcode.startsWith("6602")
						|| subjcode.startsWith("6603")){//损益，成本、费用
					vos = queryTaxitems(chargename,"2");
				}
			}else{
				if(subjcode.startsWith("6001") || subjcode.startsWith("6051") || subjcode.startsWith("6301")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}
			}
		}else if("00000100AA10000000000BMQ".equals(corptype)){//民间
			if("一般纳税人".equals(chargename)){
				if(subjcode.startsWith("1201") || subjcode.startsWith("1501") ){//资产类，库存商品，固定资产
					vos = queryTaxitems(chargename,"2");
				}else if(subjcode.startsWith("4101") || subjcode.startsWith("4201") || subjcode.startsWith("4301")||
						subjcode.startsWith("4401") || subjcode.startsWith("4501") || subjcode.startsWith("4601")
						||subjcode.startsWith("4901")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}else if(subjcode.startsWith("5101") || subjcode.startsWith("5201")
						|| subjcode.startsWith("5301") || subjcode.startsWith("5401")){//损益，成本、费用
					vos = queryTaxitems(chargename,"2");
				}
			}else{
				if(subjcode.startsWith("4101") || subjcode.startsWith("4201") || subjcode.startsWith("4301")||
						subjcode.startsWith("4401") || subjcode.startsWith("4501") || subjcode.startsWith("4601")
						||subjcode.startsWith("4901")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}
			}
		}else if("00000100000000Ig4yfE0003".equals(corptype)){//事业
			if("一般纳税人".equals(chargename)){
				if(subjcode.startsWith("1301") || subjcode.startsWith("1501") ){//资产类，库存商品，固定资产
					vos = queryTaxitems(chargename,"2");
				}else if(subjcode.startsWith("4001") || subjcode.startsWith("4101") || subjcode.startsWith("4201")||
						subjcode.startsWith("4301") || subjcode.startsWith("4401") || subjcode.startsWith("4501")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}else if(subjcode.startsWith("5001") || subjcode.startsWith("5401")){//损益，成本、费用
					vos = queryTaxitems(chargename,"2");
				}
			}else{
				if(subjcode.startsWith("4001") || subjcode.startsWith("4101") || subjcode.startsWith("4201")||
						subjcode.startsWith("4301") || subjcode.startsWith("4401") || subjcode.startsWith("4501")){//损益，收入类
					vos = queryTaxitems(chargename,"1");
				}
			}
		}else if("00000100000000Ig4yfE0005".equals(corptype)){//企业会计制度
			if("一般纳税人".equals(chargename)){
				if(subjcode.startsWith("1243") || subjcode.startsWith("1211") 
						|| subjcode.startsWith("1501")){//资产类，[库存商品，固定资产]
					vos = queryTaxitems(chargename,"2");
				}else if(subjcode.startsWith("5101")
						|| subjcode.startsWith("5102")
						|| subjcode.startsWith("5201")
						|| subjcode.startsWith("5203")
						|| subjcode.startsWith("5301")
						){//损益，[收入类]
					vos = queryTaxitems(chargename,"1");
				}else if(subjcode.startsWith("5401") 
						|| subjcode.startsWith("5501")
						|| subjcode.startsWith("5502")
						|| subjcode.startsWith("5503")
						|| subjcode.startsWith("5405")
						|| subjcode.startsWith("5601")
						){//损益，[成本、费用]
					vos = queryTaxitems(chargename,"2");
				}
			}else{
				if(subjcode.startsWith("5101")
						|| subjcode.startsWith("5102")
						|| subjcode.startsWith("5201")
						|| subjcode.startsWith("5203")
						|| subjcode.startsWith("5301")){//损益，[收入类]
					vos = queryTaxitems(chargename,"1");
				}
			}
		}
		//赋默认值
		List<TaxitemVO> list = dosetDefault(rpvo,subjcode,vos,pk_corp,chargename,corptype,coderule);
		//按编码排序
		onsort1(list);
		return list;
	}
	
	private void onsort1(List<TaxitemVO> list){
		if(list == null || list.size() == 0)
			return;
		Collections.sort(list, new Comparator<TaxitemVO>(){
			@Override
			public int compare(TaxitemVO t1, TaxitemVO t2) {
				return t1.getIorder().compareTo(t2.getIorder());
			}
		});
	}
	
	private List<TaxitemVO> dosetDefault(CorpVO rpvo,String subjcode,TaxitemVO[] vos,String pk_corp,
			String chargedeptname ,String corptype,String coderule){
		if(vos == null || vos.length == 0)
			return null;
		List<TaxitemRelationVO> listrelations = getDefaultSet(rpvo,subjcode,vos,pk_corp,chargedeptname,corptype,coderule);
		List<TaxitemVO> list = new ArrayList<TaxitemVO>(Arrays.asList(vos));
		if(listrelations != null && listrelations.size() > 0 ){
			Map<String,TaxitemVO> maps = DZfcommonTools.hashlizeObjectByPk(list, new String[]{"pk_taxitem"});
			for(TaxitemRelationVO key : listrelations){
				if(maps.containsKey(key.getPk_taxitem())){
					maps.get(key.getPk_taxitem()).setIsselect(DZFBoolean.TRUE);
				}
			}
			list = new ArrayList<TaxitemVO>(maps.values());;
		}
		return list;
	}
	
	private List<TaxitemRelationVO> getDefaultSet(CorpVO rpvo,String subjcode,TaxitemVO[] vos,
			String pk_corp,String chargedeptname ,String corptype,String coderule){
		if(vos == null || vos.length == 0)
			return null;
		TaxitemRelationVO[] relatvos = queryRelationVOs(pk_corp,chargedeptname,corptype);
		if(relatvos==null || relatvos.length==0)
			return null;
		List<TaxitemRelationVO> list = new ArrayList<TaxitemRelationVO>(Arrays.asList(relatvos));
		Map<String,List<TaxitemRelationVO>> maps = DZfcommonTools.hashlizeObject(list, new String[]{"pk_corp"});
		//取公司数据
		List<TaxitemRelationVO> list1 = maps.get(pk_corp);
		List<TaxitemRelationVO> relationvo = queryCascade(list1,subjcode,coderule);
		//公司没有返回数据，在取集团数据
		if(relationvo == null || relationvo.size() == 0){
			List<TaxitemRelationVO> list2 = maps.get(IDefaultValue.DefaultGroup);
			//将科目list2的标准科目升级为升级后的科目体系
			doUpdateKmcode(rpvo,list2);
			relationvo = queryCascade(list2,subjcode,coderule);
		}
		return relationvo;
	}
	
	private void doUpdateKmcode(CorpVO rpvo,List<TaxitemRelationVO> list2){
		if(list2 != null && list2.size() > 0){
			for(TaxitemRelationVO vo : list2){
				String subjcode = vo.getSubj_code();
				String updatecode= getUpdateKmcode(rpvo,subjcode);
				vo.setSubj_code(updatecode);
			}
		}
	}
	
	private String getUpdateKmcode(CorpVO  rpvo,String code){
		Map<String,String> map = KmbmUpgrade.getKmUpgradeinfo(rpvo, new String[]{code});
		String result = code;
		if(map!=null && map.size()>0){
			for(String key : map.keySet()){
				if(code.equals(map.get(key))){
					result = key;
					break;
				}
			}
		}
		return result;
	}
	
	private List<TaxitemRelationVO> queryCascade(List<TaxitemRelationVO> list,String subjcode,String coderule){
		if(StringUtil.isEmpty(subjcode))
			return null;
		if(list == null || list.size() == 0)
			return null;
		Map<String,List<TaxitemRelationVO>> maps =  DZfcommonTools.hashlizeObject(list, new String[]{"subj_code"});
		List<TaxitemRelationVO> listrelastion = getParentVO(maps,subjcode,coderule);
		return listrelastion;
	}
	
	private List<TaxitemRelationVO> getParentVO(Map<String,List<TaxitemRelationVO>> maps,String subjcode,String coderule){
		if(StringUtil.isEmpty(subjcode))
			return null;
		//先匹配公司级数据
		//先取相等的。
		//在取上级的，上上级的。直到结束
		List<TaxitemRelationVO> list = maps.get(subjcode);
		if(list!= null && list.size()>0){
			return list;
		}else{
			if(subjcode.length() == 4)
				return null;
			String parent = DZfcommonTools.getParentCode(subjcode, coderule);
			return getParentVO(maps,parent,coderule);
		}
	}
	
	private TaxitemRelationVO[] queryRelationVOs(String pk_corp,String chargedeptname ,String corptype){
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(corptype))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(corptype);
		sp.addParam(chargedeptname);
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		String where = " corptype = ? and  nvl(chargedeptname,'小规模纳税人') = ? and pk_corp in(?,?)  and nvl(dr,0) = 0 ";
		TaxitemRelationVO[] vos =  (TaxitemRelationVO[])singleObjectBO.queryByCondition(TaxitemRelationVO.class, where, sp);
		return vos;
	}
	
	private TaxitemVO[] queryTaxitems(String chargename,String style){
		if(StringUtil.isEmpty(style))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(chargename);
		sp.addParam(style);
		sp.addParam(IDefaultValue.DefaultGroup);
		String where = " nvl(chargedeptname,'小规模纳税人') = ? and  taxstyle = ? and nvl(dr,0) = 0 and nvl(pk_corp,'000001') = ? ";
		TaxitemVO[] vos =  (TaxitemVO[])singleObjectBO.queryByCondition(TaxitemVO.class, where, sp);
		return vos;
	}
	
	public List<TaxitemVO> queryAllTaxitems() throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String where = " pk_corp = ? and nvl(dr,0) = 0 order by iorder ";
		TaxitemVO[] vos =  (TaxitemVO[])singleObjectBO.queryByCondition(TaxitemVO.class, where, sp);
		return vos == null || vos.length == 0 ? null : new ArrayList<TaxitemVO>(Arrays.asList(vos));
	} 
	
	/**
	 * 查询科目显示
	 */
	public List<TaxitemPzShowVO> queryKMShow(String userid, String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			return null;
		CorpVO rpvo = corpService.queryByPk(pk_corp);
		String corptype = rpvo.getCorptype();
		String chargename = StringUtil.isEmpty(rpvo.getChargedeptname()) ? "小规模纳税人":rpvo.getChargedeptname();
		TaxitemRelationVO[] relatvos = queryRelationVOs(pk_corp,chargename,corptype);
		if(relatvos==null || relatvos.length==0)
			return null;
		List<TaxitemRelationVO> list = new ArrayList<TaxitemRelationVO>(Arrays.asList(relatvos));
		Map<String,List<TaxitemRelationVO>> maps = DZfcommonTools.hashlizeObject(list, new String[]{"pk_corp"});
		List<TaxitemRelationVO> list1 = maps.get(pk_corp);//公司数据 
		List<TaxitemRelationVO> list2 = maps.get(IDefaultValue.DefaultGroup);//集团数据
		//组装
		YntCpaccountVO[] accountvos = accountService.queryByPk(pk_corp);
		List<TaxitemPzShowVO> list3 = doMerge(list1,list2,rpvo,accountvos);
		//生成相对应的上下级科目VO信息
		List<TaxitemPzShowVO> list4 = createAllKmTaxitemVO(list3,accountvos,rpvo);
		//将list4中区分出集团和公司的属性
		List<TaxitemPzShowVO> zlist = filterPzshowVO(pk_corp,list4);
		//按iorder排序
		onsort2(zlist);
		return zlist;
	}
	

	@Override
	public List<TaxitemPzShowVO> queryPzShow(String userid, String pk_corp) throws DZFWarpException {
		List<TaxitemPzShowVO> pzlist = queryKMShow(userid,pk_corp);
		if(pzlist == null || pzlist.size() == 0)
			return pzlist;
		List<TaxitemPzShowVO> zlist = new ArrayList<TaxitemPzShowVO>();
		for(TaxitemPzShowVO z : pzlist){
			DZFBoolean  pzshow = z.getShuimushowpz();
			if(pzshow != null && pzshow.booleanValue()){
				zlist.add(z);
			}
		}
		return zlist;
	}
	
	private void onsort2(List<TaxitemPzShowVO> list){
		if(list == null || list.size() == 0)
			return;
		Collections.sort(list, new Comparator<TaxitemPzShowVO>(){
			@Override
			public int compare(TaxitemPzShowVO t1, TaxitemPzShowVO t2) {
				return t1.getIorder().compareTo(t2.getIorder());
			}
		});
	}
	
	private List<TaxitemPzShowVO> filterPzshowVO(String pk_corp,List<TaxitemPzShowVO> list4){
		if(list4 == null || list4.size() == 0)
			return null;
		Map<String,List<TaxitemPzShowVO>> map1 = DZfcommonTools.hashlizeObject(list4, new String[]{"subj_code"});
		List<TaxitemPzShowVO> zlist = new ArrayList<TaxitemPzShowVO>();
		for(String key : map1.keySet()){
			List<TaxitemPzShowVO> list = map1.get(key);
			if(list == null || list.size() == 0)
				continue;
			if(isCorpDataExists(list)){
				for(TaxitemPzShowVO v : list){
					if(!IDefaultValue.DefaultGroup.equals(v.getPk_corp())){
						zlist.add(v);
					}
				}
			}else{
				zlist.addAll(list);
			}
		}
		for(TaxitemPzShowVO vo :zlist ){
			vo.setPk_corp(pk_corp);
		}
		return zlist;
	}
	
	private boolean isCorpDataExists(List<TaxitemPzShowVO> list){
		if(list == null || list.size() == 0)
			return false;
		for(TaxitemPzShowVO v : list){
			if(!IDefaultValue.DefaultGroup.equals(v.getPk_corp())){
				return true;
			}
		}
		return false;
	}
	
	
	private List<TaxitemPzShowVO> createAllKmTaxitemVO(List<TaxitemPzShowVO> list,YntCpaccountVO[] accountvos,CorpVO rpvo){
		if(list == null || list.size() == 0)
			return null;
		//选判断唯一。(科目编码+税目id 唯一)
		Map<String,TaxitemPzShowVO> maps = new HashMap<String,TaxitemPzShowVO>();
		for(TaxitemPzShowVO a : list){
			String key = a.getSubj_code()+","+a.getPk_taxitem();
			if(maps.containsKey(key)){
				maps.remove(key);
				maps.put(key, a);
			}else{
				maps.put(key, a);
			}
		}
		list = new ArrayList<TaxitemPzShowVO>(maps.values());
		if(list == null || list.size() == 0)
			return null;
		//先排序，按科目升序排列
		onsort(list);
		//将公司科目按树型排列
		YntCpaccountVO[] bodys = buildCpaccountTree(accountvos,rpvo);
		//递归进行赋值
		List<TaxitemPzShowVO> zlist = new ArrayList<TaxitemPzShowVO>();
		for(TaxitemPzShowVO vo : list){
			List<TaxitemPzShowVO> l1 = new ArrayList<TaxitemPzShowVO>();
			createPzVO(bodys,vo,l1,false,false);
			if(l1!=null && l1.size() > 0 ){
				zlist.addAll(l1);
			}
		}
		return zlist;
	}
	
	private boolean createPzVO(YntCpaccountVO[] bodys,TaxitemPzShowVO vo,List<TaxitemPzShowVO> list,boolean ischild,boolean isskip){
		if(vo == null || bodys==null || bodys.length == 0)
			return isskip;
		if(list == null){
			list = new ArrayList<TaxitemPzShowVO>();
		}
		for(YntCpaccountVO bb : bodys){
			if(bb.getAccountcode().equals(vo.getSubj_code())){
				clone(vo,bb,list);//clone加进来
				YntCpaccountVO[] bodyvos = (YntCpaccountVO[])bb.getChildren();
				if(bodyvos != null && bodyvos.length > 0){
					createPzVO(bodyvos,vo,list,true,isskip);
				}
				isskip = true;
				break;
			}else{
				if(ischild){
					clone(vo,bb,list);//clone加进来
				}
				YntCpaccountVO[] bodyvos = (YntCpaccountVO[])bb.getChildren();
				if(bodyvos != null && bodyvos.length > 0){
					isskip = createPzVO(bodyvos,vo,list,ischild,isskip);
					if(isskip)break;
				}
			}
		}
		return isskip;
	}
	
	private void clone(TaxitemPzShowVO vo,YntCpaccountVO bb,List<TaxitemPzShowVO> list){
		TaxitemPzShowVO clone = (TaxitemPzShowVO)vo.clone();
		clone.setPk_accsubj(bb.getPk_corp_account());
		clone.setSubj_code(bb.getAccountcode());
		list.add(clone);
	}
	
	private void onsort(List<TaxitemPzShowVO> list){
		if(list == null || list.size() == 0)
			return;
		Collections.sort(list, new Comparator<TaxitemPzShowVO>(){
			@Override
			public int compare(TaxitemPzShowVO t1, TaxitemPzShowVO t2) {
				int cp = 0;
				String subj1 = t1.getSubj_code() == null ? "0" : t1.getSubj_code();
				String subj2 = t2.getSubj_code() == null ? "0" : t2.getSubj_code();
				cp = subj1.compareTo(subj2);
				if (cp == 0) {
					String tax1 = t1.getTaxcode() == null ? "0" : t1.getTaxcode();
					String tax2 = t2.getTaxcode() == null ? "0" : t2.getTaxcode();
					cp = tax1.compareTo(tax2);
				}
				return cp;
			}
		});
	}
	
	
	private YntCpaccountVO[] buildCpaccountTree(YntCpaccountVO[] accountvos,CorpVO rpvo){
		String coderule = StringUtil.isEmpty(rpvo.getAccountcoderule()) ? DZFConstant.ACCOUNTCODERULE : rpvo.getAccountcoderule();
		YntCpaccountVO vo = (YntCpaccountVO) BDTreeCreator.createTree(accountvos, new AccountTreeStrategy(coderule));
		YntCpaccountVO[] bodyvos = (YntCpaccountVO[]) DZfcommonTools.convertToSuperVO(vo.getChildren());
		return bodyvos;
	}
	
	
	private List<TaxitemPzShowVO> doMerge(List<TaxitemRelationVO> list1,List<TaxitemRelationVO> list2,CorpVO rpvo,YntCpaccountVO[] accountvos) throws DZFWarpException {
		List<TaxitemPzShowVO> listshow = null;
		List<TaxitemRelationVO> lastrelationvo = null;
		list2 = convertocorpInfo(list2,rpvo);
		if(list1 != null&& list1.size() > 0 
				&& list2 != null && list2.size() > 0 ){
			Map<String,List<TaxitemRelationVO>> map1 = DZfcommonTools.hashlizeObject(list1, new String[]{"subj_code"});//公司数据
			Map<String,List<TaxitemRelationVO>> map2 = DZfcommonTools.hashlizeObject(list2, new String[]{"subj_code"});//集团数据
			//以下代码容易出现线不安全情况，报java.util.ConcurrentModificationException异常
//			for(String key : map2.keySet()){
//				if(map1.containsKey(key)){
//					map2.remove(key);
//				}
//			}
			//调用迭代器的删除
			Iterator<String> it = map2.keySet().iterator();
			while(it.hasNext()){
				if(map1.containsKey(it.next())){
					it.remove();//迭代删除
				}
			}
			map1.putAll(map2);
			lastrelationvo = new ArrayList<TaxitemRelationVO>();
			for(String key : map1.keySet()){
				List<TaxitemRelationVO> z1  = map1.get(key);
				lastrelationvo.addAll(z1);
			}
		}else if((list1 != null && list1.size() > 0 )
				&& (list2 == null || list2.size() == 0)){
			lastrelationvo = list1;
		}else if((list2 != null && list2.size() > 0 )
				&& (list1 == null || list1.size() == 0)){
			lastrelationvo = list2;
		}
		//组装TaxitemPzShowVO
		if(lastrelationvo != null && lastrelationvo.size() > 0 ){
			listshow = buildPzshowVO(lastrelationvo,accountvos);
		}
		return listshow;
	}
	
	public List<TaxitemPzShowVO> buildPzshowVO(List<TaxitemRelationVO> lastrelationvo,YntCpaccountVO[] accountvos){
		if(lastrelationvo == null || lastrelationvo.size() == 0)
			return null;
		List<TaxitemPzShowVO> listshow = new ArrayList<TaxitemPzShowVO>();
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>(Arrays.asList(accountvos));
		List<TaxitemVO> itemvos = queryAllTaxitems();
		Map<String,TaxitemVO> mapitem = DZfcommonTools.hashlizeObjectByPk(itemvos, new String[]{"pk_taxitem"});
		Map<String,YntCpaccountVO> accmap = DZfcommonTools.hashlizeObjectByPk(list, new String[]{"accountcode"});
		for(TaxitemRelationVO vv : lastrelationvo){
			String key = vv.getSubj_code();
			if(accmap.containsKey(key)){
				YntCpaccountVO v1 = accmap.get(key);
				TaxitemPzShowVO showvo = buildpzshowvo(v1,vv,mapitem);
				if(showvo!=null){
					listshow.add(showvo);
				}
			}
		}
		return listshow;
	}
	
	private TaxitemPzShowVO buildpzshowvo(YntCpaccountVO v1 ,TaxitemRelationVO v2,Map<String,TaxitemVO> mapitem){
		TaxitemPzShowVO vo = null;
		if(v1 != null && v2 != null && mapitem != null){
			TaxitemVO v3 = mapitem.get(v2.getPk_taxitem());
			if(v3 != null){
				vo = new TaxitemPzShowVO();
				vo.setPk_corp(v2.getPk_corp());//pk_corp 还保持原来的属性
				vo.setPk_accsubj(v1.getPk_corp_account());
				vo.setSubj_code(v1.getAccountcode());
				vo.setTaxcode(v3.getTaxcode());
				vo.setTaxname(v3.getTaxname());
				vo.setTaxratio(v3.getTaxratio());
				vo.setPk_taxitem(v3.getPk_taxitem());
				vo.setShortname(v3.getShortname());
				vo.setIorder(v3.getIorder());
				//是否显示凭证
				vo.setShuimushowpz(v2.getShuimushowpz());
			}
		}
		return vo;
	}
	
	private List<TaxitemRelationVO> convertocorpInfo(List<TaxitemRelationVO> list2,CorpVO rpvo) throws DZFWarpException {
		//将list2 集团subjcode 转换为 当前公司的编码code
		if(list2 == null || list2.size() == 0)
			return null;
		List<String> list = new ArrayList<String>();
		for(TaxitemRelationVO vo : list2){
			list.add(vo.getSubj_code());
		}
		Map<String,String> map = KmbmUpgrade.getKmUpgradeinfo(rpvo,list.toArray(new String[0]));		
		//key,value转换
		Map<String,String> map1 = new HashMap<String,String>();
		if(map!=null && map.size()>0){
			for(String key : map.keySet()){
				map1.put(map.get(key), key);
			}
			map.clear();
		}
		//------------------------
		List<TaxitemRelationVO> list3 = new ArrayList<TaxitemRelationVO>();
		for(TaxitemRelationVO vo : list2){
			String abc = vo.getSubj_code();
			String subj_code = map1.get(abc);
			if(!StringUtil.isEmpty(subj_code)){
				vo.setSubj_code(subj_code);
				list3.add(vo);
			}
		}
		map1.clear();
		list2.clear();
		return list3;
	}
}