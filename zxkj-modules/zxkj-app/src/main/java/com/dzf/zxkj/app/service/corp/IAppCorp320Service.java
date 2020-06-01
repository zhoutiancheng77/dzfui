package com.dzf.zxkj.app.service.corp;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 获取公司信息
 * @author zhangj
 *
 */
public interface IAppCorp320Service {

	/**
	 * 根据公司主键获取公司对应的，省市区，信息
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public LoginResponseBeanVO getCorpMsg(UserBeanVO userBean) throws DZFWarpException;
	
}
