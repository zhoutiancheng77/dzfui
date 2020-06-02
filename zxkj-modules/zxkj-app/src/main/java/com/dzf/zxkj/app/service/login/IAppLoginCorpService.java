package com.dzf.zxkj.app.service.login;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 用户管理320版本的接口放置
 * @author zhangj
 *
 */
public interface IAppLoginCorpService {
	
	
	/**
	 * 登录
	 * @param userBean
	 * @param logbean
	 * @return
	 */
	public LoginResponseBeanVO loginFromTel(UserBeanVO userBean, LoginResponseBeanVO logbean) throws DZFWarpException;// 通过手机号+验证码登录
	
	
	/**
	 * 自动登录
	 * @param ubean
	 * @param account_id
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getAutoLogin(UserBeanVO ubean , String account_id, String pk_corp , String pk_temp_corp) throws DZFWarpException;
	
	
	
	/**
	 * 赋值父级数据
	 * @param pk_corp
	 * @param bean
	 * @param soucesys
	 * @throws DZFWarpException
	 */
	public void putFatherCorp(String pk_corp, LoginResponseBeanVO bean,String soucesys) throws DZFWarpException;
	
}
