package com.dzf.zxkj.app.dao.app;

import java.util.List;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;

public interface IAppInvoiceDao {
	/**
	 * 查询销项发票信息
	 * 
	 * @param pk_corp
	 */
	public List<VATSaleInvoiceVO> queryXXInvoice(String pk_corp, String period) throws DZFWarpException;
	
	
	
	/**
	 * 查询进项数据
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATInComInvoiceVO> queryJxInvoice(String pk_corp, String period) throws DZFWarpException;
	
}
