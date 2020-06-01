package com.dzf.zxkj.app.service.user;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IAppUserService {

	public RegisterRespBeanVO saveregister(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 查询公司信息
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qryCorpService(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 业务合作+意见反馈
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO saveprocesscollabtevalt(UserBeanVO userBean) throws DZFWarpException;

	public ResponseBaseBeanVO qrySelfAndChdCorps(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 查询公司列表
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qryCorpList(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 查询是否有相应的权限
	 * 
	 * @param power
	 *            0 是否有报表的权限，1是否有上传票据， 2 全部的权限 3 开票申请权限 null的话不判断
	 * @param account
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean getPrivilege(String power, String account, String pk_account_id, String pk_corp, String pk_tempcorp)
			throws DZFWarpException;

	/**
	 * 个人信息维护
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO updatecompletInfo(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 检查公司是否存在
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO CheckExistCorp(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 环信(停止使用)
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO fpHxkfAccount(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 注册用户
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public RegisterRespBeanVO saveRegisterUser(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 保存用户信息
	 * 
	 * @param userBean
	 * @param pk_tempcorp
	 * @param pk_corp
	 * @param ismanage
	 * @param ope
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserVO> saveUser(UserBeanVO userBean, String pk_tempcorp, String pk_corp, DZFBoolean ismanage,
								 Integer ope) throws DZFWarpException;

	/**
	 * 更改帐号信息
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public RegisterRespBeanVO updateUserMsg(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 设置密码
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO savePassword(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 找回密码
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public void updateBackPassword(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 获取登录用户信息
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO qryLgBeanvo(UserBeanVO userBean) throws DZFWarpException;
	
	/**
	 * 获取登录用户信息(微信小程序)
	 * 
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO qryLgBeanvoFromWxApplet(UserBeanVO userBean) throws DZFWarpException;
	
	/**
	 * 修改密码
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public RegisterRespBeanVO updateModifyPasswordSWtch320(UserBeanVO userBean)  throws DZFWarpException;

}
