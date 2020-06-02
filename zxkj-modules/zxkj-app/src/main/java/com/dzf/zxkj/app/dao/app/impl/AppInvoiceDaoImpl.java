package com.dzf.zxkj.app.dao.app.impl;

import java.util.List;

import com.dzf.zxkj.app.dao.app.IAppInvoiceDao;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("invoice_dao")
public class AppInvoiceDaoImpl implements IAppInvoiceDao {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	/**
	 * 查询销项发票信息
	 * 
	 * @param pk_corp
	 */
	public List<VATSaleInvoiceVO> queryXXInvoice(String pk_corp, String period) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append(" from ynt_vatsaleinvoice  ");
		qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
		if (!StringUtil.isEmpty(period)) {
			qrysql.append(" and kprj like ? ");
		}
		qrysql.append(" order by kprj  desc");
		sp.addParam(pk_corp);
		if (!StringUtil.isEmpty(period)) {
			sp.addParam(period + "%");
		}
		List<VATSaleInvoiceVO> lists = (List<VATSaleInvoiceVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO.class));
		return lists;
	}
	
	/**
	 * 查询进项发票信息
	 * 
	 * @param pk_corp
	 */
	public List<VATInComInvoiceVO> queryJxInvoice(String pk_corp, String period) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append(" from ynt_vatincominvoice ");
		qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
		if (!StringUtil.isEmpty(period)) {
			qrysql.append(" and ((inperiod is not null and  inperiod like ? ) ");
			qrysql.append("  or (inperiod is null and   kprj like ?))");
		}
		qrysql.append(" order by inperiod  desc");
		sp.addParam(pk_corp);
		if (!StringUtil.isEmpty(period)) {
			sp.addParam(period + "%");
			sp.addParam(period + "%");
		}
		List<VATInComInvoiceVO> lists = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));

		return lists;
	}


}
