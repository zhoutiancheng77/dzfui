package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 凭证生成库存数据
 *
 */
public class CreateicBill extends CreatePub{
	
	private ICbComconstant gl_cbconstant;
	
	public CreateicBill(ICbComconstant gl_cbconstant, SingleObjectBO singleobjectbo) {
		super(singleobjectbo);
		this.gl_cbconstant = gl_cbconstant;
	}


	public void deleteBill(TzpzHVO hvo)throws BusinessException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPrimaryKey());
//		if(in){
			String sql1 = " delete from ynt_ictradein where pk_voucher =?  ";
			getSingleobjectbo().executeUpdate(sql1, sp);
//		}
//		if(out){
			String sql2 = " delete from ynt_ictradeout where pk_voucher =? ";
			getSingleobjectbo().executeUpdate(sql2, sp);
//		}

	}
	
	
	public  String getInWhereClause1(List<IctradeinVO> list){
		if(list == null || list.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" in (");
		for(int i = 0 ; i < list.size() ; i++){
			if(i == 0 ){
				sf.append(" '"+list.get(i).getPk_voucher_b()+"'");
			}else{
				sf.append(",'"+list.get(i).getPk_voucher_b()+"'");
			}
		}
		sf.append(")");
		return sf.toString();
	}
	
	public  String getInWhereClause2(List<IntradeoutVO> list){
		if(list == null || list.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" in (");
		for(int i = 0 ; i < list.size() ; i++){
			if(i == 0 ){
				sf.append(" '"+list.get(i).getPk_voucher_b()+"'");
			}else{
				sf.append(",'"+list.get(i).getPk_voucher_b()+"'");
			}
		}
		sf.append(")");
		return sf.toString();
	}
	
	/**
	 * 由凭证生成库存单据--------老模式
	 * */
	public void saveOldIcBill(TzpzHVO hvo)throws BusinessException {
		TzpzBVO[] bodyvos = (TzpzBVO[])hvo.getChildren();
		if(bodyvos == null || bodyvos.length == 0)
			return;
		List<IctradeinVO> inlist = new ArrayList<IctradeinVO>();
		List<IntradeoutVO> outlist = new ArrayList<IntradeoutVO>();
		for(TzpzBVO v : bodyvos){
			YntCpaccountVO vo = queryAccountVO(v.getPk_accsubj());
			if(gl_cbconstant.getKcsp_code().equals(vo.getAccountcode())
					|| gl_cbconstant.getYcl_code().equals(vo.getAccountcode())){//库存商品//原材料
				if(v.getPk_inventory() != null 
						&& vo.getIsnum() !=null 
						&& vo.getIsnum().booleanValue() 
						&& v.getNnumber() != null
						&& v.getNnumber().doubleValue() != 0){
					if(v.getVdirect() == 0){//借方//入库
						IctradeinVO invo = createinvo(v,hvo);
						inlist.add(invo);
					}else if(v.getVdirect() == 1){//贷方//出库
						IntradeoutVO outvo = createoutvo(v,hvo);
						outlist.add(outvo);
					}
				}
			}
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPrimaryKey());
		//先删除
		deleteBill(hvo);
		//后插入
		if(inlist.size()>0){
			getSingleobjectbo().insertVOArr(hvo.getPk_corp(), inlist.toArray(new IctradeinVO[0]));
		}
		if(outlist.size() >0){
			getSingleobjectbo().insertVOArr(hvo.getPk_corp(), outlist.toArray(new IntradeoutVO[0]));
		}
	}
	
	public IctradeinVO createinvo(TzpzBVO bodyvo,TzpzHVO hvo){
		if(bodyvo == null || hvo == null)
			return null;
		IctradeinVO invo = new IctradeinVO();
		invo.setPk_corp(hvo.getPk_corp());
		invo.setPk_currency(bodyvo.getPk_currency());
		invo.setNnum(bodyvo.getNnumber());//数量
		invo.setNymny(bodyvo.getYbjfmny());//原币
		invo.setNcost(bodyvo.getJfmny());//借方
		invo.setPk_inventory(bodyvo.getPk_inventory());
		invo.setPk_voucher(hvo.getPk_tzpz_h());
		invo.setPk_voucher_b(bodyvo.getPk_tzpz_b());
		invo.setDbilldate(hvo.getDoperatedate());
		invo.setDr(0);
		invo.setPk_subject(bodyvo.getPk_accsubj());
		invo.setZy(bodyvo.getZy());
		invo.setPzh(hvo.getPzh());
		return invo;
	}
	
	public IntradeoutVO createoutvo(TzpzBVO bodyvo,TzpzHVO hvo){
		if(bodyvo == null || hvo == null)
			return null;
		IntradeoutVO outvo = new IntradeoutVO();
		outvo.setPk_corp(hvo.getPk_corp());
		outvo.setPk_currency(bodyvo.getPk_currency());
		outvo.setNnum(bodyvo.getNnumber());//数量
		outvo.setNymny(bodyvo.getYbdfmny());//原币
		outvo.setNcost(bodyvo.getDfmny());//贷方
		outvo.setPk_inventory(bodyvo.getPk_inventory());
		outvo.setPk_voucher(hvo.getPk_tzpz_h());
		outvo.setPk_voucher_b(bodyvo.getPk_tzpz_b());
		outvo.setDbilldate(hvo.getDoperatedate());
		outvo.setDr(0);
		outvo.setPk_subject(bodyvo.getPk_accsubj());
		outvo.setZy(bodyvo.getZy());
		outvo.setPzh(hvo.getPzh());
		return outvo;
	}
}