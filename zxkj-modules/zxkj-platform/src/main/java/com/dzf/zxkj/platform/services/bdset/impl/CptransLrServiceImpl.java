package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCptranslrHVO;
import com.dzf.zxkj.platform.model.sys.BdTradeLrBVO;
import com.dzf.zxkj.platform.model.sys.BdTradeLrHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.ICptransLrService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("gl_cplrmbserv")
public class CptransLrServiceImpl implements ICptransLrService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	@Autowired
	private IAccountService accountService;

	@Autowired
	private ICorpService corpService;

	@Override
	public YntCptranslrHVO save(YntCptranslrHVO vo) throws DZFWarpException {
		YntCpaccountVO incvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, vo.getPk_transferinaccount());
		YntCpaccountVO outcvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, vo.getPk_transferoutaccount());
		vo.setAccountname(incvo.getAccountname());
		vo.setAccountcode(incvo.getAccountcode());
		//vo.setDirect(incvo.getDirection());
		vo.setVname(outcvo.getAccountname());
		vo.setVcode(outcvo.getAccountcode());
		//vo.setDirection(outcvo.getDirection());
		YntCptranslrHVO tvo =new YntCptranslrHVO();
		BeanUtils.copyProperties(vo, tvo);
		singleObjectBO.saveObject(vo.getPk_corp(), tvo);
		return tvo;
	}
	@Override
	public List<YntCptranslrHVO> query(String pk_corp, boolean isgroup) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select a.* from   ynt_cptranslr a ");
		sf.append(" where a.pk_corp = ? and nvl(a.dr,0) = 0 ");
		List<YntCptranslrHVO> hlist =(List<YntCptranslrHVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(YntCptranslrHVO.class));
		
		if(!isgroup){
			return hlist;
		}
		if(hlist == null || hlist.size()==0){
			BdTradeLrHVO[] hvos =getLrHvo(pk_corp);
			if(hvos !=null &&hvos.length>0){
				hlist = new ArrayList<>();
				BdTradeLrHVO hvo=hvos[0];
				
				YntCptranslrHVO vo = new YntCptranslrHVO();
				Map<String,YntCpaccountVO> map =accountService.queryMapByPk(pk_corp);
				//根据行业会计科目主键找到公司会计科目主键						
				String jfkm = yntBoPubUtil.getCorpAccountByTradeAccountPk(hvo.getPk_transferinaccount(), pk_corp);
				YntCpaccountVO incvo = map.get(jfkm);
				vo.setPk_transferinaccount(incvo.getPrimaryKey());
				vo.setAbstracts(hvo.getAbstracts());
				vo.setIndirect(incvo.getDirection());
				vo.setAccountcode(incvo.getAccountcode());
				vo.setAccountname(incvo.getAccountname());
				vo.setPk_corp(IGlobalConstants.DefaultGroup);
				vo.setPk_corp_translr_h(hvo.getPk_trade_transtemplate_h());
				BdTradeLrBVO[] bvos=getLrbvo(hvo);
				if(bvos !=null && bvos.length >0){
					BdTradeLrBVO bvo=bvos[0];
					String dfkm =yntBoPubUtil.getCorpAccountByTradeAccountPk(bvo.getPk_transferoutaccount(), pk_corp);
					YntCpaccountVO incvo1 = map.get(dfkm);
					vo.setPk_transferoutaccount(dfkm);
					vo.setAbstracts1(bvo.getAbstracts());
					vo.setIndirect(incvo1.getDirection());
					vo.setVcode(incvo1.getAccountcode());
					vo.setVname(incvo1.getAccountname());
				}
				hlist.add(vo);
			}
		}
		return hlist;
	}

		private BdTradeLrHVO[] getLrHvo(String pk_corp){
			SQLParameter sp = new SQLParameter();
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			if(corpvo != null)
			sp.addParam(corpvo.getCorptype());
			sp.addParam(IGlobalConstants.DefaultGroup);
			//String sql=" select * from Ynt_TDLRMB where PK_TRADE_ACCOUNTSCHEMA = ? and nvl(bb.dr,0) = 0 ";
			String sql=" PK_TRADE_ACCOUNTSCHEMA = ? and nvl(dr,0) = 0 and pk_corp = ? ";
			BdTradeLrHVO[] hvo =(BdTradeLrHVO[]) singleObjectBO.queryByCondition(BdTradeLrHVO.class, sql, sp);
			return hvo;
		}
		private BdTradeLrBVO[] getLrbvo(BdTradeLrHVO hvo){
			SQLParameter sp = new SQLParameter();
			sp.addParam(hvo.getPrimaryKey());
			sp.addParam(IGlobalConstants.DefaultGroup);
			String sql=" PK_TRADE_TRANSTEMPLATE_H = ? and nvl(dr,0) = 0 and pk_corp = ? ";
			BdTradeLrBVO[] bvo =(BdTradeLrBVO[]) singleObjectBO.queryByCondition(BdTradeLrBVO.class, sql, sp);
			return bvo;
		}
	@Override
	public void delete(YntCptranslrHVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}

	@Override
	public YntCptranslrHVO queryById(String id) throws DZFWarpException {
		YntCptranslrHVO vo=(YntCptranslrHVO) singleObjectBO.queryByPrimaryKey(YntCptranslrHVO.class, id);
		return vo;
	}

}
