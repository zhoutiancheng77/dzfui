package com.dzf.zxkj.app.service.bill;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;

import java.util.List;

/**
 * 发票信息 
 * @author zhangj
 *
 */
public interface IAppInvoiceService {

	/**
	 * 获取销项的发票张数和合计数
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] queryXXTotal(String pk_corp, String period) throws DZFWarpException;

	/**
	 * 获取进项的发票张数和合计数
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] queryJxTotal(String pk_corp, String period) throws DZFWarpException;
		/**
	 * 查询销项发票
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATSaleInvoiceVO> queryXXInvoice(String pk_corp, String period) throws DZFWarpException;

	/**
	 * 查询进项发票
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATInComInvoiceVO> queryJxInvoice(String pk_corp, String period) throws DZFWarpException;


//	/**
//	 * 查询发票信息
//	 * @param pk_corp
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<InvoiceVo> qryInvoice(String pk_corp) throws DZFWarpException;
//

//	/**
//	 * 销项发票详情，包含子表
//	 * @param pk_corp
//	 * @param id
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public VATSaleInvoiceVO  queryXXInvoiceDetail(String pk_corp, String id) throws DZFWarpException;
//

//	/**
//	 * 查询进项发票详情
//	 * @param pk_corp
//	 * @param id
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public VATInComInvoiceVO queryJxInvoiceDetail(String pk_corp, String id) throws DZFWarpException;
//
//
}
