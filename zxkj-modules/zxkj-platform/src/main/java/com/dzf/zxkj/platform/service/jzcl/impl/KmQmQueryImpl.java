package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.service.jzcl.IKmQmQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * 科目期末查询处理器类
 * 
 * @author zhangj
 * 
 */
@Service("gl_kmqmqueryserv")
@Slf4j
public class KmQmQueryImpl implements IKmQmQueryService {
	
	private SingleObjectBO singleObjectBO = null;


	@Override
	public Serializable save(Serializable vo) {
		return null;
	}

	@Override
	public List<KMQMJZVO> query(QueryParamVO queryParamvo) throws DZFWarpException {
		/** 查询开始截至日期 */
		DZFDate begindate = queryParamvo.getBegindate1();
		DZFDate enddate = queryParamvo.getEnddate();
		if(begindate==null || enddate ==null){
			throw new BusinessException("开始,截至日期不能为空!");
		}
		SQLParameter sp=new SQLParameter();
		StringBuffer qrySql = new StringBuffer();
		qrySql.append("	select a.period,b.accountcode, b.accountname,a.pk_kmqmjz, a.pk_corp, ");
		qrySql.append("   a.pk_accsubj, a.vdirect, a.thismonthqc, a.thismonthqm, a.ybthismonthqc,a.coperatorid,c.user_name as operatorid,  ");
		qrySql.append("   a.thismonthqm, a.ybthismonthqm,  a.ybjfmny, a.ybdfmny,a.jffse,a.dffse ");
		qrySql.append("  from ynt_kmqmjz a ");
		qrySql.append("  left join ynt_cpaccount b  on a.pk_accsubj = b.pk_corp_account ");
		qrySql.append("  left join sm_user c  on a.coperatorid = c.cuserid ");
		qrySql.append("   where nvl(a.dr, 0) = 0 ");
		qrySql.append("   and nvl(b.dr, 0) = 0 ");
		qrySql.append("  and a.pk_corp = '"+queryParamvo.getPk_corp()+"' ");
		qrySql.append("   and a.period >='"+ DateUtils.getPeriod(begindate)+"' and a.period<= '"+DateUtils.getPeriod(enddate)+"'");
		qrySql.append(" order by a.period, b.accountcode ");
	
		List<KMQMJZVO> listres = (List<KMQMJZVO>) singleObjectBO.executeQuery(qrySql.toString(), sp, new BeanListProcessor(KMQMJZVO.class));
		return listres;
	}

	@Override
	public void delete(Serializable vo)  throws DZFWarpException{
		
	}
	
	
	public SingleObjectBO getSingleObjectBO()  throws DZFWarpException{
		return singleObjectBO;
	}

	
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 更新科目信息
	 */
	@Override
	public KMQMJZVO updatekm(String pk_kmqmjz, String pk_accsubj) throws DZFWarpException {
		
		if(pk_kmqmjz==null || pk_kmqmjz.trim().length() ==0 || pk_accsubj==null || pk_accsubj.trim().length()==0){
			throw new BusinessException("更新失败，当前数据为空!");
		}
		
		/** 先查询，再更新 */
		KMQMJZVO qmjzvo = (KMQMJZVO) singleObjectBO.queryByPrimaryKey(KMQMJZVO.class, pk_kmqmjz);
		
		if(qmjzvo==null){
			throw new BusinessException("更新失败，当前数据为空!");
		}
		
		YntCpaccountVO accountvo  = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, pk_accsubj);
		
		if(accountvo == null){
			throw new BusinessException("更新失败，当前数据为空!");
		}
		
		qmjzvo.setPk_accsubj(pk_accsubj);
		
		singleObjectBO.update(qmjzvo, new String[]{"pk_accsubj"});
		
		return qmjzvo;
	}


}
