package com.dzf.zxkj.app.service.user;

import java.util.List;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;

public interface IManagingUsers {

	/**
	 * 查询用户信息
	 * @param userBean
	 * @param isself
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AppUserVO> getUserlist(UserBeanVO userBean, DZFBoolean isself) throws DZFWarpException;

//	public ResponseBaseBeanVO qryUserLs(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO saveapprove(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO adjustPrivilege(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO updateassignPrivilege(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO qryPrivilege(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO updatestop(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO updaterenew(UserBeanVO userBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO delete(UserBeanVO userBean) throws DZFWarpException;
//
//	/**
//	 * 查询环信信息
//	 *
//	 * @param userbean
//	 * @return
//	 * @throws BusinessException
//	 */
//	public ResponseBaseBeanVO qryhxUser(UserBeanVO userbean) throws DZFWarpException;
//
//	/**
//	 * 不包含自己的id
//	 *
//	 * @param userBean
//	 * @return
//	 * @throws BusinessException
//	 */
//	public ResponseBaseBeanVO qryUserLsFromCon(UserBeanVO userBean) throws DZFWarpException;
//
//	/**
//	 * 查询待审核的用户
//	 *
//	 * @param userBean
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public ResponseBaseBeanVO qryAuditUserLs(UserBeanVO userBean) throws DZFWarpException;
//
//

}
