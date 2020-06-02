package com.dzf.zxkj.app.service.app.act;


import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.BusinessResonseBeanVO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 审批流接口
 * @author zhangj
 *
 */
public interface IAppApproveService {



	/**
	 * 审批
	 * @param setvo
	 * @return
	 * @throws DZFWarpException
	 */
	public BusinessResonseBeanVO updateApprove(BusiReqBeanVo ubean, SingleObjectBO sbo) throws DZFWarpException;
	
//	/**
//	 * 审批流设置保存
//	 * @param setvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BusinessResonseBeanVO saveApproveSet(ApproveSetVo setvo) throws DZFWarpException;
//
//
//	/**
//	 * 获取审批设置vo
//	 * @param ubean
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BusinessResonseBeanVO queryApprovSet(BusiReqBeanVo ubean)  throws DZFWarpException;
//
//
//
//	/**
//	 * 驳回
//	 * @param setvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BusinessResonseBeanVO updateReject(BusiReqBeanVo ubean) throws DZFWarpException;
//
//
//
//
//	/**
//	 * 是否启用审批流
//	 * @param pk_corp
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public boolean bOpenApprove(String pk_corp) throws DZFWarpException;
	
}
