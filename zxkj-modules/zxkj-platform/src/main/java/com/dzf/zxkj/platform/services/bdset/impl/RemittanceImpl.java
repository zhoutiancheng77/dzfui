package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.services.bdset.IRemittanceService;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("gl_remitserv")
public class RemittanceImpl implements IRemittanceService {
	
	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	@Autowired
	private IUserService userServiceImpl;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;

	@Override
	public RemittanceVO save(RemittanceVO vo) throws DZFWarpException {
		if(vo.getPk_remittance() != null && !"".equals(vo.getPk_remittance())){
			update(vo);
			return vo;
		}
		return (RemittanceVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public List<RemittanceVO> query(String pk_corp) throws DZFWarpException{
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" select rm.pk_remittance,rm.pk_corp,rm.pk_corp_account,rm.accountcode,rm.pk_out_account,rm.outatcode,rm.memo,rm.coperatorid,rm.doperatedate ")
		.append(" from ynt_remittance rm ")
		.append(" where  rm.pk_corp = ? and nvl(rm.dr,0) = 0 ");
		List<RemittanceVO> qryVOs = (List<RemittanceVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(RemittanceVO.class));
		int len=qryVOs==null?0:qryVOs.size();
		Map<String, YntCpaccountVO> map=null;
		UserVO uvo=null;
		if(len>0) map=accountService.queryMapByPk(pk_corp);
		else qryVOs=null;
		YntCpaccountVO vo=null;
		RemittanceVO rvo=null;
		for (int i = 0; i < len; i++) {
			rvo=qryVOs.get(i);
			vo=map.get(rvo.getPk_corp_account());
			if(vo!=null)
				rvo.setPk_corp_account_name(vo.getAccountname());
			vo=map.get(rvo.getPk_out_account());
			if(vo!=null)
				rvo.setPk_out_account_name(vo.getAccountname());
			if(StringUtil.isEmptyWithTrim(rvo.getCoperatorid())==false){
			uvo=userServiceImpl.queryUserJmVOByID(rvo.getCoperatorid());
			rvo.setCoperatorname(uvo.getUser_name());
			}
		}

		return qryVOs;
	}

	@Override
	public void delete(RemittanceVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}

	private void update(RemittanceVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);
	}
	public RemittanceVO queryById(String id) throws DZFWarpException{
		RemittanceVO vo=null;
		vo=(RemittanceVO)singleObjectBO.queryVOByID(id, RemittanceVO.class);
		return vo;
	}
}
