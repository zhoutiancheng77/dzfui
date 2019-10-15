package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.report.service.cwzb.IKmReportPub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报表查询公共实现
 * @author zhangj
 *
 */
@Service("gl_rep_pub")
public class KmReportPubImpl implements IKmReportPub {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;

	//公司+科目+辅助类别+辅助项目
	@Override
	public KmMxZVO[] getKmfsConFZ(KmReoprtQueryParamVO pamvo) throws DZFWarpException {
		if(StringUtil.isEmpty(pamvo.getFzlb())){
			throw new BusinessException("辅助类别不能为空!");
		}
		
		SQLParameter parameter =  new SQLParameter();
		String qrysql = getQuerySqlByPeriod(pamvo.getBegindate1(), pamvo.getEnddate(),
				pamvo.getKms_id(), pamvo.getPk_corp(),pamvo.getFzlb(),pamvo.getFzxm(),parameter);
		
		List<KmMxZVO> reslist =  (List<KmMxZVO>) singleObjectBO.executeQuery(qrysql, parameter, new BeanListProcessor(KmMxZVO.class));
		
		return reslist.toArray(new KmMxZVO[0]);
	}
	
	protected String getQuerySqlByPeriod(DZFDate start, DZFDate end,
										 String pk_accsubj, String pk_corp, String fzlb, String fzxm,
										 SQLParameter parameter) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select h.period as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,a.pk_corp_account as km ,b.zy ,");
		sb.append("        b.pk_currency as bz ,b.jfmny as jf ,b.dfmny as df,a.direction as fx, b.pk_tzpz_h ,b.pk_tzpz_b ,");
		sb.append("        b.fzhsx"+fzlb );
		sb.append(" from ynt_tzpz_b b  ");
		sb.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h  ");
		sb.append(" inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account ");
		sb.append(" where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
		DZFDate d1 = DateUtils.getPeriodStartDate(DateUtils.getPeriod(start));
		sb.append(" and h.doperatedate>='").append(d1);
		d1 = DateUtils.getPeriodEndDate(DateUtils.getPeriod(end));
		sb.append("' and h.doperatedate<='").append(d1).append("'");
		parameter.addParam(pk_corp);
		sb.append(" and h.pk_corp= ?");
		if(!StringUtil.isEmpty(fzxm)){
			sb.append(" and b.fzhsx"+fzlb +"= ?" );
			parameter.addParam(fzxm);
		}
		sb.append(" order by h.doperatedate, h.pzh asc ,b.rowno   ");
		return sb.toString();
	}

	@Override
	public DZFDouble getKmQcValue(String pk_accsubj, DZFDate begindate, String pk_corp) throws DZFWarpException {
		return null;
	}
	

}
