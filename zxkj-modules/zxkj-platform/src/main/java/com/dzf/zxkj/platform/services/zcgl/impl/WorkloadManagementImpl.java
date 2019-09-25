package com.dzf.zxkj.platform.services.zcgl.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.platform.model.zcgl.WorkloadManagementVO;
import com.dzf.zxkj.platform.services.zcgl.IworkloadManagement;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("am_workloadManagement")
public class WorkloadManagementImpl implements IworkloadManagement {
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<WorkloadManagementVO> queryWorkloadManagement(
			QueryParamVO paramvo) throws DZFWarpException {

		StringBuilder sql = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		String period = DateUtils.getPeriod(paramvo.getBegindate1());
		sp.addParam(period + "-01");
		sp.addParam(period + "%");
		sql.append("select card.pk_assetcard,card.assetcode,card.assetname, ");
		sql.append(" card.zccode,card.gzzl,card.gzldw, ? as doperatedate, card.qcljgzl ");
		sql.append(" as syljgzl,card.depreciationdate as zjdate,  work.bygzl,work.sygzl,work.ljgzl, ");
		sql.append(" work.pk_workloadmanagement,card.accountdate, card.isperiodbegin ");
		sql.append(" from ynt_assetcard card ");
		sql.append(" left join ynt_workloadmanagement work on (work.pk_assetcard = card.pk_assetcard and work.doperatedate like ?) ");
		sql.append(" where card.zjtype = 1 and nvl(card.dr,0) = 0 and card.pk_corp = ? ");
		sp.addParam(paramvo.getPk_corp());
		if (paramvo.getBegindate1() != null) {
			sql.append(" and card.accountdate <= ? ");
			sp.addParam(DateUtils.getPeriodEndDate(DateUtils.getPeriod(paramvo.getBegindate1())));
		}
		if (paramvo.getPk_assetcard() != null
				&& !paramvo.getPk_assetcard().equals("")) {
			sql.append(" and card.pk_assetcard = ? ");
			sp.addParam(paramvo.getPk_assetcard());
		}
		sql.append(" order by card.assetcode ");
		List<WorkloadManagementVO> listVO = (List<WorkloadManagementVO>) singleObjectBO
				.executeQuery(sql.toString(), sp, new BeanListProcessor(
						WorkloadManagementVO.class));
		return listVO;
	}

	@Override
	public List<WorkloadManagementVO> queryBypk_assetcard(QueryParamVO paramvo)
			throws DZFWarpException {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ynt_workloadmanagement work ");
		sql.append(" where nvl(work.dr,0) = 0 and work.pk_assetcard = ? and work.doperatedate = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_assetcard());
		sp.addParam(paramvo.getBegindate1());
		List<WorkloadManagementVO> listVO = (List<WorkloadManagementVO>) singleObjectBO
				.executeQuery(sql.toString(), sp, new BeanListProcessor(
						WorkloadManagementVO.class));
		return listVO;
	}

	@Override
	public void save(WorkloadManagementVO vo) throws DZFWarpException {
		singleObjectBO.saveObject(vo.getPk_corp(), vo);

	}

}
