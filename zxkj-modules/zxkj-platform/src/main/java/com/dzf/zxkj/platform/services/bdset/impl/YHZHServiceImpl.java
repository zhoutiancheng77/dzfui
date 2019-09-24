package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.services.bdset.IYHZHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gl_yhzhserv")
public class YHZHServiceImpl implements IYHZHService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public BankAccountVO save(BankAccountVO vo) throws DZFWarpException {
		checkExist(vo);
		
		BankAccountVO bvo = (BankAccountVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
		return bvo;
	}

	@Override
	public void update(BankAccountVO vo, String[] fields) throws DZFWarpException {
		checkExist(vo);
		singleObjectBO.update(vo, fields);

	}
	
	private void checkExist(BankAccountVO vo) throws DZFWarpException{
		if(StringUtil.isEmpty(vo.getBankcode())
				&& StringUtil.isEmpty(vo.getBankname()))
			return;
		
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		
		sf.append(" Select 1 ");
		sf.append("   From ynt_bankaccount y Where 1 = 1 ");
		if(!StringUtil.isEmpty(vo.getPrimaryKey())){
			sf.append(" and y.pk_bankaccount <> ? ");
			sp.addParam(vo.getPrimaryKey());
		}
		sf.append("    and pk_corp = ? ");
		sf.append("    and (y.bankcode = ? or y.bankaccount = ?) ");
		sf.append("    and nvl(y.dr, 0) = 0 ");
		
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getBankcode());
		sp.addParam(vo.getBankaccount());
		
		boolean b = singleObjectBO.isExists(vo.getPk_corp(), 
				sf.toString(), sp);
		
		if(b){
			throw new BusinessException("银行编码或银行账户已经存在");
		}
		
	}

	@Override
	public List<BankAccountVO> query(String pk_corp, String isnhsty) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.*,cp.accountcode,cp.accountname ");
		sf.append("   from ynt_bankaccount y ");
		sf.append("   left join ynt_cpaccount cp ");
		sf.append("     on y.relatedsubj = cp.pk_corp_account ");
		sf.append("  Where nvl(y.dr, 0) = 0 ");
//		sf.append("    and nvl(cp.dr, 0) = 0 ");
		
		if(!StringUtil.isEmpty(isnhsty)){
			sf.append(" and state != ? ");
			sp.addParam(IBillManageConstants.TINGY_STATUS);
		}
		
		sf.append("    and y.pk_corp = ? ");
		sf.append("    order by y.ts  ");
		sp.addParam(pk_corp);
		
		List<BankAccountVO> acclist = (List<BankAccountVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(BankAccountVO.class));
		
		return acclist;
	}

	@Override
	public BankAccountVO queryById(String id) throws DZFWarpException {
		BankAccountVO stvo = (BankAccountVO) singleObjectBO.queryVOByID(id, BankAccountVO.class);
		return stvo;
	}

	@Override
	public void delete(BankAccountVO vo) throws DZFWarpException {
		beforeDel(vo);
		
		singleObjectBO.deleteObject(vo);
	}
	
	private void beforeDel(BankAccountVO vo){
		if(StringUtil.isEmpty(vo.getPrimaryKey()))
			throw new BusinessException("该数据参数不完整,请检查");
		
		if(checkIsRef(vo)){
			throw new BusinessException("该银行账户已被银行对账单使用，不允许删除。");
		}
		
		BankAccountVO stvo = queryById(vo.getPrimaryKey());
		
		if(stvo == null)
			throw new BusinessException("该数据不存在或已删除，请检查");
		
		
	}
	/**
	 * 校验是否被引用
	 * @param vo
	 * @return
	 */
	private boolean checkIsRef(BankAccountVO vo){
		StringBuffer sf = new StringBuffer();
		sf.append(" Select 1 ");
		sf.append("   From ynt_bankstatement t ");
		sf.append("  Where nvl(t.dr, 0) = 0 ");
		sf.append("    and t.pk_corp = ? ");
		sf.append("    and t.pk_bankaccount = ? ");
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPrimaryKey());
		
		boolean b = singleObjectBO.isExists(vo.getPk_corp(), 
				sf.toString(), sp);
		
		return b;
	}

	@Override
	public BankAccountVO[] queryByCode(String code, String pk_corp) throws DZFWarpException {
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(code);
		sp.addParam(IBillManageConstants.TINGY_STATUS);
		
		BankAccountVO[] vos = (BankAccountVO[]) singleObjectBO.queryByCondition(BankAccountVO.class, 
				" nvl(dr,0) = 0 and pk_corp = ? and bankaccount = ? and state != ? ", sp);
		
		return vos;
	}

}
