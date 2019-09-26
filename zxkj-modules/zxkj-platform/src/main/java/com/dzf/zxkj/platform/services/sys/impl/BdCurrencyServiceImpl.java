package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.services.sys.IBDCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 币种
 * 
 */
@Service("sys_currentserv")
public class BdCurrencyServiceImpl implements IBDCurrencyService {

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 
	 */
	public BdCurrencyVO[] queryCurrency() throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String condition = " pk_corp = ? and nvl(dr,0) = 0 order by currencycode  ";
		BdCurrencyVO[] vos= (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, condition, sp);
		if(vos == null || vos.length == 0)
			return null;
		return vos;
	}


	@Override
	public void save(BdCurrencyVO vo) throws DZFWarpException {
		//保存校验
		existCheck(vo);
		if(!StringUtil.isEmpty(vo.getPk_currency()))
			update(vo);
		else
			insert(vo);
	}


	@Override
	public void delete(BdCurrencyVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
		
	}
	
	public void insert(BdCurrencyVO vo) throws DZFWarpException {
		singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}
	
	public void update(BdCurrencyVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);
	}

	//校验
	private void existCheck(BdCurrencyVO vo) throws DZFWarpException {
		SQLParameter sp1 = new SQLParameter();
		String code = vo.getCurrencycode();
		String name = vo.getCurrencyname();
		String id = vo.getPk_currency();
		sp1.addParam(code);	
		sp1.addParam(name);
		String condition1 = " (currencycode = ? or currencyname= ? ) ";
		if(!StringUtil.isEmpty(id)){
			sp1.addParam(id);
			condition1=condition1+" and  pk_currency<> ?  ";
		}
		sp1.addParam(IDefaultValue.DefaultGroup);
		condition1=condition1+" and pk_corp = ? and nvl(dr,0) = 0 ";
		List<BdCurrencyVO> vo1 = (List<BdCurrencyVO>) singleObjectBO.retrieveByClause(BdCurrencyVO.class, condition1, sp1);
		int len=vo1==null?0:vo1.size();
		if(len>0){
			String str=vo1.get(0).getCurrencycode();
			if(code.equals(str))
				throw new BusinessException("币种编号已存在");
			else throw new BusinessException("币种名称已存在");
		}
	}


	@Override
	public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_currency))
			return null;
		BdCurrencyVO vo = (BdCurrencyVO)singleObjectBO.queryByPrimaryKey(BdCurrencyVO.class, pk_currency);
		return vo;
	}

}
