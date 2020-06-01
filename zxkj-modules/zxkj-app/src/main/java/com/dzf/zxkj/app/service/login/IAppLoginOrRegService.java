package com.dzf.zxkj.app.service.login;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.AccountVO;

/**
 * 用户管理260版本的接口放置
 * @author zhangj
 *
 */
public interface IAppLoginOrRegService {

	
	/**
	 * 用户注册
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO saveRegisterCorpSWtch260(UserBeanVO userBean) throws DZFWarpException;//300版本以后的公司注册
	
	/**
	 * 注册自动关联公司
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO saveRegisterAndLinkCorp(UserBeanVO userBean, DZFBoolean blogin) throws DZFWarpException;//注册同时自动关联公司
	

	/**
	 * 用户获取公司
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO logingGetCorpVOs260(UserBeanVO userBean) throws DZFWarpException;//
	

}
