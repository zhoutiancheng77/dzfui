package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BDabstractsVO;
import com.dzf.zxkj.platform.services.sys.IBDabstractsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gl_bdabstracts")
public class BDabstractsServiceImpl implements IBDabstractsService {
	
	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public void delete(BDabstractsVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}
	
	@Override
	public BDabstractsVO[] query(String pk_corp) throws DZFWarpException {
		BDabstractsVO[] vos = queryParent(pk_corp);//查询上级带本级的摘要
		if(vos == null || vos.length == 0)
			return new BDabstractsVO[0];
		
		return vos;
	}

	@Override
	public BDabstractsVO queryByID(String pk_abstracts)
			throws DZFWarpException {
		return (BDabstractsVO) singleObjectBO.queryVOByID(pk_abstracts, BDabstractsVO.class);
	}

	@Override
	public void existCheck(BDabstractsVO vo) throws DZFWarpException {
		SQLParameter sp1 = new SQLParameter();
		String code = vo.getAbstractscode();
		String name = vo.getAbstractsname();
		String id = "";
		if(!StringUtil.isEmpty(vo.getPk_abstracts())){
			id = vo.getPk_abstracts();
		}
		sp1.addParam(code);	
		sp1.addParam(name);
		if(!StringUtil.isEmpty(id)){
			sp1.addParam(id);
			sp1.addParam(id);
		}
		sp1.addParam(vo.getPk_corp());
		sp1.addParam(vo.getPk_corp());
		String condition1 = " (abstractscode = ? or abstractsname = ?) ";
		if(StringUtil.isEmpty(id)==false)
			condition1=condition1		+ " and (pk_abstracts>? or pk_abstracts<?) ";
		condition1=condition1+" and pk_corp  in ((select fathercorp from bd_corp where pk_corp = ? ), ? ) and nvl(dr,0) = 0 ";
		List<BDabstractsVO> vo1 = (List<BDabstractsVO>) singleObjectBO.retrieveByClause(BDabstractsVO.class, condition1, sp1);
		int len=vo1==null?0:vo1.size();
		if(len>0){
			String str=vo1.get(0).getAbstractscode();
			if(code.equals(str))
				throw new BusinessException("摘要编号已存在");
			else throw new BusinessException("摘要名称已存在");
		}
	}

	@Override
	public BDabstractsVO save(BDabstractsVO vo) throws DZFWarpException {
		if(!StringUtil.isEmpty(vo.getPk_abstracts())){
			update(vo);
			return vo;
		}
		return (BDabstractsVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}
	
	public void update(BDabstractsVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);
	}

	@Override
	public BDabstractsVO[] queryParent(String pk_corp) throws DZFWarpException {
		/*String corpwhere = queryParentPk(pk_corp);  //先查询当前公司对应的上级公司id  包含当前公司
		//在查询这个几个公司的全部信息
		String condition = " pk_corp  in ("+corpwhere.substring(0,corpwhere.length()-1)+") and nvl(dr,0) = 0 order by abstractscode ";
		BDabstractsVO[] vos = (BDabstractsVO[]) singleObjectBO.queryByCondition(BDabstractsVO.class, condition, null);
		*/
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		String condition = " pk_corp  in ((select fathercorp from bd_corp where pk_corp = ? ), ? ) and nvl(dr,0) = 0 order by abstractscode ";
		BDabstractsVO[] vos = (BDabstractsVO[]) singleObjectBO.queryByCondition(BDabstractsVO.class, condition, sp);//修改只查询父级和本身
		if(vos == null || vos.length == 0)
			return null;
		
		return vos;
	}
	public String queryParentPk(String pk_corp) {
		//先查询当前公司对应的上级公司信息
				StringBuffer corpsql = new StringBuffer();
				corpsql.append(" SELECT pk_corp FROM BD_CORP  ");
				corpsql.append("  START WITH PK_CORP = '"+pk_corp+"' ");
				corpsql.append("CONNECT BY  PRIOR FATHERCORP =  PK_CORP ");
				List<String> corplist =  (List<String>) singleObjectBO.executeQuery(corpsql.toString(), new SQLParameter(), new ColumnListProcessor());
				StringBuffer corpwhere = new StringBuffer();
				//不包括当前公司的wherepart
				for(String str:corplist){
//					if(str.equals(pk_corp)){
//						continue;
//					}
					corpwhere.append("'"+str+"',");
				}
				return corpwhere.toString();
	}

}
