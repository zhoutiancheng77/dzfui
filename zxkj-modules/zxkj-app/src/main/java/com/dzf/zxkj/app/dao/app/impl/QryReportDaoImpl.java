package com.dzf.zxkj.app.dao.app.impl;

import java.util.ArrayList;
import java.util.List;

import com.dzf.zxkj.app.dao.app.IQryReportDao;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orgreport_dao")
public class QryReportDaoImpl implements IQryReportDao {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<BsWorkbenchVO> queryNsgzt(String pk_corp, String period) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司信息为空");
		}

		if (StringUtil.isEmpty(period)) {
			throw new BusinessException("查询期间为空");
		}

		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from  ");
		qrysql.append("  nsworkbench ");
		qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? and period = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);

		List<BsWorkbenchVO> reslit = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(BsWorkbenchVO.class));
		return reslit;
	}
	
	/**
	 * 汇总月上传图片数量
	 * 
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int qryMonthPics(String pk_corp, DZFDate startdate, DZFDate enddate) throws DZFWarpException {
		int monthPics = 0;
		String picSql = "select count(pk_image_library) dr from ynt_image_group gr"
				+ " inner join ynt_image_library lb on gr.pk_image_group=lb.pk_image_group"
				+ " where gr.pk_corp = ? AND gr.doperatedate between ? AND ?" + " and (nvl(gr.dr, 0) = 0) ";
		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(startdate);
		sp.addParam(enddate);
		ArrayList al = (ArrayList) sbo.executeQuery(picSql, sp, new ArrayListProcessor());
		if (al != null && al.size() > 0) {
			monthPics = Integer.parseInt(((Object[]) al.get(0))[0].toString());
		}
		return monthPics;
	}
	
	/**
	 * 汇总月凭证数量
	 * 
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int qryMonthVouchers(String pk_corp, DZFDate startdate, DZFDate enddate) throws DZFWarpException {
		int vouchs = 0;
		String vouSql = "select count(h.pk_tzpz_h) dr from ynt_tzpz_h h"
				+ " where h.pk_corp = ? AND h.doperatedate between ? AND ? and nvl(h.dr,0)=0";
		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(startdate);
		sp.addParam(enddate);
		ArrayList al = (ArrayList) sbo.executeQuery(vouSql, sp, new ArrayListProcessor());
		if (al != null && al.size() > 0) {
			vouchs = Integer.parseInt(((Object[]) al.get(0))[0].toString());
		}
		return vouchs;
	}

}
