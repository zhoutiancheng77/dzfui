package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DAOException;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.services.bdset.IPzmbbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("pzmbbService")
public class PzmbbServiceImpl implements IPzmbbService {

	private SingleObjectBO singleObjectBO = null;
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	@Override
	public PzmbbVO save(PzmbbVO vo) throws BusinessException {
		return (PzmbbVO)singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public void update(PzmbbVO vo) {
		try {
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		} catch (BusinessException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public List<PzmbbVO> query() {
		return null;
	}

	@Override
	
	public List<PzmbbVO> queryByPId(String PId) {
		List<PzmbbVO> listVo = new ArrayList<PzmbbVO>();
		try{
		SQLParameter sp=new SQLParameter();
		sp.addParam(PId);
		//sp.addParam(pk_corp);
		listVo = (List<PzmbbVO>)singleObjectBO.retrieveByClause(PzmbbVO.class, " pk_corp_pztemplate_h=?", sp);
		}catch(DAOException e){
			throw new DAOException(e);
		}
		return listVo;
	}

	@Override
	public void delete(PzmbbVO vo)  throws BusinessException {
		singleObjectBO.deleteObject(vo);
	}
	

}
