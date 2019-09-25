package com.dzf.zxkj.platform.services.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.InvCurentVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.util.Kmschema;

import java.util.ArrayList;
import java.util.List;

/**
 * 凭证生成销售数据
 *
 */
public class CreateSaleBill extends CreatePub{
	
	private ICbComconstant gl_cbconstant;
	
	public CreateSaleBill(ICbComconstant gl_cbconstant, SingleObjectBO singleobjectbo) {
		super(singleobjectbo);
		this.gl_cbconstant = gl_cbconstant;
	}

	public void deleteBill(TzpzHVO hvo)throws BusinessException {
		TzpzBVO[] bodyvos = (TzpzBVO[])hvo.getChildren();
		if(bodyvos == null || bodyvos.length == 0)
			return;
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPrimaryKey());
		String sql1 = " delete from ynt_subinvtory where pk_tzpz_h =?  ";
		getSingleobjectbo().executeUpdate(sql1, sp);
	}
	
	public  String getInWhereClause(List<InvCurentVO> list){
		if(list == null || list.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" in (");
		for(int i = 0 ; i < list.size() ; i++){
			if(i == 0 ){
				sf.append(" '"+list.get(i).getPk_tzpz_b()+"'");
			}else{
				sf.append(",'"+list.get(i).getPk_tzpz_b()+"'");
			}
		}
		sf.append(")");
		return sf.toString();
	}
	
	/**
	 * 由凭证生成销售单据，，，有可能买卖单据都在这里面。
	 */
	public void saveOldicSaleBill(CorpVO corpvo, TzpzHVO hvo)throws BusinessException {
		TzpzBVO[] bodyvos = (TzpzBVO[])hvo.getChildren();
		if(bodyvos == null || bodyvos.length == 0)
			return;
		List<InvCurentVO> inlist = new ArrayList<InvCurentVO>();
		for(TzpzBVO v : bodyvos){
			YntCpaccountVO vo = queryAccountVO(v.getPk_accsubj());
			if(Kmschema.isshouru(corpvo.getCorptype(), vo.getAccountcode())//收入类科目
					&& vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
					&& v.getVdirect() ==1
					&& !StringUtil.isEmpty(v.getPk_inventory())
					&& v.getNnumber() != null
					&& v.getNnumber().doubleValue() != 0
					){
					InvCurentVO in = createInventVO(hvo,v);
					inlist.add(in);
			}
		}
		if(inlist.size() > 0){
			String sql = " delete from ynt_subinvtory where pk_tzpz_h = ?  ";
			SQLParameter sp = new SQLParameter();
			sp.addParam(hvo.getPrimaryKey());
//			String sql = " delete from ynt_subinvtory where pk_tzpz_b  "+getInWhereClause(inlist);
			getSingleobjectbo().executeUpdate(sql, sp);
			getSingleobjectbo().insertVOArr(hvo.getPk_corp(), inlist.toArray(new InvCurentVO[0]));
		}
	}
	
	public InvCurentVO createInventVO(TzpzHVO hvo,TzpzBVO body){
		InvCurentVO env = new InvCurentVO();
		env.setPk_tzpz_b(body.getPk_tzpz_b());
		env.setPk_corp(hvo.getPk_corp());
		env.setPk_tzpz_h(hvo.getPk_tzpz_h());
		env.setPk_accsubj(body.getPk_accsubj());
		env.setPk_inventory(body.getPk_inventory());
		env.setAccountcode(body.getVcode());
		env.setAccountname(body.getVname());
		env.setInvname(null);
		env.setNnumber(body.getNnumber());
		env.setDr(0);
		return env;
	}
}