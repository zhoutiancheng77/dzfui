package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbBVO;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbHVO;
import com.dzf.zxkj.platform.model.sys.BdTradeTranStemPlateBVO;
import com.dzf.zxkj.platform.model.sys.BdTradeTranstemplateHVO;
import com.dzf.zxkj.platform.services.bdset.ICPSYMBService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 公司期间损益模板维护
 *
 */

@Service("gl_cpsymbserv")
@Slf4j
public class CPSYMBServiceImpl implements ICPSYMBService {

	private SingleObjectBO singleObjectBO = null;
	private ICpaccountService cpaccountService;
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	

	@Override
	public YntCptransmbHVO save(YntCptransmbHVO headvo, String pk_corp, YntCptransmbBVO[] bodyvos, String  corpid, String date, String userid)  throws DZFWarpException {
		if(StringUtil.isEmpty(headvo.getPrimaryKey())){
			HashSet<String> bodys = new HashSet<String>();
			for (YntCptransmbBVO bvo : bodyvos) {
				bodys.add(bvo.getPk_transferoutaccount());
			}
			exist(pk_corp, headvo.getPk_transferinaccount(), headvo.getPk_corp_transtemplate_h(),bodys);//防重复校验
		}
		if(headvo.getPrimaryKey() != null && !"".equals(headvo.getPrimaryKey()) ){//不是新增情况，校验
			YntCptransmbHVO vo=(YntCptransmbHVO) singleObjectBO.queryByPrimaryKey(YntCptransmbHVO.class, headvo.getPrimaryKey());
			
			if(vo == null)
				throw new BusinessException("该数据不存在或已删除，请核对!");
			if(!vo.getPk_corp().equals(pk_corp))
				throw new BusinessException("只能操作当前登录公司权限内的数据");
			 delete(vo);//每次更新时将原数据删除，重新录入，避免删子表时删不了
		}
		setDefaultInfo(headvo,bodyvos,corpid,date,userid);
		headvo.setChildren(bodyvos);
		YntCptransmbHVO svo = (YntCptransmbHVO)singleObjectBO.saveObject(pk_corp, headvo);
		YntCpaccountVO cpaaccvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, svo.getPk_transferinaccount());
		svo.setAccountcode(cpaaccvo.getAccountcode());
		return svo;
				
	}
	//赋默认值
		private void setDefaultInfo(YntCptransmbHVO headvo,YntCptransmbBVO[] bodyvos,String  corpid,String date,String userid){
			if(headvo == null || bodyvos == null || bodyvos.length == 0)
				return;
			headvo.setPk_corp(corpid);
			headvo.setCoperatorid(userid);
			headvo.setDoperatedate(date);
			headvo.setDr(0);
			for(int i=0; i < bodyvos.length;i++){
				
				String[] names = bodyvos[i].getAccountname().split("_");
				bodyvos[i].setVcode(names[0]);
				bodyvos[i].setVname(names[1]);
				bodyvos[i].setPk_corp(corpid);
				bodyvos[i].setDr(0);
			}
		}
	@Override
	public List<YntCptransmbHVO> query(String pk_corp)  throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select a.*,b.accountname ,b.accountcode from   ynt_cptransmb a ");
		sf.append(" left join  ynt_cpaccount b on b.pk_corp_account=a.pk_transferinaccount ");
		sf.append(" where a.pk_corp = ? and nvl(a.dr,0) = 0 ");
		List<YntCptransmbHVO> hlist = (List<YntCptransmbHVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(YntCptransmbHVO.class));
		 List<YntCptransmbHVO>  tlist =translateBdTradeTranstemplateHVO(pk_corp);
		if(hlist != null && hlist.size() > 0)
			tlist.addAll(hlist);
		return tlist;
	}
	
	private List<YntCptransmbHVO> translateBdTradeTranstemplateHVO(String pk_corp) throws DZFWarpException {
		List<YntCptransmbHVO> list = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ynt_tdtransmb  where nvl(dr,0)=0 and pk_trade_accountschema in (select corptype from bd_corp where pk_corp= ? and nvl(dr,0)=0 )");
		sb.append(" and pk_corp =? ");
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(IGlobalConstants.DefaultGroup);
//		sb.append("select * from ynt_tdtransmb  where NVL(PK_CORP,'000001') = '"+vo.getPk_corp()+"' ");
		List<BdTradeTranstemplateHVO> listVo = (List<BdTradeTranstemplateHVO>) singleObjectBO.executeQuery(sb.toString(), params, new BeanListProcessor(BdTradeTranstemplateHVO.class));

		if (listVo == null || listVo.size() == 0)
			return list;
		listVo = completinfo(listVo, pk_corp);
		
		for(BdTradeTranstemplateHVO  tvo:listVo){
			YntCptransmbHVO  hvo = new YntCptransmbHVO();
			hvo.setPk_corp(IDefaultValue.DefaultGroup);
			hvo.setAccountcode(tvo.getAccountcode());
			hvo.setAccountname(tvo.getAccname());
			hvo.setAbstracts(tvo.getAbstracts());
			hvo.setPk_corp_transtemplate_h(tvo.getPk_trade_transtemplate_h());
			hvo.setMemo("系统预置，不允许修改删除");
			list.add(hvo);
		}
		return list;
	}
	
	private List<BdTradeTranstemplateHVO> completinfo(List<BdTradeTranstemplateHVO>  listVo,String pk_corp){
		Map<String, BdTradeAccountVO> accmap = new HashMap<String, BdTradeAccountVO>();
		accmap = queryMap(BdTradeAccountVO.class,IGlobalConstants.DefaultGroup);
		BdTradeAccountVO accvo;
		for(int i=0;i<listVo.size();i++){
			accvo = accmap.get(listVo.get(i).getAttributeValue("pk_transferinaccount"));
			if(accvo!=null)
				listVo.get(i).setAttributeValue("accname", accvo.getAccountname());
		}
		return listVo;
		
	}
	private <T> Map<String,T> queryMap(Class className,String pk_corp) throws DZFWarpException{
		
		Map<String,T> rsmap = new HashMap<String,T>();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<T> listVo = (List<T>) getSingleObjectBO().retrieveByClause(className, "pk_corp=?", sp);

		if(listVo != null && listVo.size() > 0){
			for(T pvo : listVo){
				rsmap.put(((SuperVO)pvo).getPrimaryKey(), pvo);
			}
		}
		return rsmap;
	}
	
	@Override
	public void delete(YntCptransmbHVO vo)  throws DZFWarpException{
		singleObjectBO.deleteObjectByID(vo.getPrimaryKey(), new Class[]{YntCptransmbHVO.class,YntCptransmbBVO.class});
	}
	
	public YntCptransmbHVO queryById(String id) throws DZFWarpException {
		return (YntCptransmbHVO)singleObjectBO.queryVOByID(id, YntCptransmbHVO.class);
	}
	
	
	public void update(YntCptransmbHVO vo) {
		singleObjectBO.update(vo);
	}

	public ICpaccountService getCpaccountService() {
		return cpaccountService;
	}
	@Autowired
	public void setCpaccountService(ICpaccountService cpaccountService) {
		this.cpaccountService = cpaccountService;
	}
	public void exist(String pk_corp,String pk_km,String pk_id,HashSet<String> bodyvos){
		String sql = "  pk_transferinaccount=? and pk_corp=? and pk_corp_transtemplate_h<>? and nvl(dr,0)=0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_km);
		sp.addParam(pk_corp);
		if(StringUtil.isEmpty(pk_id)){
			sp.addParam(" ");
		}else{
			sp.addParam(pk_id);
		}
		List<YntCptransmbHVO> hvos= (List<YntCptransmbHVO>) singleObjectBO.executeQuery(sql, sp, new Class[]{ YntCptransmbHVO.class});
		
		List<HashSet<String>> bodylist=new ArrayList<>();
		HashSet<String> aclist = null;
		List<YntCptransmbBVO> bvos = null;
		for(YntCptransmbHVO hvo:hvos){
			bvos = null;
			bvos=queryChildsById(hvo.getPk_corp_transtemplate_h(),pk_corp);
			aclist = null;
			aclist=new HashSet<String>();
			for(YntCptransmbBVO bvo: bvos){
				aclist.add(bvo.getPk_transferoutaccount());
			}
			bodylist.add(aclist);
		}
		if(bodylist.contains(bodyvos)){
			throw new BusinessException("该模版已经存在！");
		}
		
	}

	@Override
	public List<YntCptransmbBVO> queryChildsById(String id,String pk_corp) {
		
		List<YntCptransmbBVO> list = null;
		if(IGlobalConstants.DefaultGroup.equalsIgnoreCase(pk_corp)){
			list=	queryAssBodyTemplateVOs(id);
		}else{
			SQLParameter sp = new SQLParameter();
			sp.addParam(id);
			StringBuffer sf = new StringBuffer();
			sf.append(" select b.*, acc.accountcode||'_'|| acc.accountname accountname,acc.accountcode from ynt_cptransmb_b b ");
			sf.append(" join  ynt_cpaccount acc on acc.pk_corp_account = b.pk_transferoutaccount ");
			sf.append(" where b.pk_corp_transtemplate_h = ? and nvl(b.dr,0) = 0 ");
			list = (List<YntCptransmbBVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(YntCptransmbBVO.class));
			if(list == null || list.size() == 0)
				return null;
		}
		
		return list;
	}
	
	public List<YntCptransmbBVO> queryAssBodyTemplateVOs(String hid) throws DZFWarpException {
		List<YntCptransmbBVO> list = new ArrayList<>();
		SQLParameter sp = new SQLParameter();
		sp.addParam(hid);
		StringBuffer sf = new StringBuffer();
		sf.append(" select bb.* , aa.accountcode acccode,aa.accountname accname from ynt_tdtransmb_b  bb ");
		sf.append(" left join ynt_tdacc  aa on bb.pk_transferoutaccount = aa.pk_trade_account ");
		sf.append(" where bb.pk_trade_transtemplate_h = ? and nvl(bb.dr,0) = 0  ");
		List<BdTradeTranStemPlateBVO> hylist = (List<BdTradeTranStemPlateBVO>)
				getSingleObjectBO().executeQuery(sf.toString(), sp, new BeanListProcessor(BdTradeTranStemPlateBVO.class));
		
		if(hylist == null || hylist.size()==0)
			return list;
		
		for (BdTradeTranStemPlateBVO vo : hylist) {
			YntCptransmbBVO bvo = new YntCptransmbBVO();
			bvo.setPk_corp(IGlobalConstants.DefaultGroup);
			bvo.setAbstracts(vo.getAbstracts());
			bvo.setDirect(vo.getDirection());
			bvo.setDirection(vo.getDirection());
			bvo.setAccountcode(vo.getAccountcode());
			bvo.setAccountname(vo.getAccname());
			list.add(bvo);
		}
		
		return list;
	}
}
