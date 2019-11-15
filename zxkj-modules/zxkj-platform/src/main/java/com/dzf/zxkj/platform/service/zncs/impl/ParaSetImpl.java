package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.zncs.ParaSetVO;
import com.dzf.zxkj.platform.service.zncs.IParaSet;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParaSetImpl implements IParaSet {

	@Autowired
	SingleObjectBO singleObjectBO ;
	@Override
	public List<ParaSetVO> queryParaSet(String pk_corp) throws DZFWarpException {
		String sql="select * from ynt_para_set where nvl(dr,0)=0 and pk_corp=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<ParaSetVO> list=(List<ParaSetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(ParaSetVO.class));
		if(list!=null&&list.size()>0){
			return list;
		}else{
			List<ParaSetVO> l=new ArrayList<ParaSetVO>();
			l.add(insertParaSetVO(pk_corp));
			return l;
		}
	}

	private ParaSetVO insertParaSetVO(String pk_corp)throws DZFWarpException {
		ParaSetVO paraVO=new ParaSetVO();
		paraVO.setPk_corp(pk_corp);
		paraVO.setDr(0);
		paraVO.setIncomeclass(ZncsConst.SRFL_0);
		paraVO.setBankbillbyacc(DZFBoolean.FALSE);
		paraVO.setInvidentify(DZFBoolean.TRUE);
		paraVO.setVoucherqfzpp(DZFBoolean.FALSE);
		paraVO.setVoucherdate(ZncsConst.PZRQ_1);
		paraVO.setErrorvoucher(DZFBoolean.FALSE);
		paraVO.setOrderdetail(1);
		paraVO.setIsmergedetail(DZFBoolean.TRUE);
		paraVO.setMergebillnum(2);
		paraVO.setIsmergeincome(DZFBoolean.FALSE);
		paraVO.setIsmergeic(DZFBoolean.FALSE);
		paraVO.setIsmergebank(DZFBoolean.FALSE);
		paraVO.setPurchclass(DZFBoolean.FALSE);
		paraVO.setCostclass(DZFBoolean.FALSE);
		paraVO.setBankinoutclass(DZFBoolean.FALSE);
		paraVO.setNcpsl("0%");
		paraVO=(ParaSetVO)singleObjectBO.insertVO(pk_corp, paraVO);
		return paraVO;
	}

	@Override
	public void saveParaSet(String pk_corp,String pType, String pValue) throws DZFWarpException {
		String sql="select * from ynt_para_set where nvl(dr,0)=0 and pk_corp=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<ParaSetVO> list=(List<ParaSetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(ParaSetVO.class));
		ParaSetVO paraVO=null;
		if(list!=null&&list.size()>0){
			paraVO=list.get(0);
		}else{
			paraVO=insertParaSetVO(pk_corp);
		}
		if(pType.equals("srfl")){
			paraVO.setIncomeclass(Integer.parseInt(pValue));
		}else if(pType.equals("isyh")){
			paraVO.setBankbillbyacc(new DZFBoolean(pValue));
		}else if(pType.equals("isrz")){
			paraVO.setInvidentify(new DZFBoolean(pValue));
		}else if(pType.equals("iszpb")){
			paraVO.setVoucherqfzpp(new DZFBoolean(pValue));
		}else if(pType.equals("pzrq")){
			paraVO.setVoucherdate(Integer.parseInt(pValue));
		}else if(pType.equals("iscwvhr")){
			paraVO.setErrorvoucher(new DZFBoolean(pValue));
		}else if(pType.equals("ishbfl")){
			paraVO.setIsmergedetail(new DZFBoolean(pValue));
		}else if(pType.equals("flpx")){
			paraVO.setOrderdetail(Integer.parseInt(pValue));
		}else if(pType.equals("pjhbsl")){
			if(Integer.parseInt(pValue)<1||Integer.parseInt(pValue)>20){
				throw new BusinessException("票据合并数量只能设置1到20之间的数字。");
			}
			paraVO.setMergebillnum(Integer.parseInt(pValue));
		}else if(pType.equals("ishbsr")){
			paraVO.setIsmergeincome(new DZFBoolean(pValue));
		}else if(pType.equals("ishbkc")){
			paraVO.setIsmergeic(new DZFBoolean(pValue));
		}else if(pType.equals("ishbyh")){
			paraVO.setIsmergebank(new DZFBoolean(pValue));
		}else if(pType.equals("cgfz")){
			paraVO.setPurchclass(new DZFBoolean(pValue));
		}else if(pType.equals("cbfz")){
			paraVO.setCostclass(new DZFBoolean(pValue));
		}else if(pType.equals("yhhb")){
			paraVO.setBankinoutclass(new DZFBoolean(pValue));
		}else if(pType.equals("ncpsl")){
			paraVO.setNcpsl(pValue);
		}
		singleObjectBO.update(paraVO);
	}
}
